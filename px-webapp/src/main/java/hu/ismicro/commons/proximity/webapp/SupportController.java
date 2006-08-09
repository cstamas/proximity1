package hu.ismicro.commons.proximity.webapp;

import hu.ismicro.commons.proximity.Proximity;
import hu.ismicro.commons.proximity.base.ProxiedItemProperties;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.bind.RequestUtils;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;

public class SupportController extends MultiActionController {

    private Proximity proximity;

    public void setProximity(Proximity proximity) {
        this.proximity = proximity;
    }

    public Proximity getProximity() {
        return proximity;
    }

    public ModelAndView index(HttpServletRequest request, HttpServletResponse response) throws Exception {
        logger.debug("Got request for index");
        return new ModelAndView("index");
    }

    public ModelAndView search(HttpServletRequest request, HttpServletResponse response) throws Exception {
        logger.debug("Got request for search");
        ProxiedItemProperties example = null;
        String query = null;

        if (RequestUtils.getStringParameter(request, "searchAllRegexp") != null) {
            
            example = new ProxiedItemProperties();
            example.setName(RequestUtils.getRequiredStringParameter(request, "searchAllRegexp") + "*");

        } else if (RequestUtils.getStringParameter(request, "searchRepositoryRegexp") != null
                && RequestUtils.getRequiredStringParameter(request, "searchRepositoryId") != null) {

            example = new ProxiedItemProperties();
            example.setName(RequestUtils.getRequiredStringParameter(request, "searchRepositoryRegexp") + "*");
            example.setMetadata(ProxiedItemProperties.METADATA_OWNING_REPOSITORY, RequestUtils
                    .getRequiredStringParameter(request, "searchRepositoryId"));

        } else if (RequestUtils.getStringParameter(request, "searchGroupRegexp") != null
                && RequestUtils.getRequiredStringParameter(request, "searchGroupId") != null) {

            example = new ProxiedItemProperties();
            example.setName(RequestUtils.getRequiredStringParameter(request, "searchGroupRegexp") + "*");
            example.setMetadata(ProxiedItemProperties.METADATA_OWNING_REPOSITORY_GROUP, RequestUtils
                    .getRequiredStringParameter(request, "searchGroupId"));

        } else if (RequestUtils.getStringParameter(request, "searchLQL") != null
                && RequestUtils.getRequiredStringParameter(request, "searchLQLQuery") != null) {

            query = RequestUtils.getRequiredStringParameter(request, "searchLQLQuery");

        }

        logger.debug("example=" + (example == null ? null : example.getName()) + ", query=" + query);

        Map context = new HashMap();

        if (example != null) {
            List results = getProximity().searchItem(example);
            context.put("results", results);
        } else if (query != null) {
            List results = getProximity().searchItem(query);
            context.put("results", results);
        }
        context.put("searchableKeywords", getProximity().getSearchableKeywords());
        context.put("repositories", getProximity().getRepositories());
        context.put("groups", getProximity().getRepositoryGroupIds());
        return new ModelAndView("search", context);
    }

    public ModelAndView maintenance(HttpServletRequest request, HttpServletResponse response) throws Exception {
        logger.debug("Got request for maintenance");
        if (RequestUtils.getStringParameter(request, "reindexAll") != null) {
            proximity.reindex();
        } else if (RequestUtils.getStringParameter(request, "reindexSelected") != null
                && RequestUtils.getRequiredStringParameter(request, "reindexSelectedRepos") != null) {
            proximity.reindex(RequestUtils.getRequiredStringParameter(request, "reindexSelectedRepos"));
        }

        Map context = new HashMap();
        List repositories = getProximity().getRepositories();
        context.put("repositories", repositories);
        return new ModelAndView("maintenance", context);
    }

    public ModelAndView stats(HttpServletRequest request, HttpServletResponse response) throws Exception {
        logger.debug("Got request for stats");
        Map stats = proximity.getStatistics();
        Map context = new HashMap();
        context.put("stats", stats);
        return new ModelAndView("stats", context);
    }

    public ModelAndView repositories(HttpServletRequest request, HttpServletResponse response) throws Exception {
        logger.debug("Got request for repositories layout");
        Map context = new HashMap();
        context.put("emergeRepositoryGroups", Boolean.valueOf(proximity.isEmergeRepositoryGroups()));
        context.put("repositories", proximity.getRepositories());
        context.put("repositoryGroups", proximity.getRepositoryGroups());
        return new ModelAndView("repositories", "context", context);
    }

}
