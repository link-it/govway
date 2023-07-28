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

import it.gov.fatturapa.sdi.fatturapa.v1_0.constants.NaturaType;
import it.gov.fatturapa.sdi.fatturapa.v1_0.constants.RitenutaType;
import it.gov.fatturapa.sdi.fatturapa.v1_0.constants.TipoCassaType;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;
import java.io.Serializable;


/** <p>Java class for DatiCassaPrevidenzialeType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="DatiCassaPrevidenzialeType"&gt;
 * 		&lt;sequence&gt;
 * 			&lt;element name="TipoCassa" type="{http://www.fatturapa.gov.it/sdi/fatturapa/v1.0}TipoCassaType" minOccurs="1" maxOccurs="1"/&gt;
 * 			&lt;element name="AlCassa" type="{http://www.fatturapa.gov.it/sdi/fatturapa/v1.0}decimal" minOccurs="1" maxOccurs="1"/&gt;
 * 			&lt;element name="ImportoContributoCassa" type="{http://www.fatturapa.gov.it/sdi/fatturapa/v1.0}decimal" minOccurs="1" maxOccurs="1"/&gt;
 * 			&lt;element name="ImponibileCassa" type="{http://www.fatturapa.gov.it/sdi/fatturapa/v1.0}decimal" minOccurs="0" maxOccurs="1"/&gt;
 * 			&lt;element name="AliquotaIVA" type="{http://www.fatturapa.gov.it/sdi/fatturapa/v1.0}decimal" minOccurs="1" maxOccurs="1"/&gt;
 * 			&lt;element name="Ritenuta" type="{http://www.fatturapa.gov.it/sdi/fatturapa/v1.0}RitenutaType" minOccurs="0" maxOccurs="1"/&gt;
 * 			&lt;element name="Natura" type="{http://www.fatturapa.gov.it/sdi/fatturapa/v1.0}NaturaType" minOccurs="0" maxOccurs="1"/&gt;
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
@XmlType(name = "DatiCassaPrevidenzialeType", 
  propOrder = {
  	"tipoCassa",
  	"_decimalWrapper_alCassa",
  	"_decimalWrapper_importoContributoCassa",
  	"_decimalWrapper_imponibileCassa",
  	"_decimalWrapper_aliquotaIVA",
  	"ritenuta",
  	"natura",
  	"riferimentoAmministrazione"
  }
)

@XmlRootElement(name = "DatiCassaPrevidenzialeType")

public class DatiCassaPrevidenzialeType extends org.openspcoop2.utils.beans.BaseBean implements Serializable , Cloneable {
  public DatiCassaPrevidenzialeType() {
    super();
  }

  public void setTipoCassaRawEnumValue(String value) {
    this.tipoCassa = (TipoCassaType) TipoCassaType.toEnumConstantFromString(value);
  }

  public String getTipoCassaRawEnumValue() {
    if(this.tipoCassa == null){
    	return null;
    }else{
    	return this.tipoCassa.toString();
    }
  }

  public it.gov.fatturapa.sdi.fatturapa.v1_0.constants.TipoCassaType getTipoCassa() {
    return this.tipoCassa;
  }

  public void setTipoCassa(it.gov.fatturapa.sdi.fatturapa.v1_0.constants.TipoCassaType tipoCassa) {
    this.tipoCassa = tipoCassa;
  }

  public java.math.BigDecimal getAlCassa() {
    if(this._decimalWrapper_alCassa!=null){
		return (java.math.BigDecimal) this._decimalWrapper_alCassa.getObject(java.math.BigDecimal.class);
	}else{
		return this.alCassa;
	}
  }

  public void setAlCassa(java.math.BigDecimal alCassa) {
    if(alCassa!=null){
		this._decimalWrapper_alCassa = new org.openspcoop2.utils.jaxb.DecimalWrapper(1,3,2,2,alCassa);
	}
  }

  public java.math.BigDecimal getImportoContributoCassa() {
    if(this._decimalWrapper_importoContributoCassa!=null){
		return (java.math.BigDecimal) this._decimalWrapper_importoContributoCassa.getObject(java.math.BigDecimal.class);
	}else{
		return this.importoContributoCassa;
	}
  }

  public void setImportoContributoCassa(java.math.BigDecimal importoContributoCassa) {
    if(importoContributoCassa!=null){
		this._decimalWrapper_importoContributoCassa = new org.openspcoop2.utils.jaxb.DecimalWrapper(1,11,2,2,importoContributoCassa);
	}
  }

  public java.math.BigDecimal getImponibileCassa() {
    if(this._decimalWrapper_imponibileCassa!=null){
		return (java.math.BigDecimal) this._decimalWrapper_imponibileCassa.getObject(java.math.BigDecimal.class);
	}else{
		return this.imponibileCassa;
	}
  }

  public void setImponibileCassa(java.math.BigDecimal imponibileCassa) {
    if(imponibileCassa!=null){
		this._decimalWrapper_imponibileCassa = new org.openspcoop2.utils.jaxb.DecimalWrapper(1,11,2,2,imponibileCassa);
	}
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

  public void setRitenutaRawEnumValue(String value) {
    this.ritenuta = (RitenutaType) RitenutaType.toEnumConstantFromString(value);
  }

  public String getRitenutaRawEnumValue() {
    if(this.ritenuta == null){
    	return null;
    }else{
    	return this.ritenuta.toString();
    }
  }

  public it.gov.fatturapa.sdi.fatturapa.v1_0.constants.RitenutaType getRitenuta() {
    return this.ritenuta;
  }

  public void setRitenuta(it.gov.fatturapa.sdi.fatturapa.v1_0.constants.RitenutaType ritenuta) {
    this.ritenuta = ritenuta;
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

  public java.lang.String getRiferimentoAmministrazione() {
    return this.riferimentoAmministrazione;
  }

  public void setRiferimentoAmministrazione(java.lang.String riferimentoAmministrazione) {
    this.riferimentoAmministrazione = riferimentoAmministrazione;
  }

  private static final long serialVersionUID = 1L;



  @jakarta.xml.bind.annotation.XmlTransient
  protected java.lang.String tipoCassaRawEnumValue;

  @XmlElement(name="TipoCassa",required=true,nillable=false)
  protected TipoCassaType tipoCassa;

  @jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter(org.openspcoop2.utils.jaxb.Decimal2String.class)
  @jakarta.xml.bind.annotation.XmlSchemaType(name="decimal")
  @XmlElement(name="AlCassa",required=true,nillable=false)
  org.openspcoop2.utils.jaxb.DecimalWrapper _decimalWrapper_alCassa = null;

  @jakarta.xml.bind.annotation.XmlTransient
  protected java.math.BigDecimal alCassa;

  @jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter(org.openspcoop2.utils.jaxb.Decimal2String.class)
  @jakarta.xml.bind.annotation.XmlSchemaType(name="decimal")
  @XmlElement(name="ImportoContributoCassa",required=true,nillable=false)
  org.openspcoop2.utils.jaxb.DecimalWrapper _decimalWrapper_importoContributoCassa = null;

  @jakarta.xml.bind.annotation.XmlTransient
  protected java.math.BigDecimal importoContributoCassa;

  @jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter(org.openspcoop2.utils.jaxb.Decimal2String.class)
  @jakarta.xml.bind.annotation.XmlSchemaType(name="decimal")
  @XmlElement(name="ImponibileCassa",required=false,nillable=false)
  org.openspcoop2.utils.jaxb.DecimalWrapper _decimalWrapper_imponibileCassa = null;

  @jakarta.xml.bind.annotation.XmlTransient
  protected java.math.BigDecimal imponibileCassa;

  @jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter(org.openspcoop2.utils.jaxb.Decimal2String.class)
  @jakarta.xml.bind.annotation.XmlSchemaType(name="decimal")
  @XmlElement(name="AliquotaIVA",required=true,nillable=false)
  org.openspcoop2.utils.jaxb.DecimalWrapper _decimalWrapper_aliquotaIVA = null;

  @jakarta.xml.bind.annotation.XmlTransient
  protected java.math.BigDecimal aliquotaIVA;

  @jakarta.xml.bind.annotation.XmlTransient
  protected java.lang.String ritenutaRawEnumValue;

  @XmlElement(name="Ritenuta",required=false,nillable=false)
  protected RitenutaType ritenuta;

  @jakarta.xml.bind.annotation.XmlTransient
  protected java.lang.String naturaRawEnumValue;

  @XmlElement(name="Natura",required=false,nillable=false)
  protected NaturaType natura;

  @jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter(jakarta.xml.bind.annotation.adapters.NormalizedStringAdapter.class)
  @jakarta.xml.bind.annotation.XmlSchemaType(name="normalizedString")
  @XmlElement(name="RiferimentoAmministrazione",required=false,nillable=false)
  protected java.lang.String riferimentoAmministrazione;

}
