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
package org.openspcoop2.core.config.rs.server.api.impl.ruoli;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringEscapeUtils;
import org.openspcoop2.core.commons.Filtri;
import org.openspcoop2.core.commons.Liste;
import org.openspcoop2.core.config.driver.DriverConfigurazioneException;
import org.openspcoop2.core.config.rs.server.api.RuoliApi;
import org.openspcoop2.core.config.rs.server.api.impl.Enums;
import org.openspcoop2.core.config.rs.server.api.impl.Helper;
import org.openspcoop2.core.config.rs.server.config.ServerProperties;
import org.openspcoop2.core.config.rs.server.model.ContestoEnum;
import org.openspcoop2.core.config.rs.server.model.FonteEnum;
import org.openspcoop2.core.config.rs.server.model.ListaRuoli;
import org.openspcoop2.core.config.rs.server.model.Ruolo;
import org.openspcoop2.core.id.IDRuolo;
import org.openspcoop2.utils.service.BaseImpl;
import org.openspcoop2.utils.service.authorization.AuthorizationConfig;
import org.openspcoop2.utils.service.authorization.AuthorizationManager;
import org.openspcoop2.utils.service.beans.utils.BaseHelper;
import org.openspcoop2.utils.service.beans.utils.ListaUtils;
import org.openspcoop2.utils.service.context.IContext;
import org.openspcoop2.utils.service.fault.jaxrs.FaultCode;
import org.openspcoop2.web.ctrlstat.core.Search;
import org.openspcoop2.web.ctrlstat.servlet.ruoli.RuoliUtilities;
import org.openspcoop2.web.lib.mvc.TipoOperazione;

/**
 * RuoliApiServiceImpl
 * 
 * @author $Author$
 * @version $Rev$, $Date$
 * 
 */
public class RuoliApiServiceImpl extends BaseImpl implements RuoliApi {

	public RuoliApiServiceImpl(){
		super(org.slf4j.LoggerFactory.getLogger(RuoliApiServiceImpl.class));
	}

	private AuthorizationConfig getAuthorizationConfig() throws Exception{
		return new AuthorizationConfig(ServerProperties.getInstance().getProperties());
	}

