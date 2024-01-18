/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2024 Link.it srl (https://link.it).
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
package org.openspcoop2.web.monitor.core.mbean;

import javax.faces.event.ActionEvent;

import org.openspcoop2.core.config.driver.db.DriverConfigurazioneDB;
import org.openspcoop2.core.registry.driver.db.DriverRegistroServiziDB;
import org.openspcoop2.web.monitor.core.dao.DynamicUtilsService;
import org.openspcoop2.web.monitor.core.dao.IDynamicUtilsService;
import org.openspcoop2.web.monitor.core.dao.IService;

/**
 * PdDBaseBean
 * 
 * @author Pintori Giuliano (pintori@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 *
 */
@SuppressWarnings("rawtypes")
public class PdDBaseBean<T,K,ServiceType extends IService> extends BaseBean<T, K, ServiceType>{
		
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L; 
	
	@Override
	public void setService(ServiceType service) {
		this.service=service;
	}
	
	protected transient IDynamicUtilsService dynamicUtilsService = null;
	
	public PdDBaseBean (){
		this.dynamicUtilsService = new DynamicUtilsService();
	}
	public PdDBaseBean (org.openspcoop2.core.commons.search.dao.IServiceManager serviceManager, org.openspcoop2.core.plugins.dao.IServiceManager pluginsServiceManager,
			DriverRegistroServiziDB driverRegistroServiziDB, DriverConfigurazioneDB driverConfigurazioneDB){
		this.dynamicUtilsService = new DynamicUtilsService(serviceManager, pluginsServiceManager,
				driverRegistroServiziDB, driverConfigurazioneDB);
	}	
	
//	public List<Soggetto> soggettiAutoComplete(Object val){
//		String tipoProtocollo = this.getProtocollo();
//		return _getListaSoggetti(val, tipoProtocollo);
//	}
//
//	protected List<Soggetto> _getListaSoggetti(Object val, String tipoProtocollo) {
//		List<Soggetto> list = null;
//		Soggetto s = new Soggetto();
//		s.setNomeSoggetto("--");
//
//		if(val==null || StringUtils.isEmpty((String)val))
//			list = new ArrayList<Soggetto>();
//		else{
//			list = this.dynamicUtilsService.soggettiAutoComplete(tipoProtocollo,(String)val);
//		}
//		
//		list.add(0,s);
//		return list;
//	}
		
	/**
	 * Listener eseguito prima di aggiungere un nuovo ricerca, setta a null il selectedElement
	 * in modo da "scordarsi" i valori gia' impostati.
	 * @param ae
	 */
	public void addNewListener(ActionEvent ae){
		this.selectedElement = null;
	}
	
	public String getProtocollo(){
		return null;
	}
}
