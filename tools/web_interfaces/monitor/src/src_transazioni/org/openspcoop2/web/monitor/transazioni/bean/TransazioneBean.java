package org.openspcoop2.web.monitor.transazioni.bean;

import java.util.ArrayList;
import java.util.List;

import org.openspcoop2.protocol.sdk.constants.EsitoTransazioneName;
import org.openspcoop2.protocol.utils.EsitiProperties;

import it.link.pdd.core.transazioni.Transazione;
import org.openspcoop2.web.monitor.core.core.Utils;
import org.openspcoop2.web.monitor.core.logger.LoggerManager;
import org.openspcoop2.web.monitor.core.utils.BeanUtils;
import org.openspcoop2.web.monitor.core.utils.BlackListElement;

public class TransazioneBean extends Transazione{ 

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private Long latenzaTotale = null;
	private Long latenzaServizio = null;
	private Long latenzaPorta = null;

	public TransazioneBean() {
		super();
	}
	
	public TransazioneBean(Transazione transazione){
		List<BlackListElement> metodiEsclusi = new ArrayList<BlackListElement>(
				0);
		metodiEsclusi.add(new BlackListElement("setLatenzaTotale",
				Long.class));
		metodiEsclusi.add(new BlackListElement("setLatenzaServizio",
				Long.class));
		metodiEsclusi.add(new BlackListElement("setLatenzaPorta",
				Long.class));
		
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
			EsitiProperties esitiProperties = EsitiProperties.getInstance(LoggerManager.getPddMonitorCoreLogger());
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
			EsitiProperties esitiProperties = EsitiProperties.getInstance(LoggerManager.getPddMonitorCoreLogger());
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
			EsitiProperties esitiProperties = EsitiProperties.getInstance(LoggerManager.getPddMonitorCoreLogger());
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
			EsitiProperties esitiProperties = EsitiProperties.getInstance(LoggerManager.getPddMonitorCoreLogger());
			boolean res = esitiProperties.getEsitiCodeKo_senzaFaultApplicativo().contains(this.getEsito());
			//System.out.println("isEsitoOk:"+res+" (esitoChecked:"+this.getEsito()+")");
			return res;
		}catch(Exception e){
			LoggerManager.getPddMonitorCoreLogger().error("Errore durante il calcolo del layout dell'esito ["+this.getEsito()+"]: "+e.getMessage(),e);
			return false;
		}
	}
	
	public boolean isShowContesto(){
		try{
			return EsitiProperties.getInstance(LoggerManager.getPddMonitorCoreLogger()).getEsitiTransactionContextCode().size()>1;
		}catch(Exception e){
			LoggerManager.getPddMonitorCoreLogger().error("Errore durante il calcolo dei contesti: "+e.getMessage(),e);
			return false;
		}
	}
	
	public String getFaultCooperazionePretty(){
		String f = super.getFaultCooperazione();
		if(f!=null){
			return Utils.prettifyXml(f);
		}
		else{
			return f;
		}
	}
	
	public String getFaultIntegrazionePretty(){
		String f = super.getFaultIntegrazione();
		if(f!=null){
			return Utils.prettifyXml(f);
		}
		else{
			return f;
		}
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
}
