/*
 * OpenSPCoop v2 - Customizable SOAP Message Broker 
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

package org.openspcoop2.security.message.constants;

/**
 * Constants
 *
 * @author Andrea Poli <apoli@link.it>
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class WSSAttachmentsConstants {

	public static final String SWA_NS = "http://docs.oasis-open.org/wss/oasis-wss-SwAProfile-1.1";
	public static final String ATTACHMENT_CONTENT_ONLY_TRANSFORM_URI =
            WSSAttachmentsConstants.SWA_NS + "#Attachment-Content-Only-Transform";
	
	public static final String ATTACHMENT_CIPHERTEXT_TRANSFORM_URI =
            WSSAttachmentsConstants.SWA_NS + "#Attachment-Ciphertext-Transform";
    
    public static final String ATTACHMENT_COMPLETE_TRANSFORM_URI =
            WSSAttachmentsConstants.SWA_NS + "#Attachment-Complete-Transform";
    
    public static final String ATTACHMENT_CONTENT_ONLY_URI =
            WSSAttachmentsConstants.SWA_NS + "#Attachment-Content-Only";
    
    public static final String ATTACHMENT_COMPLETE_URI =
            WSSAttachmentsConstants.SWA_NS + "#Attachment-Complete";
    
    public static final String ATTACHMENT_CONTENT_SIGNATURE_TRANSFORM_URI =
            WSSAttachmentsConstants.SWA_NS + "#Attachment-Content-Signature-Transform";
    
    public static final String ATTACHMENT_COMPLETE_SIGNATURE_TRANSFORM_URI =
            WSSAttachmentsConstants.SWA_NS + "#Attachment-Complete-Signature-Transform";
	
}
