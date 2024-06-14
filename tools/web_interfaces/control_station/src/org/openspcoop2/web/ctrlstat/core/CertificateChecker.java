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

package org.openspcoop2.web.ctrlstat.core;

import java.util.List;

import org.openspcoop2.core.commons.CoreException;
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
	public boolean isUseApiCertificatoApplicativoById() throws CoreException{
		try {
			return this.consoleProperties.isApplicativiVerificaCertificatiCheckCertificatoApplicativoByIdUseApi();
		}catch(Exception e) {
			throw new CoreException(e.getMessage(),e);
		}
	}
	@Override
	public boolean isUseApiCertificatoSoggettoById() throws CoreException{
		try {
			return this.consoleProperties.isSoggettiVerificaCertificatiCheckCertificatoSoggettoByIdUseApi();
		}catch(Exception e) {
			throw new CoreException(e.getMessage(),e);
		}
	}
	
	@Override
	public String getJmxResourceType() throws CoreException{
		try {
			return this.consoleProperties.getJmxPdDConfigurazioneSistemaType(this.aliasDefault);
		}catch(Exception e) {
			throw new CoreException(e.getMessage(),e);
		}
	}
	@Override
	public String getJmxResourceNomeRisorsaConfigurazionePdD() throws CoreException{
		try {
			return this.consoleProperties.getJmxPdDConfigurazioneSistemaNomeRisorsaConfigurazionePdD(this.aliasDefault);
		}catch(Exception e) {
			throw new CoreException(e.getMessage(),e);
		}
	}
	@Override
	public String getJmxResourceNomeRisorsaAccessoRegistroServizi() throws CoreException{
		try {
			return this.consoleProperties.getJmxPdDConfigurazioneSistemaNomeRisorsaAccessoRegistroServizi(this.aliasDefault);
		}catch(Exception e) {
			throw new CoreException(e.getMessage(),e);
		}
	}
	
	@Override
	public String getJmxResourceNomeMetodoCheckConnettoreById() throws CoreException{
		try {
			return this.consoleProperties.getJmxPdDConfigurazioneSistemaNomeMetodoCheckConnettoreById(this.aliasDefault);
		}catch(Exception e) {
			throw new CoreException(e.getMessage(),e);
		}
	}
	@Override
	public String getJmxResourceNomeMetodoCheckCertificatoApplicativoById() throws CoreException{
		try {
			return this.consoleProperties.getJmxPdDConfigurazioneSistemaNomeMetodoCheckCertificatoApplicativoById(this.aliasDefault);
		}catch(Exception e) {
			throw new CoreException(e.getMessage(),e);
		}
	}
	@Override
	public String getJmxResourceNomeMetodoCheckCertificatoModIApplicativoById() throws CoreException{
		try {
			return this.consoleProperties.getJmxPdDConfigurazioneSistemaNomeMetodoCheckCertificatoModIApplicativoById(this.aliasDefault);
		}catch(Exception e) {
			throw new CoreException(e.getMessage(),e);
		}
	}
	@Override
	public String getJmxResourceNomeMetodoCheckCertificatoSoggettoById() throws CoreException{
		try {
			return this.consoleProperties.getJmxPdDConfigurazioneSistemaNomeMetodoCheckCertificatoSoggettoById(this.aliasDefault);
		}catch(Exception e) {
			throw new CoreException(e.getMessage(),e);
		}
	}
	@Override
	public String getJmxResourceNomeMetodoCheckCertificatiConnettoreHttpsById() throws CoreException{
		try {
			return this.consoleProperties.getJmxPdDConfigurazioneSistemaNomeMetodoCheckCertificatiConnettoreHttpsById(this.aliasDefault);
		}catch(Exception e) {
			throw new CoreException(e.getMessage(),e);
		}
	}
	@Override
	public String getJmxResourceNomeMetodoCheckCertificatiModIErogazioneById() throws CoreException{
		try {
			return this.consoleProperties.getJmxPdDConfigurazioneSistemaNomeMetodoCheckCertificatiModIErogazioneById(this.aliasDefault);
		}catch(Exception e) {
			throw new CoreException(e.getMessage(),e);
		}
	}
	@Override
	public String getJmxResourceNomeMetodoCheckCertificatiModIFruizioneById() throws CoreException{
		try {
			return this.consoleProperties.getJmxPdDConfigurazioneSistemaNomeMetodoCheckCertificatiModIFruizioneById(this.aliasDefault);
		}catch(Exception e) {
			throw new CoreException(e.getMessage(),e);
		}
	}
	@Override
	public String getJmxResourceNomeMetodoCheckCertificatiMessageSecurityErogazioneById() throws CoreException {
		try {
			return this.consoleProperties.getJmxPdDConfigurazioneSistemaNomeMetodoCheckCertificatiMessageSecurityErogazioneById(this.aliasDefault);
		}catch(Exception e) {
			throw new CoreException(e.getMessage(),e);
		}
	}

	@Override
	public String getJmxResourceNomeMetodoCheckCertificatiMessageSecurityFruizioneById() throws CoreException {
		try {
			return this.consoleProperties.getJmxPdDConfigurazioneSistemaNomeMetodoCheckCertificatiMessageSecurityFruizioneById(this.aliasDefault);
		}catch(Exception e) {
			throw new CoreException(e.getMessage(),e);
		}
	}
	@Override
	public String getJmxResourceNomeMetodoCheckCertificatiJvm() throws CoreException{
		try {
			return this.consoleProperties.getJmxPdDConfigurazioneSistemaNomeMetodoCheckCertificatiJvm(this.aliasDefault);
		}catch(Exception e) {
			throw new CoreException(e.getMessage(),e);
		}
	}
	@Override
	public String getJmxResourceNomeMetodoCheckCertificatiConnettoreHttpsTokenPolicyValidazione() throws CoreException{
		try {
			return this.consoleProperties.getJmxPdDConfigurazioneSistemaNomeMetodoCheckCertificatiConnettoreHttpsTokenPolicyValidazione(this.aliasDefault);
		}catch(Exception e) {
			throw new CoreException(e.getMessage(),e);
		}
	}
	@Override
	public String getJmxResourceNomeMetodoCheckCertificatiValidazioneJwtTokenPolicyValidazione() throws CoreException{
		try {
			return this.consoleProperties.getJmxPdDConfigurazioneSistemaNomeMetodoCheckCertificatiValidazioneJwtTokenPolicyValidazione(this.aliasDefault);
		}catch(Exception e) {
			throw new CoreException(e.getMessage(),e);
		}
	}
	@Override
	public String getJmxResourceNomeMetodoCheckCertificatiForwardToJwtTokenPolicyValidazione() throws CoreException{
		try {
			return this.consoleProperties.getJmxPdDConfigurazioneSistemaNomeMetodoCheckCertificatiForwardToJwtTokenPolicyValidazione(this.aliasDefault);
		}catch(Exception e) {
			throw new CoreException(e.getMessage(),e);
		}
	}
	@Override
	public String getJmxResourceNomeMetodoCheckCertificatiConnettoreHttpsTokenPolicyNegoziazione() throws CoreException{
		try {
			return this.consoleProperties.getJmxPdDConfigurazioneSistemaNomeMetodoCheckCertificatiConnettoreHttpsTokenPolicyNegoziazione(this.aliasDefault);
		}catch(Exception e) {
			throw new CoreException(e.getMessage(),e);
		}
	}
	@Override
	public String getJmxResourceNomeMetodoCheckCertificatiSignedJwtTokenPolicyNegoziazione() throws CoreException{
		try {
			return this.consoleProperties.getJmxPdDConfigurazioneSistemaNomeMetodoCheckCertificatiSignedJwtTokenPolicyNegoziazione(this.aliasDefault);
		}catch(Exception e) {
			throw new CoreException(e.getMessage(),e);
		}
	}
	@Override
	public String getJmxResourceNomeMetodoCheckCertificatiConnettoreHttpsAttributeAuthority() throws CoreException{
		try {
			return this.consoleProperties.getJmxPdDConfigurazioneSistemaNomeMetodoCheckCertificatiConnettoreHttpsAttributeAuthority(this.aliasDefault);
		}catch(Exception e) {
			throw new CoreException(e.getMessage(),e);
		}
	}
	@Override
	public String getJmxResourceNomeMetodoCheckCertificatiAttributeAuthorityJwtRichiesta() throws CoreException{
		try {
			return this.consoleProperties.getJmxPdDConfigurazioneSistemaNomeMetodoCheckCertificatiAttributeAuthorityJwtRichiesta(this.aliasDefault);
		}catch(Exception e) {
			throw new CoreException(e.getMessage(),e);
		}
	}
	@Override
	public String getJmxResourceNomeMetodoCheckCertificatiAttributeAuthorityJwtRisposta() throws CoreException{
		try {
			return this.consoleProperties.getJmxPdDConfigurazioneSistemaNomeMetodoCheckCertificatiAttributeAuthorityJwtRisposta(this.aliasDefault);
		}catch(Exception e) {
			throw new CoreException(e.getMessage(),e);
		}
	}
	
}
