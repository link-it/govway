/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2022 Link.it srl (https://link.it). 
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

package org.openspcoop2.pdd.core.connettori.nio;

import java.util.Date;

import org.openspcoop2.pdd.config.OpenSPCoop2Properties;
import org.openspcoop2.pdd.core.connettori.ConnettoreException;
import org.openspcoop2.utils.date.DateManager;

/**
 * AbstractConnettoreHTTPCORE_connection
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public abstract class AbstractConnettoreHTTPCORE_connection<T> {

	private Date created = null;	
	private Date updated = null;	
	protected T httpclient;
	private boolean expired = false;
	@SuppressWarnings("unused")
	private String key = null;
	
	public AbstractConnettoreHTTPCORE_connection(String key, T client) {
		this.created = DateManager.getDate();
		this.updated = DateManager.getDate();
		this.key = key;
		this.httpclient = client;
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
		int seconds = OpenSPCoop2Properties.getInstance().getNIOConfig_asyncClient_expireUnusedAfterSeconds();
		int ms = seconds * 1000;
		long diff = DateManager.getTimeMillis() - this.updated.getTime();
		if(diff > ms) {
			//System.out.println("Connessione '"+this.key+"' scaduta");
			this.expired = true;
		}
	}
	
	public boolean isReadyForClose() {
		if(!this.expired) {
			return false;
		}
		int seconds = OpenSPCoop2Properties.getInstance().getNIOConfig_asyncClient_closeUnusedAfterSeconds();
		int ms = seconds * 1000;
		long diff = DateManager.getTimeMillis() - this.updated.getTime();
		if(diff > ms) {
			//System.out.println("Connessione '"+this.key+"' pronta per essere chiusa");
			return true;
		}
		return false;
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
