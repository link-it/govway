package org.openspcoop2.web.monitor.transazioni.bean;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.openspcoop2.core.transazioni.TransazioneApplicativoServer;
import org.openspcoop2.message.constants.MessageType;
import org.openspcoop2.monitor.engine.condition.EsitoUtils;
import org.openspcoop2.protocol.sdk.constants.EsitoTransazioneName;
import org.openspcoop2.protocol.utils.EsitiProperties;
import org.openspcoop2.utils.UtilsException;
import org.openspcoop2.utils.beans.BlackListElement;
import org.openspcoop2.utils.json.JSONUtils;
import org.openspcoop2.web.monitor.core.core.Utils;
import org.openspcoop2.web.monitor.core.logger.LoggerManager;
import org.openspcoop2.web.monitor.core.utils.BeanUtils;
import org.openspcoop2.web.monitor.core.utils.MessageManager;
import org.openspcoop2.web.monitor.transazioni.constants.TransazioniCostanti;

public class TransazioneApplicativoServerBean extends TransazioneApplicativoServer {

	/**
	 * 
	 	public String getIconaColonnaSX() {
		if(this.isInCodaConsegnaApplicativoZeroTentativi()) {

		}

		if(this.isInCodaOttenibileImZeroPrelievi()) {

		}

		if(this.isInCodaConsegnaApplicativoImZeroTentativi()) {

		}

		if(this.isInCodaConsegnaApplicativoPiuTentativi()) {

		}

		if(this.isInCodaOttenibileImPiuPrelievi()) {

		}

		if(this.isInCodaConsegnaApplicativoImPiuPrelievi()) {

		}

		if(this.isTerminataConsegnaApplicativo()) {

		}

		if(this.isTerminataGestioneIm()) {

		}

		if(this.isTerminataConsegnaApplicativoIm()) {

		}

		if(this.isMessaggioScaduto()) {

		}

		return "";
	}

	 */
	private static final long serialVersionUID = 1L;
	
	private int lunghezzaErrore = 200; // TODO da properties

	public TransazioneApplicativoServerBean() {
		super();
	}

	public TransazioneApplicativoServerBean(TransazioneApplicativoServer transazioneApplicativoServer) {

		List<BlackListElement> metodiEsclusi = new ArrayList<BlackListElement>(0);

		BeanUtils.copy(this, transazioneApplicativoServer, metodiEsclusi);
	}

	public Long getLatenzaConsegna() {
		if(this.isInCodaConsegnaApplicativoZeroTentativi() || this.isInCodaOttenibileImZeroPrelievi() || this.isInCodaConsegnaApplicativoImZeroTentativi()) {
			return null;
		}

		if(this.isInCodaConsegnaApplicativoPiuTentativi() || this.isTerminataConsegnaApplicativo()) {
			if(this.dataUscitaRichiesta != null && this.dataIngressoRisposta != null){
				return this.dataIngressoRisposta.getTime() - this.dataUscitaRichiesta.getTime();
			} else return -1L;
			
		}

		if(this.isInCodaOttenibileImPiuPrelievi() || this.isTerminataGestioneIm()) {
			return null;
		}

		if(this.isInCodaConsegnaApplicativoImPiuPrelievi()) {
			if(this.dataUscitaRichiesta != null && this.dataIngressoRisposta != null){
				return this.dataIngressoRisposta.getTime() - this.dataUscitaRichiesta.getTime();
			} else return null;
		}

		if(this.isTerminataConsegnaApplicativoIm()) {
			if(this.dataEliminazioneIm == null)
				if(this.dataUscitaRichiesta != null && this.dataIngressoRisposta != null){
					return this.dataIngressoRisposta.getTime() - this.dataUscitaRichiesta.getTime();
				} else return -1L;
			else 
				return null;
		}

		if(this.isMessaggioScaduto()) {
			if(this.isInCodaConsegnaApplicativoZeroTentativi() || this.isInCodaOttenibileImZeroPrelievi() || this.isInCodaConsegnaApplicativoImZeroTentativi()) {
				return null;
			}
			
			if(this.isInCodaOttenibileImPiuPrelievi()) {
				return null;
			}

			if(this.isInCodaConsegnaApplicativoImPiuPrelievi()) {
				if(this.dataUscitaRichiesta != null && this.dataIngressoRisposta != null){
					return this.dataIngressoRisposta.getTime() - this.dataUscitaRichiesta.getTime();
				} else return null;
			}
		}

		return null;
	}

	public boolean isConsegnaApplicativo() {
		return this.consegnaTrasparente && !this.consegnaIntegrationManager;
	}
	
	public boolean isPrelievoIM() {
		return !this.consegnaTrasparente && this.consegnaIntegrationManager;
	}
	
	public boolean isConsegnaApplicativoIM() {
		return this.consegnaTrasparente && this.consegnaIntegrationManager;
	}
	
	
	public boolean isInCodaConsegnaApplicativoZeroTentativi(){
		return this.dataMessaggioScaduto == null && !this.consegnaTerminata && this.isConsegnaApplicativo() && this.numeroTentativi == 0;  
	}

	public boolean isInCodaOttenibileImZeroPrelievi(){
		return this.dataMessaggioScaduto == null && !this.consegnaTerminata && this.isPrelievoIM() && this.numeroPrelieviIm == 0;  
	}

	public boolean isInCodaConsegnaApplicativoImZeroTentativi(){
		return this.dataMessaggioScaduto == null && !this.consegnaTerminata && this.isConsegnaApplicativoIM() && this.numeroTentativi == 0 && this.numeroPrelieviIm == 0;  
	}

	public boolean isInCodaConsegnaApplicativoPiuTentativi(){
		return this.dataMessaggioScaduto == null && !this.consegnaTerminata && this.isConsegnaApplicativo() && this.numeroTentativi > 0;  
	}

	public boolean isInCodaOttenibileImPiuPrelievi(){
		return this.dataMessaggioScaduto == null && !this.consegnaTerminata && this.isPrelievoIM() && this.numeroPrelieviIm > 0;  
	}

	public boolean isInCodaConsegnaApplicativoImPiuPrelievi(){
		return this.dataMessaggioScaduto == null && !this.consegnaTerminata && this.isConsegnaApplicativoIM() && (this.numeroTentativi > 0 || this.numeroPrelieviIm > 0);  
	}

	public boolean isTerminataConsegnaApplicativo(){
		return this.dataMessaggioScaduto == null && this.consegnaTerminata && this.isConsegnaApplicativo();  
	}

	public boolean isTerminataGestioneIm(){
		return this.dataMessaggioScaduto == null && this.consegnaTerminata && this.isPrelievoIM();  
	}

	public boolean isTerminataConsegnaApplicativoIm(){
		return this.dataMessaggioScaduto == null && this.consegnaTerminata && this.isConsegnaApplicativoIM();  
	}

	public boolean isMessaggioScaduto(){
		return this.dataMessaggioScaduto != null;  
	} 

