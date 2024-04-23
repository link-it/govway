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

package org.openspcoop2.web.ctrlstat.servlet.aps;

import java.util.List;

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
	protected String   nomeservizio;
	protected String tiposervizio;
	protected String provider;
	protected String accordo;
	protected String servcorr;
	protected String endpointtype;
	protected String tipoconn;
	protected String url;
	protected String nome;
	protected String tipo;
	protected String user;
	protected String password;
	protected String initcont;
	protected String urlpgk;
	protected String provurl;
	protected String connfact;
	protected String sendas;
	protected String profilo;
	protected String portType;
	protected String descrizione;
	protected String httpsurl;
	protected String httpstipologia;
	protected String httpspath;
	protected String httpstipo;
	protected String httpspwd;
	protected String httpsalgoritmo;
	protected String httpskeystore;
	protected String httpspwdprivatekeytrust;
	protected String httpspathkey;
	protected String httpstipokey;
	protected String httpspwdkey;
	protected String httpspwdprivatekey;
	protected String httpsalgoritmokey;
	protected String httpsKeyAlias;
	protected String httpsTrustStoreCRLs;
	protected String httpsTrustStoreOCSPPolicy;
	protected String httpshostverifyS;
	protected String httpsstatoS;
	protected boolean httpshostverify;
	protected boolean httpsstato;
	protected boolean httpsTrustVerifyCert;
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
	protected ServiceBinding serviceBinding = null;
	protected org.openspcoop2.protocol.manifest.constants.InterfaceType formatoSpecifica = null;

	protected boolean autenticazioneToken = false;
	protected String tokenPolicy = null;
	
	protected String autenticazioneApiKey = null;
	protected boolean useOAS3Names=true;
	protected boolean useAppId=false;
	protected String apiKeyHeader = null;
	protected String apiKeyValue = null;
	protected String appIdHeader = null;
	protected String appIdValue = null;
	
	protected String proxyEnabled;
	protected String proxyHostname;
	protected String proxyPort;
	protected String proxyUsername;
	protected String proxyPassword;
	
	protected String tempiRispostaEnabled;
	protected String tempiRispostaConnectionTimeout;
	protected String tempiRispostaReadTimeout;
	protected String tempiRispostaTempoMedioRisposta;

	protected String transferMode;
	protected String transferModeChunkSize;
	protected String redirectMode;
	protected String redirectMaxHop;
	protected String opzioniAvanzate;

	// file
	protected String requestOutputFileName = null;
	protected String requestOutputFileNamePermissions = null;
	protected String requestOutputFileNameHeaders = null;
	protected String requestOutputFileNameHeadersPermissions = null;
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
	
	protected BinaryParameter wsdlimpler;
	protected BinaryParameter wsdlimplfru;

	protected String controlloAccessiStato;
	
	protected String erogazioneRuolo;
	protected String erogazioneAutenticazione;
	protected String erogazioneAutenticazioneOpzionale;
	protected TipoAutenticazionePrincipal erogazioneAutenticazionePrincipal;
	protected List<String> erogazioneAutenticazioneParametroList;
	protected String erogazioneAutorizzazione;
	protected String erogazioneAutorizzazioneAutenticati;
	protected String erogazioneAutorizzazioneRuoli;
	protected String erogazioneAutorizzazioneRuoliTipologia;
	protected String erogazioneAutorizzazioneRuoliMatch;
	protected String erogazioneSoggettoAutenticato; 
	
	protected String fruizioneServizioApplicativo;
	protected String fruizioneRuolo;
	protected String fruizioneAutenticazione;
	protected String fruizioneAutenticazioneOpzionale;
	protected TipoAutenticazionePrincipal fruizioneAutenticazionePrincipal;
	protected List<String> fruizioneAutenticazioneParametroList;
	protected String fruizioneAutorizzazione;
	protected String fruizioneAutorizzazioneAutenticati;
	protected String fruizioneAutorizzazioneRuoli;
	protected String fruizioneAutorizzazioneRuoliTipologia;
	protected String fruizioneAutorizzazioneRuoliMatch;
	
	protected String tipoProtocollo;
	
	protected String erogazioneServizioApplicativoServer;
	protected boolean erogazioneServizioApplicativoServerEnabled = false;
	
	protected String canale;
	protected String canaleStato;
	
}
