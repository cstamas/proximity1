package hu.ismicro.commons.proximity.maven;

import hu.ismicro.commons.proximity.ItemProperties;
import hu.ismicro.commons.proximity.ProximityRequest;
import hu.ismicro.commons.proximity.Repository;
import hu.ismicro.commons.proximity.base.ProxiedItem;
import hu.ismicro.commons.proximity.base.logic.DefaultExpiringProxyingRepositoryLogic;

import java.util.Date;

/**
 * Maven 1 and 2 aware proxy logic. It is configurable about expiring time for
 * SNAPSHOTs, POMs and METADATAs.
 * 
 * @author cstamas
 * 
 */
public class MavenProxyRepositoryLogic extends DefaultExpiringProxyingRepositoryLogic {

    /**
     * Expiration period of SNAPSHOT artifacts in mseconds or 0 to refetch. Put
     * NO_EXPIRATION to disable this feature. always.
     */
    private long snapshotExpirationPeriod = 86400 * 1000; // 24 hours

    /**
     * Expiration period of METADATA artifacts in mseconds or 0 to refetch. Put
     * NO_EXPIRATION to disable this feature. always.
     */
    private long metadataExpirationPeriod = 86400 * 1000; // 24 hours

    /**
     * Expiration period of POM artifacts in mseconds or 0 to refetch always.
     * Put NO_EXPIRATION to disable this feature.
     */
    private long pomExpirationPeriod = 86400 * 1000; // 24 hours

    /**
     * Should repository driven by this logic serve snapshots?
     */
    private boolean shouldServeSnapshots = false;

    /**
     * Should repository driven by this logic serve releases?
     */
    private boolean shouldServeReleases = true;

    public boolean isShouldServeReleases() {
        return shouldServeReleases;
    }

    public void setShouldServeReleases(boolean shouldServeReleases) {
        this.shouldServeReleases = shouldServeReleases;
    }

    public boolean isShouldServeSnapshots() {
        return shouldServeSnapshots;
    }

    public void setShouldServeSnapshots(boolean shouldServeSnapshots) {
        this.shouldServeSnapshots = shouldServeSnapshots;
    }

    public long getMetadataExpirationPeriodInSeconds() {
        return metadataExpirationPeriod / 1000;
    }

    public void setMetadataExpirationPeriodInSeconds(long metadataExpirationPeriod) {
        this.metadataExpirationPeriod = metadataExpirationPeriod * 1000;
    }

    public long getPomExpirationPeriodInSeconds() {
        return pomExpirationPeriod / 1000;
    }

    public void setPomExpirationPeriodInSeconds(long pomExpirationPeriod) {
        this.pomExpirationPeriod = pomExpirationPeriod * 1000;
    }

    public long getSnapshotExpirationPeriodInSeconds() {
        return snapshotExpirationPeriod / 1000;
    }

    public void setSnapshotExpirationPeriodInSeconds(long snapshotExpirationPeriod) {
        this.snapshotExpirationPeriod = snapshotExpirationPeriod * 1000;
    }

    // =========================================================================
    // Logic iface

    public boolean shouldCheckForLocalCopy(ProximityRequest request) {
        return true;
    }

    public ProxiedItem afterLocalCopyFound(ProxiedItem item, Repository repository) {
        // override super, should not delete even if expired!
        if (shouldServeByPolicies(item.getProperties())) {
            return item;
        } else {
            return null;
        }
    }

    public boolean shouldCheckForRemoteCopy(ProximityRequest request, ProxiedItem localItem) {

        if (localItem != null) {
            if (localItem.getProperties().getMetadata(DefaultExpiringProxyingRepositoryLogic.METADATA_EXPIRES) != null) {
                logger.debug("Item has expiration, checking it.");
                Date expires = new Date(Long.parseLong(localItem.getProperties().getMetadata(
                        DefaultExpiringProxyingRepositoryLogic.METADATA_EXPIRES)));
                if (expires.before(new Date(System.currentTimeMillis()))) {
                    // expired
                    // forcing remote retrieval, which will replace the item
                    // locally, but do not delete
                    logger.info("Item has expired on " + expires + ", expiring it.");
                    return true;
                } else {
                    // has expiration but not expired
                    return false;
                }
            } else {
                // we have it locally and have no expiration
                return false;
            }
        } else {
            // we have no local item
            return super.shouldCheckForRemoteCopy(request, localItem);
        }

    }

    public ProxiedItem afterRemoteCopyFound(ProxiedItem localItem, ProxiedItem remoteItem, Repository repository) {

        if (MavenArtifactRecognizer.isSnapshot(remoteItem.getProperties().getName())) {
            if (snapshotExpirationPeriod != NO_EXPIRATION) {
                logger.info("Item is Maven 1/2 Snapshot, setting expires on it to " + snapshotExpirationPeriod / 1000
                        + " seconds.");
                remoteItem.getProperties().setMetadata(DefaultExpiringProxyingRepositoryLogic.METADATA_EXPIRES,
                        Long.toString(System.currentTimeMillis() + snapshotExpirationPeriod), false);
            }

        } else if (MavenArtifactRecognizer.isPom(remoteItem.getProperties().getName())) {

            if (pomExpirationPeriod != NO_EXPIRATION) {
                logger
                        .info("Item is Maven 2 POM, setting expires on it to " + pomExpirationPeriod / 1000
                                + " seconds.");
                remoteItem.getProperties().setMetadata(DefaultExpiringProxyingRepositoryLogic.METADATA_EXPIRES,
                        Long.toString(System.currentTimeMillis() + pomExpirationPeriod), false);
            }

        } else if (MavenArtifactRecognizer.isMetadata(remoteItem.getProperties().getName())) {

            if (metadataExpirationPeriod != NO_EXPIRATION) {
                logger.info("Item is Maven 2 Metadata, setting expires on it to " + metadataExpirationPeriod / 1000
                        + " seconds.");
                remoteItem.getProperties().setMetadata(DefaultExpiringProxyingRepositoryLogic.METADATA_EXPIRES,
                        Long.toString(System.currentTimeMillis() + metadataExpirationPeriod), false);
            }

        } else {

            remoteItem = super.afterRemoteCopyFound(localItem, remoteItem, repository);
        }


        if (shouldServeByPolicies(remoteItem.getProperties())) {
            return remoteItem;
        } else {
            return null;
        }


    }

    // =========================================================================
    // Logic iface

    /**
     * Simply apply the policies.
     * 
     */
    protected boolean shouldServeByPolicies(ItemProperties item) {

        if (MavenArtifactRecognizer.isMetadata(item.getName())) {
            // metadatas goes always
            return true;
        }
        if (MavenArtifactRecognizer.isSnapshot(item.getName())) {
            // snapshots goes if enabled
            return isShouldServeSnapshots();
        }

        // in any other case take it as release
        // TODO: review this, WHAT is a "release"?
        return item.isDirectory() || isShouldServeReleases();

    }

}
