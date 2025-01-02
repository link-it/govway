/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2025 Link.it srl (https://link.it).
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

import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.openspcoop2.core.mvc.properties.Item;

/**     
 * IProvider
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public interface IProvider {

	public default void validateId(String name) throws ProviderException, ProviderValidationException {	}
	public default void validateDescription(String description) throws ProviderException, ProviderValidationException {	}
	
	public void validate(Map<String, Properties> mapProperties) throws ProviderException, ProviderValidationException;
	
	public List<String> getValues(String id) throws ProviderException;
	public default List<String> getValues(String id, ExternalResources externalResources) throws ProviderException{
		return this.getValues(id); 
	}
	public List<String> getLabels(String id) throws ProviderException;
	public default List<String> getLabels(String id, ExternalResources externalResources) throws ProviderException{
		return this.getLabels(id); 
	}
	
	public String getDefault(String id) throws ProviderException;
	public default String getDefault(String id, ExternalResources externalResources) throws ProviderException{
		return this.getDefault(id); 
	}
	
	public default String getNote(String id, String actualValue) throws ProviderException{ return null; }
	
	public default ProviderInfo getProviderInfo(String id) throws ProviderException{
		return null;
	}
	public default String dynamicUpdate(List<?> items, Map<String, String> mapNameValue, Item item, String actualValue) {
		return actualValue;
	}
	public default String dynamicUpdate(List<?> items, Map<String, String> mapNameValue, Item item, String actualValue, ExternalResources externalResources) {
		return dynamicUpdate(items, mapNameValue, item, actualValue);
	}
	
}
