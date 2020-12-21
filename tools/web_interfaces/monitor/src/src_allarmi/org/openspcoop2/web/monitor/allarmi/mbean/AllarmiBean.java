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

package org.openspcoop2.web.monitor.allarmi.mbean;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.faces.event.ActionEvent;

import org.apache.commons.lang.StringUtils;
import org.openspcoop2.core.allarmi.AllarmeHistory;
import org.openspcoop2.core.allarmi.AllarmeMail;
import org.openspcoop2.core.allarmi.AllarmeParametro;
import org.openspcoop2.core.allarmi.AllarmeScript;
import org.openspcoop2.core.allarmi.IdAllarme;
import org.openspcoop2.core.allarmi.constants.RuoloPorta;
import org.openspcoop2.core.allarmi.constants.StatoAllarme;
import org.openspcoop2.core.allarmi.constants.TipoPeriodo;
import org.openspcoop2.core.allarmi.utils.AllarmiConverterUtils;
import org.openspcoop2.core.commons.search.AccordoServizioParteComune;
import org.openspcoop2.core.commons.search.PortaApplicativa;
import org.openspcoop2.core.commons.search.PortaDelegata;
import org.openspcoop2.core.commons.search.Resource;
import org.openspcoop2.core.constants.CostantiDB;
import org.openspcoop2.core.id.IDAccordo;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.core.registry.driver.IDAccordoFactory;
import org.openspcoop2.core.transazioni.utils.TipoCredenzialeMittente;
import org.openspcoop2.generic_project.exception.NotFoundException;
import org.openspcoop2.generic_project.exception.NotImplementedException;
import org.openspcoop2.generic_project.exception.ServiceException;
import org.openspcoop2.message.constants.ServiceBinding;
import org.openspcoop2.monitor.engine.alarm.AlarmConfigProperties;
import org.openspcoop2.monitor.engine.alarm.AlarmEngineConfig;
import org.openspcoop2.monitor.engine.alarm.AlarmImpl;
import org.openspcoop2.monitor.engine.alarm.AlarmManager;
import org.openspcoop2.monitor.engine.alarm.AlarmStatusWithAck;
import org.openspcoop2.monitor.engine.alarm.utils.AllarmiConfig;
import org.openspcoop2.monitor.engine.alarm.utils.AllarmiUtils;
import org.openspcoop2.monitor.engine.alarm.wrapper.ConfigurazioneAllarmeBean;
import org.openspcoop2.monitor.engine.config.base.Plugin;
import org.openspcoop2.monitor.sdk.alarm.IAlarm;
import org.openspcoop2.monitor.sdk.condition.Context;
import org.openspcoop2.monitor.sdk.constants.AlarmStateValues;
import org.openspcoop2.monitor.sdk.exceptions.AlarmException;
import org.openspcoop2.monitor.sdk.parameters.Parameter;
import org.openspcoop2.protocol.engine.ProtocolFactoryManager;
import org.openspcoop2.protocol.engine.utils.NamingUtils;
import org.openspcoop2.protocol.sdk.IProtocolFactory;
import org.openspcoop2.protocol.utils.PorteNamingUtils;
import org.openspcoop2.web.monitor.allarmi.bean.AllarmiContext;
import org.openspcoop2.web.monitor.allarmi.bean.AllarmiMonitorConfig;
import org.openspcoop2.web.monitor.allarmi.dao.IAllarmiService;
import org.openspcoop2.web.monitor.core.core.PddMonitorProperties;
import org.openspcoop2.web.monitor.core.core.Utility;
import org.openspcoop2.web.monitor.core.dao.DynamicUtilsService;
import org.openspcoop2.web.monitor.core.dao.IService;
import org.openspcoop2.web.monitor.core.dao.MBeanUtilsService;
import org.openspcoop2.web.monitor.core.logger.LoggerManager;
import org.openspcoop2.web.monitor.core.mbean.DynamicPdDBean;
import org.openspcoop2.web.monitor.core.utils.MessageUtils;
import org.slf4j.Logger;

