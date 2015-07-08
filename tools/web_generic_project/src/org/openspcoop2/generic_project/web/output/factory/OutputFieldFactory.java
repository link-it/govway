/*
 * OpenSPCoop v2 - Customizable SOAP Message Broker 
 * http://www.openspcoop2.org
 * 
 * Copyright (c) 2005-2015 Link.it srl (http://link.it).
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
package org.openspcoop2.generic_project.web.output.factory;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import org.openspcoop2.generic_project.web.output.Button;
import org.openspcoop2.generic_project.web.output.DateTime;
import org.openspcoop2.generic_project.web.output.Image;
import org.openspcoop2.generic_project.web.output.OutputField;
import org.openspcoop2.generic_project.web.output.OutputGroup;
import org.openspcoop2.generic_project.web.output.OutputNumber;
import org.openspcoop2.generic_project.web.output.Text;

/***
 * 
 * Interfaccia base che definisce la factory degli elementi di output.
 * 
 * @author Pintori Giuliano (pintori@link.it)
 *  @author $Author$
 * @version $Rev$, $Date$ 
 *
 */
public interface OutputFieldFactory  extends Serializable{

	// Costruttori Elementi Text
	public Text createText();
	public Text createText(String name,String label);
	public Text createText(String name,String label, String value);
	
	// Costruttori Elementi Date
	public DateTime createDateTime();
	public DateTime createDateTime(String name,String label);
	public DateTime createDateTime(String name,String label,String pattern);
	public DateTime createDateTime(String name,String label, Date value);
	public DateTime createDateTime(String name,String label, String pattern, Date value);
	
	//Costruttori Elementi Numerici
	public OutputNumber createNumber();
	public OutputNumber createNumber(String name,String label);
	public OutputNumber createNumber(String name,String label, Number value);
	
	// Costruttori Elementi Immagine
	public Image createImage();
	public Image createImage(String name,String label);
	public Image createImage(String name,String label,String image);
	public Image createImage(String name,String label,String image, String title);
	public Image createImage(String name,String label,String image, String title, String alt);
	
	// Costruttori Elementi Button
	public Button createButton();
	public Button createButton(String name,String label);
	public Button createButton(String name,String label,String href);
	public Button createButton(String name,String label,String href, String image);
	public Button createButton(String name,String label,String href, String image, String title);
	public Button createButton(String name,String label,String href, String image, String title, String alt);
	
	// grouping
	public OutputGroup createOutputGroup();
	public OutputGroup createOutputGroup(String id);
	public OutputGroup createOutputGroup(String id, String label);
	public OutputGroup createOutputGroup(String id, Integer columns);
	public OutputGroup createOutputGroup(String id, String label, Integer columns);
	public OutputGroup createOutputGroup(String id, List<OutputField<?>> listaOutput);
	public OutputGroup createOutputGroup(String id, Integer columns,List<OutputField<?>> listaOutput);
	public OutputGroup createOutputGroup(String id, String label, Integer columns,List<OutputField<?>> listaOutput);
	
	
}
