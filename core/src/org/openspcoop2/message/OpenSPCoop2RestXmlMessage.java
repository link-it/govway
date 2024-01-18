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
import org.w3c.dom.Element;

/**
 * OpenSPCoop2RestXmlMessage
 *
 * @author Andrea Poli (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */

public interface OpenSPCoop2RestXmlMessage extends OpenSPCoop2RestMessage<Element> {
	
	public void addElement(String name, String value)  throws MessageException,MessageNotSupportedException;
	public void addElement(String name, String namespace, String value)  throws MessageException,MessageNotSupportedException;
	
	public void addElementIn(String pattern, String name, String value)  throws MessageException,MessageNotSupportedException;
	public void addElementIn(String pattern, String name, String namespace, String value)  throws MessageException,MessageNotSupportedException;
	
	public void removeElement(String name) throws MessageException,MessageNotSupportedException;
	public void removeElement(String name, String namespace) throws MessageException,MessageNotSupportedException;
	
	public void removeElementIn(String pattern, String name) throws MessageException,MessageNotSupportedException;
	public void removeElementIn(String pattern, String name, String namespace) throws MessageException,MessageNotSupportedException;
	
}
