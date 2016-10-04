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
package org.openspcoop2.web.ctrlstat.servlet.connettori;

import java.util.Vector;

import org.openspcoop2.web.lib.mvc.DataElement;
import org.openspcoop2.web.lib.mvc.DataElementType;

/**
 * ConnettoreCustomUtils
 *
 * @author Andrea Poli (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ConnettoreCustomUtils {

	public static void addProprietaConnettoriCustom(Vector<DataElement> dati,
			String nome, String valore,
			String servlet, String id, String nomeprov, String tipoprov,String nomeservizio,String tiposervizio,
			String myId, String correlato, String idSoggErogatore, String nomeservizioApplicativo,String idsil,String tipoAccordo,
			String provider)  {

		DataElement de = new DataElement();
		de.setLabel(ConnettoriCostanti.LABEL_PARAMETRO_CONNETTORE_CUSTOM_NOME);
		de.setType(DataElementType.TEXT_EDIT);
		de.setName(ConnettoriCostanti.PARAMETRO_CONNETTORE_CUSTOM_NOME);
		de.setSize(50);
		if (nome == null)
			de.setValue("");
		else
			de.setValue(nome);
		de.setRequired(true);
		dati.addElement(de);

		de = new DataElement();
		de.setLabel(ConnettoriCostanti.LABEL_PARAMETRO_CONNETTORE_CUSTOM_VALORE);
		de.setType(DataElementType.TEXT_EDIT);
		de.setName(ConnettoriCostanti.PARAMETRO_CONNETTORE_CUSTOM_VALORE);
		de.setSize(50);
		if (valore == null)
			de.setValue("");
		else
			de.setValue(valore);
		de.setRequired(true);
		dati.addElement(de);

		de = new DataElement();
		de.setValue(servlet);
		de.setType(DataElementType.HIDDEN);
		de.setName(ConnettoriCostanti.PARAMETRO_CONNETTORE_CUSTOM_SERVLET);
		dati.addElement(de);

		de = new DataElement();
		de.setValue(id);
		de.setType(DataElementType.HIDDEN);
		de.setName(ConnettoriCostanti.PARAMETRO_CONNETTORE_CUSTOM_ID);
		dati.addElement(de);

		de = new DataElement();
		de.setValue(nomeprov);
		de.setType(DataElementType.HIDDEN);
		de.setName(ConnettoriCostanti.PARAMETRO_CONNETTORE_CUSTOM_NOME_SOGGETTO);
		dati.addElement(de);

		de = new DataElement();
		de.setValue(tipoprov);
		de.setType(DataElementType.HIDDEN);
		de.setName(ConnettoriCostanti.PARAMETRO_CONNETTORE_CUSTOM_TIPO_SOGGETTO);
		dati.addElement(de);

		de = new DataElement();
		de.setValue(nomeservizio);
		de.setType(DataElementType.HIDDEN);
		de.setName(ConnettoriCostanti.PARAMETRO_CONNETTORE_CUSTOM_NOME_SERVIZIO);
		dati.addElement(de);

		de = new DataElement();
		de.setValue(tiposervizio);
		de.setType(DataElementType.HIDDEN);
		de.setName(ConnettoriCostanti.PARAMETRO_CONNETTORE_CUSTOM_TIPO_SERVIZIO);
		dati.addElement(de);

		de = new DataElement();
		de.setValue(myId);
		de.setType(DataElementType.HIDDEN);
		de.setName(ConnettoriCostanti.PARAMETRO_CONNETTORE_CUSTOM_MY_ID);
		dati.addElement(de);

		de = new DataElement();
		de.setValue(correlato);
		de.setType(DataElementType.HIDDEN);
		de.setName(ConnettoriCostanti.PARAMETRO_CONNETTORE_CUSTOM_CORRELATO);
		dati.addElement(de);

		de = new DataElement();
		de.setValue(idSoggErogatore);
		de.setType(DataElementType.HIDDEN);
		de.setName(ConnettoriCostanti.PARAMETRO_CONNETTORE_CUSTOM_ID_SOGGETTO_EROGATORE);
		dati.addElement(de);

		de = new DataElement();
		de.setValue(nomeservizioApplicativo);
		de.setType(DataElementType.HIDDEN);
		de.setName(ConnettoriCostanti.PARAMETRO_CONNETTORE_CUSTOM_NOME_SERVIZIO_APPLICATIVO);
		dati.addElement(de);

		de = new DataElement();
		de.setValue(idsil);
		de.setType(DataElementType.HIDDEN);
		de.setName(ConnettoriCostanti.PARAMETRO_CONNETTORE_CUSTOM_ID_SERVIZIO_APPLICATIVO);
		dati.addElement(de);

		de = new DataElement();
		de.setValue(tipoAccordo);
		de.setType(DataElementType.HIDDEN);
		de.setName(ConnettoriCostanti.PARAMETRO_CONNETTORE_CUSTOM_TIPO_ACCORDO);
		dati.addElement(de);

		de = new DataElement();
		de.setValue(provider);
		de.setType(DataElementType.HIDDEN);
		de.setName(ConnettoriCostanti.PARAMETRO_CONNETTORE_CUSTOM_ID_PROVIDER);
		dati.addElement(de);
		
	}
	
}
