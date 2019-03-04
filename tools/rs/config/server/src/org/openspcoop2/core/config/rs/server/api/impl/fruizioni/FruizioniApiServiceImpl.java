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

package org.openspcoop2.core.config.rs.server.api.impl.fruizioni;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.openspcoop2.core.commons.Filtri;
import org.openspcoop2.core.commons.Liste;
import org.openspcoop2.core.config.PortaDelegata;
import org.openspcoop2.core.config.PortaDelegataAzione;
import org.openspcoop2.core.config.constants.PortaApplicativaAzioneIdentificazione;
import org.openspcoop2.core.config.constants.PortaDelegataAzioneIdentificazione;
import org.openspcoop2.core.config.rs.server.api.FruizioniApi;
import org.openspcoop2.core.config.rs.server.api.impl.Helper;
import org.openspcoop2.core.config.rs.server.api.impl.erogazioni.ErogazioniApiHelper;
import org.openspcoop2.core.config.rs.server.api.impl.erogazioni.ErogazioniEnv;
import org.openspcoop2.core.config.rs.server.config.ServerProperties;
import org.openspcoop2.core.config.rs.server.model.ApiImplAllegato;
import org.openspcoop2.core.config.rs.server.model.ApiImplInformazioniGenerali;
import org.openspcoop2.core.config.rs.server.model.ApiImplInformazioniGeneraliView;
import org.openspcoop2.core.config.rs.server.model.ApiImplUrlInvocazione;
import org.openspcoop2.core.config.rs.server.model.ApiImplUrlInvocazioneView;
import org.openspcoop2.core.config.rs.server.model.ApiImplVersioneApi;
import org.openspcoop2.core.config.rs.server.model.ApiImplVersioneApiView;
import org.openspcoop2.core.config.rs.server.model.Connettore;
import org.openspcoop2.core.config.rs.server.model.Fruizione;
import org.openspcoop2.core.config.rs.server.model.FruizioneViewItem;
import org.openspcoop2.core.config.rs.server.model.ListaApiImplAllegati;
import org.openspcoop2.core.config.rs.server.model.ListaFruizioni;
import org.openspcoop2.core.config.rs.server.model.ModalitaIdentificazioneAzioneEnum;
import org.openspcoop2.core.config.rs.server.model.ProfiloEnum;
import org.openspcoop2.core.config.rs.server.model.TipoApiEnum;
import org.openspcoop2.core.constants.TipiConnettore;
import org.openspcoop2.core.id.IDPortaDelegata;
import org.openspcoop2.core.id.IDServizio;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.core.registry.AccordoServizioParteComune;
import org.openspcoop2.core.registry.AccordoServizioParteSpecifica;
import org.openspcoop2.core.registry.Documento;
import org.openspcoop2.core.registry.Fruitore;
import org.openspcoop2.core.registry.IdSoggetto;
import org.openspcoop2.core.registry.Soggetto;
import org.openspcoop2.core.registry.driver.DriverRegistroServiziException;
import org.openspcoop2.core.registry.driver.DriverRegistroServiziNotFound;
import org.openspcoop2.core.registry.driver.IDServizioFactory;
import org.openspcoop2.protocol.information_missing.constants.StatoType;
import org.openspcoop2.utils.json.JSONUtils;
import org.openspcoop2.utils.service.BaseImpl;
import org.openspcoop2.utils.service.authorization.AuthorizationConfig;
import org.openspcoop2.utils.service.authorization.AuthorizationManager;
import org.openspcoop2.utils.service.context.IContext;
import org.openspcoop2.utils.service.fault.jaxrs.FaultCode;
import org.openspcoop2.web.ctrlstat.core.Search;
import org.openspcoop2.web.ctrlstat.servlet.aps.AccordiServizioParteSpecificaCostanti;
import org.openspcoop2.web.ctrlstat.servlet.aps.AccordiServizioParteSpecificaUtilities;
import org.openspcoop2.web.lib.mvc.ServletUtils;
import org.openspcoop2.web.lib.mvc.TipoOperazione;

/**
 * FruizioniApiServiceImpl
 * 
 * @author $Author$
 * @version $Rev$, $Date$
 * 
 */
public class FruizioniApiServiceImpl extends BaseImpl implements FruizioniApi {

	public FruizioniApiServiceImpl(){
		super(org.slf4j.LoggerFactory.getLogger(FruizioniApiServiceImpl.class));
	}

	private AuthorizationConfig getAuthorizationConfig() throws Exception{
		return new AuthorizationConfig(ServerProperties.getInstance().getProperties());
	}

