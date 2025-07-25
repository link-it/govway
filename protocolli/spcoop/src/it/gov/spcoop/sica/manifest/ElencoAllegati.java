/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2025 Link.it srl (https://link.it).
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


/** <p>Java class for ElencoAllegati complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ElencoAllegati"&gt;
 * 		&lt;sequence&gt;
 * 			&lt;element name="genericoDocumento" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0" maxOccurs="unbounded"/&gt;
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
@XmlType(name = "ElencoAllegati", 
  propOrder = {
  	"genericoDocumento"
  }
)

@XmlRootElement(name = "ElencoAllegati")

public class ElencoAllegati extends org.openspcoop2.utils.beans.BaseBean implements Serializable , Cloneable {
  public ElencoAllegati() {
    super();
  }

  public void addGenericoDocumento(java.lang.String genericoDocumento) {
    this.genericoDocumento.add(genericoDocumento);
  }

  public java.lang.String getGenericoDocumento(int index) {
    return this.genericoDocumento.get( index );
  }

  public java.lang.String removeGenericoDocumento(int index) {
    return this.genericoDocumento.remove( index );
  }

  public List<java.lang.String> getGenericoDocumentoList() {
    return this.genericoDocumento;
  }

  public void setGenericoDocumentoList(List<java.lang.String> genericoDocumento) {
    this.genericoDocumento=genericoDocumento;
  }

  public int sizeGenericoDocumentoList() {
    return this.genericoDocumento.size();
  }

  private static final long serialVersionUID = 1L;



  @jakarta.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="genericoDocumento",required=true,nillable=false)
  private List<java.lang.String> genericoDocumento = new ArrayList<>();

  /**
   * Use method getGenericoDocumentoList
   * @return List&lt;java.lang.String&gt;
  */
  public List<java.lang.String> getGenericoDocumento() {
  	return this.getGenericoDocumentoList();
  }

  /**
   * Use method setGenericoDocumentoList
   * @param genericoDocumento List&lt;java.lang.String&gt;
  */
  public void setGenericoDocumento(List<java.lang.String> genericoDocumento) {
  	this.setGenericoDocumentoList(genericoDocumento);
  }

  /**
   * Use method sizeGenericoDocumentoList
   * @return lunghezza della lista
  */
  public int sizeGenericoDocumento() {
  	return this.sizeGenericoDocumentoList();
  }

}
