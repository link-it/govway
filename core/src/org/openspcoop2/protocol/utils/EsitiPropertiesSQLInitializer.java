/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2024 Link.it srl (https://link.it). 
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
package org.openspcoop2.protocol.utils;

import java.util.List;

import org.openspcoop2.core.constants.CostantiDB;
import org.openspcoop2.utils.LoggerWrapperFactory;
import org.openspcoop2.utils.resources.FileSystemUtilities;
import org.openspcoop2.utils.resources.Loader;
import org.slf4j.Logger;

/**
 * EsitiPropertiesSQLInitializer
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class EsitiPropertiesSQLInitializer {

	private static Logger log = LoggerWrapperFactory.getLogger(EsitiPropertiesSQLInitializer.class);
	
	public static void main(String[] args) throws Exception {
		
		String file = "/var/tmp/ArchivioTransazioni_data.sql";
		if(args.length>0) {
			file = args[0];
		}
		
		EsitiProperties.initialize(null, log, new Loader(), null);
		
		EsitiProperties esitiProperties = EsitiProperties.getInstance(log, EsitiProperties.NO_PROTOCOL_CONFIG);
		
		StringBuilder sbInit = new StringBuilder();
		
		String completateConSuccesso = "Completata con Successo";
		String faultApplicativo = "Fault Applicativo";
		String richiestaScartata = "Richiesta Scartata";
		String erroriConsegna = "Errore di Consegna";
		String autorizzazioneNegata = "Autorizzazione Negata";
		String policyViolata = "Policy Controllo Traffico Violate";
		String erroriIM = "Errori Servizio IntegrationManager/MessageBox";
		String erroriRichiesta = "Errori Processamento Richiesta";
		String erroriRisposta = "Errori Processamento Risposta";
		String erroreGenerico = "Errore Generico";
		String erroreClientNonDisponibile = "Errore Client Indisponibile";
		
		int completateConSuccesso_code = 1;
		int faultApplicativo_code = 2;
		int richiestaScartata_code = 3;
		int erroriConsegna_code = 4;
		int autorizzazioneNegata_code = 5;
		int policyViolata_code = 6;
		int erroriIM_code = 7;
		int erroriRichiesta_code = 8;
		int erroriRisposta_code = 9;
		int erroreGenerico_code = 10;
		int erroreClientNonDisponibile_code = 11;
		
		sbInit.append("-- classe esiti\n");
		sbInit.append(buildClassInsert(completateConSuccesso_code, completateConSuccesso));
		sbInit.append("\n");
		sbInit.append(buildClassInsert(faultApplicativo_code, faultApplicativo));
		sbInit.append("\n");
		sbInit.append(buildClassInsert(richiestaScartata_code, richiestaScartata));
		sbInit.append("\n");
		sbInit.append(buildClassInsert(erroriConsegna_code, erroriConsegna));
		sbInit.append("\n");
		sbInit.append(buildClassInsert(autorizzazioneNegata_code, autorizzazioneNegata));
		sbInit.append("\n");
		sbInit.append(buildClassInsert(policyViolata_code, policyViolata));
		sbInit.append("\n");
		sbInit.append(buildClassInsert(erroriIM_code, erroriIM));
		sbInit.append("\n");
		sbInit.append(buildClassInsert(erroriRichiesta_code, erroriRichiesta));
		sbInit.append("\n");
		sbInit.append(buildClassInsert(erroriRisposta_code, erroriRisposta));
		sbInit.append("\n");
		sbInit.append(buildClassInsert(erroreGenerico_code, erroreGenerico));
		sbInit.append("\n");
		sbInit.append(buildClassInsert(erroreClientNonDisponibile_code, erroreClientNonDisponibile));
		sbInit.append("\n");

		
		sbInit.append("\n");
		sbInit.append("-- esiti\n");
		List<Integer> codes = esitiProperties.getEsitiCode();
		for (Integer code : codes) {
			
			int classCode = -1;
			if(esitiProperties.getEsitiCodeOk_senzaFaultApplicativo().contains(code)) {
				classCode = completateConSuccesso_code; // Completata con Successo
			}
			else if(esitiProperties.getEsitiCodeFaultApplicativo().contains(code)) {
				classCode = faultApplicativo_code; // Fault Applicativo
			}
			else if(esitiProperties.getEsitiCodeRichiestaScartate().contains(code)) {
				classCode = richiestaScartata_code; // Richiesta Scartata
			}
			else if(esitiProperties.getEsitiCodeErroriConsegna().contains(code)) {
				classCode = erroriConsegna_code; // Errore di Consegna
			}
			else if(esitiProperties.getEsitiCodeAutorizzazioneNegata().contains(code)) {
				classCode = autorizzazioneNegata_code; // Autorizzazione Negata
			}
			else if(esitiProperties.getEsitiCodeControlloTrafficoPolicyViolate().contains(code)) {
				classCode = policyViolata_code; // Policy Controllo Traffico Violate
			}
			else if(esitiProperties.getEsitiCodeServizioIntegrationManager().contains(code)) {
				classCode = erroriIM_code; // Errori Servizio I.M. MessageBox
			}
			else if(esitiProperties.getEsitiCodeErroriProcessamentoRichiesta().contains(code)) {
				classCode = erroriRichiesta_code; // Errori Processamento Richiesta
			}
			else if(esitiProperties.getEsitiCodeErroriProcessamentoRisposta().contains(code)) {
				classCode = erroriRisposta_code; // Errori Processamento Risposta
			}
			else if(esitiProperties.getEsitiCodeErroriClientNonDisponibile().contains(code)) {
				classCode = erroreClientNonDisponibile_code; // Errore Client Indisponibile
			}
			else if(esitiProperties.getEsitiCodeErroriGenerici().contains(code)) {
				classCode = erroreGenerico_code; // Errore Generico
			}
			else {
				throw new Exception("Classificazione del codice '"+code+"' non riuscita");
			}
						
			StringBuilder sb = new StringBuilder("INSERT INTO ");
			sb.append(CostantiDB.TRANSAZIONI_ESITI);
			sb.append(" (govway_status, govway_status_key, govway_status_detail, govway_status_description, govway_status_class) VALUES ( ");
			sb.append(code);
			sb.append(" , ");
			sb.append("'").append(esitiProperties.getEsitoName(code).replaceAll("'", "''")).append("'");
			sb.append(" , ");
			sb.append("'").append(esitiProperties.getEsitoLabel(code).replaceAll("'", "''")).append("'");
			sb.append(" , ");
			sb.append("'").append(esitiProperties.getEsitoDescription(code).replaceAll("'", "''")).append("'");
			sb.append(" , ");
			sb.append("(select id from ").append(CostantiDB.TRANSAZIONI_CLASSE_ESITI).append(" WHERE govway_status="+classCode+")");
			sb.append(" );");
			
			sbInit.append(sb.toString());
			sbInit.append("\n");
		}
		

		FileSystemUtilities.writeFile(file, sbInit.toString().getBytes());
		System.out.println("Script di inizializzazione serializzato in '"+file+"'");

		
	}
	
	private static String buildClassInsert(int code, String detail) {
		StringBuilder sb = new StringBuilder("INSERT INTO ");
		sb.append(CostantiDB.TRANSAZIONI_CLASSE_ESITI);
		sb.append(" (govway_status, govway_status_detail) VALUES ( ");
		sb.append(code);
		sb.append(" , ");
		sb.append("'").append(detail).append("'");
		sb.append(" );");
		return sb.toString();
	}

}
