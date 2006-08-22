package org.abstracthorizon.proximity.webapp.util;

import hu.ismicro.commons.proximity.AccessDeniedException;
import hu.ismicro.commons.proximity.ItemNotFoundException;
import hu.ismicro.commons.proximity.Proximity;
import hu.ismicro.commons.proximity.impl.ItemPropertiesImpl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.support.MutableSortDefinition;
import org.springframework.beans.support.PropertyComparator;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;

public class ArtifactsController extends MultiActionController {

    private Proximity proximity;

    public void setProximity(Proximity proximity) {
        this.proximity = proximity;
    }

    public Proximity getProximity() {
        return proximity;
    }

    public ModelAndView artifactsList(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String requestURI = request.getRequestURI().substring(
                request.getContextPath().length() + request.getServletPath().length());
        if (requestURI.length() == 0) {
            requestURI = "/";
        }
        logger.debug("Got artifact request on URI " + requestURI);
        String orderBy = request.getParameter("orderBy") == null ? "name" : request.getParameter("orderBy");
        String targetRepository = request.getParameter("repositoryId");
        String targetGroup = request.getParameter("repositoryGroupId");
        List requestPathList = explodeUriToList(requestURI);

        String gid = null;
        String aid = null;
        String version = null;

        if (requestPathList.size() > 0) {
            gid = (String) requestPathList.get(0);
        }
        if (requestPathList.size() > 1) {
            aid = (String) requestPathList.get(1);
        }
        if (requestPathList.size() > 2) {
            version = (String) requestPathList.get(2);
        }

        // this view relies on search only, thats the trick:
        // level 1: search for "kind:pom", extract the "pom.gid" properties,
        // make them unique, show
        // level 2: search for "kind:pom AND pom.gid:SELECTION", extract the
        // "pom.aid" properties, make them unqie, show
        // level 3: search for "kind:pom AND pom.gid:SELECTION AND
        // pom.aid:SELECTION", extract the "pom.version" ...
        // level 4: search for "kind:pom AND pom.gid:SELECTION AND
        // pom.aid:SELECTION AND pom.version:SELECTION" and offer pom.pck
        // download?

        // URI: /pom.gid/pom.aid/pom.version

        try {
            logger.debug("Got request for artifactList on URI=" + requestURI);

            String searchExpr = "m2kind:pom";
            if (targetRepository != null) {
                searchExpr += " AND repository.id:" + targetRepository;
            }
            if (targetGroup != null) {
                searchExpr += " AND repository.groupId:" + targetGroup;
            }
            if (gid != null) {
                searchExpr += " AND pom.gid:" + gid;
            }
            if (aid != null) {
                searchExpr += " AND pom.aid:" + aid;
            }
            if (version != null) {
                searchExpr += " AND pom.version:" + version;
            }

            // make list unique and ordered on smthn
            List artifactList = null;
            if (version != null) {
                artifactList = sortAndMakeUnique(proximity.searchItem(searchExpr), "pom.version");
            } else if (aid != null) {
                artifactList = sortAndMakeUnique(proximity.searchItem(searchExpr), "pom.version");
            } else if (gid != null) {
                artifactList = sortAndMakeUnique(proximity.searchItem(searchExpr), "pom.aid");
            } else {
                artifactList = sortAndMakeUnique(proximity.searchItem(searchExpr), "pom.gid");
            }

            Map result = new HashMap();
            result.put("gid", gid);
            result.put("aid", aid);
            result.put("version", version);
            result.put("items", artifactList);
            result.put("orderBy", orderBy);
            result.put("requestUri", requestURI);
            result.put("requestPathList", requestPathList);

            return new ModelAndView("repository/artifactList", result);

        } catch (ItemNotFoundException ex) {
            logger.info("Item not found on URI " + requestURI);
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return null;
        } catch (AccessDeniedException ex) {
            logger.info("Access forbidden to " + requestURI + " for " + request.getRemoteAddr(), ex);
            response.sendError(HttpServletResponse.SC_FORBIDDEN);
            return null;
        }
    }

    protected List explodeUriToList(String uri) {
        List result = new ArrayList();
        String[] explodedUri = uri.split("/");
        // StringBuffer sb = new StringBuffer("/");
        for (int i = 0; i < explodedUri.length; i++) {
            if (explodedUri[i].length() > 0) {
                result.add(explodedUri[i]);
            }
        }
        return result;
    }

    protected List sortAndMakeUnique(List artifactList, String metadataKey) {
        // we do a little trick here, moving the needed key to name field
        List result = new ArrayList(artifactList.size());
        List uniqueItemList = new ArrayList(artifactList.size());
        for (Iterator i = artifactList.iterator(); i.hasNext();) {
            ItemPropertiesImpl item = (ItemPropertiesImpl) i.next();
            if (!uniqueItemList.contains(item.getMetadata(metadataKey))) {
                uniqueItemList.add(item.getMetadata(metadataKey));
                item.setName(item.getMetadata(metadataKey));
                result.add(item);
            }
        }
        PropertyComparator.sort(result, new MutableSortDefinition("name", true, true));
        return result;
    }
}