	public String getIconaColonnaSX() {
		if(this.isInCodaConsegnaApplicativoZeroTentativi()) {
			return MessageManager.getInstance().getMessage(TransazioniCostanti.TRANSAZIONI_APPLICATIVO_SERVER_ELENCO_IN_CODA_APPLICATIVO_ZERO_TENTATIVI_ICON_KEY);
		}

		if(this.isInCodaOttenibileImZeroPrelievi()) {
			return MessageManager.getInstance().getMessage(TransazioniCostanti.TRANSAZIONI_APPLICATIVO_SERVER_ELENCO_IN_CODA_APPLICATIVO_IM_ZERO_TENTATIVI_ICON_KEY);
		}

		if(this.isInCodaConsegnaApplicativoImZeroTentativi()) {
			return MessageManager.getInstance().getMessage(TransazioniCostanti.TRANSAZIONI_APPLICATIVO_SERVER_ELENCO_IN_CODA_APPLICATIVO_IM_ZERO_TENTATIVI_ICON_KEY);
		}

		if(this.isInCodaConsegnaApplicativoPiuTentativi()) {
			return MessageManager.getInstance().getMessage(TransazioniCostanti.TRANSAZIONI_APPLICATIVO_SERVER_ELENCO_IN_CODA_APPLICATIVO_PIU_TENTATIVI_ICON_KEY);
		}

		if(this.isInCodaOttenibileImPiuPrelievi()) {
			return MessageManager.getInstance().getMessage(TransazioniCostanti.TRANSAZIONI_APPLICATIVO_SERVER_ELENCO_IN_CODA_APPLICATIVO_IM_PIU_TENTATIVI_ICON_KEY);
		}

		if(this.isInCodaConsegnaApplicativoImPiuPrelievi()) {
			return MessageManager.getInstance().getMessage(TransazioniCostanti.TRANSAZIONI_APPLICATIVO_SERVER_ELENCO_IN_CODA_APPLICATIVO_IM_PIU_TENTATIVI_ICON_KEY);
		}

		if(this.isTerminataConsegnaApplicativo()) {
			return MessageManager.getInstance().getMessage(TransazioniCostanti.TRANSAZIONI_APPLICATIVO_SERVER_ELENCO_TERMINATA_CONSEGNA_APPLICATIVO_ICON_KEY);
		}

		if(this.isTerminataGestioneIm()) {
			return MessageManager.getInstance().getMessage(TransazioniCostanti.TRANSAZIONI_APPLICATIVO_SERVER_ELENCO_TERMINATA_GESTIONE_IM_ICON_KEY);
		}

		if(this.isTerminataConsegnaApplicativoIm()) {
			return MessageManager.getInstance().getMessage(TransazioniCostanti.TRANSAZIONI_APPLICATIVO_SERVER_ELENCO_TERMINATA_CONSEGNA_APPLICATIVO_IM_ICON_KEY);
		}

		if(this.isMessaggioScaduto()) {
			return MessageManager.getInstance().getMessage(TransazioniCostanti.TRANSAZIONI_APPLICATIVO_SERVER_ELENCO_MESSAGGIO_SCADUTO_ICON_KEY);
		}

		return MessageManager.getInstance().getMessage(TransazioniCostanti.TRANSAZIONI_APPLICATIVO_SERVER_ELENCO_IN_CODA_APPLICATIVO_ZERO_TENTATIVI_ICON_KEY);
	}
	
	public String getTooltipIconaColonnaSX() {
		if(this.isInCodaConsegnaApplicativoZeroTentativi()) {
			return MessageManager.getInstance().getMessage(TransazioniCostanti.TRANSAZIONI_APPLICATIVO_SERVER_ELENCO_IN_CODA_APPLICATIVO_ZERO_TENTATIVI_LABEL_KEY);
		}

		if(this.isInCodaOttenibileImZeroPrelievi()) {
			return MessageManager.getInstance().getMessage(TransazioniCostanti.TRANSAZIONI_APPLICATIVO_SERVER_ELENCO_IN_CODA_APPLICATIVO_IM_ZERO_TENTATIVI_LABEL_KEY);
		}

		if(this.isInCodaConsegnaApplicativoImZeroTentativi()) {
			return MessageManager.getInstance().getMessage(TransazioniCostanti.TRANSAZIONI_APPLICATIVO_SERVER_ELENCO_IN_CODA_APPLICATIVO_IM_ZERO_TENTATIVI_LABEL_KEY);
		}

		if(this.isInCodaConsegnaApplicativoPiuTentativi()) {
			return MessageManager.getInstance().getMessage(TransazioniCostanti.TRANSAZIONI_APPLICATIVO_SERVER_ELENCO_IN_CODA_APPLICATIVO_PIU_TENTATIVI_LABEL_KEY);
		}

		if(this.isInCodaOttenibileImPiuPrelievi()) {
			return MessageManager.getInstance().getMessage(TransazioniCostanti.TRANSAZIONI_APPLICATIVO_SERVER_ELENCO_IN_CODA_APPLICATIVO_IM_PIU_TENTATIVI_LABEL_KEY);
		}

		if(this.isInCodaConsegnaApplicativoImPiuPrelievi()) {
			return MessageManager.getInstance().getMessage(TransazioniCostanti.TRANSAZIONI_APPLICATIVO_SERVER_ELENCO_IN_CODA_APPLICATIVO_IM_PIU_TENTATIVI_LABEL_KEY);
		}

		if(this.isTerminataConsegnaApplicativo()) {
			return MessageManager.getInstance().getMessage(TransazioniCostanti.TRANSAZIONI_APPLICATIVO_SERVER_ELENCO_TERMINATA_CONSEGNA_APPLICATIVO_LABEL_KEY);
		}

		if(this.isTerminataGestioneIm()) {
			return MessageManager.getInstance().getMessage(TransazioniCostanti.TRANSAZIONI_APPLICATIVO_SERVER_ELENCO_TERMINATA_GESTIONE_IM_LABEL_KEY);
		}

		if(this.isTerminataConsegnaApplicativoIm()) {
			if(this.dataEliminazioneIm == null)
				return MessageManager.getInstance().getMessage(TransazioniCostanti.TRANSAZIONI_APPLICATIVO_SERVER_ELENCO_TERMINATA_CONSEGNA_APPLICATIVO_IM_LABEL_KEY);
			else 
				return MessageManager.getInstance().getMessage(TransazioniCostanti.TRANSAZIONI_APPLICATIVO_SERVER_ELENCO_TERMINATA_CONSEGNA_APPLICATIVO_IM_TERMINATA_IM_LABEL_KEY);
		}

		if(this.isMessaggioScaduto()) {
			return MessageManager.getInstance().getMessage(TransazioniCostanti.TRANSAZIONI_APPLICATIVO_SERVER_ELENCO_MESSAGGIO_SCADUTO_LABEL_KEY);
		}

		return MessageManager.getInstance().getMessage(TransazioniCostanti.TRANSAZIONI_APPLICATIVO_SERVER_ELENCO_IN_CODA_APPLICATIVO_ZERO_TENTATIVI_LABEL_KEY);
	}
	
	public String getTitoloSX() { // in tutti i casi previsti nel documento a SX del titolo va il nome connettore
		return this.connettoreNome;
	}
	
	public boolean isVisualizzaIconConsegnaApplicativo() { 
		return this.isConsegnaApplicativo() || this.isConsegnaApplicativoIM();
	}
	
