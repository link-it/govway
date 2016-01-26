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
package it.gov.fatturapa.sdi.fatturapa.v1_1;

import it.gov.fatturapa.sdi.fatturapa.v1_1.constants.NaturaType;
import it.gov.fatturapa.sdi.fatturapa.v1_1.constants.RitenutaType;
import it.gov.fatturapa.sdi.fatturapa.v1_1.constants.TipoCassaType;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;
import java.io.Serializable;


/** <p>Java class for DatiCassaPrevidenzialeType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="DatiCassaPrevidenzialeType">
 * 		&lt;sequence>
 * 			&lt;element name="TipoCassa" type="{http://www.fatturapa.gov.it/sdi/fatturapa/v1.1}TipoCassaType" minOccurs="1" maxOccurs="1"/>
 * 			&lt;element name="AlCassa" type="{http://www.fatturapa.gov.it/sdi/fatturapa/v1.1}decimal" minOccurs="1" maxOccurs="1"/>
 * 			&lt;element name="ImportoContributoCassa" type="{http://www.fatturapa.gov.it/sdi/fatturapa/v1.1}decimal" minOccurs="1" maxOccurs="1"/>
 * 			&lt;element name="ImponibileCassa" type="{http://www.fatturapa.gov.it/sdi/fatturapa/v1.1}decimal" minOccurs="0" maxOccurs="1"/>
 * 			&lt;element name="AliquotaIVA" type="{http://www.fatturapa.gov.it/sdi/fatturapa/v1.1}decimal" minOccurs="1" maxOccurs="1"/>
 * 			&lt;element name="Ritenuta" type="{http://www.fatturapa.gov.it/sdi/fatturapa/v1.1}RitenutaType" minOccurs="0" maxOccurs="1"/>
 * 			&lt;element name="Natura" type="{http://www.fatturapa.gov.it/sdi/fatturapa/v1.1}NaturaType" minOccurs="0" maxOccurs="1"/>
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

  public void set_value_tipoCassa(String value) {
    this.tipoCassa = (TipoCassaType) TipoCassaType.toEnumConstantFromString(value);
  }

  public String get_value_tipoCassa() {
    if(this.tipoCassa == null){
    	return null;
    }else{
    	return this.tipoCassa.toString();
    }
  }

  public it.gov.fatturapa.sdi.fatturapa.v1_1.constants.TipoCassaType getTipoCassa() {
    return this.tipoCassa;
  }

  public void setTipoCassa(it.gov.fatturapa.sdi.fatturapa.v1_1.constants.TipoCassaType tipoCassa) {
    this.tipoCassa = tipoCassa;
  }

  public java.lang.Double getAlCassa() {
    if(this._decimalWrapper_alCassa!=null){
		return (java.lang.Double) this._decimalWrapper_alCassa.getObject(java.lang.Double.class);
	}else{
		return this.alCassa;
	}
  }

  public void setAlCassa(java.lang.Double alCassa) {
    if(alCassa!=null){
		this._decimalWrapper_alCassa = new org.openspcoop2.utils.jaxb.DecimalWrapper(1,3,2,2,alCassa);
	}
  }

  public java.lang.Double getImportoContributoCassa() {
    if(this._decimalWrapper_importoContributoCassa!=null){
		return (java.lang.Double) this._decimalWrapper_importoContributoCassa.getObject(java.lang.Double.class);
	}else{
		return this.importoContributoCassa;
	}
  }

  public void setImportoContributoCassa(java.lang.Double importoContributoCassa) {
    if(importoContributoCassa!=null){
		this._decimalWrapper_importoContributoCassa = new org.openspcoop2.utils.jaxb.DecimalWrapper(1,11,2,2,importoContributoCassa);
	}
  }

  public java.lang.Double getImponibileCassa() {
    if(this._decimalWrapper_imponibileCassa!=null){
		return (java.lang.Double) this._decimalWrapper_imponibileCassa.getObject(java.lang.Double.class);
	}else{
		return this.imponibileCassa;
	}
  }

  public void setImponibileCassa(java.lang.Double imponibileCassa) {
    if(imponibileCassa!=null){
		this._decimalWrapper_imponibileCassa = new org.openspcoop2.utils.jaxb.DecimalWrapper(1,11,2,2,imponibileCassa);
	}
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

  public void set_value_ritenuta(String value) {
    this.ritenuta = (RitenutaType) RitenutaType.toEnumConstantFromString(value);
  }

  public String get_value_ritenuta() {
    if(this.ritenuta == null){
    	return null;
    }else{
    	return this.ritenuta.toString();
    }
  }

  public it.gov.fatturapa.sdi.fatturapa.v1_1.constants.RitenutaType getRitenuta() {
    return this.ritenuta;
  }

  public void setRitenuta(it.gov.fatturapa.sdi.fatturapa.v1_1.constants.RitenutaType ritenuta) {
    this.ritenuta = ritenuta;
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

  public it.gov.fatturapa.sdi.fatturapa.v1_1.constants.NaturaType getNatura() {
    return this.natura;
  }

  public void setNatura(it.gov.fatturapa.sdi.fatturapa.v1_1.constants.NaturaType natura) {
    this.natura = natura;
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



  @XmlTransient
  protected java.lang.String _value_tipoCassa;

  @XmlElement(name="TipoCassa",required=true,nillable=false)
  protected TipoCassaType tipoCassa;

  @javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter(org.openspcoop2.utils.jaxb.Decimal2String.class)
  @javax.xml.bind.annotation.XmlSchemaType(name="decimal")
  @XmlElement(name="AlCassa",required=true,nillable=false)
  org.openspcoop2.utils.jaxb.DecimalWrapper _decimalWrapper_alCassa = null;

  @XmlTransient
  protected java.lang.Double alCassa;

  @javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter(org.openspcoop2.utils.jaxb.Decimal2String.class)
  @javax.xml.bind.annotation.XmlSchemaType(name="decimal")
  @XmlElement(name="ImportoContributoCassa",required=true,nillable=false)
  org.openspcoop2.utils.jaxb.DecimalWrapper _decimalWrapper_importoContributoCassa = null;

  @XmlTransient
  protected java.lang.Double importoContributoCassa;

  @javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter(org.openspcoop2.utils.jaxb.Decimal2String.class)
  @javax.xml.bind.annotation.XmlSchemaType(name="decimal")
  @XmlElement(name="ImponibileCassa",required=false,nillable=false)
  org.openspcoop2.utils.jaxb.DecimalWrapper _decimalWrapper_imponibileCassa = null;

  @XmlTransient
  protected java.lang.Double imponibileCassa;

  @javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter(org.openspcoop2.utils.jaxb.Decimal2String.class)
  @javax.xml.bind.annotation.XmlSchemaType(name="decimal")
  @XmlElement(name="AliquotaIVA",required=true,nillable=false)
  org.openspcoop2.utils.jaxb.DecimalWrapper _decimalWrapper_aliquotaIVA = null;

  @XmlTransient
  protected java.lang.Double aliquotaIVA;

  @XmlTransient
  protected java.lang.String _value_ritenuta;

  @XmlElement(name="Ritenuta",required=false,nillable=false)
  protected RitenutaType ritenuta;

  @XmlTransient
  protected java.lang.String _value_natura;

  @XmlElement(name="Natura",required=false,nillable=false)
  protected NaturaType natura;

  @javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter(javax.xml.bind.annotation.adapters.NormalizedStringAdapter.class)
  @javax.xml.bind.annotation.XmlSchemaType(name="normalizedString")
  @XmlElement(name="RiferimentoAmministrazione",required=false,nillable=false)
  protected java.lang.String riferimentoAmministrazione;

}
