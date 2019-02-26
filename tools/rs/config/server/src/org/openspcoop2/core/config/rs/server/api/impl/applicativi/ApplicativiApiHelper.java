package org.openspcoop2.core.config.rs.server.api.impl.applicativi;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.stream.Collectors;

import org.openspcoop2.core.config.Credenziali;
import org.openspcoop2.core.config.InvocazioneCredenziali;
import org.openspcoop2.core.config.InvocazionePorta;
import org.openspcoop2.core.config.InvocazionePortaGestioneErrore;
import org.openspcoop2.core.config.InvocazioneServizio;
import org.openspcoop2.core.config.PortaApplicativa;
import org.openspcoop2.core.config.PortaApplicativaServizioApplicativo;
import org.openspcoop2.core.config.PortaDelegata;
import org.openspcoop2.core.config.PortaDelegataServizioApplicativo;
import org.openspcoop2.core.config.RispostaAsincrona;
import org.openspcoop2.core.config.Ruolo;
import org.openspcoop2.core.config.ServizioApplicativo;
import org.openspcoop2.core.config.ServizioApplicativoRuoli;
import org.openspcoop2.core.config.constants.CostantiConfigurazione;
import org.openspcoop2.core.config.constants.CredenzialeTipo;
import org.openspcoop2.core.config.constants.FaultIntegrazioneTipo;
import org.openspcoop2.core.config.constants.InvocazioneServizioTipoAutenticazione;
import org.openspcoop2.core.config.constants.StatoFunzionalita;
import org.openspcoop2.core.config.constants.TipologiaErogazione;
import org.openspcoop2.core.config.constants.TipologiaFruizione;
import org.openspcoop2.core.config.driver.DriverConfigurazioneException;
import org.openspcoop2.core.config.driver.DriverConfigurazioneNotFound;
import org.openspcoop2.core.config.driver.FiltroRicercaPorteDelegate;
import org.openspcoop2.core.config.rs.server.api.impl.HttpRequestWrapper;
import org.openspcoop2.core.config.rs.server.api.impl.Helper;
import org.openspcoop2.core.config.rs.server.config.ServerProperties;
import org.openspcoop2.core.config.rs.server.model.Applicativo;
import org.openspcoop2.core.config.rs.server.model.ApplicativoItem;
import org.openspcoop2.core.config.rs.server.model.AuthenticationHttpBasic;
import org.openspcoop2.core.config.rs.server.model.AuthenticationHttps;
import org.openspcoop2.core.config.rs.server.model.AuthenticationPrincipal;
import org.openspcoop2.core.config.rs.server.model.ProfiloEnum;
import org.openspcoop2.core.id.IDPortaDelegata;
import org.openspcoop2.core.id.IDServizioApplicativo;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.core.registry.Soggetto;
import org.openspcoop2.core.registry.constants.RuoloContesto;
import org.openspcoop2.core.registry.constants.RuoloTipologia;
import org.openspcoop2.core.registry.driver.DriverRegistroServiziException;
import org.openspcoop2.core.registry.driver.DriverRegistroServiziNotFound;
import org.openspcoop2.core.registry.driver.FiltroRicercaRuoli;
import org.openspcoop2.protocol.engine.ProtocolFactoryManager;
import org.openspcoop2.protocol.sdk.ProtocolException;
import org.openspcoop2.utils.UtilsException;
import org.openspcoop2.utils.regexp.RegularExpressionEngine;
import org.openspcoop2.utils.service.fault.jaxrs.FaultCode;
import org.openspcoop2.web.ctrlstat.core.ControlStationCore;
import org.openspcoop2.web.ctrlstat.servlet.connettori.ConnettoriCostanti;
import org.openspcoop2.web.ctrlstat.servlet.pd.PorteDelegateCore;
import org.openspcoop2.web.ctrlstat.servlet.sa.ServiziApplicativiCore;
import org.openspcoop2.web.ctrlstat.servlet.sa.ServiziApplicativiCostanti;
import org.openspcoop2.web.ctrlstat.servlet.soggetti.SoggettiCore;
import org.openspcoop2.web.lib.mvc.TipoOperazione;

public class ApplicativiApiHelper {
	

