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

package org.openspcoop2.protocol.registry;

import java.io.Serializable;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

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
import org.openspcoop2.core.registry.PortaDominio;
import org.openspcoop2.core.registry.Ruolo;
import org.openspcoop2.core.registry.Scope;
import org.openspcoop2.core.registry.Soggetto;
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
import org.openspcoop2.protocol.sdk.IProtocolFactory;
import org.openspcoop2.protocol.sdk.Servizio;
import org.openspcoop2.protocol.sdk.constants.InformationApiSource;
import org.openspcoop2.protocol.sdk.state.IState;
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


	public static RegistroServiziManager getInstance(){
		return new RegistroServiziManager();
	}
	public static RegistroServiziManager getInstance(IState ... state){
		return new RegistroServiziManager(state);
	}

	
	private RegistroServiziReader registroServiziReader = null;
	private List<StateMessage> stati = new ArrayList<StateMessage>();
	
	
	public RegistroServiziManager(IState ... state){
		this.registroServiziReader = RegistroServiziReader.getInstance();
		if(state!=null){
			for (int i = 0; i < state.length; i++) {
				if(state[i] instanceof StateMessage){
					this.stati.add((StateMessage)state[i]);
				}
			}
		}
	}
	
	public void updateState(IState ... state){
		this.stati.clear();
		if(state!=null){
			for (int i = 0; i < state.length; i++) {
				if(state[i] instanceof StateMessage){
					this.stati.add((StateMessage)state[i]);
				}
			}
		}
	}
	
	private Connection getConnection() {
		if(this.stati.size()>0){
			for (StateMessage state : this.stati) {
				boolean validConnection = false;
				try{
					validConnection = !state.getConnectionDB().isClosed();
				}catch(Exception e){}
				if(validConnection)
					return state.getConnectionDB();
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
	
	public String getProfiloGestioneFruizioneServizio(IDServizio idServizio,String nomeRegistro) 
		throws DriverRegistroServiziException,DriverRegistroServiziNotFound{
		return this.registroServiziReader.getProfiloGestioneFruizioneServizio(this.getConnection(), idServizio, nomeRegistro);
	}
	
	public String getProfiloGestioneErogazioneServizio(IDSoggetto idFruitore,IDServizio idServizio,String nomeRegistro) 
			throws DriverRegistroServiziException,DriverRegistroServiziNotFound{
		return this.registroServiziReader.getProfiloGestioneErogazioneServizio(this.getConnection(), idFruitore, idServizio, nomeRegistro);
	}
	
	public String getProfiloGestioneSoggetto(IDSoggetto idSoggetto,String nomeRegistro) 
			throws DriverRegistroServiziException,DriverRegistroServiziNotFound{
		return this.registroServiziReader.getProfiloGestioneSoggetto(this.getConnection(), idSoggetto, nomeRegistro);
	}
	
	
	/* ********  R I C E R C A   I N F O     S E R V I Z I  ******** */ 
	
	public Servizio getInfoServizio(IDSoggetto idSoggetto, IDServizio idService,String nomeRegistro, boolean verificaEsistenzaServizioAzioneCorrelato, boolean throwAzioneNotFound)
			throws DriverRegistroServiziException,DriverRegistroServiziNotFound,DriverRegistroServiziAzioneNotFound,DriverRegistroServiziPortTypeNotFound,DriverRegistroServiziCorrelatoNotFound{
		return this.registroServiziReader.getInfoServizio(this.getConnection(), idSoggetto, idService, nomeRegistro, verificaEsistenzaServizioAzioneCorrelato, throwAzioneNotFound);
	}
	
	public Servizio getInfoServizioCorrelato(IDSoggetto idSoggetto,IDServizio idService,String nomeRegistro, boolean throwAzioneNotFound) 
			throws DriverRegistroServiziException,DriverRegistroServiziNotFound,DriverRegistroServiziAzioneNotFound,DriverRegistroServiziPortTypeNotFound,DriverRegistroServiziCorrelatoNotFound{
		return this.registroServiziReader.getInfoServizioCorrelato(this.getConnection(), idSoggetto, idService, nomeRegistro, throwAzioneNotFound);
	}
	
	public Servizio getInfoServizioAzioneCorrelata(IDSoggetto idSoggetto,IDServizio idService,String nomeRegistro, boolean throwAzioneNotFound) 
			throws DriverRegistroServiziException,DriverRegistroServiziNotFound,DriverRegistroServiziAzioneNotFound,DriverRegistroServiziPortTypeNotFound,DriverRegistroServiziCorrelatoNotFound{
		return this.registroServiziReader.getInfoServizioAzioneCorrelata(this.getConnection(), idSoggetto, idService, nomeRegistro, throwAzioneNotFound);
	}
	
	public Allegati getAllegati(IDServizio idASPS)throws DriverRegistroServiziException,DriverRegistroServiziNotFound{
		return this.registroServiziReader.getAllegati(this.getConnection(), idASPS);
	}
	
	public org.openspcoop2.core.registry.wsdl.AccordoServizioWrapper getWsdlAccordoServizio(IDServizio idService,InformationApiSource infoWsdlSource,boolean buildSchemaXSD)
			throws DriverRegistroServiziException,DriverRegistroServiziNotFound{
		return this.registroServiziReader.getWsdlAccordoServizio(this.getConnection(), idService, infoWsdlSource,buildSchemaXSD);
	}
	
	public org.openspcoop2.core.registry.rest.AccordoServizioWrapper getRestAccordoServizio(IDServizio idService,InformationApiSource infoWsdlSource,boolean buildSchemaXSD, boolean processIncludeForOpenApi)
			throws DriverRegistroServiziException,DriverRegistroServiziNotFound{
		return this.registroServiziReader.getRestAccordoServizio(this.getConnection(), idService, infoWsdlSource,buildSchemaXSD, processIncludeForOpenApi);
	}
	
	public org.openspcoop2.core.registry.constants.ServiceBinding getServiceBinding(IDServizio idService)
			throws DriverRegistroServiziException,DriverRegistroServiziNotFound{
		return this.registroServiziReader.getServiceBinding(this.getConnection(), idService);
	}
	
	public EsitoAutorizzazioneRegistro isFruitoreServizioAutorizzato(String pdd,String servizioApplicativo,IDSoggetto soggetto,IDServizio servizio)
			throws DriverRegistroServiziException,DriverRegistroServiziNotFound, DriverRegistroServiziServizioNotFound{
		return this.registroServiziReader.isFruitoreServizioAutorizzato(this.getConnection(), pdd, servizioApplicativo, soggetto, servizio);
	}
	
	
	/* ********  C O N N E T T O R I  ******** */ 

	public org.openspcoop2.core.config.Connettore getConnettore(IDSoggetto idSoggetto,IDServizio idService,String nomeRegistro) 
			throws DriverRegistroServiziException,DriverRegistroServiziNotFound{
		return this.registroServiziReader.getConnettore(this.getConnection(), idSoggetto, idService, nomeRegistro);
	}
	
	public org.openspcoop2.core.config.Connettore getConnettore(IDSoggetto idSoggetto,String nomeRegistro) 
			throws DriverRegistroServiziException,DriverRegistroServiziNotFound{
		return this.registroServiziReader.getConnettore(this.getConnection(), idSoggetto, nomeRegistro);
	}
	
	
	/* ********  VALIDAZIONE  ******** */ 

	public String getDominio(IDSoggetto idSoggetto,String nomeRegistro,IProtocolFactory<?> protocolFactory) 
			throws DriverRegistroServiziException,DriverRegistroServiziNotFound{
		return this.registroServiziReader.getDominio(this.getConnection(), idSoggetto, nomeRegistro, protocolFactory);
	}
	
	public String getImplementazionePdD(IDSoggetto idSoggetto,String nomeRegistro) 
			throws DriverRegistroServiziException{
		return this.registroServiziReader.getImplementazionePdD(this.getConnection(), idSoggetto, nomeRegistro);
	}
	
	public String getIdPortaDominio(IDSoggetto idSoggetto,String nomeRegistro) 
			throws DriverRegistroServiziException{
		return this.registroServiziReader.getIdPortaDominio(this.getConnection(), idSoggetto, nomeRegistro);
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
	
	
	
	/* ********  R I C E R C A  E L E M E N T I   P R I M I T I V I  ******** */
	
	public PortaDominio getPortaDominio(String nome,String nomeRegistro) throws DriverRegistroServiziException,DriverRegistroServiziNotFound{
		return this.registroServiziReader.getPortaDominio(this.getConnection(), nome, nomeRegistro);
	}

	public Ruolo getRuolo(String nome,String nomeRegistro) throws DriverRegistroServiziException,DriverRegistroServiziNotFound{
		return this.registroServiziReader.getRuolo(this.getConnection(), nome, nomeRegistro);
	}
	public Ruolo getRuolo(IDRuolo idRuolo,String nomeRegistro) throws DriverRegistroServiziException,DriverRegistroServiziNotFound{
		return this.getRuolo(idRuolo.getNome(), nomeRegistro);
	}
	
	public Scope getScope(String nome,String nomeRegistro) throws DriverRegistroServiziException,DriverRegistroServiziNotFound{
		return this.registroServiziReader.getScope(this.getConnection(), nome, nomeRegistro);
	}
	public Scope getScope(IDScope idScope,String nomeRegistro) throws DriverRegistroServiziException,DriverRegistroServiziNotFound{
		return this.getScope(idScope.getNome(), nomeRegistro);
	}

	public Soggetto getSoggetto(IDSoggetto idSoggetto,String nomeRegistro) throws DriverRegistroServiziException,DriverRegistroServiziNotFound{
		return this.registroServiziReader.getSoggetto(this.getConnection(), idSoggetto, nomeRegistro);
	}
	
	public AccordoServizioParteComune getAccordoServizioParteComune(IDAccordo idAccordo,String nomeRegistro,Boolean readContenutiAllegati) throws DriverRegistroServiziException,DriverRegistroServiziNotFound{
		return this.registroServiziReader.getAccordoServizioParteComune(this.getConnection(), idAccordo, readContenutiAllegati, nomeRegistro);
	}
	
	public AccordoServizioParteSpecifica getAccordoServizioParteSpecifica(IDServizio idServizio,String nomeRegistro,Boolean readContenutiAllegati) throws DriverRegistroServiziException,DriverRegistroServiziNotFound{
		return this.registroServiziReader.getAccordoServizioParteSpecifica(this.getConnection(), idServizio, readContenutiAllegati, nomeRegistro);
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
