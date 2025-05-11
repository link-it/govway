/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2025 Link.it srl (https://link.it). 
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



package org.openspcoop2.protocol.sdk.registry;

import java.util.Date;
import java.util.List;

import org.openspcoop2.core.registry.constants.ServiceBinding;


/**
 * Permette il filtro di ricerca attraverso i driver che implementano l'interfaccia 'get'
 * 
 * 
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */

public class ProtocolFiltroRicercaGruppi {

	/** Intervallo inferiore */
	private Date minDate;
	
	/** Intervallo superiore */
	private Date maxDate;
	
	/** Nome */
	private String nome;
	
	/** Tipo */
	private String tipo;
	
	/** ServiceBinding */
	private ServiceBinding serviceBinding;
	private boolean ordinaDataRegistrazione = false;

	private String protocollo;
	private List<String> protocolli;
	
	public Date getMinDate() {
		return this.minDate;
	}
	public void setMinDate(Date minDate) {
		this.minDate = minDate;
	}
	public Date getMaxDate() {
		return this.maxDate;
	}
	public void setMaxDate(Date maxDate) {
		this.maxDate = maxDate;
	}
	public String getNome() {
		return this.nome;
	}
	public void setNome(String nome) {
		this.nome = nome;
	}
	public String getTipo() {
		return this.tipo;
	}
	public void setTipo(String tipo) {
		this.tipo = tipo;
	}
	public ServiceBinding getServiceBinding() {
		return this.serviceBinding;
	}
	public void setServiceBinding(ServiceBinding serviceBinding) {
		this.serviceBinding = serviceBinding;
	}
	public boolean isOrdinaDataRegistrazione() {
		return this.ordinaDataRegistrazione;
	}
	public void setOrdinaDataRegistrazione(boolean ordinaDataRegistrazione) {
		this.ordinaDataRegistrazione = ordinaDataRegistrazione;
	}
	public String getProtocollo() {
		return this.protocollo;
	}
	public void setProtocollo(String protocollo) {
		this.protocollo = protocollo;
	}
	public List<String> getProtocolli() {
		return this.protocolli;
	}
	public void setProtocolli(List<String> protocolli) {
		this.protocolli = protocolli;
	}
	
}
