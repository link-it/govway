/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2021 Link.it srl (https://link.it).
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
package org.openspcoop2.core.config.rs.server.api.impl.applicativi;

import java.util.List;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.openspcoop2.core.commons.Filtri;
import org.openspcoop2.core.commons.Liste;
import org.openspcoop2.core.config.Credenziali;
import org.openspcoop2.core.config.InvocazionePorta;
import org.openspcoop2.core.config.ServizioApplicativo;
import org.openspcoop2.core.config.constants.CostantiConfigurazione;
import org.openspcoop2.core.config.rs.server.api.ApplicativiApi;
import org.openspcoop2.core.config.rs.server.api.impl.ApiKeyInfo;
import org.openspcoop2.core.config.rs.server.api.impl.Helper;
import org.openspcoop2.core.config.rs.server.api.impl.HttpRequestWrapper;
import org.openspcoop2.core.config.rs.server.config.ServerProperties;
import org.openspcoop2.core.config.rs.server.model.Applicativo;
import org.openspcoop2.core.config.rs.server.model.AuthenticationApiKey;
import org.openspcoop2.core.config.rs.server.model.AuthenticationHttpBasic;
import org.openspcoop2.core.config.rs.server.model.BaseCredenziali;
import org.openspcoop2.core.config.rs.server.model.ListaApplicativi;
import org.openspcoop2.core.config.rs.server.model.ModalitaAccessoEnum;
import org.openspcoop2.core.config.rs.server.model.OneOfBaseCredenzialiCredenziali;
import org.openspcoop2.core.config.rs.server.model.Proprieta4000;
import org.openspcoop2.core.id.IDServizioApplicativo;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.protocol.engine.ProtocolFactoryManager;
import org.openspcoop2.protocol.sdk.constants.ConsoleOperationType;
import org.openspcoop2.protocol.sdk.properties.ProtocolProperties;
import org.openspcoop2.protocol.sdk.properties.ProtocolPropertiesUtils;
import org.openspcoop2.utils.service.BaseImpl;
import org.openspcoop2.utils.service.authorization.AuthorizationConfig;
import org.openspcoop2.utils.service.authorization.AuthorizationManager;
import org.openspcoop2.utils.service.beans.ProfiloEnum;
import org.openspcoop2.utils.service.beans.utils.BaseHelper;
import org.openspcoop2.utils.service.beans.utils.ListaUtils;
import org.openspcoop2.utils.service.context.IContext;
import org.openspcoop2.utils.service.fault.jaxrs.FaultCode;
import org.openspcoop2.web.ctrlstat.core.Search;
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
public class ApplicativiApiServiceImpl extends BaseImpl implements ApplicativiApi {

	public ApplicativiApiServiceImpl(){
		super(org.slf4j.LoggerFactory.getLogger(ApplicativiApiServiceImpl.class));
	}
	
	private AuthorizationConfig getAuthorizationConfig() throws Exception{
		return new AuthorizationConfig(ServerProperties.getInstance().getProperties());
	}

    /**
     * Creazione di un oapplicativo
     *
     * Questa operazione consente di creare un applicativo associato ad un soggetto interno
     *
     */
	@Override
    public void createApplicativo(Applicativo body, ProfiloEnum profilo, String soggetto) {
		IContext context = this.getContext();
		try {
			context.getLogger().info("Invocazione in corso ...");     

			AuthorizationManager.authorize(context, getAuthorizationConfig());
			context.getLogger().debug("Autorizzazione completata con successo");
			
			BaseHelper.throwIfNull(body);
                
			Applicativo applicativo = body;
			try{
				applicativo.setCredenziali(ApplicativiApiHelper.translateCredenzialiApplicativo(applicativo, true));
			}
			catch(javax.ws.rs.WebApplicationException e) {
				throw e;
			}
			catch(Throwable e) {
				throw FaultCode.RICHIESTA_NON_VALIDA.toException(e);
			}

			HttpRequestWrapper wrap = new HttpRequestWrapper(context.getServletRequest());
			ApplicativiEnv env = new ApplicativiEnv(wrap, profilo, soggetto, context); 					
	
			String protocollo = env.protocolFactory.getProtocol();
			String tipo_soggetto = ProtocolFactoryManager.getInstance().getDefaultOrganizationTypes().get(protocollo);
			IDSoggetto idSoggetto = new IDSoggetto(tipo_soggetto,env.idSoggetto.getNome());
			IDServizioApplicativo idSA = new IDServizioApplicativo();
			idSA.setIdSoggettoProprietario(idSoggetto);
			idSA.setNome(applicativo.getNome());
			
			ApiKeyInfo keyInfo = ApplicativiApiHelper.createApiKey(applicativo.getCredenziali(), idSA, env.saCore, protocollo);
			boolean updateKey = false;
			
			ServizioApplicativo sa = ApplicativiApiHelper.applicativoToServizioApplicativo(applicativo, env.tipo_protocollo, env.idSoggetto.getNome(), env.stationCore, keyInfo);
						
			if ( ApplicativiApiHelper.isApplicativoDuplicato(sa, env.saCore) ) {
				throw FaultCode.CONFLITTO.toException(
						"Il Servizio Applicativo " + sa.getNome() + " è già stato registrato per il soggetto scelto."
				);
			}
			
			ProtocolProperties protocolProperties = null;
			if(profilo != null) {
				protocolProperties = ApplicativiApiHelper.getProtocolProperties(body, profilo, sa, env);
	
				if(protocolProperties != null) {
					sa.setProtocolPropertyList(ProtocolPropertiesUtils.toProtocolPropertiesConfig(protocolProperties, ConsoleOperationType.ADD, null));
				}
			}

			ApplicativiApiHelper.overrideSAParameters(wrap, env.saHelper, sa, applicativo, keyInfo, updateKey);
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
						

			List<ExtendedConnettore> listExtendedConnettore = null;	// Non serve alla checkData perchè gli applicativi sono sempre fruitori

			if (! env.saHelper.servizioApplicativoCheckData(
					TipoOperazione.ADD,
					generalInfo.getSoggettiList(),
					-1,
					sa.getTipologiaFruizione(),
					sa.getTipologiaErogazione(),
					listExtendedConnettore, null
				)) {
				throw FaultCode.RICHIESTA_NON_VALIDA.toException(StringEscapeUtils.unescapeHtml(env.pd.getMessage()));
			}
				
			ApplicativiApiHelper.validateProperties(env, protocolProperties, sa);

			env.saCore.performCreateOperation(env.userLogin, false, sa);
			
			if(keyInfo!=null) {
				context.getServletResponse().setHeader(ApiKeyInfo.API_KEY, keyInfo.getApiKey());
				if(keyInfo.isMultipleApiKeys()) {
					context.getServletResponse().setHeader(ApiKeyInfo.APP_ID, keyInfo.getAppId());
				}
			}
			
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
     * Elimina un applicativo
     *
     * Questa operazione consente di eliminare un applicativo identificato dal nome e dal soggetto di riferimento
     *
     */
	@Override
    public void deleteApplicativo(String nome, ProfiloEnum profilo, String soggetto) {
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
			} catch (Exception e) {
			}
			
			if (sa != null) { 
				StringBuilder inUsoMessage = new StringBuilder();
				ServiziApplicativiUtilities.deleteServizioApplicativo(sa, context.getAuthentication().getName(), env.saCore, env.saHelper, inUsoMessage, "\n");
				
				if (inUsoMessage.length() > 0) {
					throw FaultCode.RICHIESTA_NON_VALIDA.toException(StringEscapeUtils.unescapeHtml(inUsoMessage.toString()));
				}
				
			} else if (env.delete_404) {
				throw FaultCode.NOT_FOUND.toException("Servizio applicativo con nome: " + nome + " non trovato.");
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
     * Ricerca applicativi
     *
     * Elenca gli applicativi registrati
     *
     */
	@Override
    public ListaApplicativi findAllApplicativi(ProfiloEnum profilo, String soggetto, String q, Integer limit, Integer offset, String ruolo, ModalitaAccessoEnum tipoCredenziali, Boolean profiloQualsiasi, Boolean soggettoQualsiasi) {
		IContext context = this.getContext();
		try {
			context.getLogger().info("Invocazione in corso ...");     

			AuthorizationManager.authorize(context, getAuthorizationConfig());
			context.getLogger().debug("Autorizzazione completata con successo");     
                        
			ApplicativiEnv env = new ApplicativiEnv(context.getServletRequest(), profilo, soggetto, context);
			
			int idLista = Liste.SERVIZIO_APPLICATIVO;

			Search ricerca = Helper.setupRicercaPaginata(q, limit, offset, idLista, env.idSoggetto.toIDSoggetto(), env.tipo_protocollo);
			
			if(profiloQualsiasi!=null && profiloQualsiasi) {
				ricerca.clearFilter(idLista, Filtri.FILTRO_PROTOCOLLO);
			}
			if(soggettoQualsiasi!=null && soggettoQualsiasi) {
				ricerca.clearFilter(idLista, Filtri.FILTRO_SOGGETTO);
			}
			
			//ricerca.addFilter(idLista, Filtri.FILTRO_RUOLO_SERVIZIO_APPLICATIVO, Filtri.VALUE_FILTRO_RUOLO_SERVIZIO_APPLICATIVO_FRUITORE);
			//ricerca.addFilter(idLista, Filtri.FILTRO_TIPO_SERVIZIO_APPLICATIVO, CostantiConfigurazione.CLIENT_OR_SERVER);
			ricerca.addFilter(idLista, Filtri.FILTRO_TIPO_SERVIZIO_APPLICATIVO, CostantiConfigurazione.CLIENT); // Nelle API per adesso sono gestiti solo gli applicativi SERVER
			
			if (ruolo != null && ruolo.trim().length() > 0)
				ricerca.addFilter(idLista, Filtri.FILTRO_RUOLO, ruolo.trim());
			
			if(tipoCredenziali!=null) {
				String filtro = Helper.tipoAuthFromModalitaAccesso.get(tipoCredenziali);
				if(filtro!=null && !"".equals(filtro)) {
					ricerca.addFilter(idLista, Filtri.FILTRO_TIPO_CREDENZIALI, filtro);
				}
			}
			
			List<ServizioApplicativo> saLista = env.saCore.soggettiServizioApplicativoList(null, ricerca);
			
			final ListaApplicativi ret = ListaUtils.costruisciListaPaginata(
					context.getUriInfo(),
					ricerca.getIndexIniziale(idLista),
					ricerca.getPageSize(idLista), 
					ricerca.getNumEntries(idLista), 
					ListaApplicativi.class
				); 
				
			saLista.forEach( sa -> ret.addItemsItem(ApplicativiApiHelper.servizioApplicativoToApplicativoItem(sa)));	
		
			context.getLogger().info("Invocazione completata con successo");
			
			return Helper.returnOrNotFound(ret);
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
     * Restituisce il dettaglio di un applicativo
     *
     * Questa operazione consente di ottenere il dettaglio di un applicativo identificato dal nome e dal soggetto di riferimento
     *
     */
	@Override
    public Applicativo getApplicativo(String nome, ProfiloEnum profilo, String soggetto) {
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
			} catch ( Exception e) {
				throw FaultCode.NOT_FOUND.toException("Servizio applicativo con nome: " + nome + " non trovato.");
			}
			
			context.getLogger().info("Invocazione completata con successo");
			
			Applicativo applicativo = ApplicativiApiHelper.servizioApplicativoToApplicativo(sa);
			ApplicativiApiHelper.populateProtocolInfo(sa, applicativo, env, profilo);
			return applicativo;

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
     * Modifica i dati di un applicativo
     *
     * Questa operazione consente di aggiornare i dati di un applicativo identificato dal nome e dal soggetto di riferimento
     */
	@Override
    public void updateApplicativo(Applicativo body, String nome, ProfiloEnum profilo, String soggetto) {
		IContext context = this.getContext();
		try {
			context.getLogger().info("Invocazione in corso ...");     

			AuthorizationManager.authorize(context, getAuthorizationConfig());
			context.getLogger().debug("Autorizzazione completata con successo");     
			
			BaseHelper.throwIfNull(body);
			
			Applicativo applicativo = body;
			try{
				applicativo.setCredenziali(ApplicativiApiHelper.translateCredenzialiApplicativo(applicativo, false));
			}
			catch(javax.ws.rs.WebApplicationException e) {
				throw e;
			}
			catch(Throwable e) {
				throw FaultCode.RICHIESTA_NON_VALIDA.toException(e);
			}
			
			final ApplicativiEnv env = new ApplicativiEnv(context.getServletRequest(), profilo, soggetto, context);
			soggetto = env.idSoggetto.getNome();
			
			final ServizioApplicativo oldSa = BaseHelper.supplyOrNotFound( () -> ApplicativiApiHelper.getServizioApplicativo(nome, env.idSoggetto.getNome(), env.tipo_protocollo, env.saCore), "Servizio Applicativo");
			ApiKeyInfo keyInfo = ApplicativiApiHelper.getApiKey(oldSa, false);
			boolean updateKey = false;
			
			final ServizioApplicativo tmpSa = ApplicativiApiHelper.applicativoToServizioApplicativo(applicativo, env.tipo_protocollo, soggetto, env.stationCore, keyInfo);
			final ServizioApplicativo newSa = ApplicativiApiHelper.getServizioApplicativo(nome, env.idSoggetto.getNome(), env.tipo_protocollo, env.saCore);
			
			if(ModalitaAccessoEnum.HTTP_BASIC.equals(body.getCredenziali().getModalitaAccesso())) {
				AuthenticationHttpBasic httpBasic = (AuthenticationHttpBasic) body.getCredenziali();
				if(httpBasic.getPassword()==null || StringUtils.isEmpty(httpBasic.getPassword())) {
					// password in update non è obbligatoria
					boolean set = false;
					if(newSa.getInvocazionePorta()!=null && newSa.getInvocazionePorta().sizeCredenzialiList()>0) {
						Credenziali cNewSaImageDB = newSa.getInvocazionePorta().getCredenziali(0);
						Credenziali cTmp = tmpSa.getInvocazionePorta().sizeCredenzialiList()>0 ? tmpSa.getInvocazionePorta().getCredenziali(0) : null;
						if(cNewSaImageDB!=null && cTmp!=null) {
							cTmp.setPassword(cNewSaImageDB.getPassword());
							cTmp.setCertificateStrictVerification(cNewSaImageDB.isCertificateStrictVerification());
							set = true;
						}
					}
					if(!set) {
						throw FaultCode.RICHIESTA_NON_VALIDA.toException("Tipo di autenticazione '"+body.getCredenziali().getModalitaAccesso()+"'; indicare la password");
					}
				}
			}
			else if(ModalitaAccessoEnum.API_KEY.equals(body.getCredenziali().getModalitaAccesso())) {
				AuthenticationApiKey apiKeyCred = (AuthenticationApiKey) body.getCredenziali();
				boolean appId = Helper.isAppId(apiKeyCred.isAppId());
				if(appId != keyInfo.isMultipleApiKeys()) {
					throw FaultCode.RICHIESTA_NON_VALIDA.toException("Tipo di autenticazione '"+body.getCredenziali().getModalitaAccesso()+"'; modifica del tipo (appId) non consentita");
				}
			}
			
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
			
			// Vincolo rilasciato in 3.3.1
//			if (!oldSa.getNome().equals(newSa.getNome())) {
//				throw FaultCode.RICHIESTA_NON_VALIDA.toException("Non è possibile modificare il nome del servizio applicativo");
//			}
			
			ProtocolProperties protocolProperties = null;
			if(profilo != null) {
				protocolProperties = ApplicativiApiHelper.getProtocolProperties(body, profilo, newSa, env);
	
				if(protocolProperties != null) {
					newSa.setProtocolPropertyList(ProtocolPropertiesUtils.toProtocolPropertiesConfig(protocolProperties, ConsoleOperationType.ADD, null));
				}
			}

					
			IDServizioApplicativo oldID = new IDServizioApplicativo();
			oldID.setIdSoggettoProprietario(ApplicativiApiHelper.getIDSoggetto(oldSa.getNomeSoggettoProprietario(), env.tipo_protocollo));
			oldID.setNome(oldSa.getNome());
			
			newSa.setOldIDServizioApplicativoForUpdate(oldID);
			
			List<ExtendedConnettore> listExtendedConnettore = null;	// connettori extended non supportati via API
			
			ApplicativiApiHelper.overrideSAParameters(env.requestWrapper, env.saHelper, newSa, applicativo, keyInfo, updateKey);
			env.requestWrapper.overrideParameter(ServiziApplicativiCostanti.PARAMETRO_SERVIZI_APPLICATIVI_PROTOCOLLO, env.tipo_protocollo);
			
			if (! env.saHelper.servizioApplicativoCheckData(
					TipoOperazione.CHANGE,
					null,
					oldSa.getIdSoggetto(),
					newSa.getTipologiaFruizione(),
					newSa.getTipologiaErogazione(),
					listExtendedConnettore, newSa
				)) {
				throw FaultCode.RICHIESTA_NON_VALIDA.toException(StringEscapeUtils.unescapeHtml(env.pd.getMessage()));
			}
			
			ApplicativiApiHelper.validateProperties(env, protocolProperties, newSa);

			// eseguo l'aggiornamento
			List<Object> listOggettiDaAggiornare = ServiziApplicativiUtilities.getOggettiDaAggiornare(env.saCore, oldID, newSa);
			env.saCore.performUpdateOperation(env.userLogin, false, listOggettiDaAggiornare.toArray());
					
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
     * Modifica i dati di un applicativo
     *
     * Questa operazione consente di aggiornare le credenziali associate ad un applicativo identificato dal nome e dal soggetto di riferimento
     */
    @Override
	public void updateCredenzialiApplicativo(BaseCredenziali body, String nome, ProfiloEnum profilo, String soggetto) {
    	IContext context = this.getContext();
		try {
			context.getLogger().info("Invocazione in corso ...");     

			AuthorizationManager.authorize(context, getAuthorizationConfig());
			context.getLogger().debug("Autorizzazione completata con successo");     
			
			BaseHelper.throwIfNull(body);
			
			OneOfBaseCredenzialiCredenziali credenziali = null;
			try{
				credenziali = ApplicativiApiHelper.translateCredenzialiApplicativo(body, true); // metto true, come se fosse create per obbligare la password basic
			}
			catch(javax.ws.rs.WebApplicationException e) {
				throw e;
			}
			catch(Throwable e) {
				throw FaultCode.RICHIESTA_NON_VALIDA.toException(e);
			}
			
			final ApplicativiEnv env = new ApplicativiEnv(context.getServletRequest(), profilo, soggetto, context);
			soggetto = env.idSoggetto.getNome();
			
			final ServizioApplicativo oldSa = BaseHelper.supplyOrNotFound( () -> ApplicativiApiHelper.getServizioApplicativo(nome, env.idSoggetto.getNome(), env.tipo_protocollo, env.saCore), "Servizio Applicativo");
			
			IDServizioApplicativo oldID = new IDServizioApplicativo();
			oldID.setIdSoggettoProprietario(ApplicativiApiHelper.getIDSoggetto(oldSa.getNomeSoggettoProprietario(), env.tipo_protocollo));
			oldID.setNome(oldSa.getNome());
						
			ApiKeyInfo keyInfo = ApplicativiApiHelper.createApiKey(credenziali, oldID, env.saCore, env.protocolFactory.getProtocol());
			boolean updateKey = true;
			
			List<Credenziali> newCredenziali = ApplicativiApiHelper.credenzialiFromAuth(credenziali, keyInfo);
			if(oldSa.getInvocazionePorta()==null) {
				oldSa.setInvocazionePorta(new InvocazionePorta());
			}
			oldSa.getInvocazionePorta().getCredenzialiList().clear();
			oldSa.getInvocazionePorta().getCredenzialiList().addAll(newCredenziali);
			
			oldSa.setOldIDServizioApplicativoForUpdate(oldID);
				
			List<ExtendedConnettore> listExtendedConnettore = null;	// Non serve alla checkData perchè da Api, gli applicativi sono sempre fruitori
			
			ApplicativiApiHelper.overrideSAParameters(env.requestWrapper, env.saHelper, oldSa, credenziali, keyInfo, updateKey);
			env.requestWrapper.overrideParameter(ServiziApplicativiCostanti.PARAMETRO_SERVIZI_APPLICATIVI_PROTOCOLLO, env.tipo_protocollo);
			
			if (! env.saHelper.servizioApplicativoCheckData(
					TipoOperazione.CHANGE,
					null,
					oldSa.getIdSoggetto(),
					oldSa.getTipologiaFruizione(),
					oldSa.getTipologiaErogazione(),
					listExtendedConnettore, oldSa
				)) {
				throw FaultCode.RICHIESTA_NON_VALIDA.toException(StringEscapeUtils.unescapeHtml(env.pd.getMessage()));
			}
			
			// eseguo l'aggiornamento
			List<Object> listOggettiDaAggiornare = ServiziApplicativiUtilities.getOggettiDaAggiornare(env.saCore, oldID, oldSa);
			env.saCore.performUpdateOperation(env.userLogin, false, listOggettiDaAggiornare.toArray());
				
			if(keyInfo!=null) {
				context.getServletResponse().setHeader(ApiKeyInfo.API_KEY, keyInfo.getApiKey());
				if(keyInfo.isMultipleApiKeys()) {
					context.getServletResponse().setHeader(ApiKeyInfo.APP_ID, keyInfo.getAppId());
				}
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
}

