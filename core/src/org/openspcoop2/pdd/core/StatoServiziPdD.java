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

package org.openspcoop2.pdd.core;

import java.util.List;

import org.openspcoop2.core.config.StatoServiziPdd;
import org.openspcoop2.core.config.StatoServiziPddIntegrationManager;
import org.openspcoop2.core.config.StatoServiziPddPortaApplicativa;
import org.openspcoop2.core.config.StatoServiziPddPortaDelegata;
import org.openspcoop2.core.config.TipoFiltroAbilitazioneServizi;
import org.openspcoop2.core.config.constants.CostantiConfigurazione;
import org.openspcoop2.core.config.driver.DriverConfigurazioneException;
import org.openspcoop2.core.config.driver.DriverConfigurazioneNotFound;
import org.openspcoop2.core.id.IDServizio;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.pdd.config.ConfigurazionePdDManager;
import org.openspcoop2.pdd.services.RicezioneBuste;
import org.openspcoop2.pdd.services.RicezioneContenutiApplicativi;
import org.openspcoop2.pdd.services.skeleton.IntegrationManager;

/**
 * StatoServiziPdD
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class StatoServiziPdD {
	
	private static ConfigurazionePdDManager configPdDReader = ConfigurazionePdDManager.getInstance();
	
	
	/* **************** PORTA DELEGATA (GET) ***************** */
	
	public static boolean isPDServiceActive() {
		return configPdDReader.isPDServiceActive();
	}
	
	public static String getPDServiceFiltriAbilitazioneAttivi() throws DriverConfigurazioneException {
		StatoServiziPdd statoServiziPdd = StatoServiziPdD.configPdDReader.getStatoServiziPdD();
		if(statoServiziPdd==null || statoServiziPdd.getPortaDelegata()==null || statoServiziPdd.getPortaDelegata().sizeFiltroAbilitazioneList()<=0){
			return "";
		}
		else{
			StringBuffer bf = new StringBuffer();
			for (TipoFiltroAbilitazioneServizi tipo : statoServiziPdd.getPortaDelegata().getFiltroAbilitazioneList()) {
				bf.append(StatoServiziPdD.toString(tipo));
			}
			return bf.toString();
		}
	}
	public static String getPDServiceFiltriDisabilitazioneAttivi() throws DriverConfigurazioneException {
		StatoServiziPdd statoServiziPdd = StatoServiziPdD.configPdDReader.getStatoServiziPdD();
		if(statoServiziPdd==null || statoServiziPdd.getPortaDelegata()==null || statoServiziPdd.getPortaDelegata().sizeFiltroDisabilitazioneList()<=0){
			return "";
		}
		else{
			StringBuffer bf = new StringBuffer();
			for (TipoFiltroAbilitazioneServizi tipo : statoServiziPdd.getPortaDelegata().getFiltroDisabilitazioneList()) {
				bf.append(StatoServiziPdD.toString(tipo));
			}
			return bf.toString();
		}
	}
	
	/* **************** PORTA DELEGATA (MODIFY) ***************** */
	
	public static synchronized void setPDServiceActive(boolean stato) throws DriverConfigurazioneException, DriverConfigurazioneNotFound {
		synchronized (RicezioneContenutiApplicativi.isActivePDService) {
			
			StatoServiziPdd statoServiziPdd = StatoServiziPdD.configPdDReader.getStatoServiziPdD();
			if(statoServiziPdd==null){
				statoServiziPdd = new StatoServiziPdd();
			}
			if(statoServiziPdd.getPortaDelegata()==null){
				statoServiziPdd.setPortaDelegata(new StatoServiziPddPortaDelegata());
			}
			if(stato){
				statoServiziPdd.getPortaDelegata().setStato(CostantiConfigurazione.ABILITATO);
			}else{
				statoServiziPdd.getPortaDelegata().setStato(CostantiConfigurazione.DISABILITATO);
			}
			StatoServiziPdD.configPdDReader.updateStatoServiziPdD(statoServiziPdd);
			
			RicezioneContenutiApplicativi.isActivePDService = stato;
		}
	}
	
	public static synchronized void addFiltroAbilitazionePD(TipoFiltroAbilitazioneServizi tipo) throws DriverConfigurazioneException, DriverConfigurazioneNotFound {
		synchronized (RicezioneContenutiApplicativi.isActivePDService) {
			
			StatoServiziPdd statoServiziPdd = StatoServiziPdD.configPdDReader.getStatoServiziPdD();
			if(statoServiziPdd==null){
				statoServiziPdd = new StatoServiziPdd();
			}
			if(statoServiziPdd.getPortaDelegata()==null){
				statoServiziPdd.setPortaDelegata(new StatoServiziPddPortaDelegata());
			}
			statoServiziPdd.getPortaDelegata().addFiltroAbilitazione(tipo);
			StatoServiziPdD.configPdDReader.updateStatoServiziPdD(statoServiziPdd);
			
			RicezioneContenutiApplicativi.listaAbilitazioniPDService = statoServiziPdd.getPortaDelegata().getFiltroAbilitazioneList();
		}
	}
	public static synchronized void addFiltroDisabilitazionePD(TipoFiltroAbilitazioneServizi tipo) throws DriverConfigurazioneException, DriverConfigurazioneNotFound {
		synchronized (RicezioneContenutiApplicativi.isActivePDService) {
			
			StatoServiziPdd statoServiziPdd = StatoServiziPdD.configPdDReader.getStatoServiziPdD();
			if(statoServiziPdd==null){
				statoServiziPdd = new StatoServiziPdd();
			}
			if(statoServiziPdd.getPortaDelegata()==null){
				statoServiziPdd.setPortaDelegata(new StatoServiziPddPortaDelegata());
			}
			statoServiziPdd.getPortaDelegata().addFiltroDisabilitazione(tipo);
			StatoServiziPdD.configPdDReader.updateStatoServiziPdD(statoServiziPdd);
			
			RicezioneContenutiApplicativi.listaDisabilitazioniPDService = statoServiziPdd.getPortaDelegata().getFiltroDisabilitazioneList();
		}
	}
	public static synchronized void removeFiltroAbilitazionePD(TipoFiltroAbilitazioneServizi tipo) throws DriverConfigurazioneException, DriverConfigurazioneNotFound {
		synchronized (RicezioneContenutiApplicativi.isActivePDService) {
			
			StatoServiziPdd statoServiziPdd = StatoServiziPdD.configPdDReader.getStatoServiziPdD();
			if(statoServiziPdd==null){
				statoServiziPdd = new StatoServiziPdd();
			}
			if(statoServiziPdd.getPortaDelegata()==null){
				statoServiziPdd.setPortaDelegata(new StatoServiziPddPortaDelegata());
			}
			String daEliminare = StatoServiziPdD.toString(tipo);
			for (int i = 0; i < statoServiziPdd.getPortaDelegata().sizeFiltroAbilitazioneList(); i++) {
				String tmp = StatoServiziPdD.toString(statoServiziPdd.getPortaDelegata().getFiltroAbilitazione(i));
				if(tmp.equals(daEliminare)){
					statoServiziPdd.getPortaDelegata().removeFiltroAbilitazione(i);
					break;
				}
			}
			StatoServiziPdD.configPdDReader.updateStatoServiziPdD(statoServiziPdd);
			
			RicezioneContenutiApplicativi.listaAbilitazioniPDService = statoServiziPdd.getPortaDelegata().getFiltroAbilitazioneList();
		}
	}
	public static synchronized void removeFiltroDisabilitazionePD(TipoFiltroAbilitazioneServizi tipo) throws DriverConfigurazioneException, DriverConfigurazioneNotFound {
		synchronized (RicezioneContenutiApplicativi.isActivePDService) {
			
			StatoServiziPdd statoServiziPdd = StatoServiziPdD.configPdDReader.getStatoServiziPdD();
			if(statoServiziPdd==null){
				statoServiziPdd = new StatoServiziPdd();
			}
			if(statoServiziPdd.getPortaDelegata()==null){
				statoServiziPdd.setPortaDelegata(new StatoServiziPddPortaDelegata());
			}
			String daEliminare = StatoServiziPdD.toString(tipo);
			for (int i = 0; i < statoServiziPdd.getPortaDelegata().sizeFiltroDisabilitazioneList(); i++) {
				String tmp = StatoServiziPdD.toString(statoServiziPdd.getPortaDelegata().getFiltroDisabilitazione(i));
				if(tmp.equals(daEliminare)){
					statoServiziPdd.getPortaDelegata().removeFiltroDisabilitazione(i);
					break;
				}
			}
			StatoServiziPdD.configPdDReader.updateStatoServiziPdD(statoServiziPdd);
			
			RicezioneContenutiApplicativi.listaDisabilitazioniPDService = statoServiziPdd.getPortaDelegata().getFiltroDisabilitazioneList();
		}
	}
	
	
	
	
	/* **************** PORTA APPLICATIVA (GET) ***************** */
	
	public static boolean isPAServiceActive() {
		return configPdDReader.isPAServiceActive();
	}
	
	public static String getPAServiceFiltriAbilitazioneAttivi() throws DriverConfigurazioneException {
		StatoServiziPdd statoServiziPdd = StatoServiziPdD.configPdDReader.getStatoServiziPdD();
		if(statoServiziPdd==null || statoServiziPdd.getPortaApplicativa()==null || statoServiziPdd.getPortaApplicativa().sizeFiltroAbilitazioneList()<=0){
			return "";
		}
		else{
			StringBuffer bf = new StringBuffer();
			for (TipoFiltroAbilitazioneServizi tipo : statoServiziPdd.getPortaApplicativa().getFiltroAbilitazioneList()) {
				bf.append(StatoServiziPdD.toString(tipo));
			}
			return bf.toString();
		}
	}
	public static String getPAServiceFiltriDisabilitazioneAttivi() throws DriverConfigurazioneException {
		StatoServiziPdd statoServiziPdd = StatoServiziPdD.configPdDReader.getStatoServiziPdD();
		if(statoServiziPdd==null || statoServiziPdd.getPortaApplicativa()==null || statoServiziPdd.getPortaApplicativa().sizeFiltroDisabilitazioneList()<=0){
			return "";
		}
		else{
			StringBuffer bf = new StringBuffer();
			for (TipoFiltroAbilitazioneServizi tipo : statoServiziPdd.getPortaApplicativa().getFiltroDisabilitazioneList()) {
				bf.append(StatoServiziPdD.toString(tipo));
			}
			return bf.toString();
		}
	}
	
	
	/* **************** PORTA APPLICATIVA (MODIFY) ***************** */

	public static synchronized void setPAServiceActive(boolean stato) throws DriverConfigurazioneException, DriverConfigurazioneNotFound {
		synchronized (RicezioneBuste.isActivePAService) {
			
			StatoServiziPdd statoServiziPdd = StatoServiziPdD.configPdDReader.getStatoServiziPdD();
			if(statoServiziPdd==null){
				statoServiziPdd = new StatoServiziPdd();
			}
			if(statoServiziPdd.getPortaApplicativa()==null){
				statoServiziPdd.setPortaApplicativa(new StatoServiziPddPortaApplicativa());
			}
			if(stato){
				statoServiziPdd.getPortaApplicativa().setStato(CostantiConfigurazione.ABILITATO);
			}else{
				statoServiziPdd.getPortaApplicativa().setStato(CostantiConfigurazione.DISABILITATO);
			}
			StatoServiziPdD.configPdDReader.updateStatoServiziPdD(statoServiziPdd);
			
			RicezioneBuste.isActivePAService = stato;
		}
	}
	
	public static synchronized void addFiltroAbilitazionePA(TipoFiltroAbilitazioneServizi tipo) throws DriverConfigurazioneException, DriverConfigurazioneNotFound {
		synchronized (RicezioneBuste.isActivePAService) {
			
			StatoServiziPdd statoServiziPdd = StatoServiziPdD.configPdDReader.getStatoServiziPdD();
			if(statoServiziPdd==null){
				statoServiziPdd = new StatoServiziPdd();
			}
			if(statoServiziPdd.getPortaApplicativa()==null){
				statoServiziPdd.setPortaApplicativa(new StatoServiziPddPortaApplicativa());
			}
			statoServiziPdd.getPortaApplicativa().addFiltroAbilitazione(tipo);
			StatoServiziPdD.configPdDReader.updateStatoServiziPdD(statoServiziPdd);
			
			RicezioneBuste.listaAbilitazioniPAService = statoServiziPdd.getPortaApplicativa().getFiltroAbilitazioneList(); 
		}
	}
	public static synchronized void addFiltroDisabilitazionePA(TipoFiltroAbilitazioneServizi tipo) throws DriverConfigurazioneException, DriverConfigurazioneNotFound {
		synchronized (RicezioneBuste.isActivePAService) {
			
			StatoServiziPdd statoServiziPdd = StatoServiziPdD.configPdDReader.getStatoServiziPdD();
			if(statoServiziPdd==null){
				statoServiziPdd = new StatoServiziPdd();
			}
			if(statoServiziPdd.getPortaApplicativa()==null){
				statoServiziPdd.setPortaApplicativa(new StatoServiziPddPortaApplicativa());
			}
			statoServiziPdd.getPortaApplicativa().addFiltroDisabilitazione(tipo);
			StatoServiziPdD.configPdDReader.updateStatoServiziPdD(statoServiziPdd);
			
			RicezioneBuste.listaDisabilitazioniPAService = statoServiziPdd.getPortaApplicativa().getFiltroDisabilitazioneList(); 
		}
	}
	public static synchronized void removeFiltroAbilitazionePA(TipoFiltroAbilitazioneServizi tipo) throws DriverConfigurazioneException, DriverConfigurazioneNotFound {
		synchronized (RicezioneBuste.isActivePAService) {
			
			StatoServiziPdd statoServiziPdd = StatoServiziPdD.configPdDReader.getStatoServiziPdD();
			if(statoServiziPdd==null){
				statoServiziPdd = new StatoServiziPdd();
			}
			if(statoServiziPdd.getPortaApplicativa()==null){
				statoServiziPdd.setPortaApplicativa(new StatoServiziPddPortaApplicativa());
			}
			String daEliminare = StatoServiziPdD.toString(tipo);
			for (int i = 0; i < statoServiziPdd.getPortaApplicativa().sizeFiltroAbilitazioneList(); i++) {
				String tmp = StatoServiziPdD.toString(statoServiziPdd.getPortaApplicativa().getFiltroAbilitazione(i));
				if(tmp.equals(daEliminare)){
					statoServiziPdd.getPortaApplicativa().removeFiltroAbilitazione(i);
					break;
				}
			}
			StatoServiziPdD.configPdDReader.updateStatoServiziPdD(statoServiziPdd);
			
			RicezioneBuste.listaAbilitazioniPAService = statoServiziPdd.getPortaApplicativa().getFiltroAbilitazioneList(); 
		}
	}
	public static synchronized void removeFiltroDisabilitazionePA(TipoFiltroAbilitazioneServizi tipo) throws DriverConfigurazioneException, DriverConfigurazioneNotFound {
		synchronized (RicezioneBuste.isActivePAService) {
			
			StatoServiziPdd statoServiziPdd = StatoServiziPdD.configPdDReader.getStatoServiziPdD();
			if(statoServiziPdd==null){
				statoServiziPdd = new StatoServiziPdd();
			}
			if(statoServiziPdd.getPortaApplicativa()==null){
				statoServiziPdd.setPortaApplicativa(new StatoServiziPddPortaApplicativa());
			}
			String daEliminare = StatoServiziPdD.toString(tipo);
			for (int i = 0; i < statoServiziPdd.getPortaApplicativa().sizeFiltroDisabilitazioneList(); i++) {
				String tmp = StatoServiziPdD.toString(statoServiziPdd.getPortaApplicativa().getFiltroDisabilitazione(i));
				if(tmp.equals(daEliminare)){
					statoServiziPdd.getPortaApplicativa().removeFiltroDisabilitazione(i);
					break;
				}
			}
			StatoServiziPdD.configPdDReader.updateStatoServiziPdD(statoServiziPdd);
			
			RicezioneBuste.listaDisabilitazioniPAService = statoServiziPdd.getPortaApplicativa().getFiltroDisabilitazioneList(); 
		}
	}
	
	
	
	
	
	/* **************** INTEGRATION MANAGER (GET) ***************** */
	
	public static boolean isIMServiceActive() {
		return configPdDReader.isIMServiceActive();
	}
	
	
	/* **************** INTEGRATION MANAGER (MODIFY) ***************** */
	
	public static synchronized void setIMServiceActive(boolean stato) throws DriverConfigurazioneException, DriverConfigurazioneNotFound {
		synchronized (IntegrationManager.isActiveIMService) {
			
			StatoServiziPdd statoServiziPdd = StatoServiziPdD.configPdDReader.getStatoServiziPdD();
			if(statoServiziPdd==null){
				statoServiziPdd = new StatoServiziPdd();
			}
			if(statoServiziPdd.getIntegrationManager()==null){
				statoServiziPdd.setIntegrationManager(new StatoServiziPddIntegrationManager());
			}
			if(stato){
				statoServiziPdd.getIntegrationManager().setStato(CostantiConfigurazione.ABILITATO);
			}else{
				statoServiziPdd.getIntegrationManager().setStato(CostantiConfigurazione.DISABILITATO);
			}
			StatoServiziPdD.configPdDReader.updateStatoServiziPdD(statoServiziPdd);
			
			IntegrationManager.isActiveIMService = stato;
		}
	}

	
	
	
	
	private static String toString(TipoFiltroAbilitazioneServizi tipo){
		StringBuffer bf = new StringBuffer();
		bf.append("(");
		
		if(tipo.getTipoSoggettoFruitore()!=null){
			bf.append(" TIPO_FRUITORE:");
			bf.append(tipo.getTipoSoggettoFruitore());
		}
		if(tipo.getSoggettoFruitore()!=null){
			bf.append(" FRUITORE:");
			bf.append(tipo.getSoggettoFruitore());
		}
		if(tipo.getIdentificativoPortaFruitore()!=null){
			bf.append(" PORTA_FRUITORE:");
			bf.append(tipo.getSoggettoFruitore());
		}
		
		if(tipo.getTipoSoggettoErogatore()!=null){
			bf.append(" TIPO_EROGATORE:");
			bf.append(tipo.getTipoSoggettoErogatore());
		}
		if(tipo.getSoggettoErogatore()!=null){
			bf.append(" EROGATORE:");
			bf.append(tipo.getSoggettoErogatore());
		}
		if(tipo.getIdentificativoPortaErogatore()!=null){
			bf.append(" PORTA_EROGATORE:");
			bf.append(tipo.getSoggettoErogatore());
		}
		
		if(tipo.getTipoServizio()!=null){
			bf.append(" TIPO_SERVIZIO:");
			bf.append(tipo.getTipoServizio());
		}
		if(tipo.getServizio()!=null){
			bf.append(" SERVIZIO:");
			bf.append(tipo.getServizio());
		}
		
		if(tipo.getAzione()!=null){
			bf.append(" AZIONE:");
			bf.append(tipo.getAzione());
		}
		
		bf.append(" )");
		return bf.toString();
	}
	
	
	
	public static boolean isEnabled(boolean statoServizioAbilitato,
			List<TipoFiltroAbilitazioneServizi> filtriAbilitazioni,
			List<TipoFiltroAbilitazioneServizi> filtriDisabilitazioni,
			IDSoggetto soggettoFruitore,IDServizio idServizio){
		
		if(statoServizioAbilitato){
			
			// il servizio e' abilitato.
			// Verifico se per caso esiste qualche regola puntuale di disabilitazione.
			if(filtriDisabilitazioni!=null){
				for (TipoFiltroAbilitazioneServizi tipo : filtriDisabilitazioni) {			
					if(!StatoServiziPdD.isMatch(tipo, soggettoFruitore, idServizio)){
						continue;
					}
					return false; // la regola ha un match: disabilitato!
				}
			}
			
			return true;
			
		}
		else{
			
			// il servizio e' disabilitato.
			// Verifico se per caso esiste qualche regola puntuale di abilitazione.
			if(filtriAbilitazioni!=null){
				for (TipoFiltroAbilitazioneServizi tipo : filtriAbilitazioni) {			
					if(!StatoServiziPdD.isMatch(tipo, soggettoFruitore, idServizio)){
						continue;
					}
					return true; // la regola ha un match: abilitato!
				}
			}		
			
			return false;
			
		}
	}
	
	private static boolean isMatch(TipoFiltroAbilitazioneServizi tipo,IDSoggetto soggettoFruitore,IDServizio idServizio){
		if(soggettoFruitore==null){
			return false;
		}
		if(tipo.getTipoSoggettoFruitore()!=null){
			if(!tipo.getTipoSoggettoFruitore().equals(soggettoFruitore.getTipo())){
				return false;
			}
		}
		if(tipo.getSoggettoFruitore()!=null){
			if(!tipo.getSoggettoFruitore().equals(soggettoFruitore.getNome())){
				return false;
			}
		}
		if(tipo.getIdentificativoPortaFruitore()!=null){
			if(!tipo.getIdentificativoPortaFruitore().equals(soggettoFruitore.getCodicePorta())){
				return false;
			}
		}

		if(idServizio==null){
			return false;
		}
		
		if(idServizio.getSoggettoErogatore()==null){
			return false;
		}
		if(tipo.getTipoSoggettoErogatore()!=null){
			if(!tipo.getTipoSoggettoErogatore().equals(idServizio.getSoggettoErogatore().getTipo())){
				return false;
			}
		}
		if(tipo.getSoggettoErogatore()!=null){
			if(!tipo.getSoggettoErogatore().equals(idServizio.getSoggettoErogatore().getNome())){
				return false;
			}
		}
		if(tipo.getIdentificativoPortaErogatore()!=null){
			if(!tipo.getIdentificativoPortaErogatore().equals(idServizio.getSoggettoErogatore().getCodicePorta())){
				return false;
			}
		}
		
		if(tipo.getTipoServizio()!=null){
			if(!tipo.getTipoServizio().equals(idServizio.getTipoServizio())){
				return false;
			}
		}
		if(tipo.getServizio()!=null){
			if(!tipo.getServizio().equals(idServizio.getServizio())){
				return false;
			}
		}
		if(tipo.getAzione()!=null){
			if(!tipo.getAzione().equals(idServizio.getAzione())){
				return false;
			}
		}
		
		return true;
	}
}
