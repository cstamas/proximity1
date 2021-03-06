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
package org.abstracthorizon.proximity.maven;

import java.util.Date;

import org.abstracthorizon.proximity.Item;
import org.abstracthorizon.proximity.ItemProperties;
import org.abstracthorizon.proximity.ProximityRequest;
import org.abstracthorizon.proximity.Repository;
import org.abstracthorizon.proximity.logic.DefaultExpiringProxyingRepositoryLogic;

// TODO: Auto-generated Javadoc
/**
 * Maven 1 and 2 aware proxy logic. It is configurable about expiring time for SNAPSHOTs, POMs and METADATAs.
 * 
 * @author cstamas
 */
public class MavenProxyRepositoryLogic
    extends DefaultExpiringProxyingRepositoryLogic
{

    /** Expiration period of SNAPSHOT artifacts in mseconds or 0 to refetch. Put NO_EXPIRATION to disable this feature. always. */
    private long snapshotExpirationPeriod = 86400 * 1000; // 24 hours

    /** Expiration period of METADATA artifacts in mseconds or 0 to refetch. Put NO_EXPIRATION to disable this feature. always. */
    private long metadataExpirationPeriod = 86400 * 1000; // 24 hours

    /** Expiration period of POM artifacts in mseconds or 0 to refetch always. Put NO_EXPIRATION to disable this feature. */
    private long pomExpirationPeriod = 86400 * 1000; // 24 hours

    /** Should repository driven by this logic serve snapshots?. */
    private boolean shouldServeSnapshots = false;

    /** Should repository driven by this logic serve releases?. */
    private boolean shouldServeReleases = true;

    /**
     * Checks if is should serve releases.
     * 
     * @return true, if is should serve releases
     */
    public boolean isShouldServeReleases()
    {
        return shouldServeReleases;
    }

    /**
     * Sets the should serve releases.
     * 
     * @param shouldServeReleases the new should serve releases
     */
    public void setShouldServeReleases( boolean shouldServeReleases )
    {
        this.shouldServeReleases = shouldServeReleases;
    }

    /**
     * Checks if is should serve snapshots.
     * 
     * @return true, if is should serve snapshots
     */
    public boolean isShouldServeSnapshots()
    {
        return shouldServeSnapshots;
    }

    /**
     * Sets the should serve snapshots.
     * 
     * @param shouldServeSnapshots the new should serve snapshots
     */
    public void setShouldServeSnapshots( boolean shouldServeSnapshots )
    {
        this.shouldServeSnapshots = shouldServeSnapshots;
    }

    /**
     * Gets the metadata expiration period in seconds.
     * 
     * @return the metadata expiration period in seconds
     */
    public long getMetadataExpirationPeriodInSeconds()
    {
        return metadataExpirationPeriod / 1000;
    }

    /**
     * Sets the metadata expiration period in seconds.
     * 
     * @param metadataExpirationPeriod the new metadata expiration period in seconds
     */
    public void setMetadataExpirationPeriodInSeconds( long metadataExpirationPeriod )
    {
        this.metadataExpirationPeriod = metadataExpirationPeriod * 1000;
    }

    /**
     * Gets the pom expiration period in seconds.
     * 
     * @return the pom expiration period in seconds
     */
    public long getPomExpirationPeriodInSeconds()
    {
        return pomExpirationPeriod / 1000;
    }

    /**
     * Sets the pom expiration period in seconds.
     * 
     * @param pomExpirationPeriod the new pom expiration period in seconds
     */
    public void setPomExpirationPeriodInSeconds( long pomExpirationPeriod )
    {
        this.pomExpirationPeriod = pomExpirationPeriod * 1000;
    }

    /**
     * Gets the snapshot expiration period in seconds.
     * 
     * @return the snapshot expiration period in seconds
     */
    public long getSnapshotExpirationPeriodInSeconds()
    {
        return snapshotExpirationPeriod / 1000;
    }

    /**
     * Sets the snapshot expiration period in seconds.
     * 
     * @param snapshotExpirationPeriod the new snapshot expiration period in seconds
     */
    public void setSnapshotExpirationPeriodInSeconds( long snapshotExpirationPeriod )
    {
        this.snapshotExpirationPeriod = snapshotExpirationPeriod * 1000;
    }

    // =========================================================================
    // Logic iface

    /* (non-Javadoc)
     * @see org.abstracthorizon.proximity.logic.DefaultExpiringProxyingRepositoryLogic#afterLocalCopyFound(org.abstracthorizon.proximity.Repository, org.abstracthorizon.proximity.ProximityRequest, org.abstracthorizon.proximity.Item)
     */
    public Item afterLocalCopyFound( Repository repository, ProximityRequest request, Item item )
    {
        // override super, should not delete even if expired!
        if ( shouldServeByPolicies( item.getProperties() ) )
        {
            return item;
        }
        else
        {
            logger.info( "Logic vetoed the [{}] item local retrieval due to repo policies!", request.getPath() );
            return null;
        }
    }

    /* (non-Javadoc)
     * @see org.abstracthorizon.proximity.logic.DefaultProxyingRepositoryLogic#shouldCheckForRemoteCopy(org.abstracthorizon.proximity.Repository, org.abstracthorizon.proximity.ProximityRequest, org.abstracthorizon.proximity.Item)
     */
    public boolean shouldCheckForRemoteCopy( Repository repository, ProximityRequest request, Item localItem )
    {
        if ( localItem != null )
        {
            if ( localItem.getProperties().getMetadata( DefaultExpiringProxyingRepositoryLogic.METADATA_EXPIRES ) != null )
            {
                logger.debug( "Item has expiration, checking it." );
                Date expires = new Date( Long.parseLong( localItem.getProperties().getMetadata(
                    DefaultExpiringProxyingRepositoryLogic.METADATA_EXPIRES ) ) );
                if ( expires.before( new Date( System.currentTimeMillis() ) ) )
                {
                    // expired
                    // forcing remote retrieval, which will replace the item
                    // locally, but do not delete
                    logger.info( "Item {} has expired on {}, trying to refetch it.", request.getPath(), expires );
                    // Fix
                    // fix for issue #104: Problem with item expiration in class MavenProxyRepositoryLogic
                    return true;
                    // was:
                    // return super.shouldCheckForRemoteCopy(repository, request, localItem);
                }
                else
                {
                    // has expiration but not expired
                    return false;
                }
            }
            else
            {
                // we have it locally and have no expiration
                return false;
            }
        }
        else
        {
            // we have no local item
            return super.shouldCheckForRemoteCopy( repository, request, localItem );
        }
    }

    /* (non-Javadoc)
     * @see org.abstracthorizon.proximity.logic.DefaultExpiringProxyingRepositoryLogic#afterRemoteCopyFound(org.abstracthorizon.proximity.Repository, org.abstracthorizon.proximity.ProximityRequest, org.abstracthorizon.proximity.Item, org.abstracthorizon.proximity.Item)
     */
    public Item afterRemoteCopyFound( Repository repository, ProximityRequest request, Item localItem, Item remoteItem )
    {

        if ( MavenArtifactRecognizer.isSnapshot( remoteItem.getProperties().getDirectoryPath(), remoteItem
            .getProperties().getName() ) )
        {
            if ( snapshotExpirationPeriod != NO_EXPIRATION )
            {
                logger.debug( "Item is Maven 1/2 Snapshot, setting expires on it to " + snapshotExpirationPeriod / 1000
                    + " seconds." );
                remoteItem.getProperties().setMetadata(
                    DefaultExpiringProxyingRepositoryLogic.METADATA_EXPIRES,
                    Long.toString( System.currentTimeMillis() + snapshotExpirationPeriod ) );
            }

        }
        else if ( MavenArtifactRecognizer.isPom( remoteItem.getProperties().getName() ) )
        {

            if ( pomExpirationPeriod != NO_EXPIRATION )
            {
                logger.debug( "Item is Maven 2 POM, setting expires on it to " + ( pomExpirationPeriod / 1000 )
                    + " seconds." );
                remoteItem.getProperties().setMetadata(
                    DefaultExpiringProxyingRepositoryLogic.METADATA_EXPIRES,
                    Long.toString( System.currentTimeMillis() + pomExpirationPeriod ) );
            }

        }
        else if ( MavenArtifactRecognizer.isMetadata( remoteItem.getProperties().getName() ) )
        {

            if ( metadataExpirationPeriod != NO_EXPIRATION )
            {
                logger.debug( "Item is Maven 2 Metadata, setting expires on it to " + metadataExpirationPeriod / 1000
                    + " seconds." );
                remoteItem.getProperties().setMetadata(
                    DefaultExpiringProxyingRepositoryLogic.METADATA_EXPIRES,
                    Long.toString( System.currentTimeMillis() + metadataExpirationPeriod ) );
            }

        }
        else
        {

            remoteItem = super.afterRemoteCopyFound( repository, request, localItem, remoteItem );
        }

        if ( shouldServeByPolicies( remoteItem.getProperties() ) )
        {
            return remoteItem;
        }
        else
        {
            logger.info( "Logic vetoed the [{}] item remote retrieval due to repo policies!", request.getPath() );
            return null;
        }
    }

    // =========================================================================
    // Logic iface

    /**
     * Simply apply the policies.
     * 
     * @param item the item
     * 
     * @return true, if should serve by policies
     */
    protected boolean shouldServeByPolicies( ItemProperties item )
    {
        if ( MavenArtifactRecognizer.isMetadata( item.getName() ) )
        {
            // metadatas goes always
            return true;
        }
        if ( MavenArtifactRecognizer.isSnapshot( item.getDirectoryPath(), item.getName() ) )
        {
            // snapshots goes if enabled
            return isShouldServeSnapshots();
        }

        // in any other case take it as release
        // TODO: review this, WHAT is a "release"?
        return item.isDirectory() || isShouldServeReleases();
    }

}
