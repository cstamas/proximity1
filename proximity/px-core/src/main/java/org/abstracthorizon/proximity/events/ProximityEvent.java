/*

   Copyright 2005-2007 Tamas Cservenak (t.cservenak@gmail.com)

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.

 */
package org.abstracthorizon.proximity.events;

import java.util.Date;

// TODO: Auto-generated Javadoc
/**
 * The Class ProximityEvent.
 */
public abstract class ProximityEvent
{

    /** The event date. */
    private Date eventDate;

    /**
     * Instantiates a new proximity event.
     */
    public ProximityEvent()
    {
        super();
        this.eventDate = new Date();
    }

    /**
     * Gets the event date.
     * 
     * @return the event date
     */
    public Date getEventDate()
    {
        return eventDate;
    }

    /**
     * Sets the event date.
     * 
     * @param eventDate the new event date
     */
    public void setEventDate( Date eventDate )
    {
        this.eventDate = eventDate;
    }

}
