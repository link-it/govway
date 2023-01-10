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
package org.openspcoop2.core.config.rs.server.api.impl.fruizioni.gruppi;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;
import org.openspcoop2.core.commons.Filtri;
import org.openspcoop2.core.commons.Liste;
import org.openspcoop2.core.config.PortaDelegata;
import org.openspcoop2.core.config.constants.TipoAutenticazionePrincipal;
import org.openspcoop2.core.config.rs.server.api.FruizioniGruppiApi;
import org.openspcoop2.core.config.rs.server.api.impl.Enums;
import org.openspcoop2.core.config.rs.server.api.impl.Helper;
import org.openspcoop2.core.config.rs.server.api.impl.IdServizio;
import org.openspcoop2.core.config.rs.server.api.impl.erogazioni.ErogazioniApiHelper;
import org.openspcoop2.core.config.rs.server.api.impl.erogazioni.ErogazioniEnv;
import org.openspcoop2.core.config.rs.server.config.ServerProperties;
import org.openspcoop2.core.config.rs.server.model.Gruppo;
import org.openspcoop2.core.config.rs.server.model.GruppoAzioni;
import org.openspcoop2.core.config.rs.server.model.GruppoEreditaConfigurazione;
import org.openspcoop2.core.config.rs.server.model.GruppoItem;
import org.openspcoop2.core.config.rs.server.model.GruppoNome;
import org.openspcoop2.core.config.rs.server.model.GruppoNuovaConfigurazione;
import org.openspcoop2.core.config.rs.server.model.ListaGruppi;
import org.openspcoop2.core.config.rs.server.model.ModalitaConfigurazioneGruppoEnum;
import org.openspcoop2.core.id.IDPortaDelegata;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.core.mapping.MappingFruizionePortaDelegata;
import org.openspcoop2.core.registry.AccordoServizioParteSpecifica;
import org.openspcoop2.utils.BooleanNullable;
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
import org.openspcoop2.web.ctrlstat.servlet.aps.AccordiServizioParteSpecificaFruitoriPorteDelegateMappingInfo;
import org.openspcoop2.web.ctrlstat.servlet.aps.AccordiServizioParteSpecificaUtilities;
import org.openspcoop2.web.ctrlstat.servlet.connettori.ConnettoriCostanti;
import org.openspcoop2.web.ctrlstat.servlet.pd.PorteDelegateUtilities;
import org.openspcoop2.web.lib.mvc.ServletUtils;
import org.openspcoop2.web.lib.mvc.TipoOperazione;

/**
 * FruizioniGruppiApiServiceImpl
 * 
 * @author $Author$
 * @version $Rev$, $Date$
 * 
 */
public class FruizioniGruppiApiServiceImpl extends BaseImpl implements FruizioniGruppiApi {

	public FruizioniGruppiApiServiceImpl(){
		super(org.slf4j.LoggerFactory.getLogger(FruizioniGruppiApiServiceImpl.class));
	}

	private AuthorizationConfig getAuthorizationConfig() throws Exception{
		return new AuthorizationConfig(ServerProperties.getInstance().getProperties());
	}

