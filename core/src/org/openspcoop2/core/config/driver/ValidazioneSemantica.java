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


package org.openspcoop2.core.config.driver;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.openspcoop2.core.config.AccessoRegistro;
import org.openspcoop2.core.config.AccessoRegistroRegistro;
import org.openspcoop2.core.config.Attachments;
import org.openspcoop2.core.config.Cache;
import org.openspcoop2.core.config.Configurazione;
import org.openspcoop2.core.config.Connettore;
import org.openspcoop2.core.config.CorrelazioneApplicativa;
import org.openspcoop2.core.config.CorrelazioneApplicativaElemento;
import org.openspcoop2.core.config.CorrelazioneApplicativaRisposta;
import org.openspcoop2.core.config.CorrelazioneApplicativaRispostaElemento;
import org.openspcoop2.core.config.Credenziali;
import org.openspcoop2.core.config.Dump;
import org.openspcoop2.core.config.GestioneErrore;
import org.openspcoop2.core.config.GestioneErroreCodiceTrasporto;
import org.openspcoop2.core.config.GestioneErroreSoapFault;
import org.openspcoop2.core.config.IndirizzoRisposta;
import org.openspcoop2.core.config.InoltroBusteNonRiscontrate;
import org.openspcoop2.core.config.IntegrationManager;
import org.openspcoop2.core.config.InvocazioneCredenziali;
import org.openspcoop2.core.config.InvocazionePorta;
import org.openspcoop2.core.config.InvocazionePortaGestioneErrore;
import org.openspcoop2.core.config.InvocazioneServizio;
import org.openspcoop2.core.config.MessageSecurity;
import org.openspcoop2.core.config.MessageSecurityFlow;
import org.openspcoop2.core.config.MessageSecurityFlowParameter;
import org.openspcoop2.core.config.MessaggiDiagnostici;
import org.openspcoop2.core.config.MtomProcessor;
import org.openspcoop2.core.config.MtomProcessorFlow;
import org.openspcoop2.core.config.MtomProcessorFlowParameter;
import org.openspcoop2.core.config.OpenspcoopAppender;
import org.openspcoop2.core.config.PortaApplicativa;
import org.openspcoop2.core.config.PortaApplicativaAzione;
import org.openspcoop2.core.config.PortaApplicativaServizio;
import org.openspcoop2.core.config.PortaApplicativaServizioApplicativo;
import org.openspcoop2.core.config.PortaApplicativaSoggettoVirtuale;
import org.openspcoop2.core.config.PortaDelegata;
import org.openspcoop2.core.config.PortaDelegataAzione;
import org.openspcoop2.core.config.PortaDelegataServizio;
import org.openspcoop2.core.config.PortaDelegataServizioApplicativo;
import org.openspcoop2.core.config.PortaDelegataSoggettoErogatore;
import org.openspcoop2.core.config.Property;
import org.openspcoop2.core.config.Proprieta;
import org.openspcoop2.core.config.RispostaAsincrona;
import org.openspcoop2.core.config.Risposte;
import org.openspcoop2.core.config.Route;
import org.openspcoop2.core.config.RouteGateway;
import org.openspcoop2.core.config.RouteRegistro;
import org.openspcoop2.core.config.RoutingTable;
import org.openspcoop2.core.config.RoutingTableDefault;
import org.openspcoop2.core.config.RoutingTableDestinazione;
import org.openspcoop2.core.config.ServizioApplicativo;
import org.openspcoop2.core.config.Soggetto;
import org.openspcoop2.core.config.StatoServiziPdd;
import org.openspcoop2.core.config.StatoServiziPddIntegrationManager;
import org.openspcoop2.core.config.StatoServiziPddPortaApplicativa;
import org.openspcoop2.core.config.StatoServiziPddPortaDelegata;
import org.openspcoop2.core.config.TipoFiltroAbilitazioneServizi;
import org.openspcoop2.core.config.Tracciamento;
import org.openspcoop2.core.config.ValidazioneBuste;
import org.openspcoop2.core.config.ValidazioneContenutiApplicativi;
import org.openspcoop2.core.config.constants.AlgoritmoCache;
import org.openspcoop2.core.config.constants.CorrelazioneApplicativaGestioneIdentificazioneFallita;
import org.openspcoop2.core.config.constants.CorrelazioneApplicativaRichiestaIdentificazione;
import org.openspcoop2.core.config.constants.CorrelazioneApplicativaRispostaIdentificazione;
import org.openspcoop2.core.config.constants.CostantiConfigurazione;
import org.openspcoop2.core.config.constants.CredenzialeTipo;
import org.openspcoop2.core.config.constants.FaultIntegrazioneTipo;
import org.openspcoop2.core.config.constants.GestioneErroreComportamento;
import org.openspcoop2.core.config.constants.InvocazioneServizioTipoAutenticazione;
import org.openspcoop2.core.config.constants.PortaApplicativaAzioneIdentificazione;
import org.openspcoop2.core.config.constants.PortaDelegataAzioneIdentificazione;
import org.openspcoop2.core.config.constants.RegistroTipo;
import org.openspcoop2.core.config.constants.Severita;
import org.openspcoop2.core.config.constants.StatoFunzionalita;
import org.openspcoop2.core.config.constants.StatoFunzionalitaConWarning;
import org.openspcoop2.core.config.constants.TipoConnessioneRisposte;
import org.openspcoop2.core.config.constants.ValidazioneBusteTipoControllo;
import org.openspcoop2.core.config.constants.ValidazioneContenutiApplicativiTipo;
import org.openspcoop2.core.constants.CostantiConnettori;
import org.openspcoop2.core.constants.CostantiDB;
import org.openspcoop2.core.constants.TipiConnettore;
import org.openspcoop2.utils.certificate.CertificateUtils;
import org.openspcoop2.utils.certificate.PrincipalType;
import org.openspcoop2.utils.regexp.RegExpUtilities;
import org.openspcoop2.utils.regexp.RegularExpressionEngine;
import org.slf4j.Logger;


