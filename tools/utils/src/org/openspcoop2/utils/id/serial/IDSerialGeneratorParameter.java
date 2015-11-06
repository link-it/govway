/*
 * OpenSPCoop v2 - Customizable SOAP Message Broker 
 * http://www.openspcoop2.org
 * 
 * Copyright (c) 2005-2015 Link.it srl (http://link.it). 
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

package org.openspcoop2.utils.id.serial;

/**
 * IDSerialGenerator
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class IDSerialGeneratorParameter {

	private IDSerialGeneratorType tipo = IDSerialGeneratorType.DEFAULT;
	private String protocollo = "@DEFAULT@"; // per usare la libreria al di fuori del contesto del protocol della PdD
	private Long maxValue = Long.MAX_VALUE; // value o cifre
	private String informazioneAssociataAlProgressivo= null;
	private boolean wrap = true;
	private Integer size = null;

	private long serializableTimeWaitMs = 60000; // tempo massimo di attesa in millisecondi
	private int serializableNextIntervalTimeMs = 100; // nuovo tentativo ogni 100 millisecondi
	
	private String tableName;
	private String columnPrg;
	private String columnProtocol;
	private String columnRelativeInfo;
	
	public IDSerialGeneratorParameter(){
	}
	public IDSerialGeneratorParameter(String protocollo){
		this.protocollo = protocollo;
	}
	public IDSerialGeneratorParameter(String protocollo,String informazioneAssociataAlProgressivo){
		this.protocollo = protocollo;
		this.informazioneAssociataAlProgressivo = informazioneAssociataAlProgressivo;
	}
	
	public String getProtocollo() {
		return this.protocollo;
	}
	public void setProtocollo(String protocollo) {
		this.protocollo = protocollo;
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
	
	public long getSerializableTimeWaitMs() {
		return this.serializableTimeWaitMs;
	}
	public void setSerializableTimeWaitMs(long serializableTimeWaitMs) {
		this.serializableTimeWaitMs = serializableTimeWaitMs;
	}
	
	public int getSerializableNextIntervalTimeMs() {
		return this.serializableNextIntervalTimeMs;
	}
	public void setSerializableNextIntervalTimeMs(int serializableNextIntervalTimeMs) {
		this.serializableNextIntervalTimeMs = serializableNextIntervalTimeMs;
	}
	
	public String getTableName() {
		return this.tableName;
	}
	public void setTableName(String tableName) {
		this.tableName = tableName;
	}
	public String getColumnPrg() {
		return this.columnPrg;
	}
	public void setColumnPrg(String columnPrg) {
		this.columnPrg = columnPrg;
	}
	public String getColumnProtocol() {
		return this.columnProtocol;
	}
	public void setColumnProtocol(String columnProtocol) {
		this.columnProtocol = columnProtocol;
	}
	public String getColumnRelativeInfo() {
		return this.columnRelativeInfo;
	}
	public void setColumnRelativeInfo(String columnRelativeInfo) {
		this.columnRelativeInfo = columnRelativeInfo;
	}
}
