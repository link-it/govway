/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2026 Link.it srl (https://link.it). 
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
package org.openspcoop2.web.ctrlstat.servlet.connettori;

import java.util.List;

import org.openspcoop2.core.constants.CostantiConnettori;
import org.openspcoop2.core.constants.CostantiDB;
import org.openspcoop2.core.constants.TipiConnettore;
import org.openspcoop2.pdd.core.dynamic.DynamicHelperCostanti;

import org.openspcoop2.web.ctrlstat.servlet.aps.AccordiServizioParteSpecificaCostanti;
import org.openspcoop2.web.ctrlstat.servlet.pa.PorteApplicativeCostanti;
import org.openspcoop2.web.lib.mvc.DataElement;
import org.openspcoop2.web.lib.mvc.DataElementInfo;
import org.openspcoop2.web.lib.mvc.DataElementType;

/**
 * ConnettoreCustomUtils
 *
 * @author Andrea Poli (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ConnettoreCustomUtils {
	
	/**
	 * Le proprietà gestite tramite la lista 'Proprietà' sono:
	 * - per un connettore custom, tutte le proprietà ad eccezione di 'debug' e delle extended;
	 * - per un connettore JMS, le sole proprietà prive di un campo dedicato nella maschera del connettore (es. 'context-*', 'locations-cache').
	 */
	public static boolean isConnettoreJms(String tipo, Boolean custom) {
		return TipiConnettore.JMS.getNome().equals(tipo) && (custom==null || !custom.booleanValue());
	}
	public static boolean isProprietaGestitaTramiteLista(String tipo, Boolean custom, String nome) {
		if(isConnettoreJms(tipo, custom)) {
			return CostantiDB.isConnettoreJmsProprietaAggiuntiva(nome);
		}
		return nome!=null && !CostantiDB.CONNETTORE_DEBUG.equals(nome) && !nome.startsWith(CostantiConnettori.CONNETTORE_EXTENDED_PREFIX);
	}
	
	private ConnettoreCustomUtils() {}

	public static void addProprietaConnettoriCustom(List<DataElement> dati,
			String nome, String valore,
			String servlet, String id, String nomeprov, String tipoprov,String nomeservizio,String tiposervizio, String versioneservizio,
			String myId, String correlato, String idSoggErogatore, String nomeservizioApplicativo,String idsil,String tipoAccordo,
			String provider, String accessoDaAPSParametro, String idPorta, String azioneConnettoreIdPorta,
			boolean connettoreJms)  {
		
		DataElement de = new DataElement();
		de.setType(DataElementType.TITLE);
		de.setLabel(ConnettoriCostanti.LABEL_SEZIONE_CONNETTORE_CUSTOM_PROPRIETA);
		dati.add(de);
		
		de = new DataElement();
		de.setLabel(ConnettoriCostanti.LABEL_PARAMETRO_CONNETTORE_CUSTOM_NOME);
		de.setType(DataElementType.TEXT_EDIT);
		de.setName(ConnettoriCostanti.PARAMETRO_CONNETTORE_CUSTOM_NOME);
		de.setSize(50);
		if (nome == null)
			de.setValue("");
		else
			de.setValue(nome);
		de.setRequired(true);
		dati.add(de);

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
		if(connettoreJms) {
			// per il connettore JMS nome e valore della proprietà possono contenere parti dinamiche risolte a runtime (es. ${context:NAME})
			DataElementInfo dInfoValore = new DataElementInfo(ConnettoriCostanti.LABEL_PARAMETRO_CONNETTORE_CUSTOM_VALORE);
			dInfoValore.setHeaderBody(DynamicHelperCostanti.LABEL_CONFIGURAZIONE_INFO_TRASPORTO);
			boolean fruizione = AccordiServizioParteSpecificaCostanti.SERVLET_NAME_APS_FRUITORI_CHANGE.equals(servlet);
			dInfoValore.setListBody(DynamicHelperCostanti.getLABEL_CONFIGURAZIONE_INFO_CONNETTORE_VALORI(false, fruizione, false));
			de.setInfo(dInfoValore);
		}
		dati.add(de);

		de = new DataElement();
		de.setValue(servlet);
		de.setType(DataElementType.HIDDEN);
		de.setName(ConnettoriCostanti.PARAMETRO_CONNETTORE_CUSTOM_SERVLET);
		dati.add(de);

		de = new DataElement();
		de.setValue(id);
		de.setType(DataElementType.HIDDEN);
		de.setName(ConnettoriCostanti.PARAMETRO_CONNETTORE_CUSTOM_ID);
		dati.add(de);

		de = new DataElement();
		de.setValue(nomeprov);
		de.setType(DataElementType.HIDDEN);
		de.setName(ConnettoriCostanti.PARAMETRO_CONNETTORE_CUSTOM_NOME_SOGGETTO);
		dati.add(de);

		de = new DataElement();
		de.setValue(tipoprov);
		de.setType(DataElementType.HIDDEN);
		de.setName(ConnettoriCostanti.PARAMETRO_CONNETTORE_CUSTOM_TIPO_SOGGETTO);
		dati.add(de);

		de = new DataElement();
		de.setValue(nomeservizio);
		de.setType(DataElementType.HIDDEN);
		de.setName(ConnettoriCostanti.PARAMETRO_CONNETTORE_CUSTOM_NOME_SERVIZIO);
		dati.add(de);

		de = new DataElement();
		de.setValue(tiposervizio);
		de.setType(DataElementType.HIDDEN);
		de.setName(ConnettoriCostanti.PARAMETRO_CONNETTORE_CUSTOM_TIPO_SERVIZIO);
		dati.add(de);
		
		de = new DataElement();
		de.setValue(versioneservizio);
		de.setType(DataElementType.HIDDEN);
		de.setName(ConnettoriCostanti.PARAMETRO_CONNETTORE_CUSTOM_VERSIONE_SERVIZIO);
		dati.add(de);

		de = new DataElement();
		de.setValue(myId);
		de.setType(DataElementType.HIDDEN);
		de.setName(ConnettoriCostanti.PARAMETRO_CONNETTORE_CUSTOM_MY_ID);
		dati.add(de);

		de = new DataElement();
		de.setValue(correlato);
		de.setType(DataElementType.HIDDEN);
		de.setName(ConnettoriCostanti.PARAMETRO_CONNETTORE_CUSTOM_CORRELATO);
		dati.add(de);

		de = new DataElement();
		de.setValue(idSoggErogatore);
		de.setType(DataElementType.HIDDEN);
		de.setName(ConnettoriCostanti.PARAMETRO_CONNETTORE_CUSTOM_ID_SOGGETTO_EROGATORE);
		dati.add(de);

		de = new DataElement();
		de.setValue(nomeservizioApplicativo);
		de.setType(DataElementType.HIDDEN);
		de.setName(ConnettoriCostanti.PARAMETRO_CONNETTORE_CUSTOM_NOME_SERVIZIO_APPLICATIVO);
		dati.add(de);

		de = new DataElement();
		de.setValue(idsil);
		de.setType(DataElementType.HIDDEN);
		de.setName(ConnettoriCostanti.PARAMETRO_CONNETTORE_CUSTOM_ID_SERVIZIO_APPLICATIVO);
		dati.add(de);

		de = new DataElement();
		de.setValue(tipoAccordo);
		de.setType(DataElementType.HIDDEN);
		de.setName(ConnettoriCostanti.PARAMETRO_CONNETTORE_CUSTOM_TIPO_ACCORDO);
		dati.add(de);

		de = new DataElement();
		de.setValue(provider);
		de.setType(DataElementType.HIDDEN);
		de.setName(ConnettoriCostanti.PARAMETRO_CONNETTORE_CUSTOM_ID_PROVIDER);
		dati.add(de);
		
		de = new DataElement();
		de.setValue(accessoDaAPSParametro);
		de.setType(DataElementType.HIDDEN);
		de.setName(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_CONNETTORE_DA_LISTA_APS);
		dati.add(de);
		
		de = new DataElement();
		de.setValue(idPorta);
		de.setType(DataElementType.HIDDEN);
		de.setName(ConnettoriCostanti.PARAMETRO_CONNETTORE_CUSTOM_ID_PORTA);
		dati.add(de);
		
		de = new DataElement();
		de.setValue(azioneConnettoreIdPorta);
		de.setType(DataElementType.HIDDEN);
		de.setName(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_FRUITORE_VIEW_CONNETTORE_MAPPING_AZIONE_ID_PORTA);
		dati.add(de);
	}
	
}
