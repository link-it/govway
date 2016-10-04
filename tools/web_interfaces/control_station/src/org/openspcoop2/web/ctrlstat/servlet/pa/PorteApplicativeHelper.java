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
package org.openspcoop2.web.ctrlstat.servlet.pa;

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
import org.openspcoop2.core.config.PortaApplicativa;
import org.openspcoop2.core.config.PortaApplicativaAzione;
import org.openspcoop2.core.config.PortaApplicativaServizio;
import org.openspcoop2.core.config.PortaApplicativaSoggettoVirtuale;
import org.openspcoop2.core.config.ProprietaProtocollo;
import org.openspcoop2.core.config.ServizioApplicativo;
import org.openspcoop2.core.config.constants.CostantiConfigurazione;
import org.openspcoop2.core.config.constants.MTOMProcessorType;
import org.openspcoop2.core.config.constants.ProprietaProtocolloValore;
import org.openspcoop2.core.id.IDPortaApplicativa;
import org.openspcoop2.core.id.IDServizio;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.core.registry.AccordoServizioParteSpecifica;
import org.openspcoop2.core.registry.Servizio;
import org.openspcoop2.core.registry.Soggetto;
import org.openspcoop2.core.registry.constants.CostantiRegistroServizi;
import org.openspcoop2.core.registry.driver.DriverRegistroServiziException;
import org.openspcoop2.core.registry.driver.DriverRegistroServiziNotFound;
import org.openspcoop2.protocol.sdk.constants.FunzionalitaProtocollo;
import org.openspcoop2.web.ctrlstat.core.Search;
import org.openspcoop2.web.ctrlstat.costanti.CostantiControlStation;
import org.openspcoop2.web.ctrlstat.plugins.IExtendedListServlet;
import org.openspcoop2.web.ctrlstat.servlet.ConsoleHelper;
import org.openspcoop2.web.ctrlstat.servlet.aps.AccordiServizioParteSpecificaCostanti;
import org.openspcoop2.web.ctrlstat.servlet.pd.PorteDelegateCostanti;
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
 * PorteApplicativeHelper
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class PorteApplicativeHelper extends ConsoleHelper {

	public PorteApplicativeHelper(HttpServletRequest request, PageData pd, 
			HttpSession session) throws Exception {
		super(request, pd,  session);
	}


	// Controlla i dati della porta applicativa
	public boolean porteAppCheckData(TipoOperazione tipoOp, String oldNomePA) throws Exception {
		try {
			//String idPorta = this.request.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_PORTA);
			String nomePorta = this.request.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_NOME_PORTA);
			String idsogg = this.request.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_SOGGETTO);
			int soggInt = Integer.parseInt(idsogg);
			// String descr = this.request.getParameter("descr");
			String soggvirt = this.request.getParameter("soggvirt");
			if (soggvirt == null) {
				soggvirt = "-";
			}
			IDSoggetto idSoggettoVirtuale = null;
			if (!soggvirt.equals("-")){
				idSoggettoVirtuale = new IDSoggetto(soggvirt.split("/")[0],soggvirt.split("/")[1]);
			}
			String servizio = this.request.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_SERVIZIO);
			if (servizio == null) {
				servizio = "";
			}
			String azione = this.request.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_AZIONE);
			if (azione == null) {
				azione = "-";
			}
			String xsd = this.request.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_XSD);

			String behaviour = this.request.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_BEHAVIOUR);
			
			// Campi obbligatori
			if (nomePorta.equals("") || soggvirt.equals("") || servizio.equals("")) {
				String tmpElenco = "";
				if (nomePorta.equals("")) {
					tmpElenco = "Nome";
				}
				if (soggvirt.equals("")) {
					if (tmpElenco.equals("")) {
						tmpElenco = "Soggetto virtuale";
					} else {
						tmpElenco = tmpElenco + ", Soggetto virtuale";
					}
				}
				if (servizio.equals("")) {
					if (tmpElenco.equals("")) {
						tmpElenco = "Servizio";
					} else {
						tmpElenco = tmpElenco + ", Servizio";
					}
				}
				this.pd.setMessage("Dati incompleti. E' necessario indicare: " + tmpElenco);
				return false;
			}

			// Controllo che non ci siano spazi nei campi di testo
			if(behaviour!=null && !"".equals(behaviour)){
				if (behaviour.indexOf(" ") != -1 || behaviour.indexOf(",") != -1 ) {
					this.pd.setMessage("Non inserire ne spazi ne ',' nel campo 'behaviour'");
					return false;
				}
			}
			
			// Controllo che non ci siano spazi nei campi di testo
			if (nomePorta.indexOf(" ") != -1) {
				this.pd.setMessage("Non inserire spazi nei campi di testo");
				return false;
			}

			// Controllo che i campi "select" abbiano uno dei valori ammessi
			if (!xsd.equals("abilitato") && !xsd.equals("disabilitato") && !xsd.equals("warningOnly")) {
				this.pd.setMessage("Validazione XSD dev'essere abilitato, disabilitato o warningOnly");
				return false;
			}

			if(this.core.isRegistroServiziLocale()){
				// Controllo che il soggvirt appartenga alla lista di soggetti
				// disponibili
				boolean trovatoSogg = false;
				List<Soggetto> lista = this.soggettiCore.soggettiRegistroList("", new Search(true));
				for (int i = 0; i < lista.size(); i++) {
					Soggetto singleSogg = lista.get(i);
					if (soggInt != singleSogg.getId()) {
						trovatoSogg = true;
						break;
					}
				}
				if (!trovatoSogg && !soggvirt.equals("-")) {
					this.pd.setMessage("Il Soggetto virtuale dev'essere scelto tra quelli definiti nel pannello Soggetti");
					return false;
				}

				/*
					// Controllo che il servizio appartenga alla lista di servizi
					// disponibili per il soggetto
					int servInt = Integer.parseInt(servizio);

					// Prendo il nome e il tipo del soggetto
					Soggetto mySogg;
					if (!soggvirt.equals("-"))
						mySogg = this.soggettiCore.getSoggettoRegistro(idSoggettoVirtuale);
					else
						mySogg = this.soggettiCore.getSoggettoRegistro(soggInt);
					String nomeprov = mySogg.getNome();
					String tipoprov = mySogg.getTipo();

					//String nomeserv = "", tiposerv = "";
					boolean trovatoServ = this.core.existsServizio(servInt);
					//if (trovatoServ) {
					//	Servizio myServ = this.core.getServizio(servInt);
					//	tiposerv = myServ.getTipo();
					//	nomeserv = myServ.getNome();
					//}
					if (!trovatoServ) {
						this.pd.setMessage("Il Servizio dev'essere scelto tra quelli definiti nel pannello Servizi ed associati al soggetto " + tipoprov + "/" + nomeprov);
						return false;
					}*/ // Questo controllo serve??? Il valore è stato preso da una select list!!!

				// Controllo che l'azione, se definita, appartenga alla lista di
				// azioni
				// disponibili per il servizio
				/*boolean trovataAz = false;
					if (!azione.equals("-")) {
						// Prendo il nome, il tipo e l'accordo del servizio
						Servizio myServ = this.core.getServizio(servInt);
						//nomeserv = myServ.getNome();
						//tiposerv = myServ.getTipo();
						long idAcc = myServ.getIdAccordo();

						if(myServ.getPortType()!=null){
							trovataAz = this.core.existsAccordoServizioPorttypeOperation(azione, IDAccordo.getIDAccordoFromUri(myServ.getAccordoServizio()), myServ.getPortType());
							if (!trovataAz) {
								this.pd.setMessage("L'Azione dev'essere scelta tra quelle associate al servizio "+myServ.getPortType()+" dell'accordo di servizio "+myServ.getAccordoServizio());
								return false;
							}
						}
						else{
							trovataAz = this.core.existsAccordoServizioAzione(azione, idAcc);
							if (!trovataAz) {
								this.pd.setMessage("L'Azione dev'essere scelta tra quelle associate all'accordo di servizio "+myServ.getAccordoServizio());
								return false;
							}
						}
					}*/ // Questo controllo serve??? Il valore è stato preso da una select list!!!
			}

			// Se tipoOp = add, controllo che la porta applicativa non sia
			// gia' stata registrata
			if (tipoOp.equals(TipoOperazione.ADD) || TipoOperazione.CHANGE.equals(tipoOp)) {
				boolean giaRegistrato = false;
				/*
				 * fix bug 54 Una porta applicativa viene identificata
				 * univocamente dalla tripla: - SoggettoErogatore - Servizio
				 * - Azione
				 * 
				 * Dove uno dei possibili valore dell'azione e' NULL.
				 * 
				 * Il soggetto erogatore e' normalmente il proprietario
				 * della porta applicativa. Se un soggetto virtuale e'
				 * definito, questo diventa il soggetto erogatore.
				 * 
				 * Note: Vengono inoltre effettuati controlli
				 * sull'univocita' del nome la pddConsole non permette
				 * di inserire porte applicative con stesso nome;
				 */

				IDPortaApplicativa idPA = new IDPortaApplicativa();
				// se specificato il soggetto virtuale
				org.openspcoop2.core.config.Soggetto proprietario = null;
				Soggetto virtuale = null;
				proprietario = this.soggettiCore.getSoggetto(soggInt);
				if (!soggvirt.equals("-")) {
					virtuale = this.soggettiCore.getSoggettoRegistro(idSoggettoVirtuale);
				}
				IDSoggetto idSO = new IDSoggetto(proprietario.getTipo(), proprietario.getNome());
				IDServizio idSE = new IDServizio(idSO);
				String [] tmp = servizio.split(" ");
//				IDServizio idServizio = new IDServizio(tmp[0].split("/")[0],tmp[0].split("/")[1],
//						tmp[1].split("/")[0],tmp[1].split("/")[1]);
				idSE.setTipoServizio(tmp[1].split("/")[0]);
				idSE.setServizio(tmp[1].split("/")[1]);
				if (azione!=null && !"".equals(azione) && !"-".equals(azione) ) {
					idSE.setAzione(azione);
				}

				idPA.setIDServizio(idSE);

				// controllo su nome e proprietario in caso di aggiunta
				// nuova pa
				// non posso inserire una porta con stesso nome e
				// proprietario
				if (TipoOperazione.ADD.equals(tipoOp)) {
					giaRegistrato = this.porteApplicativeCore.existsPortaApplicativa(nomePorta, idSO);

					if (giaRegistrato) {
						this.pd.setMessage("Esiste gia' una Porta Applicativa [" + nomePorta + "] appartenente al Soggetto [" + idSO.toString() + "]");
						return false;
					}

					if (!soggvirt.equals("-")) {
						IDSoggetto idSOVirt = new IDSoggetto(virtuale.getTipo(), virtuale.getNome());
						giaRegistrato = this.porteApplicativeCore.existsPortaApplicativaVirtuale(idPA, idSOVirt, true);
					} else {
						giaRegistrato = this.porteApplicativeCore.existsPortaApplicativa(idPA, true);
					}

				} else {
					PortaApplicativa pa = this.porteApplicativeCore.getPortaApplicativa(oldNomePA, idSO);
					PortaApplicativaServizio pa_servizio_precedente = pa.getServizio();
					PortaApplicativaAzione pa_azione = pa.getAzione();

					IDServizio idServPrec = new IDServizio();
					if (pa_servizio_precedente != null) {
						idServPrec.setTipoServizio(pa_servizio_precedente.getTipo());
						idServPrec.setServizio(pa_servizio_precedente.getNome());
					}
					if (pa_azione != null)
						idServPrec.setAzione(pa_azione.getNome());
					idServPrec.setSoggettoErogatore(idSO);

					// se e' cambiato il servizio e/o azione
					// allora devo controllare se esistono altre porte
					// applicative associate
					if (!idSE.equals(idServPrec)) {
						// se presente soggetto virtuale
						if (!soggvirt.equals("-")) {
							IDSoggetto idSOVirt = new IDSoggetto(virtuale.getTipo(), virtuale.getNome());
							giaRegistrato = this.porteApplicativeCore.existsPortaApplicativaVirtuale(idPA, idSOVirt, true);
						} else {
							giaRegistrato = this.porteApplicativeCore.existsPortaApplicativa(idPA, true);
						}

					}
				}

				if (giaRegistrato) {
					this.pd.setMessage("Esiste gia' una Porta Applicativa per il " + "Servizio [" + idPA.getIDServizio().getTipoServizio() + "/" + idPA.getIDServizio().getServizio() + "] " + "con Azione [" + (idPA.getIDServizio().getAzione() != null ? idPA.getIDServizio().getAzione() : "not set") + "] " + "erogato dal Soggetto " + (!soggvirt.equals("-") ? "Virtuale" : "") + " [" + (!soggvirt.equals("-") ? virtuale.getTipo() + "/" + virtuale.getNome() : idPA.getIDServizio().getSoggettoErogatore().toString()) + "]");

					// this.pd.setMessage("La porta applicativa " + id + "
					// &egrave; gi&agrave; stata registrata");
					return false;
				}

				
				if (TipoOperazione.CHANGE.equals(tipoOp)) {
					if (!nomePorta.equals(oldNomePA)) {
						boolean exists = this.porteApplicativeCore.existsPortaApplicativa(nomePorta, idSO);
						if (exists) {
							this.pd.setMessage("Esiste gia' una Porta Applicativa con nome [" + nomePorta + "] associata al Soggetto [" + idSO.toString() + "]");
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

	// Controlla i dati del servizioApplicativo della porta applicativa
	public boolean porteAppServizioApplicativoCheckData(TipoOperazione tipoOp)
			throws Exception {
		try {
			String idPorta = this.request.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID);
			int idInt = Integer.parseInt(idPorta);
			String idsogg = this.request.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_SOGGETTO);
			int soggInt = Integer.parseInt(idsogg);
			String servizioApplicativo = this.request.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_SERVIZIO_APPLICATIVO);
			if (servizioApplicativo == null) {
				servizioApplicativo = "";
			}

			// Campi obbligatori
			if (servizioApplicativo.equals("")) {
				this.pd.setMessage("Dati incompleti. E' necessario indicare un Servizio Applicativo");
				return false;
			}

			boolean trovatoServizioApplicativo = false;
			long idSA = 0;
			// Controllo che il servizioApplicativo appartenga alla lista di
			// servizioApplicativo disponibili per il soggetto
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

			if (trovatoServizioApplicativo)
				idSA = this.saCore.getIdServizioApplicativo(ids, servizioApplicativo);

			if (!trovatoServizioApplicativo) {
				this.pd.setMessage("Il Servizio Applicativo dev'essere scelto tra quelli definiti nel pannello Servizi Applicativi ed associati al soggetto " + tipoprov + "/" + nomeprov);
				return false;
			}

			// Se tipoOp = add, controllo che il servizioApplicativo non sia
			// gia'
			// stato
			// registrato per la porta applicativa
			if (tipoOp.equals(TipoOperazione.ADD)) {
				boolean giaRegistrato = false;

				// Prendo il nome della porta applicativa
				PortaApplicativa pa = this.porteApplicativeCore.getPortaApplicativa(idInt);
				String nomeporta = pa.getNome();

				for (int i = 0; i < pa.sizeServizioApplicativoList(); i++) {
					ServizioApplicativo tmpSA = pa.getServizioApplicativo(i);
					if (idSA == tmpSA.getId()) {
						giaRegistrato = true;
						break;
					}
				}

				if (giaRegistrato) {
					this.pd.setMessage("Il Servizio Applicativo " + servizioApplicativo + " &egrave; gi&agrave; stato associato alla porta applicativa " + nomeporta);
					return false;
				}
			}

			return true;
		} catch (Exception e) {
			this.log.error("Exception: " + e.getMessage(), e);
			throw new Exception(e);
		}
	}







	// Controlla i dati della property della porta applicativa
	public boolean porteAppPropCheckData(TipoOperazione tipoOp) throws Exception {
		try {
			String idPorta = this.request.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID);
			int idInt = Integer.parseInt(idPorta);
			String nome = this.request.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_NOME);
			String valore = this.request.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_VALORE);

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
			if ((nome.indexOf(" ") != -1) || (valore.indexOf(" ") != -1)) {
				this.pd.setMessage("Non inserire spazi nei campi di testo");
				return false;
			}

			// Se tipoOp = add, controllo che la property non sia gia'
			// stata
			// registrata per la porta applicativa
			if (tipoOp.equals(TipoOperazione.ADD)) {
				boolean giaRegistrato = false;
				PortaApplicativa pa = this.porteApplicativeCore.getPortaApplicativa(idInt);
				String nomeporta = pa.getNome();

				for (int i = 0; i < pa.sizeProprietaProtocolloList(); i++) {
					ProprietaProtocollo tmpProp = pa.getProprietaProtocollo(i);
					if (nome.equals(tmpProp.getNome())) {
						giaRegistrato = true;
						break;
					}
				}

				if (giaRegistrato) {
					this.pd.setMessage("La property " + nome + " &egrave; gi&agrave; stata associata alla porta applicativa " + nomeporta);
					return false;
				}
			}

			return true;
		} catch (Exception e) {
			this.log.error("Exception: " + e.getMessage(), e);
			throw new Exception(e);
		}
	}

	// Controlla i dati del message-security request-flow della porta applicativa
	public boolean porteAppMessageSecurityRequestCheckData(TipoOperazione tipoOp) throws Exception {
		try {
			String idPorta = this.request.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID);
			int idInt = Integer.parseInt(idPorta);
			String nome = this.request.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_NOME);
			String valore = this.request.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_VALORE);

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
			// registrato per la porta applicativa
			if (tipoOp.equals(TipoOperazione.ADD)) {
				boolean giaRegistrato = false;
				PortaApplicativa pa = this.porteApplicativeCore.getPortaApplicativa(idInt);
				String nomeporta = pa.getNome();
				MessageSecurity messageSecurity = pa.getMessageSecurity();

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
					this.pd.setMessage("La proprieta' di message-security " + nome + " &egrave; gi&agrave; stato associata alla porta applicativa " + nomeporta);
					return false;
				}
			}

			return true;

		} catch (Exception e) {
			this.log.error("Exception: " + e.getMessage(), e);
			throw new Exception(e);
		}
	}

	// Controlla i dati del message-security response-flow della porta applicativa
	public boolean porteAppMessageSecurityResponseCheckData(TipoOperazione tipoOp) throws Exception {
		try {
			String idPorta = this.request.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID);
			int idInt = Integer.parseInt(idPorta);
			String nome = this.request.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_NOME);
			String valore = this.request.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_VALORE);

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
			// registrato per la porta applicativa
			if (tipoOp.equals(TipoOperazione.ADD)) {
				boolean giaRegistrato = false;
				PortaApplicativa pa = this.porteApplicativeCore.getPortaApplicativa(idInt);
				String nomeporta = pa.getNome();
				MessageSecurity messageSecurity = pa.getMessageSecurity();

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
					this.pd.setMessage("La proprieta' di message-security " + nome + " &egrave; gi&agrave; stato associato alla porta applicativa " + nomeporta);
					return false;
				}
			}

			return true;

		} catch (Exception e) {
			this.log.error("Exception: " + e.getMessage(), e);
			throw new Exception(e);
		}
	}

	public Vector<DataElement> addHiddenFieldCorrelazioneApplicativaToDati(TipoOperazione tipoOp,
			String idcorrString, Vector<DataElement> dati) {

		DataElement de = new DataElement();
		if(idcorrString != null){
			de = new DataElement();
			de.setLabel(PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_ID_CORRELAZIONE_APPLICATIVA);
			de.setValue(idcorrString);
			de.setType(DataElementType.HIDDEN);
			de.setName(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_CORRELAZIONE_APPLICATIVA);
			dati.addElement(de);
		}
		return dati;
	}


	public Vector<DataElement> addPorteAppToDati(TipoOperazione tipoOp,Vector<DataElement> dati, String nomePorta, String descr, String soggvirt, String[] soggettiList, String[] soggettiListLabel, 
			String servizio, String[] serviziList, String[] serviziListLabel, String azione, String[] azioniList,  String stateless, String ricsim, String ricasim, 
			String idsogg, String idPorta, String xsd, String tipoValidazione, String gestBody, String gestManifest,String integrazione,
			int numCorrApp,String scadcorr,String autorizzazioneContenuti, String protocollo
			,int numSA,	String statoMessageSecurity ,String statoMTOM ,int numCorrelazioneReq , int numCorrelazioneRes, int numProprProt,String applicaMTOM,
			String behaviour,
			String[] servizioApplicativoList, String servizioApplicativo) throws DriverRegistroServiziNotFound, DriverRegistroServiziException {

		boolean isModalitaAvanzata = ServletUtils.getUserFromSession(this.session).getInterfaceType().equals(InterfaceType.AVANZATA);
		Boolean contaListe = ServletUtils.getContaListeFromSession(this.session);

		// Soggetto virtuale?
		Boolean soggVirt = (Boolean) this.session.getAttribute(CostantiControlStation.SESSION_PARAMETRO_GESTIONE_SOGGETTI_VIRTUALI);
		
		int alternativeSize = 80;

		boolean configurazioneStandardNonApplicabile = false;
		
		User user = ServletUtils.getUserFromSession(this.session);
		
		
		
		// *************** Nome/Descrizione *********************
		
		DataElement de = new DataElement();
		de.setLabel(PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_NOME);
		if (nomePorta == null) {
			de.setValue("");
		} else {
			de.setValue(nomePorta);
		}
		if( InterfaceType.STANDARD.equals(user.getInterfaceType()) && TipoOperazione.CHANGE.equals(tipoOp) ){
			de.setType(DataElementType.TEXT);
		}
		else{
			de.setType(DataElementType.TEXT_EDIT);
			de.setRequired(true);
		}
		de.setName(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_NOME_PORTA);
		de.setSize(alternativeSize);
		dati.addElement(de);

		de = new DataElement();
		de.setLabel(PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_DESCRIZIONE);
		if (descr == null) {
			de.setValue("");
		} else {
			de.setValue(descr);
		}
		de.setType(DataElementType.TEXT_EDIT);
		de.setName(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_DESCRIZIONE);
		de.setSize(alternativeSize);
		dati.addElement(de);


		
		
		
		
		
		// *************** Soggetto Virtuale *********************

		if (soggVirt) {
		
			de = new DataElement();
			de.setLabel(PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_SOGGETTO_VIRTUALE);
			de.setType(DataElementType.TITLE);
			dati.addElement(de);
			
		}
		
		de = new DataElement();
		de.setLabel(PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_NOME);
		de.setName(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_SOGGETTO_VIRTUALE);
		if (soggVirt) {
			de.setType(DataElementType.SELECT);
			de.setValues(soggettiList);
			de.setLabels(soggettiListLabel);
			de.setSelected(soggvirt);
			//			de.setOnChange("CambiaSoggetto('" + tipoOp + "')");
			de.setPostBack(true);
		}else{
			de.setValue("-");
			de.setType(DataElementType.HIDDEN);
		}
		dati.addElement(de);

		
		
		
		
		
		// *************** Servizio *********************
		
		de = new DataElement();
		//if(this.core.isTerminologiaSICA_RegistroServizi()){
		//	de.setLabel("Accordo Servizio Parte Specifica");
		//}else{
		de.setLabel(PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_SERVIZIO);
		//}
		de.setType(DataElementType.TITLE);
		dati.addElement(de);

		de = new DataElement();
		de.setLabel(PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_NOME);
		de.setName(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_SERVIZIO);
		de.setPostBack(true);
		if(TipoOperazione.CHANGE.equals(tipoOp) && InterfaceType.STANDARD.equals(ServletUtils.getUserFromSession(this.session).getInterfaceType())) {
			de.setType(DataElementType.HIDDEN);
			de.setValue(servizio);
			dati.addElement(de);
			
			de = new DataElement();
			for (int i = 0; i < serviziList.length; i++) {
				if(serviziList[i]!=null && serviziList[i].equals(servizio)){
					de.setLabel(serviziListLabel[i]);
					break;
				}
			}
			de.setType(DataElementType.NOTE);
			
		}else{
			de.setType(DataElementType.SELECT);
			de.setValues(serviziList);
			de.setLabels(serviziListLabel);
			de.setSelected(servizio);
		}
		//		de.setOnChange("CambiaServizio('" + tipoOp + "')");
		dati.addElement(de);

		
		
		
		// *************** Azione *********************
		
		if (azioniList != null) {
			
			de = new DataElement();
			de.setLabel(PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_AZIONE);
			de.setType(DataElementType.TITLE);
			dati.addElement(de);

			de = new DataElement();
			de.setLabel(PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_NOME);
			de.setName(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_AZIONE);
			if(TipoOperazione.CHANGE.equals(tipoOp) && InterfaceType.STANDARD.equals(ServletUtils.getUserFromSession(this.session).getInterfaceType())) {
				if("-".equals(azione)){
					de.setType(DataElementType.HIDDEN);
					de.setValue(azione);
					dati.addElement(de);
					
					de = new DataElement();
					de.setLabel(PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_QUALSIASI_AZIONE);
					de.setType(DataElementType.NOTE);
					
				}else{
					de.setType(DataElementType.TEXT);
					de.setValue(azione);
				}
			}else{
				de.setType(DataElementType.SELECT);
				de.setValues(azioniList);
				de.setSelected(azione);
			}
			dati.addElement(de);
		}
		
		
		
		
		
		
		// *************** ServizioApplicativo Erogatore *********************
		
		if(TipoOperazione.CHANGE.equals(tipoOp)){
		
			if (InterfaceType.STANDARD.equals(ServletUtils.getUserFromSession(this.session).getInterfaceType())) {
				
				de = new DataElement();
				de.setType(DataElementType.TITLE);
				de.setLabel(PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_SERVIZIO_APPLICATIVO_EROGATORE);
				dati.addElement(de);
				
				de = new DataElement();
				if(numSA<=1){
					
					de = new DataElement();
					de.setLabel(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_NOME );
					de.setType(DataElementType.SELECT);
					de.setName(CostantiControlStation.PARAMETRO_SERVIZIO_APPLICATIVO);
					de.setValues(servizioApplicativoList);
					de.setSelected(servizioApplicativo);
					
				}
				else{
					de.setLabel(CostantiControlStation.LABEL_CONFIGURAZIONE_IMPOSTATA_MODALITA_AVANZATA_SHORT_MESSAGE);
					de.setType(DataElementType.TEXT);
					de.setValue("");
					configurazioneStandardNonApplicabile = true;
				}
				dati.addElement(de);
			}
			else{
				
				de = new DataElement();
				de.setType(DataElementType.TITLE);
				de.setLabel(PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_SERVIZI_APPLICATIVI_EROGATORI);
				dati.addElement(de);
				
				
				Parameter pId = new Parameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID, idPorta);
				Parameter pIdSogg = new Parameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_SOGGETTO, idsogg);
				
				de = new DataElement();
				de.setType(DataElementType.LINK);

				de.setUrl(PorteApplicativeCostanti.SERVLET_NAME_PORTE_APPLICATIVE_SERVIZIO_APPLICATIVO_LIST,
						pIdSogg,pId);
				if (contaListe) {
					ServletUtils.setDataElementCustomLabel(de,PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_SERVIZI_APPLICATIVI,new Long(numSA));
				} else
					ServletUtils.setDataElementCustomLabel(de,PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_SERVIZI_APPLICATIVI);

				dati.addElement(de);
			}
		
			
		}
		
		
		
		
		
		
		// *************** Controllo degli Accessi *********************
		if (InterfaceType.STANDARD.equals(ServletUtils.getUserFromSession(this.session).getInterfaceType())==false) {
			de = new DataElement();
			de.setType(DataElementType.TITLE);
			de.setLabel(PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_CONTROLLO_ACCESSI);
			dati.addElement(de);
		}
		
		// Autorizzazione contenuto
		de = new DataElement();
		de.setLabel(PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_AUTORIZZAZIONE_CONTENUTI);
		if (InterfaceType.STANDARD.equals(ServletUtils.getUserFromSession(this.session).getInterfaceType())) {
			de.setType(DataElementType.HIDDEN);
		}else{
			de.setType(DataElementType.TEXT_EDIT);
		}
		de.setName(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_AUTORIZZAZIONE_CONTENUTI);
		de.setValue(autorizzazioneContenuti);
		de.setSize(alternativeSize);
		dati.addElement(de);

		
		
		
		
		
		// *************** Integrazione *********************
		
		Vector<DataElement> deIntegrazione = new Vector<DataElement>();
				
		if (tipoOp.equals(TipoOperazione.CHANGE)) {
			de = new DataElement();
			de.setLabel(PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_METADATI);
			de.setValue(integrazione);
			de.setName(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_INTEGRAZIONE);
			de.setSize(alternativeSize);
			if(InterfaceType.STANDARD.equals(ServletUtils.getUserFromSession(this.session).getInterfaceType())){
				de.setType(DataElementType.HIDDEN);
				dati.addElement(de);
			}else{
				de.setType(DataElementType.TEXT_EDIT);
				deIntegrazione.addElement(de);
			}
		}
		
		String[] tipoStateless = { 
				PorteApplicativeCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_APPLICATIVE_STATELESS_DEFAULT, 
				PorteApplicativeCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_APPLICATIVE_STATELESS_ABILITATO, 
				PorteApplicativeCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_APPLICATIVE_STATELESS_DISABILITATO
		};
		de = new DataElement();
		de.setLabel(PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_STATELESS);
		de.setName(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_STATELESS);
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
		
				
		de = new DataElement();
		de.setLabel(PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_BEHAVIOUR);
		de.setValue(behaviour);
		de.setName(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_BEHAVIOUR);
		de.setSize(alternativeSize);
		if (InterfaceType.AVANZATA.equals(ServletUtils.getUserFromSession(this.session).getInterfaceType())) {
			de.setType(DataElementType.TEXT_EDIT);
			deIntegrazione.addElement(de);
		}
		else{
			de.setType(DataElementType.HIDDEN);
			dati.addElement(de);
		}
			
		if (tipoOp.equals(TipoOperazione.CHANGE)) {
			Parameter pId = new Parameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID, idPorta);
			Parameter pIdSogg = new Parameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_SOGGETTO, idsogg);
			
			if(isModalitaAvanzata){
				de = new DataElement();
				de.setType(DataElementType.LINK);
				de.setUrl(PorteApplicativeCostanti.SERVLET_NAME_PORTE_APPLICATIVE_PROPRIETA_PROTOCOLLO_LIST,pIdSogg,pId);

				if (contaListe) {
					ServletUtils.setDataElementCustomLabel(de,PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_PROTOCOL_PROPERTIES,new Long(numProprProt));
				} else
					ServletUtils.setDataElementCustomLabel(de,PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_PROTOCOL_PROPERTIES);

				deIntegrazione.addElement(de);
			}
		}
		
		if(deIntegrazione.size()>0){
			
			de = new DataElement();
			de.setType(DataElementType.TITLE);
			de.setLabel(PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_INTEGRAZIONE);
			dati.addElement(de);
			
			for (int i = 0; i < deIntegrazione.size(); i++) {
				dati.addElement(deIntegrazione.get(i));
			}
		}
		

		
		
		
		// *************** CorrelazioneApplicativa *********************
		
		if (tipoOp.equals(TipoOperazione.CHANGE)) {
		
			de = new DataElement();
			de.setType(DataElementType.TITLE);
			de.setLabel(PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_CORRELAZIONE_APPLICATIVA);
			dati.addElement(de);
			
			if (numCorrApp > 0) {
				de = new DataElement();
				de.setLabel(PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_SCADENZA_CORRELAZIONE_APPLICATIVA );
				de.setValue(scadcorr);
				de.setType(DataElementType.TEXT_EDIT);
				de.setName(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_SCADENZA_CORRELAZIONE_APPLICATIVA);
				de.setSize(alternativeSize);
				dati.addElement(de);
			}
			
			Parameter pId = new Parameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID, idPorta);
			Parameter pIdSogg = new Parameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_SOGGETTO, idsogg);
			Parameter pNome = new Parameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_NOME, nomePorta);

			de = new DataElement();
			de.setType(DataElementType.LINK);
			de.setUrl(PorteApplicativeCostanti.SERVLET_NAME_PORTE_APPLICATIVE_CORRELAZIONE_APPLICATIVA_REQUEST_LIST,pIdSogg,pId,pNome);
			if (contaListe) {
				ServletUtils.setDataElementCustomLabel(de,PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_CORRELAZIONE_APPLICATIVA_RICHIESTA,new Long(numCorrelazioneReq));
			} else
				ServletUtils.setDataElementCustomLabel(de,PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_CORRELAZIONE_APPLICATIVA_RICHIESTA);

			dati.addElement(de);

			de = new DataElement();
			de.setType(DataElementType.LINK);
			de.setUrl(PorteApplicativeCostanti.SERVLET_NAME_PORTE_APPLICATIVE_CORRELAZIONE_APPLICATIVA_RESPONSE_LIST,pIdSogg,pId,pNome);
			if (contaListe) {
				ServletUtils.setDataElementCustomLabel(de,PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_CORRELAZIONE_APPLICATIVA_RISPOSTA,new Long(numCorrelazioneRes));
			} else
				ServletUtils.setDataElementCustomLabel(de,PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_CORRELAZIONE_APPLICATIVA_RISPOSTA);

			dati.addElement(de);
						
		}

		

		
		
		
		
		// *************** Gestione Messaggio *********************
		
		if (tipoOp.equals(TipoOperazione.CHANGE)) {
			Parameter pId = new Parameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID, idPorta);
			Parameter pIdSogg = new Parameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_SOGGETTO, idsogg);
			
			de = new DataElement();
			de.setType(DataElementType.TITLE);
			de.setLabel(PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_GESTIONE_MESSAGGIO);
			dati.addElement(de);

			de = new DataElement();
			de.setType(DataElementType.LINK);
			de.setUrl(PorteApplicativeCostanti.SERVLET_NAME_PORTE_APPLICATIVE_MESSAGE_SECURITY,pIdSogg,pId);
			ServletUtils.setDataElementCustomLabel(de, PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_MESSAGE_SECURITY, statoMessageSecurity);
			dati.addElement(de);
			
			//if (InterfaceType.AVANZATA.equals(ServletUtils.getUserFromSession(this.session).getInterfaceType())) {
			de = new DataElement();
			de.setType(DataElementType.LINK);
			de.setUrl(PorteApplicativeCostanti.SERVLET_NAME_PORTE_APPLICATIVE_MTOM, pIdSogg,pId);
			ServletUtils.setDataElementCustomLabel(de, PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_MTOM, statoMTOM);
			dati.addElement(de);
			//}
			
		}
		
		

		
		
		
		
		
		
		
		// *************** Validazione Contenuti *********************
		
		de = new DataElement();
		de.setType(DataElementType.TITLE);
		de.setLabel(PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_VALIDAZIONE_CONTENUTI);
		dati.addElement(de);
		
		String[] tipoXsd = { 
				PorteApplicativeCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_APPLICATIVE_XSD_ABILITATO ,
				PorteApplicativeCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_APPLICATIVE_XSD_DISABILITATO ,
				PorteApplicativeCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_APPLICATIVE_XSD_WARNING_ONLY 
		};
		de = new DataElement();
		de.setLabel(PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_STATO);
		de.setType(DataElementType.SELECT);
		de.setName(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_XSD);
		de.setValues(tipoXsd);
		//		de.setOnChange("CambiaMode('" + tipoOp + "')");
		de.setPostBack(true);
		de.setSelected(xsd);
		dati.addElement(de);

		if (PorteApplicativeCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_APPLICATIVE_XSD_ABILITATO.equals(xsd) ||
				PorteApplicativeCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_APPLICATIVE_XSD_WARNING_ONLY.equals(xsd)) {
			String[] tipi_validazione = {
					PorteApplicativeCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_APPLICATIVE_TIPO_VALIDAZIONE_XSD,
					PorteApplicativeCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_APPLICATIVE_TIPO_VALIDAZIONE_WSDL,
					PorteApplicativeCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_APPLICATIVE_TIPO_VALIDAZIONE_OPENSPCOOP 
			};
			//String[] tipi_validazione = { "xsd", "wsdl" };
			de = new DataElement();
			de.setLabel(PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_TIPO);
			de.setType(DataElementType.SELECT);
			de.setName(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_TIPO_VALIDAZIONE);
			de.setValues(tipi_validazione);
			de.setSelected(tipoValidazione);
			dati.addElement(de);
			
			
			// Applica MTOM 
			de = new DataElement();
			de.setLabel(PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_ACCETTA_MTOM);
			
			//if (InterfaceType.AVANZATA.equals(ServletUtils.getUserFromSession(this.session).getInterfaceType())) {
			de.setType(DataElementType.CHECKBOX);
			if( ServletUtils.isCheckBoxEnabled(applicaMTOM) || CostantiRegistroServizi.ABILITATO.equals(applicaMTOM) ){
				de.setSelected(true);
			}
//			}
//			else{
//				de.setType(DataElementType.HIDDEN);
//				de.setValue(applicaMTOM);
//			}
			 
			de.setName(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_APPLICA_MTOM);
			dati.addElement(de);
		}
		
		
		
		
		
		
		
		
		
		// *************** Asincroni *********************
		
		de = new DataElement();
		if (InterfaceType.STANDARD.equals(ServletUtils.getUserFromSession(this.session).getInterfaceType())) {
			de.setType(DataElementType.HIDDEN);
		}else{
			de.setType(DataElementType.TITLE);
		}
		de.setLabel(PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_GESTIONE_ASINCRONA );
		dati.addElement(de);

		String[] tipoRicsim = { 
				PorteApplicativeCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_APPLICATIVE_ABILITATO, 
				PorteApplicativeCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_APPLICATIVE_DISABILITATO 
		};
		de = new DataElement();
		de.setLabel(PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_RICEVUTA_ASINCRONA_SIMMETRICA);
		if (InterfaceType.STANDARD.equals(ServletUtils.getUserFromSession(this.session).getInterfaceType())) {
			de.setType(DataElementType.HIDDEN);
			de.setValue(PorteApplicativeCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_APPLICATIVE_ABILITATO);
		}else{
			de.setType(DataElementType.SELECT);
			de.setValues(tipoRicsim);
			de.setSelected(ricsim);
		}
		de.setName(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_RICEVUTA_ASINCRONA_SIMMETRICA);
		dati.addElement(de);

		String[] tipoRicasim = {
				PorteApplicativeCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_APPLICATIVE_ABILITATO, 
				PorteApplicativeCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_APPLICATIVE_DISABILITATO 
		};
		de = new DataElement();
		de.setLabel(PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_RICEVUTA_ASINCRONA_ASIMMETRICA);
		if (InterfaceType.STANDARD.equals(ServletUtils.getUserFromSession(this.session).getInterfaceType())) {
			de.setType(DataElementType.HIDDEN);
			de.setValue(PorteApplicativeCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_APPLICATIVE_ABILITATO);
		}else{
			de.setType(DataElementType.SELECT);
			de.setValues(tipoRicasim);
			de.setSelected(ricasim);
		}
		de.setName(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_RICEVUTA_ASINCRONA_ASIMMETRICA);
		dati.addElement(de);
		
		
		
		
		
		
		
		
		// ***************  SOAP With Attachments *********************

		if (InterfaceType.AVANZATA.equals(ServletUtils.getUserFromSession(this.session).getInterfaceType())) {

			de = new DataElement();
			de.setType(DataElementType.TITLE);
			de.setLabel(PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_SOAP_WITH_ATTACHMENTS);
			dati.addElement(de);
	
			String[] tipoGestBody = {
					PorteApplicativeCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_APPLICATIVE_GEST_BODY_NONE,
					PorteApplicativeCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_APPLICATIVE_GEST_BODY_ALLEGA,
					PorteApplicativeCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_APPLICATIVE_GEST_BODY_SCARTA 
			};
			de = new DataElement();
			de.setLabel(PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_GESTIONE_BODY);
			de.setType(DataElementType.SELECT);
			de.setName(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_GESTIONE_BODY);
			de.setValues(tipoGestBody);
			de.setSelected(gestBody);
			dati.addElement(de);

			String[] tipoGestManifest = { 
					PorteApplicativeCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_APPLICATIVE_GEST_MANIFEST_DEFAULT, 
					PorteApplicativeCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_APPLICATIVE_GEST_MANIFEST_ABILITATO, 
					PorteApplicativeCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_APPLICATIVE_GEST_MANIFEST_DISABILITATO 
			};
			de = new DataElement();
			de.setLabel(PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_GESTIONE_MANIFEST);
			if(this.core.isFunzionalitaProtocolloSupportataDalProtocollo(protocollo, FunzionalitaProtocollo.MANIFEST_ATTACHMENTS)){
				de.setType(DataElementType.SELECT);
				de.setValues(tipoGestManifest);
				de.setSelected(gestManifest);
			}else {
				de.setType(DataElementType.HIDDEN);
				de.setValue(PorteDelegateCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_DELEGATE_GEST_MANIFEST_DISABILITATO );
			}
	
			de.setName(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_GESTIONE_MANIFEST);
			dati.addElement(de);
			
		}
		
		
		
		
		
		
		if(configurazioneStandardNonApplicabile){
			this.pd.setMessage(CostantiControlStation.LABEL_CONFIGURAZIONE_IMPOSTATA_MODALITA_AVANZATA_LONG_MESSAGE);
			this.pd.disableEditMode();
		}
		


		return dati;
	}


	public void preparePorteAppList(ISearch ricerca, List<PortaApplicativa> lista)
			throws Exception {
		try {
			Boolean contaListe = ServletUtils.getContaListeFromSession(this.session);
			boolean isModalitaAvanzata = ServletUtils.getUserFromSession(this.session).getInterfaceType().equals(InterfaceType.AVANZATA);

			// prelevo il flag che mi dice da quale pagina ho acceduto la sezione delle porte delegate
			Boolean useIdSogg= ServletUtils.getBooleanAttributeFromSession(PorteApplicativeCostanti.ATTRIBUTO_PORTE_APPLICATIVE_USA_ID_SOGGETTO , this.session);

			IExtendedListServlet extendedServletList = this.core.getExtendedServletPortaApplicativa();
			
			String id = this.request.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_SOGGETTO);

			if(useIdSogg)
				ServletUtils.addListElementIntoSession(this.session, PorteApplicativeCostanti.OBJECT_NAME_PORTE_APPLICATIVE,
						new Parameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_SOGGETTO, id));
			else
				ServletUtils.addListElementIntoSession(this.session, PorteApplicativeCostanti.OBJECT_NAME_PORTE_APPLICATIVE);

			// Prendo il soggetto
			String tmpTitle = null;
			if(useIdSogg){
				if(this.core.isRegistroServiziLocale()){
					Soggetto tmpSogg = this.soggettiCore.getSoggettoRegistro(Integer.parseInt(id));
					tmpTitle = tmpSogg.getTipo() + "/" + tmpSogg.getNome();
				}else{
					org.openspcoop2.core.config.Soggetto tmpSogg = this.soggettiCore.getSoggetto(Integer.parseInt(id));
					tmpTitle = tmpSogg.getTipo() + "/" + tmpSogg.getNome();
				}
			}

			int idLista = useIdSogg ? Liste.PORTE_APPLICATIVE_BY_SOGGETTO : Liste.PORTE_APPLICATIVE;
			int limit = ricerca.getPageSize(idLista);
			int offset = ricerca.getIndexIniziale(idLista);
			String search = ServletUtils.getSearchFromSession(ricerca, idLista);

			this.pd.setIndex(offset);
			this.pd.setPageSize(limit);
			this.pd.setNumEntries(ricerca.getNumEntries(idLista));

			// setto la barra del titolo
			List<Parameter> lstParam = new ArrayList<Parameter>();
			if(useIdSogg){
				lstParam.add(new Parameter(SoggettiCostanti.LABEL_SOGGETTI, null));
				lstParam.add(new Parameter(Costanti.PAGE_DATA_TITLE_LABEL_ELENCO, SoggettiCostanti.SERVLET_NAME_SOGGETTI_LIST));

				if(search.equals("")){
					this.pd.setSearchDescription("");
					lstParam.add(new Parameter(PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_PORTE_APPLICATIVE_DI + tmpTitle,null));
				}else{
					lstParam.add(new Parameter(PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_PORTE_APPLICATIVE_DI + tmpTitle,
							PorteApplicativeCostanti.SERVLET_NAME_PORTE_APPLICATIVE_LIST ,
							new Parameter( PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_SOGGETTO, id)));
					lstParam.add(new Parameter(PorteApplicativeCostanti.LABEL_PORTE_APPLICATIVE_RISULTATI_RICERCA, null));
				}
			}else {
				lstParam.add(new Parameter(PorteApplicativeCostanti.LABEL_PORTE_APPLICATIVE, null));


				if(search.equals("")){
					this.pd.setSearchDescription("");
					lstParam.add(new Parameter(Costanti.PAGE_DATA_TITLE_LABEL_ELENCO,null));
				}
				else{
					lstParam.add(new Parameter(Costanti.PAGE_DATA_TITLE_LABEL_ELENCO, PorteApplicativeCostanti.SERVLET_NAME_PORTE_APPLICATIVE_LIST));
					lstParam.add(new Parameter(PorteApplicativeCostanti.LABEL_PORTE_APPLICATIVE_RISULTATI_RICERCA, null));

				}
			}

			// setto la barra del titolo
			ServletUtils.setPageDataTitle(this.pd, lstParam.toArray(new Parameter[lstParam.size()]));

			// controllo eventuali risultati ricerca
			if (!search.equals("")) {
				this.pd.setSearch("on");
				this.pd.setSearchDescription("Porte Applicative contenenti la stringa '" + search + "'");
			}

			List<String> listaLabel = new ArrayList<String>();

			listaLabel.add(PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_NOME);
			listaLabel.add(PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_DESCRIZIONE);
			if(isModalitaAvanzata){
				listaLabel.add(PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_SERVIZI_APPLICATIVI);
			}
			listaLabel.add(PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_MESSAGE_SECURITY);
			//if(isModalitaAvanzata){
			listaLabel.add(PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_MTOM);
			//}
			listaLabel.add(PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_CORRELAZIONE_APPLICATIVA_BR_RICHIESTA);
			listaLabel.add(PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_CORRELAZIONE_APPLICATIVA_BR_RISPOSTA);
			if(isModalitaAvanzata)
				listaLabel.add(PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_PROTOCOL_PROPERTIES);
			if(this.core.isRegistroServiziLocale())
				listaLabel.add(PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_SERVIZIO);
			if(extendedServletList!=null && extendedServletList.showExtendedInfo(this.request, this.session)){
				listaLabel.add(extendedServletList.getListTitle(this));
			}
			
			String[] labels = listaLabel.toArray(new String[listaLabel.size()]);
			this.pd.setLabels(labels);

			// preparo i dati
			Vector<Vector<DataElement>> dati = new Vector<Vector<DataElement>>();

			if (lista != null) {
				Iterator<PortaApplicativa> it = lista.iterator();
				while (it.hasNext()) {
					PortaApplicativa pa = it.next();

					Vector<DataElement> e = new Vector<DataElement>();

					Parameter pId = new Parameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_NOME_PORTA, pa.getNome());
					Parameter pIdSogg = new Parameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_SOGGETTO, pa.getIdSoggetto() + "");
					Parameter pIdPorta = new Parameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID, ""+pa.getId());
					Parameter pIdNome = new Parameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_NOME, ""+pa.getNome());


					DataElement de = new DataElement();
					de.setType(DataElementType.HIDDEN);
					de.setValue("" + pa.getId());
					e.addElement(de);

					de = new DataElement();
					de.setUrl(PorteApplicativeCostanti.SERVLET_NAME_PORTE_APPLICATIVE_CHANGE, pIdSogg, pId, pIdPorta);
					de.setValue(pa.getNome());
					de.setIdToRemove(pa.getId().toString());
					e.addElement(de);

					de = new DataElement();
					de.setValue(pa.getDescrizione());
					e.addElement(de);

					pId = new Parameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID, ""+pa.getId());

					if(isModalitaAvanzata){
						de = new DataElement();
						de.setUrl(PorteApplicativeCostanti.SERVLET_NAME_PORTE_APPLICATIVE_SERVIZIO_APPLICATIVO_LIST, pIdSogg, pId);
						if (contaListe) {
							int numSA = pa.sizeServizioApplicativoList();
							ServletUtils.setDataElementVisualizzaLabel(de,new Long(numSA));
						} else
							ServletUtils.setDataElementVisualizzaLabel(de);
						e.addElement(de);
					}

					de = new DataElement();
					de.setUrl(PorteApplicativeCostanti.SERVLET_NAME_PORTE_APPLICATIVE_MESSAGE_SECURITY, pIdSogg, pId);
					de.setValue(pa.getStatoMessageSecurity());
					e.addElement(de);
					
					//if(isModalitaAvanzata){
					de = new DataElement();
					de.setUrl( 
							PorteApplicativeCostanti.SERVLET_NAME_PORTE_APPLICATIVE_MTOM,pIdPorta, pIdSogg);

					boolean isMTOMAbilitatoReq = false;
					boolean isMTOMAbilitatoRes= false;
					if(pa.getMtomProcessor()!= null){
						if(pa.getMtomProcessor().getRequestFlow() != null){
							if(pa.getMtomProcessor().getRequestFlow().getMode() != null){
								MTOMProcessorType mode = pa.getMtomProcessor().getRequestFlow().getMode();
								if(!mode.equals(MTOMProcessorType.DISABLE))
									isMTOMAbilitatoReq = true;
							}
						}

						if(pa.getMtomProcessor().getResponseFlow() != null){
							if(pa.getMtomProcessor().getResponseFlow().getMode() != null){
								MTOMProcessorType mode = pa.getMtomProcessor().getResponseFlow().getMode();
								if(!mode.equals(MTOMProcessorType.DISABLE))
									isMTOMAbilitatoRes = true;
							}
						}
					}

					if(isMTOMAbilitatoReq || isMTOMAbilitatoRes)
						de.setValue(PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_MTOM_ABILITATO);
					else 
						de.setValue(PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_MTOM_DISABILITATO);
					e.addElement(de);
					//}

					de = new DataElement();
					de.setUrl(PorteApplicativeCostanti.SERVLET_NAME_PORTE_APPLICATIVE_CORRELAZIONE_APPLICATIVA_REQUEST_LIST, pIdSogg, pId, pIdNome);
					if (contaListe) {
						int numCorrelazione = 0;
						if (pa.getCorrelazioneApplicativa() != null)
							numCorrelazione = pa.getCorrelazioneApplicativa().sizeElementoList();
						ServletUtils.setDataElementVisualizzaLabel(de,new Long(numCorrelazione));
					} else
						ServletUtils.setDataElementVisualizzaLabel(de);
					e.addElement(de);

					de = new DataElement();
					de.setUrl(PorteApplicativeCostanti.SERVLET_NAME_PORTE_APPLICATIVE_CORRELAZIONE_APPLICATIVA_RESPONSE_LIST, pIdSogg, pId, pIdNome);
					if (contaListe) {
						int numCorrelazione = 0;
						if (pa.getCorrelazioneApplicativaRisposta() != null)
							numCorrelazione = pa.getCorrelazioneApplicativaRisposta().sizeElementoList();
						ServletUtils.setDataElementVisualizzaLabel(de,new Long(numCorrelazione));
					} else
						ServletUtils.setDataElementVisualizzaLabel(de);
					e.addElement(de);

					if(isModalitaAvanzata){
						de = new DataElement();
						de.setUrl(PorteApplicativeCostanti.SERVLET_NAME_PORTE_APPLICATIVE_PROPRIETA_PROTOCOLLO_LIST, pIdSogg, pId );
						if (contaListe) {
							int numProp = pa.sizeProprietaProtocolloList();
							ServletUtils.setDataElementVisualizzaLabel(de,new Long(numProp));
						} else
							ServletUtils.setDataElementVisualizzaLabel(de);
						e.addElement(de);
					}

					if(this.core.isRegistroServiziLocale()){
						/*
						 * Visualizzo SoggettoErogatore/Servizio I soggetti
						 * erogatori possono essere identificati con due casi 1. PA
						 * che hanno soggetto proprietario, servizio e non hanno
						 * definito il soggetto virtuale. In questo caso il soggetto
						 * erogatore e' il soggetto proprietario. 2. PA che hanno un
						 * soggetto virtuale. In questo caso il soggetto erogatore
						 * e' il soggetto virtuale
						 */
						int idSoggEr = 0;
						PortaApplicativaSoggettoVirtuale pasv = pa.getSoggettoVirtuale();
						if (pasv != null)
							idSoggEr = pasv.getId().intValue();
						else
							idSoggEr = pa.getIdSoggetto().intValue();
						PortaApplicativaServizio pas = pa.getServizio();
						int idServ = pas.getId().intValue();
						Soggetto soggEr = null;
						AccordoServizioParteSpecifica asps = null;
						Servizio servizio = null;
						try {
							soggEr = this.soggettiCore.getSoggettoRegistro(idSoggEr);
							asps = this.apsCore.getAccordoServizioParteSpecifica(idServ);
							servizio = asps.getServizio();
						} catch (DriverRegistroServiziNotFound drsnf) {
							// ok
						} catch (DriverRegistroServiziException drse) {
							// ok
						}
						String tmpAz = "";
						PortaApplicativaAzione paa = pa.getAzione();
						if (paa != null)
							tmpAz = "-" + paa.getNome();
						de = new DataElement();
						Parameter pId2 = new Parameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_ID, ""+ idServ);
						Parameter pNomeServizio = new Parameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_NOME_SERVIZIO, servizio.getNome());
						Parameter pTipoServizio = new Parameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_TIPO_SERVIZIO, servizio.getTipo());

						de.setUrl(AccordiServizioParteSpecificaCostanti.SERVLET_NAME_APS_CHANGE, 
								pId2, pNomeServizio, pTipoServizio);
						try {
							de.setValue(soggEr.getTipo() + "/" + soggEr.getNome() + "-" + servizio.getTipo() + "/" + servizio.getNome() + tmpAz);
						} catch (Exception eDE) {
							String tipoSE = null;
							String nomeSE = null;
							String tipoSERV = null;
							String nomeSERV = null;
							if (soggEr != null) {
								tipoSE = soggEr.getTipo();
								nomeSE = soggEr.getNome();
							}
							if (servizio != null) {
								tipoSERV = servizio.getTipo();
								nomeSERV = servizio.getNome();
							}
							String v = tipoSE + "/" + nomeSE + "-" + tipoSERV + "/" + nomeSERV;
							if (tmpAz != null)
								v = v + tmpAz;
							de.setValue(v);
						}
						e.addElement(de);
					}

					
					if(extendedServletList!=null && extendedServletList.showExtendedInfo(this.request, this.session)){
						de = new DataElement();
						de.setUrl(PorteApplicativeCostanti.SERVLET_NAME_PORTE_APPLICATIVE_EXTENDED_LIST,
								pId,pIdNome,pIdPorta, pIdSogg
								);
						if (contaListe) {
							int numExtended = extendedServletList.sizeList(pa);
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

	// Prepara la lista di  properties delle porte applicative
	public void preparePorteAppPropList(String nomePorta, ISearch ricerca, List<ProprietaProtocollo> lista)
			throws Exception {
		try {
			// prelevo il flag che mi dice da quale pagina ho acceduto la sezione delle porte delegate
			Boolean useIdSogg= ServletUtils.getBooleanAttributeFromSession(PorteApplicativeCostanti.ATTRIBUTO_PORTE_APPLICATIVE_USA_ID_SOGGETTO , this.session);

			String idPorta = this.request.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID);
			String idsogg = this.request.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_SOGGETTO);

			ServletUtils.addListElementIntoSession(this.session,  PorteApplicativeCostanti.OBJECT_NAME_PORTE_APPLICATIVE_PROPRIETA_PROTOCOLLO,
					new Parameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID, idPorta),
					new Parameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_SOGGETTO, idsogg));

			int idLista = Liste.PORTE_APPLICATIVE_PROP;
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

			PortaApplicativa myPA = this.porteApplicativeCore.getPortaApplicativa(Integer.parseInt(idPorta));
			String idporta = myPA.getNome();

			// setto la barra del titolo
			List<Parameter> lstParam = new ArrayList<Parameter>();


			if(useIdSogg){
				lstParam.add(new Parameter(SoggettiCostanti.LABEL_SOGGETTI, null));
				lstParam.add(new Parameter(Costanti.PAGE_DATA_TITLE_LABEL_ELENCO, SoggettiCostanti.SERVLET_NAME_SOGGETTI_LIST));
				lstParam.add(new Parameter(PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_PORTE_APPLICATIVE_DI + tmpTitle,
						PorteApplicativeCostanti.SERVLET_NAME_PORTE_APPLICATIVE_LIST ,
						new Parameter( PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_SOGGETTO, idsogg)));
			}else{
				lstParam.add(new Parameter(PorteApplicativeCostanti.LABEL_PORTE_APPLICATIVE, null));
				lstParam.add(new Parameter(Costanti.PAGE_DATA_TITLE_LABEL_ELENCO, PorteApplicativeCostanti.SERVLET_NAME_PORTE_APPLICATIVE_LIST));

			}

			if(search.equals("")){
				this.pd.setSearchDescription("");
				lstParam.add(new Parameter(PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_PROTOCOL_PROPERTIES_DI + idporta,null));
			}else{
				lstParam.add(new Parameter(PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_PROTOCOL_PROPERTIES_DI + idporta,
						PorteApplicativeCostanti.SERVLET_NAME_PORTE_APPLICATIVE_PROPRIETA_PROTOCOLLO_LIST ,
						new Parameter( PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID, idPorta),
						new Parameter( PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_SOGGETTO, idsogg)));
				lstParam.add(new Parameter(PorteApplicativeCostanti.LABEL_PORTE_APPLICATIVE_RISULTATI_RICERCA, null));
			}

			// setto la barra del titolo
			ServletUtils.setPageDataTitle(this.pd, lstParam.toArray(new Parameter[lstParam.size()]));

			// controllo eventuali risultati ricerca
			if (!search.equals("")) {
				this.pd.setSearch("on");
				this.pd.setSearchDescription("Propriet&agrave; protocollo contenenti la stringa '" + search + "'");
			}

			// setto le label delle colonne
			String valueLabel = PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_VALORE;
			if(this.core.isShowSelectList_PA_ProtocolProperties()){
				valueLabel = PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_KEYWORD;
			}
			String[] labels = { PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_NOME, valueLabel };
			this.pd.setLabels(labels);

			// preparo i dati
			Vector<Vector<DataElement>> dati = new Vector<Vector<DataElement>>();

			if (lista != null) {
				Iterator<ProprietaProtocollo> it = lista.iterator();
				while (it.hasNext()) {
					ProprietaProtocollo ssp = it.next();

					Vector<DataElement> e = new Vector<DataElement>();

					DataElement de = new DataElement();
					de.setUrl(PorteApplicativeCostanti.SERVLET_NAME_PORTE_APPLICATIVE_PROPRIETA_PROTOCOLLO_CHANGE,
							new Parameter( PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID, idPorta),
							new Parameter( PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_SOGGETTO, idsogg),
							new Parameter( PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_NOME, ssp.getNome()));
					de.setValue(ssp.getNome());
					de.setIdToRemove(ssp.getNome());
					e.addElement(de);

					de = new DataElement();
					if(ssp.getValore()!=null)
						de.setValue(ssp.getValore().toString());
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


	// Prepara la lista di sil delle porte applicative
	public void preparePorteAppServizioApplicativoList(String nomePorta, ISearch ricerca, List<ServizioApplicativo> lista)
			throws Exception {
		try {

			// prelevo il flag che mi dice da quale pagina ho acceduto la sezione delle porte delegate
			Boolean useIdSogg= ServletUtils.getBooleanAttributeFromSession(PorteApplicativeCostanti.ATTRIBUTO_PORTE_APPLICATIVE_USA_ID_SOGGETTO , this.session);

			String idPorta = this.request.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID);
			String idsogg = this.request.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_SOGGETTO);

			ServletUtils.addListElementIntoSession(this.session,  PorteApplicativeCostanti.OBJECT_NAME_PORTE_APPLICATIVE_SERVIZIO_APPLICATIVO,
					new Parameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID, idPorta),
					new Parameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_SOGGETTO, idsogg));

			int idLista = Liste.PORTE_APPLICATIVE_SERVIZIO_APPLICATIVO;
			int limit = ricerca.getPageSize(idLista);
			int offset = ricerca.getIndexIniziale(idLista);
			String search = ServletUtils.getSearchFromSession(ricerca, idLista);

			this.pd.setIndex(offset);
			this.pd.setPageSize(limit);
			this.pd.setNumEntries(ricerca.getNumEntries(idLista));

			String tmpTitle = null;
			if(this.core.isRegistroServiziLocale()){
				Soggetto mySogg = this.soggettiCore.getSoggettoRegistro(Integer.parseInt(idsogg));
				tmpTitle = mySogg.getTipo() + "/" + mySogg.getNome();
			}
			else{
				org.openspcoop2.core.config.Soggetto mySogg = this.soggettiCore.getSoggetto(Integer.parseInt(idsogg));
				tmpTitle = mySogg.getTipo() + "/" + mySogg.getNome();
			}

			PortaApplicativa myPA = this.porteApplicativeCore.getPortaApplicativa(Integer.parseInt(idPorta));
			String idporta = myPA.getNome();

			// setto la barra del titolo
			List<Parameter> lstParam = new ArrayList<Parameter>();

			if(useIdSogg){
				lstParam.add(new Parameter(SoggettiCostanti.LABEL_SOGGETTI, null));
				lstParam.add(new Parameter(Costanti.PAGE_DATA_TITLE_LABEL_ELENCO, SoggettiCostanti.SERVLET_NAME_SOGGETTI_LIST));
				lstParam.add(new Parameter(PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_PORTE_APPLICATIVE_DI + tmpTitle,
						PorteApplicativeCostanti.SERVLET_NAME_PORTE_APPLICATIVE_LIST ,
						new Parameter( PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_SOGGETTO, idsogg)));
			}else{
				lstParam.add(new Parameter(PorteApplicativeCostanti.LABEL_PORTE_APPLICATIVE, null));
				lstParam.add(new Parameter(Costanti.PAGE_DATA_TITLE_LABEL_ELENCO, PorteApplicativeCostanti.SERVLET_NAME_PORTE_APPLICATIVE_LIST));

			}

			if(search.equals("")){
				this.pd.setSearchDescription("");
				lstParam.add(new Parameter(PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_SERVIZIO_APPLICATIVO_DI + idporta,null));
			}else{
				lstParam.add(new Parameter(PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_SERVIZIO_APPLICATIVO_DI + idporta,
						PorteApplicativeCostanti.SERVLET_NAME_PORTE_APPLICATIVE_SERVIZIO_APPLICATIVO_LIST ,
						new Parameter( PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID, idPorta),
						new Parameter( PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_SOGGETTO, idsogg)));
				lstParam.add(new Parameter(PorteApplicativeCostanti.LABEL_PORTE_APPLICATIVE_RISULTATI_RICERCA, null));
			}

			// setto la barra del titolo
			ServletUtils.setPageDataTitle(this.pd, lstParam.toArray(new Parameter[lstParam.size()]));

			// controllo eventuali risultati ricerca
			if (!search.equals("")) {
				this.pd.setSearch("on");
				this.pd.setSearchDescription("Servizi Applicativi contenenti la stringa '" + search + "'");
			}

			// setto le label delle colonne
			String[] labels = { PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_SERVIZIO_APPLICATIVO};
			this.pd.setLabels(labels);

			// preparo i dati
			Vector<Vector<DataElement>> dati = new Vector<Vector<DataElement>>();

			if (lista != null) {
				Iterator<ServizioApplicativo> it = lista.iterator();
				while (it.hasNext()) {
					ServizioApplicativo sa = it.next();

					Vector<DataElement> e = new Vector<DataElement>();

					DataElement de = new DataElement();
					de.setUrl(
							ServiziApplicativiCostanti.SERVLET_NAME_SERVIZI_APPLICATIVI_CHANGE,
							new Parameter(ServiziApplicativiCostanti.PARAMETRO_SERVIZI_APPLICATIVI_ID, ""+sa.getId()),
							new Parameter(ServiziApplicativiCostanti.PARAMETRO_SERVIZI_APPLICATIVI_PROVIDER, ""+idsogg));
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

	public void preparePorteApplicativeCorrAppList(String nomePorta, ISearch ricerca, List<CorrelazioneApplicativaElemento> lista)
			throws Exception {
		try {

			// prelevo il flag che mi dice da quale pagina ho acceduto la sezione delle porte delegate
			Boolean useIdSogg= ServletUtils.getBooleanAttributeFromSession(PorteApplicativeCostanti.ATTRIBUTO_PORTE_APPLICATIVE_USA_ID_SOGGETTO , this.session);

			String idPorta = this.request.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID);
			String idsogg = this.request.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_SOGGETTO);

			ServletUtils.addListElementIntoSession(this.session,  PorteApplicativeCostanti.OBJECT_NAME_PORTE_APPLICATIVE_CORRELAZIONE_APPLICATIVA_REQUEST,
					new Parameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID, idPorta),
					new Parameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_SOGGETTO, idsogg));

			int idLista = Liste.PORTE_APPLICATIVE_CORRELAZIONE_APPLICATIVA;
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

			PortaApplicativa myPA = this.porteApplicativeCore.getPortaApplicativa(Integer.parseInt(idPorta));
			String idporta = myPA.getNome();

			// setto la barra del titolo
			List<Parameter> lstParam = new ArrayList<Parameter>();

			if(useIdSogg){
				lstParam.add(new Parameter(SoggettiCostanti.LABEL_SOGGETTI, null));
				lstParam.add(new Parameter(Costanti.PAGE_DATA_TITLE_LABEL_ELENCO, SoggettiCostanti.SERVLET_NAME_SOGGETTI_LIST));
				lstParam.add(new Parameter(PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_PORTE_APPLICATIVE_DI + tmpTitle,
						PorteApplicativeCostanti.SERVLET_NAME_PORTE_APPLICATIVE_LIST ,
						new Parameter( PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_SOGGETTO, idsogg)));
			}else{
				lstParam.add(new Parameter(PorteApplicativeCostanti.LABEL_PORTE_APPLICATIVE, null));
				lstParam.add(new Parameter(Costanti.PAGE_DATA_TITLE_LABEL_ELENCO, PorteApplicativeCostanti.SERVLET_NAME_PORTE_APPLICATIVE_LIST));

			}
			if(search.equals("")){
				this.pd.setSearchDescription("");
				lstParam.add(new Parameter(PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_CORRELAZIONI_APPLICATIVE_DI + idporta,null));
			}else{
				lstParam.add(new Parameter(PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_CORRELAZIONI_APPLICATIVE_DI + idporta,
						PorteApplicativeCostanti.SERVLET_NAME_PORTE_APPLICATIVE_CORRELAZIONE_APPLICATIVA_REQUEST_LIST ,
						new Parameter( PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID, idPorta),
						new Parameter( PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_SOGGETTO, idsogg),
						new Parameter( PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_NOME, idporta)
						));
				lstParam.add(new Parameter(PorteApplicativeCostanti.LABEL_PORTE_APPLICATIVE_RISULTATI_RICERCA, null));
			}

			// setto la barra del titolo
			ServletUtils.setPageDataTitle(this.pd, lstParam.toArray(new Parameter[lstParam.size()]));

			// controllo eventuali risultati ricerca
			if (!search.equals("")) {
				this.pd.setSearch("on");
				this.pd.setSearchDescription("Correlazioni Applicative contenenti la stringa '" + search + "'");
			}

			// setto le label delle colonne
			String[] labels = { 
					PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_ELEMENTO_XML, 
					PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_MODALITA_IDENTIFICAZIONE
			};
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
							PorteApplicativeCostanti.SERVLET_NAME_PORTE_APPLICATIVE_CORRELAZIONE_APPLICATIVA_REQUEST_CHANGE,
							new Parameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID, idPorta),
							new Parameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_SOGGETTO, idsogg),
							new Parameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_CORRELAZIONE_APPLICATIVA, "" + cae.getId())
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


	public void preparePorteApplicativeCorrAppRispostaList(String nomePorta, ISearch ricerca, List<CorrelazioneApplicativaRispostaElemento> lista)
			throws Exception {
		try {

			// prelevo il flag che mi dice da quale pagina ho acceduto la sezione delle porte delegate
			Boolean useIdSogg= ServletUtils.getBooleanAttributeFromSession(PorteApplicativeCostanti.ATTRIBUTO_PORTE_APPLICATIVE_USA_ID_SOGGETTO , this.session);

			String idPorta = this.request.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID);
			String idsogg = this.request.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_SOGGETTO);

			ServletUtils.addListElementIntoSession(this.session,  PorteApplicativeCostanti.OBJECT_NAME_PORTE_APPLICATIVE_CORRELAZIONE_APPLICATIVA_RESPONSE,
					new Parameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID, idPorta),
					new Parameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_SOGGETTO, idsogg));

			int idLista = Liste.PORTE_APPLICATIVE_CORRELAZIONE_APPLICATIVA_RISPOSTA;
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

			PortaApplicativa myPA = this.porteApplicativeCore.getPortaApplicativa(Integer.parseInt(idPorta));
			String idporta = myPA.getNome();

			// setto la barra del titolo
			List<Parameter> lstParam = new ArrayList<Parameter>();

			if(useIdSogg){
				lstParam.add(new Parameter(SoggettiCostanti.LABEL_SOGGETTI, null));
				lstParam.add(new Parameter(Costanti.PAGE_DATA_TITLE_LABEL_ELENCO, SoggettiCostanti.SERVLET_NAME_SOGGETTI_LIST));
				lstParam.add(new Parameter(PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_PORTE_APPLICATIVE_DI + tmpTitle,
						PorteApplicativeCostanti.SERVLET_NAME_PORTE_APPLICATIVE_LIST ,
						new Parameter( PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_SOGGETTO, idsogg)));
			}else{
				lstParam.add(new Parameter(PorteApplicativeCostanti.LABEL_PORTE_APPLICATIVE, null));
				lstParam.add(new Parameter(Costanti.PAGE_DATA_TITLE_LABEL_ELENCO, PorteApplicativeCostanti.SERVLET_NAME_PORTE_APPLICATIVE_LIST));

			}
			if(search.equals("")){
				this.pd.setSearchDescription("");
				lstParam.add(new Parameter(PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_CORRELAZIONI_APPLICATIVE_RISPOSTA_DI + idporta,null));
			}else{
				lstParam.add(new Parameter(PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_CORRELAZIONI_APPLICATIVE_RISPOSTA_DI + idporta,
						PorteApplicativeCostanti.SERVLET_NAME_PORTE_APPLICATIVE_CORRELAZIONE_APPLICATIVA_RESPONSE_LIST ,
						new Parameter( PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID, idPorta),
						new Parameter( PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_SOGGETTO, idsogg),
						new Parameter( PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_NOME, idporta)
						));
				lstParam.add(new Parameter(PorteApplicativeCostanti.LABEL_PORTE_APPLICATIVE_RISULTATI_RICERCA, null));
			}

			// setto la barra del titolo
			ServletUtils.setPageDataTitle(this.pd, lstParam.toArray(new Parameter[lstParam.size()]));


			// controllo eventuali risultati ricerca
			if (!search.equals("")) {
				this.pd.setSearch("on");
				this.pd.setSearchDescription("Correlazioni Applicative per la risposta contenenti la stringa '" + search + "'");
			}

			// setto le label delle colonne
			String[] labels = {
					PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_ELEMENTO_XML, 
					PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_MODALITA_IDENTIFICAZIONE
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
							PorteApplicativeCostanti.SERVLET_NAME_PORTE_APPLICATIVE_CORRELAZIONE_APPLICATIVA_RESPONSE_CHANGE,
							new Parameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID, idPorta),
							new Parameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_SOGGETTO, idsogg),
							new Parameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_CORRELAZIONE_APPLICATIVA, cae.getId() + "")
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


	// Prepara la lista di Message-Security request-flow delle porte applicative
	public void preparePorteAppMessageSecurityRequestList(String nomePorta, ISearch ricerca, List<MessageSecurityFlowParameter> lista)
			throws Exception {
		try {
			// prelevo il flag che mi dice da quale pagina ho acceduto la sezione delle porte delegate
			Boolean useIdSogg= ServletUtils.getBooleanAttributeFromSession(PorteApplicativeCostanti.ATTRIBUTO_PORTE_APPLICATIVE_USA_ID_SOGGETTO , this.session);

			String idPorta = this.request.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID);
			String idsogg = this.request.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_SOGGETTO);
			ServletUtils.addListElementIntoSession(this.session,  PorteApplicativeCostanti.OBJECT_NAME_PORTE_APPLICATIVE_MESSAGE_SECURITY_REQUEST,
					new Parameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID, idPorta),
					new Parameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_SOGGETTO, idsogg));

			int idLista = Liste.PORTE_APPLICATIVE_MESSAGE_SECURITY_REQUEST;
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

			PortaApplicativa myPA = this.porteApplicativeCore.getPortaApplicativa(Integer.parseInt(idPorta));
			String idporta = myPA.getNome();

			// setto la barra del titolo
			List<Parameter> lstParam = new ArrayList<Parameter>();

			if(useIdSogg){
				lstParam.add(new Parameter(SoggettiCostanti.LABEL_SOGGETTI, null));
				lstParam.add(new Parameter(Costanti.PAGE_DATA_TITLE_LABEL_ELENCO, SoggettiCostanti.SERVLET_NAME_SOGGETTI_LIST));
				lstParam.add(new Parameter(PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_PORTE_APPLICATIVE_DI + tmpTitle,
						PorteApplicativeCostanti.SERVLET_NAME_PORTE_APPLICATIVE_LIST ,
						new Parameter( PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_SOGGETTO, idsogg)));
			}else{
				lstParam.add(new Parameter(PorteApplicativeCostanti.LABEL_PORTE_APPLICATIVE, null));
				lstParam.add(new Parameter(Costanti.PAGE_DATA_TITLE_LABEL_ELENCO, PorteApplicativeCostanti.SERVLET_NAME_PORTE_APPLICATIVE_LIST));

			}

			lstParam.add(new Parameter(PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_MESSAGE_SECURITY_DI + idporta,
					PorteApplicativeCostanti.SERVLET_NAME_PORTE_APPLICATIVE_MESSAGE_SECURITY ,
					new Parameter( PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID, idPorta),
					new Parameter( PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_SOGGETTO, idsogg)));
			if(search.equals("")){
				this.pd.setSearchDescription("");
				lstParam.add(new Parameter(PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_MESSAGE_SECURITY_REQUEST_FLOW_DI + idporta,null));
			}else{
				lstParam.add(new Parameter(PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_MESSAGE_SECURITY_REQUEST_FLOW_DI + idporta,
						PorteApplicativeCostanti.SERVLET_NAME_PORTE_APPLICATIVE_MESSAGE_SECURITY_REQUEST_LIST ,
						new Parameter( PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID, idPorta),
						new Parameter( PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_SOGGETTO, idsogg) 
						));
				lstParam.add(new Parameter(PorteApplicativeCostanti.LABEL_PORTE_APPLICATIVE_RISULTATI_RICERCA, null));
			}

			// setto la barra del titolo
			ServletUtils.setPageDataTitle(this.pd, lstParam.toArray(new Parameter[lstParam.size()]));

			// controllo eventuali risultati ricerca
			if (!search.equals("")) {
				this.pd.setSearch("on");
				this.pd.setSearchDescription("Message-Security request-flow contenenti la stringa '" + search + "'");
			}

			// setto le label delle colonne
			String[] labels = { 
					PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_NOME, 
					PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_VALORE
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
					de.setUrl(
							PorteApplicativeCostanti.SERVLET_NAME_PORTE_APPLICATIVE_MESSAGE_SECURITY_REQUEST_CHANGE,
							new Parameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID, idPorta),
							new Parameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_SOGGETTO, idsogg),
							new Parameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_NOME, wsrfp.getNome()));
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

	// Prepara la lista di Message-Security response-flow delle porte applicative
	public void preparePorteAppMessageSecurityResponseList(String nomePorta, ISearch ricerca, List<MessageSecurityFlowParameter> lista)
			throws Exception {
		try {
			// prelevo il flag che mi dice da quale pagina ho acceduto la sezione delle porte delegate
			Boolean useIdSogg= ServletUtils.getBooleanAttributeFromSession(PorteApplicativeCostanti.ATTRIBUTO_PORTE_APPLICATIVE_USA_ID_SOGGETTO , this.session);	

			String idPorta = this.request.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID);
			String idsogg = this.request.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_SOGGETTO);

			ServletUtils.addListElementIntoSession(this.session,  PorteApplicativeCostanti.OBJECT_NAME_PORTE_APPLICATIVE_MESSAGE_SECURITY_RESPONSE,
					new Parameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID, idPorta),
					new Parameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_SOGGETTO, idsogg));

			int idLista = Liste.PORTE_APPLICATIVE_MESSAGE_SECURITY_RESPONSE;
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

			PortaApplicativa myPA = this.porteApplicativeCore.getPortaApplicativa(Integer.parseInt(idPorta));
			String idporta = myPA.getNome();

			// setto la barra del titolo
			List<Parameter> lstParam = new ArrayList<Parameter>();
			if(useIdSogg){
				lstParam.add(new Parameter(SoggettiCostanti.LABEL_SOGGETTI, null));
				lstParam.add(new Parameter(Costanti.PAGE_DATA_TITLE_LABEL_ELENCO, SoggettiCostanti.SERVLET_NAME_SOGGETTI_LIST));
				lstParam.add(new Parameter(PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_PORTE_APPLICATIVE_DI + tmpTitle,
						PorteApplicativeCostanti.SERVLET_NAME_PORTE_APPLICATIVE_LIST ,
						new Parameter( PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_SOGGETTO, idsogg)));
			}else{
				lstParam.add(new Parameter(PorteApplicativeCostanti.LABEL_PORTE_APPLICATIVE, null));
				lstParam.add(new Parameter(Costanti.PAGE_DATA_TITLE_LABEL_ELENCO, PorteApplicativeCostanti.SERVLET_NAME_PORTE_APPLICATIVE_LIST));

			}

			lstParam.add(new Parameter(PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_MESSAGE_SECURITY_DI + idporta,
					PorteApplicativeCostanti.SERVLET_NAME_PORTE_APPLICATIVE_MESSAGE_SECURITY ,
					new Parameter( PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID, idPorta),
					new Parameter( PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_SOGGETTO, idsogg)));
			if(search.equals("")){
				this.pd.setSearchDescription("");
				lstParam.add(new Parameter(PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_MESSAGE_SECURITY_RESPONSE_FLOW_DI + idporta,null));
			}else{
				lstParam.add(new Parameter(PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_MESSAGE_SECURITY_RESPONSE_FLOW_DI + idporta,
						PorteApplicativeCostanti.SERVLET_NAME_PORTE_APPLICATIVE_MESSAGE_SECURITY_RESPONSE_LIST ,
						new Parameter( PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID, idPorta),
						new Parameter( PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_SOGGETTO, idsogg) 
						));
				lstParam.add(new Parameter(PorteApplicativeCostanti.LABEL_PORTE_APPLICATIVE_RISULTATI_RICERCA, null));
			}

			// setto la barra del titolo
			ServletUtils.setPageDataTitle(this.pd, lstParam.toArray(new Parameter[lstParam.size()]));

			// controllo eventuali risultati ricerca
			if (!search.equals("")) {
				this.pd.setSearch("on");
				this.pd.setSearchDescription("Message-Security response-flow contenenti la stringa '" + search + "'");
			}

			// setto le label delle colonne
			String[] labels = {PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_NOME, 
					PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_VALORE };
			this.pd.setLabels(labels);

			// preparo i dati
			Vector<Vector<DataElement>> dati = new Vector<Vector<DataElement>>();

			if (lista != null) {
				Iterator<MessageSecurityFlowParameter> it = lista.iterator();
				while (it.hasNext()) {
					MessageSecurityFlowParameter wsrfp = it.next();

					Vector<DataElement> e = new Vector<DataElement>();

					DataElement de = new DataElement();
					de.setUrl(
							PorteApplicativeCostanti.SERVLET_NAME_PORTE_APPLICATIVE_MESSAGE_SECURITY_RESPONSE_CHANGE,
							new Parameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID, idPorta),
							new Parameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_SOGGETTO, idsogg),
							new Parameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_NOME, wsrfp.getNome())
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

	public Vector<DataElement> addPorteApplicativeCorrelazioneApplicativeRichiestaToDati(TipoOperazione tipoOp,
			String elemxml, String mode, String pattern, String gif,
			String riusoIdMessaggio, Vector<DataElement> dati) {
		DataElement de = new DataElement();
		de.setLabel(PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_ELEMENTO_XML_BR);
		de.setType(DataElementType.TEXT_EDIT);
		de.setName(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ELEMENTO_XML);
		de.setSize(80);
		if (elemxml == null) {
			de.setValue("");
		} else {
			de.setValue(elemxml);
		}
		dati.addElement(de);

		//String[] tipoMode = { "urlBased", "contentBased", "inputBased","disabilitato" };
		String[] tipoMode = { 
				PorteApplicativeCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_APPLICATIVE_TIPO_MODE_CORRELAZIONE_URL_BASED,
				PorteApplicativeCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_APPLICATIVE_TIPO_MODE_CORRELAZIONE_CONTENT_BASED, 
				PorteApplicativeCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_APPLICATIVE_TIPO_MODE_CORRELAZIONE_INPUT_BASED, 
				PorteApplicativeCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_APPLICATIVE_TIPO_MODE_CORRELAZIONE_DISABILITATO
		};
		de = new DataElement();
		de.setLabel(PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_MODALITA_IDENTIFICAZIONE );
		de.setType(DataElementType.SELECT);
		de.setName(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_MODE);
		de.setValues(tipoMode);
		de.setSelected(mode);
		//				de.setOnChange("CambiaModeCorrAppPortaApplicativa('add')");
		de.setPostBack(true);
		dati.addElement(de);

		if (mode.equals(PorteApplicativeCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_APPLICATIVE_TIPO_MODE_CORRELAZIONE_URL_BASED) ||
				mode.equals(PorteApplicativeCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_APPLICATIVE_TIPO_MODE_CORRELAZIONE_CONTENT_BASED)
				) {
			de = new DataElement();
			de.setLabel(PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_PATTERN);
			if (pattern == null) {
				de.setValue("");
			} else {
				de.setValue(pattern);
			}
			de.setType(DataElementType.TEXT_EDIT);
			de.setName(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_PATTERN);
			de.setSize(80);
			de.setRequired(true);
			dati.addElement(de);
		}

		if(!PorteApplicativeCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_APPLICATIVE_TIPO_MODE_CORRELAZIONE_DISABILITATO.equals(mode)){
			String[] tipiGIF = { CostantiConfigurazione.BLOCCA.toString(), CostantiConfigurazione.ACCETTA.toString()};
			de = new DataElement();
			de.setLabel(PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_GESTIONE_IDENTIFICAZIONE_FALLITA);
			de.setType(DataElementType.SELECT);
			de.setName(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_GESTIONE_IDENTIFICAZIONE_FALLITA);
			de.setValues(tipiGIF);
			de.setSelected(gif);
			dati.addElement(de);

			de = new DataElement();
			de.setName(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_RIUSO_ID_MESSAGGIO);
			de.setValue(riusoIdMessaggio);
			de.setType(DataElementType.HIDDEN);
			dati.addElement(de);

		}


		return dati;
	}

	public Vector<DataElement> addPorteApplicativeCorrelazioneApplicativeRispostaToDati(TipoOperazione tipoOp,
			String elemxml, String mode, String pattern, String gif,
			Vector<DataElement> dati) {
		DataElement de = new DataElement();
		de.setLabel(PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_ELEMENTO_XML_BR);
		de.setType(DataElementType.TEXT_EDIT);
		de.setName(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ELEMENTO_XML);
		de.setSize(80);
		if (elemxml == null) {
			de.setValue("");
		} else {
			de.setValue(elemxml);
		}
		dati.addElement(de);

		String[] tipoMode = {
				PorteApplicativeCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_APPLICATIVE_TIPO_MODE_CORRELAZIONE_CONTENT_BASED,
				PorteApplicativeCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_APPLICATIVE_TIPO_MODE_CORRELAZIONE_INPUT_BASED,
				PorteApplicativeCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_APPLICATIVE_TIPO_MODE_CORRELAZIONE_DISABILITATO
		};
		de = new DataElement();
		de.setLabel(PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_MODALITA_IDENTIFICAZIONE);
		de.setType(DataElementType.SELECT);
		de.setName(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_MODE);
		de.setValues(tipoMode);
		de.setSelected(mode);
		//				de.setOnChange("CambiaModeCorrAppPortaApplicativa('add','Risposta')");
		de.setPostBack(true);
		dati.addElement(de);

		if (mode.equals(PorteApplicativeCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_APPLICATIVE_TIPO_MODE_CORRELAZIONE_URL_BASED) ||
				mode.equals(PorteApplicativeCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_APPLICATIVE_TIPO_MODE_CORRELAZIONE_CONTENT_BASED)) {
			de = new DataElement();
			de.setLabel(PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_PATTERN);
			if (pattern == null) {
				de.setValue("");
			} else {
				de.setValue(pattern);
			}
			de.setType(DataElementType.TEXT_EDIT);
			de.setName(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_PATTERN);
			de.setSize(80);
			de.setRequired(true);
			dati.addElement(de);
		}

		if(!PorteApplicativeCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_APPLICATIVE_TIPO_MODE_CORRELAZIONE_DISABILITATO.equals(mode)){
			String[] tipiGIF = { CostantiConfigurazione.BLOCCA.toString(), CostantiConfigurazione.ACCETTA.toString()};
			de = new DataElement();
			de.setLabel(PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_GESTIONE_IDENTIFICAZIONE_FALLITA);
			de.setType(DataElementType.SELECT);
			de.setName(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_GESTIONE_IDENTIFICAZIONE_FALLITA);
			de.setValues(tipiGIF);
			de.setSelected(gif);
			dati.addElement(de);

		}

		return dati;
	}

	@Override
	public int getSize() {
		return 50;
	}

	public  Vector<DataElement>  addProprietaProtocolloToDati(TipoOperazione tipoOp,
			int size,
			boolean isShowSelectList_PA_ProtocolProperties  , String nome, String valore, Vector<DataElement> dati) {

		DataElement de = new DataElement();
		de.setLabel(PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_NOME);
		de.setValue(nome);
		if(TipoOperazione.ADD.equals(tipoOp)){
			de.setType(DataElementType.TEXT_EDIT);
			de.setRequired(true);
		}
		else{
			de.setType(DataElementType.TEXT);
		}
		de.setName(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_NOME);
		de.setSize(size);
		dati.addElement(de);

		String[] values = { 

				ProprietaProtocolloValore.TIPO_MITTENTE.toString(),
				ProprietaProtocolloValore.MITTENTE.toString(),
				ProprietaProtocolloValore.IDENTIFICATIVO_PORTA_MITTENTE.toString(),

				ProprietaProtocolloValore.TIPO_DESTINATARIO.toString(),
				ProprietaProtocolloValore.DESTINATARIO.toString(),
				ProprietaProtocolloValore.IDENTIFICATIVO_PORTA_DESTINATARIO.toString(),

				ProprietaProtocolloValore.TIPO_SERVIZIO.toString(),
				ProprietaProtocolloValore.SERVIZIO.toString(),
				ProprietaProtocolloValore.VERSIONE_SERVIZIO.toString(),

				ProprietaProtocolloValore.AZIONE.toString(),

				ProprietaProtocolloValore.IDENTIFICATIVO.toString(),

				ProprietaProtocolloValore.IDENTIFICATIVO_CORRELAZIONE_APPLICATIVA.toString()
		};
		de = new DataElement();
		if(isShowSelectList_PA_ProtocolProperties){
			de.setLabel(PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_KEYWORD);
			de.setValues(values);
			de.setType(DataElementType.SELECT);
			de.setSelected(valore);
		}
		else{
			de.setLabel(CostantiControlStation.LABEL_PARAMETRO_VALORE);
			de.setType(DataElementType.TEXT_EDIT);
			de.setRequired(true);
		}
		de.setName(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_VALORE);
		de.setSize(size);
		dati.addElement(de);

		return dati;
	}

	// Prepara la lista di MTOM request-flow delle porte applicative
	public void preparePorteApplicativeMTOMRequestList(String nomePorta, ISearch ricerca, List<MtomProcessorFlowParameter> lista)	throws Exception {
		try {

			// prelevo il flag che mi dice da quale pagina ho acceduto la sezione delle porte applicative
			Boolean useIdSogg= ServletUtils.getBooleanAttributeFromSession(PorteApplicativeCostanti.ATTRIBUTO_PORTE_APPLICATIVE_USA_ID_SOGGETTO, this.session);


			String id = this.request.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID);
			String idsogg = this.request.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_SOGGETTO);

			ServletUtils.addListElementIntoSession(this.session, PorteApplicativeCostanti.OBJECT_NAME_PORTE_APPLICATIVE_MTOM_REQUEST,
					new Parameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID, id),
					new Parameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_SOGGETTO, idsogg));

			int idLista = Liste.PORTE_APPLICATIVE_MTOM_REQUEST;
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

			PortaApplicativa myPA = this.porteApplicativeCore.getPortaApplicativa(Integer.parseInt(id));
			String idporta = myPA.getNome();

			// setto la barra del titolo
			List<Parameter> lstParam = new ArrayList<Parameter>();

			if(useIdSogg){
				lstParam.add(new Parameter(PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_SOGGETTI, null));
				lstParam.add(new Parameter(Costanti.PAGE_DATA_TITLE_LABEL_ELENCO, SoggettiCostanti.SERVLET_NAME_SOGGETTI_LIST));
				lstParam.add(new Parameter(PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_PORTE_APPLICATIVE_DI + tmpTitle,
						PorteApplicativeCostanti.SERVLET_NAME_PORTE_APPLICATIVE_LIST,
						new Parameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_SOGGETTO,idsogg)
						));
			}else {
				lstParam.add(new Parameter(PorteApplicativeCostanti.LABEL_PORTE_APPLICATIVE, null));
				lstParam.add(new Parameter(Costanti.PAGE_DATA_TITLE_LABEL_ELENCO, PorteApplicativeCostanti.SERVLET_NAME_PORTE_APPLICATIVE_LIST));
			}	

			lstParam.add(new Parameter(PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_MTOM_DI + idporta,
					PorteApplicativeCostanti.SERVLET_NAME_PORTE_APPLICATIVE_MTOM,
					new Parameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID,id),
					new Parameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_SOGGETTO,idsogg)
					));
			if(search.equals("")){
				this.pd.setSearchDescription("");
				lstParam.add(new Parameter(PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_MTOM_REQUEST_FLOW_DI + idporta,null));
			}
			else{
				lstParam.add(new Parameter(PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_MTOM_REQUEST_FLOW_DI + idporta,
						PorteApplicativeCostanti.SERVLET_NAME_PORTE_APPLICATIVE_MTOM_REQUEST_LIST,
						new Parameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID,id),
						new Parameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_SOGGETTO,idsogg)
						));
				lstParam.add(new Parameter(PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_RISULTATI_RICERCA, null));
			}


			ServletUtils.setPageDataTitle(this.pd, lstParam.toArray(new Parameter[lstParam.size()]));

			// controllo eventuali risultati ricerca
			if (!search.equals("")) {
				this.pd.setSearch("on");
				this.pd.setSearchDescription("MTOM request-flow contenenti la stringa '" + search + "'");
			}

			// setto le label delle colonne
			String[] labels = {
					PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_NOME,
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
					de.setUrl(PorteApplicativeCostanti.SERVLET_NAME_PORTE_APPLICATIVE_MTOM_REQUEST_CHANGE ,
							new Parameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID,id),
							new Parameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_SOGGETTO,idsogg),
							new Parameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_NOME, parametro.getNome())
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


	// Prepara la lista di MTOM response-flow delle porte applicative
	public void preparePorteApplicativeMTOMResponseList(String nomePorta, ISearch ricerca,
			List<MtomProcessorFlowParameter> lista)
					throws Exception {
		try {
			String id = this.request.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID);
			String idsogg = this.request.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_SOGGETTO);

			// prelevo il flag che mi dice da quale pagina ho acceduto la sezione delle porte applicative
			Boolean useIdSogg= ServletUtils.getBooleanAttributeFromSession(PorteApplicativeCostanti.ATTRIBUTO_PORTE_APPLICATIVE_USA_ID_SOGGETTO, this.session);

			ServletUtils.addListElementIntoSession(this.session, PorteApplicativeCostanti.OBJECT_NAME_PORTE_APPLICATIVE_MTOM_RESPONSE, 
					new Parameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID, id),
					new Parameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_SOGGETTO, idsogg));

			int idLista = Liste.PORTE_APPLICATIVE_MTOM_RESPONSE;
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

			PortaApplicativa myPA = this.porteApplicativeCore.getPortaApplicativa(Integer.parseInt(id));
			String idporta = myPA.getNome();

			// setto la barra del titolo
			List<Parameter> lstParam = new ArrayList<Parameter>();
			if(useIdSogg){
				lstParam.add(new Parameter(PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_SOGGETTI, null));
				lstParam.add(new Parameter(Costanti.PAGE_DATA_TITLE_LABEL_ELENCO, SoggettiCostanti.SERVLET_NAME_SOGGETTI_LIST));
				lstParam.add(new Parameter(PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_PORTE_APPLICATIVE_DI + tmpTitle,
						PorteApplicativeCostanti.SERVLET_NAME_PORTE_APPLICATIVE_LIST,
						new Parameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_SOGGETTO,idsogg)
						));

			}else {
				lstParam.add(new Parameter(PorteApplicativeCostanti.LABEL_PORTE_APPLICATIVE, null));
				lstParam.add(new Parameter(Costanti.PAGE_DATA_TITLE_LABEL_ELENCO, PorteApplicativeCostanti.SERVLET_NAME_PORTE_APPLICATIVE_LIST));
			}


			lstParam.add(new Parameter(PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_MTOM_DI + idporta,
					PorteApplicativeCostanti.SERVLET_NAME_PORTE_APPLICATIVE_MTOM,
					new Parameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID,id),
					new Parameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_SOGGETTO,idsogg)
					));

			if(search.equals("")){
				this.pd.setSearchDescription("");
				lstParam.add(new Parameter(PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_MTOM_RESPONSE_FLOW_DI + idporta,null));
			}
			else{
				lstParam.add(new Parameter(PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_MTOM_RESPONSE_FLOW_DI + idporta,
						PorteApplicativeCostanti.SERVLET_NAME_PORTE_APPLICATIVE_MTOM_RESPONSE_LIST,
						new Parameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID,id),
						new Parameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_SOGGETTO,idsogg)
						));
				lstParam.add(new Parameter(PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_RISULTATI_RICERCA, null));

			}

			ServletUtils.setPageDataTitle(this.pd, lstParam.toArray(new Parameter[lstParam.size()]));

			// controllo eventuali risultati ricerca
			if (!search.equals("")) {
				this.pd.setSearch("on");
				this.pd.setSearchDescription("MTOM response-flow contenenti la stringa '" + search + "'");
			}

			// setto le label delle colonne
			String[] labels = { 
					PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_NOME,
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
					de.setUrl(PorteApplicativeCostanti.SERVLET_NAME_PORTE_APPLICATIVE_MTOM_RESPONSE_CHANGE,
							new Parameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID,id),
							new Parameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_SOGGETTO,idsogg),
							new Parameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_NOME, wsrfp.getNome())
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
