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
package org.openspcoop2.monitor.engine.config.transazioni;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;
import org.openspcoop2.monitor.engine.config.transazioni.constants.PosizioneMascheramento;
import org.openspcoop2.monitor.engine.config.transazioni.constants.TipoCompressione;
import org.openspcoop2.monitor.engine.config.transazioni.constants.TipoMascheramento;
import org.openspcoop2.monitor.engine.config.transazioni.constants.TipoMessaggio;
import java.io.Serializable;


/** <p>Java class for configurazione-transazione-risorsa-contenuto complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="configurazione-transazione-risorsa-contenuto"&gt;
 * 		&lt;sequence&gt;
 * 			&lt;element name="abilita-anonimizzazione" type="{http://www.w3.org/2001/XMLSchema}integer" minOccurs="1" maxOccurs="1" default="0"/&gt;
 * 			&lt;element name="abilita-compressione" type="{http://www.w3.org/2001/XMLSchema}integer" minOccurs="1" maxOccurs="1" default="0"/&gt;
 * 			&lt;element name="tipo-compressione" type="{http://www.openspcoop2.org/monitor/engine/config/transazioni}tipo-compressione" minOccurs="0" maxOccurs="1"/&gt;
 * 			&lt;element name="carattere-maschera" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0" maxOccurs="1"/&gt;
 * 			&lt;element name="numero-caratteri-maschera" type="{http://www.w3.org/2001/XMLSchema}integer" minOccurs="0" maxOccurs="1"/&gt;
 * 			&lt;element name="posizionamento-maschera" type="{http://www.openspcoop2.org/monitor/engine/config/transazioni}posizione-mascheramento" minOccurs="0" maxOccurs="1"/&gt;
 * 			&lt;element name="tipo-mascheramento" type="{http://www.openspcoop2.org/monitor/engine/config/transazioni}tipo-mascheramento" minOccurs="0" maxOccurs="1"/&gt;
 * 			&lt;element name="enabled" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="1" maxOccurs="1"/&gt;
 * 			&lt;element name="nome" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="1" maxOccurs="1"/&gt;
 * 			&lt;element name="tipo-messaggio" type="{http://www.openspcoop2.org/monitor/engine/config/transazioni}tipo-messaggio" minOccurs="1" maxOccurs="1"/&gt;
 * 			&lt;element name="xpath" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="1" maxOccurs="1"/&gt;
 * 			&lt;element name="stat-enabled" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="1" maxOccurs="1" default="false"/&gt;
 * 			&lt;element name="id-configurazione-transazione-stato" type="{http://www.openspcoop2.org/monitor/engine/config/transazioni}id-configurazione-transazione-stato" minOccurs="0" maxOccurs="1"/&gt;
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
@XmlType(name = "configurazione-transazione-risorsa-contenuto", 
  propOrder = {
  	"abilitaAnonimizzazione",
  	"abilitaCompressione",
  	"tipoCompressione",
  	"_carattereMaschera_Value",
  	"numeroCaratteriMaschera",
  	"posizionamentoMaschera",
  	"tipoMascheramento",
  	"enabled",
  	"nome",
  	"tipoMessaggio",
  	"xpath",
  	"statEnabled",
  	"idConfigurazioneTransazioneStato"
  }
)

@XmlRootElement(name = "configurazione-transazione-risorsa-contenuto")

public class ConfigurazioneTransazioneRisorsaContenuto extends org.openspcoop2.utils.beans.BaseBean implements Serializable , Cloneable {
  public ConfigurazioneTransazioneRisorsaContenuto() {
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

  public java.lang.Integer getAbilitaAnonimizzazione() {
    return this.abilitaAnonimizzazione;
  }

  public void setAbilitaAnonimizzazione(java.lang.Integer abilitaAnonimizzazione) {
    this.abilitaAnonimizzazione = abilitaAnonimizzazione;
  }

  public java.lang.Integer getAbilitaCompressione() {
    return this.abilitaCompressione;
  }

  public void setAbilitaCompressione(java.lang.Integer abilitaCompressione) {
    this.abilitaCompressione = abilitaCompressione;
  }

  public void set_value_tipoCompressione(String value) {
    this.tipoCompressione = (TipoCompressione) TipoCompressione.toEnumConstantFromString(value);
  }

  public String get_value_tipoCompressione() {
    if(this.tipoCompressione == null){
    	return null;
    }else{
    	return this.tipoCompressione.toString();
    }
  }

  public org.openspcoop2.monitor.engine.config.transazioni.constants.TipoCompressione getTipoCompressione() {
    return this.tipoCompressione;
  }

  public void setTipoCompressione(org.openspcoop2.monitor.engine.config.transazioni.constants.TipoCompressione tipoCompressione) {
    this.tipoCompressione = tipoCompressione;
  }

  public char getCarattereMaschera() {
    if(this._carattereMaschera_Value==null){
  		// primitive default value
  		return '\u0000';
  	}else{
  		return this._carattereMaschera_Value;
  	}

  }

  public void setCarattereMaschera(char carattereMaschera) {
    this._carattereMaschera_Value = carattereMaschera;
  }

  public java.lang.Integer getNumeroCaratteriMaschera() {
    return this.numeroCaratteriMaschera;
  }

  public void setNumeroCaratteriMaschera(java.lang.Integer numeroCaratteriMaschera) {
    this.numeroCaratteriMaschera = numeroCaratteriMaschera;
  }

  public void set_value_posizionamentoMaschera(String value) {
    this.posizionamentoMaschera = (PosizioneMascheramento) PosizioneMascheramento.toEnumConstantFromString(value);
  }

  public String get_value_posizionamentoMaschera() {
    if(this.posizionamentoMaschera == null){
    	return null;
    }else{
    	return this.posizionamentoMaschera.toString();
    }
  }

  public org.openspcoop2.monitor.engine.config.transazioni.constants.PosizioneMascheramento getPosizionamentoMaschera() {
    return this.posizionamentoMaschera;
  }

  public void setPosizionamentoMaschera(org.openspcoop2.monitor.engine.config.transazioni.constants.PosizioneMascheramento posizionamentoMaschera) {
    this.posizionamentoMaschera = posizionamentoMaschera;
  }

  public void set_value_tipoMascheramento(String value) {
    this.tipoMascheramento = (TipoMascheramento) TipoMascheramento.toEnumConstantFromString(value);
  }

  public String get_value_tipoMascheramento() {
    if(this.tipoMascheramento == null){
    	return null;
    }else{
    	return this.tipoMascheramento.toString();
    }
  }

  public org.openspcoop2.monitor.engine.config.transazioni.constants.TipoMascheramento getTipoMascheramento() {
    return this.tipoMascheramento;
  }

  public void setTipoMascheramento(org.openspcoop2.monitor.engine.config.transazioni.constants.TipoMascheramento tipoMascheramento) {
    this.tipoMascheramento = tipoMascheramento;
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

  public java.lang.String getNome() {
    return this.nome;
  }

  public void setNome(java.lang.String nome) {
    this.nome = nome;
  }

  public void set_value_tipoMessaggio(String value) {
    this.tipoMessaggio = (TipoMessaggio) TipoMessaggio.toEnumConstantFromString(value);
  }

  public String get_value_tipoMessaggio() {
    if(this.tipoMessaggio == null){
    	return null;
    }else{
    	return this.tipoMessaggio.toString();
    }
  }

  public org.openspcoop2.monitor.engine.config.transazioni.constants.TipoMessaggio getTipoMessaggio() {
    return this.tipoMessaggio;
  }

  public void setTipoMessaggio(org.openspcoop2.monitor.engine.config.transazioni.constants.TipoMessaggio tipoMessaggio) {
    this.tipoMessaggio = tipoMessaggio;
  }

  public java.lang.String getXpath() {
    return this.xpath;
  }

  public void setXpath(java.lang.String xpath) {
    this.xpath = xpath;
  }

  public boolean isStatEnabled() {
    return this.statEnabled;
  }

  public boolean getStatEnabled() {
    return this.statEnabled;
  }

  public void setStatEnabled(boolean statEnabled) {
    this.statEnabled = statEnabled;
  }

  public IdConfigurazioneTransazioneStato getIdConfigurazioneTransazioneStato() {
    return this.idConfigurazioneTransazioneStato;
  }

  public void setIdConfigurazioneTransazioneStato(IdConfigurazioneTransazioneStato idConfigurazioneTransazioneStato) {
    this.idConfigurazioneTransazioneStato = idConfigurazioneTransazioneStato;
  }

  private static final long serialVersionUID = 1L;

  @XmlTransient
  private Long id;



  @javax.xml.bind.annotation.XmlSchemaType(name="integer")
  @XmlElement(name="abilita-anonimizzazione",required=true,nillable=false,defaultValue="0")
  protected java.lang.Integer abilitaAnonimizzazione = java.lang.Integer.valueOf("0");

  @javax.xml.bind.annotation.XmlSchemaType(name="integer")
  @XmlElement(name="abilita-compressione",required=true,nillable=false,defaultValue="0")
  protected java.lang.Integer abilitaCompressione = java.lang.Integer.valueOf("0");

  @javax.xml.bind.annotation.XmlTransient
  protected java.lang.String _value_tipoCompressione;

  @XmlElement(name="tipo-compressione",required=false,nillable=false)
  protected TipoCompressione tipoCompressione;

  @javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter(org.openspcoop2.utils.jaxb.Char2String.class)
  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="carattere-maschera",required=false,nillable=false)
  protected java.lang.Character _carattereMaschera_Value;

  @javax.xml.bind.annotation.XmlTransient
  protected char carattereMaschera;

  public java.lang.Character get_carattereMaschera_Value() {
  	return this._carattereMaschera_Value;
  }
  public void set_carattereMaschera_Value(java.lang.Character carattereMascheraValue) {
  	this._carattereMaschera_Value = carattereMascheraValue;
  }

  @javax.xml.bind.annotation.XmlSchemaType(name="integer")
  @XmlElement(name="numero-caratteri-maschera",required=false,nillable=false)
  protected java.lang.Integer numeroCaratteriMaschera;

  @javax.xml.bind.annotation.XmlTransient
  protected java.lang.String _value_posizionamentoMaschera;

  @XmlElement(name="posizionamento-maschera",required=false,nillable=false)
  protected PosizioneMascheramento posizionamentoMaschera;

  @javax.xml.bind.annotation.XmlTransient
  protected java.lang.String _value_tipoMascheramento;

  @XmlElement(name="tipo-mascheramento",required=false,nillable=false)
  protected TipoMascheramento tipoMascheramento;

  @javax.xml.bind.annotation.XmlSchemaType(name="boolean")
  @XmlElement(name="enabled",required=true,nillable=false)
  protected boolean enabled;

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="nome",required=true,nillable=false)
  protected java.lang.String nome;

  @javax.xml.bind.annotation.XmlTransient
  protected java.lang.String _value_tipoMessaggio;

  @XmlElement(name="tipo-messaggio",required=true,nillable=false)
  protected TipoMessaggio tipoMessaggio;

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="xpath",required=true,nillable=false)
  protected java.lang.String xpath;

  @javax.xml.bind.annotation.XmlSchemaType(name="boolean")
  @XmlElement(name="stat-enabled",required=true,nillable=false,defaultValue="false")
  protected boolean statEnabled = false;

  @XmlElement(name="id-configurazione-transazione-stato",required=false,nillable=false)
  protected IdConfigurazioneTransazioneStato idConfigurazioneTransazioneStato;

}
