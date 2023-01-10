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


package org.openspcoop2.utils.wadl;

import org.openspcoop2.utils.rest.api.Api;


/**
 * WADLApi
 * 
 * @author Andrea Poli (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 *
 */
public class WADLApi extends Api {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private transient ApplicationWrapper applicationWadlWrapper;
	
	public WADLApi(ApplicationWrapper applicationWadlWrapper){
		this.applicationWadlWrapper = applicationWadlWrapper;
	}
	
	public ApplicationWrapper getApplicationWadlWrapper() {
		return this.applicationWadlWrapper;
	}

	public void setApplicationWadlWrapper(ApplicationWrapper applicationWadlWrapper) {
		this.applicationWadlWrapper = applicationWadlWrapper;
	}
}
