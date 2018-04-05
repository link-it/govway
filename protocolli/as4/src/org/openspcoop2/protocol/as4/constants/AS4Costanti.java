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

import org.openspcoop2.protocol.sdk.archive.ArchiveMode;
import org.openspcoop2.protocol.sdk.archive.ArchiveModeType;

/**
 * Classe dove sono fornite le stringhe costanti, definite dalla specifica del protocollo AS4, 
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */

public class AS4Costanti {
   
	public final static String OPENSPCOOP2_LOCAL_HOME = "OPENSPCOOP2_HOME";
	
	public final static String PROTOCOL_NAME = "as4";
	
    public final static String AS4_PROPERTIES_LOCAL_PATH = "as4_local.properties";
    public final static String AS4_PROPERTIES = "AS4_PROPERTIES";
    
    
    public final static String AS4_PROTOCOL_PROPERTIES_USER_MESSAGE_PARTY_ID_BASE = "ebmsUserMessagePartyId";
    public final static String AS4_PROTOCOL_PROPERTIES_USER_MESSAGE_PARTY_ID_TYPE_NAME = "ebmsUserMessagePartyIdTypeName";
	public static final String AS4_PROTOCOL_PROPERTIES_USER_MESSAGE_PARTY_ID_TYPE_VALUE = "ebmsUserMessagePartyIdTypeValue";
	public final static String AS4_PROTOCOL_PROPERTIES_USER_MESSAGE_PARTY_ENDPOINT = "ebmsUserMessagePartyEndpoint";
	public final static String AS4_PROTOCOL_PROPERTIES_USER_MESSAGE_PARTY_COMMON_NAME = "ebmsUserMessagePartyCommonName";
    
    public final static String AS4_PROTOCOL_PROPERTIES_USER_MESSAGE_COLLABORATION_INFO_SERVICE_BASE = "ebmsUserMessageCollaborationInfoService";
    public final static String AS4_PROTOCOL_PROPERTIES_USER_MESSAGE_COLLABORATION_INFO_SERVICE_TYPE = "ebmsUserMessageCollaborationInfoServiceType";
    public final static String AS4_PROTOCOL_PROPERTIES_USER_MESSAGE_COLLABORATION_INFO_ACTION = "ebmsUserMessageCollaborationInfoAction";
    
    public final static String AS4_PROTOCOL_PROPERTIES_SECURITY_PROFILE = "ebmsSecurityProfile";
    
    public final static String AS4_PROTOCOL_PROPERTIES_SERVICE_PAYLOAD_PROFILE = "ebmsServicePayloadProfile";
    public static final String AS4_PROTOCOL_PROPERTIES_ACTION_PAYLOAD_PROFILE = "ebmsActionPayloadProfile";
    public static final String AS4_PROTOCOL_PROPERTIES_ACTION_COMPRESS_PAYLOAD = "ebmsActionCompressPayload";
    
    public final static String AS4_PROTOCOL_PROPERTIES_SERVICE_PROPERTIES = "ebmsServiceProperties";
    public static final String AS4_PROTOCOL_PROPERTIES_ACTION_PROPERTY_SET = "ebmsActionPropertySet";
    
    
    public final static String AS4_USER_MESSAGE_FROM_ROLE_INITIATOR = "http://docs.oasis-open.org/ebxml-msg/ebms/v3.0/ns/core/200704/initiator";
    public final static String AS4_USER_MESSAGE_FROM_ROLE_RESPONDER = "http://docs.oasis-open.org/ebxml-msg/ebms/v3.0/ns/core/200704/responder";
    
    public final static String AS4_USER_MESSAGE_PAYLOAD_INFO_PROPERTIES_MIME_TYPE = "MimeType";
    
    public static final String AS4_NAMESPACE_CID_MESSAGGIO = "http://www.openspcoop2.org/protocol/as4";
        
	/** Archive mode */
    public static final ArchiveMode DOMIBUS_MODE = new ArchiveMode("domibus");	
	public static final ArchiveMode EXPORT_MODE_DOMIBUS_FROM_SOGGETTI = new ArchiveMode("domibus");
	public static final ArchiveMode EXPORT_MODE_DOMIBUS_FROM_CONFIG = new ArchiveMode("domibus-pmode"); // necessita di utilizzare un nome differente

