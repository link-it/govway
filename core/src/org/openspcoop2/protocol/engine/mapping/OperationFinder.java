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
package org.openspcoop2.protocol.engine.mapping;

import org.slf4j.Logger;
import org.openspcoop2.core.id.IDServizio;
import org.openspcoop2.core.registry.driver.DriverRegistroServiziException;
import org.openspcoop2.core.registry.driver.DriverRegistroServiziNotFound;
import org.openspcoop2.core.registry.wsdl.AccordoServizioWrapper;
import org.openspcoop2.core.registry.wsdl.AccordoServizioWrapperUtilities;
import org.openspcoop2.message.OpenSPCoop2Message;
import org.openspcoop2.protocol.registry.InformationWsdlSource;
import org.openspcoop2.protocol.registry.RegistroServiziManager;

/**
 * OperationFinder
 *
 * @author Andrea Poli (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class OperationFinder {

	public static String searchOperationByRequestMessage(OpenSPCoop2Message msg, RegistroServiziManager registroServiziReader,IDServizio idServizio,Logger log) throws DriverRegistroServiziException, DriverRegistroServiziNotFound{
		return searchOperation(true, msg, registroServiziReader, idServizio, log);
	}
	public static String searchOperationByResponseMessage(OpenSPCoop2Message msg, RegistroServiziManager registroServiziReader,IDServizio idServizio,Logger log) throws DriverRegistroServiziException, DriverRegistroServiziNotFound{
		return searchOperation(false, msg, registroServiziReader, idServizio, log);
	}
	public static String searchOperation(boolean isRichiesta,OpenSPCoop2Message msg, RegistroServiziManager registroServiziReader,IDServizio idServizio,Logger log) throws DriverRegistroServiziException, DriverRegistroServiziNotFound{
		AccordoServizioWrapper wrapper = registroServiziReader.getWsdlAccordoServizio(idServizio,InformationWsdlSource.SAFE_WSDL_REGISTRY,false);
		AccordoServizioWrapperUtilities wrapperUtilities = new AccordoServizioWrapperUtilities(log,wrapper);
		return wrapperUtilities.searchOperationName(isRichiesta, wrapper.getNomePortType(), msg);
	}
	
	public static void checkIDServizioPerRiconoscimentoAzione(IDServizio idServizio, ModalitaIdentificazione modalitaIdentificazione) throws Exception{
		InformazioniServizioURLMapping.checkIDServizioPerRiconoscimentoAzione(idServizio, modalitaIdentificazione);
	}
	
}
