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
package org.openspcoop2.pdd.core.token.parser;

import java.util.Map;

/**     
 * ITokenUserInfoParser
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public interface ITokenUserInfoParser {

	public void init(String raw, Map<String,Object> claims);
		
	//  End-User's full name in displayable form including all name parts, possibly including titles and suffixes, ordered according to the End-User's locale and preferences. 
	public String getFullName();
	
	//  Given name(s) or first name(s) of the End-User. Note that in some cultures, people can have multiple given names; 
	// all can be present, with the names being separated by space characters. 
	public String getFirstName();

	//  Middle name(s) of the End-User. Note that in some cultures, people can have multiple middle names; 
	// all can be present, with the names being separated by space characters. Also note that in some cultures, middle names are not used. 
	public String getMiddleName();
	
	//  Surname(s) or last name(s) of the End-User. Note that in some cultures, people can have multiple family names or no family name; 
	// all can be present, with the names being separated by space characters. 
	public String getFamilyName();
	
	//  End-User's preferred e-mail address. 
	public String getEMail();
	
}

