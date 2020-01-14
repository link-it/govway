/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2020 Link.it srl (https://link.it). 
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

package org.openspcoop2.security.message.saml;



/**
 * SAMLXPathConstants
 * 	
 * @author Andrea Poli <apoli@link.it>
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class SAMLConstants {

	public static final String SAML_20_NAMESPACE = org.openspcoop2.message.constants.Costanti.SAML_20_NAMESPACE;
	public static final String SAML_20_ASSERTION_ID = org.openspcoop2.message.constants.Costanti.SAML_20_ASSERTION_ID;
	
	public static final String XPATH_SAML_20_ASSERTION = org.openspcoop2.message.constants.Costanti.XPATH_SAML_20_ASSERTION;
	public static final String XPATH_SAML_20_ASSERTION_SUBJECT_NAMEID = "//{"+SAML_20_NAMESPACE+"}:Assertion/{"+SAML_20_NAMESPACE+"}:Subject/{"+SAML_20_NAMESPACE+"}:NameID/text()";
	public static final String XPATH_SAML_20_ASSERTION_SUBJECT_CONFIRMATION_METHOD = "//{"+SAML_20_NAMESPACE+"}:Assertion/{"+SAML_20_NAMESPACE+"}:Subject/{"+SAML_20_NAMESPACE+"}:SubjectConfirmation/@Method";
	public static final String XPATH_SAML_20_ASSERTION_ISSUER = "//{"+SAML_20_NAMESPACE+"}:Assertion/{"+SAML_20_NAMESPACE+"}:Issuer/text()";	
	public static final String XPATH_SAML_20_ASSERTION_ATTRIBUTESTATEMENT_ATTRIBUTE = "//{"+SAML_20_NAMESPACE+"}:Assertion/{"+SAML_20_NAMESPACE+"}:AttributeStatement/{"+SAML_20_NAMESPACE+"}:Attribute";

	public static final String SAML_11_NAMESPACE = org.openspcoop2.message.constants.Costanti.SAML_11_NAMESPACE;
	public static final String SAML_11_ASSERTION_ID = org.openspcoop2.message.constants.Costanti.SAML_11_ASSERTION_ID;
	
	public static final String XPATH_SAML_11_ASSERTION = org.openspcoop2.message.constants.Costanti.XPATH_SAML_11_ASSERTION;
	public static final String XPATH_SAML_11_ASSERTION_AUTHENTICATIONSTATEMENT_SUBJECT_NAMEID = "//{"+SAML_11_NAMESPACE+"}:Assertion/{"+SAML_11_NAMESPACE+"}:AuthenticationStatement/{"+SAML_11_NAMESPACE+"}:Subject/{"+SAML_11_NAMESPACE+"}:NameIdentifier/text()";
	public static final String XPATH_SAML_11_ASSERTION_ATTRIBUTESTATEMENT_SUBJECT_NAMEID = "//{"+SAML_11_NAMESPACE+"}:Assertion/{"+SAML_11_NAMESPACE+"}:AttributeStatement/{"+SAML_11_NAMESPACE+"}:Subject/{"+SAML_11_NAMESPACE+"}:NameIdentifier/text()";
	public static final String XPATH_SAML_11_ASSERTION_SUBJECT_CONFIRMATION_METHOD = "//{"+SAML_11_NAMESPACE+"}:Assertion/{"+SAML_11_NAMESPACE+"}:AuthenticationStatement/{"+SAML_11_NAMESPACE+"}:Subject/{"+SAML_11_NAMESPACE+"}:SubjectConfirmation/{"+SAML_11_NAMESPACE+"}:ConfirmationMethod/text()";
	public static final String XPATH_SAML_11_ASSERTION_ISSUER = "//{"+SAML_11_NAMESPACE+"}:Assertion/@Issuer";	
	public static final String XPATH_SAML_11_ASSERTION_ATTRIBUTESTATEMENT_ATTRIBUTE = "//{"+SAML_11_NAMESPACE+"}:Assertion/{"+SAML_11_NAMESPACE+"}:AttributeStatement/{"+SAML_11_NAMESPACE+"}:Attribute";

}
