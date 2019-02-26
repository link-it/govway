/*
 * GovWay - A customizable API Gateway 
 * http://www.govway.org
 *
 * from the Link.it OpenSPCoop project codebase
 * 
 * Copyright (c) 2005-2019 Link.it srl (http://link.it). 
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
 * OpenSPCoop2RestMessage
 *
 * @author Andrea Poli <apoli@link.it>
 * @author $Author$
 * @version $Rev$, $Date$
 */

public interface OpenSPCoop2RestMessage<T> extends OpenSPCoop2Message {
	
	/* Elementi REST */
	
	public boolean hasContent() throws MessageException,MessageNotSupportedException;
	
	public T getContent() throws MessageException,MessageNotSupportedException;
	
	public String getContentAsString() throws MessageException,MessageNotSupportedException;
	
	public void updateContent(T content) throws MessageException,MessageNotSupportedException;

	public boolean isProblemDetailsForHttpApis_RFC7807() throws MessageException,MessageNotSupportedException;
	
}
