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


package org.openspcoop2.pdd.core.token;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**     
 * TokenForward
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class TokenForward implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private Map<String, String> trasporto = new HashMap<String, String>();
	private Map<String, String> url = new HashMap<String, String>();
	
	public Map<String, String> getTrasporto() {
		return this.trasporto;
	}
	public void setTrasporto(Map<String, String> trasporto) {
		this.trasporto = trasporto;
	}
	public Map<String, String> getUrl() {
		return this.url;
	}
	public void setUrl(Map<String, String> url) {
		this.url = url;
	}
}
