/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2020 Link.it srl (https://link.it). 
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
package org.openspcoop2.security.message.utils;

import org.openspcoop2.message.OpenSPCoop2SoapMessage;

/**
 * ProcessingPart
 *
 * @author Andrea Poli (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public abstract class ProcessingPart<INPUT, OUTPUT> {

	protected INPUT part;
	
	protected boolean content;
	
	public INPUT getPart() {
		return this.part;
	}
	public void setPart(INPUT part) {
		this.part = part;
	}
	
	public abstract OUTPUT getOutput(OpenSPCoop2SoapMessage msg) throws Exception;
	
	public boolean isContent() {
		return this.content;
	}
	public void setContent(boolean content) {
		this.content = content;
	}
}
