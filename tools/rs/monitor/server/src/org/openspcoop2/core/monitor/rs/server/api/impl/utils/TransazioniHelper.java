package org.openspcoop2.core.monitor.rs.server.api.impl.utils;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.apache.commons.lang3.BooleanUtils;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.core.monitor.rs.server.config.DBManager;
import org.openspcoop2.core.monitor.rs.server.config.LoggerProperties;
import org.openspcoop2.core.monitor.rs.server.config.ServerProperties;
import org.openspcoop2.core.monitor.rs.server.model.FiltroApiBase;
import org.openspcoop2.core.monitor.rs.server.model.FiltroEsito;
import org.openspcoop2.core.monitor.rs.server.model.FiltroFruizione;
import org.openspcoop2.core.monitor.rs.server.model.FiltroMittenteErogazione;
import org.openspcoop2.core.monitor.rs.server.model.FiltroMittenteErogazioneApplicativo;
import org.openspcoop2.core.monitor.rs.server.model.FiltroMittenteErogazioneSoggetto;
import org.openspcoop2.core.monitor.rs.server.model.FiltroMittenteErogazioneTokenClaim;
import org.openspcoop2.core.monitor.rs.server.model.FiltroMittenteFruizione;
import org.openspcoop2.core.monitor.rs.server.model.FiltroMittenteFruizioneApplicativo;
import org.openspcoop2.core.monitor.rs.server.model.FiltroMittenteFruizioneTokenClaim;
import org.openspcoop2.core.monitor.rs.server.model.FiltroMittenteIdApplicativo;
import org.openspcoop2.core.monitor.rs.server.model.ListaTransazioni;
import org.openspcoop2.core.monitor.rs.server.model.RicercaBaseTransazione;
import org.openspcoop2.core.monitor.rs.server.model.RicercaIntervalloTemporale;
import org.openspcoop2.generic_project.utils.ServiceManagerProperties;
import org.openspcoop2.message.constants.ServiceBinding;
import org.openspcoop2.monitor.engine.condition.EsitoUtils;
import org.openspcoop2.protocol.sdk.config.IProtocolConfiguration;
import org.openspcoop2.utils.UtilsException;
import org.openspcoop2.utils.service.beans.FiltroRicercaId;
import org.openspcoop2.utils.service.beans.utils.BaseHelper;
import org.openspcoop2.utils.service.beans.utils.ListaUtils;
import org.openspcoop2.utils.service.fault.jaxrs.FaultCode;
import org.openspcoop2.web.monitor.core.constants.CaseSensitiveMatch;
import org.openspcoop2.web.monitor.core.constants.Costanti;
import org.openspcoop2.web.monitor.core.constants.TipoMatch;
import org.openspcoop2.web.monitor.transazioni.bean.TransazioneBean;
import org.openspcoop2.web.monitor.transazioni.bean.TransazioniSearchForm;
import org.openspcoop2.web.monitor.transazioni.dao.TransazioniService;

public class TransazioniHelper {
	
