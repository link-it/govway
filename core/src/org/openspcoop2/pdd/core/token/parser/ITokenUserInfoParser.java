package org.openspcoop2.pdd.core.token.parser;

import java.util.Map;

public interface ITokenUserInfoParser {

	public void init(String raw, Map<String,String> claims);
		
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

