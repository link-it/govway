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
package org.openspcoop2.web.monitor.core.utils;

import org.openspcoop2.web.monitor.core.bean.ApplicationBean;

/**
 * ContentAuthorizationCostanti
 * 
 * @author Pintori Giuliano (pintori@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 *
 */
public class ContentAuthorizationCostanti {
	
	private ContentAuthorizationCostanti() { /*static only */}

	// Elenco dei path che sono disponibili sempre
	public static String [] listaPathConsentiti = {
		"/a4j/",
		"/resources/",
		"/images/",
		"/css/",
		"/fonts/",
		"/scripts/",
		"/public/error.jsf",
		"/public/timeoutPage.jsf",
		"/public/login.jsf",
		"/FusionCharts/",
		"/index.jsp",
		"/login.jsp",
		"/timeout.jsp",
		"/report/statistica",
		"/report/configurazione",
		"/webjars/",  

		// Cambio Password Scaduta Utente
		"/core/pages/form/userPasswordScaduta.jsf",

		// Servlet callback OAuth2
		"/oauth2/callback",
		"/oauth2/user",

		// Health check endpoint
		"/check",
	};

	// Elenco delle pagine che sono disponibili al ruolo amministratore
	public static String[] listaPagineRuoloAmministratore = {
		// welcome page
		"/commons/pages/welcome.jsf",
		"/commons/pages/about.jsf",
		"/commons/pages/statoPdd.jsf",
		"/commons/pages/modalita.jsf",
		"/commons/pages/soggettoPddMonitor.jsf",

		// Transazioni
		"/transazioni/pages/list/transazioni.jsf",
		"/transazioni/pages/form/transazioni.jsf",
		"/transazioni/pages/form/transazioniLivello2.jsf",
		
		//Servlet Archivio Zip
		"/archivioZipUpload",
		
		// Esiti Live
		"/transazioni/pages/form/esitiLive.jsf",

		// Password Utenti
		"/core/pages/form/user.jsf",
		
		// Ricerche Utente
		"/core/pages/list/ricercheUtenteList.jsf",
		"/core/pages/form/modificaRicercaUtente.jsf",
		
		//Servlet Ricerche Utente
		"/ricercheUpload",
		"/ricercheExporter",

		// Stats
		"/stat/pages/list/configurazioniGenerali.jsf",
		"/stat/pages/form/dettaglioConfigurazione.jsf",
		
		//Servlet Configurazioni Exporter
		"/configurazioniexporter",

		// Eventi
		"/eventi/pages/form/evento.jsf",
		"/eventi/pages/list/eventi.jsf",
		
		// Allarmi
		"/allarmi/pages/form/allarme.jsf",
		"/allarmi/pages/form/dettaglioAllarmeHistory.jsf",
		"/allarmi/pages/list/statoAllarmi.jsf",
		"/allarmi/pages/list/allarmeHistory.jsf",
	}; 

	// Elenco delle pagine che sono disponibili al ruolo configuratore
	public static String[] listaPagineRuoloConfiguratore = {
		// welcome page
		"/commons/pages/welcome.jsf",	
		"/commons/pages/about.jsf",
		"/commons/pages/modalita.jsf",
		"/commons/pages/soggettoPddMonitor.jsf",
			
		// Configurazione Utenti
		"/core/pages/form/user.jsf",
		
		// Ricerche Utente
		"/core/pages/list/ricercheUtenteList.jsf",
		"/core/pages/form/modificaRicercaUtente.jsf",

		//Servlet Ricerche Utente
		"/ricercheUpload",
		"/ricercheExporter",
	};

