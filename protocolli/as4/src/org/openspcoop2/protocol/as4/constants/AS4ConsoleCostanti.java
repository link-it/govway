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



package org.openspcoop2.protocol.as4.constants;


/**
 * Classe dove sono fornite le stringhe costanti, definite dalla specifica del protocollo AS4, 
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */

public class AS4ConsoleCostanti {
   
	private static final String AS4_TITLE_LABEL = "eDelivery";
	@SuppressWarnings("unused")
	private static final String AS4_PREFIX_TITLE_LABEL = "eDelivery - ";
	
	
	// Soggetti
	
	public static final String AS4_SOGGETTI_LABEL = AS4_TITLE_LABEL;
	public static final String AS4_SOGGETTI_ID = "as4SoggettiTitleId";
	
	public static final String AS4_SOGGETTI_PARTY_INFO_LABEL = "Party Info";
	public static final String AS4_SOGGETTI_PARTY_INFO_ID = "as4SoggettiPartyInfoSubTitleId";
	
	public static final String AS4_SOGGETTO_USER_MESSAGE_PARTY_ID_LABEL = "Id";
	public static final String AS4_SOGGETTO_USER_MESSAGE_PARTY_ID_ID = AS4Costanti.AS4_PROTOCOL_PROPERTIES_USER_MESSAGE_PARTY_ID_BASE;
	
	public static final String AS4_SOGGETTO_USER_MESSAGE_PARTY_TYPE_NAME_LABEL = "Type Name";
	public static final String AS4_SOGGETTO_USER_MESSAGE_PARTY_TYPE_NAME_ID = AS4Costanti.AS4_PROTOCOL_PROPERTIES_USER_MESSAGE_PARTY_ID_TYPE_NAME;
	public static final String AS4_SOGGETTO_USER_MESSAGE_PARTY_TYPE_NAME_DEFAULT_VALUE = "partyTypeUrn";
	
	public static final String AS4_SOGGETTO_USER_MESSAGE_PARTY_TYPE_VALUE_LABEL = "Type Value";
	public static final String AS4_SOGGETTO_USER_MESSAGE_PARTY_TYPE_VALUE_ID = AS4Costanti.AS4_PROTOCOL_PROPERTIES_USER_MESSAGE_PARTY_ID_TYPE_VALUE;
	public static final String AS4_SOGGETTO_USER_MESSAGE_PARTY_TYPE_VALUE_DEFAULT_VALUE = "urn:oasis:names:tc:ebcore:partyid-type:unregistered";
	
	public static final String AS4_SOGGETTI_PARTY_ENDPOINT_LABEL = "Party Endpoint";
	public static final String AS4_SOGGETTI_PARTY_ENDPOINT_ID = "as4SoggettiPartyEndpointSubTitleId";
	
	public static final String AS4_SOGGETTO_USER_MESSAGE_PARTY_ENDPOINT_LABEL = "URL";
	public static final String AS4_SOGGETTO_USER_MESSAGE_PARTY_ENDPOINT_ID = AS4Costanti.AS4_PROTOCOL_PROPERTIES_USER_MESSAGE_PARTY_ENDPOINT;
	
	public static final String AS4_SOGGETTO_USER_MESSAGE_PARTY_COMMON_NAME_LABEL = "Common Name";
	public static final String AS4_SOGGETTO_USER_MESSAGE_PARTY_COMMON_NAME_ID = AS4Costanti.AS4_PROTOCOL_PROPERTIES_USER_MESSAGE_PARTY_COMMON_NAME;
	public static final String AS4_SOGGETTO_USER_MESSAGE_PARTY_COMMON_NAME_NOTE = "CN del certificato esposto dal Gateway Domibus associato al Soggetto";
	
	
	// API
	
	public static final String AS4_TITLE_ACCORDO_LABEL = AS4_TITLE_LABEL;
	public static final String AS4_TITLE_ACCORDO_ID = "as4ApiTitleId";
	
	public static final String AS4_TITLE_ACCORDO_SERVICE_LABEL = "Service Info";
	public static final String AS4_TITLE_ACCORDO_SERVICE_ID = "as4ApiServiceSubTitleId";
	
	public static final String AS4_ACCORDO_USER_MESSAGE_COLLABORATION_INFO_SERVICE_TYPE_ID_LABEL = "Type";
	public static final String AS4_ACCORDO_USER_MESSAGE_COLLABORATION_INFO_SERVICE_TYPE_ID = AS4Costanti.AS4_PROTOCOL_PROPERTIES_USER_MESSAGE_COLLABORATION_INFO_SERVICE_TYPE;
	
