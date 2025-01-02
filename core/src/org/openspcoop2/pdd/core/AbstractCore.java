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

package org.openspcoop2.pdd.core;

import org.openspcoop2.protocol.sdk.IProtocolFactory;

/**
 * AbstractCore
 *
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public abstract class AbstractCore implements ICore {

	private PdDContext pddContext;
	private IProtocolFactory<?> protocolFactory;
	private Object[] args = null;
	
	@Override
	public void init(PdDContext pddContext,IProtocolFactory<?> protocolFactory,Object ... args) {
		this.pddContext = pddContext;
		this.protocolFactory = protocolFactory;
		this.args = args;
	}

	
	public PdDContext getPddContext() {
		return this.pddContext;
	}

	public void setPddContext(PdDContext pddContext) {
		this.pddContext = pddContext;
	}

	public IProtocolFactory<?> getProtocolFactory() {
		return this.protocolFactory;
	}

	public void setProtocolFactory(IProtocolFactory<?> protocolFactory) {
		this.protocolFactory = protocolFactory;
	}

	public Object[] getArgs() {
		return this.args;
	}

	public void setArgs(Object[] args) {
		this.args = args;
	}
	
	
	public static void init(ICore core,PdDContext pddContext,IProtocolFactory<?> protocolFactory,Object ... args){
		if(core==null) {
			throw new RuntimeException("Param core is null");
		}
		core.init(pddContext, protocolFactory, args);
	}
}
