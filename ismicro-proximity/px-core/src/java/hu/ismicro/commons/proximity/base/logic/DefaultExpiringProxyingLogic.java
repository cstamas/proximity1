package hu.ismicro.commons.proximity.base.logic;

import hu.ismicro.commons.proximity.ItemProperties;
import hu.ismicro.commons.proximity.Repository;
import hu.ismicro.commons.proximity.base.PathHelper;
import hu.ismicro.commons.proximity.base.ProxiedItem;

import java.util.Date;

/**
 * Simple logic with expiration support. If expirationPeriod is not 0, it will
 * apply expiration period onto items, and will handle removal of them when 
 * they expires.
 * 
 * @author cstamas
 * 
 */
public class DefaultExpiringProxyingLogic extends DefaultProxyingLogic {

	private long itemExpirationPeriod = 86400 * 1000; // 24 hours

	public long getItemExpirationPeriod() {
		return itemExpirationPeriod;
	}

	public void setItemExpirationPeriod(long itemExpirationPeriod) {
		this.itemExpirationPeriod = itemExpirationPeriod;
	}

	/**
	 * If item has defined EXPIRES metadata, will use it and remove item from
	 * repository if needed.
	 */
	public ProxiedItem afterLocalCopyFound(ProxiedItem item, Repository repository) {
		if (item.getProperties().getMetadata(ItemProperties.METADATA_EXPIRES) != null) {
			logger.debug("Item has expiration, checking it.");
			Date expires = new Date(Long.parseLong(item.getProperties().getMetadata(ItemProperties.METADATA_EXPIRES)));
			if (expires.before(new Date(System.currentTimeMillis()))) {
				logger.info("Item has expired on " + expires + ", deleting it.");
				repository.deleteItem(PathHelper.walkThePath(item.getProperties().getAbsolutePath(), item
						.getProperties().getName()));
				return null;
			}
		}
		return item;
	}

	/**
	 * If expiration period is not 0, it will apply it on all items.
	 */
	public ProxiedItem afterRemoteCopyFound(ProxiedItem item, Repository repository) {
		if (getItemExpirationPeriod() != 0) {
			Date expires = new Date(System.currentTimeMillis() + getItemExpirationPeriod()); 
			logger.info("Setting expires on item  to " + expires.toString());
			item.getProperties().setMetadata(ItemProperties.METADATA_EXPIRES,
					Long.toString(expires.getTime()));
		}
		return item;
	}

}
