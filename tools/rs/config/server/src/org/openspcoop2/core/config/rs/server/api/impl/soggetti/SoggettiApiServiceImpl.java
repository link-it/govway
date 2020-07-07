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
package org.openspcoop2.core.config.rs.server.api.impl.soggetti;

import java.util.List;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.openspcoop2.core.commons.Filtri;
import org.openspcoop2.core.commons.Liste;
import org.openspcoop2.core.config.rs.server.api.SoggettiApi;
import org.openspcoop2.core.config.rs.server.api.impl.Helper;
import org.openspcoop2.core.config.rs.server.config.ServerProperties;
import org.openspcoop2.core.config.rs.server.model.AuthenticationHttpBasic;
import org.openspcoop2.core.config.rs.server.model.DominioEnum;
import org.openspcoop2.core.config.rs.server.model.ListaSoggetti;
import org.openspcoop2.core.config.rs.server.model.ModalitaAccessoEnum;
import org.openspcoop2.core.config.rs.server.model.Soggetto;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.core.registry.CredenzialiSoggetto;
import org.openspcoop2.utils.service.BaseImpl;
import org.openspcoop2.utils.service.authorization.AuthorizationConfig;
import org.openspcoop2.utils.service.authorization.AuthorizationManager;
import org.openspcoop2.utils.service.beans.ProfiloEnum;
import org.openspcoop2.utils.service.beans.utils.BaseHelper;
import org.openspcoop2.utils.service.beans.utils.ListaUtils;
import org.openspcoop2.utils.service.context.IContext;
import org.openspcoop2.utils.service.fault.jaxrs.FaultCode;
import org.openspcoop2.web.ctrlstat.core.Search;
import org.openspcoop2.web.ctrlstat.dao.SoggettoCtrlStat;
import org.openspcoop2.web.ctrlstat.servlet.soggetti.SoggettiUtilities;
import org.openspcoop2.web.lib.mvc.TipoOperazione;


/**
 * SoggettiApiServiceImpl
 * 
 * @author $Author$
 * @version $Rev$, $Date$
 * 
 */
public class SoggettiApiServiceImpl extends BaseImpl implements SoggettiApi {

	public SoggettiApiServiceImpl(){
		super(org.slf4j.LoggerFactory.getLogger(SoggettiApiServiceImpl.class));
	}

	private AuthorizationConfig getAuthorizationConfig() throws Exception{
		return new AuthorizationConfig(ServerProperties.getInstance().getProperties());
	}

