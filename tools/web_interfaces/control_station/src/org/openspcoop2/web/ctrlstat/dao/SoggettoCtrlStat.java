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


package org.openspcoop2.web.ctrlstat.dao;

import org.openspcoop2.core.registry.Soggetto;

/**
 * SoggettoCtrlstat
 * 
 * @author Andrea Poli (apoli@link.it)
 * @author Stefano Corallo (corallo@link.it)
 * @author Sandra Giangrandi (sandra@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 * 
 */
public class SoggettoCtrlStat {

	private Soggetto soggettoReg;
	private org.openspcoop2.core.config.Soggetto soggettoConf;
	private String nome;
	private String tipo;
	private String oldNomeForUpdate;
	private String oldTipoForUpdate;

	private Long id;

	public void setId(Long id) {
		this.id = id;
	}

	public Long getId() {
		return this.id;
	}

	public SoggettoCtrlStat(Soggetto sogReg, org.openspcoop2.core.config.Soggetto sogConf) {
		this.soggettoReg = sogReg;
		this.soggettoConf = sogConf;

		if(sogReg!=null){
			this.nome = sogReg.getNome();
			this.tipo = sogReg.getTipo();
			this.id = sogReg.getId();
		}else{
			this.nome = sogConf.getNome();
			this.tipo = sogConf.getTipo();
			this.id = sogConf.getId();
		}
	}

	public Soggetto getSoggettoReg() {
		return this.soggettoReg;
	}

	public org.openspcoop2.core.config.Soggetto getSoggettoConf() {
		return this.soggettoConf;
	}

	public String getNome() {
		return this.nome;
	}

	public String getTipo() {
		return this.tipo;
	}

	public String getOldNomeForUpdate() {
		return this.oldNomeForUpdate;
	}

	public void setOldNomeForUpdate(String oldNomeForUpdate) {
		this.oldNomeForUpdate = oldNomeForUpdate;
	}

	public String getOldTipoForUpdate() {
		return this.oldTipoForUpdate;
	}

	public void setOldTipoForUpdate(String oldTipoForUpdate) {
		this.oldTipoForUpdate = oldTipoForUpdate;
	}

}
