/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2025 Link.it srl (https://link.it). 
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

import org.openspcoop2.utils.mime.MimeTypes;
import org.openspcoop2.utils.transport.http.HttpConstants;
import org.slf4j.Logger;

/**
 * MailAttach
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public abstract class MailAttach {

	private String name;
	private String contentType;
	
	protected MailAttach(String name, String contentType){
		this.name = name;
		this.contentType = contentType;
	}
	
	public String getName() {
		return this.name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getContentType(Logger log) {
		if(this.contentType!=null)
			return this.contentType;
		else{
			MimeTypes mimeTypes = null;
			try{
				mimeTypes = MimeTypes.getInstance();
				if(this.name!=null &&
					this.name.contains(".")){
					String ext = this.name.substring(this.name.lastIndexOf('.')+1, this.name.length());
					return mimeTypes.getMimeType(ext);
				}
			}catch(Exception e){
				log.error(e.getMessage(),e);
			}
			// default
			if(mimeTypes!=null)
				return mimeTypes.getMimeType("bin");
			else
				return HttpConstants.CONTENT_TYPE_APPLICATION_OCTET_STREAM;
		}
	}
	public void setContentType(String contentType) {
		this.contentType = contentType;
	}
}
