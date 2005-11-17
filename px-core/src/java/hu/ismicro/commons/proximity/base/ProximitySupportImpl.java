package hu.ismicro.commons.proximity.base;

import hu.ismicro.commons.proximity.Item;
import hu.ismicro.commons.proximity.Proximity;
import hu.ismicro.commons.proximity.ProximitySupport;

import java.util.Iterator;
import java.util.List;
import java.util.regex.Pattern;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.support.MutableSortDefinition;
import org.springframework.beans.support.PropertyComparator;

public class ProximitySupportImpl implements ProximitySupport {

    protected Log logger = LogFactory.getLog(this.getClass());
    
    private Proximity proximity;

    public void setProximity(Proximity proximity) {
        this.proximity = proximity;
    }

    public Proximity getProximity() {
        return proximity;
    }
    
    protected List filterItemsWithRegexp(List items, String regexp) {
        Pattern patt = Pattern.compile(regexp);
        for (Iterator i = items.iterator(); i.hasNext(); ) {
            Item item = (Item) i.next();
            if (!patt.matcher(item.getName()).matches()) {
                i.remove();
            }
        }
        return items;
    }

    public List searchAllRepositories(String regexp) {
        logger.debug("Got request for search all repositories with regexp: " + regexp);
        List items = proximity.listItems("/");
        filterItemsWithRegexp(items, regexp);
        PropertyComparator.sort(items, new MutableSortDefinition("name", true, true));
        return items;
    }

    public List searchRepository(String reposName, String regexp) {
        logger.debug("Got request for search all repositories with regexp: " + regexp);
        List items = proximity.listItemsFromRepository("/", reposName);
        filterItemsWithRegexp(items, regexp);
        PropertyComparator.sort(items, new MutableSortDefinition("name", true, true));
        return items;
    }

}
