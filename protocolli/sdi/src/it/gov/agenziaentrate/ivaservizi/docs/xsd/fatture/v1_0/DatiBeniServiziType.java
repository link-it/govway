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

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;
import java.io.Serializable;


/** <p>Java class for DatiBeniServiziType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="DatiBeniServiziType"&gt;
 * 		&lt;sequence&gt;
 * 			&lt;element name="Descrizione" type="{http://ivaservizi.agenziaentrate.gov.it/docs/xsd/fatture/v1.0}normalizedString" minOccurs="1" maxOccurs="1"/&gt;
 * 			&lt;element name="Importo" type="{http://ivaservizi.agenziaentrate.gov.it/docs/xsd/fatture/v1.0}decimal" minOccurs="1" maxOccurs="1"/&gt;
 * 			&lt;element name="DatiIVA" type="{http://ivaservizi.agenziaentrate.gov.it/docs/xsd/fatture/v1.0}DatiIVAType" minOccurs="1" maxOccurs="1"/&gt;
 * 			&lt;element name="Natura" type="{http://ivaservizi.agenziaentrate.gov.it/docs/xsd/fatture/v1.0}string" minOccurs="0" maxOccurs="1"/&gt;
 * 			&lt;element name="RiferimentoNormativo" type="{http://ivaservizi.agenziaentrate.gov.it/docs/xsd/fatture/v1.0}normalizedString" minOccurs="0" maxOccurs="1"/&gt;
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
@XmlType(name = "DatiBeniServiziType", 
  propOrder = {
  	"descrizione",
  	"_decimalWrapper_importo",
  	"datiIVA",
  	"natura",
  	"riferimentoNormativo"
  }
)

@XmlRootElement(name = "DatiBeniServiziType")

public class DatiBeniServiziType extends org.openspcoop2.utils.beans.BaseBean implements Serializable , Cloneable {
  public DatiBeniServiziType() {
    super();
  }

  public java.lang.String getDescrizione() {
    return this.descrizione;
  }

  public void setDescrizione(java.lang.String descrizione) {
    this.descrizione = descrizione;
  }

  public java.math.BigDecimal getImporto() {
    if(this._decimalWrapper_importo!=null){
		return (java.math.BigDecimal) this._decimalWrapper_importo.getObject(java.math.BigDecimal.class);
	}else{
		return this.importo;
	}
  }

  public void setImporto(java.math.BigDecimal importo) {
    if(importo!=null){
		this._decimalWrapper_importo = new org.openspcoop2.utils.jaxb.DecimalWrapper(1,11,2,2,importo);
	}
  }

  public DatiIVAType getDatiIVA() {
    return this.datiIVA;
  }

  public void setDatiIVA(DatiIVAType datiIVA) {
    this.datiIVA = datiIVA;
  }

  public java.lang.String getNatura() {
    return this.natura;
  }

  public void setNatura(java.lang.String natura) {
    this.natura = natura;
  }

  public java.lang.String getRiferimentoNormativo() {
    return this.riferimentoNormativo;
  }

  public void setRiferimentoNormativo(java.lang.String riferimentoNormativo) {
    this.riferimentoNormativo = riferimentoNormativo;
  }

  private static final long serialVersionUID = 1L;



  @jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter(jakarta.xml.bind.annotation.adapters.NormalizedStringAdapter.class)
  @jakarta.xml.bind.annotation.XmlSchemaType(name="normalizedString")
  @XmlElement(name="Descrizione",required=true,nillable=false)
  protected java.lang.String descrizione;

  @jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter(org.openspcoop2.utils.jaxb.Decimal2String.class)
  @jakarta.xml.bind.annotation.XmlSchemaType(name="decimal")
  @XmlElement(name="Importo",required=true,nillable=false)
  org.openspcoop2.utils.jaxb.DecimalWrapper _decimalWrapper_importo = null;

  @jakarta.xml.bind.annotation.XmlTransient
  protected java.math.BigDecimal importo;

  @XmlElement(name="DatiIVA",required=true,nillable=false)
  protected DatiIVAType datiIVA;

  @jakarta.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="Natura",required=false,nillable=false)
  protected java.lang.String natura;

  @jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter(jakarta.xml.bind.annotation.adapters.NormalizedStringAdapter.class)
  @jakarta.xml.bind.annotation.XmlSchemaType(name="normalizedString")
  @XmlElement(name="RiferimentoNormativo",required=false,nillable=false)
  protected java.lang.String riferimentoNormativo;

}
