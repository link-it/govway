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
package org.openspcoop2.web.monitor.core.bean;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;

import org.apache.commons.lang.StringUtils;
import org.openspcoop2.core.commons.CoreException;
import org.openspcoop2.core.commons.search.AccordoServizioParteSpecifica;
import org.openspcoop2.core.commons.search.Soggetto;
import org.openspcoop2.core.config.constants.TipoAutenticazione;
import org.openspcoop2.core.id.IDServizio;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.core.registry.constants.ServiceBinding;
import org.openspcoop2.generic_project.expression.SortOrder;
import org.openspcoop2.monitor.engine.condition.EsitoUtils;
import org.openspcoop2.monitor.engine.config.ricerche.ConfigurazioneRicerca;
import org.openspcoop2.monitor.engine.config.statistiche.ConfigurazioneStatistica;
import org.openspcoop2.monitor.sdk.condition.IFilter;
import org.openspcoop2.protocol.engine.ProtocolFactoryManager;
import org.openspcoop2.protocol.engine.utils.NamingUtils;
import org.openspcoop2.protocol.sdk.IProtocolFactory;
import org.openspcoop2.protocol.sdk.ProtocolException;
import org.openspcoop2.protocol.sdk.constants.EsitoTransazioneName;
import org.openspcoop2.protocol.utils.EsitiConfigUtils;
import org.openspcoop2.protocol.utils.EsitiProperties;
import org.openspcoop2.utils.TipiDatabase;
import org.openspcoop2.utils.UtilsException;
import org.openspcoop2.utils.certificate.CertificateUtils;
import org.openspcoop2.utils.certificate.PrincipalType;
import org.openspcoop2.utils.resources.MapReader;
import org.openspcoop2.web.lib.users.dao.User;
import org.openspcoop2.web.monitor.core.constants.CaseSensitiveMatch;
import org.openspcoop2.web.monitor.core.constants.Costanti;
import org.openspcoop2.web.monitor.core.constants.TipoMatch;
import org.openspcoop2.web.monitor.core.constants.TipoMessaggio;
import org.openspcoop2.web.monitor.core.constants.TipologiaRicerca;
import org.openspcoop2.web.monitor.core.core.PddMonitorProperties;
import org.openspcoop2.web.monitor.core.core.PermessiUtenteOperatore;
import org.openspcoop2.web.monitor.core.core.Utility;
import org.openspcoop2.web.monitor.core.dynamic.Ricerche;
import org.openspcoop2.web.monitor.core.dynamic.Statistiche;
import org.openspcoop2.web.monitor.core.exception.UserInvalidException;
import org.openspcoop2.web.monitor.core.logger.LoggerManager;
import org.openspcoop2.web.monitor.core.utils.DynamicPdDBeanUtils;
import org.openspcoop2.web.monitor.core.utils.MessageManager;
import org.openspcoop2.web.monitor.core.utils.MessageUtils;
import org.richfaces.model.Ordering;
import org.slf4j.Logger;

/****
 * BaseSearchForm
 * 
 * @author Pintori Giuliano (pintori@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 *
 */
public abstract class BaseSearchForm extends AbstractDateSearchForm {

	// private String nomeMittente;
	private String nomeAzione;
	private String nomeServizio;
	// private String nomeDestinatario;
	private TipologiaRicerca tipologiaRicerca;
	private TipologiaRicerca defaultTipologiaRicerca;
	private boolean _tipologiaRicercaEntrambiEnabled;
	// private String trafficoPerSoggetto;
	// private String soggettoLocale;

	private Integer esitoGruppo;
	private Integer esitoDettaglio;
	private Integer[] esitoDettaglioPersonalizzato;
	private String esitoContesto;
	private boolean escludiRichiesteScartate;

	private String servizioApplicativo;
	private String idCorrelazioneApplicativa;
	private String idEgov;
	private String tipoIdMessaggio = TipoMessaggio.Richiesta.name();
	private String idTransazione;

	private String tipoNomeMittente;
	private String tipoNomeDestinatario;
	private String tipoNomeTrafficoPerSoggetto;
	private String tipoNomeSoggettoLocale;
	
	// supporto ricerche con autocompletamento
	private String labelTipoNomeMittente;
	private String labelTipoNomeDestinatario;
	private String labelTipoNomeTrafficoPerSoggetto;
	private String labelTipoNomeSoggettoLocale;
	private String labelNomeServizio;
	private String labelNomeAzione;	
	
	private String gruppo;
	

	// ricerche
	private String nomeRicercaPersonalizzata;
	private Ricerche ricerchePersonalizzate;
	private ConfigurazioneRicerca ricercaSelezionata;
	// statistiche
	private String nomeStatisticaPersonalizzata;
	private Statistiche statistichePersonalizzate;
	private ConfigurazioneStatistica statisticaSelezionata;

	private boolean ricerchePersonalizzateAttive = false;
	private boolean statistichePersonalizzateAttive = false;

	private String protocollo;
	private List<SelectItem> protocolli= null;
	private String modalita = null;
	private String soggettoPddMonitor = null;
	private boolean checkSoggettoPddMonitor = true;
	

	private IFilter filtro;

	// indica il tipo di ricerca che si vuole fare
	// true (default) = tipo di ricerca spcoop
	// false = tipo di ricerca IM
	// null = entrambe
	private String tipoRicercaSPCoop;

	private String intervalloRefresh = null;
	private int tempoMassimoRefreshLive = 0;

	private User user;

	private SortOrder sortOrder = SortOrder.DESC;
	private String sortField = null;

	private Map<String, Ordering> sortOrders =null;

	private static Logger log =  LoggerManager.getPddMonitorCoreLogger();

	private TipiDatabase tipoDatabase = null;

	private String riconoscimento = null;
	private String autenticazione = null;
	private String tokenClaim = null;
	private String valoreRiconoscimento = null;
	private TipoMatch mittenteMatchingType = TipoMatch.EQUALS;
	private CaseSensitiveMatch mittenteCaseSensitiveType = CaseSensitiveMatch.SENSITIVE;
	private String clientAddressMode = null;
	
	private boolean isSearchFormEsitoConsegnaMultiplaEnabled = true;
	

	public TipiDatabase getDatabaseType() {
		return _getTipoDatabase(org.openspcoop2.core.transazioni.utils.ProjectInfo.getInstance());
	}

	public TipiDatabase _getTipoDatabase(org.openspcoop2.generic_project.beans.IProjectInfo pfInfo) {
		if(this.tipoDatabase == null){
			try {
				PddMonitorProperties govwayMonitorProperties = PddMonitorProperties.getInstance(BaseSearchForm.log);
				this.tipoDatabase = govwayMonitorProperties.tipoDatabase(pfInfo);
			} catch (Exception e) {
				BaseSearchForm.log.error("Errore la get Tipo Database: " + e.getMessage(),e);
			}
		}
		return this.tipoDatabase;
	}

	@Override
	public String getPrintPeriodo(){
		return super.getDefaultPrintPeriodoBehaviour();
	}

	public BaseSearchForm() {

		this.tipologiaRicerca = this.getDefaultTipologiaRicercaEnum();
		this.esitoGruppo = EsitoUtils.ALL_VALUE;
		this.esitoDettaglio = EsitoUtils.ALL_VALUE;
		try {
			EsitiProperties esitiProperties = EsitiConfigUtils.getEsitiPropertiesForContext(BaseSearchForm.log);
			if(esitiProperties.getEsitoTransactionContextDefault()!=null){
				this.esitoContesto = esitiProperties.getEsitoTransactionContextDefault();
			}
			else{
				this.esitoContesto = EsitoUtils.ALL_VALUE_AS_STRING;
			}
		} catch (Exception e) {
			BaseSearchForm.log.error("Errore durante l'impostazione del default per il contesto: " + e.getMessage(),e);
			this.esitoContesto = EsitoUtils.ALL_VALUE_AS_STRING;
		}
		
		this.tipoRicercaSPCoop = "spcoop";
		this.setPeriodo(this.periodoDefault != null ? this.periodoDefault
				: "Ultimo mese");
		this.sortOrder = SortOrder.DESC;
		this.sortField = null;
		this.sortOrders = new HashMap<String, Ordering>();

		try {
			PddMonitorProperties govwayMonitorProperties = PddMonitorProperties.getInstance(BaseSearchForm.log);

			this.intervalloRefresh = govwayMonitorProperties.getIntervalloRefreshTransazioniLive();
			this.tempoMassimoRefreshLive = govwayMonitorProperties.getTempoMassimoRefreshLive();
			this.setRicerchePersonalizzateAttive(govwayMonitorProperties.isAttivoModuloRicerchePersonalizzate());
			this.setStatistichePersonalizzateAttive(govwayMonitorProperties.isAttivoModuloTransazioniStatistichePersonalizzate());	
			this._tipologiaRicercaEntrambiEnabled = govwayMonitorProperties.isVisualizzaVoceEntrambiFiltroRuolo();
			this.isSearchFormEsitoConsegnaMultiplaEnabled = govwayMonitorProperties.isSearchFormEsitoConsegnaMultiplaEnabled();
			this.escludiRichiesteScartate = govwayMonitorProperties.escludiRichiesteScartateDefaultValue();
		} catch (Exception e) {
			BaseSearchForm.log.error("Errore durante la creazione del form: " + e.getMessage(),e);
		}

	}


	public BaseSearchForm(boolean useInBatch) {

		this.tipologiaRicerca = this.getDefaultTipologiaRicercaEnum();
		this.esitoGruppo = EsitoUtils.ALL_VALUE;
		this.esitoDettaglio = EsitoUtils.ALL_VALUE;
		try {
			EsitiProperties esitiProperties = EsitiConfigUtils.getEsitiPropertiesForContext(BaseSearchForm.log);
			if(esitiProperties.getEsitoTransactionContextDefault()!=null){
				this.esitoContesto = esitiProperties.getEsitoTransactionContextDefault();
			}
			else{
				this.esitoContesto = EsitoUtils.ALL_VALUE_AS_STRING;
			}
		} catch (Exception e) {
			BaseSearchForm.log.error("Errore durante l'impostazione del default per il contesto: " + e.getMessage(),e);
			this.esitoContesto = EsitoUtils.ALL_VALUE_AS_STRING;
		}
		try {
			PddMonitorProperties govwayMonitorProperties = PddMonitorProperties.getInstance(BaseSearchForm.log);
			this.escludiRichiesteScartate = govwayMonitorProperties.escludiRichiesteScartateDefaultValue();
		} catch (Exception e) {
			BaseSearchForm.log.error("Errore durante la creazione del form: " + e.getMessage(),e);
		}
		this.tipoRicercaSPCoop = "spcoop";
		this.setPeriodo(this.periodoDefault != null ? this.periodoDefault
				: "Ultimo mese");

		this.intervalloRefresh = "";
		this.tempoMassimoRefreshLive = 0;
		this.ricerchePersonalizzateAttive = false;
		this.statistichePersonalizzateAttive = false;
		this.sortOrder = SortOrder.DESC;
		this.sortField = null;
		this.sortOrders = new HashMap<String, Ordering>();

	}

	@Override
	protected String ripulisciValori(){
		this.initSearchListener(null);
		return null;
	}