	public static void overrideSAParameters(HttpRequestWrapper wrap, ServizioApplicativo sa) {
		Credenziali credenziali = sa.getInvocazionePorta().getCredenziali(0);
		
		wrap.overrideParameter(ServiziApplicativiCostanti.PARAMETRO_SERVIZI_APPLICATIVI_NOME, sa.getNome());
		wrap.overrideParameter(ServiziApplicativiCostanti.PARAMETRO_SERVIZI_APPLICATIVI_PROVIDER, sa.getIdSoggetto());
		wrap.overrideParameter(ServiziApplicativiCostanti.PARAMETRO_SERVIZI_APPLICATIVI_FAULT, ServiziApplicativiCostanti.SERVIZI_APPLICATIVI_FAULT_SOAP);
		wrap.overrideParameter(ConnettoriCostanti.PARAMETRO_CREDENZIALI_TIPO_AUTENTICAZIONE, credenziali.getTipo().toString());
		wrap.overrideParameter(ConnettoriCostanti.PARAMETRO_CREDENZIALI_AUTENTICAZIONE_USERNAME, credenziali.getUser());
		wrap.overrideParameter(ConnettoriCostanti.PARAMETRO_CREDENZIALI_AUTENTICAZIONE_PASSWORD, credenziali.getPassword());
		wrap.overrideParameter(ConnettoriCostanti.PARAMETRO_CREDENZIALI_AUTENTICAZIONE_SUBJECT, credenziali.getSubject());
		wrap.overrideParameter(ConnettoriCostanti.PARAMETRO_CREDENZIALI_AUTENTICAZIONE_PRINCIPAL, credenziali.getUser());
	}
    
	
	public static ServizioApplicativo applicativoToServizioApplicativo(
			Applicativo applicativo,
			String tipo_protocollo,
			String soggetto,
			ControlStationCore stationCore) throws UtilsException, Exception {

		ServerProperties serverProperties = ServerProperties.getInstance();
	
		
		ControlStationCore core = new ControlStationCore(true, serverProperties.getConfDirectory(),tipo_protocollo); 
		SoggettiCore soggettiCore = new SoggettiCore(core);	

		String tipo_soggetto = ProtocolFactoryManager.getInstance().getDefaultOrganizationTypes().get(tipo_protocollo);
		IDSoggetto idSoggetto = new IDSoggetto(tipo_soggetto,soggetto);
		Soggetto soggettoRegistro = soggettiCore.getSoggettoRegistro(idSoggetto);
		
	
		//soggettoRegistro.get
	    ServizioApplicativo sa = new ServizioApplicativo();
	
	    sa.setNome(applicativo.getNome());
	    sa.setTipologiaFruizione(TipologiaFruizione.NORMALE.getValue());
	    sa.setTipologiaErogazione(TipologiaErogazione.DISABILITATO.getValue());	
	
	    //Inseriamo il soggetto del registro locale
	    sa.setIdSoggetto(soggettoRegistro.getId());
	    sa.setNomeSoggettoProprietario(soggettoRegistro.getNome());
	    sa.setTipoSoggettoProprietario(soggettoRegistro.getTipo());	    
	    
	    // *** risposta asinc ***
		InvocazioneCredenziali credenzialiInvocazione = new InvocazioneCredenziali();
		credenzialiInvocazione.setUser("");
		credenzialiInvocazione.setPassword("");
		
		RispostaAsincrona rispostaAsinc = new RispostaAsincrona();
		rispostaAsinc.setAutenticazione(InvocazioneServizioTipoAutenticazione.NONE);
		rispostaAsinc.setCredenziali(credenzialiInvocazione);
		rispostaAsinc.setGetMessage(CostantiConfigurazione.DISABILITATO);
	
		sa.setRispostaAsincrona(rispostaAsinc);
		
		InvocazioneServizio invServizio = new InvocazioneServizio();
		invServizio.setAutenticazione(InvocazioneServizioTipoAutenticazione.NONE);
		invServizio.setCredenziali(credenzialiInvocazione);
		invServizio.setGetMessage(CostantiConfigurazione.DISABILITATO);
		
		sa.setInvocazioneServizio(invServizio);
		
		// *** Invocazione Porta ***
		InvocazionePorta invocazionePorta = new InvocazionePorta();
		Credenziali credenziali = credenzialiFromAuth(applicativo.getCredenziali(), Helper.tipoAuthSAFromModalita.get(applicativo.getModalitaAccesso().toString()));

		invocazionePorta.addCredenziali(credenziali);
		
	    //Imposto i ruoli
		FiltroRicercaRuoli filtroRuoli = new FiltroRicercaRuoli();
		filtroRuoli.setContesto(RuoloContesto.QUALSIASI); // gli applicativi possono essere usati anche nelle erogazioni.
		filtroRuoli.setTipologia(RuoloTipologia.INTERNO);
	
		List<String> allRuoli = stationCore.getAllRuoli(filtroRuoli);
		final ServizioApplicativoRuoli ruoli = invocazionePorta.getRuoli() == null ? new ServizioApplicativoRuoli() : invocazionePorta.getRuoli();
		
		if (applicativo.getRuoli() != null) {
			applicativo.getRuoli().forEach( nome -> {
				
				if (!allRuoli.contains(nome)) {
					throw FaultCode.RICHIESTA_NON_VALIDA.toException("Il ruolo di nome " + nome + " non è presente o assegnabile al servizio applicativo.");
				}
				Ruolo r = new Ruolo();
				r.setNome(nome);
				ruoli.addRuolo(r);
			});
		}
		invocazionePorta.setRuoli(ruoli);
			
		final String fault = ServiziApplicativiCostanti.SERVIZI_APPLICATIVI_FAULT_SOAP;
		
		InvocazionePortaGestioneErrore ipge = new InvocazionePortaGestioneErrore();
		ipge.setFault(FaultIntegrazioneTipo.toEnumConstant(fault));
		invocazionePorta.setGestioneErrore(ipge);
		
		invocazionePorta.setSbustamentoInformazioniProtocollo(StatoFunzionalita.toEnumConstant(""));

		sa.setInvocazionePorta(invocazionePorta);
		
	    return sa;
	}
	
	
	public static final Applicativo servizioApplicativoToApplicativo(ServizioApplicativo sa) {
		Applicativo ret = new Applicativo();
		
		ret.setNome(sa.getNome());

		
		InvocazionePorta invPorta = sa.getInvocazionePorta();
		if (invPorta != null) {
			ServizioApplicativoRuoli ruoli = invPorta.getRuoli();
			if (ruoli != null) {
				ret.setRuoli(ruoli.getRuoloList().stream().map( ruolo -> ruolo.getNome().toString() ).collect(Collectors.toList()));
			}
		}
	
		
		Credenziali cred = invPorta.getCredenziali(0);
		ret.setModalitaAccesso(Helper.modalitaAccessoFromCredenzialeTipo.get(cred.getTipo()));
		ret.setCredenziali(authFromCredenziali(cred));
			
		return ret;
	}
	
