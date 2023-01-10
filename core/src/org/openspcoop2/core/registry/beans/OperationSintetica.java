/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2023 Link.it srl (https://link.it).
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
package org.openspcoop2.core.registry.beans;

import java.io.Serializable;

import org.openspcoop2.core.registry.Operation;
import org.openspcoop2.core.registry.constants.ProfiloCollaborazione;


/** 
 * OperationSintetica
 * 
 * @version $Rev$, $Date$
 * 
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * */

public class OperationSintetica extends org.openspcoop2.utils.beans.BaseBean implements Serializable , Cloneable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public OperationSintetica() {
	}
	public OperationSintetica(Operation param) {
		this.id = param.getId();
		this.profAzione = param.getProfAzione();
		this.idPortType = param.getIdPortType();
		this.nome = param.getNome();
		this.profiloCollaborazione = param.getProfiloCollaborazione();
		this.correlataServizio = param.getCorrelataServizio();
		this.correlata = param.getCorrelata();
	}

	private Long id;

	private java.lang.String profAzione;

	private java.lang.Long idPortType;

	private java.lang.String nome;

	private ProfiloCollaborazione profiloCollaborazione;

	private java.lang.String correlataServizio;

	private java.lang.String correlata;

	public Long getId() {
		return this.id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public java.lang.String getProfAzione() {
		return this.profAzione;
	}

	public void setProfAzione(java.lang.String profAzione) {
		this.profAzione = profAzione;
	}

	public java.lang.Long getIdPortType() {
		return this.idPortType;
	}

	public void setIdPortType(java.lang.Long idPortType) {
		this.idPortType = idPortType;
	}

	public java.lang.String getNome() {
		return this.nome;
	}

	public void setNome(java.lang.String nome) {
		this.nome = nome;
	}

	public ProfiloCollaborazione getProfiloCollaborazione() {
		return this.profiloCollaborazione;
	}

	public void setProfiloCollaborazione(ProfiloCollaborazione profiloCollaborazione) {
		this.profiloCollaborazione = profiloCollaborazione;
	}

	public java.lang.String getCorrelataServizio() {
		return this.correlataServizio;
	}

	public void setCorrelataServizio(java.lang.String correlataServizio) {
		this.correlataServizio = correlataServizio;
	}

	public java.lang.String getCorrelata() {
		return this.correlata;
	}

	public void setCorrelata(java.lang.String correlata) {
		this.correlata = correlata;
	}

}
