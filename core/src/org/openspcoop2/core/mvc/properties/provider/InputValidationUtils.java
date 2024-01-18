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
package org.openspcoop2.core.mvc.properties.provider;

/**     
 * InputValidationUtils
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class InputValidationUtils {
	
	private InputValidationUtils() {}

	public static void validateTextAreaInput(String input, String fieldName) throws ProviderValidationException {
		validateTextAreaInput(input, fieldName, false, false, false);
	}
	public static void validateTextAreaInput(String input, String fieldName,boolean spaceEnabled) throws ProviderValidationException {
		validateTextAreaInput(input, fieldName, spaceEnabled, false, false);
	}
	public static void validateTextAreaInput(String input, String fieldName,
			boolean spaceEnabled, boolean tabEnabled, boolean newLineEnabled) throws ProviderValidationException {
		
		String prefix = "Il valore indicato nel campo '";
		
		if(input==null || "".equals(input)) {
			throw new ProviderValidationException("Non è stato fornito un valore nel campo '"+fieldName+"'");
		}
		if(spaceEnabled) {
			if(input.startsWith(" ")) {
				throw new ProviderValidationException(prefix+fieldName+"' non deve iniziare con uno spazio");
			}
			if(input.endsWith(" ")) {
				throw new ProviderValidationException(prefix+fieldName+"' non deve terminare con uno spazio");
			}
		}
		else {
			if(input.contains(" ")) {
				throw new ProviderValidationException(prefix+fieldName+"' non deve contenere spazi");
			}
		}
		if(!tabEnabled && input.contains("\t")) {
			throw new ProviderValidationException(prefix+fieldName+"' non deve contenere tab (\\t)");
		}
		if(!newLineEnabled && 
				(input.contains("\n") || input.contains("\r"))) {
			throw new ProviderValidationException(prefix+fieldName+"' non deve contenere ritorni a capo (\\n o \\r)");
		}
	}
	
}
