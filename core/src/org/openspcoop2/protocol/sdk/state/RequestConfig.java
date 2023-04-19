/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2023 Link.it srl (https://link.it). 
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

package org.openspcoop2.protocol.sdk.state;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.openspcoop2.core.config.PortaApplicativa;
import org.openspcoop2.core.config.PortaDelegata;
import org.openspcoop2.core.config.ServizioApplicativo;
import org.openspcoop2.core.config.SystemProperties;
import org.openspcoop2.core.id.IDPortaApplicativa;
import org.openspcoop2.core.id.IDPortaDelegata;
import org.openspcoop2.core.id.IDServizio;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.core.mapping.MappingErogazionePortaApplicativa;
import org.openspcoop2.core.mapping.MappingFruizionePortaDelegata;
import org.openspcoop2.core.registry.AccordoServizioParteComune;
import org.openspcoop2.core.registry.AccordoServizioParteSpecifica;
import org.openspcoop2.core.registry.Documento;
import org.openspcoop2.core.registry.PortaDominio;
import org.openspcoop2.core.registry.Ruolo;
import org.openspcoop2.core.registry.Scope;
import org.openspcoop2.core.transazioni.CredenzialeMittente;
import org.openspcoop2.core.transazioni.utils.TipoCredenzialeMittente;
import org.openspcoop2.protocol.sdk.Servizio;
import org.openspcoop2.protocol.sdk.constants.InformationApiSource;
import org.openspcoop2.protocol.sdk.registry.ProtocolFiltroRicercaPorteApplicative;
import org.openspcoop2.protocol.sdk.registry.ProtocolFiltroRicercaPorteDelegate;
import org.openspcoop2.utils.BooleanNullable;


/**
 * RequestConfig
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class RequestConfig implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String key = null;
	
	private boolean cached = false;

	private transient org.openspcoop2.utils.Semaphore semaphore = null; // possono essere alimentati da thread differenti
	
	private IDSoggetto dominioDefault;
	
	private IDServizio idServizio;
	private org.openspcoop2.core.registry.constants.ServiceBinding serviceBinding = null;
	private AccordoServizioParteComune aspc = null;
	private AccordoServizioParteSpecifica asps = null;
	private Map<String, org.openspcoop2.core.registry.wsdl.AccordoServizioWrapper> asWrapperSoap = null;
	private Map<String, org.openspcoop2.core.registry.rest.AccordoServizioWrapper> asWrapperRest = null;
	private Servizio infoServizio;
	private Servizio infoServizioCorrelato;
	private Servizio infoServizioAzioneCorrelata;
	private String servizioVersioneProtocollo;
	
	private transient org.openspcoop2.utils.Semaphore semaphoreAllegatoApi = null; // possono essere alimentati da thread differenti
	private Map<String, Documento> allegatoApi = null;
	private transient org.openspcoop2.utils.Semaphore semaphoreAllegatoServizio = null; // possono essere alimentati da thread differenti
	private Map<String, Documento> allegatoServizio = null;
	
	private org.openspcoop2.core.registry.Soggetto soggettoErogatoreRegistry;
	private org.openspcoop2.core.config.Soggetto soggettoErogatoreConfig;
	private String soggettoErogatoreIdentificativoPorta;
	private Boolean soggettoErogatoreSoggettoVirtuale;
	private String soggettoErogatoreImplementazionePdd;
	private Boolean soggettoErogatorePddReaded;
	private PortaDominio soggettoErogatorePdd;
	private String soggettoErogatoreVersioneProtocollo;
	
	private IDPortaApplicativa idPortaApplicativaDefault;
	private PortaApplicativa portaApplicativaDefault;
	private IDPortaApplicativa idPortaApplicativa;
	private PortaApplicativa portaApplicativa;
	private List<MappingErogazionePortaApplicativa> listMappingErogazionePortaApplicativa;
	private Map<String, ServizioApplicativo> serviziApplicativiErogatore = null;
	
	private transient org.openspcoop2.utils.Semaphore semaphorePorteApplicativeByFiltroRicerca = null; // possono essere alimentati da thread differenti
	private Map<String, List<IDPortaApplicativa>> listPorteApplicativeByFiltroRicerca = null;
	
	private IDSoggetto idFruitore;
	private IDPortaDelegata idPortaDelegataDefault;
	private PortaDelegata portaDelegataDefault;
	private IDPortaDelegata idPortaDelegata;
	private PortaDelegata portaDelegata;
	private List<MappingFruizionePortaDelegata> listMappingFruizionePortaDelegata;
	
	private transient org.openspcoop2.utils.Semaphore semaphorePorteDelegateByFiltroRicerca = null; // possono essere alimentati da thread differenti
	private Map<String, List<IDPortaDelegata>> listPorteDelegateByFiltroRicerca = null;
	
	private org.openspcoop2.core.registry.Soggetto soggettoFruitoreRegistry;
	private org.openspcoop2.core.config.Soggetto soggettoFruitoreConfig;
	private String soggettoFruitoreIdentificativoPorta;
	private Boolean soggettoFruitoreSoggettoVirtuale;
	private String soggettoFruitoreImplementazionePdd;
	private Boolean soggettoFruitorePddReaded;
	private PortaDominio soggettoFruitorePdd;
	private String soggettoFruitoreVersioneProtocollo;
	
	private org.openspcoop2.core.config.Connettore connettoreFrutoreServizio = null;
	private org.openspcoop2.core.config.Connettore connettoreSoggettoErogatore=null;
	
	private Map<String, Ruolo> ruolo = null;
	private Map<String, Scope> scope = null;
	
	private SystemProperties systemProperties;
	
	private Boolean forwardProxyEnabled;
	private Map<String, Serializable> forwardProxy = null;
	
	private transient org.openspcoop2.utils.Semaphore semaphoreCredenziali = null; // possono essere alimentati da thread differenti
	private EnumMap<TipoCredenzialeMittente, Map<String, CredenzialeMittente>> mapCredenziali = null;
	
	private transient org.openspcoop2.utils.Semaphore semaphorePolicy = null; // possono essere alimentati da thread differenti
	private Map<String, Serializable> policyValidazioneToken = null;
	private Map<String, Serializable> policyNegoziazioneToken = null;
	private Map<String, Serializable> attributeAuthority = null;
	
	private transient org.openspcoop2.utils.Semaphore semaphoreStore = null; // possono essere alimentati da thread differenti
	private Map<String, Serializable> merlinTruststore = null;
	private Map<String, Serializable> merlinKeystore = null;
	private Map<String, Serializable> symmetricKeystore = null;
	private Map<String, Serializable> multiKeystore = null;
	private Map<String, Serializable> jwkSetStore = null;
	private Map<String, Serializable> httpStore = null;
	private Map<String, Serializable> crlCertstore = null;
	private Map<String, Serializable> sslSocketFactory = null;
	private Map<String, Serializable> sslConfigProps = null;
	private Map<String, Serializable> externalResource = null;
	private Map<String, Serializable> ocspResponse = null;
	
	private transient org.openspcoop2.utils.Semaphore semaphoreTemplate = null; // possono essere alimentati da thread differenti
	private Map<String, Serializable> template = null;
	
	
	
	public void copyFrom(RequestConfig source) {

		if(source==null) {
			throw new NullPointerException("Source is null");
		}
		
		if(source.key!=null) {
			this.key = source.key;
		}
		
		this.cached = source.cached;
		
		if(source.dominioDefault!=null) {
			this.dominioDefault = source.dominioDefault.clone();
		}
		
		if(source.idServizio!=null) {
			this.idServizio = source.idServizio.clone();
		}
		if(source.serviceBinding!=null) {
			this.serviceBinding = source.serviceBinding;
		}
		if(source.aspc!=null) {
			this.aspc = source.aspc;
		}
		if(source.asps!=null) {
			this.asps = source.asps;
		}
		if(source.asWrapperSoap!=null) {
			this.asWrapperSoap = source.asWrapperSoap;
		}
		if(source.asWrapperRest!=null) {
			this.asWrapperRest = source.asWrapperRest;
		}
		if(source.infoServizio!=null) {
			this.infoServizio = source.infoServizio;
		}
		if(source.infoServizioCorrelato!=null) {
			this.infoServizioCorrelato = source.infoServizioCorrelato;
		}
		if(source.infoServizioAzioneCorrelata!=null) {
			this.infoServizioAzioneCorrelata = source.infoServizioAzioneCorrelata;
		}
		if(source.servizioVersioneProtocollo!=null) {
			this.servizioVersioneProtocollo = source.servizioVersioneProtocollo;
		}
		
		// Informazioni che vengono impostate dopo aver letto l'azione specifica
		if(source.allegatoApi!=null) {
			this.allegatoApi = source.allegatoApi;
		}	
		if(source.allegatoServizio!=null) {
			this.allegatoServizio = source.allegatoServizio;
		}

		this.copyErogatoreFrom(source);
		
		this.copyFruitoreFrom(source);
		
		this.copyConfigFrom(source);
		
		this.copyCredenzialiFrom(source);
		
		this.copyPolicyFrom(source);
		
		this.copyStoreFrom(source);
		
		this.copyTemplateFrom(source);

	}
	private void copyErogatoreFrom(RequestConfig source) {
		
		if(source.soggettoErogatoreRegistry!=null) {
			this.soggettoErogatoreRegistry = source.soggettoErogatoreRegistry;
		}
		if(source.soggettoErogatoreConfig!=null) {
			this.soggettoErogatoreConfig = source.soggettoErogatoreConfig;
		}
		if(source.soggettoErogatoreIdentificativoPorta!=null) {
			this.soggettoErogatoreIdentificativoPorta = source.soggettoErogatoreIdentificativoPorta;
		}
		if(source.soggettoErogatoreSoggettoVirtuale!=null) {
			this.soggettoErogatoreSoggettoVirtuale = source.soggettoErogatoreSoggettoVirtuale;
		}
		if(source.soggettoErogatoreImplementazionePdd!=null) {
			this.soggettoErogatoreImplementazionePdd = source.soggettoErogatoreImplementazionePdd;
		}
		if(source.soggettoErogatorePddReaded!=null) {
			this.soggettoErogatorePddReaded = source.soggettoErogatorePddReaded;
		}
		if(source.soggettoErogatorePdd!=null) {
			this.soggettoErogatorePdd = source.soggettoErogatorePdd;
		}
		if(source.soggettoErogatoreVersioneProtocollo!=null) {
			this.soggettoErogatoreVersioneProtocollo = source.soggettoErogatoreVersioneProtocollo;
		}

		if(source.idPortaApplicativaDefault!=null) {
			this.idPortaApplicativaDefault = source.idPortaApplicativaDefault.clone();
		}
		if(source.portaApplicativaDefault!=null) {
			this.portaApplicativaDefault = source.portaApplicativaDefault;
		}
		if(source.idPortaApplicativa!=null) {
			this.idPortaApplicativa = source.idPortaApplicativa.clone();
		}
		if(source.portaApplicativa!=null) {
			this.portaApplicativa = source.portaApplicativa;
		}
		if(source.listMappingErogazionePortaApplicativa!=null) {
			this.listMappingErogazionePortaApplicativa = source.listMappingErogazionePortaApplicativa;
		}
		if(source.serviziApplicativiErogatore!=null) {
			this.serviziApplicativiErogatore = source.serviziApplicativiErogatore;
		}
		
		if(source.listPorteApplicativeByFiltroRicerca!=null) {
			this.listPorteApplicativeByFiltroRicerca = source.listPorteApplicativeByFiltroRicerca;
		}
		
	}
	private void copyFruitoreFrom(RequestConfig source) {
		
		if(source.idFruitore!=null) {
			this.idFruitore = source.idFruitore.clone();
		}
		if(source.idPortaDelegataDefault!=null) {
			this.idPortaDelegataDefault = source.idPortaDelegataDefault.clone();
		}
		if(source.portaDelegataDefault!=null) {
			this.portaDelegataDefault = source.portaDelegataDefault;
		}
		if(source.idPortaDelegata!=null) {
			this.idPortaDelegata = source.idPortaDelegata.clone();
		}
		if(source.portaDelegata!=null) {
			this.portaDelegata = source.portaDelegata;
		}
		if(source.listMappingFruizionePortaDelegata!=null) {
			this.listMappingFruizionePortaDelegata = source.listMappingFruizionePortaDelegata;
		}
		
		if(source.listPorteDelegateByFiltroRicerca!=null) {
			this.listPorteDelegateByFiltroRicerca = source.listPorteDelegateByFiltroRicerca;
		}
		
		if(source.soggettoFruitoreRegistry!=null) {
			this.soggettoFruitoreRegistry = source.soggettoFruitoreRegistry;
		}
		if(source.soggettoFruitoreConfig!=null) {
			this.soggettoFruitoreConfig = source.soggettoFruitoreConfig;
		}
		if(source.soggettoFruitoreIdentificativoPorta!=null) {
			this.soggettoFruitoreIdentificativoPorta = source.soggettoFruitoreIdentificativoPorta;
		}
		if(source.soggettoFruitoreSoggettoVirtuale!=null) {
			this.soggettoFruitoreSoggettoVirtuale = source.soggettoFruitoreSoggettoVirtuale;
		}
		if(source.soggettoFruitoreImplementazionePdd!=null) {
			this.soggettoFruitoreImplementazionePdd = source.soggettoFruitoreImplementazionePdd;
		}
		if(source.soggettoFruitorePddReaded!=null) {
			this.soggettoFruitorePddReaded = source.soggettoFruitorePddReaded;
		}
		if(source.soggettoFruitorePdd!=null) {
			this.soggettoFruitorePdd = source.soggettoFruitorePdd;
		}
		if(source.soggettoFruitoreVersioneProtocollo!=null) {
			this.soggettoFruitoreVersioneProtocollo = source.soggettoFruitoreVersioneProtocollo;
		}
	}
	private void copyConfigFrom(RequestConfig source) {
		
		if(source.connettoreFrutoreServizio!=null) {
			this.connettoreFrutoreServizio = source.connettoreFrutoreServizio;
		}
		if(source.connettoreSoggettoErogatore!=null) {
			this.connettoreSoggettoErogatore = source.connettoreSoggettoErogatore;
		}
		
		if(source.ruolo!=null) {
			this.ruolo = source.ruolo;
		}
		if(source.scope!=null) {
			this.scope = source.scope;
		}
		
		if(source.systemProperties!=null) {
			this.systemProperties = source.systemProperties;
		}
		
		if(source.forwardProxyEnabled!=null) {
			this.forwardProxyEnabled = source.forwardProxyEnabled;
		}
		if(source.forwardProxy!=null) {
			this.forwardProxy = source.forwardProxy;
		}
		
	}
	private void copyCredenzialiFrom(RequestConfig source) {		
		if(source.mapCredenziali!=null) {
			this.mapCredenziali = source.mapCredenziali;
		}
	}
	private void copyPolicyFrom(RequestConfig source) {
		if(source.policyValidazioneToken!=null) {
			this.policyValidazioneToken = source.policyValidazioneToken;
		}
		if(source.policyNegoziazioneToken!=null) {
			this.policyNegoziazioneToken = source.policyNegoziazioneToken;
		}
		if(source.attributeAuthority!=null) {
			this.attributeAuthority = source.attributeAuthority;
		}
	}
	private void copyStoreFrom(RequestConfig source) {		
		if(source.merlinTruststore!=null) {
			this.merlinTruststore = source.merlinTruststore;
		}
		if(source.merlinKeystore!=null) {
			this.merlinKeystore = source.merlinKeystore;
		}
		if(source.symmetricKeystore!=null) {
			this.symmetricKeystore = source.symmetricKeystore;
		}
		if(source.multiKeystore!=null) {
			this.multiKeystore = source.multiKeystore;
		}
		if(source.jwkSetStore!=null) {
			this.jwkSetStore = source.jwkSetStore;
		}
		if(source.httpStore!=null) {
			this.httpStore = source.httpStore;
		}
		if(source.crlCertstore!=null) {
			this.crlCertstore = source.crlCertstore;
		}
		if(source.sslSocketFactory!=null) {
			this.sslSocketFactory = source.sslSocketFactory;
		}
		if(source.sslConfigProps!=null) {
			this.sslConfigProps = source.sslConfigProps;
		}
		if(source.externalResource!=null) {
			this.externalResource = source.externalResource;
		}
		if(source.ocspResponse!=null) {
			this.ocspResponse = source.ocspResponse;
		}
	}
	private void copyTemplateFrom(RequestConfig source) {
		if(source.template!=null) {
			this.template = source.template;
		}
	}
	
	
	
	
	
	public String getKey() {
		return this.key;
	}
	public void setKey(String key) {
		this.key = key;
	}
	
	public boolean isCached() {
		return this.cached;
	}
	public void setCached(boolean cached) {
		this.cached = cached;
	}
	
	private synchronized void initSemaphore() {
		if(this.semaphore==null) {
			this.semaphore = new org.openspcoop2.utils.Semaphore("RequestConfig");
		}
	}
	
	public IDSoggetto getDominioDefault() {
		return this.dominioDefault;
	}
	public void setDominioDefault(IDSoggetto dominioDefault) {
		this.dominioDefault = dominioDefault;
	}
	
	public IDServizio getIdServizio() {
		return this.idServizio;
	}
	public void setIdServizio(IDServizio idServizio) {
		this.idServizio = idServizio;
	}
	public AccordoServizioParteComune getAspc() {
		return this.aspc;
	}
	public void setAspc(AccordoServizioParteComune aspc) {
		this.aspc = aspc;
	}
	public AccordoServizioParteSpecifica getAsps() {
		return this.asps;
	}
	public void setAsps(AccordoServizioParteSpecifica asps) {
		this.asps = asps;
	}
	public org.openspcoop2.core.registry.constants.ServiceBinding getServiceBinding() {
		return this.serviceBinding;
	}
	public void setServiceBinding(org.openspcoop2.core.registry.constants.ServiceBinding serviceBinding) {
		this.serviceBinding = serviceBinding;
	}
	
	private static String buildKeyAccordoServizioWrapper(InformationApiSource infoWsdlSource,boolean buildSchemaXSD,boolean processIncludeForOpenApi,boolean readDatiRegistro) {
		if(infoWsdlSource!=null) {
			return infoWsdlSource.name()+"_"+buildSchemaXSD+"_"+processIncludeForOpenApi+"_"+readDatiRegistro;
		}
		else {
			return "NULL_"+buildSchemaXSD+"_"+processIncludeForOpenApi+"_"+readDatiRegistro;
		}
	}
	
	public org.openspcoop2.core.registry.wsdl.AccordoServizioWrapper getAsWrapperSoap(InformationApiSource infoWsdlSource,boolean buildSchemaXSD,boolean readDatiRegistro) {
		if(this.asWrapperSoap==null) {
			return null;
		}
		String keyASWrapper = buildKeyAccordoServizioWrapper(infoWsdlSource, buildSchemaXSD, false, readDatiRegistro);
		return this.asWrapperSoap.get(keyASWrapper);
	}
	public void setAsWrapperSoap(org.openspcoop2.core.registry.wsdl.AccordoServizioWrapper asWrapperSoap, InformationApiSource infoWsdlSource,boolean buildSchemaXSD,boolean readDatiRegistro, String idTransazione) {
		String keyASWrapper = buildKeyAccordoServizioWrapper(infoWsdlSource, buildSchemaXSD, false, readDatiRegistro);
		if(this.semaphore==null) {
			// serializzazione da transient
			initSemaphore();
		}
		
		this.semaphore.acquireThrowRuntime("setAsWrapperSoap", idTransazione);
		try {
			if(this.asWrapperSoap==null) {
				this.asWrapperSoap = new HashMap<>(2);
			}
			this.asWrapperSoap.put(keyASWrapper, asWrapperSoap);
		}finally {
			this.semaphore.release("setAsWrapperSoap", idTransazione);
		}
	}
	public org.openspcoop2.core.registry.rest.AccordoServizioWrapper getAsWrapperRest(InformationApiSource infoWsdlSource,boolean buildSchemaXSD,boolean processIncludeForOpenApi,boolean readDatiRegistro) {
		if(this.asWrapperRest==null) {
			return null;
		}
		String keyASWrapper = buildKeyAccordoServizioWrapper(infoWsdlSource, buildSchemaXSD, processIncludeForOpenApi, readDatiRegistro);
		return this.asWrapperRest.get(keyASWrapper);
	}
	public void setAsWrapperRest(org.openspcoop2.core.registry.rest.AccordoServizioWrapper asWrapperRest, InformationApiSource infoWsdlSource,boolean buildSchemaXSD,boolean processIncludeForOpenApi,boolean readDatiRegistro, String idTransazione) {
		String keyASWrapper = buildKeyAccordoServizioWrapper(infoWsdlSource, buildSchemaXSD, processIncludeForOpenApi, readDatiRegistro);
		if(this.semaphore==null) {
			// serializzazione da transient
			initSemaphore();
		}
		
		this.semaphore.acquireThrowRuntime("setAsWrapperRest", idTransazione);
		try {
			if(this.asWrapperRest==null) {
				this.asWrapperRest = new HashMap<>(2);
			}
			this.asWrapperRest.put(keyASWrapper, asWrapperRest);
		}finally {
			this.semaphore.release("setAsWrapperRest", idTransazione);
		}
	}
	
	
	public Servizio getInfoServizio() {
		return this.infoServizio;
	}
	public void setInfoServizio(Servizio infoServizio) {
		this.infoServizio = infoServizio;
	}
	public Servizio getInfoServizioCorrelato() {
		return this.infoServizioCorrelato;
	}
	public void setInfoServizioCorrelato(Servizio infoServizioCorrelato) {
		this.infoServizioCorrelato = infoServizioCorrelato;
	}
	public Servizio getInfoServizioAzioneCorrelata() {
		return this.infoServizioAzioneCorrelata;
	}
	public void setInfoServizioAzioneCorrelata(Servizio infoServizioAzioneCorrelata) {
		this.infoServizioAzioneCorrelata = infoServizioAzioneCorrelata;
	}
	public String getServizioVersioneProtocollo() {
		return this.servizioVersioneProtocollo;
	}
	public void setServizioVersioneProtocollo(String servizioVersioneProtocollo) {
		this.servizioVersioneProtocollo = servizioVersioneProtocollo;
	}
	
	private synchronized void initSemaphoreAllegatoApi() {
		if(this.semaphoreAllegatoApi==null) {
			this.semaphoreAllegatoApi = new org.openspcoop2.utils.Semaphore("RequestConfigAllegatoApi");
		}
	}
	public void addAllegatoApi(String key, Documento documento, String idTransazione) {
		if(this.semaphoreAllegatoApi==null) {
			// serializzazione da transient
			initSemaphoreAllegatoApi();
		}
		
		this.semaphoreAllegatoApi.acquireThrowRuntime("addAllegatoApi", idTransazione);
		try {
			if(this.allegatoApi==null) {
				this.allegatoApi = new HashMap<>(3);
			}
			this.allegatoApi.put(key, documento);
		}finally {
			this.semaphoreAllegatoApi.release("addAllegatoApi", idTransazione);
		}
	}
	public Documento getAllegatoApi(String key) {
		if(this.allegatoApi==null) {
			return null;
		}
		return this.allegatoApi.get(key);
	}
	
	private synchronized void initSemaphoreAllegatoServizio() {
		if(this.semaphoreAllegatoServizio==null) {
			this.semaphoreAllegatoServizio = new org.openspcoop2.utils.Semaphore("RequestConfigAllegatoServizio");
		}
	}
	public void addAllegatoServizio(String key, Documento documento, String idTransazione) {
		if(this.semaphoreAllegatoServizio==null) {
			// serializzazione da transient
			initSemaphoreAllegatoServizio();
		}
		
		this.semaphoreAllegatoServizio.acquireThrowRuntime("addAllegatoServizio", idTransazione);
		try {
			if(this.allegatoServizio==null) {
				this.allegatoServizio = new HashMap<>(3);
			}
			this.allegatoServizio.put(key, documento);
		}finally {
			this.semaphoreAllegatoServizio.release("addAllegatoServizio", idTransazione);
		}
	}
	public Documento getAllegatoServizio(String key) {
		if(this.allegatoServizio==null) {
			return null;
		}
		return this.allegatoServizio.get(key);
	}
	
	public IDPortaApplicativa getIdPortaApplicativaDefault() {
		return this.idPortaApplicativaDefault;
	}
	public void setIdPortaApplicativaDefault(IDPortaApplicativa idPortaApplicativaDefault) {
		this.idPortaApplicativaDefault = idPortaApplicativaDefault;
	}
	public IDPortaApplicativa getIdPortaApplicativa() {
		return this.idPortaApplicativa;
	}
	public void setIdPortaApplicativa(IDPortaApplicativa idPortaApplicativa) {
		this.idPortaApplicativa = idPortaApplicativa;
	}
	public PortaApplicativa getPortaApplicativaDefault() {
		return this.portaApplicativaDefault;
	}
	public void setPortaApplicativaDefault(PortaApplicativa portaApplicativaDefault) {
		this.portaApplicativaDefault = portaApplicativaDefault;
	}
	public PortaApplicativa getPortaApplicativa() {
		return this.portaApplicativa;
	}
	public void setPortaApplicativa(PortaApplicativa portaApplicativa) {
		this.portaApplicativa = portaApplicativa;
	}
	public List<MappingErogazionePortaApplicativa> getListMappingErogazionePortaApplicativa() {
		return this.listMappingErogazionePortaApplicativa;
	}
	public void setListMappingErogazionePortaApplicativa(
			List<MappingErogazionePortaApplicativa> listMappingErogazionePortaApplicativa) {
		this.listMappingErogazionePortaApplicativa = listMappingErogazionePortaApplicativa;
	}
	public void addServizioApplicativoErogatore(ServizioApplicativo sa, String idTransazione) {
		if(this.semaphore==null) {
			// serializzazione da transient
			initSemaphore();
		}
		
		this.semaphore.acquireThrowRuntime("addServizioApplicativoErogatore", idTransazione);
		try {
			if(this.serviziApplicativiErogatore==null) {
				this.serviziApplicativiErogatore = new HashMap<>(5);
			}		
			this.serviziApplicativiErogatore.put(sa.getNome(), sa);
		}finally {
			this.semaphore.release("addServizioApplicativoErogatore", idTransazione);
		}
	}
	public ServizioApplicativo getServizioApplicativoErogatore(String nome) {
		if(this.serviziApplicativiErogatore==null) {
			return null;
		}
		return this.serviziApplicativiErogatore.get(nome);
	}
	public int sizeServiziApplicativiErogatori() {
		if(this.serviziApplicativiErogatore==null) {
			return 0;
		}
		return this.serviziApplicativiErogatore.size();
	}
	
	public List<IDPortaApplicativa> getPorteApplicativeByFiltroRicerca(ProtocolFiltroRicercaPorteApplicative filtro, BooleanNullable nullConditionsList) {
		if(this.listPorteApplicativeByFiltroRicerca==null) {
			nullConditionsList.setValue(null);
			return new ArrayList<>();
		}
		String keyCache = filtro.toString();
		return this.listPorteApplicativeByFiltroRicerca.get(keyCache);
	}
	public Map<String, List<IDPortaApplicativa>> getListPorteApplicativeByFiltroRicerca() {
		return this.listPorteApplicativeByFiltroRicerca;
	}
	private synchronized void initSemaphorePorteApplicativeByFiltroRicerca() {
		if(this.semaphorePorteApplicativeByFiltroRicerca==null) {
			this.semaphorePorteApplicativeByFiltroRicerca = new org.openspcoop2.utils.Semaphore("RequestConfigPorteApplicativeByFiltroRicerca");
		}
	}
	public void addPorteApplicativeByFiltroRicerca(ProtocolFiltroRicercaPorteApplicative filtro, List<IDPortaApplicativa> list, String idTransazione) {
		if(this.semaphorePorteApplicativeByFiltroRicerca==null) {
			// serializzazione da transient
			initSemaphorePorteApplicativeByFiltroRicerca();
		}
		
		this.semaphorePorteApplicativeByFiltroRicerca.acquireThrowRuntime("addPorteApplicativeByFiltroRicerca", idTransazione);
		try {
			if(this.listPorteApplicativeByFiltroRicerca==null) {
				this.listPorteApplicativeByFiltroRicerca=new HashMap<>(3);
			}
			String keyCache = filtro.toString();
			this.listPorteApplicativeByFiltroRicerca.put(keyCache, list);
		}finally {
			this.semaphorePorteApplicativeByFiltroRicerca.release("addPorteApplicativeByFiltroRicerca", idTransazione);
		}
	}
	
	public IDSoggetto getIdFruitore() {
		return this.idFruitore;
	}
	public void setIdFruitore(IDSoggetto idFruitore) {
		this.idFruitore = idFruitore;
	}
	public IDPortaDelegata getIdPortaDelegataDefault() {
		return this.idPortaDelegataDefault;
	}
	public void setIdPortaDelegataDefault(IDPortaDelegata idPortaDelegataDefault) {
		this.idPortaDelegataDefault = idPortaDelegataDefault;
	}
	public PortaDelegata getPortaDelegataDefault() {
		return this.portaDelegataDefault;
	}
	public void setPortaDelegataDefault(PortaDelegata portaDelegataDefault) {
		this.portaDelegataDefault = portaDelegataDefault;
	}
	public IDPortaDelegata getIdPortaDelegata() {
		return this.idPortaDelegata;
	}
	public void setIdPortaDelegata(IDPortaDelegata idPortaDelegata) {
		this.idPortaDelegata = idPortaDelegata;
	}
	public PortaDelegata getPortaDelegata() {
		return this.portaDelegata;
	}
	public void setPortaDelegata(PortaDelegata portaDelegata) {
		this.portaDelegata = portaDelegata;
	}
	public List<MappingFruizionePortaDelegata> getListMappingFruizionePortaDelegata() {
		return this.listMappingFruizionePortaDelegata;
	}
	public void setListMappingFruizionePortaDelegata(
			List<MappingFruizionePortaDelegata> listMappingFruizionePortaDelegata) {
		this.listMappingFruizionePortaDelegata = listMappingFruizionePortaDelegata;
	}
	
	public List<IDPortaDelegata> getPorteDelegateByFiltroRicerca(ProtocolFiltroRicercaPorteDelegate filtro, BooleanNullable nullConditionsList) {
		if(this.listPorteDelegateByFiltroRicerca==null) {
			nullConditionsList.setValue(null);
			return new ArrayList<>();
		}
		String keyCache = filtro.toString();
		return this.listPorteDelegateByFiltroRicerca.get(keyCache);
	}
	public Map<String, List<IDPortaDelegata>> getListPorteDelegateByFiltroRicerca() {
		return this.listPorteDelegateByFiltroRicerca;
	}
	private synchronized void initSemaphorePorteDelegateByFiltroRicerca() {
		if(this.semaphorePorteDelegateByFiltroRicerca==null) {
			this.semaphorePorteDelegateByFiltroRicerca = new org.openspcoop2.utils.Semaphore("RequestConfigPorteDelegateByFiltroRicerca");
		}
	}
	public void addPorteDelegateByFiltroRicerca(ProtocolFiltroRicercaPorteDelegate filtro, List<IDPortaDelegata> list, String idTransazione) {
		if(this.semaphorePorteDelegateByFiltroRicerca==null) {
			// serializzazione da transient
			initSemaphorePorteDelegateByFiltroRicerca();
		}
		
		this.semaphorePorteDelegateByFiltroRicerca.acquireThrowRuntime("addPorteDelegateByFiltroRicerca", idTransazione);
		try {
			if(this.listPorteDelegateByFiltroRicerca==null) {
				this.listPorteDelegateByFiltroRicerca=new HashMap<>(3);
			}
			String keyCache = filtro.toString();
			this.listPorteDelegateByFiltroRicerca.put(keyCache, list);
		}finally {
			this.semaphorePorteDelegateByFiltroRicerca.release("addPorteDelegateByFiltroRicerca", idTransazione);
		}
	}
	
	
	public org.openspcoop2.core.registry.Soggetto getSoggettoErogatoreRegistry() {
		return this.soggettoErogatoreRegistry;
	}
	public void setSoggettoErogatoreRegistry(org.openspcoop2.core.registry.Soggetto soggettoErogatoreRegistry) {
		this.soggettoErogatoreRegistry = soggettoErogatoreRegistry;
	}
	public org.openspcoop2.core.config.Soggetto getSoggettoErogatoreConfig() {
		return this.soggettoErogatoreConfig;
	}
	public void setSoggettoErogatoreConfig(org.openspcoop2.core.config.Soggetto soggettoErogatoreConfig) {
		this.soggettoErogatoreConfig = soggettoErogatoreConfig;
	}
	public org.openspcoop2.core.registry.Soggetto getSoggettoFruitoreRegistry() {
		return this.soggettoFruitoreRegistry;
	}
	public void setSoggettoFruitoreRegistry(org.openspcoop2.core.registry.Soggetto soggettoFruitoreRegistry) {
		this.soggettoFruitoreRegistry = soggettoFruitoreRegistry;
	}
	public org.openspcoop2.core.config.Soggetto getSoggettoFruitoreConfig() {
		return this.soggettoFruitoreConfig;
	}
	public void setSoggettoFruitoreConfig(org.openspcoop2.core.config.Soggetto soggettoFruitoreConfig) {
		this.soggettoFruitoreConfig = soggettoFruitoreConfig;
	}
	
	public String getSoggettoErogatoreIdentificativoPorta() {
		return this.soggettoErogatoreIdentificativoPorta;
	}
	public void setSoggettoErogatoreIdentificativoPorta(String soggettoErogatoreIdentificativoPorta) {
		this.soggettoErogatoreIdentificativoPorta = soggettoErogatoreIdentificativoPorta;
	}
	public Boolean getSoggettoErogatoreSoggettoVirtuale() {
		return this.soggettoErogatoreSoggettoVirtuale;
	}
	public void setSoggettoErogatoreSoggettoVirtuale(Boolean soggettoErogatoreSoggettoVirtuale) {
		this.soggettoErogatoreSoggettoVirtuale = soggettoErogatoreSoggettoVirtuale;
	}
	public String getSoggettoFruitoreIdentificativoPorta() {
		return this.soggettoFruitoreIdentificativoPorta;
	}
	public void setSoggettoFruitoreIdentificativoPorta(String soggettoFruitoreIdentificativoPorta) {
		this.soggettoFruitoreIdentificativoPorta = soggettoFruitoreIdentificativoPorta;
	}
	public Boolean getSoggettoFruitoreSoggettoVirtuale() {
		return this.soggettoFruitoreSoggettoVirtuale;
	}
	public void setSoggettoFruitoreSoggettoVirtuale(Boolean soggettoFruitoreSoggettoVirtuale) {
		this.soggettoFruitoreSoggettoVirtuale = soggettoFruitoreSoggettoVirtuale;
	}
	
	public String getSoggettoErogatoreImplementazionePdd() {
		return this.soggettoErogatoreImplementazionePdd;
	}
	public void setSoggettoErogatoreImplementazionePdd(String soggettoErogatoreImplementazionePdd) {
		this.soggettoErogatoreImplementazionePdd = soggettoErogatoreImplementazionePdd;
	}
	public Boolean getSoggettoErogatorePddReaded() {
		return this.soggettoErogatorePddReaded;
	}
	public void setSoggettoErogatorePddReaded(Boolean soggettoErogatorePddReaded) {
		this.soggettoErogatorePddReaded = soggettoErogatorePddReaded;
	}
	public PortaDominio getSoggettoErogatorePdd() {
		return this.soggettoErogatorePdd;
	}
	public void setSoggettoErogatorePdd(PortaDominio soggettoErogatorePdd) {
		this.soggettoErogatorePdd = soggettoErogatorePdd;
	}
	public String getSoggettoFruitoreImplementazionePdd() {
		return this.soggettoFruitoreImplementazionePdd;
	}
	public void setSoggettoFruitoreImplementazionePdd(String soggettoFruitoreImplementazionePdd) {
		this.soggettoFruitoreImplementazionePdd = soggettoFruitoreImplementazionePdd;
	}
	public Boolean getSoggettoFruitorePddReaded() {
		return this.soggettoFruitorePddReaded;
	}
	public void setSoggettoFruitorePddReaded(Boolean soggettoFruitorePddReaded) {
		this.soggettoFruitorePddReaded = soggettoFruitorePddReaded;
	}
	public PortaDominio getSoggettoFruitorePdd() {
		return this.soggettoFruitorePdd;
	}
	public void setSoggettoFruitorePdd(PortaDominio soggettoFruitorePdd) {
		this.soggettoFruitorePdd = soggettoFruitorePdd;
	}
	
	public String getSoggettoErogatoreVersioneProtocollo() {
		return this.soggettoErogatoreVersioneProtocollo;
	}
	public void setSoggettoErogatoreVersioneProtocollo(String soggettoErogatoreVersioneProtocollo) {
		this.soggettoErogatoreVersioneProtocollo = soggettoErogatoreVersioneProtocollo;
	}
	public String getSoggettoFruitoreVersioneProtocollo() {
		return this.soggettoFruitoreVersioneProtocollo;
	}
	public void setSoggettoFruitoreVersioneProtocollo(String soggettoFruitoreVersioneProtocollo) {
		this.soggettoFruitoreVersioneProtocollo = soggettoFruitoreVersioneProtocollo;
	}
	
	public org.openspcoop2.core.config.Connettore getConnettoreFrutoreServizio() {
		return this.connettoreFrutoreServizio;
	}
	public void setConnettoreFrutoreServizio(org.openspcoop2.core.config.Connettore connettoreFrutoreServizio) {
		this.connettoreFrutoreServizio = connettoreFrutoreServizio;
	}
	public org.openspcoop2.core.config.Connettore getConnettoreSoggettoErogatore() {
		return this.connettoreSoggettoErogatore;
	}
	public void setConnettoreSoggettoErogatore(org.openspcoop2.core.config.Connettore connettoreSoggettoErogatore) {
		this.connettoreSoggettoErogatore = connettoreSoggettoErogatore;
	}

	public void addRuolo(String key, Ruolo ruolo, String idTransazione) {
		if(this.semaphore==null) {
			// serializzazione da transient
			initSemaphore();
		}
		
		this.semaphore.acquireThrowRuntime("addRuolo", idTransazione);
		try {
			if(this.ruolo==null) {
				this.ruolo=new HashMap<>(3);
			}
			this.ruolo.put(key, ruolo);
		}finally {
			this.semaphore.release("addRuolo", idTransazione);
		}
	}
	public Ruolo getRuolo(String key) {
		if(this.ruolo==null) {
			return null;
		}
		return this.ruolo.get(key);
	}
	public List<String> getRuoloKeys(){
		List<String> l = new ArrayList<>();
		if(this.ruolo!=null && !this.ruolo.isEmpty()) {
			l.addAll(this.ruolo.keySet());
		}
		return l;
	}
	
	public void addScope(String key, Scope scope, String idTransazione) {
		if(this.semaphore==null) {
			// serializzazione da transient
			initSemaphore();
		}
		
		this.semaphore.acquireThrowRuntime("addScope", idTransazione);
		try {
			if(this.scope==null) {
				this.scope=new HashMap<>(3);
			}
			this.scope.put(key, scope);
		}finally {
			this.semaphore.release("addScope", idTransazione);
		}
	}
	public Scope getScope(String key) {
		if(this.scope==null) {
			return null;
		}
		return this.scope.get(key);
	}
	public List<String> getScopeKeys(){
		List<String> l = new ArrayList<>();
		if(this.scope!=null && !this.scope.isEmpty()) {
			l.addAll(this.scope.keySet());
		}
		return l;
	}
	
	public SystemProperties getSystemProperties() {
		return this.systemProperties;
	}
	public void setSystemProperties(SystemProperties systemProperties) {
		this.systemProperties = systemProperties;
	}
	public Boolean getForwardProxyEnabled() {
		return this.forwardProxyEnabled;
	}
	public void setForwardProxyEnabled(Boolean forwardProxyEnabled) {
		this.forwardProxyEnabled = forwardProxyEnabled;
	}
	
	public void addForwardProxy(String key, Serializable fp, String idTransazione) {
		if(this.semaphore==null) {
			// serializzazione da transient
			initSemaphore();
		}
		
		this.semaphore.acquireThrowRuntime("addForwardProxy", idTransazione);
		try {
			if(this.forwardProxy==null) {
				this.forwardProxy=new HashMap<>(3);
			}
			this.forwardProxy.put(key, fp);
		}finally {
			this.semaphore.release("addForwardProxy", idTransazione);
		}
	}
	public Serializable getForwardProxy(String key) {
		if(this.forwardProxy==null) {
			return null;
		}
		return this.forwardProxy.get(key);
	}
	public List<String> getForwardProxyKeys(){
		List<String> l = new ArrayList<>();
		if(this.forwardProxy!=null && !this.forwardProxy.isEmpty()) {
			l.addAll(this.forwardProxy.keySet());
		}
		return l;
	}
	
	public CredenzialeMittente getCredenzialeMittente(TipoCredenzialeMittente tipo, String keyCache) {
		if(this.mapCredenziali==null) {
			return null;
		}
		Map<String, CredenzialeMittente> map = this.mapCredenziali.get(tipo);
		if(map!=null) {
			return map.get(keyCache);
		}
		return null;
	}
	
	private synchronized void initSemaphoreCredenziali() {
		if(this.semaphoreCredenziali==null) {
			this.semaphoreCredenziali = new org.openspcoop2.utils.Semaphore("RequestConfigCredenzialeMittente");
		}
	}
	public void addCredenzialeMittente(TipoCredenzialeMittente tipo, String keyCache, CredenzialeMittente credenziale, String idTransazione, Date scadenzaEntry) {
		
		if(this.semaphoreCredenziali==null) {
			// serializzazione da transient
			initSemaphoreCredenziali();
		}
		
		this.semaphoreCredenziali.acquireThrowRuntime("addCredenzialeMittente", idTransazione);
		try {
			if(this.mapCredenziali==null) {
				this.mapCredenziali = new EnumMap<>(TipoCredenzialeMittente.class);
			}
			Map<String, CredenzialeMittente> map = this.mapCredenziali.get(tipo);
			if(map==null) {
				map = new HashMap<>();
				this.mapCredenziali.put(tipo, map);
			}
			if(!map.containsKey(keyCache)) {
				map.put(keyCache, credenziale);	
			}
			else {
				CredenzialeMittente c = map.get(keyCache);
				if(!c.getOraRegistrazione().after(scadenzaEntry)) { // serve per forzare l'aggiornamento dell'oggetto se "scaduto"
					map.remove(keyCache);
					map.put(keyCache, credenziale);	
				}
			}
		}finally {
			this.semaphoreCredenziali.release("addCredenzialeMittente", idTransazione);
		}
	}
	
	private synchronized void initSemaphorePolicy() {
		if(this.semaphorePolicy==null) {
			this.semaphorePolicy = new org.openspcoop2.utils.Semaphore("RequestConfigPolicy");
		}
	}
	
	public void addPolicyValidazioneToken(String key, Serializable fp, String idTransazione) {
		if(this.semaphorePolicy==null) {
			// serializzazione da transient
			initSemaphorePolicy();
		}
		
		this.semaphorePolicy.acquireThrowRuntime("addPolicyValidazioneToken", idTransazione);
		try {
			if(this.policyValidazioneToken==null) {
				this.policyValidazioneToken = new HashMap<>(3);
			}
			this.policyValidazioneToken.put(key, fp);
		}finally {
			this.semaphorePolicy.release("addPolicyValidazioneToken", idTransazione);
		}
	}
	public Serializable getPolicyValidazioneToken(String key) {
		if(this.policyValidazioneToken==null) {
			return null;
		}
		return this.policyValidazioneToken.get(key);
	}
	public List<String> getPolicyValidazioneTokenKeys(){
		List<String> l = new ArrayList<>();
		if(this.policyValidazioneToken!=null && !this.policyValidazioneToken.isEmpty()) {
			l.addAll(this.policyValidazioneToken.keySet());
		}
		return l;
	}
	
	public void addPolicyNegoziazioneToken(String key, Serializable fp, String idTransazione) {
		
		if(this.semaphorePolicy==null) {
			// serializzazione da transient
			initSemaphorePolicy();
		}
		
		this.semaphorePolicy.acquireThrowRuntime("addPolicyNegoziazioneToken", idTransazione);
		try {
			if(this.policyNegoziazioneToken==null) {
				this.policyNegoziazioneToken = new HashMap<>(3);
			}
			this.policyNegoziazioneToken.put(key, fp);
		}finally {
			this.semaphorePolicy.release("addPolicyNegoziazioneToken", idTransazione);
		}
	}
	public Serializable getPolicyNegoziazioneToken(String key) {
		if(this.policyNegoziazioneToken==null) {
			return null;
		}
		return this.policyNegoziazioneToken.get(key);
	}
	public List<String> getPolicyNegoziazioneTokenKeys(){
		List<String> l = new ArrayList<>();
		if(this.policyNegoziazioneToken!=null && !this.policyNegoziazioneToken.isEmpty()) {
			l.addAll(this.policyNegoziazioneToken.keySet());
		}
		return l;
	}
	
	public void addAttributeAuthority(String key, Serializable fp, String idTransazione) {
		if(this.semaphorePolicy==null) {
			// serializzazione da transient
			initSemaphorePolicy();
		}
		
		this.semaphorePolicy.acquireThrowRuntime("addAttributeAuthority", idTransazione);
		try {
			if(this.attributeAuthority==null) {
				this.attributeAuthority = new HashMap<>(3);
			}
			this.attributeAuthority.put(key, fp);
		}finally {
			this.semaphorePolicy.release("addAttributeAuthority", idTransazione);
		}
	}
	public Serializable getAttributeAuthority(String key) {
		if(this.attributeAuthority==null) {
			return null;
		}
		return this.attributeAuthority.get(key);
	}
	public List<String> getAttributeAuthorityKeys(){
		List<String> l = new ArrayList<>();
		if(this.attributeAuthority!=null && !this.attributeAuthority.isEmpty()) {
			l.addAll(this.attributeAuthority.keySet());
		}
		return l;
	}
	
	
	private synchronized void initSemaphoreStore() {
		if(this.semaphoreStore==null) {
			this.semaphoreStore = new org.openspcoop2.utils.Semaphore("RequestConfigStore");
		}
	}
	
	public void addMerlinTruststore(String key, Serializable merlinTruststore, String idTransazione) {
		
		if(this.semaphoreStore==null) {
			// serializzazione da transient
			initSemaphoreStore();
		}
		
		this.semaphoreStore.acquireThrowRuntime("addMerlinTruststore", idTransazione);
		try {
			if(this.merlinTruststore==null) {
				this.merlinTruststore = new HashMap<>(3);
			}
			this.merlinTruststore.put(key, merlinTruststore);
		}finally {
			this.semaphoreStore.release("addMerlinTruststore", idTransazione);
		}
	}
	public Serializable getMerlinTruststore(String key) {
		if(this.merlinTruststore==null) {
			return null;
		}
		return this.merlinTruststore.get(key);
	}
	
	public void addMerlinKeystore(String key, Serializable merlinKeystore, String idTransazione) {
		
		if(this.semaphoreStore==null) {
			// serializzazione da transient
			initSemaphoreStore();
		}
		
		this.semaphoreStore.acquireThrowRuntime("addMerlinKeystore", idTransazione);
		try {
			if(this.merlinKeystore==null) {
				this.merlinKeystore = new HashMap<>(3);
			}
			this.merlinKeystore.put(key, merlinKeystore);
		}finally {
			this.semaphoreStore.release("addMerlinKeystore", idTransazione);
		}
	}
	public Serializable getMerlinKeystore(String key) {
		if(this.merlinKeystore==null) {
			return null;
		}
		return this.merlinKeystore.get(key);
	}
	
	public void addSymmetricKeystore(String key, Serializable symmetricKeystore, String idTransazione) {
		 
		
		if(this.semaphoreStore==null) {
			// serializzazione da transient
			initSemaphoreStore();
		}
		
		this.semaphoreStore.acquireThrowRuntime("addSymmetricKeystore", idTransazione);
		try {
			if(this.symmetricKeystore==null) {
				this.symmetricKeystore = new HashMap<>(3);
			}
			this.symmetricKeystore.put(key, symmetricKeystore);
		}finally {
			this.semaphoreStore.release("addSymmetricKeystore", idTransazione);
		}
	}
	public Serializable getSymmetricKeystore(String key) {
		if(this.symmetricKeystore==null) {
			return null;
		}
		return this.symmetricKeystore.get(key);
	}
	
	public void addMultiKeystore(String key, Serializable multiKeystore, String idTransazione) {
		 
		
		if(this.semaphoreStore==null) {
			// serializzazione da transient
			initSemaphoreStore();
		}
		
		this.semaphoreStore.acquireThrowRuntime("addMultiKeystore", idTransazione);
		try {
			if(this.multiKeystore==null) {
				this.multiKeystore = new HashMap<>(3);
			}
			this.multiKeystore.put(key, multiKeystore);
		}finally {
			this.semaphoreStore.release("addMultiKeystore", idTransazione);
		}
	}
	public Serializable getMultiKeystore(String key) {
		if(this.multiKeystore==null) {
			return null;
		}
		return this.multiKeystore.get(key);
	}
	
	public void addJWKSetStore(String key, Serializable jwkSetStore, String idTransazione) {
		 
		
		if(this.semaphoreStore==null) {
			// serializzazione da transient
			initSemaphoreStore();
		}
		
		this.semaphoreStore.acquireThrowRuntime("addJWKSetStore", idTransazione);
		try {
			if(this.jwkSetStore==null) {
				this.jwkSetStore = new HashMap<>(3);
			}
			this.jwkSetStore.put(key, jwkSetStore);
		}finally {
			this.semaphoreStore.release("addJWKSetStore", idTransazione);
		}
	}
	public Serializable getJWKSetStore(String key) {
		if(this.jwkSetStore==null) {
			return null;
		}
		return this.jwkSetStore.get(key);
	}

	public void addHttpStore(String key, Serializable httpStore, String idTransazione) {
		 
		
		if(this.semaphoreStore==null) {
			// serializzazione da transient
			initSemaphoreStore();
		}
		
		this.semaphoreStore.acquireThrowRuntime("addHttpStore", idTransazione);
		try {
			if(this.httpStore==null) {
				this.httpStore = new HashMap<>(3);
			}
			this.httpStore.put(key, httpStore);
		}finally {
			this.semaphoreStore.release("addHttpStore", idTransazione);
		}
	}
	public Serializable getHttpStore(String key) {
		if(this.httpStore==null) {
			return null;
		}
		return this.httpStore.get(key);
	}
	
	public void addCRLCertstore(String key, Serializable crlCertstore, String idTransazione) {
		 
		
		if(this.semaphoreStore==null) {
			// serializzazione da transient
			initSemaphoreStore();
		}
		
		this.semaphoreStore.acquireThrowRuntime("addCRLCertstore", idTransazione);
		try {
			if(this.crlCertstore==null) {
				this.crlCertstore = new HashMap<>(3);
			}
			this.crlCertstore.put(key, crlCertstore);
		}finally {
			this.semaphoreStore.release("addCRLCertstore", idTransazione);
		}
	}
	public Serializable getCRLCertstore(String key) {
		if(this.crlCertstore==null) {
			return null;
		}
		return this.crlCertstore.get(key);
	}
	
	public void addSSLSocketFactory(String key, Serializable sslSocketFactory, String idTransazione) {
		 
		
		if(this.semaphoreStore==null) {
			// serializzazione da transient
			initSemaphoreStore();
		}
		
		this.semaphoreStore.acquireThrowRuntime("addSSLSocketFactory", idTransazione);
		try {
			if(this.sslSocketFactory==null) {
				this.sslSocketFactory = new HashMap<>(3);
			}
			this.sslSocketFactory.put(key, sslSocketFactory);
		}finally {
			this.semaphoreStore.release("addSSLSocketFactory", idTransazione);
		}
	}
	public Serializable getSSLSocketFactory(String key) {
		if(this.sslSocketFactory==null) {
			return null;
		}
		return this.sslSocketFactory.get(key);
	}
	
	public void addSSLConfigProps(String key, Serializable resource, String idTransazione) {
		 
		
		if(this.semaphoreStore==null) {
			// serializzazione da transient
			initSemaphoreStore();
		}
		
		this.semaphoreStore.acquireThrowRuntime("addSSLConfigProps", idTransazione);
		try {
			if(this.sslConfigProps==null) {
				this.sslConfigProps = new HashMap<>(3);
			}
			this.sslConfigProps.put(key, resource);
		}finally {
			this.semaphoreStore.release("addSSLConfigProps", idTransazione);
		}
	}
	public Serializable getSSLConfigProps(String key) {
		if(this.sslConfigProps==null) {
			return null;
		}
		return this.sslConfigProps.get(key);
	}
	
	public void addExternalResource(String key, Serializable resource, String idTransazione) {
		 
		
		if(this.semaphoreStore==null) {
			// serializzazione da transient
			initSemaphoreStore();
		}
		
		this.semaphoreStore.acquireThrowRuntime("addExternalResource", idTransazione);
		try {
			if(this.externalResource==null) {
				this.externalResource = new HashMap<>(3);
			}
			this.externalResource.put(key, resource);
		}finally {
			this.semaphoreStore.release("addExternalResource", idTransazione);
		}
	}
	public Serializable getExternalResource(String key) {
		if(this.externalResource==null) {
			return null;
		}
		return this.externalResource.get(key);
	}
	
	public void addOCSPResponse(String key, Serializable resource, String idTransazione) {
		 
		
		if(this.semaphoreStore==null) {
			// serializzazione da transient
			initSemaphoreStore();
		}
		
		this.semaphoreStore.acquireThrowRuntime("addOCSPResponse", idTransazione);
		try {
			if(this.ocspResponse==null) {
				this.ocspResponse = new HashMap<>(3);
			}
			this.ocspResponse.put(key, resource);
		}finally {
			this.semaphoreStore.release("addOCSPResponse", idTransazione);
		}
	}
	public Serializable getOCSPResponse(String key) {
		if(this.ocspResponse==null) {
			return null;
		}
		return this.ocspResponse.get(key);
	}

	
	private synchronized void initSemaphoreTemplate() {
		if(this.semaphoreTemplate==null) {
			this.semaphoreTemplate = new org.openspcoop2.utils.Semaphore("RequestConfigTemplate");
		}
	}
	
	public void addTemplate(String key, Serializable fp, String idTransazione) {
		if(this.semaphoreTemplate==null) {
			// serializzazione da transient
			initSemaphoreTemplate();
		}
		
		this.semaphoreTemplate.acquireThrowRuntime("addTemplate", idTransazione);
		try {
			if(this.template==null) {
				this.template = new HashMap<>(3);
			}
			this.template.put(key, fp);
		}finally {
			this.semaphoreTemplate.release("addTemplate", idTransazione);
		}
	}
	public Serializable getTemplate(String key) {
		if(this.template==null) {
			return null;
		}
		return this.template.get(key);
	}

}
