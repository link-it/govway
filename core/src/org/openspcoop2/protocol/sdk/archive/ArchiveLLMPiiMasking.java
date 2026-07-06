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
package org.openspcoop2.protocol.sdk.archive;

import org.openspcoop2.protocol.sdk.ProtocolException;

/**
 * ArchiveLLMPiiMasking
 *
 * @author Poli Andrea (apoli@link.it)
 */
public class ArchiveLLMPiiMasking extends AbstractArchiveGenericProperties {

	public static String buildKey(String tipologiaPolicy, String nomePolicy) throws ProtocolException{

		return AbstractArchiveGenericProperties.buildKey("LLMPiiMasking", tipologiaPolicy, nomePolicy);

	}

	@Override
	public String key() throws ProtocolException {
		return ArchiveLLMPiiMasking.buildKey(this.tipologiaPolicy, this.idPolicy);
	}

	public ArchiveLLMPiiMasking(org.openspcoop2.core.config.GenericProperties policy, ArchiveIdCorrelazione idCorrelazione) throws ProtocolException{
		super(policy, idCorrelazione);

	}

}
