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
package org.openspcoop2.core.config.rs.server.api.impl.applicativi;

import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.stream.Collectors;

import org.openspcoop2.core.config.Credenziali;
import org.openspcoop2.core.config.InvocazioneCredenziali;
import org.openspcoop2.core.config.InvocazionePorta;
import org.openspcoop2.core.config.InvocazionePortaGestioneErrore;
import org.openspcoop2.core.config.InvocazioneServizio;
import org.openspcoop2.core.config.PortaApplicativa;
import org.openspcoop2.core.config.PortaApplicativaServizioApplicativo;
import org.openspcoop2.core.config.PortaDelegata;
import org.openspcoop2.core.config.PortaDelegataServizioApplicativo;
import org.openspcoop2.core.config.RispostaAsincrona;
import org.openspcoop2.core.config.Ruolo;
import org.openspcoop2.core.config.ServizioApplicativo;
import org.openspcoop2.core.config.ServizioApplicativoRuoli;
import org.openspcoop2.core.config.constants.CostantiConfigurazione;
import org.openspcoop2.core.config.constants.FaultIntegrazioneTipo;
import org.openspcoop2.core.config.constants.InvocazioneServizioTipoAutenticazione;
import org.openspcoop2.core.config.constants.StatoFunzionalita;
import org.openspcoop2.core.config.constants.TipologiaErogazione;
import org.openspcoop2.core.config.constants.TipologiaFruizione;
import org.openspcoop2.core.config.driver.DriverConfigurazioneException;
import org.openspcoop2.core.config.driver.DriverConfigurazioneNotFound;
import org.openspcoop2.core.config.driver.FiltroRicercaPorteDelegate;
import org.openspcoop2.core.config.rs.server.api.impl.Helper;
import org.openspcoop2.core.config.rs.server.api.impl.HttpRequestWrapper;
import org.openspcoop2.core.config.rs.server.config.ServerProperties;
import org.openspcoop2.core.config.rs.server.model.Applicativo;
import org.openspcoop2.core.config.rs.server.model.ApplicativoItem;
import org.openspcoop2.core.config.rs.server.model.ModalitaAccessoEnum;
import org.openspcoop2.core.config.rs.server.model.OneOfBaseCredenzialiCredenziali;
import org.openspcoop2.core.id.IDPortaDelegata;
import org.openspcoop2.core.id.IDServizioApplicativo;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.core.registry.Soggetto;
import org.openspcoop2.core.registry.constants.RuoloContesto;
import org.openspcoop2.core.registry.constants.RuoloTipologia;
import org.openspcoop2.core.registry.driver.DriverRegistroServiziException;
import org.openspcoop2.core.registry.driver.DriverRegistroServiziNotFound;
import org.openspcoop2.core.registry.driver.FiltroRicercaRuoli;
import org.openspcoop2.protocol.engine.ProtocolFactoryManager;
import org.openspcoop2.protocol.sdk.ProtocolException;
import org.openspcoop2.utils.UtilsException;
import org.openspcoop2.utils.regexp.RegularExpressionEngine;
import org.openspcoop2.utils.service.beans.ProfiloEnum;
import org.openspcoop2.utils.service.beans.utils.BaseHelper;
import org.openspcoop2.utils.service.fault.jaxrs.FaultCode;
import org.openspcoop2.web.ctrlstat.core.ControlStationCore;
import org.openspcoop2.web.ctrlstat.servlet.ConsoleHelper;
import org.openspcoop2.web.ctrlstat.servlet.connettori.ConnettoriCostanti;
import org.openspcoop2.web.ctrlstat.servlet.pd.PorteDelegateCore;
import org.openspcoop2.web.ctrlstat.servlet.sa.ServiziApplicativiCore;
import org.openspcoop2.web.ctrlstat.servlet.sa.ServiziApplicativiCostanti;
import org.openspcoop2.web.ctrlstat.servlet.soggetti.SoggettiCore;
import org.openspcoop2.web.lib.mvc.TipoOperazione;

/**
 * ApplicativiApiHelper
 * 
 * @author $Author$
 * @version $Rev$, $Date$
 * 
 */