	@Override
	public void initSearchListener(ActionEvent ae) {
		try {
			super.initSearchListener(ae);
			this.tipologiaRicerca = this.getDefaultTipologiaRicercaEnum();
			this.esitoGruppo = EsitoUtils.ALL_VALUE;
			this.esitoDettaglio = EsitoUtils.ALL_VALUE;
			try {
				EsitiProperties esitiProperties = EsitiConfigUtils.getEsitiPropertiesForContext(BaseSearchForm.log);
				if(esitiProperties.getEsitoTransactionContextDefault()!=null){
					this.esitoContesto = esitiProperties.getEsitoTransactionContextDefault();
				}
				else{
					this.esitoContesto = EsitoUtils.ALL_VALUE_AS_STRING;
				}
			} catch (Exception e) {
				BaseSearchForm.log.error("Errore durante l'impostazione del default per il contesto: " + e.getMessage(),e);
				this.esitoContesto = EsitoUtils.ALL_VALUE_AS_STRING;
			}
			PddMonitorProperties govwayMonitorProperties = PddMonitorProperties.getInstance(BaseSearchForm.log);
			this.escludiRichiesteScartate = govwayMonitorProperties.escludiRichiesteScartateDefaultValue();
			this.tipoRicercaSPCoop = "spcoop";
			this.setPeriodo(this.periodoDefault != null ? this.periodoDefault
					: "Ultimo mese");

			_setPeriodo();
			User utente = this.getUser();

			this.aggiornaNuovaDataRicerca();

			this.nomeAzione = null;
			// this.nomeDestinatario=null;
			// this.nomeMittente=null;
			this.nomeRicercaPersonalizzata = null;
			this.nomeServizio = null;
			// this.soggettoLocale=null;
			this.nomeStatisticaPersonalizzata = null;
			this.filtro = null;
			this.servizioApplicativo = null;
			this.ricercaSelezionata = null;
			// this.trafficoPerSoggetto=null;
			this.statisticaSelezionata = null;
			this.statistichePersonalizzate = null;
			this.ricerchePersonalizzate = null;

			this.tipoNomeDestinatario = null;
			this.tipoNomeMittente = null;
			
			String loggedUtenteSoggettoPddMonitor = Utility.getLoggedUtenteSoggettoPddMonitor();
			if(this.isShowFiltroSoggettoLocale() && Costanti.VALUE_PARAMETRO_MODALITA_ALL.equals(loggedUtenteSoggettoPddMonitor)) {
				this.tipoNomeSoggettoLocale = null;
			} else {
				this.tipoNomeSoggettoLocale = loggedUtenteSoggettoPddMonitor;
			}
			
			this.tipoNomeTrafficoPerSoggetto = null;
			
			this.labelTipoNomeMittente = null;
			this.labelTipoNomeDestinatario = null;
			this.labelTipoNomeTrafficoPerSoggetto = null;
			this.labelTipoNomeSoggettoLocale = null;
			this.labelNomeServizio = null;
			this.labelNomeAzione = null;

			this.gruppo = null;
			
			this.idCorrelazioneApplicativa = null;
			this.idEgov = null;
			this.tipoIdMessaggio = TipoMessaggio.Richiesta.name();
			this.idTransazione = null;

			this.sortOrder = SortOrder.DESC;
			this.sortField = null;
			
			IProtocolFactory<?> protocolFactory = null;
			
			
			if(this.isShowListaProtocolli()) {
				String loggedUtenteModalita = Utility.getLoggedUtenteModalita();
				
				if(Costanti.VALUE_PARAMETRO_MODALITA_ALL.equals(loggedUtenteModalita)) {
					if(utente.getProtocolliSupportati() !=null && utente.getProtocolliSupportati().size() > 0) {
						if(utente.getProtocolliSupportati().contains(ProtocolFactoryManager.getInstance().getDefaultProtocolFactory().getProtocol())) {
							protocolFactory = ProtocolFactoryManager.getInstance().getDefaultProtocolFactory();
						} else {
							protocolFactory = ProtocolFactoryManager.getInstance().getProtocolFactoryByName(utente.getProtocolliSupportati().get(0));
						}
					} else {
						try {
							protocolFactory = ProtocolFactoryManager.getInstance().getDefaultProtocolFactory();
						}catch(Exception e) {
							BaseSearchForm.log.error("Errore durante l'impostazione del default per il protocollo: " + e.getMessage(),e);
						}
					}
				}else if(loggedUtenteModalita==null) {
					protocolFactory = ProtocolFactoryManager.getInstance().getDefaultProtocolFactory();
				} 
				else {
					protocolFactory = ProtocolFactoryManager.getInstance().getProtocolFactoryByName(loggedUtenteModalita);
				}
				
			}
			else {
				// utente ha selezionato una modalita'
				if(utente.getProtocolloSelezionatoPddMonitor()!=null) {
					protocolFactory = ProtocolFactoryManager.getInstance().getProtocolFactoryByName(utente.getProtocolloSelezionatoPddMonitor());
				} else if(utente.getProtocolliSupportati() !=null && utente.getProtocolliSupportati().size() > 0) {
					protocolFactory = ProtocolFactoryManager.getInstance().getProtocolFactoryByName(utente.getProtocolliSupportati().get(0));
				} else {
					protocolFactory = ProtocolFactoryManager.getInstance().getDefaultProtocolFactory();
				}
			}
			this.protocollo = protocolFactory.getProtocol();
			
			this.riconoscimento = null;
			this.autenticazione = null;
			this.tokenClaim = null;
			this.valoreRiconoscimento = null;
			this.mittenteMatchingType = TipoMatch.EQUALS;
			this.mittenteCaseSensitiveType = CaseSensitiveMatch.SENSITIVE;
			this.clientAddressMode = "--";
			// gia' eseguito nella chiamata del parent
//			this.executeQuery = false;
		} catch (Throwable e) {
			BaseSearchForm.log.error("Errore durante l'inizializzazione: " + e.getMessage(),e);
		}
	}

	public void modalitaRicercaListener(ActionEvent ae) {

		//		String mod = this.modalitaRicercaStorico;
		//		
		//		initSearchListener(ae);
		//		
		//		this.modalitaRicercaStorico = mod;
		this.sortOrders = new HashMap<String, Ordering>();
	}

	public void soggettoLocaleSelected(ActionEvent ae){
		this.setServizioApplicativo(null);
	}

	public void destinatarioSelected(ActionEvent ae) {
		this.nomeServizio = null;
		this.nomeAzione = null;
		this.ricerchePersonalizzate = null;
		this.statistichePersonalizzate = null;
		this.labelNomeServizio = null;
		this.labelNomeAzione = null;
	}

	public void gruppoSelected(ActionEvent ae) {
		this.nomeServizio = null;
		this.nomeAzione = null;
		this.ricercaSelezionata = null;
		this.nomeRicercaPersonalizzata = null;
		this.nomeStatisticaPersonalizzata = null;
		this.labelNomeServizio = null;
		this.labelAzione = null;
		this.labelNomeAzione = null;
	}
	
	public void servizioSelected(ActionEvent ae) {
		this.nomeAzione = null;
		this.ricercaSelezionata = null;
		this.nomeRicercaPersonalizzata = null;
		this.nomeStatisticaPersonalizzata = null;
		this.labelAzione = null;
		this.labelNomeAzione = null;
	}

	public void azioneSelected(ActionEvent ae) {
		this.ricercaSelezionata = null;
		this.nomeRicercaPersonalizzata = null;
		this.nomeStatisticaPersonalizzata = null;
	}

	public void tipologiaRicercaListener(ActionEvent ae) {
		// se cambia la tipologia di ricerca devo azzerare le scelte precedenti
		// this.nomeDestinatario = null;
		this.nomeServizio = null;
		this.nomeAzione = null;
		// this.nomeMittente = null;
		// this.trafficoPerSoggetto = null;
		this.ricerchePersonalizzate = null;
		this.ricercaSelezionata = null;
		// this.soggettoGestione = null;

		// User loggedUser = Utility.getLoggedUser();
		// this.identificativoPorta = loggedUser.getIdentificativoPorta();

		// if(loggedUser.isAdmin()==false){
		//
		// if("ingresso".equals(this.tipologiaRicerca)){
		// this.nomeDestinatario = loggedUser.getSoggetto().getNome();
		// }
		// if("uscita".equals(this.tipologiaRicerca)){
		// this.nomeMittente = loggedUser.getSoggetto().getNome();
		// }
		// }

		this.tipoNomeDestinatario = null;
		this.tipoNomeMittente = null;
		String loggedUtenteSoggettoPddMonitor = Utility.getLoggedUtenteSoggettoPddMonitor();
		if(this.isShowFiltroSoggettoLocale() && Costanti.VALUE_PARAMETRO_MODALITA_ALL.equals(loggedUtenteSoggettoPddMonitor)) {
			this.tipoNomeSoggettoLocale = null;
		} else {
			this.tipoNomeSoggettoLocale = loggedUtenteSoggettoPddMonitor;
		}
		this.tipoNomeTrafficoPerSoggetto = null;
		
		this.labelTipoNomeMittente = null;
		this.labelTipoNomeDestinatario = null;
		this.labelTipoNomeTrafficoPerSoggetto = null;
		this.labelTipoNomeSoggettoLocale = null;
		this.labelNomeServizio = null;
		this.labelNomeAzione = null;
		
		this.servizioApplicativo=null;
		
		this.riconoscimento = null;

	}

	/**
	 * Se l'utente non e' impostato, allora lo prendo dal contesto di spring
	 * security Qualora l'utente fosse stato gia' impostato tramite la setUser
	 * ad esempio non sono in un contesto j2ee e quindi spring-security non lo
	 * utilizzo, allora viene ritornato l'utente gia' impostato
	 * 
	 * @return Utente collegato
	 */
	public User getUser() {

		if (this.user != null) {
			// e' stato impostato manualmente l'utente
			return this.user;
		}

		return Utility.getLoggedUtente();
	}

	public void setUser(User user) {
		this.user = user;
	}


	public String getTipoMittente() {
		return Utility.parseTipoSoggetto(this.tipoNomeMittente);
	}

	public String getNomeMittente() {
		return Utility.parseNomeSoggetto(this.tipoNomeMittente);
	}

	/**
	 * La stringa di input sara' del tipo tipoSoggetto/nomeSoggetto
	 * 
	 * @param tipoNomeMittente
	 */
	public void setTipoNomeMittente(String tipoNomeMittente) {
		this.tipoNomeMittente = tipoNomeMittente;

		if (StringUtils.isEmpty(this.tipoNomeMittente)
				|| "--".equals(this.tipoNomeMittente))
			this.tipoNomeMittente = null;
	}

	public String getNomeAzione() {
		return this.nomeAzione;
	}

	public void setNomeAzione(String nomeAzione) {
		this.nomeAzione = nomeAzione;

		if (StringUtils.isEmpty(nomeAzione) || "--".equals(nomeAzione))
			this.nomeAzione = null;
	}
	
