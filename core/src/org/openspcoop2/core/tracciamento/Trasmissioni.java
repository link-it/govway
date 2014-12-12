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
import java.util.ArrayList;
import java.util.List;


/** <p>Java class Trasmissioni.
 * 
 * @version $Rev$, $Date$
 * 
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 */

public class Trasmissioni extends org.openspcoop2.utils.beans.BaseBean implements Serializable , Cloneable {
  private Long id;


  public Trasmissioni() {
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

  public void addTrasmissione(Trasmissione trasmissione) {
    this.trasmissione.add(trasmissione);
  }

  public Trasmissione getTrasmissione(int index) {
    return this.trasmissione.get( index );
  }

  public Trasmissione removeTrasmissione(int index) {
    return this.trasmissione.remove( index );
  }

  public List<Trasmissione> getTrasmissioneList() {
    return this.trasmissione;
  }

  public void setTrasmissioneList(List<Trasmissione> trasmissione) {
    this.trasmissione=trasmissione;
  }

  public int sizeTrasmissioneList() {
    return this.trasmissione.size();
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

  protected List<Trasmissione> trasmissione = new ArrayList<Trasmissione>();

  /**
   * @deprecated Use method getTrasmissioneList
   * @return List<Trasmissione>
  */
  @Deprecated
  public List<Trasmissione> getTrasmissione() {
  	return this.trasmissione;
  }

  /**
   * @deprecated Use method setTrasmissioneList
   * @param trasmissione List<Trasmissione>
  */
  @Deprecated
  public void setTrasmissione(List<Trasmissione> trasmissione) {
  	this.trasmissione=trasmissione;
  }

  /**
   * @deprecated Use method sizeTrasmissioneList
   * @return lunghezza della lista
  */
  @Deprecated
  public int sizeTrasmissione() {
  	return this.trasmissione.size();
  }

  public static final String TRASMISSIONE = "trasmissione";

}
