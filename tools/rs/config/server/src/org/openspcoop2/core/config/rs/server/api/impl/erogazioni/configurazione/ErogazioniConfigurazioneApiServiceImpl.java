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
package org.openspcoop2.core.config.rs.server.api.impl.erogazioni.configurazione;

import static org.openspcoop2.core.config.rs.server.api.impl.erogazioni.ErogazioniApiHelper.convert;
import static org.openspcoop2.core.config.rs.server.api.impl.erogazioni.ErogazioniApiHelper.correlazioneApplicativaRichiestaCheckData;
import static org.openspcoop2.core.config.rs.server.api.impl.erogazioni.ErogazioniApiHelper.correlazioneApplicativaRispostaCheckData;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;
import org.openspcoop2.core.commons.Filtri;
import org.openspcoop2.core.commons.Liste;
import org.openspcoop2.core.config.AutorizzazioneRuoli;
import org.openspcoop2.core.config.AutorizzazioneScope;
import org.openspcoop2.core.config.CorrelazioneApplicativa;
import org.openspcoop2.core.config.CorrelazioneApplicativaElemento;
import org.openspcoop2.core.config.CorrelazioneApplicativaRispostaElemento;
import org.openspcoop2.core.config.CorsConfigurazione;
import org.openspcoop2.core.config.GestioneToken;
import org.openspcoop2.core.config.PortaApplicativa;
import org.openspcoop2.core.config.PortaApplicativaAutorizzazioneServiziApplicativi;
import org.openspcoop2.core.config.PortaApplicativaAutorizzazioneServizioApplicativo;
import org.openspcoop2.core.config.PortaApplicativaAutorizzazioneSoggetti;
import org.openspcoop2.core.config.PortaApplicativaAutorizzazioneSoggetto;
import org.openspcoop2.core.config.Proprieta;
import org.openspcoop2.core.config.ResponseCachingConfigurazione;
import org.openspcoop2.core.config.Ruolo;
import org.openspcoop2.core.config.Scope;
import org.openspcoop2.core.config.ServizioApplicativo;
import org.openspcoop2.core.config.Soggetto;
import org.openspcoop2.core.config.ValidazioneContenutiApplicativi;
import org.openspcoop2.core.config.constants.StatoFunzionalitaConWarning;
import org.openspcoop2.core.config.constants.TipoAutenticazione;
import org.openspcoop2.core.config.constants.TipoAutenticazionePrincipal;
import org.openspcoop2.core.config.constants.TipoAutorizzazione;
import org.openspcoop2.core.config.constants.ValidazioneContenutiApplicativiTipo;
import org.openspcoop2.core.config.driver.db.IDServizioApplicativoDB;
import org.openspcoop2.core.config.rs.server.api.ErogazioniConfigurazioneApi;
import org.openspcoop2.core.config.rs.server.api.impl.Enums;
import org.openspcoop2.core.config.rs.server.api.impl.Helper;
import org.openspcoop2.core.config.rs.server.api.impl.IdServizio;
import org.openspcoop2.core.config.rs.server.api.impl.erogazioni.ErogazioniApiHelper;
import org.openspcoop2.core.config.rs.server.api.impl.erogazioni.ErogazioniEnv;
import org.openspcoop2.core.config.rs.server.config.ServerProperties;
import org.openspcoop2.core.config.rs.server.model.APIImplAutenticazioneBasic;
import org.openspcoop2.core.config.rs.server.model.APIImplAutenticazioneCustom;
import org.openspcoop2.core.config.rs.server.model.APIImplAutenticazioneHttps;
import org.openspcoop2.core.config.rs.server.model.APIImplAutenticazionePrincipal;
import org.openspcoop2.core.config.rs.server.model.ApiImplStato;
import org.openspcoop2.core.config.rs.server.model.CachingRisposta;
import org.openspcoop2.core.config.rs.server.model.ControlloAccessiAutenticazione;
import org.openspcoop2.core.config.rs.server.model.ControlloAccessiAutenticazioneToken;
import org.openspcoop2.core.config.rs.server.model.ControlloAccessiAutorizzazione;
import org.openspcoop2.core.config.rs.server.model.ControlloAccessiAutorizzazioneRuoli;
import org.openspcoop2.core.config.rs.server.model.ControlloAccessiAutorizzazioneRuolo;
import org.openspcoop2.core.config.rs.server.model.ControlloAccessiAutorizzazioneScope;
import org.openspcoop2.core.config.rs.server.model.ControlloAccessiAutorizzazioneScopes;
import org.openspcoop2.core.config.rs.server.model.ControlloAccessiAutorizzazioneSoggetti;
import org.openspcoop2.core.config.rs.server.model.ControlloAccessiAutorizzazioneSoggetto;
import org.openspcoop2.core.config.rs.server.model.ControlloAccessiAutorizzazioneView;
import org.openspcoop2.core.config.rs.server.model.ControlloAccessiErogazioneAutorizzazioneApplicativi;
import org.openspcoop2.core.config.rs.server.model.ControlloAccessiErogazioneAutorizzazioneApplicativo;
import org.openspcoop2.core.config.rs.server.model.ControlloAccessiGestioneToken;
import org.openspcoop2.core.config.rs.server.model.CorrelazioneApplicativaRichiesta;
import org.openspcoop2.core.config.rs.server.model.CorrelazioneApplicativaRichiestaEnum;
import org.openspcoop2.core.config.rs.server.model.CorrelazioneApplicativaRichiestaItem;
import org.openspcoop2.core.config.rs.server.model.CorrelazioneApplicativaRisposta;
import org.openspcoop2.core.config.rs.server.model.CorrelazioneApplicativaRispostaEnum;
import org.openspcoop2.core.config.rs.server.model.CorrelazioneApplicativaRispostaItem;
import org.openspcoop2.core.config.rs.server.model.GestioneCors;
import org.openspcoop2.core.config.rs.server.model.ListaCorrelazioneApplicativaRichiesta;
import org.openspcoop2.core.config.rs.server.model.ListaCorrelazioneApplicativaRisposta;
import org.openspcoop2.core.config.rs.server.model.ListaRateLimitingPolicy;
import org.openspcoop2.core.config.rs.server.model.OneOfControlloAccessiAutenticazioneAutenticazione;
import org.openspcoop2.core.config.rs.server.model.RateLimitingCriteriMetricaEnum;
import org.openspcoop2.core.config.rs.server.model.RateLimitingPolicyErogazione;
import org.openspcoop2.core.config.rs.server.model.RateLimitingPolicyErogazioneUpdate;
import org.openspcoop2.core.config.rs.server.model.RateLimitingPolicyErogazioneView;
import org.openspcoop2.core.config.rs.server.model.RateLimitingPolicyItem;
import org.openspcoop2.core.config.rs.server.model.RegistrazioneMessaggi;
import org.openspcoop2.core.config.rs.server.model.StatoFunzionalitaConWarningEnum;
import org.openspcoop2.core.config.rs.server.model.TipoAutenticazioneEnum;
import org.openspcoop2.core.config.rs.server.model.TipoAutenticazionePrincipalToken;
import org.openspcoop2.core.config.rs.server.model.Validazione;
import org.openspcoop2.core.controllo_traffico.AttivazionePolicy;
import org.openspcoop2.core.controllo_traffico.AttivazionePolicyFiltro;
import org.openspcoop2.core.controllo_traffico.AttivazionePolicyRaggruppamento;
import org.openspcoop2.core.controllo_traffico.beans.InfoPolicy;
import org.openspcoop2.core.controllo_traffico.constants.RuoloPolicy;
import org.openspcoop2.core.controllo_traffico.utils.PolicyUtilities;
import org.openspcoop2.core.id.IDPortaApplicativa;
import org.openspcoop2.core.id.IDServizioApplicativo;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.core.registry.AccordoServizioParteSpecifica;
import org.openspcoop2.core.registry.IdSoggetto;
import org.openspcoop2.core.registry.beans.AccordoServizioParteComuneSintetico;
import org.openspcoop2.core.registry.constants.CredenzialeTipo;
import org.openspcoop2.core.registry.constants.RuoloContesto;
import org.openspcoop2.core.registry.constants.RuoloTipologia;
import org.openspcoop2.core.registry.constants.ScopeContesto;
import org.openspcoop2.core.registry.driver.FiltroRicercaRuoli;
import org.openspcoop2.core.registry.driver.FiltroRicercaScope;
import org.openspcoop2.core.registry.driver.db.IDSoggettoDB;
import org.openspcoop2.pdd.core.autenticazione.ParametriAutenticazioneBasic;
import org.openspcoop2.pdd.core.autenticazione.ParametriAutenticazionePrincipal;
import org.openspcoop2.utils.service.BaseImpl;
import org.openspcoop2.utils.service.authorization.AuthorizationConfig;
import org.openspcoop2.utils.service.authorization.AuthorizationManager;
import org.openspcoop2.utils.service.beans.ProfiloEnum;
import org.openspcoop2.utils.service.beans.utils.BaseHelper;
import org.openspcoop2.utils.service.beans.utils.ListaUtils;
import org.openspcoop2.utils.service.context.IContext;
import org.openspcoop2.utils.service.fault.jaxrs.FaultCode;
import org.openspcoop2.web.ctrlstat.core.Search;
import org.openspcoop2.web.ctrlstat.costanti.CostantiControlStation;
import org.openspcoop2.web.ctrlstat.servlet.ApiKeyState;
import org.openspcoop2.web.ctrlstat.servlet.ConsoleUtilities;
import org.openspcoop2.web.ctrlstat.servlet.config.ConfigurazioneUtilities;
import org.openspcoop2.web.ctrlstat.servlet.pa.PorteApplicativeCostanti;
import org.openspcoop2.web.ctrlstat.servlet.ruoli.RuoliCore;
import org.openspcoop2.web.lib.mvc.TipoOperazione;
/**
 * ErogazioniConfigurazioneApiServiceImpl
 * 
 * @author $Author$
 * @version $Rev$, $Date$
 * 
 */
public class ErogazioniConfigurazioneApiServiceImpl extends BaseImpl implements ErogazioniConfigurazioneApi {

	public ErogazioniConfigurazioneApiServiceImpl(){
		super(org.slf4j.LoggerFactory.getLogger(ErogazioniConfigurazioneApiServiceImpl.class));
	}

	private AuthorizationConfig getAuthorizationConfig() throws Exception{
		return new AuthorizationConfig(ServerProperties.getInstance().getProperties());
	}