    /**
     * Creazione di un ruolo
     *
     * Questa operazione consente di creare un ruolo
     *
     */
	@Override
    public void createRuolo(Ruolo body) {
		IContext context = this.getContext();
		try {
			context.getLogger().info("Invocazione in corso ...");     

			AuthorizationManager.authorize(context, getAuthorizationConfig());
			context.getLogger().debug("Autorizzazione completata con successo");
			
			BaseHelper.throwIfNull(body);
                        
			Ruolo ruolo = body;
					
			RuoliEnv env = new RuoliEnv(context.getServletRequest(),context);
			RuoliApiHelper.overrideRuoloParams(ruolo, env.requestWrapper);
			
			org.openspcoop2.core.registry.Ruolo regRuolo = null;
			try {
				regRuolo = RuoliApiHelper.apiRuoloToRuoloRegistro(ruolo, env.userLogin);
			} catch (Exception e) {
				throw FaultCode.RICHIESTA_NON_VALIDA.toException(e.getMessage());
			}
			
			if(env.ruoliCore.existsRuolo(ruolo.getNome())){
				throw FaultCode.CONFLITTO.toException("Un ruolo con nome '" + ruolo.getNome() + "' risulta già stato registrato");
			}
			
			if (!env.ruoliHelper.ruoloCheckData(TipoOperazione.ADD, null)) {
				throw FaultCode.RICHIESTA_NON_VALIDA.toException(StringEscapeUtils.unescapeHtml(env.pd.getMessage()));
			}
			
			env.ruoliCore.performCreateOperation(env.userLogin, false, regRuolo);			
        
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
     * Elimina un ruolo
     *
     * Questa operazione consente di eliminare un ruolo identificato dal nome
     *
     */
	@Override
    public void deleteRuolo(String nome) {
		IContext context = this.getContext();
		try {
			context.getLogger().info("Invocazione in corso ...");     

			AuthorizationManager.authorize(context, getAuthorizationConfig());
			context.getLogger().debug("Autorizzazione completata con successo");     
                        
			String userLogin = context.getAuthentication().getName();
			RuoliEnv rEnv = new RuoliEnv(context.getServletRequest(),context);
						
			final org.openspcoop2.core.registry.Ruolo regRuolo = BaseHelper.evalnull( () -> rEnv.ruoliCore.getRuolo(nome) );
			
			if ( regRuolo != null ) {
				StringBuffer inUsoMessage = new StringBuffer();
				RuoliUtilities.deleteRuolo(regRuolo, userLogin, rEnv.ruoliCore, rEnv.ruoliHelper, inUsoMessage, System.lineSeparator());
				
				if (inUsoMessage.length() > 0) {
					throw FaultCode.RICHIESTA_NON_VALIDA.toException(StringEscapeUtils.unescapeHtml(inUsoMessage.toString()));
				}
			}
			
			if (rEnv.delete_404 && regRuolo == null) {
				throw FaultCode.NOT_FOUND.toException("Nessun ruolo corrisponde al nome indicato");
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
     * Ricerca ruoli
     *
     * Elenca i ruoli registrati
     *
     */
	@Override
    public ListaRuoli findAllRuoli(String q, Integer limit, Integer offset, FonteEnum fonte, ContestoEnum contesto) {
		IContext context = this.getContext();
		try {
			context.getLogger().info("Invocazione in corso ...");     

			AuthorizationManager.authorize(context, getAuthorizationConfig());
			context.getLogger().debug("Autorizzazione completata con successo");     
			
			RuoliEnv rEnv = new RuoliEnv(context.getServletRequest(), context);
						
			int idLista = Liste.RUOLI;
			Search ricerca =  Helper.setupRicercaPaginata(q, limit, offset, idLista);
			
			if (fonte != null)
				ricerca.addFilter(idLista, Filtri.FILTRO_RUOLO_TIPOLOGIA, Enums.apiFonteToRegistroTipologia(fonte).toString());
			if (contesto != null)
				ricerca.addFilter(idLista, Filtri.FILTRO_RUOLO_CONTESTO, Enums.apiContestoToRegistroContesto(contesto).toString());


			List<org.openspcoop2.core.registry.Ruolo> lista = new ArrayList<>();
			try {
				lista = rEnv.ruoliCore.ruoliList(null, ricerca);
			} catch (Exception e) {
				throw FaultCode.NOT_FOUND.toException(e.getMessage());
			}			
			
			
			if ( rEnv.findall_404 && lista.isEmpty() ) {
				throw FaultCode.NOT_FOUND.toException("Nessun ruolo corrisponde ai criteri di ricerca specificati");
			}
			
			final ListaRuoli ret = ListaUtils.costruisciListaPaginata(
					context.getUriInfo(),
					ricerca.getIndexIniziale(idLista),
					ricerca.getPageSize(idLista), 
					ricerca.getNumEntries(idLista), 
					ListaRuoli.class
				); 
			
			lista.forEach( regRuolo -> ret.addItemsItem(
					RuoliApiHelper.ruoloApiToRuoloItem(RuoliApiHelper.ruoloRegistroToApiRuolo(regRuolo))
					)
			);
			
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
     * Restituisce il dettaglio di un ruolo
     *
     * Questa operazione consente di ottenere il dettaglio di un ruolo identificato dal nome
     *
     */
	@Override
    public Ruolo getRuolo(String nome) {
		IContext context = this.getContext();
		try {
			context.getLogger().info("Invocazione in corso ...");     

			AuthorizationManager.authorize(context, getAuthorizationConfig());
			context.getLogger().debug("Autorizzazione completata con successo");     
                        
			RuoliEnv rEnv = new RuoliEnv(context.getServletRequest(), context);
			
			org.openspcoop2.core.registry.Ruolo regRuolo = null;
			try {
				regRuolo = rEnv.ruoliCore.getRuolo(nome);
			} catch (DriverConfigurazioneException e) {	}
			
			if (regRuolo == null) {
				throw FaultCode.NOT_FOUND.toException("Non esiste nessun ruolo con nome " + nome);
			}
        
			context.getLogger().info("Invocazione completata con successo");
			return RuoliApiHelper.ruoloRegistroToApiRuolo(regRuolo);     
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
     * Modifica i dati di un ruolo
     *
     * Questa operazione consente di aggiornare i dati di un ruolo identificato dal nome
     *
     */
	@Override
    public void updateRuolo(Ruolo body, String nome) {
		IContext context = this.getContext();
		try {
			context.getLogger().info("Invocazione in corso ...");     

			AuthorizationManager.authorize(context, getAuthorizationConfig());
			context.getLogger().debug("Autorizzazione completata con successo");
			
			BaseHelper.throwIfNull(body);
                        		
			RuoliEnv rEnv = new RuoliEnv(context.getServletRequest(), context);
			RuoliApiHelper.overrideRuoloParams(body, rEnv.requestWrapper);
			
			org.openspcoop2.core.registry.Ruolo oldRuolo = null;
			try {
				oldRuolo = rEnv.ruoliCore.getRuolo(nome); 
			} catch (Exception e) {}
			if (oldRuolo == null) throw FaultCode.NOT_FOUND.toException("Nessun ruolo da aggiornare corrisponde al nome: " + nome);
			
			org.openspcoop2.core.registry.Ruolo ruoloNEW = RuoliApiHelper.apiRuoloToRuoloRegistro(body, rEnv.userLogin);
			ruoloNEW.setOldIDRuoloForUpdate(new IDRuolo(oldRuolo.getNome()));	// Qui nella versione della controlstation parte dal nuovo nome assumendo che siano identici.
			
			if (!rEnv.ruoliHelper.ruoloCheckData(TipoOperazione.CHANGE, oldRuolo)) {
				throw FaultCode.RICHIESTA_NON_VALIDA.toException(StringEscapeUtils.unescapeHtml(rEnv.pd.getMessage()));
			}

			List<Object> listOggettiDaAggiornare = new ArrayList<>();
			listOggettiDaAggiornare.add(ruoloNEW);
			
			if(oldRuolo.getNome().equals(ruoloNEW.getNome())==false) {					
				IDRuolo oldIdRuolo = ruoloNEW.getOldIDRuoloForUpdate();
				oldIdRuolo.setNome(nome);
				
				RuoliUtilities.findOggettiDaAggiornare(oldIdRuolo, ruoloNEW, rEnv.ruoliCore, listOggettiDaAggiornare);
			}
			
			rEnv.ruoliCore.performUpdateOperation(rEnv.userLogin, false, listOggettiDaAggiornare.toArray());
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

