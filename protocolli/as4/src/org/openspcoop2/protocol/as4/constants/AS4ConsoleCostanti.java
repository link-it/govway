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



package org.openspcoop2.protocol.as4.constants;


/**
 * Classe dove sono fornite le stringhe costanti, definite dalla specifica del protocollo AS4, 
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */

public class AS4ConsoleCostanti {
   
	private static final String AS4_PREFIX_TITLE_LABEL = "eDelivery - ";
	
	
	// Soggetti
	
	public static final String AS4_SOGGETTI_LABEL = AS4_PREFIX_TITLE_LABEL+"Party Info";
	public static final String AS4_SOGGETTI_ID = "as4SoggettiTitleId";
	
	public static final String AS4_SOGGETTO_USER_MESSAGE_PARTY_ID_LABEL = "Id";
	public static final String AS4_SOGGETTO_USER_MESSAGE_PARTY_ID_ID = AS4Costanti.AS4_PROTOCOL_PROPERTIES_USER_MESSAGE_PARTY_ID_BASE;
	
	public static final String AS4_SOGGETTO_USER_MESSAGE_PARTY_TYPE_NAME_LABEL = "Type Name";
	public static final String AS4_SOGGETTO_USER_MESSAGE_PARTY_TYPE_NAME_ID = AS4Costanti.AS4_PROTOCOL_PROPERTIES_USER_MESSAGE_PARTY_ID_TYPE_NAME;
	public static final String AS4_SOGGETTO_USER_MESSAGE_PARTY_TYPE_NAME_DEFAULT_VALUE = "partyTypeUrn";
	
	public static final String AS4_SOGGETTO_USER_MESSAGE_PARTY_TYPE_VALUE_LABEL = "Type Value";
	public static final String AS4_SOGGETTO_USER_MESSAGE_PARTY_TYPE_VALUE_ID = AS4Costanti.AS4_PROTOCOL_PROPERTIES_USER_MESSAGE_PARTY_ID_TYPE_VALUE;
	public static final String AS4_SOGGETTO_USER_MESSAGE_PARTY_TYPE_VALUE_DEFAULT_VALUE = "urn:oasis:names:tc:ebcore:partyid-type:unregistered";
	
	public static final String AS4_SOGGETTO_USER_MESSAGE_PARTY_COMMON_NAME_LABEL = "Common Name";
	public static final String AS4_SOGGETTO_USER_MESSAGE_PARTY_COMMON_NAME_ID = AS4Costanti.AS4_PROTOCOL_PROPERTIES_USER_MESSAGE_PARTY_COMMON_NAME;
	public static final String AS4_SOGGETTO_USER_MESSAGE_PARTY_COMMON_NAME_NOTE = "CN del certificato esposto dal Gateway Domibus associato al Soggetto";
	
	
	// API
	
	public static final String AS4_TITLE_ACCORDO_LABEL = AS4_PREFIX_TITLE_LABEL+"Service Info";
	public static final String AS4_TITLE_ACCORDO_ID = "as4ApiTitleId";
	
	public static final String AS4_ACCORDO_USER_MESSAGE_COLLABORATION_INFO_SERVICE_TYPE_ID_LABEL = "Service Type";
	public static final String AS4_ACCORDO_USER_MESSAGE_COLLABORATION_INFO_SERVICE_TYPE_ID = AS4Costanti.AS4_PROTOCOL_PROPERTIES_USER_MESSAGE_COLLABORATION_INFO_SERVICE_TYPE;
	
	public static final String AS4_ACCORDO_USER_MESSAGE_COLLABORATION_INFO_SERVICE_NAME_ID_LABEL = "Service Name";
	public static final String AS4_ACCORDO_USER_MESSAGE_COLLABORATION_INFO_SERVICE_NAME_ID = AS4Costanti.AS4_PROTOCOL_PROPERTIES_USER_MESSAGE_COLLABORATION_INFO_SERVICE_BASE;

	public static final String AS4_ACCORDO_SERVICE_PAYLOAD_PROFILE_LABEL = "Payload Profile";
	public static final String AS4_ACCORDO_SERVICE_PAYLOAD_PROFILE_ID = AS4Costanti.AS4_PROTOCOL_PROPERTIES_SERVICE_PAYLOAD_PROFILE;

	
	
	// Azioni
	
	public static final String AS4_TITLE_AZIONE_LABEL = AS4_PREFIX_TITLE_LABEL+"Action Info";
	public static final String AS4_TITLE_AZIONE_ID = "as4AzioneTitleId";
	
	public static final String AS4_AZIONE_USER_MESSAGE_COLLABORATION_INFO_ACTION_ID_LABEL = "Action Name";
	public static final String AS4_AZIONE_USER_MESSAGE_COLLABORATION_INFO_ACTION_ID = AS4Costanti.AS4_PROTOCOL_PROPERTIES_USER_MESSAGE_COLLABORATION_INFO_ACTION;
	
	public static final String AS4_AZIONE_ACTION_PAYLOAD_PROFILE_LABEL = "Payload Profile";
	public static final String AS4_AZIONE_ACTION_PAYLOAD_PROFILE_ID = AS4Costanti.AS4_PROTOCOL_PROPERTIES_ACTION_PAYLOAD_PROFILE;
	
	public static final String AS4_AZIONE_ACTION_PAYLOAD_COMPRESS_LABEL = "Payload Compress";
	public static final String AS4_AZIONE_ACTION_PAYLOAD_COMPRESS_ID = AS4Costanti.AS4_PROTOCOL_PROPERTIES_ACTION_COMPRESS_PAYLOAD;
	public static final Boolean AS4_AZIONE_ACTION_PAYLOAD_COMPRESS_DEFAULT = true;
	
	
	
	// Erogazioni
	
	public static final String AS4_TITLE_EROGAZIONE_LABEL = AS4_PREFIX_TITLE_LABEL+"Service Info";
	public static final String AS4_TITLE_EROGAZIONE_ID = "as4ErogazioneTitleId";
	
	public static final String AS4_EROGAZIONE_SECURITY_PROFILE_LABEL = "Security Profile";
	public static final String AS4_EROGAZIONE_SECURITY_PROFILE_ID = AS4Costanti.AS4_PROTOCOL_PROPERTIES_SECURITY_PROFILE;

}





