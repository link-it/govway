/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2022 Link.it srl (https://link.it).
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
package org.openspcoop2.web.monitor.transazioni.bean;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.openspcoop2.core.commons.search.IdAccordoServizioParteComune;
import org.openspcoop2.core.commons.search.IdSoggetto;
import org.openspcoop2.core.commons.search.Resource;
import org.openspcoop2.core.constants.CostantiDB;
import org.openspcoop2.core.id.IDAccordo;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.core.registry.driver.IDAccordoFactory;
import org.openspcoop2.core.transazioni.Transazione;
import org.openspcoop2.core.transazioni.constants.PddRuolo;
import org.openspcoop2.core.transazioni.constants.TipoAPI;
import org.openspcoop2.core.transazioni.utils.TempiElaborazione;
import org.openspcoop2.core.transazioni.utils.TempiElaborazioneUtils;
import org.openspcoop2.generic_project.exception.MultipleResultException;
import org.openspcoop2.generic_project.exception.NotImplementedException;
import org.openspcoop2.generic_project.exception.ServiceException;
import org.openspcoop2.pdd.core.CostantiPdD;
import org.openspcoop2.pdd.logger.LogLevels;
import org.openspcoop2.pdd.logger.info.DatiEsitoTransazione;
import org.openspcoop2.pdd.logger.info.DatiMittente;
import org.openspcoop2.pdd.logger.info.InfoEsitoTransazioneFormatUtils;
import org.openspcoop2.pdd.logger.info.InfoMittenteFormatUtils;
import org.openspcoop2.protocol.engine.ProtocolFactoryManager;
import org.openspcoop2.protocol.engine.utils.NamingUtils;
import org.openspcoop2.protocol.sdk.IProtocolFactory;
import org.openspcoop2.protocol.sdk.constants.EsitoTransazioneName;
import org.openspcoop2.protocol.sdk.diagnostica.DriverMsgDiagnosticiNotFoundException;
import org.openspcoop2.protocol.sdk.diagnostica.FiltroRicercaDiagnosticiConPaginazione;
import org.openspcoop2.protocol.sdk.diagnostica.IDiagnosticDriver;
import org.openspcoop2.protocol.sdk.diagnostica.MsgDiagnostico;
import org.openspcoop2.protocol.utils.EsitiProperties;
import org.openspcoop2.protocol.utils.PorteNamingUtils;
import org.openspcoop2.utils.beans.BlackListElement;
import org.openspcoop2.web.monitor.core.core.PddMonitorProperties;
import org.openspcoop2.web.monitor.core.core.Utility;
import org.openspcoop2.web.monitor.core.dao.MBeanUtilsService;
import org.openspcoop2.web.monitor.core.logger.LoggerManager;
import org.openspcoop2.web.monitor.core.utils.BeanUtils;
import org.openspcoop2.web.monitor.core.utils.MessageManager;
import org.openspcoop2.web.monitor.transazioni.constants.TransazioniCostanti;
import org.openspcoop2.web.monitor.transazioni.dao.TransazioniService;
import org.openspcoop2.web.monitor.transazioni.utils.FormatoFaultUtils;
import org.openspcoop2.web.monitor.transazioni.utils.TransazioniEsitiUtils;
import org.slf4j.Logger;

/**
 * TransazioneBean
 * 
 * @author Pintori Giuliano (pintori@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 *
 */
public class TransazioneBean extends Transazione{ 

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private Long latenzaTotale = null;
	private Long latenzaServizio = null;
	private Long latenzaPorta = null;

	private java.lang.String socketClientAddressLabel = null;
	private java.lang.String transportClientAddressLabel = null;
	private java.lang.String trasportoMittenteLabel = null;
	private java.lang.String tipoTrasportoMittenteLabel = null;
	private java.lang.String tokenIssuerLabel = null;
	private java.lang.String tokenClientIdLabel = null;
	private java.lang.String tokenSubjectLabel = null;
	private java.lang.String tokenUsernameLabel = null;
	private java.lang.String tokenMailLabel = null;
	private java.lang.String eventiLabel = null;
	private java.lang.String gruppiLabel = null;
	private java.lang.String operazioneLabel;

	private String soggettoPddMonitor;
	
	public TransazioneBean() {
		super();
	}

	public TransazioneBean(Transazione transazione, String soggettoPddMonitor){
		
		this.soggettoPddMonitor = soggettoPddMonitor;
		
		List<BlackListElement> metodiEsclusi = new ArrayList<BlackListElement>(
				0);
		metodiEsclusi.add(new BlackListElement("setLatenzaTotale", Long.class));
		metodiEsclusi.add(new BlackListElement("setLatenzaServizio", Long.class));
		metodiEsclusi.add(new BlackListElement("setLatenzaPorta", Long.class));
		metodiEsclusi.add(new BlackListElement("setSocketClientAddressLabel", String.class));
		metodiEsclusi.add(new BlackListElement("setTransportClientAddressLabel", String.class));
		metodiEsclusi.add(new BlackListElement("setTrasportoMittenteLabel", String.class));
		metodiEsclusi.add(new BlackListElement("setTipoTrasportoMittenteLabel", String.class));
		metodiEsclusi.add(new BlackListElement("setTokenIssuerLabel", String.class));
		metodiEsclusi.add(new BlackListElement("setTokenClientIdLabel", String.class));
		metodiEsclusi.add(new BlackListElement("setTokenSubjectLabel", String.class));
		metodiEsclusi.add(new BlackListElement("setTokenUsernameLabel", String.class));
		metodiEsclusi.add(new BlackListElement("setTokenMailLabel", String.class));
		metodiEsclusi.add(new BlackListElement("setEventiLabel", String.class));
		metodiEsclusi.add(new BlackListElement("setGruppiLabel", String.class));
		metodiEsclusi.add(new BlackListElement("setOperazioneLabel", String.class));
		metodiEsclusi.add(new BlackListElement("setListaGruppi", List.class));
		metodiEsclusi.add(new BlackListElement("setConsegnaMultipla", boolean.class));
		
		BeanUtils.copy(this, transazione, metodiEsclusi);
	}

	public Long getLatenzaTotale() {
		if(this.latenzaTotale == null){
			if(this.dataUscitaRisposta != null && this.dataIngressoRichiesta != null){
				this.latenzaTotale = this.dataUscitaRisposta.getTime() - this.dataIngressoRichiesta.getTime();
			}
		}

		if(this.latenzaTotale == null)
			return -1L;

		return this.latenzaTotale;
	}

	public void setLatenzaTotale(Long latenzaTotale) {
		this.latenzaTotale = latenzaTotale;
	}

	public Long getLatenzaServizio() {
		if(this.latenzaServizio == null){
			if(this.dataUscitaRichiesta != null && this.dataIngressoRisposta != null){
				this.latenzaServizio = this.dataIngressoRisposta.getTime() - this.dataUscitaRichiesta.getTime();
			}
		}

		if(this.latenzaServizio == null)
			return -1L;

		return this.latenzaServizio;
	}

	public void setLatenzaServizio(Long latenzaServizio) {
		this.latenzaServizio = latenzaServizio;
	}

	public Long getLatenzaPorta() {
		if(this.latenzaPorta == null){
			if(this.getLatenzaTotale() != null && this.getLatenzaTotale()>=0)
				if(this.getLatenzaServizio() != null && this.getLatenzaServizio()>=0)
					this.latenzaPorta = this.getLatenzaTotale().longValue() - this.getLatenzaServizio().longValue();
				else 
					this.latenzaPorta = this.getLatenzaTotale();

		}

		if(this.latenzaPorta == null)
			return -1L;

		return this.latenzaPorta;
	}

	public void setLatenzaPorta(Long latenzaPorta) {
		this.latenzaPorta = latenzaPorta;
	}

	public String getEsitoStyleClass(){
		return TransazioniEsitiUtils.getEsitoStyleClass(this.getEsito(), this.getProtocollo());
	}
	
	public boolean isEsitoOk(){	
		return TransazioniEsitiUtils.isEsitoOk(this.getEsito(), this.getProtocollo());
	}
	public boolean isEsitoFaultApplicativo(){	
		return TransazioniEsitiUtils.isEsitoFaultApplicativo(this.getEsito(), this.getProtocollo());
	}
	public boolean isEsitoKo(){	
		return TransazioniEsitiUtils.isEsitoKo(this.getEsito(), this.getProtocollo());
	}
	
	public String getEsitoIcona() {
		if(TransazioniEsitiUtils.isEsitoOk(this.getEsito(), this.getProtocollo()))
			return MessageManager.getInstance().getMessage(TransazioniCostanti.TRANSAZIONI_ELENCO_ESITO_OK_ICON_KEY);
		if(TransazioniEsitiUtils.isEsitoFaultApplicativo(this.getEsito(), this.getProtocollo()))
			return MessageManager.getInstance().getMessage(TransazioniCostanti.TRANSAZIONI_ELENCO_ESITO_ERROR_ICON_KEY);
		if(TransazioniEsitiUtils.isEsitoKo(this.getEsito(), this.getProtocollo()))
			return MessageManager.getInstance().getMessage(TransazioniCostanti.TRANSAZIONI_ELENCO_ESITO_WARNING_ICON_KEY);

		return MessageManager.getInstance().getMessage(TransazioniCostanti.TRANSAZIONI_ELENCO_ESITO_WARNING_ICON_KEY);
	}

	public java.lang.String getEsitoLabel() {
		return TransazioniEsitiUtils.getEsitoLabel(this.getEsito(), this.getProtocollo());
	}
	
	public java.lang.String getEsitoLabelSyntetic() {
		Integer httpStatus = null;
		if(this.getCodiceRispostaUscita()!=null && !"".equals(this.getCodiceRispostaUscita())) {
			try {
				httpStatus = Integer.valueOf(this.getCodiceRispostaUscita());
			}catch(Throwable t) {}
		}
		return TransazioniEsitiUtils.getEsitoLabelSyntetic(this.getEsito(), this.getProtocollo(), httpStatus, this.getTipoApi());
	}
	
	public java.lang.String getEsitoLabelDescription() {
		Integer httpStatus = null;
		if(this.getCodiceRispostaUscita()!=null && !"".equals(this.getCodiceRispostaUscita())) {
			try {
				httpStatus = Integer.valueOf(this.getCodiceRispostaUscita());
			}catch(Throwable t) {}
		}
		Integer httpInStatus = null;
		if(this.getCodiceRispostaIngresso()!=null && !"".equals(this.getCodiceRispostaIngresso())) {
			try {
				httpInStatus = Integer.valueOf(this.getCodiceRispostaIngresso());
			}catch(Throwable t) {}
		}
		return TransazioniEsitiUtils.getEsitoLabelDescription(this.getEsito(), this.getProtocollo(), httpStatus, httpInStatus, this.getTipoApi());
	}
	
	public boolean isShowContesto(){
		try{
			return EsitiProperties.getInstance(LoggerManager.getPddMonitorCoreLogger(),this.getProtocollo()).getEsitiTransactionContextCode().size()>1;
		}catch(Exception e){
			LoggerManager.getPddMonitorCoreLogger().error("Errore durante il calcolo dei contesti: "+e.getMessage(),e);
			return false;
		}
	}
	
	public java.lang.String getEsitoContestoLabel() {
		return TransazioniEsitiUtils.getEsitoContestoLabel(this.getEsitoContesto(), this.getProtocollo());
	}
	
	public String getFaultCooperazionePretty(){
		return FormatoFaultUtils.getFaultPretty(super.getFaultCooperazione(), super.getFormatoFaultCooperazione());
	}

	public boolean isVisualizzaFaultCooperazione(){
		return FormatoFaultUtils.isVisualizzaFault(super.getFaultCooperazione());
	}

	public String getBrushFaultCooperazione() {
		return FormatoFaultUtils.getBrushFault(super.getFaultCooperazione(), super.getFormatoFaultCooperazione());
	}

	public String getErroreVisualizzaFaultCooperazione(){
		return FormatoFaultUtils.getErroreVisualizzaFault(super.getFaultCooperazione());
	}

	public String getFaultIntegrazionePretty(){
		return FormatoFaultUtils.getFaultPretty(super.getFaultIntegrazione(), super.getFormatoFaultIntegrazione());
	}

	public boolean isVisualizzaFaultIntegrazione(){
		return FormatoFaultUtils.isVisualizzaFault(super.getFaultIntegrazione());
	}

	public String getBrushFaultIntegrazione() {
		return FormatoFaultUtils.getBrushFault(super.getFaultIntegrazione(), super.getFormatoFaultIntegrazione());
	}

	public String getErroreVisualizzaFaultIntegrazione(){
		return FormatoFaultUtils.getErroreVisualizzaFault(super.getFaultIntegrazione());
	}

	@Override
	public String getNomePorta(){
		String nomePorta = super.getNomePorta();
		if(nomePorta!=null && nomePorta.startsWith("null_") && (nomePorta.length()>"null_".length())){
			return nomePorta.substring("null_".length());
		}
		else{
			return nomePorta;
		}
	}

	public String getSoggettoFruitore() throws Exception { 
		if(StringUtils.isNotEmpty(this.getNomeSoggettoFruitore())) {
			return NamingUtils.getLabelSoggetto(this.getProtocollo(), this.getTipoSoggettoFruitore(), this.getNomeSoggettoFruitore());
		}

		return "";
	}

	public String getSoggettoErogatore() throws Exception { 
		if(StringUtils.isNotEmpty(this.getNomeSoggettoErogatore())) {
			return NamingUtils.getLabelSoggetto(this.getProtocollo(), this.getTipoSoggettoErogatore(), this.getNomeSoggettoErogatore());
		}

		return "";
	}

	public String getSoggettoPdd() throws Exception { 
		if(StringUtils.isNotEmpty(this.getPddNomeSoggetto())) {
			return NamingUtils.getLabelSoggetto(this.getProtocollo(), this.getPddTipoSoggetto(), this.getPddNomeSoggetto());
		}

		return "";
	}

	public String getProtocolloLabel() throws Exception{
		return NamingUtils.getLabelProtocollo(this.getProtocollo()); 
	}


	public String getServizio() throws Exception{
		if(StringUtils.isNotEmpty(this.getNomeServizio())) {
			return NamingUtils.getLabelAccordoServizioParteSpecificaSenzaErogatore(this.getProtocollo(), this.getTipoServizio(), this.getNomeServizio(), this.getVersioneServizio());
		}
		return "";
	}

	public String getPortaLabel() throws Exception{
		IProtocolFactory<?> protocolFactory = ProtocolFactoryManager.getInstance().getProtocolFactoryByName(this.getProtocollo());
		PorteNamingUtils n = new PorteNamingUtils(protocolFactory);
		switch(this.getPddRuolo()) {
		case APPLICATIVA:
			return n.normalizePA(this.getNomePorta());
		case DELEGATA:
			return n.normalizePD(this.getNomePorta());
		case INTEGRATION_MANAGER:
		case ROUTER:
		default:
			return this.getNomePorta();
		}
	}

	public java.lang.String getSocketClientAddressLabel() {
		return this.socketClientAddressLabel;
	}

	public void setSocketClientAddressLabel(java.lang.String socketClientAddressLabel) {
		this.socketClientAddressLabel = socketClientAddressLabel;
	}

	public java.lang.String getTransportClientAddressLabel() {
		return this.transportClientAddressLabel;
	}

	public void setTransportClientAddressLabel(java.lang.String transportClientAddressLabel) {
		this.transportClientAddressLabel = transportClientAddressLabel;
	}
	
	public java.lang.String getTrasportoMittenteLabel() {
		return this.trasportoMittenteLabel;
	}

	public void setTrasportoMittenteLabel(java.lang.String trasportoMittenteLabel) {
		this.trasportoMittenteLabel = trasportoMittenteLabel;
	}

	public java.lang.String getTipoTrasportoMittenteLabel() {
		return this.tipoTrasportoMittenteLabel;
	}

	public void setTipoTrasportoMittenteLabel(java.lang.String tipoTrasportoMittenteLabel) {
		this.tipoTrasportoMittenteLabel = tipoTrasportoMittenteLabel;
	}
	
	public java.lang.String getTokenIssuerLabel() {
		return this.tokenIssuerLabel;
	}

	public void setTokenIssuerLabel(java.lang.String tokenIssuerLabel) {
		this.tokenIssuerLabel = tokenIssuerLabel;
	}

	public java.lang.String getTokenClientIdLabel() {
		return this.tokenClientIdLabel;
	}

	public void setTokenClientIdLabel(java.lang.String tokenClientIdLabel) {
		this.tokenClientIdLabel = tokenClientIdLabel;
	}

	public java.lang.String getTokenSubjectLabel() {
		return this.tokenSubjectLabel;
	}

	public void setTokenSubjectLabel(java.lang.String tokenSubjectLabel) {
		this.tokenSubjectLabel = tokenSubjectLabel;
	}

	public java.lang.String getTokenUsernameLabel() {
		return this.tokenUsernameLabel;
	}

	public void setTokenUsernameLabel(java.lang.String tokenUsernameLabel) {
		this.tokenUsernameLabel = tokenUsernameLabel;
	}

	public java.lang.String getTokenMailLabel() {
		return this.tokenMailLabel;
	}

	public void setTokenMailLabel(java.lang.String tokenMailLabel) {
		this.tokenMailLabel = tokenMailLabel;
	}

	
	
	
	@Override
	public String getEventiGestione() {
		return this.eventiLabel;
	}
	public String getEventiGestioneRawValue() {
		return this.eventiGestione;
	}
	
	public String getEventiGestioneHTML(){
		String tmp = this.getEventiGestione();
		if(tmp!=null){
			tmp = tmp.trim();
			if(tmp.contains(CostantiPdD.PREFIX_CONNETTORI_MULTIPLI)) {
				tmp = tmp.replace(CostantiPdD.PREFIX_CONNETTORI_MULTIPLI, "");
			}
			if(tmp.contains(",")){
				String [] split = tmp.split(",");
				if(split!=null && split.length>0){
					StringBuilder bf = new StringBuilder();
					for (int i = 0; i < split.length; i++) {
						if(bf.length()>0){
							bf.append("<BR/>");
						}
						bf.append(split[i].trim());
					}
					return bf.toString();
				}
				else{
					return tmp;
				}
			}
			else{
				return tmp;
			}
		}
		else{
			return null;
		}
	}
	
	public List<String> getEventiGestioneAsList(){
		String tmp = this.getEventiGestione();
		List<String> l = new ArrayList<String>();
		if(tmp!=null){
			tmp = tmp.trim();
			if(tmp.contains(",")){
				String [] split = tmp.split(",");
				if(split!=null && split.length>0){
					for (int i = 0; i < split.length; i++) {
						l.add(split[i].trim());
					}
				}
				else{
					l.add(tmp);
				}
			}
			else{
				l.add(tmp);
			}
		}
		return l;
	}
	
	public Integer getInResponseCodeFromEventiGestione(){
		List<String> l = getEventiGestioneAsList();
		if(!l.isEmpty()) {
			for (String evento : l) {
				if(evento!=null && evento.startsWith(CostantiPdD.PREFIX_HTTP_STATUS_CODE_IN) && evento.length()>CostantiPdD.PREFIX_HTTP_STATUS_CODE_IN.length()) {
					try {
						String sub = evento.substring(CostantiPdD.PREFIX_HTTP_STATUS_CODE_IN.length());
						int httpStatus = Integer.valueOf(sub);
						if(httpStatus>0) {
							return httpStatus;
						}
					}catch(Throwable t) {}
				}
			}
		}
		return null;
	}
	public Integer getOutResponseCodeFromEventiGestione(){
		List<String> l = getEventiGestioneAsList();
		if(!l.isEmpty()) {
			for (String evento : l) {
				if(evento!=null && evento.startsWith(CostantiPdD.PREFIX_HTTP_STATUS_CODE_OUT) && evento.length()>CostantiPdD.PREFIX_HTTP_STATUS_CODE_OUT.length()) {
					try {
						String sub = evento.substring(CostantiPdD.PREFIX_HTTP_STATUS_CODE_OUT.length());
						int httpStatus = Integer.valueOf(sub);
						if(httpStatus>0) {
							return httpStatus;
						}
					}catch(Throwable t) {}
				}
			}
		}
		return null;
	}
	public Integer getTipoApiFromEventiGestione(){
		List<String> l = getEventiGestioneAsList();
		if(!l.isEmpty()) {
			for (String evento : l) {
				if(evento!=null && evento.startsWith(CostantiPdD.PREFIX_API) && evento.length()>CostantiPdD.PREFIX_API.length()) {
					try {
						String sub = evento.substring(CostantiPdD.PREFIX_API.length());
						int tipoApi = Integer.valueOf(sub);
						if(tipoApi>0) {
							return tipoApi;
						}
					}catch(Throwable t) {}
				}
			}
		}
		return null;
	}
	
	public java.lang.String getEventiLabel() {
		return this.eventiLabel;
	}

	public void setEventiLabel(java.lang.String eventiLabel) {
		this.eventiLabel = eventiLabel;
	}

	
	@Override
	public String getGruppi() {
		return this.gruppiLabel;
	}
	public String getGruppiRawValue() {
		return this.gruppi;
	}
	
	/*
	 * Il metodo getGruppiHTML serve nel dettaglio della transazione per far vedere i tag testuali:
	 * <h:outputText id="outGruppi" value="#{dettagliBean.dettaglio.gruppiHTML}" escape="false" rendered="#{not empty dettagliBean.dettaglio.gruppiHTML}" />
	 * 
	 * invece che tramite il seguente metodo:
	 *		<a4j:outputPanel layout="block" id="outGruppi" styleClass="titoloTags" rendered="#{not empty dettagliBean.dettaglio.listaGruppi}">
	 *		<a4j:repeat value="#{dettagliBean.dettaglio.listaGruppi}" var="gruppo">
	 *			<h:outputText id="outCustomContentTag" value="#{gruppo.label}"  styleClass="tag label label-info #{gruppo.value}"/>
	 *		</a4j:repeat>
	 *		</a4j:outputPanel>
	 */
	public String getGruppiHTML(){
		String tmp = this.getGruppi();
		if(tmp!=null){
			tmp = tmp.trim();
			if(tmp.contains(",")){
				String [] split = tmp.split(",");
				if(split!=null && split.length>0){
					StringBuilder bf = new StringBuilder();
					for (int i = 0; i < split.length; i++) {
						if(bf.length()>0){
							bf.append("<BR/>");
						}
						bf.append(split[i].trim());
					}
					return bf.toString();
				}
				else{
					return tmp;
				}
			}
			else{
				return tmp;
			}
		}
		else{
			return null;
		}
	}
	
	public java.lang.String getGruppiLabel() {
		return this.gruppiLabel;
	}

	public void setGruppiLabel(java.lang.String gruppiLabel) {
		this.gruppiLabel = gruppiLabel;
	}
	
	public List<Gruppo> getListaGruppi(){
		String tmp = this.getGruppi();
		List<Gruppo> gruppiObj = new ArrayList<Gruppo>();
		if(tmp!=null){
			// colleziono i tags registrati
			List<String> tagsDisponibili = Utility.getListaNomiGruppi();
			
			tmp = tmp.trim();
			
			if(tmp.contains(",")){
				String [] split = tmp.split(",");
				if(split!=null && split.length>0){
					for (String nomeGruppo : split) {
						Gruppo gruppo = new Gruppo();
						gruppo.setLabel(nomeGruppo);
						gruppiObj.add(gruppo);
						
						int indexOf = tagsDisponibili.indexOf(nomeGruppo);
						if(indexOf == -1)
							indexOf = 0;
						
						indexOf = indexOf % TransazioniCostanti.NUMERO_GRUPPI_CSS;
						
						gruppo.setValue("label-info-"+indexOf);
					}
				}
			}else {
				Gruppo gruppo = new Gruppo();
				gruppo.setLabel(tmp);
				int indexOf = tagsDisponibili.indexOf(tmp);
				if(indexOf == -1)
					indexOf = 0;
				
				indexOf = indexOf % TransazioniCostanti.NUMERO_GRUPPI_CSS;
				
				gruppo.setValue("label-info-"+indexOf);
				
				gruppiObj.add(gruppo);
			}
		}
		
		return gruppiObj;
	}
	
	public void setListaGruppi(List<Gruppo> lista) {}
	
	
	public TempiElaborazioneBean getTempiElaborazioneObject() {
		String tempiElaborazione = this.getTempiElaborazione();
		try {
			TempiElaborazione tempi = TempiElaborazioneUtils.convertFromDBValue(tempiElaborazione);
			if(tempi!=null) {
				return new TempiElaborazioneBean(tempi);
			}
			else {
				return null;
			}
		}catch(Exception e) {
			LoggerManager.getPddMonitorCoreLogger().error("Errore durante il processamento dei tempi di elaborazione ["+tempiElaborazione+"]",e);
			return null;
		}
	}
	
	public void normalizeTipoApiInfo(org.openspcoop2.core.commons.search.dao.IServiceManager service, Logger log) {
		
		if(TipoAPI.REST.getValoreAsInt() != this.getTipoApi() && TipoAPI.SOAP.getValoreAsInt() != this.getTipoApi()) {
			
			String uriAPI = this.getUriAccordoServizio();
			try {
			
				if(StringUtils.isNotEmpty(uriAPI)) {
					IDAccordo idAccordo = IDAccordoFactory.getInstance().getIDAccordoFromUri(uriAPI);
					IdAccordoServizioParteComune idAPI = new IdAccordoServizioParteComune();
					idAPI.setIdSoggetto(new IdSoggetto());
					idAPI.getIdSoggetto().setTipo(idAccordo.getSoggettoReferente().getTipo());
					idAPI.getIdSoggetto().setNome(idAccordo.getSoggettoReferente().getNome());
					idAPI.setVersione(idAccordo.getVersione());
					idAPI.setNome(idAccordo.getNome());
					
					MBeanUtilsService mBeanUtilsService = new MBeanUtilsService(service, log);
					int tipo = mBeanUtilsService.getTipoApiFromCache(idAccordo);
					this.setTipoApi(tipo);
				}
				
			}catch(Exception e) {
				// L'API puo non esistere piu se e' stata eliminata; non loggare come error
				log.info("Normalizzazione tipo di api '"+uriAPI+"' non riuscita: "+e.getMessage(),e);
			}
			
		}
		
		this.operazioneLabel = getAzione();
	}
	
	public String getTipoApiLabel() {
		if(TipoAPI.REST.getValoreAsInt() == this.getTipoApi()) {
			return "Rest";
		}
		else if(TipoAPI.SOAP.getValoreAsInt() == this.getTipoApi()) {
			return "Soap";
		}
		else {
			return "";
		}
	}
	
	public boolean isShowParteComune() {
		String parteComune = this.getUriAccordoServizio();
		if(parteComune!=null && !"".equals(parteComune)) {
			try {
				IDAccordo idAccordo = IDAccordoFactory.getInstance().getIDAccordoFromUri(parteComune);
				if(!idAccordo.getNome().equals(this.getNomeServizio())) {
					return true;
				}
				if(idAccordo.getVersione().intValue() != this.getVersioneServizio()) {
					return true;
				}
				IProtocolFactory<?> protocolFactory = ProtocolFactoryManager.getInstance().getProtocolFactoryByName(this.getProtocollo());
				if(protocolFactory.createProtocolConfiguration().isSupportoSoggettoReferenteAccordiParteComune()) {
					if(!idAccordo.getSoggettoReferente().getTipo().equals(this.getTipoSoggettoErogatore())) {
						return true;
					}
					if(!idAccordo.getSoggettoReferente().getNome().equals(this.getNomeSoggettoErogatore())) {
						return true;
					}
				}
			}catch(Throwable t) {}
		}
		return false;
	}
	
	public String getApi() throws Exception{
		String parteComune = this.getUriAccordoServizio();
		if(parteComune!=null && !"".equals(parteComune)) {
			try {
				IDAccordo idAccordo = IDAccordoFactory.getInstance().getIDAccordoFromUri(parteComune);
				return NamingUtils.getLabelAccordoServizioParteComune(idAccordo);
			}catch(Throwable t) {}
		}
		return "";
	}
	
	public boolean isShowProfiloCollaborazione() {
		return TipoAPI.SOAP.getValoreAsInt() == this.getTipoApi();
	}
	
	public String getPddRuoloToolTip() {
		PddRuolo pddRuolo = this.getPddRuolo();
		if(pddRuolo!=null) {
			switch (pddRuolo) {
			case DELEGATA:
				return MessageManager.getInstance().getMessage(TransazioniCostanti.TRANSAZIONI_ELENCO_RUOLO_PDD_DELEGATA_LABEL_KEY);
			case APPLICATIVA:
				return MessageManager.getInstance().getMessage(TransazioniCostanti.TRANSAZIONI_ELENCO_RUOLO_PDD_APPLICATIVA_LABEL_KEY);
			case ROUTER:
				return MessageManager.getInstance().getMessage(TransazioniCostanti.TRANSAZIONI_ELENCO_RUOLO_PDD_ROUTER_LABEL_KEY);
			case INTEGRATION_MANAGER:
				return MessageManager.getInstance().getMessage(TransazioniCostanti.TRANSAZIONI_ELENCO_RUOLO_PDD_IM_LABEL_KEY);
			}
		}
		return MessageManager.getInstance().getMessage(TransazioniCostanti.TRANSAZIONI_ELENCO_RUOLO_PDD_ROUTER_LABEL_KEY);
	}
	
	public String getPddRuoloImage() {
		PddRuolo pddRuolo = this.getPddRuolo();
		if(pddRuolo!=null) {
			switch (pddRuolo) {
			case DELEGATA:
				return MessageManager.getInstance().getMessage(TransazioniCostanti.TRANSAZIONI_ELENCO_RUOLO_PDD_DELEGATA_ICON_KEY);
			case APPLICATIVA:
				return MessageManager.getInstance().getMessage(TransazioniCostanti.TRANSAZIONI_ELENCO_RUOLO_PDD_APPLICATIVA_ICON_KEY);
			case ROUTER:
				return MessageManager.getInstance().getMessage(TransazioniCostanti.TRANSAZIONI_ELENCO_RUOLO_PDD_ROUTER_ICON_KEY);
			case INTEGRATION_MANAGER:
				return MessageManager.getInstance().getMessage(TransazioniCostanti.TRANSAZIONI_ELENCO_RUOLO_PDD_IM_ICON_KEY);
			}
		}
		return MessageManager.getInstance().getMessage(TransazioniCostanti.TRANSAZIONI_ELENCO_RUOLO_PDD_ROUTER_ICON_KEY);
	}
	
	public void normalizeRichiedenteInfo(Transazione t, TransazioneBean transazioneBean, TransazioniService transazioniService) throws ServiceException, MultipleResultException, NotImplementedException {
		
		// 1) Username del Token
		String sTokenUsername = getTokenUsername();
		if(StringUtils.isNotEmpty(sTokenUsername)) {
			transazioniService.normalizeInfoTransazioniFromCredenzialiMittenteTokenUsername(transazioneBean, t);
			return;
		}
		
		// 2) Subject/Issuer del Token
		String sTokenSubject = getTokenSubject();
		if(StringUtils.isNotEmpty(sTokenSubject)) {
			transazioniService.normalizeInfoTransazioniFromCredenzialiMittenteTokenSubject(transazioneBean, t);
			String sTokenIssuer = getTokenIssuer();
			if(StringUtils.isNotEmpty(sTokenIssuer)) {
				transazioniService.normalizeInfoTransazioniFromCredenzialiMittenteTokenIssuer(transazioneBean, t);
			}
			return;
		}
		
		// 3) Applicativo Fruitore
		String sApplicativoFruitore = getServizioApplicativoFruitore();
		if(StringUtils.isNotEmpty(sApplicativoFruitore)) {
			return;
		}
		
		// 4) Credenziali dell'autenticazione di trasporto
		String sTrasportoMittente = getTrasportoMittente();
		if(StringUtils.isNotEmpty(sTrasportoMittente)) {
			transazioniService.normalizeInfoTransazioniFromCredenzialiMittenteTrasporto(transazioneBean, t);
			return;
		}
		
		// 5) Client ID, per il caso di ClientCredential
		String sTokenClientId = getTokenClientId();
		if(StringUtils.isNotEmpty(sTokenClientId)) {
			transazioniService.normalizeInfoTransazioniFromCredenzialiMittenteTokenClientID(transazioneBean, t);
			return;
		}

	}
	
	public boolean isVisualizzaTextAreaRichiedente() {
		String de = this.getRichiedente();
		if(StringUtils.isNotEmpty(de)) {
			if(de.length() > 150)
				return true;
		} 
		return false;
	}
	
	public DatiMittente convertToDatiMittente() {
		
		DatiMittente datiMittente = new DatiMittente();
		
		datiMittente.setTokenUsername(getTokenUsernameLabel());
		datiMittente.setTokenSubject(getTokenSubjectLabel());
		datiMittente.setTokenIssuer(getTokenIssuerLabel());
		datiMittente.setTokenClientId(getTokenClientIdLabel());
		
		datiMittente.setTipoTrasportoMittente(getTipoTrasportoMittenteLabel());
		datiMittente.setTrasportoMittente(getTrasportoMittenteLabel());
		
		datiMittente.setServizioApplicativoFruitore(getServizioApplicativoFruitore());
		
		try {
			datiMittente.setSoggettoFruitore(getSoggettoFruitore());
		}catch(Exception e) {
			throw new RuntimeException(e.getMessage(),e);
		}
		datiMittente.setTipoSoggettoFruitore(getTipoSoggettoFruitore());
		datiMittente.setNomeSoggettoFruitore(getNomeSoggettoFruitore());

		datiMittente.setPddRuolo(this.getPddRuolo());
		datiMittente.setSoggettoOperativo(this.soggettoPddMonitor);
	
		if(this.getTransportClientAddress()!=null){
			datiMittente.setTransportClientAddress(this.getTransportClientAddress());
		}
		else {
			datiMittente.setTransportClientAddress(this.getTransportClientAddressLabel());
		}
		if(this.getSocketClientAddress()!=null) {
			datiMittente.setSocketClientAddress(getSocketClientAddress());
		}
		else {
			datiMittente.setSocketClientAddress(getSocketClientAddressLabel());
		}
		
		return datiMittente;
	}
	
	public DatiEsitoTransazione convertToDatiEsitoTransazione() {
		
		DatiEsitoTransazione datiEsitoTransazione = new DatiEsitoTransazione();
		
		datiEsitoTransazione.setEsito(this.getEsito());
		datiEsitoTransazione.setProtocollo(this.getProtocollo());
		
		datiEsitoTransazione.setFaultIntegrazione(this.getFaultIntegrazione());
		datiEsitoTransazione.setFormatoFaultIntegrazione(this.getFormatoFaultIntegrazione());
		
		datiEsitoTransazione.setFaultCooperazione(this.getFaultCooperazione());
		datiEsitoTransazione.setFormatoFaultCooperazione(this.getFormatoFaultCooperazione());
				
		datiEsitoTransazione.setPddRuolo(this.getPddRuolo());
		
		return datiEsitoTransazione;
		
	}
	
	
	public String getRichiedente() {
				
		DatiMittente datiMittente = this.convertToDatiMittente();
		return InfoMittenteFormatUtils.getRichiedente(datiMittente);
				
	}
	
	public String getIpRichiedente() {
		
		DatiMittente datiMittente = this.convertToDatiMittente();
		return InfoMittenteFormatUtils.getIpRichiedente(datiMittente);
		
	}
	
	public String getLabelRichiedenteConFruitore() throws Exception {
		
		DatiMittente datiMittente = this.convertToDatiMittente();
		return InfoMittenteFormatUtils.getLabelRichiedenteConFruitore(datiMittente);
		
	}
	
	public String getLabelAPIConErogatore() throws Exception {
		StringBuilder bf = new StringBuilder();
		
		String api = getServizio();
		if(StringUtils.isNotEmpty(api)) {
			bf.append(api);	
		}
		else {
			return "API non individuata";
		}
		
		
		String sErogatore = getSoggettoErogatore();
		if(StringUtils.isNotEmpty(sErogatore)) {
		
			boolean addErogatore = true;
			
			if(org.openspcoop2.core.transazioni.constants.PddRuolo.APPLICATIVA.equals(this.getPddRuolo())) {
				if(this.soggettoPddMonitor!=null && StringUtils.isNotEmpty(this.getTipoSoggettoErogatore()) && StringUtils.isNotEmpty(this.getNomeSoggettoErogatore())) {
					IDSoggetto idSoggettoErogatore = new IDSoggetto(this.getTipoSoggettoErogatore(), this.getNomeSoggettoErogatore());
					addErogatore = !this.soggettoPddMonitor.equals(idSoggettoErogatore.toString());
				}
			}
			
			if(addErogatore) {
				
				bf = new StringBuilder();
				bf.append(NamingUtils.getLabelServizioConDominioErogatore(api, sErogatore));
				
			}
		}
		
		return bf.toString();
	}
		
	public java.lang.String getTipoOperazioneLabel() {
		if(TipoAPI.REST.getValoreAsInt() == this.getTipoApi()) {
			return "Risorsa";
		}
		else {
			return "Azione";
		}
	}
	
	public java.lang.String getOperazioneLabel() {
		return this.operazioneLabel;
	}

	public void setOperazioneLabel(java.lang.String operazioneLabel) {
		this.operazioneLabel = operazioneLabel;
	}
	
	public void normalizeOperazioneInfo(org.openspcoop2.core.commons.search.dao.IServiceManager service, Logger log) {
		
		if(TipoAPI.REST.getValoreAsInt() == this.getTipoApi()) {
			
			String uriAPI = this.getUriAccordoServizio();
			String op = getAzione();
			try {
			
				if(StringUtils.isNotEmpty(uriAPI) && StringUtils.isNotEmpty(op)) {
					IDAccordo idAccordo = IDAccordoFactory.getInstance().getIDAccordoFromUri(uriAPI);
					IdAccordoServizioParteComune idAPI = new IdAccordoServizioParteComune();
					idAPI.setIdSoggetto(new IdSoggetto());
					idAPI.getIdSoggetto().setTipo(idAccordo.getSoggettoReferente().getTipo());
					idAPI.getIdSoggetto().setNome(idAccordo.getSoggettoReferente().getNome());
					idAPI.setVersione(idAccordo.getVersione());
					idAPI.setNome(idAccordo.getNome());
					
					MBeanUtilsService mBeanUtilsService = new MBeanUtilsService(service, log);
					List<Map<String,Object>> l = mBeanUtilsService.getInfoOperazioneFromCache(op, idAccordo);
					if(l!=null && l.size()==1) {
						Map<String,Object> map = l.get(0);
						String method = (String) map.get(Resource.model().HTTP_METHOD.getFieldName());
						String path = (String) map.get(Resource.model().PATH.getFieldName());
						//System.out.println("LETTO ["+method+"] ["+path+"]");
						StringBuilder bf = new StringBuilder();
						if(!CostantiDB.API_RESOURCE_HTTP_METHOD_ALL_VALUE.equals(method)) {
							bf.append(method);
							bf.append(" ");
						}
						if(!CostantiDB.API_RESOURCE_PATH_ALL_VALUE.equals(path)) {
							bf.append(path);
							this.operazioneLabel = bf.toString();
							return ;
						}
					}
				}
				
			}catch(Exception e) {
				// L'API puo non esistere piu se e' stata eliminata; non loggare come error
				log.info("Normalizzazione operazione '"+op+"' per api '"+uriAPI+"' non riuscita: "+e.getMessage(),e);
			}
			
		}
		
		this.operazioneLabel = getAzione();
	}
	
	public String getLabelOperazioneConGestioneNonPresenza() throws Exception {
		
		String op = getOperazioneLabel();
		if(StringUtils.isNotEmpty(op)) {
			
			return this.getOperazioneLabel();
			
		}
		else {
			return "-";
		}
		
	}
	
	public String getCssColonnaEsito() {
		if(this.isEsitoOk())
			return TransazioniCostanti.TRANSAZIONI_ELENCO_CUSTOM_CLASSE_CSS_COL_ESITO_OK;
		if(this.isEsitoFaultApplicativo())
			return TransazioniCostanti.TRANSAZIONI_ELENCO_CUSTOM_CLASSE_CSS_COL_ESITO_FAULT;
		if(this.isEsitoKo())
			return TransazioniCostanti.TRANSAZIONI_ELENCO_CUSTOM_CLASSE_CSS_COL_ESITO_ERRORE;
		
		return TransazioniCostanti.TRANSAZIONI_ELENCO_CUSTOM_CLASSE_CSS_COL_ESITO_ERRORE;
	}
	
	public String getCssColonnaLatenza() {
		if(this.isEsitoOk())
			return TransazioniCostanti.TRANSAZIONI_ELENCO_CUSTOM_CLASSE_CSS_COL_LATENZA_OK;
		if(this.isEsitoFaultApplicativo())
			return TransazioniCostanti.TRANSAZIONI_ELENCO_CUSTOM_CLASSE_CSS_COL_LATENZA_FAULT;
		if(this.isEsitoKo())
			return TransazioniCostanti.TRANSAZIONI_ELENCO_CUSTOM_CLASSE_CSS_COL_LATENZA_ERRORE;
		
		return TransazioniCostanti.TRANSAZIONI_ELENCO_CUSTOM_CLASSE_CSS_COL_LATENZA_ERRORE;
	}
	
	public boolean isConsegnaMultipla() {
		try{
			EsitiProperties esitiProperties = EsitiProperties.getInstance(LoggerManager.getPddMonitorCoreLogger(),this.getProtocollo());
			String name = esitiProperties.getEsitoName(this.getEsito());
			EsitoTransazioneName esitoName = EsitoTransazioneName.convertoTo(name);
			return EsitoTransazioneName.isConsegnaMultipla(esitoName);
		}catch(Exception e){
			LoggerManager.getPddMonitorCoreLogger().error("Errore durante il check consegna multipla: "+e.getMessage(),e);
			return false;
		}
	}
	
	public boolean isSavedInMessageBox() {
		try{
			EsitiProperties esitiProperties = EsitiProperties.getInstance(LoggerManager.getPddMonitorCoreLogger(),this.getProtocollo());
			String name = esitiProperties.getEsitoName(this.getEsito());
			EsitoTransazioneName esitoName = EsitoTransazioneName.convertoTo(name);
			return EsitoTransazioneName.isSavedInMessageBox(esitoName);
		}catch(Exception e){
			LoggerManager.getPddMonitorCoreLogger().error("Errore durante il check saved in message box: "+e.getMessage(),e);
			return false;
		}
	}

	public void setConsegnaMultipla(boolean consegnaMultipla) {}
	
	public String getLabelDettaglioErrore() {
		return this.isEsitoOk() ? "Dettaglio Anomalia" : "Dettaglio Errore";
	}
	
	public boolean isVisualizzaTextAreaDettaglioErrore() {
		String de = this.getDettaglioErrore();
		if(StringUtils.isNotEmpty(de)) {
			if(de.length() > 150)
				return true;
		} 
		return false;
	}

	private String dettaglioErrore;
	public String getDettaglioErrore() {
		
		if(this.dettaglioErrore!=null) {
			return this.dettaglioErrore;
		}
		
		return _getDettaglioErrore(null);
	}
	public String getDettaglioErrore(List<MsgDiagnostico> msgs) {
		
		if(this.dettaglioErrore!=null) {
			return this.dettaglioErrore;
		}
		
		return _getDettaglioErrore(msgs);
	}
	private synchronized String _getDettaglioErrore(List<MsgDiagnostico> msgsParams) {
		
		if(this.dettaglioErrore!=null) {
			return this.dettaglioErrore;
		}
		
		Logger log = LoggerManager.getPddMonitorCoreLogger();
				
		// lista diagnostici
		List<MsgDiagnostico> msgs = null;
		if(msgsParams!=null) {
			msgs = msgsParams;
		}
		else {
			// Esito
			EsitiProperties esitiProperties = null;
			EsitoTransazioneName esitoTransactionName = null;
			try {
				esitiProperties = EsitiProperties.getInstance(log, this.getProtocollo());
				esitoTransactionName = esitiProperties.getEsitoTransazioneName(this.getEsito());
			}catch(Exception e){
				log.error("Errore durante il recupero dell'esito della transazione: "+e.getMessage(),e);
				return ""; // non dovrebbe mai succedere
			}
			try{
				PddMonitorProperties govwayMonitorProperties = PddMonitorProperties.getInstance(log);
				IDiagnosticDriver driver = govwayMonitorProperties.getDriverMsgDiagnostici();
				
				FiltroRicercaDiagnosticiConPaginazione filter = new FiltroRicercaDiagnosticiConPaginazione();
				
				Map<String, String> properties = new HashMap<String, String>();
				properties.put("id_transazione",
						this.getIdTransazione());
				filter.setProperties(properties);
				
				filter.setApplicativo(null);
				if(EsitoTransazioneName.isConsegnaMultipla(esitoTransactionName)) {
					filter.setCheckApplicativoIsNull(true);
				}
				else {
					filter.setCheckApplicativoIsNull(false);
				}
				filter.setSeverita(LogLevels.SEVERITA_ERROR_INTEGRATION);
				filter.setAsc(true);
				
				try {
					msgs = driver.getMessaggiDiagnostici(filter);
				}catch(DriverMsgDiagnosticiNotFoundException notFound){
					msgs = new ArrayList<MsgDiagnostico>();
					log.debug(notFound.getMessage(), notFound);
				}
			}catch(Exception e){
				LoggerManager.getPddMonitorCoreLogger().error("Errore durante il recupero dei diagnostici per la ricostruzione del motivo dell'errore: "+e.getMessage(),e);
			}
		}
		
		DatiEsitoTransazione datiEsitoTransazione = convertToDatiEsitoTransazione();
		String dettaglioErroreResult = InfoEsitoTransazioneFormatUtils.getDettaglioErrore(log, datiEsitoTransazione, msgs);	
		
		if(dettaglioErroreResult!=null) {
			this.dettaglioErrore = dettaglioErroreResult;
		}
		else {
			this.dettaglioErrore = "";
		}
		return this.dettaglioErrore;
		
	}
}
