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
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.openspcoop2.core.commons.ISearch;
import org.openspcoop2.core.commons.Liste;
import org.openspcoop2.core.config.ConfigurazioneProtocollo;
import org.openspcoop2.core.config.CorrelazioneApplicativaElemento;
import org.openspcoop2.core.config.CorrelazioneApplicativaRispostaElemento;
import org.openspcoop2.core.config.MessageSecurity;
import org.openspcoop2.core.config.MessageSecurityFlowParameter;
import org.openspcoop2.core.config.MtomProcessorFlowParameter;
import org.openspcoop2.core.config.PortaApplicativa;
import org.openspcoop2.core.config.PortaApplicativaAutorizzazioneSoggetti;
import org.openspcoop2.core.config.PortaApplicativaAutorizzazioneSoggetto;
import org.openspcoop2.core.config.PortaApplicativaAzione;
import org.openspcoop2.core.config.PortaApplicativaServizio;
import org.openspcoop2.core.config.PortaApplicativaServizioApplicativo;
import org.openspcoop2.core.config.Proprieta;
import org.openspcoop2.core.config.ServizioApplicativo;
import org.openspcoop2.core.config.constants.CostantiConfigurazione;
import org.openspcoop2.core.config.constants.MTOMProcessorType;
import org.openspcoop2.core.config.constants.PortaApplicativaAzioneIdentificazione;
import org.openspcoop2.core.config.constants.StatoFunzionalita;
import org.openspcoop2.core.config.constants.TipoAutenticazione;
import org.openspcoop2.core.config.constants.TipoAutorizzazione;
import org.openspcoop2.core.config.driver.DriverConfigurazioneException;
import org.openspcoop2.core.id.IDPortaApplicativa;
import org.openspcoop2.core.id.IDServizio;
import org.openspcoop2.core.id.IDServizioApplicativo;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.core.mapping.MappingErogazionePortaApplicativa;
import org.openspcoop2.core.registry.AccordoServizioParteComune;
import org.openspcoop2.core.registry.AccordoServizioParteSpecifica;
import org.openspcoop2.core.registry.Fruitore;
import org.openspcoop2.core.registry.Soggetto;
import org.openspcoop2.core.registry.constants.CostantiRegistroServizi;
import org.openspcoop2.core.registry.driver.DriverRegistroServiziException;
import org.openspcoop2.core.registry.driver.DriverRegistroServiziNotFound;
import org.openspcoop2.core.registry.driver.IDServizioFactory;
import org.openspcoop2.message.constants.ServiceBinding;
import org.openspcoop2.protocol.engine.ProtocolFactoryManager;
import org.openspcoop2.protocol.sdk.ProtocolException;
import org.openspcoop2.protocol.sdk.constants.FunzionalitaProtocollo;
import org.openspcoop2.protocol.utils.PorteNamingUtils;
import org.openspcoop2.web.ctrlstat.core.AutorizzazioneUtilities;
import org.openspcoop2.web.ctrlstat.core.Search;
import org.openspcoop2.web.ctrlstat.costanti.CostantiControlStation;
import org.openspcoop2.web.ctrlstat.plugins.IExtendedListServlet;
import org.openspcoop2.web.ctrlstat.servlet.aps.AccordiServizioParteSpecificaCore;
import org.openspcoop2.web.ctrlstat.servlet.aps.AccordiServizioParteSpecificaCostanti;
import org.openspcoop2.web.ctrlstat.servlet.connettori.ConnettoriHelper;
import org.openspcoop2.web.ctrlstat.servlet.protocol_properties.ProtocolPropertiesUtilities;
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
public class PorteApplicativeHelper extends ConnettoriHelper {

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
			String azid = this.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_AZIONE_ID);
			String modeaz = this.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_MODE_AZIONE);

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
			if(tipoOp.equals(TipoOperazione.ADD)) {
				if (!xsd.equals(CostantiControlStation.DEFAULT_VALUE_ABILITATO) && !xsd.equals(CostantiControlStation.DEFAULT_VALUE_DISABILITATO) && !xsd.equals(CostantiControlStation.DEFAULT_VALUE_WARNING_ONLY)) {
					this.pd.setMessage(CostantiControlStation.MESSAGGIO_ERRORE_VALIDAZIONE_XSD_DEV_ESSERE_ABILITATO_DISABILITATO_O_WARNING_ONLY);
					return false;
				}
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
				 * sull'univocita' del nome la govwayConsole non permette
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
				
				if (modeaz.equals(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_MODE_REGISTER_INPUT)) {
					azione = azid;
				}
				
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
						this.pd.setMessage(MessageFormat.format(PorteApplicativeCostanti.MESSAGGIO_ERRORE_ESISTE_GIA_UNA_PORTA_APPLICATIVA_XX_APPARTENENTE_AL_SOGGETTO_YY,
								nomePorta, idSO.toString()));
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
					
					String nomeAzioneMessaggio = idSE.getAzione() != null ? idSE.getAzione() : "not set";
					String nomeSoggettoVirtualeMessaggio = !soggvirt.equals("-") ? " Virtuale" : "";
					String nomeSoggettoMessaggio = !soggvirt.equals("-") ? virtuale.getTipo() + "/" + virtuale.getNome() : idSE.getSoggettoErogatore().toString();
					String nomeServizioMessaggio = idSE.getTipo() + "/" + idSE.getNome() +  "/" + idSE.getVersione();
					this.pd.setMessage(MessageFormat.format(
							PorteApplicativeCostanti.MESSAGGIO_ERRORE_ESISTE_GIA_UNA_PORTA_APPLICATIVA_PER_IL_SERVIZIO_XX_CON_AZIONE_YY_EROGATO_DAL_SOGGETTO_ZZ,
							nomeServizioMessaggio, nomeAzioneMessaggio, nomeSoggettoVirtualeMessaggio, nomeSoggettoMessaggio));

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
							this.pd.setMessage(MessageFormat.format(PorteApplicativeCostanti.MESSAGGIO_ERRORE_ESISTE_GIA_UNA_PORTA_APPLICATIVA_CON_NOME_XX, nomePorta));
							return false;
						}
					}
				}
			}
			
			if (modeaz == null) {
				this.pd.setMessage(PorteApplicativeCostanti.MESSAGGIO_ERRORE_DATI_INCOMPLETI_NON_E_STATA_TROVATA_NESSUNA_MODALITA_AZIONE);
				return false;
			}
			
			if (modeaz.equals(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_MODE_REGISTER_INPUT) && (azid == null)) {
				this.pd.setMessage(PorteApplicativeCostanti.MESSAGGIO_ERRORE_DATI_INCOMPLETI_NON_E_STATA_TROVATA_NESSUNA_AZIONE_ASSOCIATA_AL_SERVIZIO_SCEGLIERE_UNA_DELLE_ALTRE_MODALITA);
				return false;
			}
			if ((modeaz.equals(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_MODE_URL_BASED) || 
					modeaz.equals(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_MODE_HEADER_BASED) ||
					modeaz.equals(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_MODE_CONTENT_BASED)) && (azione==null || azione.equals(""))) {
				this.pd.setMessage(PorteApplicativeCostanti.MESSAGGIO_ERRORE_DATI_INCOMPLETI_E_NECESSARIO_INDICARE_PATTERN_AZIONE);
				return false;
			}
			
			if ((modeaz != null) && !modeaz.equals(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_MODE_REGISTER_INPUT) && 
					!modeaz.equals(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_MODE_INPUT_BASED) &&
					!modeaz.equals(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_MODE_SOAP_ACTION_BASED) &&
					!modeaz.equals(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_MODE_DELEGATED_BY) &&
					(azione.indexOf(" ") != -1)) {
				this.pd.setMessage(PorteApplicativeCostanti.MESSAGGIO_ERRORE_NON_INSERIRE_SPAZI_NEI_CAMPI_DI_TESTO);
				return false;
			}

