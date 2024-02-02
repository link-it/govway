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

package org.openspcoop2.core.protocolli.trasparente.testsuite.plugin.classes;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.openspcoop2.pdd.core.token.attribute_authority.BasicRetrieveAttributeAuthorityResponseParser;
import org.openspcoop2.pdd.core.token.attribute_authority.TipologiaResponseAttributeAuthority;
import org.openspcoop2.pdd.logger.OpenSPCoop2Logger;

/**
* AttributeAuthorityParserCustom
*
* @author Andrea Poli (poli@link.it)
* @author $Author$
* @version $Rev$, $Date$
*/
public class AttributeAuthorityParserCustom extends BasicRetrieveAttributeAuthorityResponseParser {

	private static List<String> attributesClaims = new ArrayList<>();
	static {
		attributesClaims.add("pa");
		attributesClaims.add("pd");
		attributesClaims.add("operazione");
	}
	
	public AttributeAuthorityParserCustom() {
		super("TestPlugins", OpenSPCoop2Logger.getLoggerOpenSPCoopCore(), TipologiaResponseAttributeAuthority.json, attributesClaims);
	}
	
	@Override
	public void init(String raw, Map<String, Serializable> claims) {
		
		try {
			String id = Utilities.readIdentificativoTest();
			String tipoTest = "attribute-authority";
			String operazione = (String) claims.get("operazione");
			tipoTest = tipoTest + "-"+operazione;
			if(claims.containsKey("pa")) {
				tipoTest = tipoTest + "-pa";
			}
			else {
				tipoTest = tipoTest + "-pd";
			}
			Utilities.writeIdentificativoTest(id, tipoTest);
		}catch(Exception e) {
			throw new RuntimeException(e.getMessage(),e);
		}
		
		super.init(raw, claims);
	}

}
