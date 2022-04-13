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
package org.openspcoop2.protocol.sdk.state;

import org.slf4j.Logger;

/**
 * RequestThreadContext
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class RequestThreadContext {

	public static Logger log = null;
	private static final ThreadLocal<RequestThreadContext> transactionContext_threadLocal =  new ThreadLocal<RequestThreadContext>() {
		 @Override
		 protected RequestThreadContext initialValue() {
			 String tName = Thread.currentThread().getName();
			 return new RequestThreadContext(tName, log);
		 }
	};
	
	public static RequestThreadContext getRequestThreadContext() {
		return transactionContext_threadLocal.get();
	}
	
	
	public RequestThreadContext(String tName, Logger log) {
		this.tName = tName;
		if(log!=null) {
			log.debug("ThreadLocal request context created for thread '"+tName+"'");
		}
	}
	
	private String tName;
	
	private RequestFruitore requestFruitoreInfo;


	public RequestFruitore getRequestFruitoreInfo() {
		return this.requestFruitoreInfo;
	}
	public void setRequestFruitoreInfo(RequestFruitore requestFruitoreInfo) {
		this.requestFruitoreInfo = requestFruitoreInfo;
	}


	public String gettName() {
		return this.tName;
	}

	public void clear() {
		this.requestFruitoreInfo = null;
	}

}
