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
package org.openspcoop2.core.config.rs.server.api.impl.scope;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringEscapeUtils;
import org.openspcoop2.core.commons.Filtri;
import org.openspcoop2.core.commons.Liste;
import org.openspcoop2.core.config.rs.server.api.ScopeApi;
import org.openspcoop2.core.config.rs.server.api.impl.Enums;
import org.openspcoop2.core.config.rs.server.api.impl.Helper;
import org.openspcoop2.core.config.rs.server.config.ServerProperties;
import org.openspcoop2.core.config.rs.server.model.ContestoEnum;
import org.openspcoop2.core.config.rs.server.model.ListaScope;
import org.openspcoop2.core.config.rs.server.model.Scope;
import org.openspcoop2.core.id.IDScope;
import org.openspcoop2.utils.service.BaseImpl;
import org.openspcoop2.utils.service.authorization.AuthorizationConfig;
import org.openspcoop2.utils.service.authorization.AuthorizationManager;
import org.openspcoop2.utils.service.context.IContext;
import org.openspcoop2.utils.service.fault.jaxrs.FaultCode;
import org.openspcoop2.web.ctrlstat.core.Search;
import org.openspcoop2.web.ctrlstat.servlet.scope.ScopeUtilities;
import org.openspcoop2.web.lib.mvc.TipoOperazione;

/**
 * ScopeApiServiceImpl
 * 
 * @author $Author$
 * @version $Rev$, $Date$
 * 
 */
public class ScopeApiServiceImpl extends BaseImpl implements ScopeApi {

	public ScopeApiServiceImpl(){
		super(org.slf4j.LoggerFactory.getLogger(ScopeApiServiceImpl.class));
	}

	private AuthorizationConfig getAuthorizationConfig() throws Exception{
		return new AuthorizationConfig(ServerProperties.getInstance().getProperties());
	}
	
