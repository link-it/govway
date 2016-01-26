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


/** <p>Java class SpecificaSicurezza.
 * 
 * @version $Rev$, $Date$
 * 
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 */

public class SpecificaSicurezza extends org.openspcoop2.utils.beans.BaseBean implements Serializable , Cloneable {
  private Long id;


  public SpecificaSicurezza() {
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

  public void addDocumentoSicurezza(DocumentoSicurezza documentoSicurezza) {
    this.documentoSicurezza.add(documentoSicurezza);
  }

  public DocumentoSicurezza getDocumentoSicurezza(int index) {
    return this.documentoSicurezza.get( index );
  }

  public DocumentoSicurezza removeDocumentoSicurezza(int index) {
    return this.documentoSicurezza.remove( index );
  }

  public List<DocumentoSicurezza> getDocumentoSicurezzaList() {
    return this.documentoSicurezza;
  }

  public void setDocumentoSicurezzaList(List<DocumentoSicurezza> documentoSicurezza) {
    this.documentoSicurezza=documentoSicurezza;
  }

  public int sizeDocumentoSicurezzaList() {
    return this.documentoSicurezza.size();
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

  protected List<DocumentoSicurezza> documentoSicurezza = new ArrayList<DocumentoSicurezza>();

  /**
   * @deprecated Use method getDocumentoSicurezzaList
   * @return List<DocumentoSicurezza>
  */
  @Deprecated
  public List<DocumentoSicurezza> getDocumentoSicurezza() {
  	return this.documentoSicurezza;
  }

  /**
   * @deprecated Use method setDocumentoSicurezzaList
   * @param documentoSicurezza List<DocumentoSicurezza>
  */
  @Deprecated
  public void setDocumentoSicurezza(List<DocumentoSicurezza> documentoSicurezza) {
  	this.documentoSicurezza=documentoSicurezza;
  }

  /**
   * @deprecated Use method sizeDocumentoSicurezzaList
   * @return lunghezza della lista
  */
  @Deprecated
  public int sizeDocumentoSicurezza() {
  	return this.documentoSicurezza.size();
  }

  public static final String DOCUMENTO_SICUREZZA = "documentoSicurezza";

}
