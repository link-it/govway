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
package org.openspcoop2.core.config.rs.server.api.impl;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.AbstractMap.SimpleEntry;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.beanutils.BeanUtils;
import org.openspcoop2.core.commons.Filtri;
import org.openspcoop2.core.config.constants.CredenzialeTipo;
import org.openspcoop2.core.config.rs.server.config.ServerProperties;
import org.openspcoop2.core.config.rs.server.model.AuthenticationHttpBasic;
import org.openspcoop2.core.config.rs.server.model.AuthenticationHttps;
import org.openspcoop2.core.config.rs.server.model.AuthenticationHttpsCertificato;
import org.openspcoop2.core.config.rs.server.model.AuthenticationHttpsConfigurazioneManuale;
import org.openspcoop2.core.config.rs.server.model.AuthenticationPrincipal;
import org.openspcoop2.core.config.rs.server.model.ModalitaAccessoEnum;
import org.openspcoop2.core.config.rs.server.model.TipoAutenticazioneHttps;
import org.openspcoop2.core.config.rs.server.model.TipoKeystore;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.core.registry.AccordoServizioParteComune;
import org.openspcoop2.core.registry.beans.AccordoServizioParteComuneSintetico;
import org.openspcoop2.core.registry.constants.StatoFunzionalita;
import org.openspcoop2.core.registry.driver.IDAccordoFactory;
import org.openspcoop2.protocol.manifest.constants.ServiceBinding;
import org.openspcoop2.utils.UtilsException;
import org.openspcoop2.utils.certificate.ArchiveLoader;
import org.openspcoop2.utils.certificate.ArchiveType;
import org.openspcoop2.utils.certificate.CertificateInfo;
import org.openspcoop2.utils.io.Base64Utilities;
import org.openspcoop2.utils.service.beans.Lista;
import org.openspcoop2.utils.service.beans.ProfiloEnum;
import org.openspcoop2.utils.service.fault.jaxrs.FaultCode;
import org.openspcoop2.web.ctrlstat.core.Search;
import org.openspcoop2.web.ctrlstat.servlet.ConsoleHelper;
import org.openspcoop2.web.ctrlstat.servlet.apc.AccordiServizioParteComuneCore;
import org.openspcoop2.web.ctrlstat.servlet.connettori.ConnettoriCostanti;
import org.openspcoop2.web.lib.mvc.BinaryParameter;
import org.openspcoop2.web.lib.mvc.ServletUtils;

/**
 * Helper
 * 
 * @author $Author$
 * @version $Rev$, $Date$
 * 
 */
public class Helper extends org.openspcoop2.utils.service.beans.utils.BaseHelper {
	
	
	
	public static <T extends Lista> T returnOrNotFound(T ret ) throws UtilsException {
		boolean throwIfEmpty = ServerProperties.getInstance().isFindall404();
		
		if ( (ret == null || ret.getTotal() <= 0 ) && throwIfEmpty ) 
			throw FaultCode.NOT_FOUND.toException("Nessun elemento corrisponde ai criteri di ricerca");
		
		return ret;
	}
	
	/**
	 * Converte un enum in un altro enum supposto che i valori di enumerazione siano identici.
	 * Questo si presta bene a conversioni del tipo HttpMethodEnum -> HttpMethod. In questo caso, pur avendo
	 * il primo enum un valore in più - QUALSIASI-, esso viene tradotto a null così come richiesto dalla console.
	 *  
	 * Potrei utilizzare un terzo parametro per sollevare un eccezione in caso di mancata conversione
	 */
	public static final <E1 extends Enum<E1>,E2 extends Enum<E2>> E2 apiEnumToGovway(E1 aenum, Class<E2> toClass  ) {
		
		try {
			return Enum.valueOf(toClass, aenum.name());
		} catch(Exception e) {
			return null;
		}
	//   BeanUtils.setProperty(ret, "tipo", Enum.valueOf( (Class<Enum>)enumClass, "BASIC"));
	//	 return Enum.valueOf( (Class<Enum>)toClass, "BASIC")
	}

