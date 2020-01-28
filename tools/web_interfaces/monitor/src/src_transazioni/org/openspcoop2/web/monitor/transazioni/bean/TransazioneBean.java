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
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.openspcoop2.core.id.IDAccordo;
import org.openspcoop2.core.registry.driver.IDAccordoFactory;
import org.openspcoop2.core.transazioni.Transazione;
import org.openspcoop2.core.transazioni.constants.PddRuolo;
import org.openspcoop2.core.transazioni.constants.TipoAPI;
import org.openspcoop2.core.transazioni.utils.TempiElaborazione;
import org.openspcoop2.core.transazioni.utils.TempiElaborazioneUtils;
import org.openspcoop2.message.constants.MessageType;
import org.openspcoop2.monitor.engine.condition.EsitoUtils;
import org.openspcoop2.protocol.engine.ProtocolFactoryManager;
import org.openspcoop2.protocol.engine.utils.NamingUtils;
import org.openspcoop2.protocol.sdk.IProtocolFactory;
import org.openspcoop2.protocol.sdk.constants.EsitoTransazioneName;
import org.openspcoop2.protocol.utils.EsitiProperties;
import org.openspcoop2.protocol.utils.PorteNamingUtils;
import org.openspcoop2.utils.UtilsException;
import org.openspcoop2.utils.beans.BlackListElement;
import org.openspcoop2.utils.json.JSONUtils;
import org.openspcoop2.web.monitor.core.core.Utils;
import org.openspcoop2.web.monitor.core.logger.LoggerManager;
import org.openspcoop2.web.monitor.core.utils.BeanUtils;
import org.openspcoop2.web.monitor.core.utils.MessageManager;
import org.openspcoop2.web.monitor.transazioni.constants.TransazioniCostanti;

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
	private java.lang.String tokenIssuerLabel = null;
	private java.lang.String tokenClientIdLabel = null;
	private java.lang.String tokenSubjectLabel = null;
	private java.lang.String tokenUsernameLabel = null;
	private java.lang.String tokenMailLabel = null;
	private java.lang.String eventiLabel = null;
	private java.lang.String gruppiLabel = null;

	public TransazioneBean() {
		super();
	}

	public TransazioneBean(Transazione transazione){
		List<BlackListElement> metodiEsclusi = new ArrayList<BlackListElement>(
				0);
		metodiEsclusi.add(new BlackListElement("setLatenzaTotale", Long.class));
		metodiEsclusi.add(new BlackListElement("setLatenzaServizio", Long.class));
		metodiEsclusi.add(new BlackListElement("setLatenzaPorta", Long.class));
		metodiEsclusi.add(new BlackListElement("setTrasportoMittenteLabel", String.class));
		metodiEsclusi.add(new BlackListElement("setTokenIssuerLabel", String.class));
		metodiEsclusi.add(new BlackListElement("setTokenClientIdLabel", String.class));
		metodiEsclusi.add(new BlackListElement("setTokenSubjectLabel", String.class));
		metodiEsclusi.add(new BlackListElement("setTokenUsernameLabel", String.class));
		metodiEsclusi.add(new BlackListElement("setTokenMailLabel", String.class));
		metodiEsclusi.add(new BlackListElement("setEventiLabel", String.class));
		metodiEsclusi.add(new BlackListElement("setGruppiLabel", String.class));

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

		try{
			EsitiProperties esitiProperties = EsitiProperties.getInstance(LoggerManager.getPddMonitorCoreLogger(),this.getProtocollo());
			String name = esitiProperties.getEsitoName(this.getEsito());
			EsitoTransazioneName esitoName = EsitoTransazioneName.convertoTo(name);
			boolean casoSuccesso = esitiProperties.getEsitiCodeOk().contains(this.getEsito());
			if(EsitoTransazioneName.ERRORE_APPLICATIVO.equals(esitoName)){
				//return "icon-alert-orange";
				return "icon-alert-yellow";
			}
			else if(casoSuccesso){
				return "icon-verified-green";
			}
			else{
				return "icon-alert-red";
			}
		}catch(Exception e){
			LoggerManager.getPddMonitorCoreLogger().error("Errore durante il calcolo del layout dell'esito ["+this.getEsito()+"]: "+e.getMessage(),e);
			return "icon-ko";
		}

	}

	public boolean isEsitoOk(){	
		try{
			EsitiProperties esitiProperties = EsitiProperties.getInstance(LoggerManager.getPddMonitorCoreLogger(),this.getProtocollo());
			boolean res = esitiProperties.getEsitiCodeOk_senzaFaultApplicativo().contains(this.getEsito());
			//System.out.println("isEsitoOk:"+res+" (esitoChecked:"+this.getEsito()+")");
			return res;
		}catch(Exception e){
			LoggerManager.getPddMonitorCoreLogger().error("Errore durante il calcolo del layout dell'esito ["+this.getEsito()+"]: "+e.getMessage(),e);
			return false;
		}
	}
	public boolean isEsitoFaultApplicativo(){	
		try{
			EsitiProperties esitiProperties = EsitiProperties.getInstance(LoggerManager.getPddMonitorCoreLogger(),this.getProtocollo());
			boolean res = esitiProperties.getEsitiCodeFaultApplicativo().contains(this.getEsito());
			//System.out.println("isEsitoOk:"+res+" (esitoChecked:"+this.getEsito()+")");
			return res;
		}catch(Exception e){
			LoggerManager.getPddMonitorCoreLogger().error("Errore durante il calcolo del layout dell'esito ["+this.getEsito()+"]: "+e.getMessage(),e);
			return false;
		}
	}
	public boolean isEsitoKo(){	
		try{
			EsitiProperties esitiProperties = EsitiProperties.getInstance(LoggerManager.getPddMonitorCoreLogger(),this.getProtocollo());
			boolean res = esitiProperties.getEsitiCodeKo_senzaFaultApplicativo().contains(this.getEsito());
			//System.out.println("isEsitoOk:"+res+" (esitoChecked:"+this.getEsito()+")");
			return res;
		}catch(Exception e){
			LoggerManager.getPddMonitorCoreLogger().error("Errore durante il calcolo del layout dell'esito ["+this.getEsito()+"]: "+e.getMessage(),e);
			return false;
		}
	}

	public java.lang.String getEsitoLabel() {
		try{
			EsitoUtils esitoUtils = new EsitoUtils(LoggerManager.getPddMonitorCoreLogger(), this.protocollo);
			return esitoUtils.getEsitoLabelFromValue(this.esito);
		}catch(Exception e){
			LoggerManager.getPddMonitorCoreLogger().error("Errore durante il calcolo della label per l'esito ["+this.esito+"]: "+e.getMessage(),e);
			return "Conversione non riuscita";
		}
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
		try{
			EsitoUtils esitoUtils = new EsitoUtils(LoggerManager.getPddMonitorCoreLogger(), this.protocollo);
			return esitoUtils.getEsitoContestoLabelFromValue(this.esitoContesto);
		}catch(Exception e){
			LoggerManager.getPddMonitorCoreLogger().error("Errore durante il calcolo della label per il contesto esito ["+this.esitoContesto+"]: "+e.getMessage(),e);
			return "Conversione non riuscita";
		}
	}
	
	
	public String getFaultCooperazionePretty(){
		String f = super.getFaultCooperazione();
		String toRet = null;
		if(f !=null) {
			StringBuffer contenutoDocumentoStringBuffer = new StringBuffer();
			String errore = Utils.getTestoVisualizzabile(f.getBytes(),contenutoDocumentoStringBuffer, true);
			if(errore!= null)
				return "";

			MessageType messageType= MessageType.XML;
			if(StringUtils.isNotEmpty(super.getFormatoFaultCooperazione())) {
				messageType = MessageType.valueOf(super.getFormatoFaultCooperazione());
			}

			switch (messageType) {
			case BINARY:
			case MIME_MULTIPART:
				// questi due casi dovrebbero essere gestiti sopra 
				break;	
			case JSON:
				JSONUtils jsonUtils = JSONUtils.getInstance(true);
				try {
					toRet = jsonUtils.toString(jsonUtils.getAsNode(f));
				} catch (UtilsException e) {
				}
				break;
			case SOAP_11:
			case SOAP_12:
			case XML:
			default:
				toRet = Utils.prettifyXml(f);
				break;
			}
		}

		if(toRet == null)
			toRet = f != null ? f : "";

		return toRet;
	}

	public boolean isVisualizzaFaultCooperazione(){
		boolean visualizzaMessaggio = true;
		String f = super.getFaultCooperazione();

		if(f == null)
			return false;

		StringBuffer contenutoDocumentoStringBuffer = new StringBuffer();
		String errore = Utils.getTestoVisualizzabile(f.getBytes(),contenutoDocumentoStringBuffer, false);
		if(errore!= null)
			return false;

		//			MessageType messageType= MessageType.XML;
		//			if(StringUtils.isNotEmpty(this.dumpMessaggio.getFormatoMessaggio())) {
		//				messageType = MessageType.valueOf(this.dumpMessaggio.getFormatoMessaggio());
		//			}

		//			switch (messageType) {
		//			case BINARY:
		//			case MIME_MULTIPART:
		//				// questi due casi dovrebbero essere gestiti sopra 
		//				break;	
		//			case JSON:
		//				JSONUtils jsonUtils = JSONUtils.getInstance(true);
		//				try {
		//					toRet = jsonUtils.toString(jsonUtils.getAsNode(this.dumpMessaggio.getBody()));
		//				} catch (UtilsException e) {
		//				}
		//				break;
		//			case SOAP_11:
		//			case SOAP_12:
		//			case XML:
		//			default:
		//				toRet = Utils.prettifyXml(this.dumpMessaggio.getBody());
		//				break;
		//			}

		return visualizzaMessaggio;
	}

	public String getBrushFaultCooperazione() {
		String toRet = null;
		String f = super.getFaultCooperazione();
		if(f!=null) {
			MessageType messageType= MessageType.XML;
			if(StringUtils.isNotEmpty(super.getFormatoFaultCooperazione())) {
				messageType = MessageType.valueOf(super.getFormatoFaultCooperazione());
			}

			switch (messageType) {
			case JSON:
				toRet = "json";
				break;
			case BINARY:
			case MIME_MULTIPART:
				// per ora restituisco il default
			case SOAP_11:
			case SOAP_12:
			case XML:
			default:
				toRet = "xml";
				break;
			}
		}

		return toRet;
	}

	public String getErroreVisualizzaFaultCooperazione(){
		String f = super.getFaultCooperazione();
		if(f!=null) {
			StringBuffer contenutoDocumentoStringBuffer = new StringBuffer();
			String errore = Utils.getTestoVisualizzabile(f.getBytes(),contenutoDocumentoStringBuffer,false);
			return errore;
		}

		return null;
	}

	public String getFaultIntegrazionePretty(){
		String f = super.getFaultIntegrazione();
		String toRet = null;
		if(f !=null) {
			StringBuffer contenutoDocumentoStringBuffer = new StringBuffer();
			String errore = Utils.getTestoVisualizzabile(f.getBytes(),contenutoDocumentoStringBuffer,true);
			if(errore!= null)
				return "";

			MessageType messageType= MessageType.XML;
			if(StringUtils.isNotEmpty(super.getFormatoFaultIntegrazione())) {
				messageType = MessageType.valueOf(super.getFormatoFaultIntegrazione());
			}

			switch (messageType) {
			case BINARY:
			case MIME_MULTIPART:
				// questi due casi dovrebbero essere gestiti sopra 
				break;	
			case JSON:
				JSONUtils jsonUtils = JSONUtils.getInstance(true);
				try {
					toRet = jsonUtils.toString(jsonUtils.getAsNode(f));
				} catch (UtilsException e) {
				}
				break;
			case SOAP_11:
			case SOAP_12:
			case XML:
			default:
				toRet = Utils.prettifyXml(f);
				break;
			}
		}

		if(toRet == null)
			toRet = f != null ? f : "";

		return toRet;
	}

	public boolean isVisualizzaFaultIntegrazione(){
		boolean visualizzaMessaggio = true;
		String f = super.getFaultIntegrazione();

		if(f == null)
			return false;

		StringBuffer contenutoDocumentoStringBuffer = new StringBuffer();
		String errore = Utils.getTestoVisualizzabile(f.getBytes(),contenutoDocumentoStringBuffer,false);
		if(errore!= null)
			return false;

		//			MessageType messageType= MessageType.XML;
		//			if(StringUtils.isNotEmpty(this.dumpMessaggio.getFormatoMessaggio())) {
		//				messageType = MessageType.valueOf(this.dumpMessaggio.getFormatoMessaggio());
		//			}

		//			switch (messageType) {
		//			case BINARY:
		//			case MIME_MULTIPART:
		//				// questi due casi dovrebbero essere gestiti sopra 
		//				break;	
		//			case JSON:
		//				JSONUtils jsonUtils = JSONUtils.getInstance(true);
		//				try {
		//					toRet = jsonUtils.toString(jsonUtils.getAsNode(this.dumpMessaggio.getBody()));
		//				} catch (UtilsException e) {
		//				}
		//				break;
		//			case SOAP_11:
		//			case SOAP_12:
		//			case XML:
		//			default:
		//				toRet = Utils.prettifyXml(this.dumpMessaggio.getBody());
		//				break;
		//			}

		return visualizzaMessaggio;
	}

	public String getBrushFaultIntegrazione() {
		String toRet = null;
		String f = super.getFaultIntegrazione();
		if(f!=null) {
			MessageType messageType= MessageType.XML;
			if(StringUtils.isNotEmpty(super.getFormatoFaultIntegrazione())) {
				messageType = MessageType.valueOf(super.getFormatoFaultIntegrazione());
			}

			switch (messageType) {
			case JSON:
				toRet = "json";
				break;
			case BINARY:
			case MIME_MULTIPART:
				// per ora restituisco il default
			case SOAP_11:
			case SOAP_12:
			case XML:
			default:
				toRet = "xml";
				break;
			}
		}

		return toRet;
	}

	public String getErroreVisualizzaFaultIntegrazione(){
		String f = super.getFaultIntegrazione();
		if(f!=null) {
			StringBuffer contenutoDocumentoStringBuffer = new StringBuffer();
			String errore = Utils.getTestoVisualizzabile(f.getBytes(),contenutoDocumentoStringBuffer,false);
			return errore;
		}

		return null;
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
					StringBuffer bf = new StringBuffer();
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
	
	public String getGruppiHTML(){
		String tmp = this.getGruppi();
		if(tmp!=null){
			tmp = tmp.trim();
			if(tmp.contains(",")){
				String [] split = tmp.split(",");
				if(split!=null && split.length>0){
					StringBuffer bf = new StringBuffer();
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
	

}
