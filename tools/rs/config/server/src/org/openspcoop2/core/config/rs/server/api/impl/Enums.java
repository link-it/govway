/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2022 Link.it srl (https://link.it).
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

import java.util.HashMap;
import java.util.Map;

import org.openspcoop2.core.config.constants.TipoAutenticazione;
import org.openspcoop2.core.config.constants.TipoGestioneCORS;
import org.openspcoop2.core.config.rs.server.model.ContestoEnum;
import org.openspcoop2.core.config.rs.server.model.FonteEnum;
import org.openspcoop2.core.config.rs.server.model.FormatoRestEnum;
import org.openspcoop2.core.config.rs.server.model.FormatoSoapEnum;
import org.openspcoop2.core.config.rs.server.model.RateLimitingChiaveEnum;
import org.openspcoop2.core.config.rs.server.model.RuoloAllegatoAPI;
import org.openspcoop2.core.config.rs.server.model.RuoloAllegatoAPIImpl;
import org.openspcoop2.core.config.rs.server.model.TipoApiEnum;
import org.openspcoop2.core.config.rs.server.model.TipoAutenticazioneEnum;
import org.openspcoop2.core.config.rs.server.model.TipoAutenticazionePrincipal;
import org.openspcoop2.core.config.rs.server.model.TipoGestioneCorsEnum;
import org.openspcoop2.core.config.rs.server.model.TipoSpecificaLivelloServizioEnum;
import org.openspcoop2.core.config.rs.server.model.TipoSpecificaSemiformaleEnum;
import org.openspcoop2.core.config.rs.server.model.TipoSpecificaSicurezzaEnum;
import org.openspcoop2.core.controllo_traffico.constants.TipoFiltroApplicativo;
import org.openspcoop2.core.registry.constants.CredenzialeTipo;
import org.openspcoop2.core.registry.constants.FormatoSpecifica;
import org.openspcoop2.core.registry.constants.ProfiloCollaborazione;
import org.openspcoop2.core.registry.constants.RuoliDocumento;
import org.openspcoop2.core.registry.constants.RuoloContesto;
import org.openspcoop2.core.registry.constants.RuoloTipologia;
import org.openspcoop2.core.registry.constants.ScopeContesto;
import org.openspcoop2.core.registry.constants.TipiDocumentoLivelloServizio;
import org.openspcoop2.core.registry.constants.TipiDocumentoSemiformale;
import org.openspcoop2.core.registry.constants.TipiDocumentoSicurezza;
import org.openspcoop2.message.constants.ServiceBinding;
import org.openspcoop2.protocol.manifest.constants.InterfaceType;
import org.openspcoop2.utils.service.beans.ProfiloCollaborazioneEnum;

/**
 * Enums
 * 
 * @author $Author$
 * @version $Rev$, $Date$
 * 
 */
public class Enums {
	
	public static final <E extends Enum<E>> E fromValue(Class<E> enumClass, String value) {
	
		E[] constants = enumClass.getEnumConstants();
		if  (constants == null)
			throw new IllegalArgumentException("La classe passata non è un'enumerazione");
		
		for ( E e : constants) {
			if (String.valueOf(e.toString()).equals(value.trim())) {
				return e;
			}
		}
		
		return null;
	}
	

	//RuoloAllegatoAPIImpl -> RuoiliDocumento
	//public static final Map<RuoloAllegatoAPIImpl,RuoliDocumento> 
	
	public static final Map<FonteEnum, RuoloTipologia> ruoloTipologiaFromRest = new HashMap<FonteEnum, RuoloTipologia>();
	static {
		Enums.ruoloTipologiaFromRest.put(FonteEnum.ESTERNA, RuoloTipologia.ESTERNO);
		Enums.ruoloTipologiaFromRest.put(FonteEnum.QUALSIASI, RuoloTipologia.QUALSIASI);
		Enums.ruoloTipologiaFromRest.put(FonteEnum.REGISTRO, RuoloTipologia.INTERNO);
	}
	
	
	public static final Map<TipoAutenticazioneEnum, TipoAutenticazione> tipoAutenticazioneFromRest = new HashMap<TipoAutenticazioneEnum, TipoAutenticazione>();
	static {
		Enums.tipoAutenticazioneFromRest.put(TipoAutenticazioneEnum.DISABILITATO, TipoAutenticazione.DISABILITATO);
		Enums.tipoAutenticazioneFromRest.put(TipoAutenticazioneEnum.HTTP_BASIC, TipoAutenticazione.BASIC);
		Enums.tipoAutenticazioneFromRest.put(TipoAutenticazioneEnum.HTTPS, TipoAutenticazione.SSL);
		Enums.tipoAutenticazioneFromRest.put(TipoAutenticazioneEnum.PRINCIPAL, TipoAutenticazione.PRINCIPAL);
		Enums.tipoAutenticazioneFromRest.put(TipoAutenticazioneEnum.API_KEY, TipoAutenticazione.APIKEY);
	}
	
	public static final Map<TipoAutenticazionePrincipal, org.openspcoop2.core.config.constants.TipoAutenticazionePrincipal> tipoAutenticazionePrincipalFromRest = new HashMap<>();
	static {
		Enums.tipoAutenticazionePrincipalFromRest.put(TipoAutenticazionePrincipal.CONTAINER, org.openspcoop2.core.config.constants.TipoAutenticazionePrincipal.CONTAINER);
		Enums.tipoAutenticazionePrincipalFromRest.put(TipoAutenticazionePrincipal.FORM_BASED, org.openspcoop2.core.config.constants.TipoAutenticazionePrincipal.FORM);
		Enums.tipoAutenticazionePrincipalFromRest.put(TipoAutenticazionePrincipal.HEADER_BASED, org.openspcoop2.core.config.constants.TipoAutenticazionePrincipal.HEADER);
		Enums.tipoAutenticazionePrincipalFromRest.put(TipoAutenticazionePrincipal.IP_ADDRESS, org.openspcoop2.core.config.constants.TipoAutenticazionePrincipal.INDIRIZZO_IP);
		// TODO: aggiungere all'API
		Enums.tipoAutenticazionePrincipalFromRest.put(TipoAutenticazionePrincipal.IP_ADDRESS_FORWARDED_FOR, org.openspcoop2.core.config.constants.TipoAutenticazionePrincipal.INDIRIZZO_IP_X_FORWARDED_FOR);
		Enums.tipoAutenticazionePrincipalFromRest.put(TipoAutenticazionePrincipal.URL_BASED, org.openspcoop2.core.config.constants.TipoAutenticazionePrincipal.URL);
		Enums.tipoAutenticazionePrincipalFromRest.put(TipoAutenticazionePrincipal.TOKEN, org.openspcoop2.core.config.constants.TipoAutenticazionePrincipal.TOKEN);
	}
	
	public static final <T1,T2> Map<T1,T2> dualizeMap(Map<T2,T1> map) {
		HashMap<T1,T2> ret = new HashMap<T1,T2>();
		
		map.forEach( (t2, t1) -> ret.put(t1, t2));
		
		return ret;
	}
	
	
	public static final Map<TipoApiEnum,ServiceBinding> serviceBindingFromTipo = new HashMap<TipoApiEnum,ServiceBinding>();
	static {
		Enums.serviceBindingFromTipo.put(TipoApiEnum.REST, ServiceBinding.REST);
		Enums.serviceBindingFromTipo.put(TipoApiEnum.SOAP, ServiceBinding.SOAP);
	}
	
	
	public static final Map<FormatoSoapEnum,FormatoSpecifica> formatoSpecificaFromSoap = new HashMap<FormatoSoapEnum,FormatoSpecifica>();
	static {
		Enums.formatoSpecificaFromSoap.put(FormatoSoapEnum._1, FormatoSpecifica.WSDL_11);
	}
	
	
	public static final Map<FormatoSpecifica,FormatoSoapEnum> formatoSoapFromSpecifica = new HashMap<FormatoSpecifica,FormatoSoapEnum>();
	static {
		Enums.formatoSpecificaFromSoap.forEach( (soapenum, fspec) -> Enums.formatoSoapFromSpecifica.put(fspec,soapenum) );
	}
	
	
	public static final Map<FormatoRestEnum,FormatoSpecifica> formatoSpecificaFromRest = new HashMap<FormatoRestEnum,FormatoSpecifica>();
	static {
		Enums.formatoSpecificaFromRest.put(FormatoRestEnum.OPENAPI3_0, FormatoSpecifica.OPEN_API_3);
		Enums.formatoSpecificaFromRest.put(FormatoRestEnum.SWAGGER2_0, FormatoSpecifica.SWAGGER_2);
		Enums.formatoSpecificaFromRest.put(FormatoRestEnum.WADL, FormatoSpecifica.WADL);
	}
	
	
	public static final Map<FormatoSpecifica,FormatoRestEnum> formatoRestFromSpecifica = new HashMap<FormatoSpecifica,FormatoRestEnum>();
	static {
		Enums.formatoSpecificaFromRest.forEach( (fr, fs) -> Enums.formatoRestFromSpecifica.put(fs, fr));
	}
		

	public static final Map<FormatoSpecifica,InterfaceType> interfaceTypeFromFormatoSpecifica = new HashMap<FormatoSpecifica,InterfaceType>();
	static {
		Enums.interfaceTypeFromFormatoSpecifica.put(FormatoSpecifica.OPEN_API_3, InterfaceType.OPEN_API_3);
		Enums.interfaceTypeFromFormatoSpecifica.put(FormatoSpecifica.SWAGGER_2, InterfaceType.SWAGGER_2);
		Enums.interfaceTypeFromFormatoSpecifica.put(FormatoSpecifica.WADL, InterfaceType.WADL);
		Enums.interfaceTypeFromFormatoSpecifica.put(FormatoSpecifica.WSDL_11, InterfaceType.WSDL_11);
	}
	
	
	public static final Map<RuoloAllegatoAPI,RuoliDocumento> ruoliDocumentoFromApi = new HashMap<RuoloAllegatoAPI,RuoliDocumento>();
	static {
		Enums.ruoliDocumentoFromApi.put(RuoloAllegatoAPI.ALLEGATO, RuoliDocumento.allegato);
		Enums.ruoliDocumentoFromApi.put(RuoloAllegatoAPI.SPECIFICASEMIFORMALE, RuoliDocumento.specificaSemiformale);
	}
	
	
	public static final Map<RuoliDocumento,RuoloAllegatoAPI> ruoliApiFromDocumento = new HashMap<RuoliDocumento,RuoloAllegatoAPI>();
	static {
		Enums.ruoliDocumentoFromApi.forEach( (ra,rd) -> Enums.ruoliApiFromDocumento.put(rd, ra));
	}
	
	public static final Map<RuoloAllegatoAPIImpl,RuoliDocumento> ruoliDocumentoFromApiImpl = new HashMap<RuoloAllegatoAPIImpl,RuoliDocumento>();
	static {
		Enums.ruoliDocumentoFromApiImpl.put(RuoloAllegatoAPIImpl.ALLEGATO, RuoliDocumento.allegato);
		Enums.ruoliDocumentoFromApiImpl.put(RuoloAllegatoAPIImpl.SPECIFICASEMIFORMALE, RuoliDocumento.specificaSemiformale);
		Enums.ruoliDocumentoFromApiImpl.put(RuoloAllegatoAPIImpl.SPECIFICASICUREZZA, RuoliDocumento.specificaSicurezza);
		Enums.ruoliDocumentoFromApiImpl.put(RuoloAllegatoAPIImpl.SPECIFICALIVELLOSERVIZIO, RuoliDocumento.specificaLivelloServizio);
	}
	
	
	
	public static final Map<RuoliDocumento,RuoloAllegatoAPIImpl> ruoliApiImplFromDocumento = new HashMap<RuoliDocumento,RuoloAllegatoAPIImpl>();
	static {
		Enums.ruoliDocumentoFromApiImpl.forEach( (ra,rd) -> Enums.ruoliApiImplFromDocumento.put(rd, ra));
	}


	
	public static final Map<TipoSpecificaSemiformaleEnum,TipiDocumentoSemiformale> tipoDocumentoSemiFormaleFromSpecifica = new HashMap<TipoSpecificaSemiformaleEnum,TipiDocumentoSemiformale>();
	static {
		Enums.tipoDocumentoSemiFormaleFromSpecifica.put(TipoSpecificaSemiformaleEnum.HTML, TipiDocumentoSemiformale.HTML);
		Enums.tipoDocumentoSemiFormaleFromSpecifica.put(TipoSpecificaSemiformaleEnum.JSON, TipiDocumentoSemiformale.JSON);
		Enums.tipoDocumentoSemiFormaleFromSpecifica.put(TipoSpecificaSemiformaleEnum.LINGUAGGIO_NATURALE, TipiDocumentoSemiformale.LINGUAGGIO_NATURALE);
		Enums.tipoDocumentoSemiFormaleFromSpecifica.put(TipoSpecificaSemiformaleEnum.UML, TipiDocumentoSemiformale.UML);
		Enums.tipoDocumentoSemiFormaleFromSpecifica.put(TipoSpecificaSemiformaleEnum.XML, TipiDocumentoSemiformale.XML);
		Enums.tipoDocumentoSemiFormaleFromSpecifica.put(TipoSpecificaSemiformaleEnum.XSD, TipiDocumentoSemiformale.XSD);
		Enums.tipoDocumentoSemiFormaleFromSpecifica.put(TipoSpecificaSemiformaleEnum.YAML, TipiDocumentoSemiformale.YAML);
	}
	
	public static final Map<TipoSpecificaLivelloServizioEnum,TipiDocumentoLivelloServizio> tipoDocumentoLivelloServizioFromSpecifica = new HashMap<TipoSpecificaLivelloServizioEnum,TipiDocumentoLivelloServizio>();
	static {
		Enums.tipoDocumentoLivelloServizioFromSpecifica.put(TipoSpecificaLivelloServizioEnum.WS_AGREEMENT, TipiDocumentoLivelloServizio.WSAGREEMENT);
		Enums.tipoDocumentoLivelloServizioFromSpecifica.put(TipoSpecificaLivelloServizioEnum.WSLA, TipiDocumentoLivelloServizio.WSLA);
	}
	
	public static final Map<TipoSpecificaSicurezzaEnum,TipiDocumentoSicurezza> tipoDocumentoSicurezzaFromSpecifica = new HashMap<TipoSpecificaSicurezzaEnum,TipiDocumentoSicurezza>();
	static {
		Enums.tipoDocumentoSicurezzaFromSpecifica.put(TipoSpecificaSicurezzaEnum.WS_POLICY, TipiDocumentoSicurezza.WSPOLICY);
		Enums.tipoDocumentoSicurezzaFromSpecifica.put(TipoSpecificaSicurezzaEnum.XACML_POLICY, TipiDocumentoSicurezza.XACML_POLICY);
		Enums.tipoDocumentoSicurezzaFromSpecifica.put(TipoSpecificaSicurezzaEnum.LINGUAGGIO_NATURALE, TipiDocumentoSicurezza.LINGUAGGIO_NATURALE);
	}
	
	
	public static final Map<ProfiloCollaborazioneEnum,ProfiloCollaborazione> profiloCollaborazioneFromApiEnum = new HashMap<ProfiloCollaborazioneEnum,ProfiloCollaborazione>();
	static {
		Enums.profiloCollaborazioneFromApiEnum.put(ProfiloCollaborazioneEnum.ASINCRONOASIMMETRICO, ProfiloCollaborazione.ASINCRONO_ASIMMETRICO);
		Enums.profiloCollaborazioneFromApiEnum.put(ProfiloCollaborazioneEnum.ASINCRONOSIMMETRICO, ProfiloCollaborazione.ASINCRONO_SIMMETRICO);
		Enums.profiloCollaborazioneFromApiEnum.put(ProfiloCollaborazioneEnum.ONEWAY, ProfiloCollaborazione.ONEWAY);
		Enums.profiloCollaborazioneFromApiEnum.put(ProfiloCollaborazioneEnum.SINCRONO, ProfiloCollaborazione.SINCRONO);
	}
	
	
	public static final Map<ProfiloCollaborazione,ProfiloCollaborazioneEnum> profiloCollaborazioneApiFromRegistro = new HashMap<ProfiloCollaborazione,ProfiloCollaborazioneEnum>();
	static {
		Enums.profiloCollaborazioneFromApiEnum.forEach( (a,r) -> Enums.profiloCollaborazioneApiFromRegistro.put(r, a));
	}


	public static final Map<ContestoEnum,ScopeContesto> apiContestoToRegistroContesto = new HashMap<>();
	static {
		Enums.apiContestoToRegistroContesto.put(ContestoEnum.EROGAZIONE,ScopeContesto.PORTA_APPLICATIVA);
		Enums.apiContestoToRegistroContesto.put(ContestoEnum.FRUIZIONE,ScopeContesto.PORTA_DELEGATA);
		Enums.apiContestoToRegistroContesto.put(ContestoEnum.QUALSIASI,ScopeContesto.QUALSIASI);
	}
	
	
	public static final Map<ScopeContesto,ContestoEnum> registroContestoToApiContesto = new HashMap<>();
	static {
		Enums.apiContestoToRegistroContesto.forEach( (ac,rc) -> Enums.registroContestoToApiContesto.put(rc, ac));
	}
	
	
	public static RuoloContesto apiContestoToRegistroContesto(ContestoEnum c) {
		switch(c) {
		case EROGAZIONE:	return RuoloContesto.PORTA_APPLICATIVA;
		case FRUIZIONE:  	return RuoloContesto.PORTA_DELEGATA;
		case QUALSIASI: 	return RuoloContesto.QUALSIASI;
		default: throw new IllegalArgumentException("Contesto di configurazione ruoli sconosciuto: " + c.toString());
		}
	}


	public static ContestoEnum registroContestoToApiContesto(RuoloContesto c) {
		switch(c) {
		case PORTA_APPLICATIVA: return ContestoEnum.EROGAZIONE;
		case PORTA_DELEGATA: return ContestoEnum.FRUIZIONE;
		case QUALSIASI: return ContestoEnum.QUALSIASI;
		default: throw new IllegalArgumentException("Contesto di registro ruoli sconosciuto: " + c.toString());
		}
	}


	public static FonteEnum registroTipologiaToApiFonte(RuoloTipologia tipo) {
		switch (tipo) {
		case ESTERNO: return FonteEnum.ESTERNA;
		case INTERNO: return FonteEnum.REGISTRO;
		case QUALSIASI: return FonteEnum.QUALSIASI;
		default: throw new IllegalArgumentException("TipologiaRuolo sconociuta: " + tipo.toString());
		}
	}


	public static RuoloTipologia apiFonteToRegistroTipologia(FonteEnum fonte) {
		switch (fonte) {
		case ESTERNA: return RuoloTipologia.ESTERNO;
		case REGISTRO: return RuoloTipologia.INTERNO;
		case QUALSIASI: return RuoloTipologia.QUALSIASI;
		default: throw new IllegalArgumentException("Fonte del ruolo sconosciuta: " + fonte.toString());
		}
	}
	
	

	public static final Map<TipoGestioneCorsEnum,TipoGestioneCORS> tipoGestioneCorsFromRest = new HashMap<TipoGestioneCorsEnum,TipoGestioneCORS>();
	static {
		Enums.tipoGestioneCorsFromRest.put(TipoGestioneCorsEnum.APPLICATIVO, TipoGestioneCORS.TRASPARENTE);
		Enums.tipoGestioneCorsFromRest.put(TipoGestioneCorsEnum.GATEWAY, TipoGestioneCORS.GATEWAY);
	}
	
	
	public static final Map<RateLimitingChiaveEnum,TipoFiltroApplicativo> tipoFiltroApplicativo = new HashMap<RateLimitingChiaveEnum,TipoFiltroApplicativo>();
	static {
		tipoFiltroApplicativo.put(RateLimitingChiaveEnum.CONTENT_BASED, TipoFiltroApplicativo.CONTENT_BASED);
		tipoFiltroApplicativo.put(RateLimitingChiaveEnum.FORM_BASED, TipoFiltroApplicativo.FORM_BASED);
		tipoFiltroApplicativo.put(RateLimitingChiaveEnum.HEADER_BASED, TipoFiltroApplicativo.HEADER_BASED);
		tipoFiltroApplicativo.put(RateLimitingChiaveEnum.PLUGIN_BASED, TipoFiltroApplicativo.PLUGIN_BASED);
		tipoFiltroApplicativo.put(RateLimitingChiaveEnum.SOAP_ACTION_BASED, TipoFiltroApplicativo.SOAPACTION_BASED);
		tipoFiltroApplicativo.put(RateLimitingChiaveEnum.INDIRIZZO_IP, TipoFiltroApplicativo.INDIRIZZO_IP);
		tipoFiltroApplicativo.put(RateLimitingChiaveEnum.INDIRIZZO_IP_FORWARDED, TipoFiltroApplicativo.INDIRIZZO_IP_FORWARDED);
		tipoFiltroApplicativo.put(RateLimitingChiaveEnum.URL_BASED, TipoFiltroApplicativo.URLBASED);
	}
	
	public static final Map<TipoFiltroApplicativo,RateLimitingChiaveEnum> rateLimitingChiaveEnum = new HashMap<TipoFiltroApplicativo,RateLimitingChiaveEnum>();
	static {
		tipoFiltroApplicativo.forEach( (a,r) -> rateLimitingChiaveEnum.put(r, a));
	}
	
	public static final Map<TipoAutenticazioneEnum, CredenzialeTipo> credenzialeTipoFromTipoAutenticazione = new HashMap<TipoAutenticazioneEnum, CredenzialeTipo>();
	static {
		credenzialeTipoFromTipoAutenticazione.put(TipoAutenticazioneEnum.HTTP_BASIC, CredenzialeTipo.BASIC);
		credenzialeTipoFromTipoAutenticazione.put(TipoAutenticazioneEnum.HTTPS,  CredenzialeTipo.SSL);
		credenzialeTipoFromTipoAutenticazione.put(TipoAutenticazioneEnum.PRINCIPAL, CredenzialeTipo.PRINCIPAL);
		credenzialeTipoFromTipoAutenticazione.put(TipoAutenticazioneEnum.API_KEY, CredenzialeTipo.APIKEY);
	}

	public static final TipoAutenticazione toTipoAutenticazione(TipoAutenticazioneEnum authn) {
		if (authn == null) return null;
		
		switch(authn) {
		case DISABILITATO: return TipoAutenticazione.DISABILITATO;
		case HTTP_BASIC: return TipoAutenticazione.BASIC;
		case HTTPS: return TipoAutenticazione.SSL;
		case PRINCIPAL: return TipoAutenticazione.PRINCIPAL;
		case API_KEY: return TipoAutenticazione.APIKEY;
		default: return null;
		}
		
	}
	
}
