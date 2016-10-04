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


/** <p>Java class Eccezioni.
 * 
 * @version $Rev$, $Date$
 * 
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 */

public class Eccezioni extends org.openspcoop2.utils.beans.BaseBean implements Serializable , Cloneable {
  private Long id;


  public Eccezioni() {
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

  public void addEccezione(Eccezione eccezione) {
    this.eccezione.add(eccezione);
  }

  public Eccezione getEccezione(int index) {
    return this.eccezione.get( index );
  }

  public Eccezione removeEccezione(int index) {
    return this.eccezione.remove( index );
  }

  public List<Eccezione> getEccezioneList() {
    return this.eccezione;
  }

  public void setEccezioneList(List<Eccezione> eccezione) {
    this.eccezione=eccezione;
  }

  public int sizeEccezioneList() {
    return this.eccezione.size();
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

  protected List<Eccezione> eccezione = new ArrayList<Eccezione>();

  /**
   * @deprecated Use method getEccezioneList
   * @return List<Eccezione>
  */
  @Deprecated
  public List<Eccezione> getEccezione() {
  	return this.eccezione;
  }

  /**
   * @deprecated Use method setEccezioneList
   * @param eccezione List<Eccezione>
  */
  @Deprecated
  public void setEccezione(List<Eccezione> eccezione) {
  	this.eccezione=eccezione;
  }

  /**
   * @deprecated Use method sizeEccezioneList
   * @return lunghezza della lista
  */
  @Deprecated
  public int sizeEccezione() {
  	return this.eccezione.size();
  }

  public static final String ECCEZIONE = "eccezione";

}
