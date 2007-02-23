package net.sf.webdav;

import net.sf.webdav.exceptions.UnauthenticatedException;
import net.sf.webdav.exceptions.WebdavException;
import net.sf.webdav.fromcatalina.MD5Encoder;
import net.sf.webdav.methods.DoCopy;
import net.sf.webdav.methods.DoDelete;
import net.sf.webdav.methods.DoGet;
import net.sf.webdav.methods.DoMkcol;
import net.sf.webdav.methods.DoMove;
import net.sf.webdav.methods.DoOptions;
import net.sf.webdav.methods.DoPropfind;
import net.sf.webdav.methods.DoPut;
import net.sf.webdav.methods.DoHead;
import net.sf.webdav.methods.DoNotImplemented;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Enumeration;
import java.util.HashMap;

public class WebDavServletBean extends HttpServlet {

    private static final boolean readOnly = false;
    private ResourceLocks resLocks;
    private WebdavStore store;
    private int debug = -1;
    private HashMap methodMap = new HashMap();

    public WebDavServletBean() {
        this.resLocks = new ResourceLocks();
        try {
            MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException();
        }
    }

    public void init(WebdavStore store, String dftIndexFile,
            String insteadOf404, int nocontentLenghHeaders,
            boolean lazyFolderCreationOnPut, int debug) throws ServletException {

        this.store = store;
        this.debug = debug;

        MimeTyper mimeTyper = new MimeTyper() {
            public String getMimeType(String path) {
                return getServletContext().getMimeType(path);
            }
        };

        register("GET", new DoGet(store, dftIndexFile, insteadOf404, resLocks, mimeTyper, nocontentLenghHeaders, debug));
        register("HEAD", new DoHead(store, dftIndexFile, insteadOf404, resLocks, mimeTyper, nocontentLenghHeaders, debug));
        DoDelete doDelete = (DoDelete) register("DELETE", new DoDelete(store, resLocks, readOnly, debug));
        DoCopy doCopy = (DoCopy) register("COPY", new DoCopy(store, resLocks, doDelete, readOnly, debug));
        register("MOVE", new DoMove(resLocks, doDelete, doCopy, readOnly, debug));
        register("MKCOL", new DoMkcol(store, resLocks, readOnly, debug));
        register("OPTIONS", new DoOptions(store, resLocks, debug));
        register("PUT", new DoPut(store, resLocks, readOnly, debug, lazyFolderCreationOnPut));
        register("PROPFIND", new DoPropfind(store, resLocks, readOnly, mimeTyper, debug));
        register("PROPPATCH", new DoNotImplemented(readOnly, debug));
        register("*NO*IMPL*", new DoNotImplemented(readOnly, debug));
    }

    private MethodExecutor register(String methodName, MethodExecutor method) {
        methodMap.put(methodName, method);
        return method;
    }

    /**
     * Handles the special WebDAV methods.
     */
    protected void service(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        String methodName = req.getMethod();

        if (debug == 1) {
            debugRequest(methodName, req);
        }

        try {
            store.begin(req.getUserPrincipal());
            store.checkAuthentication();
            resp.setStatus(WebdavStatus.SC_OK);

            try {
                MethodExecutor methodExecutor = (MethodExecutor) methodMap.get(methodName);
                if (methodExecutor == null) {
                    methodExecutor = (MethodExecutor) methodMap.get("*NO*IMPL*");
                }
                methodExecutor.execute(req, resp);

                store.commit();
            } catch (IOException e) {
                e.printStackTrace();
                resp.sendError(WebdavStatus.SC_INTERNAL_SERVER_ERROR);
                store.rollback();
                throw new ServletException(e);
            }

        } catch (UnauthenticatedException e) {
            resp.sendError(WebdavStatus.SC_FORBIDDEN);
        } catch (WebdavException e) {
            e.printStackTrace();
            throw new ServletException(e);
        }
    }

    private void debugRequest(String methodName, HttpServletRequest req) {
        System.out.println("-----------");
        System.out.println("WebdavServlet\n request: methodName = " + methodName);
        System.out.println("time: " + System.currentTimeMillis());
        System.out.println("path: " + req.getRequestURI() );
        System.out.println("-----------");
        Enumeration e = req.getHeaderNames();
        while (e.hasMoreElements()) {
            String s = (String) e.nextElement();
            System.out.println("header: " + s + " " + req.getHeader(s));
        }
        e = req.getAttributeNames();
        while (e.hasMoreElements()) {
            String s = (String) e.nextElement();
            System.out.println("attribute: " + s + " "
                    + req.getAttribute(s));
        }
        e = req.getParameterNames();
        while (e.hasMoreElements()) {
            String s = (String) e.nextElement();
            System.out.println("parameter: " + s + " "
                    + req.getParameter(s));
        }
    }


}
