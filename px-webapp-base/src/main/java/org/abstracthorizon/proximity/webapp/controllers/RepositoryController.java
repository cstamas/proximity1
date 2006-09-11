package org.abstracthorizon.proximity.webapp.controllers;


import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.abstracthorizon.proximity.AccessDeniedException;
import org.abstracthorizon.proximity.Item;
import org.abstracthorizon.proximity.ItemNotFoundException;
import org.abstracthorizon.proximity.Proximity;
import org.abstracthorizon.proximity.ProximityRequest;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.support.MutableSortDefinition;
import org.springframework.beans.support.PropertyComparator;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;

public class RepositoryController extends MultiActionController {

    private Proximity proximity;

    public void setProximity(Proximity proximity) {
        this.proximity = proximity;
    }

    public Proximity getProximity() {
        return proximity;
    }

    public ModelAndView repositoryList(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String requestURI = request.getRequestURI().substring(
                request.getContextPath().length() + request.getServletPath().length());
        if (requestURI.length() == 0) {
            requestURI = "/";
        }
        logger.debug("Got repository request on URI " + requestURI);
        String orderBy = request.getParameter("orderBy") == null ? "name" : request.getParameter("orderBy");
        String targetRepository = request.getParameter("repositoryId");
        String targetGroup = request.getParameter("repositoryGroupId");

        Item item = null;
        ProximityRequest pRequest = new ProximityRequest();
        pRequest.setPath(requestURI);
        pRequest.setTargetedReposId(targetRepository);
        pRequest.setTargetedReposGroupId(targetGroup);
        pRequest.setGrantee(null);
        pRequest.getAttributes().put(ProximityRequest.REQUEST_REMOTE_ADDRESS, request.getRemoteAddr());
        
        // issue #42, collect header information
        Enumeration headerNames = request.getHeaderNames();
        while(headerNames.hasMoreElements()) {
            String headerName = (String) headerNames.nextElement();
            pRequest.getAttributes().put("http." + headerName.toLowerCase(), request.getHeader(headerName));
        }
        
        try {
            logger.debug("Got request for " + targetRepository + " repository on URI: " + requestURI);
            item = proximity.retrieveItem(pRequest);
            logger.debug("Got response " + item.getProperties().getPath());

            if (item.getProperties().isDirectory()) {
                List items = null;
                items = proximity.listItems(pRequest);
                PropertyComparator.sort(items, new MutableSortDefinition(orderBy, true, true));
                Map result = new HashMap();
                result.put("items", items);
                result.put("orderBy", orderBy);
                result.put("requestUri", requestURI);
                result.put("requestPathList", explodeUriToList(requestURI));
                return new ModelAndView("repository/repositoryList", result);
            } else {
                // TODO: check for If-Modified-Since?
                response.setContentType("application/octet-stream");
                response.setContentLength((int) item.getProperties().getSize());
                response.setDateHeader("Last-Modified", item.getProperties().getLastModified().getTime());
                InputStream is = item.getStream();
                OutputStream os = response.getOutputStream();
                IOUtils.copy(is, os);
                is.close();
                return null;
            }
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

}
