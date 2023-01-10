/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2023 Link.it srl (https://link.it).
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
package org.openspcoop2.generic_project.web.output;

/***
 * 
 * Interfaccia che descrive un elemento di output di tipo Immagine/Icona.
 * 
 * @author Pintori Giuliano (pintori@link.it)
 *  @author $Author$
 * @version $Rev$, $Date$ 
 *
 */
public interface Image extends OutputField<String>{
	
	// Path del file immagine da utilizzare
	public String getImage();
	public void setImage(String image);

	// Title per l'immagine
	public String getTitle();
	public void setTitle(String title);

	// Testo alternativo per l'immagine
	public String getAlt();
	public void setAlt(String alt);

}