	public String getNomeAzioneTooltip() {
		if(this.nomeServizio!=null && this.nomeAzione != null){
			try {
				String tipoProtocollo = this.getProtocollo();
				IDServizio idServizio = Utility.parseServizioSoggetto(this.getNomeServizio());
				
				Map<String, String> findAzioniFromServizio = DynamicPdDBeanUtils.getInstance(log).findAzioniFromServizio(tipoProtocollo, idServizio, null);
				
				if(findAzioniFromServizio.containsKey(this.nomeAzione)) {
					return findAzioniFromServizio.get(this.nomeAzione);
				}
			}catch(Exception e) {
				return this.nomeAzione;
			}
		}
		return this.nomeAzione;
	}

	public String estraiNomeServizioDalServizio() {
		if(this.nomeServizio!=null){
			try {
				IDServizio idServizio = Utility.parseServizioSoggetto(this.nomeServizio);
				return  idServizio.getNome();
			}catch(Exception e) {
				return null;
			}
//			if(this.nomeServizio.contains(" ")){
//				String [] tmp = this.nomeServizio.split(" ");
//				if(tmp!=null && tmp.length>0){
//					String tipoNomeServizio = tmp[0].trim();
//					if(tipoNomeServizio.contains("/")){
//						tmp = tipoNomeServizio.split("/");
//						if(tmp!=null && tmp.length>1){
//							return tmp[1].trim();
//						}
//					}
//				}
//			}
		}
		return null;
	}
	public String estraiTipoServizioDalServizio() {
		if(this.nomeServizio!=null){
			try {
				IDServizio idServizio = Utility.parseServizioSoggetto(this.nomeServizio);
				return  idServizio.getTipo();
			}catch(Exception e) {
				return null;
			}
//			if(this.nomeServizio.contains(" ")){
//				String [] tmp = this.nomeServizio.split(" ");
//				if(tmp!=null && tmp.length>0){
//					String tipoNomeServizio = tmp[0].trim();
//					if(tipoNomeServizio.contains("/")){
//						tmp = tipoNomeServizio.split("/");
//						if(tmp!=null && tmp.length>1){
//							return tmp[0].trim();
//						}
//					}
//				}
//			}
		}
		return null;
	}

	public String estraiNomeSoggettoDalServizio() {
		if(this.nomeServizio!=null){
			try {
				IDServizio idServizio = Utility.parseServizioSoggetto(this.nomeServizio);
				return  idServizio.getSoggettoErogatore()  != null ? idServizio.getSoggettoErogatore().getNome() : null;
			}catch(Exception e) {
				return null;
			}
//			if(this.nomeServizio.contains(" ")){
//				String [] tmp = this.nomeServizio.split(" ");
//				if(tmp!=null && tmp.length>0){
//					String tipoNomeSoggetto = tmp[1].trim();
//					if(tipoNomeSoggetto.startsWith("(")){
//						tipoNomeSoggetto = tipoNomeSoggetto.substring(1);
//					}
//					if(tipoNomeSoggetto.endsWith(")")){
//						tipoNomeSoggetto = tipoNomeSoggetto.substring(0,(tipoNomeSoggetto.length()-1));
//					}
//					if(tipoNomeSoggetto.contains("/")){
//						tmp = tipoNomeSoggetto.split("/");
//						if(tmp!=null && tmp.length>1){
//							return tmp[1].trim();
//						}
//					}
//				}
//			}
		}
		return null;
	}

	public String estraiTipoSoggettoDalServizio() {
		if(this.nomeServizio!=null){
			try {
				IDServizio idServizio = Utility.parseServizioSoggetto(this.nomeServizio);
				return  idServizio.getSoggettoErogatore()  != null ? idServizio.getSoggettoErogatore().getTipo() : null;
			}catch(Exception e) {
				return null;
			}
//			
//			if(this.nomeServizio.contains(" ")){
//				String [] tmp = this.nomeServizio.split(" ");
//				if(tmp!=null && tmp.length>0){
//					String tipoNomeSoggetto = tmp[1].trim();
//					if(tipoNomeSoggetto.startsWith("(")){
//						tipoNomeSoggetto = tipoNomeSoggetto.substring(1);
//					}
//					if(tipoNomeSoggetto.endsWith(")")){
//						tipoNomeSoggetto = tipoNomeSoggetto.substring(0,(tipoNomeSoggetto.length()-1));
//					}
//					if(tipoNomeSoggetto.contains("/")){
//						tmp = tipoNomeSoggetto.split("/");
//						if(tmp!=null && tmp.length>1){
//							return tmp[0].trim();
//						}
//					}
//				}
//			}
		}
		return null;
	}
	
	public Integer estraiVersioneServizioDalServizio() {
		if(this.nomeServizio!=null){
			try {
				IDServizio idServizio = Utility.parseServizioSoggetto(this.nomeServizio);
				return  idServizio.getVersione();
			}catch(Exception e) {
				return null;
			}
		}
		return null;
	}

	public String getGruppoTooltip() {
		if(StringUtils.isNotEmpty(this.getGruppo())) {
			return this.gruppo;
		}
		return null;
	}
	
	public String getGruppo() {
		return this.gruppo;
	}

	public void setGruppo(String gruppo) {
		this.gruppo = gruppo;

		if (StringUtils.isEmpty(gruppo) || "--".equals(gruppo))
			this.gruppo = null;
	} 
	
	public String getNomeServizioTooltip() {
		if(StringUtils.isNotEmpty(this.getNomeServizio())) {
			try {
				String tipoProtocollo = this.getProtocollo();
				IDServizio idServizio = Utility.parseServizioSoggetto(this.getNomeServizio());
				
				String nomeSoggetto = null;
				if (TipologiaRicerca.ingresso.equals(this.getTipologiaRicercaEnum())) {
					if (StringUtils.isNotBlank(this.getSoggettoLocale())) {
						nomeSoggetto = this.getSoggettoLocale();
					}
				} else if (TipologiaRicerca.uscita.equals(this.getTipologiaRicercaEnum())) {
					// uscita
					if (StringUtils.isNotBlank(this.getNomeDestinatario())) {
						nomeSoggetto = this.getNomeDestinatario();
					}
				}
				String label = StringUtils.isEmpty(nomeSoggetto) ? NamingUtils.getLabelAccordoServizioParteSpecifica(tipoProtocollo,idServizio) 
						: NamingUtils.getLabelAccordoServizioParteSpecificaSenzaErogatore(tipoProtocollo, idServizio.getTipo(), idServizio.getNome(), idServizio.getVersione());
				
				return label;
			} catch (Exception e) {
				BaseSearchForm.log.error(e.getMessage(), e);
			}
		}
		
		return null;
	}

	public String getNomeServizio() {
		return this.nomeServizio;
	}

	public void setNomeServizio(String nomeServizio) {
		this.nomeServizio = nomeServizio;

		if (StringUtils.isEmpty(nomeServizio) || "--".equals(nomeServizio))
			this.nomeServizio = null;
	}

	public String getTipoDestinatario() {
		if(this.tipoNomeDestinatario!=null && !"".equals(this.tipoNomeDestinatario)){
			return Utility.parseTipoSoggetto(this.tipoNomeDestinatario);
		}
		else{
			return this.estraiTipoSoggettoDalServizio();
		}
	}

	public String getNomeDestinatario() {
		if(this.tipoNomeDestinatario!=null && !"".equals(this.tipoNomeDestinatario)){
			return Utility.parseNomeSoggetto(this.tipoNomeDestinatario);
		}
		else{
			return this.estraiNomeSoggettoDalServizio();
		}
	}

	public void setTipoNomeDestinatario(String nomeDestinatario) {
		this.tipoNomeDestinatario = nomeDestinatario;

		if (StringUtils.isEmpty(nomeDestinatario)
				|| "--".equals(nomeDestinatario))
			this.tipoNomeDestinatario = null;

	}
	
	public String getTipologiaRicerca() {
		return this.getTipologiaRicercaEnum() != null ? this.getTipologiaRicercaEnum().toString() : "";
	}

	public void setTipologiaRicerca(String tipologiaRicerca) {
		if (StringUtils.isEmpty(tipologiaRicerca) || "--".equals(tipologiaRicerca))
			this.tipologiaRicerca = null;
		else 
			this.setTipologiaRicerca(TipologiaRicerca.valueOf(tipologiaRicerca));
		
	}

	public TipologiaRicerca getTipologiaRicercaEnum() {
		return this.tipologiaRicerca;
	}

	public void setTipologiaRicerca(TipologiaRicerca tipologiaRicerca) {
		this.tipologiaRicerca = tipologiaRicerca;
	}
	
	public String getDefaultTipologiaRicerca() {
		return this.getDefaultTipologiaRicercaEnum() != null ? this.getDefaultTipologiaRicercaEnum().toString() : "";
	}
	
	protected boolean isTipologiaRicercaEntrambiEnabled() {
		return this._tipologiaRicercaEntrambiEnabled;
	}
	
	public TipologiaRicerca getDefaultTipologiaRicercaEnum() {
		if(this.defaultTipologiaRicerca != null) {
			return this.defaultTipologiaRicerca;
		} else {
			if(this.isTipologiaRicercaEntrambiEnabled()) 
				return TipologiaRicerca.all;
			else 
				return TipologiaRicerca.ingresso;
		}
	}
	
	public void setDefaultTipologiaRicerca(TipologiaRicerca defaultTipologiaRicerca) {
		this.defaultTipologiaRicerca = defaultTipologiaRicerca;
	}

	public void setDefaultTipologiaRicerca(String defaultTipologiaRicerca) {
		if (StringUtils.isEmpty(defaultTipologiaRicerca) || "--".equals(defaultTipologiaRicerca))
			this.defaultTipologiaRicerca = null;
		else 
			this.setDefaultTipologiaRicerca(TipologiaRicerca.valueOf(defaultTipologiaRicerca));
	}

	public PermessiUtenteOperatore getPermessiUtenteOperatore() throws CoreException, UserInvalidException {

		User u = getUser();
		UserDetailsBean user = new UserDetailsBean();
		user.setUtente(u);

		String tipoSoggettoLocale = null;
		String nomeSoggettoLocale = null;

		if(!StringUtils.isEmpty(this.getTipoNomeSoggettoLocale()) && !"--".equals(this.getTipoNomeSoggettoLocale())){
			tipoSoggettoLocale = Utility.parseTipoSoggetto(this.getTipoNomeSoggettoLocale());
			nomeSoggettoLocale = Utility.parseNomeSoggetto(this.getTipoNomeSoggettoLocale());
		}

		return PermessiUtenteOperatore.getPermessiUtenteOperatore(user, tipoSoggettoLocale, nomeSoggettoLocale);

	}
	
	public boolean isMultitenant() {
		return Utility.isMultitenantAbilitato();
	}

	public String getTipoTrafficoPerSoggetto() {
		return Utility.parseTipoSoggetto(this.tipoNomeTrafficoPerSoggetto);
	}

	public String getTrafficoPerSoggetto() {
		return Utility.parseNomeSoggetto(this.tipoNomeTrafficoPerSoggetto);
	}

	public void setTipoNomeTrafficoPerSoggetto(String trafficoPerSoggetto) {
		this.tipoNomeTrafficoPerSoggetto = trafficoPerSoggetto;
		if (StringUtils.isEmpty(trafficoPerSoggetto)
				|| "--".equals(trafficoPerSoggetto))
			this.tipoNomeTrafficoPerSoggetto = null;
	}

