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

package org.openspcoop2.core.api.utils;

import java.io.IOException;
import java.util.Properties;
import java.util.Vector;

import javax.xml.soap.AttachmentPart;
import javax.xml.soap.SOAPException;

import org.openspcoop2.core.api.HeaderParameter;
import org.openspcoop2.core.api.Invocation;
import org.openspcoop2.core.api.UrlParameter;
import org.openspcoop2.core.api.constants.CostantiApi;
import org.openspcoop2.core.api.constants.MessageType;
import org.openspcoop2.message.OpenSPCoop2Message;
import org.openspcoop2.message.OpenSPCoop2MessageException;
import org.openspcoop2.message.SoapUtils;
import org.openspcoop2.message.XMLUtils;
import org.openspcoop2.utils.resources.HttpUtilities;
import org.openspcoop2.utils.resources.TransportUtils;
import org.w3c.dom.Node;

/**
 * Sbustamento
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class Sbustamento {

	private OpenSPCoop2Message message;
	private Invocation apiObject;
	private byte[] body;
	private String nomePorta;
	
	public Sbustamento(OpenSPCoop2Message message) throws OpenSPCoop2MessageException{
		this.message = message;
		
		try{
			if(this.message==null){
				throw new OpenSPCoop2MessageException("Message not defined");
			}
			if(this.message.getSOAPBody()==null){
				throw new OpenSPCoop2MessageException("Message (SOAPBody) not defined");
			}
			Vector<Node> childs = SoapUtils.getNotEmptyChildNodes(this.message.getSOAPBody(),false);
			if(childs==null || childs.size()<=0){
				throw new OpenSPCoop2MessageException("Message (SOAPBody) empty");
			}
			if(childs.size()>1){
				throw new OpenSPCoop2MessageException("Message (SOAPBody) uncorrect (size:"+childs.size()+")");
			}
			Node child = childs.get(0);
			
			try{
				org.openspcoop2.core.api.utils.serializer.JaxbDeserializer deserializer = new org.openspcoop2.core.api.utils.serializer.JaxbDeserializer();
				byte[] childAsByte = XMLUtils.getInstance().toByteArray(child,true);
				this.apiObject = deserializer.readInvocation(childAsByte);
			}catch(Exception e){
				throw new OpenSPCoop2MessageException("Message Parsing error: "+e.getMessage(),e);
			}
			
			int countAttach = this.message.countAttachments(); 
			if(countAttach>0){
				if(countAttach>1){
					throw new OpenSPCoop2MessageException("Message (Attachments) uncorrect (size:"+countAttach+")");
				}
				
				// Verifica se il metodo http lo consente
				// Faccio sollevare l'eccezione durante la fase di sbustamento per far produrre l'errore applicativo ritornato al client
				boolean isRichiesta = MessageType.REQUEST.equals(this.apiObject.getResource().getType());
				if(HttpUtilities.isHttpBodyPermitted(isRichiesta, this.apiObject.getResource().getMethod().name(), this.apiObject.getResource().getMediaType())==false){						
					if(isRichiesta)
						throw new OpenSPCoop2MessageException("Http don't support body in "+this.apiObject.getResource().getMethod().name()+" request method");
					else
						throw new OpenSPCoop2MessageException("Http don't support body in "+this.apiObject.getResource().getMethod().name()+" response method");
				}
				
				AttachmentPart ap = (AttachmentPart) this.message.getAttachments().next();
				this.body = this.readAttachment(ap);
			}
			
			Object nome = this.message.getContextProperty(CostantiApi.NOME_PORTA);
			if(nome!=null){
				this.nomePorta = (String) nome;
			}
				
		}catch(OpenSPCoop2MessageException e){
			throw e;
		}catch(Exception e){
			throw new OpenSPCoop2MessageException("Message reader error: "+e.getMessage(),e);
		}
	}
	
	private byte[] readAttachment(AttachmentPart ap) throws SOAPException,IOException{
		javax.activation.DataHandler dh= ap.getDataHandler();  
    	java.io.InputStream inputDH = dh.getInputStream();
		java.io.ByteArrayOutputStream bout = new java.io.ByteArrayOutputStream();
    	byte [] readB = new byte[8192];
		int readByte = 0;
		while((readByte = inputDH.read(readB))!= -1)
			bout.write(readB,0,readByte);
		inputDH.close();
		bout.flush();
		bout.close();
		return bout.toByteArray();
	}
	
	public byte[] getBody() {
		return this.body;
	}
	public int getBodyLength() {
		if(this.body!=null){
			return this.body.length;
		}
		return 0;
	}
	
	public String buildUrl(String url,Properties propertiesUrlBased){
		
		String baseUrl = url;
		String parameterOriginalUrl = null;
		if(url.contains("?")){
			baseUrl = url.split("\\?")[0];
			if(baseUrl!=null){
				baseUrl.trim();
			}
			parameterOriginalUrl = url.split("\\?")[1]; 
			if(parameterOriginalUrl!=null){
				parameterOriginalUrl.trim();
			}
		}
		
		StringBuffer newUrl = new StringBuffer();
		newUrl.append(baseUrl);
		
		String resourcePath = this.apiObject.getResource().getPath();
		if(resourcePath!=null){
			
			String resourcePathBuild = new String(resourcePath);  
			if(resourcePathBuild.startsWith("/")){
				resourcePathBuild = resourcePathBuild.substring(1);
			}
			if(this.nomePorta!=null && resourcePathBuild.startsWith(this.nomePorta)){
				resourcePathBuild = resourcePathBuild.substring(this.nomePorta.length());
			}
			if(resourcePathBuild.startsWith("/")){
				resourcePathBuild = resourcePathBuild.substring(1);
			}
			
			if(baseUrl.endsWith("/")==false && resourcePathBuild!=null && !"".equals(resourcePathBuild)){
				newUrl.append("/");
			}
			newUrl.append(resourcePathBuild);
			
		}
		
		Properties parameters = null;
		if(
			(propertiesUrlBased!=null && propertiesUrlBased.size()>0) 
				|| 
			(this.apiObject.getUrlParameters()!=null && this.apiObject.getUrlParameters().sizeUrlParameterList()>0)
		){
			parameters = new Properties();
			if( (propertiesUrlBased!=null && propertiesUrlBased.size()>0) ){
				parameters.putAll(propertiesUrlBased);
			}
			if(this.apiObject.getUrlParameters()!=null && this.apiObject.getUrlParameters().sizeUrlParameterList()>0){
				for (UrlParameter urlParameter : this.apiObject.getUrlParameters().getUrlParameterList()) {
					if(urlParameter.getNome()==null || urlParameter.getBase()==null){
//						if(urlParameter.getNome()==null){
//							System.out.println("PARAMETRO NOME IS NULL");
//						}
//						if(urlParameter.getBase()==null){
//							System.out.println("PARAMETRO ["+urlParameter.getNome()+"] CON VALUE NULL");
//						}
						continue;
					}
					parameters.put(urlParameter.getNome(), urlParameter.getBase());
				}
			}
		}
		
		if(parameterOriginalUrl!=null){
			String [] split = parameterOriginalUrl.split("&");
			if(split!=null){
				if(parameters==null){
					parameters = new Properties();
				}
				for (int i = 0; i < split.length; i++) {
					if(split[i].contains("=")){
						String [] splitNomeValore = split[i].split("=");
						if(splitNomeValore!=null){
							String nome = splitNomeValore[0];
							if(nome!=null){
								nome = nome.trim();
							}
							String valore = splitNomeValore[1];
							if(valore!=null){
								valore = valore.trim();
							}
							if(nome!=null && valore!=null){
								parameters.put(nome,valore);
							}
						}
					}
				}
			}
		}
		
		return TransportUtils.buildLocationWithURLBasedParameter(parameters, newUrl.toString());
		
	}
	
	public String getContentType(){
		return this.apiObject.getResource().getMediaType();
	}
	
	public Properties getTransportProperties(){
		Properties parameters = null;
		if(this.apiObject.getHeaderParameters()!=null && this.apiObject.getHeaderParameters().sizeHeaderParameterList()>0){
			parameters = new Properties();
		}
		if(this.apiObject.getHeaderParameters()!=null && this.apiObject.getHeaderParameters().sizeHeaderParameterList()>0){
			for (HeaderParameter headerParameter : this.apiObject.getHeaderParameters().getHeaderParameterList()) {
				parameters.put(headerParameter.getNome(), headerParameter.getBase());
			}
		}
		return parameters;
	}
	
	public String getHttpMethod(){
		return this.apiObject.getResource().getMethod().getValue();
	}
	
	public int getResponseStatus(){
		return this.apiObject.getResource().getResponseStatus();
	}
	
	public String getResponseMessage(){
		return this.apiObject.getResource().getResponseMessage();
	}
}

