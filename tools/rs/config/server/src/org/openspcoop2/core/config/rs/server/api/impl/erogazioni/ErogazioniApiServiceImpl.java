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
package org.openspcoop2.core.config.rs.server.api.impl.erogazioni;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.apache.commons.lang.StringEscapeUtils;
import org.openspcoop2.core.commons.Filtri;
import org.openspcoop2.core.commons.Liste;
import org.openspcoop2.core.config.InvocazioneCredenziali;
import org.openspcoop2.core.config.InvocazioneServizio;
import org.openspcoop2.core.config.PortaApplicativa;
import org.openspcoop2.core.config.PortaApplicativaAzione;
import org.openspcoop2.core.config.ServizioApplicativo;
import org.openspcoop2.core.config.constants.InvocazioneServizioTipoAutenticazione;
import org.openspcoop2.core.config.constants.PortaApplicativaAzioneIdentificazione;
import org.openspcoop2.core.config.constants.StatoFunzionalita;
import org.openspcoop2.core.config.constants.TipologiaErogazione;
import org.openspcoop2.core.config.rs.server.api.ErogazioniApi;
import org.openspcoop2.core.config.rs.server.api.impl.Helper;
import org.openspcoop2.core.config.rs.server.config.ServerProperties;
import org.openspcoop2.core.config.rs.server.model.ApiImplAllegato;
import org.openspcoop2.core.config.rs.server.model.ApiImplInformazioniGenerali;
import org.openspcoop2.core.config.rs.server.model.ApiImplInformazioniGeneraliView;
import org.openspcoop2.core.config.rs.server.model.ApiImplUrlInvocazione;
import org.openspcoop2.core.config.rs.server.model.ApiImplUrlInvocazioneView;
import org.openspcoop2.core.config.rs.server.model.ApiImplVersioneApi;
import org.openspcoop2.core.config.rs.server.model.ApiImplVersioneApiView;
import org.openspcoop2.core.config.rs.server.model.Connettore;
import org.openspcoop2.core.config.rs.server.model.Erogazione;
import org.openspcoop2.core.config.rs.server.model.ErogazioneViewItem;
import org.openspcoop2.core.config.rs.server.model.ListaApiImplAllegati;
import org.openspcoop2.core.config.rs.server.model.ListaErogazioni;
import org.openspcoop2.core.config.rs.server.model.ModalitaIdentificazioneAzioneEnum;
import org.openspcoop2.core.config.rs.server.model.TipoApiEnum;
import org.openspcoop2.core.constants.TipiConnettore;
import org.openspcoop2.core.id.IDPortaApplicativa;
import org.openspcoop2.core.id.IDServizio;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.core.registry.AccordoServizioParteComune;
import org.openspcoop2.core.registry.AccordoServizioParteSpecifica;
import org.openspcoop2.core.registry.Documento;
import org.openspcoop2.core.registry.Soggetto;
import org.openspcoop2.core.registry.beans.AccordoServizioParteComuneSintetico;
import org.openspcoop2.utils.service.BaseImpl;
import org.openspcoop2.utils.service.authorization.AuthorizationConfig;
import org.openspcoop2.utils.service.authorization.AuthorizationManager;
import org.openspcoop2.utils.service.beans.ProfiloEnum;
import org.openspcoop2.utils.service.beans.utils.BaseHelper;
import org.openspcoop2.utils.service.beans.utils.ListaUtils;
import org.openspcoop2.utils.service.context.IContext;
import org.openspcoop2.utils.service.fault.jaxrs.FaultCode;
import org.openspcoop2.web.ctrlstat.core.Search;
import org.openspcoop2.web.ctrlstat.servlet.aps.AccordiServizioParteSpecificaCostanti;
import org.openspcoop2.web.ctrlstat.servlet.aps.AccordiServizioParteSpecificaUtilities;
import org.openspcoop2.web.ctrlstat.servlet.pa.PorteApplicativeCostanti;
import org.openspcoop2.web.ctrlstat.servlet.sa.ServiziApplicativiUtilities;
import org.openspcoop2.web.lib.mvc.ServletUtils;
import org.openspcoop2.web.lib.mvc.TipoOperazione;

/**
 * ErogazioniApiServiceImpl
 * 
 * @author $Author$
 * @version $Rev$, $Date$
 * 
 */
public class ErogazioniApiServiceImpl extends BaseImpl implements ErogazioniApi {

	public ErogazioniApiServiceImpl(){
		super(org.slf4j.LoggerFactory.getLogger(ErogazioniApiServiceImpl.class));
	}

	private AuthorizationConfig getAuthorizationConfig() throws Exception{
		return new AuthorizationConfig(ServerProperties.getInstance().getProperties());
	}

