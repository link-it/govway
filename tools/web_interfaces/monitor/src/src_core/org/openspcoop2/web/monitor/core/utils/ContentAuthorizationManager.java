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
package org.openspcoop2.web.monitor.core.utils;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.openspcoop2.web.monitor.core.bean.ApplicationBean;
import org.openspcoop2.web.monitor.core.core.PddMonitorProperties;
import org.openspcoop2.web.monitor.core.logger.LoggerManager;
import org.slf4j.Logger;

/**
 * ContentAuthorizationManager
 * 
 * @author Pintori Giuliano (pintori@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 *
 */
public class ContentAuthorizationManager implements IContentAuthorizationManager{

	private Map<String, List<String>> mappaRuoliPagine = null;

	private Map<String, String> mappaPagineModuli = null;

	private static Logger log = LoggerManager.getPddMonitorCoreLogger();

	private static ContentAuthorizationManager _instance = null;

	private IContentAuthorizationManager extendedContentAuthorizationManager = null;

	public static ContentAuthorizationManager getInstance() throws Exception{ 
		if(ContentAuthorizationManager._instance == null)
			init();

		return ContentAuthorizationManager._instance;
	}

	private static synchronized void init() throws Exception{ 
		if(ContentAuthorizationManager._instance == null)
			ContentAuthorizationManager._instance = new ContentAuthorizationManager();
	}

	public ContentAuthorizationManager () throws Exception{
		log.debug("Inizializzazione Content Authorization Manager in corso...");
		this.mappaRuoliPagine = new HashMap<String, List<String>>();
		this.mappaPagineModuli = new HashMap<String,String>();

		this.extendedContentAuthorizationManager = this.loadExtendedAuthorizationManager(log);

		load();
		log.debug("Inizializzazione Content Authorization Manager completata.");
	}

	private void load(){
		// Caricamento delle map con le associazioni ruoli / pagine
		this.mappaRuoliPagine.put(ApplicationBean.RUOLO_AMMINISTRATORE, Arrays.asList(this.getListaPagineRuoloAmministratore()));
		this.mappaRuoliPagine.put(ApplicationBean.RUOLO_CONFIGURATORE, Arrays.asList(this.getListaPagineRuoloConfiguratore()));
		this.mappaRuoliPagine.put(ApplicationBean.RUOLO_OPERATORE, Arrays.asList(this.getListaPagineRuoloOperatore()));

		// Associazione pagine moduli
		for (String[] regola : this.getListaPagineModuli()) {
			String pagina = regola[0];
			String modulo = regola[1];
			this.mappaPagineModuli.put(pagina, modulo);
		}
	}

	public boolean isRisorsaRichiestaAbilitata(String requestUrl,ApplicationBean applicationBean){
		boolean risorsaDisponibile = false;

		String funzionalita = this.mappaPagineModuli.get(requestUrl);

		// N.B. Se la funzionalita' non e' registrata l'accesso e' vietato
		if(funzionalita != null && applicationBean != null){
			risorsaDisponibile = applicationBean.isFunzionalitaAbilitata(funzionalita);
		}

		if(funzionalita != null)
			log.debug("La funzionalita' ["+funzionalita+"] "+(risorsaDisponibile ? "" : "non ")+"e' abilitata");
		else
			log.debug("La risorsa richiesta non corrisponde a nessuna funzionalita' disponibile nel sistema");

		return risorsaDisponibile;
	}

	public boolean checkRuoloRichiestoPerLaRisorsa(Map<String, Boolean> ruoliUtente, String requestUrl,ApplicationBean applicationBean){
		boolean autorizzatoOperatore = false;
		boolean autorizzatoConfiguratore = false;
		boolean autorizzatoAmministratore = false;

		// Utente senza ruoli non e' autorizzato
		if(ruoliUtente == null || ruoliUtente.isEmpty())
			return false;

		// l'utente possiede ruolo operatore
		if(ruoliUtente.get(ApplicationBean.RUOLO_OPERATORE)){
			autorizzatoOperatore = contains(requestUrl, this.mappaRuoliPagine.get(ApplicationBean.RUOLO_OPERATORE)); 

			// Caso speciale per il live non attivo per l'operatore
			String funzionalita = this.mappaPagineModuli.get(requestUrl);
			if(funzionalita.equals(ApplicationBean.FUNZIONALITA_ESITI_LIVE) && !applicationBean.isFunzionalitaAbilitata(ApplicationBean.FUNZIONALITA_ESITI_LIVE_OPERATORE)){
				autorizzatoOperatore = false;				
			}
			if(funzionalita.equals(ApplicationBean.FUNZIONALITA_TRANSAZIONI_LIVE) && !applicationBean.isFunzionalitaAbilitata(ApplicationBean.FUNZIONALITA_TRANSAZIONI_LIVE_OPERATORE)){
				autorizzatoOperatore = false;				
			}

			// caso speciale cambio password
			if(funzionalita.equals(ApplicationBean.FUNZIONALITA_UTENTI) && !applicationBean.isFunzionalitaAbilitata(ApplicationBean.FUNZIONALITA_GESTIONE_PASSWORD)){
				autorizzatoOperatore = false;				
			}
		}

		// l'utente possiede ruolo configuratore
		if(ruoliUtente.get(ApplicationBean.RUOLO_CONFIGURATORE)){
			autorizzatoConfiguratore = contains(requestUrl, this.mappaRuoliPagine.get(ApplicationBean.RUOLO_CONFIGURATORE)); 


			// caso speciale cambio password
			String funzionalita = this.mappaPagineModuli.get(requestUrl);
			if(funzionalita.equals(ApplicationBean.FUNZIONALITA_UTENTI) && !applicationBean.isFunzionalitaAbilitata(ApplicationBean.FUNZIONALITA_GESTIONE_PASSWORD)){
				autorizzatoOperatore = false;				
			}
		}

		// l'utente possiede ruolo amministratore
		if(ruoliUtente.get(ApplicationBean.RUOLO_AMMINISTRATORE)){
			autorizzatoAmministratore = contains(requestUrl, this.mappaRuoliPagine.get(ApplicationBean.RUOLO_AMMINISTRATORE)); 
		}

		return autorizzatoOperatore || autorizzatoConfiguratore || autorizzatoAmministratore;
	}

