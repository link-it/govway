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

package org.openspcoop2.utils;

import org.slf4j.Logger;

/**	
 * IVersionInfo
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public interface IVersionInfo {
	
	public void init(Logger log, Object ... objects) throws UtilsException;
	
	public String getInfo() throws UtilsException;
		
	public String getErrorMessage();
	
	public String getErrorTitleSuffix();
	
	public String getWarningMessage();
	
	public String getWarningTitleSuffix();
	
	public String getWebSite();
	
	public String getCopyright();
	
	public void set(String info, Logger log, Object ... objects) throws UtilsException;
	
}
