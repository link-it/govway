/*
 * OpenSPCoop - Customizable API Gateway 
 * http://www.openspcoop2.org
 * 
 * Copyright (c) 2005-2018 Link.it srl (http://link.it). 
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

package org.openspcoop2.pdd.core.autorizzazione;

/**
 * XACMLCostanti
 *
 * @author Andrea Poli <apoli@link.it>
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class XACMLCostanti {

	public static final String _XACML_REQUEST_ATTRIBUTE_ID = "org:openspcoop";

	public static final String _XACML_REQUEST_ACTION_ATTRIBUTE_ID = _XACML_REQUEST_ATTRIBUTE_ID+":action";
	public static final String XACML_REQUEST_ACTION_PROVIDER_ATTRIBUTE_ID = _XACML_REQUEST_ACTION_ATTRIBUTE_ID+":provider";
	public static final String XACML_REQUEST_ACTION_SERVICE_ATTRIBUTE_ID = _XACML_REQUEST_ACTION_ATTRIBUTE_ID+":service";
	public static final String XACML_REQUEST_ACTION_ACTION_ATTRIBUTE_ID = _XACML_REQUEST_ACTION_ATTRIBUTE_ID+":action";
	public static final String XACML_REQUEST_ACTION_URL_ATTRIBUTE_ID = _XACML_REQUEST_ACTION_ATTRIBUTE_ID+":url";
	public static final String XACML_REQUEST_ACTION_URL_PARAMETER_ATTRIBUTE_ID = _XACML_REQUEST_ACTION_ATTRIBUTE_ID+":url:parameter:";
	public static final String XACML_REQUEST_ACTION_TRANSPORT_HEADER_ATTRIBUTE_ID = _XACML_REQUEST_ACTION_ATTRIBUTE_ID+":transport:header:";
	public static final String XACML_REQUEST_ACTION_SOAP_ACTION_ATTRIBUTE_ID = _XACML_REQUEST_ACTION_ATTRIBUTE_ID+":soapAction";
	public static final String XACML_REQUEST_ACTION_PDD_SERVICE_ATTRIBUTE_ID = _XACML_REQUEST_ACTION_ATTRIBUTE_ID+":pddService";
	public static final String XACML_REQUEST_ACTION_PROTOCOL_ATTRIBUTE_ID = _XACML_REQUEST_ACTION_ATTRIBUTE_ID+":protocol";
	public static final String XACML_REQUEST_ACTION_TOKEN_ATTRIBUTE_PREFIX = _XACML_REQUEST_ACTION_ATTRIBUTE_ID+":token";
	public static final String XACML_REQUEST_ACTION_TOKEN_AUDIENCE_ATTRIBUTE_ID = XACML_REQUEST_ACTION_TOKEN_ATTRIBUTE_PREFIX+":audience";
	public static final String XACML_REQUEST_ACTION_TOKEN_SCOPE_ATTRIBUTE_ID = XACML_REQUEST_ACTION_TOKEN_ATTRIBUTE_PREFIX+":scope";
	public static final String XACML_REQUEST_ACTION_TOKEN_JWT_CLAIMS_PREFIX = _XACML_REQUEST_ACTION_ATTRIBUTE_ID+":jwt:claim:";
	public static final String XACML_REQUEST_ACTION_TOKEN_INTROSPECTION_CLAIMS_PREFIX = _XACML_REQUEST_ACTION_ATTRIBUTE_ID+":introspection:claim:";
	
	public static final String _XACML_REQUEST_SUBJECT_ATTRIBUTE_ID = _XACML_REQUEST_ATTRIBUTE_ID+":subject";
	public static final String XACML_REQUEST_SUBJECT_ORGANIZATION_ATTRIBUTE_ID = _XACML_REQUEST_SUBJECT_ATTRIBUTE_ID+":organization";
	public static final String XACML_REQUEST_SUBJECT_CLIENT_ATTRIBUTE_ID = _XACML_REQUEST_SUBJECT_ATTRIBUTE_ID+":client";
	public static final String XACML_REQUEST_SUBJECT_CREDENTIAL_ATTRIBUTE_ID = _XACML_REQUEST_SUBJECT_ATTRIBUTE_ID+":credential";
	public static final String XACML_REQUEST_SUBJECT_ROLE_ATTRIBUTE_ID = _XACML_REQUEST_SUBJECT_ATTRIBUTE_ID+":role";
	public static final String XACML_REQUEST_SUBJECT_TOKEN_ATTRIBUTE_PREFIX = _XACML_REQUEST_SUBJECT_ATTRIBUTE_ID+":token";
	public static final String XACML_REQUEST_SUBJECT_TOKEN_USER_INFO_PREFIX = XACML_REQUEST_SUBJECT_TOKEN_ATTRIBUTE_PREFIX+":userInfo";
	public static final String XACML_REQUEST_SUBJECT_USER_INFO_FULL_NAME_ATTRIBUTE_ID = XACML_REQUEST_SUBJECT_TOKEN_USER_INFO_PREFIX+":fullName";
	public static final String XACML_REQUEST_SUBJECT_USER_INFO_FIRST_NAME_ATTRIBUTE_ID = XACML_REQUEST_SUBJECT_TOKEN_USER_INFO_PREFIX+":firstName";
	public static final String XACML_REQUEST_SUBJECT_USER_INFO_MIDDLE_NAME_ATTRIBUTE_ID = XACML_REQUEST_SUBJECT_TOKEN_USER_INFO_PREFIX+":middleName";
	public static final String XACML_REQUEST_SUBJECT_USER_INFO_FAMILY_NAME_ATTRIBUTE_ID = XACML_REQUEST_SUBJECT_TOKEN_USER_INFO_PREFIX+":familyName";
	public static final String XACML_REQUEST_SUBJECT_USER_INFO_EMAIL_NAME_ATTRIBUTE_ID = XACML_REQUEST_SUBJECT_TOKEN_USER_INFO_PREFIX+":eMail";
	public static final String XACML_REQUEST_SUBJECT_TOKEN_ISSUER_ATTRIBUTE_ID = XACML_REQUEST_SUBJECT_TOKEN_ATTRIBUTE_PREFIX+":issuer";
	public static final String XACML_REQUEST_SUBJECT_TOKEN_SUBJECT_ATTRIBUTE_ID = XACML_REQUEST_SUBJECT_TOKEN_ATTRIBUTE_PREFIX+":subject";
	public static final String XACML_REQUEST_SUBJECT_TOKEN_USERNAME_ATTRIBUTE_ID = XACML_REQUEST_SUBJECT_TOKEN_ATTRIBUTE_PREFIX+":username";
	public static final String XACML_REQUEST_SUBJECT_TOKEN_CLIENT_ID_ATTRIBUTE_ID = XACML_REQUEST_SUBJECT_TOKEN_ATTRIBUTE_PREFIX+":clientId";
	public static final String XACML_REQUEST_SUBJECT_TOKEN_USERINFO_CLAIMS_PREFIX = _XACML_REQUEST_SUBJECT_ATTRIBUTE_ID+":userInfo:claim:";
	
}
