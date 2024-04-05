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

package org.openspcoop2.utils.certificate.hsm;

/**
 * HSMManager
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class HSMCostanti {
	
	private HSMCostanti() {}

	public static final String PROPERTY_PREFIX = "hsm.";
	
	public static final String PROPERTY_SUFFIX_PROVIDER = "provider";
	public static final String PROPERTY_SUFFIX_PROVIDER_ADD = "provider.add";
	public static final String PROPERTY_SUFFIX_PROVIDER_CONFIG_FILE = "provider.configFile";
	public static final String PROPERTY_SUFFIX_PROVIDER_CONFIG = "provider.config";
	public static final String PROPERTY_SUFFIX_PIN = "pin";
	public static final String PROPERTY_SUFFIX_KEYSTORE_TYPE_LABEL = "keystoreType.label";
	public static final String PROPERTY_SUFFIX_KEYSTORE_TYPE = "keystoreType";
	public static final String PROPERTY_SUFFIX_USABLE_AS_TRUST_STORE = "usableAsTrustStore";
	public static final String PROPERTY_SUFFIX_USABLE_AS_SECRET_KEY_STORE = "usableAsSecretKeyStore";

}
