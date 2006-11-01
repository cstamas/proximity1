package org.abstracthorizon.proximity.webapp.view;

import org.abstracthorizon.proximity.webapp.view.FormatFileSize;

import junit.framework.TestCase;

public class FormatFileSizeTest extends TestCase {

	private FormatFileSize ffs = new FormatFileSize();

	public void testSimple() {
		System.out.println("1=" + ffs.getFileSizeAsString("1"));
		System.out.println("1a=" + ffs.getFileSizeAsString("1a"));
		System.out.println("1024=" + ffs.getFileSizeAsString("1024"));
		System.out.println("2545=" + ffs.getFileSizeAsString("2544"));
		System.out.println("34436=" + ffs.getFileSizeAsString("34436"));
		System.out.println("344363=" + ffs.getFileSizeAsString("344363"));
		System.out.println("3443633=" + ffs.getFileSizeAsString("3443633"));
		System.out.println("34436332=" + ffs.getFileSizeAsString("34436332"));
		System.out.println("344363329=" + ffs.getFileSizeAsString("344363329"));
		System.out.println("1040548345=" + ffs.getFileSizeAsString("1040548345"));
		System.out.println("1540548345=" + ffs.getFileSizeAsString("1540548345"));
		System.out.println("41540548345=" + ffs.getFileSizeAsString("41540548345"));
	}

}