	public String getTipoSoggettoLocale() {
//		User u = getUser();
//		if (u.getSoggetti().size() == 1) {
//			IDSoggetto s = u.getSoggetti().get(0);
//			this.tipoNomeSoggettoLocale = s.getTipo() + "/" + s.getNome();
//		}

		return Utility.parseTipoSoggetto(this.getTipoNomeSoggettoLocale());
	}

	/**
	 * ritorna il nome del soggetto locale
	 * 
	 * @return Soggetto Locale
	 */
	public String getSoggettoLocale() {
//
//		User u = getUser();
//		if (u.getSoggetti().size() == 1) {
//			IDSoggetto s = u.getSoggetti().get(0);
//			this.tipoNomeSoggettoLocale = s.getTipo() + "/" + s.getNome();
//		}

		return Utility.parseNomeSoggetto(this.getTipoNomeSoggettoLocale());
	}

	public void setTipoNomeSoggettoLocale(String soggettoLocale) {
		this.tipoNomeSoggettoLocale = soggettoLocale;

		if (StringUtils.isEmpty(soggettoLocale) || "--".equals(soggettoLocale)) {
			this.tipoNomeSoggettoLocale = null;
		}
	}

	/**
	 * I nomi spcoop dei soggetti associati all'utente loggato
	 */
	public List<Soggetto> getSoggettiGestione() {
		User u = getUser();
		return Utility.getSoggettiGestione(u,this.getTipoNomeSoggettoLocale());
	}

	// public void setSoggettoGestione(String soggettoGestione) {
	//
	// this.soggettoGestione = soggettoGestione;
	//
	// //l'identificativo porta dipende dal soggetto selezionato
	// this.identificativoPorta = this.soggettoGestione+"SPCoopIT";
	//
	// if(StringUtils.isEmpty(soggettoGestione) ||
	// "--".equals(soggettoGestione)){
	// this.soggettoGestione = null;
	// this.identificativoPorta = null;
	// }
	// }

	public Integer getEsitoGruppo() {
		return this.esitoGruppo;
	}

	public void setEsitoGruppo(Integer esito) {
		this.esitoGruppo = esito;
		this.checkDettaglio();
		this.checkDettaglioPersonalizzato();
	}

	public Integer getEsitoDettaglio() {
		return this.esitoDettaglio;
	}

	public void setEsitoDettaglio(Integer esito) {
		this.esitoDettaglio = esito;
		this.checkDettaglio();
	}

	public Integer[] getEsitoDettaglioPersonalizzato() {
		return this.esitoDettaglioPersonalizzato;
	}

	public void setEsitoDettaglioPersonalizzato(Integer[] esitoDettaglioPersonalizzato) {
		this.esitoDettaglioPersonalizzato = esitoDettaglioPersonalizzato;
	}

	public boolean isShowRichiesteScartate() {
		if(EsitoUtils.ALL_VALUE == this.esitoGruppo ||
				EsitoUtils.ALL_ERROR_VALUE == this.esitoGruppo ||
				EsitoUtils.ALL_ERROR_FAULT_APPLICATIVO_VALUE == this.esitoGruppo 
				//||
				//EsitoUtils.ALL_PERSONALIZZATO_VALUE == this.esitoGruppo
				){
			return true;
		}
		return false;
	}	
	
	private void checkDettaglio(){
		if(EsitoUtils.ALL_VALUE != this.esitoDettaglio){
			// devo verificare il dettaglio che sia compatibile con il nuovo esito
			if(EsitoUtils.ALL_VALUE != this.esitoGruppo){
				try{
					EsitiProperties esitiProperties = EsitiProperties.getInstance(BaseSearchForm.log, this.protocollo);
					List<Integer> codes = null;
					if(EsitoUtils.ALL_ERROR_VALUE == this.esitoGruppo){
						codes = esitiProperties.getEsitiCodeKo_senzaFaultApplicativo();
					}
					else if(EsitoUtils.ALL_FAULT_APPLICATIVO_VALUE == this.esitoGruppo){
						codes = esitiProperties.getEsitiCodeFaultApplicativo();
					}
					else if(EsitoUtils.ALL_OK_VALUE == this.esitoGruppo){
						codes = esitiProperties.getEsitiCodeOk_senzaFaultApplicativo();
					}
					else if(EsitoUtils.ALL_PERSONALIZZATO_VALUE == this.esitoGruppo){
						this.esitoDettaglio = EsitoUtils.ALL_VALUE;
					}
					else if(EsitoUtils.ALL_ERROR_FAULT_APPLICATIVO_VALUE == this.esitoGruppo){
						codes = esitiProperties.getEsitiCodeKo();
						codes.addAll(esitiProperties.getEsitiCodeFaultApplicativo());
					}
					else if(EsitoUtils.ALL_ERROR_CONSEGNA_VALUE == this.esitoGruppo){
						codes = esitiProperties.getEsitiCodeErroriConsegna();
					}
					else if(EsitoUtils.ALL_ERROR_RICHIESTE_SCARTATE_VALUE == this.esitoGruppo){
						codes = esitiProperties.getEsitiCodeRichiestaScartate();
					}
					
					if(this.escludiRichiesteScartate){
						List<Integer> escludiEsitiRichiesteMalformate = esitiProperties.getEsitiCodeRichiestaScartate();
						boolean found = false;
						if(escludiEsitiRichiesteMalformate!=null) {
							for (Integer code : escludiEsitiRichiesteMalformate) {
								if(code == this.esitoDettaglio){
									found = true;
									break;
								}
							}
						}
						if(found){
							this.esitoDettaglio = EsitoUtils.ALL_VALUE;
						}
					}
					
					
					boolean found = false;
					if(codes!=null) {
						for (Integer code : codes) {
							if(code == this.esitoDettaglio){
								found = true;
								break;
							}
						}
					}
					if(!found){
						this.esitoDettaglio = EsitoUtils.ALL_VALUE;
					}
				}catch(Exception e){
					this.esitoDettaglio = EsitoUtils.ALL_VALUE;
					BaseSearchForm.log.error("Errore durante il controllo della compatibilità del dettaglio esito "+e.getMessage(),e);
				}
			}
			else {
				if(this.escludiRichiesteScartate){
					try{
						EsitiProperties esitiProperties = EsitiProperties.getInstance(BaseSearchForm.log, this.protocollo);
						List<Integer> escludiEsitiRichiesteMalformate = esitiProperties.getEsitiCodeRichiestaScartate();
						boolean found = false;
						if(escludiEsitiRichiesteMalformate!=null) {
							for (Integer code : escludiEsitiRichiesteMalformate) {
								if(code == this.esitoDettaglio){
									found = true;
									break;
								}
							}
						}
						if(found){
							this.esitoDettaglio = EsitoUtils.ALL_VALUE;
						}
					}catch(Exception e){
						this.esitoDettaglio = EsitoUtils.ALL_VALUE;
						BaseSearchForm.log.error("Errore durante il controllo della compatibilità del dettaglio esito "+e.getMessage(),e);
					}
				}
			}
		}
	}
	private void checkDettaglioPersonalizzato(){
		// Rendere null l'oggetto rompe il componente
		/*if(this.esitoDettaglioPersonalizzato!=null && this.esitoDettaglioPersonalizzato.length>0){
			if(EsitoUtils.ALL_PERSONALIZZATO_VALUE != this.esitoGruppo){
				this.esitoDettaglioPersonalizzato=null;
			}
		}*/
	}

	public String getEsitoContesto() {
		return this.esitoContesto;
	}

	public void setEsitoContesto(String esitoContesto) {
		this.esitoContesto = esitoContesto;
	}

	public boolean isEscludiRichiesteScartate() {
		return this.escludiRichiesteScartate;
	}

	public void setEscludiRichiesteScartate(boolean escludiRichiesteScartate) {
		this.escludiRichiesteScartate = escludiRichiesteScartate;
		this.checkDettaglio();
	}
	
	public String getTipoRicercaSPCoop() {
		return this.tipoRicercaSPCoop;
	}

	public void setTipoRicercaSPCoop(String tipoRicercaSPCoop) {
		this.tipoRicercaSPCoop = tipoRicercaSPCoop;
	}

	public Boolean getTipologiaTransazioneSPCoop() {
		if ("spcoop".equals(this.tipoRicercaSPCoop))
			return true;
		if ("im".equals(this.tipoRicercaSPCoop))
			return false;
		return null;// entrambe
	}

	public String getServizioApplicativo() {
		return this.servizioApplicativo;
	}

	public void setServizioApplicativo(String servizioApplicativo) {
		this.servizioApplicativo = servizioApplicativo;

		if (StringUtils.isEmpty(servizioApplicativo)
				|| "--".equals(servizioApplicativo))
			this.servizioApplicativo = null;
	}

	public String getNomeRicercaPersonalizzata() {
		return this.nomeRicercaPersonalizzata;
	}

	public void setNomeRicercaPersonalizzata(String nomeRicercaPersonalizzata) {
		if ("--".equals(nomeRicercaPersonalizzata))
			nomeRicercaPersonalizzata = null;

		this.nomeRicercaPersonalizzata = nomeRicercaPersonalizzata;
	}

	public Ricerche getRicerchePersonalizzate() {
		return this.ricerchePersonalizzate;
	}

	public void setRicerchePersonalizzate(Ricerche ricerchePersonalizzate) {
		this.ricerchePersonalizzate = ricerchePersonalizzate;
	}

	public ConfigurazioneRicerca getRicercaSelezionata() {
		return this.ricercaSelezionata;
	}

	public void ricercaSelezionataListener(ActionEvent ae) {

		this.ricercaSelezionata = this.ricerchePersonalizzate
				.getRicercaByLabel(this.nomeRicercaPersonalizzata);
	}

	protected boolean validateForm() {	
		return validateForm(false);
	}

	protected boolean validateForm(boolean statsSASValidazioneDatiMittente) {
		// Tipo di periodo selezionato 'Personalizzato'
		if(this.getPeriodo().equals("Personalizzato")){
			if(this.getDataInizio() == null){
				MessageUtils.addErrorMsg("Selezionare Data Inizio");
				return false;
			}

			if(this.getDataFine() == null){
				MessageUtils.addErrorMsg("Selezionare Data Fine");
				return false;
			}
		}
		
		if(!statsSASValidazioneDatiMittente) {
			boolean sezioneDatiMittente = this.validaSezioneDatiMittente();
			
			if(!sezioneDatiMittente)
				return false;
		} else {
			boolean sezioneDatiMittente = this.validaSezioneDatiMittenteCustom();
			
			if(!sezioneDatiMittente)
				return false;
		}

		if(this.esitoGruppo!=null && (EsitoUtils.ALL_PERSONALIZZATO_VALUE == this.esitoGruppo)){
			if(this.esitoDettaglioPersonalizzato==null || this.esitoDettaglioPersonalizzato.length<=0){
				MessageUtils.addErrorMsg("Selezionare almeno un esito di dettaglio");
				return false;
			}
		}
		
		return true;
	}
	
	public String validaSezioneDatiMittenteAction() {
		validaSezioneDatiMittente();
		return null;
	}
	
	public String validaSezioneDatiMittenteCustomAction() {
		validaSezioneDatiMittenteCustom();
		return null;
	}
	
