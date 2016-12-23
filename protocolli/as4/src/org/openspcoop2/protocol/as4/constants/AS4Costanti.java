/*
 * OpenSPCoop - Customizable API Gateway 
 * http://www.openspcoop2.org
 * 
 * Copyright (c) 2005-2016 Link.it srl (http://link.it). 
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
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

public class AS4Costanti {
   
	public final static String OPENSPCOOP2_LOCAL_HOME = "OPENSPCOOP2_HOME";
	
    public final static String AS4_PROPERTIES_LOCAL_PATH = "as4_local.properties";
    public final static String AS4_PROPERTIES = "AS4_PROPERTIES";
    
    
    public final static String AS4_PROTOCOL_PROPERTIES_USER_MESSAGE_FROM_PARTY_ID_BASE = "ebmsUserMessageFromPartyId";
    public final static String AS4_PROTOCOL_PROPERTIES_USER_MESSAGE_FROM_PARTY_ID_TYPE = "ebmsUserMessageFromPartyIdType";
    
    public final static String AS4_PROTOCOL_PROPERTIES_USER_MESSAGE_TO_PARTY_ID_BASE = "ebmsUserMessageToPartyId";
    public final static String AS4_PROTOCOL_PROPERTIES_USER_MESSAGE_TO_PARTY_ID_TYPE = "ebmsUserMessageToPartyIdType";
    
    public final static String AS4_PROTOCOL_PROPERTIES_USER_MESSAGE_COLLABORATION_INFO_SERVICE_BASE = "ebmsUserMessageCollaborationInfoService";
    public final static String AS4_PROTOCOL_PROPERTIES_USER_MESSAGE_COLLABORATION_INFO_SERVICE_TYPE = "ebmsUserMessageCollaborationInfoServiceType";

    public final static String AS4_PROTOCOL_PROPERTIES_USER_MESSAGE_COLLABORATION_INFO_ACTION = "ebmsUserMessageCollaborationInfoAction";
    
    
    public final static String AS4_USER_MESSAGE_FROM_ROLE_INITIATOR = "http://docs.oasis-open.org/ebxml-msg/ebms/v3.0/ns/core/200704/initiator";
    public final static String AS4_USER_MESSAGE_FROM_ROLE_RESPONDER = "http://docs.oasis-open.org/ebxml-msg/ebms/v3.0/ns/core/200704/responder";
    
    public final static String AS4_USER_MESSAGE_PAYLOAD_INFO_PROPERTIES_MIME_TYPE = "MimeType";
    
    public static final String AS4_NAMESPACE_CID_MESSAGGIO = "http://www.openspcoop2.org/protocol/as4";
}





