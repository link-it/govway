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

package org.openspcoop2.protocol.as4.utils;

import java.util.List;

import org.openspcoop2.core.registry.ProtocolProperty;
import org.openspcoop2.protocol.sdk.ProtocolException;
import org.openspcoop2.protocol.sdk.properties.ProtocolPropertiesUtils;

/**
 * AS4PropertiesUtils
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class AS4PropertiesUtils {

	public static String getRequiredStringValue(List<ProtocolProperty> list, String propertyName) throws ProtocolException{
		return ProtocolPropertiesUtils.getRequiredStringValuePropertyRegistry(list, propertyName);
	}
	public static String getOptionalStringValue(List<ProtocolProperty> list, String propertyName) throws ProtocolException{
		return ProtocolPropertiesUtils.getOptionalStringValuePropertyRegistry(list, propertyName);
	}
	
	public static ProtocolProperty getProtocolProperty(List<ProtocolProperty> list, String propertyName, boolean throwNotFoundException) throws ProtocolException{
		return ProtocolPropertiesUtils.getProtocolPropertyRegistry(list, propertyName, throwNotFoundException);
	}
	
}
