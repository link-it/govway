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

package org.openspcoop2.utils.mail;

import org.openspcoop2.utils.resources.MimeTypes;

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
	
	public MailAttach(String name, String contentType){
		this.name = name;
		this.contentType = contentType;
	}
	
	public String getName() {
		return this.name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getContentType() {
		if(this.contentType!=null)
			return this.contentType;
		else{
			MimeTypes mimeTypes = null;
			try{
				mimeTypes = MimeTypes.getInstance();
				if(this.name!=null){
					if(this.name.contains(".")){
						String ext = this.name.substring(this.name.lastIndexOf('.')+1, this.name.length());
						return mimeTypes.getMimeType(ext);
					}
				}
			}catch(Exception e){
				e.printStackTrace(System.err);
				
			}
			// default
			if(mimeTypes!=null)
				return mimeTypes.getMimeType("bin");
			else
				return "application/octet-stream";
		}
	}
	public void setContentType(String contentType) {
		this.contentType = contentType;
	}
}