	public boolean validaSezioneDatiMittente() {
		if(StringUtils.isNotEmpty(this.getRiconoscimento())) {
			if(this.getRiconoscimento().equals(Costanti.VALUE_TIPO_RICONOSCIMENTO_SOGGETTO)) {
				if (StringUtils.isEmpty(this.getTipoNomeMittente())) {
					MessageUtils.addErrorMsg("Indicare un Soggetto");
					return false;
				}
			} else if(this.getRiconoscimento().equals(Costanti.VALUE_TIPO_RICONOSCIMENTO_APPLICATIVO)) {
				if (StringUtils.isEmpty(this.getServizioApplicativo())) {
					MessageUtils.addErrorMsg("Indicare un Applicativo");
					return false;
				}
			} else if(this.getRiconoscimento().equals(Costanti.VALUE_TIPO_RICONOSCIMENTO_IDENTIFICATIVO_AUTENTICATO)) {
				if (StringUtils.isEmpty(this.getAutenticazione())) {
					MessageUtils.addErrorMsg("Indicare un'Autenicazione");
					return false;
				}
				
				if (StringUtils.isEmpty(this.getValoreRiconoscimento())) {
					MessageUtils.addErrorMsg("Indicare un Identificativo");
					return false;
				}
				
				// controllo sul input di tipo subject
				TipoMatch match = TipoMatch.valueOf(this.getMittenteMatchingType());
				boolean ricercaEsatta = TipoMatch.EQUALS.equals(match);
				if(TipoAutenticazione.SSL.getValue().equalsIgnoreCase(this.getAutenticazione()) && ricercaEsatta) {
					try {
						CertificateUtils.validaPrincipal(this.getValoreRiconoscimento(), PrincipalType.subject);
					} catch (UtilsException e) {
						MessageUtils.addErrorMsg("Indicare un Subject corretto");
						return false;
					}
				}
				
				
			} 
			else if(this.getRiconoscimento().equals(Costanti.VALUE_TIPO_RICONOSCIMENTO_INDIRIZZO_IP)) {
				/*if (StringUtils.isEmpty(this.getClientAddressMode())) {
					MessageUtils.addErrorMsg("Indicare una Modalità");
					return false;
				}*/
				
				if (StringUtils.isEmpty(this.getValoreRiconoscimento())) {
					MessageUtils.addErrorMsg("Indicare un Indirizzo");
					return false;
				}
			}
			else { // token_info
				if (StringUtils.isEmpty(this.getTokenClaim())) {
					MessageUtils.addErrorMsg("Indicare un Claim");
					return false;
				}
				
				if (StringUtils.isEmpty(this.getValoreRiconoscimento())) {
					MessageUtils.addErrorMsg("Indicare un Valore");
					return false;
				}
			}
		}
		
		return true;
	}
	
	public boolean validaSezioneDatiMittenteCustom() {
		return true;
	}

	@Override
	protected String eseguiFiltra() {
		if(validateForm()==false) {
			return null;
		}

		return null;
	}

	public void setFiltro(IFilter filtro) {
		this.filtro = filtro;
	}

	public IFilter getFiltro() {
		return this.filtro;
	}

	public Statistiche getStatistichePersonalizzate() {
		return this.statistichePersonalizzate;
	}

	public ConfigurazioneStatistica getStatisticaSelezionata() {
		return this.statisticaSelezionata;
	}

	public void setStatistichePersonalizzate(
			Statistiche statistichePersonalizzate) {
		this.statistichePersonalizzate = statistichePersonalizzate;
	}

	public void setStatisticaSelezionata(
			ConfigurazioneStatistica statisticaSelezionata) {
		this.statisticaSelezionata = statisticaSelezionata;
	}

	public String getNomeStatisticaPersonalizzata() {
		return this.nomeStatisticaPersonalizzata;
	}

	public void setNomeStatisticaPersonalizzata(
			String nomeStatisticaPersonalizzata) {

		if ("--".equals(nomeStatisticaPersonalizzata))
			nomeStatisticaPersonalizzata = null;

		this.nomeStatisticaPersonalizzata = nomeStatisticaPersonalizzata;
	}

	public void statisticaSelezionataListener(ActionEvent ae) {


		this.statisticaSelezionata = this.statistichePersonalizzate
				.getStatisticaByLabel(this.nomeStatisticaPersonalizzata);
	}

	public void setRicercaSelezionata(ConfigurazioneRicerca ricercaSelezionata) {
		this.ricercaSelezionata = ricercaSelezionata;
	}

	public String getIdCorrelazioneApplicativa() {
		return this.idCorrelazioneApplicativa;
	}

	public void setIdCorrelazioneApplicativa(String idCorrelazioneApplicativa) {
		this.idCorrelazioneApplicativa = idCorrelazioneApplicativa;
		if (StringUtils.isEmpty(this.idCorrelazioneApplicativa)
				|| "--".equals(this.idCorrelazioneApplicativa))
			this.idCorrelazioneApplicativa = null;
	}

	public String getIdEgov() {
		return this.idEgov;
	}

	public void setIdEgov(String idEgov) {
		this.idEgov = idEgov;
	}

	public String getTipoIdMessaggio() {
		if(this.tipoIdMessaggio==null){
			this.tipoIdMessaggio = TipoMessaggio.Richiesta.name();
		}
		return this.tipoIdMessaggio;
	}

	public void setTipoIdMessaggio(String tipoIdMessaggio) {
		this.tipoIdMessaggio = tipoIdMessaggio;
	}

	public String getIdTransazione() {
		return this.idTransazione;
	}

	public void setIdTransazione(String idTransazione) {
		this.idTransazione = idTransazione;
	}

	public List<SelectItem> getEsitiGruppo() {
		ArrayList<SelectItem> list = new ArrayList<SelectItem>();
		try{
			EsitoUtils esitoUtils = new EsitoUtils(BaseSearchForm.log, this.protocollo);
			list.add(new SelectItem(EsitoUtils.ALL_VALUE,esitoUtils.getEsitoLabelFromValue(EsitoUtils.ALL_VALUE,false)));
			list.add(new SelectItem(EsitoUtils.ALL_ERROR_VALUE,esitoUtils.getEsitoLabelFromValue(EsitoUtils.ALL_ERROR_VALUE,false)));
			list.add(new SelectItem(EsitoUtils.ALL_FAULT_APPLICATIVO_VALUE,esitoUtils.getEsitoLabelFromValue(EsitoUtils.ALL_FAULT_APPLICATIVO_VALUE,false)));
			list.add(new SelectItem(EsitoUtils.ALL_ERROR_FAULT_APPLICATIVO_VALUE,esitoUtils.getEsitoLabelFromValue(EsitoUtils.ALL_ERROR_FAULT_APPLICATIVO_VALUE,false)));
			list.add(new SelectItem(EsitoUtils.ALL_ERROR_CONSEGNA_VALUE,esitoUtils.getEsitoLabelFromValue(EsitoUtils.ALL_ERROR_CONSEGNA_VALUE,false)));
			list.add(new SelectItem(EsitoUtils.ALL_ERROR_RICHIESTE_SCARTATE_VALUE,esitoUtils.getEsitoLabelFromValue(EsitoUtils.ALL_ERROR_RICHIESTE_SCARTATE_VALUE,false)));
			list.add(new SelectItem(EsitoUtils.ALL_OK_VALUE,esitoUtils.getEsitoLabelFromValue(EsitoUtils.ALL_OK_VALUE,false)));
			list.add(new SelectItem(EsitoUtils.ALL_PERSONALIZZATO_VALUE,esitoUtils.getEsitoLabelFromValue(EsitoUtils.ALL_PERSONALIZZATO_VALUE,false)));
	
			return list;
		}catch(Exception e){
			BaseSearchForm.log.error("Errore durante il recupero della lista dei gruppi di esito "+e.getMessage(),e);
			throw new RuntimeException(e.getMessage(),e);
		}
	}

	public boolean isShowDettaglioPersonalizzato(){
		return this.esitoGruppo!=null &&  (EsitoUtils.ALL_PERSONALIZZATO_VALUE == this.esitoGruppo);
	}
	public boolean isShowDettaglio(){
		return this.esitoGruppo!=null &&  (EsitoUtils.ALL_FAULT_APPLICATIVO_VALUE != this.esitoGruppo) && !this.isShowDettaglioPersonalizzato();
	}

	public List<SelectItem> getEsitiDettaglio(boolean statistiche) {
		ArrayList<SelectItem> list = new ArrayList<SelectItem>();

		try{
			EsitoUtils esitoUtils = new EsitoUtils(BaseSearchForm.log, this.protocollo);
			
			list.add(new SelectItem(EsitoUtils.ALL_VALUE,esitoUtils.getEsitoLabelFromValue(EsitoUtils.ALL_VALUE, statistiche)));

			EsitiProperties esitiProperties = EsitiProperties.getInstance(BaseSearchForm.log, this.protocollo);

			List<Integer> esiti = esitiProperties.getEsitiCodeOrderLabel();

			List<Integer> esitiFiltro = null;
			if(EsitoUtils.ALL_OK_VALUE == this.esitoGruppo){
				esitiFiltro = esitiProperties.getEsitiCodeOk_senzaFaultApplicativo();
			}
			else if(EsitoUtils.ALL_ERROR_VALUE == this.esitoGruppo){
				esitiFiltro = esitiProperties.getEsitiCodeKo_senzaFaultApplicativo();
			}
			else if(EsitoUtils.ALL_ERROR_FAULT_APPLICATIVO_VALUE == this.esitoGruppo){
				esitiFiltro = esitiProperties.getEsitiCodeKo();
			}
			else if(EsitoUtils.ALL_ERROR_CONSEGNA_VALUE == this.esitoGruppo){
				esitiFiltro = esitiProperties.getEsitiCodeErroriConsegna();
			}
			else if(EsitoUtils.ALL_ERROR_RICHIESTE_SCARTATE_VALUE == this.esitoGruppo){
				esitiFiltro = esitiProperties.getEsitiCodeRichiestaScartate();
			}
			
			List<Integer> escludiEsiti = null;
			
			if(this.escludiRichiesteScartate && (EsitoUtils.ALL_ERROR_RICHIESTE_SCARTATE_VALUE != this.esitoGruppo)){
				List<Integer> escludiEsiti_tmp = esitiProperties.getEsitiCodeRichiestaScartate();
				if(escludiEsiti_tmp!=null && !escludiEsiti_tmp.isEmpty()) {
					escludiEsiti = new ArrayList<Integer>();
					escludiEsiti.addAll(escludiEsiti_tmp);
				}
			}

			if(!this.isSearchFormEsitoConsegnaMultiplaEnabled) {
				if(escludiEsiti==null) {
					escludiEsiti = new ArrayList<Integer>();
				}
				escludiEsiti.add(esitiProperties.convertoToCode(EsitoTransazioneName.CONSEGNA_MULTIPLA));
				escludiEsiti.add(esitiProperties.convertoToCode(EsitoTransazioneName.CONSEGNA_MULTIPLA_COMPLETATA));
				escludiEsiti.add(esitiProperties.convertoToCode(EsitoTransazioneName.CONSEGNA_MULTIPLA_FALLITA));
			}
			
			
			for (Integer esito : esiti) {

				if(esitiFiltro!=null){
					boolean found = false;
					for (Integer esitoFiltro : esitiFiltro) {
						if(esitoFiltro.intValue() == esito.intValue()){
							found = true;
							break;
						}
					}
					if(!found){
						continue;
					}
				}
				
				if(escludiEsiti!=null) {
					boolean found = false;
					for (Integer checkEsito : escludiEsiti) {
						if(checkEsito.intValue() == esito.intValue()){
							found = true;
							break;
						}
					}
					if(found){
						continue;
					}
				}
				
				String name = esitiProperties.getEsitoName(esito);
				EsitoTransazioneName esitoTransactionName = EsitoTransazioneName.convertoTo(name);

				if(statistiche && EsitoTransazioneName.isStatiConsegnaMultipla(esitoTransactionName)) {
					continue;
				}
				
				SelectItem si = new SelectItem(esito,esitoUtils.getEsitoLabelFromValue(esito, statistiche));

				boolean pddSpecific = EsitoTransazioneName.isPddSpecific(esitoTransactionName);
				boolean integrationManagerSpecific = EsitoTransazioneName.isIntegrationManagerSpecific(esitoTransactionName);				
				if ("spcoop".equals(this.tipoRicercaSPCoop)) {
					if(pddSpecific || !integrationManagerSpecific){
						list.add(si);
					}
				} else if ("im".equals(this.tipoRicercaSPCoop)) {
					if(integrationManagerSpecific || !pddSpecific){
						list.add(si);
					}
				} else{
					list.add(si);
				}
			}

		}catch(Exception e){
			BaseSearchForm.log.error("Errore durante il recupero della lista del dettaglio esito "+e.getMessage(),e);
			throw new RuntimeException(e.getMessage(),e);
		}

		return list;
	}

