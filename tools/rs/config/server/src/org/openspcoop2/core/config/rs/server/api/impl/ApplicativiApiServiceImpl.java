package org.openspcoop2.core.config.rs.server.api.impl;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import org.openspcoop2.core.commons.ErrorsHandlerCostant;
import org.openspcoop2.core.commons.Filtri;
import org.openspcoop2.core.commons.Liste;
import org.openspcoop2.core.config.Credenziali;
import org.openspcoop2.core.config.ServizioApplicativo;
import org.openspcoop2.core.config.rs.server.api.ApplicativiApi;
import org.openspcoop2.core.config.rs.server.config.ServerProperties;
import org.openspcoop2.core.config.rs.server.model.Applicativo;
import org.openspcoop2.core.config.rs.server.model.ListaApplicativi;
import org.openspcoop2.core.config.rs.server.model.ProfiloEnum;
import org.openspcoop2.core.id.IDPortaDelegata;
import org.openspcoop2.core.id.IDServizioApplicativo;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.core.registry.driver.FiltroRicercaSoggetti;
import org.openspcoop2.protocol.engine.utils.DBOggettiInUsoUtils;
import org.openspcoop2.utils.jaxrs.fault.FaultCode;
import org.openspcoop2.utils.jaxrs.impl.AuthorizationConfig;
import org.openspcoop2.utils.jaxrs.impl.AuthorizationManager;
import org.openspcoop2.utils.jaxrs.impl.BaseImpl;
import org.openspcoop2.utils.jaxrs.impl.ServiceContext;
import org.openspcoop2.utils.json.JSONUtils;
import org.openspcoop2.web.ctrlstat.core.Search;
import org.openspcoop2.web.lib.mvc.TipoOperazione;
/**
 * GovWay Config API
 *
 * <p>Servizi per la configurazione di GovWay
 *
 */
public class ApplicativiApiServiceImpl extends BaseImpl implements ApplicativiApi {

	public ApplicativiApiServiceImpl(){
		super(org.slf4j.LoggerFactory.getLogger(ApplicativiApiServiceImpl.class));
	}
	
	
	// TODO: TOASK Tutte le implementazioni dell'API applicativi utilizzano la stessa
	// configurazione di autenticazione? Posso in futuro fare una superclasse?
	
	private AuthorizationConfig getAuthorizationConfig() throws Exception{
		return new AuthorizationConfig(ServerProperties.getInstance().getProperties());
	}

