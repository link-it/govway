/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2026 Link.it srl (https://link.it).
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
package org.openspcoop2.utils.transport.http.credential;

import java.io.Serializable;

/**
 * Enum dei tipi di reader disponibili
 * 
 * @author Pintori Giuliano (pintori@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public enum PrincipalReaderType implements  Serializable , Cloneable {
	
	PRINCIPAL ("principal","org.openspcoop2.utils.transport.http.credential.IdentityPrincipalReader"),
	HEADER ("header","org.openspcoop2.utils.transport.http.credential.IdentityHttpReader"),
	OAUTH2 ("oauth2","org.openspcoop2.utils.oauth2.OAuth2PrincipalReader");

	/** Value */
	private String value;
	private String className;
	public String getValue()
	{
		return this.value;
	}

	/** Official Constructor */
	PrincipalReaderType(String value, String className)
	{
		this.value = value;
		this.className = className;
	}
	
	@Override
	public String toString(){
		return this.value;
	}

	public static boolean contains(String value){
		return toEnumConstant(value)!=null;
	}
	
	public static PrincipalReaderType toEnumConstant(String value){
		PrincipalReaderType res = null;
		for (PrincipalReaderType tmp : values()) {
			if(tmp.getValue().equals(value)){
				res = tmp;
				break;
			}
		}
		return res;
	}
	
	public String getClassName() {
        return this.className;
    }
}
