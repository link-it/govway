/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2023 Link.it srl (https://link.it). 
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



package org.openspcoop2.protocol.sdk;

import java.io.Serializable;

/**
 * Bean Contenente le informazioni per il root element di elementi xml
 * 
 * @author Andrea Poli (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 * 
 */
public abstract class XMLRootElement implements Serializable{

	private static final long serialVersionUID = -3157816024001587816L;

	private String localName;
	private String namespace;
	private String prefix;
	
	protected XMLRootElement(String localName,String namespace,String prefix){
		this.localName = localName;
		this.namespace = namespace;
		this.prefix = prefix;
	}
	protected XMLRootElement(String localName,String namespace){
		this(localName, namespace, null);
	}
	protected XMLRootElement(){}
	
	public String getLocalName() {
		return this.localName;
	}
	public void setLocalName(String localName) {
		this.localName = localName;
	}
	public String getNamespace() {
		return this.namespace;
	}
	public void setNamespace(String namespace) {
		this.namespace = namespace;
	}
	public String getPrefix() {
		return this.prefix;
	}
	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}

	public String getAsStringStartTag()throws ProtocolException{
		return this.getAsStringTag(true);
	}
	public String getAsStringEndTag()throws ProtocolException{
		return this.getAsStringTag(false);
	}
	private String getAsStringTag(boolean start) throws ProtocolException{
		
		if(this.localName==null || "".equals(this.localName)){
			throw new ProtocolException("LocalName not defined");
		}
		if(this.namespace==null || "".equals(this.namespace)){
			throw new ProtocolException("Namespace not defined");
		}
		
		StringBuilder bf = new StringBuilder();
		bf.append("<");
		if(!start){
			bf.append("/");
		}
		if(this.prefix!=null && !"".equals(this.prefix)){
			bf.append(this.prefix).append(":");
		}
		bf.append(this.localName);
		if(start){
			bf.append(" xmlns");
			if(this.prefix!=null && !"".equals(this.prefix)){
				bf.append(":").append(this.prefix);
			}
			bf.append("=\"").append(this.namespace).append("\"");
		}
		bf.append(">");
		return bf.toString();
	}
}


