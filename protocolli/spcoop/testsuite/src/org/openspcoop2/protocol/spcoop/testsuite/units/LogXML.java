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



package org.openspcoop2.protocol.spcoop.testsuite.units;

import javax.xml.soap.SOAPException;

import org.openspcoop2.testsuite.core.TestSuiteException;
import org.testng.annotations.Test;


/**
 * Test sui profili di collaborazione implementati nella Porta di Dominio
 * 
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class LogXML {

	/** Identificativo del gruppo */
	public static final String ID_GRUPPO = "LogXML";
	

	
	


	
	
	
	
	// ------------- Msg diagnostici ------------------

	@Test(groups={LogXML.ID_GRUPPO,LogXML.ID_GRUPPO+".MSG_DIAGNOSTICI"})
	public void msgDiagnostici()throws TestSuiteException,SOAPException, Exception{
		
		org.openspcoop2.protocol.spcoop.testsuite.core.FileSystemUtilities.verificaMsgDiagnosticiXML();
		
	}
	
	
	
	
	
	
	
	
	
	
	// ------------- Tracce ------------------

	@Test(groups={LogXML.ID_GRUPPO,LogXML.ID_GRUPPO+".TRACCE"})
	public void tracce()throws TestSuiteException,SOAPException, Exception{
		
		org.openspcoop2.protocol.spcoop.testsuite.core.FileSystemUtilities.verificaTracciaturaXML();
		
	}
	
	
	
}
