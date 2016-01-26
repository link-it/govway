/*
 * OpenSPCoop v2 - Customizable SOAP Message Broker 
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
package it.gov.spcoop.sica.manifest;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


/** <p>Java class ElencoServiziComposti.
 * 
 * @version $Rev$, $Date$
 * 
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 */

public class ElencoServiziComposti extends org.openspcoop2.utils.beans.BaseBean implements Serializable , Cloneable {
  private Long id;


  public ElencoServiziComposti() {
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

  public void addServizioComposto(String servizioComposto) {
    this.servizioComposto.add(servizioComposto);
  }

  public String getServizioComposto(int index) {
    return this.servizioComposto.get( index );
  }

  public String removeServizioComposto(int index) {
    return this.servizioComposto.remove( index );
  }

  public List<String> getServizioCompostoList() {
    return this.servizioComposto;
  }

  public void setServizioCompostoList(List<String> servizioComposto) {
    this.servizioComposto=servizioComposto;
  }

  public int sizeServizioCompostoList() {
    return this.servizioComposto.size();
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

  protected List<String> servizioComposto = new ArrayList<String>();

  /**
   * @deprecated Use method getServizioCompostoList
   * @return List<String>
  */
  @Deprecated
  public List<String> getServizioComposto() {
  	return this.servizioComposto;
  }

  /**
   * @deprecated Use method setServizioCompostoList
   * @param servizioComposto List<String>
  */
  @Deprecated
  public void setServizioComposto(List<String> servizioComposto) {
  	this.servizioComposto=servizioComposto;
  }

  /**
   * @deprecated Use method sizeServizioCompostoList
   * @return lunghezza della lista
  */
  @Deprecated
  public int sizeServizioComposto() {
  	return this.servizioComposto.size();
  }

  public static final String SERVIZIO_COMPOSTO = "servizioComposto";

}
