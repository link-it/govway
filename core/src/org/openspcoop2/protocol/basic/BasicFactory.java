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

package org.openspcoop2.protocol.basic;


import org.openspcoop2.core.config.driver.IDriverConfigurazioneGet;
import org.openspcoop2.core.registry.driver.IDriverRegistroServiziGet;
import org.openspcoop2.protocol.basic.archive.BasicArchive;
import org.openspcoop2.protocol.basic.builder.BustaBuilder;
import org.openspcoop2.protocol.basic.builder.ErroreApplicativoBuilder;
import org.openspcoop2.protocol.basic.builder.EsitoBuilder;
import org.openspcoop2.protocol.basic.config.BasicProtocolIntegrationConfiguration;
import org.openspcoop2.protocol.basic.config.BasicTraduttore;
import org.openspcoop2.protocol.basic.diagnostica.DiagnosticSerializer;
import org.openspcoop2.protocol.basic.properties.BasicDynamicConfiguration;
import org.openspcoop2.protocol.basic.registry.ConfigIntegrationReader;
import org.openspcoop2.protocol.basic.registry.RegistryReader;
import org.openspcoop2.protocol.basic.tracciamento.TracciaSerializer;
import org.openspcoop2.protocol.basic.validator.ValidatoreErrori;
import org.openspcoop2.protocol.basic.validator.ValidazioneAccordi;
import org.openspcoop2.protocol.basic.validator.ValidazioneConSchema;
import org.openspcoop2.protocol.basic.validator.ValidazioneDocumenti;
import org.openspcoop2.protocol.basic.validator.ValidazioneSemantica;
import org.openspcoop2.protocol.basic.validator.ValidazioneSintattica;
import org.openspcoop2.protocol.manifest.Openspcoop2;
import org.openspcoop2.protocol.registry.CachedRegistryReader;
import org.openspcoop2.protocol.sdk.ConfigurazionePdD;
import org.openspcoop2.protocol.sdk.IProtocolFactory;
import org.openspcoop2.protocol.sdk.InformazioniProtocollo;
import org.openspcoop2.protocol.sdk.ProtocolException;
import org.openspcoop2.protocol.sdk.archive.IArchive;
import org.openspcoop2.protocol.sdk.config.IProtocolIntegrationConfiguration;
import org.openspcoop2.protocol.sdk.diagnostica.IDiagnosticDriver;
import org.openspcoop2.protocol.sdk.diagnostica.IDiagnosticProducer;
import org.openspcoop2.protocol.sdk.dump.IDumpProducer;
import org.openspcoop2.protocol.sdk.properties.IConsoleDynamicConfiguration;
import org.openspcoop2.protocol.sdk.registry.IConfigIntegrationReader;
import org.openspcoop2.protocol.sdk.registry.IRegistryReader;
import org.openspcoop2.protocol.sdk.state.IState;
import org.openspcoop2.protocol.sdk.tracciamento.ITracciaDriver;
import org.openspcoop2.protocol.sdk.tracciamento.ITracciaProducer;
import org.openspcoop2.protocol.sdk.validator.IValidazioneAccordi;
import org.openspcoop2.protocol.sdk.validator.IValidazioneDocumenti;
import org.slf4j.Logger;


