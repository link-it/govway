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
import org.openspcoop2.web.lib.mvc.Costanti;
import org.openspcoop2.web.lib.mvc.DataElement;
import org.openspcoop2.web.lib.mvc.DataElementType;

/**
 * ConnettoreHTTPSUtils
 *
 * @author Andrea Poli (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ConnettoreHTTPSUtils {

	public static void fillConnettoreConfig(org.openspcoop2.core.config.Connettore connettore,
			String httpsurl, String httpstipologia, boolean httpshostverify,
			String httpspath, String httpstipo, String httpspwd,
			String httpsalgoritmo, boolean httpsstato, String httpskeystore,
			String httpspwdprivatekeytrust, String httpspathkey,
			String httpstipokey, String httpspwdkey,
			String httpspwdprivatekey, String httpsalgoritmokey){
		
		connettore.setCustom(true);
		
		org.openspcoop2.core.config.Property prop = new org.openspcoop2.core.config.Property();
		prop.setNome(CostantiDB.CONNETTORE_HTTPS_LOCATION);
		prop.setValore(httpsurl);
		connettore.addProperty(prop);

		prop = new org.openspcoop2.core.config.Property();
		prop.setNome(CostantiDB.CONNETTORE_HTTPS_SSL_TYPE);
		prop.setValore(httpstipologia);
		connettore.addProperty(prop);

		prop = new org.openspcoop2.core.config.Property();
		prop.setNome(CostantiDB.CONNETTORE_HTTPS_HOSTNAME_VERIFIER);
		prop.setValore(String.valueOf(httpshostverify));
		connettore.addProperty(prop);

		prop = new org.openspcoop2.core.config.Property();
		prop.setNome(CostantiDB.CONNETTORE_HTTPS_TRUST_STORE_LOCATION);
		prop.setValore(httpspath);
		connettore.addProperty(prop);

		prop = new org.openspcoop2.core.config.Property();
		prop.setNome(CostantiDB.CONNETTORE_HTTPS_TRUST_STORE_TYPE);
		prop.setValore(httpstipo);
		connettore.addProperty(prop);

		prop = new org.openspcoop2.core.config.Property();
		prop.setNome(CostantiDB.CONNETTORE_HTTPS_TRUST_STORE_PASSWORD);
		prop.setValore(httpspwd);
		connettore.addProperty(prop);

		prop = new org.openspcoop2.core.config.Property();
		prop.setNome(CostantiDB.CONNETTORE_HTTPS_TRUST_MANAGEMENT_ALGORITM);
		if (httpsalgoritmo == null || "".equals(httpsalgoritmo))
			prop.setValore(javax.net.ssl.TrustManagerFactory.getDefaultAlgorithm());
		else
			prop.setValore(httpsalgoritmo);
		connettore.addProperty(prop);

		if (httpsstato) {

			prop = new org.openspcoop2.core.config.Property();
			prop.setNome(CostantiDB.CONNETTORE_HTTPS_KEY_MANAGEMENT_ALGORITM);
			if (httpsalgoritmokey == null || "".equals(httpsalgoritmokey))
				prop.setValore(javax.net.ssl.KeyManagerFactory.getDefaultAlgorithm());
			else
				prop.setValore(httpsalgoritmokey);
			connettore.addProperty(prop);


			if (ConnettoriCostanti.DEFAULT_CONNETTORE_HTTPS_KEYSTORE_CLIENT_AUTH_MODE_DEFAULT.equals(httpskeystore)) {
				prop = new org.openspcoop2.core.config.Property();
				prop.setNome(CostantiDB.CONNETTORE_HTTPS_KEY_PASSWORD);
				prop.setValore(httpspwdprivatekeytrust);
				connettore.addProperty(prop);

				prop = new org.openspcoop2.core.config.Property();
				prop.setNome(CostantiDB.CONNETTORE_HTTPS_KEY_STORE_LOCATION);
				prop.setValore(httpspath);
				connettore.addProperty(prop);

				prop = new org.openspcoop2.core.config.Property();
				prop.setNome(CostantiDB.CONNETTORE_HTTPS_KEY_STORE_TYPE);
				prop.setValore(httpstipo);
				connettore.addProperty(prop);

				prop = new org.openspcoop2.core.config.Property();
				prop.setNome(CostantiDB.CONNETTORE_HTTPS_KEY_STORE_PASSWORD);
				prop.setValore(httpspwd);
				connettore.addProperty(prop);

			} else {
				prop = new org.openspcoop2.core.config.Property();
				prop.setNome(CostantiDB.CONNETTORE_HTTPS_KEY_STORE_LOCATION);
				prop.setValore(httpspathkey);
				connettore.addProperty(prop);

				prop = new org.openspcoop2.core.config.Property();
				prop.setNome(CostantiDB.CONNETTORE_HTTPS_KEY_STORE_TYPE);
				prop.setValore(httpstipokey);
				connettore.addProperty(prop);

				prop = new org.openspcoop2.core.config.Property();
				prop.setNome(CostantiDB.CONNETTORE_HTTPS_KEY_STORE_PASSWORD);
				prop.setValore(httpspwdkey);
				connettore.addProperty(prop);

				prop = new org.openspcoop2.core.config.Property();
				prop.setNome(CostantiDB.CONNETTORE_HTTPS_KEY_PASSWORD);
				prop.setValore(httpspwdprivatekey);
				connettore.addProperty(prop);

			}
		}
	}
	
	public static void fillConnettoreRegistry(org.openspcoop2.core.registry.Connettore connettore,
			String httpsurl, String httpstipologia, boolean httpshostverify,
			String httpspath, String httpstipo, String httpspwd,
			String httpsalgoritmo, boolean httpsstato, String httpskeystore,
			String httpspwdprivatekeytrust, String httpspathkey,
			String httpstipokey, String httpspwdkey,
			String httpspwdprivatekey, String httpsalgoritmokey,
			String user, String pwd){
		
		connettore.setCustom(true);
	
		org.openspcoop2.core.registry.Property prop = new org.openspcoop2.core.registry.Property();
		prop.setNome(CostantiDB.CONNETTORE_HTTPS_LOCATION);
		prop.setValore(httpsurl);
		connettore.addProperty(prop);

		if(user!=null){
			prop = new org.openspcoop2.core.registry.Property();
			prop.setNome(CostantiDB.CONNETTORE_USER);
			prop.setValore(user);
			connettore.addProperty(prop);
		}
		
		if(pwd!=null){
			prop = new org.openspcoop2.core.registry.Property();
			prop.setNome(CostantiDB.CONNETTORE_PWD);
			prop.setValore(pwd);
			connettore.addProperty(prop);
		}
		
		prop = new org.openspcoop2.core.registry.Property();
		prop.setNome(CostantiDB.CONNETTORE_HTTPS_SSL_TYPE);
		prop.setValore(httpstipologia);
		connettore.addProperty(prop);

		prop = new org.openspcoop2.core.registry.Property();
		prop.setNome(CostantiDB.CONNETTORE_HTTPS_HOSTNAME_VERIFIER);
		prop.setValore(String.valueOf(httpshostverify));
		connettore.addProperty(prop);

		prop = new org.openspcoop2.core.registry.Property();
		prop.setNome(CostantiDB.CONNETTORE_HTTPS_TRUST_STORE_LOCATION);
		prop.setValore(httpspath);
		connettore.addProperty(prop);

		prop = new org.openspcoop2.core.registry.Property();
		prop.setNome(CostantiDB.CONNETTORE_HTTPS_TRUST_STORE_TYPE);
		prop.setValore(httpstipo);
		connettore.addProperty(prop);

		prop = new org.openspcoop2.core.registry.Property();
		prop.setNome(CostantiDB.CONNETTORE_HTTPS_TRUST_STORE_PASSWORD);
		prop.setValore(httpspwd);
		connettore.addProperty(prop);

		prop = new org.openspcoop2.core.registry.Property();
		prop.setNome(CostantiDB.CONNETTORE_HTTPS_TRUST_MANAGEMENT_ALGORITM);
		if (httpsalgoritmo == null || "".equals(httpsalgoritmo))
			prop.setValore(javax.net.ssl.TrustManagerFactory.getDefaultAlgorithm());
		else
			prop.setValore(httpsalgoritmo);
		connettore.addProperty(prop);

		if (httpsstato) {

			prop = new org.openspcoop2.core.registry.Property();
			prop.setNome(CostantiDB.CONNETTORE_HTTPS_KEY_MANAGEMENT_ALGORITM);
			if (httpsalgoritmokey == null || "".equals(httpsalgoritmokey))
				prop.setValore(javax.net.ssl.KeyManagerFactory.getDefaultAlgorithm());
			else
				prop.setValore(httpsalgoritmokey);
			connettore.addProperty(prop);


			if (ConnettoriCostanti.DEFAULT_CONNETTORE_HTTPS_KEYSTORE_CLIENT_AUTH_MODE_DEFAULT.equals(httpskeystore)) {
				prop = new org.openspcoop2.core.registry.Property();
				prop.setNome(CostantiDB.CONNETTORE_HTTPS_KEY_PASSWORD);
				prop.setValore(httpspwdprivatekeytrust);
				connettore.addProperty(prop);

				prop = new org.openspcoop2.core.registry.Property();
				prop.setNome(CostantiDB.CONNETTORE_HTTPS_KEY_STORE_LOCATION);
				prop.setValore(httpspath);
				connettore.addProperty(prop);

				prop = new org.openspcoop2.core.registry.Property();
				prop.setNome(CostantiDB.CONNETTORE_HTTPS_KEY_STORE_TYPE);
				prop.setValore(httpstipo);
				connettore.addProperty(prop);

				prop = new org.openspcoop2.core.registry.Property();
				prop.setNome(CostantiDB.CONNETTORE_HTTPS_KEY_STORE_PASSWORD);
				prop.setValore(httpspwd);
				connettore.addProperty(prop);

			} else {
				prop = new org.openspcoop2.core.registry.Property();
				prop.setNome(CostantiDB.CONNETTORE_HTTPS_KEY_STORE_LOCATION);
				prop.setValore(httpspathkey);
				connettore.addProperty(prop);

				prop = new org.openspcoop2.core.registry.Property();
				prop.setNome(CostantiDB.CONNETTORE_HTTPS_KEY_STORE_TYPE);
				prop.setValore(httpstipokey);
				connettore.addProperty(prop);

				prop = new org.openspcoop2.core.registry.Property();
				prop.setNome(CostantiDB.CONNETTORE_HTTPS_KEY_STORE_PASSWORD);
				prop.setValore(httpspwdkey);
				connettore.addProperty(prop);

				prop = new org.openspcoop2.core.registry.Property();
				prop.setNome(CostantiDB.CONNETTORE_HTTPS_KEY_PASSWORD);
				prop.setValore(httpspwdprivatekey);
				connettore.addProperty(prop);

			}
		}
	}
	
	public static void addHTTPSDati(Vector<DataElement> dati,
			String httpsurl, String httpstipologia, boolean httpshostverify,
			String httpspath, String httpstipo, String httpspwd,
			String httpsalgoritmo, boolean httpsstato, String httpskeystore,
			String httpspwdprivatekeytrust, String httpspathkey,
			String httpstipokey, String httpspwdkey,
			String httpspwdprivatekey, String httpsalgoritmokey, String stato,
			ControlStationCore core,int pageSize, boolean addUrlParameter,
			String prefix){
		
		if(addUrlParameter){
			DataElement de = new DataElement();
			de.setLabel(ConnettoriCostanti.LABEL_PARAMETRO_CONNETTORE_HTTPS_URL);
			de.setValue(httpsurl);
			if(!core.isShowGestioneWorkflowStatoDocumenti() || !StatiAccordo.finale.toString().equals(stato)){
				de.setType(DataElementType.TEXT_EDIT);
				de.setRequired(true);	
			}else{
				de.setType(DataElementType.TEXT);
			}
			de.setName(ConnettoriCostanti.PARAMETRO_CONNETTORE_HTTPS_URL);
			de.setSize(pageSize);
			dati.addElement(de);
		}

		if(prefix==null){
			prefix = "";
		}
		
		DataElement de = new DataElement();
		de.setLabel(prefix+ConnettoriCostanti.LABEL_CONNETTORE_AUTENTICAZIONE);
		de.setType(DataElementType.TITLE);
		dati.addElement(de);

		de = new DataElement();
		de.setLabel(ConnettoriCostanti.LABEL_PARAMETRO_CONNETTORE_HTTPS_SSL_TYPE);
		de.setType(DataElementType.SELECT);
		de.setValues(ConnettoriCostanti.TIPOLOGIE_HTTPS);
		de.setSelected(httpstipologia);
		de.setName(ConnettoriCostanti.PARAMETRO_CONNETTORE_HTTPS_SSL_TYPE);
		dati.addElement(de);

		de = new DataElement();
		de.setLabel(ConnettoriCostanti.LABEL_PARAMETRO_CONNETTORE_HTTPS_HOST_VERIFY);
		de.setValue(httpshostverify ? Costanti.CHECK_BOX_ENABLED : "");
		de.setSelected(httpshostverify);
		de.setType(DataElementType.CHECKBOX);
		de.setName(ConnettoriCostanti.PARAMETRO_CONNETTORE_HTTPS_HOST_VERIFY);
		dati.addElement(de);

		de = new DataElement();
		de.setLabel(prefix+ConnettoriCostanti.LABEL_CONNETTORE_AUTENTICAZIONE_SERVER);
		de.setType(DataElementType.TITLE);
		dati.addElement(de);

		de = new DataElement();
		de.setLabel(ConnettoriCostanti.LABEL_PARAMETRO_CONNETTORE_HTTPS_TRUST_STORE_LOCATION);
		de.setValue(httpspath);
		if(!core.isShowGestioneWorkflowStatoDocumenti() || !StatiAccordo.finale.toString().equals(stato)){
			de.setType(DataElementType.TEXT_EDIT);
			de.setRequired(true);	
		}else{
			de.setType(DataElementType.TEXT);
		}
		de.setName(ConnettoriCostanti.PARAMETRO_CONNETTORE_HTTPS_TRUST_STORE_LOCATION);
		de.setSize(pageSize);
		dati.addElement(de);

		de = new DataElement();
		de.setLabel(ConnettoriCostanti.LABEL_PARAMETRO_CONNETTORE_HTTPS_TRUST_STORE_TYPE);
		de.setType(DataElementType.SELECT);
		de.setValues(ConnettoriCostanti.TIPOLOGIE_KEYSTORE);
		de.setSelected(httpstipo);
		de.setName(ConnettoriCostanti.PARAMETRO_CONNETTORE_HTTPS_TRUST_STORE_TYPE);
		dati.addElement(de);

		de = new DataElement();
		de.setLabel(ConnettoriCostanti.LABEL_PARAMETRO_CONNETTORE_HTTPS_TRUST_STORE_PASSWORD);
		de.setValue(httpspwd);
		if(!core.isShowGestioneWorkflowStatoDocumenti() || !StatiAccordo.finale.toString().equals(stato)){
			de.setType(DataElementType.TEXT_EDIT);
			de.setRequired(true);	
		}else{
			de.setType(DataElementType.TEXT);
		}
		de.setName(ConnettoriCostanti.PARAMETRO_CONNETTORE_HTTPS_TRUST_STORE_PASSWORD);
		de.setSize(pageSize);
		dati.addElement(de);

		de = new DataElement();
		de.setLabel(ConnettoriCostanti.LABEL_PARAMETRO_CONNETTORE_HTTPS_TRUST_MANAGEMENT_ALGORITM);
		de.setValue(httpsalgoritmo);
		if(!core.isShowGestioneWorkflowStatoDocumenti() || !StatiAccordo.finale.toString().equals(stato)){
			de.setType(DataElementType.TEXT_EDIT);
			de.setRequired(true);	
		}else{
			de.setType(DataElementType.TEXT);
		}
		de.setName(ConnettoriCostanti.PARAMETRO_CONNETTORE_HTTPS_TRUST_MANAGEMENT_ALGORITM);
		de.setSize(pageSize);
		dati.addElement(de);

		de = new DataElement();
		de.setLabel(prefix+ConnettoriCostanti.LABEL_CONNETTORE_AUTENTICAZIONE_CLIENT);
		de.setType(DataElementType.TITLE);
		dati.addElement(de);

		de = new DataElement();
		de.setLabel(ConnettoriCostanti.LABEL_PARAMETRO_CONNETTORE_HTTPS_STATO);
		de.setValue(httpsstato ? Costanti.CHECK_BOX_ENABLED : "");
		de.setSelected(httpsstato);
		de.setType(DataElementType.CHECKBOX);
		de.setName(ConnettoriCostanti.PARAMETRO_CONNETTORE_HTTPS_STATO);
		//			de.setOnClick("CambiaKeyStore(\"" + tipoOp + "\")");
		de.setPostBack(true);
		dati.addElement(de);


		de = new DataElement();
		de.setLabel(ConnettoriCostanti.LABEL_PARAMETRO_CONNETTORE_HTTPS_KEYSTORE_CLIENT_AUTH_MODE);
		de.setName(ConnettoriCostanti.PARAMETRO_CONNETTORE_HTTPS_KEYSTORE_CLIENT_AUTH_MODE);
		if (httpsstato) {
			de.setType(DataElementType.SELECT);
			de.setValues(ConnettoriCostanti.DEFAULT_CONNETTORE_HTTPS_KEYSTORE_CLIENT_AUTH_MODES);
			de.setLabels(ConnettoriCostanti.DEFAULT_CONNETTORE_HTTPS_KEYSTORE_CLIENT_AUTH_LABEL_MODES);
			de.setSelected(httpskeystore);
			//				de.setOnChange("CambiaKeyStore('" + tipoOp + "')");
			de.setPostBack(true);
		} else {
			de.setType(DataElementType.HIDDEN);
			de.setValue("");
		}
		dati.addElement(de);

		de = new DataElement();
		de.setLabel(ConnettoriCostanti.LABEL_PARAMETRO_CONNETTORE_HTTPS_PASSWORD_PRIVATE_KEY_STORE);
		de.setValue(httpspwdprivatekeytrust);
		if (httpsstato &&
				(httpskeystore == null || "".equals(httpskeystore) || httpskeystore.equals(ConnettoriCostanti.DEFAULT_CONNETTORE_HTTPS_KEYSTORE_CLIENT_AUTH_MODE_DEFAULT))){
			if(!core.isShowGestioneWorkflowStatoDocumenti() || !StatiAccordo.finale.toString().equals(stato)){
				de.setType(DataElementType.TEXT_EDIT);
				de.setRequired(true);	
			}else{
				de.setType(DataElementType.TEXT);
			}
		}else
			de.setType(DataElementType.HIDDEN);
		de.setName(ConnettoriCostanti.PARAMETRO_CONNETTORE_HTTPS_PASSWORD_PRIVATE_KEY_STORE);
		de.setSize(pageSize);
		dati.addElement(de);

		de = new DataElement();
		de.setLabel(ConnettoriCostanti.LABEL_PARAMETRO_CONNETTORE_HTTPS_KEY_STORE_LOCATION);
		de.setValue(httpspathkey);
		if (httpsstato &&
				(httpskeystore != null && httpskeystore.equals(ConnettoriCostanti.DEFAULT_CONNETTORE_HTTPS_KEYSTORE_CLIENT_AUTH_MODE_RIDEFINISCI))){
			if(!core.isShowGestioneWorkflowStatoDocumenti() || !StatiAccordo.finale.toString().equals(stato)){
				de.setType(DataElementType.TEXT_EDIT);
				de.setRequired(true);	
			}else{
				de.setType(DataElementType.TEXT);
			}
		}else
			de.setType(DataElementType.HIDDEN);
		de.setName(ConnettoriCostanti.PARAMETRO_CONNETTORE_HTTPS_KEY_STORE_LOCATION);
		de.setSize(pageSize);
		dati.addElement(de);

		de = new DataElement();
		de.setLabel(ConnettoriCostanti.LABEL_PARAMETRO_CONNETTORE_HTTPS_KEY_STORE_TYPE);
		if (httpsstato &&
				(httpskeystore != null && httpskeystore.equals(ConnettoriCostanti.DEFAULT_CONNETTORE_HTTPS_KEYSTORE_CLIENT_AUTH_MODE_RIDEFINISCI))) {
			de.setType(DataElementType.SELECT);
			de.setValues(ConnettoriCostanti.TIPOLOGIE_KEYSTORE);
			de.setSelected(httpstipokey);
		} else {
			de.setType(DataElementType.HIDDEN);
			de.setValue("");
		}
		de.setName(ConnettoriCostanti.PARAMETRO_CONNETTORE_HTTPS_KEY_STORE_TYPE);
		dati.addElement(de);

		de = new DataElement();
		de.setLabel(ConnettoriCostanti.LABEL_PARAMETRO_CONNETTORE_HTTPS_KEY_STORE_PASSWORD);
		de.setValue(httpspwdkey);
		if (httpsstato &&
				(httpskeystore != null && httpskeystore.equals(ConnettoriCostanti.DEFAULT_CONNETTORE_HTTPS_KEYSTORE_CLIENT_AUTH_MODE_RIDEFINISCI))){
			if(!core.isShowGestioneWorkflowStatoDocumenti() || !StatiAccordo.finale.toString().equals(stato)){
				de.setType(DataElementType.TEXT_EDIT);
				de.setRequired(true);	
			}else{
				de.setType(DataElementType.TEXT);
			}
		}else
			de.setType(DataElementType.HIDDEN);
		de.setName(ConnettoriCostanti.PARAMETRO_CONNETTORE_HTTPS_KEY_STORE_PASSWORD);
		de.setSize(pageSize);
		dati.addElement(de);

		de = new DataElement();
		de.setLabel(ConnettoriCostanti.LABEL_PARAMETRO_CONNETTORE_HTTPS_PASSWORD_PRIVATE_KEY_KEYSTORE);
		de.setValue(httpspwdprivatekey);
		if (httpsstato &&
				(httpskeystore != null && httpskeystore.equals(ConnettoriCostanti.DEFAULT_CONNETTORE_HTTPS_KEYSTORE_CLIENT_AUTH_MODE_RIDEFINISCI))){
			if(!core.isShowGestioneWorkflowStatoDocumenti() || !StatiAccordo.finale.toString().equals(stato)){
				de.setType(DataElementType.TEXT_EDIT);
				de.setRequired(true);	
			}else{
				de.setType(DataElementType.TEXT);
			}
		}else
			de.setType(DataElementType.HIDDEN);
		de.setName(ConnettoriCostanti.PARAMETRO_CONNETTORE_HTTPS_PASSWORD_PRIVATE_KEY_KEYSTORE);
		de.setSize(pageSize);
		dati.addElement(de);

		de = new DataElement();
		de.setLabel(ConnettoriCostanti.LABEL_PARAMETRO_CONNETTORE_HTTPS_KEY_MANAGEMENT_ALGORITM);
		de.setValue(httpsalgoritmokey);
		if (httpsstato){
			if(!core.isShowGestioneWorkflowStatoDocumenti() || !StatiAccordo.finale.toString().equals(stato)){
				de.setType(DataElementType.TEXT_EDIT);
				de.setRequired(true);	
			}else{
				de.setType(DataElementType.TEXT);
			}
		}else
			de.setType(DataElementType.HIDDEN);
		de.setName(ConnettoriCostanti.PARAMETRO_CONNETTORE_HTTPS_KEY_MANAGEMENT_ALGORITM);
		de.setSize(pageSize);
		dati.addElement(de);
	}
	
	public static void addHTTPSDatiAsHidden(Vector<DataElement> dati,
			String httpsurl, String httpstipologia, boolean httpshostverify,
			String httpspath, String httpstipo, String httpspwd,
			String httpsalgoritmo, boolean httpsstato, String httpskeystore,
			String httpspwdprivatekeytrust, String httpspathkey,
			String httpstipokey, String httpspwdkey,
			String httpspwdprivatekey, String httpsalgoritmokey,
			String stato,
			ControlStationCore core,int pageSize){
		
		DataElement de = new DataElement();
		de.setLabel(ConnettoriCostanti.LABEL_PARAMETRO_CONNETTORE_HTTPS_URL);
		de.setValue(httpsurl);
		de.setType(DataElementType.HIDDEN);
		de.setName(ConnettoriCostanti.PARAMETRO_CONNETTORE_HTTPS_URL);
		de.setSize(pageSize);
		dati.addElement(de);


		de = new DataElement();
		de.setLabel(ConnettoriCostanti.LABEL_PARAMETRO_CONNETTORE_HTTPS_SSL_TYPE);
		de.setType(DataElementType.HIDDEN);
		de.setValue(httpstipologia);
//		de.setType(DataElementType.SELECT);
//		de.setValues(ConnettoriCostanti.TIPOLOGIE_HTTPS);
//		de.setSelected(httpstipologia);
		de.setName(ConnettoriCostanti.PARAMETRO_CONNETTORE_HTTPS_SSL_TYPE);
		dati.addElement(de);

		de = new DataElement();
		de.setLabel(ConnettoriCostanti.LABEL_PARAMETRO_CONNETTORE_HTTPS_HOST_VERIFY);
		de.setValue(httpshostverify ? Costanti.CHECK_BOX_ENABLED : "");
		de.setType(DataElementType.HIDDEN);
//		de.setSelected(httpshostverify);
//		de.setType(DataElementType.CHECKBOX);
		de.setName(ConnettoriCostanti.PARAMETRO_CONNETTORE_HTTPS_HOST_VERIFY);
		dati.addElement(de);

//		de = new DataElement();
//		de.setLabel(ConnettoriCostanti.LABEL_CONNETTORE_AUTENTICAZIONE_SERVER);
//		de.setType(DataElementType.TITLE);
//		dati.addElement(de);

		de = new DataElement();
		de.setLabel(ConnettoriCostanti.LABEL_PARAMETRO_CONNETTORE_HTTPS_TRUST_STORE_LOCATION);
		de.setValue(httpspath);
		de.setType(DataElementType.HIDDEN);
//		if(!this.core.isShowGestioneWorkflowStatoDocumenti() || !StatiAccordo.finale.toString().equals(stato)){
//			de.setType(DataElementType.TEXT_EDIT);
//			de.setRequired(true);	
//		}else{
//			de.setType(DataElementType.TEXT);
//		}
		de.setName(ConnettoriCostanti.PARAMETRO_CONNETTORE_HTTPS_TRUST_STORE_LOCATION);
		de.setSize(pageSize);
		dati.addElement(de);

		de = new DataElement();
		de.setLabel(ConnettoriCostanti.LABEL_PARAMETRO_CONNETTORE_HTTPS_TRUST_STORE_TYPE);
		de.setType(DataElementType.HIDDEN);
		de.setValue(httpstipo);
//		de.setType(DataElementType.SELECT);
//		de.setValues(ConnettoriCostanti.TIPOLOGIE_KEYSTORE);
//		de.setSelected(httpstipo);
		de.setName(ConnettoriCostanti.PARAMETRO_CONNETTORE_HTTPS_TRUST_STORE_TYPE);
		dati.addElement(de);

		de = new DataElement();
		de.setLabel(ConnettoriCostanti.LABEL_PARAMETRO_CONNETTORE_HTTPS_TRUST_STORE_PASSWORD);
		de.setValue(httpspwd);
		de.setType(DataElementType.HIDDEN);
//		if(!this.core.isShowGestioneWorkflowStatoDocumenti() || !StatiAccordo.finale.toString().equals(stato)){
//			de.setType(DataElementType.TEXT_EDIT);
//			de.setRequired(true);	
//		}else{
//			de.setType(DataElementType.TEXT);
//		}
		de.setName(ConnettoriCostanti.PARAMETRO_CONNETTORE_HTTPS_TRUST_STORE_PASSWORD);
		de.setSize(pageSize);
		dati.addElement(de);

		de = new DataElement();
		de.setLabel(ConnettoriCostanti.LABEL_PARAMETRO_CONNETTORE_HTTPS_TRUST_MANAGEMENT_ALGORITM);
		de.setValue(httpsalgoritmo);
		de.setType(DataElementType.HIDDEN);
//		if(!this.core.isShowGestioneWorkflowStatoDocumenti() || !StatiAccordo.finale.toString().equals(stato)){
//			de.setType(DataElementType.TEXT_EDIT);
//			de.setRequired(true);	
//		}else{
//			de.setType(DataElementType.TEXT);
//		}
		de.setName(ConnettoriCostanti.PARAMETRO_CONNETTORE_HTTPS_TRUST_MANAGEMENT_ALGORITM);
		de.setSize(pageSize);
		dati.addElement(de);

//		de = new DataElement();
//		de.setLabel(ConnettoriCostanti.LABEL_CONNETTORE_AUTENTICAZIONE_CLIENT);
//		de.setType(DataElementType.TITLE);
//		dati.addElement(de);

		de = new DataElement();
		de.setLabel(ConnettoriCostanti.LABEL_PARAMETRO_CONNETTORE_HTTPS_STATO);
		de.setValue(httpsstato ? Costanti.CHECK_BOX_ENABLED : "");
		de.setType(DataElementType.HIDDEN);
//		de.setSelected(httpsstato);
//		de.setType(DataElementType.CHECKBOX);
		de.setName(ConnettoriCostanti.PARAMETRO_CONNETTORE_HTTPS_STATO);
		//			de.setOnClick("CambiaKeyStore(\"" + tipoOp + "\")");
//		de.setPostBack(true);
		dati.addElement(de);


		de = new DataElement();
		de.setLabel(ConnettoriCostanti.LABEL_PARAMETRO_CONNETTORE_HTTPS_KEYSTORE_CLIENT_AUTH_MODE);
		de.setName(ConnettoriCostanti.PARAMETRO_CONNETTORE_HTTPS_KEYSTORE_CLIENT_AUTH_MODE);
		de.setType(DataElementType.HIDDEN);
		
		if (httpsstato) {
//			de.setType(DataElementType.SELECT);
//			de.setValues(ConnettoriCostanti.DEFAULT_CONNETTORE_HTTPS_KEYSTORE_CLIENT_AUTH_MODES);
//			de.setLabels(ConnettoriCostanti.DEFAULT_CONNETTORE_HTTPS_KEYSTORE_CLIENT_AUTH_LABEL_MODES);
//			de.setSelected(httpskeystore);
//			//				de.setOnChange("CambiaKeyStore('" + tipoOp + "')");
//			de.setPostBack(true);
			de.setValue(httpskeystore);
		} else {
//			de.setType(DataElementType.HIDDEN);
			de.setValue("");
		}
		dati.addElement(de);

		de = new DataElement();
		de.setLabel(ConnettoriCostanti.LABEL_PARAMETRO_CONNETTORE_HTTPS_PASSWORD_PRIVATE_KEY_STORE);
		de.setValue(httpspwdprivatekeytrust);
//		if (httpsstato &&
//				(httpskeystore == null || httpskeystore.equals(ConnettoriCostanti.DEFAULT_CONNETTORE_HTTPS_KEYSTORE_CLIENT_AUTH_MODE_DEFAULT))){
//			if(!this.core.isShowGestioneWorkflowStatoDocumenti() || !StatiAccordo.finale.toString().equals(stato)){
//				de.setType(DataElementType.TEXT_EDIT);
//				de.setRequired(true);	
//			}else{
//				de.setType(DataElementType.TEXT);
//			}
//		}else
			de.setType(DataElementType.HIDDEN);
		de.setName(ConnettoriCostanti.PARAMETRO_CONNETTORE_HTTPS_PASSWORD_PRIVATE_KEY_STORE);
		de.setSize(pageSize);
		dati.addElement(de);

		de = new DataElement();
		de.setLabel(ConnettoriCostanti.LABEL_PARAMETRO_CONNETTORE_HTTPS_KEY_STORE_LOCATION);
		de.setValue(httpspathkey);
//		if (httpsstato &&
//				(httpskeystore != null && httpskeystore.equals(ConnettoriCostanti.DEFAULT_CONNETTORE_HTTPS_KEYSTORE_CLIENT_AUTH_MODE_RIDEFINISCI))){
//			if(!this.core.isShowGestioneWorkflowStatoDocumenti() || !StatiAccordo.finale.toString().equals(stato)){
//				de.setType(DataElementType.TEXT_EDIT);
//				de.setRequired(true);	
//			}else{
//				de.setType(DataElementType.TEXT);
//			}
//		}else
			de.setType(DataElementType.HIDDEN);
		de.setName(ConnettoriCostanti.PARAMETRO_CONNETTORE_HTTPS_KEY_STORE_LOCATION);
		de.setSize(pageSize);
		dati.addElement(de);

		de = new DataElement();
		de.setLabel(ConnettoriCostanti.LABEL_PARAMETRO_CONNETTORE_HTTPS_KEY_STORE_TYPE);
		if (httpsstato &&
				(httpskeystore != null && httpskeystore.equals(ConnettoriCostanti.DEFAULT_CONNETTORE_HTTPS_KEYSTORE_CLIENT_AUTH_MODE_RIDEFINISCI))) {
			de.setType(DataElementType.HIDDEN);
			de.setValue(httpstipokey);
//			de.setType(DataElementType.SELECT);
//			de.setValues(ConnettoriCostanti.TIPOLOGIE_KEYSTORE);
//			de.setSelected(httpstipokey);
		} else {
			de.setType(DataElementType.HIDDEN);
			de.setValue("");
		}
		de.setName(ConnettoriCostanti.PARAMETRO_CONNETTORE_HTTPS_KEY_STORE_TYPE);
		dati.addElement(de);

		de = new DataElement();
		de.setLabel(ConnettoriCostanti.LABEL_PARAMETRO_CONNETTORE_HTTPS_KEY_STORE_PASSWORD);
		de.setValue(httpspwdkey);
//		if (httpsstato &&
//				(httpskeystore != null && httpskeystore.equals(ConnettoriCostanti.DEFAULT_CONNETTORE_HTTPS_KEYSTORE_CLIENT_AUTH_MODE_RIDEFINISCI))){
//			if(!this.core.isShowGestioneWorkflowStatoDocumenti() || !StatiAccordo.finale.toString().equals(stato)){
//				de.setType(DataElementType.TEXT_EDIT);
//				de.setRequired(true);	
//			}else{
//				de.setType(DataElementType.TEXT);
//			}
//		}else
			de.setType(DataElementType.HIDDEN);
		de.setName(ConnettoriCostanti.PARAMETRO_CONNETTORE_HTTPS_KEY_STORE_PASSWORD);
		de.setSize(pageSize);
		dati.addElement(de);

		de = new DataElement();
		de.setLabel(ConnettoriCostanti.LABEL_PARAMETRO_CONNETTORE_HTTPS_PASSWORD_PRIVATE_KEY_KEYSTORE);
		de.setValue(httpspwdprivatekey);
//		if (httpsstato &&
//				(httpskeystore != null && httpskeystore.equals(ConnettoriCostanti.DEFAULT_CONNETTORE_HTTPS_KEYSTORE_CLIENT_AUTH_MODE_RIDEFINISCI))){
//			if(!this.core.isShowGestioneWorkflowStatoDocumenti() || !StatiAccordo.finale.toString().equals(stato)){
//				de.setType(DataElementType.TEXT_EDIT);
//				de.setRequired(true);	
//			}else{
//				de.setType(DataElementType.TEXT);
//			}
//		}else
			de.setType(DataElementType.HIDDEN);
		de.setName(ConnettoriCostanti.PARAMETRO_CONNETTORE_HTTPS_PASSWORD_PRIVATE_KEY_KEYSTORE);
		de.setSize(pageSize);
		dati.addElement(de);

		de = new DataElement();
		de.setLabel(ConnettoriCostanti.LABEL_PARAMETRO_CONNETTORE_HTTPS_KEY_MANAGEMENT_ALGORITM);
		de.setValue(httpsalgoritmokey);
//		if (httpsstato){
//			if(!this.core.isShowGestioneWorkflowStatoDocumenti() || !StatiAccordo.finale.toString().equals(stato)){
//				de.setType(DataElementType.TEXT_EDIT);
//				de.setRequired(true);	
//			}else{
//				de.setType(DataElementType.TEXT);
//			}
//		}else
			de.setType(DataElementType.HIDDEN);
		de.setName(ConnettoriCostanti.PARAMETRO_CONNETTORE_HTTPS_KEY_MANAGEMENT_ALGORITM);
		de.setSize(pageSize);
		dati.addElement(de);
	}
	
}