	// Rationale: Questa funzione è bene che sia unchecked. Se non esplicitamente catturata infatti, comporterà in ogni caso lato API
	// la segnalazione di un errore interno.
	//
	// Il fatto che abbiamo creato nel sistema un oggetto di tipo ServizioApplicativo ritenuto corretto (il parametro)
	// ma per il quale non si riesce più a recuperare, mmmh.
	//	+ Il tipo del soggetto propietario
	//	+ Il tipo del protocollo associato al tipo del soggeto
	//  + Il servizio protocolli stesso
	//
	// Denotano un errore interno.
	//
	// è una funzione che effettua un mapping da un tipo all'altro e che potrà essere utilizzata liberamente negli stream.
	
	public static final ApplicativoItem servizioApplicativoToApplicativoItem(ServizioApplicativo sa) {
		ApplicativoItem ret = new ApplicativoItem();
		
		ret.setNome(sa.getNome());
		
		String tipo_protocollo = null;
		
		try {
			tipo_protocollo = ProtocolFactoryManager.getInstance().getProtocolByOrganizationType(sa.getTipoSoggettoProprietario());
		} catch (ProtocolException e) {
		
			e.printStackTrace();
			throw new RuntimeException(e);
		}		
		
		ret.setProfilo(Helper.profiloFromTipoProtocollo.get(tipo_protocollo));
		ret.setSoggetto(sa.getNomeSoggettoProprietario());
		
		ServizioApplicativoRuoli saRoles = null;
		if(sa.getInvocazionePorta()!=null) {
			saRoles = sa.getInvocazionePorta().getRuoli();
		}
		if (saRoles == null) {
			ret.setCountRuoli(0);
		} 
		else {
			ret.setCountRuoli(saRoles.sizeRuoloList());
		}
		
		return ret;
	}
	
	
	
