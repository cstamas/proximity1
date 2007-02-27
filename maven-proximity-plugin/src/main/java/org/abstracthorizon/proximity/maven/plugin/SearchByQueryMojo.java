package org.abstracthorizon.proximity.maven.plugin;

import org.abstracthorizon.proximity.ItemProperties;
import org.abstracthorizon.proximity.ws.SearchService;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;

/**
 * Goal to trigger reindexing on remote Proximity over SOAP iface.
 * 
 * @goal search-by-query
 * 
 * @author cstamas
 * 
 */
public class SearchByQueryMojo extends AbstractProximityMojo {
	
	/**
	 * The Lucene query to pass for Proximity search.
	 * @parameter expression="${searchQuery}"
	 * @required
	 */
	private String searchQuery;

	public void execute() throws MojoExecutionException, MojoFailureException {

		try {
			SearchService service = getSearchService();
			ItemProperties[] results = service.searchItemByQuery(searchQuery);
			
			getLog().info("");
			if (results != null && results.length > 0) {
				getLog().info("The Proximity results are:");
				for (int i = 0; i < results.length; i++) {
					ItemProperties ip = (ItemProperties) results[i];
					getLog().info("  o " + ip.getDirectoryPath() + " : " + ip.getName());
				}
			} else {
				getLog().info("The query has no results.");
			}
			getLog().info("");
		} catch (Exception ex) {
			throw new MojoExecutionException(
					"Could not invoke Proximity over SOAP", ex);
		}

	}

}
