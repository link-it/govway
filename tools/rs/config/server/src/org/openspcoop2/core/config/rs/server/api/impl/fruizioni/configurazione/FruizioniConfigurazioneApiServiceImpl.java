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
package org.openspcoop2.core.config.rs.server.api.impl.fruizioni.configurazione;

import static org.openspcoop2.core.config.rs.server.api.impl.erogazioni.ErogazioniApiHelper.convert;
import static org.openspcoop2.core.config.rs.server.api.impl.erogazioni.ErogazioniApiHelper.correlazioneApplicativaRichiestaCheckData;
import static org.openspcoop2.core.config.rs.server.api.impl.erogazioni.ErogazioniApiHelper.correlazioneApplicativaRispostaCheckData;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.commons.text.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;
import org.openspcoop2.core.byok.BYOKUtilities;
import org.openspcoop2.core.commons.CoreException;
import org.openspcoop2.core.commons.Filtri;
import org.openspcoop2.core.commons.Liste;
import org.openspcoop2.core.config.AttributeAuthority;
import org.openspcoop2.core.config.AutorizzazioneRuoli;
import org.openspcoop2.core.config.AutorizzazioneScope;
import org.openspcoop2.core.config.CorrelazioneApplicativa;
import org.openspcoop2.core.config.CorrelazioneApplicativaElemento;
import org.openspcoop2.core.config.CorrelazioneApplicativaRispostaElemento;
import org.openspcoop2.core.config.CorsConfigurazione;
import org.openspcoop2.core.config.GenericProperties;
import org.openspcoop2.core.config.GestioneToken;
import org.openspcoop2.core.config.PortaDelegata;
import org.openspcoop2.core.config.PortaDelegataAutorizzazioneServiziApplicativi;
import org.openspcoop2.core.config.PortaDelegataAutorizzazioneToken;
import org.openspcoop2.core.config.PortaDelegataServizioApplicativo;
import org.openspcoop2.core.config.Proprieta;
import org.openspcoop2.core.config.ResponseCachingConfigurazione;
import org.openspcoop2.core.config.Ruolo;
import org.openspcoop2.core.config.Scope;
import org.openspcoop2.core.config.ServizioApplicativo;
import org.openspcoop2.core.config.ValidazioneContenutiApplicativi;
import org.openspcoop2.core.config.constants.StatoFunzionalita;
import org.openspcoop2.core.config.constants.StatoFunzionalitaConWarning;
import org.openspcoop2.core.config.constants.TipoAutenticazione;
import org.openspcoop2.core.config.constants.TipoAutenticazionePrincipal;
import org.openspcoop2.core.config.constants.TipoAutorizzazione;
import org.openspcoop2.core.config.constants.ValidazioneContenutiApplicativiTipo;
import org.openspcoop2.core.config.driver.DriverConfigurazioneNotFound;
import org.openspcoop2.core.config.driver.db.IDServizioApplicativoDB;
import org.openspcoop2.core.config.rs.server.api.FruizioniConfigurazioneApi;
import org.openspcoop2.core.config.rs.server.api.impl.Enums;
import org.openspcoop2.core.config.rs.server.api.impl.Helper;
import org.openspcoop2.core.config.rs.server.api.impl.IdServizio;
import org.openspcoop2.core.config.rs.server.api.impl.erogazioni.ErogazioniApiHelper;
import org.openspcoop2.core.config.rs.server.api.impl.erogazioni.ErogazioniEnv;
import org.openspcoop2.core.config.rs.server.config.ServerProperties;
import org.openspcoop2.core.config.rs.server.model.APIImplAutenticazioneApiKey;
import org.openspcoop2.core.config.rs.server.model.APIImplAutenticazioneApiKeyConfig;
import org.openspcoop2.core.config.rs.server.model.APIImplAutenticazioneApiKeyPosizione;
import org.openspcoop2.core.config.rs.server.model.APIImplAutenticazioneBasic;
import org.openspcoop2.core.config.rs.server.model.APIImplAutenticazioneCustom;
import org.openspcoop2.core.config.rs.server.model.APIImplAutenticazioneHttps;
import org.openspcoop2.core.config.rs.server.model.APIImplAutenticazionePrincipal;
import org.openspcoop2.core.config.rs.server.model.ApiCanale;
import org.openspcoop2.core.config.rs.server.model.ApiImplStato;
import org.openspcoop2.core.config.rs.server.model.CachingRisposta;
import org.openspcoop2.core.config.rs.server.model.ConfigurazioneApiCanale;
import org.openspcoop2.core.config.rs.server.model.ConfigurazioneCanaleEnum;
import org.openspcoop2.core.config.rs.server.model.ControlloAccessiAttributeAuthority;
import org.openspcoop2.core.config.rs.server.model.ControlloAccessiAutenticazione;
import org.openspcoop2.core.config.rs.server.model.ControlloAccessiAutenticazioneToken;
import org.openspcoop2.core.config.rs.server.model.ControlloAccessiAutorizzazione;
import org.openspcoop2.core.config.rs.server.model.ControlloAccessiAutorizzazioneApplicativi;
import org.openspcoop2.core.config.rs.server.model.ControlloAccessiAutorizzazioneApplicativo;
import org.openspcoop2.core.config.rs.server.model.ControlloAccessiAutorizzazioneRuoli;
import org.openspcoop2.core.config.rs.server.model.ControlloAccessiAutorizzazioneRuolo;
import org.openspcoop2.core.config.rs.server.model.ControlloAccessiAutorizzazioneScope;
import org.openspcoop2.core.config.rs.server.model.ControlloAccessiAutorizzazioneScopes;
import org.openspcoop2.core.config.rs.server.model.ControlloAccessiAutorizzazioneView;
import org.openspcoop2.core.config.rs.server.model.ControlloAccessiGestioneToken;
import org.openspcoop2.core.config.rs.server.model.ControlloAccessiIdentificazioneAttributi;
import org.openspcoop2.core.config.rs.server.model.CorrelazioneApplicativaRichiesta;
import org.openspcoop2.core.config.rs.server.model.CorrelazioneApplicativaRichiestaEnum;
import org.openspcoop2.core.config.rs.server.model.CorrelazioneApplicativaRichiestaItem;
import org.openspcoop2.core.config.rs.server.model.CorrelazioneApplicativaRisposta;
import org.openspcoop2.core.config.rs.server.model.CorrelazioneApplicativaRispostaEnum;
import org.openspcoop2.core.config.rs.server.model.CorrelazioneApplicativaRispostaItem;
import org.openspcoop2.core.config.rs.server.model.ElencoProprieta;
import org.openspcoop2.core.config.rs.server.model.GestioneCors;
import org.openspcoop2.core.config.rs.server.model.ListaCorrelazioneApplicativaRichiesta;
import org.openspcoop2.core.config.rs.server.model.ListaCorrelazioneApplicativaRisposta;
import org.openspcoop2.core.config.rs.server.model.ListaRateLimitingPolicy;
import org.openspcoop2.core.config.rs.server.model.OneOfControlloAccessiAutenticazioneAutenticazione;
import org.openspcoop2.core.config.rs.server.model.RateLimitingCriteriMetricaEnum;
import org.openspcoop2.core.config.rs.server.model.RateLimitingPolicyFruizione;
import org.openspcoop2.core.config.rs.server.model.RateLimitingPolicyFruizioneUpdate;
import org.openspcoop2.core.config.rs.server.model.RateLimitingPolicyFruizioneView;
import org.openspcoop2.core.config.rs.server.model.RateLimitingPolicyItem;
import org.openspcoop2.core.config.rs.server.model.RegistrazioneMessaggi;
import org.openspcoop2.core.config.rs.server.model.RegistrazioneDiagnosticiConfigurazione;
import org.openspcoop2.core.config.rs.server.model.RegistrazioneTransazioniConfigurazione;
import org.openspcoop2.core.config.rs.server.model.StatoFunzionalitaConWarningEnum;
import org.openspcoop2.core.config.rs.server.model.TipoAutenticazioneEnum;
import org.openspcoop2.core.config.rs.server.model.TipoAutenticazionePrincipalToken;
import org.openspcoop2.core.config.rs.server.model.Validazione;
import org.openspcoop2.core.controllo_traffico.AttivazionePolicy;
import org.openspcoop2.core.controllo_traffico.AttivazionePolicyFiltro;
import org.openspcoop2.core.controllo_traffico.AttivazionePolicyRaggruppamento;
import org.openspcoop2.core.controllo_traffico.beans.InfoPolicy;
import org.openspcoop2.core.controllo_traffico.constants.RuoloPolicy;
import org.openspcoop2.core.controllo_traffico.utils.ControlloTrafficoDriverUtils;
import org.openspcoop2.core.controllo_traffico.utils.PolicyUtilities;
import org.openspcoop2.core.id.IDPortaDelegata;
import org.openspcoop2.core.id.IDServizioApplicativo;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.core.registry.AccordoServizioParteSpecifica;
import org.openspcoop2.core.registry.beans.AccordoServizioParteComuneSintetico;
import org.openspcoop2.core.registry.constants.RuoloContesto;
import org.openspcoop2.core.registry.constants.RuoloTipologia;
import org.openspcoop2.core.registry.constants.ScopeContesto;
import org.openspcoop2.core.registry.driver.FiltroRicercaRuoli;
import org.openspcoop2.core.registry.driver.FiltroRicercaScope;
import org.openspcoop2.message.constants.ServiceBinding;
import org.openspcoop2.pdd.core.autenticazione.ParametriAutenticazioneApiKey;
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
import org.openspcoop2.web.ctrlstat.core.ConsoleSearch;
import org.openspcoop2.web.ctrlstat.costanti.CostantiControlStation;
import org.openspcoop2.web.ctrlstat.servlet.ApiKeyState;
import org.openspcoop2.web.ctrlstat.servlet.ConsoleUtilities;
import org.openspcoop2.web.ctrlstat.servlet.config.ConfigurazioneUtilities;
import org.openspcoop2.web.ctrlstat.servlet.pd.PorteDelegateCostanti;
import org.openspcoop2.web.ctrlstat.servlet.ruoli.RuoliCore;
import org.openspcoop2.web.ctrlstat.servlet.sa.ServiziApplicativiCostanti;
import org.openspcoop2.web.lib.mvc.TipoOperazione;
/**
 * FruizioniConfigurazioneApiServiceImpl
 * 
 * @author $Author$
 * @version $Rev$, $Date$
 * 
 */
public class FruizioniConfigurazioneApiServiceImpl extends BaseImpl implements FruizioniConfigurazioneApi {

	public FruizioniConfigurazioneApiServiceImpl(){
		super(org.slf4j.LoggerFactory.getLogger(FruizioniConfigurazioneApiServiceImpl.class));
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
    public void addFruizioneControlloAccessiAutorizzazioneApplicativi(ControlloAccessiAutorizzazioneApplicativo body, String erogatore, String nome, Integer versione, ProfiloEnum profilo, String soggetto, String gruppo, String tipoServizio) {
		IContext context = this.getContext();
		try {
			context.getLogger().info("Invocazione in corso ...");     

			AuthorizationManager.authorize(context, getAuthorizationConfig());
			context.getLogger().debug("Autorizzazione completata con successo");
			
			BaseHelper.throwIfNull(body);
			
			final FruizioniConfEnv env = new FruizioniConfEnv(context.getServletRequest(), profilo, soggetto, context, erogatore, nome, versione, gruppo, tipoServizio);		
			final PortaDelegata pd = env.pdCore.getPortaDelegata(env.idPd);
			
			final IDServizioApplicativo idSA = new IDServizioApplicativo();
				idSA.setIdSoggettoProprietario(env.idSoggetto.toIDSoggetto());
				idSA.setNome(body.getApplicativo());
				
				if ( !TipoAutorizzazione.isAuthenticationRequired(pd.getAutorizzazione())) {
					throw FaultCode.RICHIESTA_NON_VALIDA.toException("L'autenticazione puntuale non è abilitata");
				}
			
			final ServizioApplicativo sa = BaseHelper.supplyOrNonValida( 
					() -> env.saCore.getServizioApplicativo(idSA),
					"Servizio Applicativo " + idSA.toString()
				);
			
			// Prendo la lista di servizi applicativi associati al soggetto
			final org.openspcoop2.core.config.constants.CredenzialeTipo tipoAutenticazione = org.openspcoop2.core.config.constants.CredenzialeTipo.toEnumConstant(pd.getAutenticazione());
			if(tipoAutenticazione==null) {
				throw FaultCode.RICHIESTA_NON_VALIDA.toException("Non risulta abilitato un tipo di autenticazione trasporto nella fruizione selezionata");
			}
			Boolean appId = null;
			if(org.openspcoop2.core.config.constants.CredenzialeTipo.APIKEY.equals(tipoAutenticazione)) {
				ApiKeyState apiKeyState =  new ApiKeyState(env.pdCore.getParametroAutenticazione(pd.getAutenticazione(), pd.getProprietaAutenticazioneList()));
				appId = apiKeyState.appIdSelected;
			}
			
			boolean bothSslAndToken = false;
			List<IDServizioApplicativoDB> saCompatibili = env.saCore.soggettiServizioApplicativoList(env.idSoggetto.toIDSoggetto(),env.userLogin,tipoAutenticazione, appId,
					ServiziApplicativiCostanti.VALUE_SERVIZI_APPLICATIVI_TIPO_CLIENT,
					bothSslAndToken, pd.getGestioneToken()!=null ? pd.getGestioneToken().getPolicy() : null);
			if (!BaseHelper.findFirst(saCompatibili, s -> s.getId().equals(sa.getId())).isPresent()) {
				throw FaultCode.RICHIESTA_NON_VALIDA.toException("Il tipo di credenziali dell'Applicativo non sono compatibili con l'autenticazione impostata nella fruizione selezionata");
			}

			if ( BaseHelper.findFirst(
						pd.getServizioApplicativoList(),
						s -> s.getNome().equals(sa.getNome())
						).isPresent()
				) {
				throw FaultCode.CONFLITTO.toException("Servizio Applicativo già associato");
			}
					
			env.requestWrapper.overrideParameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID, pd.getId().toString());
			env.requestWrapper.overrideParameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID_SOGGETTO, env.idSoggetto.getId().toString());
			env.requestWrapper.overrideParameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_SERVIZIO_APPLICATIVO, sa.getNome());
			
			if (!env.pdHelper.porteDelegateServizioApplicativoCheckData(TipoOperazione.ADD)) {
				throw FaultCode.RICHIESTA_NON_VALIDA.toException(StringEscapeUtils.unescapeHtml4(env.pd.getMessage()));
			}
			
			
			PortaDelegataServizioApplicativo pdSa = new PortaDelegataServizioApplicativo();
			pdSa.setNome(body.getApplicativo());
			pd.addServizioApplicativo(pdSa);
			
			env.pdCore.performUpdateOperation(env.userLogin, false, pd);
			
