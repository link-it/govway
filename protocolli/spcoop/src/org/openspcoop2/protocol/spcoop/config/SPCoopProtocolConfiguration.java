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

package org.openspcoop2.protocol.spcoop.config;

import org.openspcoop2.core.constants.TipoPdD;
import org.openspcoop2.protocol.basic.config.BasicConfiguration;
import org.openspcoop2.protocol.sdk.IProtocolFactory;
import org.openspcoop2.protocol.sdk.ProtocolException;
import org.openspcoop2.protocol.sdk.constants.FunzionalitaProtocollo;
import org.openspcoop2.protocol.sdk.constants.InitialIdConversationType;

/**
 * Classe che implementa, in base al protocollo SPCoop, l'interfaccia {@link org.openspcoop2.protocol.sdk.config.IProtocolConfiguration} 
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class SPCoopProtocolConfiguration extends BasicConfiguration {

	public SPCoopProtocolConfiguration(IProtocolFactory<?> factory) throws ProtocolException {
		super(factory);
	}
	
	@Override
	public InitialIdConversationType isGenerateInitialIdConversation(TipoPdD tipoPdD, FunzionalitaProtocollo funzionalitaProtocollo) throws ProtocolException{
		if(TipoPdD.DELEGATA.equals(tipoPdD)) {
			return InitialIdConversationType.ID_MESSAGGIO;
		}
		else {
			return InitialIdConversationType.DISABILITATO;
		}
	}
	
	@Override
	public boolean isDataPresenteInIdentificativoMessaggio() {
		return true;
	}
	
}
