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

import org.openspcoop2.core.config.PortaDelegata;
import org.openspcoop2.core.controllo_traffico.AttivazionePolicy;

/**
 * Subscription
 *
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */

public class Subscription implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private PortaDelegata portaDelegata;
	private MappingFruizionePortaDelegata mapping;
	private List<AttivazionePolicy> rateLimitingPolicies;
	
	public List<AttivazionePolicy> getRateLimitingPolicies() {
		return this.rateLimitingPolicies;
	}
	public void setRateLimitingPolicies(List<AttivazionePolicy> rateLimitingPolicies) {
		this.rateLimitingPolicies = rateLimitingPolicies;
	}
	public PortaDelegata getPortaDelegata() {
		return this.portaDelegata;
	}
	public void setPortaDelegata(PortaDelegata portaDelegata) {
		this.portaDelegata = portaDelegata;
	}
	public MappingFruizionePortaDelegata getMapping() {
		return this.mapping;
	}
	public void setMapping(MappingFruizionePortaDelegata mapping) {
		this.mapping = mapping;
	}
}





