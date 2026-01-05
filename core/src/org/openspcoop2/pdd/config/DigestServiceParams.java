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
package org.openspcoop2.pdd.config;


import java.io.Serializable;
import java.time.Instant;

import org.openspcoop2.core.id.IDServizio;
import org.openspcoop2.utils.digest.DigestType;

/**
 * DigestServiceParamsDriver
 *
 * @author Burlon Tommaso (tommaso.burlon@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class DigestServiceParams implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private IDServizio idServizio;
	private DigestType digestAlgorithm;
	private byte[] seed;
	private Instant dataRegistrazione;
	private Integer durata;
	private Long serialNumber;
	
	public IDServizio getIdServizio() {
		return this.idServizio;
	}
	public void setIdServizio(IDServizio idServizio) {
		this.idServizio = idServizio;
	}
	public DigestType getDigestAlgorithm() {
		return this.digestAlgorithm;
	}
	public void setDigestAlgorithm(DigestType digestAlgorithm) {
		this.digestAlgorithm = digestAlgorithm;
	}
	public byte[] getSeed() {
		return this.seed;
	}
	public void setSeed(byte[] seed) {
		this.seed = seed;
	}
	public Instant getDataRegistrazione() {
		return this.dataRegistrazione;
	}
	public void setDataRegistrazione(Instant dataRegistrazione) {
		this.dataRegistrazione = dataRegistrazione;
	}
	public Integer getDurata() {
		return this.durata;
	}
	public void setDurata(Integer durata) {
		this.durata = durata;
	}
	public Long getSerialNumber() {
		return this.serialNumber;
	}
	public void setSerialNumber(Long serialNumber) {
		this.serialNumber = serialNumber;
	}
	
}
