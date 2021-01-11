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

package org.openspcoop2.pdd.core.dynamic;

import java.util.Map;

import org.openspcoop2.message.OpenSPCoop2Message;
import org.openspcoop2.pdd.core.PdDContext;
import org.openspcoop2.pdd.core.connettori.ConnettoreMsg;
import org.openspcoop2.protocol.sdk.Busta;
import org.openspcoop2.protocol.sdk.Context;
import org.w3c.dom.Element;

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
	private Map<String, String> trasporto;
	private Map<String, String> queryParameters;
	
	// non disponibili nel connettore
	private String url;
	private Element xml;
	private String json;
	private OpenSPCoop2Message message;
	private ErrorHandler errorHandler;
	
	public DynamicInfo() {
		
	}
	public DynamicInfo(ConnettoreMsg connettoreMsg, PdDContext pddContext) {
		if(connettoreMsg!=null && connettoreMsg.getBusta()!=null) {
			this.busta = connettoreMsg.getBusta();
		}
		if(connettoreMsg!=null && 
				connettoreMsg.getPropertiesTrasporto()!=null && !connettoreMsg.getPropertiesTrasporto().isEmpty()) {
			this.trasporto =  connettoreMsg.getPropertiesTrasporto();
		}
		if(connettoreMsg!=null && 
				connettoreMsg.getPropertiesUrlBased()!=null && !connettoreMsg.getPropertiesUrlBased().isEmpty()) {
			this.queryParameters = connettoreMsg.getPropertiesUrlBased();
		}
		this.pddContext = pddContext;
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
	public Map<String, String> getTrasporto() {
		return this.trasporto;
	}
	public void setTrasporto(Map<String, String> trasporto) {
		this.trasporto = trasporto;
	}
	public Map<String, String> getQueryParameters() {
		return this.queryParameters;
	}
	public void setQueryParameters(Map<String, String> queryParameters) {
		this.queryParameters = queryParameters;
	}
	public String getUrl() {
		return this.url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public Element getXml() {
		return this.xml;
	}
	public void setXml(Element xml) {
		this.xml = xml;
	}
	public String getJson() {
		return this.json;
	}
	public void setJson(String json) {
		this.json = json;
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
