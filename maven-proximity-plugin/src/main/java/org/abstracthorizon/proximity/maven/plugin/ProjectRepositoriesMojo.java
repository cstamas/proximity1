package org.abstracthorizon.proximity.maven.plugin;

import java.util.ArrayList;
import java.util.List;

import org.apache.maven.model.Repository;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProject;
import org.apache.maven.settings.Mirror;
import org.apache.maven.settings.Settings;

/**
 * Goal to report all known repository ID's and URL's of the current project.
 * 
 * @goal project-repositories
 * 
 * @author cstamas
 * 
 */
public class ProjectRepositoriesMojo extends AbstractProximityMojo {
	
	/**
	 * @parameter expression="${project}"
	 * @required
	 */
	private MavenProject mavenProject;

	/**
	 * @parameter expression="${settings}"
	 * @required
	 */
	private Settings settings;

	/**
	 * If true, the build will fail if one of the reposes is not served by
	 * Proximity.
	 * 
	 * @parameter expression="${failIfNotMirrored}" default-value="false"
	 */
	private boolean failIfNotMirrored;

	public void execute() throws MojoExecutionException, MojoFailureException {

		List notMirroredRepositories = new ArrayList();

		getLog().info("");
		for (int i = 0; i < mavenProject.getRepositories().size(); i++) {
			Repository repository = (Repository) mavenProject.getRepositories()
					.get(i);
			getLog().info("      RepoId: " + repository.getId());
			getLog().info("         URL: " + repository.getUrl());
			Mirror mirror = settings.getMirrorOf(repository.getId());
			if (mirror != null) {
				if (proximityBaseUrl != null && mirror.getUrl().startsWith(proximityBaseUrl.toString())) {
					getLog().info(" mirrored on: " + mirror.getUrl() + " (OK)");
				} else {
					getLog().info(" mirrored on: " + mirror.getUrl() + " (Unknown proxy)");
				}
			} else {
				notMirroredRepositories.add(repository);
				getLog().info("              Not mirrored.");
			}
		}
		getLog().info("");

		if (failIfNotMirrored && notMirroredRepositories.size() > 0) {
			getLog().error("");
			getLog().error("The following repositories are NOT mirrored:");
			for (int i = 0; i < notMirroredRepositories.size(); i++) {
				Repository repository = (Repository) notMirroredRepositories
						.get(i);
				getLog().error("  o " + repository.getId());
				getLog().error("    is still using " + repository.getUrl());
				if (proximityBaseUrl != null) {
					if (factoryDefaults.containsKey(repository.getId())) {
						// we know the "factory" groupId
						getLog().error(
								"    it should be mirrored by "
										+ proximityBaseUrl.toString()
										+ "/repository/" + factoryDefaults.get(repository.getId()));
					} else {
						// we do not know the "factory" groupId
						getLog().error(
								"    it should be mirrored by "
										+ proximityBaseUrl.toString()
										+ "/repository/(groupId of the given repository)");
					}
				}
			}
			getLog().error("");
			getLog().error("(The URLs are shown for default Proximity configuration)");
			throw new MojoFailureException(
					"There are non-mirrored repositories in the environment,");
		}

	}

}
