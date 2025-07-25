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


/** <p>Java class for ElencoPartecipanti complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ElencoPartecipanti"&gt;
 * 		&lt;sequence&gt;
 * 			&lt;element name="partecipante" type="{http://spcoop.gov.it/sica/manifest}anyURI" minOccurs="2" maxOccurs="unbounded"/&gt;
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
@XmlType(name = "ElencoPartecipanti", 
  propOrder = {
  	"partecipante"
  }
)

@XmlRootElement(name = "ElencoPartecipanti")

public class ElencoPartecipanti extends org.openspcoop2.utils.beans.BaseBean implements Serializable , Cloneable {
  public ElencoPartecipanti() {
    super();
  }

  public void addPartecipante(java.net.URI partecipante) {
    this.partecipante.add(partecipante);
  }

  public java.net.URI getPartecipante(int index) {
    return this.partecipante.get( index );
  }

  public java.net.URI removePartecipante(int index) {
    return this.partecipante.remove( index );
  }

  public List<java.net.URI> getPartecipanteList() {
    return this.partecipante;
  }

  public void setPartecipanteList(List<java.net.URI> partecipante) {
    this.partecipante=partecipante;
  }

  public int sizePartecipanteList() {
    return this.partecipante.size();
  }

  private static final long serialVersionUID = 1L;



  @jakarta.xml.bind.annotation.XmlSchemaType(name="anyURI")
  @XmlElement(name="partecipante",required=true,nillable=false)
  private List<java.net.URI> partecipante = new ArrayList<>();

  /**
   * Use method getPartecipanteList
   * @return List&lt;java.net.URI&gt;
  */
  public List<java.net.URI> getPartecipante() {
  	return this.getPartecipanteList();
  }

  /**
   * Use method setPartecipanteList
   * @param partecipante List&lt;java.net.URI&gt;
  */
  public void setPartecipante(List<java.net.URI> partecipante) {
  	this.setPartecipanteList(partecipante);
  }

  /**
   * Use method sizePartecipanteList
   * @return lunghezza della lista
  */
  public int sizePartecipante() {
  	return this.sizePartecipanteList();
  }

}
