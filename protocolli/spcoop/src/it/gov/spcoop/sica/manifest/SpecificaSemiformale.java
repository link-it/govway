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


/** <p>Java class SpecificaSemiformale.
 * 
 * @version $Rev$, $Date$
 * 
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 */

public class SpecificaSemiformale extends org.openspcoop2.utils.beans.BaseBean implements Serializable , Cloneable {
  private Long id;


  public SpecificaSemiformale() {
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

  public void addDocumentoSemiformale(DocumentoSemiformale documentoSemiformale) {
    this.documentoSemiformale.add(documentoSemiformale);
  }

  public DocumentoSemiformale getDocumentoSemiformale(int index) {
    return this.documentoSemiformale.get( index );
  }

  public DocumentoSemiformale removeDocumentoSemiformale(int index) {
    return this.documentoSemiformale.remove( index );
  }

  public List<DocumentoSemiformale> getDocumentoSemiformaleList() {
    return this.documentoSemiformale;
  }

  public void setDocumentoSemiformaleList(List<DocumentoSemiformale> documentoSemiformale) {
    this.documentoSemiformale=documentoSemiformale;
  }

  public int sizeDocumentoSemiformaleList() {
    return this.documentoSemiformale.size();
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

  protected List<DocumentoSemiformale> documentoSemiformale = new ArrayList<DocumentoSemiformale>();

  /**
   * @deprecated Use method getDocumentoSemiformaleList
   * @return List<DocumentoSemiformale>
  */
  @Deprecated
  public List<DocumentoSemiformale> getDocumentoSemiformale() {
  	return this.documentoSemiformale;
  }

  /**
   * @deprecated Use method setDocumentoSemiformaleList
   * @param documentoSemiformale List<DocumentoSemiformale>
  */
  @Deprecated
  public void setDocumentoSemiformale(List<DocumentoSemiformale> documentoSemiformale) {
  	this.documentoSemiformale=documentoSemiformale;
  }

  /**
   * @deprecated Use method sizeDocumentoSemiformaleList
   * @return lunghezza della lista
  */
  @Deprecated
  public int sizeDocumentoSemiformale() {
  	return this.documentoSemiformale.size();
  }

  public static final String DOCUMENTO_SEMIFORMALE = "documentoSemiformale";

}
