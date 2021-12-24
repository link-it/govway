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
