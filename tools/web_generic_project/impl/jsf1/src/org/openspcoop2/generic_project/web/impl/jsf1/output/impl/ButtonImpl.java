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
package org.openspcoop2.generic_project.web.impl.jsf1.output.impl;

import org.openspcoop2.generic_project.web.output.Button;
import org.openspcoop2.generic_project.web.output.OutputType;

/**
 * Implementazione di un elemento di output di tipo Button.
 * 
 * @author Pintori Giuliano (pintori@link.it)
 *  @author $Author$
 * @version $Rev$, $Date$ 
 * 
 */
public class ButtonImpl extends ImageImpl implements Button{

	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public ButtonImpl(){
		super();
		this.setType(OutputType.BUTTON);
		this.setInsideGroup(false);
	}
	private String href;


	@Override
	public String getHref() {
		return  this.href;
	}

	@Override
	public void setHref(String href) {
		this.href = href;

	}

}