	public boolean isVisualizzaIconPrelievoIM() { 
		return this.isPrelievoIM() || this.isConsegnaApplicativoIM();
	}
	
	public String getTitoloDX() { 
		if(this.isConsegnaApplicativo() || this.isConsegnaApplicativoIM()) {
			return this.locationConnettore;
		}
		
		if(this.isPrelievoIM()) {
			return MessageManager.getInstance().getMessage(TransazioniCostanti.TRANSAZIONI_APPLICATIVO_SERVER_ELENCO_IM_TITOLO_LABEL_KEY);
		}
		
		return "";
	}
	
	public String getLabelDataSX() {
		if(this.isInCodaConsegnaApplicativoZeroTentativi() || this.isInCodaOttenibileImZeroPrelievi() || this.isInCodaConsegnaApplicativoImZeroTentativi()) {
			return MessageManager.getInstance().getMessage(TransazioniCostanti.TRANSAZIONI_APPLICATIVO_SERVER_ELENCO_DATA_IN_CODA_LABEL_KEY);
		}

		if(this.isInCodaConsegnaApplicativoPiuTentativi() || this.isInCodaConsegnaApplicativoImPiuPrelievi() || this.isTerminataConsegnaApplicativo()) {
			return MessageManager.getInstance().getMessage(TransazioniCostanti.TRANSAZIONI_APPLICATIVO_SERVER_ELENCO_DATA_ULTIMA_CONSEGNA_LABEL_KEY);
		}

		if(this.isInCodaOttenibileImPiuPrelievi()) {
			return MessageManager.getInstance().getMessage(TransazioniCostanti.TRANSAZIONI_APPLICATIVO_SERVER_ELENCO_DATA_ULTIMO_PRELIEVO_LABEL_KEY);
		}

		if(this.isTerminataGestioneIm()) {
			return MessageManager.getInstance().getMessage(TransazioniCostanti.TRANSAZIONI_APPLICATIVO_SERVER_ELENCO_DATA_ELIMINAZIONE_LABEL_KEY);
		}

		if(this.isTerminataConsegnaApplicativoIm()) {
			if(this.dataEliminazioneIm != null)
				return MessageManager.getInstance().getMessage(TransazioniCostanti.TRANSAZIONI_APPLICATIVO_SERVER_ELENCO_DATA_ELIMINAZIONE_LABEL_KEY);
			else 
				return MessageManager.getInstance().getMessage(TransazioniCostanti.TRANSAZIONI_APPLICATIVO_SERVER_ELENCO_DATA_ULTIMA_CONSEGNA_LABEL_KEY);
		}

		if(this.isMessaggioScaduto()) {
			return MessageManager.getInstance().getMessage(TransazioniCostanti.TRANSAZIONI_APPLICATIVO_SERVER_ELENCO_DATA_SCADUTO_LABEL_KEY);
		}

		return null;
	}
	
	public Date getDataSX() {
		if(this.isInCodaConsegnaApplicativoZeroTentativi() || this.isInCodaOttenibileImZeroPrelievi() || this.isInCodaConsegnaApplicativoImZeroTentativi()) {
			return this.dataRegistrazione;
		}

		if(this.isInCodaConsegnaApplicativoPiuTentativi()) {
			return this.getDataUltimaConsegna();
		}

		if(this.isInCodaOttenibileImPiuPrelievi()) {
			return this.dataPrelievoIm;
		}

		if(this.isInCodaConsegnaApplicativoImPiuPrelievi()) {
			return this.getDataUltimaConsegna();
		}

		if(this.isTerminataConsegnaApplicativo()) {
			return this.getDataUltimaConsegna();
		}

		if(this.isTerminataGestioneIm()) {
			return this.dataEliminazioneIm;
		}

		if(this.isTerminataConsegnaApplicativoIm()) {
			if(this.dataEliminazioneIm != null)
				return this.dataEliminazioneIm;
			else 
				return this.getDataUltimaConsegna();
		}

		if(this.isMessaggioScaduto()) {
			return this.dataMessaggioScaduto;
		}

		return null;
	}
	
	public String getLabelData2SX() {
		if(this.isTerminataGestioneIm()) {
			return MessageManager.getInstance().getMessage(TransazioniCostanti.TRANSAZIONI_APPLICATIVO_SERVER_ELENCO_DATA_ULTIMO_PRELIEVO_LABEL_KEY);
		}

		if(this.isTerminataConsegnaApplicativoIm()) {
			if(this.dataEliminazioneIm != null)
				return MessageManager.getInstance().getMessage(TransazioniCostanti.TRANSAZIONI_APPLICATIVO_SERVER_ELENCO_DATA_ULTIMO_PRELIEVO_LABEL_KEY);
			else return null;
		}

		if(this.isMessaggioScaduto()) {
			if(this.isInCodaConsegnaApplicativoZeroTentativi() || this.isInCodaOttenibileImZeroPrelievi() || this.isInCodaConsegnaApplicativoImZeroTentativi()) {
				return MessageManager.getInstance().getMessage(TransazioniCostanti.TRANSAZIONI_APPLICATIVO_SERVER_ELENCO_DATA_IN_CODA_LABEL_KEY);
			}
			
			if(this.isInCodaConsegnaApplicativoPiuTentativi() || this.isInCodaConsegnaApplicativoImPiuPrelievi()) {
				return MessageManager.getInstance().getMessage(TransazioniCostanti.TRANSAZIONI_APPLICATIVO_SERVER_ELENCO_DATA_ULTIMA_CONSEGNA_LABEL_KEY);
			}

			if(this.isInCodaOttenibileImPiuPrelievi()) {
				return MessageManager.getInstance().getMessage(TransazioniCostanti.TRANSAZIONI_APPLICATIVO_SERVER_ELENCO_DATA_ULTIMO_PRELIEVO_LABEL_KEY);
			}
		}

		return null;
	}
	
	public Date getData2SX() {
		if(this.isTerminataGestioneIm()) {
			return this.dataPrelievoIm;
		}

		if(this.isTerminataConsegnaApplicativoIm()) {
			if(this.dataEliminazioneIm != null)
				return this.dataPrelievoIm;
			else 
				return this.getDataUltimaConsegna();
		}

		if(this.isMessaggioScaduto()) {
			if(this.isInCodaConsegnaApplicativoZeroTentativi() || this.isInCodaOttenibileImZeroPrelievi() || this.isInCodaConsegnaApplicativoImZeroTentativi()) {
				return this.dataRegistrazione;
			}
			
			if(this.isInCodaConsegnaApplicativoPiuTentativi() || this.isInCodaConsegnaApplicativoImPiuPrelievi()) {
				return this.getDataUltimaConsegna();
			}
			
			if(this.isInCodaOttenibileImPiuPrelievi()) {
				return this.dataPrelievoIm;
			}
		}

		return null;
	}
	
	
	public Date getDataUltimaConsegna() {
		if(this.dataUscitaRichiesta == null)
			return this.dataAccettazioneRichiesta;
		
		return this.dataUscitaRichiesta;
	}
	

