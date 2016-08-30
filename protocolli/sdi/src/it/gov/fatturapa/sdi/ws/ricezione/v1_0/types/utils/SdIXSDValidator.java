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
package it.gov.fatturapa.sdi.ws.ricezione.v1_0.types.utils;

import org.slf4j.Logger;
import org.openspcoop2.utils.xml.AbstractValidatoreXSD;
import org.openspcoop2.generic_project.exception.ServiceException;

import it.gov.fatturapa.sdi.ws.ricezione.v1_0.types.FileSdIConMetadatiType;

/** 
 * XSD Validator    
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */

public class SdIXSDValidator {

	private static org.openspcoop2.generic_project.utils.XSDValidator validator = null;
	
	private static synchronized void initValidator(Class<?> validatorImpl,Logger log) throws ServiceException{
		if(validator==null){
			validator = new org.openspcoop2.generic_project.utils.XSDValidator(log,FileSdIConMetadatiType.class, 
				"/ricezione/RicezioneTypes_v1.0.xsd"
				// elencare in questa posizione altri schemi xsd che vengono inclusi/importati dallo schema /RicezioneTypes_v1.0.xsd
			);
		}
	}
	
	public static AbstractValidatoreXSD getXSDValidator(Class<?> validatorImpl,Logger log) throws ServiceException{
		if(validator==null){
			initValidator(validatorImpl,log);
		}
		return validator.getXsdValidator();
	}
	public static AbstractValidatoreXSD getXSDValidator(Logger log) throws ServiceException{
		if(validator==null){
			initValidator(org.openspcoop2.utils.xml.ValidatoreXSD.class,log);
		}
		return validator.getXsdValidator();
	}
	
}