			context.getLogger().info("Invocazione completata con successo");
        
     
			// Bug Fix: altrimenti viene generato 204
			context.getServletResponse().setStatus(201);
		}
		catch(jakarta.ws.rs.WebApplicationException e) {
			context.getLogger().error_except404("Invocazione terminata con errore '4xx': %s",e, e.getMessage());
			throw e;
		}
		catch(Throwable e) {
			context.getLogger().error("Invocazione terminata con errore: %s",e, e.getMessage());
			throw FaultCode.ERRORE_INTERNO.toException(e);
		}
    }
    
    /**
     * Aggiunta di applicativi all'elenco degli applicativi token autorizzati
     *
     * Questa operazione consente di aggiungere applicativi all'elenco degli applicativi token autorizzati
     *
     */
	@Override
    public void addFruizioneControlloAccessiAutorizzazioneApplicativiToken(ControlloAccessiAutorizzazioneApplicativo body, String erogatore, String nome, Integer versione, ProfiloEnum profilo, String soggetto, String gruppo, String tipoServizio) {
		IContext context = this.getContext();
		try {
			context.getLogger().info("Invocazione in corso ...");     

			AuthorizationManager.authorize(context, getAuthorizationConfig());
			context.getLogger().debug("Autorizzazione completata con successo");     
                        
			BaseHelper.throwIfNull(body);
			
			final FruizioniConfEnv env = new FruizioniConfEnv(context.getServletRequest(), profilo, soggetto, context, erogatore, nome, versione, gruppo, tipoServizio);		
			final PortaDelegata pd = env.pdCore.getPortaDelegata(env.idPd);
			
			final IDServizioApplicativo idSA = new IDServizioApplicativo();
			idSA.setIdSoggettoProprietario(env.idSoggetto.toIDSoggetto());
			idSA.setNome(body.getApplicativo());
			
			String tokenPolicy = null;
			if(pd.getGestioneToken()==null || pd.getGestioneToken().getPolicy()==null) {
				throw FaultCode.RICHIESTA_NON_VALIDA.toException("L'autorizzazione token per richiedente non è utilizzabile, non risulta abilitata una token policy di validazione");
			}
			tokenPolicy = pd.getGestioneToken().getPolicy();
			if(pd.getAutorizzazioneToken()==null || !StatoFunzionalita.ABILITATO.equals(pd.getAutorizzazioneToken().getAutorizzazioneApplicativi())) {
				throw FaultCode.RICHIESTA_NON_VALIDA.toException("L'autorizzazione token per richiedente non è abilitata");
			}
			
			final ServizioApplicativo sa = BaseHelper.supplyOrNonValida( 
					() -> env.saCore.getServizioApplicativo(idSA),
					"Servizio Applicativo " + idSA.toString()
				);
			
			// Prendo la lista di servizi applicativi associati al soggetto
			final org.openspcoop2.core.config.constants.CredenzialeTipo tipoAutenticazione = org.openspcoop2.core.config.constants.CredenzialeTipo.TOKEN;
			Boolean appId = null;
			boolean bothSslAndToken = false;
			List<IDServizioApplicativoDB> saCompatibili = env.saCore.soggettiServizioApplicativoList(env.idSoggetto.toIDSoggetto(),env.userLogin,tipoAutenticazione, appId,
					ServiziApplicativiCostanti.VALUE_SERVIZI_APPLICATIVI_TIPO_CLIENT,
					bothSslAndToken, tokenPolicy);
			if (!BaseHelper.findFirst(saCompatibili, s -> s.getId().equals(sa.getId())).isPresent()) {
				throw FaultCode.RICHIESTA_NON_VALIDA.toException("Il tipo di credenziali dell'Applicativo non sono compatibili con l'autenticazione impostata nella fruizione selezionata");
			}

			if(pd.getAutorizzazioneToken()==null) {
				pd.setAutorizzazioneToken(new PortaDelegataAutorizzazioneToken());
			}
			if(pd.getAutorizzazioneToken().getServiziApplicativi()==null) {
				pd.getAutorizzazioneToken().setServiziApplicativi(new PortaDelegataAutorizzazioneServiziApplicativi());
			}
			
			if ( BaseHelper.findFirst(
						pd.getAutorizzazioneToken().getServiziApplicativi().getServizioApplicativoList(),
						s -> s.getNome().equals(sa.getNome())
						).isPresent()
				) {
				throw FaultCode.CONFLITTO.toException("Servizio Applicativo già associato");
			}
					
			env.requestWrapper.overrideParameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID, pd.getId().toString());
			env.requestWrapper.overrideParameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID_SOGGETTO, env.idSoggetto.getId().toString());
			env.requestWrapper.overrideParameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_SERVIZIO_APPLICATIVO, sa.getNome());
			
			if (!env.pdHelper.porteDelegateServizioApplicativoCheckData(TipoOperazione.ADD)) {
				throw FaultCode.RICHIESTA_NON_VALIDA.toException(StringEscapeUtils.unescapeHtml4(env.pd.getMessage()));
			}
			
			
			PortaDelegataServizioApplicativo pdSa = new PortaDelegataServizioApplicativo();
			pdSa.setNome(body.getApplicativo());
			pd.getAutorizzazioneToken().getServiziApplicativi().addServizioApplicativo(pdSa);
			
			env.pdCore.performUpdateOperation(env.userLogin, false, pd);
			
			context.getLogger().info("Invocazione completata con successo");
        
     
			// Bug Fix: altrimenti viene generato 204
			context.getServletResponse().setStatus(201);
     
		}
		catch(jakarta.ws.rs.WebApplicationException e) {
			context.getLogger().error("Invocazione terminata con errore '4xx': %s",e, e.getMessage());
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
    public void addFruizioneControlloAccessiAutorizzazioneRuoli(ControlloAccessiAutorizzazioneRuolo body, String erogatore, String nome, Integer versione, ProfiloEnum profilo, String soggetto, String gruppo, String tipoServizio) {
		IContext context = this.getContext();
		try {
			context.getLogger().info("Invocazione in corso ...");     

			AuthorizationManager.authorize(context, getAuthorizationConfig());
			context.getLogger().debug("Autorizzazione completata con successo");     

			final FruizioniConfEnv env = new FruizioniConfEnv(context.getServletRequest(), profilo, soggetto, context, erogatore, nome, versione, gruppo, tipoServizio );		
			final PortaDelegata pd = env.pdCore.getPortaDelegata(env.idPd);
			
			if ( !TipoAutorizzazione.isRolesRequired(pd.getAutorizzazione())) {
				throw FaultCode.RICHIESTA_NON_VALIDA.toException("L'autorizzazione per ruoli non è abilitata");
				
			}
			
			final RuoliCore ruoliCore = new RuoliCore(env.stationCore);
			BaseHelper.supplyOrNonValida( 
					() -> ruoliCore.getRuolo(body.getRuolo())
					, "Ruolo " + body.getRuolo()
				);
			
			if(pd.getRuoli()==null){
				pd.setRuoli(new AutorizzazioneRuoli());
			}
			
			// ================================
			FiltroRicercaRuoli filtroRuoli = new FiltroRicercaRuoli();
			filtroRuoli.setContesto(RuoloContesto.PORTA_DELEGATA);
			filtroRuoli.setTipologia(RuoloTipologia.QUALSIASI);
			if(TipoAutorizzazione.isInternalRolesRequired(pd.getAutorizzazione()) ){
				filtroRuoli.setTipologia(RuoloTipologia.INTERNO);
			}
			else if(TipoAutorizzazione.isExternalRolesRequired(pd.getAutorizzazione()) ){
				filtroRuoli.setTipologia(RuoloTipologia.ESTERNO);
			}
			
			List<String> ruoliAmmessi = env.stationCore.getAllRuoli(filtroRuoli);
			
			if ( !ruoliAmmessi.contains(body.getRuolo())) {
				throw FaultCode.RICHIESTA_NON_VALIDA.toException("Il ruolo " + body.getRuolo() + "non è tra i ruoli ammissibili per il gruppo"); 
			}
			
			final List<String> ruoliPresenti = pd.getRuoli().getRuoloList().stream().map( r -> r.getNome()).collect(Collectors.toList());
			
			if ( BaseHelper.findFirst( ruoliPresenti, r -> r.equals(body.getRuolo())).isPresent()) {
				throw FaultCode.CONFLITTO.toException("Il ruolo " + body.getRuolo() + " è già associato al gruppo scelto");
			}
			
			if (!env.paHelper.ruoloCheckData(TipoOperazione.ADD, body.getRuolo(), ruoliPresenti)) {
				throw FaultCode.RICHIESTA_NON_VALIDA.toException(StringEscapeUtils.unescapeHtml4(env.pd.getMessage()));
			} 
			// ================================ CHECK RUOLI
			
			Ruolo ruolo = new Ruolo();
			ruolo.setNome(body.getRuolo());
			pd.getRuoli().addRuolo(ruolo);

			env.pdCore.performUpdateOperation(env.userLogin, false, pd);

			context.getLogger().info("Invocazione completata con successo");
        
			// Bug Fix: altrimenti viene generato 204
			context.getServletResponse().setStatus(201);
     
		}
		catch(jakarta.ws.rs.WebApplicationException e) {
			context.getLogger().error_except404("Invocazione terminata con errore '4xx': %s",e, e.getMessage());
			throw e;
		}
		catch(Throwable e) {
			context.getLogger().error("Invocazione terminata con errore: %s",e, e.getMessage());
			throw FaultCode.ERRORE_INTERNO.toException(e);
		}
    }

    /**
     * Aggiunta di ruoli all'elenco dei ruoli token autorizzati
     *
     * Questa operazione consente di aggiungere ruoli all'elenco dei ruoli token autorizzati
     *
     */
	@Override
    public void addFruizioneControlloAccessiAutorizzazioneRuoliToken(ControlloAccessiAutorizzazioneRuolo body, String erogatore, String nome, Integer versione, ProfiloEnum profilo, String soggetto, String gruppo, String tipoServizio) {
		IContext context = this.getContext();
		try {
			context.getLogger().info("Invocazione in corso ...");     

			AuthorizationManager.authorize(context, getAuthorizationConfig());
			context.getLogger().debug("Autorizzazione completata con successo");     
                        
			final FruizioniConfEnv env = new FruizioniConfEnv(context.getServletRequest(), profilo, soggetto, context, erogatore, nome, versione, gruppo, tipoServizio );		
			final PortaDelegata pd = env.pdCore.getPortaDelegata(env.idPd);
			
			if(pd.getAutorizzazioneToken()==null || !StatoFunzionalita.ABILITATO.equals(pd.getAutorizzazioneToken().getAutorizzazioneRuoli())) {
				throw FaultCode.RICHIESTA_NON_VALIDA.toException("L'autorizzazione token per ruoli non è abilitata");
			}
			
			final RuoliCore ruoliCore = new RuoliCore(env.stationCore);
			BaseHelper.supplyOrNonValida( 
					() -> ruoliCore.getRuolo(body.getRuolo())
					, "Ruolo " + body.getRuolo()
				);
			
			if(pd.getAutorizzazioneToken()==null) {
				pd.setAutorizzazioneToken(new PortaDelegataAutorizzazioneToken());
			}
			if(pd.getAutorizzazioneToken().getRuoli()==null) {
				pd.getAutorizzazioneToken().setRuoli(new AutorizzazioneRuoli());
			}
			
			// ================================
			FiltroRicercaRuoli filtroRuoli = new FiltroRicercaRuoli();
			filtroRuoli.setContesto(RuoloContesto.PORTA_DELEGATA);
			filtroRuoli.setTipologia(RuoloTipologia.QUALSIASI);
			if(TipoAutorizzazione.isInternalRolesRequired(pd.getAutorizzazione()) ){
				filtroRuoli.setTipologia(RuoloTipologia.INTERNO);
			}
			else if(TipoAutorizzazione.isExternalRolesRequired(pd.getAutorizzazione()) ){
				filtroRuoli.setTipologia(RuoloTipologia.ESTERNO);
			}
			
			List<String> ruoliAmmessi = env.stationCore.getAllRuoli(filtroRuoli);
			
			if ( !ruoliAmmessi.contains(body.getRuolo())) {
				throw FaultCode.RICHIESTA_NON_VALIDA.toException("Il ruolo " + body.getRuolo() + "non è tra i ruoli ammissibili per il gruppo"); 
			}
			
			final List<String> ruoliPresenti = pd.getAutorizzazioneToken().getRuoli().getRuoloList().stream().map( r -> r.getNome()).collect(Collectors.toList());
			
			if ( BaseHelper.findFirst( ruoliPresenti, r -> r.equals(body.getRuolo())).isPresent()) {
				throw FaultCode.CONFLITTO.toException("Il ruolo " + body.getRuolo() + " è già associato al gruppo scelto");
			}
			
			if (!env.paHelper.ruoloCheckData(TipoOperazione.ADD, body.getRuolo(), ruoliPresenti)) {
				throw FaultCode.RICHIESTA_NON_VALIDA.toException(StringEscapeUtils.unescapeHtml4(env.pd.getMessage()));
			} 
			// ================================ CHECK RUOLI
			
			Ruolo ruolo = new Ruolo();
			ruolo.setNome(body.getRuolo());
			pd.getAutorizzazioneToken().getRuoli().addRuolo(ruolo);

			env.pdCore.performUpdateOperation(env.userLogin, false, pd);

			context.getLogger().info("Invocazione completata con successo");
        
			// Bug Fix: altrimenti viene generato 204
			context.getServletResponse().setStatus(201);
		}
		catch(jakarta.ws.rs.WebApplicationException e) {
			context.getLogger().error("Invocazione terminata con errore '4xx': %s",e, e.getMessage());
			throw e;
		}
		catch(Throwable e) {
			context.getLogger().error("Invocazione terminata con errore: %s",e, e.getMessage());
			throw FaultCode.ERRORE_INTERNO.toException(e);
		}
    }
    
   /**
     * Aggiunta di una proprietà di configurazione
     *
     * Questa operazione consente di registrare una proprietà di configurazione
     *
     */
	@Override
    public void addFruizioneProprieta(org.openspcoop2.core.config.rs.server.model.ProprietaOpzioneCifratura body, String erogatore, String nome, Integer versione, ProfiloEnum profilo, String soggetto, String gruppo, String tipoServizio) {
		IContext context = this.getContext();
		try {
			context.getLogger().info("Invocazione in corso ...");     

			AuthorizationManager.authorize(context, getAuthorizationConfig());
			context.getLogger().debug("Autorizzazione completata con successo");     
            
			BaseHelper.throwIfNull(body);	
			
			final FruizioniConfEnv env = new FruizioniConfEnv(context.getServletRequest(), profilo, soggetto, context, erogatore, nome, versione, gruppo, tipoServizio );		
			if(env.pdCore==null) {
				throw new CoreException("PdCore not initialized");
			}
			final PortaDelegata pd = env.pdCore.getPortaDelegata(env.idPd);
			
			if ((body.getNome().indexOf(" ") != -1) || (body.getValore().indexOf(" ") != -1)) {
				throw FaultCode.RICHIESTA_NON_VALIDA.toException(CostantiControlStation.MESSAGGIO_ERRORE_NON_INSERIRE_SPAZI_NEI_CAMPI_DI_TESTO);
			}
			
			if(pd.getProprietaList()!=null && !pd.getProprietaList().isEmpty()) {
				for (Proprieta p : pd.getProprietaList()) {
					if(p.getNome().equals(body.getNome())) {
						throw FaultCode.CONFLITTO.toException("Proprietà " + body.getNome() + " già assegnata alla configurazione");
					}
				}
			}
			
			Proprieta p = new Proprieta();
			p.setNome(body.getNome());
			if(env.pdCore!=null && env.pdCore.getDriverBYOKUtilities()!=null && 
					body.isEncrypted()!=null && body.isEncrypted().booleanValue()) {
				p.setValore(env.pdCore.getDriverBYOKUtilities().wrap(body.getValore()));
			}
			else {
				if(body.getValore().length()>255) {
					throw FaultCode.RICHIESTA_NON_VALIDA.toException(CostantiControlStation.MESSAGGIO_ERRORE_VALORE_PROPRIETA_255);
				}
				p.setValore(body.getValore());
			}
			pd.addProprieta(p);
			
			env.pdCore.performUpdateOperation(env.userLogin, false, pd);
		
			context.getLogger().info("Invocazione completata con successo");
     
			// Bug Fix: altrimenti viene generato 204
			context.getServletResponse().setStatus(201);
		}
		catch(jakarta.ws.rs.WebApplicationException e) {
			context.getLogger().error("Invocazione terminata con errore '4xx': %s",e, e.getMessage());
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
    public void addFruizioneControlloAccessiAutorizzazioneScope(ControlloAccessiAutorizzazioneScope body, String erogatore, String nome, Integer versione, ProfiloEnum profilo, String soggetto, String gruppo, String tipoServizio) {
		IContext context = this.getContext();
		try {
			context.getLogger().info("Invocazione in corso ...");     

			AuthorizationManager.authorize(context, getAuthorizationConfig());
			context.getLogger().debug("Autorizzazione completata con successo");     

			final FruizioniConfEnv env = new FruizioniConfEnv(context.getServletRequest(), profilo, soggetto, context, erogatore, nome, versione, gruppo, tipoServizio );		
			final PortaDelegata pd = env.pdCore.getPortaDelegata(env.idPd);
			
			if(pd.getScope()==null) {
				throw FaultCode.RICHIESTA_NON_VALIDA.toException("L'autorizzazione per scope non è abilitata");
			}
			
			// ==================
			FiltroRicercaScope filtroScope = new FiltroRicercaScope();
			filtroScope.setContesto(ScopeContesto.PORTA_APPLICATIVA);
			filtroScope.setTipologia("");
			
			final List<String> scopeAmmessi = env.stationCore.getAllScope(filtroScope);
			final List<String> scopePresenti = pd.getScope().getScopeList().stream().map(Scope::getNome).collect(Collectors.toList());
			
			if ( !scopeAmmessi.contains(body.getScope())) {
				throw FaultCode.RICHIESTA_NON_VALIDA.toException("Scope " + body.getScope() + " non presente fra gli scope ammissibili."); 
			}
			
			if ( scopePresenti.contains(body.getScope()) ) {
				throw FaultCode.CONFLITTO.toException("Scope " + body.getScope() + " già assegnato al gruppo");
			}
			
			if (!env.paHelper.scopeCheckData(TipoOperazione.ADD, body.getScope(), scopePresenti )) {
				throw FaultCode.RICHIESTA_NON_VALIDA.toException(StringEscapeUtils.unescapeHtml4(env.pd.getMessage()));
				
			}
			// ================= CHECK SCOPE
			
			Scope scope = new Scope();
			scope.setNome(body.getScope());
			pd.getScope().addScope(scope);
			
			env.pdCore.performUpdateOperation(env.userLogin, false, pd);
		
			context.getLogger().info("Invocazione completata con successo");
     
			// Bug Fix: altrimenti viene generato 204
			context.getServletResponse().setStatus(201);
			
		}
		catch(jakarta.ws.rs.WebApplicationException e) {
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
    public void addFruizioneRateLimitingPolicy(RateLimitingPolicyFruizione body, String erogatore, String nome, Integer versione, ProfiloEnum profilo, String soggetto, String gruppo, String tipoServizio) {
		IContext context = this.getContext();
		try {
			context.getLogger().info("Invocazione in corso ...");     

			AuthorizationManager.authorize(context, getAuthorizationConfig());
			context.getLogger().debug("Autorizzazione completata con successo");     
                        
			BaseHelper.throwIfNull(body);

			final FruizioniConfEnv env = new FruizioniConfEnv(context.getServletRequest(), profilo, soggetto, context, erogatore, nome, versione, gruppo, tipoServizio );		
			final PortaDelegata pd = env.pdCore.getPortaDelegata(env.idPd);

			final IDSoggetto idErogatore = new IDSoggetto(env.tipo_soggetto, erogatore);
			final AccordoServizioParteSpecifica asps = BaseHelper.supplyOrNotFound(() -> ErogazioniApiHelper
					.getServizioIfFruizione(tipoServizio, nome, versione, idErogatore, env.idSoggetto.toIDSoggetto(), env),
					"Fruizione");
			final AccordoServizioParteComuneSintetico apc = env.apcCore.getAccordoServizioSintetico(asps.getIdAccordo());
			
			final RuoloPolicy ruoloPorta = RuoloPolicy.DELEGATA;
			final String nomePorta = pd.getNome();
						
			AttivazionePolicy policy = new AttivazionePolicy();
			policy.setFiltro(new AttivazionePolicyFiltro());
			policy.setGroupBy(new AttivazionePolicyRaggruppamento());
			
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
			String serialId = env.confCore.getNextPolicyInstanceSerialId(infoPolicy.getIdPolicy());
			policy.setIdActivePolicy(ControlloTrafficoDriverUtils.buildIdActivePolicy(infoPolicy.getIdPolicy(), serialId));
	
			ErogazioniApiHelper.override(infoPolicy.getTipoRisorsa(), body, env.protocolFactory.getProtocol(),  env.idSoggetto.toIDSoggetto(), env.requestWrapper);
			// Dati Attivazione
			String errorAttivazione = env.confHelper.readDatiAttivazionePolicyFromHttpParameters(policy, false, TipoOperazione.ADD, infoPolicy);
			if ( !StringUtils.isEmpty(errorAttivazione) ) {
				throw FaultCode.RICHIESTA_NON_VALIDA.toException(StringEscapeUtils.unescapeHtml4(errorAttivazione));
			}
			
			policy.getFiltro().setEnabled(true);
			policy.getFiltro().setProtocollo(env.tipo_protocollo);
			policy.getFiltro().setRuoloPorta(ruoloPorta);
			policy.getFiltro().setNomePorta(nomePorta);

			policy.getFiltro().setTipoFruitore(pd.getTipoSoggettoProprietario());
			policy.getFiltro().setNomeFruitore(pd.getNomeSoggettoProprietario());

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
				throw FaultCode.CONFLITTO.toException(StringEscapeUtils.unescapeHtml4(existsMessage.toString()));
			}
			// Qui viene sollevata eccezione se il check non viene superato
			FruizioniConfigurazioneHelper.attivazionePolicyCheckData(TipoOperazione.ADD, pd, policy,infoPolicy, env, env.apcCore.toMessageServiceBinding(apc.getServiceBinding()), modalita);
			
			// aggiorno prossima posizione nella policy
			ConfigurazioneUtilities.updatePosizioneAttivazionePolicy(env.confCore, infoPolicy, policy, ruoloPorta, nomePorta);
			
			env.confCore.performCreateOperation(env.userLogin, false, policy);
			
			context.getLogger().info("Invocazione completata con successo");
			
			// Bug Fix: altrimenti viene generato 204
			context.getServletResponse().setStatus(201);
		}
		catch(jakarta.ws.rs.WebApplicationException e) {
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
    public void addFruizioneTracciamentoCorrelazioneApplicativaRichiesta(CorrelazioneApplicativaRichiesta body, String erogatore, String nome, Integer versione, ProfiloEnum profilo, String soggetto, String gruppo, String tipoServizio) {
		IContext context = this.getContext();
		try {
			context.getLogger().info("Invocazione in corso ...");     

			AuthorizationManager.authorize(context, getAuthorizationConfig());
			context.getLogger().debug("Autorizzazione completata con successo");     
			
			BaseHelper.throwIfNull(body);
			
			if ( body.getElemento() == null )
				body.setElemento("");              
                        
			final FruizioniConfEnv env = new FruizioniConfEnv(context.getServletRequest(), profilo, soggetto, context, erogatore, nome, versione, gruppo, tipoServizio );		
			final PortaDelegata pd = env.pdCore.getPortaDelegata(env.idPd);
			final Long idPorta = pd.getId();
			
			final IDSoggetto idErogatore = new IDSoggetto(env.tipo_soggetto, erogatore);
			final AccordoServizioParteSpecifica asps = BaseHelper.supplyOrNotFound(() -> ErogazioniApiHelper
					.getServizioIfFruizione(tipoServizio, nome, versione, idErogatore, env.idSoggetto.toIDSoggetto(), env),
					"Fruizione");
			final AccordoServizioParteComuneSintetico apc = env.apcCore.getAccordoServizioSintetico(asps.getIdAccordo());
			ServiceBinding serviceBinding = env.apcCore.toMessageServiceBinding(apc.getServiceBinding());
			
			StringBuilder existsMessage = new StringBuilder();
			if ( ConsoleUtilities.alreadyExistsCorrelazioneApplicativaRichiesta(env.pdCore, idPorta, body.getElemento(), 0, existsMessage)) {
				throw FaultCode.CONFLITTO.toException(StringEscapeUtils.unescapeHtml4(existsMessage.toString()));
			}

			
			if ( !correlazioneApplicativaRichiestaCheckData(TipoOperazione.ADD, env.requestWrapper, env.paHelper, true, body, idPorta, null,
					serviceBinding) ) {
				throw FaultCode.RICHIESTA_NON_VALIDA.toException(StringEscapeUtils.unescapeHtml4(env.pd.getMessage()));
			}
									
			CorrelazioneApplicativa ca = pd.getCorrelazioneApplicativa();
			if (ca == null) {
				ca = new CorrelazioneApplicativa();
			}
			ca.addElemento(convert(body));
			pd.setCorrelazioneApplicativa(ca);
			
			env.pdCore.performUpdateOperation(env.userLogin, false, pd);
        
			context.getLogger().info("Invocazione completata con successo");
        
			// Bug Fix: altrimenti viene generato 204
			context.getServletResponse().setStatus(201);
		}
		catch(jakarta.ws.rs.WebApplicationException e) {
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
    public void addFruizioneTracciamentoCorrelazioneApplicativaRisposta(CorrelazioneApplicativaRisposta body, String erogatore, String nome, Integer versione, ProfiloEnum profilo, String soggetto, String gruppo, String tipoServizio) {
		IContext context = this.getContext();
		try {
			context.getLogger().info("Invocazione in corso ...");     

			AuthorizationManager.authorize(context, getAuthorizationConfig());
			context.getLogger().debug("Autorizzazione completata con successo");
			
			BaseHelper.throwIfNull(body);
			
			if ( body.getElemento() == null )
				body.setElemento("");              
                        
			final FruizioniConfEnv env = new FruizioniConfEnv(context.getServletRequest(), profilo, soggetto, context, erogatore, nome, versione, gruppo, tipoServizio );		
			final PortaDelegata pd = env.pdCore.getPortaDelegata(env.idPd);
			final Long idPorta = pd.getId();
			
			final IDSoggetto idErogatore = new IDSoggetto(env.tipo_soggetto, erogatore);
			final AccordoServizioParteSpecifica asps = BaseHelper.supplyOrNotFound(() -> ErogazioniApiHelper
					.getServizioIfFruizione(tipoServizio, nome, versione, idErogatore, env.idSoggetto.toIDSoggetto(), env),
					"Fruizione");
			final AccordoServizioParteComuneSintetico apc = env.apcCore.getAccordoServizioSintetico(asps.getIdAccordo());
			ServiceBinding serviceBinding = env.apcCore.toMessageServiceBinding(apc.getServiceBinding());
			
			StringBuilder existsMessage = new StringBuilder();
			if ( ConsoleUtilities.alreadyExistsCorrelazioneApplicativaRisposta(env.pdCore, idPorta, body.getElemento(), 0, existsMessage)) {
				throw FaultCode.CONFLITTO.toException(StringEscapeUtils.unescapeHtml4(existsMessage.toString()));
			}
			
			if ( !correlazioneApplicativaRispostaCheckData(TipoOperazione.ADD, env.requestWrapper, env.pdHelper, true, body, idPorta, null,
					serviceBinding)) {
				throw FaultCode.RICHIESTA_NON_VALIDA.toException(StringEscapeUtils.unescapeHtml4(env.pd.getMessage()));
			}
                       			
			if ( pd.getCorrelazioneApplicativaRisposta() == null)
				pd.setCorrelazioneApplicativaRisposta(new org.openspcoop2.core.config.CorrelazioneApplicativaRisposta());
			
			pd.getCorrelazioneApplicativaRisposta().addElemento(convert(body));
			
			
			env.pdCore.performUpdateOperation(env.userLogin, false, pd);
			context.getLogger().info("Invocazione completata con successo");
        
			// Bug Fix: altrimenti viene generato 204
			context.getServletResponse().setStatus(201);
		}
		catch(jakarta.ws.rs.WebApplicationException e) {
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
    public void deleteFruizioneControlloAccessiAutorizzazioneApplicativi(String erogatore, String nome, Integer versione, String applicativoAutorizzato, ProfiloEnum profilo, String soggetto, String gruppo, String tipoServizio) {
		IContext context = this.getContext();
		try {
			context.getLogger().info("Invocazione in corso ...");     

			AuthorizationManager.authorize(context, getAuthorizationConfig());
			context.getLogger().debug("Autorizzazione completata con successo");       

			final FruizioniConfEnv env = new FruizioniConfEnv(context.getServletRequest(), profilo, soggetto, context, erogatore, nome, versione, gruppo, tipoServizio );		
			final PortaDelegata pd = env.pdCore.getPortaDelegata(env.idPd);
			
			PortaDelegataServizioApplicativo to_remove = BaseHelper.findAndRemoveFirst(pd.getServizioApplicativoList(), sa -> sa.getNome().equals(applicativoAutorizzato));
			
			if (env.delete_404 && to_remove == null) {
				throw FaultCode.NOT_FOUND.toException("Nessun Applicativo " + applicativoAutorizzato + " è associato al gruppo scelto"); 
			} else if ( to_remove != null ) {
			
				env.pdCore.performUpdateOperation(env.userLogin, false, pd);
			}
			context.getLogger().info("Invocazione completata con successo");
        
     
		}
		catch(jakarta.ws.rs.WebApplicationException e) {
			context.getLogger().error_except404("Invocazione terminata con errore '4xx': %s",e, e.getMessage());
			throw e;
		}
		catch(Throwable e) {
			context.getLogger().error("Invocazione terminata con errore: %s",e, e.getMessage());
			throw FaultCode.ERRORE_INTERNO.toException(e);
		}
    }
    
   /**
     * Elimina applicativi dall'elenco degli applicativi token autorizzati
     *
     * Questa operazione consente di eliminare applicativi dall'elenco degli applicativi token autorizzati
     *
     */
	@Override
    public void deleteFruizioneControlloAccessiAutorizzazioneApplicativiToken(String erogatore, String nome, Integer versione, String applicativoAutorizzato, ProfiloEnum profilo, String soggetto, String gruppo, String tipoServizio) {
		IContext context = this.getContext();
		try {
			context.getLogger().info("Invocazione in corso ...");     

			AuthorizationManager.authorize(context, getAuthorizationConfig());
			context.getLogger().debug("Autorizzazione completata con successo");     
                        
			final FruizioniConfEnv env = new FruizioniConfEnv(context.getServletRequest(), profilo, soggetto, context, erogatore, nome, versione, gruppo, tipoServizio );		
			final PortaDelegata pd = env.pdCore.getPortaDelegata(env.idPd);
			
			if(pd.getAutorizzazioneToken()==null) {
				pd.setAutorizzazioneToken(new PortaDelegataAutorizzazioneToken());
			}
			if(pd.getAutorizzazioneToken().getServiziApplicativi()==null) {
				pd.getAutorizzazioneToken().setServiziApplicativi(new PortaDelegataAutorizzazioneServiziApplicativi());
			}
			
			PortaDelegataServizioApplicativo to_remove = BaseHelper.findAndRemoveFirst(pd.getAutorizzazioneToken().getServiziApplicativi().getServizioApplicativoList(), sa -> sa.getNome().equals(applicativoAutorizzato));
			
			if (env.delete_404 && to_remove == null) {
				throw FaultCode.NOT_FOUND.toException("Nessun Applicativo " + applicativoAutorizzato + " è associato al gruppo scelto"); 
			} else if ( to_remove != null ) {
			
				env.pdCore.performUpdateOperation(env.userLogin, false, pd);
			}
			context.getLogger().info("Invocazione completata con successo");
     
		}
		catch(jakarta.ws.rs.WebApplicationException e) {
			context.getLogger().error("Invocazione terminata con errore '4xx': %s",e, e.getMessage());
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
    public void deleteFruizioneControlloAccessiAutorizzazioneRuoli(String erogatore, String nome, Integer versione, String ruoloAutorizzato, ProfiloEnum profilo, String soggetto, String gruppo, String tipoServizio) {
		IContext context = this.getContext();
		try {
			context.getLogger().info("Invocazione in corso ...");     

			AuthorizationManager.authorize(context, getAuthorizationConfig());
			context.getLogger().debug("Autorizzazione completata con successo");     

			final FruizioniConfEnv env = new FruizioniConfEnv(context.getServletRequest(), profilo, soggetto, context, erogatore, nome, versione, gruppo, tipoServizio );		
			final PortaDelegata pd = env.pdCore.getPortaDelegata(env.idPd);
			
			if (pd.getRuoli() == null)	pd.setRuoli(new AutorizzazioneRuoli());
			
			Ruolo to_remove = BaseHelper.findAndRemoveFirst(pd.getRuoli().getRuoloList(), r -> r.getNome().equals(ruoloAutorizzato));
			
			if (env.delete_404 && to_remove == null) {
				throw FaultCode.NOT_FOUND.toException("Nessun Ruolo " + ruoloAutorizzato + " è associato al gruppo scelto"); 
			} else if ( to_remove != null ) {
			
				env.pdCore.performUpdateOperation(env.userLogin, false, pd);
			}
			
			context.getLogger().info("Invocazione completata con successo");     
		}
		catch(jakarta.ws.rs.WebApplicationException e) {
			context.getLogger().error_except404("Invocazione terminata con errore '4xx': %s",e, e.getMessage());
			throw e;
		}
		catch(Throwable e) {
			context.getLogger().error("Invocazione terminata con errore: %s",e, e.getMessage());
			throw FaultCode.ERRORE_INTERNO.toException(e);
		}
    }
    
    /**
     * Elimina ruoli dall'elenco dei ruoli token autorizzati
     *
     * Questa operazione consente di eliminare ruoli dall'elenco dei ruoli token autorizzati
     *
     */
	@Override
    public void deleteFruizioneControlloAccessiAutorizzazioneRuoliToken(String erogatore, String nome, Integer versione, String ruoloAutorizzato, ProfiloEnum profilo, String soggetto, String gruppo, String tipoServizio) {
		IContext context = this.getContext();
		try {
			context.getLogger().info("Invocazione in corso ...");     

			AuthorizationManager.authorize(context, getAuthorizationConfig());
			context.getLogger().debug("Autorizzazione completata con successo");     
                        
			final FruizioniConfEnv env = new FruizioniConfEnv(context.getServletRequest(), profilo, soggetto, context, erogatore, nome, versione, gruppo, tipoServizio );		
			final PortaDelegata pd = env.pdCore.getPortaDelegata(env.idPd);
			
			if(pd.getAutorizzazioneToken()==null) {
				pd.setAutorizzazioneToken(new PortaDelegataAutorizzazioneToken());
			}
			if(pd.getAutorizzazioneToken().getRuoli()==null) {
				pd.getAutorizzazioneToken().setRuoli(new AutorizzazioneRuoli());
			}
			
			Ruolo to_remove = BaseHelper.findAndRemoveFirst(pd.getAutorizzazioneToken().getRuoli().getRuoloList(), r -> r.getNome().equals(ruoloAutorizzato));
			
			if (env.delete_404 && to_remove == null) {
				throw FaultCode.NOT_FOUND.toException("Nessun Ruolo " + ruoloAutorizzato + " è associato al gruppo scelto"); 
			} else if ( to_remove != null ) {
			
				env.pdCore.performUpdateOperation(env.userLogin, false, pd);
			}
			
			context.getLogger().info("Invocazione completata con successo");   
		}
		catch(jakarta.ws.rs.WebApplicationException e) {
			context.getLogger().error("Invocazione terminata con errore '4xx': %s",e, e.getMessage());
			throw e;
		}
		catch(Throwable e) {
			context.getLogger().error("Invocazione terminata con errore: %s",e, e.getMessage());
			throw FaultCode.ERRORE_INTERNO.toException(e);
		}
    }

   /**
     * Elimina la proprietà di configurazione dall&#x27;elenco di quelle attivate
     *
     * Questa operazione consente di eliminare la proprietà di configurazione dall&#x27;elenco di quelle attivate
     *
     */
	@Override
    public void deleteFruizioneProprietaConfigurazione(String erogatore, String nome, Integer versione, String proprieta, ProfiloEnum profilo, String soggetto, String gruppo, String tipoServizio) {
		IContext context = this.getContext();
		try {
			context.getLogger().info("Invocazione in corso ...");     

			AuthorizationManager.authorize(context, getAuthorizationConfig());
			context.getLogger().debug("Autorizzazione completata con successo");     
                        
			final FruizioniConfEnv env = new FruizioniConfEnv(context.getServletRequest(), profilo, soggetto, context, erogatore, nome, versione, gruppo, tipoServizio );		
			final PortaDelegata pd = env.pdCore.getPortaDelegata(env.idPd);
			
			Proprieta to_remove = null;
			if(pd.getProprietaList()!=null && !pd.getProprietaList().isEmpty()) {
				to_remove = BaseHelper.findAndRemoveFirst(pd.getProprietaList(), p -> p.getNome().equals(proprieta));
			}
			
			if (env.delete_404 && to_remove == null) {
				throw FaultCode.NOT_FOUND.toException("Nessuna proprietà è presente nella configurazione con nome '"+proprieta+"'"); 
			} else if ( to_remove != null ) {
			
				env.pdCore.performUpdateOperation(env.userLogin, false, pd);
			}
        
			context.getLogger().info("Invocazione completata con successo");        
		}
		catch(jakarta.ws.rs.WebApplicationException e) {
			context.getLogger().error("Invocazione terminata con errore '4xx': %s",e, e.getMessage());
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
    public void deleteFruizioneControlloAccessiAutorizzazioneScope(String erogatore, String nome, Integer versione, String scopeAutorizzato, ProfiloEnum profilo, String soggetto, String gruppo, String tipoServizio) {
		IContext context = this.getContext();
		try {
			context.getLogger().info("Invocazione in corso ...");     

			AuthorizationManager.authorize(context, getAuthorizationConfig());
			context.getLogger().debug("Autorizzazione completata con successo");     

			final FruizioniConfEnv env = new FruizioniConfEnv(context.getServletRequest(), profilo, soggetto, context, erogatore, nome, versione, gruppo, tipoServizio );		
			final PortaDelegata pd = env.pdCore.getPortaDelegata(env.idPd);
			
			if (pd.getScope() == null)	pd.setScope(new AutorizzazioneScope());
			
			Scope to_remove = BaseHelper.findAndRemoveFirst(pd.getScope().getScopeList(), s -> s.getNome().equals(scopeAutorizzato));
			
			if (env.delete_404 && to_remove == null) {
				throw FaultCode.NOT_FOUND.toException("Nessuno scope " + scopeAutorizzato + " è associato al gruppo scelto"); 
			} else if ( to_remove != null ) {
			
				env.pdCore.performUpdateOperation(env.userLogin, false, pd);
			}
			
			context.getLogger().info("Invocazione completata con successo");        
     
		}
		catch(jakarta.ws.rs.WebApplicationException e) {
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
    public void deleteFruizioneRateLimitingPolicy(String erogatore, String nome, Integer versione, String idPolicy, ProfiloEnum profilo, String soggetto, String gruppo, String tipoServizio) {
		IContext context = this.getContext();
		try {
			context.getLogger().info("Invocazione in corso ...");     

			AuthorizationManager.authorize(context, getAuthorizationConfig());
			context.getLogger().debug("Autorizzazione completata con successo");     
                        
			final FruizioniConfEnv env = new FruizioniConfEnv(context.getServletRequest(), profilo, soggetto, context, erogatore, nome, versione, gruppo, tipoServizio );		
			final PortaDelegata pd = env.pdCore.getPortaDelegata(env.idPd);
			
			List<AttivazionePolicy> policies = env.confCore.attivazionePolicyList(null, RuoloPolicy.DELEGATA, pd.getNome());
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
					throw FaultCode.RICHIESTA_NON_VALIDA.toException(StringEscapeUtils.unescapeHtml4(inUsoMessage.toString()));
				}
				
			}
			else if ( env.delete_404 ) {
				throw FaultCode.NOT_FOUND.toException("Nessuna policy di rate limiting con id " + idPolicy );
			}

			context.getLogger().info("Invocazione completata con successo");
		}
		catch(jakarta.ws.rs.WebApplicationException e) {
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
    public void deleteFruizioneTracciamentoCorrelazioneApplicativaRichiesta(String erogatore, String nome, Integer versione, String elemento, ProfiloEnum profilo, String soggetto, String gruppo, String tipoServizio) {
		IContext context = this.getContext();
		try {
			context.getLogger().info("Invocazione in corso ...");     

			AuthorizationManager.authorize(context, getAuthorizationConfig());
			context.getLogger().debug("Autorizzazione completata con successo");     
                         
			final FruizioniConfEnv env = new FruizioniConfEnv(context.getServletRequest(), profilo, soggetto, context, erogatore, nome, versione, gruppo, tipoServizio );		
			final PortaDelegata pd = env.pdCore.getPortaDelegata(env.idPd);
			
			final CorrelazioneApplicativa correlazioneApplicativa = pd.getCorrelazioneApplicativa();
			
			final String searchElemento = elemento.equals("*")
					? ""
				  	: elemento;	
									
			CorrelazioneApplicativaElemento to_del = BaseHelper.evalnull( () -> BaseHelper.findAndRemoveFirst( 
					correlazioneApplicativa.getElementoList(), 
					e -> (e.getNome()==null ? "" : e.getNome()).equals(searchElemento)
				));
			
			if ( to_del != null ) {
				env.pdCore.performUpdateOperation(env.userLogin, false, pd);
			}
			
			else if (env.delete_404) {
				throw FaultCode.NOT_FOUND.toException("Correlazione applicativa per l'elemento " + elemento + " non trovata");
			}
			
			
			context.getLogger().info("Invocazione completata con successo");
        
     
		}
		catch(jakarta.ws.rs.WebApplicationException e) {
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
    public void deleteFruizioneTracciamentoCorrelazioneApplicativaRisposta(String erogatore, String nome, Integer versione, String elemento, ProfiloEnum profilo, String soggetto, String gruppo, String tipoServizio) {
		IContext context = this.getContext();
		try {
			context.getLogger().info("Invocazione in corso ...");     

			AuthorizationManager.authorize(context, getAuthorizationConfig());
			context.getLogger().debug("Autorizzazione completata con successo");     
                        
			final FruizioniConfEnv env = new FruizioniConfEnv(context.getServletRequest(), profilo, soggetto, context, erogatore, nome, versione, gruppo, tipoServizio );		
			final PortaDelegata pd = env.pdCore.getPortaDelegata(env.idPd);
			
			org.openspcoop2.core.config.CorrelazioneApplicativaRisposta correlazioneApplicativa = pd.getCorrelazioneApplicativaRisposta();
			
			final String searchElemento = elemento.equals("*")
					? ""
				  	: elemento;	
									
			CorrelazioneApplicativaRispostaElemento to_del = BaseHelper.evalnull( () -> BaseHelper.findAndRemoveFirst( 
					correlazioneApplicativa.getElementoList(), 
					e -> (e.getNome()==null ? "" : e.getNome()).equals(searchElemento)
				));
			
			if ( to_del != null ) {
				env.pdCore.performUpdateOperation(env.userLogin, false, pd);
			}
			
			else if (env.delete_404) {
				throw FaultCode.NOT_FOUND.toException("Correlazione applicativa per l'elemento " + elemento + " non trovata");
			}        
			context.getLogger().info("Invocazione completata con successo");
        
     
		}
		catch(jakarta.ws.rs.WebApplicationException e) {
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
    public byte[] downloadFruizioneControlloAccessiAutorizzazioneXacmlPolicy(String erogatore, String nome, Integer versione, ProfiloEnum profilo, String soggetto, String gruppo, String tipoServizio) {
		IContext context = this.getContext();
		try {
			context.getLogger().info("Invocazione in corso ...");     

			AuthorizationManager.authorize(context, getAuthorizationConfig());
			context.getLogger().debug("Autorizzazione completata con successo");
			
			final FruizioniConfEnv env = new FruizioniConfEnv(context.getServletRequest(), profilo, soggetto, context, erogatore, nome, versione, gruppo, tipoServizio );		
			final PortaDelegata pd = env.pdCore.getPortaDelegata(env.idPd);
			
			if (pd.getXacmlPolicy() == null)
				throw FaultCode.NOT_FOUND.toException("Xacml policy non assegnata al gruppo scelto");
                       
			context.getLogger().info("Invocazione completata con successo");
			return pd.getXacmlPolicy().getBytes();
     
		}
		catch(jakarta.ws.rs.WebApplicationException e) {
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
    public ListaRateLimitingPolicy findAllFruizioneRateLimitingPolicies(String erogatore, String nome, Integer versione, ProfiloEnum profilo, String soggetto, String gruppo, String tipoServizio, String q, Integer limit, Integer offset, RateLimitingCriteriMetricaEnum metrica) {
		IContext context = this.getContext();
		try {
			context.getLogger().info("Invocazione in corso ...");     

			AuthorizationManager.authorize(context, getAuthorizationConfig());
			context.getLogger().debug("Autorizzazione completata con successo");     
                        
			final FruizioniConfEnv env = new FruizioniConfEnv(context.getServletRequest(), profilo, soggetto, context, erogatore, nome, versione, gruppo, tipoServizio );		
			final PortaDelegata pd = env.pdCore.getPortaDelegata(env.idPd);
			
			final int idLista = Liste.CONFIGURAZIONE_CONTROLLO_TRAFFICO_ATTIVAZIONE_POLICY;
			final ConsoleSearch ricerca = Helper.setupRicercaPaginata(q, limit, offset, idLista, env.idSoggetto.toIDSoggetto(), env.tipo_protocollo);
			if(metrica!=null) {
				String risorsa = ErogazioniApiHelper.getDataElementModalitaRisorsa(metrica);
				ricerca.addFilter(idLista, Filtri.FILTRO_TIPO_RISORSA_POLICY, risorsa);
			}
			List<AttivazionePolicy> policies = env.confCore.attivazionePolicyList(ricerca, RuoloPolicy.DELEGATA, pd.getNome());

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
		catch(jakarta.ws.rs.WebApplicationException e) {
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
    public ListaCorrelazioneApplicativaRichiesta findAllFruizioneTracciamentoCorrelazioneApplicativaRichiesta(String erogatore, String nome, Integer versione, ProfiloEnum profilo, String soggetto, String gruppo, String tipoServizio, String q, Integer limit, Integer offset) {
		IContext context = this.getContext();
		try {
			context.getLogger().info("Invocazione in corso ...");     

			AuthorizationManager.authorize(context, getAuthorizationConfig());
			context.getLogger().debug("Autorizzazione completata con successo");     
                        
			final FruizioniConfEnv env = new FruizioniConfEnv(context.getServletRequest(), profilo, soggetto, context, erogatore, nome, versione, gruppo, tipoServizio );		
			final PortaDelegata pd = env.pdCore.getPortaDelegata(env.idPd);
			
			final int idLista = Liste.PORTE_DELEGATE_CORRELAZIONE_APPLICATIVA;
			final ConsoleSearch ricerca = Helper.setupRicercaPaginata(q, limit, offset, idLista, env.idSoggetto.toIDSoggetto(), env.tipo_protocollo);
			
			List<CorrelazioneApplicativaElemento> lista = env.pdCore.porteDelegateCorrelazioneApplicativaList(pd.getId().intValue(), ricerca);

			if ( env.findall_404 && lista.isEmpty() ) {
				throw FaultCode.NOT_FOUND.toException("Nessuna regola di correlazione applicativa associata");
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
		catch(jakarta.ws.rs.WebApplicationException e) {
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
    public ListaCorrelazioneApplicativaRisposta findAllFruizioneTracciamentoCorrelazioneApplicativaRisposta(String erogatore, String nome, Integer versione, ProfiloEnum profilo, String soggetto, String gruppo, String tipoServizio, String q, Integer limit, Integer offset) {
		IContext context = this.getContext();
		try {
			context.getLogger().info("Invocazione in corso ...");     

			AuthorizationManager.authorize(context, getAuthorizationConfig());
			context.getLogger().debug("Autorizzazione completata con successo");     
       
			final FruizioniConfEnv env = new FruizioniConfEnv(context.getServletRequest(), profilo, soggetto, context, erogatore, nome, versione, gruppo, tipoServizio );		
			final PortaDelegata pd = env.pdCore.getPortaDelegata(env.idPd);
			
			final int idLista = Liste.PORTE_DELEGATE_CORRELAZIONE_APPLICATIVA_RISPOSTA;
			final ConsoleSearch ricerca = Helper.setupRicercaPaginata(q, limit, offset, idLista, env.idSoggetto.toIDSoggetto(), env.tipo_protocollo);
			
			List<CorrelazioneApplicativaRispostaElemento> lista = env.pdCore.porteDelegateCorrelazioneApplicativaRispostaList(pd.getId().intValue(), ricerca);

			if ( env.findall_404 && lista.isEmpty() ) {
				throw FaultCode.NOT_FOUND.toException("Nessuna regola di correlazione applicativa della risposta associata");
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
					);			
				item.setIdentificazioneTipo(CorrelazioneApplicativaRispostaEnum.valueOf(c.getIdentificazione().name()));
				ret.addItemsItem(item);
			});        
			
			context.getLogger().info("Invocazione completata con successo");
        	return ret;
		     
		}
		catch(jakarta.ws.rs.WebApplicationException e) {
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
    public CachingRisposta getFruizioneCachingRisposta(String erogatore, String nome, Integer versione, ProfiloEnum profilo, String soggetto, String gruppo, String tipoServizio) {
		IContext context = this.getContext();
		try {
			context.getLogger().info("Invocazione in corso ...");     

			AuthorizationManager.authorize(context, getAuthorizationConfig());
			context.getLogger().debug("Autorizzazione completata con successo");     
                       
			final FruizioniConfEnv env = new FruizioniConfEnv(context.getServletRequest(), profilo, soggetto, context, erogatore, nome, versione, gruppo, tipoServizio );		
			final PortaDelegata pd = env.pdCore.getPortaDelegata(env.idPd);

			CachingRisposta ret = ErogazioniApiHelper.buildCachingRisposta(pd.getResponseCaching());
			context.getLogger().info("Invocazione completata con successo");
			return ret;
     
		}
		catch(jakarta.ws.rs.WebApplicationException e) {
			context.getLogger().error_except404("Invocazione terminata con errore '4xx': %s",e, e.getMessage());
			throw e;
		}
		catch(Throwable e) {
			context.getLogger().error("Invocazione terminata con errore: %s",e, e.getMessage());
			throw FaultCode.ERRORE_INTERNO.toException(e);
		}
    }
   
    /**
     * Restituisce il canale associato alla fruizione
     *
     * Questa operazione consente di ottenere il canale associato alla fruizione
     *
     */
	@Override
    public ApiCanale getFruizioneCanale(String erogatore, String nome, Integer versione, ProfiloEnum profilo, String soggetto, String tipoServizio) {
		IContext context = this.getContext();
		try {
			context.getLogger().info("Invocazione in corso ...");     

			AuthorizationManager.authorize(context, getAuthorizationConfig());
			context.getLogger().debug("Autorizzazione completata con successo");     
                        
			final ErogazioniEnv env = new ErogazioniEnv(context.getServletRequest(), profilo, soggetto, context);
			final IDSoggetto idErogatore = new IDSoggetto(env.tipo_soggetto, erogatore);
			final AccordoServizioParteSpecifica asps = BaseHelper.supplyOrNotFound( 
					() -> ErogazioniApiHelper.getServizioIfFruizione(tipoServizio, nome, versione, idErogatore, env.idSoggetto.toIDSoggetto(), env),
					"Fruizione"
				);
			
			final IdServizio idAsps = new IdServizio(env.idServizioFactory.getIDServizioFromAccordo(asps), asps.getId());
			final IDPortaDelegata idPd =  ErogazioniApiHelper.getIDGruppoPDDefault(env.idSoggetto.toIDSoggetto(), idAsps, env.apsCore);
			final PortaDelegata pd = env.pdCore.getPortaDelegata(idPd);
			
			final AccordoServizioParteComuneSintetico apc = env.apcCore.getAccordoServizioSintetico(asps.getIdAccordo());
			ApiCanale canale = ErogazioniApiHelper.toApiCanale(env, pd, apc, true);
			
			context.getLogger().info("Invocazione completata con successo");
			return canale;
     
		}
		catch(jakarta.ws.rs.WebApplicationException e) {
			context.getLogger().error("Invocazione terminata con errore '4xx': %s",e, e.getMessage());
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
    public ControlloAccessiAutenticazione getFruizioneControlloAccessiAutenticazione(String erogatore, String nome, Integer versione, ProfiloEnum profilo, String soggetto, String gruppo, String tipoServizio) {
		IContext context = this.getContext();
		try {
			context.getLogger().info("Invocazione in corso ...");     

			AuthorizationManager.authorize(context, getAuthorizationConfig());
			context.getLogger().debug("Autorizzazione completata con successo");
			
			final FruizioniConfEnv env = new FruizioniConfEnv(context.getServletRequest(), profilo, soggetto, context, erogatore, nome, versione, gruppo, tipoServizio );		
			final PortaDelegata pd = env.pdCore.getPortaDelegata(env.idPd);
		
			OneOfControlloAccessiAutenticazioneAutenticazione autRet = null;
			TipoAutenticazioneEnum tipoAutenticazione = null;
			if ( TipoAutenticazione.toEnumConstant(pd.getAutenticazione()) == null ) {
				tipoAutenticazione = TipoAutenticazioneEnum.CUSTOM;
			}
			else {		
				tipoAutenticazione = Enums.dualizeMap(Enums.tipoAutenticazioneFromRest).get(TipoAutenticazione.toEnumConstant(pd.getAutenticazione())) ;
			}
						
			ControlloAccessiAutenticazioneToken token = BaseHelper.evalnull( () -> pd.getGestioneToken().getAutenticazione() ) != null
					? ErogazioniApiHelper.fromGestioneTokenAutenticazione(pd.getGestioneToken().getAutenticazione())
					: null;
			
			switch(tipoAutenticazione) {
			case HTTP_BASIC: {
				APIImplAutenticazioneBasic authnBasic = new APIImplAutenticazioneBasic();
				authnBasic.setTipo(tipoAutenticazione);
				authnBasic.setOpzionale(Helper.statoFunzionalitaConfToBool( pd.getAutenticazioneOpzionale() ));
				autRet = authnBasic;
				
				Optional<Proprieta> prop = pd.getProprietaAutenticazioneList().stream().filter( p -> ParametriAutenticazioneBasic.CLEAN_HEADER_AUTHORIZATION.equals(p.getNome())).findAny();
				if (prop.isPresent() && prop.get().getValore().equals(ParametriAutenticazionePrincipal.CLEAN_PRINCIPAL_TRUE))
					authnBasic.setForward(false);
				else
					authnBasic.setForward(true);
				
				break;
			}
			case HTTPS: {
				APIImplAutenticazioneHttps authnHttps = new APIImplAutenticazioneHttps();
				authnHttps.setTipo(tipoAutenticazione);
				authnHttps.setOpzionale(Helper.statoFunzionalitaConfToBool( pd.getAutenticazioneOpzionale() ));
				autRet = authnHttps;
				
				break;
			}
			case PRINCIPAL: {
			
				APIImplAutenticazionePrincipal authnPrincipal = new APIImplAutenticazionePrincipal();
				authnPrincipal.setTipo(tipoAutenticazione);
				authnPrincipal.setOpzionale(Helper.statoFunzionalitaConfToBool( pd.getAutenticazioneOpzionale() ));
				autRet = authnPrincipal;
				
				TipoAutenticazionePrincipal tipoAuthnPrincipal = pd.getProprietaAutenticazioneList()
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
					Optional<Proprieta> prop = pd.getProprietaAutenticazioneList().stream().filter( p -> ParametriAutenticazionePrincipal.NOME.equals(p.getNome())).findAny();
					if (prop.isPresent()) authnPrincipal.setNome(prop.get().getValore());					
					break;
				}
				case FORM_BASED: {
					Optional<Proprieta> prop = pd.getProprietaAutenticazioneList().stream().filter( p -> ParametriAutenticazionePrincipal.NOME.equals(p.getNome())).findAny();
					if (prop.isPresent()) authnPrincipal.setNome(prop.get().getValore());					
					break;
				}
				case URL_BASED: {
					Optional<Proprieta> prop = pd.getProprietaAutenticazioneList().stream().filter( p -> ParametriAutenticazionePrincipal.PATTERN.equals(p.getNome())).findAny();
					if (prop.isPresent()) authnPrincipal.setPattern(prop.get().getValore());
					break;
				}
				case TOKEN: {
					Optional<Proprieta> prop = pd.getProprietaAutenticazioneList().stream().filter( p -> ParametriAutenticazionePrincipal.TOKEN_CLAIM.equals(p.getNome())).findAny();
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
							
							Optional<Proprieta> propName = pd.getProprietaAutenticazioneList().stream().filter( p -> ParametriAutenticazionePrincipal.NOME.equals(p.getNome())).findAny();
							if (propName.isPresent()) authnPrincipal.setNome(propName.get().getValore());
						}
					}
					break;
				}
				}	// switch config.getTipo
				
				// IsForward
				Optional<Proprieta> prop = pd.getProprietaAutenticazioneList().stream().filter( p -> ParametriAutenticazionePrincipal.CLEAN_PRINCIPAL.equals(p.getNome())).findAny();
				if (prop.isPresent() && ParametriAutenticazionePrincipal.CLEAN_PRINCIPAL_TRUE.equals(prop.get().getValore()))
					authnPrincipal.setForward(false);
				else
					authnPrincipal.setForward(true);
				
				break;
			}  // case principal
			
			case API_KEY: {
				
				APIImplAutenticazioneApiKey authnApiKey = new APIImplAutenticazioneApiKey();
				authnApiKey.setTipo(tipoAutenticazione);
				authnApiKey.setOpzionale(Helper.statoFunzionalitaConfToBool( pd.getAutenticazioneOpzionale() ));
				autRet = authnApiKey;
			
				if(pd.getProprietaAutenticazioneList()!=null && pd.sizeProprietaAutenticazioneList()>0) {
					
					APIImplAutenticazioneApiKeyPosizione posizione = null;
					APIImplAutenticazioneApiKeyConfig apiKeyNomi = null;
					APIImplAutenticazioneApiKeyConfig appIdNomi = null;
					boolean useOasNames = false;
					
					for (Proprieta proprieta : pd.getProprietaAutenticazioneList()) {
						String nomeP = proprieta.getNome();
						String valoreP = proprieta.getValore();
						if(ParametriAutenticazioneApiKey.APP_ID.equals(nomeP)) {
							authnApiKey.setAppId(ParametriAutenticazioneApiKey.APP_ID_TRUE.equals(valoreP));
						}
						else if(ParametriAutenticazioneApiKey.QUERY_PARAMETER.equals(nomeP)) {
							if(posizione==null) {
								posizione = new APIImplAutenticazioneApiKeyPosizione();
							}
							posizione.setQueryParameter(ParametriAutenticazioneApiKey.QUERY_PARAMETER_TRUE.equals(valoreP));
						}
						else if(ParametriAutenticazioneApiKey.HEADER.equals(nomeP)) {
							if(posizione==null) {
								posizione = new APIImplAutenticazioneApiKeyPosizione();
							}
							posizione.setHeader(ParametriAutenticazioneApiKey.HEADER_TRUE.equals(valoreP));
						}
						else if(ParametriAutenticazioneApiKey.COOKIE.equals(nomeP)) {
							if(posizione==null) {
								posizione = new APIImplAutenticazioneApiKeyPosizione();
							}
							posizione.setCookie(ParametriAutenticazioneApiKey.COOKIE_TRUE.equals(valoreP));
						}
						else if(ParametriAutenticazioneApiKey.USE_OAS3_NAMES.equals(nomeP)) {
							useOasNames = ParametriAutenticazioneApiKey.USE_OAS3_NAMES_TRUE.equals(valoreP);
						}	
						else if(ParametriAutenticazioneApiKey.CLEAN_API_KEY.equals(nomeP)) {
							boolean clean = ParametriAutenticazioneApiKey.CLEAN_API_KEY_TRUE.equals(valoreP);
							authnApiKey.setApiKeyForward(!clean);
						}
						else if(ParametriAutenticazioneApiKey.CLEAN_APP_ID.equals(nomeP)) {
							boolean clean = ParametriAutenticazioneApiKey.CLEAN_APP_ID_TRUE.equals(valoreP);
							authnApiKey.setAppIdForward(!clean);
						}
					}
					
					if(!useOasNames) {
						for (Proprieta proprieta : pd.getProprietaAutenticazioneList()) {
							String nomeP = proprieta.getNome();
							String valoreP = proprieta.getValore();
							if(ParametriAutenticazioneApiKey.NOME_QUERY_PARAMETER_API_KEY.equals(nomeP)) {
								if(apiKeyNomi==null) {
									apiKeyNomi = new APIImplAutenticazioneApiKeyConfig();
								}
								apiKeyNomi.setQueryParameter(valoreP);
							}
							else if(ParametriAutenticazioneApiKey.NOME_HEADER_API_KEY.equals(nomeP)) {
								if(apiKeyNomi==null) {
									apiKeyNomi = new APIImplAutenticazioneApiKeyConfig();
								}
								apiKeyNomi.setHeader(valoreP);
							}
							else if(ParametriAutenticazioneApiKey.NOME_COOKIE_API_KEY.equals(nomeP)) {
								if(apiKeyNomi==null) {
									apiKeyNomi = new APIImplAutenticazioneApiKeyConfig();
								}
								apiKeyNomi.setCookie(valoreP);
							}
							else if(ParametriAutenticazioneApiKey.NOME_QUERY_PARAMETER_APP_ID.equals(nomeP)) {
								if(appIdNomi==null) {
									appIdNomi = new APIImplAutenticazioneApiKeyConfig();
								}
								appIdNomi.setQueryParameter(valoreP);
							}
							else if(ParametriAutenticazioneApiKey.NOME_HEADER_APP_ID.equals(nomeP)) {
								if(appIdNomi==null) {
									appIdNomi = new APIImplAutenticazioneApiKeyConfig();
								}
								appIdNomi.setHeader(valoreP);
							}
							else if(ParametriAutenticazioneApiKey.NOME_COOKIE_APP_ID.equals(nomeP)) {
								if(appIdNomi==null) {
									appIdNomi = new APIImplAutenticazioneApiKeyConfig();
								}
								appIdNomi.setCookie(valoreP);
							}
						}
					}
					
					authnApiKey.setPosizione(posizione);
					authnApiKey.setApiKeyNomi(apiKeyNomi);
					authnApiKey.setAppIdNomi(appIdNomi);
				}
				
				break;
			}  // case api-key
			
			case CUSTOM: {
				APIImplAutenticazioneCustom authnCustom = new APIImplAutenticazioneCustom();
				authnCustom.setTipo(tipoAutenticazione);
				authnCustom.setOpzionale(Helper.statoFunzionalitaConfToBool( pd.getAutenticazioneOpzionale() ));
				autRet = authnCustom;
				
				authnCustom.setNome(pd.getAutenticazione());
				
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
		catch(jakarta.ws.rs.WebApplicationException e) {
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
    public ControlloAccessiAutorizzazioneView getFruizioneControlloAccessiAutorizzazione(String erogatore, String nome, Integer versione, ProfiloEnum profilo, String soggetto, String gruppo, String tipoServizio) {
		IContext context = this.getContext();
		try {
			context.getLogger().info("Invocazione in corso ...");     

			AuthorizationManager.authorize(context, getAuthorizationConfig());
			context.getLogger().debug("Autorizzazione completata con successo");     
                        
			final FruizioniConfEnv env = new FruizioniConfEnv(context.getServletRequest(), profilo, soggetto, context, erogatore, nome, versione, gruppo, tipoServizio );		
			final PortaDelegata pd = env.pdCore.getPortaDelegata(env.idPd);
			
			ControlloAccessiAutorizzazioneView ret = ErogazioniApiHelper.controlloAccessiAutorizzazioneFromPD(pd);
			
			context.getLogger().info("Invocazione completata con successo");		
			return ret;
     
		}
		catch(jakarta.ws.rs.WebApplicationException e) {
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
    public ControlloAccessiAutorizzazioneApplicativi getFruizioneControlloAccessiAutorizzazioneApplicativi(String erogatore, String nome, Integer versione, ProfiloEnum profilo, String soggetto, String gruppo, String tipoServizio) {
		IContext context = this.getContext();
		try {
			context.getLogger().info("Invocazione in corso ...");     

			AuthorizationManager.authorize(context, getAuthorizationConfig());
			context.getLogger().debug("Autorizzazione completata con successo");
			
			final FruizioniConfEnv env = new FruizioniConfEnv(context.getServletRequest(), profilo, soggetto, context, erogatore, nome, versione, gruppo, tipoServizio );		
			final PortaDelegata pd = env.pdCore.getPortaDelegata(env.idPd);
			ControlloAccessiAutorizzazioneApplicativi ret = new ControlloAccessiAutorizzazioneApplicativi();
			
			int idLista = Liste.PORTE_DELEGATE_SERVIZIO_APPLICATIVO;
			ConsoleSearch ricerca = Helper.setupRicercaPaginata("", -1, 0, idLista);
			List<ServizioApplicativo> lista = env.pdCore.porteDelegateServizioApplicativoList(pd.getId(), ricerca);
			ret.setApplicativi(lista.stream().map( saPA -> saPA.getNome()).collect(Collectors.toList()));
		
			context.getLogger().info("Invocazione completata con successo");
			return ret;
		}
		catch(jakarta.ws.rs.WebApplicationException e) {
			context.getLogger().error_except404("Invocazione terminata con errore '4xx': %s",e, e.getMessage());
			throw e;
		}
		catch(Throwable e) {
			context.getLogger().error("Invocazione terminata con errore: %s",e, e.getMessage());
			throw FaultCode.ERRORE_INTERNO.toException(e);
		}
    }
    
    /**
     * Restituisce l'elenco degli applicativi token autorizzati
     *
     * Questa operazione consente di ottenere l'elenco degli applicativi token autorizzati
     *
     */
	@Override
    public ControlloAccessiAutorizzazioneApplicativi getFruizioneControlloAccessiAutorizzazioneApplicativiToken(String erogatore, String nome, Integer versione, ProfiloEnum profilo, String soggetto, String gruppo, String tipoServizio) {
		IContext context = this.getContext();
		try {
			context.getLogger().info("Invocazione in corso ...");     

			AuthorizationManager.authorize(context, getAuthorizationConfig());
			context.getLogger().debug("Autorizzazione completata con successo");     
                        
			final FruizioniConfEnv env = new FruizioniConfEnv(context.getServletRequest(), profilo, soggetto, context, erogatore, nome, versione, gruppo, tipoServizio );		
			final PortaDelegata pd = env.pdCore.getPortaDelegata(env.idPd);
			ControlloAccessiAutorizzazioneApplicativi ret = new ControlloAccessiAutorizzazioneApplicativi();
			
			int idLista = Liste.PORTE_DELEGATE_TOKEN_SERVIZIO_APPLICATIVO;
			ConsoleSearch ricerca = Helper.setupRicercaPaginata("", -1, 0, idLista);
			List<ServizioApplicativo> lista = env.pdCore.porteDelegateServizioApplicativoTokenList(pd.getId(), ricerca);
			ret.setApplicativi(lista.stream().map( saPA -> saPA.getNome()).collect(Collectors.toList()));
		
			context.getLogger().info("Invocazione completata con successo");
			return ret;
		}
		catch(jakarta.ws.rs.WebApplicationException e) {
			context.getLogger().error("Invocazione terminata con errore '4xx': %s",e, e.getMessage());
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
    public ControlloAccessiAutorizzazioneRuoli getFruizioneControlloAccessiAutorizzazioneRuoli(String erogatore, String nome, Integer versione, ProfiloEnum profilo, String soggetto, String gruppo, String tipoServizio) {
		IContext context = this.getContext();
		try {
			context.getLogger().info("Invocazione in corso ...");     

			AuthorizationManager.authorize(context, getAuthorizationConfig());
			context.getLogger().debug("Autorizzazione completata con successo");     
                        
			final FruizioniConfEnv env = new FruizioniConfEnv(context.getServletRequest(), profilo, soggetto, context, erogatore, nome, versione, gruppo, tipoServizio );		
			final PortaDelegata pd = env.pdCore.getPortaDelegata(env.idPd);
			final ControlloAccessiAutorizzazioneRuoli ret = new ControlloAccessiAutorizzazioneRuoli(); 
            
			if(pd.getRuoli()!=null && pd.getRuoli().sizeRuoloList()>0) {
				ret.setRuoli(BaseHelper.evalnull( () -> pd.getRuoli().getRuoloList().stream().map(Ruolo::getNome).collect(Collectors.toList()) ));
			}
        
			context.getLogger().info("Invocazione completata con successo");
        	return ret;
          
		}
		catch(jakarta.ws.rs.WebApplicationException e) {
			context.getLogger().error_except404("Invocazione terminata con errore '4xx': %s",e, e.getMessage());
			throw e;
		}
		catch(Throwable e) {
			context.getLogger().error("Invocazione terminata con errore: %s",e, e.getMessage());
			throw FaultCode.ERRORE_INTERNO.toException(e);
		}
    }
    
    /**
     * Restituisce l'elenco dei ruoli token autorizzati
     *
     * Questa operazione consente di ottenere l'elenco dei ruoli token autorizzati
     *
     */
	@Override
    public ControlloAccessiAutorizzazioneRuoli getFruizioneControlloAccessiAutorizzazioneRuoliToken(String erogatore, String nome, Integer versione, ProfiloEnum profilo, String soggetto, String gruppo, String tipoServizio) {
		IContext context = this.getContext();
		try {
			context.getLogger().info("Invocazione in corso ...");     

			AuthorizationManager.authorize(context, getAuthorizationConfig());
			context.getLogger().debug("Autorizzazione completata con successo");     
                        
			final FruizioniConfEnv env = new FruizioniConfEnv(context.getServletRequest(), profilo, soggetto, context, erogatore, nome, versione, gruppo, tipoServizio );		
			final PortaDelegata pd = env.pdCore.getPortaDelegata(env.idPd);
			final ControlloAccessiAutorizzazioneRuoli ret = new ControlloAccessiAutorizzazioneRuoli(); 
            
			if(pd.getAutorizzazioneToken()!=null && pd.getAutorizzazioneToken().getRuoli()!=null && pd.getAutorizzazioneToken().getRuoli().sizeRuoloList()>0) {
				ret.setRuoli(BaseHelper.evalnull( () -> pd.getAutorizzazioneToken().getRuoli().getRuoloList().stream().map(Ruolo::getNome).collect(Collectors.toList()) ));
			}
        
			context.getLogger().info("Invocazione completata con successo");
        	return ret;
		}
		catch(jakarta.ws.rs.WebApplicationException e) {
			context.getLogger().error("Invocazione terminata con errore '4xx': %s",e, e.getMessage());
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
    public ControlloAccessiAutorizzazioneScopes getFruizioneControlloAccessiAutorizzazioneScope(String erogatore, String nome, Integer versione, ProfiloEnum profilo, String soggetto, String gruppo, String tipoServizio) {
		IContext context = this.getContext();
		try {
			context.getLogger().info("Invocazione in corso ...");     

			AuthorizationManager.authorize(context, getAuthorizationConfig());
			context.getLogger().debug("Autorizzazione completata con successo");  
            
			final FruizioniConfEnv env = new FruizioniConfEnv(context.getServletRequest(), profilo, soggetto, context, erogatore, nome, versione, gruppo, tipoServizio );		
			final PortaDelegata pd = env.pdCore.getPortaDelegata(env.idPd);
			final ControlloAccessiAutorizzazioneScopes ret = new ControlloAccessiAutorizzazioneScopes();

			ret.setScope(BaseHelper.evalnull( () -> pd.getScope().getScopeList().stream().map(Scope::getNome).collect(Collectors.toList()) ));
			
			context.getLogger().info("Invocazione completata con successo");
			return ret;
     
		}
		catch(jakarta.ws.rs.WebApplicationException e) {
			context.getLogger().error_except404("Invocazione terminata con errore '4xx': %s",e, e.getMessage());
			throw e;
		}
		catch(Throwable e) {
			context.getLogger().error("Invocazione terminata con errore: %s",e, e.getMessage());
			throw FaultCode.ERRORE_INTERNO.toException(e);
		}
    }
    
   /**
     * Restituisce le proprietà di configurazione attivate
     *
     * Questa operazione consente di ottenere le proprietà di configurazione
     *
     */
	@Override
    public ElencoProprieta getFruizioneElencoProprieta(String erogatore, String nome, Integer versione, ProfiloEnum profilo, String soggetto, String gruppo, String tipoServizio) {
		IContext context = this.getContext();
		try {
			context.getLogger().info("Invocazione in corso ...");     

			AuthorizationManager.authorize(context, getAuthorizationConfig());
			context.getLogger().debug("Autorizzazione completata con successo");     
                        
			final FruizioniConfEnv env = new FruizioniConfEnv(context.getServletRequest(), profilo, soggetto, context, erogatore, nome, versione, gruppo, tipoServizio );		
			final PortaDelegata pd = env.pdCore.getPortaDelegata(env.idPd);
        
			ElencoProprieta ret = new ElencoProprieta();
			ret.setProprieta(new ArrayList<>());
			if(pd.getProprietaList()!=null && !pd.getProprietaList().isEmpty()) {
				for (Proprieta p: pd.getProprietaList()) {
					org.openspcoop2.core.config.rs.server.model.ProprietaOpzioneCifratura retP = new org.openspcoop2.core.config.rs.server.model.ProprietaOpzioneCifratura();
					retP.setNome(p.getNome());
					retP.setValore(p.getValore());
					retP.setEncrypted(BYOKUtilities.isWrappedValue(p.getValore()));
					ret.addProprietaItem(retP);
				}
			}
			
			context.getLogger().info("Invocazione completata con successo");
			return ret;        
     
		}
		catch(jakarta.ws.rs.WebApplicationException e) {
			context.getLogger().error("Invocazione terminata con errore '4xx': %s",e, e.getMessage());
			throw e;
		}
		catch(Throwable e) {
			context.getLogger().error("Invocazione terminata con errore: %s",e, e.getMessage());
			throw FaultCode.ERRORE_INTERNO.toException(e);
		}
    }

    /**
     * Restituisce la configurazione relativa all'identificazione degli attributi per quanto concerne il controllo degli accessi
     *
     * Questa operazione consente di ottenere la configurazione relativa all'identificazione degli attributi
     *
     */
	@Override
    public ControlloAccessiIdentificazioneAttributi getFruizioneControlloAccessiIdentificazioneAttributi(String erogatore, String nome, Integer versione, ProfiloEnum profilo, String soggetto, String gruppo, String tipoServizio) {
		IContext context = this.getContext();
		try {
			context.getLogger().info("Invocazione in corso ...");     

			AuthorizationManager.authorize(context, getAuthorizationConfig());
			context.getLogger().debug("Autorizzazione completata con successo");     
                        
			final FruizioniConfEnv env = new FruizioniConfEnv(context.getServletRequest(), profilo, soggetto, context, erogatore, nome, versione, gruppo, tipoServizio );		
			final PortaDelegata pd = env.pdCore.getPortaDelegata(env.idPd);
        
			final ControlloAccessiIdentificazioneAttributi ret = new ControlloAccessiIdentificazioneAttributi();
			if(pd.sizeAttributeAuthorityList()>0) {
				ret.setAbilitato(true);
				ret.setAttributeAuthority(new ArrayList<>());
				for (AttributeAuthority aa : pd.getAttributeAuthorityList()) {
					ControlloAccessiAttributeAuthority attributeAuthorityItem = new ControlloAccessiAttributeAuthority();
					attributeAuthorityItem.setNome(aa.getNome());
					if(aa.sizeAttributoList()>0) {
						attributeAuthorityItem.setAttributi(new ArrayList<>());
						for (String attributeName : aa.getAttributoList()) {
							attributeAuthorityItem.addAttributiItem(attributeName);
						}
					}
					ret.addAttributeAuthorityItem(attributeAuthorityItem);
				}
			}
			else {
				ret.setAbilitato(false);
			}

			context.getLogger().info("Invocazione completata con successo");
			return ret;
		}
		catch(jakarta.ws.rs.WebApplicationException e) {
			context.getLogger().error("Invocazione terminata con errore '4xx': %s",e, e.getMessage());
			throw e;
		}
		catch(Throwable e) {
			context.getLogger().error("Invocazione terminata con errore: %s",e, e.getMessage());
			throw FaultCode.ERRORE_INTERNO.toException(e);
		}
    }

   /**
     * Restituisce il dettaglio di una proprietà di configurazione
     *
     * Questa operazione consente di ottenere il dettaglio di una proprietà di configurazione
     *
     */
	@Override
    public org.openspcoop2.core.config.rs.server.model.ProprietaOpzioneCifratura getFruizioneProprieta(String erogatore, String nome, Integer versione, String proprieta, ProfiloEnum profilo, String soggetto, String gruppo, String tipoServizio) {
		IContext context = this.getContext();
		try {
			context.getLogger().info("Invocazione in corso ...");     

			AuthorizationManager.authorize(context, getAuthorizationConfig());
			context.getLogger().debug("Autorizzazione completata con successo");     
                        
			final FruizioniConfEnv env = new FruizioniConfEnv(context.getServletRequest(), profilo, soggetto, context, erogatore, nome, versione, gruppo, tipoServizio );		
			final PortaDelegata pd = env.pdCore.getPortaDelegata(env.idPd);
        
			Proprieta toGet = null;
			if(pd.getProprietaList()!=null && !pd.getProprietaList().isEmpty()) {
				Optional<Proprieta> op = BaseHelper.findFirst(pd.getProprietaList(), p -> p.getNome().equals(proprieta));
				if(op.isPresent()) {
					toGet = op.get();
				}
			}
			
			org.openspcoop2.core.config.rs.server.model.ProprietaOpzioneCifratura ret = null;
			if(toGet!=null) {
				ret = new org.openspcoop2.core.config.rs.server.model.ProprietaOpzioneCifratura();
				ret.setNome(toGet.getNome());
				ret.setValore(toGet.getValore());
				ret.setEncrypted(BYOKUtilities.isWrappedValue(toGet.getValore()));
			}
			else {
				throw FaultCode.NOT_FOUND.toException("Nessuna proprietà è presente nella configurazione con nome '"+proprieta+"'"); 
			}
			
			context.getLogger().info("Invocazione completata con successo");
			return ret;
     
		}
		catch(jakarta.ws.rs.WebApplicationException e) {
			context.getLogger().error("Invocazione terminata con errore '4xx': %s",e, e.getMessage());
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
     * Questa operazione consente di ottenere la configurazione relativa alla gestione dei token
     *
     */
	@Override
    public ControlloAccessiGestioneToken getFruizioneControlloAccessiGestioneToken(String erogatore, String nome, Integer versione, ProfiloEnum profilo, String soggetto, String gruppo, String tipoServizio) {
		IContext context = this.getContext();
		try {
			context.getLogger().info("Invocazione in corso ...");     

			AuthorizationManager.authorize(context, getAuthorizationConfig());
			context.getLogger().debug("Autorizzazione completata con successo");
			   
			final FruizioniConfEnv env = new FruizioniConfEnv(context.getServletRequest(), profilo, soggetto, context, erogatore, nome, versione, gruppo, tipoServizio );		
			final PortaDelegata pd = env.pdCore.getPortaDelegata(env.idPd);
                        
			final ControlloAccessiGestioneToken ret = new ControlloAccessiGestioneToken();
			final GestioneToken paToken = pd.getGestioneToken();
			
			ret.setAbilitato(paToken != null);
			if (paToken != null) {
				ret.setIntrospection(StatoFunzionalitaConWarningEnum.fromValue(paToken.getIntrospection().getValue() ));
				ret.setPolicy(paToken.getPolicy());
				ret.setTokenForward( Helper.statoFunzionalitaConfToBool( paToken.getForward() ));
				ret.setTokenOpzionale( Helper.statoFunzionalitaConfToBool(paToken.getTokenOpzionale() ));
				ret.setUserInfo(StatoFunzionalitaConWarningEnum.fromValue( paToken.getUserInfo().getValue() ));
				ret.setValidazioneJwt(StatoFunzionalitaConWarningEnum.fromValue( paToken.getValidazione().getValue() ));
			}
        
			context.getLogger().info("Invocazione completata con successo");
			return ret;
     
		}
		catch(jakarta.ws.rs.WebApplicationException e) {
			context.getLogger().error_except404("Invocazione terminata con errore '4xx': %s",e, e.getMessage());
			throw e;
		}
		catch(Throwable e) {
			context.getLogger().error("Invocazione terminata con errore: %s",e, e.getMessage());
			throw FaultCode.ERRORE_INTERNO.toException(e);
		}
    }
    
    /**
     * Restituisce le informazioni sulla configurazione CORS associata alla fruizione
     *
     * Questa operazione consente di ottenere le informazioni sulla configurazione CORS associata alla fruizione identificata dall'erogatore, dal nome e dalla versione
     *
     */
	@Override
    public GestioneCors getFruizioneGestioneCORS(String erogatore, String nome, Integer versione, ProfiloEnum profilo, String soggetto, String tipoServizio) {
		IContext context = this.getContext();
		try {
			context.getLogger().info("Invocazione in corso ...");     

			AuthorizationManager.authorize(context, getAuthorizationConfig());
			context.getLogger().debug("Autorizzazione completata con successo");     
                       
			final ErogazioniEnv env = new ErogazioniEnv(context.getServletRequest(), profilo, soggetto, context);
			final IDSoggetto idErogatore = new IDSoggetto(env.tipo_soggetto, erogatore);
			final AccordoServizioParteSpecifica asps = BaseHelper.supplyOrNotFound( 
					() -> ErogazioniApiHelper.getServizioIfFruizione(tipoServizio, nome, versione, idErogatore, env.idSoggetto.toIDSoggetto(), env),
					"Fruizione"
				);
			
			final IdServizio idAsps = new IdServizio(env.idServizioFactory.getIDServizioFromAccordo(asps), asps.getId());
			final IDPortaDelegata idPd =  ErogazioniApiHelper.getIDGruppoPDDefault(env.idSoggetto.toIDSoggetto(), idAsps, env.apsCore);
			final PortaDelegata pd = env.pdCore.getPortaDelegata(idPd);
			
			final CorsConfigurazione pdConf = pd.getGestioneCors();        
			
			final GestioneCors ret = ErogazioniApiHelper.convert(pdConf);
			
			
			context.getLogger().info("Invocazione completata con successo");
			return ret;
     
		}
		catch(jakarta.ws.rs.WebApplicationException e) {
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
    public RateLimitingPolicyFruizioneView getFruizioneRateLimitingPolicy(String erogatore, String nome, Integer versione, String idPolicy, ProfiloEnum profilo, String soggetto, String gruppo, String tipoServizio) {
		IContext context = this.getContext();
		try {
			context.getLogger().info("Invocazione in corso ...");     

			AuthorizationManager.authorize(context, getAuthorizationConfig());
			context.getLogger().debug("Autorizzazione completata con successo");     
                        
            
			final FruizioniConfEnv env = new FruizioniConfEnv(context.getServletRequest(), profilo, soggetto, context, erogatore, nome, versione, gruppo, tipoServizio );		
			final PortaDelegata pd = env.pdCore.getPortaDelegata(env.idPd);
			
			AttivazionePolicy policy = BaseHelper.supplyOrNotFound( 
					() -> env.confCore.getAttivazionePolicy(idPolicy, RuoloPolicy.DELEGATA, pd.getNome()),
					"Rate Limiting Policy con nome " + idPolicy
				);
			if ( policy == null ) 
				throw FaultCode.NOT_FOUND.toException("Nessuna policy di rate limiting con nome " + idPolicy );
			
			InfoPolicy infoPolicy = env.confCore.getInfoPolicy(policy.getIdPolicy());
			RateLimitingPolicyFruizioneView ret = ErogazioniApiHelper.convert( policy, infoPolicy, new RateLimitingPolicyFruizioneView() );
	
			context.getLogger().info("Invocazione completata con successo");
			return ret;
     
		}
		catch(jakarta.ws.rs.WebApplicationException e) {
			context.getLogger().error_except404("Invocazione terminata con errore '4xx': %s",e, e.getMessage());
			throw e;
		}
		catch(Throwable e) {
			context.getLogger().error("Invocazione terminata con errore: %s",e, e.getMessage());
			throw FaultCode.ERRORE_INTERNO.toException(e);
		}
    }

    /**
     * Restituisce la configurazione relativa alla registrazione dei diagnostici
     *
     * Questa operazione consente di ottenere la configurazione relativa alla registrazione dei diagnostici
     *
     */
	@Override
    public RegistrazioneDiagnosticiConfigurazione getFruizioneRegistrazioneDiagnostici(String erogatore, String nome, Integer versione, ProfiloEnum profilo, String soggetto, String gruppo, String tipoServizio) {
		IContext context = this.getContext();
		try {
			context.getLogger().info("Invocazione in corso ...");     

			AuthorizationManager.authorize(context, getAuthorizationConfig());
			context.getLogger().debug("Autorizzazione completata con successo");     
                        
			final FruizioniConfEnv env = new FruizioniConfEnv(context.getServletRequest(), profilo, soggetto, context, erogatore, nome, versione, gruppo, tipoServizio );		
			final PortaDelegata pd = env.pdCore.getPortaDelegata(env.idPd);
			final RegistrazioneDiagnosticiConfigurazione ret = ErogazioniApiHelper.fromDiagnosticiConfigurazione(pd.getTracciamento());
			
			context.getLogger().info("Invocazione completata con successo");
			return ret;
     
		}
		catch(jakarta.ws.rs.WebApplicationException e) {
			context.getLogger().error("Invocazione terminata con errore '4xx': %s",e, e.getMessage());
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
    public RegistrazioneMessaggi getFruizioneRegistrazioneMessaggi(String erogatore, String nome, Integer versione, ProfiloEnum profilo, String soggetto, String gruppo, String tipoServizio) {
		IContext context = this.getContext();
		try {
			context.getLogger().info("Invocazione in corso ...");     

			AuthorizationManager.authorize(context, getAuthorizationConfig());
			context.getLogger().debug("Autorizzazione completata con successo");     
                        
			final FruizioniConfEnv env = new FruizioniConfEnv(context.getServletRequest(), profilo, soggetto, context, erogatore, nome, versione, gruppo, tipoServizio );		
			final PortaDelegata pd = env.pdCore.getPortaDelegata(env.idPd);
			final RegistrazioneMessaggi ret = ErogazioniApiHelper.fromDumpConfigurazione(pd.getDump());
			
			context.getLogger().info("Invocazione completata con successo");
			return ret;     
		}
		catch(jakarta.ws.rs.WebApplicationException e) {
			context.getLogger().error_except404("Invocazione terminata con errore '4xx': %s",e, e.getMessage());
			throw e;
		}
		catch(Throwable e) {
			context.getLogger().error("Invocazione terminata con errore: %s",e, e.getMessage());
			throw FaultCode.ERRORE_INTERNO.toException(e);
		}
    }
    
    /**
     * Restituisce la configurazione relativa alla registrazione delle transazioni
     *
     * Questa operazione consente di ottenere la configurazione relativa alla registrazione delle transazioni
     *
     */
	@Override
    public RegistrazioneTransazioniConfigurazione getFruizioneRegistrazioneTransazioni(String erogatore, String nome, Integer versione, ProfiloEnum profilo, String soggetto, String gruppo, String tipoServizio) {
		IContext context = this.getContext();
		try {
			context.getLogger().info("Invocazione in corso ...");     

			AuthorizationManager.authorize(context, getAuthorizationConfig());
			context.getLogger().debug("Autorizzazione completata con successo");     
            
			final FruizioniConfEnv env = new FruizioniConfEnv(context.getServletRequest(), profilo, soggetto, context, erogatore, nome, versione, gruppo, tipoServizio );		
			final PortaDelegata pd = env.pdCore.getPortaDelegata(env.idPd);
			final RegistrazioneTransazioniConfigurazione ret = ErogazioniApiHelper.fromTransazioniConfigurazione(pd.getTracciamento(), env);
        
			context.getLogger().info("Invocazione completata con successo");
			return ret;
     
		}
		catch(jakarta.ws.rs.WebApplicationException e) {
			context.getLogger().error("Invocazione terminata con errore '4xx': %s",e, e.getMessage());
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
    public ApiImplStato getFruizioneStato(String erogatore, String nome, Integer versione, ProfiloEnum profilo, String soggetto, String gruppo, String tipoServizio) {
		IContext context = this.getContext();
		try {
			context.getLogger().info("Invocazione in corso ...");     

			AuthorizationManager.authorize(context, getAuthorizationConfig());
			context.getLogger().debug("Autorizzazione completata con successo");     
                        
			final FruizioniConfEnv env = new FruizioniConfEnv(context.getServletRequest(), profilo, soggetto, context, erogatore, nome, versione, gruppo, tipoServizio );		
			final PortaDelegata pd = env.pdCore.getPortaDelegata(env.idPd);
			
			final ApiImplStato ret = new ApiImplStato();
			ret.setAbilitato(Helper.statoFunzionalitaConfToBool(pd.getStato()));
                        
		
			context.getLogger().info("Invocazione completata con successo");
        	return ret;
		}
		catch(jakarta.ws.rs.WebApplicationException e) {
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
    public CorrelazioneApplicativaRichiesta getFruizioneTracciamentoCorrelazioneApplicativaRichiesta(String erogatore, String nome, Integer versione, String elemento, ProfiloEnum profilo, String soggetto, String gruppo, String tipoServizio) {
		IContext context = this.getContext();
		try {
			context.getLogger().info("Invocazione in corso ...");     

			AuthorizationManager.authorize(context, getAuthorizationConfig());
			context.getLogger().debug("Autorizzazione completata con successo");
			
			final FruizioniConfEnv env = new FruizioniConfEnv(context.getServletRequest(), profilo, soggetto, context, erogatore, nome, versione, gruppo, tipoServizio );		
			final PortaDelegata pd = env.pdCore.getPortaDelegata(env.idPd);
			
			final String searchElemento = elemento.equals("*")
					? ""
					: elemento;			
			List<CorrelazioneApplicativaElemento> lista = BaseHelper.evalnull( () -> pd.getCorrelazioneApplicativa().getElementoList() );
            
			Optional<CorrelazioneApplicativaElemento> el = BaseHelper.findFirst( lista, c -> (c.getNome()==null ? "" : c.getNome()).equals(searchElemento) );
			
			if ( !el.isPresent() )
				throw FaultCode.NOT_FOUND.toException("CorrelazioneApplicativaRichiesta per l'elemento " + elemento + " non presente");
			
			CorrelazioneApplicativaRichiesta ret = ErogazioniApiHelper.convert(el.get());
			
			context.getLogger().info("Invocazione completata con successo");
			return ret;
     
		}
		catch(jakarta.ws.rs.WebApplicationException e) {
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
    public CorrelazioneApplicativaRisposta getFruizioneTracciamentoCorrelazioneApplicativaRisposta(String erogatore, String nome, Integer versione, String elemento, ProfiloEnum profilo, String soggetto, String gruppo, String tipoServizio) {
		IContext context = this.getContext();
		try {
			context.getLogger().info("Invocazione in corso ...");     

			AuthorizationManager.authorize(context, getAuthorizationConfig());
			context.getLogger().debug("Autorizzazione completata con successo");     
                        
			final FruizioniConfEnv env = new FruizioniConfEnv(context.getServletRequest(), profilo, soggetto, context, erogatore, nome, versione, gruppo, tipoServizio );		
			final PortaDelegata pd = env.pdCore.getPortaDelegata(env.idPd);
			
			final String searchElemento = elemento.equals("*")
					? ""
					: elemento;			
			
			List<CorrelazioneApplicativaRispostaElemento> lista = BaseHelper.evalnull( () -> pd.getCorrelazioneApplicativaRisposta().getElementoList() );
      
			Optional<CorrelazioneApplicativaRispostaElemento> el = BaseHelper.findFirst( lista, c -> (c.getNome()==null ? "" : c.getNome()).equals(searchElemento) );
			
			if ( !el.isPresent() )
				throw FaultCode.NOT_FOUND.toException("CorrelazioneApplicativaRisposta per l'elemento " + elemento + " non presente");
			
			CorrelazioneApplicativaRisposta ret = ErogazioniApiHelper.convert(el.get());
			
			context.getLogger().info("Invocazione completata con successo");
			return ret;
     
		}
		catch(jakarta.ws.rs.WebApplicationException e) {
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
    public Validazione getFruizioneValidazione(String erogatore, String nome, Integer versione, ProfiloEnum profilo, String soggetto, String gruppo, String tipoServizio) {
		IContext context = this.getContext();
		try {
			context.getLogger().info("Invocazione in corso ...");     

			AuthorizationManager.authorize(context, getAuthorizationConfig());
			context.getLogger().debug("Autorizzazione completata con successo");     
			
			final FruizioniConfEnv env = new FruizioniConfEnv(context.getServletRequest(), profilo, soggetto, context, erogatore, nome, versione, gruppo, tipoServizio );		
			final PortaDelegata pd = env.pdCore.getPortaDelegata(env.idPd);
			final Validazione ret = ErogazioniApiHelper.fromValidazioneContenutiApplicativi(pd.getValidazioneContenutiApplicativi());
        
			context.getLogger().info("Invocazione completata con successo");
			return ret;
     
		}
		catch(jakarta.ws.rs.WebApplicationException e) {
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
    public void updateFruizioneCachingRisposta(CachingRisposta body, String erogatore, String nome, Integer versione, ProfiloEnum profilo, String soggetto, String gruppo, String tipoServizio) {
		IContext context = this.getContext();
		try {
			context.getLogger().info("Invocazione in corso ...");     

			AuthorizationManager.authorize(context, getAuthorizationConfig());
			context.getLogger().debug("Autorizzazione completata con successo");
			
			BaseHelper.throwIfNull(body);
			
			final FruizioniConfEnv env = new FruizioniConfEnv(context.getServletRequest(), profilo, soggetto, context, erogatore, nome, versione, gruppo, tipoServizio );		
			final PortaDelegata pd = env.pdCore.getPortaDelegata(env.idPd);
			
			if (!env.paHelper.checkDataConfigurazioneResponseCachingPorta(TipoOperazione.OTHER, true, body.getStato().toString())) {
				throw FaultCode.RICHIESTA_NON_VALIDA.toException(StringEscapeUtils.unescapeHtml4(env.pd.getMessage()));
			}
			
			ResponseCachingConfigurazione newConfigurazione = ErogazioniApiHelper.buildResponseCachingConfigurazione(body, env.paHelper);
			
			pd.setResponseCaching(newConfigurazione);
			env.pdCore.performUpdateOperation(env.userLogin, false, pd);			
			

			context.getLogger().info("Invocazione completata con successo");       
     
		}
		catch(jakarta.ws.rs.WebApplicationException e) {
			context.getLogger().error_except404("Invocazione terminata con errore '4xx': %s",e, e.getMessage());
			throw e;
		}
		catch(Throwable e) {
			context.getLogger().error("Invocazione terminata con errore: %s",e, e.getMessage());
			throw FaultCode.ERRORE_INTERNO.toException(e);
		}
    }
    
    /**
     * Consente di modificare il canale associato alla fruizione
     *
     * Questa operazione consente di aggiornare il canale associato alla fruizione
     *
     */
	@Override
    public void updateFruizioneCanale(ConfigurazioneApiCanale body, String erogatore, String nome, Integer versione, ProfiloEnum profilo, String soggetto, String tipoServizio) {
		IContext context = this.getContext();
		try {
			context.getLogger().info("Invocazione in corso ...");     

			AuthorizationManager.authorize(context, getAuthorizationConfig());
			context.getLogger().debug("Autorizzazione completata con successo");     
                        
			BaseHelper.throwIfNull(body);	
			  
			final ErogazioniEnv env = new ErogazioniEnv(context.getServletRequest(), profilo, soggetto, context);
			final IDSoggetto idErogatore = new IDSoggetto(env.tipo_soggetto, erogatore);
			final AccordoServizioParteSpecifica asps = BaseHelper.supplyOrNotFound( 
					() -> ErogazioniApiHelper.getServizioIfFruizione(tipoServizio, nome, versione, idErogatore, env.idSoggetto.toIDSoggetto(),  env),
					"Fruizione"
				);
			final IdServizio idAsps = new IdServizio(env.idServizioFactory.getIDServizioFromAccordo(asps), asps.getId());
			final IDPortaDelegata idPd =  ErogazioniApiHelper.getIDGruppoPDDefault(env.idSoggetto.toIDSoggetto(), idAsps, env.apsCore);
			final PortaDelegata pd = env.pdCore.getPortaDelegata(idPd);
			
			if(ConfigurazioneCanaleEnum.RIDEFINITO.equals(body.getConfigurazione())){
				if(body.getCanale()==null || "".equals(body.getCanale())) {
					throw FaultCode.RICHIESTA_NON_VALIDA.toException("Specificare un canale");
				}
				if(!env.canali.contains(body.getCanale())) {
					throw FaultCode.RICHIESTA_NON_VALIDA.toException("Il canale fornito '" + body.getCanale() + "' non è presente nel registro");
				}
				pd.setCanale(body.getCanale());
			}
			else {
				pd.setCanale(null);
			}
			
			env.paCore.performUpdateOperation(env.userLogin, false, pd);
		    
			context.getLogger().info("Invocazione completata con successo");
     
		}
		catch(jakarta.ws.rs.WebApplicationException e) {
			context.getLogger().error("Invocazione terminata con errore '4xx': %s",e, e.getMessage());
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
    public void updateFruizioneControlloAccessiAutenticazione(ControlloAccessiAutenticazione body, String erogatore, String nome, Integer versione, ProfiloEnum profilo, String soggetto, String gruppo, String tipoServizio) {
		IContext context = this.getContext();
		try {
			context.getLogger().info("Invocazione in corso ...");     

			AuthorizationManager.authorize(context, getAuthorizationConfig());
			context.getLogger().debug("Autorizzazione completata con successo");
			
			BaseHelper.throwIfNull(body);
                        
			final FruizioniConfEnv env = new FruizioniConfEnv(context.getServletRequest(), profilo, soggetto, context, erogatore, nome, versione, gruppo, tipoServizio );		
			final PortaDelegata oldPd = env.pdCore.getPortaDelegata(env.idPd);
			final PortaDelegata newPd = env.pdCore.getPortaDelegata(env.idPd);
			
			ErogazioniApiHelper.fillPortaDelegata(env, body, newPd);
			
			if (! ErogazioniApiHelper.controlloAccessiCheckPD(env, oldPd, newPd)) {
				throw FaultCode.RICHIESTA_NON_VALIDA.toException(StringEscapeUtils.unescapeHtml4(  env.pd.getMessage() ));
			}
		
			
			env.pdCore.performUpdateOperation(env.userLogin, false, newPd);
			
			context.getLogger().info("Invocazione completata con successo");
        
     
		}
		catch(jakarta.ws.rs.WebApplicationException e) {
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
    public void updateFruizioneControlloAccessiAutorizzazione(ControlloAccessiAutorizzazione body, String erogatore, String nome, Integer versione, ProfiloEnum profilo, String soggetto, String gruppo, String tipoServizio) {
		IContext context = this.getContext();
		try {
			context.getLogger().info("Invocazione in corso ...");     

			AuthorizationManager.authorize(context, getAuthorizationConfig());
			context.getLogger().debug("Autorizzazione completata con successo");
			
			BaseHelper.throwIfNull(body);
                        
			final FruizioniConfEnv env = new FruizioniConfEnv(context.getServletRequest(), profilo, soggetto, context, erogatore, nome, versione, gruppo, tipoServizio );		
			final PortaDelegata pd = env.pdCore.getPortaDelegata(env.idPd);
			final PortaDelegata newPd = env.pdCore.getPortaDelegata(env.idPd);
			
			ErogazioniApiHelper.fillPortaDelegata(body, newPd);
			
			if (!ErogazioniApiHelper.controlloAccessiCheckPD(env, pd, newPd)) {
				throw FaultCode.RICHIESTA_NON_VALIDA.toException(StringEscapeUtils.unescapeHtml4(  env.pd.getMessage() ));
			}
			
			env.pdCore.performUpdateOperation(env.userLogin, false, newPd);
                        
			context.getLogger().info("Invocazione completata con successo");        
     
		}
		catch(jakarta.ws.rs.WebApplicationException e) {
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
     * Questa operazione consente di aggiornare la configurazione relativa alla gestione dei token
     *
     */
	@Override
    public void updateFruizioneControlloAccessiGestioneToken(ControlloAccessiGestioneToken body, String erogatore, String nome, Integer versione, ProfiloEnum profilo, String soggetto, String gruppo, String tipoServizio) {
		IContext context = this.getContext();
		try {
			context.getLogger().info("Invocazione in corso ...");     

			AuthorizationManager.authorize(context, getAuthorizationConfig());
			context.getLogger().debug("Autorizzazione completata con successo");
			
			BaseHelper.throwIfNull(body);

			final FruizioniConfEnv env = new FruizioniConfEnv(context.getServletRequest(), profilo, soggetto, context, erogatore, nome, versione, gruppo, tipoServizio );		
			final PortaDelegata newPd = env.pdCore.getPortaDelegata(env.idPd);
			final PortaDelegata oldPd = env.pdCore.getPortaDelegata(env.idPd);
			
			if (body.isAbilitato()) {
				GestioneToken gTok = newPd.getGestioneToken() != null ? newPd.getGestioneToken() : new GestioneToken();
				ErogazioniApiHelper.fillGestioneToken(gTok, body);
				newPd.setGestioneToken(gTok);
			}
			else
				newPd.setGestioneToken(null);
			
			if ( !ErogazioniApiHelper.controlloAccessiCheckPD(env, oldPd, newPd)) {
				throw FaultCode.RICHIESTA_NON_VALIDA.toException(env.pd.getMessage());
			}
			
			env.pdCore.performUpdateOperation(env.userLogin, false, newPd);
			context.getLogger().info("Invocazione completata con successo");
		}
		catch(jakarta.ws.rs.WebApplicationException e) {
			context.getLogger().error_except404("Invocazione terminata con errore '4xx': %s",e, e.getMessage());
			throw e;
		}
		catch(Throwable e) {
			context.getLogger().error("Invocazione terminata con errore: %s",e, e.getMessage());
			throw FaultCode.ERRORE_INTERNO.toException(e);
		}
    }
    
   /**
     * Modifica i dati di una proprietà di configurazione
     *
     * Questa operazione consente di aggiornare i dati relativi ad una proprietà di configurazione
     *
     */
	@Override
    public void updateFruizioneProprieta(org.openspcoop2.core.config.rs.server.model.ProprietaOpzioneCifratura body, String erogatore, String nome, Integer versione, String proprieta, ProfiloEnum profilo, String soggetto, String gruppo, String tipoServizio) {
		IContext context = this.getContext();
		try {
			context.getLogger().info("Invocazione in corso ...");     

			AuthorizationManager.authorize(context, getAuthorizationConfig());
			context.getLogger().debug("Autorizzazione completata con successo");     
                        
			BaseHelper.throwIfNull(body);

			final FruizioniConfEnv env = new FruizioniConfEnv(context.getServletRequest(), profilo, soggetto, context, erogatore, nome, versione, gruppo, tipoServizio );
			if(env.pdCore==null) {
				throw new CoreException("PdCore not initialized");
			}
			final PortaDelegata pd = env.pdCore.getPortaDelegata(env.idPd);
			
			if ((body.getNome().indexOf(" ") != -1) || (body.getValore().indexOf(" ") != -1)) {
				throw FaultCode.RICHIESTA_NON_VALIDA.toException(CostantiControlStation.MESSAGGIO_ERRORE_NON_INSERIRE_SPAZI_NEI_CAMPI_DI_TESTO);
			}
			
			if(!proprieta.equals(body.getNome()) &&
				// cambio nome proprieta
				pd.getProprietaList()!=null && !pd.getProprietaList().isEmpty()) {
				for (Proprieta p : pd.getProprietaList()) {
					if(p.getNome().equals(body.getNome())) {
						throw FaultCode.CONFLITTO.toException("Proprietà " + body.getNome() + " già assegnata alla configurazione");
					}
				}
			}
			
			boolean found = false;
			if(pd.getProprietaList()!=null && !pd.getProprietaList().isEmpty()) {
				for (Proprieta p : pd.getProprietaList()) {
					if(p.getNome().equals(proprieta)) {
						p.setNome(body.getNome());
						if(env.pdCore!=null && env.pdCore.getDriverBYOKUtilities()!=null && 
								body.isEncrypted()!=null && body.isEncrypted().booleanValue()) {
							p.setValore(env.pdCore.getDriverBYOKUtilities().wrap(body.getValore()));
						}
						else {
							if(body.getValore().length()>255) {
								throw FaultCode.RICHIESTA_NON_VALIDA.toException(CostantiControlStation.MESSAGGIO_ERRORE_VALORE_PROPRIETA_255);
							}
							p.setValore(body.getValore());
						}
						found = true;
						break;
					}
				}
			}
			
			if(!found) {
				throw FaultCode.NOT_FOUND.toException("Proprietà " + body.getNome() + " non presente nella configurazione");
			}
			
			env.pdCore.performUpdateOperation(env.userLogin, false, pd);
			context.getLogger().info("Invocazione completata con successo");
     
		}
		catch(jakarta.ws.rs.WebApplicationException e) {
			context.getLogger().error("Invocazione terminata con errore '4xx': %s",e, e.getMessage());
			throw e;
		}
		catch(Throwable e) {
			context.getLogger().error("Invocazione terminata con errore: %s",e, e.getMessage());
			throw FaultCode.ERRORE_INTERNO.toException(e);
		}
    }

    /**
     * Consente di modificare la configurazione relativa all&#x27;identificazione degli attributi per quanto concerne il controllo degli accessi
     *
     * Questa operazione consente di aggiornare la configurazione relativa all&#x27;identificazione degli attributi
     *
     */
	@Override
    public void updateFruizioneControlloAccessiIdentificazioneAttributi(ControlloAccessiIdentificazioneAttributi body, String erogatore, String nome, Integer versione, ProfiloEnum profilo, String soggetto, String gruppo, String tipoServizio) {
		IContext context = this.getContext();
		try {
			context.getLogger().info("Invocazione in corso ...");     

			AuthorizationManager.authorize(context, getAuthorizationConfig());
			context.getLogger().debug("Autorizzazione completata con successo");     
           
			BaseHelper.throwIfNull(body);

			final FruizioniConfEnv env = new FruizioniConfEnv(context.getServletRequest(), profilo, soggetto, context, erogatore, nome, versione, gruppo, tipoServizio );		
			final PortaDelegata newPd = env.pdCore.getPortaDelegata(env.idPd);
			final PortaDelegata oldPd = env.pdCore.getPortaDelegata(env.idPd);
			
			if (body.isAbilitato() && body.getAttributeAuthority()!=null && !body.getAttributeAuthority().isEmpty()) {
				if(newPd.getAttributeAuthorityList()==null) {
					newPd.setAttributeAuthorityList(new ArrayList<AttributeAuthority>());
				}
				else {
					newPd.getAttributeAuthorityList().clear();
				}
				for (ControlloAccessiAttributeAuthority controlloAccessiAttributeAuthority : body.getAttributeAuthority()) {
					
					GenericProperties gp = null;
					try {
						gp = env.confCore.getGenericProperties(controlloAccessiAttributeAuthority.getNome(), org.openspcoop2.pdd.core.token.Costanti.ATTRIBUTE_AUTHORITY, false);
					}catch(DriverConfigurazioneNotFound notFound) {}
					if(gp==null) {
						throw FaultCode.RICHIESTA_NON_VALIDA.toException("AttributeAuthority '"+controlloAccessiAttributeAuthority.getNome()+"' non esistente");
					}
					
					AttributeAuthority aa = new AttributeAuthority();
					aa.setNome(controlloAccessiAttributeAuthority.getNome());
					if(controlloAccessiAttributeAuthority.getAttributi()!=null && !controlloAccessiAttributeAuthority.getAttributi().isEmpty()) {
						aa.setAttributoList(controlloAccessiAttributeAuthority.getAttributi());
					}
					newPd.addAttributeAuthority(aa);
				}
			}
			else {
				if(newPd.getAttributeAuthorityList()!=null) {
					newPd.getAttributeAuthorityList().clear();
				}
				else {
					newPd.setAttributeAuthorityList(new ArrayList<AttributeAuthority>());
				}
			}
			
			if ( !ErogazioniApiHelper.controlloAccessiCheckPD(env, oldPd, newPd)) {
				throw FaultCode.RICHIESTA_NON_VALIDA.toException(env.pd.getMessage());
			}
			
			env.pdCore.performUpdateOperation(env.userLogin, false, newPd);
			context.getLogger().info("Invocazione completata con successo");
		}
		catch(jakarta.ws.rs.WebApplicationException e) {
			context.getLogger().error("Invocazione terminata con errore '4xx': %s",e, e.getMessage());
			throw e;
		}
		catch(Throwable e) {
			context.getLogger().error("Invocazione terminata con errore: %s",e, e.getMessage());
			throw FaultCode.ERRORE_INTERNO.toException(e);
		}
    }
    

    /**
     * Consente di modificare la configurazione CORS associata alla fruizione
     *
     * Questa operazione consente di aggiornare la configurazione CORS associata alla fruizione identificata dall'erogatore, dal nome e dalla versione
     *
     */
	@Override
    public void updateFruizioneGestioneCORS(GestioneCors body, String erogatore, String nome, Integer versione, ProfiloEnum profilo, String soggetto, String tipoServizio) {
		IContext context = this.getContext();
		try {
			context.getLogger().info("Invocazione in corso ...");     

			AuthorizationManager.authorize(context, getAuthorizationConfig());
			context.getLogger().debug("Autorizzazione completata con successo");     
                        
			BaseHelper.throwIfNull(body);	
			  
			final ErogazioniEnv env = new ErogazioniEnv(context.getServletRequest(), profilo, soggetto, context);
			final IDSoggetto idErogatore = new IDSoggetto(env.tipo_soggetto, erogatore);
			final AccordoServizioParteSpecifica asps = BaseHelper.supplyOrNotFound( 
					() -> ErogazioniApiHelper.getServizioIfFruizione(tipoServizio, nome, versione, idErogatore, env.idSoggetto.toIDSoggetto(),  env),
					"Fruizione"
				);
			final IdServizio idAsps = new IdServizio(env.idServizioFactory.getIDServizioFromAccordo(asps), asps.getId());
			final IDPortaDelegata idPd =  ErogazioniApiHelper.getIDGruppoPDDefault(env.idSoggetto.toIDSoggetto(), idAsps, env.apsCore);
			final PortaDelegata pd = env.pdCore.getPortaDelegata(idPd);
			final CorsConfigurazione oldConf = pd.getGestioneCors();
			
			if (body.isRidefinito())
				pd.setGestioneCors(ErogazioniApiHelper.buildCorsConfigurazione(body, env, oldConf));
			else
				pd.setGestioneCors(null);
			
			env.pdCore.performUpdateOperation(env.userLogin, false, pd);
			
			context.getLogger().info("Invocazione completata con successo");
		}
		catch(jakarta.ws.rs.WebApplicationException e) {
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
    public void updateFruizioneRateLimitingPolicy(RateLimitingPolicyFruizioneUpdate body, String erogatore, String nome, Integer versione, String idPolicy, ProfiloEnum profilo, String soggetto, String gruppo, String tipoServizio) {
		IContext context = this.getContext();
		try {
			context.getLogger().info("Invocazione in corso ...");     

			AuthorizationManager.authorize(context, getAuthorizationConfig());
			context.getLogger().debug("Autorizzazione completata con successo");     
                        
			BaseHelper.throwIfNull(body);
			final FruizioniConfEnv env = new FruizioniConfEnv(context.getServletRequest(), profilo, soggetto, context, erogatore, nome, versione, gruppo, tipoServizio );		
			final PortaDelegata pd = env.pdCore.getPortaDelegata(env.idPd);
			
			final IDSoggetto idErogatore = new IDSoggetto(env.tipo_soggetto, erogatore);
			final AccordoServizioParteSpecifica asps = BaseHelper.supplyOrNotFound(() -> ErogazioniApiHelper
					.getServizioIfFruizione(tipoServizio, nome, versione, idErogatore, env.idSoggetto.toIDSoggetto(), env),
					"Fruizione");
			final AccordoServizioParteComuneSintetico apc = env.apcCore.getAccordoServizioSintetico(asps.getIdAccordo());
			
			AttivazionePolicy policy = BaseHelper.supplyOrNotFound( 
					() -> env.confCore.getAttivazionePolicy(idPolicy, RuoloPolicy.DELEGATA, pd.getNome()),
					"Rate Limiting Policy con nome " + idPolicy
				);
			if ( policy == null ) 
				throw FaultCode.NOT_FOUND.toException("Nessuna policy di rate limiting con nome " + idPolicy );
			
			InfoPolicy infoPolicy = env.confCore.getInfoPolicy(policy.getIdPolicy());
			ErogazioniApiHelper.override(infoPolicy.getTipoRisorsa(), body, env.protocolFactory.getProtocol(),  env.idSoggetto.toIDSoggetto(), env.requestWrapper);

			String errorAttivazione = env.confHelper.readDatiAttivazionePolicyFromHttpParameters(policy, false, TipoOperazione.CHANGE, infoPolicy);
			if ( !StringUtils.isEmpty(errorAttivazione) ) {
				throw FaultCode.RICHIESTA_NON_VALIDA.toException(StringEscapeUtils.unescapeHtml4(errorAttivazione));
			}
			
			String modalita = ErogazioniApiHelper.getDataElementModalita(infoPolicy.isBuiltIn());
			FruizioniConfigurazioneHelper.attivazionePolicyCheckData(TipoOperazione.CHANGE, pd, policy,infoPolicy, env, env.apcCore.toMessageServiceBinding(apc.getServiceBinding()), modalita);
			
			env.confCore.performUpdateOperation(env.userLogin, false, policy);
			
			context.getLogger().info("Invocazione completata con successo");
		}
		catch(jakarta.ws.rs.WebApplicationException e) {
			context.getLogger().error_except404("Invocazione terminata con errore '4xx': %s",e, e.getMessage());
			throw e;
		}
		catch(Throwable e) {
			context.getLogger().error("Invocazione terminata con errore: %s",e, e.getMessage());
			throw FaultCode.ERRORE_INTERNO.toException(e);
		}
    }
    
    /**
     * Consente di modificare la configurazione relativa alla registrazione dei diagnostici
     *
     * Questa operazione consente di aggiornare la configurazione relativa alla registrazione dei diagnostici
     *
     */
	@Override
    public void updateFruizioneRegistrazioneDiagnostici(RegistrazioneDiagnosticiConfigurazione body, String erogatore, String nome, Integer versione, ProfiloEnum profilo, String soggetto, String gruppo, String tipoServizio) {
		IContext context = this.getContext();
		try {
			context.getLogger().info("Invocazione in corso ...");     

			AuthorizationManager.authorize(context, getAuthorizationConfig());
			context.getLogger().debug("Autorizzazione completata con successo");     
                        
			BaseHelper.throwIfNull(body);	

			final FruizioniConfEnv env = new FruizioniConfEnv(context.getServletRequest(), profilo, soggetto, context, erogatore, nome, versione, gruppo, tipoServizio );		
			final PortaDelegata pd = env.pdCore.getPortaDelegata(env.idPd);
			
			ErogazioniApiHelper.updateTracciamento(body, pd, env);
			
			env.pdCore.performUpdateOperation(env.userLogin, false, pd);
			
			context.getLogger().info("Invocazione completata con successo");
        
		}
		catch(jakarta.ws.rs.WebApplicationException e) {
			context.getLogger().error("Invocazione terminata con errore '4xx': %s",e, e.getMessage());
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
    public void updateFruizioneRegistrazioneMessaggi(RegistrazioneMessaggi body, String erogatore, String nome, Integer versione, ProfiloEnum profilo, String soggetto, String gruppo, String tipoServizio) {
		IContext context = this.getContext();
		try {
			context.getLogger().info("Invocazione in corso ...");     

			AuthorizationManager.authorize(context, getAuthorizationConfig());
			context.getLogger().debug("Autorizzazione completata con successo");     
			
			BaseHelper.throwIfNull(body);	

			final FruizioniConfEnv env = new FruizioniConfEnv(context.getServletRequest(), profilo, soggetto, context, erogatore, nome, versione, gruppo, tipoServizio );		
			final PortaDelegata pd = env.pdCore.getPortaDelegata(env.idPd);
			
			if (body.isRidefinito())
				pd.setDump(ErogazioniApiHelper.buildDumpConfigurazione(body, false, env));
			else
				pd.setDump(null);
			
			env.pdCore.performUpdateOperation(env.userLogin, false, pd);
        
			context.getLogger().info("Invocazione completata con successo");             
		}
		catch(jakarta.ws.rs.WebApplicationException e) {
			context.getLogger().error_except404("Invocazione terminata con errore '4xx': %s",e, e.getMessage());
			throw e;
		}
		catch(Throwable e) {
			context.getLogger().error("Invocazione terminata con errore: %s",e, e.getMessage());
			throw FaultCode.ERRORE_INTERNO.toException(e);
		}
    }
    
    /**
     * Consente di modificare la configurazione relativa alla registrazione delle transazioni
     *
     * Questa operazione consente di aggiornare la configurazione relativa alla registrazione delle transazioni
     *
     */
	@Override
    public void updateFruizioneRegistrazioneTransazioni(RegistrazioneTransazioniConfigurazione body, String erogatore, String nome, Integer versione, ProfiloEnum profilo, String soggetto, String gruppo, String tipoServizio) {
		IContext context = this.getContext();
		try {
			context.getLogger().info("Invocazione in corso ...");     

			AuthorizationManager.authorize(context, getAuthorizationConfig());
			context.getLogger().debug("Autorizzazione completata con successo");     
                        
			BaseHelper.throwIfNull(body);	

			final FruizioniConfEnv env = new FruizioniConfEnv(context.getServletRequest(), profilo, soggetto, context, erogatore, nome, versione, gruppo, tipoServizio );		
			final PortaDelegata pd = env.pdCore.getPortaDelegata(env.idPd);
			
			ErogazioniApiHelper.updateTracciamento(body, pd, env);
			
			env.pdCore.performUpdateOperation(env.userLogin, false, pd);
        
			context.getLogger().info("Invocazione completata con successo");
        
		}
		catch(jakarta.ws.rs.WebApplicationException e) {
			context.getLogger().error("Invocazione terminata con errore '4xx': %s",e, e.getMessage());
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
    public void updateFruizioneStato(ApiImplStato body, String erogatore, String nome, Integer versione, ProfiloEnum profilo, String soggetto, String gruppo, String tipoServizio) {
		IContext context = this.getContext();
		try {
			context.getLogger().info("Invocazione in corso ...");     

			AuthorizationManager.authorize(context, getAuthorizationConfig());
			context.getLogger().debug("Autorizzazione completata con successo");     
                           
			BaseHelper.throwIfNull(body);	

			final FruizioniConfEnv env = new FruizioniConfEnv(context.getServletRequest(), profilo, soggetto, context, erogatore, nome, versione, gruppo, tipoServizio );		
			final PortaDelegata pd = env.pdCore.getPortaDelegata(env.idPd);
			
			IDPortaDelegata oldIDPortaDelegataForUpdate= new IDPortaDelegata();
			oldIDPortaDelegataForUpdate.setNome(pd.getNome());
			pd.setOldIDPortaDelegataForUpdate(oldIDPortaDelegataForUpdate);
			
			pd.setStato(Helper.boolToStatoFunzionalitaConf(body.isAbilitato()));
						
			env.pdCore.performUpdateOperation(env.userLogin, false, pd);
				
			context.getLogger().info("Invocazione completata con successo");
        
     
		}
		catch(jakarta.ws.rs.WebApplicationException e) {
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
    public void updateFruizioneTracciamentoCorrelazioneApplicativaRichiesta(CorrelazioneApplicativaRichiesta body, String erogatore, String nome, Integer versione, String elemento, ProfiloEnum profilo, String soggetto, String gruppo, String tipoServizio) {
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

			final FruizioniConfEnv env = new FruizioniConfEnv(context.getServletRequest(), profilo, soggetto, context, erogatore, nome, versione, gruppo, tipoServizio );		
			final PortaDelegata pd = env.pdCore.getPortaDelegata(env.idPd);   
			final Long idPorta = pd.getId();
			
			final IDSoggetto idErogatore = new IDSoggetto(env.tipo_soggetto, erogatore);
			final AccordoServizioParteSpecifica asps = BaseHelper.supplyOrNotFound(() -> ErogazioniApiHelper
					.getServizioIfFruizione(tipoServizio, nome, versione, idErogatore, env.idSoggetto.toIDSoggetto(), env),
					"Fruizione");
			final AccordoServizioParteComuneSintetico apc = env.apcCore.getAccordoServizioSintetico(asps.getIdAccordo());
			ServiceBinding serviceBinding = env.apcCore.toMessageServiceBinding(apc.getServiceBinding());
			
			if (pd.getCorrelazioneApplicativa() == null)
				pd.setCorrelazioneApplicativa(new org.openspcoop2.core.config.CorrelazioneApplicativa());
			
			final List<CorrelazioneApplicativaElemento> correlazioni = pd.getCorrelazioneApplicativa().getElementoList();
			final CorrelazioneApplicativaElemento oldElem = BaseHelper.findAndRemoveFirst(correlazioni, c -> (c.getNome()==null ? "" : c.getNome()).equals(searchElemento));
			
			if ( oldElem == null ) 
				throw FaultCode.NOT_FOUND.toException("Correlazione Applicativa Richiesta per l'elemento " + elemento + " non trovata ");
			
			if ( !correlazioneApplicativaRichiestaCheckData(TipoOperazione.CHANGE, env.requestWrapper, env.pdHelper, true, body, idPorta, oldElem.getId(),
					serviceBinding)) {
				throw FaultCode.RICHIESTA_NON_VALIDA.toException(StringEscapeUtils.unescapeHtml4(env.pd.getMessage()));
			}
			
			correlazioni.add(convert(body));
                        
			pd.getCorrelazioneApplicativa().setElementoList(correlazioni);
			
			env.pdCore.performUpdateOperation(env.userLogin, false, pd);
			
			
			context.getLogger().info("Invocazione completata con successo");
        
     
		}
		catch(jakarta.ws.rs.WebApplicationException e) {
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
    public void updateFruizioneTracciamentoCorrelazioneApplicativaRisposta(CorrelazioneApplicativaRisposta body, String erogatore, String nome, Integer versione, String elemento, ProfiloEnum profilo, String soggetto, String gruppo, String tipoServizio) {
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

			final FruizioniConfEnv env = new FruizioniConfEnv(context.getServletRequest(), profilo, soggetto, context, erogatore, nome, versione, gruppo, tipoServizio );		
			final PortaDelegata pd = env.pdCore.getPortaDelegata(env.idPd);   
			final Long idPorta = pd.getId();

			final IDSoggetto idErogatore = new IDSoggetto(env.tipo_soggetto, erogatore);
			final AccordoServizioParteSpecifica asps = BaseHelper.supplyOrNotFound(() -> ErogazioniApiHelper
					.getServizioIfFruizione(tipoServizio, nome, versione, idErogatore, env.idSoggetto.toIDSoggetto(), env),
					"Fruizione");
			final AccordoServizioParteComuneSintetico apc = env.apcCore.getAccordoServizioSintetico(asps.getIdAccordo());
			ServiceBinding serviceBinding = env.apcCore.toMessageServiceBinding(apc.getServiceBinding());
			
			if (pd.getCorrelazioneApplicativaRisposta() == null)
				pd.setCorrelazioneApplicativaRisposta(new org.openspcoop2.core.config.CorrelazioneApplicativaRisposta());
			
			final List<CorrelazioneApplicativaRispostaElemento> correlazioni = pd.getCorrelazioneApplicativaRisposta().getElementoList();
			final CorrelazioneApplicativaRispostaElemento oldElem = BaseHelper.findAndRemoveFirst(correlazioni, c -> (c.getNome()==null ? "" : c.getNome()).equals(searchElemento));
			
			if ( oldElem == null ) 
				throw FaultCode.NOT_FOUND.toException("Correlazione Applicativa Risposta per l'elemento " + elemento + " non trovata ");
			
			if ( !correlazioneApplicativaRispostaCheckData(TipoOperazione.CHANGE, env.requestWrapper, env.pdHelper, true, body, idPorta, oldElem.getId(),
					serviceBinding)) {
				throw FaultCode.RICHIESTA_NON_VALIDA.toException(StringEscapeUtils.unescapeHtml4(env.pd.getMessage()));
			}
			
			correlazioni.add(convert(body));
                        
			pd.getCorrelazioneApplicativaRisposta().setElementoList(correlazioni);
			
			env.pdCore.performUpdateOperation(env.userLogin, false, pd);
			        
			context.getLogger().info("Invocazione completata con successo");
        
		}
		catch(jakarta.ws.rs.WebApplicationException e) {
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
    public void updateFruizioneValidazione(Validazione body, String erogatore, String nome, Integer versione, ProfiloEnum profilo, String soggetto, String gruppo, String tipoServizio) {
		IContext context = this.getContext();
		try {
			context.getLogger().info("Invocazione in corso ...");     

			AuthorizationManager.authorize(context, getAuthorizationConfig());
			context.getLogger().debug("Autorizzazione completata con successo");     
             
			BaseHelper.throwIfNull(body);	

			final FruizioniConfEnv env = new FruizioniConfEnv(context.getServletRequest(), profilo, soggetto, context, erogatore, nome, versione, gruppo, tipoServizio );		
			final PortaDelegata pd = env.pdCore.getPortaDelegata(env.idPd);
			
			// ============================================
			final String stato = BaseHelper.evalnull( () -> body.getStato().toString());
			final String tipoValidazione = BaseHelper.evalnull( () -> body.getTipo().toString() );
			
			env.requestWrapper.overrideParameter(CostantiControlStation.PARAMETRO_PORTE_XSD, stato);
			
			if (!env.pdHelper.validazioneContenutiCheck(TipoOperazione.OTHER, true)) {
				throw FaultCode.RICHIESTA_NON_VALIDA.toException(StringEscapeUtils.unescapeHtml4(env.pd.getMessage()));
			}
			
			final ValidazioneContenutiApplicativi vx = new ValidazioneContenutiApplicativi();
			
			// Imposto Mtom al valore eventualmente già presente nel db.
			vx.setAcceptMtomMessage( BaseHelper.evalnull( () -> pd.getValidazioneContenutiApplicativi().getAcceptMtomMessage()) );
			vx.setStato( StatoFunzionalitaConWarning.toEnumConstant(stato) );
			vx.setTipo( ValidazioneContenutiApplicativiTipo.toEnumConstant(tipoValidazione) );
			
			pd.setValidazioneContenutiApplicativi(vx);
			// ============================================

			env.pdCore.performUpdateOperation(env.userLogin, false, pd);        
			context.getLogger().info("Invocazione completata con successo");
        
     
		}
		catch(jakarta.ws.rs.WebApplicationException e) {
			context.getLogger().error_except404("Invocazione terminata con errore '4xx': %s",e, e.getMessage());
			throw e;
		}
		catch(Throwable e) {
			context.getLogger().error("Invocazione terminata con errore: %s",e, e.getMessage());
			throw FaultCode.ERRORE_INTERNO.toException(e);
		}
    }
    
}

