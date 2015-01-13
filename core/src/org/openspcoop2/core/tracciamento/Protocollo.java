/*
 * OpenSPCoop v2 - Customizable SOAP Message Broker 
 * http://www.openspcoop2.org
 * 
 * Copyright (c) 2005-2015 Link.it srl (http://link.it).
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
import java.util.ArrayList;
import java.util.List;


/** <p>Java class Protocollo.
 * 
 * @version $Rev$, $Date$
 * 
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 */

public class Protocollo extends org.openspcoop2.utils.beans.BaseBean implements Serializable , Cloneable {
  private Long id;

  protected String identificativo;


  public Protocollo() {
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

  public void addProprieta(ProtocolloProprieta proprieta) {
    this.proprieta.add(proprieta);
  }

  public ProtocolloProprieta getProprieta(int index) {
    return this.proprieta.get( index );
  }

  public ProtocolloProprieta removeProprieta(int index) {
    return this.proprieta.remove( index );
  }

  public List<ProtocolloProprieta> getProprietaList() {
    return this.proprieta;
  }

  public void setProprietaList(List<ProtocolloProprieta> proprieta) {
    this.proprieta=proprieta;
  }

  public int sizeProprietaList() {
    return this.proprieta.size();
  }

  public String getIdentificativo() {
    if(this.identificativo!=null && ("".equals(this.identificativo)==false)){
		return this.identificativo.trim();
	}else{
		return null;
	}

  }

  public void setIdentificativo(String identificativo) {
    this.identificativo = identificativo;
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

  protected List<ProtocolloProprieta> proprieta = new ArrayList<ProtocolloProprieta>();

  /**
   * @deprecated Use method getProprietaList
   * @return List<ProtocolloProprieta>
  */
  @Deprecated
  public List<ProtocolloProprieta> getProprieta() {
  	return this.proprieta;
  }

  /**
   * @deprecated Use method setProprietaList
   * @param proprieta List<ProtocolloProprieta>
  */
  @Deprecated
  public void setProprieta(List<ProtocolloProprieta> proprieta) {
  	this.proprieta=proprieta;
  }

  /**
   * @deprecated Use method sizeProprietaList
   * @return lunghezza della lista
  */
  @Deprecated
  public int sizeProprieta() {
  	return this.proprieta.size();
  }

  public static final String PROPRIETA = "proprieta";

  public static final String IDENTIFICATIVO = "identificativo";

}
