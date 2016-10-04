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
package org.openspcoop2.core.eccezione.details;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


/** <p>Java class Dettagli.
 * 
 * @version $Rev$, $Date$
 * 
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 */

public class Dettagli extends org.openspcoop2.utils.beans.BaseBean implements Serializable , Cloneable {
  private Long id;


  public Dettagli() {
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

  public void addDettaglio(Dettaglio dettaglio) {
    this.dettaglio.add(dettaglio);
  }

  public Dettaglio getDettaglio(int index) {
    return this.dettaglio.get( index );
  }

  public Dettaglio removeDettaglio(int index) {
    return this.dettaglio.remove( index );
  }

  public List<Dettaglio> getDettaglioList() {
    return this.dettaglio;
  }

  public void setDettaglioList(List<Dettaglio> dettaglio) {
    this.dettaglio=dettaglio;
  }

  public int sizeDettaglioList() {
    return this.dettaglio.size();
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

  protected List<Dettaglio> dettaglio = new ArrayList<Dettaglio>();

  /**
   * @deprecated Use method getDettaglioList
   * @return List<Dettaglio>
  */
  @Deprecated
  public List<Dettaglio> getDettaglio() {
  	return this.dettaglio;
  }

  /**
   * @deprecated Use method setDettaglioList
   * @param dettaglio List<Dettaglio>
  */
  @Deprecated
  public void setDettaglio(List<Dettaglio> dettaglio) {
  	this.dettaglio=dettaglio;
  }

  /**
   * @deprecated Use method sizeDettaglioList
   * @return lunghezza della lista
  */
  @Deprecated
  public int sizeDettaglio() {
  	return this.dettaglio.size();
  }

  public static final String DETTAGLIO = "dettaglio";

}
