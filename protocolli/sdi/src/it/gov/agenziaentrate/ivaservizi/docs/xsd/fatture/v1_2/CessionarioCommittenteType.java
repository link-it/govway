/*
 * OpenSPCoop - Customizable API Gateway 
 * http://www.openspcoop2.org
 * 
 * Copyright (c) 2005-2016 Link.it srl (http://link.it).
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
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
package it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_2;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import java.io.Serializable;


/** <p>Java class for CessionarioCommittenteType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="CessionarioCommittenteType">
 * 		&lt;sequence>
 * 			&lt;element name="DatiAnagrafici" type="{http://ivaservizi.agenziaentrate.gov.it/docs/xsd/fatture/v1.2}DatiAnagraficiCessionarioType" minOccurs="1" maxOccurs="1"/>
 * 			&lt;element name="Sede" type="{http://ivaservizi.agenziaentrate.gov.it/docs/xsd/fatture/v1.2}IndirizzoType" minOccurs="1" maxOccurs="1"/>
 * 			&lt;element name="StabileOrganizzazione" type="{http://ivaservizi.agenziaentrate.gov.it/docs/xsd/fatture/v1.2}IndirizzoType" minOccurs="0" maxOccurs="1"/>
 * 			&lt;element name="RappresentanteFiscale" type="{http://ivaservizi.agenziaentrate.gov.it/docs/xsd/fatture/v1.2}RappresentanteFiscaleCessionarioType" minOccurs="0" maxOccurs="1"/>
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
@XmlType(name = "CessionarioCommittenteType", 
  propOrder = {
  	"datiAnagrafici",
  	"sede",
  	"stabileOrganizzazione",
  	"rappresentanteFiscale"
  }
)

@XmlRootElement(name = "CessionarioCommittenteType")

public class CessionarioCommittenteType extends org.openspcoop2.utils.beans.BaseBean implements Serializable , Cloneable {
  public CessionarioCommittenteType() {
  }

  public DatiAnagraficiCessionarioType getDatiAnagrafici() {
    return this.datiAnagrafici;
  }

  public void setDatiAnagrafici(DatiAnagraficiCessionarioType datiAnagrafici) {
    this.datiAnagrafici = datiAnagrafici;
  }

  public IndirizzoType getSede() {
    return this.sede;
  }

  public void setSede(IndirizzoType sede) {
    this.sede = sede;
  }

  public IndirizzoType getStabileOrganizzazione() {
    return this.stabileOrganizzazione;
  }

  public void setStabileOrganizzazione(IndirizzoType stabileOrganizzazione) {
    this.stabileOrganizzazione = stabileOrganizzazione;
  }

  public RappresentanteFiscaleCessionarioType getRappresentanteFiscale() {
    return this.rappresentanteFiscale;
  }

  public void setRappresentanteFiscale(RappresentanteFiscaleCessionarioType rappresentanteFiscale) {
    this.rappresentanteFiscale = rappresentanteFiscale;
  }

  private static final long serialVersionUID = 1L;



  @XmlElement(name="DatiAnagrafici",required=true,nillable=false)
  protected DatiAnagraficiCessionarioType datiAnagrafici;

  @XmlElement(name="Sede",required=true,nillable=false)
  protected IndirizzoType sede;

  @XmlElement(name="StabileOrganizzazione",required=false,nillable=false)
  protected IndirizzoType stabileOrganizzazione;

  @XmlElement(name="RappresentanteFiscale",required=false,nillable=false)
  protected RappresentanteFiscaleCessionarioType rappresentanteFiscale;

}
