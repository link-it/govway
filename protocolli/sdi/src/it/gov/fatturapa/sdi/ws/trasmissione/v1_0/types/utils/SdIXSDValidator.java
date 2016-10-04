/*
 * OpenSPCoop - Customizable API Gateway 
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
package it.gov.fatturapa.sdi.ws.trasmissione.v1_0.types.utils;

import org.slf4j.Logger;
import org.openspcoop2.utils.xml.AbstractValidatoreXSD;
import org.openspcoop2.generic_project.exception.ServiceException;

import it.gov.fatturapa.sdi.ws.trasmissione.v1_0.types.FileSdIBaseType;

/** 
 * XSD Validator    
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */

public class SdIXSDValidator {

	private static org.openspcoop2.generic_project.utils.XSDValidator validator_v1_0 = null;
	private static org.openspcoop2.generic_project.utils.XSDValidator validator_v1_1 = null;
	
	private static synchronized void initValidatorTrasmissione_1_0(Class<?> validatorImpl,Logger log) throws ServiceException{
		if(validator_v1_0==null){
			validator_v1_0 = new org.openspcoop2.generic_project.utils.XSDValidator(log,FileSdIBaseType.class, 
				"/trasmissione/TrasmissioneTypes_v1.0.xsd"
				// elencare in questa posizione altri schemi xsd che vengono inclusi/importati dallo schema /TrasmissioneTypes_mergev_1.0-1.1.xsd
			);
		}
	}
	private static synchronized void initValidatorTrasmissione_1_1(Class<?> validatorImpl,Logger log) throws ServiceException{
		if(validator_v1_1==null){
			validator_v1_1 = new org.openspcoop2.generic_project.utils.XSDValidator(log,FileSdIBaseType.class, 
				"/trasmissione/TrasmissioneTypes_v1.1.xsd"
				// elencare in questa posizione altri schemi xsd che vengono inclusi/importati dallo schema /TrasmissioneTypes_mergev_1.0-1.1.xsd
			);
		}
	}
	
	public static AbstractValidatoreXSD getXSDValidatorTrasmissione_1_0(Class<?> validatorImpl,Logger log) throws ServiceException{
		if(validator_v1_0==null){
			initValidatorTrasmissione_1_0(validatorImpl,log);
		}
		return validator_v1_0.getXsdValidator();
	}
	public static AbstractValidatoreXSD getXSDValidatorTrasmissione_1_0(Logger log) throws ServiceException{
		if(validator_v1_0==null){
			initValidatorTrasmissione_1_0(org.openspcoop2.utils.xml.ValidatoreXSD.class,log);
		}
		return validator_v1_0.getXsdValidator();
	}
	
	public static AbstractValidatoreXSD getXSDValidatorTrasmissione_1_1(Class<?> validatorImpl,Logger log) throws ServiceException{
		if(validator_v1_1==null){
			initValidatorTrasmissione_1_1(validatorImpl,log);
		}
		return validator_v1_1.getXsdValidator();
	}
	public static AbstractValidatoreXSD getXSDValidatorTrasmissione_1_1(Logger log) throws ServiceException{
		if(validator_v1_1==null){
			initValidatorTrasmissione_1_1(org.openspcoop2.utils.xml.ValidatoreXSD.class,log);
		}
		return validator_v1_1.getXsdValidator();
	}
	
}
