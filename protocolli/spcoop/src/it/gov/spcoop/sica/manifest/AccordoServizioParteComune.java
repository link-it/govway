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

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;
import java.io.Serializable;


/** <p>Java class for accordoServizioParteComune complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="accordoServizioParteComune"&gt;
 * 		&lt;sequence&gt;
 * 			&lt;element name="specificaInterfaccia" type="{http://spcoop.gov.it/sica/manifest}SpecificaInterfaccia" minOccurs="1" maxOccurs="1"/&gt;
 * 			&lt;element name="specificaConversazione" type="{http://spcoop.gov.it/sica/manifest}SpecificaConversazione" minOccurs="0" maxOccurs="1"/&gt;
 * 		&lt;/sequence&gt;
 * 		&lt;attribute name="pubblicatore" type="{http://spcoop.gov.it/sica/manifest}anyURI" use="optional"/&gt;
 * &lt;/complexType&gt;
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
    super();
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

  @jakarta.xml.bind.annotation.XmlSchemaType(name="anyURI")
  @XmlAttribute(name="pubblicatore",required=false)
  protected java.net.URI pubblicatore;

}
