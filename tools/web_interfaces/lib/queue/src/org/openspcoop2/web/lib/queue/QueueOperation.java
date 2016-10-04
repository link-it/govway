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



package org.openspcoop2.web.lib.queue;

import java.util.Vector;

import org.openspcoop2.utils.beans.BaseBean;
import org.openspcoop2.web.lib.queue.costanti.Operazione;
import org.openspcoop2.web.lib.queue.costanti.TipoOperazione;

/**
 * QueueOperation
 * 
 * @author Andrea Poli (apoli@link.it)
 * @author Stefano Corallo (corallo@link.it)
 * @author Sandra Giangrandi (sandra@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 * 
 */
public class QueueOperation extends BaseBean implements java.io.Serializable , Cloneable {


	private static final long serialVersionUID = 2650366189104344387L;
	//campi comuni
	private TipoOperazione tipoOperazione; 
	private Operazione operazione; 
	private String superuser;
	private Vector<QueueParameter> parameters;

	//Costruttore
	public QueueOperation() {
		this.parameters = new Vector<QueueParameter>();
	}

	//Metodi setter e getter per tutte le operazioni
	public void setTipoOperazione(TipoOperazione to) {
		this.tipoOperazione = to;
	}
	public TipoOperazione getTipoOperazione() {
		return this.tipoOperazione;
	}

	public void setOperazione(Operazione op) {
		this.operazione = op;
	}
	public Operazione getOperazione() {
		return this.operazione;
	}

	public void setSuperuser(String s) {
		this.superuser = s;
	}
	public String getSuperuser() {
		return this.superuser;
	}

	public void addParametro(QueueParameter pp) {
		this.parameters.add(pp);
	}
	public QueueParameter getParametro(int index) {
		return this.parameters.get(index);
	}
	public int sizeParametri() {
		return this.parameters.size();
	}
	public Vector<QueueParameter> getParametri() {
		return this.parameters;
	}
	
	public Vector<QueueParameter> getParameters() {
		return this.parameters;
	}
	public void setParameters(Vector<QueueParameter> parameters) {
		this.parameters = parameters;
	}
}
