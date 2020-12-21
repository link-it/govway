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
package org.openspcoop2.web.lib.mvc.dynamic.components;

import java.util.ArrayList;

import javax.faces.event.ActionEvent;

import org.apache.commons.lang.StringUtils;
import org.openspcoop2.monitor.engine.dynamic.IDynamicLoader;
import org.openspcoop2.monitor.sdk.condition.Context;
import org.openspcoop2.monitor.sdk.constants.Constants;
import org.openspcoop2.monitor.sdk.exceptions.ParameterException;
import org.openspcoop2.monitor.sdk.parameters.Parameter;
import org.openspcoop2.utils.LoggerWrapperFactory;
import org.openspcoop2.web.lib.mvc.DataElement;
import org.slf4j.Logger;

/**
 * BaseComponent
 * 
 * @author Pintori Giuliano (pintori@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 *
 */
public abstract class BaseComponent<T> extends Parameter<T>{

	protected static final String CONTANER_SUFFIX = "-ctr";
	
	private static Logger log = LoggerWrapperFactory.getLogger(BaseComponent.class);
	
	private Context context;
	private IDynamicLoader loader;
	
	private Parameter<T> sdkParameter;
	
	public BaseComponent(Parameter<T> parameter, IDynamicLoader loader) {
		super(parameter);
		this.sdkParameter = parameter; // L'SDK Parameter viene utilizzato per implementare i metodi astratti setValueAsString e getValueAsString
		this.loader = loader;
	}	

	public Context getContext() {
		return this.context;
	}

	public void setContext(Context context) {
		this.context = context;
	}

	@Override
	public void setValueAsString(String value) throws ParameterException{
		this.sdkParameter.setValueAsString(value);
		this.setValue(this.sdkParameter.getValue());
	}
	
	@Override
	public String getValueAsString() throws ParameterException{
		return this.sdkParameter.getValueAsString();
	}
	
	private boolean initialized = false;
	
	@Override
	public T getValue(){
		if(super.getValue()==null){
			if(this.initialized){
				return null;
			}
			else{
				this.initialized = true;
				T defaultValue = this.getRendering().getDefaultValue();
				this.setValue(defaultValue);
				return defaultValue;
			}
		}
		else{
			return super.getValue();
		}
	}
	
	@Override
	public void setValue(T val){
		if( (val instanceof String) && 
			(("--".equals(val)) || "".equals(val)) 
		  ){		
			val = null;
		}
		super.setValue(val);
		this.sdkParameter.setValue(val);
	}
	
	/**
	 * Indica se il componente deve essere renderizzato o meno.
	 * 
	 * Se e' presente un {@link IDynamicLoader} allora viene utilizzato per decidere se renderizzare il componente
	 * altrimenti viene sempre renderizzato.
	 * @return true se il componente deve essere renderizzato false altrimenti.
	 */
	public boolean getRendered() {
		if(this.getLoader()==null)
			return true;
		try{
			//r potrebbe essere null qualora il loader non trovasse il componente tramite l'id fornito
			this.getLoader().updateRendering(this, this.context);
			return this.getRendering().isHidden()==false;
		}catch (Exception e) {
			BaseComponent.log.error("Impossibile recuperare le informazioni di rendering dal Loader: "+e.getMessage());
			return true;
		}
	}
	
	public IDynamicLoader getLoader() {
		return this.loader;
	}

	
	public void setLoader(IDynamicLoader loader) {
		this.loader = loader;
	}
	
	public String getContainerId() {
		String cid =  this.buildContainerId(this.getId());
		return cid;
	}
	
	public String getContainersIdToRefresh(){
		if(this.getRefreshParamIds()!=null){
			ArrayList<String> res = new ArrayList<String>();
			for (String pid : this.getRefreshParamIds()) {
				 res.add(this.buildContainerId(pid));
			}
			return StringUtils.join(res, ",");		
		}else{
			return this.buildContainerId(this.getId());
		}
	}
	
	public void valueSelectedListener(ActionEvent ae){
		this.getLoader().valueSelectedListener(this, this.getContext());
	}
	
	private String buildContainerId(String id) {
		if(id!=null){
			String tmp = id.trim();
			if(tmp.endsWith(Constants.CONTANER_SUFFIX_NO_PLUGIN)){
				return tmp.substring(0, (tmp.length()-Constants.CONTANER_SUFFIX_NO_PLUGIN.length()));
			}
			else{
				return tmp + CONTANER_SUFFIX;
			}
		}
		return null;
	}
	
	public abstract DataElement toDataElement() throws ParameterException ;
	
	public abstract void setValueFromRequest(String parameterValue) throws ParameterException;
}
