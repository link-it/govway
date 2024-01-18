/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2024 Link.it srl (https://link.it). 
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
package org.openspcoop2.web.ctrlstat.servlet.utenti;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.openspcoop2.core.commons.ISearch;
import org.openspcoop2.core.commons.Liste;
import org.openspcoop2.core.id.IDServizio;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.core.registry.driver.DriverRegistroServiziException;
import org.openspcoop2.protocol.engine.utils.NamingUtils;
import org.openspcoop2.protocol.utils.ProtocolUtils;
import org.openspcoop2.utils.crypt.PasswordGenerator;
import org.openspcoop2.utils.crypt.PasswordVerifier;
import org.openspcoop2.web.ctrlstat.core.ControlStationCore;
import org.openspcoop2.web.ctrlstat.core.ConsoleSearch;
import org.openspcoop2.web.ctrlstat.costanti.CostantiControlStation;
import org.openspcoop2.web.ctrlstat.servlet.ConsoleHelper;
import org.openspcoop2.web.ctrlstat.servlet.login.LoginCostanti;
import org.openspcoop2.web.lib.mvc.CheckboxStatusType;
import org.openspcoop2.web.lib.mvc.Costanti;
import org.openspcoop2.web.lib.mvc.DataElement;
import org.openspcoop2.web.lib.mvc.DataElementType;
import org.openspcoop2.web.lib.mvc.PageData;
import org.openspcoop2.web.lib.mvc.Parameter;
import org.openspcoop2.web.lib.mvc.ServletUtils;
import org.openspcoop2.web.lib.mvc.TipoOperazione;
import org.openspcoop2.web.lib.users.DriverUsersDBException;
import org.openspcoop2.web.lib.users.dao.InterfaceType;
import org.openspcoop2.web.lib.users.dao.Permessi;
import org.openspcoop2.web.lib.users.dao.PermessiUtente;
import org.openspcoop2.web.lib.users.dao.User;
import org.openspcoop2.web.lib.users.dao.UserPassword;

/**
 * UtentiHelper
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class UtentiHelper extends ConsoleHelper {

	public UtentiHelper(HttpServletRequest request, PageData pd,
			HttpSession session) throws Exception {
		super(request, pd, session);
	}
	public UtentiHelper(ControlStationCore core, HttpServletRequest request, PageData pd,
			HttpSession session) throws Exception {
		super(core, request, pd, session);
	}

	private boolean hasOnlyPermessiUtenti(String isServizi,String isDiagnostica,String isReportistica,String isSistema,String isMessaggi,
			String isUtenti,String isAuditing, String isAccordiCooperazione,boolean singlePdD) {
		return (
				(
						(isServizi == null) 
						|| 
						!ServletUtils.isCheckBoxEnabled(isServizi)
				) &&
				(
						!singlePdD 
						|| 
						(
								//singlePdD 
								//&& 
								(
										(isDiagnostica == null) || !ServletUtils.isCheckBoxEnabled(isDiagnostica)
								)
								&& 
								(
										(isReportistica == null) || !ServletUtils.isCheckBoxEnabled(isReportistica)
								)
						)
				) &&
				((isSistema == null) || !ServletUtils.isCheckBoxEnabled(isSistema)) &&
				((isMessaggi == null) || !ServletUtils.isCheckBoxEnabled(isMessaggi)) &&
				((isUtenti != null) || ServletUtils.isCheckBoxEnabled(isUtenti)) &&
				((isAuditing == null) || !ServletUtils.isCheckBoxEnabled(isAuditing)) &&
				((isAccordiCooperazione == null) || !ServletUtils.isCheckBoxEnabled(isAccordiCooperazione)));
	}
	
	@SuppressWarnings("unused")
	private String getLabelSezionePddMonitorSoggettiServizi(boolean isDiagnosticaEnabled, boolean isReportisticaEnabled) {
		
		if(isDiagnosticaEnabled && isReportisticaEnabled) {
			return UtentiCostanti.LABEL_CONFIGURAZIONE_PDD_MONITOR_MONITORAGGIO_REPORTISTICA;
		}
		else if(isDiagnosticaEnabled) {
			return UtentiCostanti.LABEL_CONFIGURAZIONE_PDD_MONITOR_MONITORAGGIO;
		}
		else {
			return UtentiCostanti.LABEL_CONFIGURAZIONE_PDD_MONITOR_REPORTISTICA;
		}

	}
	
	public List<DataElement> addUtentiToDati(List<DataElement> dati,TipoOperazione tipoOperazione,boolean singlePdD,
			String nomesu,String pwsu,String confpwsu,InterfaceType interfaceType,
			String isServizi,String isDiagnostica,String isReportistica,String isSistema,String isMessaggi,String isUtenti,String isAuditing, String isAccordiCooperazione,
			String changepwd, String [] modalitaGateway,
			String isSoggettiAll, String isServiziAll, User oldImgUser, String scadenza, Date dataUltimoAggiornamentoPassword , boolean oldScadenza, 
			String profiloDefaultConsoleGestione, String soggettoDefaultConsoleGestione, 
			String profiloDefaultConsoleMonitoraggio, String soggettoDefaultConsoleMonitoraggio, String homePageMonitoraggio, String intervalloTemporaleReportStatistico) throws Exception{

		Boolean contaListe = ServletUtils.getContaListeFromSession(this.session);
		
		boolean onlyUser = ServletUtils.isCheckBoxEnabled(isUtenti) &&
				!ServletUtils.isCheckBoxEnabled(isServizi) &&
				!ServletUtils.isCheckBoxEnabled(isAccordiCooperazione) &&
				!ServletUtils.isCheckBoxEnabled(isDiagnostica) &&
				!ServletUtils.isCheckBoxEnabled(isReportistica) &&
				!ServletUtils.isCheckBoxEnabled(isSistema) &&
				!ServletUtils.isCheckBoxEnabled(isMessaggi) &&
				!ServletUtils.isCheckBoxEnabled(isAuditing);
		
		DataElement de = new DataElement();
		de.setLabel(UtentiCostanti.LABEL_INFORMAZIONI_UTENTE);
		de.setType(DataElementType.TITLE);
		dati.add(de);

		de = new DataElement();
		de.setLabel(UtentiCostanti.LABEL_PARAMETRO_UTENTI_USERNAME);
		de.setValue(nomesu);
		if(TipoOperazione.ADD.equals(tipoOperazione)){
			de.setType(DataElementType.TEXT_EDIT);
			de.setRequired(true);
		}
		else{
			de.setType(DataElementType.TEXT);
		}
		de.setName(UtentiCostanti.PARAMETRO_UTENTI_USERNAME);
		de.setSize(this.getSize());
		dati.add(de);
		
		
		de = new DataElement();
		de.setLabel(UtentiCostanti.LABEL_PERMESSI_GESTIONE);
		de.setType(DataElementType.TITLE);
		dati.add(de);
		
		de = new DataElement();
		de.setLabel(UtentiCostanti.LABEL_PARAMETRO_UTENTI_REGISTRO_SUBSECTION);
		de.setType(DataElementType.SUBTITLE);
		dati.add(de);

		de = new DataElement();
		de.setLabel(UtentiCostanti.LABEL_PARAMETRO_UTENTI_IS_SERVIZI);
		de.setType(DataElementType.CHECKBOX);
		de.setName(UtentiCostanti.PARAMETRO_UTENTI_IS_SERVIZI);
		ServletUtils.setCheckBox(de, isServizi);
		de.setPostBack(true);
		dati.add(de);
		
		de = new DataElement();
		de.setLabel(UtentiCostanti.LABEL_PARAMETRO_UTENTI_IS_ACCORDI_COOPERAZIONE);
		de.setName(UtentiCostanti.PARAMETRO_UTENTI_IS_ACCORDI_COOPERAZIONE);
		if(this.utentiCore.isAccordiCooperazioneEnabled()) {
			de.setType(DataElementType.CHECKBOX);
			de.setPostBack(true);
		}
		else {
			de.setType(DataElementType.HIDDEN);
		}
		ServletUtils.setCheckBox(de, isAccordiCooperazione);
		de.setValue(de.getSelected());
		dati.add(de);
			
		if(singlePdD) {
			de = new DataElement();
			de.setLabel(UtentiCostanti.LABEL_PARAMETRO_UTENTI_GOVWAY_MONITOR_SUBSECTION);
			de.setType(DataElementType.SUBTITLE);
			dati.add(de);
		}
		
		de = new DataElement();
		de.setLabel(UtentiCostanti.LABEL_PARAMETRO_UTENTI_IS_DIAGNOSTICA);
		if (singlePdD) {
			de.setType(DataElementType.CHECKBOX);
			ServletUtils.setCheckBox(de, isDiagnostica);
		} else {
			de.setType(DataElementType.HIDDEN);
			de.setValue("");
		}
		de.setName(UtentiCostanti.PARAMETRO_UTENTI_IS_DIAGNOSTICA);
		de.setPostBack(true);
		dati.add(de);
		
		de = new DataElement();
		de.setLabel(UtentiCostanti.LABEL_PARAMETRO_UTENTI_IS_REPORTISTICA);
		if (singlePdD) {
			de.setType(DataElementType.CHECKBOX);
			ServletUtils.setCheckBox(de, isReportistica);
		} else {
			de.setType(DataElementType.HIDDEN);
			de.setValue("");
		}
		de.setName(UtentiCostanti.PARAMETRO_UTENTI_IS_REPORTISTICA);
		de.setPostBack(true);
		dati.add(de);
		
		de = new DataElement();
		de.setLabel(UtentiCostanti.LABEL_PARAMETRO_UTENTI_STRUMENTI_SUBSECTION);
		de.setType(DataElementType.SUBTITLE);
		dati.add(de);

		de = new DataElement();
		de.setLabel(UtentiCostanti.LABEL_PARAMETRO_UTENTI_IS_MESSAGGI);
		de.setName(UtentiCostanti.PARAMETRO_UTENTI_IS_MESSAGGI);
		if(this.utentiCore.showCodaMessage()) {
			de.setType(DataElementType.CHECKBOX);
			de.setPostBack(true);
		}
		else {
			de.setType(DataElementType.HIDDEN);
		}
		ServletUtils.setCheckBox(de, isMessaggi);
		de.setValue(de.getSelected());
		dati.add(de);
		
		de = new DataElement();
		de.setLabel(UtentiCostanti.LABEL_PARAMETRO_UTENTI_IS_AUDITING);
		de.setType(DataElementType.CHECKBOX);
		de.setName(UtentiCostanti.PARAMETRO_UTENTI_IS_AUDITING);
		ServletUtils.setCheckBox(de, isAuditing);
		de.setPostBack(true);
		dati.add(de);
		
		de = new DataElement();
		de.setLabel(UtentiCostanti.LABEL_PARAMETRO_UTENTI_CONFIGURAZIONE_SUBSECTION);
		de.setType(DataElementType.SUBTITLE);
		dati.add(de);
		
		de = new DataElement();
		de.setLabel(UtentiCostanti.LABEL_PARAMETRO_UTENTI_IS_SISTEMA);
		de.setType(DataElementType.CHECKBOX);
		de.setName(UtentiCostanti.PARAMETRO_UTENTI_IS_SISTEMA);
		ServletUtils.setCheckBox(de, isSistema);
		de.setPostBack(true);
		dati.add(de);

		de = new DataElement();
		de.setLabel(UtentiCostanti.LABEL_PARAMETRO_UTENTI_IS_UTENTI);
		de.setType(DataElementType.CHECKBOX);
		de.setName(UtentiCostanti.PARAMETRO_UTENTI_IS_UTENTI);
		ServletUtils.setCheckBox(de, isUtenti);
		de.setPostBack(true);
		dati.add(de);
	
		
		
		if(!onlyUser) {
			de = new DataElement();
			de.setLabel(org.openspcoop2.core.constants.Costanti.LABEL_PARAMETRO_PROTOCOLLO_DI_HTML_ESCAPE);
			de.setType(DataElementType.TITLE);
			dati.add(de);
			
			List<String> protocolliRegistratiConsole = this.core.getProtocolli();
			for (int i = 0; i < protocolliRegistratiConsole.size() ; i++) {
				String protocolloName = protocolliRegistratiConsole.get(i);
				de = new DataElement();
				de.setLabel(this.getLabelProtocollo(protocolloName));
				de.setType(DataElementType.CHECKBOX);
				de.setName(UtentiCostanti.PARAMETRO_UTENTI_MODALITA_PREFIX + protocolloName);
				ServletUtils.setCheckBox(de, modalitaGateway[i]);
				de.setPostBack(true);
				dati.add(de);
			}
		}

		

		// Abilitazioni

		boolean isDiagnosticaEnabled = ServletUtils.isCheckBoxEnabled(isDiagnostica);
		boolean isReportisticaEnabled = ServletUtils.isCheckBoxEnabled(isReportistica);
		
		if(isDiagnosticaEnabled || isReportisticaEnabled) {
		
			de = new DataElement();
			de.setLabel(UtentiCostanti.LABEL_VISIBILITA_DATI_GOVWAY_MONITOR);
			de.setType(DataElementType.TITLE);
			dati.add(de);	
			

			if(this.utentiCore.isMultitenant()) {
				
				de = new DataElement();
				de.setLabel(UtentiCostanti.LABEL_UTENTI_SOGGETTI);
				de.setType(DataElementType.SUBTITLE);
				dati.add(de);
				
//				if(TipoOperazione.CHANGE.equals(tipoOperazione)) {
//					String valueSoggetti = getLabelSezionePddMonitorSoggettiServizi(isDiagnosticaEnabled, isReportisticaEnabled) + 
//							UtentiCostanti.LABEL_SUFFIX_RESTRIZIONE_SOGGETTI;
//					de = new DataElement();
//					de.setType(DataElementType.NOTE);
//					de.setValue(valueSoggetti);
//					dati.add(de);
//				}
				
				de = new DataElement();
				de.setType(DataElementType.CHECKBOX);
				de.setLabel(UtentiCostanti.LABEL_ABILITAZIONI_PUNTUALI_SOGGETTI_TUTTI);
				de.setName(UtentiCostanti.PARAMETRO_UTENTI_ABILITAZIONI_SOGGETTI_ALL);
				de.setSelected(ServletUtils.isCheckBoxEnabled(isSoggettiAll));
				de.setPostBack(true);
				dati.add(de);
				
				if(  TipoOperazione.CHANGE.equals(tipoOperazione) && oldImgUser.isPermitAllSoggetti()==false && !ServletUtils.isCheckBoxEnabled(isSoggettiAll)) {
					
					de = new DataElement();
					de.setType(DataElementType.LINK);
					de.setUrl(UtentiCostanti.SERVLET_NAME_UTENTI_SOGGETTI_LIST, new Parameter(UtentiCostanti.PARAMETRO_UTENTI_USERNAME, nomesu));
					String nomeLink = UtentiCostanti.LABEL_UTENTI_SOGGETTI;
					if(contaListe){
						ConsoleSearch searchForCount = new ConsoleSearch(true,1);
						this.utentiCore.utentiSoggettiList(nomesu, searchForCount);
						int num = searchForCount.getNumEntries(Liste.UTENTI_SOGGETTI);
						ServletUtils.setDataElementCustomLabel(de, nomeLink, (long) num);
					}else {
						de.setValue(nomeLink);
					}
					dati.add(de);
			
				}
			}
			else {
				
				de = new DataElement();
				de.setType(DataElementType.HIDDEN);
				de.setName(UtentiCostanti.PARAMETRO_UTENTI_ABILITAZIONI_SOGGETTI_ALL);
				de.setValue(isSoggettiAll);
				dati.add(de);
				
			}
			
				
			de = new DataElement();
			de.setLabel(UtentiCostanti.LABEL_UTENTI_SERVIZI);
			de.setType(DataElementType.SUBTITLE);
			dati.add(de);
			
//			if(TipoOperazione.CHANGE.equals(tipoOperazione)) {
//				String valueServizi = getLabelSezionePddMonitorSoggettiServizi(isDiagnosticaEnabled, isReportisticaEnabled)+ 
//						UtentiCostanti.LABEL_SUFFIX_RESTRIZIONE_API;
//				de = new DataElement();
//				de.setType(DataElementType.NOTE);
//				de.setValue(valueServizi);
//				dati.add(de);
//			}
			
			de = new DataElement();
			de.setType(DataElementType.CHECKBOX);
			de.setLabel(UtentiCostanti.LABEL_ABILITAZIONI_PUNTUALI_SERVIZI_TUTTI);
			de.setName(UtentiCostanti.PARAMETRO_UTENTI_ABILITAZIONI_SERVIZI_ALL);
			de.setSelected(ServletUtils.isCheckBoxEnabled(isServiziAll));
			de.setPostBack(true);
			dati.add(de);
			
			if((TipoOperazione.CHANGE.equals(tipoOperazione) && oldImgUser.isPermitAllServizi()==false && !ServletUtils.isCheckBoxEnabled(isServiziAll))) {
				
				de = new DataElement();
				de.setType(DataElementType.LINK);
				de.setUrl(UtentiCostanti.SERVLET_NAME_UTENTI_SERVIZI_LIST, new Parameter(UtentiCostanti.PARAMETRO_UTENTI_USERNAME, nomesu));
				String nomeLink = UtentiCostanti.LABEL_UTENTI_SERVIZI;
				if(contaListe){
					ConsoleSearch searchForCount = new ConsoleSearch(true,1);
					this.utentiCore.utentiServiziList(nomesu, searchForCount);
					int num = searchForCount.getNumEntries(Liste.UTENTI_SERVIZI);
					ServletUtils.setDataElementCustomLabel(de, nomeLink, (long) num);
				}else {
					de.setValue(nomeLink);
				}
				dati.add(de);
				
			}
			
		}
		
		
		boolean utenteConsoleEnabled = 
				ServletUtils.isCheckBoxEnabled(isServizi) ||
				ServletUtils.isCheckBoxEnabled(isMessaggi) ||
				ServletUtils.isCheckBoxEnabled(isAuditing) ||
				ServletUtils.isCheckBoxEnabled(isSistema) ||
				ServletUtils.isCheckBoxEnabled(isUtenti) ||				
				ServletUtils.isCheckBoxEnabled(isAccordiCooperazione);
		
		boolean utenteMonitorEnabled = isDiagnosticaEnabled || isReportisticaEnabled;
		
		if(utenteConsoleEnabled || utenteMonitorEnabled) {
		
			de = new DataElement();
			de.setLabel(UtentiCostanti.LABEL_PROFILO_UTENTE);
			de.setType(DataElementType.TITLE);
			dati.add(de);
			
			// se ho selezionato almento un procollo mostro la selezione del protocollo per il profilo utente
			boolean protocolloSelezionato = false;
			
			for (String modalitaI : modalitaGateway) {
				if(ServletUtils.isCheckBoxEnabled(modalitaI)) {
					protocolloSelezionato = true;
					break;
				}
			}
	
			if(utenteConsoleEnabled) {
			
				de = new DataElement();
				de.setLabel(UtentiCostanti.LABEL_PROFILO_UTENTE_CONSOLE_GESTIONE);
				de.setType(DataElementType.SUBTITLE);
				dati.add(de);
				
				de = new DataElement();
				de.setLabel(UtentiCostanti.LABEL_MODALITA_INTERFACCIA);
				de.setName(UtentiCostanti.PARAMETRO_UTENTI_TIPO_GUI);
				boolean permitInterfaceComplete = false;
				boolean showSelectTipoInterfaccia = true;
				if(TipoOperazione.CHANGE.equals(tipoOperazione)) {
					User user = this.utentiCore.getUser(nomesu);
					permitInterfaceComplete = user.isPermitInterfaceComplete();
					showSelectTipoInterfaccia = !interfaceType.equals(InterfaceType.COMPLETA);
				}
				
				if(showSelectTipoInterfaccia) {
					de.setType(DataElementType.SELECT);
					String[] tipiInterfacce=null;
					String[] tipiInterfacceLabel=null;
					if(permitInterfaceComplete) {
						tipiInterfacce = new String[3];			
					}
					else {
						tipiInterfacce = new String[2];
					}
					tipiInterfacce[0]=InterfaceType.STANDARD.toString();
					tipiInterfacce[1]=InterfaceType.AVANZATA.toString();
					if(permitInterfaceComplete) {
						tipiInterfacce[2]=InterfaceType.COMPLETA.toString();
					}
					tipiInterfacceLabel = new String[tipiInterfacce.length];
					for (int i = 0; i < tipiInterfacce.length; i++) {
						tipiInterfacceLabel[i] = tipiInterfacce[i].toLowerCase();
					}
					de.setValues(tipiInterfacce);
					de.setLabels(tipiInterfacceLabel);
					de.setSelected(interfaceType.toString());
					dati.add(de);
				} else {
					de.setType(DataElementType.HIDDEN);
					de.setValue(interfaceType.toString());
					dati.add(de);
					
					de = new DataElement();
					de.setLabel(UtentiCostanti.LABEL_MODALITA_INTERFACCIA);
					de.setName(UtentiCostanti.PARAMETRO_UTENTI_TIPO_GUI+ "_text");
					de.setType(DataElementType.TEXT);
					de.setValue(interfaceType.toString().toLowerCase());
					dati.add(de);
				}
				
				if(!onlyUser) { // selezione di modalita' e soggetto solo se non ho selezionato solo la gestione utenti.
					if(protocolloSelezionato) {
						// select list Profilo Interoperabilità
						List<String> protocolliRegistratiConsole = this.core.getProtocolli();
						
						List<String> profiloValues = new ArrayList<>();
						List<String> profiloLabels = new ArrayList<>();
						for (int i = 0; i < protocolliRegistratiConsole.size() ; i++) {
							String protocolloName = protocolliRegistratiConsole.get(i);
							if(ServletUtils.isCheckBoxEnabled(modalitaGateway[i])) {
								profiloValues.add(protocolloName);
								String labelProt = ConsoleHelper._getLabelProtocollo(protocolloName);
								profiloLabels.add(labelProt);
							}
						}
						
						if(profiloValues.size() > 1) { // select
							de = new DataElement();
							de.setType(DataElementType.SELECT);		
							de.setLabel(org.openspcoop2.core.constants.Costanti.LABEL_PARAMETRO_PROTOCOLLO_HTML_ESCAPE);
							
							profiloLabels.add(0, UtentiCostanti.LABEL_PARAMETRO_MODALITA_ALL);
							profiloValues.add(0,UtentiCostanti.VALORE_PARAMETRO_MODALITA_ALL);
							de.setName(UtentiCostanti.PARAMETRO_UTENTE_TIPO_MODALITA);
							de.setValues(profiloValues);
							de.setLabels(profiloLabels);
							
							de.setSelected(profiloDefaultConsoleGestione);
							de.setPostBack(true);
							dati.add(de);
							
						} else { // text
							de = new DataElement();
							de.setLabel(org.openspcoop2.core.constants.Costanti.LABEL_PARAMETRO_PROTOCOLLO_HTML_ESCAPE);
							de.setType(DataElementType.HIDDEN);			
							de.setName(UtentiCostanti.PARAMETRO_UTENTE_TIPO_MODALITA);
							de.setValue(profiloDefaultConsoleGestione);
							dati.add(de);
							
							de = new DataElement();
							de.setLabel(org.openspcoop2.core.constants.Costanti.LABEL_PARAMETRO_PROTOCOLLO_HTML_ESCAPE);
							de.setType(DataElementType.TEXT);			
							de.setName(UtentiCostanti.PARAMETRO_UTENTE_TIPO_MODALITA+ "_txt");
							
							String protocolloName = profiloValues.get(0);
							String labelProt = ConsoleHelper._getLabelProtocollo(protocolloName);
							de.setValue(labelProt);
			//				de.setValue(profiloDefaultUtente.equals(UtentiCostanti.VALORE_PARAMETRO_MODALITA_ALL) ? UtentiCostanti.LABEL_PARAMETRO_MODALITA_ALL : profiloDefaultUtente);
							dati.add(de);
						}
						
						// select list soggetti
						if((profiloDefaultConsoleGestione!=null && !UtentiCostanti.VALORE_PARAMETRO_MODALITA_ALL.equals(profiloDefaultConsoleGestione)) ||
								profiloValues.size() == 1) {
							
							String profiloDefaultCorrente = (profiloValues.size() == 1) ? profiloValues.get(0) : profiloDefaultConsoleGestione;
							
							List<IDSoggetto> idSoggettiOperativi = this.soggettiCore.getIdSoggettiOperativi(profiloDefaultCorrente);
							
							// se ho selezionato un profilo e ho almeno due soggetti visualizzo la tendina
							if(idSoggettiOperativi != null && idSoggettiOperativi.size()>1) {
								List<String> listaLabel = new ArrayList<>();
								Map<String, IDSoggetto> mapLabelIds = new HashMap<>();
								for (IDSoggetto idSoggetto : idSoggettiOperativi) {
									String labelSoggetto = ConsoleHelper._getLabelNomeSoggetto(idSoggetto);
									if(!listaLabel.contains(labelSoggetto)) {
										listaLabel.add(labelSoggetto);
										mapLabelIds.put(labelSoggetto, idSoggetto);
									}
								}
								
								// Per ordinare in maniera case insensistive
								Collections.sort(listaLabel, new Comparator<String>() {
									 @Override
									public int compare(String o1, String o2) {
								           return o1.toLowerCase().compareTo(o2.toLowerCase());
								        }
									});
								
								List<String> listaValues = new ArrayList<>();
								
								for (String label : listaLabel) {
									listaValues.add(NamingUtils.getSoggettoFromLabel(profiloDefaultCorrente, label).toString());
								}
								
								de = new DataElement();
								de.setType(DataElementType.SELECT);
								de.setLabel(UtentiCostanti.LABEL_PARAMETRO_SOGGETTO_OPERATIVO);
								listaLabel.add(0, UtentiCostanti.LABEL_PARAMETRO_MODALITA_ALL);
								listaValues.add(0,UtentiCostanti.VALORE_PARAMETRO_MODALITA_ALL);
								de.setValues(listaValues);
								de.setLabels(listaLabel);
								de.setName(UtentiCostanti.PARAMETRO_UTENTE_ID_SOGGETTO);
								de.setSelected(soggettoDefaultConsoleGestione);
								dati.add(de);
								
							} else { // se ho un solo soggetto visualizzo il text
								de = new DataElement();
								de.setLabel(UtentiCostanti.LABEL_PARAMETRO_SOGGETTO_OPERATIVO);
								de.setType(DataElementType.HIDDEN);
								de.setName(UtentiCostanti.PARAMETRO_UTENTE_ID_SOGGETTO);
								de.setValue(soggettoDefaultConsoleGestione);
								dati.add(de);
								
								de = new DataElement();
								de.setLabel(UtentiCostanti.LABEL_PARAMETRO_SOGGETTO_OPERATIVO);
								de.setType(DataElementType.TEXT);
								de.setName(UtentiCostanti.PARAMETRO_UTENTE_ID_SOGGETTO+ "_txt");
								
								if(idSoggettiOperativi!=null) {
									IDSoggetto idSoggetto = idSoggettiOperativi.get(0);
									String labelSoggetto = ConsoleHelper._getLabelNomeSoggetto(idSoggetto);
									de.setValue(labelSoggetto);
								}
			//					de.setValue(soggettoDefaultUtente.equals(UtentiCostanti.VALORE_PARAMETRO_MODALITA_ALL) ? UtentiCostanti.LABEL_PARAMETRO_MODALITA_ALL : soggettoDefaultUtente);
								dati.add(de);
							}
							
						} else {
							de = new DataElement();
							de.setLabel(UtentiCostanti.LABEL_PARAMETRO_SOGGETTO_OPERATIVO);
							de.setType(DataElementType.HIDDEN);
							de.setName(UtentiCostanti.PARAMETRO_UTENTE_ID_SOGGETTO);
							de.setValue(soggettoDefaultConsoleGestione);
							dati.add(de); 
						}
					} else {
						de = new DataElement();
						de.setLabel(org.openspcoop2.core.constants.Costanti.LABEL_PARAMETRO_PROTOCOLLO_HTML_ESCAPE);
						de.setType(DataElementType.HIDDEN);			
						de.setName(UtentiCostanti.PARAMETRO_UTENTE_TIPO_MODALITA);
						de.setValue(profiloDefaultConsoleGestione);
						dati.add(de);
						
						de = new DataElement();
						de.setLabel(UtentiCostanti.LABEL_PARAMETRO_SOGGETTO_OPERATIVO);
						de.setType(DataElementType.HIDDEN);
						de.setName(UtentiCostanti.PARAMETRO_UTENTE_ID_SOGGETTO);
						de.setValue(soggettoDefaultConsoleGestione);
						dati.add(de);
					}
				}
			}
			
			// profilo e soggetti govwayMonitor
			if(utenteMonitorEnabled) {
				de = new DataElement();
				de.setLabel(UtentiCostanti.LABEL_PROFILO_UTENTE_CONSOLE_MONITOR);
				de.setType(DataElementType.SUBTITLE);
				dati.add(de);
				
				if(protocolloSelezionato) {
					// select list Profilo Interoperabilità
					List<String> protocolliRegistratiConsole = this.core.getProtocolli();
					
					List<String> profiloValues = new ArrayList<>();
					List<String> profiloLabels = new ArrayList<>();
					for (int i = 0; i < protocolliRegistratiConsole.size() ; i++) {
						String protocolloName = protocolliRegistratiConsole.get(i);
						if(ServletUtils.isCheckBoxEnabled(modalitaGateway[i])) {
							profiloValues.add(protocolloName);
							String labelProt = ConsoleHelper._getLabelProtocollo(protocolloName);
							profiloLabels.add(labelProt);
						}
					}
					
					if(profiloValues.size() > 1) { // select
						de = new DataElement();
						de.setType(DataElementType.SELECT);		
						de.setLabel(org.openspcoop2.core.constants.Costanti.LABEL_PARAMETRO_PROTOCOLLO_HTML_ESCAPE);
						
						profiloLabels.add(0, UtentiCostanti.LABEL_PARAMETRO_MODALITA_ALL);
						profiloValues.add(0,UtentiCostanti.VALORE_PARAMETRO_MODALITA_ALL);
						de.setName(UtentiCostanti.PARAMETRO_UTENTE_TIPO_MODALITA_MONITOR);
						de.setValues(profiloValues);
						de.setLabels(profiloLabels);
						
						de.setSelected(profiloDefaultConsoleMonitoraggio);
						de.setPostBack(true);
						dati.add(de);
						
					} else { // text
						de = new DataElement();
						de.setLabel(org.openspcoop2.core.constants.Costanti.LABEL_PARAMETRO_PROTOCOLLO_HTML_ESCAPE);
						de.setType(DataElementType.HIDDEN);			
						de.setName(UtentiCostanti.PARAMETRO_UTENTE_TIPO_MODALITA_MONITOR);
						de.setValue(profiloDefaultConsoleMonitoraggio);
						dati.add(de);
						
						de = new DataElement();
						de.setLabel(org.openspcoop2.core.constants.Costanti.LABEL_PARAMETRO_PROTOCOLLO_HTML_ESCAPE);
						de.setType(DataElementType.TEXT);			
						de.setName(UtentiCostanti.PARAMETRO_UTENTE_TIPO_MODALITA_MONITOR+ "_txt");
						
						String protocolloName = profiloValues.get(0);
						String labelProt = ConsoleHelper._getLabelProtocollo(protocolloName);
						de.setValue(labelProt);
	//					de.setValue(profiloDefaultUtente.equals(UtentiCostanti.VALORE_PARAMETRO_MODALITA_ALL) ? UtentiCostanti.LABEL_PARAMETRO_MODALITA_ALL : profiloDefaultUtente);
						dati.add(de);
					}
					
					// select list soggetti
					if((profiloDefaultConsoleMonitoraggio!=null && !UtentiCostanti.VALORE_PARAMETRO_MODALITA_ALL.equals(profiloDefaultConsoleMonitoraggio)) ||
							profiloValues.size() == 1) {
						
						String profiloDefaultCorrente = (profiloValues.size() == 1) ? profiloValues.get(0) : profiloDefaultConsoleMonitoraggio;
						
						List<IDSoggetto> idSoggettiOperativi = this.soggettiCore.getIdSoggettiOperativi(profiloDefaultCorrente);
						
						// se ho selezionato un profilo e ho almeno due soggetti visualizzo la tendina
						if(idSoggettiOperativi != null && idSoggettiOperativi.size()>1) {
							List<String> listaLabel = new ArrayList<>();
							Map<String, IDSoggetto> mapLabelIds = new HashMap<>();
							for (IDSoggetto idSoggetto : idSoggettiOperativi) {
								String labelSoggetto = ConsoleHelper._getLabelNomeSoggetto(idSoggetto);
								if(!listaLabel.contains(labelSoggetto)) {
									listaLabel.add(labelSoggetto);
									mapLabelIds.put(labelSoggetto, idSoggetto);
								}
							}
							
							// Per ordinare in maniera case insensistive
							Collections.sort(listaLabel, new Comparator<String>() {
								 @Override
								public int compare(String o1, String o2) {
							           return o1.toLowerCase().compareTo(o2.toLowerCase());
							        }
								});
							
							List<String> listaValues = new ArrayList<>();
							
							for (String label : listaLabel) {
								listaValues.add(NamingUtils.getSoggettoFromLabel(profiloDefaultCorrente, label).toString());
							}
							
							de = new DataElement();
							de.setType(DataElementType.SELECT);
							de.setLabel(UtentiCostanti.LABEL_PARAMETRO_SOGGETTO_OPERATIVO);
							listaLabel.add(0, UtentiCostanti.LABEL_PARAMETRO_MODALITA_ALL);
							listaValues.add(0,UtentiCostanti.VALORE_PARAMETRO_MODALITA_ALL);
							de.setValues(listaValues);
							de.setLabels(listaLabel);
							de.setName(UtentiCostanti.PARAMETRO_UTENTE_ID_SOGGETTO_MONITOR);
							de.setSelected(soggettoDefaultConsoleMonitoraggio);
							dati.add(de);
							
						} else { // se ho un solo soggetto visualizzo il text
							de = new DataElement();
							de.setLabel(UtentiCostanti.LABEL_PARAMETRO_SOGGETTO_OPERATIVO);
							de.setType(DataElementType.HIDDEN);
							de.setName(UtentiCostanti.PARAMETRO_UTENTE_ID_SOGGETTO_MONITOR);
							de.setValue(soggettoDefaultConsoleMonitoraggio);
							dati.add(de);
							
							de = new DataElement();
							de.setLabel(UtentiCostanti.LABEL_PARAMETRO_SOGGETTO_OPERATIVO);
							de.setType(DataElementType.TEXT);
							de.setName(UtentiCostanti.PARAMETRO_UTENTE_ID_SOGGETTO_MONITOR+ "_txt");
							
							if(idSoggettiOperativi!=null) {
								IDSoggetto idSoggetto = idSoggettiOperativi.get(0);
								String labelSoggetto = ConsoleHelper._getLabelNomeSoggetto(idSoggetto);
								de.setValue(labelSoggetto);
							}
	//						de.setValue(soggettoDefaultUtente.equals(UtentiCostanti.VALORE_PARAMETRO_MODALITA_ALL) ? UtentiCostanti.LABEL_PARAMETRO_MODALITA_ALL : soggettoDefaultUtente);
							dati.add(de);
						}
						
					} else {
						de = new DataElement();
						de.setLabel(UtentiCostanti.LABEL_PARAMETRO_SOGGETTO_OPERATIVO);
						de.setType(DataElementType.HIDDEN);
						de.setName(UtentiCostanti.PARAMETRO_UTENTE_ID_SOGGETTO_MONITOR);
						de.setValue(soggettoDefaultConsoleMonitoraggio);
						dati.add(de); 
					}
				} else {
					de = new DataElement();
					de.setLabel(org.openspcoop2.core.constants.Costanti.LABEL_PARAMETRO_PROTOCOLLO_HTML_ESCAPE);
					de.setType(DataElementType.HIDDEN);			
					de.setName(UtentiCostanti.PARAMETRO_UTENTE_TIPO_MODALITA_MONITOR);
					de.setValue(profiloDefaultConsoleMonitoraggio);
					dati.add(de);
					
					de = new DataElement();
					de.setLabel(UtentiCostanti.LABEL_PARAMETRO_SOGGETTO_OPERATIVO);
					de.setType(DataElementType.HIDDEN);
					de.setName(UtentiCostanti.PARAMETRO_UTENTE_ID_SOGGETTO_MONITOR);
					de.setValue(soggettoDefaultConsoleMonitoraggio);
					dati.add(de);
				}
				
				// Home page console di monitoraggio
				de = new DataElement();
				de.setType(DataElementType.SELECT);
				de.setLabel(UtentiCostanti.LABEL_PARAMETRO_UTENTI_HOME_PAGE_MONITORAGGIO);
				String[] homePageLabels = { UtentiCostanti.LABEL_VALUE_PARAMETRO_UTENTI_HOME_PAGE_MONITORAGGIO_TRANSAZIONI, UtentiCostanti.LABEL_VALUE_PARAMETRO_UTENTI_HOME_PAGE_MONITORAGGIO_STATISTICHE };
				de.setValues(UtentiCostanti.getValuesParametroUtentiHomePageMonitoraggio());
				de.setLabels(homePageLabels);
				de.setName(UtentiCostanti.PARAMETRO_UTENTI_HOME_PAGE_MONITORAGGIO);
				de.setPostBack(true);
				de.setSelected(homePageMonitoraggio);
				dati.add(de);
				
				// select per la selezione dell'intervallo temporale del grafico della home
				if(homePageMonitoraggio.equals(UtentiCostanti.VALUE_PARAMETRO_UTENTI_HOME_PAGE_MONITORAGGIO_STATISTICHE)) {
					de = new DataElement();
					de.setType(DataElementType.SELECT);
					de.setLabel(UtentiCostanti.LABEL_PARAMETRO_UTENTI_INTERVALLO_TEMPORALE_HOME_PAGE_MONITORAGGIO);
					String[] intervalloTemporaleLabels = { 
							UtentiCostanti.LABEL_PARAMETRO_UTENTI_INTERVALLO_TEMPORALE_HOME_PAGE_MONITORAGGIO_NO_GRAFICO, 
							UtentiCostanti.LABEL_PARAMETRO_UTENTI_INTERVALLO_TEMPORALE_HOME_PAGE_MONITORAGGIO_ULTIME_24_ORE, 
							UtentiCostanti.LABEL_PARAMETRO_UTENTI_INTERVALLO_TEMPORALE_HOME_PAGE_MONITORAGGIO_ULTIMI_7_GIORNI, 
							UtentiCostanti.LABEL_PARAMETRO_UTENTI_INTERVALLO_TEMPORALE_HOME_PAGE_MONITORAGGIO_ULTIMI_30_GIORNI, 
							UtentiCostanti.LABEL_PARAMETRO_UTENTI_INTERVALLO_TEMPORALE_HOME_PAGE_MONITORAGGIO_ULTIMO_ANNO };
					de.setValues(UtentiCostanti.getValuesParametroUtentiIntervalloTemporaleHomePageMonitoraggio());
					de.setLabels(intervalloTemporaleLabels);
					de.setName(UtentiCostanti.PARAMETRO_UTENTI_INTERVALLO_TEMPORALE_HOME_PAGE_MONITORAGGIO);
					de.setSelected(intervalloTemporaleReportStatistico);
					dati.add(de);
				} else {
					de = new DataElement();
					de.setLabel(UtentiCostanti.LABEL_PARAMETRO_UTENTI_INTERVALLO_TEMPORALE_HOME_PAGE_MONITORAGGIO);
					de.setType(DataElementType.HIDDEN);			
					de.setName(UtentiCostanti.PARAMETRO_UTENTI_INTERVALLO_TEMPORALE_HOME_PAGE_MONITORAGGIO);
					de.setValue(intervalloTemporaleReportStatistico);
					dati.add(de);
				}
				
			} else {
				de = new DataElement();
				de.setLabel(org.openspcoop2.core.constants.Costanti.LABEL_PARAMETRO_PROTOCOLLO_HTML_ESCAPE);
				de.setType(DataElementType.HIDDEN);			
				de.setName(UtentiCostanti.PARAMETRO_UTENTE_TIPO_MODALITA_MONITOR);
				de.setValue(profiloDefaultConsoleMonitoraggio);
				dati.add(de);
				
				de = new DataElement();
				de.setLabel(UtentiCostanti.LABEL_PARAMETRO_SOGGETTO_OPERATIVO);
				de.setType(DataElementType.HIDDEN);
				de.setName(UtentiCostanti.PARAMETRO_UTENTE_ID_SOGGETTO_MONITOR);
				de.setValue(soggettoDefaultConsoleMonitoraggio);
				dati.add(de);
				
				de = new DataElement();
				de.setLabel(UtentiCostanti.LABEL_PARAMETRO_UTENTI_HOME_PAGE_MONITORAGGIO);
				de.setType(DataElementType.HIDDEN);			
				de.setName(UtentiCostanti.PARAMETRO_UTENTI_HOME_PAGE_MONITORAGGIO);
				de.setValue(homePageMonitoraggio);
				dati.add(de);
				
				de = new DataElement();
				de.setLabel(UtentiCostanti.LABEL_PARAMETRO_UTENTI_INTERVALLO_TEMPORALE_HOME_PAGE_MONITORAGGIO);
				de.setType(DataElementType.HIDDEN);			
				de.setName(UtentiCostanti.PARAMETRO_UTENTI_INTERVALLO_TEMPORALE_HOME_PAGE_MONITORAGGIO);
				de.setValue(intervalloTemporaleReportStatistico);
				dati.add(de);
			}
		} else {
			de = new DataElement();
			de.setLabel(org.openspcoop2.core.constants.Costanti.LABEL_PARAMETRO_PROTOCOLLO_HTML_ESCAPE);
			de.setType(DataElementType.HIDDEN);			
			de.setName(UtentiCostanti.PARAMETRO_UTENTE_TIPO_MODALITA_MONITOR);
			de.setValue(profiloDefaultConsoleMonitoraggio);
			dati.add(de);
			
			de = new DataElement();
			de.setLabel(UtentiCostanti.LABEL_PARAMETRO_SOGGETTO_OPERATIVO);
			de.setType(DataElementType.HIDDEN);
			de.setName(UtentiCostanti.PARAMETRO_UTENTE_ID_SOGGETTO_MONITOR);
			de.setValue(soggettoDefaultConsoleMonitoraggio);
			dati.add(de);
			
			de = new DataElement();
			de.setLabel(UtentiCostanti.LABEL_PARAMETRO_UTENTI_HOME_PAGE_MONITORAGGIO);
			de.setType(DataElementType.HIDDEN);			
			de.setName(UtentiCostanti.PARAMETRO_UTENTI_HOME_PAGE_MONITORAGGIO);
			de.setValue(homePageMonitoraggio);
			dati.add(de);
			
			de = new DataElement();
			de.setLabel(UtentiCostanti.LABEL_PARAMETRO_UTENTI_INTERVALLO_TEMPORALE_HOME_PAGE_MONITORAGGIO);
			de.setType(DataElementType.HIDDEN);			
			de.setName(UtentiCostanti.PARAMETRO_UTENTI_INTERVALLO_TEMPORALE_HOME_PAGE_MONITORAGGIO);
			de.setValue(intervalloTemporaleReportStatistico);
			dati.add(de);
		}
		
		
		
		boolean loginApplication = this.core.isLoginApplication();
		
		if(loginApplication) {
			de = new DataElement();
			de.setLabel(UtentiCostanti.LABEL_PASSWORD);
			de.setType(DataElementType.TITLE);
			dati.add(de);
		}
		
		PasswordVerifier passwordVerifier = this.utentiCore.getUtenzePasswordVerifier();
		
		// se e' abilitato il controllo sulla scadenza della password visualizzo la checkbox di abilitazione scadenza sul singolo utente
		if(this.utentiCore.isCheckPasswordExpire(passwordVerifier)) {
			de = new DataElement();
			de.setLabel(UtentiCostanti.LABEL_PARAMETRO_UTENTI_SCADENZA);
			de.setType(DataElementType.CHECKBOX);
			de.setName(UtentiCostanti.PARAMETRO_UTENTI_SCADENZA);
			de.setSelected(scadenza);
			de.setSize(this.getSize());
			
			if(TipoOperazione.ADD.equals(tipoOperazione)) {
				de.setLabelAffiancata(true);
				de.setNote(MessageFormat.format(UtentiCostanti.LABEL_NOTA_UTENTI_SCADENZA_ADD, passwordVerifier.getExpireDays()));
			}
			
			if(TipoOperazione.CHANGE.equals(tipoOperazione)) {
				de.setLabelAffiancata(true);
				boolean passwordExpire = passwordVerifier.isPasswordExpire(dataUltimoAggiornamentoPassword);
				
				// se l'utente e' stato configurato con controllo scadenza password visualizzo il messaggio di stato attuale della password
				if(oldScadenza) {
					if(passwordExpire) {
						de.setNote(UtentiCostanti.LABEL_UTENTI_SCADENZA_PW_SCADUTA);
						de.setValoreBoldRed();
					} else {
						long giorni = 0;
						Calendar c = Calendar.getInstance();
						c.setTime(dataUltimoAggiornamentoPassword);
						c.add(Calendar.DATE, passwordVerifier.getExpireDays());
						Date end = c.getTime();
						Date now = new Date();
						long endMs = end.getTime();
						long nowMs = now.getTime();
						long diff = endMs - nowMs;
						giorni = (diff / 1000 / 60 / 60 / 24);
						
						de.setNote(MessageFormat.format(UtentiCostanti.LABEL_NOTA_UTENTI_SCADENZA_CHANGE, giorni));
					}
				} 
			}
			
			dati.add(de);
		}

		if(loginApplication && TipoOperazione.CHANGE.equals(tipoOperazione)){
			de = new DataElement();
			de.setLabel(UtentiCostanti.LABEL_MODIFICA);
			de.setType(DataElementType.CHECKBOX);
			//			de.setOnClick("cambiaPassword(\"suChange\");");
			de.setName(UtentiCostanti.PARAMETRO_UTENTI_CHANGE_PW);
			de.setPostBack(true);
			de.setSelected(changepwd);
			de.setSize(this.getSize());
			dati.add(de);
		}

		if( (TipoOperazione.ADD.equals(tipoOperazione)) || (ServletUtils.isCheckBoxEnabled(changepwd)) ){
			de = new DataElement();
			de.setLabel(UtentiCostanti.LABEL_PARAMETRO_UTENTI_PW);
			if(loginApplication) {
				de.setValue(pwsu);
				de.setType(DataElementType.CRYPT);
				de.getPassword().setVisualizzaPasswordChiaro(true);
				de.getPassword().setVisualizzaBottoneGeneraPassword(true);
				if(passwordVerifier != null) {
					PasswordGenerator passwordGenerator = new PasswordGenerator(passwordVerifier);
					passwordGenerator.setDefaultLength(this.utentiCore.getUtenzeLunghezzaPasswordGenerate());
					de.getPassword().setPasswordGenerator(passwordGenerator);
					// stesso messaggio in add e change perche' l'amministratore puo' impostare anche password ripetute
					de.setNote(passwordVerifier.help(org.openspcoop2.core.constants.Costanti.WEB_NEW_LINE));
				}
				de.setRequired(true);
			} else {
				de.setType(DataElementType.HIDDEN);
				de.setValue(UtentiCostanti.VALORE_PARAMETRO_PW_MODALITA_NO_LOGIN_APPLICATION);
			}
			de.setName(UtentiCostanti.PARAMETRO_UTENTI_PW);
			de.setSize(this.getSize());
			dati.add(de);
		}
		
		
		return dati;

	}


	public void addChangeUtenteInfoToDati(List<DataElement> dati,
			String nomesu,String changepwd,String pwsu,String confpwsu,InterfaceType interfaceType,
			String isServizi,String isDiagnostica,String isReportistica, String isSistema,String isMessaggi,String isUtenti,String isAuditing, String isAccordiCooperazione,
			boolean scegliSuServizi,
			String [] uws, boolean scegliSuAccordi,String [] uwp, String [] modalitaGateway, 
			String profiloDefaultConsoleGestione, String soggettoDefaultConsoleGestione, 
			String profiloDefaultConsoleMonitoraggio, String soggettoDefaultConsoleMonitoraggio, String homePageMonitoraggio, String intervalloTemporaleReportStatistico) throws Exception{ 

		DataElement de = new DataElement();
		de.setLabel(UtentiCostanti.LABEL_PARAMETRO_UTENTI_USERNAME);
		de.setValue(nomesu);
		de.setType(DataElementType.HIDDEN);
		de.setName(UtentiCostanti.PARAMETRO_UTENTI_USERNAME);
		dati.add(de);

		if(ServletUtils.isCheckBoxEnabled(changepwd)){

			de = new DataElement();
			de.setLabel(UtentiCostanti.LABEL_PARAMETRO_UTENTI_PW);
			de.setValue(pwsu);
			de.setType(DataElementType.HIDDEN);
			de.setName(UtentiCostanti.PARAMETRO_UTENTI_PW);
			dati.add(de);

			de = new DataElement();
			de.setLabel(UtentiCostanti.LABEL_PARAMETRO_UTENTI_CONFERMA_PW);
			de.setValue(confpwsu);
			de.setType(DataElementType.HIDDEN);
			de.setName(UtentiCostanti.PARAMETRO_UTENTI_CONFERMA_PW);
			dati.add(de);

			de = new DataElement();
			de.setLabel(UtentiCostanti.LABEL_PARAMETRO_UTENTI_CHANGE_PW);
			de.setValue(changepwd);
			de.setType(DataElementType.HIDDEN);
			de.setName(UtentiCostanti.PARAMETRO_UTENTI_CHANGE_PW);
			dati.add(de);
		}

		de = new DataElement();
		de.setLabel(org.openspcoop2.core.constants.Costanti.LABEL_PARAMETRO_PROTOCOLLO_HTML_ESCAPE);
		de.setType(DataElementType.HIDDEN);			
		de.setName(UtentiCostanti.PARAMETRO_UTENTE_TIPO_MODALITA);
		de.setValue(profiloDefaultConsoleGestione);
		dati.add(de);
		
		de = new DataElement();
		de.setLabel(UtentiCostanti.LABEL_PARAMETRO_SOGGETTO_OPERATIVO);
		de.setType(DataElementType.HIDDEN);
		de.setName(UtentiCostanti.PARAMETRO_UTENTE_ID_SOGGETTO);
		de.setValue(soggettoDefaultConsoleGestione);
		dati.add(de);

		de = new DataElement();
		de.setLabel(UtentiCostanti.LABEL_MODALITA_INTERFACCIA);
		de.setValue(interfaceType.toString());
		de.setType(DataElementType.HIDDEN);
		de.setName(UtentiCostanti.PARAMETRO_UTENTI_TIPO_GUI);
		dati.add(de);
		
		de = new DataElement();
		de.setLabel(org.openspcoop2.core.constants.Costanti.LABEL_PARAMETRO_PROTOCOLLO_HTML_ESCAPE);
		de.setType(DataElementType.HIDDEN);			
		de.setName(UtentiCostanti.PARAMETRO_UTENTE_TIPO_MODALITA_MONITOR);
		de.setValue(profiloDefaultConsoleMonitoraggio);
		dati.add(de);
		
		de = new DataElement();
		de.setLabel(UtentiCostanti.LABEL_PARAMETRO_SOGGETTO_OPERATIVO);
		de.setType(DataElementType.HIDDEN);
		de.setName(UtentiCostanti.PARAMETRO_UTENTE_ID_SOGGETTO_MONITOR);
		de.setValue(soggettoDefaultConsoleMonitoraggio);
		dati.add(de);
		
		de = new DataElement();
		de.setLabel(UtentiCostanti.LABEL_PARAMETRO_UTENTI_HOME_PAGE_MONITORAGGIO);
		de.setType(DataElementType.HIDDEN);			
		de.setName(UtentiCostanti.PARAMETRO_UTENTI_HOME_PAGE_MONITORAGGIO);
		de.setValue(homePageMonitoraggio);
		dati.add(de);
		
		de = new DataElement();
		de.setLabel(UtentiCostanti.LABEL_PARAMETRO_UTENTI_INTERVALLO_TEMPORALE_HOME_PAGE_MONITORAGGIO);
		de.setType(DataElementType.HIDDEN);			
		de.setName(UtentiCostanti.PARAMETRO_UTENTI_INTERVALLO_TEMPORALE_HOME_PAGE_MONITORAGGIO);
		de.setValue(intervalloTemporaleReportStatistico);
		dati.add(de);

		de = new DataElement();
		de.setLabel(UtentiCostanti.LABEL_PARAMETRO_UTENTI_IS_SERVIZI);
		de.setValue(isServizi);
		de.setType(DataElementType.HIDDEN);
		de.setName(UtentiCostanti.PARAMETRO_UTENTI_IS_SERVIZI);
		dati.add(de);

		de = new DataElement();
		de.setLabel(UtentiCostanti.LABEL_PARAMETRO_UTENTI_IS_SISTEMA);
		de.setValue(isSistema);
		de.setType(DataElementType.HIDDEN);
		de.setName(UtentiCostanti.PARAMETRO_UTENTI_IS_SISTEMA);
		dati.add(de);

		de = new DataElement();
		de.setLabel(UtentiCostanti.LABEL_PARAMETRO_UTENTI_IS_MESSAGGI);
		de.setValue(isMessaggi);
		de.setType(DataElementType.HIDDEN);
		de.setName(UtentiCostanti.PARAMETRO_UTENTI_IS_MESSAGGI);
		dati.add(de);

		de = new DataElement();
		de.setLabel(UtentiCostanti.LABEL_PARAMETRO_UTENTI_IS_DIAGNOSTICA);
		de.setValue(isDiagnostica);
		de.setType(DataElementType.HIDDEN);
		de.setName(UtentiCostanti.PARAMETRO_UTENTI_IS_DIAGNOSTICA);
		dati.add(de);
		
		de = new DataElement();
		de.setLabel(UtentiCostanti.LABEL_PARAMETRO_UTENTI_IS_REPORTISTICA);
		de.setValue(isReportistica);
		de.setType(DataElementType.HIDDEN);
		de.setName(UtentiCostanti.PARAMETRO_UTENTI_IS_REPORTISTICA);
		dati.add(de);
		
		de = new DataElement();
		de.setLabel(UtentiCostanti.LABEL_PARAMETRO_UTENTI_IS_UTENTI);
		de.setValue(isUtenti);
		de.setType(DataElementType.HIDDEN);
		de.setName(UtentiCostanti.PARAMETRO_UTENTI_IS_UTENTI);
		dati.add(de);

		de = new DataElement();
		de.setLabel(UtentiCostanti.LABEL_PARAMETRO_UTENTI_IS_AUDITING);
		de.setValue(isAuditing);
		de.setType(DataElementType.HIDDEN);
		de.setName(UtentiCostanti.PARAMETRO_UTENTI_IS_AUDITING);
		dati.add(de);

		de = new DataElement();
		de.setLabel(UtentiCostanti.LABEL_PARAMETRO_UTENTI_IS_ACCORDI_COOPERAZIONE);
		de.setType(DataElementType.HIDDEN);
		de.setName(UtentiCostanti.PARAMETRO_UTENTI_IS_ACCORDI_COOPERAZIONE);
		de.setValue(isAccordiCooperazione);
		dati.add(de);

		if(scegliSuServizi){
			de = new DataElement();
			de.setNote(UtentiCostanti.LABEL_UTENTE_PERMESSI_SERVIZI_NOTE);
			de.setLabel(UtentiCostanti.LABEL_UTENTE_PERMESSI);
			de.setType(DataElementType.SELECT);
			de.setName(UtentiCostanti.PARAMETRO_UTENTI_SINGLE_SU_SERVIZI);
			de.setValues(uws);
			dati.add(de);
		}

		if(scegliSuAccordi){
			de = new DataElement();
			de.setNote(UtentiCostanti.LABEL_UTENTE_PERMESSI_ACCORDI_COOPERAZIONE_NOTE);
			de.setLabel(UtentiCostanti.LABEL_UTENTE);
			de.setType(DataElementType.SELECT);
			de.setName(UtentiCostanti.PARAMETRO_UTENTI_SINGLE_SU_ACCORDI_COOPERAZIONE);
			de.setValues(uwp);
			dati.add(de);
		}
		
		List<String> protocolliRegistratiConsole = this.core.getProtocolli();
		for (int i = 0; i < protocolliRegistratiConsole.size() ; i++) {
			String protocolloName = protocolliRegistratiConsole.get(i);
			de = new DataElement();
			de.setLabel(this.getLabelProtocollo(protocolloName));
			de.setType(DataElementType.HIDDEN);
			de.setName(UtentiCostanti.PARAMETRO_UTENTI_MODALITA_PREFIX + protocolloName);
			de.setValue(modalitaGateway[i]);
			dati.add(de);
		}
	}


	public void addChooseUtenteForPermessiSToDati(List<DataElement> dati,String objToRemove,boolean scegliSuServizi,
			String [] uws, boolean scegliSuAccordi,String [] uwp){

		DataElement de = new DataElement();
		de.setLabel(Costanti.PARAMETER_NAME_OBJECTS_FOR_REMOVE);
		de.setValue(objToRemove);
		de.setType(DataElementType.HIDDEN);
		de.setName(Costanti.PARAMETER_NAME_OBJECTS_FOR_REMOVE);
		dati.add(de);

		if(scegliSuServizi){
			de = new DataElement();
			de.setNote(UtentiCostanti.LABEL_UTENTE_PERMESSI_SERVIZI_NOTE);
			de.setLabel(UtentiCostanti.LABEL_UTENTE_PERMESSI);
			de.setType(DataElementType.SELECT);
			de.setName(UtentiCostanti.PARAMETRO_UTENTI_SINGLE_SU_SERVIZI);
			de.setValues(uws);
			dati.add(de);
		}

		if(scegliSuAccordi){
			de = new DataElement();
			de.setNote(UtentiCostanti.LABEL_UTENTE_PERMESSI_ACCORDI_COOPERAZIONE_NOTE);
			de.setLabel(UtentiCostanti.LABEL_UTENTE);
			de.setType(DataElementType.SELECT);
			de.setName(UtentiCostanti.PARAMETRO_UTENTI_SINGLE_SU_ACCORDI_COOPERAZIONE);
			de.setValues(uwp);
			dati.add(de);
		}



	}

	public void addUtenteChangeToDati(List<DataElement> dati,InterfaceType interfaceType,
			String changepw, String nomeUtente, String profiloSelezionatoUtente, String soggettoSelezionatoUtente) throws Exception{

		DataElement de = new DataElement();
		de.setName(UtentiCostanti.PARAMETRO_UTENTI_FIRST);
		de.setType(DataElementType.HIDDEN);
		de.setValue("false");
		dati.add(de);
		
		de = new DataElement();
		de.setLabel(UtentiCostanti.LABEL_INFO_UTENTE);
		de.setType(DataElementType.TITLE);
		dati.add(de);
		
		de = new DataElement();
		de.setLabel(UtentiCostanti.LABEL_PARAMETRO_UTENTI_USERNAME);
		de.setName(UtentiCostanti.PARAMETRO_UTENTE_LOGIN);
		de.setType(DataElementType.TEXT);
		de.setValue(nomeUtente);
		dati.add(de);
		
		User utente = this.utentiCore.getUser(nomeUtente);
		
		if(!utente.hasOnlyPermessiUtenti()) { // questa sezione viene visualizzata solo se si hanno i diritti
			de = new DataElement();
			de.setType(DataElementType.SUBTITLE);
			de.setLabel(UtentiCostanti.LABEL_PROFILO);
			dati.add(de);
		}
		
		// tipo interfaccia
		de = new DataElement();
		de.setLabel(UtentiCostanti.LABEL_MODALITA_INTERFACCIA);
		de.setName(UtentiCostanti.PARAMETRO_UTENTI_TIPO_GUI);
		if(utente.hasOnlyPermessiUtenti()) {
			de.setType(DataElementType.HIDDEN);
			de.setValue(interfaceType.toString());
		}
		else {
			if(interfaceType.equals(InterfaceType.COMPLETA)) {
				de.setLabel(UtentiCostanti.LABEL_MODALITA_INTERFACCIA);
				de.setType(DataElementType.TEXT);
				de.setName(UtentiCostanti.PARAMETRO_UTENTI_TIPO_GUI+"_text");
				de.setValue(interfaceType.toString().toLowerCase());
				dati.add(de);
				
				de = new DataElement();
				de.setLabel(UtentiCostanti.LABEL_MODALITA_INTERFACCIA);
				de.setName(UtentiCostanti.PARAMETRO_UTENTI_TIPO_GUI);
				de.setType(DataElementType.HIDDEN);	
				de.setValue(interfaceType.toString());
				
			} else {
				de.setType(DataElementType.SELECT);		
				User user = ServletUtils.getUserFromSession(this.request, this.session);
				String[] tipiInterfacce=null;
				String[] tipiInterfacceLabel=null;
				if(user.isPermitInterfaceComplete()) {
					tipiInterfacce = new String[3];			
				}
				else {
					tipiInterfacce = new String[2];
				}
				tipiInterfacce[0]=InterfaceType.STANDARD.toString();
				tipiInterfacce[1]=InterfaceType.AVANZATA.toString();
				if(user.isPermitInterfaceComplete()) {
					tipiInterfacce[2]=InterfaceType.COMPLETA.toString();
				}
				tipiInterfacceLabel = new String[tipiInterfacce.length];
				for (int i = 0; i < tipiInterfacce.length; i++) {
					tipiInterfacceLabel[i] = tipiInterfacce[i].toLowerCase();
				}
				de.setValues(tipiInterfacce);
				de.setLabels(tipiInterfacceLabel);
				de.setSelected(interfaceType.toString());
			}
		}
		dati.add(de);
		
		de = new DataElement();
		de.setLabel(org.openspcoop2.core.constants.Costanti.LABEL_PARAMETRO_PROTOCOLLO_HTML_ESCAPE);
		if(utente.hasOnlyPermessiUtenti()) {
			de.setType(DataElementType.HIDDEN);
			de.setName(UtentiCostanti.PARAMETRO_UTENTE_TIPO_MODALITA);
			de.setValue(profiloSelezionatoUtente);
			dati.add(de);
		}
		else {
			List<String> protocolliDispondibili = this.core.getProtocolli(this.request, this.session, true);
			
			if(protocolliDispondibili != null && protocolliDispondibili.size() > 1) {
				de.setType(DataElementType.SELECT);
				
				List<String> labelProtocolli = new ArrayList<>();
				
				for (String protocolloDisponibile : ProtocolUtils.orderProtocolli(protocolliDispondibili)) {
					String labelProt = ConsoleHelper._getLabelProtocollo(protocolloDisponibile);
					labelProtocolli.add(labelProt);
				}
				
				labelProtocolli.add(0, UtentiCostanti.LABEL_PARAMETRO_MODALITA_ALL);
				protocolliDispondibili.add(0,UtentiCostanti.VALORE_PARAMETRO_MODALITA_ALL);
				de.setName(UtentiCostanti.PARAMETRO_UTENTE_TIPO_MODALITA);
				de.setValues(protocolliDispondibili);
				de.setLabels(labelProtocolli);
				
				de.setSelected(profiloSelezionatoUtente);
				de.setPostBack(true);
				dati.add(de);
			} else {
				// solo un protocollo visualizzo il testo e basta
				de.setType(DataElementType.HIDDEN);
				de.setName(UtentiCostanti.PARAMETRO_UTENTE_TIPO_MODALITA);
				de.setValue(profiloSelezionatoUtente);
				dati.add(de);
				
				// visualizzo label
				de = new DataElement();
				de.setType(DataElementType.TEXT);
				de.setLabel(org.openspcoop2.core.constants.Costanti.LABEL_PARAMETRO_PROTOCOLLO_HTML_ESCAPE);
				de.setName(UtentiCostanti.PARAMETRO_UTENTE_TIPO_MODALITA + "_txt");
				
				if(protocolliDispondibili!=null) {
					String labelProt = ConsoleHelper._getLabelProtocollo(protocolliDispondibili.get(0));
					de.setValue(labelProt);
				}
//				de.setValue(profiloSelezionatoUtente.equals(UtentiCostanti.VALORE_PARAMETRO_MODALITA_ALL) ? UtentiCostanti.LABEL_PARAMETRO_MODALITA_ALL : ConsoleHelper._getLabelProtocollo(profiloSelezionatoUtente));
				dati.add(de);
				
			} 
		}
		
		if(utente.hasOnlyPermessiUtenti()) {
			de = new DataElement();
			de.setLabel(UtentiCostanti.LABEL_PARAMETRO_SOGGETTO_OPERATIVO);
			de.setType(DataElementType.HIDDEN);
			de.setName(UtentiCostanti.PARAMETRO_UTENTE_ID_SOGGETTO);
			de.setValue(soggettoSelezionatoUtente);
			dati.add(de);
		} else {
			List<String> protocolliDispondibili = this.core.getProtocolli(this.request, this.session, true);
			// selezione del soggetto se disponibile		
			if((profiloSelezionatoUtente!=null && !UtentiCostanti.VALORE_PARAMETRO_MODALITA_ALL.equals(profiloSelezionatoUtente)) 
					|| protocolliDispondibili.size() == 1) {
					
				String profiloDefaultCorrente = (protocolliDispondibili.size() == 1) ? protocolliDispondibili.get(0) : profiloSelezionatoUtente;
				
				List<IDSoggetto> idSoggettiOperativi = this.soggettiCore.getIdSoggettiOperativi(profiloDefaultCorrente);
			
				// se ho selezionato un profilo e ho almeno due soggetti visualizzo la tendina
				if(idSoggettiOperativi != null && idSoggettiOperativi.size()>1) {
					List<String> listaLabel = new ArrayList<>();
					Map<String, IDSoggetto> mapLabelIds = new HashMap<>();
					for (IDSoggetto idSoggetto : idSoggettiOperativi) {
						String labelSoggetto = ConsoleHelper._getLabelNomeSoggetto(idSoggetto);
						if(!listaLabel.contains(labelSoggetto)) {
							listaLabel.add(labelSoggetto);
							mapLabelIds.put(labelSoggetto, idSoggetto);
						}
					}
					
					// Per ordinare in maniera case insensistive
					Collections.sort(listaLabel, new Comparator<String>() {
						 @Override
						public int compare(String o1, String o2) {
					           return o1.toLowerCase().compareTo(o2.toLowerCase());
					        }
						});
					
					List<String> listaValues = new ArrayList<>();
					
					for (String label : listaLabel) {
						listaValues.add(NamingUtils.getSoggettoFromLabel(profiloDefaultCorrente, label).toString());
					}
					
					
					de = new DataElement();
					de.setType(DataElementType.SELECT);
					de.setLabel(UtentiCostanti.LABEL_PARAMETRO_SOGGETTO_OPERATIVO);
					listaLabel.add(0, UtentiCostanti.LABEL_PARAMETRO_MODALITA_ALL);
					listaValues.add(0,UtentiCostanti.VALORE_PARAMETRO_MODALITA_ALL);
					de.setValues(listaValues);
					de.setLabels(listaLabel);
					de.setName(UtentiCostanti.PARAMETRO_UTENTE_ID_SOGGETTO);
					de.setSelected(soggettoSelezionatoUtente);
					dati.add(de);
					
				} else { // se ho un solo soggetto visualizzo il text
					de = new DataElement();
					de.setLabel(UtentiCostanti.LABEL_PARAMETRO_SOGGETTO_OPERATIVO);
					de.setType(DataElementType.HIDDEN);
					de.setName(UtentiCostanti.PARAMETRO_UTENTE_ID_SOGGETTO);
					de.setValue(soggettoSelezionatoUtente);
					dati.add(de);
					
					de = new DataElement();
					de.setLabel(UtentiCostanti.LABEL_PARAMETRO_SOGGETTO_OPERATIVO);
					de.setType(DataElementType.TEXT);
					de.setName(UtentiCostanti.PARAMETRO_UTENTE_ID_SOGGETTO+ "_txt");
					
					if(idSoggettiOperativi!=null) {
						IDSoggetto idSoggetto = idSoggettiOperativi.get(0);
						String labelSoggetto = ConsoleHelper._getLabelNomeSoggetto(idSoggetto);
						de.setValue(labelSoggetto);
					}
//					de.setValue(soggettoSelezionatoUtente.equals(UtentiCostanti.VALORE_PARAMETRO_MODALITA_ALL) ? UtentiCostanti.LABEL_PARAMETRO_MODALITA_ALL : soggettoSelezionatoUtente);
					dati.add(de);
				}
			} else {
				de = new DataElement();
				de.setLabel(UtentiCostanti.LABEL_PARAMETRO_SOGGETTO_OPERATIVO);
				de.setType(DataElementType.HIDDEN);
				de.setName(UtentiCostanti.PARAMETRO_UTENTE_ID_SOGGETTO);
				de.setValue(soggettoSelezionatoUtente);
				dati.add(de);
			}
		}
		
		if(this.core.isLoginApplication()) {
		
			de = new DataElement();
			de.setLabel(UtentiCostanti.LABEL_PASSWORD);
			de.setType(DataElementType.TITLE);
			dati.add(de);
	
			de = new DataElement();
			de.setLabel(UtentiCostanti.LABEL_MODIFICA);
			de.setType(DataElementType.CHECKBOX);
			//		de.setOnClick("cambiaPassword(\"changePwd\");");
			de.setName(UtentiCostanti.PARAMETRO_UTENTI_CHANGE_PW);
			de.setPostBack(true);
			de.setSelected(changepw);
			de.setSize(this.getSize());
			dati.add(de);
	
	
			//se e' stato selezionato il link per il cambio password allora mostro i dati
			if(ServletUtils.isCheckBoxEnabled(changepw)){
	
				PasswordVerifier passwordVerifier = this.utentiCore.getUtenzePasswordVerifier();
				
	//			if(ServletUtils.getUserFromSession(this.session).getPermessi().isUtenti()==false){
				
				de = new DataElement();
				de.setLabel(UtentiCostanti.LABEL_PARAMETRO_UTENTE_VECCHIA_PW);
				de.setType(DataElementType.CRYPT);
				de.setName(UtentiCostanti.PARAMETRO_UTENTE_VECCHIA_PW);
				de.setValue("");
				de.setSize(this.getSize());
				de.setRequired(true);
				dati.add(de);
					
	//			}
	
				de = new DataElement();
				de.setLabel(UtentiCostanti.LABEL_PARAMETRO_UTENTE_NUOVA_PW);
				de.setType(DataElementType.CRYPT);
				de.setName(UtentiCostanti.PARAMETRO_UTENTE_NUOVA_PW);
				de.setSize(this.getSize());
				de.setValue("");
				de.setRequired(true);
				dati.add(de);
	
				de = new DataElement();
				de.setLabel(UtentiCostanti.LABEL_PARAMETRO_UTENTE_CONFERMA_NUOVA_PW);
				de.setType(DataElementType.CRYPT);
				de.setName(UtentiCostanti.PARAMETRO_UTENTE_CONFERMA_NUOVA_PW);
				de.setSize(this.getSize());
				de.setValue("");
				de.setRequired(true);
				if(passwordVerifier!=null){
					de.setNote(passwordVerifier.helpUpdate(org.openspcoop2.core.constants.Costanti.WEB_NEW_LINE));
				}
				dati.add(de);
			}

		}
		de = new DataElement();
		de.setType(DataElementType.HIDDEN);
		de.setName(UtentiCostanti.PARAMETRO_UTENTE_ESEGUI);
		de.setValue(UtentiCostanti.PARAMETRO_UTENTE_ESEGUI);
		dati.add(de);
	}


	public boolean utentiCheckData(TipoOperazione tipoOperazione,boolean singlePdD,List<String> oldProtocolliSupportati, boolean oldUserHasOnlyPermessiUtenti) throws Exception {
		try {
			String nomesu = this.getParameter(UtentiCostanti.PARAMETRO_UTENTI_USERNAME);
			String pwsu = this.getParameter(UtentiCostanti.PARAMETRO_UTENTI_PW);
			// String confpwsu = this.getParameter(UtentiCostanti.PARAMETRO_UTENTI_CONFERMA_PASSWORD);
			String tipoGui = this.getParameter(UtentiCostanti.PARAMETRO_UTENTI_TIPO_GUI);
			String isServizi = this.getParameter(UtentiCostanti.PARAMETRO_UTENTI_IS_SERVIZI);
			String isDiagnostica = this.getParameter(UtentiCostanti.PARAMETRO_UTENTI_IS_DIAGNOSTICA);
			String isReportistica = this.getParameter(UtentiCostanti.PARAMETRO_UTENTI_IS_REPORTISTICA);
			String isSistema = this.getParameter(UtentiCostanti.PARAMETRO_UTENTI_IS_SISTEMA);
			String isMessaggi = this.getParameter(UtentiCostanti.PARAMETRO_UTENTI_IS_MESSAGGI);
			String isUtenti = this.getParameter(UtentiCostanti.PARAMETRO_UTENTI_IS_UTENTI);
			String isAccordiCooperazione = this.getParameter(UtentiCostanti.PARAMETRO_UTENTI_IS_ACCORDI_COOPERAZIONE);
			String isAuditing = this.getParameter(UtentiCostanti.PARAMETRO_UTENTI_IS_AUDITING);
			String changepwd = this.getParameter(UtentiCostanti.PARAMETRO_UTENTI_CHANGE_PW);

			List<String> protocolliRegistratiConsole = this.utentiCore.getProtocolli();
			
			String [] modalitaScelte = new String[protocolliRegistratiConsole.size()]; 
			List<String> nuoviProtocolliSupportati = new ArrayList<>();
			for (int i = 0; i < protocolliRegistratiConsole.size() ; i++) {
				String protocolloName = protocolliRegistratiConsole.get(i);
				modalitaScelte[i] = this.getParameter(UtentiCostanti.PARAMETRO_UTENTI_MODALITA_PREFIX + protocolloName);
				if(ServletUtils.isCheckBoxEnabled(modalitaScelte[i])) {
					nuoviProtocolliSupportati.add(protocolloName);
				}
			}

			boolean loginApplication = this.core.isLoginApplication();
			
			// Campi obbligatori
			if (TipoOperazione.ADD.equals(tipoOperazione) || ServletUtils.isCheckBoxEnabled(changepwd) ) {
				String tmpElenco = "";
				if (nomesu.equals("")) {
					tmpElenco = UtentiCostanti.LABEL_PARAMETRO_UTENTI_USERNAME;
				}
				boolean checkPassword = true;
				if(TipoOperazione.CHANGE.equals(tipoOperazione) && !ServletUtils.isCheckBoxEnabled(changepwd)){
					//if( confpwsu.equals("") && pwsu.equals("") ){
					//	checkPassword = false;
					//}
					checkPassword=false;
				}
				if(!loginApplication) {
					checkPassword=false;
				}
				if(checkPassword){
					if (pwsu==null || pwsu.equals("")) {
						if (tmpElenco.equals("")) {
							tmpElenco = UtentiCostanti.LABEL_PARAMETRO_UTENTI_PW;
						} else {
							tmpElenco = tmpElenco + ", "+UtentiCostanti.LABEL_PARAMETRO_UTENTI_PW;
						}
					}
//					if (confpwsu.equals("")) {
//						if (tmpElenco.equals("")) {
//							tmpElenco = UtentiCostanti.LABEL_PARAMETRO_UTENTI_CONFERMA_PASSWORD;
//						} else {
//							tmpElenco = tmpElenco + ", "+UtentiCostanti.LABEL_PARAMETRO_UTENTI_CONFERMA_PASSWORD;
//						}
//					}
				}
				if(!tmpElenco.equals("")){
					this.pd.setMessage("Dati incompleti. &Egrave; necessario indicare: " + tmpElenco);
					return false;
				}
			}

			// Controllo che non ci siano spazi nei campi di testo
//			if ((nomesu.indexOf(" ") != -1) || ( ServletUtils.isCheckBoxEnabled(changepwd) && ( (pwsu.indexOf(" ") != -1) || (confpwsu.indexOf(" ") != -1)))) {
			if ((nomesu.indexOf(" ") != -1) || ( ServletUtils.isCheckBoxEnabled(changepwd) &&  pwsu.indexOf(" ") != -1 )) {
				this.pd.setMessage("Non inserire spazi nei campi di testo");
				return false;
			}
			
			// length
			if(this.checkLength255(nomesu, UtentiCostanti.LABEL_PARAMETRO_UTENTI_USERNAME)==false) {
				return false;
			}

			// Controllo che i campi "checkbox" abbiano uno dei valori
			// ammessi
			//			if ((isServizi != null) && !isServizi.equals("") && !isServizi.equals("yes") && !isServizi.equals("no")) {
			//				this.pd.setMessage("Servizi dev'essere selezionato o deselezionato");
			//				return false;
			//			}
			//			if (singlePdD.equals("true") && (isDiagnostica != null) && !isDiagnostica.equals("") && !isDiagnostica.equals("yes") && !isDiagnostica.equals("no")) {
			//				this.pd.setMessage("Diagnostica dev'essere selezionato o deselezionato");
			//				return false;
			//			}
			//			if (singlePdD.equals("true") && (isReportistica != null) && !isReportistica.equals("") && !isReportistica.equals("yes") && !isReportistica.equals("no")) {
			//				this.pd.setMessage("Reportistica dev'essere selezionato o deselezionato");
			//				return false;
			//			}
			//			if ((isSistema != null) && !isSistema.equals("") && !isSistema.equals("yes") && !isSistema.equals("no")) {
			//				this.pd.setMessage("Sistema dev'essere selezionato o deselezionato");
			//				return false;
			//			}
			//			if ((isMessaggi != null) && !isMessaggi.equals("") && !isMessaggi.equals("yes") && !isMessaggi.equals("no")) {
			//				this.pd.setMessage("Messaggi dev'essere selezionato o deselezionato");
			//				return false;
			//			}
			//			if ((isUtenti != null) && !isUtenti.equals("") && !isUtenti.equals("yes") && !isUtenti.equals("no")) {
			//				this.pd.setMessage("Utenti dev'essere selezionato o deselezionato");
			//				return false;
			//			}
			//			if ((isAuditing != null) && !isAuditing.equals("") && !isAuditing.equals("yes") && !isAuditing.equals("no")) {
			//				this.pd.setMessage("Auditing dev'essere selezionato o deselezionato");
			//				return false;
			//			}
			//			if ((isAccordiCooperazione != null) && !isAccordiCooperazione.equals("") && !isAccordiCooperazione.equals("yes") && !isAccordiCooperazione.equals("no")) {
			//			this.pd.setMessage("Accordi Cooperazione dev'essere selezionato o deselezionato");
			//			return false;
			//		}
			
			// in modalita change devo controllare che se ho cambiato le modalita' all'utente ci sia almeno un altro utente che puo' gestire le modalita' che lascio
			User user = null;
			if(TipoOperazione.CHANGE.equals(tipoOperazione)) {
				
				// prelevo l'utenza dal db
				user = this.utentiCore.getUser(nomesu);
				
				// se l'utente aveva solo il controllo degli utenti, questo controllo non importa tanto non ha modalita' associate
				if(!oldUserHasOnlyPermessiUtenti) {
					
					if(oldProtocolliSupportati==null) {
						oldProtocolliSupportati = this.utentiCore.getProtocolli();
					}
					
					Collections.sort(oldProtocolliSupportati);
					Collections.sort(nuoviProtocolliSupportati);
					
					List<String> protocolliEliminati = new ArrayList<>(); 
					for (String vecchioProtocollo : oldProtocolliSupportati) {
						boolean protocolloEliminato = !nuoviProtocolliSupportati.contains(vecchioProtocollo);
						
						if(protocolloEliminato)
							protocolliEliminati.add(vecchioProtocollo);
					}
					
					if(protocolliEliminati.size() > 0) {
						List<String> nomiUtentiDaRimuovere = new ArrayList<>();
						nomiUtentiDaRimuovere.add(nomesu);
						List<String> utentiDaNonEliminare = new ArrayList<>();
						List<String> protocolliNonValidi = new ArrayList<>();
						for (String protocolloDaControllare : protocolliEliminati) {
							boolean protocolloNonPiuAssociato = this.controllaEsistenzaUtentePerAssociareIlProtocollo(nomiUtentiDaRimuovere, utentiDaNonEliminare, nomesu, protocolloDaControllare);
							if(protocolloNonPiuAssociato)
								protocolliNonValidi.add(this.getLabelProtocollo(protocolloDaControllare));
						}
						
						if(utentiDaNonEliminare.size() > 0) {
							if(protocolliNonValidi.size() > 1) {
								StringBuilder sbPnV = new StringBuilder();
								for (String protNonVal : protocolliNonValidi) {
									if(sbPnV.length() >0 )
										sbPnV.append(", ");
									sbPnV.append(protNonVal);
								}
								
								this.pd.setMessage("L'utente " +utentiDaNonEliminare.get(0) +
									" non pu&ograve; essere modificato poich&egrave; sono stati rilevati oggetti, appartenenti ai "+
										org.openspcoop2.core.constants.Costanti.LABEL_PARAMETRO_PROTOCOLLI_DI_HTML_ESCAPE+" '"+ sbPnV.toString() +"', non assegnati a nessun altro utente");
							} else {
								this.pd.setMessage("L'utente " +utentiDaNonEliminare.get(0) +
										" non pu&ograve; essere modificato poich&egrave; sono stati rilevati oggetti, appartenenti al "+
										org.openspcoop2.core.constants.Costanti.LABEL_PARAMETRO_PROTOCOLLO_DI_HTML_ESCAPE+" '"+ protocolliNonValidi.get(0) +"', non assegnati a nessun altro utente");
							}
							return false;
						}
						
						// Controlli su eventuali soggetti/servizi associati alle modalita' deselezionate
						for (String protocolloDaControllare : protocolliEliminati) {
							// controllo servizi
							if(user.getServizi().size() > 0) {
								for (IDServizio idServizio : user.getServizi()) {
									String protocolloAssociatoTipoSoggetto = this.soggettiCore.getProtocolloAssociatoTipoSoggetto(idServizio.getTipo());
									if(protocolloAssociatoTipoSoggetto.equals(protocolloDaControllare)) {
										this.pd.setMessage("L'utente " + nomesu 
												+ " non pu&ograve; essere modificato poich&egrave; sono stati rilevati delle API, appartenenti al "+
										org.openspcoop2.core.constants.Costanti.LABEL_PARAMETRO_PROTOCOLLO_DI_HTML_ESCAPE+" '"
												+ this.getLabelProtocollo(protocolloDaControllare) +"', registrate tra le restrizioni dell'utente");
										return false;
									}
								}
							}
							// controllo soggetti
							if(user.getSoggetti().size() > 0) {
								for (IDSoggetto idSoggetto : user.getSoggetti()) {
									String protocolloAssociatoTipoSoggetto = this.soggettiCore.getProtocolloAssociatoTipoSoggetto(idSoggetto.getTipo());
									if(protocolloAssociatoTipoSoggetto.equals(protocolloDaControllare)) {
										this.pd.setMessage("L'utente " + nomesu 
												+ " non pu&ograve; essere modificato poich&egrave; sono stati rilevati dei soggetti, appartenenti al "+
										org.openspcoop2.core.constants.Costanti.LABEL_PARAMETRO_PROTOCOLLO_DI_HTML_ESCAPE+" '"
												+ this.getLabelProtocollo(protocolloDaControllare) +"', registrati tra le restrizioni dell'utente");
										return false;
									}
								}
							}
						}
					}
				}
				
				// Controlli su soggetti/servizi associati
				//1. l'utente aveva un permesso di tipo 'D' e' stato eliminato, devo controllare che non abbia soggetti/servizi associati
				if(
						(isDiagnostica == null || !ServletUtils.isCheckBoxEnabled(isDiagnostica))
						&&
						(isReportistica == null || !ServletUtils.isCheckBoxEnabled(isReportistica))
						){
					
					boolean oldDiagnostica = user.getPermessi().isDiagnostica();
					boolean oldReportistica = user.getPermessi().isReportistica();
						
					if( (oldDiagnostica || oldReportistica) && user.getServizi().size() > 0) {
						this.pd.setMessage("L'utente " + nomesu + " non pu&ograve; essere modificato poich&egrave; sono stati rilevate delle API registrate tra le restrizioni dell'utente");
						return false;
					}
					if( (oldDiagnostica || oldReportistica) && user.getSoggetti().size() > 0) {
						this.pd.setMessage("L'utente " + nomesu + " non pu&ograve; essere modificato poich&egrave; sono stati rilevati dei soggetti registrati tra le restrizioni dell'utente");
						return false;
					}
				}
			}
			

			// Controllo che i campi "select" abbiano uno dei valori ammessi
			// solo per l'utente della govwayConsole
			boolean utenteConsoleEnabled = 
					ServletUtils.isCheckBoxEnabled(isServizi) ||
					ServletUtils.isCheckBoxEnabled(isMessaggi) ||
					ServletUtils.isCheckBoxEnabled(isAuditing) ||
					ServletUtils.isCheckBoxEnabled(isSistema) ||
					ServletUtils.isCheckBoxEnabled(isUtenti) ||				
					ServletUtils.isCheckBoxEnabled(isAccordiCooperazione);
			
			if(utenteConsoleEnabled) {
				try {
					InterfaceType.convert(tipoGui, true);
				}catch(Exception e) {
					this.pd.setMessage("Tipo dev'essere uno dei seguenti valori: "+InterfaceType.AVANZATA + ", " + InterfaceType.STANDARD + ".");
					return false;
				}
				
				String homePageMonitoraggio = this.getParameter(UtentiCostanti.PARAMETRO_UTENTI_HOME_PAGE_MONITORAGGIO);
				String intervalloTemporaleHomePageConsoleMonitoraggio = this.getParameter(UtentiCostanti.PARAMETRO_UTENTI_INTERVALLO_TEMPORALE_HOME_PAGE_MONITORAGGIO);

				if(!Arrays.asList(UtentiCostanti.getValuesParametroUtentiHomePageMonitoraggio()).contains(homePageMonitoraggio)) {
					this.pd.setMessage(UtentiCostanti.LABEL_PARAMETRO_UTENTI_HOME_PAGE_MONITORAGGIO + " contiene un valore non valido.");
					return false;
				}
				if(!Arrays.asList(UtentiCostanti.getValuesParametroUtentiIntervalloTemporaleHomePageMonitoraggio()).contains(intervalloTemporaleHomePageConsoleMonitoraggio)) {
					this.pd.setMessage(UtentiCostanti.LABEL_PARAMETRO_UTENTI_INTERVALLO_TEMPORALE_HOME_PAGE_MONITORAGGIO + " contiene un valore non valido.");
					return false;
				}
			}
			
			// Controllo che le password corrispondano
			boolean checkPassword = true;
			if(TipoOperazione.CHANGE.equals(tipoOperazione) && !ServletUtils.isCheckBoxEnabled(changepwd)){
				//if( confpwsu.equals("") && pwsu.equals("") ){
				//	checkPassword = false;
				//}
				checkPassword=false;
			}
			if(!loginApplication) {
				checkPassword=false;
			}
//			if (checkPassword && !pwsu.equals(confpwsu)) {
//				this.pd.setMessage("Le password non corrispondono");
//				return false;
//			}
			
			if (checkPassword){
				PasswordVerifier passwordVerifier = this.utentiCore.getUtenzePasswordVerifier();
				if(passwordVerifier!=null){
					StringBuilder motivazioneErrore = new StringBuilder();
					if(passwordVerifier.validate(nomesu, pwsu, motivazioneErrore)==false){
						this.pd.setMessage(motivazioneErrore.toString());
						return false;
					}
					
					// controllo storico password in caso di change
					// Attualmente l'amministratore puo' impostare una vecchia password
//					if(TipoOperazione.CHANGE.equals(tipoOperazione) && passwordVerifier.isHistory()) {
//						List<UserPassword> precedentiPassword = user.getPrecedentiPassword();
//						
////						String tmpPass = pwsu;
////						if(this.utentiCore.isUtenzePasswordEncryptEnabled()) {
////							tmpPass = this.utentiCore.getUtenzePasswordManager().crypt(pwsu);
////						}
//
//						// se la lista storico e' vuota controllo la password precedente salvata nel bean
//						if(precedentiPassword == null || precedentiPassword.isEmpty()) {
//							boolean trovato = this.utentiCore.getUtenzePasswordManager().check(pwsu, user.getPassword());
//							if(!trovato && this.utentiCore.getUtenzePasswordManager_backwardCompatibility()!=null) {
//								trovato = this.utentiCore.getUtenzePasswordManager_backwardCompatibility().check(pwsu, user.getPassword());
//							}
//							if (trovato) {
//								this.pd.setMessage(UtentiCostanti.MESSAGGIO_ERRORE_PASSWORD_GIA_UTILIZZATA);
//								return false;
//							}
//						}
//						
//						for (UserPassword userPassword : precedentiPassword) {
//							boolean trovato = this.utentiCore.getUtenzePasswordManager().check(pwsu, userPassword.getPassword());
//							if(!trovato && this.utentiCore.getUtenzePasswordManager_backwardCompatibility()!=null) {
//								trovato = this.utentiCore.getUtenzePasswordManager_backwardCompatibility().check(pwsu, userPassword.getPassword());
//							}
//							if (trovato) {
//								this.pd.setMessage(UtentiCostanti.MESSAGGIO_ERRORE_PASSWORD_GIA_UTILIZZATA);
//								return false;
//							}
//						}
//					}
				}
			}

			// Almeno un permesso dev'essere selezionato
			if (
					(
							(isServizi == null) || !ServletUtils.isCheckBoxEnabled(isServizi)
					) &&
					(
							!singlePdD 
							|| 
							(
									//singlePdD 
									//&& 
									(
											(isDiagnostica == null) || !ServletUtils.isCheckBoxEnabled(isDiagnostica)
									)
									&& 
									(
											(isReportistica == null) || !ServletUtils.isCheckBoxEnabled(isReportistica)
									)
							)
					) &&
					((isSistema == null) || !ServletUtils.isCheckBoxEnabled(isSistema)) &&
					((isMessaggi == null) || !ServletUtils.isCheckBoxEnabled(isMessaggi)) &&
					((isUtenti == null) || !ServletUtils.isCheckBoxEnabled(isUtenti)) &&
					((isAuditing == null) || !ServletUtils.isCheckBoxEnabled(isAuditing)) &&
					((isAccordiCooperazione == null) || !ServletUtils.isCheckBoxEnabled(isAccordiCooperazione))) {
				this.pd.setMessage("Selezionare almeno un Permesso");
				return false;
			}

			boolean modalitaPresenti = false;
			// controllo che abbia selezionato almeno una modalita gateway	
			for (int i = 0; i < modalitaScelte.length; i++) {
				modalitaPresenti  = ((modalitaScelte[i] != null) && ServletUtils.isCheckBoxEnabled(modalitaScelte[i]));
				
				if(modalitaPresenti)
					break;
			}
			
			// se l'utenza che sto creando e' solo Utenti ignoro la modalita gateway
			if(hasOnlyPermessiUtenti(isServizi, isDiagnostica, isReportistica, isSistema, isMessaggi, isUtenti, isAuditing, isAccordiCooperazione, singlePdD)==false) {
							
				if(!modalitaPresenti) {
					this.pd.setMessage("Selezionare almeno un "+org.openspcoop2.core.constants.Costanti.LABEL_PARAMETRO_PROTOCOLLO_DI_HTML_ESCAPE);
					return false;
				}
			}
			else {
				if(modalitaPresenti) {
					this.pd.setMessage("Se all'utente viene assegnato solamente il Permesso 'U' non deve essere selezionata alcun "+org.openspcoop2.core.constants.Costanti.LABEL_PARAMETRO_PROTOCOLLO_DI_HTML_ESCAPE);
					return false;
				}
			}
			
			// Se è stato selezionato solo la configurazione senza il permesso dei servizi non ha senso.
			if(!singlePdD){
				/*if( ((isServizi == null) || !isServizi.equals("yes")) &&
						((isMessaggi == null) || !isMessaggi.equals("yes")) &&
						((isSistema != null) && isSistema.equals("yes")) ) {
					this.pd.setMessage("Il Permesso 'C' è selezionabile solo in combinazione con i permessi 'S' e/o 'M'");
					return false;
				}*/
			}

			// Controllo che non esistano altri utenti con lo stesso nome
			if (TipoOperazione.ADD.equals(tipoOperazione)) {
				boolean trovato = this.utentiCore.existsUser(nomesu);
				if (trovato) {
					this.pd.setMessage("Esiste gi&agrave; un utente con nome " + nomesu);
					return false;
				}
			}

			// Se sono in modifica e l'utente modificato non ha il
			// permesso U, controllo che non fosse proprio l'unico
			// utente ad avere il permesso U
			if (TipoOperazione.CHANGE.equals(tipoOperazione) &&
					(isUtenti == null || !ServletUtils.isCheckBoxEnabled(isUtenti))) {
				List<String> usersWithU = this.utentiCore.getUsersWithType(Permessi.UTENTI.toString());
				if (usersWithU.size() == 1 &&
						usersWithU.get(0).equals(nomesu)) {
					this.pd.setMessage("Non è possibile eliminare il permesso 'Utenti', poichè non esistono altri utenti con tale permesso");
					return false;
				}
			}



			return true;

		} catch (Exception e) {
			this.log.error("Exception: " + e.getMessage(), e);
			throw new Exception(e);
		}
	}

	boolean changePwCheckData() throws Exception {

		try{

			String oldpw = this.getParameter(UtentiCostanti.PARAMETRO_UTENTE_VECCHIA_PW);
			String newpw = this.getParameter(UtentiCostanti.PARAMETRO_UTENTE_NUOVA_PW);
			String confpw = this.getParameter(UtentiCostanti.PARAMETRO_UTENTE_CONFERMA_NUOVA_PW);
			String changepwd = this.getParameter(UtentiCostanti.PARAMETRO_UTENTE_CHANGE_PW);

			if (!ServletUtils.isCheckBoxEnabled(changepwd)) {
				// non si vuole cambiare le pwd
				return true;
			}

			// String cpwd = this.procToCall.cryptPw(oldpw);
			User user = ServletUtils.getUserFromSession(this.request, this.session);

			if(user.getPermessi().isUtenti()==false){
				boolean trovato = this.utentiCore.getUtenzePasswordManager().check(oldpw, user.getPassword());
				if(!trovato && this.utentiCore.getUtenzePasswordManagerBackwardCompatibility()!=null) {
					trovato = this.utentiCore.getUtenzePasswordManagerBackwardCompatibility().check(oldpw, user.getPassword());
				}
				if (!trovato) {
					this.pd.setMessage("La vecchia password indicata non e' corretta");
					return false;
				}
				
				// Controllo che non ci siano spazi nei campi di testo
				if ((oldpw.indexOf(" ") != -1)) {
					this.pd.setMessage("Non inserire spazi nei campi di testo");
					return false;
				}
			}

			// Campi obbligatori
			if ( (newpw==null || newpw.equals("")) || (confpw==null || confpw.equals("")) ) {
				this.pd.setMessage("Dati incompleti. &Egrave; necessario indicare una password");
				return false;
			}

			// Controllo che non ci siano spazi nei campi di testo
			if ((newpw.indexOf(" ") != -1) || (confpw.indexOf(" ") != -1)) {
				this.pd.setMessage("Non inserire spazi nei campi di testo");
				return false;
			}

			// Controllo che la vecchia password e la nuova corrispondano
			if(user.getPermessi().isUtenti()==false){
				if (oldpw.equals(newpw)) {
					this.pd.setMessage("La nuova password deve essere differente dalla vecchia");
					return false;
				}
			}
			
			// Controllo che le password corrispondano
			if (!newpw.equals(confpw)) {
				this.pd.setMessage(UtentiCostanti.MESSAGGIO_ERRORE_PASSWORD_NUOVE_DIFFERENTI);
				return false;
			}

			PasswordVerifier passwordVerifier = this.utentiCore.getUtenzePasswordVerifier();
			if(passwordVerifier!=null){
				StringBuilder motivazioneErrore = new StringBuilder();
				if(passwordVerifier.validate(user.getLogin(), newpw, motivazioneErrore)==false){
					this.pd.setMessage(motivazioneErrore.toString());
					return false;
				}
				
				// controllo storico password in caso di change
				if(passwordVerifier.isHistory()) {
					List<UserPassword> precedentiPassword = user.getPrecedentiPassword();
					
//					String tmpPass = newpw;
//					if(this.utentiCore.isUtenzePasswordEncryptEnabled()) {
//						tmpPass = this.utentiCore.getUtenzePasswordManager().crypt(newpw);
//					}
					
					if(precedentiPassword == null || precedentiPassword.isEmpty()) {
						boolean trovato = this.utentiCore.getUtenzePasswordManager().check(newpw, user.getPassword());
						if(!trovato && this.utentiCore.getUtenzePasswordManagerBackwardCompatibility()!=null) {
							trovato = this.utentiCore.getUtenzePasswordManagerBackwardCompatibility().check(newpw, user.getPassword());
						}
						if (trovato) {
							this.pd.setMessage(UtentiCostanti.MESSAGGIO_ERRORE_PASSWORD_GIA_UTILIZZATA);
							return false;
						}
					}
					
					for (UserPassword userPassword : precedentiPassword) {
						boolean trovato = this.utentiCore.getUtenzePasswordManager().check(newpw, userPassword.getPassword());
						if(!trovato && this.utentiCore.getUtenzePasswordManagerBackwardCompatibility()!=null) {
							trovato = this.utentiCore.getUtenzePasswordManagerBackwardCompatibility().check(newpw, userPassword.getPassword());
						}
						if (trovato) {
							this.pd.setMessage(UtentiCostanti.MESSAGGIO_ERRORE_PASSWORD_GIA_UTILIZZATA);
							return false;
						}
					}
				}
			}
			
			return true;

		} catch (Exception e) {
			this.log.error("Exception: " + e.getMessage(), e);
			throw new Exception(e);
		}
	}

	public void prepareUtentiList(ISearch ricerca, List<User> lista, boolean singlePdD) throws Exception {
		try {
			ServletUtils.addListElementIntoSession(this.request, this.session, UtentiCostanti.OBJECT_NAME_UTENTI);

			String userLogin = ServletUtils.getUserLoginFromSession(this.session);

			int idLista = Liste.SU;
			int limit = ricerca.getPageSize(idLista);
			int offset = ricerca.getIndexIniziale(idLista);
			String search = ServletUtils.getSearchFromSession(ricerca, idLista);

			this.pd.setIndex(offset);
			this.pd.setPageSize(limit);
			this.pd.setNumEntries(ricerca.getNumEntries(idLista));

			// setto la barra del titolo
			if (search.equals("")) {
				this.pd.setSearchDescription("");
				ServletUtils.setPageDataTitle(this.pd, 
						new Parameter(UtentiCostanti.LABEL_UTENTI, UtentiCostanti.SERVLET_NAME_UTENTI_LIST));
			}
			else{
				ServletUtils.setPageDataTitle(this.pd,
						new Parameter(UtentiCostanti.LABEL_UTENTI, UtentiCostanti.SERVLET_NAME_UTENTI_LIST),
						new Parameter(Costanti.PAGE_DATA_TITLE_LABEL_RISULTATI_RICERCA, null));	
			}

			// controllo eventuali risultati ricerca
			if (!search.equals("")) {
				ServletUtils.enabledPageDataSearch(this.pd, UtentiCostanti.LABEL_UTENTI, search);
			}

			// setto le label delle colonne
			String[] labels = { 
					"",
					UtentiCostanti.LABEL_UTENTE, UtentiCostanti.LABEL_MODALITA_INTERFACCIA, 
					UtentiCostanti.LABEL_MODALITA_GATEWAY_COMPACT, 
					UtentiCostanti.LABEL_PERMESSI_GESTIONE, UtentiCostanti.LABEL_CAMBIA_IDENTITA };
			this.pd.setLabels(labels);

			// preparo i dati
			List<List<DataElement>> dati = new ArrayList<>();
			
			PasswordVerifier passwordVerifier = this.utentiCore.getUtenzePasswordVerifier();

			if (lista != null) {
				Iterator<User> it = lista.iterator();
				while (it.hasNext()) {
					User mySU = it.next();

					List<DataElement> e = new ArrayList<>();

					// Stato utente
					DataElement de = new DataElement();
					de.setWidthPx(10);
					de.setType(DataElementType.CHECKBOX);
					if(mySU.isConfigurazioneValidaAbilitazioni()){
						de.setSelected(CheckboxStatusType.ABILITATO);
					}
					else{
						de.setToolTip(mySU.getReasonInvalidConfiguration());
						de.setValue(mySU.getReasonInvalidConfiguration());
						de.setSelected(CheckboxStatusType.DISABILITATO);
					}
					
					// se e' abilitato il check di scadenza delle password e la password e' scaduta imposto il check rosso
					if(this.core.isCheckPasswordExpire(passwordVerifier)) {
						if(mySU.isCheckLastUpdatePassword()) {
							if(passwordVerifier.isPasswordExpire(mySU.getLastUpdatePassword())) {
								de.setToolTip(UtentiCostanti.LABEL_UTENTI_SCADENZA_PW_SCADUTA);
								de.setValue(UtentiCostanti.LABEL_UTENTI_SCADENZA_PW_SCADUTA);
								de.setSelected(CheckboxStatusType.DISABILITATO);
							}
						}
					}
					
					de.setUrl(UtentiCostanti.SERVLET_NAME_UTENTI_CHANGE,
							new Parameter(UtentiCostanti.PARAMETRO_UTENTI_USERNAME, mySU.getLogin()));
					e.add(de);
					
					// nome utente
					de = new DataElement();
					de.setUrl(UtentiCostanti.SERVLET_NAME_UTENTI_CHANGE,
							new Parameter(UtentiCostanti.PARAMETRO_UTENTI_USERNAME, mySU.getLogin()));
					de.setIdToRemove(mySU.getId().toString());
					de.setValue(mySU.getLogin());
					e.add(de);

					// modalita interfaccia
					de = new DataElement();
					if(mySU.hasOnlyPermessiUtenti()) {
						de.setValue("-");
					}
					else {
						de.setValue(mySU.getInterfaceType().toString().toLowerCase());
					}
					e.add(de);
					
					// modalita gateway
					de = new DataElement();
					
					List<String> protocolliSupportati = mySU.getProtocolliSupportati();
					if(protocolliSupportati == null)
						protocolliSupportati = new ArrayList<>();
					
					if(mySU.hasOnlyPermessiUtenti()) {
						de.setValue("-");
					}
					else {
						if(protocolliSupportati.size() > 0) {
							Collections.sort(protocolliSupportati);
							List<String> protocolliInstallati = this.core.getProtocolli();
							Collections.sort(protocolliInstallati);
							
							String labelProtocolli = null;
							if(protocolliSupportati.size() == protocolliInstallati.size()) {
								boolean all = true;
								for (int i = 0; i < protocolliInstallati.size(); i++) {
									String pI = protocolliInstallati.get(i);
									String pS = protocolliSupportati.get(i);
									
									if(!pI.equals(pS)) {
										all=false;
										break;
									}
								}
								
								if(all)
									labelProtocolli = UtentiCostanti.LABEL_PARAMETRO_MODALITA_ALL;
							}
							
							if(labelProtocolli == null) {
								StringBuilder sb = new StringBuilder();
								for (int i = 0; i < protocolliSupportati.size(); i++) {
									String pS = protocolliSupportati.get(i);
									if(sb.length() > 0)
										sb.append(", ");
									
									sb.append(this.getLabelProtocollo(pS));
								}
								labelProtocolli = sb.toString();
							}
							
							de.setValue(labelProtocolli);
						} else {
							de.setValue(UtentiCostanti.LABEL_PARAMETRO_MODALITA_ALL);
						}
					}
					e.add(de);

					// permessi utente
					de = new DataElement();
					if(singlePdD){
						de.setValue(mySU.getPermessi().toString(","));
					}else{
						PermessiUtente maschera = new PermessiUtente();
						maschera.setAuditing(true);
						maschera.setServizi(true);
						maschera.setSistema(true);
						maschera.setUtenti(true);
						maschera.setCodeMessaggi(true);
						maschera.setAccordiCooperazione(true);
						de.setValue(mySU.getPermessi().toString(",",maschera));
					}
					e.add(de);

					// login as su
					de = new DataElement();
					if (!userLogin.equals(mySU.getLogin()) && !this.hasOnlyPermessiDiagnosticaReportistica(mySU) &&
							mySU.isConfigurazioneValidaAbilitazioni()) {
						de.setUrl(LoginCostanti.SERVLET_NAME_LOGIN_AS_SU,
								new Parameter(LoginCostanti.PARAMETRO_LOGIN_LOGIN, mySU.getLogin()));
						de.setValue(UtentiCostanti.LABEL_ACCEDI);
					} else
						de.setValue("");
					e.add(de);

					dati.add(e);
				}
			}

			this.pd.setDati(dati);
			this.pd.setAddButton(true);
		} catch (Exception e) {
			this.log.error("Exception: " + e.getMessage(), e);
			throw new Exception(e);
		}
	}
	
	public List<String> controlloModalitaUtenteDaEliminare(List<String> nomiUtentiDaRimuovere, List<String> utentiDaNonEliminare, User user) throws DriverRegistroServiziException, DriverUsersDBException {
		return controlloModalitaUtenteDaEliminare(nomiUtentiDaRimuovere, utentiDaNonEliminare, user, user.getProtocolliSupportati());
	}
	
	public List<String> controlloModalitaUtenteDaEliminare(List<String> nomiUtentiDaRimuovere, List<String> utentiDaNonEliminare, User user, List<String> protocolliSupportati) throws DriverRegistroServiziException, DriverUsersDBException {
		List<String> protocolliNonPiuAssociati = new ArrayList<>();
		if(protocolliSupportati != null && protocolliSupportati.size() > 0) {
			for (String protocollo : protocolliSupportati) {
				boolean protocolloNonPiuAssociato = controllaEsistenzaUtentePerAssociareIlProtocollo(nomiUtentiDaRimuovere, utentiDaNonEliminare, user.getLogin(), protocollo);
				if(protocolloNonPiuAssociato)
				protocolliNonPiuAssociati.add(protocollo);
			}
		}
		
		return protocolliNonPiuAssociati;
	}

	public boolean controllaEsistenzaUtentePerAssociareIlProtocollo(List<String> nomiUtentiDaRimuovere, List<String> utentiDaNonEliminare, String userLogin,
			String protocollo) throws DriverRegistroServiziException, DriverUsersDBException {
		boolean protocolloNonPiuAssociato = false;
		boolean existsAlmostOneOrganization = this.utentiCore.existsAlmostOneOrganization(null, userLogin, protocollo);
		if(existsAlmostOneOrganization) {
			List<String> usersByProtocolloSupportatoTmp = this.utentiCore.getUsersByProtocolloSupportato(protocollo,true);
			
			List<String> usersByProtocolloSupportato = new ArrayList<>();
			for (String uDE : usersByProtocolloSupportatoTmp) {
				if(nomiUtentiDaRimuovere.contains(uDE) == false) {
					usersByProtocolloSupportato.add(uDE);
				}
			}
			
			if(usersByProtocolloSupportato.size() < 1) {
				protocolloNonPiuAssociato = true;
				if(!utentiDaNonEliminare.contains(userLogin))
					utentiDaNonEliminare.add(userLogin);
			}
		}
		
		return protocolloNonPiuAssociato;
	}
	
	
	/***
	 * Restituisce true se l'utente User puo' gestire tutte le modalita' dell'utente userToCheck
	 * 
	 * @param userLoginToCheck
	 * @param userLogin
	 * @return true se l'utente User puo' gestire tutte le modalita' dell'utente userToCheck
	 * @throws DriverUsersDBException 
	 */
	public boolean checkUsersModalitaGatewayCompatibili(String userLoginToCheck, String userLogin) throws DriverUsersDBException {
		User userToCheck = this.utentiCore.getUser(userLoginToCheck);
		User user = this.utentiCore.getUser(userLogin);
		return checkUsersModalitaGatewayCompatibili(userToCheck, user);
	}
	
	/***
	 * Restituisce true se l'utente User puo' gestire tutte le modalita' dell'utente userToCheck
	 * 
	 * @param userToCheck
	 * @param user
	 * @return true se l'utente User puo' gestire tutte le modalita' dell'utente userToCheck
	 */
	public boolean checkUsersModalitaGatewayCompatibili(User userToCheck, User user) {
		// utente che sto controllando e' un utente che gestisce solo utenti, non ci sono controlli da effettuare
		if(userToCheck.hasOnlyPermessiUtenti()) return true;
		
		// se l'utente destinazione possiede tutti i protocolli allora non ci sono controlli da fare
		List<String> protocolliRegistratiConsole = this.utentiCore.getProtocolli();
		if(user.getProtocolliSupportati() == null) return true;
		
		boolean userHasAll= true;
		for (String protocolloConsole : protocolliRegistratiConsole) {
			if(!user.getProtocolliSupportati().contains(protocolloConsole)) {
				userHasAll = false;
				break;
			}
		}
		
		// se l'utente li ha tutti il controllo e' ok
		if(userHasAll) return true;
		
		// l'utente destinazione non gestisce tutti i protocolli, se userTocheck li aveva tutti allora user non puo' gestire tutte le modalita di userTocheck
		if(userToCheck.getProtocolliSupportati() == null) return false;
		
		boolean usertoCheckHasAll= true;
		for (String protocolloConsole : protocolliRegistratiConsole) {
			if(!userToCheck.getProtocolliSupportati().contains(protocolloConsole)) {
				usertoCheckHasAll = false;
				break;
			}
		}
		
		if(usertoCheckHasAll) return false;
		
		// a questo punto nessuno dei due utenti possiede tutte le modalita' 
		// cerco semplicemente la prima modalita' posseduta da userToCheck che non puo' essere gestita da user.
		for (String protocolloUserToCheck : userToCheck.getProtocolliSupportati()) {
			if(!user.getProtocolliSupportati().contains(protocolloUserToCheck))
				return false;
		}
		
		return true;
		
	}

	public void prepareUtentiSoggettiList(ConsoleSearch ricerca, List<IDSoggetto> lista, User user) throws Exception{
		try {
			ServletUtils.addListElementIntoSession(this.request, this.session, UtentiCostanti.OBJECT_NAME_UTENTI_SOGGETTI,
					new Parameter(UtentiCostanti.PARAMETRO_UTENTI_USERNAME, user.getLogin()));

			int idLista = Liste.UTENTI_SOGGETTI;
			int limit = ricerca.getPageSize(idLista);
			int offset = ricerca.getIndexIniziale(idLista);
			String search = ServletUtils.getSearchFromSession(ricerca, idLista);
			
			addFilterProtocol(ricerca, idLista, user.getProtocolliSupportati());

			this.pd.setIndex(offset);
			this.pd.setPageSize(limit);
			this.pd.setNumEntries(ricerca.getNumEntries(idLista));
			
			boolean showProtocolli = (user.getProtocolliSupportati() != null && user.getProtocolliSupportati().size() >1);
			
			List<Parameter> lstParam = new ArrayList<>();
			lstParam.add(new Parameter(UtentiCostanti.LABEL_UTENTI ,UtentiCostanti.SERVLET_NAME_UTENTI_LIST));
			lstParam.add(new Parameter(user.getLogin(), UtentiCostanti.SERVLET_NAME_UTENTI_CHANGE, new Parameter(UtentiCostanti.PARAMETRO_UTENTI_USERNAME, user.getLogin())));
			// setto la barra del titolo
			if (search.equals("")) {
				this.pd.setSearchDescription("");
				lstParam.add(new Parameter(UtentiCostanti.LABEL_UTENTI_SOGGETTI, null));
			}else {
				lstParam.add(new Parameter(UtentiCostanti.LABEL_UTENTI_SOGGETTI, UtentiCostanti.SERVLET_NAME_UTENTI_SOGGETTI_LIST,
						new Parameter(UtentiCostanti.PARAMETRO_UTENTI_USERNAME, user.getLogin())));
				lstParam.add(new Parameter(Costanti.PAGE_DATA_TITLE_LABEL_RISULTATI_RICERCA, null));	
			}
			
			// controllo eventuali risultati ricerca
			this.pd.setSearchLabel(UtentiCostanti.LABEL_PARAMETRO_UTENTI_SOGGETTO);
			if (!search.equals("")) {
				ServletUtils.enabledPageDataSearch(this.pd, UtentiCostanti.LABEL_UTENTI_SOGGETTI, search);
			}
			
			ServletUtils.setPageDataTitle(this.pd,lstParam); 
					
			// setto le label delle colonne
			int totEl = 1;
			if( showProtocolli ) {
				totEl++;
			}
			String[] labels = new String[totEl];
			int i = 0;
			labels[i++] = UtentiCostanti.LABEL_PARAMETRO_UTENTI_SOGGETTO;
			if( showProtocolli ) {
				labels[i++] = UtentiCostanti.LABEL_PARAMETRO_UTENTI_PROTOCOLLO;
			}
			this.pd.setLabels(labels);

			// preparo i dati
			List<List<DataElement>> dati = new ArrayList<>();
			
			if (lista != null) {
				Iterator<IDSoggetto> it = lista.iterator();
				while (it.hasNext()) {
					IDSoggetto soggetto = it.next();

					List<DataElement> e = new ArrayList<>();

					String protocollo = this.soggettiCore.getProtocolloAssociatoTipoSoggetto(soggetto.getTipo());
					// soggetto
					DataElement de = new DataElement();
					de.setValue(this.getLabelNomeSoggetto(protocollo, soggetto.getTipo(), soggetto.getNome()));
					de.setIdToRemove(soggetto.getTipo() + "/" + soggetto.getNome());
					de.setToolTip(this.getLabelNomeSoggetto(protocollo, soggetto.getTipo(), soggetto.getNome()));
					e.add(de);
					
					if(showProtocolli) {
						de = new DataElement();
						de.setValue(this.getLabelProtocollo(this.soggettiCore.getProtocolloAssociatoTipoSoggetto(soggetto.getTipo())));
						e.add(de);
					}
					
					dati.add(e);
				}
			}
					
			this.pd.setDati(dati);
			this.pd.setAddButton(true);
		} catch (Exception e) {
			this.log.error("Exception: " + e.getMessage(), e);
			throw new Exception(e);
		}
	}
	
	public void prepareUtentiServiziList(ConsoleSearch ricerca, List<IDServizio> lista, User user) throws Exception{
		try {
			ServletUtils.addListElementIntoSession(this.request, this.session, UtentiCostanti.OBJECT_NAME_UTENTI_SERVIZI,
					new Parameter(UtentiCostanti.PARAMETRO_UTENTI_USERNAME, user.getLogin()));

			int idLista = Liste.UTENTI_SERVIZI;
			int limit = ricerca.getPageSize(idLista);
			int offset = ricerca.getIndexIniziale(idLista);
			String search = ServletUtils.getSearchFromSession(ricerca, idLista);
			
			addFilterProtocol(ricerca, idLista, user.getProtocolliSupportati());

			this.pd.setIndex(offset);
			this.pd.setPageSize(limit);
			this.pd.setNumEntries(ricerca.getNumEntries(idLista));
			
			boolean showProtocolli = (user.getProtocolliSupportati() != null && user.getProtocolliSupportati().size() >1);
			
			List<Parameter> lstParam = new ArrayList<>();
			lstParam.add(new Parameter(UtentiCostanti.LABEL_UTENTI ,UtentiCostanti.SERVLET_NAME_UTENTI_LIST));
			lstParam.add(new Parameter(user.getLogin(), UtentiCostanti.SERVLET_NAME_UTENTI_CHANGE, new Parameter(UtentiCostanti.PARAMETRO_UTENTI_USERNAME, user.getLogin())));
			// setto la barra del titolo
			if (search.equals("")) {
				this.pd.setSearchDescription("");
				lstParam.add(new Parameter(UtentiCostanti.LABEL_UTENTI_SERVIZI, null));
			}else {
				lstParam.add(new Parameter(UtentiCostanti.LABEL_UTENTI_SERVIZI, UtentiCostanti.SERVLET_NAME_UTENTI_SERVIZI_LIST,
						new Parameter(UtentiCostanti.PARAMETRO_UTENTI_USERNAME, user.getLogin())));
				lstParam.add(new Parameter(Costanti.PAGE_DATA_TITLE_LABEL_RISULTATI_RICERCA, null));	
			}
			
			// controllo eventuali risultati ricerca
			this.pd.setSearchLabel(UtentiCostanti.LABEL_PARAMETRO_UTENTI_SERVIZIO);
			if (!search.equals("")) {
				ServletUtils.enabledPageDataSearch(this.pd, UtentiCostanti.LABEL_UTENTI_SERVIZI, search);
			}
			
			ServletUtils.setPageDataTitle(this.pd,lstParam); 
					
			// setto le label delle colonne
			int totEl = 2;
			if( showProtocolli ) {
				totEl++;
			}
			String[] labels = new String[totEl];
			int i = 0;
			labels[i++] = UtentiCostanti.LABEL_PARAMETRO_UTENTI_SERVIZIO;
			labels[i++] = UtentiCostanti.LABEL_PARAMETRO_UTENTI_SOGGETTO_EROGATORE;
			if( showProtocolli ) {
				labels[i++] = UtentiCostanti.LABEL_PARAMETRO_UTENTI_PROTOCOLLO;
			}
			this.pd.setLabels(labels);
			this.pd.setLabels(labels);

			// preparo i dati
			List<List<DataElement>> dati = new ArrayList<>();
			
			if (lista != null) {
				Iterator<IDServizio> it = lista.iterator();
				while (it.hasNext()) {
					IDServizio servizio = it.next();
					List<DataElement> e = new ArrayList<>();

					String protocollo = this.soggettiCore.getProtocolloAssociatoTipoSoggetto(servizio.getTipo());
					String uriASPS = this.idServizioFactory.getUriFromIDServizio(servizio);

					// servizio
					DataElement de = new DataElement();
					de.setValue(this.getLabelNomeServizio(protocollo, servizio.getTipo(), servizio.getNome(), servizio.getVersione()));
					de.setIdToRemove(uriASPS);
					e.add(de);
					
					// soggetto
					de = new DataElement();
					de.setValue(this.getLabelNomeSoggetto(protocollo, servizio.getSoggettoErogatore().getTipo(), servizio.getSoggettoErogatore().getNome()));
					e.add(de);
					
					if(showProtocolli) {
						de = new DataElement();
						de.setValue(this.getLabelProtocollo(protocollo));
						e.add(de);
					}
					
					dati.add(e);
				}
			}
					
			this.pd.setDati(dati);
			this.pd.setAddButton(true);
		} catch (Exception e) {
			this.log.error("Exception: " + e.getMessage(), e);
			throw new Exception(e);
		}
	}

	public void addUtentiSoggettiToDati(List<DataElement> dati, TipoOperazione tipoOperazione, String nomesu, String soggetto, String[] soggettiValues, String[] soggettiLabels, List<String> listaTipiProtocollo, String tipoProtocollo) throws Exception {

		DataElement de = new DataElement();
		de.setLabel(UtentiCostanti.LABEL_UTENTI_SOGGETTI);
		de.setType(DataElementType.TITLE);
		dati.add(de);

		de = new DataElement();
		de.setLabel(UtentiCostanti.LABEL_PARAMETRO_UTENTI_USERNAME);
		de.setValue(nomesu);
		de.setType(DataElementType.HIDDEN);
		de.setName(UtentiCostanti.PARAMETRO_UTENTI_USERNAME);
		dati.add(de);
		
		de = new DataElement();
		de.setLabel(UtentiCostanti.LABEL_PARAMETRO_UTENTI_PROTOCOLLO);
		de.setName(UtentiCostanti.PARAMETRO_UTENTI_PROTOCOLLO);
		if(listaTipiProtocollo != null && listaTipiProtocollo.size() > 1){
				de.setLabel(UtentiCostanti.LABEL_PARAMETRO_UTENTI_PROTOCOLLO);
				de.setValues(listaTipiProtocollo);
				de.setLabels(this.getLabelsProtocolli(listaTipiProtocollo));
				de.setSelected(tipoProtocollo);
				de.setType(DataElementType.SELECT);
				de.setPostBack(true);
		} else {
			de.setValue(tipoProtocollo);
			de.setType(DataElementType.HIDDEN);
		}
		
		de.setSize(this.getSize());
		dati.add(de);
		
		de = new DataElement();
		de.setLabel(UtentiCostanti.LABEL_PARAMETRO_UTENTI_SOGGETTO);
		de.setName(UtentiCostanti.PARAMETRO_UTENTI_SOGGETTO);
		if(soggettiValues != null && soggettiValues.length > 1){
			de.setType(DataElementType.SELECT);		
			de.setValues(soggettiValues);
			de.setLabels(soggettiLabels);
			de.setSelected(soggetto);
			de.setRequired(true);
		} else {
			de.setValue(soggetto);
			de.setType(DataElementType.HIDDEN);
		}
		dati.add(de);
		
	}
	
	public void addUtentiServiziToDati(List<DataElement> dati, TipoOperazione tipoOperazione, String nomesu, String servizio, String[] serviziValues, String[] serviziLabels, List<String> listaTipiProtocollo, String tipoProtocollo) throws Exception {

		DataElement de = new DataElement();
		de.setLabel(UtentiCostanti.LABEL_UTENTI_SERVIZI);
		de.setType(DataElementType.TITLE);
		dati.add(de);

		de = new DataElement();
		de.setLabel(UtentiCostanti.LABEL_PARAMETRO_UTENTI_USERNAME);
		de.setValue(nomesu);
		de.setType(DataElementType.HIDDEN);
		de.setName(UtentiCostanti.PARAMETRO_UTENTI_USERNAME);
		dati.add(de);
		
		de = new DataElement();
		de.setLabel(UtentiCostanti.LABEL_PARAMETRO_UTENTI_PROTOCOLLO);
		de.setName(UtentiCostanti.PARAMETRO_UTENTI_PROTOCOLLO);
		if(listaTipiProtocollo != null && listaTipiProtocollo.size() > 1){
				de.setLabel(UtentiCostanti.LABEL_PARAMETRO_UTENTI_PROTOCOLLO);
				de.setValues(listaTipiProtocollo);
				de.setLabels(this.getLabelsProtocolli(listaTipiProtocollo));
				de.setSelected(tipoProtocollo);
				de.setType(DataElementType.SELECT);
				de.setPostBack(true);
		} else {
			de.setValue(tipoProtocollo);
			de.setType(DataElementType.HIDDEN);
		}
		de.setSize(this.getSize());
		dati.add(de);
		
		de = new DataElement();
		de.setLabel(UtentiCostanti.LABEL_PARAMETRO_UTENTI_SERVIZIO);
		de.setName(UtentiCostanti.PARAMETRO_UTENTI_SERVIZIO);
		if(serviziValues != null && serviziValues.length > 1){
			de.setType(DataElementType.SELECT);		
			de.setValues(serviziValues);
			de.setLabels(serviziLabels);
			de.setSelected(servizio);
			de.setRequired(true);
		} else {
			de.setValue(servizio);
			de.setType(DataElementType.HIDDEN);
		}
		dati.add(de);
		
	}

	public boolean utentiSoggettiCheckData(TipoOperazione tipoOperazione, String nomesu, String soggetto) throws Exception{
		// controllo selezione soggetto
		if (StringUtils.isEmpty(soggetto) || soggetto.equals(CostantiControlStation.DEFAULT_VALUE_NON_SELEZIONATO)) {
			this.pd.setMessage(UtentiCostanti.MESSAGGIO_ERRORE_NOME_SOGGETTO_OBBLIGATORIO);
			return false;
		}
		
		return true;
	}
	
	public boolean utentiServiziCheckData(TipoOperazione tipoOperazione, String nomesu, String servizio) throws Exception{
		// controllo selezione soggetto
		if (StringUtils.isEmpty(servizio) || servizio.equals(CostantiControlStation.DEFAULT_VALUE_NON_SELEZIONATO)) {
			this.pd.setMessage(UtentiCostanti.MESSAGGIO_ERRORE_NOME_SERVIZIO_OBBLIGATORIO);
			return false;
		}
		
		return true;
	}
	
	public void addUtenteChangePasswordScadutaToDati(List<DataElement> dati, TipoOperazione tipoOperazione) throws Exception{

		DataElement de = new DataElement();
		de.setName(UtentiCostanti.PARAMETRO_UTENTI_FIRST);
		de.setType(DataElementType.HIDDEN);
		de.setValue("false");
		dati.add(de);
		
		de = new DataElement();
		de.setLabel(UtentiCostanti.LABEL_PASSWORD);
		de.setType(DataElementType.TITLE);
		dati.add(de);

		PasswordVerifier passwordVerifier = this.utentiCore.getUtenzePasswordVerifier();
		
		de = new DataElement();
		de.setLabel(UtentiCostanti.LABEL_PARAMETRO_UTENTE_VECCHIA_PW);
		de.setType(DataElementType.CRYPT);
		de.setName(UtentiCostanti.PARAMETRO_UTENTE_VECCHIA_PW);
		de.setValue("");
		de.setSize(this.getSize());
		de.setRequired(true);
		dati.add(de);
			
		de = new DataElement();
		de.setLabel(UtentiCostanti.LABEL_PARAMETRO_UTENTE_NUOVA_PW);
		de.setType(DataElementType.CRYPT);
		de.setName(UtentiCostanti.PARAMETRO_UTENTE_NUOVA_PW);
		de.setSize(this.getSize());
		de.setValue("");
		de.setRequired(true);
		dati.add(de);

		de = new DataElement();
		de.setLabel(UtentiCostanti.LABEL_PARAMETRO_UTENTE_CONFERMA_NUOVA_PW);
		de.setType(DataElementType.CRYPT);
		de.setName(UtentiCostanti.PARAMETRO_UTENTE_CONFERMA_NUOVA_PW);
		de.setSize(this.getSize());
		de.setValue("");
		de.setRequired(true);
		if(passwordVerifier!=null){
			de.setNote(passwordVerifier.helpUpdate(org.openspcoop2.core.constants.Costanti.WEB_NEW_LINE));
		}
		dati.add(de);

		de = new DataElement();
		de.setType(DataElementType.HIDDEN);
		de.setName(UtentiCostanti.PARAMETRO_UTENTE_ESEGUI);
		de.setValue(UtentiCostanti.PARAMETRO_UTENTE_ESEGUI);
		dati.add(de);
	}
	
	boolean changePwScadutaCheckData() throws Exception {

		try{

			String oldpw = this.getParameter(UtentiCostanti.PARAMETRO_UTENTE_VECCHIA_PW);
			String newpw = this.getParameter(UtentiCostanti.PARAMETRO_UTENTE_NUOVA_PW);
			String confpw = this.getParameter(UtentiCostanti.PARAMETRO_UTENTE_CONFERMA_NUOVA_PW);

			// String cpwd = this.procToCall.cryptPw(oldpw);
			String userToUpdate = ServletUtils.getObjectFromSession(this.request, this.session, String.class, LoginCostanti.ATTRIBUTO_MODALITA_CAMBIA_PWD_SCADUTA);
			
			User user = this.utentiCore.getUser(userToUpdate);

			boolean trovato = this.utentiCore.getUtenzePasswordManager().check(oldpw, user.getPassword());
			if(!trovato && this.utentiCore.getUtenzePasswordManagerBackwardCompatibility()!=null) {
				trovato = this.utentiCore.getUtenzePasswordManagerBackwardCompatibility().check(oldpw, user.getPassword());
			}
			if (!trovato) {
				this.pd.setMessage("La vecchia password indicata non &egrave; corretta");
				return false;
			}
			
			// Controllo che non ci siano spazi nei campi di testo
			if ((oldpw.indexOf(" ") != -1)) {
				this.pd.setMessage("Non inserire spazi nei campi di testo");
				return false;
			}

			// Campi obbligatori
			if (newpw.equals("") || confpw.equals("")) {
				this.pd.setMessage("Dati incompleti. &Egrave; necessario indicare una password");
				return false;
			}

			// Controllo che non ci siano spazi nei campi di testo
			if ((newpw.indexOf(" ") != -1) || (confpw.indexOf(" ") != -1)) {
				this.pd.setMessage("Non inserire spazi nei campi di testo");
				return false;
			}

			// Controllo che la vecchia password e la nuova corrispondano
			if (oldpw.equals(newpw)) {
				this.pd.setMessage("La nuova password deve essere differente dalla vecchia");
				return false;
			}
			
			// Controllo che le password corrispondano
			if (!newpw.equals(confpw)) {
				this.pd.setMessage(UtentiCostanti.MESSAGGIO_ERRORE_PASSWORD_NUOVE_DIFFERENTI);
				return false;
			}

			PasswordVerifier passwordVerifier = this.utentiCore.getUtenzePasswordVerifier();
			if(passwordVerifier!=null){
				StringBuilder motivazioneErrore = new StringBuilder();
				if(passwordVerifier.validate(user.getLogin(), newpw, motivazioneErrore)==false){
					this.pd.setMessage(motivazioneErrore.toString());
					return false;
				}
				
				// controllo storico password in caso di change
				if(passwordVerifier.isHistory()) {
					List<UserPassword> precedentiPassword = user.getPrecedentiPassword();
					
//					String tmpPass = newpw;
//					if(this.utentiCore.isUtenzePasswordEncryptEnabled()) {
//						tmpPass = this.utentiCore.getUtenzePasswordManager().crypt(newpw);
//					}
					
					if(precedentiPassword == null || precedentiPassword.isEmpty()) {
						trovato = this.utentiCore.getUtenzePasswordManager().check(newpw, user.getPassword());
						if(!trovato && this.utentiCore.getUtenzePasswordManagerBackwardCompatibility()!=null) {
							trovato = this.utentiCore.getUtenzePasswordManagerBackwardCompatibility().check(newpw, user.getPassword());
						}
						if (trovato) {
							this.pd.setMessage(UtentiCostanti.MESSAGGIO_ERRORE_PASSWORD_GIA_UTILIZZATA);
							return false;
						}
					}
					
					for (UserPassword userPassword : precedentiPassword) {
						trovato = this.utentiCore.getUtenzePasswordManager().check(newpw, userPassword.getPassword());
						if(!trovato && this.utentiCore.getUtenzePasswordManagerBackwardCompatibility()!=null) {
							trovato = this.utentiCore.getUtenzePasswordManagerBackwardCompatibility().check(newpw, userPassword.getPassword());
						}
						if (trovato) {
							this.pd.setMessage(UtentiCostanti.MESSAGGIO_ERRORE_PASSWORD_GIA_UTILIZZATA);
							return false;
						}
					}
				}
			}
			
			return true;

		} catch (Exception e) {
			this.log.error("Exception: " + e.getMessage(), e);
			throw new Exception(e);
		}
	}
	
	public String incapsulaValoreStato(String valoreStato) {
		if(valoreStato!=null){
			valoreStato = "{" + valoreStato + "}"; // trasformo in json
		}
		return valoreStato;
	}

	public String extractValoreStato(String valoreStato) {
		if(valoreStato!=null){
			if(valoreStato.startsWith("{")){
				valoreStato = valoreStato.substring(1);
			}
			if(valoreStato.endsWith("}")){
				valoreStato = valoreStato.substring(0, (valoreStato.length()-1) );
			}
		}
		return  valoreStato;
	}
}
