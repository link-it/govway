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




package org.openspcoop2.core.commons;

import java.util.ArrayList;
import java.util.List;


/**
 * Filtri
 * 
 * @author Stefano Corallo - corallo@link.it
 * @author $Author$
 * @version $Rev$, $Date$
 */

public final class Filtri
{

	
	public final static String FILTRO_PROTOCOLLO = "filtroProtocollo";
	public final static String FILTRO_PROTOCOLLI = "filtroProtocolli";
	
	public final static String FILTRO_SOGGETTO_DEFAULT = "filtroSoggettoDefault";
	
	public final static String FILTRO_DOMINIO = "filtroDominio";
	
	public final static String FILTRO_STATO_ACCORDO = "filtroStatoAccordo";
		
	public final static String FILTRO_SERVICE_BINDING = "filtroServiceBinding";
	
	public final static String FILTRO_HTTP_METHOD = "filtroHttpMethod";
	
	public final static String FILTRO_SOGGETTO = "filtroSoggetto";
	
	public final static String FILTRO_RUOLO_SERVIZIO_APPLICATIVO = "filtroRuoloSA";
	public final static String VALUE_FILTRO_RUOLO_SERVIZIO_APPLICATIVO_EROGATORE = "Erogatore";
	public final static String VALUE_FILTRO_RUOLO_SERVIZIO_APPLICATIVO_FRUITORE = "Fruitore";
	
	public final static String FILTRO_TIPO_SERVIZIO_APPLICATIVO = "filtroTipoSA";
	
	public final static String FILTRO_TIPO_SOGGETTO = "filtroTipoSoggetto";
	
	public final static String FILTRO_TIPO_CREDENZIALI = "filtroTipoCredenziali";
	
	public final static String FILTRO_RUOLO_TIPOLOGIA = "filtroRuoloTipologia";
	public final static String FILTRO_RUOLO_CONTESTO = "filtroRuoloContesto";
	
	public final static String FILTRO_SCOPE_TIPOLOGIA = "filtroScopeTipologia";
	public final static String FILTRO_SCOPE_CONTESTO = "filtroScopeContesto";
	
	public final static String FILTRO_API_CONTESTO = "filtroApiContesto";
	public final static String FILTRO_API_CONTESTO_VALUE_EROGAZIONE_FRUIZIONE = "ErogazioneFruizione";
	public final static String FILTRO_API_CONTESTO_VALUE_APPLICATIVI = "Applicativi";
	public final static String FILTRO_API_CONTESTO_VALUE_SOGGETTI = "Soggetti";
	public final static String FILTRO_API_IMPLEMENTAZIONE = "filtroApiImpl";
	
	public final static String FILTRO_SERVIZIO_APPLICATIVO = "filtroSA";
	
	public final static String FILTRO_UTENTE = "filtroUtente";
	
	public final static String FILTRO_AZIONE = "filtroAzione";
	
	public final static String FILTRO_RUOLO = "filtroRuolo";
	
	public final static String FILTRO_TIPO_POLICY = "filtroTipoPolicy";
	public final static String FILTRO_TIPO_POLICY_BUILT_IN = "built-in";
	public final static String FILTRO_TIPO_POLICY_UTENTE = "utente";
	
	public final static String FILTRO_TIPO_RISORSA_POLICY = "filtroTipoRisorsaPolicy";
	
	public final static String FILTRO_TIPO_TOKEN_POLICY = "filtroTipoTokenPolicy";
	
	public final static String FILTRO_GRUPPO_SERVICE_BINDING = "filtroGruppoServiceBinding";
	
	public final static String FILTRO_GRUPPO = "filtroGruppo";
	
	public final static String FILTRO_CANALE = "filtroCanale";
	public final static String PREFIX_VALUE_CANALE_DEFAULT = "__DEFAULT__ ";
	
	public static List<String> convertToTipiSoggetti(String filterProtocollo, String filterProtocolli) throws CoreException {
		List<String> tipoSoggettiProtocollo = null;
		if(filterProtocollo!=null && !"".equals(filterProtocollo)) {
			try {
				tipoSoggettiProtocollo = ProtocolFactoryReflectionUtils.getOrganizationTypes(filterProtocollo);
			}catch(Exception e) {
				throw new CoreException(e.getMessage(),e);
			}
		}
		else if(filterProtocolli!=null && !"".equals(filterProtocolli)) {
			List<String> protocolli = Filtri.convertToList(filterProtocolli);
			if(protocolli!=null && protocolli.size()>0) {
				tipoSoggettiProtocollo = new ArrayList<>();
				for (String protocollo : protocolli) {
					try {
						List<String> tipi = ProtocolFactoryReflectionUtils.getOrganizationTypes(protocollo);
						if(tipi!=null && tipi.size()>0) {
							tipoSoggettiProtocollo.addAll(tipi);
						}
					}catch(Exception e) {
						throw new CoreException(e.getMessage(),e);
					}
				}
				if(tipoSoggettiProtocollo.size()<=0) {
					tipoSoggettiProtocollo = null;
				}
			}
		}
		return tipoSoggettiProtocollo;
	}
	
	public static List<String> convertToTipiServizi(String filterProtocollo, String filterProtocolli) throws CoreException {
		List<String> tipoServiziProtocollo = null;
		if(filterProtocollo!=null && !"".equals(filterProtocollo)) {
			try {
				tipoServiziProtocollo = ProtocolFactoryReflectionUtils.getServiceTypes(filterProtocollo);
			}catch(Exception e) {
				throw new CoreException(e.getMessage(),e);
			}
		}
		else if(filterProtocolli!=null && !"".equals(filterProtocolli)) {
			List<String> protocolli = Filtri.convertToList(filterProtocolli);
			if(protocolli!=null && protocolli.size()>0) {
				tipoServiziProtocollo = new ArrayList<>();
				for (String protocollo : protocolli) {
					try {
						List<String> tipi = ProtocolFactoryReflectionUtils.getServiceTypes(protocollo);
						if(tipi!=null && tipi.size()>0) {
							tipoServiziProtocollo.addAll(tipi);
						}
					}catch(Exception e) {
						throw new CoreException(e.getMessage(),e);
					}
				}
				if(tipoServiziProtocollo.size()<=0) {
					tipoServiziProtocollo = null;
				}
			}
		}
		return tipoServiziProtocollo;
	}
	
	
	
	public static String convertToString(List<String> listSrc) {
		if(listSrc==null || listSrc.size()<=0) {
			return null;
		}
		StringBuilder bf = new StringBuilder();
		for (String src : listSrc) {
			if(bf.length()>0) {
				bf.append(",");
			}
			bf.append(src);
		}
		return bf.toString();
	}
	public static List<String> convertToList(String src) {
		if(src==null) {
			return null;
		}
		List<String> l = new ArrayList<>();
		if(src.contains(",")) {
			String [] tmp = src.split(",");
			for (int i = 0; i < tmp.length; i++) {
				l.add(tmp[i].trim());
			}
		}
		else {
			l.add(src);
		}
		return l;
	}
	
}
