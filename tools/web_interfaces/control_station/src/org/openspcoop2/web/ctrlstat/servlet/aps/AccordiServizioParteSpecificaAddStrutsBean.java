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

package org.openspcoop2.web.ctrlstat.servlet.aps;

import java.util.List;
import java.util.Properties;

import org.openspcoop2.core.config.constants.TipoAutenticazionePrincipal;
import org.openspcoop2.message.constants.ServiceBinding;
import org.openspcoop2.protocol.sdk.IProtocolFactory;
import org.openspcoop2.protocol.sdk.constants.ConsoleOperationType;
import org.openspcoop2.protocol.sdk.properties.ConsoleConfiguration;
import org.openspcoop2.protocol.sdk.properties.IConsoleDynamicConfiguration;
import org.openspcoop2.protocol.sdk.properties.ProtocolProperties;
import org.openspcoop2.protocol.sdk.registry.IConfigIntegrationReader;
import org.openspcoop2.protocol.sdk.registry.IRegistryReader;
import org.openspcoop2.web.lib.mvc.BinaryParameter;

/**
 * AccordiServizioParteSpecificaAddStrutsBean
 * 
 * @author Andrea Poli (apoli@link.it)
 * @author Stefano Corallo (corallo@link.it)
 * @author Sandra Giangrandi (sandra@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 * 
 */
public class AccordiServizioParteSpecificaAddStrutsBean {

	protected String connettoreDebug;
	protected String   nomeservizio, tiposervizio, provider, accordo,
	servcorr, endpointtype, tipoconn, url, nome, tipo, user, password, initcont,
	urlpgk, provurl, connfact, sendas, 
	profilo, portType,descrizione,
	httpsurl, httpstipologia, httpspath,
	httpstipo, httpspwd, httpsalgoritmo,
	httpskeystore, httpspwdprivatekeytrust, httpspathkey,
	httpstipokey, httpspwdkey, httpspwdprivatekey,
	httpsalgoritmokey,
	httpsKeyAlias,
	httpsTrustStoreCRLs,
	httpsTrustStoreOCSPPolicy;
	protected String httpshostverifyS, httpsstatoS;
	protected boolean httpshostverify, httpsstato, httpsTrustVerifyCert;
	protected String nomeSoggettoErogatore = "";
	protected String tipoSoggettoErogatore = "";
	String providerSoggettoFruitore = null;
	protected String nomeSoggettoFruitore = "";
	protected String tipoSoggettoFruitore = "";
	protected boolean privato = false;
	protected String statoPackage = "";
	protected String versione;
	protected boolean validazioneDocumenti = true;
	protected boolean decodeRequestValidazioneDocumenti = false;
	protected String editMode = null;
	protected String nomeSA = null;
	protected String oldPortType = null;
	protected String autenticazioneHttp;
	protected Properties parametersPOST;
	protected ServiceBinding serviceBinding = null;
	protected org.openspcoop2.protocol.manifest.constants.InterfaceType formatoSpecifica = null;

	protected boolean autenticazioneToken = false;
	protected String token_policy = null;
	
	protected String proxy_enabled, proxy_hostname,proxy_port,proxy_username,proxy_password;
	
	protected String tempiRisposta_enabled, tempiRisposta_connectionTimeout, tempiRisposta_readTimeout, tempiRisposta_tempoMedioRisposta;

	protected String transfer_mode, transfer_mode_chunk_size, redirect_mode, redirect_max_hop, opzioniAvanzate;

	// file
	protected String requestOutputFileName = null;
	protected String requestOutputFileName_permissions = null;
	protected String requestOutputFileNameHeaders = null;
	protected String requestOutputFileNameHeaders_permissions = null;
	protected String requestOutputParentDirCreateIfNotExists = null;
	protected String requestOutputOverwriteIfExists = null;
	protected String responseInputMode = null;
	protected String responseInputFileName = null;
	protected String responseInputFileNameHeaders = null;
	protected String responseInputDeleteAfterRead = null;
	protected String responseInputWaitTime = null;
	
	// Protocol Properties
	protected IConsoleDynamicConfiguration consoleDynamicConfiguration = null;
	protected ConsoleConfiguration consoleConfiguration =null;
	protected ProtocolProperties protocolProperties = null;
	protected IProtocolFactory<?> protocolFactory= null;
	protected IRegistryReader registryReader = null; 
	protected IConfigIntegrationReader configRegistryReader = null; 
	protected ConsoleOperationType consoleOperationType = null;
	
	protected BinaryParameter wsdlimpler, wsdlimplfru;

	protected String controlloAccessiStato;
	
	protected String erogazioneRuolo;
	protected String erogazioneAutenticazione;
	protected String erogazioneAutenticazioneOpzionale;
	protected TipoAutenticazionePrincipal erogazioneAutenticazionePrincipal;
	protected List<String> erogazioneAutenticazioneParametroList;
	protected String erogazioneAutorizzazione;
	protected String erogazioneAutorizzazioneAutenticati, erogazioneAutorizzazioneRuoli, erogazioneAutorizzazioneRuoliTipologia, erogazioneAutorizzazioneRuoliMatch;
	protected String erogazioneSoggettoAutenticato; 
	
	protected String fruizioneServizioApplicativo;
	protected String fruizioneRuolo;
	protected String fruizioneAutenticazione;
	protected String fruizioneAutenticazioneOpzionale;
	protected TipoAutenticazionePrincipal fruizioneAutenticazionePrincipal;
	protected List<String> fruizioneAutenticazioneParametroList;
	protected String fruizioneAutorizzazione;
	protected String fruizioneAutorizzazioneAutenticati, fruizioneAutorizzazioneRuoli, fruizioneAutorizzazioneRuoliTipologia, fruizioneAutorizzazioneRuoliMatch;
	
	protected String tipoProtocollo;
	
	protected String erogazioneServizioApplicativoServer;
	protected boolean erogazioneServizioApplicativoServerEnabled = false;
	
	protected String canale, canaleStato;
	
}
