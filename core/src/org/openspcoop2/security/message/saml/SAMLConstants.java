/*
 * OpenSPCoop - Customizable API Gateway 
 * http://www.openspcoop2.org
 * 
 * Copyright (c) 2005-2017 Link.it srl (http://link.it). 
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
 * @author $Author: apoli $
 * @version $Rev: 12564 $, $Date: 2017-01-11 14:31:31 +0100 (Wed, 11 Jan 2017) $
 */
public class SAMLConstants {

	public static final String SAML_20_NAMESPACE = "urn:oasis:names:tc:SAML:2.0:assertion";
	
	public static final String XPATH_SAML_20_ASSERTION = "//{"+SAML_20_NAMESPACE+"}:Assertion";
	public static final String XPATH_SAML_20_ASSERTION_SUBJECT_NAMEID = "//{"+SAML_20_NAMESPACE+"}:Assertion/{"+SAML_20_NAMESPACE+"}:Subject/{"+SAML_20_NAMESPACE+"}:NameID/text()";
	public static final String XPATH_SAML_20_ASSERTION_ISSUER = "//{"+SAML_20_NAMESPACE+"}:Assertion/{"+SAML_20_NAMESPACE+"}:Issuer/text()";	
	public static final String XPATH_SAML_20_ASSERTION_ATTRIBUTESTATEMENT_ATTRIBUTE = "//{"+SAML_20_NAMESPACE+"}:Assertion/{"+SAML_20_NAMESPACE+"}:AttributeStatement/{"+SAML_20_NAMESPACE+"}:Attribute";

	public static final String SAML_11_NAMESPACE = "urn:oasis:names:tc:SAML:1.0:assertion";
	
	public static final String XPATH_SAML_11_ASSERTION = "//{"+SAML_11_NAMESPACE+"}:Assertion";
	public static final String XPATH_SAML_11_ASSERTION_AUTHENTICATIONSTATEMENT_SUBJECT_NAMEID = "//{"+SAML_11_NAMESPACE+"}:Assertion/{"+SAML_11_NAMESPACE+"}:AuthenticationStatement/{"+SAML_11_NAMESPACE+"}:Subject/{"+SAML_11_NAMESPACE+"}:NameIdentifier/text()";
	public static final String XPATH_SAML_11_ASSERTION_ATTRIBUTESTATEMENT_SUBJECT_NAMEID = "//{"+SAML_11_NAMESPACE+"}:Assertion/{"+SAML_11_NAMESPACE+"}:AttributeStatement/{"+SAML_11_NAMESPACE+"}:Subject/{"+SAML_11_NAMESPACE+"}:NameIdentifier/text()";
	public static final String XPATH_SAML_11_ASSERTION_ISSUER = "//{"+SAML_11_NAMESPACE+"}:Assertion/@Issuer";	
	public static final String XPATH_SAML_11_ASSERTION_ATTRIBUTESTATEMENT_ATTRIBUTE = "//{"+SAML_11_NAMESPACE+"}:Assertion/{"+SAML_11_NAMESPACE+"}:AttributeStatement/{"+SAML_11_NAMESPACE+"}:Attribute";

}