    /**
     * Creazione di una fruizione di API
     *
     * Questa operazione consente di creare una fruizione di API
     *
     */
	@Override
    public void createFruizione(Fruizione body, ProfiloEnum profilo, String soggetto) {
		IContext context = this.getContext();
		try {
			context.getLogger().info("Invocazione in corso ...");     

			AuthorizationManager.authorize(context, getAuthorizationConfig());
			context.getLogger().debug("Autorizzazione completata con successo");
			
			Helper.throwIfNull(body);
			         
            final ErogazioniEnv env = new ErogazioniEnv(context.getServletRequest(), profilo, soggetto, context);
            
            final IdSoggetto idero   = new IdSoggetto(new IDSoggetto(env.tipo_soggetto,body.getErogatore()));
            final Soggetto erogatore = Helper.supplyOrNotFound( () -> env.soggettiCore.getSoggettoRegistro(idero.toIDSoggetto()), "Soggetto Erogatore");
            idero.setId(erogatore.getId());
            final Soggetto fruitore  = env.soggettiCore.getSoggettoRegistro(env.idSoggetto.toIDSoggetto());
            
            IDSoggetto idReferente = ErogazioniApiHelper.getIdReferente(body, env);
            final AccordoServizioParteComune as = Helper.getAccordo(body.getApiNome(), body.getApiVersione(), idReferente,env.apcCore);
            
            if ( as == null ) {
            	throw FaultCode.RICHIESTA_NON_VALIDA.toException("Nessuna Api registrata con nome " + body.getApiNome() + " e versione " + body.getApiVersione());
            }
            
            AccordoServizioParteSpecifica asps = ErogazioniApiHelper.apiImplToAps(body, erogatore, as, env);
            final IDServizio idAps = env.idServizioFactory.getIDServizioFromValues(asps.getTipo(), asps.getNome(), new IDSoggetto(asps.getTipoSoggettoErogatore(), asps.getNomeSoggettoErogatore()), asps.getVersione());
            final boolean alreadyExists = env.apsCore.existsAccordoServizioParteSpecifica(idAps);
            
            if (alreadyExists)
            	asps = env.apsCore.getServizio(idAps);
            
          
            ServletUtils.setObjectIntoSession(context.getServletRequest().getSession(), AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_TIPO_EROGAZIONE_VALUE_FRUIZIONE, AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_TIPO_EROGAZIONE);  
            ErogazioniApiHelper.serviziCheckData(TipoOperazione.ADD, env, as, asps, env.idSoggetto, body, false);
            
   
			org.openspcoop2.core.registry.Connettore regConnettore = ErogazioniApiHelper.buildConnettoreRegistro(env, body.getConnettore());
			
			Fruitore f = new Fruitore();
			f.setTipo(fruitore.getTipo());
			f.setNome(fruitore.getNome());
			f.setStatoPackage(StatoType.FINALE.getValue());
			f.setConnettore(regConnettore);
			asps.addFruitore(f);
         
            ErogazioniApiHelper.createAps(env, asps, regConnettore, body, alreadyExists, false);
            	
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
     * Creazione di un allegato nella fruizione di API
     *
     * Questa operazione consente di aggiungere un allegato alla fruizione di API identificata dall'erogatore, dal nome e dalla versione
     *
     */
	@Override
    public void createFruizioneAllegato(ApiImplAllegato body, String erogatore, String nome, Integer versione, ProfiloEnum profilo, String soggetto, String tipoServizio) {
		IContext context = this.getContext();
		try {
			context.getLogger().info("Invocazione in corso ...");     

			AuthorizationManager.authorize(context, getAuthorizationConfig());
			context.getLogger().debug("Autorizzazione completata con successo");
			
			Helper.throwIfNull(body);
            
			final ErogazioniEnv env = new ErogazioniEnv(context.getServletRequest(), profilo, soggetto, context);            
			final IDSoggetto idErogatore = new IDSoggetto(env.tipo_soggetto, erogatore);
			final AccordoServizioParteSpecifica asps = Helper.supplyOrNotFound( () -> ErogazioniApiHelper.getServizioIfFruizione(tipoServizio, nome, versione, idErogatore, env.idSoggetto.toIDSoggetto(), env), "Fruizione");
			final IDServizio idServizio = env.idServizioFactory.getIDServizioFromAccordo(asps);

			
			try {
				env.pdCore.getIDPorteDelegateAssociate(idServizio, env.idSoggetto.toIDSoggetto());
			} catch (Exception e) {
				throw FaultCode.NOT_FOUND.toException(e.getMessage());
			}
			
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
     * Elimina una fruizione di api
     *
     * Questa operazione consente di eliminare una fruizione di API identificata dall'erogatore, dal nome e dalla versione
     *
     */
	@Override
    public void deleteFruizione(String erogatore, String nome, Integer versione, ProfiloEnum profilo, String soggetto, String tipoServizio) {
		IContext context = this.getContext();
		try {
			context.getLogger().info("Invocazione in corso ...");     

			AuthorizationManager.authorize(context, getAuthorizationConfig());
			context.getLogger().debug("Autorizzazione completata con successo");
			
			 
			final ErogazioniEnv env = new ErogazioniEnv(context.getServletRequest(), profilo, soggetto, context);            
			final IDSoggetto idErogatore = new IDSoggetto(env.tipo_soggetto, erogatore);
			final AccordoServizioParteSpecifica asps = Helper.evalnull( () -> ErogazioniApiHelper.getServizioIfFruizione(tipoServizio, nome, versione, idErogatore, env.idSoggetto.toIDSoggetto(), env) );
			
			if ( asps != null ) {
				final IDServizio idServizio = env.idServizioFactory.getIDServizioFromAccordo(asps);
				final StringBuffer inUsoMessage =  new StringBuffer();
				
			
				AccordiServizioParteSpecificaUtilities.deleteAccordoServizioParteSpecifica(
						asps, 
						true, // gestioneFruitori, 
						false, //gestioneErogatori, 
						env.idSoggetto.toIDSoggetto(),
						idServizio,
						env.paCore.getExtendedServletPortaApplicativa(),
						env.userLogin,
						env.apsCore, 
						env.apsHelper, 
						inUsoMessage, 
						"\n");
	                        
				if (inUsoMessage.length() > 0) {
					throw FaultCode.RICHIESTA_NON_VALIDA.toException(inUsoMessage.toString());
				}
			} else if ( env.delete_404 ) {
				throw FaultCode.NOT_FOUND.toException("Fruizione inesistente");
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
     * Elimina un allegato dalla fruizione
     *
     * Questa operazione consente di eliminare un'allegato dalla fruizione di API identificata dall'erogatore, dal nome e dalla versione
     *
     */
	@Override
    public void deleteFruizioneAllegato(String erogatore, String nome, Integer versione, String nomeAllegato, ProfiloEnum profilo, String soggetto, String tipoServizio) {
		IContext context = this.getContext();
		try {
			context.getLogger().info("Invocazione in corso ...");     

			AuthorizationManager.authorize(context, getAuthorizationConfig());
			context.getLogger().debug("Autorizzazione completata con successo");
			
			 
			final ErogazioniEnv env = new ErogazioniEnv(context.getServletRequest(), profilo, soggetto, context);            
			final IDSoggetto idErogatore = new IDSoggetto(env.tipo_soggetto, erogatore);
			final AccordoServizioParteSpecifica asps = Helper.supplyOrNotFound( () -> ErogazioniApiHelper.getServizioIfFruizione(tipoServizio, nome, versione, idErogatore, env.idSoggetto.toIDSoggetto(),  env), "Fruizione");
			
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
     * Restituisce l'allegato di una fruizione
     *
     * Questa operazione consente di ottenere l'allegato di un'erogazione di API identificata dall'erogatore, dal nome e dalla versione
     *
     */
	@Override
    public byte[] downloadFruizioneAllegato(String erogatore, String nome, Integer versione, String nomeAllegato, ProfiloEnum profilo, String soggetto, String tipoServizio) {
		IContext context = this.getContext();
		try {
			context.getLogger().info("Invocazione in corso ...");     

			AuthorizationManager.authorize(context, getAuthorizationConfig());
			context.getLogger().debug("Autorizzazione completata con successo");
			
			final ErogazioniEnv env = new ErogazioniEnv(context.getServletRequest(), profilo, soggetto, context);            
			final IDSoggetto idErogatore = new IDSoggetto(env.tipo_soggetto, erogatore);
			final AccordoServizioParteSpecifica asps = Helper.supplyOrNotFound( () -> ErogazioniApiHelper.getServizioIfFruizione(tipoServizio, nome, versione, idErogatore, env.idSoggetto.toIDSoggetto(), env), "Fruizione");
			
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
     * Ricerca fruizioni di api
     *
     * Elenca le fruizioni di API
     *
     */
	@Override
    public ListaFruizioni findAllFruizioni(ProfiloEnum profilo, String soggetto, String q, Integer limit, Integer offset, TipoApiEnum tipoApi) {
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
			
			List<AccordoServizioParteSpecifica> lista = env.apsCore.soggettiServizioList(null, ricerca, null, true, false);
			
			if ( env.findall_404 && lista.isEmpty() ) {
				throw FaultCode.NOT_FOUND.toException("Nessuna fruizione presente nel registro");
			}
			
			final ListaFruizioni ret = Helper.costruisciListaPaginata(
					context.getServletRequest().getRequestURI(), 
					offset, 
					limit, 
					ricerca.getNumEntries(idLista), 
					ListaFruizioni.class
				);

			
			// Prendo le fruizioni una ad una dagli asps
			lista.forEach( asps -> {
					asps.getFruitoreList().forEach( fruitore -> {
						
						try {
							IdSoggetto idFruitore;
							idFruitore = new IdSoggetto(env.soggettiCore.getIdSoggettoRegistro(fruitore.getIdSoggetto()));
							idFruitore.setId(fruitore.getIdSoggetto());
							ret.addItemsItem( ErogazioniApiHelper.fruizioneViewItemToFruizioneItem(ErogazioniApiHelper.aspsToFruizioneViewItem(env, asps,idFruitore)));
							
						} catch (DriverRegistroServiziNotFound | DriverRegistroServiziException e) {
							// Cosa fare in questi casi? Errore interno perchè il fruitore deve essere necessariamente presente 
							// dato che l'erogazione l'abbiamo appena presa dal db?
							// Però altri utenti avrebbero potuto contemporaneamente modificare il db e l'assenza del fruitore potrebbe essere perchè esso è stato appena eliminato.
							// Questa osservazione vale in generale.
							throw new RuntimeException(e);	
						}
					});
					
				});
			
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
     * Elenco allegati di una fruizione di API
     *
     * Questa operazione consente di ottenere gli allegati di una fruizione di API identificata dall'erogatore, dal nome e dalla versione
     *
     */
	@Override
    public ListaApiImplAllegati findAllFruizioneAllegati(String erogatore, String nome, Integer versione, ProfiloEnum profilo, String soggetto, String tipoServizio, String q, Integer limit, Integer offset) {
		IContext context = this.getContext();
		try {
			context.getLogger().info("Invocazione in corso ...");     

			AuthorizationManager.authorize(context, getAuthorizationConfig());
			context.getLogger().debug("Autorizzazione completata con successo");     
			
			final ErogazioniEnv env = new ErogazioniEnv(context.getServletRequest(), profilo, soggetto, context);            
			final IDSoggetto idErogatore = new IDSoggetto(env.tipo_soggetto, erogatore);
			final AccordoServizioParteSpecifica asps = Helper.supplyOrNotFound( () -> ErogazioniApiHelper.getServizioIfFruizione(tipoServizio, nome, versione, idErogatore, env.idSoggetto.toIDSoggetto(),  env), "Fruizione");
			
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
     * Restituisce i dettagli di una fruizione di API
     *
     * Questa operazione consente di ottenere i dettagli di una fruizione di API identificata dall'erogatore, dal nome e dalla versione
     *
     */
	@Override
    public FruizioneViewItem getFruizione(String erogatore, String nome, Integer versione, ProfiloEnum profilo, String soggetto, String tipoServizio) {
		IContext context = this.getContext();
		try {
			context.getLogger().info("Invocazione in corso ...");     

			AuthorizationManager.authorize(context, getAuthorizationConfig());
			context.getLogger().debug("Autorizzazione completata con successo");     
			
			final ErogazioniEnv env = new ErogazioniEnv(context.getServletRequest(), profilo, soggetto, context);            
			final IDSoggetto idErogatore = new IDSoggetto(env.tipo_soggetto, erogatore);
			final AccordoServizioParteSpecifica asps = Helper.supplyOrNotFound( () -> ErogazioniApiHelper.getServizioIfFruizione(tipoServizio, nome, versione, idErogatore, env.idSoggetto.toIDSoggetto(),  env), "Fruizione");
            
			FruizioneViewItem ret = ErogazioniApiHelper.aspsToFruizioneViewItem(env, asps, env.idSoggetto);
			
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
     * Restituisce le informazioni sull'API implementata dalla fruizione
     *
     * Questa operazione consente di ottenere le informazioni sull'API implementata dall'erogazione identificata dall'erogatore, dal nome e dalla versione
     *
     */
	@Override
    public ApiImplVersioneApiView getFruizioneAPI(String erogatore, String nome, Integer versione, ProfiloEnum profilo, String soggetto, String tipoServizio) {
		IContext context = this.getContext();
		try {
			context.getLogger().info("Invocazione in corso ...");     

			AuthorizationManager.authorize(context, getAuthorizationConfig());
			context.getLogger().debug("Autorizzazione completata con successo");
			

			final ErogazioniEnv env = new ErogazioniEnv(context.getServletRequest(), profilo, soggetto, context);            
			final IDSoggetto idErogatore = new IDSoggetto(env.tipo_soggetto, erogatore);
			final AccordoServizioParteSpecifica asps = Helper.supplyOrNotFound( () -> ErogazioniApiHelper.getServizioIfFruizione(tipoServizio, nome, versione, idErogatore, env.idSoggetto.toIDSoggetto(),  env), "Fruizione");
			
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
     * Restituisce il dettaglio di un allegato della fruizione
     *
     * Questa operazione consente di ottenere il dettaglio di un allegato della fruizione di API identificata dall'erogatore, dal nome e dalla versione
     *
     */
	@Override
    public ApiImplAllegato getFruizioneAllegato(String erogatore, String nome, Integer versione, String nomeAllegato, ProfiloEnum profilo, String soggetto, String tipoServizio) {
		IContext context = this.getContext();
		try {
			context.getLogger().info("Invocazione in corso ...");     

			AuthorizationManager.authorize(context, getAuthorizationConfig());
			context.getLogger().debug("Autorizzazione completata con successo");     
			
			final ErogazioniEnv env = new ErogazioniEnv(context.getServletRequest(), profilo, soggetto, context);            
			final IDSoggetto idErogatore = new IDSoggetto(env.tipo_soggetto, erogatore);
			final AccordoServizioParteSpecifica asps = Helper.supplyOrNotFound( () -> ErogazioniApiHelper.getServizioIfFruizione(tipoServizio, nome, versione, idErogatore, env.idSoggetto.toIDSoggetto(), env), "Accordo servizio parte specifica");
			
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
     * Restituisce le informazioni su connettore associato alla fruizione
     *
     * Questa operazione consente di ottenere le informazioni sul connettore associato alla fruizione identificata dall'erogatore, dal nome e dalla versione
     *
     */
	@Override
    public Connettore getFruizioneConnettore(String erogatore, String nome, Integer versione, ProfiloEnum profilo, String soggetto, String tipoServizio) {
		IContext context = this.getContext();
		try {
			context.getLogger().info("Invocazione in corso ...");     

			AuthorizationManager.authorize(context, getAuthorizationConfig());
			context.getLogger().debug("Autorizzazione completata con successo");
			
			final ErogazioniEnv env = new ErogazioniEnv(context.getServletRequest(), profilo, soggetto, context);            
			final IDSoggetto idErogatore = new IDSoggetto(env.tipo_soggetto, erogatore);
			final AccordoServizioParteSpecifica asps = Helper.supplyOrNotFound( () -> ErogazioniApiHelper.getServizioIfFruizione(tipoServizio, nome, versione, idErogatore, env.idSoggetto.toIDSoggetto(), env), "Fruizione");
			
			org.openspcoop2.core.registry.Connettore regConn = ErogazioniApiHelper.getConnettoreFruizione(asps, env.idSoggetto, env);
			Map<String, String> props = regConn.getProperties();	// Qui c'è solo la proprietà location.
			
			Connettore c = ErogazioniApiHelper.buildConnettore(props);
			
			context.getLogger().info("Invocazione completata con successo");
			return c;
     
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
     * Restituisce le informazioni generali di una fruizione di API
     *
     * Questa operazione consente di ottenere le informazioni generali di una fruizione di API identificata dall'erogatore, dal nome e dalla versione
     *
     */
	@Override
    public ApiImplInformazioniGeneraliView getFruizioneInformazioniGenerali(String erogatore, String nome, Integer versione, ProfiloEnum profilo, String soggetto, String tipoServizio) {
		IContext context = this.getContext();
		try {
			context.getLogger().info("Invocazione in corso ...");     

			AuthorizationManager.authorize(context, getAuthorizationConfig());
			context.getLogger().debug("Autorizzazione completata con successo");     
			
			final ErogazioniEnv env = new ErogazioniEnv(context.getServletRequest(), profilo, soggetto, context);            
			final IDSoggetto idErogatore = new IDSoggetto(env.tipo_soggetto, erogatore);
			final AccordoServizioParteSpecifica asps = Helper.supplyOrNotFound( () -> ErogazioniApiHelper.getServizioIfFruizione(tipoServizio, nome, versione, idErogatore, env.idSoggetto.toIDSoggetto(), env), "Fruizione");
                                
			ApiImplInformazioniGeneraliView ret = ErogazioniApiHelper.fruizioneToApiImplInformazioniGeneraliView(env, asps, env.idSoggetto);
                        
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
     * Restituisce le informazioni sull'url di invocazione necessaria ad invocare la fruizione
     *
     * Questa operazione consente di ottenere le informazioni sull'url di invocazione necessaria ad invocare la fruizione identificata dall'erogatore, dal nome e dalla versione
     *
     */
	@Override
    public ApiImplUrlInvocazioneView getFruizioneUrlInvocazione(String erogatore, String nome, Integer versione, ProfiloEnum profilo, String soggetto, String tipoServizio) {
		IContext context = this.getContext();
		try {
			context.getLogger().info("Invocazione in corso ...");     

			AuthorizationManager.authorize(context, getAuthorizationConfig());
			context.getLogger().debug("Autorizzazione completata con successo");
			
			final ErogazioniEnv env = new ErogazioniEnv(context.getServletRequest(), profilo, soggetto, context);
		    final IDSoggetto idErogatore = new IDSoggetto(env.tipo_soggetto, erogatore);
			final AccordoServizioParteSpecifica asps = Helper.supplyOrNotFound( () -> ErogazioniApiHelper.getServizioIfFruizione(tipoServizio, nome, versione, idErogatore, env.idSoggetto.toIDSoggetto(), env), "Fruizione");
			final AccordoServizioParteComune aspc = env.apcCore.getAccordoServizio(asps.getIdAccordo());
			final IDPortaDelegata idPorta = Helper.supplyOrNotFound( () -> env.pdCore.getIDPortaDelegataAssociataDefault(env.idServizioFactory.getIDServizioFromAccordo(asps), env.idSoggetto.toIDSoggetto()), "Porta Delegata");
			final PortaDelegata pd  = Helper.supplyOrNotFound( () -> env.pdCore.getPortaDelegata(idPorta), "Porta Delegata");
			final PortaDelegataAzione pdAzione =  pd.getAzione();
			
			String urlInvocazione = ErogazioniApiHelper.getUrlInvocazioneFruizione(asps, env.idSoggetto.toIDSoggetto(), env);
			ApiImplUrlInvocazioneView ret = new ApiImplUrlInvocazioneView();
			
			Map<String,String> azioni = env.paCore.getAzioniConLabel(asps, aspc, false, true, new ArrayList<String>()); 
			ret.setAzioni(Arrays.asList(azioni.keySet().toArray(new String[azioni.size()])));		
			ret.setForceInterface(Helper.statoFunzionalitaConfToBool(pdAzione.getForceInterfaceBased()));
			ret.setModalita(Enum.valueOf(ModalitaIdentificazioneAzioneEnum.class, pdAzione.getIdentificazione().name()));
			ret.setNome(nome);
			ret.setPattern(pdAzione.getPattern());
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
     * Consente di modificare la versione dell'API implementata dalla fruizione
     *
     * Questa operazione consente di aggiornare la versione dell'API implementata dall'erogazione identificata dall'erogatore, dal nome e dalla versione
     *
     */
	@Override
    public void updateFruizioneAPI(ApiImplVersioneApi body, String erogatore, String nome, Integer versione, ProfiloEnum profilo, String soggetto, String tipoServizio) {
		IContext context = this.getContext();
		try {
			context.getLogger().info("Invocazione in corso ...");
			
			Helper.throwIfNull(body);

			AuthorizationManager.authorize(context, getAuthorizationConfig());
			context.getLogger().debug("Autorizzazione completata con successo");
			
			final ErogazioniEnv env = new ErogazioniEnv(context.getServletRequest(), profilo, soggetto, context);
            final IdSoggetto idErogatore   = new IdSoggetto(new IDSoggetto(env.tipo_soggetto,erogatore));
            final Soggetto soggErogatore = Helper.supplyOrNotFound( () -> env.soggettiCore.getSoggettoRegistro(idErogatore.toIDSoggetto()), "Soggetto Erogatore");
            idErogatore.setId(soggErogatore.getId());
            
			final AccordoServizioParteSpecifica asps = Helper.supplyOrNotFound( () -> ErogazioniApiHelper.getServizioIfFruizione(tipoServizio, nome, versione, idErogatore.toIDSoggetto(), env.idSoggetto.toIDSoggetto(), env), "Fruizione");
			final AccordoServizioParteComune as = env.apcCore.getAccordoServizio(asps.getIdAccordo());

	        List<AccordoServizioParteComune> asParteComuneCompatibili = env.apsCore.findAccordiParteComuneBySoggettoAndNome(
	        		as.getNome(),
	                new IDSoggetto(as.getSoggettoReferente().getTipo(), as.getSoggettoReferente().getNome())
	        	);
	        
	        Optional<AccordoServizioParteComune> newApc = Helper.findFirst( asParteComuneCompatibili, a -> a.getVersione() == body.getApiVersione());
	        
	        if ( !newApc.isPresent() ) {
	        	throw FaultCode.RICHIESTA_NON_VALIDA.toException("Nessuna api " + as.getNome() + " e versione " + body.getApiVersione() + " registrata");
	        }

			asps.setAccordoServizioParteComune(env.idAccordoFactory.getUriFromAccordo(newApc.get()));
			asps.setIdAccordo(newApc.get().getId());
			
			asps.setOldIDServizioForUpdate(env.idServizioFactory.getIDServizioFromAccordo(asps));
			
			ErogazioniApiHelper.serviziCheckData(TipoOperazione.CHANGE, env, as, asps, null, null, true);
			
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
     * Modifica i dati di un allegato della fruizione
     *
     * Questa operazione consente di aggiornare i dettagli di un allegato della fruizione di API identificata dall'erogatore, dal nome e dalla versione
     *
     */
	@Override
    public void updateFruizioneAllegato(ApiImplAllegato body, String erogatore, String nome, Integer versione, String nomeAllegato, ProfiloEnum profilo, String soggetto, String tipoServizio) {
		IContext context = this.getContext();
		try {
			context.getLogger().info("Invocazione in corso ...");     

			AuthorizationManager.authorize(context, getAuthorizationConfig());
			context.getLogger().debug("Autorizzazione completata con successo");
			
			final ErogazioniEnv env = new ErogazioniEnv(context.getServletRequest(), profilo, soggetto, context);            
			final IDSoggetto idErogatore = new IDSoggetto(env.tipo_soggetto, erogatore);
			final AccordoServizioParteSpecifica asps = Helper.supplyOrNotFound( () -> ErogazioniApiHelper.getServizioIfFruizione(tipoServizio, nome, versione, idErogatore, env.idSoggetto.toIDSoggetto(), env), "Fruizione");
						
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
     * Consente di modificare la configurazione del connettore associato alla fruizione
     *
     * Questa operazione consente di aggiornare la configurazione del connettore associato alla fruizione identificata dall'erogatore, dal nome e dalla versione
     *
     */
	@Override
    public void updateFruizioneConnettore(Connettore body, String erogatore, String nome, Integer versione, ProfiloEnum profilo, String soggetto, String tipoServizio) {
		IContext context = this.getContext();
		try {
			context.getLogger().info("Invocazione in corso ...");     

			AuthorizationManager.authorize(context, getAuthorizationConfig());
			context.getLogger().debug("Autorizzazione completata con successo");
			
			Helper.throwIfNull(body);
			
			final ErogazioniEnv env = new ErogazioniEnv(context.getServletRequest(), profilo, soggetto, context);            
			final IDSoggetto idErogatore = new IDSoggetto(env.tipo_soggetto, erogatore);
			
			final AccordoServizioParteSpecifica asps = Helper.supplyOrNotFound( () -> ErogazioniApiHelper.getServizioIfFruizione(tipoServizio, nome, versione, idErogatore, env.idSoggetto.toIDSoggetto(), env), "Fruizione");
			
			final List<Fruitore> fruitori = asps.getFruitoreList();
					
		    long idServizioFruitoreInt = env.apsCore.getServizioFruitore(IDServizioFactory.getInstance().getIDServizioFromAccordo(asps), env.idSoggetto.getId());
			final Fruitore servFru = env.apsCore.getServizioFruitore(idServizioFruitoreInt);
			final Fruitore fruitore = Helper.findAndRemoveFirst(fruitori, f -> f.getId() == servFru.getId());
			
			if (fruitore == null)
				throw FaultCode.NOT_FOUND.toException("Soggetto fruitore " + env.idSoggetto.toString() + "non registrato per la fruizione scelta" );
			
			final org.openspcoop2.core.registry.Connettore connettore = fruitore.getConnettore();
			
			String oldConnT = connettore.getTipo();
			if ((connettore.getCustom()!=null && connettore.getCustom()) && 
					!connettore.getTipo().equals(TipiConnettore.HTTPS.toString()) && 
					!connettore.getTipo().equals(TipiConnettore.FILE.toString())
					) {
				oldConnT = TipiConnettore.CUSTOM.toString();
			}
			
			ErogazioniApiHelper.fillConnettoreRegistro(connettore,env, body, oldConnT);

			if (!ErogazioniApiHelper.connettoreCheckData(body, env)) {
				throw FaultCode.RICHIESTA_NON_VALIDA.toException(env.pd.getMessage());
			}							
			
			
			fruitore.setConnettore(ErogazioniApiHelper.buildConnettoreRegistro(env, body));
			fruitori.add(fruitore);
			asps.setFruitoreList(fruitori);
                        
			env.apsCore.performUpdateOperation(env.userLogin, false, asps);
		
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
     * Consente di modificare le informazioni generali di una fruizione di API
     *
     * Questa operazione consente di aggiornare le informazioni generali di una fruizione di API identificata  dall'erogatore, dal nome e dalla versione
     *
     */
	@Override
    public void updateFruizioneInformazioniGenerali(ApiImplInformazioniGenerali body, String erogatore, String nome, Integer versione, ProfiloEnum profilo, String soggetto, String tipoServizio) {
		IContext context = this.getContext();
		try {
			context.getLogger().info("Invocazione in corso ...");     
			

			AuthorizationManager.authorize(context, getAuthorizationConfig());
			context.getLogger().debug("Autorizzazione completata con successo");
			
 		    final ErogazioniEnv env = new ErogazioniEnv(context.getServletRequest(), profilo, soggetto, context);
 		    final IDSoggetto idErogatore = new IDSoggetto(env.tipo_soggetto, erogatore);
			final AccordoServizioParteSpecifica asps = Helper.supplyOrNotFound( () -> ErogazioniApiHelper.getServizioIfFruizione(tipoServizio, nome, versione, idErogatore, env.idSoggetto.toIDSoggetto(), env), "Fruizione");
			
		    ErogazioniApiHelper.updateInformazioniGenerali(body, env, asps);
        
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
     * Consente di modificare la configurazione utilizzata per identificare l'azione invocata dell'API implementata dalla fruizione
     *
     * Questa operazione consente di aggiornare la configurazione utilizzata dal Gateway per identificare l'azione invocata
     *
     */
	@Override
    public void updateFruizioneUrlInvocazione(Object body, String erogatore, String nome, Integer versione, ProfiloEnum profilo, String soggetto, String tipoServizio) {
		IContext context = this.getContext();
		try {
			context.getLogger().info("Invocazione in corso ...");     

			AuthorizationManager.authorize(context, getAuthorizationConfig());
			context.getLogger().debug("Autorizzazione completata con successo");     
  			
			Helper.throwIfNull(body);
	
			final ApiImplUrlInvocazione urlInvocazione = JSONUtils.getInstance().getAsObject((InputStream) body, ApiImplUrlInvocazione.class);
			final ErogazioniEnv env = new ErogazioniEnv(context.getServletRequest(), profilo, soggetto, context);
		    final IDSoggetto idErogatore = new IDSoggetto(env.tipo_soggetto, erogatore);
			final AccordoServizioParteSpecifica asps = Helper.supplyOrNotFound( () -> ErogazioniApiHelper.getServizioIfFruizione(tipoServizio, nome, versione, idErogatore, env.idSoggetto.toIDSoggetto(), env), "Fruizione");
			final IDServizio idServizio = env.idServizioFactory.getIDServizioFromAccordo(asps);			
			final IDPortaDelegata idPorta = Helper.supplyOrNotFound( () -> env.pdCore.getIDPortaDelegataAssociataDefault(env.idServizioFactory.getIDServizioFromAccordo(asps), env.idSoggetto.toIDSoggetto()), "Porta Delegata");
			final PortaDelegata pd  = Helper.supplyOrNotFound( () -> env.pdCore.getPortaDelegata(idPorta), "Porta Delegata");
			final PortaDelegataAzione pdAzione =  new PortaDelegataAzione(); //pd.getAzione() == null ? new PortaDelegataAzione() : pd.getAzione();
			
			final AccordoServizioParteComune apc = env.apcCore.getAccordoServizio(asps.getIdAccordo());
			List<PortaApplicativaAzioneIdentificazione> identModes = env.paHelper.getModalitaIdentificazionePorta(env.tipo_protocollo, env.apcCore.toMessageServiceBinding(apc.getServiceBinding()));
			if ( identModes.contains(PortaApplicativaAzioneIdentificazione.PROTOCOL_BASED) && identModes.size() == 1 ) {
				throw FaultCode.RICHIESTA_NON_VALIDA.toException("Non puoi modificare la url-invocazione il cui metodo di identificazione azioni può essere solo " + PortaApplicativaAzioneIdentificazione.PROTOCOL_BASED.toString() );
			}
			
			
			if ( !identModes.contains( PortaApplicativaAzioneIdentificazione.valueOf(urlInvocazione.getModalita().name()) )) {
				throw FaultCode.RICHIESTA_NON_VALIDA.toException("La modalità di identificazione azione deve essere una fra: " + identModes.toString() );
			}
				
			switch (urlInvocazione.getModalita()) {
			case CONTENT_BASED:
				pdAzione.setPattern(urlInvocazione.getPattern());
				pdAzione.setForceInterfaceBased(Helper.boolToStatoFunzionalitaConf(urlInvocazione.isForceInterface()));
				break;
			case HEADER_BASED:
				pdAzione.setPattern(urlInvocazione.getNome());
				pdAzione.setForceInterfaceBased(Helper.boolToStatoFunzionalitaConf(urlInvocazione.isForceInterface()));
				break;
			case INPUT_BASED:
				pdAzione.setForceInterfaceBased(Helper.boolToStatoFunzionalitaConf(urlInvocazione.isForceInterface()));
				break;
			case INTERFACE_BASED:
				break;
			case SOAP_ACTION_BASED:
				pdAzione.setForceInterfaceBased(Helper.boolToStatoFunzionalitaConf(urlInvocazione.isForceInterface()));
				break;
			case URL_BASED:
				pdAzione.setPattern(urlInvocazione.getPattern());
				pdAzione.setForceInterfaceBased(Helper.boolToStatoFunzionalitaConf(urlInvocazione.isForceInterface()));
				break;
			case PROTOCOL_BASED:
				break;
			}
			
			pdAzione.setNome(pdAzione.getPattern());
			pdAzione.setIdentificazione( Enum.valueOf(PortaDelegataAzioneIdentificazione.class, urlInvocazione.getModalita().name()));
			
			ErogazioniApiHelper.overrideFruizioneUrlInvocazione(env.requestWrapper, idErogatore, idServizio, pd, pdAzione);
			
			if ( !env.pdHelper.porteDelegateCheckData(TipoOperazione.CHANGE, pd.getNome()) ) {
				throw FaultCode.RICHIESTA_NON_VALIDA.toException(env.pd.getMessage());
			}
						
			pd.setAzione(pdAzione);
			pd.setOldIDPortaDelegataForUpdate(idPorta);
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

	
    
}