	public List<SelectItem> getEsitiDettagliPersonalizzati(boolean statistiche) {
		try{
			ArrayList<SelectItem> list = new ArrayList<SelectItem>();

			EsitiProperties esitiProperties = EsitiProperties.getInstance(BaseSearchForm.log, this.protocollo);

			List<Integer> esiti = esitiProperties.getEsitiCodeOrderLabel();

			for (Integer esito : esiti) {

				String name = esitiProperties.getEsitoName(esito);
				EsitoTransazioneName esitoTransactionName = EsitoTransazioneName.convertoTo(name);

				if(!this.isSearchFormEsitoConsegnaMultiplaEnabled && EsitoTransazioneName.isConsegnaMultipla(esitoTransactionName)) {
					continue;
				}
				if(statistiche && EsitoTransazioneName.isStatiConsegnaMultipla(esitoTransactionName)) {
					continue;
				}
				
				SelectItem si = new SelectItem(esito.intValue(),esitiProperties.getEsitoLabel(esito));

				boolean pddSpecific = EsitoTransazioneName.isPddSpecific(esitoTransactionName);
				boolean integrationManagerSpecific = EsitoTransazioneName.isIntegrationManagerSpecific(esitoTransactionName);				
				if ("spcoop".equals(this.tipoRicercaSPCoop)) {
					if(pddSpecific || !integrationManagerSpecific){
						list.add(si);
					}
				} else if ("im".equals(this.tipoRicercaSPCoop)) {
					if(integrationManagerSpecific || !pddSpecific){
						list.add(si);
					}
				} else{
					list.add(si);
				}
			}

			return list;
		}catch(Exception e){
			BaseSearchForm.log.error("Errore durante il recupero della lista del dettaglio esito personalizzato "+e.getMessage(),e);
			throw new RuntimeException(e.getMessage(),e);
		}
	}

	public boolean isShowEsitiContesto(){
		return this.getEsitiContesto().size()>2;
	}

	public List<SelectItem> getEsitiContesto() {
		ArrayList<SelectItem> list = new ArrayList<SelectItem>();

		try{

			EsitoUtils esitoUtils = new EsitoUtils(BaseSearchForm.log, this.protocollo);
			
			list.add(new SelectItem(EsitoUtils.ALL_VALUE_AS_STRING,esitoUtils.getEsitoContestoLabelFromValue(EsitoUtils.ALL_VALUE_AS_STRING)));
			
			EsitiProperties esitiProperties = EsitiProperties.getInstance(BaseSearchForm.log, this.protocollo);

			List<String> esiti = esitiProperties.getEsitiTransactionContextCodeOrderLabel();
			for (String esito : esiti) {

				SelectItem si = new SelectItem(esito,esitoUtils.getEsitoContestoLabelFromValue(esito));

				list.add(si);
			}

		}catch(Exception e){
			throw new RuntimeException(e.getMessage(),e);
		}

		return list;
	}

	public List<SelectItem> getTipiIdMessaggio() {
		ArrayList<SelectItem> list = new ArrayList<SelectItem>();
		list.add(new SelectItem(TipoMessaggio.Richiesta.name(),TipoMessaggio.Richiesta.getLabel()));
		list.add(new SelectItem(TipoMessaggio.Risposta.name(),TipoMessaggio.Risposta.getLabel()));
		list.add(new SelectItem(TipoMessaggio.Collaborazione.name(),TipoMessaggio.Collaborazione.getLabel()));
		list.add(new SelectItem(TipoMessaggio.RiferimentoRichiesta.name(),TipoMessaggio.RiferimentoRichiesta.getLabel()));
		return list;
	}

	//	public OrderBy getOrderBy() {
	//		return this.orderBy;
	//	}
	//
	//	public void setOrderBy(OrderBy orderBy) {
	//		this.orderBy = orderBy;
	//	}

	public String getTipoNomeMittente() {
		return this.tipoNomeMittente;
	}

	public String getTipoNomeDestinatario() {
		return this.tipoNomeDestinatario;
	}

	public String getTipoNomeTrafficoPerSoggetto() {
		return this.tipoNomeTrafficoPerSoggetto;
	}
	
	public String getTipoNomeSoggettoLocale() {
		if(this.isCloned) {
			return this.tipoNomeSoggettoLocaleCloned;
		}
		else {
			if(!this.getSoggettoPddMonitor().equals(Costanti.VALUE_PARAMETRO_MODALITA_ALL)) {
				this.setTipoNomeSoggettoLocale(Utility.getLoginBean().getSoggettoPddMonitor()); 
			}
			
			return this.tipoNomeSoggettoLocale;
		}
	}
	
	public String getTipoNomeMittenteTooltip() {
		if(StringUtils.isNotEmpty(this.tipoNomeMittente)) {
			try {
				String tipoProtocollo = this.getProtocollo();
				IDSoggetto idSoggetto = new IDSoggetto();
				idSoggetto.setTipo(this.getTipoMittente());
				idSoggetto.setNome(this.getNomeMittente());
				String label = NamingUtils.getLabelSoggetto(tipoProtocollo, idSoggetto );
				
				return label;
			} catch (Exception e) {
				BaseSearchForm.log.error(e.getMessage(), e);
			}
		}
		
		return this.tipoNomeMittente;
	}

	public String getTipoNomeDestinatarioTooltip() {
		if(StringUtils.isNotEmpty(this.tipoNomeDestinatario)) {
			try {
				String tipoProtocollo = this.getProtocollo();
				IDSoggetto idSoggetto = new IDSoggetto();
				idSoggetto.setTipo(this.getTipoDestinatario());
				idSoggetto.setNome(this.getNomeDestinatario());
				String label = NamingUtils.getLabelSoggetto(tipoProtocollo, idSoggetto );
				
				return label;
			} catch (Exception e) {
				BaseSearchForm.log.error(e.getMessage(), e);
			}
		}
		
		return this.tipoNomeDestinatario;
	}

	public String getTipoNomeTrafficoPerSoggettoTooltip() {
		if(StringUtils.isNotEmpty(this.tipoNomeTrafficoPerSoggetto)) {
			try {
				String tipoProtocollo = this.getProtocollo();
				IDSoggetto idSoggetto = new IDSoggetto();
				idSoggetto.setTipo(this.getTipoTrafficoPerSoggetto());
				idSoggetto.setNome(this.getTrafficoPerSoggetto());
				String label = NamingUtils.getLabelSoggetto(tipoProtocollo, idSoggetto );
				
				return label;
			} catch (Exception e) {
				BaseSearchForm.log.error(e.getMessage(), e);
			}
		}
		return this.tipoNomeTrafficoPerSoggetto;
	}

	public String getTipoNomeSoggettoLocaleTooltip() {
		if(StringUtils.isNotEmpty(this.getTipoNomeSoggettoLocale())) {
			try {
				String tipoProtocollo = this.getProtocollo();
				IDSoggetto idSoggetto = new IDSoggetto();
				idSoggetto.setTipo(this.getTipoSoggettoLocale());
				idSoggetto.setNome(this.getSoggettoLocale());
				String label = NamingUtils.getLabelSoggetto(tipoProtocollo, idSoggetto );
				
				return label;
			} catch (Exception e) {
				BaseSearchForm.log.error(e.getMessage(), e);
			}
		}
		return this.getTipoNomeSoggettoLocale();
	}

	public String getIntervalloRefresh() {
		return this.intervalloRefresh;
	}

	public void setIntervalloRefresh(String intervalloRefresh) {
		this.intervalloRefresh = intervalloRefresh;
	}

	public boolean isRicerchePersonalizzateAttive() {
		return this.ricerchePersonalizzateAttive;
	}

	public void setRicerchePersonalizzateAttive(boolean ricerchePersonalizzateAttive) {
		this.ricerchePersonalizzateAttive = ricerchePersonalizzateAttive;
	}

	public boolean isStatistichePersonalizzateAttive() {
		return this.statistichePersonalizzateAttive;
	}

	public void setStatistichePersonalizzateAttive(
			boolean statistichePersonalizzateAttive) {
		this.statistichePersonalizzateAttive = statistichePersonalizzateAttive;
	}

	public SortOrder getSortOrder() {
		return this.sortOrder;
	}

	public void setSortOrder(SortOrder sortOrder) {
		this.sortOrder = sortOrder;
	}

	public String getSortField() {
		return this.sortField;
	}

	public void setSortField(String sortField) {
		this.sortField = sortField;
	}
	
	public String getModalita() {
		if(this.modalita == null)
			return Utility.getLoginBean().getModalita();
		
		return this.modalita;
	}

	public void setModalita(String modalita) {
		this.modalita = modalita;
	}

	private boolean isCloned = false;
	private String protocolCloned = null;
	private String tipoNomeSoggettoLocaleCloned = null;
	public void saveProtocollo() {
		this.protocolCloned = this.protocollo;
		this.tipoNomeSoggettoLocaleCloned = this.tipoNomeSoggettoLocale;
		this.isCloned = true;
	}
	
	public String getProtocollo() {
		if(this.isCloned) {
			return this.protocolCloned;
		}
		else {
			if(!this.getModalita().equals(Costanti.VALUE_PARAMETRO_MODALITA_ALL)) {
				this.setProtocollo(Utility.getLoginBean().getModalita()); 
			}
			
			return this.protocollo;
		}
	}

	public void setProtocollo(String protocollo) {
		this.protocollo = protocollo;

		if (StringUtils.isEmpty(protocollo)
				|| "*".equals(protocollo))
			this.protocollo = null;
	}

