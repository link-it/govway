package org.openspcoop2.web.monitor.core.converter;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;

import org.slf4j.Logger;

import org.openspcoop2.monitor.engine.condition.EsitoUtils;
import org.openspcoop2.web.monitor.core.logger.LoggerManager;

public class EsitoContestoConverter implements Converter {
	
	private EsitoUtils esitoUtils;
	
	public EsitoContestoConverter(){
		Logger logger = LoggerManager.getPddMonitorCoreLogger();
		try{
			this.esitoUtils = new EsitoUtils(logger);
		}catch(Exception e){
			logger.error("Errore durante l'inizializzazione dell'EsitoContestoConverter");
		}
	}
	
	
	@Override
	public Object getAsObject(FacesContext ctx, UIComponent component, String value) {
	
		return this.esitoUtils.getEsitoContestoValueFromLabel(value);
		
	}

	@Override
	public String getAsString(FacesContext ctx, UIComponent component, Object value) {
		
		return this.esitoUtils.getEsitoContestoLabelFromValue(value);
		
	}

}
