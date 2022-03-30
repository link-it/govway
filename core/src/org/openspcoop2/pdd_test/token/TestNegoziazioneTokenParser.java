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

package org.openspcoop2.pdd_test.token;

import org.openspcoop2.pdd_test.Costanti;
import org.openspcoop2.utils.test.TestLogger;
import org.testng.annotations.Test;

/**
 * TestNegoziazioneTokenParser
 * 
 * @author Andrea Poli (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class TestNegoziazioneTokenParser {

	private static final String ID_TEST = "NegoziazioneTokenParser";
	
	@Test(groups={Costanti.GRUPPO_PDD,Costanti.GRUPPO_PDD+"."+ID_TEST})
	public void testNegoziazioneTokenParser() throws Exception{
		
		TestLogger.info("Run test '"+ID_TEST+"' ...");
		org.openspcoop2.pdd.core.token.parser.TestBasicNegoziazioneTokenParser.main(null);
		TestLogger.info("Run test '"+ID_TEST+"' ok");
		
	}
	
}