	public boolean contains(String requestUrl, List<String> listUrl){

		boolean found = false;
		if(listUrl.size() > 0)
			for (String page : listUrl) {
				if(StringUtils.contains(requestUrl, page)){
					found = true;
					break;
				}
			}
		else
			found = false;

		return found;
	}

	private IContentAuthorizationManager loadExtendedAuthorizationManager(Logger log) throws Exception {
		IContentAuthorizationManager p = null;
		String authManagerClass = null;
		try{
			authManagerClass = PddMonitorProperties.getInstance(log).getExtendedInfoAuthorizationClass();
			if(authManagerClass != null) {
				Class<?> c = Class.forName(authManagerClass);
				Constructor<?> constructor = c.getConstructor();
				p = (IContentAuthorizationManager) constructor.newInstance();
			}
		} catch (Exception e) {
			throw new Exception("Impossibile caricare l'Authorization Manager indicato ["+authManagerClass+"] " + e, e);
		}
		return  p;
	}

	@Override
	public String[] getListaPathConsentiti() {
		if(this.extendedContentAuthorizationManager != null) {
			List<String> toReturn = new ArrayList<>();
			toReturn.addAll(Arrays.asList(ContentAuthorizationCostanti.listaPathConsentiti));
			toReturn.addAll(Arrays.asList(this.extendedContentAuthorizationManager.getListaPathConsentiti()));
			return toReturn.toArray(new String[toReturn.size()]); 
		}
		
		return ContentAuthorizationCostanti.listaPathConsentiti;
	}

	@Override
	public String[] getListaPagineRuoloAmministratore() {
		if(this.extendedContentAuthorizationManager != null) {
			List<String> toReturn = new ArrayList<>();
			toReturn.addAll(Arrays.asList(ContentAuthorizationCostanti.listaPagineRuoloAmministratore));
			toReturn.addAll(Arrays.asList(this.extendedContentAuthorizationManager.getListaPagineRuoloAmministratore()));
			return toReturn.toArray(new String[toReturn.size()]); 
		}
		
		return ContentAuthorizationCostanti.listaPagineRuoloAmministratore;
	}

	@Override
	public String[] getListaPagineRuoloConfiguratore() {
		if(this.extendedContentAuthorizationManager != null) {
			List<String> toReturn = new ArrayList<>();
			toReturn.addAll(Arrays.asList(ContentAuthorizationCostanti.listaPagineRuoloConfiguratore));
			toReturn.addAll(Arrays.asList(this.extendedContentAuthorizationManager.getListaPagineRuoloConfiguratore()));
			return toReturn.toArray(new String[toReturn.size()]); 
		}
		
		return ContentAuthorizationCostanti.listaPagineRuoloConfiguratore;
	}

	@Override
	public String[] getListaPagineRuoloOperatore() {
		if(this.extendedContentAuthorizationManager != null) {
			List<String> toReturn = new ArrayList<>();
			toReturn.addAll(Arrays.asList(ContentAuthorizationCostanti.listaPagineRuoloOperatore));
			toReturn.addAll(Arrays.asList(this.extendedContentAuthorizationManager.getListaPagineRuoloOperatore()));
			return toReturn.toArray(new String[toReturn.size()]); 
		}
		
		return ContentAuthorizationCostanti.listaPagineRuoloOperatore;
	}

	@Override
	public String[][] getListaPagineModuli() {
		if(this.extendedContentAuthorizationManager != null) {
			String[][] listaPagineModuli = this.extendedContentAuthorizationManager.getListaPagineModuli();
			String [][] toReturn = new String[listaPagineModuli.length + ContentAuthorizationCostanti.listaPagineModuli.length][2];
			
			int c = 0;
			for (int i = 0; i < ContentAuthorizationCostanti.listaPagineModuli.length; i++) { 
				toReturn[i] = ContentAuthorizationCostanti.listaPagineModuli[i];
				c++;
			}
			
			for (int i = 0; i < listaPagineModuli.length; i++) { 
				toReturn[c] = listaPagineModuli[i];
				c++;
			}
			
			return toReturn;
		}
		
		return ContentAuthorizationCostanti.listaPagineModuli;
	}

	@Override
	public String[] getListaPagineNoIE8() {
		if(this.extendedContentAuthorizationManager != null) {
			List<String> toReturn = new ArrayList<>();
			toReturn.addAll(Arrays.asList(ContentAuthorizationCostanti.listaPagineNoIE8));
			toReturn.addAll(Arrays.asList(this.extendedContentAuthorizationManager.getListaPagineNoIE8()));
			return toReturn.toArray(new String[toReturn.size()]); 
		}
		
		return ContentAuthorizationCostanti.listaPagineNoIE8;
	}
}