/**
 * AllarmiBean 
 *
 * @author Giuliano Pintori (pintori@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class AllarmiBean extends
DynamicPdDBean<ConfigurazioneAllarmeBean, Integer, IService<ConfigurazioneAllarmeBean, Long>> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L; 
	private static Logger log = LoggerManager.getPddMonitorCoreLogger();

	private transient List<Parameter<?>> parameters;

	//private AllarmiSearchForm search;

	private ConfigurazioneAllarmeBean allarme;

	private String ack;

	private boolean modificatoInformazioniHistory = false;
	private boolean modificatoStato = false;
	private boolean modificatoAckwoldegment = false;
	private StatoAllarme modificatoInformazioniHistory_statoAllarme = null;
	private String modificatoInformazioniHistory_dettaglioAllarme = null;
	private Integer modificatoInformazioniHistory_ackwoldegmentAllarme = null;
	private StatoAllarme statoAllarmePrimaModifica = null;
	
	private AlarmEngineConfig alarmEngineConfig;
	private AllarmiConfig allarmiConfig;

	private boolean showFilter = true;
	private boolean showGroupBy = true;
	
	
	public boolean isShowFilter() throws Exception {
		if(this.allarme==null || this.allarme.getPlugin()==null){
			return false; // all'inizio deve prima essere scelto il plugin
		}
		
		this.showFilter = ((IAllarmiService)this.service).isUsableFilter(this.allarme);
		
		return this.showFilter;
	}

	public void setShowFilter(boolean showFilter) {
		this.showFilter = showFilter;
	}

	public boolean isShowGroupBy() throws Exception {
		if(this.allarme==null || this.allarme.getPlugin()==null){
			return false; // all'inizio deve prima essere scelto il plugin
		}
		
		this.showGroupBy = ((IAllarmiService)this.service).isUsableGroupBy(this.allarme);
		
		return this.showGroupBy;
	}

	public void setShowGroupBy(boolean showGroupBy) {
		this.showGroupBy = showGroupBy;
	}
	
	
	public AllarmiBean() {
		super();
		try {
			this.allarmiConfig = new AllarmiMonitorConfig(PddMonitorProperties.getInstance(log));
			this.alarmEngineConfig = AlarmConfigProperties.getAlarmConfiguration(log, PddMonitorProperties.getInstance(log).getAllarmiConfigurazione());
			
			if(AlarmManager.getAlarmEngineConfig() == null) {
				AlarmManager.setAlarmEngineConfig(this.alarmEngineConfig);
			}
		} catch (Throwable e) {
			AllarmiBean.log.error(e.getMessage(), e);
		}

	}
	
	public String getDescrizione(){

		if(this.allarme.getPlugin()!=null){
			return this.allarme.getPlugin().getDescrizione();
		}
		return "-";
	}
		
	public Plugin getPlugin() {
		return this.allarme.getPlugin();
	}

	public void setPlugin(Plugin infoPlugin) {
		this.allarme.setPlugin(infoPlugin);
	}
	
	public boolean isAllarmiConsultazioneModificaStatoAbilitata() {
		return this.allarmiConfig.isAllarmiConsultazioneModificaStatoAbilitata();
	}
	
	public boolean isAllarmiAssociazioneAcknowledgedStatoAllarme() {
		return this.allarmiConfig.isAllarmiAssociazioneAcknowledgedStatoAllarme();
	}
	
	public boolean isAllarmiNotificaMailVisualizzazioneCompleta() {
		return this.allarmiConfig.isAllarmiNotificaMailVisualizzazioneCompleta();
	}

	public boolean isAllarmiMonitoraggioEsternoVisualizzazioneCompleta() {
		return this.allarmiConfig.isAllarmiMonitoraggioEsternoVisualizzazioneCompleta();
	}

	public String getLabelStato(){
		return AllarmiUtils.getLabelStato(AllarmiConverterUtils.toStatoAllarme(this.allarme.getStato()));
	}
	public String getLabelPrecedenteStato(){
		return AllarmiUtils.getLabelStato(AllarmiConverterUtils.toStatoAllarme(this.allarme.getStatoPrecedente()));
	}

	public ConfigurazioneAllarmeBean getAllarme() {
		if (this.allarme == null){
			this.allarme = new ConfigurazioneAllarmeBean();

			//Configurazione default
			this.allarme.setEnabled(1);
			
			this.allarme.setTipoAllarme(null);

			this.allarme.setMail(new AllarmeMail());
			this.allarme.getMail().setInviaAlert(0);
			this.allarme.getMail().setInviaWarning(0);
			if(this.alarmEngineConfig.isMailAckMode()){
				this.allarme.getMail().setAckMode(1);
			}else{
				this.allarme.getMail().setAckMode(0);
			}

			this.allarme.setScript(new AllarmeScript());
			this.allarme.getScript().setInvocaAlert(0);
			this.allarme.getScript().setInvocaWarning(0);
			if(this.alarmEngineConfig.isScriptAckMode()){
				this.allarme.getScript().setAckMode(1);
			}else{
				this.allarme.getScript().setAckMode(0);
			}
			
			this.parameters=null;
			
			this.modificatoInformazioniHistory = false;
			this.modificatoStato = false;
			this.modificatoAckwoldegment = false;
			this.modificatoInformazioniHistory_statoAllarme = null;
			this.modificatoInformazioniHistory_dettaglioAllarme = null;
			this.modificatoInformazioniHistory_ackwoldegmentAllarme = null;
		}

		return this.allarme;
	}

	
	public void setModificaStato(int value){
		
		StatoAllarme nuovoStato = AllarmiConverterUtils.toStatoAllarme(value);
		if(this.modificatoInformazioniHistory_statoAllarme!=null){
			if(this.modificatoInformazioniHistory_statoAllarme.equals(nuovoStato)==false){
				this.modificatoInformazioniHistory = true;
				this.modificatoStato = true;
				
				// azzero ack
				this.setModificaAcknowledged(0);
			}
		}
		this.modificatoInformazioniHistory_statoAllarme = nuovoStato;
		this.allarme.setStato(value);
	}
	public int getModificaStato(){
		if(this.allarme.getStato()!=null){
			return this.allarme.getStato();
		}
		return -1;
	}
	
	public void setModificaDettaglioStato(String nuovoDettaglio){
		if(this.modificatoInformazioniHistory_dettaglioAllarme!=null){
			if(this.modificatoInformazioniHistory_dettaglioAllarme.equals(nuovoDettaglio)==false){
				this.modificatoInformazioniHistory = true;
			}
		}
		this.modificatoInformazioniHistory_dettaglioAllarme = nuovoDettaglio;
		this.allarme.setDettaglioStato(nuovoDettaglio);
	}
	public String getModificaDettaglioStato(){
		if(this.allarme.getStato()!=null){
			return this.allarme.getDettaglioStato();
		}
		return null;
	}
	
	public void setModificaAcknowledged(int nuovoAck){
		if(this.modificatoInformazioniHistory_ackwoldegmentAllarme!=null){
			if(this.modificatoInformazioniHistory_ackwoldegmentAllarme != nuovoAck){
				this.modificatoInformazioniHistory = true;
				this.modificatoAckwoldegment = true;
			}
		}
		this.modificatoInformazioniHistory_ackwoldegmentAllarme = nuovoAck;
		this.allarme.setAcknowledged(nuovoAck);
	}
	public int getModificaAcknowledged(){
		return this.allarme.getAcknowledged();
	}
	
	public void setAllarme(ConfigurazioneAllarmeBean allarme) {
		this.allarme = allarme;
		this.parameters = null;

		if(allarme.getId()!=null && allarme.getId()>0){
			this.modificatoInformazioniHistory = false;
			this.modificatoStato = false;
			this.modificatoAckwoldegment = false;
			this.modificatoInformazioniHistory_statoAllarme = AllarmiConverterUtils.toStatoAllarme(allarme.getStato());
			this.modificatoInformazioniHistory_dettaglioAllarme = allarme.getDettaglioStato();
			this.modificatoInformazioniHistory_ackwoldegmentAllarme = allarme.getAcknowledged();
			this.statoAllarmePrimaModifica = AllarmiConverterUtils.toStatoAllarme(allarme.getStato());
		}

	}

	public List<String> getStatusFilterValues() {

		ArrayList<String> f = null;
		if (f == null) {
			f = new ArrayList<String>();
			f.add("All");
			f.add("Non Disabilitato");
			f.add("Ok");
			f.add("Warn");
			f.add("Error");
			f.add("Disabilitato");
		}

		return f;
	}
	
	public List<String> getStatusFilterValuesWithAck() {

		ArrayList<String> f = null;
		if (f == null) {
			f = new ArrayList<String>();
			f.add("All");
			f.add("Non Disabilitato");
			f.add("Ok");
			f.add("Warn");
			f.add("Warn (Acknowledged)");
			f.add("Warn (Unacknowledged)");
			f.add("Error");
			f.add("Error (Acknowledged)");
			f.add("Error (Unacknowledged)");
			f.add("Disabilitato");
		}

		return f;
	}
	
	public List<String> getAcknowledgedFilterValues() {

		ArrayList<String> f = null;
		if (f == null) {
			f = new ArrayList<String>();
			f.add("All");
			f.add("Si");
			f.add("No");
		}

		return f;
	}

	public String getTotOk() {
		Long t = this.getTot("Ok",null);//this.getTot(0);
		return t != null ? t.toString() : "0";
	}

	public String getTotWarn() {
		Long t = this.getTot("Warn",null);//this.getTot(1);
		return t != null ? t.toString() : "0";
	}
	
	public String getTotWarnNoAck() {
		Long t = this.getTot("Warn",0);//this.getTot(1);
		return t != null ? t.toString() : "0";
	}
	
	public String getTotWarnAck() {
		Long t = this.getTot("Warn",1);//this.getTot(1);
		return t != null ? t.toString() : "0";
	}

	public String getTotError() {
		Long t = this.getTot("Error",null);//this.getTot(2);
		return t != null ? t.toString() : "0";
	}
	
	public String getTotErrorNoAck() {
		Long t = this.getTot("Error",0);//this.getTot(2);
		return t != null ? t.toString() : "0";
	}
	
	public String getTotErrorAck() {
		Long t = this.getTot("Error",1);//this.getTot(2);
		return t != null ? t.toString() : "0";
	}

	public String getTotDisabilitati() {
		Long t = this.getTot("Disabilitato",null);//this.getTot(2);
		return t != null ? t.toString() : "0";
	}

	private Long getTot(String stato, Integer acknowledged) {
		try {

			return ((IAllarmiService)this.service).getCountAllarmiByStato(stato,acknowledged);

		} catch (Exception e) {
			AllarmiBean.log.error(e.getMessage(), e);
		}

		return 0L;
	}

	public String getTipoPeriodo(){
		if(this.allarme.getTipoPeriodo()!=null){
			TipoPeriodo tp = AllarmiConverterUtils.toTipoPeriodo(this.allarme.getTipoPeriodo());
			switch (tp) {
			case M:
				return "Minuti";
			case H:
				return "Ore";
			case G:
				return "Giorni";
			}
		}
		// Default
		return "Ore";
	}

	public void setTipoPeriodo(String p){
		if("Minuti".equals(p)){
			this.allarme.setTipoPeriodo(AllarmiConverterUtils.toValue(TipoPeriodo.M));
		}
		else if("Giorni".equals(p)){
			this.allarme.setTipoPeriodo(AllarmiConverterUtils.toValue(TipoPeriodo.G));
		}
		else{
			this.allarme.setTipoPeriodo(AllarmiConverterUtils.toValue(TipoPeriodo.H));
		}
	}

	public List<Parameter<?>> getParameters() {

		if (this.parameters != null)
			return this.parameters;

		if(this.allarme==null || this.allarme.getTipo()==null){
			return null;
		}
		
		try{
			Context context = new AllarmiContext(this);
			this.parameters = ((IAllarmiService)this.service).instanceParameters(this.allarme,context);
			for (AllarmeParametro parDB : this.allarme.getAllarmeParametroList()) {
				for (Parameter<?> par : this.parameters) {
					if(parDB.getIdParametro().equals(par.getId())){
						par.setValueAsString(parDB.getValore());
						break;
					}
				}
			}
		}catch(Exception e){
			MessageUtils.addErrorMsg("Si e' verificato un errore. " + e.getMessage());
			//return null;
		}

		return this.parameters;
	}

	public String dettaglioAllarme(){
		return "allarme";
	}
	
	public String salva() {
		try {
				
			// N.B. I controlli sulla configurazione sono stati spostati nella console di gestione
			// i campi modificabili sono Stato , Dettaglio ed Ack
			// Stato puo' avere solo i 3 valori previsti
			// Dettaglio e' un testo senza vincoli
			// Ack puo' avere solo i 2 valori previsti.
			
			AllarmiBean.log.debug("Salvataggio Stato Allarme in corso...");
			ConfigurazioneAllarmeBean oldConfigurazioneAllarme = null;
			
			try {
				// Servirà per la gestione delle notifiche in caso di allarme Attivo
				oldConfigurazioneAllarme = ((IAllarmiService)this.service).getAllarme(this.allarme.getNome());
			} catch (NotFoundException e) {
				// do nothing
				AllarmiBean.log.debug("Allarme non presente");
			} catch (ServiceException e) {
				AllarmiBean.log
				.debug("Errore durante l'esecuzione del controllo di esistenza dell'allarme");
				MessageUtils
				.addErrorMsg("Si e' verificato un errore. Riprovare.");
				return null;
			}
			
			/* ******** GESTIONE NOTIFICA CAMBIO DI STATO *************** */
			
			boolean historyCreatoTramiteNotificaCambioStato = false;
			if(this.modificatoStato){
				this.notifyChangeState();
				historyCreatoTramiteNotificaCambioStato = true;
			}
			
			
			/* ******** SALVO ALLARME *************** */
			
			((IAllarmiService)this.service).store(this.allarme);

			
			/* ******** GESTIONE HISTORY *************** */
			
			if(this.modificatoInformazioniHistory && !historyCreatoTramiteNotificaCambioStato){
				// registro la modifica
				this.addHistory();
			}
			
			/* ******** GESTIONE AVVIO THREAD NEL CASO DI ATTIVO *************** */
			
			try {
				AllarmiUtils.notifyStateActiveThread(false, this.modificatoStato, this.modificatoAckwoldegment, oldConfigurazioneAllarme, this.allarme, AllarmiBean.log, this.allarmiConfig);
				MessageUtils.addInfoMsg("Allarme salvato con successo.");
			} catch (Exception e) {
				MessageUtils.addErrorMsg("Allarme salvato con successo, ma invio notifica terminato con errore: " + e.getMessage());
			}
		} catch (Exception e) {
			MessageUtils.addErrorMsg("Si e' verificato un errore. Riprovare.");
			AllarmiBean.log.error(e.getMessage(), e);
		}

		return "success";
	}

	private void addHistory() throws ServiceException, NotImplementedException{
		// registro la modifica
		AllarmeHistory history = new AllarmeHistory();
		history.setEnabled(this.allarme.getEnabled());
		history.setAcknowledged(this.allarme.getAcknowledged());
		history.setDettaglioStato(this.allarme.getDettaglioStato());
		IdAllarme idConfigurazioneAllarme = new IdAllarme();
		idConfigurazioneAllarme.setNome(this.allarme.getNome());
		history.setIdAllarme(idConfigurazioneAllarme);
		history.setStato(this.allarme.getStato());
		history.setTimestampUpdate(new Date());
		history.setUtente(Utility.getLoggedUtente().getLogin());
		((IAllarmiService)this.service).addHistory(history);
	}
	

	@Override
	public String delete(){
		return null;
	}
	
	private void notifyChangeState() throws AlarmException{
		IAlarm alarm = ((IAllarmiService)this.service).getAlarm(this.allarme.getNome());
		AlarmStatusWithAck alarmStatus = new AlarmStatusWithAck();
		StatoAllarme statoAllarme = AllarmiConverterUtils.toStatoAllarme(this.allarme.getStato());
		switch (statoAllarme) {
		case OK:
			alarmStatus.setStatus(AlarmStateValues.OK);	
			break;
		case WARNING:
			alarmStatus.setStatus(AlarmStateValues.WARNING);	
			break;
		case ERROR:
			alarmStatus.setStatus(AlarmStateValues.ERROR);	
			break;
		}
		alarmStatus.setDetail(this.allarme.getDettaglioStato());
		if(this.allarme.getAcknowledged()==1){
			alarmStatus.setAck(true);
		}
		else{
			alarmStatus.setAck(false);
		}
		if(alarm instanceof AlarmImpl){
			((AlarmImpl)alarm).setUsername(Utility.getLoggedUtente().getLogin());
		}
		alarm.changeStatus(alarmStatus);
		AllarmiBean.log.debug("Notificato cambio di stato all'allarme con nome ["+this.allarme.getNome()+"]");
		if(this.statoAllarmePrimaModifica!=null){
			this.allarme.setStatoPrecedente(AllarmiConverterUtils.toIntegerValue(this.statoAllarmePrimaModifica));
		}
	}
	
	public void toggleAck(ActionEvent ae) {
		try {
			if (this.ack == null)
				this.setModificaAcknowledged(0);
			else
				this.setModificaAcknowledged(Boolean.parseBoolean(this.ack) ? 1
						: 0);

			((IAllarmiService)this.service).store(this.allarme);
			
			// registro la modifica
			this.addHistory();
			
			// notifico avvio thread o stop
			AllarmiUtils.notifyStateActiveThread(false, this.modificatoStato, this.modificatoAckwoldegment, this.allarme, this.allarme, AllarmiBean.log, this.allarmiConfig);
			
		} catch (Exception e) {
			AllarmiBean.log.error(e.getMessage(), e);
		}
	}

	public String toggleAbilitato() {
		try {
			int val = this.allarme.getEnabled();
			if(val==0){
				this.allarme.setEnabled(1);
			}
			else{
				this.allarme.setEnabled(0);
			}
			
			((IAllarmiService)this.service).store(this.allarme);

			// registro la modifica
			this.addHistory();
			
			// notifico avvio thread o stop
			AllarmiUtils.notifyStateActiveThread(false, this.modificatoStato, this.modificatoAckwoldegment, null, this.allarme, AllarmiBean.log, this.allarmiConfig);

		} catch (Exception e) {
			AllarmiBean.log.error(e.getMessage(), e);
		}
		return null;
	}
	
	public String getAck() {
		return this.ack;
	}

	public void setAck(String ack) {
		this.ack = ack;
	}

	/**
	 * Listener eseguito prima di aggiungere un nuovo allarme
	 * 
	 * @param ae
	 */
	@Override
	public void addNewListener(ActionEvent ae) {
		this.allarme = null;
	}


