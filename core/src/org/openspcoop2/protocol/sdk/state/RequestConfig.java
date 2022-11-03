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

package org.openspcoop2.protocol.sdk.state;

import java.util.ArrayList;
import java.util.Date;
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
import org.openspcoop2.protocol.sdk.registry.FiltroRicercaPorteApplicative;
import org.openspcoop2.protocol.sdk.registry.FiltroRicercaPorteDelegate;


/**
 * RequestConfig
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class RequestConfig implements java.io.Serializable, Cloneable {

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
	private Map<String, org.openspcoop2.core.registry.wsdl.AccordoServizioWrapper> asWrapper_soap = null;
	private Map<String, org.openspcoop2.core.registry.rest.AccordoServizioWrapper> asWrapper_rest = null;
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
	private Map<String, Object> forwardProxy = null;
	
	private transient org.openspcoop2.utils.Semaphore semaphoreCredenziali = null; // possono essere alimentati da thread differenti
	private Map<TipoCredenzialeMittente, Map<String, CredenzialeMittente>> mapCredenziali = null;
	
	private transient org.openspcoop2.utils.Semaphore semaphorePolicy = null; // possono essere alimentati da thread differenti
	private Map<String, Object> policyValidazioneToken = null;
	private Map<String, Object> policyNegoziazioneToken = null;
	private Map<String, Object> attributeAuthority = null;
	
	private transient org.openspcoop2.utils.Semaphore semaphoreStore = null; // possono essere alimentati da thread differenti
	private Map<String, Object> merlinTruststore = null;
	private Map<String, Object> merlinKeystore = null;
	private Map<String, Object> symmetricKeystore = null;
	private Map<String, Object> multiKeystore = null;
	private Map<String, Object> jwkSetStore = null;
	private Map<String, Object> httpStore = null;
	private Map<String, Object> crlCertstore = null;
	private Map<String, Object> sslSocketFactory = null;
	
	private transient org.openspcoop2.utils.Semaphore semaphoreTemplate = null; // possono essere alimentati da thread differenti
	private Map<String, Object> template = null;
	
	
	
	
	@Override
	public RequestConfig clone(){
		RequestConfig clone = new RequestConfig();
		
		if(this.key!=null) {
			clone.key = this.key;
		}
		
		clone.cached = this.cached;
		
		if(this.dominioDefault!=null) {
			clone.dominioDefault = this.dominioDefault.clone();
		}
		
		if(this.idServizio!=null) {
			clone.idServizio = this.idServizio.clone();
		}
		if(this.serviceBinding!=null) {
			clone.serviceBinding = this.serviceBinding;
		}
		if(this.aspc!=null) {
			clone.aspc = this.aspc;
		}
		if(this.asps!=null) {
			clone.asps = this.asps;
		}
		if(this.asWrapper_soap!=null) {
			clone.asWrapper_soap = this.asWrapper_soap;
		}
		if(this.asWrapper_rest!=null) {
			clone.asWrapper_rest = this.asWrapper_rest;
		}
		if(this.infoServizio!=null) {
			clone.infoServizio = this.infoServizio;
		}
		if(this.infoServizioCorrelato!=null) {
			clone.infoServizioCorrelato = this.infoServizioCorrelato;
		}
		if(this.infoServizioAzioneCorrelata!=null) {
			clone.infoServizioAzioneCorrelata = this.infoServizioAzioneCorrelata;
		}
		if(this.servizioVersioneProtocollo!=null) {
			clone.servizioVersioneProtocollo = this.servizioVersioneProtocollo;
		}
		
		// Informazioni che vengono impostate dopo aver letto l'azione specifica
		if(this.allegatoApi!=null) {
			clone.allegatoApi = this.allegatoApi;
		}	
		if(this.allegatoServizio!=null) {
			clone.allegatoServizio = this.allegatoServizio;
		}

		if(this.soggettoErogatoreRegistry!=null) {
			clone.soggettoErogatoreRegistry = this.soggettoErogatoreRegistry;
		}
		if(this.soggettoErogatoreConfig!=null) {
			clone.soggettoErogatoreConfig = this.soggettoErogatoreConfig;
		}
		if(this.soggettoErogatoreIdentificativoPorta!=null) {
			clone.soggettoErogatoreIdentificativoPorta = this.soggettoErogatoreIdentificativoPorta;
		}
		if(this.soggettoErogatoreSoggettoVirtuale!=null) {
			clone.soggettoErogatoreSoggettoVirtuale = this.soggettoErogatoreSoggettoVirtuale;
		}
		if(this.soggettoErogatoreImplementazionePdd!=null) {
			clone.soggettoErogatoreImplementazionePdd = this.soggettoErogatoreImplementazionePdd;
		}
		if(this.soggettoErogatorePddReaded!=null) {
			clone.soggettoErogatorePddReaded = this.soggettoErogatorePddReaded;
		}
		if(this.soggettoErogatorePdd!=null) {
			clone.soggettoErogatorePdd = this.soggettoErogatorePdd;
		}
		if(this.soggettoErogatoreVersioneProtocollo!=null) {
			clone.soggettoErogatoreVersioneProtocollo = this.soggettoErogatoreVersioneProtocollo;
		}

		if(this.idPortaApplicativaDefault!=null) {
			clone.idPortaApplicativaDefault = this.idPortaApplicativaDefault.clone();
		}
		if(this.portaApplicativaDefault!=null) {
			clone.portaApplicativaDefault = this.portaApplicativaDefault;
		}
		if(this.idPortaApplicativa!=null) {
			clone.idPortaApplicativa = this.idPortaApplicativa.clone();
		}
		if(this.portaApplicativa!=null) {
			clone.portaApplicativa = this.portaApplicativa;
		}
		if(this.listMappingErogazionePortaApplicativa!=null) {
			clone.listMappingErogazionePortaApplicativa = this.listMappingErogazionePortaApplicativa;
		}
		if(this.serviziApplicativiErogatore!=null) {
			clone.serviziApplicativiErogatore = this.serviziApplicativiErogatore;
		}
		
		if(this.listPorteApplicativeByFiltroRicerca!=null) {
			clone.listPorteApplicativeByFiltroRicerca = this.listPorteApplicativeByFiltroRicerca;
		}
		
		if(this.idFruitore!=null) {
			clone.idFruitore = this.idFruitore.clone();
		}
		if(this.idPortaDelegataDefault!=null) {
			clone.idPortaDelegataDefault = this.idPortaDelegataDefault.clone();
		}
		if(this.portaDelegataDefault!=null) {
			clone.portaDelegataDefault = this.portaDelegataDefault;
		}
		if(this.idPortaDelegata!=null) {
			clone.idPortaDelegata = this.idPortaDelegata.clone();
		}
		if(this.portaDelegata!=null) {
			clone.portaDelegata = this.portaDelegata;
		}
		if(this.listMappingFruizionePortaDelegata!=null) {
			clone.listMappingFruizionePortaDelegata = this.listMappingFruizionePortaDelegata;
		}
		
		if(this.listPorteDelegateByFiltroRicerca!=null) {
			clone.listPorteDelegateByFiltroRicerca = this.listPorteDelegateByFiltroRicerca;
		}
		
		
		if(this.soggettoFruitoreRegistry!=null) {
			clone.soggettoFruitoreRegistry = this.soggettoFruitoreRegistry;
		}
		if(this.soggettoFruitoreConfig!=null) {
			clone.soggettoFruitoreConfig = this.soggettoFruitoreConfig;
		}
		if(this.soggettoFruitoreIdentificativoPorta!=null) {
			clone.soggettoFruitoreIdentificativoPorta = this.soggettoFruitoreIdentificativoPorta;
		}
		if(this.soggettoFruitoreSoggettoVirtuale!=null) {
			clone.soggettoFruitoreSoggettoVirtuale = this.soggettoFruitoreSoggettoVirtuale;
		}
		if(this.soggettoFruitoreImplementazionePdd!=null) {
			clone.soggettoFruitoreImplementazionePdd = this.soggettoFruitoreImplementazionePdd;
		}
		if(this.soggettoFruitorePddReaded!=null) {
			clone.soggettoFruitorePddReaded = this.soggettoFruitorePddReaded;
		}
		if(this.soggettoFruitorePdd!=null) {
			clone.soggettoFruitorePdd = this.soggettoFruitorePdd;
		}
		if(this.soggettoFruitoreVersioneProtocollo!=null) {
			clone.soggettoFruitoreVersioneProtocollo = this.soggettoFruitoreVersioneProtocollo;
		}
		
		if(this.connettoreFrutoreServizio!=null) {
			clone.connettoreFrutoreServizio = this.connettoreFrutoreServizio;
		}
		if(this.connettoreSoggettoErogatore!=null) {
			clone.connettoreSoggettoErogatore = this.connettoreSoggettoErogatore;
		}
		
		if(this.ruolo!=null) {
			clone.ruolo = this.ruolo;
		}
		if(this.scope!=null) {
			clone.scope = this.scope;
		}
		
		if(this.systemProperties!=null) {
			clone.systemProperties = this.systemProperties;
		}
		
		if(this.forwardProxyEnabled!=null) {
			clone.forwardProxyEnabled = this.forwardProxyEnabled;
		}
		if(this.forwardProxy!=null) {
			clone.forwardProxy = this.forwardProxy;
		}
		
		if(this.mapCredenziali!=null) {
			clone.mapCredenziali = this.mapCredenziali;
		}
		
		if(this.policyValidazioneToken!=null) {
			clone.policyValidazioneToken = this.policyValidazioneToken;
		}
		if(this.policyNegoziazioneToken!=null) {
			clone.policyNegoziazioneToken = this.policyNegoziazioneToken;
		}
		if(this.attributeAuthority!=null) {
			clone.attributeAuthority = this.attributeAuthority;
		}
		
		if(this.merlinTruststore!=null) {
			clone.merlinTruststore = this.merlinTruststore;
		}
		if(this.merlinKeystore!=null) {
			clone.merlinKeystore = this.merlinKeystore;
		}
		if(this.symmetricKeystore!=null) {
			clone.symmetricKeystore = this.symmetricKeystore;
		}
		if(this.multiKeystore!=null) {
			clone.multiKeystore = this.multiKeystore;
		}
		if(this.jwkSetStore!=null) {
			clone.jwkSetStore = this.jwkSetStore;
		}
		if(this.httpStore!=null) {
			clone.httpStore = this.httpStore;
		}
		if(this.crlCertstore!=null) {
			clone.crlCertstore = this.crlCertstore;
		}
		if(this.sslSocketFactory!=null) {
			clone.sslSocketFactory = this.sslSocketFactory;
		}

		if(this.template!=null) {
			clone.template = this.template;
		}

		return clone;
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
	
	public org.openspcoop2.core.registry.wsdl.AccordoServizioWrapper getAsWrapper_soap(InformationApiSource infoWsdlSource,boolean buildSchemaXSD,boolean readDatiRegistro) {
		if(this.asWrapper_soap==null) {
			return null;
		}
		String key = buildKeyAccordoServizioWrapper(infoWsdlSource, buildSchemaXSD, false, readDatiRegistro);
		return this.asWrapper_soap.get(key);
	}
	public void setAsWrapper_soap(org.openspcoop2.core.registry.wsdl.AccordoServizioWrapper asWrapper_soap, InformationApiSource infoWsdlSource,boolean buildSchemaXSD,boolean readDatiRegistro, String idTransazione) {
		String key = buildKeyAccordoServizioWrapper(infoWsdlSource, buildSchemaXSD, false, readDatiRegistro);
		if(this.semaphore==null) {
			// serializzazione da transient
			initSemaphore();
		}
		
		this.semaphore.acquireThrowRuntime("setAsWrapper_soap", idTransazione);
		try {
			if(this.asWrapper_soap==null) {
				this.asWrapper_soap = new HashMap<String, org.openspcoop2.core.registry.wsdl.AccordoServizioWrapper>(2);
			}
			this.asWrapper_soap.put(key, asWrapper_soap);
		}finally {
			this.semaphore.release("setAsWrapper_soap", idTransazione);
		}
	}
	public org.openspcoop2.core.registry.rest.AccordoServizioWrapper getAsWrapper_rest(InformationApiSource infoWsdlSource,boolean buildSchemaXSD,boolean processIncludeForOpenApi,boolean readDatiRegistro) {
		if(this.asWrapper_rest==null) {
			return null;
		}
		String key = buildKeyAccordoServizioWrapper(infoWsdlSource, buildSchemaXSD, processIncludeForOpenApi, readDatiRegistro);
		return this.asWrapper_rest.get(key);
	}
	public void setAsWrapper_rest(org.openspcoop2.core.registry.rest.AccordoServizioWrapper asWrapper_rest, InformationApiSource infoWsdlSource,boolean buildSchemaXSD,boolean processIncludeForOpenApi,boolean readDatiRegistro, String idTransazione) {
		String key = buildKeyAccordoServizioWrapper(infoWsdlSource, buildSchemaXSD, processIncludeForOpenApi, readDatiRegistro);
		if(this.semaphore==null) {
			// serializzazione da transient
			initSemaphore();
		}
		
		this.semaphore.acquireThrowRuntime("setAsWrapper_rest", idTransazione);
		try {
			if(this.asWrapper_rest==null) {
				this.asWrapper_rest = new HashMap<String, org.openspcoop2.core.registry.rest.AccordoServizioWrapper>(2);
			}
			this.asWrapper_rest.put(key, asWrapper_rest);
		}finally {
			this.semaphore.release("setAsWrapper_rest", idTransazione);
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
				this.allegatoApi = new HashMap<String, Documento>(3);
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
				this.allegatoServizio = new HashMap<String, Documento>(3);
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
				this.serviziApplicativiErogatore = new HashMap<String, ServizioApplicativo>(5);
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
	
	public List<IDPortaApplicativa> getPorteApplicativeByFiltroRicerca(FiltroRicercaPorteApplicative filtro) {
		if(this.listPorteApplicativeByFiltroRicerca==null) {
			return null;
		}
		String keyCache = filtro.toString();
		List<IDPortaApplicativa> l = this.listPorteApplicativeByFiltroRicerca.get(keyCache);
		return l;
	}
	public Map<String, List<IDPortaApplicativa>> getListPorteApplicativeByFiltroRicerca() {
		return this.listPorteApplicativeByFiltroRicerca;
	}
	private synchronized void initSemaphorePorteApplicativeByFiltroRicerca() {
		if(this.semaphorePorteApplicativeByFiltroRicerca==null) {
			this.semaphorePorteApplicativeByFiltroRicerca = new org.openspcoop2.utils.Semaphore("RequestConfigPorteApplicativeByFiltroRicerca");
		}
	}
	public void addPorteApplicativeByFiltroRicerca(FiltroRicercaPorteApplicative filtro, List<IDPortaApplicativa> list, String idTransazione) {
		if(this.semaphorePorteApplicativeByFiltroRicerca==null) {
			// serializzazione da transient
			initSemaphorePorteApplicativeByFiltroRicerca();
		}
		
		this.semaphorePorteApplicativeByFiltroRicerca.acquireThrowRuntime("addPorteApplicativeByFiltroRicerca", idTransazione);
		try {
			if(this.listPorteApplicativeByFiltroRicerca==null) {
				this.listPorteApplicativeByFiltroRicerca=new HashMap<String, List<IDPortaApplicativa>>(3);
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
	
	public List<IDPortaDelegata> getPorteDelegateByFiltroRicerca(FiltroRicercaPorteDelegate filtro) {
		if(this.listPorteDelegateByFiltroRicerca==null) {
			return null;
		}
		String keyCache = filtro.toString();
		List<IDPortaDelegata> l = this.listPorteDelegateByFiltroRicerca.get(keyCache);
		return l;
	}
	public Map<String, List<IDPortaDelegata>> getListPorteDelegateByFiltroRicerca() {
		return this.listPorteDelegateByFiltroRicerca;
	}
	private synchronized void initSemaphorePorteDelegateByFiltroRicerca() {
		if(this.semaphorePorteDelegateByFiltroRicerca==null) {
			this.semaphorePorteDelegateByFiltroRicerca = new org.openspcoop2.utils.Semaphore("RequestConfigPorteDelegateByFiltroRicerca");
		}
	}
	public void addPorteDelegateByFiltroRicerca(FiltroRicercaPorteDelegate filtro, List<IDPortaDelegata> list, String idTransazione) {
		if(this.semaphorePorteDelegateByFiltroRicerca==null) {
			// serializzazione da transient
			initSemaphorePorteDelegateByFiltroRicerca();
		}
		
		this.semaphorePorteDelegateByFiltroRicerca.acquireThrowRuntime("addPorteDelegateByFiltroRicerca", idTransazione);
		try {
			if(this.listPorteDelegateByFiltroRicerca==null) {
				this.listPorteDelegateByFiltroRicerca=new HashMap<String, List<IDPortaDelegata>>(3);
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
				this.ruolo=new HashMap<String, Ruolo>(3);
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
		List<String> l = new ArrayList<String>();
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
				this.scope=new HashMap<String, Scope>(3);
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
		List<String> l = new ArrayList<String>();
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
	
	public void addForwardProxy(String key, Object fp, String idTransazione) {
		if(this.semaphore==null) {
			// serializzazione da transient
			initSemaphore();
		}
		
		this.semaphore.acquireThrowRuntime("addForwardProxy", idTransazione);
		try {
			if(this.forwardProxy==null) {
				this.forwardProxy=new HashMap<String, Object>(3);
			}
			this.forwardProxy.put(key, fp);
		}finally {
			this.semaphore.release("addForwardProxy", idTransazione);
		}
	}
	public Object getForwardProxy(String key) {
		if(this.forwardProxy==null) {
			return null;
		}
		return this.forwardProxy.get(key);
	}
	public List<String> getForwardProxyKeys(){
		List<String> l = new ArrayList<String>();
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
				this.mapCredenziali = new HashMap<TipoCredenzialeMittente, Map<String,CredenzialeMittente>>();
			}
			Map<String, CredenzialeMittente> map = this.mapCredenziali.get(tipo);
			if(map==null) {
				map = new HashMap<String, CredenzialeMittente>();
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
	
	public void addPolicyValidazioneToken(String key, Object fp, String idTransazione) {
		if(this.semaphorePolicy==null) {
			// serializzazione da transient
			initSemaphorePolicy();
		}
		
		this.semaphorePolicy.acquireThrowRuntime("addPolicyValidazioneToken", idTransazione);
		try {
			if(this.policyValidazioneToken==null) {
				this.policyValidazioneToken = new HashMap<String, Object>(3);
			}
			this.policyValidazioneToken.put(key, fp);
		}finally {
			this.semaphorePolicy.release("addPolicyValidazioneToken", idTransazione);
		}
	}
	public Object getPolicyValidazioneToken(String key) {
		if(this.policyValidazioneToken==null) {
			return null;
		}
		return this.policyValidazioneToken.get(key);
	}
	public List<String> getPolicyValidazioneTokenKeys(){
		List<String> l = new ArrayList<String>();
		if(this.policyValidazioneToken!=null && !this.policyValidazioneToken.isEmpty()) {
			l.addAll(this.policyValidazioneToken.keySet());
		}
		return l;
	}
	
	public void addPolicyNegoziazioneToken(String key, Object fp, String idTransazione) {
		
		if(this.semaphorePolicy==null) {
			// serializzazione da transient
			initSemaphorePolicy();
		}
		
		this.semaphorePolicy.acquireThrowRuntime("addPolicyNegoziazioneToken", idTransazione);
		try {
			if(this.policyNegoziazioneToken==null) {
				this.policyNegoziazioneToken = new HashMap<String, Object>(3);
			}
			this.policyNegoziazioneToken.put(key, fp);
		}finally {
			this.semaphorePolicy.release("addPolicyNegoziazioneToken", idTransazione);
		}
	}
	public Object getPolicyNegoziazioneToken(String key) {
		if(this.policyNegoziazioneToken==null) {
			return null;
		}
		return this.policyNegoziazioneToken.get(key);
	}
	public List<String> getPolicyNegoziazioneTokenKeys(){
		List<String> l = new ArrayList<String>();
		if(this.policyNegoziazioneToken!=null && !this.policyNegoziazioneToken.isEmpty()) {
			l.addAll(this.policyNegoziazioneToken.keySet());
		}
		return l;
	}
	
	public void addAttributeAuthority(String key, Object fp, String idTransazione) {
		if(this.semaphorePolicy==null) {
			// serializzazione da transient
			initSemaphorePolicy();
		}
		
		this.semaphorePolicy.acquireThrowRuntime("addAttributeAuthority", idTransazione);
		try {
			if(this.attributeAuthority==null) {
				this.attributeAuthority = new HashMap<String, Object>(3);
			}
			this.attributeAuthority.put(key, fp);
		}finally {
			this.semaphorePolicy.release("addAttributeAuthority", idTransazione);
		}
	}
	public Object getAttributeAuthority(String key) {
		if(this.attributeAuthority==null) {
			return null;
		}
		return this.attributeAuthority.get(key);
	}
	public List<String> getAttributeAuthorityKeys(){
		List<String> l = new ArrayList<String>();
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
	
	public void addMerlinTruststore(String key, Object merlinTruststore, String idTransazione) {
		
		if(this.semaphoreStore==null) {
			// serializzazione da transient
			initSemaphoreStore();
		}
		
		this.semaphoreStore.acquireThrowRuntime("addMerlinTruststore", idTransazione);
		try {
			if(this.merlinTruststore==null) {
				this.merlinTruststore = new HashMap<String, Object>(3);
			}
			this.merlinTruststore.put(key, merlinTruststore);
		}finally {
			this.semaphoreStore.release("addMerlinTruststore", idTransazione);
		}
	}
	public Object getMerlinTruststore(String key) {
		if(this.merlinTruststore==null) {
			return null;
		}
		return this.merlinTruststore.get(key);
	}
	
	public void addMerlinKeystore(String key, Object merlinKeystore, String idTransazione) {
		
		if(this.semaphoreStore==null) {
			// serializzazione da transient
			initSemaphoreStore();
		}
		
		this.semaphoreStore.acquireThrowRuntime("addMerlinKeystore", idTransazione);
		try {
			if(this.merlinKeystore==null) {
				this.merlinKeystore = new HashMap<String, Object>(3);
			}
			this.merlinKeystore.put(key, merlinKeystore);
		}finally {
			this.semaphoreStore.release("addMerlinKeystore", idTransazione);
		}
	}
	public Object getMerlinKeystore(String key) {
		if(this.merlinKeystore==null) {
			return null;
		}
		return this.merlinKeystore.get(key);
	}
	
	public void addSymmetricKeystore(String key, Object symmetricKeystore, String idTransazione) {
		 
		
		if(this.semaphoreStore==null) {
			// serializzazione da transient
			initSemaphoreStore();
		}
		
		this.semaphoreStore.acquireThrowRuntime("addSymmetricKeystore", idTransazione);
		try {
			if(this.symmetricKeystore==null) {
				this.symmetricKeystore = new HashMap<String, Object>(3);
			}
			this.symmetricKeystore.put(key, symmetricKeystore);
		}finally {
			this.semaphoreStore.release("addSymmetricKeystore", idTransazione);
		}
	}
	public Object getSymmetricKeystore(String key) {
		if(this.symmetricKeystore==null) {
			return null;
		}
		return this.symmetricKeystore.get(key);
	}
	
	public void addMultiKeystore(String key, Object multiKeystore, String idTransazione) {
		 
		
		if(this.semaphoreStore==null) {
			// serializzazione da transient
			initSemaphoreStore();
		}
		
		this.semaphoreStore.acquireThrowRuntime("addMultiKeystore", idTransazione);
		try {
			if(this.multiKeystore==null) {
				this.multiKeystore = new HashMap<String, Object>(3);
			}
			this.multiKeystore.put(key, multiKeystore);
		}finally {
			this.semaphoreStore.release("addMultiKeystore", idTransazione);
		}
	}
	public Object getMultiKeystore(String key) {
		if(this.multiKeystore==null) {
			return null;
		}
		return this.multiKeystore.get(key);
	}
	
	public void addJWKSetStore(String key, Object jwkSetStore, String idTransazione) {
		 
		
		if(this.semaphoreStore==null) {
			// serializzazione da transient
			initSemaphoreStore();
		}
		
		this.semaphoreStore.acquireThrowRuntime("addJWKSetStore", idTransazione);
		try {
			if(this.jwkSetStore==null) {
				this.jwkSetStore = new HashMap<String, Object>(3);
			}
			this.jwkSetStore.put(key, jwkSetStore);
		}finally {
			this.semaphoreStore.release("addJWKSetStore", idTransazione);
		}
	}
	public Object getJWKSetStore(String key) {
		if(this.jwkSetStore==null) {
			return null;
		}
		return this.jwkSetStore.get(key);
	}

	public void addHttpStore(String key, Object httpStore, String idTransazione) {
		 
		
		if(this.semaphoreStore==null) {
			// serializzazione da transient
			initSemaphoreStore();
		}
		
		this.semaphoreStore.acquireThrowRuntime("addHttpStore", idTransazione);
		try {
			if(this.httpStore==null) {
				this.httpStore = new HashMap<String, Object>(3);
			}
			this.httpStore.put(key, httpStore);
		}finally {
			this.semaphoreStore.release("addHttpStore", idTransazione);
		}
	}
	public Object getHttpStore(String key) {
		if(this.httpStore==null) {
			return null;
		}
		return this.httpStore.get(key);
	}
	
	public void addCRLCertstore(String key, Object crlCertstore, String idTransazione) {
		 
		
		if(this.semaphoreStore==null) {
			// serializzazione da transient
			initSemaphoreStore();
		}
		
		this.semaphoreStore.acquireThrowRuntime("addCRLCertstore", idTransazione);
		try {
			if(this.crlCertstore==null) {
				this.crlCertstore = new HashMap<String, Object>(3);
			}
			this.crlCertstore.put(key, crlCertstore);
		}finally {
			this.semaphoreStore.release("addCRLCertstore", idTransazione);
		}
	}
	public Object getCRLCertstore(String key) {
		if(this.crlCertstore==null) {
			return null;
		}
		return this.crlCertstore.get(key);
	}
	
	public void addSSLSocketFactory(String key, Object sslSocketFactory, String idTransazione) {
		 
		
		if(this.semaphoreStore==null) {
			// serializzazione da transient
			initSemaphoreStore();
		}
		
		this.semaphoreStore.acquireThrowRuntime("addSSLSocketFactory", idTransazione);
		try {
			if(this.sslSocketFactory==null) {
				this.sslSocketFactory = new HashMap<String, Object>(3);
			}
			this.sslSocketFactory.put(key, sslSocketFactory);
		}finally {
			this.semaphoreStore.release("addSSLSocketFactory", idTransazione);
		}
	}
	public Object getSSLSocketFactory(String key) {
		if(this.sslSocketFactory==null) {
			return null;
		}
		return this.sslSocketFactory.get(key);
	}

	
	private synchronized void initSemaphoreTemplate() {
		if(this.semaphoreTemplate==null) {
			this.semaphoreTemplate = new org.openspcoop2.utils.Semaphore("RequestConfigTemplate");
		}
	}
	
	public void addTemplate(String key, Object fp, String idTransazione) {
		if(this.semaphoreTemplate==null) {
			// serializzazione da transient
			initSemaphoreTemplate();
		}
		
		this.semaphoreTemplate.acquireThrowRuntime("addTemplate", idTransazione);
		try {
			if(this.template==null) {
				this.template = new HashMap<String, Object>(3);
			}
			this.template.put(key, fp);
		}finally {
			this.semaphoreTemplate.release("addTemplate", idTransazione);
		}
	}
	public Object getTemplate(String key) {
		if(this.template==null) {
			return null;
		}
		return this.template.get(key);
	}

}
