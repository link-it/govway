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
package org.openspcoop2.generic_project.beans;


/**
 * AbstractModel
 * 
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public abstract class AbstractModel<T> implements IModel<T> {

	public AbstractModel(){
	}
	
	public AbstractModel(IField father){
		this._BASE = father;
	}
	
	private IField _BASE = null;
	
	@Override
	public IField getBaseField() {
		return this._BASE;
	}


	@Override
	public boolean equals(Object o){
		if(o==null){
			return false;
		}
		if(!(o instanceof IModel)){
			return false;
		}
		IModel<?> oIModel = (IModel<?>) o;
		
		// la seconda condizione _BASE in and l'ho aggiunto in seguito al fix di due figli di un oggetto che sono model con stesso tipo ma nome diverso
		// il caso viene riprodotto dal test FruitoreModel che contiene il fruitore ed una parte specifica con erogatore. 
		// Sia il fruitore che l'erogatore possiedono la stessa getModeledClass 'IdSoggettoModel' e quindi l'equals non era veritiero.
		if(this._BASE==null){
			if(oIModel.getBaseField()!=null){
				return false;
			}
			else{
				return this.getModeledClass().getName().equals(oIModel.getModeledClass().getName());
			}
		}
		else{
			return this.getModeledClass().getName().equals(oIModel.getModeledClass().getName())  
						&& this.getBaseField().equals(oIModel.getBaseField()); 
		}
		
	}
}
