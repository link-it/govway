/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2024 Link.it srl (https://link.it).
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License version 3, as published by
 * the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 */
package org.openspcoop2.pdd.timers.pdnd;

import java.util.ArrayList;
import java.util.List;

import org.openspcoop2.pdd.timers.TimerException;
import org.openspcoop2.utils.serialization.IDeserializer;
import org.openspcoop2.utils.serialization.SerializationConfig;
import org.openspcoop2.utils.serialization.SerializationFactory;
import org.openspcoop2.utils.serialization.SerializationFactory.SERIALIZATION_TYPE;

/**     
 * TimerGestoreChiaviPDNDEvents
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class TimerGestoreChiaviPDNDEvents {

	/** Esempi:
	 * Events:
      type: object
      properties:
        lastEventId:
          type: integer
          format: int64
        events:
          type: array
          items:
            $ref: '#/components/schemas/Event'
      required:
        - events
        
      {"events":[...]}
      Se non presenti: {"events":[]}
	*/
	// (type:integer format:int64) This value is also used to sort the events in chronological order
	private long lastEventId; 
	// events
	private List<TimerGestoreChiaviPDNDEvent> events = new ArrayList<>();
	
	
	public long getLastEventId() {
		return this.lastEventId;
	}
	public void setLastEventId(long lastEventId) {
		this.lastEventId = lastEventId;
	}
		  
	public List<TimerGestoreChiaviPDNDEvent> getEvents() {
		return this.events;
	}
	public void setEvents(List<TimerGestoreChiaviPDNDEvent> events) {
		this.events = events;
	}	
	public TimerGestoreChiaviPDNDEvents addEventsItem(TimerGestoreChiaviPDNDEvent eventsItem) {
		this.events.add(eventsItem);
		return this;
	}
	
	public static TimerGestoreChiaviPDNDEvents toEvents(String json) throws TimerException {
		try {
			IDeserializer jsonJacksonDeserializer = SerializationFactory.getDeserializer(SERIALIZATION_TYPE.JSON_JACKSON, new SerializationConfig());
			return (TimerGestoreChiaviPDNDEvents) jsonJacksonDeserializer.getObject(json, TimerGestoreChiaviPDNDEvents.class);
		}catch(Exception e) {
			throw new TimerException(e.getMessage(),e);
		}
	}
}
