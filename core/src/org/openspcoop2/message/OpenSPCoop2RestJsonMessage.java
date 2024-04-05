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

package org.openspcoop2.message;

import org.openspcoop2.message.exception.MessageException;
import org.openspcoop2.message.exception.MessageNotSupportedException;

/**
 * OpenSPCoop2RestJsonMessage
 *
 * @author Andrea Poli (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */

public interface OpenSPCoop2RestJsonMessage extends OpenSPCoop2RestMessage<String> {
	
	public void prettyFormatContent() throws MessageException,MessageNotSupportedException;
	
	public void addSimpleElement(String name, Object value) throws MessageException,MessageNotSupportedException;
	public void addSimpleElement(String jsonPath, String name, Object value) throws MessageException,MessageNotSupportedException;
	public void addObjectElement(String name, Object value) throws MessageException,MessageNotSupportedException;
	public void addObjectElement(String jsonPath, String name, Object value) throws MessageException,MessageNotSupportedException;
	public void addArrayElement(String name, Object value) throws MessageException,MessageNotSupportedException;
	public void addArrayElement(String jsonPath, String name, Object value) throws MessageException,MessageNotSupportedException;

	public void removeElement(String name) throws MessageException,MessageNotSupportedException;
	public void removeElement(String jsonPath, String name) throws MessageException,MessageNotSupportedException;
	
	public void replaceValue(String name, Object value) throws MessageException,MessageNotSupportedException;
	public void replaceValue(String jsonPath, String name, Object value) throws MessageException,MessageNotSupportedException;
	
}