	public static final Map<CredenzialeTipo,ModalitaAccessoEnum> modalitaAccessoFromCredenzialeTipo = new HashMap<>();
	static {
		modalitaAccessoFromCredenzialeTipo.put(CredenzialeTipo.BASIC, ModalitaAccessoEnum.HTTP_BASIC);
		modalitaAccessoFromCredenzialeTipo.put(CredenzialeTipo.PRINCIPAL, ModalitaAccessoEnum.PRINCIPAL);
		modalitaAccessoFromCredenzialeTipo.put(CredenzialeTipo.SSL, ModalitaAccessoEnum.HTTPS);
	}
	
	public static final Map<ModalitaAccessoEnum,CredenzialeTipo> credenzialeTipoFromModalitaAccesso = new HashMap<ModalitaAccessoEnum,CredenzialeTipo>();
	static {
		modalitaAccessoFromCredenzialeTipo.forEach( (cred,mod) -> credenzialeTipoFromModalitaAccesso.put(mod, cred));
	}

	
	public static final Map<ModalitaAccessoEnum,String> tipoAuthFromModalitaAccesso = new HashMap<ModalitaAccessoEnum,String>();
	static {
		tipoAuthFromModalitaAccesso.put(ModalitaAccessoEnum.HTTPS, ConnettoriCostanti.AUTENTICAZIONE_TIPO_SSL);
		tipoAuthFromModalitaAccesso.put(ModalitaAccessoEnum.HTTP_BASIC, ConnettoriCostanti.AUTENTICAZIONE_TIPO_BASIC);
		tipoAuthFromModalitaAccesso.put(ModalitaAccessoEnum.PRINCIPAL, ConnettoriCostanti.AUTENTICAZIONE_TIPO_PRINCIPAL);
		tipoAuthFromModalitaAccesso.put(null, ConnettoriCostanti.AUTENTICAZIONE_TIPO_NESSUNA);
	}

	
	// Deprecato, utilizzare il dizionario di sopra.
	public static final Map<String,String> tipoAuthSAFromModalita = Stream.of(
			new SimpleEntry<>("http-basic", ConnettoriCostanti.AUTENTICAZIONE_TIPO_BASIC),
			new SimpleEntry<>("https", ConnettoriCostanti.AUTENTICAZIONE_TIPO_SSL),
			new SimpleEntry<>("principal",ConnettoriCostanti.AUTENTICAZIONE_TIPO_PRINCIPAL),
			new SimpleEntry<>("custom", ConnettoriCostanti.AUTENTICAZIONE_TIPO_NESSUNA)
		).collect(Collectors.toMap(SimpleEntry::getKey, SimpleEntry::getValue));


	public static ProfiloEnum getProfiloDefault() throws UtilsException {
		ServerProperties serverProperties = ServerProperties.getInstance();
		
		return profiloFromTipoProtocollo.get(serverProperties.getProtocolloDefault());
	}


	public static String getProtocolloOrDefault(ProfiloEnum profilo) throws UtilsException {
		ServerProperties serverProperties = ServerProperties.getInstance();
		String tipo_protocollo = null;
		
		String modalita = profilo!=null ? profilo.toString() : null;
		if (modalita == null || modalita.length() == 0) {
			tipo_protocollo = serverProperties.getProtocolloDefault();
		}
		
		else {
			tipo_protocollo = tipoProtocolloFromProfilo.get(profilo);
		}
		
		if (tipo_protocollo == null) {
			StringBuilder sb = new StringBuilder("Impossible recuperare il profilo di interoperabilità dalla modalità: ");
			sb.append(modalita);
			throw FaultCode.RICHIESTA_NON_VALIDA.toException(sb.toString());
		}
		
		return tipo_protocollo;
	}


	public static final String getSoggettoOrDefault(String soggetto, ProfiloEnum profilo) throws UtilsException {
		
		if (soggetto == null || soggetto.length() == 0) {
			ServerProperties serverProperties = ServerProperties.getInstance();
			soggetto = serverProperties.getSoggettoDefault(profilo!=null ? profilo.toString() : null);
		}
		
		return soggetto;
	}
	
