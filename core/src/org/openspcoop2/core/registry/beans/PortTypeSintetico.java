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
import java.util.ArrayList;
import java.util.List;

import org.openspcoop2.core.registry.Operation;
import org.openspcoop2.core.registry.PortType;
import org.openspcoop2.core.registry.constants.ProfiloCollaborazione;


/** 
 * PortTypeSintetico
 * 
 * @version $Rev$, $Date$
 * 
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * */
public class PortTypeSintetico extends org.openspcoop2.utils.beans.BaseBean implements Serializable , Cloneable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public PortTypeSintetico() {
	}

	public PortTypeSintetico(PortType param) {
		this.id = param.getId();
		if(param.sizeAzioneList()>0) {
			for (Operation op : param.getAzioneList()) {
				this.azione.add(new OperationSintetica(op));
			}
		}
		this.profiloPT = param.getProfiloPT();
		this.idAccordo = param.getIdAccordo();
		this.descrizione = param.getDescrizione();
		this.nome = param.getNome();
		this.profiloCollaborazione = param.getProfiloCollaborazione();
	}
	
	private Long id;

	private List<OperationSintetica> azione = new ArrayList<>();

	private java.lang.String profiloPT;

	private java.lang.Long idAccordo;

	private java.lang.String descrizione;

	private java.lang.String nome;

	private ProfiloCollaborazione profiloCollaborazione;

	public Long getId() {
		return this.id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public List<OperationSintetica> getAzione() {
		return this.azione;
	}

	public void setAzione(List<OperationSintetica> azione) {
		this.azione = azione;
	}

	public java.lang.String getProfiloPT() {
		return this.profiloPT;
	}

	public void setProfiloPT(java.lang.String profiloPT) {
		this.profiloPT = profiloPT;
	}

	public java.lang.Long getIdAccordo() {
		return this.idAccordo;
	}

	public void setIdAccordo(java.lang.Long idAccordo) {
		this.idAccordo = idAccordo;
	}

	public java.lang.String getDescrizione() {
		return this.descrizione;
	}

	public void setDescrizione(java.lang.String descrizione) {
		this.descrizione = descrizione;
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


}
