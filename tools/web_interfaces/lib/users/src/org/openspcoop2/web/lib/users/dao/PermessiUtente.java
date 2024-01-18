/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2024 Link.it srl (https://link.it). 
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


package org.openspcoop2.web.lib.users.dao;

import java.io.Serializable;

/**
 * Permessi di un utente
 *
 * 
 * @author Andrea Poli (apoli@link.it)
 * @author Stefano Corallo (corallo@link.it)
 * @author Sandra Giangrandi (sandra@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 * 
 */
public class PermessiUtente implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	/** Permessi di visualizzazione dei servizi */
	private boolean servizi;
	/** Permessi di visualizzazione della diagnostica */
	private boolean diagnostica;
	/** Permessi di visualizzazione della reportistica */
	private boolean reportistica;
	/** Permessi di visualizzazione del sistema */
	private boolean sistema;
	/** Permessi di visualizzazione del monitoraggio applicativo */
	private boolean codeMessaggi;
	/** Permessi di visualizzazione del auditing */
	private boolean auditing;
	/** Permessi di visualizzazione del utenti */
	private boolean utenti;
	/** Permessi di visualizzazione degli accordi di cooperazione */
	private boolean accordiCooperazione;
	
	
	@Override
	public String toString(){
		StringBuilder bf = new StringBuilder();
		if(this.servizi)
			bf.append(Permessi.SERVIZI.toString());
		if(this.diagnostica)
			bf.append(Permessi.DIAGNOSTICA.toString());
		if(this.reportistica)
			bf.append(Permessi.REPORTISTICA.toString());
		if(this.sistema)
			bf.append(Permessi.SISTEMA.toString());
		if(this.codeMessaggi)
			bf.append(Permessi.CODE_MESSAGGI.toString());
		if(this.auditing)
			bf.append(Permessi.AUDITING.toString());
		if(this.utenti)
			bf.append(Permessi.UTENTI.toString());
		if(this.accordiCooperazione)
			bf.append(Permessi.ACCORDI_COOPERAZIONE.toString());
		return bf.toString();
	}
	
	public String toString(String separatore){
		PermessiUtente p = new PermessiUtente();
		p.setAuditing(true);
		p.setCodeMessaggi(true);
		p.setDiagnostica(true);
		p.setReportistica(true);
		p.setServizi(true);
		p.setSistema(true);
		p.setUtenti(true);
		p.setAccordiCooperazione(true);
		return this.toString(separatore, p);
	}
	public String toString(String separatore,PermessiUtente maschera){
		StringBuilder bf = new StringBuilder();
		if(maschera.servizi){
			if(this.servizi){
				if(bf.length()>0)
					bf.append(separatore);
				bf.append(Permessi.SERVIZI.toString());
			}
		}
		if(maschera.diagnostica){
			if(this.diagnostica){
				if(bf.length()>0)
					bf.append(separatore);
				bf.append(Permessi.DIAGNOSTICA.toString());
			}
		}
		if(maschera.reportistica){
			if(this.reportistica){
				if(bf.length()>0)
					bf.append(separatore);
				bf.append(Permessi.REPORTISTICA.toString());
			}
		}
		if(maschera.sistema){
			if(this.sistema){
				if(bf.length()>0)
					bf.append(separatore);
				bf.append(Permessi.SISTEMA.toString());
			}
		}
		if(maschera.codeMessaggi){
			if(this.codeMessaggi){
				if(bf.length()>0)
					bf.append(separatore);
				bf.append(Permessi.CODE_MESSAGGI.toString());
			}
		}
		if(maschera.auditing){
			if(this.auditing){
				if(bf.length()>0)
					bf.append(separatore);
				bf.append(Permessi.AUDITING.toString());
			}
		}
		if(maschera.utenti){
			if(this.utenti){
				if(bf.length()>0)
					bf.append(separatore);
				bf.append(Permessi.UTENTI.toString());
			}
		}
		if(maschera.accordiCooperazione){
			if(this.accordiCooperazione){
				if(bf.length()>0)
					bf.append(separatore);
				bf.append(Permessi.ACCORDI_COOPERAZIONE.toString());
			}
		}
		return bf.toString();
	}
	
	public static PermessiUtente toPermessiUtente(String value){
		PermessiUtente permUtenti = new PermessiUtente();
		if(value.contains(Permessi.SERVIZI.toString())){
			permUtenti.setServizi(true);
		}
		if(value.contains(Permessi.DIAGNOSTICA.toString())){
			permUtenti.setDiagnostica(true);
		}
		if(value.contains(Permessi.REPORTISTICA.toString())){
			permUtenti.setReportistica(true);
		}
		if(value.contains(Permessi.SISTEMA.toString())){
			permUtenti.setSistema(true);
		}
		if(value.contains(Permessi.CODE_MESSAGGI.toString())){
			permUtenti.setCodeMessaggi(true);
		}
		if(value.contains(Permessi.AUDITING.toString())){
			permUtenti.setAuditing(true);
		}
		if(value.contains(Permessi.UTENTI.toString())){
			permUtenti.setUtenti(true);
		}
		if(value.contains(Permessi.ACCORDI_COOPERAZIONE.toString())){
			permUtenti.setAccordiCooperazione(true);
		}
		return permUtenti;
	}
	
	
	
	public boolean isAccordiCooperazione() {
		return this.accordiCooperazione;
	}

	public void setAccordiCooperazione(boolean accordiCooperazione) {
		this.accordiCooperazione = accordiCooperazione;
	}

	public boolean isServizi() {
		return this.servizi;
	}


	public void setServizi(boolean servizi) {
		this.servizi = servizi;
	}


	public boolean isDiagnostica() {
		return this.diagnostica;
	}


	public void setDiagnostica(boolean diagnostica) {
		this.diagnostica = diagnostica;
	}
	
	
	public boolean isReportistica() {
		return this.reportistica;
	}


	public void setReportistica(boolean reportistica) {
		this.reportistica = reportistica;
	}


	public boolean isSistema() {
		return this.sistema;
	}


	public void setSistema(boolean sistema) {
		this.sistema = sistema;
	}


	public boolean isCodeMessaggi() {
		return this.codeMessaggi;
	}


	public void setCodeMessaggi(boolean codeMessaggi) {
		this.codeMessaggi = codeMessaggi;
	}


	public boolean isAuditing() {
		return this.auditing;
	}


	public void setAuditing(boolean auditing) {
		this.auditing = auditing;
	}


	public boolean isUtenti() {
		return this.utenti;
	}


	public void setUtenti(boolean utenti) {
		this.utenti = utenti;
	}
	
	
	public boolean or(PermessiUtente permessi){
		boolean ok = false;
		if(this.isAuditing()){
			if(permessi.isAuditing()){
				ok = true;
			}
		}
		if(this.isCodeMessaggi()){
			if(permessi.isCodeMessaggi()){
				ok = true;
			}
		}
		if(this.isDiagnostica()){
			if(permessi.isDiagnostica()){
				ok = true;
			}
		}
		if(this.isReportistica()){
			if(permessi.isReportistica()){
				ok = true;
			}
		}
		if(this.isServizi()){
			if(permessi.isServizi()){
				ok = true;
			}
		}
		if(this.isSistema()){
			if(permessi.isSistema()){
				ok = true;
			}
		}
		if(this.isUtenti()){
			if(permessi.isUtenti()){
				ok = true;
			}
		}
		if(this.isAccordiCooperazione()){
			if(permessi.isAccordiCooperazione()){
				ok = true;
			}
		}
		return ok;
	}
	
	public boolean and(PermessiUtente permessi){
		if(this.isAuditing()){
			if(permessi.isAuditing()){
				return false;
			}
		}
		if(this.isCodeMessaggi()){
			if(permessi.isCodeMessaggi()){
				return false;
			}
		}
		if(this.isDiagnostica()){
			if(permessi.isDiagnostica()){
				return false;
			}
		}
		if(this.isReportistica()){
			if(permessi.isReportistica()){
				return false;
			}
		}
		if(this.isServizi()){
			if(permessi.isServizi()){
				return false;
			}
		}
		if(this.isSistema()){
			if(permessi.isSistema()){
				return false;
			}
		}
		if(this.isUtenti()){
			if(permessi.isUtenti()){
				return false;
			}
		}
		if(this.isAccordiCooperazione()){
			if(permessi.isAccordiCooperazione()){
				return false;
			}
		}
		return true;
	}
}
