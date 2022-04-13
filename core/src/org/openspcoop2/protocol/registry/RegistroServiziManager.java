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

package org.openspcoop2.protocol.registry;

import java.io.Serializable;
import java.sql.Connection;
import java.util.List;
import java.util.Map;

import org.openspcoop2.core.commons.CoreException;
import org.openspcoop2.core.id.IDAccordo;
import org.openspcoop2.core.id.IDAccordoAzione;
import org.openspcoop2.core.id.IDAccordoCooperazione;
import org.openspcoop2.core.id.IDFruizione;
import org.openspcoop2.core.id.IDPortType;
import org.openspcoop2.core.id.IDPortTypeAzione;
import org.openspcoop2.core.id.IDResource;
import org.openspcoop2.core.id.IDRuolo;
import org.openspcoop2.core.id.IDScope;
import org.openspcoop2.core.id.IDServizio;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.core.registry.AccordoCooperazione;
import org.openspcoop2.core.registry.AccordoServizioParteComune;
import org.openspcoop2.core.registry.AccordoServizioParteSpecifica;
import org.openspcoop2.core.registry.Documento;
import org.openspcoop2.core.registry.PortaDominio;
import org.openspcoop2.core.registry.Ruolo;
import org.openspcoop2.core.registry.Scope;
import org.openspcoop2.core.registry.Soggetto;
import org.openspcoop2.core.registry.constants.TipiDocumentoLivelloServizio;
import org.openspcoop2.core.registry.constants.TipiDocumentoSemiformale;
import org.openspcoop2.core.registry.constants.TipiDocumentoSicurezza;
import org.openspcoop2.core.registry.driver.DriverRegistroServiziAzioneNotFound;
import org.openspcoop2.core.registry.driver.DriverRegistroServiziCorrelatoNotFound;
import org.openspcoop2.core.registry.driver.DriverRegistroServiziException;
import org.openspcoop2.core.registry.driver.DriverRegistroServiziNotFound;
import org.openspcoop2.core.registry.driver.DriverRegistroServiziPortTypeNotFound;
import org.openspcoop2.core.registry.driver.DriverRegistroServiziServizioNotFound;
import org.openspcoop2.core.registry.driver.FiltroRicerca;
import org.openspcoop2.core.registry.driver.FiltroRicercaAccordi;
import org.openspcoop2.core.registry.driver.FiltroRicercaAzioni;
import org.openspcoop2.core.registry.driver.FiltroRicercaFruizioniServizio;
import org.openspcoop2.core.registry.driver.FiltroRicercaOperations;
import org.openspcoop2.core.registry.driver.FiltroRicercaPortTypes;
import org.openspcoop2.core.registry.driver.FiltroRicercaResources;
import org.openspcoop2.core.registry.driver.FiltroRicercaRuoli;
import org.openspcoop2.core.registry.driver.FiltroRicercaScope;
import org.openspcoop2.core.registry.driver.FiltroRicercaServizi;
import org.openspcoop2.core.registry.driver.FiltroRicercaSoggetti;
import org.openspcoop2.core.registry.driver.IDAccordoFactory;
import org.openspcoop2.core.registry.driver.IDServizioFactory;
import org.openspcoop2.protocol.sdk.IProtocolFactory;
import org.openspcoop2.protocol.sdk.Servizio;
import org.openspcoop2.protocol.sdk.constants.InformationApiSource;
import org.openspcoop2.protocol.sdk.state.IState;
import org.openspcoop2.protocol.sdk.state.RequestInfo;
import org.openspcoop2.protocol.sdk.state.StateMessage;
import org.openspcoop2.utils.certificate.CertificateInfo;
import org.openspcoop2.utils.crypt.CryptConfig;
import org.slf4j.Logger;

