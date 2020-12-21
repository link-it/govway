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

package org.openspcoop2.monitor.engine.alarm;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.openspcoop2.core.allarmi.Allarme;
import org.openspcoop2.core.allarmi.AllarmeHistory;
import org.openspcoop2.core.allarmi.AllarmeParametro;
import org.openspcoop2.core.allarmi.IdAllarme;
import org.openspcoop2.core.allarmi.constants.StatoAllarme;
import org.openspcoop2.core.allarmi.dao.IAllarmeHistoryService;
import org.openspcoop2.core.allarmi.dao.IAllarmeService;
import org.openspcoop2.core.allarmi.dao.IAllarmeServiceSearch;
import org.openspcoop2.core.allarmi.dao.IServiceManager;
import org.openspcoop2.core.allarmi.utils.AllarmiConverterUtils;
import org.openspcoop2.core.allarmi.utils.ProjectInfo;
import org.openspcoop2.core.commons.dao.DAOFactory;
import org.openspcoop2.generic_project.exception.NotFoundException;
import org.openspcoop2.generic_project.expression.IExpression;
import org.openspcoop2.monitor.engine.config.base.IdPlugin;
import org.openspcoop2.monitor.engine.config.base.Plugin;
import org.openspcoop2.monitor.engine.config.base.constants.TipoPlugin;
import org.openspcoop2.monitor.engine.constants.CostantiConfigurazione;
import org.openspcoop2.monitor.engine.dynamic.DynamicFactory;
import org.openspcoop2.monitor.engine.dynamic.IDynamicLoader;
import org.openspcoop2.monitor.sdk.alarm.AlarmStatus;
import org.openspcoop2.monitor.sdk.alarm.IAlarm;
import org.openspcoop2.monitor.sdk.constants.AlarmStateValues;
import org.openspcoop2.monitor.sdk.exceptions.AlarmException;
import org.openspcoop2.monitor.sdk.parameters.Parameter;
import org.openspcoop2.monitor.sdk.plugins.IAlarmProcessing;
import org.openspcoop2.utils.date.DateManager;
import org.openspcoop2.utils.mail.Mail;
import org.openspcoop2.utils.mail.Sender;
import org.openspcoop2.utils.mail.SenderFactory;
import org.openspcoop2.utils.resources.ScriptInvoker;


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
			throw new AlarmException(e);
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
				
				org.openspcoop2.monitor.engine.config.base.dao.IServiceManager pluginSM = 
						(org.openspcoop2.monitor.engine.config.base.dao.IServiceManager) 
						daoFactory.getServiceManager(org.openspcoop2.monitor.engine.config.base.utils.ProjectInfo.getInstance());
				TipoPlugin tipoPlugin = TipoPlugin.ALLARME;
				IdPlugin idPlugin = new IdPlugin();
				idPlugin.setTipoPlugin(tipoPlugin.getValue());
				idPlugin.setTipo(allarme.getTipo());
				Plugin plugin = pluginSM.getPluginServiceSearch().get(idPlugin);
				
				alarm.setPluginClassName(plugin.getClassName());
				
				IDynamicLoader cAllarme = DynamicFactory.getInstance().newDynamicLoader(tipoPlugin, allarme.getTipo(), plugin.getClassName(), log);
				IAlarmProcessing alarmProc = (IAlarmProcessing) cAllarme.newInstance();
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
			throw new AlarmException(e);
		}
		return alarm;
	}
	
	protected static void changeStatus(AlarmStatus newStatoAllarme,IAlarm allarme,Logger log,DAOFactory  daoFactory,String username) throws AlarmException {
		try {
			
			Allarme oldConfig = allarme.getConfigAllarme();
			
			IServiceManager pluginSM = (IServiceManager) daoFactory.getServiceManager(ProjectInfo.getInstance());
			IAllarmeServiceSearch allarmeSearchDAO = pluginSM
					.getAllarmeServiceSearch();
			IAllarmeService allarmeDAO = pluginSM
					.getAllarmeService();
			IAllarmeHistoryService allarmeHistoryDAO = pluginSM
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
				
				if(confAllarme.getStato()!=null){
					confAllarme.setStatoPrecedente(confAllarme.getStato());
					oldConfig.setStatoPrecedente(confAllarme.getStatoPrecedente());
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
				if(confAllarme.getStatoPrecedente()!=null){
					if(autoChangeAck && confAllarme.getStatoPrecedente().equals(confAllarme.getStato())==false){
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
				
				AlarmEngineConfig alarmEngineConfig = AlarmManager.getAlarmEngineConfig();
				if(alarmEngineConfig.isHistoryEnabled()) {

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
						allarmeHistoryDAO.create(history);
						//System.out.println("REGISTRATO HISTORY ALLARME");
					}
					
				}
				
			} else
				throw new AlarmException("Allarme -" + allarme.getId()
						+ "- non esistente");
			
		} catch (Exception e) {
			log.error(
					"AlarmManager.changeStatus() ha rilevato un errore: "
							+ e.getMessage(), e);
			throw new AlarmException(e);
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
	
	protected static void sendMail(Allarme configAllarme, Logger log, String threadName) throws Exception{
		
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
				sender = SenderFactory.newSender(alarmEngineConfig.getMailSenderType(), log);
			}
			
			if(alarmEngineConfig.getMailSenderConnectionTimeout()!=null){
				sender.setConnectionTimeout(alarmEngineConfig.getMailSenderConnectionTimeout());
			}
			if(alarmEngineConfig.getMailSenderReadTimeout()!=null){
				sender.setReadTimeout(alarmEngineConfig.getMailSenderReadTimeout());
			}
			
			for (String destinatario : destinatari) {
				
				Mail mail = new Mail();
			
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
					log.debug("["+threadName+"] Allarme - " + configAllarme.getNome()
						+ " - inviata mail per notifica stato ["+configAllarme.getStato()+"] al destinatario ["+destinatario+"]");
				}
			}
			
		}
	}
	
	protected static void invokeScript(Allarme configAllarme, Logger log, String threadName) throws Exception{
		
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
		StringBuffer bfUtils = null;
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
							bfUtils = new StringBuffer(s.substring(1));
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
				log.debug("["+threadName+"] Allarme - " + configAllarme.getNome()
				+	 " - "+msg);
			}
		}
		
	}
	
	public static String replaceKeywordTemplate(String original, Allarme configAllarme, boolean script){
		
		String newS = original.replace(CostantiConfigurazione.ALARM_KEYWORD_TEMPLATE_NOME_ALLARME, configAllarme.getNome());
		
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

}
