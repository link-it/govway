/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2022 Link.it srl (https://link.it).
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
package org.openspcoop2.message.rest;

import java.io.ByteArrayInputStream;

import org.openspcoop2.message.AttachmentsProcessingMode;
import org.openspcoop2.message.OpenSPCoop2Message;
import org.openspcoop2.message.OpenSPCoop2MessageFactory;
import org.openspcoop2.message.constants.MessageRole;
import org.openspcoop2.message.constants.MessageType;

/**
 * TestXXE
 * 
 * @author Andrea Poli (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class TestXXE {


	
	public static void main(String[] args) throws Exception {
		
		// Default è true
		// AbstractXMLUtils.DISABLE_DTDs=false;
		
		OpenSPCoop2MessageFactory factory = OpenSPCoop2MessageFactory.getDefaultMessageFactory();
		
		
		System.out.println("- createMessage(byte[])");
		try {
			OpenSPCoop2Message msg = factory.createMessage(MessageType.XML, MessageRole.REQUEST, "application/xml;charset=UTF-8", org.openspcoop2.utils.xml.test.TestXXE.xmlMessage, AttachmentsProcessingMode.getMemoryCacheProcessingMode()).getMessage_throwParseException();	
			msg.castAsRestXml().getContent();
			throw new Exception("createMessage ok ?");
		}catch(Exception e) {
			if(e.getMessage().contains("DOCTYPE is disallowed when the feature \"http://apache.org/xml/features/disallow-doctype-decl\" set to true")) {
				System.out.println("\t Eccezione attesa: "+e.getMessage());
			}
			else {
				throw e;
			}
		}
		
		
		System.out.println("- createMessage(InputStream)");
		try {
			try(ByteArrayInputStream bin = new ByteArrayInputStream(org.openspcoop2.utils.xml.test.TestXXE.xmlMessage)){
				OpenSPCoop2Message msg = factory.createMessage(MessageType.XML, MessageRole.REQUEST, "application/xml;charset=UTF-8", bin, null).getMessage_throwParseException();	
				msg.castAsRestXml().getContent();
			}
			throw new Exception("createMessage ok ?");
		}catch(Exception e) {
			if(e.getMessage().contains("DOCTYPE is disallowed when the feature \"http://apache.org/xml/features/disallow-doctype-decl\" set to true")) {
				System.out.println("\t Eccezione attesa: "+e.getMessage());
			}
			else {
				throw e;
			}
		}
		
	}

}
