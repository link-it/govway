package org.openspcoop2.web.ctrlstat.servlet.remote_stores;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.openspcoop2.core.commons.Filtri;
import org.openspcoop2.core.commons.Liste;
import org.openspcoop2.core.commons.SearchUtils;
import org.openspcoop2.pdd.core.keystore.RemoteStore;
import org.openspcoop2.pdd.core.keystore.RemoteStoreKeyEntry;
import org.openspcoop2.utils.date.DateUtils;
import org.openspcoop2.web.ctrlstat.core.ConsoleSearch;
import org.openspcoop2.web.ctrlstat.core.ControlStationCore;
import org.openspcoop2.web.ctrlstat.servlet.ConsoleHelper;
import org.openspcoop2.web.ctrlstat.servlet.archivi.ArchiviCostanti;
import org.openspcoop2.web.lib.mvc.DataElement;
import org.openspcoop2.web.lib.mvc.DataElementType;
import org.openspcoop2.web.lib.mvc.PageData;
import org.openspcoop2.web.lib.mvc.Parameter;
import org.openspcoop2.web.lib.mvc.ServletUtils;

/**
 * RemoteStoresHelper
 *
 * @author Pintori Giuliano (pintori@link.it)
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class RemoteStoresHelper extends ConsoleHelper{

	public RemoteStoresHelper(HttpServletRequest request, PageData pd, HttpSession session) {
		super(request, pd,  session);
	}
	public RemoteStoresHelper(ControlStationCore core, HttpServletRequest request, PageData pd, HttpSession session) {
		super(core, request, pd,  session);
	}


	public void prepareRemoteStoreKeysList(ConsoleSearch ricerca, List<RemoteStoreKeyEntry> lista, long remoteStoreId) {
		try {
			Parameter pIdRemoteStore = new Parameter(RemoteStoresCostanti.PARAMETRO_REMOTE_STORE_ID, remoteStoreId + "");
			
			ServletUtils.addListElementIntoSession(this.request, this.session, RemoteStoresCostanti.OBJECT_NAME_REMOTE_STORES_KEYS, pIdRemoteStore);

			int idLista = Liste.REMOTE_STORE_KEY;
			int limit = ricerca.getPageSize(idLista);
			int offset = ricerca.getIndexIniziale(idLista);

			String filterRemoteStoreId = SearchUtils.getFilter(ricerca, idLista, Filtri.FILTRO_REMOTE_STORE_ID);

			// primo accesso
			if(StringUtils.isEmpty(filterRemoteStoreId)) {
				filterRemoteStoreId = remoteStoreId + "";
			}
			
			this.addFilterRemoteStoreId(filterRemoteStoreId, true);

			String filterRemoteStoreKid = SearchUtils.getFilter(ricerca, idLista, Filtri.FILTRO_REMOTE_STORE_KEY_KID);
			this.addFilterRemoteStoreKid(filterRemoteStoreKid);

			String filterRemoteStoreClientId = SearchUtils.getFilter(ricerca, idLista, Filtri.FILTRO_REMOTE_STORE_KEY_CLIENT_ID);
			this.addFilterRemoteStoreClientId(filterRemoteStoreClientId);

			String filterRemoteStoreOrganizzazione = SearchUtils.getFilter(ricerca, idLista, Filtri.FILTRO_REMOTE_STORE_KEY_ORGANIZZAZIONE);
			this.addFilterRemoteStoreOrganizzazione(filterRemoteStoreOrganizzazione);

			this.pd.setIndex(offset);
			this.pd.setPageSize(limit);
			this.pd.setNumEntries(ricerca.getNumEntries(idLista));
			this.pd.nascondiTextFilterAutomatico();

			// setto la barra del titolo
			ServletUtils.setPageDataTitle(this.pd, 
					new Parameter(RemoteStoresCostanti.LABEL_CACHE_PDND, RemoteStoresCostanti.SERVLET_NAME_REMOTE_STORES_KEYS_LIST));

			// Label colonne
			String[] labels = {
					RemoteStoresCostanti.LABEL_PARAMETRO_REMOTE_STORE_KEY_DATA_REGISTRAZIONE,
					RemoteStoresCostanti.LABEL_PARAMETRO_REMOTE_STORE_KEY_CONTENT_KEY,
					RemoteStoresCostanti.LABEL_PARAMETRO_REMOTE_STORE_KEY_CLIENT_ID,
					RemoteStoresCostanti.LABEL_PARAMETRO_REMOTE_STORE_KEY_ORGANIZATION_DETAILS
			};
			this.pd.setLabels(labels);

			// preparo i dati
			List<List<DataElement>> dati = new ArrayList<>();

			if (lista != null) {
				Iterator<RemoteStoreKeyEntry> it = lista.iterator();
				while (it.hasNext()) {
					List<DataElement> e = this.creaEntry(it, pIdRemoteStore);
					dati.add(e);
				}
			}

			this.pd.setDati(dati);
			// rimuovo il tasto add
			this.pd.setAddButton(false);
		} catch (Exception e) {
			this.log.error("Exception: " + e.getMessage(), e);
		}
	}


	private List<DataElement> creaEntry(Iterator<RemoteStoreKeyEntry> it, Parameter pIdRemoteStore) {
		RemoteStoreKeyEntry entry = it.next();
		List<DataElement> e = new ArrayList<>();

		// Data creazione
		SimpleDateFormat formatter = DateUtils.getDefaultDateTimeFormatter("yyyy-MM-dd HH:mm:ss.SSS");
		DataElement de = new DataElement();
		Parameter pId = new Parameter(RemoteStoresCostanti.PARAMETRO_REMOTE_STORE_KEY_ID, entry.getId()+"");
		de.setUrl(RemoteStoresCostanti.SERVLET_NAME_REMOTE_STORES_KEYS_CHANGE , pId, pIdRemoteStore);
		de.setValue(formatter.format(entry.getDataRegistrazione()));
		de.setIdToRemove(entry.getId() + "");
		de.setSize(this.core.getElenchiMenuIdentificativiLunghezzaMassima());
		e.add(de);
		
		// chiave pubblica

		de = new DataElement();
		if(entry.getContentKey() != null && isKidNotStartsWithClientId(entry.getKid())) {
			/** de.setValue(RemoteStoresCostanti.LABEL_DOWNLOAD.toLowerCase()); 
			    de.setToolTip(entry.getKid());*/
			de.setValue(entry.getKid());
			de.setToolTip(RemoteStoresCostanti.LABEL_DOWNLOAD.toLowerCase());
			
			de.setUrl(ArchiviCostanti.SERVLET_NAME_DOCUMENTI_EXPORT, 
					new Parameter(ArchiviCostanti.PARAMETRO_ARCHIVI_ALLEGATO_ID_ACCORDO, entry.getId()+""),
					new Parameter(ArchiviCostanti.PARAMETRO_ARCHIVI_ALLEGATO_TIPO_ACCORDO_TIPO_DOCUMENTO, ArchiviCostanti.PARAMETRO_VALORE_ARCHIVI_ALLEGATO_TIPO_DOCUMENTO_CHIAVE_PUBBLICA ),
					new Parameter(ArchiviCostanti.PARAMETRO_ARCHIVI_ALLEGATO_TIPO_ACCORDO, ArchiviCostanti.PARAMETRO_VALORE_ARCHIVI_ALLEGATO_TIPO_REMOTE_STORE_ENTRY));
			de.setDisabilitaAjaxStatus();
			
		}else {
			de.setValue(RemoteStoresCostanti.VALUE_PARAMETRO_NON_PRESENTE);
		}
		e.add(de);
		
		// client id
		de = new DataElement();
		if(entry.getClientId() != null) {
			de.setValue(entry.getClientId());
			de.setSize(this.core.getElenchiMenuIdentificativiLunghezzaMassima());
		}else {
			de.setValue(RemoteStoresCostanti.VALUE_PARAMETRO_NON_PRESENTE);
		}
		e.add(de);
		
		// dettagli organizzazione
		de = new DataElement();
		if(entry.getOrganizationName() != null) {
			StringBuilder sb = new StringBuilder();
			sb.append(entry.getOrganizationName()).append(" (").append(entry.getOrganizationExternalOrigin()).append(" ").append(entry.getOrganizationExternalId()).append(")");
			de.setValue(sb.toString());
			de.setToolTip(entry.getOrganizationCategory());
			de.setSize(this.core.getElenchiMenuIdentificativiLunghezzaMassima());
		}else {
			de.setValue(RemoteStoresCostanti.VALUE_PARAMETRO_NON_PRESENTE);
		}
		e.add(de);
		
		
		return e;
	}
	
	private boolean isKidNotStartsWithClientId(String kid) {
		return kid != null && !kid.startsWith(RemoteStoresCostanti.VALUE_PARAMETRO_REMOTE_STORE_KEY_KID_STARTS_WITH_CLIENT_ID);
	}
	
	
	
	private void addFilterRemoteStoreOrganizzazione(String filterRemoteStoreOrganizzazione) throws Exception {
		String label = RemoteStoresCostanti.LABEL_PARAMETRO_REMOTE_STORE_KEY_ORGANIZATION_DETAILS;
		this.pd.addTextFilter(Filtri.FILTRO_REMOTE_STORE_KEY_ORGANIZZAZIONE, label, filterRemoteStoreOrganizzazione, this.getSize());
	}
	
	private void addFilterRemoteStoreClientId(String filterRemoteStoreClientId) throws Exception {
		String label = RemoteStoresCostanti.LABEL_PARAMETRO_REMOTE_STORE_KEY_CLIENT_ID;
		this.pd.addTextFilter(Filtri.FILTRO_REMOTE_STORE_KEY_CLIENT_ID, label, filterRemoteStoreClientId, this.getSize());
	}
	
	private void addFilterRemoteStoreKid(String filterRemoteStoreKid) throws Exception {
		String label = RemoteStoresCostanti.LABEL_PARAMETRO_REMOTE_STORE_KEY_KID;
		this.pd.addTextFilter(Filtri.FILTRO_REMOTE_STORE_KEY_KID, label, filterRemoteStoreKid, this.getSize());
	}
	private void addFilterRemoteStoreId(String filterRemoteStoreId, boolean postback) throws Exception {
		// se e' presente solo un remote store allora inserisco un campo hidden altriment una select list
		List<RemoteStore> remoteStoresList = this.remoteStoresCore.remoteStoresList();
		
		if(remoteStoresList.size() > 1) {
			String label = RemoteStoresCostanti.LABEL_REMOTE_STORE;
			
			List<String> labels = new ArrayList<>();
			List<String> values = new ArrayList<>();
			
			for (RemoteStore remoteStore : remoteStoresList) {
				labels.add(remoteStore.getNome());
				values.add(remoteStore.getId() + "");
			}
			
			this.pd.addFilter(Filtri.FILTRO_REMOTE_STORE_ID, label, filterRemoteStoreId, values.toArray(new String[values.size()]), labels.toArray(new String[labels.size()]), postback, this.getSize());
			
		} else {
			this.pd.addHiddenFilter(Filtri.FILTRO_REMOTE_STORE_ID, filterRemoteStoreId, this.getSize());
		}
	}
	
	public List<DataElement> addRemoteStoreKeyToDati(long remoteStoreId, long id, RemoteStoreKeyEntry entry, List<DataElement> dati) {
		// le informazioni verranno visualizzate in modalita' noedit
		this.pd.disableEditMode();
		
		// data registrazione
		SimpleDateFormat formatter = DateUtils.getDefaultDateTimeFormatter("yyyy-MM-dd HH:mm:ss.SSS");
		DataElement de = new DataElement();
		de.setLabel(RemoteStoresCostanti.LABEL_PARAMETRO_REMOTE_STORE_KEY_DATA_REGISTRAZIONE);
		de.setValue(formatter.format(entry.getDataRegistrazione()));
		de.setType(DataElementType.TEXT_EDIT);
		dati.add(de);
		
		// identificativi
		de = new DataElement();
		de.setLabel(RemoteStoresCostanti.LABEL_PARAMETRO_REMOTE_STORE_ID);
		de.setValue(remoteStoreId +"");
		de.setType(DataElementType.HIDDEN);
		dati.add(de);
		
		de = new DataElement();
		de.setLabel(RemoteStoresCostanti.LABEL_PARAMETRO_REMOTE_STORE_KEY_ID);
		de.setValue(id +"");
		de.setType(DataElementType.HIDDEN);
		dati.add(de);
		
		// sottosezione chiave pubblica (visualizza solo se il kid non inizia per ClientId--
		if(this.isKidNotStartsWithClientId(entry.getKid())) {
			if(entry.getDataAggiornamento() != null || entry.getContentKey() != null) {
				de = new DataElement();
				de.setType(DataElementType.SUBTITLE);
				de.setLabel(RemoteStoresCostanti.LABEL_PARAMETRO_REMOTE_STORE_KEY_CONTENT_KEY);
				dati.add(de);
			}
			
			
			if(entry.getDataAggiornamento() != null) {
				de = new DataElement();
				de.setLabel(RemoteStoresCostanti.LABEL_PARAMETRO_REMOTE_STORE_KEY_DATA_AGGIORAMENTO);
				de.setValue(formatter.format(entry.getDataAggiornamento()));
				de.setType(DataElementType.TEXT_EDIT);
				dati.add(de);
			}
			
			de = new DataElement();
			de.setLabel(RemoteStoresCostanti.LABEL_PARAMETRO_REMOTE_STORE_KEY_KID);
			de.setValue(entry.getKid());
			de.setType(DataElementType.TEXT_EDIT);
			dati.add(de);
			
			if(entry.getContentKey() != null) {
				de = new DataElement();
				de.setLabel(RemoteStoresCostanti.LABEL_PARAMETRO_REMOTE_STORE_KEY_CONTENT_KEY);
				de.setValue(new String(entry.getContentKey()));
				de.setType(DataElementType.TEXT_AREA_NO_EDIT);
				dati.add(de);
				
				de = new DataElement();
				de.setLabel("");
				de.setValue(RemoteStoresCostanti.LABEL_DOWNLOAD.toLowerCase());
				de.setUrl(ArchiviCostanti.SERVLET_NAME_DOCUMENTI_EXPORT, 
						new Parameter(ArchiviCostanti.PARAMETRO_ARCHIVI_ALLEGATO_ID_ACCORDO, entry.getId()+""),
						new Parameter(ArchiviCostanti.PARAMETRO_ARCHIVI_ALLEGATO_TIPO_ACCORDO_TIPO_DOCUMENTO, ArchiviCostanti.PARAMETRO_VALORE_ARCHIVI_ALLEGATO_TIPO_DOCUMENTO_CHIAVE_PUBBLICA ),
						new Parameter(ArchiviCostanti.PARAMETRO_ARCHIVI_ALLEGATO_TIPO_ACCORDO, ArchiviCostanti.PARAMETRO_VALORE_ARCHIVI_ALLEGATO_TIPO_REMOTE_STORE_ENTRY));
				de.setDisabilitaAjaxStatus();
				de.setType(DataElementType.LINK);
				dati.add(de);
			}
		}
		
		// sottosezione informazioni client (visualizza se presente client id)
		if(entry.getClientId() != null) {
			de = new DataElement();
			de.setType(DataElementType.SUBTITLE);
			de.setLabel(RemoteStoresCostanti.LABEL_INFORMAZIONI_CLIENT);
			dati.add(de);
			
			if(entry.getClientDataAggiornamento() != null) {
				de = new DataElement();
				de.setLabel(RemoteStoresCostanti.LABEL_PARAMETRO_REMOTE_STORE_KEY_CLIENT_DATA_AGGIORNAMENTO);
				de.setValue(formatter.format(entry.getClientDataAggiornamento()));
				de.setType(DataElementType.TEXT_EDIT);
				dati.add(de);
			}
			
			de = new DataElement();
			de.setLabel(RemoteStoresCostanti.LABEL_PARAMETRO_REMOTE_STORE_KEY_CLIENT_ID);
			de.setValue(entry.getClientId());
			de.setType(DataElementType.TEXT_EDIT);
			dati.add(de);
			
			de = new DataElement();
			de.setLabel(RemoteStoresCostanti.LABEL_PARAMETRO_REMOTE_STORE_KEY_CLIENT_DETAILS);
			de.setValue(entry.getClientDetails());
			de.setType(DataElementType.TEXT_AREA_NO_EDIT);
			dati.add(de);
			
			de = new DataElement();
			de.setLabel(RemoteStoresCostanti.LABEL_PARAMETRO_REMOTE_STORE_KEY_ORGANIZATION_DETAILS);
			de.setValue(entry.getOrganizationDetails());
			de.setType(DataElementType.TEXT_AREA_NO_EDIT);
			dati.add(de);
		}
		
		return dati;
	}
}
