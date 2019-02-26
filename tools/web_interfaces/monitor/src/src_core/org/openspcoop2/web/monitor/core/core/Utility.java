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
package org.openspcoop2.web.monitor.core.core;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.MessageFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.openspcoop2.core.commons.CoreException;
import org.openspcoop2.core.commons.dao.DAOFactory;
import org.openspcoop2.core.commons.search.IdSoggetto;
import org.openspcoop2.core.commons.search.Soggetto;
import org.openspcoop2.core.config.Configurazione;
import org.openspcoop2.core.config.constants.PortaApplicativaSoggettiFruitori;
import org.openspcoop2.core.config.constants.PortaDelegataSoggettiErogatori;
import org.openspcoop2.core.config.constants.StatoFunzionalita;
import org.openspcoop2.core.id.IDServizio;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.protocol.engine.ProtocolFactoryManager;
import org.openspcoop2.protocol.sdk.IProtocolFactory;
import org.openspcoop2.protocol.sdk.ProtocolException;
import org.openspcoop2.protocol.utils.ProtocolUtils;
import org.openspcoop2.utils.resources.MapReader;
import org.openspcoop2.web.lib.users.dao.User;
import org.openspcoop2.web.monitor.core.bean.LoginBean;
import org.openspcoop2.web.monitor.core.bean.UserDetailsBean;
import org.openspcoop2.web.monitor.core.constants.TipologiaRicerca;
import org.openspcoop2.web.monitor.core.logger.LoggerManager;
import org.openspcoop2.web.monitor.core.utils.DynamicPdDBeanUtils;
import org.openspcoop2.web.monitor.core.utils.ParseUtility;
import org.slf4j.Logger;

/**
 * Utility
 * 
 * @author Pintori Giuliano (pintori@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 *
 */
public class Utility {

	private static final String REPORTISTICA_DIAGNOSTICI_FREQ_INVIO = "reportistica.diagnostici.freq_invio.";
	private static final String REPORTISTICA_DIAGNOSTICI_INTERVALLO_INTERESSE = "reportistica.diagnostici.intervallo_interesse.";
	private static final String REPORTISTICA_TRACCE_FREQ_INVIO = "reportistica.tracce.freq_invio.";
	private static final String REPORTISTICA_TRACCE_INTERVALLO_INTERESSE = "reportistica.tracce.intervallo_interesse.";
	private static final String STATUS_INTERVALLO_STORICO = "status.storico.";

	private static Logger log =  LoggerManager.getPddMonitorCoreLogger();

	private static ArrayList<String> frequenzaInvioDiagnostici = null;
	private static ArrayList<String> frequenzaInvioTracce = null;
	private static ArrayList<String> intervalloInteresseDiagnostici = null;
	private static ArrayList<String> intervalloInteresseTracce = null;
	private static ArrayList<String> statoSistemaStorico = null;

	//	private static LoginBean loginBean;

	public static void setLoginMBean(LoginBean loginBean) {
		//		Utility.loginBean = loginBean;
	}

	public void setLoginBean(LoginBean loginBean) {
		//		Utility.loginBean = loginBean;
	}

	public static LoginBean getLoginBean() {
		FacesContext fc = FacesContext.getCurrentInstance();
		if(fc!= null){
			ExternalContext ec = fc.getExternalContext();
			LoginBean lb = (LoginBean)ec.getSessionMap().get(org.openspcoop2.web.monitor.core.bean.AbstractLoginBean.LOGIN_BEAN_SESSION_ATTRIBUTE_NAME);

			return lb;
		}
		return null;
		//		return Utility.loginBean;
	}

	public static LoginBean getLoginBeanFromSession(HttpSession sessione) {
		if(sessione!= null){
			LoginBean lb = (LoginBean)sessione.getAttribute(org.openspcoop2.web.monitor.core.bean.AbstractLoginBean.LOGIN_BEAN_SESSION_ATTRIBUTE_NAME);
			return lb;
		}
		return null;
	}

	// public static String getSoggettoInGestione(){
	// if(getLoginBean()==null){
	// setLoginMBean((LoginBean)Utils.resolveExpression("#{loginBean}"));
	// }
	//
	// if(getLoginBean()==null){
	// return null;
	// }
	//
	// if(!Utility.getLoggedUser().isAdmin()){
	// return Utility.getLoggedUser().getSoggetto().getNome();
	// }else{
	// return Utility.loginBean.getSoggettoInGestione();
	// }
	// }