	public static void overrideAuthParams(HttpRequestWrapper wrap, ConsoleHelper consoleHelper, ModalitaAccessoEnum modalitaAccesso, Object credenziali) {
		
		switch(modalitaAccesso) {
		case HTTP_BASIC: {
			AuthenticationHttpBasic c = (AuthenticationHttpBasic) credenziali;
			wrap.overrideParameter(ConnettoriCostanti.PARAMETRO_CREDENZIALI_AUTENTICAZIONE_USERNAME, c.getUsername());
			wrap.overrideParameter(ConnettoriCostanti.PARAMETRO_CREDENZIALI_AUTENTICAZIONE_PASSWORD, c.getPassword());
			break;
		}
		case HTTPS: {
			AuthenticationHttps c = (AuthenticationHttps) credenziali;
			wrap.overrideParameter(ConnettoriCostanti.PARAMETRO_CREDENZIALI_AUTENTICAZIONE_CONFIGURAZIONE_SSL_WIZARD_STEP,
					ConnettoriCostanti.VALUE_PARAMETRO_CREDENZIALI_AUTENTICAZIONE_CONFIGURAZIONE_SSL_NO_WIZARD);
			switch (c.getTipo()) {
			case CERTIFICATO:
				wrap.overrideParameter(ConnettoriCostanti.PARAMETRO_CREDENZIALI_AUTENTICAZIONE_CONFIGURAZIONE_SSL,
						ConnettoriCostanti.VALUE_PARAMETRO_CREDENZIALI_AUTENTICAZIONE_CONFIGURAZIONE_SSL_UPLOAD_CERTIFICATO);
				AuthenticationHttpsCertificato certificate = (AuthenticationHttpsCertificato) c.getCertificato();
				consoleHelper.registerBinaryParameter(ConnettoriCostanti.PARAMETRO_CREDENZIALI_AUTENTICAZIONE_CONFIGURAZIONE_SSL_FILE_CERTIFICATO, certificate.getArchivio());
				wrap.overrideParameter(ConnettoriCostanti.PARAMETRO_CREDENZIALI_AUTENTICAZIONE_CONFIGURAZIONE_SSL_TIPO_ARCHIVIO, certificate.getTipo().toString());
				wrap.overrideParameter(ConnettoriCostanti.PARAMETRO_CREDENZIALI_AUTENTICAZIONE_CONFIGURAZIONE_SSL_ALIAS_CERTIFICATO, certificate.getAlias());
				wrap.overrideParameter(ConnettoriCostanti.PARAMETRO_CREDENZIALI_AUTENTICAZIONE_CONFIGURAZIONE_SSL_FILE_CERTIFICATO_PASSWORD, certificate.getPassword());
				break;
			case CONFIGURAZIONE_MANUALE:
				wrap.overrideParameter(ConnettoriCostanti.PARAMETRO_CREDENZIALI_AUTENTICAZIONE_CONFIGURAZIONE_SSL,
						ConnettoriCostanti.VALUE_PARAMETRO_CREDENZIALI_AUTENTICAZIONE_CONFIGURAZIONE_SSL_CONFIGURAZIONE_MANUALE);
				AuthenticationHttpsConfigurazioneManuale creManuale = (AuthenticationHttpsConfigurazioneManuale) c.getCertificato();
				wrap.overrideParameter(ConnettoriCostanti.PARAMETRO_CREDENZIALI_AUTENTICAZIONE_SUBJECT, creManuale.getSubject());
				wrap.overrideParameter(ConnettoriCostanti.PARAMETRO_CREDENZIALI_AUTENTICAZIONE_ISSUER, creManuale.getIssuer());
				break;
			}
			break;
		}
		case PRINCIPAL: {
			AuthenticationPrincipal c = (AuthenticationPrincipal) credenziali;
			wrap.overrideParameter(ConnettoriCostanti.PARAMETRO_CREDENZIALI_AUTENTICAZIONE_PRINCIPAL, c.getUserid());
			break;
		}
		}
	}
	
	
	/**
	 * Swagger passa le credenziali come una linkedHashMap.
	 * Dobbiamo trasformarla nei relativi oggetti di autenticazione.
	 * 
	 * @param applicativo
	 * @return
	 * @throws Exception
	 */
	public static Object translateCredenziali(Object creds, ModalitaAccessoEnum tipoAuth) {
		Object ret = null;
	
		switch (tipoAuth) {
		case HTTP_BASIC: {
			if(creds!=null && creds instanceof AuthenticationHttpBasic) {
				ret = (AuthenticationHttpBasic) creds;
			}			
			else {
				@SuppressWarnings("unchecked")
				LinkedHashMap<String, Object> map_creds = (LinkedHashMap<String,Object>) creds;
				AuthenticationHttpBasic c = new AuthenticationHttpBasic();
				c.setPassword((String) map_creds.get("password"));
				c.setUsername((String) map_creds.get("username"));
				ret = c;					
			}
			break;
		}
		case HTTPS: {
			if(creds!=null && creds instanceof AuthenticationHttps) {
				ret = (AuthenticationHttps) creds;
			}
			else {
				@SuppressWarnings("unchecked")
				LinkedHashMap<String, Object> map_creds = (LinkedHashMap<String,Object>) creds;
				AuthenticationHttps c = new AuthenticationHttps();
				c.setTipo(TipoAutenticazioneHttps.fromValue((String) map_creds.get("tipo")));
				Object oCredenziali = map_creds.get("certificato");
				@SuppressWarnings("unchecked")
				LinkedHashMap<String, Object> map_creds_https = (LinkedHashMap<String,Object>) oCredenziali;
				switch (c.getTipo()) {
				case CERTIFICATO:
					AuthenticationHttpsCertificato cre = new AuthenticationHttpsCertificato();
					String base64archive = (String) map_creds_https.get("archivio");
					cre.setArchivio(Base64Utilities.decode(base64archive));
					cre.setTipo(TipoKeystore.fromValue( (String) map_creds_https.get("tipo")));
					if(map_creds_https.containsKey("alias")) {
						cre.setAlias((String)map_creds_https.get("alias"));
					}
					if(map_creds_https.containsKey("password")) {
						cre.setPassword((String)map_creds_https.get("password"));
					}
					if(map_creds_https.containsKey("strict_verification")) {
						cre.setStrictVerification((Boolean)map_creds_https.get("strict_verification"));
					}
					c.setCertificato(cre);
					break;
				case CONFIGURAZIONE_MANUALE:
					AuthenticationHttpsConfigurazioneManuale creManuale = new AuthenticationHttpsConfigurazioneManuale();
					creManuale.setSubject((String)map_creds_https.get("subject"));
					if(map_creds_https.containsKey("issuer")) {
						creManuale.setIssuer((String)map_creds_https.get("issuer"));
					}
					c.setCertificato(creManuale);
					break;	
				}
				ret = c;
			}
			break;
		}
		case PRINCIPAL: {
			if(creds!=null && creds instanceof AuthenticationPrincipal) {
				ret = (AuthenticationPrincipal) creds;
			}
			else {
				@SuppressWarnings("unchecked")
				LinkedHashMap<String, Object> map_creds = (LinkedHashMap<String,Object>) creds;
				AuthenticationPrincipal c = new AuthenticationPrincipal();
				c.setUserid((String) map_creds.get("userid"));
				ret = c;
			}
			break;
		}
		default:
			ret = null;
			break;
			
		}
		return ret;
	}
	
