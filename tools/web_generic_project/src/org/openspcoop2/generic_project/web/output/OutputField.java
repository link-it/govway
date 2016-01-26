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
package org.openspcoop2.generic_project.web.output;

import java.io.Serializable;

/***
 * 
 * Interfaccia che descrive un elemento di output generico.
 * 
 * @author Pintori Giuliano (pintori@link.it)
 *  @author $Author$
 * @version $Rev$, $Date$ 
 *
 */
public interface OutputField<T> extends Serializable{

	// Nome del field
	public String getName();
	public void setName(String name);

	// Identificativo del field
	public String getId();
	public void setId(String id);

	// Label del field
	public abstract String getLabel();
	public void setLabel(String label);

	// Valore del field
	public abstract T getValue();
	public void setValue(T value);

	// Default Value del field
	public abstract T getDefaultValue();
	public void setDefaultValue(T defaultValue);

	// controlla se il field deve essere visualizzato
	public boolean isRendered();
	public void setRendered(boolean rendered);

	// Controlla se il field contiene un valore da mostrare Secretato
	public boolean isSecret();
	public void setSecret(boolean secret);

	// Tipo del field
	public OutputType getType();
	public void setType(OutputType type);
	public String get_value_type() ;
	public void set_value_type(String _value_type);

	// Indica se il field deve essere visualizzato all'interno di un gruppo di fields
	public boolean isInsideGroup();
	public void setInsideGroup(boolean insideGroup);

	// Pattern da visualizzare
	public String getPattern();
	public void setPattern(String pattern);

	// Indica se bisogna fare l'escape html del valore
	public boolean isEscape();
	public void setEscape(boolean escape);

	// Classe/i CSS della Label
	public String getLabelStyleClass();
	public void setLabelStyleClass(String labelStyleClass);

	// Classe/i CSS del Value
	public String getValueStyleClass();
	public void setValueStyleClass(String valueStyleClass);

	// Classe/i CSS che controllano la disposizione dell'elemento in caso di visualizzazione fuori da un gruppo
	public String getColumnClasses();
	public void setColumnClasses(String columnClasses);

	// Classe/i CSS del container che contiene la coppia Label/Valore in caso di visualizzazione fuori da un gruppo
	public String getStyleClass();
	public void setStyleClass(String styleClass);

	// Classe/i CSS per la colonna della tabella quando il field viene inserito in una tabella
	public String getTableColumnStyleClass();
	public void setTableColumnStyleClass(String tableColumnStyleClass);

}
