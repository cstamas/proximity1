package hu.ismicro.commons.proximity.webapp.view;

import java.text.DecimalFormat;

/**
 * A stupid little class to format file sizes in "human readable" form. Used in
 * Proximity webapp as part of Velocity toolbox.
 * 
 * @author cstamas
 * 
 */
public class FormatFileSize {

    private DecimalFormat formatInteger = new DecimalFormat("0");

    private DecimalFormat formatDecimals = new DecimalFormat("0.00");

    private String BytesSuffix = "Byte";

    private String kBytesSuffix = "KB";

    private String mBytesSuffix = "MB";

    private String gBytesSuffix = "GB";

    private String tBytesSuffix = "TB";

    public double base = 1024;

    public FormatFileSize() {
        // do nothing
    }

    public String getFileSizeAsString(Object sizeInBytesObj) {
        double sizeLong = 0;
        double base = getBase();
        String suffix = "";
        try {
            sizeLong = Double.parseDouble(sizeInBytesObj.toString());
        } catch (NullPointerException e) {
            return null;
        } catch (NumberFormatException e) {
            return sizeInBytesObj.toString();
        }
        if (Math.rint(sizeLong / base) == 0) {
            // we do not have decimals
            return formatInteger.format(sizeLong) + " " + getBytesSuffix();
        } else if (Math.rint(sizeLong / (base * base)) == 0) {
            suffix = getKBytesSuffix();
            sizeLong = sizeLong / base;
        } else if (Math.rint(sizeLong / (base * base * base)) == 0) {
            suffix = getMBytesSuffix();
            sizeLong = sizeLong / (base * base);
        } else if (Math.rint(sizeLong / (base * base * base * base)) == 0) {
            suffix = getGBytesSuffix();
            sizeLong = sizeLong / (base * base * base);
        } else if (Math.rint(sizeLong / (base * base * base * base * base)) == 0) {
            suffix = getTBytesSuffix();
            sizeLong = sizeLong / (base * base * base * base);
        }
        return formatDecimals.format(sizeLong) + " " + suffix;
    }

    public double getBase() {
        return base;
    }

    public void setBase(double base) {
        this.base = base;
    }

    public String getBytesSuffix() {
        return BytesSuffix;
    }

    public void setBytesSuffix(String bytesSuffix) {
        BytesSuffix = bytesSuffix;
    }

    public String getGBytesSuffix() {
        return gBytesSuffix;
    }

    public void setGBytesSuffix(String bytesSuffix) {
        gBytesSuffix = bytesSuffix;
    }

    public String getKBytesSuffix() {
        return kBytesSuffix;
    }

    public void setKBytesSuffix(String bytesSuffix) {
        kBytesSuffix = bytesSuffix;
    }

    public String getMBytesSuffix() {
        return mBytesSuffix;
    }

    public void setMBytesSuffix(String bytesSuffix) {
        mBytesSuffix = bytesSuffix;
    }

    public String getTBytesSuffix() {
        return tBytesSuffix;
    }

    public void setTBytesSuffix(String bytesSuffix) {
        tBytesSuffix = bytesSuffix;
    }

}
