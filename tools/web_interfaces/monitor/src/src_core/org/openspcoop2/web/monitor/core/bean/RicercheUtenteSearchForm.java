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
package org.openspcoop2.web.monitor.core.bean;


import java.util.List;

import javax.faces.event.ActionEvent;

import org.openspcoop2.web.lib.users.dao.User;
import org.openspcoop2.web.monitor.core.constants.Costanti;
import org.openspcoop2.web.monitor.core.core.Utility;
import org.openspcoop2.web.monitor.core.ricerche.ModuloRicerca;


/****
 * 
 * RicercheUtenteSearchForm
 * 
 * @author Pintori Giuliano (pintori@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 *
 */
public class RicercheUtenteSearchForm extends AbstractDateSearchForm implements Cloneable {
	
	private String filtroModulo;
	private String filtroModalitaRicerca;
	private String filtroVisibilita;
	
	private User user;

	@Override
	protected String ripulisciValori(){
		this.initSearchListener(null);
		return null;
	}
	
	public RicercheUtenteSearchForm(){
		super();
		ripulisciValori();
	}

	@Override
	public void initSearchListener(ActionEvent ae) {
		super.initSearchListener(ae);
		
		this.filtroModulo = Costanti.NON_SELEZIONATO;
		this.filtroModalitaRicerca = Costanti.NON_SELEZIONATO;
		this.executeQuery = false;
	}
	
	@Override
	protected void initStatoFiltroRicerca () { 
		this.setVisualizzaFiltroAperto(false); // visualizzo il filtro chiuso come sulla console
	}

	@Override
	protected String eseguiAggiorna() {
		return null;
	}
	
	@Override
	protected String eseguiFiltra() {
		return null;
	}
	
	@Override
	public String getPrintPeriodo(){
		return super.getDefaultPrintPeriodoBehaviour();
	}
	
	@Override
	public ModuloRicerca getModulo() {
		throw new UnsupportedOperationException(Costanti.OPERAZIONE_NON_CONSENTITA);
	}
	
	@Override
	public String getModalitaRicerca() {
		throw new UnsupportedOperationException(Costanti.OPERAZIONE_NON_CONSENTITA);
	}
	
	@Override
	public boolean isShowFiltroRicercheUtente() {
		throw new UnsupportedOperationException(Costanti.OPERAZIONE_NON_CONSENTITA);
	}
	
	@Override
	public boolean isVisualizzaComandoSalvaRicerca() {
		throw new UnsupportedOperationException(Costanti.OPERAZIONE_NON_CONSENTITA);
	}
	
	@Override
	public List<String> getElencoFieldRicercaDaIgnorare() {
		throw new UnsupportedOperationException(Costanti.OPERAZIONE_NON_CONSENTITA);
	}
	
	@Override
	public String getProtocolloRicerca() {
		throw new UnsupportedOperationException(Costanti.OPERAZIONE_NON_CONSENTITA);
	}
	
	@Override
	public String getSoggettoRicerca() {
		throw new UnsupportedOperationException(Costanti.OPERAZIONE_NON_CONSENTITA);
	}

	public String getFiltroModulo() {
		return this.filtroModulo;
	}

	public void setFiltroModulo(String filtroModulo) {
		this.filtroModulo = filtroModulo;
	}
	
	public void filtroModuloSelected(ActionEvent ae) {
		this.filtroModalitaRicerca = Costanti.NON_SELEZIONATO;
	}

	public String getFiltroModalitaRicerca() {
		return this.filtroModalitaRicerca;
	}

	public void setFiltroModalitaRicerca(String filtroModalitaRicerca) {
		this.filtroModalitaRicerca = filtroModalitaRicerca;
	}

	public String getFiltroVisibilita() {
		return this.filtroVisibilita;
	}

	public void setFiltroVisibilita(String filtroVisibilita) {
		this.filtroVisibilita = filtroVisibilita;
	}

	/**
	 * Se l'utente non e' impostato, allora lo prendo dal contesto di spring
	 * security Qualora l'utente fosse stato gia' impostato tramite la setUser
	 * ad esempio non sono in un contesto j2ee e quindi spring-security non lo
	 * utilizzo, allora viene ritornato l'utente gia' impostato
	 * 
	 * @return Utente collegato
	 */
	public User getUser() {

		if (this.user != null) {
			// e' stato impostato manualmente l'utente
			return this.user;
		}

		return Utility.getLoggedUtente();
	}

	public void setUser(User user) {
		this.user = user;
	}
	
	public boolean isVisualizzaFiltroModalitaRicerca() {
		String filtroModulo2 = this.getFiltroModulo();
		
		return filtroModulo2 != null && 
				(filtroModulo2.equals(ModuloRicerca.STATISTICHE.toString()) 
					|| filtroModulo2.equals(ModuloRicerca.TRANSAZIONI.toString()) 
					);
	}
	
	@Override
	public Object clone() throws CloneNotSupportedException {
		return super.clone();
	}
}
