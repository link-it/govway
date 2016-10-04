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
package org.openspcoop2.web.lib.audit.dao;

import java.io.Serializable;
import java.math.BigInteger;


/** <p>Java class Binary.
 * 
 * @version $Rev$, $Date$
 * 
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 */

public class Binary extends org.openspcoop2.utils.beans.BaseBean implements Serializable , Cloneable {
  private Long id;

  private java.lang.Long idOperation;

  protected String binaryId;

  protected BigInteger checksum;


  public Binary() {
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

  public Long getIdOperation() {
    if(this.idOperation!=null){
		return this.idOperation;
	}else{
		return new Long(-1);
	}

  }

  public void setIdOperation(Long idOperation) {
    if(idOperation!=null)
this.idOperation=idOperation;
else
this.idOperation=new Long(-1);
  }

  public String getBinaryId() {
    if(this.binaryId!=null && ("".equals(this.binaryId)==false)){
		return this.binaryId.trim();
	}else{
		return null;
	}

  }

  public void setBinaryId(String binaryId) {
    this.binaryId = binaryId;
  }

  public BigInteger getChecksum() {
    return this.checksum;
  }

  public void setChecksum(BigInteger checksum) {
    this.checksum = checksum;
  }

  private static final long serialVersionUID = 1L;

  public static final String ID_OPERATION = "idOperation";

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

  public static final String BINARY_ID = "binaryId";

  public static final String CHECKSUM = "checksum";

}