public class ApplicativiApiHelper {
	

	public static void overrideSAParameters(HttpRequestWrapper wrap, ConsoleHelper consoleHelper, ServizioApplicativo sa, Applicativo applicativo) {
		Credenziali credenziali = sa.getInvocazionePorta().getCredenziali(0);
		
		wrap.overrideParameter(ServiziApplicativiCostanti.PARAMETRO_SERVIZI_APPLICATIVI_NOME, sa.getNome());
		wrap.overrideParameter(ServiziApplicativiCostanti.PARAMETRO_SERVIZI_APPLICATIVI_PROVIDER, sa.getIdSoggetto());
		wrap.overrideParameter(ServiziApplicativiCostanti.PARAMETRO_SERVIZI_APPLICATIVI_FAULT, ServiziApplicativiCostanti.SERVIZI_APPLICATIVI_FAULT_SOAP);
		
		wrap.overrideParameter(ConnettoriCostanti.PARAMETRO_CREDENZIALI_TIPO_AUTENTICAZIONE, credenziali.getTipo().toString());
		
		if(applicativo.getCredenziali()!=null) {
			Helper.overrideAuthParams(wrap, consoleHelper, applicativo.getCredenziali());
		}
	}
    
	
	public static ServizioApplicativo applicativoToServizioApplicativo(
			Applicativo applicativo,
			String tipo_protocollo,
			String soggetto,
			ControlStationCore stationCore) throws UtilsException, Exception {

		ServerProperties serverProperties = ServerProperties.getInstance();
	
		
		ControlStationCore core = new ControlStationCore(true, serverProperties.getConfDirectory(),tipo_protocollo); 
		SoggettiCore soggettiCore = new SoggettiCore(core);	

		String tipo_soggetto = ProtocolFactoryManager.getInstance().getDefaultOrganizationTypes().get(tipo_protocollo);
		IDSoggetto idSoggetto = new IDSoggetto(tipo_soggetto,soggetto);
		Soggetto soggettoRegistro = soggettiCore.getSoggettoRegistro(idSoggetto);
		
	
		//soggettoRegistro.get
	    ServizioApplicativo sa = new ServizioApplicativo();
	
	    sa.setNome(applicativo.getNome());
	    sa.setTipologiaFruizione(TipologiaFruizione.NORMALE.getValue());
	    sa.setTipo(CostantiConfigurazione.CLIENT);
		sa.setTipologiaErogazione(TipologiaErogazione.DISABILITATO.getValue());	
		
	    //Inseriamo il soggetto del registro locale
	    sa.setIdSoggetto(soggettoRegistro.getId());
	    sa.setNomeSoggettoProprietario(soggettoRegistro.getNome());
	    sa.setTipoSoggettoProprietario(soggettoRegistro.getTipo());	    
	    
	    // *** risposta asinc ***
		InvocazioneCredenziali credenzialiInvocazione = new InvocazioneCredenziali();
		credenzialiInvocazione.setUser("");
		credenzialiInvocazione.setPassword("");
		
		RispostaAsincrona rispostaAsinc = new RispostaAsincrona();
		rispostaAsinc.setAutenticazione(InvocazioneServizioTipoAutenticazione.NONE);
		rispostaAsinc.setCredenziali(credenzialiInvocazione);
		rispostaAsinc.setGetMessage(CostantiConfigurazione.DISABILITATO);
	
		sa.setRispostaAsincrona(rispostaAsinc);
		
		InvocazioneServizio invServizio = new InvocazioneServizio();
		invServizio.setAutenticazione(InvocazioneServizioTipoAutenticazione.NONE);
		invServizio.setCredenziali(credenzialiInvocazione);
		invServizio.setGetMessage(CostantiConfigurazione.DISABILITATO);
		
		sa.setInvocazioneServizio(invServizio);
		
		// *** Invocazione Porta ***
		InvocazionePorta invocazionePorta = new InvocazionePorta();
		Credenziali credenziali = credenzialiFromAuth(applicativo.getCredenziali());

		invocazionePorta.addCredenziali(credenziali);
		
	    //Imposto i ruoli
		FiltroRicercaRuoli filtroRuoli = new FiltroRicercaRuoli();
		filtroRuoli.setContesto(RuoloContesto.QUALSIASI); // gli applicativi possono essere usati anche nelle erogazioni.
		filtroRuoli.setTipologia(RuoloTipologia.INTERNO);
	
		List<String> allRuoli = stationCore.getAllRuoli(filtroRuoli);
		final ServizioApplicativoRuoli ruoli = invocazionePorta.getRuoli() == null ? new ServizioApplicativoRuoli() : invocazionePorta.getRuoli();
		
		if (applicativo.getRuoli() != null) {
			applicativo.getRuoli().forEach( nome -> {
				
				if (!allRuoli.contains(nome)) {
					throw FaultCode.RICHIESTA_NON_VALIDA.toException("Il ruolo di nome " + nome + " non è presente o assegnabile al servizio applicativo.");
				}
				Ruolo r = new Ruolo();
				r.setNome(nome);
				ruoli.addRuolo(r);
			});
		}
		invocazionePorta.setRuoli(ruoli);
			
		final String fault = ServiziApplicativiCostanti.SERVIZI_APPLICATIVI_FAULT_SOAP;
		
		InvocazionePortaGestioneErrore ipge = new InvocazionePortaGestioneErrore();
		ipge.setFault(FaultIntegrazioneTipo.toEnumConstant(fault));
		invocazionePorta.setGestioneErrore(ipge);
		
		invocazionePorta.setSbustamentoInformazioniProtocollo(StatoFunzionalita.toEnumConstant(""));

		sa.setInvocazionePorta(invocazionePorta);
		
	    return sa;
	}
	
	
	public static final Applicativo servizioApplicativoToApplicativo(ServizioApplicativo sa) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		Applicativo ret = new Applicativo();
		
