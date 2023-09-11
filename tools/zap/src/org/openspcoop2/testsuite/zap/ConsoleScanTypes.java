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
package org.openspcoop2.testsuite.zap;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.openspcoop2.utils.UtilsException;

/**
 * ConsoleScanTypes
 *
 * @author Andrea Poli (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ConsoleScanTypes {

	private boolean spider = false;
	private boolean ajaxspider = false;
	private boolean active = false;
	
	public ConsoleScanTypes(String[] args, String usageMsg, int index) throws UtilsException {
		String scanTypes = null;
		if(args.length>(index+3)) {
			scanTypes = args[index+3];
		}
		if(scanTypes==null || StringUtils.isEmpty(scanTypes)) {
			throw new UtilsException("ERROR: argument 'scanTypes' undefined"+usageMsg);
		}
		/**LoggerManager.info("scanTypes: "+scanTypes);*/
		String [] split = scanTypes.split("\\|");
		
		init(scanTypes, split, usageMsg);
	}
	
	private void init(String scanTypes, String [] split, String usageMsg) throws UtilsException {
		List<String> scanTypesSupported = getAllScanTypes();
		if(split!=null && split.length>0) {
			for (String s : split) {
				if(!scanTypesSupported.contains(s)) {
					throw new UtilsException("ERROR: argument 'scanTypes'='"+scanTypes+"' unknown confidence '"+s+"' ; usable: "+scanTypesSupported+usageMsg);
				}
				LoggerManager.info("Scan '"+s+"' enabled");
				if(SCAN_TYPE_SPIDER.equalsIgnoreCase(s)) {
					this.spider=true;
				}
				else if(SCAN_TYPE_ACTIVE.equalsIgnoreCase(s)) {
					this.active=true;
				}
				if(SCAN_TYPE_AJAXSPIDER.equalsIgnoreCase(s)) {
					this.ajaxspider=true;
				}
			}
		}
	}
	
	public static final String SCAN_TYPE = "SCAN_TYPE";
	public static final String SCAN_TYPE_SPIDER = "spider";
	public static final String SCAN_TYPE_ACTIVE = "active";
	public static final String SCAN_TYPE_AJAXSPIDER = "ajaxspider";
	public static List<String> getAllScanTypes(){
		List<String> l = new ArrayList<>();
		l.add(SCAN_TYPE_SPIDER);
		l.add(SCAN_TYPE_ACTIVE);
		l.add(SCAN_TYPE_AJAXSPIDER);
		return l;
	}
	
	public boolean isSpider() {
		return this.spider;
	}
	public boolean isAjaxspider() {
		return this.ajaxspider;
	}
	public boolean isActive() {
		return this.active;
	}
}
