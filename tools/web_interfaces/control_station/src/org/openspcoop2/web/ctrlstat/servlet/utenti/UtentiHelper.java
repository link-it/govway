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
package org.openspcoop2.web.ctrlstat.servlet.utenti;

import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.openspcoop2.core.commons.ISearch;
import org.openspcoop2.core.commons.Liste;
import org.openspcoop2.web.ctrlstat.servlet.ConsoleHelper;
import org.openspcoop2.web.ctrlstat.servlet.config.ConfigurazioneCostanti;
import org.openspcoop2.web.ctrlstat.servlet.login.LoginCostanti;
import org.openspcoop2.web.lib.mvc.Costanti;
import org.openspcoop2.web.lib.mvc.DataElement;
import org.openspcoop2.web.lib.mvc.DataElementType;
import org.openspcoop2.web.lib.mvc.PageData;
import org.openspcoop2.web.lib.mvc.Parameter;
import org.openspcoop2.web.lib.mvc.ServletUtils;
import org.openspcoop2.web.lib.mvc.TipoOperazione;
import org.openspcoop2.web.lib.users.dao.InterfaceType;
import org.openspcoop2.web.lib.users.dao.Permessi;
import org.openspcoop2.web.lib.users.dao.PermessiUtente;
import org.openspcoop2.web.lib.users.dao.User;

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

	public Vector<DataElement> addUtentiToDati(Vector<DataElement> dati,TipoOperazione tipoOperazione,boolean singlePdD,
			String nomesu,String pwsu,String confpwsu,InterfaceType interfaceType,
			String isServizi,String isDiagnostica,String isSistema,String isMessaggi,String isUtenti,String isAuditing, String isAccordiCooperazione,
			String changepwd){

		DataElement de = new DataElement();
		de.setLabel(UtentiCostanti.LABEL_INFORMAZIONI_UTENTE);
		de.setType(DataElementType.TITLE);
		dati.addElement(de);

		de = new DataElement();
		de.setLabel(UtentiCostanti.LABEL_PARAMETRO_UTENTI_USERNAME);
		de.setValue(nomesu);
		if(TipoOperazione.ADD.equals(tipoOperazione)){
			de.setType(DataElementType.TEXT_EDIT);
		}
		else{
			de.setType(DataElementType.TEXT);
		}
		de.setName(UtentiCostanti.PARAMETRO_UTENTI_USERNAME);
		de.setSize(this.getSize());
		de.setRequired(true);
		dati.addElement(de);

		de = new DataElement();
		de.setLabel(UtentiCostanti.LABEL_PASSWORD);
		de.setType(DataElementType.TITLE);
		dati.addElement(de);

		if(TipoOperazione.CHANGE.equals(tipoOperazione)){
			de = new DataElement();
			de.setLabel(UtentiCostanti.LABEL_MODIFICA);
			de.setType(DataElementType.CHECKBOX);
			//			de.setOnClick("cambiaPassword(\"suChange\");");
			de.setName(UtentiCostanti.PARAMETRO_UTENTI_CHANGE_PASSWORD);
			de.setPostBack(true);
			de.setSelected(changepwd);
			de.setSize(this.getSize());
			dati.addElement(de);
		}

		if( (TipoOperazione.ADD.equals(tipoOperazione)) || (ServletUtils.isCheckBoxEnabled(changepwd)) ){

			de = new DataElement();
			de.setLabel(UtentiCostanti.LABEL_PARAMETRO_UTENTI_PASSWORD);
			de.setValue(pwsu);
			de.setType(DataElementType.CRYPT);
			de.setName(UtentiCostanti.PARAMETRO_UTENTI_PASSWORD);
			de.setSize(this.getSize());
			de.setRequired(true);
			dati.addElement(de);

			de = new DataElement();
			de.setLabel(UtentiCostanti.LABEL_PARAMETRO_UTENTI_CONFERMA_PASSWORD);
			de.setValue(confpwsu);
			de.setType(DataElementType.CRYPT);
			de.setName(UtentiCostanti.PARAMETRO_UTENTI_CONFERMA_PASSWORD);
			de.setSize(this.getSize());
			de.setRequired(true);
			dati.addElement(de);
		}

		de = new DataElement();
		de.setLabel(UtentiCostanti.LABEL_MODALITA_INTERFACCIA);
		de.setType(DataElementType.TITLE);
		dati.addElement(de);

		de = new DataElement();
		de.setLabel(UtentiCostanti.LABEL_PARAMETRO_UTENTI_TIPO_GUI);
		de.setType(DataElementType.SELECT);
		de.setName(UtentiCostanti.PARAMETRO_UTENTI_TIPO_GUI);
		String[] tipiInterfacce={InterfaceType.STANDARD.toString(),InterfaceType.AVANZATA.toString()};
		String[] tipiInterfacceLabel={InterfaceType.STANDARD.toString().toLowerCase(),InterfaceType.AVANZATA.toString().toLowerCase()};
		de.setValues(tipiInterfacce);
		de.setLabels(tipiInterfacceLabel);
		de.setSelected(interfaceType.toString());
		dati.addElement(de);

		de = new DataElement();
		de.setLabel(UtentiCostanti.LABEL_PERMESSI_GESTIONE);
		de.setType(DataElementType.TITLE);
		dati.addElement(de);

		de = new DataElement();
		de.setLabel(UtentiCostanti.LABEL_PARAMETRO_UTENTI_IS_SERVIZI);
		de.setType(DataElementType.CHECKBOX);
		de.setName(UtentiCostanti.PARAMETRO_UTENTI_IS_SERVIZI);
		ServletUtils.setCheckBox(de, isServizi);
		dati.addElement(de);
		
		de = new DataElement();
		de.setLabel(UtentiCostanti.LABEL_PARAMETRO_UTENTI_IS_ACCORDI_COOPERAZIONE);
		de.setType(DataElementType.CHECKBOX);
		de.setName(UtentiCostanti.PARAMETRO_UTENTI_IS_ACCORDI_COOPERAZIONE);
		ServletUtils.setCheckBox(de, isAccordiCooperazione);
		dati.addElement(de);

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
		dati.addElement(de);

		de = new DataElement();
		de.setLabel(UtentiCostanti.LABEL_PARAMETRO_UTENTI_IS_SISTEMA);
		de.setType(DataElementType.CHECKBOX);
		de.setName(UtentiCostanti.PARAMETRO_UTENTI_IS_SISTEMA);
		ServletUtils.setCheckBox(de, isSistema);
		dati.addElement(de);

		de = new DataElement();
		de.setLabel(UtentiCostanti.LABEL_PARAMETRO_UTENTI_IS_MESSAGGI);
		de.setType(DataElementType.CHECKBOX);
		de.setName(UtentiCostanti.PARAMETRO_UTENTI_IS_MESSAGGI);
		ServletUtils.setCheckBox(de, isMessaggi);
		dati.addElement(de);

		de = new DataElement();
		de.setLabel(UtentiCostanti.LABEL_PARAMETRO_UTENTI_IS_UTENTI);
		de.setType(DataElementType.CHECKBOX);
		de.setName(UtentiCostanti.PARAMETRO_UTENTI_IS_UTENTI);
		ServletUtils.setCheckBox(de, isUtenti);
		dati.addElement(de);

		de = new DataElement();
		de.setLabel(UtentiCostanti.LABEL_PARAMETRO_UTENTI_IS_AUDITING);
		de.setType(DataElementType.CHECKBOX);
		de.setName(UtentiCostanti.PARAMETRO_UTENTI_IS_AUDITING);
		ServletUtils.setCheckBox(de, isAuditing);
		dati.addElement(de);

	

		return dati;

	}


	public void addChangeUtenteInfoToDati(Vector<DataElement> dati,
			String nomesu,String changepwd,String pwsu,String confpwsu,InterfaceType interfaceType,
			String isServizi,String isDiagnostica,String isSistema,String isMessaggi,String isUtenti,String isAuditing, String isAccordiCooperazione,
			boolean scegliSuServizi,
			String [] uws, boolean scegliSuAccordi,String [] uwp){

		DataElement de = new DataElement();
		de.setLabel(UtentiCostanti.LABEL_PARAMETRO_UTENTI_USERNAME);
		de.setValue(nomesu);
		de.setType(DataElementType.HIDDEN);
		de.setName(UtentiCostanti.PARAMETRO_UTENTI_USERNAME);
		dati.addElement(de);

		if(ServletUtils.isCheckBoxEnabled(changepwd)){

			de = new DataElement();
			de.setLabel(UtentiCostanti.LABEL_PARAMETRO_UTENTI_PASSWORD);
			de.setValue(pwsu);
			de.setType(DataElementType.HIDDEN);
			de.setName(UtentiCostanti.PARAMETRO_UTENTI_PASSWORD);
			dati.addElement(de);

			de = new DataElement();
			de.setLabel(UtentiCostanti.LABEL_PARAMETRO_UTENTI_CONFERMA_PASSWORD);
			de.setValue(confpwsu);
			de.setType(DataElementType.HIDDEN);
			de.setName(UtentiCostanti.PARAMETRO_UTENTI_CONFERMA_PASSWORD);
			dati.addElement(de);

			de = new DataElement();
			de.setLabel(UtentiCostanti.LABEL_PARAMETRO_UTENTI_CHANGE_PASSWORD);
			de.setValue(changepwd);
			de.setType(DataElementType.HIDDEN);
			de.setName(UtentiCostanti.PARAMETRO_UTENTI_CHANGE_PASSWORD);
			dati.addElement(de);
		}


		de = new DataElement();
		de.setLabel(UtentiCostanti.LABEL_PARAMETRO_UTENTI_TIPO_GUI);
		de.setValue(interfaceType.toString());
		de.setType(DataElementType.HIDDEN);
		de.setName(UtentiCostanti.PARAMETRO_UTENTI_TIPO_GUI);
		dati.addElement(de);

		de = new DataElement();
		de.setLabel(UtentiCostanti.LABEL_PARAMETRO_UTENTI_IS_SERVIZI);
		de.setValue(isServizi);
		de.setType(DataElementType.HIDDEN);
		de.setName(UtentiCostanti.PARAMETRO_UTENTI_IS_SERVIZI);
		dati.addElement(de);

		de = new DataElement();
		de.setLabel(UtentiCostanti.LABEL_PARAMETRO_UTENTI_IS_DIAGNOSTICA);
		de.setValue(isDiagnostica);
		de.setType(DataElementType.HIDDEN);
		de.setName(UtentiCostanti.PARAMETRO_UTENTI_IS_DIAGNOSTICA);
		dati.addElement(de);

		de = new DataElement();
		de.setLabel(UtentiCostanti.LABEL_PARAMETRO_UTENTI_IS_SISTEMA);
		de.setValue(isSistema);
		de.setType(DataElementType.HIDDEN);
		de.setName(UtentiCostanti.PARAMETRO_UTENTI_IS_SISTEMA);
		dati.addElement(de);

		de = new DataElement();
		de.setLabel(UtentiCostanti.LABEL_PARAMETRO_UTENTI_IS_MESSAGGI);
		de.setValue(isMessaggi);
		de.setType(DataElementType.HIDDEN);
		de.setName(UtentiCostanti.PARAMETRO_UTENTI_IS_MESSAGGI);
		dati.addElement(de);

		de = new DataElement();
		de.setLabel(UtentiCostanti.LABEL_PARAMETRO_UTENTI_IS_UTENTI);
		de.setValue(isUtenti);
		de.setType(DataElementType.HIDDEN);
		de.setName(UtentiCostanti.PARAMETRO_UTENTI_IS_UTENTI);
		dati.addElement(de);

		de = new DataElement();
		de.setLabel(UtentiCostanti.LABEL_PARAMETRO_UTENTI_IS_AUDITING);
		de.setValue(isAuditing);
		de.setType(DataElementType.HIDDEN);
		de.setName(UtentiCostanti.PARAMETRO_UTENTI_IS_AUDITING);
		dati.addElement(de);

		de = new DataElement();
		de.setLabel(UtentiCostanti.LABEL_PARAMETRO_UTENTI_IS_ACCORDI_COOPERAZIONE);
		de.setType(DataElementType.HIDDEN);
		de.setName(UtentiCostanti.PARAMETRO_UTENTI_IS_ACCORDI_COOPERAZIONE);
		de.setValue(isAccordiCooperazione);
		dati.addElement(de);

		if(scegliSuServizi){
			de = new DataElement();
			de.setLabel(UtentiCostanti.LABEL_UTENTE_PERMESSI_SERVIZI);
			de.setType(DataElementType.SELECT);
			de.setName(UtentiCostanti.PARAMETRO_UTENTI_SINGLE_SU_SERVIZI);
			de.setValues(uws);
			dati.addElement(de);
		}

		if(scegliSuAccordi){
			de = new DataElement();
			de.setLabel(UtentiCostanti.LABEL_UTENTE_PERMESSI_ACCORDI_COOPERAZIONE);
			de.setType(DataElementType.SELECT);
			de.setName(UtentiCostanti.PARAMETRO_UTENTI_SINGLE_SU_ACCORDI_COOPERAZIONE);
			de.setValues(uwp);
			dati.addElement(de);
		}
	}


	public void addChooseUtenteForPermessiSToDati(Vector<DataElement> dati,String objToRemove,boolean scegliSuServizi,
			String [] uws, boolean scegliSuAccordi,String [] uwp){

		DataElement de = new DataElement();
		de.setLabel(Costanti.PARAMETER_NAME_OBJECTS_FOR_REMOVE);
		de.setValue(objToRemove);
		de.setType(DataElementType.HIDDEN);
		de.setName(Costanti.PARAMETER_NAME_OBJECTS_FOR_REMOVE);
		dati.addElement(de);

		if(scegliSuServizi){
			de = new DataElement();
			de.setLabel(UtentiCostanti.LABEL_UTENTE_PERMESSI_SERVIZI);
			de.setType(DataElementType.SELECT);
			de.setName(UtentiCostanti.PARAMETRO_UTENTI_SINGLE_SU_SERVIZI);
			de.setValues(uws);
			dati.addElement(de);
		}

		if(scegliSuAccordi){
			de = new DataElement();
			de.setLabel(UtentiCostanti.LABEL_UTENTE_PERMESSI_ACCORDI_COOPERAZIONE);
			de.setType(DataElementType.SELECT);
			de.setName(UtentiCostanti.PARAMETRO_UTENTI_SINGLE_SU_ACCORDI_COOPERAZIONE);
			de.setValues(uwp);
			dati.addElement(de);
		}



	}

	public void addUtenteChangeToDato(Vector<DataElement> dati,InterfaceType interfaceType,
			String changepw){

		DataElement de = new DataElement();

		de = new DataElement();
		de.setLabel(UtentiCostanti.LABEL_MODALITA_INTERFACCIA);
		de.setType(DataElementType.TITLE);
		dati.addElement(de);

		de = new DataElement();
		de.setLabel(UtentiCostanti.LABEL_PARAMETRO_UTENTI_TIPO_GUI);
		de.setType(DataElementType.SELECT);
		de.setName(UtentiCostanti.PARAMETRO_UTENTI_TIPO_GUI);
		String[] tipiInterfacce={InterfaceType.STANDARD.toString(),InterfaceType.AVANZATA.toString()};
		String[] tipiInterfacceLabel={InterfaceType.STANDARD.toString().toLowerCase(),InterfaceType.AVANZATA.toString().toLowerCase()};
		de.setValues(tipiInterfacce);
		de.setLabels(tipiInterfacceLabel);
		de.setSelected(interfaceType.toString());
		dati.addElement(de);


		de = new DataElement();
		de.setLabel(UtentiCostanti.LABEL_PASSWORD);
		de.setType(DataElementType.TITLE);
		dati.addElement(de);

		de = new DataElement();
		de.setLabel(UtentiCostanti.LABEL_MODIFICA);
		de.setType(DataElementType.CHECKBOX);
		//		de.setOnClick("cambiaPassword(\"changePwd\");");
		de.setName(UtentiCostanti.PARAMETRO_UTENTI_CHANGE_PASSWORD);
		de.setPostBack(true);
		de.setSelected(changepw);
		de.setSize(this.getSize());
		dati.addElement(de);


		//se e' stato selezionato il link per il cambio password allora mostro i dati
		if(ServletUtils.isCheckBoxEnabled(changepw)){

			de = new DataElement();
			de.setLabel(UtentiCostanti.LABEL_PARAMETRO_UTENTE_VECCHIA_PASSWORD);
			de.setType(DataElementType.CRYPT);
			de.setName(UtentiCostanti.PARAMETRO_UTENTE_VECCHIA_PASSWORD);
			de.setValue("");
			de.setSize(this.getSize());
			de.setRequired(true);
			dati.addElement(de);

			de = new DataElement();
			de.setLabel(UtentiCostanti.LABEL_PARAMETRO_UTENTE_NUOVA_PASSWORD);
			de.setType(DataElementType.CRYPT);
			de.setName(UtentiCostanti.PARAMETRO_UTENTE_NUOVA_PASSWORD);
			de.setSize(this.getSize());
			de.setValue("");
			de.setRequired(true);
			dati.addElement(de);

			de = new DataElement();
			de.setLabel(UtentiCostanti.LABEL_PARAMETRO_UTENTE_CONFERMA_NUOVA_PASSWORD);
			de.setType(DataElementType.CRYPT);
			de.setName(UtentiCostanti.PARAMETRO_UTENTE_CONFERMA_NUOVA_PASSWORD);
			de.setSize(this.getSize());
			de.setValue("");
			de.setRequired(true);
			dati.addElement(de);
		}

		de = new DataElement();
		de.setType(DataElementType.HIDDEN);
		de.setName(UtentiCostanti.PARAMETRO_UTENTE_ESEGUI);
		de.setValue(UtentiCostanti.PARAMETRO_UTENTE_ESEGUI);
		dati.addElement(de);
	}


	public boolean utentiCheckData(TipoOperazione tipoOperazione,boolean singlePdD) throws Exception {
		try {
			String nomesu = this.request.getParameter(UtentiCostanti.PARAMETRO_UTENTI_USERNAME);
			String pwsu = this.request.getParameter(UtentiCostanti.PARAMETRO_UTENTI_PASSWORD);
			String confpwsu = this.request.getParameter(UtentiCostanti.PARAMETRO_UTENTI_CONFERMA_PASSWORD);
			String tipoGui = this.request.getParameter(UtentiCostanti.PARAMETRO_UTENTI_TIPO_GUI);
			String isServizi = this.request.getParameter(UtentiCostanti.PARAMETRO_UTENTI_IS_SERVIZI);
			String isDiagnostica = this.request.getParameter(UtentiCostanti.PARAMETRO_UTENTI_IS_DIAGNOSTICA);
			String isSistema = this.request.getParameter(UtentiCostanti.PARAMETRO_UTENTI_IS_SISTEMA);
			String isMessaggi = this.request.getParameter(UtentiCostanti.PARAMETRO_UTENTI_IS_MESSAGGI);
			String isUtenti = this.request.getParameter(UtentiCostanti.PARAMETRO_UTENTI_IS_UTENTI);
			String isAccordiCooperazione = this.request.getParameter(UtentiCostanti.PARAMETRO_UTENTI_IS_ACCORDI_COOPERAZIONE);
			String isAuditing = this.request.getParameter(UtentiCostanti.PARAMETRO_UTENTI_IS_AUDITING);
			String changepwd = this.request.getParameter(UtentiCostanti.PARAMETRO_UTENTI_CHANGE_PASSWORD);


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
				if(checkPassword){
					if (pwsu.equals("")) {
						if (tmpElenco.equals("")) {
							tmpElenco = UtentiCostanti.LABEL_PARAMETRO_UTENTI_PASSWORD;
						} else {
							tmpElenco = tmpElenco + ", "+UtentiCostanti.LABEL_PARAMETRO_UTENTI_PASSWORD;
						}
					}
					if (confpwsu.equals("")) {
						if (tmpElenco.equals("")) {
							tmpElenco = UtentiCostanti.LABEL_PARAMETRO_UTENTI_CONFERMA_PASSWORD;
						} else {
							tmpElenco = tmpElenco + ", "+UtentiCostanti.LABEL_PARAMETRO_UTENTI_CONFERMA_PASSWORD;
						}
					}
				}
				if(!tmpElenco.equals("")){
					this.pd.setMessage("Dati incompleti. E' necessario indicare: " + tmpElenco);
					return false;
				}
			}

			// Controllo che non ci siano spazi nei campi di testo
			if ((nomesu.indexOf(" ") != -1) || ( ServletUtils.isCheckBoxEnabled(changepwd) && ( (pwsu.indexOf(" ") != -1) || (confpwsu.indexOf(" ") != -1)))) {
				this.pd.setMessage("Non inserire spazi nei campi di testo");
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

			// Controllo che i campi "select" abbiano uno dei valori ammessi
			if (!tipoGui.equals(InterfaceType.AVANZATA.toString()) && !tipoGui.equals(InterfaceType.STANDARD.toString())) {
				this.pd.setMessage("Tipo dev'essere " + InterfaceType.AVANZATA.toString() + " o " + InterfaceType.STANDARD.toString());
				return false;
			}

			// Controllo che le password corrispondano
			boolean checkPassword = true;
			if(TipoOperazione.CHANGE.equals(tipoOperazione) && !ServletUtils.isCheckBoxEnabled(changepwd)){
				//if( confpwsu.equals("") && pwsu.equals("") ){
				//	checkPassword = false;
				//}
				checkPassword=false;
			}
			if (checkPassword && !pwsu.equals(confpwsu)) {
				this.pd.setMessage("Le password non corrispondono");
				return false;
			}

			// Almeno un permesso dev'essere selezionato
			if (((isServizi == null) || !ServletUtils.isCheckBoxEnabled(isServizi)) &&
					(!singlePdD || (singlePdD && ((isDiagnostica == null) || !ServletUtils.isCheckBoxEnabled(isDiagnostica)))) &&
					((isSistema == null) || !ServletUtils.isCheckBoxEnabled(isSistema)) &&
					((isMessaggi == null) || !ServletUtils.isCheckBoxEnabled(isMessaggi)) &&
					((isUtenti == null) || !ServletUtils.isCheckBoxEnabled(isUtenti)) &&
					((isAuditing == null) || !ServletUtils.isCheckBoxEnabled(isAuditing)) &&
					((isAccordiCooperazione == null) || !ServletUtils.isCheckBoxEnabled(isAccordiCooperazione))) {
				this.pd.setMessage("Selezionare almeno un Permesso");
				return false;
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

			String oldpw = this.request.getParameter(UtentiCostanti.PARAMETRO_UTENTE_VECCHIA_PASSWORD);
			String newpw = this.request.getParameter(UtentiCostanti.PARAMETRO_UTENTE_NUOVA_PASSWORD);
			String confpw = this.request.getParameter(UtentiCostanti.PARAMETRO_UTENTE_CONFERMA_NUOVA_PASSWORD);
			String changepwd = this.request.getParameter(UtentiCostanti.PARAMETRO_UTENTE_CHANGE_PASSWORD);

			if (!ServletUtils.isCheckBoxEnabled(changepwd)) {
				// non si vuole cambiare le pwd
				return true;
			}

			// String cpwd = this.procToCall.cryptPw(oldpw);
			User user = ServletUtils.getUserFromSession(this.session);
			// if(!cpwd.equals(user.getPassword())){
			if (!this.passwordManager.checkPw(oldpw, user.getPassword())) {
				this.pd.setMessage("La vecchia password indicata non e' corretta");
				return false;
			}

			// Campi obbligatori
			if (newpw.equals("") || confpw.equals("")) {
				this.pd.setMessage("Dati incompleti. E' necessario indicare una password");
				return false;
			}

			// Controllo che non ci siano spazi nei campi di testo
			if ((newpw.indexOf(" ") != -1) || (confpw.indexOf(" ") != -1)) {
				this.pd.setMessage("Non inserire spazi nei campi di testo");
				return false;
			}

			// Controllo che le password corrispondano
			if (!newpw.equals(confpw)) {
				this.pd.setMessage("Le due password non corrispondono!");
				return false;
			}

			// La password dev'essere lunga almeno 5 caratteri
			if (newpw.length() < 5) {
				this.pd.setMessage("Inserire una password di almeno 5 caratteri");
				return false;
			}

			return true;

		} catch (Exception e) {
			this.log.error("Exception: " + e.getMessage(), e);
			throw new Exception(e);
		}
	}

	public void prepareUtentiList(ISearch ricerca, List<User> lista, boolean singlePdD) throws Exception {
		try {
			ServletUtils.addListElementIntoSession(this.session, UtentiCostanti.OBJECT_NAME_UTENTI);

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
						new Parameter(ConfigurazioneCostanti.LABEL_CONFIGURAZIONE, null),
						new Parameter(UtentiCostanti.LABEL_UTENTI, null),
						new Parameter(Costanti.PAGE_DATA_TITLE_LABEL_ELENCO,null));
			}
			else{
				ServletUtils.setPageDataTitle(this.pd,
						new Parameter(ConfigurazioneCostanti.LABEL_CONFIGURAZIONE, null),
						new Parameter(UtentiCostanti.LABEL_UTENTI, null),
						new Parameter(Costanti.PAGE_DATA_TITLE_LABEL_ELENCO, UtentiCostanti.SERVLET_NAME_UTENTI_LIST),
						new Parameter(Costanti.PAGE_DATA_TITLE_LABEL_RISULTATI_RICERCA, null));	
			}

			// controllo eventuali risultati ricerca
			if (!search.equals("")) {
				ServletUtils.enabledPageDataSearch(this.pd, UtentiCostanti.LABEL_UTENTI, search);
			}

			// setto le label delle colonne
			String[] labels = { UtentiCostanti.LABEL_UTENTE, UtentiCostanti.LABEL_MODALITA_INTERFACCIA, 
					UtentiCostanti.LABEL_PERMESSI_GESTIONE, UtentiCostanti.LABEL_CAMBIA_IDENTITA };
			this.pd.setLabels(labels);

			// preparo i dati
			Vector<Vector<DataElement>> dati = new Vector<Vector<DataElement>>();

			if (lista != null) {
				Iterator<User> it = lista.iterator();
				while (it.hasNext()) {
					User mySU = it.next();

					Vector<DataElement> e = new Vector<DataElement>();

					DataElement de = new DataElement();
					de.setUrl(UtentiCostanti.SERVLET_NAME_UTENTI_CHANGE,
							new Parameter(UtentiCostanti.PARAMETRO_UTENTI_USERNAME, mySU.getLogin()));
					de.setIdToRemove(mySU.getId().toString());
					de.setValue(mySU.getLogin());
					e.addElement(de);

					de = new DataElement();
					if(InterfaceType.AVANZATA.toString().equals(mySU.getInterfaceType().toString())){
						de.setValue(InterfaceType.AVANZATA.toString().toLowerCase());
					}else{
						de.setValue(InterfaceType.STANDARD.toString().toLowerCase());
					}
					e.addElement(de);

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
					e.addElement(de);

					de = new DataElement();
					if (!userLogin.equals(mySU.getLogin())) {
						de.setUrl(LoginCostanti.SERVLET_NAME_LOGIN_AS_SU,
								new Parameter(LoginCostanti.PARAMETRO_LOGIN_LOGIN, mySU.getLogin()));
						de.setValue(UtentiCostanti.LABEL_ACCEDI);
					} else
						de.setValue("");
					e.addElement(de);

					dati.addElement(e);
				}
			}

			this.pd.setDati(dati);
			this.pd.setAddButton(this.core.isShowPulsanteAggiungiElenchi());
		} catch (Exception e) {
			this.log.error("Exception: " + e.getMessage(), e);
			throw new Exception(e);
		}
	}
}
