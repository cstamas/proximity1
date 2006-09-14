package org.abstracthorizon.proximity.webapp.webdav;

import javax.servlet.ServletContext;

import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import net.sf.webdav.IWebdavStorage;
import net.sf.webdav.WebdavStoreFactory;

public class SpringAwareWebdavStoreFactory extends WebdavStoreFactory {

    public SpringAwareWebdavStoreFactory(Class clazz) {
        super(clazz);
    }
    
    public IWebdavStorage getStore(ServletContext ctx) throws InstantiationException, IllegalAccessException {
        // completely override super implementation, get the bean named "webdavstorage" from Spring context
        WebApplicationContext context = WebApplicationContextUtils.getWebApplicationContext(ctx);
        return (IWebdavStorage) context.getBean("webdavstorage");
    }
    
}
