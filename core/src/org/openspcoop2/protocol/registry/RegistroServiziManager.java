/*
 * OpenSPCoop v2 - Customizable SOAP Message Broker 
 * http://www.openspcoop2.org
 * 
 * Copyright (c) 2005-2016 Link.it srl (http://link.it). 
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
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

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.openspcoop2.core.commons.CoreException;
import org.openspcoop2.core.id.IDAccordo;
import org.openspcoop2.core.id.IDAccordoCooperazione;
import org.openspcoop2.core.id.IDServizio;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.core.registry.AccordoCooperazione;
import org.openspcoop2.core.registry.AccordoServizioParteComune;
import org.openspcoop2.core.registry.AccordoServizioParteSpecifica;
import org.openspcoop2.core.registry.driver.DriverRegistroServiziAzioneNotFound;
import org.openspcoop2.core.registry.driver.DriverRegistroServiziCorrelatoNotFound;
import org.openspcoop2.core.registry.driver.DriverRegistroServiziException;
import org.openspcoop2.core.registry.driver.DriverRegistroServiziNotFound;
import org.openspcoop2.core.registry.driver.DriverRegistroServiziPortTypeNotFound;
import org.openspcoop2.core.registry.driver.DriverRegistroServiziServizioNotFound;
import org.openspcoop2.core.registry.wsdl.AccordoServizioWrapper;
import org.openspcoop2.protocol.sdk.IProtocolFactory;
import org.openspcoop2.protocol.sdk.Servizio;
import org.openspcoop2.protocol.sdk.state.IState;
import org.openspcoop2.protocol.sdk.state.StateMessage;

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
	

	
	
	/* ********  U T I L S  ******** */ 
	
	public void isAlive(boolean controlloTotale) throws CoreException{
		this.registroServiziReader.isAlive(controlloTotale);
	}
	
	public void validazioneSemantica(boolean controlloTotale,boolean verificaURI, 
			String[] tipiSoggettiValidi,String [] tipiServiziValidi, String[] tipiConnettoriValidi,
			boolean validazioneSemanticaAbilitataXML,boolean validazioneSemanticaAbilitataAltriRegistri,
			Logger logConsole) throws CoreException{
		this.registroServiziReader.validazioneSemantica(controlloTotale, verificaURI, tipiSoggettiValidi, tipiServiziValidi, 
				tipiConnettoriValidi, validazioneSemanticaAbilitataXML, validazioneSemanticaAbilitataAltriRegistri, logConsole);
	}
	
	public void setValidazioneSemanticaModificaRegistroServiziXML(boolean verificaURI, 
			String[] tipiSoggettiValidi,String [] tipiServiziValidi, String[] tipiConnettoriValidi) throws CoreException{
		this.registroServiziReader.setValidazioneSemanticaModificaRegistroServiziXML(verificaURI, tipiSoggettiValidi, tipiServiziValidi, tipiConnettoriValidi);
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
	
	public Servizio getInfoServizio(IDSoggetto idSoggetto, IDServizio idService,String nomeRegistro, boolean verificaEsistenzaServizioAzioneCorrelato)
			throws DriverRegistroServiziException,DriverRegistroServiziNotFound,DriverRegistroServiziAzioneNotFound,DriverRegistroServiziPortTypeNotFound,DriverRegistroServiziCorrelatoNotFound{
		return this.registroServiziReader.getInfoServizio(this.getConnection(), idSoggetto, idService, nomeRegistro, verificaEsistenzaServizioAzioneCorrelato);
	}
	
	public Servizio getInfoServizioCorrelato(IDSoggetto idSoggetto,IDServizio idService,String nomeRegistro) 
			throws DriverRegistroServiziException,DriverRegistroServiziNotFound,DriverRegistroServiziAzioneNotFound,DriverRegistroServiziPortTypeNotFound,DriverRegistroServiziCorrelatoNotFound{
		return this.registroServiziReader.getInfoServizioCorrelato(this.getConnection(), idSoggetto, idService, nomeRegistro);
	}
	
	public Servizio getInfoServizioAzioneCorrelata(IDSoggetto idSoggetto,IDServizio idService,String nomeRegistro) 
			throws DriverRegistroServiziException,DriverRegistroServiziNotFound,DriverRegistroServiziAzioneNotFound,DriverRegistroServiziPortTypeNotFound,DriverRegistroServiziCorrelatoNotFound{
		return this.registroServiziReader.getInfoServizioAzioneCorrelata(this.getConnection(), idSoggetto, idService, nomeRegistro);
	}
	
	public Allegati getAllegati(IDServizio idASPS)throws DriverRegistroServiziException,DriverRegistroServiziNotFound{
		return this.registroServiziReader.getAllegati(this.getConnection(), idASPS);
	}
	
	public AccordoServizioWrapper getWsdlAccordoServizio(IDServizio idService,InformationWsdlSource infoWsdlSource,boolean buildSchemaXSD)
			throws DriverRegistroServiziException,DriverRegistroServiziNotFound{
		return this.registroServiziReader.getWsdlAccordoServizio(this.getConnection(), idService, infoWsdlSource,buildSchemaXSD);
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

	public String getDominio(IDSoggetto idSoggetto,String nomeRegistro,IProtocolFactory protocolFactory) 
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
	
	
	/* ********  R I C E R C A  E L E M E N T I   P R I M I T I V I  ******** */
	
	public AccordoServizioParteComune getAccordoServizioParteComune(IDAccordo idAccordo,String nomeRegistro,Boolean readContenutiAllegati) throws DriverRegistroServiziException,DriverRegistroServiziNotFound{
		return this.registroServiziReader.getAccordoServizioParteComune(this.getConnection(), idAccordo, readContenutiAllegati, nomeRegistro);
	}
	
	public AccordoServizioParteSpecifica getAccordoServizioParteSpecifica(IDServizio idServizio,String nomeRegistro,Boolean readContenutiAllegati) throws DriverRegistroServiziException,DriverRegistroServiziNotFound{
		return this.registroServiziReader.getAccordoServizioParteSpecifica(this.getConnection(), idServizio, readContenutiAllegati, nomeRegistro);
	}
	
	public AccordoCooperazione getAccordoCooperazione(IDAccordoCooperazione idAccordo,String nomeRegistro,Boolean readContenutiAllegati) throws DriverRegistroServiziException,DriverRegistroServiziNotFound{
		return this.registroServiziReader.getAccordoCooperazione(this.getConnection(), idAccordo, readContenutiAllegati, nomeRegistro);
	}
}
