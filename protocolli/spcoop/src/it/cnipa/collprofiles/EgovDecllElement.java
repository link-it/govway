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
package it.cnipa.collprofiles;

import java.io.Serializable;


/** <p>Java class EgovDecllElement.
 * 
 * @version $Rev$, $Date$
 * 
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 */

public class EgovDecllElement extends org.openspcoop2.utils.beans.BaseBean implements Serializable , Cloneable {
  private Long id;

  protected String eGovVersion;

  protected String rifDefinizioneInterfaccia;

  protected OperationListType operationList;


  public EgovDecllElement() {
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

  public String getEGovVersion() {
    if(this.eGovVersion!=null && ("".equals(this.eGovVersion)==false)){
		return this.eGovVersion.trim();
	}else{
		return null;
	}

  }

  public void setEGovVersion(String eGovVersion) {
    this.eGovVersion = eGovVersion;
  }

  public String getRifDefinizioneInterfaccia() {
    if(this.rifDefinizioneInterfaccia!=null && ("".equals(this.rifDefinizioneInterfaccia)==false)){
		return this.rifDefinizioneInterfaccia.trim();
	}else{
		return null;
	}

  }

  public void setRifDefinizioneInterfaccia(String rifDefinizioneInterfaccia) {
    this.rifDefinizioneInterfaccia = rifDefinizioneInterfaccia;
  }

  public OperationListType getOperationList() {
    return this.operationList;
  }

  public void setOperationList(OperationListType operationList) {
    this.operationList = operationList;
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

  public static final String E_GOV_VERSION = "eGovVersion";

  public static final String RIF_DEFINIZIONE_INTERFACCIA = "rifDefinizioneInterfaccia";

  public static final String OPERATION_LIST = "operationList";

}
