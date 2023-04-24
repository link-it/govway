/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2023 Link.it srl (https://link.it).
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

package org.openspcoop2.protocol.engine.utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.openspcoop2.core.config.driver.DriverConfigurazioneException;
import org.openspcoop2.core.constants.CostantiDB;
import org.openspcoop2.core.id.IDServizio;
import org.openspcoop2.core.registry.AccordoServizioParteComune;
import org.openspcoop2.core.registry.AccordoServizioParteSpecifica;
import org.openspcoop2.core.registry.Operation;
import org.openspcoop2.core.registry.PortType;
import org.openspcoop2.core.registry.ProtocolProperty;
import org.openspcoop2.core.registry.Resource;
import org.openspcoop2.core.registry.beans.AccordoServizioParteComuneSintetico;
import org.openspcoop2.core.registry.beans.PortTypeSintetico;
import org.openspcoop2.core.registry.driver.IDServizioFactory;
import org.slf4j.Logger;


/**
 * AzioniUtils
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class AzioniUtils {

	public static List<String> getAzioni(AccordoServizioParteSpecifica asps,AccordoServizioParteComuneSintetico aspc, 
			boolean addTrattinoSelezioneNonEffettuata, boolean throwException, List<String> filtraAzioniUtilizzate,
			String DEFAULT_VALUE_AZIONE_RISORSA_NON_SELEZIONATA, Logger log) throws DriverConfigurazioneException{
		String nomeMetodo = "getAzioni";
		try {
			// Prendo le azioni associate al servizio
			List<String> azioniList = null;
			try {
				if(aspc!=null) {
					org.openspcoop2.core.registry.constants.ServiceBinding sb = aspc.getServiceBinding();
					switch (sb) {
					case SOAP:
						if (asps != null) {
							
							IDServizio idServizio = IDServizioFactory.getInstance().getIDServizioFromAccordo(asps);
							
							if(asps.getPortType()!=null){
								// Bisogna prendere le operations del port type
								PortTypeSintetico pt = null;
								for (int i = 0; i < aspc.getPortType().size(); i++) {
									if(aspc.getPortType().get(i).getNome().equals(asps.getPortType())){
										pt = aspc.getPortType().get(i);
										break;
									}
								}
								if(pt==null){
									throw new Exception("Servizio ["+idServizio.toString()+"] possiede il port type ["+asps.getPortType()+"] che non risulta essere registrato nell'accordo di servizio ["+asps.getAccordoServizioParteComune()+"]");
								}
								if(pt.getAzione().size()>0){
									azioniList = new ArrayList<>();
									for (int i = 0; i < pt.getAzione().size(); i++) {
										if(filtraAzioniUtilizzate==null || !filtraAzioniUtilizzate.contains(pt.getAzione().get(i).getNome())) {
											azioniList.add(pt.getAzione().get(i).getNome());
										}
									}
								}
							}else{
								if(aspc.getAzione().size()>0){
									azioniList = new ArrayList<>();
									for (int i = 0; i < aspc.getAzione().size(); i++) {
										if(filtraAzioniUtilizzate==null || !filtraAzioniUtilizzate.contains(aspc.getAzione().get(i).getNome())) {
											azioniList.add(aspc.getAzione().get(i).getNome());
										}
									}
								}
							}				
						}
						break;

					case REST:
						if(aspc.getResource().size()>0){
							azioniList = new ArrayList<>();
							for (int i = 0; i < aspc.getResource().size(); i++) {
								if(filtraAzioniUtilizzate==null || !filtraAzioniUtilizzate.contains(aspc.getResource().get(i).getNome())) {
									azioniList.add(aspc.getResource().get(i).getNome());
								}
							}
						}
						break;
					}
					
				}
			} catch (Exception e) {
				if(throwException) {
					throw e;
				}
			}
			
			List<String> azioniListReturn = null;
			if(azioniList!=null && azioniList.size()>0) {
				Collections.sort(azioniList);
				
				azioniListReturn = new ArrayList<>();
				if(addTrattinoSelezioneNonEffettuata) {
					azioniListReturn.add(DEFAULT_VALUE_AZIONE_RISORSA_NON_SELEZIONATA);
				}
				azioniListReturn.addAll(azioniList);
			}
				
			return azioniListReturn;
			
		} catch (Exception e) {
			log.error("["+nomeMetodo + "] Exception :" + e.getMessage(), e);
			throw new DriverConfigurazioneException("[" + nomeMetodo + "] Error :" + e.getMessage(),e);
		}
		
	}
	
	public static Map<String,String> getMapAzioni(AccordoServizioParteSpecifica asps,AccordoServizioParteComuneSintetico aspc, 
			boolean addTrattinoSelezioneNonEffettuata, boolean throwException, List<String> filtraAzioniUtilizzate, 
			boolean sortByLabel, boolean sortFirstByPath, // per soap questi due parametri sono  ininfluenti
			String DEFAULT_VALUE_AZIONE_RISORSA_NON_SELEZIONATA, String DEFAULT_LABEL_AZIONE_RISORSA_NON_SELEZIONATA, Logger log
			) throws DriverConfigurazioneException{
		String nomeMetodo = "getAzioni";
		try {
			// Prendo le azioni associate al servizio
			Map<String,String> azioniMap = null; // <id,label>
			List<String> sortList = null;
			Map<String,String> sortMap = null; // <sort,id>
			try {
				if(aspc!=null) {
					org.openspcoop2.core.registry.constants.ServiceBinding sb = aspc.getServiceBinding();
					switch (sb) {
					case SOAP:
						if (asps != null) {
							
							IDServizio idServizio = IDServizioFactory.getInstance().getIDServizioFromAccordo(asps);
							
							if(asps.getPortType()!=null){
								// Bisogna prendere le operations del port type
								PortTypeSintetico pt = null;
								for (int i = 0; i < aspc.getPortType().size(); i++) {
									if(aspc.getPortType().get(i).getNome().equals(asps.getPortType())){
										pt = aspc.getPortType().get(i);
										break;
									}
								}
								if(pt==null){
									throw new Exception("Servizio ["+idServizio.toString()+"] possiede il port type ["+asps.getPortType()+"] che non risulta essere registrato nell'accordo di servizio ["+asps.getAccordoServizioParteComune()+"]");
								}
								if(pt.getAzione().size()>0){
									azioniMap = new HashMap<>();
									sortList = new ArrayList<>();
									for (int i = 0; i < pt.getAzione().size(); i++) {
										if(filtraAzioniUtilizzate==null || !filtraAzioniUtilizzate.contains(pt.getAzione().get(i).getNome())) {
											sortList.add(pt.getAzione().get(i).getNome());
											azioniMap.put(pt.getAzione().get(i).getNome(),pt.getAzione().get(i).getNome());
										}
									}
								}
							}else{
								if(aspc.getAzione().size()>0){
									azioniMap = new HashMap<>();
									sortList = new ArrayList<>();
									for (int i = 0; i < aspc.getAzione().size(); i++) {
										if(filtraAzioniUtilizzate==null || !filtraAzioniUtilizzate.contains(aspc.getAzione().get(i).getNome())) {
											sortList.add(aspc.getAzione().get(i).getNome());
											azioniMap.put(aspc.getAzione().get(i).getNome(),aspc.getAzione().get(i).getNome());
										}
									}
								}
							}				
						}
						break;

					case REST:
						if(aspc.getResource().size()>0){
							azioniMap = new HashMap<>();
							sortList = new ArrayList<>();
							if(sortByLabel) {
								sortMap = new HashMap<>();
							}
							for (int i = 0; i < aspc.getResource().size(); i++) {
								if(filtraAzioniUtilizzate==null || !filtraAzioniUtilizzate.contains(aspc.getResource().get(i).getNome())) {
									if(sortByLabel) {
										String sortLabelId = null;
										if(!sortFirstByPath) {
											sortLabelId = NamingUtils.getLabelResource(aspc.getResource().get(i));
										}
										else {
											String path = aspc.getResource().get(i).getPath()!=null ? aspc.getResource().get(i).getPath() : CostantiDB.API_RESOURCE_PATH_ALL_VALUE;
											String method = aspc.getResource().get(i).getMethod()!=null ? aspc.getResource().get(i).getMethod().getValue() : CostantiDB.API_RESOURCE_HTTP_METHOD_ALL_VALUE;
											sortLabelId = path+" "+method;
										}
										sortList.add(sortLabelId);
										sortMap.put(sortLabelId, aspc.getResource().get(i).getNome());
									}
									else {
										sortList.add(aspc.getResource().get(i).getNome());
									}
									azioniMap.put(aspc.getResource().get(i).getNome(),NamingUtils.getLabelResource(aspc.getResource().get(i)));
								}
							}
						}
						break;
					}
					
				}
			} catch (Exception e) {
				if(throwException) {
					throw e;
				}
			}
			
			Map<String, String> mapAzioniReturn = new LinkedHashMap<String, String>();
			if(sortList!=null && sortList.size()>0) {
				Collections.sort(sortList);
				
				if(addTrattinoSelezioneNonEffettuata) {
					mapAzioniReturn.put(DEFAULT_VALUE_AZIONE_RISORSA_NON_SELEZIONATA,DEFAULT_LABEL_AZIONE_RISORSA_NON_SELEZIONATA);
				}
				
				if(sortMap!=null) {
					for (String idSort : sortList) { 
						String id  = sortMap.get(idSort);
						mapAzioniReturn.put(id, azioniMap.get(id));
					}
				}
				else {
					for (String id : sortList) { // nelle sortList ci sono gli id
						mapAzioniReturn.put(id, azioniMap.get(id));
					}
				}
			}
				
			return mapAzioniReturn;
			
		} catch (Exception e) {
			log.error("[ControlStationCore::" + nomeMetodo + "] Exception :" + e.getMessage(), e);
			throw new DriverConfigurazioneException("[ControlStationCore::" + nomeMetodo + "] Error :" + e.getMessage(),e);
		}
	}
	
	public static Map<String,String> getAzioniConLabel(AccordoServizioParteSpecifica asps,AccordoServizioParteComuneSintetico aspc, 
			boolean addTrattinoSelezioneNonEffettuata, boolean throwException, List<String> filtraAzioniUtilizzate,
			String DEFAULT_VALUE_AZIONE_RISORSA_NON_SELEZIONATA, String DEFAULT_LABEL_AZIONE_RISORSA_NON_SELEZIONATA, Logger log) throws Exception{
		Map<String,String> mapAzioni = getMapAzioni(asps, aspc, addTrattinoSelezioneNonEffettuata, throwException, filtraAzioniUtilizzate,
				true, true,
				DEFAULT_VALUE_AZIONE_RISORSA_NON_SELEZIONATA, DEFAULT_LABEL_AZIONE_RISORSA_NON_SELEZIONATA, log);
		return mapAzioni;
	}
	
	
	
	public static String getProtocolPropertyStringValue(AccordoServizioParteComune aspc, String idPortType, String idAzione, String propertyName) {
		if(idPortType!=null) {
			for (PortType pt : aspc.getPortTypeList()) {
				if(pt.getNome().equals(idPortType)) {
					for (Operation op : pt.getAzioneList()) {
						if(op.getNome().equals(idAzione)) {
							for (ProtocolProperty pp : op.getProtocolPropertyList()) {
								if(pp.getName().equals(propertyName)) {
									return pp.getValue();
								}
							}
							break;
						}
					}
					break;
				}
			}
		}
		else {
			for (Resource resource : aspc.getResourceList()) {
				if(resource.getNome().equals(idAzione)) {
					for (ProtocolProperty pp : resource.getProtocolPropertyList()) {
						if(pp.getName().equals(propertyName)) {
							return pp.getValue();
						}
					}
					break;
				}
			}
		}
		return null;
	}
	
}
