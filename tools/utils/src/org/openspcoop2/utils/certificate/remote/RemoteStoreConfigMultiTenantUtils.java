/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2023 Link.it srl (https://link.it). 
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

package org.openspcoop2.utils.certificate.remote;

import java.util.HashMap;
import java.util.Map;

/**
 * RemoteStoreConfigMultiTenantUtils
 *
 * @author Andrea Poli (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class RemoteStoreConfigMultiTenantUtils {
	
	private RemoteStoreConfigMultiTenantUtils() {}


	protected static String getMultitenant(Map<String, String> thisParam, String tenant,
			String cloned){
		if(thisParam!=null && !thisParam.isEmpty()) {
			String newValue = thisParam.get(tenant);
			if(newValue!=null) {
				return newValue;
			}
		}
		return cloned;
	}
	protected static Map<String, String> getMultitenant(Map<String, Map<String, String>> thisParam, String tenant,
			Map<String, String> cloned){
		if(thisParam!=null && !thisParam.isEmpty()) {
			Map<String, String> thisMap = thisParam.get(tenant);
			return getMultitenant(thisMap, cloned);
		}
		return cloned;
	}
	private static Map<String, String> getMultitenant(Map<String, String> thisParam, Map<String, String> cloned){
		if(thisParam!=null && !thisParam.isEmpty()) {
			for (Map.Entry<String,String> entry : thisParam.entrySet()) {
				if(entry.getKey()!=null && entry.getValue()!=null) {
					if(cloned==null) {
						cloned=new HashMap<>();
					}
					cloned.put(entry.getKey(), entry.getValue());
				}
			}
		}
		return cloned;
	}
	
	protected static Map<String, String> newMapInstance(Map<String, String> thisParam){
		HashMap<String, String> cloned = null;
		if(thisParam!=null) {
			cloned = new HashMap<>();
			if(!thisParam.isEmpty()) {
				for (Map.Entry<String,String> entry : thisParam.entrySet()) {
					if(entry.getKey()!=null && entry.getValue()!=null) {
						cloned.put(entry.getKey()+"", entry.getValue()+"");
					}
				}
			}
		}
		return cloned;
	}
	protected static Map<String, Map<String, String>> newMultiMapInstance(Map<String, Map<String, String>> thisParam){
		Map<String, Map<String, String>> cloned = null;
		if(thisParam!=null) {
			cloned = new HashMap<>();
			setMultiMapInstanceEngine(thisParam, cloned);
		}
		return cloned;
	}
	private static void setMultiMapInstanceEngine(Map<String, Map<String, String>> thisParam, Map<String, Map<String, String>> cloned){
		if(!thisParam.isEmpty()) {
			for (Map.Entry<String,Map<String, String>> entry : thisParam.entrySet()) {
				if(entry.getKey()!=null && entry.getValue()!=null) {
					Map<String, String> mValue = newMapInstance(entry.getValue());
					if(mValue!=null) {
						cloned.put(entry.getKey()+"", mValue);
					}
				}
			}
		}
	}
	
}
