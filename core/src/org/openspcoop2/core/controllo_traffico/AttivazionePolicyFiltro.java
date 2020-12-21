/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2020 Link.it srl (https://link.it).
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
package org.openspcoop2.core.controllo_traffico;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;
import org.openspcoop2.core.controllo_traffico.constants.RuoloPolicy;
import java.io.Serializable;


/** <p>Java class for attivazione-policy-filtro complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="attivazione-policy-filtro"&gt;
 * 		&lt;sequence&gt;
 * 			&lt;element name="enabled" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="1" maxOccurs="1" default="false"/&gt;
 * 			&lt;element name="protocollo" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0" maxOccurs="1"/&gt;
 * 			&lt;element name="ruolo-porta" type="{http://www.openspcoop2.org/core/controllo_traffico}ruolo-policy" minOccurs="0" maxOccurs="1"/&gt;
 * 			&lt;element name="nome-porta" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0" maxOccurs="1"/&gt;
 * 			&lt;element name="tipo-fruitore" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0" maxOccurs="1"/&gt;
 * 			&lt;element name="nome-fruitore" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0" maxOccurs="1"/&gt;
 * 			&lt;element name="ruolo-fruitore" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0" maxOccurs="1"/&gt;
 * 			&lt;element name="servizio-applicativo-fruitore" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0" maxOccurs="1"/&gt;
 * 			&lt;element name="tipo-erogatore" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0" maxOccurs="1"/&gt;
 * 			&lt;element name="nome-erogatore" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0" maxOccurs="1"/&gt;
 * 			&lt;element name="ruolo-erogatore" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0" maxOccurs="1"/&gt;
 * 			&lt;element name="servizio-applicativo-erogatore" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0" maxOccurs="1"/&gt;
 * 			&lt;element name="tag" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0" maxOccurs="1"/&gt;
 * 			&lt;element name="tipo-servizio" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0" maxOccurs="1"/&gt;
 * 			&lt;element name="nome-servizio" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0" maxOccurs="1"/&gt;
 * 			&lt;element name="versione-servizio" type="{http://www.w3.org/2001/XMLSchema}integer" minOccurs="0" maxOccurs="1"/&gt;
 * 			&lt;element name="azione" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0" maxOccurs="1"/&gt;
 * 			&lt;element name="informazione-applicativa-enabled" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="1" maxOccurs="1" default="false"/&gt;
 * 			&lt;element name="informazione-applicativa-tipo" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0" maxOccurs="1"/&gt;
 * 			&lt;element name="informazione-applicativa-nome" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0" maxOccurs="1"/&gt;
 * 			&lt;element name="informazione-applicativa-valore" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0" maxOccurs="1"/&gt;
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
@XmlType(name = "attivazione-policy-filtro", 
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
  	"servizioApplicativoErogatore",
  	"tag",
  	"tipoServizio",
  	"nomeServizio",
  	"versioneServizio",
  	"azione",
  	"informazioneApplicativaEnabled",
  	"informazioneApplicativaTipo",
  	"informazioneApplicativaNome",
  	"informazioneApplicativaValore"
  }
)

@XmlRootElement(name = "attivazione-policy-filtro")

public class AttivazionePolicyFiltro extends org.openspcoop2.utils.beans.BaseBean implements Serializable , Cloneable {
  public AttivazionePolicyFiltro() {
  }

  public Long getId() {
    if(this.id!=null)
		return this.id;
	else
		return Long.valueOf(-1);
  }

  public void setId(Long id) {
    if(id!=null)
		this.id=id;
	else
		this.id=Long.valueOf(-1);
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

  public void set_value_ruoloPorta(String value) {
    this.ruoloPorta = (RuoloPolicy) RuoloPolicy.toEnumConstantFromString(value);
  }

  public String get_value_ruoloPorta() {
    if(this.ruoloPorta == null){
    	return null;
    }else{
    	return this.ruoloPorta.toString();
    }
  }

  public org.openspcoop2.core.controllo_traffico.constants.RuoloPolicy getRuoloPorta() {
    return this.ruoloPorta;
  }

  public void setRuoloPorta(org.openspcoop2.core.controllo_traffico.constants.RuoloPolicy ruoloPorta) {
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

  public java.lang.String getServizioApplicativoErogatore() {
    return this.servizioApplicativoErogatore;
  }

  public void setServizioApplicativoErogatore(java.lang.String servizioApplicativoErogatore) {
    this.servizioApplicativoErogatore = servizioApplicativoErogatore;
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

  public boolean isInformazioneApplicativaEnabled() {
    return this.informazioneApplicativaEnabled;
  }

  public boolean getInformazioneApplicativaEnabled() {
    return this.informazioneApplicativaEnabled;
  }

  public void setInformazioneApplicativaEnabled(boolean informazioneApplicativaEnabled) {
    this.informazioneApplicativaEnabled = informazioneApplicativaEnabled;
  }

  public java.lang.String getInformazioneApplicativaTipo() {
    return this.informazioneApplicativaTipo;
  }

  public void setInformazioneApplicativaTipo(java.lang.String informazioneApplicativaTipo) {
    this.informazioneApplicativaTipo = informazioneApplicativaTipo;
  }

  public java.lang.String getInformazioneApplicativaNome() {
    return this.informazioneApplicativaNome;
  }

  public void setInformazioneApplicativaNome(java.lang.String informazioneApplicativaNome) {
    this.informazioneApplicativaNome = informazioneApplicativaNome;
  }

  public java.lang.String getInformazioneApplicativaValore() {
    return this.informazioneApplicativaValore;
  }

  public void setInformazioneApplicativaValore(java.lang.String informazioneApplicativaValore) {
    this.informazioneApplicativaValore = informazioneApplicativaValore;
  }

  private static final long serialVersionUID = 1L;

  @XmlTransient
  private Long id;



  @javax.xml.bind.annotation.XmlSchemaType(name="boolean")
  @XmlElement(name="enabled",required=true,nillable=false,defaultValue="false")
  protected boolean enabled = false;

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="protocollo",required=false,nillable=false)
  protected java.lang.String protocollo;

  @javax.xml.bind.annotation.XmlTransient
  protected java.lang.String _value_ruoloPorta;

  @XmlElement(name="ruolo-porta",required=false,nillable=false)
  protected RuoloPolicy ruoloPorta;

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="nome-porta",required=false,nillable=false)
  protected java.lang.String nomePorta;

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="tipo-fruitore",required=false,nillable=false)
  protected java.lang.String tipoFruitore;

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="nome-fruitore",required=false,nillable=false)
  protected java.lang.String nomeFruitore;

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="ruolo-fruitore",required=false,nillable=false)
  protected java.lang.String ruoloFruitore;

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="servizio-applicativo-fruitore",required=false,nillable=false)
  protected java.lang.String servizioApplicativoFruitore;

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="tipo-erogatore",required=false,nillable=false)
  protected java.lang.String tipoErogatore;

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="nome-erogatore",required=false,nillable=false)
  protected java.lang.String nomeErogatore;

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="ruolo-erogatore",required=false,nillable=false)
  protected java.lang.String ruoloErogatore;

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="servizio-applicativo-erogatore",required=false,nillable=false)
  protected java.lang.String servizioApplicativoErogatore;

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="tag",required=false,nillable=false)
  protected java.lang.String tag;

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="tipo-servizio",required=false,nillable=false)
  protected java.lang.String tipoServizio;

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="nome-servizio",required=false,nillable=false)
  protected java.lang.String nomeServizio;

  @javax.xml.bind.annotation.XmlSchemaType(name="integer")
  @XmlElement(name="versione-servizio",required=false,nillable=false)
  protected java.lang.Integer versioneServizio;

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="azione",required=false,nillable=false)
  protected java.lang.String azione;

  @javax.xml.bind.annotation.XmlSchemaType(name="boolean")
  @XmlElement(name="informazione-applicativa-enabled",required=true,nillable=false,defaultValue="false")
  protected boolean informazioneApplicativaEnabled = false;

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="informazione-applicativa-tipo",required=false,nillable=false)
  protected java.lang.String informazioneApplicativaTipo;

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="informazione-applicativa-nome",required=false,nillable=false)
  protected java.lang.String informazioneApplicativaNome;

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="informazione-applicativa-valore",required=false,nillable=false)
  protected java.lang.String informazioneApplicativaValore;

}
