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
package it.gov.fatturapa.sdi.fatturapa.v1_0;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import java.io.Serializable;


/** <p>Java class for CedentePrestatoreType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="CedentePrestatoreType"&gt;
 * 		&lt;sequence&gt;
 * 			&lt;element name="DatiAnagrafici" type="{http://www.fatturapa.gov.it/sdi/fatturapa/v1.0}DatiAnagraficiCedenteType" minOccurs="1" maxOccurs="1"/&gt;
 * 			&lt;element name="Sede" type="{http://www.fatturapa.gov.it/sdi/fatturapa/v1.0}IndirizzoType" minOccurs="1" maxOccurs="1"/&gt;
 * 			&lt;element name="StabileOrganizzazione" type="{http://www.fatturapa.gov.it/sdi/fatturapa/v1.0}IndirizzoType" minOccurs="0" maxOccurs="1"/&gt;
 * 			&lt;element name="IscrizioneREA" type="{http://www.fatturapa.gov.it/sdi/fatturapa/v1.0}IscrizioneREAType" minOccurs="0" maxOccurs="1"/&gt;
 * 			&lt;element name="Contatti" type="{http://www.fatturapa.gov.it/sdi/fatturapa/v1.0}ContattiType" minOccurs="0" maxOccurs="1"/&gt;
 * 			&lt;element name="RiferimentoAmministrazione" type="{http://www.fatturapa.gov.it/sdi/fatturapa/v1.0}normalizedString" minOccurs="0" maxOccurs="1"/&gt;
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
    super();
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