	public static final String AS4_ACCORDO_USER_MESSAGE_COLLABORATION_INFO_SERVICE_NAME_ID_LABEL = "Name";
	public static final String AS4_ACCORDO_USER_MESSAGE_COLLABORATION_INFO_SERVICE_NAME_ID = AS4Costanti.AS4_PROTOCOL_PROPERTIES_USER_MESSAGE_COLLABORATION_INFO_SERVICE_BASE;

	public static final String AS4_TITLE_ACCORDO_PAYLOAD_LABEL = "Payload";
	public static final String AS4_TITLE_ACCORDO_PAYLOAD_PROFILES_LABEL = "Payload Profiles";
	public static final String AS4_TITLE_ACCORDO_PAYLOAD_ID = "as4ApiPayloadSubTitleId";
	
	public static final String AS4_ACCORDO_SERVICE_PAYLOAD_PROFILE_LABEL = "API Profiles";
	public static final String AS4_ACCORDO_SERVICE_PAYLOAD_PROFILE_ID = AS4Costanti.AS4_PROTOCOL_PROPERTIES_SERVICE_PAYLOAD_PROFILE;
	
	public static final String AS4_ACCORDO_SERVICE_PAYLOAD_PROFILE_DEFAULT_LABEL = " Default Profiles";
	public static final String AS4_ACCORDO_SERVICE_PAYLOAD_PROFILE_DEFAULT_ID = "ebmsServicePayloadProfileDefault"; // usato solo in grafica per consultazione
	
	public static final String AS4_TITLE_ACCORDO_PROPERTIES_LABEL = "Properties";
	public static final String AS4_TITLE_ACCORDO_PROPERTIES_ID = "as4ApiPropertiesSubTitleId";
	
	public static final String AS4_ACCORDO_SERVICE_PROPERTIES_LABEL = "API Properties";
	public static final String AS4_ACCORDO_SERVICE_PROPERTIES_ID = AS4Costanti.AS4_PROTOCOL_PROPERTIES_SERVICE_PROPERTIES;
	
	public static final String AS4_ACCORDO_SERVICE_PROPERTIES_DEFAULT_LABEL = " Default Properties";
	public static final String AS4_ACCORDO_SERVICE_PROPERTIES_DEFAULT_ID = "ebmsServicePropertiesDefault"; // usato solo in grafica per consultazione
	
	
	// Azioni
	
	public static final String AS4_TITLE_AZIONE_LABEL = AS4_TITLE_LABEL;
	public static final String AS4_TITLE_AZIONE_ID = "as4AzioneTitleId";
	
	public static final String AS4_TITLE_AZIONE_ACTION_LABEL = "Action Info";
	public static final String AS4_TITLE_AZIONE_ACTION_ID = "as4AzioneActionSubTitleId";
	
	public static final String AS4_AZIONE_USER_MESSAGE_COLLABORATION_INFO_ACTION_ID_LABEL = "Name";
	public static final String AS4_AZIONE_USER_MESSAGE_COLLABORATION_INFO_ACTION_ID = AS4Costanti.AS4_PROTOCOL_PROPERTIES_USER_MESSAGE_COLLABORATION_INFO_ACTION;
	
	public static final String AS4_AZIONE_USER_MESSAGE_COLLABORATION_INFO_ACTION_BINDING_LABEL = "Binding";
	public static final String AS4_AZIONE_USER_MESSAGE_COLLABORATION_INFO_ACTION_BINDING_ID = AS4Costanti.AS4_PROTOCOL_PROPERTIES_USER_MESSAGE_COLLABORATION_INFO_ACTION_BINDING;
	public static final String AS4_AZIONE_USER_MESSAGE_COLLABORATION_INFO_ACTION_BINDING_PUSH_LABEL = "oneway";
	public static final String AS4_AZIONE_USER_MESSAGE_COLLABORATION_INFO_ACTION_BINDING_PUSH_VALUE = "push";
	public static final String AS4_AZIONE_USER_MESSAGE_COLLABORATION_INFO_ACTION_BINDING_PUSH_VALUE_MEP = "oneway"; // usato in template
	public static final String AS4_AZIONE_USER_MESSAGE_COLLABORATION_INFO_ACTION_BINDING_PUSH_AND_PUSH_LABEL = "twoway";
	public static final String AS4_AZIONE_USER_MESSAGE_COLLABORATION_INFO_ACTION_BINDING_PUSH_AND_PUSH_VALUE = "pushAndPush";
	public static final String AS4_AZIONE_USER_MESSAGE_COLLABORATION_INFO_ACTION_BINDING_PUSH_AND_PUSH_VALUE_MEP = "twoway"; // usato in template
	public static final String AS4_AZIONE_USER_MESSAGE_COLLABORATION_INFO_ACTION_BINDING_DEFAULT_VALUE = AS4_AZIONE_USER_MESSAGE_COLLABORATION_INFO_ACTION_BINDING_PUSH_VALUE;
		
