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

package org.openspcoop2.pdd.core.jmx;

/**
 * InformazioniStatoPorta
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class InformazioniStatoPorta {

	public String formatStatoPorta(String versionePdD, 
			String versioneBaseDati,
			String confDir, String versioneJava, String messageFactory,
			String livelloSeveritaDiagnostici,String livelloSeveritaDiagnosticiLog4j,
			boolean log4j_diagnostica,  boolean log4j_openspcoop, boolean log4j_integrationManager, 
			boolean tracciamento, boolean dumpApplicativo, boolean dumpPD, boolean dumpPA,
			boolean log4j_tracciamento, boolean log4j_dump,
			String infoDatabase, String infoProtocolli,
			InformazioniStatoPortaCache ... cache){
		return formatStatoPorta(versionePdD, versioneBaseDati, confDir, versioneJava, messageFactory,
				livelloSeveritaDiagnostici, livelloSeveritaDiagnosticiLog4j,
				log4j_diagnostica, log4j_openspcoop, log4j_integrationManager,
				tracciamento, dumpApplicativo, dumpPD, dumpPA,
				log4j_tracciamento, log4j_dump,
				infoDatabase, infoProtocolli,
				null,null,null,null,
				cache);
	}
	
	public String formatStatoPorta(String versionePdD, 
			String versioneBaseDati,
			String confDir, String versioneJava, String messageFactory,
			String livelloSeveritaDiagnostici,String livelloSeveritaDiagnosticiLog4j,
			boolean log4j_diagnostica,  boolean log4j_openspcoop, boolean log4j_integrationManager, 
			boolean tracciamento, boolean dumpApplicativo, boolean dumpPD, boolean dumpPA, 
			boolean log4j_tracciamento, boolean log4j_dump,
			String infoDatabase, String infoProtocolli,
			String statoConnessioniDB, String statoConnessioniPD, String statoConnessioniPA, String statoConnessioniJMS,
			InformazioniStatoPortaCache ... cache){
		
		StringBuffer bf = new StringBuffer();
		
		// informazioni generali
		
		bf.append("\n");
		bf.append("===========================\n");
		bf.append("Informazioni Generali\n");
		bf.append("===========================\n");
		bf.append("\n");
		format(bf, versionePdD, "Versione PdD");
		bf.append("\n");
		format(bf, versioneBaseDati, "Versione BaseDati");
		bf.append("\n");
		format(bf, confDir, "Directory Configurazione");
		bf.append("\n");
		format(bf, versioneJava, "Versione Java");
		bf.append("\n");
		format(bf, messageFactory, "Message Factory");
		bf.append("\n");
		
		bf.append("===========================\n");
		bf.append("Informazioni Diagnostica\n");
		bf.append("===========================\n");
		bf.append("\n");
		format(bf, livelloSeveritaDiagnostici, "Severità");
		format(bf, livelloSeveritaDiagnosticiLog4j, "Severità Log4j");
		format(bf, log4j_diagnostica ? "abilitato" : "disabilitato", "Log4J openspcoop2_msgDiagnostico.log");
		format(bf, log4j_openspcoop ? "abilitato" : "disabilitato", "Log4J openspcoop2.log");
		format(bf, log4j_integrationManager ? "abilitato" : "disabilitato", "Log4J openspcoop2_integrationManager.log");
		bf.append("\n");
		
		bf.append("===========================\n");
		bf.append("Informazioni Tracciamento\n");
		bf.append("===========================\n");
		bf.append("\n");
		format(bf, tracciamento ? "abilitato" : "disabilitato", "Buste");
		format(bf, dumpApplicativo ? "abilitato" : "disabilitato", "Dump Applicativo");
		format(bf, dumpPD ? "abilitato" : "disabilitato", "Dump Binario Porta Delegata");
		format(bf, dumpPA ? "abilitato" : "disabilitato", "Dump Binario Porta Applicativa");
		format(bf, log4j_tracciamento ? "abilitato" : "disabilitato", "Log4J openspcoop2_tracciamento.log");
		format(bf, log4j_dump ? "abilitato" : "disabilitato", "Log4J openspcoop2_dump.log");
		bf.append("\n");
		
		bf.append("===========================\n");
		bf.append("Informazioni Database\n");
		bf.append("===========================\n");
		bf.append("\n");
		bf.append(infoDatabase);
		bf.append("\n");
		
		bf.append("===========================\n");
		bf.append("Informazioni Protocolli\n");
		bf.append("===========================\n");
		bf.append("\n");
		bf.append(infoProtocolli);
		bf.append("\n");
		bf.append("\n");
		
		if(cache!=null){
			for (int i = 0; i < cache.length; i++) {
				bf.append("===========================\n");
				bf.append("Cache "+cache[i].getNomeCache()+"\n");
				bf.append("===========================\n");
				bf.append("\n");
				format(bf, cache[i].isEnabled()+"", "Abilitata");
				if(cache[i].getStatoCache()!=null){
					bf.append(cache[i].getStatoCache());
				}
				bf.append("\n");
				bf.append("\n");
			}
		}
		
		if(statoConnessioniDB!=null){
			bf.append("==============================\n");
			bf.append("Connessioni Attive al Database\n");
			bf.append("==============================\n");
			bf.append("\n");
			bf.append(statoConnessioniDB);
			bf.append("\n");
			bf.append("\n");
		}
		
		if(statoConnessioniPD!=null){
			bf.append("=========================================================\n");
			bf.append("Connessioni HTTP Attive in uscita dal modulo InoltroBuste\n");
			bf.append("=========================================================\n");
			bf.append("\n");
			bf.append(statoConnessioniPD);
			bf.append("\n");
			bf.append("\n");
		}
		
		if(statoConnessioniPA!=null){
			bf.append("=========================================================================\n");
			bf.append("Connessioni HTTP Attive in uscita dal modulo ConsegnaContenutiApplicativi\n");
			bf.append("=========================================================================\n");
			bf.append("\n");
			bf.append(statoConnessioniPA);
			bf.append("\n");
			bf.append("\n");
		}
		
		if(statoConnessioniJMS!=null){
			bf.append("================================\n");
			bf.append("Connessioni Attive al Broker JMS\n");
			bf.append("================================\n");
			bf.append("\n");
			bf.append(statoConnessioniJMS);
			bf.append("\n");
			bf.append("\n");
		}
		
		return bf.toString();
	}
	
	private void format(StringBuffer bf,String v,String label){
		if(v==null || "".equals(v)){
			bf.append(label+": informazione non disponibile\n");
		}
		else{
			if(v.contains("\n")){
				bf.append(label+": \n");
				String [] tmp = v.split("\n");
				for (int i = 0; i < tmp.length; i++) {
					bf.append("\t- "+tmp[i]+"\n");
				}
			}else{
				bf.append(label+": "+v+"\n");
			}
		}
	}
}
