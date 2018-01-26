/*
 * OpenSPCoop - Customizable API Gateway 
 * http://www.openspcoop2.org
 * 
 * Copyright (c) 2005-2018 Link.it srl (http://link.it). 
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

package org.openspcoop2.utils.rest;

import org.openspcoop2.utils.rest.api.Api;
import org.openspcoop2.utils.rest.entity.HttpBaseEntity;
import org.slf4j.Logger;

/**
 * IApiValidator
 *
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public interface IApiValidator {

	public void init(Logger log, Api api, ApiValidatorConfig config) throws ProcessingException;
	
	public void validate(HttpBaseEntity<?> httpEntity) throws ProcessingException, ValidatorException;
		
}
