package hu.ismicro.commons.proximity.base.logic;

import hu.ismicro.commons.proximity.ItemProperties;
import hu.ismicro.commons.proximity.Repository;
import hu.ismicro.commons.proximity.base.PathHelper;
import hu.ismicro.commons.proximity.base.ProxiedItem;

import java.util.Date;

/**
 * Abstract helper class to implement items with expiration. It only checks
 * the expires metadata, some subclass should implement the metadata setting.
 * 
 * @author cstamas
 *
 */
public abstract class DefaultExpiringProxyingLogic extends DefaultProxyingLogic  {

	public ProxiedItem afterLocalCopyFound(ProxiedItem item, Repository repository) {
		if (item.getProperties().getMetadata(ItemProperties.METADATA_EXPIRES) != null) {
			logger.info("Item has expiration, using it.");
			Date expires = new Date(Long.parseLong(item.getProperties().getMetadata(ItemProperties.METADATA_EXPIRES)));
			if (expires.before(new Date(System.currentTimeMillis()))) {
				repository.deleteItem(PathHelper.walkThePath(item.getProperties().getAbsolutePath(), item
						.getProperties().getName()));
				return null;
			}
		}
		return item;
	}

}
