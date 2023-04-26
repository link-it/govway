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


/** <p>Java class for ElencoServiziComponenti complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ElencoServiziComponenti"&gt;
 * 		&lt;sequence&gt;
 * 			&lt;element name="servizioComponente" type="{http://spcoop.gov.it/sica/manifest}anyURI" minOccurs="1" maxOccurs="unbounded"/&gt;
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
@XmlType(name = "ElencoServiziComponenti", 
  propOrder = {
  	"servizioComponente"
  }
)

@XmlRootElement(name = "ElencoServiziComponenti")

public class ElencoServiziComponenti extends org.openspcoop2.utils.beans.BaseBean implements Serializable , Cloneable {
  public ElencoServiziComponenti() {
    super();
  }

  public void addServizioComponente(java.net.URI servizioComponente) {
    this.servizioComponente.add(servizioComponente);
  }

  public java.net.URI getServizioComponente(int index) {
    return this.servizioComponente.get( index );
  }

  public java.net.URI removeServizioComponente(int index) {
    return this.servizioComponente.remove( index );
  }

  public List<java.net.URI> getServizioComponenteList() {
    return this.servizioComponente;
  }

  public void setServizioComponenteList(List<java.net.URI> servizioComponente) {
    this.servizioComponente=servizioComponente;
  }

  public int sizeServizioComponenteList() {
    return this.servizioComponente.size();
  }

  private static final long serialVersionUID = 1L;



  @javax.xml.bind.annotation.XmlSchemaType(name="anyURI")
  @XmlElement(name="servizioComponente",required=true,nillable=false)
  private List<java.net.URI> servizioComponente = new ArrayList<>();

  /**
   * Use method getServizioComponenteList
   * @return List&lt;java.net.URI&gt;
  */
  public List<java.net.URI> getServizioComponente() {
  	return this.getServizioComponenteList();
  }

  /**
   * Use method setServizioComponenteList
   * @param servizioComponente List&lt;java.net.URI&gt;
  */
  public void setServizioComponente(List<java.net.URI> servizioComponente) {
  	this.setServizioComponenteList(servizioComponente);
  }

  /**
   * Use method sizeServizioComponenteList
   * @return lunghezza della lista
  */
  public int sizeServizioComponente() {
  	return this.sizeServizioComponenteList();
  }

}
