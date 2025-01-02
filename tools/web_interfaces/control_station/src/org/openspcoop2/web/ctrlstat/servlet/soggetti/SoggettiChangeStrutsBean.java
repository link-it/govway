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

package org.openspcoop2.web.ctrlstat.servlet.soggetti;

import java.text.SimpleDateFormat;

import org.openspcoop2.protocol.sdk.IProtocolFactory;
import org.openspcoop2.protocol.sdk.constants.ConsoleOperationType;
import org.openspcoop2.protocol.sdk.properties.ConsoleConfiguration;
import org.openspcoop2.protocol.sdk.properties.IConsoleDynamicConfiguration;
import org.openspcoop2.protocol.sdk.properties.ProtocolProperties;
import org.openspcoop2.protocol.sdk.registry.IConfigIntegrationReader;
import org.openspcoop2.protocol.sdk.registry.IRegistryReader;

/**
 * SoggettiChangeStrutsBean
 * 
 * @author Andrea Poli (apoli@link.it)
 * @author Stefano Corallo (corallo@link.it)
 * @author Sandra Giangrandi (sandra@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 * 
 */
public class SoggettiChangeStrutsBean {

	protected String editMode = null;
	protected String id, nomeprov , tipoprov, portadom, descr, versioneProtocollo,pdd, codiceIpa, pd_url_prefix_rewriter,pa_url_prefix_rewriter,protocollo,dominio;
	protected boolean isRouter,privato; 

	// Protocol Properties
	protected IConsoleDynamicConfiguration consoleDynamicConfiguration = null;
	protected ConsoleConfiguration consoleConfiguration =null;
	protected ProtocolProperties protocolProperties = null;
	protected IProtocolFactory<?> protocolFactory= null;
	protected IRegistryReader registryReader = null; 
	protected IConfigIntegrationReader configRegistryReader = null; 
	protected ConsoleOperationType consoleOperationType = null;
	protected String protocolPropertiesSet = null;

	protected String tipoauthSoggetto = null;
	protected String utenteSoggetto = null;
	protected String passwordSoggetto = null;
	protected String subjectSoggetto = null;
	protected String principalSoggetto = null;
	
	protected String modificaOperativo = null;
	
	protected static final String CERTIFICATE_FORMAT = "dd/MM/yyyy HH:mm:SS";
	protected SimpleDateFormat sdf = new SimpleDateFormat(CERTIFICATE_FORMAT);
	
}
