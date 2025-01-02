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
package org.openspcoop2.core.commons.search;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


/** <p>Java class for servizio-applicativo complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="servizio-applicativo"&gt;
 * 		&lt;sequence&gt;
 * 			&lt;element name="nome" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="1" maxOccurs="1"/&gt;
 * 			&lt;element name="tipologia_fruizione" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="1" maxOccurs="1"/&gt;
 * 			&lt;element name="tipologia_erogazione" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="1" maxOccurs="1"/&gt;
 * 			&lt;element name="tipo" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0" maxOccurs="1"/&gt;
 * 			&lt;element name="as_client" type="{http://www.w3.org/2001/XMLSchema}integer" minOccurs="0" maxOccurs="1"/&gt;
 * 			&lt;element name="tipoauth" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0" maxOccurs="1"/&gt;
 * 			&lt;element name="utente" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0" maxOccurs="1"/&gt;
 * 			&lt;element name="token_policy" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0" maxOccurs="1"/&gt;
 * 			&lt;element name="id-soggetto" type="{http://www.openspcoop2.org/core/commons/search}id-soggetto" minOccurs="1" maxOccurs="1"/&gt;
 * 			&lt;element name="servizio-applicativo-ruolo" type="{http://www.openspcoop2.org/core/commons/search}servizio-applicativo-ruolo" minOccurs="0" maxOccurs="unbounded"/&gt;
 * 			&lt;element name="servizio-applicativo-proprieta-protocollo" type="{http://www.openspcoop2.org/core/commons/search}servizio-applicativo-proprieta-protocollo" minOccurs="0" maxOccurs="unbounded"/&gt;
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
@XmlType(name = "servizio-applicativo", 
  propOrder = {
  	"nome",
  	"tipologiaFruizione",
  	"tipologiaErogazione",
  	"tipo",
  	"asClient",
  	"tipoauth",
  	"utente",
  	"tokenPolicy",
  	"idSoggetto",
  	"servizioApplicativoRuolo",
  	"servizioApplicativoProprietaProtocollo"
  }
)

@XmlRootElement(name = "servizio-applicativo")

public class ServizioApplicativo extends org.openspcoop2.utils.beans.BaseBeanWithId implements Serializable , Cloneable {
  public ServizioApplicativo() {
    super();
  }

  public java.lang.String getNome() {
    return this.nome;
  }

  public void setNome(java.lang.String nome) {
    this.nome = nome;
  }

  public java.lang.String getTipologiaFruizione() {
    return this.tipologiaFruizione;
  }

  public void setTipologiaFruizione(java.lang.String tipologiaFruizione) {
    this.tipologiaFruizione = tipologiaFruizione;
  }

  public java.lang.String getTipologiaErogazione() {
    return this.tipologiaErogazione;
  }

  public void setTipologiaErogazione(java.lang.String tipologiaErogazione) {
    this.tipologiaErogazione = tipologiaErogazione;
  }

  public java.lang.String getTipo() {
    return this.tipo;
  }

  public void setTipo(java.lang.String tipo) {
    this.tipo = tipo;
  }

  public java.lang.Integer getAsClient() {
    return this.asClient;
  }

  public void setAsClient(java.lang.Integer asClient) {
    this.asClient = asClient;
  }

  public java.lang.String getTipoauth() {
    return this.tipoauth;
  }

  public void setTipoauth(java.lang.String tipoauth) {
    this.tipoauth = tipoauth;
  }

  public java.lang.String getUtente() {
    return this.utente;
  }

  public void setUtente(java.lang.String utente) {
    this.utente = utente;
  }

  public java.lang.String getTokenPolicy() {
    return this.tokenPolicy;
  }

  public void setTokenPolicy(java.lang.String tokenPolicy) {
    this.tokenPolicy = tokenPolicy;
  }

  public IdSoggetto getIdSoggetto() {
    return this.idSoggetto;
  }

  public void setIdSoggetto(IdSoggetto idSoggetto) {
    this.idSoggetto = idSoggetto;
  }

  public void addServizioApplicativoRuolo(ServizioApplicativoRuolo servizioApplicativoRuolo) {
    this.servizioApplicativoRuolo.add(servizioApplicativoRuolo);
  }

  public ServizioApplicativoRuolo getServizioApplicativoRuolo(int index) {
    return this.servizioApplicativoRuolo.get( index );
  }

  public ServizioApplicativoRuolo removeServizioApplicativoRuolo(int index) {
    return this.servizioApplicativoRuolo.remove( index );
  }

  public List<ServizioApplicativoRuolo> getServizioApplicativoRuoloList() {
    return this.servizioApplicativoRuolo;
  }

  public void setServizioApplicativoRuoloList(List<ServizioApplicativoRuolo> servizioApplicativoRuolo) {
    this.servizioApplicativoRuolo=servizioApplicativoRuolo;
  }

  public int sizeServizioApplicativoRuoloList() {
    return this.servizioApplicativoRuolo.size();
  }

  public void addServizioApplicativoProprietaProtocollo(ServizioApplicativoProprietaProtocollo servizioApplicativoProprietaProtocollo) {
    this.servizioApplicativoProprietaProtocollo.add(servizioApplicativoProprietaProtocollo);
  }

  public ServizioApplicativoProprietaProtocollo getServizioApplicativoProprietaProtocollo(int index) {
    return this.servizioApplicativoProprietaProtocollo.get( index );
  }

  public ServizioApplicativoProprietaProtocollo removeServizioApplicativoProprietaProtocollo(int index) {
    return this.servizioApplicativoProprietaProtocollo.remove( index );
  }

  public List<ServizioApplicativoProprietaProtocollo> getServizioApplicativoProprietaProtocolloList() {
    return this.servizioApplicativoProprietaProtocollo;
  }

  public void setServizioApplicativoProprietaProtocolloList(List<ServizioApplicativoProprietaProtocollo> servizioApplicativoProprietaProtocollo) {
    this.servizioApplicativoProprietaProtocollo=servizioApplicativoProprietaProtocollo;
  }

  public int sizeServizioApplicativoProprietaProtocolloList() {
    return this.servizioApplicativoProprietaProtocollo.size();
  }

  private static final long serialVersionUID = 1L;

  private static org.openspcoop2.core.commons.search.model.ServizioApplicativoModel modelStaticInstance = null;
  private static synchronized void initModelStaticInstance(){
	  if(org.openspcoop2.core.commons.search.ServizioApplicativo.modelStaticInstance==null){
  			org.openspcoop2.core.commons.search.ServizioApplicativo.modelStaticInstance = new org.openspcoop2.core.commons.search.model.ServizioApplicativoModel();
	  }
  }
  public static org.openspcoop2.core.commons.search.model.ServizioApplicativoModel model(){
	  if(org.openspcoop2.core.commons.search.ServizioApplicativo.modelStaticInstance==null){
	  		initModelStaticInstance();
	  }
	  return org.openspcoop2.core.commons.search.ServizioApplicativo.modelStaticInstance;
  }


  @jakarta.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="nome",required=true,nillable=false)
  protected java.lang.String nome;

  @jakarta.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="tipologia_fruizione",required=true,nillable=false)
  protected java.lang.String tipologiaFruizione;

  @jakarta.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="tipologia_erogazione",required=true,nillable=false)
  protected java.lang.String tipologiaErogazione;

  @jakarta.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="tipo",required=false,nillable=false)
  protected java.lang.String tipo;

  @jakarta.xml.bind.annotation.XmlSchemaType(name="integer")
  @XmlElement(name="as_client",required=false,nillable=false)
  protected java.lang.Integer asClient;

  @jakarta.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="tipoauth",required=false,nillable=false)
  protected java.lang.String tipoauth;

  @jakarta.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="utente",required=false,nillable=false)
  protected java.lang.String utente;

  @jakarta.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="token_policy",required=false,nillable=false)
  protected java.lang.String tokenPolicy;

  @XmlElement(name="id-soggetto",required=true,nillable=false)
  protected IdSoggetto idSoggetto;

  @XmlElement(name="servizio-applicativo-ruolo",required=true,nillable=false)
  private List<ServizioApplicativoRuolo> servizioApplicativoRuolo = new ArrayList<>();

  /**
   * Use method getServizioApplicativoRuoloList
   * @return List&lt;ServizioApplicativoRuolo&gt;
  */
  public List<ServizioApplicativoRuolo> getServizioApplicativoRuolo() {
  	return this.getServizioApplicativoRuoloList();
  }

  /**
   * Use method setServizioApplicativoRuoloList
   * @param servizioApplicativoRuolo List&lt;ServizioApplicativoRuolo&gt;
  */
  public void setServizioApplicativoRuolo(List<ServizioApplicativoRuolo> servizioApplicativoRuolo) {
  	this.setServizioApplicativoRuoloList(servizioApplicativoRuolo);
  }

  /**
   * Use method sizeServizioApplicativoRuoloList
   * @return lunghezza della lista
  */
  public int sizeServizioApplicativoRuolo() {
  	return this.sizeServizioApplicativoRuoloList();
  }

  @XmlElement(name="servizio-applicativo-proprieta-protocollo",required=true,nillable=false)
  private List<ServizioApplicativoProprietaProtocollo> servizioApplicativoProprietaProtocollo = new ArrayList<>();

  /**
   * Use method getServizioApplicativoProprietaProtocolloList
   * @return List&lt;ServizioApplicativoProprietaProtocollo&gt;
  */
  public List<ServizioApplicativoProprietaProtocollo> getServizioApplicativoProprietaProtocollo() {
  	return this.getServizioApplicativoProprietaProtocolloList();
  }

  /**
   * Use method setServizioApplicativoProprietaProtocolloList
   * @param servizioApplicativoProprietaProtocollo List&lt;ServizioApplicativoProprietaProtocollo&gt;
  */
  public void setServizioApplicativoProprietaProtocollo(List<ServizioApplicativoProprietaProtocollo> servizioApplicativoProprietaProtocollo) {
  	this.setServizioApplicativoProprietaProtocolloList(servizioApplicativoProprietaProtocollo);
  }

  /**
   * Use method sizeServizioApplicativoProprietaProtocolloList
   * @return lunghezza della lista
  */
  public int sizeServizioApplicativoProprietaProtocollo() {
  	return this.sizeServizioApplicativoProprietaProtocolloList();
  }

}
