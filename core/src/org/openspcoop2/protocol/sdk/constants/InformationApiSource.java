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
package org.openspcoop2.protocol.sdk.constants;

/**
 * InformationWsdlSource
 *
 * @author Andrea Poli (apoli@link.it)
 * @author $Author: apoli $
 * @version $Rev: 13576 $, $Date: 2018-01-26 12:39:34 +0100 (Fri, 26 Jan 2018) $
 */
public enum InformationApiSource {

	SPECIFIC, REGISTRY, 
	
	SPECIFIC_REGISTRY, 
	REGISTRY_SPECIFIC,
	
	SAFE_SPECIFIC_REGISTRY,
	SAFE_REGISTRY_SPECIFIC
}