	public String getEsitoStyleClass(){

		try{
			EsitiProperties esitiProperties = EsitiProperties.getInstance(LoggerManager.getPddMonitorCoreLogger(),this.getProtocollo());
			String name = esitiProperties.getEsitoName(this.dettaglioEsito);
			EsitoTransazioneName esitoName = EsitoTransazioneName.convertoTo(name);
			boolean casoSuccesso = esitiProperties.getEsitiCodeOk().contains(this.dettaglioEsito);
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
			LoggerManager.getPddMonitorCoreLogger().error("Errore durante il calcolo del layout dell'esito ["+this.dettaglioEsito+"]: "+e.getMessage(),e);
			return "icon-ko";
		}

	}

	public boolean isEsitoOk(){	
		try{
			EsitiProperties esitiProperties = EsitiProperties.getInstance(LoggerManager.getPddMonitorCoreLogger(),this.getProtocollo());
			boolean res = esitiProperties.getEsitiCodeOk_senzaFaultApplicativo().contains(this.dettaglioEsito);
			//System.out.println("isEsitoOk:"+res+" (esitoChecked:"+this.getEsito()+")");
			return res;
		}catch(Exception e){
			LoggerManager.getPddMonitorCoreLogger().error("Errore durante il calcolo del layout dell'esito ["+this.dettaglioEsito+"]: "+e.getMessage(),e);
			return false;
		}
	}
	public boolean isEsitoFaultApplicativo(){	
		try{
			EsitiProperties esitiProperties = EsitiProperties.getInstance(LoggerManager.getPddMonitorCoreLogger(),this.getProtocollo());
			boolean res = esitiProperties.getEsitiCodeFaultApplicativo().contains(this.dettaglioEsito);
			//System.out.println("isEsitoOk:"+res+" (esitoChecked:"+this.getEsito()+")");
			return res;
		}catch(Exception e){
			LoggerManager.getPddMonitorCoreLogger().error("Errore durante il calcolo del layout dell'esito ["+this.dettaglioEsito+"]: "+e.getMessage(),e);
			return false;
		}
	}
	public boolean isEsitoKo(){	
		try{
			EsitiProperties esitiProperties = EsitiProperties.getInstance(LoggerManager.getPddMonitorCoreLogger(),this.getProtocollo());
			boolean res = esitiProperties.getEsitiCodeKo_senzaFaultApplicativo().contains(this.dettaglioEsito);
			//System.out.println("isEsitoOk:"+res+" (esitoChecked:"+this.getEsito()+")");
			return res;
		}catch(Exception e){
			LoggerManager.getPddMonitorCoreLogger().error("Errore durante il calcolo del layout dell'esito ["+this.dettaglioEsito+"]: "+e.getMessage(),e);
			return false;
		}
	}
	
	
	
	
	
	

	public java.lang.String getEsitoLabel() {
		try{
			EsitoUtils esitoUtils = new EsitoUtils(LoggerManager.getPddMonitorCoreLogger(), this.protocollo);
			return esitoUtils.getEsitoLabelFromValue(this.dettaglioEsito, false);
		}catch(Exception e){
			LoggerManager.getPddMonitorCoreLogger().error("Errore durante il calcolo della label per l'esito ["+this.dettaglioEsito+"]: "+e.getMessage(),e);
			return "Conversione non riuscita";
		}
	}

	public java.lang.String getEsitoLabelSyntetic() {
		try{
			EsitiProperties esitiProperties = EsitiProperties.getInstance(LoggerManager.getPddMonitorCoreLogger(),this.protocollo);
			return esitiProperties.getEsitoLabelSyntetic(this.dettaglioEsito);
		}catch(Exception e){
			LoggerManager.getPddMonitorCoreLogger().error("Errore durante il calcolo della label per l'esito ["+this.dettaglioEsito+"]: "+e.getMessage(),e);
			return "Conversione non riuscita";
		}
	}

	public java.lang.String getEsitoLabelDescription() {
		try{
			EsitiProperties esitiProperties = EsitiProperties.getInstance(LoggerManager.getPddMonitorCoreLogger(),this.protocollo);
			return getEsitoLabel() + " - " + esitiProperties.getEsitoDescription(this.dettaglioEsito);
		}catch(Exception e){
			LoggerManager.getPddMonitorCoreLogger().error("Errore durante il calcolo della label per l'esito ["+this.dettaglioEsito+"]: "+e.getMessage(),e);
			return "Conversione non riuscita";
		}
	}
	
	public java.lang.String getEsitoSyntetic() {
		if(this.isInCodaConsegnaApplicativoZeroTentativi()) {
			return MessageManager.getInstance().getMessage(TransazioniCostanti.TRANSAZIONI_APPLICATIVO_SERVER_ELENCO_ESITO_APPLICATIVO_IN_CODA_ZERO_TENTATIVI_LABEL_KEY); 
		}
		
		if(this.isInCodaOttenibileImZeroPrelievi()) {
			return MessageManager.getInstance().getMessage(TransazioniCostanti.TRANSAZIONI_APPLICATIVO_SERVER_ELENCO_ESITO_IM_IN_CODA_ZERO_TENTATIVI_LABEL_KEY); 
		}
		
		if(this.isInCodaConsegnaApplicativoImZeroTentativi()) {
			return MessageManager.getInstance().getMessage(TransazioniCostanti.TRANSAZIONI_APPLICATIVO_SERVER_ELENCO_ESITO_APPLICATIVO_IM_IN_CODA_ZERO_TENTATIVI_LABEL_KEY); 
		}

		if(this.isInCodaConsegnaApplicativoPiuTentativi()) {
			return this.getEsitoLabelSyntetic();
		}

		if(this.isInCodaOttenibileImPiuPrelievi()) {
			return MessageManager.getInstance().getMessage(TransazioniCostanti.TRANSAZIONI_APPLICATIVO_SERVER_ELENCO_ESITO_IM_PIU_TENTATIVI_LABEL_KEY); 
		}

		if(this.isInCodaConsegnaApplicativoImPiuPrelievi()) {
			if(this.dataUscitaRichiesta != null && this.dataIngressoRisposta != null){
				return this.getEsitoLabelSyntetic();
			} else return MessageManager.getInstance().getMessage(TransazioniCostanti.TRANSAZIONI_APPLICATIVO_SERVER_ELENCO_ESITO_APPLICATIVO_IM_TERMINATA_CONSEGNA_LABEL_KEY); 
		}

		if(this.isTerminataConsegnaApplicativo()) {
			return this.getEsitoLabelSyntetic();
		}

		if(this.isTerminataGestioneIm()) {
			return MessageManager.getInstance().getMessage(TransazioniCostanti.TRANSAZIONI_APPLICATIVO_SERVER_ELENCO_ESITO_IM_TERMINATA_GESTIONE_LABEL_KEY); 
		}

		if(this.isTerminataConsegnaApplicativoIm()) {
			if(this.dataEliminazioneIm != null)
				return MessageManager.getInstance().getMessage(TransazioniCostanti.TRANSAZIONI_APPLICATIVO_SERVER_ELENCO_ESITO_APPLICATIVO_IM_TERMINATA_CONSEGNA_LABEL_KEY); 
			else 
				if(this.dataUscitaRichiesta != null && this.dataIngressoRisposta != null){
					return this.getEsitoLabelSyntetic();
				} else return MessageManager.getInstance().getMessage(TransazioniCostanti.TRANSAZIONI_APPLICATIVO_SERVER_ELENCO_ESITO_APPLICATIVO_IN_CODA_ZERO_TENTATIVI_LABEL_KEY); 
		}

		if(this.isMessaggioScaduto()) {
			if(this.isInCodaConsegnaApplicativoZeroTentativi()) {
				return MessageManager.getInstance().getMessage(TransazioniCostanti.TRANSAZIONI_APPLICATIVO_SERVER_ELENCO_ESITO_APPLICATIVO_IN_CODA_ZERO_TENTATIVI_LABEL_KEY); 
			}
			
			if(this.isInCodaOttenibileImZeroPrelievi()) {
				return MessageManager.getInstance().getMessage(TransazioniCostanti.TRANSAZIONI_APPLICATIVO_SERVER_ELENCO_ESITO_IM_IN_CODA_ZERO_TENTATIVI_LABEL_KEY); 
			}
			
			if(this.isInCodaConsegnaApplicativoImZeroTentativi()) {
				return MessageManager.getInstance().getMessage(TransazioniCostanti.TRANSAZIONI_APPLICATIVO_SERVER_ELENCO_ESITO_APPLICATIVO_IM_IN_CODA_ZERO_TENTATIVI_LABEL_KEY); 
			}

			if(this.isInCodaConsegnaApplicativoPiuTentativi()) {
				return this.getEsitoLabelSyntetic();
			}

			if(this.isInCodaOttenibileImPiuPrelievi()) {
				return MessageManager.getInstance().getMessage(TransazioniCostanti.TRANSAZIONI_APPLICATIVO_SERVER_ELENCO_ESITO_IM_PIU_TENTATIVI_LABEL_KEY); 
			}

			if(this.isInCodaConsegnaApplicativoImPiuPrelievi()) {
				if(this.dataUscitaRichiesta != null && this.dataIngressoRisposta != null){
					return this.getEsitoLabelSyntetic();
				} else return MessageManager.getInstance().getMessage(TransazioniCostanti.TRANSAZIONI_APPLICATIVO_SERVER_ELENCO_ESITO_APPLICATIVO_IN_CODA_ZERO_TENTATIVI_LABEL_KEY); 
			}
		}
		
		return MessageManager.getInstance().getMessage(TransazioniCostanti.TRANSAZIONI_APPLICATIVO_SERVER_ELENCO_ESITO_APPLICATIVO_IN_CODA_ZERO_TENTATIVI_LABEL_KEY);
	}
	
