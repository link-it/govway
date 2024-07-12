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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.openspcoop2.utils.SortedMap;
import org.openspcoop2.utils.UtilsException;

/**     
 * BYOKProvider
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class BYOKProvider {
	
	public static final String BYOK_POLICY_UNDEFINED_EMPTY = "";
	public static final String BYOK_POLICY_UNDEFINED = "-"; // nelle maschere generic_properties ho bisogno di usare questo valore
	public static final String BYOK_POLICY_UNDEFINED_LABEL = BYOK_POLICY_UNDEFINED;
	
	private static boolean unwrapKeystoreFileEnabled = true;
	public static boolean isUnwrapKeystoreFileEnabled() {
		return unwrapKeystoreFileEnabled;
	}
	public static void setUnwrapKeystoreFileEnabled(boolean unwrapKeystoreFileEnabled) {
		BYOKProvider.unwrapKeystoreFileEnabled = unwrapKeystoreFileEnabled;
	}
	
	public static BYOKProvider getWrapInstance() throws UtilsException {
		return new BYOKProvider(true);
	}
	public static BYOKProvider getUnwrapInstance() throws UtilsException {
		return new BYOKProvider(false);
	}
	
	private boolean byok = false;
	public boolean isByokEnabled() {
		return this.byok;
	}
	public boolean isUnwrapByokKeystoreEnabled() {
		return this.byok && unwrapKeystoreFileEnabled;
	}
	private List<String> byokTypes = new ArrayList<>();
	private List<String> byokLabels = new ArrayList<>();
	private Map<String,BYOKSecurityConfig> byokSecurity = new HashMap<>();
	private static final String NO_BYOK = "--no_byok--";
	private static List<String> noBYOK = new ArrayList<>();
	static{
		noBYOK.add(NO_BYOK);	
	}
	private static final String DEFAULT_BYOK = "Default";
	private BYOKProvider(boolean wrap) throws UtilsException {
		BYOKManager byokManager = BYOKManager.getInstance();
		SortedMap<String> sortedMap = wrap ? byokManager.getKeystoreWrapConfigTypesLabels() : byokManager.getKeystoreUnwrapConfigTypesLabels();
		this.byok = sortedMap!=null && !sortedMap.isEmpty();
		if(this.byok) {
			List<String> byokTypesAdd = new ArrayList<>();
			List<String> byokLabelsAdd = new ArrayList<>();
			String typeDefault = null;
			if(!sortedMap.isEmpty()) {
				for (String type : sortedMap.keys()) {
					if(init(byokManager, type, byokTypesAdd, byokLabelsAdd, sortedMap)) {
						typeDefault = type;
					}
				}
			}
			boolean byokEnabled = !byokTypesAdd.isEmpty();
			if(byokEnabled) {
				fillList(typeDefault, byokTypesAdd, byokLabelsAdd);
			}
		}
	}
	private boolean init(BYOKManager byokManager, String type, List<String> byokTypesAdd, List<String> byokLabelsAdd, SortedMap<String> sortedMap) throws UtilsException {
		
		boolean addDefault = false;
		
		String label = sortedMap.get(type);
		
		StringBuilder securityId = new StringBuilder();
		if(byokManager.isKSMUsedInSecurityUnwrapConfig(type, securityId)) {
			String secId =  securityId.toString();
			BYOKSecurityConfig secConfig = byokManager.getKSMSecurityConfig(secId);
			
			if( BYOKManager.getSecurityRemoteEngineGovWayPolicy()!=null &&
				BYOKManager.getSecurityRemoteEngineGovWayPolicy().equals(secId)) {
				addDefault = true;
			}
			
			if(BYOKManager.getSecurityEngineGovWayPolicy()!=null && 
				BYOKManager.getSecurityEngineGovWayPolicy().equals(secId)) {
				if( BYOKManager.getSecurityRemoteEngineGovWayPolicy()!=null ) {
					return false;
				}
				else {
					addDefault = true;
				}
			}

			this.byokSecurity.put(type,secConfig);
						
			if(!addDefault) {
				byokTypesAdd.add(type);
				byokLabelsAdd.add(label);
			}

		}
		else {
			byokTypesAdd.add(type);
			byokLabelsAdd.add(label);
		}
		
		return addDefault;
	}
	private void fillList(String typeDefault, List<String> byokTypesAdd, List<String> byokLabelsAdd) {
		this.byokTypes.add(BYOK_POLICY_UNDEFINED_EMPTY);
		if(typeDefault!=null) {
			this.byokTypes.add(typeDefault);
		}
		this.byokTypes.addAll(byokTypesAdd);
		this.byokLabels.add(BYOK_POLICY_UNDEFINED_LABEL);
		if(typeDefault!=null) {
			this.byokLabels.add(DEFAULT_BYOK);
		}
		this.byokLabels.addAll(byokLabelsAdd);
	}
	
	public List<String> getValues() {
		return this.byok ? this.byokTypes : noBYOK;
	}

	public List<String> getLabels() {
		return this.byok ? this.byokLabels : noBYOK;
	}

	public Map<String, String> getInputMap(String ksmId) {
		Map<String, String> inputMap = new HashMap<>();
		BYOKSecurityConfig secConfig = this.byokSecurity.get(ksmId);
		if(secConfig!=null && secConfig.getInputParameters()!=null && !secConfig.getInputParameters().isEmpty()) {
			for (BYOKSecurityConfigParameter param : secConfig.getInputParameters()) {
				inputMap.put(param.getName(), param.getValue());
			}
		}
		return inputMap;
	}
	
	public static boolean isPolicyDefined(String ksmId) {
		return ksmId!=null && StringUtils.isNotEmpty(ksmId) && !BYOK_POLICY_UNDEFINED_EMPTY.equals(ksmId) && !BYOK_POLICY_UNDEFINED.equals(ksmId);
	}
	
	public static BYOKRequestParams getBYOKRequestParamsByUnwrapBYOKPolicy(String ksmId,
			Map<String,Object> dynamicMap) throws UtilsException {
		// configurato via console
		
		if(!isPolicyDefined(ksmId)) {
			return null;
		}
		
		BYOKProvider provider = BYOKProvider.getUnwrapInstance();
		Map<String, String> inputMap = provider.getInputMap(ksmId);
		
		return BYOKRequestParams.getBYOKRequestParamsByKsmId(ksmId, 
				inputMap, dynamicMap);
	}
	
}
