package org.abstracthorizon.proximity;

public abstract class ProximityException extends Exception {

	public ProximityException(String msg) {
		super(msg);
	}

	public ProximityException(String msg, Throwable cause) {
		super(msg, cause);
	}

}
