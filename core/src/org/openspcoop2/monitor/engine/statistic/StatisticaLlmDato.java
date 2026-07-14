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
package org.openspcoop2.monitor.engine.statistic;

/**
 * Dato intermedio per il breakdown LLM (provider/model/binding + token/costo) di un bucket
 * statistico, prodotto da {@link AbstractStatistiche} e convertito nel bean satellite
 * specifico del livello (orario/giornaliero) dai relativi engine.
 *
 * @author Andrea Poli (apoli@link.it)
 */
public class StatisticaLlmDato {

	private String llmProvider;
	private String llmModel;
	private String llmProviderBinding;
	private long tokenInput;
	private long tokenOutput;
	private double costEstimated;
	private int numeroTransazioni;
	private long bandaComplessiva;
	private long bandaInterna;
	private long bandaEsterna;
	private Long latenzaTotale;
	private Long latenzaPorta;
	private Long latenzaServizio;

	public String getLlmProvider() {
		return this.llmProvider;
	}
	public void setLlmProvider(String llmProvider) {
		this.llmProvider = llmProvider;
	}
	public String getLlmModel() {
		return this.llmModel;
	}
	public void setLlmModel(String llmModel) {
		this.llmModel = llmModel;
	}
	public String getLlmProviderBinding() {
		return this.llmProviderBinding;
	}
	public void setLlmProviderBinding(String llmProviderBinding) {
		this.llmProviderBinding = llmProviderBinding;
	}
	public long getTokenInput() {
		return this.tokenInput;
	}
	public void setTokenInput(long tokenInput) {
		this.tokenInput = tokenInput;
	}
	public long getTokenOutput() {
		return this.tokenOutput;
	}
	public void setTokenOutput(long tokenOutput) {
		this.tokenOutput = tokenOutput;
	}
	public double getCostEstimated() {
		return this.costEstimated;
	}
	public void setCostEstimated(double costEstimated) {
		this.costEstimated = costEstimated;
	}
	public int getNumeroTransazioni() {
		return this.numeroTransazioni;
	}
	public void setNumeroTransazioni(int numeroTransazioni) {
		this.numeroTransazioni = numeroTransazioni;
	}
	public long getBandaComplessiva() {
		return this.bandaComplessiva;
	}
	public void setBandaComplessiva(long bandaComplessiva) {
		this.bandaComplessiva = bandaComplessiva;
	}
	public long getBandaInterna() {
		return this.bandaInterna;
	}
	public void setBandaInterna(long bandaInterna) {
		this.bandaInterna = bandaInterna;
	}
	public long getBandaEsterna() {
		return this.bandaEsterna;
	}
	public void setBandaEsterna(long bandaEsterna) {
		this.bandaEsterna = bandaEsterna;
	}
	public Long getLatenzaTotale() {
		return this.latenzaTotale;
	}
	public void setLatenzaTotale(Long latenzaTotale) {
		this.latenzaTotale = latenzaTotale;
	}
	public Long getLatenzaPorta() {
		return this.latenzaPorta;
	}
	public void setLatenzaPorta(Long latenzaPorta) {
		this.latenzaPorta = latenzaPorta;
	}
	public Long getLatenzaServizio() {
		return this.latenzaServizio;
	}
	public void setLatenzaServizio(Long latenzaServizio) {
		this.latenzaServizio = latenzaServizio;
	}
}
