package net.sf.webdav;

import javax.servlet.ServletContext;

public class WebdavStoreFactory {

	private Class fImplementation;

	public WebdavStoreFactory(Class class1) {
		fImplementation = class1;
	}

	public IWebdavStorage getStore(ServletContext ctx) throws InstantiationException, IllegalAccessException {
		return (IWebdavStorage) fImplementation.newInstance();
	}

}
