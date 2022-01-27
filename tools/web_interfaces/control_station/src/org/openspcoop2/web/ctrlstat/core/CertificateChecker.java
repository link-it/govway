package org.openspcoop2.web.ctrlstat.core;

import java.util.List;

import org.openspcoop2.pdd.config.ConfigurazioneNodiRuntime;
import org.openspcoop2.pdd.config.InvokerNodiRuntime;
import org.openspcoop2.pdd.core.jmx.AbstractConfigChecker;
import org.openspcoop2.web.ctrlstat.config.ConsoleProperties;
import org.slf4j.Logger;

public class CertificateChecker extends AbstractConfigChecker {

	private Logger logger;
	private ConsoleProperties consoleProperties;
	private String aliasDefault;
	
	public CertificateChecker(Logger logger, InvokerNodiRuntime invoker, ConfigurazioneNodiRuntime config, List<String> nodiRuntime,
			ConsoleProperties consoleProperties) {
		super(invoker, config, nodiRuntime);
		this.logger = logger;
		this.consoleProperties = consoleProperties;
		this.aliasDefault = nodiRuntime.get(0); // prendo il primo
	}
	
	@Override
	protected String getMultipleNodeSeparator() {
		return org.openspcoop2.core.constants.Costanti.WEB_NEW_LINE;
	}
	
	@Override
	public void error(String msg) {
		this.logger.error(msg);
	}
	@Override
	public void error(String msg, Throwable t) {
		this.logger.error(msg, t);
	}
	@Override
	public Logger getInternalLogger() {
		return this.logger;
	}
	
	@Override
	public boolean isUseApiCertificatoApplicativoById() throws Exception{
		return this.consoleProperties.isApplicativiVerificaCertificati_checkCertificatoApplicativoById_useApi();
	}
	@Override
	public boolean isUseApiCertificatoSoggettoById() throws Exception{
		return this.consoleProperties.isSoggettiVerificaCertificati_checkCertificatoSoggettoById_useApi();
	}
	
	@Override
	public String getJmxResourceType() throws Exception{
		return this.consoleProperties.getJmxPdD_configurazioneSistema_type(this.aliasDefault);
	}
	@Override
	public String getJmxResourceNomeRisorsaConfigurazionePdD() throws Exception{
		return this.consoleProperties.getJmxPdD_configurazioneSistema_nomeRisorsaConfigurazionePdD(this.aliasDefault);
	}
	@Override
	public String getJmxResourceNomeRisorsaAccessoRegistroServizi() throws Exception{
		return this.consoleProperties.getJmxPdD_configurazioneSistema_nomeRisorsaAccessoRegistroServizi(this.aliasDefault);
	}
	
	@Override
	public String getJmxResourceNomeMetodoCheckConnettoreById() throws Exception{
		return this.consoleProperties.getJmxPdD_configurazioneSistema_nomeMetodo_checkConnettoreById(this.aliasDefault);
	}
	@Override
	public String getJmxResourceNomeMetodoCheckCertificatoApplicativoById() throws Exception{
		return this.consoleProperties.getJmxPdD_configurazioneSistema_nomeMetodo_checkCertificatoApplicativoById(this.aliasDefault);
	}
	@Override
	public String getJmxResourceNomeMetodoCheckCertificatoModIApplicativoById() throws Exception{
		return this.consoleProperties.getJmxPdD_configurazioneSistema_nomeMetodo_checkCertificatoModIApplicativoById(this.aliasDefault);
	}
	@Override
	public String getJmxResourceNomeMetodoCheckCertificatoSoggettoById() throws Exception{
		return this.consoleProperties.getJmxPdD_configurazioneSistema_nomeMetodo_checkCertificatoSoggettoById(this.aliasDefault);
	}
	@Override
	public String getJmxResourceNomeMetodoCheckCertificatiConnettoreHttpsById() throws Exception{
		return this.consoleProperties.getJmxPdD_configurazioneSistema_nomeMetodo_checkCertificatiConnettoreHttpsById(this.aliasDefault);
	}
	@Override
	public String getJmxResourceNomeMetodoCheckCertificatiModIErogazioneById() throws Exception{
		return this.consoleProperties.getJmxPdD_configurazioneSistema_nomeMetodo_checkCertificatiModIErogazioneById(this.aliasDefault);
	}
	@Override
	public String getJmxResourceNomeMetodoCheckCertificatiModIFruizioneById() throws Exception{
		return this.consoleProperties.getJmxPdD_configurazioneSistema_nomeMetodo_checkCertificatiModIFruizioneById(this.aliasDefault);
	}
	@Override
	public String getJmxResourceNomeMetodoCheckCertificatiJvm() throws Exception{
		return this.consoleProperties.getJmxPdD_configurazioneSistema_nomeMetodo_checkCertificatiJvm(this.aliasDefault);
	}
	@Override
	public String getJmxResourceNomeMetodoCheckCertificatiConnettoreHttpsTokenPolicyValidazione() throws Exception{
		return this.consoleProperties.getJmxPdD_configurazioneSistema_nomeMetodo_checkCertificatiConnettoreHttpsTokenPolicyValidazione(this.aliasDefault);
	}
	@Override
	public String getJmxResourceNomeMetodoCheckCertificatiValidazioneJwtTokenPolicyValidazione() throws Exception{
		return this.consoleProperties.getJmxPdD_configurazioneSistema_nomeMetodo_checkCertificatiValidazioneJwtTokenPolicyValidazione(this.aliasDefault);
	}
	@Override
	public String getJmxResourceNomeMetodoCheckCertificatiForwardToJwtTokenPolicyValidazione() throws Exception{
		return this.consoleProperties.getJmxPdD_configurazioneSistema_nomeMetodo_checkCertificatiForwardToJwtTokenPolicyValidazione(this.aliasDefault);
	}
	@Override
	public String getJmxResourceNomeMetodoCheckCertificatiConnettoreHttpsTokenPolicyNegoziazione() throws Exception{
		return this.consoleProperties.getJmxPdD_configurazioneSistema_nomeMetodo_checkCertificatiConnettoreHttpsTokenPolicyNegoziazione(this.aliasDefault);
	}
	@Override
	public String getJmxResourceNomeMetodoCheckCertificatiSignedJwtTokenPolicyNegoziazione() throws Exception{
		return this.consoleProperties.getJmxPdD_configurazioneSistema_nomeMetodo_checkCertificatiSignedJwtTokenPolicyNegoziazione(this.aliasDefault);
	}
	@Override
	public String getJmxResourceNomeMetodoCheckCertificatiConnettoreHttpsAttributeAuthority() throws Exception{
		return this.consoleProperties.getJmxPdD_configurazioneSistema_nomeMetodo_checkCertificatiConnettoreHttpsAttributeAuthority(this.aliasDefault);
	}
	@Override
	public String getJmxResourceNomeMetodoCheckCertificatiAttributeAuthorityJwtRichiesta() throws Exception{
		return this.consoleProperties.getJmxPdD_configurazioneSistema_nomeMetodo_checkCertificatiAttributeAuthorityJwtRichiesta(this.aliasDefault);
	}
	@Override
	public String getJmxResourceNomeMetodoCheckCertificatiAttributeAuthorityJwtRisposta() throws Exception{
		return this.consoleProperties.getJmxPdD_configurazioneSistema_nomeMetodo_checkCertificatiAttributeAuthorityJwtRisposta(this.aliasDefault);
	}
	
}
