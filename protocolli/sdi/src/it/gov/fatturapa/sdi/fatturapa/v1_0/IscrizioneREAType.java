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

import it.gov.fatturapa.sdi.fatturapa.v1_0.constants.SocioUnicoType;
import it.gov.fatturapa.sdi.fatturapa.v1_0.constants.StatoLiquidazioneType;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;
import java.io.Serializable;


/** <p>Java class for IscrizioneREAType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="IscrizioneREAType">
 * 		&lt;sequence>
 * 			&lt;element name="Ufficio" type="{http://www.fatturapa.gov.it/sdi/fatturapa/v1.0}string" minOccurs="1" maxOccurs="1"/>
 * 			&lt;element name="NumeroREA" type="{http://www.fatturapa.gov.it/sdi/fatturapa/v1.0}normalizedString" minOccurs="1" maxOccurs="1"/>
 * 			&lt;element name="CapitaleSociale" type="{http://www.fatturapa.gov.it/sdi/fatturapa/v1.0}decimal" minOccurs="0" maxOccurs="1"/>
 * 			&lt;element name="SocioUnico" type="{http://www.fatturapa.gov.it/sdi/fatturapa/v1.0}SocioUnicoType" minOccurs="0" maxOccurs="1"/>
 * 			&lt;element name="StatoLiquidazione" type="{http://www.fatturapa.gov.it/sdi/fatturapa/v1.0}StatoLiquidazioneType" minOccurs="1" maxOccurs="1"/>
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

  public java.lang.Double getCapitaleSociale() {
    if(this._decimalWrapper_capitaleSociale!=null){
		return (java.lang.Double) this._decimalWrapper_capitaleSociale.getObject(java.lang.Double.class);
	}else{
		return this.capitaleSociale;
	}
  }

  public void setCapitaleSociale(java.lang.Double capitaleSociale) {
    if(capitaleSociale!=null){
		this._decimalWrapper_capitaleSociale = new org.openspcoop2.utils.jaxb.DecimalWrapper(1,11,2,2,capitaleSociale);
	}
  }

  public void set_value_socioUnico(String value) {
    this.socioUnico = (SocioUnicoType) SocioUnicoType.toEnumConstantFromString(value);
  }

  public String get_value_socioUnico() {
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

  public void set_value_statoLiquidazione(String value) {
    this.statoLiquidazione = (StatoLiquidazioneType) StatoLiquidazioneType.toEnumConstantFromString(value);
  }

  public String get_value_statoLiquidazione() {
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

  @XmlTransient
  private Long id;



  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="Ufficio",required=true,nillable=false)
  protected java.lang.String ufficio;

  @javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter(javax.xml.bind.annotation.adapters.NormalizedStringAdapter.class)
  @javax.xml.bind.annotation.XmlSchemaType(name="normalizedString")
  @XmlElement(name="NumeroREA",required=true,nillable=false)
  protected java.lang.String numeroREA;

  @javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter(org.openspcoop2.utils.jaxb.Decimal2String.class)
  @javax.xml.bind.annotation.XmlSchemaType(name="decimal")
  @XmlElement(name="CapitaleSociale",required=false,nillable=false)
  org.openspcoop2.utils.jaxb.DecimalWrapper _decimalWrapper_capitaleSociale = null;

  @XmlTransient
  protected java.lang.Double capitaleSociale;

  @XmlTransient
  protected java.lang.String _value_socioUnico;

  @XmlElement(name="SocioUnico",required=false,nillable=false)
  protected SocioUnicoType socioUnico;

  @XmlTransient
  protected java.lang.String _value_statoLiquidazione;

  @XmlElement(name="StatoLiquidazione",required=true,nillable=false)
  protected StatoLiquidazioneType statoLiquidazione;

}
