/*
 * GovWay - A customizable API Gateway 
 * http://www.govway.org
 *
 * from the Link.it OpenSPCoop project codebase
 * 
 * Copyright (c) 2005-2019 Link.it srl (http://link.it). 
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

package org.openspcoop2.monitor.engine.alarm;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.openspcoop2.core.allarmi.Allarme;
import org.openspcoop2.core.allarmi.utils.ProjectInfo;
import org.openspcoop2.core.commons.dao.DAOFactory;
import org.openspcoop2.core.commons.dao.DAOFactoryProperties;
import org.openspcoop2.generic_project.beans.IProjectInfo;
import org.openspcoop2.monitor.sdk.condition.Context;
import org.openspcoop2.monitor.sdk.constants.CRUDType;
import org.openspcoop2.monitor.sdk.constants.SearchType;
import org.openspcoop2.monitor.sdk.parameters.Parameter;
import org.openspcoop2.protocol.sdk.builder.EsitoTransazione;
import org.openspcoop2.utils.TipiDatabase;

/**
 * AlarmContext
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class AlarmContext implements Context{
	
	private Allarme allarme;
	private Logger log;
	private DAOFactory daoFactory;
	private List<Parameter<?>> parameters;

	public AlarmContext(Allarme allarme,Logger log, DAOFactory daoFactory){
		this(allarme, log, daoFactory, null);
	}
	
	public AlarmContext(Allarme allarme,Logger log, DAOFactory daoFactory,List<Parameter<?>> parameters){
		this.allarme = allarme;
		this.log = log;
		this.daoFactory = daoFactory;
		this.parameters = parameters;
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
	public String getTipoSoggettoMittente(){
		if(this.allarme!=null && 
				this.allarme.getFiltro()!=null &&
				StringUtils.isNotEmpty(this.allarme.getFiltro().getTipoFruitore()) &&
				!"*".equals(this.allarme.getFiltro().getTipoFruitore())
			){
			return this.allarme.getFiltro().getTipoFruitore();			
		}
		return null;
	}
	@Override
	public String getSoggettoMittente(){
		if(this.allarme!=null && 
				this.allarme.getFiltro()!=null &&
				StringUtils.isNotEmpty(this.allarme.getFiltro().getNomeFruitore()) &&
				!"*".equals(this.allarme.getFiltro().getNomeFruitore())
			){
			return this.allarme.getFiltro().getNomeFruitore();			
		}
		return null;
	}
	
	@Override
	public String getTipoSoggettoDestinatario(){
		if(this.allarme!=null && 
				this.allarme.getFiltro()!=null &&
				StringUtils.isNotEmpty(this.allarme.getFiltro().getTipoErogatore()) &&
				!"*".equals(this.allarme.getFiltro().getTipoErogatore())
			){
			return this.allarme.getFiltro().getTipoErogatore();			
		}
		return null;
	}
	@Override
	public String getSoggettoDestinatario(){
		if(this.allarme!=null && 
				this.allarme.getFiltro()!=null &&
				StringUtils.isNotEmpty(this.allarme.getFiltro().getNomeErogatore()) &&
				!"*".equals(this.allarme.getFiltro().getNomeErogatore())
			){
			return this.allarme.getFiltro().getNomeErogatore();			
		}
		return null;
	}
	
	@Override
	public String getTipoServizio(){
		if(this.allarme!=null && 
				this.allarme.getFiltro()!=null &&
				StringUtils.isNotEmpty(this.allarme.getFiltro().getTipoServizio()) &&
				!"*".equals(this.allarme.getFiltro().getTipoServizio())
			){
			return this.allarme.getFiltro().getTipoServizio();			
		}
		return null;
	}
	@Override
	public String getServizio(){
		if(this.allarme!=null && 
				this.allarme.getFiltro()!=null &&
				StringUtils.isNotEmpty(this.allarme.getFiltro().getNomeServizio()) &&
				!"*".equals(this.allarme.getFiltro().getNomeServizio())
			){
			return this.allarme.getFiltro().getNomeServizio();			
		}
		return null;
	}
	@Override
	public Integer getVersioneServizio() {
		if(this.allarme!=null && 
				this.allarme.getFiltro()!=null &&
				this.allarme.getFiltro().getVersioneServizio()!=null &&
				this.allarme.getFiltro().getVersioneServizio()>0
			){
			return this.allarme.getFiltro().getVersioneServizio();			
		}
		return null;
	}

	@Override
	public String getAzione() {
		if(this.allarme!=null && 
				this.allarme.getFiltro()!=null &&
				StringUtils.isNotEmpty(this.allarme.getFiltro().getAzione()) &&
				!"*".equals(this.allarme.getFiltro().getAzione())
			){
			return this.allarme.getFiltro().getAzione();			
		}
		return null;
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
		// 2020/10/09 modifica per salvataggio in console
		Map<String, Parameter<?>> map  =  new HashMap<String, Parameter<?>>();
		if(this.parameters != null){
			for (Parameter<?> param : this.parameters) {
				map.put(param.getId(), param);
			}
		}
		return map;
	}


	@Override
	public TipiDatabase getDatabaseType() {
		return _getTipoDatabase(ProjectInfo.getInstance());
	}

	public TipiDatabase _getTipoDatabase(IProjectInfo nomeDAO) {
		try{
			DAOFactoryProperties prop =  DAOFactoryProperties.getInstance(this.getLogger());
			return prop.getTipoDatabaseEnum(nomeDAO);
		}catch(Exception e){
			this.log.error(e.getMessage(),e);
			return null;
		}
	}

	@Override
	public Logger getLogger() {
		return this.log;
	}

	@Override
	public DAOFactory getDAOFactory() {
		return this.daoFactory;
	}
	
	@Override
	public CRUDType getTipoOperazione() {
		if(this.allarme!=null && this.allarme.getId()!=null &&
				this.allarme.getId()>0) {
			return CRUDType.UPDATE;
		}
		else {
			return CRUDType.CREATE;
		}
	}

}
