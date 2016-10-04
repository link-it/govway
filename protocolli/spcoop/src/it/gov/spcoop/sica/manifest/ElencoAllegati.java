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
package it.gov.spcoop.sica.manifest;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


/** <p>Java class ElencoAllegati.
 * 
 * @version $Rev$, $Date$
 * 
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 */

public class ElencoAllegati extends org.openspcoop2.utils.beans.BaseBean implements Serializable , Cloneable {
  private Long id;


  public ElencoAllegati() {
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

  public void addGenericoDocumento(String genericoDocumento) {
    this.genericoDocumento.add(genericoDocumento);
  }

  public String getGenericoDocumento(int index) {
    return this.genericoDocumento.get( index );
  }

  public String removeGenericoDocumento(int index) {
    return this.genericoDocumento.remove( index );
  }

  public List<String> getGenericoDocumentoList() {
    return this.genericoDocumento;
  }

  public void setGenericoDocumentoList(List<String> genericoDocumento) {
    this.genericoDocumento=genericoDocumento;
  }

  public int sizeGenericoDocumentoList() {
    return this.genericoDocumento.size();
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

  protected List<String> genericoDocumento = new ArrayList<String>();

  /**
   * @deprecated Use method getGenericoDocumentoList
   * @return List<String>
  */
  @Deprecated
  public List<String> getGenericoDocumento() {
  	return this.genericoDocumento;
  }

  /**
   * @deprecated Use method setGenericoDocumentoList
   * @param genericoDocumento List<String>
  */
  @Deprecated
  public void setGenericoDocumento(List<String> genericoDocumento) {
  	this.genericoDocumento=genericoDocumento;
  }

  /**
   * @deprecated Use method sizeGenericoDocumentoList
   * @return lunghezza della lista
  */
  @Deprecated
  public int sizeGenericoDocumento() {
  	return this.genericoDocumento.size();
  }

  public static final String GENERICO_DOCUMENTO = "genericoDocumento";

}
