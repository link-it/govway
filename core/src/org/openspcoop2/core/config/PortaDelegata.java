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
package org.openspcoop2.core.config;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;
import org.openspcoop2.core.config.constants.StatoFunzionalita;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


/** <p>Java class for porta-delegata complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="porta-delegata">
 * 		&lt;sequence>
 * 			&lt;element name="soggetto-erogatore" type="{http://www.openspcoop2.org/core/config}porta-delegata-soggetto-erogatore" minOccurs="1" maxOccurs="1"/>
 * 			&lt;element name="servizio" type="{http://www.openspcoop2.org/core/config}porta-delegata-servizio" minOccurs="1" maxOccurs="1"/>
 * 			&lt;element name="azione" type="{http://www.openspcoop2.org/core/config}porta-delegata-azione" minOccurs="0" maxOccurs="1"/>
 * 			&lt;element name="servizio-applicativo" type="{http://www.openspcoop2.org/core/config}servizio-applicativo" minOccurs="0" maxOccurs="unbounded"/>
 * 			&lt;element name="mtom-processor" type="{http://www.openspcoop2.org/core/config}mtom-processor" minOccurs="0" maxOccurs="1"/>
 * 			&lt;element name="message-security" type="{http://www.openspcoop2.org/core/config}message-security" minOccurs="0" maxOccurs="1"/>
 * 			&lt;element name="validazione-contenuti-applicativi" type="{http://www.openspcoop2.org/core/config}validazione-contenuti-applicativi" minOccurs="0" maxOccurs="1"/>
 * 			&lt;element name="correlazione-applicativa" type="{http://www.openspcoop2.org/core/config}correlazione-applicativa" minOccurs="0" maxOccurs="1"/>
 * 			&lt;element name="correlazione-applicativa-risposta" type="{http://www.openspcoop2.org/core/config}correlazione-applicativa-risposta" minOccurs="0" maxOccurs="1"/>
 * 		&lt;/sequence>
 * 		&lt;attribute name="id-soggetto" type="{http://www.w3.org/2001/XMLSchema}unsignedLong" use="optional"/>
 * 		&lt;attribute name="id-accordo" type="{http://www.w3.org/2001/XMLSchema}unsignedLong" use="optional"/>
 * 		&lt;attribute name="id-port-type" type="{http://www.w3.org/2001/XMLSchema}unsignedLong" use="optional"/>
 * 		&lt;attribute name="tipo-soggetto-proprietario" type="{http://www.w3.org/2001/XMLSchema}string" use="optional"/>
 * 		&lt;attribute name="nome-soggetto-proprietario" type="{http://www.w3.org/2001/XMLSchema}string" use="optional"/>
 * 		&lt;attribute name="stato-message-security" type="{http://www.w3.org/2001/XMLSchema}string" use="optional"/>
 * 		&lt;attribute name="nome" type="{http://www.w3.org/2001/XMLSchema}string" use="required"/>
 * 		&lt;attribute name="descrizione" type="{http://www.w3.org/2001/XMLSchema}string" use="optional"/>
 * 		&lt;attribute name="location" type="{http://www.w3.org/2001/XMLSchema}string" use="optional"/>
 * 		&lt;attribute name="autenticazione" type="{http://www.w3.org/2001/XMLSchema}string" use="optional" default="ssl"/>
 * 		&lt;attribute name="autorizzazione" type="{http://www.w3.org/2001/XMLSchema}string" use="optional" default="openspcoop"/>
 * 		&lt;attribute name="autorizzazione-contenuto" type="{http://www.w3.org/2001/XMLSchema}string" use="optional"/>
 * 		&lt;attribute name="ricevuta-asincrona-simmetrica" type="{http://www.openspcoop2.org/core/config}StatoFunzionalita" use="optional" default="abilitato"/>
 * 		&lt;attribute name="ricevuta-asincrona-asimmetrica" type="{http://www.openspcoop2.org/core/config}StatoFunzionalita" use="optional" default="abilitato"/>
 * 		&lt;attribute name="integrazione" type="{http://www.w3.org/2001/XMLSchema}string" use="optional"/>
 * 		&lt;attribute name="allega-body" type="{http://www.openspcoop2.org/core/config}StatoFunzionalita" use="optional" default="disabilitato"/>
 * 		&lt;attribute name="scarta-body" type="{http://www.openspcoop2.org/core/config}StatoFunzionalita" use="optional" default="disabilitato"/>
 * 		&lt;attribute name="gestione-manifest" type="{http://www.openspcoop2.org/core/config}StatoFunzionalita" use="optional"/>
 * 		&lt;attribute name="stateless" type="{http://www.openspcoop2.org/core/config}StatoFunzionalita" use="optional"/>
 * 		&lt;attribute name="local-forward" type="{http://www.openspcoop2.org/core/config}StatoFunzionalita" use="optional" default="disabilitato"/>
 * 		&lt;attribute name="ora-registrazione" type="{http://www.w3.org/2001/XMLSchema}dateTime" use="optional"/>
 * 		&lt;attribute name="old-nome-soggetto-proprietario-for-update" type="{http://www.w3.org/2001/XMLSchema}string" use="optional"/>
 * 		&lt;attribute name="old-tipo-soggetto-proprietario-for-update" type="{http://www.w3.org/2001/XMLSchema}string" use="optional"/>
 * &lt;/complexType>
 * </pre>
 * 
 * @version $Rev$, $Date$
 * 
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * */

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "porta-delegata", 
  propOrder = {
  	"soggettoErogatore",
  	"servizio",
  	"azione",
  	"servizioApplicativo",
  	"mtomProcessor",
  	"messageSecurity",
  	"validazioneContenutiApplicativi",
  	"correlazioneApplicativa",
  	"correlazioneApplicativaRisposta"
  }
)

