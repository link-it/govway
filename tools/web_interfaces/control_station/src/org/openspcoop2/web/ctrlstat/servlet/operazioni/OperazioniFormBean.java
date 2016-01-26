/*
 * OpenSPCoop v2 - Customizable SOAP Message Broker 
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
package org.openspcoop2.web.ctrlstat.servlet.operazioni;

import java.util.List;
import java.util.Vector;

import org.openspcoop2.web.lib.queue.costanti.OperationStatus;
import org.openspcoop2.web.lib.users.dao.User;

/**
 * OperazioniFormBean
 *
 * @author Pintori Giuliano (pintori@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class OperazioniFormBean {


	private String utente;

	private String tipo;

	private String method;

	private String idOperazione;

	private String dataInizio;
	private String dataFine;
	private String hostname;
	private String pezzoAny;
	private List<String> listaUtenti;	
	private List<User> listaUser;

	public String getDataInizio() {
		return this.dataInizio;
	}

	public void setDataInizio(String dataInizio) {
		this.dataInizio = dataInizio;
	}

	public String getDataFine() {
		return this.dataFine;
	}

	public void setDataFine(String dataFine) {
		this.dataFine = dataFine;
	}

	public String getHostname() {
		return this.hostname;
	}

	public void setHostname(String hostname) {
		this.hostname = hostname;
	}

	public String getPezzoAny() {
		return this.pezzoAny;
	}

	public void setPezzoAny(String pezzoAny) {
		this.pezzoAny = pezzoAny;
	}

	public List<String> getListaUtenti() {
		return this.listaUtenti;
	}

	public Vector<String> getUtenti(){
		Vector<String> values = new Vector<String>();
		if(this.listaUser != null){
			values = new Vector<String>();
			for (User user : this.listaUser) {
				values.add(user.getLogin());
			}
		}

		return values;
	}

	public void setListaUtenti(List<String> listaUtenti) {
		this.listaUtenti = listaUtenti;
	}

	public String getIdOperazione() {
		return this.idOperazione;
	}

	public void setIdOperazione(String idOperazione) {
		this.idOperazione = idOperazione;
	}

	public String getUtente() {
		return this.utente;
	}

	public void setUtente(String utente) {
		this.utente = utente;
	}

	public String getTipo() {
		return this.tipo;
	}

	public OperationStatus getTipoOperationStatus(){
		if(this.tipo != null){
			if(this.tipo.equals(OperazioniCostanti.PARAMETRO_OPERAZIONI_OPERAZIONE_IN_CODA))
				return OperationStatus.NOT_SET;
			else if(this.tipo.equals(OperazioniCostanti.PARAMETRO_OPERAZIONI_OPERAZIONE_ESEGUITE))
				return OperationStatus.SUCCESS;
			else if(this.tipo.equals(OperazioniCostanti.PARAMETRO_OPERAZIONI_OPERAZIONE_ATTESA))
				return OperationStatus.WAIT;			
			else if(this.tipo.equals(OperazioniCostanti.PARAMETRO_OPERAZIONI_OPERAZIONE_FALLITE))
				return OperationStatus.ERROR;	
			else if(this.tipo.equals(OperazioniCostanti.PARAMETRO_OPERAZIONI_OPERAZIONE_INVALIDE))
				return OperationStatus.INVALID;			
		}

		return null;
	}

	public void setTipo(String tipo) {
		this.tipo = tipo;
	}

	public String getMethod() {
		return this.method;
	}

	public void setMethod(String method) {
		this.method = method;
	}

	public List<User> getListaUser() {
		return this.listaUser;
	}

	public void setListaUser(List<User> listaUser) {
		this.listaUser = listaUser;
	}

	

	 

}
