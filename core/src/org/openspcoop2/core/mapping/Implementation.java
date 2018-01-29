/*
 * OpenSPCoop - Customizable API Gateway 
 * http://www.openspcoop2.org
 * 
 * Copyright (c) 2005-2018 Link.it srl (http://link.it). 
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

import org.openspcoop2.core.config.PortaApplicativa;
import org.openspcoop2.core.mapping.MappingErogazionePortaApplicativa;

/**
 * Implementation
 *
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author: apoli $
 * @version $Rev: 13574 $, $Date: 2018-01-26 12:24:34 +0100 (Fri, 26 Jan 2018) $
 */

public class Implementation implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private PortaApplicativa portaApplicativa;
	private MappingErogazionePortaApplicativa mapping;
		
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





