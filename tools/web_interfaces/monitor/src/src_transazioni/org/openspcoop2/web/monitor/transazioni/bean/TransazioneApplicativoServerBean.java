package org.openspcoop2.web.monitor.transazioni.bean;

import java.util.ArrayList;
import java.util.List;

import org.openspcoop2.core.transazioni.TransazioneApplicativoServer;
import org.openspcoop2.monitor.engine.condition.EsitoUtils;
import org.openspcoop2.protocol.sdk.constants.EsitoTransazioneName;
import org.openspcoop2.protocol.utils.EsitiProperties;
import org.openspcoop2.utils.beans.BlackListElement;
import org.openspcoop2.web.monitor.core.logger.LoggerManager;
import org.openspcoop2.web.monitor.core.utils.BeanUtils;
import org.openspcoop2.web.monitor.transazioni.constants.TransazioniCostanti;

public class TransazioneApplicativoServerBean extends TransazioneApplicativoServer {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public TransazioneApplicativoServerBean() {
		super();
	}
	
	public TransazioneApplicativoServerBean(TransazioneApplicativoServer transazioneApplicativoServer) {
		
		List<BlackListElement> metodiEsclusi = new ArrayList<BlackListElement>(0);
		
		metodiEsclusi.add(new BlackListElement("setLatenza", Long.class));
		
		BeanUtils.copy(this, transazioneApplicativoServer, metodiEsclusi);
	}
	
	public Long getLatenza() {
		return 0L;
	}

	public void setLatenza(Long latenza) {
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
	
	public boolean isShowContesto(){
		try{
			return EsitiProperties.getInstance(LoggerManager.getPddMonitorCoreLogger(),this.getProtocollo()).getEsitiTransactionContextCode().size()>1;
		}catch(Exception e){
			LoggerManager.getPddMonitorCoreLogger().error("Errore durante il calcolo dei contesti: "+e.getMessage(),e);
			return false;
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
	
	public String getClusterId() {
		return this.clusterIdConsegna;
	}
	
	public String getNomeServizioApplicativoErogatore() {
		return this.servizioApplicativoErogatore;
	}
}
