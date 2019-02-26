package org.openspcoop2.core.config.rs.server.api.impl.ruoli;

import java.util.ArrayList;
import java.util.List;

import org.openspcoop2.core.commons.Filtri;
import org.openspcoop2.core.commons.Liste;
import org.openspcoop2.core.config.driver.DriverConfigurazioneException;
import org.openspcoop2.core.config.rs.server.api.RuoliApi;
import org.openspcoop2.core.config.rs.server.api.impl.Helper;
import org.openspcoop2.core.config.rs.server.api.impl.HttpRequestWrapper;
import org.openspcoop2.core.config.rs.server.config.ServerProperties;
import org.openspcoop2.core.config.rs.server.model.ContestoEnum;
import org.openspcoop2.core.config.rs.server.model.FonteEnum;
import org.openspcoop2.core.config.rs.server.model.ListaRuoli;
import org.openspcoop2.core.config.rs.server.model.Ruolo;
import org.openspcoop2.core.id.IDRuolo;
import org.openspcoop2.utils.service.BaseImpl;
import org.openspcoop2.utils.service.authorization.AuthorizationConfig;
import org.openspcoop2.utils.service.authorization.AuthorizationManager;
import org.openspcoop2.utils.service.context.IContext;
import org.openspcoop2.utils.service.fault.jaxrs.FaultCode;
import org.openspcoop2.web.ctrlstat.core.Search;
import org.openspcoop2.web.ctrlstat.servlet.ruoli.RuoliUtilities;
import org.openspcoop2.web.lib.mvc.TipoOperazione;
/**
 * GovWay Config API
 *
 * <p>Servizi per la configurazione di GovWay
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
    public void create(Ruolo body) {
		IContext context = this.getContext();
		try {
			context.getLogger().info("Invocazione in corso ...");     

			AuthorizationManager.authorize(context, getAuthorizationConfig());
			context.getLogger().debug("Autorizzazione completata con successo");     
                        
			Ruolo ruolo = body;

			if (ruolo == null)
				throw FaultCode.RICHIESTA_NON_VALIDA.toException("Specificare un body corretto.");

			HttpRequestWrapper wrap = RuoliApiHelper.overrideRuoloParams(ruolo, context);		
			RuoliEnv rEnv = new RuoliEnv(wrap,context);
			
			org.openspcoop2.core.registry.Ruolo regRuolo = null;
			try {
				regRuolo = RuoliApiHelper.apiRuoloToRuoloRegistro(ruolo, rEnv.userLogin);
			} catch (Exception e) {
				throw FaultCode.RICHIESTA_NON_VALIDA.toException(e.getMessage());
			}
			
			if(rEnv.ruoliCore.existsRuolo(ruolo.getNome())){
				throw FaultCode.CONFLITTO.toException("Un ruolo con nome '" + ruolo.getNome() + "' risulta già stato registrato");
			}
			
			if (!rEnv.ruoliHelper.ruoloCheckData(TipoOperazione.ADD, null)) {
				throw FaultCode.RICHIESTA_NON_VALIDA.toException(rEnv.pd.getMessage());
			}
			
			rEnv.ruoliCore.performCreateOperation(rEnv.userLogin, false, regRuolo);			
        
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
     * Elimina un ruolo
     *
     * Questa operazione consente di eliminare un ruolo identificato dal nome
     *
     */
	@Override
    public void delete(String nome) {
		IContext context = this.getContext();
		try {
			context.getLogger().info("Invocazione in corso ...");     

			AuthorizationManager.authorize(context, getAuthorizationConfig());
			context.getLogger().debug("Autorizzazione completata con successo");     
                        
			HttpRequestWrapper wrap = new HttpRequestWrapper(context.getServletRequest());

			String userLogin = context.getAuthentication().getName();
			RuoliEnv rEnv = new RuoliEnv(wrap,context);
						
			org.openspcoop2.core.registry.Ruolo regRuolo = null;
			try {
				regRuolo = rEnv.ruoliCore.getRuolo(nome);
			}catch (Exception e) {
				throw FaultCode.NOT_FOUND.toException("Nessun ruolo corrisponde al nome indicato");
			}
			
			StringBuffer inUsoMessage = new StringBuffer();
			RuoliUtilities.deleteRuolo(regRuolo, userLogin, rEnv.ruoliCore, rEnv.ruoliHelper, inUsoMessage, "\n");
			
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
     * Ricerca ruoli
     *
     * Elenca i ruoli registrati
     *
     */
	@Override
    public ListaRuoli findAll(String q, Integer limit, Integer offset, FonteEnum fonte, ContestoEnum contesto) {
		IContext context = this.getContext();
		try {
			context.getLogger().info("Invocazione in corso ...");     

			AuthorizationManager.authorize(context, getAuthorizationConfig());
			context.getLogger().debug("Autorizzazione completata con successo");     
			

			HttpRequestWrapper wrap = new HttpRequestWrapper(context.getServletRequest());
			RuoliEnv rEnv = new RuoliEnv(wrap, context);
						
			int idLista = Liste.RUOLI;
			Search ricerca =  Helper.setupRicercaPaginata(q, limit, offset, idLista);
			
			if (fonte != null)
				ricerca.addFilter(idLista, Filtri.FILTRO_RUOLO_TIPOLOGIA, RuoliApiHelper.apiFonteToRegistroTipologia(fonte).toString());
			if (contesto != null)
				ricerca.addFilter(idLista, Filtri.FILTRO_RUOLO_CONTESTO, RuoliApiHelper.apiContestoToRegistroContesto(contesto).toString());


			List<org.openspcoop2.core.registry.Ruolo> lista = new ArrayList<>();
			try {
				lista = rEnv.ruoliCore.ruoliList(null, ricerca);
			} catch (Exception e) {
				throw FaultCode.NOT_FOUND.toException(e.getMessage());
			}			
			
			
			if (lista.size() == 0) {
				throw FaultCode.NOT_FOUND.toException("Nessun ruolo corrisponde ai criteri di ricerca specificati");
			}
			
			final ListaRuoli ret = Helper.costruisciListaPaginata(
					context.getServletRequest().getRequestURI(),
					offset, 
					limit, 
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
    public Ruolo get(String nome) {
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
    public void update(Ruolo body, String nome) {
		IContext context = this.getContext();
		try {
			context.getLogger().info("Invocazione in corso ...");     

			AuthorizationManager.authorize(context, getAuthorizationConfig());
			context.getLogger().debug("Autorizzazione completata con successo");
			
			if (body == null)
				throw FaultCode.RICHIESTA_NON_VALIDA.toException("Specificare un body corretto");
                        
			HttpRequestWrapper wrap = RuoliApiHelper.overrideRuoloParams(body, context);
		
			RuoliEnv rEnv = new RuoliEnv(wrap, context);
			
			org.openspcoop2.core.registry.Ruolo oldRuolo = null;
			try {
				oldRuolo = rEnv.ruoliCore.getRuolo(nome); 
			} catch (Exception e) {}
			if (oldRuolo == null) throw FaultCode.RICHIESTA_NON_VALIDA.toException("Nessun ruolo da aggiornare corrisponde al nome: " + nome);
			
			org.openspcoop2.core.registry.Ruolo ruoloNEW = RuoliApiHelper.apiRuoloToRuoloRegistro(body, rEnv.userLogin);
			ruoloNEW.setOldIDRuoloForUpdate(new IDRuolo(oldRuolo.getNome()));	// Qui nella versione della controlstation parte dal nuovo nome assumendo che siano identici.
			
			if (!rEnv.ruoliHelper.ruoloCheckData(TipoOperazione.CHANGE, oldRuolo)) {
				throw FaultCode.RICHIESTA_NON_VALIDA.toException(rEnv.pd.getMessage());
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

