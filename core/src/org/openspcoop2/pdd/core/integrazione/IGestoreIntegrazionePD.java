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

package org.openspcoop2.pdd.core.integrazione;

import org.openspcoop2.pdd.core.ICore;


/**
 * Classe utilizzata per la ricezione di informazioni di integrazione 
 * dai servizi applicativi verso la porta di dominio.
 *
 * @author Poli Andrea (apoli@link.it)
 * @author Lorenzo Nardi (nardi@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public interface IGestoreIntegrazionePD extends ICore {

	// IN - Request
	
	public void readInRequestHeader(HeaderIntegrazione integrazione,
			InRequestPDMessage inRequestPDMessage) throws HeaderIntegrazioneException;
	
	// OUT - Request
	
	public void setOutRequestHeader(HeaderIntegrazione integrazione,
			OutRequestPDMessage outRequestPDMessage) throws HeaderIntegrazioneException;
	
	// IN - Response
	
	public void readInResponseHeader(HeaderIntegrazione integrazione,
			InResponsePDMessage inResponsePDMessage) throws HeaderIntegrazioneException;
	
	// OUT - Response
	
	public void setOutResponseHeader(HeaderIntegrazione integrazione,
			OutResponsePDMessage outResponsePDMessage) throws HeaderIntegrazioneException;

}
