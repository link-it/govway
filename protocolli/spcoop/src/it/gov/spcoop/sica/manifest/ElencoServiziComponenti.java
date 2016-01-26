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


/** <p>Java class ElencoServiziComponenti.
 * 
 * @version $Rev$, $Date$
 * 
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 */

public class ElencoServiziComponenti extends org.openspcoop2.utils.beans.BaseBean implements Serializable , Cloneable {
  private Long id;


  public ElencoServiziComponenti() {
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

  public void addServizioComponente(String servizioComponente) {
    this.servizioComponente.add(servizioComponente);
  }

  public String getServizioComponente(int index) {
    return this.servizioComponente.get( index );
  }

  public String removeServizioComponente(int index) {
    return this.servizioComponente.remove( index );
  }

  public List<String> getServizioComponenteList() {
    return this.servizioComponente;
  }

  public void setServizioComponenteList(List<String> servizioComponente) {
    this.servizioComponente=servizioComponente;
  }

  public int sizeServizioComponenteList() {
    return this.servizioComponente.size();
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

  protected List<String> servizioComponente = new ArrayList<String>();

  /**
   * @deprecated Use method getServizioComponenteList
   * @return List<String>
  */
  @Deprecated
  public List<String> getServizioComponente() {
  	return this.servizioComponente;
  }

  /**
   * @deprecated Use method setServizioComponenteList
   * @param servizioComponente List<String>
  */
  @Deprecated
  public void setServizioComponente(List<String> servizioComponente) {
  	this.servizioComponente=servizioComponente;
  }

  /**
   * @deprecated Use method sizeServizioComponenteList
   * @return lunghezza della lista
  */
  @Deprecated
  public int sizeServizioComponente() {
  	return this.servizioComponente.size();
  }

  public static final String SERVIZIO_COMPONENTE = "servizioComponente";

}
