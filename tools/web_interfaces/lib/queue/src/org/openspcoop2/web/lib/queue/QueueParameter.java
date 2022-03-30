/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2022 Link.it srl (https://link.it). 
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



package org.openspcoop2.web.lib.queue;

import org.openspcoop2.utils.beans.BaseBean;

/**
 * QueueParameter
 * 
 * @author Andrea Poli (apoli@link.it)
 * @author Stefano Corallo (corallo@link.it)
 * @author Sandra Giangrandi (sandra@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 * 
 */
public class QueueParameter extends BaseBean implements java.io.Serializable , Cloneable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String nome;
	private String valore;

	public QueueParameter() {
		this.nome = null;
		this.valore = null;
	}

	public QueueParameter(String nome, String valore) {
		this.nome = nome;
		this.valore = valore;
	}

	//Metodi setter e getter
	public void setNome(String n) {
		this.nome = n;
	}
	public String getNome() {
		return this.nome;
	}

	public void setValore(String v) {
		this.valore = v;
	}
	public String getValore() {
		return this.valore;
	}
}
