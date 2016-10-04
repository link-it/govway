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
package org.openspcoop2.security.message.engine;

import org.slf4j.Logger;
import org.openspcoop2.message.XMLUtils;
import org.openspcoop2.message.XPathExpressionEngine;
import org.openspcoop2.security.message.MessageSecurityDigestReader;
import org.openspcoop2.security.message.constants.SecurityType;
import org.openspcoop2.utils.digest.IDigestReader;
import org.openspcoop2.utils.digest.WSSecurityDigestReader;
import org.openspcoop2.utils.digest.XMLSecurityDigestReader;

/**
 * MessageSecurityDigestReader_impl
 *
 * @author Andrea Poli (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class MessageSecurityDigestReader_impl extends MessageSecurityDigestReader {

	public MessageSecurityDigestReader_impl(Logger log) {
		super(log);
	}

	@Override
	public IDigestReader getDigestReader(SecurityType securityType) {
		switch (securityType) {
		case WSSecurity:
			return new WSSecurityDigestReader(this.log,XMLUtils.getInstance(),new XPathExpressionEngine());
		case XMLSecurity:
			return new XMLSecurityDigestReader(this.log,XMLUtils.getInstance(),new XPathExpressionEngine());
		default:
			return null;
		}
	}

}
