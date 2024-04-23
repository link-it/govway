/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2024 Link.it srl (https://link.it). 
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

import org.openspcoop2.core.constants.CostantiDB;
import org.openspcoop2.core.registry.constants.StatiAccordo;
import org.openspcoop2.web.ctrlstat.core.ControlStationCore;
import org.openspcoop2.web.ctrlstat.driver.DriverControlStationException;
import org.openspcoop2.web.ctrlstat.servlet.ConsoleHelper;
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
	
	private ConnettoreJMSUtils() {}

	public static void fillConnettoreConfig(org.openspcoop2.core.config.Connettore connettore,
			String jmsNome,
			String jmsTipo, String jmsUser, String jmsPwd,
			String jmsNfInitial, String jmsNfUrlPkg, String jmsNpUrl,
			String jmsConnectionFactory, String jmsSendAs){
		
		org.openspcoop2.core.config.Property prop = new org.openspcoop2.core.config.Property();
		prop.setNome(CostantiDB.CONNETTORE_JMS_NOME);
		prop.setValore(jmsNome);
		connettore.addProperty(prop);

		prop = new org.openspcoop2.core.config.Property();
		prop.setNome(CostantiDB.CONNETTORE_USER);
		prop.setValore(jmsUser);
		connettore.addProperty(prop);

		prop = new org.openspcoop2.core.config.Property();
		prop.setNome(CostantiDB.CONNETTORE_JMS_TIPO);
		prop.setValore(jmsTipo);
		connettore.addProperty(prop);

		prop = new org.openspcoop2.core.config.Property();
		prop.setNome(CostantiDB.CONNETTORE_PWD);
		prop.setValore(jmsPwd);
		connettore.addProperty(prop);

		prop = new org.openspcoop2.core.config.Property();
		prop.setNome(CostantiDB.CONNETTORE_JMS_CONNECTION_FACTORY);
		prop.setValore(jmsConnectionFactory);
		connettore.addProperty(prop);

		prop = new org.openspcoop2.core.config.Property();
		prop.setNome(CostantiDB.CONNETTORE_JMS_CONTEXT_JAVA_NAMING_FACTORY_INITIAL);
		prop.setValore(jmsNfInitial);
		connettore.addProperty(prop);

		prop = new org.openspcoop2.core.config.Property();
		prop.setNome(CostantiDB.CONNETTORE_JMS_CONTEXT_JAVA_NAMING_FACTORY_URL_PKG);
		prop.setValore(jmsNfUrlPkg);
		connettore.addProperty(prop);

		prop = new org.openspcoop2.core.config.Property();
		prop.setNome(CostantiDB.CONNETTORE_JMS_CONTEXT_JAVA_NAMING_PROVIDER_URL);
		prop.setValore(jmsNpUrl);
		connettore.addProperty(prop);

		prop = new org.openspcoop2.core.config.Property();
		prop.setNome(CostantiDB.CONNETTORE_JMS_SEND_AS);
		prop.setValore(jmsSendAs);
		connettore.addProperty(prop);
	}
	
	public static void fillConnettoreRegistry(org.openspcoop2.core.registry.Connettore connettore,
			String jmsNome,
			String jmsTipo, String jmsUser, String jmsPwd,
			String jmsNfInitial, String jmsNfUrlPkg, String jmsNpUrl,
			String jmsConnectionFactory, String jmsSendAs){
		
		org.openspcoop2.core.registry.Property prop = new org.openspcoop2.core.registry.Property();
		prop.setNome(CostantiDB.CONNETTORE_JMS_NOME);
		prop.setValore(jmsNome);
		connettore.addProperty(prop);

		prop = new org.openspcoop2.core.registry.Property();
		prop.setNome(CostantiDB.CONNETTORE_USER);
		prop.setValore(jmsUser);
		connettore.addProperty(prop);

		prop = new org.openspcoop2.core.registry.Property();
		prop.setNome(CostantiDB.CONNETTORE_JMS_TIPO);
		prop.setValore(jmsTipo);
		connettore.addProperty(prop);

		prop = new org.openspcoop2.core.registry.Property();
		prop.setNome(CostantiDB.CONNETTORE_PWD);
		prop.setValore(jmsPwd);
		connettore.addProperty(prop);

		prop = new org.openspcoop2.core.registry.Property();
		prop.setNome(CostantiDB.CONNETTORE_JMS_CONNECTION_FACTORY);
		prop.setValore(jmsConnectionFactory);
		connettore.addProperty(prop);

		prop = new org.openspcoop2.core.registry.Property();
		prop.setNome(CostantiDB.CONNETTORE_JMS_CONTEXT_JAVA_NAMING_FACTORY_INITIAL);
		prop.setValore(jmsNfInitial);
		connettore.addProperty(prop);

		prop = new org.openspcoop2.core.registry.Property();
		prop.setNome(CostantiDB.CONNETTORE_JMS_CONTEXT_JAVA_NAMING_FACTORY_URL_PKG);
		prop.setValore(jmsNfUrlPkg);
		connettore.addProperty(prop);

		prop = new org.openspcoop2.core.registry.Property();
		prop.setNome(CostantiDB.CONNETTORE_JMS_CONTEXT_JAVA_NAMING_PROVIDER_URL);
		prop.setValore(jmsNpUrl);
		connettore.addProperty(prop);

		prop = new org.openspcoop2.core.registry.Property();
		prop.setNome(CostantiDB.CONNETTORE_JMS_SEND_AS);
		prop.setValore(jmsSendAs);
		connettore.addProperty(prop);
	}
	
	public static void addJMSDati(List<DataElement> dati,
			String nome, String tipo,
			String user, String password, String initcont, String urlpgk,
			String provurl, String connfact, String sendas, String objectName, TipoOperazione tipoOperazione,
			String stato,
			ControlStationCore core,ConsoleHelper consoleHelper,int pageSize,
			boolean postBackViaPost) throws DriverControlStationException{
		
		if(postBackViaPost || objectName!=null || tipoOperazione!=null) {
			// unused
		}
		
		DataElement de = new DataElement();
		de.setLabel(ConnettoriCostanti.LABEL_CONNETTORE_JMS_CONFIGURAZIONI_CODA);
		de.setType(DataElementType.TITLE);
		dati.add(de);
		
		de = new DataElement();
		de.setLabel(ConnettoriCostanti.LABEL_PARAMETRO_CONNETTORE_JMS_NOME_CODA);
		de.setValue(nome);
		if(!consoleHelper.isShowGestioneWorkflowStatoDocumenti() || !StatiAccordo.finale.toString().equals(stato)){
			de.setType(DataElementType.TEXT_EDIT);
			de.setRequired(true);	
		}else{
			de.setType(DataElementType.TEXT);
		}
		de.setName(ConnettoriCostanti.PARAMETRO_CONNETTORE_JMS_NOME_CODA);
		de.setSize(pageSize);
		dati.add(de);

		de = new DataElement();
		de.setLabel(ConnettoriCostanti.LABEL_PARAMETRO_CONNETTORE_JMS_TIPO_CODA);
		de.setType(DataElementType.SELECT);
		de.setValues(ConnettoriCostanti.TIPI_CODE_JMS);
		de.setSelected(tipo);

		de.setName(ConnettoriCostanti.PARAMETRO_CONNETTORE_JMS_TIPO_CODA);
		dati.add(de);
		
		de = new DataElement();
		de.setLabel(ConnettoriCostanti.LABEL_PARAMETRO_CONNETTORE_JMS_TIPO_OGGETTO_JMS);
		de.setType(DataElementType.SELECT);
		de.setValues(ConnettoriCostanti.TIPO_SEND_AS);
		de.setSelected(sendas);
		de.setName(ConnettoriCostanti.PARAMETRO_CONNETTORE_JMS_TIPO_OGGETTO_JMS);
		dati.add(de);

		de = new DataElement();
		de.setLabel(ConnettoriCostanti.LABEL_CONNETTORE_JMS_CONFIGURAZIONI_CONNESIONE);
		de.setType(DataElementType.TITLE);
		dati.add(de);
		
		de = new DataElement();
		de.setLabel(ConnettoriCostanti.LABEL_PARAMETRO_CONNETTORE_JMS_CONNECTION_FACTORY);
		de.setValue(connfact);
		if(!consoleHelper.isShowGestioneWorkflowStatoDocumenti() || !StatiAccordo.finale.toString().equals(stato)){
			de.setType(DataElementType.TEXT_EDIT);
			de.setRequired(true);	
		}else{
			de.setType(DataElementType.TEXT);
		}
		de.setName(ConnettoriCostanti.PARAMETRO_CONNETTORE_JMS_CONNECTION_FACTORY);
		de.setSize(pageSize);
		dati.add(de);
		
		de = new DataElement();
		de.setLabel(ConnettoriCostanti.LABEL_PARAMETRO_CONNETTORE_JMS_USERNAME);
		de.setValue(user);
		de.setType(DataElementType.TEXT_EDIT);
		de.setName(ConnettoriCostanti.PARAMETRO_CONNETTORE_JMS_USERNAME);
		de.setSize(pageSize);
		dati.add(de);

		de = new DataElement();
		de.setLabel(ConnettoriCostanti.LABEL_PARAMETRO_CONNETTORE_JMS_PASSWORD);
		de.setName(ConnettoriCostanti.PARAMETRO_CONNETTORE_JMS_PASSWORD);
		core.lock(de, password);
		de.setSize(pageSize);
		dati.add(de);

		de = new DataElement();
		de.setLabel(ConnettoriCostanti.LABEL_CONNETTORE_JMS_CONFIGURAZIONI_CONTESTO_JNDI);
		de.setType(DataElementType.TITLE);
		dati.add(de);
		
		de = new DataElement();
		de.setLabel(ConnettoriCostanti.LABEL_PARAMETRO_CONNETTORE_JMS_INIT_CTX);
		de.setValue(initcont);
		de.setType(DataElementType.TEXT_EDIT);
		de.setName(ConnettoriCostanti.PARAMETRO_CONNETTORE_JMS_INIT_CTX);
		de.setSize(pageSize);
		dati.add(de);

		de = new DataElement();
		de.setLabel(ConnettoriCostanti.LABEL_PARAMETRO_CONNETTORE_JMS_URL_PKG);
		de.setValue(urlpgk);
		de.setType(DataElementType.TEXT_EDIT);
		de.setName(ConnettoriCostanti.PARAMETRO_CONNETTORE_JMS_URL_PKG);
		de.setSize(pageSize);
		dati.add(de);

		de = new DataElement();
		de.setLabel(ConnettoriCostanti.LABEL_PARAMETRO_CONNETTORE_JMS_PROVIDER_URL);
		de.setValue(provurl);
		de.setType(DataElementType.TEXT_EDIT);
		de.setName(ConnettoriCostanti.PARAMETRO_CONNETTORE_JMS_PROVIDER_URL);
		de.setSize(pageSize);
		dati.add(de);

	}
	
	
	public static void addJMSDatiAsHidden(List<DataElement> dati,
			String nome, String tipo,
			String user, String password, String initcont, String urlpgk,
			String provurl, String connfact, String sendas, String objectName, TipoOperazione tipoOperazione,
			String stato,
			ControlStationCore core,int pageSize) throws DriverControlStationException{
		
		if(tipoOperazione!=null && stato!=null) {
			 // nop
		}
		
		DataElement de = new DataElement();
		de.setLabel(ConnettoriCostanti.LABEL_PARAMETRO_CONNETTORE_JMS_NOME_CODA);
		de.setValue(nome);
		de.setType(DataElementType.HIDDEN);
		de.setName(ConnettoriCostanti.PARAMETRO_CONNETTORE_JMS_NOME_CODA);
		de.setSize(pageSize);
		dati.add(de);


		de = new DataElement();
		de.setLabel(ConnettoriCostanti.LABEL_PARAMETRO_CONNETTORE_JMS_TIPO_CODA);
		de.setType(DataElementType.HIDDEN);
		de.setValue(tipo);

		de.setName(ConnettoriCostanti.PARAMETRO_CONNETTORE_JMS_TIPO_CODA);
		dati.add(de);

		if ( !objectName.equals(ServiziApplicativiCostanti.OBJECT_NAME_SERVIZI_APPLICATIVI) ) {

			de = new DataElement();
			de.setLabel(ConnettoriCostanti.LABEL_PARAMETRO_CONNETTORE_JMS_USERNAME);
			de.setValue(user);
			de.setType(DataElementType.HIDDEN);
			de.setName(ConnettoriCostanti.PARAMETRO_CONNETTORE_JMS_USERNAME);
			de.setSize(pageSize);
			dati.add(de);

			de = new DataElement();
			de.setLabel(ConnettoriCostanti.LABEL_PARAMETRO_CONNETTORE_JMS_PASSWORD);
			de.setType(DataElementType.HIDDEN);
			de.setName(ConnettoriCostanti.PARAMETRO_CONNETTORE_JMS_PASSWORD);
			core.lockHidden(de, password);
			de.setSize(pageSize);
			dati.add(de);
		}

		de = new DataElement();
		de.setLabel(ConnettoriCostanti.LABEL_PARAMETRO_CONNETTORE_JMS_INIT_CTX);
		de.setValue(initcont);
		de.setType(DataElementType.HIDDEN);
		de.setName(ConnettoriCostanti.PARAMETRO_CONNETTORE_JMS_INIT_CTX);
		de.setSize(pageSize);
		dati.add(de);

		de = new DataElement();
		de.setLabel(ConnettoriCostanti.LABEL_PARAMETRO_CONNETTORE_JMS_URL_PKG);
		de.setValue(urlpgk);
		de.setType(DataElementType.TEXT_EDIT);
		de.setName(ConnettoriCostanti.PARAMETRO_CONNETTORE_JMS_URL_PKG);
		de.setSize(pageSize);
		dati.add(de);

		de = new DataElement();
		de.setLabel(ConnettoriCostanti.LABEL_PARAMETRO_CONNETTORE_JMS_PROVIDER_URL);
		de.setValue(provurl);
		de.setType(DataElementType.HIDDEN);
		de.setName(ConnettoriCostanti.PARAMETRO_CONNETTORE_JMS_PROVIDER_URL);
		de.setSize(pageSize);
		dati.add(de);

		de = new DataElement();
		de.setLabel(ConnettoriCostanti.LABEL_PARAMETRO_CONNETTORE_JMS_CONNECTION_FACTORY);
		de.setValue(connfact);
		de.setType(DataElementType.HIDDEN);
		de.setName(ConnettoriCostanti.PARAMETRO_CONNETTORE_JMS_CONNECTION_FACTORY);
		de.setSize(pageSize);
		dati.add(de);

		de = new DataElement();
		de.setLabel(ConnettoriCostanti.LABEL_PARAMETRO_CONNETTORE_JMS_TIPO_OGGETTO_JMS);
		de.setType(DataElementType.HIDDEN);
		de.setValue(sendas);
		de.setName(ConnettoriCostanti.PARAMETRO_CONNETTORE_JMS_TIPO_OGGETTO_JMS);
		dati.add(de);
		
	}
	
}
