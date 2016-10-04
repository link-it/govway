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
package org.openspcoop2.generic_project.web.table;

import java.io.Serializable;

/***
 * 
 * Interfaccia base che definisce una tabella
 * 
 * @author Pintori Giuliano (pintori@link.it)
 *  @author $Author$
 * @version $Rev$, $Date$ 
 *
 * @param <V> Oggetto che contiene i dati da visualizzare nella tabella.
 */
public interface Table<V> extends Serializable {

	// Dati da visualizzare
	public V getValue();
	public void setValue(V value);

	// Metodi per settare l'id html della tabella
	public String getId();
	public void setId(String id);

	// Metodi per la definizione testo di header della tabella
	public String getHeaderText(); 
	public void setHeaderText(String headerText);

	// Metodi per il controllo della visualizzazione della tabella
	public boolean isRendered();
	public void setRendered(boolean rendered);

	// lunghezza della tabella
	public String getWidth();
	public void setWidth(String width);

	// numero di righe da visualizzare
	public Integer getRowsToDisplay();
	public void setRowsToDisplay(Integer rowsToDisplay);

	// Mostra/Nascondi la colonna del dettaglio
	public boolean isShowDetailColumn();
	public void setShowDetailColumn(boolean showDetailColumn);

	// Metodi per la definizione testo del link di dettaglio
	public String getDetailLinkText(); 
	public void setDetailLinkText(String detailLinkText);

	// Metodi per la definizione  della posizione della colonna dettaglio
	public String getDetailColumnPosition(); 
	public void setDetailColumnPosition(String detailColumnPosition);
	
	public Object getMetadata();
	public void setMetadata(Object metadata);
	
	// Metodi per la definizione della classe CSS per l'header della tabella
	public String getHeaderClass(); 
	public void setHeaderClass(String headerClass);
	
	// Metodi per la definizione della classe CSS per il footer della tabella
	public String getFooterClass(); 
	public void setFooterClass(String footerClass);
	
	// Metodi per la definizione della classe CSS per la tabella
	public String getStyleClass(); 
	public void setStyleClass(String styleClass);
	
	

}
