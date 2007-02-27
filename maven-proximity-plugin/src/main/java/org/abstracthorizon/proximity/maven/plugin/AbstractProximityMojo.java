package org.abstracthorizon.proximity.maven.plugin;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.abstracthorizon.proximity.ws.MaintenanceService;
import org.abstracthorizon.proximity.ws.SearchService;
import org.apache.maven.plugin.AbstractMojo;
import org.codehaus.xfire.client.XFireProxyFactory;
import org.codehaus.xfire.service.Service;
import org.codehaus.xfire.service.ServiceFactory;
import org.codehaus.xfire.service.binding.ObjectServiceFactory;

public abstract class AbstractProximityMojo extends AbstractMojo {

	protected static final Map factoryDefaults;

	static {
		factoryDefaults = new HashMap();
		factoryDefaults.put("central", "public");
		factoryDefaults.put("codehaus", "codehaus");
		factoryDefaults.put("apache.snapshot", "apache.snapshot");
		factoryDefaults.put("codehaus.snapshot", "codehaus.snapshot");
	}

	/**
	 * The base URL of Proximity, used to check is Proximity mirrors the given
	 * repo.
	 * 
	 * @parameter expression="${proximityBaseUrl}"
	 */
	protected URL proximityBaseUrl;

	private ServiceFactory serviceFactory = new ObjectServiceFactory();

	private XFireProxyFactory proxyFactory = new XFireProxyFactory();

	private Object getRemoteService(String serviceName, Class clazz)
			throws MalformedURLException {
		StringBuffer serviceUrl = new StringBuffer(proximityBaseUrl.toString());
		if (!serviceUrl.toString().endsWith("/")) {
			serviceUrl.append("/");
		}
		serviceUrl.append("ws/");
		serviceUrl.append(serviceName);
		Service serviceModel = serviceFactory.create(clazz);
		return proxyFactory.create(serviceModel, serviceUrl.toString());
	}

	protected SearchService getSearchService() throws MalformedURLException {
		return (SearchService) getRemoteService("SearchService", SearchService.class);
	}

	protected MaintenanceService getMaintenanceService()
			throws MalformedURLException {
		return (MaintenanceService) getRemoteService("MaintenanceService", MaintenanceService.class);
	}
}