    /**
     * Creazione di un applicativo
     *
     * Questa operazione consente di creare un applicativo associato ad un soggetto interno
     * 
     *  {   "profilo": "APIGateway", 
     *      "soggetto": "Ente",     
     *      "nome": "Applicativo",     
     *      "modalita_accesso": "http-basic",     
     *      "credenziali": {"username": "username",         "password": "password"     }
     *   }
     *
     */
	@Override
    public void create(Object body, ProfiloEnum profilo, String soggetto) {
		ServiceContext context = this.getContext();
		try {
			context.getLogger().info("Invocazione in corso ...");     

			AuthorizationManager.authorize(context, getAuthorizationConfig());
			context.getLogger().debug("Autorizzazione completata con successo");     
                        
			Applicativo applicativo = null;
			try{
				applicativo = JSONUtils.getInstance().getAsObject(((InputStream)body), Applicativo.class);
			}catch(Throwable e) {
				throw FaultCode.RICHIESTA_NON_VALIDA.toException(e);
			}
			applicativo.setCredenziali(ApplicativiApiHelper.translateCredenzialiApplicativo(applicativo));
			soggetto = ApplicativiApiHelper.getSoggettoOrDefault(soggetto,profilo);
			
			String superUser =  context.getAuthentication().getName();
			
			ApplicativiEnv aEnv = new ApplicativiEnv(profilo); 					
			
			ServizioApplicativo sa = ApplicativiApiHelper.applicativoToServizioApplicativo(applicativo, aEnv.tipo_protocollo, soggetto);
			
			ApplicativiApiHelper.checkServizioApplicativoFormat(sa, aEnv.saCore, aEnv.soggettiCore);
			ApplicativiApiHelper.checkApplicativoDuplicati(sa, aEnv.saCore);
			ApplicativiApiHelper.checkNoDuplicateCred(
					sa,
					ApplicativiApiHelper.getApplicativiStesseCredenziali(sa, aEnv.saCore),
					aEnv.soggettiCore,
					TipoOperazione.ADD
				);
				
			aEnv.saCore.performCreateOperation(superUser, false, sa);
			
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
    public void delete(String nome, ProfiloEnum profilo, String soggetto) {
		ServiceContext context = this.getContext();
		try {
			context.getLogger().info("Invocazione in corso ...");     

			AuthorizationManager.authorize(context, getAuthorizationConfig());
			context.getLogger().debug("Autorizzazione completata con successo");     
                        
			soggetto = ApplicativiApiHelper.getSoggettoOrDefault(soggetto,profilo);

			ApplicativiEnv aEnv = new ApplicativiEnv(profilo); 							
			
			IDServizioApplicativo idServizioApplicativo = null;
			ServizioApplicativo sa = null;
			try {
				idServizioApplicativo= new IDServizioApplicativo();
				idServizioApplicativo.setIdSoggettoProprietario(ApplicativiApiHelper.getIDSoggetto(soggetto, aEnv.tipo_protocollo));
				
			} catch (Exception e) {
				throw FaultCode.NOT_FOUND.toException("Soggetto non trovato.");
			}
			
			
			try {
				idServizioApplicativo.setNome(nome);
				sa = aEnv.saCore.getServizioApplicativo(idServizioApplicativo);
			} catch (Exception e) {
				throw FaultCode.NOT_FOUND.toException("Servizio applicativo non trovato.");
			}
			
			// Controllo che il sil non sia in uso
			Map<ErrorsHandlerCostant, List<String>> whereIsInUso = new Hashtable<ErrorsHandlerCostant, List<String>>();
			boolean normalizeObjectIds = true;
			boolean saInUso  = aEnv.saCore.isServizioApplicativoInUso(idServizioApplicativo, whereIsInUso, aEnv.saCore.isRegistroServiziLocale(), normalizeObjectIds);
			
			if (saInUso) {
				String msg = DBOggettiInUsoUtils.toString(idServizioApplicativo, whereIsInUso, true, "\n",normalizeObjectIds);
				throw FaultCode.RICHIESTA_NON_VALIDA.toException("Il servizio applicativo è in uso: " + msg);

			} else {
				aEnv.saCore.performDeleteOperation(context.getAuthentication().getName(), false, sa);
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
    public ListaApplicativi findAll(ProfiloEnum profilo, String soggetto, String q, Integer limit, Integer offset, String ruolo) {
		ServiceContext context = this.getContext();
		try {
			context.getLogger().info("Invocazione in corso ...");     

			AuthorizationManager.authorize(context, getAuthorizationConfig());
			context.getLogger().debug("Autorizzazione completata con successo");     
                        
			ApplicativiEnv aEnv = new ApplicativiEnv(profilo);
				
			int idLista = Liste.SERVIZIO_APPLICATIVO;

			Search ricerca = new Search();
			
			if (limit != null)
				ricerca.setPageSize(idLista, limit);
			if (offset != null)
				ricerca.setIndexIniziale(idLista, offset);
			
			if (q == null) q = "";
			
			q = q.trim();
			if (q.equals("")) {
				ricerca.setSearchString(idLista, org.openspcoop2.core.constants.Costanti.SESSION_ATTRIBUTE_VALUE_RICERCA_UNDEFINED);
			} else {
				ricerca.setSearchString(idLista, q);
			}
			
			if (profilo != null) {
				String modalita = profilo!=null ? profilo.toString() : null;
				ricerca.addFilter(idLista, Filtri.FILTRO_PROTOCOLLO, ApplicativiApiHelper.tipoProtocolloFromModalita.get(modalita));
			}
		
			// Recupero tutti i soggetti che soddisfano i criteri di ricerca, in più, controllo che
			// il nome del soggetto dei serviziApplicativi ottenuti rispetti il criterio di ricerca.
			List<ServizioApplicativo> saLista = new ArrayList<ServizioApplicativo>();
			try {
				saLista = aEnv.saCore.soggettiServizioApplicativoList(null, ricerca);
			} catch (Exception e) {	}
			
			
			if (soggetto != null && soggetto.length() > 0) {
				try {
					FiltroRicercaSoggetti filtro = new FiltroRicercaSoggetti();
					filtro.setNome(soggetto);
					List<IDSoggetto> soggettiFiltrati = aEnv.soggettiCore.getAllIdSoggettiRegistro(filtro);
					
					saLista.removeIf( s -> !soggettiFiltrati.contains(new IDSoggetto(s.getTipoSoggettoProprietario(),s.getNomeSoggettoProprietario())) );
				} catch (Exception e) {
					saLista.clear();
					throw FaultCode.NOT_FOUND.toException("Il nome soggetto impostato nei criteri di ricerca non esiste");
				}
			}


			if (saLista.size() == 0) {
				throw FaultCode.NOT_FOUND.toException("Nessun applicativo rispetta i criteri di ricerca.");
			}
		
			ListaApplicativi ret = new ListaApplicativi();
			saLista.forEach( sa -> ret.addItemsItem(ApplicativiApiHelper.servizioApplicativoToApplicativoItem(sa)));	
			
			/* TODO: Devo popolare a mano. Estendere la lista paginata in modo che sia capace di farlo da solo. 
			 * {"numRisultati":null,"numPagine":null,"risultatiPerPagina":null,"pagina":null,"prossimiRisultati":null,"risultati": ... }
			 */
			
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
     * Restituisce il dettaglio di un applicativo
     *
     * Questa operazione consente di ottenere il dettaglio di un applicativo identificato dal nome e dal soggetto di riferimento
     *
     */
	@Override
    public Applicativo get(String nome, ProfiloEnum profilo, String soggetto) {
		ServiceContext context = this.getContext();
		try {
			context.getLogger().info("Invocazione in corso ...");     

			AuthorizationManager.authorize(context, getAuthorizationConfig());
			context.getLogger().debug("Autorizzazione completata con successo");     
                        
			soggetto = ApplicativiApiHelper.getSoggettoOrDefault(soggetto,profilo);
			
			ApplicativiEnv aEnv = new ApplicativiEnv(profilo);
	
			ServizioApplicativo sa = null;			
			try {
				IDServizioApplicativo idServizioApplicativo = new IDServizioApplicativo();
				idServizioApplicativo.setIdSoggettoProprietario(ApplicativiApiHelper.getIDSoggetto(soggetto, aEnv.tipo_protocollo));
				idServizioApplicativo.setNome(nome);
				sa = aEnv.saCore.getServizioApplicativo(idServizioApplicativo);
			} catch ( Exception e) {
				throw FaultCode.NOT_FOUND.toException("Servizio applicativo non trovato.");
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
     * TODO: non posso modificare il nome.
     */
	@Override
    public void update(Object body, String nome, ProfiloEnum profilo, String soggetto) {
		ServiceContext context = this.getContext();
		try {
			context.getLogger().info("Invocazione in corso ...");     

			AuthorizationManager.authorize(context, getAuthorizationConfig());
			context.getLogger().debug("Autorizzazione completata con successo");     
                        
			soggetto = ApplicativiApiHelper.getSoggettoOrDefault(soggetto,profilo);
			
			Applicativo applicativo = null;
			try{
				applicativo = JSONUtils.getInstance().getAsObject(((InputStream)body), Applicativo.class);
			}catch(Throwable e) {
				throw FaultCode.RICHIESTA_NON_VALIDA.toException(e);
			}
			applicativo.setCredenziali(ApplicativiApiHelper.translateCredenzialiApplicativo(applicativo));
	
			ApplicativiEnv aEnv = new ApplicativiEnv(profilo);
			
			ServizioApplicativo oldSa = ApplicativiApiHelper.getServizioApplicativo(nome, soggetto, aEnv.tipo_protocollo, aEnv.saCore);
			ServizioApplicativo tmpSa = ApplicativiApiHelper.applicativoToServizioApplicativo(applicativo, aEnv.tipo_protocollo, soggetto);
			
			ServizioApplicativo newSa = ApplicativiApiHelper.getServizioApplicativo(nome, soggetto, aEnv.tipo_protocollo, aEnv.saCore);
			newSa.setNome(tmpSa.getNome());
			newSa.setIdSoggetto(tmpSa.getIdSoggetto());
			newSa.setNomeSoggettoProprietario(tmpSa.getNomeSoggettoProprietario());
			newSa.setTipoSoggettoProprietario(tmpSa.getTipoSoggettoProprietario());
			newSa.setInvocazionePorta(tmpSa.getInvocazionePorta());
		
			
			ApplicativiApiHelper.checkServizioApplicativoFormat(newSa, aEnv.saCore, aEnv.soggettiCore);
			
			Credenziali oldCred = oldSa.getInvocazionePorta().getCredenziali(0);
			Credenziali newCred = newSa.getInvocazionePorta().getCredenziali(0);
			
			// Qui non utilizziamo equals, perchè abbiamo a che fare con un enum e il risultato è identico, inoltre
			// in caso di nessuna autenticazione getTipo sarebbe null e dovremmo fare check multipli per il controllo di cambio credenziali.
			if (oldCred.getTipo() != newCred.getTipo()) {
				List<IDPortaDelegata> porte = ApplicativiApiHelper.getIdPorteDelegate(oldSa,aEnv.porteDelegateCore);
				
				if(porte != null && porte.size() > 0) {
					throw FaultCode.RICHIESTA_NON_VALIDA.toException(
							"Non è possibile modificare il tipo di credenziali poichè l'applicativo viene utilizzato all'interno del controllo degli accessi di " +
							porte.size()+" configurazioni di fruizione di servizio"
					);
				}
			}
			
			List<ServizioApplicativo> saConflicts = ApplicativiApiHelper.getApplicativiStesseCredenziali(newSa, aEnv.saCore);
			ApplicativiApiHelper.checkNoDuplicateCred(oldSa, saConflicts, aEnv.soggettiCore, TipoOperazione.CHANGE);
			
			// Se vogliamo cambiare il tipo del soggetto controlliamo che il servizio applicativo non sia in uso.
			if(!oldSa.getTipoSoggettoProprietario().equals(newSa.getTipoSoggettoProprietario())) {
				ApplicativiApiHelper.checkServizioApplicativoInUso(oldSa, aEnv.soggettiCore);
			}
			
			IDServizioApplicativo oldID = new IDServizioApplicativo();
			oldID.setIdSoggettoProprietario(ApplicativiApiHelper.getIDSoggetto(oldSa.getNomeSoggettoProprietario(), aEnv.tipo_protocollo));
			oldID.setNome(oldSa.getNome());
			
			newSa.setOldIDServizioApplicativoForUpdate(oldID);
			
			String userLogin = context.getAuthentication().getName();
			aEnv.saCore.performUpdateOperation(userLogin, false, newSa);
					
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

