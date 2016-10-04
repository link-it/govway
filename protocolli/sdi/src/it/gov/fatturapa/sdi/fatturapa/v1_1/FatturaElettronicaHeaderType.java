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

import it.gov.fatturapa.sdi.fatturapa.v1_1.constants.SoggettoEmittenteType;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;
import java.io.Serializable;


/** <p>Java class for FatturaElettronicaHeaderType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="FatturaElettronicaHeaderType">
 * 		&lt;sequence>
 * 			&lt;element name="DatiTrasmissione" type="{http://www.fatturapa.gov.it/sdi/fatturapa/v1.1}DatiTrasmissioneType" minOccurs="1" maxOccurs="1"/>
 * 			&lt;element name="CedentePrestatore" type="{http://www.fatturapa.gov.it/sdi/fatturapa/v1.1}CedentePrestatoreType" minOccurs="1" maxOccurs="1"/>
 * 			&lt;element name="RappresentanteFiscale" type="{http://www.fatturapa.gov.it/sdi/fatturapa/v1.1}RappresentanteFiscaleType" minOccurs="0" maxOccurs="1"/>
 * 			&lt;element name="CessionarioCommittente" type="{http://www.fatturapa.gov.it/sdi/fatturapa/v1.1}CessionarioCommittenteType" minOccurs="1" maxOccurs="1"/>
 * 			&lt;element name="TerzoIntermediarioOSoggettoEmittente" type="{http://www.fatturapa.gov.it/sdi/fatturapa/v1.1}TerzoIntermediarioSoggettoEmittenteType" minOccurs="0" maxOccurs="1"/>
 * 			&lt;element name="SoggettoEmittente" type="{http://www.fatturapa.gov.it/sdi/fatturapa/v1.1}SoggettoEmittenteType" minOccurs="0" maxOccurs="1"/>
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
@XmlType(name = "FatturaElettronicaHeaderType", 
  propOrder = {
  	"datiTrasmissione",
  	"cedentePrestatore",
  	"rappresentanteFiscale",
  	"cessionarioCommittente",
  	"terzoIntermediarioOSoggettoEmittente",
  	"soggettoEmittente"
  }
)

@XmlRootElement(name = "FatturaElettronicaHeaderType")

public class FatturaElettronicaHeaderType extends org.openspcoop2.utils.beans.BaseBean implements Serializable , Cloneable {
  public FatturaElettronicaHeaderType() {
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

  public DatiTrasmissioneType getDatiTrasmissione() {
    return this.datiTrasmissione;
  }

  public void setDatiTrasmissione(DatiTrasmissioneType datiTrasmissione) {
    this.datiTrasmissione = datiTrasmissione;
  }

  public CedentePrestatoreType getCedentePrestatore() {
    return this.cedentePrestatore;
  }

  public void setCedentePrestatore(CedentePrestatoreType cedentePrestatore) {
    this.cedentePrestatore = cedentePrestatore;
  }

  public RappresentanteFiscaleType getRappresentanteFiscale() {
    return this.rappresentanteFiscale;
  }

  public void setRappresentanteFiscale(RappresentanteFiscaleType rappresentanteFiscale) {
    this.rappresentanteFiscale = rappresentanteFiscale;
  }

  public CessionarioCommittenteType getCessionarioCommittente() {
    return this.cessionarioCommittente;
  }

  public void setCessionarioCommittente(CessionarioCommittenteType cessionarioCommittente) {
    this.cessionarioCommittente = cessionarioCommittente;
  }

  public TerzoIntermediarioSoggettoEmittenteType getTerzoIntermediarioOSoggettoEmittente() {
    return this.terzoIntermediarioOSoggettoEmittente;
  }

  public void setTerzoIntermediarioOSoggettoEmittente(TerzoIntermediarioSoggettoEmittenteType terzoIntermediarioOSoggettoEmittente) {
    this.terzoIntermediarioOSoggettoEmittente = terzoIntermediarioOSoggettoEmittente;
  }

  public void set_value_soggettoEmittente(String value) {
    this.soggettoEmittente = (SoggettoEmittenteType) SoggettoEmittenteType.toEnumConstantFromString(value);
  }

  public String get_value_soggettoEmittente() {
    if(this.soggettoEmittente == null){
    	return null;
    }else{
    	return this.soggettoEmittente.toString();
    }
  }

  public it.gov.fatturapa.sdi.fatturapa.v1_1.constants.SoggettoEmittenteType getSoggettoEmittente() {
    return this.soggettoEmittente;
  }

  public void setSoggettoEmittente(it.gov.fatturapa.sdi.fatturapa.v1_1.constants.SoggettoEmittenteType soggettoEmittente) {
    this.soggettoEmittente = soggettoEmittente;
  }

  private static final long serialVersionUID = 1L;

  @XmlTransient
  private Long id;



  @XmlElement(name="DatiTrasmissione",required=true,nillable=false)
  protected DatiTrasmissioneType datiTrasmissione;

  @XmlElement(name="CedentePrestatore",required=true,nillable=false)
  protected CedentePrestatoreType cedentePrestatore;

  @XmlElement(name="RappresentanteFiscale",required=false,nillable=false)
  protected RappresentanteFiscaleType rappresentanteFiscale;

  @XmlElement(name="CessionarioCommittente",required=true,nillable=false)
  protected CessionarioCommittenteType cessionarioCommittente;

  @XmlElement(name="TerzoIntermediarioOSoggettoEmittente",required=false,nillable=false)
  protected TerzoIntermediarioSoggettoEmittenteType terzoIntermediarioOSoggettoEmittente;

  @XmlTransient
  protected java.lang.String _value_soggettoEmittente;

  @XmlElement(name="SoggettoEmittente",required=false,nillable=false)
  protected SoggettoEmittenteType soggettoEmittente;

}
