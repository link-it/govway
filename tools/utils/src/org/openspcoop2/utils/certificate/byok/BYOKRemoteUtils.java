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
package org.openspcoop2.utils.certificate.byok;

import org.apache.commons.lang.StringUtils;
import org.openspcoop2.utils.UtilsException;
import org.openspcoop2.utils.io.Base64Utilities;
import org.openspcoop2.utils.io.HexBinaryUtilities;
import org.openspcoop2.utils.json.JSONUtils;
import org.openspcoop2.utils.json.JsonPathExpressionEngine;
import org.slf4j.Logger;

/**
 * BYOKRemoteUtils
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class BYOKRemoteUtils {
	
	private BYOKRemoteUtils() {}

	public static byte[] normalizeResponse(BYOKInstance instance, byte [] content, Logger log) throws UtilsException {
		if(content!=null && content.length>0) {
			if(instance.getConfig().getRemoteConfig().isHttpResponseBase64Encoded()) {
				content = Base64Utilities.decode(content);
			}
			else if(instance.getConfig().getRemoteConfig().isHttpResponseHexEncoded()) {
				content = HexBinaryUtilities.decode(new String(content).toCharArray());
			}
			
			return normalizeResponseByJsonPath(instance, content, log);
		}
		return content;
	}
	private static byte[] normalizeResponseByJsonPath(BYOKInstance instance, byte [] content, Logger log) throws UtilsException {
		String pattern = instance.getConfig().getRemoteConfig().getHttpResponseJsonPath();
		if(pattern!=null && StringUtils.isNotEmpty(pattern)) {
			JSONUtils jsonUtils = JSONUtils.getInstance();
			if(jsonUtils.isJson(content)) {
				String elementJson = new String(content); 
				try {
					String valoreEstratto = JsonPathExpressionEngine.extractAndConvertResultAsString(elementJson, pattern, log);
					if(valoreEstratto==null || StringUtils.isEmpty(valoreEstratto)) {
						throw new UtilsException("Read failure with pattern '"+pattern+"'");
					}
					content = valoreEstratto.getBytes();
				}catch(Exception e) {
					throw new UtilsException(e.getMessage(),e);
				}
			}
		}
		return content;
	}
	
}
