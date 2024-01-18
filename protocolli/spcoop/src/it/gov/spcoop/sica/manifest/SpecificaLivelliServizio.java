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

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


/** <p>Java class for SpecificaLivelliServizio complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="SpecificaLivelliServizio"&gt;
 * 		&lt;sequence&gt;
 * 			&lt;element name="documentoLivelloServizio" type="{http://spcoop.gov.it/sica/manifest}DocumentoLivelloServizio" minOccurs="1" maxOccurs="unbounded"/&gt;
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
@XmlType(name = "SpecificaLivelliServizio", 
  propOrder = {
  	"documentoLivelloServizio"
  }
)

@XmlRootElement(name = "SpecificaLivelliServizio")

public class SpecificaLivelliServizio extends org.openspcoop2.utils.beans.BaseBean implements Serializable , Cloneable {
  public SpecificaLivelliServizio() {
    super();
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



  @XmlElement(name="documentoLivelloServizio",required=true,nillable=false)
  private List<DocumentoLivelloServizio> documentoLivelloServizio = new ArrayList<>();

  /**
   * Use method getDocumentoLivelloServizioList
   * @return List&lt;DocumentoLivelloServizio&gt;
  */
  public List<DocumentoLivelloServizio> getDocumentoLivelloServizio() {
  	return this.getDocumentoLivelloServizioList();
  }

  /**
   * Use method setDocumentoLivelloServizioList
   * @param documentoLivelloServizio List&lt;DocumentoLivelloServizio&gt;
  */
  public void setDocumentoLivelloServizio(List<DocumentoLivelloServizio> documentoLivelloServizio) {
  	this.setDocumentoLivelloServizioList(documentoLivelloServizio);
  }

  /**
   * Use method sizeDocumentoLivelloServizioList
   * @return lunghezza della lista
  */
  public int sizeDocumentoLivelloServizio() {
  	return this.sizeDocumentoLivelloServizioList();
  }

}
