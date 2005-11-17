package hu.ismicro.commons.proximity.webapp;

import hu.ismicro.commons.proximity.Proximity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.support.MutableSortDefinition;
import org.springframework.beans.support.PropertyComparator;
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
        logger.debug("Got request for index");
        List repositories = proximity.getRepositories();
        PropertyComparator.sort(repositories, new MutableSortDefinition("name", true, true));
        Map result = new HashMap();
        result.put("repositories", repositories);
        return new ModelAndView("search", result);
    }

    public ModelAndView stats(HttpServletRequest request, HttpServletResponse response) throws Exception {
        logger.debug("Got request for stats");
        return new ModelAndView("stats");
    }

}