	public java.lang.String getEsitoDescription() {
		if(this.isInCodaConsegnaApplicativoZeroTentativi()) {
			return MessageManager.getInstance().getMessage(TransazioniCostanti.TRANSAZIONI_APPLICATIVO_SERVER_ELENCO_ESITO_APPLICATIVO_IN_CODA_ZERO_TENTATIVI_TOOLTIP_KEY); 
		}
		
		if(this.isInCodaOttenibileImZeroPrelievi()) {
			return MessageManager.getInstance().getMessage(TransazioniCostanti.TRANSAZIONI_APPLICATIVO_SERVER_ELENCO_ESITO_IM_IN_CODA_ZERO_TENTATIVI_TOOLTIP_KEY); 
		}
		
		if(this.isInCodaConsegnaApplicativoImZeroTentativi()) {
			return MessageManager.getInstance().getMessage(TransazioniCostanti.TRANSAZIONI_APPLICATIVO_SERVER_ELENCO_ESITO_APPLICATIVO_IM_IN_CODA_ZERO_TENTATIVI_TOOLTIP_KEY); 
		}

		if(this.isInCodaConsegnaApplicativoPiuTentativi()) {
			return this.getEsitoLabelDescription();
		}

		if(this.isInCodaOttenibileImPiuPrelievi()) {
			return MessageManager.getInstance().getMessage(TransazioniCostanti.TRANSAZIONI_APPLICATIVO_SERVER_ELENCO_ESITO_IM_PIU_TENTATIVI_TOOLTIP_KEY); 
		}

		if(this.isInCodaConsegnaApplicativoImPiuPrelievi()) {
			if(this.dataUscitaRichiesta != null && this.dataIngressoRisposta != null){
				return this.getEsitoLabelDescription();
			} else return MessageManager.getInstance().getMessage(TransazioniCostanti.TRANSAZIONI_APPLICATIVO_SERVER_ELENCO_ESITO_APPLICATIVO_IM_TERMINATA_CONSEGNA_TOOLTIP_KEY); 
		}

		if(this.isTerminataConsegnaApplicativo()) {
			return this.getEsitoLabelDescription();
		}

		if(this.isTerminataGestioneIm()) {
			return MessageManager.getInstance().getMessage(TransazioniCostanti.TRANSAZIONI_APPLICATIVO_SERVER_ELENCO_ESITO_IM_TERMINATA_GESTIONE_TOOLTIP_KEY); 
		}

		if(this.isTerminataConsegnaApplicativoIm()) {
			if(this.dataEliminazioneIm != null)
				return MessageManager.getInstance().getMessage(TransazioniCostanti.TRANSAZIONI_APPLICATIVO_SERVER_ELENCO_ESITO_APPLICATIVO_IM_TERMINATA_CONSEGNA_TOOLTIP_KEY); 
			else 
				if(this.dataUscitaRichiesta != null && this.dataIngressoRisposta != null){
					return this.getEsitoLabelDescription();
				} else return MessageManager.getInstance().getMessage(TransazioniCostanti.TRANSAZIONI_APPLICATIVO_SERVER_ELENCO_ESITO_APPLICATIVO_IN_CODA_ZERO_TENTATIVI_TOOLTIP_KEY); 
		}

		if(this.isMessaggioScaduto()) {
			if(this.isInCodaConsegnaApplicativoZeroTentativi()) {
				return MessageManager.getInstance().getMessage(TransazioniCostanti.TRANSAZIONI_APPLICATIVO_SERVER_ELENCO_ESITO_APPLICATIVO_IN_CODA_ZERO_TENTATIVI_TOOLTIP_KEY); 
			}
			
			if(this.isInCodaOttenibileImZeroPrelievi()) {
				return MessageManager.getInstance().getMessage(TransazioniCostanti.TRANSAZIONI_APPLICATIVO_SERVER_ELENCO_ESITO_IM_IN_CODA_ZERO_TENTATIVI_TOOLTIP_KEY); 
			}
			
			if(this.isInCodaConsegnaApplicativoImZeroTentativi()) {
				return MessageManager.getInstance().getMessage(TransazioniCostanti.TRANSAZIONI_APPLICATIVO_SERVER_ELENCO_ESITO_APPLICATIVO_IM_IN_CODA_ZERO_TENTATIVI_TOOLTIP_KEY); 
			}

			if(this.isInCodaConsegnaApplicativoPiuTentativi()) {
				return this.getEsitoLabelDescription();
			}

			if(this.isInCodaOttenibileImPiuPrelievi()) {
				return MessageManager.getInstance().getMessage(TransazioniCostanti.TRANSAZIONI_APPLICATIVO_SERVER_ELENCO_ESITO_IM_PIU_TENTATIVI_TOOLTIP_KEY); 
			}

			if(this.isInCodaConsegnaApplicativoImPiuPrelievi()) {
				if(this.dataUscitaRichiesta != null && this.dataIngressoRisposta != null){
					return this.getEsitoLabelDescription();
				} else return MessageManager.getInstance().getMessage(TransazioniCostanti.TRANSAZIONI_APPLICATIVO_SERVER_ELENCO_ESITO_APPLICATIVO_IN_CODA_ZERO_TENTATIVI_TOOLTIP_KEY); 
			}
		}
		
		return MessageManager.getInstance().getMessage(TransazioniCostanti.TRANSAZIONI_APPLICATIVO_SERVER_ELENCO_ESITO_APPLICATIVO_IN_CODA_ZERO_TENTATIVI_TOOLTIP_KEY);
	}

