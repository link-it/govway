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


package org.openspcoop2.pdd.core.token;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
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
	
	private Map<String, List<String>> trasporto = new HashMap<>();
	private Map<String, List<String>> url = new HashMap<>();
	
	public Map<String, List<String>> getTrasporto() {
		return this.trasporto;
	}
	public void setTrasporto(Map<String, List<String>> trasporto) {
		this.trasporto = trasporto;
	}
	public Map<String, List<String>> getUrl() {
		return this.url;
	}
	public void setUrl(Map<String, List<String>> url) {
		this.url = url;
	}
}
