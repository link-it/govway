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

package org.openspcoop2.protocol.engine.utils;

import java.util.ArrayList;
import java.util.List;

import org.openspcoop2.core.id.IDAccordo;
import org.openspcoop2.core.id.IDAccordoCooperazione;
import org.openspcoop2.core.id.IDServizio;
import org.openspcoop2.core.id.IDServizioApplicativo;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.core.registry.AccordoCooperazione;
import org.openspcoop2.core.registry.AccordoServizioParteComune;
import org.openspcoop2.core.registry.AccordoServizioParteSpecifica;
import org.openspcoop2.core.registry.beans.AccordoServizioParteComuneSintetico;
import org.openspcoop2.core.registry.driver.IDAccordoCooperazioneFactory;
import org.openspcoop2.core.registry.driver.IDAccordoFactory;
import org.openspcoop2.core.registry.driver.IDServizioFactory;
import org.openspcoop2.protocol.engine.ProtocolFactoryManager;
import org.openspcoop2.protocol.sdk.IProtocolFactory;

/**
 * PorteNamingUtils
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class NamingUtils {

	
	// PROTOCOLLI
	
	public static List<String> getLabelsProtocolli(List<String> protocolli) throws Exception{
		if(protocolli==null || protocolli.size()<=0) {
			return null;
		}
		List<String> l = new ArrayList<>();
		for (String protocollo : protocolli) {
			l.add(NamingUtils.getLabelProtocollo(protocollo));
		}
		return l;
	}
	
	public static String getLabelProtocollo(String protocollo) throws Exception{
		ProtocolFactoryManager protocolFactoryManager = ProtocolFactoryManager.getInstance();
		return protocolFactoryManager.getProtocolFactoryByName(protocollo).getInformazioniProtocol().getLabel();
	}
	
	public static String getDescrizioneProtocollo(String protocollo) throws Exception{
		ProtocolFactoryManager protocolFactoryManager = ProtocolFactoryManager.getInstance();
		return protocolFactoryManager.getProtocolFactoryByName(protocollo).getInformazioniProtocol().getDescription();
	}
	
	public static String getWebSiteProtocollo(String protocollo) throws Exception{
		ProtocolFactoryManager protocolFactoryManager = ProtocolFactoryManager.getInstance();
		return protocolFactoryManager.getProtocolFactoryByName(protocollo).getInformazioniProtocol().getWebSite();
	}
	
	
	
	// SOGGETTI
	
	public static String getLabelSoggetto(IDSoggetto idSoggetto) throws Exception{
		ProtocolFactoryManager protocolFactoryManager = ProtocolFactoryManager.getInstance();
		String protocollo = protocolFactoryManager.getProtocolByOrganizationType(idSoggetto.getTipo());
		return getLabelSoggetto(protocollo,	idSoggetto.getTipo(), idSoggetto.getNome());
	}
	
	public static String getLabelSoggetto(String protocollo, IDSoggetto idSoggetto) throws Exception{
		return getLabelSoggetto(protocollo, idSoggetto.getTipo(), idSoggetto.getNome());
	}
	public static String getLabelSoggetto(String protocollo, String tipoSoggetto, String nomeSoggetto) throws Exception{
		StringBuilder bf = new StringBuilder();
		ProtocolFactoryManager protocolFactoryManager = ProtocolFactoryManager.getInstance();
		if(protocolFactoryManager.getOrganizationTypes().get(protocollo).size()>1) {
			IProtocolFactory<?> protocolFactory = protocolFactoryManager.getProtocolFactoryByName(protocollo);
			if(tipoSoggetto.equals(protocolFactory.createProtocolConfiguration().getTipoSoggettoDefault())) {
				bf.append(nomeSoggetto);
			}
			else{
				bf.append(tipoSoggetto).append("/").append(nomeSoggetto);
			}
		}
		else {
			bf.append(nomeSoggetto);
		}
		return bf.toString();
	}
	
	public static IDSoggetto getSoggettoFromLabel(String protocollo, String labelSoggetto) throws Exception{
		ProtocolFactoryManager protocolFactoryManager = ProtocolFactoryManager.getInstance();
		IProtocolFactory<?> protocolFactory = protocolFactoryManager.getProtocolFactoryByName(protocollo);
		String tipoSoggettoDefault = protocolFactory.createProtocolConfiguration().getTipoSoggettoDefault();
		String tipo = null;
		String nome = null;
		if(labelSoggetto.contains("/")) {
			String [] tmp = labelSoggetto.split("/");
			tipo = tmp[0];
			nome = tmp[1];
		}
		else {
			tipo = tipoSoggettoDefault;
			nome = labelSoggetto;
		}
		return new IDSoggetto(tipo, nome);
	}
	
	
	// APPLICATIVI
	
	public static String getLabelServizioApplicativo(IDServizioApplicativo idServizioApplicativo) throws Exception{
		ProtocolFactoryManager protocolFactoryManager = ProtocolFactoryManager.getInstance();
		String protocollo = protocolFactoryManager.getProtocolByOrganizationType(idServizioApplicativo.getIdSoggettoProprietario().getTipo());
		return getLabelServizioApplicativo(protocollo, idServizioApplicativo);
	}
	public static String getLabelServizioApplicativo(String protocollo, IDServizioApplicativo idServizioApplicativo) throws Exception{
		StringBuilder bf = new StringBuilder();
		bf.append(idServizioApplicativo.getNome());
		bf.append(" (");
		bf.append(getLabelSoggetto(protocollo, idServizioApplicativo.getIdSoggettoProprietario()));
		bf.append(")");
		return bf.toString();
	}
	
	
	
	// API
	
	public static String getLabelAccordoServizioParteComune(AccordoServizioParteComune as) throws Exception{
		ProtocolFactoryManager protocolFactoryManager = ProtocolFactoryManager.getInstance();
		String protocollo = protocolFactoryManager.getProtocolByOrganizationType(as.getSoggettoReferente().getTipo());
		return getLabelAccordoServizioParteComune(protocollo, IDAccordoFactory.getInstance().getIDAccordoFromAccordo(as));
	}
	public static String getLabelAccordoServizioParteComune(AccordoServizioParteComuneSintetico as) throws Exception{
		ProtocolFactoryManager protocolFactoryManager = ProtocolFactoryManager.getInstance();
		String protocollo = protocolFactoryManager.getProtocolByOrganizationType(as.getSoggettoReferente().getTipo());
		return getLabelAccordoServizioParteComune(protocollo, IDAccordoFactory.getInstance().getIDAccordoFromAccordo(as));
	}
	public static String getLabelAccordoServizioParteComune(String protocollo, AccordoServizioParteComune as) throws Exception{
		return getLabelAccordoServizioParteComune(protocollo, IDAccordoFactory.getInstance().getIDAccordoFromAccordo(as));
	}
	public static String getLabelAccordoServizioParteComune(IDAccordo idAccordo) throws Exception{
		ProtocolFactoryManager protocolFactoryManager = ProtocolFactoryManager.getInstance();
		String protocollo = protocolFactoryManager.getProtocolByOrganizationType(idAccordo.getSoggettoReferente().getTipo());
		return getLabelAccordoServizioParteComune(protocollo, idAccordo);
	}
	public static String getLabelAccordoServizioParteComune(String protocollo, IDAccordo idAccordo) throws Exception{
		return getLabelAccordoServizioParteComune(protocollo, idAccordo, true);
	}
	public static String getLabelAccordoServizioParteComune(String protocollo, IDAccordo idAccordo, boolean addSoggettoReferente) throws Exception{
		StringBuilder bf = new StringBuilder();
		bf.append(idAccordo.getNome());
		bf.append(" v");
		bf.append(idAccordo.getVersione());
		if(addSoggettoReferente) {
			ProtocolFactoryManager protocolFactoryManager = ProtocolFactoryManager.getInstance();
			boolean supportatoSoggettoReferente = protocolFactoryManager.getProtocolFactoryByName(protocollo).createProtocolConfiguration().isSupportoSoggettoReferenteAccordiParteComune();
			if(supportatoSoggettoReferente) {
				if(idAccordo.getSoggettoReferente()!=null){
					bf.append(" (");
					bf.append(getLabelSoggetto(protocollo, idAccordo.getSoggettoReferente().getTipo(), idAccordo.getSoggettoReferente().getNome()));
					bf.append(")");
				}
			}
		}
		return bf.toString();
	}
	
	// SERVIZI
	
	public static String getLabelAccordoServizioParteSpecificaSenzaErogatore(String protocollo, String tipoServizio, String nomeServizio, Integer versioneInt) throws Exception{
		
		String versione = "";
		if(ProtocolFactoryManager.getInstance().getProtocolFactoryByName(protocollo).createProtocolConfiguration().isSupportoVersionamentoAccordiParteSpecifica()) {
			versione = " v"+versioneInt;
		}
		
		ProtocolFactoryManager protocolFactoryManager = ProtocolFactoryManager.getInstance();
		if(protocolFactoryManager._getServiceTypes().get(protocollo).size()>1) {
			IProtocolFactory<?> protocolFactory = protocolFactoryManager.getProtocolFactoryByName(protocollo);
			if(tipoServizio.equals(protocolFactory.createProtocolConfiguration().getTipoServizioDefault(null))) {
				return nomeServizio+versione;
			}
			else {
				return tipoServizio+"/"+nomeServizio+versione;	
			}
		}
		else {
			return nomeServizio+versione;
		}
	}
	
	public static String getLabelAccordoServizioParteSpecifica(AccordoServizioParteSpecifica as) throws Exception{
		ProtocolFactoryManager protocolFactoryManager = ProtocolFactoryManager.getInstance();
		String protocollo = protocolFactoryManager.getProtocolByOrganizationType(as.getTipoSoggettoErogatore());
		return getLabelAccordoServizioParteSpecifica(protocollo,IDServizioFactory.getInstance().getIDServizioFromAccordo(as));
	}
	public static String getLabelAccordoServizioParteSpecifica(String protocollo, AccordoServizioParteSpecifica as) throws Exception{
		return getLabelAccordoServizioParteSpecifica(protocollo,IDServizioFactory.getInstance().getIDServizioFromAccordo(as));
	}
	public static String getLabelAccordoServizioParteSpecifica(IDServizio idServizio) throws Exception{
		ProtocolFactoryManager protocolFactoryManager = ProtocolFactoryManager.getInstance();
		String protocollo = protocolFactoryManager.getProtocolByOrganizationType(idServizio.getSoggettoErogatore().getTipo());
		return getLabelAccordoServizioParteSpecifica(protocollo, idServizio);
	}
	public static String getLabelAccordoServizioParteSpecificaSenzaErogatore(IDServizio idServizio) throws Exception{
		ProtocolFactoryManager protocolFactoryManager = ProtocolFactoryManager.getInstance();
		String protocollo = protocolFactoryManager.getProtocolByServiceType(idServizio.getTipo());
		return getLabelAccordoServizioParteSpecificaSenzaErogatore(protocollo, idServizio);
	}
	public static String getLabelAccordoServizioParteSpecificaSenzaErogatore(String protocollo, IDServizio idServizio) throws Exception{
		return getLabelAccordoServizioParteSpecificaSenzaErogatore(protocollo, idServizio.getTipo(), idServizio.getNome(), idServizio.getVersione());
	}
	public static String getLabelAccordoServizioParteSpecifica(String protocollo, IDServizio idServizio) throws Exception{
		StringBuilder bf = new StringBuilder();
		bf.append(getLabelAccordoServizioParteSpecificaSenzaErogatore(protocollo, idServizio.getTipo(), idServizio.getNome(), idServizio.getVersione()));
		bf.append(" (");
		bf.append(getLabelSoggetto(protocollo, idServizio.getSoggettoErogatore().getTipo(), idServizio.getSoggettoErogatore().getNome()));
		bf.append(")");
		return bf.toString();
	}
	
	
	
	// ACCORDI COOPERAZIONE
	
	public static String getLabelAccordoCooperazione(AccordoCooperazione ac) throws Exception{
		ProtocolFactoryManager protocolFactoryManager = ProtocolFactoryManager.getInstance();
		String protocollo = protocolFactoryManager.getProtocolByOrganizationType(ac.getSoggettoReferente().getTipo());
		return getLabelAccordoCooperazione(protocollo, IDAccordoCooperazioneFactory.getInstance().getIDAccordoFromAccordo(ac));
	}
	public static String getLabelAccordoCooperazione(String protocollo, AccordoCooperazione ac) throws Exception{
		return getLabelAccordoCooperazione(protocollo, IDAccordoCooperazioneFactory.getInstance().getIDAccordoFromAccordo(ac));
	}
	public static String getLabelAccordoCooperazione(IDAccordoCooperazione idAccordo) throws Exception{
		ProtocolFactoryManager protocolFactoryManager = ProtocolFactoryManager.getInstance();
		String protocollo = protocolFactoryManager.getProtocolByOrganizationType(idAccordo.getSoggettoReferente().getTipo());
		return getLabelAccordoCooperazione(protocollo, idAccordo);
	}
	public static String getLabelAccordoCooperazione(String protocollo, IDAccordoCooperazione idAccordo) throws Exception{
		StringBuilder bf = new StringBuilder();
		bf.append(idAccordo.getNome());
		bf.append(" v");
		bf.append(idAccordo.getVersione());
		//if(this.apcCore.isSupportatoSoggettoReferente(protocollo)) {
		if(idAccordo.getSoggettoReferente()!=null){
			bf.append(" (");
			bf.append(getLabelSoggetto(protocollo, idAccordo.getSoggettoReferente().getTipo(), idAccordo.getSoggettoReferente().getNome()));
			bf.append(")");
		}
		//}
		return bf.toString();
	}
	
	
	// RISORSE
	
	public static String getLabelResource(org.openspcoop2.core.registry.Resource resource) throws Exception{
		String method = null;
		if(resource.getMethod()!=null) {
			method = resource.getMethod().getValue();
		}
		return getLabelResource(method, resource.getPath());
	}
	public static String getLabelResource(org.openspcoop2.core.registry.beans.ResourceSintetica resource) throws Exception{
		String method = null;
		if(resource.getMethod()!=null) {
			method = resource.getMethod().getValue();
		}
		return getLabelResource(method, resource.getPath());
	}
	
	public static String getLabelResource(String httpmethodParam, String pathParam) throws Exception{
		String method = null;
		if(httpmethodParam==null || "".equals(httpmethodParam)) {
			//resourcePrefix = "*"; Non mettiamo nulla
		}
		else {
			method = httpmethodParam;
		}
		
		String path = null;
		if(pathParam==null || "".equals(pathParam)) {
			path = "Qualsiasi";
		}
		else {
			path = pathParam;
		}
		
		if(method!=null) {
			return  method + " " + path;
		}
		else {
			return path;
		}
		
	}
	
	
	// API con DOMINIO
	
	public static final String LABEL_DOMINIO = "@";
	
	public static String getLabelServizioConDominioErogatore(String servizio, String erogatore) {
		if(servizio.contains(" ")) {
			String [] split = servizio.split(" ");
			if(split!=null && split.length==2) {
				StringBuilder bf = new StringBuilder();
				bf.append(split[0]);
				bf.append(LABEL_DOMINIO);
				bf.append(erogatore);
				bf.append(" ");
				bf.append(split[1]);
				return bf.toString();
			}
			else {
				StringBuilder bf = new StringBuilder();
				bf.append(servizio);
				bf.append(LABEL_DOMINIO);
				bf.append(erogatore);	
				return bf.toString();
			}
		}
		else {
			StringBuilder bf = new StringBuilder();
			bf.append(servizio);
			bf.append(LABEL_DOMINIO);
			bf.append(erogatore);	
			return bf.toString();
		}

	}
}
