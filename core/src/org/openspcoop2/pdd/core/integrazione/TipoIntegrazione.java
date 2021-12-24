/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2022 Link.it srl (https://link.it).
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

/**     
 * Enumeration TipoIntegrazione
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public enum TipoIntegrazione {

	DISABILITATO ("none", "Disabilitato", null),
	
	// NOTA: Usare 'Abilitato' e non  'Genera' perchè si riferisce anche alla lettura dell'header di integrazione in ingresso
	
	TRASPORTO_EXT ("trasportoExt", "Abilitato verso dominio interno e esterno", GruppoIntegrazione.HTTP),
	TRASPORTO ("trasporto", "Abilitato solo verso dominio interno", GruppoIntegrazione.HTTP),
	
	URL_EXT ("urlBasedExt", "Abilitato verso dominio interno e esterno", GruppoIntegrazione.URL),
	URL ("urlBased", "Abilitato solo verso dominio interno", GruppoIntegrazione.URL),
	// NON HO CAPITO BENE DOVE SI USA, SEMMAI BASTA REGISTRARLO COME PLUGIN URL_ONLY_READ ("urlBasedOnlyRead", "Abilitato solo per lettura dei metadati forniti dal client", GruppoIntegrazione.URL),
	
	SOAP_EXT ("soapExt", "Abilitato verso dominio interno e esterno", GruppoIntegrazione.SOAP),
	SOAP ("soap", "Abilitato solo verso dominio interno", GruppoIntegrazione.SOAP),
	
	WSA_EXT ("wsaExt", "Abilitato verso dominio interno e esterno", GruppoIntegrazione.WSA),
	WSA ("wsa", "Abilitato solo verso dominio interno", GruppoIntegrazione.WSA),
	
	TEMPLATE ("template", "Abilitato su richiesta e risposta", GruppoIntegrazione.TEMPLATE),
	TEMPLATE_REQUEST ("template-request", "Abilitato solo sulla richiesta", GruppoIntegrazione.TEMPLATE),
	TEMPLATE_RESPONSE ("template-response", "Abilitato solo sulla risposta", GruppoIntegrazione.TEMPLATE),
	
	OPENSPCOOP2_TRASPORTO_EXT ("openspcoop2-trasportoExt", "Abilitato verso dominio interno e esterno senza prefisso 'X-'", GruppoIntegrazione.BACKWARD_COMPATIBILITY_OPENSPCOOP2_HTTP),
	OPENSPCOOP2_TRASPORTO ("openspcoop2-trasporto", "Abilitato solo verso dominio interno senza prefisso 'X-'", GruppoIntegrazione.BACKWARD_COMPATIBILITY_OPENSPCOOP2_HTTP),
	OPENSPCOOP2_X_TRASPORTO_EXT ("openspcoop2-x-trasportoExt", "Abilitato verso dominio interno e esterno con prefisso 'X-'", GruppoIntegrazione.BACKWARD_COMPATIBILITY_OPENSPCOOP2_HTTP),
	OPENSPCOOP2_X_TRASPORTO ("openspcoop2-x-trasporto", "Abilitato solo verso dominio interno con prefisso 'X-'", GruppoIntegrazione.BACKWARD_COMPATIBILITY_OPENSPCOOP2_HTTP),

	OPENSPCOOP1_TRASPORTO_EXT ("openspcoop1-trasportoExt", "Abilitato verso dominio interno e esterno senza prefisso 'X-'", GruppoIntegrazione.BACKWARD_COMPATIBILITY_OPENSPCOOP1_HTTP),
	OPENSPCOOP1_TRASPORTO ("openspcoop1-trasporto", "Abilitato solo verso dominio interno senza prefisso 'X-'", GruppoIntegrazione.BACKWARD_COMPATIBILITY_OPENSPCOOP1_HTTP),
	OPENSPCOOP1_X_TRASPORTO_EXT ("openspcoop1-x-trasportoExt", "Abilitato verso dominio interno e esterno con prefisso 'X-'", GruppoIntegrazione.BACKWARD_COMPATIBILITY_OPENSPCOOP1_HTTP),
	OPENSPCOOP1_X_TRASPORTO ("openspcoop1-x-trasporto", "Abilitato solo verso dominio interno con prefisso 'X-'", GruppoIntegrazione.BACKWARD_COMPATIBILITY_OPENSPCOOP1_HTTP),
	
	OPENSPCOOP2_URL_EXT ("openspcoop2-urlBasedExt", "Abilitato verso dominio interno e esterno", GruppoIntegrazione.BACKWARD_COMPATIBILITY_OPENSPCOOP2_URL),
	OPENSPCOOP2_URL ("openspcoop2-urlBased", "Abilitato solo verso dominio interno", GruppoIntegrazione.BACKWARD_COMPATIBILITY_OPENSPCOOP2_URL),
	
	OPENSPCOOP1_URL_EXT ("openspcoop1-urlBasedExt", "Abilitato verso dominio interno e esterno", GruppoIntegrazione.BACKWARD_COMPATIBILITY_OPENSPCOOP1_URL),
	OPENSPCOOP1_URL ("openspcoop1-urlBased", "Abilitato solo verso dominio interno", GruppoIntegrazione.BACKWARD_COMPATIBILITY_OPENSPCOOP1_URL),
	
	OPENSPCOOP2_SOAP_EXT ("openspcoop2-soapExt", "Abilitato verso dominio interno e esterno", GruppoIntegrazione.BACKWARD_COMPATIBILITY_OPENSPCOOP2_SOAP),
	OPENSPCOOP2_SOAP ("openspcoop2-soap", "Abilitato solo verso dominio interno", GruppoIntegrazione.BACKWARD_COMPATIBILITY_OPENSPCOOP2_SOAP),
	
	OPENSPCOOP1_SOAP_EXT ("openspcoop1-soapExt", "Abilitato verso dominio interno e esterno", GruppoIntegrazione.BACKWARD_COMPATIBILITY_OPENSPCOOP1_SOAP),
	OPENSPCOOP1_SOAP ("openspcoop1-soap", "Abilitato solo verso dominio interno", GruppoIntegrazione.BACKWARD_COMPATIBILITY_OPENSPCOOP1_SOAP);
	
	
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
	
	/** Group */
	private GruppoIntegrazione group;
	public GruppoIntegrazione getGroup()
	{
		return this.group;
	}

	/** Official Constructor */
	TipoIntegrazione(String value, String label, GruppoIntegrazione group)
	{
		this.value = value;
		this.label = label;
		this.group = group;
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
		for (TipoIntegrazione tmp : values()) {
			res[i]=tmp.getValue();
			i++;
		}
		return res;
	}
	public static String[] toLabels(){
		String[] res = new String[values().length];
		int i=0;
		for (TipoIntegrazione tmp : values()) {
			res[i]=tmp.getLabel();
			i++;
		}
		return res;
	}
	
	public static String[] toValues(GruppoIntegrazione gruppo){
		List<String> list = new ArrayList<String>();
		for (TipoIntegrazione tmp : values()) {
			if(gruppo==null) {
				if(tmp.getGroup()!=null) {
					continue;
				}
			}
			else {
				if(!gruppo.equals(tmp.getGroup())) {
					continue;
				}
			}
			list.add(tmp.getValue());
		}
		if(list.isEmpty()) {
			return null;
		}
		return list.toArray(new String[list.size()]);
	}
	public static String[] toLabels(GruppoIntegrazione gruppo){
		List<String> list = new ArrayList<String>();
		for (TipoIntegrazione tmp : values()) {
			if(gruppo==null) {
				if(tmp.getGroup()!=null) {
					continue;
				}
			}
			else {
				if(!gruppo.equals(tmp.getGroup())) {
					continue;
				}
			}
			list.add(tmp.getLabel());
		}
		if(list.isEmpty()) {
			return null;
		}
		return list.toArray(new String[list.size()]);
	}
	
	public static boolean contains(String value){
		return toEnumConstant(value)!=null;
	}
	
	public static TipoIntegrazione toEnumConstant(String value){
		try{
			return toEnumConstant(value,false);
		}catch(NotFoundException notFound){
			return null;
		}
	}
	public static TipoIntegrazione toEnumConstant(String value, boolean throwNotFoundException) throws NotFoundException{
		TipoIntegrazione res = null;
		for (TipoIntegrazione tmp : values()) {
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
