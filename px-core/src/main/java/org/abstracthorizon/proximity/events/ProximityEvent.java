package org.abstracthorizon.proximity.events;

import java.util.Date;

public abstract class ProximityEvent {
	
	private Date eventDate;
	
	public ProximityEvent() {
		super();
		this.eventDate = new Date();
	}

	public Date getEventDate() {
		return eventDate;
	}

	public void setEventDate(Date eventDate) {
		this.eventDate = eventDate;
	}

}
