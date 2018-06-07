package org.openspcoop2.pdd.core.token;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.openspcoop2.pdd.core.token.parser.ITokenUserInfoParser;
import org.openspcoop2.utils.UtilsException;

public class InformazioniTokenUserInfo implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public InformazioniTokenUserInfo(InformazioniToken informazioniToken, ITokenUserInfoParser tokenUserInfoParser) throws UtilsException {
		String rawResponse = informazioniToken.getRawResponse();
		Map<String, String> claims = informazioniToken.getClaims();
		tokenUserInfoParser.init(rawResponse, claims);
		this.fullName = tokenUserInfoParser.getFullName();
		this.firstName = tokenUserInfoParser.getFirstName();
		this.middleName = tokenUserInfoParser.getMiddleName();
		this.familyName = tokenUserInfoParser.getFamilyName();
		this.eMail = tokenUserInfoParser.getEMail();
		
	}
	public InformazioniTokenUserInfo(InformazioniTokenUserInfo ... informazioniTokens ) throws Exception {
		if(informazioniTokens!=null && informazioniTokens.length>0) {
			
			Object fullName = getValue("fullName", informazioniTokens); 
			if(fullName!=null && fullName instanceof String) {
				this.fullName = (String) fullName;
			}
			
			Object firstName = getValue("firstName", informazioniTokens); 
			if(firstName!=null && firstName instanceof String) {
				this.firstName = (String) firstName;
			}
			
			Object middleName = getValue("middleName", informazioniTokens); 
			if(middleName!=null && middleName instanceof String) {
				this.middleName = (String) middleName;
			}
			
			Object familyName = getValue("familyName", informazioniTokens); 
			if(familyName!=null && familyName instanceof String) {
				this.familyName = (String) familyName;
			}
			
			Object eMail = getValue("eMail", informazioniTokens); 
			if(eMail!=null && eMail instanceof String) {
				this.eMail = (String) eMail;
			}
			
		}
	}
	private static Object getValue(String field, InformazioniTokenUserInfo ... informazioniTokens) throws Exception {
		Object tmp = null;
		List<Object> listTmp = new ArrayList<>();
		String getMethodName = "get"+((field.charAt(0)+"").toUpperCase())+field.substring(1);
		Method getMethod = InformazioniTokenUserInfo.class.getMethod(getMethodName);
		
		// La fusione avviene per precedenza dall'ultimo fino al primo (a meno che non sia una lista)
		for (int i = 0; i < informazioniTokens.length; i++) {
			InformazioniTokenUserInfo infoToken = informazioniTokens[i];
			Object o = getMethod.invoke(infoToken);
			if(o!=null) {
				if(o instanceof List<?>) {
					List<?> list = (List<?>) o;
					if(list.size()>0) {
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
			}
		}
		if(listTmp.size()>0) {
			return listTmp;
		}
		else {
			return tmp;
		}
	}
	
	//  End-User's full name in displayable form including all name parts, possibly including titles and suffixes, ordered according to the End-User's locale and preferences. 
	private String fullName;
	
	//  Given name(s) or first name(s) of the End-User. Note that in some cultures, people can have multiple given names; 
	// all can be present, with the names being separated by space characters. 
	private String firstName;

	//  Middle name(s) of the End-User. Note that in some cultures, people can have multiple middle names; 
	// all can be present, with the names being separated by space characters. Also note that in some cultures, middle names are not used. 
	private String middleName;
	
	//  Surname(s) or last name(s) of the End-User. Note that in some cultures, people can have multiple family names or no family name; 
	// all can be present, with the names being separated by space characters. 
	private String familyName;
	
	//  End-User's preferred e-mail address. 
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
}
