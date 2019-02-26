package org.openspcoop2.core.config.rs.server.api.impl.scope;

import java.util.ArrayList;
import java.util.List;

import org.openspcoop2.core.commons.Filtri;
import org.openspcoop2.core.commons.Liste;
import org.openspcoop2.core.config.rs.server.api.ScopeApi;
import org.openspcoop2.core.config.rs.server.api.impl.Helper;
import org.openspcoop2.core.config.rs.server.api.impl.HttpRequestWrapper;
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
 * GovWay Config API
 *
 * <p>Servizi per la configurazione di GovWay
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
    public void create(Scope body) {
		IContext context = this.getContext();
		try {
			context.getLogger().info("Invocazione in corso ...");     

			AuthorizationManager.authorize(context, getAuthorizationConfig());
			context.getLogger().debug("Autorizzazione completata con successo");  
			
			if(context.getServletResponse()!=null) {
				System.out.println("SERVLET RESPONSE NOT NULL");
			}
			else {
				System.out.println("SERVLET RESPONSE NULL");
			}
			
			if (body == null) {
				throw FaultCode.RICHIESTA_NON_VALIDA.toException("Body mancante.");
			}
			
			HttpRequestWrapper wrap = ScopeApiHelper.ovverrideParameters(context.getServletRequest(), body);
			ScopeEnv sEnv = new ScopeEnv(wrap,context);
			
			org.openspcoop2.core.registry.Scope regScope = ScopeApiHelper.apiScopeToRegistroScope(body, sEnv.userLogin);
			
			if(sEnv.scopeCore.existsScope(regScope.getNome())) {
				throw FaultCode.CONFLITTO.toException("Un scope con nome '" + regScope.getNome() + "' risulta già registrato");
			}
			
			if (!sEnv.scopeHelper.scopeCheckData(TipoOperazione.ADD, null)) {
				throw FaultCode.RICHIESTA_NON_VALIDA.toException(sEnv.pd.getMessage());
			}
			
			sEnv.scopeCore.performCreateOperation(sEnv.userLogin, false, regScope);

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
    public void delete(String nome) {
		IContext context = this.getContext();
		try {
			context.getLogger().info("Invocazione in corso ...");     

			AuthorizationManager.authorize(context, getAuthorizationConfig());
			context.getLogger().debug("Autorizzazione completata con successo");
			
			ScopeEnv sEnv = new ScopeEnv(context.getServletRequest(),context);
			
			org.openspcoop2.core.registry.Scope scope = null;
			try {
				scope = sEnv.scopeCore.getScope(nome);
			} catch (Exception e) {}
			
			if (scope == null) {
				throw FaultCode.NOT_FOUND.toException("Nessuno scope da eliminare corrisponde al nome: " + nome);
			}
			
			StringBuffer inUsoMessage = new StringBuffer();
			ScopeUtilities.deleteScope(scope, sEnv.userLogin, sEnv.scopeCore, sEnv.scopeHelper, inUsoMessage, "\n");
			
			if (inUsoMessage.length() > 0) {
				throw FaultCode.RICHIESTA_NON_VALIDA.toException(inUsoMessage.toString());
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
    public ListaScope findAll(String q, Integer limit, Integer offset, ContestoEnum contesto) {
		IContext context = this.getContext();
		try {
			context.getLogger().info("Invocazione in corso ...");
			AuthorizationManager.authorize(context, getAuthorizationConfig());
			
			context.getLogger().debug("Autorizzazione completata con successo");     
            
			ScopeEnv env = new ScopeEnv(context.getServletRequest(), context);
			
			int idLista = Liste.SCOPE;
			Search ricerca = Helper.setupRicercaPaginata(q, limit, offset, idLista);	
			
			if (contesto != null)
				ricerca.addFilter(idLista, Filtri.FILTRO_SCOPE_CONTESTO, ScopeApiHelper.apiContestoToRegistroContesto.get(contesto).toString());
						
			List<org.openspcoop2.core.registry.Scope> scopes = env.scopeCore.scopeList(null, ricerca);
			if (scopes.size() == 0) {
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
    public Scope get(String nome) {
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
    public void update(Scope body, String nome) {
		IContext context = this.getContext();
		try {
			context.getLogger().info("Invocazione in corso ...");     

			AuthorizationManager.authorize(context, getAuthorizationConfig());
			context.getLogger().debug("Autorizzazione completata con successo");     
            
			if (body == null)
				throw FaultCode.RICHIESTA_NON_VALIDA.toException("Body mancante");
			
			HttpRequestWrapper wrap = ScopeApiHelper.ovverrideParameters(context.getServletRequest(), body);
			ScopeEnv env = new ScopeEnv(wrap, context);
			
			org.openspcoop2.core.registry.Scope scopeNEW = ScopeApiHelper.apiScopeToRegistroScope(body, env.userLogin);
			org.openspcoop2.core.registry.Scope scopeOLD = null;
			
			try {
				scopeOLD = env.scopeCore.getScope(nome); 
			} catch (Exception e) {}
			
			// Chiedere ad andrea se il detail lo vuole privato o meno.
			if (scopeOLD == null)
				throw FaultCode.NOT_FOUND.toException("Nessuno scope corrisponde al nome " + nome);
			
			if (!env.scopeHelper.scopeCheckData(TipoOperazione.CHANGE, scopeOLD)) {
				throw FaultCode.RICHIESTA_NON_VALIDA.toException(env.pd.getMessage());
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

