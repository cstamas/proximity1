package org.abstracthorizon.proximity.webapp.controllers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.abstracthorizon.proximity.HashMapItemPropertiesImpl;
import org.abstracthorizon.proximity.ItemProperties;
import org.abstracthorizon.proximity.Proximity;
import org.abstracthorizon.proximity.indexer.Indexer;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;

public class SupportController extends MultiActionController {

    private Proximity proximity;

    private Indexer indexer;

    public void setProximity(Proximity proximity) {
        this.proximity = proximity;
    }

    public Proximity getProximity() {
        return proximity;
    }

    public Indexer getIndexer() {
        return indexer;
    }

    public void setIndexer(Indexer indexer) {
        this.indexer = indexer;
    }

    public ModelAndView index(HttpServletRequest request, HttpServletResponse response) throws Exception {
        logger.debug("Got request for index");
        return new ModelAndView("index");
    }

    public ModelAndView search(HttpServletRequest request, HttpServletResponse response) throws Exception {
        logger.debug("Got request for search");
        ItemProperties example = null;
        String query = null;

        if (ServletRequestUtils.getStringParameter(request, "searchAllRegexp") != null) {

            example = new HashMapItemPropertiesImpl();
            example.setName(ServletRequestUtils.getRequiredStringParameter(request, "searchAllRegexp") + "*");

        } else if (ServletRequestUtils.getStringParameter(request, "searchRepositoryRegexp") != null
                && ServletRequestUtils.getRequiredStringParameter(request, "searchRepositoryId") != null) {

            example = new HashMapItemPropertiesImpl();
            example.setName(ServletRequestUtils.getRequiredStringParameter(request, "searchRepositoryRegexp") + "*");
            example.setRepositoryId(ServletRequestUtils
                    .getRequiredStringParameter(request, "searchRepositoryId"));

        } else if (ServletRequestUtils.getStringParameter(request, "searchGroupRegexp") != null
                && ServletRequestUtils.getRequiredStringParameter(request, "searchGroupId") != null) {

            example = new HashMapItemPropertiesImpl();
            example.setName(ServletRequestUtils.getRequiredStringParameter(request, "searchGroupRegexp") + "*");
            example.setRepositoryGroupId(ServletRequestUtils
                    .getRequiredStringParameter(request, "searchGroupId"));

        } else if (ServletRequestUtils.getStringParameter(request, "searchLQL") != null
                && ServletRequestUtils.getRequiredStringParameter(request, "searchLQLQuery") != null) {

            query = ServletRequestUtils.getRequiredStringParameter(request, "searchLQLQuery");

        }

        logger.debug("example=" + (example == null ? null : example.getName()) + ", query=" + query);

        Map context = new HashMap();

        if (example != null) {
            List results = getIndexer().searchByItemPropertiesExample(example);
            context.put("results", results);
        } else if (query != null) {
            List results = getIndexer().searchByQuery(query);
            context.put("results", results);
        }
        context.put("searchableKeywords", getIndexer().getSearchableKeywords());
        context.put("repositories", getProximity().getRepositoryIds());
        context.put("groups", getProximity().getRepositoryGroupIds());
        return new ModelAndView("search", context);
    }

    public ModelAndView maintenance(HttpServletRequest request, HttpServletResponse response) throws Exception {
        logger.debug("Got request for maintenance");
        if (ServletRequestUtils.getStringParameter(request, "reindexAll") != null) {
            proximity.reindex();
        } else if (ServletRequestUtils.getStringParameter(request, "reindexSelected") != null
                && ServletRequestUtils.getRequiredStringParameter(request, "reindexSelectedRepos") != null) {
            proximity.reindex(ServletRequestUtils.getRequiredStringParameter(request, "reindexSelectedRepos"));
        }

        Map context = new HashMap();
        List repositories = getProximity().getRepositories();
        context.put("repositories", repositories);
        return new ModelAndView("maintenance", context);
    }

    public ModelAndView stats(HttpServletRequest request, HttpServletResponse response) throws Exception {
        logger.debug("Got request for stats");
        // TODO: refactor this
        Map stats = new HashMap(); // proximity.getStatistics();
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
