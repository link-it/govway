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


package org.openspcoop2.pdd.core.token.attribute_authority;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.openspcoop2.core.config.GenericProperties;
import org.openspcoop2.core.config.Property;
import org.openspcoop2.core.mvc.properties.utils.DBPropertiesUtils;

/**     
 * TokenUtilities
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class AttributeAuthorityUtilities {
	
	public static PolicyAttributeAuthority convertTo(GenericProperties gp) throws Exception { 
		
		PolicyAttributeAuthority policy = new PolicyAttributeAuthority();
		policy.setName(gp.getNome());
		policy.setDescrizione(gp.getDescrizione());
		
		HashMap<String, String> properties = new HashMap<>();
		for (Property pConfig : gp.getPropertyList()) {
			properties.put(pConfig.getNome(), pConfig.getValore());
		}
		Map<String, Properties> multiProperties = DBPropertiesUtils.toMultiMap(properties);
		policy.setProperties(multiProperties);
		
		return policy;

	}
}