	/**
	 * Trasforma un oggetto di tipo credenziali parte API (è assunto ben formato) nei numerosi oggetti Credenziale* generati
	 * automaticamente all'interno del progetto e identici.
	 * 
	 * @param creds
	 * @param tipoAuth
	 * @param credClass	La classe verso cui tradurre
	 * @param enumClass La modalità di accesso, ovvero il campo "tipo" della classe verso cui tradurre.
	 * @throws UtilsException 
	 */
	
	// TODO: Manage exceptions.
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static <T,E> T apiCredenzialiToGovwayCred(Object creds, ModalitaAccessoEnum tipoAuth, Class<T> credClass, Class<E> enumClass)
			throws IllegalAccessException, InvocationTargetException, InstantiationException, UtilsException {
		
		if (!enumClass.isEnum())
			throw new IllegalArgumentException("Il parametro enumClass deve essere un'enumerazione");
		
		T ret = credClass.newInstance();
		
		switch (tipoAuth) {
		case HTTP_BASIC: {
			AuthenticationHttpBasic auth = (AuthenticationHttpBasic) creds;
			BeanUtils.setProperty(ret, "tipo", Enum.valueOf( (Class<Enum>)enumClass, "BASIC"));
			BeanUtils.setProperty(ret, "user", auth.getUsername());
			BeanUtils.setProperty(ret, "password", auth.getPassword());
			break;
		}
		case HTTPS: {
			AuthenticationHttps auth = (AuthenticationHttps) creds;
			BeanUtils.setProperty(ret, "tipo", Enum.valueOf( (Class<Enum>)enumClass, "SSL"));
			switch (auth.getTipo()) {
			case CERTIFICATO:
				AuthenticationHttpsCertificato cre = (AuthenticationHttpsCertificato) auth.getCertificato();
				ArchiveType type = ArchiveType.CER;
				String alias = cre.getAlias();
				String password = cre.getPassword();
				if(cre.getTipo()!=null) {
					type = ArchiveType.valueOf(cre.getTipo().toString());
				}
				CertificateInfo cInfo = ArchiveLoader.load(type, cre.getArchivio(), alias, password).getCertificate();
				BeanUtils.setProperty(ret, "certificate", cre.getArchivio());
				BeanUtils.setProperty(ret, "certificateStrictVerification", cre.isStrictVerification());
				BeanUtils.setProperty(ret, "cnSubject", cInfo.getSubject().getCN());
				BeanUtils.setProperty(ret, "cnIssuer", cInfo.getIssuer().getCN());				
				break;
			case CONFIGURAZIONE_MANUALE:
				AuthenticationHttpsConfigurazioneManuale creManuale = (AuthenticationHttpsConfigurazioneManuale) auth.getCertificato();
				BeanUtils.setProperty(ret, "subject", creManuale.getSubject());
				BeanUtils.setProperty(ret, "issuer", creManuale.getIssuer());
				break;
			}
			break;
		}
		case PRINCIPAL: {
			AuthenticationPrincipal auth = (AuthenticationPrincipal) creds;
			BeanUtils.setProperty(ret, "tipo", Enum.valueOf( (Class<Enum>)enumClass, "PRINCIPAL"));
			BeanUtils.setProperty(ret, "user", auth.getUserid());
			break;
		}
		default:
			BeanUtils.setProperty(ret, "tipo", null);
			
		}
		
		return ret;
	}

	
	public static final <T,E> Object govwayCredenzialiToApi(Object govwayCreds, Class<T> credClass, Class<E> enumClass) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		
		if (!enumClass.isEnum())
			throw new IllegalArgumentException("Il parametro enumClass deve essere un'enumerazione");
		
