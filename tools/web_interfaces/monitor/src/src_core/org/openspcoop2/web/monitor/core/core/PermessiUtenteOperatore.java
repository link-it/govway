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
package org.openspcoop2.web.monitor.core.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.openspcoop2.core.commons.CoreException;
import org.openspcoop2.core.id.IDServizio;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.generic_project.beans.IField;
import org.openspcoop2.generic_project.dao.IExpressionConstructor;
import org.openspcoop2.generic_project.expression.IExpression;

import org.openspcoop2.monitor.engine.constants.Costanti;
import org.openspcoop2.web.monitor.core.bean.UserDetailsBean;

/**
 * PermessiUtenteOperatore
 * 
 * @author Pintori Giuliano (pintori@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 *
 */
public class PermessiUtenteOperatore {

	public static boolean CHECK_UNIQUE_SOGGETTO_SERVIZIO_UTENTE = false;

	private List<IDServizio> listIDServizi = new ArrayList<IDServizio>();
	private List<IDSoggetto> listIDSoggetti = new ArrayList<IDSoggetto>();

	public void addSoggetto(IDSoggetto idSoggetto) throws CoreException{
		for (IDSoggetto idSoggettoCheck : this.listIDSoggetti) {
			if(idSoggettoCheck.equals(idSoggetto)){
				throw new CoreException("Soggetto ["+idSoggetto+"] già aggiunto");
			}
		}
		if(CHECK_UNIQUE_SOGGETTO_SERVIZIO_UTENTE){
			for (IDServizio idServizio : this.listIDServizi) {
				if(idServizio.getSoggettoErogatore().equals(idSoggetto)){
					throw new CoreException("Soggetto ["+idSoggetto+"] non può essere aggiunto come filtro singolo poichè già esistono dei filtri sui servizi da lui erogati");
				}
			}
		}
		this.listIDSoggetti.add(idSoggetto);
	}

	public void addServizio(IDServizio idServizio) throws CoreException{
		for (IDServizio idServizioCheck : this.listIDServizi) {
			if(idServizioCheck.equals(idServizio)){
				throw new CoreException("Servizio ["+idServizio+"] già aggiunto");
			}
		}
		if(CHECK_UNIQUE_SOGGETTO_SERVIZIO_UTENTE){
			for (IDSoggetto idSoggetto : this.listIDSoggetti) {
				if(idSoggetto.equals(idServizio.getSoggettoErogatore())){
					throw new CoreException("Servizio ["+idServizio+"] non può essere aggiunto poichè esiste già un filtro generale sul soggetto erogatore valido per tutti i servizi");
				}
			}
		}
		this.listIDServizi.add(idServizio);
	}

	public void addPermessi(PermessiUtenteOperatore p) throws CoreException{
		for (IDSoggetto idSoggetto : p.listIDSoggetti) {
			this.addSoggetto(idSoggetto);
		}
		for (IDServizio idServizio : p.listIDServizi) {
			this.addServizio(idServizio);
		}
	}
	public void addPermessiSoggetti(PermessiUtenteOperatore p) throws CoreException{
		for (IDSoggetto idSoggetto : p.listIDSoggetti) {
			this.addSoggetto(idSoggetto);
		}
	}
	public void addPermessiServizi(PermessiUtenteOperatore p) throws CoreException{
		for (IDServizio idServizio : p.listIDServizi) {
			this.addServizio(idServizio);
		}
	}

	public List<IDServizio> getListIDServizi() {
		return this.listIDServizi;
	}

	public List<IDSoggetto> getListIDSoggetti() {
		return this.listIDSoggetti;
	}


	@Override
	public String toString(){
		StringBuilder bf = new StringBuilder();
		if(this.listIDSoggetti.size()>0){
			for (IDSoggetto idSoggetto : this.listIDSoggetti) {
				if(bf.length()>0){
					bf.append(",");
				}
				bf.append(idSoggetto.toString());
			}
		}
		if(this.listIDServizi.size()>0){
			for (IDServizio idServizio : this.listIDServizi) {
				if(bf.length()>0){
					bf.append(",");
				}
				bf.append(idServizio.toString());
			}
		}
		return bf.toString();
	}