    /**
     * Creazione di un soggetto
     *
     * Questa operazione consente di creare un soggetto
     *
     */
	@Override
    public void createSoggetto(Soggetto body, ProfiloEnum profilo) {
		IContext context = this.getContext();
		try {
			context.getLogger().info("Invocazione in corso ...");     

			AuthorizationManager.authorize(context, getAuthorizationConfig());
			context.getLogger().debug("Autorizzazione completata con successo");

			SoggettiEnv env = new SoggettiEnv(context.getServletRequest(),  profilo, context);

			/*if (profilo == null)
				profilo = Helper.getProfiloDefault();*/
            
			Soggetto soggetto = null;
			try{
				soggetto = (Soggetto) body;
				
				if ( soggetto.getCredenziali() != null && soggetto.getCredenziali().getModalitaAccesso() != null ) {
					soggetto.setCredenziali(Helper.translateCredenziali(soggetto.getCredenziali(), true));
					SoggettiApiHelper.overrideAuthParams(env.soggettiHelper, soggetto, env.requestWrapper);
				}
				
			}
			catch(javax.ws.rs.WebApplicationException e) {
				throw e;
			}
			catch(Exception e) {
				throw FaultCode.RICHIESTA_NON_VALIDA.toException(e);
			}
			
			org.openspcoop2.core.registry.Soggetto soggettoRegistro = SoggettiApiHelper.soggettoApiToRegistro(soggetto, env);
			
			IDSoggetto idSogg = new IDSoggetto();
			idSogg.setNome(soggettoRegistro.getNome());
			idSogg.setTipo(soggettoRegistro.getTipo());
			idSogg.setCodicePorta(soggettoRegistro.getIdentificativoPorta());
			
			if (env.soggettiCore.existsSoggetto(idSogg))
				throw FaultCode.CONFLITTO.toException("Esiste già un soggetto con il nome e il tipo scelti");
			if (env.soggettiCore.existsSoggetto(soggettoRegistro.getCodiceIpa()))
				throw FaultCode.CONFLITTO.toException("Esiste già un soggetto con codice IPA " + soggettoRegistro.getCodiceIpa());
			
			boolean isOk = env.soggettiHelper.soggettiCheckData(
					TipoOperazione.ADD,
					null,
					env.tipo_soggetto, 
					soggettoRegistro.getNome(),
					soggettoRegistro.getCodiceIpa(),
					null,	//pd_url_prefix_rewriter,
					null,	//pa_url_prefix_rewriter,
					null, false, soggettoRegistro.getDescrizione());
			
			if (!isOk)
				throw FaultCode.RICHIESTA_NON_VALIDA.toException(StringEscapeUtils.unescapeHtml(env.pd.getMessage()));
	
			// Dal debug isRouter è sempre false.
			org.openspcoop2.core.config.Soggetto soggettoConfig = SoggettiApiHelper.soggettoRegistroToConfig(soggettoRegistro,new org.openspcoop2.core.config.Soggetto(),false);
			SoggettoCtrlStat sog = new SoggettoCtrlStat(soggettoRegistro, soggettoConfig);
			
			env.soggettiCore.performCreateOperation(env.userLogin, false, sog);		
        
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
     * Elimina un soggetto
     *
     * Questa operazione consente di eliminare un soggetto identificato dal nome
     *
     */
	@Override
    public void deleteSoggetto(String nome, ProfiloEnum profilo) {
		IContext context = this.getContext();
		try {
			context.getLogger().info("Invocazione in corso ...");     

			AuthorizationManager.authorize(context, getAuthorizationConfig());
			context.getLogger().debug("Autorizzazione completata con successo");
			
			if (profilo == null)
				profilo = Helper.getProfiloDefault();
			
			SoggettiEnv env = new SoggettiEnv(context.getServletRequest(), profilo, context);
			IDSoggetto idSogg = new IDSoggetto(env.tipo_soggetto, nome);
			
			org.openspcoop2.core.registry.Soggetto soggettoRegistro = BaseHelper.evalnull(() -> env.soggettiCore.getSoggettoRegistro(idSogg) );
			org.openspcoop2.core.config.Soggetto soggettoConfig = BaseHelper.evalnull(() -> env.soggettiCore.getSoggetto(soggettoRegistro.getId() ));
			
			if  ( soggettoRegistro != null && soggettoConfig != null ) {

				StringBuilder inUsoMessage = new StringBuilder();
				SoggettiUtilities.deleteSoggetto(
						soggettoRegistro,
						soggettoConfig,
						env.userLogin,
						env.soggettiCore,
						env.soggettiHelper, 
						inUsoMessage,
						"\n"
					);
				if (inUsoMessage.length() > 0)
					throw FaultCode.RICHIESTA_NON_VALIDA.toException(StringEscapeUtils.escapeHtml(inUsoMessage.toString()));
			}
			else if (env.delete_404) {
				throw FaultCode.NOT_FOUND.toException("Nessun soggetto corrisponde al nome e il profilo indicati");
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
     * Ricerca soggetti
     *
     * Elenca i soggetti registrati
     *
     */
	@Override
    public ListaSoggetti findAllSoggetti(ProfiloEnum profilo, String q, Integer limit, Integer offset, DominioEnum dominio, String ruolo) {
		IContext context = this.getContext();
		try {
			context.getLogger().info("Invocazione in corso ...");     

			AuthorizationManager.authorize(context, getAuthorizationConfig());
			context.getLogger().debug("Autorizzazione completata con successo");
			
			SoggettiEnv env = new SoggettiEnv(context.getServletRequest(), profilo, context);                        
			int idLista = Liste.SOGGETTI;
			Search ricerca = Helper.setupRicercaPaginata(q, limit, offset, idLista, env.idSoggetto.toIDSoggetto(), env.tipo_protocollo);
			
			if (dominio != null)
				ricerca.addFilter(idLista, Filtri.FILTRO_DOMINIO, dominio.toString());
			if (ruolo != null && ruolo.trim().length() > 0)
				ricerca.addFilter(idLista, Filtri.FILTRO_RUOLO, ruolo.trim());
			
			List<org.openspcoop2.core.registry.Soggetto> soggetti = env.soggettiCore.soggettiRegistroList(null, ricerca);
			
			if ( soggetti.size() == 0 && env.findall_404 )
				throw FaultCode.NOT_FOUND.toException("Nessun soggetto corrisponde ai criteri di ricerca specificati");
			
			final ListaSoggetti ret = ListaUtils.costruisciListaPaginata(
					context.getUriInfo(),
					ricerca.getIndexIniziale(idLista),
					ricerca.getPageSize(idLista), 
					ricerca.getNumEntries(idLista), 
					ListaSoggetti.class
				); 
			
			soggetti.forEach( s -> {
				ret.addItemsItem(SoggettiApiHelper.soggettoRegistroToItem(s,env.pddCore,env.soggettiCore));
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
     * Restituisce il dettaglio di un soggetto
     *
     * Questa operazione consente di ottenere il dettaglio di un soggetto identificato dal nome
     * 
     *
     */
	@Override
    public Soggetto getSoggetto(String nome, ProfiloEnum profilo) {
		IContext context = this.getContext();
		try {
			context.getLogger().info("Invocazione in corso ...");     

			AuthorizationManager.authorize(context, getAuthorizationConfig());
			context.getLogger().debug("Autorizzazione completata con successo");     
			
			SoggettiEnv env = new SoggettiEnv(context.getServletRequest(), profilo, context);			
			IDSoggetto idSogg = new IDSoggetto(env.tipo_soggetto,nome);
			
			org.openspcoop2.core.registry.Soggetto soggettoReg = BaseHelper.supplyOrNotFound( () -> env.soggettiCore.getSoggettoRegistro(idSogg), "Soggetto " + idSogg.toString() );
			
			Soggetto soggettoApi = BaseHelper.supplyOrNotFound( () -> SoggettiApiHelper.soggettoRegistroToApi(soggettoReg, env.pddCore,env.soggettiCore), "Soggetto " + idSogg.toString());
			
			if (soggettoApi == null)
				throw FaultCode.NOT_FOUND.toException("Nessun soggetto trovato corrisponde al nome " + nome + " e profilo " + profilo.toString());
                       
        
			context.getLogger().info("Invocazione completata con successo");
			return soggettoApi;
     
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
     * Modifica i dati di un soggetto
     *
     * Questa operazione consente di aggiornare i dati di un soggetto identificato dal nome
     *
     */
	@Override
    public void updateSoggetto(Soggetto body, String nome, ProfiloEnum profilo) {
		IContext context = this.getContext();
		try {
			context.getLogger().info("Invocazione in corso ...");     

			AuthorizationManager.authorize(context, getAuthorizationConfig());
			context.getLogger().debug("Autorizzazione completata con successo");
			
			if (profilo == null)
				profilo = Helper.getProfiloDefault();
            
			Soggetto soggetto = null;
			try{
				soggetto = (Soggetto) body;
				soggetto.setCredenziali(Helper.translateCredenziali(soggetto.getCredenziali(), false));
			}
			catch(javax.ws.rs.WebApplicationException e) {
				throw e;
			}
			catch(Throwable e) {
				throw FaultCode.RICHIESTA_NON_VALIDA.toException(e);
			}

			final SoggettiEnv env = new SoggettiEnv(context.getServletRequest(), profilo, context);
			SoggettiApiHelper.overrideAuthParams(env.soggettiHelper, soggetto, env.requestWrapper);
			
			final IDSoggetto idSogg = new IDSoggetto(env.tipo_soggetto,nome);
			org.openspcoop2.core.registry.Soggetto oldSoggetto = null; 
			try {
				oldSoggetto = env.soggettiCore.getSoggettoRegistro(idSogg);
			} catch (Exception e) {			
			}
			
			if (oldSoggetto == null)
				throw FaultCode.NOT_FOUND.toException("Nessun soggetto trovato corrisponde al nome " + nome + " e profilo " + profilo.toString());
			
			SoggettoCtrlStat oldSoggettoCtrlStat = env.soggettiCore.getSoggettoCtrlStat(oldSoggetto.getId());
			
			final org.openspcoop2.core.registry.Soggetto newSoggetto = env.soggettiCore.getSoggettoRegistro(idSogg);
			
			SoggettiApiHelper.convert(body, newSoggetto, env);
			
			if(ModalitaAccessoEnum.HTTP_BASIC.equals(body.getCredenziali().getModalitaAccesso())) {
				AuthenticationHttpBasic httpBasic = (AuthenticationHttpBasic) body.getCredenziali();
				if(httpBasic.getPassword()==null || StringUtils.isEmpty(httpBasic.getPassword())) {
					// password in update non è obbligatoria
					boolean set = false;
					if(oldSoggetto.getCredenziali()!=null) {
						CredenzialiSoggetto cImageDB = oldSoggetto.getCredenziali();
						CredenzialiSoggetto cTmp = newSoggetto.getCredenziali();
						if(cImageDB!=null && cTmp!=null) {
							cTmp.setPassword(cImageDB.getPassword());
							cTmp.setCertificateStrictVerification(cImageDB.isCertificateStrictVerification());
							set = true;
						}
					}
					if(!set) {
						throw FaultCode.RICHIESTA_NON_VALIDA.toException("Tipo di autenticazione '"+body.getCredenziali().getModalitaAccesso()+"'; indicare la password");
					}
				}
			}
			
			boolean isOk = env.soggettiHelper.soggettiCheckData(
					TipoOperazione.CHANGE,
					oldSoggetto.getId().toString(),
					env.tipo_soggetto, 
					newSoggetto.getNome(), 
					newSoggetto.getCodiceIpa(),
					null,  // this.pd_url_prefix_rewriter, 
					null, // this.pa_url_prefix_rewriter,
					oldSoggetto,
					false,	//IsSupportatoAutenticazione
					newSoggetto.getDescrizione()
				);
			
			if (!isOk)
				throw FaultCode.RICHIESTA_NON_VALIDA.toException(StringEscapeUtils.unescapeHtml(env.pd.getMessage()));
			
			org.openspcoop2.core.config.Soggetto soggettoConfig = SoggettiApiHelper.soggettoRegistroToConfig(newSoggetto,oldSoggettoCtrlStat.getSoggettoConf(),false);
			newSoggetto.setOldIDSoggettoForUpdate(new IDSoggetto(oldSoggetto.getTipo(), oldSoggetto.getNome()));
			soggettoConfig.setOldIDSoggettoForUpdate(new IDSoggetto(oldSoggetto.getTipo(), oldSoggetto.getNome()));

			SoggettoCtrlStat sog = new SoggettoCtrlStat(newSoggetto, soggettoConfig);
			
			sog.setOldNomeForUpdate(oldSoggetto.getNome());
			sog.setOldTipoForUpdate(oldSoggetto.getTipo());

			// eseguo l'aggiornamento
			List<Object> listOggettiDaAggiornare = SoggettiUtilities.getOggettiDaAggiornare(
					env.soggettiCore, 
					oldSoggetto.getNome(),
					newSoggetto.getNome(),
					oldSoggetto.getTipo(),
					newSoggetto.getTipo(),
					sog)
				;
			env.soggettiCore.performUpdateOperation(env.userLogin, false, listOggettiDaAggiornare.toArray());

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

