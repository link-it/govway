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


package org.openspcoop2.pdd.core.handlers;

import org.slf4j.Logger;
import org.openspcoop2.pdd.core.PdDContext;

/**
 * PreInRequestContext
 *
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class InitContext {

	/** Logger */
	private Logger logCore;
	/** Logger */
	private Logger logConsole;
	/** PdDContext */
	private PdDContext pddContext;
	
	public Logger getLogCore() {
		return this.logCore;
	}

	public void setLogCore(Logger logCore) {
		this.logCore = logCore;
	}
		
	public PdDContext getPddContext() {
		return this.pddContext;
	}

	public void setPddContext(PdDContext pddContext) {
		this.pddContext = pddContext;
	}

	public Logger getLogConsole() {
		return this.logConsole;
	}

	public void setLogConsole(Logger logConsole) {
		this.logConsole = logConsole;
	}
}
