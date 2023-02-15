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

package org.openspcoop2.core.protocolli.trasparente.testsuite.plugin.classes;

import org.openspcoop2.pdd.core.PdDContext;
import org.openspcoop2.pdd.core.integrazione.HeaderIntegrazione;
import org.openspcoop2.pdd.core.integrazione.HeaderIntegrazioneException;
import org.openspcoop2.pdd.core.integrazione.IGestoreIntegrazionePD;
import org.openspcoop2.pdd.core.integrazione.InRequestPDMessage;
import org.openspcoop2.pdd.core.integrazione.InResponsePDMessage;
import org.openspcoop2.pdd.core.integrazione.OutRequestPDMessage;
import org.openspcoop2.pdd.core.integrazione.OutResponsePDMessage;
import org.openspcoop2.protocol.sdk.IProtocolFactory;

/**
* IntegrazionePDCustom
*
* @author Andrea Poli (poli@link.it)
* @author $Author$
* @version $Rev$, $Date$
*/
public class IntegrazionePDCustom implements IGestoreIntegrazionePD  {


	@Override
	public void init(PdDContext pddContext, IProtocolFactory<?> protocolFactory, Object... args) {
		
	}

	@Override
	public void readInRequestHeader(HeaderIntegrazione integrazione, InRequestPDMessage inRequestPDMessage)
			throws HeaderIntegrazioneException {
		
		try {
			String id = Utilities.readIdentificativoTest();
			String tipoTest = "integrazione-in-request-pd";
			Utilities.writeIdentificativoTest(id, tipoTest);
		}catch(Exception e) {
			throw new HeaderIntegrazioneException(e.getMessage(), e);
		}
		
	}

	@Override
	public void setOutRequestHeader(HeaderIntegrazione integrazione, OutRequestPDMessage outRequestPDMessage)
			throws HeaderIntegrazioneException {
		
		try {
			String id = Utilities.readIdentificativoTest();
			String tipoTest = "integrazione-out-request-pd";
			Utilities.writeIdentificativoTest(id, tipoTest);
		}catch(Exception e) {
			throw new HeaderIntegrazioneException(e.getMessage(), e);
		}
		
	}

	@Override
	public void readInResponseHeader(HeaderIntegrazione integrazione, InResponsePDMessage inResponsePDMessage)
			throws HeaderIntegrazioneException {
		
		try {
			String id = Utilities.readIdentificativoTest();
			String tipoTest = "integrazione-in-response-pd";
			Utilities.writeIdentificativoTest(id, tipoTest);
		}catch(Exception e) {
			throw new HeaderIntegrazioneException(e.getMessage(), e);
		}
		
	}

	@Override
	public void setOutResponseHeader(HeaderIntegrazione integrazione, OutResponsePDMessage outResponsePDMessage)
			throws HeaderIntegrazioneException {
		
		try {
			String id = Utilities.readIdentificativoTest();
			String tipoTest = "integrazione-out-response-pd";
			Utilities.writeIdentificativoTest(id, tipoTest);
		}catch(Exception e) {
			throw new HeaderIntegrazioneException(e.getMessage(), e);
		}
		
	}

	

}