	public String getIconaColonnaEsito() {
		if(this.isInCodaConsegnaApplicativoZeroTentativi() || this.isInCodaOttenibileImZeroPrelievi() || this.isInCodaConsegnaApplicativoImZeroTentativi()) {
			return MessageManager.getInstance().getMessage(TransazioniCostanti.TRANSAZIONI_APPLICATIVO_SERVER_ELENCO_ESITO_IN_CORSO_ICON_KEY); 
		}

		if(this.isInCodaConsegnaApplicativoPiuTentativi()) {
			return this.getIconaFromEsitoConsegna();
		}

		if(this.isInCodaOttenibileImPiuPrelievi()) {
			return MessageManager.getInstance().getMessage(TransazioniCostanti.TRANSAZIONI_APPLICATIVO_SERVER_ELENCO_ESITO_IN_CORSO_ICON_KEY); 
		}

		if(this.isInCodaConsegnaApplicativoImPiuPrelievi()) {
			if(this.dataUscitaRichiesta != null && this.dataIngressoRisposta != null){
				return this.getIconaFromEsitoConsegna();
			} else return MessageManager.getInstance().getMessage(TransazioniCostanti.TRANSAZIONI_APPLICATIVO_SERVER_ELENCO_ESITO_IN_CORSO_ICON_KEY); 
		}

		if(this.isTerminataConsegnaApplicativo()) {
			return this.getIconaFromEsitoConsegna();
		}

		if(this.isTerminataGestioneIm()) {
			return MessageManager.getInstance().getMessage(TransazioniCostanti.TRANSAZIONI_APPLICATIVO_SERVER_ELENCO_ESITO_IM_DONE_ICON_KEY); 
		}

		if(this.isTerminataConsegnaApplicativoIm()) {
			if(this.dataEliminazioneIm != null)
				return MessageManager.getInstance().getMessage(TransazioniCostanti.TRANSAZIONI_APPLICATIVO_SERVER_ELENCO_ESITO_IM_DONE_ICON_KEY); 
			else 
				if(this.dataUscitaRichiesta != null && this.dataIngressoRisposta != null){
					return this.getIconaFromEsitoConsegna();
				} else return MessageManager.getInstance().getMessage(TransazioniCostanti.TRANSAZIONI_APPLICATIVO_SERVER_ELENCO_ESITO_IN_CORSO_ICON_KEY); 
		}

		if(this.isMessaggioScaduto()) {
			if(this.isInCodaConsegnaApplicativoZeroTentativi() || this.isInCodaOttenibileImZeroPrelievi() || this.isInCodaConsegnaApplicativoImZeroTentativi()) {
				return MessageManager.getInstance().getMessage(TransazioniCostanti.TRANSAZIONI_APPLICATIVO_SERVER_ELENCO_ESITO_IN_CORSO_ICON_KEY); 
			}

			if(this.isInCodaConsegnaApplicativoPiuTentativi()) {
				return this.getIconaFromEsitoConsegna();
			}

			if(this.isInCodaOttenibileImPiuPrelievi()) {
				return MessageManager.getInstance().getMessage(TransazioniCostanti.TRANSAZIONI_APPLICATIVO_SERVER_ELENCO_ESITO_IN_CORSO_ICON_KEY); 
			}

			if(this.isInCodaConsegnaApplicativoImPiuPrelievi()) {
				if(this.dataUscitaRichiesta != null && this.dataIngressoRisposta != null){
					return this.getIconaFromEsitoConsegna();
				} else return MessageManager.getInstance().getMessage(TransazioniCostanti.TRANSAZIONI_APPLICATIVO_SERVER_ELENCO_ESITO_IN_CORSO_ICON_KEY); 
			}
		}
		
		return MessageManager.getInstance().getMessage(TransazioniCostanti.TRANSAZIONI_APPLICATIVO_SERVER_ELENCO_ESITO_IN_CORSO_ICON_KEY);
	}
	
	public String getBackgroundColonnaEsito() {
		if(this.isInCodaConsegnaApplicativoZeroTentativi() || this.isInCodaOttenibileImZeroPrelievi() || this.isInCodaConsegnaApplicativoImZeroTentativi()) {
			return TransazioniCostanti.TRANSAZIONI_ELENCO_CUSTOM_CLASSE_CSS_COL_ESITO_OK; 
		}

		if(this.isInCodaConsegnaApplicativoPiuTentativi()) {
			return this.getCssColonnaEsito();
		}

		if(this.isInCodaOttenibileImPiuPrelievi()) {
			return TransazioniCostanti.TRANSAZIONI_ELENCO_CUSTOM_CLASSE_CSS_COL_ESITO_OK; 
		}

		if(this.isInCodaConsegnaApplicativoImPiuPrelievi()) {
			if(this.dataUscitaRichiesta != null && this.dataIngressoRisposta != null){
				return this.getCssColonnaEsito();
			} else return TransazioniCostanti.TRANSAZIONI_ELENCO_CUSTOM_CLASSE_CSS_COL_ESITO_OK; 
		}

		if(this.isTerminataConsegnaApplicativo()) {
			return this.getCssColonnaEsito();
		}

		if(this.isTerminataGestioneIm()) {
			return TransazioniCostanti.TRANSAZIONI_ELENCO_CUSTOM_CLASSE_CSS_COL_ESITO_OK; 
		}

		if(this.isTerminataConsegnaApplicativoIm()) {
			if(this.dataEliminazioneIm != null)
				return TransazioniCostanti.TRANSAZIONI_ELENCO_CUSTOM_CLASSE_CSS_COL_ESITO_OK; 
			else 
				if(this.dataUscitaRichiesta != null && this.dataIngressoRisposta != null){
					return this.getCssColonnaEsito();
				} else return TransazioniCostanti.TRANSAZIONI_ELENCO_CUSTOM_CLASSE_CSS_COL_ESITO_OK; 
		}

		if(this.isMessaggioScaduto()) {
			if(this.isInCodaConsegnaApplicativoZeroTentativi() || this.isInCodaOttenibileImZeroPrelievi() || this.isInCodaConsegnaApplicativoImZeroTentativi()) {
				return TransazioniCostanti.TRANSAZIONI_ELENCO_CUSTOM_CLASSE_CSS_COL_ESITO_OK; 
			}

			if(this.isInCodaConsegnaApplicativoPiuTentativi()) {
				return this.getCssColonnaEsito();
			}

			if(this.isInCodaOttenibileImPiuPrelievi()) {
				return TransazioniCostanti.TRANSAZIONI_ELENCO_CUSTOM_CLASSE_CSS_COL_ESITO_OK; 
			}

			if(this.isInCodaConsegnaApplicativoImPiuPrelievi()) {
				if(this.dataUscitaRichiesta != null && this.dataIngressoRisposta != null){
					return this.getCssColonnaEsito();
				} else return TransazioniCostanti.TRANSAZIONI_ELENCO_CUSTOM_CLASSE_CSS_COL_ESITO_OK; 
			}
		}
		
		return TransazioniCostanti.TRANSAZIONI_ELENCO_CUSTOM_CLASSE_CSS_COL_ESITO_OK;
	}

