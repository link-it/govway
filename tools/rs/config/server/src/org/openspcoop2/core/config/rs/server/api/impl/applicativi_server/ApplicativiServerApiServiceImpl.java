/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2022 Link.it srl (https://link.it).
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
package org.openspcoop2.core.config.rs.server.api.impl.applicativi_server;

import java.util.List;

import org.apache.commons.lang.StringEscapeUtils;
import org.openspcoop2.core.commons.Filtri;
import org.openspcoop2.core.commons.Liste;
import org.openspcoop2.core.config.InvocazioneCredenziali;
import org.openspcoop2.core.config.InvocazioneServizio;
import org.openspcoop2.core.config.ServizioApplicativo;
import org.openspcoop2.core.config.constants.CostantiConfigurazione;
import org.openspcoop2.core.config.constants.InvocazioneServizioTipoAutenticazione;
import org.openspcoop2.core.config.rs.server.api.ApplicativiServerApi;
import org.openspcoop2.core.config.rs.server.api.impl.Helper;
import org.openspcoop2.core.config.rs.server.api.impl.HttpRequestWrapper;
import org.openspcoop2.core.config.rs.server.api.impl.applicativi.ApplicativiApiHelper;
import org.openspcoop2.core.config.rs.server.api.impl.applicativi.ApplicativiEnv;
import org.openspcoop2.core.config.rs.server.api.impl.erogazioni.ConnettoreAPIHelper;
import org.openspcoop2.core.config.rs.server.api.impl.erogazioni.ErogazioniEnv;
import org.openspcoop2.core.config.rs.server.config.ServerProperties;
import org.openspcoop2.core.config.rs.server.model.ApplicativoServer;
import org.openspcoop2.core.config.rs.server.model.ConnettoreConfigurazioneHttpBasic;
import org.openspcoop2.core.config.rs.server.model.ConnettoreEnum;
import org.openspcoop2.core.config.rs.server.model.ConnettoreHttp;
import org.openspcoop2.core.config.rs.server.model.ListaApplicativiServer;
import org.openspcoop2.core.config.rs.server.model.Proprieta4000;
import org.openspcoop2.core.constants.TipiConnettore;
import org.openspcoop2.core.id.IDServizioApplicativo;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.protocol.engine.ProtocolFactoryManager;
import org.openspcoop2.utils.service.BaseImpl;
import org.openspcoop2.utils.service.authorization.AuthorizationConfig;
import org.openspcoop2.utils.service.authorization.AuthorizationManager;
import org.openspcoop2.utils.service.beans.ProfiloEnum;
import org.openspcoop2.utils.service.beans.utils.BaseHelper;
import org.openspcoop2.utils.service.beans.utils.ListaUtils;
import org.openspcoop2.utils.service.context.IContext;
import org.openspcoop2.utils.service.fault.jaxrs.FaultCode;
import org.openspcoop2.web.ctrlstat.core.ConsoleSearch;
import org.openspcoop2.web.ctrlstat.plugins.ExtendedConnettore;
import org.openspcoop2.web.ctrlstat.servlet.sa.ServiziApplicativiCostanti;
import org.openspcoop2.web.ctrlstat.servlet.sa.ServiziApplicativiGeneralInfo;
import org.openspcoop2.web.ctrlstat.servlet.sa.ServiziApplicativiUtilities;
import org.openspcoop2.web.lib.mvc.TipoOperazione;
/**
 * ApplicativiApiServiceImpl
 * 
 * @author $Author$
 * @version $Rev$, $Date$
 * 
 */
public class ApplicativiServerApiServiceImpl extends BaseImpl implements ApplicativiServerApi {

	public ApplicativiServerApiServiceImpl(){
		super(org.slf4j.LoggerFactory.getLogger(ApplicativiServerApiServiceImpl.class));
	}

	private AuthorizationConfig getAuthorizationConfig() throws Exception{
		return new AuthorizationConfig(ServerProperties.getInstance().getProperties());
	}

    /**
     * Creazione di un applicativo server
     *
     * Questa operazione consente di creare un applicativo server associato ad un soggetto interno
     *
     */
	@Override
    public void createApplicativoServer(ApplicativoServer body, ProfiloEnum profilo, String soggetto) {
		IContext context = this.getContext();
		try {
			context.getLogger().info("Invocazione in corso ...");     

			AuthorizationManager.authorize(context, getAuthorizationConfig());
			context.getLogger().debug("Autorizzazione completata con successo");     
                        
			
			
			// TODO SOGGETTO TIPO OPERATIVO I servizi applicativi di tipo server sono registrabili solamente su soggetti di tipo ‘operativo’.
			BaseHelper.throwIfNull(body);
            
			ApplicativoServer applicativo = body;

			HttpRequestWrapper wrap = new HttpRequestWrapper(context.getServletRequest());
			ApplicativiEnv env = new ApplicativiEnv(wrap, profilo, soggetto, context); 					
			ErogazioniEnv erogEnv = new ErogazioniEnv(wrap, profilo, soggetto, context); 					
	
			String protocollo = env.protocolFactory.getProtocol();
			String tipo_soggetto = ProtocolFactoryManager.getInstance().getDefaultOrganizationTypes().get(protocollo);
			IDSoggetto idSoggetto = new IDSoggetto(tipo_soggetto,env.idSoggetto.getNome());
			IDServizioApplicativo idSA = new IDServizioApplicativo();
			idSA.setIdSoggettoProprietario(idSoggetto);
			idSA.setNome(applicativo.getNome());
			
			ServizioApplicativo sa = ApplicativiApiHelper.applicativoToServizioApplicativo(applicativo, env.tipo_protocollo, env.idSoggetto.getNome(), env.stationCore);
						
			if ( ApplicativiApiHelper.isApplicativoDuplicato(sa, env.saCore) ) {
				throw FaultCode.CONFLITTO.toException(
						"Applicativo server " + sa.getNome() + " già registrato per il soggetto scelto."
				);
			}
			
			ApplicativiApiHelper.overrideSAParametersApplicativoServer(env.requestWrapper, env.saHelper, sa, false);

			wrap.overrideParameter(ServiziApplicativiCostanti.PARAMETRO_SERVIZI_APPLICATIVI_PROTOCOLLO, env.tipo_protocollo);
			
			List<String> listaTipiProtocollo = ProtocolFactoryManager.getInstance().getProtocolNamesAsList();
			IDSoggetto soggettoMultitenantSelezionato = new IDSoggetto(env.idSoggetto.getTipo(), env.idSoggetto.getNome());
			
			
			String dominio = null;

			if(!env.isDominioInterno(idSoggetto)) {
				if(profilo != null && !(profilo.equals(ProfiloEnum.MODI) || profilo.equals(ProfiloEnum.MODIPA))) {
					throw FaultCode.RICHIESTA_NON_VALIDA.toException("Impossibile creare un applicativo per un soggetto esterno col profilo ["+profilo+"]");
				}
				dominio = "esterno";
			}

			ServiziApplicativiGeneralInfo generalInfo = ServiziApplicativiUtilities.getGeneralInfo(false, env.idSoggetto.getId().toString(), listaTipiProtocollo, 
					env.saCore, env.saHelper, env.userLogin, true, 
					soggettoMultitenantSelezionato.toString(), dominio);
						

			List<ExtendedConnettore> listExtendedConnettore = null;	// connettori extended non supportati via API

			InvocazioneServizio is = sa.getInvocazioneServizio();
			InvocazioneCredenziali credenziali_is = is.getCredenziali();
			org.openspcoop2.core.config.Connettore connis = is.getConnettore();

			String oldConnT = connis.getTipo();
			if ((connis.getCustom() != null && connis.getCustom())
					&& !connis.getTipo().equals(TipiConnettore.HTTPS.toString())
					&& !connis.getTipo().equals(TipiConnettore.FILE.toString())) {
				oldConnT = TipiConnettore.CUSTOM.toString();
			}

			if (!ConnettoreAPIHelper.connettoreCheckData(body.getConnettore(), erogEnv, true)) {
				throw FaultCode.RICHIESTA_NON_VALIDA.toException(StringEscapeUtils.unescapeHtml(env.pd.getMessage()));
			}

			ConnettoreAPIHelper.fillConnettoreConfigurazione(sa, erogEnv, body.getConnettore(), oldConnT);

			if(body.getConnettore().getTipo().equals(ConnettoreEnum.HTTP) && ((ConnettoreHttp) body.getConnettore()).getAutenticazioneHttp() != null) {
					if (credenziali_is == null) {
						credenziali_is = new InvocazioneCredenziali();
					}
					
					ConnettoreConfigurazioneHttpBasic authHttp = ((ConnettoreHttp) body.getConnettore()).getAutenticazioneHttp();
					credenziali_is.setUser(authHttp.getUsername());
					credenziali_is.setPassword(authHttp.getPassword());
					is.setCredenziali(credenziali_is);
					is.setAutenticazione(InvocazioneServizioTipoAutenticazione.BASIC);
			} else {
				is.setCredenziali(null);
				is.setAutenticazione(InvocazioneServizioTipoAutenticazione.NONE);
			}

			is.setConnettore(connis);
			sa.setInvocazioneServizio(is);

			if (! env.saHelper.servizioApplicativoCheckData(
					TipoOperazione.ADD,
					generalInfo.getSoggettiList(),
					-1,
					sa.getTipologiaFruizione(),
					sa.getTipologiaErogazione(),
					listExtendedConnettore, sa,
					new StringBuilder()
				)) {
				throw FaultCode.RICHIESTA_NON_VALIDA.toException(StringEscapeUtils.unescapeHtml(env.pd.getMessage()));
			}
				
			env.saCore.performCreateOperation(env.userLogin, false, sa);
			
			context.getLogger().info("Invocazione completata con successo");
        
			// Bug Fix: altrimenti viene generato 204
			context.getServletResponse().setStatus(201);
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
     * Elimina un applicativo server
     *
     * Questa operazione consente di eliminare un applicativo server identificato dal nome e dal soggetto di riferimento
     *
     */
	@Override
    public void deleteApplicativoServer(String nome, ProfiloEnum profilo, String soggetto) {
		IContext context = this.getContext();
		try {
			context.getLogger().info("Invocazione in corso ...");     

			AuthorizationManager.authorize(context, getAuthorizationConfig());
			context.getLogger().debug("Autorizzazione completata con successo");     
                        
			ApplicativiEnv env = new ApplicativiEnv(context.getServletRequest(), profilo, soggetto, context);
			IDServizioApplicativo idServizioApplicativo = null;
			ServizioApplicativo sa = null;
			try {
				idServizioApplicativo = new IDServizioApplicativo();
				idServizioApplicativo.setIdSoggettoProprietario(env.idSoggetto.toIDSoggetto());
				
			} catch (Exception e) {
				throw FaultCode.NOT_FOUND.toException("Soggetto non trovato.");
			}
			
			idServizioApplicativo.setNome(nome);
			try {
				sa = env.saCore.getServizioApplicativo(idServizioApplicativo);
				if(!sa.getTipo().equals(CostantiConfigurazione.SERVER)) {
					sa = null;
				}
			} catch (Exception e) {
			}
			
			if (sa != null) { 
				StringBuilder inUsoMessage = new StringBuilder();
				ServiziApplicativiUtilities.deleteServizioApplicativo(sa, env.userLogin, env.saCore, env.saHelper, inUsoMessage, "\n");
				
				if (inUsoMessage.length() > 0) {
					throw FaultCode.RICHIESTA_NON_VALIDA.toException(StringEscapeUtils.unescapeHtml(inUsoMessage.toString()));
				}
				
			} else if (env.delete_404) {
				throw FaultCode.NOT_FOUND.toException("Applicativo server con nome: " + nome + " non trovato.");
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
     * Ricerca applicativi server
     *
     * Elenca gli applicativi server registrati
     *
     */
	@Override
    public ListaApplicativiServer findAllApplicativiServer(ProfiloEnum profilo, String soggetto, String q, Integer limit, Integer offset, Boolean profiloQualsiasi, Boolean soggettoQualsiasi) {
		IContext context = this.getContext();
		try {
			context.getLogger().info("Invocazione in corso ...");     

			AuthorizationManager.authorize(context, getAuthorizationConfig());
			context.getLogger().debug("Autorizzazione completata con successo");     
                        
			ApplicativiEnv env = new ApplicativiEnv(context.getServletRequest(), profilo, soggetto, context);
			
			int idLista = Liste.SERVIZIO_APPLICATIVO;

			ConsoleSearch ricerca = Helper.setupRicercaPaginata(q, limit, offset, idLista, env.idSoggetto.toIDSoggetto(), env.tipo_protocollo);
			
			if(profiloQualsiasi!=null && profiloQualsiasi) {
				ricerca.clearFilter(idLista, Filtri.FILTRO_PROTOCOLLO);
			}
			if(soggettoQualsiasi!=null && soggettoQualsiasi) {
				ricerca.clearFilter(idLista, Filtri.FILTRO_SOGGETTO);
			}
			
			//ricerca.addFilter(idLista, Filtri.FILTRO_RUOLO_SERVIZIO_APPLICATIVO, Filtri.VALUE_FILTRO_RUOLO_SERVIZIO_APPLICATIVO_FRUITORE);
			//ricerca.addFilter(idLista, Filtri.FILTRO_TIPO_SERVIZIO_APPLICATIVO, CostantiConfigurazione.CLIENT_OR_SERVER);
			ricerca.addFilter(idLista, Filtri.FILTRO_TIPO_SERVIZIO_APPLICATIVO, CostantiConfigurazione.SERVER); // Nelle API per adesso sono gestiti solo gli applicativi SERVER
			
			List<ServizioApplicativo> saLista = env.saCore.soggettiServizioApplicativoList(null, ricerca);
			
			final ListaApplicativiServer ret = ListaUtils.costruisciListaPaginata(
					context.getUriInfo(),
					ricerca.getIndexIniziale(idLista),
					ricerca.getPageSize(idLista), 
					ricerca.getNumEntries(idLista), 
					ListaApplicativiServer.class
				); 
				
			saLista.forEach( sa -> ret.addItemsItem(ApplicativiApiHelper.servizioApplicativoToApplicativoServerItem(sa)));	
		
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
     * Restituisce il dettaglio di un applicativo server
     *
     * Questa operazione consente di ottenere il dettaglio di un applicativo server identificato dal nome e dal soggetto di riferimento
     *
     */
	@Override
    public ApplicativoServer getApplicativoServer(String nome, ProfiloEnum profilo, String soggetto) {
		IContext context = this.getContext();
		try {
			context.getLogger().info("Invocazione in corso ...");     

			AuthorizationManager.authorize(context, getAuthorizationConfig());
			context.getLogger().debug("Autorizzazione completata con successo");     
                        
			ApplicativiEnv env = new ApplicativiEnv(context.getServletRequest(), profilo, soggetto, context);
			
			ServizioApplicativo sa = null;			
			try {
				IDServizioApplicativo idServizioApplicativo = new IDServizioApplicativo();
				idServizioApplicativo.setIdSoggettoProprietario(env.idSoggetto.toIDSoggetto());
				idServizioApplicativo.setNome(nome);
				sa = env.saCore.getServizioApplicativo(idServizioApplicativo);
				
				if(!sa.getTipo().equals(CostantiConfigurazione.SERVER)) {
					throw new Exception("Applicativo non di tipo server: " + sa.getTipo());
				}
			} catch ( Exception e) {
				throw FaultCode.NOT_FOUND.toException("Applicativo server con nome: " + nome + " non trovato.");
			}
			
			context.getLogger().info("Invocazione completata con successo");
			
			ApplicativoServer applicativo = ApplicativiApiHelper.servizioApplicativoToApplicativoServer(sa);
        
			context.getLogger().info("Invocazione completata con successo");
			
			return applicativo;     
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
     * Modifica i dati di un applicativo server
     *
     * Questa operazione consente di aggiornare i dati di un applicativo server identificato dal nome e dal soggetto di riferimento
     *
     */
	@Override
    public void updateApplicativoServer(ApplicativoServer body, String nome, ProfiloEnum profilo, String soggetto) {
		IContext context = this.getContext();
		try {
			context.getLogger().info("Invocazione in corso ...");     

			AuthorizationManager.authorize(context, getAuthorizationConfig());
			context.getLogger().debug("Autorizzazione completata con successo");     
                        
			BaseHelper.throwIfNull(body);
			
			ApplicativoServer applicativo = body;

			final ApplicativiEnv env = new ApplicativiEnv(context.getServletRequest(), profilo, soggetto, context);
			final ErogazioniEnv erogEnv = new ErogazioniEnv(context.getServletRequest(), profilo, soggetto, context);
			soggetto = env.idSoggetto.getNome();
			
			final ServizioApplicativo oldSa = BaseHelper.supplyOrNotFound( () -> ApplicativiApiHelper.getServizioApplicativo(nome, env.idSoggetto.getNome(), env.tipo_protocollo, env.saCore), "Servizio Applicativo");
			
			if(!oldSa.getTipo().equals(CostantiConfigurazione.SERVER)) {
				throw FaultCode.NOT_FOUND.toException("Applicativo server con nome "+oldSa.getNome()+" non trovato");
			}

			final ServizioApplicativo tmpSa = ApplicativiApiHelper.applicativoToServizioApplicativo(applicativo, env.tipo_protocollo, soggetto, env.stationCore);
			final ServizioApplicativo newSa = ApplicativiApiHelper.getServizioApplicativo(nome, env.idSoggetto.getNome(), env.tipo_protocollo, env.saCore);
			
			newSa.setNome(tmpSa.getNome());
			newSa.setIdSoggetto(tmpSa.getIdSoggetto());
			newSa.setNomeSoggettoProprietario(tmpSa.getNomeSoggettoProprietario());
			newSa.setTipoSoggettoProprietario(tmpSa.getTipoSoggettoProprietario());		
			newSa.getInvocazionePorta().setCredenzialiList(tmpSa.getInvocazionePorta().getCredenzialiList());
			newSa.getInvocazionePorta().setRuoli(tmpSa.getInvocazionePorta().getRuoli());

			newSa.getProprietaList().clear();
			if(applicativo.getProprieta()!=null && !applicativo.getProprieta().isEmpty()) {
				for (Proprieta4000 proprieta : applicativo.getProprieta()) {
					org.openspcoop2.core.config.Proprieta pConfig = new org.openspcoop2.core.config.Proprieta();
					pConfig.setNome(proprieta.getNome());
					pConfig.setValore(proprieta.getValore());
					newSa.addProprieta(pConfig);
				}
			}
			
			InvocazioneServizio is = newSa.getInvocazioneServizio();
			InvocazioneCredenziali credenziali_is = is.getCredenziali();
			org.openspcoop2.core.config.Connettore connis = is.getConnettore();

			String oldConnT = connis.getTipo();
			if ((connis.getCustom() != null && connis.getCustom())
					&& !connis.getTipo().equals(TipiConnettore.HTTPS.toString())
					&& !connis.getTipo().equals(TipiConnettore.FILE.toString())) {
				oldConnT = TipiConnettore.CUSTOM.toString();
			}

			if (!ConnettoreAPIHelper.connettoreCheckData(body.getConnettore(), erogEnv, true)) {
				throw FaultCode.RICHIESTA_NON_VALIDA.toException(StringEscapeUtils.unescapeHtml(env.pd.getMessage()));
			}

			ConnettoreAPIHelper.fillConnettoreConfigurazione(newSa, erogEnv, body.getConnettore(), oldConnT);

			if(body.getConnettore().getTipo().equals(ConnettoreEnum.HTTP) && ((ConnettoreHttp) body.getConnettore()).getAutenticazioneHttp() != null) {
				if (credenziali_is == null) {
					credenziali_is = new InvocazioneCredenziali();
				}
				
				ConnettoreConfigurazioneHttpBasic authHttp = ((ConnettoreHttp) body.getConnettore()).getAutenticazioneHttp();
				credenziali_is.setUser(authHttp.getUsername());
				credenziali_is.setPassword(authHttp.getPassword());
				is.setCredenziali(credenziali_is);
				is.setAutenticazione(InvocazioneServizioTipoAutenticazione.BASIC);
			}

			else {
				is.setCredenziali(null);
				is.setAutenticazione(InvocazioneServizioTipoAutenticazione.NONE);
			}

			is.setConnettore(connis);
			newSa.setInvocazioneServizio(is);


			IDServizioApplicativo oldID = new IDServizioApplicativo();
			oldID.setIdSoggettoProprietario(ApplicativiApiHelper.getIDSoggetto(oldSa.getNomeSoggettoProprietario(), env.tipo_protocollo));
			oldID.setNome(oldSa.getNome());
			
			newSa.setOldIDServizioApplicativoForUpdate(oldID);
			
			List<ExtendedConnettore> listExtendedConnettore = null;	// connettori extended non supportati via API
			
			ApplicativiApiHelper.overrideSAParametersApplicativoServer(env.requestWrapper, env.saHelper, newSa, false);
			env.requestWrapper.overrideParameter(ServiziApplicativiCostanti.PARAMETRO_SERVIZI_APPLICATIVI_PROTOCOLLO, env.tipo_protocollo);
			
			if (! env.saHelper.servizioApplicativoCheckData(
					TipoOperazione.CHANGE,
					null,
					oldSa.getIdSoggetto(),
					newSa.getTipologiaFruizione(),
					newSa.getTipologiaErogazione(),
					listExtendedConnettore, newSa,
					new StringBuilder()
				)) {
				throw FaultCode.RICHIESTA_NON_VALIDA.toException(StringEscapeUtils.unescapeHtml(env.pd.getMessage()));
			}
			
			// eseguo l'aggiornamento
			List<Object> listOggettiDaAggiornare = ServiziApplicativiUtilities.getOggettiDaAggiornare(env.saCore, oldID, newSa);
			env.saCore.performUpdateOperation(env.userLogin, false, listOggettiDaAggiornare.toArray());
        
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

