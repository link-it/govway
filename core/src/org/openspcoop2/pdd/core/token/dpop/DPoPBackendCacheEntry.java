/*
 * GovWay - A customizable API Gateway
 * https://govway.org
 *
 * Copyright (c) 2005-2025 Link.it srl (https://link.it).
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

package org.openspcoop2.pdd.core.token.dpop;

import java.io.Serializable;

/**
 * DPoPBackendCacheEntry - Entry per la cache del DPoP backend proof
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class DPoPBackendCacheEntry implements Serializable {

	private static final long serialVersionUID = 1L;

	private String dpopProof;
	private long creationTime;

	public DPoPBackendCacheEntry(String dpopProof) {
		this.dpopProof = dpopProof;
		this.creationTime = System.currentTimeMillis();
	}

	public String getDpopProof() {
		return this.dpopProof;
	}

	public long getCreationTime() {
		return this.creationTime;
	}
}
