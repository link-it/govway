package org.openspcoop2.core.config.rs.server.api.impl;

import java.lang.reflect.InvocationTargetException;
import java.util.AbstractMap.SimpleEntry;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.ws.rs.core.UriBuilder;

import org.apache.commons.beanutils.BeanUtils;
import org.openspcoop2.core.config.constants.CredenzialeTipo;
import org.openspcoop2.core.config.rs.server.config.ServerProperties;
import org.openspcoop2.core.config.rs.server.model.AuthenticationHttpBasic;
import org.openspcoop2.core.config.rs.server.model.AuthenticationHttps;
import org.openspcoop2.core.config.rs.server.model.AuthenticationPrincipal;
import org.openspcoop2.core.config.rs.server.model.Lista;
import org.openspcoop2.core.config.rs.server.model.ModalitaAccessoEnum;
import org.openspcoop2.core.config.rs.server.model.ProfiloEnum;
import org.openspcoop2.core.registry.constants.StatoFunzionalita;
import org.openspcoop2.utils.UtilsException;
import org.openspcoop2.utils.jaxrs.fault.FaultCode;
import org.openspcoop2.web.ctrlstat.core.Search;
import org.openspcoop2.web.ctrlstat.servlet.connettori.ConnettoriCostanti;
import org.openspcoop2.web.lib.mvc.BinaryParameter;

public class Helper {
	

	public static <T> T findAndRemoveFirst(Iterable<? extends T> collection, Predicate<? super T> test) {
	    T value = null;
	    for (Iterator<? extends T> it = collection.iterator(); it.hasNext();)
	        if (test.test(value = it.next())) {
	            it.remove();
	            return value;
	        }
	    return null;
	}

	public static final Map<ProfiloEnum,String> tipoProtocolloFromProfilo = new HashMap<ProfiloEnum,String>();
	static {
		tipoProtocolloFromProfilo.put(ProfiloEnum.APIGATEWAY, "trasparente");
		tipoProtocolloFromProfilo.put(ProfiloEnum.SPCOOP, "spcoop");
		tipoProtocolloFromProfilo.put(ProfiloEnum.FATTURAPA, "sdi");
		tipoProtocolloFromProfilo.put(ProfiloEnum.EDELIVERY, "as4");
	}
	
	
	public static final Map<String,ProfiloEnum> profiloFromTipoProtocollo = new HashMap<String,ProfiloEnum>();
	static {
		tipoProtocolloFromProfilo.forEach( (mod,prot) -> Helper.profiloFromTipoProtocollo.put(prot, mod));
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
	
		@SuppressWarnings("unchecked")
		LinkedHashMap<String, String> map_creds = (LinkedHashMap<String,String>) creds;
		
		switch (tipoAuth) {
		case HTTP_BASIC: {
			AuthenticationHttpBasic c = new AuthenticationHttpBasic();
			c.setPassword(map_creds.get("password"));
			c.setUsername(map_creds.get("username"));
			ret = c;					
			break;
		}
		case HTTPS: {
			AuthenticationHttps c = new AuthenticationHttps();
			c.setSubject(map_creds.get("subject"));
			ret = c;
			break;
		}
		case PRINCIPAL: {
			AuthenticationPrincipal c = new AuthenticationPrincipal();
			c.setUserid(map_creds.get("userid"));
			ret = c;
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
	 */
	
	// TODO: Manage exceptions.
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static <T,E> T apiCredenzialiToGovwayCred(Object creds, ModalitaAccessoEnum tipoAuth, Class<T> credClass, Class<E> enumClass)
			throws IllegalAccessException, InvocationTargetException, InstantiationException {
		
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
			BeanUtils.setProperty(ret, "subject", auth.getSubject());
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
			auth.setSubject(BeanUtils.getProperty(govwayCreds, "subject"));
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


	public static final Search setupRicercaPaginata(String q, Integer limit, Integer offset, int idLista) {
		Search ricerca = new Search();
		
		if (limit != null)
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
	
	
	public static boolean statoFunzionalitaToBool(StatoFunzionalita sf) {
		if (sf == null) return false;
		
		switch (sf) {
		case ABILITATO: return true;
		case DISABILITATO: return false;
		default: return false;
		}
	}
	
	public static StatoFunzionalita boolToStatoFunzionalita(boolean v) {
		return v ? StatoFunzionalita.ABILITATO : StatoFunzionalita.DISABILITATO;
	}


	public static BinaryParameter toBinaryParameter(byte[] value) {
		BinaryParameter ret = new BinaryParameter();
		ret.setValue(value);
		return ret;
	}
	
	public static<T extends Lista> T costruisciListaPaginata(String requestURI, Integer offset, Integer limit, long total, Class<T> lclass) throws InstantiationException, IllegalAccessException {
		T l = lclass.newInstance();
		
		if (total < 0)
			throw new IllegalArgumentException("Il numero totale di elementi deve essere positivo");
		
		if (offset == null || offset < 0) offset = 0;
		if (limit == null || limit < 0 ) limit = Integer.MAX_VALUE;
		
		if(offset > 0)
			l.setFirst(UriBuilder.fromUri(requestURI).queryParam("offset", 0).build().toString());
		
		if(offset > limit)
        	l.setPrev(UriBuilder.fromUri(requestURI).queryParam("offset", offset - limit).build().toString());
		
		if (limit < total - offset) {
			l.setNext(UriBuilder.fromUri(requestURI).queryParam("offset", offset + limit).build().toString());
			l.setLast(UriBuilder.fromUri(requestURI).queryParam("offset", (total / limit) * limit).build().toString());
		}		
		        
        l.setOffset(offset.longValue());
        l.setLimit(limit == Integer.MAX_VALUE ? 0 : limit);
        l.setTotal(total);
		
		return l;
	}
	
	
	/*public static <T1,T2 extends Object> T2 apiConverter(T1 t1, Class<T2> toClass) throws InstantiationException, IllegalAccessException {
		T2 ret = toClass.newInstance();
		
		if (t1 instanceof StatoFunzionalita ) {
			ret = (T2) statoFunzionalitaToBool( (StatoFunzionalita) t1);
		}
		
		return ret;
	}*/
	

}
