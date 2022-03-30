/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2022 Link.it srl (https://link.it).
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

package org.openspcoop2.monitor.engine.alarm;

import java.util.ArrayList;
import java.util.List;

import org.openspcoop2.core.allarmi.Allarme;
import org.openspcoop2.core.allarmi.AllarmeHistory;
import org.openspcoop2.core.allarmi.AllarmeNotifica;
import org.openspcoop2.core.allarmi.AllarmeParametro;
import org.openspcoop2.core.allarmi.IdAllarme;
import org.openspcoop2.core.allarmi.constants.StatoAllarme;
import org.openspcoop2.core.allarmi.dao.IAllarmeHistoryService;
import org.openspcoop2.core.allarmi.dao.IAllarmeNotificaService;
import org.openspcoop2.core.allarmi.dao.IAllarmeService;
import org.openspcoop2.core.allarmi.dao.IAllarmeServiceSearch;
import org.openspcoop2.core.allarmi.dao.IServiceManager;
import org.openspcoop2.core.allarmi.utils.AllarmiConverterUtils;
import org.openspcoop2.core.allarmi.utils.ProjectInfo;
import org.openspcoop2.core.commons.dao.DAOFactory;
import org.openspcoop2.core.plugins.IdPlugin;
import org.openspcoop2.core.plugins.Plugin;
import org.openspcoop2.core.plugins.constants.TipoPlugin;
import org.openspcoop2.generic_project.exception.NotFoundException;
import org.openspcoop2.generic_project.expression.IExpression;
import org.openspcoop2.monitor.engine.constants.CostantiConfigurazione;
import org.openspcoop2.monitor.engine.dynamic.DynamicFactory;
import org.openspcoop2.monitor.engine.dynamic.IDynamicLoader;
import org.openspcoop2.monitor.sdk.alarm.AlarmStatus;
import org.openspcoop2.monitor.sdk.alarm.IAlarm;
import org.openspcoop2.monitor.sdk.alarm.IAlarmLogger;
import org.openspcoop2.monitor.sdk.constants.AlarmStateValues;
import org.openspcoop2.monitor.sdk.exceptions.AlarmException;
import org.openspcoop2.monitor.sdk.exceptions.AlarmNotifyException;
import org.openspcoop2.monitor.sdk.parameters.Parameter;
import org.openspcoop2.monitor.sdk.plugins.IAlarmProcessing;
import org.openspcoop2.utils.UtilsMultiException;
import org.openspcoop2.utils.date.DateManager;
import org.openspcoop2.utils.mail.Mail;
import org.openspcoop2.utils.mail.Sender;
import org.openspcoop2.utils.mail.SenderFactory;
import org.openspcoop2.utils.resources.ScriptInvoker;
import org.slf4j.Logger;


/**
 * AlarmManager
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class AlarmManager {


	private static AlarmEngineConfig alarmEngineConfig;	
	public static AlarmEngineConfig getAlarmEngineConfig() {
		return AlarmManager.alarmEngineConfig;
	}
	public static void setAlarmEngineConfig(AlarmEngineConfig alarmEngineConfig) {
		AlarmManager.alarmEngineConfig = alarmEngineConfig;
	}

	/**
	 * Ritorna i dati di un allarme con identificativo idAllarme
	 * 
	 * @param idAllarme
	 *            Identificativo dell'allarme
	 * @return Allarme
	 * @throws AlarmException
	 */
	public static IAlarm getAlarm(String idAllarme,Logger log, DAOFactory daoFactory) throws AlarmException {

		AlarmImpl alarm = null;
		try {
			IServiceManager pluginSM = (IServiceManager) daoFactory.getServiceManager(ProjectInfo.getInstance());
			IAllarmeServiceSearch allarmeDAO = pluginSM.getAllarmeServiceSearch();

			IExpression expr = allarmeDAO.newExpression();
			expr.equals(Allarme.model().NOME, idAllarme);

			Allarme allarme = null;
			try{
				allarme = allarmeDAO.find(expr);
			}catch(NotFoundException notFound){}
				
			if (allarme != null) {
				alarm = (AlarmImpl) getAlarm(allarme,log,daoFactory);
			} else
				throw new AlarmException("Allarme -" + idAllarme
						+ "- non esistente");

		} catch (Exception e) {
			log.error("AlarmManager.getAlarm(" + idAllarme
					+ ") ha rilevato un errore: " + e.getMessage(), e);
			throw new AlarmException(e.getMessage(),e);
		}
		return alarm;
	}

	/**
	 * Ritorna i dati di un allarme a partire da una configurazione Allarme
	 * 
	 * @param allarme
	 *            Configurazione dell'allarme
	 * @return Allarme
	 * @throws AlarmException
	 */
	public static IAlarm getAlarm(Allarme allarme,Logger log, DAOFactory daoFactory)
			throws AlarmException {
		return _getAlarm(allarme, log, daoFactory, null);
	}
	public static IAlarm getAlarm(Allarme allarme,Logger log, DAOFactory daoFactory, org.openspcoop2.core.plugins.dao.IServiceManager pluginSM)
			throws AlarmException {
		return _getAlarm(allarme, log, daoFactory, pluginSM);
	}
	private static IAlarm _getAlarm(Allarme allarme,Logger log, DAOFactory daoFactory, org.openspcoop2.core.plugins.dao.IServiceManager pluginSM)
			throws AlarmException {
		
		AlarmImpl alarm = null;
		try {
			if (allarme != null) {
				
				alarm = new AlarmImpl(allarme,log,daoFactory);
				AlarmStateValues statusValue = null;
				StatoAllarme statoAllarme = AllarmiConverterUtils.toStatoAllarme(allarme.getStato());
				switch (statoAllarme) {
				case OK:
					statusValue = AlarmStateValues.OK;
					break;
				case WARNING:
					statusValue = AlarmStateValues.WARNING;
					break;
				case ERROR:
					statusValue = AlarmStateValues.ERROR;
					break;
				}
				AlarmStatus alarmStatus = new AlarmStatus();
				alarmStatus.setStatus(statusValue);
				alarmStatus.setDetail(allarme.getDettaglioStato());
				alarm.setStatus(alarmStatus);
				
				if(pluginSM==null) {
					pluginSM = 
							(org.openspcoop2.core.plugins.dao.IServiceManager) 
							daoFactory.getServiceManager(org.openspcoop2.core.plugins.utils.ProjectInfo.getInstance());
				}
				TipoPlugin tipoPlugin = TipoPlugin.ALLARME;
				IdPlugin idPlugin = new IdPlugin();
				idPlugin.setTipoPlugin(tipoPlugin.getValue());
				idPlugin.setTipo(allarme.getTipo());
				Plugin plugin = pluginSM.getPluginServiceSearch().get(idPlugin);
				
				alarm.setPluginClassName(plugin.getClassName());
				
				IDynamicLoader cAllarme = DynamicFactory.getInstance().newDynamicLoader(tipoPlugin, allarme.getTipo(), plugin.getClassName(), log);
				IAlarmProcessing alarmProc = (IAlarmProcessing) cAllarme.newInstance();
				alarm.setManuallyUpdateState(alarmProc.isManuallyUpdateState());
				alarm.setManuallyAckCriteria(alarmProc.isManuallyAckCriteria());
				AlarmContext ctx = new AlarmContext(allarme, log, daoFactory);
				List<Parameter<?>> listParameters = alarmProc.getParameters(ctx);
				
				for (AllarmeParametro parametro : allarme.getAllarmeParametroList()) {
					for (Parameter<?> parameter : listParameters) {
						if(parameter.getId().equals(parametro.getIdParametro())){
							parameter.setValueAsString(parametro.getValore());
							break;
						}
					}
				}
				
				for (Parameter<?> parameter : listParameters) {
					alarm.addParameter(parameter);
				}
				
			} else
				throw new AlarmException("Allarme non valido");

		} catch (Exception e) {
			log.error(
					"AlarmManager.getAlarm(" + allarme.getNome()
							+ ") ha rilevato un errore: " + e.getMessage(), e);
			throw new AlarmException(e.getMessage(),e);
		}
		return alarm;
	}
	
	protected static void changeStatus(AlarmStatus newStatoAllarme,IAlarm allarme,IServiceManager allarmiSM,String username,boolean statusChanged, 
			List<AllarmeHistory> repositoryHistory) throws AlarmException {
		try {
			
			Allarme oldConfig = allarme.getConfigAllarme();
			
			IAllarmeServiceSearch allarmeSearchDAO = allarmiSM
					.getAllarmeServiceSearch();
			IAllarmeService allarmeDAO = allarmiSM
					.getAllarmeService();
			IAllarmeHistoryService allarmeHistoryDAO = allarmiSM
					.getAllarmeHistoryService();

			IExpression expr = allarmeSearchDAO.newExpression();
			expr.equals(Allarme.model().NOME, allarme.getId());

			Allarme confAllarme = allarmeSearchDAO.find(expr); // non mi posso fidare dell'allarme passato come parametro. Potrebbe essere cambiato nel frattempo

			if (confAllarme != null) {
				
				// Se il tipo è AlarmStatusWithAck sto forzando la decisione dell'ack all'utente console
				boolean autoChangeAck = true;
				if(newStatoAllarme instanceof AlarmStatusWithAck){
					AlarmStatusWithAck withAck = (AlarmStatusWithAck) newStatoAllarme;
					if(withAck.isAck()){
						confAllarme.setAcknowledged(1);
						oldConfig.setAcknowledged(1);
					}
					else{
						confAllarme.setAcknowledged(0);
						oldConfig.setAcknowledged(0);
					}
					autoChangeAck = false;
				}
				
				if(statusChanged && confAllarme.getStato()!=null){
					confAllarme.setStatoPrecedente(confAllarme.getStato());
					oldConfig.setStatoPrecedente(confAllarme.getStato());
				}
				
				switch (newStatoAllarme.getStatus()) {
				case OK:
					confAllarme.setStato(AllarmiConverterUtils.toIntegerValue(StatoAllarme.OK));
					break;
				case WARNING:
					confAllarme.setStato(AllarmiConverterUtils.toIntegerValue(StatoAllarme.WARNING));
					break;
				default:
					confAllarme.setStato(AllarmiConverterUtils.toIntegerValue(StatoAllarme.ERROR));
					break;
				}
				oldConfig.setStato(confAllarme.getStato());
				
				confAllarme.setLasttimestampUpdate(DateManager.getDate());
				oldConfig.setLasttimestampUpdate(confAllarme.getLasttimestampUpdate());
				
				// Azzero acknoledgement in caso di cambio stato
				if(statusChanged){
					if(autoChangeAck){
						// lo stato è modificato
						confAllarme.setAcknowledged(0);
						oldConfig.setAcknowledged(0);
					}
					else{
						// se lo stato è rimasto lo stesso ma è cambiato il dettaglio, modificao l'acknoldegement ??
	//					if(newStatoAllarme.getDetail()!=null && !newStatoAllarme.getDetail().equals(confAllarme.getDettaglioStato())){
	//						confAllarme.setAcknowledged(0);
	//					}
					}
				}
				
				confAllarme.setDettaglioStato(newStatoAllarme.getDetail());
				oldConfig.setDettaglioStato(confAllarme.getDettaglioStato());
				
				IdAllarme idConfAllarme = new IdAllarme();

				idConfAllarme.setId(confAllarme.getId());
				idConfAllarme.setNome(confAllarme.getNome());
//				idConfAllarme.setIdConfigurazioneServizioAzione(confAllarme
//						.getIdConfigurazioneServizioAzione());
				
				// Recupero stato dell'allarme dal db per evitare di far crescere all'infinito l'history dell'allarme per ogni check.
				Allarme allarmeImmagineDB = allarmeDAO.get(idConfAllarme);
				
				//System.out.println("UPDATE ALLARME");
				allarmeDAO.update(idConfAllarme, confAllarme);
				
				boolean checkHistory = false;
				if(repositoryHistory!=null) {
					checkHistory = true;
				}
				else {
					AlarmEngineConfig alarmEngineConfig = AlarmManager.getAlarmEngineConfig();
					checkHistory = alarmEngineConfig.isHistoryEnabled();
				}
				if(checkHistory) {

					boolean registraHistory = registraHistory(confAllarme, allarmeImmagineDB);
					//System.out.println("REGISTRO HISTORY ALLARME ? "+registraHistory);
					if(registraHistory){
						AllarmeHistory history = new AllarmeHistory();
						history.setEnabled(confAllarme.getEnabled());
						history.setAcknowledged(confAllarme.getAcknowledged());
						history.setDettaglioStato(confAllarme.getDettaglioStato());
						history.setStato(confAllarme.getStato());
						IdAllarme idAllarme = new IdAllarme();
						idAllarme.setNome(confAllarme.getNome());
						history.setIdAllarme(idAllarme);
						history.setUtente(username);	
						history.setTimestampUpdate(confAllarme.getLasttimestampUpdate());
						if(repositoryHistory!=null) {
							repositoryHistory.add(history);
						}
						else {
							allarmeHistoryDAO.create(history);
						}
						//System.out.println("REGISTRATO HISTORY ALLARME");
					}
					
				}
				
			} else
				throw new AlarmException("Allarme -" + allarme.getId()
						+ "- non esistente");
			
		} catch (Exception e) {
			allarme.getLogger().error(
					"AlarmManager.changeStatus() ha rilevato un errore: "
							+ e.getMessage(), e);
			throw new AlarmException(e.getMessage(),e);
		}
	}
	
	private static boolean registraHistory(Allarme confAllarme, Allarme allarmeImmagineDB){
		
		if(confAllarme.getEnabled()!=null){
			if(allarmeImmagineDB.getEnabled()==null){
				return true;
			}
			else if(allarmeImmagineDB.getEnabled() != confAllarme.getEnabled()){
				return true;
			}
		}
		else{
			if(allarmeImmagineDB.getEnabled()!=null){
				return true;
			}
		}
		
		if(confAllarme.getAcknowledged()!=null){
			if(allarmeImmagineDB.getAcknowledged()==null){
				return true;
			}
			else if(allarmeImmagineDB.getAcknowledged() != confAllarme.getAcknowledged()){
				return true;
			}
		}
		else{
			if(allarmeImmagineDB.getAcknowledged()!=null){
				return true;
			}
		}
		
//		Bug Fix OPPT-753:
//			Il Dettaglio non deve far parte della discriminante se salvare o meno nell'history dell'allarme poichè l'allarme ha cambiato stato.
//		if(confAllarme.getDettaglioStato()!=null){
//			if(allarmeImmagineDB.getDettaglioStato()==null){
//				return true;
//			}
//			else if(!allarmeImmagineDB.getDettaglioStato().equals(confAllarme.getDettaglioStato())){
//				return true;
//			}
//		}
//		else{
//			if(allarmeImmagineDB.getDettaglioStato()!=null){
//				return true;
//			}
//		}
		
		if(confAllarme.getStato()!=null){
			if(allarmeImmagineDB.getStato()==null){
				return true;
			}
			else if(!allarmeImmagineDB.getStato().equals(confAllarme.getStato())){
				return true;
			}
		}
		else{
			if(allarmeImmagineDB.getStato()!=null){
				return true;
			}
		}
		
		
		return false;
	}
	
	protected static void sendMail(Allarme configAllarme, IAlarmLogger alarmLog, List<String> logEvents) throws Exception{
		
		AlarmEngineConfig alarmEngineConfig = AlarmManager.getAlarmEngineConfig();
		if(alarmEngineConfig==null){
			throw new Exception("Configurazione Mail non fornita, utilizzare il metodo AlarmManager.setAlarmEngineConfig(...)");
		}
		
		List<String> destinatari = new ArrayList<String>();
		if(configAllarme.getMail()!=null && configAllarme.getMail().getDestinatari()!=null){
			String [] tmp = configAllarme.getMail().getDestinatari().split(",");
			if(tmp!=null && tmp.length>0){
				for (int i = 0; i < tmp.length; i++) {
					destinatari.add(tmp[i].trim());
				}
			}
		}
		
		if(destinatari.size()>0){
			
			Sender sender = null;
			if(alarmEngineConfig.getMailSenderType()==null){ 
				throw new Exception("Configurazione mail errata [Parametro 'SenderType' non definito]");
			}
			else{
				sender = SenderFactory.newSender(alarmEngineConfig.getMailSenderType(), alarmLog.getInternalLogger());
			}
			
			if(alarmEngineConfig.getMailSenderConnectionTimeout()!=null){
				sender.setConnectionTimeout(alarmEngineConfig.getMailSenderConnectionTimeout());
			}
			if(alarmEngineConfig.getMailSenderReadTimeout()!=null){
				sender.setReadTimeout(alarmEngineConfig.getMailSenderReadTimeout());
			}
			
			for (String destinatario : destinatari) {
				
				Mail mail = new Mail();
			
				// agent
				mail.setUserAgent(alarmEngineConfig.getMailAgent());
				
				// from
				if(alarmEngineConfig.getMailFrom()==null){ 
					throw new Exception("Configurazione mail errata [Parametro 'From' non definito]");
				}
				else{
					mail.setFrom(alarmEngineConfig.getMailFrom());
				}
				
				// to
				mail.setTo(destinatario);
				
				// dati connessione
				if(alarmEngineConfig.getMailHost()==null){ 
					throw new Exception("Configurazione mail errata [Parametro 'Hostname' non definito]");
				}
				else{
					mail.setServerHost(alarmEngineConfig.getMailHost());
				}
				if(alarmEngineConfig.getMailPort()==null){ 
					throw new Exception("Configurazione mail errata [Parametro 'Port' non definito]");
				}
				else{
					mail.setServerPort(alarmEngineConfig.getMailPort());
				}
				if(alarmEngineConfig.getMailUsername()!=null){ 
					mail.setUsername(alarmEngineConfig.getMailUsername());
				}
				if(alarmEngineConfig.getMailPassword()!=null){ 
					mail.setPassword(alarmEngineConfig.getMailPassword());
				}
				
				// ssl
				mail.setSslConfig(alarmEngineConfig.getMailSSLConfig());
				mail.setStartTls(alarmEngineConfig.isMailStartTls());
				
				// subject
				String templateSubjectMail = null;
				if(configAllarme.getMail()!=null && configAllarme.getMail().getSubject()!=null) {
					templateSubjectMail = configAllarme.getMail().getSubject();
				}
				if(templateSubjectMail==null || "".equals(templateSubjectMail)){
					templateSubjectMail = alarmEngineConfig.getMailSubject();
					if(templateSubjectMail==null){ 
						throw new Exception("Configurazione mail errata [Parametro 'Subject' non definito]");
					}
				}
				mail.setSubject(replaceKeywordTemplate(templateSubjectMail, configAllarme, false));
				
				// body
				String templateBodyMail = null;
				if(configAllarme.getMail()!=null && configAllarme.getMail().getBody()!=null) {
					templateBodyMail = configAllarme.getMail().getBody();
				}
				if(templateBodyMail==null || "".equals(templateBodyMail)){
					templateBodyMail = alarmEngineConfig.getMailBody();
					if(templateBodyMail==null){ 
						throw new Exception("Configurazione mail errata [Parametro 'Body' non definito]");
					}
				}
				mail.getBody().setMessage(replaceKeywordTemplate(templateBodyMail, configAllarme, false));
				
				// send
				sender.send(mail, alarmEngineConfig.isMailDebug());
				
				if(alarmEngineConfig.isMailDebug()){
					logEvents.add("eMail per notifica stato ["+configAllarme.getStato()+"] inviata correttamente al destinatario ["+destinatario+"]");
				}
				
			}
			
		}
	}
	
	protected static void invokeScript(Allarme configAllarme, IAlarmLogger alarmLog, List<String> logEvents) throws Exception{
		
		AlarmEngineConfig alarmEngineConfig = AlarmManager.getAlarmEngineConfig();
		if(alarmEngineConfig==null){
			throw new Exception("Configurazione Script non fornita, utilizzare il metodo AlarmManager.setAlarmEngineConfig(...)");
		}
		
		String path = null;
		if(configAllarme.getScript()!=null) {
			path = configAllarme.getScript().getCommand();
		}
		if(path==null || "".equals(path)){
			path = alarmEngineConfig.getDefaultScriptPath();
			if(path==null){ 
				throw new Exception("Configurazione script errata [Parametro 'Path' non definito]");
			}
		}
		
		String args = null;
		if(configAllarme.getScript()!=null) {
			args = configAllarme.getScript().getArgs();
		}
		if(args==null || "".equals(args)){
			args = alarmEngineConfig.getDefaultScriptArgs();
			if(args==null){ 
				throw new Exception("Configurazione script errata [Parametro 'Args' non definito]");
			}
		}
		args = replaceKeywordTemplate(args, configAllarme, true);
		
		ScriptInvoker invoker = new ScriptInvoker(path);
		
		List<String> arguments = new ArrayList<String>();
		StringBuilder bfUtils = null;
		if(args!=null && !"".equals(args)){
			String [] tmp = args.trim().split(" ");
			for (int i = 0; i < tmp.length; i++) {
				String s = tmp[i].trim();
				if(bfUtils == null){
					if(s.startsWith("\"")){
						if(s.endsWith("\"")){
							if(s.length()>2){
								arguments.add(s.substring(1,s.length()-1));
							}
							else{
								arguments.add(s); // caso speciale?
							}
						}
						else{
							bfUtils = new StringBuilder(s.substring(1));
						}
					}
					else{
						arguments.add(s);
					}
				}
				else{
					if(s.endsWith("\"")){
						bfUtils.append(" ").append(s.substring(0, (s.length()-1)));
						arguments.add(bfUtils.toString());
						bfUtils = null;
					}
					else{
						bfUtils.append(" ").append(s);
					}
				}
			}
		}
		
		if(arguments.size()>0)
			invoker.run(arguments.toArray(new String[1]));
		else{
			invoker.run();
		}
		
		String msg = "Invocazione script ["+path+"] (args: "+args+") ha ritornato un codice di uscita ["+invoker.getExitValue()+
				"]\nOutputStream: "+invoker.getOutputStream()+"\nErrorStream: "+invoker.getErrorStream();
		if(invoker.getExitValue()!=0){
			throw new Exception(msg);
		}
		else{
			if(alarmEngineConfig.isScriptDebug()){
				logEvents.add(msg);
			}
		}
		
	}
	
	public static String replaceKeywordTemplate(String original, Allarme configAllarme, boolean script){
		
		String newS = original.replace(CostantiConfigurazione.ALARM_KEYWORD_TEMPLATE_NOME_ALLARME, configAllarme.getAlias());
		
		newS = newS.replace(CostantiConfigurazione.ALARM_KEYWORD_TEMPLATE_ID_ALLARME, configAllarme.getNome());
		
		StatoAllarme statoAllarme = AllarmiConverterUtils.toStatoAllarme(configAllarme.getStato());
		switch ( statoAllarme ) {
		case OK:
			newS = newS.replace(CostantiConfigurazione.ALARM_KEYWORD_TEMPLATE_STATO_ALLARME, "Ok");
			break;
		case WARNING:
			newS = newS.replace(CostantiConfigurazione.ALARM_KEYWORD_TEMPLATE_STATO_ALLARME, "Warning");
			break;
		case ERROR:
			newS = newS.replace(CostantiConfigurazione.ALARM_KEYWORD_TEMPLATE_STATO_ALLARME, "Error");
			break;
		}
		
		String details = configAllarme.getDettaglioStato();
		if(details==null){
			details = "";
		}
		if(script){
			newS = newS.replace(CostantiConfigurazione.ALARM_KEYWORD_TEMPLATE_DETTAGLIO_ALLARME,"\""+details+"\"");
		}else{
			newS = newS.replace(CostantiConfigurazione.ALARM_KEYWORD_TEMPLATE_DETTAGLIO_ALLARME,details);
		}
		return newS;
	}

	public static void notifyChangeStatus(AlarmEngineConfig alarmEngineConfig, Allarme allarme,
			IAlarm alarm, String pluginClassName,String threadName,
			AlarmLogger alarmLogger,
			AlarmStatus oldStatus, AlarmStatus nuovoStatoAllarme,
			boolean checkAcknowledState) throws AlarmException, AlarmNotifyException {
		
		boolean statusChanged = false;
		String prefix = null;
		try{
			
			// Cambio di stato effettivo ?
			if(oldStatus==null || oldStatus.getStatus()==null){
				statusChanged = true;
			}
			else{
				statusChanged = !oldStatus.getStatus().equals(nuovoStatoAllarme.getStatus());
			}
			
			// Gestione invocazione script, mail e notifiche a plugin
			boolean statusWarningError = AlarmStateValues.WARNING.equals(nuovoStatoAllarme) || AlarmStateValues.ERROR.equals(nuovoStatoAllarme);
			if(!statusChanged && (!statusWarningError || !checkAcknowledState)){
				alarmLogger.debug("Cambio di stato non rilevato (old:"+oldStatus+" new:"+nuovoStatoAllarme+")");
				return;
			}
			
			if(statusChanged) {
				prefix = "Cambio di stato rilevato (old:"+oldStatus+" new:"+nuovoStatoAllarme+"); ";
			}
			else {
				prefix = "Cambio di stato non rilevato; ";
			}
			
		}catch(Exception e){
			throw new AlarmException(e.getMessage(),e);
		}
		
		boolean invokePlugin = statusChanged;
		boolean sendMail = false;
		boolean invokeScript = false;
		try {
			alarmLogger.debug(prefix+"lettura configurazione in corso ...");
			
			boolean mailCheckAcknowledgedStatus = alarmEngineConfig.isMailCheckAcknowledgedStatus();
			boolean mailSendChangeStatusOk = alarmEngineConfig.isMailSendChangeStatusOk();
			
			boolean scriptCheckAcknowledgedStatus = alarmEngineConfig.isScriptCheckAcknowledgedStatus();
			boolean scriptSendChangeStatusOk = alarmEngineConfig.isScriptSendChangeStatusOk();
			
			boolean acknowledged = (allarme.getAcknowledged()==null || allarme.getAcknowledged()==1);
			
			
			alarmLogger.debug(prefix+"valutazione se è necessario una notifica di cambio stato per mail/script (acknowledged:"+acknowledged+") ...");
			
			alarmLogger.debug(prefix+"mail sendChangeStatusOk:"+mailSendChangeStatusOk+" checkAcknowledgedStatus:"+mailCheckAcknowledgedStatus+"");
			alarmLogger.debug(prefix+"script sendChangeStatusOk:"+scriptSendChangeStatusOk+" checkAcknowledgedStatus:"+scriptCheckAcknowledgedStatus+"");
			
			if(nuovoStatoAllarme!=null) {
				
				if(AlarmStateValues.OK.equals(nuovoStatoAllarme.getStatus())){
					if(mailSendChangeStatusOk){
						// sia in ack mode che non in ack mode viene spedito da questo metodo.
						// tanto deve essere spedita solo la prima volta e non continuamente
						sendMail = (allarme.getMail()!=null &&
								allarme.getMail().getInvia()!=null && 
								allarme.getMail().getInvia()==1);
					}
					if(scriptSendChangeStatusOk){
						// sia in ack mode che non in ack mode viene spedito da questo metodo.
						// tanto deve essere spedita solo la prima volta e non continuamente
						invokeScript = (allarme.getScript()!=null &&
								allarme.getScript().getInvoca()!=null && 
								allarme.getScript().getInvoca()==1);
					}
					
				}
				
				
				else if(AlarmStateValues.WARNING.equals(nuovoStatoAllarme.getStatus())){
					
					if( (statusChanged) || (mailCheckAcknowledgedStatus && !acknowledged) ){
						// NOTA: per come è impostata la pddMonitor il warning esiste solo se è attivo anche l'alert
						sendMail = (
									allarme.getMail()!=null && 
									allarme.getMail().getInvia()!=null && 
									allarme.getMail().getInvia()==1
								) 
								&& 
								(
									allarme.getMail()!=null &&
									allarme.getMail().getInviaWarning()!=null && 
									allarme.getMail().getInviaWarning()==1
								);
					}
					if( (statusChanged) || (scriptCheckAcknowledgedStatus && !acknowledged) ){
						// NOTA: per come è impostata la pddMonitor il warning esiste solo se è attivo anche l'alert
						invokeScript = (
									allarme.getScript()!=null &&
									allarme.getScript().getInvoca()!=null && 
									allarme.getScript().getInvoca()==1
								) 
								&&
								(
									allarme.getScript()!=null && 
									allarme.getScript().getInvocaWarning()!=null && 
									allarme.getScript().getInvocaWarning()==1
								);
					}
					
				}
				else if(AlarmStateValues.ERROR.equals(nuovoStatoAllarme.getStatus())){
					
					if( (statusChanged) || (mailCheckAcknowledgedStatus && !acknowledged) ){
						sendMail = allarme.getMail()!=null &&
								allarme.getMail().getInvia()!=null && 
								allarme.getMail().getInvia()==1; // note: Alert rappresenta l'invio della mail nel db e non veramente solo il livello error
					}
					if( (statusChanged) || (scriptCheckAcknowledgedStatus && !acknowledged) ){
						invokeScript = allarme.getScript()!=null &&
								allarme.getScript().getInvoca()!=null && 
								allarme.getScript().getInvoca()==1; // note: Alert rappresenta l'invio via script nel db e non veramente solo il livello error
					}
					
				}
			}
			
		}catch(Exception e){
			throw new AlarmException(e.getMessage(),e);
		}
		
		List<Exception> listExceptions = new ArrayList<Exception>();
		
		// Notifico cambio di stato all'interfaccia	
		String pluginInvocationError = null;
		try {
			if(invokePlugin) {
				alarmLogger.debug(prefix+"notifica plugin ...");
				IDynamicLoader cPlugin = DynamicFactory.getInstance().newDynamicLoader(TipoPlugin.ALLARME,allarme.getTipo(),pluginClassName, alarmLogger.getInternalLogger());
				IAlarmProcessing alarmProc = (IAlarmProcessing) cPlugin.newInstance();
				alarmProc.changeStatusNotify(alarm, oldStatus, nuovoStatoAllarme);
				alarmLogger.debug(prefix+"notifica plugin terminata");
			}
		} catch( Exception e) {
			alarmLogger.error(prefix+"notifica plugin fallita: "+e.getMessage(),e);
			pluginInvocationError = e.getMessage();
			listExceptions.add(e);
		}
		
		// Notifico cambio di stato via mail
		String sendMailError = null;
		try {
			if(sendMail){
				alarmLogger.debug(prefix+"notifica mail ...");
				List<String> logEvents = new ArrayList<String>();
				AlarmManager.sendMail(allarme, alarmLogger, logEvents);
				if(logEvents!=null && !logEvents.isEmpty()) {
					for (String logEvent : logEvents) {
						String prefixLog = AlarmLogger.buildPrefix(threadName,allarme.getAlias(),allarme.getNome());
						alarmLogger.debug(prefixLog+logEvent);
					}
				}
				alarmLogger.debug(prefix+"notifica mail completata");
			}
		}	
		catch( Exception e) {
			alarmLogger.error(prefix+"notifica mail fallita: "+e.getMessage(),e);
			sendMailError = e.getMessage();
			listExceptions.add(e);
		}
		
		// Notifico cambio di stato via script
		String scriptInvocationError = null;
		try {
			if(invokeScript){
				alarmLogger.debug(prefix+"notifica via script ...");
				List<String> logEvents = new ArrayList<String>();
				AlarmManager.invokeScript(allarme, alarmLogger, logEvents);
				if(logEvents!=null && !logEvents.isEmpty()) {
					for (String logEvent : logEvents) {
						String prefixLog = AlarmLogger.buildPrefix(threadName,allarme.getAlias(),allarme.getNome());
						alarmLogger.debug(prefixLog+logEvent);
					}
				}
				alarmLogger.debug(prefix+"notifica via script completata");
			}
		}	
		catch( Exception e) {
			alarmLogger.error(prefix+"notifica via script fallita: "+e.getMessage(),e);
			scriptInvocationError = e.getMessage();
			listExceptions.add(e);
		}
		
		if(!listExceptions.isEmpty()) {
			AlarmNotifyException ane = null;
			if(listExceptions.size()==1) {
				Exception e = listExceptions.get(0);
				ane = new AlarmNotifyException(e.getMessage(),e);
			}
			else {
				org.openspcoop2.utils.UtilsMultiException multi = new UtilsMultiException(listExceptions.toArray(new Exception[listExceptions.size()]));
				ane = new AlarmNotifyException("Notifiche di cambio stato fallite",multi);
			}
			ane.setPluginInvocationError(pluginInvocationError);
			ane.setSendMailError(sendMailError);
			ane.setScriptInvocationError(scriptInvocationError);
			throw ane;
		}
	}
	
	protected static void registraNotifica(AlarmLogger alarmLogger, Allarme allarme, AlarmStatus oldStatoAllarme, AlarmStatus newStatoAllarme, AllarmeHistory repositoryHistory,
			IServiceManager allarmiSM) throws AlarmException {
		try {
			IAllarmeNotificaService allarmeNotificaSearchDAO = allarmiSM
					.getAllarmeNotificaService();
			
			AllarmeNotifica notifica = new AllarmeNotifica();
			
			notifica.setDataNotifica(DateManager.getDate());
			
			IdAllarme idAllarme = allarmiSM.getAllarmeServiceSearch().convertToId(allarme);
			notifica.setIdAllarme(idAllarme);
			
			notifica.setOldStato(AllarmiConverterUtils.toIntegerValue(StatoAllarme.valueOf(oldStatoAllarme.getStatus().name())));
			notifica.setOldDettaglioStato(oldStatoAllarme.getDetail());
			
			notifica.setNuovoStato(AllarmiConverterUtils.toIntegerValue(StatoAllarme.valueOf(newStatoAllarme.getStatus().name())));
			notifica.setNuovoDettaglioStato(newStatoAllarme.getDetail());
			
			if(repositoryHistory!=null) {
				org.openspcoop2.core.allarmi.utils.serializer.JaxbSerializer serializer = new org.openspcoop2.core.allarmi.utils.serializer.JaxbSerializer();
				notifica.setHistoryEntry(serializer.toString(repositoryHistory));
			}
			allarmeNotificaSearchDAO.create(notifica);
			
		} catch (Exception e) {
			alarmLogger.error(
					"Registrazione notifica di cambio stato fallita: "
							+ e.getMessage(), e);
			throw new AlarmException(e.getMessage(),e);
		}
	}
}