	public static ArrayList<String> getFrequenzaInvioDiagnostici() {
		if (Utility.frequenzaInvioDiagnostici != null)
			return Utility.frequenzaInvioDiagnostici;
		Utility.frequenzaInvioDiagnostici = Utility
				.loadAndOrderProps(Utility.REPORTISTICA_DIAGNOSTICI_FREQ_INVIO);

		return Utility.frequenzaInvioDiagnostici;
	}

	public static ArrayList<String> getFrequenzaInvioTracce() {
		if (Utility.frequenzaInvioTracce != null)
			return Utility.frequenzaInvioTracce;
		Utility.frequenzaInvioTracce = Utility
				.loadAndOrderProps(Utility.REPORTISTICA_TRACCE_FREQ_INVIO);
		return Utility.frequenzaInvioTracce;
	}

	public static ArrayList<String> getIntervalloInteresseDiagnostici() {

		if (Utility.intervalloInteresseDiagnostici != null)
			return Utility.intervalloInteresseDiagnostici;
		Utility.intervalloInteresseDiagnostici = Utility
				.loadAndOrderProps(Utility.REPORTISTICA_DIAGNOSTICI_INTERVALLO_INTERESSE);

		return Utility.intervalloInteresseDiagnostici;
	}

	public static ArrayList<String> getIntervalloInteresseTracce() {
		if (Utility.intervalloInteresseTracce != null)
			return Utility.intervalloInteresseTracce;
		Utility.intervalloInteresseTracce = Utility
				.loadAndOrderProps(Utility.REPORTISTICA_TRACCE_INTERVALLO_INTERESSE);

		return Utility.intervalloInteresseTracce;
	}

	public static ArrayList<String> getStatoSistemaStorico() {
		if (Utility.statoSistemaStorico != null)
			return Utility.statoSistemaStorico;
		Utility.statoSistemaStorico = Utility
				.loadAndOrderProps(Utility.STATUS_INTERVALLO_STORICO);
		return Utility.statoSistemaStorico;
	}

	private static ArrayList<String> loadAndOrderProps(String prefix) {
		try {

			PddMonitorProperties govwayMonitorProperties = PddMonitorProperties.getInstance(Utility.log);


			Enumeration<?> keys = govwayMonitorProperties.keys();
			HashMap<String, String> matched = new HashMap<String, String>();
			ArrayList<String> matchedKeys = new ArrayList<String>();
			while (keys.hasMoreElements()) {
				String key = (String) keys.nextElement();
				if (key.startsWith(prefix)) {
					// recupero le properties ke matchano e rimuovo il prefisso
					// aggiungo 0 davanti per poter fare il sort
					String mk = "0" + (key.substring(prefix.length()));
					String val = govwayMonitorProperties.getProperty(key,false,false);
					matched.put(mk, val);
					matchedKeys.add(mk);
				}
			}
			Collections.sort(matchedKeys);

			ArrayList<String> ordered = new ArrayList<String>();
			for (String key : matchedKeys) {
				String val = matched.get(key);
				ordered.add(val);
			}

			return ordered;
		} catch (Exception e) {
			Utility.log
			.info("Errore durante il caricamento frequenza invio diagnostici, "
					+ e.getMessage());
			Utility.log.debug(e.getMessage(), e);
			return null;
		}
	}

	public static int getFreqInvioValueForDiagnostici(String label) {
		return Utility.getValueFromLabel(
				Utility.REPORTISTICA_DIAGNOSTICI_FREQ_INVIO, label);
	}

	public static int getIntervalloInteresseValueDiagnostici(String label) {
		return Utility.getValueFromLabel(
				Utility.REPORTISTICA_DIAGNOSTICI_INTERVALLO_INTERESSE, label);
	}

	public static int getFreqInvioValueForTracce(String label) {
		return Utility.getValueFromLabel(
				Utility.REPORTISTICA_TRACCE_FREQ_INVIO, label);
	}

	public static int getIntervalloInteresseValueTracce(String label) {
		return Utility.getValueFromLabel(
				Utility.REPORTISTICA_TRACCE_INTERVALLO_INTERESSE, label);
	}

	public static String getFreqInvioLabelForDiagnistici(int value) {
		return Utility.getLabelFromValue(
				Utility.REPORTISTICA_DIAGNOSTICI_FREQ_INVIO, value);
	}

