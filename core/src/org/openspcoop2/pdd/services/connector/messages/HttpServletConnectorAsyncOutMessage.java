/*
 * GovWay - A customizable API Gateway 
 * http://www.govway.org
 *
 * from the Link.it OpenSPCoop project codebase
 * 
 * Copyright (c) 2005-2019 Link.it srl (http://link.it). 
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
package org.openspcoop2.pdd.services.connector.messages;

import javax.servlet.AsyncContext;
import javax.servlet.http.HttpServletResponse;

import org.openspcoop2.pdd.services.connector.ConnectorException;
import org.openspcoop2.protocol.engine.constants.IDService;
import org.openspcoop2.protocol.sdk.IProtocolFactory;

/**
 * HttpServletConnectorOutMessage
 *
 * @author Andrea Poli (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class HttpServletConnectorAsyncOutMessage extends HttpServletConnectorOutMessage {

	protected AsyncContext _ac;	
	
	public HttpServletConnectorAsyncOutMessage(IProtocolFactory<?> protocolFactory, AsyncContext ac,
			IDService idModuloAsIDService, String idModulo) throws ConnectorException{
		super(protocolFactory, null, idModuloAsIDService, idModulo);
		this._ac = ac;
	}

	private HttpServletResponse _res;
	private synchronized void initHttpServletResponse() {
		if(this._res==null) {
			this._res = (HttpServletResponse) this._ac.getResponse();
		}
	}
	@Override
	protected HttpServletResponse getHttpServletResponse() {
		if(this._res==null) {
			this.initHttpServletResponse();
		}
		return this._res;
	}

	private Throwable nioException;
	public void setNioException(Throwable nioException) {
		this.nioException = nioException;
	}
	
	@Override
	public void flush(boolean throwException) throws ConnectorException {
	
		if(this.nioException!=null && throwException) {
			throw new ConnectorException(this.nioException.getMessage(),this.nioException);
		}
		
		super.flush(throwException);
	}
	
	@Override
	public void close(boolean throwException) throws ConnectorException{
		
		super.close(throwException);
		
		try{
			if(this._ac!=null){
				try{
					this._ac.complete();
				}catch(Exception e){
					if(throwException){
						throw e;
					}
				}
			}
		}catch(Exception e){
			throw new ConnectorException(e.getMessage(),e);
		}	
	}
}
