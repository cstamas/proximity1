package hu.ismicro.commons.proximity.base.logic;

import hu.ismicro.commons.proximity.ItemProperties;
import hu.ismicro.commons.proximity.ProximityRequest;
import hu.ismicro.commons.proximity.Repository;
import hu.ismicro.commons.proximity.base.PathHelper;
import hu.ismicro.commons.proximity.base.ProxiedItem;

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

	public boolean isMetadataRefetch() {
		return this.metadataExpirationPeriod == 0;
	}

	public boolean isPomRefetch() {
		return this.pomExpirationPeriod == 0;
	}

	public boolean isSnapshotRefetch() {
		return this.snapshotExpirationPeriod == 0;
	}

	protected boolean isPom(String name) {
		return name.endsWith(".pom");
	}

	protected boolean isSnapshot(String name) {
		return name.indexOf("SNAPSHOT") != -1;
	}

	protected boolean isMetadata(String name) {
		return name.startsWith("maven-metadata.xml") || name.endsWith(".sha1") || name.endsWith(".md5");
	}

	// =========================================================================
	// Logic iface

	public boolean shouldCheckForRemoteCopy(String path, boolean locallyExists) {
		if (!locallyExists) {
			return true;
		}
		if (isPom(PathHelper.getFileName(path))) {
			return isPomRefetch();
		}
		if (isMetadata(PathHelper.getFileName(path))) {
			return isMetadataRefetch();
		}
		if (isSnapshot(PathHelper.getFileName(path))) {
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
			if (pomExpirationPeriod != -1) {
				logger.info("Item is Maven 2 POM, setting expires on it to " + pomExpirationPeriod / 1000
						+ " seconds.");
				item.getProperties().setMetadata(ItemProperties.METADATA_EXPIRES,
						Long.toString(System.currentTimeMillis() + pomExpirationPeriod));
			}
			item.getProperties().setMetadata("item.isPom", Boolean.TRUE.toString());
		} else if (isMetadata(item.getProperties().getName())) {
			if (metadataExpirationPeriod != -1) {
				logger.info("Item is Maven 2 Metadata, setting expires on it to " + metadataExpirationPeriod
						/ 1000 + " seconds.");
				item.getProperties().setMetadata(ItemProperties.METADATA_EXPIRES,
						Long.toString(System.currentTimeMillis() + metadataExpirationPeriod));
			}
			item.getProperties().setMetadata("item.isMetadata", Boolean.TRUE.toString());
		} else if (isSnapshot(item.getProperties().getName())) {
			if (snapshotExpirationPeriod != -1) {
				logger.info("Item is Maven 1/2 Snapshot, setting expires on it to " + snapshotExpirationPeriod
						/ 1000 + " seconds.");
				item.getProperties().setMetadata(ItemProperties.METADATA_EXPIRES,
						Long.toString(System.currentTimeMillis() + snapshotExpirationPeriod));
			}
			item.getProperties().setMetadata("item.isSnapshot", Boolean.TRUE.toString());
		} else {
			item = super.afterRemoteCopyFound(item, repository);
		}
		return item;
	}

}
