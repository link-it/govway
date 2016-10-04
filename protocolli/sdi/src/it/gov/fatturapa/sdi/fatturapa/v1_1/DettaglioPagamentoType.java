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

import it.gov.fatturapa.sdi.fatturapa.v1_1.constants.ModalitaPagamentoType;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;
import java.io.Serializable;


/** <p>Java class for DettaglioPagamentoType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="DettaglioPagamentoType">
 * 		&lt;sequence>
 * 			&lt;element name="Beneficiario" type="{http://www.fatturapa.gov.it/sdi/fatturapa/v1.1}normalizedString" minOccurs="0" maxOccurs="1"/>
 * 			&lt;element name="ModalitaPagamento" type="{http://www.fatturapa.gov.it/sdi/fatturapa/v1.1}ModalitaPagamentoType" minOccurs="1" maxOccurs="1"/>
 * 			&lt;element name="DataRiferimentoTerminiPagamento" type="{http://www.w3.org/2001/XMLSchema}date" minOccurs="0" maxOccurs="1"/>
 * 			&lt;element name="GiorniTerminiPagamento" type="{http://www.fatturapa.gov.it/sdi/fatturapa/v1.1}integer" minOccurs="0" maxOccurs="1"/>
 * 			&lt;element name="DataScadenzaPagamento" type="{http://www.w3.org/2001/XMLSchema}date" minOccurs="0" maxOccurs="1"/>
 * 			&lt;element name="ImportoPagamento" type="{http://www.fatturapa.gov.it/sdi/fatturapa/v1.1}decimal" minOccurs="1" maxOccurs="1"/>
 * 			&lt;element name="CodUfficioPostale" type="{http://www.fatturapa.gov.it/sdi/fatturapa/v1.1}normalizedString" minOccurs="0" maxOccurs="1"/>
 * 			&lt;element name="CognomeQuietanzante" type="{http://www.fatturapa.gov.it/sdi/fatturapa/v1.1}normalizedString" minOccurs="0" maxOccurs="1"/>
 * 			&lt;element name="NomeQuietanzante" type="{http://www.fatturapa.gov.it/sdi/fatturapa/v1.1}normalizedString" minOccurs="0" maxOccurs="1"/>
 * 			&lt;element name="CFQuietanzante" type="{http://www.fatturapa.gov.it/sdi/fatturapa/v1.1}string" minOccurs="0" maxOccurs="1"/>
 * 			&lt;element name="TitoloQuietanzante" type="{http://www.fatturapa.gov.it/sdi/fatturapa/v1.1}normalizedString" minOccurs="0" maxOccurs="1"/>
 * 			&lt;element name="IstitutoFinanziario" type="{http://www.fatturapa.gov.it/sdi/fatturapa/v1.1}normalizedString" minOccurs="0" maxOccurs="1"/>
 * 			&lt;element name="IBAN" type="{http://www.fatturapa.gov.it/sdi/fatturapa/v1.1}string" minOccurs="0" maxOccurs="1"/>
 * 			&lt;element name="ABI" type="{http://www.fatturapa.gov.it/sdi/fatturapa/v1.1}string" minOccurs="0" maxOccurs="1"/>
 * 			&lt;element name="CAB" type="{http://www.fatturapa.gov.it/sdi/fatturapa/v1.1}string" minOccurs="0" maxOccurs="1"/>
 * 			&lt;element name="BIC" type="{http://www.fatturapa.gov.it/sdi/fatturapa/v1.1}string" minOccurs="0" maxOccurs="1"/>
 * 			&lt;element name="ScontoPagamentoAnticipato" type="{http://www.fatturapa.gov.it/sdi/fatturapa/v1.1}decimal" minOccurs="0" maxOccurs="1"/>
 * 			&lt;element name="DataLimitePagamentoAnticipato" type="{http://www.w3.org/2001/XMLSchema}date" minOccurs="0" maxOccurs="1"/>
 * 			&lt;element name="PenalitaPagamentiRitardati" type="{http://www.fatturapa.gov.it/sdi/fatturapa/v1.1}decimal" minOccurs="0" maxOccurs="1"/>
 * 			&lt;element name="DataDecorrenzaPenale" type="{http://www.w3.org/2001/XMLSchema}date" minOccurs="0" maxOccurs="1"/>
 * 			&lt;element name="CodicePagamento" type="{http://www.fatturapa.gov.it/sdi/fatturapa/v1.1}normalizedString" minOccurs="0" maxOccurs="1"/>
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
@XmlType(name = "DettaglioPagamentoType", 
  propOrder = {
  	"beneficiario",
  	"modalitaPagamento",
  	"dataRiferimentoTerminiPagamento",
  	"giorniTerminiPagamento",
  	"dataScadenzaPagamento",
  	"_decimalWrapper_importoPagamento",
  	"codUfficioPostale",
  	"cognomeQuietanzante",
  	"nomeQuietanzante",
  	"cfQuietanzante",
  	"titoloQuietanzante",
  	"istitutoFinanziario",
  	"iban",
  	"abi",
  	"cab",
  	"bic",
  	"_decimalWrapper_scontoPagamentoAnticipato",
  	"dataLimitePagamentoAnticipato",
  	"_decimalWrapper_penalitaPagamentiRitardati",
  	"dataDecorrenzaPenale",
  	"codicePagamento"
  }
)

@XmlRootElement(name = "DettaglioPagamentoType")

public class DettaglioPagamentoType extends org.openspcoop2.utils.beans.BaseBean implements Serializable , Cloneable {
  public DettaglioPagamentoType() {
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

  public java.lang.String getBeneficiario() {
    return this.beneficiario;
  }

  public void setBeneficiario(java.lang.String beneficiario) {
    this.beneficiario = beneficiario;
  }

  public void set_value_modalitaPagamento(String value) {
    this.modalitaPagamento = (ModalitaPagamentoType) ModalitaPagamentoType.toEnumConstantFromString(value);
  }

  public String get_value_modalitaPagamento() {
    if(this.modalitaPagamento == null){
    	return null;
    }else{
    	return this.modalitaPagamento.toString();
    }
  }

  public it.gov.fatturapa.sdi.fatturapa.v1_1.constants.ModalitaPagamentoType getModalitaPagamento() {
    return this.modalitaPagamento;
  }

  public void setModalitaPagamento(it.gov.fatturapa.sdi.fatturapa.v1_1.constants.ModalitaPagamentoType modalitaPagamento) {
    this.modalitaPagamento = modalitaPagamento;
  }

  public java.util.Date getDataRiferimentoTerminiPagamento() {
    return this.dataRiferimentoTerminiPagamento;
  }

  public void setDataRiferimentoTerminiPagamento(java.util.Date dataRiferimentoTerminiPagamento) {
    this.dataRiferimentoTerminiPagamento = dataRiferimentoTerminiPagamento;
  }

  public java.lang.Integer getGiorniTerminiPagamento() {
    return this.giorniTerminiPagamento;
  }

  public void setGiorniTerminiPagamento(java.lang.Integer giorniTerminiPagamento) {
    this.giorniTerminiPagamento = giorniTerminiPagamento;
  }

  public java.util.Date getDataScadenzaPagamento() {
    return this.dataScadenzaPagamento;
  }

  public void setDataScadenzaPagamento(java.util.Date dataScadenzaPagamento) {
    this.dataScadenzaPagamento = dataScadenzaPagamento;
  }

  public java.lang.Double getImportoPagamento() {
    if(this._decimalWrapper_importoPagamento!=null){
		return (java.lang.Double) this._decimalWrapper_importoPagamento.getObject(java.lang.Double.class);
	}else{
		return this.importoPagamento;
	}
  }

  public void setImportoPagamento(java.lang.Double importoPagamento) {
    if(importoPagamento!=null){
		this._decimalWrapper_importoPagamento = new org.openspcoop2.utils.jaxb.DecimalWrapper(1,11,2,2,importoPagamento);
	}
  }

  public java.lang.String getCodUfficioPostale() {
    return this.codUfficioPostale;
  }

  public void setCodUfficioPostale(java.lang.String codUfficioPostale) {
    this.codUfficioPostale = codUfficioPostale;
  }

  public java.lang.String getCognomeQuietanzante() {
    return this.cognomeQuietanzante;
  }

  public void setCognomeQuietanzante(java.lang.String cognomeQuietanzante) {
    this.cognomeQuietanzante = cognomeQuietanzante;
  }

  public java.lang.String getNomeQuietanzante() {
    return this.nomeQuietanzante;
  }

  public void setNomeQuietanzante(java.lang.String nomeQuietanzante) {
    this.nomeQuietanzante = nomeQuietanzante;
  }

  public java.lang.String getCfQuietanzante() {
    return this.cfQuietanzante;
  }

  public void setCfQuietanzante(java.lang.String cfQuietanzante) {
    this.cfQuietanzante = cfQuietanzante;
  }

  public java.lang.String getTitoloQuietanzante() {
    return this.titoloQuietanzante;
  }

  public void setTitoloQuietanzante(java.lang.String titoloQuietanzante) {
    this.titoloQuietanzante = titoloQuietanzante;
  }

  public java.lang.String getIstitutoFinanziario() {
    return this.istitutoFinanziario;
  }

  public void setIstitutoFinanziario(java.lang.String istitutoFinanziario) {
    this.istitutoFinanziario = istitutoFinanziario;
  }

  public java.lang.String getIban() {
    return this.iban;
  }

  public void setIban(java.lang.String iban) {
    this.iban = iban;
  }

  public java.lang.String getAbi() {
    return this.abi;
  }

  public void setAbi(java.lang.String abi) {
    this.abi = abi;
  }

  public java.lang.String getCab() {
    return this.cab;
  }

  public void setCab(java.lang.String cab) {
    this.cab = cab;
  }

  public java.lang.String getBic() {
    return this.bic;
  }

  public void setBic(java.lang.String bic) {
    this.bic = bic;
  }

  public java.lang.Double getScontoPagamentoAnticipato() {
    if(this._decimalWrapper_scontoPagamentoAnticipato!=null){
		return (java.lang.Double) this._decimalWrapper_scontoPagamentoAnticipato.getObject(java.lang.Double.class);
	}else{
		return this.scontoPagamentoAnticipato;
	}
  }

  public void setScontoPagamentoAnticipato(java.lang.Double scontoPagamentoAnticipato) {
    if(scontoPagamentoAnticipato!=null){
		this._decimalWrapper_scontoPagamentoAnticipato = new org.openspcoop2.utils.jaxb.DecimalWrapper(1,11,2,2,scontoPagamentoAnticipato);
	}
  }

  public java.util.Date getDataLimitePagamentoAnticipato() {
    return this.dataLimitePagamentoAnticipato;
  }

  public void setDataLimitePagamentoAnticipato(java.util.Date dataLimitePagamentoAnticipato) {
    this.dataLimitePagamentoAnticipato = dataLimitePagamentoAnticipato;
  }

  public java.lang.Double getPenalitaPagamentiRitardati() {
    if(this._decimalWrapper_penalitaPagamentiRitardati!=null){
		return (java.lang.Double) this._decimalWrapper_penalitaPagamentiRitardati.getObject(java.lang.Double.class);
	}else{
		return this.penalitaPagamentiRitardati;
	}
  }

  public void setPenalitaPagamentiRitardati(java.lang.Double penalitaPagamentiRitardati) {
    if(penalitaPagamentiRitardati!=null){
		this._decimalWrapper_penalitaPagamentiRitardati = new org.openspcoop2.utils.jaxb.DecimalWrapper(1,11,2,2,penalitaPagamentiRitardati);
	}
  }

  public java.util.Date getDataDecorrenzaPenale() {
    return this.dataDecorrenzaPenale;
  }

  public void setDataDecorrenzaPenale(java.util.Date dataDecorrenzaPenale) {
    this.dataDecorrenzaPenale = dataDecorrenzaPenale;
  }

  public java.lang.String getCodicePagamento() {
    return this.codicePagamento;
  }

  public void setCodicePagamento(java.lang.String codicePagamento) {
    this.codicePagamento = codicePagamento;
  }

  private static final long serialVersionUID = 1L;

  @XmlTransient
  private Long id;



  @javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter(javax.xml.bind.annotation.adapters.NormalizedStringAdapter.class)
  @javax.xml.bind.annotation.XmlSchemaType(name="normalizedString")
  @XmlElement(name="Beneficiario",required=false,nillable=false)
  protected java.lang.String beneficiario;

  @XmlTransient
  protected java.lang.String _value_modalitaPagamento;

  @XmlElement(name="ModalitaPagamento",required=true,nillable=false)
  protected ModalitaPagamentoType modalitaPagamento;

  @javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter(org.openspcoop2.utils.jaxb.Date2String.class)
  @javax.xml.bind.annotation.XmlSchemaType(name="date")
  @XmlElement(name="DataRiferimentoTerminiPagamento",required=false,nillable=false,type=java.lang.String.class)
  protected java.util.Date dataRiferimentoTerminiPagamento;

  @javax.xml.bind.annotation.XmlSchemaType(name="integer")
  @XmlElement(name="GiorniTerminiPagamento",required=false,nillable=false)
  protected java.lang.Integer giorniTerminiPagamento;

  @javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter(org.openspcoop2.utils.jaxb.Date2String.class)
  @javax.xml.bind.annotation.XmlSchemaType(name="date")
  @XmlElement(name="DataScadenzaPagamento",required=false,nillable=false,type=java.lang.String.class)
  protected java.util.Date dataScadenzaPagamento;

  @javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter(org.openspcoop2.utils.jaxb.Decimal2String.class)
  @javax.xml.bind.annotation.XmlSchemaType(name="decimal")
  @XmlElement(name="ImportoPagamento",required=true,nillable=false)
  org.openspcoop2.utils.jaxb.DecimalWrapper _decimalWrapper_importoPagamento = null;

  @XmlTransient
  protected java.lang.Double importoPagamento;

  @javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter(javax.xml.bind.annotation.adapters.NormalizedStringAdapter.class)
  @javax.xml.bind.annotation.XmlSchemaType(name="normalizedString")
  @XmlElement(name="CodUfficioPostale",required=false,nillable=false)
  protected java.lang.String codUfficioPostale;

  @javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter(javax.xml.bind.annotation.adapters.NormalizedStringAdapter.class)
  @javax.xml.bind.annotation.XmlSchemaType(name="normalizedString")
  @XmlElement(name="CognomeQuietanzante",required=false,nillable=false)
  protected java.lang.String cognomeQuietanzante;

  @javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter(javax.xml.bind.annotation.adapters.NormalizedStringAdapter.class)
  @javax.xml.bind.annotation.XmlSchemaType(name="normalizedString")
  @XmlElement(name="NomeQuietanzante",required=false,nillable=false)
  protected java.lang.String nomeQuietanzante;

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="CFQuietanzante",required=false,nillable=false)
  protected java.lang.String cfQuietanzante;

  @javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter(javax.xml.bind.annotation.adapters.NormalizedStringAdapter.class)
  @javax.xml.bind.annotation.XmlSchemaType(name="normalizedString")
  @XmlElement(name="TitoloQuietanzante",required=false,nillable=false)
  protected java.lang.String titoloQuietanzante;

  @javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter(javax.xml.bind.annotation.adapters.NormalizedStringAdapter.class)
  @javax.xml.bind.annotation.XmlSchemaType(name="normalizedString")
  @XmlElement(name="IstitutoFinanziario",required=false,nillable=false)
  protected java.lang.String istitutoFinanziario;

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="IBAN",required=false,nillable=false)
  protected java.lang.String iban;

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="ABI",required=false,nillable=false)
  protected java.lang.String abi;

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="CAB",required=false,nillable=false)
  protected java.lang.String cab;

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="BIC",required=false,nillable=false)
  protected java.lang.String bic;

  @javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter(org.openspcoop2.utils.jaxb.Decimal2String.class)
  @javax.xml.bind.annotation.XmlSchemaType(name="decimal")
  @XmlElement(name="ScontoPagamentoAnticipato",required=false,nillable=false)
  org.openspcoop2.utils.jaxb.DecimalWrapper _decimalWrapper_scontoPagamentoAnticipato = null;

  @XmlTransient
  protected java.lang.Double scontoPagamentoAnticipato;

  @javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter(org.openspcoop2.utils.jaxb.Date2String.class)
  @javax.xml.bind.annotation.XmlSchemaType(name="date")
  @XmlElement(name="DataLimitePagamentoAnticipato",required=false,nillable=false,type=java.lang.String.class)
  protected java.util.Date dataLimitePagamentoAnticipato;

  @javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter(org.openspcoop2.utils.jaxb.Decimal2String.class)
  @javax.xml.bind.annotation.XmlSchemaType(name="decimal")
  @XmlElement(name="PenalitaPagamentiRitardati",required=false,nillable=false)
  org.openspcoop2.utils.jaxb.DecimalWrapper _decimalWrapper_penalitaPagamentiRitardati = null;

  @XmlTransient
  protected java.lang.Double penalitaPagamentiRitardati;

  @javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter(org.openspcoop2.utils.jaxb.Date2String.class)
  @javax.xml.bind.annotation.XmlSchemaType(name="date")
  @XmlElement(name="DataDecorrenzaPenale",required=false,nillable=false,type=java.lang.String.class)
  protected java.util.Date dataDecorrenzaPenale;

  @javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter(javax.xml.bind.annotation.adapters.NormalizedStringAdapter.class)
  @javax.xml.bind.annotation.XmlSchemaType(name="normalizedString")
  @XmlElement(name="CodicePagamento",required=false,nillable=false)
  protected java.lang.String codicePagamento;

}
