/*
 * OpenSPCoop v2 - Customizable SOAP Message Broker 
 * http://www.openspcoop2.org
 * 
 * Copyright (c) 2005-2016 Link.it srl (http://link.it).
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
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

/**
 * Severity
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public enum Severity {

	DEBUG_HIGH, DEBUG_MEDIUM, DEBUG_LOW, INFO, WARN, ERROR, FATAL;  
	
	public int intValue(){
		switch (this) {
		case FATAL:
			return 0; 
		case ERROR:
			return 1; 
		case WARN:
			return 2; 
		case INFO:
			return 3; 
		case DEBUG_LOW:
			return 4; 
		case DEBUG_MEDIUM:
			return 5; 
		case DEBUG_HIGH:
			return 6; 
		}
		return -1;
	}
	
}
