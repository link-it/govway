/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2023 Link.it srl (https://link.it).
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

package org.openspcoop2.pdd.core.integrazione;

import java.util.ArrayList;
import java.util.List;

import org.openspcoop2.generic_project.exception.NotFoundException;
import org.openspcoop2.message.constants.ServiceBinding;

/**     
 * Enumeration TipoIntegrazione
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public enum GruppoIntegrazione {

	HTTP ("http", "Header HTTP", "Header HTTP", true, false, null),
	URL ("urlBased", "Parametri della Url", "Parametri della Url", true, false, null),
	SOAP ("soap", "Header SOAP proprietari di GovWay", "Header SOAP GovWay", true, false, ServiceBinding.SOAP),
	WSA ("wsa", "Header SOAP con formato standard WS-Addressing", "WS-Addressing", true, false, ServiceBinding.SOAP),
	TEMPLATE ("template" , "Metadati definiti in template freemaker o velocity", "Template", true, false, null),
	AUTENTICAZIONE ("autenticazione" , "Header HTTP utilizzati dal backend per autenticare l'API Gateway", "Header HTTP di Autenticazione", false, false, null),
	PLUGIN ("plugin", "Plugin", "Plugin", true, true, null),
	BACKWARD_COMPATIBILITY_OPENSPCOOP2_HTTP ("backward_compatibility_op2_http", "Header HTTP generati come OpenSPCoop 2.x", "Header HTTP OpenSPCoop 2.x", true, false, null),
	BACKWARD_COMPATIBILITY_OPENSPCOOP2_URL ("backward_compatibility_op2_url", "Parametri della Url generati come OpenSPCoop 2.x", "Parametri Url OpenSPCoop 2.x", true, false, null),
	BACKWARD_COMPATIBILITY_OPENSPCOOP2_SOAP ("backward_compatibility_op2_soap", "Header SOAP generati come OpenSPCoop 2.x", "Header SOAP OpenSPCoop 2.x", true, false, ServiceBinding.SOAP),
	BACKWARD_COMPATIBILITY_OPENSPCOOP1_HTTP ("backward_compatibility_op1_http", "Header HTTP generati come OpenSPCoop 1.x", "Header HTTP OpenSPCoop 1.x", true, false, null),
	BACKWARD_COMPATIBILITY_OPENSPCOOP1_URL ("backward_compatibility_op1_url", "Parametri della Url generati come OpenSPCoop 1.x", "Parametri Url OpenSPCoop 1.x", true, false, null),
	BACKWARD_COMPATIBILITY_OPENSPCOOP1_SOAP ("backward_compatibility_op1_soap", "Header SOAP generati come OpenSPCoop 1.x", "Header SOAP OpenSPCoop 1.x", true, false, ServiceBinding.SOAP);

	
	/** Value */
	private String value;
	public String getValue()
	{
		return this.value;
	}
	
	/** Label */
	private String label;
	public String getLabel()
	{
		return this.label;
	}
	
	/** CompactLabel */
	private String compactLabel;
	public String getCompactLabel()
	{
		return this.compactLabel;
	}
	
	/** ParametriConfigurazione */
	private boolean config;
	public boolean isConfig() {
		return this.config;
	}

	/** MultiSelection */
	private boolean multi;
	public boolean isMulti() {
		return this.multi;
	}

	/** ServiceBinding */
	private ServiceBinding serviceBinding;
	public ServiceBinding getServiceBinding() {
		return this.serviceBinding;
	}

	/** Official Constructor */
	GruppoIntegrazione(String value, String label, String compactLabel, boolean config, boolean multi, ServiceBinding serviceBinding)
	{
		this.value = value;
		this.label = label;
		this.compactLabel = compactLabel;
		this.config = config;
		this.multi = multi;
		this.serviceBinding = serviceBinding;
	}
	
	@Override
	public String toString(){
		return this.value;
	}
	public boolean equals(String object){
		if(object==null)
			return false;
		return object.equals(this.getValue());	
	}
	
	
	public static String[] toValues(ServiceBinding serviceBinding){
		List<String> list = new ArrayList<>();
		for (GruppoIntegrazione tmp : values()) {
			if(tmp.getServiceBinding()==null || serviceBinding==null) {
				list.add(tmp.getValue());
			}
			else if(tmp.getServiceBinding().equals(serviceBinding)) {
				list.add(tmp.getValue());
			}
		}
		return list.toArray(new String[list.size()]);
	}
	public static String[] toLabels(boolean compact, ServiceBinding serviceBinding){
		List<String> list = new ArrayList<>();
		for (GruppoIntegrazione tmp : values()) {
			String label = compact?tmp.getCompactLabel():tmp.getLabel();
			if(tmp.getServiceBinding()==null || serviceBinding==null) {
				list.add(label);
			}
			else if(tmp.getServiceBinding().equals(serviceBinding)) {
				list.add(label);
			}
		}
		return list.toArray(new String[list.size()]);
	}
	
	public static boolean contains(String value){
		return toEnumConstant(value)!=null;
	}
	
	public static GruppoIntegrazione toEnumConstant(String value){
		try{
			return toEnumConstant(value,false);
		}catch(NotFoundException notFound){
			return null;
		}
	}
	public static GruppoIntegrazione toEnumConstant(String value, boolean throwNotFoundException) throws NotFoundException{
		GruppoIntegrazione res = null;
		for (GruppoIntegrazione tmp : values()) {
			if(tmp.getValue().equals(value)){
				res = tmp;
				break;
			}
		}
		if(res==null && throwNotFoundException){
			throw new NotFoundException("Enum with value ["+value+"] not found");
		}
		return res;
	}
}
