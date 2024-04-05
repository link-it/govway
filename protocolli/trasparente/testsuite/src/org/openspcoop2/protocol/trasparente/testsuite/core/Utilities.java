/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2024 Link.it srl (https://link.it). 
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



package org.openspcoop2.protocol.trasparente.testsuite.core;

import java.io.File;

import org.openspcoop2.message.OpenSPCoop2MessageFactory;
import org.openspcoop2.message.xml.MessageDynamicNamespaceContextFactory;
import org.openspcoop2.message.xml.MessageXMLUtils;
import org.openspcoop2.message.xml.XPathExpressionEngine;
import org.openspcoop2.protocol.sdk.ProtocolException;
import org.openspcoop2.protocol.sdk.constants.CodiceErroreCooperazione;
import org.openspcoop2.protocol.sdk.constants.CodiceErroreIntegrazione;
import org.openspcoop2.utils.xml.AbstractXMLUtils;
import org.w3c.dom.Document;

/**
 * Contiene utility per i test effettuati.
 * 
 * @author Andi Rexha (rexha@openspcoop.org)
 * @author $Author$
 * @version $Rev$, $Date$
 */

public class Utilities {

	/** 
	 * Counter utilizzato per la creazione dell'identificativo eGov
	 */
	public static int counterIDSeriale = 1;
	
	/** TestSuite Properties */
	public static TestSuiteProperties testSuiteProperties = TestSuiteProperties.getInstance();
	
	/** Database Properties */
	public static org.openspcoop2.testsuite.units.utils.DatabaseProperties databaseProperties = DatabaseProperties.getInstance();
	
	public static String readApplicationServerVersion() throws Exception{
		byte[] local_env = org.openspcoop2.utils.resources.FileSystemUtilities.readBytesFromFile(new File("local_env.xml"));
		OpenSPCoop2MessageFactory messageFactory = OpenSPCoop2MessageFactory.getDefaultMessageFactory();
		AbstractXMLUtils xmlUtils = MessageXMLUtils.getInstance(messageFactory);
		XPathExpressionEngine engine = new XPathExpressionEngine(messageFactory);
		Document local_env_document = xmlUtils.newDocument(local_env);
		String version_jbossas = engine.getStringMatchPattern(local_env_document, MessageDynamicNamespaceContextFactory.getInstance(messageFactory).getNamespaceContext(local_env_document), "//property[@name='as']/@value");
		return version_jbossas;
	}
	
	public static String toString(CodiceErroreIntegrazione codiceErrore) throws ProtocolException{
		return org.openspcoop2.protocol.basic.Costanti.ERRORE_INTEGRAZIONE_PREFIX_CODE+codiceErrore.getCodice();
	}
	public static String toString(CodiceErroreCooperazione codiceErrore) throws ProtocolException{
		return org.openspcoop2.protocol.basic.Costanti.ERRORE_PROTOCOLLO_PREFIX_CODE+codiceErrore.getCodice();
	}
}


