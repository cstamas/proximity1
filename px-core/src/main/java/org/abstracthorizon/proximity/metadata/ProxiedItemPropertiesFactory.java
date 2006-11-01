package org.abstracthorizon.proximity.metadata;

import java.io.File;
import java.util.List;

import org.abstracthorizon.proximity.ItemProperties;

public interface ProxiedItemPropertiesFactory {

	/**
	 * Returns the list that this constructor marks as indexable.
	 * 
	 * @return list of keywords usable in searches.
	 */
	List getSearchableKeywords();

	/**
	 * Returns filled in item properties for file that is located on path.
	 * 
	 * @param file
	 *            The file about we need more to know.
	 * @return filled up ProxiedItemProperties.
	 */
	ItemProperties expandItemProperties(String path, File file, boolean defaultOnly);

}
