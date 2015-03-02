/*
 * OpenSPCoop v2 - Customizable SOAP Message Broker 
 * http://www.openspcoop2.org
 * 
 * Copyright (c) 2005-2015 Link.it srl (http://link.it). 
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



package org.openspcoop2.protocol.trasparente.testsuite.core;

import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.protocol.sdk.constants.Inoltro;
import org.openspcoop2.protocol.sdk.constants.ProfiloDiCollaborazione;

/**
 * Costanti utilizzate nelle units test.
 * 
 * @author Andi Rexha (rexha@openspcoop.org)
 * @author $Author: apoli $
 * @version $Rev: 10489 $, $Date: 2015-01-13 10:15:51 +0100 (Tue, 13 Jan 2015) $
 */

public class CostantiTestSuite {

	
	/** Protocollo */
	public static final String PROTOCOL_NAME = "trasparente";
	
	
	/** ENTITA Profili Protocollo */
	public static final String PROXY_PROFILO_COLLABORAZIONE_ONEWAY=ProfiloDiCollaborazione.ONEWAY.getEngineValue();
	public static final String PROXY_PROFILO_COLLABORAZIONE_SINCRONO=ProfiloDiCollaborazione.SINCRONO.getEngineValue();
	
	/** ENTITA Profili Trasmissione */
	public static final String PROXY_PROFILO_TRASMISSIONE_CON_DUPLICATI=Inoltro.CON_DUPLICATI.getEngineValue();
	public static final String PROXY_PROFILO_TRASMISSIONE_SENZA_DUPLICATI=Inoltro.SENZA_DUPLICATI.getEngineValue();
	
	
	
	/** Porte Delegate per il test dei profili di collaborazione: OneWay */
	public static final String PORTA_DELEGATA_PROFILO_ONEWAY_NON_AUTENTICATO="PROXYMinisteroFruitore/PROXYMinisteroErogatoreEsterno/PROXYOneway/notifica";
	
		
	/** ENTITA SPCOOP: Tipo Soggetto */
	public static final String PROXY_TIPO_SOGGETTO="PROXY";
	
	
	/** ENTITA SPCOOP: Nome Soggetto Fruitore */
	public static final String PROXY_NOME_SOGGETTO_FRUITORE="MinisteroFruitore";
	/** ENTITA SPCOOP: IdPorta Soggetto Fruitore */
	public static final String PROXY_IDPORTA_SOGGETTO_FRUITORE="MinisteroFruitorePdD";
	/** ENTITA SPCOOP: IDSoggetto */
	public static final IDSoggetto PROXY_SOGGETTO_FRUITORE = new IDSoggetto(CostantiTestSuite.PROXY_TIPO_SOGGETTO, 
			CostantiTestSuite.PROXY_NOME_SOGGETTO_FRUITORE, CostantiTestSuite.PROXY_IDPORTA_SOGGETTO_FRUITORE);
		
	/** ENTITA SPCOOP: Nome Soggetto Erogatore */
	public static final String PROXY_NOME_SOGGETTO_EROGATORE="MinisteroErogatore";
	/** ENTITA SPCOOP: IdPorta Soggetto Erogatore */
	public static final String PROXY_IDPORTA_SOGGETTO_EROGATORE="MinisteroErogatorePdD";
	/** ENTITA SPCOOP: IDSoggetto */
	public static final IDSoggetto PROXY_SOGGETTO_EROGATORE = new IDSoggetto(CostantiTestSuite.PROXY_TIPO_SOGGETTO, 
			CostantiTestSuite.PROXY_NOME_SOGGETTO_EROGATORE, CostantiTestSuite.PROXY_IDPORTA_SOGGETTO_EROGATORE);
	
	/** ENTITA SPCOOP: Nome Soggetto Erogatore Esterno */
	public static final String PROXY_NOME_SOGGETTO_EROGATORE_ESTERNO="MinisteroErogatoreEsterno";
	/** ENTITA SPCOOP: IdPorta Soggetto Erogatore Esterno */
	public static final String PROXY_IDPORTA_SOGGETTO_EROGATORE_ESTERNO="MinisteroErogatoreEsternoPdD";
	/** ENTITA SPCOOP: IDSoggetto */
	public static final IDSoggetto PROXY_SOGGETTO_EROGATORE_ESTERNO = new IDSoggetto(CostantiTestSuite.PROXY_TIPO_SOGGETTO, 
			CostantiTestSuite.PROXY_NOME_SOGGETTO_EROGATORE_ESTERNO, CostantiTestSuite.PROXY_IDPORTA_SOGGETTO_EROGATORE_ESTERNO);
		
	
	
	/** ENTITA SPCOOP: Versione Servizio */
	public static final Integer PROXY_VERSIONE_SERVIZIO_DEFAULT=1;
	
	
	/** ENTITA SPCOOP: Tipo Servizio */
	public static final String PROXY_TIPO_SERVIZIO="PROXY";
	
	/** ENTITA SPCOOP: Nome Servizio OneWay */
	public static final String PROXY_NOME_SERVIZIO_ONEWAY="Oneway";
	/** ENTITA SPCOOP: Nome Azione Affidabile del Servizio OneWay con notifica */
	public static final String PROXY_SERVIZIO_ONEWAY_AZIONE_NOTIFICA="notifica";
	

	/** ENTITA SPCOOP: Nome Servizio Sincrono */
	public static final String PROXY_NOME_SERVIZIO_SINCRONO="RichiestaStatoAvanzamento";
    /** ENTITA SPCOOP: Nome Azione Collaborazione del Servizio RichiestaStatoAvanzamento */
    public static final String PROXY_SERVIZIO_SINCRONO_AZIONE_COLLABORAZIONE="Collaborazione";
    /** ENTITA SPCOOP: Nome Azione WSSTimestamp del Servizio RichiestaStatoAvanzamento */
    public static final String PROXY_SERVIZIO_SINCRONO_AZIONE_WSS_TIMESTAMP="WSSTimestamp";
   
	
}