/**
 * Validazione Semantica
 * 
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ValidazioneSemantica {

	/** ConfigurazionePdD */
	private org.openspcoop2.core.config.Openspcoop2 configurazione = null;
	/** Lista in cui scrivo le anomalie riscontrate */
	private List<String> errori = new ArrayList<String>();
	/** Logger */
	private Logger log = null;
	/** Indicazione se deve essere validata la configurazione */
	private boolean validazioneConfigurazione = false;

	/** Lista di tipi di connettori validi */
	private List<String> tipoConnettori = new ArrayList<String>();
	/** Lista dei tipi di connettori ammessi */
	private String getTipoConnettori(){
		StringBuilder bf = new StringBuilder();
		for(int i=0; i<this.tipoConnettori.size(); i++){
			if(i>0)
				bf.append(",");
			bf.append(this.tipoConnettori.get(i));
		}
		return bf.toString();
	}

	/** Lista di tipi di soggetti validi */
	private List<String> tipoSoggetti = new ArrayList<String>();
	/** Lista dei tipi di soggetti ammessi */
	private String getTipoSoggetti(){
		StringBuilder bf = new StringBuilder();
		for(int i=0; i<this.tipoSoggetti.size(); i++){
			if(i>0)
				bf.append(",");
			bf.append(this.tipoSoggetti.get(i));
		}
		return bf.toString();
	}

	/** Lista di tipi di servizi (soap) validi */
	private List<String> tipoServiziSoap = new ArrayList<String>();
	/** Lista dei tipi di servizi ammessi */
	private String getTipoServiziSoap(){  
		StringBuilder bf = new StringBuilder();
		for(int i=0; i<this.tipoServiziSoap.size(); i++){
			if(i>0)
				bf.append(",");
			bf.append(this.tipoServiziSoap.get(i));
		}
		return bf.toString();
	}
	
	/** Lista di tipi di servizi (rest) validi */
	private List<String> tipoServiziRest = new ArrayList<String>();
	/** Lista dei tipi di servizi ammessi */
	private String getTipoServiziRest(){
		StringBuilder bf = new StringBuilder();
		for(int i=0; i<this.tipoServiziRest.size(); i++){
			if(i>0)
				bf.append(",");
			bf.append(this.tipoServiziRest.get(i));
		}
		return bf.toString();
	}
	
	private String getTipoServizi(){
		StringBuilder bf = new StringBuilder();
		
		String soap = getTipoServiziSoap();
		if(soap!=null && !"".equals(soap)){
			bf.append(soap);
		}
		
		String rest = getTipoServiziRest();
		if(rest!=null && !"".equals(rest)){
			if(bf.length()>0)
				bf.append(",");
			bf.append(rest);
		}
		
		return bf.toString();
	}
	
	/** Lista di tipi di msg diagnostici appender validi */
	private List<String> tipoMsgDiagnosticiAppender = new ArrayList<String>();
	/** Lista dei tipi di msg diagnostici appender ammessi */
	private String getTipoMsgDiagnosticiAppender(){
		StringBuilder bf = new StringBuilder();
		for(int i=0; i<this.tipoMsgDiagnosticiAppender.size(); i++){
			if(i>0)
				bf.append(",");
			bf.append(this.tipoMsgDiagnosticiAppender.get(i));
		}
		return bf.toString();
	}
	
	/** Lista di tipi di tracciamento appender validi */
	private List<String> tipoTracciamentoAppender = new ArrayList<String>();
	/** Lista dei tipi di tracciamento appender ammessi */
	private String getTipoTracciamentoAppender(){
		StringBuilder bf = new StringBuilder();
		for(int i=0; i<this.tipoTracciamentoAppender.size(); i++){
			if(i>0)
				bf.append(",");
			bf.append(this.tipoTracciamentoAppender.get(i));
		}
		return bf.toString();
	}
	
	/** Lista di tipi di dump appender validi */
	private List<String> tipoDumpAppender = new ArrayList<String>();
	/** Lista dei tipi di dump appender ammessi */
	private String getTipoDumpAppender(){
		StringBuilder bf = new StringBuilder();
		for(int i=0; i<this.tipoDumpAppender.size(); i++){
			if(i>0)
				bf.append(",");
			bf.append(this.tipoDumpAppender.get(i));
		}
		return bf.toString();
	}

	/** Lista di tipi di autenticazione porta delegata validi */
	private List<String> tipoAutenticazionePortaDelegata = new ArrayList<String>();
	/** Lista dei tipi di autenticazione porta delegata ammessi */
	private String getTipoAutenticazionePortaDelegata(){
		StringBuilder bf = new StringBuilder();
		for(int i=0; i<this.tipoAutenticazionePortaDelegata.size(); i++){
			if(i>0)
				bf.append(",");
			bf.append(this.tipoAutenticazionePortaDelegata.get(i));
		}
		return bf.toString();
	}
	
	/** Lista di tipi di autenticazione porta applicativa validi */
	private List<String> tipoAutenticazionePortaApplicativa = new ArrayList<String>();
	/** Lista dei tipi di autenticazione porta applicativa ammessi */
	private String getTipoAutenticazionePortaApplicativa(){
		StringBuilder bf = new StringBuilder();
		for(int i=0; i<this.tipoAutenticazionePortaApplicativa.size(); i++){
			if(i>0)
				bf.append(",");
			bf.append(this.tipoAutenticazionePortaApplicativa.get(i));
		}
		return bf.toString();
	}
	
	/** Lista di tipi di autorizzazione porta delegata validi */
	private List<String> tipoAutorizzazionePortaDelegata = new ArrayList<String>();
	/** Lista dei tipi di autorizzazione porta delegata ammessi */
	private String getTipoAutorizzazionePortaDelegata(){
		StringBuilder bf = new StringBuilder();
		for(int i=0; i<this.tipoAutorizzazionePortaDelegata.size(); i++){
			if(i>0)
				bf.append(",");
			bf.append(this.tipoAutorizzazionePortaDelegata.get(i));
		}
		return bf.toString();
	}
	
	/** Lista di tipi di autorizzazione porta applicativa validi */
	private List<String> tipoAutorizzazionePortaApplicativa = new ArrayList<String>();
	/** Lista dei tipi di autorizzazione porta applicativa ammessi */
	private String getTipoAutorizzazionePortaApplicativa(){
		StringBuilder bf = new StringBuilder();
		for(int i=0; i<this.tipoAutorizzazionePortaApplicativa.size(); i++){
			if(i>0)
				bf.append(",");
			bf.append(this.tipoAutorizzazionePortaApplicativa.get(i));
		}
		return bf.toString();
	}
	
	/** Lista di tipi di autorizzazione per contenuto porta delegata validi */
	private List<String> tipoAutorizzazioneContenutoPortaDelegata = new ArrayList<String>();
	/** Lista dei tipi di autorizzazione per contenuto porta delegata  ammessi */
	private String getTipoAutorizzazioneContenutoPortaDelegata(){
		StringBuilder bf = new StringBuilder();
		for(int i=0; i<this.tipoAutorizzazioneContenutoPortaDelegata.size(); i++){
			if(i>0)
				bf.append(",");
			bf.append(this.tipoAutorizzazioneContenutoPortaDelegata.get(i));
		}
		return bf.toString();
	}
	
	/** Lista di tipi di autorizzazione per contenuto porta applicativa validi */
	private List<String> tipoAutorizzazioneContenutoPortaApplicativa = new ArrayList<String>();
	/** Lista dei tipi di autorizzazione per contenuto porta applicativa  ammessi */
	private String getTipoAutorizzazioneContenutoPortaApplicativa(){
		StringBuilder bf = new StringBuilder();
		for(int i=0; i<this.tipoAutorizzazioneContenutoPortaApplicativa.size(); i++){
			if(i>0)
				bf.append(",");
			bf.append(this.tipoAutorizzazioneContenutoPortaApplicativa.get(i));
		}
		return bf.toString();
	}
	
	/** Lista di tipi di integrazionePD validi */
	private List<String> tipoIntegrazionePD = new ArrayList<String>();
	/** Lista dei tipi di integrazionePD ammessi */
	private String getTipoIntegrazionePD(){
		StringBuilder bf = new StringBuilder();
		for(int i=0; i<this.tipoIntegrazionePD.size(); i++){
			if(i>0)
				bf.append(",");
			bf.append(this.tipoIntegrazionePD.get(i));
		}
		return bf.toString();
	}
	
	/** Lista di tipi di integrazionePA validi */
	private List<String> tipoIntegrazionePA = new ArrayList<String>();
	/** Lista dei tipi di integrazionePA ammessi */
	private String getTipoIntegrazionePA(){
		StringBuilder bf = new StringBuilder();
		for(int i=0; i<this.tipoIntegrazionePA.size(); i++){
			if(i>0)
				bf.append(",");
			bf.append(this.tipoIntegrazionePA.get(i));
		}
		return bf.toString();
	}
	
	

	public ValidazioneSemantica(org.openspcoop2.core.config.Openspcoop2 configurazione,
			String[]tipoConnettori,String[]tipoSoggetti,String[]tipoServiziSoap,String[]tipoServiziRest,
			String[]tipoMsgDiagnosticiAppender,String[]tipoTracciamentoAppender,String[]tipoDumpAppender,
			String[]tipoAutenticazionePortaDelegata,String[]tipoAutenticazionePortaApplicativa,
			String[]tipoAutorizzazionePortaDelegata,String[]tipoAutorizzazionePortaApplicativa,
			String[]tipoAutorizzazioneContenutoPortaDelegata,String[]tipoAutorizzazioneContenutoPortaApplicativa,
			String[]tipoIntegrazionePD,String[]tipoIntegrazionePA,
			boolean validazioneConfigurazione,Logger log) throws DriverConfigurazioneException{
		this.configurazione = configurazione;
		this.log = log;
		this.validazioneConfigurazione = validazioneConfigurazione;
		if(tipoConnettori!=null && tipoConnettori.length>0 ){
			for(int i=0; i<tipoConnettori.length; i++){
				this.tipoConnettori.add(tipoConnettori[i]);
			}
		}else{
			throw new DriverConfigurazioneException("Tipo di connettori ammissibili non definiti");
		}
		if(tipoSoggetti!=null && tipoSoggetti.length>0 ){
			for(int i=0; i<tipoSoggetti.length; i++){
				this.tipoSoggetti.add(tipoSoggetti[i]);
			}
		}else{
			throw new DriverConfigurazioneException("Tipo di soggetti ammissibili non definiti");
		}
		boolean tipiSoapNonEsistenti = false;
		if(tipoServiziSoap!=null && tipoServiziSoap.length>0 ){
			for(int i=0; i<tipoServiziSoap.length; i++){
				this.tipoServiziSoap.add(tipoServiziSoap[i]);
			}
		}else{
			tipiSoapNonEsistenti = true;
		}
		boolean tipiRestNonEsistenti = false;
		if(tipoServiziRest!=null && tipoServiziRest.length>0 ){
			for(int i=0; i<tipoServiziRest.length; i++){
				this.tipoServiziRest.add(tipoServiziRest[i]);
			}
		}else{
			tipiRestNonEsistenti = true;			
		}
		if(tipiSoapNonEsistenti && tipiRestNonEsistenti){
			throw new DriverConfigurazioneException("Non sono stati configurati tipo di servizi ne per il service binding Soap ne per Rest");
		}
		if(tipoMsgDiagnosticiAppender!=null && tipoMsgDiagnosticiAppender.length>0 ){
			for(int i=0; i<tipoMsgDiagnosticiAppender.length; i++){
				this.tipoMsgDiagnosticiAppender.add(tipoMsgDiagnosticiAppender[i]);
			}
		}else{
			throw new DriverConfigurazioneException("Tipo di appender ammissibili per i msg diagnostici non definiti");
		}
		if(tipoTracciamentoAppender!=null && tipoTracciamentoAppender.length>0 ){
			for(int i=0; i<tipoTracciamentoAppender.length; i++){
				this.tipoTracciamentoAppender.add(tipoTracciamentoAppender[i]);
			}
		}else{
			throw new DriverConfigurazioneException("Tipo di appender ammissibili per le traccie non definiti");
		}
		if(tipoDumpAppender!=null && tipoDumpAppender.length>0 ){
			for(int i=0; i<tipoDumpAppender.length; i++){
				this.tipoDumpAppender.add(tipoDumpAppender[i]);
			}
		}else{
			throw new DriverConfigurazioneException("Tipo di appender ammissibili per il dump non definiti");
		}
		if(tipoAutenticazionePortaDelegata!=null && tipoAutenticazionePortaDelegata.length>0 ){
			for(int i=0; i<tipoAutenticazionePortaDelegata.length; i++){
				this.tipoAutenticazionePortaDelegata.add(tipoAutenticazionePortaDelegata[i]);
			}
		}else{
			throw new DriverConfigurazioneException("Tipo di processi di autenticazione porta delegata ammissibili non definiti");
		}
		if(tipoAutenticazionePortaApplicativa!=null && tipoAutenticazionePortaApplicativa.length>0 ){
			for(int i=0; i<tipoAutenticazionePortaApplicativa.length; i++){
				this.tipoAutenticazionePortaApplicativa.add(tipoAutenticazionePortaApplicativa[i]);
			}
		}else{
			throw new DriverConfigurazioneException("Tipo di processi di autenticazione porta applicativa ammissibili non definiti");
		}
		if(tipoAutorizzazionePortaDelegata!=null && tipoAutorizzazionePortaDelegata.length>0 ){
			for(int i=0; i<tipoAutorizzazionePortaDelegata.length; i++){
				this.tipoAutorizzazionePortaDelegata.add(tipoAutorizzazionePortaDelegata[i]);
			}
		}else{
			throw new DriverConfigurazioneException("Tipo di processi di autorizzazione porta delegata ammissibili non definiti");
		}
		if(tipoAutorizzazionePortaApplicativa!=null && tipoAutorizzazionePortaApplicativa.length>0 ){
			for(int i=0; i<tipoAutorizzazionePortaApplicativa.length; i++){
				this.tipoAutorizzazionePortaApplicativa.add(tipoAutorizzazionePortaApplicativa[i]);
			}
		}else{
			throw new DriverConfigurazioneException("Tipo di processi di autorizzazione porta applicativa ammissibili non definiti");
		}
		if(tipoAutorizzazioneContenutoPortaDelegata!=null && tipoAutorizzazioneContenutoPortaDelegata.length>0 ){
			for(int i=0; i<tipoAutorizzazioneContenutoPortaDelegata.length; i++){
				this.tipoAutorizzazioneContenutoPortaDelegata.add(tipoAutorizzazioneContenutoPortaDelegata[i]);
			}
		}else{
			throw new DriverConfigurazioneException("Tipo di processi di autorizzazione contenuto porta delegata ammissibili non definiti");
		}
		if(tipoAutorizzazioneContenutoPortaApplicativa!=null && tipoAutorizzazioneContenutoPortaApplicativa.length>0 ){
			for(int i=0; i<tipoAutorizzazioneContenutoPortaApplicativa.length; i++){
				this.tipoAutorizzazioneContenutoPortaApplicativa.add(tipoAutorizzazioneContenutoPortaApplicativa[i]);
			}
		}else{
			throw new DriverConfigurazioneException("Tipo di processi di autorizzazione contenuto porta applicativa ammissibili non definiti");
		}
		if(tipoIntegrazionePD!=null && tipoIntegrazionePD.length>0 ){
			for(int i=0; i<tipoIntegrazionePD.length; i++){
				this.tipoIntegrazionePD.add(tipoIntegrazionePD[i]);
			}
		}else{
			throw new DriverConfigurazioneException("Tipo di integrazione lato PortaDelegata ammissibili non definiti");
		}
		if(tipoIntegrazionePA!=null && tipoIntegrazionePA.length>0 ){
			for(int i=0; i<tipoIntegrazionePA.length; i++){
				this.tipoIntegrazionePA.add(tipoIntegrazionePA[i]);
			}
		}else{
			throw new DriverConfigurazioneException("Tipo di integrazione lato PortaApplicativa ammissibili non definiti");
		}
	}
	public ValidazioneSemantica(org.openspcoop2.core.config.Openspcoop2 configurazione,
			String[]tipoConnettori,String[]tipoSoggetti,String[]tipoServiziSoap,String[]tipoServiziRest,
			String[]tipoMsgDiagnosticiAppender,String[]tipoTracciamentoAppender,String[]tipoDumpAppender,
			String[]tipoAutenticazionePortaDelegata,String[]tipoAutenticazionePortaApplicativa,
			String[]tipoAutorizzazionePortaDelegata,String[]tipoAutorizzazionePortaApplicativa,
			String[]tipoAutorizzazioneContenutoPortaDelegata,String[]tipoAutorizzazioneContenutoPortaApplicativa,
			String[]tipoIntegrazionePD,String[]tipoIntegrazionePA,
			boolean validazioneConfigurazione) throws DriverConfigurazioneException{
		this(configurazione,tipoConnettori,tipoSoggetti,tipoServiziSoap,tipoServiziRest,tipoMsgDiagnosticiAppender,tipoTracciamentoAppender,tipoDumpAppender,
				tipoAutenticazionePortaDelegata,tipoAutenticazionePortaApplicativa,
				tipoAutorizzazionePortaDelegata,tipoAutorizzazionePortaApplicativa,
				tipoAutorizzazioneContenutoPortaDelegata,tipoAutorizzazioneContenutoPortaApplicativa,
				tipoIntegrazionePD,tipoIntegrazionePA,
				validazioneConfigurazione,null);
	}

	private void printMsg(String msg){
		if(this.log == null){
			printMsg(msg);
		}else{
			this.log.debug(msg);
		}
	}


	public void validazioneSemantica(boolean showIDOggettiAnalizzati) throws DriverConfigurazioneException {

		if(showIDOggettiAnalizzati)
			printMsg("\n\n------------------------------------Soggetti("+this.configurazione.sizeSoggettoList()+")-----------------------------------------------------");

		// soggetto
		for(int i=0; i<this.configurazione.sizeSoggettoList();i++){
			Soggetto sogg = this.configurazione.getSoggetto(i);
			if(showIDOggettiAnalizzati)
				printMsg("Soggetto: "+sogg.getTipo()+"/"+sogg.getNome());
			validaSoggetto(sogg,showIDOggettiAnalizzati);
		}


		if(this.validazioneConfigurazione){
			if(showIDOggettiAnalizzati)
				printMsg("\n\n------------------------------------Configurazione-----------------------------------------------------");
	
			// Configurazione
			this.validaConfigurazione(this.configurazione.getConfigurazione());
		}


		if(showIDOggettiAnalizzati)
			printMsg("\n\n-----------------------------------------------------------------------------------------");

		// Se è stata trovata almeno un'anomalia, lancio un'eccezione
		if (!this.errori.isEmpty()) {
			StringBuilder stringB = new StringBuilder().append("\n");
			Iterator<String> itE = this.errori.iterator();
			while (itE.hasNext())
				stringB.append(itE.next()).append("\n");
			throw new DriverConfigurazioneException(stringB.toString());
		}
	}


	private  void validaSoggetto(Soggetto sogg,boolean showIDOggettiAnalizzati) throws DriverConfigurazioneException {

		// required
		if(sogg.getNome()==null){
			this.errori.add("Esiste un soggetto senza nome");
			return;
		}
		if(sogg.getTipo()==null){
			this.errori.add("Esiste un soggetto senza tipo");
			return;
		}
		
		// porte delegate
		for(int j =0; j<sogg.sizePortaDelegataList(); j++){
			PortaDelegata pd = sogg.getPortaDelegata(j);
			if(showIDOggettiAnalizzati)
				printMsg("\tPorta Delegata: "+pd.getNome());
			validaPortaDelegata(pd, sogg);
		}

		// porte applicative
		for(int j =0; j<sogg.sizePortaApplicativaList(); j++){
			PortaApplicativa pa = sogg.getPortaApplicativa(j);
			if(showIDOggettiAnalizzati)
				printMsg("\tPortaApplicativa: "+pa.getNome());
			validaPortaApplicativa(pa, sogg);
		}

		// servizi applicativi
		for(int j =0; j<sogg.sizeServizioApplicativoList(); j++){
			ServizioApplicativo sa = sogg.getServizioApplicativo(j);
			if(showIDOggettiAnalizzati)
				printMsg("\tServizioApplicativo: "+sa.getNome());
			validaServizioApplicativo(sa, sogg);
		}

		// connettori presenti nella root del soggetto
		for(int j=0; j<sogg.sizeConnettoreList();j++){
			Connettore connettore = sogg.getConnettore(j);
			if(showIDOggettiAnalizzati)
				printMsg("\tConnettore definito nella root del soggetto: "+connettore.getNome());
			this.validaConnettore(connettore, null, sogg);
		}
		
		// Il tipo del soggetto deve essere uno tra quelli definiti in govway.properties. Ci puoi accedere attraverso il comando: org.openspcoop.pdd.config.OpenSPCoopProperties.getInstance().getTipiSoggetti()
		if(this.tipoSoggetti.contains(sogg.getTipo())==false){
			this.errori.add("Il tipo del soggetto "+sogg.getTipo()+"/"+sogg.getNome()+" non è valido (Tipi utilizzabili: "+this.getTipoSoggetti()+")");
		}

		// Nome del soggetto
		try{
			if (!RegularExpressionEngine.isMatch(sogg.getNome(),"^[0-9A-Za-z]+$")) {
				this.errori.add("Il nome del soggetto "+sogg.getTipo()+"/"+sogg.getNome()+" dev'essere formato solo caratteri e cifre");
			}
		}catch(Exception e){
			throw new DriverConfigurazioneException("Errore durante l'analisi tramite espressione regolare del soggetto "+sogg.getTipo()+"/"+sogg.getNome()+": "+e.getMessage(),e);
		}

		// ogni soggetto identificato come coppia tipo/nome, deve essere univoco
		int numS = 0;
		for(int j=0; j<this.configurazione.sizeSoggettoList();j++){
			Soggetto tmpSogg = this.configurazione.getSoggetto(j);
			if (sogg.getNome().equals(tmpSogg.getNome()) && sogg.getTipo().equals(tmpSogg.getTipo()))
				numS++;
		}
		if (numS > 1)
			this.errori.add("Non può esistere più di un soggetto con nome "+sogg.getNome()+" e tipo "+sogg.getTipo());


		// Controlla che esista SOLO un soggetto che contiene router=true
		if (sogg.getRouter()) {
			int numR = 0;
			for(int j=0; j<this.configurazione.sizeSoggettoList();j++){
				Soggetto tmpS = this.configurazione.getSoggetto(j);
				if (tmpS.getRouter())
					numR++;
			}
			if (numR > 1)
				this.errori.add("Non può esistere più di un soggetto abilitato alla funzione di Router");
		}
		
		// Controllo che eventuali PdUrlPrefixRewriter o PaUrlPrefixRewriter rispettino l'espressione regolare: [A-Za-z]+:\/\/(.*)
		try{
			if(sogg.getPdUrlPrefixRewriter()!=null){
				if(RegularExpressionEngine.isMatch(sogg.getPdUrlPrefixRewriter(), "[A-Za-z]+:\\/\\/(.*)")==false){
					this.errori.add("La funzione PdUrlPrefixRewriter contiene un valore errato che non rispetta l'espressione "+"[A-Za-z]+:\\/\\/(.*)");
				}
			}
			if(sogg.getPaUrlPrefixRewriter()!=null){
				if(RegularExpressionEngine.isMatch(sogg.getPaUrlPrefixRewriter(), "[A-Za-z]+:\\/\\/(.*)")==false){
					this.errori.add("La funzione PaUrlPrefixRewriter contiene un valore errato che non rispetta l'espressione "+"[A-Za-z]+:\\/\\/(.*)");
				}
			}
		}catch(Exception e){
			throw new DriverConfigurazioneException(e);
		}
	}


	private  void validaPortaDelegata(PortaDelegata pd, Soggetto sogg) throws DriverConfigurazioneException {
		
		if(pd.getNome()==null){
			this.errori.add("Il soggetto ["+sogg.getTipo()+"/"+sogg.getNome()+"] posside una porta delegata che non contiene la definizione del nome");
			return;
		}
		
		String nomePorta = pd.getNome();
		String idPortaDelegata = "("+sogg.getTipo()+"/"+sogg.getNome()+") "+nomePorta;
		
		// La porta delegata viene identificata dal nome.
		// Una volta identificata la sua stringa di identificazione DEVE essere univoca tra tutte le porte delegate di TUTTA la configurazione 
		// (e non solo tra quelle del soggetto proprietario)
		int numPD = 0;
		List<String> pdTrovate = new ArrayList<String>();
		for(int j=0; j<this.configurazione.sizeSoggettoList();j++){
			Soggetto s = this.configurazione.getSoggetto(j);
			for(int k =0; k<s.sizePortaDelegataList(); k++){
				PortaDelegata tmpPd = s.getPortaDelegata(k);
				String tmpNomePorta = tmpPd.getNome();
				if (nomePorta.equals(tmpNomePorta)){
					numPD++;
					pdTrovate.add("("+s.getTipo()+"/"+s.getNome()+") "+tmpPd.getNome());
				}
			}
		}
		if (numPD > 1)
			this.errori.add("Non può esistere più di una porta delegata con nome "+nomePorta+". Sono state identificate le seguenti porte delegate: "+pdTrovate);

		
		// Il soggetto erogatore deve essere definito. Il tipo e' obbligatorio.
		// Il nome DEVE essere presente solo se identificazione='static'.
		// Il pattern DEVE essere definito solo se identificazione='urlBased' o 'contentBased'.
		if (pd.getSoggettoErogatore() == null)
			this.errori.add("La porta delegata "+idPortaDelegata+" non contiene la definizione del soggetto erogatore");
		else {
			PortaDelegataSoggettoErogatore pdsse = pd.getSoggettoErogatore();
			
			// Tipo
			if (pdsse.getTipo() == null){
				this.errori.add("Il soggetto erogatore della porta delegata "+idPortaDelegata+" non contiene la definizione del tipo.");
			}
			else if(this.tipoSoggetti.contains(pdsse.getTipo())==false){
				this.errori.add("Il tipo del soggetto erogatore della porta delegata "+idPortaDelegata+" non è valido (Tipi utilizzabili: "+this.getTipoSoggetti()+")");
			}
			
			// Nome
			if (pdsse.getNome() == null){
				this.errori.add("Il soggetto erogatore della porta delegata "+idPortaDelegata+" non contiene la definizione del nome");
			}			
		}

		// Il servizio deve essere definito. Il tipo e' obbligatorio.
		// Il nome DEVE essere presente solo se identificazione='static'.
		// Il pattern DEVE essere definito solo se identificazione='urlBased' o 'contentBased'.
		if (pd.getServizio() == null)
			this.errori.add("La porta delegata "+idPortaDelegata+" non contiene la definizione del servizio");
		else {
			PortaDelegataServizio pds = pd.getServizio();
			
			// Tipo
			if (pds.getTipo() == null){
				this.errori.add("Il servizio della porta delegata "+idPortaDelegata+" non contiene la definizione del tipo.");
			}
			else if(this.tipoServiziSoap.contains(pds.getTipo())==false && this.tipoServiziRest.contains(pds.getTipo())==false){
				this.errori.add("Il tipo del servizio della porta delegata "+idPortaDelegata+" non è valido (Tipi utilizzabili: "+this.getTipoServizi()+")");
			}
			
			// Nome
			if (pds.getNome() == null){
				this.errori.add("Il servizio della porta delegata "+idPortaDelegata+" non contiene la definizione del nome");
			}		
		}

		
		// L'azione e' opzionale. Se presente:
		// Il nome DEVE essere presente solo se identificazione='static'.
		// Il pattern DEVE essere definito solo se identificazione='urlBased' o 'contentBased'.
		if (pd.getAzione() != null){
			PortaDelegataAzione pda = pd.getAzione();
			
			// XSD: identificazione: static, urlBased, contentBased, inputBased e soapActionBased
			PortaDelegataAzioneIdentificazione identificazione = pda.getIdentificazione();
			if ((identificazione != null) && !identificazione.equals(CostantiConfigurazione.PORTA_DELEGATA_AZIONE_STATIC) && 
					!identificazione.equals(CostantiConfigurazione.PORTA_DELEGATA_AZIONE_URL_BASED) && 
					!identificazione.equals(CostantiConfigurazione.PORTA_DELEGATA_AZIONE_CONTENT_BASED) &&
					!identificazione.equals(CostantiConfigurazione.PORTA_DELEGATA_AZIONE_HEADER_BASED) && 
					!identificazione.equals(CostantiConfigurazione.PORTA_DELEGATA_AZIONE_INPUT_BASED) && 
					!identificazione.equals(CostantiConfigurazione.PORTA_DELEGATA_AZIONE_SOAP_ACTION_BASED) && 
					!identificazione.equals(CostantiConfigurazione.PORTA_DELEGATA_AZIONE_WSDL_BASED) && 
					!identificazione.equals(CostantiConfigurazione.PORTA_DELEGATA_AZIONE_DELEGATED_BY)){
				this.errori.add("La modalita d'identificazione dell'azione della porta delegata "+idPortaDelegata+" deve assumere uno dei seguente valori: "+
						CostantiConfigurazione.PORTA_DELEGATA_AZIONE_STATIC+", "+
						CostantiConfigurazione.PORTA_DELEGATA_AZIONE_URL_BASED+", "+
						CostantiConfigurazione.PORTA_DELEGATA_AZIONE_CONTENT_BASED+", "+
						CostantiConfigurazione.PORTA_DELEGATA_AZIONE_HEADER_BASED+" o "+
						CostantiConfigurazione.PORTA_DELEGATA_AZIONE_INPUT_BASED+" o "+
						CostantiConfigurazione.PORTA_DELEGATA_AZIONE_SOAP_ACTION_BASED+" o "+
						CostantiConfigurazione.PORTA_DELEGATA_AZIONE_WSDL_BASED+" o "+
						CostantiConfigurazione.PORTA_DELEGATA_AZIONE_DELEGATED_BY);
			}
			if(identificazione==null){
				identificazione = CostantiConfigurazione.PORTA_DELEGATA_AZIONE_STATIC;
			}
			
			if(CostantiConfigurazione.PORTA_DELEGATA_AZIONE_STATIC.equals(identificazione)){
				if (pda.getNome() == null)
					this.errori.add("L'azione della porta delegata "+idPortaDelegata+" non contiene la definizione del nome, nonostante la modalita' di identificazione sia "+identificazione);
			}
			else if(CostantiConfigurazione.PORTA_DELEGATA_AZIONE_URL_BASED.equals(identificazione) || 
					CostantiConfigurazione.PORTA_DELEGATA_AZIONE_CONTENT_BASED.equals(identificazione) || 
					CostantiConfigurazione.PORTA_DELEGATA_AZIONE_HEADER_BASED.equals(identificazione)){
				if (pda.getPattern() == null)
					this.errori.add("L'azione della porta delegata "+idPortaDelegata+" non contiene la definizione del pattern, nonostante la modalita' di identificazione sia "+identificazione);
			}
			else if(CostantiConfigurazione.PORTA_DELEGATA_AZIONE_DELEGATED_BY.equals(identificazione)){
				if (pda.getNomePortaDelegante() == null)
					this.errori.add("L'azione della porta delegata "+idPortaDelegata+" non contiene la definizione del nome della porta delegante, nonostante la modalita' di identificazione sia "+identificazione);
			}
		}
		
	
		// Check processo di autenticazione della porta delegata
		String autenticazione = CredenzialeTipo.SSL.toString();
		if(pd.getAutenticazione()!=null){
			autenticazione = pd.getAutenticazione();
			if(this.tipoAutenticazionePortaDelegata.contains(pd.getAutenticazione())==false){
				this.errori.add("Alla porta delegata "+idPortaDelegata+" e' stato associato un processo di autenticazione ["+pd.getAutenticazione()+"] non valido; tipi conosciuti: "+this.getTipoAutenticazionePortaDelegata()); 
			}
		}
		
		// Autorizzazione
		String autorizzazione = CostantiConfigurazione.AUTORIZZAZIONE_AUTHENTICATED;
		if(pd.getAutorizzazione()!=null){
			autorizzazione = pd.getAutorizzazione();
			if(this.tipoAutorizzazionePortaDelegata.contains(pd.getAutorizzazione())==false){
				this.errori.add("Alla porta delegata "+idPortaDelegata+" e' stato associato un processo di autorizzazione ["+pd.getAutorizzazione()+"] non valido; tipi conosciuti: "+this.getTipoAutorizzazionePortaDelegata()); 
			}
		}
		
		if(autorizzazione!=null){
			if(autorizzazione.toLowerCase().contains(CostantiConfigurazione.AUTORIZZAZIONE_AUTHENTICATED) &&
					autorizzazione.toLowerCase().contains(CostantiConfigurazione.AUTORIZZAZIONE_ROLES)){
				// Se e' definito un processo di autorizzazione che prevede sia autenticazione che ruoli l'autenticazione deve essere opzionale
				if(StatoFunzionalita.ABILITATO.equals(pd.getAutenticazioneOpzionale())==false){
					this.errori.add("Alla porta delegata "+idPortaDelegata+" e' stato associata un'autorizzazione '"+autorizzazione+"' che richiede una autenticazione dei servizi applicativi opzionali"); 
				}
			}
			if(autorizzazione.toLowerCase().contains("internal") || 
					(CostantiConfigurazione.AUTORIZZAZIONE_AUTHENTICATED.equals(autorizzazione))){
				// Se l'autorizzazione prevede una gestione dei ruoli interni, oppure è solamente basata sull'autenticazione dei chiamanti, 
				// una autenticazione DEVE essere presente e non deve essere opzionale
				if(CostantiConfigurazione.AUTENTICAZIONE_NONE.equals(autenticazione)){
					this.errori.add("Alla porta delegata "+idPortaDelegata+" e' stato associata un'autorizzazione '"+autorizzazione+"' che richiede una autenticazione obbligatoria dei servizi applicativi, autenticazione non impostata nella porta delegata"); 
				}
				if(StatoFunzionalita.ABILITATO.equals(pd.getAutenticazioneOpzionale())){
					if(autorizzazione.toLowerCase().contains("or")==false){
						this.errori.add("Alla porta delegata "+idPortaDelegata+" e' stato associata un'autorizzazione '"+autorizzazione+"' che richiede una autenticazione obbligatoria dei servizi applicativi, autenticazione impostata come opzionale nella porta delegata");
					}
				}
			}
		}

		// Vincoli troppo forti nel caso di configurazione su console, la PdD non parte se la configurazione non e' stata ultimata.
//		if(autorizzazione!=null && autorizzazione.contains(CostantiConfigurazione.AUTORIZZAZIONE_AUTHENTICATED)){
//			if (pd.sizeServizioApplicativoList() == 0){
//				this.errori.add("La porta delegata "+idPortaDelegata+" deve avere almeno un servizio applicativo associato poiche' lo richiede il tipo di autorizzazione indicato: "+autorizzazione);
//			}
//		}
//		if(autorizzazione!=null && autorizzazione.toLowerCase().contains(CostantiConfigurazione.AUTORIZZAZIONE_ROLES.toLowerCase())){
//			if (pd.getRuoli()==null || pd.getRuoli().sizeRuoloList() == 0){
//				this.errori.add("La porta delegata "+idPortaDelegata+" deve avere almeno un ruolo associato poiche' lo richiede il tipo di autorizzazione indicato: "+autorizzazione);
//			}
//		}
		
		// Autorizzazione Contenuto
		if(pd.getAutorizzazioneContenuto()!=null){
			if(this.tipoAutorizzazioneContenutoPortaDelegata.contains(pd.getAutorizzazioneContenuto())==false){
				this.errori.add("Alla porta delegata "+idPortaDelegata+" e' stato associato un processo di autorizzazione contenuto ["+pd.getAutorizzazioneContenuto()+"] non valido; tipi conosciuti: "+this.getTipoAutorizzazioneContenutoPortaDelegata()); 
			}
		}
		
		// Check servizi applicativi
		for(int i=0; i<pd.sizeServizioApplicativoList(); i++){
			// Servizio Applicativo puo' avere solo meta informazioni
			PortaDelegataServizioApplicativo pdSa = pd.getServizioApplicativo(i);
			ServizioApplicativo sa = null;
			if(pdSa.getNome()==null){
				this.errori.add("La porta delegata "+idPortaDelegata+" ha associato un servizio applicativo per cui non e' stato definito il nome");
				continue;
			}
			else{
				// servizio applicativo definito nella root del soggetto
				if(existsServizioApplicativo(pdSa.getNome(), sogg)==false){
					this.errori.add("La porta delegata "+idPortaDelegata+" ha associato un riferimento ad un servizio applicativo ["+pdSa.getNome()+"] che non risulta registrato nel soggetto "+sogg.getTipo()+"/"+sogg.getNome());
					continue;
				}
				else{
					// Prelevo servizio applicativo per conformita con l'utilizzo nella porta delegata
					sa = this.getServizioApplicativo(pdSa.getNome(), sogg);
				}
			}
			
			// Validazione conformita' con porta delegata
			if(CostantiConfigurazione.INVOCAZIONE_SERVIZIO_AUTENTICAZIONE_NONE.equals(autenticazione)==false){
				if(sa.getInvocazionePorta()==null){
					this.errori.add("La porta delegata "+idPortaDelegata+" possiede un meccanismo di autenticazione ["+autenticazione+"]: il servizio applicativo "+sa.getNome() +" non contiene la definizione dell'elemento invocazione porta");
				}
				else if(sa.getInvocazionePorta().sizeCredenzialiList()<=0){
					this.errori.add("La porta delegata "+idPortaDelegata+" possiede un meccanismo di autenticazione ["+autenticazione+"]: il servizio applicativo "+sa.getNome() +" non contiene la definizione di credenziali di accesso");
				}
				else{
					if(CostantiConfigurazione.CREDENZIALE_BASIC.equals(autenticazione) ||
							CostantiConfigurazione.CREDENZIALE_APIKEY.equals(autenticazione) ||
							CostantiConfigurazione.CREDENZIALE_SSL.equals(autenticazione)  ||
							CostantiConfigurazione.CREDENZIALE_PRINCIPAL.equals(autenticazione)){
						boolean trovato = false;
						for(int k=0; k<sa.getInvocazionePorta().sizeCredenzialiList(); k++){
							if(autenticazione.equals(sa.getInvocazionePorta().getCredenziali(k).getTipo().getValue())){
								trovato=true;
								break;
							}
						}
						if(!trovato){
							this.errori.add("La porta delegata "+idPortaDelegata+" possiede un meccanismo di autenticazione ["+autenticazione+"]: il servizio applicativo "+sa.getNome() +" non contiene la definizione di credenziali di accesso compatibili");
						}
					}
				}
			}
		}

		// Valida SetProtocolProperties
		for (int j=0; j<pd.sizeProprietaList();j++) {
			Proprieta ssp = pd.getProprieta(j);
			validaProtocolProperty(ssp, "PortaDelegata_"+idPortaDelegata);
		}
		
		// Valida MessageSecurity
		MessageSecurity messageSecurity = pd.getMessageSecurity();
		if (messageSecurity != null)
			validaMessageSecurity(messageSecurity,"PortaDelegata_"+idPortaDelegata);

		// Valida MTOM
		MtomProcessor mtom = pd.getMtomProcessor();
		if (mtom != null)
			validaMTOM(mtom,"PortaDelegata_"+idPortaDelegata);
		
		// Valida ValidazioneContenutiApplicativi
		ValidazioneContenutiApplicativi vca = pd.getValidazioneContenutiApplicativi();
		if (vca != null)
			validazioneValidazioneContenutiApplicativi(vca, "PortaDelegata_"+idPortaDelegata);
			
		// CorrelazioneApplicativa:
		String idRisorsa = "Porta delegata "+idPortaDelegata;
		validaCorrelazioneApplicativaRichiesta(idRisorsa,pd.getCorrelazioneApplicativa(),true);
		validaCorrelazioneApplicativaRisposta(idRisorsa,pd.getCorrelazioneApplicativaRisposta(),true);

		// XSD: ricevuta-asincrona-simmetrica: abilitato, disabilitato
		StatoFunzionalita ricAsiSim = pd.getRicevutaAsincronaSimmetrica();
		if ((ricAsiSim != null) && !ricAsiSim.equals(CostantiConfigurazione.ABILITATO) && !ricAsiSim.equals(CostantiConfigurazione.DISABILITATO))
			this.errori.add("La ricevuta asincrona simmetrica della porta delegata "+idPortaDelegata+" deve assumere i valori: "+
					CostantiConfigurazione.ABILITATO+" o "+CostantiConfigurazione.DISABILITATO);

		// XSD: ricevuta-asincrona-asimmetrica: abilitato, disabilitato
		StatoFunzionalita ricAsiAsim = pd.getRicevutaAsincronaAsimmetrica();
		if ((ricAsiAsim != null) && !ricAsiAsim.equals(CostantiConfigurazione.ABILITATO) && !ricAsiAsim.equals(CostantiConfigurazione.DISABILITATO))
			this.errori.add("La ricevuta asincrona asimmetrica della porta delegata "+idPortaDelegata+" deve assumere i valori: "+
					CostantiConfigurazione.ABILITATO+" o "+CostantiConfigurazione.DISABILITATO);

		// Integrazione
		String integrazione = pd.getIntegrazione();
		if(integrazione!=null){
			String [] tipiIntegrazione = integrazione.split(",");
			if(tipiIntegrazione==null || tipiIntegrazione.length<=0){
				this.errori.add("La porta delegata "+idPortaDelegata+" possiede un valore nel campo 'integrazione' non valido: "+integrazione);
			}
			for(int p=0;p<tipiIntegrazione.length;p++){
				String tipoI = tipiIntegrazione[p].trim();
				if(this.tipoIntegrazionePD.contains(tipoI)==false){
					this.errori.add("La porta delegata "+idPortaDelegata+" possiede un tipo di integrazione ["+tipoI+"] non valido: valori utilizzabili sono: "+this.getTipoIntegrazionePD());
				}
			}
		}
		
		// XSD: allega-body: abilitato, disabilitato
		StatoFunzionalita allegaBody = pd.getAllegaBody();
		if ((allegaBody != null) && !allegaBody.equals(CostantiConfigurazione.ABILITATO) && !allegaBody.equals(CostantiConfigurazione.DISABILITATO))
			this.errori.add("La funzionalita' di allega-boxy della porta delegata "+idPortaDelegata+" deve assumere i valori: "+
					CostantiConfigurazione.ABILITATO+" o "+CostantiConfigurazione.DISABILITATO);

		// XSD: scarta-body: abilitato, disabilitato
		StatoFunzionalita scartaBody = pd.getScartaBody();
		if ((scartaBody != null) && !scartaBody.equals(CostantiConfigurazione.ABILITATO) && !scartaBody.equals(CostantiConfigurazione.DISABILITATO))
			this.errori.add("La funzionalita' di scarta-boxy della porta delegata "+idPortaDelegata+" deve assumere i valori: "+
					CostantiConfigurazione.ABILITATO+" o "+CostantiConfigurazione.DISABILITATO);

		// XSD: gestione-manifest: abilitato, disabilitato
		StatoFunzionalita gestMan = pd.getGestioneManifest();
		if ((gestMan != null) && !gestMan.equals(CostantiConfigurazione.ABILITATO) && !gestMan.equals(CostantiConfigurazione.DISABILITATO))
			this.errori.add("La funzionalita' di gestioneManifest della porta delegata "+idPortaDelegata+" deve assumere i valori: "+
					CostantiConfigurazione.ABILITATO+" o "+CostantiConfigurazione.DISABILITATO);
		
		// XSD: stateless: abilitato, disabilitato
		StatoFunzionalita stateless = pd.getStateless();
		if ((stateless != null) && !stateless.equals(CostantiConfigurazione.ABILITATO) && !stateless.equals(CostantiConfigurazione.DISABILITATO))
			this.errori.add("La funzionalita' 'stateless' della porta delegata "+idPortaDelegata+" deve assumere i valori: "+
					CostantiConfigurazione.ABILITATO+" o "+CostantiConfigurazione.DISABILITATO);
		
		// XSD: local-forward: abilitato, disabilitato
		StatoFunzionalita localForward = null;
		if(pd.getLocalForward()!=null){
			localForward = pd.getLocalForward().getStato();
		}
		if ((localForward != null) && !localForward.equals(CostantiConfigurazione.ABILITATO) && !localForward.equals(CostantiConfigurazione.DISABILITATO))
			this.errori.add("La funzionalita' 'local-forward' della porta delegata "+idPortaDelegata+" deve assumere i valori: "+
					CostantiConfigurazione.ABILITATO+" o "+CostantiConfigurazione.DISABILITATO);
		
		// ruoli
		if(pd.getRuoli()!=null){
			for (int i = 0; i < pd.getRuoli().sizeRuoloList(); i++) {
				String nomeRuolo = pd.getRuoli().getRuolo(i).getNome();
				try{
					if (!RegularExpressionEngine.isMatch(nomeRuolo,"^[0-9A-Za-z_]+$")) {
						this.errori.add("Il ruolo ["+nomeRuolo+"] della porta delegata "+idPortaDelegata+" dev'essere formato solo da caratteri, cifre e '_'");
					}
				}catch(Exception e){
					throw new DriverConfigurazioneException("Errore durante l'analisi tramite espressione regolare del nome del ruolo "+nomeRuolo+" della porta delegata "+idPortaDelegata+" :" +e.getMessage(),e);
				}
			}
			// Ogni ruolo deve avere un nome diverso!
			for (int i = 0; i < pd.getRuoli().sizeRuoloList(); i++) {
				int numRuolo = 0;
				String tmpRuolo = pd.getRuoli().getRuolo(i).getNome();
				for (int j = 0; j < pd.getRuoli().sizeRuoloList(); j++) {
					String checkRuolo = pd.getRuoli().getRuolo(j).getNome();
					if (checkRuolo.equals(tmpRuolo))
						numRuolo++;	
				}
				if (numRuolo > 1)
					this.errori.add("Non può esistere più di un ruolo con nome "+tmpRuolo+". Trovate "+numRuolo+" occorrenze nella porta delegata "+idPortaDelegata);
			}
		}
	}

	private  void validaPortaApplicativa(PortaApplicativa pa, Soggetto sogg) throws DriverConfigurazioneException {
		
		if(pa.getNome()==null){
			this.errori.add("Il soggetto ["+sogg.getTipo()+"/"+sogg.getNome()+"] posside una porta applicativa che non contiene la definizione del nome");
			return;
		}
		
		String nomePorta = pa.getNome();
		String idPortaApplicativa = "("+sogg.getTipo()+"/"+sogg.getNome()+") "+nomePorta;
		
		// La porta applicativa viene identificata dal nome.
		// Una volta identificata la sua stringa di identificazione DEVE essere univoca tra tutte le porte applicative di TUTTA la configurazione 
		// (e non solo tra quelle del soggetto proprietario)
		int numPD = 0;
		List<String> paTrovate = new ArrayList<String>();
		for(int j=0; j<this.configurazione.sizeSoggettoList();j++){
			Soggetto s = this.configurazione.getSoggetto(j);
			for(int k =0; k<s.sizePortaApplicativaList(); k++){
				PortaApplicativa tmpPa = s.getPortaApplicativa(k);
				String tmpNomePorta = tmpPa.getNome();
				if (nomePorta.equals(tmpNomePorta)){
					numPD++;
					paTrovate.add("("+s.getTipo()+"/"+s.getNome()+") "+tmpPa.getNome());
				}
			}
		}
		if (numPD > 1)
			this.errori.add("Non può esistere più di una porta applicativa con nome "+nomePorta+". Sono state identificate le seguenti porte applicative: "+paTrovate);

		
		
		
		// Il vincolo è stato eliminato nella versione 3.0
		@SuppressWarnings("unused")
		boolean checkIDPA = true; // Info per sapere se abbiamo abbastanza informazioni per costruire l'id
		
		// SoggettoVirtuale
		if(pa.getSoggettoVirtuale()!=null){
			
			PortaApplicativaSoggettoVirtuale sv = pa.getSoggettoVirtuale();
			
			if(sv.getTipo()==null){
				this.errori.add("La porta applicativa ["+idPortaApplicativa+"] possiede la definizione di un soggetto virtuale per cui non e' stato definito il tipo");
				checkIDPA = false;
			}
			else if(this.tipoSoggetti.contains(sv.getTipo())==false){
				this.errori.add("La porta applicativa ["+idPortaApplicativa+"] possiede la definizione di un soggetto virtuale per cui e' stato definito un tipo non valido (Tipi utilizzabili: "+this.getTipoSoggetti()+")");
			}
			
			if(sv.getNome()==null){
				this.errori.add("La porta applicativa ["+idPortaApplicativa+"] possiede la definizione di un soggetto virtuale per cui non e' stato definito il nome");
				checkIDPA = false;
			}
			
		}
		
		// Servizio
		if(pa.getServizio()!=null){
			
			PortaApplicativaServizio serv = pa.getServizio();
			
			if(serv.getTipo()==null){
				this.errori.add("La porta applicativa ["+idPortaApplicativa+"] possiede la definizione di un servizio erogato per cui non e' stato definito il tipo");
				checkIDPA = false;
			}
			else if(this.tipoServiziSoap.contains(serv.getTipo())==false && this.tipoServiziRest.contains(serv.getTipo())==false){
				this.errori.add("La porta applicativa ["+idPortaApplicativa+"] possiede la definizione di un servizio erogato per cui e' stato definito un tipo non valido (Tipi utilizzabili: "+this.getTipoServizi()+")");
			}
			
			if(serv.getNome()==null){
				this.errori.add("La porta applicativa ["+idPortaApplicativa+"] possiede la definizione di un servizio erogato per cui non e' stato definito il nome");
				checkIDPA = false;
			}
			
		}else{
			
			this.errori.add("La porta applicativa ["+idPortaApplicativa+"] non possiede la definizione del servizio erogato");
			
		}
		
		// Azione
		if(pa.getAzione()!=null){
			
			PortaApplicativaAzione pda = pa.getAzione();
			
			// XSD: identificazione: static, urlBased, contentBased, inputBased e soapActionBased
			PortaApplicativaAzioneIdentificazione identificazione = pda.getIdentificazione();
			if ((identificazione != null) && !identificazione.equals(CostantiConfigurazione.PORTA_APPLICATIVA_AZIONE_STATIC) && 
					!identificazione.equals(CostantiConfigurazione.PORTA_APPLICATIVA_AZIONE_URL_BASED) && 
					!identificazione.equals(CostantiConfigurazione.PORTA_APPLICATIVA_AZIONE_CONTENT_BASED) &&
					!identificazione.equals(CostantiConfigurazione.PORTA_APPLICATIVA_AZIONE_HEADER_BASED) && 
					!identificazione.equals(CostantiConfigurazione.PORTA_APPLICATIVA_AZIONE_PROTOCOL_BASED) && 
					!identificazione.equals(CostantiConfigurazione.PORTA_APPLICATIVA_AZIONE_INPUT_BASED) && 
					!identificazione.equals(CostantiConfigurazione.PORTA_APPLICATIVA_AZIONE_SOAP_ACTION_BASED) &&
					!identificazione.equals(CostantiConfigurazione.PORTA_APPLICATIVA_AZIONE_WSDL_BASED) &&
					!identificazione.equals(CostantiConfigurazione.PORTA_APPLICATIVA_AZIONE_DELEGATED_BY)){
				this.errori.add("La modalita d'identificazione dell'azione della porta applicativa "+idPortaApplicativa+" deve assumere uno dei seguente valori: "+
						CostantiConfigurazione.PORTA_APPLICATIVA_AZIONE_STATIC+", "+
						CostantiConfigurazione.PORTA_APPLICATIVA_AZIONE_URL_BASED+", "+
						CostantiConfigurazione.PORTA_APPLICATIVA_AZIONE_CONTENT_BASED+", "+
						CostantiConfigurazione.PORTA_APPLICATIVA_AZIONE_HEADER_BASED+" o "+
						CostantiConfigurazione.PORTA_APPLICATIVA_AZIONE_PROTOCOL_BASED+" o "+
						CostantiConfigurazione.PORTA_APPLICATIVA_AZIONE_INPUT_BASED+" o "+
						CostantiConfigurazione.PORTA_APPLICATIVA_AZIONE_SOAP_ACTION_BASED+" o "+
						CostantiConfigurazione.PORTA_APPLICATIVA_AZIONE_WSDL_BASED+" o "+
						CostantiConfigurazione.PORTA_APPLICATIVA_AZIONE_DELEGATED_BY);
			}
			if(identificazione==null){
				identificazione = CostantiConfigurazione.PORTA_APPLICATIVA_AZIONE_STATIC;
			}
			
			if(CostantiConfigurazione.PORTA_APPLICATIVA_AZIONE_STATIC.equals(identificazione)){
				if (pda.getNome() == null){
					this.errori.add("L'azione della porta applicativa "+idPortaApplicativa+" non contiene la definizione del nome, nonostante la modalita' di identificazione sia "+identificazione);
					checkIDPA = false;
				}
			}
			else if(CostantiConfigurazione.PORTA_APPLICATIVA_AZIONE_URL_BASED.equals(identificazione) || 
					CostantiConfigurazione.PORTA_APPLICATIVA_AZIONE_CONTENT_BASED.equals(identificazione) || 
					CostantiConfigurazione.PORTA_APPLICATIVA_AZIONE_HEADER_BASED.equals(identificazione)){
				if (pda.getPattern() == null){
					this.errori.add("L'azione della porta applicativa "+idPortaApplicativa+" non contiene la definizione del pattern, nonostante la modalita' di identificazione sia "+identificazione);
					checkIDPA = false;
				}
			}	
			else if(CostantiConfigurazione.PORTA_DELEGATA_AZIONE_DELEGATED_BY.equals(identificazione)){
				if (pda.getNomePortaDelegante() == null) {
					this.errori.add("L'azione della porta delegata "+idPortaApplicativa+" non contiene la definizione del nome della porta delegante, nonostante la modalita' di identificazione sia "+identificazione);
					checkIDPA = false;
				}
			}
			
		}
		
		// Servizi Applicativi
		if(pa.sizeServizioApplicativoList()<1){
			this.errori.add("Alla porta applicativa ["+idPortaApplicativa+"] non sono stati associati servizi applicativi");
		}
		else{
			for(int i=0; i<pa.sizeServizioApplicativoList(); i++){
				// Servizio Applicativo puo' avere solo meta informazioni
				PortaApplicativaServizioApplicativo paSa = pa.getServizioApplicativo(i);
				ServizioApplicativo sa = null;
				if(paSa.getNome()==null){
					this.errori.add("La porta applicativa "+idPortaApplicativa+" ha associato un servizio applicativo per cui non e' stato definito il nome");
					continue;
				}
				else{
					// servizio applicativo definito nella root del soggetto
					if(existsServizioApplicativo(paSa.getNome(), sogg)==false){
						this.errori.add("La porta applicativa "+idPortaApplicativa+" ha associato un riferimento ad un servizio applicativo ["+paSa.getNome()+"] che non risulta registrato nel soggetto "+sogg.getTipo()+"/"+sogg.getNome());
						continue;
					}
					else{
						// Prelevo servizio applicativo per conformita con l'utilizzo nella porta applicativa
						sa = this.getServizioApplicativo(paSa.getNome(), sogg);
					}
				}
				
				// Validazione conformita' con porta applicativa
				if(sa.getInvocazioneServizio()==null){
					this.errori.add("La porta applicativa "+idPortaApplicativa+" ha associato un servizio applicativo ["+sa.getNome()+"] per cui non e' stato definito l'elemento invocazione servizio");
				}
				else{
					boolean connettore = false;
					boolean get = false;
					if(sa.getInvocazioneServizio().getConnettore()!=null && !CostantiConfigurazione.DISABILITATO.equals(sa.getInvocazioneServizio().getConnettore().getTipo())){
						connettore = true;
					}
					if(sa.getInvocazioneServizio().getGetMessage()!=null && CostantiConfigurazione.ABILITATO.equals(sa.getInvocazioneServizio().getGetMessage())){
						get = true;
					}
					if(!connettore && !get){
						this.errori.add("La porta applicativa "+idPortaApplicativa+" ha associato un servizio applicativo ["+sa.getNome()+"] per cui non e' stato definito ne una consegna trasparente (tramite connettore) ne il servizio di message box (tramite IntegrationManager)");
					}
				}
			}
		}

		// Check processo di autenticazione della porta delegata
		String autenticazione = CredenzialeTipo.SSL.toString();
		if(pa.getAutenticazione()!=null){
			autenticazione = pa.getAutenticazione();
			if(this.tipoAutenticazionePortaApplicativa.contains(pa.getAutenticazione())==false){
				this.errori.add("Alla porta applicativa "+idPortaApplicativa+" e' stato associato un processo di autenticazione ["+pa.getAutenticazione()+"] non valido; tipi conosciuti: "+this.getTipoAutenticazionePortaApplicativa()); 
			}
		}
		
		// Autorizzazione
		String autorizzazione = CostantiConfigurazione.AUTORIZZAZIONE_AUTHENTICATED;
		if(pa.getAutorizzazione()!=null){
			autorizzazione = pa.getAutorizzazione();
			if(this.tipoAutorizzazionePortaApplicativa.contains(pa.getAutorizzazione())==false){
				this.errori.add("Alla porta applicativa "+idPortaApplicativa+" e' stato associato un processo di autorizzazione ["+pa.getAutorizzazione()+"] non valido; tipi conosciuti: "+this.getTipoAutorizzazionePortaApplicativa()); 
			}
		}
		
		if(autorizzazione!=null){
		
			// Questi vincoli non valgono in spcoop e nello sdi
			if("gw".equals(sogg.getTipo())){
				if(autorizzazione.toLowerCase().contains(CostantiConfigurazione.AUTORIZZAZIONE_AUTHENTICATED) &&
						autorizzazione.toLowerCase().contains(CostantiConfigurazione.AUTORIZZAZIONE_ROLES)){
					// Se e' definito un processo di autorizzazione che prevede sia autenticazione che ruoli l'autenticazione deve essere opzionale
					if(StatoFunzionalita.ABILITATO.equals(pa.getAutenticazioneOpzionale())==false){
						this.errori.add("Alla porta applicativa "+idPortaApplicativa+" e' stato associata un'autorizzazione '"+autorizzazione+"' che richiede una autenticazione dei soggetti opzionali"); 
					}
				}
				if(autorizzazione.toLowerCase().contains("internal") || 
						(CostantiConfigurazione.AUTORIZZAZIONE_AUTHENTICATED.equals(autorizzazione))){
					// Se l'autorizzazione prevede una gestione dei ruoli interni, oppure è solamente basata sull'autenticazione dei chiamanti, 
					// una autenticazione DEVE essere presente e non deve essere opzionale
					if(CostantiConfigurazione.AUTENTICAZIONE_NONE.equals(autenticazione)){
						this.errori.add("Alla porta applicativa "+idPortaApplicativa+" e' stato associata un'autorizzazione '"+autorizzazione+"' che richiede una autenticazione obbligatoria dei soggetti, autenticazione non impostata nella porta applicativa"); 
					}
					if(StatoFunzionalita.ABILITATO.equals(pa.getAutenticazioneOpzionale())){
						if(autorizzazione.toLowerCase().contains("or")==false){
							this.errori.add("Alla porta applicativa "+idPortaApplicativa+" e' stato associata un'autorizzazione '"+autorizzazione+"' che richiede una autenticazione obbligatoria dei soggetti, autenticazione impostata come opzionale nella porta applicativa");
						}
					}
				}
			}
			
		}

		// Vincoli troppo forti nel caso di configurazione su console, la PdD non parte se la configurazione non e' stata ultimata.
//		if(autorizzazione!=null && autorizzazione.toLowerCase().contains(CostantiConfigurazione.AUTORIZZAZIONE_ROLES.toLowerCase())){
//			if (pa.getRuoli()==null || pa.getRuoli().sizeRuoloList() == 0){
//				this.errori.add("La porta applicativa "+idPortaApplicativa+" deve avere almeno un ruolo associato poiche' lo richiede il tipo di autorizzazione indicato: "+autorizzazione);
//			}
//		}
		
		// Autorizzazione Contenuto
		if(pa.getAutorizzazioneContenuto()!=null){
			if(this.tipoAutorizzazioneContenutoPortaApplicativa.contains(pa.getAutorizzazioneContenuto())==false){
				this.errori.add("Alla porta applicativa "+idPortaApplicativa+" e' stato associato un processo di autorizzazione contenuto ["+pa.getAutorizzazioneContenuto()+"] non valido; tipi conosciuti: "+this.getTipoAutorizzazioneContenutoPortaApplicativa()); 
			}
		}
		
		// Valida SetProtocolProperties
		for (int j=0; j<pa.sizeProprietaList();j++) {
			Proprieta ssp = pa.getProprieta(j);
			validaProtocolProperty(ssp, "PortaApplicativa_"+idPortaApplicativa);
		}

		// Valida MessageSecurity
		MessageSecurity messageSecurity = pa.getMessageSecurity();
		if (messageSecurity != null)
			validaMessageSecurity(messageSecurity,"PortaApplicativa_"+idPortaApplicativa);

		// Valida MTOM
		MtomProcessor mtom = pa.getMtomProcessor();
		if (mtom != null)
			validaMTOM(mtom,"PortaApplicativa_"+idPortaApplicativa);

		// Valida ValidazioneContenutiApplicativi
		ValidazioneContenutiApplicativi vca = pa.getValidazioneContenutiApplicativi();
		if (vca != null)
			validazioneValidazioneContenutiApplicativi(vca, "PortaApplicativa_"+idPortaApplicativa);

		// CorrelazioneApplicativa:
		String idRisorsa = "Porta applicativa "+idPortaApplicativa;
		validaCorrelazioneApplicativaRichiesta(idRisorsa,pa.getCorrelazioneApplicativa(),false);
		validaCorrelazioneApplicativaRisposta(idRisorsa,pa.getCorrelazioneApplicativaRisposta(),false);
				
		// Il nome della porta applicativa (obbligatorio) deve essere univoco tra i nomi delle porte applicative del soggetto.
		int numPA = 0;
		for (int j=0; j<sogg.sizePortaApplicativaList(); j++) {
			PortaApplicativa tmpPa = sogg.getPortaApplicativa(j);
			if (pa.getNome().equals(tmpPa.getNome()))
				numPA++;
		}
		if (numPA > 1)
			this.errori.add("Non può esistere più di una porta applicativa del soggetto "+sogg.getTipo()+"/"+sogg.getNome()+" con nome "+pa.getNome());
	
		//Check univocita' anche per valori soggetto (virtuale), servizio e azione
		// Il vincolo è stato eliminato nella versione 3.0
//		String idPortaA = null;
//		if(checkIDPA == false){
//			
//			if(pa.getSoggettoVirtuale()!=null){
//				idPortaA = pa.getSoggettoVirtuale().getTipo()+"/"+pa.getSoggettoVirtuale().getNome();
//			}
//			else{
//				idPortaA = sogg.getTipo()+"/"+sogg.getNome();
//			}
//			idPortaA = idPortaA +"_" + pa.getServizio().getTipo()+"/"+pa.getServizio().getNome(); 
//			if(pa.getAzione()!=null){
//				idPortaA = idPortaA +"_" +pa.getAzione().getNome();
//			}
//			
//			int cont = 0;
//			for(int i=0; i<sogg.sizePortaApplicativaList(); i++){
//				PortaApplicativa tmp = sogg.getPortaApplicativa(i);
//				String idTMPPA = null;
//				if(tmp.getSoggettoVirtuale()!=null){
//					if(tmp.getSoggettoVirtuale().getTipo()==null)
//						continue; // PA malformata, verra segnalata
//					if(tmp.getSoggettoVirtuale().getNome()==null)
//						continue; // PA malformata, verra segnalata
//					idTMPPA = tmp.getSoggettoVirtuale().getTipo()+"/"+tmp.getSoggettoVirtuale().getNome();
//				}
//				else{
//					idTMPPA = sogg.getTipo()+"/"+sogg.getNome();
//				}
//				if(tmp.getServizio()==null)
//					continue; // PA malformata, verra segnalata
//				if(tmp.getServizio().getTipo()==null)
//					continue; // PA malformata, verra segnalata
//				if(tmp.getServizio().getNome()==null)
//					continue; // PA malformata, verra segnalata
//				idTMPPA = idTMPPA +"_" + tmp.getServizio().getTipo()+"/"+tmp.getServizio().getNome(); 
//				if(tmp.getAzione()!=null){
//					if(tmp.getAzione().getNome()==null){
//						continue; // PA malformata, verra segnalata
//					}
//					idTMPPA = idTMPPA +"_" +pa.getAzione().getNome();
//				}
//				if(idPortaA.equals(idTMPPA)){
//					cont++;
//				}
//			}
//			
//			if(cont>1){
//				if(pa.getSoggettoVirtuale()!=null)
//					this.errori.add("Non può esistere più di una porta applicativa (con soggetto virtuale) che mappa il servizio: "+idPortaA);
//				else
//					this.errori.add("Non può esistere più di una porta applicativa che mappa il servizio: "+idPortaA);
//			}
//		}
		
		
		// XSD: ricevuta-asincrona-simmetrica: abilitato, disabilitato
		StatoFunzionalita ricAsiSim = pa.getRicevutaAsincronaSimmetrica();
		if ((ricAsiSim != null) && !ricAsiSim.equals(CostantiConfigurazione.ABILITATO) && !ricAsiSim.equals(CostantiConfigurazione.DISABILITATO))
			this.errori.add("La ricevuta asincrona simmetrica della porta applicativa "+idPortaApplicativa+" deve assumere i valori: "+
					CostantiConfigurazione.ABILITATO+" o "+CostantiConfigurazione.DISABILITATO);

		// XSD: ricevuta-asincrona-asimmetrica: abilitato, disabilitato
		StatoFunzionalita ricAsiAsim = pa.getRicevutaAsincronaAsimmetrica();
		if ((ricAsiAsim != null) && !ricAsiAsim.equals(CostantiConfigurazione.ABILITATO) && !ricAsiAsim.equals(CostantiConfigurazione.DISABILITATO))
			this.errori.add("La ricevuta asincrona asimmetrica della porta applicativa "+idPortaApplicativa+" deve assumere i valori: "+
					CostantiConfigurazione.ABILITATO+" o "+CostantiConfigurazione.DISABILITATO);

		// Integrazione
		String integrazione = pa.getIntegrazione();
		if(integrazione!=null){
			String [] tipiIntegrazione = integrazione.split(",");
			if(tipiIntegrazione==null || tipiIntegrazione.length<=0){
				this.errori.add("La porta applicativa "+idPortaApplicativa+" possiede un valore nel campo 'integrazione' non valido: "+integrazione);
			}
			for(int p=0;p<tipiIntegrazione.length;p++){
				String tipoI = tipiIntegrazione[p].trim();
				if(this.tipoIntegrazionePA.contains(tipoI)==false){
					this.errori.add("La porta applicativa "+idPortaApplicativa+" possiede un tipo di integrazione ["+tipoI+"] non valido: valori utilizzabili sono: "+this.getTipoIntegrazionePA());
				}
			}
		}
		
		// XSD: allega-body: abilitato, disabilitato
		StatoFunzionalita allegaBody = pa.getAllegaBody();
		if ((allegaBody != null) && !allegaBody.equals(CostantiConfigurazione.ABILITATO) && !allegaBody.equals(CostantiConfigurazione.DISABILITATO))
			this.errori.add("La funzionalita' di allega-boxy della porta applicativa "+idPortaApplicativa+" deve assumere i valori: "+
					CostantiConfigurazione.ABILITATO+" o "+CostantiConfigurazione.DISABILITATO);

		// XSD: scarta-body: abilitato, disabilitato
		StatoFunzionalita scartaBody = pa.getScartaBody();
		if ((scartaBody != null) && !scartaBody.equals(CostantiConfigurazione.ABILITATO) && !scartaBody.equals(CostantiConfigurazione.DISABILITATO))
			this.errori.add("La funzionalita' di scarta-boxy della porta applicativa "+idPortaApplicativa+" deve assumere i valori: "+
					CostantiConfigurazione.ABILITATO+" o "+CostantiConfigurazione.DISABILITATO);

		// XSD: gestione-manifest: abilitato, disabilitato
		StatoFunzionalita gestMan = pa.getGestioneManifest();
		if ((gestMan != null) && !gestMan.equals(CostantiConfigurazione.ABILITATO) && !gestMan.equals(CostantiConfigurazione.DISABILITATO))
			this.errori.add("La funzionalita' di gestioneManifest della porta applicativa "+idPortaApplicativa+" deve assumere i valori: "+
					CostantiConfigurazione.ABILITATO+" o "+CostantiConfigurazione.DISABILITATO);
		
		// XSD: stateless: abilitato, disabilitato
		StatoFunzionalita stateless = pa.getStateless();
		if ((stateless != null) && !stateless.equals(CostantiConfigurazione.ABILITATO) && !stateless.equals(CostantiConfigurazione.DISABILITATO))
			this.errori.add("La funzionalita' 'stateless' della porta applicativa "+idPortaApplicativa+" deve assumere i valori: "+
					CostantiConfigurazione.ABILITATO+" o "+CostantiConfigurazione.DISABILITATO);
		
		// ruoli
		if(pa.getRuoli()!=null){
			for (int i = 0; i < pa.getRuoli().sizeRuoloList(); i++) {
				String nomeRuolo = pa.getRuoli().getRuolo(i).getNome();
				try{
					if (!RegularExpressionEngine.isMatch(nomeRuolo,"^[0-9A-Za-z_]+$")) {
						this.errori.add("Il ruolo ["+nomeRuolo+"] della porta applicativa "+idPortaApplicativa+" dev'essere formato solo da caratteri, cifre e '_'");
					}
				}catch(Exception e){
					throw new DriverConfigurazioneException("Errore durante l'analisi tramite espressione regolare del nome del ruolo "+nomeRuolo+" della porta applicativa "+idPortaApplicativa+" :" +e.getMessage(),e);
				}
			}
			// Ogni ruolo deve avere un nome diverso!
			for (int i = 0; i < pa.getRuoli().sizeRuoloList(); i++) {
				int numRuolo = 0;
				String tmpRuolo = pa.getRuoli().getRuolo(i).getNome();
				for (int j = 0; j < pa.getRuoli().sizeRuoloList(); j++) {
					String checkRuolo = pa.getRuoli().getRuolo(j).getNome();
					if (checkRuolo.equals(tmpRuolo))
						numRuolo++;	
				}
				if (numRuolo > 1)
					this.errori.add("Non può esistere più di un ruolo con nome "+tmpRuolo+". Trovate "+numRuolo+" occorrenze nella porta applicativa "+idPortaApplicativa);
			}
		}
	}

	private void validaServizioApplicativo(ServizioApplicativo sa, Soggetto sogg) throws DriverConfigurazioneException {
		// NOTA: Se checkForAutorizzazione = true, il servizio DEVE possedere l'elemento invocazione porta con ALMENO una credenziale

		if(sa.getNome()==null){
			this.errori.add("Esiste un ServizioApplicativo del soggetto ["+sogg.getTipo()+"/"+sogg.getNome()+"] per cui non e' stato definito il nome");
			return;
		}
		
		String idServizioApplicativo = sogg.getTipo()+"/"+sogg.getNome()+"_"+sa.getNome();
		
		if(sa.getInvocazionePorta()==null && sa.getRispostaAsincrona()==null  && sa.getInvocazioneServizio()==null){
			this.errori.add("Il ServizioApplicativo ["+idServizioApplicativo+"] non contiene ne la definizione dell'elemento invocazione porta, ne la definizione della risposta asincrona, ne la definizione dell'elemento invocazione servizio");
			return;
		}
		
		// InvocazionePorta
		InvocazionePorta ip = sa.getInvocazionePorta();
		if (ip != null) {
			
			// Valida Credenziali
			if(ip.sizeCredenzialiList()<1){
				this.errori.add("Il ServizioApplicativo ["+idServizioApplicativo+"] non contiene credenziali di accesso, nonostante abbia definito l'elemento invocazione porta");
			}
			for (int j=0; j<ip.sizeCredenzialiList(); j++) {
				Credenziali c = ip.getCredenziali(j);
				this.validaCredenziale(c, "servizio applicativo ["+idServizioApplicativo+"] (invocazione porta)");
			}
			
			// XSD: invio-per-riferimento: abilitato, disabilitato
			StatoFunzionalita invRif = ip.getInvioPerRiferimento();
			if ((invRif != null) && !invRif.equals(CostantiConfigurazione.ABILITATO) && !invRif.equals(CostantiConfigurazione.DISABILITATO))
				this.errori.add("L'invio per riferimento dell'invocazione porta del servizio applicativo ["+idServizioApplicativo+"] deve assumere i valori: "+
						CostantiConfigurazione.ABILITATO+" o "+CostantiConfigurazione.DISABILITATO);
			
			// XSD: sbustamento-informazioni-protocollo: abilitato, disabilitato
			StatoFunzionalita sbustamentoInfoProtocollo = ip.getSbustamentoInformazioniProtocollo();
			if ((sbustamentoInfoProtocollo != null) && !sbustamentoInfoProtocollo.equals(CostantiConfigurazione.ABILITATO) && !sbustamentoInfoProtocollo.equals(CostantiConfigurazione.DISABILITATO))
				this.errori.add("L'indicazione di sbustamento informazioni di protcollo dell'invocazione porta del servizio applicativo ["+idServizioApplicativo+"] deve assumere i valori: "+
						CostantiConfigurazione.ABILITATO+" o "+CostantiConfigurazione.DISABILITATO);
			
			InvocazionePortaGestioneErrore ipge = ip.getGestioneErrore();
			if (ipge != null) {
				
				// XSD: fault: soap, xml
				FaultIntegrazioneTipo fault = ipge.getFault();
				if ((fault != null) && !fault.equals(FaultIntegrazioneTipo.SOAP) && !fault.equals(FaultIntegrazioneTipo.XML))
					this.errori.add("Il fault della gestione errore dell'invocazione porta del servizio applicativo ["+idServizioApplicativo+"] deve assumere i valori: "+
							CostantiConfigurazione.ERRORE_APPLICATIVO_SOAP+" o "+CostantiConfigurazione.ERRORE_APPLICATIVO_XML);

				// XSD: generic-fault-code: abilitato, disabilitato
				StatoFunzionalita genFault = ipge.getGenericFaultCode();
				if ((genFault != null) && !genFault.equals(CostantiConfigurazione.ABILITATO) && !genFault.equals(CostantiConfigurazione.DISABILITATO))
					this.errori.add("Il generic-fault-code della gestione errore dell'invocazione porta del servizio applicativo ["+idServizioApplicativo+"] deve assumere i valori: "+
							CostantiConfigurazione.ABILITATO+" o "+CostantiConfigurazione.DISABILITATO);
			}

			// ruoli
			if(ip.getRuoli()!=null){
				for (int i = 0; i < ip.getRuoli().sizeRuoloList(); i++) {
					String nomeRuolo = ip.getRuoli().getRuolo(i).getNome();
					try{
						if (!RegularExpressionEngine.isMatch(nomeRuolo,"^[0-9A-Za-z_]+$")) {
							this.errori.add("Il ruolo ["+nomeRuolo+"] dell'invocazione porta del servizio applicativo ["+idServizioApplicativo+"] dev'essere formato solo da caratteri, cifre e '_'");
						}
					}catch(Exception e){
						throw new DriverConfigurazioneException("Errore durante l'analisi tramite espressione regolare del nome del ruolo "+nomeRuolo+" dell'invocazione porta del servizio applicativo ["+idServizioApplicativo+"] :" +e.getMessage(),e);
					}
				}
				// Ogni ruolo deve avere un nome diverso!
				for (int i = 0; i < ip.getRuoli().sizeRuoloList(); i++) {
					int numRuolo = 0;
					String tmpRuolo = ip.getRuoli().getRuolo(i).getNome();
					for (int j = 0; j < ip.getRuoli().sizeRuoloList(); j++) {
						String checkRuolo = ip.getRuoli().getRuolo(j).getNome();
						if (checkRuolo.equals(tmpRuolo))
							numRuolo++;	
					}
					if (numRuolo > 1)
						this.errori.add("Non può esistere più di un ruolo con nome "+tmpRuolo+". Trovate "+numRuolo+" occorrenze nell'invocazione porta del servizio applicativo ["+idServizioApplicativo+"]");
				}
			}
		}

		// InvocazioneServizio
		if (sa.getInvocazioneServizio() != null) {
			InvocazioneServizio is = sa.getInvocazioneServizio();
			
			// Valida Credenziali
			InvocazioneCredenziali c = is.getCredenziali();
			if (c != null)
				this.validaCredenziale(c, "servizio applicativo ["+idServizioApplicativo+"] (invocazione servizio)");
				
			// Autenticazione
			// XSD: autenticazione: basic, ssl, none
			InvocazioneServizioTipoAutenticazione auth = is.getAutenticazione();
			if ((auth != null) && !auth.equals(CostantiConfigurazione.INVOCAZIONE_SERVIZIO_AUTENTICAZIONE_BASIC) && 
					!auth.equals(CostantiConfigurazione.INVOCAZIONE_SERVIZIO_AUTENTICAZIONE_NONE))
				this.errori.add("L'autenticazione dell'invocazione servizio del servizio applicativo "+idServizioApplicativo+" deve assumere uno dei seguenti valori: "+
						CostantiConfigurazione.INVOCAZIONE_SERVIZIO_AUTENTICAZIONE_BASIC.toString()+" o "+CostantiConfigurazione.INVOCAZIONE_SERVIZIO_AUTENTICAZIONE_NONE.toString());
			if(CostantiConfigurazione.INVOCAZIONE_SERVIZIO_AUTENTICAZIONE_BASIC.equals(auth)){
				if(c==null){
					this.errori.add("L'autenticazione "+CostantiConfigurazione.INVOCAZIONE_SERVIZIO_AUTENTICAZIONE_BASIC.toString()+" dell'invocazione servizio del servizio applicativo "+idServizioApplicativo+" richiede la presenza di credenziali");
				}
			}

			// Valida il connettore.
			Connettore conn = is.getConnettore();
			if (conn != null)
				this.validaConnettore(conn, "servizio applicativo ["+idServizioApplicativo+"] (invocazione servizio)", sogg);
				
			// Valida GestioneErrore
			GestioneErrore ge = is.getGestioneErrore();
			if (ge != null)
				this.validaGestioneErrore(ge, "servizio applicativo ["+idServizioApplicativo+"] (invocazione servizio)");

			boolean connettore = false;
			boolean get = false;
			if(is.getConnettore()!=null && !CostantiConfigurazione.DISABILITATO.equals(is.getConnettore().getTipo())){
				connettore = true;
			}
			if(is.getGetMessage()!=null && CostantiConfigurazione.ABILITATO.equals(is.getGetMessage())){
				get = true;
			}
			if(!connettore && !get){
				this.errori.add("Il servizio Applicativo "+idServizioApplicativo+" possiede un elemento invocazione servizio per cui non e' stato definito ne una consegna trasparente (tramite connettore) ne il servizio di message box (tramite IntegrationManager)");
			}
			
						
			// XSD: get-message: abilitato, disabilitato
			StatoFunzionalita getMsg = is.getGetMessage();
			if ((getMsg != null) && !getMsg.equals(CostantiConfigurazione.ABILITATO) && !getMsg.equals(CostantiConfigurazione.DISABILITATO))
				this.errori.add("Il get-message dell'invocazione servizio del servizio applicativo "+idServizioApplicativo+" deve assumere i valori: "+
						CostantiConfigurazione.ABILITATO+" o "+CostantiConfigurazione.DISABILITATO);

			// XSD: invio-per-riferimento: abilitato, disabilitato
			StatoFunzionalita invRif = is.getInvioPerRiferimento();
			if ((invRif != null) && !invRif.equals(CostantiConfigurazione.ABILITATO) && !invRif.equals(CostantiConfigurazione.DISABILITATO))
				this.errori.add("La funzionalita' di invio per riferimento dell'invocazione servizio del servizio applicativo "+idServizioApplicativo+" deve assumere i valori: "+
						CostantiConfigurazione.ABILITATO+" o "+CostantiConfigurazione.DISABILITATO);
			
			// XSD: risposta-per-riferimento: abilitato, disabilitato
			StatoFunzionalita rispRif = is.getRispostaPerRiferimento();
			if ((rispRif != null) && !rispRif.equals(CostantiConfigurazione.ABILITATO) && !rispRif.equals(CostantiConfigurazione.DISABILITATO))
				this.errori.add("La funzionalita' di risposta per riferimento dell'invocazione servizio del servizio applicativo "+idServizioApplicativo+" deve assumere i valori: "+
						CostantiConfigurazione.ABILITATO+" o "+CostantiConfigurazione.DISABILITATO);

			// XSD: sbustamento-soap: abilitato, disabilitato
			StatoFunzionalita sbustamentoSOAP = is.getSbustamentoSoap();
			if ((sbustamentoSOAP != null) && !sbustamentoSOAP.equals(CostantiConfigurazione.ABILITATO) && !sbustamentoSOAP.equals(CostantiConfigurazione.DISABILITATO))
				this.errori.add("L'indicazione di sbustamento SOAP dell'invocazione servizio del servizio applicativo ["+idServizioApplicativo+"] deve assumere i valori: "+
						CostantiConfigurazione.ABILITATO+" o "+CostantiConfigurazione.DISABILITATO);
			
			// XSD: sbustamento-informazioni-protocollo: abilitato, disabilitato
			StatoFunzionalita sbustamentoInfoProtocollo = is.getSbustamentoInformazioniProtocollo();
			if ((sbustamentoInfoProtocollo != null) && !sbustamentoInfoProtocollo.equals(CostantiConfigurazione.ABILITATO) && !sbustamentoInfoProtocollo.equals(CostantiConfigurazione.DISABILITATO))
				this.errori.add("L'indicazione di sbustamento informazioni di protcollo dell'invocazione servizio del servizio applicativo ["+idServizioApplicativo+"] deve assumere i valori: "+
						CostantiConfigurazione.ABILITATO+" o "+CostantiConfigurazione.DISABILITATO);
			
		}

		// RispostaAsincrona
		if (sa.getRispostaAsincrona() != null) {
			
			RispostaAsincrona ra = sa.getRispostaAsincrona();
			
			// Valida Credenziali
			InvocazioneCredenziali c = ra.getCredenziali();
			if (c != null)
				this.validaCredenziale(c, "servizio applicativo ["+idServizioApplicativo+"] (risposta asincrona)");
				
			// Autenticazione
			// XSD: autenticazione: basic, ssl, none
			InvocazioneServizioTipoAutenticazione auth = ra.getAutenticazione();
			if ((auth != null) && !auth.equals(CostantiConfigurazione.INVOCAZIONE_SERVIZIO_AUTENTICAZIONE_BASIC) && 
					!auth.equals(CostantiConfigurazione.INVOCAZIONE_SERVIZIO_AUTENTICAZIONE_NONE))
				this.errori.add("L'autenticazione della risposta asincrona del servizio applicativo "+idServizioApplicativo+" deve assumere uno dei seguenti valori: "+
						CostantiConfigurazione.INVOCAZIONE_SERVIZIO_AUTENTICAZIONE_BASIC.toString()+" o "+CostantiConfigurazione.INVOCAZIONE_SERVIZIO_AUTENTICAZIONE_NONE.toString());
			if(CostantiConfigurazione.INVOCAZIONE_SERVIZIO_AUTENTICAZIONE_BASIC.equals(auth)){
				if(c==null){
					this.errori.add("L'autenticazione "+CostantiConfigurazione.INVOCAZIONE_SERVIZIO_AUTENTICAZIONE_BASIC+" della risposta asincrona del servizio applicativo "+idServizioApplicativo+" richiede la presenza di credenziali");
				}
			}

			// Valida il connettore.
			Connettore conn = ra.getConnettore();
			if (conn != null)
				this.validaConnettore(conn, "servizio applicativo ["+idServizioApplicativo+"] (risposta asincrona)", sogg);
				
			// Valida GestioneErrore
			GestioneErrore ge = ra.getGestioneErrore();
			if (ge != null)
				this.validaGestioneErrore(ge, "servizio applicativo ["+idServizioApplicativo+"] (risposta asincrona)");

			boolean connettore = false;
			boolean get = false;
			if(ra.getConnettore()!=null && !CostantiConfigurazione.DISABILITATO.equals(ra.getConnettore().getTipo())){
				connettore = true;
			}
			if(ra.getGetMessage()!=null && CostantiConfigurazione.ABILITATO.equals(ra.getGetMessage())){
				get = true;
			}
			if(!connettore && !get){
				this.errori.add("Il servizio Applicativo "+idServizioApplicativo+" possiede un elemento risposta asincrona per cui non e' stato definito ne una consegna trasparente (tramite connettore) ne il servizio di message box (tramite IntegrationManager)");
			}
			
						
			// XSD: get-message: abilitato, disabilitato
			StatoFunzionalita getMsg = ra.getGetMessage();
			if ((getMsg != null) && !getMsg.equals(CostantiConfigurazione.ABILITATO) && !getMsg.equals(CostantiConfigurazione.DISABILITATO))
				this.errori.add("Il get-message della risposta asincrona del servizio applicativo "+idServizioApplicativo+" deve assumere i valori: "+
						CostantiConfigurazione.ABILITATO+" o "+CostantiConfigurazione.DISABILITATO);

			// XSD: invio-per-riferimento: abilitato, disabilitato
			StatoFunzionalita invRif = ra.getInvioPerRiferimento();
			if ((invRif != null) && !invRif.equals(CostantiConfigurazione.ABILITATO) && !invRif.equals(CostantiConfigurazione.DISABILITATO))
				this.errori.add("La funzionalita' di invio per riferimento della risposta asincrona del servizio applicativo "+idServizioApplicativo+" deve assumere i valori: "+
						CostantiConfigurazione.ABILITATO+" o "+CostantiConfigurazione.DISABILITATO);
			
			// XSD: risposta-per-riferimento: abilitato, disabilitato
			StatoFunzionalita rispRif = ra.getRispostaPerRiferimento();
			if ((rispRif != null) && !rispRif.equals(CostantiConfigurazione.ABILITATO) && !rispRif.equals(CostantiConfigurazione.DISABILITATO))
				this.errori.add("La funzionalita' di risposta per riferimento della risposta asincrona del servizio applicativo "+idServizioApplicativo+" deve assumere i valori: "+
						CostantiConfigurazione.ABILITATO+" o "+CostantiConfigurazione.DISABILITATO);

			// XSD: sbustamento-soap: abilitato, disabilitato
			StatoFunzionalita sbustamentoSOAP = ra.getSbustamentoSoap();
			if ((sbustamentoSOAP != null) && !sbustamentoSOAP.equals(CostantiConfigurazione.ABILITATO) && !sbustamentoSOAP.equals(CostantiConfigurazione.DISABILITATO))
				this.errori.add("L'indicazione di sbustamento SOAP della risposta asincrona del servizio applicativo ["+idServizioApplicativo+"] deve assumere i valori: "+
						CostantiConfigurazione.ABILITATO+" o "+CostantiConfigurazione.DISABILITATO);
			
			// XSD: sbustamento-informazioni-protocollo: abilitato, disabilitato
			StatoFunzionalita sbustamentoInfoProtocollo = ra.getSbustamentoInformazioniProtocollo();
			if ((sbustamentoInfoProtocollo != null) && !sbustamentoInfoProtocollo.equals(CostantiConfigurazione.ABILITATO) && !sbustamentoInfoProtocollo.equals(CostantiConfigurazione.DISABILITATO))
				this.errori.add("L'indicazione di sbustamento informazioni di protcollo della risposta asincrona del servizio applicativo ["+idServizioApplicativo+"] deve assumere i valori: "+
						CostantiConfigurazione.ABILITATO+" o "+CostantiConfigurazione.DISABILITATO);
			
		}

		// UNICITA: i servizi applicativi definiti nella radice del soggetto devono possedere un nome univoco
		int numSA = 0;
		for (int j=0; j<sogg.sizeServizioApplicativoList(); j++) {
			ServizioApplicativo tmpSA = sogg.getServizioApplicativo(j);
			if (sa.getNome().equals(tmpSA.getNome())){
				numSA++;
			}
		}
		if (numSA > 1)
			this.errori.add("Non può esistere più di un servizio applicativo con nome "+sa.getNome()+" associato al soggetto "+sogg.getTipo()+"/"+sogg.getNome());
	}

	private  void validaConnettore(Connettore conn, String identificativoElementoInternoSoggetto, Soggetto soggetto) throws DriverConfigurazioneException {

		// Valida il connettore
		// NOTA: che il connettore puo' essere definito all'interno dell'oggetto o direttamente nella root del registro dei servizi. In tal caso all'interno dell'oggetto vi e' solo il nome del connettore. La validazione deve tenere conto di cio, e quindi se vi e' presente solo il nome, deve prima cercare il connettore nella root e poi validarlo.
		// 1. i connettori definiti nella radice del registro devono possedere un nome univoco
		// 2. Il connettore deve possedere un tipo definito. Il tipo di connettore definito deve essere validato utilizzando il seguente metodo: org.openspcoop.pdd.config.ClassNameProperties.getInstance().getConnettore(tipo). Se il metodo ritorna null, il tipo di connettore non e' valido.
		// 3. Se il connettore e' di tipo http deve possedere i stessi vincoli presenti per un connettore http inserito nell'interfaccia grafica regserv, e cioe deve possedere le property che servono ad un connettore http.
		// 4. Se il connettore e' di tipo jms deve possedere i stessi vincoli presenti per un connettore jms inserito nell'interfaccia grafica regserv, e cioe deve possedere le property che servono ad un connettore jms.
		// 5. Se il connettore e' di tipo 'disabilitato' non deve possedere property

		String nomeConn = conn.getNome();
		String tipoConn = conn.getTipo();

		String idSoggetto = soggetto.getTipo()+"/"+soggetto.getNome();
		
		// required
		if(nomeConn==null){
			if(identificativoElementoInternoSoggetto==null)
				this.errori.add("Esiste un connettore nella radice del soggetto["+idSoggetto+"] per cui non è definito il nome");
			else
				this.errori.add("Esiste un connettore del "+identificativoElementoInternoSoggetto+" (Soggetto:"+idSoggetto+") per cui non è definito il nome");
			return;
		}

		if(identificativoElementoInternoSoggetto==null){

			// 1. i connettori definiti nella radice del soggetto devono possedere un nome univoco
			int numC = 0;
			for(int j=0; j<soggetto.sizeConnettoreList();j++){
				Connettore tmpC = soggetto.getConnettore(j);
				if (nomeConn.equals(tmpC.getNome()))
					numC++;
			}
			if (numC > 1)
				this.errori.add("Non può esistere più di un connettore con nome "+nomeConn+" definito come elemento radice del soggetto["+idSoggetto+"]");
		}

		else{
			if (tipoConn == null) {
				// Cerco il connettore nella root del registro
				conn = null;
				for (int j=0; j<soggetto.sizeConnettoreList();j++) {
					Connettore connettore = soggetto.getConnettore(j);
					if (nomeConn.equals(connettore.getNome())) {
						conn = connettore;
						break;
					}
				}
			}
			if (conn == null)
				this.errori.add("Il connettore "+nomeConn+" del "+identificativoElementoInternoSoggetto+" (Soggetto:"+idSoggetto+") non corrisponde a nessuno dei connettori registrati");
			else {
				tipoConn = conn.getTipo();
			}
		}

		// 2. Il connettore deve possedere un tipo definito. Il tipo di connettore definito deve essere validato utilizzando il seguente metodo: org.openspcoop.pdd.config.ClassNameProperties.getInstance().getConnettore(tipo). Se il metodo ritorna null, il tipo di connettore non e' valido.
		if(this.tipoConnettori.contains(tipoConn)==false){
			if(identificativoElementoInternoSoggetto==null){
				this.errori.add("Il tipo ["+tipoConn+"] del connettore "+nomeConn+", definito nella radice del soggetto["+idSoggetto+"], non è valido (Tipi conosciuti: "+this.getTipoConnettori()+")");
			}else{
				this.errori.add("Il tipo ["+tipoConn+"] del connettore "+nomeConn+" del "+identificativoElementoInternoSoggetto+" (Soggetto:"+idSoggetto+") non è valido (Tipi conosciuti: "+this.getTipoConnettori()+")");
			}
		}
		else{

			List<Property> cps = conn.getPropertyList();
			Property[] cpArray = null;
			if(cps.size()>0){
				cpArray = cps.toArray(new Property[cps.size()]); 
			}
			if(cpArray!=null){
				// Check generale sulle proprietà
				for (int j=0; j<cpArray.length;j++) {
					Property cp = cpArray[j];
					if(cp.getNome()==null){
						if(identificativoElementoInternoSoggetto==null){
							this.errori.add("Il connettore "+nomeConn+", definito nella radice del soggetto["+idSoggetto+"], possiede una proprietà per cui non è definito il nome");
						}else{
							this.errori.add("Il connettore "+nomeConn+" del "+identificativoElementoInternoSoggetto+" (Soggetto:"+idSoggetto+") possiede una proprietà per cui non è definito il nome");
						}
						return;
					}
					if(cp.getValore()==null){
						if(identificativoElementoInternoSoggetto==null){
							this.errori.add("Il connettore "+nomeConn+", definito nella radice del soggetto["+idSoggetto+"], possiede una proprietà per cui non è definito il valore");
						}else{
							this.errori.add("Il connettore "+nomeConn+" del "+identificativoElementoInternoSoggetto+" (Soggetto:"+idSoggetto+") possiede una proprietà per cui non è definito il valore");
						}
						return;
					}
				}
			}

			// Connettore HTTP
			if (tipoConn.equals(TipiConnettore.HTTP.getNome())) {
				String urlConn = null;
				for (int j=0; j<cpArray.length;j++) {
					Property cp = cpArray[j];
					if (cp.getNome().equals(CostantiDB.CONNETTORE_HTTP_LOCATION)) {
						urlConn = cp.getValore();
						break;
					}
				}
				if (urlConn == null){
					if(identificativoElementoInternoSoggetto==null){
						this.errori.add("Il connettore "+nomeConn+", definito nella radice del soggetto["+idSoggetto+"], è di tipo http, ma non ha una url definita");
					}else{
						this.errori.add("Il connettore "+nomeConn+" del "+identificativoElementoInternoSoggetto+" (Soggetto:"+idSoggetto+") è di tipo http, ma non ha una url definita");
					}
				}
				else{
					try{
						RegExpUtilities.validateUrl(urlConn);
					}catch(Exception e){
						if(identificativoElementoInternoSoggetto==null){
							this.errori.add("Il connettore "+nomeConn+", definito nella radice del soggetto["+idSoggetto+"], è di tipo http, ma non ha una url valida: "+e.getMessage());
						}else{
							this.errori.add("Il connettore "+nomeConn+" del "+identificativoElementoInternoSoggetto+" (Soggetto:"+idSoggetto+") è di tipo http, ma non ha una url valida: "+e.getMessage());
						}
					}
				}
			}

			// Connettore JMS
			else if (tipoConn.equals(TipiConnettore.JMS.getNome())) {
				String jmsNome = null, jmsTipo = null, jmsConnFact = null, jmsSendAs = null;
				for (int j=0; j<cpArray.length;j++) {
					Property cp = cpArray[j];
					if (cp.getNome().equals(CostantiDB.CONNETTORE_JMS_NOME))
						jmsNome = cp.getValore();
					else if (cp.getNome().equals(CostantiDB.CONNETTORE_JMS_TIPO))
						jmsTipo = cp.getValore();
					else if (cp.getNome().equals(CostantiDB.CONNETTORE_JMS_CONNECTION_FACTORY))
						jmsConnFact = cp.getValore();
					else if (cp.getNome().equals(CostantiDB.CONNETTORE_JMS_SEND_AS))
						jmsSendAs = cp.getValore();
				}
				if (jmsNome == null){
					if(identificativoElementoInternoSoggetto==null){
						this.errori.add("Il connettore "+nomeConn+", definito nella radice del soggetto["+idSoggetto+"], è di tipo jms, ma non ha un nome coda/topic definito");
					}else{
						this.errori.add("Il connettore "+nomeConn+" del "+identificativoElementoInternoSoggetto+" (Soggetto:"+idSoggetto+") è di tipo jms, ma non ha un nome coda/topic definito");
					}
				}
				if (jmsTipo == null){
					if(identificativoElementoInternoSoggetto==null){
						this.errori.add("Il connettore "+nomeConn+", definito nella radice del soggetto["+idSoggetto+"], è di tipo jms, ma non ha un tipo coda/topic definito");
					}else{
						this.errori.add("Il connettore "+nomeConn+" del "+identificativoElementoInternoSoggetto+" (Soggetto:"+idSoggetto+") è di tipo jms, ma non ha un tipo coda/topic definito");
					}
				}else{
					if (!jmsTipo.equals(CostantiConnettori.CONNETTORE_JMS_TIPO_QUEUE) && !jmsTipo.equals(CostantiConnettori.CONNETTORE_JMS_TIPO_TOPIC)){
						if(identificativoElementoInternoSoggetto==null){
							this.errori.add("Il connettore "+nomeConn+", definito nella radice del soggetto["+idSoggetto+"], è di tipo jms, ma non ha un tipo coda/topic valido (valori assumibili sono topic/queue)");
						}else{
							this.errori.add("Il connettore "+nomeConn+" del "+identificativoElementoInternoSoggetto+" (Soggetto:"+idSoggetto+") è di tipo jms, ma non ha un tipo coda/topic valido (valori assumibili sono topic/queue)");
						}
					}
				}
				if (jmsConnFact == null){
					if(identificativoElementoInternoSoggetto==null){
						this.errori.add("Il connettore "+nomeConn+", definito nella radice del soggetto["+idSoggetto+"], è di tipo jms, ma non ha una connection factory definita");
					}else{
						this.errori.add("Il connettore "+nomeConn+" del "+identificativoElementoInternoSoggetto+" (Soggetto:"+idSoggetto+") è di tipo jms, ma non ha una connection factory definita");
					}
				}
				if (jmsSendAs == null){
					if(identificativoElementoInternoSoggetto==null){
						this.errori.add("Il connettore "+nomeConn+", definito nella radice del soggetto["+idSoggetto+"], è di tipo jms, ma non ha un tipo di messaggio (sendAs) definito");
					}else{
						this.errori.add("Il connettore "+nomeConn+" del "+identificativoElementoInternoSoggetto+" (Soggetto:"+idSoggetto+") è di tipo jms, ma non ha un tipo di messaggio (sendAs) definito");
					}
				}else{
					if (!jmsSendAs.equals(CostantiConnettori.CONNETTORE_JMS_SEND_AS_TEXT_MESSAGE) && !jmsSendAs.equals(CostantiConnettori.CONNETTORE_JMS_SEND_AS_BYTES_MESSAGE)){
						if(identificativoElementoInternoSoggetto==null){
							this.errori.add("Il connettore "+nomeConn+", definito nella radice del soggetto["+idSoggetto+"], è di tipo jms, ma non ha un tipo di messaggio (sendAs) (valori assumibili sono TextMessage/BytesMessage)");
						}else{
							this.errori.add("Il connettore "+nomeConn+" del "+identificativoElementoInternoSoggetto+" (Soggetto:"+idSoggetto+") è di tipo jms, ma non ha un tipo di messaggio (sendAs) (valori assumibili sono TextMessage/BytesMessage)");
						}
					}
				}
			}

			// Connettore HTTPS
			else if (tipoConn.equals(TipiConnettore.HTTPS.getNome())) {
				String urlConn = null;
				String trustStoreLocation = null;
				String trustStorePassword = null;
				String keyStoreLocation = null;
				String keyStorePassword = null;
				String keyPassword = null;
				String hostNameVerifier = null;
				for (int j=0; j<cpArray.length;j++) {
					Property cp = cpArray[j];
					if (cp.getNome().equals(CostantiDB.CONNETTORE_HTTPS_LOCATION)) {
						urlConn = cp.getValore();
					}
					else if (cp.getNome().equals(CostantiDB.CONNETTORE_HTTPS_TRUST_STORE_LOCATION)) {
						trustStoreLocation = cp.getValore();
					}
					else if (cp.getNome().equals(CostantiDB.CONNETTORE_HTTPS_TRUST_STORE_PASSWORD)) {
						trustStorePassword = cp.getValore();
					}
					else if (cp.getNome().equals(CostantiDB.CONNETTORE_HTTPS_KEY_STORE_LOCATION)) {
						keyStoreLocation = cp.getValore();
					}
					else if (cp.getNome().equals(CostantiDB.CONNETTORE_HTTPS_KEY_STORE_PASSWORD)) {
						keyStorePassword = cp.getValore();
					}
					else if (cp.getNome().equals(CostantiDB.CONNETTORE_HTTPS_KEY_PASSWORD)) {
						keyPassword = cp.getValore();
					}
					else if (cp.getNome().equals(CostantiDB.CONNETTORE_HTTPS_HOSTNAME_VERIFIER)) {
						hostNameVerifier = cp.getValore();
					}
				}
				if (urlConn == null){
					if(identificativoElementoInternoSoggetto==null){
						this.errori.add("Il connettore "+nomeConn+", definito nella radice del soggetto["+idSoggetto+"], è di tipo http, ma non ha una url definita");
					}else{
						this.errori.add("Il connettore "+nomeConn+" del "+identificativoElementoInternoSoggetto+" (Soggetto:"+idSoggetto+") è di tipo http, ma non ha una url definita");
					}
				}
				else{
					try{
						RegExpUtilities.validateUrl(urlConn);
					}catch(Exception e){
						if(identificativoElementoInternoSoggetto==null){
							this.errori.add("Il connettore "+nomeConn+", definito nella radice del soggetto["+idSoggetto+"], è di tipo http, ma non ha una url valida: "+e.getMessage());
						}else{
							this.errori.add("Il connettore "+nomeConn+" del "+identificativoElementoInternoSoggetto+" (Soggetto:"+idSoggetto+") è di tipo http, ma non ha una url valida: "+e.getMessage());
						}
					}
				}
				if (trustStoreLocation != null){
					File f = new File(trustStoreLocation);
					if(f.exists()==false){
						if(identificativoElementoInternoSoggetto==null){
							this.errori.add("Il connettore "+nomeConn+", definito nella radice del soggetto["+idSoggetto+"], definisce un truststore "+trustStoreLocation+" che non esiste");
						}else{
							this.errori.add("Il connettore "+nomeConn+" del "+identificativoElementoInternoSoggetto+" (Soggetto:"+idSoggetto+") definisce un truststore "+trustStoreLocation+" che non esiste");
						}
					}
					else if(f.isFile()==false){
						if(identificativoElementoInternoSoggetto==null){
							this.errori.add("Il connettore "+nomeConn+", definito nella radice del soggetto["+idSoggetto+"], definisce un truststore "+trustStoreLocation+" che non e' un file");
						}else{
							this.errori.add("Il connettore "+nomeConn+" del "+identificativoElementoInternoSoggetto+" (Soggetto:"+idSoggetto+") definisce un truststore "+trustStoreLocation+" che non e' un file");
						}
					}
					else if(f.canRead()==false){
						if(identificativoElementoInternoSoggetto==null){
							this.errori.add("Il connettore "+nomeConn+", definito nella radice del soggetto["+idSoggetto+"], definisce un truststore "+trustStoreLocation+" che non e' accessibile (permessi in lettura non forniti)");
						}else{
							this.errori.add("Il connettore "+nomeConn+" del "+identificativoElementoInternoSoggetto+" (Soggetto:"+idSoggetto+") definisce un truststore "+trustStoreLocation+" che non e' accessibile (permessi in lettura non forniti)");
						}
					}
					else if(trustStorePassword==null){
						if(identificativoElementoInternoSoggetto==null){
							this.errori.add("Il connettore "+nomeConn+", definito nella radice del soggetto["+idSoggetto+"], definisce un truststore "+trustStoreLocation+" per cui non e' stata specificata una password");
						}else{
							this.errori.add("Il connettore "+nomeConn+" del "+identificativoElementoInternoSoggetto+" (Soggetto:"+idSoggetto+") definisce un truststore "+trustStoreLocation+" per cui non e' stata specificata una password");
						}
					}
				}
				if (keyStoreLocation != null){
					File f = new File(keyStoreLocation);
					if(f.exists()==false){
						if(identificativoElementoInternoSoggetto==null){
							this.errori.add("Il connettore "+nomeConn+", definito nella radice del soggetto["+idSoggetto+"], definisce un keystore "+keyStoreLocation+" che non esiste");
						}else{
							this.errori.add("Il connettore "+nomeConn+" del "+identificativoElementoInternoSoggetto+" (Soggetto:"+idSoggetto+") definisce un keystore "+keyStoreLocation+" che non esiste");
						}
					}
					else if(f.isFile()==false){
						if(identificativoElementoInternoSoggetto==null){
							this.errori.add("Il connettore "+nomeConn+", definito nella radice del soggetto["+idSoggetto+"], definisce un keystore "+keyStoreLocation+" che non e' un file");
						}else{
							this.errori.add("Il connettore "+nomeConn+" del "+identificativoElementoInternoSoggetto+" (Soggetto:"+idSoggetto+") definisce un keystore "+keyStoreLocation+" che non e' un file");
						}
					}
					else if(f.canRead()==false){
						if(identificativoElementoInternoSoggetto==null){
							this.errori.add("Il connettore "+nomeConn+", definito nella radice del soggetto["+idSoggetto+"], definisce un keystore "+keyStoreLocation+" che non e' accessibile (permessi in lettura non forniti)");
						}else{
							this.errori.add("Il connettore "+nomeConn+" del "+identificativoElementoInternoSoggetto+" (Soggetto:"+idSoggetto+") definisce un keystore "+keyStoreLocation+" che non e' accessibile (permessi in lettura non forniti)");
						}
					}
					else if(keyStorePassword==null){
						if(identificativoElementoInternoSoggetto==null){
							this.errori.add("Il connettore "+nomeConn+", definito nella radice del soggetto["+idSoggetto+"], definisce un keystore "+keyStoreLocation+" per cui non e' stata specificata una password");
						}else{
							this.errori.add("Il connettore "+nomeConn+" del "+identificativoElementoInternoSoggetto+" (Soggetto:"+idSoggetto+") definisce un keystore "+keyStoreLocation+" per cui non e' stata specificata una password");
						}
					}
					else if(keyPassword==null){
						if(identificativoElementoInternoSoggetto==null){
							this.errori.add("Il connettore "+nomeConn+", definito nella radice del soggetto["+idSoggetto+"], definisce un keystore "+keyStoreLocation+" per cui non e' stata specificata una password per la chiave privata");
						}else{
							this.errori.add("Il connettore "+nomeConn+" del "+identificativoElementoInternoSoggetto+" (Soggetto:"+idSoggetto+") definisce un keystore "+keyStoreLocation+" per cui non e' stata specificata una password per la chiave privata");
						}
					}
				}
				if(hostNameVerifier!=null){
					try{
						Boolean.parseBoolean(hostNameVerifier);
					}catch(Exception e){
						if(identificativoElementoInternoSoggetto==null){
							this.errori.add("Il connettore "+nomeConn+", definito nella radice del soggetto["+idSoggetto+"], associa un valore non valido  alla proprieta' 'hostNameVerifier'; valori utilizzabili: true e false");
						}else{
							this.errori.add("Il connettore "+nomeConn+" del "+identificativoElementoInternoSoggetto+" (Soggetto:"+idSoggetto+") associa un valore non valido  alla proprieta' 'hostNameVerifier'; valori utilizzabili: true e false");
						}
					}
				}
			}
			
			else if (tipoConn.equals(TipiConnettore.DISABILITATO.getNome())) {
				if (cpArray != null) {
					if(identificativoElementoInternoSoggetto==null){
						this.errori.add("Il connettore "+nomeConn+", definito nella radice del soggetto["+idSoggetto+"], è di tipo disabilitato, ma ha delle properties definite");
					}else{
						this.errori.add("Il connettore "+nomeConn+" del "+identificativoElementoInternoSoggetto+" (Soggetto:"+idSoggetto+") è di tipo disabilitato, ma ha delle properties definite");
					}
				}
			}
		}
	}

	private void validaCorrelazioneApplicativaRichiesta(String identificativoRisorsa,CorrelazioneApplicativa ca,boolean portaDelegata) throws DriverConfigurazioneException{
		// UNICITA: i nomi definiti nella correlazione devono possedere un nome univoco. Puo' esistere anche UN SOLO elemento senza nome
		// Se identificazione='urlBased' o 'contentBased' il pattern e' obbligatorio.
		if (ca != null) {
			
			// Scadenza
			if(ca.getScadenza()!=null){
				try{
					Long.parseLong(ca.getScadenza());
				}catch(Exception e){
					this.errori.add("Il valore associato alla scadenza della correlazione applicativa inserita nella "+identificativoRisorsa+" dev'essere un numero intero");
				}
			}
			
			// Minimo numero di elementi
			if(ca.sizeElementoList()<1){
				this.errori.add(identificativoRisorsa+" che possiede la definizione di una correlazione applicativa senza elementi?");
			}
			
			// Controllo che l'elemento null, equivalga solo all'ultimo elemento della configurazione
			for (int j=0; j<ca.sizeElementoList();j++) {
				CorrelazioneApplicativaElemento cae = ca.getElemento(j);
				if(cae.getNome()==null && j!=(ca.sizeElementoList()-1)){
					this.errori.add(identificativoRisorsa+" che possiede la definizione di una correlazione applicativa ("+(j+1)+") con un elemento '*' (match qualsiasi contenuto) definito non come ultima regola di correlazione");
				}
			}
			
			// Elementi
			for (int j=0; j<ca.sizeElementoList();j++) {
				CorrelazioneApplicativaElemento cae = ca.getElemento(j);
				int numE = 0;
				for(int k=0; k<ca.sizeElementoList();k++){
					CorrelazioneApplicativaElemento tmpCae = ca.getElemento(k);
					if(cae.getNome()==null){
						if(tmpCae.getNome()==null)
							numE++;
					}else{
						if ((cae.getNome()).equals(tmpCae.getNome()))
							numE++;
					}
				}
				if (numE > 1){
					String nome = "*";
					if(cae.getNome()!=null){
						nome = cae.getNome();
					}
					this.errori.add("Non può esistere più di un elemento di correlazione applicativa con nome "+nome+ " ("+identificativoRisorsa+")");
				}
				
				CorrelazioneApplicativaRichiestaIdentificazione identificazione = cae.getIdentificazione();
				if ((identificazione != null) && !identificazione.equals(CostantiConfigurazione.CORRELAZIONE_APPLICATIVA_RICHIESTA_DISABILITATO) && 
						!identificazione.equals(CostantiConfigurazione.CORRELAZIONE_APPLICATIVA_RICHIESTA_URL_BASED) && 
						!identificazione.equals(CostantiConfigurazione.CORRELAZIONE_APPLICATIVA_RICHIESTA_CONTENT_BASED) && 
						!identificazione.equals(CostantiConfigurazione.CORRELAZIONE_APPLICATIVA_RICHIESTA_INPUT_BASED)){
					this.errori.add("La modalita d'identificazione della correlazione applicativa ("+(j+1)+") nella "+identificativoRisorsa+" deve assumere uno dei seguente valori: "+
							CostantiConfigurazione.CORRELAZIONE_APPLICATIVA_RICHIESTA_DISABILITATO+","+
							CostantiConfigurazione.CORRELAZIONE_APPLICATIVA_RICHIESTA_URL_BASED+","+
							CostantiConfigurazione.CORRELAZIONE_APPLICATIVA_RICHIESTA_CONTENT_BASED+" o "+
							CostantiConfigurazione.CORRELAZIONE_APPLICATIVA_RICHIESTA_INPUT_BASED);
				}
				if(identificazione==null){
					identificazione = CostantiConfigurazione.CORRELAZIONE_APPLICATIVA_RICHIESTA_CONTENT_BASED;
				}
				
				if(CostantiConfigurazione.CORRELAZIONE_APPLICATIVA_RICHIESTA_URL_BASED.equals(identificazione) || CostantiConfigurazione.CORRELAZIONE_APPLICATIVA_RICHIESTA_CONTENT_BASED.equals(identificazione)){
					if (cae.getPattern() == null)
						this.errori.add("La correlazione applicativa ("+(j+1)+") della "+identificativoRisorsa+" non contiene la definizione del pattern, nonostante la modalita' di identificazione sia "+identificazione);
				}	
				
				CorrelazioneApplicativaGestioneIdentificazioneFallita identificazioneFallita = cae.getIdentificazioneFallita();
				if ((identificazioneFallita != null) && !identificazioneFallita.equals(CostantiConfigurazione.BLOCCA) && 
						!identificazioneFallita.equals(CostantiConfigurazione.ACCETTA)){
					this.errori.add("Il comportamento in caso di identificazione fallita della correlazione applicativa ("+(j+1)+") nella "+identificativoRisorsa+" deve assumere uno dei seguente valori: "+
							CostantiConfigurazione.BLOCCA+" o "+
							CostantiConfigurazione.ACCETTA);
				}
				
				StatoFunzionalita riusoIdentificatore = cae.getRiusoIdentificativo();
				if ((riusoIdentificatore != null) && !riusoIdentificatore.equals(CostantiConfigurazione.ABILITATO) && 
						!riusoIdentificatore.equals(CostantiConfigurazione.DISABILITATO)){
					this.errori.add("Il comportamento per il riuso dell'id della correlazione applicativa ("+(j+1)+") nella "+identificativoRisorsa+" deve assumere uno dei seguente valori: "+
							CostantiConfigurazione.ABILITATO+" o "+
							CostantiConfigurazione.DISABILITATO);
				}
				
				
			}
		}
	}
	
	private void validaCorrelazioneApplicativaRisposta(String identificativoRisorsa,CorrelazioneApplicativaRisposta ca,boolean portaDelegata) throws DriverConfigurazioneException{
		// UNICITA: i nomi definiti nella correlazione devono possedere un nome univoco. Puo' esistere anche UN SOLO elemento senza nome
		// Se identificazione='urlBased' o 'contentBased' il pattern e' obbligatorio.
		if (ca != null) {
			
			// Minimo numero di elementi
			if(ca.sizeElementoList()<1){
				this.errori.add(identificativoRisorsa+" che possiede la definizione di una correlazione applicativa senza elementi?");
			}
			
			// Controllo che l'elemento null, equivalga solo all'ultimo elemento della configurazione
			for (int j=0; j<ca.sizeElementoList();j++) {
				CorrelazioneApplicativaRispostaElemento cae = ca.getElemento(j);
				if(cae.getNome()==null && j!=(ca.sizeElementoList()-1)){
					this.errori.add(identificativoRisorsa+" che possiede la definizione di una correlazione applicativa ("+(j+1)+") con un elemento '*' (match qualsiasi contenuto) definito non come ultima regola di correlazione");
				}
			}
			
			// Elementi
			for (int j=0; j<ca.sizeElementoList();j++) {
				CorrelazioneApplicativaRispostaElemento cae = ca.getElemento(j);
				int numE = 0;
				for(int k=0; k<ca.sizeElementoList();k++){
					CorrelazioneApplicativaRispostaElemento tmpCae = ca.getElemento(k);
					if(cae.getNome()==null){
						if(tmpCae.getNome()==null)
							numE++;
					}else{
						if ((cae.getNome()).equals(tmpCae.getNome()))
							numE++;
					}
				}
				if (numE > 1){
					String nome = "*";
					if(cae.getNome()!=null){
						nome = cae.getNome();
					}
					this.errori.add("Non può esistere più di un elemento di correlazione applicativa con nome "+nome+ " ("+identificativoRisorsa+")");
				}
				
				CorrelazioneApplicativaRispostaIdentificazione identificazione = cae.getIdentificazione();
				if ((identificazione != null) && !identificazione.equals(CostantiConfigurazione.CORRELAZIONE_APPLICATIVA_RISPOSTA_DISABILITATO)  && 
						!identificazione.equals(CostantiConfigurazione.CORRELAZIONE_APPLICATIVA_RISPOSTA_CONTENT_BASED) && 
						!identificazione.equals(CostantiConfigurazione.CORRELAZIONE_APPLICATIVA_RISPOSTA_INPUT_BASED)){
					this.errori.add("La modalita d'identificazione della correlazione applicativa ("+(j+1)+") nella "+identificativoRisorsa+" deve assumere uno dei seguente valori: "+
							CostantiConfigurazione.CORRELAZIONE_APPLICATIVA_RISPOSTA_DISABILITATO+","+
							CostantiConfigurazione.CORRELAZIONE_APPLICATIVA_RISPOSTA_CONTENT_BASED+" o "+
							CostantiConfigurazione.CORRELAZIONE_APPLICATIVA_RISPOSTA_INPUT_BASED);
				}
				if(identificazione==null){
					identificazione = CostantiConfigurazione.CORRELAZIONE_APPLICATIVA_RISPOSTA_CONTENT_BASED;
				}
				
				if(CostantiConfigurazione.CORRELAZIONE_APPLICATIVA_RISPOSTA_CONTENT_BASED.equals(identificazione)){
					if (cae.getPattern() == null)
						this.errori.add("La correlazione applicativa ("+(j+1)+") della "+identificativoRisorsa+" non contiene la definizione del pattern, nonostante la modalita' di identificazione sia "+identificazione);
				}	
				
				CorrelazioneApplicativaGestioneIdentificazioneFallita identificazioneFallita = cae.getIdentificazioneFallita();
				if ((identificazioneFallita != null) && !identificazioneFallita.equals(CostantiConfigurazione.BLOCCA) && 
						!identificazioneFallita.equals(CostantiConfigurazione.ACCETTA)){
					this.errori.add("Il comportamento in caso di identificazione fallita della correlazione applicativa ("+(j+1)+") nella "+identificativoRisorsa+" deve assumere uno dei seguente valori: "+
							CostantiConfigurazione.BLOCCA+" o "+
							CostantiConfigurazione.ACCETTA);
				}
				
			}
		}
	}
	
	private void validaConfigurazione(Configurazione configurazione) throws DriverConfigurazioneException {
		
		// 1.  Valida accesso registro
		// Ogni registro deve avere un nome univoco.
		AccessoRegistro ar = null;
		if(configurazione.getAccessoRegistro()==null){
			this.errori.add("Non è stato configurato un accesso ad un registro dei servizi");
		}else{
			ar = configurazione.getAccessoRegistro();
			if(ar.sizeRegistroList()<=0){
				this.errori.add("Non è stato configurato un accesso ad un registro dei servizi");
				ar = null;
			}else{
				for (int j=0; j<ar.sizeRegistroList(); j++) {
					AccessoRegistroRegistro arr = ar.getRegistro(j);
					String nomeArr = arr.getNome();
					
					// required
					if(nomeArr==null){
						this.errori.add("E' presente la configurazione di accesso ad un registro dei servizi anonimo; deve obbligatoriamente essere associato un nome ad un registro dei servizi");
					}
					else{
						int numAR = 0;
						for(int k=0; k<ar.sizeRegistroList();k++){
							AccessoRegistroRegistro tmpArr = ar.getRegistro(k);
							if (nomeArr.equals(tmpArr.getNome()))
								numAR++;
						}
						if (numAR > 1)
							this.errori.add("Non può esistere più di un registro dei servizi con nome "+nomeArr);
					}

					// XSD: tipo: xml, uddi, web, db, ws
					RegistroTipo tipo = arr.getTipo();
					if ((tipo != null) && !tipo.equals(CostantiConfigurazione.REGISTRO_XML) && 
							!tipo.equals(CostantiConfigurazione.REGISTRO_UDDI) && 
							!tipo.equals(CostantiConfigurazione.REGISTRO_WEB) && 
							!tipo.equals(CostantiConfigurazione.REGISTRO_DB) && 
							!tipo.equals(CostantiConfigurazione.REGISTRO_WS))
						this.errori.add("Il tipo del registro "+nomeArr+" non è valido; deve assumere uno dei seguenti tipi: "+CostantiConfigurazione.REGISTRO_XML.toString()+","+
								CostantiConfigurazione.REGISTRO_UDDI.toString()+","+CostantiConfigurazione.REGISTRO_WEB.toString()+","+CostantiConfigurazione.REGISTRO_DB.toString()+","+
								CostantiConfigurazione.REGISTRO_WS.toString());
					
					// required
					if(arr.getLocation()==null){
						this.errori.add("Per la configurazione di accesso al registro dei servizi ["+nomeArr+"] non è stata fornita la location");
					}
					
					// User e Password
					if(arr.getPassword()!=null && arr.getUser()==null){
						this.errori.add("Per la configurazione di accesso al registro dei servizi ["+nomeArr+"] non è stata fornito un username, ma è stato specificata una password?");
					}
				}

				Cache arc = ar.getCache();
				if (arc != null) {
					
					validaCache(arc, "Registro dei Servizi");
					
				}
			}
		}
		
		


		// 2.  Valida routing table
		// Ogni destinazione deve possedere un tipo/nome univoco.
		// Le rotte hanno i seguenti vincoli:
		// Registro.
		// Il nome di un registro (se presente) deve essere uno tra i registro definiti nella sezione AccessoRegistro.
		// Le rotte di tipo registro vengono identificate dal nome (dove il nome non presente e' il caso particolare registro NULL).
		// Ogni registro deve possedere un nome univoco
		// Gateway.
		// Ogni tipo/nome deve essere univoco.
		RoutingTable rt = configurazione.getRoutingTable();
		if (rt != null) {
			
			// Rotta di default
			if(rt.getDefault()==null){
				this.errori.add("(RoutingTable abilitata) Non è stata definita una rotta di default");
			}
			else if(rt.getDefault().sizeRouteList()==0){
				this.errori.add("(RoutingTable abilitata) Non è stata definita una rotta tra quelle di default");
			}
			else{
				RoutingTableDefault rtDefault = rt.getDefault();
				for(int i=0; i<rtDefault.sizeRouteList(); i++){
					Route r = rtDefault.getRoute(i);
					this.validaRotta(r, "[RottaDefault]", ar);
					
					if(r.getRegistro()!=null){
						// Controllo diversita' delle rotte di default
						int numRR = 0;
						for(int h=0; h<rtDefault.sizeRouteList();h++){
							Route tmpR = rtDefault.getRoute(h);
							if(tmpR.getRegistro()!=null){
								RouteRegistro tmpRR = tmpR.getRegistro();
								if(r.getRegistro().getNome()==null){
									if(tmpRR.getNome()==null)
										numRR++;
								}else{
									if (r.getRegistro().getNome().equals(tmpRR.getNome()))
										numRR++;
								}
							}
						}
						if (numRR > 1){
							String nome = "TuttiRegistri";
							if(r.getRegistro().getNome()!=null)
								nome = r.getRegistro().getNome();
							this.errori.add("(RoutingTableAbilitata) Non può esistere più di una rotta di default, che utilizzi il registro "+nome);
						}
					}
					
					if(r.getGateway()!=null && r.getGateway().getTipo()!=null && r.getGateway().getNome()!=null){
						// Controllo diversita' delle rotte di tipo gateway
						RouteGateway rg = r.getGateway();
						String idRG = rg.getTipo()+"/"+rg.getNome();
						int numRG = 0;
						for(int h=0; h<rtDefault.sizeRouteList();h++){
							Route tmpR = rtDefault.getRoute(h);
							if(tmpR.getGateway()!=null && tmpR.getGateway().getTipo()!=null && tmpR.getGateway().getNome()!=null) {
								RouteGateway tmpRG = tmpR.getGateway();
								String tmpIdRG = tmpRG.getTipo()+"/"+tmpRG.getNome();
								if (idRG.equals(tmpIdRG))
									numRG++;
							}
						}
						if (numRG > 1)
							this.errori.add("(RoutingTableAbilitata) Non può esistere più di una rotta di default di tipo gateway con identificativo soggetto "+idRG);
					}
				}
			}
			
			// Destinazioni statiche
			for (int j=0; j<rt.sizeDestinazioneList(); j++) {
				RoutingTableDestinazione rtd = rt.getDestinazione(j);
				
				// required
				if(rtd.getTipo()==null){
					this.errori.add("(RoutingTable abilitata) E' stata definita una rotta di destinazone statica, senza aver specificato il tipo");
				}
				else if(rtd.getNome()==null){
					this.errori.add("(RoutingTable abilitata) E' stata definita una rotta di destinazone statica, senza aver specificato il nome");
				}
				else{
					
					String idDest = rtd.getTipo()+"/"+rtd.getNome();
					
					// Verifico che non esista piu' di una rotta statica con stesso id
					int numD = 0;
					for(int k=0; k<rt.sizeDestinazioneList();k++){
						RoutingTableDestinazione tmpRtd = rt.getDestinazione(k);
						String tmpIdDest = tmpRtd.getTipo()+"/"+tmpRtd.getNome();
						if (idDest.equals(tmpIdDest))
							numD++;
					}
					if (numD > 1)
						this.errori.add("(RoutingTable abilitata)  Non può esistere più di una rotta di destinazione statico con identificativo soggetto"+idDest);
					
					// Verifico rotte presenti nella destinazione statica
					for(int k=0; k<rtd.sizeRouteList();k++){
						Route r = rtd.getRoute(k);
					
						// Valida rotta
						this.validaRotta(r, "[RottaDestinazioneStatica("+idDest+")]", ar);

						if(r.getRegistro()!=null){
							// Controllo diversita' delle rotte di destinazione statica per questo soggetto
							int numRR = 0;
							for(int h=0; h<rtd.sizeRouteList();h++){
								Route tmpR = rtd.getRoute(h);
								if(tmpR.getRegistro()!=null){
									RouteRegistro tmpRR = tmpR.getRegistro();
									if(r.getRegistro().getNome()==null){
										if(tmpRR.getNome()==null)
											numRR++;
									}else{
										if (r.getRegistro().getNome().equals(tmpRR.getNome()))
											numRR++;
									}
								}
							}
							if (numRR > 1){
								String nome = "TuttiRegistri";
								if(r.getRegistro().getNome()!=null)
									nome = r.getRegistro().getNome();
								this.errori.add("(RoutingTableAbilitata) Non può esistere più di una rotta per la destinazione statica ["+idDest+"], che utilizzi il registro "+nome);
							}
						}
						
						if(r.getGateway()!=null && r.getGateway().getTipo()!=null && r.getGateway().getNome()!=null){
							// Controllo diversita' delle rotte di tipo gateway
							RouteGateway rg = r.getGateway();
							String idRG = rg.getTipo()+"/"+rg.getNome();
							int numRG = 0;
							for(int h=0; h<rtd.sizeRouteList();h++){
								Route tmpR = rtd.getRoute(h);
								if(tmpR.getGateway()!=null && tmpR.getGateway().getTipo()!=null && tmpR.getGateway().getNome()!=null) {
									RouteGateway tmpRG = tmpR.getGateway();
									String tmpIdRG = tmpRG.getTipo()+"/"+tmpRG.getNome();
									if (idRG.equals(tmpIdRG))
										numRG++;
								}
							}
							if (numRG > 1)
								this.errori.add("(RoutingTableAbilitata) Non può esistere più di una rotta per la destinazione statica ["+idDest+"] di tipo gateway con identificativo soggetto "+idRG);
						}
					}
					
				}
			}
		}

		
		// Validazione Accesso Configurazione
		if(configurazione.getAccessoConfigurazione()!=null){
			if(configurazione.getAccessoConfigurazione().getCache()!=null){
				this.validaCache(configurazione.getAccessoConfigurazione().getCache(), "ConfigurazionePdD");
			}
		}
		
		// Validazione Dati Accesso Autorizzazione
		if(configurazione.getAccessoDatiAutorizzazione()!=null){
			if(configurazione.getAccessoDatiAutorizzazione().getCache()!=null){
				this.validaCache(configurazione.getAccessoDatiAutorizzazione().getCache(), "DatiAutorizzazione");
			}
		}
		
		
		// ValidazioneBuste
		ValidazioneBuste vbe = configurazione.getValidazioneBuste();
		if (vbe != null) {
			// XSD: stato: abilitato, disabilitato, warningOnly
			StatoFunzionalitaConWarning statoBuste = vbe.getStato();
			if ((statoBuste != null) && !statoBuste.equals(CostantiConfigurazione.STATO_CON_WARNING_ABILITATO) && 
					!statoBuste.equals(CostantiConfigurazione.STATO_CON_WARNING_DISABILITATO) && 
					!statoBuste.equals(CostantiConfigurazione.STATO_CON_WARNING_WARNING_ONLY))
				this.errori.add("Lo stato della validazione buste deve possedere uno dei seguenti valori: "+CostantiConfigurazione.STATO_CON_WARNING_ABILITATO+
						", "+CostantiConfigurazione.STATO_CON_WARNING_DISABILITATO+
						" o "+CostantiConfigurazione.STATO_CON_WARNING_WARNING_ONLY);

			// XSD: controllo: normale, rigido
			ValidazioneBusteTipoControllo controllo = vbe.getControllo();
			if ((controllo != null) && !controllo.equals(CostantiConfigurazione.VALIDAZIONE_PROTOCOL_LIVELLO_NORMALE) && 
					!controllo.equals(CostantiConfigurazione.VALIDAZIONE_PROTOCOL_LIVELLO_RIGIDO))
				this.errori.add("Il controllo della validazione buste deve possedere uno dei seguenti valori: "+CostantiConfigurazione.VALIDAZIONE_PROTOCOL_LIVELLO_NORMALE+
						" o "+CostantiConfigurazione.VALIDAZIONE_PROTOCOL_LIVELLO_RIGIDO);

			// XSD: profilo-collaborazione: abilitato, disabilitato
			StatoFunzionalita profColl = vbe.getProfiloCollaborazione();
			if ((profColl != null) && !profColl.equals(CostantiConfigurazione.ABILITATO) && !profColl.equals(CostantiConfigurazione.DISABILITATO))
				this.errori.add("Il profilo di collaborazione della validazione buste deve possedere i valori "+CostantiConfigurazione.ABILITATO+
						" o "+CostantiConfigurazione.DISABILITATO);

			// XSD: manifest-attachments: abilitato, disabilitato
			StatoFunzionalita manifest = vbe.getManifestAttachments();
			if ((manifest != null) && !manifest.equals("abilitato") && !manifest.equals("disabilitato"))
				this.errori.add("Il manifest-attachments della validazione buste deve possedere i valori "+CostantiConfigurazione.ABILITATO+
						" o "+CostantiConfigurazione.DISABILITATO);
		}
	
		
		// ValidazioneContenutiApplicativi
		if(configurazione.getValidazioneContenutiApplicativi()!=null)
			this.validazioneValidazioneContenutiApplicativi(configurazione.getValidazioneContenutiApplicativi(),"ConfigurazioneGeneralePdD");
		
	
		// Indirizzo Telematico
		IndirizzoRisposta it = configurazione.getIndirizzoRisposta();
		if (it != null) {
			// XSD: utilizzo: abilitato, disabilitato
			StatoFunzionalita utilizzo = it.getUtilizzo();
			if ((utilizzo != null) && !utilizzo.equals(CostantiConfigurazione.ABILITATO) && !utilizzo.equals(CostantiConfigurazione.DISABILITATO))
				this.errori.add("L'utilizzo dell'indirizzo per la risposta nella configurazione generale della PdD deve assumere i valori: "+
						CostantiConfigurazione.ABILITATO+" o "+CostantiConfigurazione.DISABILITATO);
		}
		
		// Attachments
		Attachments attachments = configurazione.getAttachments();
		if (attachments != null) {
			// XSD: utilizzo: abilitato, disabilitato
			StatoFunzionalita gestione = attachments.getGestioneManifest();
			if ((gestione != null) && !gestione.equals(CostantiConfigurazione.ABILITATO) && !gestione.equals(CostantiConfigurazione.DISABILITATO))
				this.errori.add("La gestione dei manifest degli attachments nella configurazione generale della PdD deve assumere i valori: "+
						CostantiConfigurazione.ABILITATO+" o "+CostantiConfigurazione.DISABILITATO);
		}
		
		// Risposte
		Risposte rs = configurazione.getRisposte();
		if (rs != null) {
			// XSD: connessione: new, reply
			TipoConnessioneRisposte connessione = rs.getConnessione();
			if ((connessione != null) && !connessione.equals(CostantiConfigurazione.NEW_CONNECTION) && !connessione.equals(CostantiConfigurazione.CONNECTION_REPLY))
				this.errori.add("La connessione delle risposte nella configurazione generale della PdD deve assumere i valori:"+
						CostantiConfigurazione.CONNECTION_REPLY+" o "+CostantiConfigurazione.NEW_CONNECTION);
		}
		
		
		// Inoltro Buste non riscontrate
		if(configurazione.getInoltroBusteNonRiscontrate()==null){
			this.errori.add("Definizione della cadenza delle buste non riscontrate non presente nella configurazione generale della PdD");
		}else{
			InoltroBusteNonRiscontrate inoltro = configurazione.getInoltroBusteNonRiscontrate();
			if(inoltro.getCadenza()==null){
				this.errori.add("Definizione della cadenza delle buste non riscontrate non presente nella configurazione generale della PdD");
			}else{
				try{
					Long.parseLong(inoltro.getCadenza());
				}catch(Exception e){
					this.errori.add("Il valore associato alla cadenza delle buste non riscontrate, presente nella configurazione generale della PdD, dev'essere un numero intero");
				}
			}
		}
		
		
		
		// 3.  Valida mssaggi diagnostici
		// Ogni elemento definito nelle proprieta' di un OpenSPCoopProperties deve avere un nome univoco.
		MessaggiDiagnostici md = configurazione.getMessaggiDiagnostici();
		if (md != null) {
			for (int j=0; j<md.sizeOpenspcoopAppenderList(); j++) {
				OpenspcoopAppender oa = md.getOpenspcoopAppender(j);
				
				// Controllo tipo
				if(oa.getTipo()==null){
					this.errori.add("E' stato definito un appender dei msg diagnostici per cui non e' stato definito il tipo");
				}
				else {
					
					// Controllo tipo
					if(this.tipoMsgDiagnosticiAppender.contains(oa.getTipo())==false){
						this.errori.add("E' stato definito un appender dei msg diagnostici per cui e' stato definito un tipo["+oa.getTipo()+"] non valido, valori ammessi sono: "+this.getTipoMsgDiagnosticiAppender());
					}
					
					for(int k=0; k<oa.sizePropertyList();k++){
						Property oap = oa.getProperty(k);
						
						if(oap.getNome()==null){
							
						}
						else if(oap.getValore()==null){
							this.errori.add("E' stato definito un appender["+oa.getTipo()+"] dei msg diagnostici per cui esiste una proprieta' senza un valore?");
						}
						else{
							// Controllo univocita' nome
							String nomeOap = oap.getNome();
							int numOap = 0;
							for(int h=0; h<oa.sizePropertyList();h++){
								Property tmpOap = oa.getProperty(h);
								if (nomeOap.equals(tmpOap.getNome()))
									numOap++;
							}
							if (numOap > 1)
								this.errori.add("Non può esistere più di una proprietà con nome "+nomeOap +" per lo stesso appender["+oa.getTipo()+"] dei msg diagnostici");
						}
					}
				}
				
			}

			// XSD:: off, fatal, errorProtocol, errorIntegration, infoProtocol, infoIntegration, debugLow, debugMedium, debugHigh, all
			Severita severita = md.getSeverita();
			if ((severita != null) && !severita.equals(Severita.OFF) && !severita.equals(Severita.FATAL) && 
					!severita.equals(Severita.ERROR_PROTOCOL) && !severita.equals(Severita.ERROR_INTEGRATION) && 
					!severita.equals(Severita.INFO_PROTOCOL) && !severita.equals(Severita.INFO_INTEGRATION) && 
					!severita.equals(Severita.DEBUG_LOW) && !severita.equals(Severita.DEBUG_MEDIUM) && !severita.equals(Severita.DEBUG_HIGH) &&
					!severita.equals(Severita.ALL))
				this.errori.add("L'opzione 'severita' della configurazione dei messaggi diagnostici nella configurazione generale della PdD deve assumere uno dei seguenti valori: off, fatal, errorProtocol, errorIntegration, infoProtocol, infoIntegration, debugLow, debugMedium, debugHigh, all");

			// XSD: openspcoop: off, fatal, errorProtocol, errorIntegration, infoProtocol, infoIntegration, debugLow, debugMedium, debugHigh, all
			Severita severitaLog4j = md.getSeveritaLog4j();
			if ((severitaLog4j != null) && !severitaLog4j.equals(Severita.OFF) && !severitaLog4j.equals(Severita.FATAL) && 
					!severitaLog4j.equals(Severita.ERROR_PROTOCOL) && !severitaLog4j.equals(Severita.ERROR_INTEGRATION) && 
					!severitaLog4j.equals(Severita.INFO_PROTOCOL) && !severitaLog4j.equals(Severita.INFO_INTEGRATION) && 
					!severitaLog4j.equals(Severita.DEBUG_LOW) && !severitaLog4j.equals(Severita.DEBUG_MEDIUM) && !severitaLog4j.equals(Severita.DEBUG_HIGH) &&
					!severitaLog4j.equals(Severita.ALL))
			this.errori.add("L'opzione 'severita-log4j' della configurazione dei messaggi diagnostici nella configurazione generale della PdD deve assumere uno dei seguenti valori: off, fatal, errorProtocol, errorIntegration, infoProtocol, infoIntegration, debugLow, debugMedium, debugHigh, all");
		}
		else{
			this.errori.add("Non e' stata definita la configurazione dei messaggi diagnostici nella configurazione generale della PdD");
		}
		
		// 4.  Valida tracciamento
		// Ogni elemento definito nelle proprieta' di un OpenSPCoopProperties deve avere un nome univoco.
		Tracciamento t = configurazione.getTracciamento();
		if (t != null) {
			for (int j=0; j<t.sizeOpenspcoopAppenderList(); j++) {
				OpenspcoopAppender oa = t.getOpenspcoopAppender(j);
				
				// Controllo tipo
				if(oa.getTipo()==null){
					this.errori.add("E' stato definito un appender delle tracce per cui non e' stato definito il tipo");
				}
				else {
					
					// Controllo tipo
					if(this.tipoTracciamentoAppender.contains(oa.getTipo())==false){
						this.errori.add("E' stato definito un appender delle tracce per cui e' stato definito un tipo["+oa.getTipo()+"] non valido, valori ammessi sono: "+this.getTipoTracciamentoAppender());
					}
				
					for(int k=0; k<oa.sizePropertyList();k++){
						Property oap = oa.getProperty(k);
						
						if(oap.getNome()==null){
							this.errori.add("E' stato definito un appender["+oa.getTipo()+"] delle tracce per cui esiste una proprieta' senza un nome?");
						}
						else if(oap.getValore()==null){
							this.errori.add("E' stato definito un appender["+oa.getTipo()+"] delle tracce per cui esiste una proprieta' senza un valore?");
						}
						else{
							// Controllo univocita' nome
							String nomeOap = oap.getNome();
							int numOap = 0;
							for(int h=0; h<oa.sizePropertyList();h++){
								Property tmpOap = oa.getProperty(h);
								if (nomeOap.equals(tmpOap.getNome()))
									numOap++;
							}
							if (numOap > 1)
								this.errori.add("Non può esistere più di una proprietà con nome "+nomeOap+" per lo stesso appender["+oa.getTipo()+"] delle tracce");
						}
					}
					
				}
			}

			// XSD: buste: abilitato, disabilitato
			StatoFunzionalita tracciamentoBuste = t.getStato();
			if ((tracciamentoBuste != null) && !tracciamentoBuste.equals(CostantiConfigurazione.ABILITATO) && !tracciamentoBuste.equals(CostantiConfigurazione.DISABILITATO))
				this.errori.add("Il valore 'buste' della configurazione per il tracciamento delle buste deve assumere i valori "+CostantiConfigurazione.ABILITATO+
						" o "+CostantiConfigurazione.DISABILITATO);

		}

		
		// 5.  Valida dump
		// Ogni elemento definito nelle proprieta' di un OpenSPCoopProperties deve avere un nome univoco.
		Dump d = configurazione.getDump();
		if (d != null) {
			for (int j=0; j<d.sizeOpenspcoopAppenderList(); j++) {
				OpenspcoopAppender oa = d.getOpenspcoopAppender(j);
				
				// Controllo tipo
				if(oa.getTipo()==null){
					this.errori.add("E' stato definito un appender del dump per cui non e' stato definito il tipo");
				}
				else {
					
					// Controllo tipo
					if(this.tipoDumpAppender.contains(oa.getTipo())==false){
						this.errori.add("E' stato definito un appender del dump per cui e' stato definito un tipo["+oa.getTipo()+"] non valido, valori ammessi sono: "+this.getTipoDumpAppender());
					}
				
					for(int k=0; k<oa.sizePropertyList();k++){
						Property oap = oa.getProperty(k);
						
						if(oap.getNome()==null){
							this.errori.add("E' stato definito un appender["+oa.getTipo()+"] del dump per cui esiste una proprieta' senza un nome?");
						}
						else if(oap.getValore()==null){
							this.errori.add("E' stato definito un appender["+oa.getTipo()+"] del dump per cui esiste una proprieta' senza un valore?");
						}
						else{
							// Controllo univocita' nome
							String nomeOap = oap.getNome();
							int numOap = 0;
							for(int h=0; h<oa.sizePropertyList();h++){
								Property tmpOap = oa.getProperty(h);
								if (nomeOap.equals(tmpOap.getNome()))
									numOap++;
							}
							if (numOap > 1)
								this.errori.add("Non può esistere più di una proprietà con nome "+nomeOap+" per lo stesso appender["+oa.getTipo()+"] delle tracce");
						}
					}
					
				}
			}

			// XSD: buste: abilitato, disabilitato
			StatoFunzionalita statoDump = d.getStato();
			if ((statoDump != null) && !statoDump.equals(CostantiConfigurazione.ABILITATO) && !statoDump.equals(CostantiConfigurazione.DISABILITATO))
				this.errori.add("Il valore 'buste' della configurazione per il tracciamento delle buste deve assumere i valori "+CostantiConfigurazione.ABILITATO+
						" o "+CostantiConfigurazione.DISABILITATO);


		}
		

		// Gestione Errore
		if(configurazione.getGestioneErrore()!=null){
			if(configurazione.getGestioneErrore().getComponenteCooperazione()!=null){
				validaGestioneErrore(configurazione.getGestioneErrore().getComponenteCooperazione(),"ConfigurazioneGenerale-GestioneErroreComponenteCooperazione");
			}
			if(configurazione.getGestioneErrore().getComponenteIntegrazione()!=null){
				validaGestioneErrore(configurazione.getGestioneErrore().getComponenteIntegrazione(),"ConfigurazioneGenerale-GestioneErroreComponenteIntegrazione");
			}
		}
				
		
		// IntegrationManager
		IntegrationManager im = configurazione.getIntegrationManager();
		if (im != null && im.getAutenticazione()!=null){
			String[]v = im.getAutenticazione().split(",");
			if(v!=null && v.length>0){
				for(int l=0;l<v.length;l++){
					if(this.tipoAutenticazionePortaDelegata.contains(v[l].trim())==false){
						this.errori.add("Processo di autenticazione["+v[l].trim()+"] associato al servizio di IntegrationManager non conosciuto, valori ammessi: "+this.getTipoAutenticazionePortaDelegata());
					}
				}
			}
		}

		// Servizi
		StatoServiziPdd s = configurazione.getStatoServiziPdd();
		if(s!=null){
			
			// PortaDelegata
			if(s.getPortaDelegata()!=null){
				StatoServiziPddPortaDelegata sPD = s.getPortaDelegata();
				
				StatoFunzionalita portaDelegata = sPD.getStato();
				// XSD: abilitato, disabilitato
				if ((portaDelegata != null) && !portaDelegata.equals(CostantiConfigurazione.ABILITATO) && !portaDelegata.equals(CostantiConfigurazione.DISABILITATO))
					this.errori.add("Il valore 'stato' della configurazione per quanto concerne i servizi attivi sulla porta (PD) deve assumere i valori "+CostantiConfigurazione.ABILITATO+
							" o "+CostantiConfigurazione.DISABILITATO);
				
				validaFiltriStatoServiziPdD("FiltroAbilitazione", sPD.getFiltroAbilitazioneList());
				validaFiltriStatoServiziPdD("FiltroDisabilitazione", sPD.getFiltroDisabilitazioneList());
			}
			
			// PortaApplicativa
			if(s.getPortaApplicativa()!=null){
				StatoServiziPddPortaApplicativa sPA = s.getPortaApplicativa();
				
				StatoFunzionalita portaApplicativa = sPA.getStato();
				// XSD: abilitato, disabilitato
				if ((portaApplicativa != null) && !portaApplicativa.equals(CostantiConfigurazione.ABILITATO) && !portaApplicativa.equals(CostantiConfigurazione.DISABILITATO))
					this.errori.add("Il valore 'porta_applicativa' della configurazione per quanto concerne i servizi attivi sulla porta (PA) deve assumere i valori "+CostantiConfigurazione.ABILITATO+
							" o "+CostantiConfigurazione.DISABILITATO);
				
				validaFiltriStatoServiziPdD("FiltroAbilitazione", sPA.getFiltroAbilitazioneList());
				validaFiltriStatoServiziPdD("FiltroDisabilitazione", sPA.getFiltroDisabilitazioneList());
			}
			
			// IntegrationManager
			if(s.getIntegrationManager()!=null){
				StatoServiziPddIntegrationManager sIM = s.getIntegrationManager();
				
				StatoFunzionalita integrationManager = sIM.getStato();
				// XSD: abilitato, disabilitato
				if ((integrationManager != null) && !integrationManager.equals(CostantiConfigurazione.ABILITATO) && !integrationManager.equals(CostantiConfigurazione.DISABILITATO))
					this.errori.add("Il valore 'integration_manager' della configurazione per quanto concerne i servizi attivi sulla porta (IM) assumere i valori "+CostantiConfigurazione.ABILITATO+
							" o "+CostantiConfigurazione.DISABILITATO);
			}
			
		}
		
	}

	private void validaCache(Cache cache, String posizione){
		// Dimensione
		if(cache.getDimensione()!=null){
			try{
				Integer.parseInt(cache.getDimensione());
			}catch(Exception e){
				this.errori.add("Il valore associato alla dimensione della cache ["+posizione+"] dev'essere un numero intero");
			}
		}
		
		// XSD: algoritmo: lru, mru
		AlgoritmoCache algoritmo = cache.getAlgoritmo();
		if ((algoritmo != null) && !algoritmo.equals(CostantiConfigurazione.CACHE_LRU) && !algoritmo.equals(CostantiConfigurazione.CACHE_MRU))
			this.errori.add("L'algoritmo dell'accesso alla cache ["+posizione+"] dev'essere "+CostantiConfigurazione.CACHE_LRU+" o "+CostantiConfigurazione.CACHE_MRU);
		
		// ItemIdleTime
		if(cache.getItemIdleTime()!=null){
			try{
				Long.parseLong(cache.getItemIdleTime());
			}catch(Exception e){
				this.errori.add("Il valore associato all'item-idle-time della cache  ["+posizione+"] dev'essere un numero intero");
			}
		}
		
		// ItemLifeSecond
		if(cache.getItemLifeSecond()!=null){
			try{
				Long.parseLong(cache.getItemLifeSecond());
			}catch(Exception e){
				this.errori.add("Il valore associato all'item-life-seconds della cache ["+posizione+"] dev'essere un numero intero");
			}
		}
	}
	
	private void validaFiltriStatoServiziPdD(String tipologiaFiltri,List<TipoFiltroAbilitazioneServizi> lista){
		if(lista!=null){
			for (int j = 0; j < lista.size(); j++) {
				@SuppressWarnings("unused")
				TipoFiltroAbilitazioneServizi filtro = lista.get(j);
				
//				if ((filtro.getTipoSoggettoFruitore() != null) && !filtro.getTipoSoggettoFruitore().equals(CostantiConfigurazione.ABILITATO) && !filtro.getTipoSoggettoFruitore().equals(CostantiConfigurazione.DISABILITATO))
//					this.errori.add("Il valore 'tipo_soggetto_fruitore' del filtro["+j+"] (tipologia:"+tipologiaFiltri+") della configurazione per quanto concerne  i servizi attivi sulla porta deve assumere i valori "+CostantiConfigurazione.ABILITATO+
//							" o "+CostantiConfigurazione.DISABILITATO);
//				if ((filtro.getSoggettoFruitore() != null) && !filtro.getSoggettoFruitore().equals(CostantiConfigurazione.ABILITATO) && !filtro.getSoggettoFruitore().equals(CostantiConfigurazione.DISABILITATO))
//					this.errori.add("Il valore 'soggetto_fruitore' del filtro["+j+"] (tipologia:"+tipologiaFiltri+") della configurazione per quanto concerne  i servizi attivi sulla porta deve assumere i valori "+CostantiConfigurazione.ABILITATO+
//							" o "+CostantiConfigurazione.DISABILITATO);
//				if ((filtro.getIdentificativoPortaFruitore() != null) && !filtro.getIdentificativoPortaFruitore().equals(CostantiConfigurazione.ABILITATO) && !filtro.getIdentificativoPortaFruitore().equals(CostantiConfigurazione.DISABILITATO))
//					this.errori.add("Il valore 'identificativo_porta_fruitore' del filtro["+j+"] (tipologia:"+tipologiaFiltri+") della configurazione per quanto concerne  i servizi attivi sulla porta deve assumere i valori "+CostantiConfigurazione.ABILITATO+
//							" o "+CostantiConfigurazione.DISABILITATO);
//				
//				if ((filtro.getTipoSoggettoErogatore() != null) && !filtro.getTipoSoggettoErogatore().equals(CostantiConfigurazione.ABILITATO) && !filtro.getTipoSoggettoErogatore().equals(CostantiConfigurazione.DISABILITATO))
//					this.errori.add("Il valore 'tipo_soggetto_erogatore' del filtro["+j+"] (tipologia:"+tipologiaFiltri+") della configurazione per quanto concerne  i servizi attivi sulla porta deve assumere i valori "+CostantiConfigurazione.ABILITATO+
//							" o "+CostantiConfigurazione.DISABILITATO);
//				if ((filtro.getSoggettoErogatore() != null) && !filtro.getSoggettoErogatore().equals(CostantiConfigurazione.ABILITATO) && !filtro.getSoggettoErogatore().equals(CostantiConfigurazione.DISABILITATO))
//					this.errori.add("Il valore 'soggetto_erogatore' del filtro["+j+"] (tipologia:"+tipologiaFiltri+") della configurazione per quanto concerne  i servizi attivi sulla porta deve assumere i valori "+CostantiConfigurazione.ABILITATO+
//							" o "+CostantiConfigurazione.DISABILITATO);
//				if ((filtro.getIdentificativoPortaErogatore() != null) && !filtro.getIdentificativoPortaErogatore().equals(CostantiConfigurazione.ABILITATO) && !filtro.getIdentificativoPortaErogatore().equals(CostantiConfigurazione.DISABILITATO))
//					this.errori.add("Il valore 'identificativo_porta_erogatore' del filtro["+j+"] (tipologia:"+tipologiaFiltri+") della configurazione per quanto concerne  i servizi attivi sulla porta deve assumere i valori "+CostantiConfigurazione.ABILITATO+
//							" o "+CostantiConfigurazione.DISABILITATO);
//				
//				if ((filtro.getTipoServizio() != null) && !filtro.getTipoServizio().equals(CostantiConfigurazione.ABILITATO) && !filtro.getTipoServizio().equals(CostantiConfigurazione.DISABILITATO))
//					this.errori.add("Il valore 'tipo_servizio' del filtro["+j+"] (tipologia:"+tipologiaFiltri+") della configurazione per quanto concerne  i servizi attivi sulla porta deve assumere i valori "+CostantiConfigurazione.ABILITATO+
//							" o "+CostantiConfigurazione.DISABILITATO);
//				if ((filtro.getServizio() != null) && !filtro.getServizio().equals(CostantiConfigurazione.ABILITATO) && !filtro.getServizio().equals(CostantiConfigurazione.DISABILITATO))
//					this.errori.add("Il valore 'servizio' del filtro["+j+"] (tipologia:"+tipologiaFiltri+") della configurazione per quanto concerne  i servizi attivi sulla porta deve assumere i valori "+CostantiConfigurazione.ABILITATO+
//							" o "+CostantiConfigurazione.DISABILITATO);
//				
//				if ((filtro.getAzione() != null) && !filtro.getAzione().equals(CostantiConfigurazione.ABILITATO) && !filtro.getAzione().equals(CostantiConfigurazione.DISABILITATO))
//					this.errori.add("Il valore 'azione' del filtro["+j+"] (tipologia:"+tipologiaFiltri+") della configurazione per quanto concerne  i servizi attivi sulla porta deve assumere i valori "+CostantiConfigurazione.ABILITATO+
//							" o "+CostantiConfigurazione.DISABILITATO);
			}
		}
	}
	
	private void validaRotta(Route route,String tipo,AccessoRegistro ar) throws DriverConfigurazioneException{
		if(route.getGateway()==null && route.getRegistro()==null){
			this.errori.add("(RoutingTable) è stata definita una rotta "+tipo+" senza aver specificato ne un Gateway ne un Registro");
		}
		else if(route.getGateway()!=null && route.getRegistro()!=null){
			this.errori.add("(RoutingTable) è stata definita una rotta "+tipo+" che possiede sia un Gateway che un Registro (Solo un tipo deve essere selezionato)");
		}
		else if(route.getGateway()!=null){
			if(route.getGateway().getNome()==null){
				this.errori.add("(RoutingTable) è stata definita una rotta "+tipo+" senza aver specificato il nome del soggetto Gateway");
			}
			if(route.getGateway().getTipo()==null){
				this.errori.add("(RoutingTable) è stata definita una rotta "+tipo+" senza aver specificato il tipo del soggetto Gateway");
			}
		}else {
			if(route.getRegistro().getNome()!=null){
				if(ar!=null){
					// Il nome del registro non è obbligatorio: se non definito equivale a cercare in tutti
					boolean trovato = false;
					for(int i=0; i<ar.sizeRegistroList();i++){
						if (ar.getRegistro(i).getNome().equals(route.getRegistro().getNome())){
							trovato = true;
							break;
						}
					}
					if(!trovato){
						this.errori.add("(RoutingTable) è stata definita una rotta "+tipo+" per cui è stata specificato un registro ["+route.getRegistro().getNome()+"] non esistente neella configurazione");
					}
				}else{
					// errore gestito nella validazione della configurazione di accesso al registro
				}
			}
		}
	}
	
	private  void validaMessageSecurity(MessageSecurity messageSecurity,String idOggetto) throws DriverConfigurazioneException {
		// Ogni elemento definito nella RequestFlow deve avere un nome univoco.
		// Ogni elemento definito nella ResponseFlow deve avere un nome univoco.
		if(messageSecurity.getRequestFlow()!=null){
			MessageSecurityFlow messageSecurityFlow = messageSecurity.getRequestFlow();
			for (int j=0; j<messageSecurityFlow.sizeParameterList();j++) {
				MessageSecurityFlowParameter securityParameter = messageSecurityFlow.getParameter(j);
				
				if(securityParameter.getNome()==null){
					this.errori.add("("+idOggetto+" MessageSecurity) Nella request-flow esiste una proprieta' per cui non e' stato definito il nome");
				}
				if(securityParameter.getValore()==null){
					this.errori.add("("+idOggetto+" MessageSecurity) Nella request-flow esiste una proprieta' per cui non e' stato definito il valore");
				}
				
				int numRF = 0;
				for(int k=0; k<messageSecurityFlow.sizeParameterList();k++){
					MessageSecurityFlowParameter tmpSecurityParameter = messageSecurityFlow.getParameter(k);
					if ((securityParameter.getNome()).equals(tmpSecurityParameter.getNome()))
						numRF++;
				}
				if (numRF > 1)
					this.errori.add("("+idOggetto+" MessageSecurity) Nella request-flow non può esistere più di una proprieta' con lo stess nome "+securityParameter.getNome());
			}
		}
		if(messageSecurity.getResponseFlow()!=null){
			MessageSecurityFlow messageSecurityFlow = messageSecurity.getResponseFlow();
			for (int j=0; j<messageSecurityFlow.sizeParameterList();j++) {
				MessageSecurityFlowParameter securityParameter = messageSecurityFlow.getParameter(j);
				
				if(securityParameter.getNome()==null){
					this.errori.add("("+idOggetto+" MessageSecurity) Nella response-flow esiste una proprieta' per cui non e' stato definito il nome");
				}
				if(securityParameter.getValore()==null){
					this.errori.add("("+idOggetto+" MessageSecurity) Nella response-flow esiste una proprieta' per cui non e' stato definito il valore");
				}
				
				int numRF = 0;
				for(int k=0; k<messageSecurityFlow.sizeParameterList();k++){
					MessageSecurityFlowParameter tmpSecurityParameter = messageSecurityFlow.getParameter(k);
					if ((securityParameter.getNome()).equals(tmpSecurityParameter.getNome()))
						numRF++;
				}
				if (numRF > 1)
					this.errori.add("("+idOggetto+" MessageSecurity) Nella response-flow non può esistere più di una proprieta' con lo stess nome "+securityParameter.getNome());
			}
		}
	}
	
	private  void validaMTOM(MtomProcessor mtomProcessor,String idOggetto) throws DriverConfigurazioneException {
		// Ogni elemento definito nella RequestFlow deve avere un nome univoco.
		// Ogni elemento definito nella ResponseFlow deve avere un nome univoco.
		if(mtomProcessor.getRequestFlow()!=null){
			MtomProcessorFlow mtomtFlow = mtomProcessor.getRequestFlow();
			for (int j=0; j<mtomtFlow.sizeParameterList();j++) {
				MtomProcessorFlowParameter mtomtFlowParameter = mtomtFlow.getParameter(j);
				
				if(mtomtFlowParameter.getNome()==null){
					this.errori.add("("+idOggetto+" MTOM-Processor) Nella request-flow esiste una proprieta' per cui non e' stato definito il nome");
				}
				if(mtomtFlowParameter.getPattern()==null){
					this.errori.add("("+idOggetto+" MTOM-Processor) Nella request-flow esiste una proprieta' per cui non e' stato definito il valore");
				}
				
				int numRF = 0;
				for(int k=0; k<mtomtFlow.sizeParameterList();k++){
					MtomProcessorFlowParameter tmpMtomtFlowParameter = mtomtFlow.getParameter(k);
					if ((mtomtFlowParameter.getNome()).equals(tmpMtomtFlowParameter.getNome()))
						numRF++;
				}
				if (numRF > 1)
					this.errori.add("("+idOggetto+" MTOM-Processor) Nella request-flow non può esistere più di una proprieta' con lo stess nome "+mtomtFlowParameter.getNome());
			}
		}
		if(mtomProcessor.getResponseFlow()!=null){
			MtomProcessorFlow mtomFlow = mtomProcessor.getResponseFlow();
			for (int j=0; j<mtomFlow.sizeParameterList();j++) {
				MtomProcessorFlowParameter mtomtFlowParameter = mtomFlow.getParameter(j);
				
				if(mtomtFlowParameter.getNome()==null){
					this.errori.add("("+idOggetto+" MTOM-Processor) Nella response-flow esiste una proprieta' per cui non e' stato definito il nome");
				}
				if(mtomtFlowParameter.getPattern()==null){
					this.errori.add("("+idOggetto+" MTOM-Processor) Nella response-flow esiste una proprieta' per cui non e' stato definito il valore");
				}
				
				int numRF = 0;
				for(int k=0; k<mtomFlow.sizeParameterList();k++){
					MtomProcessorFlowParameter tmpMtomtFlowParameter = mtomFlow.getParameter(k);
					if ((mtomtFlowParameter.getNome()).equals(tmpMtomtFlowParameter.getNome()))
						numRF++;
				}
				if (numRF > 1)
					this.errori.add("("+idOggetto+" MTOM-Processor) Nella response-flow non può esistere più di una proprieta' con lo stess nome "+mtomtFlowParameter.getNome());
			}
		}
	}

	private  void validazioneValidazioneContenutiApplicativi(ValidazioneContenutiApplicativi vca,String tipo) throws DriverConfigurazioneException {
		// Se stato = disabilitato, il tipo puo' non  essere presente
		// Se stato = abilitato o warningOnly il tipo deve essere presente con uno dei valori definiti nel xsd
		StatoFunzionalitaConWarning stato = vca.getStato();
		if ((stato != null) && !stato.equals(CostantiConfigurazione.STATO_CON_WARNING_ABILITATO) && 
				!stato.equals(CostantiConfigurazione.STATO_CON_WARNING_DISABILITATO) && 
				!stato.equals(CostantiConfigurazione.STATO_CON_WARNING_WARNING_ONLY))
			this.errori.add("Lo stato della validazione contenuti applicativi ("+tipo+") deve possedere uno dei seguenti valori: "+CostantiConfigurazione.STATO_CON_WARNING_ABILITATO+
					", "+CostantiConfigurazione.STATO_CON_WARNING_DISABILITATO+
					" o "+CostantiConfigurazione.STATO_CON_WARNING_WARNING_ONLY);
			
		ValidazioneContenutiApplicativiTipo tipoV = vca.getTipo();
		// XSD: tipo: wsdl, openspcoop, xsd
		if ((tipoV != null) && !tipoV.equals(CostantiConfigurazione.VALIDAZIONE_CONTENUTI_APPLICATIVI_OPENSPCOOP) && 
				!tipoV.equals(CostantiConfigurazione.VALIDAZIONE_CONTENUTI_APPLICATIVI_INTERFACE) && 
				!tipoV.equals(CostantiConfigurazione.VALIDAZIONE_CONTENUTI_APPLICATIVI_XSD))
			this.errori.add("Il tipo di validazione contenuti applicativi da attuare ("+tipo+") deve possedere uno dei seguenti valori: " +
					CostantiConfigurazione.VALIDAZIONE_CONTENUTI_APPLICATIVI_XSD+", "+
					CostantiConfigurazione.VALIDAZIONE_CONTENUTI_APPLICATIVI_INTERFACE+" o "+
					CostantiConfigurazione.VALIDAZIONE_CONTENUTI_APPLICATIVI_OPENSPCOOP);
	}

	private  void validaProtocolProperty(Proprieta ssp, String oggetto) throws DriverConfigurazioneException {
		// Controlli sui valori definiti nell'xsd.

		if(ssp.getNome()==null){
			this.errori.add("Esiste una ProprietaProtocollo della porta "+oggetto+" che non contiene la definizione del nome");
		}
		if(ssp.getValore()==null){
			this.errori.add("Esiste una ProprietaProtocollo della porta "+oggetto+" che non contiene la definizione del valore");
		}
		
		// XSD: valore: mittente, tipoMittente, destinatario, tipoDestinatario, servizio, tipoServizio, azione, identificativo
		String valore = ssp.getValore();
		if(valore==null){
			this.errori.add("Il valore della ProprietaProtocollo "+ssp.getNome()+" della porta applicativa "+oggetto+" non e' valorizzato");
		}
		// Il check sopra esclude la possibilita' di avere un qualsiasi valore, possibilita' offerta anche dall'interfaccia grafica
		/*
		if ((valore != null) && !valore.equals(ProprietaProtocolloValore.MITTENTE.toString()) && 
				!valore.equals(ProprietaProtocolloValore.TIPO_MITTENTE.toString()) && 
				!valore.equals(ProprietaProtocolloValore.DESTINATARIO.toString()) && 
				!valore.equals(ProprietaProtocolloValore.TIPO_DESTINATARIO.toString()) && 
				!valore.equals(ProprietaProtocolloValore.SERVIZIO.toString()) && 
				!valore.equals(ProprietaProtocolloValore.TIPO_SERVIZIO.toString()) && 
				!valore.equals(ProprietaProtocolloValore.AZIONE.toString()) && 
				!valore.equals(ProprietaProtocolloValore.IDENTIFICATIVO.toString()))
			this.errori.add("Il valore della ProprietaProtocollo "+ssp.getNome()+" della porta applicativa "+oggetto+" dev'essere mittente, tipoMittente, destinatario, tipoDestinatario, servizio, tipoServizio, azione o identificativo");
			*/
	}

	private  void validaCredenziale(Credenziali c, String oggetto) throws DriverConfigurazioneException {
		// Se il tipo e' basic, username e password sono OBBLIGATORI
		
		CredenzialeTipo tipo = CostantiConfigurazione.CREDENZIALE_SSL;
		if(c.getTipo()!=null){
			tipo = c.getTipo();
		}
		if ( !tipo.equals(CostantiConfigurazione.CREDENZIALE_BASIC) && 
				!tipo.equals(CostantiConfigurazione.CREDENZIALE_APIKEY) && 
				!tipo.equals(CostantiConfigurazione.CREDENZIALE_SSL) && 
				!tipo.equals(CostantiConfigurazione.CREDENZIALE_PRINCIPAL))
			this.errori.add("Il tipo delle credenziali del "+oggetto+" deve possedere i valori: "+
				CostantiConfigurazione.CREDENZIALE_BASIC.toString()+" o "+
				CostantiConfigurazione.CREDENZIALE_APIKEY.toString()+" o "+
					CostantiConfigurazione.CREDENZIALE_SSL.toString()+" o "+
					CostantiConfigurazione.CREDENZIALE_PRINCIPAL.toString());
		
		
		if (c.getTipo().equals(CostantiConfigurazione.CREDENZIALE_BASIC)) {
			if ((c.getUser() == null) || (c.getUser().equals("")) || (c.getPassword() == null) || (c.getPassword().equals("")))
				this.errori.add("Le credenziali di tipo basic del "+oggetto+" devono avere username e password valorizzati");
		}
		if (c.getTipo().equals(CostantiConfigurazione.CREDENZIALE_APIKEY)) {
			if ((c.getUser() == null) || (c.getUser().equals("")) || (c.getPassword() == null) || (c.getPassword().equals("")))
				this.errori.add("Le credenziali di tipo apikey del "+oggetto+" devono avere username e password valorizzati");
		}

		// Se il tipo e' ssl, subject e' OBBLIGATORIO
		if (c.getTipo().equals(CostantiConfigurazione.CREDENZIALE_SSL)){
			if (
					(c.getSubject() == null || c.getSubject().equals(""))
					&&
					c.getCertificate()==null
			){
				this.errori.add("Le credenziali di tipo ssl del "+oggetto+" devono avere subject o certificate valorizzato");
			}else{
				if(c.getSubject()!=null && !"".equals(c.getSubject())) {
					try{
						CertificateUtils.validaPrincipal(c.getSubject(), PrincipalType.subject);
					}catch(Exception e){
						this.errori.add("Le credenziali di tipo ssl del "+oggetto+" possiedono un subject non valido: "+e.getMessage());
					}
				}
				if(c.getIssuer()!=null && !"".equals(c.getIssuer())) {
					try{
						CertificateUtils.validaPrincipal(c.getIssuer(), PrincipalType.issuer);
					}catch(Exception e){
						this.errori.add("Le credenziali di tipo ssl del "+oggetto+" possiedono un issuer non valido: "+e.getMessage());
					}
				}
			}
		}
			
		// Se il tipo e' principal, user e' OBBLIGATORIO
		if (c.getTipo().equals(CostantiConfigurazione.CREDENZIALE_PRINCIPAL)){
			if ((c.getUser() == null) || (c.getUser().equals(""))){
				this.errori.add("Le credenziali di tipo principal del "+oggetto+" devono avere user valorizzato");
			}
		}
		
	}
	
	private  void validaCredenziale(InvocazioneCredenziali c, String oggetto) throws DriverConfigurazioneException {
		// Se il tipo e' basic, username e password sono OBBLIGATORI
		
		if ((c.getUser() == null) || (c.getUser().equals("")) || (c.getPassword() == null) || (c.getPassword().equals("")))
			this.errori.add("Le credenziali di tipo basic del "+oggetto+" devono avere username e password valorizzati");
		
	}

	private void validaGestioneErrore(GestioneErrore ge,String tipo) throws DriverConfigurazioneException {
		
		// Ogni elemento codice-trasporto DEVE possedere ALMENO o un valore minimo o un valore massimo.
		for(int j=0; j<ge.sizeCodiceTrasportoList();j++){
			GestioneErroreCodiceTrasporto gect = ge.getCodiceTrasporto(j);
			if (gect.getValoreMinimo() == null || gect.getValoreMassimo() == null)
				this.errori.add("Gli elementi codice-trasporto devono possedere almeno un valore minimo o un valore massimo");

			if (gect.getValoreMinimo()!=null && gect.getValoreMinimo().longValue() <0){
				this.errori.add("Il valore minimo di un codice-trasporto associato alla gestione errore ("+tipo+"), dev'essere un numero intero >= 0 (Attualemente:"+gect.getValoreMinimo().longValue()+")");
			}
			if (gect.getValoreMassimo()!=null && gect.getValoreMassimo().longValue() <=0){
				this.errori.add("Il valore massimo di un codice-trasporto associato alla gestione errore ("+tipo+"), dev'essere un numero intero > 0 (Attualemente:"+gect.getValoreMassimo().longValue()+")");
			}
			if (gect.getValoreMassimo()!=null && gect.getValoreMinimo().longValue()>=0 && 
					gect.getValoreMinimo()!=null && gect.getValoreMassimo().longValue()>0){
				if(gect.getValoreMassimo().longValue()<gect.getValoreMinimo().longValue()){
					this.errori.add("Il valore massimo di un codice-trasporto associato alla gestione errore ("+tipo+"), dev'essere un numero intero maggiore del valore associate al valore minimo (Attualemente max:"+gect.getValoreMassimo().longValue()+" min:"+gect.getValoreMinimo().longValue()+")");
				}
			}
			
			// XSD: comportamento: accetta, rispedisci
			GestioneErroreComportamento comportamento = gect.getComportamento();
			if(comportamento==null){
				this.errori.add("Non e' stato definito il comportamento di adottato per la gestione errore ("+tipo+") per un codice-trasporto");
			}else{
				if ( !comportamento.equals(CostantiConfigurazione.GESTIONE_ERRORE_ACCETTA_MSG) && !comportamento.equals(CostantiConfigurazione.GESTIONE_ERRORE_RISPEDISCI_MSG))
					this.errori.add("Il comportamento associato alla gestione errore ("+tipo+"-CodiceTrasporto) deve assumere uno dei seguenti valori: "+
							CostantiConfigurazione.GESTIONE_ERRORE_ACCETTA_MSG+" o "+CostantiConfigurazione.GESTIONE_ERRORE_RISPEDISCI_MSG);
			}
			
			// Cadenza rispedizione
			if(gect.getCadenzaRispedizione()!=null){
				try{
					Long.parseLong(gect.getCadenzaRispedizione());
				}catch(Exception e){
					this.errori.add("Il valore della cadenza di rispedizione associato alla gestione errore ("+tipo+") per un codice-trasporto, dev'essere un numero intero");
				}
			}
		}

		// Ogni elemento soap-fault deve essere univoco. L'elemento viene identificato dalla tripla (fault-actor,fault-code,fault-string). In caso alcuni dei tre attributi (anche tutti) non sono definiti, per il controllo di univocita' devono essere considerati come elementi speciali 'NULL'
		for(int j=0; j<ge.sizeSoapFaultList();j++){
			
			GestioneErroreSoapFault gesf = ge.getSoapFault(j);
			
			String identificativo = "";
			if (gesf.getFaultActor() == null)
				identificativo = "NULL-";
			else
				identificativo = gesf.getFaultActor()+"-";
			if (gesf.getFaultCode() == null)
				identificativo = identificativo+"NULL-";
			else
				identificativo = identificativo+""+gesf.getFaultCode()+"-";
			if (gesf.getFaultString() == null)
				identificativo = identificativo+"NULL";
			else
				identificativo = identificativo+""+gesf.getFaultString();
			int numSF = 0;
			for (int k=0; k<ge.sizeSoapFaultList(); k++) {
				GestioneErroreSoapFault tmpGesf = ge.getSoapFault(k);
				String tmpId = "";
				if (tmpGesf.getFaultActor() == null)
					tmpId = "NULL-";
				else
					tmpId = tmpGesf.getFaultActor()+"-";
				if (tmpGesf.getFaultCode() == null)
					tmpId = tmpId+"NULL-";
				else
					tmpId = tmpId+""+tmpGesf.getFaultCode()+"-";
				if (tmpGesf.getFaultString() == null)
					tmpId = tmpId+"NULL";
				else
					tmpId = tmpId+""+tmpGesf.getFaultString();
				if (identificativo.equals(tmpId))
					numSF++;
			}
			if (numSF > 1)
				this.errori.add("Non può esistere più di un elemento soap-fault con identificativo "+identificativo+" associato alla gestione errore ("+tipo+")");

			// XSD: comportamento: accetta, rispedisci
			GestioneErroreComportamento comportamento = gesf.getComportamento();
			if(comportamento==null){
				this.errori.add("Non e' stato definito il comportamento di adottato per la gestione errore ("+tipo+") per un soap-fault");
			}else{
				if ( !comportamento.equals(CostantiConfigurazione.GESTIONE_ERRORE_ACCETTA_MSG) && !comportamento.equals(CostantiConfigurazione.GESTIONE_ERRORE_RISPEDISCI_MSG))
					this.errori.add("Il comportamento associato alla gestione errore ("+tipo+"-SoapFault) deve assumere uno dei seguenti valori: "+
							CostantiConfigurazione.GESTIONE_ERRORE_ACCETTA_MSG+" o "+CostantiConfigurazione.GESTIONE_ERRORE_RISPEDISCI_MSG);
			}
			
			// Cadenza rispedizione
			if(gesf.getCadenzaRispedizione()!=null){
				try{
					Long.parseLong(gesf.getCadenzaRispedizione());
				}catch(Exception e){
					this.errori.add("Il valore della cadenza di rispedizione associato alla gestione errore ("+tipo+") per un soap-fault, dev'essere un numero intero");
				}
			}
		}

		
		// XSD: comportamento: accetta, rispedisci
		GestioneErroreComportamento comportamento = ge.getComportamento();
		if(comportamento==null){
			this.errori.add("Non e' stato definito il comportamento di default adottato per la gestione errore ("+tipo+")");
		}else{
			if (!comportamento.equals(CostantiConfigurazione.GESTIONE_ERRORE_ACCETTA_MSG) && !comportamento.equals(CostantiConfigurazione.GESTIONE_ERRORE_RISPEDISCI_MSG))
				this.errori.add("Il comportamento di default associato alla gestione errore ("+tipo+") deve assumere uno dei seguenti valori: "+
						CostantiConfigurazione.GESTIONE_ERRORE_ACCETTA_MSG+" o "+CostantiConfigurazione.GESTIONE_ERRORE_RISPEDISCI_MSG);
		}
		
		// Cadenza rispedizione
		if(ge.getCadenzaRispedizione()!=null){
			try{
				Long.parseLong(ge.getCadenzaRispedizione());
			}catch(Exception e){
				this.errori.add("Il valore della cadenza di rispedizione di default associato alla gestione errore ("+tipo+"), dev'essere un numero intero");
			}
		}
	}







	/* ------------------ UTILITY ------------------------------*/

	/*
	private boolean existsSoggetto(String tipo,String nome){
		return this.getSoggetto(tipo, nome)!=null;
	}
	private Soggetto getSoggetto(String tipo,String nome){
		for(int j=0; j<this.configurazione.sizeSoggettoList();j++){
			Soggetto ss = this.configurazione.getSoggetto(j);
			if (tipo.equals(ss.getTipo()) && nome.equals(ss.getNome())) {
				return ss;
			}
		}
		return null;
	}*/
	
	private boolean existsServizioApplicativo(String nome,Soggetto soggetto){
		return this.getServizioApplicativo(nome,soggetto)!=null;
	}
	private ServizioApplicativo getServizioApplicativo(String nome,Soggetto soggetto){
		for(int j=0; j<soggetto.sizeServizioApplicativoList();j++){
			ServizioApplicativo sa = soggetto.getServizioApplicativo(j);
			if (nome.equals(sa.getNome())) {
				return sa;
			}
		}
		return null;
	}

}
