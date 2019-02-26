/*
 * GovWay - A customizable API Gateway 
 * http://www.govway.org
 *
 * from the Link.it OpenSPCoop project codebase
 * 
 * Copyright (c) 2005-2019 Link.it srl (http://link.it).
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
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import java.io.Serializable;


/** <p>Java class for accordoServizioParteComune complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="accordoServizioParteComune">
 * 		&lt;sequence>
 * 			&lt;element name="specificaInterfaccia" type="{http://spcoop.gov.it/sica/manifest}SpecificaInterfaccia" minOccurs="1" maxOccurs="1"/>
 * 			&lt;element name="specificaConversazione" type="{http://spcoop.gov.it/sica/manifest}SpecificaConversazione" minOccurs="0" maxOccurs="1"/>
 * 		&lt;/sequence>
 * 		&lt;attribute name="pubblicatore" type="{http://spcoop.gov.it/sica/manifest}anyURI" use="optional"/>
 * &lt;/complexType>
 * </pre>
 * 
 * @version $Rev$, $Date$
 * 
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * */

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "accordoServizioParteComune", 
  propOrder = {
  	"specificaInterfaccia",
  	"specificaConversazione"
  }
)

@XmlRootElement(name = "accordoServizioParteComune")

public class AccordoServizioParteComune extends org.openspcoop2.utils.beans.BaseBean implements Serializable , Cloneable {
  public AccordoServizioParteComune() {
  }

  public SpecificaInterfaccia getSpecificaInterfaccia() {
    return this.specificaInterfaccia;
  }

  public void setSpecificaInterfaccia(SpecificaInterfaccia specificaInterfaccia) {
    this.specificaInterfaccia = specificaInterfaccia;
  }

  public SpecificaConversazione getSpecificaConversazione() {
    return this.specificaConversazione;
  }

  public void setSpecificaConversazione(SpecificaConversazione specificaConversazione) {
    this.specificaConversazione = specificaConversazione;
  }

  public java.net.URI getPubblicatore() {
    return this.pubblicatore;
  }

  public void setPubblicatore(java.net.URI pubblicatore) {
    this.pubblicatore = pubblicatore;
  }

  private static final long serialVersionUID = 1L;



  @XmlElement(name="specificaInterfaccia",required=true,nillable=false)
  protected SpecificaInterfaccia specificaInterfaccia;

  @XmlElement(name="specificaConversazione",required=false,nillable=false)
  protected SpecificaConversazione specificaConversazione;

  @javax.xml.bind.annotation.XmlSchemaType(name="anyURI")
  @XmlAttribute(name="pubblicatore",required=false)
  protected java.net.URI pubblicatore;

}
