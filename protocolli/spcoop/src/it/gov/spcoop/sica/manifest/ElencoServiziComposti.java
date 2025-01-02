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

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


/** <p>Java class for ElencoServiziComposti complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ElencoServiziComposti"&gt;
 * 		&lt;sequence&gt;
 * 			&lt;element name="servizioComposto" type="{http://spcoop.gov.it/sica/manifest}anyURI" minOccurs="0" maxOccurs="unbounded"/&gt;
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
@XmlType(name = "ElencoServiziComposti", 
  propOrder = {
  	"servizioComposto"
  }
)

@XmlRootElement(name = "ElencoServiziComposti")

public class ElencoServiziComposti extends org.openspcoop2.utils.beans.BaseBean implements Serializable , Cloneable {
  public ElencoServiziComposti() {
    super();
  }

  public void addServizioComposto(java.net.URI servizioComposto) {
    this.servizioComposto.add(servizioComposto);
  }

  public java.net.URI getServizioComposto(int index) {
    return this.servizioComposto.get( index );
  }

  public java.net.URI removeServizioComposto(int index) {
    return this.servizioComposto.remove( index );
  }

  public List<java.net.URI> getServizioCompostoList() {
    return this.servizioComposto;
  }

  public void setServizioCompostoList(List<java.net.URI> servizioComposto) {
    this.servizioComposto=servizioComposto;
  }

  public int sizeServizioCompostoList() {
    return this.servizioComposto.size();
  }

  private static final long serialVersionUID = 1L;



  @javax.xml.bind.annotation.XmlSchemaType(name="anyURI")
  @XmlElement(name="servizioComposto",required=true,nillable=false)
  private List<java.net.URI> servizioComposto = new ArrayList<>();

  /**
   * Use method getServizioCompostoList
   * @return List&lt;java.net.URI&gt;
  */
  public List<java.net.URI> getServizioComposto() {
  	return this.getServizioCompostoList();
  }

  /**
   * Use method setServizioCompostoList
   * @param servizioComposto List&lt;java.net.URI&gt;
  */
  public void setServizioComposto(List<java.net.URI> servizioComposto) {
  	this.setServizioCompostoList(servizioComposto);
  }

  /**
   * Use method sizeServizioCompostoList
   * @return lunghezza della lista
  */
  public int sizeServizioComposto() {
  	return this.sizeServizioCompostoList();
  }

}
