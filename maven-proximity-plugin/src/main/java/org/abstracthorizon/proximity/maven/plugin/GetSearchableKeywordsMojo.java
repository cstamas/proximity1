package org.abstracthorizon.proximity.maven.plugin;

import org.abstracthorizon.proximity.ws.SearchService;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;

/**
 * Goal to fetch searchable keywords from Proximity.
 * 
 * @goal get-searchable-keywords
 * 
 * @author cstamas
 * 
 */
public class GetSearchableKeywordsMojo extends AbstractProximityMojo {

	public void execute() throws MojoExecutionException, MojoFailureException {

		try {
			SearchService service = getSearchService();
			String[] keywords = service.getSearchableKeywords();

			getLog().info("");
			if (keywords != null && keywords.length > 0) {
				getLog().info("The Proximity searchable keywords are:");
				for (int i = 0; i < keywords.length; i++) {
					getLog().info("  o " + keywords[i]);
				}
			}
			getLog().info("");
		} catch (Exception ex) {
			throw new MojoExecutionException(
					"Could not invoke Proximity over SOAP", ex);
		}

	}

}