	public static final String AS4_TITLE_AZIONE_PAYLOAD_LABEL = "Payload";
	public static final String AS4_TITLE_AZIONE_PAYLOAD_ID = "as4AzionePayloadSubTitleId";
	
	public static final String AS4_AZIONE_ACTION_PAYLOAD_PROFILE_LABEL = "Profile";
	public static final String AS4_AZIONE_ACTION_PAYLOAD_PROFILE_ID = AS4Costanti.AS4_PROTOCOL_PROPERTIES_ACTION_PAYLOAD_PROFILE;
	
	public static final String AS4_AZIONE_ACTION_PAYLOAD_COMPRESS_LABEL = "Compress";
	public static final String AS4_AZIONE_ACTION_PAYLOAD_COMPRESS_ID = AS4Costanti.AS4_PROTOCOL_PROPERTIES_ACTION_COMPRESS_PAYLOAD;
	public static final Boolean AS4_AZIONE_ACTION_PAYLOAD_COMPRESS_DEFAULT = true;
	
	public static final String AS4_TITLE_AZIONE_PROPERTIES_LABEL = "Properties";
	public static final String AS4_TITLE_AZIONE_PROPERTIES_ID = "as4AzionePropertiesSubTitleId";
	
	public static final String AS4_AZIONE_ACTION_PROPERTY_SET_LABEL = "PropertySet";
	public static final String AS4_AZIONE_ACTION_PROPERTY_SET_ID = AS4Costanti.AS4_PROTOCOL_PROPERTIES_ACTION_PROPERTY_SET;
	
	
	
	// Erogazioni
	
	public static final String AS4_TITLE_EROGAZIONE_LABEL = AS4_TITLE_LABEL;
	public static final String AS4_TITLE_EROGAZIONE_ID = "as4ErogazioneTitleId";
	
	public static final String AS4_TITLE_EROGAZIONE_SECURITY_LABEL = "Security";
	public static final String AS4_TITLE_EROGAZIONE_SECURITY_ID = "as4ErogazioneSecuritySubTitleId";
	
	public static final String AS4_EROGAZIONE_SECURITY_PROFILE_LABEL = "Profile";
	public static final String AS4_EROGAZIONE_SECURITY_PROFILE_ID = AS4Costanti.AS4_PROTOCOL_PROPERTIES_SECURITY_PROFILE;
	
	public static final String AS4_TITLE_EROGAZIONE_RELIABILITY_LABEL = "Reliability";
	public static final String AS4_TITLE_EROGAZIONE_RELIABILITY_ID = "as4ErogazioneReliabilitySubTitleId";
	
	public static final String AS4_EROGAZIONE_RELIABILITY_NON_REPUDIATION_LABEL = "Non Repudiation";
	public static final String AS4_EROGAZIONE_RELIABILITY_NON_REPUDIATION_ID = AS4Costanti.AS4_PROTOCOL_PROPERTIES_RELIABILITY_NON_REPUDIATION;
	public static final Boolean AS4_EROGAZIONE_RELIABILITY_NON_REPUDIATION_DEFAULT = true;
	
	public static final String AS4_EROGAZIONE_RELIABILITY_REPLY_PATTERN_LABEL = "Reply Pattern";
	public static final String AS4_EROGAZIONE_RELIABILITY_REPLY_PATTERN_ID = AS4Costanti.AS4_PROTOCOL_PROPERTIES_RELIABILITY_REPLY_PATTERN;
	public static final String AS4_EROGAZIONE_RELIABILITY_REPLY_PATTERN_RESPONSE_LABEL = "Response";
	public static final String AS4_EROGAZIONE_RELIABILITY_REPLY_PATTERN_RESPONSE_VALUE = "response";
	public static final String AS4_EROGAZIONE_RELIABILITY_REPLY_PATTERN_CALLBACK_LABEL = "Callback";
	public static final String AS4_EROGAZIONE_RELIABILITY_REPLY_PATTERN_CALLBACK_VALUE = "callback";
	public static final String AS4_EROGAZIONE_RELIABILITY_REPLY_PATTERN_DEFAULT = AS4_EROGAZIONE_RELIABILITY_REPLY_PATTERN_RESPONSE_VALUE;

}





