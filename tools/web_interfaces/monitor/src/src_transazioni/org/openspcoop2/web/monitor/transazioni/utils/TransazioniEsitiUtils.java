/*
 * GovWay - A customizable API Gateway 
 * http://www.govway.org
 * 
 * from the Link.it OpenSPCoop project codebase
 * 
 * Copyright (c) 2005-2019 Link.it srl (http://link.it).
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
package org.openspcoop2.web.monitor.transazioni.utils;

import org.openspcoop2.monitor.engine.condition.EsitoUtils;
import org.openspcoop2.protocol.sdk.constants.EsitoTransazioneName;
import org.openspcoop2.protocol.utils.EsitiProperties;
import org.openspcoop2.web.monitor.core.logger.LoggerManager;

/**
 * TransazioniEsitiUtils
 * 
 * @author Pintori Giuliano (pintori@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 *
 */
public class TransazioniEsitiUtils {
	
	public static boolean isEsitoOk(Integer esito, String protocollo){	
		try{
			EsitiProperties esitiProperties = EsitiProperties.getInstance(LoggerManager.getPddMonitorCoreLogger(),protocollo);
			boolean res = esitiProperties.getEsitiCodeOk_senzaFaultApplicativo().contains(esito);
			//System.out.println("isEsitoOk:"+res+" (esitoChecked:"+esito+")");
			return res;
		}catch(Exception e){
			LoggerManager.getPddMonitorCoreLogger().error("Errore durante il calcolo del layout dell'esito ["+esito+"]: "+e.getMessage(),e);
			return false;
		}
	}
	public static boolean isEsitoFaultApplicativo(Integer esito, String protocollo){	
		try{
			EsitiProperties esitiProperties = EsitiProperties.getInstance(LoggerManager.getPddMonitorCoreLogger(),protocollo);
			boolean res = esitiProperties.getEsitiCodeFaultApplicativo().contains(esito);
			//System.out.println("isEsitoOk:"+res+" (esitoChecked:"+esito+")");
			return res;
		}catch(Exception e){
			LoggerManager.getPddMonitorCoreLogger().error("Errore durante il calcolo del layout dell'esito ["+esito+"]: "+e.getMessage(),e);
			return false;
		}
	}
	public static boolean isEsitoKo(Integer esito, String protocollo){	
		try{
			EsitiProperties esitiProperties = EsitiProperties.getInstance(LoggerManager.getPddMonitorCoreLogger(),protocollo);
			boolean res = esitiProperties.getEsitiCodeKo_senzaFaultApplicativo().contains(esito);
			//System.out.println("isEsitoOk:"+res+" (esitoChecked:"+esito+")");
			return res;
		}catch(Exception e){
			LoggerManager.getPddMonitorCoreLogger().error("Errore durante il calcolo del layout dell'esito ["+esito+"]: "+e.getMessage(),e);
			return false;
		}
	}
	
	public static java.lang.String getEsitoLabel(Integer esito, String protocollo) {
		try{
			EsitoUtils esitoUtils = new EsitoUtils(LoggerManager.getPddMonitorCoreLogger(), protocollo);
			return esitoUtils.getEsitoLabelFromValue(esito, false);
		}catch(Exception e){
			LoggerManager.getPddMonitorCoreLogger().error("Errore durante il calcolo della label per l'esito ["+esito+"]: "+e.getMessage(),e);
			return "Conversione non riuscita";
		}
	}
	
	public static java.lang.String getEsitoLabelSyntetic(Integer esito, String protocollo) {
		try{
			EsitiProperties esitiProperties = EsitiProperties.getInstance(LoggerManager.getPddMonitorCoreLogger(),protocollo);
			return esitiProperties.getEsitoLabelSyntetic(esito);
		}catch(Exception e){
			LoggerManager.getPddMonitorCoreLogger().error("Errore durante il calcolo della label per l'esito ["+esito+"]: "+e.getMessage(),e);
			return "Conversione non riuscita";
		}
	}
	
	public static java.lang.String getEsitoLabelDescription(Integer esito, String protocollo) {
		try{
			EsitiProperties esitiProperties = EsitiProperties.getInstance(LoggerManager.getPddMonitorCoreLogger(),protocollo);
			return getEsitoLabel(esito, protocollo) + " - " + esitiProperties.getEsitoDescription(esito);
		}catch(Exception e){
			LoggerManager.getPddMonitorCoreLogger().error("Errore durante il calcolo della label per l'esito ["+esito+"]: "+e.getMessage(),e);
			return "Conversione non riuscita";
		}
	}
	
	public static java.lang.String getEsitoContestoLabel(String esitoContesto, String protocollo) {
		try{
			EsitoUtils esitoUtils = new EsitoUtils(LoggerManager.getPddMonitorCoreLogger(), protocollo);
			return esitoUtils.getEsitoContestoLabelFromValue(esitoContesto);
		}catch(Exception e){
			LoggerManager.getPddMonitorCoreLogger().error("Errore durante il calcolo della label per il contesto esito ["+esitoContesto+"]: "+e.getMessage(),e);
			return "Conversione non riuscita";
		}
	}
	
	
	public static String getEsitoStyleClass(Integer esito, String protocollo){

		try{
			EsitiProperties esitiProperties = EsitiProperties.getInstance(LoggerManager.getPddMonitorCoreLogger(),protocollo);
			String name = esitiProperties.getEsitoName(esito);
			EsitoTransazioneName esitoName = EsitoTransazioneName.convertoTo(name);
			boolean casoSuccesso = esitiProperties.getEsitiCodeOk().contains(esito);
			if(EsitoTransazioneName.ERRORE_APPLICATIVO.equals(esitoName)){
				//return "icon-alert-orange";
				return "icon-alert-yellow";
			}
			else if(casoSuccesso){
				return "icon-verified-green";
			}
			else{
				return "icon-alert-red";
			}
		}catch(Exception e){
			LoggerManager.getPddMonitorCoreLogger().error("Errore durante il calcolo del layout dell'esito ["+esito+"]: "+e.getMessage(),e);
			return "icon-ko";
		}

	}
}
