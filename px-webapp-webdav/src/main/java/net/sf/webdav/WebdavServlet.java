/*
 * Copyright 1999,2004 The Apache Software Foundation.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.sf.webdav;

import java.io.File;
import java.lang.reflect.Constructor;

import javax.servlet.ServletException;

import org.abstracthorizon.proximity.webapp.webdav.SpringAwareWebdavStoreFactory;

import net.sf.webdav.exceptions.WebdavException;

/**
 * Servlet which provides support for WebDAV level 2.
 * 
 * the original class is org.apache.catalina.servlets.WebdavServlet by Remy
 * Maucherat, which was heavily changed
 * 
 * @author Remy Maucherat
 */

public class WebdavServlet extends WebDavServletBean {

    private static final String DEBUG_SERVLET_PARAMETER = "servletDebug";

    private static final String DEBUG_STORE_PARAMETER = "storeDebug";

    private static final String ROOTPATH_PARAMETER = "rootpath";

    public void init() throws ServletException {

        // Parameters from web.xml
        String clazzName = getServletConfig().getInitParameter("ResourceHandlerImplementation");
        if (clazzName == null || clazzName.equals("")) {
            clazzName = LocalFileSystemStore.class.getName();
        }

        int storeDebug = getIntInitParameter(DEBUG_STORE_PARAMETER);

        WebdavStore webdavStore = null;
        if (clazzName.startsWith("spring:")) {
        	try {
        		SpringAwareWebdavStoreFactory fFactory = new SpringAwareWebdavStoreFactory(getServletContext());
        		webdavStore = fFactory.getStore(clazzName.substring(7, clazzName.length()));
        	} catch (Exception e) {
        		e.printStackTrace();
    			throw new ServletException(e);
        	}
		} else {
			// goes with default way
			File root = getFileRoot();
	        webdavStore = constructStore(clazzName, storeDebug, root);
		}

        int servletDebug = getIntInitParameter(DEBUG_SERVLET_PARAMETER);

        boolean lazyFolderCreationOnPut = getInitParameter("lazyFolderCreationOnPut") != null
                && getInitParameter("lazyFolderCreationOnPut").equals("1");

        String dftIndexFile = getInitParameter("default-index-file");
        String insteadOf404 = getInitParameter("instead-of-404");

        int noContentLengthHeader = getIntInitParameter("no-content-length-headers");

        super.init(webdavStore, dftIndexFile, insteadOf404,
                noContentLengthHeader, lazyFolderCreationOnPut, servletDebug);
    }

    private int getIntInitParameter(String key) {
        return getInitParameter(key) == null ? -1 : Integer
                .parseInt(getInitParameter(key));

    }

    protected WebdavStore constructStore(String clazzName, int storeDebug,
            File root) {
        WebdavStore webdavStore;
        try {
            Class clazz = WebdavServlet.class.getClassLoader().loadClass(
                    clazzName);

            Constructor ctor = clazz.getConstructor(new Class[] {
                    Integer.class, File.class });

            webdavStore = (WebdavStore) ctor.newInstance(new Object[] {
                    new Integer(storeDebug), root });
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("some problem making store component", e);
        }
        return webdavStore;
    }

    private File getFileRoot() {
        String rootPath = getInitParameter(ROOTPATH_PARAMETER);
        if (rootPath == null) {
            throw new WebdavException("missing parameter: "
                    + ROOTPATH_PARAMETER);
        }
        if (rootPath.equals("*WAR-FILE-ROOT*")) {
            String file = LocalFileSystemStore.class.getProtectionDomain()
                    .getCodeSource().getLocation().getFile().replace('\\', '/');
            if (file.charAt(0) == '/'
                    && System.getProperty("os.name").indexOf("Windows") != -1) {
                file = file.substring(1, file.length());
            }

            int ix = file.indexOf("/WEB-INF/");
            if (ix != -1) {
                rootPath = file.substring(0, ix).replace('/',
                        File.separatorChar);
            } else {
                throw new WebdavException(
                        "Could not determine root of war file. Can't extract from path '"
                                + file + "' for this web container");
            }
        }
        return new File(rootPath);
    }

}
