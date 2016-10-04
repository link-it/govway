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




package org.openspcoop2.pdd.core.connettori;

import java.util.Date;

import org.openspcoop2.message.OpenSPCoop2Message;
import org.openspcoop2.pdd.core.ICore;
import org.openspcoop2.utils.io.notifier.NotifierInputStreamParams;


/**
 * Interfaccia che definisce un connettore utilizzato per effettuare
 * consegne di messaggi e gestire eventuali risposte. 
 * Esistono attualmente due implementazioni OpenSPCoop di questa interfaccia :
 * <ul>
 * <li> {@link ConnettoreHTTP}, Effettua una consegna su protocollo di trasporto HTTP.
 * <li> {@link ConnettoreJMS},  Effettua una consegna su protocollo di trasporto JMS.
 * </ul>
 *
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */

public interface IConnettore extends ICore {


    /**
     * Si occupa di effettuare la consegna.
     *
     * @param request Messaggio da consegnare.
     * @return true in caso di consegna con successo, false altrimenti
     * 
     */
    public boolean send(ConnettoreMsg request);
    
    
    
    /**
     * In caso di avvenuto errore in fase di consegna, questo metodo ritorna il motivo dell'errore.
     *
     * @return motivo dell'errore (se avvenuto in fase di consegna).
     * 
     */
    public String getErrore();
    
    /**
     * In caso di avvenuta consegna, questo metodo ritorna il codice di trasporto della consegna.
     *
     * @return se avvenuta una consegna ritorna il codice di trasporto della consegna.
     * 
     */
    public int getCodiceTrasporto();

    /**
     * In caso di avvenuta consegna, questo metodo ritorna l'header del trasporto
     *
     * @return se avvenuta una consegna ritorna l'header del trasporto
     * 
     */
    public java.util.Properties getHeaderTrasporto();
    
    /**
     * Ritorna la risposta pervenuta in seguita alla consegna effettuata
     *
     * @return la risposta.
     * 
     */
    public OpenSPCoop2Message getResponse();
    
    
    /**
     * In caso di avvenuta consegna, questo metodo ritorna, se disponibile, la dimensione della risposta (-1 se non disponibile)
     *
     * @return se avvenuta una consegna,  questo metodo ritorna, se disponibile, la dimensione della risposta (-1 se non disponibile)
     * 
     */
    public long getContentLength();
    
    /**
     * Ritorna l'informazione su dove il connettore sta spedendo il messaggio
     * 
     * @return location di inoltro del messaggio
     */
    public String getLocation();
    
    /**
     * Ritorna l'eccezione in caso di errore di processamento
     * 
     * @return eccezione in caso di errore di processamento
     */
    public Exception getEccezioneProcessamento();
    
    /**
     * Effettua la disconnessione 
     */
    public void disconnect() throws ConnettoreException;
    
    /**
     * Data di creazione del connettore 
     */
    public Date getCreationDate() throws ConnettoreException;
    
    /**
     * Ritorna le opzioni di NotifierInputStreamParams per la risposta
     * 
     * @return NotifierInputStreamParams
     * @throws ConnettoreException
     */
    public NotifierInputStreamParams getNotifierInputStreamParamsResponse() throws ConnettoreException;

}





