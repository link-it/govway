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
package org.openspcoop2.generic_project.web.form;

import java.util.Map;

import org.openspcoop2.generic_project.web.factory.FactoryException;
import org.openspcoop2.generic_project.web.factory.WebGenericProjectFactory;
import org.openspcoop2.generic_project.web.input.FormField;
import org.openspcoop2.generic_project.web.mbean.IManagedBean;

/***
 * 
 * Definisce l'interfaccia che descrive le funzionalita' base di un Form.
 * 
 * @author Pintori Giuliano (pintori@link.it)
 *  @author $Author$
 * @version $Rev$, $Date$ 
 *
 */
public interface Form {

	// Metodi per settare l'id html del form
	public String getId();
	public void setId(String id);
	
	// Metodi che definiscono la init e il reset dei campi del form.
	public void init() throws Exception;
	public void reset() ;
	
	// interfaccia per la 
	public void setObject(Object object)throws Exception;
	public Object getObject()throws Exception;
	public String valida()throws Exception;
	
	// Mappa per la gestione di tutti i field di un form
	public Map<String, FormField<?>> getFields();
	public void setFields(Map<String, FormField<?>> fields);
	public void setField(String fieldName, FormField<?> field);
	public void setField(FormField<?> field);
	public FormField<?> getField(String id) throws Exception;
	public void resetFieldValue(String id) throws Exception;
	
	// Metodi per la definizione della label del form
	public String getNomeForm(); 
	public void setNomeForm(String nomeForm);
	
	// Metodi per il controllo della visualizzazione del form
	public boolean isRendered();
	public void setRendered(boolean rendered);
	
	// Definisce la possibilita' di inserire il form in un pannello richiudibile.
	public boolean isClosable();
	public void setClosable(boolean closable);
	
	// Getter/Setter per il ManagedBean 
	public IManagedBean<SearchForm, Form> getMBean();
	public void setMBean(IManagedBean<SearchForm, Form> mBean);
	
	// Factory per i componenti
	public WebGenericProjectFactory getFactory() throws FactoryException;
	public void setFactory(WebGenericProjectFactory factory) throws FactoryException;
	
	public boolean isShowNotaCampiObbligatori();
	public void setShowNotaCampiObbligatori(boolean showNotaCampiObbligatori);
	
}
