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

package org.openspcoop2.utils.test.pdf;

import org.openspcoop2.utils.test.Costanti;
import org.openspcoop2.utils.test.TestLogger;
import org.testng.annotations.Test;

/**
 * TestEncrypt
 * 
 * @author Andrea Poli (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class TestPDFWriter {

	private static final String ID_TEST = "PDFWriter";
		
	@Test(groups={Costanti.GRUPPO_UTILS,Costanti.GRUPPO_UTILS+"."+ID_TEST,Costanti.GRUPPO_UTILS+"."+ID_TEST+".testAnnotation"})
	public void testAnnotation() throws Exception{
		
		String tipo = "testAnnotation";
		TestLogger.info("Run test '"+ID_TEST+"' (tipo: "+tipo+") ...");
		org.openspcoop2.utils.pdf.test.PDFWriterTest.testAnnotation();
		TestLogger.info("Run test '"+ID_TEST+"' (tipo: "+tipo+") ok");
		
	}
	@Test(groups={Costanti.GRUPPO_UTILS,Costanti.GRUPPO_UTILS+"."+ID_TEST,Costanti.GRUPPO_UTILS+"."+ID_TEST+".testMultipleAnnotations"})
	public void testMultipleAnnotations() throws Exception{
		
		String tipo = "testMultipleAnnotations";
		TestLogger.info("Run test '"+ID_TEST+"' (tipo: "+tipo+") ...");
		org.openspcoop2.utils.pdf.test.PDFWriterTest.testMultipleAnnotation();
		TestLogger.info("Run test '"+ID_TEST+"' (tipo: "+tipo+") ok");
		
	}
	
	@Test(groups={Costanti.GRUPPO_UTILS,Costanti.GRUPPO_UTILS+"."+ID_TEST,Costanti.GRUPPO_UTILS+"."+ID_TEST+".testEmbedded"})
	public void testEmbedded() throws Exception{
		
		String tipo = "testEmbedded";
		TestLogger.info("Run test '"+ID_TEST+"' (tipo: "+tipo+") ...");
		org.openspcoop2.utils.pdf.test.PDFWriterTest.testEmbedded();
		TestLogger.info("Run test '"+ID_TEST+"' (tipo: "+tipo+") ok");
		
	}
	@Test(groups={Costanti.GRUPPO_UTILS,Costanti.GRUPPO_UTILS+"."+ID_TEST,Costanti.GRUPPO_UTILS+"."+ID_TEST+".testMultipleEmbedded"})
	public void testMultipleEmbedded() throws Exception{
		
		String tipo = "testMultipleEmbedded";
		TestLogger.info("Run test '"+ID_TEST+"' (tipo: "+tipo+") ...");
		org.openspcoop2.utils.pdf.test.PDFWriterTest.testMultipleEmbedded();
		TestLogger.info("Run test '"+ID_TEST+"' (tipo: "+tipo+") ok");
		
	}
	
	@Test(groups={Costanti.GRUPPO_UTILS,Costanti.GRUPPO_UTILS+"."+ID_TEST,Costanti.GRUPPO_UTILS+"."+ID_TEST+".testEmbeddedKid"})
	public void testEmbeddedKid() throws Exception{
		
		String tipo = "testEmbeddedKid";
		TestLogger.info("Run test '"+ID_TEST+"' (tipo: "+tipo+") ...");
		org.openspcoop2.utils.pdf.test.PDFWriterTest.testEmbeddedKid();
		TestLogger.info("Run test '"+ID_TEST+"' (tipo: "+tipo+") ok");
		
	}
	@Test(groups={Costanti.GRUPPO_UTILS,Costanti.GRUPPO_UTILS+"."+ID_TEST,Costanti.GRUPPO_UTILS+"."+ID_TEST+".testMultipleEmbeddedKids"})
	public void testMultipleEmbeddedKids() throws Exception{
		
		String tipo = "testMultipleEmbeddedKids";
		TestLogger.info("Run test '"+ID_TEST+"' (tipo: "+tipo+") ...");
		org.openspcoop2.utils.pdf.test.PDFWriterTest.testMultipleEmbeddedKids();
		TestLogger.info("Run test '"+ID_TEST+"' (tipo: "+tipo+") ok");
		
	}
	
	@Test(groups={Costanti.GRUPPO_UTILS,Costanti.GRUPPO_UTILS+"."+ID_TEST,Costanti.GRUPPO_UTILS+"."+ID_TEST+".testXFAFile"})
	public void testXFAFile() throws Exception{
		
		String tipo = "testXFAFile";
		TestLogger.info("Run test '"+ID_TEST+"' (tipo: "+tipo+") ...");
		org.openspcoop2.utils.pdf.test.PDFWriterTest.testXFAFile(false);
		TestLogger.info("Run test '"+ID_TEST+"' (tipo: "+tipo+") ok");
		
	}
	@Test(groups={Costanti.GRUPPO_UTILS,Costanti.GRUPPO_UTILS+"."+ID_TEST,Costanti.GRUPPO_UTILS+"."+ID_TEST+".testXFAFileDatasets"})
	public void testXFAFileDatasets() throws Exception{
		
		String tipo = "testXFAFileDatasets";
		TestLogger.info("Run test '"+ID_TEST+"' (tipo: "+tipo+") ...");
		org.openspcoop2.utils.pdf.test.PDFWriterTest.testXFAFile(true);
		TestLogger.info("Run test '"+ID_TEST+"' (tipo: "+tipo+") ok");
		
	}
	
	@Test(groups={Costanti.GRUPPO_UTILS,Costanti.GRUPPO_UTILS+"."+ID_TEST,Costanti.GRUPPO_UTILS+"."+ID_TEST+".testSignature"})
	public void testSignature() throws Exception{
		
		String tipo = "testSignature";
		TestLogger.info("Run test '"+ID_TEST+"' (tipo: "+tipo+") ...");
		org.openspcoop2.utils.pdf.test.PDFWriterTest.testSignature();
		TestLogger.info("Run test '"+ID_TEST+"' (tipo: "+tipo+") ok");
		
	}
	
}
