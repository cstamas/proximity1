package hu.ismicro.commons.proximity.maven;

import hu.ismicro.commons.proximity.ProximityRequest;
import hu.ismicro.commons.proximity.Repository;
import hu.ismicro.commons.proximity.base.ProxiedItem;
import hu.ismicro.commons.proximity.base.logic.DefaultExpiringProxyingLogic;

import java.util.Date;

/**
 * Maven 1 and 2 aware proxy logic. It is configurable about expiring time for
 * SNAPSHOTs, POMs and METADATAs.
 * 
 * @author cstamas
 * 
 */
public class MavenProxyLogic extends DefaultExpiringProxyingLogic {

	/**
	 * Expiration period of SNAPSHOT artifacts in mseconds or 0 to refetch. Put NO_EXPIRATION to disable this feature.
	 * always.
	 */
	private long snapshotExpirationPeriod = 86400 * 1000; // 24 hours

	/**
	 * Expiration period of METADATA artifacts in mseconds or 0 to refetch. Put NO_EXPIRATION to disable this feature.
	 * always.
	 */
	private long metadataExpirationPeriod = 86400 * 1000; // 24 hours

	/**
	 * Expiration period of POM artifacts in mseconds or 0 to refetch always. Put NO_EXPIRATION to disable this feature.
	 */
	private long pomExpirationPeriod = 86400 * 1000; // 24 hours

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

	public ProxiedItem afterLocalCopyFound(ProxiedItem item, Repository repository) {
		// override super, should not delete even if expired!
		return item;
	}

	public boolean shouldCheckForRemoteCopy(ProximityRequest request, ProxiedItem localItem) {
        
        if (localItem != null) {
            if (localItem.getProperties().getMetadata(DefaultExpiringProxyingLogic.METADATA_EXPIRES) != null) {
                logger.debug("Item has expiration, checking it.");
                Date expires = new Date(Long.parseLong(localItem.getProperties().getMetadata(DefaultExpiringProxyingLogic.METADATA_EXPIRES)));
                if (expires.before(new Date(System.currentTimeMillis()))) {
                    // expired
                    // forcing remote retrieval, which will replace the item locally, but do not delete
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
		if (MavenArtifactRecognizer.isPom(remoteItem.getProperties().getName())) {
			if (pomExpirationPeriod != NO_EXPIRATION) {
				logger.info("Item is Maven 2 POM, setting expires on it to " + pomExpirationPeriod / 1000
						+ " seconds.");
                remoteItem.getProperties().setMetadata(DefaultExpiringProxyingLogic.METADATA_EXPIRES,
						Long.toString(System.currentTimeMillis() + pomExpirationPeriod));
			}
            remoteItem.getProperties().setMetadata("item.isPom", Boolean.TRUE.toString());
		} else if (MavenArtifactRecognizer.isMetadata(remoteItem.getProperties().getName())) {
			if (metadataExpirationPeriod != NO_EXPIRATION) {
				logger.info("Item is Maven 2 Metadata, setting expires on it to " + metadataExpirationPeriod
						/ 1000 + " seconds.");
                remoteItem.getProperties().setMetadata(DefaultExpiringProxyingLogic.METADATA_EXPIRES,
						Long.toString(System.currentTimeMillis() + metadataExpirationPeriod));
			}
            remoteItem.getProperties().setMetadata("item.isMetadata", Boolean.TRUE.toString());
		} else if (MavenArtifactRecognizer.isSnapshot(remoteItem.getProperties().getName())) {
			if (snapshotExpirationPeriod != NO_EXPIRATION) {
				logger.info("Item is Maven 1/2 Snapshot, setting expires on it to " + snapshotExpirationPeriod
						/ 1000 + " seconds.");
                remoteItem.getProperties().setMetadata(DefaultExpiringProxyingLogic.METADATA_EXPIRES,
						Long.toString(System.currentTimeMillis() + snapshotExpirationPeriod));
			}
            remoteItem.getProperties().setMetadata("item.isSnapshot", Boolean.TRUE.toString());
		} else {
            remoteItem = super.afterRemoteCopyFound(localItem, remoteItem, repository);
		}
		return remoteItem;
	}

}
