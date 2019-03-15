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
package org.openspcoop2.core.config.rs.server.api.impl.applicativi;

import java.util.List;

import org.apache.commons.lang.StringEscapeUtils;
import org.openspcoop2.core.commons.Liste;
import org.openspcoop2.core.config.ServizioApplicativo;
import org.openspcoop2.core.config.rs.server.api.ApplicativiApi;
import org.openspcoop2.core.config.rs.server.api.impl.Helper;
import org.openspcoop2.core.config.rs.server.api.impl.HttpRequestWrapper;
import org.openspcoop2.core.config.rs.server.config.ServerProperties;
import org.openspcoop2.core.config.rs.server.model.Applicativo;
import org.openspcoop2.core.config.rs.server.model.ListaApplicativi;
import org.openspcoop2.utils.service.beans.ProfiloEnum;
import org.openspcoop2.core.id.IDServizioApplicativo;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.protocol.engine.ProtocolFactoryManager;
import org.openspcoop2.utils.service.BaseImpl;
import org.openspcoop2.utils.service.authorization.AuthorizationConfig;
import org.openspcoop2.utils.service.authorization.AuthorizationManager;
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
			
			Helper.throwIfNull(body);
                
			Applicativo applicativo = body;
			try{
				applicativo.setCredenziali(ApplicativiApiHelper.translateCredenzialiApplicativo(applicativo));
			}catch(Throwable e) {
				throw FaultCode.RICHIESTA_NON_VALIDA.toException(e);
			}

			HttpRequestWrapper wrap = new HttpRequestWrapper(context.getServletRequest());
			ApplicativiEnv env = new ApplicativiEnv(wrap, profilo, soggetto, context); 					
	
			ServizioApplicativo sa = ApplicativiApiHelper.applicativoToServizioApplicativo(applicativo, env.tipo_protocollo, env.idSoggetto.getNome(), env.stationCore);
						
			if ( ApplicativiApiHelper.isApplicativoDuplicato(sa, env.saCore) ) {
				throw FaultCode.CONFLITTO.toException(
						"Il Servizio Applicativo " + sa.getNome() + " è già stato registrato per il soggetto scelto."
				);
			}
					
			ApplicativiApiHelper.overrideSAParameters(wrap, env.saHelper, sa, applicativo);
			wrap.overrideParameter(ServiziApplicativiCostanti.PARAMETRO_SERVIZI_APPLICATIVI_PROTOCOLLO, env.tipo_protocollo);
			
			List<String> listaTipiProtocollo = ProtocolFactoryManager.getInstance().getProtocolNamesAsList();
			IDSoggetto soggettoMultitenantSelezionato = new IDSoggetto(env.idSoggetto.getTipo(), env.idSoggetto.getNome());
			ServiziApplicativiGeneralInfo generalInfo = ServiziApplicativiUtilities.getGeneralInfo(false, env.idSoggetto.getId().toString(), listaTipiProtocollo, 
					env.saCore, env.saHelper, env.userLogin, true, 
					soggettoMultitenantSelezionato.toString());
						

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
				
			env.saCore.performCreateOperation(env.userLogin, false, sa);
			
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
				StringBuffer inUsoMessage = new StringBuffer();
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
			context.getLogger().error("Invocazione terminata con errore '4xx': %s",e, e.getMessage());
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
    public ListaApplicativi findAllApplicativi(ProfiloEnum profilo, String soggetto, String q, Integer limit, Integer offset, String ruolo) {
		IContext context = this.getContext();
		try {
			context.getLogger().info("Invocazione in corso ...");     

			AuthorizationManager.authorize(context, getAuthorizationConfig());
			context.getLogger().debug("Autorizzazione completata con successo");     
                        
			ApplicativiEnv env = new ApplicativiEnv(context.getServletRequest(), profilo, soggetto, context);
			
			int idLista = Liste.SERVIZIO_APPLICATIVO;

			Search ricerca = Helper.setupRicercaPaginata(q, limit, offset, idLista, env.idSoggetto.toIDSoggetto(), env.tipo_protocollo);
							
			List<ServizioApplicativo> saLista = env.saCore.soggettiServizioApplicativoList(null, ricerca);
			
			final ListaApplicativi ret = ListaUtils.costruisciListaPaginata(
					context.getServletRequest().getRequestURI(),
					offset, 
					limit, 
					ricerca.getNumEntries(idLista), 
					ListaApplicativi.class
				); 
				
			saLista.forEach( sa -> ret.addItemsItem(ApplicativiApiHelper.servizioApplicativoToApplicativoItem(sa)));	
		
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
			
			return ApplicativiApiHelper.servizioApplicativoToApplicativo(sa);    
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
			
			Helper.throwIfNull(body);
			
			Applicativo applicativo = body;
			try{
				applicativo.setCredenziali(ApplicativiApiHelper.translateCredenzialiApplicativo(applicativo));
			}catch(Throwable e) {
				throw FaultCode.RICHIESTA_NON_VALIDA.toException(e);
			}
			
			final ApplicativiEnv env = new ApplicativiEnv(context.getServletRequest(), profilo, soggetto, context);
			soggetto = env.idSoggetto.getNome();
			
			final ServizioApplicativo oldSa = Helper.supplyOrNotFound( () -> ApplicativiApiHelper.getServizioApplicativo(nome, env.idSoggetto.getNome(), env.tipo_protocollo, env.saCore), "Servizio Applicativo");
			
			final ServizioApplicativo tmpSa = ApplicativiApiHelper.applicativoToServizioApplicativo(applicativo, env.tipo_protocollo, soggetto, env.stationCore);
			final ServizioApplicativo newSa = ApplicativiApiHelper.getServizioApplicativo(nome, env.idSoggetto.getNome(), env.tipo_protocollo, env.saCore);
			
			newSa.setNome(tmpSa.getNome());
			newSa.setIdSoggetto(tmpSa.getIdSoggetto());
			newSa.setNomeSoggettoProprietario(tmpSa.getNomeSoggettoProprietario());
			newSa.setTipoSoggettoProprietario(tmpSa.getTipoSoggettoProprietario());		
			newSa.getInvocazionePorta().setCredenzialiList(tmpSa.getInvocazionePorta().getCredenzialiList());
			newSa.getInvocazionePorta().setRuoli(tmpSa.getInvocazionePorta().getRuoli());
			
			if (!oldSa.getNome().equals(newSa.getNome())) {
				throw FaultCode.RICHIESTA_NON_VALIDA.toException("Non è possibile modificare il nome del servizio applicativo");
			}
					
			IDServizioApplicativo oldID = new IDServizioApplicativo();
			oldID.setIdSoggettoProprietario(ApplicativiApiHelper.getIDSoggetto(oldSa.getNomeSoggettoProprietario(), env.tipo_protocollo));
			oldID.setNome(oldSa.getNome());
			
			newSa.setOldIDServizioApplicativoForUpdate(oldID);
			
			List<ExtendedConnettore> listExtendedConnettore = null;	// Non serve alla checkData perchè da Api, gli applicativi sono sempre fruitori
			
			ApplicativiApiHelper.overrideSAParameters(env.requestWrapper, env.saHelper, newSa, applicativo);
			env.requestWrapper.overrideParameter(ServiziApplicativiCostanti.PARAMETRO_SERVIZI_APPLICATIVI_PROTOCOLLO, env.tipo_protocollo);
			
			if (! env.saHelper.servizioApplicativoCheckData(
					TipoOperazione.CHANGE,
					null,
					oldSa.getIdSoggetto(),
					newSa.getTipologiaFruizione(),
					newSa.getTipologiaErogazione(),
					listExtendedConnettore, null
				)) {
				throw FaultCode.RICHIESTA_NON_VALIDA.toException(StringEscapeUtils.unescapeHtml(env.pd.getMessage()));
			}
			
			env.saCore.performUpdateOperation(env.userLogin, false, newSa);
					
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