	/** Archive mode type */
	public static final ArchiveModeType PMODE_ARCHIVE_MODE_TYPE_XML = new ArchiveModeType("xml");
	public static final ArchiveModeType PMODE_ARCHIVE_MODE_TYPE_ZIP = new ArchiveModeType("zip");
	/** Archive extension */
	public static final String PMODE_ARCHIVE_EXT_XML = "xml";
	public static final String PMODE_ARCHIVE_EXT_ZIP = "zip";
	public static final String PMODE_ARCHIVE_ROOT_DIR = "pmodes";

	
	
	public static final String JMS_FROM_PARTY_ID = "fromPartyId";
	public static final String JMS_FROM_PARTY_TYPE = "fromPartyType";
	public static final String JMS_FROM_ROLE = "fromRole";
	
	public static final String JMS_TO_PARTY_ID = "toPartyId";
	public static final String JMS_TO_PARTY_TYPE = "toPartyType";
	public static final String JMS_TO_ROLE = "toRole";
	
	public static final String JMS_SERVICE = "service";
	public static final String JMS_SERVICE_TYPE = "serviceType";
	public static final String JMS_ACTION = "action";
	
	public static final String JMS_CONVERSATION_ID = "conversationId";
	
	public static final String JMS_MESSAGE_ID = "messageId";
	public static final String JMS_REF_TO_MESSAGE_ID = "refToMessageId";
	
	public static final String JMS_AGREEMENT_REF = "agreementRef";
	
	public static final String JMS_PROTOCOL = "protocol";
	
	public static final String JMS_MESSAGE_TYPE = "messageType";
	
	public static final String JMS_PAYLOADS_NUMBER = "totalNumberOfPayloads";
	public static final String JMS_PAYLOAD_PREFIX = "payload_";
	public static final String JMS_PAYLOAD_MIME_CONTENT_ID_SUFFIX = "_mimeContentId";
	public static final String JMS_PAYLOAD_MIME_TYPE_SUFFIX = "_mimeType";
	
	public static final String JMS_NOTIFICA_MESSAGE_ID = "MESSAGE_ID";
	public static final String JMS_NOTIFICA_NOTIFICATION_TYPE = "NOTIFICATION_TYPE";
	
	
	public final static String AS4_CONTEXT_USER_MESSAGE = "AS4_CONTEXT_USER_MESSAGE";
	public final static String AS4_CONTEXT_CONTENT = "AS4_CONTEXT_CONTENT";

	
	/** Properties per oggetto Busta */
	
	public final static String AS4_BUSTA_PREFIX = "ebmsUserMessage";
	
    public final static String AS4_BUSTA_MITTENTE_PARTY_ID_BASE = AS4_BUSTA_PREFIX+"FromPartyId";
	public static final String AS4_BUSTA_MITTENTE_PARTY_ID_TYPE = AS4_BUSTA_PREFIX+"FromPartyIdType";
	
    public final static String AS4_BUSTA_DESTINATARIO_PARTY_ID_BASE = AS4_BUSTA_PREFIX+"ToPartyId";
	public static final String AS4_BUSTA_DESTINATARIO_PARTY_ID_TYPE = AS4_BUSTA_PREFIX+"ToPartyIdType";
	
	public final static String AS4_BUSTA_SERVIZIO_COLLABORATION_INFO_SERVICE_BASE = AS4_BUSTA_PREFIX+"CollaborationInfoService";
	public static final String AS4_BUSTA_SERVIZIO_COLLABORATION_INFO_SERVICE_TYPE = AS4_BUSTA_PREFIX+"CollaborationInfoServiceType";
	public final static String AS4_BUSTA_SERVIZIO_COLLABORATION_INFO_ACTION = AS4_BUSTA_PREFIX+"CollaborationInfoAction";
	public final static String AS4_BUSTA_SERVIZIO_COLLABORATION_INFO_CONVERSATION_ID = AS4_BUSTA_PREFIX+"CollaborationInfoConversationId";
	
	public final static String AS4_BUSTA_SERVIZIO_MESSAGE_INFO_ID = AS4_BUSTA_PREFIX+"Id";
	public final static String AS4_BUSTA_SERVIZIO_MESSAGE_INFO_REF_TO_MESSAGE_ID = AS4_BUSTA_PREFIX+"RefToMessageId";
	public final static String AS4_BUSTA_SERVIZIO_MESSAGE_INFO_SEND_STATUS = AS4_BUSTA_PREFIX+"SendStatus";
	
	public final static String AS4_BUSTA_SERVIZIO_COLLABORATION_MESSAGE_PROPERTY_PREFIX = AS4_BUSTA_PREFIX+"Property_";
}





