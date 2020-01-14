/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2020 Link.it srl (https://link.it). 
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

package org.openspcoop2.protocol.sdk;

import java.io.Serializable;

import org.slf4j.Logger;
import org.openspcoop2.core.config.driver.IDriverConfigurazioneGet;
import org.openspcoop2.core.registry.driver.IDriverRegistroServiziGet;
import org.openspcoop2.protocol.manifest.Openspcoop2;
import org.openspcoop2.protocol.sdk.archive.IArchive;
import org.openspcoop2.protocol.sdk.builder.IErroreApplicativoBuilder;
import org.openspcoop2.protocol.sdk.builder.IEsitoBuilder;
import org.openspcoop2.protocol.sdk.builder.IBustaBuilder;
import org.openspcoop2.protocol.sdk.config.IProtocolConfiguration;
import org.openspcoop2.protocol.sdk.config.IProtocolIntegrationConfiguration;
import org.openspcoop2.protocol.sdk.config.IProtocolManager;
import org.openspcoop2.protocol.sdk.config.IProtocolVersionManager;
import org.openspcoop2.protocol.sdk.config.ITraduttore;
import org.openspcoop2.protocol.sdk.diagnostica.IDiagnosticDriver;
import org.openspcoop2.protocol.sdk.diagnostica.IDiagnosticProducer;
import org.openspcoop2.protocol.sdk.diagnostica.IDiagnosticSerializer;
import org.openspcoop2.protocol.sdk.dump.IDumpProducer;
import org.openspcoop2.protocol.sdk.properties.IConsoleDynamicConfiguration;
import org.openspcoop2.protocol.sdk.registry.IConfigIntegrationReader;
import org.openspcoop2.protocol.sdk.registry.IRegistryReader;
import org.openspcoop2.protocol.sdk.state.IState;
import org.openspcoop2.protocol.sdk.tracciamento.ITracciaDriver;
import org.openspcoop2.protocol.sdk.tracciamento.ITracciaProducer;
import org.openspcoop2.protocol.sdk.tracciamento.ITracciaSerializer;
import org.openspcoop2.protocol.sdk.validator.IValidatoreErrori;
import org.openspcoop2.protocol.sdk.validator.IValidazioneAccordi;
import org.openspcoop2.protocol.sdk.validator.IValidazioneConSchema;
import org.openspcoop2.protocol.sdk.validator.IValidazioneDocumenti;
import org.openspcoop2.protocol.sdk.validator.IValidazioneSemantica;
import org.openspcoop2.protocol.sdk.validator.IValidazioneSintattica;

/**
 * L'implementazione di questa classe fornisce alla Porta di Dominio 
 * l'accesso alle classi che gestiscono gli aspetti di cooperazione, tracciamento
 * e diagnostica dipendenti dal protocollo in uso.
 * 
 * @author Lorenzo Nardi (nardi@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public interface IProtocolFactory<BustaRawType> extends Serializable {
	
	/* ** INIT ** */
	
	public void init(Logger logCore,String protocol,ConfigurazionePdD configPdD,Openspcoop2 openspcoop2Manifest) throws ProtocolException;
	public void initProtocolLogger(Logger protocolLogger) throws ProtocolException;
	
	/* ** INFO SERVIZIO ** */
	
	public String getProtocol();
	public InformazioniProtocollo getInformazioniProtocol();
	public ConfigurazionePdD getConfigurazionePdD();
	public Openspcoop2 getManifest();
	
	/* ** LOGGER ** */
	
	public Logger getLogger();
	public Logger getProtocolLogger();
		
	/* ** PROTOCOL BUILDER ** */
	
	public IBustaBuilder<BustaRawType> createBustaBuilder(IState state) throws ProtocolException;
	public IErroreApplicativoBuilder createErroreApplicativoBuilder() throws ProtocolException;
	public IEsitoBuilder createEsitoBuilder() throws ProtocolException;
	
	/* ** PROTOCOL VALIDATOR ** */
	
	public IValidatoreErrori createValidatoreErrori(IState state) throws ProtocolException;
	public IValidazioneSintattica<BustaRawType> createValidazioneSintattica(IState state) throws ProtocolException;
	public IValidazioneSemantica createValidazioneSemantica(IState state) throws ProtocolException;
	public IValidazioneConSchema createValidazioneConSchema(IState state) throws ProtocolException;
	public IValidazioneDocumenti createValidazioneDocumenti() throws ProtocolException;
	public IValidazioneAccordi createValidazioneAccordi() throws ProtocolException;
	
	/* ** DIAGNOSTICI ** */
	
	public IDiagnosticDriver createDiagnosticDriver() throws ProtocolException;
	public IDiagnosticProducer createDiagnosticProducer() throws ProtocolException;
	public IDiagnosticSerializer createDiagnosticSerializer() throws ProtocolException;
	
	/* ** TRACCE ** */
	
	public ITracciaDriver createTracciaDriver() throws ProtocolException;
	public ITracciaProducer createTracciaProducer() throws ProtocolException;
	public ITracciaSerializer createTracciaSerializer() throws ProtocolException;
	
	/* ** DUMP ** */
	
	public IDumpProducer createDumpProducer() throws ProtocolException;
	
	/* ** ARCHIVE ** */
	
	public IArchive createArchive() throws ProtocolException;
	
	/* ** CONFIG ** */
	
	public IProtocolVersionManager createProtocolVersionManager(String version) throws ProtocolException;
	public IProtocolManager createProtocolManager() throws ProtocolException;
	public ITraduttore createTraduttore() throws ProtocolException;
	public IProtocolConfiguration createProtocolConfiguration() throws ProtocolException;
	public IProtocolIntegrationConfiguration createProtocolIntegrationConfiguration() throws ProtocolException;
	
	/* ** CONSOLE ** */
	
	public IConsoleDynamicConfiguration createDynamicConfigurationConsole() throws ProtocolException;
	
	/* ** REGISTRY  ** */
	
	public IRegistryReader getRegistryReader(IDriverRegistroServiziGet driver) throws ProtocolException;
	public IRegistryReader getCachedRegistryReader(IState state) throws ProtocolException;
	public IConfigIntegrationReader getConfigIntegrationReader(IDriverConfigurazioneGet driver) throws ProtocolException;
	public IConfigIntegrationReader getCachedConfigIntegrationReader(IState state) throws ProtocolException;
	
}
