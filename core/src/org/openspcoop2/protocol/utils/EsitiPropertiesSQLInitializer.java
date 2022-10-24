/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2022 Link.it srl (https://link.it). 
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
		
		sbInit.append("-- classe esiti\n");
		sbInit.append(buildClassInsert(1, 1, "Completata con Successo"));
		sbInit.append("\n");
		sbInit.append(buildClassInsert(2, 2, "Fault Applicativo"));
		sbInit.append("\n");
		sbInit.append(buildClassInsert(3, 3, "Richiesta Scartata"));
		sbInit.append("\n");
		sbInit.append(buildClassInsert(4, 4, "Errore di Consegna"));
		sbInit.append("\n");
		sbInit.append(buildClassInsert(5, 5, "Autorizzazione Negata"));
		sbInit.append("\n");
		sbInit.append(buildClassInsert(6, 6, "Policy Controllo Traffico Violate"));
		sbInit.append("\n");
		sbInit.append(buildClassInsert(7, 7, "Errori Servizio IntegrationManager/MessageBox"));
		sbInit.append("\n");
		sbInit.append(buildClassInsert(8, 8, "Errori Processamento Richiesta"));
		sbInit.append("\n");
		sbInit.append(buildClassInsert(9, 9, "Errori Processamento Risposta"));
		sbInit.append("\n");
		sbInit.append(buildClassInsert(10, 10, "Errore Generico"));
		sbInit.append("\n");

		
		sbInit.append("\n");
		sbInit.append("-- esiti\n");
		List<Integer> codes = esitiProperties.getEsitiCode();
		for (Integer code : codes) {
			
			int classCode = -1;
			if(esitiProperties.getEsitiCodeOk_senzaFaultApplicativo().contains(code)) {
				classCode = 1; // Completata con Successo
			}
			else if(esitiProperties.getEsitiCodeFaultApplicativo().contains(code)) {
				classCode = 2; // Fault Applicativo
			}
			else if(esitiProperties.getEsitiCodeRichiestaScartate().contains(code)) {
				classCode = 3; // Richiesta Scartata
			}
			else if(esitiProperties.getEsitiCodeErroriConsegna().contains(code)) {
				classCode = 4; // Errore di Consegna
			}
			else if(esitiProperties.getEsitiCodeAutorizzazioneNegata().contains(code)) {
				classCode = 5; // Autorizzazione Negata
			}
			else if(esitiProperties.getEsitiCodeControlloTrafficoPolicyViolate().contains(code)) {
				classCode = 6; // Policy Controllo Traffico Violate
			}
			else if(esitiProperties.getEsitiCodeServizioIntegrationManager().contains(code)) {
				classCode = 7; // Errori Servizio I.M. MessageBox
			}
			else if(esitiProperties.getEsitiCodeErroriProcessamentoRichiesta().contains(code)) {
				classCode = 8; // Errori Processamento Richiesta
			}
			else if(esitiProperties.getEsitiCodeErroriProcessamentoRisposta().contains(code)) {
				classCode = 9; // Errori Processamento Risposta
			}
			else if(esitiProperties.getEsitiCodeErroriGenerici().contains(code)) {
				classCode = 10; // Errore Generico
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
			sb.append(classCode);
			sb.append(" );");
			
			sbInit.append(sb.toString());
			sbInit.append("\n");
		}
		

		FileSystemUtilities.writeFile(file, sbInit.toString().getBytes());
		System.out.println("Script di inizializzazione serializzato in '"+file+"'");

		
	}
	
	private static String buildClassInsert(int id, int code, String detail) {
		StringBuilder sb = new StringBuilder("INSERT INTO ");
		sb.append(CostantiDB.TRANSAZIONI_CLASSE_ESITI);
		sb.append(" (id, govway_status, govway_status_detail) VALUES ( ");
		sb.append(id);
		sb.append(" , ");
		sb.append(code);
		sb.append(" , ");
		sb.append("'").append(detail).append("'");
		sb.append(" );");
		return sb.toString();
	}

}