	public static String getFreqInvioLabelForTracce(int value) {
		return Utility.getLabelFromValue(
				Utility.REPORTISTICA_TRACCE_FREQ_INVIO, value);
	}

	public static String getPeriodoInteresseLabelForDiagnistici(int value) {
		return Utility.getLabelFromValue(
				Utility.REPORTISTICA_DIAGNOSTICI_INTERVALLO_INTERESSE, value);
	}

	public static String getPeriodoInteresseLabelForTracce(int value) {
		return Utility.getLabelFromValue(
				Utility.REPORTISTICA_TRACCE_INTERVALLO_INTERESSE, value);
	}

	private static int getValueFromLabel(String prefix, String label) {
		try {
			if (label == null || prefix == null)
				return 0;

			PddMonitorProperties govwayMonitorProperties = PddMonitorProperties.getInstance(Utility.log);

			Enumeration<?> keys = govwayMonitorProperties.keys();
			while (keys.hasMoreElements()) {
				String key = (String) keys.nextElement();

				if (!key.startsWith(prefix))
					continue;
				// controllo il label
				String prop = govwayMonitorProperties.getProperty(key,false,false);
				if (label.equals(prop)) {
					// ho trovato l'elemento che cercavo
					// adesso ricavo l'integer dalla key
					String val = (key.substring(prefix.length()));
					try {
						return Integer.parseInt(val);
					} catch (Exception e) {
						Utility.log.error(e.getMessage(), e);
					}
				}
			}
		} catch (Exception e) {
			Utility.log.error(e.getMessage(), e);
		}

		return 0;
	}

	private static String getLabelFromValue(String prefix, int value) {
		try {

			PddMonitorProperties govwayMonitorProperties = PddMonitorProperties.getInstance(Utility.log);

			String key = prefix + "" + value;
			String label = govwayMonitorProperties.getProperty(key,false,false);

			if (label == null)
				return "";

			return label;
		} catch (Exception e) {
			Utility.log.error(e.getMessage(), e);
		}

		return "";
	}

	public static UserDetailsBean getLoggedUser() {
		LoginBean lb = getLoginBean();

		if(lb!= null && lb.isLoggedIn()){
			return lb.getLoggedUser();
		}
		return null;
	}

	public static User getLoggedUtente() {
		LoginBean lb = getLoginBean();

		if(lb!= null && lb.isLoggedIn()){
			return lb.getUtente();
		}

		return null;
	}

	public static String getLoggedUtenteModalita() {
		LoginBean lb = getLoginBean();

		if(lb!= null && lb.isLoggedIn()){
			return lb.getModalita();
		}

		return null;
	}
	
	public static String getLoggedUtenteSoggettoPddMonitor() {
		LoginBean lb = getLoginBean();

		if(lb!= null && lb.isLoggedIn()){
			return lb.getSoggettoPddMonitor();
		}

		return null;
	}

	public static Configurazione getConfigurazioneGenerale() {
		LoginBean lb = getLoginBean();

		if(lb!= null && lb.isLoggedIn()){
			return lb.getConfigurazioneGenerale();
		}

		return null;
	}

	public static boolean isFiltroDominioAbilitato() {
		if(isMultitenantAbilitato()) {
			User utente = Utility.getLoggedUtente();
			
			String soggettoOperativoSelezionato = utente.getSoggettoSelezionatoPddMonitor();
			// utente ha selezionato un soggetto
			if(soggettoOperativoSelezionato != null) {
				return true;
			}
			
			// uso il filtro solamente se cmq ho piu' di un soggetto locale
			return getLoginBean().isShowFiltroSoggettoLocale();
		}
		else {
			return false;
		}
	}
	
	public static boolean isMultitenantAbilitato() {
		LoginBean lb = getLoginBean();

		if(lb!= null && lb.isLoggedIn()){
			Configurazione configurazioneGenerale = lb.getConfigurazioneGenerale();

			if(configurazioneGenerale.getMultitenant() != null) {
				if(configurazioneGenerale.getMultitenant().getStato()!=null) {
					return StatoFunzionalita.ABILITATO.equals(configurazioneGenerale.getMultitenant().getStato());
				}
			}
		}

		return false;
	}
	
