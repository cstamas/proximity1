package org.abstracthorizon.proximity.indexer;

import org.abstracthorizon.proximity.ProximityException;

public class IndexerException extends ProximityException {

	private static final long serialVersionUID = -834393066001736350L;

	public IndexerException(String msg) {
		super(msg);
	}

	public IndexerException(String msg, Throwable thr) {
		super(msg, thr);
	}

}