/**	
 * BasicFactory
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public abstract class BasicFactory<BustaRawType> implements IProtocolFactory<BustaRawType> {
	private static final long serialVersionUID = 1L;
	
	private String protocol;
	private InformazioniProtocollo informazioniProtocollo;
	protected Logger log;
	protected Logger logProtocol;
	private ConfigurazionePdD configPdD;
	private Openspcoop2 manifest;

	/* ** INIT ** */
		
	@Override
	public void init(Logger log,String protocol,ConfigurazionePdD configPdD,Openspcoop2 manifest) throws ProtocolException{
		this.protocol = protocol;
		this.log = log;
		this.configPdD = configPdD;
		this.manifest = manifest;
		this.informazioniProtocollo = new InformazioniProtocollo();
		this.informazioniProtocollo.setName(this.protocol);
		this.informazioniProtocollo.setLabel(manifest.getProtocol().getLabel());
		this.informazioniProtocollo.setWebSite(manifest.getProtocol().getWebSite());
		this.informazioniProtocollo.setDescription(manifest.getProtocol().getDescrizione());
		if(manifest.getProtocol().getTransaction()!=null) {
			this.informazioniProtocollo.setErrorProtocol(manifest.getProtocol().getTransaction().isErrorProtocol());
			this.informazioniProtocollo.setLabelErrorProtocol(manifest.getProtocol().getTransaction().getLabelErrorProtocol());
			this.informazioniProtocollo.setExternalFault(manifest.getProtocol().getTransaction().isExternalFault());
			this.informazioniProtocollo.setLabelExternalFault(manifest.getProtocol().getTransaction().getLabelExternalFault());
		}
	}
	
	@Override
	public void initProtocolLogger(Logger protocolLogger) throws ProtocolException{
		this.logProtocol = protocolLogger;
	}
	
	
	/* ** LOGGER ** */
	
	@Override
	public Logger getLogger() {
		return this.log;
	}
	
	@Override
	public Logger getProtocolLogger() {
		if(this.logProtocol!=null) {
			return this.logProtocol;
		}
		else {
			return this.log; // Per contesti di utilizzo fuori dalla PdD, (es. console) per non avere nullPointer
		}
	}
	
	
	/* ** INFO SERVIZIO ** */
	
	@Override
	public String getProtocol() {
		return this.protocol;
	}
		
	@Override
	public InformazioniProtocollo getInformazioniProtocol() {
		return this.informazioniProtocollo;
	}
	
	@Override
	public ConfigurazionePdD getConfigurazionePdD() {
		return this.configPdD;
	}
	
	@Override
	public Openspcoop2 getManifest() {
		return this.manifest;
	}

	
	/* ** PROTOCOL BUILDER ** */
	
	@Override
	public org.openspcoop2.protocol.sdk.builder.IBustaBuilder<BustaRawType> createBustaBuilder(IState state) throws ProtocolException {
		return new BustaBuilder<BustaRawType>(this, state);
	}
	
	@Override
	public org.openspcoop2.protocol.sdk.builder.IErroreApplicativoBuilder createErroreApplicativoBuilder()  throws ProtocolException {
		return new ErroreApplicativoBuilder(this);
	}
	
	@Override
	public org.openspcoop2.protocol.sdk.builder.IEsitoBuilder createEsitoBuilder()  throws ProtocolException{
		return new EsitoBuilder(this);
	}
	
	
	/* ** PROTOCOL VALIDATOR ** */
	
	@Override
	public org.openspcoop2.protocol.sdk.validator.IValidatoreErrori createValidatoreErrori(IState state)  throws ProtocolException{
		return new ValidatoreErrori(this, state);
	}

	@Override
	public org.openspcoop2.protocol.sdk.validator.IValidazioneSintattica<BustaRawType> createValidazioneSintattica(IState state) throws ProtocolException {
		return new ValidazioneSintattica<BustaRawType>(this, state);
	}
	
	@Override
	public org.openspcoop2.protocol.sdk.validator.IValidazioneSemantica createValidazioneSemantica(IState state)  throws ProtocolException {
		return new ValidazioneSemantica(this, state);
	}

	@Override
	public org.openspcoop2.protocol.sdk.validator.IValidazioneConSchema createValidazioneConSchema(IState state)  throws ProtocolException{
		return new ValidazioneConSchema(this, state);
	}
	
	@Override
	public IValidazioneDocumenti createValidazioneDocumenti()
			throws ProtocolException {
		return new ValidazioneDocumenti(this);
	}

	@Override
	public IValidazioneAccordi createValidazioneAccordi()
			throws ProtocolException {
		return new ValidazioneAccordi(this);
	}
	
	

	/* ** DIAGNOSTICI ** */

	@Override
	public IDiagnosticDriver createDiagnosticDriver() throws ProtocolException{
		return new org.openspcoop2.protocol.basic.diagnostica.DiagnosticDriver(this);
	}
	
	@Override
	public IDiagnosticProducer createDiagnosticProducer() throws ProtocolException{
		return new org.openspcoop2.protocol.basic.diagnostica.DiagnosticProducer(this);
	}
	
	@Override
	public org.openspcoop2.protocol.sdk.diagnostica.IDiagnosticSerializer createDiagnosticSerializer()  throws ProtocolException {
		return new DiagnosticSerializer(this);
	}

	
	/* ** TRACCE ** */
	
	@Override
	public ITracciaDriver createTracciaDriver() throws ProtocolException{
		return new org.openspcoop2.protocol.basic.tracciamento.TracciaDriver(this);
	}
	
	@Override
	public ITracciaProducer createTracciaProducer() throws ProtocolException{
		return new org.openspcoop2.protocol.basic.tracciamento.TracciaProducer(this);
	}
	
	@Override
	public org.openspcoop2.protocol.sdk.tracciamento.ITracciaSerializer createTracciaSerializer()  throws ProtocolException {
		return new TracciaSerializer(this);
	}
	
	
	/* ** DUMP ** */
	
	@Override
	public IDumpProducer createDumpProducer() throws ProtocolException{
		return new org.openspcoop2.protocol.basic.dump.DumpProducer(this);
	}
	
	
	/* ** ARCHIVE ** */
	
	@Override
	public IArchive createArchive() throws ProtocolException{
		return new BasicArchive(this);
	}
	
	
	/* ** CONFIG ** */
	
	@Override
	public org.openspcoop2.protocol.sdk.config.ITraduttore createTraduttore()  throws ProtocolException{
		return new BasicTraduttore(this);
	}
	
	@Override
	public IProtocolIntegrationConfiguration createProtocolIntegrationConfiguration() throws ProtocolException{
		return new BasicProtocolIntegrationConfiguration(this);
	}

	
	/* ** CONSOLE ** */
	
	@Override
	public IConsoleDynamicConfiguration createDynamicConfigurationConsole() throws ProtocolException{
		return new BasicDynamicConfiguration(this);
	}
	
	
	/* ** REGISTRY  ** */
	
	@Override
	public IRegistryReader getRegistryReader(IDriverRegistroServiziGet driverRegistry) throws ProtocolException{
		try{
			return new RegistryReader(driverRegistry, this.log);
		}catch(Exception e){
			throw new ProtocolException(e.getMessage(),e);
		}
	}
	@Override
	public IRegistryReader getCachedRegistryReader(IState state) throws ProtocolException{
		try{
			return new CachedRegistryReader(this.log, this, state);
		}catch(Exception e){
			throw new ProtocolException(e.getMessage(),e);
		}
	}
	@Override
	public IConfigIntegrationReader getConfigIntegrationReader(IDriverConfigurazioneGet driver) throws ProtocolException{
		try{
			return new ConfigIntegrationReader(driver, this.log);
		}catch(Exception e){
			throw new ProtocolException(e.getMessage(),e);
		}
	}
	@Override
	public IConfigIntegrationReader getCachedConfigIntegrationReader(IState state) throws ProtocolException{
		try{
			Class<?> c = Class.forName("org.openspcoop2.pdd.config.CachedConfigIntegrationReader");
			return (IConfigIntegrationReader) c.getConstructor(Logger.class,IProtocolFactory.class,IState.class).
				newInstance(this.log,this,state);
		}catch(Exception e){
			throw new ProtocolException(e.getMessage(),e);
		}
	}

}