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
package org.openspcoop2.web.ctrlstat.servlet.connettori;

import java.util.Vector;

import org.openspcoop2.core.constants.CostantiDB;
import org.openspcoop2.core.registry.constants.StatiAccordo;
import org.openspcoop2.web.ctrlstat.core.ControlStationCore;
import org.openspcoop2.web.ctrlstat.servlet.sa.ServiziApplicativiCostanti;
import org.openspcoop2.web.lib.mvc.DataElement;
import org.openspcoop2.web.lib.mvc.DataElementType;
import org.openspcoop2.web.lib.mvc.TipoOperazione;

/**
 * ConnettoreJMSUtils
 *
 * @author Andrea Poli (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ConnettoreJMSUtils {

	public static void fillConnettoreConfig(org.openspcoop2.core.config.Connettore connettore,
			String jms_nome,
			String jms_tipo, String jms_user, String jms_pwd,
			String jms_nf_initial, String jms_nf_urlPkg, String jms_np_url,
			String jms_connection_factory, String jms_send_as){
		
		org.openspcoop2.core.config.Property prop = new org.openspcoop2.core.config.Property();
		prop.setNome(CostantiDB.CONNETTORE_JMS_NOME);
		prop.setValore(jms_nome);
		connettore.addProperty(prop);

		prop = new org.openspcoop2.core.config.Property();
		prop.setNome(CostantiDB.CONNETTORE_USER);
		prop.setValore(jms_user);
		connettore.addProperty(prop);

		prop = new org.openspcoop2.core.config.Property();
		prop.setNome(CostantiDB.CONNETTORE_JMS_TIPO);
		prop.setValore(jms_tipo);
		connettore.addProperty(prop);

		prop = new org.openspcoop2.core.config.Property();
		prop.setNome(CostantiDB.CONNETTORE_PWD);
		prop.setValore(jms_pwd);
		connettore.addProperty(prop);

		prop = new org.openspcoop2.core.config.Property();
		prop.setNome(CostantiDB.CONNETTORE_JMS_CONNECTION_FACTORY);
		prop.setValore(jms_connection_factory);
		connettore.addProperty(prop);

		prop = new org.openspcoop2.core.config.Property();
		prop.setNome(CostantiDB.CONNETTORE_JMS_CONTEXT_JAVA_NAMING_FACTORY_INITIAL);
		prop.setValore(jms_nf_initial);
		connettore.addProperty(prop);

		prop = new org.openspcoop2.core.config.Property();
		prop.setNome(CostantiDB.CONNETTORE_JMS_CONTEXT_JAVA_NAMING_FACTORY_URL_PKG);
		prop.setValore(jms_nf_urlPkg);
		connettore.addProperty(prop);

		prop = new org.openspcoop2.core.config.Property();
		prop.setNome(CostantiDB.CONNETTORE_JMS_CONTEXT_JAVA_NAMING_PROVIDER_URL);
		prop.setValore(jms_np_url);
		connettore.addProperty(prop);

		prop = new org.openspcoop2.core.config.Property();
		prop.setNome(CostantiDB.CONNETTORE_JMS_SEND_AS);
		prop.setValore(jms_send_as);
		connettore.addProperty(prop);
	}
	
	public static void fillConnettoreRegistry(org.openspcoop2.core.registry.Connettore connettore,
			String jms_nome,
			String jms_tipo, String jms_user, String jms_pwd,
			String jms_nf_initial, String jms_nf_urlPkg, String jms_np_url,
			String jms_connection_factory, String jms_send_as){
		
		org.openspcoop2.core.registry.Property prop = new org.openspcoop2.core.registry.Property();
		prop.setNome(CostantiDB.CONNETTORE_JMS_NOME);
		prop.setValore(jms_nome);
		connettore.addProperty(prop);

		prop = new org.openspcoop2.core.registry.Property();
		prop.setNome(CostantiDB.CONNETTORE_USER);
		prop.setValore(jms_user);
		connettore.addProperty(prop);

		prop = new org.openspcoop2.core.registry.Property();
		prop.setNome(CostantiDB.CONNETTORE_JMS_TIPO);
		prop.setValore(jms_tipo);
		connettore.addProperty(prop);

		prop = new org.openspcoop2.core.registry.Property();
		prop.setNome(CostantiDB.CONNETTORE_PWD);
		prop.setValore(jms_pwd);
		connettore.addProperty(prop);

		prop = new org.openspcoop2.core.registry.Property();
		prop.setNome(CostantiDB.CONNETTORE_JMS_CONNECTION_FACTORY);
		prop.setValore(jms_connection_factory);
		connettore.addProperty(prop);

		prop = new org.openspcoop2.core.registry.Property();
		prop.setNome(CostantiDB.CONNETTORE_JMS_CONTEXT_JAVA_NAMING_FACTORY_INITIAL);
		prop.setValore(jms_nf_initial);
		connettore.addProperty(prop);

		prop = new org.openspcoop2.core.registry.Property();
		prop.setNome(CostantiDB.CONNETTORE_JMS_CONTEXT_JAVA_NAMING_FACTORY_URL_PKG);
		prop.setValore(jms_nf_urlPkg);
		connettore.addProperty(prop);

		prop = new org.openspcoop2.core.registry.Property();
		prop.setNome(CostantiDB.CONNETTORE_JMS_CONTEXT_JAVA_NAMING_PROVIDER_URL);
		prop.setValore(jms_np_url);
		connettore.addProperty(prop);

		prop = new org.openspcoop2.core.registry.Property();
		prop.setNome(CostantiDB.CONNETTORE_JMS_SEND_AS);
		prop.setValore(jms_send_as);
		connettore.addProperty(prop);
	}
	
	public static void addJMSDati(Vector<DataElement> dati,
			String nome, String tipo,
			String user, String password, String initcont, String urlpgk,
			String provurl, String connfact, String sendas, String objectName, TipoOperazione tipoOperazione,
			String stato,
			ControlStationCore core,int pageSize){
		
		
		DataElement de = new DataElement();
		de.setLabel(ConnettoriCostanti.LABEL_CONNETTORE_JMS_CONFIGURAZIONI_CODA);
		de.setType(DataElementType.TITLE);
		dati.addElement(de);
		
		de = new DataElement();
		de.setLabel(ConnettoriCostanti.LABEL_PARAMETRO_CONNETTORE_JMS_NOME_CODA);
		de.setValue(nome);
		if(!core.isShowGestioneWorkflowStatoDocumenti() || !StatiAccordo.finale.toString().equals(stato)){
			de.setType(DataElementType.TEXT_EDIT);
			de.setRequired(true);	
		}else{
			de.setType(DataElementType.TEXT);
		}
		de.setName(ConnettoriCostanti.PARAMETRO_CONNETTORE_JMS_NOME_CODA);
		de.setSize(pageSize);
		dati.addElement(de);

		de = new DataElement();
		de.setLabel(ConnettoriCostanti.LABEL_PARAMETRO_CONNETTORE_JMS_TIPO_CODA);
		de.setType(DataElementType.SELECT);
		de.setValues(ConnettoriCostanti.TIPI_CODE_JMS);
		de.setSelected(tipo);

		de.setName(ConnettoriCostanti.PARAMETRO_CONNETTORE_JMS_TIPO_CODA);
		dati.addElement(de);
		
		de = new DataElement();
		de.setLabel(ConnettoriCostanti.LABEL_PARAMETRO_CONNETTORE_JMS_TIPO_OGGETTO_JMS);
		de.setType(DataElementType.SELECT);
		de.setValues(ConnettoriCostanti.TIPO_SEND_AS);
		de.setSelected(sendas);
		de.setName(ConnettoriCostanti.PARAMETRO_CONNETTORE_JMS_TIPO_OGGETTO_JMS);
		dati.addElement(de);

		//if ( !objectName.equals(ServiziApplicativiCostanti.OBJECT_NAME_SERVIZI_APPLICATIVI) ) {

		de = new DataElement();
		de.setLabel(ConnettoriCostanti.LABEL_CONNETTORE_JMS_CONFIGURAZIONI_CONNESIONE);
		de.setType(DataElementType.TITLE);
		dati.addElement(de);
		
		de = new DataElement();
		de.setLabel(ConnettoriCostanti.LABEL_PARAMETRO_CONNETTORE_JMS_CONNECTION_FACTORY);
		de.setValue(connfact);
		if(!core.isShowGestioneWorkflowStatoDocumenti() || !StatiAccordo.finale.toString().equals(stato)){
			de.setType(DataElementType.TEXT_EDIT);
			de.setRequired(true);	
		}else{
			de.setType(DataElementType.TEXT);
		}
		de.setName(ConnettoriCostanti.PARAMETRO_CONNETTORE_JMS_CONNECTION_FACTORY);
		de.setSize(pageSize);
		dati.addElement(de);
		
		de = new DataElement();
		de.setLabel(ConnettoriCostanti.LABEL_PARAMETRO_CONNETTORE_JMS_USERNAME);
		de.setValue(user);
		de.setType(DataElementType.TEXT_EDIT);
		de.setName(ConnettoriCostanti.PARAMETRO_CONNETTORE_JMS_USERNAME);
		de.setSize(pageSize);
		dati.addElement(de);

		de = new DataElement();
		de.setLabel(ConnettoriCostanti.LABEL_PARAMETRO_CONNETTORE_JMS_PASSWORD);
		de.setValue(password);
		de.setType(DataElementType.TEXT_EDIT);
		de.setName(ConnettoriCostanti.PARAMETRO_CONNETTORE_JMS_PASSWORD);
		de.setSize(pageSize);
		dati.addElement(de);
		//}

		de = new DataElement();
		de.setLabel(ConnettoriCostanti.LABEL_CONNETTORE_JMS_CONFIGURAZIONI_CONTESTO_JNDI);
		de.setType(DataElementType.TITLE);
		dati.addElement(de);
		
		de = new DataElement();
		de.setLabel(ConnettoriCostanti.LABEL_PARAMETRO_CONNETTORE_JMS_INIT_CTX);
		de.setValue(initcont);
		de.setType(DataElementType.TEXT_EDIT);
		de.setName(ConnettoriCostanti.PARAMETRO_CONNETTORE_JMS_INIT_CTX);
		de.setSize(pageSize);
		dati.addElement(de);

		de = new DataElement();
		de.setLabel(ConnettoriCostanti.LABEL_PARAMETRO_CONNETTORE_JMS_URL_PKG);
		de.setValue(urlpgk);
		de.setType(DataElementType.TEXT_EDIT);
		//de.setType(DataElementType.HIDDEN);
		de.setName(ConnettoriCostanti.PARAMETRO_CONNETTORE_JMS_URL_PKG);
		de.setSize(pageSize);
		dati.addElement(de);

		de = new DataElement();
		de.setLabel(ConnettoriCostanti.LABEL_PARAMETRO_CONNETTORE_JMS_PROVIDER_URL);
		de.setValue(provurl);
		de.setType(DataElementType.TEXT_EDIT);
		de.setName(ConnettoriCostanti.PARAMETRO_CONNETTORE_JMS_PROVIDER_URL);
		de.setSize(pageSize);
		dati.addElement(de);

	}
	
	
	public static void addJMSDatiAsHidden(Vector<DataElement> dati,
			String nome, String tipo,
			String user, String password, String initcont, String urlpgk,
			String provurl, String connfact, String sendas, String objectName, TipoOperazione tipoOperazione,
			String stato,
			ControlStationCore core,int pageSize){
		
		DataElement de = new DataElement();
		de.setLabel(ConnettoriCostanti.LABEL_PARAMETRO_CONNETTORE_JMS_NOME_CODA);
		de.setValue(nome);
		de.setType(DataElementType.HIDDEN);
//		if(!this.core.isShowGestioneWorkflowStatoDocumenti() || !StatiAccordo.finale.toString().equals(stato)){
//			de.setType(DataElementType.TEXT_EDIT);
//			de.setRequired(true);	
//		}else{
//			de.setType(DataElementType.TEXT);
//		}
		de.setName(ConnettoriCostanti.PARAMETRO_CONNETTORE_JMS_NOME_CODA);
		de.setSize(pageSize);
		dati.addElement(de);


		de = new DataElement();
		de.setLabel(ConnettoriCostanti.LABEL_PARAMETRO_CONNETTORE_JMS_TIPO_CODA);
		de.setType(DataElementType.HIDDEN);
		de.setValue(tipo);
//		de.setType(DataElementType.SELECT);
//		de.setValues(ConnettoriCostanti.TIPI_CODE_JMS);
//		de.setSelected(tipo);

		de.setName(ConnettoriCostanti.PARAMETRO_CONNETTORE_JMS_TIPO_CODA);
		dati.addElement(de);

		if ( !objectName.equals(ServiziApplicativiCostanti.OBJECT_NAME_SERVIZI_APPLICATIVI) ) {

			de = new DataElement();
			de.setLabel(ConnettoriCostanti.LABEL_PARAMETRO_CONNETTORE_JMS_USERNAME);
			de.setValue(user);
			de.setType(DataElementType.HIDDEN);
//			de.setType(DataElementType.TEXT_EDIT);
			de.setName(ConnettoriCostanti.PARAMETRO_CONNETTORE_JMS_USERNAME);
			de.setSize(pageSize);
			dati.addElement(de);

			de = new DataElement();
			de.setLabel(ConnettoriCostanti.LABEL_PARAMETRO_CONNETTORE_JMS_PASSWORD);
			de.setValue(password);
			de.setType(DataElementType.HIDDEN);
//			de.setType(DataElementType.TEXT_EDIT);
			de.setName(ConnettoriCostanti.PARAMETRO_CONNETTORE_JMS_PASSWORD);
			de.setSize(pageSize);
			dati.addElement(de);
		}

		de = new DataElement();
		de.setLabel(ConnettoriCostanti.LABEL_PARAMETRO_CONNETTORE_JMS_INIT_CTX);
		de.setValue(initcont);
		de.setType(DataElementType.HIDDEN);
//		de.setType(DataElementType.TEXT_EDIT);
		de.setName(ConnettoriCostanti.PARAMETRO_CONNETTORE_JMS_INIT_CTX);
		de.setSize(pageSize);
		dati.addElement(de);

		de = new DataElement();
		de.setLabel(ConnettoriCostanti.LABEL_PARAMETRO_CONNETTORE_JMS_URL_PKG);
		de.setValue(urlpgk);
		de.setType(DataElementType.TEXT_EDIT);
		//de.setType(DataElementType.HIDDEN);
		de.setName(ConnettoriCostanti.PARAMETRO_CONNETTORE_JMS_URL_PKG);
		de.setSize(pageSize);
		dati.addElement(de);

		de = new DataElement();
		de.setLabel(ConnettoriCostanti.LABEL_PARAMETRO_CONNETTORE_JMS_PROVIDER_URL);
		de.setValue(provurl);
		de.setType(DataElementType.HIDDEN);
//		de.setType(DataElementType.TEXT_EDIT);
		de.setName(ConnettoriCostanti.PARAMETRO_CONNETTORE_JMS_PROVIDER_URL);
		de.setSize(pageSize);
		dati.addElement(de);

		de = new DataElement();
		de.setLabel(ConnettoriCostanti.LABEL_PARAMETRO_CONNETTORE_JMS_CONNECTION_FACTORY);
		de.setValue(connfact);
		de.setType(DataElementType.HIDDEN);
//		if(!this.core.isShowGestioneWorkflowStatoDocumenti() || !StatiAccordo.finale.toString().equals(stato)){
//			de.setType(DataElementType.TEXT_EDIT);
//			de.setRequired(true);	
//		}else{
//			de.setType(DataElementType.TEXT);
//		}
		de.setName(ConnettoriCostanti.PARAMETRO_CONNETTORE_JMS_CONNECTION_FACTORY);
		de.setSize(pageSize);
		dati.addElement(de);

		de = new DataElement();
		de.setLabel(ConnettoriCostanti.LABEL_PARAMETRO_CONNETTORE_JMS_TIPO_OGGETTO_JMS);
		de.setType(DataElementType.HIDDEN);
		de.setValue(sendas);
//		de.setType(DataElementType.SELECT);
//		de.setValues(ConnettoriCostanti.TIPO_SEND_AS);
//		de.setSelected(sendas);
		de.setName(ConnettoriCostanti.PARAMETRO_CONNETTORE_JMS_TIPO_OGGETTO_JMS);
		dati.addElement(de);
		
	}
	
}
