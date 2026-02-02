/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2026 Link.it srl (https://link.it).
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

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * TimerGestoreChiaviPDNDEvent
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class TimerGestoreChiaviPDNDEvent {

	public static final String EVENT_TYPE_ADDED_V1 = "ADDED";
	public static final String EVENT_TYPE_DELETED_V1 = "DELETED";
	public static final String EVENT_TYPE_UPDATED_V1 = "UPDATED";

	public static final String OBJECT_TYPE_PURPOSE_V1 = "PURPOSE";
	public static final String OBJECT_TYPE_AGREEMENT_V1 = "AGREEMENT";
	public static final String OBJECT_TYPE_KEY_V1 = "KEY";

	/** Esempi v1:
	 *  required:
        - eventId
        - eventType
        - objectType
        - objectId
	  {"eventId":1,"eventType":"ADDED","objectId":{"kid":"78rnqOpnUf4iiT30mOXUsXm52X1_7J2gMXj2wxYuR-o"},"objectType":"KEY"}
	  {"eventId":25,"eventType":"DELETED","objectId":{"kid":"n1pcztDp9QbkulHy-ufBvTpJESFEbgNnjDjUfmt4FIA"},"objectType":"KEY"}
	 */
	
	public static final String EVENT_TYPE_ADDED_V2 = "CLIENT_KEY_ADDED";
	public static final String EVENT_TYPE_DELETED_V2 = "CLIENT_KEY_DELETED";
	
	/**
	 * Esempi v2:
	 * events*: [{
		id*: uuid
		eventType*: enum Allowed: CLIENT_KEY_ADDED┃CLIENT_KEY_DELETED
		eventTimestamp*: date-time
		kid*: string
		clientId*: uuid
		}]
	 * events": [
			{
			"id": "19c17b79-6050-4000-8567-ce2f17d15401",
			"eventType": "CLIENT_KEY_ADDED",
			"eventTimestamp": "1970-01-01T00:00:00.000Z",
			"kid": "string",
			"clientId": "19c17b79-6060-4000-846d-bce18948c801"
			}
			]
	 */
	
	// per V1 (type:integer format:int64) This value is also used to sort the events in chronological order
	// per V2 id*: uuid
	@JsonAlias("id")
	private String eventId; 
	
	// (type:string) Describes the kind of the event (e.g. ADDED, DELETED, UPDATED)
	// diversi valori a seconda di V1 e V2, riportati precedentemente
	private String eventType; 
	
	/** (type:undefined)  Contains the identifiers of the object involved in the event Examples:
    { "agreementId" :  "007523dc-7ec8-4ce4-9e29-d70bf4eda769" }
    { "kid" : "ajsdkjaskd_asjkdhaskdhj29_eueU" }
	 */
	// per v1 rappresenta il kid, per la v2 c'è il campo diretto
	private Map<String, String> objectId = new HashMap<>();
	
	// (type:string) Describes which object is involved in the event (e.g. PURPOSE, AGREEMENT, KEY)
	// presente solo in v1, nella v2 c'è una risorsa dedicata per le chiavi
	private String objectType;

	// Campi aggiuntivi per v2
	private String eventTimestamp;
	private String kid;
	private String clientId;


	public String getEventId() {
		return this.eventId;
	}
	public void setEventId(String eventId) {
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

	public String getEventTimestamp() {
		return this.eventTimestamp;
	}
	public void setEventTimestamp(String eventTimestamp) {
		this.eventTimestamp = eventTimestamp;
	}

	public String getKid() {
		return this.kid;
	}
	public void setKid(String kid) {
		this.kid = kid;
	}

	public String getClientId() {
		return this.clientId;
	}
	public void setClientId(String clientId) {
		this.clientId = clientId;
	}
}