/**
 * RegistroServiziManager
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class RegistroServiziManager {

	private static RegistroServiziManager staticInstanceWithoutState = null;
	private static synchronized void initStaticInstanceWithoutState(){
		if(staticInstanceWithoutState == null) {
			staticInstanceWithoutState = new RegistroServiziManager();
			staticInstanceWithoutState.singleInstance = true;
		}
	}

	public static RegistroServiziManager getInstance(){
		//return new RegistroServiziManager();
		if(staticInstanceWithoutState == null) {
			if(RegistroServiziReader.getInstance()==null) {
				return new RegistroServiziManager(); // succede all'avvio
			}
			initStaticInstanceWithoutState();
		}
		return staticInstanceWithoutState;
	}
	public static RegistroServiziManager getInstance(IState state){
		if(state!=null && state instanceof StateMessage) {
			return getInstance((StateMessage)state);
		}
		return getInstance();
	}
	public static RegistroServiziManager getInstance(StateMessage state){
		if(state!=null) {
			return new RegistroServiziManager(state);
		}
		return getInstance();
	}
	public static RegistroServiziManager getInstance(IState requestStateParam, IState responseStateParam){
		StateMessage requestState = null;
		StateMessage responseState = null;
		if(requestStateParam!=null && requestStateParam instanceof StateMessage) {
			requestState = (StateMessage) requestStateParam;
		}
		if(responseStateParam!=null && responseStateParam instanceof StateMessage) {
			responseState = (StateMessage) responseStateParam;
		}
		if(requestState!=null || responseState!=null) {
			return new RegistroServiziManager(requestState,responseState);
		}
		return getInstance();
	}
	public static RegistroServiziManager getInstance(StateMessage requestState, StateMessage responseState){
		if(requestState!=null || responseState!=null) {
			return new RegistroServiziManager(requestState,responseState);
		}
		return getInstance();
	}


	private boolean singleInstance = false;
	private RegistroServiziReader registroServiziReader = null;
	private StateMessage state = null;
	private StateMessage responseState = null;

	public StateMessage getState() {
		return this.state;
	}
	public StateMessage getResponseState() {
		return this.responseState;
	}
	
	public RegistroServiziManager(){
		this.registroServiziReader = RegistroServiziReader.getInstance();	
	}
	public RegistroServiziManager(StateMessage state){
		this();
		this.state = state;
	}
	public RegistroServiziManager(StateMessage requestState, StateMessage responseState){
		this();
		this.state = requestState;
		this.responseState = responseState;
	}
	
	public RegistroServiziManager refreshState(IState requestStateParam, IState responseStateParam) {
		StateMessage requestState = null;
		StateMessage responseState = null;
		if(requestStateParam!=null && requestStateParam instanceof StateMessage) {
			requestState = (StateMessage) requestStateParam;
		}
		if(responseStateParam!=null && responseStateParam instanceof StateMessage) {
			responseState = (StateMessage) responseStateParam;
		}
		return refreshState(requestState, responseState);
	}
	public RegistroServiziManager refreshState(StateMessage requestState, StateMessage responseState) {
		if(requestState==null && responseState==null) {
			return getInstance(); // senza stato
		}
		if(this.singleInstance) {
			return RegistroServiziManager.getInstance(requestState, responseState); // inizialmente era senza stato, ora serve
		}
		this.state = requestState;
		this.responseState = responseState;
		return this;
	}
	
	private Connection getConnection() {
		if(this.state!=null) {
			Connection c = StateMessage.getConnection(this.state);
			if(c!=null) {
				return c;
			}
		}
		if(this.responseState!=null) {
			Connection c = StateMessage.getConnection(this.responseState);
			if(c!=null) {
				return c;
			}
		}
		return null;
	}
	

	/* ********  READER  ******** */
	
	public RegistroServiziReader getRegistroServiziReader(){
		return this.registroServiziReader;
	}
	
	
	/* ********  U T I L S  ******** */ 
	
	public void isAlive(boolean controlloTotale) throws CoreException{
		this.registroServiziReader.isAlive(controlloTotale);
	}
	
	public void validazioneSemantica(boolean controlloTotale,boolean verificaURI, 
			String[] tipiSoggettiValidi,String [] tipiServiziSoapValidi, String [] tipiServiziRestValidi, String[] tipiConnettoriValidi,
			boolean validazioneSemanticaAbilitataXML,boolean validazioneSemanticaAbilitataAltriRegistri,
			Logger logConsole) throws CoreException{
		this.registroServiziReader.validazioneSemantica(controlloTotale, verificaURI, tipiSoggettiValidi, tipiServiziSoapValidi, tipiServiziRestValidi,
				tipiConnettoriValidi, validazioneSemanticaAbilitataXML, validazioneSemanticaAbilitataAltriRegistri, logConsole);
	}
	
	public void setValidazioneSemanticaModificaRegistroServiziXML(boolean verificaURI, 
			String[] tipiSoggettiValidi,String [] tipiServiziSoapValidi, String [] tipiServiziRestValidi, String[] tipiConnettoriValidi) throws CoreException{
		this.registroServiziReader.setValidazioneSemanticaModificaRegistroServiziXML(verificaURI, tipiSoggettiValidi, tipiServiziSoapValidi, tipiServiziRestValidi, tipiConnettoriValidi);
	}
	
	public void verificaConsistenzaRegistroServizi() throws DriverRegistroServiziException {
		this.registroServiziReader.verificaConsistenzaRegistroServizi();
	}
		
	
	/* ********  P R O F I L O   D I   G E S T I O N E  ******** */ 
	
	public String getProfiloGestioneFruizioneServizio(IDServizio idServizio,String nomeRegistro, RequestInfo requestInfo) 
		throws DriverRegistroServiziException,DriverRegistroServiziNotFound{
		boolean useRequestInfo = requestInfo!=null && requestInfo.getRequestConfig()!=null && idServizio!=null && idServizio.equals(requestInfo.getRequestConfig().getIdServizio(), false);
		if(useRequestInfo) {
			if(requestInfo.getRequestConfig().getServizioVersioneProtocollo()!=null) {
				return requestInfo.getRequestConfig().getServizioVersioneProtocollo();
			}
		}
		String v = this.registroServiziReader.getProfiloGestioneFruizioneServizio(this.getConnection(), idServizio, nomeRegistro);
		if(useRequestInfo) {
			if(requestInfo.getRequestConfig().getServizioVersioneProtocollo()==null) {
				requestInfo.getRequestConfig().setServizioVersioneProtocollo(v);
			}
		}
		return v;
	}
	
	public String getProfiloGestioneErogazioneServizio(IDSoggetto idFruitore,IDServizio idServizio,String nomeRegistro, RequestInfo requestInfo) 
			throws DriverRegistroServiziException,DriverRegistroServiziNotFound{
		// accendendo all'implementazione sottostante si puo' vedere come il parametro idServizio non viene usato
		boolean useRequestInfo = requestInfo!=null && requestInfo.getRequestConfig()!=null && idFruitore!=null;
		boolean useRequestInfoSoggettoFruitore = false;
		boolean useRequestInfoSoggettoErogatore = false;
		if(useRequestInfo) {
			useRequestInfoSoggettoFruitore = useRequestInfo && requestInfo.getRequestConfig().getIdFruitore()!=null && 
					requestInfo.getRequestConfig().getIdFruitore().equals(idFruitore);
			if(useRequestInfoSoggettoFruitore) {
				if(requestInfo.getRequestConfig().getSoggettoFruitoreVersioneProtocollo()!=null) {
					return requestInfo.getRequestConfig().getSoggettoFruitoreVersioneProtocollo();
				}
			}
			else {
				useRequestInfoSoggettoErogatore = useRequestInfo && requestInfo.getRequestConfig().getIdServizio()!=null && 
					requestInfo.getRequestConfig().getIdServizio().getSoggettoErogatore()!=null &&
					requestInfo.getRequestConfig().getIdServizio().getSoggettoErogatore().equals(idFruitore);
				if(useRequestInfoSoggettoErogatore) {
					if(requestInfo.getRequestConfig().getSoggettoErogatoreVersioneProtocollo()!=null) {
						return requestInfo.getRequestConfig().getSoggettoErogatoreVersioneProtocollo();
					}
				}
			}
		}
		String v = this.registroServiziReader.getProfiloGestioneErogazioneServizio(this.getConnection(), idFruitore, idServizio, nomeRegistro);
		if(useRequestInfoSoggettoFruitore) {
			if(requestInfo.getRequestConfig().getSoggettoFruitoreVersioneProtocollo()==null) {
				requestInfo.getRequestConfig().setSoggettoFruitoreVersioneProtocollo(v);
			}
		}
		else if(useRequestInfoSoggettoErogatore) {
			if(requestInfo.getRequestConfig().getSoggettoErogatoreVersioneProtocollo()==null) {
				requestInfo.getRequestConfig().setSoggettoErogatoreVersioneProtocollo(v);
			}
		}
		return v;
	}
	
	public String getProfiloGestioneSoggetto(IDSoggetto idSoggetto,String nomeRegistro, RequestInfo requestInfo) 
			throws DriverRegistroServiziException,DriverRegistroServiziNotFound{
		boolean useRequestInfo = requestInfo!=null && requestInfo.getRequestConfig()!=null && idSoggetto!=null;
		boolean useRequestInfoSoggettoFruitore = false;
		boolean useRequestInfoSoggettoErogatore = false;
		if(useRequestInfo) {
			useRequestInfoSoggettoFruitore = useRequestInfo && requestInfo.getRequestConfig().getIdFruitore()!=null && 
					requestInfo.getRequestConfig().getIdFruitore().equals(idSoggetto);
			if(useRequestInfoSoggettoFruitore) {
				if(requestInfo.getRequestConfig().getSoggettoFruitoreVersioneProtocollo()!=null) {
					return requestInfo.getRequestConfig().getSoggettoFruitoreVersioneProtocollo();
				}
			}
			else {
				useRequestInfoSoggettoErogatore = useRequestInfo && requestInfo.getRequestConfig().getIdServizio()!=null && 
					requestInfo.getRequestConfig().getIdServizio().getSoggettoErogatore()!=null &&
					requestInfo.getRequestConfig().getIdServizio().getSoggettoErogatore().equals(idSoggetto);
				if(useRequestInfoSoggettoErogatore) {
					if(requestInfo.getRequestConfig().getSoggettoErogatoreVersioneProtocollo()!=null) {
						return requestInfo.getRequestConfig().getSoggettoErogatoreVersioneProtocollo();
					}
				}
			}
		}
		String v = this.registroServiziReader.getProfiloGestioneSoggetto(this.getConnection(), idSoggetto, nomeRegistro);
		if(useRequestInfoSoggettoFruitore) {
			if(requestInfo.getRequestConfig().getSoggettoFruitoreVersioneProtocollo()==null) {
				requestInfo.getRequestConfig().setSoggettoFruitoreVersioneProtocollo(v);
			}
		}
		else if(useRequestInfoSoggettoErogatore) {
			if(requestInfo.getRequestConfig().getSoggettoErogatoreVersioneProtocollo()==null) {
				requestInfo.getRequestConfig().setSoggettoErogatoreVersioneProtocollo(v);
			}
		}
		return v;
	}
	
	
	/* ********  R I C E R C A   I N F O     S E R V I Z I  ******** */ 
	
	public Servizio getInfoServizio(IDSoggetto idSoggetto, IDServizio idService,String nomeRegistro, boolean verificaEsistenzaServizioAzioneCorrelato, boolean throwAzioneNotFound,
			RequestInfo requestInfo)
			throws DriverRegistroServiziException,DriverRegistroServiziNotFound,DriverRegistroServiziAzioneNotFound,DriverRegistroServiziPortTypeNotFound,DriverRegistroServiziCorrelatoNotFound{
		if(requestInfo!=null && requestInfo.getRequestConfig()!=null && idService!=null && requestInfo.getRequestConfig().getInfoServizio()!=null) {
			if(requestInfo.getRequestConfig().getInfoServizio().getIDServizio().equals(idService)) {
				return requestInfo.getRequestConfig().getInfoServizio();
			}
		}
		Servizio s = this.registroServiziReader.getInfoServizio(this.getConnection(), idSoggetto, idService, nomeRegistro, verificaEsistenzaServizioAzioneCorrelato, throwAzioneNotFound);
		if(requestInfo!=null && requestInfo.getRequestConfig()!=null && requestInfo.getRequestConfig().getInfoServizio()==null) {
			requestInfo.getRequestConfig().setInfoServizio(s);
		}
		return s;
	}
	
	public Servizio getInfoServizioCorrelato(IDSoggetto idSoggetto,IDServizio idService,String nomeRegistro, boolean throwAzioneNotFound,
			RequestInfo requestInfo) 
			throws DriverRegistroServiziException,DriverRegistroServiziNotFound,DriverRegistroServiziAzioneNotFound,DriverRegistroServiziPortTypeNotFound,DriverRegistroServiziCorrelatoNotFound{
		if(requestInfo!=null && requestInfo.getRequestConfig()!=null && idService!=null && requestInfo.getRequestConfig().getInfoServizioCorrelato()!=null) {
			if(requestInfo.getRequestConfig().getInfoServizioCorrelato().getIDServizio().equals(idService)) {
				return requestInfo.getRequestConfig().getInfoServizioCorrelato();
			}
		}
		Servizio s = this.registroServiziReader.getInfoServizioCorrelato(this.getConnection(), idSoggetto, idService, nomeRegistro, throwAzioneNotFound);
		if(requestInfo!=null && requestInfo.getRequestConfig()!=null && requestInfo.getRequestConfig().getInfoServizioCorrelato()==null) {
			requestInfo.getRequestConfig().setInfoServizioCorrelato(s);
		}
		return s;
	}
	
	public Servizio getInfoServizioAzioneCorrelata(IDSoggetto idSoggetto,IDServizio idService,String nomeRegistro, boolean throwAzioneNotFound,
			RequestInfo requestInfo) 
			throws DriverRegistroServiziException,DriverRegistroServiziNotFound,DriverRegistroServiziAzioneNotFound,DriverRegistroServiziPortTypeNotFound,DriverRegistroServiziCorrelatoNotFound{
		if(requestInfo!=null && requestInfo.getRequestConfig()!=null && idService!=null && requestInfo.getRequestConfig().getInfoServizioAzioneCorrelata()!=null) {
			if(requestInfo.getRequestConfig().getInfoServizioAzioneCorrelata().getIDServizio().equals(idService)) {
				return requestInfo.getRequestConfig().getInfoServizioAzioneCorrelata();
			}
		}
		Servizio s = this.registroServiziReader.getInfoServizioAzioneCorrelata(this.getConnection(), idSoggetto, idService, nomeRegistro, throwAzioneNotFound);
		if(requestInfo!=null && requestInfo.getRequestConfig()!=null && requestInfo.getRequestConfig().getInfoServizioAzioneCorrelata()==null) {
			requestInfo.getRequestConfig().setInfoServizioAzioneCorrelata(s);
		}
		return s;
	}
	
	public Allegati getAllegati(IDServizio idASPS)throws DriverRegistroServiziException,DriverRegistroServiziNotFound{
		return this.registroServiziReader.getAllegati(this.getConnection(), idASPS);
	}
	
	public Documento getAllegato(IDAccordo idAccordo, String nome) throws DriverRegistroServiziException,DriverRegistroServiziNotFound{
		return this.registroServiziReader.getAllegato(this.getConnection(), idAccordo, nome);
	}
	public Documento getSpecificaSemiformale(IDAccordo idAccordo, TipiDocumentoSemiformale tipo, String nome)throws DriverRegistroServiziException,DriverRegistroServiziNotFound{
		return this.registroServiziReader.getSpecificaSemiformale(this.getConnection(), idAccordo, tipo, nome);
	}
	
	public Documento getAllegato(IDServizio idASPS, String nome)throws DriverRegistroServiziException,DriverRegistroServiziNotFound{
		return this.registroServiziReader.getAllegato(this.getConnection(), idASPS, nome);
	}
	public Documento getSpecificaSemiformale(IDServizio idASPS, TipiDocumentoSemiformale tipo, String nome)throws DriverRegistroServiziException,DriverRegistroServiziNotFound{
		return this.registroServiziReader.getSpecificaSemiformale(this.getConnection(), idASPS, tipo, nome);
	}
	public Documento getSpecificaSicurezza(IDServizio idASPS, TipiDocumentoSicurezza tipo, String nome)throws DriverRegistroServiziException,DriverRegistroServiziNotFound{
		return this.registroServiziReader.getSpecificaSicurezza(this.getConnection(), idASPS, tipo, nome);
	}
	public Documento getSpecificaLivelloServizio(IDServizio idASPS, TipiDocumentoLivelloServizio tipo, String nome)throws DriverRegistroServiziException,DriverRegistroServiziNotFound{
		return this.registroServiziReader.getSpecificaLivelloServizio(this.getConnection(), idASPS, tipo, nome);
	}
	
	public org.openspcoop2.core.registry.wsdl.AccordoServizioWrapper getWsdlAccordoServizio(IDServizio idService,InformationApiSource infoWsdlSource,boolean buildSchemaXSD, RequestInfo requestInfo)
			throws DriverRegistroServiziException,DriverRegistroServiziNotFound{
		boolean useRequestInfo = requestInfo!=null && requestInfo.getRequestConfig()!=null && idService!=null && infoWsdlSource!=null;
		if(useRequestInfo) {
			org.openspcoop2.core.registry.wsdl.AccordoServizioWrapper w = requestInfo.getRequestConfig().getAsWrapper_soap(infoWsdlSource, buildSchemaXSD);
			if(w!=null) {
				return w;
			}
		}
		org.openspcoop2.core.registry.wsdl.AccordoServizioWrapper w = this.registroServiziReader.getWsdlAccordoServizio(this.getConnection(), idService, infoWsdlSource,buildSchemaXSD);
		if(useRequestInfo && requestInfo.getRequestConfig().getAsWrapper_soap(infoWsdlSource, buildSchemaXSD)==null) {
			requestInfo.getRequestConfig().setAsWrapper_soap(w, infoWsdlSource, buildSchemaXSD, 
					requestInfo!=null ? requestInfo.getIdTransazione() : null);
		}
		return w;
	}
	
	public org.openspcoop2.core.registry.rest.AccordoServizioWrapper getRestAccordoServizio(IDServizio idService,InformationApiSource infoWsdlSource,boolean buildSchemaXSD, boolean processIncludeForOpenApi, RequestInfo requestInfo)
			throws DriverRegistroServiziException,DriverRegistroServiziNotFound{
		boolean useRequestInfo = requestInfo!=null && requestInfo.getRequestConfig()!=null && idService!=null && infoWsdlSource!=null;
		if(useRequestInfo) {
			org.openspcoop2.core.registry.rest.AccordoServizioWrapper w = requestInfo.getRequestConfig().getAsWrapper_rest(infoWsdlSource, buildSchemaXSD, processIncludeForOpenApi);
			if(w!=null) {
				return w;
			}
		}
		org.openspcoop2.core.registry.rest.AccordoServizioWrapper w = this.registroServiziReader.getRestAccordoServizio(this.getConnection(), idService, infoWsdlSource,buildSchemaXSD, processIncludeForOpenApi);
		if(useRequestInfo && requestInfo.getRequestConfig().getAsWrapper_soap(infoWsdlSource, buildSchemaXSD)==null) {
			requestInfo.getRequestConfig().setAsWrapper_rest(w, infoWsdlSource, buildSchemaXSD, processIncludeForOpenApi,
					requestInfo!=null ? requestInfo.getIdTransazione() : null);
		}
		return w;
	}
	
	public org.openspcoop2.core.registry.constants.ServiceBinding getServiceBinding(IDServizio idService, RequestInfo requestInfo)
			throws DriverRegistroServiziException,DriverRegistroServiziNotFound{
		boolean useRequestInfo = requestInfo!=null && requestInfo.getRequestConfig()!=null && idService!=null;
		if(useRequestInfo) {
			if( requestInfo.getRequestConfig().getServiceBinding()!=null) {
				return requestInfo.getRequestConfig().getServiceBinding();
			}
		}
		org.openspcoop2.core.registry.constants.ServiceBinding serviceBinding = this.registroServiziReader.getServiceBinding(this.getConnection(), idService);
		if(useRequestInfo && requestInfo.getRequestConfig().getServiceBinding()==null) {
			requestInfo.getRequestConfig().setServiceBinding(serviceBinding);
		}
		return serviceBinding;
	}
	
	public EsitoAutorizzazioneRegistro isFruitoreServizioAutorizzato(String pdd,String servizioApplicativo,IDSoggetto soggetto,IDServizio servizio)
			throws DriverRegistroServiziException,DriverRegistroServiziNotFound, DriverRegistroServiziServizioNotFound{
		return this.registroServiziReader.isFruitoreServizioAutorizzato(this.getConnection(), pdd, servizioApplicativo, soggetto, servizio);
	}
	
	
	/* ********  C O N N E T T O R I  ******** */ 

	public org.openspcoop2.core.config.Connettore getConnettore(IDSoggetto idSoggetto,IDServizio idService,String nomeRegistro, RequestInfo requestInfo) 
			throws DriverRegistroServiziException,DriverRegistroServiziNotFound{
		boolean useRequestInfo = requestInfo!=null && requestInfo.getRequestConfig()!=null && idSoggetto!=null && idService!=null &&
				requestInfo.getRequestConfig().getIdFruitore()!=null && idSoggetto.equals(requestInfo.getRequestConfig().getIdFruitore()) &&
				requestInfo.getRequestConfig().getIdServizio()!=null && idService.equals(requestInfo.getRequestConfig().getIdServizio(),false);
		if(useRequestInfo) {
			if(requestInfo.getRequestConfig().getConnettoreFrutoreServizio()!=null) {
				return requestInfo.getRequestConfig().getConnettoreFrutoreServizio();
			}
		}
		org.openspcoop2.core.config.Connettore c = this.registroServiziReader.getConnettore(this.getConnection(), idSoggetto, idService, nomeRegistro);
		if(useRequestInfo) {
			if(requestInfo.getRequestConfig().getConnettoreFrutoreServizio()==null) {
				requestInfo.getRequestConfig().setConnettoreFrutoreServizio(c);
			}
		}
		return c;
	}
	
	public org.openspcoop2.core.config.Connettore getConnettore(IDSoggetto idSoggetto,String nomeRegistro, RequestInfo requestInfo) 
			throws DriverRegistroServiziException,DriverRegistroServiziNotFound{
		boolean useRequestInfo = requestInfo!=null && requestInfo.getRequestConfig()!=null && idSoggetto!=null &&
				requestInfo.getRequestConfig().getIdServizio()!=null &&  
				requestInfo.getRequestConfig().getIdServizio().getSoggettoErogatore()!=null &&  
				requestInfo.getRequestConfig().getIdServizio().getSoggettoErogatore().equals(idSoggetto);
		if(useRequestInfo) {
			if(requestInfo.getRequestConfig().getConnettoreSoggettoErogatore()!=null) {
				return requestInfo.getRequestConfig().getConnettoreSoggettoErogatore();
			}
		}
		org.openspcoop2.core.config.Connettore c = this.registroServiziReader.getConnettore(this.getConnection(), idSoggetto, nomeRegistro);
		if(useRequestInfo) {
			if(requestInfo.getRequestConfig().getConnettoreSoggettoErogatore()==null) {
				requestInfo.getRequestConfig().setConnettoreSoggettoErogatore(c);
			}
		}
		return c;
	}
	
	
	/* ********  VALIDAZIONE  ******** */ 

	public String getDominio(IDSoggetto idSoggetto,String nomeRegistro,IProtocolFactory<?> protocolFactory, RequestInfo requestInfo) 
			throws DriverRegistroServiziException,DriverRegistroServiziNotFound{
		boolean useRequestInfo = requestInfo!=null && requestInfo.getRequestConfig()!=null && idSoggetto!=null;
		if(useRequestInfo) {
			if( requestInfo.getRequestConfig().getSoggettoErogatoreConfig()!=null && requestInfo.getRequestConfig().getSoggettoErogatoreIdentificativoPorta()!=null &&
				idSoggetto.getTipo().equals(requestInfo.getRequestConfig().getSoggettoErogatoreConfig().getTipo()) &&
					idSoggetto.getNome().equals(requestInfo.getRequestConfig().getSoggettoErogatoreConfig().getNome())	) {
				return requestInfo.getRequestConfig().getSoggettoErogatoreIdentificativoPorta();
			}
			else if( requestInfo.getRequestConfig().getSoggettoFruitoreConfig()!=null && requestInfo.getRequestConfig().getSoggettoFruitoreIdentificativoPorta()!=null &&
				idSoggetto.getTipo().equals(requestInfo.getRequestConfig().getSoggettoFruitoreConfig().getTipo()) &&
					idSoggetto.getNome().equals(requestInfo.getRequestConfig().getSoggettoFruitoreConfig().getNome())	) {
				return requestInfo.getRequestConfig().getSoggettoFruitoreIdentificativoPorta();
			}
		}
		if(requestInfo!=null && requestInfo.getRequestThreadContext()!=null && requestInfo.getRequestThreadContext().getRequestFruitoreInfo()!=null) {
			if(requestInfo.getRequestThreadContext().getRequestFruitoreInfo().getSoggettoFruitoreConfig()!=null && 
					requestInfo.getRequestThreadContext().getRequestFruitoreInfo().getSoggettoFruitoreIdentificativoPorta()!=null &&
					idSoggetto.getTipo().equals(requestInfo.getRequestThreadContext().getRequestFruitoreInfo().getSoggettoFruitoreConfig().getTipo()) && 
					idSoggetto.getNome().equals(requestInfo.getRequestThreadContext().getRequestFruitoreInfo().getSoggettoFruitoreConfig().getNome())) {
				return requestInfo.getRequestThreadContext().getRequestFruitoreInfo().getSoggettoFruitoreIdentificativoPorta();
			}
		}
		return this.registroServiziReader.getDominio(this.getConnection(), idSoggetto, nomeRegistro, protocolFactory);
		// il set viene effettuato nei service utils per comprendere se si tratta di fruitore o erogatore
	}
	
	public String getImplementazionePdD(IDSoggetto idSoggetto,String nomeRegistro, RequestInfo requestInfo) 
			throws DriverRegistroServiziException{
		boolean useRequestInfo = requestInfo!=null && requestInfo.getRequestConfig()!=null && idSoggetto!=null;
		if(useRequestInfo) {
			if( requestInfo.getRequestConfig().getSoggettoErogatoreConfig()!=null && requestInfo.getRequestConfig().getSoggettoErogatoreImplementazionePdd()!=null &&
				idSoggetto.getTipo().equals(requestInfo.getRequestConfig().getSoggettoErogatoreConfig().getTipo()) &&
					idSoggetto.getNome().equals(requestInfo.getRequestConfig().getSoggettoErogatoreConfig().getNome())	) {
				return requestInfo.getRequestConfig().getSoggettoErogatoreImplementazionePdd();
			}
			else if( requestInfo.getRequestConfig().getSoggettoFruitoreConfig()!=null && requestInfo.getRequestConfig().getSoggettoFruitoreImplementazionePdd()!=null && 
				idSoggetto.getTipo().equals(requestInfo.getRequestConfig().getSoggettoFruitoreConfig().getTipo()) &&
					idSoggetto.getNome().equals(requestInfo.getRequestConfig().getSoggettoFruitoreConfig().getNome())	) {
				return requestInfo.getRequestConfig().getSoggettoFruitoreImplementazionePdd();
			}
		}
		if(requestInfo!=null && requestInfo.getRequestThreadContext()!=null && requestInfo.getRequestThreadContext().getRequestFruitoreInfo()!=null) {
			if(requestInfo.getRequestThreadContext().getRequestFruitoreInfo().getSoggettoFruitoreConfig()!=null && 
					requestInfo.getRequestThreadContext().getRequestFruitoreInfo().getSoggettoFruitoreImplementazionePdd()!=null &&
					idSoggetto.getTipo().equals(requestInfo.getRequestThreadContext().getRequestFruitoreInfo().getSoggettoFruitoreConfig().getTipo()) && 
					idSoggetto.getNome().equals(requestInfo.getRequestThreadContext().getRequestFruitoreInfo().getSoggettoFruitoreConfig().getNome())) {
				return requestInfo.getRequestThreadContext().getRequestFruitoreInfo().getSoggettoFruitoreImplementazionePdd();
			}
		}
		return this.registroServiziReader.getImplementazionePdD(this.getConnection(), idSoggetto, nomeRegistro);
		// il set viene effettuato nei service utils per comprendere se si tratta di fruitore o erogatore
	}
	
	public String getIdPortaDominio(IDSoggetto idSoggetto,String nomeRegistro, RequestInfo requestInfo) 
			throws DriverRegistroServiziException{
		boolean useRequestInfo = requestInfo!=null && requestInfo.getRequestConfig()!=null && idSoggetto!=null;
		if(useRequestInfo) {
			if( requestInfo.getRequestConfig().getSoggettoErogatoreConfig()!=null && requestInfo.getRequestConfig().getSoggettoErogatorePddReaded()!=null && 
				requestInfo.getRequestConfig().getSoggettoErogatorePddReaded() &&
				idSoggetto.getTipo().equals(requestInfo.getRequestConfig().getSoggettoErogatoreConfig().getTipo()) &&
					idSoggetto.getNome().equals(requestInfo.getRequestConfig().getSoggettoErogatoreConfig().getNome())	) {
				return requestInfo.getRequestConfig().getSoggettoErogatorePdd()!=null ? requestInfo.getRequestConfig().getSoggettoErogatorePdd().getNome() : null;
			}
			else if( requestInfo.getRequestConfig().getSoggettoFruitoreConfig()!=null && requestInfo.getRequestConfig().getSoggettoFruitorePddReaded()!=null && 
					requestInfo.getRequestConfig().getSoggettoFruitorePddReaded() && 
				idSoggetto.getTipo().equals(requestInfo.getRequestConfig().getSoggettoFruitoreConfig().getTipo()) &&
					idSoggetto.getNome().equals(requestInfo.getRequestConfig().getSoggettoFruitoreConfig().getNome())	) {
				return requestInfo.getRequestConfig().getSoggettoFruitorePdd()!=null ? requestInfo.getRequestConfig().getSoggettoFruitorePdd().getNome() : null;
			}
		}
		if(requestInfo!=null && requestInfo.getRequestThreadContext()!=null && requestInfo.getRequestThreadContext().getRequestFruitoreInfo()!=null) {
			if( requestInfo.getRequestThreadContext().getRequestFruitoreInfo().getSoggettoFruitoreConfig()!=null && 
					requestInfo.getRequestThreadContext().getRequestFruitoreInfo().getSoggettoFruitorePddReaded()!=null && 
					requestInfo.getRequestThreadContext().getRequestFruitoreInfo().getSoggettoFruitorePddReaded() &&
					idSoggetto.getTipo().equals(requestInfo.getRequestThreadContext().getRequestFruitoreInfo().getSoggettoFruitoreConfig().getTipo()) &&
						idSoggetto.getNome().equals(requestInfo.getRequestThreadContext().getRequestFruitoreInfo().getSoggettoFruitoreConfig().getNome())	) {
					return requestInfo.getRequestThreadContext().getRequestFruitoreInfo().getSoggettoFruitorePdd()!=null ? requestInfo.getRequestThreadContext().getRequestFruitoreInfo().getSoggettoFruitorePdd().getNome() : null;
				}
		}
		return this.registroServiziReader.getIdPortaDominio(this.getConnection(), idSoggetto, nomeRegistro);
		// il set viene effettuato nei service utils per comprendere se si tratta di fruitore o erogatore
	}
	
	public RisultatoValidazione validaServizio(IDSoggetto soggettoFruitore,IDServizio idService,String nomeRegistro)throws DriverRegistroServiziException,DriverRegistroServiziPortTypeNotFound{
		return this.registroServiziReader.validaServizio(this.getConnection(), soggettoFruitore, idService, nomeRegistro);
	}
	
	
	/* ********  A U T E N T I C A Z I O N E   S O G G E T T I  ******** */ 
	
	public Soggetto getSoggettoByCredenzialiBasic(String username, String password, CryptConfig cryptConfig, String nomeRegistro)throws DriverRegistroServiziException,DriverRegistroServiziNotFound{
		return this.registroServiziReader.getSoggettoByCredenzialiBasic(this.getConnection(), username, password, cryptConfig, nomeRegistro);
	}
	
	public Soggetto getSoggettoByCredenzialiApiKey(String username, String password, boolean appId, CryptConfig cryptConfig, String nomeRegistro)throws DriverRegistroServiziException,DriverRegistroServiziNotFound{
		return this.registroServiziReader.getSoggettoByCredenzialiApiKey(this.getConnection(), username, password, appId, cryptConfig, nomeRegistro);
	}
	
	public Soggetto getSoggettoByCredenzialiSsl(String subject, String issuer, String nomeRegistro)throws DriverRegistroServiziException,DriverRegistroServiziNotFound{
		return this.registroServiziReader.getSoggettoByCredenzialiSsl(this.getConnection(), subject, issuer, nomeRegistro);
	}
	
	public Soggetto getSoggettoByCredenzialiSsl(CertificateInfo certificate, boolean strictVerifier, String nomeRegistro)throws DriverRegistroServiziException,DriverRegistroServiziNotFound{
		return this.registroServiziReader.getSoggettoByCredenzialiSsl(this.getConnection(), certificate, strictVerifier, nomeRegistro);
	}
	
	public Soggetto getSoggettoByCredenzialiPrincipal(String principal, String nomeRegistro)throws DriverRegistroServiziException,DriverRegistroServiziNotFound{
		return this.registroServiziReader.getSoggettoByCredenzialiPrincipal(this.getConnection(), principal, nomeRegistro);
	}
	
	public IDSoggetto getIdSoggettoByCredenzialiBasic(String username, String password, CryptConfig cryptConfig, String nomeRegistro)throws DriverRegistroServiziException,DriverRegistroServiziNotFound{
		return this.registroServiziReader.getIdSoggettoByCredenzialiBasic(this.getConnection(), username, password, cryptConfig, nomeRegistro);
	}
	
	public IDSoggetto getIdSoggettoByCredenzialiApiKey(String username, String password, boolean appId, CryptConfig cryptConfig, String nomeRegistro)throws DriverRegistroServiziException,DriverRegistroServiziNotFound{
		return this.registroServiziReader.getIdSoggettoByCredenzialiApiKey(this.getConnection(), username, password, appId, cryptConfig, nomeRegistro);
	}
	
	public IDSoggetto getIdSoggettoByCredenzialiSsl(String subject, String issuer, String nomeRegistro)throws DriverRegistroServiziException,DriverRegistroServiziNotFound{
		return this.registroServiziReader.getIdSoggettoByCredenzialiSsl(this.getConnection(), subject, issuer, nomeRegistro);
	}
	
	public IDSoggetto getIdSoggettoByCredenzialiSsl(CertificateInfo certificate, boolean strictVerifier, String nomeRegistro)throws DriverRegistroServiziException,DriverRegistroServiziNotFound{
		return this.registroServiziReader.getIdSoggettoByCredenzialiSsl(this.getConnection(), certificate, strictVerifier, nomeRegistro);
	}
	
	public IDSoggetto getIdSoggettoByCredenzialiPrincipal(String principal, String nomeRegistro)throws DriverRegistroServiziException,DriverRegistroServiziNotFound{
		return this.registroServiziReader.getIdSoggettoByCredenzialiPrincipal(this.getConnection(), principal, nomeRegistro);
	}
	
	
	
	/* ********  P R O P R I E T A  ******** */
	
	public Map<String, String> getProprietaConfigurazione(Soggetto soggetto) throws DriverRegistroServiziException {
		return this.registroServiziReader.getProprietaConfigurazione(soggetto);
	}
	
	
	
	/* ********  C E R T I F I C A T I  ******** */
	
	public CertificateCheck checkCertificatoSoggettoWithoutCache(long idSoggetto, int sogliaWarningGiorni,  
			boolean addCertificateDetails, String separator, String newLine) throws DriverRegistroServiziException,DriverRegistroServiziNotFound {
		return this.registroServiziReader.checkCertificatoSoggetto(null, false,
				idSoggetto, sogliaWarningGiorni,  
				addCertificateDetails, separator, newLine);
	}

	public CertificateCheck checkCertificatoSoggettoWithoutCache(IDSoggetto idSoggetto, int sogliaWarningGiorni, 
			boolean addCertificateDetails, String separator, String newLine) throws DriverRegistroServiziException,DriverRegistroServiziNotFound {
		return this.registroServiziReader.checkCertificatoSoggetto(null, false,
				idSoggetto, sogliaWarningGiorni,  
				addCertificateDetails, separator, newLine);
	}
	
	public CertificateCheck checkCertificatiConnettoreHttpsByIdWithoutCache(long idConnettore, int sogliaWarningGiorni,  
			boolean addCertificateDetails, String separator, String newLine) throws DriverRegistroServiziException,DriverRegistroServiziNotFound {
		return this.registroServiziReader.checkCertificatiConnettoreHttpsById(null, false,
				idConnettore, sogliaWarningGiorni,  
				addCertificateDetails, separator, newLine);
	}
	
	public CertificateCheck checkCertificatiModIErogazioneByIdWithoutCache(long idErogazione, int sogliaWarningGiorni,  
			boolean addCertificateDetails, String separator, String newLine) throws DriverRegistroServiziException,DriverRegistroServiziNotFound {
		return this.registroServiziReader.checkCertificatiModIErogazioneById(null, false,
				idErogazione, sogliaWarningGiorni,  
				addCertificateDetails, separator, newLine);
	}
	
	public CertificateCheck checkCertificatiModIFruizioneByIdWithoutCache(long idFruizione, int sogliaWarningGiorni,  
			boolean addCertificateDetails, String separator, String newLine) throws DriverRegistroServiziException,DriverRegistroServiziNotFound {
		return this.registroServiziReader.checkCertificatiModIFruizioneById(null, false,
				idFruizione, sogliaWarningGiorni,  
				addCertificateDetails, separator, newLine);
	}
	
	
	
	/* ********  R I C E R C A  E L E M E N T I   P R I M I T I V I  ******** */
	
	public PortaDominio getPortaDominio(String nome,String nomeRegistro, RequestInfo requestInfo) throws DriverRegistroServiziException,DriverRegistroServiziNotFound{
		boolean useRequestInfo = requestInfo!=null && requestInfo.getRequestConfig()!=null && nome!=null;
		if(useRequestInfo) {
			if( requestInfo.getRequestConfig().getSoggettoErogatorePdd()!=null &&
					nome.equals(requestInfo.getRequestConfig().getSoggettoErogatorePdd().getNome())	) {
				return requestInfo.getRequestConfig().getSoggettoErogatorePdd();
			}
			else if( requestInfo.getRequestConfig().getSoggettoFruitorePdd()!=null &&
					nome.equals(requestInfo.getRequestConfig().getSoggettoFruitorePdd().getNome())	) {
				return requestInfo.getRequestConfig().getSoggettoFruitorePdd();
			}
		}
		if(requestInfo!=null && requestInfo.getRequestThreadContext()!=null && requestInfo.getRequestThreadContext().getRequestFruitoreInfo()!=null) {
			if( requestInfo.getRequestThreadContext().getRequestFruitoreInfo().getSoggettoFruitorePdd()!=null &&
					nome.equals(requestInfo.getRequestThreadContext().getRequestFruitoreInfo().getSoggettoFruitorePdd().getNome())	) {
				return requestInfo.getRequestThreadContext().getRequestFruitoreInfo().getSoggettoFruitorePdd();
			}
		}
		return this.registroServiziReader.getPortaDominio(this.getConnection(), nome, nomeRegistro);
		// il set viene effettuato nei service utils per comprendere se si tratta di fruitore o erogatore
	}

	public Ruolo getRuolo(String nome,String nomeRegistro, RequestInfo requestInfo) throws DriverRegistroServiziException,DriverRegistroServiziNotFound{
		boolean useRequestInfo = requestInfo!=null && requestInfo.getRequestConfig()!=null && nome!=null;
		if(useRequestInfo) {
			Ruolo ruolo = requestInfo.getRequestConfig().getRuolo(nome);
			if(ruolo!=null) {
				return ruolo;
			}
		}
		Ruolo r = this.registroServiziReader.getRuolo(this.getConnection(), nome, nomeRegistro);
		if(useRequestInfo) {
			requestInfo.getRequestConfig().addRuolo(nome, r, 
					requestInfo!=null ? requestInfo.getIdTransazione() : null);
		}
		return r;
	}
	public Ruolo getRuolo(IDRuolo idRuolo,String nomeRegistro, RequestInfo requestInfo) throws DriverRegistroServiziException,DriverRegistroServiziNotFound{
		return this.getRuolo(idRuolo.getNome(), nomeRegistro, requestInfo);
	}
	
	public Scope getScope(String nome,String nomeRegistro, RequestInfo requestInfo) throws DriverRegistroServiziException,DriverRegistroServiziNotFound{
		boolean useRequestInfo = requestInfo!=null && requestInfo.getRequestConfig()!=null && nome!=null;
		if(useRequestInfo) {
			Scope scope = requestInfo.getRequestConfig().getScope(nome);
			if(scope!=null) {
				return scope;
			}
		}
		Scope s = this.registroServiziReader.getScope(this.getConnection(), nome, nomeRegistro);
		if(useRequestInfo) {
			requestInfo.getRequestConfig().addScope(nome, s, 
					requestInfo!=null ? requestInfo.getIdTransazione() : null);
		}
		return s;
	}
	public Scope getScope(IDScope idScope,String nomeRegistro, RequestInfo requestInfo) throws DriverRegistroServiziException,DriverRegistroServiziNotFound{
		return this.getScope(idScope.getNome(), nomeRegistro, requestInfo);
	}

	public Soggetto getSoggetto(IDSoggetto idSoggetto,String nomeRegistro, RequestInfo requestInfo) throws DriverRegistroServiziException,DriverRegistroServiziNotFound{
		boolean useRequestInfo = requestInfo!=null && requestInfo.getRequestConfig()!=null && idSoggetto!=null;
		if(useRequestInfo) {
			if( requestInfo.getRequestConfig().getSoggettoErogatoreRegistry()!=null &&
				idSoggetto.getTipo().equals(requestInfo.getRequestConfig().getSoggettoErogatoreRegistry().getTipo()) &&
					idSoggetto.getNome().equals(requestInfo.getRequestConfig().getSoggettoErogatoreRegistry().getNome())	) {
				return requestInfo.getRequestConfig().getSoggettoErogatoreRegistry();
			}
			else if( requestInfo.getRequestConfig().getSoggettoFruitoreRegistry()!=null &&
				idSoggetto.getTipo().equals(requestInfo.getRequestConfig().getSoggettoFruitoreRegistry().getTipo()) &&
					idSoggetto.getNome().equals(requestInfo.getRequestConfig().getSoggettoFruitoreRegistry().getNome())	) {
				return requestInfo.getRequestConfig().getSoggettoFruitoreRegistry();
			}
		}
		if(requestInfo!=null && requestInfo.getRequestThreadContext()!=null && requestInfo.getRequestThreadContext().getRequestFruitoreInfo()!=null) {
			if(requestInfo.getRequestThreadContext().getRequestFruitoreInfo().getSoggettoFruitoreRegistry()!=null && idSoggetto!=null && 
					idSoggetto.getTipo().equals(requestInfo.getRequestThreadContext().getRequestFruitoreInfo().getSoggettoFruitoreRegistry().getTipo()) && 
					idSoggetto.getNome().equals(requestInfo.getRequestThreadContext().getRequestFruitoreInfo().getSoggettoFruitoreRegistry().getNome())) {
				return requestInfo.getRequestThreadContext().getRequestFruitoreInfo().getSoggettoFruitoreRegistry();
			}
		}
		return this.registroServiziReader.getSoggetto(this.getConnection(), idSoggetto, nomeRegistro);
		// il set viene effettuato nei service utils per comprendere se si tratta di fruitore o erogatore
	}
	
	public AccordoServizioParteComune getAccordoServizioParteComune(IDAccordo idAccordo,String nomeRegistro,Boolean readContenutiAllegati, RequestInfo requestInfo) throws DriverRegistroServiziException,DriverRegistroServiziNotFound{
		boolean useRequestInfo = requestInfo!=null && requestInfo.getRequestConfig()!=null && idAccordo!=null && 
				(readContenutiAllegati==null || readContenutiAllegati==false);
		if(useRequestInfo) {
			if( requestInfo.getRequestConfig().getAspc()!=null) {
				IDAccordoFactory idAccordoFactory = IDAccordoFactory.getInstance();
				String uriAspc = idAccordoFactory.getUriFromAccordo(requestInfo.getRequestConfig().getAspc());
				String uriParam = idAccordoFactory.getUriFromIDAccordo(idAccordo);
				if(uriAspc.equals(uriParam)) {
					return requestInfo.getRequestConfig().getAspc();
				}
			}
		}
		AccordoServizioParteComune aspc = this.registroServiziReader.getAccordoServizioParteComune(this.getConnection(), idAccordo, readContenutiAllegati, nomeRegistro);
		if(useRequestInfo && requestInfo.getRequestConfig().getAspc()==null) {
			requestInfo.getRequestConfig().setAspc(aspc);
		}
		return aspc;
	}
	
	public AccordoServizioParteSpecifica getAccordoServizioParteSpecifica(IDServizio idServizio,String nomeRegistro,Boolean readContenutiAllegati, RequestInfo requestInfo) throws DriverRegistroServiziException,DriverRegistroServiziNotFound{
		boolean useRequestInfo = requestInfo!=null && requestInfo.getRequestConfig()!=null && idServizio!=null && 
				(readContenutiAllegati==null || readContenutiAllegati==false);
		if(useRequestInfo) {
			if( requestInfo.getRequestConfig().getAsps()!=null) {
				IDServizioFactory idServizioFactory = IDServizioFactory.getInstance();
				String uriAsps = idServizioFactory.getUriFromAccordo(requestInfo.getRequestConfig().getAsps());
				String uriParam = idServizioFactory.getUriFromIDServizio(idServizio);
				if(uriAsps.equals(uriParam)) {
					return requestInfo.getRequestConfig().getAsps();
				}
			}
		}
		AccordoServizioParteSpecifica asps = this.registroServiziReader.getAccordoServizioParteSpecifica(this.getConnection(), idServizio, readContenutiAllegati, nomeRegistro);
		if(useRequestInfo && requestInfo.getRequestConfig().getAsps()==null) {
			requestInfo.getRequestConfig().setAsps(asps);
		}
		return asps;
	}
	
	public AccordoCooperazione getAccordoCooperazione(IDAccordoCooperazione idAccordo,String nomeRegistro,Boolean readContenutiAllegati) throws DriverRegistroServiziException,DriverRegistroServiziNotFound{
		return this.registroServiziReader.getAccordoCooperazione(this.getConnection(), idAccordo, readContenutiAllegati, nomeRegistro);
	}

	
		
	/* ********  R I C E R C A  I D   E L E M E N T I   P R I M I T I V I  ******** */
	
	public List<String> getAllIdPorteDominio(FiltroRicerca filtroRicerca,String nomeRegistro) throws DriverRegistroServiziException, DriverRegistroServiziNotFound{
		return this.registroServiziReader.getAllIdPorteDominio(this.getConnection(), filtroRicerca, nomeRegistro);
	}

	public List<IDRuolo> getAllIdRuoli(FiltroRicercaRuoli filtroRicerca,String nomeRegistro) throws DriverRegistroServiziException, DriverRegistroServiziNotFound{
		return this.registroServiziReader.getAllIdRuoli(this.getConnection(), filtroRicerca, nomeRegistro);
	}
	
	public List<IDScope> getAllIdScope(FiltroRicercaScope filtroRicerca,String nomeRegistro) throws DriverRegistroServiziException, DriverRegistroServiziNotFound{
		return this.registroServiziReader.getAllIdScope(this.getConnection(), filtroRicerca, nomeRegistro);
	}
	
	public List<IDSoggetto> getAllIdSoggetti(FiltroRicercaSoggetti filtroRicerca,String nomeRegistro) throws DriverRegistroServiziException, DriverRegistroServiziNotFound{
		return this.registroServiziReader.getAllIdSoggetti(this.getConnection(), filtroRicerca, nomeRegistro);
	}
	
	public List<IDAccordoCooperazione> getAllIdAccordiCooperazione(FiltroRicercaAccordi filtroRicerca,String nomeRegistro) throws DriverRegistroServiziException, DriverRegistroServiziNotFound{
		return this.registroServiziReader.getAllIdAccordiCooperazione(this.getConnection(), filtroRicerca, nomeRegistro);
	}
	
	public List<IDAccordo> getAllIdAccordiServizioParteComune(FiltroRicercaAccordi filtroRicerca,String nomeRegistro) throws DriverRegistroServiziException, DriverRegistroServiziNotFound{
		return this.registroServiziReader.getAllIdAccordiServizioParteComune(this.getConnection(), filtroRicerca, nomeRegistro);
	}
	
	public List<IDPortType> getAllIdPortType(FiltroRicercaPortTypes filtroRicerca,String nomeRegistro) throws DriverRegistroServiziException,DriverRegistroServiziNotFound{
		return this.registroServiziReader.getAllIdPortType(this.getConnection(), filtroRicerca, nomeRegistro);
	}
	
	public List<IDPortTypeAzione> getAllIdAzionePortType(FiltroRicercaOperations filtroRicerca,String nomeRegistro) throws DriverRegistroServiziException,DriverRegistroServiziNotFound{
		return this.registroServiziReader.getAllIdAzionePortType(this.getConnection(), filtroRicerca, nomeRegistro);
	}
	
	public List<IDAccordoAzione> getAllIdAzioneAccordo(FiltroRicercaAzioni filtroRicerca,String nomeRegistro) throws DriverRegistroServiziException,DriverRegistroServiziNotFound{
		return this.registroServiziReader.getAllIdAzioneAccordo(this.getConnection(), filtroRicerca, nomeRegistro);
	}
	
	public List<IDResource> getAllIdResource(FiltroRicercaResources filtroRicerca,String nomeRegistro) throws DriverRegistroServiziException,DriverRegistroServiziNotFound{
		return this.registroServiziReader.getAllIdResource(this.getConnection(), filtroRicerca, nomeRegistro);
	}
	
	public List<IDServizio> getAllIdServizi(FiltroRicercaServizi filtroRicerca,String nomeRegistro) throws DriverRegistroServiziException, DriverRegistroServiziNotFound{
		return this.registroServiziReader.getAllIdServizi(this.getConnection(), filtroRicerca, nomeRegistro);
	}
	
	public List<IDFruizione> getAllIdFruizioniServizio(FiltroRicercaFruizioniServizio filtroRicerca,String nomeRegistro) throws DriverRegistroServiziException, DriverRegistroServiziNotFound{
		return this.registroServiziReader.getAllIdFruizioniServizio(this.getConnection(), filtroRicerca, nomeRegistro);
	}

	
	
	/* ********  R E P O S I T O R Y    O G G E T T I   G E N E R I C I  ******** */
	
	public Serializable getGenericObject(String keyObject) throws DriverRegistroServiziException,DriverRegistroServiziNotFound{
		return this.registroServiziReader.getGenericObject(keyObject);
	}
	
	public Serializable pushGenericObject(String keyObject, Serializable object) throws DriverRegistroServiziException,DriverRegistroServiziNotFound{
		return this.registroServiziReader.pushGenericObject(keyObject, object);
	}

	
	
}
