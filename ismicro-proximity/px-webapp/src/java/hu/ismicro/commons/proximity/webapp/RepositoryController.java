package hu.ismicro.commons.proximity.webapp;

import hu.ismicro.commons.proximity.Item;
import hu.ismicro.commons.proximity.Proximity;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.support.MutableSortDefinition;
import org.springframework.beans.support.PropertyComparator;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;

public class RepositoryController extends MultiActionController {

    private Proximity proximity;

    public void setProximity(Proximity proximity) {
        this.proximity = proximity;
    }

    public Proximity getProximity() {
        return proximity;
    }

    public ModelAndView repositoryList(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String requestURI = request.getRequestURI().substring(
                request.getContextPath().length() + request.getServletPath().length());
        String orderBy = request.getParameter("orderBy") == null ? "name" : request.getParameter("orderBy");
        String targetRepository = request.getParameter("repositoryId");
        
        Item item = null;
        if (targetRepository == null) {
            logger.info("Got request for repository on URI: " + requestURI);
            item = proximity.retrieveItem(requestURI);
        } else {
            logger.info("Got request for repository on URI: " + requestURI + " from repository id " + targetRepository);
            item = proximity.retrieveItemFromRepository(requestURI, targetRepository);
        }

        if (item.isDirectory()) {
            List items = null;
            if (targetRepository == null) {
                items = proximity.listItems(requestURI);
            } else {
                items = proximity.listItemsFromRepository(requestURI, targetRepository);
            }
            PropertyComparator.sort(items, new MutableSortDefinition(orderBy, true, true));
            Map result = new HashMap();
            result.put("items", items);
            result.put("orderBy", orderBy);
            result.put("requestUri", requestURI);
            result.put("requestPathList", explodeUriToList(requestURI));
            return new ModelAndView("repository/repositoryList", result);
        } else {
            // TODO: Made this proper (content type by ext, size, etc...)
            InputStream is = item.getStream();
            OutputStream os = response.getOutputStream();
            response.setContentType("application/octet-stream");
            byte[] buffer = new byte[8192];
            int read = 0;
            int cumRead = 0;
            while (is.available() > 0) {
                read = is.read(buffer);
                os.write(buffer, 0, read);
                cumRead = cumRead + read;
            }
            response.setContentLength(cumRead);
            return null;
        }
    }

    protected List explodeUriToList(String uri) {
        List result = new ArrayList();
        String[] explodedUri = uri.split("/");
        StringBuffer sb = new StringBuffer("/");
        for (int i = 0; i < explodedUri.length; i++) {
            if (explodedUri[i].length() > 0) {
                result.add(sb + explodedUri[i]);
                sb.append(explodedUri[i] + "/");
            }
        }
        return result;
    }

}
