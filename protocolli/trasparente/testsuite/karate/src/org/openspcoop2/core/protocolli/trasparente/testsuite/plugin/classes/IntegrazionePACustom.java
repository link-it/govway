/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2024 Link.it srl (https://link.it). 
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
import org.openspcoop2.pdd.core.integrazione.IGestoreIntegrazionePA;
import org.openspcoop2.pdd.core.integrazione.InRequestPAMessage;
import org.openspcoop2.pdd.core.integrazione.InResponsePAMessage;
import org.openspcoop2.pdd.core.integrazione.OutRequestPAMessage;
import org.openspcoop2.pdd.core.integrazione.OutResponsePAMessage;
import org.openspcoop2.protocol.sdk.IProtocolFactory;

/**
* IntegrazionePACustom
*
* @author Andrea Poli (poli@link.it)
* @author $Author$
* @version $Rev$, $Date$
*/
public class IntegrazionePACustom implements IGestoreIntegrazionePA  {


	@Override
	public void init(PdDContext pddContext, IProtocolFactory<?> protocolFactory, Object... args) {
		
	}

	@Override
	public void readInRequestHeader(HeaderIntegrazione integrazione, InRequestPAMessage inRequestPAMessage)
			throws HeaderIntegrazioneException {
		
		try {
			String id = Utilities.readIdentificativoTest();
			String tipoTest = "integrazione-in-request-pa";
			Utilities.writeIdentificativoTest(id, tipoTest);
		}catch(Exception e) {
			throw new HeaderIntegrazioneException(e.getMessage(), e);
		}
		
	}

	@Override
	public void setOutRequestHeader(HeaderIntegrazione integrazione, OutRequestPAMessage outRequestPAMessage)
			throws HeaderIntegrazioneException {
		
		try {
			String id = Utilities.readIdentificativoTest();
			String tipoTest = "integrazione-out-request-pa";
			Utilities.writeIdentificativoTest(id, tipoTest);
		}catch(Exception e) {
			throw new HeaderIntegrazioneException(e.getMessage(), e);
		}
		
	}

	@Override
	public void readInResponseHeader(HeaderIntegrazione integrazione, InResponsePAMessage inResponsePAMessage)
			throws HeaderIntegrazioneException {
		
		try {
			String id = Utilities.readIdentificativoTest();
			String tipoTest = "integrazione-in-response-pa";
			Utilities.writeIdentificativoTest(id, tipoTest);
		}catch(Exception e) {
			throw new HeaderIntegrazioneException(e.getMessage(), e);
		}
		
	}

	@Override
	public void setOutResponseHeader(HeaderIntegrazione integrazione, OutResponsePAMessage outResponsePAMessage)
			throws HeaderIntegrazioneException {
		
		try {
			String id = Utilities.readIdentificativoTest();
			String tipoTest = "integrazione-out-response-pa";
			Utilities.writeIdentificativoTest(id, tipoTest);
		}catch(Exception e) {
			throw new HeaderIntegrazioneException(e.getMessage(), e);
		}
		
	}

}