	public IExpression toExpression(IExpressionConstructor exprConstructor,IField fieldIdentificativoPorta,
			IField fieldTipoSoggettoErogatore,IField fieldNomeSoggettoErogatore,IField fieldTipoServizio,IField fieldNomeServizio, IField fieldVersioneServizio) throws CoreException{
		boolean includeTransazioniSenzaIdentificativoPorta = false;
		// Oramai non ha più senso, tutte le transazioni vengono sempre valorizzate con un codice porta, al massimo contengono quello di default della PdD
		// E' rimasta la condizione nel codice della query sottostante, ma non dovrebbe avere piu effetto.
		return toExpression(exprConstructor, fieldIdentificativoPorta, 
				fieldTipoSoggettoErogatore, fieldNomeSoggettoErogatore, fieldTipoServizio, fieldNomeServizio, fieldVersioneServizio,
				includeTransazioniSenzaIdentificativoPorta);
	}
	public IExpression toExpression(IExpressionConstructor exprConstructor,IField fieldIdentificativoPorta,
			IField fieldTipoSoggettoErogatore,IField fieldNomeSoggettoErogatore,IField fieldTipoServizio,IField fieldNomeServizio, IField fieldVersioneServizio,
			boolean includeTransazioniSenzaIdentificativoPorta) throws CoreException{
		try{
			IExpression expr = exprConstructor.newExpression();
			expr.or();

			// per gli errori limite (es. pd non trovata)
			if(includeTransazioniSenzaIdentificativoPorta){
				expr.isNull(fieldIdentificativoPorta); // transazioni
				expr.equals(fieldIdentificativoPorta,Costanti.INFORMAZIONE_NON_DISPONIBILE); // statistiche
			}

			if(this.listIDSoggetti.size()>0){
				// I Soggetti rappresentano coloro che gestiscono la transazione, sia che siano fruizioni che erogazioni
				List<String> identificativiPorta = new ArrayList<String>();
				for (IDSoggetto idSoggetto : this.listIDSoggetti) {
					identificativiPorta.add(idSoggetto.getCodicePorta());
				}
				expr.in(fieldIdentificativoPorta, identificativiPorta);
			}	

			if(this.listIDServizi.size()>0){
				// I Servizi invece rappresentano proprio un servizio specifico (identificato dalla quadrupla tipo/nome servizio e tipo/nome soggetto)
				// indipendentemente dal fatto se il soggetto erogatore è un soggetto operativo (che quindi ha emesso la transazione e ha l'identificativo porta) o meno
				List<IExpression> servizi = new ArrayList<IExpression>();
				for (IDServizio idServizio : this.listIDServizi) {
					IExpression exprServ = exprConstructor.newExpression();
					exprServ.and();
					exprServ.isNotNull(fieldTipoSoggettoErogatore);
					exprServ.equals(fieldTipoSoggettoErogatore, idServizio.getSoggettoErogatore().getTipo());
					exprServ.isNotNull(fieldNomeSoggettoErogatore);
					exprServ.equals(fieldNomeSoggettoErogatore, idServizio.getSoggettoErogatore().getNome());
					exprServ.isNotNull(fieldTipoServizio);
					exprServ.equals(fieldTipoServizio, idServizio.getTipo());
					exprServ.isNotNull(fieldNomeServizio);
					exprServ.equals(fieldNomeServizio, idServizio.getNome());
					exprServ.isNotNull(fieldVersioneServizio);
					exprServ.equals(fieldVersioneServizio, idServizio.getVersione());
					servizi.add(exprServ);
				}
				expr.or(servizi.toArray(new IExpression[1]));
			}

			return expr;
		}catch(Exception e){
			throw new CoreException(e.getMessage(),e);
		}
	}

