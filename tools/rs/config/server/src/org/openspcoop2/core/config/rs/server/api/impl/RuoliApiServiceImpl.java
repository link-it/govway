package org.openspcoop2.core.config.rs.server.api.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.openspcoop2.core.commons.ErrorsHandlerCostant;
import org.openspcoop2.core.commons.Liste;
import org.openspcoop2.core.config.PortaApplicativa;
import org.openspcoop2.core.config.PortaDelegata;
import org.openspcoop2.core.config.ServizioApplicativo;
import org.openspcoop2.core.config.driver.DriverConfigurazioneException;
import org.openspcoop2.core.config.driver.FiltroRicercaPorteApplicative;
import org.openspcoop2.core.config.driver.FiltroRicercaPorteDelegate;
import org.openspcoop2.core.config.driver.FiltroRicercaServiziApplicativi;
import org.openspcoop2.core.config.rs.server.api.RuoliApi;
import org.openspcoop2.core.config.rs.server.config.ServerProperties;
import org.openspcoop2.core.config.rs.server.model.ContestoEnum;
import org.openspcoop2.core.config.rs.server.model.FonteEnum;
import org.openspcoop2.core.config.rs.server.model.ListaRuoli;
import org.openspcoop2.core.config.rs.server.model.Ruolo;
import org.openspcoop2.core.id.IDPortaApplicativa;
import org.openspcoop2.core.id.IDPortaDelegata;
import org.openspcoop2.core.id.IDRuolo;
import org.openspcoop2.core.id.IDServizioApplicativo;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.core.registry.Soggetto;
import org.openspcoop2.core.registry.driver.FiltroRicercaSoggetti;
import org.openspcoop2.protocol.engine.utils.DBOggettiInUsoUtils;
import org.openspcoop2.utils.jaxrs.fault.FaultCode;
import org.openspcoop2.utils.jaxrs.impl.AuthorizationConfig;
import org.openspcoop2.utils.jaxrs.impl.AuthorizationManager;
import org.openspcoop2.utils.jaxrs.impl.BaseImpl;
import org.openspcoop2.utils.jaxrs.impl.ServiceContext;
import org.openspcoop2.web.ctrlstat.core.Search;
import org.openspcoop2.web.ctrlstat.servlet.pa.PorteApplicativeCore;
import org.openspcoop2.web.ctrlstat.servlet.pd.PorteDelegateCore;
import org.openspcoop2.web.ctrlstat.servlet.ruoli.RuoliCostanti;
import org.openspcoop2.web.ctrlstat.servlet.sa.ServiziApplicativiCore;
import org.openspcoop2.web.ctrlstat.servlet.soggetti.SoggettiCore;
import org.openspcoop2.web.lib.mvc.Costanti;
import org.openspcoop2.web.lib.mvc.ServletUtils;
import org.openspcoop2.web.lib.mvc.TipoOperazione;
import org.openspcoop2.web.lib.users.dao.User;
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
    public void create(Ruolo ruolo) {
		ServiceContext context = this.getContext();
		try {
			context.getLogger().info("Invocazione in corso ...");     

			AuthorizationManager.authorize(context, getAuthorizationConfig());
			context.getLogger().debug("Autorizzazione completata con successo");     
                        
			String userLogin = context.getAuthentication().getName();
			RuoliWrapperServlet wrap = new RuoliWrapperServlet(context.getServletRequest());

		/*	Ruolo ruolo = null;
			try {
				ruolo = (Ruolo) JSONUtils.getInstance().getAsObject(((InputStream)body), Ruolo.class);
			} catch (Exception e) {
				throw FaultCode.RICHIESTA_NON_VALIDA.toException("Errore nel formato dei parametri: " + e.getMessage());
			}*/
			
			wrap.overrideParameter(RuoliCostanti.PARAMETRO_RUOLO_DESCRIZIONE, ruolo.getDescrizione());
			wrap.overrideParameter(RuoliCostanti.PARAMETRO_RUOLO_NOME, ruolo.getNome());
			wrap.overrideParameter(RuoliCostanti.PARAMETRO_RUOLO_NOME_ESTERNO, ruolo.getIdentificativoEsterno());
		
			RuoliEnv rEnv = new RuoliEnv(wrap);
			
			org.openspcoop2.core.registry.Ruolo regRuolo = null;
			try {
				regRuolo = RuoliApiHelper.apiRuoloToRuoloRegistro(ruolo, userLogin);
			} catch (Exception e) {
				throw FaultCode.RICHIESTA_NON_VALIDA.toException(e.getMessage());
			}
			
			if(rEnv.ruoliCore.existsRuolo(ruolo.getNome())){
				throw FaultCode.CONFLITTO.toException("Un ruolo con nome '" + ruolo.getNome() + "' risulta già stato registrato");
			}
			
			if (!rEnv.ruoliHelper.ruoloCheckData(TipoOperazione.ADD, null)) {
				throw FaultCode.RICHIESTA_NON_VALIDA.toException(rEnv.pd.getMessage());
			}
			
			rEnv.ruoliCore.performCreateOperation(userLogin, false, regRuolo);			
        
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
		ServiceContext context = this.getContext();
		try {
			context.getLogger().info("Invocazione in corso ...");     

			AuthorizationManager.authorize(context, getAuthorizationConfig());
			context.getLogger().debug("Autorizzazione completata con successo");     
                        
			RuoliWrapperServlet wrap = new RuoliWrapperServlet(context.getServletRequest());

			// TODO: Se creo un enviroment aggiungere anche un String superUser.
			// TODO: Qui non serve un wrapping
			String userLogin = context.getAuthentication().getName();
			RuoliEnv rEnv = new RuoliEnv(wrap);
						
			org.openspcoop2.core.registry.Ruolo regRuolo = null;
			try {
				regRuolo = rEnv.ruoliCore.getRuolo(nome);
			}catch (Exception e) {
				throw FaultCode.NOT_FOUND.toException("Nessun ruolo corrisponde al nome indicato");
			}
			
			HashMap<ErrorsHandlerCostant, List<String>> whereIsInUso = new HashMap<ErrorsHandlerCostant, List<String>>();
			boolean normalizeObjectIds = true;
			boolean ruoloInUso = rEnv.ruoliCore.isRuoloInUso(regRuolo.getNome(),whereIsInUso,normalizeObjectIds);
			
			if (ruoloInUso) {
				String msg = DBOggettiInUsoUtils.toString(new IDRuolo(regRuolo.getNome()), whereIsInUso, true, "\n");
				throw FaultCode.RICHIESTA_NON_VALIDA.toException(msg);

			}
			
			rEnv.ruoliCore.performDeleteOperation(userLogin, false, regRuolo);			
	
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
		ServiceContext context = this.getContext();
		try {
			context.getLogger().info("Invocazione in corso ...");     

			AuthorizationManager.authorize(context, getAuthorizationConfig());
			context.getLogger().debug("Autorizzazione completata con successo");     
			
			// Devo impostare nella sessione l'utente per poter utilizzare i metodi helpers e del core.
			//User u = new User()
					
			
			RuoliWrapperServlet wrap = new RuoliWrapperServlet(context.getServletRequest());
			RuoliEnv rEnv = new RuoliEnv(wrap);
					 
			User u = rEnv.utentiCore.getUser(context.getAuthentication().getName());
			ServletUtils.setUserIntoSession(context.getServletRequest().getSession(), u);
			//context.getServletRequest().getSession().setAttribute(Costanti.SESSION_ATTRIBUTE_USER, u);
			

			//wrap.getSessio
			
			if (offset != null)
				wrap.overrideParameter(Costanti.SEARCH_INDEX, offset);
			if (limit != null)
				wrap.overrideParameter(Costanti.SEARCH_PAGE_SIZE, limit);
			if (q != null)
				wrap.overrideParameter(Costanti.SEARCH, q);

			int idLista = Liste.RUOLI;
			Search ricerca =  new Search();

			// TODO: Controllare tramite debugger la sezione PARAMETER_FILTER_NAME
			ricerca = rEnv.ruoliHelper.checkSearchParameters(idLista, ricerca);
			List<org.openspcoop2.core.registry.Ruolo> lista = new ArrayList<>();
			try {
				rEnv.ruoliCore.ruoliList(null, ricerca);
			} catch (Exception e) { }
			
			
			if (lista.size() == 0) {
				throw FaultCode.NOT_FOUND.toException("Nessun ruolo corrisponde ai criteri di ricerca specificati");
			}
			
			ListaRuoli ret = new ListaRuoli();
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
		ServiceContext context = this.getContext();
		try {
			context.getLogger().info("Invocazione in corso ...");     

			AuthorizationManager.authorize(context, getAuthorizationConfig());
			context.getLogger().debug("Autorizzazione completata con successo");     
                        
			RuoliEnv rEnv = new RuoliEnv(context.getServletRequest());
			
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
		ServiceContext context = this.getContext();
		try {
			context.getLogger().info("Invocazione in corso ...");     

			AuthorizationManager.authorize(context, getAuthorizationConfig());
			context.getLogger().debug("Autorizzazione completata con successo");     
                        
			RuoliWrapperServlet wrap = new RuoliWrapperServlet(context.getServletRequest());
			
			wrap.overrideParameter(RuoliCostanti.PARAMETRO_RUOLO_DESCRIZIONE, body.getDescrizione());
			wrap.overrideParameter(RuoliCostanti.PARAMETRO_RUOLO_NOME, body.getNome());
			wrap.overrideParameter(RuoliCostanti.PARAMETRO_RUOLO_NOME_ESTERNO, body.getIdentificativoEsterno());
			
			String superUser = context.getAuthentication().getName();
			RuoliEnv rEnv = new RuoliEnv(wrap);
			
			org.openspcoop2.core.registry.Ruolo oldRuolo = null;
			try {
				oldRuolo = rEnv.ruoliCore.getRuolo(nome); 
			} catch (Exception e) {}
			if (oldRuolo == null) throw FaultCode.RICHIESTA_NON_VALIDA.toException("Nessun ruolo da aggiornare corrisponde al nome: " + nome);
			
			org.openspcoop2.core.registry.Ruolo ruoloNEW = RuoliApiHelper.apiRuoloToRuoloRegistro(body, superUser);
			ruoloNEW.setOldIDRuoloForUpdate(new IDRuolo(oldRuolo.getNome()));	// Qui nella versione della controlstation parte dal nuovo nome assumendo che siano identici.
			
			if (!rEnv.ruoliHelper.ruoloCheckData(TipoOperazione.CHANGE, oldRuolo)) {
				throw FaultCode.RICHIESTA_NON_VALIDA.toException(rEnv.pd.getMessage());
			}

			List<Object> listOggettiDaAggiornare = new ArrayList<>();
			
			listOggettiDaAggiornare.add(ruoloNEW);
			
			if(oldRuolo.getNome().equals(ruoloNEW.getNome())==false) {	//Check in debug sul change name TODO
				
				// e' stato modificato il nome
				IDRuolo oldIdRuolo = ruoloNEW.getOldIDRuoloForUpdate();
				oldIdRuolo.setNome(body.getNome());
				
		
				// Cerco se utilizzato in soggetti 
				SoggettiCore soggettiCore = new SoggettiCore(rEnv.ruoliCore);
				FiltroRicercaSoggetti filtroRicercaSoggetti = new FiltroRicercaSoggetti();
				filtroRicercaSoggetti.setIdRuolo(oldIdRuolo);
				List<IDSoggetto> listSoggetti = soggettiCore.getAllIdSoggettiRegistro(filtroRicercaSoggetti);
				if(listSoggetti!=null && listSoggetti.size()>0){
					for (IDSoggetto idSoggettoWithRuolo : listSoggetti) {
						Soggetto soggettoWithRuolo = soggettiCore.getSoggettoRegistro(idSoggettoWithRuolo);
						if(soggettoWithRuolo.getRuoli()!=null){
							for (org.openspcoop2.core.registry.RuoloSoggetto ruoloSoggetto : soggettoWithRuolo.getRuoli().getRuoloList()) {
								if(ruoloSoggetto.getNome().equals(oldIdRuolo.getNome())){
									ruoloSoggetto.setNome(ruoloNEW.getNome());
								}
							}
						}
						listOggettiDaAggiornare.add(soggettoWithRuolo);
					}
				}				
				
				// Cerco se utilizzato in servizi applicativi
				// TODO: Vedere con andrea come fattorizzare.
				ServiziApplicativiCore saCore = new ServiziApplicativiCore(rEnv.ruoliCore);
				FiltroRicercaServiziApplicativi filtroRicercaSA = new FiltroRicercaServiziApplicativi();
				filtroRicercaSA.setIdRuolo(oldIdRuolo);
				List<IDServizioApplicativo> listSA = saCore.getAllIdServiziApplicativi(filtroRicercaSA);
				if(listSA!=null && listSA.size()>0){
					for (IDServizioApplicativo idServizioApplicativo : listSA) {
						ServizioApplicativo sa = saCore.getServizioApplicativo(idServizioApplicativo);
						if(sa.getInvocazionePorta()!=null && sa.getInvocazionePorta().getRuoli()!=null){
							for (org.openspcoop2.core.config.Ruolo ruoloConfig : sa.getInvocazionePorta().getRuoli().getRuoloList()) {
								if(ruoloConfig.getNome().equals(oldIdRuolo.getNome())){
									ruoloConfig.setNome(ruoloNEW.getNome());
								}
							}
						}
						listOggettiDaAggiornare.add(sa);
					}
				}
				
				
				// Cerco se utilizzato in porte delegate
				PorteDelegateCore pdCore = new PorteDelegateCore(rEnv.ruoliCore);
				FiltroRicercaPorteDelegate filtroRicercaPD = new FiltroRicercaPorteDelegate();
				filtroRicercaPD.setIdRuolo(oldIdRuolo);
				List<IDPortaDelegata> listPD = pdCore.getAllIdPorteDelegate(filtroRicercaPD);
				if(listPD!=null && listPD.size()>0){
					for (IDPortaDelegata idPD : listPD) {
						PortaDelegata portaDelegata = pdCore.getPortaDelegata(idPD);
						if(portaDelegata.getRuoli()!=null){
							for (org.openspcoop2.core.config.Ruolo ruoloConfig : portaDelegata.getRuoli().getRuoloList()) {
								if(ruoloConfig.getNome().equals(oldIdRuolo.getNome())){
									ruoloConfig.setNome(ruoloNEW.getNome());
								}
							}
						}
						listOggettiDaAggiornare.add(portaDelegata);
					}
				}
				
				
				
				// Cerco se utilizzato in porte applicative
				PorteApplicativeCore paCore = new PorteApplicativeCore(rEnv.ruoliCore);
				FiltroRicercaPorteApplicative filtroRicercaPA = new FiltroRicercaPorteApplicative();
				filtroRicercaPA.setIdRuolo(oldIdRuolo);
				List<IDPortaApplicativa> listPA = paCore.getAllIdPorteApplicative(filtroRicercaPA);
				if(listPA!=null && listPA.size()>0){
					for (IDPortaApplicativa idPA : listPA) {
						PortaApplicativa portaApplicativa = paCore.getPortaApplicativa(idPA);
						if(portaApplicativa.getRuoli()!=null){
							for (org.openspcoop2.core.config.Ruolo ruoloConfig : portaApplicativa.getRuoli().getRuoloList()) {
								if(ruoloConfig.getNome().equals(oldIdRuolo.getNome())){
									ruoloConfig.setNome(ruoloNEW.getNome());
								}
							}
						}
						listOggettiDaAggiornare.add(portaApplicativa);
					}
				}
				
			}
			
			
			rEnv.ruoliCore.performUpdateOperation(superUser, false, listOggettiDaAggiornare.toArray());		
        
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

