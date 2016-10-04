/*
 * OpenSPCoop - Customizable API Gateway 
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


package org.openspcoop2.utils.serialization;


/**	
 * Definisce metodi per la generazione degli Identificatori di oggetti
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */

public interface IDBuilder {

	/**
	 * Genera un identificatore che rappresenta l'oggetto passato come parametro
	 * 
	 * @param o Oggetto su cui generare un identificatore univoco
	 * @return identificatore univoco
	 */
	public String toID(Object o) throws IOException;
	
	/**
	 * Genera un identificatore che rappresenta l'oggetto passato 
	 * e il campo (dell'oggetto) passato come parametro
	 * 
	 * @param o Oggetto su cui generare un identificatore univoco
	 * @param field Field dell'oggetto
	 * @return identificatore univoco
	 */
	public String toID(Object o,String field) throws IOException;
	
	
	/**
	 * Genera il vecchio identificatore che identificava l'oggetto passato come parametro prima di un update in corso
	 * L'oggetto in corso deve essere valorizzato negli elementi old_XXX
	 * 
	 * @param o Oggetto su cui generare un identificatore univoco
	 * @return identificatore univoco
	 */
	public String toOldID(Object o) throws IOException;
	
	
	/**
	 * Ritorna i nomi delle classi degli oggetti gestiti se simpleName=false, oppure un nome descrittivo.
	 * 
	 * @return oggetti gestiti
	 * @throws DriverException
	 */
	public String[] getManagedObjects(boolean simpleName) throws IOException; 
	
	
	/**
	 * Ritorna un nome descrittivo dell'oggetto.
	 * 
	 * @param o
	 * @return nome descrittivo dell'oggetto.
	 * @throws DriverException
	 */
	public String getSimpleName(Object o) throws IOException; 
	
	
}