	public List<SelectItem> getProtocolli() throws Exception {
		//		if(this.protocolli == null)
		this.protocolli = new ArrayList<SelectItem>();
//		this.protocolli.add(new SelectItem("*",AllConverter.ALL_STRING));
		try {
			ProtocolFactoryManager pfManager = org.openspcoop2.protocol.engine.ProtocolFactoryManager.getInstance();
			MapReader<String,IProtocolFactory<?>> protocolFactories = pfManager.getProtocolFactories();	

			List<String> listaNomiProtocolli = Utility.getProtocolli(this.getUser(), pfManager, protocolFactories, true);

			for (String protocolKey : listaNomiProtocolli) {
				IProtocolFactory<?> protocollo = protocolFactories.get(protocolKey);
				this.protocolli.add(new SelectItem(protocollo.getProtocol(),NamingUtils.getLabelProtocollo(protocollo.getProtocol())));
			}

		} catch (ProtocolException e) {
			BaseSearchForm.log.error("Si e' verificato un errore durante il caricamento della lista protocolli: " + e.getMessage(), e);
		}  


		return this.protocolli;
	}


	public boolean isShowListaProtocolli(){
		try {
			ProtocolFactoryManager pfManager = org.openspcoop2.protocol.engine.ProtocolFactoryManager.getInstance();
			MapReader<String, IProtocolFactory<?>> protocolFactories = pfManager.getProtocolFactories();	
			int numeroProtocolli = protocolFactories.size();

			// se c'e' installato un solo protocollo non visualizzo la select List.
			if(numeroProtocolli == 1)
				return false;
			
			
			User utente = this.getUser();
			
			// utente ha selezionato una modalita'
			if(utente.getProtocolloSelezionatoPddMonitor()!=null) {
				return false;
			}
			
			
			if(utente.getProtocolliSupportati() !=null && utente.getProtocolliSupportati().size() <= 1) {
				return false;
			}

			List<String> listaNomiProtocolli = Utility.getProtocolli(this.getUser(), pfManager, protocolFactories, true);

			numeroProtocolli = listaNomiProtocolli.size();

			// se c'e' installato un solo protocollo non visualizzo la select List.
			if(numeroProtocolli == 1)
				return false;

		} catch (Exception e) {
			BaseSearchForm.log.error("Si e' verificato un errore durante il caricamento della lista protocolli: " + e.getMessage(), e);
		}  

		return true;
	}
	
	public String getSoggettoPddMonitor() {
		return getSoggettoPddMonitor(true);
	}
	
	public String getSoggettoPddMonitor(boolean checkLoginBean) {
		if(this.soggettoPddMonitor == null && checkLoginBean) {
			if(this.checkSoggettoPddMonitor) { // controllo che serve per le api rs.
				return Utility.getLoginBean().getSoggettoPddMonitor();
			}
		}
		
		return this.soggettoPddMonitor;
	}

	public void setSoggettoPddMonitor(String soggettoPddMonitor) {
		this.soggettoPddMonitor = soggettoPddMonitor;
	}

	public void setCheckSoggettoPddMonitor(boolean checkSoggettoPddMonitor) {
		this.checkSoggettoPddMonitor = checkSoggettoPddMonitor;
	}
	
	public boolean isShowFiltroSoggettoLocale(){
		LoginBean lb = Utility.getLoginBean();
		if(lb==null) {
			return true;
		}
		return lb.isShowFiltroSoggettoLocale();
	}
	
	public boolean isSetFiltroProtocollo() {
		boolean setFilter = StringUtils.isNotEmpty(this.getProtocollo()) ;
//		&& (this.isShowListaProtocolli() || !Utility.getLoginBean().getModalita().equals(Costanti.VALUE_PARAMETRO_MODALITA_ALL));
		
		return setFilter;
	}

	public void protocolloSelected(ActionEvent ae) {
		String tipoProt = this.getProtocollo();

		try{
			if(tipoProt!= null){
				// Controllo Destinatario
				String tipoErogatore = Utility.parseTipoSoggetto(this.getTipoNomeDestinatario());
				String nomeErogatore = Utility.parseNomeSoggetto(this.getTipoNomeDestinatario());

				if(nomeErogatore != null){
					if(!DynamicPdDBeanUtils.getInstance(BaseSearchForm.log).isTipoSoggettoCompatibileConProtocollo(tipoErogatore, tipoProt)){ 
						this.setTipoNomeDestinatario(null);
						this.setLabelTipoNomeDestinatario(null);
						this.destinatarioSelected(ae);
					}
				}

				// controllo soggetto locale
				String tipoSoggettoLocale = Utility.parseTipoSoggetto(this.getTipoNomeSoggettoLocale());
				String nomeSoggettoLocale = Utility.parseNomeSoggetto(this.getTipoNomeSoggettoLocale());

				if(nomeSoggettoLocale != null){
					if(!DynamicPdDBeanUtils.getInstance(BaseSearchForm.log).isTipoSoggettoCompatibileConProtocollo(tipoSoggettoLocale, tipoProt)){ 
						this.setTipoNomeSoggettoLocale(null);
						this.setLabelTipoNomeSoggettoLocale(null);
						this.soggettoLocaleSelected(ae);
					}
				}

				// controllo getTipoNomeTrafficoPerSoggetto
				String tipoTrafficoPerSoggetto = Utility.parseTipoSoggetto(this.getTipoNomeTrafficoPerSoggetto());
				String nomeTrafficoPerSoggetto = Utility.parseNomeSoggetto(this.getTipoNomeTrafficoPerSoggetto());

				if(nomeTrafficoPerSoggetto != null){
					if(!DynamicPdDBeanUtils.getInstance(BaseSearchForm.log).isTipoSoggettoCompatibileConProtocollo(tipoTrafficoPerSoggetto, tipoProt)){ 
						this.setTipoNomeTrafficoPerSoggetto(null);
						this.setLabelTipoNomeTrafficoPerSoggetto(null); 
						this.destinatarioSelected(ae);
					}
				}

				// Controllo mittente

				String nomeFruitore= Utility.parseTipoSoggetto(this.getTipoNomeMittente());
				String tipoFruitore = Utility.parseNomeSoggetto(this.getTipoNomeMittente());
				if(nomeFruitore!= null)
					if(!DynamicPdDBeanUtils.getInstance(BaseSearchForm.log).isTipoSoggettoCompatibileConProtocollo(tipoFruitore, tipoProt)){
						this.setTipoNomeMittente(null);
						this.setLabelTipoNomeMittente(null);
						this.destinatarioSelected(ae);
					}

				// controllo Servizio
				String nomeServizio = null, tipoServizio = null;
				if(StringUtils.isNotEmpty(this.getNomeServizio())){
					nomeServizio = this.getNomeServizio().split(" \\(")[0];
					tipoServizio = Utility.parseTipoSoggetto(nomeServizio);
					nomeServizio = Utility.parseNomeSoggetto(nomeServizio);
				}

				if(tipoServizio != null)
					if(!DynamicPdDBeanUtils.getInstance(BaseSearchForm.log).isTipoServizioCompatibileConProtocollo(tipoServizio, tipoProt)){
						this.setNomeServizio(null); 
						this.setLabelNomeServizio(null);
						this.servizioSelected(ae);
					}

			}

		}catch(Exception e){
			BaseSearchForm.log.error("Si e' verificato un errore durante la selezione del protocollo:" +e.getMessage(),e);
		}
		//		this.accordoServizio = null;
		//		this.setTipoNomeDestinatario(null);
		//		this.setTipoNomeMittente(null);
		//		accordoServizioSelected(ae);
	}

	public Map<String, Ordering> getSortOrders() {
		return this.sortOrders;
	}

	public void setSortOrders(Map<String, Ordering> sortOrders) {
		this.sortOrders = sortOrders;
	}

	public int getTempoMassimoRefreshLive() {
		return this.tempoMassimoRefreshLive;
	}

	public void setTempoMassimoRefreshLive(int tempoMassimoRefreshLive) {
		this.tempoMassimoRefreshLive = tempoMassimoRefreshLive;
	}

	public boolean isSessioneLiveValida() {
		if(this.getDataRicercaRaw() != null){
			Date d = new Date();
			BaseSearchForm.log.debug("Controllo sessione live Attiva Da: ["+this.getDataRicercaRaw()+"] Tempo Residuo: ["+((this.tempoMassimoRefreshLive * 60000 ) - (d.getTime() - this.getDataRicercaRaw().getTime()))+"] ms"); 

			return this.getDataRicercaRaw().getTime() > (d.getTime() - (this.tempoMassimoRefreshLive * 60000 ));
		}
		return false;
	}

	public void setSessioneLiveValida(boolean sessioneLiveValida) {
		BaseSearchForm.log.debug("setSessioneLiveValida"+sessioneLiveValida+"]");
		if(sessioneLiveValida == true)
			this.aggiornaNuovaDataRicerca();
	}

	public AccordoServizioParteSpecifica getAspsFromNomeServizio(String nomeServizioOrig){
		if(nomeServizioOrig == null)
			return null;
		
		try{
			IDServizio idServizio = Utility.parseServizioSoggetto(nomeServizioOrig);
			return getAspsFromNomeServizio(idServizio);
		}catch(Exception e){
			BaseSearchForm.log.error(e.getMessage(), e);
		}
		return null;
	}
	
	public AccordoServizioParteSpecifica getAspsFromNomeServizio(IDServizio idServizio){
		AccordoServizioParteSpecifica aspsFromValues =  null;
		if(idServizio == null)
			return aspsFromValues;
		
		try{
			String nomeServizio = idServizio.getNome();
			String tipoServizio = idServizio.getTipo();
			String nomeErogatore = idServizio.getSoggettoErogatore().getNome();
			String tipoErogatore = idServizio.getSoggettoErogatore().getTipo();
			Integer versioneServizio = idServizio.getVersione();
	
			aspsFromValues = DynamicPdDBeanUtils.getInstance(BaseSearchForm.log).getAspsFromValues(tipoServizio, nomeServizio, tipoErogatore, nomeErogatore,versioneServizio);
		}catch(Exception e){
			BaseSearchForm.log.error(e.getMessage(), e);
		}
		return aspsFromValues;
	}
	
	public boolean isVisualizzaFiltroSoggettiSelectList() throws Exception{
		return PddMonitorProperties.getInstance(BaseSearchForm.log).isVisualizzaFiltroSoggettiSelectList();
	}

	public boolean isExistsGruppi() throws Exception{
		return DynamicPdDBeanUtils.getInstance(BaseSearchForm.log).existsGruppi();
	}
	
	public boolean isVisualizzaFiltroGruppiSelectList() throws Exception{
		return PddMonitorProperties.getInstance(BaseSearchForm.log).isVisualizzaFiltroGruppiSelectList();
	}
	
	public boolean isVisualizzaFiltroServiziSelectList() throws Exception{
		return PddMonitorProperties.getInstance(BaseSearchForm.log).isVisualizzaFiltroServiziSelectList();
	}
	
	public boolean isVisualizzaFiltroAzioniSelectList() throws Exception{
		return PddMonitorProperties.getInstance(BaseSearchForm.log).isVisualizzaFiltroAzioniSelectList();
	}
	
	private String labelAzione = null;
	
