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
package org.openspcoop2.utils.logger.beans;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.openspcoop2.utils.logger.constants.MessageType;

/**
 * Message
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class Message implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private MessageType type;
	
	private String contentType;
	
	private byte[] content;
	
	private String idTransaction;
	
	private List<Attachment> attachments = new ArrayList<Attachment>();
	
	private List<String> _headers_position = new ArrayList<String>();
	private Map<String,Property> headers = new java.util.Hashtable<String,Property>();
	
	private List<String> _resources_position = new ArrayList<String>();
	private Map<String,Property> resources = new java.util.Hashtable<String,Property>();
	
	
	public String getContentType() {
		return this.contentType;
	}

	public void setContentType(String contentType) {
		this.contentType = contentType;
	}

	public byte[] getContent() {
		return this.content;
	}

	public void setContent(byte[] content) {
		this.content = content;
	}
	
	public List<Attachment> getAttachments() {
		return this.attachments;
	}
	
	public void addAttachment(Attachment attachment){
		this.attachments.add(attachment);
	}
	
	public Attachment getAttachment(int index){
		return this.attachments.get(index);
	}
	
	public Attachment removeAttachment(int index){
		return this.attachments.remove(index);
	}
	
	public int sizeAttachments(){
		return this.attachments.size();
	}
	
	public void clearAttachments(){
		this.attachments.clear();
	}
	
	
	public Map<String,Property> getHeaders() {
		return this.headers;
	}
	public List<Property> getHeadersAsList() {
		List<Property> l = new ArrayList<Property>();
		for (String key : this._headers_position) {
			l.add(this.headers.get(key));
		}
		return l;
	}
	
	public void addHeader(Property property){
		this.headers.put(property.getName(),property);
		this._headers_position.add(property.getName());
	}
	
	public Property getHeader(String key){
		return this.headers.get(key);
	}
	
	public Property removeHeader(String key){
		int index = -1;
		for (int i = 0; i < this._headers_position.size(); i++) {
			if(key.equals(this._headers_position.get(i))){
				index = i;
				break;
			}
		}
		this._headers_position.remove(index);
		return this.headers.remove(key);
	}
	
	public Property getHeader(int index){
		return this.getHeadersAsList().get(index);
	}
	
	public Property removeHeader(int index){
		Property p = this.getHeadersAsList().get(index);
		this.headers.remove(p.getName());
		return p;
	}
	
	public int sizeHeaders(){
		return this.headers.size();
	}
	
	public void clearHeaders(){
		this.headers.clear();
	}
	
	
	
	
	
	public Map<String,Property> getResources() {
		return this.resources;
	}
	public List<Property> getResourcesAsList() {
		List<Property> l = new ArrayList<Property>();
		for (String key : this._resources_position) {
			l.add(this.resources.get(key));
		}
		return l;
	}
	
	public void addResource(Property property){
		this.resources.put(property.getName(),property);
		this._resources_position.add(property.getName());
	}
	
	public Property getResource(String key){
		return this.resources.get(key);
	}
	
	public Property removeResource(String key){
		int index = -1;
		for (int i = 0; i < this._resources_position.size(); i++) {
			if(key.equals(this._resources_position.get(i))){
				index = i;
				break;
			}
		}
		this._resources_position.remove(index);
		return this.resources.remove(key);
	}
	
	public Property getResource(int index){
		return this.getResourcesAsList().get(index);
	}
	
	public Property removeResource(int index){
		Property p = this.getResourcesAsList().get(index);
		this.resources.remove(p.getName());
		return p;
	}
	
	public int sizeResources(){
		return this.resources.size();
	}
	
	public void clearResources(){
		this.resources.clear();
	}
	
	
	public MessageType getType() {
		return this.type;
	}

	public void setType(MessageType type) {
		this.type = type;
	}
	
	public String getIdTransaction() {
		return this.idTransaction;
	}

	public void setIdTransaction(String idTransaction) {
		this.idTransaction = idTransaction;
	}
}