//			// Controllo che i campi "select" abbiano uno dei valori ammessi
//			if ((modeaz != null) && !modeaz.equals(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_MODE_HEADER_BASED) && 
//					!modeaz.equals(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_MODE_REGISTER_INPUT) && 
//					!modeaz.equals(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_MODE_URL_BASED) && 
//					!modeaz.equals(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_MODE_CONTENT_BASED) && 
//					!modeaz.equals(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_MODE_INPUT_BASED) && 
//					!modeaz.equals(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_MODE_SOAP_ACTION_BASED) && 
//					!modeaz.equals(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_MODE_PROTOCOL_BASED) && 
//					!modeaz.equals(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_MODE_WSDL_BASED)) {
//				this.pd.setMessage(PorteApplicativeCostanti.MESSAGGIO_ERRORE_MODE_AZIONE_DEV_ESSERE_USER_INPUT_REGISTER_INPUT_URL_BASED_CONTENT_BASED_INPUT_BASED_SOAP_ACTION_BASED_PROTOCOL_BASED_O_WSDL_BASED);
//				return false;
//			}
			

			// Se autenticazione = custom, nomeauth dev'essere specificato
			if (CostantiControlStation.DEFAULT_VALUE_PARAMETRO_PORTE_AUTENTICAZIONE_CUSTOM.equals(autenticazione) && 
					(autenticazioneCustom == null || autenticazioneCustom.equals(""))) {
				this.pd.setMessage(MessageFormat.format(PorteApplicativeCostanti.MESSAGGIO_ERRORE_INDICARE_UN_NOME_PER_L_AUTENTICAZIONE_XX, CostantiControlStation.DEFAULT_VALUE_PARAMETRO_PORTE_AUTENTICAZIONE_CUSTOM));
				return false;
			}

			// Se autorizzazione = custom, nomeautor dev'essere specificato
			if (CostantiControlStation.DEFAULT_VALUE_PARAMETRO_PORTE_AUTORIZZAZIONE_CUSTOM.equals(autorizzazione) && 
					(autorizzazioneCustom == null || autorizzazioneCustom.equals(""))) {
				this.pd.setMessage(MessageFormat.format(PorteApplicativeCostanti.MESSAGGIO_ERRORE_INDICARE_UN_NOME_PER_L_AUTORIZZAZIONE_XX, CostantiControlStation.DEFAULT_VALUE_PARAMETRO_PORTE_AUTORIZZAZIONE_CUSTOM));
				return false;
			}
			
			PortaApplicativa pa = null;
			if (TipoOperazione.CHANGE.equals(tipoOp)){
				pa = this.porteApplicativeCore.getPortaApplicativa(Long.parseLong(idPorta)); 
			}
			
			List<String> ruoli = new ArrayList<>();
			if(pa!=null && pa.getRuoli()!=null && pa.getRuoli().sizeRuoloList()>0){
				for (int i = 0; i < pa.getRuoli().sizeRuoloList(); i++) {
					ruoli.add(pa.getRuoli().getRuolo(i).getNome());
				}
			}
			
			if (TipoOperazione.ADD.equals(tipoOp)){
				
				
				String gestioneToken = this.getParameter(CostantiControlStation.PARAMETRO_PORTE_GESTIONE_TOKEN);
				String gestioneTokenPolicy = this.getParameter(CostantiControlStation.PARAMETRO_PORTE_GESTIONE_TOKEN_POLICY);
				String gestioneTokenValidazioneInput = this.getParameter(CostantiControlStation.PARAMETRO_PORTE_GESTIONE_TOKEN_VALIDAZIONE_INPUT);
				String gestioneTokenIntrospection = this.getParameter(CostantiControlStation.PARAMETRO_PORTE_GESTIONE_TOKEN_INTROSPECTION);
				String gestioneTokenUserInfo = this.getParameter(CostantiControlStation.PARAMETRO_PORTE_GESTIONE_TOKEN_USERINFO);
				String gestioneTokenTokenForward = this.getParameter(CostantiControlStation.PARAMETRO_PORTE_GESTIONE_TOKEN_TOKEN_FORWARD);
				String autorizzazione_tokenOptions = this.getParameter(CostantiControlStation.PARAMETRO_PORTE_AUTORIZZAZIONE_TOKEN_OPTIONS);
				String autorizzazioneScope = this.getParameter(CostantiControlStation.PARAMETRO_PORTE_AUTORIZZAZIONE_SCOPE);
				String autorizzazioneScopeMatch = this.getParameter(CostantiControlStation.PARAMETRO_SCOPE_MATCH);
				
				if(this.controlloAccessiCheck(tipoOp, autenticazione, autenticazioneOpzionale, 
						autorizzazione, autorizzazioneAutenticati, autorizzazioneRuoli, 
						autorizzazioneRuoliTipologia, ruoloMatch, 
						isSupportatoAutenticazione, false, pa, ruoli,gestioneToken, gestioneTokenPolicy, 
						gestioneTokenValidazioneInput, gestioneTokenIntrospection, gestioneTokenUserInfo, gestioneTokenTokenForward,
						autorizzazione_tokenOptions,
						autorizzazioneScope,autorizzazioneScopeMatch)==false){
					return false;
				}
			}
			
			if (TipoOperazione.CHANGE.equals(tipoOp) && isSupportatoAutenticazione) {
				
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
							this.pd.setMessage(MessageFormat.format(
									PorteApplicativeCostanti.MESSAGGIO_ERRORE_NON_E_POSSIBILE_MODIFICARE_IL_TIPO_DI_AUTENTICAZIONE_DA_XX_A_YY_POICHÈ_RISULTANO_ASSOCIATI_AL_SERVIZIO_DEI_FRUITORI_CON_CREDENZIALI_NON_COMPATIBILI_NELLA_MODALITA_DI_ACCESSO_CON_IL_NUOVO_TIPO_DI_AUTENTICAZIONE,
									pa.getAutenticazione(), autenticazione));
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
				this.pd.setMessage(PorteApplicativeCostanti.MESSAGGIO_ERRORE_DATI_INCOMPLETI_E_NECESSARIO_INDICARE_UN_SERVIZIO_APPLICATIVO);
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
				String nomeSoggettoMessaggio = tipoprov + "/" + nomeprov;
				this.pd.setMessage(MessageFormat.format(
						PorteApplicativeCostanti.MESSAGGIO_ERRORE_IL_SERVIZIO_APPLICATIVO_DEV_ESSERE_SCELTO_TRA_QUELLI_DEFINITI_NEL_PANNELLO_SERVIZI_APPLICATIVI_ED_ASSOCIATI_AL_SOGGETTO_XX,
						nomeSoggettoMessaggio));
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
					this.pd.setMessage(MessageFormat.format(PorteApplicativeCostanti.MESSAGGIO_ERRORE_IL_SERVIZIO_APPLICATIVO_XX_E_GIA_STATO_ASSOCIATO_ALLA_PORTA_APPLICATIVA_YY, servizioApplicativo, nomeporta));
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
				this.pd.setMessage(PorteApplicativeCostanti.MESSAGGIO_ERRORE_DATI_INCOMPLETI_E_NECESSARIO_INDICARE_UN_SOGGETTO);
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
					String nomeSoggettoMessaggio = tipoSoggettoSelezionato + "/"+ nomeSoggettoSelezionato;
					this.pd.setMessage(MessageFormat.format(PorteApplicativeCostanti.MESSAGGIO_ERRORE_IL_SOGGETTO_XX_EGRAVE_GIA_STATO_ASSOCIATO_ALLA_PORTA_APPLICATIVA_YY,
							nomeSoggettoMessaggio, nomeporta));
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
				this.pd.setMessage(MessageFormat.format(PorteApplicativeCostanti.MESSAGGIO_ERRORE_DATI_INCOMPLETI_E_NECESSARIO_INDICARE_XX, tmpElenco));
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

				for (int i = 0; i < pa.sizeProprietaList(); i++) {
					Proprieta tmpProp = pa.getProprieta(i);
					if (nome.equals(tmpProp.getNome())) {
						giaRegistrato = true;
						break;
					}
				}

				if (giaRegistrato) {
					this.pd.setMessage(MessageFormat.format(
							PorteApplicativeCostanti.MESSAGGIO_ERRORE_LA_PROPERTY_XX_E_GIA_STATA_ASSOCIATA_ALLA_PORTA_APPLICATIVA_YY, nome,
							nomeporta));
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
				this.pd.setMessage(MessageFormat.format(PorteApplicativeCostanti.MESSAGGIO_ERRORE_DATI_INCOMPLETI_E_NECESSARIO_INDICARE_XX, tmpElenco));
				return false;
			}

			// Controllo che non ci siano spazi nei campi di testo
			//if ((nome.indexOf(" ") != -1) || (valore.indexOf(" ") != -1)) {
			if ((nome.indexOf(" ") != -1) ) {
				this.pd.setMessage(PorteApplicativeCostanti.MESSAGGIO_ERRORE_NON_INSERIRE_SPAZI_NEI_NOMI);
				return false;
			}
			if(valore.startsWith(" ") || valore.endsWith(" ")){
				this.pd.setMessage(PorteApplicativeCostanti.MESSAGGIO_ERRORE_NON_INSERIRE_SPAZI_ALL_INIZIO_O_ALLA_FINE_DEI_VALORI);
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
					this.pd.setMessage(MessageFormat.format(PorteApplicativeCostanti.MESSAGGIO_ERRORE_LA_PROPRIETA_DI_MESSAGE_SECURITY_XX_E_GIA_STATO_ASSOCIATA_ALLA_PORTA_APPLICATIVA_YY,nome, nomeporta));
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
				this.pd.setMessage(MessageFormat.format(PorteApplicativeCostanti.MESSAGGIO_ERRORE_DATI_INCOMPLETI_E_NECESSARIO_INDICARE_XX, tmpElenco));
				return false;
			}

			// Controllo che non ci siano spazi nei campi di testo
			//if ((nome.indexOf(" ") != -1) || (valore.indexOf(" ") != -1)) {
			if ((nome.indexOf(" ") != -1) ) {
				this.pd.setMessage(PorteApplicativeCostanti.MESSAGGIO_ERRORE_NON_INSERIRE_SPAZI_NEI_NOMI);
				return false;
			}
			if(valore.startsWith(" ") || valore.endsWith(" ")){
				this.pd.setMessage(PorteApplicativeCostanti.MESSAGGIO_ERRORE_NON_INSERIRE_SPAZI_ALL_INIZIO_O_ALLA_FINE_DEI_VALORI);
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
					this.pd.setMessage(
							MessageFormat.format(PorteApplicativeCostanti.MESSAGGIO_ERRORE_LA_PROPRIETA_DI_MESSAGE_SECURITY_XX_E_GIA_STATO_ASSOCIATA_ALLA_PORTA_APPLICATIVA_YY,nome, nomeporta));
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
			String servizio, String[] serviziList, String[] serviziListLabel, String azione, String[] azioniList,  String[] azioniListLabel,  String stateless, String ricsim, String ricasim, 
			String idsogg, String idPorta, String statoValidazione, String tipoValidazione, String gestBody, String gestManifest,String integrazione,
			int numCorrApp,String scadcorr,String autorizzazioneContenuti, String protocollo,
			int numSA,	 int numRuoli, String ruoloMatch,
			String statoMessageSecurity ,String statoMTOM ,int numCorrelazioneReq , int numCorrelazioneRes, int numProprProt,String applicaMTOM,
			String behaviour,
			String[] servizioApplicativoList, String servizioApplicativo, Long idSa,
			String autenticazione, String autorizzazione,
			String autenticazioneOpzionale, String autenticazioneCustom, String autorizzazioneCustom,
			boolean isSupportatoAutenticazioneSoggetti,
			String autorizzazioneAutenticati,String autorizzazioneRuoli,String autorizzazioneRuoliTipologia,
			AccordoServizioParteSpecifica asps, AccordoServizioParteComune aspc, ServiceBinding serviceBinding,
			String statoPorta, String modeaz, String azid, String patternAzione, String forceWsdlBased, boolean usataInConfigurazioni, boolean usataInConfigurazioneDefault,
			boolean ricercaPortaAzioneDelegata, String nomePortaDelegante,
			String gestioneToken, String[] gestioneTokenPolicyLabels, String[] gestioneTokenPolicyValues,
			String gestioneTokenPolicy, String gestioneTokenOpzionale,
			String gestioneTokenValidazioneInput, String gestioneTokenIntrospection, String gestioneTokenUserInfo, String gestioneTokenForward,
			String autenticazioneTokenIssuer,String autenticazioneTokenClientId,String autenticazioneTokenSubject,String autenticazioneTokenUsername,String autenticazioneTokenEMail,
			String autorizzazione_tokenOptions,
			String autorizzazioneScope, int numScope, String autorizzazioneScopeMatch) throws Exception {

		Boolean contaListe = ServletUtils.getContaListeFromSession(this.session);

		// Soggetto virtuale?
		Boolean soggVirt = (Boolean) this.session.getAttribute(CostantiControlStation.SESSION_PARAMETRO_GESTIONE_SOGGETTI_VIRTUALI);
		
		Boolean confPers = (Boolean) this.session.getAttribute(CostantiControlStation.SESSION_PARAMETRO_GESTIONE_CONFIGURAZIONI_PERSONALIZZATE);
		
		// prelevo il flag che mi dice da quale pagina ho acceduto la sezione delle porte delegate
		Integer parentPA = ServletUtils.getIntegerAttributeFromSession(PorteApplicativeCostanti.ATTRIBUTO_PORTE_APPLICATIVE_PARENT, this.session);
		if(parentPA == null) parentPA = PorteApplicativeCostanti.ATTRIBUTO_PORTE_APPLICATIVE_PARENT_NONE;
		
		boolean isConfigurazione = parentPA.intValue() == PorteApplicativeCostanti.ATTRIBUTO_PORTE_APPLICATIVE_PARENT_CONFIGURAZIONE; 
		
		boolean datiInvocazione = false;
		boolean datiAltro = false;
		if(isConfigurazione) {
			if(usataInConfigurazioneDefault) {
				datiInvocazione = ServletUtils.isCheckBoxEnabled(this.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_CONFIGURAZIONE_DATI_INVOCAZIONE));
			}
			datiAltro = ServletUtils.isCheckBoxEnabled(this.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_CONFIGURAZIONE_ALTRO));
			
			DataElement de = new DataElement();
			de.setName(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_CONFIGURAZIONE_DATI_INVOCAZIONE);
			de.setType(DataElementType.HIDDEN);
			de.setValue(datiInvocazione+"");
			dati.addElement(de);
			
			de = new DataElement();
			de.setName(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_CONFIGURAZIONE_ALTRO);
			de.setType(DataElementType.HIDDEN);
			de.setValue(datiAltro+"");
			dati.addElement(de);
		}
		

			
		Parameter pIdSogg = null, pIdPorta = null, pIdAsps = null, pNomePorta = null;

		if(!tipoOp.equals(TipoOperazione.ADD)) {
			pIdSogg = new Parameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_SOGGETTO, idsogg);
			pIdPorta = new Parameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID, idPorta);
			pIdAsps = new Parameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_ASPS, asps.getId()+ "");
			pNomePorta = new Parameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_NOME, nomePorta);
		}
		
		// servicebinding hidden
		dati.add(this.getServiceBindingDataElement(serviceBinding));
		
		
		// *************** Nome/Descrizione *********************
		
		DataElement de = new DataElement();
		if(datiInvocazione) {
			de.setLabel(PorteApplicativeCostanti.LABEL_PARAMETRO_TITOLO_PORTE_APPLICATIVE_DATI_INVOCAZIONE);
		}
		else {
			de.setLabel(PorteApplicativeCostanti.LABEL_PARAMETRO_TITOLO_PORTE_APPLICATIVE_DATI_GENERALI);
		}
		de.setType(DataElementType.TITLE);
		dati.addElement(de);
				
		de = new DataElement();
		de.setLabel(PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_NOME);
		de.setValue(nomePorta);
		if(isConfigurazione) {
			de.setType(DataElementType.HIDDEN);
		}
		else {				
			de.setType(DataElementType.TEXT_EDIT);
			de.setRequired(true);
		}
		de.setName(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_NOME_PORTA);
		dati.addElement(de);

		if(datiInvocazione) {
			
			ConfigurazioneProtocollo configProt = this.confCore.getConfigurazioneProtocollo(protocollo);
			
			boolean useInterfaceNameInInvocationURL = this.useInterfaceNameInInvocationURL(protocollo, serviceBinding);
			
			String prefix = configProt.getUrlInvocazioneServizioPA();
			prefix = prefix.trim();
			if(useInterfaceNameInInvocationURL) {
				if(prefix.endsWith("/")==false) {
					prefix = prefix + "/";
				}
			}
			
			String urlInvocazione = prefix;
			if(useInterfaceNameInInvocationURL) {
				PorteNamingUtils utils = new PorteNamingUtils(ProtocolFactoryManager.getInstance().getProtocolFactoryByName(protocollo));
				urlInvocazione = urlInvocazione + utils.normalizePA(nomePorta);
			}
			
			de = new DataElement();
			if(ServiceBinding.SOAP.equals(serviceBinding)) {
				de.setLabel(PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_URL_INVOCAZIONE);
			}
			else {
				de.setLabel(PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_BASE_URL_INVOCAZIONE);
			}
			de.setValue(urlInvocazione);
			de.setType(DataElementType.TEXT);
			de.setName(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_NOME_PORTA+"___LABEL");
			dati.addElement(de);
		}
		
		de = new DataElement();
		de.setLabel(PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_DESCRIZIONE);
		de.setValue(descr);
		if(isConfigurazione) {
			de.setType(DataElementType.HIDDEN);
		} else {
			de.setType(DataElementType.TEXT_EDIT);
		}
		de.setName(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_DESCRIZIONE);
		dati.addElement(de);

		de = new DataElement();
		de.setLabel(PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_STATO_PORTA);
		if(statoPorta==null || "".equals(statoPorta)){
			statoPorta = CostantiConfigurazione.ABILITATO.toString();
		}
		de.setName(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_STATO_PORTA);
		if(!isConfigurazione || datiAltro) {
			List<String> statoValues = new ArrayList<>();
			statoValues.add(CostantiConfigurazione.ABILITATO.toString());
			statoValues.add(CostantiConfigurazione.DISABILITATO.toString());
			de.setValues(statoValues);
			de.setSelected(statoPorta);
			de.setType(DataElementType.SELECT);
		}
		else {
			de.setValue(statoPorta);
			de.setType(DataElementType.HIDDEN);
		}
		dati.addElement(de);

		
		
		if(!isConfigurazione) {
			de = new DataElement();
			de.setLabel(PorteApplicativeCostanti.LABEL_PARAMETRO_TITOLO_PORTE_APPLICATIVE_DATI_SERVIZIO);
			de.setType(DataElementType.TITLE);
			dati.addElement(de);
		}
		
		
		
		// *************** Soggetto Virtuale *********************
		
		if (soggVirt) {
			if(!isConfigurazione) {
				de = new DataElement();
				de.setLabel(PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_SOGGETTO_VIRTUALE);
				de.setType(DataElementType.SUBTITLE);
				dati.addElement(de);
			}
		}
		
		de = new DataElement();
		de.setLabel(PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_NOME);
		de.setName(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_SOGGETTO_VIRTUALE);
		if (soggVirt) {
			if(!isConfigurazione) {
				de.setType(DataElementType.SELECT);
				de.setValues(soggettiList);
				de.setLabels(soggettiListLabel);
				de.setSelected(soggvirt);
				de.setPostBack(true);
			}
			else {
				de.setValue(soggvirt);
				de.setType(DataElementType.HIDDEN);
			}
		}else{
			de.setValue("-");
			de.setType(DataElementType.HIDDEN);
		}
		dati.addElement(de);

		
		
		// *************** Servizio *********************
		
		if(!isConfigurazione) {
			de = new DataElement();
			de.setLabel(PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_SERVIZIO);
			de.setType(DataElementType.SUBTITLE);
			dati.addElement(de);
		}

		de = new DataElement();
		de.setLabel(PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_NOME);
		de.setName(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_SERVIZIO);
		if(!usataInConfigurazioni) {
			de.setPostBack(true);
			de.setType(DataElementType.SELECT);
			de.setValues(serviziList);
			de.setLabels(serviziListLabel);
			de.setSelected(servizio);
		} else {
			
			de.setType(DataElementType.HIDDEN);
			de.setValue(servizio); 
			dati.addElement(de);
			
			if(this.isModalitaCompleta()) {
				DataElement deLabel = new DataElement();
				deLabel.setLabel(PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_NOME);
				deLabel.setType(DataElementType.TEXT);
				for (int i = 0; i < serviziList.length; i++) {
					if(serviziList[i]!=null && serviziList[i].equals(servizio)){
						deLabel.setValue(serviziListLabel[i]);
						break;
					}
				}
				dati.addElement(deLabel);
			}
		}		
		dati.addElement(de);
		
		
		// *************** Azione *********************
		List<PortaApplicativaAzioneIdentificazione> allImplementationIdentificationResourceModes = getModalitaIdentificazionePorta(protocollo, serviceBinding);
		
		List<String> azTmp = new ArrayList<>();
		String[] tipoModeAzione = null;
		String[] tipoModeAzioneLabel = null;
		
		if(tipoOp.equals(TipoOperazione.ADD) || (tipoOp.equals(TipoOperazione.CHANGE) && !usataInConfigurazioni))
			azTmp.add(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_MODE_REGISTER_INPUT);
		
		if(allImplementationIdentificationResourceModes != null && allImplementationIdentificationResourceModes.size() >0) {
			
			
			for (int i = 0; i < allImplementationIdentificationResourceModes.size(); i++) {
				PortaApplicativaAzioneIdentificazione pdAi = allImplementationIdentificationResourceModes.get(i);
				azTmp.add(pdAi.toString());
			}
			
			Collections.sort(azTmp);
						
			tipoModeAzione = new String [azTmp.size()];
			tipoModeAzioneLabel = new String [azTmp.size()];
			
			for (int i = 0; i < azTmp.size(); i++) {
				String azMod = azTmp.get(i);
				tipoModeAzione[i] = azMod;
				tipoModeAzioneLabel[i] = this.getPortaApplicativaAzioneIdentificazioneLabel(azMod);
			}
		}
		
		if(!isConfigurazione || datiInvocazione) {
			de = new DataElement();
			if(datiInvocazione) {
				if(ServiceBinding.REST.equals(serviceBinding)) {
					de.setLabel(CostantiControlStation.LABEL_PARAMETRO_PORTA_RISORSA_MODALITA);
				}
				else {
					de.setLabel(CostantiControlStation.LABEL_PARAMETRO_PORTA_AZIONE_MODALITA);
				}
			}
			else {
				if(ServiceBinding.REST.equals(serviceBinding)) {
					de.setLabel(CostantiControlStation.LABEL_PARAMETRO_RISORSA);
				}
				else {
					de.setLabel(CostantiControlStation.LABEL_PARAMETRO_AZIONE);
				}
			}
			de.setType(DataElementType.SUBTITLE);
			dati.addElement(de);
		}
		
		boolean viewOnlyModeDatiAzione = datiInvocazione && modeaz!=null && !"".equals(modeaz) && this.isModalitaStandard() &&
				!azTmp.contains(modeaz);
		// se true viewOnlyModeDatiAzione e' stato usato un valore modificato in avanzato e non supportato in standard
		
		de = new DataElement();
		de.setLabel(PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_MODALITA_IDENTIFICAZIONE);
		de.setName(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_MODE_AZIONE);
		if(!usataInConfigurazioni || datiInvocazione) {
			if(viewOnlyModeDatiAzione || (tipoModeAzione!=null && tipoModeAzione.length==1)) {
				de.setType(DataElementType.HIDDEN);
				de.setValue(modeaz);
				dati.addElement(de);
				
				de = new DataElement();
				de.setLabel(PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_MODALITA_IDENTIFICAZIONE);
				de.setName(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_MODE_AZIONE+"__LABEL");
				de.setType(DataElementType.TEXT);
				de.setValue(this.getPortaApplicativaAzioneIdentificazioneLabel(modeaz));
			}
			else {
				de.setType(DataElementType.SELECT);
				de.setValues(tipoModeAzione);
				de.setLabels(tipoModeAzioneLabel); 
				de.setSelected(modeaz);
				de.setPostBack(true);
			}
		} else {
			de.setType(DataElementType.HIDDEN);
			de.setValue(modeaz);
		}
		
		dati.addElement(de);
		
		boolean addHiddenAzione = false;
		
		if(!usataInConfigurazioni || datiInvocazione) {
	
			if(!usataInConfigurazioni && PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_MODE_DELEGATED_BY.equals(modeaz)) {
				
				// azione non modificabile, metto la lista delle azioni
				de = new DataElement();
				de.setLabel(PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_NOME);
				de.setName(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_AZIONE);
				de.setValue(azione);
				dati.addElement(de);
				
				addHiddenAzione = true;
				
			}
			else {
			
				if ((modeaz != null) && modeaz.equals(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_MODE_REGISTER_INPUT)) {
					de = new DataElement();
					de.setLabel(PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_NOME);
					de.setType(DataElementType.SELECT);
					de.setName(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_AZIONE_ID);
					de.setValues(azioniList);
					de.setLabels(azioniListLabel);
					de.setSelected(azid);
					dati.addElement(de);
				} else {
		
					de = new DataElement();
					if ((modeaz != null) && (modeaz.equals(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_MODE_URL_BASED) 
							|| modeaz.equals(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_MODE_CONTENT_BASED))) {
						de.setLabel(PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_PATTERN);
						de.setValue(patternAzione);
						de.setRequired(true);
					} 
					else if ((modeaz != null) && modeaz.equals(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_MODE_HEADER_BASED)
							) {
						de.setLabel(PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_NOME);
						de.setValue(patternAzione);
						de.setRequired(true);
					} 
					else {
						de.setLabel(PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_NOME);
						de.setValue(azione);
					}
		
					if (!PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_MODE_INPUT_BASED.equals(modeaz) && 
							!PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_MODE_SOAP_ACTION_BASED.equals(modeaz) && 
							!PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_MODE_PROTOCOL_BASED.equals(modeaz) && 
							!PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_MODE_INTERFACE_BASED.equals(modeaz) ){
						if(viewOnlyModeDatiAzione) {
							de.setType(DataElementType.TEXT);
							de.setRequired(false);
						}
						else {
							de.setType(DataElementType.TEXT_EDIT);
						}
					}else
						de.setType(DataElementType.HIDDEN);
					de.setName(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_AZIONE);
					dati.addElement(de);
				}
		
				// se non e' selezionata la modalita userInput / wsdlbased / registerInput faccio vedere il check box forceWsdlbased
				de = new DataElement();
				de.setLabel(PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_FORCE_INTERFACE_BASED);
				if( modeaz!= null && (
							!modeaz.equals(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_MODE_REGISTER_INPUT) &&
							!modeaz.equals(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_MODE_PROTOCOL_BASED) &&
							!modeaz.equals(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_MODE_INTERFACE_BASED))
					){
		
					if(viewOnlyModeDatiAzione) {
						de.setType(DataElementType.TEXT);
						if( ServletUtils.isCheckBoxEnabled(forceWsdlBased) || CostantiRegistroServizi.ABILITATO.equals(forceWsdlBased) ){
							de.setValue(CostantiConfigurazione.ABILITATO.getValue());
						}
						else {
							de.setValue(CostantiConfigurazione.DISABILITATO.getValue());
						}
					}
					else {
						de.setType(DataElementType.CHECKBOX);
						if( ServletUtils.isCheckBoxEnabled(forceWsdlBased) || CostantiRegistroServizi.ABILITATO.equals(forceWsdlBased) ){
							de.setSelected(true);
						}
					}
				}
				else{
					de.setType(DataElementType.HIDDEN);
					de.setValue(forceWsdlBased);
				}
				de.setName(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_FORCE_INTERFACE_BASED);
				dati.addElement(de);
				
				if( modeaz!= null && (
						!modeaz.equals(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_MODE_REGISTER_INPUT) &&
						!modeaz.equals(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_MODE_PROTOCOL_BASED) &&
						!modeaz.equals(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_MODE_INTERFACE_BASED))
				){
					de = new DataElement();
					de.setName(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_LIST_AZIONI_READ_ONLY);
					de.setLabel(this.getLabelAzioni(serviceBinding));
					Map<String,String> azioni = this.porteApplicativeCore.getAzioniConLabel(asps, aspc, false, true, new ArrayList<String>());
					StringBuffer bf = new StringBuffer();
					for (String az : azioni.keySet()) {
						if(bf.length()>0) {
							bf.append("\n");
						}
						bf.append(azioni.get(az));
					}
					de.setType(DataElementType.TEXT_AREA_NO_EDIT);
					if(azioni.size()<=5) {
						de.setRows(azioni.size());
					}
					else {
						de.setRows(5);
					}
					de.setValue(bf.toString());
					dati.addElement(de);
				}
			}
		
		}
		else {
			
			addHiddenAzione = true;
			
		}
		
		if(addHiddenAzione) {
		
			de = new DataElement();
			de.setLabel(PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_NOME);
			de.setType(DataElementType.HIDDEN);
			de.setName(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_AZIONE_ID);
			de.setValue(azid);
			dati.addElement(de);
			
			if(this.isModalitaCompleta()) {
				DataElement deLabel = new DataElement();
				deLabel.setLabel(PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_MODALITA_IDENTIFICAZIONE);
				deLabel.setType(DataElementType.TEXT);
				deLabel.setValue(modeaz);
				dati.addElement(deLabel);
			}
			
			de = new DataElement();
			if ((modeaz != null) && (modeaz.equals(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_MODE_URL_BASED) 
					|| modeaz.equals(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_MODE_CONTENT_BASED))) {
				de.setValue(patternAzione);
			} 
			else if ((modeaz != null) && modeaz.equals(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_MODE_HEADER_BASED)
					) {
				de.setValue(patternAzione);
			} 
			else {
				de.setValue(azione);
			}
			de.setType(DataElementType.HIDDEN);
			de.setName(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_AZIONE);
			dati.addElement(de);
			
			if(this.isModalitaCompleta()) {
				DataElement deLabel = new DataElement();
				deLabel.setType(DataElementType.TEXT);
				if ((modeaz != null) && (modeaz.equals(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_MODE_URL_BASED) 
						|| modeaz.equals(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_MODE_CONTENT_BASED))) {
					deLabel.setLabel(PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_PATTERN);
					deLabel.setValue(patternAzione);
				} 
				else if ((modeaz != null) && modeaz.equals(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_MODE_HEADER_BASED)
						) {
					deLabel.setLabel(PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_NOME);
					deLabel.setValue(patternAzione);
				} 
				else {
					deLabel.setLabel(PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_NOME);
					deLabel.setValue(azione);
				}
				dati.addElement(deLabel);
			}
			
			if(this.isModalitaCompleta()) {
				if ((modeaz != null) && (modeaz.equals(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_MODE_DELEGATED_BY))){
					de = new DataElement();
					de.setLabel(PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_PORTA_DELEGANTE);
					de.setType(DataElementType.TEXT);
					de.setValue(nomePortaDelegante);
					dati.addElement(de);
				}
				else {
					DataElement deLabel = new DataElement();
					deLabel.setLabel(PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_RICERCA_PORTA_AZIONE_DELEGATA);
					deLabel.setType(DataElementType.TEXT);
					deLabel.setValue(ricercaPortaAzioneDelegata ? CostantiConfigurazione.ABILITATO.getValue() : CostantiConfigurazione.DISABILITATO.getValue() );
					dati.addElement(deLabel);
				}
			}
			
			de = new DataElement();
			de.setLabel(PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_FORCE_INTERFACE_BASED);
			de.setType(DataElementType.HIDDEN);
			de.setValue(forceWsdlBased);
			de.setName(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_FORCE_INTERFACE_BASED);
			dati.addElement(de);
			
			if(this.isModalitaCompleta()) {
				if( modeaz!= null && (
						!modeaz.equals(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_MODE_REGISTER_INPUT) &&
						!modeaz.equals(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_MODE_PROTOCOL_BASED) &&
						!modeaz.equals(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_MODE_INTERFACE_BASED))
				){
					DataElement deLabel = new DataElement();
					deLabel.setLabel(PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_FORCE_INTERFACE_BASED);
					deLabel.setType(DataElementType.TEXT);
					deLabel.setValue(ServletUtils.isCheckBoxEnabled(forceWsdlBased) ? CostantiConfigurazione.ABILITATO.getValue() : CostantiConfigurazione.DISABILITATO.getValue() );
					dati.addElement(deLabel);
				}
			}
			
		}
		
		
		// *************** ServizioApplicativo Erogatore *********************
		
		if(!isConfigurazione && TipoOperazione.CHANGE.equals(tipoOp)){
					
			de = new DataElement();
			de.setType(DataElementType.TITLE);
			de.setLabel(PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_SERVIZI_APPLICATIVI_EROGATORI);
			dati.addElement(de);
			
			de = new DataElement();
			de.setType(DataElementType.LINK);
			
			de.setUrl(ServiziApplicativiCostanti.SERVLET_NAME_SERVIZI_APPLICATIVI_ENDPOINT, pIdSogg, pIdPorta, pIdAsps,
					new Parameter(ServiziApplicativiCostanti.PARAMETRO_SERVIZI_APPLICATIVI_NOME_SERVIZIO_APPLICATIVO, servizioApplicativo),
					new Parameter(ServiziApplicativiCostanti.PARAMETRO_SERVIZI_APPLICATIVI_ID_SERVIZIO_APPLICATIVO, idSa+""));
			ServletUtils.setDataElementVisualizzaLabel(de);


			dati.addElement(de);
		
			
		}
		
		// *************** Controllo degli Accessi *********************
	
		if(!tipoOp.equals(TipoOperazione.ADD)) {
			if(!isConfigurazione) {
				this.controlloAccessi(dati);
				// 	controllo accessi
				de = new DataElement();
				de.setType(DataElementType.LINK);
				de.setUrl(PorteApplicativeCostanti.SERVLET_NAME_PORTE_APPLICATIVE_CONTROLLO_ACCESSI, pIdSogg, pIdPorta, pIdAsps);
				String statoControlloAccessi = this.getLabelStatoControlloAccessi(gestioneToken,autenticazione, autenticazioneOpzionale, autenticazioneCustom, autorizzazione, autorizzazioneContenuti,autorizzazioneCustom);
				ServletUtils.setDataElementCustomLabel(de, PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_CONTROLLO_ACCESSI, statoControlloAccessi);
				dati.addElement(de);
			}
		}else {
			// Pintori 29/11/2017 Gestione Accessi spostata nella servlet PorteApplicativeControlloAccessi,  in ADD devo mostrare comunque la form.
			
			this.controlloAccessiGestioneToken(dati, tipoOp, gestioneToken, gestioneTokenPolicyLabels, gestioneTokenPolicyValues, 
					gestioneTokenPolicy, gestioneTokenOpzionale, gestioneTokenValidazioneInput, gestioneTokenIntrospection, gestioneTokenUserInfo, gestioneTokenForward, null,false);

			this.controlloAccessiAutenticazione(dati, tipoOp, autenticazione, autenticazioneCustom, autenticazioneOpzionale, confPers, isSupportatoAutenticazioneSoggetti,false,
					gestioneToken, gestioneTokenPolicy, autenticazioneTokenIssuer, autenticazioneTokenClientId, autenticazioneTokenSubject, autenticazioneTokenUsername, autenticazioneTokenEMail);
			
			String urlAutorizzazioneAutenticati = null;
			String urlAutorizzazioneRuoli = null;
			String urlAutorizzazioneScope = null;
			String servletChiamante = PorteApplicativeCostanti.SERVLET_NAME_PORTE_APPLICATIVE_ADD;
			
			this.controlloAccessiAutorizzazione(dati, tipoOp, servletChiamante, null,
					autenticazione, autorizzazione, autorizzazioneCustom, 
					autorizzazioneAutenticati, urlAutorizzazioneAutenticati, 0, null, null,
					autorizzazioneRuoli,  urlAutorizzazioneRuoli, numRuoli, null, 
					autorizzazioneRuoliTipologia, ruoloMatch,
					confPers, isSupportatoAutenticazioneSoggetti, contaListe, false, false,autorizzazioneScope,urlAutorizzazioneScope,numScope,null,autorizzazioneScopeMatch,
					gestioneToken, gestioneTokenPolicy, autorizzazione_tokenOptions);
			
			this.controlloAccessiAutorizzazioneContenuti(dati, autorizzazioneContenuti);
		}
		
		
		
		// *************** Validazione Contenuti *********************
		if(!tipoOp.equals(TipoOperazione.ADD)) {
			if(!isConfigurazione) {
				de = new DataElement();
				de.setType(DataElementType.TITLE);
				de.setLabel(PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_VALIDAZIONE_CONTENUTI);
				dati.addElement(de);
				
				
				// Validazione Contenuti
				de = new DataElement();
				de.setType(DataElementType.LINK);
				de.setUrl(PorteApplicativeCostanti.SERVLET_NAME_PORTE_APPLICATIVE_VALIDAZIONE_CONTENUTI, pIdSogg, pIdPorta, pIdAsps);
				ServletUtils.setDataElementCustomLabel(de, PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_VALIDAZIONE_CONTENUTI, statoValidazione);
				dati.addElement(de);
			}
		}else {
			// 	Pintori 08/02/2018 Validazione Contenuti spostata nella servlet PorteApplicativeValidazione, in ADD devo mostrare comunque la form.
			this.validazioneContenuti(tipoOp, dati, true, false, statoValidazione, tipoValidazione, applicaMTOM,
					serviceBinding, aspc.getFormatoSpecifica());
		}
		
		// *************** Integrazione *********************
		
		Vector<DataElement> deIntegrazione = new Vector<DataElement>();
				
		if (tipoOp.equals(TipoOperazione.CHANGE)) {
			de = new DataElement();
			de.setLabel(PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_METADATI);
			de.setValue(integrazione);
			de.setName(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_INTEGRAZIONE);
			if(this.isModalitaStandard() || (isConfigurazione && !datiAltro) ){
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
		if(!this.core.isShowJ2eeOptions() || (isConfigurazione && !datiAltro)){
			de.setType(DataElementType.HIDDEN);
			de.setValue(stateless);
			dati.addElement(de);
		}else{
			de.setType(DataElementType.SELECT);
			de.setValues(tipoStateless);
			de.setSelected(stateless);
			deIntegrazione.addElement(de);	
		}
		
				
		de = new DataElement();
		de.setLabel(PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_BEHAVIOUR);
		de.setValue(behaviour);
		de.setName(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_BEHAVIOUR);
		if (!this.isModalitaAvanzata() || (isConfigurazione && !datiAltro)) {
			de.setType(DataElementType.HIDDEN);
			dati.addElement(de);
		}
		else{
			de.setType(DataElementType.TEXT_EDIT);
			deIntegrazione.addElement(de);	
		}
			
		if (tipoOp.equals(TipoOperazione.CHANGE)) {
			if(this.isModalitaAvanzata() && !isConfigurazione){
				de = new DataElement();
				de.setType(DataElementType.LINK);
				de.setUrl(PorteApplicativeCostanti.SERVLET_NAME_PORTE_APPLICATIVE_PROPRIETA_PROTOCOLLO_LIST,pIdSogg,pIdPorta, pIdAsps);

				if (contaListe) {
					ServletUtils.setDataElementCustomLabel(de,PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_PROTOCOL_PROPERTIES,Long.valueOf(numProprProt));
				} else
					ServletUtils.setDataElementCustomLabel(de,PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_PROTOCOL_PROPERTIES);

				deIntegrazione.addElement(de);
			}
		}
		
		if(deIntegrazione.size()>0){
			
			if(!isConfigurazione || datiAltro) {
				de = new DataElement();
				de.setType(DataElementType.TITLE);
				de.setLabel(PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_INTEGRAZIONE);
				dati.addElement(de);
				
				for (int i = 0; i < deIntegrazione.size(); i++) {
					dati.addElement(deIntegrazione.get(i));
				}
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
				dati.addElement(de);
			}
			
			

			de = new DataElement();
			de.setType(DataElementType.LINK);
			de.setUrl(PorteApplicativeCostanti.SERVLET_NAME_PORTE_APPLICATIVE_CORRELAZIONE_APPLICATIVA_REQUEST_LIST,pIdSogg,pIdPorta,pNomePorta, pIdAsps);
			if (contaListe) {
				ServletUtils.setDataElementCustomLabel(de,PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_CORRELAZIONE_APPLICATIVA_RICHIESTA,Long.valueOf(numCorrelazioneReq));
			} else
				ServletUtils.setDataElementCustomLabel(de,PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_CORRELAZIONE_APPLICATIVA_RICHIESTA);

			dati.addElement(de);

			de = new DataElement();
			de.setType(DataElementType.LINK);
			de.setUrl(PorteApplicativeCostanti.SERVLET_NAME_PORTE_APPLICATIVE_CORRELAZIONE_APPLICATIVA_RESPONSE_LIST,pIdSogg,pIdPorta,pNomePorta, pIdAsps);
			if (contaListe) {
				ServletUtils.setDataElementCustomLabel(de,PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_CORRELAZIONE_APPLICATIVA_RISPOSTA,Long.valueOf(numCorrelazioneRes));
			} else
				ServletUtils.setDataElementCustomLabel(de,PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_CORRELAZIONE_APPLICATIVA_RISPOSTA);

			dati.addElement(de);
						
		}

		

		
		
		
		
		// *************** Gestione Messaggio *********************
		
		if (tipoOp.equals(TipoOperazione.CHANGE)) {
			if(!isConfigurazione) {
				de = new DataElement();
				de.setType(DataElementType.TITLE);
				de.setLabel(PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_GESTIONE_MESSAGGIO);
				dati.addElement(de);
	
				de = new DataElement();
				de.setType(DataElementType.LINK);
				de.setUrl(PorteApplicativeCostanti.SERVLET_NAME_PORTE_APPLICATIVE_CORRELAZIONE_APPLICATIVA,pIdSogg,pIdPorta,pNomePorta, pIdAsps);
				String statoCorrelazioneApplicativa = PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_CORRELAZIONE_APPLICATIVA_DISABILITATA;
				if(numCorrelazioneReq>0 || numCorrelazioneRes>0){
					statoCorrelazioneApplicativa = PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_CORRELAZIONE_APPLICATIVA_ABILITATA;
				}
				ServletUtils.setDataElementCustomLabel(de, PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_CORRELAZIONE_APPLICATIVA, statoCorrelazioneApplicativa);
				dati.addElement(de);
				
				de = new DataElement();
				de.setType(DataElementType.LINK);
				de.setUrl(PorteApplicativeCostanti.SERVLET_NAME_PORTE_APPLICATIVE_MESSAGE_SECURITY,pIdSogg,pIdPorta, pIdAsps);
				ServletUtils.setDataElementCustomLabel(de, PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_MESSAGE_SECURITY, statoMessageSecurity);
				dati.addElement(de);
				
				//if (InterfaceType.AVANZATA.equals(ServletUtils.getUserFromSession(this.session).getInterfaceType())) {
				de = new DataElement();
				de.setType(DataElementType.LINK);
				de.setUrl(PorteApplicativeCostanti.SERVLET_NAME_PORTE_APPLICATIVE_MTOM, pIdSogg,pIdPorta, pIdAsps);
				ServletUtils.setDataElementCustomLabel(de, PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_MTOM, statoMTOM);
				dati.addElement(de);
				//}
			}
		}
		
		
		// *************** Asincroni *********************
		
		boolean supportoAsincroni = this.core.isProfiloDiCollaborazioneAsincronoSupportatoDalProtocollo(protocollo,serviceBinding);
		if(supportoAsincroni) {
			de = new DataElement();
			if ( this.isModalitaStandard() || (isConfigurazione && !datiAltro)) {
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
			if (this.isModalitaStandard() || (isConfigurazione && !datiAltro)) {
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
			if (this.isModalitaStandard() || (isConfigurazione && !datiAltro)) {
				de.setType(DataElementType.HIDDEN);
				de.setValue(PorteApplicativeCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_APPLICATIVE_ABILITATO);
			}else{
				de.setType(DataElementType.SELECT);
				de.setValues(tipoRicasim);
				de.setSelected(ricasim);
			}
			de.setName(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_RICEVUTA_ASINCRONA_ASIMMETRICA);
			dati.addElement(de);
		}
		
		
		
		
		
		
		
		// ***************  SOAP With Attachments *********************

		if (this.isModalitaAvanzata() && (!isConfigurazione || datiAltro) ) {

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
			if(this.isFunzionalitaProtocolloSupportataDalProtocollo(protocollo, serviceBinding, FunzionalitaProtocollo.MANIFEST_ATTACHMENTS)){
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
		
//		if(configurazioneStandardNonApplicabile){
//			this.pd.setMessage(CostantiControlStation.LABEL_CONFIGURAZIONE_IMPOSTATA_MODALITA_AVANZATA_LONG_MESSAGE, Costanti.MESSAGE_TYPE_INFO);
//			this.pd.disableEditMode();
//		}
		


		return dati;
	}
	
	
	public boolean isFunzionalitaProtocolloSupportataDalProtocollo(String protocollo, ServiceBinding serviceBinding,FunzionalitaProtocollo funzionalitaProtocollo)
			throws DriverRegistroServiziNotFound, DriverRegistroServiziException, DriverConfigurazioneException {
		if(serviceBinding == null) {
			List<ServiceBinding> serviceBindingListProtocollo = this.core.getServiceBindingListProtocollo(protocollo);
			
			boolean supportato = true;
			if(serviceBindingListProtocollo != null && serviceBindingListProtocollo.size() > 0) {
				for (ServiceBinding serviceBinding2 : serviceBindingListProtocollo) {
					boolean supportatoTmp = this.core.isFunzionalitaProtocolloSupportataDalProtocollo(protocollo, serviceBinding2, funzionalitaProtocollo);
					supportato = supportato || supportatoTmp;
				}
			}
			return supportato;
		} else {
			return this.core.isFunzionalitaProtocolloSupportataDalProtocollo(protocollo, serviceBinding, funzionalitaProtocollo);
		}
	}
	

	public List<PortaApplicativaAzioneIdentificazione> getModalitaIdentificazionePorta(String protocollo, ServiceBinding serviceBinding)
			throws ProtocolException, DriverConfigurazioneException { 
		
		if(serviceBinding == null) {
			List<ServiceBinding> serviceBindingListProtocollo = this.core.getServiceBindingListProtocollo(protocollo);
			
			List<PortaApplicativaAzioneIdentificazione> listaModalita = new ArrayList<PortaApplicativaAzioneIdentificazione>();
			if(serviceBindingListProtocollo != null && serviceBindingListProtocollo.size() > 0) {
				for (ServiceBinding serviceBinding2 : serviceBindingListProtocollo) {
					List<PortaApplicativaAzioneIdentificazione> listaModalitaTmp = ProtocolFactoryManager.getInstance().getProtocolFactoryByName(protocollo).
							createProtocolIntegrationConfiguration().getAllImplementationIdentificationResourceModes(serviceBinding2,
									ProtocolPropertiesUtilities.getTipoInterfaccia(this) );
					
					for (PortaApplicativaAzioneIdentificazione tipoTmp : listaModalitaTmp) {
						if(!listaModalita.contains(tipoTmp))
							listaModalita.add(tipoTmp);
					}
				}
			}
			return listaModalita;
		} else {
			return ProtocolFactoryManager.getInstance().getProtocolFactoryByName(protocollo).
					createProtocolIntegrationConfiguration().getAllImplementationIdentificationResourceModes(serviceBinding,
							ProtocolPropertiesUtilities.getTipoInterfaccia(this));
		}
	}
	
	public boolean useInterfaceNameInInvocationURL(String protocollo, ServiceBinding serviceBinding) throws ProtocolException{
		return ProtocolFactoryManager.getInstance().getProtocolFactoryByName(protocollo).
				createProtocolIntegrationConfiguration().useInterfaceNameInImplementationInvocationURL(serviceBinding);
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
					String protocollo = this.soggettiCore.getProtocolloAssociatoTipoSoggetto(tmpSogg.getTipo());
					tmpTitle = this.getLabelNomeSoggetto(protocollo, tmpSogg.getTipo() , tmpSogg.getNome());
				}else{
					org.openspcoop2.core.config.Soggetto tmpSogg = this.soggettiCore.getSoggetto(Integer.parseInt(id));
					String protocollo = this.soggettiCore.getProtocolloAssociatoTipoSoggetto(tmpSogg.getTipo());
					tmpTitle = this.getLabelNomeSoggetto(protocollo, tmpSogg.getTipo() , tmpSogg.getNome());
				}
				lstParam.add(new Parameter(SoggettiCostanti.LABEL_SOGGETTI, SoggettiCostanti.SERVLET_NAME_SOGGETTI_LIST));
				
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
				lstParam.add(new Parameter(PorteApplicativeCostanti.LABEL_PORTE_APPLICATIVE, PorteApplicativeCostanti.SERVLET_NAME_PORTE_APPLICATIVE_LIST));
				
				if(search.equals("")){
					this.pd.setSearchDescription("");
				}
				else{
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
				ServletUtils.enabledPageDataSearch(this.pd, PorteApplicativeCostanti.LABEL_PORTE_APPLICATIVE, search);
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
			listaLabel.add(PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_SERVIZI_APPLICATIVI);
			
			listaLabel.add(PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_CONTROLLO_ACCESSI); 
			
			listaLabel.add(PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_MESSAGE_SECURITY);
			//if(isModalitaAvanzata){
			listaLabel.add(PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_MTOM);
			//}
			listaLabel.add(PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_CORRELAZIONE_APPLICATIVA);
			if(this.isModalitaAvanzata())
				listaLabel.add(PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_PROTOCOL_PROPERTIES);
//			if(this.core.isRegistroServiziLocale()){
//				//listaLabel.add(PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_SERVIZIO);
//				listaLabel.add(AccordiServizioParteSpecificaCostanti.LABEL_APS_MENU_VISUALE_AGGREGATA);
//			}
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
						de.setValue(this.getLabelNomeSoggetto(protocollo, pa.getTipoSoggettoProprietario() , pa.getNomeSoggettoProprietario()));
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

					de = new DataElement();
					de.setUrl(PorteApplicativeCostanti.SERVLET_NAME_PORTE_APPLICATIVE_SERVIZIO_APPLICATIVO_LIST, pIdSogg, pIdPorta, pIdAsps);
					if (contaListe) {
						int numSA = pa.sizeServizioApplicativoList();
						ServletUtils.setDataElementVisualizzaLabel(de,Long.valueOf(numSA));
					} else
						ServletUtils.setDataElementVisualizzaLabel(de);
					e.addElement(de);
					
					
					// controllo accessi
					de = new DataElement();
					//fix: idsogg e' il soggetto proprietario della porta applicativa, e nn il soggetto virtuale
					de.setUrl(PorteApplicativeCostanti.SERVLET_NAME_PORTE_APPLICATIVE_CONTROLLO_ACCESSI, pIdSogg, pIdPorta, pIdAsps);
					
					String gestioneToken = null;
					if(pa.getGestioneToken()!=null && pa.getGestioneToken().getPolicy()!=null &&
							!"".equals(pa.getGestioneToken().getPolicy()) &&
							!"-".equals(pa.getGestioneToken().getPolicy())) {
						gestioneToken = StatoFunzionalita.ABILITATO.getValue();
					}
					
					String autenticazione = pa.getAutenticazione();
					String autenticazioneCustom = null;
					if (autenticazione != null && !TipoAutenticazione.getValues().contains(autenticazione)) {
						autenticazioneCustom = autenticazione;
						autenticazione = CostantiControlStation.DEFAULT_VALUE_PARAMETRO_PORTE_AUTENTICAZIONE_CUSTOM;
					}
					String autenticazioneOpzionale = "";
					if(pa.getAutenticazioneOpzionale()!=null){
						if (pa.getAutenticazioneOpzionale().equals(StatoFunzionalita.ABILITATO)) {
							autenticazioneOpzionale = Costanti.CHECK_BOX_ENABLED;
						}
					}
					String autorizzazioneContenuti = pa.getAutorizzazioneContenuto();
					
					String autorizzazione= null, autorizzazioneCustom = null;
					if (pa.getAutorizzazione() != null &&
							!TipoAutorizzazione.getAllValues().contains(pa.getAutorizzazione())) {
						autorizzazioneCustom = pa.getAutorizzazione();
						autorizzazione = CostantiControlStation.DEFAULT_VALUE_PARAMETRO_PORTE_AUTORIZZAZIONE_CUSTOM;
					}
					else{
						autorizzazione = AutorizzazioneUtilities.convertToStato(pa.getAutorizzazione());
					}
					String statoControlloAccessi = this.getLabelStatoControlloAccessi(gestioneToken,autenticazione, autenticazioneOpzionale, autenticazioneCustom, autorizzazione, autorizzazioneContenuti,autorizzazioneCustom); 
					de.setValue(statoControlloAccessi);
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
							int numProp = pa.sizeProprietaList();
							ServletUtils.setDataElementVisualizzaLabel(de,Long.valueOf(numProp));
						} else
							ServletUtils.setDataElementVisualizzaLabel(de);
						e.addElement(de);
					}

//					if(this.core.isRegistroServiziLocale()){
//						/*
//						 * Visualizzo SoggettoErogatore/Servizio I soggetti
//						 * erogatori possono essere identificati con due casi 1. PA
//						 * che hanno soggetto proprietario, servizio e non hanno
//						 * definito il soggetto virtuale. In questo caso il soggetto
//						 * erogatore e' il soggetto proprietario. 2. PA che hanno un
//						 * soggetto virtuale. In questo caso il soggetto erogatore
//						 * e' il soggetto virtuale
//						 */
//						int idSoggEr = 0;
//						PortaApplicativaSoggettoVirtuale pasv = pa.getSoggettoVirtuale();
//						if (pasv != null)
//							idSoggEr = pasv.getId().intValue();
//						else
//							idSoggEr = pa.getIdSoggetto().intValue();
//						PortaApplicativaServizio pas = pa.getServizio();
//						int idServ = pas.getId().intValue();
//						Soggetto soggEr = null;
//						AccordoServizioParteSpecifica asps = null;
//						try {
//							soggEr = this.soggettiCore.getSoggettoRegistro(idSoggEr);
//							asps = this.apsCore.getAccordoServizioParteSpecifica(idServ);
//						} catch (DriverRegistroServiziNotFound drsnf) {
//							// ok
//						} catch (DriverRegistroServiziException drse) {
//							// ok
//						}
//						String tmpAz = "";
//						PortaApplicativaAzione paa = pa.getAzione();
//						if (paa != null && paa.getNome()!=null)
//							tmpAz = "-" + paa.getNome();
//						de = new DataElement();
//						Parameter pId2 = new Parameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_ID, ""+ idServ);
//						Parameter pNomeServizio = new Parameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_NOME_SERVIZIO, asps.getNome());
//						Parameter pTipoServizio = new Parameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_TIPO_SERVIZIO, asps.getTipo());
//						Parameter pVersioneServizio = new Parameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_VERSIONE, asps.getVersione().intValue()+"");
//
//						de.setUrl(AccordiServizioParteSpecificaCostanti.SERVLET_NAME_APS_CHANGE, 
//								pId2, pNomeServizio, pTipoServizio, pVersioneServizio);
//						IDServizio idServizio = IDServizioFactory.getInstance().getIDServizioFromValues(asps.getTipo(), asps.getNome(), 
//								soggEr.getTipo(),soggEr.getNome(), 
//								asps.getVersione());
//						de.setValue(IDServizioFactory.getInstance().getUriFromIDServizio(idServizio) + tmpAz);
//
//						e.addElement(de);
//					}

					
					if(extendedServletList!=null && extendedServletList.showExtendedInfo(this.request, this.session)){
						de = new DataElement();
						de.setUrl(PorteApplicativeCostanti.SERVLET_NAME_PORTE_APPLICATIVE_EXTENDED_LIST,
								pIdPorta,pIdNome,pIdPorta, pIdSogg, pIdAsps
								);
						if (contaListe) {
							int numExtended = extendedServletList.sizeList(pa);
							ServletUtils.setDataElementVisualizzaLabel(de,Long.valueOf(numExtended));
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
			if(!this.isModalitaCompleta() || !useIdSogg) {
				this.pd.setAddButton(false);
			}
			
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
	public void preparePorteAppPropList(String nomePorta, ISearch ricerca, List<Proprieta> lista)
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

			PortaApplicativa myPA = this.porteApplicativeCore.getPortaApplicativa(Integer.parseInt(idPorta));
			String idporta = myPA.getNome();

			// setto la barra del titolo
			List<Parameter> lstParam = this.getTitoloPA(parentPA, idsogg, idAsps);
			
			String labelPerPorta = null;
			if(parentPA!=null && (parentPA.intValue() == PorteApplicativeCostanti.ATTRIBUTO_PORTE_APPLICATIVE_PARENT_CONFIGURAZIONE)) {
				labelPerPorta = PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_PROTOCOL_PROPERTIES_CONFIG_DI+
						this.porteApplicativeCore.getLabelRegolaMappingErogazionePortaApplicativa(myPA);
			}
			else {
				labelPerPorta = PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_PROTOCOL_PROPERTIES_CONFIG_DI+idporta;
			}
			
			this.pd.setSearchLabel(PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_NOME);
			if(search.equals("")){
				this.pd.setSearchDescription("");
				lstParam.add(new Parameter(labelPerPorta,null));
			}else{
				lstParam.add(new Parameter(labelPerPorta,
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
				ServletUtils.enabledPageDataSearch(this.pd, PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_PROTOCOL_PROPERTIES, search);
			}

			// setto le label delle colonne
			String valueLabel = PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_VALORE;
			String[] labels = { PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_NOME, valueLabel };
			this.pd.setLabels(labels);

			// preparo i dati
			Vector<Vector<DataElement>> dati = new Vector<Vector<DataElement>>();

			if (lista != null) {
				Iterator<Proprieta> it = lista.iterator();
				while (it.hasNext()) {
					Proprieta ssp = it.next();

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

			PortaApplicativa myPA = this.porteApplicativeCore.getPortaApplicativa(Integer.parseInt(idPorta));
			String idporta = myPA.getNome();

			// setto la barra del titolo
			List<Parameter> lstParam = this.getTitoloPA(parentPA, idsogg, idAsps);
			
			String labelPerPorta = null;
			if(parentPA!=null && (parentPA.intValue() == PorteApplicativeCostanti.ATTRIBUTO_PORTE_APPLICATIVE_PARENT_CONFIGURAZIONE)) {
				labelPerPorta = PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_SERVIZIO_APPLICATIVO_CONFIG_DI+
						this.porteApplicativeCore.getLabelRegolaMappingErogazionePortaApplicativa(myPA);
			}
			else {
				labelPerPorta = PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_SERVIZIO_APPLICATIVO_CONFIG_DI+idporta;
			}
			
			this.pd.setSearchLabel(PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_NOME);
			if(search.equals("")){
				this.pd.setSearchDescription("");
				lstParam.add(new Parameter(labelPerPorta,null));
			}else{
				lstParam.add(new Parameter(labelPerPorta,
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
				ServletUtils.enabledPageDataSearch(this.pd, PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_SERVIZI_APPLICATIVI, search);
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

			String labelPerPorta = null;
			if(parentPA!=null && (parentPA.intValue() == PorteApplicativeCostanti.ATTRIBUTO_PORTE_APPLICATIVE_PARENT_CONFIGURAZIONE)) {
				labelPerPorta = PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_CORRELAZIONI_APPLICATIVE_CONFIG_DI+
						this.porteApplicativeCore.getLabelRegolaMappingErogazionePortaApplicativa(myPA);
			}
			else {
				labelPerPorta = PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_CORRELAZIONI_APPLICATIVE_CONFIG_DI+idporta;
			}
			lstParam.add(new Parameter(labelPerPorta,
					PorteApplicativeCostanti.SERVLET_NAME_PORTE_APPLICATIVE_CORRELAZIONE_APPLICATIVA, 
					new Parameter( PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID, idPorta),
					new Parameter( PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_SOGGETTO, idsogg),
					new Parameter( PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_NOME, idporta),
					new Parameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_ASPS, idAsps)
					));
			
			this.pd.setSearchLabel(PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_ELEMENTO_XML);
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
				ServletUtils.enabledPageDataSearch(this.pd, PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_CORRELAZIONI_APPLICATIVE_RICHIESTA_DI, search);
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

			PortaApplicativa myPA = this.porteApplicativeCore.getPortaApplicativa(Integer.parseInt(idPorta));
			String idporta = myPA.getNome();

			// setto la barra del titolo
			List<Parameter> lstParam = this.getTitoloPA(parentPA, idsogg, idAsps);

			String labelPerPorta = null;
			if(parentPA!=null && (parentPA.intValue() == PorteApplicativeCostanti.ATTRIBUTO_PORTE_APPLICATIVE_PARENT_CONFIGURAZIONE)) {
				labelPerPorta = PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_CORRELAZIONI_APPLICATIVE_CONFIG_DI+
						this.porteApplicativeCore.getLabelRegolaMappingErogazionePortaApplicativa(myPA);
			}
			else {
				labelPerPorta = PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_CORRELAZIONI_APPLICATIVE_CONFIG_DI+idporta;
			}
			lstParam.add(new Parameter(labelPerPorta,
					PorteApplicativeCostanti.SERVLET_NAME_PORTE_APPLICATIVE_CORRELAZIONE_APPLICATIVA, 
					new Parameter( PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID, idPorta),
					new Parameter( PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_SOGGETTO, idsogg),
					new Parameter( PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_NOME, idporta),
					new Parameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_ASPS, idAsps)
					));
			
			this.pd.setSearchLabel(PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_ELEMENTO_XML);
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
				ServletUtils.enabledPageDataSearch(this.pd, PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_CORRELAZIONI_APPLICATIVE_RISPOSTA_DI, search);
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

			PortaApplicativa myPA = this.porteApplicativeCore.getPortaApplicativa(Integer.parseInt(idPorta));
			String idporta = myPA.getNome();

			// setto la barra del titolo
			List<Parameter> lstParam = this.getTitoloPA(parentPA, idsogg, idAsps);

			String labelPerPorta = null;
			if(parentPA!=null && (parentPA.intValue() == PorteApplicativeCostanti.ATTRIBUTO_PORTE_APPLICATIVE_PARENT_CONFIGURAZIONE)) {
				labelPerPorta = PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_MESSAGE_SECURITY_CONFIG_DI+
						this.porteApplicativeCore.getLabelRegolaMappingErogazionePortaApplicativa(myPA);
			}
			else {
				labelPerPorta = PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_MESSAGE_SECURITY_CONFIG_DI+idporta;
			}
			lstParam.add(new Parameter(labelPerPorta,
					PorteApplicativeCostanti.SERVLET_NAME_PORTE_APPLICATIVE_MESSAGE_SECURITY ,
					new Parameter( PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID, idPorta),
					new Parameter( PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_SOGGETTO, idsogg),
					new Parameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_ASPS, idAsps)));
			
			this.pd.setSearchLabel(PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_NOME);
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
				ServletUtils.enabledPageDataSearch(this.pd, PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_MESSAGE_SECURITY_REQUEST_FLOW_DI, search);
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

			PortaApplicativa myPA = this.porteApplicativeCore.getPortaApplicativa(Integer.parseInt(idPorta));
			String idporta = myPA.getNome();

			// setto la barra del titolo
			List<Parameter> lstParam = this.getTitoloPA(parentPA, idsogg, idAsps);
			
			String labelPerPorta = null;
			if(parentPA!=null && (parentPA.intValue() == PorteApplicativeCostanti.ATTRIBUTO_PORTE_APPLICATIVE_PARENT_CONFIGURAZIONE)) {
				labelPerPorta = PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_MESSAGE_SECURITY_CONFIG_DI+
						this.porteApplicativeCore.getLabelRegolaMappingErogazionePortaApplicativa(myPA);
			}
			else {
				labelPerPorta = PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_MESSAGE_SECURITY_CONFIG_DI+idporta;
			}
			lstParam.add(new Parameter(labelPerPorta,
					PorteApplicativeCostanti.SERVLET_NAME_PORTE_APPLICATIVE_MESSAGE_SECURITY ,
					new Parameter( PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID, idPorta),
					new Parameter( PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_SOGGETTO, idsogg),
					new Parameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_ASPS, idAsps)));
			
			this.pd.setSearchLabel(PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_NOME);
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
				ServletUtils.enabledPageDataSearch(this.pd, PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_MESSAGE_SECURITY_RESPONSE_FLOW_DI, search);
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

			String protocollo = null;
			if(this.core.isRegistroServiziLocale()){
				Soggetto mySogg = this.soggettiCore.getSoggettoRegistro(Integer.parseInt(idsogg));
				protocollo = this.soggettiCore.getProtocolloAssociatoTipoSoggetto(mySogg.getTipo());
			}
			else{
				org.openspcoop2.core.config.Soggetto mySogg = this.soggettiCore.getSoggetto(Integer.parseInt(idsogg));
				protocollo = this.soggettiCore.getProtocolloAssociatoTipoSoggetto(mySogg.getTipo());
			}

			PortaApplicativa myPA = this.porteApplicativeCore.getPortaApplicativa(Integer.parseInt(idPorta));
			String idporta = myPA.getNome();

			// setto la barra del titolo
			List<Parameter> lstParam = this.getTitoloPA(parentPA, idsogg, idAsps);

			String labelPerPorta = null;
			if(parentPA!=null && (parentPA.intValue() == PorteApplicativeCostanti.ATTRIBUTO_PORTE_APPLICATIVE_PARENT_CONFIGURAZIONE)) {
				labelPerPorta = PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_CONTROLLO_ACCESSI_CONFIG_DI+
						this.porteApplicativeCore.getLabelRegolaMappingErogazionePortaApplicativa(myPA);
			}
			else {
				labelPerPorta = PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_CONTROLLO_ACCESSI_CONFIG_DI+idporta;
			}
			
			lstParam.add(new Parameter(labelPerPorta, PorteApplicativeCostanti.SERVLET_NAME_PORTE_APPLICATIVE_CONTROLLO_ACCESSI, 
					new Parameter( PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID, idPorta),
					new Parameter( PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_SOGGETTO, idsogg),
					new Parameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_ASPS, idAsps)));
			
			String labelPag = PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_SOGGETTO_CONFIG;
			
			this.pd.setSearchLabel(PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_NOME);
			if(search.equals("")){
				this.pd.setSearchDescription("");
				lstParam.add(new Parameter(labelPag,null));
			}else{
				lstParam.add(new Parameter(labelPag,
						PorteApplicativeCostanti.SERVLET_NAME_PORTE_APPLICATIVE_SOGGETTO_LIST ,
						new Parameter( PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID, idPorta),
						new Parameter( PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_SOGGETTO, idsogg),
						new Parameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_ASPS, idAsps)));
				
				lstParam.add(new Parameter(PorteApplicativeCostanti.LABEL_PORTE_APPLICATIVE_RISULTATI_RICERCA, null));
			}

			// setto la barra del titolo
			ServletUtils.setPageDataTitle(this.pd, lstParam.toArray(new Parameter[lstParam.size()]));

			// controllo eventuali risultati ricerca
//			if (!search.equals("")) {
//				this.pd.setSearch("on");
//				this.pd.setSearchDescription("Soggetti contenenti la stringa '" + search + "'");
//			}

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

					Long idSoggetto =this.soggettiCore.getIdSoggetto(sog.getNome(), sog.getTipo());
					DataElement de = new DataElement();
					de.setUrl(SoggettiCostanti.SERVLET_NAME_SOGGETTI_CHANGE,
							new Parameter(SoggettiCostanti.PARAMETRO_SOGGETTO_ID,idSoggetto+""),
							new Parameter(SoggettiCostanti.PARAMETRO_SOGGETTO_NOME,sog.getNome()),
							new Parameter(SoggettiCostanti.PARAMETRO_SOGGETTO_TIPO,sog.getTipo()));
					de.setValue(this.getLabelNomeSoggetto(protocollo, sog.getTipo() , sog.getNome()));
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
			String nome, String valore, Vector<DataElement> dati) {

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

		de = new DataElement();
		de.setLabel(CostantiControlStation.LABEL_PARAMETRO_VALORE);
		de.setType(DataElementType.TEXT_EDIT);
		de.setRequired(true);
		de.setName(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_VALORE);
		de.setValue(valore);
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

			PortaApplicativa myPA = this.porteApplicativeCore.getPortaApplicativa(Integer.parseInt(id));
			String idporta = myPA.getNome();

			// setto la barra del titolo
			List<Parameter> lstParam = this.getTitoloPA(parentPA, idsogg, idAsps);

			String labelPerPorta = null;
			if(parentPA!=null && (parentPA.intValue() == PorteApplicativeCostanti.ATTRIBUTO_PORTE_APPLICATIVE_PARENT_CONFIGURAZIONE)) {
				labelPerPorta = PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_MTOM_CONFIG_DI+
						this.porteApplicativeCore.getLabelRegolaMappingErogazionePortaApplicativa(myPA);
			}
			else {
				labelPerPorta = PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_MTOM_CONFIG_DI+idporta;
			}
			
			lstParam.add(new Parameter(labelPerPorta,
					PorteApplicativeCostanti.SERVLET_NAME_PORTE_APPLICATIVE_MTOM,
					new Parameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID,id),
					new Parameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_SOGGETTO,idsogg),
					new Parameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_ASPS, idAsps)
					));
			
			this.pd.setSearchLabel(PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_NOME);
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
				ServletUtils.enabledPageDataSearch(this.pd, PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_MTOM_REQUEST_FLOW_DI, search);
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

			PortaApplicativa myPA = this.porteApplicativeCore.getPortaApplicativa(Integer.parseInt(id));
			String idporta = myPA.getNome();

			// setto la barra del titolo
			List<Parameter> lstParam = this.getTitoloPA(parentPA, idsogg, idAsps);
			
			String labelPerPorta = null;
			if(parentPA!=null && (parentPA.intValue() == PorteApplicativeCostanti.ATTRIBUTO_PORTE_APPLICATIVE_PARENT_CONFIGURAZIONE)) {
				labelPerPorta = PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_MTOM_CONFIG_DI+
						this.porteApplicativeCore.getLabelRegolaMappingErogazionePortaApplicativa(myPA);
			}
			else {
				labelPerPorta = PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_MTOM_CONFIG_DI+idporta;
			}
			
			lstParam.add(new Parameter(labelPerPorta,
					PorteApplicativeCostanti.SERVLET_NAME_PORTE_APPLICATIVE_MTOM,
					new Parameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID,id),
					new Parameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_SOGGETTO,idsogg),
					new Parameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_ASPS, idAsps)
					));

			this.pd.setSearchLabel(PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_NOME);
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
				ServletUtils.enabledPageDataSearch(this.pd, PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_MTOM_RESPONSE_FLOW_DI, search);
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

			PortaApplicativa pa = this.porteApplicativeCore.getPortaApplicativa(Integer.parseInt(id));
			String idporta = pa.getNome();

			// setto la barra del titolo
			List<Parameter> lstParam = this.getTitoloPA(parentPA, idsogg, idAsps);
			
			String labelPerPorta = null;
			if(parentPA!=null && (parentPA.intValue() == PorteApplicativeCostanti.ATTRIBUTO_PORTE_APPLICATIVE_PARENT_CONFIGURAZIONE)) {
				labelPerPorta = PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_CONTROLLO_ACCESSI_CONFIG_DI+
						this.porteApplicativeCore.getLabelRegolaMappingErogazionePortaApplicativa(pa);
			}
			else {
				labelPerPorta = PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_CONTROLLO_ACCESSI_CONFIG_DI+idporta;
			}
			
			lstParam.add(new Parameter(labelPerPorta, PorteApplicativeCostanti.SERVLET_NAME_PORTE_APPLICATIVE_CONTROLLO_ACCESSI, 
					new Parameter( PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID, id),
					new Parameter( PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_SOGGETTO, idsogg),
					new Parameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_ASPS, idAsps)));
			
			String labelPagLista = PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_RUOLI_CONFIG;
			
			this.pd.setSearchLabel(CostantiControlStation.LABEL_PARAMETRO_RUOLO);
			if(search.equals("")){
				this.pd.setSearchDescription("");
				lstParam.add(new Parameter(labelPagLista,null));
			}
			else{
				lstParam.add(new Parameter(labelPagLista,
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
				ServletUtils.enabledPageDataSearch(this.pd, PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_RUOLI, search);
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
	
	public void preparePorteApplicativeScopeList(String nomePorta, ISearch ricerca, List<String> lista)
			throws Exception {
		try {
			// prelevo il flag che mi dice da quale pagina ho acceduto la sezione delle porte delegate
			Integer parentPA = ServletUtils.getIntegerAttributeFromSession(PorteApplicativeCostanti.ATTRIBUTO_PORTE_APPLICATIVE_PARENT, this.session);
			String idAsps = this.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_ASPS);
			if(idAsps == null)
				idAsps = "";

			String id = this.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID);
			String idsogg = this.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_SOGGETTO);

			ServletUtils.addListElementIntoSession(this.session, PorteApplicativeCostanti.OBJECT_NAME_PORTE_APPLICATIVE_SCOPE,
					new Parameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID, id), 
					new Parameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_SOGGETTO, idsogg),
					new Parameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_ASPS, idAsps));

			int idLista = Liste.PORTE_APPLICATIVE_SCOPE;
			int limit = ricerca.getPageSize(idLista);
			int offset = ricerca.getIndexIniziale(idLista);
			String search = ServletUtils.getSearchFromSession(ricerca, idLista);

			this.pd.setIndex(offset);
			this.pd.setPageSize(limit);
			this.pd.setNumEntries(ricerca.getNumEntries(idLista));

			PortaApplicativa pa = this.porteApplicativeCore.getPortaApplicativa(Integer.parseInt(id));
			String idporta = pa.getNome();

			// setto la barra del titolo
			List<Parameter> lstParam = this.getTitoloPA(parentPA, idsogg, idAsps);
			
			String labelPerPorta = null;
			if(parentPA!=null && (parentPA.intValue() == PorteApplicativeCostanti.ATTRIBUTO_PORTE_APPLICATIVE_PARENT_CONFIGURAZIONE)) {
				labelPerPorta = PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_CONTROLLO_ACCESSI_CONFIG_DI+
						this.porteApplicativeCore.getLabelRegolaMappingErogazionePortaApplicativa(pa);
			}
			else {
				labelPerPorta = PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_CONTROLLO_ACCESSI_CONFIG_DI+idporta;
			}
			
			lstParam.add(new Parameter(labelPerPorta, PorteApplicativeCostanti.SERVLET_NAME_PORTE_APPLICATIVE_CONTROLLO_ACCESSI, 
					new Parameter( PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID, id),
					new Parameter( PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_SOGGETTO, idsogg),
					new Parameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_ASPS, idAsps)));
			
			String labelPagLista = PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_SCOPE_CONFIG;
			
			this.pd.setSearchLabel(CostantiControlStation.LABEL_PARAMETRO_SCOPE);
			if(search.equals("")){
				this.pd.setSearchDescription("");
				lstParam.add(new Parameter(labelPagLista,null));
			}
			else{
				lstParam.add(new Parameter(labelPagLista,
						PorteApplicativeCostanti.SERVLET_NAME_PORTE_APPLICATIVE_SCOPE_LIST,
						new Parameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID,id),
						new Parameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_SOGGETTO,idsogg),
						new Parameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_ASPS, idAsps)
						));
				lstParam.add(new Parameter(PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_RISULTATI_RICERCA, null));

			}

			ServletUtils.setPageDataTitle(this.pd, lstParam.toArray(new Parameter[lstParam.size()]));

			// controllo eventuali risultati ricerca
			if (!search.equals("")) {
				ServletUtils.enabledPageDataSearch(this.pd, PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_SCOPE, search);
			}

			// setto le label delle colonne
			String[] labels = {CostantiControlStation.LABEL_PARAMETRO_SCOPE };
			this.pd.setLabels(labels);

			// preparo i dati
			Vector<Vector<DataElement>> dati = new Vector<Vector<DataElement>>();

			if (lista != null) {
				Iterator<String> it = lista.iterator();
				while (it.hasNext()) {
					String scope = it.next();
		
					Vector<DataElement> e = new Vector<DataElement>();
		
					DataElement de = new DataElement();
					de.setValue(scope);
					de.setIdToRemove(scope);
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
			String protocollo = this.soggettiCore.getProtocolloAssociatoTipoSoggetto(mySogg.getTipo());
			soggettoTitle = this.getLabelNomeSoggetto(protocollo, mySogg.getTipo() , mySogg.getNome());
		}
		else{
			org.openspcoop2.core.config.Soggetto mySogg = this.soggettiCore.getSoggetto(Integer.parseInt(idsogg));
			String protocollo = this.soggettiCore.getProtocolloAssociatoTipoSoggetto(mySogg.getTipo());
			soggettoTitle = this.getLabelNomeSoggetto(protocollo, mySogg.getTipo() , mySogg.getNome());
		}
		return _getTitoloPA(parentPA, idsogg, idAsps, soggettoTitle);
	}

	private List<Parameter> _getTitoloPA(Integer parentPA, String idsogg, String idAsps, String soggettoTitle)	throws Exception, DriverRegistroServiziNotFound, DriverRegistroServiziException {
		List<Parameter> lstParam = new ArrayList<>();
		switch (parentPA) {
		case PorteApplicativeCostanti.ATTRIBUTO_PORTE_APPLICATIVE_PARENT_CONFIGURAZIONE:
			AccordiServizioParteSpecificaCore apsCore = new AccordiServizioParteSpecificaCore(this.porteApplicativeCore);
			// Prendo il nome e il tipo del servizio
			AccordoServizioParteSpecifica asps = apsCore.getAccordoServizioParteSpecifica(Integer.parseInt(idAsps));
			String servizioTmpTile = this.getLabelIdServizio(asps);
			Parameter pIdServizio = new Parameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_ID, asps.getId()+ "");
			Parameter pNomeServizio = new Parameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_NOME_SERVIZIO, asps.getNome());
			Parameter pTipoServizio = new Parameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_TIPO_SERVIZIO, asps.getTipo());
			Parameter pIdsoggErogatore = new Parameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_ID_SOGGETTO_EROGATORE, ""+asps.getIdSoggetto());
			
			lstParam.add(new Parameter(AccordiServizioParteSpecificaCostanti.LABEL_APS, AccordiServizioParteSpecificaCostanti.SERVLET_NAME_APS_LIST));
		//	lstParam.add(new Parameter(servizioTmpTile, AccordiServizioParteSpecificaCostanti.SERVLET_NAME_APS_CHANGE, pIdServizio,pNomeServizio, pTipoServizio));
			lstParam.add(new Parameter(AccordiServizioParteSpecificaCostanti.LABEL_APS_CONFIGURAZIONI_DI + servizioTmpTile, 
					AccordiServizioParteSpecificaCostanti.SERVLET_NAME_APS_PORTE_APPLICATIVE_LIST ,pIdServizio,pNomeServizio, pTipoServizio, pIdsoggErogatore));
			break;
		case PorteApplicativeCostanti.ATTRIBUTO_PORTE_APPLICATIVE_PARENT_SOGGETTO:
			lstParam.add(new Parameter(SoggettiCostanti.LABEL_SOGGETTI,SoggettiCostanti.SERVLET_NAME_SOGGETTI_LIST));
			lstParam.add(new Parameter(PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_PORTE_APPLICATIVE_DI + soggettoTitle, PorteApplicativeCostanti.SERVLET_NAME_PORTE_APPLICATIVE_LIST ,
					new Parameter( PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_SOGGETTO, idsogg)));
			break;
		case PorteApplicativeCostanti.ATTRIBUTO_PORTE_APPLICATIVE_PARENT_NONE:
		default:
			lstParam.add(new Parameter(PorteApplicativeCostanti.LABEL_PORTE_APPLICATIVE, PorteApplicativeCostanti.SERVLET_NAME_PORTE_APPLICATIVE_LIST));
			break;
		}
		return lstParam;
	}
	
	
	public String getPortaApplicativaAzioneIdentificazioneLabel(String paAiString) {
		if(paAiString == null)
			return "";
		
		return getPortaApplicativaAzioneIdentificazioneLabel(PortaApplicativaAzioneIdentificazione.toEnumConstant(paAiString));
	}
	

	public String getPortaApplicativaAzioneIdentificazioneLabel(PortaApplicativaAzioneIdentificazione paAi) {
		if(paAi == null)
			return "";
		switch (paAi) {
		case CONTENT_BASED:
			return PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_MODE_CONTENT_BASED;
		case HEADER_BASED:
			return PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_MODE_HEADER_BASED;
		case INPUT_BASED:
			return PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_MODE_INPUT_BASED;
		case INTERFACE_BASED:
			return PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_MODE_WSDL_BASED;
		case SOAP_ACTION_BASED:
			return PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_MODE_SOAP_ACTION_BASED;
		case STATIC:
			return PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_MODE_REGISTER_INPUT;
		case URL_BASED:
			return PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_MODE_URL_BASED;
		case PROTOCOL_BASED:
			return PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_MODE_PROTOCOL_BASED;
		case DELEGATED_BY:
			break;
		default:
			break;
		}
		
		return "";
	}
	
	public String getMessaggioConfermaModificaRegolaMappingErogazionePortaApplicativa(PortaApplicativa pa, ServiceBinding serviceBinding,boolean abilitazione, boolean multiline,boolean listElement) throws DriverConfigurazioneException {
		MappingErogazionePortaApplicativa mapping = this.porteApplicativeCore.getMappingErogazionePortaApplicativa(pa);
		List<String> listaAzioni = pa.getAzione()!= null ?  pa.getAzione().getAzioneDelegataList() : new ArrayList<String>();
		return this.getMessaggioConfermaModificaRegolaMapping(mapping.isDefault(), listaAzioni, serviceBinding, abilitazione, multiline, listElement);
	}
}