	public static PortaDelegataSoggettiErogatori getMultitenantAbilitato_fruizione_sceltaSoggettiErogatori() {
		LoginBean lb = getLoginBean();

		if(lb!= null && lb.isLoggedIn()){
			Configurazione configurazioneGenerale = lb.getConfigurazioneGenerale();

			if(configurazioneGenerale.getMultitenant() != null) {
				return configurazioneGenerale.getMultitenant().getFruizioneSceltaSoggettiErogatori();
			}
		}

		return null;
	}
	
	public static PortaApplicativaSoggettiFruitori getMultitenantAbilitato_erogazione_sceltaSoggettiFruitori() {
		LoginBean lb = getLoginBean();

		if(lb!= null && lb.isLoggedIn()){
			Configurazione configurazioneGenerale = lb.getConfigurazioneGenerale();

			if(configurazioneGenerale.getMultitenant() != null) {
				return configurazioneGenerale.getMultitenant().getErogazioneSceltaSoggettiFruitori();
			}
		}

		return null;
	}
	
	public static ConfigurazioneSoggettiVisualizzatiSearchForm getMultitenantAbilitato_soggettiConfig(TipologiaRicerca ricercaParam) {
		boolean multiTenant = Utility.isMultitenantAbilitato();
		boolean includiSoloOperativi = false;
		boolean includiSoloEsterni = false;
		boolean escludiSoggettoSelezionato = false;
		TipologiaRicerca ricerca = ricercaParam;
		if(ricerca==null) {
			ricerca = TipologiaRicerca.all;
		}
		switch (ricerca) {
		case all:
			break;
		case uscita:
			if(multiTenant) {
				PortaDelegataSoggettiErogatori scelta = Utility.getMultitenantAbilitato_fruizione_sceltaSoggettiErogatori();
				if(scelta==null) {
					scelta = PortaDelegataSoggettiErogatori.SOGGETTI_ESTERNI;
				}
				switch (scelta) {
				case SOGGETTI_ESTERNI:
					includiSoloEsterni = true;
					break;
				case ESCLUDI_SOGGETTO_FRUITORE:
					escludiSoggettoSelezionato = true;
					break;
				case TUTTI:
					break;
				}
			}
			else {
				includiSoloEsterni = true;
			}
			break;
		case ingresso:
			if(multiTenant) {
				PortaApplicativaSoggettiFruitori scelta = Utility.getMultitenantAbilitato_erogazione_sceltaSoggettiFruitori();
				if(scelta==null) {
					scelta = PortaApplicativaSoggettiFruitori.SOGGETTI_ESTERNI;
				}
				switch (scelta) {
				case SOGGETTI_ESTERNI:
					includiSoloEsterni = true;
					break;
				case ESCLUDI_SOGGETTO_EROGATORE:
					escludiSoggettoSelezionato = true;
					break;
				case TUTTI:
					break;
				}
			}
			else {
				includiSoloEsterni = true;
			}
			break;
		}
		
		ConfigurazioneSoggettiVisualizzatiSearchForm config = new ConfigurazioneSoggettiVisualizzatiSearchForm();
		config.setIncludiSoloOperativi(includiSoloOperativi);
		config.setIncludiSoloEsterni(includiSoloEsterni);
		config.setEscludiSoggettoSelezionato(escludiSoggettoSelezionato);
		return config;
	}

	public static String fileSizeConverter(Number bytes) {
		MessageFormat mf = new MessageFormat("{0,number,#.##}");
		Double len = null;
		String res = "";

		// il valore e' in byte
		len = bytes.doubleValue();
		long d = Math.round(len / 1024);
		//Originale e funzionante :)
		//		if (d <= 1) {
		//			// byte
		//			Object[] objs = { len };
		//			res = mf.format(objs);
		//			res += " B";
		//		} else if (d > 1 && d < 1000) {
		//			// kilo byte
		//			Object[] objs = { len / 1024 };
		//			res = mf.format(objs);
		//			res += " KB";
		//		} else {
		//			// mega byte
		//			Object[] objs = { len / 1048576 };
		//			res = mf.format(objs);
		//			res += " MB";
		//		}

		if (d <= 1) {
			// byte
			Object[] objs = { len };
			res = mf.format(objs);
			res += " B";
		} else if (d > 1 && d < 1000) {
			// kilo byte
			Object[] objs = { len / 1024 };
			res = mf.format(objs);
			res += " KB";
		} else  if (d >= 1000 && d < 1000000){
			// mega byte
			Object[] objs = { len / 1048576 };
			res = mf.format(objs);
			res += " MB";
		} else {
			// giga byte
			Object[] objs = { len / 1073741824 };
			res = mf.format(objs);
			res += " GB";
		}

		return res;
	}

