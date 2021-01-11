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
package org.openspcoop2.generic_project.web.impl.jsf1.input.impl;

import org.openspcoop2.generic_project.web.factory.Costanti;
import org.openspcoop2.generic_project.web.input.FieldType;
import org.openspcoop2.generic_project.web.input.MultipleListBox;


/***
 * 
 * Implementazione base di un elemento di tipo MultipleListBox.
 * 
 * 
 * @author Pintori Giuliano (pintori@link.it)
 *  @author $Author$
 * @version $Rev$, $Date$ 
 * 
 */
public class MultipleListBoxImpl extends MultipleChoiceImpl implements MultipleListBox{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private int numeroRigheDaVisualizzare = Costanti.LISTBOX_NUMERO_RIGHE_DA_VISUALIZZARE;
	
	
	public MultipleListBoxImpl(){
		super();
		
		setType(FieldType.MULTIPLE_LISTBOX); 
	}
	
	@Override
	public int getNumeroRigheDaVisualizzare() {
		return this.numeroRigheDaVisualizzare;
	}

	@Override
	public void setNumeroRigheDaVisualizzare(int numeroRigheDaVisualizzare) {
		this.numeroRigheDaVisualizzare = numeroRigheDaVisualizzare;
	}
}
