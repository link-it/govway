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
package org.openspcoop2.protocol.sdi.validator;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;
import java.util.Properties;

import org.openspcoop2.message.OpenSPCoop2Message;
import org.openspcoop2.pdd.config.OpenSPCoop2Properties;
import org.openspcoop2.protocol.basic.ProtocolliRegistrati;
import org.openspcoop2.protocol.basic.tracciamento.TracciaDriver;
import org.openspcoop2.protocol.engine.ProtocolFactoryManager;
import org.openspcoop2.protocol.sdi.config.SDIProperties;
import org.openspcoop2.protocol.sdi.constants.SDICostanti;
import org.openspcoop2.protocol.sdk.Busta;
import org.openspcoop2.protocol.sdk.Eccezione;
import org.openspcoop2.protocol.sdk.IProtocolFactory;
import org.openspcoop2.protocol.sdk.ProtocolException;
import org.openspcoop2.protocol.sdk.constants.CodiceErroreCooperazione;
import org.openspcoop2.protocol.sdk.constants.LivelloRilevanza;
import org.openspcoop2.protocol.sdk.constants.RuoloMessaggio;
import org.openspcoop2.protocol.sdk.state.IState;
import org.openspcoop2.protocol.sdk.state.StateMessage;
import org.openspcoop2.protocol.sdk.tracciamento.DriverTracciamentoNotFoundException;
import org.openspcoop2.protocol.sdk.tracciamento.FiltroRicercaTracceConPaginazione;
import org.openspcoop2.protocol.sdk.tracciamento.ITracciaDriver;
import org.openspcoop2.protocol.sdk.tracciamento.InformazioniProtocollo;
import org.openspcoop2.protocol.sdk.tracciamento.Traccia;

