/*
 * GovWay - A customizable API Gateway 
 * http://www.govway.org
 * 
 * from the Link.it OpenSPCoop project codebase
 * 
 * Copyright (c) 2005-2018 Link.it srl (http://link.it).
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
