/*

   Copyright 2005-2007 Tamas Cservenak (t.cservenak@gmail.com)

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.

 */
package org.abstracthorizon.proximity.webapp.controllers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.abstracthorizon.proximity.ItemProperties;
import org.abstracthorizon.proximity.Proximity;
import org.abstracthorizon.proximity.indexer.Indexer;
import org.springframework.beans.support.MutableSortDefinition;
import org.springframework.beans.support.PropertyComparator;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;

// TODO: Auto-generated Javadoc
/**
 * The Class ArtifactsController.
 */
public class ArtifactsController
    extends MultiActionController
{

    /** The proximity. */
    private Proximity proximity;

    /** The indexer. */
    private Indexer indexer;

    /**
     * Sets the proximity.
     * 
     * @param proximity the new proximity
     */
    public void setProximity( Proximity proximity )
    {
        this.proximity = proximity;
    }

    /**
     * Gets the proximity.
     * 
     * @return the proximity
     */
    public Proximity getProximity()
    {
        return proximity;
    }

    /**
     * Gets the indexer.
     * 
     * @return the indexer
     */
    public Indexer getIndexer()
    {
        return indexer;
    }

    /**
     * Sets the indexer.
     * 
     * @param indexer the new indexer
     */
    public void setIndexer( Indexer indexer )
    {
        this.indexer = indexer;
    }

    /**
     * Artifacts list.
     * 
     * @param request the request
     * @param response the response
     * 
     * @return the model and view
     * 
     * @throws Exception the exception
     */
    public ModelAndView artifactsList( HttpServletRequest request, HttpServletResponse response )
        throws Exception
    {
        String requestURI = request.getRequestURI().substring(
            request.getContextPath().length() + request.getServletPath().length() );
        if ( requestURI.length() == 0 )
        {
            requestURI = "/";
        }
        logger.debug( "Got artifact request on URI " + requestURI );
        String orderBy = request.getParameter( "orderBy" ) == null ? "name" : request.getParameter( "orderBy" );
        String targetRepository = request.getParameter( "repositoryId" );
        String targetGroup = request.getParameter( "repositoryGroupId" );
        List requestPathList = explodeUriToList( requestURI );

        String gid = null;
        String aid = null;
        String version = null;

        if ( requestPathList.size() > 0 )
        {
            gid = (String) requestPathList.get( 0 );
        }
        if ( requestPathList.size() > 1 )
        {
            aid = (String) requestPathList.get( 1 );
        }
        if ( requestPathList.size() > 2 )
        {
            version = (String) requestPathList.get( 2 );
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

        logger.debug( "Got request for artifactList on URI=" + requestURI );

        String searchExpr = "m2kind:pom";
        if ( targetRepository != null )
        {
            searchExpr += " AND repository.id:" + targetRepository;
        }
        if ( targetGroup != null )
        {
            searchExpr += " AND repository.groupId:" + targetGroup;
        }
        if ( gid != null )
        {
            searchExpr += " AND pom.gid:" + gid;
        }
        if ( aid != null )
        {
            searchExpr += " AND pom.aid:" + aid;
        }
        if ( version != null )
        {
            searchExpr += " AND pom.version:" + version;
        }

        // make list unique and ordered on smthn
        List artifactList = null;
        if ( version != null )
        {
            artifactList = sortAndMakeUnique( indexer.searchByQuery( searchExpr ), "pom.version" );
        }
        else if ( aid != null )
        {
            artifactList = sortAndMakeUnique( indexer.searchByQuery( searchExpr ), "pom.version" );
        }
        else if ( gid != null )
        {
            artifactList = sortAndMakeUnique( indexer.searchByQuery( searchExpr ), "pom.aid" );
        }
        else
        {
            artifactList = sortAndMakeUnique( indexer.searchByQuery( searchExpr ), "pom.gid" );
        }

        Map result = new HashMap();
        result.put( "gid", gid );
        result.put( "aid", aid );
        result.put( "version", version );
        result.put( "items", artifactList );
        result.put( "orderBy", orderBy );
        result.put( "requestUri", requestURI );
        result.put( "requestPathList", requestPathList );

        return new ModelAndView( "repository/artifactList", result );

    }

    /**
     * Explode uri to list.
     * 
     * @param uri the uri
     * 
     * @return the list
     */
    protected List explodeUriToList( String uri )
    {
        List result = new ArrayList();
        String[] explodedUri = uri.split( "/" );
        // StringBuffer sb = new StringBuffer("/");
        for ( int i = 0; i < explodedUri.length; i++ )
        {
            if ( explodedUri[i].length() > 0 )
            {
                result.add( explodedUri[i] );
            }
        }
        return result;
    }

    /**
     * Sort and make unique.
     * 
     * @param artifactList the artifact list
     * @param metadataKey the metadata key
     * 
     * @return the list
     */
    protected List sortAndMakeUnique( List artifactList, String metadataKey )
    {
        // we do a little trick here, moving the needed key to name field
        List result = new ArrayList( artifactList.size() );
        List uniqueItemList = new ArrayList( artifactList.size() );
        for ( Iterator i = artifactList.iterator(); i.hasNext(); )
        {
            ItemProperties item = (ItemProperties) i.next();
            if ( !uniqueItemList.contains( item.getMetadata( metadataKey ) ) )
            {
                uniqueItemList.add( item.getMetadata( metadataKey ) );
                item.setName( item.getMetadata( metadataKey ) );
                result.add( item );
            }
        }
        PropertyComparator.sort( result, new MutableSortDefinition( "name", true, true ) );
        return result;
    }
}
