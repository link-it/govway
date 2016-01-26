/*
 * OpenSPCoop v2 - Customizable SOAP Message Broker 
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
package it.gov.fatturapa.sdi.fatturapa.v1_0;

import it.gov.fatturapa.sdi.fatturapa.v1_0.constants.EsigibilitaIVAType;
import it.gov.fatturapa.sdi.fatturapa.v1_0.constants.NaturaType;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;
import java.io.Serializable;


/** <p>Java class for DatiRiepilogoType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="DatiRiepilogoType">
 * 		&lt;sequence>
 * 			&lt;element name="AliquotaIVA" type="{http://www.fatturapa.gov.it/sdi/fatturapa/v1.0}decimal" minOccurs="1" maxOccurs="1"/>
 * 			&lt;element name="Natura" type="{http://www.fatturapa.gov.it/sdi/fatturapa/v1.0}NaturaType" minOccurs="0" maxOccurs="1"/>
 * 			&lt;element name="SpeseAccessorie" type="{http://www.fatturapa.gov.it/sdi/fatturapa/v1.0}decimal" minOccurs="0" maxOccurs="1"/>
 * 			&lt;element name="Arrotondamento" type="{http://www.fatturapa.gov.it/sdi/fatturapa/v1.0}decimal" minOccurs="0" maxOccurs="1"/>
 * 			&lt;element name="ImponibileImporto" type="{http://www.fatturapa.gov.it/sdi/fatturapa/v1.0}decimal" minOccurs="1" maxOccurs="1"/>
 * 			&lt;element name="Imposta" type="{http://www.fatturapa.gov.it/sdi/fatturapa/v1.0}decimal" minOccurs="1" maxOccurs="1"/>
 * 			&lt;element name="EsigibilitaIVA" type="{http://www.fatturapa.gov.it/sdi/fatturapa/v1.0}EsigibilitaIVAType" minOccurs="0" maxOccurs="1"/>
 * 			&lt;element name="RiferimentoNormativo" type="{http://www.fatturapa.gov.it/sdi/fatturapa/v1.0}normalizedString" minOccurs="0" maxOccurs="1"/>
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

  public java.lang.Double getAliquotaIVA() {
    if(this._decimalWrapper_aliquotaIVA!=null){
		return (java.lang.Double) this._decimalWrapper_aliquotaIVA.getObject(java.lang.Double.class);
	}else{
		return this.aliquotaIVA;
	}
  }

  public void setAliquotaIVA(java.lang.Double aliquotaIVA) {
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

  public it.gov.fatturapa.sdi.fatturapa.v1_0.constants.NaturaType getNatura() {
    return this.natura;
  }

  public void setNatura(it.gov.fatturapa.sdi.fatturapa.v1_0.constants.NaturaType natura) {
    this.natura = natura;
  }

  public java.lang.Double getSpeseAccessorie() {
    if(this._decimalWrapper_speseAccessorie!=null){
		return (java.lang.Double) this._decimalWrapper_speseAccessorie.getObject(java.lang.Double.class);
	}else{
		return this.speseAccessorie;
	}
  }

  public void setSpeseAccessorie(java.lang.Double speseAccessorie) {
    if(speseAccessorie!=null){
		this._decimalWrapper_speseAccessorie = new org.openspcoop2.utils.jaxb.DecimalWrapper(1,11,2,2,speseAccessorie);
	}
  }

  public java.lang.Double getArrotondamento() {
    if(this._decimalWrapper_arrotondamento!=null){
		return (java.lang.Double) this._decimalWrapper_arrotondamento.getObject(java.lang.Double.class);
	}else{
		return this.arrotondamento;
	}
  }

  public void setArrotondamento(java.lang.Double arrotondamento) {
    if(arrotondamento!=null){
		this._decimalWrapper_arrotondamento = new org.openspcoop2.utils.jaxb.DecimalWrapper(1,11,2,8,arrotondamento);
	}
  }

  public java.lang.Double getImponibileImporto() {
    if(this._decimalWrapper_imponibileImporto!=null){
		return (java.lang.Double) this._decimalWrapper_imponibileImporto.getObject(java.lang.Double.class);
	}else{
		return this.imponibileImporto;
	}
  }

  public void setImponibileImporto(java.lang.Double imponibileImporto) {
    if(imponibileImporto!=null){
		this._decimalWrapper_imponibileImporto = new org.openspcoop2.utils.jaxb.DecimalWrapper(1,11,2,2,imponibileImporto);
	}
  }

  public java.lang.Double getImposta() {
    if(this._decimalWrapper_imposta!=null){
		return (java.lang.Double) this._decimalWrapper_imposta.getObject(java.lang.Double.class);
	}else{
		return this.imposta;
	}
  }

  public void setImposta(java.lang.Double imposta) {
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

  @XmlTransient
  private Long id;



  @javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter(org.openspcoop2.utils.jaxb.Decimal2String.class)
  @javax.xml.bind.annotation.XmlSchemaType(name="decimal")
  @XmlElement(name="AliquotaIVA",required=true,nillable=false)
  org.openspcoop2.utils.jaxb.DecimalWrapper _decimalWrapper_aliquotaIVA = null;

  @XmlTransient
  protected java.lang.Double aliquotaIVA;

  @XmlTransient
  protected java.lang.String _value_natura;

  @XmlElement(name="Natura",required=false,nillable=false)
  protected NaturaType natura;

  @javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter(org.openspcoop2.utils.jaxb.Decimal2String.class)
  @javax.xml.bind.annotation.XmlSchemaType(name="decimal")
  @XmlElement(name="SpeseAccessorie",required=false,nillable=false)
  org.openspcoop2.utils.jaxb.DecimalWrapper _decimalWrapper_speseAccessorie = null;

  @XmlTransient
  protected java.lang.Double speseAccessorie;

  @javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter(org.openspcoop2.utils.jaxb.Decimal2String.class)
  @javax.xml.bind.annotation.XmlSchemaType(name="decimal")
  @XmlElement(name="Arrotondamento",required=false,nillable=false)
  org.openspcoop2.utils.jaxb.DecimalWrapper _decimalWrapper_arrotondamento = null;

  @XmlTransient
  protected java.lang.Double arrotondamento;

  @javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter(org.openspcoop2.utils.jaxb.Decimal2String.class)
  @javax.xml.bind.annotation.XmlSchemaType(name="decimal")
  @XmlElement(name="ImponibileImporto",required=true,nillable=false)
  org.openspcoop2.utils.jaxb.DecimalWrapper _decimalWrapper_imponibileImporto = null;

  @XmlTransient
  protected java.lang.Double imponibileImporto;

  @javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter(org.openspcoop2.utils.jaxb.Decimal2String.class)
  @javax.xml.bind.annotation.XmlSchemaType(name="decimal")
  @XmlElement(name="Imposta",required=true,nillable=false)
  org.openspcoop2.utils.jaxb.DecimalWrapper _decimalWrapper_imposta = null;

  @XmlTransient
  protected java.lang.Double imposta;

  @XmlTransient
  protected java.lang.String _value_esigibilitaIVA;

  @XmlElement(name="EsigibilitaIVA",required=false,nillable=false)
  protected EsigibilitaIVAType esigibilitaIVA;

  @javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter(javax.xml.bind.annotation.adapters.NormalizedStringAdapter.class)
  @javax.xml.bind.annotation.XmlSchemaType(name="normalizedString")
  @XmlElement(name="RiferimentoNormativo",required=false,nillable=false)
  protected java.lang.String riferimentoNormativo;

}
