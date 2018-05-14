package org.openspcoop2.web.monitor.core.mbean;

import org.openspcoop2.core.commons.search.Soggetto;
import org.openspcoop2.web.monitor.core.dao.IService;
import org.openspcoop2.web.monitor.core.mbean.BaseBean;
import org.openspcoop2.web.monitor.core.dao.DynamicUtilsService;
import org.openspcoop2.web.monitor.core.dao.IDynamicUtilsService;

import java.util.ArrayList;
import java.util.List;

import javax.faces.event.ActionEvent;

import org.apache.commons.lang.StringUtils;

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
		
	public List<Soggetto> soggettiAutoComplete(Object val){
		String tipoProtocollo = this.getProtocollo();
	
		return _getListaSoggetti(val, tipoProtocollo);
	 
	}

	protected List<Soggetto> _getListaSoggetti(Object val, String tipoProtocollo) {
		List<Soggetto> list = null;
		Soggetto s = new Soggetto();
		s.setNomeSoggetto("--");
		
		

		if(val==null || StringUtils.isEmpty((String)val))
			list = new ArrayList<Soggetto>();
		else{
			
			  list = this.dynamicUtilsService.soggettiAutoComplete(tipoProtocollo,(String)val);
			
		}
		
		list.add(0,s);
		return list;
	}
		
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