	public IExpression toExpressionAllarmi(IExpressionConstructor exprConstructor,
			IField fieldTipoMittente,IField fieldMittente,
			IField fieldTipoDestinatario,IField fieldDestinatario,
			IField fieldTipoServizio,IField fieldNomeServizio, IField fieldVersioneServizio ) throws CoreException{
		try{

			IExpression expr = exprConstructor.newExpression();
			expr.or();

			if(this.listIDSoggetti.size()>0){

				// utility
				IExpression mittenteQualsiasiExpr = exprConstructor.newExpression();
				mittenteQualsiasiExpr.and();
				mittenteQualsiasiExpr.isNull(fieldTipoMittente);
				mittenteQualsiasiExpr.isNull(fieldMittente);

				IExpression destinatarioQualsiasiExpr = exprConstructor.newExpression();
				destinatarioQualsiasiExpr.and();
				destinatarioQualsiasiExpr.isNull(fieldTipoDestinatario);
				destinatarioQualsiasiExpr.isNull(fieldDestinatario);

				

				// gli allarmi che possono vedere sono:
				// mittente '*' e destinatario uno dei soggetti associati all'utenza
				// mittente uno dei soggetti associati all'utenza e destinatario '*'
				// mittente e destinatario associati all'utenza

				IExpression soggettiGestioneExpr = exprConstructor.newExpression();				
				soggettiGestioneExpr.or();



				// mittente '*' e destinatario uno dei soggetti associati all'utenza

				IExpression destinatarioExpr = exprConstructor.newExpression();
				destinatarioExpr.or();
				for (IDSoggetto idSoggetto : this.listIDSoggetti) {
					Map<IField, Object> destSoggetto = new HashMap<IField, Object>();
					destSoggetto.put(fieldTipoDestinatario,idSoggetto.getTipo());
					destSoggetto.put(fieldDestinatario,idSoggetto.getNome());
					destinatarioExpr.allEquals(destSoggetto);
				}
				
				soggettiGestioneExpr.and(mittenteQualsiasiExpr,destinatarioExpr);



				// mittente uno dei soggetti associati all'utenza e destinatario '*'

				IExpression mittenteExpr = exprConstructor.newExpression();
				mittenteExpr.or();
				for (IDSoggetto idSoggetto : this.listIDSoggetti) {
					Map<IField, Object> mittSoggetto = new HashMap<IField, Object>();
					mittSoggetto.put(fieldTipoMittente,idSoggetto.getTipo());
					mittSoggetto.put(fieldMittente,idSoggetto.getNome());
					mittenteExpr.allEquals(mittSoggetto);
				}

				soggettiGestioneExpr.and(destinatarioQualsiasiExpr,mittenteExpr);



				// mittente e destinatario associati all'utenza

				soggettiGestioneExpr.and(mittenteExpr,destinatarioExpr);


				expr.or(soggettiGestioneExpr);
			}

			if(this.listIDServizi.size()>0){

				// I Servizi invece rappresentano proprio un servizio specifico (identificato dalla quadrupla tipo/nome servizio e tipo/nome soggetto)

				List<IExpression> servizi = new ArrayList<IExpression>();
				for (IDServizio idServizio : this.listIDServizi) {
					IExpression exprServ = exprConstructor.newExpression();
					exprServ.and();
					exprServ.isNotNull(fieldTipoDestinatario);
					exprServ.equals(fieldTipoDestinatario, idServizio.getSoggettoErogatore().getTipo());
					exprServ.isNotNull(fieldDestinatario);
					exprServ.equals(fieldDestinatario, idServizio.getSoggettoErogatore().getNome());
					exprServ.isNotNull(fieldTipoServizio);
					exprServ.equals(fieldTipoServizio, idServizio.getTipo());
					exprServ.isNotNull(fieldNomeServizio);
					exprServ.equals(fieldNomeServizio, idServizio.getNome());
					exprServ.isNotNull(fieldVersioneServizio);
					exprServ.equals(fieldVersioneServizio, idServizio.getVersione());
					servizi.add(exprServ);
				}
				expr.or(servizi.toArray(new IExpression[1]));
			}

			return expr;
		}catch(Exception e){
			throw new CoreException(e.getMessage(),e);
		}
	}

