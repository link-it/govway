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
package org.openspcoop2.generic_project.web.input;

import java.awt.font.FontRenderContext;
import java.awt.geom.AffineTransform;
import java.io.Serializable;

import org.openspcoop2.generic_project.web.form.ActionListener;
import org.openspcoop2.generic_project.web.form.Form;

/***
 * 
 * Interfaccia che descrive un elemento di input di tipo Generico.
 * 
 * @author Pintori Giuliano (pintori@link.it)
 *  @author $Author$
 * @version $Rev$, $Date$ 
 *
 */
public interface FormField<T> extends Serializable{

	// Definisce gli eventhandler necessari al field.
	public ActionListener getActionListener();
	public void setActionListener(ActionListener actionListener);

	// Getter/Setter per il form che contiene il field.
	public Form getForm();
	public void setForm(Form form);


	public boolean isAutoComplete();
	public void setAutoComplete(boolean autoComplete);


	public String getFieldsToUpdate();
	public void setFieldsToUpdate(String fieldsToUpdate);


	public String getName();
	public void setName(String name);

	// Identificativo del field
	public String getId();
	public void setId(String id);

	public T getValue();
	public void setValue(T value);


	public FieldType getType();
	public void setType(FieldType type);
	public String get_value_type() ;
	public void set_value_type(String _value_type);


	public boolean isInterval();
	public void setInterval(boolean interval);


	public String getLabel();
	public void setLabel(String label);


	public String getLabel2();
	public void setLabel2(String label);


	public T getValue2();
	public void setValue2(T value2);


	public void reset();


	public <C> C getValue(Class<C> clazz);
	public <C> C getValue2(Class<C> clazz);


	public boolean isRendered();
	public void setRendered(boolean rendered);


	public T getDefaultValue();
	public void setDefaultValue(T defaultValue);


	public T getDefaultValue2();
	public void setDefaultValue2(T defaultValue2);


	public boolean isRequired();
	public void setRequired(boolean required);


	public abstract String getRequiredMessage();
	public void setRequiredMessage(String requiredMessage);


	public boolean isEnableManualInput();
	public void setEnableManualInput(boolean enableManualInput);


	public boolean isDisabled();
	public void setDisabled(boolean disabled);


	public String getNote();
	public void setNote(String note);


	public boolean isConfirm();
	public void setConfirm(boolean confirm);


	public boolean isRedisplay();
	public void setRedisplay(boolean redisplay);


	public String getStyle();
	public void setStyle(String style);
	
	public String getStyleClass();
	public void setStyleClass(String styleClass);


	public int getWidth();
	public void setWidth(int width);


	public String getHandlerMethodPrefix();
	public void setHandlerMethodPrefix(String handlerMethodPrefix);


	public String getPattern();
	public void setPattern(String pattern);


	public String getFontName();
	public void setFontName(String fontName);


	public int getFontStyle();
	public void setFontStyle(int fontStyle);

	public int getFontSize();
	public void setFontSize(int fontSize);

	// Metodi per calcolare lo spazio occupato dalla string passata come parametro
	public Integer getFontWidth(String text);
	public Integer getFontHeight(String text);

	public AffineTransform getAffineTransform();
	public void setAffineTransform(AffineTransform affineTransform);
	public FontRenderContext getFontRenderContext();
	public void setFontRenderContext(FontRenderContext fontRenderContext);
}
