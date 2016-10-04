/*
 * OpenSPCoop - Customizable API Gateway 
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
package org.openspcoop2.security.message.utils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.xml.soap.AttachmentPart;

import org.openspcoop2.message.OpenSPCoop2Message;
import org.openspcoop2.security.message.constants.SecurityConstants;

/**
 * AttachmentProcessingPart
 *
 * @author Andrea Poli (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class AttachmentProcessingPart extends ProcessingPart<Integer, List<AttachmentPart>>{

	private boolean allAttachments;
	
	public AttachmentProcessingPart(boolean content) {
		this.allAttachments = true;
		this.content = content;
	}

	public AttachmentProcessingPart(Integer index, boolean content) {
		this.allAttachments = true;
		this.part = index;
		this.content = content;
	}

	public boolean isAllAttachments() {
		return this.allAttachments;
	}

	public void setAllAttachments(boolean allAttachments) {
		this.allAttachments = allAttachments;
	}

	@Override
	public List<AttachmentPart> getOutput(OpenSPCoop2Message message) throws Exception {

		Iterator<?> it = message.getAttachments();
		if(it.hasNext()==false){
			throw new Exception("Property "+SecurityConstants.SIGNATURE_PARTS+ " contiene la richiesta di cifrare attachments, ma il messaggio non ne contiene");
		}

		int indexAllegatoEncrypt = 1;
		List<AttachmentPart> lst = new ArrayList<AttachmentPart>();
		while (it.hasNext()) {
			AttachmentPart ap = (AttachmentPart) it.next();

			if(this.allAttachments){
				lst.add(ap);
			}else{
				if(this.getPart().equals(indexAllegatoEncrypt)) {
					lst.add(ap);
				}
				indexAllegatoEncrypt++;
			}
		}
		return lst;
	}
}
