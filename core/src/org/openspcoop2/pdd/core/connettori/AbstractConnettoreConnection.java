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

package org.openspcoop2.pdd.core.connettori;

import java.util.Date;

import org.openspcoop2.utils.date.DateManager;

/**
 * AbstractConnettoreConnection
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public abstract class AbstractConnettoreConnection<T> {

	private Date created = null;	
	private Date updated = null;	
	protected T httpclient;
	private boolean expired = false;
	protected String key = null;
	protected int expireUnusedAfterSeconds = -1;
	protected int closeUnusedAfterSeconds = -1;
	
	protected AbstractConnettoreConnection(String key, T client,
			int expireUnusedAfterSeconds, int closeUnusedAfterSeconds) {
		this.created = DateManager.getDate();
		this.updated = DateManager.getDate();
		this.key = key;
		this.httpclient = client;
		this.expireUnusedAfterSeconds = expireUnusedAfterSeconds;
		this.closeUnusedAfterSeconds = closeUnusedAfterSeconds;
	}
	
	public abstract String getStatus();
	public abstract void close() throws ConnettoreException;
	
	public T getHttpclient() {
		return this.httpclient;
	}
	
	public void refresh() {
		if(this.expired) {
			return;
		}
		if(this.updated==null) {
			this.updated = DateManager.getDate();
			return;
		}
		this.checkExpire();
		if(this.expired) {
			return;
		}
		this.updated = DateManager.getDate();
	}
	
	public void checkExpire() {
		int seconds = this.expireUnusedAfterSeconds;
		int ms = seconds * 1000;
		long diff = DateManager.getTimeMillis() - this.updated.getTime();
		if(diff > ms) {
			/**System.out.println("Connessione '"+this.key+"' scaduta");*/
			this.expired = true;
		}
	}
	
	public boolean isReadyForClose() {
		if(!this.expired) {
			return false;
		}
		int seconds = this.closeUnusedAfterSeconds;
		int ms = seconds * 1000;
		long diff = DateManager.getTimeMillis() - this.updated.getTime();
		/**if(diff > ms) {
			System.out.println("Connessione '"+this.key+"' pronta per essere chiusa");*/
		return diff > ms;
	}
	
	public Date getCreated() {
		return this.created;
	}

	public Date getUpdated() {
		return this.updated;
	}
	
	public boolean isExpired() {
		return this.expired;
	}
}
