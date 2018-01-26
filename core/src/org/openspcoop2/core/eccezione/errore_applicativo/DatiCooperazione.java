/*
 * OpenSPCoop - Customizable API Gateway 
 * http://www.openspcoop2.org
 * 
 * Copyright (c) 2005-2018 Link.it srl (http://link.it).
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
package org.openspcoop2.core.eccezione.errore_applicativo;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import java.io.Serializable;


/** <p>Java class for dati-cooperazione complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="dati-cooperazione">
 * 		&lt;sequence>
 * 			&lt;element name="fruitore" type="{http://www.openspcoop2.org/core/eccezione/errore_applicativo}soggetto" minOccurs="0" maxOccurs="1"/>
 * 			&lt;element name="erogatore" type="{http://www.openspcoop2.org/core/eccezione/errore_applicativo}soggetto" minOccurs="0" maxOccurs="1"/>
 * 			&lt;element name="servizio" type="{http://www.openspcoop2.org/core/eccezione/errore_applicativo}servizio" minOccurs="0" maxOccurs="1"/>
 * 			&lt;element name="azione" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0" maxOccurs="1"/>
 * 			&lt;element name="servizio-applicativo" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0" maxOccurs="1"/>
 * 		&lt;/sequence>
 * &lt;/complexType>
 * </pre>
 * 
 * @version $Rev$, $Date$
 * 
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * */

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "dati-cooperazione", 
  propOrder = {
  	"fruitore",
  	"erogatore",
  	"servizio",
  	"azione",
  	"servizioApplicativo"
  }
)

@XmlRootElement(name = "dati-cooperazione")

public class DatiCooperazione extends org.openspcoop2.utils.beans.BaseBean implements Serializable , Cloneable {
  public DatiCooperazione() {
  }

  public Soggetto getFruitore() {
    return this.fruitore;
  }

  public void setFruitore(Soggetto fruitore) {
    this.fruitore = fruitore;
  }

  public Soggetto getErogatore() {
    return this.erogatore;
  }

  public void setErogatore(Soggetto erogatore) {
    this.erogatore = erogatore;
  }

  public Servizio getServizio() {
    return this.servizio;
  }

  public void setServizio(Servizio servizio) {
    this.servizio = servizio;
  }

  public java.lang.String getAzione() {
    return this.azione;
  }

  public void setAzione(java.lang.String azione) {
    this.azione = azione;
  }

  public java.lang.String getServizioApplicativo() {
    return this.servizioApplicativo;
  }

  public void setServizioApplicativo(java.lang.String servizioApplicativo) {
    this.servizioApplicativo = servizioApplicativo;
  }

  private static final long serialVersionUID = 1L;



  @XmlElement(name="fruitore",required=false,nillable=false)
  protected Soggetto fruitore;

  @XmlElement(name="erogatore",required=false,nillable=false)
  protected Soggetto erogatore;

  @XmlElement(name="servizio",required=false,nillable=false)
  protected Servizio servizio;

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="azione",required=false,nillable=false)
  protected java.lang.String azione;

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="servizio-applicativo",required=false,nillable=false)
  protected java.lang.String servizioApplicativo;

}
