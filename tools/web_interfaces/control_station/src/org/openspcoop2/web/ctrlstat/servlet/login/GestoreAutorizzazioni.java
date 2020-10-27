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


package org.openspcoop2.web.ctrlstat.servlet.login;

import java.util.Vector;

import org.openspcoop2.web.ctrlstat.core.ControlStationCore;
import org.openspcoop2.web.ctrlstat.servlet.ac.AccordiCooperazioneCostanti;
import org.openspcoop2.web.ctrlstat.servlet.apc.AccordiServizioParteComuneCostanti;
import org.openspcoop2.web.ctrlstat.servlet.apc.api.ApiCostanti;
import org.openspcoop2.web.ctrlstat.servlet.aps.AccordiServizioParteSpecificaCostanti;
import org.openspcoop2.web.ctrlstat.servlet.aps.erogazioni.ErogazioniCostanti;
import org.openspcoop2.web.ctrlstat.servlet.archivi.ArchiviCostanti;
import org.openspcoop2.web.ctrlstat.servlet.config.ConfigurazioneCostanti;
import org.openspcoop2.web.ctrlstat.servlet.connettori.ConnettoriCostanti;
import org.openspcoop2.web.ctrlstat.servlet.gruppi.GruppiCostanti;
import org.openspcoop2.web.ctrlstat.servlet.monitor.MonitorCostanti;
import org.openspcoop2.web.ctrlstat.servlet.operazioni.OperazioniCostanti;
import org.openspcoop2.web.ctrlstat.servlet.pa.PorteApplicativeCostanti;
import org.openspcoop2.web.ctrlstat.servlet.pd.PorteDelegateCostanti;
import org.openspcoop2.web.ctrlstat.servlet.pdd.PddCostanti;
import org.openspcoop2.web.ctrlstat.servlet.protocol_properties.ProtocolPropertiesCostanti;
import org.openspcoop2.web.ctrlstat.servlet.ruoli.RuoliCostanti;
import org.openspcoop2.web.ctrlstat.servlet.sa.ServiziApplicativiCostanti;
import org.openspcoop2.web.ctrlstat.servlet.scope.ScopeCostanti;
import org.openspcoop2.web.ctrlstat.servlet.soggetti.SoggettiCostanti;
import org.openspcoop2.web.ctrlstat.servlet.utenti.UtentiCore;
import org.openspcoop2.web.ctrlstat.servlet.utenti.UtentiCostanti;
import org.openspcoop2.web.ctrlstat.servlet.utils.UtilsCostanti;
import org.openspcoop2.web.lib.audit.web.AuditCostanti;
import org.openspcoop2.web.lib.mvc.ServletUtils;
import org.openspcoop2.web.lib.users.dao.PermessiUtente;
import org.openspcoop2.web.lib.users.dao.User;
import org.slf4j.Logger;

/**
 * Mapping delle autorizzazioni necessarie per invocare una servlet
 * 
 * @author Andrea Poli (apoli@link.it)
 * @author Stefano Corallo (corallo@link.it)
 * @author Sandra Giangrandi (sandra@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 * 
 */
public class GestoreAutorizzazioni {
	
	private static GestoreAutorizzazioni permessi = null;
	private static ControlStationCore core = null;
	private static UtentiCore utentiCore = null;
	private static synchronized void init(boolean singlePdD) throws Exception{
		if(GestoreAutorizzazioni.permessi==null){
			GestoreAutorizzazioni.permessi = new GestoreAutorizzazioni(singlePdD);
			core = new ControlStationCore();
			utentiCore = new UtentiCore(core);
		}
	}
	public static boolean autorizzazioneUtente(boolean singlePdD,Logger log,String nomeServlet,LoginHelper loginHelper) throws Exception{
		if(GestoreAutorizzazioni.permessi==null)
			GestoreAutorizzazioni.init(singlePdD);
		
		
		return GestoreAutorizzazioni.permessi.permettiVisualizzazione(log, nomeServlet,loginHelper);
	}
	
	// Raccolta servlet in funzionalita
	Vector<String> servletPdD = null;
	Vector<String> servletPdDSinglePdD = null;
	Vector<String> servletSoggetti = null;
	Vector<String> servletAccordiServizio = null;
	Vector<String> servletAccordiCooperazione = null;
	Vector<String> servletServizi = null;
	Vector<String> servletRuoli = null;
	Vector<String> servletScope = null;
	Vector<String> servletPorteDelegate = null;
	Vector<String> servletPorteApplicative = null;
	Vector<String> servletServiziApplicativi = null;
	Vector<String> servletConnettoriCustom = null;
	Vector<String> servletPackage = null;
	Vector<String> servletAuditing = null;
	Vector<String> servletConfigurazione = null;
	Vector<String> servletGestioneUtenti = null;
	Vector<String> servletTracciamento = null;
	Vector<String> servletDiagnostica = null;
	Vector<String> servletMonitoraggioApplicativo = null;
	Vector<String> servletLibraryVersion = null;
	Vector<String> servletChangePWD_Modalita = null;
	Vector<String> servletOperazioni = null;
	Vector<String> servletProtocolProperties = null;
	Vector<String> servletGruppi = null;
	Vector<String> servletRegistro = null;
	
