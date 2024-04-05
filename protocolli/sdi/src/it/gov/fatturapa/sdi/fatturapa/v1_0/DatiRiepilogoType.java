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
package it.gov.fatturapa.sdi.fatturapa.v1_0;

import it.gov.fatturapa.sdi.fatturapa.v1_0.constants.EsigibilitaIVAType;
import it.gov.fatturapa.sdi.fatturapa.v1_0.constants.NaturaType;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;
import java.io.Serializable;


/** <p>Java class for DatiRiepilogoType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="DatiRiepilogoType"&gt;
 * 		&lt;sequence&gt;
 * 			&lt;element name="AliquotaIVA" type="{http://www.fatturapa.gov.it/sdi/fatturapa/v1.0}decimal" minOccurs="1" maxOccurs="1"/&gt;
 * 			&lt;element name="Natura" type="{http://www.fatturapa.gov.it/sdi/fatturapa/v1.0}NaturaType" minOccurs="0" maxOccurs="1"/&gt;
 * 			&lt;element name="SpeseAccessorie" type="{http://www.fatturapa.gov.it/sdi/fatturapa/v1.0}decimal" minOccurs="0" maxOccurs="1"/&gt;
 * 			&lt;element name="Arrotondamento" type="{http://www.fatturapa.gov.it/sdi/fatturapa/v1.0}decimal" minOccurs="0" maxOccurs="1"/&gt;
 * 			&lt;element name="ImponibileImporto" type="{http://www.fatturapa.gov.it/sdi/fatturapa/v1.0}decimal" minOccurs="1" maxOccurs="1"/&gt;
 * 			&lt;element name="Imposta" type="{http://www.fatturapa.gov.it/sdi/fatturapa/v1.0}decimal" minOccurs="1" maxOccurs="1"/&gt;
 * 			&lt;element name="EsigibilitaIVA" type="{http://www.fatturapa.gov.it/sdi/fatturapa/v1.0}EsigibilitaIVAType" minOccurs="0" maxOccurs="1"/&gt;
 * 			&lt;element name="RiferimentoNormativo" type="{http://www.fatturapa.gov.it/sdi/fatturapa/v1.0}normalizedString" minOccurs="0" maxOccurs="1"/&gt;
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
    super();
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

  public void setNaturaRawEnumValue(String value) {
    this.natura = (NaturaType) NaturaType.toEnumConstantFromString(value);
  }

  public String getNaturaRawEnumValue() {
    if(this.natura == null){
    	return null;
    }else{
    	return this.natura.toString();
    }
  }

  public it.gov.fatturapa.sdi.fatturapa.v1_0.constants.NaturaType getNatura() {
    return this.natura;
  }

  public void setNatura(it.gov.fatturapa.sdi.fatturapa.v1_0.constants.NaturaType natura) {
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

  public void setEsigibilitaIVARawEnumValue(String value) {
    this.esigibilitaIVA = (EsigibilitaIVAType) EsigibilitaIVAType.toEnumConstantFromString(value);
  }

  public String getEsigibilitaIVARawEnumValue() {
    if(this.esigibilitaIVA == null){
    	return null;
    }else{
    	return this.esigibilitaIVA.toString();
    }
  }

  public it.gov.fatturapa.sdi.fatturapa.v1_0.constants.EsigibilitaIVAType getEsigibilitaIVA() {
    return this.esigibilitaIVA;
  }

  public void setEsigibilitaIVA(it.gov.fatturapa.sdi.fatturapa.v1_0.constants.EsigibilitaIVAType esigibilitaIVA) {
    this.esigibilitaIVA = esigibilitaIVA;
  }

  public java.lang.String getRiferimentoNormativo() {
    return this.riferimentoNormativo;
  }

  public void setRiferimentoNormativo(java.lang.String riferimentoNormativo) {
    this.riferimentoNormativo = riferimentoNormativo;
  }

  private static final long serialVersionUID = 1L;



  @jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter(org.openspcoop2.utils.jaxb.Decimal2String.class)
  @jakarta.xml.bind.annotation.XmlSchemaType(name="decimal")
  @XmlElement(name="AliquotaIVA",required=true,nillable=false)
  org.openspcoop2.utils.jaxb.DecimalWrapper _decimalWrapper_aliquotaIVA = null;

  @jakarta.xml.bind.annotation.XmlTransient
  protected java.math.BigDecimal aliquotaIVA;

  @jakarta.xml.bind.annotation.XmlTransient
  protected java.lang.String naturaRawEnumValue;

  @XmlElement(name="Natura",required=false,nillable=false)
  protected NaturaType natura;

  @jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter(org.openspcoop2.utils.jaxb.Decimal2String.class)
  @jakarta.xml.bind.annotation.XmlSchemaType(name="decimal")
  @XmlElement(name="SpeseAccessorie",required=false,nillable=false)
  org.openspcoop2.utils.jaxb.DecimalWrapper _decimalWrapper_speseAccessorie = null;

  @jakarta.xml.bind.annotation.XmlTransient
  protected java.math.BigDecimal speseAccessorie;

  @jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter(org.openspcoop2.utils.jaxb.Decimal2String.class)
  @jakarta.xml.bind.annotation.XmlSchemaType(name="decimal")
  @XmlElement(name="Arrotondamento",required=false,nillable=false)
  org.openspcoop2.utils.jaxb.DecimalWrapper _decimalWrapper_arrotondamento = null;

  @jakarta.xml.bind.annotation.XmlTransient
  protected java.math.BigDecimal arrotondamento;

  @jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter(org.openspcoop2.utils.jaxb.Decimal2String.class)
  @jakarta.xml.bind.annotation.XmlSchemaType(name="decimal")
  @XmlElement(name="ImponibileImporto",required=true,nillable=false)
  org.openspcoop2.utils.jaxb.DecimalWrapper _decimalWrapper_imponibileImporto = null;

  @jakarta.xml.bind.annotation.XmlTransient
  protected java.math.BigDecimal imponibileImporto;

  @jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter(org.openspcoop2.utils.jaxb.Decimal2String.class)
  @jakarta.xml.bind.annotation.XmlSchemaType(name="decimal")
  @XmlElement(name="Imposta",required=true,nillable=false)
  org.openspcoop2.utils.jaxb.DecimalWrapper _decimalWrapper_imposta = null;

  @jakarta.xml.bind.annotation.XmlTransient
  protected java.math.BigDecimal imposta;

  @jakarta.xml.bind.annotation.XmlTransient
  protected java.lang.String esigibilitaIVARawEnumValue;

  @XmlElement(name="EsigibilitaIVA",required=false,nillable=false)
  protected EsigibilitaIVAType esigibilitaIVA;

  @jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter(jakarta.xml.bind.annotation.adapters.NormalizedStringAdapter.class)
  @jakarta.xml.bind.annotation.XmlSchemaType(name="normalizedString")
  @XmlElement(name="RiferimentoNormativo",required=false,nillable=false)
  protected java.lang.String riferimentoNormativo;

}