	public IExpression toExpressionConfigurazioneServizi(IExpressionConstructor exprConstructor,IField fieldTipoSoggetto,IField fieldNomeSoggetto,
			IField fieldTipoSoggettoErogatore,IField fieldNomeSoggettoErogatore,IField fieldTipoServizio,IField fieldNomeServizio, IField fieldVersioneServizio,
			boolean setNotNull) throws CoreException{
		try{
			IExpression expr = exprConstructor.newExpression();
			expr.or();

			if(this.listIDSoggetti.size()>0){
				// I Soggetti rappresentano coloro che gestiscono la transazione, sia che siano fruizioni che erogazioni
				List<IExpression> soggetti = new ArrayList<IExpression>();
				for (IDSoggetto idSoggetto : this.listIDSoggetti) {
					IExpression exprSogg = exprConstructor.newExpression();
					exprSogg.and();
					if(setNotNull){
						exprSogg.isNotNull(fieldTipoSoggetto);
					}
					exprSogg.equals(fieldTipoSoggetto, idSoggetto.getTipo());
					if(setNotNull){
						exprSogg.isNotNull(fieldNomeSoggetto);
					}
					exprSogg.equals(fieldNomeSoggetto, idSoggetto.getNome());
					soggetti.add(exprSogg);
				}
				if(soggetti.size()>0)
					expr.or(soggetti.toArray(new IExpression[1]));
			}	

			if(this.listIDServizi.size()>0){
				// I Servizi invece rappresentano proprio un servizio specifico (identificato dalla quadrupla tipo/nome servizio e tipo/nome soggetto)
				// indipendentemente dal fatto se il soggetto erogatore è un soggetto operativo (che quindi ha emesso la transazione e ha l'identificativo porta) o meno
				List<IExpression> servizi = new ArrayList<IExpression>();
				for (IDServizio idServizio : this.listIDServizi) {
					IExpression exprServ = exprConstructor.newExpression();
					exprServ.and();
					if(setNotNull){
						exprServ.isNotNull(fieldTipoSoggettoErogatore);
					}
					exprServ.equals(fieldTipoSoggettoErogatore, idServizio.getSoggettoErogatore().getTipo());
					if(setNotNull){
						exprServ.isNotNull(fieldNomeSoggettoErogatore);
					}
					exprServ.equals(fieldNomeSoggettoErogatore, idServizio.getSoggettoErogatore().getNome());
					if(setNotNull){
						exprServ.isNotNull(fieldTipoServizio);
					}
					exprServ.equals(fieldTipoServizio, idServizio.getTipo());
					if(setNotNull){
						exprServ.isNotNull(fieldNomeServizio);
					}
					exprServ.equals(fieldNomeServizio, idServizio.getNome());
					if(setNotNull){
						exprServ.isNotNull(fieldVersioneServizio);
					}
					exprServ.equals(fieldVersioneServizio, idServizio.getVersione());
					servizi.add(exprServ);
				}
				if(servizi.size()>0)
					expr.or(servizi.toArray(new IExpression[1]));
			}

			return expr;
		}catch(Exception e){
			throw new CoreException(e.getMessage(),e);
		}
	}

	public static PermessiUtenteOperatore getPermessiUtenteOperatore(UserDetailsBean u,
			String tipoSoggettoLocale, String nomeSoggettoLocale)
					throws CoreException {

		if (u.isAdmin()) {
			// vuota: l'utente può vedere tutto. E' un amministratore
			return null;
		}

		if (StringUtils.isNotEmpty(nomeSoggettoLocale) ){

			// L'invocazione serve solamente a verificare i permessi
			// Poi gli oggetti ritornati DEVONO corrispondere esattamente ai permessi impostati sul database
			@SuppressWarnings("unused")
			PermessiUtenteOperatore soggLocale = PermessiUtenteOperatore.getPermessiUtenteOperatore(u, tipoSoggettoLocale, nomeSoggettoLocale, null, null,null);
			@SuppressWarnings("unused")
			PermessiUtenteOperatore servizio = PermessiUtenteOperatore.getPermessiUtenteOperatore(u, null, null, null, null,null);
			//			soggLocale.addPermessiServizi(servizio);
			//			return soggLocale;
			//			
		}
		//		else{
		return PermessiUtenteOperatore.getPermessiUtenteOperatore(u, null, null, null, null,null);
		//		}

	}

