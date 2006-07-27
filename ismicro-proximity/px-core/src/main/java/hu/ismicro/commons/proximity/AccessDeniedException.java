package hu.ismicro.commons.proximity;

/**
 * Thrown when a Proximity request is not backed by trusted/granted authority.
 * 
 * @author t.cservenak
 * 
 */
public class AccessDeniedException extends ProximityException {

    /**
     * 
     */
    private static final long serialVersionUID = 8341250956517740603L;

    private ProximityRequest request;

    public AccessDeniedException(ProximityRequest request, String msg) {
        super(msg);
        this.request = request;
    }

    public String toString() {
        StringBuffer str = new StringBuffer();
        str.append("Access ");
        str.append("to resource ");
        str.append(request.getPath());
        str.append(" has been forbidden because:");
        str.append(super.toString());
        return str.toString();
    }

}
