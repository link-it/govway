/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2024 Link.it srl (https://link.it).
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
package org.openspcoop2.monitor.sdk.transaction;

import org.openspcoop2.monitor.sdk.constants.ContentResourceNames;
import org.openspcoop2.monitor.sdk.constants.MessageType;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * AttachmentResource
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class AttachmentResource extends AbstractContentResource {

	private LinkedList<Attachment> attachments = null;
	
	public AttachmentResource(MessageType messageType) {
		super(messageType);
		this.attachments = new LinkedList<Attachment>();
	}
	
	public void addAttachment(Attachment attach) {
		this.attachments.add(attach);
	}
	
	public void addAttachment(Attachment attach, int position) {
		this.attachments.add(position, attach);
	}
	
	public void setValue(LinkedList<Attachment> attachs) {
		this.attachments = attachs;
	}
	
	

	public List<Attachment> getAttachments() {
		return this.attachments;
	}
	
	public Attachment getAttachmentByPosition(int index) {
		return this.attachments.get(index);
	}
	
	public Attachment getAttachmentByContentId(String cid) {
		for (int i=0; i<this.attachments.size(); i++) {
			if (cid.equals(this.attachments.get(i).getContentID())) 
					return this.attachments.get(i);
		}
		return null;
	}
	
	@Override
	public String getName() {
		if (this.isRequest())
			return ContentResourceNames.REQ_ATTACHMENT;
		else
			return ContentResourceNames.RES_ATTACHMENT;
	}
	
	@Override
	public LinkedList<Attachment> getValue() {
		return this.attachments;
	}
	
	public List<String> cids(){
		List<String> list = new ArrayList<>();
		for (int i=0; i<this.attachments.size(); i++) {
			list.add(this.attachments.get(i).getContentID()); 
		}
		return list;
	}
}
