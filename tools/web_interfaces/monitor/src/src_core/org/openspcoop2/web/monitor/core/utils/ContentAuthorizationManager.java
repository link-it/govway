package org.openspcoop2.web.monitor.core.utils;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;

import org.openspcoop2.web.monitor.core.bean.ApplicationBean;
import org.openspcoop2.web.monitor.core.logger.LoggerManager;

public class ContentAuthorizationManager {

	private Map<String, List<String>> mappaRuoliPagine = null;

	private Map<String, String> mappaPagineModuli = null;

	private static Logger log = LoggerManager.getPddMonitorCoreLogger();

	private static ContentAuthorizationManager _instance = null;

	public static ContentAuthorizationManager getInstance(){
		if(ContentAuthorizationManager._instance == null)
			init();

		return ContentAuthorizationManager._instance;
	}

	private static synchronized void init(){
		if(ContentAuthorizationManager._instance == null)
			ContentAuthorizationManager._instance = new ContentAuthorizationManager();
	}

	public ContentAuthorizationManager (){
		log.debug("Inizializzazione Content Authorization Manager in corso...");
		this.mappaRuoliPagine = new HashMap<String, List<String>>();
		this.mappaPagineModuli = new HashMap<String,String>();
		load();
		log.debug("Inizializzazione Content Authorization Manager completata.");
	}

	private void load(){
		// Caricamento delle map con le associazioni ruoli / pagine
		this.mappaRuoliPagine.put(ApplicationBean.RUOLO_AMMINISTRATORE, Arrays.asList(ContentAuthorizationCostanti.listaPagineRuoloAmministratore));
		this.mappaRuoliPagine.put(ApplicationBean.RUOLO_CONFIGURATORE, Arrays.asList(ContentAuthorizationCostanti.listaPagineRuoloConfiguratore));
		this.mappaRuoliPagine.put(ApplicationBean.RUOLO_OPERATORE, Arrays.asList(ContentAuthorizationCostanti.listaPagineRuoloOperatore));

		// Associazione pagine moduli
		for (String[] regola : ContentAuthorizationCostanti.listaPagineModuli) {
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
}
