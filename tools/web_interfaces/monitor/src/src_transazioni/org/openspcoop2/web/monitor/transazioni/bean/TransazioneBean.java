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
package org.openspcoop2.web.monitor.transazioni.bean;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.openspcoop2.core.commons.search.IdAccordoServizioParteComune;
import org.openspcoop2.core.commons.search.IdSoggetto;
import org.openspcoop2.core.commons.search.Resource;
import org.openspcoop2.core.commons.search.dao.IResourceServiceSearch;
import org.openspcoop2.core.config.constants.TipoAutenticazione;
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
import org.openspcoop2.generic_project.expression.IPaginatedExpression;
import org.openspcoop2.pdd.core.autenticazione.ApiKeyUtilities;
import org.openspcoop2.pdd.logger.LogLevels;
import org.openspcoop2.pdd.logger.MsgDiagnosticiProperties;
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
import org.openspcoop2.utils.certificate.CertificateUtils;
import org.openspcoop2.utils.certificate.PrincipalType;
import org.openspcoop2.web.monitor.core.core.PddMonitorProperties;
import org.openspcoop2.web.monitor.core.core.Utility;
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
		return TransazioniEsitiUtils.getEsitoLabelSyntetic(this.getEsito(), this.getProtocollo());
	}
	
	public java.lang.String getEsitoLabelDescription() {
		return TransazioniEsitiUtils.getEsitoLabelDescription(this.getEsito(), this.getProtocollo());
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
	
	public String getRichiedente() {
				
		// 1) Username del Token
		String sTokenUsername = getTokenUsernameLabel();
		if(StringUtils.isNotEmpty(sTokenUsername)) {
			return sTokenUsername;
		}
		
		// 2) Subject/Issuer del Token
		String sTokenSubject = getTokenSubjectLabel();
		if(StringUtils.isNotEmpty(sTokenSubject)) {
			
			String sTokenIssuer = getTokenIssuerLabel();
			if(StringUtils.isNotEmpty(sTokenIssuer)) {
				return sTokenSubject + NamingUtils.LABEL_DOMINIO + sTokenIssuer;
			}
			else {
				return sTokenSubject;
			}
		}
		
		// 3) Applicativo Fruitore
		String sApplicativoFruitore = getServizioApplicativoFruitore();
		if(StringUtils.isNotEmpty(sApplicativoFruitore)) {
			return sApplicativoFruitore;
		}
		
		// 4) Credenziali dell'autenticazione di trasporto
		// volutamente uso l'id autenticato.
		// se l'api è pubblica non deve essere visualizzata questa informazione!
		String sTrasportoMittente = getTrasportoMittenteLabel();
		String sTipoTrasportoMittente = getTipoTrasportoMittenteLabel();
		if(StringUtils.isNotEmpty(sTrasportoMittente) && StringUtils.isNotEmpty(sTipoTrasportoMittente)) {
			if(sTipoTrasportoMittente.endsWith("_"+TipoAutenticazione.SSL.getValue())) {
				try {
					Hashtable<String, List<String>> l = CertificateUtils.getPrincipalIntoHashtable(sTrasportoMittente, PrincipalType.subject);
					if(l!=null && !l.isEmpty()) {
						List<String> cnList = l.get("CN");
						if(cnList==null || cnList.isEmpty()) {
							cnList = l.get("cn");
						}
						if(cnList==null || cnList.isEmpty()) {
							cnList = l.get("Cn");
						}
						if(cnList==null || cnList.isEmpty()) {
							cnList = l.get("cN");
						}						
						if(cnList!=null && cnList.size()>0) {
							StringBuilder bfList = new StringBuilder();
							for (String s : cnList) {
								if(bfList.length()>0) {
									bfList.append(", ");
								}
								bfList.append(s);
							}
							return bfList.toString();
						}
					}
					return sTrasportoMittente;
				}catch(Throwable t) {	
					return sTrasportoMittente;
				}
			}
			else {
				return sTrasportoMittente;
			}
		}
		
		// 5) Client ID, per il caso di ClientCredential
		String sTokenClientId = getTokenClientIdLabel();
		if(StringUtils.isNotEmpty(sTokenClientId)) {
			return sTokenClientId;
		}
		
		return null;
		
	}
	
	public String getIpRichiedente() {
		
		String t = this.getTransportClientAddress();
		if(StringUtils.isNotEmpty(t)) {
			return t;
		}
		
		String s = this.getSocketClientAddress();
		if(StringUtils.isNotEmpty(s)) {
			return s;
		}
		
		return null;
		
	}
	
	public String getLabelRichiedenteConFruitore() throws Exception {
		StringBuilder bf = new StringBuilder();
		
		String richiedente = getRichiedente();
		if(StringUtils.isNotEmpty(richiedente)) {
			bf.append(richiedente);	
		}
		
		
		
		String sFruitore = getSoggettoFruitore();
		if(StringUtils.isNotEmpty(sFruitore)) {

			boolean addFruitore = true;
						
			if(org.openspcoop2.core.transazioni.constants.PddRuolo.APPLICATIVA.equals(this.getPddRuolo())) {
				
				// L'AppId di un soggetto è già il soggetto. L'informazione sarebbe ridondante.
				String sTrasportoMittente = getTrasportoMittenteLabel();
				if(richiedente!=null && sTrasportoMittente!=null && richiedente.equals(sTrasportoMittente)) { // se e' stato selezionato l'appId
					String sTipoTrasportoMittente = getTipoTrasportoMittenteLabel();
					if(sTipoTrasportoMittente!=null && StringUtils.isNotEmpty(sTipoTrasportoMittente) && 
							sTipoTrasportoMittente.endsWith("_"+TipoAutenticazione.APIKEY.getValue())) {
						// autenticazione api-key
						if(!sTrasportoMittente.contains(ApiKeyUtilities.APPLICATIVO_SOGGETTO_SEPARATOR)) {
							// appId di un soggetto
							bf = new StringBuilder(); // aggiunto solo il soggetto
						}		
					}
				}
				
			}
			else if(org.openspcoop2.core.transazioni.constants.PddRuolo.DELEGATA.equals(this.getPddRuolo())) {
				
				if(this.soggettoPddMonitor!=null && StringUtils.isNotEmpty(this.getTipoSoggettoFruitore()) && StringUtils.isNotEmpty(this.getNomeSoggettoFruitore())) {
					IDSoggetto idSoggettoFruitore = new IDSoggetto(this.getTipoSoggettoFruitore(), this.getNomeSoggettoFruitore());
					addFruitore = !this.soggettoPddMonitor.equals(idSoggettoFruitore.toString());
				}
				
			}
			
			if(addFruitore) {
				if(bf.length()>0) {
					bf.append(NamingUtils.LABEL_DOMINIO);
				}
				
				bf.append(sFruitore);	
			}
		}
		
		return bf.toString();
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
					
					
					IResourceServiceSearch resourceServiceSearch = service.getResourceServiceSearch();
					IPaginatedExpression pagExpr = resourceServiceSearch.newPaginatedExpression();
					pagExpr.equals(Resource.model().NOME, op).
						and().
						equals(Resource.model().ID_ACCORDO_SERVIZIO_PARTE_COMUNE.NOME, idAccordo.getNome()).
						equals(Resource.model().ID_ACCORDO_SERVIZIO_PARTE_COMUNE.VERSIONE, idAccordo.getVersione()).
						equals(Resource.model().ID_ACCORDO_SERVIZIO_PARTE_COMUNE.ID_SOGGETTO.TIPO, idAccordo.getSoggettoReferente().getTipo()).
						equals(Resource.model().ID_ACCORDO_SERVIZIO_PARTE_COMUNE.ID_SOGGETTO.NOME, idAccordo.getSoggettoReferente().getNome());
					List<Map<String,Object>> l = service.getResourceServiceSearch().select(pagExpr, Resource.model().HTTP_METHOD, Resource.model().PATH);
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
		
		return _getDettaglioErrore();
	}
	private synchronized String _getDettaglioErrore() {
		
		if(this.dettaglioErrore!=null) {
			return this.dettaglioErrore;
		}
		
		if(this.isEsitoFaultApplicativo()) {
			
			if(PddRuolo.APPLICATIVA.equals(this.getPddRuolo())) {
				if(this.isVisualizzaFaultIntegrazione()) {
					this.dettaglioErrore = this.getFaultIntegrazionePretty();
					return this.dettaglioErrore;
				}
				else if(this.isVisualizzaFaultCooperazione()) {
					this.dettaglioErrore = this.getFaultCooperazionePretty();
					return this.dettaglioErrore;
				}
			}
			else if(PddRuolo.DELEGATA.equals(this.getPddRuolo())) {
				if(this.isVisualizzaFaultCooperazione()) {
					this.dettaglioErrore = this.getFaultCooperazionePretty();
					return this.dettaglioErrore;
				}
				else if(this.isVisualizzaFaultIntegrazione()) {
					this.dettaglioErrore = this.getFaultIntegrazionePretty();
					return this.dettaglioErrore;
				}
			}
			
		}
		
		// lista diagnostici
		try{
			Logger log = LoggerManager.getPddMonitorCoreLogger();
			PddMonitorProperties govwayMonitorProperties = PddMonitorProperties.getInstance(log);
			IDiagnosticDriver driver = govwayMonitorProperties.getDriverMsgDiagnostici();
			
			FiltroRicercaDiagnosticiConPaginazione filter = new FiltroRicercaDiagnosticiConPaginazione();
			
			Hashtable<String, String> properties = new Hashtable<String, String>();
			properties.put("id_transazione",
					this.getIdTransazione());
			filter.setProperties(properties);
			
			EsitiProperties esitiProperties = EsitiProperties.getInstance(log, this.getProtocollo());
			EsitoTransazioneName esitoTransactionName = esitiProperties.getEsitoTransazioneName(this.getEsito());
			filter.setApplicativo(null);
			if(EsitoTransazioneName.isConsegnaMultipla(esitoTransactionName)) {
				filter.setCheckApplicativoIsNull(true);
			}
			else {
				filter.setCheckApplicativoIsNull(false);
			}
			filter.setSeverita(LogLevels.SEVERITA_ERROR_INTEGRATION);
			filter.setAsc(true);
			
			List<MsgDiagnostico> msgs = null;
			try {
				msgs = driver.getMessaggiDiagnostici(filter);
			}catch(DriverMsgDiagnosticiNotFoundException notFound){
				msgs = new ArrayList<MsgDiagnostico>();
				log.debug(notFound.getMessage(), notFound);
			}
			
			StringBuilder sb = new StringBuilder();
			StringBuilder erroreConnessone = new StringBuilder();
			StringBuilder erroreSegnalaGenerazioneRispostaErrore = new StringBuilder();
			if(msgs!=null && !msgs.isEmpty()) {
				for (MsgDiagnostico msgDiagnostico : msgs) {
					String codice = msgDiagnostico.getCodice();
					
					if(this.isEsitoKo()) {
						// salto gli errori 'warning'
						if(MsgDiagnosticiProperties.MSG_DIAGNOSTICI_WARNING.contains(codice)) {
							continue;
						}
					}
					
					if(EsitoTransazioneName.isErroreRisposta(esitoTransactionName) && MsgDiagnosticiProperties.MSG_DIAGNOSTICI_ERRORE_CONNETTORE.contains(codice)) {
						if(erroreConnessone.length()>0) {
							erroreConnessone.append("\n");
						}
						erroreConnessone.append(msgDiagnostico.getMessaggio());
					}
					else if(MsgDiagnosticiProperties.MSG_DIAGNOSTICI_SEGNALA_GENERATA_RISPOSTA_ERRORE.contains(codice)) {
						if(erroreSegnalaGenerazioneRispostaErrore.length()>0) {
							erroreSegnalaGenerazioneRispostaErrore.append("\n");
						}
						erroreSegnalaGenerazioneRispostaErrore.append(msgDiagnostico.getMessaggio());
					}
					else {
						if(sb.length()>0) {
							sb.append("\n");
						}
						sb.append(msgDiagnostico.getMessaggio());
						
						break; // serializzo solo il primo diagnostico
					}
				}
			}
			if(sb.length()>0) {
				this.dettaglioErrore = sb.toString();
				return this.dettaglioErrore;
			}
			if(erroreConnessone.length()>0) {
				this.dettaglioErrore = erroreConnessone.toString();
				return this.dettaglioErrore;
			}
			if(erroreSegnalaGenerazioneRispostaErrore.length()>0) {
				this.dettaglioErrore = erroreSegnalaGenerazioneRispostaErrore.toString();
				return this.dettaglioErrore;
			}
			
		}catch(Exception e){
			LoggerManager.getPddMonitorCoreLogger().error("Errore durante il recupero dell'errore: "+e.getMessage(),e);
		}
		
		if(!this.isEsitoFaultApplicativo()) {
			
			if(PddRuolo.APPLICATIVA.equals(this.getPddRuolo())) {
				if(this.isVisualizzaFaultIntegrazione()) {
					this.dettaglioErrore = this.getFaultIntegrazionePretty();
					return this.dettaglioErrore;
				}
				else if(this.isVisualizzaFaultCooperazione()) {
					this.dettaglioErrore = this.getFaultCooperazionePretty();
					return this.dettaglioErrore;
				}
			}
			else if(PddRuolo.DELEGATA.equals(this.getPddRuolo())) {
				if(this.isVisualizzaFaultCooperazione()) {
					this.dettaglioErrore = this.getFaultCooperazionePretty();
					return this.dettaglioErrore;
				}
				else if(this.isVisualizzaFaultIntegrazione()) {
					this.dettaglioErrore = this.getFaultIntegrazionePretty();
					return this.dettaglioErrore;
				}
			}
			
		}
		
		this.dettaglioErrore = "";
		return this.dettaglioErrore;
	}
}
