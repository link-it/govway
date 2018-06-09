/*
 * OpenSPCoop - Customizable API Gateway 
 * http://www.openspcoop2.org
 * 
 * Copyright (c) 2005-2018 Link.it srl (http://link.it). 
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
package org.openspcoop2.security.message.jose;

import org.openspcoop2.security.SecurityException;
import org.openspcoop2.security.message.IMessageSecurityDigest;
import org.openspcoop2.security.message.MessageSecurityContext;
import org.openspcoop2.utils.digest.IDigestReader;

/**
 * MessageSecurityDigest_jose
 *
 * @author Andrea Poli (apoli@link.it)
 * @author $Author: apoli $
 * @version $Rev: 13574 $, $Date: 2018-01-26 06:24:34 -0500 (Fri, 26 Jan 2018) $
 */
public class MessageSecurityDigest_jose implements IMessageSecurityDigest{

	@Override
	public IDigestReader getDigestReader(MessageSecurityContext messageSecurityContext) throws SecurityException {
		return null;
	}
	
}
