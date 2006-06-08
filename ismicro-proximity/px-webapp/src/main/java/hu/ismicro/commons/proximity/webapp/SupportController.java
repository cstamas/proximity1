package hu.ismicro.commons.proximity.webapp;

import hu.ismicro.commons.proximity.ItemProperties;
import hu.ismicro.commons.proximity.Proximity;
import hu.ismicro.commons.proximity.base.ProxiedItemProperties;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
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
        } else if (RequestUtils.getStringParameter(request, "searchLQL") != null
                && RequestUtils.getRequiredStringParameter(request, "searchLQLQuery") != null) {
            query =  RequestUtils.getRequiredStringParameter(request, "searchLQLQuery");
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
        List keywords = getProximity().getSearchableKeywords();
        context.put("searchableKeywords", keywords);
        List repositories = getProximity().getRepositories();
        context.put("repositories", repositories);
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

        ArrayList topTenItems = new ArrayList(10);
        // dummy implementation
        int cnt = 1;
        for (Iterator i = stats.keySet().iterator(); i.hasNext() && cnt < 11; cnt++) {
            topTenItems.add((ItemProperties) i.next());
        }
        
        ArrayList latestTenItems = new ArrayList(10);
        // dummy implementation
        cnt = 1;
        for (Iterator i = stats.keySet().iterator(); i.hasNext() && cnt < 11; cnt++) {
            latestTenItems.add((ItemProperties) i.next());
        }

        ArrayList topTenIps = new ArrayList(10);
        cnt = 1;
        for (Iterator i = stats.keySet().iterator(); i.hasNext() && cnt < 11; cnt++) {
            topTenIps.add((ItemProperties) i.next());
        }
        
        ArrayList latestTenIps = new ArrayList(10);
        cnt = 1;
        for (Iterator i = stats.keySet().iterator(); i.hasNext() && cnt < 11; cnt++) {
            latestTenIps.add((ItemProperties) i.next());
        }
        
        Map context = new HashMap();
        context.put("topTenItems", topTenItems);
        context.put("latestTenItems", latestTenItems);
        context.put("topTenIps", topTenIps);
        context.put("latestTenIps", latestTenIps);
        context.put("stats", stats);
        return new ModelAndView("stats", context);
    }

    public ModelAndView repositories(HttpServletRequest request, HttpServletResponse response) throws Exception {
        logger.debug("Got request for repositories layout");
        List repositories = proximity.getRepositories();
        return new ModelAndView("repositories", "repositories", repositories);
    }

}
