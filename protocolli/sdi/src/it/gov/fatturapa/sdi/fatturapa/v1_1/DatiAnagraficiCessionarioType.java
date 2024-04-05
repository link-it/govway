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
package it.gov.fatturapa.sdi.fatturapa.v1_1;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;
import java.io.Serializable;


/** <p>Java class for DatiAnagraficiCessionarioType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="DatiAnagraficiCessionarioType"&gt;
 * 		&lt;sequence&gt;
 * 			&lt;element name="IdFiscaleIVA" type="{http://www.fatturapa.gov.it/sdi/fatturapa/v1.1}IdFiscaleType" minOccurs="0" maxOccurs="1"/&gt;
 * 			&lt;element name="CodiceFiscale" type="{http://www.fatturapa.gov.it/sdi/fatturapa/v1.1}string" minOccurs="0" maxOccurs="1"/&gt;
 * 			&lt;element name="Anagrafica" type="{http://www.fatturapa.gov.it/sdi/fatturapa/v1.1}AnagraficaType" minOccurs="1" maxOccurs="1"/&gt;
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
@XmlType(name = "DatiAnagraficiCessionarioType", 
  propOrder = {
  	"idFiscaleIVA",
  	"codiceFiscale",
  	"anagrafica"
  }
)

@XmlRootElement(name = "DatiAnagraficiCessionarioType")

public class DatiAnagraficiCessionarioType extends org.openspcoop2.utils.beans.BaseBean implements Serializable , Cloneable {
  public DatiAnagraficiCessionarioType() {
    super();
  }

  public IdFiscaleType getIdFiscaleIVA() {
    return this.idFiscaleIVA;
  }

  public void setIdFiscaleIVA(IdFiscaleType idFiscaleIVA) {
    this.idFiscaleIVA = idFiscaleIVA;
  }

  public java.lang.String getCodiceFiscale() {
    return this.codiceFiscale;
  }

  public void setCodiceFiscale(java.lang.String codiceFiscale) {
    this.codiceFiscale = codiceFiscale;
  }

  public AnagraficaType getAnagrafica() {
    return this.anagrafica;
  }

  public void setAnagrafica(AnagraficaType anagrafica) {
    this.anagrafica = anagrafica;
  }

  private static final long serialVersionUID = 1L;



  @XmlElement(name="IdFiscaleIVA",required=false,nillable=false)
  protected IdFiscaleType idFiscaleIVA;

  @jakarta.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="CodiceFiscale",required=false,nillable=false)
  protected java.lang.String codiceFiscale;

  @XmlElement(name="Anagrafica",required=true,nillable=false)
  protected AnagraficaType anagrafica;

}
