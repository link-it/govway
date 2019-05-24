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

package org.openspcoop2.pdd.core.dynamic;

import java.util.Properties;

import org.openspcoop2.pdd.core.PdDContext;
import org.openspcoop2.pdd.core.connettori.ConnettoreMsg;
import org.openspcoop2.protocol.sdk.Busta;
import org.w3c.dom.Element;

/**
 * DynamicInfo
 *
 * @author Andrea Poli (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class DynamicInfo {

	private PdDContext pddContext;
	private Busta busta;
	private Properties trasporto;
	private Properties queryParameters;
	
	// non disponibili nel connettore
	private String url;
	private Element xml;
	private String json;
	
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
	public PdDContext getPddContext() {
		return this.pddContext;
	}
	public void setPddContext(PdDContext pddContext) {
		this.pddContext = pddContext;
	}
	public Properties getTrasporto() {
		return this.trasporto;
	}
	public void setTrasporto(Properties trasporto) {
		this.trasporto = trasporto;
	}
	public Properties getQueryParameters() {
		return this.queryParameters;
	}
	public void setQueryParameters(Properties queryParameters) {
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
	
}
