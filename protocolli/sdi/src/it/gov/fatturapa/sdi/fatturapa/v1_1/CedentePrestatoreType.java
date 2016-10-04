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
package it.gov.fatturapa.sdi.fatturapa.v1_1;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;
import java.io.Serializable;


/** <p>Java class for CedentePrestatoreType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="CedentePrestatoreType">
 * 		&lt;sequence>
 * 			&lt;element name="DatiAnagrafici" type="{http://www.fatturapa.gov.it/sdi/fatturapa/v1.1}DatiAnagraficiCedenteType" minOccurs="1" maxOccurs="1"/>
 * 			&lt;element name="Sede" type="{http://www.fatturapa.gov.it/sdi/fatturapa/v1.1}IndirizzoType" minOccurs="1" maxOccurs="1"/>
 * 			&lt;element name="StabileOrganizzazione" type="{http://www.fatturapa.gov.it/sdi/fatturapa/v1.1}IndirizzoType" minOccurs="0" maxOccurs="1"/>
 * 			&lt;element name="IscrizioneREA" type="{http://www.fatturapa.gov.it/sdi/fatturapa/v1.1}IscrizioneREAType" minOccurs="0" maxOccurs="1"/>
 * 			&lt;element name="Contatti" type="{http://www.fatturapa.gov.it/sdi/fatturapa/v1.1}ContattiType" minOccurs="0" maxOccurs="1"/>
 * 			&lt;element name="RiferimentoAmministrazione" type="{http://www.fatturapa.gov.it/sdi/fatturapa/v1.1}normalizedString" minOccurs="0" maxOccurs="1"/>
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
@XmlType(name = "CedentePrestatoreType", 
  propOrder = {
  	"datiAnagrafici",
  	"sede",
  	"stabileOrganizzazione",
  	"iscrizioneREA",
  	"contatti",
  	"riferimentoAmministrazione"
  }
)

@XmlRootElement(name = "CedentePrestatoreType")

public class CedentePrestatoreType extends org.openspcoop2.utils.beans.BaseBean implements Serializable , Cloneable {
  public CedentePrestatoreType() {
  }

  public Long getId() {
    if(this.id!=null)
		return this.id;
	else
		return new Long(-1);
  }

  public void setId(Long id) {
    if(id!=null)
		this.id=id;
	else
		this.id=new Long(-1);
  }

  public DatiAnagraficiCedenteType getDatiAnagrafici() {
    return this.datiAnagrafici;
  }

  public void setDatiAnagrafici(DatiAnagraficiCedenteType datiAnagrafici) {
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

  public IscrizioneREAType getIscrizioneREA() {
    return this.iscrizioneREA;
  }

  public void setIscrizioneREA(IscrizioneREAType iscrizioneREA) {
    this.iscrizioneREA = iscrizioneREA;
  }

  public ContattiType getContatti() {
    return this.contatti;
  }

  public void setContatti(ContattiType contatti) {
    this.contatti = contatti;
  }

  public java.lang.String getRiferimentoAmministrazione() {
    return this.riferimentoAmministrazione;
  }

  public void setRiferimentoAmministrazione(java.lang.String riferimentoAmministrazione) {
    this.riferimentoAmministrazione = riferimentoAmministrazione;
  }

  private static final long serialVersionUID = 1L;

  @XmlTransient
  private Long id;



  @XmlElement(name="DatiAnagrafici",required=true,nillable=false)
  protected DatiAnagraficiCedenteType datiAnagrafici;

  @XmlElement(name="Sede",required=true,nillable=false)
  protected IndirizzoType sede;

  @XmlElement(name="StabileOrganizzazione",required=false,nillable=false)
  protected IndirizzoType stabileOrganizzazione;

  @XmlElement(name="IscrizioneREA",required=false,nillable=false)
  protected IscrizioneREAType iscrizioneREA;

  @XmlElement(name="Contatti",required=false,nillable=false)
  protected ContattiType contatti;

  @javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter(javax.xml.bind.annotation.adapters.NormalizedStringAdapter.class)
  @javax.xml.bind.annotation.XmlSchemaType(name="normalizedString")
  @XmlElement(name="RiferimentoAmministrazione",required=false,nillable=false)
  protected java.lang.String riferimentoAmministrazione;

}
