/*
 * GovWay - A customizable API Gateway 
 * http://www.govway.org
 *
 * from the Link.it OpenSPCoop project codebase
 * 
 * Copyright (c) 2005-2019 Link.it srl (http://link.it). 
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

import org.openspcoop2.web.ctrlstat.costanti.CostantiControlStation;
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
 * @author $Author$
 * @version $Rev$, $Date$
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
		de.setLabel(AboutCostanti.LABEL_PRODOTTO);
		de.setType(DataElementType.TITLE);
		dati.addElement(de);
		
		// versione
		de = new DataElement();
		de.setLabel(AboutCostanti.LABEL_VERSIONE);
		de.setType(DataElementType.TEXT);
		de.setValue(this.core.getProductVersion());
		dati.addElement(de);
		
		// sito
		de = new DataElement();
		de.setLabel(AboutCostanti.LABEL_SITO);
		de.setType(DataElementType.TEXT);
		de.setValue(CostantiControlStation.LABEL_OPENSPCOOP2_WEB);
		dati.addElement(de);
		
		// copyright
		de = new DataElement();
		de.setLabel(AboutCostanti.LABEL_COPYRIGHT);
		de.setType(DataElementType.TEXT);
		de.setValue(AboutCostanti.LABEL_COPYRIGHT_VALUE);
		dati.addElement(de);
		
		// sito openspcoop
		de = new DataElement();
		de.setLabel(AboutCostanti.LABEL_LICENZA);
		de.setType(DataElementType.TEXT_AREA_NO_EDIT);
		de.setValue(AboutCostanti.LICENSE);
		de.setRows(11);
		de.setCols(70);
		dati.addElement(de);
		
		return dati;
	}

}
