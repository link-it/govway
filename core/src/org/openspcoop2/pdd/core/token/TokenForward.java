/*
 * GovWay - A customizable API Gateway 
 * http://www.govway.org
 *
 * from the Link.it OpenSPCoop project codebase
 * 
 * Copyright (c) 2005-2019 Link.it srl (http://link.it).
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
import java.util.Properties;

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
	
	private Properties trasporto = new Properties();
	private Properties url = new Properties();
	
	public Properties getTrasporto() {
		return this.trasporto;
	}
	public void setTrasporto(Properties trasporto) {
		this.trasporto = trasporto;
	}
	public Properties getUrl() {
		return this.url;
	}
	public void setUrl(Properties url) {
		this.url = url;
	}
}
