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
package org.abstracthorizon.proximity;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

// TODO: Auto-generated Javadoc
/**
 * Object encapsulating Proximity request.
 * 
 * @author cstamas
 */
public class ProximityRequest
    implements Serializable
{

    /** The Constant REQUEST_REMOTE_ADDRESS. */
    public static final String REQUEST_REMOTE_ADDRESS = "request.remoteAddr";

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = -3576402488409590517L;

    /** The grantee who is authenticated against infrastructure that holds Proximity Core. */
    private Object grantee;

    /** The Path of the request. */
    private String path;

    /** If true, will not try to fetch from remote. */
    private boolean localOnly = false;

    /** If true, no content will be supplied just ItemProperties. */
    private boolean propertiesOnly = false;

    /** The ID of the targeted repos if any or null. */
    private String targetedReposId = null;

    /** The ID of the targeted repos group if any or null. */
    private String targetedReposGroupId = null;

    /** Map of attributes if any. */
    private Map attributes = new HashMap();

    /**
     * Instantiates a new proximity request.
     */
    public ProximityRequest()
    {
        super();
    }

    /**
     * Instantiates a new proximity request.
     * 
     * @param path the path
     */
    public ProximityRequest( String path )
    {
        this();
        setPath( path );
    }

    /**
     * Instantiates a new proximity request.
     * 
     * @param rq the rq
     */
    public ProximityRequest( ProximityRequest rq )
    {
        this( rq.getPath() );
        setGrantee( rq.getGrantee() );
        setTargetedReposId( rq.getTargetedReposId() );
        setTargetedReposGroupId( rq.getTargetedReposGroupId() );
        getAttributes().putAll( rq.getAttributes() );
    }

    /**
     * Gets the grantee.
     * 
     * @return the grantee
     */
    public Object getGrantee()
    {
        return grantee;
    }

    /**
     * Sets the grantee.
     * 
     * @param grantee the new grantee
     */
    public void setGrantee( Object grantee )
    {
        this.grantee = grantee;
    }

    /**
     * Gets the path.
     * 
     * @return the path
     */
    public String getPath()
    {
        return path;
    }

    /**
     * Sets the path.
     * 
     * @param path the new path
     */
    public void setPath( String path )
    {
        this.path = path;
    }

    /**
     * Checks if is local only.
     * 
     * @return true, if is local only
     */
    public boolean isLocalOnly()
    {
        return localOnly;
    }

    /**
     * Sets the local only.
     * 
     * @param localOnly the new local only
     */
    public void setLocalOnly( boolean localOnly )
    {
        this.localOnly = localOnly;
    }

    /**
     * Checks if is properties only.
     * 
     * @return true, if is properties only
     */
    public boolean isPropertiesOnly()
    {
        return propertiesOnly;
    }

    /**
     * Sets the properties only.
     * 
     * @param propertiesOnly the new properties only
     */
    public void setPropertiesOnly( boolean propertiesOnly )
    {
        this.propertiesOnly = propertiesOnly;
    }

    /**
     * Gets the attributes.
     * 
     * @return the attributes
     */
    public Map getAttributes()
    {
        return attributes;
    }

    /**
     * Sets the attributes.
     * 
     * @param attributes the new attributes
     */
    public void setAttributes( Map attributes )
    {
        this.attributes = attributes;
    }

    /**
     * Gets the targeted repos id.
     * 
     * @return the targeted repos id
     */
    public String getTargetedReposId()
    {
        return targetedReposId;
    }

    /**
     * Sets the targeted repos id.
     * 
     * @param targetedReposId the new targeted repos id
     */
    public void setTargetedReposId( String targetedReposId )
    {
        this.targetedReposId = targetedReposId;
    }

    /**
     * Gets the targeted repos group id.
     * 
     * @return the targeted repos group id
     */
    public String getTargetedReposGroupId()
    {
        return targetedReposGroupId;
    }

    /**
     * Sets the targeted repos group id.
     * 
     * @param targetedReposGroupId the new targeted repos group id
     */
    public void setTargetedReposGroupId( String targetedReposGroupId )
    {
        this.targetedReposGroupId = targetedReposGroupId;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    public String toString()
    {
        StringBuffer str = new StringBuffer( "ProximityRequest[" );
        str.append( "grantee=" );
        str.append( getGrantee() );
        str.append( ", path=" );
        str.append( getPath() );
        str.append( ", targetedReposId=" );
        str.append( getTargetedReposId() );
        str.append( ", targetedReposGroupId=" );
        str.append( getTargetedReposGroupId() );
        str.append( ", attributes=" );
        str.append( getAttributes() );
        str.append( "]" );
        return str.toString();
    }

}
