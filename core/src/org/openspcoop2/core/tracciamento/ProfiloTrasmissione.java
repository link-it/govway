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

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import java.io.Serializable;


/** <p>Java class for profilo-trasmissione complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="profilo-trasmissione"&gt;
 * 		&lt;sequence&gt;
 * 			&lt;element name="inoltro" type="{http://www.openspcoop2.org/core/tracciamento}inoltro" minOccurs="0" maxOccurs="1"/&gt;
 * 		&lt;/sequence&gt;
 * 		&lt;attribute name="conferma-ricezione" type="{http://www.w3.org/2001/XMLSchema}boolean" use="optional" default="false"/&gt;
 * 		&lt;attribute name="sequenza" type="{http://www.w3.org/2001/XMLSchema}integer" use="optional"/&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * @version $Rev$, $Date$
 * 
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * */

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "profilo-trasmissione", 
  propOrder = {
  	"inoltro"
  }
)

@XmlRootElement(name = "profilo-trasmissione")

public class ProfiloTrasmissione extends org.openspcoop2.utils.beans.BaseBeanWithId implements Serializable , Cloneable {
  public ProfiloTrasmissione() {
    super();
  }

  public Inoltro getInoltro() {
    return this.inoltro;
  }

  public void setInoltro(Inoltro inoltro) {
    this.inoltro = inoltro;
  }

  public boolean isConfermaRicezione() {
    return this.confermaRicezione;
  }

  public boolean getConfermaRicezione() {
    return this.confermaRicezione;
  }

  public void setConfermaRicezione(boolean confermaRicezione) {
    this.confermaRicezione = confermaRicezione;
  }

  public java.lang.Integer getSequenza() {
    return this.sequenza;
  }

  public void setSequenza(java.lang.Integer sequenza) {
    this.sequenza = sequenza;
  }

  private static final long serialVersionUID = 1L;



  @XmlElement(name="inoltro",required=false,nillable=false)
  protected Inoltro inoltro;

  @javax.xml.bind.annotation.XmlSchemaType(name="boolean")
  @XmlAttribute(name="conferma-ricezione",required=false)
  protected boolean confermaRicezione = false;

  @javax.xml.bind.annotation.XmlSchemaType(name="integer")
  @XmlAttribute(name="sequenza",required=false)
  protected java.lang.Integer sequenza;

}
