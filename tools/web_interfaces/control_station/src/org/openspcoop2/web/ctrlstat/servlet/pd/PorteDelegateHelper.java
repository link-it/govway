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
package org.openspcoop2.web.ctrlstat.servlet.pd;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.openspcoop2.core.commons.ISearch;
import org.openspcoop2.core.commons.Liste;
import org.openspcoop2.core.config.CorrelazioneApplicativaElemento;
import org.openspcoop2.core.config.CorrelazioneApplicativaRispostaElemento;
import org.openspcoop2.core.config.MessageSecurity;
import org.openspcoop2.core.config.MessageSecurityFlowParameter;
import org.openspcoop2.core.config.MtomProcessorFlowParameter;
import org.openspcoop2.core.config.PortaDelegata;
import org.openspcoop2.core.config.ServizioApplicativo;
import org.openspcoop2.core.config.constants.CostantiConfigurazione;
import org.openspcoop2.core.config.constants.CredenzialeTipo;
import org.openspcoop2.core.config.constants.MTOMProcessorType;
import org.openspcoop2.core.config.driver.DriverConfigurazioneNotFound;
import org.openspcoop2.core.id.IDPortaDelegata;
import org.openspcoop2.core.id.IDServizioApplicativo;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.core.registry.Soggetto;
import org.openspcoop2.core.registry.constants.CostantiRegistroServizi;
import org.openspcoop2.core.registry.driver.DriverRegistroServiziException;
import org.openspcoop2.core.registry.driver.DriverRegistroServiziNotFound;
import org.openspcoop2.protocol.sdk.constants.FunzionalitaProtocollo;
import org.openspcoop2.web.ctrlstat.costanti.CostantiControlStation;
import org.openspcoop2.web.ctrlstat.costanti.IdentificazioneView;
import org.openspcoop2.web.ctrlstat.driver.DriverControlStationException;
import org.openspcoop2.web.ctrlstat.driver.DriverControlStationNotFound;
import org.openspcoop2.web.ctrlstat.plugins.IExtendedListServlet;
import org.openspcoop2.web.ctrlstat.servlet.ConsoleHelper;
import org.openspcoop2.web.ctrlstat.servlet.pa.PorteApplicativeCostanti;
import org.openspcoop2.web.ctrlstat.servlet.sa.ServiziApplicativiCostanti;
import org.openspcoop2.web.ctrlstat.servlet.soggetti.SoggettiCostanti;
import org.openspcoop2.web.lib.mvc.Costanti;
import org.openspcoop2.web.lib.mvc.DataElement;
import org.openspcoop2.web.lib.mvc.DataElementType;
import org.openspcoop2.web.lib.mvc.PageData;
import org.openspcoop2.web.lib.mvc.Parameter;
import org.openspcoop2.web.lib.mvc.ServletUtils;
import org.openspcoop2.web.lib.mvc.TipoOperazione;
import org.openspcoop2.web.lib.users.dao.InterfaceType;
import org.openspcoop2.web.lib.users.dao.User;

