/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2021 Link.it srl (https://link.it).
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

import org.openspcoop2.generic_project.exception.NotFoundException;

/**     
 * Enumeration TipoIntegrazione
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public enum GruppoIntegrazione {

	HTTP ("http", "Header HTTP", "Header HTTP", false),
	URL ("urlBased", "Parametri della Url", "Parametri della Url", false),
	SOAP ("soap", "Header SOAP proprietari di GovWay", "Header SOAP GovWay", false),
	WSA ("wsa", "Header SOAP con formato standard WS-Addressing", "WS-Addressing", false),
	TEMPLATE ("template" , "Metadati definiti in template freemaker o velocity", "Template", false),
	PLUGIN ("plugin", "Plugin", "Plugin", true),
	BACKWARD_COMPATIBILITY_OPENSPCOOP2_HTTP ("backward_compatibility_op2_http", "Header HTTP generati come OpenSPCoop 2.x", "Header HTTP OpenSPCoop 2.x", false),
	BACKWARD_COMPATIBILITY_OPENSPCOOP1_HTTP ("backward_compatibility_op1_http", "Header HTTP generati come OpenSPCoop 1.x", "Header HTTP OpenSPCoop 1.x", false),
	BACKWARD_COMPATIBILITY_OPENSPCOOP2_URL ("backward_compatibility_op2_url", "Parametri della Url generati come OpenSPCoop 2.x", "Parametri Url OpenSPCoop 2.x", false),
	BACKWARD_COMPATIBILITY_OPENSPCOOP1_URL ("backward_compatibility_op1_url", "Parametri della Url generati come OpenSPCoop 1.x", "Parametri Url OpenSPCoop 1.x", false),
	BACKWARD_COMPATIBILITY_OPENSPCOOP2_SOAP ("backward_compatibility_op2_soap", "Header SOAP generati come OpenSPCoop 2.x", "Header SOAP OpenSPCoop 2.x", false),
	BACKWARD_COMPATIBILITY_OPENSPCOOP1_SOAP ("backward_compatibility_op1_soap", "Header SOAP generati come OpenSPCoop 1.x", "Header SOAP OpenSPCoop 1.x", false);

	
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
	
	/** MultiSelection */
	private boolean multi;
	public boolean isMulti() {
		return this.multi;
	}


	/** Official Constructor */
	GruppoIntegrazione(String value, String label, String compactLabel, boolean multi)
	{
		this.value = value;
		this.label = label;
		this.compactLabel = compactLabel;
		this.multi = multi;
	}
	
	@Override
	public String toString(){
		return this.value;
	}
	public boolean equals(GruppoIntegrazione object){
		if(object==null)
			return false;
		if(object.getValue()==null)
			return false;
		return object.getValue().equals(this.getValue());	
	}
	public boolean equals(String object){
		if(object==null)
			return false;
		return object.equals(this.getValue());	
	}
	
	
	public static String[] toValues(){
		String[] res = new String[values().length];
		int i=0;
		for (GruppoIntegrazione tmp : values()) {
			res[i]=tmp.getValue();
			i++;
		}
		return res;
	}
	public static String[] toLabels(){
		String[] res = new String[values().length];
		int i=0;
		for (GruppoIntegrazione tmp : values()) {
			res[i]=tmp.getLabel();
			i++;
		}
		return res;
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
