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
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


/** <p>Java class for configurazione complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="configurazione"&gt;
 * 		&lt;sequence&gt;
 * 			&lt;element name="routing-table" type="{http://www.openspcoop2.org/core/config}routing-table" minOccurs="0" maxOccurs="1"/&gt;
 * 			&lt;element name="accesso-registro" type="{http://www.openspcoop2.org/core/config}accesso-registro" minOccurs="1" maxOccurs="1"/&gt;
 * 			&lt;element name="accesso-configurazione" type="{http://www.openspcoop2.org/core/config}accesso-configurazione" minOccurs="0" maxOccurs="1"/&gt;
 * 			&lt;element name="accesso-dati-autorizzazione" type="{http://www.openspcoop2.org/core/config}accesso-dati-autorizzazione" minOccurs="0" maxOccurs="1"/&gt;
 * 			&lt;element name="accesso-dati-autenticazione" type="{http://www.openspcoop2.org/core/config}accesso-dati-autenticazione" minOccurs="0" maxOccurs="1"/&gt;
 * 			&lt;element name="accesso-dati-gestione-token" type="{http://www.openspcoop2.org/core/config}accesso-dati-gestione-token" minOccurs="0" maxOccurs="1"/&gt;
 * 			&lt;element name="accesso-dati-keystore" type="{http://www.openspcoop2.org/core/config}accesso-dati-keystore" minOccurs="0" maxOccurs="1"/&gt;
 * 			&lt;element name="accesso-dati-consegna-applicativi" type="{http://www.openspcoop2.org/core/config}accesso-dati-consegna-applicativi" minOccurs="0" maxOccurs="1"/&gt;
 * 			&lt;element name="multitenant" type="{http://www.openspcoop2.org/core/config}configurazione-multitenant" minOccurs="0" maxOccurs="1"/&gt;
 * 			&lt;element name="url-invocazione" type="{http://www.openspcoop2.org/core/config}configurazione-url-invocazione" minOccurs="0" maxOccurs="1"/&gt;
 * 			&lt;element name="validazione-buste" type="{http://www.openspcoop2.org/core/config}validazione-buste" minOccurs="0" maxOccurs="1"/&gt;
 * 			&lt;element name="validazione-contenuti-applicativi" type="{http://www.openspcoop2.org/core/config}validazione-contenuti-applicativi" minOccurs="0" maxOccurs="1"/&gt;
 * 			&lt;element name="indirizzo-risposta" type="{http://www.openspcoop2.org/core/config}indirizzo-risposta" minOccurs="0" maxOccurs="1"/&gt;
 * 			&lt;element name="attachments" type="{http://www.openspcoop2.org/core/config}attachments" minOccurs="0" maxOccurs="1"/&gt;
 * 			&lt;element name="risposte" type="{http://www.openspcoop2.org/core/config}risposte" minOccurs="0" maxOccurs="1"/&gt;
 * 			&lt;element name="inoltro-buste-non-riscontrate" type="{http://www.openspcoop2.org/core/config}inoltro-buste-non-riscontrate" minOccurs="1" maxOccurs="1"/&gt;
 * 			&lt;element name="messaggi-diagnostici" type="{http://www.openspcoop2.org/core/config}messaggi-diagnostici" minOccurs="1" maxOccurs="1"/&gt;
 * 			&lt;element name="tracciamento" type="{http://www.openspcoop2.org/core/config}tracciamento" minOccurs="0" maxOccurs="1"/&gt;
 * 			&lt;element name="dump" type="{http://www.openspcoop2.org/core/config}dump" minOccurs="0" maxOccurs="1"/&gt;
 * 			&lt;element name="transazioni" type="{http://www.openspcoop2.org/core/config}transazioni" minOccurs="0" maxOccurs="1"/&gt;
 * 			&lt;element name="gestione-errore" type="{http://www.openspcoop2.org/core/config}configurazione-gestione-errore" minOccurs="0" maxOccurs="1"/&gt;
 * 			&lt;element name="integration-manager" type="{http://www.openspcoop2.org/core/config}integration-manager" minOccurs="0" maxOccurs="1"/&gt;
 * 			&lt;element name="stato-servizi-pdd" type="{http://www.openspcoop2.org/core/config}stato-servizi-pdd" minOccurs="0" maxOccurs="1"/&gt;
 * 			&lt;element name="system-properties" type="{http://www.openspcoop2.org/core/config}system-properties" minOccurs="0" maxOccurs="1"/&gt;
 * 			&lt;element name="generic-properties" type="{http://www.openspcoop2.org/core/config}generic-properties" minOccurs="0" maxOccurs="unbounded"/&gt;
 * 			&lt;element name="gestione-cors" type="{http://www.openspcoop2.org/core/config}cors-configurazione" minOccurs="0" maxOccurs="1"/&gt;
 * 			&lt;element name="response-caching" type="{http://www.openspcoop2.org/core/config}response-caching-configurazione-generale" minOccurs="0" maxOccurs="1"/&gt;
 * 			&lt;element name="gestione-canali" type="{http://www.openspcoop2.org/core/config}canali-configurazione" minOccurs="0" maxOccurs="1"/&gt;
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
@XmlType(name = "configurazione", 
  propOrder = {
  	"routingTable",
  	"accessoRegistro",
  	"accessoConfigurazione",
  	"accessoDatiAutorizzazione",
  	"accessoDatiAutenticazione",
  	"accessoDatiGestioneToken",
  	"accessoDatiKeystore",
  	"accessoDatiConsegnaApplicativi",
  	"multitenant",
  	"urlInvocazione",
  	"validazioneBuste",
  	"validazioneContenutiApplicativi",
  	"indirizzoRisposta",
  	"attachments",
  	"risposte",
  	"inoltroBusteNonRiscontrate",
  	"messaggiDiagnostici",
  	"tracciamento",
  	"dump",
  	"transazioni",
  	"gestioneErrore",
  	"integrationManager",
  	"statoServiziPdd",
  	"systemProperties",
  	"genericProperties",
  	"gestioneCors",
  	"responseCaching",
  	"gestioneCanali"
  }
)

@XmlRootElement(name = "configurazione")

public class Configurazione extends org.openspcoop2.utils.beans.BaseBean implements Serializable , Cloneable {
  public Configurazione() {
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

  public RoutingTable getRoutingTable() {
    return this.routingTable;
  }

  public void setRoutingTable(RoutingTable routingTable) {
    this.routingTable = routingTable;
  }

  public AccessoRegistro getAccessoRegistro() {
    return this.accessoRegistro;
  }

  public void setAccessoRegistro(AccessoRegistro accessoRegistro) {
    this.accessoRegistro = accessoRegistro;
  }

  public AccessoConfigurazione getAccessoConfigurazione() {
    return this.accessoConfigurazione;
  }

  public void setAccessoConfigurazione(AccessoConfigurazione accessoConfigurazione) {
    this.accessoConfigurazione = accessoConfigurazione;
  }

  public AccessoDatiAutorizzazione getAccessoDatiAutorizzazione() {
    return this.accessoDatiAutorizzazione;
  }

  public void setAccessoDatiAutorizzazione(AccessoDatiAutorizzazione accessoDatiAutorizzazione) {
    this.accessoDatiAutorizzazione = accessoDatiAutorizzazione;
  }

  public AccessoDatiAutenticazione getAccessoDatiAutenticazione() {
    return this.accessoDatiAutenticazione;
  }

  public void setAccessoDatiAutenticazione(AccessoDatiAutenticazione accessoDatiAutenticazione) {
    this.accessoDatiAutenticazione = accessoDatiAutenticazione;
  }

  public AccessoDatiGestioneToken getAccessoDatiGestioneToken() {
    return this.accessoDatiGestioneToken;
  }

  public void setAccessoDatiGestioneToken(AccessoDatiGestioneToken accessoDatiGestioneToken) {
    this.accessoDatiGestioneToken = accessoDatiGestioneToken;
  }

  public AccessoDatiKeystore getAccessoDatiKeystore() {
    return this.accessoDatiKeystore;
  }

  public void setAccessoDatiKeystore(AccessoDatiKeystore accessoDatiKeystore) {
    this.accessoDatiKeystore = accessoDatiKeystore;
  }

  public AccessoDatiConsegnaApplicativi getAccessoDatiConsegnaApplicativi() {
    return this.accessoDatiConsegnaApplicativi;
  }

  public void setAccessoDatiConsegnaApplicativi(AccessoDatiConsegnaApplicativi accessoDatiConsegnaApplicativi) {
    this.accessoDatiConsegnaApplicativi = accessoDatiConsegnaApplicativi;
  }

  public ConfigurazioneMultitenant getMultitenant() {
    return this.multitenant;
  }

  public void setMultitenant(ConfigurazioneMultitenant multitenant) {
    this.multitenant = multitenant;
  }

  public ConfigurazioneUrlInvocazione getUrlInvocazione() {
    return this.urlInvocazione;
  }

  public void setUrlInvocazione(ConfigurazioneUrlInvocazione urlInvocazione) {
    this.urlInvocazione = urlInvocazione;
  }

  public ValidazioneBuste getValidazioneBuste() {
    return this.validazioneBuste;
  }

  public void setValidazioneBuste(ValidazioneBuste validazioneBuste) {
    this.validazioneBuste = validazioneBuste;
  }

  public ValidazioneContenutiApplicativi getValidazioneContenutiApplicativi() {
    return this.validazioneContenutiApplicativi;
  }

  public void setValidazioneContenutiApplicativi(ValidazioneContenutiApplicativi validazioneContenutiApplicativi) {
    this.validazioneContenutiApplicativi = validazioneContenutiApplicativi;
  }

  public IndirizzoRisposta getIndirizzoRisposta() {
    return this.indirizzoRisposta;
  }

  public void setIndirizzoRisposta(IndirizzoRisposta indirizzoRisposta) {
    this.indirizzoRisposta = indirizzoRisposta;
  }

  public Attachments getAttachments() {
    return this.attachments;
  }

  public void setAttachments(Attachments attachments) {
    this.attachments = attachments;
  }

  public Risposte getRisposte() {
    return this.risposte;
  }

  public void setRisposte(Risposte risposte) {
    this.risposte = risposte;
  }

  public InoltroBusteNonRiscontrate getInoltroBusteNonRiscontrate() {
    return this.inoltroBusteNonRiscontrate;
  }

  public void setInoltroBusteNonRiscontrate(InoltroBusteNonRiscontrate inoltroBusteNonRiscontrate) {
    this.inoltroBusteNonRiscontrate = inoltroBusteNonRiscontrate;
  }

  public MessaggiDiagnostici getMessaggiDiagnostici() {
    return this.messaggiDiagnostici;
  }

  public void setMessaggiDiagnostici(MessaggiDiagnostici messaggiDiagnostici) {
    this.messaggiDiagnostici = messaggiDiagnostici;
  }

  public Tracciamento getTracciamento() {
    return this.tracciamento;
  }

  public void setTracciamento(Tracciamento tracciamento) {
    this.tracciamento = tracciamento;
  }

  public Dump getDump() {
    return this.dump;
  }

  public void setDump(Dump dump) {
    this.dump = dump;
  }

  public Transazioni getTransazioni() {
    return this.transazioni;
  }

  public void setTransazioni(Transazioni transazioni) {
    this.transazioni = transazioni;
  }

  public ConfigurazioneGestioneErrore getGestioneErrore() {
    return this.gestioneErrore;
  }

  public void setGestioneErrore(ConfigurazioneGestioneErrore gestioneErrore) {
    this.gestioneErrore = gestioneErrore;
  }

  public IntegrationManager getIntegrationManager() {
    return this.integrationManager;
  }

  public void setIntegrationManager(IntegrationManager integrationManager) {
    this.integrationManager = integrationManager;
  }

  public StatoServiziPdd getStatoServiziPdd() {
    return this.statoServiziPdd;
  }

  public void setStatoServiziPdd(StatoServiziPdd statoServiziPdd) {
    this.statoServiziPdd = statoServiziPdd;
  }

  public SystemProperties getSystemProperties() {
    return this.systemProperties;
  }

  public void setSystemProperties(SystemProperties systemProperties) {
    this.systemProperties = systemProperties;
  }

  public void addGenericProperties(GenericProperties genericProperties) {
    this.genericProperties.add(genericProperties);
  }

  public GenericProperties getGenericProperties(int index) {
    return this.genericProperties.get( index );
  }

  public GenericProperties removeGenericProperties(int index) {
    return this.genericProperties.remove( index );
  }

  public List<GenericProperties> getGenericPropertiesList() {
    return this.genericProperties;
  }

  public void setGenericPropertiesList(List<GenericProperties> genericProperties) {
    this.genericProperties=genericProperties;
  }

  public int sizeGenericPropertiesList() {
    return this.genericProperties.size();
  }

  public CorsConfigurazione getGestioneCors() {
    return this.gestioneCors;
  }

  public void setGestioneCors(CorsConfigurazione gestioneCors) {
    this.gestioneCors = gestioneCors;
  }

  public ResponseCachingConfigurazioneGenerale getResponseCaching() {
    return this.responseCaching;
  }

  public void setResponseCaching(ResponseCachingConfigurazioneGenerale responseCaching) {
    this.responseCaching = responseCaching;
  }

  public CanaliConfigurazione getGestioneCanali() {
    return this.gestioneCanali;
  }

  public void setGestioneCanali(CanaliConfigurazione gestioneCanali) {
    this.gestioneCanali = gestioneCanali;
  }

  private static final long serialVersionUID = 1L;

  @XmlTransient
  private Long id;

  private static org.openspcoop2.core.config.model.ConfigurazioneModel modelStaticInstance = null;
  private static synchronized void initModelStaticInstance(){
	  if(org.openspcoop2.core.config.Configurazione.modelStaticInstance==null){
  			org.openspcoop2.core.config.Configurazione.modelStaticInstance = new org.openspcoop2.core.config.model.ConfigurazioneModel();
	  }
  }
  public static org.openspcoop2.core.config.model.ConfigurazioneModel model(){
	  if(org.openspcoop2.core.config.Configurazione.modelStaticInstance==null){
	  		initModelStaticInstance();
	  }
	  return org.openspcoop2.core.config.Configurazione.modelStaticInstance;
  }


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

  @XmlElement(name="routing-table",required=false,nillable=false)
  protected RoutingTable routingTable;

  @XmlElement(name="accesso-registro",required=true,nillable=false)
  protected AccessoRegistro accessoRegistro;

  @XmlElement(name="accesso-configurazione",required=false,nillable=false)
  protected AccessoConfigurazione accessoConfigurazione;

  @XmlElement(name="accesso-dati-autorizzazione",required=false,nillable=false)
  protected AccessoDatiAutorizzazione accessoDatiAutorizzazione;

  @XmlElement(name="accesso-dati-autenticazione",required=false,nillable=false)
  protected AccessoDatiAutenticazione accessoDatiAutenticazione;

  @XmlElement(name="accesso-dati-gestione-token",required=false,nillable=false)
  protected AccessoDatiGestioneToken accessoDatiGestioneToken;

  @XmlElement(name="accesso-dati-keystore",required=false,nillable=false)
  protected AccessoDatiKeystore accessoDatiKeystore;

  @XmlElement(name="accesso-dati-consegna-applicativi",required=false,nillable=false)
  protected AccessoDatiConsegnaApplicativi accessoDatiConsegnaApplicativi;

  @XmlElement(name="multitenant",required=false,nillable=false)
  protected ConfigurazioneMultitenant multitenant;

  @XmlElement(name="url-invocazione",required=false,nillable=false)
  protected ConfigurazioneUrlInvocazione urlInvocazione;

  @XmlElement(name="validazione-buste",required=false,nillable=false)
  protected ValidazioneBuste validazioneBuste;

  @XmlElement(name="validazione-contenuti-applicativi",required=false,nillable=false)
  protected ValidazioneContenutiApplicativi validazioneContenutiApplicativi;

  @XmlElement(name="indirizzo-risposta",required=false,nillable=false)
  protected IndirizzoRisposta indirizzoRisposta;

  @XmlElement(name="attachments",required=false,nillable=false)
  protected Attachments attachments;

  @XmlElement(name="risposte",required=false,nillable=false)
  protected Risposte risposte;

  @XmlElement(name="inoltro-buste-non-riscontrate",required=true,nillable=false)
  protected InoltroBusteNonRiscontrate inoltroBusteNonRiscontrate;

  @XmlElement(name="messaggi-diagnostici",required=true,nillable=false)
  protected MessaggiDiagnostici messaggiDiagnostici;

  @XmlElement(name="tracciamento",required=false,nillable=false)
  protected Tracciamento tracciamento;

  @XmlElement(name="dump",required=false,nillable=false)
  protected Dump dump;

  @XmlElement(name="transazioni",required=false,nillable=false)
  protected Transazioni transazioni;

  @XmlElement(name="gestione-errore",required=false,nillable=false)
  protected ConfigurazioneGestioneErrore gestioneErrore;

  @XmlElement(name="integration-manager",required=false,nillable=false)
  protected IntegrationManager integrationManager;

  @XmlElement(name="stato-servizi-pdd",required=false,nillable=false)
  protected StatoServiziPdd statoServiziPdd;

  @XmlElement(name="system-properties",required=false,nillable=false)
  protected SystemProperties systemProperties;

  @XmlElement(name="generic-properties",required=true,nillable=false)
  protected List<GenericProperties> genericProperties = new ArrayList<GenericProperties>();

  /**
   * @deprecated Use method getGenericPropertiesList
   * @return List&lt;GenericProperties&gt;
  */
  @Deprecated
  public List<GenericProperties> getGenericProperties() {
  	return this.genericProperties;
  }

  /**
   * @deprecated Use method setGenericPropertiesList
   * @param genericProperties List&lt;GenericProperties&gt;
  */
  @Deprecated
  public void setGenericProperties(List<GenericProperties> genericProperties) {
  	this.genericProperties=genericProperties;
  }

  /**
   * @deprecated Use method sizeGenericPropertiesList
   * @return lunghezza della lista
  */
  @Deprecated
  public int sizeGenericProperties() {
  	return this.genericProperties.size();
  }

  @XmlElement(name="gestione-cors",required=false,nillable=false)
  protected CorsConfigurazione gestioneCors;

  @XmlElement(name="response-caching",required=false,nillable=false)
  protected ResponseCachingConfigurazioneGenerale responseCaching;

  @XmlElement(name="gestione-canali",required=false,nillable=false)
  protected CanaliConfigurazione gestioneCanali;

}
