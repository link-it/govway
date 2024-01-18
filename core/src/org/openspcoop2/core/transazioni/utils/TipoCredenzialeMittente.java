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

package org.openspcoop2.core.transazioni.utils;

import org.openspcoop2.generic_project.exception.NotFoundException;

/**     
 * TipoCredenzialeMittente
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public enum TipoCredenzialeMittente {

	TRASPORTO("trasporto"),
	
	TOKEN_ISSUER("token_issuer"), 
	TOKEN_CLIENT_ID("token_clientId"), 
	TOKEN_SUBJECT("token_subject"), 
	TOKEN_USERNAME("token_username"), 
	TOKEN_EMAIL("token_eMail"),
	
	PDND_CLIENT_JSON("pdnd_client_json"), 
	PDND_ORGANIZATION_JSON("pdnd_org_json"), 
	PDND_ORGANIZATION_NAME("pdnd_org_name"),
	
	CLIENT_ADDRESS("client_address"), 
	EVENTI("eventi"),
	GRUPPI("gruppi"),
	API("api");
	
	private String rawValue; // finisce sul db
	
	public String getRawValue() {
		return this.rawValue;
	}

	TipoCredenzialeMittente(String rawValue){
		this.rawValue = rawValue;
	}
	
	public static TipoCredenzialeMittente toEnumConstant(String rawValue){
		try{
			return toEnumConstant(rawValue,false);
		}catch(NotFoundException notFound){
			return null;
		}
	}
	public static TipoCredenzialeMittente toEnumConstant(String rawValue, boolean throwNotFoundException) throws NotFoundException{
		TipoCredenzialeMittente res = null;
		for (TipoCredenzialeMittente tmp : values()) {
			if(tmp.getRawValue().equals(rawValue)){
				res = tmp;
				break;
			}
		}
		if(res==null && throwNotFoundException){
			throw new NotFoundException("Enum with value ["+rawValue+"] not found");
		}
		return res;
	}
}