	public static final IDSoggetto getIDSoggetto(String nome, String tipo_protocollo) throws ProtocolException {
		String tipo_soggetto = ProtocolFactoryManager.getInstance().getDefaultOrganizationTypes().get(tipo_protocollo);
		return  new IDSoggetto(tipo_soggetto,nome);
	}
	
	public static final Soggetto getSoggetto(String nome, ProfiloEnum modalita, SoggettiCore soggettiCore) throws DriverRegistroServiziException, DriverRegistroServiziNotFound, ProtocolException {
		return soggettiCore.getSoggettoRegistro(getIDSoggetto(nome,Helper.tipoProtocolloFromProfilo.get(modalita)));

	}
	
	
	public static final void checkServizioApplicativoFormat(ServizioApplicativo sa, ServiziApplicativiCore saCore, SoggettiCore soggettiCore) throws Exception {
		
		checkApplicativoName(sa.getNome());
		
		Credenziali cred =  sa.getInvocazionePorta().getCredenzialiList().get(0);
		checkCredenziali(cred);
	}
			
	
	/**
	 * Swagger passa le credenziali come una linkedHashMap.
	 * Dobbiamo trasformarla nei relativi oggetti di autenticazione.
	 * 
	 * TODO: Rimuovere questa versione e utilizzare quella in RestApiHelper.
	 * 
	 * @param applicativo
	 * @return
	 * @throws Exception
	 */
	public static Object translateCredenzialiApplicativo(Applicativo applicativo) {
		Object creds = null;
		String  tipoauthSA = Helper.tipoAuthSAFromModalita.get(applicativo.getModalitaAccesso().toString());
		
		if (tipoauthSA == null)
			throw FaultCode.RICHIESTA_NON_VALIDA.toException("Tipo modalità accesso sconosciuto: " + applicativo.getModalitaAccesso());
		
		@SuppressWarnings("unchecked")
		LinkedHashMap<String, String> map_creds = (LinkedHashMap<String,String>) applicativo.getCredenziali();
	
		if (tipoauthSA.equals(ConnettoriCostanti.AUTENTICAZIONE_TIPO_BASIC)) {
			AuthenticationHttpBasic c = new AuthenticationHttpBasic();
			c.setPassword(map_creds.get("password"));
			c.setUsername(map_creds.get("username"));
			creds = c;					
		}
		
		else if (tipoauthSA.equals(ConnettoriCostanti.AUTENTICAZIONE_TIPO_PRINCIPAL)) {
			AuthenticationPrincipal c = new AuthenticationPrincipal();
			c.setUserid(map_creds.get("user_id"));
			creds = c;
		}
		
		else if (tipoauthSA.equals(ConnettoriCostanti.AUTENTICAZIONE_TIPO_SSL))	{
			AuthenticationHttps c = new AuthenticationHttps();
			c.setSubject(map_creds.get("subject"));
			creds = c;
		}
		
		else if (tipoauthSA.equals(ConnettoriCostanti.AUTENTICAZIONE_TIPO_NESSUNA)) {
			creds = null;
		}
		else {
			throw FaultCode.RICHIESTA_NON_VALIDA.toException("Tipo autenticazione sconosciuto: " + tipoauthSA);
		}

		
		return creds;
	}
	
	
	/**
	 * Trasforma le credenziali di autenticazione di un servizio applicativo nelle credenziali
	 * di un'InvocazionePorta.
	 * 
	 */
	public static Credenziali credenzialiFromAuth(Object cred, String tipoauthSA) {
		Credenziali credenziali = new Credenziali();
		credenziali.setUser("");
		credenziali.setSubject("");

	
	    if (tipoauthSA.equals(ConnettoriCostanti.AUTENTICAZIONE_TIPO_NESSUNA)) {
			credenziali.setTipo(null);
		}else{
			credenziali.setTipo(CredenzialeTipo.toEnumConstant(tipoauthSA));
		}
		
		if (tipoauthSA.equals(ConnettoriCostanti.AUTENTICAZIONE_TIPO_BASIC)) {
			AuthenticationHttpBasic auth = (AuthenticationHttpBasic) cred;
			credenziali.setUser(auth.getUsername());
			credenziali.setPassword(auth.getPassword());
		}
		else if (tipoauthSA.equals(ConnettoriCostanti.AUTENTICAZIONE_TIPO_SSL)) {
			AuthenticationHttps auth = (AuthenticationHttps) cred;
			credenziali.setSubject(auth.getSubject());
		}
		else if (tipoauthSA.equals(ConnettoriCostanti.AUTENTICAZIONE_TIPO_PRINCIPAL)) {
			AuthenticationPrincipal auth = (AuthenticationPrincipal) cred;
			credenziali.setUser(auth.getUserid());
		}
		
		return credenziali;
	}
	
