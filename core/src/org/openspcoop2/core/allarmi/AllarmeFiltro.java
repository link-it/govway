/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2025 Link.it srl (https://link.it).
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
package org.openspcoop2.core.allarmi;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;
import org.openspcoop2.core.allarmi.constants.RuoloPorta;
import java.io.Serializable;


/** <p>Java class for allarme-filtro complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="allarme-filtro"&gt;
 * 		&lt;sequence&gt;
 * 			&lt;element name="enabled" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="1" maxOccurs="1" default="false"/&gt;
 * 			&lt;element name="protocollo" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0" maxOccurs="1"/&gt;
 * 			&lt;element name="ruolo-porta" type="{http://www.openspcoop2.org/core/allarmi}ruolo-porta" minOccurs="0" maxOccurs="1"/&gt;
 * 			&lt;element name="nome-porta" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0" maxOccurs="1"/&gt;
 * 			&lt;element name="tipo-fruitore" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0" maxOccurs="1"/&gt;
 * 			&lt;element name="nome-fruitore" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0" maxOccurs="1"/&gt;
 * 			&lt;element name="ruolo-fruitore" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0" maxOccurs="1"/&gt;
 * 			&lt;element name="servizio-applicativo-fruitore" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0" maxOccurs="1"/&gt;
 * 			&lt;element name="tipo-erogatore" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0" maxOccurs="1"/&gt;
 * 			&lt;element name="nome-erogatore" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0" maxOccurs="1"/&gt;
 * 			&lt;element name="ruolo-erogatore" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0" maxOccurs="1"/&gt;
 * 			&lt;element name="tag" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0" maxOccurs="1"/&gt;
 * 			&lt;element name="tipo-servizio" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0" maxOccurs="1"/&gt;
 * 			&lt;element name="nome-servizio" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0" maxOccurs="1"/&gt;
 * 			&lt;element name="versione-servizio" type="{http://www.w3.org/2001/XMLSchema}integer" minOccurs="0" maxOccurs="1"/&gt;
 * 			&lt;element name="azione" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0" maxOccurs="1"/&gt;
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
@XmlType(name = "allarme-filtro", 
  propOrder = {
  	"enabled",
  	"protocollo",
  	"ruoloPorta",
  	"nomePorta",
  	"tipoFruitore",
  	"nomeFruitore",
  	"ruoloFruitore",
  	"servizioApplicativoFruitore",
  	"tipoErogatore",
  	"nomeErogatore",
  	"ruoloErogatore",
  	"tag",
  	"tipoServizio",
  	"nomeServizio",
  	"versioneServizio",
  	"azione"
  }
)

@XmlRootElement(name = "allarme-filtro")

public class AllarmeFiltro extends org.openspcoop2.utils.beans.BaseBeanWithId implements Serializable , Cloneable {
  public AllarmeFiltro() {
    super();
  }

  public boolean isEnabled() {
    return this.enabled;
  }

  public boolean getEnabled() {
    return this.enabled;
  }

  public void setEnabled(boolean enabled) {
    this.enabled = enabled;
  }

  public java.lang.String getProtocollo() {
    return this.protocollo;
  }

  public void setProtocollo(java.lang.String protocollo) {
    this.protocollo = protocollo;
  }

  public void setRuoloPortaRawEnumValue(String value) {
    this.ruoloPorta = (RuoloPorta) RuoloPorta.toEnumConstantFromString(value);
  }

  public String getRuoloPortaRawEnumValue() {
    if(this.ruoloPorta == null){
    	return null;
    }else{
    	return this.ruoloPorta.toString();
    }
  }

  public org.openspcoop2.core.allarmi.constants.RuoloPorta getRuoloPorta() {
    return this.ruoloPorta;
  }

  public void setRuoloPorta(org.openspcoop2.core.allarmi.constants.RuoloPorta ruoloPorta) {
    this.ruoloPorta = ruoloPorta;
  }

  public java.lang.String getNomePorta() {
    return this.nomePorta;
  }

  public void setNomePorta(java.lang.String nomePorta) {
    this.nomePorta = nomePorta;
  }

  public java.lang.String getTipoFruitore() {
    return this.tipoFruitore;
  }

  public void setTipoFruitore(java.lang.String tipoFruitore) {
    this.tipoFruitore = tipoFruitore;
  }

  public java.lang.String getNomeFruitore() {
    return this.nomeFruitore;
  }

  public void setNomeFruitore(java.lang.String nomeFruitore) {
    this.nomeFruitore = nomeFruitore;
  }

  public java.lang.String getRuoloFruitore() {
    return this.ruoloFruitore;
  }

  public void setRuoloFruitore(java.lang.String ruoloFruitore) {
    this.ruoloFruitore = ruoloFruitore;
  }

  public java.lang.String getServizioApplicativoFruitore() {
    return this.servizioApplicativoFruitore;
  }

  public void setServizioApplicativoFruitore(java.lang.String servizioApplicativoFruitore) {
    this.servizioApplicativoFruitore = servizioApplicativoFruitore;
  }

  public java.lang.String getTipoErogatore() {
    return this.tipoErogatore;
  }

  public void setTipoErogatore(java.lang.String tipoErogatore) {
    this.tipoErogatore = tipoErogatore;
  }

  public java.lang.String getNomeErogatore() {
    return this.nomeErogatore;
  }

  public void setNomeErogatore(java.lang.String nomeErogatore) {
    this.nomeErogatore = nomeErogatore;
  }

  public java.lang.String getRuoloErogatore() {
    return this.ruoloErogatore;
  }

  public void setRuoloErogatore(java.lang.String ruoloErogatore) {
    this.ruoloErogatore = ruoloErogatore;
  }

  public java.lang.String getTag() {
    return this.tag;
  }

  public void setTag(java.lang.String tag) {
    this.tag = tag;
  }

  public java.lang.String getTipoServizio() {
    return this.tipoServizio;
  }

  public void setTipoServizio(java.lang.String tipoServizio) {
    this.tipoServizio = tipoServizio;
  }

  public java.lang.String getNomeServizio() {
    return this.nomeServizio;
  }

  public void setNomeServizio(java.lang.String nomeServizio) {
    this.nomeServizio = nomeServizio;
  }

  public java.lang.Integer getVersioneServizio() {
    return this.versioneServizio;
  }

  public void setVersioneServizio(java.lang.Integer versioneServizio) {
    this.versioneServizio = versioneServizio;
  }

  public java.lang.String getAzione() {
    return this.azione;
  }

  public void setAzione(java.lang.String azione) {
    this.azione = azione;
  }

  private static final long serialVersionUID = 1L;



  @jakarta.xml.bind.annotation.XmlSchemaType(name="boolean")
  @XmlElement(name="enabled",required=true,nillable=false,defaultValue="false")
  protected boolean enabled = false;

  @jakarta.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="protocollo",required=false,nillable=false)
  protected java.lang.String protocollo;

  @jakarta.xml.bind.annotation.XmlTransient
  protected java.lang.String ruoloPortaRawEnumValue;

  @XmlElement(name="ruolo-porta",required=false,nillable=false)
  protected RuoloPorta ruoloPorta;

  @jakarta.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="nome-porta",required=false,nillable=false)
  protected java.lang.String nomePorta;

  @jakarta.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="tipo-fruitore",required=false,nillable=false)
  protected java.lang.String tipoFruitore;

  @jakarta.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="nome-fruitore",required=false,nillable=false)
  protected java.lang.String nomeFruitore;

  @jakarta.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="ruolo-fruitore",required=false,nillable=false)
  protected java.lang.String ruoloFruitore;

  @jakarta.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="servizio-applicativo-fruitore",required=false,nillable=false)
  protected java.lang.String servizioApplicativoFruitore;

  @jakarta.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="tipo-erogatore",required=false,nillable=false)
  protected java.lang.String tipoErogatore;

  @jakarta.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="nome-erogatore",required=false,nillable=false)
  protected java.lang.String nomeErogatore;

  @jakarta.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="ruolo-erogatore",required=false,nillable=false)
  protected java.lang.String ruoloErogatore;

  @jakarta.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="tag",required=false,nillable=false)
  protected java.lang.String tag;

  @jakarta.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="tipo-servizio",required=false,nillable=false)
  protected java.lang.String tipoServizio;

  @jakarta.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="nome-servizio",required=false,nillable=false)
  protected java.lang.String nomeServizio;

  @jakarta.xml.bind.annotation.XmlSchemaType(name="integer")
  @XmlElement(name="versione-servizio",required=false,nillable=false)
  protected java.lang.Integer versioneServizio;

  @jakarta.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="azione",required=false,nillable=false)
  protected java.lang.String azione;

}
