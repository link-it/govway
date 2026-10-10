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

package org.openspcoop2.protocol.modipa.example.rest.pdnd_async;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Stato (in memoria) delle interazioni gestite dal server dell'e-service di esempio.
 * Le interazioni sono indicizzate per identificativo di collaborazione (interactionId PDND) fornito da GovWay.
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class InteractionStore {
	
	private InteractionStore() {}

	public static class Interaction {
		private final String id;
		private final String subject;
		private final int entities;
		private final String urlCallback;
		private volatile boolean confirmed = false;
		
		public Interaction(String id, String subject, int entities, String urlCallback) {
			this.id = id;
			this.subject = subject;
			this.entities = entities;
			this.urlCallback = urlCallback;
		}
		public String getId() {
			return this.id;
		}
		public String getSubject() {
			return this.subject;
		}
		public int getEntities() {
			return this.entities;
		}
		public String getUrlCallback() {
			return this.urlCallback;
		}
		public boolean isConfirmed() {
			return this.confirmed;
		}
		public void setConfirmed(boolean confirmed) {
			this.confirmed = confirmed;
		}
	}
	
	private static final Map<String, Interaction> INTERACTIONS = new ConcurrentHashMap<>();
	
	public static void put(Interaction interaction) {
		INTERACTIONS.put(interaction.getId(), interaction);
	}
	public static Interaction get(String id) {
		return id!=null ? INTERACTIONS.get(id) : null;
	}
}
