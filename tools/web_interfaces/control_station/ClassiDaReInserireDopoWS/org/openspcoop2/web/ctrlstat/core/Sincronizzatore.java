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


package org.openspcoop2.web.ctrlstat.core;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;

import org.slf4j.Logger;
import org.openspcoop2.utils.log.LogUtilities;
import org.apache.logging.log4j.PropertyConfigurator;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.core.id.IDServizio;
import org.openspcoop2.web.ctrlstat.dao.PdDControlStation;

/**
 * Sincronizzatore
 * 
 * @author Andrea Poli (apoli@link.it)
 * @author Stefano Corallo (corallo@link.it)
 * @author Sandra Giangrandi (sandra@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 * 
 */
public class Sincronizzatore {

	// Logger utilizzato per debug
	private static Logger log = null;

	public static void main(String args[]) {
		// configuro il logger
		PropertyConfigurator.configure(Sincronizzatore.class.getResource("/console.log4j.properties"));
		Sincronizzatore.log = LogUtilities.getLogger("sincronizzatore");

		String fileProperties = "/sincronizzatore.properties";
		if (args.length > 0) {
			if (args[0] != null && "".equals(args[0])) {
				fileProperties = args[0];
			}
		}

		Sincronizzatore.log.info("Leggo file di configurazione : " + fileProperties);
		InputStream inProp = null;

		try {

			inProp = Sincronizzatore.class.getResourceAsStream(fileProperties);
			Properties prop = new Properties();
			prop.load(inProp);

			Enumeration<?> en = prop.keys();
			while (en.hasMoreElements()) {
				String key = (String) en.nextElement();
				Sincronizzatore.log.debug(key + "=" + prop.getProperty(key));
			}

			ControlStationCore core = new ControlStationCore();
			SincronizzatoreLib sincronizzatore = new SincronizzatoreLib();

			String tipologiaSincronizzazione = prop.getProperty("TIPOLOGIA_SYNC");
			if (tipologiaSincronizzazione == null || "".equals(tipologiaSincronizzazione)) {
				Sincronizzatore.log.debug("Nessuna tipologia di Sincronizzazione impostata.");
				Sincronizzatore.log.debug("Interrompo Sincronizzazione");
				return;
			}

			switch (TipiSincronizzazione.valueOf(tipologiaSincronizzazione)) {
				case FILTRO:
					Sincronizzatore.log.info("Richiesta Sincronizzazione con FILTRO");

					String tipoSoggetto = prop.getProperty("TIPO_SOGGETTO");
					String nomeSoggetto = prop.getProperty("NOME_SOGGETTO");
					String tipoServizio = prop.getProperty("TIPO_SERVIZIO");
					String nomeServizio = prop.getProperty("NOME_SERVIZIO");

					IDSoggetto soggetto = null;
					if (tipoSoggetto != null || nomeSoggetto != null) {
						soggetto = new IDSoggetto();
						soggetto.setTipo(tipoSoggetto);
						soggetto.setNome(nomeSoggetto);
					}
					IDServizio servizio = null;
					if (tipoServizio != null || nomeServizio != null || soggetto != null) {
						servizio = new IDServizio();
						servizio.setTipoServizio(tipoServizio);
						servizio.setServizio(nomeServizio);
						servizio.setSoggettoErogatore(soggetto);
					}

					FiltroSincronizzazione filtro = null;
					if (soggetto != null || servizio != null) {
						filtro = new FiltroSincronizzazione();
						filtro.setServizio(servizio);
						filtro.setSoggetto(soggetto);
					}

					// controllo se e' stato specificata una lista di pdd nel
					// file
					// di configurazione
					ArrayList<PdDControlStation> pdds = Sincronizzatore.parseListaPdd(prop.getProperty("PDD_LIST"), core);
					if (pdds != null) {
						Sincronizzatore.log.info("Lista PDD specificata : " + Sincronizzatore.getNomiPdd(pdds));
					} else {
						Sincronizzatore.log.info("Nessuna Lista PDD specificata recupero tutte le pdd");
						pdds = (ArrayList<PdDControlStation>) core.pddList("", new Search(true));
					}

					try {

						sincronizzatore.syncByFilter(pdds, filtro);
						Sincronizzatore.log.info("Sincronizzazione FILTRO [" + filtro.toString() + "] completata.");
					} catch (Exception e) {
						Sincronizzatore.log.info("Sincronizzazione FILTRO [" + filtro.toString() + "]fallita.");
						Sincronizzatore.log.error(e);
					}

					break;
				case ALL:
					boolean resetOK = new Boolean(prop.getProperty("RESET")).booleanValue();
					boolean resetPdD = new Boolean(prop.getProperty("RESET_PDD")).booleanValue();
					boolean resetRegistro = new Boolean(prop.getProperty("RESET_REGISTRO")).booleanValue();
					boolean resetGE = new Boolean(prop.getProperty("RESET_GE")).booleanValue();
					boolean resetRepositoryAutorizzazioni = new Boolean(prop.getProperty("RESET_RepositoryAutorizzazioni")).booleanValue();

					Sincronizzatore.log.info("Richiesta Sincronizzazione Totale");
					Sincronizzatore.log.info("Reset Before Sync [" + resetOK + "]");
					if (resetOK) {
						Sincronizzatore.log.info("Reset PdD         [" + resetPdD + "]");
						Sincronizzatore.log.info("Reset Registro    [" + resetRegistro + "]");
						Sincronizzatore.log.info("Reset GE          [" + resetGE + "]");
						Sincronizzatore.log.info("Reset RepositoryAutorizzazioni        [" + resetRepositoryAutorizzazioni + "]");
					}

					// Reset PdD
					if (resetOK) {
						// controllo se e' stato specificata una lista di pdd
						// nel
						// file di configurazione
						pdds = Sincronizzatore.parseListaPdd(prop.getProperty("PDD_LIST"), core);
						if (pdds != null) {
							Sincronizzatore.log.info("Lista PDD specificata : " + Sincronizzatore.getNomiPdd(pdds));
						} else {
							Sincronizzatore.log.info("Nessuna Lista PDD specificata recupero tutte le pdd");
							pdds = (ArrayList<PdDControlStation>) core.pddList("", new Search(true));
						}

						if (resetPdD) {
							Sincronizzatore.log.info("Eseguo Reset delle PdD");
							try {
								sincronizzatore.resetPdD(pdds.toArray(new PdDControlStation[pdds.size()]));
								Sincronizzatore.log.info("Reset PdD effettuato!");
							} catch (Exception e) {
								Sincronizzatore.log.info("Errore durante RESET PdD");
								Sincronizzatore.log.error(e);
								resetOK = false;
							}
						}

						if (resetRegistro) {
							// Reset Registro
							Sincronizzatore.log.info("Eseguo Reset Registro");
							try {
								sincronizzatore.resetRegistroServizi();
								Sincronizzatore.log.info("Reset Registro effettuato!");
							} catch (Exception e) {
								Sincronizzatore.log.info("Errore durante RESET RegistroServizi");
								Sincronizzatore.log.error(e);
								resetOK = false;
							}
						}

						if (resetRepositoryAutorizzazioni) {
							// Reset RepositoryAutorizzazioni
							Sincronizzatore.log.info("Eseguo Reset RepositoryAutorizzazioni");
							try {
								sincronizzatore.resetRepositoryAutorizzazioni();
								Sincronizzatore.log.info("Reset RepositoryAutorizzazioni effettuato!");
							} catch (Exception e) {
								Sincronizzatore.log.info("Errore durante RESET RepositoryAutorizzazioni");
								Sincronizzatore.log.error(e);
								resetOK = false;
							}
						}

						if (resetGE) {
							// Reset Gestore Eventi
							Sincronizzatore.log.info("Eseguo Reset Gestore Eventi");
							try {
								sincronizzatore.resetGestoreEventi();
								Sincronizzatore.log.info("Reset Gestore Eventi effettuato!");
							} catch (Exception e) {
								Sincronizzatore.log.info("Errore durante RESET Gestore Eventi", e);
								resetOK = false;
							}
						}
						if (resetOK && !resetGE && !resetRepositoryAutorizzazioni && !resetPdD && !resetRegistro) {
							Sincronizzatore.log.info("Impossibile eseguire la Reset in quanto non e' stato specificato motore di reset.");
							Sincronizzatore.log.info("Reset PdD         [" + resetPdD + "]");
							Sincronizzatore.log.info("Reset Registro    [" + resetRegistro + "]");
							Sincronizzatore.log.info("Reset GE          [" + resetGE + "]");
							Sincronizzatore.log.info("Reset RepositoryAutorizzazioni        [" + resetRepositoryAutorizzazioni + "]");

							Sincronizzatore.log.info("Interrompo Sincronizzazione");
							return;

						}
						// Sincronizzo tutto se le reset sono andate a buon fine
						if (resetOK) {
							Sincronizzatore.log.info("Avvio Sincronizzazione...");
							sincronizzatore.syncAll();
							Sincronizzatore.log.info("Sincronizzazione Completata");
						} else {
							Sincronizzatore.log.info("Sincronizzazione abortita a casa di errori.");
							return;
						}
					} else {

						Sincronizzatore.log.info("Avvio Sincronizzazione Totale...");
						sincronizzatore.syncAll();
						Sincronizzatore.log.info("Sincronizzazione Totale Completata");

					}

					break;

				case RESET_ALL:
					resetOK = new Boolean(prop.getProperty("RESET")).booleanValue();
					resetPdD = new Boolean(prop.getProperty("RESET_PDD")).booleanValue();
					resetRegistro = new Boolean(prop.getProperty("RESET_REGISTRO")).booleanValue();
					resetGE = new Boolean(prop.getProperty("RESET_GE")).booleanValue();
					resetRepositoryAutorizzazioni = new Boolean(prop.getProperty("RESET_RepositoryAutorizzazioni")).booleanValue();

					Sincronizzatore.log.info("Richiesta RESET Totale");
					Sincronizzatore.log.info("Reset Value [" + resetOK + "]");
					if (resetOK) {
						Sincronizzatore.log.info("Reset PdD         [" + resetPdD + "]");
						Sincronizzatore.log.info("Reset Registro    [" + resetRegistro + "]");
						Sincronizzatore.log.info("Reset GE          [" + resetGE + "]");
						Sincronizzatore.log.info("Reset RepositoryAutorizzazioni        [" + resetRepositoryAutorizzazioni + "]");
					}

					if (resetOK) {
						// controllo se e' stato specificata una lista di pdd
						// nel
						// file di configurazione
						pdds = Sincronizzatore.parseListaPdd(prop.getProperty("PDD_LIST"), core);
						if (pdds != null) {
							Sincronizzatore.log.info("Lista PDD specificata : " + Sincronizzatore.getNomiPdd(pdds));
						} else {
							Sincronizzatore.log.info("Nessuna Lista PDD specificata recupero tutte le pdd");
							pdds = (ArrayList<PdDControlStation>) core.pddList("", new Search(true));
						}

						if (resetPdD) {
							Sincronizzatore.log.info("Eseguo Reset delle PdD");
							try {
								sincronizzatore.resetPdD(pdds.toArray(new PdDControlStation[pdds.size()]));
								Sincronizzatore.log.info("Reset PdD effettuato!");
							} catch (Exception e) {
								Sincronizzatore.log.info("Errore durante RESET PdD");
								Sincronizzatore.log.error(e);
								resetOK = false;
							}
						}

						if (resetRegistro) {
							// Reset Registro
							Sincronizzatore.log.info("Eseguo Reset Registro");
							try {
								sincronizzatore.resetRegistroServizi();
								Sincronizzatore.log.info("Reset Registro effettuato!");
							} catch (Exception e) {
								Sincronizzatore.log.info("Errore durante RESET RegistroServizi");
								Sincronizzatore.log.error(e);
								resetOK = false;
							}
						}

						if (resetRepositoryAutorizzazioni) {
							// Reset RepositoryAutorizzazioni
							Sincronizzatore.log.info("Eseguo Reset RepositoryAutorizzazioni");
							try {
								sincronizzatore.resetRepositoryAutorizzazioni();
								Sincronizzatore.log.info("Reset RepositoryAutorizzazioni effettuato!");
							} catch (Exception e) {
								Sincronizzatore.log.info("Errore durante RESET RepositoryAutorizzazioni");
								Sincronizzatore.log.error(e);
								resetOK = false;
							}
						}

						if (resetGE) {
							// Reset Gestore Eventi
							Sincronizzatore.log.info("Eseguo Reset Gestore Eventi");
							try {
								sincronizzatore.resetGestoreEventi();
								Sincronizzatore.log.info("Reset Gestore Eventi effettuato!");
							} catch (Exception e) {
								Sincronizzatore.log.info("Errore durante RESET Gestore Eventi", e);
								resetOK = false;
							}
						}
						if (resetOK && !resetGE && !resetRepositoryAutorizzazioni && !resetPdD && !resetRegistro) {
							Sincronizzatore.log.info("Impossibile eseguire la Reset in quanto non sono stati specificati parametri di reset.");
							Sincronizzatore.log.info("Reset PdD         [" + resetPdD + "]");
							Sincronizzatore.log.info("Reset Registro    [" + resetRegistro + "]");
							Sincronizzatore.log.info("Reset GE          [" + resetGE + "]");
							Sincronizzatore.log.info("Reset RepositoryAutorizzazioni        [" + resetRepositoryAutorizzazioni + "]");

							Sincronizzatore.log.info("Interrompo Sincronizzazione");
							return;

						}
					}
					break;
				default:
					Sincronizzatore.log.debug("Tipologia di Sincronizzazione [" + TipiSincronizzazione.valueOf(tipologiaSincronizzazione) + "] non supportata.");
					break;
			}

		} catch (Exception e) {
			Sincronizzatore.log.error("Errore durante Sincronizzazione...", e);
		}
	}

	/**
	 * Data una stringa contenente i nomi delle Porte di Dominio separate da ,
	 * ritorna una lista
	 * 
	 * @param cvsPddList
	 * @return
	 */
	private static ArrayList<PdDControlStation> parseListaPdd(String cvsPddList, ControlStationCore core) {

		if (cvsPddList == null || "".equals(cvsPddList))
			return null;

		ArrayList<PdDControlStation> listaPdd = null;
		String[] list = cvsPddList.split(",");
		if (list != null) {
			listaPdd = new ArrayList<PdDControlStation>();
			for (int i = 0; i < list.length; i++) {
				try {
					PdDControlStation pdd = core.getPdDControlStation(list[i]);
					listaPdd.add(pdd);
				} catch (Exception e) {
					Sincronizzatore.log.error("La pdd [" + list[i] + "] non esiste.");
				}
			}
		}
		return listaPdd;
	}

	private static String getNomiPdd(List<PdDControlStation> lista) {
		ArrayList<String> nomiPdd = new ArrayList<String>();
		for (PdDControlStation pdd : lista) {
			nomiPdd.add(pdd.getNome());
		}

		return nomiPdd.toString();
	}

}
