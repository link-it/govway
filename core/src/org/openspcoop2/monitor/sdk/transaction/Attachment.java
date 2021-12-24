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
package org.openspcoop2.monitor.sdk.transaction;

/**
 * Attachment
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class Attachment {
	
	public Attachment(byte[] content, String cid, String mime) {
		this.cid = cid;
		this.content = content;
		this.mime = mime;
	}
	
	protected String cid;
	protected String mime;
	protected byte[] content;
	private boolean updated = false;

	public boolean isUpdated() {
		return this.updated;
	}
	public void setUpdated(boolean updated) {
		this.updated = updated;
	}
	public String getContentID() {
		return this.cid;
	}
	public void setContentID(String cid) {
		this.cid = cid;
	}
	
	public String getContentType() {
		return this.mime;
	}
	public void setContentType(String mime) {
		this.mime = mime;
	}
	
	public byte[] getContentAsByte() {
		return this.content;
	}
	public void setContentAsByte(byte[] content) {
		this.content = content;
	}
	
}
