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

package org.openspcoop2.web.lib.audit.log.utils;

import org.openspcoop2.web.lib.audit.log.Binary;
import org.openspcoop2.web.lib.audit.log.Operation;

/**
 * CleanerOpenSPCoop2Extensions
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class CleanerOpenSPCoop2Extensions {

	public void clean(Operation operationParam, boolean clone){

		Operation operation = null;
		if(clone) {
			operation = (Operation) operationParam.clone();
		}
		else {
			operation = operationParam;
		}
		
		operation.setInterfaceMsg(null);
		if(operation.sizeBinaryList()>0) {
			for (Binary binary : operation.getBinaryList()) {
				binary.setIdOperation(null);
			}
		}
	}

}
