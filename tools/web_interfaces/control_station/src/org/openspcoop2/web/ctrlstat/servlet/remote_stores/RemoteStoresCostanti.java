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
package org.openspcoop2.web.ctrlstat.servlet.remote_stores;

import java.util.ArrayList;
import java.util.List;

import org.openspcoop2.pdd.config.PDNDResolver;
import org.openspcoop2.web.lib.mvc.Costanti;

/**
 * RemoteStoresCostanti
 *
 * @author Pintori Giuliano (pintori@link.it)
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class RemoteStoresCostanti {

	private RemoteStoresCostanti() {}
	
	public static final String OBJECT_NAME_REMOTE_STORES = "remoteStores";
	public static final String OBJECT_NAME_REMOTE_STORES_KEYS = "remoteStoresKeys";
	
	public static final String SERVLET_NAME_REMOTE_STORES_KEYS_CHANGE = OBJECT_NAME_REMOTE_STORES_KEYS+Costanti.STRUTS_ACTION_SUFFIX_CHANGE;
	public static final String SERVLET_NAME_REMOTE_STORES_KEYS_DELETE = OBJECT_NAME_REMOTE_STORES_KEYS+Costanti.STRUTS_ACTION_SUFFIX_DELETE;
	public static final String SERVLET_NAME_REMOTE_STORES_KEYS_LIST = OBJECT_NAME_REMOTE_STORES_KEYS+Costanti.STRUTS_ACTION_SUFFIX_LIST;
	private static final List<String> SERVLET_REMOTE_STORES_KEYS = new ArrayList<>();
	public static List<String> getServletRuoli() {
		return SERVLET_REMOTE_STORES_KEYS;
	}
	static{
		SERVLET_REMOTE_STORES_KEYS.add(SERVLET_NAME_REMOTE_STORES_KEYS_CHANGE);
		SERVLET_REMOTE_STORES_KEYS.add(SERVLET_NAME_REMOTE_STORES_KEYS_DELETE);
		SERVLET_REMOTE_STORES_KEYS.add(SERVLET_NAME_REMOTE_STORES_KEYS_LIST);
	}
	
	public static final String LABEL_CACHE_PDND = "Cache PDND";
	public static final String getBreadCrumbRemoteStore(String remoteStore) {
		return LABEL_CACHE_PDND + " (" + remoteStore + ")";
	}
	
	public static final String LABEL_REMOTE_STORE = "Remote Store";
	public static final String LABEL_REMOTE_STORES = "Remote Stores";
	
	public static final String LABEL_REMOTE_STORES_KEY = "Chiave";
	public static final String LABEL_REMOTE_STORES_KEYS = "Chiavi";
	
	public static final String LABEL_NESSUN_REMOTE_STORE_PRESENTE = "Nessun Remote Store disponibile";
	
	/* PARAMETRI */
	
	public static final String PARAMETRO_REMOTE_STORE_ID = "remoteStoreId";
	public static final String PARAMETRO_REMOTE_STORE_NOME = "remoteStoreNome";
	public static final String PARAMETRO_REMOTE_STORE_KEY_ID = "remoteStoreKeyId";
	public static final String PARAMETRO_REMOTE_STORE_KEY_KID = "remoteStoreKeyKid";
	public static final String PARAMETRO_REMOTE_STORE_KEY_CONTENT_KEY = "remoteStoreKeyContentKey";
	public static final String PARAMETRO_REMOTE_STORE_KEY_CLIENT_ID = "remoteStoreKeyClientId";
	public static final String PARAMETRO_REMOTE_STORE_KEY_CLIENT_DETAILS = "remoteStoreKeyClientDetails";
	public static final String PARAMETRO_REMOTE_STORE_KEY_ORGANIZATION_DETAILS = "remoteStoreKeyOrganizationDetails";
	public static final String PARAMETRO_REMOTE_STORE_KEY_CLIENT_DATA_AGGIORNAMENTO = "remoteStoreKeyClientDataAgg";
	public static final String PARAMETRO_REMOTE_STORE_KEY_DATA_REGISTRAZIONE = "remoteStoreKeyDataReg";
	public static final String PARAMETRO_REMOTE_STORE_KEY_DATA_AGGIORAMENTO = "remoteStoreKeyDataAgg";

	
	/* LABEL PARAMETRI */
	
	public static final String LABEL_PARAMETRO_REMOTE_STORE_ID = "Id";
	public static final String LABEL_PARAMETRO_REMOTE_STORE_NOME = "Nome";
	public static final String LABEL_PARAMETRO_REMOTE_STORE_KEY_ID = "Id";
	public static final String LABEL_PARAMETRO_REMOTE_STORE_KEY_KID = "Kid";
	public static final String LABEL_PARAMETRO_REMOTE_STORE_KEY_CONTENT_KEY = "Chiave Pubblica";
	public static final String LABEL_PARAMETRO_REMOTE_STORE_KEY_CLIENT_ID = "Client Id";
	public static final String LABEL_PARAMETRO_REMOTE_STORE_KEY_CLIENT_DETAILS = "Dettagli Client";
	public static final String LABEL_PARAMETRO_REMOTE_STORE_KEY_ORGANIZATION_DETAILS = "Dettagli Organizzazione";
	public static final String LABEL_PARAMETRO_REMOTE_STORE_KEY_CLIENT_DATA_AGGIORNAMENTO = "Data Ultimo Aggiornamento";
	public static final String LABEL_PARAMETRO_REMOTE_STORE_KEY_DATA_REGISTRAZIONE = "Data Registrazione";
	public static final String LABEL_PARAMETRO_REMOTE_STORE_KEY_DATA_AGGIORAMENTO = "Data Ultimo Aggiornamento";
	public static final String LABEL_DOWNLOAD = "Download";
	public static final String LABEL_INFORMAZIONI_CLIENT = "Informazioni Client";
	
	/* VALORI PARAMETRI */
	public static final String VALUE_PARAMETRO_REMOTE_STORE_KEY_KID_STARTS_WITH_CLIENT_ID = PDNDResolver.REMOTE_STORE_KEY_KID_STARTS_WITH_CLIENT_ID;
	public static final String VALUE_PARAMETRO_NON_PRESENTE = "-";
	
}
