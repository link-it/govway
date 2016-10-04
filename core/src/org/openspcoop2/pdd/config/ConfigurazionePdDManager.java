/*
 * OpenSPCoop - Customizable API Gateway 
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

package org.openspcoop2.pdd.config;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;

import javax.xml.soap.SOAPEnvelope;

import org.apache.logging.log4j.Level;
import org.slf4j.Logger;
import org.openspcoop2.core.commons.CoreException;
import org.openspcoop2.core.config.AccessoConfigurazione;
import org.openspcoop2.core.config.AccessoDatiAutorizzazione;
import org.openspcoop2.core.config.AccessoRegistro;
import org.openspcoop2.core.config.Connettore;
import org.openspcoop2.core.config.CorrelazioneApplicativa;
import org.openspcoop2.core.config.CorrelazioneApplicativaRisposta;
import org.openspcoop2.core.config.GestioneErrore;
import org.openspcoop2.core.config.MessaggiDiagnostici;
import org.openspcoop2.core.config.PortaApplicativa;
import org.openspcoop2.core.config.PortaDelegata;
import org.openspcoop2.core.config.ServizioApplicativo;
import org.openspcoop2.core.config.StatoServiziPdd;
import org.openspcoop2.core.config.SystemProperties;
import org.openspcoop2.core.config.TipoFiltroAbilitazioneServizi;
import org.openspcoop2.core.config.Tracciamento;
import org.openspcoop2.core.config.ValidazioneContenutiApplicativi;
import org.openspcoop2.core.config.constants.StatoFunzionalitaConWarning;
import org.openspcoop2.core.config.driver.DriverConfigurazioneException;
import org.openspcoop2.core.config.driver.DriverConfigurazioneNotFound;
import org.openspcoop2.core.id.IDPortaApplicativa;
import org.openspcoop2.core.id.IDPortaApplicativaByNome;
import org.openspcoop2.core.id.IDPortaDelegata;
import org.openspcoop2.core.id.IDServizio;
import org.openspcoop2.core.id.IDServizioApplicativo;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.message.OpenSPCoop2Message;
import org.openspcoop2.pdd.core.connettori.ConnettoreMsg;
import org.openspcoop2.pdd.core.integrazione.HeaderIntegrazione;
import org.openspcoop2.protocol.engine.URLProtocolContext;
import org.openspcoop2.protocol.registry.RegistroServiziManager;
import org.openspcoop2.protocol.sdk.IProtocolFactory;
import org.openspcoop2.protocol.sdk.builder.ProprietaErroreApplicativo;
import org.openspcoop2.protocol.sdk.constants.ProfiloDiCollaborazione;
import org.openspcoop2.protocol.sdk.state.IState;
import org.openspcoop2.protocol.sdk.state.StateMessage;

/**
 * ConfigurazionePdDManager
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ConfigurazionePdDManager {


	public static ConfigurazionePdDManager getInstance(){
		return new ConfigurazionePdDManager();
	}
	public static ConfigurazionePdDManager getInstance(IState ... state){
		return new ConfigurazionePdDManager(state);
	}

	
	private ConfigurazionePdDReader configurazionePdDReader = null;
	private RegistroServiziManager registroServiziManager = null;
	private List<StateMessage> stati = new ArrayList<StateMessage>();
	
	public boolean isInitializedConfigurazionePdDReader(){
		return this.configurazionePdDReader!=null;
	}
	
	public ConfigurazionePdDManager(IState ... state){
		this.configurazionePdDReader = ConfigurazionePdDReader.getInstance();
		if(state!=null){
			for (int i = 0; i < state.length; i++) {
				if(state[i] instanceof StateMessage){
					this.stati.add((StateMessage)state[i]);
				}
			}
		}
		this.registroServiziManager = RegistroServiziManager.getInstance(state);
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
		this.registroServiziManager.updateState(state);
	}
	
	private Connection getConnection(){
		
		if(this.stati.size()>0){
			for (StateMessage state : this.stati) {
				if(state!=null && state.getConnectionDB()!=null){
					boolean validConnection = false;
					try{
						validConnection = !state.getConnectionDB().isClosed();
					}catch(Exception e){}
					if(validConnection)
						return state.getConnectionDB();
				}
			}
		}
		return null;
	}
	
	
	/* ********  U T I L S  ******** */ 
	
	public void isAlive() throws CoreException{
		this.configurazionePdDReader.isAlive();
	}

	public void validazioneSemantica(String[] tipiConnettori,String[] tipiMsgDiagnosticoAppender,String[] tipiTracciamentoAppender,
			String [] tipiAutenticazione, String [] tipiAutorizzazione,
			String [] tipiAutorizzazioneContenuto,String [] tipiAutorizzazioneContenutoBuste,
			String [] tipiIntegrazionePD, String [] tipiIntegrazionePA,
			boolean validazioneSemanticaAbilitataXML,boolean validazioneSemanticaAbilitataAltreConfigurazioni,boolean validaConfigurazione,
			Logger logConsole) throws CoreException{
		this.configurazionePdDReader.validazioneSemantica(tipiConnettori, tipiMsgDiagnosticoAppender, tipiTracciamentoAppender, 
				tipiAutenticazione, tipiAutorizzazione, tipiAutorizzazioneContenuto, tipiAutorizzazioneContenutoBuste, 
				tipiIntegrazionePD, tipiIntegrazionePA, validazioneSemanticaAbilitataXML, 
				validazioneSemanticaAbilitataAltreConfigurazioni, validaConfigurazione, logConsole);
	}
	
	public void setValidazioneSemanticaModificaConfigurazionePdDXML(String[] tipiConnettori,
			String[]tipoMsgDiagnosticiAppender,String[]tipoTracciamentoAppender,
			String[]tipoAutenticazione,String[]tipoAutorizzazione,
			String[]tipiAutorizzazioneContenuto,String [] tipiAutorizzazioneContenutoBuste,
			String[]tipoIntegrazionePD,String[]tipoIntegrazionePA) throws CoreException{
		this.configurazionePdDReader.setValidazioneSemanticaModificaConfigurazionePdDXML(tipiConnettori, tipoMsgDiagnosticiAppender, tipoTracciamentoAppender,
				tipoAutenticazione, tipoAutorizzazione, tipiAutorizzazioneContenuto, tipiAutorizzazioneContenutoBuste, tipoIntegrazionePD, tipoIntegrazionePA);
	}
	
	public void verificaConsistenzaConfigurazione() throws DriverConfigurazioneException {
		this.configurazionePdDReader.verificaConsistenzaConfigurazione();
	}
	
	
	/* ********  SOGGETTI (Interfaccia)  ******** */
	
	public IDSoggetto getIDSoggetto(String location,IProtocolFactory protocolFactory) throws DriverConfigurazioneException,DriverConfigurazioneNotFound{ 
		return this.configurazionePdDReader.getIDSoggetto(this.getConnection(), location, protocolFactory);
	}
	
	public String getIdentificativoPorta(IDSoggetto idSoggetto,IProtocolFactory protocolFactory) throws DriverConfigurazioneException,DriverConfigurazioneNotFound{ 
		return this.configurazionePdDReader.getIdentificativoPorta(this.getConnection(), idSoggetto, protocolFactory);
	}
	
	public boolean isSoggettoVirtuale(IDSoggetto idSoggetto) throws DriverConfigurazioneException { 
		return this.configurazionePdDReader.isSoggettoVirtuale(this.getConnection(), idSoggetto);
	}
	
	public boolean existsSoggetto(IDSoggetto idSoggetto)throws DriverConfigurazioneException{  
		return this.configurazionePdDReader.existsSoggetto(this.getConnection(), idSoggetto);
	}
	
	public  HashSet<IDServizio> getServizi_SoggettiVirtuali() throws DriverConfigurazioneException,DriverConfigurazioneNotFound{
		return this.configurazionePdDReader.getServizi_SoggettiVirtuali(this.getConnection());
	}
	
	
	/* ************* ROUTING **************** */
	
	public Connettore getForwardRoute(IDSoggetto idSoggettoDestinatario,boolean functionAsRouter) throws DriverConfigurazioneException,DriverConfigurazioneNotFound{
		return this.configurazionePdDReader.getForwardRoute(this.getConnection(), this.registroServiziManager, idSoggettoDestinatario, functionAsRouter);
	}
	
	public Connettore getForwardRoute(IDSoggetto idSoggettoMittente, IDServizio idServizio,boolean functionAsRouter) throws DriverConfigurazioneException,DriverConfigurazioneNotFound{
		return this.configurazionePdDReader.getForwardRoute(this.getConnection(), this.registroServiziManager, idSoggettoMittente, idServizio, functionAsRouter);
	}
	
	public String getRegistroForImbustamento(IDSoggetto idSoggettoMittente, IDServizio idServizio,boolean functionAsRouter)throws DriverConfigurazioneException{
		return this.configurazionePdDReader.getRegistroForImbustamento(this.getConnection(), this.registroServiziManager, idSoggettoMittente, idServizio, functionAsRouter);
	}
	
	public boolean routerFunctionActive() throws DriverConfigurazioneException,DriverConfigurazioneNotFound{
		return this.configurazionePdDReader.routerFunctionActive(this.getConnection());
	}
	
	public IDSoggetto getRouterIdentity(IProtocolFactory protocolFactory) throws DriverConfigurazioneException,DriverConfigurazioneNotFound{
		return this.configurazionePdDReader.getRouterIdentity(this.getConnection(),protocolFactory);
	}
	
	
	/* ********  URLPrefixRewriter  ******** */
	
	public void setPDUrlPrefixRewriter(org.openspcoop2.core.config.Connettore connettore, IDSoggetto idSoggettoFruitore) throws DriverConfigurazioneNotFound,DriverConfigurazioneException{
		this.configurazionePdDReader.setPDUrlPrefixRewriter(this.getConnection(), connettore, idSoggettoFruitore);
	}
	
	public void setPAUrlPrefixRewriter(org.openspcoop2.core.config.Connettore connettore, IDSoggetto idSoggettoErogatore) throws DriverConfigurazioneNotFound,DriverConfigurazioneException{
		this.configurazionePdDReader.setPAUrlPrefixRewriter(this.getConnection(), connettore, idSoggettoErogatore);
	}
	
	
	/* ********  PORTE DELEGATE (Interfaccia)  ******** */
	
	public PortaDelegata getPortaDelegata(IDPortaDelegata idPD) throws DriverConfigurazioneException, DriverConfigurazioneNotFound{
		return this.configurazionePdDReader.getPortaDelegata(this.getConnection(), idPD);
	}
	
	public PortaDelegata getPortaDelegata_SafeMethod(IDPortaDelegata idPD)throws DriverConfigurazioneException{
		return this.configurazionePdDReader.getPortaDelegata_SafeMethod(this.getConnection(), idPD);		
	}
	
	public boolean identificazioneContentBased(PortaDelegata pd) throws DriverConfigurazioneException,DriverConfigurazioneNotFound{
		return this.configurazionePdDReader.identificazioneContentBased(pd);
	}
	
	public IDServizio getIDServizio(PortaDelegata pd,URLProtocolContext urlProtocolContext,
			OpenSPCoop2Message message, SOAPEnvelope envelope,HeaderIntegrazione headerIntegrazione,boolean readFirstHeaderIntegrazione,
			String soapAction,IProtocolFactory protocolFactory) throws DriverConfigurazioneException,DriverConfigurazioneNotFound,Exception { 
		return this.configurazionePdDReader.getIDServizio(this.registroServiziManager, pd, urlProtocolContext, 
				message, envelope, 
				headerIntegrazione, readFirstHeaderIntegrazione, soapAction, protocolFactory);
	}
	
	public MTOMProcessorConfig getPD_MTOMProcessorForSender(PortaDelegata pd) throws DriverConfigurazioneException{
		return this.configurazionePdDReader.getPD_MTOMProcessorForSender(pd);
	}
	
	public MTOMProcessorConfig getPD_MTOMProcessorForReceiver(PortaDelegata pd) throws DriverConfigurazioneException{
		return this.configurazionePdDReader.getPD_MTOMProcessorForReceiver(pd);
	}
	
	public MessageSecurityConfig getPD_MessageSecurityForSender(PortaDelegata pd) throws DriverConfigurazioneException{
		return this.configurazionePdDReader.getPD_MessageSecurityForSender(pd);
	}
	
	public MessageSecurityConfig getPD_MessageSecurityForReceiver(PortaDelegata pd) throws DriverConfigurazioneException{
		return this.configurazionePdDReader.getPD_MessageSecurityForReceiver(pd);
	}
	
	public String getAutenticazione(PortaDelegata pd) throws DriverConfigurazioneException,DriverConfigurazioneNotFound{ 
		return this.configurazionePdDReader.getAutenticazione(pd);
	}
	
	public String getAutorizzazione(PortaDelegata pd) throws DriverConfigurazioneException,DriverConfigurazioneNotFound{ 
		return this.configurazionePdDReader.getAutorizzazione(pd);
	}
	
	public String getAutorizzazioneContenuto(PortaDelegata pd) throws DriverConfigurazioneException,DriverConfigurazioneNotFound{ 
		return this.configurazionePdDReader.getAutorizzazioneContenuto(pd);
	}
	
	public boolean ricevutaAsincronaSimmetricaAbilitata(PortaDelegata pd) throws DriverConfigurazioneException,DriverConfigurazioneNotFound{
		return this.configurazionePdDReader.ricevutaAsincronaSimmetricaAbilitata(pd);
	}
	
	public boolean ricevutaAsincronaAsimmetricaAbilitata(PortaDelegata pd) throws DriverConfigurazioneException,DriverConfigurazioneNotFound{
		return this.configurazionePdDReader.ricevutaAsincronaAsimmetricaAbilitata(pd);
	}
	
	public ValidazioneContenutiApplicativi getTipoValidazioneContenutoApplicativo(PortaDelegata pd,String implementazionePdDSoggetto) throws DriverConfigurazioneException,DriverConfigurazioneNotFound{
		return this.configurazionePdDReader.getTipoValidazioneContenutoApplicativo(this.getConnection(), pd, implementazionePdDSoggetto);
	}
	
	public CorrelazioneApplicativa getCorrelazioneApplicativa(PortaDelegata pd) throws DriverConfigurazioneException,DriverConfigurazioneNotFound{
		return this.configurazionePdDReader.getCorrelazioneApplicativa(pd);
	}
	
	public CorrelazioneApplicativaRisposta getCorrelazioneApplicativaRisposta(PortaDelegata pd) throws DriverConfigurazioneException,DriverConfigurazioneNotFound{
		return this.configurazionePdDReader.getCorrelazioneApplicativaRisposta(pd);
	}
	
	public String[] getTipiIntegrazione(PortaDelegata pd) throws DriverConfigurazioneException,DriverConfigurazioneNotFound{ 
		return this.configurazionePdDReader.getTipiIntegrazione(pd);
	}
	
	public boolean isGestioneManifestAttachments(PortaDelegata pd, IProtocolFactory protocolFactory) throws DriverConfigurazioneException,DriverConfigurazioneNotFound{
		return this.configurazionePdDReader.isGestioneManifestAttachments(this.getConnection(), pd, protocolFactory);
	}
	
	public boolean isAllegaBody(PortaDelegata pd) throws DriverConfigurazioneException,DriverConfigurazioneNotFound{
		return this.configurazionePdDReader.isAllegaBody(pd);
	}
	
	public boolean isScartaBody(PortaDelegata pd) throws DriverConfigurazioneException,DriverConfigurazioneNotFound{
		return this.configurazionePdDReader.isScartaBody(pd);
	}
	
	public boolean isModalitaStateless(PortaDelegata pd, ProfiloDiCollaborazione profiloCollaborazione) throws DriverConfigurazioneException,DriverConfigurazioneNotFound{
		return this.configurazionePdDReader.isModalitaStateless(pd, profiloCollaborazione);
	}
	
	public boolean isLocalForwardMode(PortaDelegata pd) throws DriverConfigurazioneException,DriverConfigurazioneNotFound{
		return this.configurazionePdDReader.isLocalForwardMode(pd);
	}
	
	public List<Object> getExtendedInfo(PortaDelegata pd)throws DriverConfigurazioneException{
		return this.configurazionePdDReader.getExtendedInfo(pd);
	}
	
	
	
	/* ********  PORTE APPLICATIVE  (Interfaccia) ******** */
	
	public Hashtable<IDSoggetto,PortaApplicativa> getPorteApplicative_SoggettiVirtuali(IDPortaApplicativa idPA)throws DriverConfigurazioneException,DriverConfigurazioneNotFound{
		return this.configurazionePdDReader.getPorteApplicative_SoggettiVirtuali(this.getConnection(), idPA);
	}
	
	public boolean existsPA(RichiestaApplicativa idPA) throws DriverConfigurazioneException{	
		return this.configurazionePdDReader.existsPA(this.getConnection(), idPA);
	}
	
	public IDPortaApplicativaByNome convertTo_SafeMethod(IDServizio idServizio,Hashtable<String,String> proprietaPresentiBustaRicevuta) throws DriverConfigurazioneException, DriverConfigurazioneNotFound{
		return this.configurazionePdDReader.convertTo_SafeMethod(this.getConnection(), idServizio, proprietaPresentiBustaRicevuta);
	}
	public IDPortaApplicativaByNome convertTo(IDServizio idServizio,Hashtable<String,String> proprietaPresentiBustaRicevuta) throws DriverConfigurazioneException, DriverConfigurazioneNotFound{
		return this.configurazionePdDReader.convertTo(this.getConnection(), idServizio, proprietaPresentiBustaRicevuta);
	}
	
	public PortaApplicativa getPortaApplicativa(IDPortaApplicativaByNome idPA) throws DriverConfigurazioneException, DriverConfigurazioneNotFound{
		return this.configurazionePdDReader.getPortaApplicativa(this.getConnection(), idPA);
	}
	
	public PortaApplicativa getPortaApplicativa_SafeMethod(IDPortaApplicativaByNome idPA)throws DriverConfigurazioneException{
		return this.configurazionePdDReader.getPortaApplicativa_SafeMethod(this.getConnection(), idPA);
	}
	
	public String[] getServiziApplicativi(PortaApplicativa pa)throws DriverConfigurazioneException,DriverConfigurazioneNotFound{
		return this.configurazionePdDReader.getServiziApplicativi(pa);
	}
	
	public SoggettoVirtuale getServiziApplicativi_SoggettiVirtuali(RichiestaApplicativa idPA)throws DriverConfigurazioneException,DriverConfigurazioneNotFound{
		return this.configurazionePdDReader.getServiziApplicativi_SoggettiVirtuali(this.getConnection(), idPA);
	}
	
	public List<PortaApplicativa> getPorteApplicative(String nomePA,String tipoSoggettoProprietario,String nomeSoggettoProprietario) throws DriverConfigurazioneException, DriverConfigurazioneNotFound{
		return this.configurazionePdDReader.getPorteApplicative(this.getConnection(), nomePA, tipoSoggettoProprietario, nomeSoggettoProprietario);
	}
	
	public MTOMProcessorConfig getPA_MTOMProcessorForSender(PortaApplicativa pa)throws DriverConfigurazioneException{
		return this.configurazionePdDReader.getPA_MTOMProcessorForSender(pa);
	}
	
	public MTOMProcessorConfig getPA_MTOMProcessorForReceiver(PortaApplicativa pa)throws DriverConfigurazioneException{
		return this.configurazionePdDReader.getPA_MTOMProcessorForReceiver(pa);
	}
	
	public MessageSecurityConfig getPA_MessageSecurityForSender(PortaApplicativa pa)throws DriverConfigurazioneException{
		return this.configurazionePdDReader.getPA_MessageSecurityForSender(pa);
	}
	
	public MessageSecurityConfig getPA_MessageSecurityForReceiver(PortaApplicativa pa)throws DriverConfigurazioneException{
		return this.configurazionePdDReader.getPA_MessageSecurityForReceiver(pa);
	}
	
	public boolean ricevutaAsincronaSimmetricaAbilitata(PortaApplicativa pa) throws DriverConfigurazioneException,DriverConfigurazioneNotFound{
		return this.configurazionePdDReader.ricevutaAsincronaSimmetricaAbilitata(pa);
	}
	
	public boolean ricevutaAsincronaAsimmetricaAbilitata(PortaApplicativa pa) throws DriverConfigurazioneException,DriverConfigurazioneNotFound{
		return this.configurazionePdDReader.ricevutaAsincronaAsimmetricaAbilitata(pa);
	}
	
	public ValidazioneContenutiApplicativi getTipoValidazioneContenutoApplicativo(PortaApplicativa pa,String implementazionePdDSoggetto) throws DriverConfigurazioneException,DriverConfigurazioneNotFound{
		return this.configurazionePdDReader.getTipoValidazioneContenutoApplicativo(this.getConnection(), pa, implementazionePdDSoggetto);
	}
	
	public CorrelazioneApplicativa getCorrelazioneApplicativa(PortaApplicativa pa) throws DriverConfigurazioneException,DriverConfigurazioneNotFound{
		return this.configurazionePdDReader.getCorrelazioneApplicativa(pa);
	}
	
	public CorrelazioneApplicativaRisposta getCorrelazioneApplicativaRisposta(PortaApplicativa pa) throws DriverConfigurazioneException,DriverConfigurazioneNotFound{
		return this.configurazionePdDReader.getCorrelazioneApplicativaRisposta(pa);
	}
	
	public String[] getTipiIntegrazione(PortaApplicativa pa) throws DriverConfigurazioneException,DriverConfigurazioneNotFound { 
		return this.configurazionePdDReader.getTipiIntegrazione(pa);
	}
	
	public boolean isGestioneManifestAttachments(PortaApplicativa pa, IProtocolFactory protocolFactory) throws DriverConfigurazioneException,DriverConfigurazioneNotFound{
		return this.configurazionePdDReader.isGestioneManifestAttachments(this.getConnection(), pa, protocolFactory);
	}
	
	public boolean isAllegaBody(PortaApplicativa pa) throws DriverConfigurazioneException,DriverConfigurazioneNotFound{
		return this.configurazionePdDReader.isAllegaBody(pa);
	}
	
	public boolean isScartaBody(PortaApplicativa pa) throws DriverConfigurazioneException,DriverConfigurazioneNotFound{
		return this.configurazionePdDReader.isScartaBody(pa);
	}
	
	public boolean isModalitaStateless(PortaApplicativa pa, ProfiloDiCollaborazione profiloCollaborazione) throws DriverConfigurazioneException, DriverConfigurazioneNotFound{
		return this.configurazionePdDReader.isModalitaStateless(pa, profiloCollaborazione);
	}
	
	public String getAutorizzazioneContenuto(PortaApplicativa pa) throws DriverConfigurazioneException,DriverConfigurazioneNotFound{
		return this.configurazionePdDReader.getAutorizzazioneContenuto(pa);
	}
	
	public List<Object> getExtendedInfo(PortaApplicativa pa)throws DriverConfigurazioneException{
		return this.configurazionePdDReader.getExtendedInfo(pa);
	}
	
	
	/* ********  Servizi Applicativi (Interfaccia)  ******** */
	
	public boolean existsServizioApplicativo(IDPortaDelegata idPD,String serv) throws DriverConfigurazioneException{ 
		return this.configurazionePdDReader.existsServizioApplicativo(this.getConnection(), idPD, serv);
	}
	
	public ServizioApplicativo getServizioApplicativo(IDPortaDelegata idPD, String serv) throws DriverConfigurazioneNotFound,DriverConfigurazioneException{
		return this.configurazionePdDReader.getServizioApplicativo(this.getConnection(), idPD, serv);
	}
	
	public ServizioApplicativo getServizioApplicativo(IDPortaApplicativa idPA, String serv) throws DriverConfigurazioneNotFound,DriverConfigurazioneException{
		return this.configurazionePdDReader.getServizioApplicativo(this.getConnection(), idPA, serv);
	}
	
	public IDServizioApplicativo autenticazioneHTTP(IDSoggetto aSoggetto,String location, String aUser,String aPassword) throws DriverConfigurazioneException{ 
		return this.configurazionePdDReader.autenticazioneHTTP(this.getConnection(), aSoggetto, location, aUser, aPassword);
	}
	
	public IDServizioApplicativo autenticazioneHTTP(String aUser,String aPassword) throws DriverConfigurazioneException{ 
		return this.configurazionePdDReader.autenticazioneHTTP(this.getConnection(), aUser, aPassword);
	}
	
	public IDServizioApplicativo autenticazioneHTTPS(IDSoggetto aSoggetto,String location, String aSubject) throws DriverConfigurazioneException{ 
		return this.configurazionePdDReader.autenticazioneHTTPS(this.getConnection(), aSoggetto, location, aSubject);
	}
	
	public IDServizioApplicativo autenticazioneHTTPS(String aSubject) throws DriverConfigurazioneException{ 
		return this.configurazionePdDReader.autenticazioneHTTPS(this.getConnection(), aSubject);
	}
	
	public boolean autorizzazione(PortaDelegata pd, String servizio) throws DriverConfigurazioneException,DriverConfigurazioneNotFound{ 
		return this.configurazionePdDReader.autorizzazione(pd, servizio);
	}
	
	public void aggiornaProprietaGestioneErrorePD(ProprietaErroreApplicativo gestioneErrore, ServizioApplicativo sa) throws DriverConfigurazioneException {
		this.configurazionePdDReader.aggiornaProprietaGestioneErrorePD(gestioneErrore, sa);
	}
	
	public boolean invocazionePortaDelegataPerRiferimento(ServizioApplicativo sa) throws DriverConfigurazioneException{
		return this.configurazionePdDReader.invocazionePortaDelegataPerRiferimento(sa);
	}
	
	public boolean invocazionePortaDelegataSbustamentoInformazioniProtocollo(ServizioApplicativo sa) throws DriverConfigurazioneException{
		return this.configurazionePdDReader.invocazionePortaDelegataSbustamentoInformazioniProtocollo(sa);
	}
	
	
	/* ********  Servizi Applicativi (InvocazioneServizio)  ******** */
	
	public boolean invocazioneServizioConGetMessage(ServizioApplicativo sa)throws DriverConfigurazioneException,DriverConfigurazioneNotFound{
		return this.configurazionePdDReader.invocazioneServizioConGetMessage(sa);
	}
	
	public boolean invocazioneServizioConSbustamento(ServizioApplicativo sa)throws DriverConfigurazioneException,DriverConfigurazioneNotFound{
		return this.configurazionePdDReader.invocazioneServizioConSbustamento(sa);
	}
	
	public boolean invocazioneServizioConSbustamentoInformazioniProtocollo(ServizioApplicativo sa)throws DriverConfigurazioneException,DriverConfigurazioneNotFound{
		return this.configurazionePdDReader.invocazioneServizioConSbustamentoInformazioniProtocollo(sa);
	}
	
	public boolean invocazioneServizioConConnettore(ServizioApplicativo sa)throws DriverConfigurazioneException,DriverConfigurazioneNotFound{
		return this.configurazionePdDReader.invocazioneServizioConConnettore(sa);
	}
	
	public ConnettoreMsg getInvocazioneServizio(ServizioApplicativo sa,RichiestaApplicativa idPA)throws DriverConfigurazioneException,DriverConfigurazioneNotFound{
		return this.configurazionePdDReader.getInvocazioneServizio(this.getConnection(), sa, idPA);
	}
	
	public GestioneErrore getGestioneErroreConnettore_InvocazioneServizio(ServizioApplicativo sa)throws DriverConfigurazioneException,DriverConfigurazioneNotFound{
		return this.configurazionePdDReader.getGestioneErroreConnettore_InvocazioneServizio(this.getConnection(), sa);
	}
	
	public boolean invocazioneServizioPerRiferimento(ServizioApplicativo sa)throws DriverConfigurazioneException,DriverConfigurazioneNotFound{
		return this.configurazionePdDReader.invocazioneServizioPerRiferimento(sa);
	}
	
	public boolean invocazioneServizioRispostaPerRiferimento(ServizioApplicativo sa)throws DriverConfigurazioneException,DriverConfigurazioneNotFound{
		return this.configurazionePdDReader.invocazioneServizioRispostaPerRiferimento(sa);
	}
	
	
	/* ********  Servizi Applicativi (RispostAsincrona)  ******** */
	
	public boolean existsConsegnaRispostaAsincrona(ServizioApplicativo sa)throws DriverConfigurazioneException,DriverConfigurazioneNotFound { 
		return this.configurazionePdDReader.existsConsegnaRispostaAsincrona(sa);
	}
	
	public boolean consegnaRispostaAsincronaConGetMessage(ServizioApplicativo sa)throws DriverConfigurazioneException,DriverConfigurazioneNotFound{
		return this.configurazionePdDReader.consegnaRispostaAsincronaConGetMessage(sa);
	}
	
	public boolean consegnaRispostaAsincronaConSbustamento(ServizioApplicativo sa)throws DriverConfigurazioneException,DriverConfigurazioneNotFound{
		return this.configurazionePdDReader.consegnaRispostaAsincronaConSbustamento(sa);
	}
	
	public boolean consegnaRispostaAsincronaConSbustamentoInformazioniProtocollo(ServizioApplicativo sa)throws DriverConfigurazioneException,DriverConfigurazioneNotFound{
		return this.configurazionePdDReader.consegnaRispostaAsincronaConSbustamentoInformazioniProtocollo(sa);
	}
	
	public boolean consegnaRispostaAsincronaConConnettore(ServizioApplicativo sa)throws DriverConfigurazioneException,DriverConfigurazioneNotFound{
		return this.configurazionePdDReader.consegnaRispostaAsincronaConConnettore(sa);
	}
	
	public ConnettoreMsg getConsegnaRispostaAsincrona(ServizioApplicativo sa,RichiestaDelegata idPD)throws DriverConfigurazioneException,DriverConfigurazioneNotFound{
		return this.configurazionePdDReader.getConsegnaRispostaAsincrona(this.getConnection(), sa, idPD);
	}
	
	public ConnettoreMsg getConsegnaRispostaAsincrona(ServizioApplicativo sa,RichiestaApplicativa idPA)throws DriverConfigurazioneException,DriverConfigurazioneNotFound{
		return this.configurazionePdDReader.getConsegnaRispostaAsincrona(this.getConnection(), sa, idPA);
	}
	
	public GestioneErrore getGestioneErroreConnettore_RispostaAsincrona(ServizioApplicativo sa)throws DriverConfigurazioneException,DriverConfigurazioneNotFound{
		return this.configurazionePdDReader.getGestioneErroreConnettore_RispostaAsincrona(this.getConnection(), sa);
	}
	
	public boolean consegnaRispostaAsincronaPerRiferimento(ServizioApplicativo sa)throws DriverConfigurazioneException,DriverConfigurazioneNotFound{
		return this.configurazionePdDReader.consegnaRispostaAsincronaPerRiferimento(sa);
	}
	
	public boolean consegnaRispostaAsincronaRispostaPerRiferimento(ServizioApplicativo sa)throws DriverConfigurazioneException,DriverConfigurazioneNotFound{
		return this.configurazionePdDReader.consegnaRispostaAsincronaRispostaPerRiferimento(sa);
	}
	
	
	/* ********  CONFIGURAZIONE  ******** */
	
	public AccessoRegistro getAccessoRegistroServizi(){
		return this.configurazionePdDReader.getAccessoRegistroServizi(this.getConnection());
	}
	
	public AccessoConfigurazione getAccessoConfigurazione(){
		return this.configurazionePdDReader.getAccessoConfigurazione(this.getConnection());
	}
	
	public AccessoDatiAutorizzazione getAccessoDatiAutorizzazione(){
		return this.configurazionePdDReader.getAccessoDatiAutorizzazione(this.getConnection());
	}
	
	public StatoFunzionalitaConWarning getTipoValidazione(String implementazionePdDSoggetto){
		return this.configurazionePdDReader.getTipoValidazione(this.getConnection(), implementazionePdDSoggetto);
	}
	
	public boolean isLivelloValidazioneNormale(String implementazionePdDSoggetto){
		return this.configurazionePdDReader.isLivelloValidazioneNormale(this.getConnection(), implementazionePdDSoggetto);
	}
	
	public boolean isLivelloValidazioneRigido(String implementazionePdDSoggetto){
		return this.configurazionePdDReader.isLivelloValidazioneRigido(this.getConnection(), implementazionePdDSoggetto);
	}
	
	public boolean isValidazioneProfiloCollaborazione(String implementazionePdDSoggetto){
		return this.configurazionePdDReader.isValidazioneProfiloCollaborazione(this.getConnection(), implementazionePdDSoggetto);
	}
	
	public boolean isValidazioneManifestAttachments(String implementazionePdDSoggetto){
		return this.configurazionePdDReader.isValidazioneManifestAttachments(this.getConnection(), implementazionePdDSoggetto);
	}
	
	public boolean newConnectionForResponse(){
		return this.configurazionePdDReader.newConnectionForResponse(this.getConnection());
	}
	
	public boolean isUtilizzoIndirizzoTelematico(){
		return this.configurazionePdDReader.isUtilizzoIndirizzoTelematico(this.getConnection());
	}
	
	public boolean isGestioneManifestAttachments(){
		return this.configurazionePdDReader.isGestioneManifestAttachments(this.getConnection());
	}
	
	public long getTimeoutRiscontro(){
		return this.configurazionePdDReader.getTimeoutRiscontro(this.getConnection());
	}
	
	public Level getLivello_msgDiagnostici(){
		return this.configurazionePdDReader.getLivello_msgDiagnostici(this.getConnection());
	}
	
	public Level getLivelloLog4J_msgDiagnostici(){
		return this.configurazionePdDReader.getLivelloLog4J_msgDiagnostici(this.getConnection());
	}
	
	public int getSeverita_msgDiagnostici(){
		return this.configurazionePdDReader.getSeverita_msgDiagnostici(this.getConnection());
	}
	
	public int getSeveritaLog4J_msgDiagnostici(){
		return this.configurazionePdDReader.getSeveritaLog4J_msgDiagnostici(this.getConnection());
	}
	
	public MessaggiDiagnostici getOpenSPCoopAppender_MsgDiagnostici(){
		return this.configurazionePdDReader.getOpenSPCoopAppender_MsgDiagnostici(this.getConnection());
	}
	
	public boolean dumpMessaggi(){
		return this.configurazionePdDReader.dumpMessaggi(this.getConnection());
	}
	
	public boolean dumpBinarioPD(){
		return this.configurazionePdDReader.dumpBinarioPD(this.getConnection());
	}
	
	public boolean dumpBinarioPA(){
		return this.configurazionePdDReader.dumpBinarioPA(this.getConnection());
	}
	
	public boolean tracciamentoBuste(){
		return this.configurazionePdDReader.tracciamentoBuste(this.getConnection());
	}
	
	public Tracciamento getOpenSPCoopAppender_Tracciamento(){
		return this.configurazionePdDReader.getOpenSPCoopAppender_Tracciamento(this.getConnection());
	}
	
	public GestioneErrore getGestioneErroreConnettoreComponenteCooperazione(){
		return this.configurazionePdDReader.getGestioneErroreConnettoreComponenteCooperazione(this.getConnection());
	}
	
	public GestioneErrore getGestioneErroreConnettoreComponenteIntegrazione(){
		return this.configurazionePdDReader.getGestioneErroreConnettoreComponenteIntegrazione(this.getConnection());
	}
	
	public String[] getIntegrationManagerAuthentication(){
		return this.configurazionePdDReader.getIntegrationManagerAuthentication(this.getConnection());
	}
	
	public ValidazioneContenutiApplicativi getTipoValidazioneContenutoApplicativo(String implementazionePdDSoggetto) throws DriverConfigurazioneException,DriverConfigurazioneNotFound{
		return this.configurazionePdDReader.getTipoValidazioneContenutoApplicativo(this.getConnection(), implementazionePdDSoggetto);
	}
	
	public Boolean isPDServiceActive(){
		return this.configurazionePdDReader.isPDServiceActive();
	}
	
	public List<TipoFiltroAbilitazioneServizi> getFiltriAbilitazionePDService(){
		return this.configurazionePdDReader.getFiltriAbilitazionePDService();
	}
	
	public List<TipoFiltroAbilitazioneServizi> getFiltriDisabilitazionePDService(){
		return this.configurazionePdDReader.getFiltriDisabilitazionePDService();
	}
	
	public Boolean isPAServiceActive(){
		return this.configurazionePdDReader.isPAServiceActive();
	}
	
	public List<TipoFiltroAbilitazioneServizi> getFiltriAbilitazionePAService(){
		return this.configurazionePdDReader.getFiltriAbilitazionePAService();
	}
	
	public List<TipoFiltroAbilitazioneServizi> getFiltriDisabilitazionePAService(){
		return this.configurazionePdDReader.getFiltriDisabilitazionePAService();
	}
	
	public Boolean isIMServiceActive(){
		return this.configurazionePdDReader.isIMServiceActive();
	}
	
	public StatoServiziPdd getStatoServiziPdD() throws DriverConfigurazioneException{
		return this.configurazionePdDReader.getStatoServiziPdD();
	}
	
	public void updateStatoServiziPdD(StatoServiziPdd servizi) throws DriverConfigurazioneException{
		this.configurazionePdDReader.updateStatoServiziPdD(servizi);
	}
	
	public SystemProperties getSystemPropertiesPdD() throws DriverConfigurazioneException{
		return this.configurazionePdDReader.getSystemPropertiesPdD();
	}
	
	public void updateSystemPropertiesPdD(SystemProperties systemProperties) throws DriverConfigurazioneException{
		this.configurazionePdDReader.updateSystemPropertiesPdD(systemProperties);
	}
	
	public List<Object> getExtendedInfoConfigurazione() throws DriverConfigurazioneException{
		return this.configurazionePdDReader.getExtendedInfoConfigurazione(this.getConnection());
	}
	
	public Object getSingleExtendedInfoConfigurazione(String id) throws DriverConfigurazioneException, DriverConfigurazioneNotFound{
		return this.configurazionePdDReader.getSingleExtendedInfoConfigurazione(id, this.getConnection());
	}
	
	public List<Object> getExtendedInfoConfigurazioneFromCache() throws DriverConfigurazioneException{
		return this.configurazionePdDReader.getExtendedInfoConfigurazioneFromCache(this.getConnection());
	}
	
	public Object getSingleExtendedInfoConfigurazioneFromCache(String id) throws DriverConfigurazioneException, DriverConfigurazioneNotFound{
		return this.configurazionePdDReader.getSingleExtendedInfoConfigurazioneFromCache(id, this.getConnection());
	}
}
