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


package org.openspcoop2.pdd.core.handlers.statistics;

import java.util.Date;

import org.openspcoop2.core.constants.Costanti;
import org.openspcoop2.pdd.core.handlers.HandlerException;
import org.openspcoop2.pdd.core.handlers.PostOutResponseContext;
import org.openspcoop2.pdd.core.handlers.PostOutResponseHandler;
import org.openspcoop2.utils.date.DateManager;


/**
 * StatisticsPostOutResponseHandler
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class StatisticsPostOutResponseHandler implements PostOutResponseHandler {

	@Override
	public void invoke(PostOutResponseContext context) throws HandlerException{
		
		
		// Raccolgo date
		Object dataIngressoRichiesta = context.getPddContext().getObject(StatisticsConstants.DATA_INGRESSO_RICHIESTA);
		long timeMillisIngressoRichiesta = -1;
		if(dataIngressoRichiesta!=null)
			timeMillisIngressoRichiesta = ((Date) dataIngressoRichiesta).getTime();
		
		Object dataUscitaRichiesta = context.getPddContext().getObject(StatisticsConstants.DATA_USCITA_RICHIESTA);
		long timeMillisUscitaRichiesta = -1;
		if(dataUscitaRichiesta!=null){
			timeMillisUscitaRichiesta = ((Date) dataUscitaRichiesta).getTime();
		}
		else{
			// Provo a vedere se c'e' stata una data di presa in carico
			Object dataPresaInCarico = context.getPddContext().getObject(Costanti.DATA_PRESA_IN_CARICO);
			if(dataPresaInCarico!=null){
				try{
					dataUscitaRichiesta = Costanti.newSimpleDateFormat().parse((String)dataPresaInCarico);
					timeMillisUscitaRichiesta = ((Date) dataUscitaRichiesta).getTime();
				}catch(Exception e){
					throw new HandlerException(e.getMessage(),e);
				}
			}
		}
		
		Object dataIngressoRisposta = context.getPddContext().getObject(StatisticsConstants.DATA_INGRESSO_RISPOSTA);
		long timeMillisIngressoRisposta = -1;
		if(dataIngressoRisposta!=null)
			timeMillisIngressoRisposta = ((Date) dataIngressoRisposta).getTime();
		else{
			// Provo a vedere se c'e' stata una data di presa in carico
			Object dataPresaInCarico = context.getPddContext().getObject(Costanti.DATA_PRESA_IN_CARICO);
			if(dataPresaInCarico!=null){
				try{
					dataIngressoRisposta = Costanti.newSimpleDateFormat().parse((String)dataPresaInCarico);
					timeMillisIngressoRisposta = ((Date) dataIngressoRisposta).getTime();
				}catch(Exception e){
					throw new HandlerException(e.getMessage(),e);
				}
			}
		}
		
		long timeMillisUscitaRisposta = DateManager.getTimeMillis();
		
		// Raccolgo dimensioni
		long dimensioneIngressoRichiesta = -1;
		if(context.getInputRequestMessageSize()!=null && context.getInputRequestMessageSize()>0){
			dimensioneIngressoRichiesta = context.getInputRequestMessageSize();
		}
		long dimensioneUscitaRichiesta = -1;
		if(context.getOutputRequestMessageSize()!=null && context.getOutputRequestMessageSize()>0){
			dimensioneUscitaRichiesta = context.getOutputRequestMessageSize();
		}
		long dimensioneIngressoRisposta = -1;
		if(context.getInputResponseMessageSize()!=null && context.getInputResponseMessageSize()>0){
			dimensioneIngressoRisposta = context.getInputResponseMessageSize();
		}
		long dimensioneUscitaRisposta = -1;
		if(context.getOutputResponseMessageSize()!=null && context.getOutputResponseMessageSize()>0){
			dimensioneUscitaRisposta = context.getOutputResponseMessageSize();
		}
		
		Statistic stat =  new Statistic();
		stat.setEsito(context.getEsito());
		stat.setTipoPdD(context.getTipoPorta());
		stat.setTimeMillisIngressoRichiesta(timeMillisIngressoRichiesta);
		stat.setTimeMillisIngressoRisposta(timeMillisIngressoRisposta);
		stat.setTimeMillisUscitaRichiesta(timeMillisUscitaRichiesta);
		stat.setTimeMillisUscitaRisposta(timeMillisUscitaRisposta);
		stat.setDimensioneIngressoRichiesta(dimensioneIngressoRichiesta);
		stat.setDimensioneIngressoRisposta(dimensioneIngressoRisposta);
		stat.setDimensioneUscitaRichiesta(dimensioneUscitaRichiesta);
		stat.setDimensioneUscitaRisposta(dimensioneUscitaRisposta);
		
		StatisticsCollection.update(stat);
	}
	
}
