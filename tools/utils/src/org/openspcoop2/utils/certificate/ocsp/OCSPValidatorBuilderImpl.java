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
package org.openspcoop2.utils.certificate.ocsp;

import org.openspcoop2.utils.LoggerBuffer;
import org.openspcoop2.utils.UtilsException;
import org.openspcoop2.utils.transport.http.IOCSPValidator;
import org.openspcoop2.utils.transport.http.IOCSPValidatorBuilder;
import org.slf4j.Logger;

/**
 * OCSPValidatorBuilderImpl
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class OCSPValidatorBuilderImpl implements IOCSPValidatorBuilder {

	public OCSPValidatorBuilderImpl() {
		// usato in org.openspcoop2.utils.transport.http.HttpUtilities tramite reflections
	}
	
	@Override
	public IOCSPValidator newInstance(Logger log, java.security.KeyStore truststore, String crlPath, String ocspPolicy) throws UtilsException{
		return newInstance(log, new org.openspcoop2.utils.certificate.KeyStore(truststore), crlPath, ocspPolicy);
	}
	@Override
	public IOCSPValidator newInstance(Logger log, org.openspcoop2.utils.certificate.KeyStore truststore, String crlPath, String ocspPolicy) throws UtilsException {
		LoggerBuffer lb = new LoggerBuffer();
		lb.setLogDebug(log);
		lb.setLogError(log);
		IOCSPResourceReader ocspResourceReader = new OCSPResourceReader();
		return new OCSPValidatorImpl(lb, truststore, crlPath, ocspPolicy, ocspResourceReader);
	}

	
}
