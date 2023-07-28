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
package org.openspcoop2.core.tracciamento;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;
import java.io.Serializable;


/** <p>Java class for riscontro complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="riscontro"&gt;
 * 		&lt;sequence&gt;
 * 			&lt;element name="identificativo" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0" maxOccurs="1"/&gt;
 * 			&lt;element name="ora-registrazione" type="{http://www.openspcoop2.org/core/tracciamento}data" minOccurs="0" maxOccurs="1"/&gt;
 * 			&lt;element name="ricevuta" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0" maxOccurs="1"/&gt;
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
@XmlType(name = "riscontro", 
  propOrder = {
  	"identificativo",
  	"oraRegistrazione",
  	"ricevuta"
  }
)

@XmlRootElement(name = "riscontro")

public class Riscontro extends org.openspcoop2.utils.beans.BaseBeanWithId implements Serializable , Cloneable {
  public Riscontro() {
    super();
  }

  public java.lang.String getIdentificativo() {
    return this.identificativo;
  }

  public void setIdentificativo(java.lang.String identificativo) {
    this.identificativo = identificativo;
  }

  public Data getOraRegistrazione() {
    return this.oraRegistrazione;
  }

  public void setOraRegistrazione(Data oraRegistrazione) {
    this.oraRegistrazione = oraRegistrazione;
  }

  public java.lang.String getRicevuta() {
    return this.ricevuta;
  }

  public void setRicevuta(java.lang.String ricevuta) {
    this.ricevuta = ricevuta;
  }

  private static final long serialVersionUID = 1L;



  @jakarta.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="identificativo",required=false,nillable=false)
  protected java.lang.String identificativo;

  @XmlElement(name="ora-registrazione",required=false,nillable=false)
  protected Data oraRegistrazione;

  @jakarta.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="ricevuta",required=false,nillable=false)
  protected java.lang.String ricevuta;

}
