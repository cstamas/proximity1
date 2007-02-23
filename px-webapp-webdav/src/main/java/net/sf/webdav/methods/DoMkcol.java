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
package net.sf.webdav.methods;

import net.sf.webdav.WebdavStatus;
import net.sf.webdav.WebdavStore;
import net.sf.webdav.ResourceLocks;
import net.sf.webdav.exceptions.ObjectAlreadyExistsException;
import net.sf.webdav.exceptions.AccessDeniedException;
import net.sf.webdav.exceptions.WebdavException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class DoMkcol extends DeterminableMethod {

    private WebdavStore store;
    private ResourceLocks resourceLocks;
    private boolean readOnly;
    private int debug;

    public DoMkcol(WebdavStore store, ResourceLocks resourceLocks, boolean readOnly, int debug) {
        this.store = store;
        this.resourceLocks = resourceLocks;
        this.readOnly = readOnly;
        this.debug = debug;
    }

    public void execute(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        if (debug == 1)
            System.err.println("-- doMkcol");

        if (req.getContentLength() != 0) {
            resp.sendError(WebdavStatus.SC_NOT_IMPLEMENTED);
        } else {

            if (!readOnly) {
                // not readonly
                String path = getRelativePath(req);
                String parentPath = getParentPath(path);
                String lockOwner = "doMkcol" + System.currentTimeMillis()
                        + req.toString();
                if (resourceLocks.lock(path, lockOwner, true, 0)) {
                    try {
                        if (parentPath != null && store.isFolder(parentPath)) {
                            boolean isFolder = store.isFolder(path);
                            if (!store.objectExists(path)) {
                                try {
                                    store.createFolder(path);
                                } catch (ObjectAlreadyExistsException e) {
                                    String methodsAllowed = determineMethodsAllowed(
                                            true, isFolder);
                                    resp.addHeader("Allow", methodsAllowed);
                                    resp
                                            .sendError(WebdavStatus.SC_METHOD_NOT_ALLOWED);
                                }
                            } else {
                                // object already exists
                                String methodsAllowed = determineMethodsAllowed(
                                        true, isFolder);
                                resp.addHeader("Allow", methodsAllowed);
                                resp
                                        .sendError(WebdavStatus.SC_METHOD_NOT_ALLOWED);
                            }
                        } else {
                            resp.sendError(WebdavStatus.SC_CONFLICT);
                        }
                    } catch (AccessDeniedException e) {
                        resp.sendError(WebdavStatus.SC_FORBIDDEN);
                    } catch (WebdavException e) {
                        resp.sendError(WebdavStatus.SC_INTERNAL_SERVER_ERROR);
                    } finally {
                        resourceLocks.unlock(path, lockOwner);
                    }
                } else {
                    resp.sendError(WebdavStatus.SC_INTERNAL_SERVER_ERROR);
                }
            } else {
                resp.sendError(WebdavStatus.SC_FORBIDDEN);
            }
        }
    }
}