/**
 * PorteDelegateHelper
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class PorteDelegateHelper extends ConsoleHelper {

	public PorteDelegateHelper(HttpServletRequest request, PageData pd, 
			HttpSession session) throws Exception {
		super(request, pd,  session);
	}


	public Vector<DataElement> addPorteDelegateToDati(TipoOperazione tipoOp, String idsogg,
			String nomePorta, Vector<DataElement> dati, String idPorta,
			String descr, String urlinv, String autenticazione,
			String autorizzazione, String modesp, String soggid,
			String[] soggettiList, String[] soggettiListLabel, String sp,
			String tiposp, String patternErogatore, String modeservizio,
			String servid, String[] serviziList, String[] serviziListLabel,
			String servizio, String tiposervizio, String patternServizio,
			String modeaz, String azid, String[] azioniListLabel,
			String[] azioniList, String azione, String patternAzione,
			long totAzioni,  String stateless, String localForward, String ricsim,
			String ricasim, String xsd, String tipoValidazione,
			int numCorrApp, String scadcorr, String gestBody,
			String gestManifest, String integrazione, String nomeauth,
			String nomeautor,String autorizzazioneContenuti, String idsogg2, String protocollo
			,int numSA,	String statoMessageSecurity,String statoMTOM ,int numCorrelazioneReq , 
			int numCorrelazioneRes,String forceWsdlBased, String applicaMTOM,
			boolean riusoId) throws DriverRegistroServiziException, DriverControlStationException, DriverRegistroServiziNotFound {



		Boolean confPers = (Boolean) this.session.getAttribute(CostantiControlStation.SESSION_PARAMETRO_GESTIONE_CONFIGURAZIONI_PERSONALIZZATE);
		Boolean contaListe = ServletUtils.getContaListeFromSession(this.session);

		boolean configurazioneStandardNonApplicabile = false;
		
		User user = ServletUtils.getUserFromSession(this.session);
		
		int alternativeSize = 80;
		
		DataElement de = null;

		de = new DataElement();
		de.setLabel(PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_ID_SOGGETTO);
		de.setValue(idsogg2);
		de.setType(DataElementType.HIDDEN);
		de.setName(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID_SOGGETTO);
		dati.addElement(de);

		de = new DataElement();
		de.setLabel(PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_ID);
		de.setValue(idPorta);
		de.setType(DataElementType.HIDDEN);
		de.setName(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID);
		dati.addElement(de);

		
		
		
		
		// *************** Nome/Descrizione *********************

		de = new DataElement();
		de.setLabel(PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_NOME);
		de.setValue(nomePorta);
		if( InterfaceType.STANDARD.equals(user.getInterfaceType()) && TipoOperazione.CHANGE.equals(tipoOp) ){
			de.setType(DataElementType.TEXT);
		}
		else{
			de.setType(DataElementType.TEXT_EDIT);
			de.setRequired(true);
		}
		de.setName(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_NOME_PORTA);
		de.setSize(alternativeSize);
		dati.addElement(de);

		de = new DataElement();
		de.setLabel(PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_DESCRIZIONE);
		de.setValue(descr);
		de.setType(DataElementType.TEXT_EDIT);
		de.setName(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_DESCRIZIONE);
		de.setSize(alternativeSize);
		dati.addElement(de);
		if ( (this.porteDelegateCore.isShowPortaDelegataUrlInvocazione()==false) || 
				InterfaceType.STANDARD.equals(user.getInterfaceType())) {
			de = new DataElement();
			de.setLabel(PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_URL_DI_INVOCAZIONE);
			de.setValue("");
			de.setType(DataElementType.HIDDEN);
			de.setName(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_URL_DI_INVOCAZIONE);
			dati.addElement(de);
		} else {
			de = new DataElement();
			de.setLabel(PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_URL_DI_INVOCAZIONE);
			de.setValue(urlinv);
			de.setType(DataElementType.TEXT_EDIT);
			de.setName(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_URL_DI_INVOCAZIONE);
			de.setSize(alternativeSize);
			dati.addElement(de);
		}

		
		
		
		
		
		
		// *************** Soggetto Erogatore *********************
		
		boolean configurazioneStandardSoggettoErogatore = false;
		
		de = new DataElement();
		de.setLabel(PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_SOGGETTO_EROGATORE);
		de.setType(DataElementType.TITLE);
		dati.addElement(de);

		String[] tipoMode = {
				PorteDelegateCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_DELEGATE_MODE_USER_INPUT,
				PorteDelegateCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_DELEGATE_MODE_REGISTER_INPUT,
				PorteDelegateCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_DELEGATE_MODE_URL_BASED,
				PorteDelegateCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_DELEGATE_MODE_CONTENT_BASED,
				PorteDelegateCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_DELEGATE_MODE_INPUT_BASED
		};
		String[] tipoModeSimple = { 
				PorteDelegateCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_DELEGATE_MODE_USER_INPUT,
				PorteDelegateCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_DELEGATE_MODE_URL_BASED,
				PorteDelegateCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_DELEGATE_MODE_CONTENT_BASED,
				PorteDelegateCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_DELEGATE_MODE_INPUT_BASED
		};
		de = new DataElement();
		de.setLabel(PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_MODALITA_IDENTIFICAZIONE);
		de.setName(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_MODE_SP);
		if(TipoOperazione.CHANGE.equals(tipoOp)){
			if (InterfaceType.STANDARD.equals(ServletUtils.getUserFromSession(this.session).getInterfaceType())) {
				if(PorteDelegateCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_DELEGATE_MODE_REGISTER_INPUT.equals(modesp) ){
					de.setType(DataElementType.HIDDEN);
					de.setValue(modesp);
					configurazioneStandardSoggettoErogatore = true;
				}
				else{
					de.setLabel(CostantiControlStation.LABEL_CONFIGURAZIONE_IMPOSTATA_MODALITA_AVANZATA_SHORT_MESSAGE);
					de.setType(DataElementType.TEXT);
					de.setValue(" ");
					configurazioneStandardNonApplicabile = true;
				}
			}
			else{
				de.setType(DataElementType.SELECT);
				de.setValues(tipoMode);
				de.setSelected(modesp);
			}
		}
		else{
			de.setType(DataElementType.SELECT);
			de.setValues(tipoMode);
			de.setSelected(modesp);
		}
		//		de.setOnChange("CambiaModeSP('" + tipoOp + "', " + numCorrApp + ")");
		de.setPostBack(true);
		dati.addElement(de);

		if(!configurazioneStandardNonApplicabile){
			if (modesp.equals(PorteDelegateCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_DELEGATE_MODE_REGISTER_INPUT)) {
				de = new DataElement();
				de.setLabel(PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_NOME);
				de.setName(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_SOGGETTO_ID);
				de.setPostBack(true);
				if(configurazioneStandardSoggettoErogatore){
					de.setType(DataElementType.HIDDEN);
					de.setValue(soggid);
					dati.addElement(de);
					
					de = new DataElement();
					de.setLabel(soggid);
					de.setType(DataElementType.NOTE);
				}else{
					de.setType(DataElementType.SELECT);
					de.setValues(soggettiList);
					de.setLabels(soggettiListLabel);
					de.setSelected(soggid);
				}
				//			de.setOnChange("CambiaSoggPD('" + tipoOp + "', " + numCorrApp + ")");
				dati.addElement(de);
			} else {
	
				if (InterfaceType.STANDARD.equals(ServletUtils.getUserFromSession(this.session).getInterfaceType())) {
					de = new DataElement();
					de.setLabel(PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_TIPO);
					de.setValue(this.soggettiCore.getTipoSoggettoDefault());
					de.setType(DataElementType.HIDDEN);
					de.setName(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_TIPO_SP);
					dati.addElement(de);
				} else {
					de = new DataElement();
					de.setLabel(PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_TIPO);
					de.setValue(tiposp);
					de.setType(DataElementType.TEXT_EDIT);
					de.setName(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_TIPO_SP);
					de.setSize(alternativeSize);
					de.setRequired(true);
					dati.addElement(de);
				}
	
				de = new DataElement();
				if (modesp.equals(IdentificazioneView.URL_BASED.toString()) || modesp.equals(IdentificazioneView.CONTENT_BASED.toString())) {
					de.setLabel(PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_PATTERN);
					de.setValue(patternErogatore);
				} else {
					de.setLabel(PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_NOME);
					de.setValue(sp);
				}
				if (!modesp.equals(IdentificazioneView.INPUT_BASED.toString())){
					de.setType(DataElementType.TEXT_EDIT);
					de.setRequired(true);
				}else
					de.setType(DataElementType.HIDDEN);
				de.setName(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_SP);
				de.setSize(alternativeSize);
				dati.addElement(de);
			}
		}

		
		
		
		
		
		
		
		
		// *************** Servizio *********************
		
		boolean configurazioneStandardServizio = false;
		
		de = new DataElement();
		//if(this.core.isTerminologiaSICA_RegistroServizi()){
		//	de.setLabel("Accordo Servizio Parte Specifica");
		//}else{
		de.setLabel(PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_SERVIZIO);
		//}
		de.setType(DataElementType.TITLE);
		dati.addElement(de);

		de = new DataElement();
		de.setLabel(PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_MODALITA_IDENTIFICAZIONE);
		de.setName(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_MODE_SERVIZIO);
		if(TipoOperazione.CHANGE.equals(tipoOp)){
			if (InterfaceType.STANDARD.equals(ServletUtils.getUserFromSession(this.session).getInterfaceType())) {
				if(!configurazioneStandardNonApplicabile && PorteDelegateCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_DELEGATE_MODE_REGISTER_INPUT.equals(modeservizio) ){
					de.setType(DataElementType.HIDDEN);
					de.setValue(modeservizio);
					configurazioneStandardServizio = true;
				}
				else{
					de.setLabel(CostantiControlStation.LABEL_CONFIGURAZIONE_IMPOSTATA_MODALITA_AVANZATA_SHORT_MESSAGE);
					de.setType(DataElementType.TEXT);
					de.setValue(" ");
					configurazioneStandardNonApplicabile = true;
				}
			}
			else{
				de.setType(DataElementType.SELECT);
				if (modesp.equals(PorteDelegateCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_DELEGATE_MODE_REGISTER_INPUT)) {
					de.setValues(tipoMode);
				} else {
					de.setValues(tipoModeSimple);
				}
				de.setSelected(modeservizio);
			}
		}else{
			de.setType(DataElementType.SELECT);
			if (modesp.equals(PorteDelegateCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_DELEGATE_MODE_REGISTER_INPUT)) {
				de.setValues(tipoMode);
			} else {
				de.setValues(tipoModeSimple);
			}
			de.setSelected(modeservizio);
		}
		//if (modesp.equals("register-input")) {
		//			de.setOnChange("CambiaModeServizio('" + tipoOp + "', " + numCorrApp + ")");
		de.setPostBack(true);
		//}
		dati.addElement(de);

		if(!configurazioneStandardNonApplicabile){
			if (modeservizio.equals(PorteDelegateCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_DELEGATE_MODE_REGISTER_INPUT)) {
				de = new DataElement();
				de.setLabel(PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_NOME);
				de.setName(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_SERVIZIO_ID);
				de.setPostBack(true);
				if(configurazioneStandardServizio){
					de.setType(DataElementType.HIDDEN);
					de.setValue(servid);
					dati.addElement(de);
					
					de = new DataElement();
					de.setLabel(servid);
					de.setType(DataElementType.NOTE);
				}else{
					de.setType(DataElementType.SELECT);
					de.setValues(serviziList);
					de.setLabels(serviziListLabel);
					de.setSelected(servid);
				}
				//			de.setOnChange("CambiaServPD('" + tipoOp + "', " + numCorrApp + ")");
				dati.addElement(de);
			} else {
	
				if (InterfaceType.STANDARD.equals(ServletUtils.getUserFromSession(this.session).getInterfaceType())) {
					de = new DataElement();
					de.setLabel(PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_TIPO);
					de.setValue(this.apsCore.getTipoServizioDefault());
					de.setType(DataElementType.HIDDEN);
					de.setName(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_TIPO_SERVIZIO);
					dati.addElement(de);
				} else {
					de = new DataElement();
					de.setLabel(PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_TIPO_SERVIZIO);
					de.setValue(tiposervizio);
					de.setType(DataElementType.TEXT_EDIT);
					de.setName(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_TIPO_SERVIZIO);
					de.setSize(alternativeSize);
					de.setRequired(true);
					dati.addElement(de);
				}
	
				de = new DataElement();
				if (modeservizio.equals(IdentificazioneView.URL_BASED.toString()) || modeservizio.equals(IdentificazioneView.CONTENT_BASED.toString())) {
					de.setLabel(PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_PATTERN);
					de.setValue(patternServizio);
				} else {
					de.setLabel(PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_NOME);
					de.setValue(servizio);
				}
	
				if (!modeservizio.equals(IdentificazioneView.INPUT_BASED.toString())){
					de.setType(DataElementType.TEXT_EDIT);
					de.setRequired(true);
				}else
					de.setType(DataElementType.HIDDEN);
				de.setName(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_SERVIZIO);
				de.setSize(alternativeSize);
				dati.addElement(de);
			}
		}
		
		
		
		
		
		
		
		
		// *************** Azione *********************
		
		boolean configurazioneStandardAzione = false;
		
		// se servizio register-input e azioneList==null e
		// mode_azione=register-input allora nn c'e' azione
		String[] tipoModeAzione = {
				PorteDelegateCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_DELEGATE_MODE_USER_INPUT,
				PorteDelegateCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_DELEGATE_MODE_REGISTER_INPUT,
				PorteDelegateCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_DELEGATE_MODE_URL_BASED,
				PorteDelegateCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_DELEGATE_MODE_CONTENT_BASED,
				PorteDelegateCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_DELEGATE_MODE_INPUT_BASED,
				PorteDelegateCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_DELEGATE_MODE_SOAP_ACTION_BASED,
				PorteDelegateCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_DELEGATE_MODE_WSDL_BASED
		};
		String[] tipoModeSimpleAzione = {
				PorteDelegateCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_DELEGATE_MODE_USER_INPUT,
				PorteDelegateCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_DELEGATE_MODE_URL_BASED,
				PorteDelegateCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_DELEGATE_MODE_CONTENT_BASED,
				PorteDelegateCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_DELEGATE_MODE_INPUT_BASED,
				PorteDelegateCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_DELEGATE_MODE_SOAP_ACTION_BASED,
				PorteDelegateCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_DELEGATE_MODE_WSDL_BASED
		};
		if(TipoOperazione.CHANGE.equals(tipoOp) && InterfaceType.STANDARD.equals(ServletUtils.getUserFromSession(this.session).getInterfaceType()) ){
			if ( !configurazioneStandardNonApplicabile && 
					(modeaz != null) && 
					(
					 modeaz.equals(PorteDelegateCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_DELEGATE_MODE_URL_BASED) ||
					 modeaz.equals(PorteDelegateCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_DELEGATE_MODE_USER_INPUT) ||
					 modeaz.equals(PorteDelegateCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_DELEGATE_MODE_REGISTER_INPUT)
					)
				) {
				if( modeaz.equals(PorteDelegateCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_DELEGATE_MODE_URL_BASED) ){
					if(this.porteDelegateCore.isForceWsdlBasedAzione_generazioneAutomaticaPorteDelegate()){
						if( ServletUtils.isCheckBoxEnabled(forceWsdlBased) || CostantiRegistroServizi.ABILITATO.equals(forceWsdlBased) ){
							configurazioneStandardAzione = true;
						}
						else{
							configurazioneStandardNonApplicabile = true;
						}
					}
					else{
						if( ServletUtils.isCheckBoxEnabled(forceWsdlBased) == false ){
							configurazioneStandardAzione = true;
						}
						else{
							configurazioneStandardNonApplicabile = true;
						}
					}
				}
				else{
					configurazioneStandardAzione = true;
				}
			}
			else{
				configurazioneStandardNonApplicabile = true;
			}
		}
		if(configurazioneStandardNonApplicabile){
			
			de = new DataElement();
			de.setLabel(PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_AZIONE);
			de.setType(DataElementType.TITLE);
			dati.addElement(de);
			
			de = new DataElement();
			de.setLabel(CostantiControlStation.LABEL_CONFIGURAZIONE_IMPOSTATA_MODALITA_AVANZATA_SHORT_MESSAGE);
			de.setType(DataElementType.TEXT);
			de.setValue(" ");
			dati.addElement(de);
			
		}
		else{

			if(configurazioneStandardAzione){
				de = new DataElement();
				de.setLabel(PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_MODALITA_IDENTIFICAZIONE);
				de.setType(DataElementType.HIDDEN);
				de.setName(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_MODE_AZIONE);
				de.setValue(modeaz);
				dati.addElement(de);
				
				if( modeaz.equals(PorteDelegateCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_DELEGATE_MODE_URL_BASED) ){
					de = new DataElement();
					de.setLabel(PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_PATTERN);
					de.setValue(patternAzione);
					de.setType(DataElementType.HIDDEN);
					de.setName(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_AZIONE);
					de.setSize(alternativeSize);
					dati.addElement(de);
				}
				else{
					if ((modeaz != null) && modeaz.equals(PorteDelegateCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_DELEGATE_MODE_REGISTER_INPUT)) {
						de = new DataElement();
						de.setLabel(PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_NOME);
						de.setType(DataElementType.HIDDEN);
						de.setName(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_AZIONE_ID);
						de.setValue(azid);
						dati.addElement(de);
					}
					else{
						de = new DataElement();
						de.setLabel(PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_NOME);
						de.setValue(azione);
						de.setType(DataElementType.HIDDEN);
						de.setName(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_AZIONE);
						de.setSize(alternativeSize);
						dati.addElement(de);
					}
				}
				
				de = new DataElement();
				de.setLabel(PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_FORCE_WSDL_BASED);
				de.setType(DataElementType.HIDDEN);
				de.setValue(forceWsdlBased);
				de.setName(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_FORCE_WSDL_BASED);
				dati.addElement(de);
				
				de = new DataElement();
				de.setLabel(PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_AZIONE);
				de.setType(DataElementType.TITLE);
				dati.addElement(de);
				
				de = new DataElement();
				de.setType(DataElementType.NOTE);
				if ((modeaz != null) && (modeaz.equals(IdentificazioneView.URL_BASED.toString()))){
					de.setLabel(PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_QUALSIASI_AZIONE);
				}
				else{
					// static o registry
					if(azione==null || "".equals(azione) || "-".equals(azione)){
						de.setLabel(PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_QUALSIASI_AZIONE);
					}else{
						de.setLabel(azione);
					}
				}
				dati.addElement(de);
				
			}
			else{
				if (!(modeservizio.equals(IdentificazioneView.REGISTER_INPUT.toString()) && ((modeaz != null) && modeaz.equals(IdentificazioneView.REGISTER_INPUT.toString()) && ((azioniList == null) || (azioniList.length == 0))))) {
					de = new DataElement();
					de.setLabel(PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_AZIONE);
					de.setType(DataElementType.TITLE);
					dati.addElement(de);
		
					de = new DataElement();
					de.setLabel(PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_MODALITA_IDENTIFICAZIONE);
					de.setType(DataElementType.SELECT);
					de.setName(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_MODE_AZIONE);
					if (modeservizio.equals(PorteDelegateCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_DELEGATE_MODE_REGISTER_INPUT)) {
						de.setValues(tipoModeAzione);
					} else {
						de.setValues(tipoModeSimpleAzione);
					}
					de.setSelected(modeaz);
					//if (modeservizio.equals("register-input")) {
					//				de.setOnChange("CambiaModeAzione('" + tipoOp + "', " + numCorrApp + ")");
					de.setPostBack(true);
					//}
					dati.addElement(de);
		
					if ((modeaz != null) && modeaz.equals(PorteDelegateCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_DELEGATE_MODE_REGISTER_INPUT)) {
						de = new DataElement();
						de.setLabel(PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_NOME);
						de.setType(DataElementType.SELECT);
						de.setName(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_AZIONE_ID);
						de.setValues(azioniList);
						de.setLabels(azioniListLabel);
						de.setSelected(azid);
						dati.addElement(de);
					} else {
		
						de = new DataElement();
						if ((modeaz != null) && (modeaz.equals(IdentificazioneView.URL_BASED.toString()) || modeaz.equals(IdentificazioneView.CONTENT_BASED.toString()))) {
							de.setLabel(PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_PATTERN);
							de.setValue(patternAzione);
							de.setRequired(true);
						} else {
							de.setLabel(PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_NOME);
							de.setValue(azione);
						}
		
						if (!IdentificazioneView.INPUT_BASED.toString().equals(modeaz) && 
								!IdentificazioneView.SOAP_ACTION_BASED.toString().equals(modeaz) && 
								!IdentificazioneView.WSDL_BASED.toString().equals(modeaz) ){
							de.setType(DataElementType.TEXT_EDIT);
						}else
							de.setType(DataElementType.HIDDEN);
						de.setName(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_AZIONE);
						de.setSize(alternativeSize);
						dati.addElement(de);
					}
		
					// se non e' selezionata la modalita userInput / wsdlbased / registerInput faccio vedere il check box forceWsdlbased
					de = new DataElement();
					de.setLabel(PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_FORCE_WSDL_BASED);
					if( InterfaceType.AVANZATA.equals(ServletUtils.getUserFromSession(this.session).getInterfaceType()) &&
							modeaz!= null && (
								!modeaz.equals(PorteDelegateCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_DELEGATE_MODE_USER_INPUT) &&
								!modeaz.equals(PorteDelegateCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_DELEGATE_MODE_REGISTER_INPUT) &&
								!modeaz.equals(PorteDelegateCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_DELEGATE_MODE_WSDL_BASED))
						){
		
						de.setType(DataElementType.CHECKBOX);
						if( ServletUtils.isCheckBoxEnabled(forceWsdlBased) || CostantiRegistroServizi.ABILITATO.equals(forceWsdlBased) ){
							de.setSelected(true);
						}
					}
					else{
						de.setType(DataElementType.HIDDEN);
						de.setValue(forceWsdlBased);
					}
					de.setName(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_FORCE_WSDL_BASED);
					dati.addElement(de);
		
				}
			}
		}
		
		
		
		
		
		
		
		
		// *************** Controllo degli Accessi *********************
		de = new DataElement();
		de.setType(DataElementType.TITLE);
		de.setLabel(PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_CONTROLLO_ACCESSI);
		dati.addElement(de);
		
		int totEl = 3;
		if (confPers)
			totEl++;
		String[] tipoAutenticazione = new String[totEl];
		String[] labelTipoAutenticazione = new String[totEl];
		tipoAutenticazione[0] = PorteDelegateCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_DELEGATE_AUTENTICAZIONE_SSL;
		labelTipoAutenticazione[0] = PorteDelegateCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_DELEGATE_AUTENTICAZIONE_SSL;
		tipoAutenticazione[1] = PorteDelegateCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_DELEGATE_AUTENTICAZIONE_BASIC;
		labelTipoAutenticazione[1] = PorteDelegateCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_DELEGATE_AUTENTICAZIONE_BASIC;
		tipoAutenticazione[2] = PorteDelegateCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_DELEGATE_AUTENTICAZIONE_NONE;
		labelTipoAutenticazione[2] = CostantiConfigurazione.DISABILITATO.getValue();
		if (confPers ){
			tipoAutenticazione[3] = PorteDelegateCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_DELEGATE_AUTENTICAZIONE_CUSTOM;
			labelTipoAutenticazione[3] = PorteDelegateCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_DELEGATE_AUTENTICAZIONE_CUSTOM;
		}
		de = new DataElement();
		de.setLabel(PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_AUTENTICAZIONE);
		de.setType(DataElementType.SELECT);
		de.setName(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_AUTENTICAZIONE);
		de.setValues(tipoAutenticazione);
		de.setLabels(labelTipoAutenticazione);
		//		de.setOnChange("CambiaTipoAuth('" + tipoOp + "', " + numCorrApp + ")");
		de.setPostBack(true);
		de.setSelected(autenticazione);
		dati.addElement(de);

		de = new DataElement();
		de.setLabel("");
		if (autenticazione == null ||
				!autenticazione.equals(PorteDelegateCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_DELEGATE_AUTENTICAZIONE_CUSTOM))
			de.setType(DataElementType.HIDDEN);
		else
			de.setType(DataElementType.TEXT_EDIT);
		de.setName(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_NOME_AUTENTICAZIONE);
		de.setValue(nomeauth);
		dati.addElement(de);

		totEl = 2;
		if (confPers )
			totEl++;
		String[] tipoAutorizzazione = new String[totEl];
		String[] labelTipoAutorizzazione = new String[totEl];
		tipoAutorizzazione[0] = PorteDelegateCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_DELEGATE_AUTORIZZAZIONE_OPENSPCOOP;
		labelTipoAutorizzazione[0] = CostantiConfigurazione.ABILITATO.getValue();
		tipoAutorizzazione[1] = PorteDelegateCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_DELEGATE_AUTORIZZAZIONE_NONE;
		labelTipoAutorizzazione[1] = CostantiConfigurazione.DISABILITATO.getValue();
		if (confPers ){
			tipoAutorizzazione[2] = PorteDelegateCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_DELEGATE_AUTORIZZAZIONE_CUSTOM;
			labelTipoAutorizzazione[2] = PorteDelegateCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_DELEGATE_AUTORIZZAZIONE_CUSTOM;
		}
		de = new DataElement();
		de.setLabel(PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_AUTORIZZAZIONE);
		de.setType(DataElementType.SELECT);
		de.setName(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_AUTORIZZAZIONE);
		de.setValues(tipoAutorizzazione);
		de.setLabels(labelTipoAutorizzazione);
		//		de.setOnChange("CambiaTipoAutor('" + tipoOp + "', " + numCorrApp + ")");
		de.setPostBack(true);
		de.setSelected(autorizzazione);
		dati.addElement(de);

		de = new DataElement();
		de.setLabel("");
		if (autorizzazione == null ||
				!autorizzazione.equals(PorteDelegateCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_DELEGATE_AUTORIZZAZIONE_CUSTOM))
			de.setType(DataElementType.HIDDEN);
		else
			de.setType(DataElementType.TEXT_EDIT);
		de.setName(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_NOME_AUTORIZZAZIONE);
		de.setValue(nomeautor);
		dati.addElement(de);

		// Autorizzazione contenuto
		de = new DataElement();
		de.setLabel(PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_AUTORIZZAZIONE_CONTENUTI);
		if (InterfaceType.STANDARD.equals(ServletUtils.getUserFromSession(this.session).getInterfaceType())) 
			de.setType(DataElementType.HIDDEN);
		else
			de.setType(DataElementType.TEXT_EDIT);

		de.setName(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_AUTORIZZAZIONE_CONTENUTI);
		de.setValue(autorizzazioneContenuti);
		de.setSize(alternativeSize);
		dati.addElement(de);
		
		if (!idsogg.equals(PorteDelegateCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_DELEGATE_NEW_ID) 
				&& !idPorta.equals(PorteDelegateCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_DELEGATE_NEW_ID)) {

			de = new DataElement();
			de.setType(DataElementType.LINK);
			de.setUrl(PorteDelegateCostanti.SERVLET_NAME_PORTE_DELEGATE_SERVIZIO_APPLICATIVO_LIST +"?" + 
					PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID_SOGGETTO + "=" + idsogg + "&"
					+ PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID + "=" + idPorta);
			if (contaListe) {
				ServletUtils.setDataElementCustomLabel(de,PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_SERVIZI_APPLICATIVI,new Long(numSA));
			} else
				ServletUtils.setDataElementCustomLabel(de,PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_SERVIZI_APPLICATIVI);
			dati.addElement(de);
			
		}
		
		
		

		
		
		
		
		// *************** Integrazione *********************
		
		Vector<DataElement> deIntegrazione = new Vector<DataElement>();
		
		if (tipoOp == TipoOperazione.CHANGE) {

			de = new DataElement();
			de.setLabel(PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_METADATI);
			de.setValue(integrazione);
			de.setName(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_INTEGRAZIONE);
			de.setSize(alternativeSize);
			if(InterfaceType.STANDARD.equals(ServletUtils.getUserFromSession(this.session).getInterfaceType())){
				de.setType(DataElementType.HIDDEN);
				dati.addElement(de);
			}else{
				de.setType(DataElementType.TEXT_EDIT);
				deIntegrazione.addElement(de);
			}
			
		}

		String[] tipoStateless = { PorteDelegateCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_DELEGATE_STATELESS_DEFAULT,
				PorteDelegateCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_DELEGATE_STATELESS_ABILITATO, 
				PorteDelegateCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_DELEGATE_STATELESS_DISABILITATO };
		de = new DataElement();
		de.setLabel(PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_STATELESS);
		de.setName(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_STATELESS);
		if(this.core.isShowJ2eeOptions()){
			de.setType(DataElementType.SELECT);
			de.setValues(tipoStateless);
			de.setSelected(stateless);
			deIntegrazione.addElement(de);
		}else{
			de.setType(DataElementType.HIDDEN);
			de.setValue(stateless);
			dati.addElement(de);
		}
		

		// LocalForward
		boolean localForwardShow = true;
		Soggetto soggettoErogatoreLocalForward  = null;
		if (modesp.equals(IdentificazioneView.REGISTER_INPUT.toString()) ) {
			try{
				if(soggid!=null && soggid.contains("/")){
					soggettoErogatoreLocalForward = this.soggettiCore.getSoggettoRegistro(new IDSoggetto(soggid.split("/")[0], soggid.split("/")[1]));
				}
				else if(soggid!=null && !"".equals(soggid)){
					soggettoErogatoreLocalForward = this.soggettiCore.getSoggettoRegistro(Long.parseLong(soggid));
				}
			}catch(DriverRegistroServiziNotFound dNot){}
		}
		else if (modesp.equals(IdentificazioneView.USER_INPUT.toString()) ) {
			try{
				String tipoSoggetto = null;
				if (InterfaceType.STANDARD.equals(ServletUtils.getUserFromSession(this.session).getInterfaceType())) {
					tipoSoggetto = this.soggettiCore.getTipoSoggettoDefault();
				} else {
					tipoSoggetto = tiposp;
				}
				if(tipoSoggetto!=null && !"".equals(tipoSoggetto) && 
						sp!=null && !"".equals(sp)){
					soggettoErogatoreLocalForward = this.soggettiCore.getSoggettoRegistro(new IDSoggetto(tipoSoggetto, sp));
				}
			}catch(DriverRegistroServiziNotFound dNot){}
		}
		if(soggettoErogatoreLocalForward!=null){
			if(this.pddCore.isRegistroServiziLocale()){
				if(soggettoErogatoreLocalForward.getPortaDominio()!=null){
					try{
						if(this.pddCore.getPdDControlStation(soggettoErogatoreLocalForward.getPortaDominio()).getTipo().equals(PorteDelegateCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_DELEGATE_ESTERNO)){
							localForwardShow = false;
						}
					}catch(DriverControlStationNotFound dNot){}
				}
			}
			else{
				// se il soggetto erogatore non e' locale non puo' esistere il localForward.
				// Comunque sia lo devo far vedere lo stesso poiche' magari e' una configurazione errata indicata nella console centrale.
//				if(soggettoErogatoreLocalForward.getTipo()!=null && soggettoErogatoreLocalForward.getNome()!=null){
//					try{
//						IDSoggetto idSoggetto = new IDSoggetto(soggettoErogatoreLocalForward.getTipo(), soggettoErogatoreLocalForward.getNome());
//						if(!this.soggettiCore.existsSoggetto(idSoggetto)){
//							localForwardShow = false; 
//						}
//					}catch(Exception dNot){
//						this.log.error(dNot.getMessage(), dNot);
//					}
//				}
			}
		}			
		String[] tipoLocalForward = { PorteDelegateCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_DELEGATE_LOCAL_FORWARD_ABILITATO,
				PorteDelegateCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_DELEGATE_LOCAL_FORWARD_DISABILITATO };
		de = new DataElement();
		de.setLabel(PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_LOCAL_FORWARD);
		de.setName(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_LOCAL_FORWARD);
		de.setSize(alternativeSize);
		if (InterfaceType.STANDARD.equals(ServletUtils.getUserFromSession(this.session).getInterfaceType()) || localForwardShow==false) {
			de.setType(DataElementType.HIDDEN);
			de.setValue(localForward);
			dati.addElement(de);
		}else{
			de.setType(DataElementType.SELECT);
			de.setValues(tipoLocalForward);
			de.setSelected(localForward);
			deIntegrazione.addElement(de);
		}
		

		if(deIntegrazione.size()>0){
			
			de = new DataElement();
			de.setType(DataElementType.TITLE);
			de.setLabel(PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_INTEGRAZIONE);
			dati.addElement(de);
			
			for (int i = 0; i < deIntegrazione.size(); i++) {
				dati.addElement(deIntegrazione.get(i));
			}
		}
		
		
		
		
		
		
		// *************** CorrelazioneApplicativa *********************
		
		if (!idsogg.equals(PorteDelegateCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_DELEGATE_NEW_ID) 
				&& !idPorta.equals(PorteDelegateCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_DELEGATE_NEW_ID)) {

			de = new DataElement();
			de.setType(DataElementType.TITLE);
			de.setLabel(PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_CORRELAZIONE_APPLICATIVA);
			dati.addElement(de);
			
			if (tipoOp == TipoOperazione.CHANGE) {

				if (riusoId && numCorrApp != 0) {
					de = new DataElement();
					de.setLabel(PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_SCADENZA_CORRELAZIONE_APPLICATIVA);
					de.setValue(scadcorr);
					de.setType(DataElementType.TEXT_EDIT);
					de.setName(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_SCADENZA_CORRELAZIONE_APPLICATIVA);
					de.setSize(alternativeSize);
					dati.addElement(de);
				}
			}
			
			de = new DataElement();
			de.setType(DataElementType.LINK);
			de.setUrl(
					PorteDelegateCostanti.SERVLET_NAME_PORTE_DELEGATE_CORRELAZIONE_APPLICATIVA_REQUEST_LIST,
					new Parameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID_SOGGETTO, idsogg),
					new Parameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID, ""+idPorta),
					new Parameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_NOME,nomePorta));

			if (contaListe) {
				ServletUtils.setDataElementCustomLabel(de,PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_CORRELAZIONE_APPLICATIVA_RICHIESTA,new Long(numCorrelazioneReq));
			} else
				ServletUtils.setDataElementCustomLabel(de,PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_CORRELAZIONE_APPLICATIVA_RICHIESTA);

			dati.addElement(de);

			de = new DataElement();
			de.setType(DataElementType.LINK);
			de.setUrl(
					PorteDelegateCostanti.SERVLET_NAME_PORTE_DELEGATE_CORRELAZIONE_APPLICATIVA_RESPONSE_LIST,
					new Parameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID_SOGGETTO, idsogg),
					new Parameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID, ""+idPorta),
					new Parameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_NOME,nomePorta));

			if (contaListe) {
				ServletUtils.setDataElementCustomLabel(de,PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_CORRELAZIONE_APPLICATIVA_RISPOSTA,new Long(numCorrelazioneRes));
			} else
				ServletUtils.setDataElementCustomLabel(de,PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_CORRELAZIONE_APPLICATIVA_RISPOSTA);

			dati.addElement(de);
		}
		
		
		
		
		
		
		
		
		
		// *************** Gestione Messaggio *********************
		
		if (!idsogg.equals(PorteDelegateCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_DELEGATE_NEW_ID) 
				&& !idPorta.equals(PorteDelegateCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_DELEGATE_NEW_ID)) {

			de = new DataElement();
			de.setType(DataElementType.TITLE);
			de.setLabel(PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_GESTIONE_MESSAGGIO);
			dati.addElement(de);
			
			de = new DataElement();
			de.setType(DataElementType.LINK);
			de.setUrl(PorteDelegateCostanti.SERVLET_NAME_PORTE_DELEGATE_MESSAGE_SECURITY +"?" + 
					PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID_SOGGETTO + "=" + idsogg + "&"
					+ PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID + "=" + idPorta);
			ServletUtils.setDataElementCustomLabel(de, PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_MESSAGE_SECURITY, statoMessageSecurity);
			dati.addElement(de);

			//if(InterfaceType.AVANZATA.equals(ServletUtils.getUserFromSession(this.session).getInterfaceType())){
			de = new DataElement();
			de.setType(DataElementType.LINK);
			de.setUrl(PorteDelegateCostanti.SERVLET_NAME_PORTE_DELEGATE_MTOM +"?" + 
					PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID_SOGGETTO + "=" + idsogg + "&"
					+ PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID + "=" + idPorta);
			ServletUtils.setDataElementCustomLabel(de, PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_MTOM, statoMTOM);
			dati.addElement(de);
			//}
			
		}
		
		
		
		
		
		
		
		// *************** Validazione Contenuti *********************

		de = new DataElement();
		de.setType(DataElementType.TITLE);
		de.setLabel(PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_VALIDAZIONE_CONTENUTI);
		dati.addElement(de);

		String[] tipoXsd = { PorteDelegateCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_DELEGATE_XSD_ABILITATO,
				PorteDelegateCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_DELEGATE_XSD_DISABILITATO,
				PorteDelegateCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_DELEGATE_XSD_WARNING_ONLY };
		de = new DataElement();
		de.setLabel(PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_STATO);
		de.setType(DataElementType.SELECT);
		de.setName(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_XSD);
		de.setValues(tipoXsd);
		//		de.setOnChange("CambiaModePD('" + tipoOp + "', " + numCorrApp + ")");
		de.setPostBack(true);
		de.setSelected(xsd);
		dati.addElement(de);

		if (PorteDelegateCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_DELEGATE_XSD_ABILITATO.equals(xsd) || 
				PorteDelegateCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_DELEGATE_XSD_WARNING_ONLY.equals(xsd)) {
			String[] tipi_validazione = { PorteDelegateCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_DELEGATE_TIPO_VALIDAZIONE_XSD,
					PorteDelegateCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_DELEGATE_TIPO_VALIDAZIONE_WSDL,
					PorteDelegateCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_DELEGATE_TIPO_VALIDAZIONE_OPENSPCOOP };
			//String[] tipi_validazione = { "xsd", "wsdl" };
			de = new DataElement();
			de.setLabel(PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_TIPO);
			de.setType(DataElementType.SELECT);
			de.setName(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_TIPO_VALIDAZIONE);
			de.setValues(tipi_validazione);
			de.setSelected(tipoValidazione);
			dati.addElement(de);


			// Applica MTOM 
			de = new DataElement();
			de.setLabel(PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_ACCETTA_MTOM);

			
			//if(InterfaceType.AVANZATA.equals(ServletUtils.getUserFromSession(this.session).getInterfaceType())){
			de.setType(DataElementType.CHECKBOX);
			if( ServletUtils.isCheckBoxEnabled(applicaMTOM) || CostantiRegistroServizi.ABILITATO.equals(applicaMTOM) ){
				de.setSelected(true);
			}
//			}
//			else{
//				de.setType(DataElementType.HIDDEN);
//				de.setValue(applicaMTOM);
//			}
		 
			de.setName(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_APPLICA_MTOM);
			dati.addElement(de);

		}




		
		// *************** Asincroni *********************
		
		if (InterfaceType.STANDARD.equals(ServletUtils.getUserFromSession(this.session).getInterfaceType())) {

			de = new DataElement();
			de.setType(DataElementType.HIDDEN);
			de.setLabel(PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_GESTIONE_ASINCRONA);
			dati.addElement(de);

			de = new DataElement();
			de.setLabel(PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_RICEVUTA_ASINCRONA_SIMMETRICA);
			de.setType(DataElementType.HIDDEN);
			de.setName(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_RICEVUTA_ASINCRONA_SIMMETRICA);
			de.setValue(PorteDelegateCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_DELEGATE_RICEVUTA_ASINCRONA_SIMMETRICA_ABILITATO);
			dati.addElement(de);

			de = new DataElement();
			de.setLabel(PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_RICEVUTA_ASINCRONA_ASIMMETRICA);
			de.setType(DataElementType.HIDDEN);
			de.setName(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_RICEVUTA_ASINCRONA_ASIMMETRICA);
			de.setValue(PorteDelegateCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_DELEGATE_RICEVUTA_ASINCRONA_ASIMMETRICA_ABILITATO);
			dati.addElement(de);
		} else {

			de = new DataElement();
			de.setType(DataElementType.TITLE);
			de.setLabel(PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_GESTIONE_ASINCRONA);
			dati.addElement(de);

			String[] tipoRicsim = {PorteDelegateCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_DELEGATE_RICEVUTA_ASINCRONA_SIMMETRICA_ABILITATO
					, PorteDelegateCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_DELEGATE_RICEVUTA_ASINCRONA_SIMMETRICA_DISABILITATO};
			de = new DataElement();
			de.setLabel(PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_RICEVUTA_ASINCRONA_SIMMETRICA);
			de.setType(DataElementType.SELECT);
			de.setName(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_RICEVUTA_ASINCRONA_SIMMETRICA);
			de.setValues(tipoRicsim);
			de.setSelected(ricsim);
			dati.addElement(de);

			String[] tipoRicasim = { PorteDelegateCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_DELEGATE_RICEVUTA_ASINCRONA_ASIMMETRICA_ABILITATO
					, PorteDelegateCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_DELEGATE_RICEVUTA_ASINCRONA_ASIMMETRICA_DISABILITATO};
			de = new DataElement();
			de.setLabel(PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_RICEVUTA_ASINCRONA_ASIMMETRICA);
			de.setType(DataElementType.SELECT);
			de.setName(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_RICEVUTA_ASINCRONA_ASIMMETRICA);
			de.setValues(tipoRicasim);
			de.setSelected(ricasim);
			dati.addElement(de);
		}

		
		
		
		
		
		// ***************  SOAP With Attachments *********************

		if (InterfaceType.AVANZATA.equals(ServletUtils.getUserFromSession(this.session).getInterfaceType())) {
		
			de = new DataElement();
			de.setType(DataElementType.TITLE);
			de.setLabel(PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_SOAP_WITH_ATTACHMENTS);
			dati.addElement(de);
	
			String[] tipoGestBody = {PorteDelegateCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_DELEGATE_GEST_BODY_NONE ,
					PorteDelegateCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_DELEGATE_GEST_BODY_ALLEGA, 
					PorteDelegateCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_DELEGATE_GEST_BODY_SCARTA };
			de = new DataElement();
			de.setLabel(PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_GESTIONE_BODY);
			de.setType(DataElementType.SELECT);
			de.setName(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_GESTIONE_BODY);
			de.setValues(tipoGestBody);
			de.setSelected(gestBody);
			dati.addElement(de);
	
			String[] tipoGestManifest = {
					PorteDelegateCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_DELEGATE_GEST_MANIFEST_DEFAULT,
					PorteDelegateCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_DELEGATE_GEST_MANIFEST_ABILITATO,
					PorteDelegateCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_DELEGATE_GEST_MANIFEST_DISABILITATO };
			de = new DataElement();
			de.setLabel(PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_GESTIONE_MANIFEST);
			if(this.core.isFunzionalitaProtocolloSupportataDalProtocollo(protocollo, FunzionalitaProtocollo.MANIFEST_ATTACHMENTS)){
				de.setType(DataElementType.SELECT);
				de.setValues(tipoGestManifest);
				de.setSelected(gestManifest);
			}else {
				de.setType(DataElementType.HIDDEN);
				de.setValue(PorteDelegateCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_DELEGATE_GEST_MANIFEST_DISABILITATO );
			}
			de.setName(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_GESTIONE_MANIFEST);
			dati.addElement(de);
		
		}
		
		

	
		if(configurazioneStandardNonApplicabile){
			this.pd.setMessage(CostantiControlStation.LABEL_CONFIGURAZIONE_IMPOSTATA_MODALITA_AVANZATA_LONG_MESSAGE);
			this.pd.disableEditMode();
		}
		
		
		
		return dati;
	}


	public Vector<DataElement> addPorteDelegateCorrelazioneApplicativaRequestToDati(TipoOperazione tipoOp,
			PageData pd,   String elemxml, String mode,
			String pattern, String gif, String riusoIdMessaggio, Vector<DataElement> dati, String idcorr) {

		DataElement de = new DataElement();
		de.setLabel(PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_ELEMENTO_XML_BR);
		de.setType(DataElementType.TEXT_EDIT);
		de.setName(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ELEMENTO_XML);
		de.setSize(80);
		if (elemxml == null) {
			de.setValue("");
		} else {
			de.setValue(elemxml);
		}
		dati.addElement(de);

		String[] tipoMode = { 
				PorteDelegateCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_DELEGATE_TIPO_MODE_CORRELAZIONE_URL_BASED, 
				PorteDelegateCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_DELEGATE_TIPO_MODE_CORRELAZIONE_CONTENT_BASED,
				PorteDelegateCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_DELEGATE_TIPO_MODE_CORRELAZIONE_INPUT_BASED ,
				PorteDelegateCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_DELEGATE_TIPO_MODE_CORRELAZIONE_DISABILITATO 
		};
		//String[] tipoMode = { "contentBased", "disabilitato" };
		de = new DataElement();
		de.setLabel(PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_MODALITA_IDENTIFICAZIONE);
		de.setType(DataElementType.SELECT);
		de.setName(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_MODE);
		de.setValues(tipoMode);
		de.setSelected(mode);
		//				de.setOnChange("CambiaModeCorrApp('add','')");
		de.setPostBack(true);
		dati.addElement(de);

		if (mode.equals(PorteDelegateCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_DELEGATE_TIPO_MODE_CORRELAZIONE_URL_BASED) ||
				mode.equals(PorteDelegateCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_DELEGATE_TIPO_MODE_CORRELAZIONE_CONTENT_BASED)) {
			de = new DataElement();
			de.setLabel(PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_PATTERN);
			if (pattern == null) {
				de.setValue("");
			} else {
				de.setValue(pattern);
			}
			de.setType(DataElementType.TEXT_EDIT);
			de.setName(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_PATTERN);
			de.setSize(80);
			de.setRequired(true);
			dati.addElement(de);
		}

		if(!PorteDelegateCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_DELEGATE_DISABILITATO.equals(mode)){
			String[] tipiGIF = { CostantiConfigurazione.BLOCCA.toString(), CostantiConfigurazione.ACCETTA.toString()};
			de = new DataElement();
			de.setLabel(PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_GESTIONE_IDENTIFICAZIONE_FALLITA);
			de.setType(DataElementType.SELECT);
			de.setName(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_GESTIONE_IDENTIFICAZIONE_FALLITA);
			de.setValues(tipiGIF);
			de.setSelected(gif);
			dati.addElement(de);

			String[] tipiRiusoIdMessaggio = { CostantiConfigurazione.DISABILITATO.toString(), CostantiConfigurazione.ABILITATO.toString()};
			de = new DataElement();
			de.setLabel(PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_RIUSO_ID_MESSAGGIO);
			de.setType(DataElementType.SELECT);
			de.setName(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_RIUSO_ID_MESSAGGIO);
			de.setValues(tipiRiusoIdMessaggio);
			de.setSelected(riusoIdMessaggio);
			dati.addElement(de);

		}

		if(idcorr != null){
			de = new DataElement();
			de.setValue(idcorr);
			de.setType(DataElementType.HIDDEN);
			de.setName(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID_CORRELAZIONE);
			dati.addElement(de);
		}

		return dati;

	}

	public Vector<DataElement> addPorteDelegateCorrelazioneApplicativaResponseToDati(TipoOperazione tipoOp,
			PageData pd, String elemxml, String mode,
			String pattern, String gif,
			//			String riuso,
			Vector<DataElement> dati, String idcorr) {

		DataElement de = new DataElement();
		de.setLabel(PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_ELEMENTO_XML_BR);
		de.setType(DataElementType.TEXT_EDIT);
		de.setName(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ELEMENTO_XML);
		de.setSize(80);
		if (elemxml == null) {
			de.setValue("");
		} else {
			de.setValue(elemxml);
		}
		dati.addElement(de);

		String[] tipoMode = { 
				//				PorteDelegateCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_DELEGATE_TIPO_MODE_CORRELAZIONE_URL_BASED, 
				PorteDelegateCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_DELEGATE_TIPO_MODE_CORRELAZIONE_CONTENT_BASED,
				PorteDelegateCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_DELEGATE_TIPO_MODE_CORRELAZIONE_INPUT_BASED ,
				PorteDelegateCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_DELEGATE_TIPO_MODE_CORRELAZIONE_DISABILITATO 
		};
		//String[] tipoMode = { "contentBased", "disabilitato" };
		de = new DataElement();
		de.setLabel(PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_MODALITA_IDENTIFICAZIONE);
		de.setType(DataElementType.SELECT);
		de.setName(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_MODE);
		de.setValues(tipoMode);
		de.setSelected(mode);
		//				de.setOnChange("CambiaModeCorrApp('add','')");
		de.setPostBack(true);
		dati.addElement(de);

		if (mode.equals(PorteDelegateCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_DELEGATE_TIPO_MODE_CORRELAZIONE_URL_BASED) ||
				mode.equals(PorteDelegateCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_DELEGATE_TIPO_MODE_CORRELAZIONE_CONTENT_BASED)) {
			de = new DataElement();
			de.setLabel(PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_PATTERN);
			if (pattern == null) {
				de.setValue("");
			} else {
				de.setValue(pattern);
			}
			de.setType(DataElementType.TEXT_EDIT);
			de.setName(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_PATTERN);
			de.setSize(80);
			de.setRequired(true);
			dati.addElement(de);
		}

		if(!PorteDelegateCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_DELEGATE_DISABILITATO.equals(mode)){
			String[] tipiGIF = { CostantiConfigurazione.BLOCCA.toString(), CostantiConfigurazione.ACCETTA.toString()};
			de = new DataElement();
			de.setLabel(PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_GESTIONE_IDENTIFICAZIONE_FALLITA);
			de.setType(DataElementType.SELECT);
			de.setName(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_GESTIONE_IDENTIFICAZIONE_FALLITA);
			de.setValues(tipiGIF);
			de.setSelected(gif);
			dati.addElement(de);

			//			String[] tipiRiusoID = { CostantiConfigurazione.DISABILITATO, CostantiConfigurazione.ABILITATO};
			//			de = new DataElement();
			//			de.setLabel(PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_RIUSO_ID_MESSAGGIO);
			//			de.setType(DataElementType.SELECT);
			//			de.setName(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_RIUSO_ID_MESSAGGIO);
			//			de.setValues(tipiRiusoID);
			//			de.setSelected(riuso);
			//			dati.addElement(de);

		}

		if(idcorr != null){
			de = new DataElement();
			de.setValue(idcorr);
			de.setType(DataElementType.HIDDEN);
			de.setName(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID_CORRELAZIONE);
			dati.addElement(de);
		}


		return dati;

	}

	// Controlla i dati del message-security response-flow della porta delegata
	public boolean porteDelegateMessageSecurityResponseCheckData(TipoOperazione tipoOp)
			throws Exception {
		try {
			String id = this.request.getParameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID);
			int idInt = Integer.parseInt(id);
			String nome = this.request.getParameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_NOME);
			String valore = this.request.getParameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_VALORE);

			// Campi obbligatori
			if (nome.equals("") || valore.equals("")) {
				String tmpElenco = "";
				if (nome.equals("")) {
					tmpElenco = "Nome";
				}
				if (valore.equals("")) {
					if (tmpElenco.equals("")) {
						tmpElenco = "Valore";
					} else {
						tmpElenco = tmpElenco + ", Valore";
					}
				}
				this.pd.setMessage("Dati incompleti. E' necessario indicare: " + tmpElenco);
				return false;
			}

			// Controllo che non ci siano spazi nei campi di testo
			//if ((nome.indexOf(" ") != -1) || (valore.indexOf(" ") != -1)) {
			if ((nome.indexOf(" ") != -1) ) {
				this.pd.setMessage("Non inserire spazi nei nomi");
				return false;
			}
			if(valore.startsWith(" ") || valore.endsWith(" ")){
				this.pd.setMessage("Non inserire spazi all'inizio o alla fine dei valori");
				return false;
			}

			// Se tipoOp = add, controllo che il message-security non sia gia' stato
			// registrato per la porta delegata
			if (tipoOp.equals(TipoOperazione.ADD)) {
				boolean giaRegistrato = false;
				PortaDelegata pde = this.porteDelegateCore.getPortaDelegata(idInt);
				String nomeporta = pde.getNome();
				MessageSecurity messageSecurity = pde.getMessageSecurity();

				if(messageSecurity!=null){
					if(messageSecurity.getResponseFlow()!=null){
						for (int i = 0; i < messageSecurity.getResponseFlow().sizeParameterList(); i++) {
							MessageSecurityFlowParameter tmpMessageSecurity =messageSecurity.getResponseFlow().getParameter(i);
							if (nome.equals(tmpMessageSecurity.getNome())) {
								giaRegistrato = true;
								break;
							}
						}
					}
				}

				if (giaRegistrato) {
					this.pd.setMessage("La proprieta' di message-security " + nome + " &egrave; gi&agrave; stato associata alla porta delegata " + nomeporta);
					return false;
				}
			}

			return true;

		} catch (Exception e) {
			this.log.error("Exception: " + e.getMessage(), e);
			throw new Exception(e);
		}
	}






	// Controlla i dati della porta delegata
	public boolean porteDelegateCheckData(TipoOperazione tipoOp, String oldNomePD)
			throws Exception {
		try {
			//String id = this.request.getParameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID);
			String nomePD = this.request.getParameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_NOME_PORTA);
			String idsogg = this.request.getParameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID_SOGGETTO);
			int soggInt = Integer.parseInt(idsogg);
			// String descr = this.request.getParameter("descr");
			String urlinv = this.request.getParameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_URL_DI_INVOCAZIONE);
			String autenticazione = this.request.getParameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_AUTENTICAZIONE);
			String nomeauth = this.request.getParameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_NOME_AUTENTICAZIONE);
			String autorizzazione = this.request.getParameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_AUTORIZZAZIONE);
			String nomeautor = this.request.getParameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_NOME_AUTORIZZAZIONE);
			String soggid = this.request.getParameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_SOGGETTO_ID);
			String tiposp = this.request.getParameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_TIPO_SP);
			String modesp = this.request.getParameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_MODE_SP);
			String sp = this.request.getParameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_SP);
			String servid = this.request.getParameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_SERVIZIO_ID);
			String tiposervizio = this.request.getParameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_TIPO_SERVIZIO);
			String modeservizio = this.request.getParameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_MODE_SERVIZIO);
			String servizio = this.request.getParameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_SERVIZIO);
			String azid = this.request.getParameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_AZIONE_ID);
			String modeaz = this.request.getParameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_MODE_AZIONE);
			String azione = this.request.getParameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_AZIONE);
			String xsd = this.request.getParameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_XSD);

			// Campi obbligatori
			if (nomePD==null || nomePD.equals("")) {
				this.pd.setMessage("Dati incompleti. E' necessario indicare il Nome");
				return false;
			}

			if (!modesp.equals(PorteDelegateCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_DELEGATE_MODE_REGISTER_INPUT) && 
					!modesp.equals(PorteDelegateCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_DELEGATE_MODE_INPUT_BASED) &&
					(tiposp.equals("") || sp.equals(""))) {
				String tmpElenco = "";
				if (tiposp.equals("")) {
					tmpElenco = "Tipo soggetto erogatore";
				}
				if (sp.equals("")) {
					if(modesp.equals(PorteDelegateCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_DELEGATE_MODE_URL_BASED)
							|| modesp.equals(PorteDelegateCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_DELEGATE_MODE_CONTENT_BASED)){
						if (tmpElenco.equals("")) {
							tmpElenco = "Pattern soggetto erogatore";
						} else {
							tmpElenco = tmpElenco + ", Pattern soggetto erogatore";
						}
					}else{
						if (tmpElenco.equals("")) {
							tmpElenco = "Nome soggetto erogatore";
						} else {
							tmpElenco = tmpElenco + ", Nome soggetto erogatore";
						}
					}
				}
				this.pd.setMessage("Dati incompleti. E' necessario indicare: " + tmpElenco);
				return false;
			}
			if (modesp.equals(PorteDelegateCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_DELEGATE_MODE_REGISTER_INPUT) && (soggid == null)) {
				this.pd.setMessage("Dati incompleti. Non &egrave; stato trovato nessun soggetto erogatore. Scegliere una delle altre modalit&agrave");
				return false;
			}
			if (modesp.equals(PorteDelegateCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_DELEGATE_MODE_INPUT_BASED) && (tiposp.equals(""))) {
				this.pd.setMessage("Dati incompleti. E' necessario indicare il Tipo soggetto erogatore");
				return false;
			}

			if (!modeservizio.equals(PorteDelegateCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_DELEGATE_MODE_REGISTER_INPUT) && 
					!modeservizio.equals(PorteDelegateCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_DELEGATE_MODE_INPUT_BASED) &&
					(tiposervizio.equals("") || servizio.equals(""))) {
				String tmpElenco = "";
				if (tiposervizio.equals("")) {
					tmpElenco = "Tipo servizio";
				}
				if (servizio.equals("")) {
					if(modeservizio.equals(PorteDelegateCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_DELEGATE_MODE_URL_BASED) ||
							modeservizio.equals(PorteDelegateCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_DELEGATE_MODE_CONTENT_BASED)){
						if (tmpElenco.equals("")) {
							tmpElenco = "Pattern servizio";
						} else {
							tmpElenco = tmpElenco + ", Pattern servizio";
						}
					}else{
						if (tmpElenco.equals("")) {
							tmpElenco = "Nome servizio";
						} else {
							tmpElenco = tmpElenco + ", Nome servizio";
						}
					}
				}
				this.pd.setMessage("Dati incompleti. E' necessario indicare: " + tmpElenco);
				return false;
			}
			if (modeservizio.equals(PorteDelegateCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_DELEGATE_MODE_REGISTER_INPUT) && (servid == null)) {
				this.pd.setMessage("Dati incompleti. Non &egrave; stato trovato nessun servizio associato al soggetto erogatore. Scegliere una delle altre modalit&agrave");
				return false;
			}
			if (modeservizio.equals(PorteDelegateCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_DELEGATE_MODE_INPUT_BASED) && (tiposervizio.equals(""))) {
				this.pd.setMessage("Dati incompleti. E' necessario indicare il Tipo servizio");
				return false;
			}

			if ((modeaz != null) && modeaz.equals(PorteDelegateCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_DELEGATE_MODE_REGISTER_INPUT) && (azid == null)) {
				this.pd.setMessage("Dati incompleti. Non &egrave; stata trovata nessuna azione associata al servizio. Scegliere una delle altre modalit&agrave");
				return false;
			}
			if ( modeaz!=null && (modeaz.equals(PorteDelegateCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_DELEGATE_MODE_URL_BASED) || 
					modeaz.equals(PorteDelegateCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_DELEGATE_MODE_CONTENT_BASED)) && (azione==null || azione.equals(""))) {
				String tmpElenco = "";
				if (tmpElenco.equals("")) {
					tmpElenco = "Pattern azione";
				} else {
					tmpElenco = tmpElenco + ", Pattern azione";
				}
				this.pd.setMessage("Dati incompleti. E' necessario indicare: " + tmpElenco);
				return false;
			}

			// Controllo che non ci siano spazi nei campi di testo
			if ((nomePD.indexOf(" ") != -1) || (urlinv.indexOf(" ") != -1)) {
				this.pd.setMessage("Non inserire spazi nei campi di testo");
				return false;
			}
			if (!modesp.equals(PorteDelegateCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_DELEGATE_MODE_REGISTER_INPUT)) {
				if (modesp.equals(PorteDelegateCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_DELEGATE_MODE_INPUT_BASED)) {
					if (tiposp.indexOf(" ") != -1) {
						this.pd.setMessage("Non inserire spazi nei campi di testo");
						return false;
					}
				} else {
					if ((tiposp.indexOf(" ") != -1) || (sp.indexOf(" ") != -1)) {
						this.pd.setMessage("Non inserire spazi nei campi di testo");
						return false;
					}
				}
			}
			if (!modeservizio.equals(PorteDelegateCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_DELEGATE_MODE_REGISTER_INPUT)) {
				if (modeservizio.equals(PorteDelegateCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_DELEGATE_MODE_INPUT_BASED)) {
					if (tiposervizio.indexOf(" ") != -1) {
						this.pd.setMessage("Non inserire spazi nei campi di testo");
						return false;
					}
				} else {
					if ((tiposervizio.indexOf(" ") != -1) || (servizio.indexOf(" ") != -1)) {
						this.pd.setMessage("Non inserire spazi nei campi di testo");
						return false;
					}
				}
			}
			if ((modeaz != null) && !modeaz.equals(PorteDelegateCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_DELEGATE_MODE_REGISTER_INPUT) && 
					!modeaz.equals(PorteDelegateCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_DELEGATE_MODE_INPUT_BASED) &&
					!modeaz.equals(PorteDelegateCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_DELEGATE_MODE_SOAP_ACTION_BASED) &&
					(azione.indexOf(" ") != -1)) {
				this.pd.setMessage("Non inserire spazi nei campi di testo");
				return false;
			}

			// Controllo che i campi "select" abbiano uno dei valori ammessi
			if (!autenticazione.equals(PorteDelegateCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_DELEGATE_AUTENTICAZIONE_SSL) && 
					!autenticazione.equals(PorteDelegateCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_DELEGATE_AUTENTICAZIONE_BASIC) && 
					!autenticazione.equals(PorteDelegateCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_DELEGATE_AUTENTICAZIONE_NONE) && 
					!autenticazione.equals(PorteDelegateCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_DELEGATE_AUTENTICAZIONE_CUSTOM)) {
				this.pd.setMessage("Autenticazione dev'essere ssl, basic, none o custom");
				return false;
			}
			if (!autorizzazione.equals(PorteDelegateCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_DELEGATE_AUTORIZZAZIONE_OPENSPCOOP) &&
					!autorizzazione.equals(PorteDelegateCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_DELEGATE_AUTORIZZAZIONE_NONE) && 
					!autorizzazione.equals(PorteDelegateCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_DELEGATE_AUTORIZZAZIONE_CUSTOM)) {
				this.pd.setMessage("Autorizzazione dev'essere openspcoop, none o custom");
				return false;
			}
			if (!modesp.equals(PorteDelegateCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_DELEGATE_MODE_USER_INPUT) &&
					!modesp.equals(PorteDelegateCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_DELEGATE_MODE_REGISTER_INPUT) 
					&& !modesp.equals(PorteDelegateCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_DELEGATE_MODE_URL_BASED) && 
					!modesp.equals(PorteDelegateCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_DELEGATE_MODE_CONTENT_BASED) &&
					!modesp.equals(PorteDelegateCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_DELEGATE_MODE_INPUT_BASED)) {
				this.pd.setMessage("Mode SP dev'essere user-input, register-input, url-based, content-based o input-based");
				return false;
			}
			if (!modeservizio.equals(PorteDelegateCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_DELEGATE_MODE_USER_INPUT) && 
					!modeservizio.equals(PorteDelegateCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_DELEGATE_MODE_REGISTER_INPUT) && 
					!modeservizio.equals(PorteDelegateCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_DELEGATE_MODE_URL_BASED) &&
					!modeservizio.equals(PorteDelegateCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_DELEGATE_MODE_CONTENT_BASED) &&
					!modeservizio.equals(PorteDelegateCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_DELEGATE_MODE_INPUT_BASED)) {
				this.pd.setMessage("Mode Servizio dev'essere user-input, register-input, url-based, content-based o input-based");
				return false;
			}
			if ((modeaz != null) && !modeaz.equals(PorteDelegateCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_DELEGATE_MODE_USER_INPUT) && 
					!modeaz.equals(PorteDelegateCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_DELEGATE_MODE_REGISTER_INPUT) && 
					!modeaz.equals(PorteDelegateCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_DELEGATE_MODE_URL_BASED) && 
					!modeaz.equals(PorteDelegateCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_DELEGATE_MODE_CONTENT_BASED) && 
					!modeaz.equals(PorteDelegateCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_DELEGATE_MODE_INPUT_BASED) && 
					!modeaz.equals(PorteDelegateCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_DELEGATE_MODE_SOAP_ACTION_BASED) && 
					!modeaz.equals(PorteDelegateCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_DELEGATE_MODE_WSDL_BASED)) {
				this.pd.setMessage("Mode Azione dev'essere user-input, register-input, url-based, content-based, input-based, soap-action-based o wsdl-based");
				return false;
			}
			if (!xsd.equals(PorteDelegateCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_DELEGATE_XSD_ABILITATO) &&
					!xsd.equals(PorteDelegateCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_DELEGATE_XSD_DISABILITATO) &&
					!xsd.equals(PorteDelegateCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_DELEGATE_XSD_WARNING_ONLY)) {
				this.pd.setMessage("Validazione XSD dev'essere abilitato, disabilitato o warningOnly");
				return false;
			}

			// Se autenticazione = custom, nomeauth dev'essere specificato
			if (autenticazione.equals(PorteDelegateCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_DELEGATE_AUTENTICAZIONE_CUSTOM) && 
					(nomeauth == null || nomeauth.equals(""))) {
				this.pd.setMessage("Indicare un nome per l'autenticazione");
				return false;
			}

			// Se autorizzazione = custom, nomeautor dev'essere specificato
			if (autorizzazione.equals(PorteDelegateCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_DELEGATE_AUTORIZZAZIONE_CUSTOM) && 
					(nomeautor == null || nomeautor.equals(""))) {
				this.pd.setMessage("Indicare un nome per l'autorizzazione");
				return false;
			}

			// Se modesp = register-input, controllo che il soggetto sia uno di
			// quelli disponibili
			// Se modeservizio = register-input, controllo che il servizio sia
			// uno
			// di quelli disponibili
			// Se modeaz = register-input, controllo che l'azione sia una di
			// quelle
			// disponibili
			if (modesp.equals(PorteDelegateCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_DELEGATE_MODE_REGISTER_INPUT)) {
				/*IDSoggetto idSoggetto = new IDSoggetto(soggid.split("/")[0], soggid.split("/")[1]);
					boolean soggEsiste = this.core.existsSoggetto(idSoggetto);
					if (!soggEsiste) {
						this.pd.setMessage("Il soggetto erogatore specificato non esiste");
						return false;
					}*/ // Questo controllo serve??? Il valore è stato preso da una select list!!!
			}

			if (modeservizio.equals(PorteDelegateCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_DELEGATE_MODE_REGISTER_INPUT)) {
				/*IDSoggetto idSoggetto = new IDSoggetto(soggid.split("/")[0], soggid.split("/")[1]);
					IDServizio idServizio = new IDServizio(idSoggetto, servid.split("/")[0], servid.split("/")[1]);
					boolean servEsiste = this.core.existsAccordoServizioParteSpecifica(idServizio);
					if (!servEsiste) {
						this.pd.setMessage("Il servizio specificato non esiste");
						return false;
					}*/ // Questo controllo serve??? Il valore è stato preso da una select list!!!
			}

			if ((modeaz != null) && modeaz.equals(PorteDelegateCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_DELEGATE_MODE_REGISTER_INPUT)) {

				/*int servInt = Integer.parseInt(servid);
					AccordoServizioParteSpecifica myServ = this.core.getAccordoServizioParteSpecifica(servInt);
					boolean trovataAz = false;
					if(myServ.getPortType()!=null){
						trovataAz = this.core.existsAccordoServizioOperation(Integer.parseInt(azid)); 
						if (!trovataAz) {
							this.pd.setMessage("L'Azione dev'essere scelta tra quelle associate al servizio "+myServ.getPortType()+" dell'accordo di servizio "+myServ.getAccordoServizio());
							return false;
						}
					}
					else{
						trovataAz = this.core.existsAccordoServizioAzione(Integer.parseInt(azid));
						if (!trovataAz) {
							this.pd.setMessage("L'Azione dev'essere scelta tra quelle associate all'accordo di servizio "+myServ.getAccordoServizio());
							return false;
						}
					}*/ // Questo controllo serve??? Il valore è stato preso da una select list!!!
			}

			IDPortaDelegata idPD = new IDPortaDelegata();

			if(this.core.isRegistroServiziLocale()){
				// Soggetto Fruitore
				Soggetto soggettoFruitore = null;
				soggettoFruitore = this.soggettiCore.getSoggettoRegistro(soggInt);
				IDSoggetto idSO = new IDSoggetto(soggettoFruitore.getTipo(), soggettoFruitore.getNome());
				idPD.setSoggettoFruitore(idSO);
			}
			else{
				// Soggetto Fruitore
				org.openspcoop2.core.config.Soggetto soggettoFruitore = null;
				soggettoFruitore = this.soggettiCore.getSoggetto(soggInt);
				IDSoggetto idSO = new IDSoggetto(soggettoFruitore.getTipo(), soggettoFruitore.getNome());
				idPD.setSoggettoFruitore(idSO);
			}
			// se url invocazione null allora la url e' il nome della porta
			urlinv = (("".equals(urlinv) || urlinv == null) ? nomePD : urlinv);

			idPD.setLocationPD(urlinv);

			// Se tipoOp = add, controllo che la porta delegata non sia gia'
			// stata registrata
			if (tipoOp == TipoOperazione.ADD) {
				boolean giaRegistrato = false;
				try {

					// controllo puntuale su nome
					giaRegistrato = this.porteDelegateCore.getIdPortaDelegata(nomePD, idPD.getSoggettoFruitore().getTipo(), idPD.getSoggettoFruitore().getNome()) > 0;
					// controllo su location e nome
					if (!giaRegistrato)
						giaRegistrato = this.porteDelegateCore.getPortaDelegata(idPD) != null;
				} catch (DriverConfigurazioneNotFound e) {
					giaRegistrato = false;
				}

				if (giaRegistrato) {
					this.pd.setMessage("Esiste gia' una Porta Delegata con nome (o location) [" + nomePD + "] associata al Soggetto [" + idPD.getSoggettoFruitore().toString() + "]");
					return false;
				}
			} else if (TipoOperazione.CHANGE == tipoOp) {
				PortaDelegata portaDelegata = null;
				try {
					// controllo su nome (non possono esistere 2 pd con stesso
					// nome dello stesso fruitore)
					if (!nomePD.equals(oldNomePD)) {
						long curID = this.porteDelegateCore.getIdPortaDelegata(nomePD, idPD.getSoggettoFruitore().getTipo(), idPD.getSoggettoFruitore().getNome());
						if (curID > 0) {
							this.pd.setMessage("Esiste gia' una Porta Delegata con nome [" + nomePD + "] associata al Soggetto [" + idPD.getSoggettoFruitore().toString() + "]");
							return false;
						}
					}

					// controllo porta delegata per location (questo controlla
					// anche il nome in caso di location non presente)
					portaDelegata = this.porteDelegateCore.getPortaDelegata(idPD);

				} catch (DriverConfigurazioneNotFound e) {
					// ok non esiste un altra porta delegata del fruitore con
					// questa location
				}

				// controllo se la pdd che ho ottenuto e' quella che sto
				// modificando
				// in tal caso procedo con l update altrimenti non posso fare
				// update in quanto pdd gia esistente
				long oldIDpd =  this.porteDelegateCore.getIdPortaDelegata(oldNomePD, idPD.getSoggettoFruitore().getTipo(), idPD.getSoggettoFruitore().getNome());
				if (portaDelegata != null) {
					if (oldIDpd != portaDelegata.getId()) {
						this.pd.setMessage("Esiste gia' una Porta Delegata con nome [" + portaDelegata.getNome() + "]" + (portaDelegata.getLocation() != null && !"".equals(portaDelegata.getLocation()) ? " o location [" + portaDelegata.getLocation() + "]" : "") + " associata al Soggetto [" + idPD.getSoggettoFruitore().toString() + "]");
						return false;
					}
				}
				
				// Controllo che se e' stato cambiato il tipo di autenticazione, non devono essere presenti serviziApplicativi incompatibili
				if(portaDelegata==null){
					// la prelevo con il vecchio nome
					portaDelegata = this.porteDelegateCore.getPortaDelegata(oldIDpd);
				}
				if(autenticazione!=null && autenticazione.equals(portaDelegata.getAutenticazione())==false){
					CredenzialeTipo c = CredenzialeTipo.toEnumConstant(autenticazione);
					if(c!=null){
						if(portaDelegata.sizeServizioApplicativoList()>0){
							
							boolean saCompatibili = true;
							for (int i = 0; i < portaDelegata.sizeServizioApplicativoList(); i++) {
								
								IDServizioApplicativo idServizioApplicativo = new IDServizioApplicativo();
								idServizioApplicativo.setNome(portaDelegata.getServizioApplicativo(i).getNome());
								idServizioApplicativo.setIdSoggettoProprietario(idPD.getSoggettoFruitore());
								ServizioApplicativo saTmp = this.saCore.getServizioApplicativo(idServizioApplicativo);
								
								if(saTmp.getInvocazionePorta()==null){
									saCompatibili=false;
									break;
								}
								if(saTmp.getInvocazionePorta().sizeCredenzialiList()<=0){
									saCompatibili=false;
									break;
								}
								
								boolean ok = false;
								for (int j = 0; j < saTmp.getInvocazionePorta().sizeCredenzialiList(); j++) {
									CredenzialeTipo cSA =saTmp.getInvocazionePorta().getCredenziali(j).getTipo();
									if( c.equals(cSA) ){
										ok = true;
										break;
									}
								}
								if(!ok){
									saCompatibili=false;
									break;
								}
							}
							
							if(saCompatibili==false){
								this.pd.setMessage("Non e' possibile modificare il tipo di autenticazione da ["+portaDelegata.getAutenticazione()+"] a ["+autenticazione+
										"], poiche' risultano associati alla porta delegata dei servizi applicativi non compatibili, nella modalita' di accesso, con il nuovo tipo di autenticazione");
								return false;
							}
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

	// Controlla i dati del servizioApplicativo della porta delegata
	public boolean porteDelegateServizioApplicativoCheckData(TipoOperazione tipoOp)
			throws Exception {
		try {
			String id = this.request.getParameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID);
			int idInt = Integer.parseInt(id);
			String idsogg = this.request.getParameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID_SOGGETTO);
			int soggInt = Integer.parseInt(idsogg);
			String servizioApplicativo = this.request.getParameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_SERVIZIO_APPLICATIVO);

			// Campi obbligatori
			if (servizioApplicativo.equals("")) {
				this.pd.setMessage("Dati incompleti. E' necessario indicare un Servizio Applicativo");
				return false;
			}

			// Controllo che il servizioApplicativo appartenga alla lista di
			// servizioApplicativo disponibili per il soggetto
			boolean trovatoServizioApplicativo = false;

			// Prendo il nome e il tipo del soggetto
			String nomeprov = null;
			String tipoprov = null;
			if(this.core.isRegistroServiziLocale()){
				Soggetto mySogg = this.soggettiCore.getSoggettoRegistro(soggInt);
				nomeprov = mySogg.getNome();
				tipoprov = mySogg.getTipo();
			}else{
				org.openspcoop2.core.config.Soggetto mySogg = this.soggettiCore.getSoggetto(soggInt);
				nomeprov = mySogg.getNome();
				tipoprov = mySogg.getTipo();
			}

			IDSoggetto ids = new IDSoggetto(tipoprov, nomeprov);
			trovatoServizioApplicativo = this.saCore.existsServizioApplicativo(ids, servizioApplicativo);
			if (!trovatoServizioApplicativo) {
				this.pd.setMessage("Il Servizio Applicativo dev'essere scelto tra quelli definiti nel pannello Servizi Applicativi ed associati al soggetto " + tipoprov + "/" + nomeprov);
				return false;
			}

			// Se tipoOp = add, controllo che il servizioApplicativo non sia
			// gia'
			// stato
			// registrato per la porta delegata
			if (tipoOp.equals(TipoOperazione.ADD)) {
				boolean giaRegistrato = false;

				// Prendo il nome della porta delegata
				PortaDelegata pde = this.porteDelegateCore.getPortaDelegata(idInt);
				String nomeporta = pde.getNome();

				for (int i = 0; i < pde.sizeServizioApplicativoList(); i++) {
					ServizioApplicativo tmpSA = pde.getServizioApplicativo(i);
					if (servizioApplicativo.equals(tmpSA.getNome())) {
						giaRegistrato = true;
						break;
					}
				}

				if (giaRegistrato) {
					this.pd.setMessage("Il Servizio Applicativo " + servizioApplicativo + " &egrave; gi&agrave; stato associato alla porta delegata " + nomeporta);
					return false;
				}
			}

			return true;
		} catch (Exception e) {
			this.log.error("Exception: " + e.getMessage(), e);
			throw new Exception(e);
		}
	}

	// Prepara la lista di porte delegate
	public void preparePorteDelegateList(ISearch ricerca, List<PortaDelegata> lista)
			throws Exception {
		try {
			Boolean contaListe = ServletUtils.getContaListeFromSession(this.session);
			boolean isModalitaAvanzata = ServletUtils.getUserFromSession(this.session).getInterfaceType().equals(InterfaceType.AVANZATA);
			// prelevo il flag che mi dice da quale pagina ho acceduto la sezione delle porte delegate
			Boolean useIdSogg= ServletUtils.getBooleanAttributeFromSession(PorteDelegateCostanti.ATTRIBUTO_PORTE_DELEGATE_USA_ID_SOGGETTO, this.session);

			IExtendedListServlet extendedServletList = this.core.getExtendedServletPortaDelegata();
			
			String id = this.request.getParameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID_SOGGETTO);
			if(useIdSogg)
				ServletUtils.addListElementIntoSession(this.session, PorteDelegateCostanti.OBJECT_NAME_PORTE_DELEGATE,
						new Parameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID_SOGGETTO, id));
			else 
				ServletUtils.addListElementIntoSession(this.session, PorteDelegateCostanti.OBJECT_NAME_PORTE_DELEGATE);

			// Prendo il soggetto se e' null vuol dire che sono passato dal menu'
			String tmpTitle = null;
			if(useIdSogg){
				if(this.core.isRegistroServiziLocale()){
					org.openspcoop2.core.registry.Soggetto soggetto = this.soggettiCore.getSoggettoRegistro(Integer.parseInt(id));
					tmpTitle = soggetto.getTipo() + "/" + soggetto.getNome();
				}else{
					org.openspcoop2.core.config.Soggetto soggetto = this.soggettiCore.getSoggetto(Integer.parseInt(id));
					tmpTitle = soggetto.getTipo() + "/" + soggetto.getNome();
				}
			}

			int idLista = useIdSogg ? Liste.PORTE_DELEGATE_BY_SOGGETTO : Liste.PORTE_DELEGATE;
			int limit = ricerca.getPageSize(idLista);
			int offset = ricerca.getIndexIniziale(idLista);
			String search = ServletUtils.getSearchFromSession(ricerca, idLista);

			this.pd.setIndex(offset);
			this.pd.setPageSize(limit);
			this.pd.setNumEntries(ricerca.getNumEntries(idLista));



			List<Parameter> lstParam = new ArrayList<Parameter>();



			if(useIdSogg){
				lstParam.add(new Parameter(PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_SOGGETTI, null));
				lstParam.add(new Parameter(Costanti.PAGE_DATA_TITLE_LABEL_ELENCO, SoggettiCostanti.SERVLET_NAME_SOGGETTI_LIST));

				if(search.equals("")){
					this.pd.setSearchDescription("");
					lstParam.add(new Parameter(PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_PORTE_DELEGATE_DI + tmpTitle,null));
				}
				else{
					lstParam.add(new Parameter(PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_PORTE_DELEGATE_DI + tmpTitle, PorteDelegateCostanti.SERVLET_NAME_PORTE_DELEGATE_LIST + "?"
							+ PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID_SOGGETTO + "=" + id
							));
					lstParam.add(new Parameter(PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_RISULTATI_RICERCA, null));

				}
			} else {
				lstParam.add(new Parameter(PorteDelegateCostanti.LABEL_PORTE_DELEGATE, null));


				if(search.equals("")){
					this.pd.setSearchDescription("");
					lstParam.add(new Parameter(Costanti.PAGE_DATA_TITLE_LABEL_ELENCO,null));
				}
				else{
					lstParam.add(new Parameter(Costanti.PAGE_DATA_TITLE_LABEL_ELENCO, PorteDelegateCostanti.SERVLET_NAME_PORTE_DELEGATE_LIST));
					lstParam.add(new Parameter(PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_RISULTATI_RICERCA, null));

				}
			}

			ServletUtils.setPageDataTitle(this.pd, lstParam.toArray(new Parameter[lstParam.size()]));

			// controllo eventuali risultati ricerca
			if (!search.equals("")) {
				this.pd.setSearch("on");
				this.pd.setSearchDescription("Porte Delegate contenenti la stringa '" + search + "'");
			}

			// setto le label delle colonne
			List<String> labelsList= new ArrayList<String>();

			labelsList.add(PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_NOME); 
			labelsList.add(PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_DESCRIZIONE); 
			labelsList.add(PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_SERVIZI_APPLICATIVI); 
			labelsList.add(PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_MESSAGE_SECURITY); 
			//if(InterfaceType.AVANZATA.equals(ServletUtils.getUserFromSession(this.session).getInterfaceType())){
			labelsList.add(PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_MTOM);
			//}
			labelsList.add(PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_CORRELAZIONE_APPLICATIVA_BR_RICHIESTA); 
			labelsList.add(PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_CORRELAZIONE_APPLICATIVA_BR_RISPOSTA); 
			if(extendedServletList!=null && extendedServletList.showExtendedInfo(this.request, this.session)){
				labelsList.add(extendedServletList.getListTitle(this));
			}

			String[] labels = labelsList.toArray(new String[labelsList.size()]);

			this.pd.setLabels(labels);

			// preparo i dati
			Vector<Vector<DataElement>> dati = new Vector<Vector<DataElement>>();

			if (lista != null) {
				Iterator<PortaDelegata> it = lista.iterator();



				while (it.hasNext()) {
					PortaDelegata pd = it.next();

					Vector<DataElement> e = new Vector<DataElement>();

					DataElement de = new DataElement();
					de.setType(DataElementType.HIDDEN);
					de.setValue("" + pd.getId());
					e.addElement(de);

					de = new DataElement();
					de.setUrl(PorteDelegateCostanti.SERVLET_NAME_PORTE_DELEGATE_CHANGE,
							new Parameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID_SOGGETTO, "" + pd.getIdSoggetto()),
							new Parameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_NOME_PORTA,pd.getNome()),
							new Parameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID, "" + pd.getId())
							);
					de.setValue(pd.getNome());
					de.setIdToRemove(pd.getId().toString());
					e.addElement(de);

					de = new DataElement();
					de.setValue(pd.getDescrizione());
					e.addElement(de);

					de = new DataElement();
					de.setUrl(PorteDelegateCostanti.SERVLET_NAME_PORTE_DELEGATE_SERVIZIO_APPLICATIVO_LIST,
							new Parameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID_SOGGETTO, "" + pd.getIdSoggetto()),
							new Parameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID, ""+pd.getId())
							);
					if (contaListe) {
						int numSA = pd.sizeServizioApplicativoList();
						ServletUtils.setDataElementVisualizzaLabel(de,new Long(numSA));
					} else
						ServletUtils.setDataElementVisualizzaLabel(de);
					e.addElement(de);

					de = new DataElement();
					de.setUrl( 
							PorteDelegateCostanti.SERVLET_NAME_PORTE_DELEGATE_MESSAGE_SECURITY,
							new Parameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID_SOGGETTO, "" + pd.getIdSoggetto()),
							new Parameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID, ""+pd.getId())
							);
					de.setValue(pd.getStatoMessageSecurity());
					e.addElement(de);

					//if(InterfaceType.AVANZATA.equals(ServletUtils.getUserFromSession(this.session).getInterfaceType())){
					de = new DataElement();
					de.setUrl( 
							PorteDelegateCostanti.SERVLET_NAME_PORTE_DELEGATE_MTOM,
							new Parameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID_SOGGETTO, "" + pd.getIdSoggetto()),
							new Parameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID, ""+pd.getId())
							);

					boolean isMTOMAbilitatoReq = false;
					boolean isMTOMAbilitatoRes= false;
					if(pd.getMtomProcessor()!= null){
						if(pd.getMtomProcessor().getRequestFlow() != null){
							if(pd.getMtomProcessor().getRequestFlow().getMode() != null){
								MTOMProcessorType mode = pd.getMtomProcessor().getRequestFlow().getMode();
								if(!mode.equals(MTOMProcessorType.DISABLE))
									isMTOMAbilitatoReq = true;
							}
						}

						if(pd.getMtomProcessor().getResponseFlow() != null){
							if(pd.getMtomProcessor().getResponseFlow().getMode() != null){
								MTOMProcessorType mode = pd.getMtomProcessor().getResponseFlow().getMode();
								if(!mode.equals(MTOMProcessorType.DISABLE))
									isMTOMAbilitatoRes = true;
							}
						}
					}

					if(isMTOMAbilitatoReq || isMTOMAbilitatoRes)
						de.setValue(PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_MTOM_ABILITATO);
					else 
						de.setValue(PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_MTOM_DISABILITATO);
					e.addElement(de);
					//}

					de = new DataElement();
					de.setUrl(
							PorteDelegateCostanti.SERVLET_NAME_PORTE_DELEGATE_CORRELAZIONE_APPLICATIVA_REQUEST_LIST,
							new Parameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID_SOGGETTO, "" + pd.getIdSoggetto()),
							new Parameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID, ""+pd.getId()),
							new Parameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_NOME,pd.getNome())
							);
					if (contaListe) {
						int numCorrelazione = 0;
						if (pd.getCorrelazioneApplicativa() != null)
							numCorrelazione = pd.getCorrelazioneApplicativa().sizeElementoList();
						ServletUtils.setDataElementVisualizzaLabel(de,new Long(numCorrelazione));
					} else
						ServletUtils.setDataElementVisualizzaLabel(de);
					e.addElement(de);

					de = new DataElement();
					de.setUrl(PorteDelegateCostanti.SERVLET_NAME_PORTE_DELEGATE_CORRELAZIONE_APPLICATIVA_RESPONSE_LIST,
							new Parameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID_SOGGETTO, "" + pd.getIdSoggetto()),
							new Parameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID, ""+pd.getId()),
							new Parameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_NOME,pd.getNome())
							);
					if (contaListe) {
						int numCorrelazione = 0;
						if (pd.getCorrelazioneApplicativaRisposta() != null)
							numCorrelazione = pd.getCorrelazioneApplicativaRisposta().sizeElementoList();
						ServletUtils.setDataElementVisualizzaLabel(de,new Long(numCorrelazione));
					} else
						ServletUtils.setDataElementVisualizzaLabel(de);
					e.addElement(de);

					if(extendedServletList!=null && extendedServletList.showExtendedInfo(this.request, this.session)){
						de = new DataElement();
						de.setUrl(PorteDelegateCostanti.SERVLET_NAME_PORTE_DELEGATE_EXTENDED_LIST,
								new Parameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID_SOGGETTO, "" + pd.getIdSoggetto()),
								new Parameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID, ""+pd.getId()),
								new Parameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_NOME,pd.getNome())
								);
						if (contaListe) {
							int numExtended = extendedServletList.sizeList(pd);
							ServletUtils.setDataElementVisualizzaLabel(de,new Long(numExtended));
						} else
							ServletUtils.setDataElementVisualizzaLabel(de);
						e.addElement(de);
					}
					
					dati.addElement(e);
				}
			}

			this.pd.setDati(dati);
			if (useIdSogg){ 
				if(isModalitaAvanzata){
					this.pd.setAddButton(true);
				}
				else{
					this.pd.setAddButton(false);
					this.pd.setRemoveButton(false);
					this.pd.setSelect(false);
				}
			} else {
				this.pd.setAddButton(false);
			}


		} catch (Exception e) {
			this.log.error("Exception: " + e.getMessage(), e);
			throw new Exception(e);
		}
	}


	// Prepara la lista di sil delle porte delegate
	public void preparePorteDelegateServizioApplicativoList(String nomePorta, ISearch ricerca, List<ServizioApplicativo> lista)
			throws Exception {
		try {
			// prelevo il flag che mi dice da quale pagina ho acceduto la sezione delle porte delegate
			Boolean useIdSogg= ServletUtils.getBooleanAttributeFromSession(PorteDelegateCostanti.ATTRIBUTO_PORTE_DELEGATE_USA_ID_SOGGETTO, this.session);


			String id = this.request.getParameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID);
			String idsogg = this.request.getParameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID_SOGGETTO);

			ServletUtils.addListElementIntoSession(this.session, PorteDelegateCostanti.OBJECT_NAME_PORTE_DELEGATE_SERVIZIO_APPLICATIVO,
					new Parameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID, id), 
					new Parameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID_SOGGETTO, idsogg));

			int idLista = Liste.PORTE_DELEGATE_SERVIZIO_APPLICATIVO;
			int limit = ricerca.getPageSize(idLista);
			int offset = ricerca.getIndexIniziale(idLista);
			String search = ServletUtils.getSearchFromSession(ricerca, idLista);

			this.pd.setIndex(offset);
			this.pd.setPageSize(limit);
			this.pd.setNumEntries(ricerca.getNumEntries(idLista));

			String tmpTitle = null;
			if(this.core.isRegistroServiziLocale()){
				org.openspcoop2.core.registry.Soggetto soggetto = this.soggettiCore.getSoggettoRegistro(Integer.parseInt(idsogg));
				tmpTitle = soggetto.getTipo() + "/" + soggetto.getNome();
			}else{
				org.openspcoop2.core.config.Soggetto soggetto = this.soggettiCore.getSoggetto(Integer.parseInt(idsogg));
				tmpTitle = soggetto.getTipo() + "/" + soggetto.getNome();
			}

			PortaDelegata myPD = this.porteDelegateCore.getPortaDelegata(Integer.parseInt(id));
			String idporta = myPD.getNome();

			List<Parameter> lstParam = new ArrayList<Parameter>();

			if(useIdSogg){

				lstParam.add(new Parameter(PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_SOGGETTI, null));
				lstParam.add(new Parameter(Costanti.PAGE_DATA_TITLE_LABEL_ELENCO, SoggettiCostanti.SERVLET_NAME_SOGGETTI_LIST));
				lstParam.add(new Parameter(PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_PORTE_DELEGATE_DI + tmpTitle,
						PorteDelegateCostanti.SERVLET_NAME_PORTE_DELEGATE_LIST,
						new Parameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID_SOGGETTO,idsogg)
						));
				if(search.equals("")){
					this.pd.setSearchDescription("");
					lstParam.add(new Parameter(PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_SERVIZIO_APPLICATIVO_DI + idporta,null));
				}
				else{
					lstParam.add(new Parameter(PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_SERVIZIO_APPLICATIVO_DI + idporta,
							PorteDelegateCostanti.SERVLET_NAME_PORTE_DELEGATE_SERVIZIO_APPLICATIVO_LIST,
							new Parameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID,id),
							new Parameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID_SOGGETTO,idsogg)
							));
					lstParam.add(new Parameter(PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_RISULTATI_RICERCA, null));

				}
			}else {
				lstParam.add(new Parameter(PorteDelegateCostanti.LABEL_PORTE_DELEGATE, null));
				lstParam.add(new Parameter(Costanti.PAGE_DATA_TITLE_LABEL_ELENCO, PorteDelegateCostanti.SERVLET_NAME_PORTE_DELEGATE_LIST));

				if(search.equals("")){
					this.pd.setSearchDescription("");
					lstParam.add(new Parameter(PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_SERVIZIO_APPLICATIVO_DI + idporta,null));
				}
				else{
					lstParam.add(new Parameter(PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_SERVIZIO_APPLICATIVO_DI + idporta,
							PorteDelegateCostanti.SERVLET_NAME_PORTE_DELEGATE_SERVIZIO_APPLICATIVO_LIST,
							new Parameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID,id),
							new Parameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID_SOGGETTO,idsogg)
							));
					lstParam.add(new Parameter(PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_RISULTATI_RICERCA, null));

				}
			}



			ServletUtils.setPageDataTitle(this.pd, lstParam.toArray(new Parameter[lstParam.size()]));

			// controllo eventuali risultati ricerca
			if (!search.equals("")) {
				this.pd.setSearch("on");
				this.pd.setSearchDescription("Servizi Applicativi contenenti la stringa '" + search + "'");
			}

			// setto le label delle colonne
			String[] labels = {PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_SERVIZIO_APPLICATIVO };
			this.pd.setLabels(labels);

			// preparo i dati
			Vector<Vector<DataElement>> dati = new Vector<Vector<DataElement>>();

			if (lista != null) {
				Iterator<ServizioApplicativo> it = lista.iterator();
				while (it.hasNext()) {
					ServizioApplicativo sa = it.next();

					Vector<DataElement> e = new Vector<DataElement>();

					DataElement de = new DataElement();
					de.setUrl(ServiziApplicativiCostanti.SERVLET_NAME_SERVIZI_APPLICATIVI_CHANGE,
							new Parameter(ServiziApplicativiCostanti.PARAMETRO_SERVIZI_APPLICATIVI_ID, sa.getId() + ""),
							new Parameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_PROVIDER, idsogg));
					de.setValue(sa.getNome());
					de.setIdToRemove(sa.getNome());
					e.addElement(de);

					dati.addElement(e);
				}
			}

			this.pd.setDati(dati);
			this.pd.setAddButton(true);

		} catch (Exception e) {
			this.log.error("Exception: " + e.getMessage(), e);
			throw new Exception(e);
		}
	}


	// Prepara la lista di Message-Security response-flow delle porte delegate
	public void preparePorteDelegateMessageSecurityResponseList(String nomePorta, ISearch ricerca, List<MessageSecurityFlowParameter> lista)
			throws Exception {
		try {
			String id = this.request.getParameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID);
			String idsogg = this.request.getParameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID_SOGGETTO);

			// prelevo il flag che mi dice da quale pagina ho acceduto la sezione delle porte delegate
			Boolean useIdSogg= ServletUtils.getBooleanAttributeFromSession(PorteDelegateCostanti.ATTRIBUTO_PORTE_DELEGATE_USA_ID_SOGGETTO, this.session);

			ServletUtils.addListElementIntoSession(this.session, PorteDelegateCostanti.OBJECT_NAME_PORTE_DELEGATE_MESSAGE_SECURITY_RESPONSE, 
					new Parameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID, id),
					new Parameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID_SOGGETTO, idsogg));

			int idLista = Liste.PORTE_DELEGATE_MESSAGE_SECURITY_RESPONSE;
			int limit = ricerca.getPageSize(idLista);
			int offset = ricerca.getIndexIniziale(idLista);
			String search = ServletUtils.getSearchFromSession(ricerca, idLista);

			this.pd.setIndex(offset);
			this.pd.setPageSize(limit);
			this.pd.setNumEntries(ricerca.getNumEntries(idLista));

			String tmpTitle = null;
			if(this.core.isRegistroServiziLocale()){
				org.openspcoop2.core.registry.Soggetto soggetto = this.soggettiCore.getSoggettoRegistro(Integer.parseInt(idsogg));
				tmpTitle = soggetto.getTipo() + "/" + soggetto.getNome();
			}else{
				org.openspcoop2.core.config.Soggetto soggetto = this.soggettiCore.getSoggetto(Integer.parseInt(idsogg));
				tmpTitle = soggetto.getTipo() + "/" + soggetto.getNome();
			}

			PortaDelegata myPD = this.porteDelegateCore.getPortaDelegata(Integer.parseInt(id));
			String idporta = myPD.getNome();

			// setto la barra del titolo
			List<Parameter> lstParam = new ArrayList<Parameter>();
			if(useIdSogg){
				lstParam.add(new Parameter(PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_SOGGETTI, null));
				lstParam.add(new Parameter(Costanti.PAGE_DATA_TITLE_LABEL_ELENCO, SoggettiCostanti.SERVLET_NAME_SOGGETTI_LIST));
				lstParam.add(new Parameter(PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_PORTE_DELEGATE_DI + tmpTitle,
						PorteDelegateCostanti.SERVLET_NAME_PORTE_DELEGATE_LIST,
						new Parameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID_SOGGETTO,idsogg)
						));

			}else {
				lstParam.add(new Parameter(PorteDelegateCostanti.LABEL_PORTE_DELEGATE, null));
				lstParam.add(new Parameter(Costanti.PAGE_DATA_TITLE_LABEL_ELENCO, PorteDelegateCostanti.SERVLET_NAME_PORTE_DELEGATE_LIST));
			}


			lstParam.add(new Parameter(PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_MESSAGE_SECURITY_DI + idporta,
					PorteDelegateCostanti.SERVLET_NAME_PORTE_DELEGATE_MESSAGE_SECURITY,
					new Parameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID,id),
					new Parameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID_SOGGETTO,idsogg)
					));

			if(search.equals("")){
				this.pd.setSearchDescription("");
				lstParam.add(new Parameter(PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_MESSAGE_SECURITY_RESPONSE_FLOW_DI + idporta,null));
			}
			else{
				lstParam.add(new Parameter(PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_MESSAGE_SECURITY_RESPONSE_FLOW_DI + idporta,
						PorteDelegateCostanti.SERVLET_NAME_PORTE_DELEGATE_MESSAGE_SECURITY_RESPONSE_LIST,
						new Parameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID,id),
						new Parameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID_SOGGETTO,idsogg)
						));
				lstParam.add(new Parameter(PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_RISULTATI_RICERCA, null));

			}

			ServletUtils.setPageDataTitle(this.pd, lstParam.toArray(new Parameter[lstParam.size()]));

			// controllo eventuali risultati ricerca
			if (!search.equals("")) {
				this.pd.setSearch("on");
				this.pd.setSearchDescription("Message-Security response-flow contenenti la stringa '" + search + "'");
			}

			// setto le label delle colonne
			String[] labels = { 
					PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_NOME,
					PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_VALORE
			};
			this.pd.setLabels(labels);

			// preparo i dati
			Vector<Vector<DataElement>> dati = new Vector<Vector<DataElement>>();

			if (lista != null) {
				Iterator<MessageSecurityFlowParameter> it = lista.iterator();
				while (it.hasNext()) {
					MessageSecurityFlowParameter wsrfp = it.next();

					Vector<DataElement> e = new Vector<DataElement>();

					DataElement de = new DataElement();
					de.setUrl(PorteDelegateCostanti.SERVLET_NAME_PORTE_DELEGATE_MESSAGE_SECURITY_RESPONSE_CHANGE,
							new Parameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID,id),
							new Parameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID_SOGGETTO,idsogg),
							new Parameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_NOME, wsrfp.getNome())
							);
					de.setValue(wsrfp.getNome());
					de.setIdToRemove(wsrfp.getNome());
					e.addElement(de);

					de = new DataElement();
					de.setValue(wsrfp.getValore());
					e.addElement(de);

					dati.addElement(e);
				}
			}

			this.pd.setDati(dati);
			this.pd.setAddButton(true);

		} catch (Exception e) {
			this.log.error("Exception: " + e.getMessage(), e);
			throw new Exception(e);
		}
	}


	// Prepara la lista di correlazioni applicative delle porte delegate
	public void preparePorteDelegateCorrAppList(String nomePorta, ISearch ricerca, List<CorrelazioneApplicativaElemento> lista)
			throws Exception {
		try {
			String id = this.request.getParameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID);
			String idsogg = this.request.getParameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID_SOGGETTO);

			// prelevo il flag che mi dice da quale pagina ho acceduto la sezione delle porte delegate
			Boolean useIdSogg= ServletUtils.getBooleanAttributeFromSession(PorteDelegateCostanti.ATTRIBUTO_PORTE_DELEGATE_USA_ID_SOGGETTO, this.session);


			ServletUtils.addListElementIntoSession(this.session, PorteDelegateCostanti.OBJECT_NAME_PORTE_DELEGATE_CORRELAZIONE_APPLICATIVA_REQUEST,
					new Parameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID, id),
					new Parameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID_SOGGETTO, idsogg));

			int idLista = Liste.PORTE_DELEGATE_CORRELAZIONE_APPLICATIVA;
			int limit = ricerca.getPageSize(idLista);
			int offset = ricerca.getIndexIniziale(idLista);
			String search = ServletUtils.getSearchFromSession(ricerca, idLista);

			this.pd.setIndex(offset);
			this.pd.setPageSize(limit);
			this.pd.setNumEntries(ricerca.getNumEntries(idLista));

			String tmpTitle = null;
			if(this.core.isRegistroServiziLocale()){
				org.openspcoop2.core.registry.Soggetto soggetto = this.soggettiCore.getSoggettoRegistro(Integer.parseInt(idsogg));
				tmpTitle = soggetto.getTipo() + "/" + soggetto.getNome();
			}
			else{
				org.openspcoop2.core.config.Soggetto soggetto = this.soggettiCore.getSoggetto(Integer.parseInt(idsogg));
				tmpTitle = soggetto.getTipo() + "/" + soggetto.getNome();
			}

			PortaDelegata myPD = this.porteDelegateCore.getPortaDelegata(Integer.parseInt(id));
			String idporta = myPD.getNome();

			// setto la barra del titolo
			List<Parameter> lstParam = new ArrayList<Parameter>();
			if(useIdSogg){
				lstParam.add(new Parameter(PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_SOGGETTI, null));
				lstParam.add(new Parameter(Costanti.PAGE_DATA_TITLE_LABEL_ELENCO, SoggettiCostanti.SERVLET_NAME_SOGGETTI_LIST));
				lstParam.add(new Parameter(PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_PORTE_DELEGATE_DI + tmpTitle,
						PorteDelegateCostanti.SERVLET_NAME_PORTE_DELEGATE_LIST, 
						new Parameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID_SOGGETTO,idsogg)
						));
			}else{
				lstParam.add(new Parameter(PorteDelegateCostanti.LABEL_PORTE_DELEGATE, null));
				lstParam.add(new Parameter(Costanti.PAGE_DATA_TITLE_LABEL_ELENCO, PorteDelegateCostanti.SERVLET_NAME_PORTE_DELEGATE_LIST));
			}

			if(search.equals("")){
				this.pd.setSearchDescription("");
				lstParam.add(new Parameter(PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_CORRELAZIONI_APPLICATIVE_DI + idporta,null));
			}
			else{
				lstParam.add(new Parameter(PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_CORRELAZIONI_APPLICATIVE_DI + idporta,
						PorteDelegateCostanti.SERVLET_NAME_PORTE_DELEGATE_CORRELAZIONE_APPLICATIVA_REQUEST_LIST,
						new Parameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID,id),
						new Parameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID_SOGGETTO,idsogg),
						new Parameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_NOME, idporta)
						));
				lstParam.add(new Parameter(PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_RISULTATI_RICERCA, null));
			}

			ServletUtils.setPageDataTitle(this.pd, lstParam.toArray(new Parameter[lstParam.size()]));

			// controllo eventuali risultati ricerca
			if (!search.equals("")) {
				this.pd.setSearch("on");
				this.pd.setSearchDescription("Correlazioni Applicative contenenti la stringa '" + search + "'");
			}

			// setto le label delle colonne
			String[] labels = {
					PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_ELEMENTO_XML,
					PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_MODALITA_IDENTIFICAZIONE};
			this.pd.setLabels(labels);

			// preparo i dati
			Vector<Vector<DataElement>> dati = new Vector<Vector<DataElement>>();

			if (lista != null) {
				Iterator<CorrelazioneApplicativaElemento> it = lista.iterator();
				while (it.hasNext()) {
					CorrelazioneApplicativaElemento cae = it.next();

					Vector<DataElement> e = new Vector<DataElement>();

					DataElement de = new DataElement();
					de.setType(DataElementType.HIDDEN);
					de.setValue("" + cae.getId());
					e.addElement(de);

					de = new DataElement();
					de.setUrl(
							PorteDelegateCostanti.SERVLET_NAME_PORTE_DELEGATE_CORRELAZIONE_APPLICATIVA_REQUEST_CHANGE,
							new Parameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID,id),
							new Parameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID_SOGGETTO,idsogg),
							new Parameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID_CORRELAZIONE, ""+ cae.getId())
							);
					String nomeElemento = "*";
					if (cae.getNome() != null && !"".equals(cae.getNome()))
						nomeElemento = cae.getNome();
					de.setValue(nomeElemento);
					de.setIdToRemove("" + cae.getId());
					e.addElement(de);

					de = new DataElement();
					if(cae.getIdentificazione()!=null)
						de.setValue(cae.getIdentificazione().toString());
					e.addElement(de);

					dati.addElement(e);
				}
			}

			this.pd.setDati(dati);
			this.pd.setAddButton(true);
		} catch (Exception e) {
			this.log.error("Exception: " + e.getMessage(), e);
			throw new Exception(e);
		}
	}

	// Controlla i dati del message-security request-flow della porta delegata
	public boolean porteDelegateMessageSecurityRequestCheckData(TipoOperazione tipoOp) throws Exception {
		try {
			String id = this.request.getParameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID);
			int idInt = Integer.parseInt(id);
			String nome = this.request.getParameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_NOME);
			String valore = this.request.getParameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_VALORE);

			// Campi obbligatori
			if (nome.equals("") || valore.equals("")) {
				String tmpElenco = "";
				if (nome.equals("")) {
					tmpElenco = "Nome";
				}
				if (valore.equals("")) {
					if (tmpElenco.equals("")) {
						tmpElenco = "Valore";
					} else {
						tmpElenco = tmpElenco + ", Valore";
					}
				}
				this.pd.setMessage("Dati incompleti. E' necessario indicare: " + tmpElenco);
				return false;
			}

			// Controllo che non ci siano spazi nei campi di testo
			//if ((nome.indexOf(" ") != -1) || (valore.indexOf(" ") != -1)) {
			if ((nome.indexOf(" ") != -1) ) {
				this.pd.setMessage("Non inserire spazi nei nomi");
				return false;
			}
			if(valore.startsWith(" ") || valore.endsWith(" ")){
				this.pd.setMessage("Non inserire spazi all'inizio o alla fine dei valori");
				return false;
			}

			// Se tipoOp = add, controllo che il message-security non sia gia' stato
			// registrato per la porta delegata
			if (tipoOp.equals(TipoOperazione.ADD)) {
				boolean giaRegistrato = false;
				PortaDelegata pde = this.porteDelegateCore.getPortaDelegata(idInt);
				String nomeporta = pde.getNome();
				MessageSecurity messageSecurity = pde.getMessageSecurity();

				if(messageSecurity!=null){
					if(messageSecurity.getRequestFlow()!=null){
						for (int i = 0; i < messageSecurity.getRequestFlow().sizeParameterList(); i++) {
							MessageSecurityFlowParameter tmpMessageSecurity =messageSecurity.getRequestFlow().getParameter(i);
							if (nome.equals(tmpMessageSecurity.getNome())) {
								giaRegistrato = true;
								break;
							}
						}
					}
				}

				if (giaRegistrato) {
					this.pd.setMessage("La proprieta' di message-security " + nome + " &egrave; gi&agrave; stato associata alla porta delegata " + nomeporta);
					return false;
				}
			}

			return true;

		} catch (Exception e) {
			this.log.error("Exception: " + e.getMessage(), e);
			throw new Exception(e);
		}
	}

	// Prepara la lista di Message-Security request-flow delle porte delegate
	public void preparePorteDelegateMessageSecurityRequestList(String nomePorta, ISearch ricerca, List<MessageSecurityFlowParameter> lista)
			throws Exception {
		try {

			// prelevo il flag che mi dice da quale pagina ho acceduto la sezione delle porte delegate
			Boolean useIdSogg= ServletUtils.getBooleanAttributeFromSession(PorteDelegateCostanti.ATTRIBUTO_PORTE_DELEGATE_USA_ID_SOGGETTO, this.session);


			String id = this.request.getParameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID);
			String idsogg = this.request.getParameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID_SOGGETTO);

			ServletUtils.addListElementIntoSession(this.session, PorteDelegateCostanti.OBJECT_NAME_PORTE_DELEGATE_MESSAGE_SECURITY_REQUEST,
					new Parameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID, id),
					new Parameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID_SOGGETTO, idsogg));

			int idLista = Liste.PORTE_DELEGATE_MESSAGE_SECURITY_REQUEST;
			int limit = ricerca.getPageSize(idLista);
			int offset = ricerca.getIndexIniziale(idLista);
			String search = ServletUtils.getSearchFromSession(ricerca, idLista);

			this.pd.setIndex(offset);
			this.pd.setPageSize(limit);
			this.pd.setNumEntries(ricerca.getNumEntries(idLista));

			String tmpTitle = null;
			if(this.core.isRegistroServiziLocale()){
				org.openspcoop2.core.registry.Soggetto soggetto = this.soggettiCore.getSoggettoRegistro(Integer.parseInt(idsogg));
				tmpTitle = soggetto.getTipo() + "/" + soggetto.getNome();
			}else{
				org.openspcoop2.core.config.Soggetto soggetto = this.soggettiCore.getSoggetto(Integer.parseInt(idsogg));
				tmpTitle = soggetto.getTipo() + "/" + soggetto.getNome();
			}

			PortaDelegata myPD = this.porteDelegateCore.getPortaDelegata(Integer.parseInt(id));
			String idporta = myPD.getNome();

			// setto la barra del titolo
			List<Parameter> lstParam = new ArrayList<Parameter>();

			if(useIdSogg){
				lstParam.add(new Parameter(PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_SOGGETTI, null));
				lstParam.add(new Parameter(Costanti.PAGE_DATA_TITLE_LABEL_ELENCO, SoggettiCostanti.SERVLET_NAME_SOGGETTI_LIST));
				lstParam.add(new Parameter(PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_PORTE_DELEGATE_DI + tmpTitle,
						PorteDelegateCostanti.SERVLET_NAME_PORTE_DELEGATE_LIST,
						new Parameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID_SOGGETTO,idsogg)
						));
			}else {
				lstParam.add(new Parameter(PorteDelegateCostanti.LABEL_PORTE_DELEGATE, null));
				lstParam.add(new Parameter(Costanti.PAGE_DATA_TITLE_LABEL_ELENCO, PorteDelegateCostanti.SERVLET_NAME_PORTE_DELEGATE_LIST));
			}	

			lstParam.add(new Parameter(PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_MESSAGE_SECURITY_DI + idporta,
					PorteDelegateCostanti.SERVLET_NAME_PORTE_DELEGATE_MESSAGE_SECURITY,
					new Parameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID,id),
					new Parameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID_SOGGETTO,idsogg)
					));
			if(search.equals("")){
				this.pd.setSearchDescription("");
				lstParam.add(new Parameter(PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_MESSAGE_SECURITY_REQUEST_FLOW_DI + idporta,null));
			}
			else{
				lstParam.add(new Parameter(PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_MESSAGE_SECURITY_REQUEST_FLOW_DI + idporta,
						PorteDelegateCostanti.SERVLET_NAME_PORTE_DELEGATE_MESSAGE_SECURITY_REQUEST_LIST,
						new Parameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID,id),
						new Parameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID_SOGGETTO,idsogg)
						));
				lstParam.add(new Parameter(PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_RISULTATI_RICERCA, null));
			}


			ServletUtils.setPageDataTitle(this.pd, lstParam.toArray(new Parameter[lstParam.size()]));

			// controllo eventuali risultati ricerca
			if (!search.equals("")) {
				this.pd.setSearch("on");
				this.pd.setSearchDescription("Message-Security request-flow contenenti la stringa '" + search + "'");
			}

			// setto le label delle colonne
			String[] labels = {
					PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_NOME,
					PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_VALORE		
			};
			this.pd.setLabels(labels);

			// preparo i dati
			Vector<Vector<DataElement>> dati = new Vector<Vector<DataElement>>();

			if (lista != null) {
				Iterator<MessageSecurityFlowParameter> it = lista.iterator();
				while (it.hasNext()) {
					MessageSecurityFlowParameter wsrfp = it.next();

					Vector<DataElement> e = new Vector<DataElement>();

					DataElement de = new DataElement();
					de.setUrl(PorteDelegateCostanti.SERVLET_NAME_PORTE_DELEGATE_MESSAGE_SECURITY_REQUEST_CHANGE ,
							new Parameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID,id),
							new Parameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID_SOGGETTO,idsogg),
							new Parameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_NOME, wsrfp.getNome())
							);
					de.setValue(wsrfp.getNome());
					de.setIdToRemove(wsrfp.getNome());
					e.addElement(de);

					de = new DataElement();
					de.setValue(wsrfp.getValore());
					e.addElement(de);

					dati.addElement(e);
				}
			}

			this.pd.setDati(dati);
			this.pd.setAddButton(true);

		} catch (Exception e) {
			this.log.error("Exception: " + e.getMessage(), e);
			throw new Exception(e);
		}
	}

	public void preparePorteDelegateCorrAppRispostaList(String nomePorta, ISearch ricerca, List<CorrelazioneApplicativaRispostaElemento> lista)
			throws Exception {
		try {

			// prelevo il flag che mi dice da quale pagina ho acceduto la sezione delle porte delegate
			Boolean useIdSogg= ServletUtils.getBooleanAttributeFromSession(PorteDelegateCostanti.ATTRIBUTO_PORTE_DELEGATE_USA_ID_SOGGETTO, this.session);


			String id = this.request.getParameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID);
			String idsogg = this.request.getParameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID_SOGGETTO);

			ServletUtils.addListElementIntoSession(this.session, PorteDelegateCostanti.OBJECT_NAME_PORTE_DELEGATE_CORRELAZIONE_APPLICATIVA_RESPONSE,
					new Parameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID, id), 
					new Parameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID_SOGGETTO, idsogg));

			int idLista = Liste.PORTE_DELEGATE_CORRELAZIONE_APPLICATIVA_RISPOSTA;
			int limit = ricerca.getPageSize(idLista);
			int offset = ricerca.getIndexIniziale(idLista);
			String search = ServletUtils.getSearchFromSession(ricerca, idLista);

			this.pd.setIndex(offset);
			this.pd.setPageSize(limit);
			this.pd.setNumEntries(ricerca.getNumEntries(idLista));

			String tmpTitle = null;
			if(this.core.isRegistroServiziLocale()){
				org.openspcoop2.core.registry.Soggetto soggetto = this.soggettiCore.getSoggettoRegistro(Integer.parseInt(idsogg));
				tmpTitle = soggetto.getTipo() + "/" + soggetto.getNome();
			}
			else{
				org.openspcoop2.core.config.Soggetto soggetto = this.soggettiCore.getSoggetto(Integer.parseInt(idsogg));
				tmpTitle = soggetto.getTipo() + "/" + soggetto.getNome();
			}

			PortaDelegata myPD = this.porteDelegateCore.getPortaDelegata(Integer.parseInt(id));
			String idporta = myPD.getNome();

			// setto la barra del titolo
			List<Parameter> lstParam = new ArrayList<Parameter>();

			if(useIdSogg){
				lstParam.add(new Parameter(PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_SOGGETTI, null));
				lstParam.add(new Parameter(Costanti.PAGE_DATA_TITLE_LABEL_ELENCO, SoggettiCostanti.SERVLET_NAME_SOGGETTI_LIST));
				lstParam.add(new Parameter(PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_PORTE_DELEGATE_DI + tmpTitle,
						PorteDelegateCostanti.SERVLET_NAME_PORTE_DELEGATE_LIST,
						new Parameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID_SOGGETTO,idsogg)
						));

			}else{
				lstParam.add(new Parameter(PorteDelegateCostanti.LABEL_PORTE_DELEGATE, null));
				lstParam.add(new Parameter(Costanti.PAGE_DATA_TITLE_LABEL_ELENCO, PorteDelegateCostanti.SERVLET_NAME_PORTE_DELEGATE_LIST));
			}

			if(search.equals("")){
				this.pd.setSearchDescription("");
				lstParam.add(new Parameter(PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_CORRELAZIONI_APPLICATIVE_RISPOSTA_DI + idporta,null));
			}
			else{
				lstParam.add(new Parameter(PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_CORRELAZIONI_APPLICATIVE_RISPOSTA_DI + idporta,
						PorteDelegateCostanti.SERVLET_NAME_PORTE_DELEGATE_CORRELAZIONE_APPLICATIVA_RESPONSE_LIST,
						new Parameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID,id),
						new Parameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID_SOGGETTO,idsogg),
						new Parameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_NOME, idporta)));
				lstParam.add(new Parameter(PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_RISULTATI_RICERCA, null));
			}

			ServletUtils.setPageDataTitle(this.pd, lstParam.toArray(new Parameter[lstParam.size()]));

			// controllo eventuali risultati ricerca
			if (!search.equals("")) {
				this.pd.setSearch("on");
				this.pd.setSearchDescription("Correlazioni Applicative per la risposta contenenti la stringa '" + search + "'");
			}

			// setto le label delle colonne
			String[] labels = { 
					PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_ELEMENTO_XML,
					PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_MODALITA_IDENTIFICAZIONE
			};
			this.pd.setLabels(labels);

			// preparo i dati
			Vector<Vector<DataElement>> dati = new Vector<Vector<DataElement>>();

			if (lista != null) {
				Iterator<CorrelazioneApplicativaRispostaElemento> it = lista.iterator();
				while (it.hasNext()) {
					CorrelazioneApplicativaRispostaElemento cae = it.next();

					Vector<DataElement> e = new Vector<DataElement>();

					DataElement de = new DataElement();
					de.setType(DataElementType.HIDDEN);
					de.setValue("" + cae.getId());
					e.addElement(de);

					de = new DataElement();
					de.setUrl(
							PorteDelegateCostanti.SERVLET_NAME_PORTE_DELEGATE_CORRELAZIONE_APPLICATIVA_RESPONSE_CHANGE,
							new Parameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID,id),
							new Parameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID_SOGGETTO,idsogg),
							new Parameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID_CORRELAZIONE, cae.getId() + "")
							);
					String nomeElemento = "*";
					if (cae.getNome() != null && !"".equals(cae.getNome()))
						nomeElemento = cae.getNome();
					de.setValue(nomeElemento);
					de.setIdToRemove("" + cae.getId());
					e.addElement(de);

					de = new DataElement();
					if(cae.getIdentificazione()!=null)
						de.setValue(cae.getIdentificazione().toString());
					e.addElement(de);

					dati.addElement(e);
				}
			}

			this.pd.setDati(dati);
			this.pd.setAddButton(true);
		} catch (Exception e) {
			this.log.error("Exception: " + e.getMessage(), e);
			throw new Exception(e);
		}
	}

	// Prepara la lista di MTOM request-flow delle porte delegate
	public void preparePorteDelegateMTOMRequestList(String nomePorta, ISearch ricerca, List<MtomProcessorFlowParameter> lista)	throws Exception {
		try {

			// prelevo il flag che mi dice da quale pagina ho acceduto la sezione delle porte delegate
			Boolean useIdSogg= ServletUtils.getBooleanAttributeFromSession(PorteDelegateCostanti.ATTRIBUTO_PORTE_DELEGATE_USA_ID_SOGGETTO, this.session);


			String id = this.request.getParameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID);
			String idsogg = this.request.getParameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID_SOGGETTO);

			ServletUtils.addListElementIntoSession(this.session, PorteDelegateCostanti.OBJECT_NAME_PORTE_DELEGATE_MTOM_REQUEST,
					new Parameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID, id),
					new Parameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID_SOGGETTO, idsogg));

			int idLista = Liste.PORTE_DELEGATE_MTOM_REQUEST;
			int limit = ricerca.getPageSize(idLista);
			int offset = ricerca.getIndexIniziale(idLista);
			String search = ServletUtils.getSearchFromSession(ricerca, idLista);

			this.pd.setIndex(offset);
			this.pd.setPageSize(limit);
			this.pd.setNumEntries(ricerca.getNumEntries(idLista));

			String tmpTitle = null;
			if(this.core.isRegistroServiziLocale()){
				org.openspcoop2.core.registry.Soggetto soggetto = this.soggettiCore.getSoggettoRegistro(Integer.parseInt(idsogg));
				tmpTitle = soggetto.getTipo() + "/" + soggetto.getNome();
			}else{
				org.openspcoop2.core.config.Soggetto soggetto = this.soggettiCore.getSoggetto(Integer.parseInt(idsogg));
				tmpTitle = soggetto.getTipo() + "/" + soggetto.getNome();
			}

			PortaDelegata myPD = this.porteDelegateCore.getPortaDelegata(Integer.parseInt(id));
			String idporta = myPD.getNome();

			// setto la barra del titolo
			List<Parameter> lstParam = new ArrayList<Parameter>();

			if(useIdSogg){
				lstParam.add(new Parameter(PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_SOGGETTI, null));
				lstParam.add(new Parameter(Costanti.PAGE_DATA_TITLE_LABEL_ELENCO, SoggettiCostanti.SERVLET_NAME_SOGGETTI_LIST));
				lstParam.add(new Parameter(PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_PORTE_DELEGATE_DI + tmpTitle,
						PorteDelegateCostanti.SERVLET_NAME_PORTE_DELEGATE_LIST,
						new Parameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID_SOGGETTO,idsogg)
						));
			}else {
				lstParam.add(new Parameter(PorteDelegateCostanti.LABEL_PORTE_DELEGATE, null));
				lstParam.add(new Parameter(Costanti.PAGE_DATA_TITLE_LABEL_ELENCO, PorteDelegateCostanti.SERVLET_NAME_PORTE_DELEGATE_LIST));
			}	

			lstParam.add(new Parameter(PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_MTOM_DI + idporta,
					PorteDelegateCostanti.SERVLET_NAME_PORTE_DELEGATE_MTOM,
					new Parameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID,id),
					new Parameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID_SOGGETTO,idsogg)
					));
			if(search.equals("")){
				this.pd.setSearchDescription("");
				lstParam.add(new Parameter(PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_MTOM_REQUEST_FLOW_DI + idporta,null));
			}
			else{
				lstParam.add(new Parameter(PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_MTOM_REQUEST_FLOW_DI + idporta,
						PorteDelegateCostanti.SERVLET_NAME_PORTE_DELEGATE_MTOM_REQUEST_LIST,
						new Parameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID,id),
						new Parameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID_SOGGETTO,idsogg)
						));
				lstParam.add(new Parameter(PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_RISULTATI_RICERCA, null));
			}


			ServletUtils.setPageDataTitle(this.pd, lstParam.toArray(new Parameter[lstParam.size()]));

			// controllo eventuali risultati ricerca
			if (!search.equals("")) {
				this.pd.setSearch("on");
				this.pd.setSearchDescription("MTOM request-flow contenenti la stringa '" + search + "'");
			}

			// setto le label delle colonne
			String[] labels = {
					PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_NOME,
			};
			this.pd.setLabels(labels);

			// preparo i dati
			Vector<Vector<DataElement>> dati = new Vector<Vector<DataElement>>();

			if (lista != null) {
				Iterator<MtomProcessorFlowParameter> it = lista.iterator();
				while (it.hasNext()) {
					MtomProcessorFlowParameter parametro = it.next();

					Vector<DataElement> e = new Vector<DataElement>();

					DataElement de = new DataElement();
					de.setUrl(PorteDelegateCostanti.SERVLET_NAME_PORTE_DELEGATE_MTOM_REQUEST_CHANGE ,
							new Parameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID,id),
							new Parameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID_SOGGETTO,idsogg),
							new Parameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_NOME, parametro.getNome())
							);
					de.setValue(parametro.getNome());
					de.setIdToRemove(parametro.getNome());
					e.addElement(de);
					dati.addElement(e);
				}
			}

			this.pd.setDati(dati);
			this.pd.setAddButton(true);

		} catch (Exception e) {
			this.log.error("Exception: " + e.getMessage(), e);
			throw new Exception(e);
		}
	}


	// Prepara la lista di MTOM response-flow delle porte delegate
	public void preparePorteDelegateMTOMResponseList(String nomePorta, ISearch ricerca,
			List<MtomProcessorFlowParameter> lista)
					throws Exception {
		try {
			String id = this.request.getParameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID);
			String idsogg = this.request.getParameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID_SOGGETTO);

			// prelevo il flag che mi dice da quale pagina ho acceduto la sezione delle porte delegate
			Boolean useIdSogg= ServletUtils.getBooleanAttributeFromSession(PorteDelegateCostanti.ATTRIBUTO_PORTE_DELEGATE_USA_ID_SOGGETTO, this.session);

			ServletUtils.addListElementIntoSession(this.session, PorteDelegateCostanti.OBJECT_NAME_PORTE_DELEGATE_MTOM_RESPONSE, 
					new Parameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID, id),
					new Parameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID_SOGGETTO, idsogg));

			int idLista = Liste.PORTE_DELEGATE_MTOM_RESPONSE;
			int limit = ricerca.getPageSize(idLista);
			int offset = ricerca.getIndexIniziale(idLista);
			String search = ServletUtils.getSearchFromSession(ricerca, idLista);

			this.pd.setIndex(offset);
			this.pd.setPageSize(limit);
			this.pd.setNumEntries(ricerca.getNumEntries(idLista));

			String tmpTitle = null;
			if(this.core.isRegistroServiziLocale()){
				org.openspcoop2.core.registry.Soggetto soggetto = this.soggettiCore.getSoggettoRegistro(Integer.parseInt(idsogg));
				tmpTitle = soggetto.getTipo() + "/" + soggetto.getNome();
			}else{
				org.openspcoop2.core.config.Soggetto soggetto = this.soggettiCore.getSoggetto(Integer.parseInt(idsogg));
				tmpTitle = soggetto.getTipo() + "/" + soggetto.getNome();
			}

			PortaDelegata myPD = this.porteDelegateCore.getPortaDelegata(Integer.parseInt(id));
			String idporta = myPD.getNome();

			// setto la barra del titolo
			List<Parameter> lstParam = new ArrayList<Parameter>();
			if(useIdSogg){
				lstParam.add(new Parameter(PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_SOGGETTI, null));
				lstParam.add(new Parameter(Costanti.PAGE_DATA_TITLE_LABEL_ELENCO, SoggettiCostanti.SERVLET_NAME_SOGGETTI_LIST));
				lstParam.add(new Parameter(PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_PORTE_DELEGATE_DI + tmpTitle,
						PorteDelegateCostanti.SERVLET_NAME_PORTE_DELEGATE_LIST,
						new Parameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID_SOGGETTO,idsogg)
						));

			}else {
				lstParam.add(new Parameter(PorteDelegateCostanti.LABEL_PORTE_DELEGATE, null));
				lstParam.add(new Parameter(Costanti.PAGE_DATA_TITLE_LABEL_ELENCO, PorteDelegateCostanti.SERVLET_NAME_PORTE_DELEGATE_LIST));
			}


			lstParam.add(new Parameter(PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_MTOM_DI + idporta,
					PorteDelegateCostanti.SERVLET_NAME_PORTE_DELEGATE_MTOM,
					new Parameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID,id),
					new Parameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID_SOGGETTO,idsogg)
					));

			if(search.equals("")){
				this.pd.setSearchDescription("");
				lstParam.add(new Parameter(PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_MTOM_RESPONSE_FLOW_DI + idporta,null));
			}
			else{
				lstParam.add(new Parameter(PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_MTOM_RESPONSE_FLOW_DI + idporta,
						PorteDelegateCostanti.SERVLET_NAME_PORTE_DELEGATE_MTOM_RESPONSE_LIST,
						new Parameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID,id),
						new Parameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID_SOGGETTO,idsogg)
						));
				lstParam.add(new Parameter(PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_RISULTATI_RICERCA, null));

			}

			ServletUtils.setPageDataTitle(this.pd, lstParam.toArray(new Parameter[lstParam.size()]));

			// controllo eventuali risultati ricerca
			if (!search.equals("")) {
				this.pd.setSearch("on");
				this.pd.setSearchDescription("MTOM response-flow contenenti la stringa '" + search + "'");
			}

			// setto le label delle colonne
			String[] labels = { 
					PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_NOME,
			};
			this.pd.setLabels(labels);

			// preparo i dati
			Vector<Vector<DataElement>> dati = new Vector<Vector<DataElement>>();

			if (lista != null) {
				Iterator<MtomProcessorFlowParameter> it = lista.iterator();
				while (it.hasNext()) {
					MtomProcessorFlowParameter wsrfp = it.next();

					Vector<DataElement> e = new Vector<DataElement>();

					DataElement de = new DataElement();
					de.setUrl(PorteDelegateCostanti.SERVLET_NAME_PORTE_DELEGATE_MTOM_RESPONSE_CHANGE,
							new Parameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID,id),
							new Parameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID_SOGGETTO,idsogg),
							new Parameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_NOME, wsrfp.getNome())
							);
					de.setValue(wsrfp.getNome());
					de.setIdToRemove(wsrfp.getNome());
					e.addElement(de);

					dati.addElement(e);
				}
			}

			this.pd.setDati(dati);
			this.pd.setAddButton(true);

		} catch (Exception e) {
			this.log.error("Exception: " + e.getMessage(), e);
			throw new Exception(e);
		}
	}

}
