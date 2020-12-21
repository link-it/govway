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

package org.openspcoop2.web.monitor.allarmi.bean;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.openspcoop2.protocol.sdk.builder.EsitoTransazione;
import org.openspcoop2.utils.TipiDatabase;
import org.openspcoop2.web.monitor.allarmi.mbean.AllarmiBean;
import org.openspcoop2.web.monitor.core.core.PddMonitorProperties;
import org.openspcoop2.web.monitor.core.logger.LoggerManager;
import org.openspcoop2.core.commons.dao.DAOFactory;
import org.openspcoop2.core.transazioni.utils.ProjectInfo;
import org.openspcoop2.generic_project.beans.IProjectInfo;
import org.openspcoop2.monitor.sdk.condition.Context;
import org.openspcoop2.monitor.sdk.constants.CRUDType;
import org.openspcoop2.monitor.sdk.constants.SearchType;
import org.openspcoop2.monitor.sdk.parameters.Parameter;

/**
 * AllarmiContext 
 *
 * @author Giuliano Pintori (pintori@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class AllarmiContext implements Context{ 

	private AllarmiBean allarmiBean = null;
	private TipiDatabase tipoDatabase = null;

	public AllarmiContext(AllarmiBean allarmiBean){
		this.allarmiBean = allarmiBean;
	}

	@Override
	public SearchType getTipoRicerca() {
		return SearchType.ALL;
	}

	@Override
	public Date getIntervalloInferiore() {
		return null;
	}

	@Override
	public Date getIntervalloSuperiore() {
		return null;
	}
	
	@Override
	public String getTipoSoggettoMittente() {
		String tipoNome = this.allarmiBean.getTipoNomeMittenteFiltro();
		if(tipoNome!=null && tipoNome.contains("/")){
			return tipoNome.split("/")[0];
		}
		return null;
	}

	@Override
	public String getSoggettoMittente() {
		String tipoNome = this.allarmiBean.getTipoNomeMittenteFiltro();
		if(tipoNome!=null && tipoNome.contains("/")){
			return tipoNome.split("/")[1];
		}
		return null;
	}

	@Override
	public String getTipoSoggettoDestinatario() {
		String tipoNome = this.allarmiBean.getTipoNomeDestinatarioFiltro();
		if(tipoNome!=null && tipoNome.contains("/")){
			return tipoNome.split("/")[0];
		}
		return null;
	}
	
	@Override
	public String getSoggettoDestinatario() {
		String tipoNome = this.allarmiBean.getTipoNomeDestinatarioFiltro();
		if(tipoNome!=null && tipoNome.contains("/")){
			return tipoNome.split("/")[1];
		}
		return null;
	}

	@Override
	public String getTipoServizio() {
		String tipoNome = this.allarmiBean.getTipoNomeServizioFiltro();
		if(tipoNome!=null && tipoNome.contains("/")){
			return tipoNome.split("/")[0];
		}
		return null;
	}
	
	@Override
	public String getServizio() {
		String tipoNome = this.allarmiBean.getTipoNomeServizioFiltro();
		if(tipoNome!=null && tipoNome.contains("/")){
			return tipoNome.split("/")[1];
		}
		return null;
	}


	@Override
	public Integer getVersioneServizio() {
		String tipoNome = this.allarmiBean.getTipoNomeServizioFiltro();
		if(tipoNome!=null && tipoNome.contains("/")){
			return Integer.valueOf(tipoNome.split("/")[2]);
		}
		return null;
	}
	
	@Override
	public String getAzione() {
		return this.allarmiBean.getAzioneFiltro();
	}

	@Override
	public EsitoTransazione getEsitoTransazione() {
		return null; // non dovrebbe venire usato
	}

	@Override
	public Parameter<?> getParameter(String paramID) {
		return this.getParameters().get(paramID);
	}

	@Override
	public Map<String, Parameter<?>> getParameters() {

		List<Parameter<?>> parameters = this.allarmiBean.getParameters();
		Map<String, Parameter<?>> map  =  new HashMap<String, Parameter<?>>();
		if(parameters != null){
			for (Parameter<?> param : parameters) {
				map.put(param.getId(), param);
			}
		}
		return map;
	}

	

	@Override
	public TipiDatabase getDatabaseType() {
		return _getTipoDatabase(ProjectInfo.getInstance());
	}

	public TipiDatabase _getTipoDatabase(IProjectInfo projectInfo) {
		if(this.tipoDatabase == null){
			try {
				PddMonitorProperties pddMonitorProperties = PddMonitorProperties.getInstance(getLogger());
				this.tipoDatabase = pddMonitorProperties.tipoDatabase(projectInfo);
			} catch (Exception e) {
				getLogger().error("Errore la get Tipo Database: " + e.getMessage(),e);
			}
		}
		return this.tipoDatabase;
	}

	@Override
	public Logger getLogger() {
		return LoggerManager.getPddMonitorCoreLogger();
	}

	@Override
	public DAOFactory getDAOFactory() {
		try{
			return DAOFactory.getInstance(LoggerManager.getPddMonitorSqlLogger());
		}catch(Exception e){
			throw new RuntimeException(e.getMessage(), e);
		}
	}
	
	@Override
	public CRUDType getTipoOperazione() {
		if(this.allarmiBean!=null && this.allarmiBean.getAllarme()!=null && this.allarmiBean.getAllarme().getId()!=null &&
				this.allarmiBean.getAllarme().getId()>0) {
			return CRUDType.UPDATE;
		}
		else {
			return CRUDType.CREATE;
		}
	}

	public void setShowFilter(boolean filter) {
		if(this.allarmiBean!=null) {
			this.allarmiBean.setShowFilter(filter);
		}
	}


}
