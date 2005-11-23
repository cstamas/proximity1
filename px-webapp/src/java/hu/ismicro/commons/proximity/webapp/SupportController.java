package hu.ismicro.commons.proximity.webapp;

import hu.ismicro.commons.proximity.Proximity;
import hu.ismicro.commons.proximity.ProximitySupport;

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

    private ProximitySupport proximitySupport;

    public void setProximity(Proximity proximity) {
        this.proximity = proximity;
    }

    public Proximity getProximity() {
        return proximity;
    }

    public void setProximitySupport(ProximitySupport proximitySupport) {
        this.proximitySupport = proximitySupport;
    }

    public ProximitySupport getProximitySupport() {
        return proximitySupport;
    }

    public ModelAndView index(HttpServletRequest request, HttpServletResponse response) throws Exception {
        logger.debug("Got request for index");
        return new ModelAndView("index");
    }

    public ModelAndView search(HttpServletRequest request, HttpServletResponse response) throws Exception {
        logger.debug("Got request for search");
        if (RequestUtils.getStringParameter(request, "searchAll") != null
                && RequestUtils.getRequiredStringParameter(request, "searchAllRegexp") != null) {
        } else if (RequestUtils.getStringParameter(request, "searchSelected") != null
                && RequestUtils.getRequiredStringParameter(request, "searchSelectedRepos") != null
                && RequestUtils.getRequiredStringParameter(request, "searchSelectedRegexp") != null) {
        }
        List repositories = getProximity().getRepositories();
        Map context = new HashMap();
        context.put("repositories", repositories);
        // results
        return new ModelAndView("search", context);
    }

    public ModelAndView stats(HttpServletRequest request, HttpServletResponse response) throws Exception {
        logger.debug("Got request for stats");
        return new ModelAndView("stats");
    }

}
