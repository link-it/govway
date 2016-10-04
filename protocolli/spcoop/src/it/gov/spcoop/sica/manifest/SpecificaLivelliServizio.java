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


/** <p>Java class SpecificaLivelliServizio.
 * 
 * @version $Rev$, $Date$
 * 
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 */

public class SpecificaLivelliServizio extends org.openspcoop2.utils.beans.BaseBean implements Serializable , Cloneable {
  private Long id;


  public SpecificaLivelliServizio() {
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

  public void addDocumentoLivelloServizio(DocumentoLivelloServizio documentoLivelloServizio) {
    this.documentoLivelloServizio.add(documentoLivelloServizio);
  }

  public DocumentoLivelloServizio getDocumentoLivelloServizio(int index) {
    return this.documentoLivelloServizio.get( index );
  }

  public DocumentoLivelloServizio removeDocumentoLivelloServizio(int index) {
    return this.documentoLivelloServizio.remove( index );
  }

  public List<DocumentoLivelloServizio> getDocumentoLivelloServizioList() {
    return this.documentoLivelloServizio;
  }

  public void setDocumentoLivelloServizioList(List<DocumentoLivelloServizio> documentoLivelloServizio) {
    this.documentoLivelloServizio=documentoLivelloServizio;
  }

  public int sizeDocumentoLivelloServizioList() {
    return this.documentoLivelloServizio.size();
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

  protected List<DocumentoLivelloServizio> documentoLivelloServizio = new ArrayList<DocumentoLivelloServizio>();

  /**
   * @deprecated Use method getDocumentoLivelloServizioList
   * @return List<DocumentoLivelloServizio>
  */
  @Deprecated
  public List<DocumentoLivelloServizio> getDocumentoLivelloServizio() {
  	return this.documentoLivelloServizio;
  }

  /**
   * @deprecated Use method setDocumentoLivelloServizioList
   * @param documentoLivelloServizio List<DocumentoLivelloServizio>
  */
  @Deprecated
  public void setDocumentoLivelloServizio(List<DocumentoLivelloServizio> documentoLivelloServizio) {
  	this.documentoLivelloServizio=documentoLivelloServizio;
  }

  /**
   * @deprecated Use method sizeDocumentoLivelloServizioList
   * @return lunghezza della lista
  */
  @Deprecated
  public int sizeDocumentoLivelloServizio() {
  	return this.documentoLivelloServizio.size();
  }

  public static final String DOCUMENTO_LIVELLO_SERVIZIO = "documentoLivelloServizio";

}
