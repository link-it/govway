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

package org.openspcoop2.web.ctrlstat.servlet.apc;

import org.openspcoop2.message.constants.MessageType;
import org.openspcoop2.message.constants.ServiceBinding;
import org.openspcoop2.protocol.manifest.constants.InterfaceType;
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
public class AccordiServizioParteComuneAddStrutsBean {

	protected String nome, descr, profcoll, 
	 filtrodup, confric, idcoll, idRifRichiesta, consord, scadenza,
	referente,versione,accordoCooperazione;
	protected boolean privato, isServizioComposto;// showPrivato;
	protected String statoPackage;
	protected String tipoAccordo;
	protected boolean validazioneDocumenti = true;
	protected boolean decodeRequestValidazioneDocumenti = false;

	protected ServiceBinding serviceBinding = null;
	protected MessageType messageType = null;
	protected InterfaceType interfaceType = null;
	
	protected String tipoProtocollo;
	// Non utilizzato veniva sempre impostato false, lo lascio commentato in questo punto perche' veniva passato nella decode della richiesta multipart... 
	//	private boolean isBackwardCompatibilityAccordo11 = false;

	protected String editMode = null;
	// Protocol Properties
	protected IConsoleDynamicConfiguration consoleDynamicConfiguration = null;
	protected ConsoleConfiguration consoleConfiguration =null;
	protected ProtocolProperties protocolProperties = null;
	protected IProtocolFactory<?> protocolFactory = null;
	protected IRegistryReader registryReader = null; 
	protected IConfigIntegrationReader configRegistryReader = null; 
	protected ConsoleOperationType consoleOperationType = null;
	
	protected String gruppi, canale, canaleStato;
	
	protected boolean nuovaVersione;

	protected BinaryParameter wsdlservcorr, wsdldef, wsdlserv, wsdlconc, wsblconc, wsblserv, wsblservcorr;
	
}
