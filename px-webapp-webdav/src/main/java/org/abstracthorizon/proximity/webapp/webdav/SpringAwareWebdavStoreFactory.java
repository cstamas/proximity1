/*

   Copyright 2005-2007 Tamas Cservenak (t.cservenak@gmail.com)

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.

*/
package org.abstracthorizon.proximity.webapp.webdav;

import javax.servlet.ServletContext;

import net.sf.webdav.WebdavStore;

import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

// TODO: Auto-generated Javadoc
/**
 * A factory for creating SpringAwareWebdavStore objects.
 */
public class SpringAwareWebdavStoreFactory
{

    /** The context. */
    private WebApplicationContext context;

    /**
     * Instantiates a new spring aware webdav store factory.
     * 
     * @param ctx the ctx
     */
    public SpringAwareWebdavStoreFactory( ServletContext ctx )
    {
        super();
        context = WebApplicationContextUtils.getWebApplicationContext( ctx );
    }

    /**
     * Gets the store.
     * 
     * @param beanName the bean name
     * 
     * @return the store
     * 
     * @throws InstantiationException the instantiation exception
     * @throws IllegalAccessException the illegal access exception
     */
    public WebdavStore getStore( String beanName )
        throws InstantiationException,
            IllegalAccessException
    {
        return (WebdavStore) context.getBean( beanName, WebdavStore.class );
    }

}
