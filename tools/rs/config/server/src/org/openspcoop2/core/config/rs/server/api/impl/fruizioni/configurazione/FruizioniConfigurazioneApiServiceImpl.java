/*
 * GovWay - A customizable API Gateway 
 * http://www.govway.org
 *
 * from the Link.it OpenSPCoop project codebase
 * 
 * Copyright (c) 2005-2019 Link.it srl (http://link.it).
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

import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang.StringEscapeUtils;
import org.openspcoop2.core.config.AutorizzazioneRuoli;
import org.openspcoop2.core.config.AutorizzazioneScope;
import org.openspcoop2.core.config.CorsConfigurazione;
import org.openspcoop2.core.config.GestioneToken;
import org.openspcoop2.core.config.PortaDelegata;
import org.openspcoop2.core.config.PortaDelegataServizioApplicativo;
import org.openspcoop2.core.config.ResponseCachingConfigurazione;
import org.openspcoop2.core.config.Ruolo;
import org.openspcoop2.core.config.Scope;
import org.openspcoop2.core.config.ServizioApplicativo;
import org.openspcoop2.core.config.ValidazioneContenutiApplicativi;
import org.openspcoop2.core.config.constants.StatoFunzionalitaConWarning;
import org.openspcoop2.core.config.constants.TipoAutenticazione;
import org.openspcoop2.core.config.constants.TipoAutorizzazione;
import org.openspcoop2.core.config.constants.ValidazioneContenutiApplicativiTipo;
import org.openspcoop2.core.config.rs.server.api.FruizioniConfigurazioneApi;
import org.openspcoop2.core.config.rs.server.api.impl.Enums;
import org.openspcoop2.core.config.rs.server.api.impl.Helper;
import org.openspcoop2.core.config.rs.server.api.impl.IdServizio;
import org.openspcoop2.core.config.rs.server.api.impl.erogazioni.ErogazioniApiHelper;
import org.openspcoop2.core.config.rs.server.api.impl.erogazioni.ErogazioniEnv;
import org.openspcoop2.core.config.rs.server.config.ServerProperties;
import org.openspcoop2.core.config.rs.server.model.APIImplAutenticazione;
import org.openspcoop2.core.config.rs.server.model.ApiImplStato;
import org.openspcoop2.core.config.rs.server.model.CachingRisposta;
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
import org.openspcoop2.core.config.rs.server.model.GestioneCors;
import org.openspcoop2.core.config.rs.server.model.ProfiloEnum;
import org.openspcoop2.core.config.rs.server.model.RegistrazioneMessaggi;
import org.openspcoop2.core.config.rs.server.model.StatoFunzionalitaConWarningEnum;
import org.openspcoop2.core.config.rs.server.model.TipoAutenticazioneEnum;
import org.openspcoop2.core.config.rs.server.model.Validazione;
import org.openspcoop2.core.id.IDPortaDelegata;
import org.openspcoop2.core.id.IDServizioApplicativo;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.core.registry.AccordoServizioParteSpecifica;
import org.openspcoop2.core.registry.constants.RuoloContesto;
import org.openspcoop2.core.registry.constants.RuoloTipologia;
import org.openspcoop2.core.registry.constants.ScopeContesto;
import org.openspcoop2.core.registry.driver.FiltroRicercaRuoli;
import org.openspcoop2.core.registry.driver.FiltroRicercaScope;
import org.openspcoop2.utils.service.BaseImpl;
import org.openspcoop2.utils.service.authorization.AuthorizationConfig;
import org.openspcoop2.utils.service.authorization.AuthorizationManager;
import org.openspcoop2.utils.service.context.IContext;
import org.openspcoop2.utils.service.fault.jaxrs.FaultCode;
import org.openspcoop2.web.ctrlstat.costanti.CostantiControlStation;
import org.openspcoop2.web.ctrlstat.servlet.pd.PorteDelegateCostanti;
import org.openspcoop2.web.ctrlstat.servlet.ruoli.RuoliCore;
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
     * Aggiunta di applicativi all'elenco degli applicativi autorizzati puntualmente
     *
     * Questa operazione consente di aggiungere applicativi all'elenco degli applicativi autorizzati puntualmente
     *
     */
	@Override
    public void addFruizioneControlloAccessiAutorizzazionePuntualeApplicativi(ControlloAccessiAutorizzazioneApplicativo body, String erogatore, String nome, Integer versione, ProfiloEnum profilo, String soggetto, String gruppo) {
		IContext context = this.getContext();
		try {
			context.getLogger().info("Invocazione in corso ...");     

			AuthorizationManager.authorize(context, getAuthorizationConfig());
			context.getLogger().debug("Autorizzazione completata con successo");
			
			Helper.throwIfNull(body);
			
			final FruizioniConfEnv env = new FruizioniConfEnv(context.getServletRequest(), profilo, soggetto, context, erogatore, nome, versione, gruppo);		
			final PortaDelegata pd = env.pdCore.getPortaDelegata(env.idPd);
			
			final IDServizioApplicativo idSA = new IDServizioApplicativo();
				idSA.setIdSoggettoProprietario(env.idSoggetto.toIDSoggetto());
				idSA.setNome(body.getApplicativo());
				
			if ( TipoAutorizzazione.toEnumConstant(pd.getAutorizzazione()) != TipoAutorizzazione.AUTHENTICATED) {
				throw FaultCode.RICHIESTA_NON_VALIDA.toException("L'autenticazione puntuale per soggetti non è abilitata");
			}
			
			final ServizioApplicativo sa = Helper.supplyOrNonValida( 
					() -> env.saCore.getServizioApplicativo(idSA),
					"Servizio Applicativo " + idSA.toString()
				);
			
			// Prendo la lista di servizi applicativi associati al soggetto
			final org.openspcoop2.core.config.constants.CredenzialeTipo tipoAutenticazione = org.openspcoop2.core.config.constants.CredenzialeTipo.toEnumConstant(pd.getAutenticazione());
			
			List<ServizioApplicativo> saCompatibili = env.saCore.soggettiServizioApplicativoList(env.idSoggetto.toIDSoggetto(),env.userLogin,tipoAutenticazione);
			if (!Helper.findFirst(saCompatibili, s -> s.getId() == sa.getId()).isPresent()) {
				throw FaultCode.RICHIESTA_NON_VALIDA.toException("La modalità di autenticazione del servizio Applicativo scelto non è compatibile con il gruppo che si vuole configurare");
			}

			if ( Helper.findFirst(
						pd.getServizioApplicativoList(),
						s -> s.getId() == sa.getId()
						).isPresent()
				) {
				throw FaultCode.CONFLITTO.toException("Servizio Applicativo già associato");
			}
					
			env.requestWrapper.overrideParameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID, pd.getId().toString());
			env.requestWrapper.overrideParameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID_SOGGETTO, env.idSoggetto.getId().toString());
			env.requestWrapper.overrideParameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_SERVIZIO_APPLICATIVO, sa.getNome());
			
			if (!env.pdHelper.porteDelegateServizioApplicativoCheckData(TipoOperazione.ADD)) {
				throw FaultCode.RICHIESTA_NON_VALIDA.toException(StringEscapeUtils.unescapeHtml(env.pd.getMessage()));
			}
			
			
			PortaDelegataServizioApplicativo pdSa = new PortaDelegataServizioApplicativo();
			pdSa.setNome(body.getApplicativo());
			pd.addServizioApplicativo(pdSa);
			
			env.pdCore.performUpdateOperation(env.userLogin, false, pd);
			
			context.getLogger().info("Invocazione completata con successo");
        
     
		}
		catch(javax.ws.rs.WebApplicationException e) {
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
    public void addFruizioneControlloAccessiAutorizzazioneRuoli(ControlloAccessiAutorizzazioneRuolo body, String erogatore, String nome, Integer versione, ProfiloEnum profilo, String soggetto, String gruppo) {
		IContext context = this.getContext();
		try {
			context.getLogger().info("Invocazione in corso ...");     

			AuthorizationManager.authorize(context, getAuthorizationConfig());
			context.getLogger().debug("Autorizzazione completata con successo");     

			final FruizioniConfEnv env = new FruizioniConfEnv(context.getServletRequest(), profilo, soggetto, context, erogatore, nome, versione, gruppo);		
			final PortaDelegata pd = env.pdCore.getPortaDelegata(env.idPd);
			
			// TODO: Sempre chiedi ad andrea come funge qui per determinare se i ruoli sono richiesti o meno.
			if ( !TipoAutorizzazione.isRolesRequired(pd.getAutorizzazione())) {
				throw FaultCode.RICHIESTA_NON_VALIDA.toException("L'autorizzazione per ruoli non è abilitata");
				
			}
			
			final RuoliCore ruoliCore = new RuoliCore(env.stationCore);
			Helper.supplyOrNonValida( 
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
			
			if ( Helper.findFirst( ruoliPresenti, r -> r.equals(body.getRuolo())).isPresent()) {
				throw FaultCode.CONFLITTO.toException("Il ruolo " + body.getRuolo() + " è già associato al gruppo scelto");
			}
			
			if (!env.paHelper.ruoloCheckData(TipoOperazione.ADD, body.getRuolo(), ruoliPresenti)) {
				throw FaultCode.RICHIESTA_NON_VALIDA.toException(StringEscapeUtils.unescapeHtml(env.pd.getMessage()));
			} 
			// ================================ CHECK RUOLI
			
			Ruolo ruolo = new Ruolo();
			ruolo.setNome(body.getRuolo());
			pd.getRuoli().addRuolo(ruolo);

			env.paCore.performUpdateOperation(env.userLogin, false, pd);

			context.getLogger().info("Invocazione completata con successo");
        
     
		}
		catch(javax.ws.rs.WebApplicationException e) {
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
    public void addFruizioneControlloAccessiAutorizzazioneScope(ControlloAccessiAutorizzazioneScope body, String erogatore, String nome, Integer versione, ProfiloEnum profilo, String soggetto, String gruppo) {
		IContext context = this.getContext();
		try {
			context.getLogger().info("Invocazione in corso ...");     

			AuthorizationManager.authorize(context, getAuthorizationConfig());
			context.getLogger().debug("Autorizzazione completata con successo");     

			final FruizioniConfEnv env = new FruizioniConfEnv(context.getServletRequest(), profilo, soggetto, context, erogatore, nome, versione, gruppo);		
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
				throw FaultCode.RICHIESTA_NON_VALIDA.toException(StringEscapeUtils.unescapeHtml(env.pd.getMessage()));
				
			}
			// ================= CHECK SCOPE
			
			Scope scope = new Scope();
			scope.setNome(body.getScope());
			pd.getScope().addScope(scope);
			
			env.paCore.performUpdateOperation(env.userLogin, false, pd);
		
			context.getLogger().info("Invocazione completata con successo");
     
		}
		catch(javax.ws.rs.WebApplicationException e) {
			context.getLogger().error("Invocazione terminata con errore '4xx': %s",e, e.getMessage());
			throw e;
		}
		catch(Throwable e) {
			context.getLogger().error("Invocazione terminata con errore: %s",e, e.getMessage());
			throw FaultCode.ERRORE_INTERNO.toException(e);
		}
    }
    
    /**
     * Elimina applicativi dall'elenco degli applicativi autorizzati puntualmente
     *
     * Questa operazione consente di eliminare applicativi dall'elenco degli applicativi autorizzati puntualmente
     *
     */
	@Override
    public void deleteFruizioneControlloAccessiAutorizzazionePuntualeApplicativi(String erogatore, String nome, Integer versione, String applicativoAutorizzato, ProfiloEnum profilo, String soggetto, String gruppo) {
		IContext context = this.getContext();
		try {
			context.getLogger().info("Invocazione in corso ...");     

			AuthorizationManager.authorize(context, getAuthorizationConfig());
			context.getLogger().debug("Autorizzazione completata con successo");       

			final FruizioniConfEnv env = new FruizioniConfEnv(context.getServletRequest(), profilo, soggetto, context, erogatore, nome, versione, gruppo);		
			final PortaDelegata pd = env.pdCore.getPortaDelegata(env.idPd);
			
			PortaDelegataServizioApplicativo to_remove = Helper.findAndRemoveFirst(pd.getServizioApplicativoList(), sa -> sa.getNome().equals(applicativoAutorizzato));
			
			if (env.delete_404 && to_remove == null) {
				throw FaultCode.NOT_FOUND.toException("Nessun Applicativo " + applicativoAutorizzato + " è associato al gruppo scelto"); 
			} else if ( to_remove != null ) {
			
				env.paCore.performUpdateOperation(env.userLogin, false, pd);
			}
			context.getLogger().info("Invocazione completata con successo");
        
     
		}
		catch(javax.ws.rs.WebApplicationException e) {
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
    public void deleteFruizioneControlloAccessiAutorizzazioneRuoli(String erogatore, String nome, Integer versione, String ruoloAutorizzato, ProfiloEnum profilo, String soggetto, String gruppo) {
		IContext context = this.getContext();
		try {
			context.getLogger().info("Invocazione in corso ...");     

			AuthorizationManager.authorize(context, getAuthorizationConfig());
			context.getLogger().debug("Autorizzazione completata con successo");     

			final FruizioniConfEnv env = new FruizioniConfEnv(context.getServletRequest(), profilo, soggetto, context, erogatore, nome, versione, gruppo);		
			final PortaDelegata pd = env.pdCore.getPortaDelegata(env.idPd);
			
			if (pd.getRuoli() == null)	pd.setRuoli(new AutorizzazioneRuoli());
			
			Ruolo to_remove = Helper.findAndRemoveFirst(pd.getRuoli().getRuoloList(), r -> r.getNome().equals(ruoloAutorizzato));
			
			if (env.delete_404 && to_remove == null) {
				throw FaultCode.NOT_FOUND.toException("Nessun Ruolo " + ruoloAutorizzato + " è associato al gruppo scelto"); 
			} else if ( to_remove != null ) {
			
				env.paCore.performUpdateOperation(env.userLogin, false, pd);
			}
			
			context.getLogger().info("Invocazione completata con successo");     
		}
		catch(javax.ws.rs.WebApplicationException e) {
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
    public void deleteFruizioneControlloAccessiAutorizzazioneScope(String erogatore, String nome, Integer versione, String scopeAutorizzato, ProfiloEnum profilo, String soggetto, String gruppo) {
		IContext context = this.getContext();
		try {
			context.getLogger().info("Invocazione in corso ...");     

			AuthorizationManager.authorize(context, getAuthorizationConfig());
			context.getLogger().debug("Autorizzazione completata con successo");     

			final FruizioniConfEnv env = new FruizioniConfEnv(context.getServletRequest(), profilo, soggetto, context, erogatore, nome, versione, gruppo);		
			final PortaDelegata pd = env.pdCore.getPortaDelegata(env.idPd);
			
			if (pd.getScope() == null)	pd.setScope(new AutorizzazioneScope());
			
			Scope to_remove = Helper.findAndRemoveFirst(pd.getScope().getScopeList(), s -> s.getNome().equals(scopeAutorizzato));
			
			if (env.delete_404 && to_remove == null) {
				throw FaultCode.NOT_FOUND.toException("Nessuno scope " + scopeAutorizzato + " è associato al gruppo scelto"); 
			} else if ( to_remove != null ) {
			
				env.paCore.performUpdateOperation(env.userLogin, false, pd);
			}
			
			env.paCore.performUpdateOperation(env.userLogin, false, pd);
        
			context.getLogger().info("Invocazione completata con successo");        
     
		}
		catch(javax.ws.rs.WebApplicationException e) {
			context.getLogger().error("Invocazione terminata con errore '4xx': %s",e, e.getMessage());
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
    public byte[] downloadFruizioneControlloAccessiAutorizzazioneXacmlPolicy(String erogatore, String nome, Integer versione, ProfiloEnum profilo, String soggetto, String gruppo) {
		IContext context = this.getContext();
		try {
			context.getLogger().info("Invocazione in corso ...");     

			AuthorizationManager.authorize(context, getAuthorizationConfig());
			context.getLogger().debug("Autorizzazione completata con successo");
			
			final FruizioniConfEnv env = new FruizioniConfEnv(context.getServletRequest(), profilo, soggetto, context, erogatore, nome, versione, gruppo);		
			final PortaDelegata pd = env.pdCore.getPortaDelegata(env.idPd);
			
			if (pd.getXacmlPolicy() == null)
				throw FaultCode.NOT_FOUND.toException("Xacml policy non assegnata al gruppo scelto");
                       
			context.getLogger().info("Invocazione completata con successo");
			return pd.getXacmlPolicy().getBytes();
     
		}
		catch(javax.ws.rs.WebApplicationException e) {
			context.getLogger().error("Invocazione terminata con errore '4xx': %s",e, e.getMessage());
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
    public CachingRisposta getFruizioneCachingRisposta(String erogatore, String nome, Integer versione, ProfiloEnum profilo, String soggetto, String gruppo) {
		IContext context = this.getContext();
		try {
			context.getLogger().info("Invocazione in corso ...");     

			AuthorizationManager.authorize(context, getAuthorizationConfig());
			context.getLogger().debug("Autorizzazione completata con successo");     
                       
			final FruizioniConfEnv env = new FruizioniConfEnv(context.getServletRequest(), profilo, soggetto, context, erogatore, nome, versione, gruppo);		
			final PortaDelegata pd = env.pdCore.getPortaDelegata(env.idPd);

			CachingRisposta ret = ErogazioniApiHelper.buildCachingRisposta(pd.getResponseCaching());
			context.getLogger().info("Invocazione completata con successo");
			return ret;
     
		}
		catch(javax.ws.rs.WebApplicationException e) {
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
    public ControlloAccessiAutenticazione getFruizioneControlloAccessiAutenticazione(String erogatore, String nome, Integer versione, ProfiloEnum profilo, String soggetto, String gruppo) {
		IContext context = this.getContext();
		try {
			context.getLogger().info("Invocazione in corso ...");     

			AuthorizationManager.authorize(context, getAuthorizationConfig());
			context.getLogger().debug("Autorizzazione completata con successo");
			
			final FruizioniConfEnv env = new FruizioniConfEnv(context.getServletRequest(), profilo, soggetto, context, erogatore, nome, versione, gruppo);		
			final PortaDelegata pd = env.pdCore.getPortaDelegata(env.idPd);
		
			final APIImplAutenticazione autRet = new APIImplAutenticazione();
			autRet.setOpzionale(Helper.statoFunzionalitaConfToBool( pd.getAutenticazioneOpzionale() ));
			
			if ( TipoAutenticazione.toEnumConstant(pd.getAutenticazione()) == null ) {
				autRet.setTipo(TipoAutenticazioneEnum.CUSTOM);
				autRet.setNome(pd.getAutenticazione());
			}
			else {
				autRet.setTipo( Enums.dualizeMap(Enums.tipoAutenticazioneFromRest).get(TipoAutenticazione.toEnumConstant(pd.getAutenticazione())) );
			}
						
			ControlloAccessiAutenticazioneToken token = Helper.evalnull( () -> pd.getGestioneToken().getAutenticazione() ) != null
					? ErogazioniApiHelper.fromGestioneTokenAutenticazione(pd.getGestioneToken().getAutenticazione())
					: null;
			
			ControlloAccessiAutenticazione ret = new ControlloAccessiAutenticazione();
			ret.setAutenticazione(autRet);
			ret.setToken(token);
                                
			context.getLogger().info("Invocazione completata con successo");
			return ret;
     
		}
		catch(javax.ws.rs.WebApplicationException e) {
			context.getLogger().error("Invocazione terminata con errore '4xx': %s",e, e.getMessage());
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
    public ControlloAccessiAutorizzazioneView getFruizioneControlloAccessiAutorizzazione(String erogatore, String nome, Integer versione, ProfiloEnum profilo, String soggetto, String gruppo) {
		IContext context = this.getContext();
		try {
			context.getLogger().info("Invocazione in corso ...");     

			AuthorizationManager.authorize(context, getAuthorizationConfig());
			context.getLogger().debug("Autorizzazione completata con successo");     
                        
			final FruizioniConfEnv env = new FruizioniConfEnv(context.getServletRequest(), profilo, soggetto, context, erogatore, nome, versione, gruppo);		
			final PortaDelegata pd = env.pdCore.getPortaDelegata(env.idPd);
			
			ControlloAccessiAutorizzazioneView ret = ErogazioniApiHelper.controlloAccessiAutorizzazioneFromPD(pd);
			
			context.getLogger().info("Invocazione completata con successo");		
			return ret;
     
		}
		catch(javax.ws.rs.WebApplicationException e) {
			context.getLogger().error("Invocazione terminata con errore '4xx': %s",e, e.getMessage());
			throw e;
		}
		catch(Throwable e) {
			context.getLogger().error("Invocazione terminata con errore: %s",e, e.getMessage());
			throw FaultCode.ERRORE_INTERNO.toException(e);
		}
    }
    
    /**
     * Restituisce l'elenco degli applicativi autorizzati puntualmente
     *
     * Questa operazione consente di ottenere l'elenco degli applicativi autorizzati puntualmente
     *
     */
	@Override
    public ControlloAccessiAutorizzazioneApplicativi getFruizioneControlloAccessiAutorizzazionePuntualeApplicativi(String erogatore, String nome, Integer versione, ProfiloEnum profilo, String soggetto, String gruppo) {
		IContext context = this.getContext();
		try {
			context.getLogger().info("Invocazione in corso ...");     

			AuthorizationManager.authorize(context, getAuthorizationConfig());
			context.getLogger().debug("Autorizzazione completata con successo");
			
			final FruizioniConfEnv env = new FruizioniConfEnv(context.getServletRequest(), profilo, soggetto, context, erogatore, nome, versione, gruppo);		
			final PortaDelegata pd = env.pdCore.getPortaDelegata(env.idPd);
	
			ControlloAccessiAutorizzazioneApplicativi ret = new ControlloAccessiAutorizzazioneApplicativi();
			ret.setApplicativi(pd.getServizioApplicativoList().stream().map( saPA -> saPA.getNome()).collect(Collectors.toList()));
		
			context.getLogger().info("Invocazione completata con successo");
			return ret;
     
     
		}
		catch(javax.ws.rs.WebApplicationException e) {
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
    public ControlloAccessiAutorizzazioneRuoli getFruizioneControlloAccessiAutorizzazioneRuoli(String erogatore, String nome, Integer versione, ProfiloEnum profilo, String soggetto, String gruppo) {
		IContext context = this.getContext();
		try {
			context.getLogger().info("Invocazione in corso ...");     

			AuthorizationManager.authorize(context, getAuthorizationConfig());
			context.getLogger().debug("Autorizzazione completata con successo");     
                        
			final FruizioniConfEnv env = new FruizioniConfEnv(context.getServletRequest(), profilo, soggetto, context, erogatore, nome, versione, gruppo);		
			final PortaDelegata pd = env.pdCore.getPortaDelegata(env.idPd);
			final ControlloAccessiAutorizzazioneRuoli ret = new ControlloAccessiAutorizzazioneRuoli(); 
            
			ret.setRuoli(Helper.evalnull( () -> pd.getRuoli().getRuoloList().stream().map(Ruolo::getNome).collect(Collectors.toList()) ));
        
			context.getLogger().info("Invocazione completata con successo");
        	return ret;
          
		}
		catch(javax.ws.rs.WebApplicationException e) {
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
    public ControlloAccessiAutorizzazioneScopes getFruizioneControlloAccessiAutorizzazioneScope(String erogatore, String nome, Integer versione, ProfiloEnum profilo, String soggetto, String gruppo) {
		IContext context = this.getContext();
		try {
			context.getLogger().info("Invocazione in corso ...");     

			AuthorizationManager.authorize(context, getAuthorizationConfig());
			context.getLogger().debug("Autorizzazione completata con successo");  
            
			final FruizioniConfEnv env = new FruizioniConfEnv(context.getServletRequest(), profilo, soggetto, context, erogatore, nome, versione, gruppo);		
			final PortaDelegata pd = env.pdCore.getPortaDelegata(env.idPd);
			final ControlloAccessiAutorizzazioneScopes ret = new ControlloAccessiAutorizzazioneScopes();

			ret.setScope(Helper.evalnull( () -> pd.getScope().getScopeList().stream().map(Scope::getNome).collect(Collectors.toList()) ));
			
			context.getLogger().info("Invocazione completata con successo");
			return ret;
     
		}
		catch(javax.ws.rs.WebApplicationException e) {
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
    public ControlloAccessiGestioneToken getFruizioneControlloAccessiGestioneToken(String erogatore, String nome, Integer versione, ProfiloEnum profilo, String soggetto, String gruppo) {
		IContext context = this.getContext();
		try {
			context.getLogger().info("Invocazione in corso ...");     

			AuthorizationManager.authorize(context, getAuthorizationConfig());
			context.getLogger().debug("Autorizzazione completata con successo");
			   
			final FruizioniConfEnv env = new FruizioniConfEnv(context.getServletRequest(), profilo, soggetto, context, erogatore, nome, versione, gruppo);		
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
		catch(javax.ws.rs.WebApplicationException e) {
			context.getLogger().error("Invocazione terminata con errore '4xx': %s",e, e.getMessage());
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
    public GestioneCors getFruizioneGestioneCORS(String erogatore, String nome, Integer versione, ProfiloEnum profilo, String soggetto) {
		IContext context = this.getContext();
		try {
			context.getLogger().info("Invocazione in corso ...");     

			AuthorizationManager.authorize(context, getAuthorizationConfig());
			context.getLogger().debug("Autorizzazione completata con successo");     
                       
			final ErogazioniEnv env = new ErogazioniEnv(context.getServletRequest(), profilo, soggetto, context);
			final IDSoggetto idErogatore = new IDSoggetto(env.tipo_soggetto, erogatore);
			final AccordoServizioParteSpecifica asps = Helper.supplyOrNotFound( 
					() -> ErogazioniApiHelper.getServizioIfFruizione(nome, versione, idErogatore, env.idSoggetto.toIDSoggetto(), env),
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
		catch(javax.ws.rs.WebApplicationException e) {
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
    public RegistrazioneMessaggi getFruizioneRegistrazioneMessaggi(String erogatore, String nome, Integer versione, ProfiloEnum profilo, String soggetto, String gruppo) {
		IContext context = this.getContext();
		try {
			context.getLogger().info("Invocazione in corso ...");     

			AuthorizationManager.authorize(context, getAuthorizationConfig());
			context.getLogger().debug("Autorizzazione completata con successo");     
                        
			final FruizioniConfEnv env = new FruizioniConfEnv(context.getServletRequest(), profilo, soggetto, context, erogatore, nome, versione, gruppo);		
			final PortaDelegata pd = env.pdCore.getPortaDelegata(env.idPd);
			final RegistrazioneMessaggi ret = ErogazioniApiHelper.fromDumpConfigurazione(pd.getDump());
			
			context.getLogger().info("Invocazione completata con successo");
			return ret;     
		}
		catch(javax.ws.rs.WebApplicationException e) {
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
    public ApiImplStato getFruizioneStato(String erogatore, String nome, Integer versione, ProfiloEnum profilo, String soggetto, String gruppo) {
		IContext context = this.getContext();
		try {
			context.getLogger().info("Invocazione in corso ...");     

			AuthorizationManager.authorize(context, getAuthorizationConfig());
			context.getLogger().debug("Autorizzazione completata con successo");     
                        
			final FruizioniConfEnv env = new FruizioniConfEnv(context.getServletRequest(), profilo, soggetto, context, erogatore, nome, versione, gruppo);		
			final PortaDelegata pd = env.pdCore.getPortaDelegata(env.idPd);
			
			final ApiImplStato ret = new ApiImplStato();
			ret.setAbilitato(Helper.statoFunzionalitaConfToBool(pd.getStato()));
                        
		
			context.getLogger().info("Invocazione completata con successo");
        	return ret;
		}
		catch(javax.ws.rs.WebApplicationException e) {
			context.getLogger().error("Invocazione terminata con errore '4xx': %s",e, e.getMessage());
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
    public Validazione getFruizioneValidazione(String erogatore, String nome, Integer versione, ProfiloEnum profilo, String soggetto, String gruppo) {
		IContext context = this.getContext();
		try {
			context.getLogger().info("Invocazione in corso ...");     

			AuthorizationManager.authorize(context, getAuthorizationConfig());
			context.getLogger().debug("Autorizzazione completata con successo");     
			
			final FruizioniConfEnv env = new FruizioniConfEnv(context.getServletRequest(), profilo, soggetto, context, erogatore, nome, versione, gruppo);		
			final PortaDelegata pd = env.pdCore.getPortaDelegata(env.idPd);
			final Validazione ret = ErogazioniApiHelper.fromValidazioneContenutiApplicativi(pd.getValidazioneContenutiApplicativi());
        
			context.getLogger().info("Invocazione completata con successo");
			return ret;
     
		}
		catch(javax.ws.rs.WebApplicationException e) {
			context.getLogger().error("Invocazione terminata con errore '4xx': %s",e, e.getMessage());
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
    public void updateFruizioneCachingRisposta(CachingRisposta body, String erogatore, String nome, Integer versione, ProfiloEnum profilo, String soggetto, String gruppo) {
		IContext context = this.getContext();
		try {
			context.getLogger().info("Invocazione in corso ...");     

			AuthorizationManager.authorize(context, getAuthorizationConfig());
			context.getLogger().debug("Autorizzazione completata con successo");
			
			Helper.throwIfNull(body);
			
			final FruizioniConfEnv env = new FruizioniConfEnv(context.getServletRequest(), profilo, soggetto, context, erogatore, nome, versione, gruppo);		
			final PortaDelegata pd = env.pdCore.getPortaDelegata(env.idPd);
			
			if (!env.paHelper.checkDataConfigurazioneResponseCachingPorta(TipoOperazione.OTHER, true, body.getStato().toString())) {
				throw FaultCode.RICHIESTA_NON_VALIDA.toException(StringEscapeUtils.unescapeHtml(env.pd.getMessage()));
			}
			
			ResponseCachingConfigurazione newConfigurazione = ErogazioniApiHelper.buildResponseCachingConfigurazione(body, env.paHelper);
			
			pd.setResponseCaching(newConfigurazione);
			env.pdCore.performUpdateOperation(env.userLogin, false, pd);			
			

			context.getLogger().info("Invocazione completata con successo");       
     
		}
		catch(javax.ws.rs.WebApplicationException e) {
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
    public void updateFruizioneControlloAccessiAutenticazione(ControlloAccessiAutenticazione body, String erogatore, String nome, Integer versione, ProfiloEnum profilo, String soggetto, String gruppo) {
		IContext context = this.getContext();
		try {
			context.getLogger().info("Invocazione in corso ...");     

			AuthorizationManager.authorize(context, getAuthorizationConfig());
			context.getLogger().debug("Autorizzazione completata con successo");
			
			Helper.throwIfNull(body);
                        
			final FruizioniConfEnv env = new FruizioniConfEnv(context.getServletRequest(), profilo, soggetto, context, erogatore, nome, versione, gruppo);		
			final PortaDelegata oldPd = env.pdCore.getPortaDelegata(env.idPd);
			final PortaDelegata newPd = env.pdCore.getPortaDelegata(env.idPd);
			
			ErogazioniApiHelper.fillPortaDelegata(body, newPd);
			
			if (! ErogazioniApiHelper.controlloAccessiCheckPD(env, oldPd, newPd)) {
				throw FaultCode.RICHIESTA_NON_VALIDA.toException(StringEscapeUtils.unescapeHtml(  env.pd.getMessage() ));
			}
		
			
			env.paCore.performUpdateOperation(env.userLogin, false, newPd);
			
			context.getLogger().info("Invocazione completata con successo");
        
     
		}
		catch(javax.ws.rs.WebApplicationException e) {
			context.getLogger().error("Invocazione terminata con errore '4xx': %s",e, e.getMessage());
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
    public void updateFruizioneControlloAccessiAutorizzazione(ControlloAccessiAutorizzazione body, String erogatore, String nome, Integer versione, ProfiloEnum profilo, String soggetto, String gruppo) {
		IContext context = this.getContext();
		try {
			context.getLogger().info("Invocazione in corso ...");     

			AuthorizationManager.authorize(context, getAuthorizationConfig());
			context.getLogger().debug("Autorizzazione completata con successo");
			
			Helper.throwIfNull(body);
                        
			final FruizioniConfEnv env = new FruizioniConfEnv(context.getServletRequest(), profilo, soggetto, context, erogatore, nome, versione, gruppo);		
			final PortaDelegata pd = env.pdCore.getPortaDelegata(env.idPd);
			final PortaDelegata newPd = env.pdCore.getPortaDelegata(env.idPd);
			
			ErogazioniApiHelper.fillPortaDelegata(body, newPd);
			
			if (!ErogazioniApiHelper.controlloAccessiCheckPD(env, pd, newPd)) {
				throw FaultCode.RICHIESTA_NON_VALIDA.toException(StringEscapeUtils.unescapeHtml(  env.pd.getMessage() ));
			}
			
			env.paCore.performUpdateOperation(env.userLogin, false, newPd);
                        
			context.getLogger().info("Invocazione completata con successo");        
     
		}
		catch(javax.ws.rs.WebApplicationException e) {
			context.getLogger().error("Invocazione terminata con errore '4xx': %s",e, e.getMessage());
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
    public void updateFruizioneControlloAccessiGestioneToken(ControlloAccessiGestioneToken body, String erogatore, String nome, Integer versione, ProfiloEnum profilo, String soggetto, String gruppo) {
		IContext context = this.getContext();
		try {
			context.getLogger().info("Invocazione in corso ...");     

			AuthorizationManager.authorize(context, getAuthorizationConfig());
			context.getLogger().debug("Autorizzazione completata con successo");
			
			Helper.throwIfNull(body);

			final FruizioniConfEnv env = new FruizioniConfEnv(context.getServletRequest(), profilo, soggetto, context, erogatore, nome, versione, gruppo);		
			final PortaDelegata newPd = env.pdCore.getPortaDelegata(env.idPd);
			final PortaDelegata oldPd = env.pdCore.getPortaDelegata(env.idPd);
			
			if (body.isAbilitato())
				newPd.setGestioneToken(ErogazioniApiHelper.buildGestioneToken(body, newPd, true, env.paHelper, env));
			else
				newPd.setGestioneToken(null);
			
			if ( !ErogazioniApiHelper.controlloAccessiCheckPD(env, oldPd, newPd)) {
				throw FaultCode.RICHIESTA_NON_VALIDA.toException(env.pd.getMessage());
			}
			
			env.paCore.performUpdateOperation(env.userLogin, false, newPd);
			context.getLogger().info("Invocazione completata con successo");
        
     
		}
		catch(javax.ws.rs.WebApplicationException e) {
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
    public void updateFruizioneGestioneCORS(GestioneCors body, String erogatore, String nome, Integer versione, ProfiloEnum profilo, String soggetto) {
		IContext context = this.getContext();
		try {
			context.getLogger().info("Invocazione in corso ...");     

			AuthorizationManager.authorize(context, getAuthorizationConfig());
			context.getLogger().debug("Autorizzazione completata con successo");     
                        
			Helper.throwIfNull(body);	
			  
			final ErogazioniEnv env = new ErogazioniEnv(context.getServletRequest(), profilo, soggetto, context);
			final IDSoggetto idErogatore = new IDSoggetto(env.tipo_soggetto, erogatore);
			final AccordoServizioParteSpecifica asps = Helper.supplyOrNotFound( 
					() -> ErogazioniApiHelper.getServizioIfFruizione(nome, versione, idErogatore, env.idSoggetto.toIDSoggetto(),  env),
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
		catch(javax.ws.rs.WebApplicationException e) {
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
    public void updateFruizioneRegistrazioneMessaggi(RegistrazioneMessaggi body, String erogatore, String nome, Integer versione, ProfiloEnum profilo, String soggetto, String gruppo) {
		IContext context = this.getContext();
		try {
			context.getLogger().info("Invocazione in corso ...");     

			AuthorizationManager.authorize(context, getAuthorizationConfig());
			context.getLogger().debug("Autorizzazione completata con successo");     
			
			Helper.throwIfNull(body);	

			final FruizioniConfEnv env = new FruizioniConfEnv(context.getServletRequest(), profilo, soggetto, context, erogatore, nome, versione, gruppo);		
			final PortaDelegata pd = env.pdCore.getPortaDelegata(env.idPd);
			
			if (body.isRidefinito())
				pd.setDump(ErogazioniApiHelper.buildDumpConfigurazione(body, false, env));
			else
				pd.setDump(null);
			
			env.paCore.performUpdateOperation(env.userLogin, false, pd);
        
			context.getLogger().info("Invocazione completata con successo");             
		}
		catch(javax.ws.rs.WebApplicationException e) {
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
    public void updateFruizioneStato(ApiImplStato body, String erogatore, String nome, Integer versione, ProfiloEnum profilo, String soggetto, String gruppo) {
		IContext context = this.getContext();
		try {
			context.getLogger().info("Invocazione in corso ...");     

			AuthorizationManager.authorize(context, getAuthorizationConfig());
			context.getLogger().debug("Autorizzazione completata con successo");     
                           
			Helper.throwIfNull(body);	

			final FruizioniConfEnv env = new FruizioniConfEnv(context.getServletRequest(), profilo, soggetto, context, erogatore, nome, versione, gruppo);		
			final PortaDelegata pd = env.pdCore.getPortaDelegata(env.idPd);
			
			IDPortaDelegata oldIDPortaDelegataForUpdate= new IDPortaDelegata();
			oldIDPortaDelegataForUpdate.setNome(pd.getNome());
			pd.setOldIDPortaDelegataForUpdate(oldIDPortaDelegataForUpdate);
			
			pd.setStato(Helper.boolToStatoFunzionalitaConf(body.isAbilitato()));
						
			env.paCore.performUpdateOperation(env.userLogin, false, pd);
				
			context.getLogger().info("Invocazione completata con successo");
        
     
		}
		catch(javax.ws.rs.WebApplicationException e) {
			context.getLogger().error("Invocazione terminata con errore '4xx': %s",e, e.getMessage());
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
    public void updateFruizioneValidazione(Validazione body, String erogatore, String nome, Integer versione, ProfiloEnum profilo, String soggetto, String gruppo) {
		IContext context = this.getContext();
		try {
			context.getLogger().info("Invocazione in corso ...");     

			AuthorizationManager.authorize(context, getAuthorizationConfig());
			context.getLogger().debug("Autorizzazione completata con successo");     
             
			Helper.throwIfNull(body);	

			final FruizioniConfEnv env = new FruizioniConfEnv(context.getServletRequest(), profilo, soggetto, context, erogatore, nome, versione, gruppo);		
			final PortaDelegata pd = env.pdCore.getPortaDelegata(env.idPd);
			
			// ============================================
			final String stato = Helper.evalnull( () -> body.getStato().toString());
			final String tipoValidazione = Helper.evalnull( () -> body.getTipo().toString() );
			
			env.requestWrapper.overrideParameter(CostantiControlStation.PARAMETRO_PORTE_XSD, stato);
			
			if (!env.pdHelper.validazioneContenutiCheck(TipoOperazione.OTHER, true)) {
				throw FaultCode.RICHIESTA_NON_VALIDA.toException(StringEscapeUtils.unescapeHtml(env.pd.getMessage()));
			}
			
			final ValidazioneContenutiApplicativi vx = new ValidazioneContenutiApplicativi();
			
			// Imposto Mtom al valore eventualmente già presente nel db.
			vx.setAcceptMtomMessage( Helper.evalnull( () -> pd.getValidazioneContenutiApplicativi().getAcceptMtomMessage()) );
			vx.setStato( StatoFunzionalitaConWarning.toEnumConstant(stato) );
			vx.setTipo( ValidazioneContenutiApplicativiTipo.toEnumConstant(tipoValidazione) );
			
			pd.setValidazioneContenutiApplicativi(vx);
			// ============================================

			env.paCore.performUpdateOperation(env.userLogin, false, pd);        
			context.getLogger().info("Invocazione completata con successo");
        
     
		}
		catch(javax.ws.rs.WebApplicationException e) {
			context.getLogger().error("Invocazione terminata con errore '4xx': %s",e, e.getMessage());
			throw e;
		}
		catch(Throwable e) {
			context.getLogger().error("Invocazione terminata con errore: %s",e, e.getMessage());
			throw FaultCode.ERRORE_INTERNO.toException(e);
		}
    }
    
}

