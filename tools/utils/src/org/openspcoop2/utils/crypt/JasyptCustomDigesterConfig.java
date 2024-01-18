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
package org.openspcoop2.utils.crypt;

import java.security.Provider;

import org.jasypt.digest.config.DigesterConfig;
import org.jasypt.salt.SaltGenerator;

/**
 * JasyptCustomDigesterConfig
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class JasyptCustomDigesterConfig implements DigesterConfig {

	private CryptConfig config;
	private JasyptCustomSaltGenerator customSaltGenerator;
	
	public JasyptCustomDigesterConfig(CryptConfig config, JasyptCustomSaltGenerator customSaltGenerator) {
		this.config = config;
		this.customSaltGenerator = customSaltGenerator;
	}
	
	@Override
	public String getAlgorithm() {
		String digestAlgorithm = this.config.getDigestAlgorithm();
		if(digestAlgorithm==null) {
			digestAlgorithm = "SHA-256"; // default
		}
		return digestAlgorithm;
	}

	@Override
	public Boolean getInvertPositionOfPlainSaltInEncryptionResults() {
		return false;
	}

	@Override
	public Boolean getInvertPositionOfSaltInMessageBeforeDigesting() {
		return false;
	}

	@Override
	public Integer getIterations() {
		if(this.config.getIteration()!=null && this.config.getIteration()>0) {
			return this.config.getIteration().intValue();
		}
		return null;
	}

	@Override
	public Integer getPoolSize() {
		return null;
	}

	@Override
	public Provider getProvider() {
		return null;
	}

	@Override
	public String getProviderName() {
		return null;
	}

	@Override
	public SaltGenerator getSaltGenerator() {
		return this.customSaltGenerator;
	}

	@Override
	public Integer getSaltSizeBytes() {
		if(this.config.getSaltLength()!=null && this.config.getSaltLength()>0) {
			return this.config.getSaltLength().intValue();
		}
		return null;
	}

	@Override
	public Boolean getUseLenientSaltSizeCheck() {
		return false;
	}
	
}
