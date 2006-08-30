package org.abstracthorizon.proximity.metadata.inspectors;


import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.abstracthorizon.proximity.ItemProperties;
import org.abstracthorizon.proximity.impl.ItemPropertiesImpl;

public class ZipFileInspector extends AbstractItemInspector {

    public static String ZIP_FILES = "zip.files";

    public static String ZIP_DIRS = "zip.dirs";

    public boolean isHandled(ItemPropertiesImpl ip) {
        try {
            String ext = ip.getMetadata(ItemProperties.METADATA_EXT).toLowerCase();
            return "zip".equals(ext);
        } catch (NullPointerException ex) {
            return false;
        }
    }

    public List getIndexableKeywords() {
        List result = new ArrayList(1);
        result.add(ZIP_DIRS);
        result.add(ZIP_FILES);
        return result;
    }

    public void processItem(ItemPropertiesImpl ip, File file) {
        try {
            ZipFile zFile = new ZipFile(file);
            StringBuffer files = new StringBuffer(zFile.size());
            StringBuffer dirs = new StringBuffer(zFile.size());

            for (Enumeration e = zFile.entries(); e.hasMoreElements();) {
                ZipEntry entry = (ZipEntry) e.nextElement();
                if (entry.isDirectory()) {
                    dirs.append(entry.getName());
                    dirs.append("\n");
                } else {
                    files.append(entry.getName());
                    files.append("\n");
                }
            }

            zFile.close();
            ip.setMetadata(ZIP_FILES, files.toString());
            ip.setMetadata(ZIP_DIRS, dirs.toString());
        } catch (IOException ex) {
            logger.info("Got IOException while creating ZipFile on file [{}].", file.getAbsolutePath(), ex);
        }
    }

}
