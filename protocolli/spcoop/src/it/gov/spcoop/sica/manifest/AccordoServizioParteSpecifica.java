/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2021 Link.it srl (https://link.it).
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
package it.gov.spcoop.sica.manifest;

import it.gov.spcoop.sica.manifest.constants.TipoAdesione;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import java.io.Serializable;


/** <p>Java class for accordoServizioParteSpecifica complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="accordoServizioParteSpecifica"&gt;
 * 		&lt;sequence&gt;
 * 			&lt;element name="riferimentoParteComune" type="{http://spcoop.gov.it/sica/manifest}anyURI" minOccurs="1" maxOccurs="1"/&gt;
 * 			&lt;element name="specificaPortiAccesso" type="{http://spcoop.gov.it/sica/manifest}SpecificaPortiAccesso" minOccurs="1" maxOccurs="1"/&gt;
 * 			&lt;element name="specificaSicurezza" type="{http://spcoop.gov.it/sica/manifest}SpecificaSicurezza" minOccurs="0" maxOccurs="1"/&gt;
 * 			&lt;element name="specificaLivelliServizio" type="{http://spcoop.gov.it/sica/manifest}SpecificaLivelliServizio" minOccurs="0" maxOccurs="1"/&gt;
 * 		&lt;/sequence&gt;
 * 		&lt;attribute name="adesione" type="{http://spcoop.gov.it/sica/manifest}TipoAdesione" use="required"/&gt;
 * 		&lt;attribute name="erogatore" type="{http://spcoop.gov.it/sica/manifest}anyURI" use="optional"/&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * @version $Rev$, $Date$
 * 
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * */

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "accordoServizioParteSpecifica", 
  propOrder = {
  	"riferimentoParteComune",
  	"specificaPortiAccesso",
  	"specificaSicurezza",
  	"specificaLivelliServizio"
  }
)

@XmlRootElement(name = "accordoServizioParteSpecifica")

public class AccordoServizioParteSpecifica extends org.openspcoop2.utils.beans.BaseBean implements Serializable , Cloneable {
  public AccordoServizioParteSpecifica() {
  }

  public java.net.URI getRiferimentoParteComune() {
    return this.riferimentoParteComune;
  }

  public void setRiferimentoParteComune(java.net.URI riferimentoParteComune) {
    this.riferimentoParteComune = riferimentoParteComune;
  }

  public SpecificaPortiAccesso getSpecificaPortiAccesso() {
    return this.specificaPortiAccesso;
  }

  public void setSpecificaPortiAccesso(SpecificaPortiAccesso specificaPortiAccesso) {
    this.specificaPortiAccesso = specificaPortiAccesso;
  }

  public SpecificaSicurezza getSpecificaSicurezza() {
    return this.specificaSicurezza;
  }

  public void setSpecificaSicurezza(SpecificaSicurezza specificaSicurezza) {
    this.specificaSicurezza = specificaSicurezza;
  }

  public SpecificaLivelliServizio getSpecificaLivelliServizio() {
    return this.specificaLivelliServizio;
  }

  public void setSpecificaLivelliServizio(SpecificaLivelliServizio specificaLivelliServizio) {
    this.specificaLivelliServizio = specificaLivelliServizio;
  }

  public void set_value_adesione(String value) {
    this.adesione = (TipoAdesione) TipoAdesione.toEnumConstantFromString(value);
  }

  public String get_value_adesione() {
    if(this.adesione == null){
    	return null;
    }else{
    	return this.adesione.toString();
    }
  }

  public it.gov.spcoop.sica.manifest.constants.TipoAdesione getAdesione() {
    return this.adesione;
  }

  public void setAdesione(it.gov.spcoop.sica.manifest.constants.TipoAdesione adesione) {
    this.adesione = adesione;
  }

  public java.net.URI getErogatore() {
    return this.erogatore;
  }

  public void setErogatore(java.net.URI erogatore) {
    this.erogatore = erogatore;
  }

  private static final long serialVersionUID = 1L;



  @javax.xml.bind.annotation.XmlSchemaType(name="anyURI")
  @XmlElement(name="riferimentoParteComune",required=true,nillable=false)
  protected java.net.URI riferimentoParteComune;

  @XmlElement(name="specificaPortiAccesso",required=true,nillable=false)
  protected SpecificaPortiAccesso specificaPortiAccesso;

  @XmlElement(name="specificaSicurezza",required=false,nillable=false)
  protected SpecificaSicurezza specificaSicurezza;

  @XmlElement(name="specificaLivelliServizio",required=false,nillable=false)
  protected SpecificaLivelliServizio specificaLivelliServizio;

  @javax.xml.bind.annotation.XmlTransient
  protected java.lang.String _value_adesione;

  @XmlAttribute(name="adesione",required=true)
  protected TipoAdesione adesione;

  @javax.xml.bind.annotation.XmlSchemaType(name="anyURI")
  @XmlAttribute(name="erogatore",required=false)
  protected java.net.URI erogatore;

}
