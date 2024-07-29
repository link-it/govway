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
package org.openspcoop2.testsuite.zap;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;

import org.apache.commons.lang3.StringUtils;
import org.openspcoop2.utils.UtilsException;
import org.zaproxy.clientapi.core.ClientApi;
import org.zaproxy.clientapi.core.ClientApiException;

/**
 * ConsoleFalsePositive
 *
 * @author Andrea Poli (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class FalsePositive {
	
	private FalsePositive() {}

	private boolean enabled = false;
	private String description;
	private String id;
	private String ulrRegExp;
	
	public boolean isEnabled() {
		return this.enabled;
	}
	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}
	public String getDescription() {
		return this.description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getId() {
		return this.id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getUlrRegExp() {
		return this.ulrRegExp;
	}
	public void setUlrRegExp(String ulrRegExp) {
		this.ulrRegExp = ulrRegExp;
	}
	
	private static final String ID_SUFFIX = ".id";
	private static final String ENABLED_SUFFIX = ".enabled";
	private static final String DESCRIPTION_SUFFIX = ".description";
	private static final String URLREGEXP_SUFFIX = ".urlRegExp";

	
	public static List<FalsePositive> parse(String content) throws IOException, UtilsException{
		
		List<FalsePositive> falsePositives = new ArrayList<>();
		
		try(ByteArrayInputStream bin = new ByteArrayInputStream(content.getBytes())){
			Properties p = new Properties();
			p.load(bin);
		
			List<String> f = new ArrayList<>();
			
			Enumeration<Object> en = p.keys();
			while (en.hasMoreElements()) {
				Object object = en.nextElement();
				if(object instanceof String) {
					String key = (String) object;
					if(key.endsWith(ID_SUFFIX) && key.length()>ID_SUFFIX.length()) {
						String name = key.substring(0,key.length()-ID_SUFFIX.length());
						f.add(name);
					}
				}
			}
			
			if(!f.isEmpty()) {
				for (String name : f) {
					FalsePositive fp = new FalsePositive();
					fp.setId(getProperty(p,name,ID_SUFFIX));
					fp.setEnabled(Boolean.valueOf(getProperty(p,name,ENABLED_SUFFIX)));
					fp.setDescription(getProperty(p,name,DESCRIPTION_SUFFIX));
					fp.setUlrRegExp(getProperty(p,name,URLREGEXP_SUFFIX));
					falsePositives.add(fp);
				}
			}
		}
		
		return falsePositives;
	}
	private static String getProperty(Properties p,String name,String pName) throws UtilsException {
		String pKey = name + pName;
		String value = p.getProperty(pKey);
		if(value==null || StringUtils.isEmpty(value.trim())) {
			throw new UtilsException("Property '"+pKey+"' undefined");
		}
		return value.trim();
	}
	
	public static void addFalsePositives(List<FalsePositive> falsePositives, ClientApi api, String contextId) throws ClientApiException {
		if(!falsePositives.isEmpty()) {
			for (FalsePositive consoleFalsePositive : falsePositives) {
				System.out.println("Registrato falso positivo id:"+consoleFalsePositive.getId()+"; "+consoleFalsePositive.getDescription()+" [Url:"+consoleFalsePositive.getId()+"] [enabled:"+consoleFalsePositive.isEnabled()+"] ");
				api.alertFilter.addAlertFilter(contextId, consoleFalsePositive.getId(), "-1", consoleFalsePositive.getUlrRegExp(), "true", null, consoleFalsePositive.isEnabled()+"");
			}
		}
	}
}
