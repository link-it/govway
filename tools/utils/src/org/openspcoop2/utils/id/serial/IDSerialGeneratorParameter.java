/*
 * OpenSPCoop - Customizable API Gateway 
 * http://www.openspcoop2.org
 * 
 * Copyright (c) 2005-2016 Link.it srl (http://link.it). 
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
	private int serializableNextIntervalTimeMs = 100; // nuovo tentativo ogni 100 millisecondi (random 0-100)
	
	// E' possibile attivare anche il next interval time ms increment mode.
	// Se abilitato il nuovo tenntativo viene effettuato nell'intervallo (0 - (serializableNextIntervalTimeMs+(serializableNextIntervalTimeMsIncrement*iterazione))) 
	// fino ad un massimo intervallo destro di maxSerializableNextIntervalTimeMs
	private boolean serializableNextIntervalTimeMsIncrementMode = true;
	private int serializableNextIntervalTimeMsIncrement = 200;
	private int maxSerializableNextIntervalTimeMs = 2000;
	
	private String tableName;
	private String columnPrg;
	private String columnProtocol;
	private String columnRelativeInfo;
	
	private int sizeBuffer = 1;
	
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
	
	public boolean isSerializableNextIntervalTimeMsIncrementMode() {
		return this.serializableNextIntervalTimeMsIncrementMode;
	}
	public void setSerializableNextIntervalTimeMsIncrementMode(boolean serializableNextIntervalTimeMsIncrementMode) {
		this.serializableNextIntervalTimeMsIncrementMode = serializableNextIntervalTimeMsIncrementMode;
	}
	public int getSerializableNextIntervalTimeMsIncrement() {
		return this.serializableNextIntervalTimeMsIncrement;
	}
	public void setSerializableNextIntervalTimeMsIncrement(int serializableNextIntervalTimeMsIncrement) {
		this.serializableNextIntervalTimeMsIncrement = serializableNextIntervalTimeMsIncrement;
	}
	public int getMaxSerializableNextIntervalTimeMs() {
		return this.maxSerializableNextIntervalTimeMs;
	}
	public void setMaxSerializableNextIntervalTimeMs(int maxSerializableNextIntervalTimeMs) {
		this.maxSerializableNextIntervalTimeMs = maxSerializableNextIntervalTimeMs;
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
	
	public int getSizeBuffer() {
		return this.sizeBuffer;
	}
	public void setSizeBuffer(int sizeBuffer) {
		this.sizeBuffer = sizeBuffer;
	}
}