		ret.setNome(sa.getNome());

		
		InvocazionePorta invPorta = sa.getInvocazionePorta();
		if (invPorta != null) {
			ServizioApplicativoRuoli ruoli = invPorta.getRuoli();
			if (ruoli != null) {
				ret.setRuoli(ruoli.getRuoloList().stream().map( ruolo -> ruolo.getNome().toString() ).collect(Collectors.toList()));
			}
		}
	
		
		Credenziali cred = invPorta.getCredenziali(0);
		ret.setCredenziali(authFromCredenziali(cred));
			
		return ret;
	}
	
	// Rationale: Questa funzione è bene che sia unchecked. Se non esplicitamente catturata infatti, comporterà in ogni caso lato API
	// la segnalazione di un errore interno.
	//
	// Il fatto che abbiamo creato nel sistema un oggetto di tipo ServizioApplicativo ritenuto corretto (il parametro)
	// ma per il quale non si riesce più a recuperare, mmmh.
	//	+ Il tipo del soggetto propietario
	//	+ Il tipo del protocollo associato al tipo del soggeto
	//  + Il servizio protocolli stesso
	//
	// Denotano un errore interno.
	//
	// è una funzione che effettua un mapping da un tipo all'altro e che potrà essere utilizzata liberamente negli stream.
	
	public static final ApplicativoItem servizioApplicativoToApplicativoItem(ServizioApplicativo sa) {
		ApplicativoItem ret = new ApplicativoItem();
		
		ret.setNome(sa.getNome());
		
		String tipo_protocollo = null;
		
		try {
			tipo_protocollo = ProtocolFactoryManager.getInstance().getProtocolByOrganizationType(sa.getTipoSoggettoProprietario());
		} catch (ProtocolException e) {
		
			e.printStackTrace();
			throw new RuntimeException(e);
		}		
		
		ret.setProfilo(BaseHelper.profiloFromTipoProtocollo.get(tipo_protocollo));
		ret.setSoggetto(sa.getNomeSoggettoProprietario());
		
		ServizioApplicativoRuoli saRoles = null;
		if(sa.getInvocazionePorta()!=null) {
			saRoles = sa.getInvocazionePorta().getRuoli();
		}
		if (saRoles == null) {
			ret.setCountRuoli(0);
		} 
		else {
			ret.setCountRuoli(saRoles.sizeRuoloList());
		}
		
		return ret;
	}
	
	
	
	public static final IDSoggetto getIDSoggetto(String nome, String tipo_protocollo) throws ProtocolException {
		String tipo_soggetto = ProtocolFactoryManager.getInstance().getDefaultOrganizationTypes().get(tipo_protocollo);
		return  new IDSoggetto(tipo_soggetto,nome);
	}
	
	public static final Soggetto getSoggetto(String nome, ProfiloEnum modalita, SoggettiCore soggettiCore) throws DriverRegistroServiziException, DriverRegistroServiziNotFound, ProtocolException {
		return soggettiCore.getSoggettoRegistro(getIDSoggetto(nome,BaseHelper.tipoProtocolloFromProfilo.get(modalita)));

	}
			
	
	/**
	 * Swagger passa le credenziali come una linkedHashMap.
	 * Dobbiamo trasformarla nei relativi oggetti di autenticazione.
	 * 
	 * TODO: Rimuovere questa versione e utilizzare quella in RestApiHelper.
	 * 
	 * @param applicativo
	 * @return
	 * @throws Exception
	 */
	public static OneOfBaseCredenzialiCredenziali translateCredenzialiApplicativo(Applicativo applicativo, boolean create) {
		OneOfBaseCredenzialiCredenziali creds = null;
		
		if(applicativo.getCredenziali()==null || applicativo.getCredenziali().getModalitaAccesso()==null) {
			return null;
		}
		
		String  tipoauthSA = Helper.tipoAuthSAFromModalita.get(applicativo.getCredenziali().getModalitaAccesso().toString());
		
		if (tipoauthSA == null)
			throw FaultCode.RICHIESTA_NON_VALIDA.toException("Tipo modalità accesso sconosciuto: " + applicativo.getCredenziali().getModalitaAccesso());
		
		if (tipoauthSA.equals(ConnettoriCostanti.AUTENTICAZIONE_TIPO_BASIC) ||
				tipoauthSA.equals(ConnettoriCostanti.AUTENTICAZIONE_TIPO_PRINCIPAL) ||
				tipoauthSA.equals(ConnettoriCostanti.AUTENTICAZIONE_TIPO_SSL)) {
			creds = Helper.translateCredenziali(applicativo.getCredenziali(), create);
		}
		else if (tipoauthSA.equals(ConnettoriCostanti.AUTENTICAZIONE_TIPO_NESSUNA)) {
			creds = null;
		}
		else {
			throw FaultCode.RICHIESTA_NON_VALIDA.toException("Tipo autenticazione sconosciuto: " + tipoauthSA);
		}

		return creds;
	}
	
	
	/**
	 * Trasforma le credenziali di autenticazione di un servizio applicativo nelle credenziali
	 * di un'InvocazionePorta.
	 * @throws UtilsException 
	 * @throws InstantiationException 
	 * @throws InvocationTargetException 
	 * @throws IllegalAccessException 
	 * 
	 */
	public static Credenziali credenzialiFromAuth(OneOfBaseCredenzialiCredenziali cred) throws IllegalAccessException, InvocationTargetException, InstantiationException, UtilsException {
		
		Credenziali credenziali = null;
		
		if(cred!=null) {
		
			ModalitaAccessoEnum modalitaAccesso = cred.getModalitaAccesso();
			if(modalitaAccesso == null) {
				throw FaultCode.RICHIESTA_NON_VALIDA.toException("Modalità di accesso delle credenziali non indicata");
			}
			
			String tipoauthSA = Helper.tipoAuthSAFromModalita.get(modalitaAccesso.toString());
			
			credenziali = new Credenziali();
			if (tipoauthSA.equals(ConnettoriCostanti.AUTENTICAZIONE_TIPO_NESSUNA)) {
				credenziali.setTipo(null);
			}
			else {
			
				credenziali = Helper.apiCredenzialiToGovwayCred(
						cred,
						modalitaAccesso,
						Credenziali.class,
						org.openspcoop2.core.config.constants.CredenzialeTipo.class
						);	
			
			}
			
		}
		
		return credenziali;
	}
	
	/**
	 * Trasforma le credenziali per un'InvocazionePorta nelle credenziali conservate in un applicativo.
	 * 
	 * @param cred
	 * @return
	 * @throws NoSuchMethodException 
	 * @throws InvocationTargetException 
	 * @throws IllegalAccessException 
	 */
	public static OneOfBaseCredenzialiCredenziali authFromCredenziali(Credenziali cred) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		
		return Helper.govwayCredenzialiToApi(
				cred,
				Credenziali.class,
				org.openspcoop2.core.config.constants.CredenzialeTipo.class);

	}
	
	
	public static void checkApplicativoName(String nome) throws Exception {

		if (nome.equals(""))		
			throw FaultCode.RICHIESTA_NON_VALIDA.toException(
					"Dati incompleti. E' necessario indicare: " + ServiziApplicativiCostanti.LABEL_PARAMETRO_SERVIZI_APPLICATIVI_NOME
			);
					
		if (nome.indexOf(" ") != -1 || nome.indexOf('\"') != -1) {
			throw FaultCode.RICHIESTA_NON_VALIDA.toException("Non inserire spazi o doppi apici nei campi di testo");
		}
		
		checkIntegrationEntityName(nome);
		checkLength255(nome, ServiziApplicativiCostanti.LABEL_PARAMETRO_SERVIZI_APPLICATIVI_NOME);
	}
	
	
	public static boolean isApplicativoDuplicato(ServizioApplicativo sa, ServiziApplicativiCore saCore) throws DriverConfigurazioneException {

		IDServizioApplicativo idSA = new IDServizioApplicativo();
		IDSoggetto idSoggetto = new IDSoggetto(sa.getTipoSoggettoProprietario(), sa.getNomeSoggettoProprietario());
		idSA.setIdSoggettoProprietario(idSoggetto);
		idSA.setNome(sa.getNome());
		
		return saCore.existsServizioApplicativo(idSA);
	}
	
	
	public static void checkIntegrationEntityName(String name) throws Exception{
		// Il nome deve contenere solo lettere e numeri e '_' '-' '.' '/'
		if (!RegularExpressionEngine.isMatch(name,"^[_A-Za-z][\\-\\._/A-Za-z0-9]*$")) {
			
			throw FaultCode.RICHIESTA_NON_VALIDA.toException(
					"Il campo '" + 
					 ServiziApplicativiCostanti.LABEL_PARAMETRO_SERVIZI_APPLICATIVI_NOME + 
					 "' può iniziare solo con un carattere [A-Za-z] o il simbolo '_' e dev'essere formato solo da caratteri, cifre, '_' , '-'");
		}
	}
	
	
	public static void checkLength255(String value, String object) {
		checkLength(value, object, -1, 255);
	}
	
	
	public static void checkLength(String value, String object, int minLength, int maxLength) {
		
		boolean error = false;
		
		if(minLength>0) {
			if(value==null || value.length()<minLength) {
				error = true;
			}
		}
		if(maxLength>0) {
			if(value!=null && value.length()>maxLength) {
				error = true;
			}
		}
		
		if (error) {
			throw FaultCode.RICHIESTA_NON_VALIDA.toException(
					"L'informazione fornita nel campo " +
					object + 
					" deve essere compresa fa " + Integer.toString(minLength) + " e " + Integer.toString(maxLength) +" caratteri"
			);
		}
			
	}

	
	public static final void checkNoDuplicateCred(
			ServizioApplicativo sa,
			List<ServizioApplicativo> saConflicts,
			SoggettiCore soggettiCore,
			TipoOperazione tipoOperazione ) throws DriverRegistroServiziNotFound, DriverRegistroServiziException 
	{		
		Soggetto soggettoToCheck = soggettiCore.getSoggettoRegistro(sa.getIdSoggetto());
		String portaDominio = soggettoToCheck.getPortaDominio();
		
		for (int i = 0; i < saConflicts.size(); i++) {
			ServizioApplicativo saConflict = saConflicts.get(i);

			// controllo se soggetto appartiene a nal diversi, in tal caso e' possibile avere stesse credenziali
			Soggetto tmpSoggettoProprietarioSa = soggettiCore.getSoggettoRegistro(saConflict.getIdSoggetto());
			if (!portaDominio.equals(tmpSoggettoProprietarioSa.getPortaDominio()))
				continue;
			
			if (tipoOperazione == TipoOperazione.CHANGE && sa.getNome().equals(saConflict.getNome()) && (sa.getIdSoggetto() == saConflict.getIdSoggetto())) {
				continue;
			}
			
			throw FaultCode.RICHIESTA_NON_VALIDA.toException("Esistono gia' altri servizi applicativi che possiedono le credenziali indicate.");
		}
		
	}
	
	public static final ServizioApplicativo getServizioApplicativo(String nome, String soggetto, String tipo_protocollo, ServiziApplicativiCore saCore) throws ProtocolException, DriverConfigurazioneException  { 
		IDServizioApplicativo idServizioApplicativo = new IDServizioApplicativo();
		idServizioApplicativo.setIdSoggettoProprietario(ApplicativiApiHelper.getIDSoggetto(soggetto, tipo_protocollo));
		idServizioApplicativo.setNome(nome);
		
		return saCore.getServizioApplicativo(idServizioApplicativo);
	}


	public static List<IDPortaDelegata> getIdPorteDelegate(ServizioApplicativo oldSa, PorteDelegateCore pCore) throws DriverConfigurazioneException {
		FiltroRicercaPorteDelegate filtro = new FiltroRicercaPorteDelegate();
		filtro.setTipoSoggetto(oldSa.getTipoSoggettoProprietario());
		filtro.setNomeSoggetto(oldSa.getNomeSoggettoProprietario());
		filtro.setNomeServizioApplicativo(oldSa.getNome());
		return pCore.getAllIdPorteDelegate(filtro);
		
	}


	public static void checkServizioApplicativoInUso(ServizioApplicativo oldSa, SoggettiCore soggetiCore) throws DriverConfigurazioneNotFound, DriverConfigurazioneException {
		
		org.openspcoop2.core.config.Soggetto oldSogg = soggetiCore.getSoggetto(oldSa.getIdSoggetto());
		String nomeProv = oldSogg.getTipo() + "/" + oldSogg.getNome();

		boolean servizioApplicativoInUso = false;
		
		for (int i = 0; i < oldSogg.sizePortaDelegataList(); i++) {
			PortaDelegata pde = oldSogg.getPortaDelegata(i);
			for (int j = 0; j < pde.sizeServizioApplicativoList(); j++) {
				PortaDelegataServizioApplicativo tmpSA = pde.getServizioApplicativo(j);
				if (oldSa.getNome().equals(tmpSA.getNome())) {
					servizioApplicativoInUso = true;
					break;
				}
			}
			if (servizioApplicativoInUso)
				break;
		}

		if (!servizioApplicativoInUso) {
			for (int i = 0; i < oldSogg.sizePortaApplicativaList(); i++) {
				PortaApplicativa pa = oldSogg.getPortaApplicativa(i);
				for (int j = 0; j < pa.sizeServizioApplicativoList(); j++) {
					PortaApplicativaServizioApplicativo tmpSA = pa.getServizioApplicativo(j);
					if (oldSa.getNome().equals(tmpSA.getNome())) {
						servizioApplicativoInUso = true;
						break;
					}
				}
				if (servizioApplicativoInUso)
					break;
			}
		}
		
		if (servizioApplicativoInUso) {
			throw FaultCode.RICHIESTA_NON_VALIDA.toException("Il Servizio Applicativo " + oldSa.getNome() + "è già stato associato ad alcune porte delegate e/o applicative del Soggetto " + nomeProv + ". Se si desidera modificare il Soggetto è necessario eliminare prima tutte le occorrenze del Servizio Applicativo");
		}
	}
	
}
