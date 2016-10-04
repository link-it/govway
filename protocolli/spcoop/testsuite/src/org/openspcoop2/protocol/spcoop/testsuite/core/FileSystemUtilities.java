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


package org.openspcoop2.protocol.spcoop.testsuite.core;

import java.util.Date;

import org.openspcoop2.message.ValidatoreXSD;
import org.openspcoop2.testsuite.core.ErroreAttesoOpenSPCoopLogCore;
import org.openspcoop2.utils.LoggerWrapperFactory;
import org.openspcoop2.utils.xml.XSDResourceResolver;

/**
 * Utilities per verifica log su file system
 * 
 * @author Andrea Poli (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */

public class FileSystemUtilities {

	public static void verificaOpenspcoopCore(Date dataAvvioGruppoTest) throws Exception{
		verificaOpenspcoopCore(dataAvvioGruppoTest,new ErroreAttesoOpenSPCoopLogCore[0]);
	}

	public static void verificaOpenspcoopCore(Date dataAvvioGruppoTest, ErroreAttesoOpenSPCoopLogCore ... erroriAttesi) throws Exception{
		org.openspcoop2.protocol.spcoop.testsuite.core.TestSuiteProperties testsuiteProperties = org.openspcoop2.protocol.spcoop.testsuite.core.TestSuiteProperties.getInstance();
		String logDirectory = testsuiteProperties.getLogDirectoryOpenSPCoop();
		org.openspcoop2.testsuite.core.FileSystemUtilities.verificaOpenspcoopCore(dataAvvioGruppoTest, logDirectory, erroriAttesi);
	}


	public static void verificaMsgDiagnosticiXML() throws Exception{
		org.openspcoop2.protocol.spcoop.testsuite.core.TestSuiteProperties testsuiteProperties = org.openspcoop2.protocol.spcoop.testsuite.core.TestSuiteProperties.getInstance();
		String logDirectory = testsuiteProperties.getLogDirectoryOpenSPCoop();
		ValidatoreXSD validatoreXSD = new ValidatoreXSD(LoggerWrapperFactory.getLogger(FileSystemUtilities.class),
				FileSystemUtilities.class.getResourceAsStream("/msgDiagnostico.xsd"));
		org.openspcoop2.testsuite.core.FileSystemUtilities.verificaMsgDiagnosticiXML(logDirectory, validatoreXSD);
	}


	public static void verificaTracciaturaXML() throws Exception{
		org.openspcoop2.protocol.spcoop.testsuite.core.TestSuiteProperties testsuiteProperties = org.openspcoop2.protocol.spcoop.testsuite.core.TestSuiteProperties.getInstance();
		String logDirectory = testsuiteProperties.getLogDirectoryOpenSPCoop();
		XSDResourceResolver xsdResourceResolver = new XSDResourceResolver();
		xsdResourceResolver.addResource("Busta.xsd", FileSystemUtilities.class.getResourceAsStream("/Busta.xsd"));
		xsdResourceResolver.addResource("soapEnvelope.xsd", FileSystemUtilities.class.getResourceAsStream("/soapEnvelope.xsd"));
		xsdResourceResolver.addResource("wssecurityUtility.xsd", FileSystemUtilities.class.getResourceAsStream("/wssecurityUtility.xsd"));
		ValidatoreXSD validatoreXSD = new ValidatoreXSD(LoggerWrapperFactory.getLogger(FileSystemUtilities.class),
				xsdResourceResolver,FileSystemUtilities.class.getResourceAsStream("/Tracciamento.xsd"));
		org.openspcoop2.testsuite.core.FileSystemUtilities.verificaTracciaturaXML(logDirectory, validatoreXSD);
	}
	
}