	@SuppressWarnings("deprecation")
	public static PermessiUtenteOperatore getPermessiUtenteOperatore(UserDetailsBean u,
			String tipoSoggettoLocale, String nomeSoggettoLocale, 
			String tipoServizio, String nomeServizio, Integer versioneServizio)
					throws CoreException {

		// Controllo sul soggetto indicato
		if (StringUtils.isNotEmpty(nomeSoggettoLocale) && StringUtils.isEmpty(nomeServizio) ) {		
			// Se ho selezionato un soggetto locale controllo che il valore inserito sia effettivamente un soggetto gestito dall'utente, 
			// altrimenti segnalo l'errore
			// il controllo vale solo se l'utente non e' admin
			if(!u.isAdmin()){
				boolean found = false;
				for (IDSoggetto utenteSoggetto : u.getUtenteSoggettoList()) {
					if(utenteSoggetto.getTipo().equals(tipoSoggettoLocale) &&
							utenteSoggetto.getNome().equals(nomeSoggettoLocale)){						
						found = true;
						break;
					}
				}
				if(!found){
					throw new CoreException("L'utente '"+u.getUsername()+"' non ha i diritti necessari per esaminare le informazioni diagnostiche del soggetto "+tipoSoggettoLocale+"/"+nomeSoggettoLocale);
				}
			}		
		}

		// Controllo sul servizio indicato
		if (StringUtils.isNotEmpty(nomeServizio) ) {		
			// Se ho selezionato un servizio controllo che il valore inserito sia effettivamente uno dei servizi permessi all'utente, 
			// altrimenti segnalo l'errore
			// il controllo vale solo se l'utente non e' admin
			if(!u.isAdmin()){
				boolean found = false;
				if (StringUtils.isNotEmpty(nomeSoggettoLocale) ) {
					for (IDServizio idServizio : u.getUtenteServizioList()) {
						if(idServizio.getSoggettoErogatore().getTipo().equals(tipoSoggettoLocale) &&
								idServizio.getSoggettoErogatore().getNome().equals(nomeSoggettoLocale) &&
								idServizio.getTipo().equals(tipoServizio) &&
								idServizio.getNome().equals(nomeServizio) &&
								idServizio.getVersione().intValue() == versioneServizio.intValue()){		
							found = true;
							break;
						}
					}
					if(!found){
						throw new CoreException("L'utente '"+u.getUsername()+"' non ha i diritti necessari per esaminare le informazioni diagnostiche del servizio "+tipoServizio+"/"+nomeServizio+
								" erogato dal soggetto "+tipoSoggettoLocale+"/"+nomeSoggettoLocale);
					}
				}
				else{
					throw new CoreException("Utilizzo del metodo non valido. Se viene indicato il servizio deve anche essere indicato il soggeto erogatore");
				}
			}		
		}


		// Ricerca puntuale dei permessi riguardanti il soggettoLocale o il servizio indicato
		if (StringUtils.isNotEmpty(nomeSoggettoLocale) || StringUtils.isNotEmpty(nomeServizio) ) {		

			// Recupero identificativoPorta se è stato selezionato un soggetto locale (o un servizio)
			String idPorta = Utility.getIdentificativoPorta(tipoSoggettoLocale, nomeSoggettoLocale);

			// Creazione dei permessi
			PermessiUtenteOperatore permessi = new PermessiUtenteOperatore();
			IDSoggetto idSoggetto = new IDSoggetto(tipoSoggettoLocale,nomeSoggettoLocale,idPorta);

			if(StringUtils.isNotEmpty(nomeServizio)){
				IDServizio idServizio = new IDServizio();
				idServizio.setSoggettoErogatore(idSoggetto);
				idServizio.setTipo(tipoServizio);
				idServizio.setNome(nomeServizio); 
				idServizio.setVersione(versioneServizio); 
				permessi.addServizio(idServizio);
			}
			else{
				permessi.addSoggetto(idSoggetto);
			}			
			return permessi;
		}


		// Ricerca in base a ciò che l'utente può vedere rispetto ai suoi soggetto o servizi

		if (u.isAdmin()) {
			// vuota: l'utente può vedere tutto. E' un amministratore
			return null;
		} else {

			PermessiUtenteOperatore permessi = new PermessiUtenteOperatore();
			
			for (IDSoggetto idSoggetto  : u.getUtenteSoggettoList()) {
				permessi.addSoggetto(idSoggetto);
			}

			for (IDServizio idServizio : u.getUtenteServizioList()) {
					permessi.addServizio(idServizio);
			}

			return permessi;
		}
	}



}
