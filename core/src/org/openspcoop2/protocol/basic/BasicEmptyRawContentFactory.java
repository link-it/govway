/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2023 Link.it srl (https://link.it).
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

package org.openspcoop2.protocol.basic;

import org.openspcoop2.protocol.basic.builder.BustaBuilder;
import org.openspcoop2.protocol.basic.validator.ValidazioneSintattica;
import org.openspcoop2.protocol.sdk.ProtocolException;
import org.openspcoop2.protocol.sdk.state.IState;

/**
 * BasicEmptyRawContentFactory
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public abstract class BasicEmptyRawContentFactory extends BasicFactory<BasicEmptyRawContent> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	
	/* ** PROTOCOL BUILDER ** */
	
	@Override
	public org.openspcoop2.protocol.sdk.builder.IBustaBuilder<BasicEmptyRawContent> createBustaBuilder(IState state) throws ProtocolException {
		return new BustaBuilder<BasicEmptyRawContent>(this, state);
	}
	
	/* ** PROTOCOL VALIDATOR ** */
	
	@Override
	public org.openspcoop2.protocol.sdk.validator.IValidazioneSintattica<BasicEmptyRawContent> createValidazioneSintattica(IState state) throws ProtocolException {
		return new ValidazioneSintattica<BasicEmptyRawContent>(this, state);
	}
}
