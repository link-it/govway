/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2020 Link.it srl (https://link.it). 
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

package org.openspcoop2.message;

import java.io.Serializable;
import java.util.List;

/**
 * ForwardHeaderConfig
 *
 * @author Andrea Poli (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */

public class ForwardConfig implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private List<String> blackList;
	private List<String> whiteList;
	private boolean forwardEnable;
	
	public boolean isForwardEnable() {
		return this.forwardEnable;
	}
	public void setForwardEnable(boolean forwardEnable) {
		this.forwardEnable = forwardEnable;
	}
	public List<String> getBlackList() {
		return this.blackList;
	}
	public void setBlackList(List<String> blackList) {
		this.blackList = blackList;
	}
	public List<String> getWhiteList() {
		return this.whiteList;
	}
	public void setWhiteList(List<String> whiteList) {
		this.whiteList = whiteList;
	}
}
