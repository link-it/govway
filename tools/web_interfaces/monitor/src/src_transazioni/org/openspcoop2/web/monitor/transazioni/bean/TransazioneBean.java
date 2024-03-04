/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2024 Link.it srl (https://link.it).
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
import org.openspcoop2.core.constants.CostantiLabel;
import org.openspcoop2.core.id.IDAccordo;
import org.openspcoop2.core.id.IDServizioApplicativo;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.core.registry.driver.IDAccordoFactory;
import org.openspcoop2.core.transazioni.Transazione;
import org.openspcoop2.core.transazioni.constants.PddRuolo;
import org.openspcoop2.core.transazioni.constants.TipoAPI;
import org.openspcoop2.core.transazioni.utils.TempiElaborazione;
import org.openspcoop2.core.transazioni.utils.TempiElaborazioneUtils;
import org.openspcoop2.generic_project.exception.ExpressionNotImplementedException;
import org.openspcoop2.generic_project.exception.MultipleResultException;
import org.openspcoop2.generic_project.exception.NotImplementedException;
import org.openspcoop2.generic_project.exception.ServiceException;
import org.openspcoop2.monitor.engine.condition.EsitoUtils;
import org.openspcoop2.pdd.core.CostantiPdD;
import org.openspcoop2.pdd.logger.LogLevels;
import org.openspcoop2.pdd.logger.info.DatiEsitoTransazione;
import org.openspcoop2.pdd.logger.info.DatiMittente;
import org.openspcoop2.pdd.logger.info.InfoEsitoTransazioneFormatUtils;
import org.openspcoop2.pdd.logger.info.InfoMittenteFormatUtils;
import org.openspcoop2.protocol.engine.ProtocolFactoryManager;
import org.openspcoop2.protocol.engine.utils.NamingUtils;
import org.openspcoop2.protocol.sdk.IProtocolFactory;
import org.openspcoop2.protocol.sdk.PDNDTokenInfo;
import org.openspcoop2.protocol.sdk.ProtocolException;
import org.openspcoop2.protocol.sdk.constants.EsitoTransazioneName;
import org.openspcoop2.protocol.sdk.diagnostica.DriverMsgDiagnosticiException;
import org.openspcoop2.protocol.sdk.diagnostica.DriverMsgDiagnosticiNotFoundException;
import org.openspcoop2.protocol.sdk.diagnostica.FiltroRicercaDiagnosticiConPaginazione;
import org.openspcoop2.protocol.sdk.diagnostica.IDiagnosticDriver;
import org.openspcoop2.protocol.sdk.diagnostica.MsgDiagnostico;
import org.openspcoop2.protocol.utils.EsitiProperties;
import org.openspcoop2.protocol.utils.PorteNamingUtils;
import org.openspcoop2.utils.UtilsRuntimeException;
import org.openspcoop2.utils.beans.BlackListElement;
import org.openspcoop2.web.monitor.core.constants.Costanti;
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
	private java.lang.String tokenSubjectLabel = null;
	private java.lang.String tokenClientIdLabel = null;
	private IDServizioApplicativo tokenClient = null;
	private String tokenClientNameLabel = null;
	private String tokenClientOrganizationNameLabel = null;
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
		
		List<BlackListElement> metodiEsclusi = new ArrayList<>(0);
		metodiEsclusi.add(new BlackListElement("setLatenzaTotale", Long.class));
		metodiEsclusi.add(new BlackListElement("setLatenzaServizio", Long.class));
		metodiEsclusi.add(new BlackListElement("setLatenzaPorta", Long.class));
		metodiEsclusi.add(new BlackListElement("setSocketClientAddressLabel", String.class));
		metodiEsclusi.add(new BlackListElement("setTransportClientAddressLabel", String.class));
		metodiEsclusi.add(new BlackListElement("setTrasportoMittenteLabel", String.class));
		metodiEsclusi.add(new BlackListElement("setTipoTrasportoMittenteLabel", String.class));
		metodiEsclusi.add(new BlackListElement("setTokenIssuerLabel", String.class));
		metodiEsclusi.add(new BlackListElement("setTokenSubjectLabel", String.class));
		metodiEsclusi.add(new BlackListElement("setTokenClientIdLabel", String.class));
		metodiEsclusi.add(new BlackListElement("setTokenClient", IDServizioApplicativo.class));
		metodiEsclusi.add(new BlackListElement("setTokenClientNameLabel", String.class));
		metodiEsclusi.add(new BlackListElement("setTokenClientOrganizationNameLabel", String.class));
		metodiEsclusi.add(new BlackListElement("setTokenClientWithOrganizationNameLabel", String.class));
		metodiEsclusi.add(new BlackListElement("setTokenUsernameLabel", String.class));
		metodiEsclusi.add(new BlackListElement("setTokenMailLabel", String.class));
		metodiEsclusi.add(new BlackListElement("setPdndOrganization", String.class));
		metodiEsclusi.add(new BlackListElement("setPdndOrganizationName", String.class));
		metodiEsclusi.add(new BlackListElement("setPdndOrganizationExternalId", String.class));
		metodiEsclusi.add(new BlackListElement("setPdndOrganizationCategory", String.class));
		metodiEsclusi.add(new BlackListElement("setEventiLabel", String.class));
		metodiEsclusi.add(new BlackListElement("setGruppiLabel", String.class));
		metodiEsclusi.add(new BlackListElement("setOperazioneLabel", String.class));
		metodiEsclusi.add(new BlackListElement("setListaGruppi", List.class));
		metodiEsclusi.add(new BlackListElement("setConsegnaMultipla", boolean.class));
		
		BeanUtils.copy(this, transazione, metodiEsclusi);
	}

	private String formatHTML(String tmp) {
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
	
	public Long getLatenzaTotale() {
		if(this.latenzaTotale == null &&
			this.dataUscitaRisposta != null && this.dataIngressoRichiesta != null){
			this.latenzaTotale = this.dataUscitaRisposta.getTime() - this.dataIngressoRichiesta.getTime();
		}

		if(this.latenzaTotale == null)
			return -1L;

		return this.latenzaTotale;
	}

	public void setLatenzaTotale(Long latenzaTotale) {
		this.latenzaTotale = latenzaTotale;
	}

	public Long getLatenzaServizio() {
		if(this.latenzaServizio == null &&
			this.dataUscitaRichiesta != null && this.dataIngressoRisposta != null){
			this.latenzaServizio = this.dataIngressoRisposta.getTime() - this.dataUscitaRichiesta.getTime();
		}

		if(this.latenzaServizio == null)
			return -1L;

		return this.latenzaServizio;
	}

	public void setLatenzaServizio(Long latenzaServizio) {
		this.latenzaServizio = latenzaServizio;
	}

	public Long getLatenzaPorta() {
		if(this.latenzaPorta == null &&
			this.getLatenzaTotale() != null && this.getLatenzaTotale()>=0) {
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
		return !isEsitoSendInCorso() && 
				/**!isEsitoSendResponseInCorso() && rimane un esito OK, poi va aggiunta una categorizzazione*/  
				TransazioniEsitiUtils.isEsitoOk(this.getEsito(), this.getProtocollo());
	}
	public boolean isEsitoFaultApplicativo(){	
		return TransazioniEsitiUtils.isEsitoFaultApplicativo(this.getEsito(), this.getProtocollo());
	}
	public boolean isEsitoKo(){	
		return TransazioniEsitiUtils.isEsitoKo(this.getEsito(), this.getProtocollo());
	}
	public boolean isEsitoSendInCorso(){	
		String esitoContesto = getEsitoContesto();
		return EsitoUtils.isFaseRequestIn(esitoContesto) || EsitoUtils.isFaseRequestOut(esitoContesto);
	}
	public boolean isEsitoSendResponseInCorso(){	
		String esitoContesto = getEsitoContesto();
		return EsitoUtils.isFaseResponseOut(esitoContesto);
	}
	
	public String getEsitoIcona() {
		String esitoContesto = getEsitoContesto();
		if(EsitoUtils.isFaseRequestIn(esitoContesto) || EsitoUtils.isFaseRequestOut(esitoContesto)) {
			return MessageManager.getInstance().getMessage(TransazioniCostanti.TRANSAZIONI_ELENCO_ESITO_SEND_ICON_KEY);
		}
		if(EsitoUtils.isFaseResponseOut(esitoContesto)) {
			return MessageManager.getInstance().getMessage(TransazioniCostanti.TRANSAZIONI_ELENCO_ESITO_SEND_RESPONSE_ICON_KEY);
		}
		if(TransazioniEsitiUtils.isEsitoOk(this.getEsito(), this.getProtocollo()))
			return MessageManager.getInstance().getMessage(TransazioniCostanti.TRANSAZIONI_ELENCO_ESITO_OK_ICON_KEY);
		if(TransazioniEsitiUtils.isEsitoFaultApplicativo(this.getEsito(), this.getProtocollo()))
			return MessageManager.getInstance().getMessage(TransazioniCostanti.TRANSAZIONI_ELENCO_ESITO_ERROR_ICON_KEY);
		if(TransazioniEsitiUtils.isEsitoKo(this.getEsito(), this.getProtocollo()))
			return MessageManager.getInstance().getMessage(TransazioniCostanti.TRANSAZIONI_ELENCO_ESITO_WARNING_ICON_KEY);

		return MessageManager.getInstance().getMessage(TransazioniCostanti.TRANSAZIONI_ELENCO_ESITO_WARNING_ICON_KEY);
	}

	public java.lang.String getEsitoLabel() {
		
		String esitoContesto = getEsitoContesto();
		if(EsitoUtils.isFaseRequestIn(esitoContesto)) {
			return CostantiLabel.LABEL_CONFIGURAZIONE_AVANZATA_REQ_IN;
		}
		else if(EsitoUtils.isFaseRequestOut(esitoContesto)) {
			return CostantiLabel.LABEL_CONFIGURAZIONE_AVANZATA_REQ_OUT;
		}
		else {
		
			return TransazioniEsitiUtils.getEsitoLabel(this.getEsito(), this.getProtocollo());
			
		}
	}
	
	public java.lang.String getEsitoLabelSyntetic() {
		
		String esitoContesto = getEsitoContesto();
		if(EsitoUtils.isFaseRequestIn(esitoContesto)) {
			return CostantiLabel.LABEL_CONFIGURAZIONE_AVANZATA_REQ_IN;
		}
		else if(EsitoUtils.isFaseRequestOut(esitoContesto)) {
			return CostantiLabel.LABEL_CONFIGURAZIONE_AVANZATA_REQ_OUT;
		}
		else {
		
			Integer httpStatus = null;
			if(this.getCodiceRispostaUscita()!=null && !"".equals(this.getCodiceRispostaUscita())) {
				try {
					httpStatus = Integer.valueOf(this.getCodiceRispostaUscita());
				}catch(Exception t) {
					// ignore
				}
			}
			return TransazioniEsitiUtils.getEsitoLabelSyntetic(this.getEsito(), this.getProtocollo(), httpStatus, this.getTipoApi());
			
		}
	}
	
	public java.lang.String getEsitoLabelDescription() {
		return getEsitoLabelDescription(false);
	}
	public java.lang.String getEsitoLabelDescriptionCheckResponseOut() {
		return getEsitoLabelDescription(true);
	}
	private java.lang.String getEsitoLabelDescription(boolean verifyResponseOut) {
		
		String esitoContesto = getEsitoContesto();
		if(EsitoUtils.isFaseRequestIn(esitoContesto)) {
			return CostantiLabel.LABEL_CONFIGURAZIONE_AVANZATA_REQ_IN;
		}
		else if(EsitoUtils.isFaseRequestOut(esitoContesto)) {
			return CostantiLabel.LABEL_CONFIGURAZIONE_AVANZATA_REQ_OUT;
		}
		else {
		
			String prefixResponseOut = "";
			if(verifyResponseOut && EsitoUtils.isFaseResponseOut(esitoContesto)) {
				prefixResponseOut = CostantiLabel.LABEL_CONFIGURAZIONE_AVANZATA_RES_OUT+"<BR/>";
			}
			
			Integer httpStatus = null;
			if(this.getCodiceRispostaUscita()!=null && !"".equals(this.getCodiceRispostaUscita())) {
				try {
					httpStatus = Integer.valueOf(this.getCodiceRispostaUscita());
				}catch(Exception t) {
					// ignore
				}
			}
			Integer httpInStatus = null;
			if(this.getCodiceRispostaIngresso()!=null && !"".equals(this.getCodiceRispostaIngresso())) {
				try {
					httpInStatus = Integer.valueOf(this.getCodiceRispostaIngresso());
				}catch(Exception t) {
					// ignore
				}
			}
			return prefixResponseOut+TransazioniEsitiUtils.getEsitoLabelDescription(this.getEsito(), this.getProtocollo(), httpStatus, httpInStatus, this.getTipoApi());
			
		}
	}
	
	public boolean isShowContesto(){
		try{
			if(EsitiProperties.getInstanceFromProtocolName(LoggerManager.getPddMonitorCoreLogger(),this.getProtocollo()).getEsitiTransactionContextCode().size()>1) {
				return true;
			}
			else {
				String esitoContesto = getEsitoContesto();
				/** return TransazioneUtilities.isFaseIntermedia(esitoContesto);*/
				// la req_in e req_out viene riportata nell'esito
				return EsitoUtils.isFaseResponseOut(esitoContesto);
			}
		}catch(Exception e){
			LoggerManager.getPddMonitorCoreLogger().error("Errore durante il calcolo dei contesti: "+e.getMessage(),e);
			return false;
		}
	}
	
	public java.lang.String getEsitoContestoLabel() {
		String esitoContesto = getEsitoContesto();
		boolean moreContext = false;
		try{
			moreContext = EsitiProperties.getInstanceFromProtocolName(LoggerManager.getPddMonitorCoreLogger(),this.getProtocollo()).getEsitiTransactionContextCode().size()>1;
		}catch(Exception e){
			LoggerManager.getPddMonitorCoreLogger().error("Errore durante il calcolo dei contesti: "+e.getMessage(),e);
		}
		if(EsitoUtils.isFaseRequestIn(esitoContesto)) {
			/**
			if(moreContext) {
				return TransazioniEsitiUtils.getEsitoContestoLabel(TransazioneUtilities.getRawEsitoContext(esitoContesto), this.getProtocollo()) + " - " + CostantiLabel.LABEL_CONFIGURAZIONE_AVANZATA_REQ_IN;
			}
			else {
				return CostantiLabel.LABEL_CONFIGURAZIONE_AVANZATA_REQ_IN;
			}*/
			// la req_in e req_out viene riportata nell'esito
			return TransazioniEsitiUtils.getEsitoContestoLabel(EsitoUtils.getRawEsitoContext(esitoContesto), this.getProtocollo());
		}
		else if(EsitoUtils.isFaseRequestOut(esitoContesto)) {
			/**if(moreContext) {
				return TransazioniEsitiUtils.getEsitoContestoLabel(TransazioneUtilities.getRawEsitoContext(esitoContesto), this.getProtocollo()) + " - " + CostantiLabel.LABEL_CONFIGURAZIONE_AVANZATA_REQ_OUT;
			}
			else {
				return CostantiLabel.LABEL_CONFIGURAZIONE_AVANZATA_REQ_OUT;
			}*/
			// la req_in e req_out viene riportata nell'esito
			return TransazioniEsitiUtils.getEsitoContestoLabel(EsitoUtils.getRawEsitoContext(esitoContesto), this.getProtocollo());
		}
		else if(EsitoUtils.isFaseResponseOut(esitoContesto)) {
			if(moreContext) {
				return TransazioniEsitiUtils.getEsitoContestoLabel(EsitoUtils.getRawEsitoContext(esitoContesto), this.getProtocollo()) + " - " + CostantiLabel.LABEL_CONFIGURAZIONE_AVANZATA_RES_OUT;
			}
			else {
				return CostantiLabel.LABEL_CONFIGURAZIONE_AVANZATA_RES_OUT;
			}
		}
		else {
			return TransazioniEsitiUtils.getEsitoContestoLabel(this.getEsitoContesto(), this.getProtocollo());
		}
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
		String prefixNull = "null_";
		if(nomePorta!=null && nomePorta.startsWith(prefixNull) && (nomePorta.length()>prefixNull.length())){
			return nomePorta.substring(prefixNull.length());
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

	public String getPortaLabel() throws ProtocolException {
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

	public java.lang.String getTokenSubjectLabel() {
		return this.tokenSubjectLabel;
	}

	public void setTokenSubjectLabel(java.lang.String tokenSubjectLabel) {
		this.tokenSubjectLabel = tokenSubjectLabel;
	}
	
	public java.lang.String getTokenClientIdLabel() {
		return this.tokenClientIdLabel;
	}

	public void setTokenClientIdLabel(java.lang.String tokenClientIdLabel) {
		this.tokenClientIdLabel = tokenClientIdLabel;
	}

	public IDServizioApplicativo getTokenClient() {
		return this.tokenClient;
	}

	public void setTokenClient(IDServizioApplicativo tokenClient) {
		this.tokenClient = tokenClient;
	}

	public String getTokenClientNameLabel() {
		return this.tokenClientNameLabel;
	}

	public void setTokenClientNameLabel(String tokenClientNameLabel) {
		this.tokenClientNameLabel = tokenClientNameLabel;
	}

	public String getTokenClientOrganizationNameLabel() {
		return this.tokenClientOrganizationNameLabel;
	}

	public void setTokenClientOrganizationNameLabel(String tokenClientOrganizationNameLabel) {
		this.tokenClientOrganizationNameLabel = tokenClientOrganizationNameLabel;
	}
	
	public String getTokenClientWithOrganizationNameLabel() {
		if(this.tokenClientNameLabel!=null && StringUtils.isNotEmpty(this.tokenClientNameLabel)) {
			if(this.tokenClientOrganizationNameLabel!=null && StringUtils.isNotEmpty(this.tokenClientOrganizationNameLabel)) {
				boolean equals = false;
				try {
					equals = this.tokenClientOrganizationNameLabel.equals(this.getSoggettoFruitore());
				}catch(Exception t) {
					// ignore
				}
				if(!equals) {
					return this.tokenClientNameLabel + NamingUtils.LABEL_DOMINIO + this.tokenClientOrganizationNameLabel;
				}
				else {
					return this.tokenClientNameLabel;
				}
			}
			return this.tokenClientNameLabel;
		}
		return null;
	}

	public void setTokenClientWithOrganizationNameLabel(String tokenClientOrganizationNameLabel) {
		// nop
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

	private String pdndOrganizationName;
	public void setPdndOrganizationName(String pdndOrganizationName) {
		this.pdndOrganizationName = pdndOrganizationName;
	}
	public String getPdndOrganizationName() {
		if(this.pdndOrganizationName!=null) {
			
			boolean esaminaTokenInfo = org.openspcoop2.protocol.engine.constants.Costanti.MODIPA_PROTOCOL_NAME.equals(this.getProtocollo()) &&
					(Costanti.LABEL_INFORMAZIONE_NON_DISPONIBILE.equals(this.pdndOrganizationName) || StringUtils.isEmpty(this.pdndOrganizationName));
			
			if(esaminaTokenInfo &&
				this.tokenInfo!=null && StringUtils.isNotEmpty(this.tokenInfo) ) {
				try {
					Logger log = LoggerManager.getPddMonitorCoreLogger();
					this.pdndOrganizationName = PDNDTokenInfo.readOrganizationNameFromTokenInfo(log, this.tokenInfo);
					
				}catch(Exception e) {
					// ignore
				}
			}
			
			if(StringUtils.isEmpty(this.pdndOrganizationName)) {
				return null;
			}
			return this.pdndOrganizationName;
			
		}
		else {
			this.pdndOrganizationName = "";
		}
		return this.pdndOrganizationName;
	}
	
	private String pdndOrganizationExternalId;
	public void setPdndOrganizationExternalId(String pdndOrganizationExternalId) {
		this.pdndOrganizationExternalId = pdndOrganizationExternalId;
	}
	public String getPdndOrganizationExternalId() {
		if(this.pdndOrganizationExternalId!=null) {
			
			boolean esaminaTokenInfo = org.openspcoop2.protocol.engine.constants.Costanti.MODIPA_PROTOCOL_NAME.equals(this.getProtocollo()) &&
					(Costanti.LABEL_INFORMAZIONE_NON_DISPONIBILE.equals(this.pdndOrganizationExternalId) || StringUtils.isEmpty(this.pdndOrganizationExternalId));
			
			if(esaminaTokenInfo &&
				this.tokenInfo!=null && StringUtils.isNotEmpty(this.tokenInfo)) {
				
				setPdndOrganizationExternalIdFromTokenInfo();
			}
			
			if(StringUtils.isEmpty(this.pdndOrganizationExternalId)) {
				return null;
			}
			return this.pdndOrganizationExternalId;
			
		}
		else {
			this.pdndOrganizationExternalId = "";
		}
		return this.pdndOrganizationExternalId;
	}
	private void setPdndOrganizationExternalIdFromTokenInfo() {
		try {
			Logger log = LoggerManager.getPddMonitorCoreLogger();
			String origin = PDNDTokenInfo.readOrganizationExternalOriginFromTokenInfo(log, this.tokenInfo);
			String id = PDNDTokenInfo.readOrganizationExternalIdFromTokenInfo(log, this.tokenInfo);
			if(origin!=null && StringUtils.isNotEmpty(origin) &&
					id!=null && StringUtils.isNotEmpty(id)) {
				this.pdndOrganizationExternalId = origin + " "+id;
			}
			else if(origin!=null && StringUtils.isNotEmpty(origin)) {
				this.pdndOrganizationExternalId = origin;
			}
			else if(id!=null && StringUtils.isNotEmpty(id)) {
				this.pdndOrganizationExternalId = id;
			}
		}catch(Exception e) {
			// ignore
		}
	}
	
	private String pdndOrganizationCategory;
	public void setPdndOrganizationCategory(String pdndOrganizationCategory) {
		this.pdndOrganizationCategory = pdndOrganizationCategory;
	}
	public String getPdndOrganizationCategory() {
		if(this.pdndOrganizationCategory!=null) {
			
			boolean esaminaTokenInfo = org.openspcoop2.protocol.engine.constants.Costanti.MODIPA_PROTOCOL_NAME.equals(this.getProtocollo()) &&
					(Costanti.LABEL_INFORMAZIONE_NON_DISPONIBILE.equals(this.pdndOrganizationCategory) || StringUtils.isEmpty(this.pdndOrganizationCategory));
			
			if(esaminaTokenInfo &&
				this.tokenInfo!=null && StringUtils.isNotEmpty(this.tokenInfo)) {
				try {
					Logger log = LoggerManager.getPddMonitorCoreLogger();
					this.pdndOrganizationCategory = PDNDTokenInfo.readOrganizationCategoryFromTokenInfo(log, this.tokenInfo);
				}catch(Exception e) {
					// ignore
				}
			}
			
			if(StringUtils.isEmpty(this.pdndOrganizationCategory)) {
				return null;
			}
			return this.pdndOrganizationCategory;
			
		}
		else {
			this.pdndOrganizationCategory = "";
		}
		return this.pdndOrganizationCategory;
	}
	
	public void setPdndOrganization(String pdndOrganizationCategory) {
		// nop
	}
	public String getPdndOrganization() {
		StringBuilder sb = new StringBuilder();
		
		String name = getPdndOrganizationName();
		if(name!=null && StringUtils.isNotEmpty(name)) {
			sb.append(name);
		}
		
		String cat = getPdndOrganizationCategory();
		if(cat!=null && StringUtils.isNotEmpty(cat)) {
			if(sb.length()>0){
				sb.append("<BR/>");
			}
			sb.append("category: ");
			sb.append(cat);
		}
		
		String extId = getPdndOrganizationExternalId();
		if(extId!=null && StringUtils.isNotEmpty(extId)) {
			if(sb.length()>0){
				sb.append("<BR/>");
			}
			sb.append("externalId: ");
			sb.append(extId);
		}
		
		return sb.length()>0 ? sb.toString() : null;
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
				return formatHTML(tmp);
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
		List<String> l = new ArrayList<>();
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
						int httpStatus = Integer.parseInt(sub);
						if(httpStatus>0) {
							return httpStatus;
						}
					}catch(Exception t) {
						// ignore
					}
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
						int httpStatus = Integer.parseInt(sub);
						if(httpStatus>0) {
							return httpStatus;
						}
					}catch(Exception t) {
						// ignore
					}
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
						int tipoApi = Integer.parseInt(sub);
						if(tipoApi>0) {
							return tipoApi;
						}
					}catch(Exception t) {
						// ignore
					}
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
				return formatHTML(tmp);
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
		List<Gruppo> gruppiObj = new ArrayList<>();
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
	
	public void setListaGruppi(List<Gruppo> lista) {
		// nop
	}
	
	
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
			}catch(Exception t) {
				// ignore
			}
		}
		return false;
	}
	
	public String getApi() throws Exception{
		String parteComune = this.getUriAccordoServizio();
		if(parteComune!=null && !"".equals(parteComune)) {
			try {
				IDAccordo idAccordo = IDAccordoFactory.getInstance().getIDAccordoFromUri(parteComune);
				return NamingUtils.getLabelAccordoServizioParteComune(idAccordo);
			}catch(Exception t) {
				// ignore
			}
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
	
	public void normalizeRichiedenteInfo(Transazione t, TransazioneBean transazioneBean, TransazioniService transazioniService) throws ServiceException, MultipleResultException, NotImplementedException, ExpressionNotImplementedException {
		
		/**
		 * Logica (vedi classe org.openspcoop2.pdd.logger.info.InfoMittenteFormatUtils):
		 * - prevale l'utente descritto in forma umana (username);
		 * - altrimenti prevale un eventuale applicativo identificato (registrato su GovWay) dando precedenza ad un applicativo token rispetto ad un applicativo di trasporto;
		 * - altrimenti prevalgono le informazioni di un eventuale token presente rispetto al trasporto; se si tratta di client credentials (subject non presente o client_id=subject) prevale l'informazione sul client-id altrimenti quella sul subject.
		 */
		
		// 1) Username del Token
		String sTokenUsername = getTokenUsername();
		if(StringUtils.isNotEmpty(sTokenUsername)) {
			transazioniService.normalizeInfoTransazioniFromCredenzialiMittenteTokenUsername(transazioneBean, t);
			return;
		}
		
		// 2) Applicativo Token identificato tramite ClientID
		String sTokenClientId = getTokenClientId();
		if(StringUtils.isNotEmpty(sTokenClientId)) {
			transazioniService.normalizeInfoTransazioniFromCredenzialiMittenteTokenClientID(transazioneBean, t);
			transazioniService.normalizeInfoTransazioniFromCredenzialiMittenteTokenPdnd(transazioneBean, t, false);
			return;
		}
		
		// 3) Applicativo Fruitore
		String sApplicativoFruitore = getServizioApplicativoFruitore();
		if(StringUtils.isNotEmpty(sApplicativoFruitore)) {
			return;
		}
		
		// 4) ClientId/Subject/Issuer del Token
		String sTokenSubject = getTokenSubject();
		boolean clientCredentialsFlow = false;
		if(StringUtils.isNotEmpty(sTokenClientId)) {
			clientCredentialsFlow = (sTokenSubject==null) || (StringUtils.isEmpty(sTokenSubject)) || (sTokenSubject.equals(sTokenClientId));
		}
		
		// 4a) Client ID, per il caso di ClientCredential
		if(clientCredentialsFlow &&
			StringUtils.isNotEmpty(sTokenClientId)) {
			// gia' normalizzato prima
			return;
		}
		
		// 4b) Subject/Issuer del Token
		if(StringUtils.isNotEmpty(sTokenSubject)) {
			transazioniService.normalizeInfoTransazioniFromCredenzialiMittenteTokenSubject(transazioneBean, t);
			String sTokenIssuer = getTokenIssuer();
			if(StringUtils.isNotEmpty(sTokenIssuer)) {
				transazioniService.normalizeInfoTransazioniFromCredenzialiMittenteTokenIssuer(transazioneBean, t);
			}
			return;
		}
		
		// 4c) Client ID, per il caso diverso da ClientCredential
		if(!clientCredentialsFlow &&
			StringUtils.isNotEmpty(sTokenClientId)) {
			// gia' normalizzato prima
			return;
		}
		
		// 5) Credenziali dell'autenticazione di trasporto
		String sTrasportoMittente = getTrasportoMittente();
		if(StringUtils.isNotEmpty(sTrasportoMittente)) {
			transazioniService.normalizeInfoTransazioniFromCredenzialiMittenteTrasporto(transazioneBean, t);
		}

	}
	
	public boolean isVisualizzaTextAreaRichiedente() throws Exception {
		String de = this.getRichiedente();
		return StringUtils.isNotEmpty(de) && de.length() > 150;
	}
	
	public DatiMittente convertToDatiMittente() {
		
		DatiMittente datiMittente = new DatiMittente();
		
		datiMittente.setTokenUsername(getTokenUsernameLabel());
		datiMittente.setTokenSubject(getTokenSubjectLabel());
		datiMittente.setTokenIssuer(getTokenIssuerLabel());
		datiMittente.setTokenClientId(getTokenClientIdLabel());
		if(getTokenClient()!=null) {
			datiMittente.setTokenClient(getTokenClient().getNome());
			datiMittente.setTokenClientTipoSoggettoFruitore(getTokenClient().getIdSoggettoProprietario().getTipo());
			datiMittente.setTokenClientNomeSoggettoFruitore(getTokenClient().getIdSoggettoProprietario().getNome());
			try {
				datiMittente.setTokenClientSoggettoFruitore(getTokenClientOrganizationNameLabel());
			}catch(Exception e) {
				throw new UtilsRuntimeException(e.getMessage(),e);
			}
		}
		
		datiMittente.setPdndOrganizationName(getPdndOrganizationName());
				
		datiMittente.setTipoTrasportoMittente(getTipoTrasportoMittenteLabel());
		datiMittente.setTrasportoMittente(getTrasportoMittenteLabel());
		
		datiMittente.setServizioApplicativoFruitore(getServizioApplicativoFruitore());
		
		try {
			datiMittente.setSoggettoFruitore(getSoggettoFruitore());
		}catch(Exception e) {
			throw new UtilsRuntimeException(e.getMessage(),e);
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
	
	
	public String getRichiedente() throws Exception {
				
		DatiMittente datiMittente = this.convertToDatiMittente();
		
		if( (datiMittente.getTokenUsername()==null || StringUtils.isEmpty(datiMittente.getTokenUsername()))
				&&
			StringUtils.isNotEmpty(datiMittente.getTokenClient())) {
			boolean soggettoEqualsTokenSoggetto = false;
			if(datiMittente.getTokenClientSoggettoFruitore()!=null &&
					datiMittente.getTokenClientTipoSoggettoFruitore()!=null && datiMittente.getTokenClientNomeSoggettoFruitore()!=null) {
				soggettoEqualsTokenSoggetto = datiMittente.getTokenClientTipoSoggettoFruitore().equals(datiMittente.getTipoSoggettoFruitore()) && 
						datiMittente.getTokenClientNomeSoggettoFruitore().equals(datiMittente.getNomeSoggettoFruitore());
			}
			if(!soggettoEqualsTokenSoggetto) {
				return getLabelRichiedenteConFruitore();
			}
		}
		
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
			
			if(org.openspcoop2.core.transazioni.constants.PddRuolo.APPLICATIVA.equals(this.getPddRuolo()) &&
				this.soggettoPddMonitor!=null && StringUtils.isNotEmpty(this.getTipoSoggettoErogatore()) && StringUtils.isNotEmpty(this.getNomeSoggettoErogatore())) {
				IDSoggetto idSoggettoErogatore = new IDSoggetto(this.getTipoSoggettoErogatore(), this.getNomeSoggettoErogatore());
				addErogatore = !this.soggettoPddMonitor.equals(idSoggettoErogatore.toString());
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
						/**System.out.println("LETTO ["+method+"] ["+path+"]");*/
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
	
	public String getLabelOperazioneConGestioneNonPresenza() {
		
		String op = getOperazioneLabel();
		if(StringUtils.isNotEmpty(op)) {
			
			return this.getOperazioneLabel();
			
		}
		else {
			return "-";
		}
		
	}
	
	public String getCssColonnaEsito() {
		String esitoContesto = getEsitoContesto();
		if(EsitoUtils.isFaseRequestIn(esitoContesto) || EsitoUtils.isFaseRequestOut(esitoContesto)) {
			return TransazioniCostanti.TRANSAZIONI_ELENCO_CUSTOM_CLASSE_CSS_COL_ESITO_OK;
		}
		if(this.isEsitoOk())
			return TransazioniCostanti.TRANSAZIONI_ELENCO_CUSTOM_CLASSE_CSS_COL_ESITO_OK;
		if(this.isEsitoFaultApplicativo())
			return TransazioniCostanti.TRANSAZIONI_ELENCO_CUSTOM_CLASSE_CSS_COL_ESITO_FAULT;
		if(this.isEsitoKo())
			return TransazioniCostanti.TRANSAZIONI_ELENCO_CUSTOM_CLASSE_CSS_COL_ESITO_ERRORE;
		
		return TransazioniCostanti.TRANSAZIONI_ELENCO_CUSTOM_CLASSE_CSS_COL_ESITO_ERRORE;
	}
	
	public String getCssColonnaLatenza() {
		String esitoContesto = getEsitoContesto();
		if(EsitoUtils.isFaseRequestIn(esitoContesto) || EsitoUtils.isFaseRequestOut(esitoContesto)) {
			return TransazioniCostanti.TRANSAZIONI_ELENCO_CUSTOM_CLASSE_CSS_COL_LATENZA_OK;
		}
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
			EsitiProperties esitiProperties = EsitiProperties.getInstanceFromProtocolName(LoggerManager.getPddMonitorCoreLogger(),this.getProtocollo());
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
			EsitiProperties esitiProperties = EsitiProperties.getInstanceFromProtocolName(LoggerManager.getPddMonitorCoreLogger(),this.getProtocollo());
			String name = esitiProperties.getEsitoName(this.getEsito());
			EsitoTransazioneName esitoName = EsitoTransazioneName.convertoTo(name);
			return EsitoTransazioneName.isSavedInMessageBox(esitoName);
		}catch(Exception e){
			LoggerManager.getPddMonitorCoreLogger().error("Errore durante il check saved in message box: "+e.getMessage(),e);
			return false;
		}
	}

	public void setConsegnaMultipla(boolean consegnaMultipla) {
		// nop
	}
	
	public String getLabelDettaglioErrore() {
		return this.isEsitoOk() ? "Dettaglio Anomalia" : "Dettaglio Errore";
	}
	
	public boolean isVisualizzaTextAreaDettaglioErrore() {
		String de = this.getDettaglioErrore();
		return StringUtils.isNotEmpty(de) && de.length() > 150;
	}

	private String dettaglioErrore;
	public String getDettaglioErrore() {
		
		if(this.dettaglioErrore!=null) {
			return this.dettaglioErrore;
		}
		
		return getDettaglioErroreEngine(null);
	}
	public String getDettaglioErrore(List<MsgDiagnostico> msgs) {
		
		if(this.dettaglioErrore!=null) {
			return this.dettaglioErrore;
		}
		
		return getDettaglioErroreEngine(msgs);
	}
	private synchronized String getDettaglioErroreEngine(List<MsgDiagnostico> msgsParams) {
		
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
				esitiProperties = EsitiProperties.getInstanceFromProtocolName(log, this.getProtocollo());
				esitoTransactionName = esitiProperties.getEsitoTransazioneName(this.getEsito());
			}catch(Exception e){
				log.error("Errore durante il recupero dell'esito della transazione: "+e.getMessage(),e);
				return ""; // non dovrebbe mai succedere
			}
			try{
				PddMonitorProperties govwayMonitorProperties = PddMonitorProperties.getInstance(log);
				IDiagnosticDriver driver = govwayMonitorProperties.getDriverMsgDiagnostici();
				
				FiltroRicercaDiagnosticiConPaginazione filter = new FiltroRicercaDiagnosticiConPaginazione();
				
				Map<String, String> properties = new HashMap<>();
				properties.put("id_transazione",
						this.getIdTransazione());
				filter.setProperties(properties);
				
				filter.setApplicativo(null);
				filter.setCheckApplicativoIsNull(EsitoTransazioneName.isConsegnaMultipla(esitoTransactionName));
				
				filter.setSeverita(LogLevels.SEVERITA_ERROR_INTEGRATION);
				filter.setAsc(true);
				
				msgs = getDiagnostici(driver, filter, log);
				
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
	
	private List<MsgDiagnostico> getDiagnostici(IDiagnosticDriver driver, FiltroRicercaDiagnosticiConPaginazione filter, Logger log) throws DriverMsgDiagnosticiException{
		List<MsgDiagnostico> msgs = null;
		try {
			msgs = driver.getMessaggiDiagnostici(filter);
		}catch(DriverMsgDiagnosticiNotFoundException notFound){
			msgs = new ArrayList<>();
			log.debug(notFound.getMessage(), notFound);
		}
		return msgs;
	}
}
