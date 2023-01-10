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
package it.gov.fatturapa.sdi.fatturapa.v1_0;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import java.io.Serializable;


/** <p>Java class for DatiTrasportoType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="DatiTrasportoType"&gt;
 * 		&lt;sequence&gt;
 * 			&lt;element name="DatiAnagraficiVettore" type="{http://www.fatturapa.gov.it/sdi/fatturapa/v1.0}DatiAnagraficiVettoreType" minOccurs="0" maxOccurs="1"/&gt;
 * 			&lt;element name="MezzoTrasporto" type="{http://www.fatturapa.gov.it/sdi/fatturapa/v1.0}normalizedString" minOccurs="0" maxOccurs="1"/&gt;
 * 			&lt;element name="CausaleTrasporto" type="{http://www.fatturapa.gov.it/sdi/fatturapa/v1.0}normalizedString" minOccurs="0" maxOccurs="1"/&gt;
 * 			&lt;element name="NumeroColli" type="{http://www.fatturapa.gov.it/sdi/fatturapa/v1.0}integer" minOccurs="0" maxOccurs="1"/&gt;
 * 			&lt;element name="Descrizione" type="{http://www.fatturapa.gov.it/sdi/fatturapa/v1.0}normalizedString" minOccurs="0" maxOccurs="1"/&gt;
 * 			&lt;element name="UnitaMisuraPeso" type="{http://www.fatturapa.gov.it/sdi/fatturapa/v1.0}normalizedString" minOccurs="0" maxOccurs="1"/&gt;
 * 			&lt;element name="PesoLordo" type="{http://www.fatturapa.gov.it/sdi/fatturapa/v1.0}decimal" minOccurs="0" maxOccurs="1"/&gt;
 * 			&lt;element name="PesoNetto" type="{http://www.fatturapa.gov.it/sdi/fatturapa/v1.0}decimal" minOccurs="0" maxOccurs="1"/&gt;
 * 			&lt;element name="DataOraRitiro" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0" maxOccurs="1"/&gt;
 * 			&lt;element name="DataInizioTrasporto" type="{http://www.w3.org/2001/XMLSchema}date" minOccurs="0" maxOccurs="1"/&gt;
 * 			&lt;element name="TipoResa" type="{http://www.fatturapa.gov.it/sdi/fatturapa/v1.0}string" minOccurs="0" maxOccurs="1"/&gt;
 * 			&lt;element name="IndirizzoResa" type="{http://www.fatturapa.gov.it/sdi/fatturapa/v1.0}IndirizzoType" minOccurs="0" maxOccurs="1"/&gt;
 * 			&lt;element name="DataOraConsegna" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0" maxOccurs="1"/&gt;
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
@XmlType(name = "DatiTrasportoType", 
  propOrder = {
  	"datiAnagraficiVettore",
  	"mezzoTrasporto",
  	"causaleTrasporto",
  	"numeroColli",
  	"descrizione",
  	"unitaMisuraPeso",
  	"_decimalWrapper_pesoLordo",
  	"_decimalWrapper_pesoNetto",
  	"dataOraRitiro",
  	"dataInizioTrasporto",
  	"tipoResa",
  	"indirizzoResa",
  	"dataOraConsegna"
  }
)

@XmlRootElement(name = "DatiTrasportoType")

public class DatiTrasportoType extends org.openspcoop2.utils.beans.BaseBean implements Serializable , Cloneable {
  public DatiTrasportoType() {
  }

  public DatiAnagraficiVettoreType getDatiAnagraficiVettore() {
    return this.datiAnagraficiVettore;
  }

  public void setDatiAnagraficiVettore(DatiAnagraficiVettoreType datiAnagraficiVettore) {
    this.datiAnagraficiVettore = datiAnagraficiVettore;
  }

  public java.lang.String getMezzoTrasporto() {
    return this.mezzoTrasporto;
  }

  public void setMezzoTrasporto(java.lang.String mezzoTrasporto) {
    this.mezzoTrasporto = mezzoTrasporto;
  }

  public java.lang.String getCausaleTrasporto() {
    return this.causaleTrasporto;
  }

  public void setCausaleTrasporto(java.lang.String causaleTrasporto) {
    this.causaleTrasporto = causaleTrasporto;
  }

  public java.math.BigInteger getNumeroColli() {
    return this.numeroColli;
  }

  public void setNumeroColli(java.math.BigInteger numeroColli) {
    this.numeroColli = numeroColli;
  }

  public java.lang.String getDescrizione() {
    return this.descrizione;
  }

  public void setDescrizione(java.lang.String descrizione) {
    this.descrizione = descrizione;
  }

  public java.lang.String getUnitaMisuraPeso() {
    return this.unitaMisuraPeso;
  }

  public void setUnitaMisuraPeso(java.lang.String unitaMisuraPeso) {
    this.unitaMisuraPeso = unitaMisuraPeso;
  }

  public java.math.BigDecimal getPesoLordo() {
    if(this._decimalWrapper_pesoLordo!=null){
		return (java.math.BigDecimal) this._decimalWrapper_pesoLordo.getObject(java.math.BigDecimal.class);
	}else{
		return this.pesoLordo;
	}
  }

  public void setPesoLordo(java.math.BigDecimal pesoLordo) {
    if(pesoLordo!=null){
		this._decimalWrapper_pesoLordo = new org.openspcoop2.utils.jaxb.DecimalWrapper(1,4,1,2,pesoLordo);
	}
  }

  public java.math.BigDecimal getPesoNetto() {
    if(this._decimalWrapper_pesoNetto!=null){
		return (java.math.BigDecimal) this._decimalWrapper_pesoNetto.getObject(java.math.BigDecimal.class);
	}else{
		return this.pesoNetto;
	}
  }

  public void setPesoNetto(java.math.BigDecimal pesoNetto) {
    if(pesoNetto!=null){
		this._decimalWrapper_pesoNetto = new org.openspcoop2.utils.jaxb.DecimalWrapper(1,4,1,2,pesoNetto);
	}
  }

  public java.util.Date getDataOraRitiro() {
    return this.dataOraRitiro;
  }

  public void setDataOraRitiro(java.util.Date dataOraRitiro) {
    this.dataOraRitiro = dataOraRitiro;
  }

  public java.util.Date getDataInizioTrasporto() {
    return this.dataInizioTrasporto;
  }

  public void setDataInizioTrasporto(java.util.Date dataInizioTrasporto) {
    this.dataInizioTrasporto = dataInizioTrasporto;
  }

  public java.lang.String getTipoResa() {
    return this.tipoResa;
  }

  public void setTipoResa(java.lang.String tipoResa) {
    this.tipoResa = tipoResa;
  }

  public IndirizzoType getIndirizzoResa() {
    return this.indirizzoResa;
  }

  public void setIndirizzoResa(IndirizzoType indirizzoResa) {
    this.indirizzoResa = indirizzoResa;
  }

  public java.util.Date getDataOraConsegna() {
    return this.dataOraConsegna;
  }

  public void setDataOraConsegna(java.util.Date dataOraConsegna) {
    this.dataOraConsegna = dataOraConsegna;
  }

  private static final long serialVersionUID = 1L;



  @XmlElement(name="DatiAnagraficiVettore",required=false,nillable=false)
  protected DatiAnagraficiVettoreType datiAnagraficiVettore;

  @javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter(javax.xml.bind.annotation.adapters.NormalizedStringAdapter.class)
  @javax.xml.bind.annotation.XmlSchemaType(name="normalizedString")
  @XmlElement(name="MezzoTrasporto",required=false,nillable=false)
  protected java.lang.String mezzoTrasporto;

  @javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter(javax.xml.bind.annotation.adapters.NormalizedStringAdapter.class)
  @javax.xml.bind.annotation.XmlSchemaType(name="normalizedString")
  @XmlElement(name="CausaleTrasporto",required=false,nillable=false)
  protected java.lang.String causaleTrasporto;

  @javax.xml.bind.annotation.XmlSchemaType(name="integer")
  @XmlElement(name="NumeroColli",required=false,nillable=false)
  protected java.math.BigInteger numeroColli;

  @javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter(javax.xml.bind.annotation.adapters.NormalizedStringAdapter.class)
  @javax.xml.bind.annotation.XmlSchemaType(name="normalizedString")
  @XmlElement(name="Descrizione",required=false,nillable=false)
  protected java.lang.String descrizione;

  @javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter(javax.xml.bind.annotation.adapters.NormalizedStringAdapter.class)
  @javax.xml.bind.annotation.XmlSchemaType(name="normalizedString")
  @XmlElement(name="UnitaMisuraPeso",required=false,nillable=false)
  protected java.lang.String unitaMisuraPeso;

  @javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter(org.openspcoop2.utils.jaxb.Decimal2String.class)
  @javax.xml.bind.annotation.XmlSchemaType(name="decimal")
  @XmlElement(name="PesoLordo",required=false,nillable=false)
  org.openspcoop2.utils.jaxb.DecimalWrapper _decimalWrapper_pesoLordo = null;

  @javax.xml.bind.annotation.XmlTransient
  protected java.math.BigDecimal pesoLordo;

  @javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter(org.openspcoop2.utils.jaxb.Decimal2String.class)
  @javax.xml.bind.annotation.XmlSchemaType(name="decimal")
  @XmlElement(name="PesoNetto",required=false,nillable=false)
  org.openspcoop2.utils.jaxb.DecimalWrapper _decimalWrapper_pesoNetto = null;

  @javax.xml.bind.annotation.XmlTransient
  protected java.math.BigDecimal pesoNetto;

  @javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter(org.openspcoop2.utils.jaxb.DateTime2String.class)
  @javax.xml.bind.annotation.XmlSchemaType(name="dateTime")
  @XmlElement(name="DataOraRitiro",required=false,nillable=false,type=java.lang.String.class)
  protected java.util.Date dataOraRitiro;

  @javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter(org.openspcoop2.utils.jaxb.Date2String.class)
  @javax.xml.bind.annotation.XmlSchemaType(name="date")
  @XmlElement(name="DataInizioTrasporto",required=false,nillable=false,type=java.lang.String.class)
  protected java.util.Date dataInizioTrasporto;

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="TipoResa",required=false,nillable=false)
  protected java.lang.String tipoResa;

  @XmlElement(name="IndirizzoResa",required=false,nillable=false)
  protected IndirizzoType indirizzoResa;

  @javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter(org.openspcoop2.utils.jaxb.DateTime2String.class)
  @javax.xml.bind.annotation.XmlSchemaType(name="dateTime")
  @XmlElement(name="DataOraConsegna",required=false,nillable=false,type=java.lang.String.class)
  protected java.util.Date dataOraConsegna;

}
