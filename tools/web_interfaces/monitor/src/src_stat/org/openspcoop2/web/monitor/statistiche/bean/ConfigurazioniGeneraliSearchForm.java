package org.openspcoop2.web.monitor.statistiche.bean;

import javax.faces.event.ActionEvent;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.openspcoop2.core.transazioni.constants.PddRuolo;
import org.openspcoop2.web.monitor.core.bean.BaseSearchForm;
import org.openspcoop2.web.monitor.core.core.PddMonitorProperties;
import org.openspcoop2.web.monitor.core.logger.LoggerManager;

public class ConfigurazioniGeneraliSearchForm extends BaseSearchForm implements Cloneable{

	private static Logger log = LoggerManager.getPddMonitorCoreLogger();
	private PddRuolo tipologiaTransazioni = null;
	
	public static final String NON_SELEZIONATO = "--"; 

	public ConfigurazioniGeneraliSearchForm(){
		super();
		try {
			PddMonitorProperties govwayMonitorProperties = PddMonitorProperties.getInstance(log);
			this.setUseCount(govwayMonitorProperties.isAttivoUtilizzaCountStatisticheListaConfigurazioni()); 
		}catch (Exception e) {
			log.error(e.getMessage(), e);
		}
	}
	
	@Override
	public Object clone() throws CloneNotSupportedException {
		return super.clone();
	}

	@Override
	public void initSearchListener(ActionEvent ae) {
		super.initSearchListener(ae);
		this.setTipologiaRicerca("ingresso"); 
		this.tipologiaTransazioni = PddRuolo.APPLICATIVA;
		this.tipologiaTransazioniListener(ae);
		this.executeQuery = false;
	}

	@Override
	protected String ripulisciValori(){
		this.initSearchListener(null);
		return null;
	}

	public void tipologiaTransazioniListener(ActionEvent ae){}
	
	public PddRuolo getTipologiaTransazioni() {
		return this.tipologiaTransazioni;
	}
	public void setTipologiaTransazioni(PddRuolo tipologiaTransazioni) {
		this.tipologiaTransazioni = tipologiaTransazioni;
	}

	public void set_value_tipologiaTransazioni(String value) {
		if (StringUtils.isEmpty(value) || "*".equals(value))
			this.tipologiaTransazioni = null;
		else 
			this.tipologiaTransazioni = (PddRuolo) PddRuolo.toEnumConstantFromString(value);
	}

	public String get_value_tipologiaTransazioni() {
		if(this.tipologiaTransazioni == null){
			return "*";
		}else{
			return this.tipologiaTransazioni.toString();
		}
	}
	
	@Override
	protected String eseguiAggiorna() {
		return null;
	}
}
