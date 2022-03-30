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

package org.openspcoop2.utils.mail;

import java.util.ArrayList;
import java.util.List;

/**
 * MailBody
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class MailBody {

	private String contentType = null;
	private String message;
	private List<MailAttach> attachments = new ArrayList<MailAttach>();
	
	public String getContentType() {
		return this.contentType;
	}
	public void setContentType(String contentType) {
		this.contentType = contentType;
	}
	public String getMessage() {
		return this.message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public List<MailAttach> getAttachments() {
		return this.attachments;
	}
	public void setAttachments(List<MailAttach> attachments) {
		this.attachments = attachments;
	}
}