	public static String numberConverter(Number bytes) {
		//MessageFormat mf = new MessageFormat("{0,number,#,###,###,##0}");
		NumberFormat nf = NumberFormat.getInstance(Locale.ITALIAN);
		//		Double len = bytes.doubleValue();
		String res = "";
		//		Object[] objs = { len };
		res = nf.format(bytes.longValue());

		return res;
	}


	/**
	 * Effettua il parsing di una stringa del tipo tipoSoggetto/nomeSoggetto
	 * 
	 * @param tipoNomeSoggetto
	 * @return il nome del soggetto (nomeSoggetto) se la stringa contiene il
	 *         carattere separatore, altrimenti l'intera stringa
	 */
	public static String parseNomeSoggetto(String tipoNomeSoggetto) {
		return ParseUtility.parseNomeSoggetto(tipoNomeSoggetto);
	}

	/**
	 * Effettua il parsing di una stringa del tipo tipoSoggetto/nomeSoggetto
	 * 
	 * @param tipoNomeSoggetto
	 * @return il tipo del soggetto (tipoSoggetto) se la stringa contiene il
	 *         carattere separatore, altrimenti l'intera stringa
	 */
	public static String parseTipoSoggetto(String tipoNomeSoggetto) {
		return ParseUtility.parseTipoSoggetto(tipoNomeSoggetto);
	}

	public static org.openspcoop2.core.commons.search.Soggetto getSoggetto(IdSoggetto idSog) {
		LoginBean lb = getLoginBean();

		if(lb!= null && lb.isLoggedIn()){
			return lb.getSoggetto(idSog);
		}
		else if(lb==null){
			// is null quando si accede via http get service
			lb = new LoginBean(true);
			return lb.getSoggetto(idSog);
		}

		return null;
	}

	public static IDServizio parseSoggettoServizio(String input){
		return ParseUtility.parseSoggettoServizio(input);
	}

	public static String convertToSoggettoServizio(IDServizio idServizio){
		return ParseUtility.convertToSoggettoServizio(idServizio);
	}

	public static IDServizio parseServizioSoggetto(String input) throws CoreException{
		return ParseUtility.parseServizioSoggetto(input);
	}

	public static String convertToServizioSoggetto(IDServizio idServizio) throws CoreException{
		return ParseUtility.convertToServizioSoggetto(idServizio);
	}

	public static String getIdentificativoPorta(String tipoSoggetto,String nomeSoggetto) throws CoreException{
		// Recupero identificativoPorta se è stato selezionato un soggetto locale (o un servizio)
		String idPorta = null;
		try {	
			Logger log =  LoggerManager.getPddMonitorSqlLogger();
			// [TODO] controllare se il tipo di project info e' corretto
			org.openspcoop2.core.commons.search.utils.ProjectInfo prInfo = org.openspcoop2.core.commons.search.utils.ProjectInfo.getInstance();
			org.openspcoop2.core.commons.search.dao.IServiceManager sm =
					(org.openspcoop2.core.commons.search.dao.IServiceManager) DAOFactory.getInstance(log).getServiceManager(prInfo,log);
			IdSoggetto idSog = new IdSoggetto();
			idSog.setTipo(tipoSoggetto);
			idSog.setNome(nomeSoggetto);
			org.openspcoop2.core.commons.search.Soggetto soggetto = sm.getSoggettoServiceSearch().get(idSog);
			idPorta = soggetto.getIdentificativoPorta();
		} catch (Exception e) {
			throw new CoreException(
					"Si e' verificato un errore durante il recupero del soggetto dal registro: tipoSoggetto["
							+ tipoSoggetto
							+ "] nomeSoggetto["
							+ nomeSoggetto + "]");
		}
		return idPorta;
	}

	public static void copy(InputStream in, OutputStream out) 
			throws IOException {

		// do not allow other threads to read from the
		// input or write to the output while copying is
		// taking place

		synchronized (in) {
			synchronized (out) {

				byte[] buffer = new byte[256];
				while (true) {
					int bytesRead = in.read(buffer);
					if (bytesRead == -1) break;
					out.write(buffer, 0, bytesRead);
				}
			}
		}
	}