    /**
     * Aggiunta di applicativi all'elenco degli applicativi autorizzati
     *
     * Questa operazione consente di aggiungere applicativi all'elenco degli applicativi autorizzati
     *
     */
	@Override
    public void addErogazioneControlloAccessiAutorizzazioneApplicativi(ControlloAccessiErogazioneAutorizzazioneApplicativo body, String nome, Integer versione, ProfiloEnum profilo, String soggetto, String gruppo, String tipoServizio) {
		IContext context = this.getContext();
		try {
			context.getLogger().info("Invocazione in corso ...");

			AuthorizationManager.authorize(context, getAuthorizationConfig());
			context.getLogger().debug("Autorizzazione completata con successo");
			
			BaseHelper.throwIfNull(body);
			
			final ErogazioniConfEnv env = new ErogazioniConfEnv(context.getServletRequest(), profilo, soggetto, context, nome, versione, gruppo, tipoServizio );
			final PortaApplicativa pa = env.paCore.getPortaApplicativa(env.idPa);

			if ( !TipoAutorizzazione.isAuthenticationRequired(pa.getAutorizzazione()) ) {
				throw FaultCode.RICHIESTA_NON_VALIDA.toException("L'autorizzazione per richiedente non è abilitata");
			}
			
			IdSoggetto idSoggettoProprietarioSA = (IdSoggetto) env.idSoggetto.clone();
			if(org.apache.commons.lang.StringUtils.isNotEmpty(body.getSoggetto())) {
				idSoggettoProprietarioSA.setNome(body.getSoggetto());
				try {
					idSoggettoProprietarioSA.setId(env.soggettiCore.getIdSoggetto(body.getSoggetto(),env.tipo_soggetto));
				} catch (Exception e) {}
			}
			
			final IDServizioApplicativo idSA = new IDServizioApplicativo();
			idSA.setIdSoggettoProprietario(idSoggettoProprietarioSA.toIDSoggetto());
			idSA.setNome(body.getApplicativo());
			
			final ServizioApplicativo sa = BaseHelper.supplyOrNonValida( 
					() -> env.saCore.getServizioApplicativo(idSA),
					"Servizio Applicativo " + idSA.toString()
				);
					
			
			final org.openspcoop2.core.config.constants.CredenzialeTipo tipoAutenticazione = org.openspcoop2.core.config.constants.CredenzialeTipo.toEnumConstant(pa.getAutenticazione());	
			Boolean appId = null;
			if(org.openspcoop2.core.config.constants.CredenzialeTipo.APIKEY.equals(tipoAutenticazione)) {
				ApiKeyState apiKeyState =  new ApiKeyState(env.paCore.getParametroAutenticazione(pa.getAutenticazione(), pa.getProprietaAutenticazioneList()));
				appId = apiKeyState.appIdSelected;
			}
			List<IDServizioApplicativoDB> saCompatibili = env.saCore.soggettiServizioApplicativoList(idSoggettoProprietarioSA.toIDSoggetto(),env.userLogin,tipoAutenticazione, appId);
			if(env.protocolFactory.createProtocolConfiguration().isSupportoAutenticazioneApplicativiErogazioni()) {
				if (!BaseHelper.findFirst(saCompatibili, s -> s.getId().equals(sa.getId())).isPresent()) {
					throw FaultCode.RICHIESTA_NON_VALIDA.toException("Il tipo di credenziali dell'Applicativo non sono compatibili con l'autenticazione impostata nell'erogazione selezionata");
				}
			}
			
			if (pa.getServiziApplicativiAutorizzati() == null)
				pa.setServiziApplicativiAutorizzati(new PortaApplicativaAutorizzazioneServiziApplicativi());
			
			if ( BaseHelper.findFirst(
						pa.getServiziApplicativiAutorizzati().getServizioApplicativoList(),
						s -> ( (sa.getNome().equals(s.getNome())) && (sa.getTipoSoggettoProprietario().equals(s.getTipoSoggettoProprietario())) && (sa.getNomeSoggettoProprietario().equals(s.getNomeSoggettoProprietario())) )
						).isPresent()
				) {
				throw FaultCode.CONFLITTO.toException("Servizio Applicativo già associato");
			}
			
			env.requestWrapper.overrideParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID, pa.getId().toString());
			env.requestWrapper.overrideParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_SOGGETTO, idSoggettoProprietarioSA.getId().toString());
			env.requestWrapper.overrideParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_SERVIZIO_APPLICATIVO_AUTORIZZATO, sa.getId().toString());
			if (!env.paHelper.porteAppServizioApplicativoAutorizzatiCheckData(TipoOperazione.ADD)) {
				throw FaultCode.RICHIESTA_NON_VALIDA.toException(StringEscapeUtils.unescapeHtml(  env.pd.getMessage() ));
			}
			
			PortaApplicativaAutorizzazioneServizioApplicativo paSaAutorizzato = new PortaApplicativaAutorizzazioneServizioApplicativo();
			paSaAutorizzato.setNome(sa.getNome());
			paSaAutorizzato.setTipoSoggettoProprietario(idSoggettoProprietarioSA.getTipo());
			paSaAutorizzato.setNomeSoggettoProprietario(idSoggettoProprietarioSA.getNome());

			pa.getServiziApplicativiAutorizzati().addServizioApplicativo(paSaAutorizzato);			
			env.paCore.performUpdateOperation(env.userLogin, false, pa);
			
			context.getLogger().info("Invocazione completata con successo");
        
			// Bug Fix: altrimenti viene generato 204
			context.getServletResponse().setStatus(201);
		}
		catch(javax.ws.rs.WebApplicationException e) {
			context.getLogger().error_except404("Invocazione terminata con errore '4xx': %s",e, e.getMessage());
			throw e;
		}
		catch(Throwable e) {
			context.getLogger().error("Invocazione terminata con errore: %s",e, e.getMessage());
			throw FaultCode.ERRORE_INTERNO.toException(e);
		}
    }
    
    /**
     * Aggiunta di soggetti all'elenco dei soggetti autorizzati
     *
     * Questa operazione consente di aggiungere soggetti all'elenco dei soggetti autorizzati
     *
     */
	@Override
    public void addErogazioneControlloAccessiAutorizzazioneSoggetti(ControlloAccessiAutorizzazioneSoggetto body, String nome, Integer versione, ProfiloEnum profilo, String soggetto, String gruppo, String tipoServizio) {
		IContext context = this.getContext();
		try {
			
			// Continua da qui, assicurati che l'autorizzazione puntuale sia abilitata guardando al TipoAutorizzazioneEnum
			context.getLogger().info("Invocazione in corso ...");     

			AuthorizationManager.authorize(context, getAuthorizationConfig());
			context.getLogger().debug("Autorizzazione completata con successo");     
                        
			BaseHelper.throwIfNull(body);
			
			final ErogazioniConfEnv env = new ErogazioniConfEnv(context.getServletRequest(), profilo, soggetto, context, nome, versione, gruppo, tipoServizio );
			final PortaApplicativa pa = env.paCore.getPortaApplicativa(env.idPa);
			
			if ( !TipoAutorizzazione.isAuthenticationRequired(pa.getAutorizzazione())) {
				throw FaultCode.RICHIESTA_NON_VALIDA.toException("L'autorizzazione per richiedente per soggetti non è abilitata");
			}
			
			final IDSoggetto daAutenticareID = new IDSoggetto(env.tipo_soggetto, body.getSoggetto());
			final Soggetto daAutenticare = BaseHelper.supplyOrNonValida( 
					() -> env.soggettiCore.getSoggetto(daAutenticareID),
					"Soggetto " + body.getSoggetto() + " da autenticare"
				);
			
			final CredenzialeTipo tipoAutenticazione = CredenzialeTipo.toEnumConstant(pa.getAutenticazione());	
			Boolean appId = null;
			if(CredenzialeTipo.APIKEY.equals(tipoAutenticazione)) {
				ApiKeyState apiKeyState =  new ApiKeyState(env.paCore.getParametroAutenticazione(pa.getAutenticazione(), pa.getProprietaAutenticazioneList()));
				appId = apiKeyState.appIdSelected;
			}
			final List<String> tipiSoggettiGestitiProtocollo = env.soggettiCore.getTipiSoggettiGestitiProtocollo(env.tipo_protocollo);
			
			// L'ultimo parametro è da configurare quando utilizzeremo il multitenant 
			final List<IDSoggettoDB> soggettiCompatibili = env.soggettiCore.getSoggettiFromTipoAutenticazione(tipiSoggettiGestitiProtocollo, null, tipoAutenticazione, appId, null);
			if(env.protocolFactory.createProtocolConfiguration().isSupportoAutenticazioneSoggetti()) {
				if (!BaseHelper.findFirst(soggettiCompatibili, s -> { 
						return (daAutenticare.getTipo().equals(s.getTipo()) && daAutenticare.getNome().equals(s.getNome()) ); 
					}).isPresent()) {
					throw FaultCode.RICHIESTA_NON_VALIDA.toException("Il tipo di credenziali del Soggetto non sono compatibili con l'autenticazione impostata nell'erogazione selezionata");
				}
			}
			
			if (pa.getSoggetti() == null) pa.setSoggetti(new PortaApplicativaAutorizzazioneSoggetti());
			
			if ( BaseHelper.findFirst(
					pa.getSoggetti().getSoggettoList(),
					sogg -> new IDSoggetto(sogg.getTipo(),sogg.getNome()).equals(daAutenticareID)
					).isPresent()
				) {
				throw FaultCode.CONFLITTO.toException("Soggetto da autorizzare già presente");
			}
			
			env.requestWrapper.overrideParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID, pa.getId().toString());
			env.requestWrapper.overrideParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_SOGGETTO, daAutenticare.getId().toString());
			if (!env.paHelper.porteAppSoggettoCheckData(TipoOperazione.ADD)) {
				throw FaultCode.RICHIESTA_NON_VALIDA.toException(StringEscapeUtils.unescapeHtml(  env.pd.getMessage() ));
			}
			
		
			PortaApplicativaAutorizzazioneSoggetto paSoggetto = new PortaApplicativaAutorizzazioneSoggetto();
			paSoggetto.setTipo(daAutenticare.getTipo());
			paSoggetto.setNome(daAutenticare.getNome());
			
			pa.getSoggetti().addSoggetto(paSoggetto);			
        
			env.paCore.performUpdateOperation(env.userLogin, false, pa);
			
			context.getLogger().info("Invocazione completata con successo");
        
			// Bug Fix: altrimenti viene generato 204
			context.getServletResponse().setStatus(201);
		}
		catch(javax.ws.rs.WebApplicationException e) {
			context.getLogger().error_except404("Invocazione terminata con errore '4xx': %s",e, e.getMessage());
			throw e;
		}
		catch(Throwable e) {
			context.getLogger().error("Invocazione terminata con errore: %s",e, e.getMessage());
			throw FaultCode.ERRORE_INTERNO.toException(e);
		}
    }
    
    /**
     * Aggiunta di ruoli all'elenco dei ruoli autorizzati
     *
     * Questa operazione consente di aggiungere ruoli all'elenco dei ruoli autorizzati
     *
     */
	@Override
    public void addErogazioneControlloAccessiAutorizzazioneRuoli(ControlloAccessiAutorizzazioneRuolo body, String nome, Integer versione, ProfiloEnum profilo, String soggetto, String gruppo, String tipoServizio) {
		IContext context = this.getContext();
		try {
			context.getLogger().info("Invocazione in corso ...");
			
			AuthorizationManager.authorize(context, getAuthorizationConfig());
			context.getLogger().debug("Autorizzazione completata con successo");
			
			BaseHelper.throwIfNull(body);
			
			final ErogazioniConfEnv env = new ErogazioniConfEnv(context.getServletRequest(), profilo, soggetto, context, nome, versione, gruppo, tipoServizio );
			final PortaApplicativa pa = env.paCore.getPortaApplicativa(env.idPa);
			
			if ( !TipoAutorizzazione.isRolesRequired(pa.getAutorizzazione()) ) {
				throw FaultCode.RICHIESTA_NON_VALIDA.toException("L'autorizzazione per ruoli non è abilitata per il gruppo scelto");	
			}
			
			final RuoliCore ruoliCore = new RuoliCore(env.stationCore);
			BaseHelper.supplyOrNonValida( 
					() -> ruoliCore.getRuolo(body.getRuolo())
					, "Ruolo " + body.getRuolo()
				);
			
			if(pa.getRuoli()==null){
				pa.setRuoli(new AutorizzazioneRuoli());
			}
			
			FiltroRicercaRuoli filtroRuoli = new FiltroRicercaRuoli();
			filtroRuoli.setContesto(RuoloContesto.PORTA_APPLICATIVA);
			filtroRuoli.setTipologia(RuoloTipologia.QUALSIASI);
			if(TipoAutorizzazione.isInternalRolesRequired(pa.getAutorizzazione()) ){
				filtroRuoli.setTipologia(RuoloTipologia.INTERNO);
			}
			else if(TipoAutorizzazione.isExternalRolesRequired(pa.getAutorizzazione()) ){
				filtroRuoli.setTipologia(RuoloTipologia.ESTERNO);
			}
			
			List<String> ruoliAmmessi = env.stationCore.getAllRuoli(filtroRuoli);
			
			if ( !ruoliAmmessi.contains(body.getRuolo())) {
				throw FaultCode.RICHIESTA_NON_VALIDA.toException("Il ruolo " + body.getRuolo() + "non è tra i ruoli ammissibili per il gruppo"); 
			}
			
			final List<String> ruoliPresenti = pa.getRuoli().getRuoloList().stream().map( r -> r.getNome()).collect(Collectors.toList());
			
			if ( BaseHelper.findFirst( ruoliPresenti, r -> r.equals(body.getRuolo())).isPresent()) {
				throw FaultCode.CONFLITTO.toException("Il ruolo " + body.getRuolo() + " è già associato al gruppo scelto");
			}
			
			if (!env.paHelper.ruoloCheckData(TipoOperazione.ADD, body.getRuolo(), ruoliPresenti)) {
				throw FaultCode.RICHIESTA_NON_VALIDA.toException(StringEscapeUtils.unescapeHtml(  env.pd.getMessage() ));
			}

			Ruolo ruolo = new Ruolo();
			ruolo.setNome(body.getRuolo());
			pa.getRuoli().addRuolo(ruolo);

			env.paCore.performUpdateOperation(env.userLogin, false, pa);

			context.getLogger().info("Invocazione completata con successo");
     
			// Bug Fix: altrimenti viene generato 204
			context.getServletResponse().setStatus(201);
		}
		catch(javax.ws.rs.WebApplicationException e) {
			context.getLogger().error_except404("Invocazione terminata con errore '4xx': %s",e, e.getMessage());
			throw e;
		}
		catch(Throwable e) {
			context.getLogger().error("Invocazione terminata con errore: %s",e, e.getMessage());
			throw FaultCode.ERRORE_INTERNO.toException(e);
		}
    }
    
    /**
     * Aggiunta di scope all'elenco degli scope autorizzati
     *
     * Questa operazione consente di aggiungere scope all'elenco degli scope autorizzati
     *
     */
	@Override
    public void addErogazioneControlloAccessiAutorizzazioneScope(ControlloAccessiAutorizzazioneScope body, String nome, Integer versione, ProfiloEnum profilo, String soggetto, String gruppo, String tipoServizio) {
		IContext context = this.getContext();
		try {
			context.getLogger().info("Invocazione in corso ...");     

			AuthorizationManager.authorize(context, getAuthorizationConfig());
			context.getLogger().debug("Autorizzazione completata con successo");
			
			BaseHelper.throwIfNull(body);			
			
			final ErogazioniConfEnv env = new ErogazioniConfEnv(context.getServletRequest(), profilo, soggetto, context, nome, versione, gruppo, tipoServizio );
			final PortaApplicativa pa = env.paCore.getPortaApplicativa(env.idPa);
			
			if(pa.getScope()==null) {
				throw FaultCode.RICHIESTA_NON_VALIDA.toException("L'autorizzazione per scope non è abilitata");
			}
			
			FiltroRicercaScope filtroScope = new FiltroRicercaScope();
			filtroScope.setContesto(ScopeContesto.PORTA_APPLICATIVA);
			filtroScope.setTipologia("");
			
			final List<String> scopeAmmessi = env.stationCore.getAllScope(filtroScope);
			final List<String> scopePresenti = pa.getScope().getScopeList().stream().map(Scope::getNome).collect(Collectors.toList());
			
			if ( !scopeAmmessi.contains(body.getScope())) {
				throw FaultCode.RICHIESTA_NON_VALIDA.toException("Scope " + body.getScope() + " non presente fra gli scope ammissibili."); 
			}
			
			if ( scopePresenti.contains(body.getScope()) ) {
				throw FaultCode.CONFLITTO.toException("Scope " + body.getScope() + " già assegnato al gruppo");
			}
			
			if (!env.paHelper.scopeCheckData(TipoOperazione.ADD, body.getScope(), scopePresenti )) {
				throw FaultCode.RICHIESTA_NON_VALIDA.toException(StringEscapeUtils.unescapeHtml(  env.pd.getMessage() ));
				
			}

			// Inserisco il scope nel db
			Scope scope = new Scope();
			scope.setNome(body.getScope());
			pa.getScope().addScope(scope);

			
			env.paCore.performUpdateOperation(env.userLogin, false, pa);

			context.getLogger().info("Invocazione completata con successo");
         
			// Bug Fix: altrimenti viene generato 204
			context.getServletResponse().setStatus(201);
		}
		catch(javax.ws.rs.WebApplicationException e) {
			context.getLogger().error_except404("Invocazione terminata con errore '4xx': %s",e, e.getMessage());
			throw e;
		}
		catch(Throwable e) {
			context.getLogger().error("Invocazione terminata con errore: %s",e, e.getMessage());
			throw FaultCode.ERRORE_INTERNO.toException(e);
		}
    }
    
    /**
     * Aggiunta di una policy di rate limiting
     *
     * Questa operazione consente di aggiungere una policy di rate limiting
     *
     */
	@Override
    public void addErogazioneRateLimitingPolicy(RateLimitingPolicyErogazione body, String nome, Integer versione, ProfiloEnum profilo, String soggetto, String gruppo, String tipoServizio) {
		IContext context = this.getContext();
		try {
			context.getLogger().info("Invocazione in corso ...");     

			AuthorizationManager.authorize(context, getAuthorizationConfig());
			context.getLogger().debug("Autorizzazione completata con successo");
			
			BaseHelper.throwIfNull(body);
			final ErogazioniConfEnv env = new ErogazioniConfEnv(context.getServletRequest(), profilo, soggetto, context, nome, versione, gruppo, tipoServizio );
			final PortaApplicativa pa = env.paCore.getPortaApplicativa(env.idPa);

			final AccordoServizioParteSpecifica asps = BaseHelper.supplyOrNotFound(() -> ErogazioniApiHelper
					.getServizioIfErogazione(tipoServizio, nome, versione, env.idSoggetto.toIDSoggetto(), env),
					"Accordo Servizio Parte Specifica");
			final AccordoServizioParteComuneSintetico apc = env.apcCore.getAccordoServizioSintetico(asps.getIdAccordo());
			
			AttivazionePolicy policy = new AttivazionePolicy();
			policy.setFiltro(new AttivazionePolicyFiltro());
			policy.setGroupBy(new AttivazionePolicyRaggruppamento());
			
			final RuoloPolicy ruoloPorta = RuoloPolicy.APPLICATIVA;
			final String nomePorta = pa.getNome();
			
			String modalita = ErogazioniApiHelper.getDataElementModalita(body.getConfigurazione().getIdentificazione());
			
			String idPolicy = ErogazioniApiHelper.getIdPolicy(body, env.confCore, env.confHelper);
			if(idPolicy==null) {
				switch (body.getConfigurazione().getIdentificazione()) {
				case POLICY:
					throw FaultCode.RICHIESTA_NON_VALIDA.toException("Policy Utente non trovata");
				case CRITERI:
					throw FaultCode.RICHIESTA_NON_VALIDA.toException("Policy Built-In non trovata che rispettano i criteri forniti");
				}
			}
			policy.setIdPolicy(idPolicy);
			
			// Questo lo prendo paro paro dal codice della console.
			InfoPolicy infoPolicy = env.confCore.getInfoPolicy(policy.getIdPolicy());
			int counter = env.confCore.getFreeCounterForGlobalPolicy(infoPolicy.getIdPolicy());
			policy.setIdActivePolicy(infoPolicy.getIdPolicy()+":"+counter);
			
			ErogazioniApiHelper.override(body, env.protocolFactory.getProtocol(), env.idSoggetto.toIDSoggetto(), env.requestWrapper);
			
			String errorAttivazione = env.confHelper.readDatiAttivazionePolicyFromHttpParameters(policy, false, TipoOperazione.ADD, infoPolicy);
			if ( !StringUtils.isEmpty(errorAttivazione) ) {
				throw FaultCode.RICHIESTA_NON_VALIDA.toException(StringEscapeUtils.unescapeHtml(errorAttivazione));
			}
			
			policy.getFiltro().setEnabled(true);
			policy.getFiltro().setProtocollo(env.tipo_protocollo);
			policy.getFiltro().setRuoloPorta(ruoloPorta);
			policy.getFiltro().setNomePorta(nomePorta);
		
			StringBuilder existsMessage = new StringBuilder();
			if ( ConfigurazioneUtilities.alreadyExists(
					TipoOperazione.ADD,
					env.confCore,
					env.confHelper,
					policy, 
					infoPolicy,
					ruoloPorta, 
					nomePorta, 
					env.apcCore.toMessageServiceBinding(apc.getServiceBinding()),
					existsMessage, 
					org.openspcoop2.core.constants.Costanti.WEB_NEW_LINE,
					modalita
				)) {
				throw FaultCode.CONFLITTO.toException(StringEscapeUtils.unescapeHtml(existsMessage.toString()));
			}
			
			ErogazioniApiHelper.attivazionePolicyCheckData(TipoOperazione.ADD, pa, policy, infoPolicy, env, env.apcCore.toMessageServiceBinding(apc.getServiceBinding()), modalita);
			
			// aggiorno prossima posizione nella policy
			ConfigurazioneUtilities.updatePosizioneAttivazionePolicy(env.confCore, infoPolicy, policy, ruoloPorta, nomePorta);
			
			env.confCore.performCreateOperation(env.userLogin, false, policy);

			context.getLogger().info("Invocazione completata con successo");
        
			// Bug Fix: altrimenti viene generato 204
			context.getServletResponse().setStatus(201);
		}
		catch(javax.ws.rs.WebApplicationException e) {
			context.getLogger().error_except404("Invocazione terminata con errore '4xx': %s",e, e.getMessage());
			throw e;
		}
		catch(Throwable e) {
			context.getLogger().error("Invocazione terminata con errore: %s",e, e.getMessage());
			throw FaultCode.ERRORE_INTERNO.toException(e);
		}
    }
    
    /**
     * Aggiunta di una regola di correlazione applicativa
     *
     * Questa operazione consente di registrare una regola di correlazione applicativa per la richiesta
     *
     */
	@Override
    public void addErogazioneTracciamentoCorrelazioneApplicativaRichiesta(CorrelazioneApplicativaRichiesta body, String nome, Integer versione, ProfiloEnum profilo, String soggetto, String gruppo, String tipoServizio) {
		IContext context = this.getContext();
		try {
			context.getLogger().info("Invocazione in corso ...");     

			AuthorizationManager.authorize(context, getAuthorizationConfig());
			context.getLogger().debug("Autorizzazione completata con successo");
			
			BaseHelper.throwIfNull(body);
			
			if ( body.getElemento() == null )
				body.setElemento("");
                        
			final ErogazioniConfEnv env = new ErogazioniConfEnv(context.getServletRequest(), profilo, soggetto, context, nome, versione, gruppo, tipoServizio );
			final PortaApplicativa pa = env.paCore.getPortaApplicativa(env.idPa);
			final Long idPorta = pa.getId();
			
			StringBuilder existsMessage = new StringBuilder();
			if ( ConsoleUtilities.alreadyExistsCorrelazioneApplicativaRichiesta(env.paCore, idPorta, body.getElemento(), 0, existsMessage)) {
				throw FaultCode.CONFLITTO.toException(StringEscapeUtils.unescapeHtml(existsMessage.toString()));
			}
			
			if ( !correlazioneApplicativaRichiestaCheckData(TipoOperazione.ADD, env.requestWrapper, env.paHelper, false, body, idPorta, null) ) {
				throw FaultCode.RICHIESTA_NON_VALIDA.toException(StringEscapeUtils.unescapeHtml(env.pd.getMessage()));
			}
									
			CorrelazioneApplicativa ca = pa.getCorrelazioneApplicativa();
			if (ca == null) {
				ca = new CorrelazioneApplicativa();
			}
			ca.addElemento(convert(body));
			pa.setCorrelazioneApplicativa(ca);
			
			env.paCore.performUpdateOperation(env.userLogin, false, pa);

					
			context.getLogger().info("Invocazione completata con successo");
     
			// Bug Fix: altrimenti viene generato 204
			context.getServletResponse().setStatus(201);
		}
		catch(javax.ws.rs.WebApplicationException e) {
			context.getLogger().error_except404("Invocazione terminata con errore '4xx': %s",e, e.getMessage());
			throw e;
		}
		catch(Throwable e) {
			context.getLogger().error("Invocazione terminata con errore: %s",e, e.getMessage());
			throw FaultCode.ERRORE_INTERNO.toException(e);
		}
    }

	/**
     * Aggiunta di una regola di correlazione applicativa
     *
     * Questa operazione consente di registrare una regola di correlazione applicativa per la risposta
     *
     */
	@Override
    public void addErogazioneTracciamentoCorrelazioneApplicativaRisposta(CorrelazioneApplicativaRisposta body, String nome, Integer versione, ProfiloEnum profilo, String soggetto, String gruppo, String tipoServizio) {
		IContext context = this.getContext();
		try {
			context.getLogger().info("Invocazione in corso ...");     

			AuthorizationManager.authorize(context, getAuthorizationConfig());
			context.getLogger().debug("Autorizzazione completata con successo");     
			
			BaseHelper.throwIfNull(body);
			
			if ( body.getElemento() == null )
				body.setElemento("");
                        
			final ErogazioniConfEnv env = new ErogazioniConfEnv(context.getServletRequest(), profilo, soggetto, context, nome, versione, gruppo, tipoServizio );
			final PortaApplicativa pa = env.paCore.getPortaApplicativa(env.idPa);
			final Long idPorta = pa.getId();
			
			
			StringBuilder existsMessage = new StringBuilder();
			if ( ConsoleUtilities.alreadyExistsCorrelazioneApplicativaRisposta(env.paCore, idPorta, body.getElemento(), 0, existsMessage)) {
				throw FaultCode.CONFLITTO.toException(StringEscapeUtils.unescapeHtml(existsMessage.toString()));
			}
			
			if ( !correlazioneApplicativaRispostaCheckData(TipoOperazione.ADD, env.requestWrapper, env.pdHelper, false, body, idPorta, null)) {
				throw FaultCode.RICHIESTA_NON_VALIDA.toException(StringEscapeUtils.unescapeHtml(env.pd.getMessage()));
			}
                       			
			if ( pa.getCorrelazioneApplicativaRisposta() == null)
				pa.setCorrelazioneApplicativaRisposta(new org.openspcoop2.core.config.CorrelazioneApplicativaRisposta());
			
			pa.getCorrelazioneApplicativaRisposta().addElemento(convert(body));
			
			env.paCore.performUpdateOperation(env.userLogin, false, pa);
			
			context.getLogger().info("Invocazione completata con successo");       
     
			// Bug Fix: altrimenti viene generato 204
			context.getServletResponse().setStatus(201);
		}
		catch(javax.ws.rs.WebApplicationException e) {
			context.getLogger().error_except404("Invocazione terminata con errore '4xx': %s",e, e.getMessage());
			throw e;
		}
		catch(Throwable e) {
			context.getLogger().error("Invocazione terminata con errore: %s",e, e.getMessage());
			throw FaultCode.ERRORE_INTERNO.toException(e);
		}
    }
    
    /**
     * Elimina applicativi dall'elenco degli applicativi autorizzati
     *
     * Questa operazione consente di eliminare applicativi dall'elenco degli applicativi autorizzati
     *
     */
	@Override
    public void deleteErogazioneControlloAccessiAutorizzazioneApplicativi(String nome, Integer versione, String applicativoAutorizzato, ProfiloEnum profilo, String soggetto, String soggettoApplicativo, String gruppo, String tipoServizio) {
		IContext context = this.getContext();
		try {
			context.getLogger().info("Invocazione in corso ...");     

			AuthorizationManager.authorize(context, getAuthorizationConfig());
			context.getLogger().debug("Autorizzazione completata con successo");     
			
			final ErogazioniConfEnv env = new ErogazioniConfEnv(context.getServletRequest(), profilo, soggetto, context, nome, versione, gruppo, tipoServizio );
			final PortaApplicativa pa = env.paCore.getPortaApplicativa(env.idPa);
			
			if (pa.getServiziApplicativiAutorizzati() == null)	pa.setServiziApplicativiAutorizzati(new PortaApplicativaAutorizzazioneServiziApplicativi());
			
			IdSoggetto idSoggettoProprietarioSA = (IdSoggetto) env.idSoggetto.clone();
			if(org.apache.commons.lang.StringUtils.isNotEmpty(soggettoApplicativo)) {
				idSoggettoProprietarioSA.setNome(soggettoApplicativo);
			}
			
			PortaApplicativaAutorizzazioneServizioApplicativo to_remove = BaseHelper.findAndRemoveFirst(pa.getServiziApplicativiAutorizzati().getServizioApplicativoList(), sa -> 
				(sa.getNome().equals(applicativoAutorizzato) && 
				idSoggettoProprietarioSA.getNome().equals(sa.getNomeSoggettoProprietario()) && 
				idSoggettoProprietarioSA.getTipo().equals(sa.getTipoSoggettoProprietario())));
			
			if (env.delete_404 && to_remove == null) {
				throw FaultCode.NOT_FOUND.toException("Nessun Applicativo " + applicativoAutorizzato + " (soggetto: "+idSoggettoProprietarioSA.getNome()+") è associato al gruppo scelto");
			} else if (to_remove != null) {
				env.paCore.performUpdateOperation(env.userLogin, false, pa);
			}

        
			context.getLogger().info("Invocazione completata con successo");     
		}
		catch(javax.ws.rs.WebApplicationException e) {
			context.getLogger().error_except404("Invocazione terminata con errore '4xx': %s",e, e.getMessage());
			throw e;
		}
		catch(Throwable e) {
			context.getLogger().error("Invocazione terminata con errore: %s",e, e.getMessage());
			throw FaultCode.ERRORE_INTERNO.toException(e);
		}
    }
    
    /**
     * Elimina soggetti all'elenco dei soggetti autorizzati
     *
     * Questa operazione consente di eliminare soggetti all'elenco dei soggetti autorizzati
     *
     */
	@Override
    public void deleteErogazioneControlloAccessiAutorizzazioneSoggetti(String nome, Integer versione, String soggettoAutorizzato, ProfiloEnum profilo, String soggetto, String gruppo, String tipoServizio) {
		IContext context = this.getContext();
		try {
			context.getLogger().info("Invocazione in corso ...");     

			AuthorizationManager.authorize(context, getAuthorizationConfig());
			context.getLogger().debug("Autorizzazione completata con successo");     
                        

			final ErogazioniConfEnv env = new ErogazioniConfEnv(context.getServletRequest(), profilo, soggetto, context, nome, versione, gruppo, tipoServizio );
			PortaApplicativa pa = env.paCore.getPortaApplicativa(env.idPa);
			
			if (pa.getSoggetti() == null)	pa.setSoggetti(new PortaApplicativaAutorizzazioneSoggetti());
			
			PortaApplicativaAutorizzazioneSoggetto to_remove = BaseHelper.findAndRemoveFirst(pa.getSoggetti().getSoggettoList(), sogg -> sogg.getNome().equals(soggettoAutorizzato));
			
			if (env.delete_404 && to_remove == null) {
				throw FaultCode.NOT_FOUND.toException("Nessun Soggetto " + soggettoAutorizzato + " è associato al gruppo scelto"); 
			} else if ( to_remove != null ) {
			
				env.paCore.performUpdateOperation(env.userLogin, false, pa);
			}
        
			context.getLogger().info("Invocazione completata con successo");
        
     
		}
		catch(javax.ws.rs.WebApplicationException e) {
			context.getLogger().error_except404("Invocazione terminata con errore '4xx': %s",e, e.getMessage());
			throw e;
		}
		catch(Throwable e) {
			context.getLogger().error("Invocazione terminata con errore: %s",e, e.getMessage());
			throw FaultCode.ERRORE_INTERNO.toException(e);
		}
    }
    
    /**
     * Elimina ruoli dall'elenco dei ruoli autorizzati
     *
     * Questa operazione consente di eliminare ruoli dall'elenco dei ruoli autorizzati
     *
     */
	@Override
    public void deleteErogazioneControlloAccessiAutorizzazioneRuoli(String nome, Integer versione, String ruoloAutorizzato, ProfiloEnum profilo, String soggetto, String gruppo, String tipoServizio) {
		IContext context = this.getContext();
		try {
			context.getLogger().info("Invocazione in corso ...");     

			AuthorizationManager.authorize(context, getAuthorizationConfig());
			context.getLogger().debug("Autorizzazione completata con successo");     
                        
			final ErogazioniConfEnv env = new ErogazioniConfEnv(context.getServletRequest(), profilo, soggetto, context, nome, versione, gruppo, tipoServizio );
			final PortaApplicativa pa = env.paCore.getPortaApplicativa(env.idPa);
			
			if (pa.getRuoli() == null)	pa.setRuoli(new AutorizzazioneRuoli());
			
			Ruolo to_remove = BaseHelper.findAndRemoveFirst(pa.getRuoli().getRuoloList(), r -> r.getNome().equals(ruoloAutorizzato));
			
			if (env.delete_404 && to_remove == null) {
				throw FaultCode.NOT_FOUND.toException("Nessun Ruolo " + ruoloAutorizzato + " è associato al gruppo scelto"); 
			} else if ( to_remove != null ) {
			
				env.paCore.performUpdateOperation(env.userLogin, false, pa);
			}
			context.getLogger().info("Invocazione completata con successo");
        
     
		}
		catch(javax.ws.rs.WebApplicationException e) {
			context.getLogger().error_except404("Invocazione terminata con errore '4xx': %s",e, e.getMessage());
			throw e;
		}
		catch(Throwable e) {
			context.getLogger().error("Invocazione terminata con errore: %s",e, e.getMessage());
			throw FaultCode.ERRORE_INTERNO.toException(e);
		}
    }
    
    /**
     * Elimina scope dall'elenco degli scope autorizzati
     *
     * Questa operazione consente di eliminare scope dall'elenco degli scope autorizzati
     *
     */
	@Override
    public void deleteErogazioneControlloAccessiAutorizzazioneScope(String nome, Integer versione, String scopeAutorizzato, ProfiloEnum profilo, String soggetto, String gruppo, String tipoServizio) {
		IContext context = this.getContext();
		try {
			context.getLogger().info("Invocazione in corso ...");     

			AuthorizationManager.authorize(context, getAuthorizationConfig());
			context.getLogger().debug("Autorizzazione completata con successo");     
			
			final ErogazioniConfEnv env = new ErogazioniConfEnv(context.getServletRequest(), profilo, soggetto, context, nome, versione, gruppo, tipoServizio );
			final PortaApplicativa pa = env.paCore.getPortaApplicativa(env.idPa);
			
			if (pa.getScope() == null)	pa.setScope(new AutorizzazioneScope());
			
			Scope to_remove = BaseHelper.findAndRemoveFirst(pa.getScope().getScopeList(), s -> s.getNome().equals(scopeAutorizzato));
			
			if (env.delete_404 && to_remove == null) {
				throw FaultCode.NOT_FOUND.toException("Nessuno Scope " + scopeAutorizzato + " è associato al gruppo scelto"); 
			} else if ( to_remove != null ) {
			
				env.paCore.performUpdateOperation(env.userLogin, false, pa);
			}
        
			context.getLogger().info("Invocazione completata con successo");
        
     
		}
		catch(javax.ws.rs.WebApplicationException e) {
			context.getLogger().error_except404("Invocazione terminata con errore '4xx': %s",e, e.getMessage());
			throw e;
		}
		catch(Throwable e) {
			context.getLogger().error("Invocazione terminata con errore: %s",e, e.getMessage());
			throw FaultCode.ERRORE_INTERNO.toException(e);
		}
    }
    
    /**
     * Elimina la policy dall'elenco delle policies attive
     *
     * Questa operazione consente di eliminare la policy dall'elenco delle policies attive
     *
     */
	@Override
    public void deleteErogazioneRateLimitingPolicy(String nome, Integer versione, String idPolicy, ProfiloEnum profilo, String soggetto, String gruppo, String tipoServizio) {
		IContext context = this.getContext();
		try {
			context.getLogger().info("Invocazione in corso ...");     

			AuthorizationManager.authorize(context, getAuthorizationConfig());
			context.getLogger().debug("Autorizzazione completata con successo");     
                        
			final ErogazioniConfEnv env = new ErogazioniConfEnv(context.getServletRequest(), profilo, soggetto, context, nome, versione, gruppo, tipoServizio );
			final PortaApplicativa pa = env.paCore.getPortaApplicativa(env.idPa);
			
			List<AttivazionePolicy> policies = env.confCore.attivazionePolicyList(null, RuoloPolicy.APPLICATIVA, pa.getNome());
			AttivazionePolicy policy = BaseHelper.findFirst( policies, p -> (PolicyUtilities.getNomeActivePolicy(p.getAlias(),p.getIdActivePolicy())).equals(idPolicy) ).orElse(null);
			
			if ( policy != null ) {
				StringBuilder inUsoMessage = new StringBuilder();
				List<AttivazionePolicy> policyRimosse = new ArrayList<AttivazionePolicy>();
				
				ConfigurazioneUtilities.deleteAttivazionePolicy(
						new ArrayList<AttivazionePolicy>(Arrays.asList( policy )), 
						env.confHelper, 
						env.confCore, 
						env.userLogin, 
						inUsoMessage, 
						org.openspcoop2.core.constants.Costanti.WEB_NEW_LINE, policyRimosse
					);
				
				if ( inUsoMessage.length() > 0 ) {
					throw FaultCode.RICHIESTA_NON_VALIDA.toException(StringEscapeUtils.unescapeHtml(inUsoMessage.toString()));
				}
				
			}
			else if ( env.delete_404 ) {
				throw FaultCode.NOT_FOUND.toException("Nessuna policy di rate limiting con id " + idPolicy );
			}
	   
			context.getLogger().info("Invocazione completata con successo");
     
		}
		catch(javax.ws.rs.WebApplicationException e) {
			context.getLogger().error_except404("Invocazione terminata con errore '4xx': %s",e, e.getMessage());
			throw e;
		}
		catch(Throwable e) {
			context.getLogger().error("Invocazione terminata con errore: %s",e, e.getMessage());
			throw FaultCode.ERRORE_INTERNO.toException(e);
		}
    }
    
    /**
     * Elimina la regola di correlazione applicativa dall'elenco di quelle attivate per la richiesta
     *
     * Questa operazione consente di eliminare la regola di correlazione applicativa dall'elenco di quelle attivate per la richiesta
     *
     */
	@Override
    public void deleteErogazioneTracciamentoCorrelazioneApplicativaRichiesta(String nome, Integer versione, String elemento, ProfiloEnum profilo, String soggetto, String gruppo, String tipoServizio) {
		IContext context = this.getContext();
		try {
			context.getLogger().info("Invocazione in corso ...");     

			AuthorizationManager.authorize(context, getAuthorizationConfig());
			context.getLogger().debug("Autorizzazione completata con successo");     
                        
			final ErogazioniConfEnv env = new ErogazioniConfEnv(context.getServletRequest(), profilo, soggetto, context, nome, versione, gruppo, tipoServizio );
			final PortaApplicativa pa = env.paCore.getPortaApplicativa(env.idPa);
			
			final CorrelazioneApplicativa correlazioneApplicativa = pa.getCorrelazioneApplicativa();
			
			final String searchElemento = elemento.equals("*")
					? ""
				  	: elemento;	
									
			CorrelazioneApplicativaElemento to_del = BaseHelper.evalnull( () -> BaseHelper.findAndRemoveFirst( 
					correlazioneApplicativa.getElementoList(), 
					e -> (e.getNome()==null ? "" : e.getNome()).equals(searchElemento)
				));
			
			if ( to_del != null ) {
				env.paCore.performUpdateOperation(env.userLogin, false, pa);
			}
			
			else if (env.delete_404) {
				throw FaultCode.NOT_FOUND.toException("Correlazione applicativa per l'elemento " + elemento + " non trovata");
			}
				
			
			context.getLogger().info("Invocazione completata con successo");     
		}
		catch(javax.ws.rs.WebApplicationException e) {
			context.getLogger().error_except404("Invocazione terminata con errore '4xx': %s",e, e.getMessage());
			throw e;
		}
		catch(Throwable e) {
			context.getLogger().error("Invocazione terminata con errore: %s",e, e.getMessage());
			throw FaultCode.ERRORE_INTERNO.toException(e);
		}
    }
    
    /**
     * Elimina la regola di correlazione applicativa dall'elenco di quelle attivate per la risposta
     *
     * Questa operazione consente di eliminare la regola di correlazione applicativa dall'elenco di quelle attivate per la risposta
     *
     */
	@Override
    public void deleteErogazioneTracciamentoCorrelazioneApplicativaRisposta(String nome, Integer versione, String elemento, ProfiloEnum profilo, String soggetto, String gruppo, String tipoServizio) {
		IContext context = this.getContext();
		try {
			context.getLogger().info("Invocazione in corso ...");     

			AuthorizationManager.authorize(context, getAuthorizationConfig());
			context.getLogger().debug("Autorizzazione completata con successo");     
                        
			final ErogazioniConfEnv env = new ErogazioniConfEnv(context.getServletRequest(), profilo, soggetto, context, nome, versione, gruppo, tipoServizio );
			final PortaApplicativa pa = env.paCore.getPortaApplicativa(env.idPa);
			
			org.openspcoop2.core.config.CorrelazioneApplicativaRisposta correlazioneApplicativa = pa.getCorrelazioneApplicativaRisposta();
			
			final String searchElemento = elemento.equals("*")
					? ""
				  	: elemento;	
									
			CorrelazioneApplicativaRispostaElemento to_del = BaseHelper.evalnull( () -> BaseHelper.findAndRemoveFirst( 
					correlazioneApplicativa.getElementoList(), 
					e -> (e.getNome()==null ? "" : e.getNome()).equals(searchElemento)
				));
			
			if ( to_del != null ) {
				env.paCore.performUpdateOperation(env.userLogin, false, pa);
			}
			
			else if (env.delete_404) {
				throw FaultCode.NOT_FOUND.toException("Correlazione applicativa per l'elemento " + elemento + " non trovata");
			}        
			context.getLogger().info("Invocazione completata con successo");
        
     
		}
		catch(javax.ws.rs.WebApplicationException e) {
			context.getLogger().error_except404("Invocazione terminata con errore '4xx': %s",e, e.getMessage());
			throw e;
		}
		catch(Throwable e) {
			context.getLogger().error("Invocazione terminata con errore: %s",e, e.getMessage());
			throw FaultCode.ERRORE_INTERNO.toException(e);
		}
    }
    
    /**
     * Restituisce la policy XACML associata all'autorizzazione
     *
     * Questa operazione consente di ottenere la policy XACML associata all'autorizzazione
     *
     */
	@Override
    public byte[] downloadErogazioneControlloAccessiAutorizzazioneXacmlPolicy(String nome, Integer versione, ProfiloEnum profilo, String soggetto, String gruppo, String tipoServizio) {
		IContext context = this.getContext();
		try {
			context.getLogger().info("Invocazione in corso ...");     

			AuthorizationManager.authorize(context, getAuthorizationConfig());
			context.getLogger().debug("Autorizzazione completata con successo");     
			
			final ErogazioniConfEnv env = new ErogazioniConfEnv(context.getServletRequest(), profilo, soggetto, context, nome, versione, gruppo, tipoServizio );
			final PortaApplicativa pa = env.paCore.getPortaApplicativa(env.idPa);
			
			if (pa.getXacmlPolicy() == null)
				throw FaultCode.NOT_FOUND.toException("Xacml policy non assegnata al gruppo scelto");
			
			context.getLogger().info("Invocazione completata con successo");
			return BaseHelper.evalnull( () -> pa.getXacmlPolicy().getBytes() );

		}
		catch(javax.ws.rs.WebApplicationException e) {
			context.getLogger().error_except404("Invocazione terminata con errore '4xx': %s",e, e.getMessage());
			throw e;
		}
		catch(Throwable e) {
			context.getLogger().error("Invocazione terminata con errore: %s",e, e.getMessage());
			throw FaultCode.ERRORE_INTERNO.toException(e);
		}
    }
    
    /**
     * Restituisce l'elenco delle policy di rate limiting configurate
     *
     * Questa operazione consente di ottenere l'elenco delle policy di rate limiting configurate
     *
     */
	@Override
    public ListaRateLimitingPolicy findAllErogazioneRateLimitingPolicies(String nome, Integer versione, ProfiloEnum profilo, String soggetto, String gruppo, String tipoServizio, String q, Integer limit, Integer offset, RateLimitingCriteriMetricaEnum metrica) {
		IContext context = this.getContext();
		try {
			context.getLogger().info("Invocazione in corso ...");     

			AuthorizationManager.authorize(context, getAuthorizationConfig());
			context.getLogger().debug("Autorizzazione completata con successo");     
			
			final ErogazioniConfEnv env = new ErogazioniConfEnv(context.getServletRequest(), profilo, soggetto, context, nome, versione, gruppo, tipoServizio );
			final PortaApplicativa pa = env.paCore.getPortaApplicativa(env.idPa);
			
			final int idLista = Liste.CONFIGURAZIONE_CONTROLLO_TRAFFICO_ATTIVAZIONE_POLICY;
			final Search ricerca = Helper.setupRicercaPaginata(q, limit, offset, idLista, env.idSoggetto.toIDSoggetto(), env.tipo_protocollo);
			if(metrica!=null) {
				String risorsa = ErogazioniApiHelper.getDataElementModalitaRisorsa(metrica);
				ricerca.addFilter(idLista, Filtri.FILTRO_TIPO_RISORSA_POLICY, risorsa);
			}
			List<AttivazionePolicy> policies = env.confCore.attivazionePolicyList(ricerca, RuoloPolicy.APPLICATIVA, pa.getNome());

			if ( env.findall_404 && policies.isEmpty() ) {
				throw FaultCode.NOT_FOUND.toException("Nessuna policy di rate limiting associata");
			}
			
			ListaRateLimitingPolicy ret = ListaUtils.costruisciListaPaginata(context.getUriInfo(), 
					ricerca.getIndexIniziale(idLista),
					ricerca.getPageSize(idLista), 
					ricerca.getNumEntries(idLista), ListaRateLimitingPolicy.class);
			
			policies.forEach( p -> {
				RateLimitingPolicyItem item = new RateLimitingPolicyItem();
				item.setNome(PolicyUtilities.getNomeActivePolicy(p.getAlias(), p.getIdActivePolicy()));
				ret.addItemsItem(item);
			});
                              
			context.getLogger().info("Invocazione completata con successo");
			return ret;
     
		}
		catch(javax.ws.rs.WebApplicationException e) {
			context.getLogger().error_except404("Invocazione terminata con errore '4xx': %s",e, e.getMessage());
			throw e;
		}
		catch(Throwable e) {
			context.getLogger().error("Invocazione terminata con errore: %s",e, e.getMessage());
			throw FaultCode.ERRORE_INTERNO.toException(e);
		}
    }
    
    /**
     * Restituisce l'elenco delle regole di correlazione applicativa per la richiesta
     *
     * Questa operazione consente di ottenere l'elenco delle regole di correlazione applicativa per la richiesta
     *
     */
	@Override
    public ListaCorrelazioneApplicativaRichiesta findAllErogazioneTracciamentoCorrelazioneApplicativaRichiesta(String nome, Integer versione, ProfiloEnum profilo, String soggetto, String gruppo, String tipoServizio, String q, Integer limit, Integer offset) {
		IContext context = this.getContext();
		try {
			context.getLogger().info("Invocazione in corso ...");     

			AuthorizationManager.authorize(context, getAuthorizationConfig());
			context.getLogger().debug("Autorizzazione completata con successo");
			
			final ErogazioniConfEnv env = new ErogazioniConfEnv(context.getServletRequest(), profilo, soggetto, context, nome, versione, gruppo, tipoServizio );
			final PortaApplicativa pa = env.paCore.getPortaApplicativa(env.idPa);
			
			final int idLista = Liste.PORTE_APPLICATIVE_CORRELAZIONE_APPLICATIVA;
			final Search ricerca = Helper.setupRicercaPaginata(q, limit, offset, idLista, env.idSoggetto.toIDSoggetto(), env.tipo_protocollo);
			
			List<CorrelazioneApplicativaElemento> lista = env.paCore.porteApplicativeCorrelazioneApplicativaList(pa.getId().intValue(), ricerca);

			if ( env.findall_404 && lista.isEmpty() ) {
				throw FaultCode.NOT_FOUND.toException("Nessuna policy di rate limiting associata");
			}
			
			ListaCorrelazioneApplicativaRichiesta ret = ListaUtils.costruisciListaPaginata(context.getUriInfo(), 
					ricerca.getIndexIniziale(idLista),
					ricerca.getPageSize(idLista), 
					ricerca.getNumEntries(idLista), ListaCorrelazioneApplicativaRichiesta.class);
			
			lista.forEach( c -> {
				CorrelazioneApplicativaRichiestaItem item = new CorrelazioneApplicativaRichiestaItem();
				item.setElemento( StringUtils.isEmpty(c.getNome()) 
						? "*"
						: c.getNome() 
					);
				
				item.setIdentificazioneTipo(CorrelazioneApplicativaRichiestaEnum.valueOf(c.getIdentificazione().name()));
				ret.addItemsItem(item);
			});

			context.getLogger().info("Invocazione completata con successo");
			return ret;
     
		}
		catch(javax.ws.rs.WebApplicationException e) {
			context.getLogger().error_except404("Invocazione terminata con errore '4xx': %s",e, e.getMessage());
			throw e;
		}
		catch(Throwable e) {
			context.getLogger().error("Invocazione terminata con errore: %s",e, e.getMessage());
			throw FaultCode.ERRORE_INTERNO.toException(e);
		}
    }
    
    /**
     * Restituisce l'elenco delle regole di correlazione applicativa per la risposta
     *
     * Questa operazione consente di ottenere l'elenco delle regole di correlazione applicativa per la risposta
     *
     */
	@Override
    public ListaCorrelazioneApplicativaRisposta findAllErogazioneTracciamentoCorrelazioneApplicativaRisposta(String nome, Integer versione, ProfiloEnum profilo, String soggetto, String gruppo, String tipoServizio, String q, Integer limit, Integer offset) {
		IContext context = this.getContext();
		try {
			context.getLogger().info("Invocazione in corso ...");     

			AuthorizationManager.authorize(context, getAuthorizationConfig());
			context.getLogger().debug("Autorizzazione completata con successo");     
                        
			final ErogazioniConfEnv env = new ErogazioniConfEnv(context.getServletRequest(), profilo, soggetto, context, nome, versione, gruppo, tipoServizio );
			final PortaApplicativa pa = env.paCore.getPortaApplicativa(env.idPa);
			
			final int idLista = Liste.PORTE_APPLICATIVE_CORRELAZIONE_APPLICATIVA_RISPOSTA;
			final Search ricerca = Helper.setupRicercaPaginata(q, limit, offset, idLista, env.idSoggetto.toIDSoggetto(), env.tipo_protocollo);
			
			List<CorrelazioneApplicativaRispostaElemento> lista = env.paCore.porteApplicativeCorrelazioneApplicativaRispostaList(pa.getId().intValue(), ricerca);

			if ( env.findall_404 && lista.isEmpty() ) {
				throw FaultCode.NOT_FOUND.toException("Nessuna policy di rate limiting associata");
			}
			
			ListaCorrelazioneApplicativaRisposta ret = ListaUtils.costruisciListaPaginata(context.getUriInfo(), 
					ricerca.getIndexIniziale(idLista),
					ricerca.getPageSize(idLista), 
					ricerca.getNumEntries(idLista), ListaCorrelazioneApplicativaRisposta.class);
			
			lista.forEach( c -> {
				CorrelazioneApplicativaRispostaItem item = new CorrelazioneApplicativaRispostaItem();
				item.setElemento( StringUtils.isEmpty(c.getNome()) 
						? "*"
						: c.getNome() 
					);				item.setIdentificazioneTipo(CorrelazioneApplicativaRispostaEnum.valueOf(c.getIdentificazione().name()));
				ret.addItemsItem(item);
			});        
			
			context.getLogger().info("Invocazione completata con successo");
        	return ret;
     
		}
		catch(javax.ws.rs.WebApplicationException e) {
			context.getLogger().error_except404("Invocazione terminata con errore '4xx': %s",e, e.getMessage());
			throw e;
		}
		catch(Throwable e) {
			context.getLogger().error("Invocazione terminata con errore: %s",e, e.getMessage());
			throw FaultCode.ERRORE_INTERNO.toException(e);
		}
    }
    
    /**
     * Restituisce la configurazione relativa al caching delle risposte
     *
     * Questa operazione consente di ottenere la configurazione relativa al caching delle risposte
     *
     */
	@Override
    public CachingRisposta getErogazioneCachingRisposta(String nome, Integer versione, ProfiloEnum profilo, String soggetto, String gruppo, String tipoServizio) {
		IContext context = this.getContext();
		try {
			context.getLogger().info("Invocazione in corso ...");     

			AuthorizationManager.authorize(context, getAuthorizationConfig());
			context.getLogger().debug("Autorizzazione completata con successo");
			

			final ErogazioniConfEnv env = new ErogazioniConfEnv(context.getServletRequest(), profilo, soggetto, context, nome, versione, gruppo, tipoServizio );
			final PortaApplicativa pa = env.paCore.getPortaApplicativa(env.idPa);
			
			CachingRisposta ret = ErogazioniApiHelper.buildCachingRisposta(pa.getResponseCaching());
                      
        
			context.getLogger().info("Invocazione completata con successo");
			return ret;
     
		}
		catch(javax.ws.rs.WebApplicationException e) {
			context.getLogger().error_except404("Invocazione terminata con errore '4xx': %s",e, e.getMessage());
			throw e;
		}
		catch(Throwable e) {
			context.getLogger().error("Invocazione terminata con errore: %s",e, e.getMessage());
			throw FaultCode.ERRORE_INTERNO.toException(e);
		}
    }
    
    /**
     * Restituisce la configurazione relativa all'autenticazione per quanto concerne il controllo degli accessi
     *
     * Questa operazione consente di ottenere la configurazione relativa all'autenticazione per quanto concerne il controllo degli accessi
     *
     */
	@Override
    public ControlloAccessiAutenticazione getErogazioneControlloAccessiAutenticazione(String nome, Integer versione, ProfiloEnum profilo, String soggetto, String gruppo, String tipoServizio) {
		IContext context = this.getContext();
		try {
			context.getLogger().info("Invocazione in corso ...");     

			AuthorizationManager.authorize(context, getAuthorizationConfig());
			context.getLogger().debug("Autorizzazione completata con successo");     
			
			final ErogazioniConfEnv env = new ErogazioniConfEnv(context.getServletRequest(), profilo, soggetto, context, nome, versione, gruppo, tipoServizio );
			final PortaApplicativa pa = env.paCore.getPortaApplicativa(env.idPa);
			
			OneOfControlloAccessiAutenticazioneAutenticazione autRet = null;
			TipoAutenticazioneEnum tipoAutenticazione = null;
			if ( TipoAutenticazione.toEnumConstant(pa.getAutenticazione()) == null ) {
				tipoAutenticazione = TipoAutenticazioneEnum.CUSTOM;
			}
			else {		
				tipoAutenticazione = Enums.dualizeMap(Enums.tipoAutenticazioneFromRest).get(TipoAutenticazione.toEnumConstant(pa.getAutenticazione())) ;
			}
			
			ControlloAccessiAutenticazioneToken token = BaseHelper.evalnull( () -> pa.getGestioneToken().getAutenticazione() ) != null
					? ErogazioniApiHelper.fromGestioneTokenAutenticazione(pa.getGestioneToken().getAutenticazione())
					: null;			
					
			switch(tipoAutenticazione) {
			case HTTP_BASIC: {
				APIImplAutenticazioneBasic authnBasic = new APIImplAutenticazioneBasic();
				authnBasic.setTipo(tipoAutenticazione);
				authnBasic.setOpzionale(Helper.statoFunzionalitaConfToBool( pa.getAutenticazioneOpzionale() ));
				autRet = authnBasic;
				
				Optional<Proprieta> prop = pa.getProprietaAutenticazioneList().stream().filter( p -> ParametriAutenticazioneBasic.CLEAN_HEADER_AUTHORIZATION.equals(p.getNome())).findAny();
				if (prop.isPresent() && prop.get().getValore().equals(ParametriAutenticazionePrincipal.CLEAN_PRINCIPAL_TRUE))
					authnBasic.setForward(false);
				else
					authnBasic.setForward(true);

				break;
			}
			case HTTPS: {
				APIImplAutenticazioneHttps authnHttps = new APIImplAutenticazioneHttps();
				authnHttps.setTipo(tipoAutenticazione);
				authnHttps.setOpzionale(Helper.statoFunzionalitaConfToBool( pa.getAutenticazioneOpzionale() ));
				autRet = authnHttps;
				
				break;
			}
			case PRINCIPAL: {
				
				APIImplAutenticazionePrincipal authnPrincipal = new APIImplAutenticazionePrincipal();
				authnPrincipal.setTipo(tipoAutenticazione);
				authnPrincipal.setOpzionale(Helper.statoFunzionalitaConfToBool( pa.getAutenticazioneOpzionale() ));
				autRet = authnPrincipal;
				
				TipoAutenticazionePrincipal tipoAuthnPrincipal = pa.getProprietaAutenticazioneList()
						.stream()
						.filter( p -> ParametriAutenticazionePrincipal.TIPO_AUTENTICAZIONE.equals(p.getNome()))
						.map( p -> TipoAutenticazionePrincipal.toEnumConstant(p.getValore()) )
						.findAny()
						.orElse(TipoAutenticazionePrincipal.CONTAINER);
				authnPrincipal.setTipoPrincipal(Enums.dualizeMap(Enums.tipoAutenticazionePrincipalFromRest).get(tipoAuthnPrincipal));
				
				switch (authnPrincipal.getTipoPrincipal()) {
				case CONTAINER:
				case IP_ADDRESS:
				case IP_ADDRESS_FORWARDED_FOR:
					break;
				case HEADER_BASED: {
					Optional<Proprieta> prop = pa.getProprietaAutenticazioneList().stream().filter( p -> ParametriAutenticazionePrincipal.NOME.equals(p.getNome())).findAny();
					if (prop.isPresent()) authnPrincipal.setNome(prop.get().getValore());					
					break;
				}
				case FORM_BASED: {
					Optional<Proprieta> prop = pa.getProprietaAutenticazioneList().stream().filter( p -> ParametriAutenticazionePrincipal.NOME.equals(p.getNome())).findAny();
					if (prop.isPresent()) authnPrincipal.setNome(prop.get().getValore());					
					break;
				}
				case URL_BASED: {
					Optional<Proprieta> prop = pa.getProprietaAutenticazioneList().stream().filter( p -> ParametriAutenticazionePrincipal.PATTERN.equals(p.getNome())).findAny();
					if (prop.isPresent()) authnPrincipal.setPattern(prop.get().getValore());
					break;
				}
				case TOKEN: {
					Optional<Proprieta> prop = pa.getProprietaAutenticazioneList().stream().filter( p -> ParametriAutenticazionePrincipal.TOKEN_CLAIM.equals(p.getNome())).findAny();
					if (prop.isPresent()) {
						if(ParametriAutenticazionePrincipal.TOKEN_CLAIM_SUBJECT.equals(prop.get().getValore())) {
							authnPrincipal.setToken(TipoAutenticazionePrincipalToken.SUBJECT);
						}
						else if(ParametriAutenticazionePrincipal.TOKEN_CLAIM_CLIENT_ID.equals(prop.get().getValore())) {
							authnPrincipal.setToken(TipoAutenticazionePrincipalToken.CLIENTID);
						}
						else if(ParametriAutenticazionePrincipal.TOKEN_CLAIM_USERNAME.equals(prop.get().getValore())) {
							authnPrincipal.setToken(TipoAutenticazionePrincipalToken.USERNAME);
						}
						else if(ParametriAutenticazionePrincipal.TOKEN_CLAIM_EMAIL.equals(prop.get().getValore())) {
							authnPrincipal.setToken(TipoAutenticazionePrincipalToken.EMAIL);
						}
						else if(ParametriAutenticazionePrincipal.TOKEN_CLAIM_CUSTOM.equals(prop.get().getValore())) {
							authnPrincipal.setToken(TipoAutenticazionePrincipalToken.CUSTOM);
							
							Optional<Proprieta> propName = pa.getProprietaAutenticazioneList().stream().filter( p -> ParametriAutenticazionePrincipal.NOME.equals(p.getNome())).findAny();
							if (propName.isPresent()) authnPrincipal.setNome(propName.get().getValore());
						}
					}
					break;
				}
				}	// switch config.getTipo
				
				// IsForward
				Optional<Proprieta> prop = pa.getProprietaAutenticazioneList().stream().filter( p -> ParametriAutenticazionePrincipal.CLEAN_PRINCIPAL.equals(p.getNome())).findAny();
				if (prop.isPresent() && ParametriAutenticazionePrincipal.CLEAN_PRINCIPAL_TRUE.equals(prop.get().getValore()))
					authnPrincipal.setForward(false);
				else
					authnPrincipal.setForward(true);
				
				break;
			}  // case principal
			case CUSTOM: {
				APIImplAutenticazioneCustom authnCustom = new APIImplAutenticazioneCustom();
				authnCustom.setTipo(tipoAutenticazione);
				authnCustom.setOpzionale(Helper.statoFunzionalitaConfToBool( pa.getAutenticazioneOpzionale() ));
				autRet = authnCustom;
				
				authnCustom.setNome(pa.getAutenticazione());
				
				break;
			}
			default:
				break;
			}  // switch autRet.getTipo
			
			ControlloAccessiAutenticazione ret = new ControlloAccessiAutenticazione();
			ret.setAutenticazione(autRet);
			ret.setToken(token);
			context.getLogger().info("Invocazione completata con successo");
			return ret;
     
		}
		catch(javax.ws.rs.WebApplicationException e) {
			context.getLogger().error_except404("Invocazione terminata con errore '4xx': %s",e, e.getMessage());
			throw e;
		}
		catch(Throwable e) {
			context.getLogger().error("Invocazione terminata con errore: %s",e, e.getMessage());
			throw FaultCode.ERRORE_INTERNO.toException(e);
		}
    }

	/**
     * Restituisce la configurazione relativa all'autorizzazione per quanto concerne il controllo degli accessi
     *
     * Questa operazione consente di ottenere la configurazione relativa all'autorizzazione per quanto concerne il controllo degli accessi
     *
     */
	@Override
    public ControlloAccessiAutorizzazioneView getErogazioneControlloAccessiAutorizzazione(String nome, Integer versione, ProfiloEnum profilo, String soggetto, String gruppo, String tipoServizio) {
		IContext context = this.getContext();
		try {
			context.getLogger().info("Invocazione in corso ...");     

			AuthorizationManager.authorize(context, getAuthorizationConfig());
			context.getLogger().debug("Autorizzazione completata con successo");    
			

			final ErogazioniConfEnv env = new ErogazioniConfEnv(context.getServletRequest(), profilo, soggetto, context, nome, versione, gruppo, tipoServizio );
			final PortaApplicativa pa = env.paCore.getPortaApplicativa(env.idPa);
			
			ControlloAccessiAutorizzazioneView ret = ErogazioniApiHelper.controlloAccessiAutorizzazioneFromPA(pa);
			
			return ret;
     
		}
		catch(javax.ws.rs.WebApplicationException e) {
			context.getLogger().error_except404("Invocazione terminata con errore '4xx': %s",e, e.getMessage());
			throw e;
		}
		catch(Throwable e) {
			context.getLogger().error("Invocazione terminata con errore: %s",e, e.getMessage());
			throw FaultCode.ERRORE_INTERNO.toException(e);
		}
    }

	/**
     * Restituisce l'elenco degli applicativi autorizzati
     *
     * Questa operazione consente di ottenere l'elenco degli applicativi autorizzati
     *
     */
	@Override
    public ControlloAccessiErogazioneAutorizzazioneApplicativi getErogazioneControlloAccessiAutorizzazioneApplicativi(String nome, Integer versione, ProfiloEnum profilo, String soggetto, String gruppo, String tipoServizio) {
		IContext context = this.getContext();
		try {
			context.getLogger().info("Invocazione in corso ...");     

			AuthorizationManager.authorize(context, getAuthorizationConfig());
			context.getLogger().debug("Autorizzazione completata con successo");     
			
			final ErogazioniConfEnv env = new ErogazioniConfEnv(context.getServletRequest(), profilo, soggetto, context, nome, versione, gruppo, tipoServizio );
			final PortaApplicativa pa = env.paCore.getPortaApplicativa(env.idPa);
			ControlloAccessiErogazioneAutorizzazioneApplicativi ret = new ControlloAccessiErogazioneAutorizzazioneApplicativi();
			
			int idLista = Liste.PORTE_APPLICATIVE_SERVIZIO_APPLICATIVO_AUTORIZZATO;
			Search ricerca = Helper.setupRicercaPaginata("", -1, 0, idLista);
			List<PortaApplicativaAutorizzazioneServizioApplicativo> lista = env.paCore.porteAppServiziApplicativiAutorizzatiList(pa.getId(), ricerca);
			
			if(lista!=null && !lista.isEmpty()) {
				for (PortaApplicativaAutorizzazioneServizioApplicativo portaApplicativaAutorizzazioneServizioApplicativo : lista) {
					ControlloAccessiErogazioneAutorizzazioneApplicativo applicativiItem = new ControlloAccessiErogazioneAutorizzazioneApplicativo();
					applicativiItem.setApplicativo(portaApplicativaAutorizzazioneServizioApplicativo.getNome());
					applicativiItem.setSoggetto(portaApplicativaAutorizzazioneServizioApplicativo.getNomeSoggettoProprietario());
					ret.addApplicativiItem(applicativiItem);
				}
			}
			
			context.getLogger().info("Invocazione completata con successo");
			return ret;
     
		}
		catch(javax.ws.rs.WebApplicationException e) {
			context.getLogger().error_except404("Invocazione terminata con errore '4xx': %s",e, e.getMessage());
			throw e;
		}
		catch(Throwable e) {
			context.getLogger().error("Invocazione terminata con errore: %s",e, e.getMessage());
			throw FaultCode.ERRORE_INTERNO.toException(e);
		}
    }
    
    /**
     * Restituisce l'elenco dei soggetti autorizzati
     *
     * Questa operazione consente di ottenere l'elenco dei soggetti autorizzati
     *
     */
	@Override
    public ControlloAccessiAutorizzazioneSoggetti getErogazioneControlloAccessiAutorizzazioneSoggetti(String nome, Integer versione, ProfiloEnum profilo, String soggetto, String gruppo, String tipoServizio) {
		IContext context = this.getContext();
		try {
			context.getLogger().info("Invocazione in corso ...");     

			AuthorizationManager.authorize(context, getAuthorizationConfig());
			context.getLogger().debug("Autorizzazione completata con successo");
			
			final ErogazioniConfEnv env = new ErogazioniConfEnv(context.getServletRequest(), profilo, soggetto, context, nome, versione, gruppo, tipoServizio );
			final PortaApplicativa pa = env.paCore.getPortaApplicativa(env.idPa);
			
			ControlloAccessiAutorizzazioneSoggetti ret = new ControlloAccessiAutorizzazioneSoggetti();
			List<String> soggetti = BaseHelper.evalnull( () -> pa.getSoggetti().getSoggettoList().stream().map( s -> s.getNome()).collect(Collectors.toList()) );
					
			ret.setSoggetti(soggetti);
        
			context.getLogger().info("Invocazione completata con successo");
			return ret;
 
		}
		catch(javax.ws.rs.WebApplicationException e) {
			context.getLogger().error_except404("Invocazione terminata con errore '4xx': %s",e, e.getMessage());
			throw e;
		}
		catch(Throwable e) {
			context.getLogger().error("Invocazione terminata con errore: %s",e, e.getMessage());
			throw FaultCode.ERRORE_INTERNO.toException(e);
		}
    }
    
    /**
     * Restituisce l'elenco dei ruoli autorizzati
     *
     * Questa operazione consente di ottenere l'elenco dei ruoli autorizzati
     *
     */
	@Override
    public ControlloAccessiAutorizzazioneRuoli getErogazioneControlloAccessiAutorizzazioneRuoli(String nome, Integer versione, ProfiloEnum profilo, String soggetto, String gruppo, String tipoServizio) {
		IContext context = this.getContext();
		try {
			context.getLogger().info("Invocazione in corso ...");     

			AuthorizationManager.authorize(context, getAuthorizationConfig());
			context.getLogger().debug("Autorizzazione completata con successo");
			
			final ErogazioniConfEnv env = new ErogazioniConfEnv(context.getServletRequest(), profilo, soggetto, context, nome, versione, gruppo, tipoServizio );
			final PortaApplicativa pa = env.paCore.getPortaApplicativa(env.idPa);
			final ControlloAccessiAutorizzazioneRuoli ret = new ControlloAccessiAutorizzazioneRuoli(); 
                        
			ret.setRuoli(BaseHelper.evalnull( () -> pa.getRuoli().getRuoloList().stream().map(Ruolo::getNome).collect(Collectors.toList()) ));
        
			context.getLogger().info("Invocazione completata con successo");
        	return ret;
     
		}
		catch(javax.ws.rs.WebApplicationException e) {
			context.getLogger().error_except404("Invocazione terminata con errore '4xx': %s",e, e.getMessage());
			throw e;
		}
		catch(Throwable e) {
			context.getLogger().error("Invocazione terminata con errore: %s",e, e.getMessage());
			throw FaultCode.ERRORE_INTERNO.toException(e);
		}
    }
    
    /**
     * Restituisce l'elenco degli scope autorizzati
     *
     * Questa operazione consente di ottenere l'elenco degli scope autorizzati
     *
     */
	@Override
    public ControlloAccessiAutorizzazioneScopes getErogazioneControlloAccessiAutorizzazioneScope(String nome, Integer versione, ProfiloEnum profilo, String soggetto, String gruppo, String tipoServizio) {
		IContext context = this.getContext();
		try {
			context.getLogger().info("Invocazione in corso ...");     

			AuthorizationManager.authorize(context, getAuthorizationConfig());
			context.getLogger().debug("Autorizzazione completata con successo");     

			final ErogazioniConfEnv env = new ErogazioniConfEnv(context.getServletRequest(), profilo, soggetto, context, nome, versione, gruppo, tipoServizio );
			final PortaApplicativa pa = env.paCore.getPortaApplicativa(env.idPa);	
			final ControlloAccessiAutorizzazioneScopes ret = new ControlloAccessiAutorizzazioneScopes();
			
			ret.setScope(BaseHelper.evalnull( () -> pa.getScope().getScopeList().stream().map(Scope::getNome).collect(Collectors.toList()) ));
			
			context.getLogger().info("Invocazione completata con successo");
			return ret;
     
		}
		catch(javax.ws.rs.WebApplicationException e) {
			context.getLogger().error_except404("Invocazione terminata con errore '4xx': %s",e, e.getMessage());
			throw e;
		}
		catch(Throwable e) {
			context.getLogger().error("Invocazione terminata con errore: %s",e, e.getMessage());
			throw FaultCode.ERRORE_INTERNO.toException(e);
		}
    }
    
    /**
     * Restituisce la configurazione relativa alla gestione dei token per quanto concerne il controllo degli accessi
     *
     * Questa operazione consente di ottenere la configurazione relativa alla gestione dei token per quanto concerne il controllo degli accessi
     *
     */
	@Override
    public ControlloAccessiGestioneToken getErogazioneControlloAccessiGestioneToken(String nome, Integer versione, ProfiloEnum profilo, String soggetto, String gruppo, String tipoServizio) {
		IContext context = this.getContext();
		try {
			context.getLogger().info("Invocazione in corso ...");     

			AuthorizationManager.authorize(context, getAuthorizationConfig());
			context.getLogger().debug("Autorizzazione completata con successo");
		
			final ErogazioniConfEnv env = new ErogazioniConfEnv(context.getServletRequest(), profilo, soggetto, context, nome, versione, gruppo, tipoServizio );
			final PortaApplicativa pa = env.paCore.getPortaApplicativa(env.idPa);	
			
			final ControlloAccessiGestioneToken ret = new ControlloAccessiGestioneToken();
			final GestioneToken paToken = pa.getGestioneToken();
			
			ret.setAbilitato(paToken != null);
			if (paToken != null) {
				ret.setIntrospection(StatoFunzionalitaConWarningEnum.fromValue(paToken.getIntrospection().getValue()));
				ret.setPolicy(paToken.getPolicy());
				ret.setTokenForward( Helper.statoFunzionalitaConfToBool( paToken.getForward() ));
				ret.setTokenOpzionale( Helper.statoFunzionalitaConfToBool( paToken.getTokenOpzionale() ));
				ret.setUserInfo(StatoFunzionalitaConWarningEnum.fromValue( paToken.getUserInfo().getValue() ));
				ret.setValidazioneJwt(StatoFunzionalitaConWarningEnum.fromValue( paToken.getValidazione().getValue() ));
			}
        
			context.getLogger().info("Invocazione completata con successo");
			return ret;
     
		}
		catch(javax.ws.rs.WebApplicationException e) {
			context.getLogger().error_except404("Invocazione terminata con errore '4xx': %s",e, e.getMessage());
			throw e;
		}
		catch(Throwable e) {
			context.getLogger().error("Invocazione terminata con errore: %s",e, e.getMessage());
			throw FaultCode.ERRORE_INTERNO.toException(e);
		}
    }
    
    /**
     * Restituisce le informazioni sulla configurazione CORS associata all'erogazione
     *
     * Questa operazione consente di ottenere le informazioni sulla configurazione CORS associata all'erogazione identificata dal nome e dalla versione
     *
     */
	@Override
    public GestioneCors getErogazioneGestioneCORS(String nome, Integer versione, ProfiloEnum profilo, String soggetto, String tipoServizio) {
		IContext context = this.getContext();
		try {
			context.getLogger().info("Invocazione in corso ...");     

			AuthorizationManager.authorize(context, getAuthorizationConfig());
			context.getLogger().debug("Autorizzazione completata con successo");     
                        
			final ErogazioniEnv env = new ErogazioniEnv(context.getServletRequest(), profilo, soggetto, context);
			final AccordoServizioParteSpecifica asps = BaseHelper.supplyOrNotFound( () -> ErogazioniApiHelper.getServizioIfErogazione(tipoServizio , nome, versione, env.idSoggetto.toIDSoggetto(), env), "Erogazione");
			final IdServizio idAsps = new IdServizio(env.idServizioFactory.getIDServizioFromAccordo(asps), asps.getId());
			
			final IDPortaApplicativa idPa = BaseHelper.supplyOrNotFound( () -> ErogazioniApiHelper.getIDGruppoPADefault( idAsps, env.apsCore ),  "Gruppo default per l'erogazione scelta" );
			final PortaApplicativa pa = env.paCore.getPortaApplicativa(idPa);
			final CorsConfigurazione paConf = pa.getGestioneCors();
			
			final GestioneCors ret = ErogazioniApiHelper.convert(paConf);
			
			context.getLogger().info("Invocazione completata con successo");
			return ret;

		}
		catch(javax.ws.rs.WebApplicationException e) {
			context.getLogger().error_except404("Invocazione terminata con errore '4xx': %s",e, e.getMessage());
			throw e;
		}
		catch(Throwable e) {
			context.getLogger().error("Invocazione terminata con errore: %s",e, e.getMessage());
			throw FaultCode.ERRORE_INTERNO.toException(e);
		}
    }
    
    /**
     * Restituisce il dettaglio di una policy di rate limiting
     *
     * Questa operazione consente di ottenere il dettaglio di una policy di rate limiting
     *
     */
	@Override
    public RateLimitingPolicyErogazioneView getErogazioneRateLimitingPolicy(String nome, Integer versione, String idPolicy, ProfiloEnum profilo, String soggetto, String gruppo, String tipoServizio) {
		IContext context = this.getContext();
		try {
			context.getLogger().info("Invocazione in corso ...");     

			AuthorizationManager.authorize(context, getAuthorizationConfig());
			context.getLogger().debug("Autorizzazione completata con successo");
			
			final ErogazioniConfEnv env = new ErogazioniConfEnv(context.getServletRequest(), profilo, soggetto, context, nome, versione, gruppo, tipoServizio );
			final PortaApplicativa pa = env.paCore.getPortaApplicativa(env.idPa);
			
			AttivazionePolicy policy = BaseHelper.supplyOrNotFound( 
					() -> env.confCore.getAttivazionePolicy(idPolicy, RuoloPolicy.APPLICATIVA, pa.getNome()),
					"Rate Limiting Policy con nome " + idPolicy
				);
			if ( policy == null ) 
				throw FaultCode.NOT_FOUND.toException("Nessuna policy di rate limiting con nome " + idPolicy );
			
			InfoPolicy infoPolicy = env.confCore.getInfoPolicy(policy.getIdPolicy());
						
			RateLimitingPolicyErogazioneView ret = ErogazioniApiHelper.convert(policy, infoPolicy, new RateLimitingPolicyErogazioneView());
			
			context.getLogger().info("Invocazione completata con successo");
			return ret;
     
		}
		catch(javax.ws.rs.WebApplicationException e) {
			context.getLogger().error_except404("Invocazione terminata con errore '4xx': %s",e, e.getMessage());
			throw e;
		}
		catch(Throwable e) {
			context.getLogger().error("Invocazione terminata con errore: %s",e, e.getMessage());
			throw FaultCode.ERRORE_INTERNO.toException(e);
		}
    }
    
    /**
     * Restituisce la configurazione relativa alla registrazione dei messaggi
     *
     * Questa operazione consente di ottenere la configurazione relativa alla registrazione dei messaggi
     *
     */
	@Override
    public RegistrazioneMessaggi getErogazioneRegistrazioneMessaggi(String nome, Integer versione, ProfiloEnum profilo, String soggetto, String gruppo, String tipoServizio) {
		IContext context = this.getContext();
		try {
			context.getLogger().info("Invocazione in corso ...");     

			AuthorizationManager.authorize(context, getAuthorizationConfig());
			context.getLogger().debug("Autorizzazione completata con successo");
			
			final ErogazioniConfEnv env = new ErogazioniConfEnv(context.getServletRequest(), profilo, soggetto, context, nome, versione, gruppo, tipoServizio );
			final PortaApplicativa pa = env.paCore.getPortaApplicativa(env.idPa);	
			final RegistrazioneMessaggi ret = ErogazioniApiHelper.fromDumpConfigurazione(pa.getDump());
			
			context.getLogger().info("Invocazione completata con successo");
			return ret;
     
		}
		catch(javax.ws.rs.WebApplicationException e) {
			context.getLogger().error_except404("Invocazione terminata con errore '4xx': %s",e, e.getMessage());
			throw e;
		}
		catch(Throwable e) {
			context.getLogger().error("Invocazione terminata con errore: %s",e, e.getMessage());
			throw FaultCode.ERRORE_INTERNO.toException(e);
		}
    }

	/**
     * Restituisce l'indicazione sullo stato del gruppo
     *
     * Questa operazione consente di ottenere lo stato attuale del gruppo
     *
     */
	@Override
    public ApiImplStato getErogazioneStato(String nome, Integer versione, ProfiloEnum profilo, String soggetto, String gruppo, String tipoServizio) {
		IContext context = this.getContext();
		try {
			context.getLogger().info("Invocazione in corso ...");     

			AuthorizationManager.authorize(context, getAuthorizationConfig());
			context.getLogger().debug("Autorizzazione completata con successo");
		
			final ErogazioniConfEnv env = new ErogazioniConfEnv(context.getServletRequest(), profilo, soggetto, context, nome, versione, gruppo, tipoServizio );
			final PortaApplicativa pa = env.paCore.getPortaApplicativa(env.idPa);
			
			final ApiImplStato ret = new ApiImplStato();
			ret.setAbilitato(Helper.statoFunzionalitaConfToBool(pa.getStato()));
                        
		
			context.getLogger().info("Invocazione completata con successo");
        	return ret;
     
		}
		catch(javax.ws.rs.WebApplicationException e) {
			context.getLogger().error_except404("Invocazione terminata con errore '4xx': %s",e, e.getMessage());
			throw e;
		}
		catch(Throwable e) {
			context.getLogger().error("Invocazione terminata con errore: %s",e, e.getMessage());
			throw FaultCode.ERRORE_INTERNO.toException(e);
		}
    }
    
    /**
     * Restituisce il dettaglio di una regola di correlazione applicativa per la richiesta
     *
     * Questa operazione consente di ottenere il dettaglio di una regola di correlazione applicativa per la richiesta
     *
     */
	@Override
    public CorrelazioneApplicativaRichiesta getErogazioneTracciamentoCorrelazioneApplicativaRichiesta(String nome, Integer versione, String elemento, ProfiloEnum profilo, String soggetto, String gruppo, String tipoServizio) {
		IContext context = this.getContext();
		try {
			context.getLogger().info("Invocazione in corso ...");     

			AuthorizationManager.authorize(context, getAuthorizationConfig());
			context.getLogger().debug("Autorizzazione completata con successo");     
			
			final ErogazioniConfEnv env = new ErogazioniConfEnv(context.getServletRequest(), profilo, soggetto, context, nome, versione, gruppo, tipoServizio );
			final PortaApplicativa pa = env.paCore.getPortaApplicativa(env.idPa);
			
			final String searchElemento = elemento.equals("*")
					? ""
					: elemento;			
			List<CorrelazioneApplicativaElemento> lista = BaseHelper.evalnull( () -> pa.getCorrelazioneApplicativa().getElementoList() );
            
			Optional<CorrelazioneApplicativaElemento> el = BaseHelper.findFirst( lista, c -> (c.getNome()==null ? "" : c.getNome()).equals(searchElemento) );
			
			if ( !el.isPresent() )
				throw FaultCode.NOT_FOUND.toException("CorrelazioneApplicativaRichiesta per l'elemento " + elemento + " non presente");
			
			CorrelazioneApplicativaRichiesta ret = ErogazioniApiHelper.convert(el.get());
			
			context.getLogger().info("Invocazione completata con successo");
			return ret;
     
		}
		catch(javax.ws.rs.WebApplicationException e) {
			context.getLogger().error_except404("Invocazione terminata con errore '4xx': %s",e, e.getMessage());
			throw e;
		}
		catch(Throwable e) {
			context.getLogger().error("Invocazione terminata con errore: %s",e, e.getMessage());
			throw FaultCode.ERRORE_INTERNO.toException(e);
		}
    }
    
    /**
     * Restituisce il dettaglio di una regola di correlazione applicativa per la risposta
     *
     * Questa operazione consente di ottenere il dettaglio di una regola di correlazione applicativa per la risposta
     *
     */
	@Override
    public CorrelazioneApplicativaRisposta getErogazioneTracciamentoCorrelazioneApplicativaRisposta(String nome, Integer versione, String elemento, ProfiloEnum profilo, String soggetto, String gruppo, String tipoServizio) {
		IContext context = this.getContext();
		try {
			context.getLogger().info("Invocazione in corso ...");     

			AuthorizationManager.authorize(context, getAuthorizationConfig());
			context.getLogger().debug("Autorizzazione completata con successo");    
			
			final ErogazioniConfEnv env = new ErogazioniConfEnv(context.getServletRequest(), profilo, soggetto, context, nome, versione, gruppo, tipoServizio );
			final PortaApplicativa pa = env.paCore.getPortaApplicativa(env.idPa);
			
			final String searchElemento = elemento.equals("*")
					? ""
					: elemento;			
			
			List<CorrelazioneApplicativaRispostaElemento> lista = BaseHelper.evalnull( () -> pa.getCorrelazioneApplicativaRisposta().getElementoList() );
            
			Optional<CorrelazioneApplicativaRispostaElemento> el = BaseHelper.findFirst( lista, c -> (c.getNome()==null ? "" : c.getNome()).equals(searchElemento) );
			
			if ( !el.isPresent() )
				throw FaultCode.NOT_FOUND.toException("CorrelazioneApplicativaRisposta per l'elemento " + elemento + " non presente");
			
			CorrelazioneApplicativaRisposta ret = ErogazioniApiHelper.convert(el.get());
			
			context.getLogger().info("Invocazione completata con successo");
			return ret;
     
		}
		catch(javax.ws.rs.WebApplicationException e) {
			context.getLogger().error_except404("Invocazione terminata con errore '4xx': %s",e, e.getMessage());
			throw e;
		}
		catch(Throwable e) {
			context.getLogger().error("Invocazione terminata con errore: %s",e, e.getMessage());
			throw FaultCode.ERRORE_INTERNO.toException(e);
		}
    }
    
    /**
     * Restituisce la configurazione relativa alla validazione dei contenuti applicativi
     *
     * Questa operazione consente di ottenere la configurazione relativa alla validazione dei contenuti applicativi
     *
     */
	@Override
    public Validazione getErogazioneValidazione(String nome, Integer versione, ProfiloEnum profilo, String soggetto, String gruppo, String tipoServizio) {
		IContext context = this.getContext();
		try {
			context.getLogger().info("Invocazione in corso ...");     

			AuthorizationManager.authorize(context, getAuthorizationConfig());
			context.getLogger().debug("Autorizzazione completata con successo");     
            
			final ErogazioniConfEnv env = new ErogazioniConfEnv(context.getServletRequest(), profilo, soggetto, context, nome, versione, gruppo, tipoServizio );
			final PortaApplicativa pa = env.paCore.getPortaApplicativa(env.idPa);
			final Validazione ret = ErogazioniApiHelper.fromValidazioneContenutiApplicativi(pa.getValidazioneContenutiApplicativi());
        
			context.getLogger().info("Invocazione completata con successo");
			return ret;
     
		}
		catch(javax.ws.rs.WebApplicationException e) {
			context.getLogger().error_except404("Invocazione terminata con errore '4xx': %s",e, e.getMessage());
			throw e;
		}
		catch(Throwable e) {
			context.getLogger().error("Invocazione terminata con errore: %s",e, e.getMessage());
			throw FaultCode.ERRORE_INTERNO.toException(e);
		}
    }
    
    /**
     * Consente di modificare la configurazione relativa al caching delle risposte
     *
     * Questa operazione consente di aggiornare la configurazione relativa al caching delle risposte
     *
     */
	@Override
    public void updateErogazioneCachingRisposta(CachingRisposta body, String nome, Integer versione, ProfiloEnum profilo, String soggetto, String gruppo, String tipoServizio) {
		IContext context = this.getContext();
		try {
			context.getLogger().info("Invocazione in corso ...");     

			AuthorizationManager.authorize(context, getAuthorizationConfig());
			context.getLogger().debug("Autorizzazione completata con successo");
			
			BaseHelper.throwIfNull(body);
			
			// Così prendi le impostazioni di default.
			//  Configurazione configurazione = confCore.getConfigurazioneGenerale();
			//  ResponseCachingConfigurazioneGenerale responseCachingGenerale = configurazione.getResponseCaching();
			//  responseCachingGenerale.getConfigurazione();		
			
			if (body.isMaxResponseSize()) {
				if (body.getMaxResponseSizeKb() == null)
					throw FaultCode.RICHIESTA_NON_VALIDA.toException("Devi specificare il campo MaxResponseSizeKb");
			}
			
			final ErogazioniConfEnv env = new ErogazioniConfEnv(context.getServletRequest(), profilo, soggetto, context, nome, versione, gruppo, tipoServizio );
			final PortaApplicativa pa = env.paCore.getPortaApplicativa(env.idPa);
		
			if (!env.paHelper.checkDataConfigurazioneResponseCachingPorta(TipoOperazione.OTHER, true, body.getStato().toString())) {
				throw FaultCode.RICHIESTA_NON_VALIDA.toException(StringEscapeUtils.unescapeHtml(env.pd.getMessage()));
			}
			
			ResponseCachingConfigurazione newConfigurazione = ErogazioniApiHelper.buildResponseCachingConfigurazione(body, env.paHelper);
			
			pa.setResponseCaching(newConfigurazione);
			env.paCore.performUpdateOperation(env.userLogin, false, pa);			
			
			context.getLogger().info("Invocazione completata con successo");
     
		}
		catch(javax.ws.rs.WebApplicationException e) {
			context.getLogger().error_except404("Invocazione terminata con errore '4xx': %s",e, e.getMessage());
			throw e;
		}
		catch(Throwable e) {
			context.getLogger().error("Invocazione terminata con errore: %s",e, e.getMessage());
			throw FaultCode.ERRORE_INTERNO.toException(e);
		}
    }

	/**
     * Consente di modificare la configurazione relativa all'autenticazione per quanto concerne il controllo degli accessi
     *
     * Questa operazione consente di aggiornare la configurazione relativa all'autenticazione per quanto concerne il controllo degli accessi
     *
     */
	@Override
    public void updateErogazioneControlloAccessiAutenticazione(ControlloAccessiAutenticazione body, String nome, Integer versione, ProfiloEnum profilo, String soggetto, String gruppo, String tipoServizio) {
		IContext context = this.getContext();
		try {
			context.getLogger().info("Invocazione in corso ...");     

			AuthorizationManager.authorize(context, getAuthorizationConfig());
			context.getLogger().debug("Autorizzazione completata con successo");
			
			BaseHelper.throwIfNull(body);
			
			final ErogazioniConfEnv env = new ErogazioniConfEnv(context.getServletRequest(), profilo, soggetto, context, nome, versione, gruppo, tipoServizio );
			final PortaApplicativa newPa = env.paCore.getPortaApplicativa(env.idPa);
			final PortaApplicativa oldPa = env.paCore.getPortaApplicativa(env.idPa);			

			ErogazioniApiHelper.fillPortaApplicativa(env, body, newPa);
			
			if (! ErogazioniApiHelper.controlloAccessiCheckPA(env, oldPa, newPa)) {
				throw FaultCode.RICHIESTA_NON_VALIDA.toException(StringEscapeUtils.unescapeHtml(  env.pd.getMessage() ));
			}
		
			env.paCore.performUpdateOperation(env.userLogin, false, newPa);
        
			context.getLogger().info("Invocazione completata con successo");
     
		}
		catch(javax.ws.rs.WebApplicationException e) {
			context.getLogger().error_except404("Invocazione terminata con errore '4xx': %s",e, e.getMessage());
			throw e;
		}
		catch(Throwable e) {
			context.getLogger().error("Invocazione terminata con errore: %s",e, e.getMessage());
			throw FaultCode.ERRORE_INTERNO.toException(e);
		}
    }

	/**
     * Consente di modificare la configurazione relativa all'autorizzazione per quanto concerne il controllo degli accessi
     *
     * Questa operazione consente di aggiornare la configurazione relativa all'autorizzazione per quanto concerne il controllo degli accessi
     *
     */
	@Override
    public void updateErogazioneControlloAccessiAutorizzazione(ControlloAccessiAutorizzazione body, String nome, Integer versione, ProfiloEnum profilo, String soggetto, String gruppo, String tipoServizio) {
		IContext context = this.getContext();
		try {
			context.getLogger().info("Invocazione in corso ...");     

			AuthorizationManager.authorize(context, getAuthorizationConfig());
			context.getLogger().debug("Autorizzazione completata con successo");
			
			BaseHelper.throwIfNull(body);
			
			final ErogazioniConfEnv env = new ErogazioniConfEnv(context.getServletRequest(), profilo, soggetto, context, nome, versione, gruppo, tipoServizio );
			final PortaApplicativa pa = env.paCore.getPortaApplicativa(env.idPa);					
			final PortaApplicativa newPa = env.paCore.getPortaApplicativa(env.idPa);		
			
			ErogazioniApiHelper.fillPortaApplicativa(body, newPa);
			
			if (!ErogazioniApiHelper.controlloAccessiCheckPA(env, pa, newPa)) {
				throw FaultCode.RICHIESTA_NON_VALIDA.toException(StringEscapeUtils.unescapeHtml(  env.pd.getMessage() ));
			}
			
			env.paCore.performUpdateOperation(env.userLogin, false, newPa);
                        
			context.getLogger().info("Invocazione completata con successo");
     
		}
		catch(javax.ws.rs.WebApplicationException e) {
			context.getLogger().error_except404("Invocazione terminata con errore '4xx': %s",e, e.getMessage());
			throw e;
		}
		catch(Throwable e) {
			context.getLogger().error("Invocazione terminata con errore: %s",e, e.getMessage());
			throw FaultCode.ERRORE_INTERNO.toException(e);
		}
    }

	/**
     * Consente di modificare la configurazione relativa alla gestione dei token per quanto concerne il controllo degli accessi
     *
     * Questa operazione consente di aggiornare la configurazione relativa alla gestione dei token per quanto concerne il controllo degli accessi
     *
     */
	@Override
    public void updateErogazioneControlloAccessiGestioneToken(ControlloAccessiGestioneToken body, String nome, Integer versione, ProfiloEnum profilo, String soggetto, String gruppo, String tipoServizio) {
		IContext context = this.getContext();
		try {
			context.getLogger().info("Invocazione in corso ...");
			
			AuthorizationManager.authorize(context, getAuthorizationConfig());
			context.getLogger().debug("Autorizzazione completata con successo");     
			
			BaseHelper.throwIfNull(body);
			
			final ErogazioniConfEnv env = new ErogazioniConfEnv(context.getServletRequest(), profilo, soggetto, context, nome, versione, gruppo, tipoServizio );
			final PortaApplicativa oldPa = env.paCore.getPortaApplicativa(env.idPa);
			final PortaApplicativa newPa = env.paCore.getPortaApplicativa(env.idPa);
			
			if (body.isAbilitato()) {
				GestioneToken gTok = newPa.getGestioneToken() != null ? newPa.getGestioneToken() : new GestioneToken();
				ErogazioniApiHelper.fillGestioneToken(gTok, body);
				newPa.setGestioneToken(gTok);
			}
			else
				newPa.setGestioneToken(null);
			
			if ( !ErogazioniApiHelper.controlloAccessiCheckPA(env, oldPa, newPa) ) {
				throw FaultCode.RICHIESTA_NON_VALIDA.toException(env.pd.getMessage());
			}
			
			env.paCore.performUpdateOperation(env.userLogin, false, newPa);
			
			context.getLogger().info("Invocazione completata con successo");
 
		}
		catch(javax.ws.rs.WebApplicationException e) {
			context.getLogger().error_except404("Invocazione terminata con errore '4xx': %s",e, e.getMessage());
			throw e;
		}
		catch(Throwable e) {
			context.getLogger().error("Invocazione terminata con errore: %s",e, e.getMessage());
			throw FaultCode.ERRORE_INTERNO.toException(e);
		}
    }
    
    /**
     * Consente di modificare la configurazione CORS associata all'erogazione
     *
     * Questa operazione consente di aggiornare la configurazione CORS associata all'erogazione identificata dal nome e dalla versione
     *
     */
	@Override
    public void updateErogazioneGestioneCORS(GestioneCors body, String nome, Integer versione, ProfiloEnum profilo, String soggetto, String tipoServizio) {
		IContext context = this.getContext();
		try {
			context.getLogger().info("Invocazione in corso ...");     

			AuthorizationManager.authorize(context, getAuthorizationConfig());
			context.getLogger().debug("Autorizzazione completata con successo");
			
			BaseHelper.throwIfNull(body);

			final ErogazioniEnv env = new ErogazioniEnv(context.getServletRequest(), profilo, soggetto, context);
			final AccordoServizioParteSpecifica asps = BaseHelper.supplyOrNotFound( () -> ErogazioniApiHelper.getServizioIfErogazione(tipoServizio , nome, versione, env.idSoggetto.toIDSoggetto(), env), "Erogazione");
			final IdServizio idAsps = new IdServizio(env.idServizioFactory.getIDServizioFromAccordo(asps), asps.getId());
			
			final IDPortaApplicativa idPa = BaseHelper.supplyOrNotFound( () -> ErogazioniApiHelper.getIDGruppoPADefault( idAsps, env.apsCore ),  "Gruppo default per l'erogazione scelta" );
			final PortaApplicativa pa = env.paCore.getPortaApplicativa(idPa);
			final CorsConfigurazione oldConf = pa.getGestioneCors();
			
			
			if (body.isRidefinito())
				pa.setGestioneCors(ErogazioniApiHelper.buildCorsConfigurazione(body, env, oldConf));
			else
				pa.setGestioneCors(null);
			
			env.paCore.performUpdateOperation(env.userLogin, false, pa);
		    
			context.getLogger().info("Invocazione completata con successo");
        
     
		}
		catch(javax.ws.rs.WebApplicationException e) {
			context.getLogger().error_except404("Invocazione terminata con errore '4xx': %s",e, e.getMessage());
			throw e;
		}
		catch(Throwable e) {
			context.getLogger().error("Invocazione terminata con errore: %s",e, e.getMessage());
			throw FaultCode.ERRORE_INTERNO.toException(e);
		}
    }
    
    /**
     * Modifica i dati di una policy di rate limiting
     *
     * Questa operazione consente di aggiornare i dati relativi ad una policy di rate limiting
     *
     */
	@Override
    public void updateErogazioneRateLimitingPolicy(RateLimitingPolicyErogazioneUpdate body, String nome, Integer versione, String idPolicy, ProfiloEnum profilo, String soggetto, String gruppo, String tipoServizio) {
		IContext context = this.getContext();
		try {
			context.getLogger().info("Invocazione in corso ...");     

			AuthorizationManager.authorize(context, getAuthorizationConfig());
			context.getLogger().debug("Autorizzazione completata con successo");
			
			BaseHelper.throwIfNull(body);            
			
			final ErogazioniConfEnv env = new ErogazioniConfEnv(context.getServletRequest(), profilo, soggetto, context, nome, versione, gruppo, tipoServizio );
			final PortaApplicativa pa = env.paCore.getPortaApplicativa(env.idPa);		
			final AccordoServizioParteSpecifica asps = BaseHelper.supplyOrNotFound(() -> ErogazioniApiHelper
					.getServizioIfErogazione(tipoServizio, nome, versione, env.idSoggetto.toIDSoggetto(), env),
					"Accordo Servizio Parte Specifica");
			final AccordoServizioParteComuneSintetico apc = env.apcCore.getAccordoServizioSintetico(asps.getIdAccordo());
			
			AttivazionePolicy policy = BaseHelper.supplyOrNotFound( 
					() -> env.confCore.getAttivazionePolicy(idPolicy, RuoloPolicy.APPLICATIVA, pa.getNome()),
					"Rate Limiting Policy con nome " + idPolicy
				);
			if ( policy == null ) 
				throw FaultCode.NOT_FOUND.toException("Nessuna policy di rate limiting con nome " + idPolicy );
			InfoPolicy infoPolicy = env.confCore.getInfoPolicy(policy.getIdPolicy());
		
			ErogazioniApiHelper.override(body, env.protocolFactory.getProtocol(), env.idSoggetto.toIDSoggetto(), env.requestWrapper);	
			String errorAttivazione = env.confHelper.readDatiAttivazionePolicyFromHttpParameters(policy, false, TipoOperazione.CHANGE, infoPolicy);
			if ( !StringUtils.isEmpty(errorAttivazione) ) {
				throw FaultCode.RICHIESTA_NON_VALIDA.toException(StringEscapeUtils.unescapeHtml(errorAttivazione));
			}
			
			String modalita = ErogazioniApiHelper.getDataElementModalita(infoPolicy.isBuiltIn());
			ErogazioniApiHelper.attivazionePolicyCheckData(TipoOperazione.CHANGE, pa, policy, infoPolicy, env, env.apcCore.toMessageServiceBinding(apc.getServiceBinding()), modalita);
			
			env.confCore.performUpdateOperation(env.userLogin, false, policy);

			context.getLogger().info("Invocazione completata con successo");
		}
		catch(javax.ws.rs.WebApplicationException e) {
			context.getLogger().error_except404("Invocazione terminata con errore '4xx': %s",e, e.getMessage());
			throw e;
		}
		catch(Throwable e) {
			context.getLogger().error("Invocazione terminata con errore: %s",e, e.getMessage());
			throw FaultCode.ERRORE_INTERNO.toException(e);
		}
    }
    
    /**
     * Consente di modificare la configurazione relativa alla registrazione dei messaggi
     *
     * Questa operazione consente di aggiornare la configurazione relativa alla registrazione dei messaggi
     *
     */
	@Override
    public void updateErogazioneRegistrazioneMessaggi(RegistrazioneMessaggi body, String nome, Integer versione, ProfiloEnum profilo, String soggetto, String gruppo, String tipoServizio) {
		IContext context = this.getContext();
		try {
			context.getLogger().info("Invocazione in corso ...");     

			AuthorizationManager.authorize(context, getAuthorizationConfig());
			context.getLogger().debug("Autorizzazione completata con successo");
			
			BaseHelper.throwIfNull(body);
			
			final ErogazioniConfEnv env = new ErogazioniConfEnv(context.getServletRequest(), profilo, soggetto, context, nome, versione, gruppo, tipoServizio );
			final PortaApplicativa pa = env.paCore.getPortaApplicativa(env.idPa);
			
			if (body.isRidefinito())
				pa.setDump(ErogazioniApiHelper.buildDumpConfigurazione(body, true, env));
			else
				pa.setDump(null);
			
			env.paCore.performUpdateOperation(env.userLogin, false, pa);

			context.getLogger().info("Invocazione completata con successo");
        
     
		}
		catch(javax.ws.rs.WebApplicationException e) {
			context.getLogger().error_except404("Invocazione terminata con errore '4xx': %s",e, e.getMessage());
			throw e;
		}
		catch(Throwable e) {
			context.getLogger().error("Invocazione terminata con errore: %s",e, e.getMessage());
			throw FaultCode.ERRORE_INTERNO.toException(e);
		}
    }
    
    /**
     * Consente di modificare lo stato del gruppo
     *
     * Questa operazione consente di aggiornare lo stato del gruppo
     *
     */
	@Override
    public void updateErogazioneStato(ApiImplStato body, String nome, Integer versione, ProfiloEnum profilo, String soggetto, String gruppo, String tipoServizio) {
		IContext context = this.getContext();
		try {
			context.getLogger().info("Invocazione in corso ...");     

			AuthorizationManager.authorize(context, getAuthorizationConfig());
			context.getLogger().debug("Autorizzazione completata con successo");     		
			
			BaseHelper.throwIfNull(body);	
			
			final ErogazioniConfEnv env = new ErogazioniConfEnv(context.getServletRequest(), profilo, soggetto, context, nome, versione, gruppo, tipoServizio );
			final PortaApplicativa pa = env.paCore.getPortaApplicativa(env.idPa);
			
			IDPortaApplicativa oldIDPortaApplicativaForUpdate = new IDPortaApplicativa();
			oldIDPortaApplicativaForUpdate.setNome(pa.getNome());
			pa.setOldIDPortaApplicativaForUpdate(oldIDPortaApplicativaForUpdate);
			
			pa.setStato(Helper.boolToStatoFunzionalitaConf(body.isAbilitato()));
						
			env.paCore.performUpdateOperation(env.userLogin, false, pa);
			context.getLogger().info("Invocazione completata con successo");
		}
		catch(javax.ws.rs.WebApplicationException e) {
			context.getLogger().error_except404("Invocazione terminata con errore '4xx': %s",e, e.getMessage());
			throw e;
		}
		catch(Throwable e) {
			context.getLogger().error("Invocazione terminata con errore: %s",e, e.getMessage());
			throw FaultCode.ERRORE_INTERNO.toException(e);
		}
    }
    
    /**
     * Modifica i dati di una regola di correlazione applicativa per la richiesta
     *
     * Questa operazione consente di aggiornare i dati relativi ad una regola di correlazione applicativa per la richiesta
     *
     */
	@Override
    public void updateErogazioneTracciamentoCorrelazioneApplicativaRichiesta(CorrelazioneApplicativaRichiesta body, String nome, Integer versione, String elemento, ProfiloEnum profilo, String soggetto, String gruppo, String tipoServizio) {
		IContext context = this.getContext();
		try {
			context.getLogger().info("Invocazione in corso ...");     

			AuthorizationManager.authorize(context, getAuthorizationConfig());
			context.getLogger().debug("Autorizzazione completata con successo");
			
			BaseHelper.throwIfNull(body);
			
			if ( body.getElemento() == null )
				body.setElemento("");
			
			final String searchElemento = elemento.equals("*")
					? ""
					: elemento;
                        
			final ErogazioniConfEnv env = new ErogazioniConfEnv(context.getServletRequest(), profilo, soggetto, context, nome, versione, gruppo, tipoServizio );
			final PortaApplicativa pa = env.paCore.getPortaApplicativa(env.idPa);
			final Long idPorta = pa.getId();
			
			if (pa.getCorrelazioneApplicativa() == null)
				pa.setCorrelazioneApplicativa(new org.openspcoop2.core.config.CorrelazioneApplicativa());
			
			final List<CorrelazioneApplicativaElemento> correlazioni = pa.getCorrelazioneApplicativa().getElementoList();
			final CorrelazioneApplicativaElemento oldElem = BaseHelper.findAndRemoveFirst(correlazioni, c -> (c.getNome()==null ? "" : c.getNome()).equals(searchElemento));
			
			if ( oldElem == null ) 
				throw FaultCode.NOT_FOUND.toException("Correlazione Applicativa Richiesta per l'elemento " + elemento + " non trovata ");
			
			if ( !correlazioneApplicativaRichiestaCheckData(TipoOperazione.CHANGE, env.requestWrapper, env.pdHelper, false, body, idPorta, oldElem.getId())) {
				throw FaultCode.RICHIESTA_NON_VALIDA.toException(StringEscapeUtils.unescapeHtml(env.pd.getMessage()));
			}
			
			correlazioni.add(convert(body));
                        
			pa.getCorrelazioneApplicativa().setElementoList(correlazioni);
			
			env.paCore.performUpdateOperation(env.userLogin, false, pa);
			
			context.getLogger().info("Invocazione completata con successo");
         
		}
		catch(javax.ws.rs.WebApplicationException e) {
			context.getLogger().error_except404("Invocazione terminata con errore '4xx': %s",e, e.getMessage());
			throw e;
		}
		catch(Throwable e) {
			context.getLogger().error("Invocazione terminata con errore: %s",e, e.getMessage());
			throw FaultCode.ERRORE_INTERNO.toException(e);
		}
    }
    
    /**
     * Modifica i dati di una regola di correlazione applicativa per la risposta
     *
     * Questa operazione consente di aggiornare i dati relativi ad una regola di correlazione applicativa per la risposta
     *
     */
	@Override
    public void updateErogazioneTracciamentoCorrelazioneApplicativaRisposta(CorrelazioneApplicativaRisposta body, String nome, Integer versione, String elemento, ProfiloEnum profilo, String soggetto, String gruppo, String tipoServizio) {
		IContext context = this.getContext();
		try {
			context.getLogger().info("Invocazione in corso ...");     

			AuthorizationManager.authorize(context, getAuthorizationConfig());
			context.getLogger().debug("Autorizzazione completata con successo");     
			
			BaseHelper.throwIfNull(body);
			
			if ( body.getElemento() == null )
				body.setElemento("");
			
			final String searchElemento = elemento.equals("*")
					? ""
					: elemento;
                        
			final ErogazioniConfEnv env = new ErogazioniConfEnv(context.getServletRequest(), profilo, soggetto, context, nome, versione, gruppo, tipoServizio );
			final PortaApplicativa pa = env.paCore.getPortaApplicativa(env.idPa);
			final Long idPorta = pa.getId();
			
			if (pa.getCorrelazioneApplicativaRisposta() == null)
				pa.setCorrelazioneApplicativaRisposta(new org.openspcoop2.core.config.CorrelazioneApplicativaRisposta());
			
			final List<CorrelazioneApplicativaRispostaElemento> correlazioni = pa.getCorrelazioneApplicativaRisposta().getElementoList();
			final CorrelazioneApplicativaRispostaElemento oldElem = BaseHelper.findAndRemoveFirst(correlazioni, c -> (c.getNome()==null ? "" : c.getNome()).equals(searchElemento));
			
			if ( oldElem == null ) 
				throw FaultCode.NOT_FOUND.toException("Correlazione Applicativa Risposta per l'elemento " + elemento + " non trovata ");
			
			if ( !correlazioneApplicativaRispostaCheckData(TipoOperazione.CHANGE, env.requestWrapper, env.pdHelper, false, body, idPorta, oldElem.getId())) {
				throw FaultCode.RICHIESTA_NON_VALIDA.toException(StringEscapeUtils.unescapeHtml(env.pd.getMessage()));
			}
			
			correlazioni.add(convert(body));
                        
			pa.getCorrelazioneApplicativaRisposta().setElementoList(correlazioni);
			
			env.paCore.performUpdateOperation(env.userLogin, false, pa);
        
			context.getLogger().info("Invocazione completata con successo");
        
     
		}
		catch(javax.ws.rs.WebApplicationException e) {
			context.getLogger().error_except404("Invocazione terminata con errore '4xx': %s",e, e.getMessage());
			throw e;
		}
		catch(Throwable e) {
			context.getLogger().error("Invocazione terminata con errore: %s",e, e.getMessage());
			throw FaultCode.ERRORE_INTERNO.toException(e);
		}
    }
    
    /**
     * Consente di modificare la configurazione relativa alla validazione dei contenuti applicativi
     *
     * Questa operazione consente di aggiornare la configurazione relativa alla validazione dei contenuti applicativi
     *
     */
	@Override
    public void updateErogazioneValidazione(Validazione body, String nome, Integer versione, ProfiloEnum profilo, String soggetto, String gruppo, String tipoServizio) {
		IContext context = this.getContext();
		try {
			context.getLogger().info("Invocazione in corso ...");     

			AuthorizationManager.authorize(context, getAuthorizationConfig());
			context.getLogger().debug("Autorizzazione completata con successo");
			
			BaseHelper.throwIfNull(body);	
			
			final ErogazioniConfEnv env = new ErogazioniConfEnv(context.getServletRequest(), profilo, soggetto, context, nome, versione, gruppo, tipoServizio );
			final PortaApplicativa pa = env.paCore.getPortaApplicativa(env.idPa);
			
			final String stato = BaseHelper.evalnull( () -> body.getStato().toString());
			final String tipoValidazione = BaseHelper.evalnull( () -> body.getTipo().toString() );
			
			env.requestWrapper.overrideParameter(CostantiControlStation.PARAMETRO_PORTE_XSD, stato);
			if (!env.paHelper.validazioneContenutiCheck(TipoOperazione.OTHER, false)) {
				throw FaultCode.RICHIESTA_NON_VALIDA.toException(StringEscapeUtils.unescapeHtml(  env.pd.getMessage() ));
			}
			
			final ValidazioneContenutiApplicativi vx = new ValidazioneContenutiApplicativi();
			// Imposto Mtom al valore eventualmente già presente nel db.
			vx.setAcceptMtomMessage( BaseHelper.evalnull( () -> pa.getValidazioneContenutiApplicativi().getAcceptMtomMessage()) );
			vx.setStato( StatoFunzionalitaConWarning.toEnumConstant(stato) );
			vx.setTipo( ValidazioneContenutiApplicativiTipo.toEnumConstant(tipoValidazione) );
			
			pa.setValidazioneContenutiApplicativi(vx);

			env.paCore.performUpdateOperation(env.userLogin, false, pa);
        
			context.getLogger().info("Invocazione completata con successo");
     
		}
		catch(javax.ws.rs.WebApplicationException e) {
			context.getLogger().error_except404("Invocazione terminata con errore '4xx': %s",e, e.getMessage());
			throw e;
		}
		catch(Throwable e) {
			context.getLogger().error("Invocazione terminata con errore: %s",e, e.getMessage());
			throw FaultCode.ERRORE_INTERNO.toException(e);
		}
    }
    
}

