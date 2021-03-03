/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2021 Link.it srl (https://link.it). 
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

package org.openspcoop2.monitor.engine.alarm.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.openspcoop2.core.allarmi.Allarme;
import org.openspcoop2.core.allarmi.AllarmeFiltro;
import org.openspcoop2.core.allarmi.constants.StatoAllarme;
import org.openspcoop2.core.allarmi.constants.TipoAllarme;
import org.openspcoop2.core.allarmi.utils.AllarmiConverterUtils;
import org.openspcoop2.core.id.IDServizio;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.core.plugins.constants.TipoPlugin;
import org.openspcoop2.monitor.engine.alarm.AlarmEngineConfig;
import org.openspcoop2.monitor.engine.alarm.wrapper.ConfigurazioneAllarmeBean;
import org.openspcoop2.monitor.engine.dynamic.DynamicFactory;
import org.openspcoop2.monitor.engine.dynamic.IDynamicLoader;
import org.openspcoop2.monitor.sdk.condition.Context;
import org.openspcoop2.monitor.sdk.plugins.IAlarmProcessing;
import org.openspcoop2.protocol.engine.ProtocolFactoryManager;
import org.openspcoop2.protocol.engine.utils.NamingUtils;
import org.openspcoop2.protocol.sdk.IProtocolFactory;
import org.openspcoop2.utils.beans.BeanUtils;
import org.openspcoop2.utils.transport.http.HttpConstants;
import org.openspcoop2.utils.transport.http.HttpRequest;
import org.openspcoop2.utils.transport.http.HttpRequestMethod;
import org.openspcoop2.utils.transport.http.HttpResponse;
import org.openspcoop2.utils.transport.http.HttpUtilities;
import org.slf4j.Logger;

/**     
 * AllarmiUtils
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class AllarmiUtils {

	
	/***
	 * 
	 * Restituisce true se i tipi indicati sono compatibili, false altrimenti.
	 * 
	 * @param filtro
	 * @return
	 */
	public static boolean controllaTipiIndicatiNelFiltro(AllarmeFiltro filtro, String valoreQualsiasi) throws Exception{

		String tipoMittente = filtro.getTipoFruitore();
		String tipoDestinatario = filtro.getTipoErogatore();
		String tipoServizio = filtro.getTipoServizio();

		List<String> protocolloMittente = new ArrayList<String>();
		List<String> protocolloDestinatario = new ArrayList<String>();
		List<String> protocolloServizio = new ArrayList<String>();


		try{

			// 1. Carico i tipi disponibili per mittente, destinatario e servizio, se uno dei tre non e' stato scelto ('*') la lista risultera' vuota
			if(!tipoDestinatario.equals(valoreQualsiasi) ){
				IProtocolFactory<?> protocolFactory = ProtocolFactoryManager.getInstance().getProtocolFactoryByOrganizationType(tipoDestinatario);
				protocolloDestinatario.add(protocolFactory.getProtocol());
				//				tipiDisponibiliDestinatario = protocolFactory.createProtocolConfiguration().getTipiSoggetti();
			}

			if(!tipoMittente.equals(valoreQualsiasi)){
				IProtocolFactory<?> protocolFactory = ProtocolFactoryManager.getInstance().getProtocolFactoryByOrganizationType(tipoMittente);
				protocolloMittente.add(protocolFactory.getProtocol());
				//				tipiDisponibiliMittente = protocolFactory.createProtocolConfiguration().getTipiSoggetti();
			}

			if(!tipoServizio.equals(valoreQualsiasi)){
				IProtocolFactory<?> protocolFactory = ProtocolFactoryManager.getInstance().getProtocolFactoryByServiceType(tipoServizio);
				protocolloServizio.add(protocolFactory.getProtocol());
				//				tipiDisponibiliServizio = protocolFactory.createProtocolConfiguration().getTipiServizi();
			}

			// Inserisco tutti i tipi trovati in una mappa, se i tre tipi sono compatibili, l'elenco delle chiavi coincide con la dimensione delle liste di quelli settati.
			Map<String, String> mappaProtocolli = new HashMap<String, String>();
			if(protocolloServizio != null && protocolloServizio.size() > 0)
				for (String tipo : protocolloServizio) {
					if(!mappaProtocolli.containsKey(tipo))
						mappaProtocolli.put(tipo, tipo);
				}

			if(protocolloMittente != null && protocolloMittente.size() > 0)
				for (String tipo : protocolloMittente) {
					if(!mappaProtocolli.containsKey(tipo))
						mappaProtocolli.put(tipo, tipo);
				}

			if(protocolloDestinatario != null && protocolloDestinatario.size() > 0)
				for (String tipo : protocolloDestinatario) {
					if(!mappaProtocolli.containsKey(tipo))
						mappaProtocolli.put(tipo, tipo);
				}	

			// controllo di validita  
			if(protocolloDestinatario.size() > 0 && protocolloDestinatario.size() != mappaProtocolli.keySet().size())
				return false;

			if(protocolloMittente.size() > 0 && protocolloMittente.size() != mappaProtocolli.keySet().size())
				return false;

			if(protocolloServizio.size() > 0 && protocolloServizio.size() != mappaProtocolli.keySet().size())
				return false;

			return true;
		}catch(Exception e){
			throw e;
		}
	}
	
	private static HttpRequest buildHttpRequest(AlarmEngineConfig allarmiConfig, String url, HttpRequestMethod method, byte [] content, String contentType) {
		HttpRequest request = new HttpRequest();
		request.setUrl(url);
		request.setContent(content);
		request.setMethod(method);
		request.setContentType(contentType);
		request.setUsername(allarmiConfig.getActiveAlarm_serviceUrl_manager_username());
		request.setPassword(allarmiConfig.getActiveAlarm_serviceUrl_manager_password());
		
		request.setConnectTimeout(allarmiConfig.getActiveAlarm_serviceUrl_connectionTimeout());
		request.setReadTimeout(allarmiConfig.getActiveAlarm_serviceUrl_readConnectionTimeout());
		
		if(allarmiConfig.isActiveAlarm_serviceUrl_https()) {
			request.setHostnameVerifier(allarmiConfig.isActiveAlarm_serviceUrl_https_verificaHostName());
			if(allarmiConfig.isActiveAlarm_serviceUrl_https_autenticazioneServer()) {
				request.setTrustStorePath(allarmiConfig.getActiveAlarm_serviceUrl_https_truststorePath());
				request.setTrustStoreType(allarmiConfig.getActiveAlarm_serviceUrl_https_truststoreType());
				request.setTrustStorePassword(allarmiConfig.getActiveAlarm_serviceUrl_https_truststorePassword());
			}
			else {
				request.setTrustAllCerts(true);
			}
		}
		return request;
	}
	
	public static void sendToAllarmi(AlarmEngineConfig allarmiConfig, List<String> post_urls, List<String> post_contents, Logger log) throws Exception{
		if(post_urls!=null && post_urls.size()>0){
			for (int i = 0; i < post_urls.size(); i++) {
				String url = post_urls.get(i);
				String content = post_contents.get(i);

				log.debug("Invoke ["+url+"] ...");
				HttpRequest request = buildHttpRequest(allarmiConfig, url, HttpRequestMethod.POST, content.getBytes(), HttpConstants.CONTENT_TYPE_JSON);				
				HttpResponse response = HttpUtilities.httpInvoke(request);
				if(response.getContent()!=null){
					log.debug("Invoked ["+url+"] Status["+response.getResultHTTPOperation()+"] Message["+new String(response.getContent())+"]");	
				}
				else{
					log.debug("Invoked ["+url+"] Status["+response.getResultHTTPOperation()+"]");
				}
				if(response.getResultHTTPOperation()>202){
					throw new Exception("Error occurs during invoke url["+url+"] Status["+response.getResultHTTPOperation()+"] Message["+new String(response.getContent())+"]");	
				}	
			}
		}
	}
	
	public static String sendToAllarme(AlarmEngineConfig allarmiConfig, String get_url, Logger log) throws Exception{

		log.debug("Invoke ["+get_url+"] ...");
		HttpRequest request = buildHttpRequest(allarmiConfig, get_url, HttpRequestMethod.GET, null, null);				
		HttpResponse response = HttpUtilities.httpInvoke(request);
		String responseS = null;
		if(response.getContent()!=null){
			responseS = new String(response.getContent());
			log.debug("Invoked ["+get_url+"] Status["+response.getResultHTTPOperation()+"] Payload["+responseS+"]");	
		}
		else{
			log.debug("Invoked ["+get_url+"] Status["+response.getResultHTTPOperation()+"]");
			responseS = "";
		}
		if(response.getResultHTTPOperation()>202){
			throw new Exception("Error occurs during invoke url["+get_url+"] Status["+response.getResultHTTPOperation()+"] Payload["+responseS+"]");	
		}
		else {
			return responseS;
		}

	}
	
	public static void notifyStateActiveThread(boolean isAdd, boolean modificatoStato, boolean modificatoAckwoldegment,
			ConfigurazioneAllarmeBean oldAllarmePrimaModifica, ConfigurazioneAllarmeBean allarme, Logger log, AlarmEngineConfig allarmiConfig) throws Exception{
		if(TipoAllarme.PASSIVO.equals(allarme.getTipoAllarme())){
			// NOTA: il tipo di allarme non è modificabile.
			log.debug("Allarme ["+allarme.getNome()+"] è passivo. Non viene attivato alcun thread");
			return;
		}
		
		String prefixUrl = allarmiConfig.getActiveAlarm_serviceUrl();
		if(prefixUrl.endsWith("/")==false){
			prefixUrl = prefixUrl + "/";
		}
		prefixUrl = prefixUrl + "gestione/attivi/"+allarme.getNome();
		List<String> post_urls = new ArrayList<String>();
		List<String> post_contents = new ArrayList<String>();
		if(isAdd){
			if(allarme.getEnabled()==1){
				// start allarme
				post_urls.add(prefixUrl);
				post_contents.add("{\"operazione\": \"start\"}");
			}
		}
		else{
			// Se è stato modificato solo lo stato non richiede un stop/restart 
			boolean equals = false;
			StringBuilder bfDiff = null;
			if(oldAllarmePrimaModifica!=null){
				
				List<String> fieldEsclusi = new ArrayList<String>();
				fieldEsclusi.add("id");
				if(modificatoStato){
					fieldEsclusi.add("stato");
					fieldEsclusi.add("_value_stato");
					fieldEsclusi.add("precedenteStato");
					fieldEsclusi.add("_value_precedenteStato");
					fieldEsclusi.add("dettaglioStato");
				}
				if(modificatoAckwoldegment){
					fieldEsclusi.add("acknowledged");
				}
				
				// i metodi equals e diff non funzionano in caso di extends
				
				Allarme old = new Allarme();
				BeanUtils.copy(old, oldAllarmePrimaModifica, null);
				
				Allarme attuale = new Allarme();
				BeanUtils.copy(attuale, allarme, null);
				
				equals = attuale.equals(old, fieldEsclusi);
				if(!equals){
					bfDiff = new StringBuilder();
					attuale.diff(old, bfDiff, false, fieldEsclusi);
				}
			}
			if(equals){			
				if(modificatoStato){
					StatoAllarme statoAllarme = AllarmiConverterUtils.toStatoAllarme(allarme.getStato());
					post_urls.add(prefixUrl+"/stato");
					switch (statoAllarme) {
					case OK:
						post_contents.add("{\"stato\": \"OK\"}");
						break;
					case WARNING:
						post_contents.add("{\"stato\": \"WARNING\"}");
						break;
					case ERROR:
						post_contents.add("{\"stato\": \"ERROR\"}");
						break;
					}
				}
				if(modificatoAckwoldegment){
					post_urls.add(prefixUrl+"/acknoledgement");
					if(allarme.getAcknowledged()==1){
						post_contents.add("{\"acknoledgement\": true}");
					}
					else{
						post_contents.add("{\"acknoledgement\": false}");
					}
				}
				//else{
				// non è cambiato nulla
				//}
			}
			else{
				if(bfDiff!=null){	
					log.debug("Rilevata modifica, diff: "+bfDiff.toString());
				}
				
				if(allarme.getEnabled()==0){
					// stop allarme
					post_urls.add(prefixUrl);
					post_contents.add("{\"operazione\": \"stop\"}");
				}
				else{
					// restart allarme
					post_urls.add(prefixUrl);
					post_contents.add("{\"operazione\": \"restart\"}");
				}
			}
		}
		AllarmiUtils.sendToAllarmi(allarmiConfig, post_urls, post_contents, log);
	}
	
	public static void stopActiveThreads(List<String> allarmi, Logger log, AlarmEngineConfig allarmiConfig) throws Exception{
		
		List<String> post_urls = new ArrayList<String>();
		List<String> post_contents = new ArrayList<String>();
		if(!allarmi.isEmpty()) {
			for (String nomeAllarme : allarmi) {
				
				String prefixUrl = allarmiConfig.getActiveAlarm_serviceUrl();
				if(prefixUrl.endsWith("/")==false){
					prefixUrl = prefixUrl + "/";
				}
				prefixUrl = prefixUrl + "gestione/attivi/"+nomeAllarme;
				post_urls.add(prefixUrl);
				post_contents.add("{\"operazione\": \"stop\"}");
				
			}
		}
		
		if(!post_urls.isEmpty()) {
			AllarmiUtils.sendToAllarmi(allarmiConfig, post_urls, post_contents, log);
		}
		
	}
	
	public static void startActiveThread(ConfigurazioneAllarmeBean allarme, Logger log, AlarmEngineConfig allarmiConfig) throws Exception{
		_manageActiveThreads(allarme, log, allarmiConfig, "start");	
	}
	public static void restartActiveThread(ConfigurazioneAllarmeBean allarme, Logger log, AlarmEngineConfig allarmiConfig) throws Exception{
		_manageActiveThreads(allarme, log, allarmiConfig, "restart");	
	}
	public static void stopActiveThread(ConfigurazioneAllarmeBean allarme, Logger log, AlarmEngineConfig allarmiConfig) throws Exception{
		_manageActiveThreads(allarme, log, allarmiConfig, "stop");	
	}
	public static void _manageActiveThreads(ConfigurazioneAllarmeBean allarme, Logger log, AlarmEngineConfig allarmiConfig, String operazione) throws Exception{
		
		List<String> post_urls = new ArrayList<String>();
		List<String> post_contents = new ArrayList<String>();
				
		String prefixUrl = allarmiConfig.getActiveAlarm_serviceUrl();
		if(prefixUrl.endsWith("/")==false){
			prefixUrl = prefixUrl + "/";
		}
		prefixUrl = prefixUrl + "gestione/attivi/"+allarme.getNome();
		post_urls.add(prefixUrl);
		post_contents.add("{\"operazione\": \""+operazione+"\"}");
				
		AllarmiUtils.sendToAllarmi(allarmiConfig, post_urls, post_contents, log);
		
	}
	
	public static boolean existsActiveThread(ConfigurazioneAllarmeBean allarme, Logger log, AlarmEngineConfig allarmiConfig) throws Exception{
		if(TipoAllarme.PASSIVO.equals(allarme.getTipoAllarme())){
			// NOTA: il tipo di allarme non è modificabile.
			log.debug("Allarme ["+allarme.getNome()+"] è passivo. Non contiene una immagine");
			return false;
		}
		
		String prefixUrl = allarmiConfig.getActiveAlarm_serviceUrl();
		if(prefixUrl.endsWith("/")==false){
			prefixUrl = prefixUrl + "/";
		}
		prefixUrl = prefixUrl + "gestione/attivi/"+allarme.getNome()+"/image";
		log.debug("Invoke ["+prefixUrl+"] ...");
		HttpRequest request = buildHttpRequest(allarmiConfig, prefixUrl, HttpRequestMethod.GET, null, null);				
		HttpResponse response = HttpUtilities.httpInvoke(request);
		return response.getResultHTTPOperation()==200;
	}
	
	public static String getActiveThreadImage(ConfigurazioneAllarmeBean allarme, Logger log, AlarmEngineConfig allarmiConfig) throws Exception{
		if(TipoAllarme.PASSIVO.equals(allarme.getTipoAllarme())){
			// NOTA: il tipo di allarme non è modificabile.
			log.debug("Allarme ["+allarme.getNome()+"] è passivo. Non contiene una immagine");
			return "L'allarme è passivo";
		}
		
		String prefixUrl = allarmiConfig.getActiveAlarm_serviceUrl();
		if(prefixUrl.endsWith("/")==false){
			prefixUrl = prefixUrl + "/";
		}
		prefixUrl = prefixUrl + "gestione/attivi/"+allarme.getNome()+"/image";
		return AllarmiUtils.sendToAllarme(allarmiConfig, prefixUrl.toString(), log);
	}
	
	public static void refreshActiveThreadState(ConfigurazioneAllarmeBean allarme, Logger log, AlarmEngineConfig allarmiConfig) throws Exception{
		if(TipoAllarme.PASSIVO.equals(allarme.getTipoAllarme())){
			// NOTA: il tipo di allarme non è modificabile.
			log.debug("Allarme ["+allarme.getNome()+"] è passivo. Non contiene una immagine");
			return;
		}
		
		String prefixUrl = allarmiConfig.getActiveAlarm_serviceUrl();
		if(prefixUrl.endsWith("/")==false){
			prefixUrl = prefixUrl + "/";
		}
		prefixUrl = prefixUrl + "gestione/attivi/"+allarme.getNome()+"/refresh";
		AllarmiUtils.sendToAllarme(allarmiConfig, prefixUrl.toString(), log);
	}
	
	public static String getActiveThreadImages(Logger log, AlarmEngineConfig allarmiConfig) throws Exception{

		String prefixUrl = allarmiConfig.getActiveAlarm_serviceUrl();
		if(prefixUrl.endsWith("/")==false){
			prefixUrl = prefixUrl + "/";
		}
		prefixUrl = prefixUrl + "gestione/attivi/image";
		return AllarmiUtils.sendToAllarme(allarmiConfig, prefixUrl.toString(), log);
	}
	
	public static void startActiveThreads(Logger log, AlarmEngineConfig allarmiConfig) throws Exception{
		_manageActiveThreads(log, allarmiConfig, "start");	
	}
	public static void restartActiveThreads(Logger log, AlarmEngineConfig allarmiConfig) throws Exception{
		_manageActiveThreads(log, allarmiConfig, "restart");	
	}
	public static void stopActiveThreads(Logger log, AlarmEngineConfig allarmiConfig) throws Exception{
		_manageActiveThreads(log, allarmiConfig, "stop");	
	}
	public static void _manageActiveThreads(Logger log, AlarmEngineConfig allarmiConfig, String operazione) throws Exception{
		
		List<String> post_urls = new ArrayList<String>();
		List<String> post_contents = new ArrayList<String>();
				
		String prefixUrl = allarmiConfig.getActiveAlarm_serviceUrl();
		if(prefixUrl.endsWith("/")==false){
			prefixUrl = prefixUrl + "/";
		}
		prefixUrl = prefixUrl + "gestione/attivi";
		post_urls.add(prefixUrl);
		post_contents.add("{\"operazione\": \""+operazione+"\"}");
				
		AllarmiUtils.sendToAllarmi(allarmiConfig, post_urls, post_contents, log);
		
	}
	
	public static String getTipoNomeMittente(AllarmeFiltro configurazioneFiltro) {
		if (configurazioneFiltro != null && StringUtils.isNotEmpty(configurazioneFiltro.getNomeFruitore()) && !"*".equals(configurazioneFiltro.getNomeFruitore())) {
			String res = configurazioneFiltro.getTipoFruitore() + "/" + configurazioneFiltro.getNomeFruitore();
			return res;
		}
		return null;
	}
	
	public static String getTipoNomeDestinatario(AllarmeFiltro configurazioneFiltro) {
		if (configurazioneFiltro != null && StringUtils.isNotEmpty(configurazioneFiltro.getNomeErogatore())	&& !"*".equals(configurazioneFiltro.getNomeErogatore())) {
			String res = configurazioneFiltro.getTipoErogatore() + "/" + configurazioneFiltro.getNomeErogatore();
			return res;
		}
		return null;
	}
	
	
	@SuppressWarnings("deprecation")
	public static String getTipoNomeServizio(AllarmeFiltro configurazioneFiltro, Logger log, boolean controlloAllarmiFiltroApiSoggettoErogatore) {
		if (configurazioneFiltro != null && StringUtils.isNotEmpty(configurazioneFiltro.getNomeServizio()) && !"*".equals(configurazioneFiltro.getNomeServizio())) {
			
			IDServizio idServizio = new IDServizio();
			idServizio.setNome(configurazioneFiltro.getNomeServizio());
			idServizio.setTipo(configurazioneFiltro.getTipoServizio());
			idServizio.setVersione(configurazioneFiltro.getVersioneServizio());
			String res;
			try {
				if(controlloAllarmiFiltroApiSoggettoErogatore) {
					IDSoggetto erogatore = new IDSoggetto(configurazioneFiltro.getTipoErogatore(), configurazioneFiltro.getNomeErogatore());
					idServizio.setSoggettoErogatore(erogatore );
					
					res = NamingUtils.getLabelAccordoServizioParteSpecifica(idServizio);
				}
				else {
					res = NamingUtils.getLabelAccordoServizioParteSpecificaSenzaErogatore(idServizio );
				}
				return res;
			} catch (Exception e) {
				log.error(e.getMessage(),e);
			}
		}
		return null;
	}
	
	public static String getAzione(AllarmeFiltro configurazioneFiltro) {
		if (configurazioneFiltro != null && StringUtils.isNotEmpty(configurazioneFiltro.getAzione()) && !"*".equals(configurazioneFiltro.getAzione())) {
			String res = configurazioneFiltro.getAzione();
			return res;
		}
		return null;
	}
	
	private static String normalizeLabelPlugin(ConfigurazioneAllarmeBean allarme){
		String nome = allarme.getPlugin().getLabel();
		StringBuilder bf = new StringBuilder();
		
		String [] tmp = nome.split(" ");
		for (int i = 0; i < tmp.length; i++) {
			String t = tmp[i].trim();
			if(t==null || t.length()<1){
				continue;
			}
			if(Character.isDigit(t.charAt(0))){
				bf.append(t);
			}
			else{
				bf.append((t.charAt(0)+"").toUpperCase());
				if(t.length()>1){
					bf.append(t.substring(1, t.length()));
				}
			}
		}
		
		return bf.toString();
	}
	
	public static String costruisciAliasAllarme(ConfigurazioneAllarmeBean allarme, Logger log, Context context){
		
		StringBuilder bf = new StringBuilder();
		bf.append(normalizeLabelPlugin(allarme));
		
		// Ci viene Concatenato anche il Filtro (non dovrebbe più servire)
		/*
		AllarmeFiltro configurazioneFiltro = allarme.getFiltro();
		if(AllarmiUtils.getTipoNomeMittente(configurazioneFiltro)!=null){
			bf.append("_M-");
			bf.append(AllarmiUtils.getTipoNomeMittente(configurazioneFiltro));
		}
		if(AllarmiUtils.getTipoNomeDestinatario(configurazioneFiltro)!=null){
			bf.append("_D-");
			bf.append(AllarmiUtils.getTipoNomeDestinatario(configurazioneFiltro));
		}
		if(AllarmiUtils.getTipoNomeServizio(configurazioneFiltro)!=null){
			bf.append("_S-");
			bf.append(AllarmiUtils.getTipoNomeServizio(configurazioneFiltro));
		}
		if(AllarmiUtils.getAzione(configurazioneFiltro)!=null){
			bf.append("_A-");
			bf.append(AllarmiUtils.getAzione(configurazioneFiltro));
		}
		/*/
		
		String nome = bf.toString();
		
		String p = "";
		String s = "";
		try{
			if(allarme!=null && allarme.getPlugin()!=null && allarme.getPlugin().getClassName()!=null){
				IDynamicLoader dl = DynamicFactory.getInstance().newDynamicLoader(TipoPlugin.ALLARME, allarme.getTipo(), allarme.getPlugin().getClassName(), log);
				IAlarmProcessing alarm = (IAlarmProcessing) dl.newInstance();
				p = alarm.getAutomaticPrefixName(context);
				//System.out.println("P ["+p+"]");
				s = alarm.getAutomaticSuffixName(context);
				//System.out.println("S ["+s+"]");
			}
				
		}catch(Exception e){
			log.error(e.getMessage(), e);
		}
		
		if(p==null){
			p = "";
		}
		if(s==null){
			s = "";
		}
		return p+nome+s;
	}
	
	public static String getLabelStato(StatoAllarme stato){
		if(stato!=null){
			switch (stato) {
			case OK:
				return "Ok";
			case WARNING:
				return "Warning";
			case ERROR:
				return "Error";
			}
		}
		return null;
	}
}
