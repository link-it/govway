/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2020 Link.it srl (https://link.it).
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
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang.StringUtils;
import org.openspcoop2.core.commons.Filtri;
import org.openspcoop2.core.config.constants.CredenzialeTipo;
import org.openspcoop2.core.config.rs.server.config.ServerProperties;
import org.openspcoop2.core.config.rs.server.model.AuthenticationHttpBasic;
import org.openspcoop2.core.config.rs.server.model.AuthenticationHttps;
import org.openspcoop2.core.config.rs.server.model.AuthenticationHttpsCertificato;
import org.openspcoop2.core.config.rs.server.model.AuthenticationHttpsConfigurazioneManuale;
import org.openspcoop2.core.config.rs.server.model.AuthenticationPrincipal;
import org.openspcoop2.core.config.rs.server.model.ModalitaAccessoEnum;
import org.openspcoop2.core.config.rs.server.model.OneOfBaseCredenzialiCredenziali;
import org.openspcoop2.core.config.rs.server.model.TipoAutenticazioneHttps;
import org.openspcoop2.core.config.rs.server.model.TipoKeystore;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.core.registry.AccordoServizioParteComune;
import org.openspcoop2.core.registry.beans.AccordoServizioParteComuneSintetico;
import org.openspcoop2.core.registry.constants.StatoFunzionalita;
import org.openspcoop2.core.registry.driver.IDAccordoFactory;
import org.openspcoop2.protocol.manifest.constants.ServiceBinding;
import org.openspcoop2.utils.Utilities;
import org.openspcoop2.utils.UtilsException;
import org.openspcoop2.utils.certificate.ArchiveLoader;
import org.openspcoop2.utils.certificate.ArchiveType;
import org.openspcoop2.utils.certificate.CertificateInfo;
import org.openspcoop2.utils.mime.MimeTypes;
import org.openspcoop2.utils.service.beans.Lista;
import org.openspcoop2.utils.service.beans.ProfiloEnum;
import org.openspcoop2.utils.service.context.IContext;
import org.openspcoop2.utils.service.fault.jaxrs.FaultCode;
import org.openspcoop2.utils.transport.http.HttpConstants;
import org.openspcoop2.web.ctrlstat.core.Search;
import org.openspcoop2.web.ctrlstat.servlet.ConsoleHelper;
import org.openspcoop2.web.ctrlstat.servlet.apc.AccordiServizioParteComuneCore;
import org.openspcoop2.web.ctrlstat.servlet.connettori.ConnettoriCostanti;
import org.openspcoop2.web.lib.mvc.BinaryParameter;
import org.openspcoop2.web.lib.mvc.Costanti;
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
	
	public static void overrideAuthParams(HttpRequestWrapper wrap, ConsoleHelper consoleHelper, OneOfBaseCredenzialiCredenziali credenziali) {
		
		ModalitaAccessoEnum modalitaAccesso = credenziali.getModalitaAccesso();
		if(modalitaAccesso == null) {
			throw FaultCode.RICHIESTA_NON_VALIDA.toException("Modalità di accesso delle credenziali non indicata");
		}
		
		switch(modalitaAccesso) {
		case HTTP_BASIC: {
			
			if(! (credenziali instanceof AuthenticationHttpBasic)) {
				throw FaultCode.RICHIESTA_NON_VALIDA.toException("Credenziali '"+credenziali.getClass().getName()+"' non compatibili con la modalità '+"+modalitaAccesso.toString()+"+'");
			}
			
			AuthenticationHttpBasic c = (AuthenticationHttpBasic) credenziali;
			wrap.overrideParameter(ConnettoriCostanti.PARAMETRO_CREDENZIALI_AUTENTICAZIONE_USERNAME, c.getUsername());
			wrap.overrideParameter(ConnettoriCostanti.PARAMETRO_CREDENZIALI_AUTENTICAZIONE_PASSWORD, c.getPassword());
			if(c.getPassword()!=null && StringUtils.isNotEmpty(c.getPassword())) {
				wrap.overrideParameter(ConnettoriCostanti.PARAMETRO_CREDENZIALI_AUTENTICAZIONE_CHANGE_PASSWORD, 
						Costanti.CHECK_BOX_CONFIG_ENABLE);
			}
			
			break;
		}
		case HTTPS: {
			
			if(! (credenziali instanceof AuthenticationHttps)) {
				throw FaultCode.RICHIESTA_NON_VALIDA.toException("Credenziali '"+credenziali.getClass().getName()+"' non compatibili con la modalità '+"+modalitaAccesso.toString()+"+'");
			}
						
			AuthenticationHttps c = (AuthenticationHttps) credenziali;
			wrap.overrideParameter(ConnettoriCostanti.PARAMETRO_CREDENZIALI_AUTENTICAZIONE_CONFIGURAZIONE_SSL_WIZARD_STEP,
					ConnettoriCostanti.VALUE_PARAMETRO_CREDENZIALI_AUTENTICAZIONE_CONFIGURAZIONE_SSL_NO_WIZARD);
			
			switch (c.getCertificato().getTipo()) {
			case CERTIFICATO:
				
				if(c.getCertificato() instanceof AuthenticationHttpsCertificato) {
				
					wrap.overrideParameter(ConnettoriCostanti.PARAMETRO_CREDENZIALI_AUTENTICAZIONE_CONFIGURAZIONE_SSL,
							ConnettoriCostanti.VALUE_PARAMETRO_CREDENZIALI_AUTENTICAZIONE_CONFIGURAZIONE_SSL_UPLOAD_CERTIFICATO);
					AuthenticationHttpsCertificato certificate = (AuthenticationHttpsCertificato) c.getCertificato();
					consoleHelper.registerBinaryParameter(ConnettoriCostanti.PARAMETRO_CREDENZIALI_AUTENTICAZIONE_CONFIGURAZIONE_SSL_FILE_CERTIFICATO, certificate.getArchivio());
					wrap.overrideParameter(ConnettoriCostanti.PARAMETRO_CREDENZIALI_AUTENTICAZIONE_CONFIGURAZIONE_SSL_TIPO_ARCHIVIO, certificate.getTipoCertificato().toString());
					wrap.overrideParameter(ConnettoriCostanti.PARAMETRO_CREDENZIALI_AUTENTICAZIONE_CONFIGURAZIONE_SSL_ALIAS_CERTIFICATO, certificate.getAlias());
					wrap.overrideParameter(ConnettoriCostanti.PARAMETRO_CREDENZIALI_AUTENTICAZIONE_CONFIGURAZIONE_SSL_FILE_CERTIFICATO_PASSWORD, certificate.getPassword());
					
					wrap.overrideParameter(ConnettoriCostanti.PARAMETRO_CREDENZIALI_AUTENTICAZIONE_CONFIGURAZIONE_SSL_VERIFICA_TUTTI_CAMPI, 
							certificate.isStrictVerification()? Costanti.CHECK_BOX_ENABLED : Costanti.CHECK_BOX_CONFIG_DISABLE);
					
				}
				else {
					throw FaultCode.RICHIESTA_NON_VALIDA.toException("Certificato fornito '"+c.getCertificato().getClass().getName()+"' non compatibile con il tipo '"+c.getCertificato().getTipo()+"' ");
				}
				
				break;
			case CONFIGURAZIONE_MANUALE:
				
				if(c.getCertificato() instanceof AuthenticationHttpsConfigurazioneManuale) {
					
					wrap.overrideParameter(ConnettoriCostanti.PARAMETRO_CREDENZIALI_AUTENTICAZIONE_CONFIGURAZIONE_SSL,
							ConnettoriCostanti.VALUE_PARAMETRO_CREDENZIALI_AUTENTICAZIONE_CONFIGURAZIONE_SSL_CONFIGURAZIONE_MANUALE);
					AuthenticationHttpsConfigurazioneManuale creManuale = (AuthenticationHttpsConfigurazioneManuale) c.getCertificato();
					wrap.overrideParameter(ConnettoriCostanti.PARAMETRO_CREDENZIALI_AUTENTICAZIONE_SUBJECT, creManuale.getSubject());
					wrap.overrideParameter(ConnettoriCostanti.PARAMETRO_CREDENZIALI_AUTENTICAZIONE_ISSUER, creManuale.getIssuer());
					
				}
				else {
					throw FaultCode.RICHIESTA_NON_VALIDA.toException("Certificato fornito '"+c.getCertificato().getClass().getName()+"' non compatibile con il tipo '"+c.getCertificato().getTipo()+"' ");
				}
				
				break;
			}
			break;
		}
		case PRINCIPAL: {
			
			if(! (credenziali instanceof AuthenticationPrincipal)) {
				throw FaultCode.RICHIESTA_NON_VALIDA.toException("Credenziali '"+credenziali.getClass().getName()+"' non compatibili con la modalità '+"+modalitaAccesso.toString()+"+'");
			}
			
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
	public static OneOfBaseCredenzialiCredenziali translateCredenziali(OneOfBaseCredenzialiCredenziali creds, boolean create) {
		
		if(creds==null || creds.getModalitaAccesso()==null) {
			return null;
		}
		
		OneOfBaseCredenzialiCredenziali ret = null;
		ModalitaAccessoEnum tipoAuth = creds.getModalitaAccesso();
	
		switch (tipoAuth) {
		case HTTP_BASIC: {
			ret = (AuthenticationHttpBasic) creds;
			if(create) {
				AuthenticationHttpBasic basic = (AuthenticationHttpBasic) ret;
				if(basic.getPassword()==null || StringUtils.isEmpty(basic.getPassword())) {
					throw FaultCode.RICHIESTA_NON_VALIDA.toException("In una operazione 'create' con tipo di autenticazione '"+tipoAuth+"' è obbligatorio indicare la password");
				}
			}
			break;
		}
		case HTTPS: {
			ret = (AuthenticationHttps) creds;
			break;
		}
		case PRINCIPAL: {
			ret = (AuthenticationPrincipal) creds;
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
		
		T ret = Utilities.newInstance(credClass);
		
		switch (tipoAuth) {
		case HTTP_BASIC: {
			
			if(! (creds instanceof AuthenticationHttpBasic)) {
				throw FaultCode.RICHIESTA_NON_VALIDA.toException("Credenziali '"+creds.getClass().getName()+"' non compatibili con la modalità '+"+tipoAuth.toString()+"+'");
			}
			
			AuthenticationHttpBasic auth = (AuthenticationHttpBasic) creds;
			BeanUtils.setProperty(ret, "tipo", Enum.valueOf( (Class<Enum>)enumClass, "BASIC"));
			BeanUtils.setProperty(ret, "user", auth.getUsername());
			BeanUtils.setProperty(ret, "password", auth.getPassword());
			break;
		}
		case HTTPS: {
			
			if(! (creds instanceof AuthenticationHttps)) {
				throw FaultCode.RICHIESTA_NON_VALIDA.toException("Credenziali '"+creds.getClass().getName()+"' non compatibili con la modalità '+"+tipoAuth.toString()+"+'");
			}
			
			AuthenticationHttps auth = (AuthenticationHttps) creds;
			BeanUtils.setProperty(ret, "tipo", Enum.valueOf( (Class<Enum>)enumClass, "SSL"));
			switch (auth.getCertificato().getTipo()) {
			case CERTIFICATO:
				AuthenticationHttpsCertificato cre = (AuthenticationHttpsCertificato) auth.getCertificato();
				ArchiveType type = ArchiveType.CER;
				String alias = cre.getAlias();
				String password = cre.getPassword();
				if(cre.getTipoCertificato()!=null) {
					type = ArchiveType.valueOf(cre.getTipoCertificato().toString());
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
			
			if(! (creds instanceof AuthenticationPrincipal)) {
				throw FaultCode.RICHIESTA_NON_VALIDA.toException("Credenziali '"+creds.getClass().getName()+"' non compatibili con la modalità '+"+tipoAuth.toString()+"+'");
			}
			
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

	
	public static final <T,E> OneOfBaseCredenzialiCredenziali govwayCredenzialiToApi(Object govwayCreds, Class<T> credClass, Class<E> enumClass) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		
		if (!enumClass.isEnum())
			throw new IllegalArgumentException("Il parametro enumClass deve essere un'enumerazione");
		
		OneOfBaseCredenzialiCredenziali ret = null;		
		
		// Questo è il caso di autenticazione di tipo NESSUNO.
		if (govwayCreds == null)
			return ret;
		
		String tipo = BeanUtils.getProperty(govwayCreds, "tipo");
		
		if ("basic".equals(tipo)) {
			AuthenticationHttpBasic auth = new AuthenticationHttpBasic();
			auth.setUsername(BeanUtils.getProperty(govwayCreds, "user"));
			
			Method mCertificateStrictVerification = credClass.getMethod("isCertificateStrictVerification");
			boolean cifrata = (Boolean) mCertificateStrictVerification.invoke(govwayCreds);
			if(!cifrata) {
				auth.setPassword(BeanUtils.getProperty(govwayCreds, "password"));
			}
			auth.setModalitaAccesso(ModalitaAccessoEnum.HTTP_BASIC);
			ret = auth;
		}
		else if ("ssl".equals(tipo)) {
			AuthenticationHttps auth = new AuthenticationHttps();
			Method mCertificate = credClass.getMethod("getCertificate");
			Object oCertificate = mCertificate.invoke(govwayCreds);
			if(oCertificate==null) {
				AuthenticationHttpsConfigurazioneManuale manuale = new AuthenticationHttpsConfigurazioneManuale();
				manuale.setTipo(TipoAutenticazioneHttps.CONFIGURAZIONE_MANUALE);
				manuale.setSubject(BeanUtils.getProperty(govwayCreds, "subject"));	
				manuale.setIssuer(BeanUtils.getProperty(govwayCreds, "issuer"));	
				auth.setCertificato(manuale);
			}
			else {
				AuthenticationHttpsCertificato certificato = new AuthenticationHttpsCertificato();
				certificato.setTipo(TipoAutenticazioneHttps.CERTIFICATO);
				certificato.setTipoCertificato(TipoKeystore.CER);
				certificato.setArchivio((byte[])oCertificate);
				Method mCertificateStrictVerification = credClass.getMethod("isCertificateStrictVerification");
				certificato.setStrictVerification((Boolean) mCertificateStrictVerification.invoke(govwayCreds));
				auth.setCertificato(certificato);
			}
			auth.setModalitaAccesso(ModalitaAccessoEnum.HTTPS);
			ret = auth;
		}
		else if ("principal".equals(tipo)) {
			AuthenticationPrincipal auth = new AuthenticationPrincipal();
			auth.setUserid(BeanUtils.getProperty(govwayCreds, "user"));
			auth.setModalitaAccesso(ModalitaAccessoEnum.PRINCIPAL);
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
	
	public static void setContentType(IContext context, String fileName) throws UtilsException {
		String mimeType = null;
		if(fileName.contains(".")){
			String ext = null;
			try{
				ext = fileName.substring(fileName.lastIndexOf(".")+1,fileName.length());
			}catch(Exception e){}
			MimeTypes mimeTypes = MimeTypes.getInstance();
			if(ext!=null && mimeTypes.existsExtension(ext)){
				mimeType = mimeTypes.getMimeType(ext);
				//System.out.println("CUSTOM ["+mimeType+"]");		
			}
			else{
				mimeType = HttpConstants.CONTENT_TYPE_X_DOWNLOAD;
			}
		}
		else{
			mimeType = HttpConstants.CONTENT_TYPE_X_DOWNLOAD;
		}
		
		context.getServletResponse().setContentType(mimeType);
	}

}
