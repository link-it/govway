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

package org.openspcoop2.web.ctrlstat.core;

import java.util.List;

import org.openspcoop2.pdd.config.ConfigurazioneNodiRuntime;
import org.openspcoop2.pdd.config.InvokerNodiRuntime;
import org.openspcoop2.pdd.core.jmx.AbstractConfigChecker;
import org.openspcoop2.web.ctrlstat.config.ConsoleProperties;
import org.slf4j.Logger;

/**
 * CertificateChecker
 * 
 * @author Andrea Poli (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 * 
 */
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
		return this.consoleProperties.isApplicativiVerificaCertificatiCheckCertificatoApplicativoByIdUseApi();
	}
	@Override
	public boolean isUseApiCertificatoSoggettoById() throws Exception{
		return this.consoleProperties.isSoggettiVerificaCertificatiCheckCertificatoSoggettoByIdUseApi();
	}
	
	@Override
	public String getJmxResourceType() throws Exception{
		return this.consoleProperties.getJmxPdDConfigurazioneSistemaType(this.aliasDefault);
	}
	@Override
	public String getJmxResourceNomeRisorsaConfigurazionePdD() throws Exception{
		return this.consoleProperties.getJmxPdDConfigurazioneSistemaNomeRisorsaConfigurazionePdD(this.aliasDefault);
	}
	@Override
	public String getJmxResourceNomeRisorsaAccessoRegistroServizi() throws Exception{
		return this.consoleProperties.getJmxPdDConfigurazioneSistemaNomeRisorsaAccessoRegistroServizi(this.aliasDefault);
	}
	
	@Override
	public String getJmxResourceNomeMetodoCheckConnettoreById() throws Exception{
		return this.consoleProperties.getJmxPdDConfigurazioneSistemaNomeMetodoCheckConnettoreById(this.aliasDefault);
	}
	@Override
	public String getJmxResourceNomeMetodoCheckCertificatoApplicativoById() throws Exception{
		return this.consoleProperties.getJmxPdDConfigurazioneSistemaNomeMetodoCheckCertificatoApplicativoById(this.aliasDefault);
	}
	@Override
	public String getJmxResourceNomeMetodoCheckCertificatoModIApplicativoById() throws Exception{
		return this.consoleProperties.getJmxPdDConfigurazioneSistemaNomeMetodoCheckCertificatoModIApplicativoById(this.aliasDefault);
	}
	@Override
	public String getJmxResourceNomeMetodoCheckCertificatoSoggettoById() throws Exception{
		return this.consoleProperties.getJmxPdDConfigurazioneSistemaNomeMetodoCheckCertificatoSoggettoById(this.aliasDefault);
	}
	@Override
	public String getJmxResourceNomeMetodoCheckCertificatiConnettoreHttpsById() throws Exception{
		return this.consoleProperties.getJmxPdDConfigurazioneSistemaNomeMetodoCheckCertificatiConnettoreHttpsById(this.aliasDefault);
	}
	@Override
	public String getJmxResourceNomeMetodoCheckCertificatiModIErogazioneById() throws Exception{
		return this.consoleProperties.getJmxPdDConfigurazioneSistemaNomeMetodoCheckCertificatiModIErogazioneById(this.aliasDefault);
	}
	@Override
	public String getJmxResourceNomeMetodoCheckCertificatiModIFruizioneById() throws Exception{
		return this.consoleProperties.getJmxPdDConfigurazioneSistemaNomeMetodoCheckCertificatiModIFruizioneById(this.aliasDefault);
	}
	@Override
	public String getJmxResourceNomeMetodoCheckCertificatiJvm() throws Exception{
		return this.consoleProperties.getJmxPdDConfigurazioneSistemaNomeMetodoCheckCertificatiJvm(this.aliasDefault);
	}
	@Override
	public String getJmxResourceNomeMetodoCheckCertificatiConnettoreHttpsTokenPolicyValidazione() throws Exception{
		return this.consoleProperties.getJmxPdDConfigurazioneSistemaNomeMetodoCheckCertificatiConnettoreHttpsTokenPolicyValidazione(this.aliasDefault);
	}
	@Override
	public String getJmxResourceNomeMetodoCheckCertificatiValidazioneJwtTokenPolicyValidazione() throws Exception{
		return this.consoleProperties.getJmxPdDConfigurazioneSistemaNomeMetodoCheckCertificatiValidazioneJwtTokenPolicyValidazione(this.aliasDefault);
	}
	@Override
	public String getJmxResourceNomeMetodoCheckCertificatiForwardToJwtTokenPolicyValidazione() throws Exception{
		return this.consoleProperties.getJmxPdDConfigurazioneSistemaNomeMetodoCheckCertificatiForwardToJwtTokenPolicyValidazione(this.aliasDefault);
	}
	@Override
	public String getJmxResourceNomeMetodoCheckCertificatiConnettoreHttpsTokenPolicyNegoziazione() throws Exception{
		return this.consoleProperties.getJmxPdDConfigurazioneSistemaNomeMetodoCheckCertificatiConnettoreHttpsTokenPolicyNegoziazione(this.aliasDefault);
	}
	@Override
	public String getJmxResourceNomeMetodoCheckCertificatiSignedJwtTokenPolicyNegoziazione() throws Exception{
		return this.consoleProperties.getJmxPdDConfigurazioneSistemaNomeMetodoCheckCertificatiSignedJwtTokenPolicyNegoziazione(this.aliasDefault);
	}
	@Override
	public String getJmxResourceNomeMetodoCheckCertificatiConnettoreHttpsAttributeAuthority() throws Exception{
		return this.consoleProperties.getJmxPdDConfigurazioneSistemaNomeMetodoCheckCertificatiConnettoreHttpsAttributeAuthority(this.aliasDefault);
	}
	@Override
	public String getJmxResourceNomeMetodoCheckCertificatiAttributeAuthorityJwtRichiesta() throws Exception{
		return this.consoleProperties.getJmxPdDConfigurazioneSistemaNomeMetodoCheckCertificatiAttributeAuthorityJwtRichiesta(this.aliasDefault);
	}
	@Override
	public String getJmxResourceNomeMetodoCheckCertificatiAttributeAuthorityJwtRisposta() throws Exception{
		return this.consoleProperties.getJmxPdDConfigurazioneSistemaNomeMetodoCheckCertificatiAttributeAuthorityJwtRisposta(this.aliasDefault);
	}
	
}
