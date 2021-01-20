/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2021 Link.it srl (https://link.it). 
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

package org.openspcoop2.core.allarmi.utils;

/**
 * FiltroRicercaAllarmi
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class FiltroRicercaAllarmi {

	private String tipo;
	
	private String idParametroCluster;
	private String idCluster;
	private boolean idClusterOpzionale;
	
	private String idParametro;
	private String valoreParametro;
	
	private boolean recuperaSoloAllarmiInStatoDiversoDaOk;

	@Override
	public String toString() {
		return toString("\n");
	}
	public String toString(String separator) {
		StringBuilder sb = new StringBuilder();
		sb.append("tipo:").append(this.tipo);
		sb.append(separator).append("idParametroCluster:").append(this.idParametroCluster!=null ? this.idParametroCluster : "");
		sb.append(separator).append("idCluster:").append(this.idCluster!=null ? this.idCluster : "");
		sb.append(separator).append("idClusterOpzionale:").append(this.idClusterOpzionale);
		sb.append(separator).append("idParametro:").append(this.idParametro!=null ? this.idParametro : "");
		sb.append(separator).append("valoreParametro:").append(this.valoreParametro!=null ? this.valoreParametro : "");
		sb.append(separator).append("diversiOk:").append(this.recuperaSoloAllarmiInStatoDiversoDaOk);
		return sb.toString();
	} 
	
	
	public String getTipo() {
		return this.tipo;
	}

	public void setTipo(String tipo) {
		this.tipo = tipo;
	}

	public String getIdParametroCluster() {
		return this.idParametroCluster;
	}

	public void setIdParametroCluster(String idParametroCluster) {
		this.idParametroCluster = idParametroCluster;
	}

	public String getIdCluster() {
		return this.idCluster;
	}

	public void setIdCluster(String idCluster) {
		this.idCluster = idCluster;
	}

	public boolean isIdClusterOpzionale() {
		return this.idClusterOpzionale;
	}

	public void setIdClusterOpzionale(boolean idClusterOpzionale) {
		this.idClusterOpzionale = idClusterOpzionale;
	}

	public String getIdParametro() {
		return this.idParametro;
	}

	public void setIdParametro(String idParametro) {
		this.idParametro = idParametro;
	}

	public String getValoreParametro() {
		return this.valoreParametro;
	}

	public void setValoreParametro(String valoreParametro) {
		this.valoreParametro = valoreParametro;
	}

	public boolean isRecuperaSoloAllarmiInStatoDiversoDaOk() {
		return this.recuperaSoloAllarmiInStatoDiversoDaOk;
	}

	public void setRecuperaSoloAllarmiInStatoDiversoDaOk(boolean recuperaSoloAllarmiInStatoDiversoDaOk) {
		this.recuperaSoloAllarmiInStatoDiversoDaOk = recuperaSoloAllarmiInStatoDiversoDaOk;
	}
	
}
