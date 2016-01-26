/*
 * OpenSPCoop v2 - Customizable SOAP Message Broker 
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

import java.util.Enumeration;

import javax.servlet.http.HttpServletResponse;
import javax.xml.soap.Detail;

import org.openspcoop2.core.api.utils.Sbustamento;
import org.openspcoop2.message.Costanti;
import org.openspcoop2.message.OpenSPCoop2Message;
import org.openspcoop2.message.SoapUtils;
import org.openspcoop2.protocol.engine.constants.IDService;
import org.openspcoop2.protocol.sdk.IProtocolFactory;
import org.openspcoop2.protocol.sdk.builder.IErroreApplicativoBuilder;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * HttpServletConnectorOutMessage
 *
 * @author Andrea Poli (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ApiServletConnectorOutMessage extends HttpServletConnectorOutMessage {

	public ApiServletConnectorOutMessage(IProtocolFactory protocolFactory, HttpServletResponse res,
			IDService idModuloAsIDService, String idModulo) throws ConnectorException{
		super(protocolFactory,res,idModuloAsIDService,idModulo);
	}

	private int status = -1;
	
	@Override
	public void sendResponse(OpenSPCoop2Message msg, boolean consume) throws ConnectorException {
		try{
			
			if(msg.getSOAPBody()==null){
				throw new Exception("Message without body?");
			}
			if(msg.getSOAPBody().hasFault()){
				
				// errore generato dalla PdD
				byte [] xmlErrore = null;
				
				IErroreApplicativoBuilder erroreApplicativoBuilder = this.protocolFactory.createErroreApplicativoBuilder();
				Detail d = msg.getSOAPBody().getFault().getDetail();
				if(d!=null){
					NodeList list = d.getChildNodes();
					if(list!=null){
						for (int i = 0; i < list.getLength(); i++) {
							Node n = list.item(i);
							if(erroreApplicativoBuilder.isErroreApplicativo(n)){
								xmlErrore = msg.getAsByte(n, false);
								break;
							}
						}
					}
				}
				
				if(xmlErrore==null){
					xmlErrore = SoapUtils.sbustamentoMessaggio(msg);
				}
				this.res.setStatus(500);
				this.status = 500;
				this.res.setHeader(Costanti.CONTENT_TYPE,Costanti.CONTENT_TYPE_SOAP_1_1);
				this.res.setHeader(Costanti.CONTENT_LENGTH,xmlErrore.length+"");
				this.out.write(xmlErrore);
				
			}
			else{
			
				Sbustamento sbustamento = new Sbustamento(msg);
				
				if(sbustamento.getResponseMessage()!=null){
					// DEPRECATO this.res.setStatus(sbustamento.getResponseStatus(), sbustamento.getResponseMessage());
					this.res.setStatus(sbustamento.getResponseStatus());
					this.status = sbustamento.getResponseStatus();
				}
				else{
					this.res.setStatus(sbustamento.getResponseStatus());
					this.status = sbustamento.getResponseStatus();
				}
				
				if(sbustamento.getTransportProperties()!=null && sbustamento.getTransportProperties().size()>0){
					Enumeration<?> keys = sbustamento.getTransportProperties().keys();
					while (keys.hasMoreElements()) {
						String key = (String) keys.nextElement();
						this.res.setHeader(key,sbustamento.getTransportProperties().getProperty(key));
					}
				}
				
				if(sbustamento.getBody()!=null){
					String contentType = sbustamento.getContentType();
					this.res.setHeader(Costanti.CONTENT_TYPE,contentType);
					this.res.setHeader(Costanti.CONTENT_LENGTH,sbustamento.getBodyLength()+"");
					this.out.write(sbustamento.getBody());
				}
				else{
					this.res.setHeader(Costanti.CONTENT_LENGTH,"0");
				}
				
			}
			
		}catch(Exception e){
			throw new ConnectorException(e.getMessage(),e);
		}
	}
	
	@Override
	public void sendResponse(byte[] message) throws ConnectorException{
		throw new ConnectorException("Not Implemented");
	}
	
	@Override
	public void setHeader(String key,String value) throws ConnectorException{
		if(Costanti.CONTENT_TYPE.equals(key)){
			return; // non deve essere impostato
		}
		if(Costanti.CONTENT_LENGTH.equals(key)){
			return; // non deve essere impostato
		}
		if(Costanti.TRANSFER_ENCODING.equals(key)){
			return; // non deve essere impostato
		}
		super.setHeader(key, value);
	}
	
	@Override
	public void setContentLength(int length) throws ConnectorException{
		// nop
	}
	
	@Override
	public void setContentType(String type) throws ConnectorException{
		// nop;
	}
	
	@Override
	public void setStatus(int status) throws ConnectorException{
		// nop
	}
	@Override
	public int getResponseStatus() throws ConnectorException{
		return this.status;
	}

}
