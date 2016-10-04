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
package org.openspcoop2.pdd.services.connector;

import java.io.ByteArrayOutputStream;

import javax.servlet.http.HttpServletRequest;

import org.openspcoop2.core.api.constants.CostantiApi;
import org.openspcoop2.core.api.constants.MethodType;
import org.openspcoop2.core.api.utils.Imbustamento;
import org.openspcoop2.message.OpenSPCoop2MessageFactory;
import org.openspcoop2.message.OpenSPCoop2MessageParseResult;
import org.openspcoop2.message.SOAPVersion;
import org.openspcoop2.pdd.services.ServletUtils;
import org.openspcoop2.protocol.engine.URLProtocolContext;
import org.openspcoop2.protocol.engine.constants.IDService;
import org.openspcoop2.utils.io.notifier.NotifierInputStreamParams;

/**
 * HttpServletConnectorInMessage
 *
 * @author Andrea Poli (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ApiServletConnectorInMessage extends HttpServletConnectorInMessage {

	public static OpenSPCoop2MessageFactory factory = OpenSPCoop2MessageFactory.getMessageFactory();
	

	
	public ApiServletConnectorInMessage(HttpServletRequest req,IDService idModuloAsIDService,String idModulo) throws ConnectorException{
		super(req, idModuloAsIDService, idModulo);
	}

	private synchronized void initMessage(NotifierInputStreamParams notifierInputStreamParams) throws ConnectorException{
		if(this.message==null){
			
			try{
			
				// SOAPVersione: utilizzo la prima compatibile con il protocollo
				SOAPVersion soapVersion = null;
				if(this.getProtocolFactory().createProtocolConfiguration().isSupportoSOAP11()){
					soapVersion = SOAPVersion.SOAP11;
				}
				else if(this.getProtocolFactory().createProtocolConfiguration().isSupportoSOAP12()){
					soapVersion = SOAPVersion.SOAP12;
				}
				else {
					// Nessuna versione del protocollo SOAP supportata?? forzo 11
					// in effetti potrebbe esistere un protocollo che supporta solo api.
					soapVersion = SOAPVersion.SOAP11;
					
				}
				
				String contentType = ServletUtils.readContentTypeFromHeader(this.req,false);
				
				URLProtocolContext urlProtocolContext = this.getURLProtocolContext();
				Imbustamento imbustamentoAPI = new Imbustamento(soapVersion, notifierInputStreamParams, this.is, MethodType.toEnumConstant(this.req.getMethod()), 
						contentType, urlProtocolContext.getParametersTrasporto(), urlProtocolContext.getParametersFormBased(), 
						urlProtocolContext.getFunctionParameters(), this.openspcoopProperties.getAPIServicesWhiteListRequestHeaderList());
				
				this.message = imbustamentoAPI.getMessage();
				
			}catch(Exception e){
				throw new ConnectorException(e.getMessage(),e);
			}
		}
	}
	

	@Override
	public String getContentType() throws ConnectorException{
		try{
			if(this.message==null){
				this.initMessage(null);
			}
			return this.message.getContentType();
		}catch(Exception e){
			throw new ConnectorException(e.getMessage(),e);
		}
	}
	
	@Override
	public String getSOAPAction(SOAPVersion versioneSoap, String contentType) throws ConnectorException{
		return CostantiApi.SOAP_ACTION_API;
	}
	
	@Override
	public OpenSPCoop2MessageParseResult getRequest(NotifierInputStreamParams notifierInputStreamParams, String contentType) throws ConnectorException{
		try{
			if(this.message==null){
				this.initMessage(null);
			}
			OpenSPCoop2MessageParseResult pr = new OpenSPCoop2MessageParseResult();
			pr.setMessage(this.message);
			return pr;
		}catch(Exception e){
			throw new ConnectorException(e.getMessage(),e);
		}	
	}
	
	@Override
	public byte[] getRequest() throws ConnectorException{
		try{
			if(this.message==null){
				this.initMessage(null);
			}
			ByteArrayOutputStream bout = new ByteArrayOutputStream();
			this.message.writeTo(bout, true);
			bout.flush();
			bout.close();
			return bout.toByteArray();
		}catch(Exception e){
			throw new ConnectorException(e.getMessage(),e);
		}
	}
	
	
}