	/**
	 * Trasforma le credenziali per un'InvocazionePorta nelle credenziali conservate in un applicativo.
	 * TODO: Sarà possibile unificare in futuro?
	 * 
	 * @param cred
	 * @return
	 */
	public static Object authFromCredenziali(Credenziali cred) {
		
		Object ret = null;
		
		switch(cred.getTipo()) {
		case BASIC: {
			AuthenticationHttpBasic auth = new AuthenticationHttpBasic();
			auth.setUsername(cred.getUser());
			auth.setPassword(cred.getPassword());
			ret = auth;
			break;
		}
			
		case PRINCIPAL: {
			AuthenticationPrincipal auth = new AuthenticationPrincipal();
			auth.setUserid(cred.getUser());
			break;
		}
			
		case SSL: {
			AuthenticationHttps auth = new AuthenticationHttps();
			auth.setSubject(cred.getSubject());
			ret = auth;
			break;
		} 
		}
		
		return ret;
	}
	
	
	public static void checkApplicativoName(String nome) throws Exception {

		if (nome.equals(""))		
			throw FaultCode.RICHIESTA_NON_VALIDA.toException(
					"Dati incompleti. E' necessario indicare: " + ServiziApplicativiCostanti.LABEL_PARAMETRO_SERVIZI_APPLICATIVI_NOME
			);
					
		if (nome.indexOf(" ") != -1 || nome.indexOf('\"') != -1) {
			throw FaultCode.RICHIESTA_NON_VALIDA.toException("Non inserire spazi o doppi apici nei campi di testo");
		}
		
		checkIntegrationEntityName(nome);
		checkLength255(nome, ServiziApplicativiCostanti.LABEL_PARAMETRO_SERVIZI_APPLICATIVI_NOME);
	}
	
	
	public static boolean isApplicativoDuplicato(ServizioApplicativo sa, ServiziApplicativiCore saCore) throws DriverConfigurazioneException {

		IDServizioApplicativo idSA = new IDServizioApplicativo();
		IDSoggetto idSoggetto = new IDSoggetto(sa.getTipoSoggettoProprietario(), sa.getNomeSoggettoProprietario());
		idSA.setIdSoggettoProprietario(idSoggetto);
		idSA.setNome(sa.getNome());
		
		return saCore.existsServizioApplicativo(idSA);
	}
	
	
	public static void checkIntegrationEntityName(String name) throws Exception{
		// Il nome deve contenere solo lettere e numeri e '_' '-' '.' '/'
		if (!RegularExpressionEngine.isMatch(name,"^[_A-Za-z][\\-\\._/A-Za-z0-9]*$")) {
			
			throw FaultCode.RICHIESTA_NON_VALIDA.toException(
					"Il campo '" + 
					 ServiziApplicativiCostanti.LABEL_PARAMETRO_SERVIZI_APPLICATIVI_NOME + 
					 "' può iniziare solo con un carattere [A-Za-z] o il simbolo '_' e dev'essere formato solo da caratteri, cifre, '_' , '-'");
		}
	}
	
	
	public static void checkLength255(String value, String object) {
		checkLength(value, object, -1, 255);
	}
	
	
	public static void checkLength(String value, String object, int minLength, int maxLength) {
		
		boolean error = false;
		
		if(minLength>0) {
			if(value==null || value.length()<minLength) {
				error = true;
			}
		}
		if(maxLength>0) {
			if(value!=null && value.length()>maxLength) {
				error = true;
			}
		}
		
		if (error) {
			throw FaultCode.RICHIESTA_NON_VALIDA.toException(
					"L'informazione fornita nel campo " +
					object + 
					" deve essere compresa fa " + Integer.toString(minLength) + " e " + Integer.toString(maxLength) +" caratteri"
			);
		}
			
	}

