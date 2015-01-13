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


/** <p>Java class Riscontri.
 * 
 * @version $Rev$, $Date$
 * 
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 */

public class Riscontri extends org.openspcoop2.utils.beans.BaseBean implements Serializable , Cloneable {
  private Long id;


  public Riscontri() {
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

  public void addRiscontro(Riscontro riscontro) {
    this.riscontro.add(riscontro);
  }

  public Riscontro getRiscontro(int index) {
    return this.riscontro.get( index );
  }

  public Riscontro removeRiscontro(int index) {
    return this.riscontro.remove( index );
  }

  public List<Riscontro> getRiscontroList() {
    return this.riscontro;
  }

  public void setRiscontroList(List<Riscontro> riscontro) {
    this.riscontro=riscontro;
  }

  public int sizeRiscontroList() {
    return this.riscontro.size();
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

  protected List<Riscontro> riscontro = new ArrayList<Riscontro>();

  /**
   * @deprecated Use method getRiscontroList
   * @return List<Riscontro>
  */
  @Deprecated
  public List<Riscontro> getRiscontro() {
  	return this.riscontro;
  }

  /**
   * @deprecated Use method setRiscontroList
   * @param riscontro List<Riscontro>
  */
  @Deprecated
  public void setRiscontro(List<Riscontro> riscontro) {
  	this.riscontro=riscontro;
  }

  /**
   * @deprecated Use method sizeRiscontroList
   * @return lunghezza della lista
  */
  @Deprecated
  public int sizeRiscontro() {
  	return this.riscontro.size();
  }

  public static final String RISCONTRO = "riscontro";

}
