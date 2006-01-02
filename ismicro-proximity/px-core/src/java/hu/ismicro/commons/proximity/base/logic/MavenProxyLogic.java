package hu.ismicro.commons.proximity.base.logic;

import hu.ismicro.commons.proximity.ItemProperties;
import hu.ismicro.commons.proximity.Repository;
import hu.ismicro.commons.proximity.base.PathHelper;
import hu.ismicro.commons.proximity.base.ProxiedItem;

/**
 * Maven 1 and 2 aware proxy logic. It is configurable about expiring time for
 * SNAPHSOTs, POMs and METADATAs.
 * 
 * @author cstamas
 * 
 */
public class MavenProxyLogic extends DefaultExpiringProxyingLogic {

	/**
	 * Expiration period of SNAPSHOT artifacts in mseconds or 0 to refetch
	 * always.
	 */
	private long snapshotExpirationPeriod = 86400 * 1000; // 24 hours

	/**
	 * Expiration period of METADATA artifacts in mseconds or 0 to refetch
	 * always.
	 */
	private long metadataExpirationPeriod = 86400 * 1000; // 24 hours

	/**
	 * Expiration period of POM artifacts in mseconds or 0 to refetch always.
	 */
	private long pomExpirationPeriod = 86400 * 1000; // 24 hours

	public long getMetadataExpirationPeriod() {
		return metadataExpirationPeriod;
	}

	public void setMetadataExpirationPeriod(long metadataExpirationPeriod) {
		this.metadataExpirationPeriod = metadataExpirationPeriod;
	}

	public long getPomExpirationPeriod() {
		return pomExpirationPeriod;
	}

	public void setPomExpirationPeriod(long pomExpirationPeriod) {
		this.pomExpirationPeriod = pomExpirationPeriod;
	}

	public long getSnapshotExpirationPeriod() {
		return snapshotExpirationPeriod;
	}

	public void setSnapshotExpirationPeriod(long snapshotExpirationPeriod) {
		this.snapshotExpirationPeriod = snapshotExpirationPeriod;
	}

	public boolean isMetadataRefetch() {
		return this.metadataExpirationPeriod == 0;
	}

	public boolean isPomRefetch() {
		return this.pomExpirationPeriod == 0;
	}

	public boolean isSnapshotRefetch() {
		return this.snapshotExpirationPeriod == 0;
	}

	protected boolean isPom(String path) {
		return PathHelper.getFileName(path).contains(".pom");
	}

	protected boolean isSnapshot(String path) {
		return PathHelper.getFileName(path).contains("SNAPSHOT");
	}

	protected boolean isMetadata(String path) {
		return PathHelper.getFileName(path).startsWith("maven-metadata.xml");
	}

	// =========================================================================
	// Logic iface

	public boolean shouldCheckForRemoteCopy(String path, boolean locallyExists) {
		if (!locallyExists) {
			return true;
		}
		if (isPom(path)) {
			return isPomRefetch();
		}
		if (isMetadata(path)) {
			return isMetadataRefetch();
		}
		if (isSnapshot(path)) {
			return isSnapshotRefetch();
		}
		return false;
	}

	public ProxiedItem afterRemoteCopyFound(ProxiedItem item, Repository repository) {
		if (isPom(item.getProperties().getName())) {
			logger.info("Item is Maven 2 POM, setting expires on it to " + getPomExpirationPeriod()/1000 + " seconds.");
			item.getProperties().setMetadata(ItemProperties.METADATA_EXPIRES,
					Long.toString(System.currentTimeMillis() + getPomExpirationPeriod()));
		} else if (isMetadata(item.getProperties().getName())) {
			logger.info("Item is Maven 2 Metadata, setting expires on it to " + getMetadataExpirationPeriod()/1000 + " seconds.");
			item.getProperties().setMetadata(ItemProperties.METADATA_EXPIRES,
					Long.toString(System.currentTimeMillis() + getMetadataExpirationPeriod()));
		} else if (isSnapshot(item.getProperties().getName())) {
			logger.info("Item is Maven 1/2 Snapshot, setting expires on it to " + getSnapshotExpirationPeriod()/1000 + " seconds.");
			item.getProperties().setMetadata(ItemProperties.METADATA_EXPIRES,
					Long.toString(System.currentTimeMillis() + getSnapshotExpirationPeriod()));
		} else {
			item = super.afterRemoteCopyFound(item, repository);
		}
		return item;
	}

}