	public static List<String> getListaProtocolli(User utente, List<Soggetto> listaSoggettiGestione, ProtocolFactoryManager pfManager,	MapReader<String, IProtocolFactory<?>> protocolFactories) throws ProtocolException {
		List<String> listaNomiProtocolli = new  ArrayList<String>();

		if(listaSoggettiGestione != null && listaSoggettiGestione.size() > 0){
			List<String> tipiSoggetti = new ArrayList<String>();
			for (Soggetto soggetto : listaSoggettiGestione) {
				String tipoSoggetto = soggetto.getTipoSoggetto();

				if(!tipiSoggetti.contains(tipoSoggetto))
					tipiSoggetti.add(tipoSoggetto); 
			}

			for (String tipo : tipiSoggetti) {
				String protocolBySubjectType = pfManager.getProtocolByOrganizationType(tipo);
				if(!listaNomiProtocolli.contains(protocolBySubjectType))
					listaNomiProtocolli.add(protocolBySubjectType);
			}

		} else {
			if(utente.getProtocolliSupportati() !=null) {
				for (String protocolKey : utente.getProtocolliSupportati()) {
					if(!listaNomiProtocolli.contains(protocolKey))
						listaNomiProtocolli.add(protocolKey);
				}
			}
			else {
				// Tutti i protocolli
				Enumeration<String> keys = protocolFactories.keys();
				while (keys.hasMoreElements()) {
					String protocolKey = (String) keys.nextElement();
					if(!listaNomiProtocolli.contains(protocolKey))
						listaNomiProtocolli.add(protocolKey);
				}
			}
		}

		return ProtocolUtils.orderProtocolli(listaNomiProtocolli);
	}
	public static List<Soggetto> getSoggettiOperativiAssociatiAlProfilo(UserDetailsBean u, String profiloSelezionato) throws Exception {
		List<Soggetto> soggetti = new ArrayList<Soggetto>();
		
		if(u.getUtenteSoggettoProtocolliMap().containsKey(profiloSelezionato)) {
			for (IDSoggetto idSog : u.getUtenteSoggettoProtocolliMap().get(profiloSelezionato)) {
				IdSoggetto idsog2 = new IdSoggetto();
				idsog2.setNome(idSog.getNome());
				idsog2.setTipo(idSog.getTipo());
				Soggetto soggetto = Utility.getSoggetto(idsog2);
				soggetti.add(soggetto);
			}
		}
			
		return soggetti;
	}
	
	public static boolean isTipoSoggettoCompatibileConProtocollo(String tipoSoggetto, String tipoProtocollo)  throws Exception{
		return DynamicPdDBeanUtils.getInstance(log).isTipoSoggettoCompatibileConProtocollo(tipoSoggetto, tipoProtocollo);
	}
	
	public static List<Soggetto> getSoggettiGestione(User u, String tipoNomeSoggettoLocale) {
		List<Soggetto> soggetti = new ArrayList<Soggetto>();
		// se il soggetto locale e' specificato allora ritorno solo quello
		if (StringUtils.isNotEmpty(tipoNomeSoggettoLocale)) {

			// nomi.add(this.soggettoLocale);
			String tipo = Utility
					.parseTipoSoggetto(tipoNomeSoggettoLocale);
			String nome = Utility
					.parseNomeSoggetto(tipoNomeSoggettoLocale);

			for (IDSoggetto idSog : u.getSoggetti()) {
				if (idSog.getTipo().equals(tipo)
						&& idSog.getNome().equals(nome)) {
					IdSoggetto idsog2 = new IdSoggetto();
					//					idsog2.setId(idSog.getId());
					idsog2.setNome(idSog.getNome());
					idsog2.setTipo(idSog.getTipo());
					Soggetto soggetto = Utility.getSoggetto(idsog2);
					soggetti.add(soggetto);
					break;
				}
			}

			return soggetti;
		} else {
			List<String> checkUnique = new ArrayList<String>();
			for (IDSoggetto idSog : u.getSoggetti()) {

				String tipoNome = idSog.getTipo()+"/"+idSog.getNome();
				if(checkUnique.contains(tipoNome)==false){
					IdSoggetto idsog2 = new IdSoggetto();
					//					idsog2.setId(idSog.getId());
					idsog2.setNome(idSog.getNome());
					idsog2.setTipo(idSog.getTipo());

					Soggetto s = Utility.getSoggetto(idsog2);
					soggetti.add(s);	

					checkUnique.add(tipoNome);
				}

			}
			return soggetti;
		}
	}
	
