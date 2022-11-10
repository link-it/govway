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

package org.openspcoop2.pdd.core.dynamic;

import java.util.List;
import java.util.Map;

import org.openspcoop2.message.OpenSPCoop2Message;
import org.openspcoop2.pdd.core.PdDContext;
import org.openspcoop2.pdd.core.connettori.ConnettoreMsg;
import org.openspcoop2.protocol.sdk.Busta;
import org.openspcoop2.protocol.sdk.Context;
import org.openspcoop2.utils.transport.TransportRequestContext;
import org.openspcoop2.utils.transport.TransportUtils;

/**
 * DynamicInfo
 *
 * @author Andrea Poli (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class DynamicInfo {

	private Context pddContext;
	private Busta busta;
	private Map<String, List<String>> trasporto;
	private Map<String, List<String>> queryParameters;
	private Map<String, List<String>> formParameters;
	
	// non disponibili nel connettore
	private String url;
	private MessageContent messageContent;
	private OpenSPCoop2Message message;
	private ErrorHandler errorHandler;
	
	public DynamicInfo() {
		
	}
	public DynamicInfo(ConnettoreMsg connettoreMsg, PdDContext pddContext) {
		if(connettoreMsg!=null && connettoreMsg.getBusta()!=null) {
			this.busta = connettoreMsg.getBusta();
		}
		
		TransportRequestContext trasportRequestContext = null;
		if(connettoreMsg!=null) {
			trasportRequestContext = connettoreMsg.getTransportRequestContext();
		}

		if(trasportRequestContext!=null) {
			this.trasporto = trasportRequestContext.getHeaders();
		}
		if(connettoreMsg!=null && 
				connettoreMsg.getPropertiesTrasporto()!=null && !connettoreMsg.getPropertiesTrasporto().isEmpty()) {
			if(this.trasporto==null || this.trasporto.isEmpty()) {
				this.trasporto =  connettoreMsg.getPropertiesTrasporto();
			}
			else {
				Map<String, List<String>> mapNew = connettoreMsg.getPropertiesTrasporto();
				for (String name : mapNew.keySet()) {
					TransportUtils.removeObject(this.trasporto, name);
					this.trasporto.put(name, mapNew.get(name));
				}			
			}
		}
		
		if(trasportRequestContext!=null) {
			this.queryParameters = trasportRequestContext.getParameters();
		}
		if(connettoreMsg!=null && 
				connettoreMsg.getPropertiesUrlBased()!=null && !connettoreMsg.getPropertiesUrlBased().isEmpty()) {
			if(this.queryParameters==null || this.queryParameters.isEmpty()) {
				this.queryParameters =  connettoreMsg.getPropertiesUrlBased();
			}
			else {
				Map<String, List<String>> mapNew = connettoreMsg.getPropertiesUrlBased();
				for (String name : mapNew.keySet()) {
					TransportUtils.removeObject(this.queryParameters, name);
					this.queryParameters.put(name, mapNew.get(name));
				}			
			}
		}
		
		this.pddContext = pddContext;
		
		if(connettoreMsg!=null) {
			this.url = connettoreMsg.getUrlInvocazionePorta();
		}
	}
	
	public Busta getBusta() {
		return this.busta;
	}
	public void setBusta(Busta busta) {
		this.busta = busta;
	}
	public Context getPddContext() {
		return this.pddContext;
	}
	public void setPddContext(Context pddContext) {
		this.pddContext = pddContext;
	}
	public Map<String, List<String>> getHeaders() {
		return this.trasporto;
	}
	public void setHeaders(Map<String, List<String>> trasporto) {
		this.trasporto = trasporto;
	}
	public Map<String, List<String>> getParameters() {
		return this.queryParameters;
	}
	public void setParameters(Map<String, List<String>> queryParameters) {
		this.queryParameters = queryParameters;
	}
	public Map<String, List<String>> getFormParameters() {
		return this.formParameters;
	}
	public void setFormParameters(Map<String, List<String>> formParameters) {
		this.formParameters = formParameters;
	}
	public String getUrl() {
		return this.url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public MessageContent getMessageContent() {
		return this.messageContent;
	}
	public void setMessageContent(MessageContent messageContent) {
		this.messageContent = messageContent;
	}
	public OpenSPCoop2Message getMessage() {
		return this.message;
	}
	public void setMessage(OpenSPCoop2Message message) {
		this.message = message;
	}
	public ErrorHandler getErrorHandler() {
		return this.errorHandler;
	}
	public void setErrorHandler(ErrorHandler errorHandler) {
		this.errorHandler = errorHandler;
	}
	
}
