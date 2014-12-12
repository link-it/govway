/*
 * OpenSPCoop v2 - Customizable SOAP Message Broker 
 * http://www.openspcoop2.org
 * 
 * Copyright (c) 2005-2014 Link.it srl (http://link.it). All rights reserved.
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
package org.openspcoop2.core.tracciamento;

import java.io.Serializable;


/** <p>Java class Allegato.
 * 
 * @version $Rev$, $Date$
 * 
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 */

public class Allegato extends org.openspcoop2.utils.beans.BaseBean implements Serializable , Cloneable {
  private Long id;

  protected String contentId;

  protected String contentLocation;

  protected String contentType;

  protected String digest;


  public Allegato() {
  }

  public Long getId() {
    if(this.id!=null)
		return this.id;
	else
		return new Long(-1);
  }

  public void setId(Long id) {
    if(id!=null)
		this.id=id;
	else
		this.id=new Long(-1);
  }

  public String getContentId() {
    if(this.contentId!=null && ("".equals(this.contentId)==false)){
		return this.contentId.trim();
	}else{
		return null;
	}

  }

  public void setContentId(String contentId) {
    this.contentId = contentId;
  }

  public String getContentLocation() {
    if(this.contentLocation!=null && ("".equals(this.contentLocation)==false)){
		return this.contentLocation.trim();
	}else{
		return null;
	}

  }

  public void setContentLocation(String contentLocation) {
    this.contentLocation = contentLocation;
  }

  public String getContentType() {
    if(this.contentType!=null && ("".equals(this.contentType)==false)){
		return this.contentType.trim();
	}else{
		return null;
	}

  }

  public void setContentType(String contentType) {
    this.contentType = contentType;
  }

  public String getDigest() {
    if(this.digest!=null && ("".equals(this.digest)==false)){
		return this.digest.trim();
	}else{
		return null;
	}

  }

  public void setDigest(String digest) {
    this.digest = digest;
  }

  private static final long serialVersionUID = 1L;

	@Override
	public String serialize(org.openspcoop2.utils.beans.WriteToSerializerType type) throws org.openspcoop2.utils.UtilsException {
		if(type!=null && org.openspcoop2.utils.beans.WriteToSerializerType.JAXB.equals(type)){
			throw new org.openspcoop2.utils.UtilsException("Jaxb annotations not generated");
		}
		else{
			return super.serialize(type);
		}
	}
	@Override
	public String toXml_Jaxb() throws org.openspcoop2.utils.UtilsException {
		throw new org.openspcoop2.utils.UtilsException("Jaxb annotations not generated");
	}

  public static final String CONTENT_ID = "contentId";

  public static final String CONTENT_LOCATION = "contentLocation";

  public static final String CONTENT_TYPE = "contentType";

  public static final String DIGEST = "digest";

}
