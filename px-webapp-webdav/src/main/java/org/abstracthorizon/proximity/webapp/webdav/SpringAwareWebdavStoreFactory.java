package org.abstracthorizon.proximity.webapp.webdav;

import javax.servlet.ServletContext;

import net.sf.webdav.IWebdavStorage;

import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

public class SpringAwareWebdavStoreFactory {
    
    private WebApplicationContext context;

    public SpringAwareWebdavStoreFactory(ServletContext ctx) {
        super();
        context = WebApplicationContextUtils.getWebApplicationContext(ctx);
    }
    
    public IWebdavStorage getStore(String beanName) throws InstantiationException, IllegalAccessException {
        return (IWebdavStorage) context.getBean(beanName, IWebdavStorage.class);
    }
    
}