    /**
     * Creazione di uno scope
     *
     * Questa operazione consente di creare uno scope
     *
     */
	@Override
    public void createScope(Scope body) {
		IContext context = this.getContext();
		try {
			context.getLogger().info("Invocazione in corso ...");     

			AuthorizationManager.authorize(context, getAuthorizationConfig());
			context.getLogger().debug("Autorizzazione completata con successo");  
			
			Helper.throwIfNull(body);
			
			ScopeEnv env = new ScopeEnv(context.getServletRequest(),context);
			ScopeApiHelper.ovverrideParameters(env.requestWrapper, body);
			
			org.openspcoop2.core.registry.Scope regScope = ScopeApiHelper.apiScopeToRegistroScope(body, env.userLogin);
			
			if(env.scopeCore.existsScope(regScope.getNome())) {
				throw FaultCode.CONFLITTO.toException("Un scope con nome '" + regScope.getNome() + "' risulta già registrato");
			}
			
			if (!env.scopeHelper.scopeCheckData(TipoOperazione.ADD, null)) {
				throw FaultCode.RICHIESTA_NON_VALIDA.toException(StringEscapeUtils.unescapeHtml(env.pd.getMessage()));
			}
			
			env.scopeCore.performCreateOperation(env.userLogin, false, regScope);

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
     * Elimina uno scope
     *
     * Questa operazione consente di eliminare uno scope identificato dal nome
     *
     */
	@Override
    public void deleteScope(String nome) {
		IContext context = this.getContext();
		try {
			context.getLogger().info("Invocazione in corso ...");     

			AuthorizationManager.authorize(context, getAuthorizationConfig());
			context.getLogger().debug("Autorizzazione completata con successo");
			
			ScopeEnv sEnv = new ScopeEnv(context.getServletRequest(),context);
			
			final org.openspcoop2.core.registry.Scope scope = Helper.evalnull( () -> sEnv.scopeCore.getScope(nome) );
			
			if ( scope != null ) {
				StringBuffer inUsoMessage = new StringBuffer();
				ScopeUtilities.deleteScope(scope, sEnv.userLogin, sEnv.scopeCore, sEnv.scopeHelper, inUsoMessage, "\n");
				
				if (inUsoMessage.length() > 0) {
					throw FaultCode.RICHIESTA_NON_VALIDA.toException(StringEscapeUtils.unescapeHtml(inUsoMessage.toString()));
				}
			}
			
			if ( scope == null && sEnv.delete_404 ) {
				throw FaultCode.NOT_FOUND.toException("Nessuno scope da eliminare corrisponde al nome: " + nome);
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
     * Ricerca scope
     *
     * Elenca gli scope registrati
     *
     */
	@Override
    public ListaScope findAllScope(String q, Integer limit, Integer offset, ContestoEnum contesto) {
		IContext context = this.getContext();
		try {
			context.getLogger().info("Invocazione in corso ...");
			AuthorizationManager.authorize(context, getAuthorizationConfig());
			
			context.getLogger().debug("Autorizzazione completata con successo");     
            
			ScopeEnv env = new ScopeEnv(context.getServletRequest(), context);
			
			int idLista = Liste.SCOPE;
			Search ricerca = Helper.setupRicercaPaginata(q, limit, offset, idLista);
			
			
			if (contesto != null)
				ricerca.addFilter(idLista, Filtri.FILTRO_SCOPE_CONTESTO, Enums.apiContestoToRegistroContesto.get(contesto).toString());
						
			List<org.openspcoop2.core.registry.Scope> scopes = env.scopeCore.scopeList(null, ricerca);
			if ( env.findall_404 && scopes.isEmpty() ) {
				throw FaultCode.NOT_FOUND.toException("Nessuno scope corrisponde ai criteri di ricerca specificati");
			}
		
			final ListaScope ret = Helper.costruisciListaPaginata(
					context.getServletRequest().getRequestURI(),
					offset, 
					limit, 
					ricerca.getNumEntries(idLista), 
					ListaScope.class
				); 
			
			scopes.forEach( regScope -> ret.addItemsItem(ScopeApiHelper.registroScopeToScopeItem(regScope))	);
				
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
     * Restituisce il dettaglio di uno scope
     *
     * Questa operazione consente di ottenere il dettaglio di uno scope identificato dal nome
     *
     */
	@Override
    public Scope getScope(String nome) {
		IContext context = this.getContext();
		try {
			context.getLogger().info("Invocazione in corso ...");     

			AuthorizationManager.authorize(context, getAuthorizationConfig());
			context.getLogger().debug("Autorizzazione completata con successo");     
			
			ScopeEnv env = new ScopeEnv(context.getServletRequest(), context);
			
			org.openspcoop2.core.registry.Scope regScope = null;
			try {
				regScope = env.scopeCore.getScope(nome);
			} catch (Exception e) {}
			
			if (regScope == null)
				throw FaultCode.NOT_FOUND.toException("Non trovato alcuno scope con nome " + nome);
			
			context.getLogger().info("Invocazione completata con successo");
			
			return ScopeApiHelper.registroScopeToScope(regScope);
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
     * Modifica i dati di uno scope
     *
     * Questa operazione consente di aggiornare i dati di uno scope identificato dal nome
     *
     */
	@Override
    public void updateScope(Scope body, String nome) {
		IContext context = this.getContext();
		try {
			context.getLogger().info("Invocazione in corso ...");     

			AuthorizationManager.authorize(context, getAuthorizationConfig());
			context.getLogger().debug("Autorizzazione completata con successo");     
            
			Helper.throwIfNull(body);
			
			ScopeEnv env = new ScopeEnv(context.getServletRequest(), context);
			ScopeApiHelper.ovverrideParameters(env.requestWrapper, body);
			
			org.openspcoop2.core.registry.Scope scopeNEW = ScopeApiHelper.apiScopeToRegistroScope(body, env.userLogin);
			org.openspcoop2.core.registry.Scope scopeOLD = Helper.evalnull( () -> env.scopeCore.getScope(nome));
						
			// Chiedere ad andrea se il detail lo vuole privato o meno.
			if (scopeOLD == null)
				throw FaultCode.NOT_FOUND.toException("Nessuno scope corrisponde al nome " + nome);
			
			if (!env.scopeHelper.scopeCheckData(TipoOperazione.CHANGE, scopeOLD)) {
				throw FaultCode.RICHIESTA_NON_VALIDA.toException(StringEscapeUtils.unescapeHtml(env.pd.getMessage()));
			}
			
			scopeNEW.setOldIDScopeForUpdate(new IDScope(nome));

			List<Object> listOggettiDaAggiornare = new ArrayList<>();
			
			listOggettiDaAggiornare.add(scopeNEW);
			if(scopeNEW.getNome().equals(nome)==false){
				// e' stato modificato il nome
				IDScope oldIdScope = scopeNEW.getOldIDScopeForUpdate();
				oldIdScope.setNome(nome);
				
				ScopeUtilities.findOggettiDaAggiornare(oldIdScope, scopeNEW, env.scopeCore, listOggettiDaAggiornare);
			}
			
			env.scopeCore.performUpdateOperation(env.userLogin, false, listOggettiDaAggiornare.toArray());
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

