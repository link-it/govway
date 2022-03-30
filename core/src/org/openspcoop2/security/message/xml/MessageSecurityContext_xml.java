/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2022 Link.it srl (https://link.it). 
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



package org.openspcoop2.security.message.xml;

import org.openspcoop2.security.SecurityException;
import org.openspcoop2.security.message.IMessageSecurityContext;
import org.openspcoop2.security.message.MessageSecurityContext;
import org.slf4j.Logger;

/**
 * MessageSecurityContext_xml
 *
 * @author Andrea Poli (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class MessageSecurityContext_xml implements IMessageSecurityContext{

	@SuppressWarnings("unused")
	private Logger log = null;

	@Override
	public void init(MessageSecurityContext wssContext) throws SecurityException {

		this.log = wssContext.getLog();
		
	}

}