	public static List<String> getProtocolli(User utente) throws Exception {
		return getProtocolli(utente, false);
	}

	public static List<String> getProtocolli(User utente, boolean ignoreProtocolloSelezionato) throws Exception {
		ProtocolFactoryManager pfManager = ProtocolFactoryManager.getInstance();
		MapReader<String,IProtocolFactory<?>> protocolFactories = pfManager.getProtocolFactories();	
		return getProtocolli(utente, pfManager, protocolFactories, ignoreProtocolloSelezionato);
	}
	public static List<String> getProtocolli(User utente, ProtocolFactoryManager pfManager, MapReader<String, IProtocolFactory<?>> protocolFactories) throws Exception {
		return getProtocolli(utente, pfManager, protocolFactories, false);
	}
	public static List<String> getProtocolli(User utente, ProtocolFactoryManager pfManager, MapReader<String, IProtocolFactory<?>> protocolFactories, boolean ignoreProtocolloSelezionato) throws  Exception {
		return getProtocolli(utente, pfManager, protocolFactories, ignoreProtocolloSelezionato, false);
	}
	public static List<String> getProtocolli(User utente, ProtocolFactoryManager pfManager, MapReader<String, IProtocolFactory<?>> protocolFactories, boolean ignoreProtocolloSelezionato, 
			boolean consideraProtocolliCompatibiliSoggettoSelezionato) throws Exception {
		List<String> protocolliList = new ArrayList<String>();

		if(!ignoreProtocolloSelezionato) {
			if(utente.getProtocolloSelezionatoPddMonitor()!=null) {
				protocolliList.add(utente.getProtocolloSelezionatoPddMonitor());
				return protocolliList;
			}
			else if(consideraProtocolliCompatibiliSoggettoSelezionato && utente.getProtocolloSelezionatoPddMonitor()!=null && !"".equals(utente.getProtocolloSelezionatoPddMonitor())) {
				String tipoSoggetto = utente.getProtocolloSelezionatoPddMonitor().split("/")[0];
				String protocollo = pfManager.getProtocolByOrganizationType(tipoSoggetto);
				protocolliList.add(protocollo);
				return protocolliList;
			}
		}

		if(utente.getProtocolliSupportati()!=null && utente.getProtocolliSupportati().size()>0) {
			return ProtocolUtils.orderProtocolli(utente.getProtocolliSupportati());
		}

		return getProtocolli(protocolFactories); // ordinato dentro il metodo

	}

	public static List<String> getProtocolli(MapReader<String, IProtocolFactory<?>> protocolFactories){

		List<String> protocolliList = new ArrayList<String>();

		Enumeration<String> protocolli = protocolFactories.keys();
		while (protocolli.hasMoreElements()) {

			String protocollo = protocolli.nextElement();
			protocolliList.add(protocollo);
		}

		return ProtocolUtils.orderProtocolli(protocolliList);
	}
	
	public static String normalizeLabel(String label, int maxWidth) {
		return normalizeLabel(label, maxWidth, false,false);
	}
	
	public static String normalizeLabel(String label, int maxWidth, boolean multiline, boolean soloTestoPrimaLinea) {
		if(label.length() > maxWidth) {
			if(multiline) {
				StringBuilder sb = new StringBuilder();
				
				int startToken = 0;
				int endToken = startToken + maxWidth;
				
				if(!soloTestoPrimaLinea) {
					sb.append("<p>");
					sb.append(label.substring(startToken, endToken)).append("-");
					sb.append("</p>");
					do {
						startToken += maxWidth;
						endToken = startToken + maxWidth;
						if(endToken >= label.length()) {
							endToken = label.length();
						}
						
						if(startToken < label.length()) {
							sb.append("<p>");
							
							sb.append(label.substring(startToken, endToken));
							
							if(endToken < label.length())
								sb.append("-");
							
							sb.append("</p>");
						}
					} while(endToken < label.length());
				} else {
					sb.append(label.substring(startToken, endToken)).append("-");
				}
								
				return sb.toString();
			} else {
				return label.substring(0, maxWidth - 3) + "...";
			}
		}
		
		return label;
	}
}
