/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2020 Link.it srl (https://link.it). 
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
package org.openspcoop2.web.ctrlstat.servlet.apc;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import org.openspcoop2.core.commons.ErrorsHandlerCostant;
import org.openspcoop2.core.id.IDAccordo;
import org.openspcoop2.core.id.IDPortType;
import org.openspcoop2.core.id.IDPortaApplicativa;
import org.openspcoop2.core.id.IDPortaDelegata;
import org.openspcoop2.core.id.IDServizio;
import org.openspcoop2.core.mapping.MappingErogazionePortaApplicativa;
import org.openspcoop2.core.mapping.MappingFruizionePortaDelegata;
import org.openspcoop2.core.registry.AccordoServizioParteComune;
import org.openspcoop2.core.registry.AccordoServizioParteComuneServizioComposto;
import org.openspcoop2.core.registry.AccordoServizioParteSpecifica;
import org.openspcoop2.core.registry.Documento;
import org.openspcoop2.core.registry.Operation;
import org.openspcoop2.core.registry.PortType;
import org.openspcoop2.core.registry.Resource;
import org.openspcoop2.core.registry.beans.AccordoServizioParteComuneSintetico;
import org.openspcoop2.core.registry.constants.FormatoSpecifica;
import org.openspcoop2.core.registry.constants.RuoliDocumento;
import org.openspcoop2.core.registry.constants.StatiAccordo;
import org.openspcoop2.core.registry.driver.DriverRegistroServiziException;
import org.openspcoop2.core.registry.driver.DriverRegistroServiziNotFound;
import org.openspcoop2.core.registry.driver.IDAccordoFactory;
import org.openspcoop2.core.registry.driver.ValidazioneStatoPackageException;
import org.openspcoop2.protocol.engine.utils.DBOggettiInUsoUtils;
import org.openspcoop2.protocol.engine.utils.NamingUtils;
import org.openspcoop2.protocol.manifest.constants.InterfaceType;
import org.openspcoop2.web.ctrlstat.core.Search;
import org.openspcoop2.web.ctrlstat.servlet.aps.AccordiServizioParteSpecificaCore;
import org.openspcoop2.web.ctrlstat.servlet.archivi.ArchiviCore;
import org.openspcoop2.web.ctrlstat.servlet.config.ConfigurazioneCore;
import org.openspcoop2.web.ctrlstat.servlet.pa.PorteApplicativeCore;
import org.openspcoop2.web.ctrlstat.servlet.pd.PorteDelegateCore;
import org.openspcoop2.web.lib.mvc.Parameter;


/**
 * AccordiServizioParteComuneUtilities
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class AccordiServizioParteComuneUtilities {

	public static void findOggettiDaAggiornare(IDAccordo idAccordoOLD, AccordoServizioParteComune as, AccordiServizioParteComuneCore apcCore, List<Object> listOggettiDaAggiornare) throws Exception {
		
		AccordiServizioParteSpecificaCore apsCore = new AccordiServizioParteSpecificaCore(apcCore);
		
		String newURI = IDAccordoFactory.getInstance().getUriFromAccordo(as);

		// Cerco i servizi in cui devo cambiare la URI dell'accordo
		List<AccordoServizioParteSpecifica> servizi = apsCore.serviziByAccordoFilterList(idAccordoOLD);
		if(servizi!=null && servizi.size()>0){
			while(servizi.size()>0){
				AccordoServizioParteSpecifica s = servizi.remove(0);
				s.setAccordoServizioParteComune(newURI);
				listOggettiDaAggiornare.add(s);
			}
		}
		
	}
	
	public static void deleteAccordoServizioParteComune(AccordoServizioParteComune as, String userLogin, AccordiServizioParteComuneCore apcCore, AccordiServizioParteComuneHelper apcHelper, StringBuffer inUsoMessage, String newLine) throws Exception {
		
		HashMap<ErrorsHandlerCostant, List<String>> whereIsInUso = new HashMap<ErrorsHandlerCostant, List<String>>();
		
		IDAccordo idAccordo = IDAccordoFactory.getInstance().getIDAccordoFromAccordo(as);

		boolean normalizeObjectIds = !apcHelper.isModalitaCompleta();
		if (apcCore.isAccordoInUso(as, whereIsInUso, normalizeObjectIds)) {// accordo in uso
			inUsoMessage.append(DBOggettiInUsoUtils.toString(idAccordo, whereIsInUso, true, newLine, normalizeObjectIds));
			inUsoMessage.append(newLine);
		} else {// accordo non in uso
			apcCore.performDeleteOperation(userLogin, apcHelper.smista(), as);
		}
		
	}
	
	public static void deleteAccordoServizioParteComuneRisorse(AccordoServizioParteComune as, String userLogin, AccordiServizioParteComuneCore apcCore, AccordiServizioParteComuneHelper apcHelper, 
			StringBuffer inUsoMessage, String newLine, List<IDServizio> idServiziWithAccordo, List<String> risorse) throws Exception {
		
		PorteApplicativeCore porteApplicativeCore = new PorteApplicativeCore(apcCore);
		PorteDelegateCore porteDelegateCore = new PorteDelegateCore(apcCore);
		ConfigurazioneCore confCore = new ConfigurazioneCore(apcCore);
		
		boolean modificaAS_effettuata = false;
		for (int i = 0; i < risorse.size(); i++) {
			String nomeRisorsa = risorse.get(i);
			
			// traduco nomeRisorsa in path
			String methodPath = null;
			for (int j = 0; j < as.sizeResourceList(); j++) {
				Resource risorsa = as.getResource(j);
				if (nomeRisorsa.equals(risorsa.getNome())) {
					methodPath = NamingUtils.getLabelResource(risorsa);
					break;
				}
			}
			if(methodPath==null) {
				methodPath = nomeRisorsa;
			}
			
			// Se esiste un mapping
			List<MappingErogazionePortaApplicativa> lPA = porteApplicativeCore.getMappingConGruppiPerAzione(nomeRisorsa, idServiziWithAccordo);
			if(lPA!=null && !lPA.isEmpty()) {
				if(inUsoMessage.length()>0) {
					inUsoMessage.append(newLine);
				}
				inUsoMessage.append("Risorsa '"+methodPath+"' non rimuovibile poichè riassegnata in un gruppo dell'erogazione del servizio: "+newLine);
				for(int j=0;j<lPA.size();j++){
					inUsoMessage.append("- "+lPA.get(j).getIdServizio()+" (gruppo: '"+lPA.get(j).getDescrizione()+"')"+newLine);
				}
				continue;
			}
			List<MappingFruizionePortaDelegata> lPD = porteDelegateCore.getMappingConGruppiPerAzione(nomeRisorsa, idServiziWithAccordo);
			if(lPD!=null && !lPD.isEmpty()) {
				if(inUsoMessage.length()>0) {
					inUsoMessage.append(newLine);
				}
				inUsoMessage.append("Risorsa '"+methodPath+"' non rimuovibile poichè riassegnata in un gruppo della fruizione del servizio: "+newLine);
				for(int j=0;j<lPD.size();j++){
					inUsoMessage.append("- "+lPD.get(j).getIdServizio()+" (fruitore: "+lPD.get(j).getIdFruitore()+") (gruppo: '"+lPD.get(j).getDescrizione()+"')"+newLine);
				}
				continue;
			}
			
			// Controllo se assegnata in filtri di rate limiting
			if(confCore.usedInConfigurazioneControlloTrafficoAttivazionePolicy(null,null, nomeRisorsa)) {
				if(inUsoMessage.length()>0) {
					inUsoMessage.append(newLine);
				}
				inUsoMessage.append("Risorsa '"+methodPath+"' non rimuovibile poichè in uso in politiche di Rate Limiting"+newLine);
				continue;
			}
			
			// Controllo se assegnata in criteri di applicabilita delle trasformazioni
			if(porteApplicativeCore.azioneUsataInTrasformazioniPortaApplicativa(nomeRisorsa)) {
				if(inUsoMessage.length()>0) {
					inUsoMessage.append(newLine);
				}
				inUsoMessage.append("Risorsa '"+methodPath+"' non rimuovibile poichè in uso in erogazioni per la configurazione di trasformazioni"+newLine);
				continue;
			}
			if(porteDelegateCore.azioneUsataInTrasformazioniPortaDelegata(nomeRisorsa)) {
				if(inUsoMessage.length()>0) {
					inUsoMessage.append(newLine);
				}
				inUsoMessage.append("Risorsa '"+methodPath+"' non rimuovibile poichè in uso in fruizioni per la configurazione di trasformazioni"+newLine);
				continue;
			}
			
			// Controllo che l'azione non sia in uso (se esistono servizi, allora poi saranno state create PD o PA)
			if(idServiziWithAccordo!=null && idServiziWithAccordo.size()>0){
				
				// Se esiste solo un'azione con tale identificativo, posso effettuare il controllo che non vi siano porteApplicative/porteDelegate esistenti.
				if (porteApplicativeCore.existsPortaApplicativaAzione(nomeRisorsa)) {
					List<IDPortaApplicativa> idPAs = porteApplicativeCore.getPortaApplicativaAzione(nomeRisorsa);
					if(inUsoMessage.length()>0) {
						inUsoMessage.append(newLine);
					}
					inUsoMessage.append("Risorsa '"+methodPath+"' non rimuovibile poichè in uso in porte applicative: "+newLine);
					for(int j=0;j<idPAs.size();j++){
						inUsoMessage.append("- "+idPAs.get(j).toString()+newLine);
					}
					continue;
				}
				if (porteDelegateCore.existsPortaDelegataAzione(nomeRisorsa)) {
					List<IDPortaDelegata> idPDs = porteDelegateCore.getPortaDelegataAzione(nomeRisorsa);
					if(inUsoMessage.length()>0) {
						inUsoMessage.append(newLine);
					}
					inUsoMessage.append("Risorsa '"+methodPath+"' non rimuovibile poichè in uso in porte delegate: "+newLine);
					for(int j=0;j<idPDs.size();j++){
						inUsoMessage.append("- "+idPDs.get(j).toString()+newLine);
					}
					continue;
				}
				
			}
			
			// Effettuo eliminazione
			for (int j = 0; j < as.sizeResourceList(); j++) {
				Resource risorsa = as.getResource(j);
				if (nomeRisorsa.equals(risorsa.getNome())) {
					modificaAS_effettuata = true;
					as.removeResource(j);
					break;
				}
			}
		}
		
		// effettuo le operazioni
		if(modificaAS_effettuata)
			apcCore.performUpdateOperation(userLogin, apcHelper.smista(), as);
		
	}
	
	public static void deleteAccordoServizioParteComunePortTypes(AccordoServizioParteComune as, String userLogin, AccordiServizioParteComuneCore apcCore, AccordiServizioParteComuneHelper apcHelper, 
			StringBuffer inUsoMessage, String newLine, IDPortType idPT, List<String> ptsToRemove) throws Exception {
		
		AccordiServizioParteSpecificaCore apsCore = new AccordiServizioParteSpecificaCore(apcCore);
		PorteApplicativeCore porteApplicativeCore = new PorteApplicativeCore(apcCore);
		PorteDelegateCore porteDelegateCore = new PorteDelegateCore(apcCore);
		
		String nomept = "";
		boolean modificaAS_effettuata = false;
		for (int i = 0; i < ptsToRemove.size(); i++) {

			nomept = ptsToRemove.get(i);

			idPT.setNome(nomept);
			List<IDServizio> idServizi = null;
			try{
				idServizi = apsCore.getIdServiziWithPortType(idPT);
			}catch(DriverRegistroServiziNotFound dNotF){}
			
			if(idServizi==null || idServizi.size()<=0){
			
				// Check che il port type non sia correlato da altri porttype.
				Vector<String> tmp = new Vector<String>();
				for (int j = 0; j < as.sizePortTypeList(); j++) {
					PortType pt = as.getPortType(j);
					if(pt.getNome().equals(nomept)==false){
						for (int check = 0; check < pt.sizeAzioneList(); check++) {
							Operation opCheck = pt.getAzione(check);
							if(opCheck.getCorrelataServizio()!=null && nomept.equals(opCheck.getCorrelataServizio())){
								tmp.add("azione "+opCheck.getNome()+" del servizio "+pt.getNome());
							}
						}
					}
				}
				if(tmp.size()>0){
					if(inUsoMessage.length()>0) {
						inUsoMessage.append(newLine);
					}
					inUsoMessage.append("Servizio ["+nomept+"] non rimosso poichè correlato da azioni di altri servizi della API: "+newLine);
					for(int j=0; j<tmp.size();j++){
						inUsoMessage.append("- "+tmp.get(j).toString()+newLine);
					}
					continue;
				}
				
				// Effettuo eliminazione
				for (int j = 0; j < as.sizePortTypeList(); j++) {
					PortType pt = as.getPortType(j);
					if (nomept.equals(pt.getNome())) {
						modificaAS_effettuata = true;
						as.removePortType(j);
						break;
					}
				}
				
			}else{
				
				// Se esiste un mapping segnalo l'errore specifico
				List<MappingErogazionePortaApplicativa> lPA = porteApplicativeCore.getMapping(idServizi, true, false);
				if(lPA!=null && !lPA.isEmpty()) {
					if(inUsoMessage.length()>0) {
						inUsoMessage.append(newLine);
					}
					inUsoMessage.append("Servizio "+nomept+" non rimuovibile poichè implementato nell'erogazione del servizio: "+newLine);
					for(int j=0;j<lPA.size();j++){
						inUsoMessage.append("- "+lPA.get(j).getIdServizio()+newLine);
					}
					continue;
				}
				List<MappingFruizionePortaDelegata> lPD = porteDelegateCore.getMapping(idServizi, true, false);
				if(lPD!=null && !lPD.isEmpty()) {
					if(inUsoMessage.length()>0) {
						inUsoMessage.append(newLine);
					}
					inUsoMessage.append("Servizio "+nomept+" non rimuovibile poichè implementato nella fruizione del servizio: "+newLine);
					for(int j=0;j<lPD.size();j++){
						inUsoMessage.append("- "+lPD.get(j).getIdServizio()+" (fruitore: "+lPD.get(j).getIdFruitore()+")"+newLine);
					}
					continue;
				}
				
				// Altrimenti segnalo l'errore più generico sull'accordo
				if(inUsoMessage.length()>0) {
					inUsoMessage.append(newLine);
				}
				inUsoMessage.append("Servizio ["+nomept+"] non rimosso poichè viene implementato dai seguenti servizi: "+newLine);
				for(int j=0; j<idServizi.size();j++){
					inUsoMessage.append("- "+idServizi.get(j).toString()+newLine);
				}
				//continue;
				
			}
		}
		

		// effettuo le operazioni
		if(modificaAS_effettuata)
			apcCore.performUpdateOperation(userLogin, apcHelper.smista(), as);
		
	}
	
	public static void deleteAccordoServizioParteComuneOperations(AccordoServizioParteComune as, String userLogin, AccordiServizioParteComuneCore apcCore, AccordiServizioParteComuneHelper apcHelper, 
			StringBuffer inUsoMessage, String newLine, PortType pt, List<IDServizio> idServiziWithPortType, List<String> optsToRemove) throws Exception {
	
		PorteApplicativeCore porteApplicativeCore = new PorteApplicativeCore(apcCore);
		PorteDelegateCore porteDelegateCore = new PorteDelegateCore(apcCore);
		ConfigurazioneCore confCore = new ConfigurazioneCore(apcCore);
		
		String nomept = pt.getNome();
		

		for (int i = 0; i < optsToRemove.size(); i++) {

			String nomeop = optsToRemove.get(i);
			
			
			// Controllo che l'azione non sia stata correlata da un'altra azione
			ArrayList<String> tmp = new ArrayList<String>();
			for (int b = 0; b < as.sizePortTypeList(); b++) {
				PortType ptCheck = as.getPortType(b);
				for (int check = 0; check < ptCheck.sizeAzioneList(); check++) {
					Operation opCheck = ptCheck.getAzione(check);
					if(!ptCheck.getNome().equals(nomept) || !opCheck.getNome().equals(nomeop)){
						// controllo le altre azioni che non siano correlate a quella da eliminare
						if(opCheck.getCorrelata()!=null && opCheck.getCorrelata().equals(nomeop)){
							if(opCheck.getCorrelataServizio()==null && ptCheck.getNome().equals(nomept)){
								tmp.add(opCheck.getNome()+" del servizio "+ptCheck.getNome());
							}
							if(opCheck.getCorrelataServizio()!=null && opCheck.getCorrelataServizio().equals(nomept)){
								tmp.add(opCheck.getNome()+" del servizio "+ptCheck.getNome());
							}
						}
					}
				}
			}
			if(tmp.size()>0){
				if(inUsoMessage.length()>0) {
					inUsoMessage.append(newLine);
				}
				inUsoMessage.append("Azione "+nomeop+" non rimuovibile poichè perche' correlata ad altre azioni:"+newLine );
				for(int p=0; p<tmp.size();p++){
					inUsoMessage.append("- "+tmp.get(p)+newLine);
				}
				continue;
			}
			
			// Controllo se assegnata in filtri di rate limiting
			if(confCore.usedInConfigurazioneControlloTrafficoAttivazionePolicy(null,null, nomeop)) {
				if(inUsoMessage.length()>0) {
					inUsoMessage.append(newLine);
				}
				inUsoMessage.append("Azione '"+nomeop+"' non rimuovibile poichè in uso in politiche di Rate Limiting"+newLine);
				continue;
			}
			
			// Controllo se assegnata in criteri di applicabilita delle trasformazioni
			if(porteApplicativeCore.azioneUsataInTrasformazioniPortaApplicativa(nomeop)) {
				if(inUsoMessage.length()>0) {
					inUsoMessage.append(newLine);
				}
				inUsoMessage.append("Azione '"+nomeop+"' non rimuovibile poichè in uso in erogazioni per la configurazione di trasformazioni"+newLine);
				continue;
			}
			if(porteDelegateCore.azioneUsataInTrasformazioniPortaDelegata(nomeop)) {
				if(inUsoMessage.length()>0) {
					inUsoMessage.append(newLine);
				}
				inUsoMessage.append("Azione '"+nomeop+"' non rimuovibile poichè in uso in fruizioni per la configurazione di trasformazioni"+newLine);
				continue;
			}
			
			
			// Controllo che l'azione non sia in uso (se esistono servizi, allora poi saranno state create PD o PA)
			if(idServiziWithPortType!=null && idServiziWithPortType.size()>0){
			
				// Se esiste un mapping
				List<MappingErogazionePortaApplicativa> lPA = porteApplicativeCore.getMappingConGruppiPerAzione(nomeop, idServiziWithPortType);
				if(lPA!=null && !lPA.isEmpty()) {
					if(inUsoMessage.length()>0) {
						inUsoMessage.append(newLine);
					}
					inUsoMessage.append("Azione "+nomeop+" non rimuovibile poichè riassegnata in un gruppo dell'erogazione del servizio: "+newLine);
					for(int j=0;j<lPA.size();j++){
						inUsoMessage.append("- "+lPA.get(j).getIdServizio()+" (gruppo: '"+lPA.get(j).getDescrizione()+"')"+newLine);
					}
					continue;
				}
				List<MappingFruizionePortaDelegata> lPD = porteDelegateCore.getMappingConGruppiPerAzione(nomeop, idServiziWithPortType);
				if(lPD!=null && !lPD.isEmpty()) {
					if(inUsoMessage.length()>0) {
						inUsoMessage.append(newLine);
					}
					inUsoMessage.append("Azione "+nomeop+" non rimuovibile poichè riassegnata in un gruppo della fruizione del servizio: "+newLine);
					for(int j=0;j<lPD.size();j++){
						inUsoMessage.append("- "+lPD.get(j).getIdServizio()+" (fruitore: "+lPD.get(j).getIdFruitore()+") (gruppo: '"+lPD.get(j).getDescrizione()+"')"+newLine);
					}
					continue;
				}
				
				if(apcCore.isUnicaAzioneInAccordi(nomeop)){
					
					// Se esiste solo un'azione con tale identificativo, posso effettuare il controllo che non vi siano porteApplicative/porteDelegate esistenti.
					if (porteApplicativeCore.existsPortaApplicativaAzione(nomeop)) {
						List<IDPortaApplicativa> idPAs = porteApplicativeCore.getPortaApplicativaAzione(nomeop);
						if(inUsoMessage.length()>0) {
							inUsoMessage.append(newLine);
						}
						inUsoMessage.append("Azione "+nomeop+" non rimuovibile poichè in uso in porte applicative: "+newLine);
						for(int j=0;j<idPAs.size();j++){
							inUsoMessage.append("- "+idPAs.get(j).toString()+newLine);
						}
						continue;
					}
					if (porteDelegateCore.existsPortaDelegataAzione(nomeop)) {
						List<IDPortaDelegata> idPDs = porteDelegateCore.getPortaDelegataAzione(nomeop);
						if(inUsoMessage.length()>0) {
							inUsoMessage.append(newLine);
						}
						inUsoMessage.append("Azione "+nomeop+" non rimuovibile poichè in uso in porte delegate: "+newLine);
						for(int j=0;j<idPDs.size();j++){
							inUsoMessage.append("- "+idPDs.get(j).toString()+newLine);
						}
						continue;
					}
					
				}else{
					
					// Se esiste piu' di un'azione con tale identificativo, non posso effettuare il controllo che non vi siano porteApplicative/porteDelegate esistenti,
					// poichè non saprei se l'azione di una PD/PA si riferisce all'azione in questione.
					// Allora non permetto l'eliminazione poichè esistono dei servizi che implementano l'accordo
					
					// RILASCIO CONTROLLO: non permette di eliminare azioni che comunque non sono usate nelle principali funzionalità sopra verificate.
					
					/*
					if(inUsoMessage.length()>0) {
						inUsoMessage.append(newLine);
					}
					inUsoMessage.append("Azione "+nomeop+" non rimuovibile poichè il servizio "+nomept+" della API viene implementato dai seguenti servizi: "+newLine);
					for(int j=0; j<idServiziWithPortType.size();j++){
						inUsoMessage.append("- "+idServiziWithPortType.get(j).toString()+newLine);
					}
					continue;
					*/
					
				}
				
			}
			
			
			// effettuo eliminazione
			for (int j = 0; j < pt.sizeAzioneList(); j++) {
				Operation op = pt.getAzione(j);
				if (nomeop.equals(op.getNome())) {
					pt.removeAzione(j);
					break;
				}
			}
		}

		// effettuo le operazioni
		apcCore.performUpdateOperation(userLogin, apcHelper.smista(), pt);
		
	}
	
	@SuppressWarnings("incomplete-switch")
	public static void updateAccordoServizioParteComuneAllegati(AccordoServizioParteComune as,Documento doc, Documento docNew) {
		switch (RuoliDocumento.valueOf(doc.getRuolo())) {
			case allegato:
				//rimuovo il vecchio doc dalla lista
				for (int i = 0; i < as.sizeAllegatoList(); i++) {
					Documento documento = as.getAllegato(i);						
					if(documento.getId().equals(doc.getId()))
						as.removeAllegato(i);
				}
				//aggiungo il nuovo
				as.addAllegato(docNew);
				
				break;
	
			case specificaSemiformale:
				
				for (int i = 0; i < as.sizeSpecificaSemiformaleList(); i++) {
					Documento documento = as.getSpecificaSemiformale(i);						
					if(documento.getId().equals(doc.getId())){
						as.removeSpecificaSemiformale(i);
						break;
					}
				}
				//aggiungo il nuovo
				as.addSpecificaSemiformale(docNew);
				break;
				
			case specificaCoordinamento:
				AccordoServizioParteComuneServizioComposto assc = as.getServizioComposto();
				for (int i = 0; i < assc.sizeSpecificaCoordinamentoList(); i++) {
					Documento documento = assc.getSpecificaCoordinamento(i);						
					if(documento.getId().equals(doc.getId())){
						assc.removeSpecificaCoordinamento(i);
						break;
					}
				}
				assc.addSpecificaCoordinamento(docNew);
				as.setServizioComposto(assc);
				break;
		}
	}
	
	public static void deleteAccordoServizioParteComuneAllegati(AccordoServizioParteComune as, String userLogin, 
			AccordiServizioParteComuneCore apcCore, AccordiServizioParteComuneHelper apcHelper, 
			List<Long> idAllegati) throws Exception {
		
		ArchiviCore archiviCore = new ArchiviCore(apcCore);
		
		for (int i = 0; i < idAllegati.size(); i++) {
			long idAllegato = idAllegati.get(i);
			
			Documento doc = archiviCore.getDocumento(idAllegato, false);
			
			switch (RuoliDocumento.valueOf(doc.getRuolo())) {
				case allegato:
					//rimuovo il vecchio doc dalla lista
					for (int j = 0; j < as.sizeAllegatoList(); j++) {
						Documento documento = as.getAllegato(j);						
						if(documento.getFile().equals(doc.getFile()))
							as.removeAllegato(j);
					}
					
					break;

				case specificaSemiformale:
					
					for (int j = 0; j < as.sizeSpecificaSemiformaleList(); j++) {
						Documento documento = as.getSpecificaSemiformale(j);						
						if(documento.getFile().equals(doc.getFile()))
							as.removeSpecificaSemiformale(j);
					}
					break;
				
				case specificaCoordinamento:
					if(as.getServizioComposto()!=null){
						for (int j = 0; j < as.getServizioComposto().sizeSpecificaCoordinamentoList(); j++) {
							Documento documento = as.getServizioComposto().getSpecificaCoordinamento(j);						
							if(documento.getFile().equals(doc.getFile()))
								as.getServizioComposto().removeSpecificaCoordinamento(j);
						}
					}
					break;
				case specificaLivelloServizio:
					break;
				case specificaSicurezza:
					break;
			}
			
		}

		// effettuo le operazioni
		apcCore.performUpdateOperation(userLogin, apcHelper.smista(), as);
		
	}
	
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
	public static Parameter getParametroAccordoServizio(AccordoServizioParteComuneSintetico apc){
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

	public static List<AccordoServizioParteComuneSintetico> accordiList(AccordiServizioParteComuneCore core,String userLogin,Search ricerca,String tipoAccordo) throws DriverRegistroServiziException{
		List<AccordoServizioParteComuneSintetico> lista = null;
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

	public static List<AccordoServizioParteComuneSintetico> accordiListFromPermessiUtente(AccordiServizioParteComuneCore core,String userLogin,Search ricerca,boolean[] permessiUtente) throws DriverRegistroServiziException{
		List<AccordoServizioParteComuneSintetico> lista = null;
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

	public static void mapppingAutomaticoInterfaccia(AccordoServizioParteComune as, AccordiServizioParteComuneCore apcCore,
			boolean enableAutoMapping, boolean validazioneDocumenti, boolean enableAutoMapping_estraiXsdSchemiFromWsdlTypes, boolean facilityUnicoWSDL_interfacciaStandard,
			String tipoProtocollo, 
			InterfaceType interfaceType) throws Exception{
		if(as.getByteWsdlConcettuale() != null || as.getByteWsdlLogicoErogatore() != null || as.getByteWsdlLogicoFruitore() != null) {
			apcCore.mappingAutomatico(tipoProtocollo, as, validazioneDocumenti);
			if(enableAutoMapping_estraiXsdSchemiFromWsdlTypes && InterfaceType.WSDL_11.equals(interfaceType)){
				Hashtable<String, byte[]> schemiAggiuntiInQuestaOperazione = new Hashtable<String, byte[]>();
				if(as.getByteWsdlConcettuale() != null){ 
					apcCore.estraiSchemiFromWSDLTypesAsAllegati(as, as.getByteWsdlConcettuale(),AccordiServizioParteComuneCostanti.TIPO_WSDL_CONCETTUALE, schemiAggiuntiInQuestaOperazione);
				}
				if(facilityUnicoWSDL_interfacciaStandard){
					// è stato utilizzato il concettuale. Lo riporto nel logico
					if(as.getByteWsdlConcettuale()!=null){
						as.setByteWsdlLogicoErogatore(as.getByteWsdlConcettuale());
					}
				}
				else{
					if(as.getByteWsdlLogicoErogatore() != null){
						apcCore.estraiSchemiFromWSDLTypesAsAllegati(as, as.getByteWsdlLogicoErogatore(),AccordiServizioParteComuneCostanti.TIPO_WSDL_EROGATORE, schemiAggiuntiInQuestaOperazione);
					}
					if(as.getByteWsdlLogicoFruitore() != null){
						apcCore.estraiSchemiFromWSDLTypesAsAllegati(as, as.getByteWsdlLogicoFruitore(),AccordiServizioParteComuneCostanti.TIPO_WSDL_FRUITORE, schemiAggiuntiInQuestaOperazione);
					}
				}
			}
			try{
				// Se ho fatto il mapping controllo la validita' di quanto prodotto
				as.setStatoPackage(StatiAccordo.operativo.toString());
				boolean utilizzoAzioniDiretteInAccordoAbilitato = apcCore.isShowAccordiColonnaAzioni();
				apcCore.validaStatoAccordoServizio(as, utilizzoAzioniDiretteInAccordoAbilitato, false);
			}catch(ValidazioneStatoPackageException validazioneException){
				// Se l'automapping non ha prodotto ne porttype ne operatin rimetto lo stato a bozza
				as.setStatoPackage(StatiAccordo.bozza.toString());
			}
		}
	}
	
	public static boolean createPortTypeOperation(boolean enableAutoMapping, AccordiServizioParteComuneCore apcCore, AccordiServizioParteComuneHelper apcHelper,
			AccordoServizioParteComune as, PortType pt, String userLogin) throws Exception{
		boolean updateAccordo = false;
		try{
			if(enableAutoMapping) {
				if(StatiAccordo.bozza.toString().equals(as.getStatoPackage())) {
					// Se ho aggiunto la prima operazione
					if(pt.sizeAzioneList()==1) {
						as.setStatoPackage(StatiAccordo.operativo.toString());
						boolean utilizzoAzioniDiretteInAccordoAbilitato = apcCore.isShowAccordiColonnaAzioni();
						apcCore.validaStatoAccordoServizio(as, utilizzoAzioniDiretteInAccordoAbilitato, false);
						updateAccordo = true;
					}
				}
			}
		}catch(ValidazioneStatoPackageException validazioneException){
		}
		
		if(updateAccordo) {			
			// effettuo le operazioni
			apcCore.performUpdateOperation(userLogin, apcHelper.smista(), as);			
		}		
		else {				
			// effettuo le operazioni
			apcCore.performUpdateOperation(userLogin, apcHelper.smista(), pt);				
		}
		return updateAccordo;
	}
	
	public static void createResource(boolean enableAutoMapping, AccordiServizioParteComuneCore apcCore, AccordiServizioParteComuneHelper apcHelper,
			AccordoServizioParteComune as, String userLogin) throws Exception{
	
		try{
			if(enableAutoMapping) {
				if(StatiAccordo.bozza.toString().equals(as.getStatoPackage())) {
					// Se ho aggiunto la prima risorsa
					if(as.sizeResourceList()==1) {
						as.setStatoPackage(StatiAccordo.operativo.toString());
						boolean utilizzoAzioniDiretteInAccordoAbilitato = apcCore.isShowAccordiColonnaAzioni();
						apcCore.validaStatoAccordoServizio(as, utilizzoAzioniDiretteInAccordoAbilitato, false);
					}
				}
			}
		}catch(ValidazioneStatoPackageException validazioneException){
			// Se l'automapping non ha prodotto ne porttype ne operatin rimetto lo stato a bozza
			as.setStatoPackage(StatiAccordo.bozza.toString());
		}
		
		// effettuo le operazioni
		apcCore.performUpdateOperation(userLogin, apcHelper.smista(), as);
		
	}
	
	public static void updateInterfacciaAccordoServizioParteComune(String tipoParam, String wsdlParam, AccordoServizioParteComune as,
			boolean enableAutoMapping, boolean validazioneDocumenti, boolean enableAutoMapping_estraiXsdSchemiFromWsdlTypes, boolean facilityUnicoWSDL_interfacciaStandard,
			String tipoProtocollo, 
			AccordiServizioParteComuneCore apcCore) throws Exception {
		// il wsdl definitorio rimane fuori dal nuovo comportamento quindi il flusso della pagina continua come prima
		if (tipoParam.equals(AccordiServizioParteComuneCostanti.PARAMETRO_APC_WSDL_DEFINITORIO)) {
			as.setByteWsdlDefinitorio(wsdlParam.getBytes());
		}
		else {
			// se sono state definiti dei port type ed e' la prima volta che ho passato i controlli 
			//Informo l'utente che potrebbe sovrascrivere i servizi definiti tramite l'aggiornamento del wsdl
			// Questa Modalita' e' controllata tramite la proprieta' isenabledAutoMappingWsdlIntoAccordo
			// e se non e' un reset
			if(enableAutoMapping && (wsdlParam != null) && !wsdlParam.trim().replaceAll("\n", "").equals("") ){
				
				if((wsdlParam != null) && !wsdlParam.trim().replaceAll("\n", "").equals("") ){
					AccordoServizioParteComune asNuovo = new AccordoServizioParteComune();
					asNuovo.setFormatoSpecifica(as.getFormatoSpecifica());
					asNuovo.setServiceBinding(as.getServiceBinding());

					boolean fillXsd = false;
					String tipo = null;
					
					// decodifico quale wsdl/wsbl sto aggiornando
					if (tipoParam.equals(AccordiServizioParteComuneCostanti.PARAMETRO_APC_WSDL_CONCETTUALE)) {
						as.setByteWsdlConcettuale(wsdlParam.getBytes());

						asNuovo.setByteSpecificaConversazioneConcettuale(as.getByteSpecificaConversazioneConcettuale());
						asNuovo.setByteSpecificaConversazioneErogatore(as.getByteSpecificaConversazioneErogatore());
						asNuovo.setByteSpecificaConversazioneFruitore(as.getByteSpecificaConversazioneFruitore());
						asNuovo.setByteWsdlConcettuale(wsdlParam.getBytes());
						
						fillXsd = true;
						tipo=AccordiServizioParteComuneCostanti.TIPO_WSDL_CONCETTUALE;
					}
					if (tipoParam.equals(AccordiServizioParteComuneCostanti.PARAMETRO_APC_WSDL_EROGATORE)) {
						as.setByteWsdlLogicoErogatore(wsdlParam.getBytes());

						asNuovo.setByteSpecificaConversazioneConcettuale(as.getByteSpecificaConversazioneConcettuale());
						asNuovo.setByteSpecificaConversazioneErogatore(as.getByteSpecificaConversazioneErogatore());
						asNuovo.setByteSpecificaConversazioneFruitore(as.getByteSpecificaConversazioneFruitore());
						asNuovo.setByteWsdlLogicoErogatore(wsdlParam.getBytes());
						
						fillXsd = true;
						if(facilityUnicoWSDL_interfacciaStandard){
							tipo=AccordiServizioParteComuneCostanti.TIPO_WSDL_CONCETTUALE;
						}
						else{
							tipo=AccordiServizioParteComuneCostanti.TIPO_WSDL_EROGATORE;
						}
						
					}
					if (tipoParam.equals(AccordiServizioParteComuneCostanti.PARAMETRO_APC_WSDL_FRUITORE)) {
						as.setByteWsdlLogicoFruitore(wsdlParam.getBytes());

						asNuovo.setByteSpecificaConversazioneConcettuale(as.getByteSpecificaConversazioneConcettuale());
						asNuovo.setByteSpecificaConversazioneErogatore(as.getByteSpecificaConversazioneErogatore());
						asNuovo.setByteSpecificaConversazioneFruitore(as.getByteSpecificaConversazioneFruitore());
						asNuovo.setByteWsdlLogicoFruitore(wsdlParam.getBytes());
						
						fillXsd = true;
						tipo=AccordiServizioParteComuneCostanti.TIPO_WSDL_FRUITORE;
					}
					if (tipoParam.equals(AccordiServizioParteComuneCostanti.PARAMETRO_APC_SPECIFICA_CONVERSAZIONE_CONCETTUALE)) {
						as.setByteSpecificaConversazioneConcettuale(wsdlParam.getBytes());

						asNuovo.setByteSpecificaConversazioneConcettuale(wsdlParam.getBytes());
						asNuovo.setByteWsdlConcettuale(as.getByteWsdlConcettuale());
						asNuovo.setByteWsdlLogicoErogatore(as.getByteWsdlLogicoErogatore());
						asNuovo.setByteWsdlLogicoFruitore(as.getByteWsdlLogicoFruitore());
					}
					if (tipoParam.equals(AccordiServizioParteComuneCostanti.PARAMETRO_APC_SPECIFICA_CONVERSAZIONE_EROGATORE)) {
						as.setByteSpecificaConversazioneErogatore(wsdlParam.getBytes());

						asNuovo.setByteSpecificaConversazioneErogatore(wsdlParam.getBytes());
						asNuovo.setByteWsdlConcettuale(as.getByteWsdlConcettuale());
						asNuovo.setByteWsdlLogicoErogatore(as.getByteWsdlLogicoErogatore());
						asNuovo.setByteWsdlLogicoFruitore(as.getByteWsdlLogicoFruitore());
					}
					if (tipoParam.equals(AccordiServizioParteComuneCostanti.PARAMETRO_APC_SPECIFICA_CONVERSAZIONE_FRUITORE)) {
						as.setByteSpecificaConversazioneFruitore(wsdlParam.getBytes());

						asNuovo.setByteSpecificaConversazioneFruitore(wsdlParam.getBytes());
						asNuovo.setByteWsdlConcettuale(as.getByteWsdlConcettuale());
						asNuovo.setByteWsdlLogicoErogatore(as.getByteWsdlLogicoErogatore());
						asNuovo.setByteWsdlLogicoFruitore(as.getByteWsdlLogicoFruitore());
					}

					// Genero la nuova definizione
					apcCore.mappingAutomatico(tipoProtocollo, asNuovo, validazioneDocumenti);

					// se l'aggiornamento ha creato nuovi oggetti o aggiornato i vecchi aggiorno la configurazione
					if(org.openspcoop2.core.registry.constants.ServiceBinding.REST.equals(as.getServiceBinding())) {
						apcCore.popolaResourceDaUnAltroASPC(as,asNuovo);
					}
					else {
						apcCore.popolaPorttypeOperationDaUnAltroASPC(as,asNuovo);
					}
					
					// popolo gli allegati
					if(fillXsd && enableAutoMapping_estraiXsdSchemiFromWsdlTypes && FormatoSpecifica.WSDL_11.equals(as.getFormatoSpecifica())){
						apcCore.estraiSchemiFromWSDLTypesAsAllegati(as, wsdlParam.getBytes(), tipo, new Hashtable<String, byte[]> ());
						if(facilityUnicoWSDL_interfacciaStandard){
							// è stato utilizzato il concettuale. Lo riporto nel logico
							as.setByteWsdlLogicoErogatore(as.getByteWsdlConcettuale());
						}
					}
					
					try{
						if(StatiAccordo.bozza.toString().equals(as.getStatoPackage())) {
							// Se ho fatto il mapping controllo la validita' di quanto prodotto
							as.setStatoPackage(StatiAccordo.operativo.toString());
							boolean utilizzoAzioniDiretteInAccordoAbilitato = apcCore.isShowAccordiColonnaAzioni();
							apcCore.validaStatoAccordoServizio(as, utilizzoAzioniDiretteInAccordoAbilitato, false);
						}
					}catch(ValidazioneStatoPackageException validazioneException){
						// Se l'automapping non ha prodotto ne porttype ne operatin rimetto lo stato a bozza
						as.setStatoPackage(StatiAccordo.bozza.toString());
					}
					
				}
			}else {
				// vecchio comportamento sovrascrivo i wsdl
				// Modifico i dati del wsdl dell'accordo nel db
				// anche in caso di reset del wsdl

				if (tipoParam.equals(AccordiServizioParteComuneCostanti.PARAMETRO_APC_WSDL_CONCETTUALE)) {
					as.setByteWsdlConcettuale(wsdlParam.getBytes());
				}
				if (tipoParam.equals(AccordiServizioParteComuneCostanti.PARAMETRO_APC_WSDL_EROGATORE)) {
					as.setByteWsdlLogicoErogatore(wsdlParam.getBytes());
				}
				if (tipoParam.equals(AccordiServizioParteComuneCostanti.PARAMETRO_APC_WSDL_FRUITORE)) {
					as.setByteWsdlLogicoFruitore(wsdlParam.getBytes());
				}
				if (tipoParam.equals(AccordiServizioParteComuneCostanti.PARAMETRO_APC_SPECIFICA_CONVERSAZIONE_CONCETTUALE)) {
					as.setByteSpecificaConversazioneConcettuale(wsdlParam.getBytes());
				}
				if (tipoParam.equals(AccordiServizioParteComuneCostanti.PARAMETRO_APC_SPECIFICA_CONVERSAZIONE_EROGATORE)) {
					as.setByteSpecificaConversazioneErogatore(wsdlParam.getBytes());
				}
				if (tipoParam.equals(AccordiServizioParteComuneCostanti.PARAMETRO_APC_SPECIFICA_CONVERSAZIONE_FRUITORE)) {
					as.setByteSpecificaConversazioneFruitore(wsdlParam.getBytes());
				}

			} 
		}

		// Se un utente ha impostato solo il logico erogatore (avviene automaticamente nel caso non venga visualizzato il campo concettuale)
		// imposto lo stesso wsdl anche per il concettuale. Tanto Rappresenta la stessa informazione, ma e' utile per lo stato dell'accordo
		if(as.getByteWsdlLogicoErogatore()!=null && as.getByteWsdlLogicoFruitore()==null && as.getByteWsdlConcettuale()==null){
			as.setByteWsdlConcettuale(as.getByteWsdlLogicoErogatore());
		}
	}
}
