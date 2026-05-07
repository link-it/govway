/*
 * GovWay - A customizable API Gateway
 * https://govway.org
 *
 * Copyright (c) 2005-2026 Link.it srl (https://link.it).
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

package org.openspcoop2.utils.rest;

import java.util.function.UnaryOperator;

/**
 * Configurazione del {@link BaseSpecValidator}: usata come fallback quando
 * non viene specificato un engine o quando l'engine richiesto non è
 * istanziabile.
 *
 * Espone i due flag della validate strutturale di {@link org.openspcoop2.utils.rest.api.Api},
 * cioè {@code usingFromSetProtocolInfo} e {@code validateBodyParameterElement}.
 *
 * @author Tommaso Burlon (tommaso.burlon@link.it)
 */
public class BaseSpecConfig extends AbstractApiSpecConfig {

	public static final String ENGINE = "base";

	public static final String PROPERTY_KEY_USING_FROM_SET_PROTOCOL_INFO = "usingFromSetProtocolInfo";
	public static final String PROPERTY_KEY_VALIDATE_BODY_PARAMETER_ELEMENT = "validateBodyParameterElement";

	private boolean usingFromSetProtocolInfo = false;
	private boolean validateBodyParameterElement = false;

	public BaseSpecConfig() {
		setProperty(PROPERTY_KEY_USING_FROM_SET_PROTOCOL_INFO, String.valueOf(this.usingFromSetProtocolInfo));
		setProperty(PROPERTY_KEY_VALIDATE_BODY_PARAMETER_ELEMENT, String.valueOf(this.validateBodyParameterElement));
	}

	@Override
	public String getEngine() {
		return ENGINE;
	}

	public boolean isUsingFromSetProtocolInfo() {
		return this.usingFromSetProtocolInfo;
	}

	public void setUsingFromSetProtocolInfo(boolean usingFromSetProtocolInfo) {
		this.usingFromSetProtocolInfo = usingFromSetProtocolInfo;
		setProperty(PROPERTY_KEY_USING_FROM_SET_PROTOCOL_INFO, String.valueOf(usingFromSetProtocolInfo));
	}

	public boolean isValidateBodyParameterElement() {
		return this.validateBodyParameterElement;
	}

	public void setValidateBodyParameterElement(boolean validateBodyParameterElement) {
		this.validateBodyParameterElement = validateBodyParameterElement;
		setProperty(PROPERTY_KEY_VALIDATE_BODY_PARAMETER_ELEMENT, String.valueOf(validateBodyParameterElement));
	}

	@Override
	public void readProperties(UnaryOperator<String> propertyProvider) {
		String v = propertyProvider.apply(PROPERTY_KEY_USING_FROM_SET_PROTOCOL_INFO);
		if (v != null) {
			setUsingFromSetProtocolInfo(Boolean.parseBoolean(v));
		}
		v = propertyProvider.apply(PROPERTY_KEY_VALIDATE_BODY_PARAMETER_ELEMENT);
		if (v != null) {
			setValidateBodyParameterElement(Boolean.parseBoolean(v));
		}
	}

}