	// Elenco delle pagine che sono disponibili al ruolo operatore
	public static String[] listaPagineRuoloOperatore = {
		"/commons/pages/statoPdd.jsf",
		"/commons/pages/welcome.jsf",
		"/commons/pages/modalita.jsf",
		"/commons/pages/soggettoPddMonitor.jsf",
			
		// Monitoraggio
		"/commons/pages/about.jsf",
		"/transazioni/pages/list/transazioni.jsf",
		"/transazioni/pages/form/transazioni.jsf",
		"/transazioni/pages/form/transazioniLivello2.jsf",
		"/transazioni/pages/form/dettagliMessaggio.jsf",
		"/transazioni/pages/form/dettagliMessaggioTab.jsf",
		"/transazioni/pages/form/dettaglioDump.jsf",
		"/transazioni/pages/form/esitiLive.jsf",
		"/transazioni/pages/form/fault.jsf",
		"/transazioni/pages/form/tokenInfo.jsf",
		"/transazioni/pages/form/visualizzaDiagnostici.jsf",
		"/transazioni/pages/form/visualizzaTraccia.jsf",
		"/transazioni/pages/form/dettagliTransazioneApplicativoServer.jsf",
		"/transazioni/pages/form/dettaglioDumpTransazioneApplicativoServer.jsf",
		"/transazioni/pages/form/faultTransazioneApplicativoServer.jsf",
		"/transazioni/pages/form/dettaglioDumpMultipart.jsf",
		"/transazioni/pages/form/dettaglioDumpTransazioneApplicativoServerMultipart.jsf",
		
		//Servlet Archivio Zip
		"/archivioZipUpload",

		// Configurazione Utenti
		"/core/pages/form/user.jsf",
		
		// Ricerche Utente
		"/core/pages/list/ricercheUtenteList.jsf",
		"/core/pages/form/modificaRicercaUtente.jsf",
		
		//Servlet Ricerche Utente
		"/ricercheUpload",
		"/ricercheExporter",
		
		// Servlet Exporter
		"/diagnosticiexporter",
		"/tracceexporter",
		"/transazioniexporter",
		"/transazionicsvexporter",

		// Stats
		"/stat/pages/form/analisiStatistica.jsf",
		"/stat/pages/form/analisiStatisticaFiltriRicerca.jsf",
		"/stat/pages/form/andamentoTemporale.jsf",
		"/stat/pages/form/andamentoTemporaleGrafico.jsf",
		"/stat/pages/form/distribSA.jsf",
		"/stat/pages/form/distribSAGrafico.jsf",
		"/stat/pages/form/distribServizio.jsf",
		"/stat/pages/form/distribServizioGrafico.jsf",
		"/stat/pages/form/distribSoggetto.jsf",
		"/stat/pages/form/distribSoggettoGrafico.jsf",
		"/stat/pages/form/distribAzione.jsf",
		"/stat/pages/form/distribAzioneGrafico.jsf",
		"/stat/pages/form/distribErrori.jsf",
		"/stat/pages/form/distribErroriGrafico.jsf",
		"/stat/pages/form/statsPersonalizzate.jsf",
		"/stat/pages/form/statsPersonalizzateGrafico.jsf",
		"/stat/pages/list/configurazioniGenerali.jsf",
		"/stat/pages/form/dettaglioConfigurazione.jsf",
		"/stat/pages/list/statistichePdndTracingList.jsf",
		"/stat/pages/form/statistichePdndTracingDettaglio.jsf",
		"/stat/pages/form/statistichePdndTracingTipiRicerca.jsf",
		
		// Allarmi
		"/allarmi/pages/form/allarme.jsf",
		"/allarmi/pages/form/dettaglioAllarmeHistory.jsf",
		"/allarmi/pages/list/statoAllarmi.jsf",
		"/allarmi/pages/list/allarmeHistory.jsf",
		
		//Servlet Configurazioni Exporter
		"/configurazioniexporter",
		
		//Servlet Tracing PDND Exporter
		"/tracingpdndexporter"
	};

