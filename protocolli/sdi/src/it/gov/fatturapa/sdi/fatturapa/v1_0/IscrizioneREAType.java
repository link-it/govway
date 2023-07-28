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

import it.gov.fatturapa.sdi.fatturapa.v1_0.constants.SocioUnicoType;
import it.gov.fatturapa.sdi.fatturapa.v1_0.constants.StatoLiquidazioneType;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;
import java.io.Serializable;


/** <p>Java class for IscrizioneREAType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="IscrizioneREAType"&gt;
 * 		&lt;sequence&gt;
 * 			&lt;element name="Ufficio" type="{http://www.fatturapa.gov.it/sdi/fatturapa/v1.0}string" minOccurs="1" maxOccurs="1"/&gt;
 * 			&lt;element name="NumeroREA" type="{http://www.fatturapa.gov.it/sdi/fatturapa/v1.0}normalizedString" minOccurs="1" maxOccurs="1"/&gt;
 * 			&lt;element name="CapitaleSociale" type="{http://www.fatturapa.gov.it/sdi/fatturapa/v1.0}decimal" minOccurs="0" maxOccurs="1"/&gt;
 * 			&lt;element name="SocioUnico" type="{http://www.fatturapa.gov.it/sdi/fatturapa/v1.0}SocioUnicoType" minOccurs="0" maxOccurs="1"/&gt;
 * 			&lt;element name="StatoLiquidazione" type="{http://www.fatturapa.gov.it/sdi/fatturapa/v1.0}StatoLiquidazioneType" minOccurs="1" maxOccurs="1"/&gt;
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
@XmlType(name = "IscrizioneREAType", 
  propOrder = {
  	"ufficio",
  	"numeroREA",
  	"_decimalWrapper_capitaleSociale",
  	"socioUnico",
  	"statoLiquidazione"
  }
)

@XmlRootElement(name = "IscrizioneREAType")

public class IscrizioneREAType extends org.openspcoop2.utils.beans.BaseBean implements Serializable , Cloneable {
  public IscrizioneREAType() {
    super();
  }

  public java.lang.String getUfficio() {
    return this.ufficio;
  }

  public void setUfficio(java.lang.String ufficio) {
    this.ufficio = ufficio;
  }

  public java.lang.String getNumeroREA() {
    return this.numeroREA;
  }

  public void setNumeroREA(java.lang.String numeroREA) {
    this.numeroREA = numeroREA;
  }

  public java.math.BigDecimal getCapitaleSociale() {
    if(this._decimalWrapper_capitaleSociale!=null){
		return (java.math.BigDecimal) this._decimalWrapper_capitaleSociale.getObject(java.math.BigDecimal.class);
	}else{
		return this.capitaleSociale;
	}
  }

  public void setCapitaleSociale(java.math.BigDecimal capitaleSociale) {
    if(capitaleSociale!=null){
		this._decimalWrapper_capitaleSociale = new org.openspcoop2.utils.jaxb.DecimalWrapper(1,11,2,2,capitaleSociale);
	}
  }

  public void setSocioUnicoRawEnumValue(String value) {
    this.socioUnico = (SocioUnicoType) SocioUnicoType.toEnumConstantFromString(value);
  }

  public String getSocioUnicoRawEnumValue() {
    if(this.socioUnico == null){
    	return null;
    }else{
    	return this.socioUnico.toString();
    }
  }

  public it.gov.fatturapa.sdi.fatturapa.v1_0.constants.SocioUnicoType getSocioUnico() {
    return this.socioUnico;
  }

  public void setSocioUnico(it.gov.fatturapa.sdi.fatturapa.v1_0.constants.SocioUnicoType socioUnico) {
    this.socioUnico = socioUnico;
  }

  public void setStatoLiquidazioneRawEnumValue(String value) {
    this.statoLiquidazione = (StatoLiquidazioneType) StatoLiquidazioneType.toEnumConstantFromString(value);
  }

  public String getStatoLiquidazioneRawEnumValue() {
    if(this.statoLiquidazione == null){
    	return null;
    }else{
    	return this.statoLiquidazione.toString();
    }
  }

  public it.gov.fatturapa.sdi.fatturapa.v1_0.constants.StatoLiquidazioneType getStatoLiquidazione() {
    return this.statoLiquidazione;
  }

  public void setStatoLiquidazione(it.gov.fatturapa.sdi.fatturapa.v1_0.constants.StatoLiquidazioneType statoLiquidazione) {
    this.statoLiquidazione = statoLiquidazione;
  }

  private static final long serialVersionUID = 1L;



  @jakarta.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="Ufficio",required=true,nillable=false)
  protected java.lang.String ufficio;

  @jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter(jakarta.xml.bind.annotation.adapters.NormalizedStringAdapter.class)
  @jakarta.xml.bind.annotation.XmlSchemaType(name="normalizedString")
  @XmlElement(name="NumeroREA",required=true,nillable=false)
  protected java.lang.String numeroREA;

  @jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter(org.openspcoop2.utils.jaxb.Decimal2String.class)
  @jakarta.xml.bind.annotation.XmlSchemaType(name="decimal")
  @XmlElement(name="CapitaleSociale",required=false,nillable=false)
  org.openspcoop2.utils.jaxb.DecimalWrapper _decimalWrapper_capitaleSociale = null;

  @jakarta.xml.bind.annotation.XmlTransient
  protected java.math.BigDecimal capitaleSociale;

  @jakarta.xml.bind.annotation.XmlTransient
  protected java.lang.String socioUnicoRawEnumValue;

  @XmlElement(name="SocioUnico",required=false,nillable=false)
  protected SocioUnicoType socioUnico;

  @jakarta.xml.bind.annotation.XmlTransient
  protected java.lang.String statoLiquidazioneRawEnumValue;

  @XmlElement(name="StatoLiquidazione",required=true,nillable=false)
  protected StatoLiquidazioneType statoLiquidazione;

}
