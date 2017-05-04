/*
 * OpenSPCoop - Customizable API Gateway 
 * http://www.openspcoop2.org
 * 
 * Copyright (c) 2005-2017 Link.it srl (http://link.it). 
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
package org.openspcoop2.web.ctrlstat.servlet.about;

import java.util.Vector;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.openspcoop2.web.ctrlstat.servlet.ConsoleHelper;
import org.openspcoop2.web.lib.mvc.DataElement;
import org.openspcoop2.web.lib.mvc.DataElementType;
import org.openspcoop2.web.lib.mvc.PageData;
import org.openspcoop2.web.lib.mvc.TipoOperazione;

/**
 * AboutHelper
 *
 * @author Poli Andrea (apoli@link.it)
 * @author Pintori Giuliano (pintori@link.it)
 * @author $Author: pintori $
 * @version $Rev: 12564 $, $Date: 2017-01-11 14:31:31 +0100(mer, 11 gen 2017) $
 */
public class AboutHelper extends ConsoleHelper {

	public AboutHelper(HttpServletRequest request, PageData pd, 
			HttpSession session) throws Exception {
		super(request, pd,  session);
	}

	public Vector<DataElement> addAboutToDati(Vector<DataElement> dati,TipoOperazione tipoOperazione, String userLogin) {
		 
		DataElement de = new DataElement();

		// titolo sezione
		de = new DataElement();
		de.setLabel(AboutCostanti.LABEL_LICENZA + " TODO");
		de.setType(DataElementType.TITLE);
		dati.addElement(de);
		
		// tipo licenza
		de = new DataElement();
		de.setLabel(AboutCostanti.LABEL_TIPO);
		de.setType(DataElementType.TEXT);
		de.setValue("TODO");
		dati.addElement(de);
		
		// numero licenza
		de = new DataElement();
		de.setLabel(AboutCostanti.LABEL_NUMERO);
		de.setType(DataElementType.TEXT);
		de.setValue("TODO");
		dati.addElement(de);
		
		// intestata
		de = new DataElement();
		de.setLabel(AboutCostanti.LABEL_INTESTATA_A);
		de.setType(DataElementType.TEXT);
		de.setValue("TODO");
		dati.addElement(de);
		
		// sito openspcoop
		de = new DataElement();
		de.setLabel(AboutCostanti.LABEL_OPENSPCOOP2);
		de.setType(DataElementType.LINK);
		de.setValue(AboutCostanti.LABEL_OPENSPCOOP2);
		de.setUrl(AboutCostanti.LABEL_OPENSPCOOP2_WEB);
		dati.addElement(de);
		
		return dati;
	}

}
