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
package org.openspcoop2.generic_project.web.impl.jsf1.bean;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.HashMap;
import java.util.Map;

import org.openspcoop2.generic_project.web.factory.FactoryException;
import org.openspcoop2.generic_project.web.factory.WebGenericProjectFactory;
import org.openspcoop2.generic_project.web.factory.WebGenericProjectFactoryManager;
import org.openspcoop2.generic_project.web.impl.jsf1.CostantiJsf1Impl;
import org.openspcoop2.generic_project.web.output.OutputField;
import org.openspcoop2.generic_project.web.view.IViewBean;

/***
 * 
 * Implementazione base di un Bean da utilizzare in fase di presentazione dati.
 * 
 * 
 * @author Pintori Giuliano (pintori@link.it)
 *  @author $Author$
 * @version $Rev$, $Date$ 
 * 
 */
public abstract class BaseBean<DTOType, KeyType>  implements IViewBean<DTOType, KeyType> {

	private Map<String, OutputField<?>> fields = null;

	protected DTOType dto  ;

	protected KeyType id ;

	private WebGenericProjectFactory factory = null;

	@Override
	public abstract KeyType getId();

	@Override
	public void setId(KeyType id) {
		this.id = id;
	}

	@Override
	public void setDTO(DTOType dto) {
		this.dto = dto;
	}

	public BaseBean(){
		this.fields = new HashMap<String, OutputField<?>>();
	}

	@Override
	@SuppressWarnings("unchecked")
	public DTOType getDTO(){
		if(this.dto==null){
			try{
				ParameterizedType parameterizedType = (ParameterizedType) getClass().getGenericSuperclass();
				this.dto = ((Class<DTOType>)parameterizedType.getActualTypeArguments()[0]).newInstance();
			}catch (Exception e) {

			}
		}
		return this.dto;
	}

	@Override
	public Map<String, OutputField<?>> getFields() {
		return this.fields;
	}

	@Override
	public void setFields(Map<String, OutputField<?>> fields) {
		this.fields = fields;
	}

	@Override
	public void setField(String fieldName, OutputField<?> field){
		this.fields.put(fieldName, field);
	}

	@Override
	public void setField(OutputField<?> field) {
		if(field != null && field.getName() != null)
			this.setField(field.getName(), field);
	}

	@Override
	public WebGenericProjectFactory getFactory()
			throws FactoryException {
		if(this.factory == null)
			this.factory = WebGenericProjectFactoryManager.getInstance().getWebGenericProjectFactoryByName(CostantiJsf1Impl.FACTORY_NAME);

		return this.factory;
	}

	@Override
	public void setFactory(WebGenericProjectFactory factory)
			throws FactoryException {
		this.factory  = factory;

	}

	@Override
	public OutputField<?> getField(String id) throws Exception {
		return getFieldById(id);
	}

	@SuppressWarnings("rawtypes")
	private OutputField<?> getFieldById(String id) throws Exception{
		OutputField<?> f = null;
		try {
			// Se il field si trova all'interno della mappa dei field, lo restituisco 
			if(this.getFields() != null)
				f = this.getFields().get(id);


			if(f == null){
				Class<? extends BaseBean> myClass = this.getClass();


				Field[] fields = myClass.getDeclaredFields();

				for (Field field : fields) {
					Class<?> fieldClazz = field.getType();

					// controllo che il field implementi l'interfaccia OutputField
					if(OutputField.class.isAssignableFrom(fieldClazz)){
						//prelevo accessibilita field
						boolean accessible = field.isAccessible();
						field.setAccessible(true);
						OutputField<?> outputField = (OutputField<?>) field.get(this);
						// ripristino accessibilita
						field.setAccessible(accessible);

						if(outputField != null){
							String name = (String) outputField.getName();

							// se ho trovato il field corretto allora ho terminato la ricerca.
							if(name.equals(id)){
								f = outputField;
								
								// se non l'ho trovato dentro la mappa dei field lo aggiungo per evitare di fare sempre la reflection
								if(!this.fields.containsKey(name))
									this.setField(name, f);
																
								break;
							}
						}
					}

				}
			}
		} catch (Exception e) {
			throw e;		
		} 
		return f;
	}
}