	public static String[][] listaPagineModuli ={

		// Stato della porta
		{"/commons/pages/statoPdd.jsf",ApplicationBean.FUNZIONALITA_STATUS_PDD},
		{"/commons/pages/about.jsf",ApplicationBean.FUNZIONALITA_GENERICHE},
		{"/commons/pages/welcome.jsf",ApplicationBean.FUNZIONALITA_GENERICHE},
		{"/commons/pages/modalita.jsf",ApplicationBean.FUNZIONALITA_GENERICHE},
		{"/commons/pages/soggettoPddMonitor.jsf",ApplicationBean.FUNZIONALITA_GENERICHE},

		// Monitoraggio
		{"/transazioni/pages/list/transazioni.jsf",ApplicationBean.FUNZIONALITA_TRANSAZIONI_BASE},
		{"/transazioni/pages/form/transazioni.jsf",ApplicationBean.FUNZIONALITA_TRANSAZIONI_BASE},
		{"/transazioni/pages/form/transazioniLivello2.jsf",ApplicationBean.FUNZIONALITA_TRANSAZIONI_BASE},
		{"/transazioni/pages/form/dettagliMessaggio.jsf",ApplicationBean.FUNZIONALITA_TRANSAZIONI_BASE},
		{"/transazioni/pages/form/dettagliMessaggioTab.jsf",ApplicationBean.FUNZIONALITA_TRANSAZIONI_BASE},
		{"/transazioni/pages/form/dettaglioDump.jsf",ApplicationBean.FUNZIONALITA_TRANSAZIONI_BASE},
		{"/transazioni/pages/form/fault.jsf",ApplicationBean.FUNZIONALITA_TRANSAZIONI_BASE},
		{"/transazioni/pages/form/tokenInfo.jsf",ApplicationBean.FUNZIONALITA_TRANSAZIONI_BASE},
		{"/transazioni/pages/form/visualizzaDiagnostici.jsf",ApplicationBean.FUNZIONALITA_TRANSAZIONI_BASE},
		{"/transazioni/pages/form/visualizzaTraccia.jsf",ApplicationBean.FUNZIONALITA_TRANSAZIONI_BASE},
		{"/transazioni/pages/form/dettagliTransazioneApplicativoServer.jsf",ApplicationBean.FUNZIONALITA_TRANSAZIONI_BASE},
		{"/transazioni/pages/form/dettaglioDumpTransazioneApplicativoServer.jsf",ApplicationBean.FUNZIONALITA_TRANSAZIONI_BASE},
		{"/transazioni/pages/form/faultTransazioneApplicativoServer.jsf",ApplicationBean.FUNZIONALITA_TRANSAZIONI_BASE},
		{"/transazioni/pages/form/dettaglioDumpMultipart.jsf",ApplicationBean.FUNZIONALITA_TRANSAZIONI_BASE},
		{"/transazioni/pages/form/dettaglioDumpTransazioneApplicativoServerMultipart.jsf",ApplicationBean.FUNZIONALITA_TRANSAZIONI_BASE},
		// Export Transazioni
		{"/diagnosticiexporter",ApplicationBean.FUNZIONALITA_EXPORT_TRANSAZIONI},
		{"/tracceexporter",ApplicationBean.FUNZIONALITA_EXPORT_TRANSAZIONI},
		{"/transazioniexporter",ApplicationBean.FUNZIONALITA_EXPORT_TRANSAZIONI},
		{"/transazionicsvexporter",ApplicationBean.FUNZIONALITA_EXPORT_TRANSAZIONI},
		
		//Servlet Archivio Zip
		{"/archivioZipUpload",ApplicationBean.FUNZIONALITA_EXPORT_TRANSAZIONI},

		// Esiti Live
		{"/transazioni/pages/form/esitiLive.jsf",ApplicationBean.FUNZIONALITA_ESITI_LIVE},

		// Eventi
		{"/eventi/pages/form/evento.jsf",ApplicationBean.FUNZIONALITA_EVENTI},
		{"/eventi/pages/list/eventi.jsf",ApplicationBean.FUNZIONALITA_EVENTI},

		//Utenti
		{"/core/pages/form/user.jsf",ApplicationBean.FUNZIONALITA_UTENTI},
		{"/core/pages/form/userPasswordScaduta.jsf",ApplicationBean.FUNZIONALITA_UTENTI},
		{"/core/pages/list/ricercheUtenteList.jsf",ApplicationBean.FUNZIONALITA_UTENTI},
		{"/core/pages/form/modificaRicercaUtente.jsf",ApplicationBean.FUNZIONALITA_UTENTI},
		
		//Servlet Ricerche Utente
		{"/ricercheUpload",ApplicationBean.FUNZIONALITA_UTENTI},
		{"/ricercheExporter",ApplicationBean.FUNZIONALITA_UTENTI},

		// Stats
		{"/stat/pages/form/analisiStatistica.jsf",ApplicationBean.FUNZIONALITA_STATISTICHE_BASE},
		{"/stat/pages/form/analisiStatisticaFiltriRicerca.jsf",ApplicationBean.FUNZIONALITA_STATISTICHE_BASE},
		{"/stat/pages/list/configurazioniGenerali.jsf",ApplicationBean.FUNZIONALITA_STATISTICHE_BASE},
		{"/stat/pages/form/dettaglioConfigurazione.jsf",ApplicationBean.FUNZIONALITA_STATISTICHE_BASE},
		
		//Servlet Configurazioni Exporter
		{"/configurazioniexporter",ApplicationBean.FUNZIONALITA_STATISTICHE_BASE},
		
		{"/stat/pages/form/andamentoTemporale.jsf",ApplicationBean.FUNZIONALITA_STATISTICHE_BASE},
		{"/stat/pages/form/andamentoTemporaleGrafico.jsf",ApplicationBean.FUNZIONALITA_STATISTICHE_BASE},
		{"/stat/pages/form/distribSA.jsf",ApplicationBean.FUNZIONALITA_STATISTICHE_BASE},
		{"/stat/pages/form/distribSAGrafico.jsf",ApplicationBean.FUNZIONALITA_STATISTICHE_BASE},
		{"/stat/pages/form/distribServizio.jsf",ApplicationBean.FUNZIONALITA_STATISTICHE_BASE},
		{"/stat/pages/form/distribServizioGrafico.jsf",ApplicationBean.FUNZIONALITA_STATISTICHE_BASE},
		{"/stat/pages/form/distribSoggetto.jsf",ApplicationBean.FUNZIONALITA_STATISTICHE_BASE},
		{"/stat/pages/form/distribSoggettoGrafico.jsf",ApplicationBean.FUNZIONALITA_STATISTICHE_BASE},
		{"/stat/pages/form/distribAzione.jsf",ApplicationBean.FUNZIONALITA_STATISTICHE_BASE},
		{"/stat/pages/form/distribAzioneGrafico.jsf",ApplicationBean.FUNZIONALITA_STATISTICHE_BASE},
		{"/stat/pages/form/distribErrori.jsf",ApplicationBean.FUNZIONALITA_STATISTICHE_BASE},
		{"/stat/pages/form/distribErroriGrafico.jsf",ApplicationBean.FUNZIONALITA_STATISTICHE_BASE},
		// Statistiche personalizzate
		{"/stat/pages/form/statsPersonalizzate.jsf",ApplicationBean.FUNZIONALITA_STATISTICHE_PERSONALIZZATE},
		{"/stat/pages/form/statsPersonalizzateGrafico.jsf",ApplicationBean.FUNZIONALITA_STATISTICHE_PERSONALIZZATE},
		
		// Tracing PDND
		{"/stat/pages/list/statistichePdndTracingList.jsf",ApplicationBean.FUNZIONALITA_STATISTICHE_BASE},
		{"/stat/pages/form/statistichePdndTracingDettaglio.jsf",ApplicationBean.FUNZIONALITA_STATISTICHE_BASE},
		{"/stat/pages/form/statistichePdndTracingTipiRicerca.jsf",ApplicationBean.FUNZIONALITA_STATISTICHE_BASE},
		
		//Servlet Tracing PDND Exporter
		{"/tracingpdndexporter",ApplicationBean.FUNZIONALITA_STATISTICHE_BASE},
		
		// Allarmi
		{"/allarmi/pages/form/allarme.jsf",ApplicationBean.FUNZIONALITA_ALLARMI},
		{"/allarmi/pages/form/dettaglioAllarmeHistory.jsf",ApplicationBean.FUNZIONALITA_ALLARMI},
		{"/allarmi/pages/list/statoAllarmi.jsf",ApplicationBean.FUNZIONALITA_ALLARMI},
		{"/allarmi/pages/list/allarmeHistory.jsf",ApplicationBean.FUNZIONALITA_ALLARMI},

	};

	// Elenco delle pagine sulle quali non abilitare la compatibilita' IE8
	public static String[] listaPagineNoIE8 = {
			// Stats
			"/stat/pages/form/andamentoTemporaleGrafico.jsf",
			"/stat/pages/form/distribSAGrafico.jsf",
			"/stat/pages/form/distribServizioGrafico.jsf",
			"/stat/pages/form/distribSoggettoGrafico.jsf",
			"/stat/pages/form/distribAzioneGrafico.jsf",
			"/stat/pages/form/distribErroriGrafico.jsf",
			"/stat/pages/form/statsPersonalizzateGrafico.jsf",
			// Esiti Live
			"/transazioni/pages/form/esitiLive.jsf",
			// Summary
			"/commons/pages/welcome.jsf"
	};
}
