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

package org.openspcoop2.web.ctrlstat.servlet.apc;

import org.openspcoop2.protocol.sdk.IProtocolFactory;
import org.openspcoop2.protocol.sdk.constants.ConsoleOperationType;
import org.openspcoop2.protocol.sdk.properties.ConsoleConfiguration;
import org.openspcoop2.protocol.sdk.properties.IConsoleDynamicConfiguration;
import org.openspcoop2.protocol.sdk.properties.ProtocolProperties;
import org.openspcoop2.protocol.sdk.registry.IConfigIntegrationReader;
import org.openspcoop2.protocol.sdk.registry.IRegistryReader;
import org.openspcoop2.web.lib.mvc.BinaryParameter;

/**
 * AccordiServizioParteComuneAddStrutsBean
 * 
 * @author Andrea Poli (apoli@link.it)
 * @author Stefano Corallo (corallo@link.it)
 * @author Sandra Giangrandi (sandra@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 * 
 */
public class AccordiServizioParteComuneWSDLChangeStrutsBean {

	protected String id, tipo, wsdl,tipoAccordo;
	protected boolean validazioneDocumenti = true;
	protected boolean decodeRequestValidazioneDocumenti = false;
	protected String editMode = null;
	protected BinaryParameter wsdlservcorr, wsdldef, wsdlserv, wsdlconc, wsblconc, wsblserv, wsblservcorr;
	
	protected boolean aggiornaEsistenti = true;
	protected boolean decodeRequestAggiornaEsistenti= false;
	
	protected boolean eliminaNonPresentiNuovaInterfaccia = true;
	protected boolean decodeRequestEliminaNonPresentiNuovaInterfaccia= false;
	
	// Protocol Properties
	protected IConsoleDynamicConfiguration consoleDynamicConfiguration = null;
	protected ConsoleConfiguration consoleConfiguration =null;
	protected ProtocolProperties protocolProperties = null;
	protected IProtocolFactory<?> protocolFactory= null;
	protected IRegistryReader registryReader = null; 
	protected IConfigIntegrationReader configRegistryReader = null; 
	protected ConsoleOperationType consoleOperationType = null;
	
}
