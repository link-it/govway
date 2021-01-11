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


package org.openspcoop2.web.lib.audit.dao;

import java.io.Serializable;

import org.openspcoop2.web.lib.audit.log.constants.Stato;
import org.openspcoop2.web.lib.audit.log.constants.Tipologia;


/**
 * Dao contenente i valori della Configurazione dell'audit (Filtri)
 * 
 * 
 * @author Andrea Poli (apoli@link.it)
 * @author Stefano Corallo (corallo@link.it)
 * @author Sandra Giangrandi (sandra@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 * 
 */
public class Filtro implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5870944802330378174L;
	
	private String username;
	private Tipologia tipoOperazione;
	private String tipoOggettoInModifica; // es. AccordoServizio etc...
	private Stato statoOperazione;
	private String dump;
	private boolean dumpExprRegular;
	
	// Action
	private boolean auditEnabled = false;
	private boolean dumpEnabled = false;
	
	private long id;

	
	
	
	
	public String getUsername() {
		return this.username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public Tipologia getTipoOperazione() {
		return this.tipoOperazione;
	}

	public void setTipoOperazione(Tipologia tipoOperazione) {
		this.tipoOperazione = tipoOperazione;
	}

	public String getTipoOggettoInModifica() {
		return this.tipoOggettoInModifica;
	}

	public void setTipoOggettoInModifica(String tipoOggettoInModifica) {
		this.tipoOggettoInModifica = tipoOggettoInModifica;
	}

	public Stato getStatoOperazione() {
		return this.statoOperazione;
	}

	public void setStatoOperazione(Stato statoOperazione) {
		this.statoOperazione = statoOperazione;
	}

	public String getDump() {
		return this.dump;
	}

	public void setDump(String dump) {
		this.dump = dump;
	}

    public boolean isDumpExprRegular() {
		return this.dumpExprRegular;
	}

	public void setDumpExprRegular(boolean dumpExprRegular) {
		this.dumpExprRegular = dumpExprRegular;
	}
	
	public boolean isAuditEnabled() {
		return this.auditEnabled;
	}

	public void setAuditEnabled(boolean auditEnabled) {
		this.auditEnabled = auditEnabled;
	}

	public boolean isDumpEnabled() {
		return this.dumpEnabled;
	}

	public void setDumpEnabled(boolean dumpEnabled) {
		this.dumpEnabled = dumpEnabled;
	}

	public long getId() {
		return this.id;
	}

	public void setId(long id) {
		this.id = id;
	}
	
	
	@Override
	public String toString(){
		StringBuilder bf = new StringBuilder();
		
		if(this.username!=null){
			bf.append("User["+this.username+"]");
		}
		if(bf.length()>0){
			bf.append(" ");
		}
		if(this.tipoOperazione!=null){
			bf.append("Op["+this.tipoOperazione.toString()+"]");
		}
		if(bf.length()>0){
			bf.append(" ");
		}
		if(this.tipoOggettoInModifica!=null){
			bf.append("Tipo["+this.tipoOggettoInModifica+"]");
		}
		if(bf.length()>0){
			bf.append(" ");
		}
		if(this.statoOperazione!=null){
			bf.append("Stato["+this.statoOperazione.toString()+"]");
		}
		if(bf.length()>0){
			bf.append(" ");
		}
		if(this.dump!=null){
			
			String descr = this.dump;
			if(this.dump.length()>30){
				descr = descr.substring(0, 27)+"...";
			}
			
			if(this.dumpExprRegular){
				bf.append("FiltroContenuto-ExprReg["+descr+"]");
			}else{
				bf.append("FiltroContenuto["+descr+"]");
			}
		}
		return bf.toString();
	}
}
