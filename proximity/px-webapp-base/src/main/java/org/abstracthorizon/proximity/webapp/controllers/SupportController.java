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
import org.abstracthorizon.proximity.stats.StatisticsGatherer;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;

public class SupportController extends MultiActionController {

	private Indexer indexer;

	private StatisticsGatherer statsGatherer;

	private Proximity proximity;

	public Indexer getIndexer() {
		return indexer;
	}

	public void setIndexer(Indexer indexer) {
		this.indexer = indexer;
	}

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
		ItemProperties example = null;

		String searchAllRegexp = null;
		
		String searchRepositoryRegexp = null;
		String searchRepositoryId = null;
		
		String searchGroupRegexp = null;
		String searchGroupId = null;

		String searchLQLQuery = null;

		if (ServletRequestUtils.getStringParameter(request, "searchAllRegexp") != null) {
			
			searchAllRegexp = ServletRequestUtils.getRequiredStringParameter(request, "searchAllRegexp"); 

			example = new HashMapItemPropertiesImpl();
			example.setName(searchAllRegexp);

		} else if (ServletRequestUtils.getStringParameter(request, "searchRepositoryRegexp") != null
				&& ServletRequestUtils.getRequiredStringParameter(request, "searchRepositoryId") != null) {

			searchRepositoryRegexp = ServletRequestUtils.getRequiredStringParameter(request, "searchRepositoryRegexp");
			searchRepositoryId = ServletRequestUtils.getRequiredStringParameter(request, "searchRepositoryId");
			
			example = new HashMapItemPropertiesImpl();
			example.setName(searchRepositoryRegexp);
			example.setRepositoryId(searchRepositoryId);

		} else if (ServletRequestUtils.getStringParameter(request, "searchGroupRegexp") != null
				&& ServletRequestUtils.getRequiredStringParameter(request, "searchGroupId") != null) {

			searchGroupRegexp = ServletRequestUtils.getRequiredStringParameter(request, "searchGroupRegexp");
			searchGroupId = ServletRequestUtils.getRequiredStringParameter(request, "searchGroupId");
			
			example = new HashMapItemPropertiesImpl();
			example.setName(searchGroupRegexp);
			example.setRepositoryGroupId(searchGroupId);

		} else if (ServletRequestUtils.getStringParameter(request, "searchLQL") != null
				&& ServletRequestUtils.getRequiredStringParameter(request, "searchLQLQuery") != null) {

			searchLQLQuery = ServletRequestUtils.getRequiredStringParameter(request, "searchLQLQuery");

		}

		logger.debug("example=" + (example == null ? null : example.getName()) + ", query=" + searchLQLQuery);

		Map context = new HashMap();

		if (example != null) {
			List results = getIndexer().searchByItemPropertiesExample(example);
			context.put("results", results);
		} else if (searchLQLQuery != null) {
			List results = getIndexer().searchByQuery(searchLQLQuery);
			context.put("results", results);
		}
		
		// for redisplay search input
		context.put("searchLQLQuery", searchLQLQuery);
		context.put("searchAllRegexp", searchAllRegexp);
		context.put("searchGroupRegexp", searchGroupRegexp);
		context.put("searchGroupId", searchGroupId);
		context.put("searchRepositoryRegexp", searchRepositoryRegexp);
		context.put("searchRepositoryId", searchRepositoryId);
		
		context.put("searchableKeywords", getIndexer().getSearchableKeywords());
		context.put("repositories", getProximity().getRepositoryIds());
		context.put("groups", getProximity().getRepositoryGroupIds());
		return new ModelAndView("search", context);
	}

	public ModelAndView maintenance(HttpServletRequest request, HttpServletResponse response) throws Exception {
		logger.debug("Got request for maintenance");
		if (ServletRequestUtils.getStringParameter(request, "reindexAll") != null) {
			getIndexer().reindex();
		} else if (ServletRequestUtils.getStringParameter(request, "reindexSelected") != null
				&& ServletRequestUtils.getRequiredStringParameter(request, "reindexSelectedRepos") != null) {
			getIndexer().reindex(ServletRequestUtils.getRequiredStringParameter(request, "reindexSelectedRepos"));
		}

		Map context = new HashMap();
		List repositories = getProximity().getRepositories();
		context.put("repositories", repositories);
		return new ModelAndView("maintenance", context);
	}

	public ModelAndView stats(HttpServletRequest request, HttpServletResponse response) throws Exception {
		logger.debug("Got request for stats");
		Map stats = null;
		if (statsGatherer != null) {
			stats = statsGatherer.getStatistics();
		}
		Map context = new HashMap();
		context.put("stats", stats);
		return new ModelAndView("stats", context);
	}

	public ModelAndView repositories(HttpServletRequest request, HttpServletResponse response) throws Exception {
		logger.debug("Got request for repositories layout");
		Map context = new HashMap();
		context.put("emergeRepositoryGroups", Boolean.valueOf(getProximity().isEmergeRepositoryGroups()));
		context.put("repositories", getProximity().getRepositories());
		context.put("repositoryGroups", getProximity().getRepositoryGroups());
		return new ModelAndView("repositories", "context", context);
	}

}
