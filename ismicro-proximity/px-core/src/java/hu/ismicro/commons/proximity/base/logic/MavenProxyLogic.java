package hu.ismicro.commons.proximity.base.logic;

import hu.ismicro.commons.proximity.ItemProperties;
import hu.ismicro.commons.proximity.Repository;
import hu.ismicro.commons.proximity.base.PathHelper;
import hu.ismicro.commons.proximity.base.ProxiedItem;

/**
 * Maven 1 and 2 aware proxy logic. It is configurable about
 * expiring time for SNAPHSOTs, POMs and METADATAs. 
 * @author cstamas
 *
 */
public class MavenProxyLogic extends DefaultExpiringProxyingLogic {

	private long snapshotExpirationPeriod = 86400 * 1000; // 24 hours

	private boolean snapshotRefetch = true;

	private long metadataExpirationPeriod = 86400 * 1000; // 24 hours

	private boolean metadataRefetch = true;

	private long pomExpirationPeriod = 86400 * 1000; // 24 hours

	private boolean pomRefetch = true;

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
		return metadataRefetch;
	}

	public void setMetadataRefetch(boolean metadataRefetch) {
		this.metadataRefetch = metadataRefetch;
	}

	public boolean isPomRefetch() {
		return pomRefetch;
	}

	public void setPomRefetch(boolean pomRefetch) {
		this.pomRefetch = pomRefetch;
	}

	public boolean isSnapshotRefetch() {
		return snapshotRefetch;
	}

	public void setSnapshotRefetch(boolean snapshotRefetch) {
		this.snapshotRefetch = snapshotRefetch;
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
		if (isPom(item.getProperties().getName()) && isPomRefetch()) {
			item.getProperties().setMetadata(ItemProperties.METADATA_EXPIRES,
					Long.toString(System.currentTimeMillis() + getPomExpirationPeriod()));
		}
		if (isMetadata(item.getProperties().getName()) && isMetadataRefetch()) {
			item.getProperties().setMetadata(ItemProperties.METADATA_EXPIRES,
					Long.toString(System.currentTimeMillis() + getMetadataExpirationPeriod()));
		}
		if (isSnapshot(item.getProperties().getName()) && isSnapshotRefetch()) {
			item.getProperties().setMetadata(ItemProperties.METADATA_EXPIRES,
					Long.toString(System.currentTimeMillis() + getSnapshotExpirationPeriod()));
		}
		return item;
	}

}
