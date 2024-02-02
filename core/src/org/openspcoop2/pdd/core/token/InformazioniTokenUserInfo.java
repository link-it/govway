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


package org.openspcoop2.pdd.core.token;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.openspcoop2.pdd.core.token.parser.ITokenUserInfoParser;
import org.openspcoop2.utils.UtilsException;

/**     
 * InformazioniTokenUserInfo
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class InformazioniTokenUserInfo extends org.openspcoop2.utils.beans.BaseBean implements Serializable , Cloneable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public InformazioniTokenUserInfo(InformazioniToken informazioniToken, ITokenUserInfoParser tokenUserInfoParser) {
		String rawResponse = informazioniToken.getRawResponse();
		Map<String, Serializable> claims = informazioniToken.getClaims();
		tokenUserInfoParser.init(rawResponse, claims);
		this.fullName = tokenUserInfoParser.getFullName();
		this.firstName = tokenUserInfoParser.getFirstName();
		this.middleName = tokenUserInfoParser.getMiddleName();
		this.familyName = tokenUserInfoParser.getFamilyName();
		this.eMail = tokenUserInfoParser.getEMail();
		
	}
	public InformazioniTokenUserInfo(InformazioniTokenUserInfo ... informazioniTokens ) throws UtilsException {
		if(informazioniTokens!=null && informazioniTokens.length>0) {
			
			Object fullNameO = getValue("fullName", informazioniTokens); 
			if(fullNameO instanceof String) {
				this.fullName = (String) fullNameO;
			}
			
			Object firstNameO = getValue("firstName", informazioniTokens); 
			if(firstNameO instanceof String) {
				this.firstName = (String) firstNameO;
			}
			
			Object middleNameO = getValue("middleName", informazioniTokens); 
			if(middleNameO instanceof String) {
				this.middleName = (String) middleNameO;
			}
			
			Object familyNameO = getValue("familyName", informazioniTokens); 
			if(familyNameO instanceof String) {
				this.familyName = (String) familyNameO;
			}
			
			Object eMailO = getValue("eMail", informazioniTokens); 
			if(eMailO instanceof String) {
				this.eMail = (String) eMailO;
			}
			
		}
	}
	private static Object getValue(String field, InformazioniTokenUserInfo ... informazioniTokens) throws UtilsException {
		Object tmp = null;
		List<Object> listTmp = new ArrayList<>();
		String getMethodName = "get"+((field.charAt(0)+"").toUpperCase())+field.substring(1);
		Method getMethod = null;
		try {
			getMethod = InformazioniTokenUserInfo.class.getMethod(getMethodName);
		}catch(Exception e) {
			throw new UtilsException(e.getMessage(),e);
		}
		
		// La fusione avviene per precedenza dall'ultimo fino al primo (a meno che non sia una lista)
		tmp = getValue(getMethod, listTmp, informazioniTokens);
		if(!listTmp.isEmpty()) {
			return listTmp;
		}
		else {
			return tmp;
		}
	}
	private static Object getValue(Method getMethod, List<Object> listTmp, InformazioniTokenUserInfo ... informazioniTokens) throws UtilsException {
		Object tmp = null;
		for (int i = 0; i < informazioniTokens.length; i++) {
			InformazioniTokenUserInfo infoToken = informazioniTokens[i];
			Object o = null;
			try {
				o = getMethod.invoke(infoToken);
			}catch(Exception e) {
				throw new UtilsException(e.getMessage(),e);
			}
			if(o!=null) {
				tmp = getValue(o, listTmp);
			}
		}
		return tmp;
	}
	private static Object getValue(Object o, List<Object> listTmp) {
		Object tmp = null;
		if(o instanceof List<?>) {
			List<?> list = (List<?>) o;
			if(!list.isEmpty()) {
				for (Object object : list) {
					if(!listTmp.contains(object)) {
						listTmp.add(object);
					}
				}
			}
		}
		else {
			tmp = o;
		}
		return tmp;
	}
		
	/**  End-User's full name in displayable form including all name parts, possibly including titles and suffixes, ordered according to the End-User's locale and preferences. */ 
	private String fullName;
	
	/**  Given name(s) or first name(s) of the End-User. Note that in some cultures, people can have multiple given names; 
	     all can be present, with the names being separated by space characters.*/ 
	private String firstName;

	/**  Middle name(s) of the End-User. Note that in some cultures, people can have multiple middle names; 
	     all can be present, with the names being separated by space characters. Also note that in some cultures, middle names are not used. */ 
	private String middleName;
	
	/**  Surname(s) or last name(s) of the End-User. Note that in some cultures, people can have multiple family names or no family name; 
	     all can be present, with the names being separated by space characters. */ 
	private String familyName;
	
	/**  End-User's preferred e-mail address. */ 
	private String eMail;
	
	
	public String getFullName() {
		return this.fullName;
	}
	public void setFullName(String fullName) {
		this.fullName = fullName;
	}
	
	public String getFirstName() {
		return this.firstName;
	}
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
	
	public String getMiddleName() {
		return this.middleName;
	}
	public void setMiddleName(String middleName) {
		this.middleName = middleName;
	}
	
	public String getFamilyName() {
		return this.familyName;
	}
	public void setFamilyName(String familyName) {
		this.familyName = familyName;
	}
	
	public String getEMail() {
		return this.eMail;
	}
	public void setEMail(String eMail) {
		this.eMail = eMail;
	}
	public String geteMail() { // clone
		return this.eMail;
	}
	public void seteMail(String eMail) { // clone
		this.eMail = eMail;
	}
}
