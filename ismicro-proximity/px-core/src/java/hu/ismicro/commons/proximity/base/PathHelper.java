package hu.ismicro.commons.proximity.base;

import java.util.Stack;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * <p>
 * A simple collection of static methods to handle paths.
 * 
 * <p>
 * Recognizes the following special codes:
 * 
 * <source> public static final String PATH_SEPARATOR =
 * System.getProperty("file.separator"); public static final String PATH_SELF =
 * "."; public static final String PATH_PARENT = ".."; </source>
 * 
 * @author cstamas
 * 
 */
public class PathHelper {

	protected static Log logger = LogFactory.getLog(PathHelper.class);

	public static final String PATH_SEPARATOR = System.getProperty("file.separator");

	public static final String PATH_SELF = ".";

	public static final String PATH_PARENT = "..";

	/**
	 * Calculates absolute path from base path.
	 * 
	 * @param base
	 * @param path
	 * @return
	 */
	public static String absolutizePathFromBase(final String base, final String path) {
		if (!path.startsWith(PATH_SEPARATOR) || path.contains(PATH_SELF) || path.contains(PATH_PARENT)) {
			Stack stack = new Stack();
			String[] explodedBase = base.split(PATH_SEPARATOR);
			for (int i = 0; i < explodedBase.length; i++) {
				if (explodedBase[i].length() > 0) {
					stack.push(explodedBase[i]);
				}
			}
			String[] explodedPath = path.split(PATH_SEPARATOR);
			for (int i = 0; i < explodedPath.length; i++) {
				if (explodedPath[i].length() > 0) {
					if (PATH_PARENT.equals(explodedPath[i])) {
						stack.pop();
					} else if (PATH_SELF.equals(explodedPath[i])) {
						// noop
					} else {
						stack.push(explodedPath[i]);
					}
				}
			}
			StringBuffer newPath = new StringBuffer();
			while (!stack.empty()) {
				newPath.insert(0, (String) stack.pop());
				newPath.insert(0, PATH_SEPARATOR);
			}
			if (newPath.length() == 0) {
				// we are on root
				newPath.append(PATH_SEPARATOR);
			}
			logger.debug("Absolutized relative path [" + path + "] from basepath [" + base + "] to ["
					+ newPath.toString() + "]");
			return newPath.toString();
		} else {
			return path;
		}
	}

	/**
	 * Walks the path. Dest is one of the codes (".", "..") or simple name, it
	 * should not contain PATH_SEPARATOR ("/")!
	 * 
	 * @param base
	 * @param dest
	 * @return
	 */
	public static String changePathLevel(final String base, String dest) {
		if (dest.contains(PATH_SEPARATOR)) {
			throw new IllegalArgumentException("The dest param should not contain [" + PATH_SEPARATOR + "]!");
		}
		if (PATH_SELF.equals(dest)) {
			return base;
		} else if (PATH_PARENT.equals(dest)) {
			StringBuffer newPath = new StringBuffer();
			String[] explodedPath = base.split(PATH_SEPARATOR);
			for (int i = 0; i < explodedPath.length - 1; i++) {
				if (explodedPath[i].length() > 0) {
					newPath.append(PATH_SEPARATOR);
					newPath.append(explodedPath[i]);
				}
			}
			if (newPath.length() == 0) {
				newPath.append(PATH_SEPARATOR);
			}
			return newPath.toString();
		} else {
			if (PATH_SEPARATOR.equals(base)) {
				// we are on root
				StringBuffer sb = new StringBuffer(base);
				sb.append(dest);
				return sb.toString();
			} else {
				StringBuffer sb = new StringBuffer(base);
				if (!dest.startsWith(PATH_SEPARATOR)) {
					sb.append(PATH_SEPARATOR);
				}
				sb.append(dest);
				return sb.toString();
			}
		}
	}

	/**
	 * Concatenates base and destPath.
	 * 
	 * @param base
	 * @param destPath
	 * @return
	 */
	public static String walkThePath(final String base, String destPath) {
		String currentBase = base;
		if (base.endsWith(PATH_SEPARATOR)) {
			currentBase = base.substring(0, base.length() - 1);
		}
		String[] explodedPath = destPath.split(PATH_SEPARATOR);
		for (int i = 0; i < explodedPath.length; i++) {
			if (explodedPath[i].length() > 0) {
				currentBase = changePathLevel(currentBase, explodedPath[i]);
			}
		}
		return currentBase;
	}

	/**
	 * Returns the file name in contained in the path. Simply gets the last
	 * String after the last PATH_SEPARATOR.
	 * 
	 * @param path
	 * @return
	 */
	public static String getFileName(String path) {
		while (path.endsWith(PATH_SEPARATOR)) {
			path = path.substring(0, path.length() - 1);
		}
		String[] explodedPath = path.split(PATH_SEPARATOR);
		if (explodedPath.length > 0 && explodedPath[explodedPath.length - 1].length() > 0) {
			return explodedPath[explodedPath.length - 1];
		} else {
			return PATH_SEPARATOR;
		}

	}
}