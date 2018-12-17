package org.openspcoop2.core.config.rs.server.api.impl.applicativi;

import java.util.ArrayList;
import java.util.List;

import org.openspcoop2.core.commons.Filtri;
import org.openspcoop2.core.commons.Liste;
import org.openspcoop2.core.config.ServizioApplicativo;
import org.openspcoop2.core.config.rs.server.api.ApplicativiApi;
import org.openspcoop2.core.config.rs.server.api.impl.Helper;
import org.openspcoop2.core.config.rs.server.api.impl.HttpRequestWrapper;
import org.openspcoop2.core.config.rs.server.config.ServerProperties;
import org.openspcoop2.core.config.rs.server.model.Applicativo;
import org.openspcoop2.core.config.rs.server.model.ListaApplicativi;
import org.openspcoop2.core.config.rs.server.model.ProfiloEnum;
import org.openspcoop2.core.id.IDServizioApplicativo;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.core.registry.driver.FiltroRicercaSoggetti;
import org.openspcoop2.protocol.engine.ProtocolFactoryManager;
import org.openspcoop2.utils.jaxrs.fault.FaultCode;
import org.openspcoop2.utils.jaxrs.impl.AuthorizationConfig;
import org.openspcoop2.utils.jaxrs.impl.AuthorizationManager;
import org.openspcoop2.utils.jaxrs.impl.BaseImpl;
import org.openspcoop2.utils.jaxrs.impl.ServiceContext;
import org.openspcoop2.web.ctrlstat.core.Search;
import org.openspcoop2.web.ctrlstat.plugins.ExtendedConnettore;
import org.openspcoop2.web.ctrlstat.servlet.sa.ServiziApplicativiCostanti;
import org.openspcoop2.web.ctrlstat.servlet.sa.ServiziApplicativiGeneralInfo;
import org.openspcoop2.web.ctrlstat.servlet.sa.ServiziApplicativiUtilities;
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
     */
	@Override
    public void create(Applicativo body, ProfiloEnum profilo, String soggetto) {
		ServiceContext context = this.getContext();
		try {
			context.getLogger().info("Invocazione in corso ...");     

			AuthorizationManager.authorize(context, getAuthorizationConfig());
			context.getLogger().debug("Autorizzazione completata con successo");
			
			if (body == null)
				throw FaultCode.RICHIESTA_NON_VALIDA.toException("Specificare un body");
                
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
				throw FaultCode.CONFLITTO.toException( //TODO Cambiare codice errore in CONFLITTO
						"Il Servizio Applicativo " + sa.getNome() + " è già stato registrato per il soggetto scelto."
				);
			}
					
			ApplicativiApiHelper.overrideSAParameters(wrap, sa);
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
				throw FaultCode.RICHIESTA_NON_VALIDA.toException(env.pd.getMessage());
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
    public void delete(String nome, ProfiloEnum profilo, String soggetto) {
		ServiceContext context = this.getContext();
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
			
			
			try {
				idServizioApplicativo.setNome(nome);
				sa = env.saCore.getServizioApplicativo(idServizioApplicativo);
			} catch (Exception e) {
				throw FaultCode.NOT_FOUND.toException("Servizio applicativo con nome: " + nome + " non trovato.");
			}
			
			StringBuffer inUsoMessage = new StringBuffer();
			ServiziApplicativiUtilities.deleteServizioApplicativo(sa, context.getAuthentication().getName(), env.saCore, env.saHelper, inUsoMessage, "\n");
			
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
                        
			ApplicativiEnv env = new ApplicativiEnv(context.getServletRequest(), profilo, soggetto, context);
			
			int idLista = Liste.SERVIZIO_APPLICATIVO;

			Search ricerca = Helper.setupRicercaPaginata(q, limit, offset, idLista);
			
			if (profilo != null) {
				ricerca.addFilter(idLista, Filtri.FILTRO_PROTOCOLLO, Helper.tipoProtocolloFromProfilo.get(profilo));
			}
			
			// Recupero tutti i soggetti che soddisfano i criteri di ricerca, in più, controllo che
			// il nome del soggetto dei serviziApplicativi ottenuti rispetti il criterio di ricerca.
			List<ServizioApplicativo> saLista = new ArrayList<ServizioApplicativo>();
			try {
				saLista = env.saCore.soggettiServizioApplicativoList(null, ricerca);
			} catch (Exception e) {	}
			
			if (soggetto != null && soggetto.length() > 0) {
				try {
					FiltroRicercaSoggetti filtro = new FiltroRicercaSoggetti();
					filtro.setNome(soggetto);
					List<IDSoggetto> soggettiFiltrati = env.soggettiCore.getAllIdSoggettiRegistro(filtro);
					
					saLista.removeIf( s -> !soggettiFiltrati.contains(new IDSoggetto(s.getTipoSoggettoProprietario(),s.getNomeSoggettoProprietario())) );
				} catch (Exception e) {
					saLista.clear();
					throw FaultCode.NOT_FOUND.toException("Il nome soggetto impostato nei criteri di ricerca non esiste");
				}
			}

			final ListaApplicativi ret = Helper.costruisciListaPaginata(
					context.getServletRequest().getRequestURI(),
					offset, 
					limit, 
					ricerca.getNumEntries(idLista), 
					ListaApplicativi.class
				); 
				
			saLista.forEach( sa -> ret.addItemsItem(ApplicativiApiHelper.servizioApplicativoToApplicativoItem(sa)));	
		
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
    public void update(Applicativo body, String nome, ProfiloEnum profilo, String soggetto) {
		ServiceContext context = this.getContext();
		try {
			context.getLogger().info("Invocazione in corso ...");     

			AuthorizationManager.authorize(context, getAuthorizationConfig());
			context.getLogger().debug("Autorizzazione completata con successo");     
			
			if (body == null)
				throw FaultCode.RICHIESTA_NON_VALIDA.toException("Specificare un body");
			
			Applicativo applicativo = body;
			try{
				applicativo.setCredenziali(ApplicativiApiHelper.translateCredenzialiApplicativo(applicativo));
			}catch(Throwable e) {
				throw FaultCode.RICHIESTA_NON_VALIDA.toException(e);
			}
			
			final HttpRequestWrapper wrap = new HttpRequestWrapper(context.getServletRequest());
			final ApplicativiEnv env = new ApplicativiEnv(wrap, profilo, soggetto, context);
			soggetto = env.idSoggetto.getNome();
			
			ServizioApplicativo oldSa = null;
			try {
				oldSa = ApplicativiApiHelper.getServizioApplicativo(nome, env.idSoggetto.getNome(), env.tipo_protocollo, env.saCore);
			} catch (Exception e) {	}
			
			if (oldSa == null)
				throw FaultCode.NOT_FOUND.toException("Nessun applicativo corrispondente al nome e soggetto indicati trovato.");
			
			final ServizioApplicativo tmpSa = ApplicativiApiHelper.applicativoToServizioApplicativo(applicativo, env.tipo_protocollo, soggetto, env.stationCore);
			final ServizioApplicativo newSa = ApplicativiApiHelper.getServizioApplicativo(nome, soggetto, env.tipo_protocollo, env.saCore);
			
			newSa.setNome(tmpSa.getNome());
			newSa.setIdSoggetto(tmpSa.getIdSoggetto());
			newSa.setNomeSoggettoProprietario(tmpSa.getNomeSoggettoProprietario());
			newSa.setTipoSoggettoProprietario(tmpSa.getTipoSoggettoProprietario());		
			newSa.getInvocazionePorta().setCredenzialiList(tmpSa.getInvocazionePorta().getCredenzialiList());
			
			if (!oldSa.getNome().equals(newSa.getNome())) {
				throw FaultCode.RICHIESTA_NON_VALIDA.toException("Non è possibile modificare il nome del servizio applicativo");
			}
					
			IDServizioApplicativo oldID = new IDServizioApplicativo();
			oldID.setIdSoggettoProprietario(ApplicativiApiHelper.getIDSoggetto(oldSa.getNomeSoggettoProprietario(), env.tipo_protocollo));
			oldID.setNome(oldSa.getNome());
			
			newSa.setOldIDServizioApplicativoForUpdate(oldID);
			
			List<ExtendedConnettore> listExtendedConnettore = null;	// Non serve alla checkData perchè da Api, gli applicativi sono sempre fruitori
			
			ApplicativiApiHelper.overrideSAParameters(wrap, newSa);
			wrap.overrideParameter(ServiziApplicativiCostanti.PARAMETRO_SERVIZI_APPLICATIVI_PROTOCOLLO, env.tipo_protocollo);
			
			if (! env.saHelper.servizioApplicativoCheckData(
					TipoOperazione.CHANGE,
					null,
					oldSa.getId(),
					newSa.getTipologiaFruizione(),
					newSa.getTipologiaErogazione(),
					listExtendedConnettore, null
				)) {
				throw FaultCode.RICHIESTA_NON_VALIDA.toException(env.pd.getMessage());
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

