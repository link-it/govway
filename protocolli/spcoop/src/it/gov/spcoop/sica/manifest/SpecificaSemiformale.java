/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2023 Link.it srl (https://link.it).
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

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


/** <p>Java class for SpecificaSemiformale complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="SpecificaSemiformale"&gt;
 * 		&lt;sequence&gt;
 * 			&lt;element name="documentoSemiformale" type="{http://spcoop.gov.it/sica/manifest}DocumentoSemiformale" minOccurs="0" maxOccurs="unbounded"/&gt;
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
@XmlType(name = "SpecificaSemiformale", 
  propOrder = {
  	"documentoSemiformale"
  }
)

@XmlRootElement(name = "SpecificaSemiformale")

public class SpecificaSemiformale extends org.openspcoop2.utils.beans.BaseBean implements Serializable , Cloneable {
  public SpecificaSemiformale() {
    super();
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



  @XmlElement(name="documentoSemiformale",required=true,nillable=false)
  private List<DocumentoSemiformale> documentoSemiformale = new ArrayList<>();

  /**
   * Use method getDocumentoSemiformaleList
   * @return List&lt;DocumentoSemiformale&gt;
  */
  public List<DocumentoSemiformale> getDocumentoSemiformale() {
  	return this.getDocumentoSemiformaleList();
  }

  /**
   * Use method setDocumentoSemiformaleList
   * @param documentoSemiformale List&lt;DocumentoSemiformale&gt;
  */
  public void setDocumentoSemiformale(List<DocumentoSemiformale> documentoSemiformale) {
  	this.setDocumentoSemiformaleList(documentoSemiformale);
  }

  /**
   * Use method sizeDocumentoSemiformaleList
   * @return lunghezza della lista
  */
  public int sizeDocumentoSemiformale() {
  	return this.sizeDocumentoSemiformaleList();
  }

}
