package org.openspcoop2.web.monitor.core.bean;

import java.util.ArrayList;
import java.util.List;

import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;

import org.openspcoop2.web.monitor.core.core.PddMonitorProperties;
import org.openspcoop2.web.monitor.core.logger.LoggerManager;



public class UtentiSearchForm extends BaseSearchForm{
	
	private static Logger log =  LoggerManager.getPddMonitorCoreLogger();
	
	public final static String ROLE_ADMIN_LABEL  = "Amministratore";
	public final static String ROLE_CONFIG_LABEL  = "Configuratore";
	public final static String ROLE_OPERATORE_LABEL = "Operatore";
	
	private String nome= null;
	private String ruolo = null;
	private List<SelectItem> ruoli = null;
	
	public UtentiSearchForm() {
		super();
		try {
			PddMonitorProperties pddMonitorProperties = PddMonitorProperties.getInstance(log);
			this.setUseCount(pddMonitorProperties.isAttivoUtilizzaCountListaUtenti()); 
		}catch (Exception e) {
			log.error(e.getMessage(), e);
		}
	}
	
	@Override
	public void initSearchListener(ActionEvent ae) {
		super.initSearchListener(ae);
		
		this.nome = null;
		this.ruolo = null;
	}
	
	public String getRuolo() {
		return this.ruolo;
	}
	
	public void setRuolo(String ruolo) {
		this.ruolo = ruolo;
		if (StringUtils.isEmpty(ruolo) || "*".equals(ruolo))
			this.ruolo = null;
	}

	public List<SelectItem> getRuoli() {
		this.ruoli = new ArrayList<SelectItem>();
		this.ruoli.add(new SelectItem("*"));
		
		this.ruoli.add(new SelectItem(ROLE_ADMIN_LABEL));
		this.ruoli.add(new SelectItem(ROLE_CONFIG_LABEL));
		this.ruoli.add(new SelectItem(ROLE_OPERATORE_LABEL));
		
		return this.ruoli;
	}

	public String getNome() {
		return this.nome;
	}
	
	public void setNome(String nome) {
		this.nome = nome;
	}
	
	public void nomeUtenteSelected(ActionEvent ae) {
		
	}
	
	@Override
	protected String eseguiAggiorna() {
		return null;
	}
}
