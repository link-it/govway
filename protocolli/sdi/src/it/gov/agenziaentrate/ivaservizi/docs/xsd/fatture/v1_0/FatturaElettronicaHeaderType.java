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

import it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0.constants.SoggettoEmittenteType;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import java.io.Serializable;


/** <p>Java class for FatturaElettronicaHeaderType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="FatturaElettronicaHeaderType"&gt;
 * 		&lt;sequence&gt;
 * 			&lt;element name="DatiTrasmissione" type="{http://ivaservizi.agenziaentrate.gov.it/docs/xsd/fatture/v1.0}DatiTrasmissioneType" minOccurs="1" maxOccurs="1"/&gt;
 * 			&lt;element name="CedentePrestatore" type="{http://ivaservizi.agenziaentrate.gov.it/docs/xsd/fatture/v1.0}CedentePrestatoreType" minOccurs="1" maxOccurs="1"/&gt;
 * 			&lt;element name="CessionarioCommittente" type="{http://ivaservizi.agenziaentrate.gov.it/docs/xsd/fatture/v1.0}CessionarioCommittenteType" minOccurs="1" maxOccurs="1"/&gt;
 * 			&lt;element name="SoggettoEmittente" type="{http://ivaservizi.agenziaentrate.gov.it/docs/xsd/fatture/v1.0}SoggettoEmittenteType" minOccurs="0" maxOccurs="1"/&gt;
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
@XmlType(name = "FatturaElettronicaHeaderType", 
  propOrder = {
  	"datiTrasmissione",
  	"cedentePrestatore",
  	"cessionarioCommittente",
  	"soggettoEmittente"
  }
)

@XmlRootElement(name = "FatturaElettronicaHeaderType")

public class FatturaElettronicaHeaderType extends org.openspcoop2.utils.beans.BaseBean implements Serializable , Cloneable {
  public FatturaElettronicaHeaderType() {
    super();
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

  public CessionarioCommittenteType getCessionarioCommittente() {
    return this.cessionarioCommittente;
  }

  public void setCessionarioCommittente(CessionarioCommittenteType cessionarioCommittente) {
    this.cessionarioCommittente = cessionarioCommittente;
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

  public it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0.constants.SoggettoEmittenteType getSoggettoEmittente() {
    return this.soggettoEmittente;
  }

  public void setSoggettoEmittente(it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0.constants.SoggettoEmittenteType soggettoEmittente) {
    this.soggettoEmittente = soggettoEmittente;
  }

  private static final long serialVersionUID = 1L;



  @XmlElement(name="DatiTrasmissione",required=true,nillable=false)
  protected DatiTrasmissioneType datiTrasmissione;

  @XmlElement(name="CedentePrestatore",required=true,nillable=false)
  protected CedentePrestatoreType cedentePrestatore;

  @XmlElement(name="CessionarioCommittente",required=true,nillable=false)
  protected CessionarioCommittenteType cessionarioCommittente;

  @javax.xml.bind.annotation.XmlTransient
  protected java.lang.String _value_soggettoEmittente;

  @XmlElement(name="SoggettoEmittente",required=false,nillable=false)
  protected SoggettoEmittenteType soggettoEmittente;

}
