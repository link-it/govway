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
package org.openspcoop2.core.config;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;
import org.openspcoop2.core.config.constants.StatoFunzionalita;
import org.openspcoop2.core.id.IDPortaDelegata;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


/** <p>Java class for porta-delegata complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="porta-delegata"&gt;
 * 		&lt;sequence&gt;
 * 			&lt;element name="soggetto-erogatore" type="{http://www.openspcoop2.org/core/config}porta-delegata-soggetto-erogatore" minOccurs="1" maxOccurs="1"/&gt;
 * 			&lt;element name="servizio" type="{http://www.openspcoop2.org/core/config}porta-delegata-servizio" minOccurs="1" maxOccurs="1"/&gt;
 * 			&lt;element name="azione" type="{http://www.openspcoop2.org/core/config}porta-delegata-azione" minOccurs="0" maxOccurs="1"/&gt;
 * 			&lt;element name="proprieta-autenticazione" type="{http://www.openspcoop2.org/core/config}proprieta" minOccurs="0" maxOccurs="unbounded"/&gt;
 * 			&lt;element name="proprieta-autorizzazione" type="{http://www.openspcoop2.org/core/config}proprieta" minOccurs="0" maxOccurs="unbounded"/&gt;
 * 			&lt;element name="proprieta-autorizzazione-contenuto" type="{http://www.openspcoop2.org/core/config}proprieta" minOccurs="0" maxOccurs="unbounded"/&gt;
 * 			&lt;element name="xacml-policy" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0" maxOccurs="1"/&gt;
 * 			&lt;element name="servizio-applicativo" type="{http://www.openspcoop2.org/core/config}porta-delegata-servizio-applicativo" minOccurs="0" maxOccurs="unbounded"/&gt;
 * 			&lt;element name="ruoli" type="{http://www.openspcoop2.org/core/config}autorizzazione-ruoli" minOccurs="0" maxOccurs="1"/&gt;
 * 			&lt;element name="scope" type="{http://www.openspcoop2.org/core/config}autorizzazione-scope" minOccurs="0" maxOccurs="1"/&gt;
 * 			&lt;element name="gestione-token" type="{http://www.openspcoop2.org/core/config}gestione-token" minOccurs="0" maxOccurs="1"/&gt;
 * 			&lt;element name="local-forward" type="{http://www.openspcoop2.org/core/config}porta-delegata-local-forward" minOccurs="0" maxOccurs="1"/&gt;
 * 			&lt;element name="proprieta" type="{http://www.openspcoop2.org/core/config}proprieta" minOccurs="0" maxOccurs="unbounded"/&gt;
 * 			&lt;element name="mtom-processor" type="{http://www.openspcoop2.org/core/config}mtom-processor" minOccurs="0" maxOccurs="1"/&gt;
 * 			&lt;element name="message-security" type="{http://www.openspcoop2.org/core/config}message-security" minOccurs="0" maxOccurs="1"/&gt;
 * 			&lt;element name="validazione-contenuti-applicativi" type="{http://www.openspcoop2.org/core/config}validazione-contenuti-applicativi" minOccurs="0" maxOccurs="1"/&gt;
 * 			&lt;element name="correlazione-applicativa" type="{http://www.openspcoop2.org/core/config}correlazione-applicativa" minOccurs="0" maxOccurs="1"/&gt;
 * 			&lt;element name="correlazione-applicativa-risposta" type="{http://www.openspcoop2.org/core/config}correlazione-applicativa-risposta" minOccurs="0" maxOccurs="1"/&gt;
 * 			&lt;element name="dump" type="{http://www.openspcoop2.org/core/config}dump-configurazione" minOccurs="0" maxOccurs="1"/&gt;
 * 			&lt;element name="tracciamento" type="{http://www.openspcoop2.org/core/config}porta-tracciamento" minOccurs="0" maxOccurs="1"/&gt;
 * 			&lt;element name="gestione-cors" type="{http://www.openspcoop2.org/core/config}cors-configurazione" minOccurs="0" maxOccurs="1"/&gt;
 * 			&lt;element name="response-caching" type="{http://www.openspcoop2.org/core/config}response-caching-configurazione" minOccurs="0" maxOccurs="1"/&gt;
 * 			&lt;element name="trasformazioni" type="{http://www.openspcoop2.org/core/config}trasformazioni" minOccurs="0" maxOccurs="1"/&gt;
 * 		&lt;/sequence&gt;
 * 		&lt;attribute name="id-soggetto" type="{http://www.w3.org/2001/XMLSchema}unsignedLong" use="optional"/&gt;
 * 		&lt;attribute name="id-accordo" type="{http://www.w3.org/2001/XMLSchema}unsignedLong" use="optional"/&gt;
 * 		&lt;attribute name="id-port-type" type="{http://www.w3.org/2001/XMLSchema}unsignedLong" use="optional"/&gt;
 * 		&lt;attribute name="tipo-soggetto-proprietario" type="{http://www.w3.org/2001/XMLSchema}string" use="optional"/&gt;
 * 		&lt;attribute name="nome-soggetto-proprietario" type="{http://www.w3.org/2001/XMLSchema}string" use="optional"/&gt;
 * 		&lt;attribute name="stato-message-security" type="{http://www.w3.org/2001/XMLSchema}string" use="optional"/&gt;
 * 		&lt;attribute name="nome" type="{http://www.w3.org/2001/XMLSchema}string" use="required"/&gt;
 * 		&lt;attribute name="descrizione" type="{http://www.w3.org/2001/XMLSchema}string" use="optional"/&gt;
 * 		&lt;attribute name="autenticazione" type="{http://www.w3.org/2001/XMLSchema}string" use="optional" default="ssl"/&gt;
 * 		&lt;attribute name="autenticazione-opzionale" type="{http://www.openspcoop2.org/core/config}StatoFunzionalita" use="optional" default="disabilitato"/&gt;
 * 		&lt;attribute name="autorizzazione" type="{http://www.w3.org/2001/XMLSchema}string" use="optional" default="authenticated"/&gt;
 * 		&lt;attribute name="autorizzazione-contenuto" type="{http://www.w3.org/2001/XMLSchema}string" use="optional"/&gt;
 * 		&lt;attribute name="ricevuta-asincrona-simmetrica" type="{http://www.openspcoop2.org/core/config}StatoFunzionalita" use="optional" default="abilitato"/&gt;
 * 		&lt;attribute name="ricevuta-asincrona-asimmetrica" type="{http://www.openspcoop2.org/core/config}StatoFunzionalita" use="optional" default="abilitato"/&gt;
 * 		&lt;attribute name="integrazione" type="{http://www.w3.org/2001/XMLSchema}string" use="optional"/&gt;
 * 		&lt;attribute name="allega-body" type="{http://www.openspcoop2.org/core/config}StatoFunzionalita" use="optional" default="disabilitato"/&gt;
 * 		&lt;attribute name="scarta-body" type="{http://www.openspcoop2.org/core/config}StatoFunzionalita" use="optional" default="disabilitato"/&gt;
 * 		&lt;attribute name="gestione-manifest" type="{http://www.openspcoop2.org/core/config}StatoFunzionalita" use="optional"/&gt;
 * 		&lt;attribute name="stateless" type="{http://www.openspcoop2.org/core/config}StatoFunzionalita" use="optional"/&gt;
 * 		&lt;attribute name="ricerca-porta-azione-delegata" type="{http://www.openspcoop2.org/core/config}StatoFunzionalita" use="optional" default="disabilitato"/&gt;
 * 		&lt;attribute name="stato" type="{http://www.openspcoop2.org/core/config}StatoFunzionalita" use="optional" default="abilitato"/&gt;
 * 		&lt;attribute name="ora-registrazione" type="{http://www.w3.org/2001/XMLSchema}dateTime" use="optional"/&gt;
 * 		&lt;attribute name="options" type="{http://www.w3.org/2001/XMLSchema}string" use="optional"/&gt;
 * 		&lt;attribute name="canale" type="{http://www.w3.org/2001/XMLSchema}string" use="optional"/&gt;
 * &lt;/complexType&gt;
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
  	"proprietaAutenticazione",
  	"proprietaAutorizzazione",
  	"proprietaAutorizzazioneContenuto",
  	"xacmlPolicy",
  	"servizioApplicativo",
  	"ruoli",
  	"scope",
  	"gestioneToken",
  	"localForward",
  	"proprieta",
  	"mtomProcessor",
  	"messageSecurity",
  	"validazioneContenutiApplicativi",
  	"correlazioneApplicativa",
  	"correlazioneApplicativaRisposta",
  	"dump",
  	"tracciamento",
  	"gestioneCors",
  	"responseCaching",
  	"trasformazioni"
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
		return Long.valueOf(-1);
  }

  public void setId(Long id) {
    if(id!=null)
		this.id=id;
	else
		this.id=Long.valueOf(-1);
  }

  public IDPortaDelegata getOldIDPortaDelegataForUpdate() {
    return this.oldIDPortaDelegataForUpdate;
  }

  public void setOldIDPortaDelegataForUpdate(IDPortaDelegata oldIDPortaDelegataForUpdate) {
    this.oldIDPortaDelegataForUpdate=oldIDPortaDelegataForUpdate;
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

  public void addProprietaAutenticazione(Proprieta proprietaAutenticazione) {
    this.proprietaAutenticazione.add(proprietaAutenticazione);
  }

  public Proprieta getProprietaAutenticazione(int index) {
    return this.proprietaAutenticazione.get( index );
  }

  public Proprieta removeProprietaAutenticazione(int index) {
    return this.proprietaAutenticazione.remove( index );
  }

  public List<Proprieta> getProprietaAutenticazioneList() {
    return this.proprietaAutenticazione;
  }

  public void setProprietaAutenticazioneList(List<Proprieta> proprietaAutenticazione) {
    this.proprietaAutenticazione=proprietaAutenticazione;
  }

  public int sizeProprietaAutenticazioneList() {
    return this.proprietaAutenticazione.size();
  }

  public void addProprietaAutorizzazione(Proprieta proprietaAutorizzazione) {
    this.proprietaAutorizzazione.add(proprietaAutorizzazione);
  }

  public Proprieta getProprietaAutorizzazione(int index) {
    return this.proprietaAutorizzazione.get( index );
  }

  public Proprieta removeProprietaAutorizzazione(int index) {
    return this.proprietaAutorizzazione.remove( index );
  }

  public List<Proprieta> getProprietaAutorizzazioneList() {
    return this.proprietaAutorizzazione;
  }

  public void setProprietaAutorizzazioneList(List<Proprieta> proprietaAutorizzazione) {
    this.proprietaAutorizzazione=proprietaAutorizzazione;
  }

  public int sizeProprietaAutorizzazioneList() {
    return this.proprietaAutorizzazione.size();
  }

  public void addProprietaAutorizzazioneContenuto(Proprieta proprietaAutorizzazioneContenuto) {
    this.proprietaAutorizzazioneContenuto.add(proprietaAutorizzazioneContenuto);
  }

  public Proprieta getProprietaAutorizzazioneContenuto(int index) {
    return this.proprietaAutorizzazioneContenuto.get( index );
  }

  public Proprieta removeProprietaAutorizzazioneContenuto(int index) {
    return this.proprietaAutorizzazioneContenuto.remove( index );
  }

  public List<Proprieta> getProprietaAutorizzazioneContenutoList() {
    return this.proprietaAutorizzazioneContenuto;
  }

  public void setProprietaAutorizzazioneContenutoList(List<Proprieta> proprietaAutorizzazioneContenuto) {
    this.proprietaAutorizzazioneContenuto=proprietaAutorizzazioneContenuto;
  }

  public int sizeProprietaAutorizzazioneContenutoList() {
    return this.proprietaAutorizzazioneContenuto.size();
  }

  public java.lang.String getXacmlPolicy() {
    return this.xacmlPolicy;
  }

  public void setXacmlPolicy(java.lang.String xacmlPolicy) {
    this.xacmlPolicy = xacmlPolicy;
  }

  public void addServizioApplicativo(PortaDelegataServizioApplicativo servizioApplicativo) {
    this.servizioApplicativo.add(servizioApplicativo);
  }

  public PortaDelegataServizioApplicativo getServizioApplicativo(int index) {
    return this.servizioApplicativo.get( index );
  }

  public PortaDelegataServizioApplicativo removeServizioApplicativo(int index) {
    return this.servizioApplicativo.remove( index );
  }

  public List<PortaDelegataServizioApplicativo> getServizioApplicativoList() {
    return this.servizioApplicativo;
  }

  public void setServizioApplicativoList(List<PortaDelegataServizioApplicativo> servizioApplicativo) {
    this.servizioApplicativo=servizioApplicativo;
  }

  public int sizeServizioApplicativoList() {
    return this.servizioApplicativo.size();
  }

  public AutorizzazioneRuoli getRuoli() {
    return this.ruoli;
  }

  public void setRuoli(AutorizzazioneRuoli ruoli) {
    this.ruoli = ruoli;
  }

  public AutorizzazioneScope getScope() {
    return this.scope;
  }

  public void setScope(AutorizzazioneScope scope) {
    this.scope = scope;
  }

  public GestioneToken getGestioneToken() {
    return this.gestioneToken;
  }

  public void setGestioneToken(GestioneToken gestioneToken) {
    this.gestioneToken = gestioneToken;
  }

  public PortaDelegataLocalForward getLocalForward() {
    return this.localForward;
  }

  public void setLocalForward(PortaDelegataLocalForward localForward) {
    this.localForward = localForward;
  }

  public void addProprieta(Proprieta proprieta) {
    this.proprieta.add(proprieta);
  }

  public Proprieta getProprieta(int index) {
    return this.proprieta.get( index );
  }

  public Proprieta removeProprieta(int index) {
    return this.proprieta.remove( index );
  }

  public List<Proprieta> getProprietaList() {
    return this.proprieta;
  }

  public void setProprietaList(List<Proprieta> proprieta) {
    this.proprieta=proprieta;
  }

  public int sizeProprietaList() {
    return this.proprieta.size();
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

  public DumpConfigurazione getDump() {
    return this.dump;
  }

  public void setDump(DumpConfigurazione dump) {
    this.dump = dump;
  }

  public PortaTracciamento getTracciamento() {
    return this.tracciamento;
  }

  public void setTracciamento(PortaTracciamento tracciamento) {
    this.tracciamento = tracciamento;
  }

  public CorsConfigurazione getGestioneCors() {
    return this.gestioneCors;
  }

  public void setGestioneCors(CorsConfigurazione gestioneCors) {
    this.gestioneCors = gestioneCors;
  }

  public ResponseCachingConfigurazione getResponseCaching() {
    return this.responseCaching;
  }

  public void setResponseCaching(ResponseCachingConfigurazione responseCaching) {
    this.responseCaching = responseCaching;
  }

  public Trasformazioni getTrasformazioni() {
    return this.trasformazioni;
  }

  public void setTrasformazioni(Trasformazioni trasformazioni) {
    this.trasformazioni = trasformazioni;
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

  public java.lang.String getAutenticazione() {
    return this.autenticazione;
  }

  public void setAutenticazione(java.lang.String autenticazione) {
    this.autenticazione = autenticazione;
  }

  public void set_value_autenticazioneOpzionale(String value) {
    this.autenticazioneOpzionale = (StatoFunzionalita) StatoFunzionalita.toEnumConstantFromString(value);
  }

  public String get_value_autenticazioneOpzionale() {
    if(this.autenticazioneOpzionale == null){
    	return null;
    }else{
    	return this.autenticazioneOpzionale.toString();
    }
  }

  public org.openspcoop2.core.config.constants.StatoFunzionalita getAutenticazioneOpzionale() {
    return this.autenticazioneOpzionale;
  }

  public void setAutenticazioneOpzionale(org.openspcoop2.core.config.constants.StatoFunzionalita autenticazioneOpzionale) {
    this.autenticazioneOpzionale = autenticazioneOpzionale;
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

  public void set_value_ricercaPortaAzioneDelegata(String value) {
    this.ricercaPortaAzioneDelegata = (StatoFunzionalita) StatoFunzionalita.toEnumConstantFromString(value);
  }

  public String get_value_ricercaPortaAzioneDelegata() {
    if(this.ricercaPortaAzioneDelegata == null){
    	return null;
    }else{
    	return this.ricercaPortaAzioneDelegata.toString();
    }
  }

  public org.openspcoop2.core.config.constants.StatoFunzionalita getRicercaPortaAzioneDelegata() {
    return this.ricercaPortaAzioneDelegata;
  }

  public void setRicercaPortaAzioneDelegata(org.openspcoop2.core.config.constants.StatoFunzionalita ricercaPortaAzioneDelegata) {
    this.ricercaPortaAzioneDelegata = ricercaPortaAzioneDelegata;
  }

  public void set_value_stato(String value) {
    this.stato = (StatoFunzionalita) StatoFunzionalita.toEnumConstantFromString(value);
  }

  public String get_value_stato() {
    if(this.stato == null){
    	return null;
    }else{
    	return this.stato.toString();
    }
  }

  public org.openspcoop2.core.config.constants.StatoFunzionalita getStato() {
    return this.stato;
  }

  public void setStato(org.openspcoop2.core.config.constants.StatoFunzionalita stato) {
    this.stato = stato;
  }

  public java.util.Date getOraRegistrazione() {
    return this.oraRegistrazione;
  }

  public void setOraRegistrazione(java.util.Date oraRegistrazione) {
    this.oraRegistrazione = oraRegistrazione;
  }

  public java.lang.String getOptions() {
    return this.options;
  }

  public void setOptions(java.lang.String options) {
    this.options = options;
  }

  public java.lang.String getCanale() {
    return this.canale;
  }

  public void setCanale(java.lang.String canale) {
    this.canale = canale;
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


  @javax.xml.bind.annotation.XmlTransient
  protected IDPortaDelegata oldIDPortaDelegataForUpdate;

  @javax.xml.bind.annotation.XmlTransient
  protected List<Object> extendedInfo = new ArrayList<Object>();

  /**
   * @deprecated Use method getExtendedInfoList
   * @return List&lt;Object&gt;
  */
  @Deprecated
  public List<Object> getExtendedInfo() {
  	return this.extendedInfo;
  }

  /**
   * @deprecated Use method setExtendedInfoList
   * @param extendedInfo List&lt;Object&gt;
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

  @XmlElement(name="proprieta-autenticazione",required=true,nillable=false)
  protected List<Proprieta> proprietaAutenticazione = new ArrayList<Proprieta>();

  /**
   * @deprecated Use method getProprietaAutenticazioneList
   * @return List&lt;Proprieta&gt;
  */
  @Deprecated
  public List<Proprieta> getProprietaAutenticazione() {
  	return this.proprietaAutenticazione;
  }

  /**
   * @deprecated Use method setProprietaAutenticazioneList
   * @param proprietaAutenticazione List&lt;Proprieta&gt;
  */
  @Deprecated
  public void setProprietaAutenticazione(List<Proprieta> proprietaAutenticazione) {
  	this.proprietaAutenticazione=proprietaAutenticazione;
  }

  /**
   * @deprecated Use method sizeProprietaAutenticazioneList
   * @return lunghezza della lista
  */
  @Deprecated
  public int sizeProprietaAutenticazione() {
  	return this.proprietaAutenticazione.size();
  }

  @XmlElement(name="proprieta-autorizzazione",required=true,nillable=false)
  protected List<Proprieta> proprietaAutorizzazione = new ArrayList<Proprieta>();

  /**
   * @deprecated Use method getProprietaAutorizzazioneList
   * @return List&lt;Proprieta&gt;
  */
  @Deprecated
  public List<Proprieta> getProprietaAutorizzazione() {
  	return this.proprietaAutorizzazione;
  }

  /**
   * @deprecated Use method setProprietaAutorizzazioneList
   * @param proprietaAutorizzazione List&lt;Proprieta&gt;
  */
  @Deprecated
  public void setProprietaAutorizzazione(List<Proprieta> proprietaAutorizzazione) {
  	this.proprietaAutorizzazione=proprietaAutorizzazione;
  }

  /**
   * @deprecated Use method sizeProprietaAutorizzazioneList
   * @return lunghezza della lista
  */
  @Deprecated
  public int sizeProprietaAutorizzazione() {
  	return this.proprietaAutorizzazione.size();
  }

  @XmlElement(name="proprieta-autorizzazione-contenuto",required=true,nillable=false)
  protected List<Proprieta> proprietaAutorizzazioneContenuto = new ArrayList<Proprieta>();

  /**
   * @deprecated Use method getProprietaAutorizzazioneContenutoList
   * @return List&lt;Proprieta&gt;
  */
  @Deprecated
  public List<Proprieta> getProprietaAutorizzazioneContenuto() {
  	return this.proprietaAutorizzazioneContenuto;
  }

  /**
   * @deprecated Use method setProprietaAutorizzazioneContenutoList
   * @param proprietaAutorizzazioneContenuto List&lt;Proprieta&gt;
  */
  @Deprecated
  public void setProprietaAutorizzazioneContenuto(List<Proprieta> proprietaAutorizzazioneContenuto) {
  	this.proprietaAutorizzazioneContenuto=proprietaAutorizzazioneContenuto;
  }

  /**
   * @deprecated Use method sizeProprietaAutorizzazioneContenutoList
   * @return lunghezza della lista
  */
  @Deprecated
  public int sizeProprietaAutorizzazioneContenuto() {
  	return this.proprietaAutorizzazioneContenuto.size();
  }

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="xacml-policy",required=false,nillable=false)
  protected java.lang.String xacmlPolicy;

  @XmlElement(name="servizio-applicativo",required=true,nillable=false)
  protected List<PortaDelegataServizioApplicativo> servizioApplicativo = new ArrayList<PortaDelegataServizioApplicativo>();

  /**
   * @deprecated Use method getServizioApplicativoList
   * @return List&lt;PortaDelegataServizioApplicativo&gt;
  */
  @Deprecated
  public List<PortaDelegataServizioApplicativo> getServizioApplicativo() {
  	return this.servizioApplicativo;
  }

  /**
   * @deprecated Use method setServizioApplicativoList
   * @param servizioApplicativo List&lt;PortaDelegataServizioApplicativo&gt;
  */
  @Deprecated
  public void setServizioApplicativo(List<PortaDelegataServizioApplicativo> servizioApplicativo) {
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

  @XmlElement(name="ruoli",required=false,nillable=false)
  protected AutorizzazioneRuoli ruoli;

  @XmlElement(name="scope",required=false,nillable=false)
  protected AutorizzazioneScope scope;

  @XmlElement(name="gestione-token",required=false,nillable=false)
  protected GestioneToken gestioneToken;

  @XmlElement(name="local-forward",required=false,nillable=false)
  protected PortaDelegataLocalForward localForward;

  @XmlElement(name="proprieta",required=true,nillable=false)
  protected List<Proprieta> proprieta = new ArrayList<Proprieta>();

  /**
   * @deprecated Use method getProprietaList
   * @return List&lt;Proprieta&gt;
  */
  @Deprecated
  public List<Proprieta> getProprieta() {
  	return this.proprieta;
  }

  /**
   * @deprecated Use method setProprietaList
   * @param proprieta List&lt;Proprieta&gt;
  */
  @Deprecated
  public void setProprieta(List<Proprieta> proprieta) {
  	this.proprieta=proprieta;
  }

  /**
   * @deprecated Use method sizeProprietaList
   * @return lunghezza della lista
  */
  @Deprecated
  public int sizeProprieta() {
  	return this.proprieta.size();
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

  @XmlElement(name="dump",required=false,nillable=false)
  protected DumpConfigurazione dump;

  @XmlElement(name="tracciamento",required=false,nillable=false)
  protected PortaTracciamento tracciamento;

  @XmlElement(name="gestione-cors",required=false,nillable=false)
  protected CorsConfigurazione gestioneCors;

  @XmlElement(name="response-caching",required=false,nillable=false)
  protected ResponseCachingConfigurazione responseCaching;

  @XmlElement(name="trasformazioni",required=false,nillable=false)
  protected Trasformazioni trasformazioni;

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
  @XmlAttribute(name="autenticazione",required=false)
  protected java.lang.String autenticazione = "ssl";

  @javax.xml.bind.annotation.XmlTransient
  protected java.lang.String _value_autenticazioneOpzionale;

  @XmlAttribute(name="autenticazione-opzionale",required=false)
  protected StatoFunzionalita autenticazioneOpzionale = (StatoFunzionalita) StatoFunzionalita.toEnumConstantFromString("disabilitato");

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlAttribute(name="autorizzazione",required=false)
  protected java.lang.String autorizzazione = "authenticated";

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlAttribute(name="autorizzazione-contenuto",required=false)
  protected java.lang.String autorizzazioneContenuto;

  @javax.xml.bind.annotation.XmlTransient
  protected java.lang.String _value_ricevutaAsincronaSimmetrica;

  @XmlAttribute(name="ricevuta-asincrona-simmetrica",required=false)
  protected StatoFunzionalita ricevutaAsincronaSimmetrica = (StatoFunzionalita) StatoFunzionalita.toEnumConstantFromString("abilitato");

  @javax.xml.bind.annotation.XmlTransient
  protected java.lang.String _value_ricevutaAsincronaAsimmetrica;

  @XmlAttribute(name="ricevuta-asincrona-asimmetrica",required=false)
  protected StatoFunzionalita ricevutaAsincronaAsimmetrica = (StatoFunzionalita) StatoFunzionalita.toEnumConstantFromString("abilitato");

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlAttribute(name="integrazione",required=false)
  protected java.lang.String integrazione;

  @javax.xml.bind.annotation.XmlTransient
  protected java.lang.String _value_allegaBody;

  @XmlAttribute(name="allega-body",required=false)
  protected StatoFunzionalita allegaBody = (StatoFunzionalita) StatoFunzionalita.toEnumConstantFromString("disabilitato");

  @javax.xml.bind.annotation.XmlTransient
  protected java.lang.String _value_scartaBody;

  @XmlAttribute(name="scarta-body",required=false)
  protected StatoFunzionalita scartaBody = (StatoFunzionalita) StatoFunzionalita.toEnumConstantFromString("disabilitato");

  @javax.xml.bind.annotation.XmlTransient
  protected java.lang.String _value_gestioneManifest;

  @XmlAttribute(name="gestione-manifest",required=false)
  protected StatoFunzionalita gestioneManifest;

  @javax.xml.bind.annotation.XmlTransient
  protected java.lang.String _value_stateless;

  @XmlAttribute(name="stateless",required=false)
  protected StatoFunzionalita stateless;

  @javax.xml.bind.annotation.XmlTransient
  protected java.lang.String _value_ricercaPortaAzioneDelegata;

  @XmlAttribute(name="ricerca-porta-azione-delegata",required=false)
  protected StatoFunzionalita ricercaPortaAzioneDelegata = (StatoFunzionalita) StatoFunzionalita.toEnumConstantFromString("disabilitato");

  @javax.xml.bind.annotation.XmlTransient
  protected java.lang.String _value_stato;

  @XmlAttribute(name="stato",required=false)
  protected StatoFunzionalita stato = (StatoFunzionalita) StatoFunzionalita.toEnumConstantFromString("abilitato");

  @javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter(org.openspcoop2.utils.jaxb.DateTime2String.class)
  @javax.xml.bind.annotation.XmlSchemaType(name="dateTime")
  @XmlAttribute(name="ora-registrazione",required=false)
  protected java.util.Date oraRegistrazione;

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlAttribute(name="options",required=false)
  protected java.lang.String options;

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlAttribute(name="canale",required=false)
  protected java.lang.String canale;

}
