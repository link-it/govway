/*
 * OpenSPCoop - Customizable API Gateway 
 * http://www.openspcoop2.org
 * 
 * Copyright (c) 2005-2016 Link.it srl (http://link.it).
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
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
package org.openspcoop2.utils.logger.beans.proxy;

import java.io.Serializable;

import org.openspcoop2.utils.logger.IContext;

/**
 * ProxyContext
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ProxyContext implements IContext,Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	protected String idTransaction;
		
	protected Transaction transaction;
	
	protected Request request;
	
	protected Response response;
		
	public ProxyContext(){}
	
	@Override
	public String getIdTransaction(){
		return this.idTransaction;
	}
	
	public void setIdTransaction(String idTransaction) {
		this.idTransaction = idTransaction;
	}
	
	public Transaction getTransaction() {
		if(this.transaction == null){
			this.transaction = new Transaction();
		}
		return this.transaction;
	}

	public Request getRequest() {
		if(this.request == null){
			this.request = new Request();
		}
		return this.request;
	}

	public Response getResponse() {
		if(this.response == null){
			this.response = new Response();
		}
		return this.response;
	}
}
