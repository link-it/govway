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
package it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0;

import it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0.constants.RegimeFiscaleType;
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
 * 			&lt;element name="IdFiscaleIVA" type="{http://ivaservizi.agenziaentrate.gov.it/docs/xsd/fatture/v1.0}IdFiscaleType" minOccurs="1" maxOccurs="1"/&gt;
 * 			&lt;element name="CodiceFiscale" type="{http://ivaservizi.agenziaentrate.gov.it/docs/xsd/fatture/v1.0}string" minOccurs="0" maxOccurs="1"/&gt;
 * 			&lt;element name="Denominazione" type="{http://ivaservizi.agenziaentrate.gov.it/docs/xsd/fatture/v1.0}normalizedString" minOccurs="1" maxOccurs="1"/&gt;
 * 			&lt;element name="Nome" type="{http://ivaservizi.agenziaentrate.gov.it/docs/xsd/fatture/v1.0}normalizedString" minOccurs="1" maxOccurs="1"/&gt;
 * 			&lt;element name="Cognome" type="{http://ivaservizi.agenziaentrate.gov.it/docs/xsd/fatture/v1.0}normalizedString" minOccurs="1" maxOccurs="1"/&gt;
 * 			&lt;element name="Sede" type="{http://ivaservizi.agenziaentrate.gov.it/docs/xsd/fatture/v1.0}IndirizzoType" minOccurs="1" maxOccurs="1"/&gt;
 * 			&lt;element name="StabileOrganizzazione" type="{http://ivaservizi.agenziaentrate.gov.it/docs/xsd/fatture/v1.0}IndirizzoType" minOccurs="0" maxOccurs="1"/&gt;
 * 			&lt;element name="RappresentanteFiscale" type="{http://ivaservizi.agenziaentrate.gov.it/docs/xsd/fatture/v1.0}RappresentanteFiscaleType" minOccurs="0" maxOccurs="1"/&gt;
 * 			&lt;element name="IscrizioneREA" type="{http://ivaservizi.agenziaentrate.gov.it/docs/xsd/fatture/v1.0}IscrizioneREAType" minOccurs="0" maxOccurs="1"/&gt;
 * 			&lt;element name="RegimeFiscale" type="{http://ivaservizi.agenziaentrate.gov.it/docs/xsd/fatture/v1.0}RegimeFiscaleType" minOccurs="1" maxOccurs="1"/&gt;
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
  	"idFiscaleIVA",
  	"codiceFiscale",
  	"denominazione",
  	"nome",
  	"cognome",
  	"sede",
  	"stabileOrganizzazione",
  	"rappresentanteFiscale",
  	"iscrizioneREA",
  	"regimeFiscale"
  }
)

@XmlRootElement(name = "CedentePrestatoreType")

public class CedentePrestatoreType extends org.openspcoop2.utils.beans.BaseBean implements Serializable , Cloneable {
  public CedentePrestatoreType() {
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

  public java.lang.String getDenominazione() {
    return this.denominazione;
  }

  public void setDenominazione(java.lang.String denominazione) {
    this.denominazione = denominazione;
  }

  public java.lang.String getNome() {
    return this.nome;
  }

  public void setNome(java.lang.String nome) {
    this.nome = nome;
  }

  public java.lang.String getCognome() {
    return this.cognome;
  }

  public void setCognome(java.lang.String cognome) {
    this.cognome = cognome;
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

  public RappresentanteFiscaleType getRappresentanteFiscale() {
    return this.rappresentanteFiscale;
  }

  public void setRappresentanteFiscale(RappresentanteFiscaleType rappresentanteFiscale) {
    this.rappresentanteFiscale = rappresentanteFiscale;
  }

  public IscrizioneREAType getIscrizioneREA() {
    return this.iscrizioneREA;
  }

  public void setIscrizioneREA(IscrizioneREAType iscrizioneREA) {
    this.iscrizioneREA = iscrizioneREA;
  }

  public void set_value_regimeFiscale(String value) {
    this.regimeFiscale = (RegimeFiscaleType) RegimeFiscaleType.toEnumConstantFromString(value);
  }

  public String get_value_regimeFiscale() {
    if(this.regimeFiscale == null){
    	return null;
    }else{
    	return this.regimeFiscale.toString();
    }
  }

  public it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0.constants.RegimeFiscaleType getRegimeFiscale() {
    return this.regimeFiscale;
  }

  public void setRegimeFiscale(it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0.constants.RegimeFiscaleType regimeFiscale) {
    this.regimeFiscale = regimeFiscale;
  }

  private static final long serialVersionUID = 1L;



  @XmlElement(name="IdFiscaleIVA",required=true,nillable=false)
  protected IdFiscaleType idFiscaleIVA;

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="CodiceFiscale",required=false,nillable=false)
  protected java.lang.String codiceFiscale;

  @javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter(javax.xml.bind.annotation.adapters.NormalizedStringAdapter.class)
  @javax.xml.bind.annotation.XmlSchemaType(name="normalizedString")
  @XmlElement(name="Denominazione",required=true,nillable=false)
  protected java.lang.String denominazione;

  @javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter(javax.xml.bind.annotation.adapters.NormalizedStringAdapter.class)
  @javax.xml.bind.annotation.XmlSchemaType(name="normalizedString")
  @XmlElement(name="Nome",required=true,nillable=false)
  protected java.lang.String nome;

  @javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter(javax.xml.bind.annotation.adapters.NormalizedStringAdapter.class)
  @javax.xml.bind.annotation.XmlSchemaType(name="normalizedString")
  @XmlElement(name="Cognome",required=true,nillable=false)
  protected java.lang.String cognome;

  @XmlElement(name="Sede",required=true,nillable=false)
  protected IndirizzoType sede;

  @XmlElement(name="StabileOrganizzazione",required=false,nillable=false)
  protected IndirizzoType stabileOrganizzazione;

  @XmlElement(name="RappresentanteFiscale",required=false,nillable=false)
  protected RappresentanteFiscaleType rappresentanteFiscale;

  @XmlElement(name="IscrizioneREA",required=false,nillable=false)
  protected IscrizioneREAType iscrizioneREA;

  @javax.xml.bind.annotation.XmlTransient
  protected java.lang.String _value_regimeFiscale;

  @XmlElement(name="RegimeFiscale",required=true,nillable=false)
  protected RegimeFiscaleType regimeFiscale;

}
