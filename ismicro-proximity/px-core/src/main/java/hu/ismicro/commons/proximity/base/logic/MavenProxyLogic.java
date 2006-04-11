package hu.ismicro.commons.proximity.base.logic;

import java.util.Date;

import hu.ismicro.commons.proximity.ItemProperties;
import hu.ismicro.commons.proximity.ProximityRequest;
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
	 * Expiration period of SNAPSHOT artifacts in mseconds or 0 to refetch. Put -1 to disable this feature.
	 * always.
	 */
	private long snapshotExpirationPeriod = 86400 * 1000; // 24 hours

	/**
	 * Expiration period of METADATA artifacts in mseconds or 0 to refetch. Put -1 to disable this feature.
	 * always.
	 */
	private long metadataExpirationPeriod = 86400 * 1000; // 24 hours

	/**
	 * Expiration period of POM artifacts in mseconds or 0 to refetch always. Put -1 to disable this feature.
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
		return PathHelper.getFileName(path).endsWith(".pom");
	}

	protected boolean isSnapshot(String path) {
		return PathHelper.getFileName(path).indexOf("SNAPSHOT") != -1;
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

	public ProxiedItem afterLocalCopyFound(ProxiedItem item, Repository repository) {
		// override super, should not delete!
		return item;
	}

	public boolean shouldCheckForRemoteCopy(ProximityRequest request, ProxiedItem localItem) {
		if (localItem != null && localItem.getProperties().getMetadata(ItemProperties.METADATA_EXPIRES) != null) {
			logger.debug("Item has expiration, checking it.");
			Date expires = new Date(Long.parseLong(localItem.getProperties().getMetadata(ItemProperties.METADATA_EXPIRES)));
			if (expires.before(new Date(System.currentTimeMillis()))) {
				logger.info("Item has expired on " + expires + ", deleting it.");
				return true;
			}
			return false;
		} else {
			return super.shouldCheckForRemoteCopy(request, localItem);
		}
	}

	public ProxiedItem afterRemoteCopyFound(ProxiedItem item, Repository repository) {
		if (isPom(item.getProperties().getName())) {
			if (getPomExpirationPeriod() != -1) {
				logger.info("Item is Maven 2 POM, setting expires on it to " + getPomExpirationPeriod() / 1000
						+ " seconds.");
				item.getProperties().setMetadata(ItemProperties.METADATA_EXPIRES,
						Long.toString(System.currentTimeMillis() + getPomExpirationPeriod()));
			}
			item.getProperties().setMetadata("item.isPom", Boolean.TRUE.toString());
		} else if (isMetadata(item.getProperties().getName())) {
			if (getMetadataExpirationPeriod() != -1) {
				logger.info("Item is Maven 2 Metadata, setting expires on it to " + getMetadataExpirationPeriod()
						/ 1000 + " seconds.");
				item.getProperties().setMetadata(ItemProperties.METADATA_EXPIRES,
						Long.toString(System.currentTimeMillis() + getMetadataExpirationPeriod()));
			}
			item.getProperties().setMetadata("item.isMetadata", Boolean.TRUE.toString());
		} else if (isSnapshot(item.getProperties().getName())) {
			if (getSnapshotExpirationPeriod() != -1) {
				logger.info("Item is Maven 1/2 Snapshot, setting expires on it to " + getSnapshotExpirationPeriod()
						/ 1000 + " seconds.");
				item.getProperties().setMetadata(ItemProperties.METADATA_EXPIRES,
						Long.toString(System.currentTimeMillis() + getSnapshotExpirationPeriod()));
			}
			item.getProperties().setMetadata("item.isSnapshot", Boolean.TRUE.toString());
		} else {
			item = super.afterRemoteCopyFound(item, repository);
		}
		return item;
	}

}
