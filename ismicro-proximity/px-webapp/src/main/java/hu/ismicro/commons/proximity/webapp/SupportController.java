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
        ProxiedItemProperties example = new ProxiedItemProperties();
        if (RequestUtils.getStringParameter(request, "searchAll") != null
                && RequestUtils.getRequiredStringParameter(request, "searchAllRegexp") != null) {
            example.setName(RequestUtils.getRequiredStringParameter(request, "searchAllRegexp") + "*");
        } else if (RequestUtils.getStringParameter(request, "searchSelected") != null
                && RequestUtils.getRequiredStringParameter(request, "searchSelectedRepos") != null
                && RequestUtils.getRequiredStringParameter(request, "searchSelectedRegexp") != null) {
            example.setName(RequestUtils.getRequiredStringParameter(request, "searchSelectedRegexp") + "*");
            example.setMetadata(ProxiedItemProperties.METADATA_OWNING_REPOSITORY, RequestUtils.getRequiredStringParameter(request, "searchSelectedRepos"));
        }
        List results = proximity.searchItem(example);
        List repositories = getProximity().getRepositories();
        Map context = new HashMap();
        context.put("repositories", repositories);
        context.put("results", results);
        return new ModelAndView("search", context);
    }

    public ModelAndView stats(HttpServletRequest request, HttpServletResponse response) throws Exception {
        logger.debug("Got request for stats");
        Map stats = proximity.getStatistics();
        return new ModelAndView("stats", "stats", stats);
    }

}