	// Associazione diritti alle funzionalita'
	PermessiUtente permessiPdD = null;
	PermessiUtente permessiPdDSinglePdD = null;
	PermessiUtente permessiSoggetti = null;
	PermessiUtente permessiAccordiServizio = null;
	PermessiUtente permessiAccordiCooperazione = null;
	PermessiUtente permessiServizi = null;
	PermessiUtente permessiRuoli = null;
	PermessiUtente permessiScope = null;
	PermessiUtente permessiPorteDelegate = null;
	PermessiUtente permessiPorteApplicative = null;
	PermessiUtente permessiServiziApplicativi = null;
	PermessiUtente permessiConnettoriCustom = null;
	PermessiUtente permessiPackage = null;
	PermessiUtente permessiAuditing = null;
	PermessiUtente permessiConfigurazione = null;
	PermessiUtente permessiGestioneUtenti = null;
	PermessiUtente permessiTracciamento = null;
	PermessiUtente permessiDiagnostica = null;
	PermessiUtente permessiMonitoraggioApplicativo = null;
	PermessiUtente permessiMonitoraggioApplicativoSinglePdD = null;
	PermessiUtente permessiLibraryVersion = null;
	PermessiUtente permessiChangePWD_Modalita = null;
	PermessiUtente permessiOperazioni = null;
	PermessiUtente permessiProtocolProperties = null;
	PermessiUtente permessiGruppi = null;
	PermessiUtente permessiRegistro = null;

	
	private boolean singlePdD = false;
	public GestoreAutorizzazioni(boolean singlePdD){
		this.singlePdD = singlePdD;
	
		/** Gruppo di servlet che gestiscono le porte di dominio */
		this.servletPdD = new Vector<String>();
		if(this.singlePdD==false){
			this.servletPdD.addAll(PddCostanti.SERVLET_PDD);
			this.servletPdD.addAll(PddCostanti.SERVLET_PDD_SOGGETTI);
		}
		/** Permessi associati alla gestione delle porte di dominio */
		this.permessiPdD = new PermessiUtente();
		this.permessiPdD.setServizi(true);
		
		/** Gruppo di servlet che gestiscono le porte di dominio */
		this.servletPdDSinglePdD = new Vector<String>();
		if(this.singlePdD){
			this.servletPdDSinglePdD.addAll(PddCostanti.SERVLET_PDD_SINGLEPDD);
			this.servletPdDSinglePdD.addAll(PddCostanti.SERVLET_PDD_SOGGETTI);
		}
		/** Permessi associati alla gestione delle porte di dominio */
		this.permessiPdDSinglePdD = new PermessiUtente();
		this.permessiPdDSinglePdD.setServizi(true);
		
		/** Gruppo di servlet che gestiscono i soggetti */
		this.servletSoggetti = new Vector<String>();
		this.servletSoggetti.addAll(SoggettiCostanti.SERVLET_SOGGETTI);
		this.servletSoggetti.addAll(SoggettiCostanti.SERVLET_SOGGETTI_RUOLI);
		/** Permessi associati alla gestione dei soggetti */
		this.permessiSoggetti = new PermessiUtente();
		this.permessiSoggetti.setServizi(true);
		
		/** Gruppo di servlet che gestiscono gli Accordi di Servizio */
		this.servletAccordiServizio = new Vector<String>();
		this.servletAccordiServizio.addAll(AccordiServizioParteComuneCostanti.SERVLET_APC);
		this.servletAccordiServizio.addAll(AccordiServizioParteComuneCostanti.SERVLET_APC_AZIONI);
		this.servletAccordiServizio.addAll(AccordiServizioParteComuneCostanti.SERVLET_APC_PORT_TYPES);
		this.servletAccordiServizio.addAll(AccordiServizioParteComuneCostanti.SERVLET_APC_PORT_TYPE_OPERATIONS);
		this.servletAccordiServizio.addAll(AccordiServizioParteComuneCostanti.SERVLET_APC_PORT_TYPE_OPERATIONS_MESSAGE);
		this.servletAccordiServizio.addAll(AccordiServizioParteComuneCostanti.SERVLET_APC_ALLEGATI);
		this.servletAccordiServizio.addAll(AccordiServizioParteComuneCostanti.SERVLET_APC_EROGATORI);
		this.servletAccordiServizio.addAll(AccordiServizioParteComuneCostanti.SERVLET_APC_COMPONENTI);
		this.servletAccordiServizio.addAll(AccordiServizioParteComuneCostanti.SERVLET_APC_RESOURCES);
		this.servletAccordiServizio.addAll(AccordiServizioParteComuneCostanti.SERVLET_APC_RESOURCES_RISPOSTE);
		this.servletAccordiServizio.addAll(AccordiServizioParteComuneCostanti.SERVLET_APC_RESOURCES_REPRESENTATIONS);
		this.servletAccordiServizio.addAll(AccordiServizioParteComuneCostanti.SERVLET_APC_RESOURCES_PARAMETERS);
		this.servletAccordiServizio.add(ArchiviCostanti.SERVLET_NAME_DOCUMENTI_EXPORT);
		this.servletAccordiServizio.addAll(ApiCostanti.SERVLET_APC_API);
		/** Permessi associati alla gestione degli accordi di servizio */
		this.permessiAccordiServizio = new PermessiUtente();
		this.permessiAccordiServizio.setServizi(true);
		this.permessiAccordiServizio.setAccordiCooperazione(true);
				
		/** Gruppo di servlet che gestiscono gli Accordi di Cooperazione */
		this.servletAccordiCooperazione = new Vector<String>();
		this.servletAccordiCooperazione.addAll(AccordiCooperazioneCostanti.SERVLET_ACCORDI_COOPERAZIONE);
		this.servletAccordiCooperazione.addAll(AccordiCooperazioneCostanti.SERVLET_AC_ALLEGATI);
		this.servletAccordiCooperazione.addAll(AccordiCooperazioneCostanti.SERVLET_AC_PARTECIPANTI);
		/** Permessi associati alla gestione degli accordi di cooperazione */
		this.permessiAccordiCooperazione = new PermessiUtente();
		this.permessiAccordiCooperazione.setAccordiCooperazione(true);

		/** Gruppo di servlet che gestiscono i servizi */
		this.servletServizi = new Vector<String>();
		this.servletServizi.addAll(AccordiServizioParteSpecificaCostanti.SERVLET_APS);
		this.servletServizi.addAll(AccordiServizioParteSpecificaCostanti.SERVLET_APS_ALLEGATI);
		this.servletServizi.addAll(AccordiServizioParteSpecificaCostanti.SERVLET_APS_FRUITORI);
		this.servletServizi.addAll(AccordiServizioParteSpecificaCostanti.SERVLET_APS_FRUITORI_PORTE_DELEGATE);
		this.servletServizi.addAll(AccordiServizioParteSpecificaCostanti.SERVLET_APS_PORTE_APPLICATIVE);
		this.servletServizi.add(ArchiviCostanti.SERVLET_NAME_DOCUMENTI_EXPORT);
		this.servletServizi.addAll(ErogazioniCostanti.SERVLET_ASPS_EROGAZIONI);
		/** Permessi associati alla gestione dei servizi */
		this.permessiServizi = new PermessiUtente();
		this.permessiServizi.setServizi(true);
		
		/** Gruppo di servlet che gestiscono i ruoli */
		this.servletRuoli = new Vector<String>();
		this.servletRuoli.addAll(RuoliCostanti.SERVLET_RUOLI);
		/** Permessi associati alla gestione dei ruoli */
		this.permessiRuoli = new PermessiUtente();
		this.permessiRuoli.setServizi(true);
		
		/** Gruppo di servlet che gestiscono gli scope */
		this.servletScope = new Vector<String>();
		this.servletScope.addAll(ScopeCostanti.SERVLET_SCOPE);
		/** Permessi associati alla gestione degli scope */
		this.permessiScope = new PermessiUtente();
		this.permessiScope.setServizi(true);
		
		/** Gruppo di servlet che gestiscono le porte delegate */
		this.servletPorteDelegate = new Vector<String>();
		this.servletPorteDelegate.addAll(PorteDelegateCostanti.SERVLET_PORTE_DELEGATE);
		this.servletPorteDelegate.add(PorteDelegateCostanti.SERVLET_NAME_PORTE_DELEGATE_MESSAGE_SECURITY);
		this.servletPorteDelegate.addAll(PorteDelegateCostanti.SERVLET_PORTE_DELEGATE_MESSAGE_SECURITY_REQUEST);
		this.servletPorteDelegate.addAll(PorteDelegateCostanti.SERVLET_PORTE_DELEGATE_MESSAGE_SECURITY_RESPONSE);
		this.servletPorteDelegate.addAll(PorteDelegateCostanti.SERVLET_PORTE_DELEGATE_SERVIZIO_APPLICATIVO);
		this.servletPorteDelegate.addAll(PorteDelegateCostanti.SERVLET_PORTE_DELEGATE_RUOLI);
		this.servletPorteDelegate.addAll(PorteDelegateCostanti.SERVLET_PORTE_DELEGATE_SCOPE);
		this.servletPorteDelegate.add(PorteDelegateCostanti.SERVLET_NAME_PORTE_DELEGATE_CORRELAZIONE_APPLICATIVA);
		this.servletPorteDelegate.addAll(PorteDelegateCostanti.SERVLET_PORTE_DELEGATE_CORRELAZIONE_APPLICATIVA_REQUEST);
		this.servletPorteDelegate.addAll(PorteDelegateCostanti.SERVLET_PORTE_DELEGATE_CORRELAZIONE_APPLICATIVA_RESPONSE);
		this.servletPorteDelegate.add(PorteDelegateCostanti.SERVLET_NAME_PORTE_DELEGATE_MTOM);
		this.servletPorteDelegate.addAll(PorteDelegateCostanti.SERVLET_PORTE_DELEGATE_MTOM_REQUEST);
		this.servletPorteDelegate.addAll(PorteDelegateCostanti.SERVLET_PORTE_DELEGATE_MTOM_RESPONSE);
		this.servletPorteDelegate.add(PorteDelegateCostanti.SERVLET_NAME_PORTE_DELEGATE_CONTROLLO_ACCESSI);
		this.servletPorteDelegate.addAll(PorteDelegateCostanti.SERVLET_PORTE_DELEGATE_AZIONE);
		this.servletPorteDelegate.add(PorteDelegateCostanti.SERVLET_NAME_PORTE_DELEGATE_VALIDAZIONE_CONTENUTI);
		this.servletPorteDelegate.add(PorteDelegateCostanti.SERVLET_NAME_PORTE_DELEGATE_ABILITAZIONE);
		this.servletPorteDelegate.addAll(PorteDelegateCostanti.SERVLET_PORTE_DELEGATE_PROPRIETA_PROTOCOLLO);
		this.servletPorteDelegate.add(PorteDelegateCostanti.SERVLET_NAME_PORTE_DELEGATE_CONNETTORE_DEFAULT);
		this.servletPorteDelegate.add(PorteDelegateCostanti.SERVLET_NAME_PORTE_DELEGATE_CONNETTORE_RIDEFINITO);
		this.servletPorteDelegate.add(PorteDelegateCostanti.SERVLET_NAME_PORTE_DELEGATE_DUMP_CONFIGURAZIONE);
		this.servletPorteDelegate.addAll(PorteDelegateCostanti.SERVLET_PORTE_DELEGATE_MESSAGE_SECURITY_PROPERTIES_CONFIG);
		this.servletPorteDelegate.addAll(PorteDelegateCostanti.SERVLET_PORTE_DELEGATE_CONFIGURAZIONE_CHANGE);
		this.servletPorteDelegate.add(PorteDelegateCostanti.SERVLET_NAME_PORTE_DELEGATE_GESTIONE_CORS);
		this.servletPorteDelegate.add(PorteDelegateCostanti.SERVLET_NAME_PORTE_DELEGATE_GESTIONE_CANALE);
		this.servletPorteDelegate.add(PorteDelegateCostanti.SERVLET_NAME_PORTE_DELEGATE_VERIFICA_CONNETTORE);
		this.servletPorteDelegate.add(PorteDelegateCostanti.SERVLET_NAME_PORTE_DELEGATE_RESPONSE_CACHING);
		this.servletPorteDelegate.addAll(PorteDelegateCostanti.SERVLET_PORTE_DELEGATE_RESPONSE_CACHING_CONFIGURAZIONE_REGOLA);
		this.servletPorteDelegate.addAll(PorteDelegateCostanti.SERVLET_PORTE_DELEGATE_TRASFORMAZIONI);
		this.servletPorteDelegate.addAll(PorteDelegateCostanti.SERVLET_PORTE_DELEGATE_AUTENTICAZIONE_CUSTOM_PROPERTIES);
		this.servletPorteDelegate.addAll(PorteDelegateCostanti.SERVLET_PORTE_DELEGATE_AUTORIZZAZIONE_CUSTOM_PROPERTIES);
		this.servletPorteDelegate.addAll(PorteDelegateCostanti.SERVLET_PORTE_DELEGATE_AUTORIZZAZIONE_CONTENUTI_CUSTOM_PROPERTIES);
		
		/** Permessi associati alla gestione delle porte delegate */
		this.permessiPorteDelegate = new PermessiUtente();
		this.permessiPorteDelegate.setServizi(true);
		
		/** Gruppo di servlet che gestiscono le porte applicative */
		this.servletPorteApplicative = new Vector<String>();
		this.servletPorteApplicative.addAll(PorteApplicativeCostanti.SERVLET_PORTE_APPLICATIVE);
		this.servletPorteApplicative.addAll(PorteApplicativeCostanti.SERVLET_PORTE_APPLICATIVE);
		this.servletPorteApplicative.add(PorteApplicativeCostanti.SERVLET_NAME_PORTE_APPLICATIVE_MESSAGE_SECURITY);
		this.servletPorteApplicative.addAll(PorteApplicativeCostanti.SERVLET_PORTE_APPLICATIVE_MESSAGE_SECURITY_REQUEST);
		this.servletPorteApplicative.addAll(PorteApplicativeCostanti.SERVLET_PORTE_APPLICATIVE_MESSAGE_SECURITY_RESPONSE);
		this.servletPorteApplicative.addAll(PorteApplicativeCostanti.SERVLET_PORTE_APPLICATIVE_SERVIZIO_APPLICATIVO);
		this.servletPorteApplicative.addAll(PorteApplicativeCostanti.SERVLET_PORTE_APPLICATIVE_RUOLI);
		this.servletPorteApplicative.addAll(PorteApplicativeCostanti.SERVLET_PORTE_APPLICATIVE_SCOPE);
		this.servletPorteApplicative.add(PorteApplicativeCostanti.SERVLET_NAME_PORTE_APPLICATIVE_CORRELAZIONE_APPLICATIVA);
		this.servletPorteApplicative.addAll(PorteApplicativeCostanti.SERVLET_PORTE_APPLICATIVE_CORRELAZIONE_APPLICATIVA_REQUEST);
		this.servletPorteApplicative.addAll(PorteApplicativeCostanti.SERVLET_PORTE_APPLICATIVE_CORRELAZIONE_APPLICATIVA_RESPONSE);
		this.servletPorteApplicative.addAll(PorteApplicativeCostanti.SERVLET_PORTE_APPLICATIVE_PROPRIETA_PROTOCOLLO);
		this.servletPorteApplicative.add(PorteApplicativeCostanti.SERVLET_NAME_PORTE_APPLICATIVE_MTOM);
		this.servletPorteApplicative.addAll(PorteApplicativeCostanti.SERVLET_PORTE_APPLICATIVE_MTOM_REQUEST);
		this.servletPorteApplicative.addAll(PorteApplicativeCostanti.SERVLET_PORTE_APPLICATIVE_MTOM_RESPONSE);
		this.servletPorteApplicative.add(PorteApplicativeCostanti.SERVLET_NAME_PORTE_APPLICATIVE_CONTROLLO_ACCESSI);
		this.servletPorteApplicative.addAll(PorteApplicativeCostanti.SERVLET_PORTE_APPLICATIVE_AZIONE);
		this.servletPorteApplicative.addAll(PorteApplicativeCostanti.SERVLET_PORTE_APPLICATIVE_SOGGETTO);
		this.servletPorteApplicative.addAll(PorteApplicativeCostanti.SERVLET_PORTE_APPLICATIVE_SERVIZIO_APPLICATIVO_AUTORIZZATO);
		this.servletPorteApplicative.add(PorteApplicativeCostanti.SERVLET_NAME_PORTE_APPLICATIVE_VALIDAZIONE_CONTENUTI);
		this.servletPorteApplicative.add(PorteApplicativeCostanti.SERVLET_NAME_PORTE_APPLICATIVE_ABILITAZIONE);
		this.servletPorteApplicative.add(PorteApplicativeCostanti.SERVLET_NAME_PORTE_APPLICATIVE_CONNETTORE_DEFAULT);
		this.servletPorteApplicative.add(PorteApplicativeCostanti.SERVLET_NAME_PORTE_APPLICATIVE_CONNETTORE_RIDEFINITO);
		this.servletPorteApplicative.add(PorteApplicativeCostanti.SERVLET_NAME_PORTE_APPLICATIVE_DUMP_CONFIGURAZIONE);
		this.servletPorteApplicative.addAll(PorteApplicativeCostanti.SERVLET_PORTE_APPLICATIVE_MESSAGE_SECURITY_PROPERTIES_CONFIG);
		this.servletPorteApplicative.addAll(PorteApplicativeCostanti.SERVLET_PORTE_APPLICATIVE_CONFIGURAZIONE_CHANGE);
		this.servletPorteApplicative.add(PorteApplicativeCostanti.SERVLET_NAME_PORTE_APPLICATIVE_GESTIONE_CORS);
		this.servletPorteApplicative.add(PorteApplicativeCostanti.SERVLET_NAME_PORTE_APPLICATIVE_GESTIONE_CANALE);
		this.servletPorteApplicative.add(PorteApplicativeCostanti.SERVLET_NAME_PORTE_APPLICATIVE_VERIFICA_CONNETTORE);
		this.servletPorteApplicative.add(PorteApplicativeCostanti.SERVLET_NAME_PORTE_APPLICATIVE_RESPONSE_CACHING);
		this.servletPorteApplicative.addAll(PorteApplicativeCostanti.SERVLET_PORTE_APPLICATIVE_RESPONSE_CACHING_CONFIGURAZIONE_REGOLA);
		this.servletPorteApplicative.addAll(PorteApplicativeCostanti.SERVLET_PORTE_APPLICATIVE_TRASFORMAZIONI);
		this.servletPorteApplicative.addAll(PorteApplicativeCostanti.SERVLET_PORTE_APPLICATIVE_AUTENTICAZIONE_CUSTOM_PROPERTIES);
		this.servletPorteApplicative.addAll(PorteApplicativeCostanti.SERVLET_PORTE_APPLICATIVE_AUTORIZZAZIONE_CUSTOM_PROPERTIES);
		this.servletPorteApplicative.addAll(PorteApplicativeCostanti.SERVLET_PORTE_APPLICATIVE_AUTORIZZAZIONE_CONTENUTI_CUSTOM_PROPERTIES);
		this.servletPorteApplicative.add(PorteApplicativeCostanti.SERVLET_NAME_PORTE_APPLICATIVE_CONFIGURAZIONE_CONNETTORI_MULTIPLI);
		this.servletPorteApplicative.addAll(PorteApplicativeCostanti.SERVLET_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI);
		this.servletPorteApplicative.addAll(PorteApplicativeCostanti.SERVLET_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_CONFIG_PROPERTIES);
		
		/** Permessi associati alla gestione delle porte applicative */
		this.permessiPorteApplicative = new PermessiUtente();
		this.permessiPorteApplicative.setServizi(true);
		
		/** Gruppo di servlet che gestiscono i servizi applicativi */
		this.servletServiziApplicativi = new Vector<String>();
		this.servletServiziApplicativi.addAll(ServiziApplicativiCostanti.SERVLET_SERVIZI_APPLICATIVI);
		this.servletServiziApplicativi.addAll(ServiziApplicativiCostanti.SERVLET_SERVIZI_APPLICATIVI_RUOLI);
		/** Permessi associati alla gestione i servizi applicativi */
		this.permessiServiziApplicativi = new PermessiUtente();
		this.permessiServiziApplicativi.setServizi(true);
		
		/** Gruppo di servlet che gestiscono i connettori custom (condivisi tra soggetti e servizi) */
		this.servletConnettoriCustom = new Vector<String>();
		this.servletConnettoriCustom.addAll(ConnettoriCostanti.SERVLET_CONNETTORI_CUSTOM_PROPERTIES);
		/** Permessi associati alla gestione dei connettori custom (condivisi tra soggetti e servizi) */
		this.permessiConnettoriCustom = new PermessiUtente();
		this.permessiConnettoriCustom.setServizi(true);
		
		/** Gruppo di servlet che gestiscono l'importazioni/esportazione delle configurazioni in package */
		this.servletPackage = new Vector<String>();
		this.servletPackage.addAll(ArchiviCostanti.SERVLET_ARCHIVI_EXPORT);
		this.servletPackage.addAll(ArchiviCostanti.SERVLET_ARCHIVI_IMPORT);
		/** Permessi associati alla gestione import/export dei package */
		this.permessiPackage = new PermessiUtente();
		this.permessiPackage.setServizi(true);
		
		/** Gruppo di servlet che gestiscono la visualizzazione dei risultati di auditing */
		this.servletAuditing = new Vector<String>();
		this.servletAuditing.addAll(AuditCostanti.SERVLET_AUDITING);
		this.servletAuditing.addAll(AuditCostanti.SERVLET_AUDITING_DETTAGLIO);
		/** Permessi associati alla visualizzazione dell'auditing */
		this.permessiAuditing = new PermessiUtente();
		this.permessiAuditing.setAuditing(true);
		
		/** Gruppo di servlet che gestiscono la configurazione */
		this.servletConfigurazione = new Vector<String>();
		this.servletConfigurazione.addAll(AuditCostanti.SERVLET_AUDIT);
		this.servletConfigurazione.addAll(AuditCostanti.SERVLET_AUDIT_FILTRI);
		if(this.singlePdD){
			this.servletConfigurazione.addAll(ConfigurazioneCostanti.SERVLET_CONFIGURAZIONE_GENERALE);
			this.servletConfigurazione.addAll(ConfigurazioneCostanti.SERVLET_CONFIGURAZIONE_GENERALE_LIST_EXTENDED);
			this.servletConfigurazione.addAll(ConfigurazioneCostanti.SERVLET_CONFIGURAZIONE_DIAGNOSTICA_APPENDER);
			this.servletConfigurazione.addAll(ConfigurazioneCostanti.SERVLET_CONFIGURAZIONE_DIAGNOSTICA_APPENDER_PROPERTIES);
			this.servletConfigurazione.addAll(ConfigurazioneCostanti.SERVLET_CONFIGURAZIONE_DIAGNOSTICA_DATASOURCE);
			this.servletConfigurazione.addAll(ConfigurazioneCostanti.SERVLET_CONFIGURAZIONE_DIAGNOSTICA_DATASOURCE_PROPERTIES);
			this.servletConfigurazione.addAll(ConfigurazioneCostanti.SERVLET_CONFIGURAZIONE_TRACCIAMENTO_APPENDER);
			this.servletConfigurazione.addAll(ConfigurazioneCostanti.SERVLET_CONFIGURAZIONE_TRACCIAMENTO_APPENDER_PROPERTIES);
			this.servletConfigurazione.add(ConfigurazioneCostanti.SERVLET_NAME_CONFIGURAZIONE_TRACCIAMENTO_TRANSAZIONI);
			this.servletConfigurazione.addAll(ConfigurazioneCostanti.SERVLET_CONFIGURAZIONE_TRACCIAMENTO_DATASOURCE);
			this.servletConfigurazione.addAll(ConfigurazioneCostanti.SERVLET_CONFIGURAZIONE_TRACCIAMENTO_DATASOURCE_PROPERTIES);
			this.servletConfigurazione.addAll(ConfigurazioneCostanti.SERVLET_CONFIGURAZIONE_ROUTING);
			this.servletConfigurazione.addAll(ConfigurazioneCostanti.SERVLET_CONFIGURAZIONE_ROTTE_ROUTING);
			this.servletConfigurazione.addAll(ConfigurazioneCostanti.SERVLET_CONFIGURAZIONE_ACCESSO_REGISTRO_SERVIZI);
			this.servletConfigurazione.addAll(ConfigurazioneCostanti.SERVLET_CONFIGURAZIONE_REGISTRI);
			this.servletConfigurazione.addAll(ConfigurazioneCostanti.SERVLET_CONFIGURAZIONE_SYSTEM_PROPERTIES);
			this.servletConfigurazione.addAll(ConfigurazioneCostanti.SERVLET_CONFIGURAZIONE_DUMP_APPENDER);
			this.servletConfigurazione.addAll(ConfigurazioneCostanti.SERVLET_CONFIGURAZIONE_DUMP_APPENDER_PROPERTIES);
			this.servletConfigurazione.add(ConfigurazioneCostanti.SERVLET_NAME_CONFIGURAZIONE_DUMP_CONFIGURAZIONE);
			this.servletConfigurazione.addAll(ConfigurazioneCostanti.SERVLET_CONFIGURAZIONE_CONTROLLO_TRAFFICO);
			this.servletConfigurazione.addAll(ConfigurazioneCostanti.SERVLET_CONFIGURAZIONE_POLICY_GESTIONE_TOKEN);
			this.servletConfigurazione.addAll(ConfigurazioneCostanti.SERVLET_CONFIGURAZIONE_RESPONSE_CACHING_CONFIGURAZIONE_REGOLA);
			this.servletConfigurazione.addAll(ConfigurazioneCostanti.SERVLET_CONFIGURAZIONE_PROXY_PASS_REGOLA);
			this.servletConfigurazione.addAll(ConfigurazioneCostanti.SERVLET_CONFIGURAZIONE_CANALI);
			this.servletConfigurazione.addAll(ConfigurazioneCostanti.SERVLET_CONFIGURAZIONE_CANALI_NODI);
		}
		this.servletConfigurazione.addAll(ConfigurazioneCostanti.SERVLET_CONFIGURAZIONE_SISTEMA);
		this.servletConfigurazione.add(ConfigurazioneCostanti.SERVLET_NAME_CONFIGURAZIONE_SISTEMA_EXPORTER);
		this.servletConfigurazione.add(ArchiviCostanti.SERVLET_NAME_RESOCONTO_EXPORT);
		/** Permessi associati alla gestione della configurazione */
		this.permessiConfigurazione = new PermessiUtente();
		this.permessiConfigurazione.setSistema(true);
		
		/** Gruppo di servlet che visualizzano la gestione degli utenti */
		this.servletGestioneUtenti = new Vector<String>();
		this.servletGestioneUtenti.addAll(LoginCostanti.SERVLET_LOGIN_AS_SU);
		this.servletGestioneUtenti.addAll(UtentiCostanti.SERVLET_UTENTI);
		this.servletGestioneUtenti.addAll(UtentiCostanti.SERVLET_UTENTI_SERVIZI);
		this.servletGestioneUtenti.addAll(UtentiCostanti.SERVLET_UTENTI_SOGGETTI);
		/** Permessi associati alla gestione degli utenti */
		this.permessiGestioneUtenti = new PermessiUtente();
		this.permessiGestioneUtenti.setUtenti(true);
		
		/** Gruppo di servlet che gestiscono la tracciatura */
		this.servletTracciamento = new Vector<String>();
		this.servletTracciamento.addAll(ArchiviCostanti.SERVLET_ARCHIVI_TRACCIAMENTO);
		this.servletTracciamento.add(ArchiviCostanti.SERVLET_NAME_TRACCE_EXPORT);
		/** Permessi associati alla gestione la tracciatura */
		this.permessiTracciamento = new PermessiUtente();
		this.permessiTracciamento.setDiagnostica(true);
		
		/** Gruppo di servlet che gestiscono la diagnostica */
		this.servletDiagnostica = new Vector<String>();
		this.servletTracciamento.addAll(ArchiviCostanti.SERVLET_ARCHIVI_DIAGNOSTICA);
		this.servletDiagnostica.add(ArchiviCostanti.SERVLET_NAME_MESSAGGI_DIAGNOSTICI_EXPORT);
		/** Permessi associati alla gestione la diagnostica */
		this.permessiDiagnostica = new PermessiUtente();
		this.permessiDiagnostica.setDiagnostica(true);
		
		/** Gruppo di servlet che gestiscono il monitoraggio applicativo */
		this.servletMonitoraggioApplicativo = new Vector<String>();
		this.servletMonitoraggioApplicativo.addAll(MonitorCostanti.SERVLET_MONITOR);
		/** Permessi associati alla gestione il monitoraggio applicativo */
		this.permessiMonitoraggioApplicativo = new PermessiUtente();
		this.permessiMonitoraggioApplicativo.setCodeMessaggi(true);
		
		/** Gruppo di servlet che visualizzano la versione delle librerie installate */
		this.servletLibraryVersion = new Vector<String>();
		this.servletLibraryVersion.add("libInfo.do");
		/** Permessi associati alla visualizzazione delle info sulle librerie installate */
		this.permessiLibraryVersion = new PermessiUtente();
		this.permessiLibraryVersion.setSistema(true);
		
		/** Gruppo di servlet che gestiscono l'utente (password e modalita' di interfaccia) */
		this.servletChangePWD_Modalita = new Vector<String>();
		this.servletChangePWD_Modalita.addAll(UtentiCostanti.SERVLET_UTENTE);
		/** Permessi associati alla gestione della configurazione */
		this.permessiChangePWD_Modalita = new PermessiUtente();
		this.permessiChangePWD_Modalita.setSistema(true);
		this.permessiChangePWD_Modalita.setAuditing(true);
		this.permessiChangePWD_Modalita.setCodeMessaggi(true);
		this.permessiChangePWD_Modalita.setDiagnostica(true);
		this.permessiChangePWD_Modalita.setServizi(true);
		this.permessiChangePWD_Modalita.setAccordiCooperazione(true);
		// permettere anche agli utenti che gestiscono altri utenti di modificare la propria password
		this.permessiChangePWD_Modalita.setUtenti(true);
		
		/** Gruppo di servlet che gestiscono la ricerca delle operazioni */
		this.servletOperazioni = new Vector<String>();
		this.servletOperazioni.addAll(OperazioniCostanti.SERVLET_OPERAZIONI);
		/** Permessi associati alla ricerca delle operazioni */
		this.permessiOperazioni = new PermessiUtente();
		this.permessiOperazioni.setSistema(true);
		
		/** Gruppo di servlet che gestiscono le protocolproperties */
		this.servletProtocolProperties = new Vector<String>();
		this.servletProtocolProperties.addAll(ProtocolPropertiesCostanti.SERVLET_PP);
		/** Permessi Associati alla gestione delle protocol properties */
		this.permessiProtocolProperties = new PermessiUtente();
		this.permessiProtocolProperties.setServizi(true);
		
		/** Gruppo di servlet che gestiscono i gruppi */
		this.servletGruppi = new Vector<String>();
		this.servletGruppi.addAll(GruppiCostanti.SERVLET_GRUPPI);
		/** Permessi associati alla gestione dei gruppi */
		this.permessiGruppi = new PermessiUtente();
		this.permessiGruppi.setSistema(true);
		
		/** Gruppo di servlet che gestiscono il supporto delle funzionalita' di registro */
		this.servletRegistro = new Vector<String>();
		this.servletRegistro.add(UtilsCostanti.SERVLET_NAME_INFORMAZIONI_UTILIZZO_OGGETTO);
		/** Permessi Associati al supporto delle funzionalita' di registro */
		this.permessiRegistro = new PermessiUtente();
		this.permessiRegistro.setServizi(true);
	}
	
	
	public boolean permettiVisualizzazione(Logger log,String nomeServlet,LoginHelper loginHelper) throws Exception{
		
		String login = ServletUtils.getUserLoginFromSession(loginHelper.getSession());
		User user = null;
		try{
			user = utentiCore.getUser(login);
		}catch(Exception e){
			ControlStationCore.logError(e.getMessage(), e);
			return false;
		}
		
		// Se sono loginAsSu guardo che abbia i diritti di utente.
		// Tanto se li possiedo posso in teoria modificare anche i miei fornendomi tutti quelli necessari 
		
		// Check appartenenza
		if(this.singlePdD==false && this.servletPdD.contains(nomeServlet)){
			return this.permessiPdD.or(user.getPermessi());
		}else if(this.singlePdD && this.servletPdDSinglePdD.contains(nomeServlet)){
			return this.permessiPdDSinglePdD.or(user.getPermessi());
		}else if(this.servletSoggetti.contains(nomeServlet)){
			return this.permessiSoggetti.or(user.getPermessi());
		}else if(this.servletAccordiCooperazione.contains(nomeServlet)){
			return this.permessiAccordiCooperazione.or(user.getPermessi());
		}else if(this.servletAccordiServizio.contains(nomeServlet)){
			//controllo che la servlet richiesta sia consentita per il profilo dell'utente
			boolean servletOk = this.permessiAccordiServizio.or(user.getPermessi());
			
			String tipoAccordo = loginHelper.getParameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_TIPO_ACCORDO); 
			
			//se la richiesta fa parte di un gruppo di servlet consentite 
			//controllo se viene passato un parametro con nome tipo accordo 
			if(servletOk && tipoAccordo != null){
				// per gli accordi parte comune servono i diritti 'S'
				if(tipoAccordo.equals(AccordiServizioParteComuneCostanti.PARAMETRO_VALORE_APC_TIPO_ACCORDO_PARTE_COMUNE)){
					if(!user.getPermessi().isServizi())
						return false;
				} else 
					// per gli accordi di servizio composti servono i diritti 'P'
					if(tipoAccordo.equals(AccordiServizioParteComuneCostanti.PARAMETRO_VALORE_APC_TIPO_ACCORDO_SERVIZIO_COMPOSTO)){
						if(!user.getPermessi().isAccordiCooperazione())
							return false;
				} else {
					// tipo accordo con valore non buono
					return false;
				}
			}
			
			return servletOk;
		}else if(this.servletServizi.contains(nomeServlet)){
			return this.permessiServizi.or(user.getPermessi());
		}else if(this.servletRuoli.contains(nomeServlet)){
			return this.permessiRuoli.or(user.getPermessi());
		}else if(this.servletScope.contains(nomeServlet)){
			return this.permessiScope.or(user.getPermessi());
		}else if(this.servletPorteDelegate.contains(nomeServlet)){
			return this.permessiPorteDelegate.or(user.getPermessi());
		}else if(this.servletPorteApplicative.contains(nomeServlet)){
			return this.permessiPorteApplicative.or(user.getPermessi());
		}else if(this.servletServiziApplicativi.contains(nomeServlet)){
			return this.permessiServiziApplicativi.or(user.getPermessi());
		}else if(this.servletConnettoriCustom.contains(nomeServlet)){
			return this.permessiConnettoriCustom.or(user.getPermessi());
		}else if(this.servletPackage.contains(nomeServlet)){
			return this.permessiPackage.or(user.getPermessi());
		}else if(this.servletAuditing.contains(nomeServlet)){
			return this.permessiAuditing.or(user.getPermessi());
		}else if(this.servletConfigurazione.contains(nomeServlet)){
			return this.permessiConfigurazione.or(user.getPermessi());
		}else if(this.servletGestioneUtenti.contains(nomeServlet)){
			return this.permessiGestioneUtenti.or(user.getPermessi());
		}else if(this.singlePdD && this.servletTracciamento.contains(nomeServlet)){
			return this.permessiTracciamento.or(user.getPermessi());
		}else if(this.singlePdD && this.servletDiagnostica.contains(nomeServlet)){
			return this.permessiDiagnostica.or(user.getPermessi());
		}else if(this.servletMonitoraggioApplicativo.contains(nomeServlet)){
			return this.permessiMonitoraggioApplicativo.or(user.getPermessi());
		}else if(this.servletLibraryVersion.contains(nomeServlet)){
			return this.permessiLibraryVersion.or(user.getPermessi());
		}else if(this.servletChangePWD_Modalita.contains(nomeServlet)){
			return this.permessiChangePWD_Modalita.or(user.getPermessi());
		}else if(this.servletOperazioni.contains(nomeServlet)){
			return this.permessiOperazioni.or(user.getPermessi());
		}else if(this.servletProtocolProperties.contains(nomeServlet)){
			return this.permessiProtocolProperties.or(user.getPermessi());
		}else if(this.servletGruppi.contains(nomeServlet)){
			return this.permessiGruppi.or(user.getPermessi());
		}else if(this.servletRegistro.contains(nomeServlet)){
			return this.permessiRegistro.or(user.getPermessi());
		}else{
			log.error("Servlet richiesta non gestita: "+nomeServlet);
			return false;
		}
	}
}
