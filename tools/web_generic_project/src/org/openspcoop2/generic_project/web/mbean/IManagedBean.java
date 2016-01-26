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
package org.openspcoop2.generic_project.web.mbean;

import java.io.Serializable;

import org.openspcoop2.generic_project.web.factory.WebGenericProjectFactory;
import org.openspcoop2.generic_project.web.form.Form;
import org.openspcoop2.generic_project.web.form.SearchForm;
import org.openspcoop2.generic_project.web.mbean.exception.AnnullaException;
import org.openspcoop2.generic_project.web.mbean.exception.DeleteException;
import org.openspcoop2.generic_project.web.mbean.exception.DettaglioException;
import org.openspcoop2.generic_project.web.mbean.exception.FiltraException;
import org.openspcoop2.generic_project.web.mbean.exception.InviaException;
import org.openspcoop2.generic_project.web.mbean.exception.MenuActionException;
import org.openspcoop2.generic_project.web.mbean.exception.ModificaException;
import org.openspcoop2.generic_project.web.mbean.exception.NuovoException;
import org.openspcoop2.generic_project.web.mbean.exception.ResetException;
import org.openspcoop2.generic_project.web.mbean.exception.RestoreSearchException;

/***
 * 
 * Interfaccia che descrive le funzionalita' di base di un Bean di una pagina Web.
 * 
 * @author Pintori Giuliano (pintori@link.it)
 *  @author $Author$
 * @version $Rev$, $Date$ 
 *
 */
public interface IManagedBean<SearchFormType extends SearchForm,FormType extends Form> extends Serializable {

	// Getter/Setter per il form 
	public FormType getForm();
	public void setForm(FormType form);

	// Getter/Setter per il form di ricerca
	public SearchFormType   getSearch();
	public  void setSearch(SearchFormType search);

	public void init() throws Exception;

	public WebGenericProjectFactory getFactory();
	public void setFactory(WebGenericProjectFactory factory);

	// Elenco dei metodi con le possibili azioni disponibili per l'utente.
	public String azioneAnnulla() throws AnnullaException;
	public String azioneDelete() throws DeleteException;
	public String azioneDettaglio() throws DettaglioException;
	public String azioneFiltra() throws FiltraException;
	public String azioneInvia() throws InviaException;
	public String azioneMenuAction() throws MenuActionException;
	public String azioneModifica() throws ModificaException;
	public String azioneNuovo() throws NuovoException;
	public String azioneReset() throws ResetException;
	public String azioneRestoreSearch() throws RestoreSearchException;
}
