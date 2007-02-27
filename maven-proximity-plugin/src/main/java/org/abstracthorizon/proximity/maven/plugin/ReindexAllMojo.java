package org.abstracthorizon.proximity.maven.plugin;

import org.abstracthorizon.proximity.ws.MaintenanceService;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;

/**
 * Goal to trigger reindexing on remote Proximity over SOAP iface.
 * 
 * @goal reindex-all
 * 
 * @author cstamas
 * 
 */
public class ReindexAllMojo extends AbstractProximityMojo {

	public void execute() throws MojoExecutionException, MojoFailureException {

		try {
			MaintenanceService service = getMaintenanceService();
			service.reindexAll();
			getLog().info("reindexAll() invoked on Proximity.");
		} catch (Exception ex) {
			throw new MojoExecutionException(
					"Could not invoke Proximity over SOAP", ex);
		}
	}

}
