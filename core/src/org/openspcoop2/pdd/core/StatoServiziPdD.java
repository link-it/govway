/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2021 Link.it srl (https://link.it). 
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

package org.openspcoop2.pdd.core;

import java.util.ArrayList;
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

/**
 * StatoServiziPdD
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class StatoServiziPdD {
	
	private static ConfigurazionePdDManager configPdDReader = ConfigurazionePdDManager.getInstance();
	
	
	/* **************** INIT ***************** */
	
	public static void initialize() throws DriverConfigurazioneException {
		
		StatoServiziPdd statoServiziPdd = StatoServiziPdD.configPdDReader.getStatoServiziPdD();
		
		// *** aggiorno stato in ram ***
		
		// -- portaDelegata --
		if(statoServiziPdd!=null && statoServiziPdd.getPortaDelegata()!=null){ 
			if(CostantiConfigurazione.DISABILITATO.equals(statoServiziPdd.getPortaDelegata().getStato())){
				activePDService = false;
			}
			else{
				activePDService = true;
			}
			listaAbilitazioniPDService = statoServiziPdd.getPortaDelegata().getFiltroAbilitazioneList();
			listaDisabilitazioniPDService = statoServiziPdd.getPortaDelegata().getFiltroDisabilitazioneList();
		}
		// se non si inizializza, tutte le transazioni effettuano un nuovo accesso al db
		if(activePDService==null){
			activePDService = true;
		}
		if(listaAbilitazioniPDService==null){
			listaAbilitazioniPDService = new ArrayList<TipoFiltroAbilitazioneServizi>();
		}
		if(listaDisabilitazioniPDService==null){
			listaDisabilitazioniPDService = new ArrayList<TipoFiltroAbilitazioneServizi>();
		}
		
		// -- portaApplicativa --	
		if(statoServiziPdd!=null && statoServiziPdd.getPortaApplicativa()!=null){ 
			if(CostantiConfigurazione.DISABILITATO.equals(statoServiziPdd.getPortaApplicativa().getStato())){
				activePAService = false;
			}
			else{
				activePAService = true;
			}
			listaAbilitazioniPAService = statoServiziPdd.getPortaApplicativa().getFiltroAbilitazioneList();
			listaDisabilitazioniPAService = statoServiziPdd.getPortaApplicativa().getFiltroDisabilitazioneList();
		}
		// se non si inizializza, tutte le transazioni effettuano un nuovo accesso al db
		if(activePAService==null){
			activePAService = true;
		}
		if(listaAbilitazioniPAService==null){
			listaAbilitazioniPAService = new ArrayList<TipoFiltroAbilitazioneServizi>();
		}
		if(listaDisabilitazioniPAService==null){
			listaDisabilitazioniPAService = new ArrayList<TipoFiltroAbilitazioneServizi>();
		}
		
		// -- integrationManager --
		if(statoServiziPdd!=null && statoServiziPdd.getIntegrationManager()!=null){ 
			if(CostantiConfigurazione.DISABILITATO.equals(statoServiziPdd.getIntegrationManager().getStato())){
				activeIMService = false;
			}
			else{
				activeIMService = true;
			}
		}
		if(activeIMService==null){
			activeIMService = true;
		}


	}
	
	
	
	/* **************** PORTA DELEGATA (GET) ***************** */
	
	public static boolean isPDServiceActive() {
		if(activePDService!=null){
			return activePDService;
		}
		else{
			return configPdDReader.isPDServiceActive();
		}
	}
	
	public static List<TipoFiltroAbilitazioneServizi> getPDServiceFiltriAbilitazioneAttiviList() throws DriverConfigurazioneException {
		List<TipoFiltroAbilitazioneServizi> list = null;
		if(listaAbilitazioniPDService!=null){
			list = listaAbilitazioniPDService;
		}
		else{
			StatoServiziPdd statoServiziPdd = StatoServiziPdD.configPdDReader.getStatoServiziPdD();
			if(statoServiziPdd!=null && statoServiziPdd.getPortaDelegata()!=null){
				list = statoServiziPdd.getPortaDelegata().getFiltroAbilitazioneList();
			}
		}
		return list;
	}	
	public static String getPDServiceFiltriAbilitazioneAttivi() throws DriverConfigurazioneException {
		
		List<TipoFiltroAbilitazioneServizi> list = getPDServiceFiltriAbilitazioneAttiviList();			
		if(list==null || list.size()<=0){
			return "";
		}
		else{
			StringBuilder bf = new StringBuilder();
			for (TipoFiltroAbilitazioneServizi tipo : list) {
				bf.append(StatoServiziPdD.toString(tipo));
			}
			return bf.toString();
		}
	}
	
	public static List<TipoFiltroAbilitazioneServizi> getPDServiceFiltriDisabilitazioneAttiviList() throws DriverConfigurazioneException {
		
		List<TipoFiltroAbilitazioneServizi> list = null;
		if(listaDisabilitazioniPDService!=null){
			list = listaDisabilitazioniPDService;
		}
		else{
			StatoServiziPdd statoServiziPdd = StatoServiziPdD.configPdDReader.getStatoServiziPdD();
			if(statoServiziPdd!=null && statoServiziPdd.getPortaDelegata()!=null){
				list = statoServiziPdd.getPortaDelegata().getFiltroDisabilitazioneList();
			}
		}
		return list;
		
	}
	public static String getPDServiceFiltriDisabilitazioneAttivi() throws DriverConfigurazioneException {
		
		List<TipoFiltroAbilitazioneServizi> list = getPDServiceFiltriDisabilitazioneAttiviList();		
		if(list==null || list.size()<=0){
			return "";
		}
		else{
			StringBuilder bf = new StringBuilder();
			for (TipoFiltroAbilitazioneServizi tipo : list) {
				bf.append(StatoServiziPdD.toString(tipo));
			}
			return bf.toString();
		}
	}
	
	/* **************** PORTA DELEGATA (MODIFY) ***************** */
	
	//private static Integer semaphoreActivePDService = 1;
	private static final org.openspcoop2.utils.Semaphore lockActivePDService = new org.openspcoop2.utils.Semaphore("StatoServiziFruizioni");
	
	private static Boolean activePDService = null;	
	public static void setPDServiceActive(boolean stato) throws DriverConfigurazioneException, DriverConfigurazioneNotFound {
		//synchronized (semaphoreActivePDService) {
		StatoServiziPdD.lockActivePDService.acquireThrowRuntime("setPDServiceActive");
		try {
						
			// rendo persistente modifica
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
			
			// aggiorno stato in ram
			activePDService = stato;
		}finally {
			StatoServiziPdD.lockActivePDService.release("setPDServiceActive");
		}
	}
	
	private static List<TipoFiltroAbilitazioneServizi> listaAbilitazioniPDService = null;
	public static void addFiltroAbilitazionePD(TipoFiltroAbilitazioneServizi tipo) throws DriverConfigurazioneException, DriverConfigurazioneNotFound {
		//synchronized (semaphoreActivePDService) {
		StatoServiziPdD.lockActivePDService.acquireThrowRuntime("addFiltroAbilitazionePD");
		try {
					
			// rendo persistente modifica
			StatoServiziPdd statoServiziPdd = StatoServiziPdD.configPdDReader.getStatoServiziPdD();
			if(statoServiziPdd==null){
				statoServiziPdd = new StatoServiziPdd();
			}
			if(statoServiziPdd.getPortaDelegata()==null){
				statoServiziPdd.setPortaDelegata(new StatoServiziPddPortaDelegata());
			}
			statoServiziPdd.getPortaDelegata().addFiltroAbilitazione(tipo);
			StatoServiziPdD.configPdDReader.updateStatoServiziPdD(statoServiziPdd);
			
			// aggiorno stato in ram
			listaAbilitazioniPDService = statoServiziPdd.getPortaDelegata().getFiltroAbilitazioneList();
		}finally {
			StatoServiziPdD.lockActivePDService.release("addFiltroAbilitazionePD");
		}
	}
	public static void removeFiltroAbilitazionePD(TipoFiltroAbilitazioneServizi tipo) throws DriverConfigurazioneException, DriverConfigurazioneNotFound {
		//synchronized (semaphoreActivePDService) {
		StatoServiziPdD.lockActivePDService.acquireThrowRuntime("removeFiltroAbilitazionePD");
		try {
			
			// rendo persistente modifica
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
			
			// aggiorno stato in ram
			listaAbilitazioniPDService = statoServiziPdd.getPortaDelegata().getFiltroAbilitazioneList();
		}finally{
			StatoServiziPdD.lockActivePDService.release("removeFiltroAbilitazionePD");
		}
	}
	
	private static List<TipoFiltroAbilitazioneServizi> listaDisabilitazioniPDService = null;
	public static void addFiltroDisabilitazionePD(TipoFiltroAbilitazioneServizi tipo) throws DriverConfigurazioneException, DriverConfigurazioneNotFound {
		//synchronized (semaphoreActivePDService) {
		StatoServiziPdD.lockActivePDService.acquireThrowRuntime("addFiltroDisabilitazionePD");
		try {
			
			// rendo persistente modifica
			StatoServiziPdd statoServiziPdd = StatoServiziPdD.configPdDReader.getStatoServiziPdD();
			if(statoServiziPdd==null){
				statoServiziPdd = new StatoServiziPdd();
			}
			if(statoServiziPdd.getPortaDelegata()==null){
				statoServiziPdd.setPortaDelegata(new StatoServiziPddPortaDelegata());
			}
			statoServiziPdd.getPortaDelegata().addFiltroDisabilitazione(tipo);
			StatoServiziPdD.configPdDReader.updateStatoServiziPdD(statoServiziPdd);
			
			// aggiorno stato in ram
			listaDisabilitazioniPDService = statoServiziPdd.getPortaDelegata().getFiltroDisabilitazioneList();
		}finally {
			StatoServiziPdD.lockActivePDService.release("addFiltroDisabilitazionePD");
		}
	}
	public static void removeFiltroDisabilitazionePD(TipoFiltroAbilitazioneServizi tipo) throws DriverConfigurazioneException, DriverConfigurazioneNotFound {
		//synchronized (semaphoreActivePDService) {
		StatoServiziPdD.lockActivePDService.acquireThrowRuntime("removeFiltroDisabilitazionePD");
		try {
			
			// rendo persistente modifica
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
			
			// aggiorno stato in ram
			listaDisabilitazioniPDService = statoServiziPdd.getPortaDelegata().getFiltroDisabilitazioneList();
		}finally {
			StatoServiziPdD.lockActivePDService.release("removeFiltroDisabilitazionePD");
		}
	}
	
	
	
	
	
	/* **************** PORTA APPLICATIVA (GET) ***************** */
	
	public static boolean isPAServiceActive() {
		if(activePAService!=null){
			return activePAService;
		}
		else{
			return configPdDReader.isPAServiceActive();
		}
	}
	
	public static List<TipoFiltroAbilitazioneServizi> getPAServiceFiltriAbilitazioneAttiviList() throws DriverConfigurazioneException {
		List<TipoFiltroAbilitazioneServizi> list = null;
		if(listaAbilitazioniPAService!=null){
			list = listaAbilitazioniPAService;
		}
		else{
			StatoServiziPdd statoServiziPdd = StatoServiziPdD.configPdDReader.getStatoServiziPdD();
			if(statoServiziPdd!=null && statoServiziPdd.getPortaApplicativa()!=null){
				list = statoServiziPdd.getPortaApplicativa().getFiltroAbilitazioneList();
			}
		}
		return list;
	}	
	public static String getPAServiceFiltriAbilitazioneAttivi() throws DriverConfigurazioneException {
		
		List<TipoFiltroAbilitazioneServizi> list = getPAServiceFiltriAbilitazioneAttiviList();			
		if(list==null || list.size()<=0){
			return "";
		}
		else{
			StringBuilder bf = new StringBuilder();
			for (TipoFiltroAbilitazioneServizi tipo : list) {
				bf.append(StatoServiziPdD.toString(tipo));
			}
			return bf.toString();
		}
	}
	
	public static List<TipoFiltroAbilitazioneServizi> getPAServiceFiltriDisabilitazioneAttiviList() throws DriverConfigurazioneException {
		
		List<TipoFiltroAbilitazioneServizi> list = null;
		if(listaDisabilitazioniPAService!=null){
			list = listaDisabilitazioniPAService;
		}
		else{
			StatoServiziPdd statoServiziPdd = StatoServiziPdD.configPdDReader.getStatoServiziPdD();
			if(statoServiziPdd!=null && statoServiziPdd.getPortaApplicativa()!=null){
				list = statoServiziPdd.getPortaApplicativa().getFiltroDisabilitazioneList();
			}
		}
		return list;
		
	}
	public static String getPAServiceFiltriDisabilitazioneAttivi() throws DriverConfigurazioneException {
		
		List<TipoFiltroAbilitazioneServizi> list = getPAServiceFiltriDisabilitazioneAttiviList();		
		if(list==null || list.size()<=0){
			return "";
		}
		else{
			StringBuilder bf = new StringBuilder();
			for (TipoFiltroAbilitazioneServizi tipo : list) {
				bf.append(StatoServiziPdD.toString(tipo));
			}
			return bf.toString();
		}
	}
	
	/* **************** PORTA APPLICATIVA (MODIFY) ***************** */
	
	//private static Integer semaphoreActivePAService = 1;
	private static final org.openspcoop2.utils.Semaphore lockActivePAService = new org.openspcoop2.utils.Semaphore("StatoServiziErogazioni");
	
	private static Boolean activePAService = null;	
	public static void setPAServiceActive(boolean stato) throws DriverConfigurazioneException, DriverConfigurazioneNotFound {
		//synchronized (semaphoreActivePAService) {
		StatoServiziPdD.lockActivePAService.acquireThrowRuntime("setPAServiceActive");
		try {
						
			// rendo persistente modifica
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
			
			// aggiorno stato in ram
			activePAService = stato;
		}finally {
			StatoServiziPdD.lockActivePAService.release("setPAServiceActive");
		}
	}
	
	private static List<TipoFiltroAbilitazioneServizi> listaAbilitazioniPAService = null;
	public static void addFiltroAbilitazionePA(TipoFiltroAbilitazioneServizi tipo) throws DriverConfigurazioneException, DriverConfigurazioneNotFound {
		//synchronized (semaphoreActivePAService) {
		StatoServiziPdD.lockActivePAService.acquireThrowRuntime("addFiltroAbilitazionePA");
		try {
					
			// rendo persistente modifica
			StatoServiziPdd statoServiziPdd = StatoServiziPdD.configPdDReader.getStatoServiziPdD();
			if(statoServiziPdd==null){
				statoServiziPdd = new StatoServiziPdd();
			}
			if(statoServiziPdd.getPortaApplicativa()==null){
				statoServiziPdd.setPortaApplicativa(new StatoServiziPddPortaApplicativa());
			}
			statoServiziPdd.getPortaApplicativa().addFiltroAbilitazione(tipo);
			StatoServiziPdD.configPdDReader.updateStatoServiziPdD(statoServiziPdd);
			
			// aggiorno stato in ram
			listaAbilitazioniPAService = statoServiziPdd.getPortaApplicativa().getFiltroAbilitazioneList();
		}finally {
			StatoServiziPdD.lockActivePAService.release("addFiltroAbilitazionePA");
		}
	}
	public static void removeFiltroAbilitazionePA(TipoFiltroAbilitazioneServizi tipo) throws DriverConfigurazioneException, DriverConfigurazioneNotFound {
		//synchronized (semaphoreActivePAService) {
		StatoServiziPdD.lockActivePAService.acquireThrowRuntime("removeFiltroAbilitazionePA");
		try {
			
			// rendo persistente modifica
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
			
			// aggiorno stato in ram
			listaAbilitazioniPAService = statoServiziPdd.getPortaApplicativa().getFiltroAbilitazioneList();
		}finally {
			StatoServiziPdD.lockActivePAService.release("removeFiltroAbilitazionePA");
		}
	}
	
	private static List<TipoFiltroAbilitazioneServizi> listaDisabilitazioniPAService = null;
	public static void addFiltroDisabilitazionePA(TipoFiltroAbilitazioneServizi tipo) throws DriverConfigurazioneException, DriverConfigurazioneNotFound {
		//synchronized (semaphoreActivePAService) {
		StatoServiziPdD.lockActivePAService.acquireThrowRuntime("addFiltroDisabilitazionePA");
		try {
			
			// rendo persistente modifica
			StatoServiziPdd statoServiziPdd = StatoServiziPdD.configPdDReader.getStatoServiziPdD();
			if(statoServiziPdd==null){
				statoServiziPdd = new StatoServiziPdd();
			}
			if(statoServiziPdd.getPortaApplicativa()==null){
				statoServiziPdd.setPortaApplicativa(new StatoServiziPddPortaApplicativa());
			}
			statoServiziPdd.getPortaApplicativa().addFiltroDisabilitazione(tipo);
			StatoServiziPdD.configPdDReader.updateStatoServiziPdD(statoServiziPdd);
			
			// aggiorno stato in ram
			listaDisabilitazioniPAService = statoServiziPdd.getPortaApplicativa().getFiltroDisabilitazioneList();
		}finally {
			StatoServiziPdD.lockActivePAService.release("addFiltroDisabilitazionePA");
		}
	}
	public static void removeFiltroDisabilitazionePA(TipoFiltroAbilitazioneServizi tipo) throws DriverConfigurazioneException, DriverConfigurazioneNotFound {
		//synchronized (semaphoreActivePAService) {
		StatoServiziPdD.lockActivePAService.acquireThrowRuntime("removeFiltroDisabilitazionePA");
		try {
			
			// rendo persistente modifica
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
			
			// aggiorno stato in ram
			listaDisabilitazioniPAService = statoServiziPdd.getPortaApplicativa().getFiltroDisabilitazioneList();
		}finally {
			StatoServiziPdD.lockActivePAService.release("removeFiltroDisabilitazionePA");
		}
	}
	
	
	
	
	
	/* **************** INTEGRATION MANAGER (GET) ***************** */
	
	public static boolean isIMServiceActive() {
		if(activeIMService!=null){
			return activeIMService;
		}
		else{
			return configPdDReader.isIMServiceActive();
		}
	}
	

	
	
	/* **************** INTEGRATION MANAGER (MODIFY) ***************** */
	
	//private static Integer semaphoreActiveIMService = 1;
	private static final org.openspcoop2.utils.Semaphore lockActiveIMService = new org.openspcoop2.utils.Semaphore("StatoServiziIntegrationManager");
	
	private static Boolean activeIMService = null;	
	public static void setIMServiceActive(boolean stato) throws DriverConfigurazioneException, DriverConfigurazioneNotFound {
		//synchronized (semaphoreActiveIMService) {
		StatoServiziPdD.lockActiveIMService.acquireThrowRuntime("setIMServiceActive");
		try {
						
			// rendo persistente modifica
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
			
			// aggiorno stato in ram
			activeIMService = stato;
		}finally {
			StatoServiziPdD.lockActiveIMService.release("setIMServiceActive");
		}
	}


	
	

	
	
	
	/* **************** UTILITIES ***************** */
	
	private static String toString(TipoFiltroAbilitazioneServizi tipo){
		StringBuilder bf = new StringBuilder();
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
		if(tipo.getVersioneServizio()!=null){
			bf.append(" VERSIONE_SERVIZIO:");
			bf.append(tipo.getVersioneServizio());
		}
		
		if(tipo.getAzione()!=null){
			bf.append(" AZIONE:");
			bf.append(tipo.getAzione());
		}
		
		bf.append(" )");
		return bf.toString();
	}
	
	
	
	
	
	
	
	/* **************** IS ENALBED ***************** */
	
	public static boolean isEnabledPortaDelegata(IDSoggetto soggettoFruitore,IDServizio idServizio) throws DriverConfigurazioneException{
		return _isEnabled(isPDServiceActive(), 
				getPDServiceFiltriAbilitazioneAttiviList(),
				getPDServiceFiltriDisabilitazioneAttiviList(), 
				soggettoFruitore, idServizio);
	}
	public static boolean isEnabledPortaApplicativa(IDSoggetto soggettoFruitore,IDServizio idServizio) throws DriverConfigurazioneException{
		return _isEnabled(isPAServiceActive(), 
				getPAServiceFiltriAbilitazioneAttiviList(),
				getPAServiceFiltriDisabilitazioneAttiviList(), 
				soggettoFruitore, idServizio);
	}
	public static boolean isEnabledIntegrationManager() throws DriverConfigurazioneException{
		return _isEnabled(isIMServiceActive(), 
				null,null,null,null);
	}
	private static boolean _isEnabled(boolean statoServizioAbilitato,
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
			if(!tipo.getTipoServizio().equals(idServizio.getTipo())){
				return false;
			}
		}
		if(tipo.getServizio()!=null){
			if(!tipo.getServizio().equals(idServizio.getNome())){
				return false;
			}
		}
		if(tipo.getVersioneServizio()!=null){
			if(tipo.getVersioneServizio().intValue() != idServizio.getVersione().intValue() ){
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
