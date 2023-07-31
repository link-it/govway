/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2023 Link.it srl (https://link.it). 
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

import java.util.ArrayList;
import java.util.List;

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
	public static boolean autorizzazioneUtente(boolean singlePdD,Logger log,String nomeServlet,LoginHelper loginHelper, StringBuilder bfError) throws Exception{
		if(GestoreAutorizzazioni.permessi==null)
			GestoreAutorizzazioni.init(singlePdD);
		
		
		return GestoreAutorizzazioni.permessi.permettiVisualizzazione(log, nomeServlet,loginHelper, bfError);
	}
	
	// Raccolta servlet in funzionalita
	List<String> servletPdDSinglePdD = null;
	List<String> servletSoggetti = null;
	List<String> servletAccordiServizio = null;
	List<String> servletAccordiCooperazione = null;
	List<String> servletServizi = null;
	List<String> servletRuoli = null;
	List<String> servletScope = null;
	List<String> servletPorteDelegate = null;
	List<String> servletPorteApplicative = null;
	List<String> servletServiziApplicativi = null;
	List<String> servletConnettoriCustom = null;
	List<String> servletPackage = null;
	List<String> servletAuditing = null;
	List<String> servletConfigurazione = null;
	List<String> servletGestioneUtenti = null;
	List<String> servletTracciamento = null;
	List<String> servletDiagnostica = null;
	List<String> servletMonitoraggioApplicativo = null;
	List<String> servletLibraryVersion = null;
	List<String> servletChangePwdModalita = null;
	List<String> servletProtocolProperties = null;
	List<String> servletGruppi = null;
	List<String> servletRegistro = null;
	
	// Associazione diritti alle funzionalita'
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
	PermessiUtente permessiChangePwdModalita = null;
	PermessiUtente permessiProtocolProperties = null;
	PermessiUtente permessiGruppi = null;
	PermessiUtente permessiRegistro = null;

	
	private boolean singlePdD = false;
	public GestoreAutorizzazioni(boolean singlePdD){
		this.singlePdD = singlePdD;
			
		/** Gruppo di servlet che gestiscono le porte di dominio */
		this.servletPdDSinglePdD = new ArrayList<>();
		if(this.singlePdD){
			this.servletPdDSinglePdD.addAll(PddCostanti.getServletPddSinglepdd());
			this.servletPdDSinglePdD.addAll(PddCostanti.getServletPddSoggetti());
		}
		/** Permessi associati alla gestione delle porte di dominio */
		this.permessiPdDSinglePdD = new PermessiUtente();
		this.permessiPdDSinglePdD.setServizi(true);
		
		/** Gruppo di servlet che gestiscono i soggetti */
		this.servletSoggetti = new ArrayList<>();
		this.servletSoggetti.addAll(SoggettiCostanti.getServletSoggetti());
		this.servletSoggetti.addAll(SoggettiCostanti.getServletSoggettiRuoli());
		this.servletSoggetti.addAll(SoggettiCostanti.getServletSoggettiCredenziali());
		this.servletSoggetti.addAll(SoggettiCostanti.getServletSoggettiProprieta());
		this.servletSoggetti.add(SoggettiCostanti.SERVLET_NAME_SOGGETTI_VERIFICA_CERTIFICATI);
		/** Permessi associati alla gestione dei soggetti */
		this.permessiSoggetti = new PermessiUtente();
		this.permessiSoggetti.setServizi(true);
		
		/** Gruppo di servlet che gestiscono gli Accordi di Servizio */
		this.servletAccordiServizio = new ArrayList<>();
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
		this.servletAccordiServizio.addAll(ApiCostanti.getServletApcApi());
		/** Permessi associati alla gestione degli accordi di servizio */
		this.permessiAccordiServizio = new PermessiUtente();
		this.permessiAccordiServizio.setServizi(true);
		this.permessiAccordiServizio.setAccordiCooperazione(true);
				
		/** Gruppo di servlet che gestiscono gli Accordi di Cooperazione */
		this.servletAccordiCooperazione = new ArrayList<>();
		this.servletAccordiCooperazione.addAll(AccordiCooperazioneCostanti.getServletAccordiCooperazione());
		this.servletAccordiCooperazione.addAll(AccordiCooperazioneCostanti.getServletAcAllegati());
		this.servletAccordiCooperazione.addAll(AccordiCooperazioneCostanti.getServletAcPartecipanti());
		/** Permessi associati alla gestione degli accordi di cooperazione */
		this.permessiAccordiCooperazione = new PermessiUtente();
		this.permessiAccordiCooperazione.setAccordiCooperazione(true);

		/** Gruppo di servlet che gestiscono i servizi */
		this.servletServizi = new ArrayList<>();
		this.servletServizi.addAll(AccordiServizioParteSpecificaCostanti.getServletAps());
		this.servletServizi.addAll(AccordiServizioParteSpecificaCostanti.getServletApsAllegati());
		this.servletServizi.addAll(AccordiServizioParteSpecificaCostanti.getServletApsFruitori());
		this.servletServizi.addAll(AccordiServizioParteSpecificaCostanti.getServletApsFruitoriPorteDelegate());
		this.servletServizi.addAll(AccordiServizioParteSpecificaCostanti.getServletApsPorteApplicative());
		this.servletServizi.add(ArchiviCostanti.SERVLET_NAME_DOCUMENTI_EXPORT);
		this.servletServizi.addAll(ErogazioniCostanti.getServletAspsErogazioni());
		this.servletServizi.add(ErogazioniCostanti.SERVLET_NAME_ASPS_EROGAZIONI_VERIFICA_CERTIFICATI);
		/** Permessi associati alla gestione dei servizi */
		this.permessiServizi = new PermessiUtente();
		this.permessiServizi.setServizi(true);
		
		/** Gruppo di servlet che gestiscono i ruoli */
		this.servletRuoli = new ArrayList<>();
		this.servletRuoli.addAll(RuoliCostanti.getServletRuoli());
		/** Permessi associati alla gestione dei ruoli */
		this.permessiRuoli = new PermessiUtente();
		this.permessiRuoli.setServizi(true);
		
		/** Gruppo di servlet che gestiscono gli scope */
		this.servletScope = new ArrayList<>();
		this.servletScope.addAll(ScopeCostanti.getServletScope());
		/** Permessi associati alla gestione degli scope */
		this.permessiScope = new PermessiUtente();
		this.permessiScope.setServizi(true);
		
		/** Gruppo di servlet che gestiscono le porte delegate */
		this.servletPorteDelegate = new ArrayList<>();
		this.servletPorteDelegate.addAll(PorteDelegateCostanti.getServletPorteDelegate());
		this.servletPorteDelegate.add(PorteDelegateCostanti.SERVLET_NAME_PORTE_DELEGATE_MESSAGE_SECURITY);
		this.servletPorteDelegate.addAll(PorteDelegateCostanti.getServletPorteDelegateMessageSecurityRequest());
		this.servletPorteDelegate.addAll(PorteDelegateCostanti.getServletPorteDelegateMessageSecurityResponse());
		this.servletPorteDelegate.addAll(PorteDelegateCostanti.getServletPorteDelegateServizioApplicativo());
		this.servletPorteDelegate.addAll(PorteDelegateCostanti.getServletPorteDelegateRuoli());
		this.servletPorteDelegate.addAll(PorteDelegateCostanti.getServletPorteDelegateScope());
		this.servletPorteDelegate.add(PorteDelegateCostanti.SERVLET_NAME_PORTE_DELEGATE_CORRELAZIONE_APPLICATIVA);
		this.servletPorteDelegate.addAll(PorteDelegateCostanti.getServletPorteDelegateCorrelazioneApplicativaRequest());
		this.servletPorteDelegate.addAll(PorteDelegateCostanti.getServletPorteDelegateCorrelazioneApplicativaResponse());
		this.servletPorteDelegate.add(PorteDelegateCostanti.SERVLET_NAME_PORTE_DELEGATE_MTOM);
		this.servletPorteDelegate.addAll(PorteDelegateCostanti.getServletPorteDelegateMtomRequest());
		this.servletPorteDelegate.addAll(PorteDelegateCostanti.getServletPorteDelegateMtomResponse());
		this.servletPorteDelegate.add(PorteDelegateCostanti.SERVLET_NAME_PORTE_DELEGATE_CONTROLLO_ACCESSI);
		this.servletPorteDelegate.addAll(PorteDelegateCostanti.getServletPorteDelegateAzione());
		this.servletPorteDelegate.add(PorteDelegateCostanti.SERVLET_NAME_PORTE_DELEGATE_VALIDAZIONE_CONTENUTI);
		this.servletPorteDelegate.add(PorteDelegateCostanti.SERVLET_NAME_PORTE_DELEGATE_ABILITAZIONE);
		this.servletPorteDelegate.addAll(PorteDelegateCostanti.getServletPorteDelegateProprietaProtocollo());
		this.servletPorteDelegate.add(PorteDelegateCostanti.SERVLET_NAME_PORTE_DELEGATE_CONNETTORE_DEFAULT);
		this.servletPorteDelegate.add(PorteDelegateCostanti.SERVLET_NAME_PORTE_DELEGATE_CONNETTORE_RIDEFINITO);
		this.servletPorteDelegate.add(PorteDelegateCostanti.SERVLET_NAME_PORTE_DELEGATE_DUMP_CONFIGURAZIONE);
		this.servletPorteDelegate.addAll(PorteDelegateCostanti.getServletPorteDelegateMessageSecurityPropertiesConfig());
		this.servletPorteDelegate.addAll(PorteDelegateCostanti.getServletPorteDelegateConfigurazioneChange());
		this.servletPorteDelegate.add(PorteDelegateCostanti.SERVLET_NAME_PORTE_DELEGATE_GESTIONE_CORS);
		this.servletPorteDelegate.add(PorteDelegateCostanti.SERVLET_NAME_PORTE_DELEGATE_GESTIONE_CANALE);
		this.servletPorteDelegate.add(PorteDelegateCostanti.SERVLET_NAME_PORTE_DELEGATE_VERIFICA_CONNETTORE);
		this.servletPorteDelegate.add(PorteDelegateCostanti.SERVLET_NAME_PORTE_DELEGATE_RESPONSE_CACHING);
		this.servletPorteDelegate.addAll(PorteDelegateCostanti.getServletPorteDelegateResponseCachingConfigurazioneRegola());
		this.servletPorteDelegate.addAll(PorteDelegateCostanti.getServletPorteDelegateTrasformazioni());
		this.servletPorteDelegate.addAll(PorteDelegateCostanti.getServletPorteDelegateAutenticazioneCustomProperties());
		this.servletPorteDelegate.addAll(PorteDelegateCostanti.getServletPorteDelegateAutorizzazioneCustomProperties());
		this.servletPorteDelegate.addAll(PorteDelegateCostanti.getServletPorteDelegateAutorizzazioneContenutiCustomProperties());
		this.servletPorteDelegate.addAll(PorteDelegateCostanti.getServletPorteDelegateExtended());
		
		/** Permessi associati alla gestione delle porte delegate */
		this.permessiPorteDelegate = new PermessiUtente();
		this.permessiPorteDelegate.setServizi(true);
		
		/** Gruppo di servlet che gestiscono le porte applicative */
		this.servletPorteApplicative = new ArrayList<>();
		this.servletPorteApplicative.addAll(PorteApplicativeCostanti.getServletPorteApplicative());
		this.servletPorteApplicative.add(PorteApplicativeCostanti.SERVLET_NAME_PORTE_APPLICATIVE_MESSAGE_SECURITY);
		this.servletPorteApplicative.addAll(PorteApplicativeCostanti.getServletPorteApplicativeMessageSecurityRequest());
		this.servletPorteApplicative.addAll(PorteApplicativeCostanti.getServletPorteApplicativeMessageSecurityResponse());
		this.servletPorteApplicative.addAll(PorteApplicativeCostanti.getServletPorteApplicativeServizioApplicativo());
		this.servletPorteApplicative.addAll(PorteApplicativeCostanti.getServletPorteApplicativeRuoli());
		this.servletPorteApplicative.addAll(PorteApplicativeCostanti.getServletPorteApplicativeScope());
		this.servletPorteApplicative.add(PorteApplicativeCostanti.SERVLET_NAME_PORTE_APPLICATIVE_CORRELAZIONE_APPLICATIVA);
		this.servletPorteApplicative.addAll(PorteApplicativeCostanti.getServletPorteApplicativeCorrelazioneApplicativaRequest());
		this.servletPorteApplicative.addAll(PorteApplicativeCostanti.getServletPorteApplicativeCorrelazioneApplicativaResponse());
		this.servletPorteApplicative.addAll(PorteApplicativeCostanti.getServletPorteApplicativeProprietaProtocollo());
		this.servletPorteApplicative.add(PorteApplicativeCostanti.SERVLET_NAME_PORTE_APPLICATIVE_MTOM);
		this.servletPorteApplicative.addAll(PorteApplicativeCostanti.getServletPorteApplicativeMtomRequest());
		this.servletPorteApplicative.addAll(PorteApplicativeCostanti.getServletPorteApplicativeMtomResponse());
		this.servletPorteApplicative.add(PorteApplicativeCostanti.SERVLET_NAME_PORTE_APPLICATIVE_CONTROLLO_ACCESSI);
		this.servletPorteApplicative.addAll(PorteApplicativeCostanti.getServletPorteApplicativeAzione());
		this.servletPorteApplicative.addAll(PorteApplicativeCostanti.getServletPorteApplicativeSoggetto());
		this.servletPorteApplicative.addAll(PorteApplicativeCostanti.getServletPorteApplicativeServizioApplicativoAutorizzato());
		this.servletPorteApplicative.add(PorteApplicativeCostanti.SERVLET_NAME_PORTE_APPLICATIVE_VALIDAZIONE_CONTENUTI);
		this.servletPorteApplicative.add(PorteApplicativeCostanti.SERVLET_NAME_PORTE_APPLICATIVE_ABILITAZIONE);
		this.servletPorteApplicative.add(PorteApplicativeCostanti.SERVLET_NAME_PORTE_APPLICATIVE_CONNETTORE_DEFAULT);
		this.servletPorteApplicative.add(PorteApplicativeCostanti.SERVLET_NAME_PORTE_APPLICATIVE_CONNETTORE_RIDEFINITO);
		this.servletPorteApplicative.add(PorteApplicativeCostanti.SERVLET_NAME_PORTE_APPLICATIVE_DUMP_CONFIGURAZIONE);
		this.servletPorteApplicative.addAll(PorteApplicativeCostanti.getServletPorteApplicativeMessageSecurityPropertiesConfig());
		this.servletPorteApplicative.addAll(PorteApplicativeCostanti.getServletPorteApplicativeConfigurazioneChange());
		this.servletPorteApplicative.add(PorteApplicativeCostanti.SERVLET_NAME_PORTE_APPLICATIVE_GESTIONE_CORS);
		this.servletPorteApplicative.add(PorteApplicativeCostanti.SERVLET_NAME_PORTE_APPLICATIVE_GESTIONE_CANALE);
		this.servletPorteApplicative.add(PorteApplicativeCostanti.SERVLET_NAME_PORTE_APPLICATIVE_VERIFICA_CONNETTORE);
		this.servletPorteApplicative.add(PorteApplicativeCostanti.SERVLET_NAME_PORTE_APPLICATIVE_RESPONSE_CACHING);
		this.servletPorteApplicative.addAll(PorteApplicativeCostanti.getServletPorteApplicativeResponseCachingConfigurazioneRegola());
		this.servletPorteApplicative.addAll(PorteApplicativeCostanti.getServletPorteApplicativeTrasformazioni());
		this.servletPorteApplicative.addAll(PorteApplicativeCostanti.getServletPorteApplicativeAutenticazioneCustomProperties());
		this.servletPorteApplicative.addAll(PorteApplicativeCostanti.getServletPorteApplicativeAutorizzazioneCustomProperties());
		this.servletPorteApplicative.addAll(PorteApplicativeCostanti.getServletPorteApplicativeAutorizzazioneContenutiCustomProperties());
		this.servletPorteApplicative.add(PorteApplicativeCostanti.SERVLET_NAME_PORTE_APPLICATIVE_CONFIGURAZIONE_CONNETTORI_MULTIPLI);
		this.servletPorteApplicative.addAll(PorteApplicativeCostanti.getServletPorteApplicativeConnettoriMultipli());
		this.servletPorteApplicative.addAll(PorteApplicativeCostanti.getServletPorteApplicativeConnettoriMultipliConfigProperties());
		this.servletPorteApplicative.addAll(PorteApplicativeCostanti.getServletPorteApplicativeExtended());
		
		/** Permessi associati alla gestione delle porte applicative */
		this.permessiPorteApplicative = new PermessiUtente();
		this.permessiPorteApplicative.setServizi(true);
		
		/** Gruppo di servlet che gestiscono i servizi applicativi */
		this.servletServiziApplicativi = new ArrayList<>();
		this.servletServiziApplicativi.addAll(ServiziApplicativiCostanti.getServletServiziApplicativi());
		this.servletServiziApplicativi.addAll(ServiziApplicativiCostanti.getServletServiziApplicativiRuoli());
		this.servletServiziApplicativi.addAll(ServiziApplicativiCostanti.getServletServiziApplicativiCredenziali());
		this.servletServiziApplicativi.addAll(ServiziApplicativiCostanti.getServletServiziApplicativiProprieta());
		this.servletServiziApplicativi.add(ServiziApplicativiCostanti.SERVLET_NAME_SERVIZI_APPLICATIVI_VERIFICA_CERTIFICATI);
		/** Permessi associati alla gestione i servizi applicativi */
		this.permessiServiziApplicativi = new PermessiUtente();
		this.permessiServiziApplicativi.setServizi(true);
		
		/** Gruppo di servlet che gestiscono i connettori custom (condivisi tra soggetti e servizi) */
		this.servletConnettoriCustom = new ArrayList<>();
		this.servletConnettoriCustom.addAll(ConnettoriCostanti.getServletConnettoriCustomProperties());
		/** Permessi associati alla gestione dei connettori custom (condivisi tra soggetti e servizi) */
		this.permessiConnettoriCustom = new PermessiUtente();
		this.permessiConnettoriCustom.setServizi(true);
		
		/** Gruppo di servlet che gestiscono l'importazioni/esportazione delle configurazioni in package */
		this.servletPackage = new ArrayList<>();
		this.servletPackage.addAll(ArchiviCostanti.getServletArchiviExport());
		this.servletPackage.addAll(ArchiviCostanti.getServletArchiviImport());
		/** Permessi associati alla gestione import/export dei package */
		this.permessiPackage = new PermessiUtente();
		this.permessiPackage.setServizi(true);
		
		/** Gruppo di servlet che gestiscono la visualizzazione dei risultati di auditing */
		this.servletAuditing = new ArrayList<>();
		this.servletAuditing.addAll(AuditCostanti.SERVLET_AUDITING);
		this.servletAuditing.addAll(AuditCostanti.SERVLET_AUDITING_DETTAGLIO);
		/** Permessi associati alla visualizzazione dell'auditing */
		this.permessiAuditing = new PermessiUtente();
		this.permessiAuditing.setAuditing(true);
		
		/** Gruppo di servlet che gestiscono la configurazione */
		this.servletConfigurazione = new ArrayList<>();
		this.servletConfigurazione.addAll(AuditCostanti.SERVLET_AUDIT);
		this.servletConfigurazione.addAll(AuditCostanti.SERVLET_AUDIT_FILTRI);
		if(this.singlePdD){
			this.servletConfigurazione.addAll(ConfigurazioneCostanti.getServletConfigurazioneGenerale());
			this.servletConfigurazione.addAll(ConfigurazioneCostanti.getServletConfigurazioneGeneraleListExtended());
			this.servletConfigurazione.addAll(ConfigurazioneCostanti.getServletConfigurazioneDiagnosticaAppender());
			this.servletConfigurazione.addAll(ConfigurazioneCostanti.getServletConfigurazioneDiagnosticaAppenderProperties());
			this.servletConfigurazione.addAll(ConfigurazioneCostanti.getServletConfigurazioneDiagnosticaDatasource());
			this.servletConfigurazione.addAll(ConfigurazioneCostanti.getServletConfigurazioneDiagnosticaDatasourceProperties());
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
			this.servletConfigurazione.add(ConfigurazioneCostanti.SERVLET_NAME_CONFIGURAZIONE_POLICY_GESTIONE_TOKEN_VERIFICA_CERTIFICATI);
			this.servletConfigurazione.addAll(ConfigurazioneCostanti.SERVLET_CONFIGURAZIONE_RESPONSE_CACHING_CONFIGURAZIONE_REGOLA);
			this.servletConfigurazione.addAll(ConfigurazioneCostanti.SERVLET_CONFIGURAZIONE_PROXY_PASS_REGOLA);
			this.servletConfigurazione.addAll(ConfigurazioneCostanti.SERVLET_CONFIGURAZIONE_CANALI);
			this.servletConfigurazione.addAll(ConfigurazioneCostanti.SERVLET_CONFIGURAZIONE_CANALI_NODI);
			this.servletConfigurazione.addAll(ConfigurazioneCostanti.SERVLET_CONFIGURAZIONE_PLUGINS_ARCHIVI);
			this.servletConfigurazione.addAll(ConfigurazioneCostanti.SERVLET_CONFIGURAZIONE_PLUGINS_ARCHIVI_JAR);
			this.servletConfigurazione.addAll(ConfigurazioneCostanti.SERVLET_CONFIGURAZIONE_PLUGINS_CLASSI);
			this.servletConfigurazione.addAll(ConfigurazioneCostanti.SERVLET_CONFIGURAZIONE_ALLARMI);
			this.servletConfigurazione.addAll(ConfigurazioneCostanti.SERVLET_CONFIGURAZIONE_HANDLERS_RICHIESTA);
			this.servletConfigurazione.addAll(ConfigurazioneCostanti.SERVLET_CONFIGURAZIONE_HANDLERS_RISPOSTA);
			this.servletConfigurazione.addAll(ConfigurazioneCostanti.SERVLET_CONFIGURAZIONE_HANDLERS_SERVIZIO);
		}
		this.servletConfigurazione.addAll(ConfigurazioneCostanti.SERVLET_CONFIGURAZIONE_SISTEMA);
		this.servletConfigurazione.add(ConfigurazioneCostanti.SERVLET_NAME_CONFIGURAZIONE_SISTEMA_EXPORTER);
		this.servletConfigurazione.add(ArchiviCostanti.SERVLET_NAME_RESOCONTO_EXPORT);
		/** Permessi associati alla gestione della configurazione */
		this.permessiConfigurazione = new PermessiUtente();
		this.permessiConfigurazione.setSistema(true);
		
		/** Gruppo di servlet che visualizzano la gestione degli utenti */
		this.servletGestioneUtenti = new ArrayList<>();
		this.servletGestioneUtenti.addAll(LoginCostanti.getServletLoginAsSu());
		this.servletGestioneUtenti.addAll(UtentiCostanti.getServletUtenti());
		this.servletGestioneUtenti.addAll(UtentiCostanti.getServletUtentiServizi());
		this.servletGestioneUtenti.addAll(UtentiCostanti.getServletUtentiSoggetti());
		/** Permessi associati alla gestione degli utenti */
		this.permessiGestioneUtenti = new PermessiUtente();
		this.permessiGestioneUtenti.setUtenti(true);
		
		/** Gruppo di servlet che gestiscono la tracciatura */
		this.servletTracciamento = new ArrayList<>();
		this.servletTracciamento.addAll(ArchiviCostanti.getServletArchiviTracciamento());
		this.servletTracciamento.add(ArchiviCostanti.SERVLET_NAME_TRACCE_EXPORT);
		/** Permessi associati alla gestione la tracciatura */
		this.permessiTracciamento = new PermessiUtente();
		this.permessiTracciamento.setDiagnostica(true);
		
		/** Gruppo di servlet che gestiscono la diagnostica */
		this.servletDiagnostica = new ArrayList<>();
		this.servletTracciamento.addAll(ArchiviCostanti.getServletArchiviDiagnostica());
		this.servletDiagnostica.add(ArchiviCostanti.SERVLET_NAME_MESSAGGI_DIAGNOSTICI_EXPORT);
		/** Permessi associati alla gestione la diagnostica */
		this.permessiDiagnostica = new PermessiUtente();
		this.permessiDiagnostica.setDiagnostica(true);
		
		/** Gruppo di servlet che gestiscono il monitoraggio applicativo */
		this.servletMonitoraggioApplicativo = new ArrayList<>();
		this.servletMonitoraggioApplicativo.addAll(MonitorCostanti.getServletMonitor());
		/** Permessi associati alla gestione il monitoraggio applicativo */
		this.permessiMonitoraggioApplicativo = new PermessiUtente();
		this.permessiMonitoraggioApplicativo.setCodeMessaggi(true);
		
		/** Gruppo di servlet che visualizzano la versione delle librerie installate */
		this.servletLibraryVersion = new ArrayList<>();
		this.servletLibraryVersion.add("libInfo.do");
		/** Permessi associati alla visualizzazione delle info sulle librerie installate */
		this.permessiLibraryVersion = new PermessiUtente();
		this.permessiLibraryVersion.setSistema(true);
		
		/** Gruppo di servlet che gestiscono l'utente (password e modalita' di interfaccia) */
		this.servletChangePwdModalita = new ArrayList<>();
		this.servletChangePwdModalita.addAll(UtentiCostanti.getServletUtente());
		/** Permessi associati alla gestione della configurazione */
		this.permessiChangePwdModalita = new PermessiUtente();
		this.permessiChangePwdModalita.setSistema(true);
		this.permessiChangePwdModalita.setAuditing(true);
		this.permessiChangePwdModalita.setCodeMessaggi(true);
		this.permessiChangePwdModalita.setDiagnostica(true);
		this.permessiChangePwdModalita.setServizi(true);
		this.permessiChangePwdModalita.setAccordiCooperazione(true);
		// permettere anche agli utenti che gestiscono altri utenti di modificare la propria password
		this.permessiChangePwdModalita.setUtenti(true);
				
		/** Gruppo di servlet che gestiscono le protocolproperties */
		this.servletProtocolProperties = new ArrayList<>();
		this.servletProtocolProperties.addAll(ProtocolPropertiesCostanti.getServletPp());
		/** Permessi Associati alla gestione delle protocol properties */
		this.permessiProtocolProperties = new PermessiUtente();
		this.permessiProtocolProperties.setServizi(true);
		
		/** Gruppo di servlet che gestiscono i gruppi */
		this.servletGruppi = new ArrayList<>();
		this.servletGruppi.addAll(GruppiCostanti.getServletGruppi());
		/** Permessi associati alla gestione dei gruppi */
		this.permessiGruppi = new PermessiUtente();
		this.permessiGruppi.setSistema(true);
		
		/** Gruppo di servlet che gestiscono il supporto delle funzionalita' di registro */
		this.servletRegistro = new ArrayList<>();
		this.servletRegistro.add(UtilsCostanti.SERVLET_NAME_INFORMAZIONI_UTILIZZO_OGGETTO);
		/** Permessi Associati al supporto delle funzionalita' di registro */
		this.permessiRegistro = new PermessiUtente();
		this.permessiRegistro.setServizi(true);
	}
	
	
	public boolean permettiVisualizzazione(Logger log,String nomeServlet,LoginHelper loginHelper, StringBuilder bfError) throws Exception{
		
		String login = ServletUtils.getUserLoginFromSession(loginHelper.getSession());
		User user = null;
		try{
			if(utentiCore==null) {
				if(bfError!=null) {
					bfError.append("Utenti Core is null");
				}
				return false;
			}
			user = utentiCore.getUser(login);
		}catch(Exception e){
			ControlStationCore.logError(e.getMessage(), e);
			if(bfError!=null) {
				bfError.append("Utente non recuperato: "+e.getMessage());
			}
			return false;
		}
		
		// Se sono loginAsSu guardo che abbia i diritti di utente.
		// Tanto se li possiedo posso in teoria modificare anche i miei fornendomi tutti quelli necessari 
		
		// Check appartenenza
		if(this.singlePdD && this.servletPdDSinglePdD.contains(nomeServlet)){
			boolean p = this.permessiPdDSinglePdD.or(user.getPermessi());
			if(!p && bfError!=null) {
				bfError.append("servlet richiesta non autorizzata; censita nella lista di autorizzazione singlePdD");
			}
			return p;
		}else if(this.servletSoggetti.contains(nomeServlet)){
			boolean p = this.permessiSoggetti.or(user.getPermessi());
			if(!p && bfError!=null) {
				bfError.append("servlet richiesta non autorizzata; censita nella lista di autorizzazione dei soggetti");
			}
			return p;
		}else if(this.servletAccordiCooperazione.contains(nomeServlet)){
			boolean p = this.permessiAccordiCooperazione.or(user.getPermessi());
			if(!p && bfError!=null) {
				bfError.append("servlet richiesta non autorizzata; censita nella lista di autorizzazione degli accordi di cooperazione");
			}
			return p;
		}else if(this.servletAccordiServizio.contains(nomeServlet)){
			//controllo che la servlet richiesta sia consentita per il profilo dell'utente
			boolean servletOk = this.permessiAccordiServizio.or(user.getPermessi());
			
			String tipoAccordo = loginHelper.getParameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_TIPO_ACCORDO); 
			
			//se la richiesta fa parte di un gruppo di servlet consentite 
			//controllo se viene passato un parametro con nome tipo accordo 
			if(servletOk && tipoAccordo != null){
				// per gli accordi parte comune servono i diritti 'S'
				if(tipoAccordo.equals(AccordiServizioParteComuneCostanti.PARAMETRO_VALORE_APC_TIPO_ACCORDO_PARTE_COMUNE)){
					if(!user.getPermessi().isServizi()) {
						if(bfError!=null) {
							bfError.append("servlet richiesta non autorizzata; censita nella lista di autorizzazione delle API");
						}
						return false;
					}
				} else 
					// per gli accordi di servizio composti servono i diritti 'P'
					if(tipoAccordo.equals(AccordiServizioParteComuneCostanti.PARAMETRO_VALORE_APC_TIPO_ACCORDO_SERVIZIO_COMPOSTO)){
						if(!user.getPermessi().isAccordiCooperazione()) {
							if(bfError!=null) {
								bfError.append("servlet richiesta non autorizzata; censita nella lista di autorizzazione degli accordi di cooperazione (servizi composti)");
							}
							return false;
						}
				} else {
					// tipo accordo con valore non buono
					if(bfError!=null) {
						bfError.append("servlet richiesta non autorizzata; censita nella lista di autorizzazione delle API (tipoAccordo '"+tipoAccordo+"' non valido)");
					}
					return false;
				}
			}
			
			return servletOk;
		}else if(this.servletServizi.contains(nomeServlet)){
			boolean p = this.permessiServizi.or(user.getPermessi());
			if(!p && bfError!=null) {
				bfError.append("servlet richiesta non autorizzata; censita nella lista di autorizzazione dei servizi");
			}
			return p;
		}else if(this.servletRuoli.contains(nomeServlet)){
			boolean p = this.permessiRuoli.or(user.getPermessi());
			if(!p && bfError!=null) {
				bfError.append("servlet richiesta non autorizzata; censita nella lista di autorizzazione dei ruoli");
			}
			return p;
		}else if(this.servletScope.contains(nomeServlet)){
			boolean p = this.permessiScope.or(user.getPermessi());
			if(!p && bfError!=null) {
				bfError.append("servlet richiesta non autorizzata; censita nella lista di autorizzazione degli scope");
			}
			return p;
		}else if(this.servletPorteDelegate.contains(nomeServlet)){
			boolean p = this.permessiPorteDelegate.or(user.getPermessi());
			if(!p && bfError!=null) {
				bfError.append("servlet richiesta non autorizzata; censita nella lista di autorizzazione delle porte delegate");
			}
			return p;
		}else if(this.servletPorteApplicative.contains(nomeServlet)){
			boolean p = this.permessiPorteApplicative.or(user.getPermessi());
			if(!p && bfError!=null) {
				bfError.append("servlet richiesta non autorizzata; censita nella lista di autorizzazione delle porte applicativa");
			}
			return p;
		}else if(this.servletServiziApplicativi.contains(nomeServlet)){
			boolean p = this.permessiServiziApplicativi.or(user.getPermessi());
			if(!p && bfError!=null) {
				bfError.append("servlet richiesta non autorizzata; censita nella lista di autorizzazione dei servizi applicativi");
			}
			return p;
		}else if(this.servletConnettoriCustom.contains(nomeServlet)){
			boolean p = this.permessiConnettoriCustom.or(user.getPermessi());
			if(!p && bfError!=null) {
				bfError.append("servlet richiesta non autorizzata; censita nella lista di autorizzazione dei connettori custom");
			}
			return p;
		}else if(this.servletPackage.contains(nomeServlet)){
			boolean p = this.permessiPackage.or(user.getPermessi());
			if(!p && bfError!=null) {
				bfError.append("servlet richiesta non autorizzata; censita nella lista di autorizzazione dei packages");
			}
			return p;
		}else if(this.servletAuditing.contains(nomeServlet)){
			boolean p = this.permessiAuditing.or(user.getPermessi());if(!p && bfError!=null) {
				bfError.append("servlet richiesta non autorizzata; censita nella lista di autorizzazione dell'auditing");
			}
			return p;
		}else if(this.servletConfigurazione.contains(nomeServlet)){
			boolean p = this.permessiConfigurazione.or(user.getPermessi());
			if(!p && bfError!=null) {
				bfError.append("servlet richiesta non autorizzata; censita nella lista di autorizzazione delle pagine di configurazione");
			}
			return p;
		}else if(this.servletGestioneUtenti.contains(nomeServlet)){
			boolean p = this.permessiGestioneUtenti.or(user.getPermessi());
			if(!p && bfError!=null) {
				bfError.append("servlet richiesta non autorizzata; censita nella lista di autorizzazione per gli utenti");
			}
			return p;
		}else if(this.singlePdD && this.servletTracciamento.contains(nomeServlet)){
			boolean p = this.permessiTracciamento.or(user.getPermessi());
			if(!p && bfError!=null) {
				bfError.append("servlet richiesta non autorizzata; censita nella lista di autorizzazione per il tracciamento");
			}
			return p;
		}else if(this.singlePdD && this.servletDiagnostica.contains(nomeServlet)){
			boolean p = this.permessiDiagnostica.or(user.getPermessi());
			if(!p && bfError!=null) {
				bfError.append("servlet richiesta non autorizzata; censita nella lista di autorizzazione per la diagnostica");
			}
			return p;
		}else if(this.servletMonitoraggioApplicativo.contains(nomeServlet)){
			boolean p = this.permessiMonitoraggioApplicativo.or(user.getPermessi());
			if(!p && bfError!=null) {
				bfError.append("servlet richiesta non autorizzata; censita nella lista di autorizzazione per la coda messaggi");
			}
			return p;
		}else if(this.servletLibraryVersion.contains(nomeServlet)){
			boolean p = this.permessiLibraryVersion.or(user.getPermessi());
			if(!p && bfError!=null) {
				bfError.append("servlet richiesta non autorizzata; censita nella lista di autorizzazione per la versione delle librerie");
			}
			return p;
		}else if(this.servletChangePwdModalita.contains(nomeServlet)){
			boolean p = this.permessiChangePwdModalita.or(user.getPermessi());
			if(!p && bfError!=null) {
				bfError.append("servlet richiesta non autorizzata; censita nella lista di autorizzazione per il cambio password");
			}
			return p;
		}else if(this.servletProtocolProperties.contains(nomeServlet)){
			boolean p = this.permessiProtocolProperties.or(user.getPermessi());
			if(!p && bfError!=null) {
				bfError.append("servlet richiesta non autorizzata; censita nella lista di autorizzazione per le proprietà dei protocolli");
			}
			return p;
		}else if(this.servletGruppi.contains(nomeServlet)){
			boolean p = this.permessiGruppi.or(user.getPermessi());
			if(!p && bfError!=null) {
				bfError.append("servlet richiesta non autorizzata; censita nella lista di autorizzazione dei gruppi");
			}
			return p;
		}else if(this.servletRegistro.contains(nomeServlet)){
			boolean p = this.permessiRegistro.or(user.getPermessi());
			if(!p && bfError!=null) {
				bfError.append("servlet richiesta non autorizzata; censita nella lista di autorizzazione del registro");
			}
			return p;
		}else{
			if(bfError!=null) {
				bfError.append("servlet richiesta non autorizzata; non risulta censita");
			}
			String error = "Servlet richiesta non gestita: "+nomeServlet;
			log.error(error);
			return false;
		}
	}
}
