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
        if (RequestUtils.getStringParameter(request, "searchAll") != null
                && RequestUtils.getRequiredStringParameter(request, "searchAllRegexp") != null) {
            example = new ProxiedItemProperties();
            example.setName(RequestUtils.getRequiredStringParameter(request, "searchAllRegexp") + "*");
        } else if (RequestUtils.getStringParameter(request, "searchSelected") != null
                && RequestUtils.getRequiredStringParameter(request, "searchSelectedRepos") != null
                && RequestUtils.getRequiredStringParameter(request, "searchSelectedRegexp") != null) {
            example = new ProxiedItemProperties();
            example.setName(RequestUtils.getRequiredStringParameter(request, "searchSelectedRegexp") + "*");
            example.setMetadata(ProxiedItemProperties.METADATA_OWNING_REPOSITORY, RequestUtils.getRequiredStringParameter(request, "searchSelectedRepos"));
        } else if (RequestUtils.getStringParameter(request, "reindexAll") != null) {
            proximity.reindex();
        } else if (RequestUtils.getStringParameter(request, "reindexSelected") != null
                && RequestUtils.getRequiredStringParameter(request, "reindexSelectedRepos") != null) {
            proximity.reindex(RequestUtils.getRequiredStringParameter(request, "reindexSelectedRepos"));
        }

        Map context = new HashMap();

        if (example != null) {
            List results = proximity.searchItem(example);
            context.put("results", results);
        }
        List repositories = getProximity().getRepositories();
        context.put("repositories", repositories);
        return new ModelAndView("search", context);
    }

    public ModelAndView stats(HttpServletRequest request, HttpServletResponse response) throws Exception {
        logger.debug("Got request for stats");
        Map stats = proximity.getStatistics();
        return new ModelAndView("stats", "stats", stats);
    }

    public ModelAndView repositories(HttpServletRequest request, HttpServletResponse response) throws Exception {
        logger.debug("Got request for repositories layout");
        List repositories = proximity.getRepositories();
        return new ModelAndView("repositories", "repositories", repositories);
    }

}