	public static final void checkCredenziali(Credenziali cred) {
					
		switch (cred.getTipo()) {
		case BASIC: {
			AuthenticationHttpBasic auth = new AuthenticationHttpBasic();
			auth.setUsername(cred.getUser());
			auth.setPassword(cred.getPassword());
			checkBasicAuth(auth);
			break;
		}
		case PRINCIPAL: {
			AuthenticationPrincipal auth = new AuthenticationPrincipal();
			auth.setUserid(cred.getSubject());	//TODO: Lo user-id dell'auth è il subject delle credenziali=
			checkPrincipalAuth(auth);
			break;
		}
		case SSL: {
			AuthenticationHttps auth = new AuthenticationHttps();
			auth.setSubject(cred.getSubject());
			checkSSLAuth(auth);
			break;
		}
		
		}
	}
		
	
	public static final void checkBasicAuth(AuthenticationHttpBasic auth) {
		String utente = auth.getUsername();
		String password = auth.getPassword();
		
		if(utente.equals("") || password.equals("")){
			
			List<String> pMancanti = new ArrayList<String>(2);
		
			if (utente.equals(""))
				pMancanti.add(ConnettoriCostanti.LABEL_PARAMETRO_CREDENZIALI_AUTENTICAZIONE_USERNAME);
			if (password.equals(""))
				pMancanti.add(ConnettoriCostanti.LABEL_PARAMETRO_CREDENZIALI_AUTENTICAZIONE_PASSWORD);
			
			throw FaultCode.RICHIESTA_NON_VALIDA.toException("Dati incompleti. E' necessario indicare: " + String.join(",",pMancanti));
		}
		
		checkLength255(utente, ConnettoriCostanti.LABEL_PARAMETRO_CREDENZIALI_AUTENTICAZIONE_USERNAME);
		checkLength255(password, ConnettoriCostanti.LABEL_PARAMETRO_CREDENZIALI_AUTENTICAZIONE_PASSWORD);
			
		if (utente.indexOf(" ") != -1 || (password.indexOf(" ") != -1)) {
			throw FaultCode.RICHIESTA_NON_VALIDA.toException("Non inserire spazi nei campi di testo");
		}		
	}
	
	
	public static final void checkSSLAuth(AuthenticationHttps auth) {
		String subject = auth.getSubject();
		
		if (subject.equals("")) {
			throw FaultCode.RICHIESTA_NON_VALIDA.toException(
					"Dati incompleti. E' necessario indicare il "+ConnettoriCostanti.LABEL_PARAMETRO_CREDENZIALI_AUTENTICAZIONE_SUBJECT
			);
		}
		
		try{
			org.openspcoop2.utils.Utilities.validaSubject(subject);
		} catch(Exception e){
			throw FaultCode.RICHIESTA_NON_VALIDA.toException("Le credenziali di tipo ssl  possiedono un subject non valido: "+e.getMessage());
		}
		
		checkLength255(subject, ConnettoriCostanti.LABEL_PARAMETRO_CREDENZIALI_AUTENTICAZIONE_SUBJECT);
	}
	
	
	public static final void checkPrincipalAuth(AuthenticationPrincipal auth) {
		String principal = auth.getUserid();
		
		if(principal.equals("")) {
			throw FaultCode.RICHIESTA_NON_VALIDA.toException(
					"Dati incompleti. E' necessario indicare: " + ConnettoriCostanti.LABEL_PARAMETRO_CREDENZIALI_AUTENTICAZIONE_PRINCIPAL
			);
		}
		
		checkLength255(principal, ConnettoriCostanti.LABEL_PARAMETRO_CREDENZIALI_AUTENTICAZIONE_PRINCIPAL);
	}

	
	public static final List<ServizioApplicativo> getApplicativiStesseCredenziali(ServizioApplicativo sa, ServiziApplicativiCore saCore) throws DriverConfigurazioneException {
		Credenziali cred = sa.getInvocazionePorta().getCredenziali(0);
		
		switch(cred.getTipo()) {
		case BASIC:
			return saCore.servizioApplicativoWithCredenzialiBasicList(cred.getUser(), cred.getPassword());	
			
		case PRINCIPAL:
			return saCore.servizioApplicativoWithCredenzialiPrincipalList(cred.getSubject());
	
		case SSL:
			return saCore.servizioApplicativoWithCredenzialiSslList(cred.getSubject());
		default:	
			return new ArrayList<ServizioApplicativo>();
		}
		// TODO: Ask andrea Se quando non ho credenziali di accesso, ovvero provengo da un ConnettoriCostanti.AUTENTICAZIONE_TIPO_NESSUNA
		// non avrò problemi di conflitti
	}
	
	
	public static final void checkNoDuplicateCred(
			ServizioApplicativo sa,
			List<ServizioApplicativo> saConflicts,
			SoggettiCore soggettiCore,
			TipoOperazione tipoOperazione ) throws DriverRegistroServiziNotFound, DriverRegistroServiziException 
	{		
		Soggetto soggettoToCheck = soggettiCore.getSoggettoRegistro(sa.getIdSoggetto());
		String portaDominio = soggettoToCheck.getPortaDominio();
		
		for (int i = 0; i < saConflicts.size(); i++) {
			ServizioApplicativo saConflict = saConflicts.get(i);

			// controllo se soggetto appartiene a nal diversi, in tal caso e' possibile avere stesse credenziali
			Soggetto tmpSoggettoProprietarioSa = soggettiCore.getSoggettoRegistro(saConflict.getIdSoggetto());
			if (!portaDominio.equals(tmpSoggettoProprietarioSa.getPortaDominio()))
				continue;
			
			if (tipoOperazione == TipoOperazione.CHANGE && sa.getNome().equals(saConflict.getNome()) && (sa.getIdSoggetto() == saConflict.getIdSoggetto())) {
				continue;
			}
			
			throw FaultCode.RICHIESTA_NON_VALIDA.toException("Esistono gia' altri servizi applicativi che possiedono le credenziali indicate.");
		}
		
	}
	
