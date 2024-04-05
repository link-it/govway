/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2024 Link.it srl (https://link.it).
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License version 3, as published by
 * the Free Software Foundation.
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

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


/** <p>Java class for SpecificaSicurezza complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="SpecificaSicurezza"&gt;
 * 		&lt;sequence&gt;
 * 			&lt;element name="documentoSicurezza" type="{http://spcoop.gov.it/sica/manifest}DocumentoSicurezza" minOccurs="1" maxOccurs="unbounded"/&gt;
 * 		&lt;/sequence&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * @version $Rev$, $Date$
 * 
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * */

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "SpecificaSicurezza", 
  propOrder = {
  	"documentoSicurezza"
  }
)

@XmlRootElement(name = "SpecificaSicurezza")

public class SpecificaSicurezza extends org.openspcoop2.utils.beans.BaseBean implements Serializable , Cloneable {
  public SpecificaSicurezza() {
    super();
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



  @XmlElement(name="documentoSicurezza",required=true,nillable=false)
  private List<DocumentoSicurezza> documentoSicurezza = new ArrayList<>();

  /**
   * Use method getDocumentoSicurezzaList
   * @return List&lt;DocumentoSicurezza&gt;
  */
  public List<DocumentoSicurezza> getDocumentoSicurezza() {
  	return this.getDocumentoSicurezzaList();
  }

  /**
   * Use method setDocumentoSicurezzaList
   * @param documentoSicurezza List&lt;DocumentoSicurezza&gt;
  */
  public void setDocumentoSicurezza(List<DocumentoSicurezza> documentoSicurezza) {
  	this.setDocumentoSicurezzaList(documentoSicurezza);
  }

  /**
   * Use method sizeDocumentoSicurezzaList
   * @return lunghezza della lista
  */
  public int sizeDocumentoSicurezza() {
  	return this.sizeDocumentoSicurezzaList();
  }

}