    /**
     * Aggiunta di azioni o risorse dell'API al gruppo
     *
     * Questa operazione consente di aggiungere azioni o risorse dell'API al gruppo
     *
     */
	@Override
    public void addFruizioneGruppoAzioni(GruppoAzioni body, String erogatore, String nome, Integer versione, String nomeGruppo, ProfiloEnum profilo, String soggetto, String tipoServizio) {
		IContext context = this.getContext();
		try {
			context.getLogger().info("Invocazione in corso ...");     
		

			AuthorizationManager.authorize(context, getAuthorizationConfig());
			context.getLogger().debug("Autorizzazione completata con successo");
			
			BaseHelper.throwIfNull(body);
			
			final ErogazioniEnv env = new ErogazioniEnv(context.getServletRequest(), profilo, soggetto, context);

			final IDSoggetto idErogatore = new IDSoggetto(env.tipo_soggetto, erogatore);
			final AccordoServizioParteSpecifica asps = BaseHelper.supplyOrNotFound( () -> ErogazioniApiHelper.getServizioIfFruizione(tipoServizio, nome, versione, idErogatore, env.idSoggetto.toIDSoggetto(), env), "Fruizione");
			final IdServizio idAsps = new IdServizio(env.idServizioFactory.getIDServizioFromAccordo(asps), asps.getId());

			final IDPortaDelegata idPd = BaseHelper.supplyOrNotFound( () -> ErogazioniApiHelper.getIDGruppoPD(nomeGruppo, env.idSoggetto.toIDSoggetto(), idAsps, env.apsCore), "Gruppo per la fruizione scelta");
			final PortaDelegata pd = env.pdCore.getPortaDelegata(idPd);
						
			List<String> azioniOccupate = ErogazioniApiHelper.getAzioniOccupateFruizione(idAsps, env.idSoggetto.toIDSoggetto(), env.apsCore, env.pdCore);
			
			ErogazioniApiHelper.checkAzioniAdd( 
					body.getAzioni(),
					azioniOccupate,
					env.apcCore.getAzioni(asps, env.apcCore.getAccordoServizioSintetico(asps.getIdAccordo()), false, false, null)
				);
			
			
			env.requestWrapper.overrideParameterValues(CostantiControlStation.PARAMETRO_AZIONI, body.getAzioni().toArray(new String[0]));
		
			long idFruizione = env.apsCore.getIdFruizioneAccordoServizioParteSpecifica(env.idSoggetto.toIDSoggetto(), idAsps);
			List<MappingFruizionePortaDelegata> listaMappingFruizione = env.apsCore.serviziFruitoriMappingList(idFruizione, env.idSoggetto.toIDSoggetto(), idAsps, null);
			if (!env.paHelper.porteDelAzioneCheckData(TipoOperazione.ADD,azioniOccupate,listaMappingFruizione)) {
				throw FaultCode.RICHIESTA_NON_VALIDA.toException(StringEscapeUtils.unescapeHtml(env.pd.getMessage()));
			}
			
			// aggiungo azione nel db
			for(String azione: body.getAzioni()) {
				pd.getAzione().addAzioneDelegata(azione);
			}

			env.paCore.performUpdateOperation(env.userLogin, false, pd);
     
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
     * Creazione di un gruppo di azioni o risorse dell'API fruita
     *
     * Questa operazione consente di creare un gruppo di azioni o risorse dell'API fruita
     *
     */
	@Override
    public void createFruizioneGruppo(Gruppo body, String erogatore, String nome, Integer versione, ProfiloEnum profilo, String soggetto, String tipoServizio) {
		IContext context = this.getContext();
		try {
			context.getLogger().info("Invocazione in corso ...");     

			AuthorizationManager.authorize(context, getAuthorizationConfig());
			context.getLogger().debug("Autorizzazione completata con successo");
			
			ErogazioniEnv env = new ErogazioniEnv(context.getServletRequest(), profilo, soggetto, context);
			
			final IDSoggetto idErogatore = new IDSoggetto(env.tipo_soggetto, erogatore);
			final AccordoServizioParteSpecifica asps = BaseHelper.supplyOrNotFound( () -> ErogazioniApiHelper.getServizioIfFruizione(tipoServizio, nome, versione, idErogatore, env.idSoggetto.toIDSoggetto(),  env), "Fruizione");
			final IdServizio idAsps = new IdServizio(env.idServizioFactory.getIDServizioFromAccordo(asps), asps.getId());

		
			String mappingPadre = null;
			String fruizioneAutenticazione = null;
			String fruizioneAutenticazioneOpzionale = null;				
			TipoAutenticazionePrincipal fruizioneAutenticazionePrincipal = null;
			List<String> fruizioneAutenticazioneParametroList = null;
			
			if ( body.getConfigurazione().getModalita() == ModalitaConfigurazioneGruppoEnum.NUOVA ) {
				
				final GruppoNuovaConfigurazione confNuova = ErogazioniApiHelper.deserializeModalitaConfGruppo(body.getConfigurazione().getModalita(), body.getConfigurazione());
				if(confNuova.getAutenticazione()!=null) {
					fruizioneAutenticazione = Enums.toTipoAutenticazione(confNuova.getAutenticazione().getTipo()).toString();
					BooleanNullable autenticazioneOpzionaleNullable = ErogazioniApiHelper.getAutenticazioneOpzionale(confNuova.getAutenticazione()); // gestisce authn se null
					fruizioneAutenticazioneOpzionale = ServletUtils.boolToCheckBoxStatus(autenticazioneOpzionaleNullable!=null ? autenticazioneOpzionaleNullable.getValue() : null);
					fruizioneAutenticazionePrincipal = ErogazioniApiHelper.getTipoAutenticazionePrincipal(confNuova.getAutenticazione()); 
					fruizioneAutenticazioneParametroList = ErogazioniApiHelper.getAutenticazioneParametroList(env, confNuova.getAutenticazione().getTipo(), confNuova.getAutenticazione());
				}
			}
			
			else if ( body.getConfigurazione().getModalita() == ModalitaConfigurazioneGruppoEnum.EREDITA ) {	
				GruppoEreditaConfigurazione confEredita = ErogazioniApiHelper.deserializeModalitaConfGruppo(body.getConfigurazione().getModalita(), body.getConfigurazione());
				List<MappingFruizionePortaDelegata> mappings = ErogazioniApiHelper.getMappingGruppiPD( confEredita.getNome(), env.idSoggetto.toIDSoggetto(), idAsps, env.apsCore);
				if ( mappings.isEmpty() ) {
					throw FaultCode.RICHIESTA_NON_VALIDA.toException("Il gruppo " + confEredita.getNome() + " da cui ereditare è inesistente");
				}
				mappingPadre = mappings.get(0).getNome();			
			}
			
			AccordiServizioParteSpecificaFruitoriPorteDelegateMappingInfo mappingInfo = AccordiServizioParteSpecificaUtilities.getMappingInfo(mappingPadre, env.idSoggetto.toIDSoggetto(), asps, env.apsCore);
			MappingFruizionePortaDelegata mappingSelezionato = mappingInfo.getMappingSelezionato();
			MappingFruizionePortaDelegata mappingDefault = mappingInfo.getMappingDefault();
			
			if ( mappingDefault == null )
				throw FaultCode.NOT_FOUND.toException("Nessuna fruizione trovata");
			if ( mappingSelezionato == null )
				throw FaultCode.RICHIESTA_NON_VALIDA.toException("Gruppo con nome  non trovato");
			
			List<String> azioniOccupate = mappingInfo.getAzioniOccupate();
			
			ErogazioniApiHelper.checkAzioniAdd( 
					body.getAzioni(),
					azioniOccupate,
					env.apcCore.getAzioni(asps, env.apcCore.getAccordoServizioSintetico(asps.getIdAccordo()), false, false, null)
				);
			
			if (!env.apsHelper.configurazioneFruizioneCheckData(
					TipoOperazione.ADD, 
					mappingInfo.getNomeNuovaConfigurazione(), 
					body.getNome(), 
					body.getAzioni().toArray(new String[0]),
					asps, 
					azioniOccupate,
					body.getConfigurazione().getModalita().toString(),
					null,
					env.isSupportatoAutenticazioneSoggetti,
					mappingInfo)) {
				throw FaultCode.RICHIESTA_NON_VALIDA.toException(StringEscapeUtils.unescapeHtml(env.pd.getMessage()));
			}
			
			AccordiServizioParteSpecificaUtilities.addAccordoServizioParteSpecificaPorteDelegate(
					mappingDefault,
					mappingSelezionato,
					mappingInfo.getNomeNuovaConfigurazione(), 
					body.getNome(),
					body.getAzioni().toArray(new String[0]),
					body.getConfigurazione().getModalita().toString(),
					body.getConfigurazione().getModalita().toString(),
					null,							// endpointtype,
					null,							// tipoconn,
					null,							// autenticazioneHttp,	// RECHECK
					null,							// connettoreDebug,
					null,							// url,
					null,							// nomeCodaJms,
					null,							// tipoJms, 
					null,							// initcont, 
					null,							// urlpgk, 
					null,							// provurl, 
					null,							// connfact, 
					null,							// tipoSendas, 
					null,							// user, 	RECHECK
					null,							// password, RECHECK
					null,							// httpsurl, 
					null,							// httpstipologia, 
					false,							// httpshostverify,
					ConnettoriCostanti.DEFAULT_CONNETTORE_HTTPS_TRUST_VERIFY_CERTS, // httpsTrustVerifyCert
					null,							// httpspath, 
					null,							// httpstipo, 
					null,							// httpspwd,
					null,							// httpsalgoritmo, 
					false,							// httpsstato,
					null,							// httpskeystore,
					null,							// httpspwdprivatekeytrust, 
					null,							// httpspathkey,
					null,							// httpstipokey, 
					null,							// httpspwdkey,
					null,							// httpspwdprivatekey, 
					null,							// httpsalgoritmokey,
	        		null,							// httpsKeyAlias
	        		null,							// httpsTrustStoreCRLs
					null,							// proxy_enabled, 
					null,							// proxy_hostname, 
					null,							// proxy_port, 
					null,							// proxy_username, 
					null,							// proxy_password,
					null,							// tempiRisposta_enabled, 
					null,							// tempiRisposta_connectionTimeout,
					null,							// tempiRisposta_readTimeout, 
					null,							// tempiRisposta_tempoMedioRisposta,
					"no",							// opzioniAvanzate,
					null,							// transfer_mode, 
					null,							// transfer_mode_chunk_size, 
					null,							// redirect_mode, 
					null,							// redirect_max_hop,
					null,							// requestOutputFileName,
					null,							// requestOutputFileName_permissions,
					null,							// requestOutputFileNameHeaders,
					null,							// requestOutputFileNameHeaders_permissions,
					null,							// requestOutputParentDirCreateIfNotExists,
					null,							// requestOutputOverwriteIfExists,
					null,							// responseInputMode,
					null,							// responseInputFileName,
					null,							// responseInputFileNameHeaders, 
					null,							// responseInputDeleteAfterRead, 
					null,							// responseInputWaitTime,
					false, 							// autenticazioneToken,
					null, 							// tokenPolicy,
					null,							// listExtendedConnettore, 
	        		fruizioneAutenticazione,
	        		fruizioneAutenticazioneOpzionale,
	        		fruizioneAutenticazionePrincipal,
	        		fruizioneAutenticazioneParametroList,
					"disabilitato",					// erogazioneAutorizzazione, Come da debug. 
					null,							// erogazioneAutorizzazioneAutenticati, 
					null,							// erogazioneAutorizzazioneRuoli, 
					null,							// erogazioneAutorizzazioneRuoliTipologia, 
					null,							// erogazioneAutorizzazioneRuoliMatch,
					null,							// nomeSA, 
					null,							// erogazioneRuolo, 
			    	null,   						// 	autorizzazioneAutenticatiToken, 
			    	null,   						// 	autorizzazioneRuoliToken, 
			    	null,   						// 	autorizzazioneRuoliTipologiaToken, 
			    	null,   						// 	autorizzazioneRuoliMatchToken,
					null,							// autorizzazione_tokenOptions,
					null,							// autorizzazioneScope,
					null,							// scope, 
					null,							// autorizzazioneScopeMatch,
					null,							// allegatoXacmlPolicy,
					"disabilitato",					// gestioneToken, Come da debug. 
					null,							// gestioneTokenPolicy,  
					null,							// gestioneTokenOpzionale,  
					null,							// gestioneTokenValidazioneInput, 
					null,							// gestioneTokenIntrospection, 
					null,							// gestioneTokenUserInfo, 
					null,							// gestioneTokenTokenForward,
					null,							// autenticazioneTokenIssuer, 
					null,							// autenticazioneTokenClientId,
					null,							// autenticazioneTokenSubject, 
					null,							// autenticazioneTokenUsername, 
					null,							// autenticazioneTokenEMail,
					env.idSoggetto.toIDSoggetto(),
					asps, 
					env.userLogin,
					env.apsCore,
					env.apsHelper,
					null, // identificazioneAttributiStato
	        		null, //String [] attributeAuthoritySelezionate
	        		null // attributeAuthorityAttributi
				);


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
     * Elimina il gruppo identificato dal nome
     *
     * Questa operazione consente di eliminare il gruppo identificato dal nome
     *
     */
	@Override
    public void deleteFruizioneGruppo(String erogatore, String nome, Integer versione, String nomeGruppo, ProfiloEnum profilo, String soggetto, String tipoServizio) {
		IContext context = this.getContext();
		try {
			context.getLogger().info("Invocazione in corso ...");     

			AuthorizationManager.authorize(context, getAuthorizationConfig());
			context.getLogger().debug("Autorizzazione completata con successo");     
                        
			final ErogazioniEnv env = new ErogazioniEnv(context.getServletRequest(), profilo, soggetto, context);
			final IDSoggetto idErogatore = new IDSoggetto(env.tipo_soggetto, erogatore);
			final AccordoServizioParteSpecifica asps = BaseHelper.supplyOrNotFound( () -> ErogazioniApiHelper.getServizioIfFruizione(tipoServizio, nome, versione, idErogatore, env.idSoggetto.toIDSoggetto(), env), "Fruizione");
			final IdServizio idAsps = new IdServizio(env.idServizioFactory.getIDServizioFromAccordo(asps),asps.getId());
			
			// ricevo come parametro l'id della pa associata al mapping da cancellare
			final IDPortaDelegata idPortaDelegata = ErogazioniApiHelper.getIDGruppoPD(nomeGruppo, env.idSoggetto.toIDSoggetto(), idAsps, env.apsCore);
			
			if ( idPortaDelegata != null ) {
			
				StringBuilder inUsoMessage = new StringBuilder();
				AccordiServizioParteSpecificaUtilities.deleteAccordoServizioParteSpecificaFruitoriPorteDelegate(
						new ArrayList<IDPortaDelegata>(Arrays.asList(idPortaDelegata)), 
						asps, env.idSoggetto.toIDSoggetto(), 
						env.userLogin, 
						env.apsCore, 
						env.apsHelper, 
						inUsoMessage
					);
				
				if (inUsoMessage.length() > 0)
					throw FaultCode.RICHIESTA_NON_VALIDA.toException(StringEscapeUtils.unescapeHtml(inUsoMessage.toString()));
			} else if ( env.delete_404 ) {
				throw FaultCode.NOT_FOUND.toException("Gruppo " + nomeGruppo + " non associato alla fruizione");
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
     * Elimina l'azione o la risorsa dell'API associatia al gruppo
     *
     * Questa operazione consente di eliminare l'azione o la risorsa dell'API associata al gruppo
     *
     */
	@Override
    public void deleteFruizioneGruppoAzione(String erogatore, String nome, Integer versione, String nomeGruppo, String nomeAzione, ProfiloEnum profilo, String soggetto, String tipoServizio) {
		IContext context = this.getContext();
		try {
			context.getLogger().info("Invocazione in corso ...");     

			AuthorizationManager.authorize(context, getAuthorizationConfig());
			context.getLogger().debug("Autorizzazione completata con successo");
			
            
			final ErogazioniEnv env = new ErogazioniEnv(context.getServletRequest(), profilo, soggetto, context);
			final IDSoggetto idErogatore = new IDSoggetto(env.tipo_soggetto, erogatore);
			final AccordoServizioParteSpecifica asps = BaseHelper.supplyOrNotFound( () -> ErogazioniApiHelper.getServizioIfFruizione(tipoServizio, nome, versione, idErogatore, env.idSoggetto.toIDSoggetto(), env), "Fruizione");
			final IdServizio idAsps = new IdServizio(env.idServizioFactory.getIDServizioFromAccordo(asps),asps.getId());
			final IDPortaDelegata idPd = BaseHelper.supplyOrNotFound( () -> ErogazioniApiHelper.getIDGruppoPD(nomeGruppo, env.idSoggetto.toIDSoggetto(), idAsps, env.apsCore), "Gruppo per la fruizione scelta");
			final PortaDelegata pd = env.pdCore.getPortaDelegata(idPd);
			
			if ( BaseHelper.findFirst( pd.getAzione().getAzioneDelegataList(), a -> a.equals(nomeAzione)).isPresent() ) {
                        
				StringBuilder inUsoMessage = new StringBuilder();
				
				PorteDelegateUtilities.deletePortaDelegataAzioni(pd, asps, env.pdCore, env.pdHelper, inUsoMessage, new ArrayList<String>(Arrays.asList(nomeAzione)), env.userLogin);
				
				if (inUsoMessage.length() > 0)
					throw FaultCode.RICHIESTA_NON_VALIDA.toException(StringEscapeUtils.unescapeHtml(inUsoMessage.toString()));
			} else if ( env.delete_404 ) {
				throw FaultCode.NOT_FOUND.toException("Azione " + nomeAzione + " non associata al gruppo " + nomeGruppo );
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
     * Ricerca i gruppi in cui sono stati classificate le azioni o le risorse dell'API
     *
     * Elenca i gruppi in cui sono stati classificate le azioni o le risorse dell'API
     *
     */
	@Override
    public ListaGruppi findAllFruizioneGruppi(String erogatore, String nome, Integer versione, ProfiloEnum profilo, String soggetto, String tipoServizio, Integer limit, Integer offset, String azione) {
		IContext context = this.getContext();
		try {
			context.getLogger().info("Invocazione in corso ...");     

			AuthorizationManager.authorize(context, getAuthorizationConfig());
			context.getLogger().debug("Autorizzazione completata con successo");
			
			final ErogazioniEnv env = new ErogazioniEnv(context.getServletRequest(), profilo, soggetto, context);
			final IDSoggetto idErogatore = new IDSoggetto(env.tipo_soggetto, erogatore);
			final AccordoServizioParteSpecifica asps = BaseHelper.supplyOrNotFound( () -> ErogazioniApiHelper.getServizioIfFruizione(tipoServizio, nome, versione, idErogatore, env.idSoggetto.toIDSoggetto(), env), "Fruizione");
			final IdServizio idAsps = new IdServizio(env.idServizioFactory.getIDServizioFromAccordo(asps),asps.getId());
			
			final int idLista = Liste.CONFIGURAZIONE_FRUIZIONE;
			final ConsoleSearch ricerca = Helper.setupRicercaPaginata(null, limit, offset, idLista, env.idSoggetto.toIDSoggetto(), env.tipo_protocollo);
			
			if (!StringUtils.isEmpty(azione))
				ricerca.addFilter(idLista, Filtri.FILTRO_AZIONE, azione);
			
			List<MappingFruizionePortaDelegata> mappings = env.apsCore.serviziFruitoriMappingList(env.idSoggetto.toIDSoggetto(), idAsps, ricerca);
			
			if ( env.findall_404 && mappings.isEmpty() ) {
				throw FaultCode.NOT_FOUND.toException("Nessun gruppo associato alla fruizione");
			}
			
			ListaGruppi ret = ListaUtils.costruisciListaPaginata(context.getUriInfo(), 
					ricerca.getIndexIniziale(idLista),
					ricerca.getPageSize(idLista), 
					ricerca.getNumEntries(idLista), ListaGruppi.class);
			
			for (MappingFruizionePortaDelegata m : mappings) {
				final PortaDelegata pd = env.pdCore.getPortaDelegata(m.getIdPortaDelegata());
				
				GruppoItem g = new GruppoItem();
				g.setAzioni(pd.getAzione().getAzioneDelegataList());
				g.setNome(m.getDescrizione());
				g.setPredefinito(m.isDefault());
				
				ret.addItemsItem(g);			
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
     * Restituisce azioni/risorse associate al gruppo identificato dal nome
     *
     * Questa operazione consente di ottenere le azioni associate al gruppo identificato dal nome
     *
     */
	@Override
    public GruppoAzioni getFruizioneGruppoAzioni(String erogatore, String nome, Integer versione, String nomeGruppo, ProfiloEnum profilo, String soggetto, String tipoServizio) {
		IContext context = this.getContext();
		try {
			context.getLogger().info("Invocazione in corso ...");     

			AuthorizationManager.authorize(context, getAuthorizationConfig());
			context.getLogger().debug("Autorizzazione completata con successo");
			
            
			final ErogazioniEnv env = new ErogazioniEnv(context.getServletRequest(), profilo, soggetto, context);
			final IDSoggetto idErogatore = new IDSoggetto(env.tipo_soggetto, erogatore);
			final AccordoServizioParteSpecifica asps = BaseHelper.supplyOrNotFound( () -> ErogazioniApiHelper.getServizioIfFruizione(tipoServizio, nome, versione, idErogatore, env.idSoggetto.toIDSoggetto(), env), "Fruizione");
			final IdServizio idAsps = new IdServizio(env.idServizioFactory.getIDServizioFromAccordo(asps),asps.getId());
			
			// ricevo come parametro l'id della pa associata al mapping da cancellare
			final IDPortaDelegata idPortaDelegata = BaseHelper.supplyOrNotFound( 
					() -> ErogazioniApiHelper.getIDGruppoPD(nomeGruppo, env.idSoggetto.toIDSoggetto(), idAsps, env.apsCore)
					, "Gruppo per la fruizione scelta"
				);
			final PortaDelegata pd = BaseHelper.supplyOrNotFound(
					() -> env.pdCore.getPortaDelegata(idPortaDelegata)
					, "Gruppo per la fruizione scelta"
				);
			
			
			GruppoAzioni ret = new GruppoAzioni();
			ret.setAzioni(pd.getAzione().getAzioneDelegataList());
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
     * Consente di modificare il nome del gruppo
     *
     * Questa operazione consente di aggiornare il nome di un gruppo
     *
     */
	@Override
    public void updateFruizioneGruppoNome(GruppoNome body, String erogatore, String nome, Integer versione, String nomeGruppo, ProfiloEnum profilo, String soggetto, String tipoServizio) {
		IContext context = this.getContext();
		try {
			context.getLogger().info("Invocazione in corso ...");     

			AuthorizationManager.authorize(context, getAuthorizationConfig());
			context.getLogger().debug("Autorizzazione completata con successo");
			
		      
			final ErogazioniEnv env = new ErogazioniEnv(context.getServletRequest(), profilo, soggetto, context);
			final IDSoggetto idErogatore = new IDSoggetto(env.tipo_soggetto, erogatore);
			final AccordoServizioParteSpecifica asps = BaseHelper.supplyOrNotFound( () -> ErogazioniApiHelper.getServizioIfFruizione(tipoServizio, nome, versione, idErogatore, env.idSoggetto.toIDSoggetto(), env), "Fruizione");
			final IdServizio idAsps = new IdServizio(env.idServizioFactory.getIDServizioFromAccordo(asps),asps.getId());
			final List<MappingFruizionePortaDelegata> listaMapping = env.apsCore.serviziFruitoriMappingList(env.idSoggetto.toIDSoggetto(), idAsps, null);

			final List<String> mappingUtilizzati = ErogazioniApiHelper.getDescrizioniMappingPD(listaMapping);
			
			BaseHelper.supplyOrNotFound( () -> ErogazioniApiHelper.getIDGruppoPD(nomeGruppo, env.idSoggetto.toIDSoggetto(),  idAsps, env.apsCore), "Gruppo per la fruizione scelta");
			
			if (mappingUtilizzati.stream().filter( m -> m.equalsIgnoreCase(body.getNome())).findFirst().isPresent()) {
				throw FaultCode.CONFLITTO.toException(CostantiControlStation.MESSAGGIO_ERRORE_NOME_GRUPPO_GIA_PRESENTE);
			}
			
			if (! env.paHelper.configurazioneCambiaNomeCheck(TipoOperazione.OTHER, body.getNome(), mappingUtilizzati,true)) {
				throw FaultCode.RICHIESTA_NON_VALIDA.toException(StringEscapeUtils.unescapeHtml(env.pd.getMessage()));
			}
			
			final MappingFruizionePortaDelegata mapping= AccordiServizioParteSpecificaUtilities.getMappingPD_filterByDescription(
					 listaMapping, 
					 nomeGruppo
					);
			
			mapping.setDescrizione(body.getNome());
			
			env.pdCore.aggiornaDescrizioneMappingFruizionePortaDelegata( mapping );
			                        
        
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

