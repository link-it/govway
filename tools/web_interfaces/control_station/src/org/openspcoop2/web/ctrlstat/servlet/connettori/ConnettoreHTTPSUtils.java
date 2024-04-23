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

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.TrustManagerFactory;

import org.openspcoop2.core.constants.CostantiDB;
import org.openspcoop2.core.registry.constants.StatiAccordo;
import org.openspcoop2.pdd.core.dynamic.DynamicHelperCostanti;
import org.openspcoop2.utils.certificate.hsm.HSMUtils;
import org.openspcoop2.utils.certificate.ocsp.OCSPProvider;
import org.openspcoop2.utils.transport.http.SSLUtilities;
import org.openspcoop2.web.ctrlstat.core.ControlStationCore;
import org.openspcoop2.web.ctrlstat.costanti.CostantiControlStation;
import org.openspcoop2.web.ctrlstat.driver.DriverControlStationException;
import org.openspcoop2.web.ctrlstat.servlet.ConsoleHelper;
import org.openspcoop2.web.lib.mvc.Costanti;
import org.openspcoop2.web.lib.mvc.DataElement;
import org.openspcoop2.web.lib.mvc.DataElementInfo;
import org.openspcoop2.web.lib.mvc.DataElementType;

/**
 * ConnettoreHTTPSUtils
 *
 * @author Andrea Poli (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ConnettoreHTTPSUtils {
	
	private ConnettoreHTTPSUtils() {}

	public static void fillConnettoreConfig(org.openspcoop2.core.config.Connettore connettore,
			String httpsurl, String httpstipologia, boolean httpshostverify,
			boolean httpsTrustVerifyCert, String httpspath, String httpstipo, String httpspwd,
			String httpsalgoritmo, boolean httpsstato, String httpskeystore,
			String httpspwdprivatekeytrust, String httpspathkey,
			String httpstipokey, String httpspwdkey,
			String httpspwdprivatekey, String httpsalgoritmokey,
			String httpsKeyAlias, String httpsTrustStoreCRLs, String httpsTrustStoreOCSPPolicy){
		
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
		prop.setNome(CostantiDB.CONNETTORE_HTTPS_TRUST_ALL_CERTS);
		if(httpsTrustVerifyCert) {
			prop.setValore(String.valueOf(false));
		}
		else {
			prop.setValore(String.valueOf(true));
		}
		connettore.addProperty(prop);
		
		if(httpsTrustVerifyCert) {
		
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
			
			if(httpsTrustStoreCRLs!=null && !"".equals(httpsTrustStoreCRLs)) {
				prop = new org.openspcoop2.core.config.Property();
				prop.setNome(CostantiDB.CONNETTORE_HTTPS_TRUST_STORE_CRLS);
				prop.setValore(httpsTrustStoreCRLs);
				connettore.addProperty(prop);
			}
			
			prop = new org.openspcoop2.core.config.Property();
			prop.setNome(CostantiDB.CONNETTORE_HTTPS_TRUST_MANAGEMENT_ALGORITM);
			if (httpsalgoritmo == null || "".equals(httpsalgoritmo))
				prop.setValore(javax.net.ssl.TrustManagerFactory.getDefaultAlgorithm());
			else
				prop.setValore(httpsalgoritmo);
			connettore.addProperty(prop);
		
		}
		
		if(httpsTrustStoreOCSPPolicy!=null && !"".equals(httpsTrustStoreOCSPPolicy)) {
			prop = new org.openspcoop2.core.config.Property();
			prop.setNome(CostantiDB.CONNETTORE_HTTPS_TRUST_STORE_OCSP_POLICY);
			prop.setValore(httpsTrustStoreOCSPPolicy);
			connettore.addProperty(prop);
			
			if(!httpsTrustVerifyCert &&
				httpsTrustStoreCRLs!=null && !"".equals(httpsTrustStoreCRLs)) {
				prop = new org.openspcoop2.core.config.Property();
				prop.setNome(CostantiDB.CONNETTORE_HTTPS_TRUST_STORE_CRLS);
				prop.setValore(httpsTrustStoreCRLs);
				connettore.addProperty(prop);
			}
		}

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
			
			if(httpsKeyAlias!=null && !"".equals(httpsKeyAlias)) {
				prop = new org.openspcoop2.core.config.Property();
				prop.setNome(CostantiDB.CONNETTORE_HTTPS_KEY_ALIAS);
				prop.setValore(httpsKeyAlias);
				connettore.addProperty(prop);
			}
		}
	}
	
	public static void fillConnettoreRegistry(org.openspcoop2.core.registry.Connettore connettore,
			String httpsurl, String httpstipologia, boolean httpshostverify,
			boolean httpsTrustVerifyCert, String httpspath, String httpstipo, String httpspwd,
			String httpsalgoritmo, boolean httpsstato, String httpskeystore,
			String httpspwdprivatekeytrust, String httpspathkey,
			String httpstipokey, String httpspwdkey,
			String httpspwdprivatekey, String httpsalgoritmokey,
			String httpsKeyAlias, String httpsTrustStoreCRLs, String httpsTrustStoreOCSPPolicy,
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
		prop.setNome(CostantiDB.CONNETTORE_HTTPS_TRUST_ALL_CERTS);
		if(httpsTrustVerifyCert) {
			prop.setValore(String.valueOf(false));
		}
		else {
			prop.setValore(String.valueOf(true));
		}
		connettore.addProperty(prop);
		
		if(httpsTrustVerifyCert) {
			
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
	
			if(httpsTrustStoreCRLs!=null && !"".equals(httpsTrustStoreCRLs)) {
				prop = new org.openspcoop2.core.registry.Property();
				prop.setNome(CostantiDB.CONNETTORE_HTTPS_TRUST_STORE_CRLS);
				prop.setValore(httpsTrustStoreCRLs);
				connettore.addProperty(prop);
			}
		
			prop = new org.openspcoop2.core.registry.Property();
			prop.setNome(CostantiDB.CONNETTORE_HTTPS_TRUST_MANAGEMENT_ALGORITM);
			if (httpsalgoritmo == null || "".equals(httpsalgoritmo))
				prop.setValore(javax.net.ssl.TrustManagerFactory.getDefaultAlgorithm());
			else
				prop.setValore(httpsalgoritmo);
			connettore.addProperty(prop);

		}
		
		
		if(httpsTrustStoreOCSPPolicy!=null && !"".equals(httpsTrustStoreOCSPPolicy)) {
			prop = new org.openspcoop2.core.registry.Property();
			prop.setNome(CostantiDB.CONNETTORE_HTTPS_TRUST_STORE_OCSP_POLICY);
			prop.setValore(httpsTrustStoreOCSPPolicy);
			connettore.addProperty(prop);
			
			if(!httpsTrustVerifyCert &&
				httpsTrustStoreCRLs!=null && !"".equals(httpsTrustStoreCRLs)) {
				prop = new org.openspcoop2.core.registry.Property();
				prop.setNome(CostantiDB.CONNETTORE_HTTPS_TRUST_STORE_CRLS);
				prop.setValore(httpsTrustStoreCRLs);
				connettore.addProperty(prop);
			}
		}
		
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
			
			if(httpsKeyAlias!=null && !"".equals(httpsKeyAlias)) {
				prop = new org.openspcoop2.core.registry.Property();
				prop.setNome(CostantiDB.CONNETTORE_HTTPS_KEY_ALIAS);
				prop.setValore(httpsKeyAlias);
				connettore.addProperty(prop);
			}
		}
	}
	
	public static void addHTTPSDati(List<DataElement> dati,
			String httpsurl, String httpstipologia, boolean httpshostverify,
			boolean httpsTrustVerifyCert, String httpspath, String httpstipo, String httpspwd,
			String httpsalgoritmo, boolean httpsstato, String httpskeystore,
			String httpspwdprivatekeytrust, String httpspathkey,
			String httpstipokey, String httpspwdkey,
			String httpspwdprivatekey, String httpsalgoritmokey, 
			String httpsKeyAlias, String httpsTrustStoreCRLs, String httpsTrustStoreOCSPPolicy,
			String stato,
			ControlStationCore core,ConsoleHelper consoleHelper, int pageSize, boolean addUrlParameter,
			String prefix, boolean forceHttpsClient,
			boolean modi, boolean fruizione, boolean forceNoSec,
			boolean postBackViaPost) throws DriverControlStationException {
		
		// default
		if(httpsalgoritmo==null || "".equals(httpsalgoritmo)){
			httpsalgoritmo = TrustManagerFactory.getDefaultAlgorithm();
		}
		if(httpsalgoritmokey==null || "".equals(httpsalgoritmokey)){
			httpsalgoritmokey = KeyManagerFactory.getDefaultAlgorithm();
		}
		if(httpstipologia==null || "".equals(httpstipologia)){
			httpstipologia = ConnettoriCostanti.DEFAULT_CONNETTORE_HTTPS_TYPE;
		}
		
		if(addUrlParameter){
			DataElement de = new DataElement();
			de.setLabel(ConnettoriCostanti.LABEL_PARAMETRO_CONNETTORE_HTTPS_URL);
			de.setValue(httpsurl);
			if(!consoleHelper.isShowGestioneWorkflowStatoDocumenti() || !StatiAccordo.finale.toString().equals(stato)){
				de.setType(DataElementType.TEXT_EDIT);
				de.setRequired(true);	
				
				DataElementInfo dInfoPatternFileName = new DataElementInfo(ConnettoriCostanti.LABEL_PARAMETRO_CONNETTORE_HTTPS_URL);
				dInfoPatternFileName.setHeaderBody(DynamicHelperCostanti.LABEL_CONFIGURAZIONE_INFO_TRASPORTO);
				dInfoPatternFileName.setListBody(DynamicHelperCostanti.getLABEL_CONFIGURAZIONE_INFO_CONNETTORE_VALORI(modi, fruizione, forceNoSec));
				de.setInfo(dInfoPatternFileName);
			}else{
				de.setType(DataElementType.TEXT);
			}
			de.setName(ConnettoriCostanti.PARAMETRO_CONNETTORE_URL);
			de.setSize(pageSize);
			dati.add(de);
		}

		if(prefix==null){
			prefix = "";
		}
		
		DataElement de = new DataElement();
		de.setLabel(prefix+ConnettoriCostanti.LABEL_CONNETTORE_AUTENTICAZIONE);
		de.setType(DataElementType.TITLE);
		dati.add(de);

		de = new DataElement();
		de.setLabel(ConnettoriCostanti.LABEL_PARAMETRO_CONNETTORE_HTTPS_SSL_TYPE);
		de.setType(DataElementType.SELECT);
		List<String> tipologie = null;
		try{
			tipologie = SSLUtilities.getSSLSupportedProtocols();
		}catch(Exception e){
			ControlStationCore.logError(e.getMessage(), e);
			tipologie = SSLUtilities.getAllSslProtocol();
		}
		if(httpstipologia!=null && !"".equals(httpstipologia) && 
				!tipologie.contains(httpstipologia)){
			tipologie.add(httpstipologia); // per retrocompatibilità delle configurazioni SSL e TLS
		}
		de.setValues(tipologie);
		de.setSelected(httpstipologia);
		de.setName(ConnettoriCostanti.PARAMETRO_CONNETTORE_HTTPS_SSL_TYPE);
		dati.add(de);

		de = new DataElement();
		de.setLabel(ConnettoriCostanti.LABEL_PARAMETRO_CONNETTORE_HTTPS_HOST_VERIFY);
		de.setValue(httpshostverify ? Costanti.CHECK_BOX_ENABLED : "");
		de.setSelected(httpshostverify);
		de.setType(DataElementType.CHECKBOX);
		de.setName(ConnettoriCostanti.PARAMETRO_CONNETTORE_HTTPS_HOST_VERIFY);
		dati.add(de);

		de = new DataElement();
		de.setLabel(prefix+ConnettoriCostanti.LABEL_CONNETTORE_AUTENTICAZIONE_SERVER);
		de.setType(DataElementType.SUBTITLE);
		dati.add(de);

		de = new DataElement();
		de.setLabel(ConnettoriCostanti.LABEL_PARAMETRO_CONNETTORE_HTTPS_TRUST_VERIFY_CERTS);
		de.setName(ConnettoriCostanti.PARAMETRO_CONNETTORE_HTTPS_TRUST_VERIFY_CERTS);
		de.setValue(httpsTrustVerifyCert ? Costanti.CHECK_BOX_ENABLED : "");
		de.setSelected(httpsTrustVerifyCert);
		de.setType(DataElementType.CHECKBOX);
		if(postBackViaPost) {
			de.setPostBack_viaPOST(true);
		}
		else {
			de.setPostBack(true);
		}
		dati.add(de);
		
		boolean truststoreHsm = false;
		de = new DataElement();
		de.setLabel(ConnettoriCostanti.LABEL_PARAMETRO_CONNETTORE_HTTPS_TRUST_STORE_TYPE);
		de.setName(ConnettoriCostanti.PARAMETRO_CONNETTORE_HTTPS_TRUST_STORE_TYPE);
		if(httpsTrustVerifyCert) {
			de.setType(DataElementType.SELECT);
			if(core.isConnettoriAllTypesEnabled()) {
				de.setValues(ConnettoriCostanti.TIPOLOGIE_KEYSTORE_OLD);
			}
			else {
				List<String> values = ConnettoriCostanti.getTIPOLOGIE_KEYSTORE(true, false);
				List<String> labels = ConnettoriCostanti.getTIPOLOGIE_KEYSTORE(true, true);
				// per retrocompatibilita verifico che esista ancora nelle nuove, altrimenti lo aggiungo
				if(httpstipo!=null && !"".equals(httpstipo)) {
					boolean exists = false;
					for (String v : values) {
						if(httpstipo.equalsIgnoreCase(v)) {
							exists = true;
						}
					}
					if(!exists) {
						values.add(httpstipo);
					}
				}
				de.setValues(values);
				de.setLabels(labels);
				if(ConnettoriCostanti.existsTIPOLOGIE_KEYSTORE_HSM(true)) {
					if(postBackViaPost) {
						de.setPostBack_viaPOST(true);
					}
					else {
						de.setPostBack(true);
					}
				}
			}
			if(httpstipo!=null) {
				de.setSelected(httpstipo);
				truststoreHsm = HSMUtils.isKeystoreHSM(httpstipo);
			}
		}
		else {
			de.setType(DataElementType.HIDDEN);
		}
		dati.add(de);

		de = new DataElement();
		de.setLabel(ConnettoriCostanti.LABEL_PARAMETRO_CONNETTORE_HTTPS_TRUST_STORE_LOCATION);
		de.setValue(httpspath);
		if(httpsTrustVerifyCert) {
			if(truststoreHsm) {
				de.setValue(ConnettoriCostanti.DEFAULT_CONNETTORE_HTTPS_PATH_HSM_PREFIX+httpstipo);
				de.setType(DataElementType.HIDDEN);
			}
			else {
				if(!consoleHelper.isShowGestioneWorkflowStatoDocumenti() || !StatiAccordo.finale.toString().equals(stato)){
					de.setType(DataElementType.TEXT_AREA);
					de.setRequired(true);	
				}else{
					de.setType(DataElementType.TEXT_AREA_NO_EDIT);
				}
				de.setRows(ConnettoriCostanti.LABEL_PARAMETRO_CONNETTORE_HTTPS_TRUST_STORE_LOCATION_SIZE);
				de.setSize(pageSize);
			}
		}
		else {
			de.setType(DataElementType.HIDDEN);
		}
		de.setName(ConnettoriCostanti.PARAMETRO_CONNETTORE_HTTPS_TRUST_STORE_LOCATION);
		dati.add(de);
		
		de = new DataElement();
		de.setLabel(ConnettoriCostanti.LABEL_PARAMETRO_CONNETTORE_HTTPS_TRUST_STORE_PASSWORD);
		if(httpsTrustVerifyCert) {
			if(truststoreHsm) {
				de.setValue(ConnettoriCostanti.DEFAULT_CONNETTORE_HTTPS_HSM_STORE_PASSWORD_UNDEFINED);
				de.setType(DataElementType.HIDDEN);
			}
			else if(!consoleHelper.isShowGestioneWorkflowStatoDocumenti() || !StatiAccordo.finale.toString().equals(stato)){
				de.setRequired(true);	
				core.lock(de, httpspwd);
			}else{
				core.lockReadOnly(de, httpspwd);
			}
			de.setSize(pageSize);
		}
		else {
			core.lockHidden(de, httpspwd);
		}
		de.setName(ConnettoriCostanti.PARAMETRO_CONNETTORE_HTTPS_TRUST_STORE_PASSWORD);
		dati.add(de);

		OCSPProvider ocspProvider = new OCSPProvider();
		boolean ocspEnabled = ocspProvider.isOcspEnabled();
		List<String> ocspTypes = ocspProvider.getValues();
		List<String> ocspLabels = ocspProvider.getLabels();
				
		boolean crlWithOCSPEnabledTrustAllHttpsServer = 
				ocspEnabled &&
				core.isOCSPPolicyChoiceConnettoreHTTPSVerificaServerDisabilitata() &&
				httpsTrustStoreOCSPPolicy!=null &&
				!"".equals(httpsTrustStoreOCSPPolicy);
				
		de = new DataElement();
		de.setName(ConnettoriCostanti.PARAMETRO_CONNETTORE_HTTPS_TRUST_STORE_OCSP_POLICY);
		de.setLabel(ConnettoriCostanti.LABEL_PARAMETRO_CONNETTORE_HTTPS_TRUST_STORE_OCSP_POLICY);
		if(ocspEnabled && (httpsTrustVerifyCert || core.isOCSPPolicyChoiceConnettoreHTTPSVerificaServerDisabilitata())) {
			if(!consoleHelper.isShowGestioneWorkflowStatoDocumenti() || !StatiAccordo.finale.toString().equals(stato)){
				de.setType(DataElementType.SELECT);
				de.setValues(ocspTypes);
				de.setLabels(ocspLabels);
				if(postBackViaPost) {
					de.setPostBack_viaPOST(core.isOCSPPolicyChoiceConnettoreHTTPSVerificaServerDisabilitata());
				}
				else {
					de.setPostBack(core.isOCSPPolicyChoiceConnettoreHTTPSVerificaServerDisabilitata());
				}
				if(httpsTrustStoreOCSPPolicy==null) {
					httpsTrustStoreOCSPPolicy = "";
				}	
				de.setSelected(httpsTrustStoreOCSPPolicy);
			}else{
				de.setType(DataElementType.HIDDEN);
				
				if(httpsTrustStoreOCSPPolicy!=null &&
						!"".equals(httpsTrustStoreOCSPPolicy)){
					
					String label = null;
					for (int i = 0; i < ocspTypes.size(); i++) {
						String type = ocspTypes.get(i);
						if(type!=null && type.equals(httpsTrustStoreOCSPPolicy)) {
							label = ocspLabels.get(i);
						}
					}
					if(label!=null) {
						DataElement deLABEL = new DataElement();
						de.setType(DataElementType.TEXT);
						deLABEL.setLabel(ConnettoriCostanti.LABEL_PARAMETRO_CONNETTORE_HTTPS_TRUST_STORE_OCSP_POLICY);
						deLABEL.setValue(label);
						deLABEL.setName(ConnettoriCostanti.PARAMETRO_CONNETTORE_HTTPS_TRUST_STORE_OCSP_POLICY+CostantiControlStation.PARAMETRO_SUFFIX_LABEL);
						dati.add(deLABEL);
					}
				}
			}
			de.setSize(pageSize);
		}
		else {
			de.setType(DataElementType.HIDDEN);
		}
		de.setValue(httpsTrustStoreOCSPPolicy);
		dati.add(de);
		
		de = new DataElement();
		de.setLabel(ConnettoriCostanti.LABEL_PARAMETRO_CONNETTORE_HTTPS_TRUST_STORE_CRL);
		de.setValue(httpsTrustStoreCRLs);
		if(httpsTrustVerifyCert || crlWithOCSPEnabledTrustAllHttpsServer) {
			de.setNote(ConnettoriCostanti.LABEL_PARAMETRO_CONNETTORE_HTTPS_TRUST_STORE_CRL_NOTE);
			if(!consoleHelper.isShowGestioneWorkflowStatoDocumenti() || !StatiAccordo.finale.toString().equals(stato)){
				de.setType(DataElementType.TEXT_AREA);	
			}else{
				de.setType(DataElementType.TEXT_AREA_NO_EDIT);
			}
			de.setRows(ConnettoriCostanti.LABEL_PARAMETRO_CONNETTORE_HTTPS_TRUST_STORE_CRL_SIZE);
			de.setSize(pageSize);
		}
		else {
			de.setType(DataElementType.HIDDEN);
		}
		de.setName(ConnettoriCostanti.PARAMETRO_CONNETTORE_HTTPS_TRUST_STORE_CRL);
		dati.add(de);
		
		de = new DataElement();
		de.setLabel(ConnettoriCostanti.LABEL_PARAMETRO_CONNETTORE_HTTPS_TRUST_MANAGEMENT_ALGORITM);
		de.setValue(httpsalgoritmo);
		if(httpsTrustVerifyCert) {
			if(!consoleHelper.isModalitaStandard()) {
				if(!consoleHelper.isShowGestioneWorkflowStatoDocumenti() || !StatiAccordo.finale.toString().equals(stato)){
					de.setType(DataElementType.TEXT_EDIT);
					de.setRequired(true);	
				}else{
					de.setType(DataElementType.TEXT);
				}
				de.setSize(pageSize);
			}
			else {
				de.setType(DataElementType.HIDDEN);
			}
		}
		else {
			de.setType(DataElementType.HIDDEN);
		}
		de.setName(ConnettoriCostanti.PARAMETRO_CONNETTORE_HTTPS_TRUST_MANAGEMENT_ALGORITM);
		dati.add(de);

		de = new DataElement();
		de.setLabel(prefix+ConnettoriCostanti.LABEL_CONNETTORE_AUTENTICAZIONE_CLIENT);
		de.setType(DataElementType.SUBTITLE);
		dati.add(de);

		de = new DataElement();
		de.setLabel(ConnettoriCostanti.LABEL_PARAMETRO_CONNETTORE_HTTPS_STATO);
		de.setName(ConnettoriCostanti.PARAMETRO_CONNETTORE_HTTPS_STATO);
		if(forceHttpsClient) {
			httpsstato = true;
			de.setValue(Costanti.CHECK_BOX_ENABLED);
			de.setType(DataElementType.HIDDEN);
		}
		else {
			de.setValue(httpsstato ? Costanti.CHECK_BOX_ENABLED : "");
			de.setSelected(httpsstato);
			de.setType(DataElementType.CHECKBOX);
			if(postBackViaPost) {
				de.setPostBack_viaPOST(true);
			}
			else {
				de.setPostBack(true);
			}
		}
		dati.add(de);


		de = new DataElement();
		de.setLabel(ConnettoriCostanti.LABEL_PARAMETRO_CONNETTORE_HTTPS_KEYSTORE_CLIENT_AUTH_MODE);
		de.setName(ConnettoriCostanti.PARAMETRO_CONNETTORE_HTTPS_KEYSTORE_CLIENT_AUTH_MODE);
		if (httpsstato) {
			if(httpsTrustVerifyCert) {
				de.setType(DataElementType.SELECT);
				de.setValues(ConnettoriCostanti.DEFAULT_CONNETTORE_HTTPS_KEYSTORE_CLIENT_AUTH_MODES);
				de.setLabels(ConnettoriCostanti.DEFAULT_CONNETTORE_HTTPS_KEYSTORE_CLIENT_AUTH_LABEL_MODES);
				de.setSelected(httpskeystore);
				if(postBackViaPost) {
					de.setPostBack_viaPOST(true);
				}
				else {
					de.setPostBack(true);
				}
			}
			else {
				de.setType(DataElementType.HIDDEN);
				httpskeystore = ConnettoriCostanti.DEFAULT_CONNETTORE_HTTPS_KEYSTORE_CLIENT_AUTH_MODE_RIDEFINISCI;
				de.setValue(ConnettoriCostanti.DEFAULT_CONNETTORE_HTTPS_KEYSTORE_CLIENT_AUTH_MODE_RIDEFINISCI);
			}
		} else {
			de.setType(DataElementType.HIDDEN);
			de.setValue("");
		}
		dati.add(de);

		de = new DataElement();
		de.setLabel(ConnettoriCostanti.LABEL_PARAMETRO_CONNETTORE_HTTPS_PASSWORD_PRIVATE_KEY_STORE);
		if (httpsstato &&
				(httpskeystore == null || "".equals(httpskeystore) || httpskeystore.equals(ConnettoriCostanti.DEFAULT_CONNETTORE_HTTPS_KEYSTORE_CLIENT_AUTH_MODE_DEFAULT))){
			if(!consoleHelper.isShowGestioneWorkflowStatoDocumenti() || !StatiAccordo.finale.toString().equals(stato)){
				de.setRequired(true);	
				core.lock(de, httpspwdprivatekeytrust);
			}else{
				core.lockReadOnly(de, httpspwdprivatekeytrust);
			}
		}else {
			core.lockHidden(de, httpspwdprivatekeytrust);
		}
		de.setName(ConnettoriCostanti.PARAMETRO_CONNETTORE_HTTPS_PASSWORD_PRIVATE_KEY_STORE);
		de.setSize(pageSize);
		dati.add(de);

		boolean keystoreHsm = false;
		de = new DataElement();
		de.setLabel(ConnettoriCostanti.LABEL_PARAMETRO_CONNETTORE_HTTPS_KEY_STORE_TYPE);
		de.setName(ConnettoriCostanti.PARAMETRO_CONNETTORE_HTTPS_KEY_STORE_TYPE);
		if (httpsstato &&
				(httpskeystore != null && httpskeystore.equals(ConnettoriCostanti.DEFAULT_CONNETTORE_HTTPS_KEYSTORE_CLIENT_AUTH_MODE_RIDEFINISCI))) {
			de.setType(DataElementType.SELECT);
			if(core.isConnettoriAllTypesEnabled()) {
				de.setValues(ConnettoriCostanti.TIPOLOGIE_KEYSTORE_OLD);
			}
			else {
				List<String> values = ConnettoriCostanti.getTIPOLOGIE_KEYSTORE(false, false);
				List<String> labels = ConnettoriCostanti.getTIPOLOGIE_KEYSTORE(false, true);
				// per retrocompatibilita verifico che esista ancora nelle nuove, altrimenti lo aggiungo
				if(httpstipokey!=null && !"".equals(httpstipokey)) {
					boolean exists = false;
					for (String v : values) {
						if(httpstipokey.equalsIgnoreCase(v)) {
							exists = true;
							break;
						}
					}
					if(!exists) {
						values.add(httpstipokey);
					}
				}
				de.setValues(values);
				de.setLabels(labels);
				if(ConnettoriCostanti.existsTIPOLOGIE_KEYSTORE_HSM(false)) {
					if(postBackViaPost) {
						de.setPostBack_viaPOST(true);
					}
					else {
						de.setPostBack(true);
					}
				}
			}
			if(httpstipokey!=null) {
				de.setSelected(httpstipokey);
				keystoreHsm = HSMUtils.isKeystoreHSM(httpstipokey);
			}
		} else {
			de.setType(DataElementType.HIDDEN);
			de.setValue("");
		}
		dati.add(de);
		
		de = new DataElement();
		de.setLabel(ConnettoriCostanti.LABEL_PARAMETRO_CONNETTORE_HTTPS_KEY_STORE_LOCATION);
		de.setValue(httpspathkey);
		if (httpsstato &&
				(httpskeystore != null && httpskeystore.equals(ConnettoriCostanti.DEFAULT_CONNETTORE_HTTPS_KEYSTORE_CLIENT_AUTH_MODE_RIDEFINISCI))){
			if(keystoreHsm) {
				de.setValue(ConnettoriCostanti.DEFAULT_CONNETTORE_HTTPS_PATH_HSM_PREFIX+httpstipokey);
				de.setType(DataElementType.HIDDEN);
			}
			else {
				if(!consoleHelper.isShowGestioneWorkflowStatoDocumenti() || !StatiAccordo.finale.toString().equals(stato)){
					de.setType(DataElementType.TEXT_AREA);
					de.setRequired(true);	
				}else{
					de.setType(DataElementType.TEXT_AREA_NO_EDIT);
				}
				de.setRows(ConnettoriCostanti.LABEL_PARAMETRO_CONNETTORE_HTTPS_KEY_STORE_LOCATION_SIZE);
			}
		}else
			de.setType(DataElementType.HIDDEN);
		de.setName(ConnettoriCostanti.PARAMETRO_CONNETTORE_HTTPS_KEY_STORE_LOCATION);
		de.setSize(pageSize);
		dati.add(de);

		de = new DataElement();
		de.setLabel(ConnettoriCostanti.LABEL_PARAMETRO_CONNETTORE_HTTPS_KEY_STORE_PASSWORD);
		if (httpsstato &&
				(httpskeystore != null && httpskeystore.equals(ConnettoriCostanti.DEFAULT_CONNETTORE_HTTPS_KEYSTORE_CLIENT_AUTH_MODE_RIDEFINISCI))){
			if(keystoreHsm) {
				de.setValue(ConnettoriCostanti.DEFAULT_CONNETTORE_HTTPS_HSM_STORE_PASSWORD_UNDEFINED);
				de.setType(DataElementType.HIDDEN);
			}
			else if(!consoleHelper.isShowGestioneWorkflowStatoDocumenti() || !StatiAccordo.finale.toString().equals(stato)){
				de.setRequired(true);	
				core.lock(de, httpspwdkey);
			}else{
				core.lockReadOnly(de, httpspwdkey);
			}
		}else {
			core.lockHidden(de, httpspwdkey);
		}
		de.setName(ConnettoriCostanti.PARAMETRO_CONNETTORE_HTTPS_KEY_STORE_PASSWORD);
		de.setSize(pageSize);
		dati.add(de);

		de = new DataElement();
		de.setLabel(ConnettoriCostanti.LABEL_PARAMETRO_CONNETTORE_HTTPS_PASSWORD_PRIVATE_KEY_KEYSTORE);
		if (httpsstato &&
				(httpskeystore != null && httpskeystore.equals(ConnettoriCostanti.DEFAULT_CONNETTORE_HTTPS_KEYSTORE_CLIENT_AUTH_MODE_RIDEFINISCI))){
			if(keystoreHsm && !ConnettoriCostanti.DEFAULT_CONNETTORE_HTTPS_HSM_CONFIGURABLE_KEY_PASSWORD) {
				de.setValue(ConnettoriCostanti.DEFAULT_CONNETTORE_HTTPS_HSM_PRIVATE_KEY_PASSWORD_UNDEFINED);
				de.setType(DataElementType.HIDDEN);
			}
			else if(!consoleHelper.isShowGestioneWorkflowStatoDocumenti() || !StatiAccordo.finale.toString().equals(stato)){
				de.setRequired(true);
				core.lock(de, httpspwdprivatekey);
			}else{
				core.lockReadOnly(de, httpspwdprivatekey);
			}
		}else {
			core.lockHidden(de, httpspwdprivatekey);
		}
		de.setName(ConnettoriCostanti.PARAMETRO_CONNETTORE_HTTPS_PASSWORD_PRIVATE_KEY_KEYSTORE);
		de.setSize(pageSize);
		dati.add(de);

		de = new DataElement();
		de.setLabel(ConnettoriCostanti.LABEL_PARAMETRO_CONNETTORE_HTTPS_ALIAS_PRIVATE_KEY_KEYSTORE);
		de.setValue(httpsKeyAlias);
		if (httpsstato){
			if(!consoleHelper.isShowGestioneWorkflowStatoDocumenti() || !StatiAccordo.finale.toString().equals(stato)){
				de.setType(DataElementType.TEXT_EDIT);	
			}else{
				de.setType(DataElementType.TEXT);
			}
		}else
			de.setType(DataElementType.HIDDEN);
		de.setName(ConnettoriCostanti.PARAMETRO_CONNETTORE_HTTPS_ALIAS_PRIVATE_KEY_KEYSTORE);
		de.setSize(pageSize);
		dati.add(de);
		
		de = new DataElement();
		de.setLabel(ConnettoriCostanti.LABEL_PARAMETRO_CONNETTORE_HTTPS_KEY_MANAGEMENT_ALGORITM);
		de.setValue(httpsalgoritmokey);
		if (httpsstato && !consoleHelper.isModalitaStandard()){
			if(!consoleHelper.isShowGestioneWorkflowStatoDocumenti() || !StatiAccordo.finale.toString().equals(stato)){
				de.setType(DataElementType.TEXT_EDIT);
				de.setRequired(true);	
			}else{
				de.setType(DataElementType.TEXT);
			}
		}else
			de.setType(DataElementType.HIDDEN);
		de.setName(ConnettoriCostanti.PARAMETRO_CONNETTORE_HTTPS_KEY_MANAGEMENT_ALGORITM);
		de.setSize(pageSize);
		dati.add(de);
	}
	
	public static void addHTTPSDatiAsHidden(List<DataElement> dati,
			String httpsurl, String httpstipologia, boolean httpshostverify,
			boolean httpsTrustVerifyCert, String httpspath, String httpstipo, String httpspwd,
			String httpsalgoritmo, boolean httpsstato, String httpskeystore,
			String httpspwdprivatekeytrust, String httpspathkey,
			String httpstipokey, String httpspwdkey,
			String httpspwdprivatekey, String httpsalgoritmokey,
			String httpsKeyAlias, String httpsTrustStoreCRLs, String httpsTrustStoreOCSPPolicy,
			String stato,
			ControlStationCore core,int pageSize){
		
		DataElement de = new DataElement();
		de.setLabel(ConnettoriCostanti.LABEL_PARAMETRO_CONNETTORE_HTTPS_URL);
		de.setValue(httpsurl);
		de.setType(DataElementType.HIDDEN);
		de.setName(ConnettoriCostanti.PARAMETRO_CONNETTORE_URL);
		de.setSize(pageSize);
		dati.add(de);


		de = new DataElement();
		de.setLabel(ConnettoriCostanti.LABEL_PARAMETRO_CONNETTORE_HTTPS_SSL_TYPE);
		de.setType(DataElementType.HIDDEN);
		de.setValue(httpstipologia);
		de.setName(ConnettoriCostanti.PARAMETRO_CONNETTORE_HTTPS_SSL_TYPE);
		dati.add(de);

		de = new DataElement();
		de.setLabel(ConnettoriCostanti.LABEL_PARAMETRO_CONNETTORE_HTTPS_HOST_VERIFY);
		de.setValue(httpshostverify ? Costanti.CHECK_BOX_ENABLED : "");
		de.setType(DataElementType.HIDDEN);
		de.setName(ConnettoriCostanti.PARAMETRO_CONNETTORE_HTTPS_HOST_VERIFY);
		dati.add(de);

		de = new DataElement();
		de.setLabel(ConnettoriCostanti.LABEL_PARAMETRO_CONNETTORE_HTTPS_TRUST_VERIFY_CERTS);
		de.setValue(httpsTrustVerifyCert ? Costanti.CHECK_BOX_ENABLED : "");
		de.setType(DataElementType.HIDDEN);
		de.setName(ConnettoriCostanti.PARAMETRO_CONNETTORE_HTTPS_TRUST_VERIFY_CERTS);
		de.setSize(pageSize);
		dati.add(de);
		
		de = new DataElement();
		de.setLabel(ConnettoriCostanti.LABEL_PARAMETRO_CONNETTORE_HTTPS_TRUST_STORE_LOCATION);
		de.setValue(httpspath);
		de.setType(DataElementType.HIDDEN);
		de.setName(ConnettoriCostanti.PARAMETRO_CONNETTORE_HTTPS_TRUST_STORE_LOCATION);
		de.setSize(pageSize);
		dati.add(de);

		de = new DataElement();
		de.setLabel(ConnettoriCostanti.LABEL_PARAMETRO_CONNETTORE_HTTPS_TRUST_STORE_TYPE);
		de.setType(DataElementType.HIDDEN);
		de.setValue(httpstipo);
		de.setName(ConnettoriCostanti.PARAMETRO_CONNETTORE_HTTPS_TRUST_STORE_TYPE);
		dati.add(de);

		de = new DataElement();
		de.setLabel(ConnettoriCostanti.LABEL_PARAMETRO_CONNETTORE_HTTPS_TRUST_STORE_PASSWORD);
		de.setValue(httpspwd);
		de.setType(DataElementType.HIDDEN);
		de.setName(ConnettoriCostanti.PARAMETRO_CONNETTORE_HTTPS_TRUST_STORE_PASSWORD);
		de.setSize(pageSize);
		dati.add(de);

		de = new DataElement();
		de.setLabel(ConnettoriCostanti.LABEL_PARAMETRO_CONNETTORE_HTTPS_TRUST_STORE_CRL);
		de.setValue(httpsTrustStoreCRLs);
		de.setType(DataElementType.HIDDEN);
		de.setName(ConnettoriCostanti.PARAMETRO_CONNETTORE_HTTPS_TRUST_STORE_CRL);
		de.setSize(pageSize);
		dati.add(de);
		
		de = new DataElement();
		de.setLabel(ConnettoriCostanti.LABEL_PARAMETRO_CONNETTORE_HTTPS_TRUST_STORE_OCSP_POLICY);
		de.setValue(httpsTrustStoreOCSPPolicy);
		de.setType(DataElementType.HIDDEN);
		de.setName(ConnettoriCostanti.PARAMETRO_CONNETTORE_HTTPS_TRUST_STORE_OCSP_POLICY);
		de.setSize(pageSize);
		dati.add(de);
		
		de = new DataElement();
		de.setLabel(ConnettoriCostanti.LABEL_PARAMETRO_CONNETTORE_HTTPS_TRUST_MANAGEMENT_ALGORITM);
		de.setValue(httpsalgoritmo);
		de.setType(DataElementType.HIDDEN);
		de.setName(ConnettoriCostanti.PARAMETRO_CONNETTORE_HTTPS_TRUST_MANAGEMENT_ALGORITM);
		de.setSize(pageSize);
		dati.add(de);

		de = new DataElement();
		de.setLabel(ConnettoriCostanti.LABEL_PARAMETRO_CONNETTORE_HTTPS_STATO);
		de.setValue(httpsstato ? Costanti.CHECK_BOX_ENABLED : "");
		de.setType(DataElementType.HIDDEN);
		de.setName(ConnettoriCostanti.PARAMETRO_CONNETTORE_HTTPS_STATO);
		dati.add(de);


		de = new DataElement();
		de.setLabel(ConnettoriCostanti.LABEL_PARAMETRO_CONNETTORE_HTTPS_KEYSTORE_CLIENT_AUTH_MODE);
		de.setName(ConnettoriCostanti.PARAMETRO_CONNETTORE_HTTPS_KEYSTORE_CLIENT_AUTH_MODE);
		de.setType(DataElementType.HIDDEN);	
		if (httpsstato) {
			de.setValue(httpskeystore);
		} else {
			de.setValue("");
		}
		dati.add(de);

		de = new DataElement();
		de.setLabel(ConnettoriCostanti.LABEL_PARAMETRO_CONNETTORE_HTTPS_PASSWORD_PRIVATE_KEY_STORE);
		de.setValue(httpspwdprivatekeytrust);
		de.setType(DataElementType.HIDDEN);
		de.setName(ConnettoriCostanti.PARAMETRO_CONNETTORE_HTTPS_PASSWORD_PRIVATE_KEY_STORE);
		de.setSize(pageSize);
		dati.add(de);

		de = new DataElement();
		de.setLabel(ConnettoriCostanti.LABEL_PARAMETRO_CONNETTORE_HTTPS_KEY_STORE_LOCATION);
		de.setValue(httpspathkey);
		de.setType(DataElementType.HIDDEN);
		de.setName(ConnettoriCostanti.PARAMETRO_CONNETTORE_HTTPS_KEY_STORE_LOCATION);
		de.setSize(pageSize);
		dati.add(de);

		de = new DataElement();
		de.setLabel(ConnettoriCostanti.LABEL_PARAMETRO_CONNETTORE_HTTPS_KEY_STORE_TYPE);
		if (httpsstato &&
				(httpskeystore != null && httpskeystore.equals(ConnettoriCostanti.DEFAULT_CONNETTORE_HTTPS_KEYSTORE_CLIENT_AUTH_MODE_RIDEFINISCI))) {
			de.setType(DataElementType.HIDDEN);
			de.setValue(httpstipokey);
		} else {
			de.setType(DataElementType.HIDDEN);
			de.setValue("");
		}
		de.setName(ConnettoriCostanti.PARAMETRO_CONNETTORE_HTTPS_KEY_STORE_TYPE);
		dati.add(de);

		de = new DataElement();
		de.setLabel(ConnettoriCostanti.LABEL_PARAMETRO_CONNETTORE_HTTPS_KEY_STORE_PASSWORD);
		de.setValue(httpspwdkey);
		de.setType(DataElementType.HIDDEN);
		de.setName(ConnettoriCostanti.PARAMETRO_CONNETTORE_HTTPS_KEY_STORE_PASSWORD);
		de.setSize(pageSize);
		dati.add(de);

		de = new DataElement();
		de.setLabel(ConnettoriCostanti.LABEL_PARAMETRO_CONNETTORE_HTTPS_PASSWORD_PRIVATE_KEY_KEYSTORE);
		de.setValue(httpspwdprivatekey);
		de.setType(DataElementType.HIDDEN);
		de.setName(ConnettoriCostanti.PARAMETRO_CONNETTORE_HTTPS_PASSWORD_PRIVATE_KEY_KEYSTORE);
		de.setSize(pageSize);
		dati.add(de);
		
		de = new DataElement();
		de.setLabel(ConnettoriCostanti.LABEL_PARAMETRO_CONNETTORE_HTTPS_ALIAS_PRIVATE_KEY_KEYSTORE);
		de.setValue(httpsKeyAlias);
		de.setType(DataElementType.HIDDEN);
		de.setName(ConnettoriCostanti.PARAMETRO_CONNETTORE_HTTPS_ALIAS_PRIVATE_KEY_KEYSTORE);
		de.setSize(pageSize);
		dati.add(de);

		de = new DataElement();
		de.setLabel(ConnettoriCostanti.LABEL_PARAMETRO_CONNETTORE_HTTPS_KEY_MANAGEMENT_ALGORITM);
		de.setValue(httpsalgoritmokey);
		de.setType(DataElementType.HIDDEN);
		de.setName(ConnettoriCostanti.PARAMETRO_CONNETTORE_HTTPS_KEY_MANAGEMENT_ALGORITM);
		de.setSize(pageSize);
		dati.add(de);
	}
	
}
