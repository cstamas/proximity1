package org.abstracthorizon.proximity.metadata;

import java.io.File;
import java.io.FileInputStream;
import java.security.DigestInputStream;
import java.security.MessageDigest;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.io.IOUtils;

public class FileDigest {

    private FileDigest() {
	super();
    }

    public static byte[] getFileDigest(File file, String alg) {
	byte[] result;
	try {
	    MessageDigest dalg = MessageDigest.getInstance(alg);
	    FileInputStream fis = null;
	    DigestInputStream dis = null;
	    try {
		fis = new FileInputStream(file);
		DevNullOutputStream fos = new DevNullOutputStream();
		dis = new DigestInputStream(fis, dalg);
		IOUtils.copy(dis, fos);
		result = dalg.digest();
	    } finally {
		if (dis != null) {
		    dis.close();
		}
		if (fis != null) {
		    fis.close();
		}
	    }
	    return result;
	} catch (Exception ex) {
	    return null;
	}
    }

    public static String getFileDigestAsString(File file, String alg) {
	try {
	    return new String(Hex.encodeHex(getFileDigest(file, alg)));
	} catch (Exception ex) {
	    return null;
	}
    }

}
