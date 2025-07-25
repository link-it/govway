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
package org.openspcoop2.core.controllo_traffico;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;
import org.openspcoop2.core.controllo_traffico.constants.TipoApplicabilita;
import org.openspcoop2.core.controllo_traffico.constants.TipoBanda;
import org.openspcoop2.core.controllo_traffico.constants.TipoControlloPeriodo;
import org.openspcoop2.core.controllo_traffico.constants.TipoFinestra;
import org.openspcoop2.core.controllo_traffico.constants.TipoLatenza;
import org.openspcoop2.core.controllo_traffico.constants.TipoPeriodoRealtime;
import org.openspcoop2.core.controllo_traffico.constants.TipoPeriodoStatistico;
import java.io.Serializable;


/** <p>Java class for configurazione-policy complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="configurazione-policy"&gt;
 * 		&lt;sequence&gt;
 * 			&lt;element name="id-policy" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="1" maxOccurs="1"/&gt;
 * 			&lt;element name="built-in" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="1" maxOccurs="1" default="false"/&gt;
 * 			&lt;element name="descrizione" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="1" maxOccurs="1"/&gt;
 * 			&lt;element name="risorsa" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="1" maxOccurs="1"/&gt;
 * 			&lt;element name="simultanee" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="1" maxOccurs="1" default="false"/&gt;
 * 			&lt;element name="valore" type="{http://www.w3.org/2001/XMLSchema}unsignedLong" minOccurs="0" maxOccurs="1"/&gt;
 * 			&lt;element name="valore2" type="{http://www.w3.org/2001/XMLSchema}unsignedLong" minOccurs="0" maxOccurs="1"/&gt;
 * 			&lt;element name="valore-tipo-banda" type="{http://www.openspcoop2.org/core/controllo_traffico}tipo-banda" minOccurs="0" maxOccurs="1"/&gt;
 * 			&lt;element name="valore-tipo-latenza" type="{http://www.openspcoop2.org/core/controllo_traffico}tipo-latenza" minOccurs="0" maxOccurs="1"/&gt;
 * 			&lt;element name="modalita-controllo" type="{http://www.openspcoop2.org/core/controllo_traffico}tipo-controllo-periodo" minOccurs="0" maxOccurs="1"/&gt;
 * 			&lt;element name="tipo-intervallo-osservazione-realtime" type="{http://www.openspcoop2.org/core/controllo_traffico}tipo-periodo-realtime" minOccurs="0" maxOccurs="1"/&gt;
 * 			&lt;element name="tipo-intervallo-osservazione-statistico" type="{http://www.openspcoop2.org/core/controllo_traffico}tipo-periodo-statistico" minOccurs="0" maxOccurs="1"/&gt;
 * 			&lt;element name="intervallo-osservazione" type="{http://www.w3.org/2001/XMLSchema}unsignedInt" minOccurs="0" maxOccurs="1"/&gt;
 * 			&lt;element name="finestra-osservazione" type="{http://www.openspcoop2.org/core/controllo_traffico}tipo-finestra" minOccurs="0" maxOccurs="1"/&gt;
 * 			&lt;element name="tipo-applicabilita" type="{http://www.openspcoop2.org/core/controllo_traffico}tipo-applicabilita" minOccurs="1" maxOccurs="1" default="sempre"/&gt;
 * 			&lt;element name="applicabilita-con-congestione" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="1" maxOccurs="1" default="false"/&gt;
 * 			&lt;element name="applicabilita-degrado-prestazionale" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="1" maxOccurs="1" default="false"/&gt;
 * 			&lt;element name="degrado-avg-time-modalita-controllo" type="{http://www.openspcoop2.org/core/controllo_traffico}tipo-controllo-periodo" minOccurs="0" maxOccurs="1"/&gt;
 * 			&lt;element name="degrado-avg-time-tipo-intervallo-osservazione-realtime" type="{http://www.openspcoop2.org/core/controllo_traffico}tipo-periodo-realtime" minOccurs="0" maxOccurs="1"/&gt;
 * 			&lt;element name="degrado-avg-time-tipo-intervallo-osservazione-statistico" type="{http://www.openspcoop2.org/core/controllo_traffico}tipo-periodo-statistico" minOccurs="0" maxOccurs="1"/&gt;
 * 			&lt;element name="degrado-avg-time-intervallo-osservazione" type="{http://www.w3.org/2001/XMLSchema}unsignedInt" minOccurs="0" maxOccurs="1"/&gt;
 * 			&lt;element name="degrado-avg-time-finestra-osservazione" type="{http://www.openspcoop2.org/core/controllo_traffico}tipo-finestra" minOccurs="0" maxOccurs="1"/&gt;
 * 			&lt;element name="degrado-avg-time-tipo-latenza" type="{http://www.openspcoop2.org/core/controllo_traffico}tipo-latenza" minOccurs="0" maxOccurs="1"/&gt;
 * 			&lt;element name="applicabilita-stato-allarme" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="1" maxOccurs="1" default="false"/&gt;
 * 			&lt;element name="allarme-nome" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0" maxOccurs="1"/&gt;
 * 			&lt;element name="allarme-stato" type="{http://www.w3.org/2001/XMLSchema}unsignedInt" minOccurs="0" maxOccurs="1"/&gt;
 * 			&lt;element name="allarme-not-stato" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="1" maxOccurs="1" default="false"/&gt;
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
@XmlType(name = "configurazione-policy", 
  propOrder = {
  	"idPolicy",
  	"builtIn",
  	"descrizione",
  	"risorsa",
  	"simultanee",
  	"valore",
  	"valore2",
  	"valoreTipoBanda",
  	"valoreTipoLatenza",
  	"modalitaControllo",
  	"tipoIntervalloOsservazioneRealtime",
  	"tipoIntervalloOsservazioneStatistico",
  	"intervalloOsservazione",
  	"finestraOsservazione",
  	"tipoApplicabilita",
  	"applicabilitaConCongestione",
  	"applicabilitaDegradoPrestazionale",
  	"degradoAvgTimeModalitaControllo",
  	"degradoAvgTimeTipoIntervalloOsservazioneRealtime",
  	"degradoAvgTimeTipoIntervalloOsservazioneStatistico",
  	"degradoAvgTimeIntervalloOsservazione",
  	"degradoAvgTimeFinestraOsservazione",
  	"degradoAvgTimeTipoLatenza",
  	"applicabilitaStatoAllarme",
  	"allarmeNome",
  	"allarmeStato",
  	"allarmeNotStato"
  }
)

@XmlRootElement(name = "configurazione-policy")

public class ConfigurazionePolicy extends org.openspcoop2.utils.beans.BaseBeanWithId implements Serializable , Cloneable {
  public ConfigurazionePolicy() {
    super();
  }

  public IdPolicy getOldIdPolicy() {
    return this.oldIdPolicy;
  }

  public void setOldIdPolicy(IdPolicy oldIdPolicy) {
    this.oldIdPolicy=oldIdPolicy;
  }

  public java.lang.String getIdPolicy() {
    return this.idPolicy;
  }

  public void setIdPolicy(java.lang.String idPolicy) {
    this.idPolicy = idPolicy;
  }

  public boolean isBuiltIn() {
    return this.builtIn;
  }

  public boolean getBuiltIn() {
    return this.builtIn;
  }

  public void setBuiltIn(boolean builtIn) {
    this.builtIn = builtIn;
  }

  public java.lang.String getDescrizione() {
    return this.descrizione;
  }

  public void setDescrizione(java.lang.String descrizione) {
    this.descrizione = descrizione;
  }

  public java.lang.String getRisorsa() {
    return this.risorsa;
  }

  public void setRisorsa(java.lang.String risorsa) {
    this.risorsa = risorsa;
  }

  public boolean isSimultanee() {
    return this.simultanee;
  }

  public boolean getSimultanee() {
    return this.simultanee;
  }

  public void setSimultanee(boolean simultanee) {
    this.simultanee = simultanee;
  }

  public java.lang.Long getValore() {
    return this.valore;
  }

  public void setValore(java.lang.Long valore) {
    this.valore = valore;
  }

  public java.lang.Long getValore2() {
    return this.valore2;
  }

  public void setValore2(java.lang.Long valore2) {
    this.valore2 = valore2;
  }

  public void setValoreTipoBandaRawEnumValue(String value) {
    this.valoreTipoBanda = (TipoBanda) TipoBanda.toEnumConstantFromString(value);
  }

  public String getValoreTipoBandaRawEnumValue() {
    if(this.valoreTipoBanda == null){
    	return null;
    }else{
    	return this.valoreTipoBanda.toString();
    }
  }

  public org.openspcoop2.core.controllo_traffico.constants.TipoBanda getValoreTipoBanda() {
    return this.valoreTipoBanda;
  }

  public void setValoreTipoBanda(org.openspcoop2.core.controllo_traffico.constants.TipoBanda valoreTipoBanda) {
    this.valoreTipoBanda = valoreTipoBanda;
  }

  public void setValoreTipoLatenzaRawEnumValue(String value) {
    this.valoreTipoLatenza = (TipoLatenza) TipoLatenza.toEnumConstantFromString(value);
  }

  public String getValoreTipoLatenzaRawEnumValue() {
    if(this.valoreTipoLatenza == null){
    	return null;
    }else{
    	return this.valoreTipoLatenza.toString();
    }
  }

  public org.openspcoop2.core.controllo_traffico.constants.TipoLatenza getValoreTipoLatenza() {
    return this.valoreTipoLatenza;
  }

  public void setValoreTipoLatenza(org.openspcoop2.core.controllo_traffico.constants.TipoLatenza valoreTipoLatenza) {
    this.valoreTipoLatenza = valoreTipoLatenza;
  }

  public void setModalitaControlloRawEnumValue(String value) {
    this.modalitaControllo = (TipoControlloPeriodo) TipoControlloPeriodo.toEnumConstantFromString(value);
  }

  public String getModalitaControlloRawEnumValue() {
    if(this.modalitaControllo == null){
    	return null;
    }else{
    	return this.modalitaControllo.toString();
    }
  }

  public org.openspcoop2.core.controllo_traffico.constants.TipoControlloPeriodo getModalitaControllo() {
    return this.modalitaControllo;
  }

  public void setModalitaControllo(org.openspcoop2.core.controllo_traffico.constants.TipoControlloPeriodo modalitaControllo) {
    this.modalitaControllo = modalitaControllo;
  }

  public void setTipoIntervalloOsservazioneRealtimeRawEnumValue(String value) {
    this.tipoIntervalloOsservazioneRealtime = (TipoPeriodoRealtime) TipoPeriodoRealtime.toEnumConstantFromString(value);
  }

  public String getTipoIntervalloOsservazioneRealtimeRawEnumValue() {
    if(this.tipoIntervalloOsservazioneRealtime == null){
    	return null;
    }else{
    	return this.tipoIntervalloOsservazioneRealtime.toString();
    }
  }

  public org.openspcoop2.core.controllo_traffico.constants.TipoPeriodoRealtime getTipoIntervalloOsservazioneRealtime() {
    return this.tipoIntervalloOsservazioneRealtime;
  }

  public void setTipoIntervalloOsservazioneRealtime(org.openspcoop2.core.controllo_traffico.constants.TipoPeriodoRealtime tipoIntervalloOsservazioneRealtime) {
    this.tipoIntervalloOsservazioneRealtime = tipoIntervalloOsservazioneRealtime;
  }

  public void setTipoIntervalloOsservazioneStatisticoRawEnumValue(String value) {
    this.tipoIntervalloOsservazioneStatistico = (TipoPeriodoStatistico) TipoPeriodoStatistico.toEnumConstantFromString(value);
  }

  public String getTipoIntervalloOsservazioneStatisticoRawEnumValue() {
    if(this.tipoIntervalloOsservazioneStatistico == null){
    	return null;
    }else{
    	return this.tipoIntervalloOsservazioneStatistico.toString();
    }
  }

  public org.openspcoop2.core.controllo_traffico.constants.TipoPeriodoStatistico getTipoIntervalloOsservazioneStatistico() {
    return this.tipoIntervalloOsservazioneStatistico;
  }

  public void setTipoIntervalloOsservazioneStatistico(org.openspcoop2.core.controllo_traffico.constants.TipoPeriodoStatistico tipoIntervalloOsservazioneStatistico) {
    this.tipoIntervalloOsservazioneStatistico = tipoIntervalloOsservazioneStatistico;
  }

  public java.lang.Integer getIntervalloOsservazione() {
    return this.intervalloOsservazione;
  }

  public void setIntervalloOsservazione(java.lang.Integer intervalloOsservazione) {
    this.intervalloOsservazione = intervalloOsservazione;
  }

  public void setFinestraOsservazioneRawEnumValue(String value) {
    this.finestraOsservazione = (TipoFinestra) TipoFinestra.toEnumConstantFromString(value);
  }

  public String getFinestraOsservazioneRawEnumValue() {
    if(this.finestraOsservazione == null){
    	return null;
    }else{
    	return this.finestraOsservazione.toString();
    }
  }

  public org.openspcoop2.core.controllo_traffico.constants.TipoFinestra getFinestraOsservazione() {
    return this.finestraOsservazione;
  }

  public void setFinestraOsservazione(org.openspcoop2.core.controllo_traffico.constants.TipoFinestra finestraOsservazione) {
    this.finestraOsservazione = finestraOsservazione;
  }

  public void setTipoApplicabilitaRawEnumValue(String value) {
    this.tipoApplicabilita = (TipoApplicabilita) TipoApplicabilita.toEnumConstantFromString(value);
  }

  public String getTipoApplicabilitaRawEnumValue() {
    if(this.tipoApplicabilita == null){
    	return null;
    }else{
    	return this.tipoApplicabilita.toString();
    }
  }

  public org.openspcoop2.core.controllo_traffico.constants.TipoApplicabilita getTipoApplicabilita() {
    return this.tipoApplicabilita;
  }

  public void setTipoApplicabilita(org.openspcoop2.core.controllo_traffico.constants.TipoApplicabilita tipoApplicabilita) {
    this.tipoApplicabilita = tipoApplicabilita;
  }

  public boolean isApplicabilitaConCongestione() {
    return this.applicabilitaConCongestione;
  }

  public boolean getApplicabilitaConCongestione() {
    return this.applicabilitaConCongestione;
  }

  public void setApplicabilitaConCongestione(boolean applicabilitaConCongestione) {
    this.applicabilitaConCongestione = applicabilitaConCongestione;
  }

  public boolean isApplicabilitaDegradoPrestazionale() {
    return this.applicabilitaDegradoPrestazionale;
  }

  public boolean getApplicabilitaDegradoPrestazionale() {
    return this.applicabilitaDegradoPrestazionale;
  }

  public void setApplicabilitaDegradoPrestazionale(boolean applicabilitaDegradoPrestazionale) {
    this.applicabilitaDegradoPrestazionale = applicabilitaDegradoPrestazionale;
  }

  public void setDegradoAvgTimeModalitaControlloRawEnumValue(String value) {
    this.degradoAvgTimeModalitaControllo = (TipoControlloPeriodo) TipoControlloPeriodo.toEnumConstantFromString(value);
  }

  public String getDegradoAvgTimeModalitaControlloRawEnumValue() {
    if(this.degradoAvgTimeModalitaControllo == null){
    	return null;
    }else{
    	return this.degradoAvgTimeModalitaControllo.toString();
    }
  }

  public org.openspcoop2.core.controllo_traffico.constants.TipoControlloPeriodo getDegradoAvgTimeModalitaControllo() {
    return this.degradoAvgTimeModalitaControllo;
  }

  public void setDegradoAvgTimeModalitaControllo(org.openspcoop2.core.controllo_traffico.constants.TipoControlloPeriodo degradoAvgTimeModalitaControllo) {
    this.degradoAvgTimeModalitaControllo = degradoAvgTimeModalitaControllo;
  }

  public void setDegradoAvgTimeTipoIntervalloOsservazioneRealtimeRawEnumValue(String value) {
    this.degradoAvgTimeTipoIntervalloOsservazioneRealtime = (TipoPeriodoRealtime) TipoPeriodoRealtime.toEnumConstantFromString(value);
  }

  public String getDegradoAvgTimeTipoIntervalloOsservazioneRealtimeRawEnumValue() {
    if(this.degradoAvgTimeTipoIntervalloOsservazioneRealtime == null){
    	return null;
    }else{
    	return this.degradoAvgTimeTipoIntervalloOsservazioneRealtime.toString();
    }
  }

  public org.openspcoop2.core.controllo_traffico.constants.TipoPeriodoRealtime getDegradoAvgTimeTipoIntervalloOsservazioneRealtime() {
    return this.degradoAvgTimeTipoIntervalloOsservazioneRealtime;
  }

  public void setDegradoAvgTimeTipoIntervalloOsservazioneRealtime(org.openspcoop2.core.controllo_traffico.constants.TipoPeriodoRealtime degradoAvgTimeTipoIntervalloOsservazioneRealtime) {
    this.degradoAvgTimeTipoIntervalloOsservazioneRealtime = degradoAvgTimeTipoIntervalloOsservazioneRealtime;
  }

  public void setDegradoAvgTimeTipoIntervalloOsservazioneStatisticoRawEnumValue(String value) {
    this.degradoAvgTimeTipoIntervalloOsservazioneStatistico = (TipoPeriodoStatistico) TipoPeriodoStatistico.toEnumConstantFromString(value);
  }

  public String getDegradoAvgTimeTipoIntervalloOsservazioneStatisticoRawEnumValue() {
    if(this.degradoAvgTimeTipoIntervalloOsservazioneStatistico == null){
    	return null;
    }else{
    	return this.degradoAvgTimeTipoIntervalloOsservazioneStatistico.toString();
    }
  }

  public org.openspcoop2.core.controllo_traffico.constants.TipoPeriodoStatistico getDegradoAvgTimeTipoIntervalloOsservazioneStatistico() {
    return this.degradoAvgTimeTipoIntervalloOsservazioneStatistico;
  }

  public void setDegradoAvgTimeTipoIntervalloOsservazioneStatistico(org.openspcoop2.core.controllo_traffico.constants.TipoPeriodoStatistico degradoAvgTimeTipoIntervalloOsservazioneStatistico) {
    this.degradoAvgTimeTipoIntervalloOsservazioneStatistico = degradoAvgTimeTipoIntervalloOsservazioneStatistico;
  }

  public java.lang.Integer getDegradoAvgTimeIntervalloOsservazione() {
    return this.degradoAvgTimeIntervalloOsservazione;
  }

  public void setDegradoAvgTimeIntervalloOsservazione(java.lang.Integer degradoAvgTimeIntervalloOsservazione) {
    this.degradoAvgTimeIntervalloOsservazione = degradoAvgTimeIntervalloOsservazione;
  }

  public void setDegradoAvgTimeFinestraOsservazioneRawEnumValue(String value) {
    this.degradoAvgTimeFinestraOsservazione = (TipoFinestra) TipoFinestra.toEnumConstantFromString(value);
  }

  public String getDegradoAvgTimeFinestraOsservazioneRawEnumValue() {
    if(this.degradoAvgTimeFinestraOsservazione == null){
    	return null;
    }else{
    	return this.degradoAvgTimeFinestraOsservazione.toString();
    }
  }

  public org.openspcoop2.core.controllo_traffico.constants.TipoFinestra getDegradoAvgTimeFinestraOsservazione() {
    return this.degradoAvgTimeFinestraOsservazione;
  }

  public void setDegradoAvgTimeFinestraOsservazione(org.openspcoop2.core.controllo_traffico.constants.TipoFinestra degradoAvgTimeFinestraOsservazione) {
    this.degradoAvgTimeFinestraOsservazione = degradoAvgTimeFinestraOsservazione;
  }

  public void setDegradoAvgTimeTipoLatenzaRawEnumValue(String value) {
    this.degradoAvgTimeTipoLatenza = (TipoLatenza) TipoLatenza.toEnumConstantFromString(value);
  }

  public String getDegradoAvgTimeTipoLatenzaRawEnumValue() {
    if(this.degradoAvgTimeTipoLatenza == null){
    	return null;
    }else{
    	return this.degradoAvgTimeTipoLatenza.toString();
    }
  }

  public org.openspcoop2.core.controllo_traffico.constants.TipoLatenza getDegradoAvgTimeTipoLatenza() {
    return this.degradoAvgTimeTipoLatenza;
  }

  public void setDegradoAvgTimeTipoLatenza(org.openspcoop2.core.controllo_traffico.constants.TipoLatenza degradoAvgTimeTipoLatenza) {
    this.degradoAvgTimeTipoLatenza = degradoAvgTimeTipoLatenza;
  }

  public boolean isApplicabilitaStatoAllarme() {
    return this.applicabilitaStatoAllarme;
  }

  public boolean getApplicabilitaStatoAllarme() {
    return this.applicabilitaStatoAllarme;
  }

  public void setApplicabilitaStatoAllarme(boolean applicabilitaStatoAllarme) {
    this.applicabilitaStatoAllarme = applicabilitaStatoAllarme;
  }

  public java.lang.String getAllarmeNome() {
    return this.allarmeNome;
  }

  public void setAllarmeNome(java.lang.String allarmeNome) {
    this.allarmeNome = allarmeNome;
  }

  public java.lang.Integer getAllarmeStato() {
    return this.allarmeStato;
  }

  public void setAllarmeStato(java.lang.Integer allarmeStato) {
    this.allarmeStato = allarmeStato;
  }

  public boolean isAllarmeNotStato() {
    return this.allarmeNotStato;
  }

  public boolean getAllarmeNotStato() {
    return this.allarmeNotStato;
  }

  public void setAllarmeNotStato(boolean allarmeNotStato) {
    this.allarmeNotStato = allarmeNotStato;
  }

  private static final long serialVersionUID = 1L;

  private static org.openspcoop2.core.controllo_traffico.model.ConfigurazionePolicyModel modelStaticInstance = null;
  private static synchronized void initModelStaticInstance(){
	  if(org.openspcoop2.core.controllo_traffico.ConfigurazionePolicy.modelStaticInstance==null){
  			org.openspcoop2.core.controllo_traffico.ConfigurazionePolicy.modelStaticInstance = new org.openspcoop2.core.controllo_traffico.model.ConfigurazionePolicyModel();
	  }
  }
  public static org.openspcoop2.core.controllo_traffico.model.ConfigurazionePolicyModel model(){
	  if(org.openspcoop2.core.controllo_traffico.ConfigurazionePolicy.modelStaticInstance==null){
	  		initModelStaticInstance();
	  }
	  return org.openspcoop2.core.controllo_traffico.ConfigurazionePolicy.modelStaticInstance;
  }


  @jakarta.xml.bind.annotation.XmlTransient
  protected IdPolicy oldIdPolicy;

  @jakarta.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="id-policy",required=true,nillable=false)
  protected java.lang.String idPolicy;

  @jakarta.xml.bind.annotation.XmlSchemaType(name="boolean")
  @XmlElement(name="built-in",required=true,nillable=false,defaultValue="false")
  protected boolean builtIn = false;

  @jakarta.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="descrizione",required=true,nillable=false)
  protected java.lang.String descrizione;

  @jakarta.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="risorsa",required=true,nillable=false)
  protected java.lang.String risorsa;

  @jakarta.xml.bind.annotation.XmlSchemaType(name="boolean")
  @XmlElement(name="simultanee",required=true,nillable=false,defaultValue="false")
  protected boolean simultanee = false;

  @jakarta.xml.bind.annotation.XmlSchemaType(name="unsignedLong")
  @XmlElement(name="valore",required=false,nillable=false)
  protected java.lang.Long valore;

  @jakarta.xml.bind.annotation.XmlSchemaType(name="unsignedLong")
  @XmlElement(name="valore2",required=false,nillable=false)
  protected java.lang.Long valore2;

  @jakarta.xml.bind.annotation.XmlTransient
  protected java.lang.String valoreTipoBandaRawEnumValue;

  @XmlElement(name="valore-tipo-banda",required=false,nillable=false)
  protected TipoBanda valoreTipoBanda;

  @jakarta.xml.bind.annotation.XmlTransient
  protected java.lang.String valoreTipoLatenzaRawEnumValue;

  @XmlElement(name="valore-tipo-latenza",required=false,nillable=false)
  protected TipoLatenza valoreTipoLatenza;

  @jakarta.xml.bind.annotation.XmlTransient
  protected java.lang.String modalitaControlloRawEnumValue;

  @XmlElement(name="modalita-controllo",required=false,nillable=false)
  protected TipoControlloPeriodo modalitaControllo;

  @jakarta.xml.bind.annotation.XmlTransient
  protected java.lang.String tipoIntervalloOsservazioneRealtimeRawEnumValue;

  @XmlElement(name="tipo-intervallo-osservazione-realtime",required=false,nillable=false)
  protected TipoPeriodoRealtime tipoIntervalloOsservazioneRealtime;

  @jakarta.xml.bind.annotation.XmlTransient
  protected java.lang.String tipoIntervalloOsservazioneStatisticoRawEnumValue;

  @XmlElement(name="tipo-intervallo-osservazione-statistico",required=false,nillable=false)
  protected TipoPeriodoStatistico tipoIntervalloOsservazioneStatistico;

  @jakarta.xml.bind.annotation.XmlSchemaType(name="unsignedInt")
  @XmlElement(name="intervallo-osservazione",required=false,nillable=false)
  protected java.lang.Integer intervalloOsservazione;

  @jakarta.xml.bind.annotation.XmlTransient
  protected java.lang.String finestraOsservazioneRawEnumValue;

  @XmlElement(name="finestra-osservazione",required=false,nillable=false)
  protected TipoFinestra finestraOsservazione;

  @jakarta.xml.bind.annotation.XmlTransient
  protected java.lang.String tipoApplicabilitaRawEnumValue;

  @XmlElement(name="tipo-applicabilita",required=true,nillable=false,defaultValue="sempre")
  protected TipoApplicabilita tipoApplicabilita = (TipoApplicabilita) TipoApplicabilita.toEnumConstantFromString("sempre");

  @jakarta.xml.bind.annotation.XmlSchemaType(name="boolean")
  @XmlElement(name="applicabilita-con-congestione",required=true,nillable=false,defaultValue="false")
  protected boolean applicabilitaConCongestione = false;

  @jakarta.xml.bind.annotation.XmlSchemaType(name="boolean")
  @XmlElement(name="applicabilita-degrado-prestazionale",required=true,nillable=false,defaultValue="false")
  protected boolean applicabilitaDegradoPrestazionale = false;

  @jakarta.xml.bind.annotation.XmlTransient
  protected java.lang.String degradoAvgTimeModalitaControlloRawEnumValue;

  @XmlElement(name="degrado-avg-time-modalita-controllo",required=false,nillable=false)
  protected TipoControlloPeriodo degradoAvgTimeModalitaControllo;

  @jakarta.xml.bind.annotation.XmlTransient
  protected java.lang.String degradoAvgTimeTipoIntervalloOsservazioneRealtimeRawEnumValue;

  @XmlElement(name="degrado-avg-time-tipo-intervallo-osservazione-realtime",required=false,nillable=false)
  protected TipoPeriodoRealtime degradoAvgTimeTipoIntervalloOsservazioneRealtime;

  @jakarta.xml.bind.annotation.XmlTransient
  protected java.lang.String degradoAvgTimeTipoIntervalloOsservazioneStatisticoRawEnumValue;

  @XmlElement(name="degrado-avg-time-tipo-intervallo-osservazione-statistico",required=false,nillable=false)
  protected TipoPeriodoStatistico degradoAvgTimeTipoIntervalloOsservazioneStatistico;

  @jakarta.xml.bind.annotation.XmlSchemaType(name="unsignedInt")
  @XmlElement(name="degrado-avg-time-intervallo-osservazione",required=false,nillable=false)
  protected java.lang.Integer degradoAvgTimeIntervalloOsservazione;

  @jakarta.xml.bind.annotation.XmlTransient
  protected java.lang.String degradoAvgTimeFinestraOsservazioneRawEnumValue;

  @XmlElement(name="degrado-avg-time-finestra-osservazione",required=false,nillable=false)
  protected TipoFinestra degradoAvgTimeFinestraOsservazione;

  @jakarta.xml.bind.annotation.XmlTransient
  protected java.lang.String degradoAvgTimeTipoLatenzaRawEnumValue;

  @XmlElement(name="degrado-avg-time-tipo-latenza",required=false,nillable=false)
  protected TipoLatenza degradoAvgTimeTipoLatenza;

  @jakarta.xml.bind.annotation.XmlSchemaType(name="boolean")
  @XmlElement(name="applicabilita-stato-allarme",required=true,nillable=false,defaultValue="false")
  protected boolean applicabilitaStatoAllarme = false;

  @jakarta.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="allarme-nome",required=false,nillable=false)
  protected java.lang.String allarmeNome;

  @jakarta.xml.bind.annotation.XmlSchemaType(name="unsignedInt")
  @XmlElement(name="allarme-stato",required=false,nillable=false)
  protected java.lang.Integer allarmeStato;

  @jakarta.xml.bind.annotation.XmlSchemaType(name="boolean")
  @XmlElement(name="allarme-not-stato",required=true,nillable=false,defaultValue="false")
  protected boolean allarmeNotStato = false;

}
