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
package it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_2;

import it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_2.constants.EsigibilitaIVAType;
import it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_2.constants.NaturaType;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import java.io.Serializable;


/** <p>Java class for DatiRiepilogoType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="DatiRiepilogoType">
 * 		&lt;sequence>
 * 			&lt;element name="AliquotaIVA" type="{http://ivaservizi.agenziaentrate.gov.it/docs/xsd/fatture/v1.2}decimal" minOccurs="1" maxOccurs="1"/>
 * 			&lt;element name="Natura" type="{http://ivaservizi.agenziaentrate.gov.it/docs/xsd/fatture/v1.2}NaturaType" minOccurs="0" maxOccurs="1"/>
 * 			&lt;element name="SpeseAccessorie" type="{http://ivaservizi.agenziaentrate.gov.it/docs/xsd/fatture/v1.2}decimal" minOccurs="0" maxOccurs="1"/>
 * 			&lt;element name="Arrotondamento" type="{http://ivaservizi.agenziaentrate.gov.it/docs/xsd/fatture/v1.2}decimal" minOccurs="0" maxOccurs="1"/>
 * 			&lt;element name="ImponibileImporto" type="{http://ivaservizi.agenziaentrate.gov.it/docs/xsd/fatture/v1.2}decimal" minOccurs="1" maxOccurs="1"/>
 * 			&lt;element name="Imposta" type="{http://ivaservizi.agenziaentrate.gov.it/docs/xsd/fatture/v1.2}decimal" minOccurs="1" maxOccurs="1"/>
 * 			&lt;element name="EsigibilitaIVA" type="{http://ivaservizi.agenziaentrate.gov.it/docs/xsd/fatture/v1.2}EsigibilitaIVAType" minOccurs="0" maxOccurs="1"/>
 * 			&lt;element name="RiferimentoNormativo" type="{http://ivaservizi.agenziaentrate.gov.it/docs/xsd/fatture/v1.2}normalizedString" minOccurs="0" maxOccurs="1"/>
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
@XmlType(name = "DatiRiepilogoType", 
  propOrder = {
  	"_decimalWrapper_aliquotaIVA",
  	"natura",
  	"_decimalWrapper_speseAccessorie",
  	"_decimalWrapper_arrotondamento",
  	"_decimalWrapper_imponibileImporto",
  	"_decimalWrapper_imposta",
  	"esigibilitaIVA",
  	"riferimentoNormativo"
  }
)

@XmlRootElement(name = "DatiRiepilogoType")

public class DatiRiepilogoType extends org.openspcoop2.utils.beans.BaseBean implements Serializable , Cloneable {
  public DatiRiepilogoType() {
  }

  public java.math.BigDecimal getAliquotaIVA() {
    if(this._decimalWrapper_aliquotaIVA!=null){
		return (java.math.BigDecimal) this._decimalWrapper_aliquotaIVA.getObject(java.math.BigDecimal.class);
	}else{
		return this.aliquotaIVA;
	}
  }

  public void setAliquotaIVA(java.math.BigDecimal aliquotaIVA) {
    if(aliquotaIVA!=null){
		this._decimalWrapper_aliquotaIVA = new org.openspcoop2.utils.jaxb.DecimalWrapper(1,3,2,2,aliquotaIVA);
	}
  }

  public void set_value_natura(String value) {
    this.natura = (NaturaType) NaturaType.toEnumConstantFromString(value);
  }

  public String get_value_natura() {
    if(this.natura == null){
    	return null;
    }else{
    	return this.natura.toString();
    }
  }

  public it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_2.constants.NaturaType getNatura() {
    return this.natura;
  }

  public void setNatura(it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_2.constants.NaturaType natura) {
    this.natura = natura;
  }

  public java.math.BigDecimal getSpeseAccessorie() {
    if(this._decimalWrapper_speseAccessorie!=null){
		return (java.math.BigDecimal) this._decimalWrapper_speseAccessorie.getObject(java.math.BigDecimal.class);
	}else{
		return this.speseAccessorie;
	}
  }

  public void setSpeseAccessorie(java.math.BigDecimal speseAccessorie) {
    if(speseAccessorie!=null){
		this._decimalWrapper_speseAccessorie = new org.openspcoop2.utils.jaxb.DecimalWrapper(1,11,2,2,speseAccessorie);
	}
  }

  public java.math.BigDecimal getArrotondamento() {
    if(this._decimalWrapper_arrotondamento!=null){
		return (java.math.BigDecimal) this._decimalWrapper_arrotondamento.getObject(java.math.BigDecimal.class);
	}else{
		return this.arrotondamento;
	}
  }

  public void setArrotondamento(java.math.BigDecimal arrotondamento) {
    if(arrotondamento!=null){
		this._decimalWrapper_arrotondamento = new org.openspcoop2.utils.jaxb.DecimalWrapper(1,11,2,8,arrotondamento);
	}
  }

  public java.math.BigDecimal getImponibileImporto() {
    if(this._decimalWrapper_imponibileImporto!=null){
		return (java.math.BigDecimal) this._decimalWrapper_imponibileImporto.getObject(java.math.BigDecimal.class);
	}else{
		return this.imponibileImporto;
	}
  }

  public void setImponibileImporto(java.math.BigDecimal imponibileImporto) {
    if(imponibileImporto!=null){
		this._decimalWrapper_imponibileImporto = new org.openspcoop2.utils.jaxb.DecimalWrapper(1,11,2,2,imponibileImporto);
	}
  }

  public java.math.BigDecimal getImposta() {
    if(this._decimalWrapper_imposta!=null){
		return (java.math.BigDecimal) this._decimalWrapper_imposta.getObject(java.math.BigDecimal.class);
	}else{
		return this.imposta;
	}
  }

  public void setImposta(java.math.BigDecimal imposta) {
    if(imposta!=null){
		this._decimalWrapper_imposta = new org.openspcoop2.utils.jaxb.DecimalWrapper(1,11,2,2,imposta);
	}
  }

  public void set_value_esigibilitaIVA(String value) {
    this.esigibilitaIVA = (EsigibilitaIVAType) EsigibilitaIVAType.toEnumConstantFromString(value);
  }

  public String get_value_esigibilitaIVA() {
    if(this.esigibilitaIVA == null){
    	return null;
    }else{
    	return this.esigibilitaIVA.toString();
    }
  }

  public it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_2.constants.EsigibilitaIVAType getEsigibilitaIVA() {
    return this.esigibilitaIVA;
  }

  public void setEsigibilitaIVA(it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_2.constants.EsigibilitaIVAType esigibilitaIVA) {
    this.esigibilitaIVA = esigibilitaIVA;
  }

  public java.lang.String getRiferimentoNormativo() {
    return this.riferimentoNormativo;
  }

  public void setRiferimentoNormativo(java.lang.String riferimentoNormativo) {
    this.riferimentoNormativo = riferimentoNormativo;
  }

  private static final long serialVersionUID = 1L;



  @javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter(org.openspcoop2.utils.jaxb.Decimal2String.class)
  @javax.xml.bind.annotation.XmlSchemaType(name="decimal")
  @XmlElement(name="AliquotaIVA",required=true,nillable=false)
  org.openspcoop2.utils.jaxb.DecimalWrapper _decimalWrapper_aliquotaIVA = null;

  @javax.xml.bind.annotation.XmlTransient
  protected java.math.BigDecimal aliquotaIVA;

  @javax.xml.bind.annotation.XmlTransient
  protected java.lang.String _value_natura;

  @XmlElement(name="Natura",required=false,nillable=false)
  protected NaturaType natura;

  @javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter(org.openspcoop2.utils.jaxb.Decimal2String.class)
  @javax.xml.bind.annotation.XmlSchemaType(name="decimal")
  @XmlElement(name="SpeseAccessorie",required=false,nillable=false)
  org.openspcoop2.utils.jaxb.DecimalWrapper _decimalWrapper_speseAccessorie = null;

  @javax.xml.bind.annotation.XmlTransient
  protected java.math.BigDecimal speseAccessorie;

  @javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter(org.openspcoop2.utils.jaxb.Decimal2String.class)
  @javax.xml.bind.annotation.XmlSchemaType(name="decimal")
  @XmlElement(name="Arrotondamento",required=false,nillable=false)
  org.openspcoop2.utils.jaxb.DecimalWrapper _decimalWrapper_arrotondamento = null;

  @javax.xml.bind.annotation.XmlTransient
  protected java.math.BigDecimal arrotondamento;

  @javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter(org.openspcoop2.utils.jaxb.Decimal2String.class)
  @javax.xml.bind.annotation.XmlSchemaType(name="decimal")
  @XmlElement(name="ImponibileImporto",required=true,nillable=false)
  org.openspcoop2.utils.jaxb.DecimalWrapper _decimalWrapper_imponibileImporto = null;

  @javax.xml.bind.annotation.XmlTransient
  protected java.math.BigDecimal imponibileImporto;

  @javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter(org.openspcoop2.utils.jaxb.Decimal2String.class)
  @javax.xml.bind.annotation.XmlSchemaType(name="decimal")
  @XmlElement(name="Imposta",required=true,nillable=false)
  org.openspcoop2.utils.jaxb.DecimalWrapper _decimalWrapper_imposta = null;

  @javax.xml.bind.annotation.XmlTransient
  protected java.math.BigDecimal imposta;

  @javax.xml.bind.annotation.XmlTransient
  protected java.lang.String _value_esigibilitaIVA;

  @XmlElement(name="EsigibilitaIVA",required=false,nillable=false)
  protected EsigibilitaIVAType esigibilitaIVA;

  @javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter(javax.xml.bind.annotation.adapters.NormalizedStringAdapter.class)
  @javax.xml.bind.annotation.XmlSchemaType(name="normalizedString")
  @XmlElement(name="RiferimentoNormativo",required=false,nillable=false)
  protected java.lang.String riferimentoNormativo;

}
