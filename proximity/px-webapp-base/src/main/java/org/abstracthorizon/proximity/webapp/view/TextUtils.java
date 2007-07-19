package org.abstracthorizon.proximity.webapp.view;

public class TextUtils {
    
    private int maxLength = 40;
    
    public TextUtils() {
	super();
    }
    
    public String getAsLabel(Object string) {
	String label = string.toString();
	if (label.length() < maxLength) {
	    return label;
	} else {
	    return label.substring(0,maxLength / 2) + "...." + label.substring(label.length() - maxLength / 2, label.length());
	}
    }
    
}