@XmlRootElement(name = "porta-delegata")

public class PortaDelegata extends org.openspcoop2.utils.beans.BaseBean implements Serializable , Cloneable {
  public PortaDelegata() {
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

  public String getOldNomeForUpdate() {
    if(this.oldNomeForUpdate!=null && ("".equals(this.oldNomeForUpdate)==false)){
		return this.oldNomeForUpdate.trim();
	}else{
		return null;
	}

  }

  public void setOldNomeForUpdate(String oldNomeForUpdate) {
    this.oldNomeForUpdate=oldNomeForUpdate;
  }

  public String getOldNomeSoggettoProprietarioForUpdate() {
    if(this.oldNomeSoggettoProprietarioForUpdate!=null && ("".equals(this.oldNomeSoggettoProprietarioForUpdate)==false)){
		return this.oldNomeSoggettoProprietarioForUpdate.trim();
	}else{
		return null;
	}

  }

  public void setOldNomeSoggettoProprietarioForUpdate(String oldNomeSoggettoProprietarioForUpdate) {
    this.oldNomeSoggettoProprietarioForUpdate=oldNomeSoggettoProprietarioForUpdate;
  }

  public String getOldTipoSoggettoProprietarioForUpdate() {
    if(this.oldTipoSoggettoProprietarioForUpdate!=null && ("".equals(this.oldTipoSoggettoProprietarioForUpdate)==false)){
		return this.oldTipoSoggettoProprietarioForUpdate.trim();
	}else{
		return null;
	}

  }

  public void setOldTipoSoggettoProprietarioForUpdate(String oldTipoSoggettoProprietarioForUpdate) {
    this.oldTipoSoggettoProprietarioForUpdate=oldTipoSoggettoProprietarioForUpdate;
  }

  public void addExtendedInfo(Object extendedInfo) {
    this.extendedInfo.add(extendedInfo);
  }

  public Object getExtendedInfo(int index) {
    return this.extendedInfo.get( index );
  }

  public Object removeExtendedInfo(int index) {
    return this.extendedInfo.remove( index );
  }

  public List<Object> getExtendedInfoList() {
    return this.extendedInfo;
  }

  public void setExtendedInfoList(List<Object> extendedInfo) {
    this.extendedInfo=extendedInfo;
  }

  public int sizeExtendedInfoList() {
    return this.extendedInfo.size();
  }

  public PortaDelegataSoggettoErogatore getSoggettoErogatore() {
    return this.soggettoErogatore;
  }

  public void setSoggettoErogatore(PortaDelegataSoggettoErogatore soggettoErogatore) {
    this.soggettoErogatore = soggettoErogatore;
  }

  public PortaDelegataServizio getServizio() {
    return this.servizio;
  }

  public void setServizio(PortaDelegataServizio servizio) {
    this.servizio = servizio;
  }

  public PortaDelegataAzione getAzione() {
    return this.azione;
  }

  public void setAzione(PortaDelegataAzione azione) {
    this.azione = azione;
  }

  public void addServizioApplicativo(ServizioApplicativo servizioApplicativo) {
    this.servizioApplicativo.add(servizioApplicativo);
  }

  public ServizioApplicativo getServizioApplicativo(int index) {
    return this.servizioApplicativo.get( index );
  }

  public ServizioApplicativo removeServizioApplicativo(int index) {
    return this.servizioApplicativo.remove( index );
  }

  public List<ServizioApplicativo> getServizioApplicativoList() {
    return this.servizioApplicativo;
  }

  public void setServizioApplicativoList(List<ServizioApplicativo> servizioApplicativo) {
    this.servizioApplicativo=servizioApplicativo;
  }

  public int sizeServizioApplicativoList() {
    return this.servizioApplicativo.size();
  }

  public MtomProcessor getMtomProcessor() {
    return this.mtomProcessor;
  }

  public void setMtomProcessor(MtomProcessor mtomProcessor) {
    this.mtomProcessor = mtomProcessor;
  }

  public MessageSecurity getMessageSecurity() {
    return this.messageSecurity;
  }

  public void setMessageSecurity(MessageSecurity messageSecurity) {
    this.messageSecurity = messageSecurity;
  }

  public ValidazioneContenutiApplicativi getValidazioneContenutiApplicativi() {
    return this.validazioneContenutiApplicativi;
  }

  public void setValidazioneContenutiApplicativi(ValidazioneContenutiApplicativi validazioneContenutiApplicativi) {
    this.validazioneContenutiApplicativi = validazioneContenutiApplicativi;
  }

  public CorrelazioneApplicativa getCorrelazioneApplicativa() {
    return this.correlazioneApplicativa;
  }

  public void setCorrelazioneApplicativa(CorrelazioneApplicativa correlazioneApplicativa) {
    this.correlazioneApplicativa = correlazioneApplicativa;
  }

  public CorrelazioneApplicativaRisposta getCorrelazioneApplicativaRisposta() {
    return this.correlazioneApplicativaRisposta;
  }

  public void setCorrelazioneApplicativaRisposta(CorrelazioneApplicativaRisposta correlazioneApplicativaRisposta) {
    this.correlazioneApplicativaRisposta = correlazioneApplicativaRisposta;
  }

  public java.lang.Long getIdSoggetto() {
    return this.idSoggetto;
  }

  public void setIdSoggetto(java.lang.Long idSoggetto) {
    this.idSoggetto = idSoggetto;
  }

  public java.lang.Long getIdAccordo() {
    return this.idAccordo;
  }

  public void setIdAccordo(java.lang.Long idAccordo) {
    this.idAccordo = idAccordo;
  }

  public java.lang.Long getIdPortType() {
    return this.idPortType;
  }

  public void setIdPortType(java.lang.Long idPortType) {
    this.idPortType = idPortType;
  }

  public java.lang.String getTipoSoggettoProprietario() {
    return this.tipoSoggettoProprietario;
  }

  public void setTipoSoggettoProprietario(java.lang.String tipoSoggettoProprietario) {
    this.tipoSoggettoProprietario = tipoSoggettoProprietario;
  }

  public java.lang.String getNomeSoggettoProprietario() {
    return this.nomeSoggettoProprietario;
  }

  public void setNomeSoggettoProprietario(java.lang.String nomeSoggettoProprietario) {
    this.nomeSoggettoProprietario = nomeSoggettoProprietario;
  }

  public java.lang.String getStatoMessageSecurity() {
    return this.statoMessageSecurity;
  }

  public void setStatoMessageSecurity(java.lang.String statoMessageSecurity) {
    this.statoMessageSecurity = statoMessageSecurity;
  }

  public java.lang.String getNome() {
    return this.nome;
  }

  public void setNome(java.lang.String nome) {
    this.nome = nome;
  }

  public java.lang.String getDescrizione() {
    return this.descrizione;
  }

  public void setDescrizione(java.lang.String descrizione) {
    this.descrizione = descrizione;
  }

  public java.lang.String getLocation() {
    return this.location;
  }

  public void setLocation(java.lang.String location) {
    this.location = location;
  }

  public java.lang.String getAutenticazione() {
    return this.autenticazione;
  }

  public void setAutenticazione(java.lang.String autenticazione) {
    this.autenticazione = autenticazione;
  }

  public java.lang.String getAutorizzazione() {
    return this.autorizzazione;
  }

  public void setAutorizzazione(java.lang.String autorizzazione) {
    this.autorizzazione = autorizzazione;
  }

  public java.lang.String getAutorizzazioneContenuto() {
    return this.autorizzazioneContenuto;
  }

  public void setAutorizzazioneContenuto(java.lang.String autorizzazioneContenuto) {
    this.autorizzazioneContenuto = autorizzazioneContenuto;
  }

  public void set_value_ricevutaAsincronaSimmetrica(String value) {
    this.ricevutaAsincronaSimmetrica = (StatoFunzionalita) StatoFunzionalita.toEnumConstantFromString(value);
  }

  public String get_value_ricevutaAsincronaSimmetrica() {
    if(this.ricevutaAsincronaSimmetrica == null){
    	return null;
    }else{
    	return this.ricevutaAsincronaSimmetrica.toString();
    }
  }

  public org.openspcoop2.core.config.constants.StatoFunzionalita getRicevutaAsincronaSimmetrica() {
    return this.ricevutaAsincronaSimmetrica;
  }

  public void setRicevutaAsincronaSimmetrica(org.openspcoop2.core.config.constants.StatoFunzionalita ricevutaAsincronaSimmetrica) {
    this.ricevutaAsincronaSimmetrica = ricevutaAsincronaSimmetrica;
  }

  public void set_value_ricevutaAsincronaAsimmetrica(String value) {
    this.ricevutaAsincronaAsimmetrica = (StatoFunzionalita) StatoFunzionalita.toEnumConstantFromString(value);
  }

  public String get_value_ricevutaAsincronaAsimmetrica() {
    if(this.ricevutaAsincronaAsimmetrica == null){
    	return null;
    }else{
    	return this.ricevutaAsincronaAsimmetrica.toString();
    }
  }

  public org.openspcoop2.core.config.constants.StatoFunzionalita getRicevutaAsincronaAsimmetrica() {
    return this.ricevutaAsincronaAsimmetrica;
  }

  public void setRicevutaAsincronaAsimmetrica(org.openspcoop2.core.config.constants.StatoFunzionalita ricevutaAsincronaAsimmetrica) {
    this.ricevutaAsincronaAsimmetrica = ricevutaAsincronaAsimmetrica;
  }

  public java.lang.String getIntegrazione() {
    return this.integrazione;
  }

  public void setIntegrazione(java.lang.String integrazione) {
    this.integrazione = integrazione;
  }

  public void set_value_allegaBody(String value) {
    this.allegaBody = (StatoFunzionalita) StatoFunzionalita.toEnumConstantFromString(value);
  }

  public String get_value_allegaBody() {
    if(this.allegaBody == null){
    	return null;
    }else{
    	return this.allegaBody.toString();
    }
  }

  public org.openspcoop2.core.config.constants.StatoFunzionalita getAllegaBody() {
    return this.allegaBody;
  }

  public void setAllegaBody(org.openspcoop2.core.config.constants.StatoFunzionalita allegaBody) {
    this.allegaBody = allegaBody;
  }

  public void set_value_scartaBody(String value) {
    this.scartaBody = (StatoFunzionalita) StatoFunzionalita.toEnumConstantFromString(value);
  }

  public String get_value_scartaBody() {
    if(this.scartaBody == null){
    	return null;
    }else{
    	return this.scartaBody.toString();
    }
  }

  public org.openspcoop2.core.config.constants.StatoFunzionalita getScartaBody() {
    return this.scartaBody;
  }

  public void setScartaBody(org.openspcoop2.core.config.constants.StatoFunzionalita scartaBody) {
    this.scartaBody = scartaBody;
  }

  public void set_value_gestioneManifest(String value) {
    this.gestioneManifest = (StatoFunzionalita) StatoFunzionalita.toEnumConstantFromString(value);
  }

  public String get_value_gestioneManifest() {
    if(this.gestioneManifest == null){
    	return null;
    }else{
    	return this.gestioneManifest.toString();
    }
  }

  public org.openspcoop2.core.config.constants.StatoFunzionalita getGestioneManifest() {
    return this.gestioneManifest;
  }

  public void setGestioneManifest(org.openspcoop2.core.config.constants.StatoFunzionalita gestioneManifest) {
    this.gestioneManifest = gestioneManifest;
  }

  public void set_value_stateless(String value) {
    this.stateless = (StatoFunzionalita) StatoFunzionalita.toEnumConstantFromString(value);
  }

  public String get_value_stateless() {
    if(this.stateless == null){
    	return null;
    }else{
    	return this.stateless.toString();
    }
  }

  public org.openspcoop2.core.config.constants.StatoFunzionalita getStateless() {
    return this.stateless;
  }

  public void setStateless(org.openspcoop2.core.config.constants.StatoFunzionalita stateless) {
    this.stateless = stateless;
  }

  public void set_value_localForward(String value) {
    this.localForward = (StatoFunzionalita) StatoFunzionalita.toEnumConstantFromString(value);
  }

  public String get_value_localForward() {
    if(this.localForward == null){
    	return null;
    }else{
    	return this.localForward.toString();
    }
  }

  public org.openspcoop2.core.config.constants.StatoFunzionalita getLocalForward() {
    return this.localForward;
  }

  public void setLocalForward(org.openspcoop2.core.config.constants.StatoFunzionalita localForward) {
    this.localForward = localForward;
  }

  public java.util.Date getOraRegistrazione() {
    return this.oraRegistrazione;
  }

  public void setOraRegistrazione(java.util.Date oraRegistrazione) {
    this.oraRegistrazione = oraRegistrazione;
  }

  private static final long serialVersionUID = 1L;

  @XmlTransient
  private Long id;

  private static org.openspcoop2.core.config.model.PortaDelegataModel modelStaticInstance = null;
  private static synchronized void initModelStaticInstance(){
	  if(org.openspcoop2.core.config.PortaDelegata.modelStaticInstance==null){
  			org.openspcoop2.core.config.PortaDelegata.modelStaticInstance = new org.openspcoop2.core.config.model.PortaDelegataModel();
	  }
  }
  public static org.openspcoop2.core.config.model.PortaDelegataModel model(){
	  if(org.openspcoop2.core.config.PortaDelegata.modelStaticInstance==null){
	  		initModelStaticInstance();
	  }
	  return org.openspcoop2.core.config.PortaDelegata.modelStaticInstance;
  }


  @XmlTransient
  private String oldNomeForUpdate;

  @javax.xml.bind.annotation.XmlTransient
  protected String oldNomeSoggettoProprietarioForUpdate;

  @javax.xml.bind.annotation.XmlTransient
  protected String oldTipoSoggettoProprietarioForUpdate;

  @javax.xml.bind.annotation.XmlTransient
  protected List<Object> extendedInfo = new ArrayList<Object>();

  /**
   * @deprecated Use method getExtendedInfoList
   * @return List<Object>
  */
  @Deprecated
  public List<Object> getExtendedInfo() {
  	return this.extendedInfo;
  }

  /**
   * @deprecated Use method setExtendedInfoList
   * @param extendedInfo List<Object>
  */
  @Deprecated
  public void setExtendedInfo(List<Object> extendedInfo) {
  	this.extendedInfo=extendedInfo;
  }

  /**
   * @deprecated Use method sizeExtendedInfoList
   * @return lunghezza della lista
  */
  @Deprecated
  public int sizeExtendedInfo() {
  	return this.extendedInfo.size();
  }

  @XmlElement(name="soggetto-erogatore",required=true,nillable=false)
  protected PortaDelegataSoggettoErogatore soggettoErogatore;

  @XmlElement(name="servizio",required=true,nillable=false)
  protected PortaDelegataServizio servizio;

  @XmlElement(name="azione",required=false,nillable=false)
  protected PortaDelegataAzione azione;

  @XmlElement(name="servizio-applicativo",required=true,nillable=false)
  protected List<ServizioApplicativo> servizioApplicativo = new ArrayList<ServizioApplicativo>();

  /**
   * @deprecated Use method getServizioApplicativoList
   * @return List<ServizioApplicativo>
  */
  @Deprecated
  public List<ServizioApplicativo> getServizioApplicativo() {
  	return this.servizioApplicativo;
  }

  /**
   * @deprecated Use method setServizioApplicativoList
   * @param servizioApplicativo List<ServizioApplicativo>
  */
  @Deprecated
  public void setServizioApplicativo(List<ServizioApplicativo> servizioApplicativo) {
  	this.servizioApplicativo=servizioApplicativo;
  }

  /**
   * @deprecated Use method sizeServizioApplicativoList
   * @return lunghezza della lista
  */
  @Deprecated
  public int sizeServizioApplicativo() {
  	return this.servizioApplicativo.size();
  }

  @XmlElement(name="mtom-processor",required=false,nillable=false)
  protected MtomProcessor mtomProcessor;

  @XmlElement(name="message-security",required=false,nillable=false)
  protected MessageSecurity messageSecurity;

  @XmlElement(name="validazione-contenuti-applicativi",required=false,nillable=false)
  protected ValidazioneContenutiApplicativi validazioneContenutiApplicativi;

  @XmlElement(name="correlazione-applicativa",required=false,nillable=false)
  protected CorrelazioneApplicativa correlazioneApplicativa;

  @XmlElement(name="correlazione-applicativa-risposta",required=false,nillable=false)
  protected CorrelazioneApplicativaRisposta correlazioneApplicativaRisposta;

  @javax.xml.bind.annotation.XmlTransient
  protected java.lang.Long idSoggetto;

  @javax.xml.bind.annotation.XmlTransient
  protected java.lang.Long idAccordo;

  @javax.xml.bind.annotation.XmlTransient
  protected java.lang.Long idPortType;

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlAttribute(name="tipo-soggetto-proprietario",required=false)
  protected java.lang.String tipoSoggettoProprietario;

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlAttribute(name="nome-soggetto-proprietario",required=false)
  protected java.lang.String nomeSoggettoProprietario;

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlAttribute(name="stato-message-security",required=false)
  protected java.lang.String statoMessageSecurity;

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlAttribute(name="nome",required=true)
  protected java.lang.String nome;

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlAttribute(name="descrizione",required=false)
  protected java.lang.String descrizione;

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlAttribute(name="location",required=false)
  protected java.lang.String location;

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlAttribute(name="autenticazione",required=false)
  protected java.lang.String autenticazione = "ssl";

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlAttribute(name="autorizzazione",required=false)
  protected java.lang.String autorizzazione = "openspcoop";

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlAttribute(name="autorizzazione-contenuto",required=false)
  protected java.lang.String autorizzazioneContenuto;

  @XmlTransient
  protected java.lang.String _value_ricevutaAsincronaSimmetrica;

  @XmlAttribute(name="ricevuta-asincrona-simmetrica",required=false)
  protected StatoFunzionalita ricevutaAsincronaSimmetrica = (StatoFunzionalita) StatoFunzionalita.toEnumConstantFromString("abilitato");

  @XmlTransient
  protected java.lang.String _value_ricevutaAsincronaAsimmetrica;

  @XmlAttribute(name="ricevuta-asincrona-asimmetrica",required=false)
  protected StatoFunzionalita ricevutaAsincronaAsimmetrica = (StatoFunzionalita) StatoFunzionalita.toEnumConstantFromString("abilitato");

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlAttribute(name="integrazione",required=false)
  protected java.lang.String integrazione;

  @XmlTransient
  protected java.lang.String _value_allegaBody;

  @XmlAttribute(name="allega-body",required=false)
  protected StatoFunzionalita allegaBody = (StatoFunzionalita) StatoFunzionalita.toEnumConstantFromString("disabilitato");

  @XmlTransient
  protected java.lang.String _value_scartaBody;

  @XmlAttribute(name="scarta-body",required=false)
  protected StatoFunzionalita scartaBody = (StatoFunzionalita) StatoFunzionalita.toEnumConstantFromString("disabilitato");

  @XmlTransient
  protected java.lang.String _value_gestioneManifest;

  @XmlAttribute(name="gestione-manifest",required=false)
  protected StatoFunzionalita gestioneManifest;

  @XmlTransient
  protected java.lang.String _value_stateless;

  @XmlAttribute(name="stateless",required=false)
  protected StatoFunzionalita stateless;

  @XmlTransient
  protected java.lang.String _value_localForward;

  @XmlAttribute(name="local-forward",required=false)
  protected StatoFunzionalita localForward = (StatoFunzionalita) StatoFunzionalita.toEnumConstantFromString("disabilitato");

  @javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter(org.openspcoop2.utils.jaxb.DateTime2String.class)
  @javax.xml.bind.annotation.XmlSchemaType(name="dateTime")
  @XmlAttribute(name="ora-registrazione",required=false)
  protected java.util.Date oraRegistrazione;

}
