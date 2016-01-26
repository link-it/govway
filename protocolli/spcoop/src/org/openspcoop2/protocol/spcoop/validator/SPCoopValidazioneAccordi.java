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
package org.openspcoop2.protocol.spcoop.validator;

import org.openspcoop2.core.registry.AccordoCooperazione;
import org.openspcoop2.core.registry.AccordoServizioParteComune;
import org.openspcoop2.core.registry.AccordoServizioParteSpecifica;
import org.openspcoop2.protocol.basic.validator.ValidazioneAccordi;
import org.openspcoop2.protocol.sdk.IProtocolFactory;
import org.openspcoop2.protocol.sdk.validator.ValidazioneResult;
import org.openspcoop2.protocol.spcoop.sica.SICAtoOpenSPCoopContext;

/**
 * SPCoopValidazioneAccordi
 * 
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class SPCoopValidazioneAccordi extends ValidazioneAccordi  {

	public SPCoopValidazioneAccordi(IProtocolFactory factory) {
		super(factory);
	}
	
	@Override
	public ValidazioneResult valida(
			AccordoServizioParteComune accordoServizioParteComune) {
		
		ValidazioneResult result = new ValidazioneResult();
		result.setEsito(false);
		
		try{
		
			SICAtoOpenSPCoopContext context = SICAtoOpenSPCoopContext.getInstance();
			
			if(context.isSICAClient_nomeAccordo_32CaratteriMax()){
				if(accordoServizioParteComune.getNome().length()>it.gov.spcoop.sica.manifest.driver.Costanti.LUNGHEZZA_MAX_NOME_ACCORDO){
					result.setMessaggioErrore("Il nome dell'accordo di servizio parte comune deve essere formato da non piu' di "+it.gov.spcoop.sica.manifest.driver.Costanti.LUNGHEZZA_MAX_NOME_ACCORDO+" caratteri");
					return result;
				}
			}
		
		}catch(Exception e){
			result.setMessaggioErrore("Durante la validazione dell'accordo è stata generata una eccezione non prevista: "+e.getMessage());
			result.setException(e);
			return result;
		}
			
		result.setEsito(true);
		return result;
	}

	@Override
	public ValidazioneResult valida(
			AccordoServizioParteSpecifica accordoServizioParteSpecifica) {
		
		ValidazioneResult result = new ValidazioneResult();
		result.setEsito(false);
		
		try{
			
			SICAtoOpenSPCoopContext context = SICAtoOpenSPCoopContext.getInstance();
			
			if(context.isSICAClient_nomeAccordo_32CaratteriMax()){
				if(accordoServizioParteSpecifica.getNome().length()>it.gov.spcoop.sica.manifest.driver.Costanti.LUNGHEZZA_MAX_NOME_ACCORDO){
					result.setMessaggioErrore("Il nome dell'accordo di servizio parte specifica deve essere formato da non piu' di "+it.gov.spcoop.sica.manifest.driver.Costanti.LUNGHEZZA_MAX_NOME_ACCORDO+" caratteri");
					return result;
				}
			}
		
		}catch(Exception e){
			result.setMessaggioErrore("Durante la validazione dell'accordo è stata generata una eccezione non prevista: "+e.getMessage());
			result.setException(e);
			return result;
		}
		
		result.setEsito(true);
		return result;
	}

	@Override
	public ValidazioneResult valida(
			AccordoCooperazione accordoCooperazione) {
		
		ValidazioneResult result = new ValidazioneResult();
		result.setEsito(false);
		
		try{
			
			SICAtoOpenSPCoopContext context = SICAtoOpenSPCoopContext.getInstance();
			
			if(context.isSICAClient_nomeAccordo_32CaratteriMax()){
				if(accordoCooperazione.getNome().length()>it.gov.spcoop.sica.manifest.driver.Costanti.LUNGHEZZA_MAX_NOME_ACCORDO){
					result.setMessaggioErrore("Il nome dell'accordo di cooperazione deve essere formato da non piu' di "+it.gov.spcoop.sica.manifest.driver.Costanti.LUNGHEZZA_MAX_NOME_ACCORDO+" caratteri");
					return result;
				}
			}
		
		}catch(Exception e){
			result.setMessaggioErrore("Durante la validazione dell'accordo è stata generata una eccezione non prevista: "+e.getMessage());
			result.setException(e);
			return result;
		}
		
		result.setEsito(true);
		return result;
	}



	

}
