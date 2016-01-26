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
package org.openspcoop2.web.ctrlstat.servlet.apc;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import org.openspcoop2.core.registry.AccordoServizioParteComune;
import org.openspcoop2.core.registry.Operation;
import org.openspcoop2.core.registry.PortType;
import org.openspcoop2.core.registry.driver.DriverRegistroServiziException;
import org.openspcoop2.web.ctrlstat.core.Search;
import org.openspcoop2.web.lib.mvc.Parameter;


/**
 * AccordiServizioParteComuneUtilities
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class AccordiServizioParteComuneUtilities {

	public static String getTerminologiaAccordoServizio(String tipo){
		String termine = null;
		if(AccordiServizioParteComuneCostanti.PARAMETRO_VALORE_APC_TIPO_ACCORDO_PARTE_COMUNE.equals(tipo)){
			termine=AccordiServizioParteComuneCostanti.LABEL_APC;
		}
		else{
			termine=AccordiServizioParteComuneCostanti.LABEL_ASC;
		}
		return termine;
	}

	@Deprecated
	public static String getParametroAccordoServizio(String tipo,String appendChar){
		String parametro = "";
		if(AccordiServizioParteComuneCostanti.PARAMETRO_VALORE_APC_TIPO_ACCORDO_PARTE_COMUNE.equals(tipo)){
			parametro = appendChar+AccordiServizioParteComuneCostanti.PARAMETRO_APC_TIPO_ACCORDO+"="+AccordiServizioParteComuneCostanti.PARAMETRO_VALORE_APC_TIPO_ACCORDO_PARTE_COMUNE;
		}
		else{
			parametro = appendChar+AccordiServizioParteComuneCostanti.PARAMETRO_APC_TIPO_ACCORDO+"="+AccordiServizioParteComuneCostanti.PARAMETRO_VALORE_APC_TIPO_ACCORDO_SERVIZIO_COMPOSTO;
		}
		return parametro;
	}
	public static Parameter getParametroAccordoServizio(AccordoServizioParteComune apc){
		if(apc.getServizioComposto()!=null){
			return new Parameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_TIPO_ACCORDO,
					AccordiServizioParteComuneCostanti.PARAMETRO_VALORE_APC_TIPO_ACCORDO_SERVIZIO_COMPOSTO);
		}else
		{
			return new Parameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_TIPO_ACCORDO,
					AccordiServizioParteComuneCostanti.PARAMETRO_VALORE_APC_TIPO_ACCORDO_PARTE_COMUNE);
		}
	}

	public static Parameter getParametroAccordoServizio(String tipo){
		if(AccordiServizioParteComuneCostanti.PARAMETRO_VALORE_APC_TIPO_ACCORDO_PARTE_COMUNE.equals(tipo)){
			return new Parameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_TIPO_ACCORDO,
					AccordiServizioParteComuneCostanti.PARAMETRO_VALORE_APC_TIPO_ACCORDO_PARTE_COMUNE);
		}
		else{
			return new Parameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_TIPO_ACCORDO,
					AccordiServizioParteComuneCostanti.PARAMETRO_VALORE_APC_TIPO_ACCORDO_SERVIZIO_COMPOSTO);
		}
	}

	public static boolean showInformazioniServiziComponenti(String tipo){
		boolean value = true;
		if(AccordiServizioParteComuneCostanti.PARAMETRO_VALORE_APC_TIPO_ACCORDO_PARTE_COMUNE.equals(tipo)){
			value = false;
		}
		return value;
	}

	public static boolean showFlagServizioComposto(){
		return false;
	}

	public static List<AccordoServizioParteComune> accordiList(AccordiServizioParteComuneCore core,String userLogin,Search ricerca,String tipoAccordo) throws DriverRegistroServiziException{
		List<AccordoServizioParteComune> lista = null;
		if(AccordiServizioParteComuneCostanti.PARAMETRO_VALORE_APC_TIPO_ACCORDO_PARTE_COMUNE.equals(tipoAccordo)){
			if(core.isVisioneOggettiGlobale(userLogin)){
				lista = core.accordiServizioParteComuneList(null, ricerca);
			}else{
				lista = core.accordiServizioParteComuneList(userLogin, ricerca);
			}
		}
		else if(AccordiServizioParteComuneCostanti.PARAMETRO_VALORE_APC_TIPO_ACCORDO_SERVIZIO_COMPOSTO.equals(tipoAccordo)){
			if(core.isVisioneOggettiGlobale(userLogin)){
				lista = core.accordiServizioCompostiList(null, ricerca);
			}else{
				lista = core.accordiServizioCompostiList(userLogin, ricerca);
			}
		}
		else {
			if(core.isVisioneOggettiGlobale(userLogin)){
				lista = core.accordiList(null, ricerca);
			}else{
				lista = core.accordiList(userLogin, ricerca);
			}
		}
		return lista;
	}

	public static List<AccordoServizioParteComune> accordiListFromPermessiUtente(AccordiServizioParteComuneCore core,String userLogin,Search ricerca,boolean[] permessiUtente) throws DriverRegistroServiziException{
		List<AccordoServizioParteComune> lista = null;
		if(permessiUtente != null){
			if(permessiUtente[0] && !permessiUtente[1]){
				if(core.isVisioneOggettiGlobale(userLogin)){
					lista = core.accordiServizioParteComuneList(null, ricerca);
				}else{
					lista = core.accordiServizioParteComuneList(userLogin, ricerca);
				}

				return lista;
			}

			if(!permessiUtente[0] && permessiUtente[1]){
				if(core.isVisioneOggettiGlobale(userLogin)){
					lista = core.accordiServizioCompostiList(null, ricerca);
				}else{
					lista = core.accordiServizioCompostiList(userLogin, ricerca);
				}

				return lista;
			}
		}


		if(core.isVisioneOggettiGlobale(userLogin)){
			lista = core.accordiList(null, ricerca);
		}else{
			lista = core.accordiList(userLogin, ricerca);
		}

		return lista;
	}

	public static ArrayList<String> selectPortTypeAsincroni(AccordoServizioParteComune as,String profcollop,String nomept){
		// Recupero i servizi dell'accordo con lo stesso profilo di collaborazione
		ArrayList<String> servCorrUniche = null;
		//List<PortType> getServCorrList = core.accordiPorttypeList(idInt, null, new Search(true));
		List<PortType> getServCorrList = new ArrayList<PortType>();
		for(int k=0;k<as.sizePortTypeList();k++){
			getServCorrList.add(as.getPortType(k));
		}

		if (getServCorrList.size() > 0) {
			servCorrUniche = new ArrayList<String>();
			servCorrUniche.add("-");
			for (Iterator<PortType> iterator = getServCorrList.iterator(); iterator.hasNext();) {
				PortType myPT = iterator.next();
				// Devono possedere almeno un'azione
				if(myPT.sizeAzioneList()<=0)
					continue;
				// Devono possedere almeno un'azione con profilo di collaborazione uguale a quello selezionato 
				List<Operation> opList = new Vector<Operation>();
				for(int j=0; j<myPT.sizeAzioneList();j++){
					Operation op = myPT.getAzione(j);
					// ridefinito sull'azione
					if(AccordiServizioParteComuneCostanti.INFORMAZIONI_PROTOCOLLO_MODALITA_RIDEFINITO.equals(op.getProfAzione()) && 
							(op.getProfiloCollaborazione()!=null && op.getProfiloCollaborazione().equals(profcollop))
							){
						opList.add(op);
					}
					// ereditato dal port type
					else if (AccordiServizioParteComuneCostanti.INFORMAZIONI_PROTOCOLLO_MODALITA_DEFAULT.equals(op.getProfAzione()) && 
							AccordiServizioParteComuneCostanti.INFORMAZIONI_PROTOCOLLO_MODALITA_RIDEFINITO.equals(myPT.getProfiloPT()) && 
							(myPT.getProfiloCollaborazione()!=null && myPT.getProfiloCollaborazione().equals(profcollop))
							){
						opList.add(op);
					}
					// ereditato dall'accordo di servizio
					else if (AccordiServizioParteComuneCostanti.INFORMAZIONI_PROTOCOLLO_MODALITA_DEFAULT.equals(op.getProfAzione()) && 
							AccordiServizioParteComuneCostanti.INFORMAZIONI_PROTOCOLLO_MODALITA_DEFAULT.equals(myPT.getProfiloPT()) && 
							(as.getProfiloCollaborazione()!=null && as.getProfiloCollaborazione().equals(profcollop))
							){
						opList.add(op);
					}
					// profilo non matcha quello atteso
					else{
						continue;
					}
				}
				if(opList.size()<=0){
					continue;
				}
				if (!nomept.equals(myPT.getNome())) {
					servCorrUniche.add(myPT.getNome());
				}
			}
		}

		if (servCorrUniche != null && servCorrUniche.size() > 0)
			return servCorrUniche;
		else
			return null;
	}

	public static Hashtable<String, List<Operation>> selectPortTypeOperationsListAsincrone(AccordoServizioParteComune as,String profcollop,String nomept){
		//List<PortType> getServCorrList = core.accordiPorttypeList(idInt, null, new Search(true));
		List<PortType> getServCorrList = new ArrayList<PortType>();
		for(int k=0;k<as.sizePortTypeList();k++){
			getServCorrList.add(as.getPortType(k));
		}
		Hashtable<String, List<Operation>> operationsListSelezionate = new Hashtable<String, List<Operation>>();

		if (getServCorrList.size() > 0) {
			for (Iterator<PortType> iterator = getServCorrList.iterator(); iterator.hasNext();) {
				PortType myPT = iterator.next();
				// Devono possedere almeno un'azione
				if(myPT.sizeAzioneList()<=0)
					continue;
				// Devono possedere almeno un'azione con profilo di collaborazione uguale a quello selezionato 
				List<Operation> opList = new Vector<Operation>();
				for(int j=0; j<myPT.sizeAzioneList();j++){
					Operation op = myPT.getAzione(j);
					// ridefinito sull'azione
					if(AccordiServizioParteComuneCostanti.INFORMAZIONI_PROTOCOLLO_MODALITA_RIDEFINITO.equals(op.getProfAzione()) && 
							(op.getProfiloCollaborazione()!=null && op.getProfiloCollaborazione().equals(profcollop))
							){
						opList.add(op);
					}
					// ereditato dal port type
					else if (AccordiServizioParteComuneCostanti.INFORMAZIONI_PROTOCOLLO_MODALITA_DEFAULT.equals(op.getProfAzione()) && 
							AccordiServizioParteComuneCostanti.INFORMAZIONI_PROTOCOLLO_MODALITA_RIDEFINITO.equals(myPT.getProfiloPT()) && 
							(myPT.getProfiloCollaborazione()!=null && myPT.getProfiloCollaborazione().equals(profcollop))
							){
						opList.add(op);
					}
					// ereditato dall'accordo di servizio
					else if (AccordiServizioParteComuneCostanti.INFORMAZIONI_PROTOCOLLO_MODALITA_DEFAULT.equals(op.getProfAzione()) && 
							AccordiServizioParteComuneCostanti.INFORMAZIONI_PROTOCOLLO_MODALITA_DEFAULT.equals(myPT.getProfiloPT()) &&
							(as.getProfiloCollaborazione()!=null && as.getProfiloCollaborazione().equals(profcollop))
							){
						opList.add(op);
					}
					// profilo non matcha quello atteso
					else{
						continue;
					}
				}
				if(opList.size()<=0){
					continue;
				}
				if (!nomept.equals(myPT.getNome())) {
					operationsListSelezionate.put(myPT.getNome(), opList);
				}
				else{
					operationsListSelezionate.put("-", opList);
				}
			}
		}

		return operationsListSelezionate;
	}

	public static ArrayList<String> selectOperationAsincrone(AccordoServizioParteComune as,String servcorr,String profProtocollo,
			String profcollop,PortType pt,String nomeop,AccordiServizioParteComuneCore core, Hashtable<String, List<Operation>> operationsListSelezionate)throws DriverRegistroServiziException{
		// Se è stato selezionato un servizio,
		// recupero le azioni del servizio
		// che non sono già state correlate
		//
		// Se non è stato selezionato un servizio:
		// - se il profilo dell'azione è
		//   asincronoAsimmetrico, recupero le
		//   azioni del servizio con profilo
		//   asincronoAsimmetrico che non sono già
		//   state correlate
		// - se il profilo dell'azione è
		//   asincronoSimmetrico, non permetto
		//   di scegliere un'azione correlata
		ArrayList<String> aziCorrUniche = null;
		List<Operation> getAziCorrList = null;
		PortType ptSel = null;
		if (!servcorr.equals("-")) {
			// Prendo il servizio selezionato
			for (int j = 0; j < as.sizePortTypeList(); j++) {
				ptSel = as.getPortType(j);
				if (servcorr.equals(ptSel.getNome()))
					break;
			}
			//getAziCorrList = core.accordiPorttypeOperationList(ptSel.getId().intValue(), ptSel.getProfiloCollaborazione(), new Search(true));*/
			getAziCorrList = operationsListSelezionate.get(servcorr);
		} else {
			if ((profProtocollo.equals(AccordiServizioParteComuneCostanti.INFORMAZIONI_PROTOCOLLO_MODALITA_DEFAULT) && 
					as.getProfiloCollaborazione().equals("asincronoAsimmetrico")) || 
					(profProtocollo.equals(AccordiServizioParteComuneCostanti.INFORMAZIONI_PROTOCOLLO_MODALITA_RIDEFINITO) && 
							profcollop.equals("asincronoAsimmetrico"))){
				//getAziCorrList = core.accordiPorttypeOperationList(pt.getId().intValue(), "asincronoAsimmetrico", new Search(true));
				getAziCorrList = operationsListSelezionate.get(servcorr);
				ptSel = pt;
			}
		}

		if (getAziCorrList != null && getAziCorrList.size() > 0) {
			aziCorrUniche = new ArrayList<String>();
			aziCorrUniche.add("-");
			for (Iterator<Operation> iterator = getAziCorrList.iterator(); iterator.hasNext();) {
				Operation operation = iterator.next();
				// Se sto utilizzando lo stesso port type, non devo inserire le azioni con il mio nome 
				if (servcorr.equals("-") && nomeop.equals(operation.getNome())) {
					continue;
				}
				// Non devo inserire le azioni gia correlate une alle altre.
				// Devo quindi verificare se l'azione suddetta 'operation.getNome' risulta gia' correlata, oppure e' utilizzata come correlata
				if (!core.isOperationCorrelata(ptSel.getNome(), ptSel.getId(), operation.getNome(), operation.getId())) {
					aziCorrUniche.add(operation.getNome());
				}
			}
		}

		if (aziCorrUniche != null && aziCorrUniche.size() > 0)
			return aziCorrUniche;
		else
			return null;
	}

}
