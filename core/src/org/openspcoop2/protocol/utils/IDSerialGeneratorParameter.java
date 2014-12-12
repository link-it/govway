/*
 * OpenSPCoop v2 - Customizable SOAP Message Broker 
 * http://www.openspcoop2.org
 * 
 * Copyright (c) 2005-2014 Link.it srl (http://link.it). All rights reserved. 
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
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

package org.openspcoop2.protocol.utils;

import org.openspcoop2.protocol.sdk.IProtocolFactory;

/**
 * IDSerialGenerator
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class IDSerialGeneratorParameter {

	private IDSerialGeneratorType tipo = IDSerialGeneratorType.DEFAULT;
	private IProtocolFactory protocolFactory;
	private Long maxValue; // value o cifre
	private String informazioneAssociataAlProgressivo= null;
	private boolean wrap = true;
	private Integer size = null;

	public IDSerialGeneratorParameter(IProtocolFactory protocolFactory){
		this.protocolFactory = protocolFactory;
	}
	public IDSerialGeneratorParameter(IProtocolFactory protocolFactory,String informazioneAssociataAlProgressivo){
		this.protocolFactory = protocolFactory;
		this.informazioneAssociataAlProgressivo = informazioneAssociataAlProgressivo;
	}
	
	public IProtocolFactory getProtocolFactory() {
		return this.protocolFactory;
	}

	public void setProtocolFactory(IProtocolFactory protocolFactory) {
		this.protocolFactory = protocolFactory;
	}

	public Long getMaxValue() {
		return this.maxValue;
	}

	public void setMaxValue(Long maxValue) {
		this.maxValue = maxValue;
	}
	
	public Integer getSize() {
		return this.size;
	}
	public void setSize(Integer size) {
		this.size = size;
	}
	
	public IDSerialGeneratorType getTipo() {
		return this.tipo;
	}

	public void setTipo(IDSerialGeneratorType tipo) {
		this.tipo = tipo;
	}
	
	public String getInformazioneAssociataAlProgressivo() {
		return this.informazioneAssociataAlProgressivo;
	}
	public void setInformazioneAssociataAlProgressivo(
			String informazioneAssociataAlProgressivo) {
		this.informazioneAssociataAlProgressivo = informazioneAssociataAlProgressivo;
	}
	
	public boolean isWrap() {
		return this.wrap;
	}
	public void setWrap(boolean wrap) {
		this.wrap = wrap;
	}
}
