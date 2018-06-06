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
import org.openspcoop2.core.id.IDServizio;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.protocol.engine.ProtocolFactoryManager;
import org.openspcoop2.protocol.sdk.IProtocolFactory;
import org.openspcoop2.protocol.sdk.ProtocolException;
import org.openspcoop2.utils.resources.MapReader;
import org.openspcoop2.web.lib.users.dao.User;
import org.openspcoop2.web.monitor.core.bean.LoginBean;
import org.openspcoop2.web.monitor.core.bean.UserDetailsBean;
import org.openspcoop2.web.monitor.core.logger.LoggerManager;
import org.openspcoop2.web.monitor.core.utils.ParseUtility;
import org.slf4j.Logger;

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

			PddMonitorProperties pddMonitorProperties = PddMonitorProperties.getInstance(Utility.log);


			Enumeration<?> keys = pddMonitorProperties.keys();
			HashMap<String, String> matched = new HashMap<String, String>();
			ArrayList<String> matchedKeys = new ArrayList<String>();
			while (keys.hasMoreElements()) {
				String key = (String) keys.nextElement();
				if (key.startsWith(prefix)) {
					// recupero le properties ke matchano e rimuovo il prefisso
					// aggiungo 0 davanti per poter fare il sort
					String mk = "0" + (key.substring(prefix.length()));
					String val = pddMonitorProperties.getProperty(key,false,false);
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

			PddMonitorProperties pddMonitorProperties = PddMonitorProperties.getInstance(Utility.log);

			Enumeration<?> keys = pddMonitorProperties.keys();
			while (keys.hasMoreElements()) {
				String key = (String) keys.nextElement();

				if (!key.startsWith(prefix))
					continue;
				// controllo il label
				String prop = pddMonitorProperties.getProperty(key,false,false);
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

			PddMonitorProperties pddMonitorProperties = PddMonitorProperties.getInstance(Utility.log);

			String key = prefix + "" + value;
			String label = pddMonitorProperties.getProperty(key,false,false);

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
	
	public static List<String> getListaProtocolli(List<Soggetto> listaSoggettiGestione, ProtocolFactoryManager pfManager,	MapReader<String, IProtocolFactory<?>> protocolFactories) throws ProtocolException {
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
			// Tutti i protocolli
			Enumeration<String> keys = protocolFactories.keys();
			while (keys.hasMoreElements()) {
				String protocolKey = (String) keys.nextElement();
				if(!listaNomiProtocolli.contains(protocolKey))
					listaNomiProtocolli.add(protocolKey);
			}
		}
		return listaNomiProtocolli;
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
}
