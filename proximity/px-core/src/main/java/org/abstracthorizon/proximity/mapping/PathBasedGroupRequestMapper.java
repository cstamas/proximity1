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
package org.abstracthorizon.proximity.mapping;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.abstracthorizon.proximity.ProximityRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// TODO: Auto-generated Javadoc
/**
 * The Class PathBasedGroupRequestMapper.
 */
public class PathBasedGroupRequestMapper
    implements GroupRequestMapper
{

    /** The logger. */
    protected Logger logger = LoggerFactory.getLogger( this.getClass() );

    /** The compiled. */
    private boolean compiled = false;

    /** The group inclusions prepared. */
    private Map groupInclusionsPrepared = new HashMap();

    /** The group exclusions prepared. */
    private Map groupExclusionsPrepared = new HashMap();

    /** The group inclusions. */
    private Map groupInclusions = new HashMap();

    /** The group exclusions. */
    private Map groupExclusions = new HashMap();

    /*
     * (non-Javadoc)
     * 
     * @see org.abstracthorizon.proximity.mapping.GroupRequestMapper#getMappedRepositories(java.lang.String,
     *      org.abstracthorizon.proximity.ProximityRequest, java.util.List)
     */
    public List getMappedRepositories( String groupId, ProximityRequest request, List originalRepositoryGroupOrder )
    {
        if ( !compiled )
        {
            compile();
        }
        boolean mapped = false;
        List reposList = new ArrayList( originalRepositoryGroupOrder );
        // if include found, add it to the list.
        if ( groupInclusionsPrepared.containsKey( groupId ) )
        {
            Map inclusions = (Map) groupInclusionsPrepared.get( groupId );
            boolean firstAdd = true;
            for ( Iterator i = inclusions.keySet().iterator(); i.hasNext(); )
            {
                Pattern pattern = (Pattern) i.next();
                if ( pattern.matcher( request.getPath() ).matches() )
                {
                    if ( firstAdd )
                    {
                        reposList.clear();
                        firstAdd = false;
                    }
                    mapped = true;
                    reposList.addAll( (List) inclusions.get( pattern ) );
                }
            }
        }
        // then, if exlude found, remove it.
        if ( groupExclusionsPrepared.containsKey( groupId ) )
        {
            Map exclusions = (Map) groupExclusionsPrepared.get( groupId );
            for ( Iterator i = exclusions.keySet().iterator(); i.hasNext(); )
            {
                Pattern pattern = (Pattern) i.next();
                if ( pattern.matcher( request.getPath() ).matches() )
                {
                    mapped = true;
                    reposList.removeAll( (List) exclusions.get( pattern ) );
                }
            }
        }
        // at the end, if the list is empty, add all repos
        // if reposList is empty, return original list
        if ( !mapped )
        {
            logger.debug(
                "No mapping exists in group {} for request path, using all repository group members for request.",
                groupId );
        }
        else
        {
            logger.info(
                "Request path in group {} is mapped, using only {} group members for request.",
                groupId,
                reposList.toString() );
        }
        return reposList;
    }

    /**
     * Compile.
     */
    protected void compile()
    {
        HashMap res = new HashMap( groupInclusions.size() );
        for ( Iterator i = groupInclusions.keySet().iterator(); i.hasNext(); )
        {
            String groupId = (String) i.next();
            List incList = (List) groupInclusions.get( groupId );
            Map gRes = new HashMap( incList.size() );
            for ( Iterator j = incList.iterator(); j.hasNext(); )
            {
                String inc = (String) j.next();
                String regexp = inc.substring( 0, inc.indexOf( "=" ) );
                String reposes = inc.substring( inc.indexOf( "=" ) + 1, inc.length() );
                List reposList = Arrays.asList( reposes.split( "," ) );
                gRes.put( Pattern.compile( regexp ), reposList );
            }
            res.put( groupId, gRes );
        }
        this.groupInclusionsPrepared = res;

        res = new HashMap( groupExclusions.size() );
        for ( Iterator i = groupExclusions.keySet().iterator(); i.hasNext(); )
        {
            String groupId = (String) i.next();
            List excList = (List) groupExclusions.get( groupId );
            Map gRes = new HashMap( excList.size() );
            for ( Iterator j = excList.iterator(); j.hasNext(); )
            {
                String exc = (String) j.next();
                String regexp = exc.substring( 0, exc.indexOf( "=" ) );
                String reposes = exc.substring( exc.indexOf( "=" ) + 1, exc.length() );
                List reposList = Arrays.asList( reposes.split( "," ) );
                gRes.put( Pattern.compile( regexp ), reposList );
            }
            res.put( groupId, gRes );
        }
        this.groupExclusionsPrepared = res;
        this.compiled = true;
    }

    /**
     * Sets the inclusions.
     * 
     * @param inclusions the new inclusions
     */
    public void setInclusions( Map inclusions )
    {
        this.groupInclusions = inclusions;
        this.compiled = false;
    }

    /**
     * Sets the exclusions.
     * 
     * @param exclusions the new exclusions
     */
    public void setExclusions( Map exclusions )
    {
        this.groupExclusions = exclusions;
        this.compiled = false;
    }

    /**
     * Gets the inclusions.
     * 
     * @return the inclusions
     */
    public Map getInclusions()
    {
        return this.groupInclusions;
    }

    /**
     * Gets the exclusions.
     * 
     * @return the exclusions
     */
    public Map getExclusions()
    {
        return this.groupExclusions;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    public String toString()
    {
        StringBuffer sb = new StringBuffer();
        sb.append( "INCLUSIONS:\n" );
        Map inc = getInclusions();
        for ( Iterator groups = inc.keySet().iterator(); groups.hasNext(); )
        {
            String groupId = (String) groups.next();
            List gInc = (List) inc.get( groupId );
            for ( Iterator patterns = gInc.iterator(); patterns.hasNext(); )
            {
                sb.append( patterns.next() );
                sb.append( "\n" );
            }

        }
        sb.append( "EXCLUSIONS:\n" );
        Map exc = getExclusions();
        for ( Iterator groups = exc.keySet().iterator(); groups.hasNext(); )
        {
            String groupId = (String) groups.next();
            List gExc = (List) exc.get( groupId );
            for ( Iterator patterns = gExc.iterator(); patterns.hasNext(); )
            {
                sb.append( patterns.next() );
                sb.append( "\n" );
            }

        }
        return sb.toString();
    }

}