	public String getIconaFromEsitoConsegna() {
		if(this.isEsitoOk())
			return MessageManager.getInstance().getMessage(TransazioniCostanti.TRANSAZIONI_APPLICATIVO_SERVER_ELENCO_ESITO_OK_ICON_KEY);
		if(this.isEsitoFaultApplicativo())
			return MessageManager.getInstance().getMessage(TransazioniCostanti.TRANSAZIONI_APPLICATIVO_SERVER_ELENCO_ESITO_ERROR_ICON_KEY);
		if(this.isEsitoKo())
			return MessageManager.getInstance().getMessage(TransazioniCostanti.TRANSAZIONI_APPLICATIVO_SERVER_ELENCO_ESITO_WARNING_ICON_KEY);

		return MessageManager.getInstance().getMessage(TransazioniCostanti.TRANSAZIONI_APPLICATIVO_SERVER_ELENCO_ESITO_WARNING_ICON_KEY);
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
		return TransazioniCostanti.TRANSAZIONI_ELENCO_CUSTOM_CLASSE_CSS_COL_LATENZA_OK;
	}

	public String getClusterId() {
		if(this.isInCodaConsegnaApplicativoZeroTentativi() || this.isInCodaOttenibileImZeroPrelievi() || this.isInCodaConsegnaApplicativoImZeroTentativi()) {
			return this.clusterIdPresaInCarico;
		}

		if(this.isInCodaConsegnaApplicativoPiuTentativi()) {
			return this.clusterIdConsegna;
		}

		if(this.isInCodaOttenibileImPiuPrelievi()) {
			return this.clusterIdPrelievoIm;
		}

		if(this.isInCodaConsegnaApplicativoImPiuPrelievi()) {
			return this.clusterIdConsegna;
		}

		if(this.isTerminataConsegnaApplicativo()) {
			return this.clusterIdConsegna;
		}

		if(this.isTerminataGestioneIm()) {
			return this.clusterIdEliminazioneIm;
		}

		if(this.isTerminataConsegnaApplicativoIm()) {
			if(this.dataEliminazioneIm != null)
				return this.clusterIdEliminazioneIm;
			else 
				return this.clusterIdConsegna;
		}

		if(this.isMessaggioScaduto()) {
			if(this.isInCodaConsegnaApplicativoZeroTentativi() || this.isInCodaOttenibileImZeroPrelievi() || this.isInCodaConsegnaApplicativoImZeroTentativi()) {
				return this.clusterIdPresaInCarico;
			}

			if(this.isInCodaConsegnaApplicativoPiuTentativi()) {
				return this.clusterIdConsegna;
			}

			if(this.isInCodaOttenibileImPiuPrelievi()) {
				return this.clusterIdPrelievoIm;
			}

			if(this.isInCodaConsegnaApplicativoImPiuPrelievi()) {
				return this.clusterIdConsegna;
			}
		}

		return null;
	}
	
	public String getNumeroTentativiLabel() {
		if(this.isInCodaConsegnaApplicativoZeroTentativi() || this.isInCodaOttenibileImZeroPrelievi() || this.isInCodaConsegnaApplicativoImZeroTentativi()) {
			return null;
		}

		if(this.isInCodaConsegnaApplicativoPiuTentativi() || this.isInCodaConsegnaApplicativoImPiuPrelievi() || this.isTerminataConsegnaApplicativo()) {
			return MessageManager.getInstance().getMessage(TransazioniCostanti.TRANSAZIONI_APPLICATIVO_SERVER_ELENCO_NUMERO_TENTATIVI_LABEL_KEY);
		}

		if(this.isInCodaOttenibileImPiuPrelievi() || this.isTerminataGestioneIm()) {
			return MessageManager.getInstance().getMessage(TransazioniCostanti.TRANSAZIONI_APPLICATIVO_SERVER_ELENCO_NUMERO_PRELIEVI_LABEL_KEY);
		}

		if(this.isTerminataConsegnaApplicativoIm()) {
			if(this.dataEliminazioneIm != null)
				return MessageManager.getInstance().getMessage(TransazioniCostanti.TRANSAZIONI_APPLICATIVO_SERVER_ELENCO_NUMERO_PRELIEVI_LABEL_KEY);
			else 
				return MessageManager.getInstance().getMessage(TransazioniCostanti.TRANSAZIONI_APPLICATIVO_SERVER_ELENCO_NUMERO_TENTATIVI_LABEL_KEY);
		}

		if(this.isMessaggioScaduto()) {
			if(this.isInCodaConsegnaApplicativoZeroTentativi() || this.isInCodaOttenibileImZeroPrelievi() || this.isInCodaConsegnaApplicativoImZeroTentativi()) {
				return null;
			}
			
			if(this.isInCodaConsegnaApplicativoPiuTentativi() || this.isInCodaConsegnaApplicativoImPiuPrelievi()) {
				return MessageManager.getInstance().getMessage(TransazioniCostanti.TRANSAZIONI_APPLICATIVO_SERVER_ELENCO_NUMERO_TENTATIVI_LABEL_KEY);
			}

			if(this.isInCodaOttenibileImPiuPrelievi()) {
				return MessageManager.getInstance().getMessage(TransazioniCostanti.TRANSAZIONI_APPLICATIVO_SERVER_ELENCO_NUMERO_PRELIEVI_LABEL_KEY);
			}
		}

		return null;
	}
	
	public Integer getNumeroTentativiValue() {
		if(this.isInCodaConsegnaApplicativoZeroTentativi() || this.isInCodaOttenibileImZeroPrelievi() || this.isInCodaConsegnaApplicativoImZeroTentativi()) {
			return null;
		}

		if(this.isInCodaConsegnaApplicativoPiuTentativi() || this.isInCodaConsegnaApplicativoImPiuPrelievi() || this.isTerminataConsegnaApplicativo()) {
			return this.numeroTentativi;
		}

		if(this.isInCodaOttenibileImPiuPrelievi() || this.isTerminataGestioneIm()) {
			return this.numeroPrelieviIm;
		}

		if(this.isTerminataConsegnaApplicativoIm()) {
			if(this.dataEliminazioneIm != null)
				return this.numeroPrelieviIm;
			else 
				return this.numeroTentativi;
		}

		if(this.isMessaggioScaduto()) {
			if(this.isInCodaConsegnaApplicativoZeroTentativi() || this.isInCodaOttenibileImZeroPrelievi() || this.isInCodaConsegnaApplicativoImZeroTentativi()) {
				return null;
			}
			
			if(this.isInCodaConsegnaApplicativoPiuTentativi() || this.isInCodaConsegnaApplicativoImPiuPrelievi()) {
				return this.numeroTentativi;
			}

			if(this.isInCodaOttenibileImPiuPrelievi()) {
				return this.numeroPrelieviIm;
			}
		}

		return null;
	}
	
