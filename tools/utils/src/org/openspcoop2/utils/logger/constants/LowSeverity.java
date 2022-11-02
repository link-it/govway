/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2022 Link.it srl (https://link.it).
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
package org.openspcoop2.utils.logger.constants;

import java.io.Serializable;

/**
 * LowSeverity
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public enum LowSeverity implements Serializable {

	DEBUG_HIGH, DEBUG_MEDIUM, DEBUG_LOW;
	
	public Severity toSeverity(){
		if(this.name().equals(Severity.DEBUG_LOW.name())){
			return Severity.DEBUG_LOW;
		}
		else if(this.name().equals(Severity.DEBUG_MEDIUM.name())){
			return Severity.DEBUG_MEDIUM;
		}
		else{
			return Severity.DEBUG_HIGH;
		}
	}
	
}
