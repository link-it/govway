/*
 * OpenSPCoop v2 - Customizable SOAP Message Broker 
 * http://www.openspcoop2.org
 * 
 * Copyright (c) 2005-2015 Link.it srl (http://link.it).
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
package org.openspcoop2.utils.logger;

import java.util.ArrayList;
import java.util.List;

/**
 * Message
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class Message {

	private MessageType type;
	
	private String contentType;
	
	private byte[] content;
	
	private byte [] signature;
	
	private List<Attachment> attachments = new ArrayList<Attachment>();
	
	private List<Property> headers = new ArrayList<Property>();
	
	private List<Property> resources = new ArrayList<Property>();
	
	
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
	
	public void getAttachment(int index){
		this.attachments.get(index);
	}
	
	public void removeAttachment(int index){
		this.attachments.remove(index);
	}
	
	public int sizeAttachments(){
		return this.attachments.size();
	}
	
	public void clearAttachments(){
		this.attachments.clear();
	}
	
	public List<Property> getHeaders() {
		return this.headers;
	}
	
	public void addHeader(Property Property){
		this.headers.add(Property);
	}
	
	public void getHeader(int index){
		this.headers.get(index);
	}
	
	public void removeHeader(int index){
		this.headers.remove(index);
	}
	
	public int sizeHeaders(){
		return this.headers.size();
	}
	
	public void clearHeaders(){
		this.headers.clear();
	}
	
	public List<Property> getResources() {
		return this.resources;
	}
	
	public void addResource(Property Property){
		this.resources.add(Property);
	}
	
	public void getResource(int index){
		this.resources.get(index);
	}
	
	public void removeResource(int index){
		this.resources.remove(index);
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
	
	public byte[] getSignature() {
		return this.signature;
	}

	public void setSignature(byte[] signature) {
		this.signature = signature;
	}
}