		Object ret = null;		
		
		// Questo è il caso di autenticazione di tipo NESSUNO.
		if (govwayCreds == null)
			return ret;
		
		String tipo = BeanUtils.getProperty(govwayCreds, "tipo");
		
		if ("basic".equals(tipo)) {
			AuthenticationHttpBasic auth = new AuthenticationHttpBasic();
			auth.setUsername(BeanUtils.getProperty(govwayCreds, "user"));
			auth.setPassword(BeanUtils.getProperty(govwayCreds, "password"));
			ret = auth;
		}
		else if ("ssl".equals(tipo)) {
			AuthenticationHttps auth = new AuthenticationHttps();
			Method mCertificate = credClass.getMethod("getCertificate");
			Object oCertificate = mCertificate.invoke(govwayCreds);
			if(oCertificate==null) {
				auth.setTipo(TipoAutenticazioneHttps.CONFIGURAZIONE_MANUALE);
				AuthenticationHttpsConfigurazioneManuale manuale = new AuthenticationHttpsConfigurazioneManuale();
				manuale.setSubject(BeanUtils.getProperty(govwayCreds, "subject"));	
				manuale.setIssuer(BeanUtils.getProperty(govwayCreds, "issuer"));	
				auth.setCertificato(manuale);
			}
			else {
				auth.setTipo(TipoAutenticazioneHttps.CERTIFICATO);
				AuthenticationHttpsCertificato certificato = new AuthenticationHttpsCertificato();
				certificato.setTipo(TipoKeystore.CER);
				certificato.setArchivio((byte[])oCertificate);
				Method mCertificateStrictVerification = credClass.getMethod("isCertificateStrictVerification");
				certificato.setStrictVerification((Boolean) mCertificateStrictVerification.invoke(govwayCreds));
				auth.setCertificato(certificato);
			}
			ret = auth;
		}
		else if ("principal".equals(tipo)) {
			AuthenticationPrincipal auth = new AuthenticationPrincipal();
			auth.setUserid(BeanUtils.getProperty(govwayCreds, "user"));
			ret = auth;
		}
		
