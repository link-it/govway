/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2021 Link.it srl (https://link.it).
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
package org.openspcoop2.utils.logger.beans.context.core;

import java.io.Serializable;

import org.openspcoop2.utils.logger.IContext;

/**
 * AbstractContextWithClient
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public abstract class AbstractContextWithClient extends AbstractContext implements IContext,Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public AbstractContextWithClient(){}
	
	public Request getRequest() {
		if(this.transaction != null){
			AbstractTransactionWithClient tr = (AbstractTransactionWithClient) this.transaction;
			if(tr.getRequest() == null){
				tr.setRequest(new Request());
			}
			return tr.getRequest();
		}
		return null;
	}
	
	public Response getResponse() {
		if(this.transaction != null){
			AbstractTransactionWithClient tr = (AbstractTransactionWithClient) this.transaction;
			if(tr.getResponse() == null){
				tr.setResponse(new Response());
			}
			return tr.getResponse();
		}
		return null;
	}

}
