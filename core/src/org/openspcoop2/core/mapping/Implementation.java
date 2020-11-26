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



package org.openspcoop2.core.mapping;

import java.util.List;

import org.openspcoop2.core.config.PortaApplicativa;
import org.openspcoop2.core.controllo_traffico.AttivazionePolicy;

/**
 * Implementation
 *
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */

public class Implementation implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private PortaApplicativa portaApplicativa;
	private MappingErogazionePortaApplicativa mapping;
	private List<AttivazionePolicy> rateLimitingPolicies;
		
	public List<AttivazionePolicy> getRateLimitingPolicies() {
		return this.rateLimitingPolicies;
	}
	public void setRateLimitingPolicies(List<AttivazionePolicy> rateLimitingPolicies) {
		this.rateLimitingPolicies = rateLimitingPolicies;
	}
	public PortaApplicativa getPortaApplicativa() {
		return this.portaApplicativa;
	}
	public void setPortaApplicativa(PortaApplicativa portaApplicativa) {
		this.portaApplicativa = portaApplicativa;
	}
	public MappingErogazionePortaApplicativa getMapping() {
		return this.mapping;
	}
	public void setMapping(MappingErogazionePortaApplicativa mapping) {
		this.mapping = mapping;
	}

}