    /**
     * Creazione di un'erogazione di API
     *
     * Questa operazione consente di creare una erogazione di API
     *
     */
	@Override
    public void createErogazione(Erogazione body, ProfiloEnum profilo, String soggetto) {
		
		IContext context = this.getContext();
		try {
			context.getLogger().info("Invocazione in corso ...");     

			AuthorizationManager.authorize(context, getAuthorizationConfig());
			context.getLogger().debug("Autorizzazione completata con successo");
			
			BaseHelper.throwIfNull(body);
			
            final ErogazioniEnv env = new ErogazioniEnv(context.getServletRequest(), profilo, soggetto, context);
            final Soggetto soggettoErogatore = env.soggettiCore.getSoggettoRegistro(env.idSoggetto.toIDSoggetto());

            final IDSoggetto idReferente = ErogazioniApiHelper.getIdReferente(body, env);
            final AccordoServizioParteComuneSintetico as = Helper.getAccordoSintetico(body.getApiNome(), body.getApiVersione(), idReferente ,env.apcCore);
            
            if ( as == null ) {
            	throw FaultCode.RICHIESTA_NON_VALIDA.toException("Nessuna Api registrata con nome " + body.getApiNome() + " e versione " + body.getApiVersione());
            }
           
            AccordoServizioParteSpecifica asps = ErogazioniApiHelper.apiImplToAps(body, soggettoErogatore, as, env);	
            final IDServizio idAps = env.idServizioFactory.getIDServizioFromValues(asps.getTipo(), asps.getNome(), new IDSoggetto(asps.getTipoSoggettoErogatore(), asps.getNomeSoggettoErogatore()), asps.getVersione());
            boolean alreadyExists = env.apsCore.existsAccordoServizioParteSpecifica(idAps);
            
            if ( alreadyExists ) {
            	asps = env.apsCore.getServizio(idAps);
			}
                       
            ServletUtils.setObjectIntoSession(context.getServletRequest().getSession(), AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_TIPO_EROGAZIONE_VALUE_EROGAZIONE, AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_TIPO_EROGAZIONE);
            
            ErogazioniApiHelper.serviziCheckData(TipoOperazione.ADD, env, as, asps, Optional.empty(),  body);            	
			
            org.openspcoop2.core.registry.Connettore regConnettore = ErogazioniApiHelper.buildConnettoreRegistro(env, body.getConnettore());     
			ErogazioniApiHelper.createAps(env, asps, regConnettore, body, alreadyExists, true);
            	
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
     * Creazione di un allegato nell'erogazione di API
     *
     * Questa operazione consente di aggiungere un allegato all'erogazione di API identificata dal nome e dalla versione
     *
     */
	@Override
    public void createErogazioneAllegato(ApiImplAllegato body, String nome, Integer versione, ProfiloEnum profilo, String soggetto, String tipoServizio) {
		IContext context = this.getContext();
		try {
			context.getLogger().info("Invocazione in corso ...");     

			AuthorizationManager.authorize(context, getAuthorizationConfig());
			context.getLogger().debug("Autorizzazione completata con successo");
			
                        
			final ErogazioniEnv env = new ErogazioniEnv(context.getServletRequest(), profilo, soggetto, context);            

			AccordoServizioParteSpecifica asps = BaseHelper.supplyOrNotFound( () -> ErogazioniApiHelper.getServizioIfErogazione(tipoServizio, nome, versione, env.idSoggetto.toIDSoggetto(), env), "Erogazione");
			
			ErogazioniApiHelper.createAllegatoAsps(body, env, asps);       
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
     * Elimina un'erogazione di api
     *
     * Questa operazione consente di eliminare un'erogazione di API identificata dal nome e dalla versione
     *
     */
	@Override
    public void deleteErogazione(String nome, Integer versione, ProfiloEnum profilo, String soggetto, String tipoServizio) {
		IContext context = this.getContext();
		try {
			context.getLogger().info("Invocazione in corso ...");     

			AuthorizationManager.authorize(context, getAuthorizationConfig());
			context.getLogger().debug("Autorizzazione completata con successo");
			
            final ErogazioniEnv env = new ErogazioniEnv(context.getServletRequest(), profilo, soggetto, context);
			final AccordoServizioParteSpecifica asps =  BaseHelper.evalnull( () -> ErogazioniApiHelper.getServizioIfErogazione(tipoServizio, nome, versione, env.idSoggetto.toIDSoggetto(), env) );
			
			if ( asps != null ) {
			
				final IDServizio idServizio = env.idServizioFactory.getIDServizioFromAccordo(asps);
				final StringBuffer inUsoMessage =  new StringBuffer();
	
	
				AccordiServizioParteSpecificaUtilities.deleteAccordoServizioParteSpecifica(
						asps, 
						false, 		// gestioneFruitori, 
						true, 		// gestioneErogatori, 
						null,		// idSoggettoFruitore
						idServizio,
						env.paCore.getExtendedServletPortaApplicativa(),
						env.userLogin,
						env.apsCore, 
						env.apsHelper, 
						inUsoMessage, 
						"\n");
	                        
				if (inUsoMessage.length() > 0) {
					throw FaultCode.RICHIESTA_NON_VALIDA.toException(StringEscapeUtils.unescapeHtml(inUsoMessage.toString()));
				}
			}
			else if ( env.delete_404 ) {
				throw FaultCode.NOT_FOUND.toException("Erogazione non presente");
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
     * Elimina un allegato dall'erogazione
     *
     * Questa operazione consente di eliminare un'allegato dell'erogazione di API identificata dal nome e dalla versione
     *
     */
	@Override
    public void deleteErogazioneAllegato(String nome, Integer versione, String nomeAllegato, ProfiloEnum profilo, String soggetto, String tipoServizio) {
		IContext context = this.getContext();
		try {
			context.getLogger().info("Invocazione in corso ...");     

			AuthorizationManager.authorize(context, getAuthorizationConfig());
			context.getLogger().debug("Autorizzazione completata con successo");     
                        
            final ErogazioniEnv env = new ErogazioniEnv(context.getServletRequest(), profilo, soggetto, context);
			final AccordoServizioParteSpecifica asps = BaseHelper.supplyOrNotFound( () -> ErogazioniApiHelper.getServizioIfErogazione(tipoServizio, nome, versione, env.idSoggetto.toIDSoggetto(), env), "Erogazione");
			ErogazioniApiHelper.deleteAllegato(nomeAllegato, env, asps);
			
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
     * Restituisce l'allegato di una erogazione
     *
     * Questa operazione consente di ottenere l'allegato di un'erogazione di API identificata dal nome e dalla versione
     *
     */
	@Override
    public byte[] downloadErogazioneAllegato(String nome, Integer versione, String nomeAllegato, ProfiloEnum profilo, String soggetto, String tipoServizio) {
		IContext context = this.getContext();
		try {
			context.getLogger().info("Invocazione in corso ...");     

			AuthorizationManager.authorize(context, getAuthorizationConfig());
			context.getLogger().debug("Autorizzazione completata con successo");     
			
			final ErogazioniEnv env = new ErogazioniEnv(context.getServletRequest(), profilo, soggetto, context);
			final AccordoServizioParteSpecifica asps = BaseHelper.supplyOrNotFound( 
					() -> ErogazioniApiHelper.getServizioIfErogazione(tipoServizio, nome, versione, env.idSoggetto.toIDSoggetto(), env), "Erogazione"
				);
			
			final Optional<Long> idDoc = ErogazioniApiHelper.getIdDocumento(nomeAllegato, asps);
			
			if (!idDoc.isPresent())
				throw FaultCode.NOT_FOUND.toException("Allegato di nome " + nomeAllegato + " non presente.");
			
			final Documento allegato = env.archiviCore.getDocumento(idDoc.get(), true);
		     
			context.getLogger().info("Invocazione completata con successo");
			
			return allegato.getByteContenuto();
                         
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
     * Ricerca erogazioni di api
     *
     * Elenca le erogazioni di API
     *
     */
	@Override
    public ListaErogazioni findAllErogazioni(ProfiloEnum profilo, String soggetto, String q, Integer limit, Integer offset, TipoApiEnum tipoApi) {
		IContext context = this.getContext();
		try {
			context.getLogger().info("Invocazione in corso ...");     

			AuthorizationManager.authorize(context, getAuthorizationConfig());
			context.getLogger().debug("Autorizzazione completata con successo");

			final ErogazioniEnv env = new ErogazioniEnv(context.getServletRequest(), profilo, soggetto, context);
			final int idLista = Liste.SERVIZI;
			final Search ricerca = Helper.setupRicercaPaginata(q, limit, offset, idLista, env.idSoggetto.toIDSoggetto(), env.tipo_protocollo);
		
			if (tipoApi != null)
				ricerca.addFilter(idLista, Filtri.FILTRO_SERVICE_BINDING, tipoApi.toString());
			
			List<AccordoServizioParteSpecifica> lista = env.apsCore.soggettiServizioList(null, ricerca, null, false, true);
			
			if ( env.findall_404 && lista.isEmpty() ) {
				throw FaultCode.NOT_FOUND.toException("Nessuna erogazione presente");
			}
			
			final ListaErogazioni ret = ListaUtils.costruisciListaPaginata(
					context.getServletRequest().getRequestURI(), 
					offset, 
					limit, 
					ricerca.getNumEntries(idLista), 
					ListaErogazioni.class
				);
	
			lista.forEach( asps -> {
					ret.addItemsItem( ErogazioniApiHelper.erogazioneViewItemToErogazioneItem( ErogazioniApiHelper.aspsToErogazioneViewItem(env, asps) ) );
			});
			
			context.getLogger().info("Invocazione completata con successo");
			
			return Helper.returnOrNotFound(ret);
     
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
     * Elenco allegati di un'erogazione di API
     *
     * Questa operazione consente di ottenere gli allegati di un'erogazione di API identificata dal nome e dalla versione
     *
     */
	@Override
    public ListaApiImplAllegati findAllErogazioneAllegati(String nome, Integer versione, ProfiloEnum profilo, String soggetto, String tipoServizio, String q, Integer limit, Integer offset) {
		IContext context = this.getContext();
		try {
			context.getLogger().info("Invocazione in corso ...");     

			AuthorizationManager.authorize(context, getAuthorizationConfig());
			context.getLogger().debug("Autorizzazione completata con successo");
			
			
			final ErogazioniEnv env = new ErogazioniEnv(context.getServletRequest(), profilo, soggetto, context);
			final AccordoServizioParteSpecifica asps = BaseHelper.supplyOrNotFound( 
					() -> ErogazioniApiHelper.getServizioIfErogazione(tipoServizio, nome, versione, env.idSoggetto.toIDSoggetto(), env), "Erogazione"
				);
			ListaApiImplAllegati ret = ErogazioniApiHelper.findAllAllegati(q, limit, offset, context.getServletRequest().getRequestURI(), env, asps);
			
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
     * Restituisce i dettagli di un'erogazione di API
     *
     * Questa operazione consente di ottenere i dettagli di una erogazione di API identificata dal nome e dalla versione
     *
     */
	@Override
    public ErogazioneViewItem getErogazione(String nome, Integer versione, ProfiloEnum profilo, String soggetto, String tipoServizio) {
		IContext context = this.getContext();
		try {
			context.getLogger().info("Invocazione in corso ...");     

			AuthorizationManager.authorize(context, getAuthorizationConfig());
			context.getLogger().debug("Autorizzazione completata con successo");
			

			final ErogazioniEnv env = new ErogazioniEnv(context.getServletRequest(), profilo, soggetto, context);
			final AccordoServizioParteSpecifica asps = BaseHelper.supplyOrNotFound( 
					() -> ErogazioniApiHelper.getServizioIfErogazione(tipoServizio, nome, versione, env.idSoggetto.toIDSoggetto(), env), "Erogazione"
				);
			
			ErogazioneViewItem ret = ErogazioniApiHelper.aspsToErogazioneViewItem(env, asps);
		       
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
     * Restituisce le informazioni sull'API implementata dall'erogazione
     *
     * Questa operazione consente di ottenere le informazioni sull'API implementata dall'erogazione identificata dal nome e dalla versione
     *
     */
	@Override
    public ApiImplVersioneApiView getErogazioneAPI(String nome, Integer versione, ProfiloEnum profilo, String soggetto, String tipoServizio) {
		IContext context = this.getContext();
		try {
			context.getLogger().info("Invocazione in corso ...");     

			AuthorizationManager.authorize(context, getAuthorizationConfig());
			context.getLogger().debug("Autorizzazione completata con successo");
			

			final ErogazioniEnv env = new ErogazioniEnv(context.getServletRequest(), profilo, soggetto, context);
			final AccordoServizioParteSpecifica asps = BaseHelper.supplyOrNotFound( 
					() -> ErogazioniApiHelper.getServizioIfErogazione(tipoServizio, nome, versione, env.idSoggetto.toIDSoggetto(), env), "Erogazione"
				);
			ApiImplVersioneApiView ret = ErogazioniApiHelper.aspsToApiImplVersioneApiView(env, asps);

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
     * Restituisce il dettaglio di un allegato dell'erogazione
     *
     * Questa operazione consente di ottenere il dettaglio di un allegato dell'erogazione di API identificata dal nome e dalla versione
     *
     */
	@Override
    public ApiImplAllegato getErogazioneAllegato(String nome, Integer versione, String nomeAllegato, ProfiloEnum profilo, String soggetto, String tipoServizio) {
		IContext context = this.getContext();
		try {
			context.getLogger().info("Invocazione in corso ...");     

			AuthorizationManager.authorize(context, getAuthorizationConfig());
			context.getLogger().debug("Autorizzazione completata con successo");
			

			final ErogazioniEnv env = new ErogazioniEnv(context.getServletRequest(), profilo, soggetto, context);
			final AccordoServizioParteSpecifica asps = BaseHelper.supplyOrNotFound( 
					() -> ErogazioniApiHelper.getServizioIfErogazione(tipoServizio, nome, versione, env.idSoggetto.toIDSoggetto(), env), "Erogazione"
				);
			
			final Optional<Long> idDoc = ErogazioniApiHelper.getIdDocumento(nomeAllegato, asps);
			
			if (!idDoc.isPresent())
				throw FaultCode.NOT_FOUND.toException("Allegato di nome " + nomeAllegato + " non presente.");
			
			final Documento doc = env.archiviCore.getDocumento(idDoc.get(), true);
			   
			context.getLogger().info("Invocazione completata con successo");
			        
			return ErogazioniApiHelper.documentoToImplAllegato(doc);
     
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
     * Restituisce le informazioni su connettore associato all'erogazione
     *
     * Questa operazione consente di ottenere le informazioni sul connettore associato all'erogazione identificata dal nome e dalla versione
     *
     */
	@Override
    public Connettore getErogazioneConnettore(String nome, Integer versione, ProfiloEnum profilo, String soggetto, String tipoServizio) {
		IContext context = this.getContext();
		try {
			context.getLogger().info("Invocazione in corso ...");     

			AuthorizationManager.authorize(context, getAuthorizationConfig());
			context.getLogger().debug("Autorizzazione completata con successo");
			

			final ErogazioniEnv env = new ErogazioniEnv(context.getServletRequest(), profilo, soggetto, context);
			final AccordoServizioParteSpecifica asps = BaseHelper.supplyOrNotFound( 
					() -> ErogazioniApiHelper.getServizioIfErogazione(tipoServizio, nome, versione, env.idSoggetto.toIDSoggetto(), env), "Erogazione"
				);
			
			org.openspcoop2.core.config.Connettore connettore = ErogazioniApiHelper.getConnettoreErogazione(env.idServizioFactory.getIDServizioFromAccordo(asps), env.saCore, env.paCore);
			
			final Connettore ret = ErogazioniApiHelper.buildConnettore(connettore.getProperties());
                                
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
     * Restituisce le informazioni generali di un'erogazione di API
     *
     * Questa operazione consente di ottenere le informazioni generali di un'erogazione di API identificata dal nome e dalla versione
     *
     */
	@Override
    public ApiImplInformazioniGeneraliView getErogazioneInformazioniGenerali(String nome, Integer versione, ProfiloEnum profilo, String soggetto, String tipoServizio) {
		IContext context = this.getContext();
		try {
			context.getLogger().info("Invocazione in corso ...");     

			AuthorizationManager.authorize(context, getAuthorizationConfig());
			context.getLogger().debug("Autorizzazione completata con successo");
			
			final ErogazioniEnv env = new ErogazioniEnv(context.getServletRequest(), profilo, soggetto, context);
			final AccordoServizioParteSpecifica asps = BaseHelper.supplyOrNotFound( 
					() -> ErogazioniApiHelper.getServizioIfErogazione(tipoServizio, nome, versione, env.idSoggetto.toIDSoggetto(), env), "Erogazione"
				);
			
			ApiImplInformazioniGeneraliView ret = ErogazioniApiHelper.erogazioneToApiImplInformazioniGeneraliView(env, asps);
                        
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
     * Restituisce le informazioni sull'url di invocazione necessaria ad invocare l'erogazione
     *
     * Questa operazione consente di ottenere le informazioni sull'url di invocazione necessaria ad invocare l'erogazione identificata dal nome e dalla versione
     *
     */
	@Override
    public ApiImplUrlInvocazioneView getErogazioneUrlInvocazione(String nome, Integer versione, ProfiloEnum profilo, String soggetto, String tipoServizio) {
		IContext context = this.getContext();
		try {
			context.getLogger().info("Invocazione in corso ...");     

			AuthorizationManager.authorize(context, getAuthorizationConfig());
			context.getLogger().debug("Autorizzazione completata con successo");
			

			final ErogazioniEnv env = new ErogazioniEnv(context.getServletRequest(), profilo, soggetto, context);
			final AccordoServizioParteSpecifica asps = BaseHelper.supplyOrNotFound( 
					() -> ErogazioniApiHelper.getServizioIfErogazione(tipoServizio, nome, versione, env.idSoggetto.toIDSoggetto(), env), "Erogazione"
				);
			final PortaApplicativa pa = env.paCore.getPortaApplicativa(env.paCore.getIDPortaApplicativaAssociataDefault(env.idServizioFactory.getIDServizioFromAccordo(asps)));
			final PortaApplicativaAzione paAzione = pa.getAzione();
			final AccordoServizioParteComuneSintetico aspc = env.apcCore.getAccordoServizioSintetico(asps.getIdAccordo());
			
			String urlInvocazione = ErogazioniApiHelper.getUrlInvocazioneErogazione(asps, env);
			ApiImplUrlInvocazioneView ret = new ApiImplUrlInvocazioneView();
			
			// Come prendo le azioni del gruppo predefinito?
			
			Map<String,String> azioni = env.paCore.getAzioniConLabel(asps, aspc, false, true, new ArrayList<String>()); 
			ret.setAzioni(Arrays.asList(azioni.keySet().toArray(new String[azioni.size()])));		
			ret.setForceInterface(Helper.statoFunzionalitaConfToBool(paAzione.getForceInterfaceBased()));
			ret.setModalita(Enum.valueOf(ModalitaIdentificazioneAzioneEnum.class, paAzione.getIdentificazione().name()));
			ret.setNome(nome);
			ret.setPattern(paAzione.getPattern());
			ret.setUrlInvocazione(urlInvocazione);
			
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
     * Consente di modificare la versione dell'API implementata dall'erogazione
     *
     * Questa operazione consente di aggiornare la versione dell'API implementata dall'erogazione identificata dal nome e dalla versione
     *
     */
	@Override
    public void updateErogazioneAPI(ApiImplVersioneApi body, String nome, Integer versione, ProfiloEnum profilo, String soggetto, String tipoServizio) {
		IContext context = this.getContext();
		try {
			context.getLogger().info("Invocazione in corso ...");   
			
			AuthorizationManager.authorize(context, getAuthorizationConfig());
			context.getLogger().debug("Autorizzazione completata con successo");
			
			BaseHelper.throwIfNull(body);
			
            final ErogazioniEnv env = new ErogazioniEnv(context.getServletRequest(), profilo, soggetto, context);
			final AccordoServizioParteSpecifica asps = BaseHelper.supplyOrNotFound( () -> ErogazioniApiHelper.getServizioIfErogazione(tipoServizio, nome, versione, env.idSoggetto.toIDSoggetto(), env), "Erogazione");
			final AccordoServizioParteComuneSintetico as = env.apcCore.getAccordoServizioSintetico(asps.getIdAccordo());

	        List<AccordoServizioParteComune> asParteComuneCompatibili = env.apsCore.findAccordiParteComuneBySoggettoAndNome(
	        		as.getNome(),
	                new IDSoggetto(as.getSoggettoReferente().getTipo(), as.getSoggettoReferente().getNome())
	        	);
	        
	        Optional<AccordoServizioParteComune> newApc = BaseHelper.findFirst( asParteComuneCompatibili, a -> a.getVersione() == body.getApiVersione());
	        
	        if ( !newApc.isPresent() ) {
	        	throw FaultCode.RICHIESTA_NON_VALIDA.toException("Nessuna api " + as.getNome() + " e versione " + body.getApiVersione() + " registrata");
	        }

			asps.setAccordoServizioParteComune(env.idAccordoFactory.getUriFromAccordo(newApc.get()));
			asps.setIdAccordo(newApc.get().getId());
			
			asps.setOldIDServizioForUpdate(env.idServizioFactory.getIDServizioFromAccordo(asps));
			
			ErogazioniApiHelper.serviziUpdateCheckData(as, asps, true, env);
			
			List<Object> oggettiDaAggiornare = AccordiServizioParteSpecificaUtilities.getOggettiDaAggiornare(asps, env.apsCore);
			
			env.apsCore.performUpdateOperation(env.userLogin, false, oggettiDaAggiornare.toArray());

        
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
     * Modifica i dati di un allegato dell'erogazione
     *
     * Questa operazione consente di aggiornare i dettagli di un allegato dell'erogazione di API identificata dal nome e dalla versione
     *
     */
	@Override
    public void updateErogazioneAllegato(ApiImplAllegato body, String nome, Integer versione, String nomeAllegato, ProfiloEnum profilo, String soggetto, String tipoServizio) {
		IContext context = this.getContext();
		try {
			context.getLogger().info("Invocazione in corso ...");     

			AuthorizationManager.authorize(context, getAuthorizationConfig());
			context.getLogger().debug("Autorizzazione completata con successo");
			
			BaseHelper.throwIfNull(body);
			
			final ErogazioniEnv env = new ErogazioniEnv(context.getServletRequest(), profilo, soggetto, context);            
			AccordoServizioParteSpecifica asps = BaseHelper.supplyOrNotFound( () -> ErogazioniApiHelper.getServizioIfErogazione(tipoServizio, nome, versione, env.idSoggetto.toIDSoggetto(), env), "Erogazione");
			
			
			ErogazioniApiHelper.updateAllegatoAsps(body, nomeAllegato, env, asps);

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
     * Consente di modificare la configurazione del connettore associato all'erogazione
     *
     * Questa operazione consente di aggiornare la configurazione del connettore associato all'erogazione identificata dal nome e dalla versione
     *
     */
	@Override
    public void updateErogazioneConnettore(Connettore body, String nome, Integer versione, ProfiloEnum profilo, String soggetto, String tipoServizio) {
		IContext context = this.getContext();
		try {
			context.getLogger().info("Invocazione in corso ...");     
			
			AuthorizationManager.authorize(context, getAuthorizationConfig());
			context.getLogger().debug("Autorizzazione completata con successo");
		
			if (body == null)
				throw FaultCode.RICHIESTA_NON_VALIDA.toException("Specificare un body");
			
			final ErogazioniEnv env = new ErogazioniEnv(context.getServletRequest(), profilo, soggetto, context);

			
			final AccordoServizioParteSpecifica asps = BaseHelper.supplyOrNotFound( 
					() -> ErogazioniApiHelper.getServizioIfErogazione(tipoServizio, nome, versione, env.idSoggetto.toIDSoggetto(), env)
					, "Erogazione"
				);
			

			IDServizio idServizio = env.idServizioFactory.getIDServizioFromAccordo(asps);
			IDPortaApplicativa idPA = env.paCore.getIDPortaApplicativaAssociataDefault(idServizio);

			final ServizioApplicativo sa = BaseHelper.supplyOrNotFound( 
					() -> env.saCore.getServizioApplicativo(env.saCore.getIdServizioApplicativo(env.idSoggetto.toIDSoggetto(), idPA.getNome()))
					,"Applicativo"
				);

			String endpointtype = body.getAutenticazioneHttp() != null ? TipiConnettore.HTTP.getNome() : TipiConnettore.DISABILITATO.getNome();
			endpointtype = body.getAutenticazioneHttps() != null ? TipiConnettore.HTTPS.getNome() : endpointtype;

			InvocazioneServizio is = sa.getInvocazioneServizio();
			InvocazioneCredenziali credenziali_is = is.getCredenziali();
			org.openspcoop2.core.config.Connettore connis = is.getConnettore();
			
			String oldConnT = connis.getTipo();
			if ((connis.getCustom()!=null && connis.getCustom()) && 
					!connis.getTipo().equals(TipiConnettore.HTTPS.toString()) && 
					!connis.getTipo().equals(TipiConnettore.FILE.toString())
					) {
				oldConnT = TipiConnettore.CUSTOM.toString();
			}
			
			if (!ErogazioniApiHelper.connettoreCheckData(body, env)) {
				throw FaultCode.RICHIESTA_NON_VALIDA.toException(StringEscapeUtils.unescapeHtml(env.pd.getMessage()));
			}
			
				
			ErogazioniApiHelper.fillConnettoreConfigurazione(connis, env,body, oldConnT);
								
			
			if (body.getAutenticazioneHttp() != null) {
				if (credenziali_is == null) {
					credenziali_is = new InvocazioneCredenziali();
				}
				credenziali_is.setUser(body.getAutenticazioneHttp().getUsername());
				credenziali_is.setPassword(body.getAutenticazioneHttp().getPassword());
				is.setCredenziali(credenziali_is);
				is.setAutenticazione(InvocazioneServizioTipoAutenticazione.BASIC);
			}
		
			else {
				is.setCredenziali(null);
				is.setAutenticazione(InvocazioneServizioTipoAutenticazione.NONE);
			}
	
			is.setConnettore(connis);
			sa.setInvocazioneServizio(is);		
			
			if(StatoFunzionalita.ABILITATO.equals(is.getGetMessage()) ||
					!TipiConnettore.DISABILITATO.toString().equals(endpointtype)){
				sa.setTipologiaErogazione(TipologiaErogazione.TRASPARENTE.getValue());
			}
			else{
				sa.setTipologiaErogazione(TipologiaErogazione.DISABILITATO.getValue());
			}
			
			StringBuffer inUsoMessage = new StringBuffer();
			ServiziApplicativiUtilities.checkStatoConnettore(env.saCore, sa, connis, inUsoMessage, System.lineSeparator());
			
			if ( inUsoMessage.length() > 0 )
				throw FaultCode.RICHIESTA_NON_VALIDA.toException(StringEscapeUtils.unescapeHtml( inUsoMessage.toString() ));


			env.saCore.performUpdateOperation(env.userLogin, false, sa); 
        
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
     * Consente di modificare le informazioni generali di un'erogazione di API
     *
     * Questa operazione consente di aggiornare le informazioni generali di un'erogazione di API identificata dal nome e dalla versione
     *
     */
	@Override
    public void updateErogazioneInformazioniGenerali(ApiImplInformazioniGenerali body, String nome, Integer versione, ProfiloEnum profilo, String soggetto, String tipoServizio) {
		IContext context = this.getContext();
		try {
			context.getLogger().info("Invocazione in corso ...");     

			AuthorizationManager.authorize(context, getAuthorizationConfig());
			context.getLogger().debug("Autorizzazione completata con successo");
			
 		    final ErogazioniEnv env = new ErogazioniEnv(context.getServletRequest(), profilo, soggetto, context);
		    final AccordoServizioParteSpecifica asps = BaseHelper.supplyOrNotFound( () -> ErogazioniApiHelper.getServizioIfErogazione(tipoServizio, nome, versione, env.idSoggetto.toIDSoggetto(), env), "Erogazione");
		    
		    ErogazioniApiHelper.updateInformazioniGenerali(body, env, asps, true);
        
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
     * Consente di modificare la configurazione utilizzata per identificare l'azione invocata dell'API implementata dall'erogazione
     *
     * Questa operazione consente di aggiornare la configurazione utilizzata dal Gateway per identificare l'azione invocata
     *
     */
	@Override
    public void updateErogazioneUrlInvocazione(ApiImplUrlInvocazione body, String nome, Integer versione, ProfiloEnum profilo, String soggetto, String tipoServizio) {
		IContext context = this.getContext();
		try {
			context.getLogger().info("Invocazione in corso ...");     

			AuthorizationManager.authorize(context, getAuthorizationConfig());
			context.getLogger().debug("Autorizzazione completata con successo");
			
			BaseHelper.throwIfNull(body);
			
			if (body.getModalita() == null)
				throw FaultCode.RICHIESTA_NON_VALIDA.toException("Specificare una modalità di identificazione azione valida");
		
			
			final ErogazioniEnv env = new ErogazioniEnv(context.getServletRequest(), profilo, soggetto, context);
			final AccordoServizioParteSpecifica asps = BaseHelper.supplyOrNotFound( () -> ErogazioniApiHelper.getServizioIfErogazione(tipoServizio, nome, versione, env.idSoggetto.toIDSoggetto(), env), "Accordo Servizio Parte Specifica");

			final IDPortaApplicativa idPorta 	= BaseHelper.supplyOrNotFound( () -> env.paCore.getIDPortaApplicativaAssociataDefault(env.idServizioFactory.getIDServizioFromAccordo(asps)), "Porta Applicativa Default");
			final PortaApplicativa pa 			= BaseHelper.supplyOrNotFound( () -> env.paCore.getPortaApplicativa(idPorta), "Porta Applicativa Default");
			
			final PortaApplicativaAzione paa 	= pa.getAzione() == null ? new PortaApplicativaAzione() : pa.getAzione();
			
			final AccordoServizioParteComuneSintetico apc = env.apcCore.getAccordoServizioSintetico(asps.getIdAccordo());
			List<PortaApplicativaAzioneIdentificazione> identModes = env.paHelper.getModalitaIdentificazionePorta(env.tipo_protocollo, env.apcCore.toMessageServiceBinding(apc.getServiceBinding()));
			
			if ( identModes.contains(PortaApplicativaAzioneIdentificazione.PROTOCOL_BASED) && identModes.size() == 1 ) {
				throw FaultCode.RICHIESTA_NON_VALIDA.toException("Non puoi modificare la url-invocazione il cui metodo di identificazione azioni può essere solo " + PortaApplicativaAzioneIdentificazione.PROTOCOL_BASED.toString() );
			}
			
			if ( !identModes.contains( PortaApplicativaAzioneIdentificazione.valueOf(body.getModalita().name()) )) {
				throw FaultCode.RICHIESTA_NON_VALIDA.toException("La modalità di identificazione azione deve essere una fra: " + identModes.toString() );
			}
			
			switch (body.getModalita()) {
			case CONTENT_BASED:
				paa.setPattern(body.getPattern());
				paa.setForceInterfaceBased(Helper.boolToStatoFunzionalitaConf(body.isForceInterface()));
				break;
			case HEADER_BASED:
				paa.setPattern(body.getNome());
				paa.setForceInterfaceBased(Helper.boolToStatoFunzionalitaConf(body.isForceInterface()));
				break;
			case INPUT_BASED:
				paa.setForceInterfaceBased(Helper.boolToStatoFunzionalitaConf(body.isForceInterface()));
				break;
			case INTERFACE_BASED:
				break;
			case SOAP_ACTION_BASED:
				paa.setForceInterfaceBased(Helper.boolToStatoFunzionalitaConf(body.isForceInterface()));
				break;
			case URL_BASED:
				paa.setPattern(body.getPattern());
				paa.setForceInterfaceBased(Helper.boolToStatoFunzionalitaConf(body.isForceInterface()));
				break;
			case PROTOCOL_BASED:
				break;
			}
			paa.setIdentificazione(Enum.valueOf(PortaApplicativaAzioneIdentificazione.class, body.getModalita().name()));
			
			String servizio = pa.getTipoSoggettoProprietario()+"/"+pa.getNomeSoggettoProprietario()+" "+pa.getServizio().getTipo()+"/"+pa.getServizio().getNome() + "/" + pa.getServizio().getVersione().intValue(); 
				
			env.requestWrapper.overrideParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID, pa.getId().toString());
			env.requestWrapper.overrideParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_NOME_PORTA, pa.getNome());			
			env.requestWrapper.overrideParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_SOGGETTO, env.idSoggetto.getId().toString());
			env.requestWrapper.overrideParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_SOGGETTO_VIRTUALE, "-");				// Come da debug.
		    env.requestWrapper.overrideParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_SERVIZIO, servizio);				
			env.requestWrapper.overrideParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_AZIONE, paa.getPattern());			// Azione è il contenuto del campo pattern o del campo nome, che vengono settati nel campo pattern.
			env.requestWrapper.overrideParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_AZIONE_ID, null);						// Come da debug
			env.requestWrapper.overrideParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_MODE_AZIONE, paa.getIdentificazione().toString() );
			env.requestWrapper.overrideParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_XSD, null);
			env.requestWrapper.overrideParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_BEHAVIOUR, "");
			env.requestWrapper.overrideParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_INTEGRAZIONE, "");
			
			final String oldNomePA = pa.getNome();
			if (!env.paHelper.porteAppCheckData(TipoOperazione.CHANGE, oldNomePA, env.isSupportatoAutenticazioneSoggetti)) {
				throw FaultCode.RICHIESTA_NON_VALIDA.toException(StringEscapeUtils.unescapeHtml(env.pd.getMessage()));
			}

			pa.setOldIDPortaApplicativaForUpdate(idPorta);
			pa.setAzione(paa);
			
			env.paCore.performUpdateOperation(env.userLogin, false, pa);

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