	public String getErrore() {
		String errore = null;
		
		if(this.isInCodaConsegnaApplicativoZeroTentativi() || this.isInCodaOttenibileImZeroPrelievi() || this.isInCodaConsegnaApplicativoImZeroTentativi()) {
			return null;
		}

		if(this.isInCodaConsegnaApplicativoPiuTentativi() || this.isInCodaConsegnaApplicativoImPiuPrelievi() || this.isTerminataConsegnaApplicativo()) {
			errore = this.ultimoErrore;
		}

		if(this.isInCodaOttenibileImPiuPrelievi() || this.isTerminataGestioneIm()) {
			return null;
		}

		if(this.isTerminataConsegnaApplicativoIm()) {
			if(this.dataEliminazioneIm != null)
				return null;
			else 
				errore = this.ultimoErrore;
		}

		if(this.isMessaggioScaduto()) {
			if(this.isInCodaConsegnaApplicativoZeroTentativi() || this.isInCodaOttenibileImZeroPrelievi() || this.isInCodaConsegnaApplicativoImZeroTentativi()) {
				return null;
			}
			
			if(this.isInCodaConsegnaApplicativoPiuTentativi() || this.isInCodaConsegnaApplicativoImPiuPrelievi()) {
				errore = this.ultimoErrore;
			}

			if(this.isInCodaOttenibileImPiuPrelievi()) {
				return null;
			}
		}
		
		if(errore != null) {
			if(errore.length() > this.lunghezzaErrore)
				return errore.substring(0, this.lunghezzaErrore -4) + "...";
		}

		return null;
	}
	
	public String getErroreTooltip() {
		if(this.isInCodaConsegnaApplicativoZeroTentativi() || this.isInCodaOttenibileImZeroPrelievi() || this.isInCodaConsegnaApplicativoImZeroTentativi()) {
			return null;
		}

		if(this.isInCodaConsegnaApplicativoPiuTentativi() || this.isInCodaConsegnaApplicativoImPiuPrelievi() || this.isTerminataConsegnaApplicativo()) {
			return this.ultimoErrore;
		}

		if(this.isInCodaOttenibileImPiuPrelievi() || this.isTerminataGestioneIm()) {
			return null;
		}

		if(this.isTerminataConsegnaApplicativoIm()) {
			if(this.dataEliminazioneIm != null)
				return null;
			else 
				return this.ultimoErrore;
		}

		if(this.isMessaggioScaduto()) {
			if(this.isInCodaConsegnaApplicativoZeroTentativi() || this.isInCodaOttenibileImZeroPrelievi() || this.isInCodaConsegnaApplicativoImZeroTentativi()) {
				return null;
			}
			
			if(this.isInCodaConsegnaApplicativoPiuTentativi() || this.isInCodaConsegnaApplicativoImPiuPrelievi()) {
				return this.ultimoErrore;
			}

			if(this.isInCodaOttenibileImPiuPrelievi()) {
				return null;
			}
		}

		return null;
	}

	public String getNomeServizioApplicativoErogatore() {
		return this.servizioApplicativoErogatore;
	}

	public String getFaultPretty(){
		String f = super.getFault();
		String toRet = null;
		if(f !=null) {
			StringBuffer contenutoDocumentoStringBuffer = new StringBuffer();
			String errore = Utils.getTestoVisualizzabile(f.getBytes(),contenutoDocumentoStringBuffer);
			if(errore!= null)
				return "";

			MessageType messageType= MessageType.XML;
			if(StringUtils.isNotEmpty(super.getFormatoFault())) {
				messageType = MessageType.valueOf(super.getFormatoFault());
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

	public boolean isVisualizzaFault(){
		boolean visualizzaMessaggio = true;
		String f = super.getFault();

		if(f == null)
			return false;

		StringBuffer contenutoDocumentoStringBuffer = new StringBuffer();
		String errore = Utils.getTestoVisualizzabile(f.getBytes(),contenutoDocumentoStringBuffer);
		if(errore!= null)
			return false;

		return visualizzaMessaggio;
	}

	public String getBrushFault() {
		String toRet = null;
		String f = super.getFault();
		if(f!=null) {
			MessageType messageType= MessageType.XML;
			if(StringUtils.isNotEmpty(super.getFormatoFault())) {
				messageType = MessageType.valueOf(super.getFormatoFault());
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

	public String getErroreVisualizzaFault(){
		String f = super.getFault();
		if(f!=null) {
			StringBuffer contenutoDocumentoStringBuffer = new StringBuffer();
			String errore = Utils.getTestoVisualizzabile(f.getBytes(),contenutoDocumentoStringBuffer);
			return errore;
		}

		return null;
	}
	
	
	
	public String getFaultPrettyUltimoErrore(){
		String f = super.getFaultUltimoErrore();
		String toRet = null;
		if(f !=null) {
			StringBuffer contenutoDocumentoStringBuffer = new StringBuffer();
			String errore = Utils.getTestoVisualizzabile(f.getBytes(),contenutoDocumentoStringBuffer);
			if(errore!= null)
				return "";

			MessageType messageType= MessageType.XML;
			if(StringUtils.isNotEmpty(super.getFormatoFaultUltimoErrore())) {
				messageType = MessageType.valueOf(super.getFormatoFaultUltimoErrore());
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

	public boolean isVisualizzaFaultUltimoErrore(){
		boolean visualizzaMessaggio = true;
		String f = super.getFaultUltimoErrore();

		if(f == null)
			return false;

		StringBuffer contenutoDocumentoStringBuffer = new StringBuffer();
		String errore = Utils.getTestoVisualizzabile(f.getBytes(),contenutoDocumentoStringBuffer);
		if(errore!= null)
			return false;

		return visualizzaMessaggio;
	}

	public String getBrushFaultUltimoErrore() {
		String toRet = null;
		String f = super.getFaultUltimoErrore();
		if(f!=null) {
			MessageType messageType= MessageType.XML;
			if(StringUtils.isNotEmpty(super.getFormatoFaultUltimoErrore())) {
				messageType = MessageType.valueOf(super.getFormatoFaultUltimoErrore());
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

	public String getErroreVisualizzaFaultUltimoErrore(){
		String f = super.getFaultUltimoErrore();
		if(f!=null) {
			StringBuffer contenutoDocumentoStringBuffer = new StringBuffer();
			String errore = Utils.getTestoVisualizzabile(f.getBytes(),contenutoDocumentoStringBuffer);
			return errore;
		}

		return null;
	}
}
