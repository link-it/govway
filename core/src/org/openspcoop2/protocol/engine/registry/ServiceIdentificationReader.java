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

package org.openspcoop2.protocol.engine.registry;

import org.openspcoop2.core.config.PortaApplicativa;
import org.openspcoop2.core.config.PortaDelegata;
import org.openspcoop2.core.id.IDPortaApplicativa;
import org.openspcoop2.core.id.IDPortaDelegata;
import org.openspcoop2.core.id.IDServizio;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.protocol.sdk.IProtocolFactory;
import org.openspcoop2.protocol.sdk.constants.CodiceErroreIntegrazione;
import org.openspcoop2.protocol.sdk.constants.ErroreIntegrazione;
import org.openspcoop2.protocol.sdk.constants.ErroriIntegrazione;
import org.openspcoop2.protocol.sdk.registry.IRegistryReader;
import org.openspcoop2.protocol.sdk.registry.IServiceIdentificationReader;
import org.openspcoop2.protocol.sdk.registry.RegistryNotFound;
import org.openspcoop2.utils.transport.TransportRequestContext;
import org.slf4j.Logger;

/**
 *  ArchiveRegistryReader
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author: apoli $
 * @version $Rev: 12237 $, $Date: 2016-10-04 11:41:45 +0200 (Tue, 04 Oct 2016) $
 */
public class ServiceIdentificationReader implements IServiceIdentificationReader {

	private IRegistryReader registryReader;
	private IProtocolFactory<?> protocolFactory;
	private Logger log;
	public ServiceIdentificationReader(IRegistryReader registryReader,
			IProtocolFactory<?> protocolFactory, Logger log) {
		this.registryReader = registryReader;
		this.protocolFactory = protocolFactory;
		this.log = log;
	}
	
	private ErroreIntegrazione erroreIntegrazioneNotFound;
	
	public ErroreIntegrazione getErroreIntegrazioneNotFound() {
		return this.erroreIntegrazioneNotFound;
	}
	
	
	// PORTA DELEGATA
	
	private PortaDelegata pd;
	private IDPortaDelegata idPD;
	
	@Override
	public IDPortaDelegata findPortaDelegata(TransportRequestContext transportRequestContext, boolean portaUrlBased) throws RegistryNotFound{
			
		IdentificazionePortaDelegata idPD = null;
		try{
		
			idPD = 
					new IdentificazionePortaDelegata(transportRequestContext, this.log, portaUrlBased, this.registryReader, this.protocolFactory);
			if(idPD.process()==false){
				if(CodiceErroreIntegrazione.CODICE_401_PORTA_INESISTENTE.equals(idPD.getCodiceErrore())){
					throw new RegistryNotFound(idPD.getMessaggioErrore());
				}
				else {
					throw new Exception(idPD.getMessaggioErrore());
				}
			}
			this.pd = idPD.getPortaDelegata();
			this.idPD = idPD.getIDPortaDelegata();
			
			return idPD.getIDPortaDelegata();
			
		}
		catch(RegistryNotFound e){
			this.erroreIntegrazioneNotFound = idPD.getErroreIntegrazione();
			throw e;
		}
		catch(Exception e){
			this.log.error("findPortaDelegata error: "+e.getMessage(),e);
			return null;
		}

	}
	
	@Override
	public IDServizio convertToIDServizio(IDPortaDelegata idPortaDelegata) throws RegistryNotFound{
		
		try{
			if(this.idPD!=null && this.idPD.getIdentificativiFruizione()!=null &&
					this.idPD.getIdentificativiFruizione().getIdServizio()!=null){
				return this.idPD.getIdentificativiFruizione().getIdServizio();
			}
			
			if(this.pd==null){
				this.pd = this.registryReader.getPortaDelegata(idPortaDelegata);
			}
			IDServizio idS = new IDServizio(new IDSoggetto(this.pd.getSoggettoErogatore().getTipo(), this.pd.getSoggettoErogatore().getNome()), 
					this.pd.getServizio().getTipo(), this.pd.getServizio().getNome());
			
			if(idS.getSoggettoErogatore().getCodicePorta()==null){
				idS.getSoggettoErogatore().setCodicePorta(this.registryReader.getDominio(idS.getSoggettoErogatore()));
			}
			
			return idS;
		}
		catch(RegistryNotFound e){
			this.erroreIntegrazioneNotFound = 
					ErroriIntegrazione.ERRORE_401_PORTA_INESISTENTE.
					getErrore401_PortaInesistente(e.getMessage(),idPortaDelegata.getNome(),idPortaDelegata.getNome());
			throw e;
		}
		catch(Exception e){
			return null;
		}
		
	}
	
	
	// PORTA APPLICATIVA
	
	private PortaApplicativa pa;
	private IDPortaApplicativa idPA;
	
	@Override
	public IDPortaApplicativa findPortaApplicativa(TransportRequestContext transportRequestContext, boolean portaUrlBased) throws RegistryNotFound{
		
		IdentificazionePortaApplicativa idPA = null;
		try{
			
			idPA = 
					new IdentificazionePortaApplicativa(transportRequestContext, this.log, portaUrlBased, this.registryReader, this.protocolFactory);
			if(idPA.process()==false){
				if(CodiceErroreIntegrazione.CODICE_401_PORTA_INESISTENTE.equals(idPA.getCodiceErrore())){
					throw new RegistryNotFound(idPA.getMessaggioErrore());
				}
				else {
					throw new Exception(idPA.getMessaggioErrore());
				}
			}
			this.pa = idPA.getPortaApplicativa();
			this.idPA = idPA.getIDPortaApplicativa();
			
			return idPA.getIDPortaApplicativa();
			
		}
		catch(RegistryNotFound e){
			this.erroreIntegrazioneNotFound = idPA.getErroreIntegrazione();
			throw e;
		}
		catch(Exception e){
			this.log.error("findPortaDelegata error: "+e.getMessage(),e);
			return null;
		}
		
	}
	
	@Override
	public IDServizio convertToIDServizio(IDPortaApplicativa idPortaApplicativa) throws RegistryNotFound{
		
		try{
			if(this.idPA!=null && this.idPA.getIdentificativiErogazione()!=null &&
					this.idPA.getIdentificativiErogazione().getIdServizio()!=null){
				return this.idPA.getIdentificativiErogazione().getIdServizio();
			}
			
			if(this.pa==null){
				this.pa = this.registryReader.getPortaApplicativa(idPortaApplicativa);
			}
			IDServizio idS = new IDServizio(this.pa.getTipoSoggettoProprietario(),this.pa.getNomeSoggettoProprietario(), 
					this.pa.getServizio().getTipo(), this.pa.getServizio().getNome());
			
			if(idS.getSoggettoErogatore().getCodicePorta()==null){
				idS.getSoggettoErogatore().setCodicePorta(this.registryReader.getDominio(idS.getSoggettoErogatore()));
			}
			
			return idS;
		}
		catch(RegistryNotFound e){
			this.erroreIntegrazioneNotFound = 
					ErroriIntegrazione.ERRORE_401_PORTA_INESISTENTE.
					getErrore401_PortaInesistente(e.getMessage(),idPortaApplicativa.getNome(),idPortaApplicativa.getNome());
			throw e;
		}
		catch(Exception e){
			return null;
		}
		
	} 
}
