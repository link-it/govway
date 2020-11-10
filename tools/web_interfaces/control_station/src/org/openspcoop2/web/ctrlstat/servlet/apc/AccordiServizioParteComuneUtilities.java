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
import java.util.Map;
import java.util.Vector;

import org.openspcoop2.core.commons.ErrorsHandlerCostant;
import org.openspcoop2.core.id.IDAccordo;
import org.openspcoop2.core.id.IDPortType;
import org.openspcoop2.core.id.IDPortTypeAzione;
import org.openspcoop2.core.id.IDResource;
import org.openspcoop2.core.registry.AccordoServizioParteComune;
import org.openspcoop2.core.registry.AccordoServizioParteComuneServizioComposto;
import org.openspcoop2.core.registry.AccordoServizioParteSpecifica;
import org.openspcoop2.core.registry.Documento;
import org.openspcoop2.core.registry.Operation;
import org.openspcoop2.core.registry.PortType;
import org.openspcoop2.core.registry.ProtocolProperty;
import org.openspcoop2.core.registry.Resource;
import org.openspcoop2.core.registry.beans.AccordoServizioParteComuneSintetico;
import org.openspcoop2.core.registry.constants.FormatoSpecifica;
import org.openspcoop2.core.registry.constants.RuoliDocumento;
import org.openspcoop2.core.registry.constants.StatiAccordo;
import org.openspcoop2.core.registry.driver.DriverRegistroServiziException;
import org.openspcoop2.core.registry.driver.DriverRegistroServiziNotFound;
import org.openspcoop2.core.registry.driver.FiltroRicercaOperations;
import org.openspcoop2.core.registry.driver.FiltroRicercaProtocolProperty;
import org.openspcoop2.core.registry.driver.FiltroRicercaResources;
import org.openspcoop2.core.registry.driver.IDAccordoFactory;
import org.openspcoop2.core.registry.driver.ValidazioneStatoPackageException;
import org.openspcoop2.core.registry.driver.db.IDAccordoDB;
import org.openspcoop2.protocol.engine.constants.Costanti;
import org.openspcoop2.protocol.engine.utils.DBOggettiInUsoUtils;
import org.openspcoop2.protocol.engine.utils.NamingUtils;
import org.openspcoop2.protocol.manifest.constants.InterfaceType;
import org.openspcoop2.web.ctrlstat.core.Search;
import org.openspcoop2.web.ctrlstat.servlet.aps.AccordiServizioParteSpecificaCore;
import org.openspcoop2.web.ctrlstat.servlet.archivi.ArchiviCore;
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
		
		IDAccordoFactory idAccordoFactory = IDAccordoFactory.getInstance();
		
		String newURI = idAccordoFactory.getUriFromAccordo(as);

		// Cerco i servizi in cui devo cambiare la URI dell'accordo
		List<AccordoServizioParteSpecifica> servizi = apsCore.serviziByAccordoFilterList(idAccordoOLD);
		if(servizi!=null && servizi.size()>0){
			while(servizi.size()>0){
				AccordoServizioParteSpecifica s = servizi.remove(0);
				s.setAccordoServizioParteComune(newURI);
				listOggettiDaAggiornare.add(s);
			}
		}
		
		if(Costanti.MODIPA_PROTOCOL_NAME.equals(idAccordoOLD.getSoggettoReferente().getTipo())) {
			
			String oldURI = idAccordoFactory.getUriFromIDAccordo(idAccordoOLD);
			
			Map<String, AccordoServizioParteComune> map = new Hashtable<String, AccordoServizioParteComune>();
			
			if(org.openspcoop2.core.registry.constants.ServiceBinding.REST.equals(as.getServiceBinding())) {
				FiltroRicercaResources filtroRicerca = new FiltroRicercaResources();
				FiltroRicercaProtocolProperty filtroRPP = new FiltroRicercaProtocolProperty();
				filtroRPP.setName(org.openspcoop2.protocol.engine.constants.Costanti.MODIPA_PROFILO_INTERAZIONE_ASINCRONA_API_RICHIESTA_CORRELATA);
				filtroRPP.setValueAsString(oldURI);
				filtroRicerca.addProtocolPropertyResource(filtroRPP);
				List<IDResource> list = null;
				try {
					list = apcCore.getAllIdResource(filtroRicerca);
				}catch(DriverRegistroServiziNotFound notFound) {}
				if(list!=null && !list.isEmpty()) {
					for (IDResource idResource : list) {
						
						AccordoServizioParteComune aspc = null;
						String uri = idAccordoFactory.getUriFromIDAccordo(idResource.getIdAccordo());
						boolean forceAdd = false;
						if(map.containsKey(uri)) {
							aspc = map.remove(uri);
							forceAdd = true;
						}
						else {
							if(idResource.getIdAccordo().equals(idAccordoOLD)) {
								// è quello che sto modificando
								aspc = as;
							}
							else {
								aspc = apcCore.getAccordoServizioFull(idResource.getIdAccordo());
							}
						}
						boolean find = false;
						if(aspc.sizeResourceList()>0) {
							for (Resource res : aspc.getResourceList()) {
								if(res.getNome().equals(idResource.getNome())) {
									if(res.sizeProtocolPropertyList()>0) {
										for (ProtocolProperty pp : res.getProtocolPropertyList()) {
											if(org.openspcoop2.protocol.engine.constants.Costanti.MODIPA_PROFILO_INTERAZIONE_ASINCRONA_API_RICHIESTA_CORRELATA.equals(pp.getName())) {
												pp.setValue(newURI);
												find = true;
												break;
											}
										}
									}
									break;
								}
							}
						}
						if((!idResource.getIdAccordo().equals(idAccordoOLD)) && (forceAdd || find)) {
							map.put(uri, aspc);
						}
					}
				}
			}
			else {
				FiltroRicercaOperations filtroRicerca = new FiltroRicercaOperations();
				FiltroRicercaProtocolProperty filtroRPP = new FiltroRicercaProtocolProperty();
				filtroRPP.setName(org.openspcoop2.protocol.engine.constants.Costanti.MODIPA_PROFILO_INTERAZIONE_ASINCRONA_API_RICHIESTA_CORRELATA);
				filtroRPP.setValueAsString(oldURI);
				filtroRicerca.addProtocolPropertyAzione(filtroRPP);
				List<IDPortTypeAzione> list = null;
				try {
					list = apcCore.getAllIdOperation(filtroRicerca);
				}catch(DriverRegistroServiziNotFound notFound) {}
				if(list!=null && !list.isEmpty()) {
					for (IDPortTypeAzione idAzione : list) {
						
						AccordoServizioParteComune aspc = null;
						String uri = idAccordoFactory.getUriFromIDAccordo(idAzione.getIdPortType().getIdAccordo());
						boolean forceAdd = false;
						if(map.containsKey(uri)) {
							aspc = map.remove(uri);
							forceAdd = true;
						}
						else {
							if(idAzione.getIdPortType().getIdAccordo().equals(idAccordoOLD)) {
								// è quello che sto modificando
								aspc = as;
							}
							else {
								aspc = apcCore.getAccordoServizioFull(idAzione.getIdPortType().getIdAccordo());
							}
						}
						boolean find = false;
						if(aspc.sizePortTypeList()>0) {
							for (PortType portType : aspc.getPortTypeList()) {
								if(portType.getNome().contentEquals(idAzione.getIdPortType().getNome())) {
									if(portType.sizeAzioneList()>0) {
										for (Operation op : portType.getAzioneList()) {
											if(op.getNome().equals(idAzione.getNome())) {
												if(op.sizeProtocolPropertyList()>0) {
													for (ProtocolProperty pp : op.getProtocolPropertyList()) {
														if(org.openspcoop2.protocol.engine.constants.Costanti.MODIPA_PROFILO_INTERAZIONE_ASINCRONA_API_RICHIESTA_CORRELATA.equals(pp.getName())) {
															pp.setValue(newURI);
															find = true;
															break;
														}
													}
												}
												break;
											}
										}
									}
									break;
								}
							}
						}
						if((!idAzione.getIdPortType().getIdAccordo().equals(idAccordoOLD)) && (forceAdd || find)) {
							map.put(uri, aspc);
						}
						
					}
				}
			}
			
			if(!map.isEmpty()) {
				listOggettiDaAggiornare.addAll( map.values() );
			}
			
		}
	}
	
	public static void deleteAccordoServizioParteComune(AccordoServizioParteComune as, String userLogin, AccordiServizioParteComuneCore apcCore, AccordiServizioParteComuneHelper apcHelper, StringBuilder inUsoMessage, String newLine) throws Exception {
		
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
	
	public static boolean deleteResource(AccordoServizioParteComune as, IDResource idResource, String userLogin, AccordiServizioParteComuneCore apcCore, AccordiServizioParteComuneHelper apcHelper, StringBuilder inUsoMessage, String newLine) throws Exception {
		HashMap<ErrorsHandlerCostant, List<String>> whereIsInUso = new HashMap<ErrorsHandlerCostant, List<String>>();
		boolean normalizeObjectIds = !apcHelper.isModalitaCompleta();
		boolean risorsaInUso = apcCore.isRisorsaInUso(idResource,whereIsInUso,normalizeObjectIds);
		
		boolean modificaAS_effettuata = false;
		
		if (risorsaInUso) {
			
			// traduco nomeRisorsa in path
			String methodPath = null;
			for (int j = 0; j < as.sizeResourceList(); j++) {
				Resource risorsa = as.getResource(j);
				if (idResource.getNome().equals(risorsa.getNome())) {
					methodPath = NamingUtils.getLabelResource(risorsa);
					break;
				}
			}
			if(methodPath==null) {
				methodPath = idResource.getNome();
			}
			
			inUsoMessage.append(DBOggettiInUsoUtils.toString(idResource, methodPath, whereIsInUso, true, newLine));
			inUsoMessage.append(newLine);

		} else {
			// Effettuo eliminazione
			for (int j = 0; j < as.sizeResourceList(); j++) {
				Resource risorsa = as.getResource(j);
				if (idResource.getNome().equals(risorsa.getNome())) {
					modificaAS_effettuata = true;
					as.removeResource(j);
					break;
				}
			}
		}
		
		return modificaAS_effettuata;
	}
	
	public static void deleteAccordoServizioParteComuneRisorse(AccordoServizioParteComune as, String userLogin, AccordiServizioParteComuneCore apcCore, AccordiServizioParteComuneHelper apcHelper, 
			StringBuilder inUsoMessage, String newLine, List<String> resourcesToRemove) throws Exception {

		boolean modificaAS_effettuata = false;
		
		IDAccordo idAccordo = IDAccordoFactory.getInstance().getIDAccordoFromAccordo(as);
				
		for (int i = 0; i < resourcesToRemove.size(); i++) {
			String nomeRisorsa = resourcesToRemove.get(i);
			IDResource idRisorsa = new IDResource();
			idRisorsa.setNome(nomeRisorsa);
			idRisorsa.setIdAccordo(idAccordo);
			
			boolean modificato = AccordiServizioParteComuneUtilities.deleteResource(as, idRisorsa, userLogin, apcCore, apcHelper, inUsoMessage, newLine);
			if(modificato) {
				modificaAS_effettuata = true;
			}
			
		}// chiudo for
		if(modificaAS_effettuata) {
			apcCore.performUpdateOperation(userLogin, apcHelper.smista(), as);
		}
	}
	
	public static boolean deletePortType(AccordoServizioParteComune as, IDPortType idPortType, String userLogin, AccordiServizioParteComuneCore apcCore, AccordiServizioParteComuneHelper apcHelper, StringBuilder inUsoMessage, String newLine) throws Exception {
		HashMap<ErrorsHandlerCostant, List<String>> whereIsInUso = new HashMap<ErrorsHandlerCostant, List<String>>();
		boolean normalizeObjectIds = !apcHelper.isModalitaCompleta();
		boolean portTypeInUso = apcCore.isPortTypeInUso(idPortType,whereIsInUso,normalizeObjectIds);
		
		boolean modificaAS_effettuata = false;
		
		if (portTypeInUso) {
			inUsoMessage.append(DBOggettiInUsoUtils.toString(idPortType, whereIsInUso, true, newLine));
			inUsoMessage.append(newLine);

		} else {
			// Effettuo eliminazione
			for (int j = 0; j < as.sizePortTypeList(); j++) {
				PortType pt = as.getPortType(j);
				if (idPortType.getNome().equals(pt.getNome())) {
					modificaAS_effettuata = true;
					as.removePortType(j);
					break;
				}
			}
		}
		
		return modificaAS_effettuata;
	}
	
	public static void deleteAccordoServizioParteComunePortTypes(AccordoServizioParteComune as, String userLogin, AccordiServizioParteComuneCore apcCore, AccordiServizioParteComuneHelper apcHelper, 
			StringBuilder inUsoMessage, String newLine, List<String> ptsToRemove) throws Exception {
		
		boolean modificaAS_effettuata = false;
		
		IDAccordo idAccordo = IDAccordoFactory.getInstance().getIDAccordoFromAccordo(as);
				
		for (int i = 0; i < ptsToRemove.size(); i++) {
			String nomePT = ptsToRemove.get(i);
			
			IDPortType idPT = new IDPortType();
			idPT.setNome(nomePT);
			idPT.setIdAccordo(idAccordo);
						
			boolean modificato = AccordiServizioParteComuneUtilities.deletePortType(as, idPT, userLogin, apcCore, apcHelper, inUsoMessage, newLine);
			if(modificato) {
				modificaAS_effettuata = true;
			}
			
		}// chiudo for
		if(modificaAS_effettuata) {
			apcCore.performUpdateOperation(userLogin, apcHelper.smista(), as);
		}
		
	}
	
	public static boolean deleteOperazione(PortType pt, IDPortTypeAzione idOperazione, String userLogin, AccordiServizioParteComuneCore apcCore, AccordiServizioParteComuneHelper apcHelper, StringBuilder inUsoMessage, String newLine) throws Exception {
		HashMap<ErrorsHandlerCostant, List<String>> whereIsInUso = new HashMap<ErrorsHandlerCostant, List<String>>();
		boolean normalizeObjectIds = !apcHelper.isModalitaCompleta();
		boolean operazioneInUso = apcCore.isOperazioneInUso(idOperazione,whereIsInUso,normalizeObjectIds);
		
		boolean modificaAS_effettuata = false;
		
		if (operazioneInUso) {
			inUsoMessage.append(DBOggettiInUsoUtils.toString(idOperazione, whereIsInUso, true, newLine));
			inUsoMessage.append(newLine);

		} else {
			// Effettuo eliminazione
			for (int j = 0; j < pt.sizeAzioneList(); j++) {
				Operation op = pt.getAzione(j);
				if (idOperazione.getNome().equals(op.getNome())) {
					modificaAS_effettuata = true;
					pt.removeAzione(j);
					break;
				}
			}
		}
		
		return modificaAS_effettuata;
	}
	
	
	public static void deleteAccordoServizioParteComuneOperations(AccordoServizioParteComune as, String userLogin, AccordiServizioParteComuneCore apcCore, AccordiServizioParteComuneHelper apcHelper, 
			StringBuilder inUsoMessage, String newLine, PortType pt, List<String> optsToRemove) throws Exception {
	
		boolean modificaAS_effettuata = false;
		
		IDAccordo idAccordo = IDAccordoFactory.getInstance().getIDAccordoFromAccordo(as);
		IDPortType idPortType = new IDPortType();
		idPortType.setNome(pt.getNome());
		idPortType.setIdAccordo(idAccordo);
		
		for (int i = 0; i < optsToRemove.size(); i++) {
			String nomeop = optsToRemove.get(i);
			
			IDPortTypeAzione idOperazione = new IDPortTypeAzione();
			idOperazione.setNome(nomeop);
			idOperazione.setIdPortType(idPortType);
			
			boolean modificato = AccordiServizioParteComuneUtilities.deleteOperazione(pt, idOperazione, userLogin, apcCore, apcHelper, inUsoMessage, newLine);
			if(modificato) {
				modificaAS_effettuata = true;
			}
			
		}// chiudo for
		if(modificaAS_effettuata) {
			apcCore.performUpdateOperation(userLogin, apcHelper.smista(), pt);
		}
				
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
	
	public static List<IDAccordoDB> idAccordiListFromPermessiUtente(AccordiServizioParteComuneCore core,String userLogin,Search ricerca,boolean[] permessiUtente, 
			boolean soloAccordiConsistentiRest, boolean soloAccordiConsistentiSoap) throws DriverRegistroServiziException{
		List<IDAccordoDB> lista = null;
		if(permessiUtente != null){
			if(permessiUtente[0] && !permessiUtente[1]){
				if(core.isVisioneOggettiGlobale(userLogin)){
					lista = core.idAccordiServizioParteComuneList(null, ricerca, 
							soloAccordiConsistentiRest, soloAccordiConsistentiSoap);
				}else{
					lista = core.idAccordiServizioParteComuneList(userLogin, ricerca, 
							soloAccordiConsistentiRest, soloAccordiConsistentiSoap);
				}

				return lista;
			}

			if(!permessiUtente[0] && permessiUtente[1]){
				if(core.isVisioneOggettiGlobale(userLogin)){
					lista = core.idAccordiServizioCompostiList(null, ricerca, 
							soloAccordiConsistentiRest, soloAccordiConsistentiSoap);
				}else{
					lista = core.idAccordiServizioCompostiList(userLogin, ricerca, 
							soloAccordiConsistentiRest, soloAccordiConsistentiSoap);
				}

				return lista;
			}
		}


		if(core.isVisioneOggettiGlobale(userLogin)){
			lista = core.idAccordiList(null, ricerca, 
					soloAccordiConsistentiRest, soloAccordiConsistentiSoap);
		}else{
			lista = core.idAccordiList(userLogin, ricerca, 
					soloAccordiConsistentiRest, soloAccordiConsistentiSoap);
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
			
			boolean calcolaLista = false;
			if(AccordiServizioParteComuneCostanti.INFORMAZIONI_PROTOCOLLO_MODALITA_RIDEFINITO.equals(profProtocollo) && profcollop.equals("asincronoAsimmetrico")) {
				calcolaLista = true;
			}
			else if(AccordiServizioParteComuneCostanti.INFORMAZIONI_PROTOCOLLO_MODALITA_DEFAULT.contentEquals(profProtocollo)) {
				if(AccordiServizioParteComuneCostanti.INFORMAZIONI_PROTOCOLLO_MODALITA_RIDEFINITO.equals(pt.getProfiloPT())) {
					if(pt.getProfiloCollaborazione()!=null) {
						calcolaLista = "asincronoAsimmetrico".equals(pt.getProfiloCollaborazione().getValue());
					}
				}
				else {
					if(as.getProfiloCollaborazione()!=null) {
						calcolaLista = "asincronoAsimmetrico".equals(as.getProfiloCollaborazione().getValue());
					}
				}
			}
			if (calcolaLista){
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
			AccordiServizioParteComuneCore apcCore,
			boolean aggiornaEsistenti, boolean eliminaNonPresentiNuovaInterfaccia,
			List<IDResource> risorseEliminate,List<IDPortType> portTypeEliminati, List<IDPortTypeAzione> operationEliminate) throws Exception {
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
						apcCore.popolaResourceDaUnAltroASPC(as,asNuovo,
								aggiornaEsistenti, eliminaNonPresentiNuovaInterfaccia, risorseEliminate);
					}
					else {
						apcCore.popolaPorttypeOperationDaUnAltroASPC(as,asNuovo,
								aggiornaEsistenti, eliminaNonPresentiNuovaInterfaccia,
								portTypeEliminati, operationEliminate);
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
