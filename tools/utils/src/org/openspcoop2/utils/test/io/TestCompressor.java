/*
 * GovWay - A customizable API Gateway 
 * http://www.govway.org
 *
 * from the Link.it OpenSPCoop project codebase
 * 
 * Copyright (c) 2005-2019 Link.it srl (http://link.it).
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

package org.openspcoop2.utils.test.io;

import org.openspcoop2.utils.io.CompressorType;
import org.openspcoop2.utils.test.Costanti;
import org.openspcoop2.utils.test.TestLogger;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * TestCompressor
 * 
 * @author Andrea Poli (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class TestCompressor {

	private static final String ID_TEST = "Compressor";
	
	@DataProvider(name="compressorProvider")
	public Object[][] provider(){
		return new Object[][]{
				{CompressorType.DEFLATER.name()},
				{CompressorType.GZIP.name()},
				{CompressorType.ZIP.name()}
		};
	}
	
	@Test(groups={Costanti.GRUPPO_UTILS,Costanti.GRUPPO_UTILS+"."+ID_TEST},dataProvider="compressorProvider")
	public void testCompressor(String tipo) throws Exception{
		
		TestLogger.info("Run test '"+ID_TEST+"' (tipo:"+tipo+") ...");
		org.openspcoop2.utils.io.CompressorUtilities.main(new String[] {tipo});
		TestLogger.info("Run test '"+ID_TEST+"' (tipo:"+tipo+") ok");
		
	}
	
}
