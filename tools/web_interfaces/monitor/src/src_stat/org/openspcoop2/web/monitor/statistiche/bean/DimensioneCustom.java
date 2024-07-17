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
package org.openspcoop2.web.monitor.statistiche.bean;

import java.io.Serializable;

import org.openspcoop2.generic_project.beans.IEnumeration;

/**
 * NumeroDimensioni
 * 
 * @author Pintori Giuliano (pintori@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 *
 */
public enum DimensioneCustom implements IEnumeration , Serializable , Cloneable{

	TAG ("tag"), API ("api"), IMPLEMENTAZIONE_API ("apiImplementation"), OPERAZIONE ("operation"),
	SOGGETTO_LOCALE ("localOrganization"), SOGGETTO_REMOTO ("remoteOrganization"),
	SOGGETTO_FRUITORE ("clientOrganization"), SOGGETTO_EROGATORE ("providerOrganization"),
	TOKEN_ISSUER ("tokenIssuer"), TOKEN_CLIENT_ID ("tokenClientId"), TOKEN_SUBJECT ("tokenSubject"), 
	TOKEN_USERNAME ("tokenUsername"), TOKEN_EMAIL ("tokenEMail"), TOKEN_PDND_ORGANIZATION ("tokenPdndOrganization"),
	PRINCIPAL ("principal"),
	APPLICATIVO_TRASPORTO("client"), APPLICATIVO_TOKEN ("tokenClient"),
	INDIRIZZO_IP("ipAddress"),
	ESITO("result");

	/** Value */
	private String value;
	@Override
	public String getValue()
	{
		return this.value;
	}

	/** Official Constructor */
	DimensioneCustom(String value)
	{
		this.value = value;
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

	/** Utilities */

	public static String[] toArray(){
		String[] res = new String[values().length];
		int i=0;
		for (DimensioneCustom tmp : values()) {
			res[i]=tmp.getValue();
			i++;
		}
		return res;
	}	
	public static String[] toStringArray(){
		String[] res = new String[values().length];
		int i=0;
		for (DimensioneCustom tmp : values()) {
			res[i]=tmp.toString();
			i++;
		}
		return res;
	}
	public static String[] toEnumNameArray(){
		String[] res = new String[values().length];
		int i=0;
		for (DimensioneCustom tmp : values()) {
			res[i]=tmp.name();
			i++;
		}
		return res;
	}

	public static boolean contains(String value){
		return toEnumConstant(value)!=null;
	}

	public static DimensioneCustom toEnumConstant(String value){
		DimensioneCustom res = null;
		for (DimensioneCustom tmp : values()) {
			if(tmp.getValue().equals(value)){
				res = tmp;
				break;
			}
		}
		return res;
	}

	public static IEnumeration toEnumConstantFromString(String value){
		DimensioneCustom res = null;
		for (DimensioneCustom tmp : values()) {
			if(tmp.toString().equals(value)){
				res = tmp;
				break;
			}
		}
		return res;
	}

}