		return ret;
	}
	
	
	public static final ModalitaAccessoEnum credenzialiToModalita(Object obj) {
		if (obj == null) return null;
		
		if (obj instanceof AuthenticationHttpBasic)
			return ModalitaAccessoEnum.HTTP_BASIC;
		
		if (obj instanceof AuthenticationHttps)
			return ModalitaAccessoEnum.HTTPS;
		
		if (obj instanceof AuthenticationPrincipal)
			return ModalitaAccessoEnum.PRINCIPAL;
		else
			throw new IllegalArgumentException("credenzialiToModalita(): Tipo argomento sconosciuto");
	}
	
	


	public static final Search setupRicercaPaginata(String q, Integer limit, Integer offset, int idLista) {
		Search ricerca = new Search();
		
		if (limit != null && limit != 0)
			ricerca.setPageSize(idLista, limit);
		if (offset != null)
			ricerca.setIndexIniziale(idLista, offset);
		if (q == null) q = "";
		
		q = q.trim();
		if (q.equals("")) {
			ricerca.setSearchString(idLista, org.openspcoop2.core.constants.Costanti.SESSION_ATTRIBUTE_VALUE_RICERCA_UNDEFINED);
		} else {
			ricerca.setSearchString(idLista, q);
		}
		return ricerca;
	}
	
	public static final Search setupRicercaPaginata(String q, Integer limit, Integer offset, int idLista, IDSoggetto idSoggetto, String tipo_protocollo) {
		Search ricerca = setupRicercaPaginata( q, limit, offset, idLista );
		
		ricerca.addFilter(idLista, Filtri.FILTRO_SOGGETTO, idSoggetto.toString());
		ricerca.addFilter(idLista, Filtri.FILTRO_PROTOCOLLO, tipo_protocollo );
		return ricerca;
	}
	
	
	public static final boolean statoFunzionalitaToBool(StatoFunzionalita sf) {
		if (sf == null) return false;
		
		switch (sf) {
		case ABILITATO: return true;
		case DISABILITATO: return false;
		default: return false;
		}
	}
	
	public static final boolean statoFunzionalitaConfToBool(org.openspcoop2.core.config.constants.StatoFunzionalita sf) {
		if (sf == null) return false;
		
		switch (sf) {
		case ABILITATO: return true;
		case DISABILITATO: return false;
		default: return false;
		}
	}

	
	@Deprecated
	public static final String boolToYesNo(boolean v) {
		return ServletUtils.boolToCheckBoxStatus(v);
	}
	
	public static final StatoFunzionalita boolToStatoFunzionalita(Boolean v) {
		return v ? StatoFunzionalita.ABILITATO : StatoFunzionalita.DISABILITATO;
	}
	
	public static final org.openspcoop2.core.config.constants.StatoFunzionalita boolToStatoFunzionalitaConf(Boolean v) {
		if (v == null) v = false;
		
		return v ? org.openspcoop2.core.config.constants.StatoFunzionalita.ABILITATO : org.openspcoop2.core.config.constants.StatoFunzionalita.DISABILITATO; 
	}


	public static final BinaryParameter toBinaryParameter(byte[] value) {
		BinaryParameter ret = new BinaryParameter();
		ret.setValue(value);
		return ret;
	}
	
	public static final AccordoServizioParteComune getAccordoFull(String nome, int versione, IDSoggetto idSoggReferente,	AccordiServizioParteComuneCore apcCore)  {
		return evalnull( () -> apcCore.getAccordoServizioFull(IDAccordoFactory.getInstance().getIDAccordoFromValues(nome, idSoggReferente, versione)));
	}
	
	public static final AccordoServizioParteComuneSintetico getAccordoSintetico(String nome, int versione, IDSoggetto idSoggReferente,	AccordiServizioParteComuneCore apcCore)  {
		return evalnull( () -> apcCore.getAccordoServizioSintetico(IDAccordoFactory.getInstance().getIDAccordoFromValues(nome, idSoggReferente, versione)));
	}

	public static final ServiceBinding toManifestServiceBinding(
			org.openspcoop2.core.registry.constants.ServiceBinding serviceBinding) {

		switch(serviceBinding) {
		case REST:
			return ServiceBinding.REST;
		case SOAP:
			return ServiceBinding.SOAP;
		default:
			return null;
		}
			
	}
	

}
