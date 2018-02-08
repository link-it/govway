/*
 * OpenSPCoop - Customizable API Gateway 
 * http://www.openspcoop2.org
 * 
 * Copyright (c) 2005-2018 Link.it srl (http://link.it). 
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
package org.openspcoop2.web.ctrlstat.servlet.pa;

import java.text.MessageFormat;
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
import org.openspcoop2.core.config.PortaApplicativaAutorizzazioneSoggetti;
import org.openspcoop2.core.config.PortaApplicativaAutorizzazioneSoggetto;
import org.openspcoop2.core.config.PortaApplicativaAzione;
import org.openspcoop2.core.config.PortaApplicativaProprietaIntegrazioneProtocollo;
import org.openspcoop2.core.config.PortaApplicativaServizio;
import org.openspcoop2.core.config.PortaApplicativaServizioApplicativo;
import org.openspcoop2.core.config.PortaApplicativaSoggettoVirtuale;
import org.openspcoop2.core.config.ServizioApplicativo;
import org.openspcoop2.core.config.constants.CostantiConfigurazione;
import org.openspcoop2.core.config.constants.MTOMProcessorType;
import org.openspcoop2.core.config.constants.ProprietaProtocolloValore;
import org.openspcoop2.core.config.constants.TipoAutenticazione;
import org.openspcoop2.core.config.constants.TipoAutorizzazione;
import org.openspcoop2.core.id.IDPortaApplicativa;
import org.openspcoop2.core.id.IDServizio;
import org.openspcoop2.core.id.IDServizioApplicativo;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.core.registry.AccordoServizioParteComune;
import org.openspcoop2.core.registry.AccordoServizioParteSpecifica;
import org.openspcoop2.core.registry.Fruitore;
import org.openspcoop2.core.registry.Soggetto;
import org.openspcoop2.core.registry.constants.CostantiRegistroServizi;
import org.openspcoop2.core.registry.driver.DriverRegistroServiziException;
import org.openspcoop2.core.registry.driver.DriverRegistroServiziNotFound;
import org.openspcoop2.core.registry.driver.IDServizioFactory;
import org.openspcoop2.message.constants.ServiceBinding;
import org.openspcoop2.protocol.sdk.constants.FunzionalitaProtocollo;
import org.openspcoop2.web.ctrlstat.core.Search;
import org.openspcoop2.web.ctrlstat.costanti.CostantiControlStation;
import org.openspcoop2.web.ctrlstat.plugins.IExtendedListServlet;
import org.openspcoop2.web.ctrlstat.servlet.ConsoleHelper;
import org.openspcoop2.web.ctrlstat.servlet.apc.AccordiServizioParteComuneCostanti;
import org.openspcoop2.web.ctrlstat.servlet.apc.AccordiServizioParteComuneUtilities;
import org.openspcoop2.web.ctrlstat.servlet.aps.AccordiServizioParteSpecificaCore;
import org.openspcoop2.web.ctrlstat.servlet.aps.AccordiServizioParteSpecificaCostanti;
import org.openspcoop2.web.ctrlstat.servlet.sa.ServiziApplicativiCostanti;
import org.openspcoop2.web.ctrlstat.servlet.soggetti.SoggettiCostanti;
import org.openspcoop2.web.lib.mvc.Costanti;
import org.openspcoop2.web.lib.mvc.DataElement;
import org.openspcoop2.web.lib.mvc.DataElementType;
import org.openspcoop2.web.lib.mvc.PageData;
import org.openspcoop2.web.lib.mvc.Parameter;
import org.openspcoop2.web.lib.mvc.ServletUtils;
import org.openspcoop2.web.lib.mvc.TipoOperazione;

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
	public boolean porteAppCheckData(TipoOperazione tipoOp, String oldNomePA, boolean isSupportatoAutenticazione) throws Exception {
		try {
			String idPorta = this.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID);
			String nomePorta = this.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_NOME_PORTA);
			String idsogg = this.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_SOGGETTO);
			int soggInt = Integer.parseInt(idsogg);
			// String descr = this.getParameter("descr");
			String soggvirt = this.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_SOGGETTO_VIRTUALE);
			if (soggvirt == null) {
				soggvirt = "-";
			}
			IDSoggetto idSoggettoVirtuale = null;
			if (!soggvirt.equals("-")){
				idSoggettoVirtuale = new IDSoggetto(soggvirt.split("/")[0],soggvirt.split("/")[1]);
			}
			String servizio = this.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_SERVIZIO);
			if (servizio == null) {
				servizio = "";
			}
			String azione = this.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_AZIONE);
			if (azione == null) {
				azione = "-";
			}
			String xsd = this.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_XSD);

			String behaviour = this.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_BEHAVIOUR);
			
			String autenticazione = this.getParameter(CostantiControlStation.PARAMETRO_PORTE_AUTENTICAZIONE);
			String autenticazioneCustom = this.getParameter(CostantiControlStation.PARAMETRO_PORTE_AUTENTICAZIONE_CUSTOM);
			String autenticazioneOpzionale = this.getParameter(CostantiControlStation.PARAMETRO_PORTE_AUTENTICAZIONE_OPZIONALE);
			String autorizzazione = this.getParameter(CostantiControlStation.PARAMETRO_PORTE_AUTORIZZAZIONE);
			String autorizzazioneCustom = this.getParameter(CostantiControlStation.PARAMETRO_PORTE_AUTORIZZAZIONE_CUSTOM);
			String autorizzazioneAutenticati = this.getParameter(CostantiControlStation.PARAMETRO_PORTE_AUTORIZZAZIONE_AUTENTICAZIONE);
			String autorizzazioneRuoli = this.getParameter(CostantiControlStation.PARAMETRO_PORTE_AUTORIZZAZIONE_RUOLI);
			String autorizzazioneRuoliTipologia = this.getParameter(CostantiControlStation.PARAMETRO_RUOLO_TIPOLOGIA);
			String ruoloMatch = this.getParameter(CostantiControlStation.PARAMETRO_RUOLO_MATCH);
			
			// Campi obbligatori
			if (nomePorta.equals("") || soggvirt.equals("") || servizio.equals("")) {
				String tmpElenco = "";
				if (nomePorta.equals("")) {
					tmpElenco = PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_NOME;
				}
				if (soggvirt.equals("")) {
					if (tmpElenco.equals("")) {
						tmpElenco = PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_SOGGETTO_VIRTUALE;
					} else {
						tmpElenco = tmpElenco + ", " + PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_SOGGETTO_VIRTUALE;
					}
				}
				if (servizio.equals("")) {
					if (tmpElenco.equals("")) {
						tmpElenco = PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_SERVIZIO;
					} else {
						tmpElenco = tmpElenco + ", " + PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_SERVIZIO;
					}
				}
				this.pd.setMessage(MessageFormat.format(PorteApplicativeCostanti.MESSAGGIO_ERRRORE_DATI_INCOMPLETI_E_NECESSARIO_INDICARE_XX, tmpElenco));
				return false;
			}

			// Controllo che non ci siano spazi nei campi di testo
			if(behaviour!=null && !"".equals(behaviour)){
				if (behaviour.indexOf(" ") != -1 || behaviour.indexOf(",") != -1 ) {
					this.pd.setMessage(PorteApplicativeCostanti.MESSAGGIO_ERRORE_NON_INSERIRE_NE_SPAZI_NE_NEL_CAMPO_BEHAVIOUR);
					return false;
				}
			}
			
			// Controllo che non ci siano spazi nei campi di testo
			if (nomePorta.indexOf(" ") != -1) {
				this.pd.setMessage(CostantiControlStation.MESSAGGIO_ERRORE_NON_INSERIRE_SPAZI_NEI_CAMPI_DI_TESTO);
				return false;
			}
			if(this.checkIntegrationEntityName(nomePorta,PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_NOME)==false){
				return false;
			}

			// Controllo che i campi "select" abbiano uno dei valori ammessi
			if (!xsd.equals(CostantiControlStation.DEFAULT_VALUE_ABILITATO) && !xsd.equals(CostantiControlStation.DEFAULT_VALUE_DISABILITATO) && !xsd.equals(CostantiControlStation.DEFAULT_VALUE_WARNING_ONLY)) {
				this.pd.setMessage(PorteApplicativeCostanti.MESSAGGIO_ERRORE_VALIDAZIONE_XSD_DEV_ESSERE_ABILITATO_DISABILITATO_O_WARNING_ONLY);
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
					this.pd.setMessage(PorteApplicativeCostanti.MESSAGGIO_ERRORE_IL_SOGGETTO_VIRTUALE_DEVE_ESSERE_SCELTO_TRA_QUELLI_DEFINITI_NEL_PANNELLO_SOGGETTI);
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
			IDServizio idSE = null;
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
				idPA.setNome(nomePorta);
				if (TipoOperazione.ADD.equals(tipoOp)==false) {
					idPA.setNome(oldNomePA);
				}
				
				// se specificato il soggetto virtuale
				org.openspcoop2.core.config.Soggetto proprietario = null;
				Soggetto virtuale = null;
				proprietario = this.soggettiCore.getSoggetto(soggInt);
				if (!soggvirt.equals("-")) {
					virtuale = this.soggettiCore.getSoggettoRegistro(idSoggettoVirtuale);
				}
				IDSoggetto idSO = new IDSoggetto(proprietario.getTipo(), proprietario.getNome());
				String [] tmp = servizio.split(" ");
				idSE = IDServizioFactory.getInstance().getIDServizioFromValues(tmp[1].split("/")[0], tmp[1].split("/")[1], 
						idSO, Integer.parseInt(tmp[1].split("/")[2]));
				
				if (azione!=null && !"".equals(azione) && !"-".equals(azione) ) {
					idSE.setAzione(azione);
				}

				// controllo su nome e proprietario in caso di aggiunta
				// nuova pa
				// non posso inserire una porta con stesso nome e
				// proprietario
				if (TipoOperazione.ADD.equals(tipoOp)) {
					giaRegistrato = this.porteApplicativeCore.existsPortaApplicativa(idPA);

					if (giaRegistrato) {
						this.pd.setMessage("Esiste gia' una Porta Applicativa [" + nomePorta + "] appartenente al Soggetto [" + idSO.toString() + "]");
						return false;
					}

					if (!soggvirt.equals("-")) {
						IDSoggetto idSOVirt = new IDSoggetto(virtuale.getTipo(), virtuale.getNome());
						giaRegistrato = this.porteApplicativeCore.existsPortaApplicativaVirtuale(idSE, idSOVirt, true);
					} else {
						giaRegistrato = this.porteApplicativeCore.existsPortaApplicativa(idSE, true);
					}

				} else {
					PortaApplicativa pa = this.porteApplicativeCore.getPortaApplicativa(idPA);
					PortaApplicativaServizio pa_servizio_precedente = pa.getServizio();
					PortaApplicativaAzione pa_azione = pa.getAzione();

					IDServizio idServPrec = IDServizioFactory.getInstance().getIDServizioFromValues(pa_servizio_precedente.getTipo(), 
							pa_servizio_precedente.getNome(), idSO, pa_servizio_precedente.getVersione());
					if (pa_azione != null)
						idServPrec.setAzione(pa_azione.getNome());

					// se e' cambiato il servizio e/o azione
					// allora devo controllare se esistono altre porte
					// applicative associate
					if (!idSE.equals(idServPrec)) {
						// se presente soggetto virtuale
						if (!soggvirt.equals("-")) {
							IDSoggetto idSOVirt = new IDSoggetto(virtuale.getTipo(), virtuale.getNome());
							giaRegistrato = this.porteApplicativeCore.existsPortaApplicativaVirtuale(idSE, idSOVirt, true);
						} else {
							giaRegistrato = this.porteApplicativeCore.existsPortaApplicativa(idSE, true);
						}

					}
				}

				if (giaRegistrato) {
					this.pd.setMessage("Esiste gia' una Porta Applicativa per il " + "Servizio [" + idSE.getTipo() + "/" + idSE.getNome() +  "/" + idSE.getVersione()+
							"] " + "con Azione [" + (idSE.getAzione() != null ? idSE.getAzione() : "not set") + 
							"] " + "erogato dal Soggetto " + (!soggvirt.equals("-") ? "Virtuale" : "") + " [" 
							+ (!soggvirt.equals("-") ? virtuale.getTipo() + "/" + virtuale.getNome() : idSE.getSoggettoErogatore().toString()) + "]");

					// this.pd.setMessage("La porta applicativa " + id + "
					// &egrave; gi&agrave; stata registrata");
					return false;
				}

				
				if (TipoOperazione.CHANGE.equals(tipoOp)) {
					if (!nomePorta.equals(oldNomePA)) {
						IDPortaApplicativa idPANew = new IDPortaApplicativa();
						idPANew.setNome(nomePorta);
						boolean exists = this.porteApplicativeCore.existsPortaApplicativa(idPANew);
						if (exists) {
							this.pd.setMessage("Esiste gia' una Porta Applicativa con nome [" + nomePorta + "]");
							return false;
						}
					}
				}
			}
			

			// Se autenticazione = custom, nomeauth dev'essere specificato
			if (CostantiControlStation.DEFAULT_VALUE_PARAMETRO_PORTE_AUTENTICAZIONE_CUSTOM.equals(autenticazione) && 
					(autenticazioneCustom == null || autenticazioneCustom.equals(""))) {
				this.pd.setMessage("Indicare un nome per l'autenticazione '"+CostantiControlStation.DEFAULT_VALUE_PARAMETRO_PORTE_AUTENTICAZIONE_CUSTOM+"'");
				return false;
			}

			// Se autorizzazione = custom, nomeautor dev'essere specificato
			if (CostantiControlStation.DEFAULT_VALUE_PARAMETRO_PORTE_AUTORIZZAZIONE_CUSTOM.equals(autorizzazione) && 
					(autorizzazioneCustom == null || autorizzazioneCustom.equals(""))) {
				this.pd.setMessage("Indicare un nome per l'autorizzazione '"+CostantiControlStation.DEFAULT_VALUE_PARAMETRO_PORTE_AUTORIZZAZIONE_CUSTOM+"'");
				return false;
			}
			
			PortaApplicativa pa = null;
			if (TipoOperazione.CHANGE == tipoOp){
				pa = this.porteApplicativeCore.getPortaApplicativa(Long.parseLong(idPorta)); 
			}
			
			List<String> ruoli = new ArrayList<>();
			if(pa!=null && pa.getRuoli()!=null && pa.getRuoli().sizeRuoloList()>0){
				for (int i = 0; i < pa.getRuoli().sizeRuoloList(); i++) {
					ruoli.add(pa.getRuoli().getRuolo(i).getNome());
				}
			}
			
			if(this.controlloAccessiCheck(tipoOp, autenticazione, autenticazioneOpzionale, 
					autorizzazione, autorizzazioneAutenticati, autorizzazioneRuoli, 
					autorizzazioneRuoliTipologia, ruoloMatch, 
					isSupportatoAutenticazione, false, ruoli)==false){
				return false;
			}
			
			if (TipoOperazione.CHANGE == tipoOp && isSupportatoAutenticazione) {
				
				if(autenticazione!=null && autenticazione.equals(pa.getAutenticazione())==false &&
						!TipoAutenticazione.DISABILITATO.equals(autenticazione) &&
						!CostantiControlStation.DEFAULT_VALUE_PARAMETRO_PORTE_AUTENTICAZIONE_CUSTOM.equals(autenticazione)){
					AccordoServizioParteSpecifica asps = this.apsCore.getServizio(idSE);
					if(this.apsCore.filterFruitoriRispettoAutenticazione(asps)){
						boolean fruitoriCompatibili = true;
						for (int i = 0; i < asps.sizeFruitoreList(); i++) {
							Fruitore fr = asps.getFruitore(i);
							IDSoggetto idSoggetto = new IDSoggetto(fr.getTipo(), fr.getNome());
							Soggetto soggetto = this.soggettiCore.getSoggettoRegistro(idSoggetto);
							if(this.pddCore.isPddEsterna(soggetto.getPortaDominio())) {
								// Devo controllare solo i soggetti delle pddEsterne, altrimeni se registro un soggeto operativo come fruitore per fruire del servizio,
								// poi il controllo non permette la modifica
								if(soggetto.getCredenziali()==null || soggetto.getCredenziali().getTipo()==null ||
										!soggetto.getCredenziali().getTipo().equals(autenticazione)){
									fruitoriCompatibili = false;
									break;
								}
							}
						}
						if(fruitoriCompatibili == false){
							this.pd.setMessage("Non è possibile modificare il tipo di autenticazione da ["+pa.getAutenticazione()+"] a ["+autenticazione+
									"], poichè risultano associati al servizio dei fruitori con credenziali non compatibili, nella modalità di accesso, con il nuovo tipo di autenticazione");
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
			String idPorta = this.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID);
			int idInt = Integer.parseInt(idPorta);
			String idsogg = this.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_SOGGETTO);
			int soggInt = Integer.parseInt(idsogg);
			String servizioApplicativo = this.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_SERVIZIO_APPLICATIVO);
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
			IDServizioApplicativo idSAObject = new IDServizioApplicativo();
			idSAObject.setIdSoggettoProprietario(ids);
			idSAObject.setNome(servizioApplicativo);
			trovatoServizioApplicativo = this.saCore.existsServizioApplicativo(idSAObject);

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
					PortaApplicativaServizioApplicativo tmpSA = pa.getServizioApplicativo(i);
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


	// Controlla i dati dell soggetto della porta applicativa
	public boolean porteAppSoggettoCheckData(TipoOperazione tipoOp)
			throws Exception {
		try {
			String idPorta = this.request.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID);
			int idInt = Integer.parseInt(idPorta);
			String soggetto = this.request.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_SOGGETTO);
			if (soggetto == null) {
				soggetto = "";
			}

			// Campi obbligatori
			if (soggetto.equals("")) {
				this.pd.setMessage("Dati incompleti. E' necessario indicare un Soggetto");
				return false;
			}

			long idSoggetto = Integer.parseInt(soggetto);
			// Controllo che il servizioApplicativo appartenga alla lista di
			// servizioApplicativo disponibili per il soggetto
			// Prendo il nome e il tipo del soggetto
			String nomeSoggettoSelezionato = null;
			String tipoSoggettoSelezionato = null;
			if(this.core.isRegistroServiziLocale()){
				Soggetto mySogg = this.soggettiCore.getSoggettoRegistro(idSoggetto);
				nomeSoggettoSelezionato = mySogg.getNome();
				tipoSoggettoSelezionato = mySogg.getTipo();
			}else{
				org.openspcoop2.core.config.Soggetto mySogg = this.soggettiCore.getSoggetto(idSoggetto);
				nomeSoggettoSelezionato = mySogg.getNome();
				tipoSoggettoSelezionato = mySogg.getTipo();
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
				PortaApplicativaAutorizzazioneSoggetti soggetti = pa.getSoggetti() != null ? pa.getSoggetti() : new PortaApplicativaAutorizzazioneSoggetti();
				

				for (int i = 0; i < soggetti.sizeSoggettoList(); i++) {
					PortaApplicativaAutorizzazioneSoggetto tmpSoggetto = soggetti.getSoggetto(i);
					if (tipoSoggettoSelezionato.equals(tmpSoggetto.getTipo()) && nomeSoggettoSelezionato.equals(tmpSoggetto.getNome())) {
						giaRegistrato = true;
						break;
					}
				}

				if (giaRegistrato) {
					this.pd.setMessage("Il Soggetto '" + tipoSoggettoSelezionato + "/"+ nomeSoggettoSelezionato + " &egrave; gi&agrave; stato associato alla porta applicativa " + nomeporta);
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
			String idPorta = this.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID);
			int idInt = Integer.parseInt(idPorta);
			String nome = this.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_NOME);
			String valore = this.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_VALORE);

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
				this.pd.setMessage(CostantiControlStation.MESSAGGIO_ERRORE_NON_INSERIRE_SPAZI_NEI_CAMPI_DI_TESTO);
				return false;
			}

			// Se tipoOp = add, controllo che la property non sia gia'
			// stata
			// registrata per la porta applicativa
			if (tipoOp.equals(TipoOperazione.ADD)) {
				boolean giaRegistrato = false;
				PortaApplicativa pa = this.porteApplicativeCore.getPortaApplicativa(idInt);
				String nomeporta = pa.getNome();

				for (int i = 0; i < pa.sizeProprietaIntegrazioneProtocolloList(); i++) {
					PortaApplicativaProprietaIntegrazioneProtocollo tmpProp = pa.getProprietaIntegrazioneProtocollo(i);
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
			String idPorta = this.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID);
			int idInt = Integer.parseInt(idPorta);
			String nome = this.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_NOME);
			String valore = this.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_VALORE);

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
			String idPorta = this.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID);
			int idInt = Integer.parseInt(idPorta);
			String nome = this.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_NOME);
			String valore = this.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_VALORE);

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
			int numCorrApp,String scadcorr,String autorizzazioneContenuti, String protocollo,
			int numSA,	 int numRuoli, String ruoloMatch,
			String statoMessageSecurity ,String statoMTOM ,int numCorrelazioneReq , int numCorrelazioneRes, int numProprProt,String applicaMTOM,
			String behaviour,
			String[] servizioApplicativoList, String servizioApplicativo,
			String autenticazione, String autorizzazione,
			String autenticazioneOpzionale, String autenticazioneCustom, String autorizzazioneCustom,
			boolean isSupportatoAutenticazioneSoggetti,
			String autorizzazioneAutenticati,String autorizzazioneRuoli,String autorizzazioneRuoliTipologia,
			AccordoServizioParteSpecifica asps, AccordoServizioParteComune aspc, ServiceBinding serviceBinding,
			String statoPorta) throws Exception {

		Boolean contaListe = ServletUtils.getContaListeFromSession(this.session);

		// Soggetto virtuale?
		Boolean soggVirt = (Boolean) this.session.getAttribute(CostantiControlStation.SESSION_PARAMETRO_GESTIONE_SOGGETTI_VIRTUALI);
		
//		Boolean confPers = (Boolean) this.session.getAttribute(CostantiControlStation.SESSION_PARAMETRO_GESTIONE_CONFIGURAZIONI_PERSONALIZZATE);
		
		int alternativeSize = 80;

		boolean configurazioneStandardNonApplicabile = false;
		
		Parameter pIdSogg = new Parameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_SOGGETTO, idsogg);
		Parameter pIdPorta = new Parameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID, idPorta);
		Parameter pIdAsps = new Parameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_ASPS, asps.getId()+ "");
		Parameter pNomePorta = new Parameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_NOME, nomePorta);
		
		// *************** Nome/Descrizione *********************
		
		DataElement de = new DataElement();
		de.setLabel(PorteApplicativeCostanti.LABEL_PARAMETRO_TITOLO_PORTE_APPLICATIVE_DATI_GENERALI);
		de.setType(DataElementType.TITLE);
		dati.addElement(de);
		
		de = new DataElement();
		de.setLabel(PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_NOME);
		if (nomePorta == null) {
			de.setValue("");
		} else {
			de.setValue(nomePorta);
		}
		if(this.isModalitaStandard() && TipoOperazione.CHANGE.equals(tipoOp) ){
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

		List<String> statoValues = new ArrayList<>();
		statoValues.add(CostantiConfigurazione.ABILITATO.toString());
		statoValues.add(CostantiConfigurazione.DISABILITATO.toString());
		de = new DataElement();
		de.setLabel(PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_STATO_PORTA);
		de.setValues(statoValues);
		if(statoPorta==null || "".equals(statoPorta)){
			statoPorta = CostantiConfigurazione.ABILITATO.toString();
		}
		de.setSelected(statoPorta);
		de.setType(DataElementType.SELECT);
		de.setName(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_STATO_PORTA);
		dati.addElement(de);

		
		

		de = new DataElement();
		de.setLabel(PorteApplicativeCostanti.LABEL_PARAMETRO_TITOLO_PORTE_APPLICATIVE_DATI_SERVIZIO);
		de.setType(DataElementType.TITLE);
		dati.addElement(de);
		
		
		
		// *************** Soggetto Virtuale *********************
		
		if (soggVirt) {
		
			de = new DataElement();
			de.setLabel(PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_SOGGETTO_VIRTUALE);
			de.setType(DataElementType.SUBTITLE);
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
		de.setType(DataElementType.SUBTITLE);
		dati.addElement(de);

		de = new DataElement();
		de.setLabel(PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_NOME);
		de.setName(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_SERVIZIO);
		de.setPostBack(true);
		if(TipoOperazione.CHANGE.equals(tipoOp) && this.isModalitaStandard()) {
			de.setType(DataElementType.HIDDEN);
			de.setValue(servizio);
			dati.addElement(de);
			
			de = new DataElement();
			for (int i = 0; i < serviziList.length; i++) {
				if(serviziList[i]!=null && serviziList[i].equals(servizio)){
					de.setValue(serviziListLabel[i]);
					break;
				}
			}
			de.setType(DataElementType.TEXT);
			de.setLabel(PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_NOME);
			
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
			de.setType(DataElementType.SUBTITLE);
			dati.addElement(de);

			de = new DataElement();
			de.setLabel(PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_NOME);
			de.setName(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_AZIONE);
			boolean tutteAzioni = false;
			if(TipoOperazione.CHANGE.equals(tipoOp) && this.isModalitaStandard()) {
				if("-".equals(azione)){
					de.setType(DataElementType.HIDDEN);
					de.setValue(azione);
					dati.addElement(de);
					
					de = new DataElement();
					de.setValue(PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_QUALSIASI_AZIONE);
					tutteAzioni = true;
					de.setType(DataElementType.TEXT);
					de.setLabel(null);
					
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
			
			if(TipoOperazione.CHANGE.equals(tipoOp)){
				if(tutteAzioni && asps!=null && aspc!=null){
					de = new DataElement();
					de.setType(DataElementType.LINK);
					String tipoAccordo = AccordiServizioParteComuneCostanti.PARAMETRO_VALORE_APC_TIPO_ACCORDO_PARTE_COMUNE;
					if(aspc.getServizioComposto()!=null){
						tipoAccordo = AccordiServizioParteComuneCostanti.PARAMETRO_VALORE_APC_TIPO_ACCORDO_SERVIZIO_COMPOSTO;
					}
					if(asps.getPortType()!=null && !"".equals(asps.getPortType()) && !"-".equals(asps.getPortType())){
						de.setUrl(AccordiServizioParteComuneCostanti.SERVLET_NAME_APC_PORT_TYPE_OPERATIONS_LIST, 
								new Parameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_ID, aspc.getId()+""),
								new Parameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_PORT_TYPES_NOME, asps.getPortType()),
								AccordiServizioParteComuneUtilities.getParametroAccordoServizio(tipoAccordo)
								);
						org.openspcoop2.core.registry.PortType pt = null;
						for (org.openspcoop2.core.registry.PortType ptCheck : aspc.getPortTypeList()) {
							if(ptCheck.getNome().equals(asps.getPortType())){
								pt = ptCheck;
								break;
							}
						}
						if (contaListe) {
							ServletUtils.setDataElementVisualizzaLabel(de,(long)pt.sizeAzioneList());
						} else
							ServletUtils.setDataElementVisualizzaLabel(de);
					}
					else{
						de.setUrl(AccordiServizioParteComuneCostanti.SERVLET_NAME_APC_AZIONI_LIST,
								new Parameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_ID, aspc.getId()+""),
								new Parameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_NOME, aspc.getNome()),
								AccordiServizioParteComuneUtilities.getParametroAccordoServizio(tipoAccordo));
						if (contaListe) {
							ServletUtils.setDataElementVisualizzaLabel(de,(long)aspc.sizeAzioneList());
						} else
							ServletUtils.setDataElementVisualizzaLabel(de);
					}
					dati.addElement(de);
				}
			}
		}
		
		
		
		
		
		
		// *************** ServizioApplicativo Erogatore *********************
		
		if(TipoOperazione.CHANGE.equals(tipoOp)){
		
			if (this.isModalitaStandard()) {
				
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
					de.setLabel(null);
					de.setValue(CostantiControlStation.LABEL_CONFIGURAZIONE_IMPOSTATA_MODALITA_AVANZATA_SHORT_MESSAGE);
					de.setType(DataElementType.TEXT);
					configurazioneStandardNonApplicabile = true;
				}
				dati.addElement(de);
			}
			else{
				
				de = new DataElement();
				de.setType(DataElementType.TITLE);
				de.setLabel(PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_SERVIZI_APPLICATIVI_EROGATORI);
				dati.addElement(de);
				
				de = new DataElement();
				de.setType(DataElementType.LINK);

				de.setUrl(PorteApplicativeCostanti.SERVLET_NAME_PORTE_APPLICATIVE_SERVIZIO_APPLICATIVO_LIST,
						pIdSogg,pIdPorta);
				if (contaListe) {
					ServletUtils.setDataElementCustomLabel(de,PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_SERVIZI_APPLICATIVI,new Long(numSA));
				} else
					ServletUtils.setDataElementCustomLabel(de,PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_SERVIZI_APPLICATIVI);

				dati.addElement(de);
			}
		
			
		}
		
		// *************** Controllo degli Accessi *********************
	
		this.controlloAccessi(dati);
		
		// controllo accessi
		de = new DataElement();
		de.setType(DataElementType.LINK);
		de.setUrl(PorteApplicativeCostanti.SERVLET_NAME_PORTE_APPLICATIVE_CONTROLLO_ACCESSI, pIdSogg, pIdPorta, pIdAsps);
		String statoControlloAccessi = this.getLabelStatoControlloAccessi(autenticazioneCustom, autenticazioneOpzionale, autenticazioneCustom, autorizzazione, autorizzazioneContenuti,autorizzazioneCustom);
		ServletUtils.setDataElementCustomLabel(de, PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_CONTROLLO_ACCESSI, statoControlloAccessi);
		dati.addElement(de);
		
		
		
		// Pintori 29/11/2017 Gestione Accessi spostata nella servlet PorteApplicativeControlloAccessi, lascio questo codice per eventuali ripensamenti.
	/*	
		this.controlloAccessi(dati);
		
		this.controlloAccessiAutenticazione(dati, autenticazione, autenticazioneCustom, autenticazioneOpzionale, confPers, isSupportatoAutenticazioneSoggetti);
		
		String urlAutorizzazioneAutenticati = null;
		String urlAutorizzazioneRuoli = null;
		if(TipoOperazione.CHANGE.equals(tipoOp)){
			urlAutorizzazioneAutenticati = AccordiServizioParteSpecificaCostanti.SERVLET_NAME_APS_FRUITORI_LIST +"?" + 
					AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_ID + "=" + asps.getId() + "&" +
					AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_ID_SOGGETTO_EROGATORE + "=" + asps.getIdSoggetto() + "&" +
					AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_TIPO_SERVIZIO + "=" + asps.getTipo()+ "&" +
					AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_NOME_SERVIZIO + "=" + asps.getNome();
			
			urlAutorizzazioneRuoli = PorteApplicativeCostanti.SERVLET_NAME_PORTE_APPLICATIVE_RUOLI_LIST +"?" + 
					PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_SOGGETTO + "=" + idsogg + "&" +
					PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID + "=" + idPorta;
		}
		
		String servletChiamante = PorteApplicativeCostanti.SERVLET_NAME_PORTE_APPLICATIVE_ADD;
		if (tipoOp == TipoOperazione.CHANGE) {
			servletChiamante = PorteApplicativeCostanti.SERVLET_NAME_PORTE_APPLICATIVE_CHANGE;
		}
		
		int sizeFruitori = 0;
		if(asps!=null){
			sizeFruitori = asps.sizeFruitoreList();
		}
		this.controlloAccessiAutorizzazione(dati, tipoOp, servletChiamante,
				autenticazione, autorizzazione, autorizzazioneCustom, 
				autorizzazioneAutenticati, urlAutorizzazioneAutenticati, sizeFruitori, null, null,
				autorizzazioneRuoli,  urlAutorizzazioneRuoli, numRuoli, null, 
				autorizzazioneRuoliTipologia, ruoloMatch,
				confPers, isSupportatoAutenticazioneSoggetti, contaListe, false, false);
		
		this.controlloAccessiAutorizzazioneContenuti(dati, autorizzazioneContenuti);
		

		*/

		
		
		
		
		
		// *************** Integrazione *********************
		
		Vector<DataElement> deIntegrazione = new Vector<DataElement>();
				
		if (tipoOp.equals(TipoOperazione.CHANGE)) {
			de = new DataElement();
			de.setLabel(PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_METADATI);
			de.setValue(integrazione);
			de.setName(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_INTEGRAZIONE);
			de.setSize(alternativeSize);
			if(this.isModalitaStandard()){
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
		if (this.isModalitaAvanzata()) {
			de.setType(DataElementType.TEXT_EDIT);
			deIntegrazione.addElement(de);
		}
		else{
			de.setType(DataElementType.HIDDEN);
			dati.addElement(de);
		}
			
		if (tipoOp.equals(TipoOperazione.CHANGE)) {
			if(this.isModalitaAvanzata()){
				de = new DataElement();
				de.setType(DataElementType.LINK);
				de.setUrl(PorteApplicativeCostanti.SERVLET_NAME_PORTE_APPLICATIVE_PROPRIETA_PROTOCOLLO_LIST,pIdSogg,pIdPorta);

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
		boolean show = false; // aggiunto link in trattamento messaggio
		if (show && tipoOp.equals(TipoOperazione.CHANGE)) {
		
			de = new DataElement();
			de.setType(DataElementType.TITLE);
			de.setLabel(PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_CORRELAZIONE_APPLICATIVA);
			dati.addElement(de);
			
			boolean riuso = false; // riuso non abilitato nella porta applicativa
			if (numCorrApp > 0 && riuso) {
				de = new DataElement();
				de.setLabel(CostantiControlStation.LABEL_PARAMETRO_SCADENZA_CORRELAZIONE_APPLICATIVA_LABEL);
				de.setNote(CostantiControlStation.LABEL_PARAMETRO_SCADENZA_CORRELAZIONE_APPLICATIVA_NOTE);
				de.setValue(scadcorr);
				de.setType(DataElementType.TEXT_EDIT);
				de.setName(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_SCADENZA_CORRELAZIONE_APPLICATIVA);
				de.setSize(alternativeSize);
				dati.addElement(de);
			}
			
			

			de = new DataElement();
			de.setType(DataElementType.LINK);
			de.setUrl(PorteApplicativeCostanti.SERVLET_NAME_PORTE_APPLICATIVE_CORRELAZIONE_APPLICATIVA_REQUEST_LIST,pIdSogg,pIdPorta,pNomePorta);
			if (contaListe) {
				ServletUtils.setDataElementCustomLabel(de,PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_CORRELAZIONE_APPLICATIVA_RICHIESTA,new Long(numCorrelazioneReq));
			} else
				ServletUtils.setDataElementCustomLabel(de,PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_CORRELAZIONE_APPLICATIVA_RICHIESTA);

			dati.addElement(de);

			de = new DataElement();
			de.setType(DataElementType.LINK);
			de.setUrl(PorteApplicativeCostanti.SERVLET_NAME_PORTE_APPLICATIVE_CORRELAZIONE_APPLICATIVA_RESPONSE_LIST,pIdSogg,pIdPorta,pNomePorta);
			if (contaListe) {
				ServletUtils.setDataElementCustomLabel(de,PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_CORRELAZIONE_APPLICATIVA_RISPOSTA,new Long(numCorrelazioneRes));
			} else
				ServletUtils.setDataElementCustomLabel(de,PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_CORRELAZIONE_APPLICATIVA_RISPOSTA);

			dati.addElement(de);
						
		}

		

		
		
		
		
		// *************** Gestione Messaggio *********************
		
		if (tipoOp.equals(TipoOperazione.CHANGE)) {
			de = new DataElement();
			de.setType(DataElementType.TITLE);
			de.setLabel(PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_GESTIONE_MESSAGGIO);
			dati.addElement(de);

			de = new DataElement();
			de.setType(DataElementType.LINK);
			de.setUrl(PorteApplicativeCostanti.SERVLET_NAME_PORTE_APPLICATIVE_CORRELAZIONE_APPLICATIVA,pIdSogg,pIdPorta,pNomePorta);
			String statoCorrelazioneApplicativa = PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_CORRELAZIONE_APPLICATIVA_DISABILITATA;
			if(numCorrelazioneReq>0 || numCorrelazioneRes>0){
				statoCorrelazioneApplicativa = PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_CORRELAZIONE_APPLICATIVA_ABILITATA;
			}
			ServletUtils.setDataElementCustomLabel(de, PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_CORRELAZIONE_APPLICATIVA, statoCorrelazioneApplicativa);
			dati.addElement(de);
			
			de = new DataElement();
			de.setType(DataElementType.LINK);
			de.setUrl(PorteApplicativeCostanti.SERVLET_NAME_PORTE_APPLICATIVE_MESSAGE_SECURITY,pIdSogg,pIdPorta);
			ServletUtils.setDataElementCustomLabel(de, PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_MESSAGE_SECURITY, statoMessageSecurity);
			dati.addElement(de);
			
			//if (InterfaceType.AVANZATA.equals(ServletUtils.getUserFromSession(this.session).getInterfaceType())) {
			de = new DataElement();
			de.setType(DataElementType.LINK);
			de.setUrl(PorteApplicativeCostanti.SERVLET_NAME_PORTE_APPLICATIVE_MTOM, pIdSogg,pIdPorta);
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
		if (this.isModalitaStandard()) {
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
		if (this.isModalitaStandard()) {
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
		if (this.isModalitaStandard()) {
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

		if (this.isModalitaAvanzata()) {

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
			if(this.core.isFunzionalitaProtocolloSupportataDalProtocollo(protocollo, serviceBinding, FunzionalitaProtocollo.MANIFEST_ATTACHMENTS)){
				de.setType(DataElementType.SELECT);
				de.setValues(tipoGestManifest);
				de.setSelected(gestManifest);
			}else {
				de.setType(DataElementType.HIDDEN);
				de.setValue(PorteApplicativeCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_APPLICATIVE_GEST_MANIFEST_DISABILITATO );
			}
	
			de.setName(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_GESTIONE_MANIFEST);
			dati.addElement(de);
			
		}
		
		
		
		
		
		
		if(configurazioneStandardNonApplicabile){
			this.pd.setMessage(CostantiControlStation.LABEL_CONFIGURAZIONE_IMPOSTATA_MODALITA_AVANZATA_LONG_MESSAGE, Costanti.MESSAGE_TYPE_INFO);
			this.pd.disableEditMode();
		}
		


		return dati;
	}


	public void preparePorteAppList(ISearch ricerca, List<PortaApplicativa> lista, int idLista)
			throws Exception {
		try {
			Boolean contaListe = ServletUtils.getContaListeFromSession(this.session);

			// prelevo il flag che mi dice da quale pagina ho acceduto la sezione delle porte delegate
			Integer parentPA = ServletUtils.getIntegerAttributeFromSession(PorteApplicativeCostanti.ATTRIBUTO_PORTE_APPLICATIVE_PARENT, this.session);
			if(parentPA == null) parentPA = PorteApplicativeCostanti.ATTRIBUTO_PORTE_APPLICATIVE_PARENT_NONE;
			
			IExtendedListServlet extendedServletList = this.core.getExtendedServletPortaApplicativa();
			
			String id = this.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_SOGGETTO);
			
			String idAsps = this.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_ASPS);
			if(idAsps == null)
				idAsps = "";

			int limit = ricerca.getPageSize(idLista);
			int offset = ricerca.getIndexIniziale(idLista);
			String search = ServletUtils.getSearchFromSession(ricerca, idLista);

			this.pd.setIndex(offset);
			this.pd.setPageSize(limit);
			this.pd.setNumEntries(ricerca.getNumEntries(idLista));

			// setto la barra del titolo
			List<Parameter> lstParam = new ArrayList<Parameter>();
			
			boolean useIdSogg = false;
			switch (parentPA) {
			case PorteApplicativeCostanti.ATTRIBUTO_PORTE_APPLICATIVE_PARENT_CONFIGURAZIONE:
				// In teoria non dovrei mai trovarmi qui
				
//				// Prendo il nome e il tipo del servizio
//				AccordoServizioParteSpecifica asps = this.apsCore.getAccordoServizioParteSpecifica(Integer.parseInt(idAsps));
//				String servizioTmpTile = asps.getTipoSoggettoErogatore() + "/" + asps.getNomeSoggettoErogatore() + "-" + asps.getTipo() + "/" + asps.getNome();
//				Parameter pIdServizio = new Parameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_ID, asps.getId()+ "");
//				Parameter pNomeServizio = new Parameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_NOME_SERVIZIO, asps.getNome());
//				Parameter pTipoServizio = new Parameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_TIPO_SERVIZIO, asps.getTipo());
//				
//				lstParam.add(new Parameter(AccordiServizioParteSpecificaCostanti.LABEL_APS, null));
//				lstParam.add(new Parameter(Costanti.PAGE_DATA_TITLE_LABEL_ELENCO, AccordiServizioParteSpecificaCostanti.SERVLET_NAME_APS_LIST));
//				lstParam.add(new Parameter(servizioTmpTile, AccordiServizioParteSpecificaCostanti.SERVLET_NAME_APS_CHANGE, pIdServizio,pNomeServizio, pTipoServizio));
//				
//				if(search.equals("")){
//					this.pd.setSearchDescription("");
//					lstParam.add(new Parameter(AccordiServizioParteSpecificaCostanti.LABEL_APS_PORTE_APPLICATIVE,null));
//				}else{
//					lstParam.add(new Parameter(AccordiServizioParteSpecificaCostanti.LABEL_APS_PORTE_APPLICATIVE, 
//							AccordiServizioParteSpecificaCostanti.SERVLET_NAME_APS_PORTE_APPLICATIVE_LIST ,pIdServizio));
//					lstParam.add(new Parameter(PorteApplicativeCostanti.LABEL_PORTE_APPLICATIVE_RISULTATI_RICERCA, null));
//				}
				
				break;
			case PorteApplicativeCostanti.ATTRIBUTO_PORTE_APPLICATIVE_PARENT_SOGGETTO:
				ServletUtils.addListElementIntoSession(this.session, PorteApplicativeCostanti.OBJECT_NAME_PORTE_APPLICATIVE,
						new Parameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_SOGGETTO, id));
				// Prendo il soggetto
				String tmpTitle;
				if(this.core.isRegistroServiziLocale()){
					Soggetto tmpSogg = this.soggettiCore.getSoggettoRegistro(Integer.parseInt(id));
					tmpTitle = tmpSogg.getTipo() + "/" + tmpSogg.getNome();
				}else{
					org.openspcoop2.core.config.Soggetto tmpSogg = this.soggettiCore.getSoggetto(Integer.parseInt(id));
					tmpTitle = tmpSogg.getTipo() + "/" + tmpSogg.getNome();
				}
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
				useIdSogg = true;
				break;
			case PorteApplicativeCostanti.ATTRIBUTO_PORTE_APPLICATIVE_PARENT_NONE:
			default:
				ServletUtils.addListElementIntoSession(this.session, PorteApplicativeCostanti.OBJECT_NAME_PORTE_APPLICATIVE);
				lstParam.add(new Parameter(PorteApplicativeCostanti.LABEL_PORTE_APPLICATIVE, null));
				
				if(search.equals("")){
					this.pd.setSearchDescription("");
					lstParam.add(new Parameter(Costanti.PAGE_DATA_TITLE_LABEL_ELENCO,null));
				}
				else{
					lstParam.add(new Parameter(Costanti.PAGE_DATA_TITLE_LABEL_ELENCO, PorteApplicativeCostanti.SERVLET_NAME_PORTE_APPLICATIVE_LIST));
					lstParam.add(new Parameter(PorteApplicativeCostanti.LABEL_PORTE_APPLICATIVE_RISULTATI_RICERCA, null));
				}
				break;
			}
			
			if(useIdSogg==false){
				addFilterProtocol(ricerca, idLista);
			}

			// setto la barra del titolo
			ServletUtils.setPageDataTitle(this.pd, lstParam.toArray(new Parameter[lstParam.size()]));

			// controllo eventuali risultati ricerca
			this.pd.setSearchLabel(PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_NOME);
			if (!search.equals("")) {
				this.pd.setSearch("on");
				this.pd.setSearchDescription("Porte Applicative contenenti la stringa '" + search + "'");
			}

			boolean showProtocolli = this.core.countProtocolli(this.session)>1;
			
			List<String> listaLabel = new ArrayList<String>();

			listaLabel.add(PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_NOME);
			if(useIdSogg==false){
				listaLabel.add(PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_SOGGETTO);
				if( showProtocolli ) {
					listaLabel.add(CostantiControlStation.LABEL_PARAMETRO_PROTOCOLLO);
				}
			}

			//listaLabel.add(PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_DESCRIZIONE);
			if(this.isModalitaAvanzata()){
				listaLabel.add(PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_SERVIZI_APPLICATIVI);
			}
			listaLabel.add(PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_RUOLI); 
			listaLabel.add(PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_MESSAGE_SECURITY);
			//if(isModalitaAvanzata){
			listaLabel.add(PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_MTOM);
			//}
			listaLabel.add(PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_CORRELAZIONE_APPLICATIVA);
			if(this.isModalitaAvanzata())
				listaLabel.add(PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_PROTOCOL_PROPERTIES);
			if(this.core.isRegistroServiziLocale()){
				//listaLabel.add(PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_SERVIZIO);
				listaLabel.add(AccordiServizioParteSpecificaCostanti.LABEL_APS_MENU_VISUALE_AGGREGATA);
			}
			if(extendedServletList!=null && extendedServletList.showExtendedInfo(this.request, this.session)){
				listaLabel.add(extendedServletList.getListTitle(this));
			}
			
			listaLabel.add(PorteApplicativeCostanti.LABEL_COLUMN_PORTE_APPLICATIVE_STATO_PORTA);
			
			String[] labels = listaLabel.toArray(new String[listaLabel.size()]);
			this.pd.setLabels(labels);

			// preparo i dati
			Vector<Vector<DataElement>> dati = new Vector<Vector<DataElement>>();

			if (lista != null) {
				Iterator<PortaApplicativa> it = lista.iterator();
				while (it.hasNext()) {
					PortaApplicativa pa = it.next();

					Vector<DataElement> e = new Vector<DataElement>();

					Parameter pNomePorta = new Parameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_NOME_PORTA, pa.getNome());
					Parameter pIdNome = new Parameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_NOME, ""+pa.getNome());
					Parameter pIdSogg = new Parameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_SOGGETTO, pa.getIdSoggetto() + "");
					Parameter pIdPorta = new Parameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID, ""+pa.getId());
					Parameter pIdAsps = new Parameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_ASPS, idAsps);

					String protocollo = this.soggettiCore.getProtocolloAssociatoTipoSoggetto(pa.getTipoSoggettoProprietario());
					
					DataElement de = new DataElement();
					de.setType(DataElementType.HIDDEN);
					de.setValue("" + pa.getId());
					e.addElement(de);

					de = new DataElement();
					de.setUrl(PorteApplicativeCostanti.SERVLET_NAME_PORTE_APPLICATIVE_CHANGE, pIdSogg, pNomePorta, pIdPorta, pIdAsps);
					de.setValue(pa.getNome());
					de.setIdToRemove(pa.getId().toString());
					de.setToolTip(pa.getDescrizione());
					e.addElement(de);
					
					if(useIdSogg==false){
						de = new DataElement();
						de.setValue(pa.getTipoSoggettoProprietario()+"/"+pa.getNomeSoggettoProprietario());
						e.addElement(de);
						
						if( showProtocolli ) {
							de = new DataElement();
							de.setValue(this.getLabelProtocollo(protocollo));
							e.addElement(de);
						}
					}
					
//					de = new DataElement();
//					de.setValue(pa.getDescrizione());
//					e.addElement(de);

					if(this.isModalitaAvanzata()){
						de = new DataElement();
						de.setUrl(PorteApplicativeCostanti.SERVLET_NAME_PORTE_APPLICATIVE_SERVIZIO_APPLICATIVO_LIST, pIdSogg, pIdPorta, pIdAsps);
						if (contaListe) {
							int numSA = pa.sizeServizioApplicativoList();
							ServletUtils.setDataElementVisualizzaLabel(de,new Long(numSA));
						} else
							ServletUtils.setDataElementVisualizzaLabel(de);
						e.addElement(de);
					}
					
					de = new DataElement();
					if(TipoAutorizzazione.isRolesRequired(pa.getAutorizzazione()) 
							|| 
							!TipoAutorizzazione.getAllValues().contains(pa.getAutorizzazione()) // custom 
							){
						de.setUrl(PorteApplicativeCostanti.SERVLET_NAME_PORTE_APPLICATIVE_RUOLI_LIST,
								new Parameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_SOGGETTO, "" + pa.getIdSoggetto()),
								new Parameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID, ""+pa.getId()),
								pIdAsps
								);
						if (contaListe) {
							int numRuoli = 0;
							if(pa.getRuoli()!=null){
								numRuoli= pa.getRuoli().sizeRuoloList();
							}
							ServletUtils.setDataElementVisualizzaLabel(de,new Long(numRuoli));
						} else
							ServletUtils.setDataElementVisualizzaLabel(de);
					}
					else{
						de.setValue("-");
					}
					e.addElement(de);

					de = new DataElement();
					de.setUrl(PorteApplicativeCostanti.SERVLET_NAME_PORTE_APPLICATIVE_MESSAGE_SECURITY, pIdSogg, pIdPorta,pIdAsps);
					de.setValue(pa.getStatoMessageSecurity());
					e.addElement(de);
					
					//if(isModalitaAvanzata){
					de = new DataElement();
					de.setUrl( 
							PorteApplicativeCostanti.SERVLET_NAME_PORTE_APPLICATIVE_MTOM,pIdPorta, pIdSogg,pIdAsps);

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
					de.setUrl(PorteApplicativeCostanti.SERVLET_NAME_PORTE_APPLICATIVE_CORRELAZIONE_APPLICATIVA, pIdSogg, pIdPorta, pIdNome,pIdAsps);
					
					boolean isCorrelazioneApplicativaAbilitataReq = false;
					boolean isCorrelazioneApplicativaAbilitataRes = false;
					
					if (pa.getCorrelazioneApplicativa() != null)
						isCorrelazioneApplicativaAbilitataReq = pa.getCorrelazioneApplicativa().sizeElementoList() > 0;

					if (pa.getCorrelazioneApplicativaRisposta() != null)
						isCorrelazioneApplicativaAbilitataRes = pa.getCorrelazioneApplicativaRisposta().sizeElementoList() > 0;
					
					if(isCorrelazioneApplicativaAbilitataReq || isCorrelazioneApplicativaAbilitataRes)
						de.setValue(PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_CORRELAZIONE_APPLICATIVA_ABILITATA);
					else 
						de.setValue(PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_CORRELAZIONE_APPLICATIVA_DISABILITATA);
					e.addElement(de);

					if(this.isModalitaAvanzata()){
						de = new DataElement();
						de.setUrl(PorteApplicativeCostanti.SERVLET_NAME_PORTE_APPLICATIVE_PROPRIETA_PROTOCOLLO_LIST, pIdSogg, pIdPorta, pIdAsps);
						if (contaListe) {
							int numProp = pa.sizeProprietaIntegrazioneProtocolloList();
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
						try {
							soggEr = this.soggettiCore.getSoggettoRegistro(idSoggEr);
							asps = this.apsCore.getAccordoServizioParteSpecifica(idServ);
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
						Parameter pNomeServizio = new Parameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_NOME_SERVIZIO, asps.getNome());
						Parameter pTipoServizio = new Parameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_TIPO_SERVIZIO, asps.getTipo());
						Parameter pVersioneServizio = new Parameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_VERSIONE, asps.getVersione().intValue()+"");

						de.setUrl(AccordiServizioParteSpecificaCostanti.SERVLET_NAME_APS_CHANGE, 
								pId2, pNomeServizio, pTipoServizio, pVersioneServizio);
						IDServizio idServizio = IDServizioFactory.getInstance().getIDServizioFromValues(asps.getTipo(), asps.getNome(), 
								soggEr.getTipo(),soggEr.getNome(), 
								asps.getVersione());
						de.setValue(IDServizioFactory.getInstance().getUriFromIDServizio(idServizio) + tmpAz);

						e.addElement(de);
					}

					
					if(extendedServletList!=null && extendedServletList.showExtendedInfo(this.request, this.session)){
						de = new DataElement();
						de.setUrl(PorteApplicativeCostanti.SERVLET_NAME_PORTE_APPLICATIVE_EXTENDED_LIST,
								pIdPorta,pIdNome,pIdPorta, pIdSogg, pIdAsps
								);
						if (contaListe) {
							int numExtended = extendedServletList.sizeList(pa);
							ServletUtils.setDataElementVisualizzaLabel(de,new Long(numExtended));
						} else
							ServletUtils.setDataElementVisualizzaLabel(de);
						e.addElement(de);
					}
					
					de = new DataElement();
					boolean abilitatoPorta = pa.getStato()!=null ? CostantiConfigurazione.ABILITATO.equals(pa.getStato()) : true;
					de.setType(DataElementType.CHECKBOX);
					de.setSelected(abilitatoPorta);
					de.setToolTip(abilitatoPorta?CostantiConfigurazione.ABILITATO.getValue():CostantiConfigurazione.DISABILITATO.getValue());
					de.setValue(abilitatoPorta+"");
					e.addElement(de);
					
					dati.addElement(e);
				}
			}

			this.pd.setDati(dati);
			// le porte applicative non si possono piu' creare dalle liste PA e PA di un soggetto
			this.pd.setAddButton(false);
			
			if (useIdSogg){ 
				if(this.isModalitaStandard()){
					this.pd.setRemoveButton(false);
					this.pd.setSelect(false);
				}
			} 

		} catch (Exception e) {
			this.log.error("Exception: " + e.getMessage(), e);
			throw new Exception(e);
		}
	}

	// Prepara la lista di  properties delle porte applicative
	public void preparePorteAppPropList(String nomePorta, ISearch ricerca, List<PortaApplicativaProprietaIntegrazioneProtocollo> lista)
			throws Exception {
		try {
			// prelevo il flag che mi dice da quale pagina ho acceduto la sezione delle porte delegate
			Integer parentPA = ServletUtils.getIntegerAttributeFromSession(PorteApplicativeCostanti.ATTRIBUTO_PORTE_APPLICATIVE_PARENT, this.session);

			String idPorta = this.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID);
			String idsogg = this.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_SOGGETTO);
			String idAsps = this.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_ASPS);
			if(idAsps == null)
				idAsps = "";

			ServletUtils.addListElementIntoSession(this.session,  PorteApplicativeCostanti.OBJECT_NAME_PORTE_APPLICATIVE_PROPRIETA_PROTOCOLLO,
					new Parameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID, idPorta),
					new Parameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_SOGGETTO, idsogg),
					new Parameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_ASPS, idAsps));

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
			List<Parameter> lstParam = this.getTitoloPA(parentPA, idsogg, idAsps, tmpTitle);
			
			if(search.equals("")){
				this.pd.setSearchDescription("");
				lstParam.add(new Parameter(PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_PROTOCOL_PROPERTIES_DI + idporta,null));
			}else{
				lstParam.add(new Parameter(PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_PROTOCOL_PROPERTIES_DI + idporta,
						PorteApplicativeCostanti.SERVLET_NAME_PORTE_APPLICATIVE_PROPRIETA_PROTOCOLLO_LIST ,
						new Parameter( PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID, idPorta),
						new Parameter( PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_SOGGETTO, idsogg),
						new Parameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_ASPS, idAsps)));
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
				Iterator<PortaApplicativaProprietaIntegrazioneProtocollo> it = lista.iterator();
				while (it.hasNext()) {
					PortaApplicativaProprietaIntegrazioneProtocollo ssp = it.next();

					Vector<DataElement> e = new Vector<DataElement>();

					DataElement de = new DataElement();
					de.setUrl(PorteApplicativeCostanti.SERVLET_NAME_PORTE_APPLICATIVE_PROPRIETA_PROTOCOLLO_CHANGE,
							new Parameter( PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID, idPorta),
							new Parameter( PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_SOGGETTO, idsogg),
							new Parameter( PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_NOME, ssp.getNome()),
							new Parameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_ASPS, idAsps));
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
			Integer parentPA = ServletUtils.getIntegerAttributeFromSession(PorteApplicativeCostanti.ATTRIBUTO_PORTE_APPLICATIVE_PARENT, this.session);
			String idAsps = this.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_ASPS);
			if(idAsps == null)
				idAsps = "";

			String idPorta = this.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID);
			String idsogg = this.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_SOGGETTO);

			ServletUtils.addListElementIntoSession(this.session,  PorteApplicativeCostanti.OBJECT_NAME_PORTE_APPLICATIVE_SERVIZIO_APPLICATIVO,
					new Parameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID, idPorta),
					new Parameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_SOGGETTO, idsogg),
					new Parameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_ASPS, idAsps));

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
			List<Parameter> lstParam = this.getTitoloPA(parentPA, idsogg, idAsps, tmpTitle);

			if(search.equals("")){
				this.pd.setSearchDescription("");
				lstParam.add(new Parameter(PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_SERVIZIO_APPLICATIVO_DI + idporta,null));
			}else{
				lstParam.add(new Parameter(PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_SERVIZIO_APPLICATIVO_DI + idporta,
						PorteApplicativeCostanti.SERVLET_NAME_PORTE_APPLICATIVE_SERVIZIO_APPLICATIVO_LIST ,
						new Parameter( PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID, idPorta),
						new Parameter( PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_SOGGETTO, idsogg),
						new Parameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_ASPS, idAsps)));
				
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
							new Parameter(ServiziApplicativiCostanti.PARAMETRO_SERVIZI_APPLICATIVI_PROVIDER, ""+idsogg),
							new Parameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_ASPS, idAsps));
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
			Integer parentPA = ServletUtils.getIntegerAttributeFromSession(PorteApplicativeCostanti.ATTRIBUTO_PORTE_APPLICATIVE_PARENT, this.session);
			String idAsps = this.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_ASPS);
			if(idAsps == null)
				idAsps = "";

			String idPorta = this.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID);
			String idsogg = this.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_SOGGETTO);

			ServletUtils.addListElementIntoSession(this.session,  PorteApplicativeCostanti.OBJECT_NAME_PORTE_APPLICATIVE_CORRELAZIONE_APPLICATIVA_REQUEST,
					new Parameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID, idPorta),
					new Parameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_SOGGETTO, idsogg),
					new Parameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_ASPS, idAsps));

			int idLista = Liste.PORTE_APPLICATIVE_CORRELAZIONE_APPLICATIVA;
			int limit = ricerca.getPageSize(idLista);
			int offset = ricerca.getIndexIniziale(idLista);
			String search = ServletUtils.getSearchFromSession(ricerca, idLista);

			this.pd.setIndex(offset);
			this.pd.setPageSize(limit);
			this.pd.setNumEntries(ricerca.getNumEntries(idLista));

			PortaApplicativa myPA = this.porteApplicativeCore.getPortaApplicativa(Integer.parseInt(idPorta));
			String idporta = myPA.getNome();

			// setto la barra del titolo
			List<Parameter> lstParam = this.getTitoloPA(parentPA, idsogg, idAsps);

			lstParam.add(new Parameter(PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_CORRELAZIONI_APPLICATIVE_DI + idporta,
					PorteApplicativeCostanti.SERVLET_NAME_PORTE_APPLICATIVE_CORRELAZIONE_APPLICATIVA, 
					new Parameter( PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID, idPorta),
					new Parameter( PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_SOGGETTO, idsogg),
					new Parameter( PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_NOME, idporta),
					new Parameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_ASPS, idAsps)
					));
			
			if(search.equals("")){
				this.pd.setSearchDescription("");
				lstParam.add(new Parameter(PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_CORRELAZIONI_APPLICATIVE_RICHIESTA_DI, // + idporta,
						null));
			}else{
				lstParam.add(new Parameter(PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_CORRELAZIONI_APPLICATIVE_RICHIESTA_DI, // + idporta,
						PorteApplicativeCostanti.SERVLET_NAME_PORTE_APPLICATIVE_CORRELAZIONE_APPLICATIVA_REQUEST_LIST ,
						new Parameter( PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID, idPorta),
						new Parameter( PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_SOGGETTO, idsogg),
						new Parameter( PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_NOME, idporta),
						new Parameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_ASPS, idAsps)
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
							new Parameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_CORRELAZIONE_APPLICATIVA, "" + cae.getId()),
							new Parameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_ASPS, idAsps)
							);
					String nomeElemento = "(*)";
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
			Integer parentPA = ServletUtils.getIntegerAttributeFromSession(PorteApplicativeCostanti.ATTRIBUTO_PORTE_APPLICATIVE_PARENT, this.session);
			String idAsps = this.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_ASPS);
			if(idAsps == null)
				idAsps = "";

			String idPorta = this.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID);
			String idsogg = this.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_SOGGETTO);

			ServletUtils.addListElementIntoSession(this.session,  PorteApplicativeCostanti.OBJECT_NAME_PORTE_APPLICATIVE_CORRELAZIONE_APPLICATIVA_RESPONSE,
					new Parameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID, idPorta),
					new Parameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_SOGGETTO, idsogg),
					new Parameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_ASPS, idAsps));

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
			List<Parameter> lstParam = this.getTitoloPA(parentPA, idsogg, idAsps, tmpTitle);

			lstParam.add(new Parameter(PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_CORRELAZIONI_APPLICATIVE_DI + idporta,
					PorteApplicativeCostanti.SERVLET_NAME_PORTE_APPLICATIVE_CORRELAZIONE_APPLICATIVA, 
					new Parameter( PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID, idPorta),
					new Parameter( PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_SOGGETTO, idsogg),
					new Parameter( PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_NOME, idporta),
					new Parameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_ASPS, idAsps)
					));
			if(search.equals("")){
				this.pd.setSearchDescription("");
				lstParam.add(new Parameter(PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_CORRELAZIONI_APPLICATIVE_RISPOSTA_DI, // + idporta,
						null));
			}else{
				lstParam.add(new Parameter(PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_CORRELAZIONI_APPLICATIVE_RISPOSTA_DI, // + idporta,
						PorteApplicativeCostanti.SERVLET_NAME_PORTE_APPLICATIVE_CORRELAZIONE_APPLICATIVA_RESPONSE_LIST ,
						new Parameter( PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID, idPorta),
						new Parameter( PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_SOGGETTO, idsogg),
						new Parameter( PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_NOME, idporta),
						new Parameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_ASPS, idAsps)
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
							new Parameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_CORRELAZIONE_APPLICATIVA, cae.getId() + ""),
							new Parameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_ASPS, idAsps)
							);
					String nomeElemento = "(*)";
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
			Integer parentPA = ServletUtils.getIntegerAttributeFromSession(PorteApplicativeCostanti.ATTRIBUTO_PORTE_APPLICATIVE_PARENT, this.session);
			String idAsps = this.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_ASPS);
			if(idAsps == null)
				idAsps = "";

			String idPorta = this.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID);
			String idsogg = this.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_SOGGETTO);
			ServletUtils.addListElementIntoSession(this.session,  PorteApplicativeCostanti.OBJECT_NAME_PORTE_APPLICATIVE_MESSAGE_SECURITY_REQUEST,
					new Parameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID, idPorta),
					new Parameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_SOGGETTO, idsogg),
					new Parameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_ASPS, idAsps));

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
			List<Parameter> lstParam = this.getTitoloPA(parentPA, idsogg, idAsps, tmpTitle);

			lstParam.add(new Parameter(PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_MESSAGE_SECURITY_DI + idporta,
					PorteApplicativeCostanti.SERVLET_NAME_PORTE_APPLICATIVE_MESSAGE_SECURITY ,
					new Parameter( PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID, idPorta),
					new Parameter( PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_SOGGETTO, idsogg),
					new Parameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_ASPS, idAsps)));
			
			if(search.equals("")){
				this.pd.setSearchDescription("");
				lstParam.add(new Parameter(PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_MESSAGE_SECURITY_REQUEST_FLOW_DI, // + idporta,
						null));
			}else{
				lstParam.add(new Parameter(PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_MESSAGE_SECURITY_REQUEST_FLOW_DI, // + idporta,
						PorteApplicativeCostanti.SERVLET_NAME_PORTE_APPLICATIVE_MESSAGE_SECURITY_REQUEST_LIST ,
						new Parameter( PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID, idPorta),
						new Parameter( PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_SOGGETTO, idsogg),
						new Parameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_ASPS, idAsps)
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
							new Parameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_NOME, wsrfp.getNome()),
							new Parameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_ASPS, idAsps));
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
			Integer parentPA = ServletUtils.getIntegerAttributeFromSession(PorteApplicativeCostanti.ATTRIBUTO_PORTE_APPLICATIVE_PARENT, this.session);
			String idAsps = this.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_ASPS);
			if(idAsps == null)
				idAsps = "";

			String idPorta = this.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID);
			String idsogg = this.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_SOGGETTO);

			ServletUtils.addListElementIntoSession(this.session,  PorteApplicativeCostanti.OBJECT_NAME_PORTE_APPLICATIVE_MESSAGE_SECURITY_RESPONSE,
					new Parameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID, idPorta),
					new Parameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_SOGGETTO, idsogg),
					new Parameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_ASPS, idAsps));

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
			List<Parameter> lstParam = this.getTitoloPA(parentPA, idsogg, idAsps, tmpTitle);
			
			lstParam.add(new Parameter(PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_MESSAGE_SECURITY_DI + idporta,
					PorteApplicativeCostanti.SERVLET_NAME_PORTE_APPLICATIVE_MESSAGE_SECURITY ,
					new Parameter( PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID, idPorta),
					new Parameter( PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_SOGGETTO, idsogg),
					new Parameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_ASPS, idAsps)));
			
			if(search.equals("")){
				this.pd.setSearchDescription("");
				lstParam.add(new Parameter(PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_MESSAGE_SECURITY_RESPONSE_FLOW_DI, // + idporta,
						null));
			}else{
				lstParam.add(new Parameter(PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_MESSAGE_SECURITY_RESPONSE_FLOW_DI, // + idporta,
						PorteApplicativeCostanti.SERVLET_NAME_PORTE_APPLICATIVE_MESSAGE_SECURITY_RESPONSE_LIST ,
						new Parameter( PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID, idPorta),
						new Parameter( PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_SOGGETTO, idsogg),
						new Parameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_ASPS, idAsps)
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
							new Parameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_NOME, wsrfp.getNome()),
							new Parameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_ASPS, idAsps)
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
	
	
	// Prepara la lista di sil delle porte applicative
	public void preparePorteAppAzioneList(String idPorta) throws Exception {
		try {

			// prelevo il flag che mi dice da quale pagina ho acceduto la sezione delle porte delegate
			Integer parentPA = ServletUtils.getIntegerAttributeFromSession(PorteApplicativeCostanti.ATTRIBUTO_PORTE_APPLICATIVE_PARENT, this.session);
			String idAsps = this.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_ASPS);
			if(idAsps == null)
				idAsps = "";

			String idsogg = this.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_SOGGETTO);

			ServletUtils.addListElementIntoSession(this.session,  PorteApplicativeCostanti.OBJECT_NAME_PORTE_APPLICATIVE_AZIONE,
					new Parameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID, idPorta),
					new Parameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_SOGGETTO, idsogg),
					new Parameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_ASPS, idAsps));

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
			List<String> azioneDelegataList = myPA.getAzione().getAzioneDelegataList();

			// setto la barra del titolo
			List<Parameter> lstParam = this.getTitoloPA(parentPA, idsogg, idAsps, tmpTitle);

			this.pd.setSearchDescription("");
			lstParam.add(new Parameter(PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_AZIONI_DI + idporta,null));

			// setto la barra del titolo
			ServletUtils.setPageDataTitle(this.pd, lstParam.toArray(new Parameter[lstParam.size()]));

			// setto le label delle colonne
			String[] labels = { PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_AZIONE};
			this.pd.setLabels(labels);

			// preparo i dati
			Vector<Vector<DataElement>> dati = new Vector<Vector<DataElement>>();

			if (azioneDelegataList != null) {
				Iterator<String> it = azioneDelegataList.iterator();
				while (it.hasNext()) {
					String nomeAzione = it.next();

					Vector<DataElement> e = new Vector<DataElement>();

					DataElement de = new DataElement();
//					de.setUrl(
//							ServiziApplicativiCostanti.SERVLET_NAME_SERVIZI_APPLICATIVI_CHANGE,
//							new Parameter(ServiziApplicativiCostanti.PARAMETRO_SERVIZI_APPLICATIVI_ID, ""+sa.getId()),
//							new Parameter(ServiziApplicativiCostanti.PARAMETRO_SERVIZI_APPLICATIVI_PROVIDER, ""+idsogg),
//							new Parameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_ASPS, idAsps));
					de.setValue(nomeAzione);
					de.setIdToRemove(nomeAzione);
					e.addElement(de);

					dati.addElement(e);
				}
				this.pd.setNumEntries(azioneDelegataList.size());
			}

			
			this.pd.setDati(dati);
			this.pd.setAddButton(true);

		} catch (Exception e) {
			this.log.error("Exception: " + e.getMessage(), e);
			throw new Exception(e);
		}
	}
	
	// Prepara la lista di soggetti associati alla pa
	public void preparePorteAppSoggettoList(String nomePorta, ISearch ricerca, List<PortaApplicativaAutorizzazioneSoggetto> lista) throws Exception {
		try {
			// prelevo il flag che mi dice da quale pagina ho acceduto la sezione delle porte delegate
			Integer parentPA = ServletUtils.getIntegerAttributeFromSession(PorteApplicativeCostanti.ATTRIBUTO_PORTE_APPLICATIVE_PARENT, this.session);
			String idAsps = this.request.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_ASPS);
			if(idAsps == null)
				idAsps = "";

			String idPorta = this.request.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID);
			String idsogg = this.request.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_SOGGETTO);

			ServletUtils.addListElementIntoSession(this.session,  PorteApplicativeCostanti.OBJECT_NAME_PORTE_APPLICATIVE_SOGGETTO,
					new Parameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID, idPorta),
					new Parameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_SOGGETTO, idsogg),
					new Parameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_ASPS, idAsps));

			int idLista = Liste.PORTE_APPLICATIVE_SOGGETTO;
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
			List<Parameter> lstParam = this.getTitoloPA(parentPA, idsogg, idAsps, tmpTitle);

			if(search.equals("")){
				this.pd.setSearchDescription("");
				lstParam.add(new Parameter(PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_SOGGETTO_DI + idporta,null));
			}else{
				lstParam.add(new Parameter(PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_SOGGETTO_DI + idporta,
						PorteApplicativeCostanti.SERVLET_NAME_PORTE_APPLICATIVE_SOGGETTO_LIST ,
						new Parameter( PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID, idPorta),
						new Parameter( PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_SOGGETTO, idsogg),
						new Parameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_ASPS, idAsps)));
				
				lstParam.add(new Parameter(PorteApplicativeCostanti.LABEL_PORTE_APPLICATIVE_RISULTATI_RICERCA, null));
			}

			// setto la barra del titolo
			ServletUtils.setPageDataTitle(this.pd, lstParam.toArray(new Parameter[lstParam.size()]));

			// controllo eventuali risultati ricerca
			if (!search.equals("")) {
				this.pd.setSearch("on");
				this.pd.setSearchDescription("Soggetti contenenti la stringa '" + search + "'");
			}

			// setto le label delle colonne
			String[] labels = { PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_SOGGETTO};
			this.pd.setLabels(labels);

			// preparo i dati
			Vector<Vector<DataElement>> dati = new Vector<Vector<DataElement>>();

			if (lista != null) {
				Iterator<PortaApplicativaAutorizzazioneSoggetto> it = lista.iterator();
				while (it.hasNext()) {
					PortaApplicativaAutorizzazioneSoggetto sog = it.next();

					Vector<DataElement> e = new Vector<DataElement>();

					DataElement de = new DataElement();
					de.setValue(sog.getTipo() + "/" + sog.getNome());
					de.setIdToRemove(sog.getTipo() + "/" + sog.getNome());
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
		de.setLabel(PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_ELEMENTO_XML);
		de.setType(DataElementType.TEXT_EDIT);
		de.setName(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ELEMENTO_XML);
		de.setNote(PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_ELEMENTO_XML_NOTE);
		de.setSize(80);
		if (elemxml == null) {
			de.setValue("");
		} else {
			de.setValue(elemxml);
		}
		dati.addElement(de);

		//String[] tipoMode = { "urlBased", "contentBased", "inputBased","disabilitato" };
		String[] tipoMode = { 
				PorteApplicativeCostanti.VALUE_PARAMETRO_PORTE_APPLICATIVE_TIPO_MODE_CORRELAZIONE_URL_BASED,
				PorteApplicativeCostanti.VALUE_PARAMETRO_PORTE_APPLICATIVE_TIPO_MODE_CORRELAZIONE_CONTENT_BASED, 
				PorteApplicativeCostanti.VALUE_PARAMETRO_PORTE_APPLICATIVE_TIPO_MODE_CORRELAZIONE_INPUT_BASED, 
				PorteApplicativeCostanti.VALUE_PARAMETRO_PORTE_APPLICATIVE_TIPO_MODE_CORRELAZIONE_DISABILITATO
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

		if (mode.equals(PorteApplicativeCostanti.VALUE_PARAMETRO_PORTE_APPLICATIVE_TIPO_MODE_CORRELAZIONE_URL_BASED) ||
				mode.equals(PorteApplicativeCostanti.VALUE_PARAMETRO_PORTE_APPLICATIVE_TIPO_MODE_CORRELAZIONE_CONTENT_BASED)
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

		if(!PorteApplicativeCostanti.VALUE_PARAMETRO_PORTE_APPLICATIVE_TIPO_MODE_CORRELAZIONE_DISABILITATO.equals(mode)){
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
		de.setLabel(PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_ELEMENTO_XML);
		de.setType(DataElementType.TEXT_EDIT);
		de.setNote(PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_ELEMENTO_XML_NOTE);
		de.setName(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ELEMENTO_XML);
		de.setSize(80);
		if (elemxml == null) {
			de.setValue("");
		} else {
			de.setValue(elemxml);
		}
		dati.addElement(de);

		String[] tipoMode = {
				PorteApplicativeCostanti.VALUE_PARAMETRO_PORTE_APPLICATIVE_TIPO_MODE_CORRELAZIONE_CONTENT_BASED,
				PorteApplicativeCostanti.VALUE_PARAMETRO_PORTE_APPLICATIVE_TIPO_MODE_CORRELAZIONE_INPUT_BASED,
				PorteApplicativeCostanti.VALUE_PARAMETRO_PORTE_APPLICATIVE_TIPO_MODE_CORRELAZIONE_DISABILITATO
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

		if (mode.equals(PorteApplicativeCostanti.VALUE_PARAMETRO_PORTE_APPLICATIVE_TIPO_MODE_CORRELAZIONE_URL_BASED) ||
				mode.equals(PorteApplicativeCostanti.VALUE_PARAMETRO_PORTE_APPLICATIVE_TIPO_MODE_CORRELAZIONE_CONTENT_BASED)) {
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

		if(!PorteApplicativeCostanti.VALUE_PARAMETRO_PORTE_APPLICATIVE_TIPO_MODE_CORRELAZIONE_DISABILITATO.equals(mode)){
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
		de.setLabel(PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_PROTOCOL_PROPERTIES);
		de.setType(DataElementType.TITLE);
		dati.addElement(de);
		
		de = new DataElement();
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
			Integer parentPA = ServletUtils.getIntegerAttributeFromSession(PorteApplicativeCostanti.ATTRIBUTO_PORTE_APPLICATIVE_PARENT, this.session);
			String idAsps = this.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_ASPS);
			if(idAsps == null)
				idAsps = "";


			String id = this.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID);
			String idsogg = this.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_SOGGETTO);

			ServletUtils.addListElementIntoSession(this.session, PorteApplicativeCostanti.OBJECT_NAME_PORTE_APPLICATIVE_MTOM_REQUEST,
					new Parameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID, id),
					new Parameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_SOGGETTO, idsogg),
					new Parameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_ASPS, idAsps));

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
			List<Parameter> lstParam = this.getTitoloPA(parentPA, idsogg, idAsps, tmpTitle);

			lstParam.add(new Parameter(PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_MTOM_DI + idporta,
					PorteApplicativeCostanti.SERVLET_NAME_PORTE_APPLICATIVE_MTOM,
					new Parameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID,id),
					new Parameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_SOGGETTO,idsogg),
					new Parameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_ASPS, idAsps)
					));
			if(search.equals("")){
				this.pd.setSearchDescription("");
				lstParam.add(new Parameter(PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_MTOM_REQUEST_FLOW_DI, // + idporta,
						null));
			}
			else{
				lstParam.add(new Parameter(PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_MTOM_REQUEST_FLOW_DI, // + idporta,
						PorteApplicativeCostanti.SERVLET_NAME_PORTE_APPLICATIVE_MTOM_REQUEST_LIST,
						new Parameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID,id),
						new Parameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_SOGGETTO,idsogg),
						new Parameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_ASPS, idAsps)
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
							new Parameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_NOME, parametro.getNome()),
							new Parameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_ASPS, idAsps)
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
			String id = this.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID);
			String idsogg = this.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_SOGGETTO);

			// prelevo il flag che mi dice da quale pagina ho acceduto la sezione delle porte applicative
			Integer parentPA = ServletUtils.getIntegerAttributeFromSession(PorteApplicativeCostanti.ATTRIBUTO_PORTE_APPLICATIVE_PARENT, this.session);
			String idAsps = this.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_ASPS);
			if(idAsps == null)
				idAsps = "";

			ServletUtils.addListElementIntoSession(this.session, PorteApplicativeCostanti.OBJECT_NAME_PORTE_APPLICATIVE_MTOM_RESPONSE, 
					new Parameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID, id),
					new Parameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_SOGGETTO, idsogg),
					new Parameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_ASPS, idAsps));

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
			List<Parameter> lstParam = this.getTitoloPA(parentPA, idsogg, idAsps, tmpTitle);
			
			lstParam.add(new Parameter(PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_MTOM_DI + idporta,
					PorteApplicativeCostanti.SERVLET_NAME_PORTE_APPLICATIVE_MTOM,
					new Parameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID,id),
					new Parameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_SOGGETTO,idsogg),
					new Parameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_ASPS, idAsps)
					));

			if(search.equals("")){
				this.pd.setSearchDescription("");
				lstParam.add(new Parameter(PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_MTOM_RESPONSE_FLOW_DI, // + idporta,
						null));
			}
			else{
				lstParam.add(new Parameter(PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_MTOM_RESPONSE_FLOW_DI, // + idporta,
						PorteApplicativeCostanti.SERVLET_NAME_PORTE_APPLICATIVE_MTOM_RESPONSE_LIST,
						new Parameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID,id),
						new Parameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_SOGGETTO,idsogg),
						new Parameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_ASPS, idAsps)
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
							new Parameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_NOME, wsrfp.getNome()),
							new Parameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_ASPS, idAsps)
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
	
	public void preparePorteApplicativeRuoliList(String nomePorta, ISearch ricerca, List<String> lista)
			throws Exception {
		try {
			// prelevo il flag che mi dice da quale pagina ho acceduto la sezione delle porte delegate
			Integer parentPA = ServletUtils.getIntegerAttributeFromSession(PorteApplicativeCostanti.ATTRIBUTO_PORTE_APPLICATIVE_PARENT, this.session);
			String idAsps = this.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_ASPS);
			if(idAsps == null)
				idAsps = "";

			String id = this.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID);
			String idsogg = this.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_SOGGETTO);

			ServletUtils.addListElementIntoSession(this.session, PorteApplicativeCostanti.OBJECT_NAME_PORTE_APPLICATIVE_RUOLI,
					new Parameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID, id), 
					new Parameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_SOGGETTO, idsogg),
					new Parameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_ASPS, idAsps));

			int idLista = Liste.PORTE_APPLICATIVE_RUOLI;
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

			PortaApplicativa pa = this.porteApplicativeCore.getPortaApplicativa(Integer.parseInt(id));
			String idporta = pa.getNome();

			// setto la barra del titolo
			List<Parameter> lstParam = this.getTitoloPA(parentPA, idsogg, idAsps, tmpTitle);
			
			if(search.equals("")){
				this.pd.setSearchDescription("");
				lstParam.add(new Parameter(PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_RUOLI_DI + idporta,null));
			}
			else{
				lstParam.add(new Parameter(PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_RUOLI_DI + idporta,
						PorteApplicativeCostanti.SERVLET_NAME_PORTE_APPLICATIVE_RUOLI_LIST,
						new Parameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID,id),
						new Parameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_SOGGETTO,idsogg),
						new Parameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_ASPS, idAsps)
						));
				lstParam.add(new Parameter(PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_RISULTATI_RICERCA, null));

			}

			ServletUtils.setPageDataTitle(this.pd, lstParam.toArray(new Parameter[lstParam.size()]));

			// controllo eventuali risultati ricerca
			if (!search.equals("")) {
				this.pd.setSearch("on");
				this.pd.setSearchDescription("Servizi Applicativi contenenti la stringa '" + search + "'");
			}

			// setto le label delle colonne
			String[] labels = {CostantiControlStation.LABEL_PARAMETRO_RUOLO };
			this.pd.setLabels(labels);

			// preparo i dati
			Vector<Vector<DataElement>> dati = new Vector<Vector<DataElement>>();

			if (lista != null) {
				Iterator<String> it = lista.iterator();
				while (it.hasNext()) {
					String ruolo = it.next();
		
					Vector<DataElement> e = new Vector<DataElement>();
		
					DataElement de = new DataElement();
					de.setValue(ruolo);
					de.setIdToRemove(ruolo);
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
	
	public List<Parameter> getTitoloPA(Integer parentPA, String idsogg, String idAsps)	throws Exception, DriverRegistroServiziNotFound, DriverRegistroServiziException {
		String soggettoTitle = null;
		if(this.core.isRegistroServiziLocale()){
			Soggetto mySogg = this.soggettiCore.getSoggettoRegistro(Integer.parseInt(idsogg));
			soggettoTitle = mySogg.getTipo() + "/" + mySogg.getNome();
		}
		else{
			org.openspcoop2.core.config.Soggetto mySogg = this.soggettiCore.getSoggetto(Integer.parseInt(idsogg));
			soggettoTitle = mySogg.getTipo() + "/" + mySogg.getNome();
		}
		return getTitoloPA(parentPA, idsogg, idAsps, soggettoTitle);
	}

	public List<Parameter> getTitoloPA(Integer parentPA, String idsogg, String idAsps, String soggettoTitle)	throws Exception, DriverRegistroServiziNotFound, DriverRegistroServiziException {
		List<Parameter> lstParam = new ArrayList<>();
		switch (parentPA) {
		case PorteApplicativeCostanti.ATTRIBUTO_PORTE_APPLICATIVE_PARENT_CONFIGURAZIONE:
			AccordiServizioParteSpecificaCore apsCore = new AccordiServizioParteSpecificaCore(this.porteApplicativeCore);
			// Prendo il nome e il tipo del servizio
			AccordoServizioParteSpecifica asps = apsCore.getAccordoServizioParteSpecifica(Integer.parseInt(idAsps));
			String servizioTmpTile = asps.getTipoSoggettoErogatore() + "/" + asps.getNomeSoggettoErogatore() + "-" + asps.getTipo() + "/" + asps.getNome();
			Parameter pIdServizio = new Parameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_ID, asps.getId()+ "");
			Parameter pNomeServizio = new Parameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_NOME_SERVIZIO, asps.getNome());
			Parameter pTipoServizio = new Parameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_TIPO_SERVIZIO, asps.getTipo());
			Parameter pIdsoggErogatore = new Parameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_ID_SOGGETTO_EROGATORE, ""+asps.getIdSoggetto());
			
			lstParam.add(new Parameter(AccordiServizioParteSpecificaCostanti.LABEL_APS, null));
			lstParam.add(new Parameter(Costanti.PAGE_DATA_TITLE_LABEL_ELENCO, AccordiServizioParteSpecificaCostanti.SERVLET_NAME_APS_LIST));
			lstParam.add(new Parameter(servizioTmpTile, AccordiServizioParteSpecificaCostanti.SERVLET_NAME_APS_CHANGE, pIdServizio,pNomeServizio, pTipoServizio));
			lstParam.add(new Parameter(AccordiServizioParteSpecificaCostanti.LABEL_APS_PORTE_APPLICATIVE, 
					AccordiServizioParteSpecificaCostanti.SERVLET_NAME_APS_PORTE_APPLICATIVE_LIST ,pIdServizio,pNomeServizio, pTipoServizio, pIdsoggErogatore));
			break;
		case PorteApplicativeCostanti.ATTRIBUTO_PORTE_APPLICATIVE_PARENT_SOGGETTO:
			lstParam.add(new Parameter(SoggettiCostanti.LABEL_SOGGETTI, null));
			lstParam.add(new Parameter(Costanti.PAGE_DATA_TITLE_LABEL_ELENCO, SoggettiCostanti.SERVLET_NAME_SOGGETTI_LIST));
			lstParam.add(new Parameter(PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_PORTE_APPLICATIVE_DI + soggettoTitle, PorteApplicativeCostanti.SERVLET_NAME_PORTE_APPLICATIVE_LIST ,
					new Parameter( PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_SOGGETTO, idsogg)));
			break;
		case PorteApplicativeCostanti.ATTRIBUTO_PORTE_APPLICATIVE_PARENT_NONE:
		default:
			lstParam.add(new Parameter(PorteApplicativeCostanti.LABEL_PORTE_APPLICATIVE, null));
			lstParam.add(new Parameter(Costanti.PAGE_DATA_TITLE_LABEL_ELENCO, PorteApplicativeCostanti.SERVLET_NAME_PORTE_APPLICATIVE_LIST));
			break;
		}
		return lstParam;
	}
}
