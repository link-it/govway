/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2022 Link.it srl (https://link.it). 
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
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.openspcoop2.core.commons.Filtri;
import org.openspcoop2.core.commons.ISearch;
import org.openspcoop2.core.commons.Liste;
import org.openspcoop2.core.commons.ModalitaIdentificazione;
import org.openspcoop2.core.commons.SearchUtils;
import org.openspcoop2.core.config.Connettore;
import org.openspcoop2.core.config.CorrelazioneApplicativaElemento;
import org.openspcoop2.core.config.CorrelazioneApplicativaRispostaElemento;
import org.openspcoop2.core.config.MessageSecurity;
import org.openspcoop2.core.config.MessageSecurityFlowParameter;
import org.openspcoop2.core.config.MtomProcessorFlowParameter;
import org.openspcoop2.core.config.PortaApplicativa;
import org.openspcoop2.core.config.PortaApplicativaAutorizzazioneServizioApplicativo;
import org.openspcoop2.core.config.PortaApplicativaAutorizzazioneSoggetti;
import org.openspcoop2.core.config.PortaApplicativaAutorizzazioneSoggetto;
import org.openspcoop2.core.config.PortaApplicativaAzione;
import org.openspcoop2.core.config.PortaApplicativaBehaviour;
import org.openspcoop2.core.config.PortaApplicativaServizio;
import org.openspcoop2.core.config.PortaApplicativaServizioApplicativo;
import org.openspcoop2.core.config.PortaApplicativaServizioApplicativoConnettore;
import org.openspcoop2.core.config.Proprieta;
import org.openspcoop2.core.config.ResponseCachingConfigurazioneRegola;
import org.openspcoop2.core.config.ServizioApplicativo;
import org.openspcoop2.core.config.TrasformazioneRegola;
import org.openspcoop2.core.config.TrasformazioneRegolaApplicabilitaRichiesta;
import org.openspcoop2.core.config.TrasformazioneRegolaApplicabilitaRisposta;
import org.openspcoop2.core.config.TrasformazioneRegolaApplicabilitaServizioApplicativo;
import org.openspcoop2.core.config.TrasformazioneRegolaApplicabilitaSoggetto;
import org.openspcoop2.core.config.TrasformazioneRegolaParametro;
import org.openspcoop2.core.config.TrasformazioneRegolaRisposta;
import org.openspcoop2.core.config.Trasformazioni;
import org.openspcoop2.core.config.constants.CostantiConfigurazione;
import org.openspcoop2.core.config.constants.MTOMProcessorType;
import org.openspcoop2.core.config.constants.PortaApplicativaAzioneIdentificazione;
import org.openspcoop2.core.config.constants.RuoloContesto;
import org.openspcoop2.core.config.constants.StatoFunzionalita;
import org.openspcoop2.core.config.constants.TipoAutenticazione;
import org.openspcoop2.core.config.constants.TipoAutenticazionePrincipal;
import org.openspcoop2.core.config.constants.TipoBehaviour;
import org.openspcoop2.core.config.driver.DriverConfigurazioneException;
import org.openspcoop2.core.config.driver.DriverConfigurazioneNotFound;
import org.openspcoop2.core.constants.CostantiDB;
import org.openspcoop2.core.constants.TipiConnettore;
import org.openspcoop2.core.constants.TipoPdD;
import org.openspcoop2.core.controllo_traffico.constants.RuoloPolicy;
import org.openspcoop2.core.id.IDPortaApplicativa;
import org.openspcoop2.core.id.IDServizio;
import org.openspcoop2.core.id.IDServizioApplicativo;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.core.mapping.MappingErogazionePortaApplicativa;
import org.openspcoop2.core.plugins.constants.TipoPlugin;
import org.openspcoop2.core.registry.AccordoServizioParteSpecifica;
import org.openspcoop2.core.registry.CredenzialiSoggetto;
import org.openspcoop2.core.registry.Fruitore;
import org.openspcoop2.core.registry.Ruolo;
import org.openspcoop2.core.registry.Scope;
import org.openspcoop2.core.registry.Soggetto;
import org.openspcoop2.core.registry.beans.AccordoServizioParteComuneSintetico;
import org.openspcoop2.core.registry.constants.CostantiRegistroServizi;
import org.openspcoop2.core.registry.driver.DriverRegistroServiziException;
import org.openspcoop2.core.registry.driver.DriverRegistroServiziNotFound;
import org.openspcoop2.core.registry.driver.IDAccordoFactory;
import org.openspcoop2.core.registry.driver.IDServizioFactory;
import org.openspcoop2.generic_project.exception.NotFoundException;
import org.openspcoop2.message.constants.ServiceBinding;
import org.openspcoop2.pdd.config.ConfigurazionePriorita;
import org.openspcoop2.pdd.config.UrlInvocazioneAPI;
import org.openspcoop2.pdd.core.behaviour.BehaviourException;
import org.openspcoop2.pdd.core.behaviour.built_in.load_balance.LoadBalancerType;
import org.openspcoop2.pdd.core.behaviour.built_in.load_balance.health_check.HealthCheckCostanti;
import org.openspcoop2.pdd.core.behaviour.built_in.load_balance.sticky.StickyTipoSelettore;
import org.openspcoop2.pdd.core.behaviour.built_in.multi_deliver.ConfigurazioneGestioneConsegnaNotifiche;
import org.openspcoop2.pdd.core.behaviour.built_in.multi_deliver.MessaggioDaNotificare;
import org.openspcoop2.pdd.core.behaviour.built_in.multi_deliver.TipoGestioneNotificaFault;
import org.openspcoop2.pdd.core.behaviour.built_in.multi_deliver.TipoGestioneNotificaTrasporto;
import org.openspcoop2.pdd.core.behaviour.conditional.ConditionalUtils;
import org.openspcoop2.pdd.core.behaviour.conditional.ConfigurazioneSelettoreCondizione;
import org.openspcoop2.pdd.core.behaviour.conditional.ConfigurazioneSelettoreCondizioneRegola;
import org.openspcoop2.pdd.core.behaviour.conditional.IdentificazioneFallitaConfigurazione;
import org.openspcoop2.pdd.core.dynamic.DynamicHelperCostanti;
import org.openspcoop2.pdd.core.integrazione.GruppoIntegrazione;
import org.openspcoop2.protocol.engine.ProtocolFactoryManager;
import org.openspcoop2.protocol.engine.utils.DBOggettiInUsoUtils;
import org.openspcoop2.protocol.sdk.ProtocolException;
import org.openspcoop2.protocol.sdk.constants.ConsoleInterfaceType;
import org.openspcoop2.protocol.sdk.constants.FunzionalitaProtocollo;
import org.openspcoop2.utils.crypt.PasswordVerifier;
import org.openspcoop2.utils.transport.http.HttpRequestMethod;
import org.openspcoop2.web.ctrlstat.core.ControlStationCore;
import org.openspcoop2.web.ctrlstat.core.Search;
import org.openspcoop2.web.ctrlstat.costanti.CostantiControlStation;
import org.openspcoop2.web.ctrlstat.plugins.IExtendedListServlet;
import org.openspcoop2.web.ctrlstat.servlet.aps.AccordiServizioParteSpecificaCore;
import org.openspcoop2.web.ctrlstat.servlet.aps.AccordiServizioParteSpecificaCostanti;
import org.openspcoop2.web.ctrlstat.servlet.aps.erogazioni.ErogazioniCostanti;
import org.openspcoop2.web.ctrlstat.servlet.config.ConfigurazioneCostanti;
import org.openspcoop2.web.ctrlstat.servlet.connettori.ConnettoriCostanti;
import org.openspcoop2.web.ctrlstat.servlet.pd.PorteDelegateCostanti;
import org.openspcoop2.web.ctrlstat.servlet.protocol_properties.ProtocolPropertiesUtilities;
import org.openspcoop2.web.ctrlstat.servlet.ruoli.RuoliCostanti;
import org.openspcoop2.web.ctrlstat.servlet.sa.ServiziApplicativiCostanti;
import org.openspcoop2.web.ctrlstat.servlet.sa.ServiziApplicativiHelper;
import org.openspcoop2.web.ctrlstat.servlet.scope.ScopeCostanti;
import org.openspcoop2.web.ctrlstat.servlet.soggetti.SoggettiCostanti;
import org.openspcoop2.web.lib.mvc.BinaryParameter;
import org.openspcoop2.web.lib.mvc.CheckboxStatusType;
import org.openspcoop2.web.lib.mvc.Costanti;
import org.openspcoop2.web.lib.mvc.DataElement;
import org.openspcoop2.web.lib.mvc.DataElementImage;
import org.openspcoop2.web.lib.mvc.DataElementInfo;
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
public class PorteApplicativeHelper extends ServiziApplicativiHelper {

	public PorteApplicativeHelper(HttpServletRequest request, PageData pd, 
			HttpSession session) throws Exception {
		super(request, pd,  session);
	}
	public PorteApplicativeHelper(ControlStationCore core, HttpServletRequest request, PageData pd, 
			HttpSession session) throws Exception {
		super(core, request, pd,  session);
	}

	// Controlla i dati della porta applicativa
	public boolean porteAppCheckData(TipoOperazione tipoOp, String oldNomePA, boolean isSupportatoAutenticazione, boolean datiAltroPorta,
			ServiceBinding serviceBinding) throws Exception {
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
			String autenticazionePrincipalTipo = this.getParameter(CostantiControlStation.PARAMETRO_PORTE_AUTENTICAZIONE_PRINCIPAL_TIPO);
			TipoAutenticazionePrincipal autenticazionePrincipal = TipoAutenticazionePrincipal.toEnumConstant(autenticazionePrincipalTipo, false);
			List<String> autenticazioneParametroList = this.convertFromDataElementValue_parametroAutenticazioneList(autenticazione, autenticazionePrincipal);
			
			String autorizzazione = this.getParameter(CostantiControlStation.PARAMETRO_PORTE_AUTORIZZAZIONE);
			String autorizzazioneCustom = this.getParameter(CostantiControlStation.PARAMETRO_PORTE_AUTORIZZAZIONE_CUSTOM);
			String autorizzazioneAutenticati = this.getParameter(CostantiControlStation.PARAMETRO_PORTE_AUTORIZZAZIONE_AUTENTICAZIONE);
			String autorizzazioneRuoli = this.getParameter(CostantiControlStation.PARAMETRO_PORTE_AUTORIZZAZIONE_RUOLI);
			String autorizzazioneRuoliTipologia = this.getParameter(CostantiControlStation.PARAMETRO_RUOLO_TIPOLOGIA);
			String ruoloMatch = this.getParameter(CostantiControlStation.PARAMETRO_RUOLO_MATCH);
			
			String autorizzazioneContenutiStato = this.getParameter(CostantiControlStation.PARAMETRO_AUTORIZZAZIONE_CONTENUTI_STATO);
			String autorizzazioneContenuti = this.getParameter(CostantiControlStation.PARAMETRO_AUTORIZZAZIONE_CONTENUTI);
			String autorizzazioneContenutiProperties = this.getParameter(CostantiControlStation.PARAMETRO_AUTORIZZAZIONE_CONTENUTI_PROPERTIES);
						
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
			if(!this.core.isConnettoriMultipliEnabled()) {
				if(behaviour!=null && !"".equals(behaviour)){
					if (behaviour.indexOf(" ") != -1 || behaviour.indexOf(",") != -1 ) {
						this.pd.setMessage(PorteApplicativeCostanti.MESSAGGIO_ERRORE_NON_INSERIRE_NE_SPAZI_NE_NEL_CAMPO_BEHAVIOUR);
						return false;
					}
					if(this.checkLength255(behaviour, PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_BEHAVIOUR)==false) {
						return false;
					}
				}
			}
			
			// integrazione metadati
			if(tipoOp == TipoOperazione.CHANGE && datiAltroPorta) {
				boolean validazioneIntegrazione = this.validaIntegrazioneMetadati();
				if(!validazioneIntegrazione)
					return false;
			}
			
			// rate limiting
			if(tipoOp == TipoOperazione.CHANGE && datiAltroPorta) {
				boolean validazioneRT = this.validaOpzioniAvanzateRateLimiting(RuoloPolicy.APPLICATIVA,nomePorta);
				if(!validazioneRT)
					return false;
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
			
			if (modeaz.equals(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_MODE_URL_BASED)) {
				if(this.checkRegexp(azione,PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_ESPRESSIONE_REGOLARE)==false){
					return false;
				}
			}
			if (modeaz.equals(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_MODE_CONTENT_BASED)) {
				if(ServiceBinding.SOAP.equals(serviceBinding)) {
					if(this.checkXPath(azione,PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_CONTENT_PATTERN)==false){
						return false;
					}
				}
				else {
					if(this.checkXPathOrJsonPath(azione,PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_CONTENT_PATTERN)==false){
						return false;
					}
				}
			}
			
			if ((modeaz != null) && !modeaz.equals(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_MODE_REGISTER_INPUT) && 
					!modeaz.equals(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_MODE_INPUT_BASED) &&
					!modeaz.equals(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_MODE_SOAP_ACTION_BASED) &&
					!modeaz.equals(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_MODE_CONTENT_BASED) &&
					!modeaz.equals(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_MODE_DELEGATED_BY) &&
					(azione != null && azione.indexOf(" ") != -1)) {
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
				String autorizzazione_token = this.getParameter(CostantiControlStation.PARAMETRO_PORTE_AUTORIZZAZIONE_TOKEN);
				String autorizzazione_tokenOptions = this.getParameter(CostantiControlStation.PARAMETRO_PORTE_AUTORIZZAZIONE_TOKEN_OPTIONS);
				String autorizzazioneScope = this.getParameter(CostantiControlStation.PARAMETRO_PORTE_AUTORIZZAZIONE_SCOPE);
				String autorizzazioneScopeMatch = this.getParameter(CostantiControlStation.PARAMETRO_SCOPE_MATCH);
				BinaryParameter allegatoXacmlPolicy = this.getBinaryParameter(CostantiControlStation.PARAMETRO_DOCUMENTO_SICUREZZA_XACML_POLICY);
				
				String protocollo = ProtocolFactoryManager.getInstance().getProtocolByServiceType(idSE.getTipo());
				
				String identificazioneAttributiStato = this.getParameter(CostantiControlStation.PARAMETRO_PORTE_ATTRIBUTI_STATO);
				String [] attributeAuthoritySelezionate = this.getParameterValues(CostantiControlStation.PARAMETRO_PORTE_ATTRIBUTI_AUTHORITY);
				String attributeAuthorityAttributi = this.getParameter(CostantiControlStation.PARAMETRO_PORTE_ATTRIBUTI_AUTHORITY_ATTRIBUTI);
				
				if(this.controlloAccessiCheck(tipoOp, autenticazione, autenticazioneOpzionale, autenticazionePrincipal, autenticazioneParametroList,
						autorizzazione, autorizzazioneAutenticati, autorizzazioneRuoli, 
						autorizzazioneRuoliTipologia, ruoloMatch, 
						isSupportatoAutenticazione, false, pa, ruoli,gestioneToken, gestioneTokenPolicy, 
						gestioneTokenValidazioneInput, gestioneTokenIntrospection, gestioneTokenUserInfo, gestioneTokenTokenForward,
						autorizzazione_token,autorizzazione_tokenOptions,
						autorizzazioneScope,autorizzazioneScopeMatch,allegatoXacmlPolicy,
						autorizzazioneContenutiStato, autorizzazioneContenuti, autorizzazioneContenutiProperties,
						protocollo,
						identificazioneAttributiStato, attributeAuthoritySelezionate, attributeAuthorityAttributi)==false){
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
								CredenzialiSoggetto credenziali = null;
								if(soggetto.sizeCredenzialiList()>0) {
									credenziali = soggetto.getCredenziali(0);
								}
								if(credenziali==null || credenziali.getTipo()==null ||
										!credenziali.getTipo().equals(autenticazione)){
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
					if (idSA == tmpSA.getIdServizioApplicativo()) {
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
			String idPorta = this.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID);
			int idInt = Integer.parseInt(idPorta);
			String soggetto = this.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_SOGGETTO);
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


	
	// Controlla i dati dell soggetto della porta applicativa
	public boolean porteAppServizioApplicativoAutorizzatiCheckData(TipoOperazione tipoOp)
				throws Exception {
		try {
			String idPorta = this.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID);
			@SuppressWarnings("unused")
			int idInt = Integer.parseInt(idPorta);
			String soggetto = this.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_SOGGETTO);
			if (soggetto == null) {
				soggetto = "";
			}
			String sa = this.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_SERVIZIO_APPLICATIVO_AUTORIZZATO);
			if (sa == null) {
				sa = "";
			}

			// Campi obbligatori
			if (soggetto.equals("")) {
				this.pd.setMessage(PorteApplicativeCostanti.MESSAGGIO_ERRORE_DATI_INCOMPLETI_E_NECESSARIO_INDICARE_UN_SOGGETTO);
				return false;
			}
			if (sa.equals("")) {
				this.pd.setMessage(PorteApplicativeCostanti.MESSAGGIO_ERRORE_DATI_INCOMPLETI_E_NECESSARIO_INDICARE_UN_APPLICATIVO);
				return false;
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

			
			// Check Lunghezza
			if(this.checkLength255(nome, PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_NOME)==false) {
				return false;
			}
			if(this.checkLength255(valore, PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_VALORE)==false) {
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
			if(this.checkLength255(nome, PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_NOME)==false) {
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
			if(this.checkLength255(nome, PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_NOME)==false) {
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
			String idsogg, String idPorta, String statoValidazione, String tipoValidazione, String gestBody, String gestManifest,String integrazioneStato, String integrazione,
			String[] integrazioneGruppi, List<GruppoIntegrazione> integrazioneGruppiDaVisualizzare, Map<String, List<String>> integrazioneGruppiValoriDeiGruppi,
			int numCorrApp,String scadcorr,String autorizzazioneContenutiStato, String autorizzazioneContenuti, String autorizzazioneContenutiProperties, String protocollo,
			int numSA,	 int numRuoli, String ruoloMatch,
			String statoMessageSecurity ,String statoMTOM ,int numCorrelazioneReq , int numCorrelazioneRes, int numProprProt,String applicaMTOM,
			String behaviour,
			String[] servizioApplicativoList, String servizioApplicativo, Long idSa,
			String autenticazione, String autorizzazione,
			String autenticazioneOpzionale, TipoAutenticazionePrincipal autenticazionePrincipal, List<String> autenticazioneParametroList, String autenticazioneCustom, 
			String autorizzazioneCustom,
			boolean isSupportatoAutenticazioneSoggetti,
			String autorizzazioneAutenticati,String autorizzazioneRuoli,String autorizzazioneRuoliTipologia,
			AccordoServizioParteSpecifica asps, AccordoServizioParteComuneSintetico aspc, ServiceBinding serviceBinding,
			String statoPorta, String modeaz, String azid, String patternAzione, String forceWsdlBased, boolean usataInConfigurazioni, boolean usataInConfigurazioneDefault,
			boolean ricercaPortaAzioneDelegata, String nomePortaDelegante,
			String gestioneToken, String[] gestioneTokenPolicyLabels, String[] gestioneTokenPolicyValues,
			String gestioneTokenPolicy, String gestioneTokenOpzionale,
			String gestioneTokenValidazioneInput, String gestioneTokenIntrospection, String gestioneTokenUserInfo, String gestioneTokenForward,
			String autenticazioneTokenIssuer,String autenticazioneTokenClientId,String autenticazioneTokenSubject,String autenticazioneTokenUsername,String autenticazioneTokenEMail,
			String autorizzazione_token, String autorizzazione_tokenOptions,
			String autorizzazioneScope, int numScope, String autorizzazioneScopeMatch,BinaryParameter allegatoXacmlPolicy,
			String messageEngine,String canalePorta,
			String identificazioneAttributiStato, String[] attributeAuthorityLabels, String[] attributeAuthorityValues, String [] attributeAuthoritySelezionate, String attributeAuthorityAttributi,
			String ctModalitaSincronizzazione, String ctImplementazione, String ctContatori, String ctTipologia,
			String ctHeaderHttp, String ctHeaderHttp_limit, String ctHeaderHttp_remaining, String ctHeaderHttp_reset,
			String ctHeaderHttp_retryAfter, String ctHeaderHttp_retryAfterBackoff) throws Exception {

		Boolean contaListe = ServletUtils.getContaListeFromSession(this.session);

		// Soggetto virtuale?
		Boolean soggVirt = (Boolean) this.session.getAttribute(CostantiControlStation.SESSION_PARAMETRO_GESTIONE_SOGGETTI_VIRTUALI);
		
		Boolean confPers = (Boolean) this.session.getAttribute(CostantiControlStation.SESSION_PARAMETRO_GESTIONE_CONFIGURAZIONI_PERSONALIZZATE);
		
		// prelevo il flag che mi dice da quale pagina ho acceduto la sezione delle porte delegate
		Integer parentPA = ServletUtils.getIntegerAttributeFromSession(PorteApplicativeCostanti.ATTRIBUTO_PORTE_APPLICATIVE_PARENT, this.session);
		if(parentPA == null) parentPA = PorteApplicativeCostanti.ATTRIBUTO_PORTE_APPLICATIVE_PARENT_NONE;
		
		boolean isConfigurazione = parentPA.intValue() == PorteApplicativeCostanti.ATTRIBUTO_PORTE_APPLICATIVE_PARENT_CONFIGURAZIONE; 
		
		boolean datiInvocazione = false;
		boolean datiAltroPorta = false;
		boolean datiAltroApi = false; // indipendente dalla porta (viene utilizzata sempre la porta di default)
		if(isConfigurazione) {
			if(usataInConfigurazioneDefault) {
				datiInvocazione = ServletUtils.isCheckBoxEnabled(this.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_CONFIGURAZIONE_DATI_INVOCAZIONE));
			}
			datiAltroPorta = ServletUtils.isCheckBoxEnabled(this.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_CONFIGURAZIONE_ALTRO_PORTA));
			datiAltroApi = ServletUtils.isCheckBoxEnabled(this.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_CONFIGURAZIONE_ALTRO_API));
			
			DataElement de = new DataElement();
			de.setName(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_CONFIGURAZIONE_DATI_INVOCAZIONE);
			de.setType(DataElementType.HIDDEN);
			de.setValue(datiInvocazione+"");
			dati.addElement(de);
			
			de = new DataElement();
			de.setName(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_CONFIGURAZIONE_ALTRO_PORTA);
			de.setType(DataElementType.HIDDEN);
			de.setValue(datiAltroPorta+"");
			dati.addElement(de);
			
			de = new DataElement();
			de.setName(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_CONFIGURAZIONE_ALTRO_API);
			de.setType(DataElementType.HIDDEN);
			de.setValue(datiAltroApi+"");
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
		
		if(datiInvocazione || (!datiAltroPorta && !datiAltroApi)) {
			DataElement de = new DataElement();
			if(datiInvocazione) {
				de.setLabel(PorteApplicativeCostanti.LABEL_PARAMETRO_TITOLO_PORTE_APPLICATIVE_DATI_INVOCAZIONE);
			}
			else {
				de.setLabel(PorteApplicativeCostanti.LABEL_PARAMETRO_TITOLO_PORTE_APPLICATIVE_DATI_GENERALI);
			}
			de.setType(DataElementType.TITLE);
			dati.addElement(de);
		}
				
		DataElement de = new DataElement();
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
			
			IDSoggetto soggettoOperativo = null;
			if(asps!=null) {
				soggettoOperativo = new IDSoggetto(asps.getTipoSoggettoErogatore(), asps.getNomeSoggettoErogatore());
			}
			UrlInvocazioneAPI urlInvocazione = this.confCore.getConfigurazioneUrlInvocazione(protocollo, RuoloContesto.PORTA_APPLICATIVA, serviceBinding, nomePorta, soggettoOperativo,
					aspc, canalePorta);
			
			de = new DataElement();
			if(ServiceBinding.SOAP.equals(serviceBinding)) {
				de.setLabel(PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_URL_INVOCAZIONE);
			}
			else {
				de.setLabel(PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_BASE_URL_INVOCAZIONE);
			}
			de.setValue(urlInvocazione.getUrl());
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
		if(!isConfigurazione) {
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
		
		if(tipoOp.equals(TipoOperazione.ADD) || (tipoOp.equals(TipoOperazione.CHANGE) && !usataInConfigurazioni)) {
			azTmp.add(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_MODE_REGISTER_INPUT);
		}
		else if(datiInvocazione && ServiceBinding.SOAP.equals(serviceBinding)) {
			if(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_MODE_REGISTER_INPUT.equals(modeaz)) {
				azTmp.add(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_MODE_REGISTER_INPUT); // era stata impostata precedentemente
			}
			else {
				Map<String,String> azioni = this.porteApplicativeCore.getAzioniConLabel(asps, aspc, false, true, new ArrayList<String>());
				if(azioni==null || azioni.size()<=1) {
					azTmp.add(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_MODE_REGISTER_INPUT);
				}
			}
		}
		
		
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
		
		boolean disableSaveButtonForDatiInvocazione = true;
		
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
		
		// Fix per standard visualizzazione anche su soap
		// fornisco interfaceMode direttamente come per rest se cmq è abilitato sia il riconoscimento per url che anche per forceInterface.
		boolean visualizzazioneSpecialeSoapPerEssereUgualeARest = false;
		if(this.isModalitaStandard() && ServiceBinding.SOAP.equals(serviceBinding) &&
				PortaApplicativaAzioneIdentificazione.URL_BASED.getValue().equals(modeaz) &&
				(ServletUtils.isCheckBoxEnabled(forceWsdlBased) || CostantiRegistroServizi.ABILITATO.equals(forceWsdlBased)) 
				&& (aspc.getByteWsdlConcettuale()!=null)
				) {
			visualizzazioneSpecialeSoapPerEssereUgualeARest = true;
		}
		
		de = new DataElement();
		de.setLabel(PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_MODALITA_IDENTIFICAZIONE);
		de.setName(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_MODE_AZIONE);
		if(!usataInConfigurazioni || datiInvocazione) {
			if(viewOnlyModeDatiAzione || (tipoModeAzione!=null && tipoModeAzione.length==1) || visualizzazioneSpecialeSoapPerEssereUgualeARest) {
				de.setType(DataElementType.HIDDEN);
				de.setValue(modeaz);
				dati.addElement(de);
				
				de = new DataElement();
				de.setLabel(PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_MODALITA_IDENTIFICAZIONE);
				de.setName(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_MODE_AZIONE+"__LABEL");
				de.setType(DataElementType.TEXT);
				if(visualizzazioneSpecialeSoapPerEssereUgualeARest) {
					de.setValue(this.getPortaApplicativaAzioneIdentificazioneLabel(PortaApplicativaAzioneIdentificazione.INTERFACE_BASED.getValue()));
				}
				else {
					de.setValue(this.getPortaApplicativaAzioneIdentificazioneLabel(modeaz));
				}
			}
			else {
				de.setType(DataElementType.SELECT);
				de.setValues(tipoModeAzione);
				de.setLabels(tipoModeAzioneLabel); 
				de.setSelected(modeaz);
				de.setPostBack(true);
				
				disableSaveButtonForDatiInvocazione = false;
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
			
				if ( (!visualizzazioneSpecialeSoapPerEssereUgualeARest) &&
						(modeaz != null) && 
						modeaz.equals(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_MODE_REGISTER_INPUT)) {
					de = new DataElement();
					de.setLabel(PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_NOME);
					de.setType(DataElementType.SELECT);
					de.setName(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_AZIONE_ID);
					de.setValues(azioniList);
					de.setLabels(azioniListLabel);
					de.setSelected(azid);
					dati.addElement(de);
					
					disableSaveButtonForDatiInvocazione = false;
					
				} else {
		
					de = new DataElement();
					if ((modeaz != null) && (modeaz.equals(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_MODE_URL_BASED))) {
						de.setLabel(PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_ESPRESSIONE_REGOLARE);
						de.setValue(patternAzione);
						de.setRequired(true);
					} 
					else if ((modeaz != null) && (modeaz.equals(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_MODE_CONTENT_BASED))) {
						de.setLabel(PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_CONTENT_PATTERN);
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
		
					if ( (!visualizzazioneSpecialeSoapPerEssereUgualeARest) &&
							!PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_MODE_INPUT_BASED.equals(modeaz) && 
							!PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_MODE_SOAP_ACTION_BASED.equals(modeaz) && 
							!PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_MODE_PROTOCOL_BASED.equals(modeaz) && 
							!PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_MODE_INTERFACE_BASED.equals(modeaz) ){
						if(viewOnlyModeDatiAzione) {
							de.setType(DataElementType.TEXT);
							de.setRequired(false);
						}
						else {
							if(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_MODE_URL_BASED.equals(modeaz) ||
									PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_MODE_CONTENT_BASED.equals(modeaz) ) {
								de.setType(DataElementType.TEXT_AREA);
							}
							else {
								de.setType(DataElementType.TEXT_EDIT);
							}
							
							disableSaveButtonForDatiInvocazione = false;
						}
					}else
						de.setType(DataElementType.HIDDEN);
					de.setName(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_AZIONE);
					dati.addElement(de);
				}
		
				// se non e' selezionata la modalita userInput / wsdlbased / registerInput faccio vedere il check box forceWsdlbased
				de = new DataElement();
				de.setLabel(PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_FORCE_INTERFACE_BASED_LEFT);
				if( (!visualizzazioneSpecialeSoapPerEssereUgualeARest) &&
						modeaz!= null && 
						(
							!modeaz.equals(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_MODE_REGISTER_INPUT) &&
							!modeaz.equals(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_MODE_PROTOCOL_BASED) &&
							!modeaz.equals(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_MODE_INTERFACE_BASED)
						)
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
						de.setLabelRight(PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_FORCE_INTERFACE_BASED_RIGHT);
						if( ServletUtils.isCheckBoxEnabled(forceWsdlBased) || CostantiRegistroServizi.ABILITATO.equals(forceWsdlBased) ){
							de.setSelected(true);
						}
						
						disableSaveButtonForDatiInvocazione = false;
					}
				}
				else{
					de.setType(DataElementType.HIDDEN);
					de.setValue(forceWsdlBased);
				}
				de.setName(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_FORCE_INTERFACE_BASED);
				dati.addElement(de);
				
				if( (!visualizzazioneSpecialeSoapPerEssereUgualeARest) &&
						modeaz!= null && 
						(
							!modeaz.equals(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_MODE_REGISTER_INPUT) &&
							!modeaz.equals(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_MODE_PROTOCOL_BASED) &&
							!modeaz.equals(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_MODE_INTERFACE_BASED)
						)
				){
					de = new DataElement();
					de.setName(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_LIST_AZIONI_READ_ONLY);
					de.setLabel(this.getLabelAzioni(serviceBinding));
					Map<String,String> azioni = this.porteApplicativeCore.getAzioniConLabel(asps, aspc, false, true, new ArrayList<String>());
					StringBuilder bf = new StringBuilder();
					for (String az : azioni.keySet()) {
						if(bf.length()>0) {
							bf.append("\n");
						}
						bf.append(azioni.get(az));
					}
					de.setType(DataElementType.TEXT_AREA_NO_EDIT);
					if(azioni.size()<=CostantiControlStation.LABEL_PARAMETRO_TEXT_AREA_AZIONI_SIZE) {
						de.setRows(azioni.size());
					}
					else {
						de.setRows(CostantiControlStation.LABEL_PARAMETRO_TEXT_AREA_AZIONI_SIZE);
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
				if ((modeaz != null) && (modeaz.equals(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_MODE_URL_BASED))) {
					deLabel.setType(DataElementType.TEXT_AREA_NO_EDIT);
					deLabel.setLabel(PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_ESPRESSIONE_REGOLARE);
					deLabel.setValue(patternAzione);
				} 
				else if ((modeaz != null) && (modeaz.equals(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_MODE_CONTENT_BASED))) {
					deLabel.setType(DataElementType.TEXT_AREA_NO_EDIT);
					deLabel.setLabel(PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_CONTENT_PATTERN);
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
			de.setLabel(PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_FORCE_INTERFACE_BASED_LEFT);
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
					deLabel.setLabel(PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_FORCE_INTERFACE_BASED_LEFT);
					deLabel.setType(DataElementType.TEXT);
					deLabel.setValue(ServletUtils.isCheckBoxEnabled(forceWsdlBased) ? CostantiConfigurazione.ABILITATO.getValue() : CostantiConfigurazione.DISABILITATO.getValue() );
					dati.addElement(deLabel);
				}
			}
			
		}
		
		if(datiInvocazione && disableSaveButtonForDatiInvocazione) {
			this.pd.disableEditMode();
		}
		
		
		
		// *************** ServizioApplicativo Erogatore *********************
		
		if(!isConfigurazione && TipoOperazione.CHANGE.equals(tipoOp)){
				
			// Il link richiede ulteriori parametri.
			
//			de = new DataElement();
//			de.setType(DataElementType.TITLE);
//			de.setLabel(PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_SERVIZI_APPLICATIVI_EROGATORI);
//			dati.addElement(de);
//			
//			de = new DataElement();
//			de.setType(DataElementType.LINK);
//			
//			de.setUrl(ServiziApplicativiCostanti.SERVLET_NAME_SERVIZI_APPLICATIVI_ENDPOINT, pIdSogg, pIdPorta, pIdAsps,
//					new Parameter(ServiziApplicativiCostanti.PARAMETRO_SERVIZI_APPLICATIVI_NOME_SERVIZIO_APPLICATIVO, servizioApplicativo),
//					new Parameter(ServiziApplicativiCostanti.PARAMETRO_SERVIZI_APPLICATIVI_ID_SERVIZIO_APPLICATIVO, idSa+""));
//			ServletUtils.setDataElementVisualizzaLabel(de);
//
//
//			dati.addElement(de);
		
			
		}
		
		// *************** Controllo degli Accessi *********************
	
		if(!tipoOp.equals(TipoOperazione.ADD)) {
			if(!isConfigurazione) {
				
				// Il link richiede ulteriori parametri.
				
//				this.controlloAccessi(dati);
//				// 	controllo accessi
//				de = new DataElement();
//				de.setType(DataElementType.LINK);
//				de.setUrl(PorteApplicativeCostanti.SERVLET_NAME_PORTE_APPLICATIVE_CONTROLLO_ACCESSI, pIdSogg, pIdPorta, pIdAsps);
//				String statoControlloAccessi = this.getLabelStatoControlloAccessi(gestioneToken,autenticazione, autenticazioneOpzionale, autenticazioneCustom, autorizzazione, autorizzazioneContenuti,autorizzazioneCustom);
//				ServletUtils.setDataElementCustomLabel(de, PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_CONTROLLO_ACCESSI, statoControlloAccessi);
//				dati.addElement(de);
			}
		}else {
			// Pintori 29/11/2017 Gestione Accessi spostata nella servlet PorteApplicativeControlloAccessi,  in ADD devo mostrare comunque la form.
			
			String servletChiamante = PorteApplicativeCostanti.SERVLET_NAME_PORTE_APPLICATIVE_ADD;
			
			boolean forceAutenticato = false; 
			boolean forceHttps = false;
			boolean forceDisableOptional = false;
			if(this.isProfiloModIPA(protocollo)) {
				forceAutenticato = true; // in modI ci vuole sempre autenticazione https sull'erogazione (cambia l'opzionalita' o meno)
				forceHttps = forceAutenticato;
				forceDisableOptional = this.forceHttpsClientProfiloModiPA(IDAccordoFactory.getInstance().getIDAccordoFromAccordo(aspc),asps!=null ? asps.getPortType() : null);
			}
			
			this.controlloAccessiGestioneToken(dati, tipoOp, gestioneToken, gestioneTokenPolicyLabels, gestioneTokenPolicyValues, 
					gestioneTokenPolicy, gestioneTokenOpzionale, gestioneTokenValidazioneInput, gestioneTokenIntrospection, gestioneTokenUserInfo, gestioneTokenForward, null,protocollo,false);

			this.controlloAccessiAutenticazione(dati, tipoOp, servletChiamante, null,protocollo,
					autenticazione, autenticazioneCustom, autenticazioneOpzionale, 
					autenticazionePrincipal, autenticazioneParametroList,
					confPers, isSupportatoAutenticazioneSoggetti,false,
					gestioneToken, gestioneTokenPolicy, autenticazioneTokenIssuer, autenticazioneTokenClientId, autenticazioneTokenSubject, autenticazioneTokenUsername, autenticazioneTokenEMail,
					false, null, 0,
					forceHttps, forceDisableOptional);
			
			String urlAutorizzazioneAutenticati = null;
			String urlAutorizzazioneErogazioneApplicativiAutenticati = null;
			String urlAutorizzazioneRuoli = null;
			String urlAutorizzazioneScope = null;
			String urlAutorizzazioneErogazioneCustomPropertiesList = null;
			String urlAutorizzazioneContenutiErogazioneCustomPropertiesList = null;
			
			this.controlloAccessiAutorizzazione(dati, tipoOp, servletChiamante, null,protocollo,
					autenticazione, autenticazioneCustom,
					autorizzazione, autorizzazioneCustom, 
					autorizzazioneAutenticati, urlAutorizzazioneAutenticati, 0, null, null,
					autorizzazioneRuoli,  urlAutorizzazioneRuoli, numRuoli, null, 
					autorizzazioneRuoliTipologia, ruoloMatch,
					confPers, isSupportatoAutenticazioneSoggetti, contaListe, false, false,autorizzazioneScope,urlAutorizzazioneScope,numScope,null,autorizzazioneScopeMatch,
					gestioneToken, gestioneTokenPolicy, autorizzazione_token, autorizzazione_tokenOptions,allegatoXacmlPolicy,
					urlAutorizzazioneErogazioneApplicativiAutenticati, 0, urlAutorizzazioneErogazioneCustomPropertiesList , 0,
					identificazioneAttributiStato, attributeAuthorityLabels, attributeAuthorityValues, attributeAuthoritySelezionate, attributeAuthorityAttributi);
			
			this.controlloAccessiAutorizzazioneContenuti(dati, tipoOp, false, null,protocollo,
					autorizzazioneContenutiStato, 
					autorizzazioneContenuti, autorizzazioneContenutiProperties, serviceBinding, 
					false, urlAutorizzazioneContenutiErogazioneCustomPropertiesList, 0,
					confPers); 
		}
		
		
		
		// *************** Validazione Contenuti *********************
		if(!tipoOp.equals(TipoOperazione.ADD)) {
			if(!isConfigurazione) {
				
				// Il link richiede ulteriori parametri.
				
//				de = new DataElement();
//				de.setType(DataElementType.TITLE);
//				de.setLabel(PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_VALIDAZIONE_CONTENUTI);
//				dati.addElement(de);
//				
//				
//				// Validazione Contenuti
//				de = new DataElement();
//				de.setType(DataElementType.LINK);
//				de.setUrl(PorteApplicativeCostanti.SERVLET_NAME_PORTE_APPLICATIVE_VALIDAZIONE_CONTENUTI, pIdSogg, pIdPorta, pIdAsps);
//				ServletUtils.setDataElementCustomLabel(de, PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_VALIDAZIONE_CONTENUTI, statoValidazione);
//				dati.addElement(de);
			}
		}else {
			// 	Pintori 08/02/2018 Validazione Contenuti spostata nella servlet PorteApplicativeValidazione, in ADD devo mostrare comunque la form.
			this.validazioneContenuti(tipoOp, dati, true, false, statoValidazione, tipoValidazione, applicaMTOM,
					serviceBinding, aspc.getFormatoSpecifica());
		}
		
		// *************** Integrazione *********************
		
		Vector<DataElement> deIntegrazione = new Vector<DataElement>();
				
		boolean nascondiSezioneOpzioniAvanzate = (this.isModalitaStandard() || (isConfigurazione && !datiAltroPorta));
		
		if (tipoOp.equals(TipoOperazione.CHANGE)) {
			addIntegrazioneMetadatiToDati(dati, integrazioneStato, integrazione, integrazioneGruppi, integrazioneGruppiDaVisualizzare,	integrazioneGruppiValoriDeiGruppi, deIntegrazione, nascondiSezioneOpzioniAvanzate, false, serviceBinding);
		}
		
		String[] tipoStateless = { 
				PorteApplicativeCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_APPLICATIVE_STATELESS_DEFAULT, 
				PorteApplicativeCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_APPLICATIVE_STATELESS_ABILITATO, 
				PorteApplicativeCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_APPLICATIVE_STATELESS_DISABILITATO
		};
		de = new DataElement();
		de.setLabel(PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_STATELESS);
		de.setName(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_STATELESS);
		if(!this.core.isShowJ2eeOptions() || (isConfigurazione && !datiAltroPorta)){
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
		if(this.core.isConnettoriMultipliEnabled()) {
			de.setType(DataElementType.HIDDEN);
			dati.addElement(de);
		} else if (!this.isModalitaAvanzata() || (isConfigurazione && !datiAltroPorta)) {
			de.setType(DataElementType.HIDDEN);
			dati.addElement(de);
		}
		else{
			de.setType(DataElementType.TEXT_EDIT);
			deIntegrazione.addElement(de);	
		}
			
		if (tipoOp.equals(TipoOperazione.CHANGE)) {
			if( (this.isModalitaAvanzata() || this.porteApplicativeCore.isProprietaErogazioni_showModalitaStandard()) && !isConfigurazione){
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
			
			if(!isConfigurazione || datiAltroPorta) {
				de = new DataElement();
				de.setType(DataElementType.TITLE);
				de.setLabel(PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_INTEGRAZIONE);
				dati.addElement(de);
				
				for (int i = 0; i < deIntegrazione.size(); i++) {
					dati.addElement(deIntegrazione.get(i));
				}
			}
		}
		
		
		// Rate Limiting
		if(tipoOp.equals(TipoOperazione.CHANGE) && datiAltroPorta) {
			addOpzioniAvanzateRateLimitingToDati(dati,
					true,
					nascondiSezioneOpzioniAvanzate, 
					ctModalitaSincronizzazione, ctImplementazione, ctContatori, ctTipologia,
					ctHeaderHttp, ctHeaderHttp_limit, ctHeaderHttp_remaining, ctHeaderHttp_reset,
					ctHeaderHttp_retryAfter, ctHeaderHttp_retryAfterBackoff);
		}
		
		
		
		// Message Handlers
		if (tipoOp.equals(TipoOperazione.CHANGE)) {
			boolean visualizzaHandlers = this.confCore.isConfigurazioneHandlersEnabled();
			if(!nascondiSezioneOpzioniAvanzate && visualizzaHandlers) {
				this.visualizzaLinkHandlers(dati, false, TipoPdD.APPLICATIVA, Long.parseLong(idPorta), serviceBinding);
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
				
				// Il link richiede ulteriori parametri.
				
//				de = new DataElement();
//				de.setType(DataElementType.TITLE);
//				de.setLabel(PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_GESTIONE_MESSAGGIO);
//				dati.addElement(de);
//	
//				de = new DataElement();
//				de.setType(DataElementType.LINK);
//				de.setUrl(PorteApplicativeCostanti.SERVLET_NAME_PORTE_APPLICATIVE_CORRELAZIONE_APPLICATIVA,pIdSogg,pIdPorta,pNomePorta, pIdAsps);
//				String statoCorrelazioneApplicativa = PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_CORRELAZIONE_APPLICATIVA_DISABILITATA;
//				if(numCorrelazioneReq>0 || numCorrelazioneRes>0){
//					statoCorrelazioneApplicativa = PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_CORRELAZIONE_APPLICATIVA_ABILITATA;
//				}
//				ServletUtils.setDataElementCustomLabel(de, PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_CORRELAZIONE_APPLICATIVA, statoCorrelazioneApplicativa);
//				dati.addElement(de);
//				
//				de = new DataElement();
//				de.setType(DataElementType.LINK);
//				de.setUrl(PorteApplicativeCostanti.SERVLET_NAME_PORTE_APPLICATIVE_MESSAGE_SECURITY,pIdSogg,pIdPorta, pIdAsps);
//				ServletUtils.setDataElementCustomLabel(de, PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_MESSAGE_SECURITY, statoMessageSecurity);
//				dati.addElement(de);
//				
//				//if (InterfaceType.AVANZATA.equals(ServletUtils.getUserFromSession(this.session).getInterfaceType())) {
//				de = new DataElement();
//				de.setType(DataElementType.LINK);
//				de.setUrl(PorteApplicativeCostanti.SERVLET_NAME_PORTE_APPLICATIVE_MTOM, pIdSogg,pIdPorta, pIdAsps);
//				ServletUtils.setDataElementCustomLabel(de, PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_MTOM, statoMTOM);
//				dati.addElement(de);
				//}
			}
		}
		
		
		// *************** Asincroni *********************
		
		boolean supportoAsincroni = this.core.isProfiloDiCollaborazioneAsincronoSupportatoDalProtocollo(protocollo,serviceBinding);
		if(supportoAsincroni) {
			de = new DataElement();
			if ( nascondiSezioneOpzioniAvanzate) {
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
			if (nascondiSezioneOpzioniAvanzate) {
				de.setType(DataElementType.HIDDEN);
				de.setValue(ricsim);
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
			if (nascondiSezioneOpzioniAvanzate) {
				de.setType(DataElementType.HIDDEN);
				de.setValue(ricsim);
			}else{
				de.setType(DataElementType.SELECT);
				de.setValues(tipoRicasim);
				de.setSelected(ricasim);
			}
			de.setName(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_RICEVUTA_ASINCRONA_ASIMMETRICA);
			dati.addElement(de);
		}
		
		
		
		
		
		
		
		// ***************  SOAP With Attachments *********************

		boolean viewSoapWithAttachments = this.isModalitaAvanzata() && (!isConfigurazione || datiAltroPorta) && ServiceBinding.SOAP.equals(serviceBinding);
		
		if (viewSoapWithAttachments) {

			de = new DataElement();
			de.setType(DataElementType.TITLE);
			de.setLabel(PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_SOAP_WITH_ATTACHMENTS);
			dati.addElement(de);
			
		}
	
		String[] tipoGestBody = {
				PorteApplicativeCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_APPLICATIVE_GEST_BODY_NONE,
				PorteApplicativeCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_APPLICATIVE_GEST_BODY_ALLEGA,
				PorteApplicativeCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_APPLICATIVE_GEST_BODY_SCARTA 
		};
		de = new DataElement();
		de.setLabel(PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_GESTIONE_BODY);
		de.setName(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_GESTIONE_BODY);
		if (viewSoapWithAttachments) {
			de.setType(DataElementType.SELECT);
			de.setValues(tipoGestBody);
			de.setSelected(gestBody);
		}
		else {
			de.setType(DataElementType.HIDDEN);
			de.setValue(gestBody);
		}
		dati.addElement(de);

		String[] tipoGestManifest = { 
				PorteApplicativeCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_APPLICATIVE_GEST_MANIFEST_DEFAULT, 
				PorteApplicativeCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_APPLICATIVE_GEST_MANIFEST_ABILITATO, 
				PorteApplicativeCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_APPLICATIVE_GEST_MANIFEST_DISABILITATO 
		};
		de = new DataElement();
		de.setLabel(PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_GESTIONE_MANIFEST);
		de.setName(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_GESTIONE_MANIFEST);
		if(this.isFunzionalitaProtocolloSupportataDalProtocollo(protocollo, serviceBinding, FunzionalitaProtocollo.MANIFEST_ATTACHMENTS)){
			if(viewSoapWithAttachments) {
				de.setType(DataElementType.SELECT);
				de.setValues(tipoGestManifest);
				de.setSelected(gestManifest);
			}
			else {
				de.setType(DataElementType.HIDDEN);
				de.setValue(gestManifest);
			}
		}else {
			de.setType(DataElementType.HIDDEN);
			de.setValue(PorteApplicativeCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_APPLICATIVE_GEST_MANIFEST_DISABILITATO );
		}
		dati.addElement(de);
			
		
//		if(configurazioneStandardNonApplicabile){
//			this.pd.setMessage(CostantiControlStation.LABEL_CONFIGURAZIONE_IMPOSTATA_MODALITA_AVANZATA_LONG_MESSAGE, Costanti.MESSAGE_TYPE_INFO);
//			this.pd.disableEditMode();
//		}
		

		
		
		
		
		// ***************  MESSAGE FACTORY *********************
		
		if(!this.isModalitaStandard() && datiAltroApi) {
			de = new DataElement();
			de.setType(DataElementType.TITLE);
			de.setLabel(PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_MESSAGE_ENGINE);
			dati.addElement(de);
		}
		
		de = new DataElement();
		de.setName(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_GESTIONE_MESSAGE_ENGINE);
		de.setLabel(PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_GESTIONE_MESSAGE_ENGINE);
		if(!this.isModalitaStandard() && datiAltroApi) {
			de.setType(DataElementType.SELECT);
			List<String> lS = new ArrayList<String>();
			lS.add(CostantiControlStation.GESTIONE_MESSAGE_ENGINE_DEFAULT);
			lS.addAll(this.porteApplicativeCore.getMessageEngines());
			de.setValues(lS);
			if(messageEngine==null || !lS.contains(messageEngine)) {
				messageEngine = CostantiControlStation.GESTIONE_MESSAGE_ENGINE_DEFAULT;
			}
			de.setSelected(messageEngine);
		}
		else {
			de.setType(DataElementType.HIDDEN);
			de.setValue(messageEngine);
		}
		dati.addElement(de);


		
		return dati;
	}

	public List<PortaApplicativaAzioneIdentificazione> getModalitaIdentificazionePorta(String protocollo, ServiceBinding serviceBinding)
			throws ProtocolException, DriverConfigurazioneException {
		return getModalitaIdentificazionePorta(protocollo, serviceBinding, ProtocolPropertiesUtilities.getTipoInterfaccia(this));
	}
	public List<PortaApplicativaAzioneIdentificazione> getModalitaIdentificazionePorta(String protocollo, ServiceBinding serviceBinding, ConsoleInterfaceType consoleInterfaceType)
			throws ProtocolException, DriverConfigurazioneException { 
		
		if(consoleInterfaceType==null) {
			consoleInterfaceType = ProtocolPropertiesUtilities.getTipoInterfaccia(this);
		}
		
		if(serviceBinding == null) {
			List<ServiceBinding> serviceBindingListProtocollo = this.core.getServiceBindingListProtocollo(protocollo);
			
			List<PortaApplicativaAzioneIdentificazione> listaModalita = new ArrayList<PortaApplicativaAzioneIdentificazione>();
			if(serviceBindingListProtocollo != null && serviceBindingListProtocollo.size() > 0) {
				for (ServiceBinding serviceBinding2 : serviceBindingListProtocollo) {
					List<PortaApplicativaAzioneIdentificazione> listaModalitaTmp = ProtocolFactoryManager.getInstance().getProtocolFactoryByName(protocollo).
							createProtocolIntegrationConfiguration().getAllImplementationIdentificationResourceModes(serviceBinding2,
									consoleInterfaceType );
					
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
							consoleInterfaceType);
		}
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
			if((this.isModalitaAvanzata() || this.porteApplicativeCore.isProprietaErogazioni_showModalitaStandard()))
				listaLabel.add(PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_PROTOCOL_PROPERTIES);
//			if(this.core.isRegistroServiziLocale()){
//				//listaLabel.add(PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_SERVIZIO);
//				listaLabel.add(AccordiServizioParteSpecificaCostanti.LABEL_APS_MENU_VISUALE_AGGREGATA);
//			}
			if(extendedServletList!=null && extendedServletList.showExtendedInfo(this, null)){
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
					Parameter pIdAsps = null;
					if(idAsps==null || "".equals(idAsps)) {
						IDServizio idServizioObject = IDServizioFactory.getInstance().getIDServizioFromValues(pa.getServizio().getTipo(), pa.getServizio().getNome(), 
								pa.getTipoSoggettoProprietario(), pa.getNomeSoggettoProprietario(), 
								pa.getServizio().getVersione());
						AccordoServizioParteSpecifica asps = this.apsCore.getServizio(idServizioObject);
						pIdAsps = new Parameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_ASPS, asps.getId()+"");
					}
					else {
						pIdAsps = new Parameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_ASPS, idAsps);
					}

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
					String statoControlloAccessi = this.getStatoControlloAccessiPortaApplicativa(protocollo, pa);
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

					if((this.isModalitaAvanzata() || this.porteApplicativeCore.isProprietaErogazioni_showModalitaStandard())){
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

					
					if(extendedServletList!=null && extendedServletList.showExtendedInfo(this, null)){
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
				labelPerPorta = this.porteApplicativeCore.getLabelRegolaMappingErogazionePortaApplicativa(
						PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_PROTOCOL_PROPERTIES_CONFIG_DI,
						PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_PROTOCOL_PROPERTIES,
						myPA);
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
				labelPerPorta = this.porteApplicativeCore.getLabelRegolaMappingErogazionePortaApplicativa(
						PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_SERVIZIO_APPLICATIVO_CONFIG_DI,
						PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_SERVIZIO_APPLICATIVO,
						myPA);
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
				labelPerPorta = this.porteApplicativeCore.getLabelRegolaMappingErogazionePortaApplicativa(
						PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_CORRELAZIONI_APPLICATIVE_CONFIG_DI,
						PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_TRACCIAMENTO,
						myPA);
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
					//String nomeElemento = "(*)";
					String nomeElemento = CostantiControlStation.LABEL_PORTE_CORRELAZIONE_APPLICATIVA_QUALSIASI;
					if (cae.getNome() != null && !"".equals(cae.getNome()))
						nomeElemento = cae.getNome();
					de.setValue(nomeElemento);
					de.setIdToRemove("" + cae.getId());
					e.addElement(de);

					de = new DataElement();
					if(cae.getIdentificazione()!=null) {
						//de.setValue(cae.getIdentificazione().toString());
						switch (cae.getIdentificazione()) {
						case DISABILITATO:
							de.setValue(CostantiControlStation.LABEL_PARAMETRO_MODE_CORRELAZIONE_DISABILITATO);
							break;
						case HEADER_BASED:
							de.setValue(ModalitaIdentificazione.HEADER_BASED.getLabel());
							break;
						case URL_BASED:
							de.setValue(ModalitaIdentificazione.URL_BASED.getLabel());
							break;
						case CONTENT_BASED:
							de.setValue(ModalitaIdentificazione.CONTENT_BASED.getLabel());
							break;
						case INPUT_BASED:
							de.setValue(ModalitaIdentificazione.INPUT_BASED.getLabel());
							break;
						}
					}
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
				labelPerPorta = this.porteApplicativeCore.getLabelRegolaMappingErogazionePortaApplicativa(
						PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_CORRELAZIONI_APPLICATIVE_CONFIG_DI,
						PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_TRACCIAMENTO,
						myPA);
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
					//String nomeElemento = "(*)";
					String nomeElemento = CostantiControlStation.LABEL_PORTE_CORRELAZIONE_APPLICATIVA_QUALSIASI;
					if (cae.getNome() != null && !"".equals(cae.getNome()))
						nomeElemento = cae.getNome();
					de.setValue(nomeElemento);
					de.setIdToRemove("" + cae.getId());
					e.addElement(de);

					de = new DataElement();
					if(cae.getIdentificazione()!=null) {
						//de.setValue(cae.getIdentificazione().toString());
						switch (cae.getIdentificazione()) {
						case DISABILITATO:
							de.setValue(CostantiControlStation.LABEL_PARAMETRO_MODE_CORRELAZIONE_DISABILITATO);
							break;
						case HEADER_BASED:
							de.setValue(ModalitaIdentificazione.HEADER_BASED.getLabel());
							break;
						case CONTENT_BASED:
							de.setValue(ModalitaIdentificazione.CONTENT_BASED.getLabel());
							break;
						case INPUT_BASED:
							de.setValue(ModalitaIdentificazione.INPUT_BASED.getLabel());
							break;
						}
					}
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
				labelPerPorta = this.porteApplicativeCore.getLabelRegolaMappingErogazionePortaApplicativa(
						PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_MESSAGE_SECURITY_CONFIG_DI,
						PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_MESSAGE_SECURITY,
						myPA);
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
				labelPerPorta = this.porteApplicativeCore.getLabelRegolaMappingErogazionePortaApplicativa(
						PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_MESSAGE_SECURITY_CONFIG_DI,
						PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_MESSAGE_SECURITY,
						myPA);
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
			String idAsps = this.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_ASPS);
			if(idAsps == null)
				idAsps = "";

			String idPorta = this.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID);
			String idsogg = this.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_SOGGETTO);

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
				labelPerPorta = this.porteApplicativeCore.getLabelRegolaMappingErogazionePortaApplicativa(
						PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_CONTROLLO_ACCESSI_CONFIG_DI,
						PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_CONTROLLO_ACCESSI,
						myPA);
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
					String labelNomeSoggetto = this.getLabelNomeSoggetto(protocollo, sog.getTipo() , sog.getNome());
					
					String url = new Parameter("", SoggettiCostanti.SERVLET_NAME_SOGGETTI_CHANGE,
							new Parameter(SoggettiCostanti.PARAMETRO_SOGGETTO_ID,idSoggetto+""),
							new Parameter(SoggettiCostanti.PARAMETRO_SOGGETTO_NOME,sog.getNome()),
							new Parameter(SoggettiCostanti.PARAMETRO_SOGGETTO_TIPO,sog.getTipo())).getValue();
					if(this.isModalitaCompleta()) {
						de.setUrl(url);
					}else {
						this.newDataElementVisualizzaInNuovoTab(de, url, labelNomeSoggetto);
					}
					de.setValue(labelNomeSoggetto);
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

	public void preparePorteAppServizioApplicativoAutorizzatoList(String nomePorta, ISearch ricerca, List<PortaApplicativaAutorizzazioneServizioApplicativo> lista) throws Exception {
		try {
			// prelevo il flag che mi dice da quale pagina ho acceduto la sezione delle porte delegate
			Integer parentPA = ServletUtils.getIntegerAttributeFromSession(PorteApplicativeCostanti.ATTRIBUTO_PORTE_APPLICATIVE_PARENT, this.session);
			String idAsps = this.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_ASPS);
			if(idAsps == null)
				idAsps = "";

			String idPorta = this.request.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID);
			String idsogg = this.request.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_SOGGETTO);
			String modipa = this.request.getParameter(CostantiControlStation.PARAMETRO_PORTE_AUTORIZZAZIONE_MODIPA);
			boolean modipaEnabled = "true".equalsIgnoreCase(modipa);
			
			List<Parameter> listP = new ArrayList<>();
			listP.add(new Parameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID, idPorta));
			listP.add(new Parameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_SOGGETTO, idsogg));
			listP.add(new Parameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_ASPS, idAsps));
			if(modipa!=null) {
				listP.add(new Parameter(CostantiControlStation.PARAMETRO_PORTE_AUTORIZZAZIONE_MODIPA, modipa));
			}
			ServletUtils.addListElementIntoSession(this.session,  PorteApplicativeCostanti.OBJECT_NAME_PORTE_APPLICATIVE_SERVIZIO_APPLICATIVO_AUTORIZZATO,
					listP);

			int idLista = Liste.PORTE_APPLICATIVE_SERVIZIO_APPLICATIVO_AUTORIZZATO;
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
				labelPerPorta = this.porteApplicativeCore.getLabelRegolaMappingErogazionePortaApplicativa(
						PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_CONTROLLO_ACCESSI_CONFIG_DI,
						PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_CONTROLLO_ACCESSI,
						myPA);
			}
			else {
				labelPerPorta = PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_CONTROLLO_ACCESSI_CONFIG_DI+idporta;
			}
			
			lstParam.add(new Parameter(labelPerPorta, PorteApplicativeCostanti.SERVLET_NAME_PORTE_APPLICATIVE_CONTROLLO_ACCESSI, 
					new Parameter( PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID, idPorta),
					new Parameter( PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_SOGGETTO, idsogg),
					new Parameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_ASPS, idAsps)));
			
			String labelPag = PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_APPLICATIVI_CONFIG;
			
			this.pd.setSearchLabel(PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_NOME);
			if(search.equals("")){
				this.pd.setSearchDescription("");
				lstParam.add(new Parameter(labelPag,null));
			}else{
				lstParam.add(new Parameter(labelPag,
						PorteApplicativeCostanti.SERVLET_NAME_PORTE_APPLICATIVE_SERVIZIO_APPLICATIVO_AUTORIZZATO_LIST,
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
			List<String> labels = new ArrayList<>();
			if(this.porteApplicativeCore.isMultitenant() || modipaEnabled) {
				labels.add(PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_SOGGETTO);
			}
			labels.add(CostantiControlStation.LABEL_PARAMETRO_APPLICATIVO);
			this.pd.setLabels(labels.toArray(new String[1]));

			// preparo i dati
			Vector<Vector<DataElement>> dati = new Vector<Vector<DataElement>>();

			if (lista != null) {
				Iterator<PortaApplicativaAutorizzazioneServizioApplicativo> it = lista.iterator();
				while (it.hasNext()) {
					PortaApplicativaAutorizzazioneServizioApplicativo sa = it.next();

					Vector<DataElement> e = new Vector<DataElement>();

					if(this.porteApplicativeCore.isMultitenant() || modipaEnabled) {
						Long idSoggetto =this.soggettiCore.getIdSoggetto(sa.getNomeSoggettoProprietario(), sa.getTipoSoggettoProprietario());	
						DataElement de = new DataElement();
						if(this.isModalitaCompleta()) {
							de.setUrl(SoggettiCostanti.SERVLET_NAME_SOGGETTI_CHANGE,
									new Parameter(SoggettiCostanti.PARAMETRO_SOGGETTO_ID,idSoggetto+""),
									new Parameter(SoggettiCostanti.PARAMETRO_SOGGETTO_NOME,sa.getNomeSoggettoProprietario()),
									new Parameter(SoggettiCostanti.PARAMETRO_SOGGETTO_TIPO,sa.getTipoSoggettoProprietario()));
						}
						de.setValue(this.getLabelNomeSoggetto(protocollo, sa.getTipoSoggettoProprietario() , sa.getNomeSoggettoProprietario()));
						e.addElement(de);
					}
					
					
					DataElement de = new DataElement();
					de.setValue(sa.getNome());
					de.setIdToRemove(sa.getNome()+"@"+sa.getTipoSoggettoProprietario() + "/" + sa.getNomeSoggettoProprietario());
					
					if(!this.isModalitaCompleta()) {
						IDSoggetto idSoggettoProprietario = new IDSoggetto(sa.getTipoSoggettoProprietario(), sa.getNomeSoggettoProprietario());
						Soggetto soggettoProprietario = this.soggettiCore.getSoggettoRegistro(idSoggettoProprietario);
						String dominio = this.pddCore.isPddEsterna(soggettoProprietario.getPortaDominio()) ? SoggettiCostanti.SOGGETTO_DOMINIO_ESTERNO_VALUE : SoggettiCostanti.SOGGETTO_DOMINIO_OPERATIVO_VALUE;
						
						long idServizioApplicativo = this.saCore.getIdServizioApplicativo(idSoggettoProprietario, sa.getNome());
						String url = new Parameter("", ServiziApplicativiCostanti.SERVLET_NAME_SERVIZI_APPLICATIVI_CHANGE, 
								new Parameter(ServiziApplicativiCostanti.PARAMETRO_SERVIZI_APPLICATIVI_ID, idServizioApplicativo+""),
								new Parameter(ServiziApplicativiCostanti.PARAMETRO_SERVIZI_APPLICATIVI_PROVIDER, soggettoProprietario.getId()+""),
								new Parameter(SoggettiCostanti.PARAMETRO_SOGGETTO_DOMINIO, dominio)).getValue();
						String tooltip = sa.getNome();
						
						this.newDataElementVisualizzaInNuovoTab(de, url, tooltip);
					}
					
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
			String riusoIdMessaggio, Vector<DataElement> dati, org.openspcoop2.core.registry.constants.ServiceBinding serviceBinding) {
		
		DataElement de = new DataElement();
		de.setLabel(PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_CORRELAZIONE_APPLICATIVA);
		de.setType(DataElementType.TITLE);
		dati.addElement(de);
		
		dati.addElement(this.getDataElementNotCorrelazioneApplicativa());
		de = new DataElement();
		de.setLabel("");
		de.setValue("");
		de.setType(DataElementType.NOTE);
		dati.addElement(de);
		
		de = new DataElement();
		de.setLabel(PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_ELEMENTO_XML);
		de.setType(DataElementType.TEXT_EDIT);
		de.setName(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ELEMENTO_XML);
		de.setNote(PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_ELEMENTO_XML_NOTE);
		de.setSize(80);
		if (elemxml == null || CostantiControlStation.LABEL_PORTE_CORRELAZIONE_APPLICATIVA_QUALSIASI.equals(elemxml)) {
			de.setValue("");
		} else {
			de.setValue(elemxml);
		}
		DataElementInfo dInfoPattern = new DataElementInfo(PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_ELEMENTO_XML);
		dInfoPattern.setHeaderBody(CostantiControlStation.LABEL_CONFIGURAZIONE_CORRELAZIONE_APPLICATIVA_INFO_APPLICABILITA);
		if(org.openspcoop2.core.registry.constants.ServiceBinding.REST.equals(serviceBinding)) {
			dInfoPattern.setListBody(CostantiControlStation.LABEL_CONFIGURAZIONE_CORRELAZIONE_APPLICATIVA_INFO_APPLICABILITA_VALORI_REST);
		}
		else {
			dInfoPattern.setListBody(CostantiControlStation.LABEL_CONFIGURAZIONE_CORRELAZIONE_APPLICATIVA_INFO_APPLICABILITA_VALORI_SOAP);
		}
		de.setInfo(dInfoPattern);
		dati.addElement(de);

		//String[] tipoMode = { "urlBased", "contentBased", "inputBased","disabilitato" };
		String[] tipoMode = { 
				PorteApplicativeCostanti.VALUE_PARAMETRO_PORTE_APPLICATIVE_TIPO_MODE_CORRELAZIONE_URL_BASED,
				PorteApplicativeCostanti.VALUE_PARAMETRO_PORTE_APPLICATIVE_TIPO_MODE_CORRELAZIONE_HEADER_BASED,
				PorteApplicativeCostanti.VALUE_PARAMETRO_PORTE_APPLICATIVE_TIPO_MODE_CORRELAZIONE_CONTENT_BASED, 
				PorteApplicativeCostanti.VALUE_PARAMETRO_PORTE_APPLICATIVE_TIPO_MODE_CORRELAZIONE_INPUT_BASED, 
				PorteApplicativeCostanti.VALUE_PARAMETRO_PORTE_APPLICATIVE_TIPO_MODE_CORRELAZIONE_DISABILITATO
		};
		List<String> labels = ModalitaIdentificazione.getLabels(
				ModalitaIdentificazione.URL_BASED,
				ModalitaIdentificazione.HEADER_BASED,
				ModalitaIdentificazione.CONTENT_BASED,
				ModalitaIdentificazione.INPUT_BASED);
		labels.add(CostantiControlStation.LABEL_PARAMETRO_MODE_CORRELAZIONE_DISABILITATO);
		
		de = new DataElement();
		de.setLabel(PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_MODALITA_IDENTIFICAZIONE );
		de.setType(DataElementType.SELECT);
		de.setName(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_MODE);
		de.setValues(tipoMode);
		de.setLabels(labels);
		de.setSelected(mode);
		//				de.setOnChange("CambiaModeCorrAppPortaApplicativa('add')");
		de.setPostBack(true);
		dati.addElement(de);

		if (mode.equals(PorteApplicativeCostanti.VALUE_PARAMETRO_PORTE_APPLICATIVE_TIPO_MODE_CORRELAZIONE_URL_BASED) ||
				mode.equals(PorteApplicativeCostanti.VALUE_PARAMETRO_PORTE_APPLICATIVE_TIPO_MODE_CORRELAZIONE_HEADER_BASED) ||
				mode.equals(PorteApplicativeCostanti.VALUE_PARAMETRO_PORTE_APPLICATIVE_TIPO_MODE_CORRELAZIONE_CONTENT_BASED) 
				) {
			de = new DataElement();
			if(mode.equals(PorteApplicativeCostanti.VALUE_PARAMETRO_PORTE_APPLICATIVE_TIPO_MODE_CORRELAZIONE_HEADER_BASED)) {
				de.setLabel(PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_NOME);
				de.setType(DataElementType.TEXT_EDIT);
			}
			else {
				if(mode.equals(PorteApplicativeCostanti.VALUE_PARAMETRO_PORTE_APPLICATIVE_TIPO_MODE_CORRELAZIONE_URL_BASED)) {
					de.setLabel(PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_ESPRESSIONE_REGOLARE);
				}
				else {
					de.setLabel(PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_CONTENT_PATTERN);
				}
				de.setType(DataElementType.TEXT_AREA);
				
				if(mode.equals(PorteApplicativeCostanti.VALUE_PARAMETRO_PORTE_APPLICATIVE_TIPO_MODE_CORRELAZIONE_CONTENT_BASED)) {
					dInfoPattern = new DataElementInfo(PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_CONTENT_PATTERN);
					if(org.openspcoop2.core.registry.constants.ServiceBinding.REST.equals(serviceBinding)) {
						dInfoPattern.setHeaderBody(CostantiControlStation.LABEL_CONFIGURAZIONE_CORRELAZIONE_APPLICATIVA_INFO_PATTERN_REST);
						dInfoPattern.setListBody(CostantiControlStation.LABEL_CONFIGURAZIONE_CORRELAZIONE_APPLICATIVA_INFO_PATTERN_VALORI_REST);
					}
					else {
						dInfoPattern.setBody(CostantiControlStation.LABEL_CONFIGURAZIONE_CORRELAZIONE_APPLICATIVA_INFO_PATTERN_SOAP);
					}
					de.setInfo(dInfoPattern);
				}
			}
			if (pattern == null) {
				de.setValue("");
			} else {
				de.setValue(pattern);
			}
			de.setName(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_PATTERN);
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
			Vector<DataElement> dati, org.openspcoop2.core.registry.constants.ServiceBinding serviceBinding) {
		
		DataElement de = new DataElement();
		de.setLabel(PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_CORRELAZIONE_APPLICATIVA);
		de.setType(DataElementType.TITLE);
		dati.addElement(de);
		
		dati.addElement(this.getDataElementNotCorrelazioneApplicativa());
		de = new DataElement();
		de.setLabel("");
		de.setValue("");
		de.setType(DataElementType.NOTE);
		dati.addElement(de);
		
		de = new DataElement();
		de.setLabel(PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_ELEMENTO_XML);
		de.setType(DataElementType.TEXT_EDIT);
		de.setNote(PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_ELEMENTO_XML_NOTE);
		de.setName(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ELEMENTO_XML);
		de.setSize(80);
		if (elemxml == null || CostantiControlStation.LABEL_PORTE_CORRELAZIONE_APPLICATIVA_QUALSIASI.equals(elemxml)) {
			de.setValue("");
		} else {
			de.setValue(elemxml);
		}
		DataElementInfo dInfoPattern = new DataElementInfo(PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_ELEMENTO_XML);
		dInfoPattern.setHeaderBody(CostantiControlStation.LABEL_CONFIGURAZIONE_CORRELAZIONE_APPLICATIVA_INFO_APPLICABILITA);
		if(org.openspcoop2.core.registry.constants.ServiceBinding.REST.equals(serviceBinding)) {
			dInfoPattern.setListBody(CostantiControlStation.LABEL_CONFIGURAZIONE_CORRELAZIONE_APPLICATIVA_INFO_APPLICABILITA_VALORI_REST);
		}
		else {
			dInfoPattern.setListBody(CostantiControlStation.LABEL_CONFIGURAZIONE_CORRELAZIONE_APPLICATIVA_INFO_APPLICABILITA_VALORI_SOAP);
		}
		de.setInfo(dInfoPattern);
		dati.addElement(de);

		String[] tipoMode = {
				PorteApplicativeCostanti.VALUE_PARAMETRO_PORTE_APPLICATIVE_TIPO_MODE_CORRELAZIONE_HEADER_BASED,
				PorteApplicativeCostanti.VALUE_PARAMETRO_PORTE_APPLICATIVE_TIPO_MODE_CORRELAZIONE_CONTENT_BASED,
				PorteApplicativeCostanti.VALUE_PARAMETRO_PORTE_APPLICATIVE_TIPO_MODE_CORRELAZIONE_INPUT_BASED,
				PorteApplicativeCostanti.VALUE_PARAMETRO_PORTE_APPLICATIVE_TIPO_MODE_CORRELAZIONE_DISABILITATO
		};
		
		List<String> labels = ModalitaIdentificazione.getLabels(
				ModalitaIdentificazione.HEADER_BASED,
				ModalitaIdentificazione.CONTENT_BASED,
				ModalitaIdentificazione.INPUT_BASED);
		labels.add(CostantiControlStation.LABEL_PARAMETRO_MODE_CORRELAZIONE_DISABILITATO);
		
		de = new DataElement();
		de.setLabel(PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_MODALITA_IDENTIFICAZIONE);
		de.setType(DataElementType.SELECT);
		de.setName(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_MODE);
		de.setValues(tipoMode);
		de.setLabels(labels);
		de.setSelected(mode);
		//				de.setOnChange("CambiaModeCorrAppPortaApplicativa('add','Risposta')");
		de.setPostBack(true);
		dati.addElement(de);

		if (mode.equals(PorteApplicativeCostanti.VALUE_PARAMETRO_PORTE_APPLICATIVE_TIPO_MODE_CORRELAZIONE_URL_BASED) ||
				mode.equals(PorteApplicativeCostanti.VALUE_PARAMETRO_PORTE_APPLICATIVE_TIPO_MODE_CORRELAZIONE_HEADER_BASED) ||
				mode.equals(PorteApplicativeCostanti.VALUE_PARAMETRO_PORTE_APPLICATIVE_TIPO_MODE_CORRELAZIONE_CONTENT_BASED)) {
			de = new DataElement();
			if(mode.equals(PorteApplicativeCostanti.VALUE_PARAMETRO_PORTE_APPLICATIVE_TIPO_MODE_CORRELAZIONE_HEADER_BASED)) {
				de.setLabel(PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_NOME);
				de.setType(DataElementType.TEXT_EDIT);
			}
			else {
				if(mode.equals(PorteApplicativeCostanti.VALUE_PARAMETRO_PORTE_APPLICATIVE_TIPO_MODE_CORRELAZIONE_URL_BASED)) {
					de.setLabel(PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_ESPRESSIONE_REGOLARE);
				}
				else {
					de.setLabel(PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_CONTENT_PATTERN);
				}
				de.setType(DataElementType.TEXT_AREA);
				
				if(mode.equals(PorteApplicativeCostanti.VALUE_PARAMETRO_PORTE_APPLICATIVE_TIPO_MODE_CORRELAZIONE_CONTENT_BASED)) {
					dInfoPattern = new DataElementInfo(PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_CONTENT_PATTERN);
					if(org.openspcoop2.core.registry.constants.ServiceBinding.REST.equals(serviceBinding)) {
						dInfoPattern.setHeaderBody(CostantiControlStation.LABEL_CONFIGURAZIONE_CORRELAZIONE_APPLICATIVA_INFO_PATTERN_REST);
						dInfoPattern.setListBody(CostantiControlStation.LABEL_CONFIGURAZIONE_CORRELAZIONE_APPLICATIVA_INFO_PATTERN_VALORI_REST);
					}
					else {
						dInfoPattern.setBody(CostantiControlStation.LABEL_CONFIGURAZIONE_CORRELAZIONE_APPLICATIVA_INFO_PATTERN_SOAP);
					}
					de.setInfo(dInfoPattern);
				}
			}
			if (pattern == null) {
				de.setValue("");
			} else {
				de.setValue(pattern);
			}
			de.setName(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_PATTERN);
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
				labelPerPorta = this.porteApplicativeCore.getLabelRegolaMappingErogazionePortaApplicativa(
						PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_MTOM_CONFIG_DI,
						PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_MTOM_CONFIG,
						myPA);
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
				labelPerPorta = this.porteApplicativeCore.getLabelRegolaMappingErogazionePortaApplicativa(
						PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_MTOM_CONFIG_DI,
						PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_MTOM_CONFIG,
						myPA);
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
				labelPerPorta = this.porteApplicativeCore.getLabelRegolaMappingErogazionePortaApplicativa(
						PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_CONTROLLO_ACCESSI_CONFIG_DI,
						PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_CONTROLLO_ACCESSI,
						pa);
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
					
					if(!this.isModalitaCompleta()) {
						Ruolo ruoloObj = this.ruoliCore.getRuolo(ruolo);
						Parameter pIdRuolo = new Parameter(RuoliCostanti.PARAMETRO_RUOLO_ID, ruoloObj.getId()+"");
						
						String url = new Parameter("", RuoliCostanti.SERVLET_NAME_RUOLI_CHANGE , pIdRuolo).getValue();
						String tooltip = ruolo;
						
						this.newDataElementVisualizzaInNuovoTab(de, url, tooltip);
					}
					
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
				labelPerPorta = this.porteApplicativeCore.getLabelRegolaMappingErogazionePortaApplicativa(
						PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_CONTROLLO_ACCESSI_CONFIG_DI,
						PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_CONTROLLO_ACCESSI,
						pa);
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
					
					if(!this.isModalitaCompleta()) {
						Scope scopeObj = this.scopeCore.getScope(scope);
						Parameter pIdScope = new Parameter(ScopeCostanti.PARAMETRO_SCOPE_ID, scopeObj.getId()+"");
						
						String url = new Parameter("", ScopeCostanti.SERVLET_NAME_SCOPE_CHANGE , pIdScope).getValue();
						String tooltip = scope;
						
						this.newDataElementVisualizzaInNuovoTab(de, url, tooltip);
					}
					
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
			
			Boolean vistaErogazioni = ServletUtils.getBooleanAttributeFromSession(ErogazioniCostanti.ASPS_EROGAZIONI_ATTRIBUTO_VISTA_EROGAZIONI, this.session);
			if(vistaErogazioni != null && vistaErogazioni.booleanValue()) {
				lstParam.add(new Parameter(ErogazioniCostanti.LABEL_ASPS_EROGAZIONI, ErogazioniCostanti.SERVLET_NAME_ASPS_EROGAZIONI_LIST));
				lstParam.add(new Parameter(servizioTmpTile, ErogazioniCostanti.SERVLET_NAME_ASPS_EROGAZIONI_CHANGE, pIdServizio,pNomeServizio, pTipoServizio));
				boolean gestioneGruppi = true;
				String paramGestioneGruppi = ServletUtils.getObjectFromSession(this.session, String.class, AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_GESTIONE_GRUPPI);
				if(paramGestioneGruppi!=null && !"".equals(paramGestioneGruppi)) {
					gestioneGruppi = Boolean.valueOf(paramGestioneGruppi);
				}
				
				boolean gestioneConfigurazioni = true;
				String paramGestioneConfigurazioni = ServletUtils.getObjectFromSession(this.session, String.class, AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_GESTIONE_CONFIGURAZIONI);
				if(paramGestioneConfigurazioni!=null && !"".equals(paramGestioneConfigurazioni)) {
					gestioneConfigurazioni = Boolean.valueOf(paramGestioneConfigurazioni);
				}
				
				AccordoServizioParteComuneSintetico as = this.apcCore.getAccordoServizioSintetico(asps.getIdAccordo());
				ServiceBinding serviceBinding = this.apcCore.toMessageServiceBinding(as.getServiceBinding());
				String labelConfigurazione = gestioneConfigurazioni ? ErogazioniCostanti.LABEL_ASPS_GESTIONE_CONFIGURAZIONI : 
					(gestioneGruppi ? MessageFormat.format(ErogazioniCostanti.LABEL_ASPS_GESTIONE_GRUPPI_CON_PARAMETRO, this.getLabelAzioni(serviceBinding)) : AccordiServizioParteSpecificaCostanti.LABEL_APS_PORTE_APPLICATIVE);
				
				lstParam.add(new Parameter(labelConfigurazione, AccordiServizioParteSpecificaCostanti.SERVLET_NAME_APS_PORTE_APPLICATIVE_LIST ,pIdServizio,pNomeServizio, pTipoServizio, pIdsoggErogatore));
			} else {
				lstParam.add(new Parameter(AccordiServizioParteSpecificaCostanti.LABEL_APS, AccordiServizioParteSpecificaCostanti.SERVLET_NAME_APS_LIST));
		//	lstParam.add(new Parameter(servizioTmpTile, AccordiServizioParteSpecificaCostanti.SERVLET_NAME_APS_CHANGE, pIdServizio,pNomeServizio, pTipoServizio));
			lstParam.add(new Parameter(AccordiServizioParteSpecificaCostanti.LABEL_APS_CONFIGURAZIONI_DI + servizioTmpTile, 
					AccordiServizioParteSpecificaCostanti.SERVLET_NAME_APS_PORTE_APPLICATIVE_LIST ,pIdServizio,pNomeServizio, pTipoServizio, pIdsoggErogatore));
			}
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
	
	public String getMessaggioConfermaModificaRegolaMappingErogazionePortaApplicativa(boolean fromAPI, PortaApplicativa pa, ServiceBinding serviceBinding,boolean abilitazione, boolean multiline,boolean listElement) throws DriverConfigurazioneException {
		MappingErogazionePortaApplicativa mapping = this.porteApplicativeCore.getMappingErogazionePortaApplicativa(pa);
		List<String> listaAzioni = pa.getAzione()!= null ?  pa.getAzione().getAzioneDelegataList() : new ArrayList<String>();
		return this.getMessaggioConfermaModificaRegolaMapping(fromAPI, mapping.isDefault(), listaAzioni, serviceBinding, mapping.getDescrizione(), abilitazione, multiline, listElement);
	}
	
	
	
	public void prepareResponseCachingConfigurazioneRegolaList(String nomePorta, ISearch ricerca, List<ResponseCachingConfigurazioneRegola> lista, Integer defaultCacheSeconds) throws Exception {
		try {
			// prelevo il flag che mi dice da quale pagina ho acceduto la sezione delle porte delegate
			Integer parentPA = ServletUtils.getIntegerAttributeFromSession(PorteApplicativeCostanti.ATTRIBUTO_PORTE_APPLICATIVE_PARENT, this.session);
			String idAsps = this.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_ASPS);
			if(idAsps == null)
				idAsps = "";

			String id = this.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID);
			String idsogg = this.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_SOGGETTO);

			ServletUtils.addListElementIntoSession(this.session, PorteApplicativeCostanti.OBJECT_NAME_PORTE_APPLICATIVE_RESPONSE_CACHING_CONFIGURAZIONE_REGOLA,
					new Parameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID, id), 
					new Parameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_SOGGETTO, idsogg),
					new Parameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_ASPS, idAsps));

			int idLista = Liste.PORTE_APPLICATIVE_RESPONSE_CACHING_CONFIGURAZIONE_REGOLA;
			int limit = ricerca.getPageSize(idLista);
			int offset = ricerca.getIndexIniziale(idLista);

			this.pd.setIndex(offset);
			this.pd.setPageSize(limit);
			this.pd.setSearch("");
			this.pd.setNumEntries(ricerca.getNumEntries(idLista));
			this.pd.setSearchDescription("");
			
			ServletUtils.disabledPageDataSearch(this.pd);

			PortaApplicativa pa = this.porteApplicativeCore.getPortaApplicativa(Integer.parseInt(id));
			String idporta = pa.getNome();

			// setto la barra del titolo
			List<Parameter> lstParam = this.getTitoloPA(parentPA, idsogg, idAsps);
			
			String labelPerPorta = null;
			if(parentPA!=null && (parentPA.intValue() == PorteApplicativeCostanti.ATTRIBUTO_PORTE_APPLICATIVE_PARENT_CONFIGURAZIONE)) {
				labelPerPorta = this.porteApplicativeCore.getLabelRegolaMappingErogazionePortaApplicativa(
						PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_RESPONSE_CACHING_CONFIGURAZIONE_CONFIG_DI,
						PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_RESPONSE_CACHING_CONFIGURAZIONE,
						pa);
			}
			else {
				labelPerPorta = PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_RESPONSE_CACHING_CONFIGURAZIONE_CONFIG_DI+idporta;
			}
			
			lstParam.add(new Parameter(labelPerPorta, PorteApplicativeCostanti.SERVLET_NAME_PORTE_APPLICATIVE_RESPONSE_CACHING, 
					new Parameter( PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID, id),
					new Parameter( PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_SOGGETTO, idsogg),
					new Parameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_ASPS, idAsps)));
			
			String labelPagLista = PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_RESPONSE_CACHING_CONFIGURAZIONE_REGOLE;
			
			lstParam.add(new Parameter(labelPagLista,null));

			ServletUtils.setPageDataTitle(this.pd, lstParam.toArray(new Parameter[lstParam.size()]));

			// setto le label delle colonne
			String[] labels = {
					PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_RESPONSE_CACHING_CONFIGURAZIONE_REGOLA_RETURN_CODE,
					PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_RESPONSE_CACHING_CONFIGURAZIONE_REGOLA_FAULT,
					PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_RESPONSE_CACHING_CONFIGURAZIONE_REGOLA_CACHE_TIMEOUT_SECONDS
			};
			this.pd.setLabels(labels);

			// preparo i dati
			Vector<Vector<DataElement>> dati = new Vector<Vector<DataElement>>();

			if (lista != null) {
				Iterator<ResponseCachingConfigurazioneRegola> it = lista.iterator();
				while (it.hasNext()) {
					ResponseCachingConfigurazioneRegola regola = it.next();

					Vector<DataElement> e = new Vector<DataElement>();

					DataElement de = new DataElement();
					de.setIdToRemove(regola.getId() + "");
					
					Integer statusMin = regola.getReturnCodeMin();
					Integer statusMax = regola.getReturnCodeMax();
					
					// se e' stato salvato il valore 0 lo tratto come null
					if(statusMin != null && statusMin.intValue() <= 0) {
						statusMin = null;
					}
					
					if(statusMax != null && statusMax.intValue() <= 0) {
						statusMax = null;
					}
					
					String statusValue = null;
					// Intervallo
					if(statusMin != null && statusMax != null) {
						if(statusMax.longValue() == statusMin.longValue()) // esatto
							statusValue = statusMin + "";
						else 
							statusValue = "[" + statusMin + " - " + statusMax + "]";
					} else if(statusMin != null && statusMax == null) { // definito solo l'estremo inferiore
						statusValue = "&gt;" + statusMin;
					} else if(statusMin == null && statusMax != null) { // definito solo l'estremo superiore
						statusValue = "&lt;" + statusMax;
					} else { //entrambi null 
						statusValue = CostantiControlStation.LABEL_PARAMETRO_CONFIGURAZIONE_RETURN_CODE_QUALSIASI;
					}
					
					de.setValue(statusValue);
					e.addElement(de);
					
					de = new DataElement();
					de.setValue(regola.getFault() ? CostantiControlStation.LABEL_SI : CostantiControlStation.LABEL_NO);
					e.addElement(de);
					
					de = new DataElement();
					de.setValue(regola.getCacheTimeoutSeconds() != null ? regola.getCacheTimeoutSeconds() + "" : "default ("+defaultCacheSeconds+")");
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
	
	// Controlla i dati del registro
	public boolean responseCachingConfigurazioneRegolaCheckData(TipoOperazione tipoOp, long idPorta) throws Exception {

		try{
			
			if(this.checkRegolaResponseCaching() == false) {
				return false;
			}
			
			String returnCode = this.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_RESPONSE_CACHING_CONFIGURAZIONE_REGOLA_RETURN_CODE);
			String statusMinS = this.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_RESPONSE_CACHING_CONFIGURAZIONE_REGOLA_RETURN_CODE_MIN);
			String statusMaxS = this.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_RESPONSE_CACHING_CONFIGURAZIONE_REGOLA_RETURN_CODE_MAX);
			String faultS = this.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_RESPONSE_CACHING_CONFIGURAZIONE_REGOLA_FAULT);
			
			Integer statusMin = null;
			Integer statusMax = null;
			boolean fault = ServletUtils.isCheckBoxEnabled(faultS);
			
			if(!returnCode.equals(CostantiControlStation.VALUE_PARAMETRO_CONFIGURAZIONE_RETURN_CODE_QUALSIASI)) {
				
				if(StringUtils.isNotEmpty(statusMinS)) {
					statusMin = Integer.parseInt(statusMinS);
				}
				
				if(returnCode.equals(CostantiControlStation.VALUE_PARAMETRO_CONFIGURAZIONE_RETURN_CODE_INTERVALLO)) {
					if(StringUtils.isNotEmpty(statusMaxS)) {
						statusMax = Integer.parseInt(statusMaxS);
					}
				}

				// return code esatto, ho salvato lo stesso valore nel campo return code;
				if(returnCode.equals(CostantiControlStation.VALUE_PARAMETRO_CONFIGURAZIONE_RETURN_CODE_ESATTO))
					statusMax = statusMin;
			}
			
			// Se tipoOp = add, controllo che il registro non sia gia' stato
			// registrata
			if (tipoOp.equals(TipoOperazione.ADD)) {
				boolean giaRegistrato = this.porteApplicativeCore.existsResponseCachingConfigurazioneRegola(idPorta,statusMin, statusMax, fault);

				if (giaRegistrato) {
					this.pd.setMessage("&Egrave; gi&agrave; presente una Regola di Response Caching con in parametri indicati.");
					return false;
				}
			}

			return true;

		} catch (Exception e) {
			this.log.error("Exception: " + e.getMessage(), e);
			throw new Exception(e);
		}
	}
	
	public void preparePorteAppTrasformazioniRegolaList(String nomePorta, ISearch ricerca, List<TrasformazioneRegola> lista) throws Exception {
		try {
			// prelevo il flag che mi dice da quale pagina ho acceduto la sezione delle porte delegate
			Integer parentPA = ServletUtils.getIntegerAttributeFromSession(PorteApplicativeCostanti.ATTRIBUTO_PORTE_APPLICATIVE_PARENT, this.session);
			String idAsps = this.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_ASPS);
			if(idAsps == null)
				idAsps = "";

			String idPorta = this.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID);
			String idsogg = this.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_SOGGETTO);

			Parameter pIdPorta = new Parameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID, idPorta);
			Parameter pIdSoggetto = new Parameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_SOGGETTO, idsogg);
			Parameter pIdAsps = new Parameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_ASPS, idAsps);
			ServletUtils.addListElementIntoSession(this.session,  PorteApplicativeCostanti.OBJECT_NAME_PORTE_APPLICATIVE_TRASFORMAZIONI,
					pIdPorta,
					pIdSoggetto,
					pIdAsps);
			Parameter pFromList = new Parameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_TRASFORMAZIONI_APPLICABILITA_LIST, "true");
			
			int idLista = Liste.PORTE_APPLICATIVE_TRASFORMAZIONI;
			int limit = ricerca.getPageSize(idLista);
			int offset = ricerca.getIndexIniziale(idLista);

			this.pd.setIndex(offset);
			this.pd.setPageSize(limit);
			this.pd.setSearch("");
			this.pd.setNumEntries(ricerca.getNumEntries(idLista));
			this.pd.setSearchDescription("");
			
			ServletUtils.disabledPageDataSearch(this.pd);

			PortaApplicativa myPA = this.porteApplicativeCore.getPortaApplicativa(Integer.parseInt(idPorta));
			String idporta = myPA.getNome();
			
			// Prendo il nome e il tipo del servizio
			AccordoServizioParteSpecifica asps = this.apsCore.getAccordoServizioParteSpecifica(Integer.parseInt(idAsps));
			AccordoServizioParteComuneSintetico apc = this.apcCore.getAccordoServizioSintetico(asps.getIdAccordo()); 
			Map<String,String> azioni = this.porteApplicativeCore.getAzioniConLabel(asps, apc, false, true, new ArrayList<String>());

			Boolean contaListe = ServletUtils.getContaListeFromSession(this.session);
			boolean autenticazione = !TipoAutenticazione.DISABILITATO.equals(myPA.getAutenticazione());
			String protocollo = this.apsCore.getProtocolloAssociatoTipoServizio(myPA.getServizio().getTipo());
			boolean isSupportatoAutenticazione = this.soggettiCore.isSupportatoAutenticazioneApplicativiErogazione(protocollo);

			
			// setto la barra del titolo
			List<Parameter> lstParam = this.getTitoloPA(parentPA, idsogg, idAsps);

			String labelPerPorta = null;
			if(parentPA!=null && (parentPA.intValue() == PorteApplicativeCostanti.ATTRIBUTO_PORTE_APPLICATIVE_PARENT_CONFIGURAZIONE)) {
				labelPerPorta = this.porteApplicativeCore.getLabelRegolaMappingErogazionePortaApplicativa(
						PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_TRASFORMAZIONI_DI,
						PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_TRASFORMAZIONI,
						myPA);
			}
			else {
				labelPerPorta = PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_TRASFORMAZIONI_DI+idporta;
			}
			
			lstParam.add(new Parameter(labelPerPorta,null));

			// setto la barra del titolo
			ServletUtils.setPageDataTitle(this.pd, lstParam.toArray(new Parameter[lstParam.size()]));

			// setto le label delle colonne
			String nomeColonnaAzione = null;
			if(org.openspcoop2.core.registry.constants.ServiceBinding.REST.equals(apc.getServiceBinding())) {
				nomeColonnaAzione = CostantiControlStation.LABEL_PARAMETRO_CONFIGURAZIONE_TRASFORMAZIONI_APPLICABILITA_RISORSE;
			}
			else {
				nomeColonnaAzione = CostantiControlStation.LABEL_PARAMETRO_CONFIGURAZIONE_TRASFORMAZIONI_APPLICABILITA_AZIONI;
			}
			List<String> lstLabels = new ArrayList<>();
			if(lista != null && lista.size() > 1)
				lstLabels.add(PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_TRASFORMAZIONI_POSIZIONE);
			lstLabels.add(PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_TRASFORMAZIONI_STATO);
			lstLabels.add(PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_TRASFORMAZIONI_NOME);
			lstLabels.add(nomeColonnaAzione);
			lstLabels.add(PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_TRASFORMAZIONI_APPLICABILITA_CT);
			lstLabels.add(PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_TRASFORMAZIONI_APPLICABILITA_PATTERN);
			if(myPA!=null && myPA.getBehaviour()!=null && myPA.sizeServizioApplicativoList()>0) {
				lstLabels.add(PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_TRASFORMAZIONI_APPLICABILITA_CONNETTORI);
			}
			if(autenticazione){
				lstLabels.add(PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_SOGGETTI);
			}
			if(isSupportatoAutenticazione && autenticazione) {
				lstLabels.add( PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_APPLICATIVI);// uso cmq label PD
			}
			this.pd.setLabels(lstLabels.toArray(new String [lstLabels.size()]));

			// preparo i dati
			Vector<Vector<DataElement>> dati = new Vector<Vector<DataElement>>();

			if (lista != null) {
				Iterator<TrasformazioneRegola> it = lista.iterator();
				int numeroElementi = lista.size();
				int i = 0;
				while (it.hasNext()) {
					TrasformazioneRegola regola = it.next();
					Parameter pIdTrasformazione = new Parameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_TRASFORMAZIONE, regola.getId() + "");
					
					Vector<DataElement> e = new Vector<DataElement>();
					
					// Posizione
					if(lista.size() > 1) {
						DataElement de = new DataElement();
						de.setWidthPx(48);
						de.setType(DataElementType.IMAGE);
						DataElementImage imageUp = new DataElementImage();
						Parameter pDirezioneSu = new Parameter(CostantiControlStation.PARAMETRO_CONFIGURAZIONE_TRASFORMAZIONI_POSIZIONE, 
								CostantiControlStation.VALUE_PARAMETRO_CONFIGURAZIONE_POSIZIONE_SU);
						Parameter pDirezioneGiu = new Parameter(CostantiControlStation.PARAMETRO_CONFIGURAZIONE_TRASFORMAZIONI_POSIZIONE, 
								CostantiControlStation.VALUE_PARAMETRO_CONFIGURAZIONE_POSIZIONE_GIU);
								
						if(i > 0) {
							imageUp.setImage(CostantiControlStation.ICONA_FRECCIA_SU);
							imageUp.setToolTip(CostantiControlStation.LABEL_PARAMETRO_CONFIGURAZIONE_POSIZIONE_SPOSTA_SU);
							imageUp.setUrl(PorteApplicativeCostanti.SERVLET_NAME_PORTE_APPLICATIVE_TRASFORMAZIONI_LIST, pIdPorta, pIdSoggetto, pIdAsps,pIdTrasformazione, pDirezioneSu); 
						}
						else {
							imageUp.setImage(CostantiControlStation.ICONA_PLACEHOLDER);
						}
						de.addImage(imageUp);
						
						if(i < numeroElementi -1) {
							DataElementImage imageDown = new DataElementImage();
							imageDown.setImage(CostantiControlStation.ICONA_FRECCIA_GIU);
							imageDown.setToolTip(CostantiControlStation.LABEL_PARAMETRO_CONFIGURAZIONE_POSIZIONE_SPOSTA_GIU);
							imageDown.setUrl(PorteApplicativeCostanti.SERVLET_NAME_PORTE_APPLICATIVE_TRASFORMAZIONI_LIST, pIdPorta, pIdSoggetto, pIdAsps,pIdTrasformazione, pDirezioneGiu);
							de.addImage(imageDown);
						}
						de.setValue(regola.getPosizione()+"");
						e.addElement(de);
					}
					
					// Stato
					DataElement de = new DataElement();
					de.setWidthPx(10);
					de.setType(DataElementType.CHECKBOX);
					if(regola.getStato()==null // backward compatibility 
							||
							StatoFunzionalita.ABILITATO.equals(regola.getStato())){
						de.setToolTip(ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_STATO_ABILITATO);
						de.setValue(ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_STATO_ABILITATO);
						de.setSelected(CheckboxStatusType.CONFIG_ENABLE);
					}
					else{
						de.setToolTip(ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_STATO_DISABILITATO);
						de.setValue(ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_STATO_DISABILITATO);
						de.setSelected(CheckboxStatusType.CONFIG_DISABLE);
					}
					de.setUrl(PorteApplicativeCostanti.SERVLET_NAME_PORTE_APPLICATIVE_TRASFORMAZIONI_CHANGE, 
							pIdPorta,
							pIdSoggetto,
							pIdAsps,
							pIdTrasformazione
							);
					e.addElement(de);
					
					
					// Nome
					de = new DataElement();
					de.setIdToRemove(regola.getId() + "");
					de.setValue(regola.getNome());
					de.setToolTip(regola.getNome());
					de.setUrl(PorteApplicativeCostanti.SERVLET_NAME_PORTE_APPLICATIVE_TRASFORMAZIONI_CHANGE, 
							pIdPorta,
							pIdSoggetto,
							pIdAsps,
							pIdTrasformazione
							);
					e.addElement(de);
					
					
					// Azioni
					de = new DataElement();
					
					TrasformazioneRegolaApplicabilitaRichiesta applicabilita = regola.getApplicabilita();
					
					List<String> listaAzioni = applicabilita != null ? applicabilita.getAzioneList() : null;
					String nomiAzioni = "";
					if((listaAzioni != null && listaAzioni.size() > 0) && azioni.size()>0) {
						
						StringBuilder sb = new StringBuilder();
						Iterator<String> itAz = azioni.keySet().iterator();
						while (itAz.hasNext()) {
							String idAzione = (String) itAz.next();
							if(listaAzioni.contains(idAzione)) {
								if(sb.length() >0)
									sb.append(", ");
								
								sb.append(azioni.get(idAzione));
							}
						}
						nomiAzioni = sb.toString();
					}
					
					if(StringUtils.isEmpty(nomiAzioni))
						nomiAzioni = CostantiControlStation.LABEL_QUALSIASI;
					
					de.setValue(nomiAzioni);
					de.setToolTip(nomiAzioni);
					if(nomiAzioni!=null && nomiAzioni.length()>197) {
						de.setUrl(PorteApplicativeCostanti.SERVLET_NAME_PORTE_APPLICATIVE_TRASFORMAZIONI_CHANGE, 
								pIdPorta,
								pIdSoggetto,
								pIdAsps,
								pIdTrasformazione
								);
					}
					de.setSize(200);

					e.addElement(de);
					

					// Content-type
					
					String ct = "";
					List<String> contentTypeList = applicabilita != null ? applicabilita.getContentTypeList() : null;
					if(contentTypeList != null && contentTypeList.size() > 0) {
						StringBuilder sb = new StringBuilder();
						for (String string : contentTypeList) {
							if(sb.length() >0)
								sb.append(", ");
							
							sb.append(string);
						}
						ct =sb.toString();
					}
					
					if(StringUtils.isEmpty(ct))
						ct = "&nbsp;";
					
					
					de = new DataElement();
					de.setValue(ct);
					e.addElement(de);

					
					// Pattern
					de = new DataElement();
					String p = (applicabilita != null && applicabilita.getPattern() != null) ? applicabilita.getPattern() + "" : "&nbsp;";
					de.setValue(p.length()>CostantiControlStation.DEFAULT_VALUE_PARAMETRO_CONFIGURAZIONE_TRASFORMAZIONI_PATTERN_LIST_MAX_VALUE ? 
								p.substring(0, CostantiControlStation.DEFAULT_VALUE_PARAMETRO_CONFIGURAZIONE_TRASFORMAZIONI_PATTERN_LIST_MAX_VALUE)+"..." :
								p);
					e.addElement(de);
					
					
					// Connettori
					if(myPA!=null && myPA.getBehaviour()!=null && myPA.sizeServizioApplicativoList()>0) {
						
						de = new DataElement();
						
						List<String> listaConnettori = applicabilita != null ? applicabilita.getConnettoreList() : null;
						String nomiConnettori = "";
						if((listaConnettori != null && listaConnettori.size() > 0)) {
							StringBuilder sb = new StringBuilder();
							for (String sa : listaConnettori) {
								for (PortaApplicativaServizioApplicativo pasa : myPA.getServizioApplicativoList()) {
									if(pasa!=null && pasa.getNome()!=null && pasa.getNome().equals(sa)) {
										String nomeConnettoreMultiplo = pasa.getDatiConnettore()!= null ? pasa.getDatiConnettore().getNome() : null;
										if(nomeConnettoreMultiplo==null) {
											nomeConnettoreMultiplo=CostantiConfigurazione.NOME_CONNETTORE_DEFAULT;
										}
										if(sb.length() >0)
											sb.append(", ");
										sb.append(nomeConnettoreMultiplo);
										break;
									}
								}
							}
							nomiConnettori = sb.toString();
						}
						
						if(StringUtils.isEmpty(nomiConnettori))
							nomiConnettori = CostantiControlStation.LABEL_QUALSIASI;
						
						de.setValue(nomiConnettori);
						de.setToolTip(nomiConnettori);
						if(nomiConnettori!=null && nomiConnettori.length()>197) {
							de.setUrl(PorteApplicativeCostanti.SERVLET_NAME_PORTE_APPLICATIVE_TRASFORMAZIONI_CHANGE, 
									pIdPorta,
									pIdSoggetto,
									pIdAsps,
									pIdTrasformazione
									);
						}
						de.setSize(200);

						e.addElement(de);
						
					}
					
					
					if(autenticazione){
						
						String servletTrasformazioniAutorizzazioneAutenticati =   PorteApplicativeCostanti.SERVLET_NAME_PORTE_APPLICATIVE_TRASFORMAZIONI_SOGGETTO_LIST;
						List<Parameter> parametriInvocazioneServletTrasformazioniAutorizzazioneAutenticati = new ArrayList<Parameter>();
						parametriInvocazioneServletTrasformazioniAutorizzazioneAutenticati.add(pIdPorta);
						parametriInvocazioneServletTrasformazioniAutorizzazioneAutenticati.add(pIdSoggetto);
						parametriInvocazioneServletTrasformazioniAutorizzazioneAutenticati.add(pIdAsps);
						parametriInvocazioneServletTrasformazioniAutorizzazioneAutenticati.add(pIdTrasformazione);
						parametriInvocazioneServletTrasformazioniAutorizzazioneAutenticati.add(pFromList);
						int numAutenticati = applicabilita != null ? applicabilita.sizeSoggettoList() : 0;
						
						de = new DataElement();
						de.setUrl(servletTrasformazioniAutorizzazioneAutenticati, parametriInvocazioneServletTrasformazioniAutorizzazioneAutenticati.toArray(new Parameter[parametriInvocazioneServletTrasformazioniAutorizzazioneAutenticati.size()]));
						if (contaListe) {
							ServletUtils.setDataElementCustomLabel(de,PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_SOGGETTI,Long.valueOf(numAutenticati));
						} else {
							ServletUtils.setDataElementCustomLabel(de,PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_SOGGETTI);
						}
						e.addElement(de);
					}
					
					if(isSupportatoAutenticazione && autenticazione) {
						
						String servletTrasformazioniApplicativiAutenticati = PorteApplicativeCostanti.SERVLET_NAME_PORTE_APPLICATIVE_TRASFORMAZIONI_SERVIZIO_APPLICATIVO_AUTORIZZATO_LIST;
						List<Parameter> parametriInvocazioneServletTrasformazioniApplicativiAutenticati  = new ArrayList<Parameter>();
						parametriInvocazioneServletTrasformazioniApplicativiAutenticati.add(pIdPorta);
						parametriInvocazioneServletTrasformazioniApplicativiAutenticati.add(pIdSoggetto);
						parametriInvocazioneServletTrasformazioniApplicativiAutenticati.add(pIdAsps);
						parametriInvocazioneServletTrasformazioniApplicativiAutenticati.add(pIdTrasformazione);
						parametriInvocazioneServletTrasformazioniApplicativiAutenticati.add(pFromList);
						int numApplicativiAutenticati = applicabilita != null ? applicabilita.sizeServizioApplicativoList() : 0;
						
						de = new DataElement();
						de.setUrl(servletTrasformazioniApplicativiAutenticati, parametriInvocazioneServletTrasformazioniApplicativiAutenticati.toArray(new Parameter[parametriInvocazioneServletTrasformazioniApplicativiAutenticati.size()]));
						String labelApplicativi = PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_SERVIZI_APPLICATIVI; // uso cmq label PD
						if(!this.isModalitaCompleta()) {
							labelApplicativi = PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_APPLICATIVI;// uso cmq label PD
						}
						if (contaListe) {
							ServletUtils.setDataElementCustomLabel(de,labelApplicativi,Long.valueOf(numApplicativiAutenticati));
						} else {
							ServletUtils.setDataElementCustomLabel(de,labelApplicativi);
						}
						e.addElement(de);
					}
					
					dati.addElement(e);
					i++;
				}
			}

			this.pd.setDati(dati);
			this.pd.setAddButton(true);

		} catch (Exception e) {
			this.log.error("Exception: " + e.getMessage(), e);
			throw new Exception(e);
		}
	}
	
	public void preparePorteAppTrasformazioniRispostaList(String nomePorta,  long idTrasformazione, ISearch ricerca, List<TrasformazioneRegolaRisposta> lista) throws Exception {
		try {
			// prelevo il flag che mi dice da quale pagina ho acceduto la sezione delle porte delegate
			Integer parentPA = ServletUtils.getIntegerAttributeFromSession(PorteApplicativeCostanti.ATTRIBUTO_PORTE_APPLICATIVE_PARENT, this.session);
			String idAsps = this.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_ASPS);
			if(idAsps == null)
				idAsps = "";

			String idPorta = this.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID);
			String idsogg = this.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_SOGGETTO);
			
			Parameter pIdTrasformazione = new Parameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_TRASFORMAZIONE, idTrasformazione+"");
			Parameter pIdPorta = new Parameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID, idPorta);
			Parameter pIdSoggetto = new Parameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_SOGGETTO, idsogg);
			Parameter pIdAsps = new Parameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_ASPS, idAsps);
			ServletUtils.addListElementIntoSession(this.session,  PorteApplicativeCostanti.OBJECT_NAME_PORTE_APPLICATIVE_TRASFORMAZIONI_RISPOSTA,
					pIdPorta, pIdSoggetto, pIdAsps, pIdTrasformazione);

			int idLista = Liste.PORTE_APPLICATIVE_TRASFORMAZIONI_RISPOSTE;
			int limit = ricerca.getPageSize(idLista);
			int offset = ricerca.getIndexIniziale(idLista);

			this.pd.setIndex(offset);
			this.pd.setPageSize(limit);
			this.pd.setSearch("");
			this.pd.setNumEntries(ricerca.getNumEntries(idLista));
			this.pd.setSearchDescription("");
			
			ServletUtils.disabledPageDataSearch(this.pd);

			PortaApplicativa myPA = this.porteApplicativeCore.getPortaApplicativa(Integer.parseInt(idPorta));
			String idporta = myPA.getNome();
			
			Trasformazioni trasformazioni = myPA.getTrasformazioni();
			TrasformazioneRegola oldRegola = null;
			for (TrasformazioneRegola reg : trasformazioni.getRegolaList()) {
				if(reg.getId().longValue() == idTrasformazione) {
					oldRegola = reg;
					break;
				}
			}
			
			String nomeTrasformazione = oldRegola.getNome();
			
			// setto la barra del titolo
			List<Parameter> lstParam = this.getTitoloPA(parentPA, idsogg, idAsps);

			String labelPerPorta = null;
			if(parentPA!=null && (parentPA.intValue() == PorteApplicativeCostanti.ATTRIBUTO_PORTE_APPLICATIVE_PARENT_CONFIGURAZIONE)) {
				labelPerPorta = this.porteApplicativeCore.getLabelRegolaMappingErogazionePortaApplicativa(
						PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_TRASFORMAZIONI_DI,
						PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_TRASFORMAZIONI,
						myPA);
			}
			else {
				labelPerPorta = PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_TRASFORMAZIONI_DI+idporta;
			}
			
			lstParam.add(new Parameter(labelPerPorta, PorteApplicativeCostanti.SERVLET_NAME_PORTE_APPLICATIVE_TRASFORMAZIONI_LIST, 
					pIdPorta,
					pIdSoggetto,
					pIdAsps));
			
			
			lstParam.add(new Parameter(nomeTrasformazione, PorteApplicativeCostanti.SERVLET_NAME_PORTE_APPLICATIVE_TRASFORMAZIONI_CHANGE, 
					pIdPorta,
					pIdSoggetto,
					pIdAsps, pIdTrasformazione));
			
			String labelPag = PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_TRASFORMAZIONI_RISPOSTE;
			
			lstParam.add(new Parameter(labelPag,null));

			// setto la barra del titolo
			ServletUtils.setPageDataTitle(this.pd, lstParam.toArray(new Parameter[lstParam.size()]));

			// setto le label delle colonne
			List<String> lstLabels = new ArrayList<>();
			if(lista != null && lista.size() > 1)
				lstLabels.add(PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_TRASFORMAZIONI_POSIZIONE);
			lstLabels.add(PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_TRASFORMAZIONI_NOME);
			lstLabels.add(PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_TRASFORMAZIONI_RISPOSTA_APPLICABILITA_STATUS);
			lstLabels.add(PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_TRASFORMAZIONI_RISPOSTA_APPLICABILITA_CT);
			lstLabels.add(PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_TRASFORMAZIONI_RISPOSTA_APPLICABILITA_PATTERN);
			
			this.pd.setLabels(lstLabels.toArray(new String [lstLabels.size()]));

			// preparo i dati
			Vector<Vector<DataElement>> dati = new Vector<Vector<DataElement>>();

			if (lista != null) {
				Iterator<TrasformazioneRegolaRisposta> it = lista.iterator();
				int numeroElementi = lista.size();
				int i = 0;
				while (it.hasNext()) {
					TrasformazioneRegolaRisposta risposta = it.next();
					Parameter pIdTrasformazioneRisposta = new Parameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_TRASFORMAZIONE_RISPOSTA, risposta.getId() + "");
					
					Vector<DataElement> e = new Vector<DataElement>();
					
					// Posizione
					if(lista.size() > 1) {
						DataElement de = new DataElement();
						de.setWidthPx(48);
						de.setType(DataElementType.IMAGE);
						DataElementImage imageUp = new DataElementImage();
						Parameter pDirezioneSu = new Parameter(CostantiControlStation.PARAMETRO_CONFIGURAZIONE_TRASFORMAZIONI_POSIZIONE, 
								CostantiControlStation.VALUE_PARAMETRO_CONFIGURAZIONE_POSIZIONE_SU);
						Parameter pDirezioneGiu = new Parameter(CostantiControlStation.PARAMETRO_CONFIGURAZIONE_TRASFORMAZIONI_POSIZIONE, 
								CostantiControlStation.VALUE_PARAMETRO_CONFIGURAZIONE_POSIZIONE_GIU);
								
						if(i > 0) {
							imageUp.setImage(CostantiControlStation.ICONA_FRECCIA_SU);
							imageUp.setToolTip(CostantiControlStation.LABEL_PARAMETRO_CONFIGURAZIONE_POSIZIONE_SPOSTA_SU);
							imageUp.setUrl(PorteApplicativeCostanti.SERVLET_NAME_PORTE_APPLICATIVE_TRASFORMAZIONI_RISPOSTA_LIST, pIdPorta, pIdSoggetto, pIdAsps,pIdTrasformazione, pIdTrasformazioneRisposta, pDirezioneSu); 
						}
						else {
							imageUp.setImage(CostantiControlStation.ICONA_PLACEHOLDER);
						}
						de.addImage(imageUp);
						
						if(i < numeroElementi -1) {
							DataElementImage imageDown = new DataElementImage();
							imageDown.setImage(CostantiControlStation.ICONA_FRECCIA_GIU);
							imageDown.setToolTip(CostantiControlStation.LABEL_PARAMETRO_CONFIGURAZIONE_POSIZIONE_SPOSTA_GIU);
							imageDown.setUrl(PorteApplicativeCostanti.SERVLET_NAME_PORTE_APPLICATIVE_TRASFORMAZIONI_RISPOSTA_LIST, pIdPorta, pIdSoggetto, pIdAsps,pIdTrasformazione, pIdTrasformazioneRisposta, pDirezioneGiu);
							de.addImage(imageDown);
						}
						de.setValue(risposta.getPosizione()+"");
						e.addElement(de);
					}
					// Nome
					DataElement de = new DataElement();
					de.setIdToRemove(risposta.getId() + "");
					de.setValue(risposta.getNome());
					de.setToolTip(risposta.getNome());
					
					de.setUrl(PorteApplicativeCostanti.SERVLET_NAME_PORTE_APPLICATIVE_TRASFORMAZIONI_RISPOSTA_CHANGE, 
							pIdPorta,
							pIdSoggetto,
							pIdAsps,
							pIdTrasformazione,
							pIdTrasformazioneRisposta
							);
					e.addElement(de);
					
					
					
					// Status Code
					de = new DataElement();
										
					TrasformazioneRegolaApplicabilitaRisposta applicabilita = risposta.getApplicabilita();
					
					Integer statusMin = applicabilita != null ? applicabilita.getReturnCodeMin() : null;
					Integer statusMax = applicabilita != null ? applicabilita.getReturnCodeMax() : null;
					
					// se e' stato salvato il valore 0 lo tratto come null
					if(statusMin != null && statusMin.intValue() <= 0) {
						statusMin = null;
					}
					
					if(statusMax != null && statusMax.intValue() <= 0) {
						statusMax = null;
					}
					
					String statusValue = null;
					// Intervallo
					if(statusMin != null && statusMax != null) {
						if(statusMax.longValue() == statusMin.longValue()) // esatto
							statusValue = statusMin + "";
						else 
							statusValue = "[" + statusMin + " - " + statusMax + "]";
					} else if(statusMin != null && statusMax == null) { // definito solo l'estremo inferiore
						statusValue = "&gt;" + statusMin;
					} else if(statusMin == null && statusMax != null) { // definito solo l'estremo superiore
						statusValue = "&lt;" + statusMax;
					} else { //entrambi null 
						statusValue = CostantiControlStation.LABEL_PARAMETRO_CONFIGURAZIONE_RETURN_CODE_QUALSIASI;
					}
					
					de.setValue(statusValue);
					
					e.addElement(de);
					
					
					
					// Content-type
					String ct = "";
					List<String> contentTypeList = applicabilita != null ? applicabilita.getContentTypeList() : null;
					if(contentTypeList != null && contentTypeList.size() > 0) {
						StringBuilder sb = new StringBuilder();
						for (String string : contentTypeList) {
							if(sb.length() >0)
								sb.append(", ");
							
							sb.append(string);
						}
						ct =sb.toString();
					}
					
					if(StringUtils.isEmpty(ct))
						ct = "&nbsp;";
					
					
					de = new DataElement();
					de.setValue(ct);
					e.addElement(de);

					
					// Pattern
					de = new DataElement();
					String p = (applicabilita != null && applicabilita.getPattern() != null) ? applicabilita.getPattern() + "" : "&nbsp;";
					de.setValue(p.length()>CostantiControlStation.DEFAULT_VALUE_PARAMETRO_CONFIGURAZIONE_TRASFORMAZIONI_PATTERN_LIST_MAX_VALUE ? 
								p.substring(0, CostantiControlStation.DEFAULT_VALUE_PARAMETRO_CONFIGURAZIONE_TRASFORMAZIONI_PATTERN_LIST_MAX_VALUE)+"..." :
								p);
					e.addElement(de);
					
					
					
					dati.addElement(e);
					i++;
				}
			}

			this.pd.setDati(dati);
			this.pd.setAddButton(true);

		} catch (Exception e) {
			this.log.error("Exception: " + e.getMessage(), e);
			throw new Exception(e);
		}
	}
	
	public void preparePorteAppTrasformazioniRispostaHeaderList(String nomePorta,  long idTrasformazione, long idTrasformazioneRisposta,  ISearch ricerca, List<TrasformazioneRegolaParametro> lista) throws Exception {
		try {
			// prelevo il flag che mi dice da quale pagina ho acceduto la sezione delle porte delegate
			Integer parentPA = ServletUtils.getIntegerAttributeFromSession(PorteApplicativeCostanti.ATTRIBUTO_PORTE_APPLICATIVE_PARENT, this.session);
			String idAsps = this.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_ASPS);
			if(idAsps == null)
				idAsps = "";

			String idPorta = this.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID);
			String idsogg = this.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_SOGGETTO);
			
			Parameter pIdTrasformazione = new Parameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_TRASFORMAZIONE, idTrasformazione+"");
			Parameter pIdTrasformazioneRisposta = new Parameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_TRASFORMAZIONE_RISPOSTA, idTrasformazioneRisposta + "");
			ServletUtils.addListElementIntoSession(this.session,  PorteApplicativeCostanti.OBJECT_NAME_PORTE_APPLICATIVE_TRASFORMAZIONI_RISPOSTA_HEADER,
					new Parameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID, idPorta),
					new Parameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_SOGGETTO, idsogg),
					new Parameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_ASPS, idAsps),
					pIdTrasformazione, pIdTrasformazioneRisposta);

			int idLista = Liste.PORTE_APPLICATIVE_TRASFORMAZIONI_RISPOSTE_HEADER;
			int limit = ricerca.getPageSize(idLista);
			int offset = ricerca.getIndexIniziale(idLista);

			this.pd.setIndex(offset);
			this.pd.setPageSize(limit);
			this.pd.setSearch("");
			this.pd.setNumEntries(ricerca.getNumEntries(idLista));
			this.pd.setSearchDescription("");
			
			ServletUtils.disabledPageDataSearch(this.pd);

			PortaApplicativa myPA = this.porteApplicativeCore.getPortaApplicativa(Integer.parseInt(idPorta));
			String idporta = myPA.getNome();
			
			Trasformazioni trasformazioni = myPA.getTrasformazioni();
			TrasformazioneRegola oldRegola = null;
			TrasformazioneRegolaRisposta oldRisposta = null;
			for (TrasformazioneRegola reg : trasformazioni.getRegolaList()) {
				if(reg.getId().longValue() == idTrasformazione) {
					oldRegola = reg;
					break;
				}
			}
			
			for (int j = 0; j < oldRegola.sizeRispostaList(); j++) {
				TrasformazioneRegolaRisposta risposta = oldRegola.getRisposta(j);
				if (risposta.getId().longValue() == idTrasformazioneRisposta) {
					oldRisposta = risposta;
					break;
				}
			}
			
			String nomeRisposta = oldRisposta.getNome();
			String nomeTrasformazione = oldRegola.getNome();
			
			// setto la barra del titolo
			List<Parameter> lstParam = this.getTitoloPA(parentPA, idsogg, idAsps);

			String labelPerPorta = null;
			if(parentPA!=null && (parentPA.intValue() == PorteApplicativeCostanti.ATTRIBUTO_PORTE_APPLICATIVE_PARENT_CONFIGURAZIONE)) {
				labelPerPorta = this.porteApplicativeCore.getLabelRegolaMappingErogazionePortaApplicativa(
						PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_TRASFORMAZIONI_DI,
						PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_TRASFORMAZIONI,
						myPA);
			}
			else {
				labelPerPorta = PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_TRASFORMAZIONI_DI+idporta;
			}
			
			lstParam.add(new Parameter(labelPerPorta, PorteApplicativeCostanti.SERVLET_NAME_PORTE_APPLICATIVE_TRASFORMAZIONI_LIST, 
					new Parameter( PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID, idPorta),
					new Parameter( PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_SOGGETTO, idsogg),
					new Parameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_ASPS, idAsps)));
			
			
			lstParam.add(new Parameter(nomeTrasformazione, PorteApplicativeCostanti.SERVLET_NAME_PORTE_APPLICATIVE_TRASFORMAZIONI_CHANGE, 
					new Parameter( PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID, idPorta),
					new Parameter( PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_SOGGETTO, idsogg),
					new Parameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_ASPS, idAsps), pIdTrasformazione));
			
			List<Parameter> parametriInvocazioneServletTrasformazioniRisposta = new ArrayList<Parameter>();
			parametriInvocazioneServletTrasformazioniRisposta.add(new Parameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID, idPorta));
			parametriInvocazioneServletTrasformazioniRisposta.add(new Parameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_SOGGETTO, idsogg));
			parametriInvocazioneServletTrasformazioniRisposta.add(new Parameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_ASPS, idAsps));
			parametriInvocazioneServletTrasformazioniRisposta.add(pIdTrasformazione);
			
			lstParam.add(new Parameter(PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_TRASFORMAZIONI_RISPOSTE,PorteApplicativeCostanti.SERVLET_NAME_PORTE_APPLICATIVE_TRASFORMAZIONI_RISPOSTA_LIST,parametriInvocazioneServletTrasformazioniRisposta));
			
			lstParam.add(new Parameter(nomeRisposta, PorteApplicativeCostanti.SERVLET_NAME_PORTE_APPLICATIVE_TRASFORMAZIONI_RISPOSTA_CHANGE, 
					new Parameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID, idPorta),
					new Parameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_SOGGETTO, idsogg),
					new Parameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_ASPS, idAsps),
					pIdTrasformazione,pIdTrasformazioneRisposta));
			
			String labelPag = PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_TRASFORMAZIONI_RISPOSTA_HEADERS;
			
			lstParam.add(new Parameter(labelPag,null));

			// setto la barra del titolo
			ServletUtils.setPageDataTitle(this.pd, lstParam.toArray(new Parameter[lstParam.size()]));

			// setto le label delle colonne
			String[] labels = { PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_TRASFORMAZIONI_RISPOSTA_HEADER_NOME,
					PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_TRASFORMAZIONI_RISPOSTA_HEADER_TIPO,
					PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_TRASFORMAZIONI_RISPOSTA_HEADER_VALORE};
			this.pd.setLabels(labels);

			// preparo i dati
			Vector<Vector<DataElement>> dati = new Vector<Vector<DataElement>>();

			if (lista != null) {
				Iterator<TrasformazioneRegolaParametro> it = lista.iterator();
				while (it.hasNext()) {
					TrasformazioneRegolaParametro parametro = it.next();

					Vector<DataElement> e = new Vector<DataElement>();
					
					// Nome
					DataElement de = new DataElement();
					de.setIdToRemove(parametro.getId() + "");
					de.setValue(parametro.getNome());
					
					de.setUrl(PorteApplicativeCostanti.SERVLET_NAME_PORTE_APPLICATIVE_TRASFORMAZIONI_RISPOSTA_HEADER_CHANGE, 
							new Parameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID, idPorta),
							new Parameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_SOGGETTO, idsogg),
							new Parameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_ASPS, idAsps),
							pIdTrasformazione,
							pIdTrasformazioneRisposta,
							new Parameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_TRASFORMAZIONE_RISPOSTA_HEADER, parametro.getId() + "")
							);
					e.addElement(de);
					
					// Tipo
					de = new DataElement();
					de.setValue(parametro.getConversioneTipo().getValue());
					e.addElement(de);
					
					// Valore
					de = new DataElement();
					de.setValue(StringUtils.isNotEmpty(parametro.getValore()) ? this.formatInfoForView(parametro.getValore()) :  "&nbsp;" );
					if(StringUtils.isNotEmpty(parametro.getValore())) {
						de.setToolTip(parametro.getValore());
					}
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
	
	public void preparePorteAppTrasformazioniRichiestaHeaderList(String nomePorta,  long idTrasformazione, ISearch ricerca, List<TrasformazioneRegolaParametro> lista) throws Exception {
		try {
			// prelevo il flag che mi dice da quale pagina ho acceduto la sezione delle porte delegate
			Integer parentPA = ServletUtils.getIntegerAttributeFromSession(PorteApplicativeCostanti.ATTRIBUTO_PORTE_APPLICATIVE_PARENT, this.session);
			String idAsps = this.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_ASPS);
			if(idAsps == null)
				idAsps = "";

			String idPorta = this.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID);
			String idsogg = this.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_SOGGETTO);
			
			Parameter pIdTrasformazione = new Parameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_TRASFORMAZIONE, idTrasformazione+"");
			ServletUtils.addListElementIntoSession(this.session,  PorteApplicativeCostanti.OBJECT_NAME_PORTE_APPLICATIVE_TRASFORMAZIONI_RICHIESTA_HEADER,
					new Parameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID, idPorta),
					new Parameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_SOGGETTO, idsogg),
					new Parameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_ASPS, idAsps),
					pIdTrasformazione);

			int idLista = Liste.PORTE_APPLICATIVE_TRASFORMAZIONI_RICHIESTA_HEADER;
			int limit = ricerca.getPageSize(idLista);
			int offset = ricerca.getIndexIniziale(idLista);

			this.pd.setIndex(offset);
			this.pd.setPageSize(limit);
			this.pd.setSearch("");
			this.pd.setNumEntries(ricerca.getNumEntries(idLista));
			this.pd.setSearchDescription("");
			
			ServletUtils.disabledPageDataSearch(this.pd);

			PortaApplicativa myPA = this.porteApplicativeCore.getPortaApplicativa(Integer.parseInt(idPorta));
			String idporta = myPA.getNome();
			
			Trasformazioni trasformazioni = myPA.getTrasformazioni();
			TrasformazioneRegola oldRegola = null;
			for (TrasformazioneRegola reg : trasformazioni.getRegolaList()) {
				if(reg.getId().longValue() == idTrasformazione) {
					oldRegola = reg;
					break;
				}
			}
			
			String nomeTrasformazione = oldRegola.getNome();
			
			// setto la barra del titolo
			List<Parameter> lstParam = this.getTitoloPA(parentPA, idsogg, idAsps);

			String labelPerPorta = null;
			if(parentPA!=null && (parentPA.intValue() == PorteApplicativeCostanti.ATTRIBUTO_PORTE_APPLICATIVE_PARENT_CONFIGURAZIONE)) {
				labelPerPorta = this.porteApplicativeCore.getLabelRegolaMappingErogazionePortaApplicativa(
						PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_TRASFORMAZIONI_DI,
						PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_TRASFORMAZIONI,
						myPA);
			}
			else {
				labelPerPorta = PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_TRASFORMAZIONI_DI+idporta;
			}
			
			lstParam.add(new Parameter(labelPerPorta, PorteApplicativeCostanti.SERVLET_NAME_PORTE_APPLICATIVE_TRASFORMAZIONI_LIST, 
					new Parameter( PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID, idPorta),
					new Parameter( PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_SOGGETTO, idsogg),
					new Parameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_ASPS, idAsps)));
			
			
			lstParam.add(new Parameter(nomeTrasformazione, PorteApplicativeCostanti.SERVLET_NAME_PORTE_APPLICATIVE_TRASFORMAZIONI_CHANGE, 
					new Parameter( PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID, idPorta),
					new Parameter( PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_SOGGETTO, idsogg),
					new Parameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_ASPS, idAsps), pIdTrasformazione));
			
			List<Parameter> parametriInvocazioneServletTrasformazioniRichiesta = new ArrayList<Parameter>();
			parametriInvocazioneServletTrasformazioniRichiesta.add(new Parameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID, idPorta));
			parametriInvocazioneServletTrasformazioniRichiesta.add(new Parameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_SOGGETTO, idsogg));
			parametriInvocazioneServletTrasformazioniRichiesta.add(new Parameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_ASPS, idAsps));
			parametriInvocazioneServletTrasformazioniRichiesta.add(pIdTrasformazione);
			
			lstParam.add(new Parameter(PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_TRASFORMAZIONI_RICHIESTA,
					PorteApplicativeCostanti.SERVLET_NAME_PORTE_APPLICATIVE_TRASFORMAZIONI_RICHIESTA,parametriInvocazioneServletTrasformazioniRichiesta));
			
			String labelPag = PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_TRASFORMAZIONI_RICHIESTA_HEADERS;
			
			lstParam.add(new Parameter(labelPag,null));

			// setto la barra del titolo
			ServletUtils.setPageDataTitle(this.pd, lstParam.toArray(new Parameter[lstParam.size()]));

			// setto le label delle colonne
			String[] labels = { PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_TRASFORMAZIONI_RICHIESTA_HEADER_NOME,
					PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_TRASFORMAZIONI_RICHIESTA_HEADER_TIPO,
					PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_TRASFORMAZIONI_RICHIESTA_HEADER_VALORE};
			this.pd.setLabels(labels);

			// preparo i dati
			Vector<Vector<DataElement>> dati = new Vector<Vector<DataElement>>();

			if (lista != null) {
				Iterator<TrasformazioneRegolaParametro> it = lista.iterator();
				while (it.hasNext()) {
					TrasformazioneRegolaParametro parametro = it.next();

					Vector<DataElement> e = new Vector<DataElement>();
					
					// Nome
					DataElement de = new DataElement();
					de.setIdToRemove(parametro.getId() + "");
					de.setValue(parametro.getNome());
					
					de.setUrl(PorteApplicativeCostanti.SERVLET_NAME_PORTE_APPLICATIVE_TRASFORMAZIONI_RICHIESTA_HEADER_CHANGE, 
							new Parameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID, idPorta),
							new Parameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_SOGGETTO, idsogg),
							new Parameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_ASPS, idAsps),
							pIdTrasformazione,
							new Parameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_TRASFORMAZIONE_RICHIESTA_HEADER, parametro.getId() + "")
							);
					e.addElement(de);
					
					// Tipo
					de = new DataElement();
					de.setValue(parametro.getConversioneTipo().getValue());
					e.addElement(de);
					
					// Valore
					de = new DataElement();
					de.setValue(StringUtils.isNotEmpty(parametro.getValore()) ? this.formatInfoForView(parametro.getValore()) :  "&nbsp;" );
					if(StringUtils.isNotEmpty(parametro.getValore())) {
						de.setToolTip(parametro.getValore());
					}
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
	
	public void preparePorteAppTrasformazioniRichiestaUrlParameterList(String nomePorta,  long idTrasformazione, ISearch ricerca, List<TrasformazioneRegolaParametro> lista) throws Exception {
		try {
			// prelevo il flag che mi dice da quale pagina ho acceduto la sezione delle porte delegate
			Integer parentPA = ServletUtils.getIntegerAttributeFromSession(PorteApplicativeCostanti.ATTRIBUTO_PORTE_APPLICATIVE_PARENT, this.session);
			String idAsps = this.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_ASPS);
			if(idAsps == null)
				idAsps = "";

			String idPorta = this.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID);
			String idsogg = this.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_SOGGETTO);
			
			Parameter pIdTrasformazione = new Parameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_TRASFORMAZIONE, idTrasformazione+"");
			ServletUtils.addListElementIntoSession(this.session,  PorteApplicativeCostanti.OBJECT_NAME_PORTE_APPLICATIVE_TRASFORMAZIONI_RICHIESTA_PARAMETRO,
					new Parameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID, idPorta),
					new Parameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_SOGGETTO, idsogg),
					new Parameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_ASPS, idAsps),
					pIdTrasformazione);

			int idLista = Liste.PORTE_APPLICATIVE_TRASFORMAZIONI_RICHIESTA_PARAMETRI;
			int limit = ricerca.getPageSize(idLista);
			int offset = ricerca.getIndexIniziale(idLista);

			this.pd.setIndex(offset);
			this.pd.setPageSize(limit);
			this.pd.setSearch("");
			this.pd.setNumEntries(ricerca.getNumEntries(idLista));
			this.pd.setSearchDescription("");
			
			ServletUtils.disabledPageDataSearch(this.pd);

			PortaApplicativa myPA = this.porteApplicativeCore.getPortaApplicativa(Integer.parseInt(idPorta));
			String idporta = myPA.getNome();
			
			Trasformazioni trasformazioni = myPA.getTrasformazioni();
			TrasformazioneRegola oldRegola = null;
			for (TrasformazioneRegola reg : trasformazioni.getRegolaList()) {
				if(reg.getId().longValue() == idTrasformazione) {
					oldRegola = reg;
					break;
				}
			}
			
			String nomeTrasformazione = oldRegola.getNome();
			
			// setto la barra del titolo
			List<Parameter> lstParam = this.getTitoloPA(parentPA, idsogg, idAsps);

			String labelPerPorta = null;
			if(parentPA!=null && (parentPA.intValue() == PorteApplicativeCostanti.ATTRIBUTO_PORTE_APPLICATIVE_PARENT_CONFIGURAZIONE)) {
				labelPerPorta = this.porteApplicativeCore.getLabelRegolaMappingErogazionePortaApplicativa(
						PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_TRASFORMAZIONI_DI,
						PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_TRASFORMAZIONI,
						myPA);
			}
			else {
				labelPerPorta = PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_TRASFORMAZIONI_DI+idporta;
			}
			
			lstParam.add(new Parameter(labelPerPorta, PorteApplicativeCostanti.SERVLET_NAME_PORTE_APPLICATIVE_TRASFORMAZIONI_LIST, 
					new Parameter( PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID, idPorta),
					new Parameter( PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_SOGGETTO, idsogg),
					new Parameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_ASPS, idAsps)));
			
			
			lstParam.add(new Parameter(nomeTrasformazione, PorteApplicativeCostanti.SERVLET_NAME_PORTE_APPLICATIVE_TRASFORMAZIONI_CHANGE, 
					new Parameter( PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID, idPorta),
					new Parameter( PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_SOGGETTO, idsogg),
					new Parameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_ASPS, idAsps), pIdTrasformazione));
			
			List<Parameter> parametriInvocazioneServletTrasformazioniRichiesta = new ArrayList<Parameter>();
			parametriInvocazioneServletTrasformazioniRichiesta.add(new Parameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID, idPorta));
			parametriInvocazioneServletTrasformazioniRichiesta.add(new Parameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_SOGGETTO, idsogg));
			parametriInvocazioneServletTrasformazioniRichiesta.add(new Parameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_ASPS, idAsps));
			parametriInvocazioneServletTrasformazioniRichiesta.add(pIdTrasformazione);
			
			lstParam.add(new Parameter(PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_TRASFORMAZIONI_RICHIESTA,
					PorteApplicativeCostanti.SERVLET_NAME_PORTE_APPLICATIVE_TRASFORMAZIONI_RICHIESTA,parametriInvocazioneServletTrasformazioniRichiesta));
			
			String labelPag = PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_TRASFORMAZIONI_RICHIESTA_PARAMETRI;
			
			lstParam.add(new Parameter(labelPag,null));

			// setto la barra del titolo
			ServletUtils.setPageDataTitle(this.pd, lstParam.toArray(new Parameter[lstParam.size()]));

			// setto le label delle colonne
			String[] labels = { PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_TRASFORMAZIONI_RICHIESTA_PARAMETRO_NOME,
					PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_TRASFORMAZIONI_RICHIESTA_PARAMETRO_TIPO,
					PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_TRASFORMAZIONI_RICHIESTA_PARAMETRO_VALORE};
			this.pd.setLabels(labels);

			// preparo i dati
			Vector<Vector<DataElement>> dati = new Vector<Vector<DataElement>>();

			if (lista != null) {
				Iterator<TrasformazioneRegolaParametro> it = lista.iterator();
				while (it.hasNext()) {
					TrasformazioneRegolaParametro parametro = it.next();

					Vector<DataElement> e = new Vector<DataElement>();
					
					// Nome
					DataElement de = new DataElement();
					de.setIdToRemove(parametro.getId() + "");
					de.setValue(parametro.getNome());
					
					de.setUrl(PorteApplicativeCostanti.SERVLET_NAME_PORTE_APPLICATIVE_TRASFORMAZIONI_RICHIESTA_PARAMETRO_CHANGE, 
							new Parameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID, idPorta),
							new Parameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_SOGGETTO, idsogg),
							new Parameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_ASPS, idAsps),
							pIdTrasformazione,
							new Parameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_TRASFORMAZIONE_RICHIESTA_PARAMETRO, parametro.getId() + "")
							);
					e.addElement(de);
					
					// Tipo
					de = new DataElement();
					de.setValue(parametro.getConversioneTipo().getValue());
					e.addElement(de);
					
					// Valore
					de = new DataElement();
					de.setValue(StringUtils.isNotEmpty(parametro.getValore()) ? this.formatInfoForView(parametro.getValore()) :  "&nbsp;" );
					if(StringUtils.isNotEmpty(parametro.getValore())) {
						de.setToolTip(parametro.getValore());
					}
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
	
	
	public void preparePorteAppTrasformazioniServizioApplicativoAutorizzatoList(String nomePorta, long idTrasformazione, ISearch ricerca, List<TrasformazioneRegolaApplicabilitaServizioApplicativo> lista) throws Exception {
		try {
			// prelevo il flag che mi dice da quale pagina ho acceduto la sezione delle porte delegate
			Integer parentPA = ServletUtils.getIntegerAttributeFromSession(PorteApplicativeCostanti.ATTRIBUTO_PORTE_APPLICATIVE_PARENT, this.session);
			String idAsps = this.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_ASPS);
			if(idAsps == null)
				idAsps = "";
	
			String idPorta = this.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID);
			String idsogg = this.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_SOGGETTO);
			
			String listaTmp = this.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_TRASFORMAZIONI_APPLICABILITA_LIST);
			boolean fromList = false;
			if(listaTmp != null && !"".equals(listaTmp))
				fromList = true;
			
			Parameter pIdTrasformazione = new Parameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_TRASFORMAZIONE, idTrasformazione+"");
			Parameter pIdPorta = new Parameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID, idPorta);
			Parameter pIdSoggetto = new Parameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_SOGGETTO, idsogg);
			Parameter pIdAsps = new Parameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_ASPS, idAsps);
	
			List<Parameter> parameters = new ArrayList<>();
			parameters.add(pIdPorta);
			parameters.add(pIdSoggetto);
			parameters.add(pIdAsps);
			parameters.add(pIdTrasformazione);
			if(fromList) {
				parameters.add(new Parameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_TRASFORMAZIONI_APPLICABILITA_LIST, listaTmp));
			}
			
			ServletUtils.addListElementIntoSession(this.session,  PorteApplicativeCostanti.OBJECT_NAME_PORTE_APPLICATIVE_TRASFORMAZIONI_SERVIZIO_APPLICATIVO_AUTORIZZATO,
					parameters);
	
			int idLista = Liste.PORTE_APPLICATIVE_TRASFORMAZIONI_SERVIZIO_APPLICATIVO_AUTORIZZATO;
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
			
			Trasformazioni trasformazioni = myPA.getTrasformazioni();
			TrasformazioneRegola oldRegola = null;
			for (TrasformazioneRegola reg : trasformazioni.getRegolaList()) {
				if(reg.getId().longValue() == idTrasformazione) {
					oldRegola = reg;
					break;
				}
			}
	
			String nomeTrasformazione = oldRegola.getNome();
			
			// setto la barra del titolo
			List<Parameter> lstParam = this.getTitoloPA(parentPA, idsogg, idAsps);
	
			String labelPerPorta = null;
			if(parentPA!=null && (parentPA.intValue() == PorteApplicativeCostanti.ATTRIBUTO_PORTE_APPLICATIVE_PARENT_CONFIGURAZIONE)) {
				labelPerPorta = this.porteApplicativeCore.getLabelRegolaMappingErogazionePortaApplicativa(
						PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_TRASFORMAZIONI_DI,
						PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_TRASFORMAZIONI,
						myPA);
			}
			else {
				labelPerPorta = PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_TRASFORMAZIONI_DI+idporta;
			}
			
			lstParam.add(new Parameter(labelPerPorta, PorteApplicativeCostanti.SERVLET_NAME_PORTE_APPLICATIVE_TRASFORMAZIONI_LIST, 
					pIdPorta,
					pIdSoggetto,
					pIdAsps));
			
			if(!fromList) {
				lstParam.add(new Parameter(nomeTrasformazione, PorteApplicativeCostanti.SERVLET_NAME_PORTE_APPLICATIVE_TRASFORMAZIONI_CHANGE, 
						pIdPorta,
						pIdSoggetto,
						pIdAsps, pIdTrasformazione));
			}
			
			String labelPag = PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_TRASFORMAZIONI_APPLICATIVI_CONFIG;
			
			this.pd.setSearchLabel(PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_NOME);
			if(search.equals("")){
				this.pd.setSearchDescription("");
				lstParam.add(new Parameter(labelPag,null));
			}else{
				lstParam.add(new Parameter(labelPag,
						PorteApplicativeCostanti.SERVLET_NAME_PORTE_APPLICATIVE_TRASFORMAZIONI_SERVIZIO_APPLICATIVO_AUTORIZZATO_LIST,
						pIdPorta, pIdSoggetto, pIdAsps, pIdTrasformazione));
				
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
			List<String> labels = new ArrayList<>();
			if(this.porteApplicativeCore.isMultitenant()) {
				labels.add(PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_SOGGETTO);
			}
			labels.add(CostantiControlStation.LABEL_PARAMETRO_APPLICATIVO);
			this.pd.setLabels(labels.toArray(new String[1]));
	
			// preparo i dati
			Vector<Vector<DataElement>> dati = new Vector<Vector<DataElement>>();
	
			if (lista != null) {
				Iterator<TrasformazioneRegolaApplicabilitaServizioApplicativo> it = lista.iterator();
				while (it.hasNext()) {
					TrasformazioneRegolaApplicabilitaServizioApplicativo sa = it.next();
	
					Vector<DataElement> e = new Vector<DataElement>();
	
					if(this.porteApplicativeCore.isMultitenant()) {
						Long idSoggetto =this.soggettiCore.getIdSoggetto(sa.getNomeSoggettoProprietario(), sa.getTipoSoggettoProprietario());	
						DataElement de = new DataElement();
						if(this.isModalitaCompleta()) {
							de.setUrl(SoggettiCostanti.SERVLET_NAME_SOGGETTI_CHANGE,
									new Parameter(SoggettiCostanti.PARAMETRO_SOGGETTO_ID,idSoggetto+""),
									new Parameter(SoggettiCostanti.PARAMETRO_SOGGETTO_NOME,sa.getNomeSoggettoProprietario()),
									new Parameter(SoggettiCostanti.PARAMETRO_SOGGETTO_TIPO,sa.getTipoSoggettoProprietario()));
						}
						de.setValue(this.getLabelNomeSoggetto(protocollo, sa.getTipoSoggettoProprietario() , sa.getNomeSoggettoProprietario()));
						e.addElement(de);
					}
					
					
					DataElement de = new DataElement();
					de.setValue(sa.getNome());
					de.setIdToRemove(sa.getNome()+"@"+sa.getTipoSoggettoProprietario() + "/" + sa.getNomeSoggettoProprietario());
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
	public void preparePorteAppTrasformazioniSoggettoList(String nomePorta, long idTrasformazione, ISearch ricerca, List<TrasformazioneRegolaApplicabilitaSoggetto> lista) throws Exception {
		try {
			// prelevo il flag che mi dice da quale pagina ho acceduto la sezione delle porte delegate
			Integer parentPA = ServletUtils.getIntegerAttributeFromSession(PorteApplicativeCostanti.ATTRIBUTO_PORTE_APPLICATIVE_PARENT, this.session);
			String idAsps = this.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_ASPS);
			if(idAsps == null)
				idAsps = "";

			String listaTmp = this.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_TRASFORMAZIONI_APPLICABILITA_LIST);
			boolean fromList = false;
			if(listaTmp != null && !"".equals(listaTmp))
				fromList = true;
			
			String idPorta = this.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID);
			String idsogg = this.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_SOGGETTO);
			
			Parameter pIdTrasformazione = new Parameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_TRASFORMAZIONE, idTrasformazione+"");
			Parameter pIdPorta = new Parameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID, idPorta);
			Parameter pIdSoggetto = new Parameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_SOGGETTO, idsogg);
			Parameter pIdAsps = new Parameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_ASPS, idAsps);

			List<Parameter> parameters = new ArrayList<>();
			parameters.add(pIdPorta);
			parameters.add(pIdSoggetto);
			parameters.add(pIdAsps);
			parameters.add(pIdTrasformazione);
			if(fromList) {
				parameters.add(new Parameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_TRASFORMAZIONI_APPLICABILITA_LIST, listaTmp));
			}
			
			ServletUtils.addListElementIntoSession(this.session,  PorteApplicativeCostanti.OBJECT_NAME_PORTE_APPLICATIVE_TRASFORMAZIONI_SOGGETTO,
					parameters);

			int idLista = Liste.PORTE_APPLICATIVE_TRASFORMAZIONI_SOGGETTO;
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
			
			Trasformazioni trasformazioni = myPA.getTrasformazioni();
			TrasformazioneRegola oldRegola = null;
			for (TrasformazioneRegola reg : trasformazioni.getRegolaList()) {
				if(reg.getId().longValue() == idTrasformazione) {
					oldRegola = reg;
					break;
				}
			}

			String nomeTrasformazione = oldRegola.getNome();

			// setto la barra del titolo
			List<Parameter> lstParam = this.getTitoloPA(parentPA, idsogg, idAsps);

			String labelPerPorta = null;
			if(parentPA!=null && (parentPA.intValue() == PorteApplicativeCostanti.ATTRIBUTO_PORTE_APPLICATIVE_PARENT_CONFIGURAZIONE)) {
				labelPerPorta = this.porteApplicativeCore.getLabelRegolaMappingErogazionePortaApplicativa(
						PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_TRASFORMAZIONI_DI,
						PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_TRASFORMAZIONI,
						myPA);
			}
			else {
				labelPerPorta = PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_TRASFORMAZIONI_DI+idporta;
			}
			
			lstParam.add(new Parameter(labelPerPorta, PorteApplicativeCostanti.SERVLET_NAME_PORTE_APPLICATIVE_TRASFORMAZIONI_LIST, 
					pIdPorta,
					pIdSoggetto,
					pIdAsps));
			
			if(!fromList) {
				lstParam.add(new Parameter(nomeTrasformazione, PorteApplicativeCostanti.SERVLET_NAME_PORTE_APPLICATIVE_TRASFORMAZIONI_CHANGE, 
						pIdPorta,
						pIdSoggetto,
						pIdAsps, pIdTrasformazione));
			}
			
			String labelPag = PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_TRASFORMAZIONI_SOGGETTO_CONFIG;
			
			this.pd.setSearchLabel(PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_NOME);
			if(search.equals("")){
				this.pd.setSearchDescription("");
				lstParam.add(new Parameter(labelPag,null));
			}else{
				lstParam.add(new Parameter(labelPag,
						PorteApplicativeCostanti.SERVLET_NAME_PORTE_APPLICATIVE_TRASFORMAZIONI_SOGGETTO_LIST ,
						pIdPorta, pIdSoggetto, pIdAsps, pIdTrasformazione));
				
				lstParam.add(new Parameter(PorteApplicativeCostanti.LABEL_PORTE_APPLICATIVE_RISULTATI_RICERCA, null));
			}

			// setto la barra del titolo
			ServletUtils.setPageDataTitle(this.pd, lstParam.toArray(new Parameter[lstParam.size()]));

			// controllo eventuali risultati ricerca
//				if (!search.equals("")) {
//					this.pd.setSearch("on");
//					this.pd.setSearchDescription("Soggetti contenenti la stringa '" + search + "'");
//				}

			// setto le label delle colonne
			String[] labels = { PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_SOGGETTO};
			this.pd.setLabels(labels);

			// preparo i dati
			Vector<Vector<DataElement>> dati = new Vector<Vector<DataElement>>();

			if (lista != null) {
				Iterator<TrasformazioneRegolaApplicabilitaSoggetto> it = lista.iterator();
				while (it.hasNext()) {
					TrasformazioneRegolaApplicabilitaSoggetto sog = it.next();

					Vector<DataElement> e = new Vector<DataElement>();

					Long idSoggetto =this.soggettiCore.getIdSoggetto(sog.getNome(), sog.getTipo());
					DataElement de = new DataElement();
					if(this.isModalitaCompleta()) {
						de.setUrl(SoggettiCostanti.SERVLET_NAME_SOGGETTI_CHANGE,
								new Parameter(SoggettiCostanti.PARAMETRO_SOGGETTO_ID,idSoggetto+""),
								new Parameter(SoggettiCostanti.PARAMETRO_SOGGETTO_NOME,sog.getNome()),
								new Parameter(SoggettiCostanti.PARAMETRO_SOGGETTO_TIPO,sog.getTipo()));
					}
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
	
	// Controlla i dati dell soggetto della porta applicativa
	public boolean porteAppTrasformazioniSoggettoCheckData(TipoOperazione tipoOp) throws Exception {
		try {
			String idPorta = this.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID);
			int idInt = Integer.parseInt(idPorta);
			String soggetto = this.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_SOGGETTO);
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
				
				
				String idTrasformazioneS = this.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_TRASFORMAZIONE);
				long idTrasformazione = Long.parseLong(idTrasformazioneS);
				
				Trasformazioni trasformazioni = pa.getTrasformazioni();
				TrasformazioneRegola regola = null;
				for (TrasformazioneRegola reg : trasformazioni.getRegolaList()) {
					if(reg.getId().longValue() == idTrasformazione) {
						regola = reg;
						break;
					}
				}
				
//				String nometrasformazione = regola.getNome();

				TrasformazioneRegolaApplicabilitaRichiesta applicabilita = regola.getApplicabilita();
				if(applicabilita != null) {
					for (int i = 0; i < applicabilita.sizeSoggettoList(); i++) {
						TrasformazioneRegolaApplicabilitaSoggetto tmpSoggetto = applicabilita.getSoggetto(i); 
						if (tipoSoggettoSelezionato.equals(tmpSoggetto.getTipo()) && nomeSoggettoSelezionato.equals(tmpSoggetto.getNome())) {
							giaRegistrato = true;
							break;
						}
					}
				}

				if (giaRegistrato) {
					String nomeSoggettoMessaggio = tipoSoggettoSelezionato + "/"+ nomeSoggettoSelezionato;
					this.pd.setMessage(MessageFormat.format(PorteApplicativeCostanti.MESSAGGIO_ERRORE_IL_SOGGETTO_XX_EGRAVE_GIA_STATO_ASSOCIATO_ALLA_TRASFORMAZIONE,
							nomeSoggettoMessaggio));
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
	public boolean porteAppTrasformazioniServizioApplicativoAutorizzatiCheckData(TipoOperazione tipoOp) throws Exception {
		try {
			String idPorta = this.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID);
			@SuppressWarnings("unused")
			int idInt = Integer.parseInt(idPorta);
			String soggetto = this.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_SOGGETTO);
			if (soggetto == null) {
				soggetto = "";
			}
			String sa = this.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_SERVIZIO_APPLICATIVO_AUTORIZZATO);
			if (sa == null) {
				sa = "";
			}

			// Campi obbligatori
			if (soggetto.equals("")) {
				this.pd.setMessage(PorteApplicativeCostanti.MESSAGGIO_ERRORE_DATI_INCOMPLETI_E_NECESSARIO_INDICARE_UN_SOGGETTO);
				return false;
			}
			if (sa.equals("")) {
				this.pd.setMessage(PorteApplicativeCostanti.MESSAGGIO_ERRORE_DATI_INCOMPLETI_E_NECESSARIO_INDICARE_UN_APPLICATIVO);
				return false;
			}

			return true;
		} catch (Exception e) {
			this.log.error("Exception: " + e.getMessage(), e);
			throw new Exception(e);
		}
	}
	
	public void preparePorteApplicativeAutenticazioneCustomPropList(String nomePorta, ISearch ricerca, List<Proprieta> lista) throws Exception {
		try {
			// prelevo il flag che mi dice da quale pagina ho acceduto la sezione delle porte delegate
			Integer parentPA = ServletUtils.getIntegerAttributeFromSession(PorteApplicativeCostanti.ATTRIBUTO_PORTE_APPLICATIVE_PARENT, this.session);
			String idAsps = this.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_ASPS);
			if(idAsps == null)
				idAsps = "";

			String id = this.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID);
			String idsogg = this.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_SOGGETTO);

			ServletUtils.addListElementIntoSession(this.session, PorteApplicativeCostanti.OBJECT_NAME_PORTE_APPLICATIVE_AUTENTICAZIONE_CUSTOM_PROPERTIES,
					new Parameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID, id), 
					new Parameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_SOGGETTO, idsogg),
					new Parameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_ASPS, idAsps));

			int idLista = Liste.PORTE_APPLICATIVE_PROPRIETA_AUTENTICAZIONE;
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
				labelPerPorta = this.porteApplicativeCore.getLabelRegolaMappingErogazionePortaApplicativa(
						PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_CONTROLLO_ACCESSI_CONFIG_DI,
						PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_CONTROLLO_ACCESSI,
						pa);
			}
			else {
				labelPerPorta = PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_CONTROLLO_ACCESSI_CONFIG_DI+idporta;
			}
			
			lstParam.add(new Parameter(labelPerPorta, PorteApplicativeCostanti.SERVLET_NAME_PORTE_APPLICATIVE_CONTROLLO_ACCESSI, 
					new Parameter( PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID, id),
					new Parameter( PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_SOGGETTO, idsogg),
					new Parameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_ASPS, idAsps)));
			
			String labelPagLista = PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_AUTENTICAZIONE_PROPRIETA;
			
			this.pd.setSearchLabel(CostantiControlStation.LABEL_PARAMETRO_NOME);
			if(search.equals("")){
				this.pd.setSearchDescription("");
				lstParam.add(new Parameter(labelPagLista,null));
			}
			else{
				lstParam.add(new Parameter(labelPagLista,
						PorteApplicativeCostanti.SERVLET_NAME_PORTE_APPLICATIVE_AUTENTICAZIONE_CUSTOM_PROPERTIES_LIST,
						new Parameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID,id),
						new Parameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_SOGGETTO,idsogg),
						new Parameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_ASPS, idAsps)
						));
				lstParam.add(new Parameter(PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_RISULTATI_RICERCA, null));

			}

			ServletUtils.setPageDataTitle(this.pd, lstParam.toArray(new Parameter[lstParam.size()]));

			// controllo eventuali risultati ricerca
			if (!search.equals("")) {
				ServletUtils.enabledPageDataSearch(this.pd, PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_AUTENTICAZIONE_PROPRIETA, search);
			}

			// setto le label delle colonne
			String[] labels = {CostantiControlStation.LABEL_PARAMETRO_NOME , CostantiControlStation.LABEL_PARAMETRO_VALORE };
			this.pd.setLabels(labels);

			// preparo i dati
			Vector<Vector<DataElement>> dati = new Vector<Vector<DataElement>>();

			if (lista != null) {
				Iterator<Proprieta> it = lista.iterator();
				while (it.hasNext()) {
					Proprieta proprieta = it.next();
		
					Vector<DataElement> e = new Vector<DataElement>();
		
					DataElement de = new DataElement();
					de.setValue(proprieta.getNome());
					de.setIdToRemove(proprieta.getId() + "");
					e.addElement(de);
					
					de = new DataElement();
					de.setValue(proprieta.getValore());
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
	
	public boolean proprietaAutenticazioneCheckData(TipoOperazione tipoOp, String idPorta, String nome,String valore) throws Exception {
		try {
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
			
			// Check Lunghezza
			if(this.checkLength255(nome, PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_NOME)==false) {
				return false;
			}
			if(this.checkLength255(valore, PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_VALORE)==false) {
				return false;
			}
			
			// Se tipoOp = add, controllo che la property non sia gia'
			// stata
			// registrata per la porta applicativa
			if (tipoOp.equals(TipoOperazione.ADD)) {
				boolean giaRegistrato = false;
				PortaApplicativa pa = this.porteApplicativeCore.getPortaApplicativa(Integer.parseInt(idPorta));
				String nomeporta = pa.getNome();

				for (int i = 0; i < pa.sizeProprietaAutenticazioneList(); i++) {
					Proprieta tmpProp = pa.getProprietaAutenticazione(i);
					if (nome.equals(tmpProp.getNome())) {
						giaRegistrato = true;
						break;
					}
				}

				if (giaRegistrato) {
					Integer parentPA = ServletUtils.getIntegerAttributeFromSession(PorteApplicativeCostanti.ATTRIBUTO_PORTE_APPLICATIVE_PARENT, this.session);
					if(parentPA == null) parentPA = PorteApplicativeCostanti.ATTRIBUTO_PORTE_APPLICATIVE_PARENT_NONE;
					
					if(parentPA!=null && (parentPA.intValue() == PorteApplicativeCostanti.ATTRIBUTO_PORTE_APPLICATIVE_PARENT_CONFIGURAZIONE)) {
						nomeporta = this.porteApplicativeCore.getLabelRegolaMappingErogazionePortaApplicativa(null,null, pa);
						this.pd.setMessage(MessageFormat.format(
								PorteApplicativeCostanti.MESSAGGIO_ERRORE_LA_PROPERTY_XX_E_GIA_STATA_ASSOCIATA_ALLA_CONFIGURAZIONE_YY, nome,
								nomeporta)); 
					} else {
						this.pd.setMessage(MessageFormat.format(
								PorteApplicativeCostanti.MESSAGGIO_ERRORE_LA_PROPERTY_XX_E_GIA_STATA_ASSOCIATA_ALLA_PORTA_APPLICATIVA_YY, nome,
								nomeporta));	
					}
					
					
					return false;
				}
			}

			return true;
		} catch (Exception e) {
			this.log.error("Exception: " + e.getMessage(), e);
			throw new Exception(e);
		}
	}

	
	public void preparePorteApplicativeAutorizzazioneCustomPropList(String nomePorta, ISearch ricerca, List<Proprieta> lista) throws Exception {
		try {
			// prelevo il flag che mi dice da quale pagina ho acceduto la sezione delle porte delegate
			Integer parentPA = ServletUtils.getIntegerAttributeFromSession(PorteApplicativeCostanti.ATTRIBUTO_PORTE_APPLICATIVE_PARENT, this.session);
			String idAsps = this.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_ASPS);
			if(idAsps == null)
				idAsps = "";

			String id = this.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID);
			String idsogg = this.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_SOGGETTO);

			ServletUtils.addListElementIntoSession(this.session, PorteApplicativeCostanti.OBJECT_NAME_PORTE_APPLICATIVE_AUTORIZZAZIONE_CUSTOM_PROPERTIES,
					new Parameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID, id), 
					new Parameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_SOGGETTO, idsogg),
					new Parameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_ASPS, idAsps));

			int idLista = Liste.PORTE_APPLICATIVE_PROPRIETA_AUTORIZZAZIONE;
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
				labelPerPorta = this.porteApplicativeCore.getLabelRegolaMappingErogazionePortaApplicativa(
						PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_CONTROLLO_ACCESSI_CONFIG_DI,
						PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_CONTROLLO_ACCESSI,
						pa);
			}
			else {
				labelPerPorta = PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_CONTROLLO_ACCESSI_CONFIG_DI+idporta;
			}
			
			lstParam.add(new Parameter(labelPerPorta, PorteApplicativeCostanti.SERVLET_NAME_PORTE_APPLICATIVE_CONTROLLO_ACCESSI, 
					new Parameter( PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID, id),
					new Parameter( PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_SOGGETTO, idsogg),
					new Parameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_ASPS, idAsps)));
			
			String labelPagLista = PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_AUTORIZZAZIONE_PROPRIETA;
			
			this.pd.setSearchLabel(CostantiControlStation.LABEL_PARAMETRO_NOME);
			if(search.equals("")){
				this.pd.setSearchDescription("");
				lstParam.add(new Parameter(labelPagLista,null));
			}
			else{
				lstParam.add(new Parameter(labelPagLista,
						PorteApplicativeCostanti.SERVLET_NAME_PORTE_APPLICATIVE_AUTORIZZAZIONE_CUSTOM_PROPERTIES_LIST,
						new Parameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID,id),
						new Parameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_SOGGETTO,idsogg),
						new Parameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_ASPS, idAsps)
						));
				lstParam.add(new Parameter(PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_RISULTATI_RICERCA, null));

			}

			ServletUtils.setPageDataTitle(this.pd, lstParam.toArray(new Parameter[lstParam.size()]));

			// controllo eventuali risultati ricerca
			if (!search.equals("")) {
				ServletUtils.enabledPageDataSearch(this.pd, PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_AUTORIZZAZIONE_PROPRIETA, search);
			}

			// setto le label delle colonne
			String[] labels = {CostantiControlStation.LABEL_PARAMETRO_NOME , CostantiControlStation.LABEL_PARAMETRO_VALORE };
			this.pd.setLabels(labels);

			// preparo i dati
			Vector<Vector<DataElement>> dati = new Vector<Vector<DataElement>>();

			if (lista != null) {
				Iterator<Proprieta> it = lista.iterator();
				while (it.hasNext()) {
					Proprieta proprieta = it.next();
		
					Vector<DataElement> e = new Vector<DataElement>();
		
					DataElement de = new DataElement();
					de.setValue(proprieta.getNome());
					de.setIdToRemove(proprieta.getId() + "");
					e.addElement(de);
					
					de = new DataElement();
					de.setValue(proprieta.getValore());
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
	
	public boolean proprietaAutorizzazioneCheckData(TipoOperazione tipoOp, String idPorta, String nome,String valore) throws Exception {
		try {
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
			
			// Check Lunghezza
			if(this.checkLength255(nome, PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_NOME)==false) {
				return false;
			}
			if(this.checkLength255(valore, PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_VALORE)==false) {
				return false;
			}
			
			// Se tipoOp = add, controllo che la property non sia gia'
			// stata
			// registrata per la porta applicativa
			if (tipoOp.equals(TipoOperazione.ADD)) {
				boolean giaRegistrato = false;
				PortaApplicativa pa = this.porteApplicativeCore.getPortaApplicativa(Integer.parseInt(idPorta));
				String nomeporta = pa.getNome();

				for (int i = 0; i < pa.sizeProprietaAutorizzazioneList(); i++) {
					Proprieta tmpProp = pa.getProprietaAutorizzazione(i);
					if (nome.equals(tmpProp.getNome())) {
						giaRegistrato = true;
						break;
					}
				}

				if (giaRegistrato) {
					Integer parentPA = ServletUtils.getIntegerAttributeFromSession(PorteApplicativeCostanti.ATTRIBUTO_PORTE_APPLICATIVE_PARENT, this.session);
					if(parentPA == null) parentPA = PorteApplicativeCostanti.ATTRIBUTO_PORTE_APPLICATIVE_PARENT_NONE;
					
					if(parentPA!=null && (parentPA.intValue() == PorteApplicativeCostanti.ATTRIBUTO_PORTE_APPLICATIVE_PARENT_CONFIGURAZIONE)) {
						nomeporta = this.porteApplicativeCore.getLabelRegolaMappingErogazionePortaApplicativa(null,null, pa);
						this.pd.setMessage(MessageFormat.format(
								PorteApplicativeCostanti.MESSAGGIO_ERRORE_LA_PROPERTY_XX_E_GIA_STATA_ASSOCIATA_ALLA_CONFIGURAZIONE_YY, nome,
								nomeporta)); 
					} else {
						this.pd.setMessage(MessageFormat.format(
								PorteApplicativeCostanti.MESSAGGIO_ERRORE_LA_PROPERTY_XX_E_GIA_STATA_ASSOCIATA_ALLA_PORTA_APPLICATIVA_YY, nome,
								nomeporta));	
					}
					
					
					return false;
				}
			}

			return true;
		} catch (Exception e) {
			this.log.error("Exception: " + e.getMessage(), e);
			throw new Exception(e);
		}
	}
	
	public void preparePorteApplicativeAutorizzazioneContenutoCustomPropList(String nomePorta, ISearch ricerca, List<Proprieta> lista) throws Exception {
		try {
			// prelevo il flag che mi dice da quale pagina ho acceduto la sezione delle porte delegate
			Integer parentPA = ServletUtils.getIntegerAttributeFromSession(PorteApplicativeCostanti.ATTRIBUTO_PORTE_APPLICATIVE_PARENT, this.session);
			String idAsps = this.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_ASPS);
			if(idAsps == null)
				idAsps = "";

			String id = this.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID);
			String idsogg = this.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_SOGGETTO);

			ServletUtils.addListElementIntoSession(this.session, PorteApplicativeCostanti.OBJECT_NAME_PORTE_APPLICATIVE_AUTORIZZAZIONE_CONTENUTI_CUSTOM_PROPERTIES,
					new Parameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID, id), 
					new Parameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_SOGGETTO, idsogg),
					new Parameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_ASPS, idAsps));

			int idLista = Liste.PORTE_APPLICATIVE_PROPRIETA_AUTORIZZAZIONE_CONTENUTO;
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
				labelPerPorta = this.porteApplicativeCore.getLabelRegolaMappingErogazionePortaApplicativa(
						PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_CONTROLLO_ACCESSI_CONFIG_DI,
						PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_CONTROLLO_ACCESSI,
						pa);
			}
			else {
				labelPerPorta = PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_CONTROLLO_ACCESSI_CONFIG_DI+idporta;
			}
			
			lstParam.add(new Parameter(labelPerPorta, PorteApplicativeCostanti.SERVLET_NAME_PORTE_APPLICATIVE_CONTROLLO_ACCESSI, 
					new Parameter( PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID, id),
					new Parameter( PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_SOGGETTO, idsogg),
					new Parameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_ASPS, idAsps)));
			
			String labelPagLista = PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_AUTORIZZAZIONE_CONTENUTO_PROPRIETA;
			
			this.pd.setSearchLabel(CostantiControlStation.LABEL_PARAMETRO_NOME);
			if(search.equals("")){
				this.pd.setSearchDescription("");
				lstParam.add(new Parameter(labelPagLista,null));
			}
			else{
				lstParam.add(new Parameter(labelPagLista,
						PorteApplicativeCostanti.SERVLET_NAME_PORTE_APPLICATIVE_AUTORIZZAZIONE_CONTENUTI_CUSTOM_PROPERTIES_LIST,
						new Parameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID,id),
						new Parameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_SOGGETTO,idsogg),
						new Parameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_ASPS, idAsps)
						));
				lstParam.add(new Parameter(PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_RISULTATI_RICERCA, null));

			}

			ServletUtils.setPageDataTitle(this.pd, lstParam.toArray(new Parameter[lstParam.size()]));

			// controllo eventuali risultati ricerca
			if (!search.equals("")) {
				ServletUtils.enabledPageDataSearch(this.pd, PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_AUTORIZZAZIONE_CONTENUTO_PROPRIETA, search);
			}

			// setto le label delle colonne
			String[] labels = {CostantiControlStation.LABEL_PARAMETRO_NOME , CostantiControlStation.LABEL_PARAMETRO_VALORE };
			this.pd.setLabels(labels);

			// preparo i dati
			Vector<Vector<DataElement>> dati = new Vector<Vector<DataElement>>();

			if (lista != null) {
				Iterator<Proprieta> it = lista.iterator();
				while (it.hasNext()) {
					Proprieta proprieta = it.next();
		
					Vector<DataElement> e = new Vector<DataElement>();
		
					DataElement de = new DataElement();
					de.setValue(proprieta.getNome());
					de.setIdToRemove(proprieta.getId() + "");
					e.addElement(de);
					
					de = new DataElement();
					de.setValue(proprieta.getValore());
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
	
	public boolean proprietaAutorizzazioneContenutoCheckData(TipoOperazione tipoOp, String idPorta, String nome,String valore) throws Exception {
		try {
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
			
			// Check Lunghezza
			if(this.checkLength255(nome, PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_NOME)==false) {
				return false;
			}
			if(this.checkLength255(valore, PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_VALORE)==false) {
				return false;
			}
			
			// Se tipoOp = add, controllo che la property non sia gia'
			// stata
			// registrata per la porta applicativa
			if (tipoOp.equals(TipoOperazione.ADD)) {
				boolean giaRegistrato = false;
				PortaApplicativa pa = this.porteApplicativeCore.getPortaApplicativa(Integer.parseInt(idPorta));
				String nomeporta = pa.getNome();

				for (int i = 0; i < pa.sizeProprietaAutorizzazioneContenutoList(); i++) {
					Proprieta tmpProp = pa.getProprietaAutorizzazioneContenuto(i);
					if (nome.equals(tmpProp.getNome())) {
						giaRegistrato = true;
						break;
					}
				}

				if (giaRegistrato) {
					Integer parentPA = ServletUtils.getIntegerAttributeFromSession(PorteApplicativeCostanti.ATTRIBUTO_PORTE_APPLICATIVE_PARENT, this.session);
					if(parentPA == null) parentPA = PorteApplicativeCostanti.ATTRIBUTO_PORTE_APPLICATIVE_PARENT_NONE;
					
					if(parentPA!=null && (parentPA.intValue() == PorteApplicativeCostanti.ATTRIBUTO_PORTE_APPLICATIVE_PARENT_CONFIGURAZIONE)) {
						nomeporta = this.porteApplicativeCore.getLabelRegolaMappingErogazionePortaApplicativa(null,null, pa);
						this.pd.setMessage(MessageFormat.format(
								PorteApplicativeCostanti.MESSAGGIO_ERRORE_LA_PROPERTY_XX_E_GIA_STATA_ASSOCIATA_ALLA_CONFIGURAZIONE_YY, nome,
								nomeporta));
					} else {
						this.pd.setMessage(MessageFormat.format(
								PorteApplicativeCostanti.MESSAGGIO_ERRORE_LA_PROPERTY_XX_E_GIA_STATA_ASSOCIATA_ALLA_PORTA_APPLICATIVA_YY, nome,
								nomeporta));	
					}
					
					
					return false;
				}
			}

			return true;
		} catch (Exception e) {
			this.log.error("Exception: " + e.getMessage(), e);
			throw new Exception(e);
		}
	}
	
	
	
	public Vector<DataElement> addConnettoriMultipliConfigurazioneToDati(Vector<DataElement> dati,
			TipoOperazione tipoOp, String protocollo, String accessoDaAPSParametro, String stato, String modalitaConsegna, String tipoCustom,
			int numeroProprietaCustom, String servletProprietaCustom, List<Parameter> listaParametriServletProprietaCustom, boolean visualizzaLinkProprietaCustom,
			String loadBalanceStrategia,boolean modificaStatoAbilitata,
			boolean consegnaCondizionale, boolean isSoapOneWay, String connettoreImplementaAPI, List<String> connettoriImplementaAPIValues, List<String> connettoriImplementaAPILabels, String connettorePrincipale,
			boolean notificheCondizionaliEsito, String [] esitiTransazione, ServiceBinding serviceBinding, String selezioneConnettoreBy, String identificazioneCondizionale, String identificazioneCondizionalePattern,
			String identificazioneCondizionalePrefisso, String identificazioneCondizionaleSuffisso, boolean visualizzaLinkRegolePerAzioni, String servletRegolePerAzioni,  List<Parameter> listaParametriServletRegolePerAzioni,
			int numeroRegolePerAzioni, boolean condizioneNonIdentificataAbortTransaction, String condizioneNonIdentificataDiagnostico, String condizioneNonIdentificataConnettore,
			boolean connettoreNonTrovatoAbortTransaction, String connettoreNonTrovatoDiagnostico, String connettoreNonTrovatoConnettore,
			boolean sticky, String stickyTipoSelettore, String stickyTipoSelettorePattern, String stickyMaxAge,
			boolean passiveHealthCheck, String passiveHealthCheck_excludeForSeconds
			) throws Exception {
		Boolean contaListe = ServletUtils.getContaListeFromSession(this.session);
		
		DataElement de = new DataElement();
		de.setLabel(PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_CONFIG);
		de.setType(DataElementType.TITLE);
		dati.addElement(de);
		
		if(accessoDaAPSParametro!=null && !"".equals(accessoDaAPSParametro)) {
			de = new DataElement();
			de.setType(DataElementType.HIDDEN);
			de.setValue(accessoDaAPSParametro);
			de.setName(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_CONNETTORE_DA_LISTA_APS);
			dati.addElement(de);
		}
		
		de = new DataElement();
		de.setName(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_STATO);
		de.setLabel(PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_STATO);
		if(modificaStatoAbilitata) {
			de.setType(DataElementType.SELECT);
			String [] statoLabels = {PorteApplicativeCostanti.VALUE_PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_STATO_ABILITATO , PorteApplicativeCostanti.VALUE_PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_STATO_DISABILITATO };
			String [] statoValues = {PorteApplicativeCostanti.VALUE_PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_STATO_ABILITATO , PorteApplicativeCostanti.VALUE_PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_STATO_DISABILITATO };
			de.setValues(statoValues);
			de.setLabels(statoLabels);
			de.setPostBack(true);
			de.setSelected(stato);
		} else {
			de.setType(DataElementType.TEXT);
			de.setValue(stato);
		}
		dati.addElement(de);
		
		
		de = new DataElement();
		de.setName(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_MODALITA_CONSEGNA);
		de.setLabel(PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_MODALITA_CONSEGNA);
		
		if(stato.equals(PorteApplicativeCostanti.VALUE_PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_STATO_ABILITATO)) {	
			de.setType(DataElementType.SELECT);
			List<String> modalitaLabels = TipoBehaviour.getLabels(this.porteApplicativeCore.isConnettoriMultipliConsegnaMultiplaEnabled(), isSoapOneWay);
			List<String> modalitaValues = TipoBehaviour.getValues(this.porteApplicativeCore.isConnettoriMultipliConsegnaMultiplaEnabled(), isSoapOneWay);
			
			if(modalitaConsegna!=null && !"".equals(modalitaConsegna) && !"-".equals(modalitaConsegna) && !modalitaValues.contains(modalitaConsegna)) {
			
				de.setType(DataElementType.TEXT);
				
				// Provo a tradurre la modalita trovata
				TipoBehaviour modalitaValuesCompleta = TipoBehaviour.toEnumConstant(modalitaConsegna);
				if(modalitaValuesCompleta!=null && !TipoBehaviour.CUSTOM.equals(modalitaValuesCompleta)) {
					de.setValue(modalitaValuesCompleta.getLabel());
				}
				else {
					de.setValue(modalitaConsegna);
				}
				
				this.pd.setMessage("L'impostazione presente nel campo '"+PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_MODALITA_CONSEGNA+"' non è più disponibile. <BR/>Verificare che il profilo di collaborazione delle operazioni definite nell'API non sia cambiato e risulti compatibile.");
				this.pd.setInserisciBottoni(false);
				this.pd.disableEditMode();
				
				dati.addElement(de);
			
				return dati;
				
			}
			else {
				de.setValues(modalitaValues);
				de.setLabels(modalitaLabels);
				de.setPostBack(true);
				de.setSelected(modalitaConsegna);
			}
		} else {
			de.setType(DataElementType.HIDDEN);
			de.setValue(modalitaConsegna);
		}
		dati.addElement(de);
			
		if(stato.equals(PorteApplicativeCostanti.VALUE_PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_STATO_ABILITATO)) {
			
			LoadBalancerType lbt = null;
						
			// custom
			if(TipoBehaviour.CUSTOM.getValue().equals(modalitaConsegna)) {
				
				// campo tipo e link proprieta
				
				this.addCustomField(TipoPlugin.BEHAVIOUR,
						null,
						null,
						PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_MODALITA_CONSEGNA,
						PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_MODALITA_CONSEGNA_CUSTOM_TIPO, 
						PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_MODALITA_CONSEGNA_CUSTOM_TIPO, 
						tipoCustom, false, dati,
						false); 	
				
//				de = new DataElement();
//				de.setLabel(PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_MODALITA_CONSEGNA_CUSTOM_TIPO);
//				de.setType(DataElementType.TEXT_EDIT);
//				de.setName(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_MODALITA_CONSEGNA_CUSTOM_TIPO);
//				de.setSize(this.getSize());
//				de.setRequired(true);
//				de.setValue(tipoCustom);
//				dati.addElement(de);
				
				// Link
				if(visualizzaLinkProprietaCustom){
					de = new DataElement();
					de.setType(DataElementType.LINK);
					de.setUrl(servletProprietaCustom,listaParametriServletProprietaCustom.toArray(new Parameter[listaParametriServletProprietaCustom.size()]));
					if (contaListe)
						de.setValue(PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_MODALITA_CONSEGNA_CUSTOM_PROPRIETA +"(" + numeroProprietaCustom + ")");
					else
						de.setValue(PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_MODALITA_CONSEGNA_CUSTOM_PROPRIETA);
					dati.addElement(de);
				}
			} else if(TipoBehaviour.CONSEGNA_LOAD_BALANCE.getValue().equals(modalitaConsegna)) {
				
				if(loadBalanceStrategia==null) {
					loadBalanceStrategia = LoadBalancerType.ROUND_ROBIN.getValue(); // default
				}
				
				// select list con strategia
				de = new DataElement();
				de.setName(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_MODALITA_CONSEGNA_LOAD_BALANCE_STRATEGIA);
				de.setLabel(PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_MODALITA_CONSEGNA_LOAD_BALANCE_STRATEGIA);
				de.setType(DataElementType.SELECT);
				String [] strategiaLabels = PorteApplicativeCostanti.LABELS_PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_MODALITA_CONSEGNA_LOAD_BALANCE_STRATEGIA;
				String [] strategiaValues = PorteApplicativeCostanti.VALUES_PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_MODALITA_CONSEGNA_LOAD_BALANCE_STRATEGIA;
				de.setValues(strategiaValues);
				de.setLabels(strategiaLabels);
				de.setSelected(loadBalanceStrategia);
				de.setPostBack(true);
				DataElementInfo dInfo = new DataElementInfo(PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_MODALITA_CONSEGNA_LOAD_BALANCE_STRATEGIA);
				dInfo.setListBody(PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_MODALITA_CONSEGNA_LOAD_BALANCE_INFO);
				de.setInfo(dInfo);
				dati.addElement(de);
				
				
				// sticky
				
				if(loadBalanceStrategia!=null) {
					lbt = LoadBalancerType.toEnumConstant(loadBalanceStrategia, true);
				}
				
				de = new DataElement();
				de.setName(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_MODALITA_CONSEGNA_LOAD_BALANCE_STICKY);
				if(lbt!=null && lbt.isSticky()) {
					de.setLabel(PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_MODALITA_CONSEGNA_LOAD_BALANCE_STICKY);
					de.setType(DataElementType.CHECKBOX);
					de.setLabelRight(PorteApplicativeCostanti.LABEL_RIGHT_PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_MODALITA_CONSEGNA_LOAD_BALANCE_STICKY);
					de.setSelected(sticky);
					de.setPostBack(true);
					DataElementInfo dInfoSticky = new DataElementInfo(PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_MODALITA_CONSEGNA_LOAD_BALANCE_STICKY);
					dInfoSticky.setBody(PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_MODALITA_CONSEGNA_LOAD_BALANCE_STICKY_INFO);
					de.setInfo(dInfoSticky);
				}
				else {
					de.setType(DataElementType.HIDDEN);
					de.setSelected(false);
				}
				dati.addElement(de);
				
				
				// health check
				
				de = new DataElement();
				de.setName(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_MODALITA_CONSEGNA_LOAD_BALANCE_PASSIVE_HEALTH_CHECK);
				de.setLabel(PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_MODALITA_CONSEGNA_LOAD_BALANCE_PASSIVE_HEALTH_CHECK);
				de.setType(DataElementType.CHECKBOX);
				de.setLabelRight(PorteApplicativeCostanti.LABEL_RIGHT_PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_MODALITA_CONSEGNA_LOAD_BALANCE_PASSIVE_HEALTH_CHECK);
				de.setSelected(passiveHealthCheck);
				de.setPostBack(true);
				DataElementInfo dInfoPassiveHealthCheck = new DataElementInfo(PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_MODALITA_CONSEGNA_LOAD_BALANCE_PASSIVE_HEALTH_CHECK);
				dInfoPassiveHealthCheck.setBody(PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_MODALITA_CONSEGNA_LOAD_BALANCE_PASSIVE_HEALTH_CHECK_INFO);
				de.setInfo(dInfoPassiveHealthCheck);
				dati.addElement(de);
				
			}  
			
			if(TipoBehaviour.CONSEGNA_CON_NOTIFICHE.getValue().equals(modalitaConsegna)) {
				// sezione notifiche

				//de = new DataElement();
				//de.setLabel(PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_SEZIONE_NOTIFICHE);
				//de.setType(DataElementType.SUBTITLE);
				//dati.addElement(de);
					
				// select con il nome connettore
				de = new DataElement();
				de.setName(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_CONNETTORE_IMPLEMENTA_API);
				de.setLabel(PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_CONNETTORE_IMPLEMENTA_API);
				de.setType(DataElementType.SELECT);
				de.setValues(connettoriImplementaAPIValues);
				de.setLabels(connettoriImplementaAPILabels);
				de.setSelected(connettoreImplementaAPI);
				de.setPostBack(true);
				dati.addElement(de);

			}
			
			// checkbox consegna condizionale
			DataElement deConsegnaConNotifiche = null;
			if(TipoBehaviour.CONSEGNA_LOAD_BALANCE.getValue().equals(modalitaConsegna) || 
					TipoBehaviour.CONSEGNA_MULTIPLA.getValue().equals(modalitaConsegna) || 
					TipoBehaviour.CONSEGNA_CONDIZIONALE.getValue().equals(modalitaConsegna) ||
					TipoBehaviour.CONSEGNA_CON_NOTIFICHE.getValue().equals(modalitaConsegna) ) {
				de = new DataElement();
				de.setName(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_CONSEGNA_CONDIZIONALE);
				de.setType(DataElementType.CHECKBOX);
				de.setSelected(consegnaCondizionale);
				de.setPostBack(true);
				if(TipoBehaviour.CONSEGNA_LOAD_BALANCE.getValue().equals(modalitaConsegna) ) {
					de.setLabel(PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_MODALITA_CONSEGNA_CONDIZIONALE_LEFT);
					de.setLabelRight(PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_MODALITA_CONSEGNA_CONDIZIONALE_LOAD_BALANCER_RIGHT);
				}
				else if(TipoBehaviour.CONSEGNA_MULTIPLA.getValue().equals(modalitaConsegna) ) {
					de.setLabel(PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_MODALITA_CONSEGNA_CONDIZIONALE_LEFT);
					de.setLabelRight(PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_MODALITA_CONSEGNA_CONDIZIONALE_PIU_CONNETTORI_RIGHT);
				}
				else if(TipoBehaviour.CONSEGNA_CONDIZIONALE.getValue().equals(modalitaConsegna)) {
					de.setType(DataElementType.HIDDEN);
					consegnaCondizionale = true;
					de.setSelected(true);
					de.setValue(Costanti.CHECK_BOX_ENABLED);
					de.setPostBack(false);
					dati.addElement(de);
					
					de = new DataElement();
					de.setName(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_CONSEGNA_CONDIZIONALE+"__NOTE");
					de.setType(DataElementType.NOTE);
					de.setValue(PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_MODALITA_CONSEGNA_CONDIZIONALE_SINGOLO_CONNETTORE_RIGHT);
				}
				else if(TipoBehaviour.CONSEGNA_CON_NOTIFICHE.getValue().equals(modalitaConsegna)) {
					de.setLabel(PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_MODALITA_CONSEGNA_CONDIZIONALE_NOTIFICA_LEFT);
					de.setLabelRight(PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_MODALITA_CONSEGNA_CONDIZIONALE_NOTIFICHE_CONNETTORI_RIGHT);
				}
				
				if(TipoBehaviour.CONSEGNA_CON_NOTIFICHE.getValue().equals(modalitaConsegna)) {
					deConsegnaConNotifiche = de;
				}
				else {
					dati.addElement(de);
				}
			}
			
			if(TipoBehaviour.CONSEGNA_LOAD_BALANCE.getValue().equals(modalitaConsegna) && sticky &&
					lbt!=null && lbt.isSticky() ) {
			
				de = new DataElement();
				de.setLabel(PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_MODALITA_CONSEGNA_LOAD_BALANCE_STICKY);
				de.setType(DataElementType.TITLE);
				dati.addElement(de);
				
				
				de = new DataElement();
				de.setName(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_MODALITA_CONSEGNA_LOAD_BALANCE_STICKY_TIPO_SELETTORE);
				de.setLabel(PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_MODALITA_CONSEGNA_LOAD_BALANCE_STICKY_TIPO_SELETTORE);
				de.setType(DataElementType.SELECT);
				
				org.openspcoop2.pdd.core.behaviour.built_in.load_balance.sticky.StickyTipoSelettore tipoSelettoreS = null;
				if(stickyTipoSelettore==null) {
					stickyTipoSelettore = org.openspcoop2.pdd.core.behaviour.built_in.load_balance.sticky.StickyTipoSelettore.COOKIE_BASED.getValue();
				}
				tipoSelettoreS =
						org.openspcoop2.pdd.core.behaviour.built_in.load_balance.sticky.StickyTipoSelettore.toEnumConstant(stickyTipoSelettore, true);
								
				String [] identificazioneCondizionale_values = {
						org.openspcoop2.pdd.core.behaviour.built_in.load_balance.sticky.StickyTipoSelettore.COOKIE_BASED.getValue(),
						org.openspcoop2.pdd.core.behaviour.built_in.load_balance.sticky.StickyTipoSelettore.HEADER_BASED.getValue(),
						org.openspcoop2.pdd.core.behaviour.built_in.load_balance.sticky.StickyTipoSelettore.URLBASED.getValue(),
						org.openspcoop2.pdd.core.behaviour.built_in.load_balance.sticky.StickyTipoSelettore.FORM_BASED.getValue(),
						org.openspcoop2.pdd.core.behaviour.built_in.load_balance.sticky.StickyTipoSelettore.CONTENT_BASED.getValue(),
						org.openspcoop2.pdd.core.behaviour.built_in.load_balance.sticky.StickyTipoSelettore.INDIRIZZO_IP.getValue(),
						org.openspcoop2.pdd.core.behaviour.built_in.load_balance.sticky.StickyTipoSelettore.INDIRIZZO_IP_FORWARDED.getValue(),
						org.openspcoop2.pdd.core.behaviour.built_in.load_balance.sticky.StickyTipoSelettore.TEMPLATE.getValue(),
						org.openspcoop2.pdd.core.behaviour.built_in.load_balance.sticky.StickyTipoSelettore.FREEMARKER_TEMPLATE.getValue(),
						org.openspcoop2.pdd.core.behaviour.built_in.load_balance.sticky.StickyTipoSelettore.VELOCITY_TEMPLATE.getValue()
						};
				
				List<String> identificazioneCondizionale_labels = ModalitaIdentificazione.getLabels(
						ModalitaIdentificazione.COOKIE_BASED,
						ModalitaIdentificazione.HEADER_BASED,
						ModalitaIdentificazione.URL_BASED,
						ModalitaIdentificazione.FORM_BASED,
						ModalitaIdentificazione.CONTENT_BASED,
						ModalitaIdentificazione.INDIRIZZO_IP_BASED,
						ModalitaIdentificazione.X_FORWARD_FOR_BASED,
						ModalitaIdentificazione.GOVWAY_TEMPLATE,
						ModalitaIdentificazione.FREEMARKER_TEMPLATE,
						ModalitaIdentificazione.VELOCITY_TEMPLATE
					);
				
				de.setValues(identificazioneCondizionale_values);
				de.setLabels(identificazioneCondizionale_labels);
				de.setPostBack(true);
				de.setSelected(stickyTipoSelettore);
				dati.addElement(de);
				
				
				// nome
				de = new DataElement();
				de.setName(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_MODALITA_CONSEGNA_LOAD_BALANCE_STICKY_PATTERN);
				de.setLabel(this.getLabelStickySessionPattern(stickyTipoSelettore));
				de.setValue(stickyTipoSelettorePattern);
				if(stickyTipoSelettore==null || 
						org.openspcoop2.pdd.core.behaviour.built_in.load_balance.sticky.StickyTipoSelettore.INDIRIZZO_IP.equals(stickyTipoSelettore) || 
						org.openspcoop2.pdd.core.behaviour.built_in.load_balance.sticky.StickyTipoSelettore.INDIRIZZO_IP_FORWARDED.equals(stickyTipoSelettore)){
					de.setType(DataElementType.HIDDEN);
				}
				else if(org.openspcoop2.pdd.core.behaviour.built_in.load_balance.sticky.StickyTipoSelettore.URLBASED.equals(stickyTipoSelettore) ||
						org.openspcoop2.pdd.core.behaviour.built_in.load_balance.sticky.StickyTipoSelettore.CONTENT_BASED.equals(stickyTipoSelettore)||
						org.openspcoop2.pdd.core.behaviour.built_in.load_balance.sticky.StickyTipoSelettore.TEMPLATE.equals(stickyTipoSelettore)||
						org.openspcoop2.pdd.core.behaviour.built_in.load_balance.sticky.StickyTipoSelettore.VELOCITY_TEMPLATE.equals(stickyTipoSelettore)||
						org.openspcoop2.pdd.core.behaviour.built_in.load_balance.sticky.StickyTipoSelettore.FREEMARKER_TEMPLATE.equals(stickyTipoSelettore)) {
					de.setRequired(true);
					de.setType(DataElementType.TEXT_AREA);
					if(tipoSelettoreS.isTemplate()) {
						de.setRows(10);
					}
				}
				else{
					de.setRequired(true);
					de.setType(DataElementType.TEXT_EDIT);
				}
				

				if(tipoSelettoreS!=null && org.openspcoop2.pdd.core.behaviour.built_in.load_balance.sticky.StickyTipoSelettore.CONTENT_BASED.equals(tipoSelettoreS)) {
					DataElementInfo dInfoPattern = new DataElementInfo(ModalitaIdentificazione.CONTENT_BASED.getLabel());
					if(org.openspcoop2.core.registry.constants.ServiceBinding.REST.equals(serviceBinding)) {
						dInfoPattern.setHeaderBody(PorteApplicativeCostanti.LABEL_CONFIGURAZIONE_CONNETTORI_MULTIPLI_INFO_PATTERN_REST);
						dInfoPattern.setListBody(PorteApplicativeCostanti.LABEL_CONFIGURAZIONE_CONNETTORI_MULTIPLI_INFO_PATTERN_VALORI_REST);
					}
					else {
						dInfoPattern.setBody(PorteApplicativeCostanti.LABEL_CONFIGURAZIONE_CONNETTORI_MULTIPLI_INFO_PATTERN_SOAP);
					}
					de.setInfo(dInfoPattern);
				}
				
				if(tipoSelettoreS!=null && tipoSelettoreS.isTemplate()) {
					DataElementInfo dInfoPattern = null;
					if(org.openspcoop2.pdd.core.behaviour.built_in.load_balance.sticky.StickyTipoSelettore.TEMPLATE.equals(stickyTipoSelettore)) {
						dInfoPattern = new DataElementInfo(ModalitaIdentificazione.GOVWAY_TEMPLATE.getLabel());
						dInfoPattern.setHeaderBody(DynamicHelperCostanti.LABEL_CONFIGURAZIONE_INFO_TRASPORTO);
						if(ServiceBinding.REST.equals(serviceBinding)) {
							dInfoPattern.setListBody(DynamicHelperCostanti.getLABEL_CONFIGURAZIONE_INFO_TRASFORMAZIONI_TRASPORTO_REST_VALORI_CON_RISPOSTE(this.isProfiloModIPA(protocollo), false, false));
						}
						else {
							dInfoPattern.setListBody(DynamicHelperCostanti.getLABEL_CONFIGURAZIONE_INFO_TRASFORMAZIONI_TRASPORTO_SOAP_VALORI_CON_RISPOSTE(this.isProfiloModIPA(protocollo), false, false));
						}
					}
					else if(org.openspcoop2.pdd.core.behaviour.built_in.load_balance.sticky.StickyTipoSelettore.FREEMARKER_TEMPLATE.equals(stickyTipoSelettore)) {
						dInfoPattern = new DataElementInfo(ModalitaIdentificazione.FREEMARKER_TEMPLATE.getLabel());
						dInfoPattern.setHeaderBody(DynamicHelperCostanti.LABEL_CONFIGURAZIONE_INFO_OBJECT_TEMPLATE_FREEMARKER);
						if(ServiceBinding.REST.equals(serviceBinding)) {
							dInfoPattern.setListBody(DynamicHelperCostanti.getLABEL_CONFIGURAZIONE_INFO_OBJECT_REST_VALORI_FREEMARKER(this.isProfiloModIPA(protocollo), false, false));
						}
						else {
							dInfoPattern.setListBody(DynamicHelperCostanti.getLABEL_CONFIGURAZIONE_INFO_OBJECT_SOAP_VALORI_FREEMARKER(this.isProfiloModIPA(protocollo), false, false));
						}
					}
					else {
						dInfoPattern = new DataElementInfo(ModalitaIdentificazione.VELOCITY_TEMPLATE.getLabel());
						dInfoPattern.setHeaderBody(DynamicHelperCostanti.LABEL_CONFIGURAZIONE_INFO_OBJECT_TEMPLATE_VELOCITY);
						if(ServiceBinding.REST.equals(serviceBinding)) {
							dInfoPattern.setListBody(DynamicHelperCostanti.getLABEL_CONFIGURAZIONE_INFO_OBJECT_REST_VALORI_VELOCITY(this.isProfiloModIPA(protocollo), false, false));
						}
						else {
							dInfoPattern.setListBody(DynamicHelperCostanti.getLABEL_CONFIGURAZIONE_INFO_OBJECT_SOAP_VALORI_VELOCITY(this.isProfiloModIPA(protocollo), false, false));
						}
					}
					de.setInfo(dInfoPattern);
				}
				
				dati.add(de);
				
				
				de = new DataElement();
				de.setName(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_MODALITA_CONSEGNA_LOAD_BALANCE_STICKY_MAX_AGE );
				de.setLabel(PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_MODALITA_CONSEGNA_LOAD_BALANCE_STICKY_MAX_AGE);
				de.setNote(PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_MODALITA_CONSEGNA_LOAD_BALANCE_STICKY_MAX_AGE_NOTE);
				de.setType(DataElementType.NUMBER);
				de.setValue(stickyMaxAge);
				de.setMinValue(0);
				de.reloadMinValue(false);
				dati.add(de);
				
			}
			
			if(TipoBehaviour.CONSEGNA_LOAD_BALANCE.getValue().equals(modalitaConsegna) && passiveHealthCheck) {
				
				de = new DataElement();
				de.setLabel(PorteApplicativeCostanti.LABEL_TITLE_PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_MODALITA_CONSEGNA_LOAD_BALANCE_PASSIVE_HEALTH_CHECK);
				de.setType(DataElementType.TITLE);
				dati.addElement(de);
				
				de = new DataElement();
				de.setName(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_MODALITA_CONSEGNA_LOAD_BALANCE_PASSIVE_HEALTH_CHECK_EXCLUDE_FOR_SECONDS );
				if(passiveHealthCheck) {
					de.setLabel(PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_MODALITA_CONSEGNA_LOAD_BALANCE_PASSIVE_HEALTH_CHECK_EXCLUDE_FOR_SECONDS);
					de.setNote(PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_MODALITA_CONSEGNA_LOAD_BALANCE_PASSIVE_HEALTH_CHECK_EXCLUDE_FOR_SECONDS_NOTE);
					de.setType(DataElementType.NUMBER);
					de.setMinValue(1);
					de.reloadMinValue(false);
				}
				else {
					de.setType(DataElementType.HIDDEN);
				}
				if(passiveHealthCheck_excludeForSeconds==null || "".equals(passiveHealthCheck_excludeForSeconds)){
					de.setValue(HealthCheckCostanti.PASSIVE_HEALTH_CHECK_SECONDS_DEFAULT_VALUE+"");
				}
				else {
					de.setValue(passiveHealthCheck_excludeForSeconds);
				}
				dati.add(de);
				
			}

			if(TipoBehaviour.CONSEGNA_CON_NOTIFICHE.getValue().equals(modalitaConsegna)) {
				// sezione notifiche

				de = new DataElement();
				de.setLabel(PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_SEZIONE_NOTIFICHE);
				de.setType(DataElementType.SUBTITLE);
				dati.addElement(de);
									
				// checkbox abilita notifiche condizionali
				de = new DataElement();
				de.setName(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_NOTIFICHE_CONDIZIONALI_ESITO);
				de.setLabel(PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_NOTIFICHE_CONDIZIONALI_ESITO);
				de.setLabelRight(PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_NOTIFICHE_CONDIZIONALI_ESITO_RIGHT);
				de.setType(DataElementType.CHECKBOX);
				de.setSelected(notificheCondizionaliEsito);
				de.setPostBack(true);
				dati.addElement(de);
				
				// multiselect con esiti
				if(notificheCondizionaliEsito) {
					de = new DataElement();
					de.setName(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_ESITI_TRANSAZIONE);
					de.setLabel(PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_ESITI_TRANSAZIONE_LABEL);
					de.setType(DataElementType.MULTI_SELECT);
					 
					de.setValues(PorteApplicativeCostanti.VALUES_PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_ESITI_TRANSAZIONE);
					de.setLabels(PorteApplicativeCostanti.LABELS_PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_ESITI_TRANSAZIONE);
					de.setSelezionati(esitiTransazione);
					de.setRequired(true);
					dati.addElement(de);
				}
				
				dati.addElement(deConsegnaConNotifiche);
			}
			
			
			if(!TipoBehaviour.CUSTOM.getValue().equals(modalitaConsegna) && consegnaCondizionale) {
				de = new DataElement();
				de.setLabel(PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_CONFIGURAZIONE_CONDIZIONALITA);
				de.setType(DataElementType.TITLE);
				dati.addElement(de);
				
				// select list selezione connettore by se modalita' consegna e' piu' destiantari'
				de = new DataElement();
				de.setName(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_SELEZIONE_CONNETTORE_BY);
				de.setLabel(PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_SELEZIONE_CONNETTORE_BY);
				
				if(!TipoBehaviour.CONSEGNA_LOAD_BALANCE.getValue().equals(modalitaConsegna)) {	
					de.setType(DataElementType.SELECT);
					 
					String [] selezioneConnettoreByValues = {
							PorteApplicativeCostanti.VALUE_PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_SELEZIONE_CONNETTORE_BY_FILTRO,
							PorteApplicativeCostanti.VALUE_PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_SELEZIONE_CONNETTORE_BY_NOME
							};
					
					de.setValues(selezioneConnettoreByValues);
					de.setLabels(selezioneConnettoreByValues);
//					de.setPostBack(true);
					de.setSelected(selezioneConnettoreBy);
				} else {
					de.setType(DataElementType.HIDDEN);
					de.setValue(selezioneConnettoreBy);
				}
				dati.addElement(de);
				
				// Identificazione condizionale
				de = new DataElement();
				de.setName(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_IDENTIFICAZIONE_CONDIZIONALE);
				de.setLabel(PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_IDENTIFICAZIONE_CONDIZIONALE);
				de.setType(DataElementType.SELECT);
				
				org.openspcoop2.pdd.core.behaviour.conditional.TipoSelettore tipoSelettoreS = org.openspcoop2.pdd.core.behaviour.conditional.TipoSelettore.toEnumConstant(identificazioneCondizionale, true);
							
				List<String> identificazioneCondizionale_values = new ArrayList<String>();
				identificazioneCondizionale_values.add(org.openspcoop2.pdd.core.behaviour.conditional.TipoSelettore.HEADER_BASED.getValue());
				identificazioneCondizionale_values.add(org.openspcoop2.pdd.core.behaviour.conditional.TipoSelettore.URLBASED.getValue());
				identificazioneCondizionale_values.add(org.openspcoop2.pdd.core.behaviour.conditional.TipoSelettore.FORM_BASED.getValue());
				if(ServiceBinding.SOAP.equals(serviceBinding)) {
					identificazioneCondizionale_values.add(org.openspcoop2.pdd.core.behaviour.conditional.TipoSelettore.SOAPACTION_BASED.getValue());
				}
				identificazioneCondizionale_values.add(org.openspcoop2.pdd.core.behaviour.conditional.TipoSelettore.CONTENT_BASED.getValue());
				identificazioneCondizionale_values.add(org.openspcoop2.pdd.core.behaviour.conditional.TipoSelettore.INDIRIZZO_IP.getValue());
				identificazioneCondizionale_values.add(org.openspcoop2.pdd.core.behaviour.conditional.TipoSelettore.INDIRIZZO_IP_FORWARDED.getValue());
				identificazioneCondizionale_values.add(org.openspcoop2.pdd.core.behaviour.conditional.TipoSelettore.TEMPLATE.getValue());
				identificazioneCondizionale_values.add(org.openspcoop2.pdd.core.behaviour.conditional.TipoSelettore.FREEMARKER_TEMPLATE.getValue());
				identificazioneCondizionale_values.add(org.openspcoop2.pdd.core.behaviour.conditional.TipoSelettore.VELOCITY_TEMPLATE.getValue());
				
				List<String> identificazioneCondizionale_labels = ModalitaIdentificazione.getLabels(
						ModalitaIdentificazione.HEADER_BASED,
						ModalitaIdentificazione.URL_BASED,
						ModalitaIdentificazione.FORM_BASED);
				if(ServiceBinding.SOAP.equals(serviceBinding)) {
					identificazioneCondizionale_labels.addAll(ModalitaIdentificazione.getLabels(ModalitaIdentificazione.SOAP_ACTION_BASED));
				}
				identificazioneCondizionale_labels.addAll(ModalitaIdentificazione.getLabels(
						ModalitaIdentificazione.CONTENT_BASED,
						ModalitaIdentificazione.INDIRIZZO_IP_BASED,
						ModalitaIdentificazione.X_FORWARD_FOR_BASED,
						ModalitaIdentificazione.GOVWAY_TEMPLATE,
						ModalitaIdentificazione.FREEMARKER_TEMPLATE,
						ModalitaIdentificazione.VELOCITY_TEMPLATE
					));
				
				de.setValues(identificazioneCondizionale_values);
				de.setLabels(identificazioneCondizionale_labels);
				de.setPostBack(true);
				de.setSelected(identificazioneCondizionale);
				dati.addElement(de);
				
				// nome
				de = new DataElement();
				de.setName(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_IDENTIFICAZIONE_CONDIZIONALE_PATTERN);
				de.setLabel(this.getLabelIdentificazioneCondizionalePattern(identificazioneCondizionale));
				de.setValue(identificazioneCondizionalePattern);
				if(identificazioneCondizionale==null || 
						org.openspcoop2.pdd.core.behaviour.conditional.TipoSelettore.SOAPACTION_BASED.equals(identificazioneCondizionale)  || 
						org.openspcoop2.pdd.core.behaviour.conditional.TipoSelettore.INDIRIZZO_IP.equals(identificazioneCondizionale) || 
						org.openspcoop2.pdd.core.behaviour.conditional.TipoSelettore.INDIRIZZO_IP_FORWARDED.equals(identificazioneCondizionale)){
					de.setType(DataElementType.HIDDEN);
				}
				else if(org.openspcoop2.pdd.core.behaviour.conditional.TipoSelettore.URLBASED.equals(identificazioneCondizionale) ||
						org.openspcoop2.pdd.core.behaviour.conditional.TipoSelettore.CONTENT_BASED.equals(identificazioneCondizionale)||
						org.openspcoop2.pdd.core.behaviour.conditional.TipoSelettore.TEMPLATE.equals(identificazioneCondizionale)||
						org.openspcoop2.pdd.core.behaviour.conditional.TipoSelettore.VELOCITY_TEMPLATE.equals(identificazioneCondizionale)||
						org.openspcoop2.pdd.core.behaviour.conditional.TipoSelettore.FREEMARKER_TEMPLATE.equals(identificazioneCondizionale)) {
					de.setRequired(true);
					de.setType(DataElementType.TEXT_AREA);
					if(tipoSelettoreS.isTemplate()) {
						de.setRows(10);
					}
				}
				else{
					de.setRequired(true);
					de.setType(DataElementType.TEXT_EDIT);
				}
				
				if(tipoSelettoreS!=null && org.openspcoop2.pdd.core.behaviour.conditional.TipoSelettore.CONTENT_BASED.equals(tipoSelettoreS)) {
					DataElementInfo dInfoPattern = new DataElementInfo(ModalitaIdentificazione.CONTENT_BASED.getLabel());
					if(org.openspcoop2.core.registry.constants.ServiceBinding.REST.equals(serviceBinding)) {
						dInfoPattern.setHeaderBody(PorteApplicativeCostanti.LABEL_CONFIGURAZIONE_CONNETTORI_MULTIPLI_INFO_PATTERN_REST);
						dInfoPattern.setListBody(PorteApplicativeCostanti.LABEL_CONFIGURAZIONE_CONNETTORI_MULTIPLI_INFO_PATTERN_VALORI_REST);
					}
					else {
						dInfoPattern.setBody(PorteApplicativeCostanti.LABEL_CONFIGURAZIONE_CONNETTORI_MULTIPLI_INFO_PATTERN_SOAP);
					}
					de.setInfo(dInfoPattern);
				}
				
				if(tipoSelettoreS!=null && tipoSelettoreS.isTemplate()) {
					DataElementInfo dInfoPattern = null;
					if(org.openspcoop2.pdd.core.behaviour.conditional.TipoSelettore.TEMPLATE.equals(identificazioneCondizionale)) {
						dInfoPattern = new DataElementInfo(ModalitaIdentificazione.GOVWAY_TEMPLATE.getLabel());
						dInfoPattern.setHeaderBody(DynamicHelperCostanti.LABEL_CONFIGURAZIONE_INFO_TRASPORTO);
						if(ServiceBinding.REST.equals(serviceBinding)) {
							dInfoPattern.setListBody(DynamicHelperCostanti.getLABEL_CONFIGURAZIONE_INFO_TRASFORMAZIONI_TRASPORTO_REST_VALORI_CON_RISPOSTE(this.isProfiloModIPA(protocollo), false, false));
						}
						else {
							dInfoPattern.setListBody(DynamicHelperCostanti.getLABEL_CONFIGURAZIONE_INFO_TRASFORMAZIONI_TRASPORTO_SOAP_VALORI_CON_RISPOSTE(this.isProfiloModIPA(protocollo), false, false));
						}
					}
					else if(org.openspcoop2.pdd.core.behaviour.conditional.TipoSelettore.FREEMARKER_TEMPLATE.equals(identificazioneCondizionale)) {
						dInfoPattern = new DataElementInfo(ModalitaIdentificazione.FREEMARKER_TEMPLATE.getLabel());
						dInfoPattern.setHeaderBody(DynamicHelperCostanti.LABEL_CONFIGURAZIONE_INFO_OBJECT_TEMPLATE_FREEMARKER);
						if(ServiceBinding.REST.equals(serviceBinding)) {
							dInfoPattern.setListBody(DynamicHelperCostanti.getLABEL_CONFIGURAZIONE_INFO_OBJECT_REST_VALORI_FREEMARKER(this.isProfiloModIPA(protocollo), false, false));
						}
						else {
							dInfoPattern.setListBody(DynamicHelperCostanti.getLABEL_CONFIGURAZIONE_INFO_OBJECT_SOAP_VALORI_FREEMARKER(this.isProfiloModIPA(protocollo), false, false));
						}
					}
					else {
						dInfoPattern = new DataElementInfo(ModalitaIdentificazione.VELOCITY_TEMPLATE.getLabel());
						dInfoPattern.setHeaderBody(DynamicHelperCostanti.LABEL_CONFIGURAZIONE_INFO_OBJECT_TEMPLATE_VELOCITY);
						if(ServiceBinding.REST.equals(serviceBinding)) {
							dInfoPattern.setListBody(DynamicHelperCostanti.getLABEL_CONFIGURAZIONE_INFO_OBJECT_REST_VALORI_VELOCITY(this.isProfiloModIPA(protocollo), false, false));
						}
						else {
							dInfoPattern.setListBody(DynamicHelperCostanti.getLABEL_CONFIGURAZIONE_INFO_OBJECT_SOAP_VALORI_VELOCITY(this.isProfiloModIPA(protocollo), false, false));
						}
					}
					de.setInfo(dInfoPattern);
				}
				
				dati.addElement(de);
				
				// prefisso
				de = new DataElement();
				de.setLabel(PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_IDENTIFICAZIONE_CONDIZIONALE_PREFISSO);
				de.setName(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_IDENTIFICAZIONE_CONDIZIONALE_PREFISSO);
				de.setSize(this.getSize());
				de.setValue(identificazioneCondizionalePrefisso);
				if(tipoSelettoreS!=null && tipoSelettoreS.isTemplate()) {
					 de.setType(DataElementType.HIDDEN);
				} else {
					 de.setType(DataElementType.TEXT_EDIT);
				}
				dati.addElement(de);
				
				// suffisso
				de = new DataElement();
				de.setLabel(PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_IDENTIFICAZIONE_CONDIZIONALE_SUFFISSO);
				de.setName(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_IDENTIFICAZIONE_CONDIZIONALE_SUFFISSO);
				de.setSize(this.getSize());
				de.setValue(identificazioneCondizionaleSuffisso);
				if(tipoSelettoreS!=null && tipoSelettoreS.isTemplate()) {
					 de.setType(DataElementType.HIDDEN);
				} else {
					 de.setType(DataElementType.TEXT_EDIT);
				}
				dati.addElement(de);
				
				
				// regole per azioni
				// Link
				if(visualizzaLinkRegolePerAzioni){
					de = new DataElement();
					de.setType(DataElementType.LINK);
					de.setUrl(servletRegolePerAzioni,listaParametriServletRegolePerAzioni.toArray(new Parameter[listaParametriServletRegolePerAzioni.size()]));
					if (contaListe)
						de.setValue(PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_REGOLE_PER_AZIONI +"(" + numeroRegolePerAzioni + ")");
					else
						de.setValue(PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_REGOLE_PER_AZIONI);
					dati.addElement(de);
				}
				

				// Sezione Identificazione Condizione Fallita 
				
				de = new DataElement();
				de.setLabel(PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_CONDIZIONE_NON_IDENTIFICATA);
				de.setType(DataElementType.SUBTITLE);
				dati.addElement(de);
				
				de = new DataElement();
				de.setName(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_CONDIZIONE_NON_IDENTIFICATA_ABORT_TRANSACTION);
				de.setLabelRight(PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_CONDIZIONE_NON_IDENTIFICATA_ABORT_TRANSACTION);
				de.setType(DataElementType.CHECKBOX);
				de.setSelected(condizioneNonIdentificataAbortTransaction);
				de.setPostBack(true);
				dati.addElement(de);
				
				if(!condizioneNonIdentificataAbortTransaction) {
					
					// select Diagnostico
					de = new DataElement();
					de.setName(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_CONDIZIONE_NON_IDENTIFICATA_DIAGNOSTICO);
					de.setLabel(PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_CONDIZIONE_NON_IDENTIFICATA_DIAGNOSTICO);
					de.setType(DataElementType.SELECT);
					
					
					String [] condizioneNonIdentificataDiagnosticiValues = {
							StatoFunzionalita.DISABILITATO.getValue(),
							PorteApplicativeCostanti.VALUE_PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_CONDIZIONE_NON_IDENTIFICATA_DIAGNOSTICO_ERROR,
							PorteApplicativeCostanti.VALUE_PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_CONDIZIONE_NON_IDENTIFICATA_DIAGNOSTICO_INFO
							};
					
					String [] condizioneNonIdentificataDiagnosticiLabels = {
							StatoFunzionalita.DISABILITATO.getValue(),
							PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_CONDIZIONE_NON_IDENTIFICATA_DIAGNOSTICO_ERROR,
							PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_CONDIZIONE_NON_IDENTIFICATA_DIAGNOSTICO_INFO
							};
					 
					de.setValues(condizioneNonIdentificataDiagnosticiValues);
					de.setLabels(condizioneNonIdentificataDiagnosticiLabels);
					de.setSelected(condizioneNonIdentificataDiagnostico);
					dati.addElement(de);
					
					if(TipoBehaviour.CONSEGNA_LOAD_BALANCE.getValue().equals(modalitaConsegna) && !condizioneNonIdentificataAbortTransaction) {	
						
						de = new DataElement();
						de.setName(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_CONDIZIONE_NON_IDENTIFICATA_ABORT_TRANSACTION+"__NOTE");
						de.setType(DataElementType.NOTE);
						de.setValue(PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_CONDIZIONE_NON_IDENTIFICATA_LOAD_BALANCER_WARNING);
						dati.addElement(de);

					}
					
					if(!TipoBehaviour.CONSEGNA_LOAD_BALANCE.getValue().equals(modalitaConsegna)) {	
						// select Utilizza Connettore
						de = new DataElement();
						de.setName(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_CONDIZIONE_NON_IDENTIFICATA_CONNETTORE);
						de.setLabel(PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_CONDIZIONE_NON_IDENTIFICATA_CONNETTORE);
						de.setType(DataElementType.SELECT);
						
						List<String> condizioneNonIdentificataConnettoriValues = new ArrayList<String>();
						condizioneNonIdentificataConnettoriValues.addAll(connettoriImplementaAPIValues);
						List<String> condizioneNonIdentificataConnettoriLabels = new ArrayList<String>();
						condizioneNonIdentificataConnettoriLabels.addAll(connettoriImplementaAPILabels);
						if(TipoBehaviour.CONSEGNA_CON_NOTIFICHE.getValue().equals(modalitaConsegna) && connettorePrincipale!=null) {
							for (int i = 0; i < condizioneNonIdentificataConnettoriValues.size(); i++) {
								String c = condizioneNonIdentificataConnettoriValues.get(i);
								if(connettorePrincipale.equals(c)) {
									condizioneNonIdentificataConnettoriValues.remove(i);
									condizioneNonIdentificataConnettoriLabels.remove(i);
									break;
								}
							}
						}
												
						if(TipoBehaviour.CONSEGNA_MULTIPLA.getValue().equals(modalitaConsegna) || TipoBehaviour.CONSEGNA_CON_NOTIFICHE.getValue().equals(modalitaConsegna)) {
							condizioneNonIdentificataConnettoriValues.add(0, CostantiControlStation.DEFAULT_VALUE_AZIONE_RISORSA_NON_SELEZIONATA);
							condizioneNonIdentificataConnettoriLabels.add(0, PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_CONNETTORE_NON_IDENTIFICATO_QUALSIASI);
							if(TipoBehaviour.CONSEGNA_CON_NOTIFICHE.getValue().equals(modalitaConsegna)) {
								condizioneNonIdentificataConnettoriValues.add(1, org.openspcoop2.pdd.core.behaviour.conditional.Costanti.CONDITIONAL_NOME_CONNETTORE_VALORE_NESSUNO);
								condizioneNonIdentificataConnettoriLabels.add(1, PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_CONNETTORE_NON_IDENTIFICATO_NESSUNO);
							}
							de.setPostBack(true);
						}
						
						de.setValues(condizioneNonIdentificataConnettoriValues);
						de.setLabels(condizioneNonIdentificataConnettoriLabels);
						de.setSelected(condizioneNonIdentificataConnettore);
						dati.addElement(de);
						
						if(TipoBehaviour.CONSEGNA_MULTIPLA.getValue().equals(modalitaConsegna) || TipoBehaviour.CONSEGNA_CON_NOTIFICHE.getValue().equals(modalitaConsegna)) {
							if(condizioneNonIdentificataConnettore==null || 
									"".equals(condizioneNonIdentificataConnettore) || 
									CostantiControlStation.DEFAULT_VALUE_AZIONE_RISORSA_NON_SELEZIONATA.equals(condizioneNonIdentificataConnettore)) {
								de = new DataElement();
								de.setName(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_CONDIZIONE_NON_IDENTIFICATA_CONNETTORE+"__NOTE");
								de.setType(DataElementType.NOTE);
								de.setValue(PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_CONDIZIONE_NON_IDENTIFICATA_MULTI_WARNING);
								dati.addElement(de);
							}
							else if(org.openspcoop2.pdd.core.behaviour.conditional.Costanti.CONDITIONAL_NOME_CONNETTORE_VALORE_NESSUNO.equals(condizioneNonIdentificataConnettore)) {
								de = new DataElement();
								de.setName(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_CONDIZIONE_NON_IDENTIFICATA_CONNETTORE+"__NOTE");
								de.setType(DataElementType.NOTE);
								de.setValue(PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_CONNETTORE_NON_TROVATO_NESSUNA_CONSEGNA);
								dati.addElement(de);
							}
						}
					}
				}
				
				// Sezione Nessun Connettore Trovato
				
				de = new DataElement();
				de.setLabel(PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_CONNETTORE_NON_TROVATO);
				de.setType(DataElementType.SUBTITLE);
				dati.addElement(de);
				
				de = new DataElement();
				de.setName(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_CONNETTORE_NON_TROVATO_ABORT_TRANSACTION);
				de.setLabelRight(PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_CONNETTORE_NON_TROVATO_ABORT_TRANSACTION);
				de.setType(DataElementType.CHECKBOX);
				de.setSelected(connettoreNonTrovatoAbortTransaction);
				de.setPostBack(true);
				dati.addElement(de);
				
				if(!connettoreNonTrovatoAbortTransaction) {
										
					// select Diagnostico
					de = new DataElement();
					de.setName(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_CONNETTORE_NON_TROVATO_DIAGNOSTICO);
					de.setLabel(PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_CONNETTORE_NON_TROVATO_DIAGNOSTICO); 
					de.setType(DataElementType.SELECT);
					
					
					String [] connettoreNonTrovatoDiagnosticiValues = {
							StatoFunzionalita.DISABILITATO.getValue(),
							PorteApplicativeCostanti.VALUE_PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_CONNETTORE_NON_TROVATO_DIAGNOSTICO_ERROR,
							PorteApplicativeCostanti.VALUE_PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_CONNETTORE_NON_TROVATO_DIAGNOSTICO_INFO
							};
					
					String [] connettoreNonTrovatoDiagnosticiLabels = {
							StatoFunzionalita.DISABILITATO.getValue(),
							PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_CONNETTORE_NON_TROVATO_DIAGNOSTICO_ERROR,
							PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_CONNETTORE_NON_TROVATO_DIAGNOSTICO_INFO
							};
					 
					de.setValues(connettoreNonTrovatoDiagnosticiValues);
					de.setLabels(connettoreNonTrovatoDiagnosticiLabels);
					de.setSelected(connettoreNonTrovatoDiagnostico);
					dati.addElement(de);
					
					if(TipoBehaviour.CONSEGNA_LOAD_BALANCE.getValue().equals(modalitaConsegna) && !connettoreNonTrovatoAbortTransaction) {	
					
						de = new DataElement();
						de.setName(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_CONNETTORE_NON_TROVATO_ABORT_TRANSACTION+"__NOTE");
						de.setType(DataElementType.NOTE);
						de.setValue(PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_CONNETTORE_NON_TROVATO_LOAD_BALANCER_WARNING);
						dati.addElement(de);

					}
					
					if(!TipoBehaviour.CONSEGNA_LOAD_BALANCE.getValue().equals(modalitaConsegna)) {	
						// select Utilizza Connettore
						de = new DataElement();
						de.setName(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_CONNETTORE_NON_TROVATO_CONNETTORE);
						de.setLabel(PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_CONNETTORE_NON_TROVATO_CONNETTORE);
						de.setType(DataElementType.SELECT);
						 
						List<String> connettoreNonTrovatoConnettoriValues = new ArrayList<String>();
						connettoreNonTrovatoConnettoriValues.addAll(connettoriImplementaAPIValues);
						List<String> connettoreNonTrovatoConnettoriLabels = new ArrayList<String>();
						connettoreNonTrovatoConnettoriLabels.addAll(connettoriImplementaAPILabels);
						if(TipoBehaviour.CONSEGNA_CON_NOTIFICHE.getValue().equals(modalitaConsegna) && connettorePrincipale!=null) {
							for (int i = 0; i < connettoreNonTrovatoConnettoriValues.size(); i++) {
								String c = connettoreNonTrovatoConnettoriValues.get(i);
								if(connettorePrincipale.equals(c)) {
									connettoreNonTrovatoConnettoriValues.remove(i);
									connettoreNonTrovatoConnettoriLabels.remove(i);
									break;
								}
							}
						}
						
						if(TipoBehaviour.CONSEGNA_MULTIPLA.getValue().equals(modalitaConsegna) || TipoBehaviour.CONSEGNA_CON_NOTIFICHE.getValue().equals(modalitaConsegna)) {
							connettoreNonTrovatoConnettoriValues.add(0, CostantiControlStation.DEFAULT_VALUE_AZIONE_RISORSA_NON_SELEZIONATA);
							connettoreNonTrovatoConnettoriLabels.add(0, PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_CONNETTORE_NON_IDENTIFICATO_QUALSIASI);
							if(TipoBehaviour.CONSEGNA_CON_NOTIFICHE.getValue().equals(modalitaConsegna)) {
								connettoreNonTrovatoConnettoriValues.add(1, org.openspcoop2.pdd.core.behaviour.conditional.Costanti.CONDITIONAL_NOME_CONNETTORE_VALORE_NESSUNO);
								connettoreNonTrovatoConnettoriLabels.add(1, PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_CONNETTORE_NON_IDENTIFICATO_NESSUNO);
							}
							de.setPostBack(true);
						}
						
						de.setValues(connettoreNonTrovatoConnettoriValues);
						de.setLabels(connettoreNonTrovatoConnettoriLabels);
						de.setSelected(connettoreNonTrovatoConnettore);
						dati.addElement(de);
						
						if(TipoBehaviour.CONSEGNA_MULTIPLA.getValue().equals(modalitaConsegna) || TipoBehaviour.CONSEGNA_CON_NOTIFICHE.getValue().equals(modalitaConsegna)) {
							if(connettoreNonTrovatoConnettore==null || 
									"".equals(connettoreNonTrovatoConnettore) || 
									CostantiControlStation.DEFAULT_VALUE_AZIONE_RISORSA_NON_SELEZIONATA.equals(connettoreNonTrovatoConnettore)) {
								de = new DataElement();
								de.setName(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_CONNETTORE_NON_TROVATO_CONNETTORE+"__NOTE");
								de.setType(DataElementType.NOTE);
								de.setValue(PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_CONNETTORE_NON_TROVATO_MULTI_WARNING);
								dati.addElement(de);
							}
							else if(org.openspcoop2.pdd.core.behaviour.conditional.Costanti.CONDITIONAL_NOME_CONNETTORE_VALORE_NESSUNO.equals(connettoreNonTrovatoConnettore)) {
								de = new DataElement();
								de.setName(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_CONNETTORE_NON_TROVATO_CONNETTORE+"__NOTE");
								de.setType(DataElementType.NOTE);
								de.setValue(PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_CONNETTORE_NON_TROVATO_NESSUNA_CONSEGNA);
								dati.addElement(de);
							}
						}
					}
				}
			}
		} 
		
		return dati;
	}
	
	public String getLabelStickySessionPattern(String stickyTipoSelettore){
		if(stickyTipoSelettore == null)
			return ModalitaIdentificazione.COOKIE_BASED.getLabelParametro();
		
		org.openspcoop2.pdd.core.behaviour.built_in.load_balance.sticky.StickyTipoSelettore tipo = org.openspcoop2.pdd.core.behaviour.built_in.load_balance.sticky.StickyTipoSelettore.toEnumConstant(stickyTipoSelettore);
		
		switch (tipo) {
		case COOKIE_BASED:
			return ModalitaIdentificazione.COOKIE_BASED.getLabelParametro();
		case HEADER_BASED:
			return ModalitaIdentificazione.HEADER_BASED.getLabelParametro();
		case FORM_BASED:
			return ModalitaIdentificazione.FORM_BASED.getLabelParametro();
		case CONTENT_BASED:
			return ModalitaIdentificazione.CONTENT_BASED.getLabelParametro();
		case URLBASED:
			return ModalitaIdentificazione.URL_BASED.getLabelParametro();
		case INDIRIZZO_IP:
			return ModalitaIdentificazione.INDIRIZZO_IP_BASED.getLabelParametro();
		case INDIRIZZO_IP_FORWARDED:
			return ModalitaIdentificazione.X_FORWARD_FOR_BASED.getLabelParametro();
		case TEMPLATE:
			return ModalitaIdentificazione.GOVWAY_TEMPLATE.getLabelParametro();
		case FREEMARKER_TEMPLATE:
			return ModalitaIdentificazione.FREEMARKER_TEMPLATE.getLabelParametro();
		case VELOCITY_TEMPLATE:
			return ModalitaIdentificazione.VELOCITY_TEMPLATE.getLabelParametro();
		}
		return ModalitaIdentificazione.COOKIE_BASED.getLabelParametro();
	}
	
	public String getLabelIdentificazioneCondizionalePattern(String identificazioneCondizionale){
		return getLabelIdentificazioneCondizionalePattern(identificazioneCondizionale, null);
	}
	
	
	public String getLabelIdentificazioneCondizionalePattern(String identificazioneCondizionale, Boolean selezioneConnettoreByFiltro){
		if(identificazioneCondizionale == null)
			return ModalitaIdentificazione.HEADER_BASED.getLabelParametro();
		
		if(identificazioneCondizionale.equals(PorteApplicativeCostanti.VALUE_PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_AZIONI_IDENTIFICAZIONE_CONDIZIONALE_STATIC_INFO)) {
			if(selezioneConnettoreByFiltro == null) {
				return ModalitaIdentificazione.STATIC.getLabelParametro();
			}
			
			if(selezioneConnettoreByFiltro.booleanValue()) {
				return PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_AZIONI_IDENTIFICAZIONE_CONDIZIONALE_PATTERN_FILTRO;
			} else {
				return PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_AZIONI_IDENTIFICAZIONE_CONDIZIONALE_PATTERN_CONNETTORE;
			}
		}
		
		org.openspcoop2.pdd.core.behaviour.conditional.TipoSelettore tipo = org.openspcoop2.pdd.core.behaviour.conditional.TipoSelettore.toEnumConstant(identificazioneCondizionale);
		
		switch (tipo) {
		case HEADER_BASED:
			return ModalitaIdentificazione.HEADER_BASED.getLabelParametro();
		case FORM_BASED:
			return ModalitaIdentificazione.FORM_BASED.getLabelParametro();
		case CONTENT_BASED:
			return ModalitaIdentificazione.CONTENT_BASED.getLabelParametro();
		case URLBASED:
			return ModalitaIdentificazione.URL_BASED.getLabelParametro();
		case SOAPACTION_BASED:
			return ModalitaIdentificazione.SOAP_ACTION_BASED.getLabelParametro();
		case INDIRIZZO_IP:
			return ModalitaIdentificazione.INDIRIZZO_IP_BASED.getLabelParametro();
		case INDIRIZZO_IP_FORWARDED:
			return ModalitaIdentificazione.X_FORWARD_FOR_BASED.getLabelParametro();
		case TEMPLATE:
			return ModalitaIdentificazione.GOVWAY_TEMPLATE.getLabelParametro();
		case FREEMARKER_TEMPLATE:
			return ModalitaIdentificazione.FREEMARKER_TEMPLATE.getLabelParametro();
		case VELOCITY_TEMPLATE:
			return ModalitaIdentificazione.VELOCITY_TEMPLATE.getLabelParametro();
		}
		return ModalitaIdentificazione.HEADER_BASED.getLabelParametro();
	}
	
	public boolean connettoriMultipliConfigurazioneCheckData(TipoOperazione tipoOp, String stato, String modalitaConsegna, String tipoCustom, String loadBalanceStrategia, 
			boolean isSoapOneWay,
			boolean sticky, String stickyTipoSelettore, String stickyTipoSelettorePattern, String stickyMaxAge,
			boolean passiveHealthCheck, String passiveHealthCheck_excludeForSeconds,
			ServiceBinding serviceBinding) throws Exception{
		
		if (StringUtils.isEmpty(stato)) {
			this.pd.setMessage("Il campo "+PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_STATO+" non pu&ograve; essere vuoto");
			return false;
		}
		if(stato.equals(PorteApplicativeCostanti.VALUE_PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_STATO_ABILITATO)) {
			if (StringUtils.isEmpty(modalitaConsegna)) {
				this.pd.setMessage("Il campo "+PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_MODALITA_CONSEGNA+" non pu&ograve; essere vuoto");
				return false;
			}
			
			boolean validaSezioneCondizionalita = false;
			// custom
			if(modalitaConsegna.equals(TipoBehaviour.CUSTOM.getValue())) {
				if (StringUtils.isEmpty(tipoCustom) || CostantiControlStation.PARAMETRO_TIPO_PERSONALIZZATO_VALORE_UNDEFINED.equals(tipoCustom)) {
					if(this.confCore.isConfigurazionePluginsEnabled()) {
						this.pd.setMessage(PorteApplicativeCostanti.MESSAGGIO_ERRORE_BEHAVIOUR_CUSTOM_NON_INDICATA);
					}
					else {
						this.pd.setMessage("Il campo "+PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_MODALITA_CONSEGNA_CUSTOM_TIPO+" non pu&ograve; essere vuoto");
					}
					return false;
				}
				if (tipoCustom.indexOf(" ") != -1 || tipoCustom.indexOf(",") != -1 ) {
					this.pd.setMessage(PorteApplicativeCostanti.MESSAGGIO_ERRORE_NON_INSERIRE_NE_SPAZI_NE_NEL_CAMPO_TIPO);
					return false;
				}
				if(this.checkLength255(tipoCustom, PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_MODALITA_CONSEGNA_CUSTOM_TIPO)==false) {
					return false;
				}
			} else if(modalitaConsegna.equals(TipoBehaviour.CONSEGNA_LOAD_BALANCE.getValue())) {
				// select list con strategia
				if (StringUtils.isEmpty(loadBalanceStrategia)) {
					this.pd.setMessage("Il campo "+PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_MODALITA_CONSEGNA_LOAD_BALANCE_STRATEGIA+" non pu&ograve; essere vuoto");
					return false;
				}
				
				if(sticky) {
				
					org.openspcoop2.pdd.core.behaviour.built_in.load_balance.sticky.StickyTipoSelettore tipo = 
							org.openspcoop2.pdd.core.behaviour.built_in.load_balance.sticky.StickyTipoSelettore.toEnumConstant(stickyTipoSelettore);
					
					if(tipo.hasParameter()) {
					
						if (StringUtils.isEmpty(stickyTipoSelettorePattern)) {
							this.pd.setMessage("Il campo "+ this.getLabelStickySessionPattern(stickyTipoSelettore) +" non pu&ograve; essere vuoto");
							return false;
						}
						
						if(this.checkLength4000(stickyTipoSelettorePattern, this.getLabelStickySessionPattern(stickyTipoSelettore))==false) {
							return false;
						}
						
						if (org.openspcoop2.pdd.core.behaviour.built_in.load_balance.sticky.StickyTipoSelettore.URLBASED.equals(tipo)) {
							if(this.checkRegexp(stickyTipoSelettorePattern,ModalitaIdentificazione.URL_BASED.getLabelParametro())==false){
								return false;
							}
						}
						if (org.openspcoop2.pdd.core.behaviour.built_in.load_balance.sticky.StickyTipoSelettore.CONTENT_BASED.equals(tipo)) {
							if(ServiceBinding.SOAP.equals(serviceBinding)) {
								if(this.checkXPath(stickyTipoSelettorePattern,ModalitaIdentificazione.CONTENT_BASED.getLabelParametro())==false){
									return false;
								}
							}
							else {
								if(this.checkXPathOrJsonPath(stickyTipoSelettorePattern,ModalitaIdentificazione.CONTENT_BASED.getLabelParametro())==false){
									return false;
								}
							}
						}
					}
					
					if(!StringUtils.isEmpty(stickyMaxAge)) {
					
						int w = -1;
						try {
							w = Integer.parseInt(stickyMaxAge);
						}catch (Exception e) {
							this.pd.setMessage(MessageFormat.format(PorteApplicativeCostanti.MESSAGGIO_ERRRORE_FORMATO_NUMERICO_XX_NON_VALIDO,
									PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_MODALITA_CONSEGNA_LOAD_BALANCE_STICKY_MAX_AGE));
							return false;
						}
						
						if(w < 0) {
							this.pd.setMessage(MessageFormat.format(PorteApplicativeCostanti.MESSAGGIO_ERRRORE_MIN_XX_NON_VALIDO,
									PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_MODALITA_CONSEGNA_LOAD_BALANCE_STICKY_MAX_AGE, 0));
							return false;
						}
					}
					
				}
				
				if(passiveHealthCheck) {
					if(StringUtils.isEmpty(passiveHealthCheck_excludeForSeconds)) {
						this.pd.setMessage("Il campo "+ PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_MODALITA_CONSEGNA_LOAD_BALANCE_PASSIVE_HEALTH_CHECK_EXCLUDE_FOR_SECONDS +" non pu&ograve; essere vuoto");
						return false;
					}
					int w = -1;
					try {
						w = Integer.parseInt(passiveHealthCheck_excludeForSeconds);
					}catch (Exception e) {
						this.pd.setMessage(MessageFormat.format(PorteApplicativeCostanti.MESSAGGIO_ERRRORE_FORMATO_NUMERICO_XX_NON_VALIDO,
								PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_MODALITA_CONSEGNA_LOAD_BALANCE_PASSIVE_HEALTH_CHECK_EXCLUDE_FOR_SECONDS));
						return false;
					}
					
					if(w < 0) {
						this.pd.setMessage(MessageFormat.format(PorteApplicativeCostanti.MESSAGGIO_ERRRORE_MIN_XX_NON_VALIDO,
								PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_MODALITA_CONSEGNA_LOAD_BALANCE_PASSIVE_HEALTH_CHECK_EXCLUDE_FOR_SECONDS, 0));
						return false;
					}
				}
				
				validaSezioneCondizionalita = true; 
				
				
			}  else if(modalitaConsegna.equals(TipoBehaviour.CONSEGNA_MULTIPLA.getValue()) ||
					modalitaConsegna.equals(TipoBehaviour.CONSEGNA_CON_NOTIFICHE.getValue()) ||
					modalitaConsegna.equals(TipoBehaviour.CONSEGNA_CONDIZIONALE.getValue())) {
				
				validaSezioneCondizionalita = true;
			}
			
			// validazione della sezione gestione notifiche
			String connettoreImplementaAPI = this.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_CONNETTORE_IMPLEMENTA_API);
			if(TipoBehaviour.CONSEGNA_CON_NOTIFICHE.getValue().equals(modalitaConsegna)) {
				// select list con strategia
				if (StringUtils.isEmpty(connettoreImplementaAPI)) {
					this.pd.setMessage("Il campo "+PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_CONNETTORE_IMPLEMENTA_API+" non pu&ograve; essere vuoto");
					return false;
				}
				
				String notificheCondizionaliEsitoS = this.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_NOTIFICHE_CONDIZIONALI_ESITO);
				boolean notificheCondizionaliEsito = ServletUtils.isCheckBoxEnabled(notificheCondizionaliEsitoS);
				
				if(notificheCondizionaliEsito) {
					String [] esitiTransazione = this.getParameterValues(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_ESITI_TRANSAZIONE);
					
					if(esitiTransazione == null || esitiTransazione.length <= 0) {
						this.pd.setMessage("Almeno una selezione deve essere effettuata in '"+PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_NOTIFICHE_CONDIZIONALI_ESITO+"'");
						return false;
					}
				}
			}
			
			// validazione della sezione condizionalita'
			if(validaSezioneCondizionalita) {
				String consegnaCondizionaleS = this.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_CONSEGNA_CONDIZIONALE);
				boolean consegnaCondizionale = ServletUtils.isCheckBoxEnabled(consegnaCondizionaleS);
				
				if(consegnaCondizionale) {
					String selezioneConnettoreBy = this.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_SELEZIONE_CONNETTORE_BY);
					
					if (StringUtils.isEmpty(selezioneConnettoreBy)) {
						this.pd.setMessage("Il campo "+PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_SELEZIONE_CONNETTORE_BY+" non pu&ograve; essere vuoto");
						return false;
					}
					
					String identificazioneCondizionale = this.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_IDENTIFICAZIONE_CONDIZIONALE);
					
					org.openspcoop2.pdd.core.behaviour.conditional.TipoSelettore tipo = org.openspcoop2.pdd.core.behaviour.conditional.TipoSelettore.toEnumConstant(identificazioneCondizionale);
										
					if(tipo.hasParameter()) {
					
						String identificazioneCondizionalePattern = this.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_IDENTIFICAZIONE_CONDIZIONALE_PATTERN);
						
						if (StringUtils.isEmpty(identificazioneCondizionalePattern)) {
							this.pd.setMessage("Il campo "+ this.getLabelIdentificazioneCondizionalePattern(identificazioneCondizionale) +" non pu&ograve; essere vuoto");
							return false;
						}
						
						if(this.checkLength4000(identificazioneCondizionalePattern, this.getLabelIdentificazioneCondizionalePattern(identificazioneCondizionale))==false) {
							return false;
						}
						
						
						if (org.openspcoop2.pdd.core.behaviour.conditional.TipoSelettore.URLBASED.equals(tipo)) {
							if(this.checkRegexp(identificazioneCondizionalePattern,ModalitaIdentificazione.URL_BASED.getLabelParametro())==false){
								return false;
							}
						}
						if (org.openspcoop2.pdd.core.behaviour.conditional.TipoSelettore.CONTENT_BASED.equals(tipo)) {
							if(ServiceBinding.SOAP.equals(serviceBinding)) {
								if(this.checkXPath(identificazioneCondizionalePattern,ModalitaIdentificazione.CONTENT_BASED.getLabelParametro())==false){
									return false;
								}
							}
							else {
								if(this.checkXPathOrJsonPath(identificazioneCondizionalePattern,ModalitaIdentificazione.CONTENT_BASED.getLabelParametro())==false){
									return false;
								}
							}
						}
					}
					
										
					if(!tipo.isTemplate()){  // e' un caso in cui e' visibile
					
						String identificazioneCondizionalePrefisso = this.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_IDENTIFICAZIONE_CONDIZIONALE_PREFISSO);
						String identificazioneCondizionaleSuffisso = this.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_IDENTIFICAZIONE_CONDIZIONALE_SUFFISSO);
						
						if(this.checkLength255(identificazioneCondizionalePrefisso, PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_IDENTIFICAZIONE_CONDIZIONALE_PREFISSO)==false) {
							return false;
						}
						
						if(this.checkLength255(identificazioneCondizionaleSuffisso, PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_IDENTIFICAZIONE_CONDIZIONALE_SUFFISSO)==false) {
							return false;
						}
					}
					
					String condizioneNonIdentificataAbortTransactionS = this.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_CONDIZIONE_NON_IDENTIFICATA_ABORT_TRANSACTION);
					boolean condizioneNonIdentificataAbortTransaction = ServletUtils.isCheckBoxEnabled(condizioneNonIdentificataAbortTransactionS);
					if(!condizioneNonIdentificataAbortTransaction) {
						String condizioneNonIdentificataDiagnostico = this.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_CONDIZIONE_NON_IDENTIFICATA_DIAGNOSTICO);
						
						if (StringUtils.isEmpty(condizioneNonIdentificataDiagnostico)) {
							this.pd.setMessage("Il campo "+  PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_CONDIZIONE_NON_IDENTIFICATA_DIAGNOSTICO +" non pu&ograve; essere vuoto");
							return false;
						}
						
						String condizioneNonIdentificataConnettore = this.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_CONDIZIONE_NON_IDENTIFICATA_CONNETTORE);
						
						if(modalitaConsegna.equals(TipoBehaviour.CONSEGNA_CONDIZIONALE.getValue())) {	
							if (StringUtils.isEmpty(condizioneNonIdentificataConnettore)) {
								this.pd.setMessage("Il campo "+  
										PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_CONDIZIONE_NON_IDENTIFICATA_CONNETTORE +" nella sezione '"+
										PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_CONDIZIONE_NON_IDENTIFICATA+"' non pu&ograve; essere vuoto");
								return false;
							}
						}
						
						if(TipoBehaviour.CONSEGNA_CON_NOTIFICHE.getValue().equals(modalitaConsegna)) {
							if(connettoreImplementaAPI.equals(condizioneNonIdentificataConnettore)) {
								this.pd.setMessage("Il campo '"+  
										PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_CONDIZIONE_NON_IDENTIFICATA_CONNETTORE +"' nella sezione '"+
										PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_CONDIZIONE_NON_IDENTIFICATA+"' non pu&ograve; contenere lo stesso valore indicato nel campo '" + 
										PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_CONNETTORE_IMPLEMENTA_API +"'");
								return false;
							}
						}
					}
					
					String connettoreNonTrovatoAbortTransactionS = this.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_CONNETTORE_NON_TROVATO_ABORT_TRANSACTION);
					boolean connettoreNonTrovatoAbortTransaction = ServletUtils.isCheckBoxEnabled(connettoreNonTrovatoAbortTransactionS);
					if(!connettoreNonTrovatoAbortTransaction) {
						String connettoreNonTrovatoDiagnostico = this.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_CONNETTORE_NON_TROVATO_DIAGNOSTICO);
						
						if (StringUtils.isEmpty(connettoreNonTrovatoDiagnostico)) {
							this.pd.setMessage("Il campo "+  PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_CONNETTORE_NON_TROVATO_DIAGNOSTICO +" non pu&ograve; essere vuoto");
							return false;
						}
						
						String connettoreNonTrovatoConnettore = this.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_CONNETTORE_NON_TROVATO_CONNETTORE);
						
						if(modalitaConsegna.equals(TipoBehaviour.CONSEGNA_CONDIZIONALE.getValue())) {	
							if (StringUtils.isEmpty(connettoreNonTrovatoConnettore)) {
								this.pd.setMessage("Il campo "+  
										PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_CONNETTORE_NON_TROVATO_CONNETTORE +" nella sezione '"+
										PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_CONNETTORE_NON_TROVATO+"' non pu&ograve; essere vuoto");
								return false;
							}
						}
						
						if(TipoBehaviour.CONSEGNA_CON_NOTIFICHE.getValue().equals(modalitaConsegna)) {
							if(connettoreImplementaAPI.equals(connettoreNonTrovatoConnettore)) {
								this.pd.setMessage("Il campo '"+  
										PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_CONNETTORE_NON_TROVATO_CONNETTORE +"' nella sezione '"+
										PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_CONNETTORE_NON_TROVATO+"' non pu&ograve; contenere lo stesso valore indicato nel campo '" + 
										PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_CONNETTORE_IMPLEMENTA_API +"'");
								return false;
							}
						}
					}
				}
			}
		}
		
		return true;
	}
	public void preparePorteAppConnettoriMultipliList(String nomePorta, Search ricerca, 
			List<PortaApplicativaServizioApplicativo> listaFiltrata,
			PortaApplicativa pa) throws Exception {
		this._preparePorteAppConnettoriMultipliList(nomePorta, ricerca, 
				listaFiltrata,
				pa, 
				false,
				false, 
				false,
				null);
	}
	public void preparePorteAppConnettoriMultipliList_fromAddConnettore(String nomePorta, Search ricerca, 
			List<PortaApplicativaServizioApplicativo> listaFiltrata,
			PortaApplicativa pa,
			String nomeConnettoreAdd) throws Exception {
		this._preparePorteAppConnettoriMultipliList(nomePorta, ricerca, 
				listaFiltrata,
				pa, 
				true,
				false,
				false, 
				nomeConnettoreAdd);
	}
	public void preparePorteAppConnettoriMultipliList_fromChangeConnettore(String nomePorta, Search ricerca, 
			List<PortaApplicativaServizioApplicativo> listaFiltrata,
			PortaApplicativa pa,
			String nomeConnettoreChanged) throws Exception {
		this._preparePorteAppConnettoriMultipliList(nomePorta, ricerca, 
				listaFiltrata,
				pa, 
				false,
				true, 
				false,
				nomeConnettoreChanged);
	}
	public void preparePorteAppConnettoriMultipliList_fromDeleteConnettore(String nomePorta, Search ricerca, 
			List<PortaApplicativaServizioApplicativo> listaFiltrata,
			PortaApplicativa pa) throws Exception {
		this._preparePorteAppConnettoriMultipliList(nomePorta, ricerca, 
				listaFiltrata,
				pa, 
				false,
				false,
				true, 
				null);
	}
	
	private void _preparePorteAppConnettoriMultipliList(String nomePorta, Search ricerca, 
			List<PortaApplicativaServizioApplicativo> listaFiltrata,
			PortaApplicativa pa,
			boolean fromAdd, 
			boolean fromChange,
			boolean fromDelete,
			String nomeConnettoreProcessed) throws Exception {
		try {
			
			// prelevo il flag che mi dice da quale pagina ho acceduto la sezione delle porte delegate
			Integer parentPA = ServletUtils.getIntegerAttributeFromSession(PorteApplicativeCostanti.ATTRIBUTO_PORTE_APPLICATIVE_PARENT, this.session);
			String idAsps = this.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_ASPS);
			if(idAsps == null)
				idAsps = "";

			String idPorta = this.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID);
			String idsogg = this.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_SOGGETTO);

			Parameter pIdPorta = new Parameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID, idPorta);
			Parameter pNomePorta = new Parameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_NOME, nomePorta);
			Parameter pIdSogg = new Parameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_SOGGETTO, idsogg);
			Parameter pIdAsps = new Parameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_ASPS, idAsps);
			
			String idTabP = this.getParameter(CostantiControlStation.PARAMETRO_ID_TAB);
			Parameter pIdTab = new Parameter(CostantiControlStation.PARAMETRO_ID_TAB, idTabP != null ? idTabP : "");
			
			String accessoDaAPSParametro = this.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_CONNETTORE_DA_LISTA_APS);
			Parameter pAccessoDaAPS = new Parameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_CONNETTORE_DA_LISTA_APS, accessoDaAPSParametro != null ? accessoDaAPSParametro : "");
			String connettoreAccessoGruppi = this.getParameter(CostantiControlStation.PARAMETRO_VERIFICA_CONNETTORE_ACCESSO_DA_GRUPPI);
			Parameter pConnettoreAccessoDaGruppi = new Parameter(CostantiControlStation.PARAMETRO_VERIFICA_CONNETTORE_ACCESSO_DA_GRUPPI, connettoreAccessoGruppi);
			String connettoreRegistro = this.getParameter(CostantiControlStation.PARAMETRO_VERIFICA_CONNETTORE_REGISTRO);
			Parameter pConnettoreRegistro = new Parameter(CostantiControlStation.PARAMETRO_VERIFICA_CONNETTORE_REGISTRO, connettoreRegistro);
			Parameter pConnettoreAccessoCM = new Parameter(CostantiControlStation.PARAMETRO_VERIFICA_CONNETTORE_ACCESSO_DA_LISTA_CONNETTORI_MULTIPLI, "true");
			
			
			ServletUtils.addListElementIntoSession(this.session, PorteApplicativeCostanti.OBJECT_NAME_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI, pIdPorta, pIdSogg, pIdAsps, pNomePorta, pConnettoreAccessoDaGruppi, pConnettoreRegistro, pAccessoDaAPS, pIdTab);
			
			this.pd.setCustomListViewName(PorteApplicativeCostanti.PORTE_APPLICATIVE_NOME_VISTA_CUSTOM_CONNETTORI_MULTIPLI);
			
			int idLista = Liste.PORTE_APPLICATIVE_CONNETTORI_MULTIPLI;
			int limit = ricerca.getPageSize(idLista);
			int offset = ricerca.getIndexIniziale(idLista);
			ServletUtils.disabledPageDataSearch(this.pd); // disabilito il campo search sostituito dal filtro nome
//			String search = ServletUtils.getSearchFromSession(ricerca, idLista);
			
			// filtro tab N.B. impostare sempre come primo! poi aggiornare il valore
			String tabSelezionato = SearchUtils.getFilter(ricerca, idLista, PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_FILTRO_HIDDEN_TAB_SELEZIONATO);
			this.addFilterHidden(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_FILTRO_HIDDEN_TAB_SELEZIONATO, tabSelezionato);
			
			// nuovo filtro nome al posto della search
			String filtroNomeConnettore = this.addFilterNomeConnettoreMultiplo(ricerca, idLista);
			
			boolean behaviourConFiltri = ConditionalUtils.isConfigurazioneCondizionaleByFilter(pa, this.log);
		
			// nuovo filtro filtro
			this.addFilterFiltroConnettoreMultiplo(ricerca, idLista, behaviourConFiltri);
			
			
			// **** filtro connettore ****
			
			// nuovi filtri connettore
			this.addFilterSubtitle(ConnettoriCostanti.NAME_SUBTITLE_DATI_CONNETTORE, ConnettoriCostanti.LABEL_SUBTITLE_DATI_CONNETTORE, false);
			
			// filtro tipo connettore con voce IM solo sulle erogazioni
			String filterTipoConnettore = this.addFilterTipoConnettore(ricerca, idLista, true);
			
			// filtro plugin
			this.addFilterConnettorePlugin(ricerca, idLista, filterTipoConnettore);
			
			// filtro token policy
			this.addFilterConnettoreTokenPolicy(ricerca, idLista, filterTipoConnettore);
			
			// filtro endpoint
			this.addFilterConnettoreEndpoint(ricerca, idLista, filterTipoConnettore);
			
			// filtro keystore
			this.addFilterConnettoreKeystore(ricerca, idLista, filterTipoConnettore);
			
			// imposto apertura sezione
			this.impostaAperturaSubtitle(ConnettoriCostanti.NAME_SUBTITLE_DATI_CONNETTORE);

			// **** fine filtro connettore ****
			
			
			this.pd.setIndex(offset);
			this.pd.setPageSize(limit);
//			this.pd.setLabelBottoneFiltra(CostantiControlStation.LABEL_BOTTONE_INDIVIDUA_CONNETTORE);
			
			boolean accessoDaListaAPS = false;
			
			if(Costanti.CHECK_BOX_ENABLED_TRUE.equals(accessoDaAPSParametro)) {
				accessoDaListaAPS = true;
			}
			
			boolean isModalitaCompleta = this.isModalitaCompleta();
			Boolean vistaErogazioni = ServletUtils.getBooleanAttributeFromSession(ErogazioniCostanti.ASPS_EROGAZIONI_ATTRIBUTO_VISTA_EROGAZIONI, this.session);
			
			//PortaApplicativa pa = this.porteApplicativeCore.getPortaApplicativa(Integer.parseInt(idPorta));
			
			TipoBehaviour behaviourType = TipoBehaviour.toEnumConstant(pa.getBehaviour().getNome());
			
			LoadBalancerType loadBalancerType = null;
			if(behaviourType.equals(TipoBehaviour.CONSEGNA_LOAD_BALANCE)) {
				String balancerTypeS = org.openspcoop2.pdd.core.behaviour.built_in.load_balance.ConfigurazioneLoadBalancer.readLoadBalancerType(pa.getBehaviour());
				loadBalancerType = LoadBalancerType.toEnumConstant(balancerTypeS, true);
			}
			long idAspsLong = Long.parseLong(idAsps);
			AccordoServizioParteSpecifica asps = this.apsCore.getAccordoServizioParteSpecifica(idAspsLong);
			AccordoServizioParteComuneSintetico as = null;
			if(this.porteApplicativeCore.isRegistroServiziLocale()){
				int idAcc = asps.getIdAccordo().intValue();
				as = this.apcCore.getAccordoServizioSintetico(idAcc);
			}
			else{
				as = this.apcCore.getAccordoServizioSintetico(IDAccordoFactory.getInstance().getIDAccordoFromUri(asps.getAccordoServizioParteComune()));
			}
			
			ServiceBinding serviceBinding = this.porteApplicativeCore.toMessageServiceBinding(as.getServiceBinding());
			List<Parameter> lstParam = this.getTitoloPA(parentPA, idsogg, idAsps);
			
			String labelPerPorta = null;
			if(parentPA!=null && (parentPA.intValue() == PorteApplicativeCostanti.ATTRIBUTO_PORTE_APPLICATIVE_PARENT_CONFIGURAZIONE)) {
				if(accessoDaListaAPS) {
					if(!isModalitaCompleta) {
						if(vistaErogazioni != null && vistaErogazioni.booleanValue()) {
							labelPerPorta = PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI;
						} else {
							labelPerPorta = PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_DI+
									this.getLabelIdServizio(asps);
						}
					}
					else {
						labelPerPorta = PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI;
					}
				}
				else {
					labelPerPorta = this.porteApplicativeCore.getLabelRegolaMappingErogazionePortaApplicativa(
							PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_DI,
							PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI,
							pa);
				}
			}
			else {
				labelPerPorta = PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_DI+nomePorta;
			}
			
			if(accessoDaListaAPS) {
				lstParam.remove(lstParam.size()-1);
			}
			
			this.pd.setSearchLabel(PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_NOME);
			this.pd.setSearchDescription("");
			lstParam.add(new Parameter(labelPerPorta,null));
			
//			if(search.equals("")){
//				this.pd.setSearchDescription("");
//				lstParam.add(new Parameter(labelPerPorta,null));
//			}else{
//				List<Parameter> listParametersConfigutazioneConnettoriMultipli = new ArrayList<>();
//				listParametersConfigutazioneConnettoriMultipli.add(pIdSogg);
//				listParametersConfigutazioneConnettoriMultipli.add(pIdPorta);
//				listParametersConfigutazioneConnettoriMultipli.add(pIdAsps);
//				listParametersConfigutazioneConnettoriMultipli.add(pAccessoDaAPS);
//				listParametersConfigutazioneConnettoriMultipli.add(pConnettoreAccessoDaGruppi);
//				listParametersConfigutazioneConnettoriMultipli.add(pConnettoreRegistro);
//				
//				lstParam.add(new Parameter(labelPerPorta,
//						PorteApplicativeCostanti.SERVLET_NAME_PORTE_APPLICATIVE_CONFIGURAZIONE_CONNETTORI_MULTIPLI, 
//						listParametersConfigutazioneConnettoriMultipli.toArray(new Parameter[1])));
//				lstParam.add(new Parameter(PorteApplicativeCostanti.LABEL_PORTE_APPLICATIVE_RISULTATI_RICERCA, null));
//			}
			
			// setto la barra del titolo
			ServletUtils.setPageDataTitle(this.pd, lstParam );	
			
			// controllo eventuali risultati ricerca
//			if (!search.equals("")) {
//				ServletUtils.enabledPageDataSearch(this.pd, ErogazioniCostanti.LABEL_ASPS_EROGAZIONI, search);
//			}
			
			// preparo i dati
			Vector<Vector<DataElement>> dati = new Vector<Vector<DataElement>>();
			
			int idTab = 0;
			
//			if(accessoDaAPSParametro!=null && !"".equals(accessoDaAPSParametro)) {
//				pAccessoDaAPS = new Parameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_CONNETTORE_DA_LISTA_APS, accessoDaAPSParametro);
//			}
			
			Parameter pConfigurazioneDatiGenerali = new Parameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_CONFIGURAZIONE_DATI_GENERALI, Costanti.CHECK_BOX_ENABLED_TRUE);
			Parameter pConfigurazioneDescrizione = new Parameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_CONFIGURAZIONE_DESCRIZIONE, Costanti.CHECK_BOX_ENABLED_TRUE);
			Parameter pConfigurazioneFiltro = new Parameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_CONFIGURAZIONE_FILTRO, Costanti.CHECK_BOX_ENABLED_TRUE);
			Parameter pConfigurazioneConnettore = new Parameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_CONFIGURAZIONE_CONNETTORE, Costanti.CHECK_BOX_ENABLED_TRUE);
			
			// controllo che ci sia almeno un risultato altrimenti visualizzo un messaggio di info/errore
			if(listaFiltrata.size() == 0) {
				if(!fromDelete) {
					this.pd.setMessage(PorteApplicativeCostanti.MESSAGGIO_ERRORE_RICERCA_CONNETTORI_MULTIPLI_NO_RISULTATI);
				} else {
					// se passo qui vuol dire che ho eliminato tutti i connettori corrispondenti al filtro di ricerca
					this.pd.setMessage(PorteApplicativeCostanti.MESSAGGIO_ERRORE_ELIMINATI_CONNETTORI_MULTIPLI_CORRISPONDENTI_AL_FILTRO_RICERCA);
				}
			}
			
			// Colleziono prima i nomi dei connettori per ordinarli in ordine alfatebito, lasciando in testa il connettore di default
			HashMap<String, PortaApplicativaServizioApplicativo> paList = new HashMap<String, PortaApplicativaServizioApplicativo>();
			PortaApplicativaServizioApplicativo paDefault = null;
			Iterator<PortaApplicativaServizioApplicativo> it = listaFiltrata.iterator();
			while (it.hasNext()) {
				PortaApplicativaServizioApplicativo paSA = it.next();
				boolean connettoreDefault = this.isConnettoreDefault(paSA);
				if(connettoreDefault) {
					paDefault = paSA;
				}
				else {
					String nomeConnettore =  this.getLabelNomePortaApplicativaServizioApplicativo(paSA);
					paList.put(nomeConnettore, paSA);
				}
			}
			List<PortaApplicativaServizioApplicativo> listOrdinata = new ArrayList<PortaApplicativaServizioApplicativo>();
			if(paDefault!=null) {
				listOrdinata.add(paDefault);
			}	
			if(!paList.isEmpty()) {
				List<String> keysOrdinate = new ArrayList<String>();
				keysOrdinate.addAll(paList.keySet());
				Collections.sort(keysOrdinate);
				for (String key : keysOrdinate) {
					PortaApplicativaServizioApplicativo paSA = paList.get(key);
					listOrdinata.add(paSA);
				}
			}
			
			// dopo aver filtrato i risultati evidenzio il tab con il nome contenuto nella ricerca
			int tab = 0; // primo a sx 
			
			if(StringUtils.isBlank(tabSelezionato) || ((fromAdd || fromChange) && StringUtils.isNotBlank(nomeConnettoreProcessed))) {
				if( (fromAdd || fromChange) && StringUtils.isNotBlank(nomeConnettoreProcessed)){
					if(listOrdinata!=null && !listOrdinata.isEmpty()) {
						for (int i = 0; i < listOrdinata.size(); i++) {
							String tabSelezionatoCheck =  this.getLabelNomePortaApplicativaServizioApplicativo(listOrdinata.get(i));
							if(nomeConnettoreProcessed.equals(tabSelezionatoCheck)) {
								tabSelezionato = nomeConnettoreProcessed;
								break;
							}
						}
					}
				}
				
				if( !fromDelete &&
						(tabSelezionato==null || StringUtils.isBlank(tabSelezionato)) 
						) {
					String idConnTabP = this.getParameter(CostantiControlStation.PARAMETRO_ID_CONN_TAB);
					if(StringUtils.isNotBlank(idConnTabP)) {
						int idConnTab = -1;
						try {
							idConnTab = Integer.valueOf(idConnTabP);
							if(idConnTab>=0 && listOrdinata!=null && listOrdinata.size()>idConnTab) {
								tabSelezionato =  this.getLabelNomePortaApplicativaServizioApplicativo(listOrdinata.get(idConnTab));
							}
						}catch(Throwable t) {}
					}
				}
			}
			
			ServletUtils.setObjectIntoSession(this.session, tab+"", CostantiControlStation.PARAMETRO_ID_CONN_TAB);
			if(StringUtils.isBlank(tabSelezionato)) {
				if(listOrdinata.size() > 0) {
					tabSelezionato =  this.getLabelNomePortaApplicativaServizioApplicativo(listOrdinata.get(0));
				}
			} else {
				// controllo se il tab gia' selezionato e' ancora contenuto nei risultati della ricerca
				PortaApplicativaServizioApplicativo saContenenteNome = getFiltroNomeConnettore(tabSelezionato, listOrdinata);
				
				if(saContenenteNome!=null) {
					tab = 0;
					for (int i = 0; i < listOrdinata.size(); i++) {
						if(listOrdinata.get(i).getNome().equals(saContenenteNome.getNome())) {
							tab = i;
							break;
						}
					}
					if(tab>=0) {
						ServletUtils.setObjectIntoSession(this.session, tab+"", CostantiControlStation.PARAMETRO_ID_CONN_TAB);
					}
				} else {
					tabSelezionato = "";
					if(!listOrdinata.isEmpty()) {
						if(!filtroNomeConnettore.equals("")) {
							saContenenteNome = getFiltroNomeConnettore(filtroNomeConnettore, listOrdinata);
							if(saContenenteNome!=null) {
								tab = 0;
								for (int i = 0; i < listOrdinata.size(); i++) {
									if(listOrdinata.get(i).getNome().equals(saContenenteNome.getNome())) {
										tab = i;
										tabSelezionato =  this.getLabelNomePortaApplicativaServizioApplicativo(listOrdinata.get(i));
										break;
									}
								}
								if(tab>=0) {
									ServletUtils.setObjectIntoSession(this.session, tab+"", CostantiControlStation.PARAMETRO_ID_CONN_TAB);
								}
							}
						} else {
							tabSelezionato =  this.getLabelNomePortaApplicativaServizioApplicativo(listOrdinata.get(0));
						}
					}
				}
			}
			
			// imposto il nuovo valore nel campo hidden del filtro
			this.pd.updateFilter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_FILTRO_HIDDEN_TAB_SELEZIONATO, tabSelezionato);
			
			for(PortaApplicativaServizioApplicativo paSA: listOrdinata) {
				
				PortaApplicativaServizioApplicativoConnettore datiConnettore = paSA.getDatiConnettore();
				
				Vector<DataElement> e = new Vector<DataElement>();
				
				Parameter pIdTAb = new Parameter(CostantiControlStation.PARAMETRO_ID_CONN_TAB, ""+idTab);
				Parameter pNomePaSA = new Parameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_NOME_SA, paSA.getNome());
				
				// nome e stato
				String nomeConnettore =  this.getLabelNomePortaApplicativaServizioApplicativo(paSA);
				StatoFunzionalita stato = datiConnettore != null ? datiConnettore.getStato() : StatoFunzionalita.ABILITATO;
				boolean statoPA = stato.equals(StatoFunzionalita.ABILITATO);
				StatoFunzionalita scheduling = datiConnettore != null ? datiConnettore.getScheduling() : StatoFunzionalita.ABILITATO;
				boolean schedulingPA = scheduling.equals(StatoFunzionalita.ABILITATO);
				String statoMapping = statoPA ? PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_STATO_ABILITATO_TOOLTIP : PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_STATO_DISABILITATO_TOOLTIP;
				String statoScheduling = schedulingPA ? PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_SCHEDULING_ABILITATO_TOOLTIP : PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_SCHEDULING_DISABILITATO_TOOLTIP;
				boolean urlCambiaStato = true;
				
				boolean connettoreDefault = this.isConnettoreDefault(paSA);
				
				boolean showCambiaStati = urlCambiaStato;
				boolean showCambiaScheduling = false; 
				if(pa.sizeServizioApplicativoList()>1) {	
					if(showCambiaStati) {
						if(TipoBehaviour.CONSEGNA_CON_NOTIFICHE.equals(behaviourType) && this.isConnettoreDefault(paSA)) {
							showCambiaStati = false;
						}
						if(showCambiaStati) {
							showCambiaScheduling = TipoBehaviour.CONSEGNA_CON_NOTIFICHE.equals(behaviourType) || TipoBehaviour.CONSEGNA_MULTIPLA.equals(behaviourType);
						}
					}
				}
				
				IDServizioApplicativo idServizioApplicativo = new IDServizioApplicativo();
				idServizioApplicativo.setIdSoggettoProprietario(new IDSoggetto(pa.getTipoSoggettoProprietario(), pa.getNomeSoggettoProprietario()));
				idServizioApplicativo.setNome(paSA.getNome());
				ServizioApplicativo sa = this.saCore.getServizioApplicativo(idServizioApplicativo);
				org.openspcoop2.core.config.InvocazioneServizio is = sa.getInvocazioneServizio();
				org.openspcoop2.core.config.Connettore connettore = is.getConnettore();
				
				if(showCambiaScheduling) {
					if(is!=null && is.getGetMessage()!=null && StatoFunzionalita.ABILITATO.equals(is.getGetMessage())) {
						if(connettore!=null && connettore.getTipo()!=null) {
							String tipo = connettore.getTipo();
							TipiConnettore tipoC = TipiConnettore.toEnumFromName(tipo);
							if(TipiConnettore.DISABILITATO.equals(tipoC)) {
								showCambiaScheduling = false;
							}
						}
						else {
							showCambiaScheduling = false;
						}
					}
				}
				
				// Nome / Stato
				DataElement de = new DataElement();
				
				de.setWidthPx(10);
				if(TipoBehaviour.CONSEGNA_CON_NOTIFICHE.equals(behaviourType) && this.isConnettoreDefault(paSA)) {
					de.setType(DataElementType.TEXT);
				}
				else {
					de.setType(DataElementType.CHECKBOX);

					de.setStatusToolTip(statoPA ? PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_STATO_ABILITATO_TOOLTIP_NO_ACTION : PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_STATO_DISABILITATO_TOOLTIP_NO_ACTION);
					de.setStatusType(statoPA ? CheckboxStatusType.ABILITATO : CheckboxStatusType.DISABILITATO);
					if(statoPA && showCambiaScheduling && !schedulingPA) {
						de.setStatusType(CheckboxStatusType.WARNING_ONLY);
						de.setStatusToolTip(PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_SCHEDULING_DISABILITATO_TOOLTIP_NO_ACTION);
					}
					
					de.setStatusValue(nomeConnettore);
				}
				
				de.setLabel(PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_NOME);
				de.setValue(nomeConnettore);
				
//					if(!connettoreDefault) { 
				DataElementImage image = new DataElementImage();
				
				image.setUrl(PorteApplicativeCostanti.SERVLET_NAME_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_CHANGE,pIdSogg, pNomePorta, pIdPorta, pIdAsps, pNomePaSA, pIdTAb, pConfigurazioneDatiGenerali,
						pAccessoDaAPS, pConnettoreAccessoDaGruppi, pConnettoreRegistro, pConnettoreAccessoCM	);
				image.setToolTip(MessageFormat.format(CostantiControlStation.ICONA_MODIFICA_CONFIGURAZIONE_TOOLTIP_CON_PARAMETRO,	PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_NOME));
				image.setImage(CostantiControlStation.ICONA_MODIFICA_CONFIGURAZIONE);
				
				de.addImage(image );
//					}
							
				if(pa.sizeServizioApplicativoList()>1) {	
					if(!connettoreDefault) 
						de.setIdToRemove(paSA.getNome());
					
					if(showCambiaStati) {
					
						Parameter pAbilita = new Parameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ABILITA,  (statoPA ? Costanti.CHECK_BOX_DISABLED : Costanti.CHECK_BOX_ENABLED_TRUE));
						image = new DataElementImage();
						image.setUrl(PorteApplicativeCostanti.SERVLET_NAME_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_ABILITAZIONE, pIdSogg, pNomePorta, pIdPorta,pIdAsps,  pNomePaSA, pIdTAb, pAbilita,
								pAccessoDaAPS, pConnettoreAccessoDaGruppi, pConnettoreRegistro, pConnettoreAccessoCM);
						image.setToolTip(statoMapping);
						image.setImage(statoPA ? CostantiControlStation.ICONA_MODIFICA_TOGGLE_ON : CostantiControlStation.ICONA_MODIFICA_TOGGLE_OFF);
						de.addImage(image);
						
						if(showCambiaScheduling && statoPA) {
							pAbilita = new Parameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ABILITA,  (schedulingPA ? Costanti.CHECK_BOX_DISABLED : Costanti.CHECK_BOX_ENABLED_TRUE));
							Parameter pScheduling = new Parameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_SCHEDULING,  "true");
							image = new DataElementImage();
							image.setUrl(PorteApplicativeCostanti.SERVLET_NAME_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_ABILITAZIONE, pIdSogg, pNomePorta, pIdPorta,pIdAsps,  pNomePaSA, pIdTAb, pAbilita,
									pAccessoDaAPS, pConnettoreAccessoDaGruppi, pConnettoreRegistro, pConnettoreAccessoCM,
									pScheduling);
							image.setToolTip(statoScheduling);
							image.setImage(schedulingPA ? CostantiControlStation.ICONA_SCHEDULE_ACTIVE : CostantiControlStation.ICONA_SCHEDULE_PASSIVE);
							de.addImage(image);
						}
					}
					
				}	
				
				e.addElement(de);
				
				// Descrizione
				String descrizioneOrig = (datiConnettore != null && datiConnettore.getDescrizione() != null) ? datiConnettore.getDescrizione() : "";
				de = new DataElement();
				de.setType(DataElementType.TEXT);
				de.setLabel(PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_DESCRIZIONE);
				int length = 150;
				String descrizione = null;
				if(descrizioneOrig !=null && descrizioneOrig.length()>length) {
					descrizione = descrizioneOrig.substring(0, (length-4)) + " ...";
				}
				else {
					descrizione = descrizioneOrig;
				}
				if(StringUtils.isBlank(descrizione))
					descrizione = " ";
				
				de.setValue(descrizione);
				de.setToolTip(descrizioneOrig);
				
				image = new DataElementImage();
				image.setUrl(PorteApplicativeCostanti.SERVLET_NAME_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_CHANGE,pIdSogg, pNomePorta, pIdPorta, pIdAsps, pNomePaSA, pIdTAb,pConfigurazioneDescrizione, pAccessoDaAPS, pConnettoreAccessoDaGruppi, pConnettoreRegistro, pConnettoreAccessoCM);
				image.setToolTip(MessageFormat.format(CostantiControlStation.ICONA_MODIFICA_CONFIGURAZIONE_TOOLTIP_CON_PARAMETRO,	PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_DESCRIZIONE));
				image.setImage(CostantiControlStation.ICONA_MODIFICA_CONFIGURAZIONE);
				
				de.addImage(image );
				
				e.addElement(de);
				
				// Connettore
				de = new DataElement();
				de.setType(DataElementType.TEXT);
				de.setLabel(PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_CONNETTORE);				
				de.setValue(this.getLabelConnettore(sa,is,true));
				String tooltipConnettore = this.getTooltipConnettore(sa,is,true);
				de.setToolTip(tooltipConnettore);
				
				image = new DataElementImage();
				
				image.setUrl(PorteApplicativeCostanti.SERVLET_NAME_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_CHANGE,pIdSogg, pNomePorta, pIdPorta, pIdAsps, pNomePaSA, pIdTAb,pConfigurazioneConnettore, pAccessoDaAPS, pConnettoreAccessoDaGruppi, pConnettoreRegistro, pConnettoreAccessoCM);
//				image.setUrl(ServiziApplicativiCostanti.SERVLET_NAME_SERVIZI_APPLICATIVI_ENDPOINT,pIdProvider, pIdPortaPerSA, pIdAsps, pIdTAb, pAccessoDaAPS, pConnettoreAccessoDaGruppi, pConnettoreRegistro, pConnettoreAccessoCM,
//						new Parameter(ServiziApplicativiCostanti.PARAMETRO_SERVIZI_APPLICATIVI_NOME_SERVIZIO_APPLICATIVO, paSA.getNome()),
//						new Parameter(ServiziApplicativiCostanti.PARAMETRO_SERVIZI_APPLICATIVI_ID_SERVIZIO_APPLICATIVO, paSA.getId()+""));
				image.setToolTip(MessageFormat.format(CostantiControlStation.ICONA_MODIFICA_CONFIGURAZIONE_TOOLTIP_CON_PARAMETRO,	PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_CONNETTORE));
				image.setImage(CostantiControlStation.ICONA_MODIFICA_CONFIGURAZIONE);
				
				de.addImage(image );
				
				boolean checkConnettore = org.openspcoop2.pdd.core.connettori.ConnettoreCheck.checkSupported(connettore);
				
				if(checkConnettore) {
					long idConnettore = connettore.getId();
					image = new DataElementImage();
					image.setUrl(PorteApplicativeCostanti.SERVLET_NAME_PORTE_APPLICATIVE_VERIFICA_CONNETTORE, pIdSogg, pIdPorta, pIdAsps,pIdTAb,
							new Parameter(CostantiControlStation.PARAMETRO_VERIFICA_CONNETTORE_ID, idConnettore+""),
							pAccessoDaAPS, pConnettoreAccessoDaGruppi, pConnettoreRegistro, pConnettoreAccessoCM);
					image.setToolTip(MessageFormat.format(CostantiControlStation.ICONA_VERIFICA_TOOLTIP_CON_PARAMETRO, PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_CONNETTORE));
					image.setImage(CostantiControlStation.ICONA_VERIFICA);
					
					de.addImage(image);
				}
				
				e.addElement(de);
				
				// Filtri
				boolean showFiltri = behaviourConFiltri;
				if(showFiltri) {
					if(TipoBehaviour.CONSEGNA_CON_NOTIFICHE.equals(behaviourType) && this.isConnettoreDefault(paSA)) {
						showFiltri = false;
					}
				}
				if(showFiltri) {
					de = new DataElement();
					de.setType(DataElementType.BUTTON);
					de.setLabel(PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_FILTRI);
					List<String> labelsGruppi = new ArrayList<String>();
					List<String> valuesGruppi = new ArrayList<String>();
					
					if(paSA.getDatiConnettore() != null) {
						for (int i = 0; i < paSA.getDatiConnettore().sizeFiltroList(); i++) {
							String filtro = paSA.getDatiConnettore().getFiltro(i);
							labelsGruppi.add(filtro);
							valuesGruppi.add("label-info-default");
						}
					}	
					
					de.setLabels(labelsGruppi);
					de.setValues(valuesGruppi);
					
					image = new DataElementImage();
					
					image.setUrl(PorteApplicativeCostanti.SERVLET_NAME_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_CHANGE,pIdSogg, pNomePorta, pIdPorta, pIdAsps, pNomePaSA, pIdTAb,pConfigurazioneFiltro,pAccessoDaAPS, pConnettoreAccessoDaGruppi, pConnettoreRegistro, pConnettoreAccessoCM);
					image.setToolTip(MessageFormat.format(CostantiControlStation.ICONA_MODIFICA_CONFIGURAZIONE_TOOLTIP_CON_PARAMETRO,	PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_FILTRI));
					image.setImage(CostantiControlStation.ICONA_MODIFICA_CONFIGURAZIONE);
					
					de.addImage(image);
					
					e.addElement(de);
				}
				
				if(behaviourType.equals(TipoBehaviour.CONSEGNA_LOAD_BALANCE) &&
						loadBalancerType.isTypeWithWeight()) {
					// Proprieta
					de = new DataElement();
					de.setType(DataElementType.TEXT);
					de.setLabel(PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_LOAD_BALANCE);

					String balancerWeight = org.openspcoop2.pdd.core.behaviour.built_in.load_balance.ConfigurazioneLoadBalancer.readLoadBalancerWeight(paSA);
					if(StringUtils.isBlank(balancerWeight))
						balancerWeight = " ";
					de.setValue(PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_LOAD_BALANCE_WEIGHT+": "+balancerWeight);
					
					image = new DataElementImage();
					
					image.setUrl(PorteApplicativeCostanti.SERVLET_NAME_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_CONFIGURAZIONE_PROPRIETA_FORM,pIdSogg, pNomePorta, pIdPorta, pIdAsps, pNomePaSA, pIdTAb, pAccessoDaAPS);
					image.setToolTip(MessageFormat.format(CostantiControlStation.ICONA_MODIFICA_CONFIGURAZIONE_TOOLTIP_CON_PARAMETRO,	PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_LOAD_BALANCE));
					image.setImage(CostantiControlStation.ICONA_MODIFICA_CONFIGURAZIONE);
					
					de.addImage(image );
					
					e.addElement(de);
				}
				
				
				if(behaviourType.equals(TipoBehaviour.CUSTOM)) {
					// Proprieta
					de = new DataElement();
					de.setType(DataElementType.TEXT);
					de.setLabel(PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_PROPRIETA);
					int sizeProprietaList = paSA.getDatiConnettore() != null ? paSA.getDatiConnettore().sizeProprietaList() : 0;
					setStatoProprieta(de, sizeProprietaList);
					
					image = new DataElementImage();
					
					image.setUrl(PorteApplicativeCostanti.SERVLET_NAME_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_PROPERTIES_LIST,pIdSogg, pNomePorta, pIdPorta, pIdAsps, pNomePaSA, pIdTAb);
					image.setToolTip(MessageFormat.format(CostantiControlStation.ICONA_MODIFICA_CONFIGURAZIONE_TOOLTIP_CON_PARAMETRO,	PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_PROPRIETA));
					image.setImage(CostantiControlStation.ICONA_MODIFICA_CONFIGURAZIONE);
					
					de.addImage(image );
					
					e.addElement(de);
				}
				
				boolean showGestioneNotifiche = false;
				if(behaviourType.equals(TipoBehaviour.CONSEGNA_MULTIPLA) || behaviourType.equals(TipoBehaviour.CONSEGNA_CON_NOTIFICHE)) {
					showGestioneNotifiche = true;
					if(is!=null && is.getGetMessage()!=null && StatoFunzionalita.ABILITATO.equals(is.getGetMessage())) {
						if(connettore!=null && connettore.getTipo()!=null) {
							String tipo = connettore.getTipo();
							TipiConnettore tipoC = TipiConnettore.toEnumFromName(tipo);
							if(TipiConnettore.DISABILITATO.equals(tipoC)) {
								showGestioneNotifiche = false;
							}
						}
						else {
							showGestioneNotifiche = false;
						}
					}
				}
				// FIX: la gestione sincrona della connessione e' completamente differente e non può essere gestita con le opzioni di consegna delle notifiche. (es. 3xx accettato su rest normalmente)
				if(showGestioneNotifiche && behaviourType.equals(TipoBehaviour.CONSEGNA_CON_NOTIFICHE) && connettoreDefault) {
					showGestioneNotifiche = false;
				}
				if(showGestioneNotifiche) {
					
					// Proprieta
					de = new DataElement();
					de.setType(DataElementType.TEXT);
					String label = null;
					if(behaviourType.equals(TipoBehaviour.CONSEGNA_CON_NOTIFICHE) && connettoreDefault) {
						label = PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_GESTIONE_CONSEGNA;
					}
					else {
						label = PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_GESTIONE_NOTIFICHE;
					}
					de.setLabel(label);
					
					ConfigurazioneGestioneConsegnaNotifiche configurazioneGestioneConsegnaNotifiche = org.openspcoop2.pdd.core.behaviour.built_in.multi_deliver.MultiDeliverUtils.read(paSA, this.log);
					
					String consegnaNotificheLabel = "";
					
					if(configurazioneGestioneConsegnaNotifiche != null)
						consegnaNotificheLabel = org.openspcoop2.pdd.core.behaviour.built_in.multi_deliver.GestioneConsegnaNotificheUtils.toString(configurazioneGestioneConsegnaNotifiche,
								serviceBinding.equals(ServiceBinding.SOAP));
					
					if(paSA.getDatiConnettore() != null) {
						
						if(paSA.getDatiConnettore().isPrioritaMax()) {
							String consegnaNotificheLabelPriorita = "Priorità con "+PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_NOTIFICHE_PRIORITA_MAX_LEFT;
							if(StringUtils.isBlank(consegnaNotificheLabel)) {
								consegnaNotificheLabel = consegnaNotificheLabelPriorita;
							}
							else {
								consegnaNotificheLabel = consegnaNotificheLabelPriorita+". " +consegnaNotificheLabel;
							}
						}
						else if(paSA.getDatiConnettore().getPriorita() != null) {
							ConfigurazionePriorita confPriorita = this.porteApplicativeCore.getConsegnaNotificaConfigurazionePriorita(paSA.getDatiConnettore().getPriorita());
							if(!confPriorita.isNessunaPriorita()) {
								String consegnaNotificheLabelPriorita = "Priorità "+confPriorita.getLabel();
								if(StringUtils.isBlank(consegnaNotificheLabel)) {
									consegnaNotificheLabel = consegnaNotificheLabelPriorita;
								}
								else {
									consegnaNotificheLabel = consegnaNotificheLabelPriorita+". " +consegnaNotificheLabel;
								}
							}
						} 
						
						if(paSA.getDatiConnettore().getCoda() != null && !CostantiConfigurazione.CODA_DEFAULT.equals(paSA.getDatiConnettore().getCoda())) {
							String consegnaNotificheLabelCoda = "Coda '"+this.porteApplicativeCore.getConsegnaNotificaCodaLabel(paSA.getDatiConnettore().getCoda())+"'";
							if(StringUtils.isBlank(consegnaNotificheLabel)) {
								consegnaNotificheLabel = consegnaNotificheLabelCoda;
							}
							else {
								consegnaNotificheLabel = consegnaNotificheLabelCoda+". " +consegnaNotificheLabel;
							}
						} 
					}
					
					if(StringUtils.isBlank(consegnaNotificheLabel))
						consegnaNotificheLabel = " ";
					
					de.setValue(consegnaNotificheLabel);
					
					image = new DataElementImage();
					
					image.setUrl(PorteApplicativeCostanti.SERVLET_NAME_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_CONFIGURAZIONE_PROPRIETA_NOTIFICHE,pIdSogg, pNomePorta, pIdPorta, pIdAsps, pNomePaSA, pIdTAb, pAccessoDaAPS);
					image.setToolTip(MessageFormat.format(CostantiControlStation.ICONA_MODIFICA_CONFIGURAZIONE_TOOLTIP_CON_PARAMETRO, label));
					image.setImage(CostantiControlStation.ICONA_MODIFICA_CONFIGURAZIONE);
					
					de.addImage(image );
					
					e.addElement(de);
				}
				
				dati.addElement(e);
				idTab ++;
			}
			
			this.pd.setDati(dati);
			this.pd.setSelect(true);
			this.pd.setAddButton(true);
			if(pa.sizeServizioApplicativoList() > 1)
				this.pd.setRemoveButton(true);
			else 
				this.pd.setRemoveButton(false);
			
		}  catch (Exception e) {
			this.log.error("Exception: " + e.getMessage(), e);
			throw new Exception(e);
		}
	}
	
	public List<PortaApplicativaServizioApplicativo> applicaFiltriRicercaConnettoriMultipli(ISearch ricerca, int idLista, 
			List<PortaApplicativaServizioApplicativo> lista, IDSoggetto idSoggettoProprietario) throws DriverConfigurazioneException {
		
		String filtroConnettoreNome = SearchUtils.getFilter(ricerca, idLista, Filtri.FILTRO_CONNETTORE_MULTIPLO_NOME);
		String filtroConnettoreFiltro = SearchUtils.getFilter(ricerca, idLista, Filtri.FILTRO_CONNETTORE_MULTIPLO_FILTRO);
		String filtroConnettoreTipo = SearchUtils.getFilter(ricerca, idLista, Filtri.FILTRO_CONNETTORE_TIPO);
		String filtroConnettoreTipoPlugin = SearchUtils.getFilter(ricerca, idLista, Filtri.FILTRO_CONNETTORE_TIPO_PLUGIN);
		String filtroConnettoreTokenPolicy = SearchUtils.getFilter(ricerca, idLista, Filtri.FILTRO_CONNETTORE_TOKEN_POLICY);
		String filtroConnettoreEndpoint = SearchUtils.getFilter(ricerca, idLista, Filtri.FILTRO_CONNETTORE_ENDPOINT);
		String filtroConnettoreKeystore = SearchUtils.getFilter(ricerca, idLista, Filtri.FILTRO_CONNETTORE_KEYSTORE);
		if((filtroConnettoreTipo!=null && "".equals(filtroConnettoreTipo))) {
			filtroConnettoreTipo=null;
		}
		if((filtroConnettoreTipoPlugin!=null && "".equals(filtroConnettoreTipoPlugin))) {
			filtroConnettoreTipoPlugin=null;
		}
		if((filtroConnettoreTokenPolicy!=null && "".equals(filtroConnettoreTokenPolicy))) {
			filtroConnettoreTokenPolicy=null;
		}
		if((filtroConnettoreEndpoint!=null && "".equals(filtroConnettoreEndpoint))) {
			filtroConnettoreEndpoint=null;
		}
		if((filtroConnettoreKeystore!=null && "".equals(filtroConnettoreKeystore))) {
			filtroConnettoreKeystore=null;
		}
		boolean joinConnettore =  filtroConnettoreTipo!=null	|| filtroConnettoreTokenPolicy!=null || filtroConnettoreEndpoint!=null || filtroConnettoreKeystore!=null;
		TipiConnettore tipoConnettore = null;
		String endpointType = null;
		boolean tipoConnettoreIntegrationManager = false; 
		if(filtroConnettoreTipo!=null && !"".equals(filtroConnettoreTipo)) {
			if(Filtri.FILTRO_CONNETTORE_TIPO_VALORE_IM.equals(filtroConnettoreTipo)) {
				tipoConnettoreIntegrationManager = true;
			}
			else {
				tipoConnettore = TipiConnettore.toEnumFromName(filtroConnettoreTipo);
				if(tipoConnettore!=null) {
					endpointType = (TipiConnettore.CUSTOM.equals(tipoConnettore)) ? filtroConnettoreTipoPlugin : tipoConnettore.getNome();
				}
			}
		}
		
//		String search = ServletUtils.getSearchFromSession(ricerca, idLista);
		
		this.log.debug("filtroConnettoreNome : " + filtroConnettoreNome);
		this.log.debug("filtroConnettoreFiltro : " + filtroConnettoreFiltro);
		this.log.debug("filtroConnettoreTipo : " + filtroConnettoreTipo);
		this.log.debug("filtroConnettoreTokenPolicy : " + filtroConnettoreTokenPolicy);
		this.log.debug("filtroConnettoreEndpoint : " + filtroConnettoreEndpoint);
		this.log.debug("filtroConnettoreKeystore : " + filtroConnettoreKeystore);
		this.log.debug("filtroConnettoreTipoPlugin : " + filtroConnettoreTipoPlugin);
		
		List<PortaApplicativaServizioApplicativo> listaFiltrata = new ArrayList<PortaApplicativaServizioApplicativo>();
		
		if(StringUtils.isNotBlank(filtroConnettoreNome) || StringUtils.isNotBlank(filtroConnettoreFiltro) || joinConnettore) {
			
			List<String> mapSA_ok = null;
			List<String> mapSA_filtrati = null;
			if(StringUtils.isNotBlank(filtroConnettoreFiltro) || joinConnettore) {
				mapSA_ok = new ArrayList<String>();
				mapSA_filtrati = new ArrayList<String>();
			}
			
			for (PortaApplicativaServizioApplicativo paSA : lista) {
			
				// filtro nome
				if(StringUtils.isNotBlank(filtroConnettoreNome)) {
					String nome = this.getLabelNomePortaApplicativaServizioApplicativo(paSA);
					if(nome != null) {
						if(!nome.toLowerCase().contains(filtroConnettoreNome.toLowerCase())) {
							continue;
						}
					}
				}
				
				// filtro
				if(StringUtils.isNotBlank(filtroConnettoreFiltro)) {
					if(paSA.getDatiConnettore()==null || paSA.getDatiConnettore().sizeFiltroList()<=0) {
						mapSA_filtrati.add(paSA.getNome());
						continue;
					}
					boolean find = false;
					for (String filtro : paSA.getDatiConnettore().getFiltroList()) {
						if(filtro!=null && filtro.toLowerCase().contains(filtroConnettoreFiltro.toLowerCase())) {
							find=true;
						}
					}
					if(!find) {
						mapSA_filtrati.add(paSA.getNome());
						continue;
					}
				}
				
				if(joinConnettore) {
				
					if(mapSA_filtrati.contains(paSA.getNome())) {
						continue;
					}
					if(!mapSA_ok.contains(paSA.getNome())) {
						
						IDServizioApplicativo idSA = new IDServizioApplicativo();
						idSA.setNome(paSA.getNome());
						idSA.setIdSoggettoProprietario(idSoggettoProprietario);
						ServizioApplicativo sa = this.saCore.getServizioApplicativo(idSA);
						Connettore connettore = null;
						if(sa.getInvocazioneServizio()!=null) {
							connettore = sa.getInvocazioneServizio().getConnettore();
						}
						
						if(connettore==null) {
							mapSA_filtrati.add(paSA.getNome());
							continue;
						}
						
						if(endpointType!=null) {
							if(connettore.getTipo()==null || !connettore.getTipo().equalsIgnoreCase(endpointType)) {
								mapSA_filtrati.add(paSA.getNome());
								continue;
							}
						}
						else if(tipoConnettore!=null) {
							if(TipiConnettore.CUSTOM.equals(tipoConnettore)) {
								TipiConnettore[] tipi = TipiConnettore.values();
								boolean find = false;
								for (TipiConnettore tipiConnettore : tipi) {
									if(tipiConnettore.getNome().equalsIgnoreCase(connettore.getTipo())) {
										find=true;
										break;
									}
								}
								if(!find) {
									mapSA_filtrati.add(paSA.getNome());
									continue;
								}
							}
						}
						
						if(tipoConnettoreIntegrationManager) {
							if(sa.getInvocazioneServizio()==null || sa.getInvocazioneServizio().getGetMessage()==null ||
									!StatoFunzionalita.ABILITATO.equals(sa.getInvocazioneServizio().getGetMessage())) {
								mapSA_filtrati.add(paSA.getNome());
								continue;
							}
						}
						
						if(filtroConnettoreTokenPolicy!=null) {
							String valoreProperty = readValueFromProperties(connettore, CostantiDB.CONNETTORE_TOKEN_POLICY);
							if(!filtroConnettoreTokenPolicy.equalsIgnoreCase(valoreProperty)) {
								mapSA_filtrati.add(paSA.getNome());
								continue;
							}
						}
						
						if(filtroConnettoreEndpoint!=null) {
							boolean find = false;
							if((tipoConnettore==null || TipiConnettore.HTTP.equals(tipoConnettore))) {
								String valoreProperty = readValueFromProperties(connettore, CostantiDB.CONNETTORE_HTTP_LOCATION);
								if(valoreProperty!=null && valoreProperty.toLowerCase().contains(filtroConnettoreEndpoint.toLowerCase())) {
									find=true;
								}
							}
							if(!find) {
								if((tipoConnettore==null || TipiConnettore.HTTPS.equals(tipoConnettore))) {
									String valoreProperty = readValueFromProperties(connettore, CostantiDB.CONNETTORE_HTTPS_LOCATION);
									if(valoreProperty!=null && valoreProperty.toLowerCase().contains(filtroConnettoreEndpoint.toLowerCase())) {
										find=true;
									}
								}
							}
							if(!find) {
								//if((tipoConnettore==null || TipiConnettore.FILE.equals(tipoConnettore))) {
								if(tipoConnettore!=null && TipiConnettore.FILE.equals(tipoConnettore)) {
									String valoreProperty = readValueFromProperties(connettore, CostantiDB.CONNETTORE_FILE_REQUEST_OUTPUT_FILE);
									if(valoreProperty!=null && valoreProperty.toLowerCase().contains(filtroConnettoreEndpoint.toLowerCase())) {
										find=true;
									}
									if(!find) {
										valoreProperty = readValueFromProperties(connettore, CostantiDB.CONNETTORE_FILE_REQUEST_OUTPUT_FILE_HEADERS);
										if(valoreProperty!=null && valoreProperty.toLowerCase().contains(filtroConnettoreEndpoint.toLowerCase())) {
											find=true;
										}
									}
									if(!find) {
										valoreProperty = readValueFromProperties(connettore, CostantiDB.CONNETTORE_FILE_RESPONSE_INPUT_FILE);
										if(valoreProperty!=null && valoreProperty.toLowerCase().contains(filtroConnettoreEndpoint.toLowerCase())) {
											find=true;
										}
									}
									if(!find) {
										valoreProperty = readValueFromProperties(connettore, CostantiDB.CONNETTORE_FILE_RESPONSE_INPUT_FILE_HEADERS);
										if(valoreProperty!=null && valoreProperty.toLowerCase().contains(filtroConnettoreEndpoint.toLowerCase())) {
											find=true;
										}
									}
								}
							}
							if(!find) {
								//if((tipoConnettore==null || TipiConnettore.JMS.equals(tipoConnettore))) {
								if(tipoConnettore!=null && TipiConnettore.JMS.equals(tipoConnettore)) {
									String valoreProperty = readValueFromProperties(connettore, CostantiDB.CONNETTORE_JMS_NOME);
									if(valoreProperty!=null && valoreProperty.toLowerCase().contains(filtroConnettoreEndpoint.toLowerCase())) {
										find=true;
									}
									if(!find) {
										valoreProperty = readValueFromProperties(connettore, CostantiDB.CONNETTORE_JMS_CONTEXT_JAVA_NAMING_PROVIDER_URL);
										if(valoreProperty!=null && valoreProperty.toLowerCase().contains(filtroConnettoreEndpoint.toLowerCase())) {
											find=true;
										}
									}
									if(!find) {
										valoreProperty = readValueFromProperties(connettore, CostantiDB.CONNETTORE_JMS_CONNECTION_FACTORY);
										if(valoreProperty!=null && valoreProperty.toLowerCase().contains(filtroConnettoreEndpoint.toLowerCase())) {
											find=true;
										}
									}
									if(!find) {
										valoreProperty = readValueFromProperties(connettore, CostantiDB.CONNETTORE_JMS_CONTEXT_JAVA_NAMING_FACTORY_URL_PKG);
										if(valoreProperty!=null && valoreProperty.toLowerCase().contains(filtroConnettoreEndpoint.toLowerCase())) {
											find=true;
										}
									}
									if(!find) {
										valoreProperty = readValueFromProperties(connettore, CostantiDB.CONNETTORE_JMS_CONTEXT_JAVA_NAMING_FACTORY_INITIAL);
										if(valoreProperty!=null && valoreProperty.toLowerCase().contains(filtroConnettoreEndpoint.toLowerCase())) {
											find=true;
										}
									}
								}
							}
							if(!find) {
								mapSA_filtrati.add(paSA.getNome());
								continue;
							}
						}
						
						if(filtroConnettoreKeystore!=null &&
								(tipoConnettore==null || TipiConnettore.HTTPS.equals(tipoConnettore))
								) {
							boolean find = false;
							String valoreProperty = readValueFromProperties(connettore, CostantiDB.CONNETTORE_HTTPS_KEY_STORE_LOCATION);
							if(valoreProperty!=null && valoreProperty.toLowerCase().contains(filtroConnettoreKeystore.toLowerCase())) {
								find=true;
							}
							if(!find) {
								valoreProperty = readValueFromProperties(connettore, CostantiDB.CONNETTORE_HTTPS_TRUST_STORE_LOCATION);
								if(valoreProperty!=null && valoreProperty.toLowerCase().contains(filtroConnettoreKeystore.toLowerCase())) {
									find=true;
								}
							}
							if(!find) {
								mapSA_filtrati.add(paSA.getNome());
								continue;
							}
						}
						
					}
					
					mapSA_ok.add(paSA.getNome());
				}
								
				listaFiltrata.add(paSA);
			}
			
			return listaFiltrata;
		}
		
		return lista;
	}
	
	private String readValueFromProperties(Connettore connettore, String nomeProprieta) {
		for (int i = 0; i < connettore.sizePropertyList(); i++) {
			String nomeProperty = connettore.getProperty(i).getNome();
			if (nomeProperty.equals(nomeProprieta)){
				String valoreProperty = connettore.getProperty(i).getValore();
				return valoreProperty;
			}
		}
		return null;
	}
	
	private PortaApplicativaServizioApplicativo getFiltroNomeConnettore(String filtroNome, List<PortaApplicativaServizioApplicativo> lista) {
		
		for (PortaApplicativaServizioApplicativo paSA : lista) {
			String nome = this.getLabelNomePortaApplicativaServizioApplicativo(paSA);
			
			if(nome != null) {
				if(nome.toLowerCase().equals(filtroNome.toLowerCase()))
					return paSA;
			}
		}
		
		for (PortaApplicativaServizioApplicativo paSA : lista) {
			String nome = this.getLabelNomePortaApplicativaServizioApplicativo(paSA);
			
			if(nome != null) {
				if(nome.toLowerCase().contains(filtroNome.toLowerCase()))
					return paSA;
			}
		}
		
		return null;
	}
	
	public Vector<DataElement> addConnettoriMultipliToDati(Vector<DataElement> dati, TipoOperazione tipoOp,
			TipoBehaviour beaBehaviourType, String nomeSAConnettore,
			String nome, String descrizione, String stato, boolean behaviourConFiltri, String filtri, String vDatiGenerali, String vDescrizione, String vFiltri, String vConnettore,
			PortaApplicativaServizioApplicativo paSA) {
		
		boolean visualizzaDatiGenerali = ServletUtils.isCheckBoxEnabled(vDatiGenerali);
		boolean visualizzaDescrizione = ServletUtils.isCheckBoxEnabled(vDescrizione);
		boolean visualizzaFiltri = ServletUtils.isCheckBoxEnabled(vFiltri);
		
		DataElement de = new DataElement();
		
		if(tipoOp.equals(TipoOperazione.ADD) || (tipoOp.equals(TipoOperazione.CHANGE) && (visualizzaDatiGenerali || visualizzaDescrizione || visualizzaFiltri))) {
			if(tipoOp.equals(TipoOperazione.ADD)) {
				de.setLabel(PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_DATI_GENERALI);
			}
			else {
				if(visualizzaDescrizione) {
					de.setLabel(PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_DESCRIZIONE);
				}
				else if(visualizzaFiltri) {
					de.setLabel(PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_FILTRI);
				}
				else {
					de.setLabel(PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_NOME);
				}
			}
			de.setType(DataElementType.TITLE);
			dati.add(de);
		}
		
		de = new DataElement();
		de.setLabel("");
		de.setName(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_NOME_SA);
		de.setType(DataElementType.HIDDEN);
		de.setValue(nomeSAConnettore);
		dati.add(de);
		
		// Nome
		de = new DataElement();
		de.setLabel(PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_NOME);
		de.setName(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_NOME);
		
		
		if(tipoOp.equals(TipoOperazione.ADD) || (tipoOp.equals(TipoOperazione.CHANGE) && visualizzaDatiGenerali)) {
			de.setType(DataElementType.TEXT_EDIT);
			de.setValue(nome);
			de.setRequired(true);
		} else {
			de.setType(DataElementType.HIDDEN);
			de.setValue(nome);
		}
		dati.add(de);
		
		// Stato
		de = new DataElement();
		de.setLabel(PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_STATO);
		de.setName(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_STATO);
		
		if(tipoOp.equals(TipoOperazione.ADD)) {
			de.setType(DataElementType.SELECT);
			//String [] values = { StatoFunzionalita.ABILITATO.getValue() , StatoFunzionalita.DISABILITATO.getValue()};
			String [] values = { StatoFunzionalita.ABILITATO.getValue() , 
					PorteApplicativeCostanti.VALUE_PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_STATO_ABILITATO_MA_NON_SCHEDULATO,
					StatoFunzionalita.DISABILITATO.getValue()};
			String [] labels = { StatoFunzionalita.ABILITATO.getValue() , 
					PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_STATO_ABILITATO_MA_NON_SCHEDULATO,
					StatoFunzionalita.DISABILITATO.getValue()};
			de.setValues(values);
			de.setLabels(labels);
			de.setSelected(stato);
		} else {
			de.setType(DataElementType.HIDDEN);
			de.setValue(stato);
		}
		dati.add(de);
		
		// Descrizione
		de = new DataElement();
		de.setLabel(PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_DESCRIZIONE);
		de.setName(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_DESCRIZIONE);
		
			
		if(tipoOp.equals(TipoOperazione.ADD) || (tipoOp.equals(TipoOperazione.CHANGE) && visualizzaDescrizione)) {
			de.setType(DataElementType.TEXT_AREA);
			de.setValue(descrizione);
		} else {
			de.setType(DataElementType.HIDDEN);
			de.setValue(descrizione);
		}
		dati.add(de);
		
		// filtri
		de = new DataElement();
		de.setLabel(PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_FILTRI);
		de.setName(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_FILTRI);
		boolean showFiltri = behaviourConFiltri;
		if(showFiltri) {
			if(TipoBehaviour.CONSEGNA_CON_NOTIFICHE.equals(beaBehaviourType) && paSA!=null && this.isConnettoreDefault(paSA)) {
				showFiltri = false;
			}
		}
		if(showFiltri) {
			if(tipoOp.equals(TipoOperazione.ADD) || (tipoOp.equals(TipoOperazione.CHANGE) && visualizzaFiltri)) {
				de.setType(DataElementType.TEXT_EDIT);
				de.enableTags();
				de.setValue(filtri);
			} else {
				de.setType(DataElementType.HIDDEN);
				de.setValue(filtri);
			}
		} else {
			de.setType(DataElementType.HIDDEN);
			de.setValue(filtri);
		}
		dati.add(de);
		
		dati = this.addInformazioniGruppiAsHiddenToDati(tipoOp, dati, vDatiGenerali, vDescrizione, vConnettore, vFiltri);
		
		return dati;
	}
	
	public Vector<DataElement> addInformazioniGruppiAsHiddenToDati(TipoOperazione tipoOp, Vector<DataElement> dati,	
			String visualizzaDatiGenerali, String visualizzaDescrizione, String visualizzaConnettore, String visualizzaFiltri) {
		
//		Parameter pConfigurazioneDatiGenerali = new Parameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_CONFIGURAZIONE_DATI_GENERALI, visualizzaDatiGenerali);
//		Parameter pConfigurazioneDescrizione = new Parameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_CONFIGURAZIONE_DESCRIZIONE, visualizzaDescrizione);
//		Parameter pConfigurazioneFiltro = new Parameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_CONFIGURAZIONE_FILTRO, visualizzaFiltri);
//		Parameter pConfigurazioneConnettore = new Parameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_CONFIGURAZIONE_CONNETTORE, visualizzaConnettore);
		
		if(visualizzaDatiGenerali != null) {
			DataElement de = new DataElement();
			de.setName(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_CONFIGURAZIONE_DATI_GENERALI);
			de.setType(DataElementType.HIDDEN);
			de.setValue(visualizzaDatiGenerali);
			dati.add(de);
		}
		
		if(visualizzaDescrizione != null) {
			DataElement de = new DataElement();
			de.setName(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_CONFIGURAZIONE_DESCRIZIONE);
			de.setType(DataElementType.HIDDEN);
			de.setValue(visualizzaDescrizione);
			dati.add(de);
		}
		
		if(visualizzaFiltri != null) {
			DataElement de = new DataElement();
			de.setName(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_CONFIGURAZIONE_FILTRO);
			de.setType(DataElementType.HIDDEN);
			de.setValue(visualizzaFiltri);
			dati.add(de);
		}
		
		if(visualizzaConnettore != null) {
			DataElement de = new DataElement();
			de.setName(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_CONFIGURAZIONE_CONNETTORE);
			de.setType(DataElementType.HIDDEN);
			de.setValue(visualizzaConnettore);
			dati.add(de);
		}
		
		return dati;
	}
	
	public Vector<DataElement> addConnettoriMultipliLoadBalanceToDati(Vector<DataElement> dati, TipoOperazione tipoOp,
			TipoBehaviour beaBehaviourType, String nomeSAConnettore, String peso) {
		
		DataElement de = new DataElement();
		
		de.setLabel(PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_LOAD_BALANCE);
		de.setType(DataElementType.TITLE);
		dati.add(de);
		
		de = new DataElement();
		de.setLabel("");
		de.setName(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_NOME_SA);
		de.setType(DataElementType.HIDDEN);
		de.setValue(nomeSAConnettore);
		dati.add(de);
		
		// Nome
		de = new DataElement();
		de.setLabel(PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_LOAD_BALANCE_WEIGHT);
		de.setName(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_LOAD_BALANCE_WEIGHT);
		
		de.setType(DataElementType.NUMBER);
		de.setValue(peso);
		de.setRequired(true);
		de.setMinValue(1);
		dati.add(de);
		
		return dati;
	}
	
	public boolean connettoriMultipliCheckData(TipoOperazione tipoOp, PortaApplicativa pa , TipoBehaviour beaBehaviourType, String nomeSAConnettore,
			String oldNome, String nome, String descrizione, String stato, String filtri, String vDatiGenerali, String vDescrizione, String vFiltri, String vConnettore,
			String getmsg, String getmsgUsername, String getmsgPassword, ServizioApplicativo oldSA) throws Exception{
		try{
			boolean visualizzaDatiGenerali = ServletUtils.isCheckBoxEnabled(vDatiGenerali);
			if(tipoOp.equals(TipoOperazione.ADD) || (tipoOp.equals(TipoOperazione.CHANGE) && visualizzaDatiGenerali)) {
				
				if(StringUtils.isEmpty(nome)) {
					this.pd.setMessage(MessageFormat.format(PorteApplicativeCostanti.MESSAGGIO_ERRORE_DATI_INCOMPLETI_E_NECESSARIO_INDICARE_XX,
							PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_NOME));
					return false;
				}
				
				// Controllo che non ci siano spazi nei campi di testo
				if ((nome.indexOf(" ") != -1)) {
					this.pd.setMessage(CostantiControlStation.MESSAGGIO_ERRORE_NON_INSERIRE_SPAZI_NEI_CAMPI_DI_TESTO);
					return false;
				}
				
				// Check Lunghezza
				if(this.checkLength255(nome, PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_NOME)==false) {
					return false;
				}
				
				if(StringUtils.isEmpty(stato)) {
					this.pd.setMessage(MessageFormat.format(PorteApplicativeCostanti.MESSAGGIO_ERRORE_DATI_INCOMPLETI_E_NECESSARIO_INDICARE_XX,
							PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_STATO));
					return false;
				}
			}
			
			boolean visualizzaDescrizione = ServletUtils.isCheckBoxEnabled(vDescrizione);
			
			if(tipoOp.equals(TipoOperazione.ADD) || (tipoOp.equals(TipoOperazione.CHANGE) && visualizzaDescrizione)) {
				if(StringUtils.isNotEmpty(descrizione)) {
					if(this.checkLength4000(nome, PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_DESCRIZIONE)==false) {
						return false;
					}
				}
			}
			
			// check univocita' del nome
			if(tipoOp.equals(TipoOperazione.ADD) || (tipoOp.equals(TipoOperazione.CHANGE) && visualizzaDatiGenerali)) {
				
				List<PortaApplicativaServizioApplicativo> servizioApplicativoList = pa.getServizioApplicativoList();
				
				if(tipoOp.equals(TipoOperazione.ADD)) {
					for (PortaApplicativaServizioApplicativo paSA : servizioApplicativoList) {
						String nomePaSA = getLabelNomePortaApplicativaServizioApplicativo(paSA);
						if(nome.equals(nomePaSA)) {
							this.pd.setMessage(MessageFormat.format(PorteApplicativeCostanti.MESSAGGIO_ERRORE_CONNETTORI_MULTIPLI_NOME_GIA_UTILIZZATO,
									nome));
							return false;
						}
					}
				}
				
				// solo se ho cambiato il nome
				if(tipoOp.equals(TipoOperazione.CHANGE) && !nome.equals(oldNome)) {
					for (PortaApplicativaServizioApplicativo paSA : servizioApplicativoList) {
						String nomePaSA = getLabelNomePortaApplicativaServizioApplicativo(paSA);
						if(nome.equals(nomePaSA)) {
							this.pd.setMessage(MessageFormat.format(PorteApplicativeCostanti.MESSAGGIO_ERRORE_CONNETTORI_MULTIPLI_NOME_GIA_UTILIZZATO,
									nome));
							return false;
						}
					}
					
					// check utilizzo del nome nel connettore nei criteri di applicabilita delle trasformazioni
					// NON serve: nei criteri di applicabilità ci finisce il nome del servizio applicativo
					/*if(pa.getTrasformazioni()!=null && pa.getTrasformazioni().sizeRegolaList()>0){
						
						String check = null;
						for (PortaApplicativaServizioApplicativo paSA : servizioApplicativoList) {
							String nomePaSA = getLabelNomePortaApplicativaServizioApplicativo(paSA);
							if(oldNome.equals(nomePaSA)) {
								check = paSA.getNome();
								break;
							}
						}
						
						for (TrasformazioneRegola tr : pa.getTrasformazioni().getRegolaList()) {
							if(tr.getApplicabilita()!=null && tr.getApplicabilita().sizeConnettoreList()>0 && tr.getApplicabilita().getConnettoreList()!=null){
								if(tr.getApplicabilita().getConnettoreList().contains(check)) {
									this.pd.setMessage(MessageFormat.format(PorteApplicativeCostanti.MESSAGGIO_ERRORE_CONNETTORI_MULTIPLI_NOME_UTILIZZATO_CRITERI_APPLICABILITA_TRASFORMAZIONI,
											oldNome, tr.getNome()));
									return false;
								}
							}
						}
					}*/
				}
			}
			
			// check univocita' applicativo server
			
			if(tipoOp.equals(TipoOperazione.ADD)) {
				String erogazioneServizioApplicativoServerEnabledS = this.getParameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_ABILITA_USO_APPLICATIVO_SERVER);
				boolean erogazioneServizioApplicativoServerEnabled = ServletUtils.isCheckBoxEnabled(erogazioneServizioApplicativoServerEnabledS);
				String erogazioneServizioApplicativoServer = this.getParameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_ID_APPLICATIVO_SERVER);
				if(erogazioneServizioApplicativoServerEnabled) {
					for (PortaApplicativaServizioApplicativo paSA : pa.getServizioApplicativoList()) {
						if(paSA.getNome().equals(erogazioneServizioApplicativoServer)) {
							String nomeConfigurazione = getLabelNomePortaApplicativaServizioApplicativo(paSA);
							this.pd.setMessage(MessageFormat.format(PorteApplicativeCostanti.MESSAGGIO_ERRORE_CONNETTORI_MULTIPLI_APPLICATIVO_SERVER_GIA_UTILIZZATO,
									erogazioneServizioApplicativoServer,nomeConfigurazione));
							return false;
						}
					}
				}
			}
			
			if(tipoOp.equals(TipoOperazione.CHANGE)) {
				String erogazioneServizioApplicativoServerEnabledS = this.getParameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_ABILITA_USO_APPLICATIVO_SERVER);
				boolean erogazioneServizioApplicativoServerEnabled = ServletUtils.isCheckBoxEnabled(erogazioneServizioApplicativoServerEnabledS);
				String erogazioneServizioApplicativoServer = this.getParameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_ID_APPLICATIVO_SERVER);
				if(erogazioneServizioApplicativoServerEnabled) {
					for (PortaApplicativaServizioApplicativo paSA : pa.getServizioApplicativoList()) {
						// usato da un altro PASA che non sia quello in corso di modifica
						if(!paSA.getNome().equals(nomeSAConnettore) &&   paSA.getNome().equals(erogazioneServizioApplicativoServer) ) {
							String nomeConfigurazione = getLabelNomePortaApplicativaServizioApplicativo(paSA);
							this.pd.setMessage(MessageFormat.format(PorteApplicativeCostanti.MESSAGGIO_ERRORE_CONNETTORI_MULTIPLI_APPLICATIVO_SERVER_GIA_UTILIZZATO,
									erogazioneServizioApplicativoServer,nomeConfigurazione));
							return false;
						}
					}
				}
			}
						
			// check univocita' filtro
			if(TipoBehaviour.CONSEGNA_CONDIZIONALE.equals(beaBehaviourType) &&
					!this.porteApplicativeCore.isConnettoriMultipliConsegnaCondizionaleStessFiltroPermesso()) {
				
				if(StringUtils.isNotEmpty(filtri)) {
					List<String> filtriV = Arrays.asList(filtri.split(","));
					
					for (String filtro : filtriV) {
						
						if(tipoOp.equals(TipoOperazione.ADD)) {
							for (PortaApplicativaServizioApplicativo paSA : pa.getServizioApplicativoList()) {
								if(paSA.getDatiConnettore()!=null && paSA.getDatiConnettore().sizeFiltroList()>0) {
									for (String filtroCheck : paSA.getDatiConnettore().getFiltroList()) {
										if(filtroCheck.equals(filtro)) {
											String nomeConfigurazione = getLabelNomePortaApplicativaServizioApplicativo(paSA);
											this.pd.setMessage(MessageFormat.format(PorteApplicativeCostanti.MESSAGGIO_ERRORE_CONNETTORI_MULTIPLI_FILTRO_GIA_UTILIZZATO,
													filtro,nomeConfigurazione));
											return false;
										}			
									}
								}
							}
						}
						
						if(tipoOp.equals(TipoOperazione.CHANGE)) {
							for (PortaApplicativaServizioApplicativo paSA : pa.getServizioApplicativoList()) {
								if(paSA.getDatiConnettore()!=null && paSA.getDatiConnettore().sizeFiltroList()>0) {
									for (String filtroCheck : paSA.getDatiConnettore().getFiltroList()) {
										// usato da un altro PASA che non sia quello in corso di modifica
										if(!paSA.getNome().equals(nomeSAConnettore) && filtroCheck.equals(filtro) ) {
											String nomeConfigurazione = getLabelNomePortaApplicativaServizioApplicativo(paSA);
											this.pd.setMessage(MessageFormat.format(PorteApplicativeCostanti.MESSAGGIO_ERRORE_CONNETTORI_MULTIPLI_FILTRO_GIA_UTILIZZATO,
													filtro,nomeConfigurazione));
											return false;
										}
									}
								}
							}
						}
						
					}
				}
					
			}
			
			// Controllo che i campi DataElementType.SELECT abbiano uno dei valori ammessi
			if (!getmsg.equals(CostantiConfigurazione.ABILITATO.toString()) && !getmsg.equals(CostantiConfigurazione.DISABILITATO.toString())) {
				this.pd.setMessage("Servizio '"+ServiziApplicativiCostanti.LABEL_SERVIZIO_MESSAGE_BOX+"' dev'essere "+CostantiConfigurazione.ABILITATO+" o "+CostantiConfigurazione.DISABILITATO);
				return false;
			}
			if (getmsg!=null && getmsg.equals(CostantiConfigurazione.ABILITATO.toString()) ){
				
				boolean add = tipoOp.equals(TipoOperazione.ADD);
				boolean encryptEnabled = this.saCore.isApplicativiPasswordEncryptEnabled();
				
				boolean validaPassword = false;
				if(add || !encryptEnabled) {
					validaPassword = true;
				}
				else {
					boolean oldAbilitato = oldSA!=null && oldSA.getInvocazioneServizio()!=null && StatoFunzionalita.ABILITATO.equals(oldSA.getInvocazioneServizio().getGetMessage());
					if(oldAbilitato) {
						String changePwd = this.getParameter(ConnettoriCostanti.PARAMETRO_CREDENZIALI_AUTENTICAZIONE_CHANGE_PASSWORD);
						if(ServletUtils.isCheckBoxEnabled(changePwd)) {
							validaPassword = true;
						}
					}
					else {
						validaPassword = true;
					}
				}
				
				boolean passwordEmpty = false;
				if(validaPassword) {
					if(getmsgPassword==null || getmsgPassword.equals("")) {
						passwordEmpty = true;
					}
				}
				
				if(getmsgUsername==null || "".equals(getmsgUsername)) {
					this.pd.setMessage("Dati incompleti. E' necessario indicare 'Username' per il servizio '"+ServiziApplicativiCostanti.LABEL_SERVIZIO_MESSAGE_BOX+"'");
					return false;
				}
				if(passwordEmpty) {
					this.pd.setMessage("Dati incompleti. E' necessario indicare 'Password' per il servizio '"+ServiziApplicativiCostanti.LABEL_SERVIZIO_MESSAGE_BOX+"'");
					return false;
				}
				if (((getmsgUsername.indexOf(" ") != -1) || (validaPassword && getmsgPassword.indexOf(" ") != -1))) {
					this.pd.setMessage("Non inserire spazi nei campi di testo");
					return false;
				}
				
				if(validaPassword) {
					PasswordVerifier passwordVerifier = this.saCore.getApplicativiPasswordVerifier();
					if(passwordVerifier!=null){
						StringBuilder motivazioneErrore = new StringBuilder();
						if(passwordVerifier.validate(getmsgUsername, getmsgPassword, motivazioneErrore)==false){
							this.pd.setMessage(motivazioneErrore.toString());
							return false;
						}
					}
				}
				
				// recupera lista servizi applicativi con stesse credenziali
				boolean checkPassword = this.saCore.isApplicativiCredenzialiBasicCheckUniqueUsePassword(); // la password non viene utilizzata per riconoscere se l'username e' già utilizzato.
				List<ServizioApplicativo> saList = this.saCore.servizioApplicativoWithCredenzialiBasicList(getmsgUsername, getmsgPassword, checkPassword);

				for (int i = 0; i < saList.size(); i++) {
					ServizioApplicativo sa = saList.get(i);

					if(oldSA!=null && oldSA.getId().longValue() == sa.getId().longValue()) {
						continue;
					}

					// Messaggio di errore
					IDSoggetto idSoggettoProprietario = new IDSoggetto(sa.getTipoSoggettoProprietario(), sa.getNomeSoggettoProprietario());
					String labelSoggetto = this.getLabelNomeSoggetto(idSoggettoProprietario);
					if(sa.getTipo()!=null && StringUtils.isNotEmpty(sa.getTipo())) {
						this.pd.setMessage("L'applicativo "+sa.getNome()+" (soggetto: "+labelSoggetto+") possiede già l'utente (http-basic) indicato");
					}
					else {
						IDServizioApplicativo idSA = new IDServizioApplicativo();
						idSA.setIdSoggettoProprietario(idSoggettoProprietario);
						idSA.setNome(sa.getNome());
						List<IDPortaApplicativa> list = this.porteApplicativeCore.porteApplicativeWithApplicativoErogatore(idSA);
						String labelErogazione = sa.getNome();
						if(list!=null && !list.isEmpty()) {
							try {
								PortaApplicativa paFound = this.porteApplicativeCore.getPortaApplicativa(list.get(0));
								MappingErogazionePortaApplicativa mappingPA = this.porteApplicativeCore.getMappingErogazionePortaApplicativa(paFound);
								labelErogazione = this.getLabelIdServizio(mappingPA.getIdServizio());
								if(!mappingPA.isDefault()) {
									labelErogazione = labelErogazione+" (gruppo:"+mappingPA.getDescrizione()+")";
								}
							}catch(Throwable t) {
								this.log.error("Errore durante l'identificazione dell'erogazione: "+t.getMessage(),t);
							}
						}
						this.pd.setMessage("L'erogazione "+labelErogazione+" possiede già l'utente (http-basic) indicato per il servizio '"+ServiziApplicativiCostanti.LABEL_SERVIZIO_MESSAGE_BOX+"'");
					}
					return false;
				}
			}
			
			
		} catch (Exception e) {
			this.log.error("Exception: " + e.getMessage(), e);
			throw new Exception(e);
		}
		return true;
	}
	
	public boolean connettoriMultipliLoadBalanceCheckData(TipoOperazione tipoOp, PortaApplicativa pa , TipoBehaviour beaBehaviourType, String nomeSAConnettore, String peso) throws Exception{
		try{
			
			if(StringUtils.isEmpty(peso)) {
				this.pd.setMessage(MessageFormat.format(PorteApplicativeCostanti.MESSAGGIO_ERRORE_DATI_INCOMPLETI_E_NECESSARIO_INDICARE_XX,
						PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_LOAD_BALANCE_WEIGHT));
				return false;
			}
			
			int w = -1;
			try {
				w = Integer.parseInt(peso);
			}catch (Exception e) {
				this.pd.setMessage(MessageFormat.format(PorteApplicativeCostanti.MESSAGGIO_ERRRORE_FORMATO_NUMERICO_XX_NON_VALIDO,
						PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_LOAD_BALANCE_WEIGHT));
				return false;
			}
			
			if(w < 1) {
				this.pd.setMessage(MessageFormat.format(PorteApplicativeCostanti.MESSAGGIO_ERRRORE_MIN_XX_NON_VALIDO,
						PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_LOAD_BALANCE_WEIGHT, 1));
				return false;
			}
			
		} catch (Exception e) {
			this.log.error("Exception: " + e.getMessage(), e);
			throw new Exception(e);
		}
		return true;
	}	
	
	public String getLabelNomePortaApplicativaServizioApplicativo(String functionDi, String function, PortaApplicativaServizioApplicativo paSA) {
		boolean showGroup = true;
			String prefix = "";
			if(functionDi!=null) {
				prefix = functionDi;
				if(showGroup) {
					prefix = this.core.convertPrefixConfigDelConnettore(prefix);
				}
			}
					
		return prefix+this.core.getLabelGroup(this.getLabelNomePortaApplicativaServizioApplicativo(paSA));
	}
	
	public String getMessaggioConfermaModificaRegolaStatoConnettoreMultiplo(boolean fromAPI, PortaApplicativaServizioApplicativo paSA, boolean abilitazione, 
			boolean multiline,boolean listElement,
			boolean scheduling) throws DriverConfigurazioneException {
		boolean connettoreDefault = this.isConnettoreDefault(paSA);
		String nomeConnettore = this.getLabelNomePortaApplicativaServizioApplicativo(paSA);
		return this.getMessaggioConfermaModificaStatoConnettore(fromAPI, connettoreDefault, nomeConnettore, abilitazione, multiline, listElement, scheduling);
	}
	
	
	public String getMessaggioConfermaModificaStatoConnettore(boolean fromAPI, boolean isDefault, String connettore,
			boolean abilitazione, boolean multiline,boolean listElement,
			boolean scheduling) throws DriverConfigurazioneException {
		String pre = Costanti.HTML_MODAL_SPAN_PREFIX;
		String post = Costanti.HTML_MODAL_SPAN_SUFFIX;
		
		if(scheduling) {
			return pre + ( abilitazione ? MessageFormat.format(PorteApplicativeCostanti.MESSAGGIO_CONFERMA_ABILITAZIONE_SCHEDULING_CONNETTORE,connettore) : 
				MessageFormat.format(PorteApplicativeCostanti.MESSAGGIO_CONFERMA_DISABILITAZIONE_SCHEDULING_CONNETTORE,connettore) )  + post;
		}
		else {
			return pre + ( abilitazione ? MessageFormat.format(PorteApplicativeCostanti.MESSAGGIO_CONFERMA_ABILITAZIONE_CONNETTORE,connettore) : 
				MessageFormat.format(PorteApplicativeCostanti.MESSAGGIO_CONFERMA_DISABILITAZIONE_CONNETTORE,connettore) )  + post;
		}
	}
	
	
	public void preparePorteApplicativeConnettoriMultipliConfigPropList(String nomePorta, ISearch ricerca, List<Proprieta> lista) throws Exception {
		try {
			// prelevo il flag che mi dice da quale pagina ho acceduto la sezione delle porte delegate
			Integer parentPA = ServletUtils.getIntegerAttributeFromSession(PorteApplicativeCostanti.ATTRIBUTO_PORTE_APPLICATIVE_PARENT, this.session);
			String idAsps = this.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_ASPS);
			if(idAsps == null)
				idAsps = "";

			String id = this.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID);
			String idsogg = this.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_SOGGETTO);

			Parameter pIdPorta = new Parameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID, id);
			Parameter pNomePorta = new Parameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_NOME, nomePorta);
			Parameter pIdSogg = new Parameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_SOGGETTO, idsogg);
			Parameter pIdAsps = new Parameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_ASPS, idAsps);
			
			String idTabP = this.getParameter(CostantiControlStation.PARAMETRO_ID_TAB);
			Parameter pIdTab = new Parameter(CostantiControlStation.PARAMETRO_ID_TAB, idTabP != null ? idTabP : "");
			
			String accessoDaAPSParametro = this.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_CONNETTORE_DA_LISTA_APS);
			Parameter pAccessoDaAPS = new Parameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_CONNETTORE_DA_LISTA_APS, accessoDaAPSParametro != null ? accessoDaAPSParametro : "");
			String connettoreAccessoGruppi = this.getParameter(CostantiControlStation.PARAMETRO_VERIFICA_CONNETTORE_ACCESSO_DA_GRUPPI);
			Parameter pConnettoreAccessoDaGruppi = new Parameter(CostantiControlStation.PARAMETRO_VERIFICA_CONNETTORE_ACCESSO_DA_GRUPPI, connettoreAccessoGruppi);
			String connettoreRegistro = this.getParameter(CostantiControlStation.PARAMETRO_VERIFICA_CONNETTORE_REGISTRO);
			Parameter pConnettoreRegistro = new Parameter(CostantiControlStation.PARAMETRO_VERIFICA_CONNETTORE_REGISTRO, connettoreRegistro);
			String connettoreAccessoCM = this.getParameter(CostantiControlStation.PARAMETRO_VERIFICA_CONNETTORE_ACCESSO_DA_LISTA_CONNETTORI_MULTIPLI);
			Parameter pConnettoreAccessoCM = new Parameter(CostantiControlStation.PARAMETRO_VERIFICA_CONNETTORE_ACCESSO_DA_LISTA_CONNETTORI_MULTIPLI, connettoreAccessoCM);
			
			
			ServletUtils.addListElementIntoSession(this.session, PorteApplicativeCostanti.OBJECT_NAME_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_CONFIG_PROPERTIES,
					pIdPorta, pIdSogg, pIdAsps, pNomePorta, pConnettoreAccessoDaGruppi, pConnettoreRegistro, pAccessoDaAPS, pIdTab, pConnettoreAccessoCM);
			

			int idLista = Liste.PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_CONFIG_PROPRIETA;
			int limit = ricerca.getPageSize(idLista);
			int offset = ricerca.getIndexIniziale(idLista);
			String search = ServletUtils.getSearchFromSession(ricerca, idLista);

			this.pd.setIndex(offset);
			this.pd.setPageSize(limit);
			this.pd.setNumEntries(ricerca.getNumEntries(idLista));

			PortaApplicativa pa = this.porteApplicativeCore.getPortaApplicativa(Integer.parseInt(id));
			String idporta = pa.getNome();
			
			long idAspsLong = Long.parseLong(idAsps);
			AccordoServizioParteSpecifica asps = this.apsCore.getAccordoServizioParteSpecifica(idAspsLong);

			boolean accessoDaListaAPS = false;
			if(Costanti.CHECK_BOX_ENABLED_TRUE.equals(accessoDaAPSParametro)) {
				accessoDaListaAPS = true;
			}
			
			boolean isModalitaCompleta = this.isModalitaCompleta();
			Boolean vistaErogazioni = ServletUtils.getBooleanAttributeFromSession(ErogazioniCostanti.ASPS_EROGAZIONI_ATTRIBUTO_VISTA_EROGAZIONI, this.session);
			// setto la barra del titolo
			List<Parameter> lstParam = this.getTitoloPA(parentPA, idsogg, idAsps);
			
			String labelPerPorta = null;
			if(parentPA!=null && (parentPA.intValue() == PorteApplicativeCostanti.ATTRIBUTO_PORTE_APPLICATIVE_PARENT_CONFIGURAZIONE)) {
				if(accessoDaListaAPS) {
					if(!isModalitaCompleta) {
						if(vistaErogazioni != null && vistaErogazioni.booleanValue()) {
							labelPerPorta = PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_CONFIG;
						} else {
							labelPerPorta = PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_CONFIG_DI+
									this.getLabelIdServizio(asps);
						}
					}
					else {
						labelPerPorta = PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_CONFIG;
					}
				}
				else {
					labelPerPorta = this.porteApplicativeCore.getLabelRegolaMappingErogazionePortaApplicativa(
							PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_CONFIG_DI,
							PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_CONFIG,
							pa);
				}
			}
			else {
				labelPerPorta = PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_CONFIG_DI+idporta;
			}
			
			if(accessoDaListaAPS) {
				lstParam.remove(lstParam.size()-1);
			}
			
			lstParam.add(new Parameter(labelPerPorta,PorteApplicativeCostanti.SERVLET_NAME_PORTE_APPLICATIVE_CONFIGURAZIONE_CONNETTORI_MULTIPLI, pIdSogg, pIdPorta, pIdAsps,
					pConnettoreAccessoDaGruppi,	pConnettoreRegistro, pConnettoreAccessoCM));
			
			String labelPagLista = PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_CONFIG_PROPRIETA;
			
			this.pd.setSearchLabel(CostantiControlStation.LABEL_PARAMETRO_NOME);
			if(search.equals("")){
				this.pd.setSearchDescription("");
				lstParam.add(new Parameter(labelPagLista,null));
			}
			else{
				List<Parameter> listaParametriServletProprietaCustom = new ArrayList<>();
				listaParametriServletProprietaCustom.add(pIdSogg);
				listaParametriServletProprietaCustom.add(pIdPorta);
				listaParametriServletProprietaCustom.add(pNomePorta);
				listaParametriServletProprietaCustom.add(pIdAsps);
				listaParametriServletProprietaCustom.add(pIdTab);
				listaParametriServletProprietaCustom.add(pAccessoDaAPS);
				listaParametriServletProprietaCustom.add(pConnettoreAccessoDaGruppi);
				listaParametriServletProprietaCustom.add(pConnettoreRegistro);
				listaParametriServletProprietaCustom.add(pConnettoreAccessoCM);
				lstParam.add(new Parameter(labelPagLista, PorteApplicativeCostanti.SERVLET_NAME_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_CONFIG_PROPERTIES_LIST,	listaParametriServletProprietaCustom.toArray(new Parameter[listaParametriServletProprietaCustom.size()])));
				lstParam.add(new Parameter(PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_RISULTATI_RICERCA, null));

			}

			ServletUtils.setPageDataTitle(this.pd, lstParam.toArray(new Parameter[lstParam.size()]));

			// controllo eventuali risultati ricerca
			if (!search.equals("")) {
				ServletUtils.enabledPageDataSearch(this.pd, PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_CONFIG_PROPRIETA, search);
			}

			// setto le label delle colonne
			String[] labels = {CostantiControlStation.LABEL_PARAMETRO_NOME , CostantiControlStation.LABEL_PARAMETRO_VALORE };
			this.pd.setLabels(labels);

			// preparo i dati
			Vector<Vector<DataElement>> dati = new Vector<Vector<DataElement>>();

			if (lista != null) {
				Iterator<Proprieta> it = lista.iterator();
				while (it.hasNext()) {
					Proprieta proprieta = it.next();
		
					Vector<DataElement> e = new Vector<DataElement>();
		
					DataElement de = new DataElement();
					de.setValue(proprieta.getNome());
					de.setIdToRemove(proprieta.getId() + "");
					e.addElement(de);
					
					de = new DataElement();
					de.setValue(proprieta.getValore());
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
	
	public boolean proprietaConnettoriMultipliConfigCheckData(TipoOperazione tipoOp, String idPorta, String nome,String valore) throws Exception {
		try {
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
			
			// Check Lunghezza
			if(this.checkLength255(nome, PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_NOME)==false) {
				return false;
			}
			if(this.checkLength255(valore, PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_VALORE)==false) {
				return false;
			}
			
			// Se tipoOp = add, controllo che la property non sia gia'
			// stata
			// registrata per la porta applicativa
			if (tipoOp.equals(TipoOperazione.ADD)) {
				boolean giaRegistrato = false;
				PortaApplicativa pa = this.porteApplicativeCore.getPortaApplicativa(Integer.parseInt(idPorta));

				PortaApplicativaBehaviour behaviour = pa.getBehaviour();
				for (int i = 0; i < behaviour.sizeProprietaList(); i++) {
					Proprieta tmpProp = behaviour.getProprieta(i);
					if (nome.equals(tmpProp.getNome())) {
						giaRegistrato = true;
						break;
					}
				}

				if (giaRegistrato) {
					this.pd.setMessage(MessageFormat.format(PorteApplicativeCostanti.MESSAGGIO_ERRORE_LA_PROPERTY_XX_E_GIA_STATA_ASSOCIATA_ALLA_CONFIGURAZIONE, nome));	
					return false;
				}
			}

			return true;
		} catch (Exception e) {
			this.log.error("Exception: " + e.getMessage(), e);
			throw new Exception(e);
		}
	}
	
	public void preparePorteApplicativeConnettoriMultipliPropList(String nomePorta, ISearch ricerca, List<Proprieta> lista) throws Exception {
		try {
			// prelevo il flag che mi dice da quale pagina ho acceduto la sezione delle porte delegate
			Integer parentPA = ServletUtils.getIntegerAttributeFromSession(PorteApplicativeCostanti.ATTRIBUTO_PORTE_APPLICATIVE_PARENT, this.session);
			String idAsps = this.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_ASPS);
			if(idAsps == null)
				idAsps = "";

			String id = this.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID);
			String idsogg = this.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_SOGGETTO);
			String nomeSAConnettore = this.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_NOME_SA);

			Parameter pIdPorta = new Parameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID, id);
			Parameter pNomePorta = new Parameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_NOME, nomePorta);
			Parameter pIdSogg = new Parameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_SOGGETTO, idsogg);
			Parameter pIdAsps = new Parameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_ASPS, idAsps);
			Parameter pNomePASA = new Parameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_NOME_SA, nomeSAConnettore);
			
			String idTabP = this.getParameter(CostantiControlStation.PARAMETRO_ID_TAB);
			Parameter pIdTab = new Parameter(CostantiControlStation.PARAMETRO_ID_TAB, idTabP != null ? idTabP : "");
			
			String accessoDaAPSParametro = this.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_CONNETTORE_DA_LISTA_APS);
			Parameter pAccessoDaAPS = new Parameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_CONNETTORE_DA_LISTA_APS, accessoDaAPSParametro != null ? accessoDaAPSParametro : "");
			String connettoreAccessoGruppi = this.getParameter(CostantiControlStation.PARAMETRO_VERIFICA_CONNETTORE_ACCESSO_DA_GRUPPI);
			Parameter pConnettoreAccessoDaGruppi = new Parameter(CostantiControlStation.PARAMETRO_VERIFICA_CONNETTORE_ACCESSO_DA_GRUPPI, connettoreAccessoGruppi);
			String connettoreRegistro = this.getParameter(CostantiControlStation.PARAMETRO_VERIFICA_CONNETTORE_REGISTRO);
			Parameter pConnettoreRegistro = new Parameter(CostantiControlStation.PARAMETRO_VERIFICA_CONNETTORE_REGISTRO, connettoreRegistro);
			String connettoreAccessoCM = this.getParameter(CostantiControlStation.PARAMETRO_VERIFICA_CONNETTORE_ACCESSO_DA_LISTA_CONNETTORI_MULTIPLI);
			Parameter pConnettoreAccessoCM = new Parameter(CostantiControlStation.PARAMETRO_VERIFICA_CONNETTORE_ACCESSO_DA_LISTA_CONNETTORI_MULTIPLI, connettoreAccessoCM);
			
			
			ServletUtils.addListElementIntoSession(this.session, PorteApplicativeCostanti.OBJECT_NAME_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_PROPERTIES,
					pIdPorta, pIdSogg, pIdAsps, pNomePorta, pConnettoreAccessoDaGruppi, pConnettoreRegistro, pAccessoDaAPS, pIdTab, pConnettoreAccessoCM, pNomePASA);
			

			int idLista = Liste.PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_PROPRIETA;
			int limit = ricerca.getPageSize(idLista);
			int offset = ricerca.getIndexIniziale(idLista);
			String search = ServletUtils.getSearchFromSession(ricerca, idLista);

			this.pd.setIndex(offset);
			this.pd.setPageSize(limit);
			this.pd.setNumEntries(ricerca.getNumEntries(idLista));

			PortaApplicativa pa = this.porteApplicativeCore.getPortaApplicativa(Integer.parseInt(id));
			long idAspsLong = Long.parseLong(idAsps);
			AccordoServizioParteSpecifica asps = this.apsCore.getAccordoServizioParteSpecifica(idAspsLong);
			
			PortaApplicativaServizioApplicativo paSA = null;
			for (PortaApplicativaServizioApplicativo paSATmp : pa.getServizioApplicativoList()) {
				if(paSATmp.getNome().equals(nomeSAConnettore)) {
					paSA = paSATmp;					
				}
			}

			boolean accessoDaListaAPS = false;
			if(Costanti.CHECK_BOX_ENABLED_TRUE.equals(accessoDaAPSParametro)) {
				accessoDaListaAPS = true;
			}
			
			boolean isModalitaCompleta = this.isModalitaCompleta();
			Boolean vistaErogazioni = ServletUtils.getBooleanAttributeFromSession(ErogazioniCostanti.ASPS_EROGAZIONI_ATTRIBUTO_VISTA_EROGAZIONI, this.session);
			// setto la barra del titolo
			List<Parameter> lstParam = this.getTitoloPA(parentPA, idsogg, idAsps);
			
			String labelPerPorta = null;
			if(parentPA!=null && (parentPA.intValue() == PorteApplicativeCostanti.ATTRIBUTO_PORTE_APPLICATIVE_PARENT_CONFIGURAZIONE)) {
				if(accessoDaListaAPS) {
					if(!isModalitaCompleta) {
						if(vistaErogazioni != null && vistaErogazioni.booleanValue()) {
							labelPerPorta = PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_CONFIG;
						} else {
							labelPerPorta = PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_DI+
									this.getLabelIdServizio(asps);
						}
					}
					else {
						labelPerPorta = PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_CONFIG;
					}
				}
				else {
					labelPerPorta = this.porteApplicativeCore.getLabelRegolaMappingErogazionePortaApplicativa(
							PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_DI,
							PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_CONFIG,
							pa);
				}
			}
			else {
				labelPerPorta = PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_DI+nomePorta;
			}
			
			if(accessoDaListaAPS) {
				lstParam.remove(lstParam.size()-1);
			}
			List<Parameter> listaParametriServletConnettoriMultipliList = new ArrayList<>();
			listaParametriServletConnettoriMultipliList.add(pIdSogg);
			listaParametriServletConnettoriMultipliList.add(pIdPorta);
			listaParametriServletConnettoriMultipliList.add(pNomePorta);
			listaParametriServletConnettoriMultipliList.add(pIdAsps);
			listaParametriServletConnettoriMultipliList.add(pAccessoDaAPS);
			listaParametriServletConnettoriMultipliList.add(pConnettoreAccessoDaGruppi);
			listaParametriServletConnettoriMultipliList.add(pConnettoreRegistro);
			listaParametriServletConnettoriMultipliList.add(pConnettoreAccessoCM);
			
			lstParam.add(new Parameter(labelPerPorta,  PorteApplicativeCostanti.SERVLET_NAME_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_LIST, listaParametriServletConnettoriMultipliList.toArray(new Parameter[1])));
			
			String labelPagLista = this.getLabelNomePortaApplicativaServizioApplicativo(
					PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_PROPRIETA_DI,
					PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_PROPRIETA,
					paSA);
			
			this.pd.setSearchLabel(CostantiControlStation.LABEL_PARAMETRO_NOME);
			if(search.equals("")){
				this.pd.setSearchDescription("");
				lstParam.add(new Parameter(labelPagLista,null));
			}
			else{
				List<Parameter> listaParametriServletConnettoriMultipliProprietaList = new ArrayList<>();
				listaParametriServletConnettoriMultipliProprietaList.addAll(listaParametriServletConnettoriMultipliList);
				listaParametriServletConnettoriMultipliProprietaList.add(new Parameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_NOME_SA, nomeSAConnettore));
				
				lstParam.add(new Parameter(labelPagLista, PorteApplicativeCostanti.SERVLET_NAME_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_PROPERTIES_LIST,	listaParametriServletConnettoriMultipliProprietaList.toArray(new Parameter[listaParametriServletConnettoriMultipliProprietaList.size()])));
				
				lstParam.add(new Parameter(PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_RISULTATI_RICERCA, null));

			}

			ServletUtils.setPageDataTitle(this.pd, lstParam.toArray(new Parameter[lstParam.size()]));

			// controllo eventuali risultati ricerca
			if (!search.equals("")) {
				ServletUtils.enabledPageDataSearch(this.pd, PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_PROPRIETA, search);
			}

			// setto le label delle colonne
			String[] labels = {CostantiControlStation.LABEL_PARAMETRO_NOME , CostantiControlStation.LABEL_PARAMETRO_VALORE };
			this.pd.setLabels(labels);

			// preparo i dati
			Vector<Vector<DataElement>> dati = new Vector<Vector<DataElement>>();

			if (lista != null) {
				Iterator<Proprieta> it = lista.iterator();
				while (it.hasNext()) {
					Proprieta proprieta = it.next();
		
					Vector<DataElement> e = new Vector<DataElement>();
		
					DataElement de = new DataElement();
					de.setValue(proprieta.getNome());
					de.setIdToRemove(proprieta.getId() + "");
					e.addElement(de);
					
					de = new DataElement();
					de.setValue(proprieta.getValore());
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
	
	public boolean proprietaConnettoriMultipliCheckData(TipoOperazione tipoOp, String idPorta, String nomeSA, String nome,String valore) throws Exception {
		try {
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
			
			// Check Lunghezza
			if(this.checkLength255(nome, PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_NOME)==false) {
				return false;
			}
			if(this.checkLength255(valore, PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_VALORE)==false) {
				return false;
			}
			
			// Se tipoOp = add, controllo che la property non sia gia'
			// stata
			// registrata per la porta applicativa
			if (tipoOp.equals(TipoOperazione.ADD)) {
				boolean giaRegistrato = false;
				PortaApplicativa pa = this.porteApplicativeCore.getPortaApplicativa(Integer.parseInt(idPorta));
				
				PortaApplicativaServizioApplicativo paSA = null;
				for (PortaApplicativaServizioApplicativo paSATmp : pa.getServizioApplicativoList()) {
					if(paSATmp.getNome().equals(nomeSA)) {
						paSA = paSATmp;		
						break;
					}
				}

				PortaApplicativaServizioApplicativoConnettore datiConnettore = paSA.getDatiConnettore();
				
				if(datiConnettore != null) { // succede solo se e' la prima volta che modifico la configurazione di default
					for (int i = 0; i < datiConnettore.sizeProprietaList(); i++) {
						Proprieta tmpProp = datiConnettore.getProprieta(i);
						if (nome.equals(tmpProp.getNome())) {
							giaRegistrato = true;
							break;
						}
					}
				}

				if (giaRegistrato) {
						this.pd.setMessage(MessageFormat.format(PorteApplicativeCostanti.MESSAGGIO_ERRORE_LA_PROPERTY_XX_E_GIA_STATA_ASSOCIATA_AL_CONNETTORE_YY, nome, this.getLabelNomePortaApplicativaServizioApplicativo(paSA)));	
					return false;
				}
			}

			return true;
		} catch (Exception e) {
			this.log.error("Exception: " + e.getMessage(), e);
			throw new Exception(e);
		}
	}
	
	public Vector<DataElement> addProprietaConnettoriMultipliConfigToDati(Vector<DataElement> dati, TipoOperazione tipoOp, String nome, String valore, String labelTitolo) {
		return addProprietaConnettoriMultipliConfigToDati(dati, tipoOp, nome, valore, labelTitolo, null);
	}
	
	public Vector<DataElement> addProprietaConnettoriMultipliConfigToDati(Vector<DataElement> dati, TipoOperazione tipoOp, String nome, String valore, String labelTitolo, String nomeSA) {

		DataElement de = new DataElement();
		de.setLabel(labelTitolo);
		de.setType(DataElementType.TITLE);
		dati.addElement(de);
		
		de = new DataElement();
		de.setLabel(CostantiControlStation.LABEL_PARAMETRO_NOME);
		de.setValue(nome);
		if(TipoOperazione.ADD.equals(tipoOp)){
			de.setType(DataElementType.TEXT_EDIT);
			de.setRequired(true);
		}
		else{
			de.setType(DataElementType.TEXT);
		}
		de.setName(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_NOME_PROP);
		de.setSize(this.getSize());
		dati.addElement(de);

		de = new DataElement();
		de.setLabel(CostantiControlStation.LABEL_PARAMETRO_VALORE);
		de.setType(DataElementType.TEXT_EDIT);
		de.setRequired(true);
		de.setName(CostantiControlStation.PARAMETRO_VALORE);
		de.setValue(valore);
		de.setSize(this.getSize());
		dati.addElement(de);
		
		if(nomeSA != null) {
			de = new DataElement();
			de.setLabel("");
			de.setValue(nomeSA);
			de.setType(DataElementType.HIDDEN);
			de.setName(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_NOME_SA);
			dati.addElement(de);
		}
		return dati;
		
	}
	
	public String[] getEsitiTransazione(org.openspcoop2.pdd.core.behaviour.built_in.multi_deliver.ConfigurazioneMultiDeliver configurazioneMultiDeliver) {
		String[] esitiTransazione = null;
		List<String> esitiList = new ArrayList<>();
		
		if(configurazioneMultiDeliver.isNotificheByEsito_ok())
			esitiList.add(org.openspcoop2.pdd.core.behaviour.built_in.multi_deliver.Costanti.MULTI_DELIVER_NOTIFICHE_BY_ESITO_OK);
		if(configurazioneMultiDeliver.isNotificheByEsito_fault())
			esitiList.add(org.openspcoop2.pdd.core.behaviour.built_in.multi_deliver.Costanti.MULTI_DELIVER_NOTIFICHE_BY_ESITO_FAULT);
		if(configurazioneMultiDeliver.isNotificheByEsito_erroriConsegna())
			esitiList.add(org.openspcoop2.pdd.core.behaviour.built_in.multi_deliver.Costanti.MULTI_DELIVER_NOTIFICHE_BY_ESITO_ERRORI_CONSEGNA);
		if(configurazioneMultiDeliver.isNotificheByEsito_erroriProcessamento())
			esitiList.add(org.openspcoop2.pdd.core.behaviour.built_in.multi_deliver.Costanti.MULTI_DELIVER_NOTIFICHE_BY_ESITO_ERRORI_PROCESSAMENTO);
		// le richieste scartate non arrivano alla gestione della consegna in smistatore e quindi non potranno nemmeno essere notifiate
		//if(configurazioneMultiDeliver.isNotificheByEsito_richiesteScartate())
		//	esitiList.add(org.openspcoop2.pdd.core.behaviour.built_in.multi_deliver.Costanti.MULTI_DELIVER_NOTIFICHE_BY_ESITO_RICHIESTA_SCARTATE);
		
		esitiTransazione = esitiList.toArray(new String[esitiList.size()]);
		return esitiTransazione;
	}
	
	public org.openspcoop2.pdd.core.behaviour.built_in.load_balance.sticky.StickyConfigurazione toConfigurazioneSticky(boolean sticky, String stickyTipoSelettore, String stickyTipoSelettorePattern, String stickyMaxAge) throws NotFoundException {
		org.openspcoop2.pdd.core.behaviour.built_in.load_balance.sticky.StickyConfigurazione config = new org.openspcoop2.pdd.core.behaviour.built_in.load_balance.sticky.StickyConfigurazione();
		config.setStickyEnabled(sticky);
		if(sticky) {
			config.setTipoSelettore(StickyTipoSelettore.toEnumConstant(stickyTipoSelettore, true));
			config.setPattern(stickyTipoSelettorePattern);
			if(!StringUtils.isEmpty(stickyMaxAge)) {
				config.setMaxAgeSeconds(Integer.valueOf(stickyMaxAge));
			}
		}
		return config;
	}
	
	public org.openspcoop2.pdd.core.behaviour.built_in.load_balance.health_check.HealthCheckConfigurazione toConfigurazioneHealthCheck(boolean passiveHealthCheck, String passiveHealthCheck_excludeForSeconds) throws NotFoundException {
		org.openspcoop2.pdd.core.behaviour.built_in.load_balance.health_check.HealthCheckConfigurazione config = new org.openspcoop2.pdd.core.behaviour.built_in.load_balance.health_check.HealthCheckConfigurazione();
		config.setPassiveCheckEnabled(passiveHealthCheck);
		if(passiveHealthCheck) {
			if(!StringUtils.isEmpty(passiveHealthCheck_excludeForSeconds)) {
				config.setPassiveHealthCheck_excludeForSeconds(Integer.valueOf(passiveHealthCheck_excludeForSeconds));
			}
		}
		return config;
	}
	
	public org.openspcoop2.pdd.core.behaviour.built_in.multi_deliver.ConfigurazioneMultiDeliver toConfigurazioneMultiDeliver(String connettoreImplementaAPI, boolean notificheCondizionaliEsito, String [] esitiTransazione) {
		
		org.openspcoop2.pdd.core.behaviour.built_in.multi_deliver.ConfigurazioneMultiDeliver configurazioneMultiDeliver = new org.openspcoop2.pdd.core.behaviour.built_in.multi_deliver.ConfigurazioneMultiDeliver();
		
		if(StringUtils.isNotBlank(connettoreImplementaAPI))
			configurazioneMultiDeliver.setTransazioneSincrona_nomeConnettore(connettoreImplementaAPI);
		configurazioneMultiDeliver.setNotificheByEsito(notificheCondizionaliEsito);
		
		List<String> esitiList = new ArrayList<String>();
		if(esitiTransazione != null && esitiTransazione.length > 0) {
			esitiList = Arrays.asList(esitiTransazione);
		}
		
		configurazioneMultiDeliver.setNotificheByEsito_ok(esitiList.contains(org.openspcoop2.pdd.core.behaviour.built_in.multi_deliver.Costanti.MULTI_DELIVER_NOTIFICHE_BY_ESITO_OK));
		configurazioneMultiDeliver.setNotificheByEsito_fault(esitiList.contains(org.openspcoop2.pdd.core.behaviour.built_in.multi_deliver.Costanti.MULTI_DELIVER_NOTIFICHE_BY_ESITO_FAULT));
		configurazioneMultiDeliver.setNotificheByEsito_erroriConsegna(esitiList.contains(org.openspcoop2.pdd.core.behaviour.built_in.multi_deliver.Costanti.MULTI_DELIVER_NOTIFICHE_BY_ESITO_ERRORI_CONSEGNA));
		configurazioneMultiDeliver.setNotificheByEsito_erroriProcessamento(esitiList.contains(org.openspcoop2.pdd.core.behaviour.built_in.multi_deliver.Costanti.MULTI_DELIVER_NOTIFICHE_BY_ESITO_ERRORI_PROCESSAMENTO));
		// le richieste scartate non arrivano alla gestione della consegna in smistatore e quindi non potranno nemmeno essere notifiate
		//configurazioneMultiDeliver.setNotificheByEsito_richiesteScartate(esitiList.contains(org.openspcoop2.pdd.core.behaviour.built_in.multi_deliver.Costanti.MULTI_DELIVER_NOTIFICHE_BY_ESITO_RICHIESTA_SCARTATE));
		
		return configurazioneMultiDeliver;
	}
	
	public org.openspcoop2.pdd.core.behaviour.conditional.ConfigurazioneCondizionale toConfigurazioneCondizionale(
			boolean consegnaCondizionale, String selezioneConnettoreBy, String identificazioneCondizionale, String identificazioneCondizionalePattern,
			String identificazioneCondizionalePrefisso, String identificazioneCondizionaleSuffisso, boolean condizioneNonIdentificataAbortTransaction, String condizioneNonIdentificataDiagnostico, String condizioneNonIdentificataConnettore,
			boolean connettoreNonTrovatoAbortTransaction, String connettoreNonTrovatoDiagnostico, String connettoreNonTrovatoConnettore) {
		org.openspcoop2.pdd.core.behaviour.conditional.ConfigurazioneCondizionale configurazioneCondizionale = new org.openspcoop2.pdd.core.behaviour.conditional.ConfigurazioneCondizionale();
		
		if(selezioneConnettoreBy.equals(PorteApplicativeCostanti.VALUE_PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_SELEZIONE_CONNETTORE_BY_FILTRO))
			configurazioneCondizionale.setByFilter(true);
		else 
			configurazioneCondizionale.setByFilter(false);
		
		ConfigurazioneSelettoreCondizione defaultConfig = new ConfigurazioneSelettoreCondizione();
		
		defaultConfig.setTipoSelettore(org.openspcoop2.pdd.core.behaviour.conditional.TipoSelettore.toEnumConstant(identificazioneCondizionale));
		defaultConfig.setPattern(identificazioneCondizionalePattern);
		defaultConfig.setPrefix(identificazioneCondizionalePrefisso);
		defaultConfig.setSuffix(identificazioneCondizionaleSuffisso);
				
		configurazioneCondizionale.setDefaultConfig(defaultConfig);
		
				
		IdentificazioneFallitaConfigurazione condizioneNonIdentificata = new IdentificazioneFallitaConfigurazione();
		condizioneNonIdentificata.setAbortTransaction(condizioneNonIdentificataAbortTransaction);
		
		
		if(condizioneNonIdentificataDiagnostico == null || condizioneNonIdentificataDiagnostico.equals(StatoFunzionalita.DISABILITATO.getValue())) {
			condizioneNonIdentificata.setEmitDiagnosticError(false);
			condizioneNonIdentificata.setEmitDiagnosticInfo(false); 
		}else if(condizioneNonIdentificataDiagnostico.equals(PorteApplicativeCostanti.VALUE_PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_CONDIZIONE_NON_IDENTIFICATA_DIAGNOSTICO_ERROR)) {
			condizioneNonIdentificata.setEmitDiagnosticError(true);
			condizioneNonIdentificata.setEmitDiagnosticInfo(false);
		} else if(condizioneNonIdentificataDiagnostico.equals(PorteApplicativeCostanti.VALUE_PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_CONDIZIONE_NON_IDENTIFICATA_DIAGNOSTICO_INFO)) {
			condizioneNonIdentificata.setEmitDiagnosticError(false);
			condizioneNonIdentificata.setEmitDiagnosticInfo(true);
		}
		
		condizioneNonIdentificata.setNomeConnettore(condizioneNonIdentificataConnettore);
		configurazioneCondizionale.setCondizioneNonIdentificata(condizioneNonIdentificata );
		
		IdentificazioneFallitaConfigurazione nessunConnettoreTrovato = new IdentificazioneFallitaConfigurazione();
		
		nessunConnettoreTrovato.setAbortTransaction(connettoreNonTrovatoAbortTransaction);
		
		if(connettoreNonTrovatoDiagnostico == null || connettoreNonTrovatoDiagnostico.equals(StatoFunzionalita.DISABILITATO.getValue())) {
			nessunConnettoreTrovato.setEmitDiagnosticError(false);
			nessunConnettoreTrovato.setEmitDiagnosticInfo(false); 
		}else if(connettoreNonTrovatoDiagnostico.equals(PorteApplicativeCostanti.VALUE_PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_CONDIZIONE_NON_IDENTIFICATA_DIAGNOSTICO_ERROR)) {
			nessunConnettoreTrovato.setEmitDiagnosticError(true);
			nessunConnettoreTrovato.setEmitDiagnosticInfo(false);
		} else if(connettoreNonTrovatoDiagnostico.equals(PorteApplicativeCostanti.VALUE_PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_CONDIZIONE_NON_IDENTIFICATA_DIAGNOSTICO_INFO)) {
			nessunConnettoreTrovato.setEmitDiagnosticError(false);
			nessunConnettoreTrovato.setEmitDiagnosticInfo(true);
		}
		
		nessunConnettoreTrovato.setNomeConnettore(connettoreNonTrovatoConnettore);
		
		configurazioneCondizionale.setNessunConnettoreTrovato(nessunConnettoreTrovato);
		
		return configurazioneCondizionale;
	}
		
	public void preparePorteApplicativeConnettoriMultipliConfigAzioniList(PortaApplicativa pa,
			org.openspcoop2.pdd.core.behaviour.conditional.ConfigurazioneCondizionale configurazioneCondizionale, ISearch ricerca, Set<String> lista) throws Exception {
		try {
			// prelevo il flag che mi dice da quale pagina ho acceduto la sezione delle porte delegate
			Integer parentPA = ServletUtils.getIntegerAttributeFromSession(PorteApplicativeCostanti.ATTRIBUTO_PORTE_APPLICATIVE_PARENT, this.session);
			String idAsps = this.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_ASPS);
			if(idAsps == null)
				idAsps = "";

			String id = this.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID);
			String idsogg = this.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_SOGGETTO);

			Parameter pIdPorta = new Parameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID, id);
			Parameter pNomePorta = new Parameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_NOME, pa.getNome());
			Parameter pIdSogg = new Parameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_SOGGETTO, idsogg);
			Parameter pIdAsps = new Parameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_ASPS, idAsps);
			
			String idTabP = this.getParameter(CostantiControlStation.PARAMETRO_ID_TAB);
			Parameter pIdTab = new Parameter(CostantiControlStation.PARAMETRO_ID_TAB, idTabP != null ? idTabP : "");
			
			String accessoDaAPSParametro = this.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_CONNETTORE_DA_LISTA_APS);
			Parameter pAccessoDaAPS = new Parameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_CONNETTORE_DA_LISTA_APS, accessoDaAPSParametro != null ? accessoDaAPSParametro : "");
			String connettoreAccessoGruppi = this.getParameter(CostantiControlStation.PARAMETRO_VERIFICA_CONNETTORE_ACCESSO_DA_GRUPPI);
			Parameter pConnettoreAccessoDaGruppi = new Parameter(CostantiControlStation.PARAMETRO_VERIFICA_CONNETTORE_ACCESSO_DA_GRUPPI, connettoreAccessoGruppi);
			String connettoreRegistro = this.getParameter(CostantiControlStation.PARAMETRO_VERIFICA_CONNETTORE_REGISTRO);
			Parameter pConnettoreRegistro = new Parameter(CostantiControlStation.PARAMETRO_VERIFICA_CONNETTORE_REGISTRO, connettoreRegistro);
			String connettoreAccessoCM = this.getParameter(CostantiControlStation.PARAMETRO_VERIFICA_CONNETTORE_ACCESSO_DA_LISTA_CONNETTORI_MULTIPLI);
			Parameter pConnettoreAccessoCM = new Parameter(CostantiControlStation.PARAMETRO_VERIFICA_CONNETTORE_ACCESSO_DA_LISTA_CONNETTORI_MULTIPLI, connettoreAccessoCM);
			
			
			ServletUtils.addListElementIntoSession(this.session, PorteApplicativeCostanti.OBJECT_NAME_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_CONFIG_AZIONI,
					pIdPorta, pIdSogg, pIdAsps, pNomePorta, pConnettoreAccessoDaGruppi, pConnettoreRegistro, pAccessoDaAPS, pIdTab, pConnettoreAccessoCM);
			
			ServletUtils.disabledPageDataSearch(this.pd);
			
			this.pd.setNumEntries(lista != null ? lista.size() : 0);
			String idporta = pa.getNome();
			
			long idAspsLong = Long.parseLong(idAsps);
			AccordoServizioParteSpecifica asps = this.apsCore.getAccordoServizioParteSpecifica(idAspsLong);
			AccordoServizioParteComuneSintetico as = null;
			if(this.porteApplicativeCore.isRegistroServiziLocale()){
				int idAcc = asps.getIdAccordo().intValue();
				as = this.apcCore.getAccordoServizioSintetico(idAcc);
			}
			else{
				as = this.apcCore.getAccordoServizioSintetico(IDAccordoFactory.getInstance().getIDAccordoFromUri(asps.getAccordoServizioParteComune()));
			}

			boolean accessoDaListaAPS = false;
			if(Costanti.CHECK_BOX_ENABLED_TRUE.equals(accessoDaAPSParametro)) {
				accessoDaListaAPS = true;
			}
			
			boolean isModalitaCompleta = this.isModalitaCompleta();
			Boolean vistaErogazioni = ServletUtils.getBooleanAttributeFromSession(ErogazioniCostanti.ASPS_EROGAZIONI_ATTRIBUTO_VISTA_EROGAZIONI, this.session);
			// setto la barra del titolo
			List<Parameter> lstParam = this.getTitoloPA(parentPA, idsogg, idAsps);
			
			String labelPerPorta = null;
			if(parentPA!=null && (parentPA.intValue() == PorteApplicativeCostanti.ATTRIBUTO_PORTE_APPLICATIVE_PARENT_CONFIGURAZIONE)) {
				if(accessoDaListaAPS) {
					if(!isModalitaCompleta) {
						if(vistaErogazioni != null && vistaErogazioni.booleanValue()) {
							labelPerPorta = PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_CONFIG;
						} else {
							labelPerPorta = PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_CONFIG_DI+
									this.getLabelIdServizio(asps);
						}
					}
					else {
						labelPerPorta = PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_CONFIG;
					}
				}
				else {
					labelPerPorta = this.porteApplicativeCore.getLabelRegolaMappingErogazionePortaApplicativa(
							PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_CONFIG_DI,
							PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_CONFIG,
							pa);
				}
			}
			else {
				labelPerPorta = PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_CONFIG_DI+idporta;
			}
			
			if(accessoDaListaAPS) {
				lstParam.remove(lstParam.size()-1);
			}
			
			lstParam.add(new Parameter(labelPerPorta,PorteApplicativeCostanti.SERVLET_NAME_PORTE_APPLICATIVE_CONFIGURAZIONE_CONNETTORI_MULTIPLI, pIdSogg, pIdPorta, pIdAsps,
					pAccessoDaAPS, pConnettoreAccessoDaGruppi,	pConnettoreRegistro, pConnettoreAccessoCM));
			
			String labelPagLista = PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_CONFIG_AZIONI_REGOLE;
			
			lstParam.add(new Parameter(labelPagLista,null));

			ServletUtils.setPageDataTitle(this.pd, lstParam.toArray(new Parameter[lstParam.size()]));

			ServiceBinding serviceBinding = this.porteApplicativeCore.toMessageServiceBinding(as.getServiceBinding());
			
			// setto le label delle colonne
			String[] labels = {PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_AZIONI_NOME_LIST,	this.getLabelAzione(serviceBinding )};
			this.pd.setLabels(labels);

			// preparo i dati
			Vector<Vector<DataElement>> dati = new Vector<Vector<DataElement>>();

			if (lista != null) {
				Iterator<String> it = lista.iterator();
				while (it.hasNext()) {
					String nomeRegola = it.next();
					ConfigurazioneSelettoreCondizioneRegola regola = configurazioneCondizionale.getRegola(nomeRegola);
					Parameter pNomeRegola = new Parameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_AZIONI_OLD_NOME, nomeRegola);
					
					Vector<DataElement> e = new Vector<DataElement>();
		
					DataElement de = new DataElement();
					de.setValue(nomeRegola);
					de.setIdToRemove(nomeRegola + "");
					de.setUrl(PorteApplicativeCostanti.SERVLET_NAME_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_CONFIG_AZIONI_CHANGE, 
							pIdPorta, pIdSogg, pIdAsps, pNomePorta, pConnettoreAccessoDaGruppi, pConnettoreRegistro, pAccessoDaAPS, pIdTab, pConnettoreAccessoCM,pNomeRegola);
					e.addElement(de);
					
					de = new DataElement();
					de.setValue(regola.getPatternOperazione());
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
	public Vector<DataElement> addAzioneConnettoriMultipliConfigToDati(Vector<DataElement> dati, TipoOperazione tipoOp, String protocollo, ServiceBinding serviceBinding,
			String oldNome, String nome, String patternOperazione, boolean selezioneConnettoreByFiltro, String identificazioneCondizionale,
			String identificazioneCondizionalePattern, String identificazioneCondizionalePrefisso, String identificazioneCondizionaleSuffisso,
			List<String> connettoriValues, List<String> connettoriLabels) throws Exception {
	
		DataElement de = new DataElement();
		de.setLabel(PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_AZIONI_REGOLA);
		de.setType(DataElementType.TITLE);
		dati.addElement(de);
		
		// vecchio nome
		if(tipoOp.equals(TipoOperazione.CHANGE)) {
			de = new DataElement();
			de.setLabel("");
			de.setValue(oldNome);
			de.setType(DataElementType.HIDDEN);
			de.setName(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_AZIONI_OLD_NOME);
			dati.addElement(de);
		}
		
		// nome
		de = new DataElement();
		de.setLabel(PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_AZIONI_NOME_FORM);
		de.setValue(nome);
		de.setType(DataElementType.TEXT_EDIT);
		de.setName(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_AZIONI_NOME);
		de.setRequired(true);
		dati.addElement(de);
		
		// operationPattern
		de = new DataElement();
		de.setLabel(this.getLabelAzione(serviceBinding));
		de.setValue(patternOperazione);
		de.setType(DataElementType.TEXT_AREA);
		de.setName(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_AZIONI_PATTERN_OPERAZIONE);
		de.setRequired(true);
		DataElementInfo info = new DataElementInfo(this.getLabelAzione(serviceBinding));
		info.setHeaderBody(PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_AZIONI_PATTERN_AZIONE_INFO_HEADER);
		if(ServiceBinding.REST.equals(serviceBinding)) {
			info.setListBody(PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_AZIONI_PATTERN_AZIONE_INFO_BODY_LIST_REST);
		}
		else {
			info.setListBody(PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_AZIONI_PATTERN_AZIONE_INFO_BODY_LIST_SOAP);
		}
		de.setInfo(info);
		dati.addElement(de);
		
		// Identificazione condizionale
		de = new DataElement();
		de.setName(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_AZIONI_IDENTIFICAZIONE_CONDIZIONALE);
		de.setLabel(PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_AZIONI_IDENTIFICAZIONE_CONDIZIONALE);
		de.setType(DataElementType.SELECT);
		
		org.openspcoop2.pdd.core.behaviour.conditional.TipoSelettore tipoSelettoreS = null;
		if(!PorteApplicativeCostanti.VALUE_PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_AZIONI_IDENTIFICAZIONE_CONDIZIONALE_STATIC_INFO.equals(identificazioneCondizionale)) {
			tipoSelettoreS = org.openspcoop2.pdd.core.behaviour.conditional.TipoSelettore.toEnumConstant(identificazioneCondizionale, true);
		}
						
		String [] identificazioneCondizionale_values = {
				PorteApplicativeCostanti.VALUE_PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_AZIONI_IDENTIFICAZIONE_CONDIZIONALE_STATIC_INFO,
				org.openspcoop2.pdd.core.behaviour.conditional.TipoSelettore.HEADER_BASED.getValue(),
				org.openspcoop2.pdd.core.behaviour.conditional.TipoSelettore.URLBASED.getValue(),
				org.openspcoop2.pdd.core.behaviour.conditional.TipoSelettore.FORM_BASED.getValue(),
				org.openspcoop2.pdd.core.behaviour.conditional.TipoSelettore.SOAPACTION_BASED.getValue(),
				org.openspcoop2.pdd.core.behaviour.conditional.TipoSelettore.CONTENT_BASED.getValue(),
				org.openspcoop2.pdd.core.behaviour.conditional.TipoSelettore.INDIRIZZO_IP.getValue(),
				org.openspcoop2.pdd.core.behaviour.conditional.TipoSelettore.INDIRIZZO_IP_FORWARDED.getValue(),
				org.openspcoop2.pdd.core.behaviour.conditional.TipoSelettore.TEMPLATE.getValue(),
				org.openspcoop2.pdd.core.behaviour.conditional.TipoSelettore.FREEMARKER_TEMPLATE.getValue(),
				org.openspcoop2.pdd.core.behaviour.conditional.TipoSelettore.VELOCITY_TEMPLATE.getValue()
				};
		
		List<String> identificazioneCondizionale_labels = ModalitaIdentificazione.getLabels(
				ModalitaIdentificazione.STATIC,
				ModalitaIdentificazione.HEADER_BASED,
				ModalitaIdentificazione.URL_BASED,
				ModalitaIdentificazione.FORM_BASED,
				ModalitaIdentificazione.SOAP_ACTION_BASED,
				ModalitaIdentificazione.CONTENT_BASED,
				ModalitaIdentificazione.INDIRIZZO_IP_BASED,
				ModalitaIdentificazione.X_FORWARD_FOR_BASED,
				ModalitaIdentificazione.GOVWAY_TEMPLATE,
				ModalitaIdentificazione.FREEMARKER_TEMPLATE,
				ModalitaIdentificazione.VELOCITY_TEMPLATE
			);
		
		de.setValues(identificazioneCondizionale_values);
		de.setLabels(identificazioneCondizionale_labels);
		de.setPostBack(true);
		de.setSelected(identificazioneCondizionale);
		dati.addElement(de);	
		
		// nome
		de = new DataElement();
		de.setName(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_AZIONI_IDENTIFICAZIONE_CONDIZIONALE_PATTERN);
		de.setLabel(this.getLabelIdentificazioneCondizionalePattern(identificazioneCondizionale, selezioneConnettoreByFiltro));
		de.setValue(identificazioneCondizionalePattern);
		if(identificazioneCondizionale==null || 
				org.openspcoop2.pdd.core.behaviour.conditional.TipoSelettore.SOAPACTION_BASED.equals(identificazioneCondizionale)  || 
				org.openspcoop2.pdd.core.behaviour.conditional.TipoSelettore.INDIRIZZO_IP.equals(identificazioneCondizionale) || 
				org.openspcoop2.pdd.core.behaviour.conditional.TipoSelettore.INDIRIZZO_IP_FORWARDED.equals(identificazioneCondizionale)){
			de.setType(DataElementType.HIDDEN);
		}
		else if(org.openspcoop2.pdd.core.behaviour.conditional.TipoSelettore.URLBASED.equals(identificazioneCondizionale) ||
				org.openspcoop2.pdd.core.behaviour.conditional.TipoSelettore.CONTENT_BASED.equals(identificazioneCondizionale)||
				org.openspcoop2.pdd.core.behaviour.conditional.TipoSelettore.TEMPLATE.equals(identificazioneCondizionale)||
				org.openspcoop2.pdd.core.behaviour.conditional.TipoSelettore.VELOCITY_TEMPLATE.equals(identificazioneCondizionale)||
				org.openspcoop2.pdd.core.behaviour.conditional.TipoSelettore.FREEMARKER_TEMPLATE.equals(identificazioneCondizionale)) {
			de.setRequired(true);
			de.setType(DataElementType.TEXT_AREA);
			if(tipoSelettoreS!=null && tipoSelettoreS.isTemplate()) {
				de.setRows(10);
			}
		}else if(PorteApplicativeCostanti.VALUE_PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_AZIONI_IDENTIFICAZIONE_CONDIZIONALE_STATIC_INFO.equals(identificazioneCondizionale)) { // static
			if(selezioneConnettoreByFiltro) {
				de.setRequired(true);
				de.setType(DataElementType.TEXT_EDIT);
			} else {
				de.setType(DataElementType.SELECT);
				de.setValues(connettoriValues);
				de.setLabels(connettoriLabels);
				de.setSelected(identificazioneCondizionalePattern);
			}
		}
		else{
			de.setRequired(true);
			de.setType(DataElementType.TEXT_EDIT);
		}
		
		if(tipoSelettoreS!=null && tipoSelettoreS.isTemplate()) {
			DataElementInfo dInfoPattern = null;
			if(org.openspcoop2.pdd.core.behaviour.conditional.TipoSelettore.TEMPLATE.equals(identificazioneCondizionale)) {
				dInfoPattern = new DataElementInfo(ModalitaIdentificazione.GOVWAY_TEMPLATE.getLabel());
				dInfoPattern.setHeaderBody(DynamicHelperCostanti.LABEL_CONFIGURAZIONE_INFO_TRASPORTO);
				if(ServiceBinding.REST.equals(serviceBinding)) {
					dInfoPattern.setListBody(DynamicHelperCostanti.getLABEL_CONFIGURAZIONE_INFO_TRASFORMAZIONI_TRASPORTO_REST_VALORI_CON_RISPOSTE(this.isProfiloModIPA(protocollo), false, false));
				}
				else {
					dInfoPattern.setListBody(DynamicHelperCostanti.getLABEL_CONFIGURAZIONE_INFO_TRASFORMAZIONI_TRASPORTO_SOAP_VALORI_CON_RISPOSTE(this.isProfiloModIPA(protocollo), false, false));
				}
			}
			else if(org.openspcoop2.pdd.core.behaviour.conditional.TipoSelettore.FREEMARKER_TEMPLATE.equals(identificazioneCondizionale)) {
				dInfoPattern = new DataElementInfo(ModalitaIdentificazione.FREEMARKER_TEMPLATE.getLabel());
				dInfoPattern.setHeaderBody(DynamicHelperCostanti.LABEL_CONFIGURAZIONE_INFO_OBJECT_TEMPLATE_FREEMARKER);
				if(ServiceBinding.REST.equals(serviceBinding)) {
					dInfoPattern.setListBody(DynamicHelperCostanti.getLABEL_CONFIGURAZIONE_INFO_OBJECT_REST_VALORI_FREEMARKER(this.isProfiloModIPA(protocollo), false, false));
				}
				else {
					dInfoPattern.setListBody(DynamicHelperCostanti.getLABEL_CONFIGURAZIONE_INFO_OBJECT_SOAP_VALORI_FREEMARKER(this.isProfiloModIPA(protocollo), false, false));
				}
			}
			else {
				dInfoPattern = new DataElementInfo(ModalitaIdentificazione.VELOCITY_TEMPLATE.getLabel());
				dInfoPattern.setHeaderBody(DynamicHelperCostanti.LABEL_CONFIGURAZIONE_INFO_OBJECT_TEMPLATE_VELOCITY);
				if(ServiceBinding.REST.equals(serviceBinding)) {
					dInfoPattern.setListBody(DynamicHelperCostanti.getLABEL_CONFIGURAZIONE_INFO_OBJECT_REST_VALORI_VELOCITY(this.isProfiloModIPA(protocollo), false, false));
				}
				else {
					dInfoPattern.setListBody(DynamicHelperCostanti.getLABEL_CONFIGURAZIONE_INFO_OBJECT_SOAP_VALORI_VELOCITY(this.isProfiloModIPA(protocollo), false, false));
				}
			}
			de.setInfo(dInfoPattern);
		}
		
		dati.addElement(de);
		
		// prefisso
		de = new DataElement();
		de.setLabel(PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_AZIONI_IDENTIFICAZIONE_CONDIZIONALE_PREFISSO);
		de.setName(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_AZIONI_IDENTIFICAZIONE_CONDIZIONALE_PREFISSO);
		de.setSize(this.getSize());
		de.setValue(identificazioneCondizionalePrefisso);
		if( (tipoSelettoreS!=null && tipoSelettoreS.isTemplate()) 
				||
				(PorteApplicativeCostanti.VALUE_PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_AZIONI_IDENTIFICAZIONE_CONDIZIONALE_STATIC_INFO.equals(identificazioneCondizionale))
				) {
			 de.setType(DataElementType.HIDDEN);
		} else {
			 de.setType(DataElementType.TEXT_EDIT);
		}
		dati.addElement(de);
		
		// suffisso
		de = new DataElement();
		de.setLabel(PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_AZIONI_IDENTIFICAZIONE_CONDIZIONALE_SUFFISSO);
		de.setName(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_AZIONI_IDENTIFICAZIONE_CONDIZIONALE_SUFFISSO);
		de.setSize(this.getSize());
		de.setValue(identificazioneCondizionaleSuffisso);
		if( (tipoSelettoreS!=null && tipoSelettoreS.isTemplate()) 
				||
				(PorteApplicativeCostanti.VALUE_PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_AZIONI_IDENTIFICAZIONE_CONDIZIONALE_STATIC_INFO.equals(identificazioneCondizionale))
				) {
			 de.setType(DataElementType.HIDDEN);
		} else {
			 de.setType(DataElementType.TEXT_EDIT);
		}
		dati.addElement(de);
		
		
		
		return dati;
	}
	public boolean azioneConnettoriMultipliConfigCheckData(TipoOperazione tipoOp, ServiceBinding serviceBinding, String idPorta, String oldNome,
			String nome, String patternOperazione, boolean selezioneConnettoreByFiltro, String identificazioneCondizionale,
			String identificazioneCondizionalePattern, String identificazioneCondizionalePrefisso,
			String identificazioneCondizionaleSuffisso, Set<String> regoleEsistenti) throws Exception {
		
		// campi obbligatori
		if (nome.equals("")){
			this.pd.setMessage("Il campo "+ PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_AZIONI_NOME_FORM +" non pu&ograve; essere vuoto");
			return false;
		}
		
		if (patternOperazione.equals("")){
			this.pd.setMessage("Il campo "+ this.getLabelAzione(serviceBinding) +" non pu&ograve; essere vuoto");
			return false;
		}
		
		// Check Lunghezza
		if(this.checkLength255(nome, PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_AZIONI_NOME_FORM)==false) {
			return false;
		}
		
		if(this.checkLength4000(patternOperazione, this.getLabelAzione(serviceBinding))==false) {
			return false;
		}
		
		if (identificazioneCondizionale.equals("")){
			this.pd.setMessage("Il campo "+ PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_AZIONI_IDENTIFICAZIONE_CONDIZIONALE +" non pu&ograve; essere vuoto");
			return false;
		}
		
		if(!PorteApplicativeCostanti.VALUE_PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_AZIONI_IDENTIFICAZIONE_CONDIZIONALE_STATIC_INFO.equals(identificazioneCondizionale)) {
			org.openspcoop2.pdd.core.behaviour.conditional.TipoSelettore tipo = org.openspcoop2.pdd.core.behaviour.conditional.TipoSelettore.toEnumConstant(identificazioneCondizionale);
								
			if(tipo.hasParameter()) {
				if (StringUtils.isEmpty(identificazioneCondizionalePattern)) {
					this.pd.setMessage("Il campo "+ this.getLabelIdentificazioneCondizionalePattern(identificazioneCondizionale) +" non pu&ograve; essere vuoto");
					return false;
				}
				
				if(this.checkLength4000(identificazioneCondizionalePattern, this.getLabelIdentificazioneCondizionalePattern(identificazioneCondizionale))==false) {
					return false;
				}
				
				if (org.openspcoop2.pdd.core.behaviour.conditional.TipoSelettore.URLBASED.equals(tipo)) {
					if(this.checkRegexp(identificazioneCondizionalePattern,ModalitaIdentificazione.URL_BASED.getLabelParametro())==false){
						return false;
					}
				}
				if (org.openspcoop2.pdd.core.behaviour.conditional.TipoSelettore.CONTENT_BASED.equals(tipo)) {
					if(ServiceBinding.SOAP.equals(serviceBinding)) {
						if(this.checkXPath(identificazioneCondizionalePattern,ModalitaIdentificazione.CONTENT_BASED.getLabelParametro())==false){
							return false;
						}
					}
					else {
						if(this.checkXPathOrJsonPath(identificazioneCondizionalePattern,ModalitaIdentificazione.CONTENT_BASED.getLabelParametro())==false){
							return false;
						}
					}
				}
			}
			
								
			if(!tipo.isTemplate()){  // e' un caso in cui e' visibile
				if(this.checkLength255(identificazioneCondizionalePrefisso, PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_IDENTIFICAZIONE_CONDIZIONALE_PREFISSO)==false) {
					return false;
				}
				
				if(this.checkLength255(identificazioneCondizionaleSuffisso, PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_IDENTIFICAZIONE_CONDIZIONALE_SUFFISSO)==false) {
					return false;
				}
			}
		} else { // caso static
			if (StringUtils.isEmpty(identificazioneCondizionalePattern)) {
				this.pd.setMessage("Il campo "+ this.getLabelIdentificazioneCondizionalePattern(identificazioneCondizionale, selezioneConnettoreByFiltro) +" non pu&ograve; essere vuoto");
				return false;
			}
			
			if(selezioneConnettoreByFiltro) {
				if(this.checkLength255(identificazioneCondizionalePattern, this.getLabelIdentificazioneCondizionalePattern(identificazioneCondizionale, selezioneConnettoreByFiltro))==false) {
					return false;
				}
			}
			
			if(this.checkLength255(identificazioneCondizionalePrefisso, PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_IDENTIFICAZIONE_CONDIZIONALE_PREFISSO)==false) {
				return false;
			}
			
			if(this.checkLength255(identificazioneCondizionaleSuffisso, PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_IDENTIFICAZIONE_CONDIZIONALE_SUFFISSO)==false) {
				return false;
			}
		}
		
		
		if(tipoOp.equals(TipoOperazione.ADD)) {
			if(regoleEsistenti != null) {
				if(regoleEsistenti.contains(nome)) {
					this.pd.setMessage(MessageFormat.format(PorteApplicativeCostanti.MESSAGGIO_ERRORE_ESISTE_GIA_UNA_REGOLA_XX, nome));	
					return false;
				}
			}
		}
		
		if(tipoOp.equals(TipoOperazione.CHANGE)) { 
			if(!oldNome.equals(nome)) { // cambio nome alla regola nella change
				if(regoleEsistenti.contains(nome)) {
					this.pd.setMessage(MessageFormat.format(PorteApplicativeCostanti.MESSAGGIO_ERRORE_ESISTE_GIA_UNA_REGOLA_XX, nome));	
					return false;
				}
			}
		}
		
		return true;
	}
	
	public boolean isConnettoreMultiploInUso(int numeroElementiDaControllare, String nomePaSA,
			PortaApplicativa pa, AccordoServizioParteSpecifica asps,
			AccordoServizioParteComuneSintetico apc, ServiceBinding serviceBinding, List<String> messaggiSezioniConnettore) throws DriverConfigurazioneException, Exception,
			DriverRegistroServiziException, DriverConfigurazioneNotFound, BehaviourException {
		
		boolean connettoreUtilizzatiConfig = false;
		
		for (int j = 0; j < pa.sizeServizioApplicativoList(); j++) {
			PortaApplicativaServizioApplicativo paSA = pa.getServizioApplicativo(j);
			String nomeConnettore = this.getLabelNomePortaApplicativaServizioApplicativo(paSA);
			if (nomePaSA.equals(paSA.getNome())) {
				if(pa.getBehaviour() != null) {
					boolean connettoreInUso = false;
					List<String> titoliSezioniAggiornate = new ArrayList<String>();
					TipoBehaviour behaviourType = TipoBehaviour.toEnumConstant(pa.getBehaviour().getNome());

					boolean consegnaCondizionale = false;
					if(behaviourType.equals(TipoBehaviour.CONSEGNA_MULTIPLA)
							|| behaviourType.equals(TipoBehaviour.CONSEGNA_CON_NOTIFICHE)
							|| behaviourType.equals(TipoBehaviour.CONSEGNA_CONDIZIONALE)
							|| behaviourType.equals(TipoBehaviour.CONSEGNA_LOAD_BALANCE)) {
						consegnaCondizionale = org.openspcoop2.pdd.core.behaviour.conditional.ConditionalUtils.isConfigurazioneCondizionale(pa, ControlStationCore.getLog());

						if(behaviourType.equals(TipoBehaviour.CONSEGNA_CON_NOTIFICHE)) {
								org.openspcoop2.pdd.core.behaviour.built_in.multi_deliver.ConfigurazioneMultiDeliver configurazioneMultiDeliver = 
										org.openspcoop2.pdd.core.behaviour.built_in.multi_deliver.MultiDeliverUtils.read(pa, ControlStationCore.getLog());

							if(configurazioneMultiDeliver != null) {
								if(configurazioneMultiDeliver.getTransazioneSincrona_nomeConnettore() != null) {
									if(configurazioneMultiDeliver.getTransazioneSincrona_nomeConnettore().equals(nomeConnettore)) {
										// MESSAGGIO!
										titoliSezioniAggiornate.add(PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_SEZIONE_NOTIFICHE 
												+ " -> " + PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_CONNETTORE_IMPLEMENTA_API);
										connettoreInUso = true;
									}
								}
							}
						}


						if(consegnaCondizionale) {
							org.openspcoop2.pdd.core.behaviour.conditional.ConfigurazioneCondizionale configurazioneCondizionale = 
									org.openspcoop2.pdd.core.behaviour.conditional.ConditionalUtils.read(pa, ControlStationCore.getLog());
							
							for (String nomeRegola : configurazioneCondizionale.getRegoleOrdinate()) {
								ConfigurazioneSelettoreCondizioneRegola regola = configurazioneCondizionale.getRegola(nomeRegola);
								if(!configurazioneCondizionale.isByFilter()) {
									if(regola.getStaticInfo() != null) {
										if(regola.getStaticInfo().equals(nomeConnettore)) {
											// MESSAGGIO!

											titoliSezioniAggiornate.add(
													PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_CONFIGURAZIONE_CONDIZIONALITA 
													+ " -> " + PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_REGOLE_PER_AZIONI);
											connettoreInUso = true;
											break;
										}
									}
								}
							}

							org.openspcoop2.pdd.core.behaviour.conditional.IdentificazioneFallitaConfigurazione condizioneNonIdentificata =
									configurazioneCondizionale.getCondizioneNonIdentificata();

							if(condizioneNonIdentificata.getNomeConnettore() != null) {
								if(condizioneNonIdentificata.getNomeConnettore().equals(nomeConnettore)) {
									// MESSAGGIO!

									titoliSezioniAggiornate.add(

											PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_CONFIGURAZIONE_CONDIZIONALITA 
											+ " -> " + PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_CONDIZIONE_NON_IDENTIFICATA);
									connettoreInUso = true;
								}
							}

							org.openspcoop2.pdd.core.behaviour.conditional.IdentificazioneFallitaConfigurazione connettoreNonTrovato = 
									configurazioneCondizionale.getNessunConnettoreTrovato();

							if(connettoreNonTrovato.getNomeConnettore() != null) {
								if(connettoreNonTrovato.getNomeConnettore().equals(nomeConnettore)) {
									// MESSAGGIO!
									titoliSezioniAggiornate.add(PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_CONFIGURAZIONE_CONDIZIONALITA 
											+ " -> " + PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_CONNETTORE_NON_TROVATO);
									connettoreInUso = true;
								}
							}
						}

						if(connettoreInUso) {
							StringBuilder sbMsg = new StringBuilder();
							if(numeroElementiDaControllare > 1) {
								sbMsg.append(nomeConnettore).append(":").append(org.openspcoop2.core.constants.Costanti.WEB_NEW_LINE);
							}

							sbMsg.append(DBOggettiInUsoUtils.formatList(titoliSezioniAggiornate, org.openspcoop2.core.constants.Costanti.WEB_NEW_LINE));
							messaggiSezioniConnettore.add(sbMsg.toString());
							connettoreUtilizzatiConfig = true;
						}

					}
				}
				break;
			}
		} // end for tutti i pasa
		
		return connettoreUtilizzatiConfig;
	}
	
	public boolean isConnettoreMultiploInUsoCriteriApplicabilitaTrasformazioni(int numeroElementiDaControllare, String nomePaSA,
			PortaApplicativa pa, ServiceBinding serviceBinding, List<String> messaggiSezioniConnettore) throws DriverConfigurazioneException, Exception,
			DriverRegistroServiziException, DriverConfigurazioneNotFound, BehaviourException {
		
		boolean connettoreUtilizzatiConfig = false;
		
		// verifico che non sia associato a criteri di applicabilità delle configurazioni
		if(pa.getTrasformazioni()!=null && pa.getTrasformazioni().sizeRegolaList()>0) {
			for (int j = 0; j < pa.sizeServizioApplicativoList(); j++) {
				PortaApplicativaServizioApplicativo paSA = pa.getServizioApplicativo(j);
				if (nomePaSA.equals(paSA.getNome())) {
					String nomeConnettore = this.getLabelNomePortaApplicativaServizioApplicativo(paSA);
					for (TrasformazioneRegola tr : pa.getTrasformazioni().getRegolaList()) {
						if(tr.getApplicabilita()!=null && tr.getApplicabilita().sizeConnettoreList()>0) {
							if(tr.getApplicabilita().getConnettoreList().contains(nomePaSA)){
								
								StringBuilder sbMsg = new StringBuilder();
								if(numeroElementiDaControllare > 1) {
									sbMsg.append(nomeConnettore).append(":").append(org.openspcoop2.core.constants.Costanti.WEB_NEW_LINE);
								}

								
								List<String> titoliSezioniAggiornate = new ArrayList<String>();
								
								titoliSezioniAggiornate.add(PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_TRASFORMAZIONI 
										+ " -> " + tr.getNome() +" -> "+ PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_TRASFORMAZIONI_APPLICABILITA);
								
								sbMsg.append(DBOggettiInUsoUtils.formatList(titoliSezioniAggiornate, org.openspcoop2.core.constants.Costanti.WEB_NEW_LINE));
								messaggiSezioniConnettore.add(sbMsg.toString());
								connettoreUtilizzatiConfig = true;
								
								break;
							}	
						}
					}
					break;
				}
			}
		}	
					
		return connettoreUtilizzatiConfig;		
					
	}
	
	
	public Vector<DataElement> addConnettoriMultipliNotificheToDati(Vector<DataElement> dati, TipoOperazione tipoOp,
			TipoBehaviour beaBehaviourType, String nomeSAConnettore, ServiceBinding serviceBinding, String cadenzaRispedizione,
			String codiceRisposta2xx, String codiceRisposta2xxValueMin, String codiceRisposta2xxValueMax, String codiceRisposta2xxValue,
			String codiceRisposta3xx, String codiceRisposta3xxValueMin, String codiceRisposta3xxValueMax, String codiceRisposta3xxValue,
			String codiceRisposta4xx, String codiceRisposta4xxValueMin, String codiceRisposta4xxValueMax, String codiceRisposta4xxValue,
			String codiceRisposta5xx, String codiceRisposta5xxValueMin, String codiceRisposta5xxValueMax, String codiceRisposta5xxValue,
			String gestioneFault, String faultCode, String faultActor, String faultMessage,
			boolean consegnaSincrona,
			String coda, String priorita, String prioritaMax,
			String connettoreTipoMessaggioDaNotificare, String httpMethodDaNotificare
	) {
		
		DataElement de = new DataElement();
		de.setLabel("");
		de.setName(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_NOME_SA);
		de.setType(DataElementType.HIDDEN);
		de.setValue(nomeSAConnettore);
		dati.add(de);
				
		
		if(!consegnaSincrona && TipoBehaviour.CONSEGNA_CON_NOTIFICHE.equals(beaBehaviourType)) {
						
			de = new DataElement();
			de.setLabel(PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_REGOLE_CONSEGNA_NOTIFICA_MODALITA);
			de.setType(DataElementType.TITLE);
			dati.add(de);
			
			// tipo di notifica
			de = new DataElement();
			de.setName(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_NOTIFICHE_TIPO_MESSAGGIO_DA_NOTIFICARE);
			de.setLabel(PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_NOTIFICHE_TIPO_MESSAGGIO_DA_NOTIFICARE);
			de.setType(DataElementType.SELECT);
			de.setValues(PorteApplicativeCostanti.VALORI_PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_CONNETTORE_MESSAGGIO_DA_NOTIFICARE);
			de.setLabels(PorteApplicativeCostanti.VALORI_PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_CONNETTORE_MESSAGGIO_DA_NOTIFICARE);
			de.setSelected(connettoreTipoMessaggioDaNotificare);
			de.setPostBack(true);
			dati.addElement(de);
			
			// http notifica
			if(ServiceBinding.REST.equals(serviceBinding) || 
					(connettoreTipoMessaggioDaNotificare!=null && MessaggioDaNotificare.ENTRAMBI.equals(connettoreTipoMessaggioDaNotificare)) 
				) {
				de = new DataElement();
				de.setName(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_NOTIFICHE_TIPO_HTTP_NOTIFICA);
				de.setLabel(PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_NOTIFICHE_TIPO_HTTP_NOTIFICA);
				de.setType(DataElementType.SELECT);
				if(connettoreTipoMessaggioDaNotificare!=null && MessaggioDaNotificare.RICHIESTA.equals(connettoreTipoMessaggioDaNotificare)) {
					de.setValues(PorteApplicativeCostanti.VALORI_PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_CONNETTORE_MESSAGGIO_HTTP_NOTIFICA_RICHIESTA);
					de.setLabels(PorteApplicativeCostanti.LABELS_PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_CONNETTORE_MESSAGGIO_HTTP_NOTIFICA_RICHIESTA);
				}
				else {
					de.setValues(PorteApplicativeCostanti.VALORI_PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_CONNETTORE_MESSAGGIO_HTTP_NOTIFICA);
					de.setLabels(PorteApplicativeCostanti.VALORI_PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_CONNETTORE_MESSAGGIO_HTTP_NOTIFICA);
				}
				de.setSelected(httpMethodDaNotificare);
				de.setPostBack(false);
				dati.addElement(de);
			}
			
		}
		
		if(!consegnaSincrona) {
			de = new DataElement();
			de.setLabel(PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_PRIORITA_CONSEGNA_NOTIFICA);
			de.setType(DataElementType.TITLE);
			dati.add(de);
		}
				
		// Coda
		de = new DataElement();
		de.setName(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_NOTIFICHE_CODA);
		de.setLabel(PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_NOTIFICHE_CODA);
		if(consegnaSincrona) {
			de.setType(DataElementType.HIDDEN);
		}
		else {
			List<String> code = this.porteApplicativeCore.getConsegnaNotificaCode();
			if(code==null || code.size()<=1) {
				de.setType(DataElementType.HIDDEN);
			}
			else {
				de.setType(DataElementType.SELECT);
				de.setValues(code);
				List<String> labels = new ArrayList<String>();
				for (String codaNome : code) {
					labels.add(this.porteApplicativeCore.getConsegnaNotificaCodaLabel(codaNome));
				}
				de.setLabels(labels);
				de.setSelected(coda);
			}
		}
		de.setValue(coda);
		dati.add(de);
		
		// Priorita
		de = new DataElement();
		de.setName(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_NOTIFICHE_PRIORITA);
		de.setLabel(PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_NOTIFICHE_PRIORITA);
		if(consegnaSincrona) {
			de.setType(DataElementType.HIDDEN);
		}
		else {
			if(ServletUtils.isCheckBoxEnabled(prioritaMax)) {
				de.setType(DataElementType.HIDDEN);
				
				DataElement deLABEL = new DataElement();
				deLABEL.setType(DataElementType.TEXT);
				deLABEL.setName(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_NOTIFICHE_PRIORITA+"__LABEL");
				deLABEL.setLabel(PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_NOTIFICHE_PRIORITA);
				deLABEL.setValue(this.porteApplicativeCore.getConsegnaNotificaConfigurazionePriorita(priorita).getLabel());
				dati.add(deLABEL);
				
			}
			else {
				List<String> prioritaList = this.porteApplicativeCore.getConsegnaNotificaPriorita();
				de.setType(DataElementType.SELECT);
				de.setValues(prioritaList);
				List<String> labels = new ArrayList<String>();
				List<String> listPriorita = new ArrayList<String>();
				for (String prioritaNome : prioritaList) {
					ConfigurazionePriorita conf = this.porteApplicativeCore.getConsegnaNotificaConfigurazionePriorita(prioritaNome);
					labels.add(conf.getLabel());
					if(conf.isNessunaPriorita()) {
						listPriorita.add(PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_NOTIFICHE_PRIORITA_INFO_NESSUNA_PRIORITA.
								replace(PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_NOTIFICHE_PRIORITA_LABEL, conf.getLabel()));
					}
					else {
						listPriorita.add(PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_NOTIFICHE_PRIORITA_INFO_PRIORITA.
								replace(PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_NOTIFICHE_PRIORITA_LABEL, conf.getLabel()).
								replace(PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_NOTIFICHE_PRIORITA_PERCENTUALE, conf.getPercentuale()+""));
					}
				}
				de.setLabels(labels);
				de.setSelected(priorita);
				DataElementInfo deInfo = new DataElementInfo(PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_NOTIFICHE_PRIORITA);
				deInfo.setListBody(listPriorita);
				de.setInfo(deInfo);
			}
		}
		de.setValue(priorita);
		dati.add(de);
		
		
		// Priorita Max
		de = new DataElement();
		de.setName(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_NOTIFICHE_PRIORITA_MAX);
		de.setLabel(PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_NOTIFICHE_PRIORITA_MAX_LEFT);
		if(consegnaSincrona) {
			de.setType(DataElementType.HIDDEN);
		}
		else {
			de.setType(DataElementType.CHECKBOX);
			de.setPostBack(true);
			de.setSelected(ServletUtils.isCheckBoxEnabled(prioritaMax));
			de.setLabelRight(PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_NOTIFICHE_PRIORITA_MAX_RIGHT);
			de.setLabelAffiancata(false);
			de.setNote(PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_NOTIFICHE_PRIORITA_MAX_NOTE);
			DataElementInfo deInfo = new DataElementInfo(PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_NOTIFICHE_PRIORITA_MAX_LEFT);
			deInfo.setBody(PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_NOTIFICHE_PRIORITA_MAX_INFO);
			de.setInfo(deInfo);
		}
		de.setValue(priorita);
		dati.add(de);
		
		
		
		
		de = new DataElement();
		if(consegnaSincrona) {
			de.setLabel(PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_REGOLE_CONSEGNA);
		}
		else {
			de.setLabel(PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_REGOLE_CONSEGNA_NOTIFICA);
		}
		de.setType(DataElementType.TITLE);
		dati.add(de);
		
		
		
		
		String consegnaNotificheLabel = "";
		try{
			ConfigurazioneGestioneConsegnaNotifiche nuovaConfigurazioneGestioneConsegnaNotifiche  = this.getConfigurazioneGestioneConsegnaNotifiche(beaBehaviourType, serviceBinding, cadenzaRispedizione,
					codiceRisposta2xx, codiceRisposta2xxValueMin, codiceRisposta2xxValueMax, codiceRisposta2xxValue,
					codiceRisposta3xx, codiceRisposta3xxValueMin, codiceRisposta3xxValueMax, codiceRisposta3xxValue,
					codiceRisposta4xx, codiceRisposta4xxValueMin, codiceRisposta4xxValueMax, codiceRisposta4xxValue, 
					codiceRisposta5xx, codiceRisposta5xxValueMin, codiceRisposta5xxValueMax, codiceRisposta5xxValue,
					gestioneFault, faultCode, faultActor, faultMessage,
					consegnaSincrona,
					connettoreTipoMessaggioDaNotificare, httpMethodDaNotificare);
			if(nuovaConfigurazioneGestioneConsegnaNotifiche != null) {
				consegnaNotificheLabel = org.openspcoop2.pdd.core.behaviour.built_in.multi_deliver.GestioneConsegnaNotificheUtils.toString(nuovaConfigurazioneGestioneConsegnaNotifiche,
						serviceBinding.equals(ServiceBinding.SOAP));
			}
		}catch(Exception e) {
			// nel caso non siano forniti alcuni valori va in errore...
			// lascio a debug e non verra' presentata la label
			this.log.debug(e.getMessage(),e);	
		}
		if(StringUtils.isNotEmpty(consegnaNotificheLabel)) {
			DataElement deLABEL = new DataElement();
			deLABEL.setType(DataElementType.TEXT);
			deLABEL.setLabel(PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_NOTIFICHE_COMPORTAMENTO);
			deLABEL.setValue(consegnaNotificheLabel);
			dati.add(deLABEL);
		}
		
				
		
		// subtitolo Codice Risposta HTTP
		de = new DataElement();
		de.setLabel(PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_NOTIFICHE_CODICE_RISPOSTA_HTTP);
		de.setType(DataElementType.SUBTITLE);
		dati.add(de);
		
		
		String [] codificaRispostaValues = {
				org.openspcoop2.pdd.core.behaviour.built_in.multi_deliver.TipoGestioneNotificaTrasporto.CODICI_CONSEGNA_COMPLETATA.getValue(),
				org.openspcoop2.pdd.core.behaviour.built_in.multi_deliver.TipoGestioneNotificaTrasporto.CONSEGNA_COMPLETATA.getValue(),
				org.openspcoop2.pdd.core.behaviour.built_in.multi_deliver.TipoGestioneNotificaTrasporto.CONSEGNA_FALLITA.getValue(),
				org.openspcoop2.pdd.core.behaviour.built_in.multi_deliver.TipoGestioneNotificaTrasporto.INTERVALLO_CONSEGNA_COMPLETATA.getValue()
		};
		String [] codificaRispostaLabels = {
				org.openspcoop2.pdd.core.behaviour.built_in.multi_deliver.TipoGestioneNotificaTrasporto.CODICI_CONSEGNA_COMPLETATA.getLabel(),
				org.openspcoop2.pdd.core.behaviour.built_in.multi_deliver.TipoGestioneNotificaTrasporto.CONSEGNA_COMPLETATA.getLabel(),
				org.openspcoop2.pdd.core.behaviour.built_in.multi_deliver.TipoGestioneNotificaTrasporto.CONSEGNA_FALLITA.getLabel(),
				org.openspcoop2.pdd.core.behaviour.built_in.multi_deliver.TipoGestioneNotificaTrasporto.INTERVALLO_CONSEGNA_COMPLETATA.getLabel()
		};
		
		// selectList 2xx
		de = new DataElement();
		de.setLabel(PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_NOTIFICHE_CODICE_RISPOSTA_HTTP_2XX);
		de.setName(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_NOTIFICHE_CODICE_RISPOSTA_HTTP_2XX );
		de.setType(DataElementType.SELECT);
		de.setValues(codificaRispostaValues);
		de.setLabels(codificaRispostaLabels);
		de.setSelected(codiceRisposta2xx);
		de.setPostBack(true);
		dati.add(de);
		
		// intervallo consegna
		if(org.openspcoop2.pdd.core.behaviour.built_in.multi_deliver.TipoGestioneNotificaTrasporto.INTERVALLO_CONSEGNA_COMPLETATA.getValue().equals(codiceRisposta2xx)) {
			de = new DataElement();
			de.setType(DataElementType.INTERVAL_NUMBER);
			de.setLabel("&nbsp;");
			de.setValues(Arrays.asList(codiceRisposta2xxValueMin, codiceRisposta2xxValueMax));
			de.setNames(Arrays.asList(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_NOTIFICHE_CODICE_RISPOSTA_HTTP_2XX_VALUE_MIN,
					PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_NOTIFICHE_CODICE_RISPOSTA_HTTP_2XX_VALUE_MAX));
			de.setMinValue(200);
			de.setMaxValue(299);
			de.setSize(getSize());
			de.reloadMinValue(false);
			de.setRequired(true);
			dati.add(de);
		} 
		// Codici Consegna
		if(org.openspcoop2.pdd.core.behaviour.built_in.multi_deliver.TipoGestioneNotificaTrasporto.CODICI_CONSEGNA_COMPLETATA.getValue().equals(codiceRisposta2xx)) {
			de = new DataElement();
			de.setType(DataElementType.TEXT_EDIT);
			de.setLabel("&nbsp;");
			de.setValue(codiceRisposta2xxValue);
			de.setName(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_NOTIFICHE_CODICE_RISPOSTA_HTTP_2XX_VALUE);
			de.setSize(getSize());
			de.setRequired(true);
			dati.add(de);
		} 
		
		// selectList 3xx
		de = new DataElement();
		de.setLabel(PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_NOTIFICHE_CODICE_RISPOSTA_HTTP_3XX);
		de.setName(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_NOTIFICHE_CODICE_RISPOSTA_HTTP_3XX );
		if(ServiceBinding.REST.equals(serviceBinding)){
			de.setType(DataElementType.SELECT);
			de.setValues(codificaRispostaValues);
			de.setLabels(codificaRispostaLabels);
			de.setSelected(codiceRisposta3xx);
			de.setPostBack(true);
		}
		else {
			de.setType(DataElementType.HIDDEN);
			de.setValue(codiceRisposta3xx);
		}
		dati.add(de);
		
		// intervallo consegna
		if(ServiceBinding.REST.equals(serviceBinding) && org.openspcoop2.pdd.core.behaviour.built_in.multi_deliver.TipoGestioneNotificaTrasporto.INTERVALLO_CONSEGNA_COMPLETATA.getValue().equals(codiceRisposta3xx)) {
			de = new DataElement();
			de.setType(DataElementType.INTERVAL_NUMBER);
			de.setLabel("&nbsp;");
			de.setValues(Arrays.asList(codiceRisposta3xxValueMin, codiceRisposta3xxValueMax));
			de.setNames(Arrays.asList(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_NOTIFICHE_CODICE_RISPOSTA_HTTP_3XX_VALUE_MIN,
					PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_NOTIFICHE_CODICE_RISPOSTA_HTTP_3XX_VALUE_MAX));
			de.setMinValue(300);
			de.setMaxValue(399);
			de.setSize(getSize());
			de.reloadMinValue(false);
			de.setRequired(true);
			dati.add(de);
		} 
		// Codici Consegna
		if(ServiceBinding.REST.equals(serviceBinding) && org.openspcoop2.pdd.core.behaviour.built_in.multi_deliver.TipoGestioneNotificaTrasporto.CODICI_CONSEGNA_COMPLETATA.getValue().equals(codiceRisposta3xx)) {
			de = new DataElement();
			de.setType(DataElementType.TEXT_EDIT);
			de.setLabel("&nbsp;");
			de.setValue(codiceRisposta3xxValue);
			de.setName(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_NOTIFICHE_CODICE_RISPOSTA_HTTP_3XX_VALUE);
			de.setSize(getSize());
			de.setRequired(true);
			dati.add(de);
		} 
		
		// selectList 4xx
		de = new DataElement();
		de.setLabel(PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_NOTIFICHE_CODICE_RISPOSTA_HTTP_4XX);
		de.setName(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_NOTIFICHE_CODICE_RISPOSTA_HTTP_4XX );
		de.setType(DataElementType.SELECT);
		de.setValues(codificaRispostaValues);
		de.setLabels(codificaRispostaLabels);
		de.setSelected(codiceRisposta4xx);
		de.setPostBack(true);
		dati.add(de);
		
		// intervallo consegna
		if(org.openspcoop2.pdd.core.behaviour.built_in.multi_deliver.TipoGestioneNotificaTrasporto.INTERVALLO_CONSEGNA_COMPLETATA.getValue().equals(codiceRisposta4xx)) {
			de = new DataElement();
			de.setType(DataElementType.INTERVAL_NUMBER);
			de.setLabel("&nbsp;");
			de.setValues(Arrays.asList(codiceRisposta4xxValueMin, codiceRisposta4xxValueMax));
			de.setNames(Arrays.asList(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_NOTIFICHE_CODICE_RISPOSTA_HTTP_4XX_VALUE_MIN,
					PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_NOTIFICHE_CODICE_RISPOSTA_HTTP_4XX_VALUE_MAX));
			de.setMinValue(400);
			de.setMaxValue(499);
			de.setSize(getSize());
			de.reloadMinValue(false);
			de.setRequired(true);
			dati.add(de);
		} 
		// Codici Consegna
		if(org.openspcoop2.pdd.core.behaviour.built_in.multi_deliver.TipoGestioneNotificaTrasporto.CODICI_CONSEGNA_COMPLETATA.getValue().equals(codiceRisposta4xx)) {
			de = new DataElement();
			de.setType(DataElementType.TEXT_EDIT);
			de.setLabel("&nbsp;");
			de.setValue(codiceRisposta4xxValue);
			de.setName(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_NOTIFICHE_CODICE_RISPOSTA_HTTP_4XX_VALUE);
			de.setSize(getSize());
			de.setRequired(true);
			dati.add(de);
		} 
		
		// selectList 5xx
		de = new DataElement();
		de.setLabel(PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_NOTIFICHE_CODICE_RISPOSTA_HTTP_5XX);
		de.setName(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_NOTIFICHE_CODICE_RISPOSTA_HTTP_5XX );
		de.setType(DataElementType.SELECT);
		de.setValues(codificaRispostaValues);
		de.setLabels(codificaRispostaLabels);
		de.setSelected(codiceRisposta5xx);
		de.setPostBack(true);
		dati.add(de);
		
		// intervallo consegna
		if(org.openspcoop2.pdd.core.behaviour.built_in.multi_deliver.TipoGestioneNotificaTrasporto.INTERVALLO_CONSEGNA_COMPLETATA.getValue().equals(codiceRisposta5xx)) {
			de = new DataElement();
			de.setType(DataElementType.INTERVAL_NUMBER);
			de.setLabel("&nbsp;");
			de.setValues(Arrays.asList(codiceRisposta5xxValueMin, codiceRisposta5xxValueMax));
			de.setNames(Arrays.asList(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_NOTIFICHE_CODICE_RISPOSTA_HTTP_5XX_VALUE_MIN,
					PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_NOTIFICHE_CODICE_RISPOSTA_HTTP_5XX_VALUE_MAX));
			de.setMinValue(500);
			de.setMaxValue(599);
			de.setSize(getSize());
			de.reloadMinValue(false);
			de.setRequired(true);
			dati.add(de);
		} 
		// Codici Consegna
		if(org.openspcoop2.pdd.core.behaviour.built_in.multi_deliver.TipoGestioneNotificaTrasporto.CODICI_CONSEGNA_COMPLETATA.getValue().equals(codiceRisposta5xx)) {
			de = new DataElement();
			de.setType(DataElementType.TEXT_EDIT);
			de.setLabel("&nbsp;");
			de.setValue(codiceRisposta5xxValue);
			de.setName(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_NOTIFICHE_CODICE_RISPOSTA_HTTP_5XX_VALUE);
			de.setSize(getSize());
			de.setRequired(true);
			dati.add(de);
		} 
		
		String [] gestioneValues = {
				org.openspcoop2.pdd.core.behaviour.built_in.multi_deliver.TipoGestioneNotificaFault.CONSEGNA_COMPLETATA.getValue(),
				org.openspcoop2.pdd.core.behaviour.built_in.multi_deliver.TipoGestioneNotificaFault.CONSEGNA_FALLITA.getValue(),
				org.openspcoop2.pdd.core.behaviour.built_in.multi_deliver.TipoGestioneNotificaFault.CONSEGNA_COMPLETATA_PERSONALIZZATA.getValue(),
				org.openspcoop2.pdd.core.behaviour.built_in.multi_deliver.TipoGestioneNotificaFault.CONSEGNA_FALLITA_PERSONALIZZATA.getValue()
		};
		String [] gestioneLabels = {
				org.openspcoop2.pdd.core.behaviour.built_in.multi_deliver.TipoGestioneNotificaFault.CONSEGNA_COMPLETATA.getLabel(),
				org.openspcoop2.pdd.core.behaviour.built_in.multi_deliver.TipoGestioneNotificaFault.CONSEGNA_FALLITA.getLabel(),
				org.openspcoop2.pdd.core.behaviour.built_in.multi_deliver.TipoGestioneNotificaFault.CONSEGNA_COMPLETATA_PERSONALIZZATA.getLabel(),
				org.openspcoop2.pdd.core.behaviour.built_in.multi_deliver.TipoGestioneNotificaFault.CONSEGNA_FALLITA_PERSONALIZZATA.getLabel()
		};
		
		de = new DataElement();
		if(serviceBinding.equals(ServiceBinding.SOAP)) { // label SOAP Fault
			de.setLabel(PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_NOTIFICHE_SOAP_FAULT);
		} else {  // label Problem
			de.setLabel(PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_NOTIFICHE_PROBLEM_DETAIL); 
		}
		de.setType(DataElementType.SUBTITLE);
		dati.add(de);
		
		// SelectList scelta tipogestione notifica
		de = new DataElement();
		de.setLabel(PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_NOTIFICHE_GESTIONE);
		de.setName(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_NOTIFICHE_GESTIONE );
		de.setType(DataElementType.SELECT);
		de.setValues(gestioneValues);
		de.setLabels(gestioneLabels);
		de.setSelected(gestioneFault);
		de.setPostBack(true);
		if(org.openspcoop2.pdd.core.behaviour.built_in.multi_deliver.TipoGestioneNotificaFault.CONSEGNA_COMPLETATA_PERSONALIZZATA.getValue().equals(gestioneFault) ||
				org.openspcoop2.pdd.core.behaviour.built_in.multi_deliver.TipoGestioneNotificaFault.CONSEGNA_FALLITA_PERSONALIZZATA.getValue().equals(gestioneFault)) {
			StringBuilder sb = new StringBuilder();
			
			sb.append("'");
			sb.append(serviceBinding.equals(ServiceBinding.SOAP) ? PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_NOTIFICHE_CODE : 
				PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_NOTIFICHE_TYPE);
			sb.append("', '");
			
			sb.append(serviceBinding.equals(ServiceBinding.SOAP) ? PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_NOTIFICHE_ACTOR : 
				PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_NOTIFICHE_STATUS);
			
			sb.append("' o '");
			
			sb.append(serviceBinding.equals(ServiceBinding.SOAP) ? PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_NOTIFICHE_MESSAGE : 
				PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_NOTIFICHE_CLAIMS);
			
			sb.append("'");
			
			de.setNote(MessageFormat.format(PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_NOTIFICHE_GESTIONE_CUSTOM_NOTE,	sb.toString()));
		}
		dati.add(de);
		
		if(org.openspcoop2.pdd.core.behaviour.built_in.multi_deliver.TipoGestioneNotificaFault.CONSEGNA_COMPLETATA_PERSONALIZZATA.getValue().equals(gestioneFault) ||
				org.openspcoop2.pdd.core.behaviour.built_in.multi_deliver.TipoGestioneNotificaFault.CONSEGNA_FALLITA_PERSONALIZZATA.getValue().equals(gestioneFault)) {
			//code
			de = new DataElement();
			de.setType(DataElementType.TEXT_EDIT);
			DataElementInfo dInfo = null;
			if(serviceBinding.equals(ServiceBinding.SOAP)) { // SOAP : Code
				de.setLabel(PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_NOTIFICHE_CODE);
				dInfo = new DataElementInfo(PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_NOTIFICHE_CODE);
				dInfo.setHeaderBody(PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_NOTIFICHE_CODE_INFO_HEADER);
				//dInfo.setListBody(PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_NOTIFICHE_CODE_INFO_LIST);
			} else { // REST: Type
				de.setLabel(PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_NOTIFICHE_STATUS); 
				dInfo = new DataElementInfo(PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_NOTIFICHE_STATUS);
				dInfo.setHeaderBody(PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_NOTIFICHE_TYPE_INFO_HEADER);
				//dInfo.setListBody(PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_NOTIFICHE_TYPE_INFO_LIST);
			}
			de.setValue(faultCode);
			de.setName(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_NOTIFICHE_CODE);
			de.setSize(getSize());
			de.setInfo(dInfo);
			dati.add(de);
			
			
			// actor
			de = new DataElement();
			de.setType(DataElementType.TEXT_EDIT);
			if(serviceBinding.equals(ServiceBinding.SOAP)) { // SOAP: Actor
				de.setLabel(PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_NOTIFICHE_ACTOR);
				dInfo = new DataElementInfo(PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_NOTIFICHE_ACTOR);
				dInfo.setHeaderBody(PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_NOTIFICHE_ACTOR_INFO_HEADER);
				//dInfo.setListBody(PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_NOTIFICHE_ACTOR_INFO_LIST);
			} else { // REST: Status
				de.setLabel(PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_NOTIFICHE_TYPE); 
				dInfo = new DataElementInfo(PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_NOTIFICHE_TYPE);
				dInfo.setHeaderBody(PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_NOTIFICHE_STATUS_INFO_HEADER);
				//dInfo.setListBody(PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_NOTIFICHE_STATUS_INFO_LIST);
			}
			de.setValue(faultActor);
			de.setName(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_NOTIFICHE_ACTOR);
			de.setSize(getSize());
			de.setInfo(dInfo);
			dati.add(de);
			
			
			// message
			de = new DataElement();
			if(serviceBinding.equals(ServiceBinding.SOAP)) { // SOAP: Messagge
				de.setType(DataElementType.TEXT_EDIT);
				de.setSize(getSize());
				de.setLabel(PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_NOTIFICHE_MESSAGE);
				dInfo = new DataElementInfo(PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_NOTIFICHE_MESSAGE);
				dInfo.setHeaderBody(PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_NOTIFICHE_MESSAGE_INFO_HEADER);
				//dInfo.setListBody(PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_NOTIFICHE_MESSAGE_INFO_LIST);
			} else { // REST: Claims
				de.setType(DataElementType.TEXT_AREA);
				de.setLabel(PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_NOTIFICHE_CLAIMS); 
				de.setNote(PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_NOTIFICHE_CLAIMS_NOTE);
				dInfo = new DataElementInfo(PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_NOTIFICHE_CLAIMS);
				dInfo.setHeaderBody(PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_NOTIFICHE_CLAIMS_NOTE);
				//dInfo.setListBody(PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_NOTIFICHE_CLAIMS_INFO_LIST);
			}
			
			de.setValue(faultMessage);
			de.setName(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_NOTIFICHE_MESSAGE);
			de.setInfo(dInfo);
			dati.add(de);
		}
		
		
		
		if(!consegnaSincrona) {
			de = new DataElement();
			de.setLabel(PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_NOTIFICHE_CONSEGNA_FALLITA);
			de.setType(DataElementType.SUBTITLE);
			dati.add(de);
		}
		
		// cadenza rispedizione
		de = new DataElement();
		de.setLabel(PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_NOTIFICHE_CADENZA_RISPEDIZIONE);
		de.setName(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_NOTIFICHE_CADENZA_RISPEDIZIONE );
		if(consegnaSincrona) {
			de.setType(DataElementType.HIDDEN);
		}
		else {
			de.setNote(PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_NOTIFICHE_CADENZA_RISPEDIZIONE_NOTE);
			de.setType(DataElementType.NUMBER);
			de.setMinValue(0);
			de.reloadMinValue(false);
		}
		de.setValue(cadenzaRispedizione);
		dati.add(de);
		
		return dati;
	}
	
	public ConfigurazioneGestioneConsegnaNotifiche getConfigurazioneGestioneConsegnaNotifiche(TipoBehaviour beaBehaviourType, ServiceBinding serviceBinding, String cadenzaRispedizione,
			String codiceRisposta2xx, String codiceRisposta2xxValueMin, String codiceRisposta2xxValueMax, String codiceRisposta2xxValue,
			String codiceRisposta3xx, String codiceRisposta3xxValueMin, String codiceRisposta3xxValueMax, String codiceRisposta3xxValue,
			String codiceRisposta4xx, String codiceRisposta4xxValueMin, String codiceRisposta4xxValueMax, String codiceRisposta4xxValue,
			String codiceRisposta5xx, String codiceRisposta5xxValueMin, String codiceRisposta5xxValueMax, String codiceRisposta5xxValue,
			String gestioneFault, String faultCode, String faultActor, String faultMessage,
			boolean consegnaSincrona,
			String connettoreTipoMessaggioDaNotificare, String httpMethodDaNotificare) throws Exception {
		
		ConfigurazioneGestioneConsegnaNotifiche configurazioneGestioneConsegnaNotifiche = new ConfigurazioneGestioneConsegnaNotifiche();
		
		if(TipoBehaviour.CONSEGNA_CON_NOTIFICHE.equals(beaBehaviourType) && !consegnaSincrona) {
			MessaggioDaNotificare tipo = null;
			if(connettoreTipoMessaggioDaNotificare!=null) {
				tipo = MessaggioDaNotificare.toEnumConstant(connettoreTipoMessaggioDaNotificare, false);
			}
			configurazioneGestioneConsegnaNotifiche.setMessaggioDaNotificare(tipo);
			
			HttpRequestMethod tipoHttp = null;
			if(httpMethodDaNotificare!=null && !PorteApplicativeCostanti.VALORE_PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_CONNETTORE_MESSAGGIO_HTTP_NOTIFICA_USA_QUELLO_DELLA_RICHIESTA.equals(httpMethodDaNotificare)) {
				try {
					tipoHttp = HttpRequestMethod.valueOf(httpMethodDaNotificare);
				}catch(Throwable t) {}
			}
			configurazioneGestioneConsegnaNotifiche.setHttpMethod(tipoHttp);
		}
		
		if(StringUtils.isNotEmpty(cadenzaRispedizione)) {
			configurazioneGestioneConsegnaNotifiche.setCadenzaRispedizione(Integer.parseInt(cadenzaRispedizione));
		}
		
		// 2xx
		TipoGestioneNotificaTrasporto gestioneTrasporto2xx = TipoGestioneNotificaTrasporto.toEnumConstant(codiceRisposta2xx);
		switch(gestioneTrasporto2xx) {
		case CODICI_CONSEGNA_COMPLETATA:
			List<String> codiceRisposta2xxValues = Arrays.asList(codiceRisposta2xxValue.split(","));
			List<Integer> gestioneTrasporto2xx_codes = new ArrayList<Integer>();
			
			for (String code : codiceRisposta2xxValues) {
				gestioneTrasporto2xx_codes.add(Integer.parseInt(code));
			}
			
			configurazioneGestioneConsegnaNotifiche.setGestioneTrasporto2xx_codes(gestioneTrasporto2xx_codes );
			break;
		case INTERVALLO_CONSEGNA_COMPLETATA:
			configurazioneGestioneConsegnaNotifiche.setGestioneTrasporto2xx_leftInterval(Integer.parseInt(codiceRisposta2xxValueMin));
			configurazioneGestioneConsegnaNotifiche.setGestioneTrasporto2xx_rightInterval(Integer.parseInt(codiceRisposta2xxValueMax));
			break;
		case CONSEGNA_COMPLETATA:
		case CONSEGNA_FALLITA:
			break;		
		}
		
		configurazioneGestioneConsegnaNotifiche.setGestioneTrasporto2xx(gestioneTrasporto2xx);
		
		// 3xx
		TipoGestioneNotificaTrasporto gestioneTrasporto3xx = TipoGestioneNotificaTrasporto.toEnumConstant(codiceRisposta3xx);
		switch(gestioneTrasporto3xx) {
		case CODICI_CONSEGNA_COMPLETATA:
			List<String> codiceRisposta3xxValues = Arrays.asList(codiceRisposta3xxValue.split(","));
			List<Integer> gestioneTrasporto3xx_codes = new ArrayList<Integer>();
			
			for (String code : codiceRisposta3xxValues) {
				gestioneTrasporto3xx_codes.add(Integer.parseInt(code));
			}
			
			configurazioneGestioneConsegnaNotifiche.setGestioneTrasporto3xx_codes(gestioneTrasporto3xx_codes );
			break;
		case INTERVALLO_CONSEGNA_COMPLETATA:
			configurazioneGestioneConsegnaNotifiche.setGestioneTrasporto3xx_leftInterval(Integer.parseInt(codiceRisposta3xxValueMin));
			configurazioneGestioneConsegnaNotifiche.setGestioneTrasporto3xx_rightInterval(Integer.parseInt(codiceRisposta3xxValueMax));
			break;
		case CONSEGNA_COMPLETATA:
		case CONSEGNA_FALLITA:
			break;		
		}
		
		configurazioneGestioneConsegnaNotifiche.setGestioneTrasporto3xx(gestioneTrasporto3xx);
		
		// 4xx
		TipoGestioneNotificaTrasporto gestioneTrasporto4xx = TipoGestioneNotificaTrasporto.toEnumConstant(codiceRisposta4xx);
		switch(gestioneTrasporto4xx) {
		case CODICI_CONSEGNA_COMPLETATA:
			List<String> codiceRisposta4xxValues = Arrays.asList(codiceRisposta4xxValue.split(","));
			List<Integer> gestioneTrasporto4xx_codes = new ArrayList<Integer>();
			
			for (String code : codiceRisposta4xxValues) {
				gestioneTrasporto4xx_codes.add(Integer.parseInt(code));
			}
			
			configurazioneGestioneConsegnaNotifiche.setGestioneTrasporto4xx_codes(gestioneTrasporto4xx_codes );
			break;
		case INTERVALLO_CONSEGNA_COMPLETATA:
			configurazioneGestioneConsegnaNotifiche.setGestioneTrasporto4xx_leftInterval(Integer.parseInt(codiceRisposta4xxValueMin));
			configurazioneGestioneConsegnaNotifiche.setGestioneTrasporto4xx_rightInterval(Integer.parseInt(codiceRisposta4xxValueMax));
			break;
		case CONSEGNA_COMPLETATA:
		case CONSEGNA_FALLITA:
			break;		
		}
		
		configurazioneGestioneConsegnaNotifiche.setGestioneTrasporto4xx(gestioneTrasporto4xx);
		
		// 5xx
		TipoGestioneNotificaTrasporto gestioneTrasporto5xx = TipoGestioneNotificaTrasporto.toEnumConstant(codiceRisposta5xx);
		switch(gestioneTrasporto5xx) {
		case CODICI_CONSEGNA_COMPLETATA:
			List<String> codiceRisposta5xxValues = Arrays.asList(codiceRisposta5xxValue.split(","));
			List<Integer> gestioneTrasporto5xx_codes = new ArrayList<Integer>();
			
			for (String code : codiceRisposta5xxValues) {
				gestioneTrasporto5xx_codes.add(Integer.parseInt(code));
			}
			
			configurazioneGestioneConsegnaNotifiche.setGestioneTrasporto5xx_codes(gestioneTrasporto5xx_codes );
			break;
		case INTERVALLO_CONSEGNA_COMPLETATA:
			configurazioneGestioneConsegnaNotifiche.setGestioneTrasporto5xx_leftInterval(Integer.parseInt(codiceRisposta5xxValueMin));
			configurazioneGestioneConsegnaNotifiche.setGestioneTrasporto5xx_rightInterval(Integer.parseInt(codiceRisposta5xxValueMax));
			break;
		case CONSEGNA_COMPLETATA:
		case CONSEGNA_FALLITA:
			break;		
		}
		
		configurazioneGestioneConsegnaNotifiche.setGestioneTrasporto5xx(gestioneTrasporto5xx);
		
		TipoGestioneNotificaFault fault = TipoGestioneNotificaFault.toEnumConstant(gestioneFault);
		switch(fault) {
		case CONSEGNA_COMPLETATA:
		case CONSEGNA_FALLITA:
			break;
		case CONSEGNA_COMPLETATA_PERSONALIZZATA:
		case CONSEGNA_FALLITA_PERSONALIZZATA:
			configurazioneGestioneConsegnaNotifiche.setFaultCode(faultCode);
			configurazioneGestioneConsegnaNotifiche.setFaultActor(faultActor);
			configurazioneGestioneConsegnaNotifiche.setFaultMessage(faultMessage);
			break;
		}
		
		configurazioneGestioneConsegnaNotifiche.setFault(fault);
		
		return configurazioneGestioneConsegnaNotifiche;
	}
	
	
	public boolean connettoriMultipliNotificheCheckData(TipoOperazione tipoOp, PortaApplicativa pa , TipoBehaviour beaBehaviourType, ServiceBinding serviceBinding, String nomeSAConnettore, String cadenzaRispedizione,
			String codiceRisposta2xx, String codiceRisposta2xxValueMin, String codiceRisposta2xxValueMax, String codiceRisposta2xxValue,
			String codiceRisposta3xx, String codiceRisposta3xxValueMin, String codiceRisposta3xxValueMax, String codiceRisposta3xxValue,
			String codiceRisposta4xx, String codiceRisposta4xxValueMin, String codiceRisposta4xxValueMax, String codiceRisposta4xxValue,
			String codiceRisposta5xx, String codiceRisposta5xxValueMin, String codiceRisposta5xxValueMax, String codiceRisposta5xxValue,
			String gestioneFault, String faultCode, String faultActor, String faultMessage) throws Exception{
		try{
			
			if(!StringUtils.isEmpty(cadenzaRispedizione)) {
//				this.pd.setMessage(MessageFormat.format(PorteApplicativeCostanti.MESSAGGIO_ERRORE_DATI_INCOMPLETI_E_NECESSARIO_INDICARE_XX,
//						PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_NOTIFICHE_CADENZA_RISPEDIZIONE));
//				return false;
			
				int w = -1;
				try {
					w = Integer.parseInt(cadenzaRispedizione);
				}catch (Exception e) {
					this.pd.setMessage(MessageFormat.format(PorteApplicativeCostanti.MESSAGGIO_ERRRORE_FORMATO_NUMERICO_XX_NON_VALIDO,
							PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_NOTIFICHE_CADENZA_RISPEDIZIONE));
					return false;
				}
				
				if(w < 0) {
					this.pd.setMessage(MessageFormat.format(PorteApplicativeCostanti.MESSAGGIO_ERRRORE_MIN_XX_NON_VALIDO,
							PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_NOTIFICHE_CADENZA_RISPEDIZIONE, 0));
					return false;
				}
			}
			
			if(StringUtils.isEmpty(codiceRisposta2xx)) {
				this.pd.setMessage(MessageFormat.format(PorteApplicativeCostanti.MESSAGGIO_ERRORE_DATI_INCOMPLETI_E_NECESSARIO_INDICARE_XX,
						PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_NOTIFICHE_CODICE_RISPOSTA_HTTP_2XX));
				return false;
			}
			
			boolean almenoUnaConsegnaCompletata = false;
			
			// 2xx
			TipoGestioneNotificaTrasporto gestioneTrasporto2xx = TipoGestioneNotificaTrasporto.toEnumConstant(codiceRisposta2xx);
			switch(gestioneTrasporto2xx) {
			case CODICI_CONSEGNA_COMPLETATA:
				almenoUnaConsegnaCompletata=true;
				if(StringUtils.isEmpty(codiceRisposta2xxValue)) {
					this.pd.setMessage(MessageFormat.format(PorteApplicativeCostanti.MESSAGGIO_ERRORE_DATI_INCOMPLETI_E_NECESSARIO_INDICARE_XX,
							PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_NOTIFICHE_CODICE_RISPOSTA_HTTP_2XX));
					return false;
				}
				
				if(!StringUtils.containsOnly(codiceRisposta2xxValue, PorteApplicativeCostanti.VALUE_PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_NOTIFICHE_CODICE_RISPOSTA_HTTP_2XX_CARATTERI_CONSENTITI)) {
					this.pd.setMessage(MessageFormat.format(PorteApplicativeCostanti.MESSAGGIO_ERRORE_CODICI_NON_VALIDI_NEL_CAMPO_XX,
							PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_NOTIFICHE_CODICE_RISPOSTA_HTTP_2XX));
					return false;
				}
				
				List<String> codiceRisposta2xxValues = Arrays.asList(codiceRisposta2xxValue.split(","));
				
				for (String code : codiceRisposta2xxValues) {
					try {
						int val = Integer.parseInt(code);
						
						if(val < 200 || val > 299) {
							this.pd.setMessage(MessageFormat.format(PorteApplicativeCostanti.MESSAGGIO_ERRRORE_CODICE_MIN_MAX_XX_NON_VALIDO,
									PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_NOTIFICHE_CODICE_RISPOSTA_HTTP_2XX, 200, 299));
							return false;
						}
					}catch (Exception e) {
						this.pd.setMessage(MessageFormat.format(PorteApplicativeCostanti.MESSAGGIO_ERRRORE_FORMATO_NUMERICO_XX_NON_VALIDO,
								PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_NOTIFICHE_CODICE_RISPOSTA_HTTP_2XX));
						return false;
					}
				}
				break;
			case INTERVALLO_CONSEGNA_COMPLETATA:
				almenoUnaConsegnaCompletata=true;
				if(StringUtils.isEmpty(codiceRisposta2xxValueMin)) {
					this.pd.setMessage(MessageFormat.format(PorteApplicativeCostanti.MESSAGGIO_ERRORE_DATI_INCOMPLETI_E_NECESSARIO_INDICARE_XX,
							PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_NOTIFICHE_CODICE_RISPOSTA_HTTP_2XX));
					return false;
				}
				try {
					Integer.parseInt(codiceRisposta2xxValueMin);
				}catch (Exception e) {
					this.pd.setMessage(MessageFormat.format(PorteApplicativeCostanti.MESSAGGIO_ERRRORE_FORMATO_NUMERICO_XX_NON_VALIDO,
							PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_NOTIFICHE_CODICE_RISPOSTA_HTTP_2XX));
					return false;
				}
				
				if(StringUtils.isEmpty(codiceRisposta2xxValueMax)) {
					this.pd.setMessage(MessageFormat.format(PorteApplicativeCostanti.MESSAGGIO_ERRORE_DATI_INCOMPLETI_E_NECESSARIO_INDICARE_XX,
							PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_NOTIFICHE_CODICE_RISPOSTA_HTTP_2XX));
					return false;
				}
				try {
					Integer.parseInt(codiceRisposta2xxValueMax);
				}catch (Exception e) {
					this.pd.setMessage(MessageFormat.format(PorteApplicativeCostanti.MESSAGGIO_ERRRORE_FORMATO_NUMERICO_XX_NON_VALIDO,
							PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_NOTIFICHE_CODICE_RISPOSTA_HTTP_2XX));
					return false;
				}
				break;
			case CONSEGNA_COMPLETATA:
				almenoUnaConsegnaCompletata=true;
				break;	
			case CONSEGNA_FALLITA:
				break;		
			}
			
			if(StringUtils.isEmpty(codiceRisposta3xx)) {
				this.pd.setMessage(MessageFormat.format(PorteApplicativeCostanti.MESSAGGIO_ERRORE_DATI_INCOMPLETI_E_NECESSARIO_INDICARE_XX,
						PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_NOTIFICHE_CODICE_RISPOSTA_HTTP_3XX));
				return false;
			}
			
			// 3xx
			TipoGestioneNotificaTrasporto gestioneTrasporto3xx = TipoGestioneNotificaTrasporto.toEnumConstant(codiceRisposta3xx);
			switch(gestioneTrasporto3xx) {
			case CODICI_CONSEGNA_COMPLETATA:
				almenoUnaConsegnaCompletata=true;
				if(StringUtils.isEmpty(codiceRisposta3xxValue)) {
					this.pd.setMessage(MessageFormat.format(PorteApplicativeCostanti.MESSAGGIO_ERRORE_DATI_INCOMPLETI_E_NECESSARIO_INDICARE_XX,
							PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_NOTIFICHE_CODICE_RISPOSTA_HTTP_3XX));
					return false;
				}
				
				if(!StringUtils.containsOnly(codiceRisposta3xxValue, PorteApplicativeCostanti.VALUE_PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_NOTIFICHE_CODICE_RISPOSTA_HTTP_2XX_CARATTERI_CONSENTITI)) {
					this.pd.setMessage(MessageFormat.format(PorteApplicativeCostanti.MESSAGGIO_ERRORE_CODICI_NON_VALIDI_NEL_CAMPO_XX,
							PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_NOTIFICHE_CODICE_RISPOSTA_HTTP_3XX));
					return false;
				}
				
				List<String> codiceRisposta3xxValues = Arrays.asList(codiceRisposta3xxValue.split(","));
				
				for (String code : codiceRisposta3xxValues) {
					try {
						int val = Integer.parseInt(code);
						
						if(val < 300 || val > 399) {
							this.pd.setMessage(MessageFormat.format(PorteApplicativeCostanti.MESSAGGIO_ERRRORE_CODICE_MIN_MAX_XX_NON_VALIDO,
									PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_NOTIFICHE_CODICE_RISPOSTA_HTTP_3XX, 300, 399));
							return false;
						}
					}catch (Exception e) {
						this.pd.setMessage(MessageFormat.format(PorteApplicativeCostanti.MESSAGGIO_ERRRORE_FORMATO_NUMERICO_XX_NON_VALIDO,
								PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_NOTIFICHE_CODICE_RISPOSTA_HTTP_3XX));
						return false;
					}
				}
				
				break;
			case INTERVALLO_CONSEGNA_COMPLETATA:
				almenoUnaConsegnaCompletata=true;
				if(StringUtils.isEmpty(codiceRisposta3xxValueMin)) {
					this.pd.setMessage(MessageFormat.format(PorteApplicativeCostanti.MESSAGGIO_ERRORE_DATI_INCOMPLETI_E_NECESSARIO_INDICARE_XX,
							PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_NOTIFICHE_CODICE_RISPOSTA_HTTP_3XX));
					return false;
				}
				try {
					Integer.parseInt(codiceRisposta3xxValueMin);
				}catch (Exception e) {
					this.pd.setMessage(MessageFormat.format(PorteApplicativeCostanti.MESSAGGIO_ERRRORE_FORMATO_NUMERICO_XX_NON_VALIDO,
							PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_NOTIFICHE_CODICE_RISPOSTA_HTTP_3XX));
					return false;
				}
				
				if(StringUtils.isEmpty(codiceRisposta3xxValueMax)) {
					this.pd.setMessage(MessageFormat.format(PorteApplicativeCostanti.MESSAGGIO_ERRORE_DATI_INCOMPLETI_E_NECESSARIO_INDICARE_XX,
							PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_NOTIFICHE_CODICE_RISPOSTA_HTTP_3XX));
					return false;
				}
				try {
					Integer.parseInt(codiceRisposta3xxValueMax);
				}catch (Exception e) {
					this.pd.setMessage(MessageFormat.format(PorteApplicativeCostanti.MESSAGGIO_ERRRORE_FORMATO_NUMERICO_XX_NON_VALIDO,
							PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_NOTIFICHE_CODICE_RISPOSTA_HTTP_3XX));
					return false;
				}
				
				break;
			case CONSEGNA_COMPLETATA:
				almenoUnaConsegnaCompletata=true;
				break;	
			case CONSEGNA_FALLITA:
				break;		
			}
			
			if(StringUtils.isEmpty(codiceRisposta4xx)) {
				this.pd.setMessage(MessageFormat.format(PorteApplicativeCostanti.MESSAGGIO_ERRORE_DATI_INCOMPLETI_E_NECESSARIO_INDICARE_XX,
						PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_NOTIFICHE_CODICE_RISPOSTA_HTTP_4XX));
				return false;
			}
			
			// 4xx
			TipoGestioneNotificaTrasporto gestioneTrasporto4xx = TipoGestioneNotificaTrasporto.toEnumConstant(codiceRisposta4xx);
			switch(gestioneTrasporto4xx) {
			case CODICI_CONSEGNA_COMPLETATA:
				almenoUnaConsegnaCompletata=true;
				if(StringUtils.isEmpty(codiceRisposta4xxValue)) {
					this.pd.setMessage(MessageFormat.format(PorteApplicativeCostanti.MESSAGGIO_ERRORE_DATI_INCOMPLETI_E_NECESSARIO_INDICARE_XX,
							PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_NOTIFICHE_CODICE_RISPOSTA_HTTP_4XX));
					return false;
				}
				
				if(!StringUtils.containsOnly(codiceRisposta4xxValue, PorteApplicativeCostanti.VALUE_PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_NOTIFICHE_CODICE_RISPOSTA_HTTP_2XX_CARATTERI_CONSENTITI)) {
					this.pd.setMessage(MessageFormat.format(PorteApplicativeCostanti.MESSAGGIO_ERRORE_CODICI_NON_VALIDI_NEL_CAMPO_XX,
							PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_NOTIFICHE_CODICE_RISPOSTA_HTTP_4XX));
					return false;
				}
				
				List<String> codiceRisposta4xxValues = Arrays.asList(codiceRisposta4xxValue.split(","));
				
				for (String code : codiceRisposta4xxValues) {
					try {
						int val = Integer.parseInt(code);
						
						if(val < 400 || val > 499) {
							this.pd.setMessage(MessageFormat.format(PorteApplicativeCostanti.MESSAGGIO_ERRRORE_CODICE_MIN_MAX_XX_NON_VALIDO,
									PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_NOTIFICHE_CODICE_RISPOSTA_HTTP_4XX, 400, 499));
							return false;
						}
					}catch (Exception e) {
						this.pd.setMessage(MessageFormat.format(PorteApplicativeCostanti.MESSAGGIO_ERRRORE_FORMATO_NUMERICO_XX_NON_VALIDO,
								PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_NOTIFICHE_CODICE_RISPOSTA_HTTP_4XX));
						return false;
					}
				}
				
				break;
			case INTERVALLO_CONSEGNA_COMPLETATA:
				almenoUnaConsegnaCompletata=true;
				if(StringUtils.isEmpty(codiceRisposta4xxValueMin)) {
					this.pd.setMessage(MessageFormat.format(PorteApplicativeCostanti.MESSAGGIO_ERRORE_DATI_INCOMPLETI_E_NECESSARIO_INDICARE_XX,
							PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_NOTIFICHE_CODICE_RISPOSTA_HTTP_4XX));
					return false;
				}
				try {
					Integer.parseInt(codiceRisposta4xxValueMin);
				}catch (Exception e) {
					this.pd.setMessage(MessageFormat.format(PorteApplicativeCostanti.MESSAGGIO_ERRRORE_FORMATO_NUMERICO_XX_NON_VALIDO,
							PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_NOTIFICHE_CODICE_RISPOSTA_HTTP_4XX));
					return false;
				}
				
				if(StringUtils.isEmpty(codiceRisposta4xxValueMax)) {
					this.pd.setMessage(MessageFormat.format(PorteApplicativeCostanti.MESSAGGIO_ERRORE_DATI_INCOMPLETI_E_NECESSARIO_INDICARE_XX,
							PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_NOTIFICHE_CODICE_RISPOSTA_HTTP_4XX));
					return false;
				}
				try {
					Integer.parseInt(codiceRisposta4xxValueMax);
				}catch (Exception e) {
					this.pd.setMessage(MessageFormat.format(PorteApplicativeCostanti.MESSAGGIO_ERRRORE_FORMATO_NUMERICO_XX_NON_VALIDO,
							PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_NOTIFICHE_CODICE_RISPOSTA_HTTP_4XX));
					return false;
				}
				break;
			case CONSEGNA_COMPLETATA:
				almenoUnaConsegnaCompletata=true;
				break;	
			case CONSEGNA_FALLITA:
				break;		
			}
			
			if(StringUtils.isEmpty(codiceRisposta5xx)) {
				this.pd.setMessage(MessageFormat.format(PorteApplicativeCostanti.MESSAGGIO_ERRORE_DATI_INCOMPLETI_E_NECESSARIO_INDICARE_XX,
						PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_NOTIFICHE_CODICE_RISPOSTA_HTTP_5XX));
				return false;
			}
			
			// 5xx
			TipoGestioneNotificaTrasporto gestioneTrasporto5xx = TipoGestioneNotificaTrasporto.toEnumConstant(codiceRisposta5xx);
			switch(gestioneTrasporto5xx) {
			case CODICI_CONSEGNA_COMPLETATA:
				almenoUnaConsegnaCompletata=true;
				if(StringUtils.isEmpty(codiceRisposta5xxValue)) {
					this.pd.setMessage(MessageFormat.format(PorteApplicativeCostanti.MESSAGGIO_ERRORE_DATI_INCOMPLETI_E_NECESSARIO_INDICARE_XX,
							PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_NOTIFICHE_CODICE_RISPOSTA_HTTP_5XX));
					return false;
				}
				
				if(!StringUtils.containsOnly(codiceRisposta5xxValue, PorteApplicativeCostanti.VALUE_PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_NOTIFICHE_CODICE_RISPOSTA_HTTP_2XX_CARATTERI_CONSENTITI)) {
					this.pd.setMessage(MessageFormat.format(PorteApplicativeCostanti.MESSAGGIO_ERRORE_CODICI_NON_VALIDI_NEL_CAMPO_XX,
							PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_NOTIFICHE_CODICE_RISPOSTA_HTTP_5XX));
					return false;
				}
				
				List<String> codiceRisposta5xxValues = Arrays.asList(codiceRisposta5xxValue.split(","));
				
				for (String code : codiceRisposta5xxValues) {
					try {
						int val = Integer.parseInt(code);
						
						if(val < 500 || val > 599) {
							this.pd.setMessage(MessageFormat.format(PorteApplicativeCostanti.MESSAGGIO_ERRRORE_CODICE_MIN_MAX_XX_NON_VALIDO,
									PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_NOTIFICHE_CODICE_RISPOSTA_HTTP_5XX, 500, 599));
							return false;
						}
					}catch (Exception e) {
						this.pd.setMessage(MessageFormat.format(PorteApplicativeCostanti.MESSAGGIO_ERRRORE_FORMATO_NUMERICO_XX_NON_VALIDO,
								PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_NOTIFICHE_CODICE_RISPOSTA_HTTP_5XX));
						return false;
					}
				}
				
				break;
			case INTERVALLO_CONSEGNA_COMPLETATA:
				almenoUnaConsegnaCompletata=true;
				if(StringUtils.isEmpty(codiceRisposta5xxValueMin)) {
					this.pd.setMessage(MessageFormat.format(PorteApplicativeCostanti.MESSAGGIO_ERRORE_DATI_INCOMPLETI_E_NECESSARIO_INDICARE_XX,
							PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_NOTIFICHE_CODICE_RISPOSTA_HTTP_5XX));
					return false;
				}
				try {
					Integer.parseInt(codiceRisposta5xxValueMin);
				}catch (Exception e) {
					this.pd.setMessage(MessageFormat.format(PorteApplicativeCostanti.MESSAGGIO_ERRRORE_FORMATO_NUMERICO_XX_NON_VALIDO,
							PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_NOTIFICHE_CODICE_RISPOSTA_HTTP_5XX));
					return false;
				}
				
				if(StringUtils.isEmpty(codiceRisposta5xxValueMax)) {
					this.pd.setMessage(MessageFormat.format(PorteApplicativeCostanti.MESSAGGIO_ERRORE_DATI_INCOMPLETI_E_NECESSARIO_INDICARE_XX,
							PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_NOTIFICHE_CODICE_RISPOSTA_HTTP_5XX));
					return false;
				}
				try {
					Integer.parseInt(codiceRisposta5xxValueMax);
				}catch (Exception e) {
					this.pd.setMessage(MessageFormat.format(PorteApplicativeCostanti.MESSAGGIO_ERRRORE_FORMATO_NUMERICO_XX_NON_VALIDO,
							PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_NOTIFICHE_CODICE_RISPOSTA_HTTP_5XX));
					return false;
				}
				break;
			case CONSEGNA_COMPLETATA:
				almenoUnaConsegnaCompletata=true;
				break;	
			case CONSEGNA_FALLITA:
				break;		
			}
			
			if(StringUtils.isEmpty(gestioneFault)) {
				this.pd.setMessage(MessageFormat.format(PorteApplicativeCostanti.MESSAGGIO_ERRORE_DATI_INCOMPLETI_E_NECESSARIO_INDICARE_XX,
						PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_NOTIFICHE_GESTIONE));
				return false;
			}
			
			TipoGestioneNotificaFault fault = TipoGestioneNotificaFault.toEnumConstant(gestioneFault);
			String tipoFaultCompletato = null;
			switch(fault) {
			case CONSEGNA_COMPLETATA:
				almenoUnaConsegnaCompletata=true;
				break;
			case CONSEGNA_FALLITA:
				break;
			case CONSEGNA_COMPLETATA_PERSONALIZZATA:
			case CONSEGNA_FALLITA_PERSONALIZZATA:
				almenoUnaConsegnaCompletata=true;
				// almeno un elemento obbligatorio
				if(StringUtils.isEmpty(faultCode) && StringUtils.isEmpty(faultActor) && StringUtils.isEmpty(faultMessage)) {
					StringBuilder sb = new StringBuilder();
					
					sb.append("'");
					sb.append(serviceBinding.equals(ServiceBinding.SOAP) ? PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_NOTIFICHE_CODE : 
						PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_NOTIFICHE_TYPE);
					sb.append("', '");
					
					sb.append(serviceBinding.equals(ServiceBinding.SOAP) ? PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_NOTIFICHE_ACTOR : 
						PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_NOTIFICHE_STATUS);
					
					sb.append("' o '");
					
					sb.append(serviceBinding.equals(ServiceBinding.SOAP) ? PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_NOTIFICHE_MESSAGE : 
						PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_NOTIFICHE_CLAIMS);
					
					sb.append("'");
					
					this.pd.setMessage(MessageFormat.format(PorteApplicativeCostanti.MESSAGGIO_ERRORE_DATI_INCOMPLETI_E_NECESSARIO_INDICARE_ALMENO_UNO_TRA_XX,	sb.toString()));
					return false;
				}
				if(!StringUtils.isEmpty(faultCode)) { 
					String label = serviceBinding.equals(ServiceBinding.SOAP) ? PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_NOTIFICHE_CODE : 
						PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_NOTIFICHE_TYPE;
//					this.pd.setMessage(MessageFormat.format(PorteApplicativeCostanti.MESSAGGIO_ERRORE_DATI_INCOMPLETI_E_NECESSARIO_INDICARE_XX,	label));
//					return false;
					tipoFaultCompletato = label;
				}
				if(!StringUtils.isEmpty(faultActor)) {
					String label = serviceBinding.equals(ServiceBinding.SOAP) ? PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_NOTIFICHE_ACTOR : 
						PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_NOTIFICHE_STATUS;
//					this.pd.setMessage(MessageFormat.format(PorteApplicativeCostanti.MESSAGGIO_ERRORE_DATI_INCOMPLETI_E_NECESSARIO_INDICARE_XX,	label));
//					return false;
					if(tipoFaultCompletato!=null) {
						tipoFaultCompletato = tipoFaultCompletato +", ";
					}
					else {
						tipoFaultCompletato = "";
					}
					tipoFaultCompletato = tipoFaultCompletato + label;
				}
				if(!StringUtils.isEmpty(faultMessage)) {
					
					String label = serviceBinding.equals(ServiceBinding.SOAP) ? PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_NOTIFICHE_MESSAGE : 
						PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_NOTIFICHE_CLAIMS;
					
					if(serviceBinding.equals(ServiceBinding.REST) ) {
						Scanner scanner = new Scanner(faultMessage);
						try {
							while (scanner.hasNextLine()) {
								String line = scanner.nextLine();
								if(line==null || line.trim().equals("")) {
									continue;
								}
								if(line.contains("=")==false) {
									this.pd.setMessage(CostantiControlStation.MESSAGGIO_ERRORE_AUTORIZZAZIONE_TOKEN);
									return false;
								}
							}
						}finally {
							scanner.close();
						}
						
						if(this.checkLength(faultMessage, PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_NOTIFICHE_CLAIMS,-1,4000)==false) {
							return false;
						}
					}
					
					if(tipoFaultCompletato!=null) {
						tipoFaultCompletato = tipoFaultCompletato +", ";
					}
					else {
						tipoFaultCompletato = "";
					}
					tipoFaultCompletato = tipoFaultCompletato + label;
				}
				
				if(tipoFaultCompletato!=null) {
					String prefix = "";
					if(TipoGestioneNotificaFault.CONSEGNA_FALLITA_PERSONALIZZATA.equals(fault)) {
						prefix = " non";
					}
					tipoFaultCompletato = prefix+" contenente le personalizzazioni definite per "+tipoFaultCompletato;
				}
				break;
			}
			
			if(!almenoUnaConsegnaCompletata) {
				
				StringBuilder sb = new StringBuilder();
				sb.append(PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_NOTIFICHE_CODICE_RISPOSTA_HTTP_2XX);
				sb.append(", ");
				sb.append(PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_NOTIFICHE_CODICE_RISPOSTA_HTTP_3XX);
				sb.append(", ");
				sb.append(PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_NOTIFICHE_CODICE_RISPOSTA_HTTP_4XX);
				sb.append(", ");
				sb.append(PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_NOTIFICHE_CODICE_RISPOSTA_HTTP_5XX);
				sb.append(", ");
				sb.append(serviceBinding.equals(ServiceBinding.SOAP) ? PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_NOTIFICHE_SOAP_FAULT : 
					PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_NOTIFICHE_PROBLEM_DETAIL);
				
				this.pd.setMessage(MessageFormat.format(PorteApplicativeCostanti.MESSAGGIO_ERRORE_DATI_INCOMPLETI_E_NECESSARIO_INDICARE_ALMENO_UNA_CONSEGNA_COMPLETATA_XX,	sb.toString()));
				return false;
			}
			
		} catch (Exception e) {
			this.log.error("Exception: " + e.getMessage(), e);
			throw new Exception(e);
		}
		return true;
	}
}