	public String getLabelAzione() {
		if(this.labelAzione != null)
			return this.labelAzione;
		
		if(StringUtils.isNotEmpty(this.getNomeServizio())) {
			try {
				IDServizio idServizio = Utility.parseServizioSoggetto(this.getNomeServizio());
				AccordoServizioParteSpecifica asps = this.getAspsFromNomeServizio(idServizio);
				ServiceBinding serviceBinding = ServiceBinding.toEnumConstant(asps.getIdAccordoServizioParteComune().getServiceBinding());
				this.labelAzione = ServiceBinding.REST.equals(serviceBinding) ? Costanti.LABEL_PARAMETRO_RISORSA : Costanti.LABEL_PARAMETRO_AZIONE;
			} catch (Exception e) {
				BaseSearchForm.log.error(e.getMessage(), e);
				this.labelAzione = Costanti.LABEL_PARAMETRO_AZIONE;
			}
		}
		else 
			this.labelAzione = Costanti.LABEL_PARAMETRO_AZIONE;
		
		return this.labelAzione;
	}

	public String getRiconoscimento() {
		return this.riconoscimento;
	}

	public void setRiconoscimento(String riconoscimento) {
		this.riconoscimento = riconoscimento;
		
		if (StringUtils.isEmpty(riconoscimento)	|| "--".equals(riconoscimento))
			this.riconoscimento = null;
	}
	
	public List<SelectItem> getListaTipiRiconoscimento(){
		List<SelectItem> lst = new ArrayList<>();
		
		lst.add(new SelectItem("--", "--"));  
		
		boolean searchModeBySoggetto = TipologiaRicerca.ingresso.equals(this.getTipologiaRicercaEnum());
		
		String protocolloSelezionato = this.getProtocollo(); 
		boolean protocolloSupportaApplicativoinErogazione = false;
		try{
			protocolloSupportaApplicativoinErogazione = ProtocolFactoryManager.getInstance().getProtocolFactoryByName(protocolloSelezionato).createProtocolConfiguration().isSupportoAutenticazioneApplicativiErogazioni();
		}catch(Exception e) {}
		boolean searchModeByApplicativo = !TipologiaRicerca.ingresso.equals(this.getTipologiaRicercaEnum()) || protocolloSupportaApplicativoinErogazione; 
		
		// comunque sia per soggetto e applicativo DEVE essere selezionata una tipooogia di ricerca
		if( !TipologiaRicerca.ingresso.equals(this.getTipologiaRicercaEnum()) && !TipologiaRicerca.uscita.equals(this.getTipologiaRicercaEnum()) ) {
			searchModeBySoggetto = false;
			searchModeByApplicativo = false;
		}
		
		lst.add(new SelectItem(Costanti.VALUE_TIPO_RICONOSCIMENTO_TOKEN_INFO, MessageManager.getInstance().getMessage(Costanti.TOKEN_INFO_KEY)));  
		if(searchModeBySoggetto) {
			lst.add(new SelectItem(Costanti.VALUE_TIPO_RICONOSCIMENTO_SOGGETTO, MessageManager.getInstance().getMessage(Costanti.SOGGETTO_LABEL_KEY))); 
		}
		if(searchModeByApplicativo) {
			lst.add(new SelectItem(Costanti.VALUE_TIPO_RICONOSCIMENTO_APPLICATIVO, MessageManager.getInstance().getMessage(Costanti.SERVIZIO_APPLICATIVO_LABEL_KEY)));  
		}
		lst.add(new SelectItem(Costanti.VALUE_TIPO_RICONOSCIMENTO_IDENTIFICATIVO_AUTENTICATO, MessageManager.getInstance().getMessage(Costanti.IDENTIFICATIVO_AUTENTICATO_KEY)));  
		lst.add(new SelectItem(Costanti.VALUE_TIPO_RICONOSCIMENTO_INDIRIZZO_IP, MessageManager.getInstance().getMessage(Costanti.INDIRIZZO_IP_KEY)));  
		
		return lst;
	}


	public String getAutenticazione() {
		return this.autenticazione;
	}

	public void setAutenticazione(String autenticazione) {
		this.autenticazione = autenticazione;
		
		if (StringUtils.isEmpty(autenticazione)	|| "--".equals(autenticazione))
			this.autenticazione = null;
	}
	
	public List<SelectItem> getListaAutenticazioni(){
		List<SelectItem> lst = new ArrayList<>();
		
		lst.add(new SelectItem("--", "--"));
		lst.add(new SelectItem(TipoAutenticazione.SSL.getValue(), TipoAutenticazione.SSL.getLabel()));  
		lst.add(new SelectItem(TipoAutenticazione.BASIC.getValue(), TipoAutenticazione.BASIC.getLabel()));  
		lst.add(new SelectItem(TipoAutenticazione.APIKEY.getValue(), TipoAutenticazione.APIKEY.getLabel()));  
		lst.add(new SelectItem(TipoAutenticazione.PRINCIPAL.getValue(), TipoAutenticazione.PRINCIPAL.getLabel()));  
		
		return lst;
	}

	public String getClientAddressMode() {
		if( this.clientAddressMode == null || StringUtils.isEmpty(this.clientAddressMode)) {
			return "--";
		}
		else {
			return this.clientAddressMode;
		}
	}

	public void setClientAddressMode(String clientAddressMode) {
		this.clientAddressMode = clientAddressMode;
		
		if (StringUtils.isEmpty(clientAddressMode)	|| "--".equals(clientAddressMode))
			this.clientAddressMode = null;
	}
	
	public String getTokenClaim() {
		return this.tokenClaim;
	}

	public void setTokenClaim(String tokenClaim) {
		this.tokenClaim = tokenClaim;
		
		if (StringUtils.isEmpty(tokenClaim)	|| "--".equals(tokenClaim))
			this.tokenClaim = null;
	}

	public String getValoreRiconoscimento() {
		return this.valoreRiconoscimento;
	}

	public void setValoreRiconoscimento(String valoreRiconoscimento) {
		this.valoreRiconoscimento = valoreRiconoscimento;
	}
	
	public String getMittenteMatchingType() {
		if(this.mittenteMatchingType!=null)
			return this.mittenteMatchingType.name();
		return null;
	}

	public void setMittenteMatchingType(
			String mittenteMatchingType) {
		if(mittenteMatchingType!=null){
			this.mittenteMatchingType = TipoMatch.valueOf(mittenteMatchingType);
		}
	}
	
	public String getMittenteCaseSensitiveType() {
		if(this.mittenteCaseSensitiveType!=null)
			return this.mittenteCaseSensitiveType.name();
		return null;
	}

	public void setMittenteCaseSensitiveType(
			String mittenteCaseSensitiveType) {
		if(mittenteCaseSensitiveType!=null){
			this.mittenteCaseSensitiveType = CaseSensitiveMatch.valueOf(mittenteCaseSensitiveType);
		}
	}
	
	public String getLabelTipoNomeMittente() {
		return this.labelTipoNomeMittente;
	}

	public void setLabelTipoNomeMittente(String labelTipoNomeMittente) {
		this.labelTipoNomeMittente = labelTipoNomeMittente;
		
		if (StringUtils.isEmpty(this.labelTipoNomeMittente) || "--".equals(this.labelTipoNomeMittente)) {
			this.labelTipoNomeMittente = null;
		}
	}

	public String getLabelTipoNomeDestinatario() {
		return this.labelTipoNomeDestinatario;
	}

	public void setLabelTipoNomeDestinatario(String labelTipoNomeDestinatario) {
		this.labelTipoNomeDestinatario = labelTipoNomeDestinatario;
		
		if (StringUtils.isEmpty(this.labelTipoNomeDestinatario) || "--".equals(this.labelTipoNomeDestinatario)) {
			this.labelTipoNomeDestinatario = null;
		}
	}

	public String getLabelTipoNomeTrafficoPerSoggetto() {
		return this.labelTipoNomeTrafficoPerSoggetto;
	}

	public void setLabelTipoNomeTrafficoPerSoggetto(String labelTipoNomeTrafficoPerSoggetto) {
		this.labelTipoNomeTrafficoPerSoggetto = labelTipoNomeTrafficoPerSoggetto;
		
		if (StringUtils.isEmpty(this.labelTipoNomeTrafficoPerSoggetto) || "--".equals(this.labelTipoNomeTrafficoPerSoggetto)) {
			this.labelTipoNomeTrafficoPerSoggetto = null;
		}
	}

	public String getLabelTipoNomeSoggettoLocale() {
		return this.labelTipoNomeSoggettoLocale;
	}

	public void setLabelTipoNomeSoggettoLocale(String labelTipoNomeSoggettoLocale) {
		this.labelTipoNomeSoggettoLocale = labelTipoNomeSoggettoLocale;
		
		if (StringUtils.isEmpty(this.labelTipoNomeSoggettoLocale) || "--".equals(this.labelTipoNomeSoggettoLocale)) {
			this.labelTipoNomeSoggettoLocale = null;
		}
	}

	public String getLabelNomeServizio() {
		return this.labelNomeServizio;
	}

	public void setLabelNomeServizio(String labelNomeServizio) {
		this.labelNomeServizio = labelNomeServizio;
		
		if (StringUtils.isEmpty(this.labelNomeServizio) || "--".equals(this.labelNomeServizio)) {
			this.labelNomeServizio = null;
		}
	}

	public String getLabelNomeAzione() {
		return this.labelNomeAzione;
	}

	public void setLabelNomeAzione(String labelNomeAzione) {
		this.labelNomeAzione = labelNomeAzione;
		
		if (StringUtils.isEmpty(this.labelNomeAzione) || "--".equals(this.labelNomeAzione)) {
			this.labelNomeAzione = null;
		}
	}
	
	public List<SelectItem> getTipologieRicerca() throws Exception {
		List<SelectItem> listaTipologie = new ArrayList<SelectItem>();
		
		listaTipologie.add(new SelectItem(TipologiaRicerca.ingresso.toString(),"Erogazione"));
		listaTipologie.add(new SelectItem(TipologiaRicerca.uscita.toString(),"Fruizione"));
		if(this.isTipologiaRicercaEntrambiEnabled())
			listaTipologie.add(new SelectItem(TipologiaRicerca.all.toString(),"Erogazione/Fruizione"));
		
		return listaTipologie;
	}
	
	public boolean isShowTipologia() {
		return true;
	}
	
	
	public String getDefaultLabelServizioApplicativo() {
		if(this.getTipologiaRicercaEnum().equals(TipologiaRicerca.ingresso)) {
			if(StringUtils.isNotEmpty(this.getTipoNomeMittente())) {
				return MessageManager.getInstance().getMessage(Costanti.SEARCH_APPLICATIVO_DEFAULT_LABEL_KEY);
			} else {
				return MessageManager.getInstance().getMessage(Costanti.SEARCH_APPLICATIVO_DEFAULT_LABEL_NO_SOGGETTO_FRUITORE_KEY);
			}
		} else {
			if(StringUtils.isNotEmpty(this.getSoggettoLocale())) {
				return MessageManager.getInstance().getMessage(Costanti.SEARCH_APPLICATIVO_DEFAULT_LABEL_KEY);
			} else {
				return MessageManager.getInstance().getMessage(Costanti.SEARCH_APPLICATIVO_DEFAULT_LABEL_NO_SOGGETTO_LOCALE_KEY);
			}
		}
	}
}
