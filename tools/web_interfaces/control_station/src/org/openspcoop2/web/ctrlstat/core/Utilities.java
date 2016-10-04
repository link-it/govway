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


package org.openspcoop2.web.ctrlstat.core;

import java.util.ArrayList;
import java.util.StringTokenizer;

import org.openspcoop2.core.registry.Connettore;
import org.openspcoop2.web.ctrlstat.costanti.TipologiaConnettori;
import org.openspcoop2.web.ctrlstat.servlet.connettori.ConnettoriCostanti;
import org.openspcoop2.web.lib.mvc.DataElement;
/**
 * 
 * Metodi di utilita'
 * 
 * @author Andrea Poli (apoli@link.it)
 * @author Stefano Corallo (corallo@link.it)
 * @author Sandra Giangrandi (sandra@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 * 
 */
public class Utilities {

	
	public static boolean contains(String value,String [] listaValoriPossibili){
		if(value==null)
			return false;
		if(listaValoriPossibili==null){
			return false;
		}
		for (int i = 0; i < listaValoriPossibili.length; i++) {
			if(value.equals(listaValoriPossibili[i])){
				return true;
			}
		}
		return false;
	}
	
	public static String toString(String [] values,String separator){
		StringBuffer bf = new StringBuffer();
		if(values!=null){
			for (int i = 0; i < values.length; i++) {
				if(i>0){
					bf.append(separator);
					bf.append(values[i]);
				}
			}
		}
		return bf.toString();
	}
	
	
	private static TipologiaConnettori TIPOLOGIA_CONNETTORI = null;
	public static TipologiaConnettori getTipologiaConnettori(ControlStationCore core) throws Exception {

		if (Utilities.TIPOLOGIA_CONNETTORI == null) {
			Utilities.readTipologiaConnettori(core);
		}

		return Utilities.TIPOLOGIA_CONNETTORI;

	}

	/**
	 * Tipologia connettori ALL/HTTP
	 * 
	 * @param tipologia
	 */
	 public static void setTipologiaConnettori(TipologiaConnettori tipologia) {
		 Utilities.TIPOLOGIA_CONNETTORI = tipologia == null ? TipologiaConnettori.TIPOLOGIA_CONNETTORI_ALL : tipologia;
	 }

	 /**
	  * Legge il valore della proprieta' impostata nel file di configurazione se
	  * la proprieta' e' nulla setta il valore di default (ALL)
	  */
	 public static void readTipologiaConnettori(ControlStationCore core) throws Exception {
		 
		 if (core.isShowAllConnettori()) {
			 Utilities.TIPOLOGIA_CONNETTORI = TipologiaConnettori.TIPOLOGIA_CONNETTORI_ALL;
		 } else {
			 Utilities.TIPOLOGIA_CONNETTORI = TipologiaConnettori.TIPOLOGIA_CONNETTORI_HTTP;
		 } 

	 }
	 
	 public static void setDataElementLabelTipoConnettore(DataElement de,Connettore connettore){
		 de.setValue(ConnettoriCostanti.LABEL_CONNETTORE+" (" + connettore.getTipo() + ")");
	 }


	 public static ArrayList<String> parseIdsToRemove(String idsToRemove) {
		 ArrayList<String> toRem = new ArrayList<String>();
		 StringTokenizer objTok = new StringTokenizer(idsToRemove, ",");

		 while (objTok.hasMoreElements()) {
			 String id2rem = (String) objTok.nextElement();
			 toRem.add(id2rem);
		 }

		 return toRem;
	 }

	




	


	 public static String getTestoVisualizzabile(byte [] b,StringBuffer stringBuffer) {
		 try{
			 // 1024 = 1K
			 // Visualizzo al massimo 250K
			 int max = 250 * 1024;
//			 if(b.length>max){
//				 return "Visualizzazione non riuscita: la dimensione supera 250K";
//			 }
//
//			 for (int i = 0; i < b.length; i++) {
//				 if(!Utilities.isPrintableChar((char)b[i])){
//
//					 return "Visualizzazione non riuscita: il documento contiene caratteri non visualizzabili";
//				 }
//			 }
			 stringBuffer.append(org.openspcoop2.utils.Utilities.convertToPrintableText(b, max));
			 return null;

		 }catch(Exception e){
			 ControlStationCore.logError("getTestoVisualizzabile error", e);
			 return e.getMessage();
		 }

	 }

//	 public static boolean isPrintableChar( char c ) {
//		 if ( Character.isDefined(c))
//		 {
//			 return true;
//		 }
//		 else{
//			 return false;
//		 }
//	 }

}