	public static final ServizioApplicativo getServizioApplicativo(String nome, String soggetto, String tipo_protocollo, ServiziApplicativiCore saCore) throws ProtocolException, DriverConfigurazioneException  { 
		IDServizioApplicativo idServizioApplicativo = new IDServizioApplicativo();
		idServizioApplicativo.setIdSoggettoProprietario(ApplicativiApiHelper.getIDSoggetto(soggetto, tipo_protocollo));
		idServizioApplicativo.setNome(nome);
		
		return saCore.getServizioApplicativo(idServizioApplicativo);
	}


	public static List<IDPortaDelegata> getIdPorteDelegate(ServizioApplicativo oldSa, PorteDelegateCore pCore) throws DriverConfigurazioneException {
		FiltroRicercaPorteDelegate filtro = new FiltroRicercaPorteDelegate();
		filtro.setTipoSoggetto(oldSa.getTipoSoggettoProprietario());
		filtro.setNomeSoggetto(oldSa.getNomeSoggettoProprietario());
		filtro.setNomeServizioApplicativo(oldSa.getNome());
		return pCore.getAllIdPorteDelegate(filtro);
		
	}


	public static void checkServizioApplicativoInUso(ServizioApplicativo oldSa, SoggettiCore soggetiCore) throws DriverConfigurazioneNotFound, DriverConfigurazioneException {
		
		org.openspcoop2.core.config.Soggetto oldSogg = soggetiCore.getSoggetto(oldSa.getIdSoggetto());
		String nomeProv = oldSogg.getTipo() + "/" + oldSogg.getNome();

		boolean servizioApplicativoInUso = false;
		
		for (int i = 0; i < oldSogg.sizePortaDelegataList(); i++) {
			PortaDelegata pde = oldSogg.getPortaDelegata(i);
			for (int j = 0; j < pde.sizeServizioApplicativoList(); j++) {
				PortaDelegataServizioApplicativo tmpSA = pde.getServizioApplicativo(j);
				if (oldSa.getNome().equals(tmpSA.getNome())) {
					servizioApplicativoInUso = true;
					break;
				}
			}
			if (servizioApplicativoInUso)
				break;
		}

		if (!servizioApplicativoInUso) {
			for (int i = 0; i < oldSogg.sizePortaApplicativaList(); i++) {
				PortaApplicativa pa = oldSogg.getPortaApplicativa(i);
				for (int j = 0; j < pa.sizeServizioApplicativoList(); j++) {
					PortaApplicativaServizioApplicativo tmpSA = pa.getServizioApplicativo(j);
					if (oldSa.getNome().equals(tmpSA.getNome())) {
						servizioApplicativoInUso = true;
						break;
					}
				}
				if (servizioApplicativoInUso)
					break;
			}
		}
		
		if (servizioApplicativoInUso) {
			throw FaultCode.RICHIESTA_NON_VALIDA.toException("Il Servizio Applicativo " + oldSa.getNome() + "è già stato associato ad alcune porte delegate e/o applicative del Soggetto " + nomeProv + ". Se si desidera modificare il Soggetto è necessario eliminare prima tutte le occorrenze del Servizio Applicativo");
		}
	}
	
}
