/*
 * OpenSPCoop - Customizable API Gateway 
 * http://www.openspcoop2.org
 * 
 * Copyright (c) 2005-2017 Link.it srl (http://link.it). 
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



package org.openspcoop2.protocol.basic.tracciamento;

/**
 * Bean Contenente le informazioni per il root element dei messaggi diagnostici
 * 
 * @author Andrea Poli <apoli@link.it>
 * @author $Author: apoli $
 * @version $Rev: 13064 $, $Date: 2017-06-12 13:16:47 +0200 (Mon, 12 Jun 2017) $
 * 
 */
public class TracciaXMLRootElement extends org.openspcoop2.protocol.sdk.XMLRootElement{

	private static final long serialVersionUID = -3157816024001587816L;

	public TracciaXMLRootElement(){
		super("Elenco",org.openspcoop2.core.tracciamento.constants.CostantiTracciamento.TARGET_NAMESPACE,"op2Tracce");
	}
}


