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



package org.openspcoop2.protocol.as4.constants;

import org.openspcoop2.protocol.sdk.archive.ArchiveMode;
import org.openspcoop2.protocol.sdk.archive.ArchiveModeType;
import org.openspcoop2.protocol.sdk.archive.ExportMode;

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
	public final static String AS4_PROTOCOL_PROPERTIES_USER_MESSAGE_PARTY_COMMON_NAME = "ebmsUserMessagePartyCommonName";
    
    public final static String AS4_PROTOCOL_PROPERTIES_USER_MESSAGE_COLLABORATION_INFO_SERVICE_BASE = "ebmsUserMessageCollaborationInfoService";
    public final static String AS4_PROTOCOL_PROPERTIES_USER_MESSAGE_COLLABORATION_INFO_SERVICE_TYPE = "ebmsUserMessageCollaborationInfoServiceType";
    
    public final static String AS4_PROTOCOL_PROPERTIES_SECURITY_PROFILE = "ebmsSecurityProfile";
    public final static String AS4_PROTOCOL_PROPERTIES_SERVICE_PAYLOAD_PROFILE = "ebmsServicePayloadProfile";
    public static final String AS4_PROTOCOL_PROPERTIES_ACTION_PAYLOAD_PROFILE = "ebmsActionPayloadProfile";
    public static final String AS4_PROTOCOL_PROPERTIES_ACTION_COMPRESS_PAYLOAD = "ebmsActionCompressPayload";
    
    
    public final static String AS4_USER_MESSAGE_FROM_ROLE_INITIATOR = "http://docs.oasis-open.org/ebxml-msg/ebms/v3.0/ns/core/200704/initiator";
    public final static String AS4_USER_MESSAGE_FROM_ROLE_RESPONDER = "http://docs.oasis-open.org/ebxml-msg/ebms/v3.0/ns/core/200704/responder";
    
    public final static String AS4_USER_MESSAGE_PAYLOAD_INFO_PROPERTIES_MIME_TYPE = "MimeType";
    
    public static final String AS4_NAMESPACE_CID_MESSAGGIO = "http://www.openspcoop2.org/protocol/as4";
    
    public final static String AS4_PROTOCOL_PROPERTIES_SEND_RESPONSE_ID = "domibus.messageID";
    public final static String AS4_PROTOCOL_PROPERTIES_SEND_RESPONSE_STATUS = "domibus.status";
    
	/** Archive mode */
	public static final ArchiveMode PMODE_ARCHIVE_MODE_SINGLE_XML = new ArchiveMode("domibus-pmode (single xml configuration)");
	public static final ArchiveMode PMODE_ARCHIVE_MODE_MULTIPLE_ZIP = new ArchiveMode("domibus-pmode (archive zip)");
	public static final ExportMode PMODE_EXPORT_ARCHIVE_MODE_SINGLE_XML = new ExportMode(PMODE_ARCHIVE_MODE_SINGLE_XML,false);
	public static final ExportMode PMODE_EXPORT_ARCHIVE_MODE_MULTIPLE_ZIP = new ExportMode(PMODE_ARCHIVE_MODE_MULTIPLE_ZIP,false);
	/** Archive mode type */
	public static final ArchiveModeType PMODE_ARCHIVE_MODE_TYPE_XML = new ArchiveModeType("xml");
	public static final ArchiveModeType PMODE_ARCHIVE_MODE_TYPE_ZIP = new ArchiveModeType("zip");
	/** Archive extension */
	public static final String PMODE_ARCHIVE_EXT_XML = "xml";
	public static final String PMODE_ARCHIVE_EXT_ZIP = "zip";
	public static final String PMODE_ARCHIVE_ROOT_DIR = "pmodes";

}





