/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2025 Link.it srl (https://link.it). 
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

package org.openspcoop2.security.message;

import org.openspcoop2.message.OpenSPCoop2MessageFactory;
import org.openspcoop2.security.SecurityException;
import org.openspcoop2.utils.digest.IDigestReader;

/**
 * IMessageSecurityReceiver
 *
 * @author Andrea Poli (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public interface IMessageSecurityDigest {

	/**
	 * Ritorna un reader che permette di leggere i digest
	 * 
	 * @return digestReader
	 * @throws SecurityException
	 */
	public IDigestReader getDigestReader(OpenSPCoop2MessageFactory messageFactory, MessageSecurityContext messageSecurityContext) throws SecurityException;
	
}