/**
 * SDIValidazioneUtils
 * 
 * @author Andrea Poli (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class SDIValidazioneUtils {

	private IProtocolFactory<?> protocolFactory;
	private SDIProperties sdiProperties;
	private ITracciaDriver _tracciaDriver;
	
	public SDIValidazioneUtils(IProtocolFactory<?> protocolFactory) throws ProtocolException{
		this.protocolFactory = protocolFactory;
		this.sdiProperties = SDIProperties.getInstance(protocolFactory.getLogger());
	}
	private ITracciaDriver getDriverTracciamento() throws ProtocolException {
		if(this._tracciaDriver==null) {
			this._initDriverTracciamento();
		}
		return this._tracciaDriver;
	}
	private synchronized void _initDriverTracciamento() throws ProtocolException {
		if(this._tracciaDriver==null) {
			this._tracciaDriver = newDriverTracciamento(null);
		}
	}
	private ITracciaDriver getDriverTracciamento(Connection con) throws ProtocolException {
		if(con!=null) {
			return newDriverTracciamento(con);
		}
		else {
			return getDriverTracciamento();
		}
	}
	private TracciaDriver newDriverTracciamento(Connection con) throws ProtocolException {
		if(this.sdiProperties.isTracciamentoRequiredFromConfiguration()) {
			try {
				ProtocolliRegistrati pRegistrati = new ProtocolliRegistrati(ProtocolFactoryManager.getInstance().getProtocolFactories());
				
				String tipoDatabase = this.sdiProperties.getTracciamentoTipoDatabase();
				if(tipoDatabase==null) {
					tipoDatabase = OpenSPCoop2Properties.getInstance().getDatabaseType();
				}
			
				ITracciaDriver tracciaDriver = this.protocolFactory.createTracciaDriver();
				if(tracciaDriver instanceof TracciaDriver) {
				
					TracciaDriver tracciaDriverBasic = (TracciaDriver) tracciaDriver;
					
					if(con==null) {
						String datasource = this.sdiProperties.getTracciamentoDatasource();
						Properties datasourceJndiContext = this.sdiProperties.getTracciamentoDatasource_jndiContext();
						tracciaDriverBasic.init(pRegistrati, datasource, tipoDatabase, datasourceJndiContext, this.protocolFactory.getLogger());
					}
					else {
						tracciaDriverBasic.init(pRegistrati, con, tipoDatabase, this.protocolFactory.getLogger());
					}
					
					return tracciaDriverBasic;
					
				}
				else {
					throw new Exception("Unexpected traccciamento driver '"+this._tracciaDriver.getClass().getName()+"'");
				}
				
			}catch(Exception e) {
				throw new ProtocolException(e.getMessage(),e);
			}
		}
		return null;
	}
	
	
	public Eccezione newEccezioneValidazione(CodiceErroreCooperazione codiceErrore) throws ProtocolException{
		return Eccezione.getEccezioneValidazione(codiceErrore,null, this.protocolFactory);
	}
	public Eccezione newEccezioneValidazione(CodiceErroreCooperazione codiceErrore,String descrizioneErrore) throws ProtocolException{
		return Eccezione.getEccezioneValidazione(codiceErrore, descrizioneErrore, this.protocolFactory);
	}
	public Eccezione newEccezioneValidazione(CodiceErroreCooperazione codiceErrore,String descrizioneErrore, Throwable e) throws ProtocolException{
		if(this.protocolFactory!=null && this.protocolFactory.getLogger()!=null)
			this.protocolFactory.getLogger().error(descrizioneErrore,e);
		return Eccezione.getEccezioneValidazione(codiceErrore, descrizioneErrore, this.protocolFactory);
	}
	
	public Eccezione newEccezioneValidazione(CodiceErroreCooperazione codiceErrore,boolean info) throws ProtocolException{
		Eccezione ecc = Eccezione.getEccezioneValidazione(codiceErrore,null, this.protocolFactory);
		if(info){
			ecc.setRilevanza(LivelloRilevanza.INFO);
		}
		return ecc;
	}
	public Eccezione newEccezioneValidazione(CodiceErroreCooperazione codiceErrore,String descrizioneErrore,boolean info) throws ProtocolException{
		Eccezione ecc =  Eccezione.getEccezioneValidazione(codiceErrore, descrizioneErrore, this.protocolFactory);
		if(info){
			ecc.setRilevanza(LivelloRilevanza.INFO);
		}
		return ecc;
	}
	public Eccezione newEccezioneValidazione(CodiceErroreCooperazione codiceErrore,String descrizioneErrore, Throwable e,boolean info) throws ProtocolException{
		if(this.protocolFactory!=null && this.protocolFactory.getLogger()!=null)
			this.protocolFactory.getLogger().error(descrizioneErrore,e);
		Eccezione ecc =  Eccezione.getEccezioneValidazione(codiceErrore, descrizioneErrore, this.protocolFactory);
		if(info){
			ecc.setRilevanza(LivelloRilevanza.INFO);
		}
		return ecc;
	}
	
	public Eccezione newEccezioneProcessamento(CodiceErroreCooperazione codiceErrore) throws ProtocolException{
		return Eccezione.getEccezioneProcessamento(codiceErrore,null, this.protocolFactory);
	}
	public Eccezione newEccezioneProcessamento(CodiceErroreCooperazione codiceErrore,String descrizioneErrore) throws ProtocolException{
		return Eccezione.getEccezioneProcessamento(codiceErrore, descrizioneErrore, this.protocolFactory);
	}
	public Eccezione newEccezioneProcessamento(CodiceErroreCooperazione codiceErrore,String descrizioneErrore, Throwable e) throws ProtocolException{
		if(this.protocolFactory!=null && this.protocolFactory.getLogger()!=null)
			this.protocolFactory.getLogger().error(descrizioneErrore,e);
		return Eccezione.getEccezioneProcessamento(codiceErrore, descrizioneErrore, this.protocolFactory);
	}
	
	
	
	public void addHeaderIdentificativoSdiMessaggio(OpenSPCoop2Message msg, String idSdi, String idSdiRiferimentoArchivio) {
		
		if(msg==null) {
			return;
		}
		if(msg.getTransportRequestContext()==null || msg.getTransportRequestContext().getParametersTrasporto()==null) {
			return;
		}
		
		if(idSdi==null) {
			return;
		}
		if(idSdiRiferimentoArchivio!=null) {
			msg.getTransportRequestContext().getParametersTrasporto().put(SDICostanti.SDI_HEADER_ID_CORRELAZIONE,idSdi + " "+idSdiRiferimentoArchivio);
		}
		else {
			msg.getTransportRequestContext().getParametersTrasporto().put(SDICostanti.SDI_HEADER_ID_CORRELAZIONE,idSdi);
		}

	}
	
	public void readInformazioniFatturaRiferita(Busta busta, String identificativoSdI,
			String servizio, String azione,
			boolean applicativoMittente, boolean fatturazioneAttiva,
			IState state) throws ProtocolException {
		
		Connection con = null;
		if(state!=null) {
			if(state instanceof StateMessage) {
				StateMessage s = (StateMessage) state;
				try {
					if(s.getConnectionDB()!=null && !s.getConnectionDB().isClosed()) {
						con = s.getConnectionDB();
					}
				}catch(Exception e) {} // ignore
			}
		}
		
		ITracciaDriver tracciaDriver = this.getDriverTracciamento(con);
		if(tracciaDriver==null) {
			throw new ProtocolException("Accesso al database delle tracce non attivo");
		}
		
		Traccia traccia = null;
		if(fatturazioneAttiva) {
		
			// L'identificativo SDI si trova nella risposta per la fatturazione attiva. Tutte le altre informazioni nella richiesta.
			Traccia tracciaRisposta = null;
			FiltroRicercaTracceConPaginazione filtro = new FiltroRicercaTracceConPaginazione();
			filtro.setTipoTraccia(RuoloMessaggio.RISPOSTA);
			filtro.setInformazioniProtocollo(new InformazioniProtocollo());
			filtro.getInformazioniProtocollo().setServizio(servizio);
			filtro.getInformazioniProtocollo().setAzione(azione);
			filtro.getInformazioniProtocollo().addProprietaProtocollo(SDICostanti.SDI_BUSTA_EXT_IDENTIFICATIVO_SDI, identificativoSdI);
			filtro.setAsc(false);
			filtro.setLimit(1);
			List<Traccia> list = null;
			try {
				list = tracciaDriver.getTracce(filtro);
			}catch(DriverTracciamentoNotFoundException notFound) {}
			catch(Exception e) {
				throw new ProtocolException(e.getMessage(),e);
			}
			if(list!=null && !list.isEmpty()) {
				tracciaRisposta = list.get(0);
			}
			
			if(tracciaRisposta!=null && tracciaRisposta.getIdTransazione()!=null) {
				try {
					traccia = tracciaDriver.getTraccia(tracciaRisposta.getIdTransazione(), RuoloMessaggio.RICHIESTA);
				}catch(DriverTracciamentoNotFoundException notFound) {}
				catch(Exception e) {
					throw new ProtocolException(e.getMessage(),e);
				}
				
				// e' solo l'identificativo SDI e c'è gia.
//				if(traccia!=null) {
//					// riporto all'interno dell'oggetto le informazioni mancanti presenti sulla risposta
//					if(tracciaRisposta.getBusta()!=null && tracciaRisposta.getBusta().sizeProperties()>0) {
//						
//						String identificativoSDI = tracciaRisposta.getBusta().getProperty(SDICostanti.SDI_BUSTA_EXT_IDENTIFICATIVO_SDI);
//						if(identificativoSDI!=null && !"".equals(identificativoSDI)) {
//							traccia.getProperties().put(SDICostanti.SDI_BUSTA_EXT_IDENTIFICATIVO_SDI, identificativoSDI);
//						}
//						
//					}
//				}
			}
			
			
		}
		else {
			FiltroRicercaTracceConPaginazione filtro = new FiltroRicercaTracceConPaginazione();
			filtro.setTipoTraccia(RuoloMessaggio.RICHIESTA);
			filtro.setInformazioniProtocollo(new InformazioniProtocollo());
			filtro.getInformazioniProtocollo().setServizio(servizio);
			filtro.getInformazioniProtocollo().setAzione(azione);
			filtro.getInformazioniProtocollo().addProprietaProtocollo(SDICostanti.SDI_BUSTA_EXT_IDENTIFICATIVO_SDI, identificativoSdI);
			filtro.setAsc(false);
			filtro.setLimit(1);
			List<Traccia> list = null;
			try {
				list = tracciaDriver.getTracce(filtro);
			}catch(DriverTracciamentoNotFoundException notFound) {}
			catch(Exception e) {
				throw new ProtocolException(e.getMessage(),e);
			}
			if(list!=null && !list.isEmpty()) {
				traccia = list.get(0); // solo nella fatturazione passiva ne potra' esistere piu' di una in seguito a piu' tentativi di consegna. Le informazioni (raccolte sottostante, escluse gli id messaggi) saranno comunque le stesse. 
			}
		}
		if(traccia!=null) {

			if(applicativoMittente) {
				if(traccia.getBusta()!=null && traccia.getBusta().getServizioApplicativoFruitore()!=null &&
						!"".equals(traccia.getBusta().getServizioApplicativoFruitore())) {
					if(busta.getServizioApplicativoFruitore()==null) {
						busta.addProperty(SDICostanti.SDI_BUSTA_APPLICATIVO_MITTENTE_FATTURA,traccia.getBusta().getServizioApplicativoFruitore());
					}
				}
			}
			
			if(traccia.getBusta()!=null && traccia.getBusta().sizeProperties()>0) {
				
				String [] names = traccia.getBusta().getPropertiesNames();
				
				if(names!=null && names.length>0) {
					List<String> nomiDaRiportare = new ArrayList<>();
					nomiDaRiportare.add(SDICostanti.SDI_BUSTA_EXT_CODICE_DESTINATARIO);
					nomiDaRiportare.add(SDICostanti.SDI_BUSTA_EXT_NOME_FILE_METADATI);
					nomiDaRiportare.add(SDICostanti.SDI_BUSTA_EXT_SOGGETTO_EMITTENTE);
					
					Hashtable<String, String> nomiDaRiportareDiversamente = new Hashtable<>();
					nomiDaRiportareDiversamente.put(SDICostanti.SDI_BUSTA_EXT_NOME_FILE, SDICostanti.SDI_BUSTA_EXT_NOME_FILE_FATTURA);
					
					List<String> prefissiDaRiportare = new ArrayList<>();
					prefissiDaRiportare.add(SDICostanti.SDI_BUSTA_EXT_RIFERIMENTO_FATTURA_PREFIX_);
					prefissiDaRiportare.add(SDICostanti.SDI_BUSTA_EXT_TRASMITTENTE_PREFIX_);
					prefissiDaRiportare.add(SDICostanti.SDI_BUSTA_EXT_CEDENTE_PRESTATORE_PREFIX_);
					prefissiDaRiportare.add(SDICostanti.SDI_BUSTA_EXT_CESSIONARIO_COMMITTENTE_PREFIX_);
					prefissiDaRiportare.add(SDICostanti.SDI_BUSTA_EXT_TERZO_INTERMEDIARIO_O_SOGGETTO_EMITTENTE_PREFIX_);
					
					for (int i = 0; i < names.length; i++) {
						
						String checkNome = names[i];
						if(checkNome==null || "".equals(checkNome)) {
							continue;
						}
						String checkValue = traccia.getBusta().getProperty(checkNome);
						if(checkValue==null || "".equals(checkValue)) {
							continue;
						}
						
						if(!nomiDaRiportare.isEmpty()) {
							for (String nome : nomiDaRiportare) {
								if(checkNome.equals(nome)) {
									if(!busta.existsProperty(checkNome)) {
										busta.addProperty(checkNome, checkValue);
									}
								}
							}
						}
						
						if(!nomiDaRiportareDiversamente.isEmpty()) {
							Enumeration<String> keys = nomiDaRiportareDiversamente.keys();
							while (keys.hasMoreElements()) {
								String nome = (String) keys.nextElement();
								if(checkNome.equals(nome)) {
									if(!busta.existsProperty(checkNome)) {
										busta.addProperty(nomiDaRiportareDiversamente.get(checkNome), checkValue);
									}
								}
							}
						}
						
						if(!prefissiDaRiportare.isEmpty()) {
							for (String prefix : prefissiDaRiportare) {
								if(checkNome.startsWith(prefix)) {
									if(!busta.existsProperty(checkNome)) {
										busta.addProperty(checkNome, checkValue);
									}
								}
							}
						}
						
					}
				}
				
			}
		}

	}
}
