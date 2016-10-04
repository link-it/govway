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
 * &lt;complexType name="configurazione">
 * 		&lt;sequence>
 * 			&lt;element name="routing-table" type="{http://www.openspcoop2.org/core/config}routing-table" minOccurs="0" maxOccurs="1"/>
 * 			&lt;element name="accesso-registro" type="{http://www.openspcoop2.org/core/config}accesso-registro" minOccurs="1" maxOccurs="1"/>
 * 			&lt;element name="accesso-configurazione" type="{http://www.openspcoop2.org/core/config}accesso-configurazione" minOccurs="0" maxOccurs="1"/>
 * 			&lt;element name="accesso-dati-autorizzazione" type="{http://www.openspcoop2.org/core/config}accesso-dati-autorizzazione" minOccurs="0" maxOccurs="1"/>
 * 			&lt;element name="validazione-buste" type="{http://www.openspcoop2.org/core/config}validazione-buste" minOccurs="0" maxOccurs="1"/>
 * 			&lt;element name="validazione-contenuti-applicativi" type="{http://www.openspcoop2.org/core/config}validazione-contenuti-applicativi" minOccurs="0" maxOccurs="1"/>
 * 			&lt;element name="indirizzo-risposta" type="{http://www.openspcoop2.org/core/config}indirizzo-risposta" minOccurs="0" maxOccurs="1"/>
 * 			&lt;element name="attachments" type="{http://www.openspcoop2.org/core/config}attachments" minOccurs="0" maxOccurs="1"/>
 * 			&lt;element name="risposte" type="{http://www.openspcoop2.org/core/config}risposte" minOccurs="0" maxOccurs="1"/>
 * 			&lt;element name="inoltro-buste-non-riscontrate" type="{http://www.openspcoop2.org/core/config}inoltro-buste-non-riscontrate" minOccurs="1" maxOccurs="1"/>
 * 			&lt;element name="messaggi-diagnostici" type="{http://www.openspcoop2.org/core/config}messaggi-diagnostici" minOccurs="1" maxOccurs="1"/>
 * 			&lt;element name="tracciamento" type="{http://www.openspcoop2.org/core/config}tracciamento" minOccurs="0" maxOccurs="1"/>
 * 			&lt;element name="gestione-errore" type="{http://www.openspcoop2.org/core/config}configurazione-gestione-errore" minOccurs="0" maxOccurs="1"/>
 * 			&lt;element name="integration-manager" type="{http://www.openspcoop2.org/core/config}integration-manager" minOccurs="0" maxOccurs="1"/>
 * 			&lt;element name="stato-servizi-pdd" type="{http://www.openspcoop2.org/core/config}stato-servizi-pdd" minOccurs="0" maxOccurs="1"/>
 * 			&lt;element name="system-properties" type="{http://www.openspcoop2.org/core/config}system-properties" minOccurs="0" maxOccurs="1"/>
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
@XmlType(name = "configurazione", 
  propOrder = {
  	"routingTable",
  	"accessoRegistro",
  	"accessoConfigurazione",
  	"accessoDatiAutorizzazione",
  	"validazioneBuste",
  	"validazioneContenutiApplicativi",
  	"indirizzoRisposta",
  	"attachments",
  	"risposte",
  	"inoltroBusteNonRiscontrate",
  	"messaggiDiagnostici",
  	"tracciamento",
  	"gestioneErrore",
  	"integrationManager",
  	"statoServiziPdd",
  	"systemProperties"
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
		return new Long(-1);
  }

  public void setId(Long id) {
    if(id!=null)
		this.id=id;
	else
		this.id=new Long(-1);
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

  @XmlElement(name="routing-table",required=false,nillable=false)
  protected RoutingTable routingTable;

  @XmlElement(name="accesso-registro",required=true,nillable=false)
  protected AccessoRegistro accessoRegistro;

  @XmlElement(name="accesso-configurazione",required=false,nillable=false)
  protected AccessoConfigurazione accessoConfigurazione;

  @XmlElement(name="accesso-dati-autorizzazione",required=false,nillable=false)
  protected AccessoDatiAutorizzazione accessoDatiAutorizzazione;

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

  @XmlElement(name="gestione-errore",required=false,nillable=false)
  protected ConfigurazioneGestioneErrore gestioneErrore;

  @XmlElement(name="integration-manager",required=false,nillable=false)
  protected IntegrationManager integrationManager;

  @XmlElement(name="stato-servizi-pdd",required=false,nillable=false)
  protected StatoServiziPdd statoServiziPdd;

  @XmlElement(name="system-properties",required=false,nillable=false)
  protected SystemProperties systemProperties;

}
