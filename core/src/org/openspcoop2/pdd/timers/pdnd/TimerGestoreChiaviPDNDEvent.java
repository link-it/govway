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

import java.util.HashMap;
import java.util.Map;

/**     
 * TimerGestoreChiaviPDNDEvent
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class TimerGestoreChiaviPDNDEvent {

	public static final String EVENT_TYPE_ADDED = "ADDED";
	public static final String EVENT_TYPE_DELETED = "DELETED";
	public static final String EVENT_TYPE_UPDATED = "UPDATED";

	public static final String OBJECT_TYPE_PURPOSE = "PURPOSE";
	public static final String OBJECT_TYPE_AGREEMENT = "AGREEMENT";
	public static final String OBJECT_TYPE_KEY = "KEY";

	/** Esempi:
	 *  required:
        - eventId
        - eventType
        - objectType
        - objectId
	  {"eventId":1,"eventType":"ADDED","objectId":{"kid":"78rnqOpnUf4iiT30mOXUsXm52X1_7J2gMXj2wxYuR-o"},"objectType":"KEY"}
	  {"eventId":25,"eventType":"DELETED","objectId":{"kid":"n1pcztDp9QbkulHy-ufBvTpJESFEbgNnjDjUfmt4FIA"},"objectType":"KEY"}
	 */
	// (type:integer format:int64) This value is also used to sort the events in chronological order
	private long eventId; 
	// (type:string) Describes the kind of the event (e.g. ADDED, DELETED, UPDATED)
	private String eventType; 
	/** (type:undefined)  Contains the identifiers of the object involved in the event Examples:
    { "agreementId" :  "007523dc-7ec8-4ce4-9e29-d70bf4eda769" }
    { "kid" : "ajsdkjaskd_asjkdhaskdhj29_eueU" }
	 */
	private Map<String, String> objectId = new HashMap<>();
	// (type:string) Describes which object is involved in the event (e.g. PURPOSE, AGREEMENT, KEY)
	private String objectType; 



	public long getEventId() {
		return this.eventId;
	}
	public void setEventId(long eventId) {
		this.eventId = eventId;
	}

	public String getEventType() {
		return this.eventType;
	}
	public void setEventType(String eventType) {
		this.eventType = eventType;
	}

	public Map<String, String> getObjectId() {
		return this.objectId;
	}
	public void setObjectId(Map<String, String> objectId) {
		this.objectId = objectId;
	}
	public TimerGestoreChiaviPDNDEvent putObjectIdItem(String key, String objectIdItem) {
		this.objectId.put(key, objectIdItem);
		return this;
	}

	public String getObjectType() {
		return this.objectType;
	}
	public void setObjectType(String objectType) {
		this.objectType = objectType;
	}
}
