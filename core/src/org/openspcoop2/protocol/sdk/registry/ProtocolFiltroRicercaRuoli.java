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



package org.openspcoop2.protocol.sdk.registry;

import java.util.Date;

import org.openspcoop2.core.registry.constants.RuoloContesto;
import org.openspcoop2.core.registry.constants.RuoloTipologia;

/**
 * Permette il filtro di ricerca attraverso i driver che implementano l'interfaccia 'get'
 * 
 * 
 * @author Poli Andrea (apoli@link.it)
 * @author Nardi Lorenzo (nardi@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */

public class ProtocolFiltroRicercaRuoli {

	/** Intervallo inferiore */
	private Date minDate;
	
	/** Intervallo superiore */
	private Date maxDate;
	
	/** Nome */
	private String nome;
	
	/** Tipo */
	private String tipo;
	
	/** RuoloTipologia */
	private RuoloTipologia tipologia;
	/** RuoloContesto */
	private RuoloContesto contesto;
	
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
	public RuoloTipologia getTipologia() {
		return this.tipologia;
	}
	public void setTipologia(RuoloTipologia tipologia) {
		this.tipologia = tipologia;
	}
	public RuoloContesto getContesto() {
		return this.contesto;
	}
	public void setContesto(RuoloContesto contesto) {
		this.contesto = contesto;
	}
		
	
}