//	public String getTipoNomeMittenteForConverter() {
//		String v = this.getTipoNomeMittente();
//		if(v==null){
//			return "*";
//		}
//		return v;
//	}
	public String getTipoNomeMittenteFiltro() {
		return AllarmiUtils.getTipoNomeMittente(this.allarme.getFiltro());
	}

//	public String getTipoNomeDestinatarioForConverter() {
//		String v = this.getTipoNomeDestinatario();
//		if(v==null){
//			return "*";
//		}
//		return v;
//	}
	public String getTipoNomeDestinatarioFiltro() {
		return AllarmiUtils.getTipoNomeDestinatario(this.allarme.getFiltro());
	}

//	public String getTipoNomeServizioForConverter() {
//		String v = this.getTipoNomeServizio();
//		if(v==null){
//			return "*";
//		}
//		return v;
//	}
	public String getTipoNomeServizioFiltro() {
		return AllarmiUtils.getTipoNomeServizio(this.allarme.getFiltro());
	}
	
//	public String getAzioneFiltro() {
//		return AllarmiUtils.getAzione(this.allarme.getFiltro());
//	}
	
	public String getProtocolloFiltro() {
		if(this.allarme.getFiltro() != null && this.allarme.getFiltro().getProtocollo() != null) {
			try {
				return NamingUtils.getLabelProtocollo(this.allarme.getFiltro().getProtocollo());
			} catch (Exception e) {
				return null;
			} 
		}
			
		return null;
	}
	
	public String getNomePortaFiltro() throws Exception {
		if(this.allarme.getFiltro() != null && this.allarme.getFiltro().getNomePorta() != null && this.allarme.getFiltro().getProtocollo() != null) {
			IProtocolFactory<?> protocolFactory = ProtocolFactoryManager.getInstance().getProtocolFactoryByName(this.allarme.getFiltro().getProtocollo());
			PorteNamingUtils n = new PorteNamingUtils(protocolFactory);
			switch (this.allarme.getFiltro().getRuoloPorta()) {
			case APPLICATIVA:
				return n.normalizePA(this.allarme.getFiltro().getNomePorta());
			case DELEGATA:
				return n.normalizePD(this.allarme.getFiltro().getNomePorta());
			case ENTRAMBI:
			default:
				return this.allarme.getFiltro().getNomePorta();
			}
		}
			
		return null;
	}
	
	public String getRuoloPortaFiltro() {
		if(this.allarme.getFiltro() != null && this.allarme.getFiltro().getRuoloPorta() != null) {
			try {
				switch (this.allarme.getFiltro().getRuoloPorta()) {
				case APPLICATIVA:
					return "Erogazione";
				case DELEGATA:
					return "Fruizione";
				case ENTRAMBI:
					return "Qualsiasi";
				}
			} catch (Exception e) {
				return "Qualsiasi";
			} 
		}
			
		return "Qualsiasi";
	}
	
	public boolean isVisualizzaRuoloPortaGroupBy() {
		if(this.isAllarmeConfigurazione()) {
			return this.allarme.getFiltro()==null || this.allarme.getFiltro().isEnabled()==false || 
					this.allarme.getFiltro().getRuoloPorta()==null || RuoloPorta.ENTRAMBI.equals(this.allarme.getFiltro().getRuoloPorta());
		}
		
		return false;
	}
	
	public boolean isVisualizzaProtocolloGroupBy() throws Exception {
		if(this.isAllarmeConfigurazione()) {
			List<String> protocolli = Utility.getProtocolli(Utility.getLoggedUtente());
			return protocolli.size()>1 && (this.allarme.getFiltro()==null || 
					this.allarme.getFiltro().isEnabled()==false || 
							this.allarme.getFiltro().getProtocollo()==null);
		}
		
		return false;
	}
	
	public boolean isVisualizzaSoggettoErogatoreGroupBy() {
		if(this.isAllarmeConfigurazione()) {
			return this.allarme.getFiltro()==null || 
					this.allarme.getFiltro().isEnabled()==false || 
							this.allarme.getFiltro().getTipoErogatore()==null ||
							this.allarme.getFiltro().getNomeErogatore()==null;
		}
		
		return false;
	}
	
	public boolean isVisualizzaServizioGroupBy() {
		if(this.isAllarmeConfigurazione()) {
			if(this.allarmiConfig.isAllarmiGroupByApi()) {
				return this.allarme.getFiltro()==null || 
						this.allarme.getFiltro().isEnabled()==false || 
								this.allarme.getFiltro().getTipoServizio()==null ||
										this.allarme.getFiltro().getNomeServizio()==null;
			}
		}
		
		return false;
	}
	
	public boolean isVisualizzaAzioneGroupBy() {
		boolean showAzione = this.allarme.getFiltro()==null || 
				this.allarme.getFiltro().isEnabled()==false || 
				this.allarme.getFiltro().getAzione()==null;
		
		if(showAzione) {
			showAzione = this.isVisualizzaServizioGroupBy() && this.allarme.getGroupBy().isServizio(); // l'azione la scelgo se ho prima selezionato una API
		}
		
		return showAzione;
	}
	
	public String getLabelAzioneGroupBy() {
		if(this.allarme.getFiltro() != null) {
			boolean configurazione = isAllarmeConfigurazione();
			boolean definedApi = this.allarme.getFiltro().getTipoServizio()!=null && this.allarme.getFiltro().getNomeServizio()!=null && this.allarme.getFiltro().getVersioneServizio()!=null;
			ServiceBinding serviceBinding = null;
			if(configurazione) { // in configurazione deve essere selezionato il filtro sull'API
				if(definedApi) {
					IDSoggetto idSoggetto = new IDSoggetto(this.allarme.getFiltro().getTipoErogatore(), this.allarme.getFiltro().getNomeErogatore()); 
					AccordoServizioParteComune accordoServizio = this.dynamicUtilsService.getAccordoServizio(this.allarme.getFiltro().getProtocollo(), idSoggetto , this.allarme.getFiltro().getTipoServizio(), 
							this.allarme.getFiltro().getNomeServizio(), this.allarme.getFiltro().getVersioneServizio());
							
					serviceBinding = ServiceBinding.valueOf(accordoServizio.getServiceBinding().toUpperCase());
					
				}
			} else {
				if(this.isRuoloPortaDelegata()) {
					PortaDelegata portaDelegata = this.dynamicUtilsService.getPortaDelegata(this.allarme.getFiltro().getNomePorta());
					
					IDSoggetto idSoggetto = new IDSoggetto(portaDelegata.getTipoSoggettoErogatore(), portaDelegata.getNomeSoggettoErogatore()); 
					AccordoServizioParteComune accordoServizio = this.dynamicUtilsService.getAccordoServizio(this.allarme.getFiltro().getProtocollo(), idSoggetto , portaDelegata.getTipoServizio(), 
							portaDelegata.getNomeServizio(), portaDelegata.getVersioneServizio());
							
					serviceBinding = ServiceBinding.valueOf(accordoServizio.getServiceBinding().toUpperCase());
				} else if(this.isRuoloPortaApplicativa()) {
					PortaApplicativa portaApplicativa = this.dynamicUtilsService.getPortaApplicativa(this.allarme.getFiltro().getNomePorta());
					
					IDSoggetto idSoggetto = new IDSoggetto(portaApplicativa.getIdSoggetto().getTipo(), portaApplicativa.getIdSoggetto().getNome()); 
					AccordoServizioParteComune accordoServizio = this.dynamicUtilsService.getAccordoServizio(this.allarme.getFiltro().getProtocollo(), idSoggetto , portaApplicativa.getTipoServizio(), 
							portaApplicativa.getNomeServizio(), portaApplicativa.getVersioneServizio());
							
					serviceBinding = ServiceBinding.valueOf(accordoServizio.getServiceBinding().toUpperCase());
				}  
			}
			
			if(serviceBinding!=null) {
				if(ServiceBinding.REST.equals(serviceBinding)) {
					return "Risorsa";
				} else {
					return "Azione";
				}
			}
		}
		
		return "Azione";
	}
	
	public boolean isVisualizzaSoggettoFruitoreGroupBy() {
		if(this.isAllarmeConfigurazione()) {
			return this.allarme.getFiltro()==null || 
					this.allarme.getFiltro().isEnabled()==false || 
							this.allarme.getFiltro().getTipoFruitore()==null ||
									this.allarme.getFiltro().getNomeFruitore()==null;
		}
		
		return false;
	}
	
	public boolean isVisualizzaServizioApplicativoFruitoreGroupBy() {
		if(this.isAllarmeConfigurazione()) {
			return this.allarme.getFiltro()==null || 
					this.allarme.getFiltro().isEnabled()==false || 
							this.allarme.getFiltro().getRuoloPorta()==null ||
							this.allarme.getFiltro().getServizioApplicativoFruitore()==null;
		}
		
		return false;
	}
	
	public boolean isVisualizzaIdentificativoAutenticatoGroupBy() {
		return !this.isAllarmeConfigurazione();
	}
	
	public boolean isVisualizzaTokenGroupBy() {
		boolean visualizzaToken = true;
		
		// TODO mancano i field per poter decidere lo stato del token
//		if(this.allarme.getFiltro() != null && this.allarme.getFiltro().getRuoloPorta() != null) {
//			if(this.isRuoloPortaApplicativa()) {
//				PortaApplicativa portaApplicativa = this.dynamicUtilsService.getPortaApplicativa(this.allarme.getFiltro().getNomePorta());
//				
//				if(portaApplicativa.getGestioneToken()!=null) {
//					String gestioneTokenPolicy = portaApplicativa.getGestioneToken().getPolicy();
//					if(	gestioneTokenPolicy == null ||
//							gestioneTokenPolicy.equals("") ||
//							gestioneTokenPolicy.equals(CostantiControlStation.DEFAULT_VALUE_NON_SELEZIONATO)) {
//						visualizzaToken = false;
//					}						
//				}
//				else {
//					visualizzaToken = false;
//				}
//			}
//			if(this.isRuoloPortaDelegata()) {
//				PortaDelegata portaDelegata = this.dynamicUtilsService.getPortaDelegata(this.allarme.getFiltro().getNomePorta());
//				
//				if(portaDelegata.getGestioneToken()!=null) {
//					String gestioneTokenPolicy = portaDelegata.getGestioneToken().getPolicy();
//					if(	gestioneTokenPolicy == null ||
//							gestioneTokenPolicy.equals("") ||
//							gestioneTokenPolicy.equals(CostantiControlStation.DEFAULT_VALUE_NON_SELEZIONATO)) {
//						visualizzaToken = false;
//					}						
//				}
//				else {
//					visualizzaToken = false;
//				}
//			}
//		}
		return visualizzaToken;
	}
	
	public boolean isVisualizzaRuoloErogatoreFiltro() {
		boolean configurazione = this.isAllarmeConfigurazione();
		return configurazione;
	}
	
	public boolean isVisualizzaSoggettoErogatoreFiltro() {
		boolean configurazione = this.isAllarmeConfigurazione();
		return configurazione;
	}
	
	public boolean isVisualizzaTagFiltro() {
		boolean configurazione = this.isAllarmeConfigurazione();
		return configurazione;
	}
	
	public boolean isVisualizzaServizioFiltro() {
		boolean configurazione = this.isAllarmeConfigurazione();
		return configurazione;
	}
	
	public boolean isVisualizzaAzioneFiltro() {
		boolean configurazione = isAllarmeConfigurazione();
		
		boolean showAzione = true;
		boolean definedApi = this.allarme.getFiltro().getTipoServizio()!=null && this.allarme.getFiltro().getNomeServizio()!=null && this.allarme.getFiltro().getVersioneServizio()!=null;
		if(configurazione) {
			if(!definedApi) {
				showAzione = false;
			}
		}
		
		return showAzione;
	}
	
	public boolean isVisualizzaRuoloFruitoreFiltro() {
		if(this.allarme.getFiltro() != null) {
			boolean configurazione = isAllarmeConfigurazione();
			boolean showRuoloRichiedente = false;
			if(configurazione) {
				showRuoloRichiedente = true;
			}
//			else {
//				if(serviziApplicativiFruitoreValue!=null && serviziApplicativiFruitoreValue.size()>1){
//					showRuoloRichiedente = true;
//				}
//				else if(fruitoriValue!=null && fruitoriValue.size()>1){
//					showRuoloRichiedente = true;
//				}
//			}
			
			
			if((this.allarme.getFiltro().getTipoFruitore()!=null && this.allarme.getFiltro().getNomeFruitore()!=null) ||
					this.allarme.getFiltro().getServizioApplicativoFruitore() != null || !showRuoloRichiedente) {
				return false;
			} else {
				return true;
			}
		}
		return false;
	}
	
	public boolean isVisualizzaSoggettoFruitoreFiltro() {
		boolean configurazione = this.isAllarmeConfigurazione();
		boolean applicativa = this.isRuoloPortaApplicativa();
		
		if(configurazione || applicativa) {
			return true;
		}
		
		return false;
	}
	
	public boolean isVisualizzaSaFruitoreFiltro() {
		boolean configurazione = this.isAllarmeConfigurazione();
		if(this.allarme.getFiltro() != null) {
			return !configurazione || (this.allarme.getFiltro().getTipoFruitore()!=null && this.allarme.getFiltro().getNomeFruitore()!=null);
		}
		
		return false;
	}
	
	public String getSaFruitoreFiltro() {
		if(this.allarme.getFiltro() != null) {
			return this.allarme.getFiltro().getServizioApplicativoFruitore();
		}
		
		return null;
	}

	public boolean isAllarmeConfigurazione() {
		return !this.isRuoloPortaDelegata() && !this.isRuoloPortaApplicativa();
	}
	
	public String getLabelAzioneFiltro() {
		if(this.allarme.getFiltro() != null) {
			boolean configurazione = isAllarmeConfigurazione();
			boolean definedApi = this.allarme.getFiltro().getTipoServizio()!=null && this.allarme.getFiltro().getNomeServizio()!=null && this.allarme.getFiltro().getVersioneServizio()!=null;
			ServiceBinding serviceBinding = null;
			if(configurazione) { // in configurazione deve essere selezionato il filtro sull'API
				if(definedApi) {
					IDSoggetto idSoggetto = new IDSoggetto(this.allarme.getFiltro().getTipoErogatore(), this.allarme.getFiltro().getNomeErogatore()); 
					AccordoServizioParteComune accordoServizio = this.dynamicUtilsService.getAccordoServizio(this.allarme.getFiltro().getProtocollo(), idSoggetto , this.allarme.getFiltro().getTipoServizio(), 
							this.allarme.getFiltro().getNomeServizio(), this.allarme.getFiltro().getVersioneServizio());
							
					serviceBinding = ServiceBinding.valueOf(accordoServizio.getServiceBinding().toUpperCase());
					
				}
			} else {
				if(this.isRuoloPortaDelegata()) {
					PortaDelegata portaDelegata = this.dynamicUtilsService.getPortaDelegata(this.allarme.getFiltro().getNomePorta());
					
					IDSoggetto idSoggetto = new IDSoggetto(portaDelegata.getTipoSoggettoErogatore(), portaDelegata.getNomeSoggettoErogatore()); 
					AccordoServizioParteComune accordoServizio = this.dynamicUtilsService.getAccordoServizio(this.allarme.getFiltro().getProtocollo(), idSoggetto , portaDelegata.getTipoServizio(), 
							portaDelegata.getNomeServizio(), portaDelegata.getVersioneServizio());
							
					serviceBinding = ServiceBinding.valueOf(accordoServizio.getServiceBinding().toUpperCase());
				} else if(this.isRuoloPortaApplicativa()) {
					PortaApplicativa portaApplicativa = this.dynamicUtilsService.getPortaApplicativa(this.allarme.getFiltro().getNomePorta());
					
					IDSoggetto idSoggetto = new IDSoggetto(portaApplicativa.getIdSoggetto().getTipo(), portaApplicativa.getIdSoggetto().getNome()); 
					AccordoServizioParteComune accordoServizio = this.dynamicUtilsService.getAccordoServizio(this.allarme.getFiltro().getProtocollo(), idSoggetto , portaApplicativa.getTipoServizio(), 
							portaApplicativa.getNomeServizio(), portaApplicativa.getVersioneServizio());
							
					serviceBinding = ServiceBinding.valueOf(accordoServizio.getServiceBinding().toUpperCase());
				}  
			}
			
			if(serviceBinding!=null) {
				if(ServiceBinding.REST.equals(serviceBinding)) {
					return "Risorse";
				} else {
					return "Azioni";
				}
			}
		}
		
		return "Azioni";
	}
	
	public String getAzioneFiltro() {
		
		try {
		if(this.allarme.getFiltro() != null && this.allarme.getFiltro().getAzione() != null) {
			boolean configurazione = isAllarmeConfigurazione();
			boolean definedApi = this.allarme.getFiltro().getTipoServizio()!=null && this.allarme.getFiltro().getNomeServizio()!=null && this.allarme.getFiltro().getVersioneServizio()!=null;
			ServiceBinding serviceBinding = null;
			AccordoServizioParteComune accordoServizio = null;
			if(configurazione) { // in configurazione deve essere selezionato il filtro sull'API
				if(definedApi) {
					IDSoggetto idSoggetto = new IDSoggetto(this.allarme.getFiltro().getTipoErogatore(), this.allarme.getFiltro().getNomeErogatore()); 
					accordoServizio = this.dynamicUtilsService.getAccordoServizio(this.allarme.getFiltro().getProtocollo(), idSoggetto , this.allarme.getFiltro().getTipoServizio(), 
							this.allarme.getFiltro().getNomeServizio(), this.allarme.getFiltro().getVersioneServizio());
							
					serviceBinding = ServiceBinding.valueOf(accordoServizio.getServiceBinding().toUpperCase());
					
				}
			} else {
				if(this.isRuoloPortaDelegata()) {
					PortaDelegata portaDelegata = this.dynamicUtilsService.getPortaDelegata(this.allarme.getFiltro().getNomePorta());
					
					IDSoggetto idSoggetto = new IDSoggetto(portaDelegata.getTipoSoggettoErogatore(), portaDelegata.getNomeSoggettoErogatore()); 
					accordoServizio = this.dynamicUtilsService.getAccordoServizio(this.allarme.getFiltro().getProtocollo(), idSoggetto , portaDelegata.getTipoServizio(), 
							portaDelegata.getNomeServizio(), portaDelegata.getVersioneServizio());
							
					serviceBinding = ServiceBinding.valueOf(accordoServizio.getServiceBinding().toUpperCase());
				} else if(this.isRuoloPortaApplicativa()) {
					PortaApplicativa portaApplicativa = this.dynamicUtilsService.getPortaApplicativa(this.allarme.getFiltro().getNomePorta());
					
					IDSoggetto idSoggetto = new IDSoggetto(portaApplicativa.getIdSoggetto().getTipo(), portaApplicativa.getIdSoggetto().getNome()); 
					accordoServizio = this.dynamicUtilsService.getAccordoServizio(this.allarme.getFiltro().getProtocollo(), idSoggetto , portaApplicativa.getTipoServizio(), 
							portaApplicativa.getNomeServizio(), portaApplicativa.getVersioneServizio());
							
					serviceBinding = ServiceBinding.valueOf(accordoServizio.getServiceBinding().toUpperCase());
				}  
			}
			
			if(serviceBinding!=null) {
				if(ServiceBinding.REST.equals(serviceBinding)) {
					if(!"".equals(this.allarme.getFiltro().getAzione())) {
						MBeanUtilsService mBeanUtilsService = new MBeanUtilsService(((DynamicUtilsService)this.dynamicUtilsService).getUtilsServiceManager(), log);
						String [] azioniSelezionateDB = this.allarme.getFiltro().getAzione().split(",");
						
						List<String> l = new ArrayList<>();
						if(azioniSelezionateDB!=null && azioniSelezionateDB.length>0) {
							for (int i = 0; i < azioniSelezionateDB.length; i++) {
								String op = azioniSelezionateDB[i];
								String operazioneLabel = null;
								
								IDAccordo idAccordo = IDAccordoFactory.getInstance().getIDAccordoFromValues(accordoServizio.getNome(),
										accordoServizio.getIdReferente().getTipo(), accordoServizio.getIdReferente().getNome(), accordoServizio.getVersione()); 
								List<Map<String,Object>> listaInfoCache = mBeanUtilsService.getInfoOperazioneFromCache(op, idAccordo);
								if(listaInfoCache!=null && listaInfoCache.size()==1) {
									Map<String,Object> map = listaInfoCache.get(0);
									String method = (String) map.get(Resource.model().HTTP_METHOD.getFieldName());
									String path = (String) map.get(Resource.model().PATH.getFieldName());
									//System.out.println("LETTO ["+method+"] ["+path+"]");
									StringBuilder bf = new StringBuilder();
									if(!CostantiDB.API_RESOURCE_HTTP_METHOD_ALL_VALUE.equals(method)) {
										bf.append(method);
										bf.append(" ");
									}
									if(!CostantiDB.API_RESOURCE_PATH_ALL_VALUE.equals(path)) {
										bf.append(path);
										operazioneLabel = bf.toString();
										
									}
								}
								
								if(operazioneLabel != null) {
									l.add(operazioneLabel);
								}
							
							}
							if(!l.isEmpty()) {
								return StringUtils.join(l.toArray(new String[1]), ", ");
							}
						}
					}
				} else {
					return this.allarme.getFiltro().getAzione();
				}
			}
		}
		}catch (Exception e) {
			log.error(e.getMessage(),e);
		}
		
		return this.allarme.getFiltro().getAzione();
	}

	public boolean isRuoloPortaApplicativa() {
		boolean applicativa = false;
		
		if(this.allarme.getFiltro() != null && this.allarme.getFiltro().getRuoloPorta() != null) {
			if(RuoloPorta.APPLICATIVA.equals(this.allarme.getFiltro().getRuoloPorta())) {
				applicativa = (this.allarme.getFiltro().getNomePorta()!=null);
			}
		}
		
		return applicativa;
	}
	
	public boolean isRuoloPortaDelegata() {
		boolean delegata = false;
		
		if(this.allarme.getFiltro() != null && this.allarme.getFiltro().getRuoloPorta() != null) {
			if(RuoloPorta.DELEGATA.equals(this.allarme.getFiltro().getRuoloPorta())) {
				delegata = (this.allarme.getFiltro().getNomePorta()!=null);
			}
		}
		
		return delegata;
	}

	public String getClaimsGroupBy() {
		if(this.allarme.getGroupBy() != null && this.allarme.getGroupBy().getToken() != null) {
			try {
				String [] tokenSelezionatiDB = null;
				if(this.allarme.getGroupBy().getToken()!=null && !"".equals(this.allarme.getGroupBy().getToken())) {
					tokenSelezionatiDB = this.allarme.getGroupBy().getToken().split(",");
				}
				if(tokenSelezionatiDB!=null && tokenSelezionatiDB.length>0) {
					List<String> l = new ArrayList<>();
					for (int i = 0; i < tokenSelezionatiDB.length; i++) {
						TipoCredenzialeMittente tipo = TipoCredenzialeMittente.valueOf(tokenSelezionatiDB[i]);
						
						switch (tipo) {
						case token_subject:
							l.add("Subject");
							break;
						case token_username:
							l.add("Username");
							break;
						case token_clientId:
							l.add("ClientId");
							break;
						case token_eMail:
							l.add("eMail");
							break;
						case client_address:
						case eventi:
						case gruppi:
						case token_issuer:
						case trasporto:
						default:
							// non selezionabilit sulla console
							break;
						}
					}
					if(!l.isEmpty()) {
						return StringUtils.join(l.toArray(new String[1]), ", ");
					}
				}
				
				return NamingUtils.getLabelProtocollo(this.allarme.getFiltro().getProtocollo());
			} catch (Exception e) {
				return null;
			} 
		}
			
		return null;
	}
}
