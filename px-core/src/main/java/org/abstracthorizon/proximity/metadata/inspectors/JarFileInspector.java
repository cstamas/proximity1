package org.abstracthorizon.proximity.metadata.inspectors;


import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.jar.Attributes;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.Manifest;

import org.abstracthorizon.proximity.ItemProperties;
import org.abstracthorizon.proximity.impl.ItemPropertiesImpl;

public class JarFileInspector extends AbstractItemInspector {

    public static String JAR_MF = "jar.mf";

    public static String JAR_DIRS = "jar.dirs";

    public static String JAR_FILES = "jar.files";

    public boolean isHandled(ItemPropertiesImpl ip) {
        try {
            String ext = ip.getMetadata(ItemProperties.METADATA_EXT).toLowerCase();
            return "jar".equals(ext) || "war".equals(ext) || "ear".equals(ext);
        } catch (NullPointerException ex) {
            return false;
        }
    }

    public List getIndexableKeywords() {
        List result = new ArrayList(1);
        result.add(JAR_FILES);
        result.add(JAR_DIRS);
        result.add(JAR_MF);
        return result;
    }

    public void processItem(ItemPropertiesImpl ip, File file) {
        try {

            JarFile jFile = new JarFile(file);
            StringBuffer dirs = new StringBuffer(jFile.size());
            StringBuffer files = new StringBuffer(jFile.size());

            for (Enumeration e = jFile.entries(); e.hasMoreElements();) {
                JarEntry entry = (JarEntry) e.nextElement();
                if (entry.isDirectory()) {
                    dirs.append(entry.getName());
                    dirs.append("\n");
                } else {
                    files.append(entry.getName());
                    files.append("\n");
                }
            }

            ip.setMetadata(JAR_FILES, files.toString());
            ip.setMetadata(JAR_DIRS, dirs.toString());

            Manifest mf = jFile.getManifest();
            if (mf != null) {
                StringBuffer mfEntries = new StringBuffer(jFile.getManifest().getMainAttributes().size());
                Attributes mAttr = mf.getMainAttributes();
                for (Iterator i = mAttr.keySet().iterator(); i.hasNext();) {
                    Attributes.Name atrKey = (Attributes.Name) i.next();
                    mfEntries.append(mAttr.getValue(atrKey));
                    mfEntries.append("\n");
                }
                ip.setMetadata(JAR_MF, mfEntries.toString());
            }

            jFile.close();

        } catch (IOException ex) {
            logger.info("Got IOException while creating JarFile on file [{}].", file.getAbsolutePath(), ex);
        }
    }

}
