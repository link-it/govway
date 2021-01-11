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
/**
 * 
 */
package org.openspcoop2.protocol.as4.pmode.beans;

import org.w3c.dom.Node;

/**
 * @author Bussu Giovanni (bussu@link.it)
 * @author  $Author$
 * @version $Rev$, $Date$
 * 
 */
public class Payload {

	private String name;
	private String cid;
	private Boolean inBody;
	private Boolean required;
	private String schemaFile;
	private Long maxSize;
	private String mimeType;
	/**
	 * @param node
	 */
	public Payload(Node node) {
		this.name = node.getAttributes().getNamedItem("name").getNodeValue();
		this.cid = node.getAttributes().getNamedItem("cid").getNodeValue();
		this.inBody = node.getAttributes().getNamedItem("inBody")!=null ? 
				Boolean.parseBoolean(node.getAttributes().getNamedItem("inBody").getNodeValue()) : null;
		this.required = Boolean.parseBoolean(node.getAttributes().getNamedItem("required").getNodeValue());
		this.schemaFile = node.getAttributes().getNamedItem("schemaFile")!=null ? 
				node.getAttributes().getNamedItem("schemaFile").getNodeValue() : null;
		this.maxSize = node.getAttributes().getNamedItem("maxSize")!=null ?
				Long.parseLong(node.getAttributes().getNamedItem("maxSize").getNodeValue()) : null;
		this.mimeType = node.getAttributes().getNamedItem("mimeType")!=null ?
				node.getAttributes().getNamedItem("mimeType").getNodeValue() : null;
	}
	public String getName() {
		return this.name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getCid() {
		return this.cid;
	}
	public void setCid(String cid) {
		this.cid = cid;
	}
	public Boolean getInBody() {
		return this.inBody;
	}
	public void setInBody(Boolean inBody) {
		this.inBody = inBody;
	}
	public Boolean getRequired() {
		return this.required;
	}
	public void setRequired(Boolean required) {
		this.required = required;
	}
	public String getSchemaFile() {
		return this.schemaFile;
	}
	public void setSchemaFile(String schemaFile) {
		this.schemaFile = schemaFile;
	}
	public Long getMaxSize() {
		return this.maxSize;
	}
	public void setMaxSize(Long maxSize) {
		this.maxSize = maxSize;
	}
	public String getMimeType() {
		return this.mimeType;
	}
	public void setMimeType(String mimeType) {
		this.mimeType = mimeType;
	}
	
	@Override
	public boolean equals(Object obj) {
		
		if(obj == null)
			return false;
		
		if(!(obj instanceof Payload))
			return false;
		
		Payload p = (Payload) obj;
		
		if(!this.name.equals(p.getName()))
			return false;
		
		if(!this.cid.equals(p.getCid()))
			return false;
		
		if(this.inBody != null) {
			if(!this.inBody.equals(p.getInBody()))
			return false;
		} else {
			if(p.getInBody() != null)
				return false;
		}
		
		if(!this.required.equals(p.getRequired()))
			return false;
		
		if(this.schemaFile != null) {
			if(!this.schemaFile.equals(p.getSchemaFile()))
				return false;
		} else {
			if(p.getSchemaFile() != null)
				return false;
		}
		
		if(this.maxSize != null) {
			if(!this.maxSize.equals(p.getMaxSize()))
				return false;
		} else {
			if(p.getMaxSize() != null)
				return false;
		}
		
		if(this.mimeType != null) {
			if(!this.mimeType.equals(p.getMimeType()))
				return false;
		} else {
			if(p.getMimeType() != null)
				return false;
		}
			
		return true;
	}
}
