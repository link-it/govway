/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2021 Link.it srl (https://link.it). 
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

package org.openspcoop2.utils.openapi;

import java.net.URI;

import org.openspcoop2.utils.rest.ApiFormats;

/**
 * TestIndentazione
 * 
 * @author Andrea Poli (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 *
 */
public class TestIndentazione {

	public static void main(String[] args) throws Exception {

		URI uri = TestIndentazione.class.getResource("/org/openspcoop2/utils/openapi/testIndentazioneOpenAPI_3.0.yaml").toURI();
		
		String baseUri = "http://petstore.swagger.io/api";

		Test.testIndentazione(uri, "yamlIndentatoDueTab", ApiFormats.OPEN_API_3, baseUri);
		
	}

}