	public static final void overrideFiltroApiBase(FiltroApiBase filtro_api, String azione, TransazioniSearchForm search, MonitoraggioEnv env)  {
		if (filtro_api == null) return;		
		
		if (filtro_api.getTipo() == null) {
			try {
				IProtocolConfiguration protocolConf = env.protocolFactoryMgr.getProtocolFactoryByName(env.tipo_protocollo).createProtocolConfiguration();
				ServiceBinding defaultBinding = protocolConf.getDefaultServiceBindingConfiguration(null).getDefaultBinding();
				filtro_api.setTipo(protocolConf.getTipoServizioDefault(defaultBinding));
			} catch (Exception e) {
				throw FaultCode.ERRORE_INTERNO.toException("Impossibile determinare il tipo del servizio: " + e.getMessage() );
			}
			
		}
		
		String uri = ReportisticaHelper.buildNomeServizioForOverride(filtro_api.getNome(), filtro_api.getTipo(), filtro_api.getVersione(), Optional.empty());
		search.setNomeServizio(uri);
		search.setNomeAzione(azione);
	}
	
	
	public static final void overrideFiltroFruizione(FiltroFruizione filtro, String azione, TransazioniSearchForm search, MonitoraggioEnv env) {
		if (filtro == null) return;
		
		overrideFiltroApiBase(filtro, azione, search, env);
		if (filtro.getErogatore() != null)
			search.setTipoNomeDestinatario(new IDSoggetto(env.soggetto.getTipo(), filtro.getErogatore()).toString());
	}
	
	
	public static final void overrideFiltroRicercaId(FiltroRicercaId filtro, TransazioniSearchForm search, MonitoraggioEnv env) {
		if (filtro == null) return;
		
		search.setMittenteMatchingType((BooleanUtils.isTrue(filtro.isRicercaEsatta()) ? TipoMatch.EQUALS : TipoMatch.LIKE ).toString());
		search.setMittenteCaseSensitiveType( (BooleanUtils.isTrue(filtro.isCaseSensitive()) ? CaseSensitiveMatch.SENSITIVE : CaseSensitiveMatch.INSENSITIVE).toString() );
		search.setValoreRiconoscimento(filtro.getId());
		search.setRiconoscimento(Costanti.VALUE_TIPO_RICONOSCIMENTO_IDENTIFICATIVO_AUTENTICATO);
	}
	
	
	public static final void overrideFiltroMittenteIdApplicativo(FiltroMittenteIdApplicativo filtro, TransazioniSearchForm search, MonitoraggioEnv env) {
		if (filtro == null) return;
		overrideFiltroRicercaId(filtro, search, env);
		search.setAutenticazione(Enums.toTipoAutenticazione.get(filtro.getAutenticazione()).toString());
	}
	
	
	@SuppressWarnings("unchecked")
	public static final void overrideFiltroEsito(FiltroEsito filtro, TransazioniSearchForm search, MonitoraggioEnv env) {
		if (filtro == null) return;
	
		if(filtro != null && filtro.getTipo()!=null) {
			switch (filtro.getTipo()){
			case OK: 
				search.setEsitoGruppo(EsitoUtils.ALL_OK_VALUE);
				search.setEsitoDettaglio( (Integer) filtro.getDettaglio() );
				break;
			case FAULT:
				search.setEsitoGruppo(EsitoUtils.ALL_FAULT_APPLICATIVO_VALUE);
				search.setEsitoDettaglio( (Integer) filtro.getDettaglio() );
				break;
			case ERROR:
				search.setEsitoGruppo(EsitoUtils.ALL_ERROR_VALUE);
				search.setEsitoDettaglio((Integer) filtro.getDettaglio());
				break;
			case ERROR_OR_FAULT:
				search.setEsitoGruppo(EsitoUtils.ALL_ERROR_FAULT_APPLICATIVO_VALUE);
				search.setEsitoDettaglio((Integer) filtro.getDettaglio());
				break;
			case PERSONALIZZATO:
				search.setEsitoGruppo(EsitoUtils.ALL_PERSONALIZZATO_VALUE);
				
				ArrayList<Integer> dettaglioEsito = (ArrayList<Integer>) filtro.getDettaglio();
				search.setEsitoDettaglioPersonalizzato(dettaglioEsito.toArray(new Integer[1]));
				break;
			}	
		}
		
	}
	
		
	@SuppressWarnings("unchecked")
	public static final void overrideRicercaBaseTransazione(RicercaBaseTransazione body, TransazioniSearchForm search, MonitoraggioEnv env) throws Exception, IllegalAccessException {
		if (body == null) return;
		
		switch (body.getTipo()) {
		case EROGAZIONE:
			overrideFiltroApiBase(BaseHelper.fromMap( (Map<String,Object>) body.getApi(), FiltroApiBase.class), body.getAzione(), search, env);
			break;
		case FRUIZIONE:
			overrideFiltroFruizione(BaseHelper.fromMap( (Map<String,Object>) body.getApi(), FiltroFruizione.class), body.getAzione(), search, env);
			break;
		}
		
		overrideFiltroEsito(body.getEsito(), search, env);
		search.setEvento(body.getEvento());
		search.setClusterId(body.getIdCluster());
	}
	
	
	@SuppressWarnings("unchecked")
	public static final void overrideFiltroMittente(RicercaIntervalloTemporale body, TransazioniSearchForm search, MonitoraggioEnv env ) throws Exception {
		if (body == null) return;
		
		// Filtraggio  Mittente
		final String tipo_soggetto = env.soggetto.getTipo();
		switch (body.getTipo()) {
		case FRUIZIONE: {			
			FiltroMittenteFruizione fMittente = BaseHelper.fromMap( (Map<String,Object>) body.getMittente(), FiltroMittenteFruizione.class);
			if (fMittente == null) break;
			
			switch (fMittente.getTipo()) {
			case APPLICATIVO: {
				FiltroMittenteFruizioneApplicativo fAppl = BaseHelper.fromMap( (Map<String,Object>) fMittente.getId(), FiltroMittenteFruizioneApplicativo.class);
				search.setRiconoscimento(Costanti.VALUE_TIPO_RICONOSCIMENTO_APPLICATIVO);
				search.setServizioApplicativo(fAppl.getApplicativo());
				break;
			}
			case IDENTIFICATIVO_AUTENTICATO: {
				FiltroMittenteIdApplicativo fIdent = BaseHelper.fromMap( (Map<String,Object>) fMittente.getId(), FiltroMittenteIdApplicativo.class);
				TransazioniHelper.overrideFiltroMittenteIdApplicativo(fIdent, search, env);
				break;
			}
				
			case TOKEN_INFO:
				FiltroMittenteFruizioneTokenClaim fClaim = BaseHelper.fromMap( (Map<String,Object>) fMittente.getId(), FiltroMittenteFruizioneTokenClaim .class);
				search.setRiconoscimento(Costanti.VALUE_TIPO_RICONOSCIMENTO_TOKEN_INFO);
				search.setMittenteMatchingType((BooleanUtils.isTrue(fClaim.isRicercaEsatta()) ? TipoMatch.EQUALS : TipoMatch.LIKE ).toString());
				search.setMittenteCaseSensitiveType( (BooleanUtils.isTrue(fClaim.isCaseSensitive()) ? CaseSensitiveMatch.SENSITIVE : CaseSensitiveMatch.INSENSITIVE).toString() );
				search.setValoreRiconoscimento(fClaim.getId());
				break;
			}
			break;
		}
		
		case EROGAZIONE: { 
			FiltroMittenteErogazione fMittente = BaseHelper.fromMap( (Map<String,Object>) body.getMittente(), FiltroMittenteErogazione.class);
			if (fMittente == null) break;
			
			switch (fMittente.getTipo()) {
			case APPLICATIVO:{
				search.setRiconoscimento(Costanti.VALUE_TIPO_RICONOSCIMENTO_APPLICATIVO);
				FiltroMittenteErogazioneApplicativo fAppl = BaseHelper.fromMap( (Map<String,Object>) fMittente.getId(), FiltroMittenteErogazioneApplicativo.class);
				search.setServizioApplicativo(fAppl.getApplicativo());
				if (fAppl.getSoggetto() != null)
					search.setTipoNomeMittente(new IDSoggetto(tipo_soggetto, fAppl.getSoggetto()).toString() );
			
				break;
			}
			case IDENTIFICATIVO_AUTENTICATO: {
				FiltroMittenteIdApplicativo fIdent = BaseHelper.fromMap((Map<String,Object>) fMittente.getId(), FiltroMittenteIdApplicativo.class);
				// TODO:  Testa se gli puoi passare un json che abbia a null questo campo, sebbene sia annotatocome Valid e come NotNull nella classe java.
				//			Non solo, se glielo puoi passare allora aggiungi i controlli
				TransazioniHelper.overrideFiltroMittenteIdApplicativo(fIdent, search, env);
				break;
			}
			case SOGGETTO: {
				FiltroMittenteErogazioneSoggetto fSogg = BaseHelper.fromMap( (Map<String,Object>) fMittente.getId(), FiltroMittenteErogazioneSoggetto.class );
				search.setRiconoscimento(Costanti.VALUE_TIPO_RICONOSCIMENTO_SOGGETTO);
				search.setTipoNomeMittente(new IDSoggetto(tipo_soggetto, fSogg.getSoggetto()).toString() );
				break;
			}
			case TOKEN_INFO: {
				FiltroMittenteErogazioneTokenClaim fClaim = BaseHelper.fromMap( (Map<String,Object>) fMittente.getId(), FiltroMittenteErogazioneTokenClaim.class);
				search.setRiconoscimento(Costanti.VALUE_TIPO_RICONOSCIMENTO_TOKEN_INFO);
				search.setMittenteMatchingType((BooleanUtils.isTrue(fClaim.isRicercaEsatta()) ? TipoMatch.EQUALS : TipoMatch.LIKE ).toString());
				search.setMittenteCaseSensitiveType( (BooleanUtils.isTrue(fClaim.isCaseSensitive()) ? CaseSensitiveMatch.SENSITIVE : CaseSensitiveMatch.INSENSITIVE).toString() );
				search.setValoreRiconoscimento(fClaim.getId());
				search.setTipoNomeMittente(new IDSoggetto(tipo_soggetto, fClaim.getSoggetto()).toString() );
				break;
			}	
			}
			break;
		}
		}
	}

	public static final ListaTransazioni searchTransazioni(TransazioniSearchForm search, Integer offset, Integer limit, String sort, MonitoraggioEnv env) throws UtilsException, InstantiationException, IllegalAccessException {
		DBManager dbManager = DBManager.getInstance();
		Connection connection = null;
		try {
			connection = dbManager.getConnection();
			ServerProperties serverProperties = ServerProperties.getInstance();
			ServiceManagerProperties smp = dbManager.getServiceManagerProperties();
			TransazioniService transazioniService = new TransazioniService(connection, true, smp, LoggerProperties.getLoggerDAO());
			transazioniService.setSearch(search);
			
			List<TransazioneBean> listTransazioniDB = transazioniService.findAll(Converter.toOffset(offset), Converter.toLimit(limit), 
					Converter.toSortOrder(sort), Converter.toSortField(sort));
			ListaTransazioni ret = ListaUtils.costruisciLista(
					env.context.getServletRequest().getRequestURI(),
					Converter.toOffset(offset), Converter.toLimit(limit), 
					listTransazioniDB!=null ? listTransazioniDB.size() : 0, 
							ListaTransazioni.class
				); 
			
			if ( serverProperties.isFindall404() && (listTransazioniDB==null || listTransazioniDB.isEmpty()) )
				throw FaultCode.NOT_FOUND.toException("Nessuna transazione trovata corrispondente ai criteri di ricerca");
			
			if(listTransazioniDB!=null && !listTransazioniDB.isEmpty()) {
				listTransazioniDB.forEach( transazioneDB -> 
					{
						try {
							ret.addItemsItem( Converter.toItemTransazione(transazioneDB, env.log) );
						} catch (Exception e) {
							throw new RuntimeException(e.getMessage(),e);
						}
					}
					);
			}
	
			env.context.getLogger().info("Invocazione completata con successo");
	        return ret;
		}
		finally {
			dbManager.releaseConnection(connection);
		}		
	}
}
