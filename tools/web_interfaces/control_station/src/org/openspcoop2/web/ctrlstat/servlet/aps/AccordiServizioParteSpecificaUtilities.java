package org.openspcoop2.web.ctrlstat.servlet.aps;

import java.util.ArrayList;
import java.util.List;

import org.openspcoop2.core.config.PortaApplicativa;
import org.openspcoop2.core.config.PortaApplicativaAzione;
import org.openspcoop2.core.config.PortaApplicativaServizioApplicativo;
import org.openspcoop2.core.config.PortaDelegata;
import org.openspcoop2.core.config.PortaDelegataAzione;
import org.openspcoop2.core.config.ServizioApplicativo;
import org.openspcoop2.core.config.constants.PortaApplicativaAzioneIdentificazione;
import org.openspcoop2.core.config.constants.PortaDelegataAzioneIdentificazione;
import org.openspcoop2.core.config.driver.FiltroRicercaPorteApplicative;
import org.openspcoop2.core.config.driver.FiltroRicercaPorteDelegate;
import org.openspcoop2.core.id.IDPortaApplicativa;
import org.openspcoop2.core.id.IDPortaDelegata;
import org.openspcoop2.core.id.IDServizio;
import org.openspcoop2.core.id.IDServizioApplicativo;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.core.registry.AccordoServizioParteComune;
import org.openspcoop2.core.registry.AccordoServizioParteSpecifica;
import org.openspcoop2.core.registry.driver.IDServizioFactory;
import org.openspcoop2.web.ctrlstat.servlet.apc.AccordiServizioParteComuneCore;
import org.openspcoop2.web.ctrlstat.servlet.pa.PorteApplicativeCore;
import org.openspcoop2.web.ctrlstat.servlet.pd.PorteDelegateCore;
import org.openspcoop2.web.ctrlstat.servlet.sa.ServiziApplicativiCore;

public class AccordiServizioParteSpecificaUtilities {

	public static List<Object> getOggettiDaAggiornare(AccordoServizioParteSpecifica asps, AccordiServizioParteSpecificaCore apsCore) throws Exception {

		PorteDelegateCore porteDelegateCore = new PorteDelegateCore(apsCore);
		PorteApplicativeCore porteApplicativeCore = new PorteApplicativeCore(apsCore);
		ServiziApplicativiCore saCore = new ServiziApplicativiCore(apsCore);
		AccordiServizioParteComuneCore apcCore = new AccordiServizioParteComuneCore(apsCore);
		
		// Se sono cambiati il tipo o il nome allora devo aggiornare
		// anche le porte delegate e porte applicative
		List<PortaDelegata> listaPD = new ArrayList<PortaDelegata>();
		List<PortaApplicativa> listaPA = new ArrayList<PortaApplicativa>();
		List<ServizioApplicativo> listaPA_SA = new ArrayList<ServizioApplicativo>();

		// check dati modificati
		String newUri = IDServizioFactory.getInstance().getUriFromAccordo(asps);
		String oldUri = IDServizioFactory.getInstance().getUriFromIDServizio(asps.getOldIDServizioForUpdate());
		if (!newUri.equals(oldUri)) {


			// check PD
			FiltroRicercaPorteDelegate filtroPD = new FiltroRicercaPorteDelegate();
			filtroPD.setTipoSoggettoErogatore(asps.getOldIDServizioForUpdate().getSoggettoErogatore().getTipo());
			filtroPD.setNomeSoggettoErogatore(asps.getOldIDServizioForUpdate().getSoggettoErogatore().getNome());
			filtroPD.setTipoServizio(asps.getOldIDServizioForUpdate().getTipo());
			filtroPD.setNomeServizio(asps.getOldIDServizioForUpdate().getNome());
			filtroPD.setVersioneServizio(asps.getOldIDServizioForUpdate().getVersione());
			List<IDPortaDelegata> listIdsPorteDelegate = porteDelegateCore.getAllIdPorteDelegate(filtroPD);
			if(listIdsPorteDelegate!=null && !listIdsPorteDelegate.isEmpty()) {

				String tmpLocationSuffix = "/" + asps.getOldIDServizioForUpdate().getSoggettoErogatore().getTipo() + "_" + asps.getOldIDServizioForUpdate().getSoggettoErogatore().getNome() + 
						"/" + asps.getOldIDServizioForUpdate().getTipo() + "_" + asps.getOldIDServizioForUpdate().getNome();

				String locationSuffix = tmpLocationSuffix +
						"/v" + asps.getOldIDServizioForUpdate().getVersione().intValue();

				// backward compatibility: provare ad eliminare la v, che prima non veniva utilizzata
				String locationSuffix_oldWithoutV = tmpLocationSuffix +
						"/" + asps.getOldIDServizioForUpdate().getVersione().intValue();

				String newLocationSuffix = "/" + asps.getOldIDServizioForUpdate().getSoggettoErogatore().getTipo() + "_" + asps.getOldIDServizioForUpdate().getSoggettoErogatore().getNome() + 
						"/" + asps.getTipo() + "_" + asps.getNome() +
						"/v" + asps.getVersione().intValue();

				for (IDPortaDelegata idPortaDelegata : listIdsPorteDelegate) {
					PortaDelegata tmpPorta = porteDelegateCore.getPortaDelegata(idPortaDelegata);	

					// aggiorno dati servizio
					tmpPorta.getServizio().setTipo(asps.getTipo());
					tmpPorta.getServizio().setNome(asps.getNome());
					tmpPorta.getServizio().setVersione(asps.getVersione());

					String locationPrefix = tmpPorta.getTipoSoggettoProprietario()+"_"+tmpPorta.getNomeSoggettoProprietario();
					String check1 = locationPrefix+locationSuffix;
					String check2 = "__"+locationPrefix+locationSuffix;
					String check1_oldWithoutV = locationPrefix+locationSuffix_oldWithoutV;
					String check2_oldWithoutV = "__"+locationPrefix+locationSuffix_oldWithoutV;
					String parteRimanente = "";
					String nuovoNome = null;
					boolean match = false;
					if(tmpPorta.getNome().equals(check1)) {
						match = true;	
						nuovoNome = locationPrefix+newLocationSuffix;
					}
					else if(tmpPorta.getNome().startsWith(check2)) {
						match = true;	
						parteRimanente = tmpPorta.getNome().substring(check2.length());
						nuovoNome = "__"+locationPrefix+newLocationSuffix+parteRimanente;
					}
					else if(tmpPorta.getNome().equals(check1_oldWithoutV)) {
						match = true;	
						nuovoNome = locationPrefix+newLocationSuffix;
					}
					else if(tmpPorta.getNome().startsWith(check2_oldWithoutV)) {
						match = true;	
						parteRimanente = tmpPorta.getNome().substring(check2_oldWithoutV.length());
						nuovoNome = "__"+locationPrefix+newLocationSuffix+parteRimanente;
					}

					if(match) {
						IDPortaDelegata oldIDPortaDelegataForUpdate = new IDPortaDelegata();
						oldIDPortaDelegataForUpdate.setNome(tmpPorta.getNome());
						tmpPorta.setOldIDPortaDelegataForUpdate(oldIDPortaDelegataForUpdate);
						tmpPorta.setNome(nuovoNome);

						// modifica della descrizione
						String descrizionePD = tmpPorta.getDescrizione();
						if (descrizionePD != null && !descrizionePD.equals("")) {

							// Caso 1: subscription default
							// Subscription from gw/ENTE for service gw/ErogatoreEsterno:gw/EsempioREST:1
							String match_caso = ":"+asps.getOldIDServizioForUpdate().getTipo()+"/"+asps.getOldIDServizioForUpdate().getNome()+":"+asps.getOldIDServizioForUpdate().getVersione().intValue();
							if(descrizionePD.endsWith(match_caso)) {
								String replace_caso = ":"+asps.getTipo()+"/"+asps.getNome()+":"+asps.getVersione().intValue();
								descrizionePD = descrizionePD.replace(match_caso, replace_caso);
							}

							// Caso 2: altra subscription
							// Internal Subscription 'Specific1' for gw_ENTE/gw_ErogatoreEsterno/gw_EsempioREST/1
							String tmpMatchCaso2 = "/"+asps.getOldIDServizioForUpdate().getTipo()+"_"+asps.getOldIDServizioForUpdate().getNome()+"/";
							String match_caso2 = tmpMatchCaso2 +"v"+ asps.getOldIDServizioForUpdate().getVersione().intValue();
							String match_caso2_oldWithoutV = tmpMatchCaso2 + asps.getOldIDServizioForUpdate().getVersione().intValue();
							if(descrizionePD.contains(match_caso2)) {
								String replace_caso2 = "/"+asps.getTipo()+"_"+asps.getNome()+"/v"+asps.getVersione().intValue();
								descrizionePD = descrizionePD.replace(match_caso2, replace_caso2);
							}
							else if(descrizionePD.contains(match_caso2_oldWithoutV)) {
								String replace_caso2 = "/"+asps.getTipo()+"_"+asps.getNome()+"/v"+asps.getVersione().intValue();
								descrizionePD = descrizionePD.replace(match_caso2_oldWithoutV, replace_caso2);
							}

							tmpPorta.setDescrizione(descrizionePD);

						}

						// regex del pattern azione
						// .*(fruitore)/(erogatore)/(servizio)/([^/|^?]*).*
						PortaDelegataAzione pdAzione = tmpPorta.getAzione();
						PortaDelegataAzioneIdentificazione identificazione = pdAzione != null ? pdAzione.getIdentificazione() : null;
						String patterAzione = pdAzione != null ? (pdAzione.getPattern() != null ? pdAzione.getPattern() : "") : "";
						String patternAzionePrefix = ".*/";
						String patternAzioneSuffix1 = "/([^/|^?]*).*";
						// se identificazione urlbased procedo con i controlli
						if (PortaDelegataAzioneIdentificazione.URL_BASED.equals(identificazione)) {
							if (patterAzione.startsWith(patternAzionePrefix) && patterAzione.endsWith(patternAzioneSuffix1)) {
								// caso1
								int startidx = patternAzionePrefix.length();
								int endidx = patterAzione.lastIndexOf(patternAzioneSuffix1);
								String tmpPat = patterAzione.substring(startidx, endidx);
								// a questo punto ottengo una stringa del tipo
								// (fruitore)/(erogatore)/(servizio)
								// se rispetta la regex allora vuol dire che il
								// pattern azione e' quello di default
								// e devo effettuare i cambiamenti
								String regex = "(.*)\\/(.*)\\/(.*)\\/(.*)";
								if (tmpPat.matches(regex)) {
									String[] val = tmpPat.split("/");
									String partFruitore = val[0];
									String partErogatore = val[1];
									String partServizio = val[2];
									String partVersione = val[3];
									String rimanenteRegExp = "";
									int lengthParteRimanenteRegExp = (partFruitore+"/"+partErogatore+"/"+partServizio+"/"+partVersione).length();
									if(tmpPat.length()>lengthParteRimanenteRegExp){
										rimanenteRegExp = tmpPat.substring(lengthParteRimanenteRegExp);
									}	

									boolean matchURL = false;
									String partOld = "(?:"+asps.getOldIDServizioForUpdate().getTipo()+"_)?"+asps.getOldIDServizioForUpdate().getNome()+"";
									String partNew = "(?:"+asps.getTipo()+"_)?"+asps.getNome()+"";

									// vedo se matcha il fruitore
									if (partServizio.equals(partOld)) {
										partServizio = partNew;
										matchURL = true;
									}

									// vedo se matcha anche erogatore (loopback)
									String versioneOld = "v"+(asps.getOldIDServizioForUpdate().getVersione().intValue()+"");
									String versioneOld_oldWithoutV = (asps.getOldIDServizioForUpdate().getVersione().intValue()+"");
									if (partVersione.equals(versioneOld) || partVersione.equals(versioneOld_oldWithoutV)) {
										partVersione = "v"+asps.getVersione().intValue()+"";
										matchURL = true;
									}

									if(matchURL){
										String newPatternAzione = patternAzionePrefix + partFruitore + "/" + partErogatore+ "/" + partServizio+ "/" + partVersione + rimanenteRegExp + patternAzioneSuffix1;
										pdAzione.setPattern(newPatternAzione);
										tmpPorta.setAzione(pdAzione);
									}

								}
							}
						}// fine controllo azione

						// DelegatedBy
						String nomePortaDelegante = pdAzione != null ? (pdAzione.getNomePortaDelegante() != null ? pdAzione.getNomePortaDelegante() : null) : null;
						if (PortaDelegataAzioneIdentificazione.DELEGATED_BY.equals(identificazione) && nomePortaDelegante!=null ) {
							String nuovoNomeDelegate = null;
							boolean matchDelegate = false;
							if(nomePortaDelegante.equals(check1)) {
								matchDelegate = true;	
								nuovoNomeDelegate = locationPrefix+newLocationSuffix;
							}
							else if(nomePortaDelegante.equals(check1_oldWithoutV)) {
								matchDelegate = true;	
								nuovoNomeDelegate = locationPrefix+newLocationSuffix;
							}
							if(matchDelegate) {
								tmpPorta.getAzione().setNomePortaDelegante(nuovoNomeDelegate);
							}
						}// fine controllo DelegatedBy
					}

					listaPD.add(tmpPorta); // la porta la aggiungo cmq per modificare i dati
				}
			}




			// check PA
			FiltroRicercaPorteApplicative filtroPA = new FiltroRicercaPorteApplicative();
			filtroPA.setTipoSoggetto(asps.getOldIDServizioForUpdate().getSoggettoErogatore().getTipo());
			filtroPA.setNomeSoggetto(asps.getOldIDServizioForUpdate().getSoggettoErogatore().getNome());
			filtroPA.setTipoServizio(asps.getOldIDServizioForUpdate().getTipo());
			filtroPA.setNomeServizio(asps.getOldIDServizioForUpdate().getNome());
			filtroPA.setVersioneServizio(asps.getOldIDServizioForUpdate().getVersione());
			List<IDPortaApplicativa> listIdsPorteApplicative = porteApplicativeCore.getAllIdPorteApplicative(filtroPA);
			if(listIdsPorteApplicative!=null && !listIdsPorteApplicative.isEmpty()) {

				String tmpLocationSuffix = asps.getOldIDServizioForUpdate().getSoggettoErogatore().getTipo() + "_" + asps.getOldIDServizioForUpdate().getSoggettoErogatore().getNome() + 
						"/" + asps.getOldIDServizioForUpdate().getTipo() + "_" + asps.getOldIDServizioForUpdate().getNome();

				String locationSuffix = tmpLocationSuffix+
						"/v" + asps.getOldIDServizioForUpdate().getVersione().intValue();

				// backward compatibility: provare ad eliminare la v, che prima non veniva utilizzata

				String locationSuffix_oldWithoutV = tmpLocationSuffix +
						"/" + asps.getOldIDServizioForUpdate().getVersione().intValue();

				String newLocationSuffix = asps.getOldIDServizioForUpdate().getSoggettoErogatore().getTipo() + "_" + asps.getOldIDServizioForUpdate().getSoggettoErogatore().getNome() + 
						"/" + asps.getTipo() + "_" + asps.getNome() +
						"/v" + asps.getVersione().intValue();

				for (IDPortaApplicativa idPortaApplicativa : listIdsPorteApplicative) {
					PortaApplicativa tmpPorta = porteApplicativeCore.getPortaApplicativa(idPortaApplicativa);	

					// aggiorno dati servizio
					tmpPorta.getServizio().setTipo(asps.getTipo());
					tmpPorta.getServizio().setNome(asps.getNome());
					tmpPorta.getServizio().setVersione(asps.getVersione());

					String check1 = locationSuffix;
					String check2 = "__"+locationSuffix;
					String check1_oldWithoutV = locationSuffix_oldWithoutV;
					String check2_oldWithoutV = "__"+locationSuffix_oldWithoutV;
					String parteRimanente = "";
					String nuovoNome = null;
					boolean match = false;
					if(tmpPorta.getNome().equals(check1)) {
						match = true;	
						nuovoNome = newLocationSuffix;
					}
					else if(tmpPorta.getNome().startsWith(check2)) {
						match = true;	
						parteRimanente = tmpPorta.getNome().substring(check2.length());
						nuovoNome = "__"+newLocationSuffix+parteRimanente;
					}
					else if(tmpPorta.getNome().equals(check1_oldWithoutV)) {
						match = true;	
						nuovoNome = newLocationSuffix;
					}
					else if(tmpPorta.getNome().startsWith(check2_oldWithoutV)) {
						match = true;	
						parteRimanente = tmpPorta.getNome().substring(check2_oldWithoutV.length());
						nuovoNome = "__"+newLocationSuffix+parteRimanente;
					}

					IDPortaApplicativa oldIDPortaApplicativaForUpdate = null;
					if(match) {
						oldIDPortaApplicativaForUpdate = new IDPortaApplicativa();
						oldIDPortaApplicativaForUpdate.setNome(tmpPorta.getNome());
						tmpPorta.setOldIDPortaApplicativaForUpdate(oldIDPortaApplicativaForUpdate);
						tmpPorta.setNome(nuovoNome);

						// modifica della descrizione
						String descrizionePA = tmpPorta.getDescrizione();
						if (descrizionePA != null && !descrizionePA.equals("")) {

							// Caso 1: subscription default
							// Service implementation gw/ENTE:gw/TEST:1
							String match_caso = ":"+asps.getOldIDServizioForUpdate().getTipo()+"/"+asps.getOldIDServizioForUpdate().getNome()+":"+asps.getOldIDServizioForUpdate().getVersione().intValue();
							if(descrizionePA.endsWith(match_caso)) {
								String replace_caso = ":"+asps.getTipo()+"/"+asps.getNome()+":"+asps.getVersione().intValue();
								descrizionePA = descrizionePA.replace(match_caso, replace_caso);
							}

							// Caso 2: altra subscription
							// Internal Implementation 'Specific1' for gw_ENTE/gw_TEST/1
							String tmpMatch_caso2 = "/"+asps.getOldIDServizioForUpdate().getTipo()+"_"+asps.getOldIDServizioForUpdate().getNome()+"/";
							String match_caso2 = tmpMatch_caso2+"v"+asps.getOldIDServizioForUpdate().getVersione().intValue();
							String match_caso2_oldWithoutV = tmpMatch_caso2+asps.getOldIDServizioForUpdate().getVersione().intValue();
							if(descrizionePA.contains(match_caso2)) {
								String replace_caso2 = "/"+asps.getTipo()+"_"+asps.getNome()+"/v"+asps.getVersione().intValue();
								descrizionePA = descrizionePA.replace(match_caso2, replace_caso2);
							}
							else if(descrizionePA.contains(match_caso2_oldWithoutV)) {
								String replace_caso2 = "/"+asps.getTipo()+"_"+asps.getNome()+"/v"+asps.getVersione().intValue();
								descrizionePA = descrizionePA.replace(match_caso2_oldWithoutV, replace_caso2);
							}

							tmpPorta.setDescrizione(descrizionePA);

						}

						// regex del pattern azione
						// .*(erogatore)/(servizio)/([^/|^?]*).*
						PortaApplicativaAzione paAzione = tmpPorta.getAzione();
						PortaApplicativaAzioneIdentificazione identificazione = paAzione != null ? paAzione.getIdentificazione() : null;
						String patterAzione = paAzione != null ? (paAzione.getPattern() != null ? paAzione.getPattern() : "") : "";
						String patternAzionePrefix = ".*/";
						String patternAzioneSuffix1 = "/([^/|^?]*).*";
						// se identificazione urlbased procedo con i controlli
						if (PortaApplicativaAzioneIdentificazione.URL_BASED.equals(identificazione)) {
							if (patterAzione.startsWith(patternAzionePrefix) && patterAzione.endsWith(patternAzioneSuffix1)) {
								// caso1
								int startidx = patternAzionePrefix.length();
								int endidx = patterAzione.lastIndexOf(patternAzioneSuffix1);
								String tmpPat = patterAzione.substring(startidx, endidx);
								// a questo punto ottengo una stringa del tipo
								// (fruitore)/(erogatore)/(servizio)
								// se rispetta la regex allora vuol dire che il
								// pattern azione e' quello di default
								// e devo effettuare i cambiamenti
								String regex = "(.*)\\/(.*)\\/(.*)";
								if (tmpPat.matches(regex)) {
									String[] val = tmpPat.split("/");
									String partErogatore = val[0];
									String partServizio = val[1];
									String partVersione = val[2];
									String rimanenteRegExp = "";
									int lengthParteRimanenteRegExp = (partErogatore+"/"+partServizio+"/"+partVersione).length();
									if(tmpPat.length()>lengthParteRimanenteRegExp){
										rimanenteRegExp = tmpPat.substring(lengthParteRimanenteRegExp);
									}	

									boolean matchURL = false;
									String partOld = "(?:"+asps.getOldIDServizioForUpdate().getTipo()+"_)?"+asps.getOldIDServizioForUpdate().getNome()+"";
									String partNew = "(?:"+asps.getTipo()+"_)?"+asps.getNome()+"";

									// vedo se matcha il fruitore
									if (partServizio.equals(partOld)) {
										partServizio = partNew;
										matchURL = true;
									}

									// vedo se matcha anche erogatore (loopback)
									String versioneOld = "v"+(asps.getOldIDServizioForUpdate().getVersione().intValue()+"");
									String versioneOld_oldWithoutV = (asps.getOldIDServizioForUpdate().getVersione().intValue()+"");
									if (partVersione.equals(versioneOld) || partVersione.equals(versioneOld_oldWithoutV)) {
										partVersione = "v"+asps.getVersione().intValue()+"";
										matchURL = true;
									}

									if(matchURL){
										String newPatternAzione = patternAzionePrefix + partErogatore+ "/" + partServizio+ "/" + partVersione + rimanenteRegExp + patternAzioneSuffix1;
										paAzione.setPattern(newPatternAzione);
										tmpPorta.setAzione(paAzione);
									}

								}
							}
						}// fine controllo azione


						// DelegatedBy
						String nomePortaDelegante = paAzione != null ? (paAzione.getNomePortaDelegante() != null ? paAzione.getNomePortaDelegante() : null) : null;
						if (PortaApplicativaAzioneIdentificazione.DELEGATED_BY.equals(identificazione) && nomePortaDelegante!=null ) {
							String nuovoNomeDelegate = null;
							boolean matchDelegate = false;
							if(nomePortaDelegante.equals(check1)) {
								matchDelegate = true;	
								nuovoNomeDelegate = newLocationSuffix;
							}
							else if(nomePortaDelegante.equals(check1_oldWithoutV)) {
								matchDelegate = true;	
								nuovoNomeDelegate = newLocationSuffix;
							}
							if(matchDelegate) {
								tmpPorta.getAzione().setNomePortaDelegante(nuovoNomeDelegate);
							}
						}// fine controllo DelegatedBy
					}

					listaPA.add(tmpPorta); // la porta la aggiungo cmq per modificare i dati


					// modifica nome Servizi Applicativi che riflette il nome della PA
					if(oldIDPortaApplicativaForUpdate!=null && tmpPorta.sizeServizioApplicativoList()>0) {
						for (PortaApplicativaServizioApplicativo portaApplicativaSA : tmpPorta.getServizioApplicativoList()) {
							if(portaApplicativaSA.getNome().equals(oldIDPortaApplicativaForUpdate.getNome())) {
								// devo aggiornare il nome del SA
								IDServizioApplicativo idSA = new IDServizioApplicativo();
								idSA.setNome(oldIDPortaApplicativaForUpdate.getNome());
								idSA.setIdSoggettoProprietario(new IDSoggetto(tmpPorta.getTipoSoggettoProprietario(), tmpPorta.getNomeSoggettoProprietario()));
								ServizioApplicativo sa = saCore.getServizioApplicativo(idSA);

								IDServizioApplicativo oldIDServizioApplicativoForUpdate = new IDServizioApplicativo();
								oldIDServizioApplicativoForUpdate.setNome(sa.getNome());
								oldIDServizioApplicativoForUpdate.setIdSoggettoProprietario(idSA.getIdSoggettoProprietario());
								sa.setOldIDServizioApplicativoForUpdate(oldIDServizioApplicativoForUpdate);
								sa.setTipoSoggettoProprietario(tmpPorta.getTipoSoggettoProprietario());
								sa.setNomeSoggettoProprietario(tmpPorta.getNomeSoggettoProprietario());

								// __gw_ENTE/gw_TEST/1__Specific2
								// gw_ENTE/gw_TEST/1
								String tmp_check_nomeSA = "/"+asps.getOldIDServizioForUpdate().getTipo()+"_"+asps.getOldIDServizioForUpdate().getNome()+"/";
								String check_nomeSA = tmp_check_nomeSA+"v"+asps.getOldIDServizioForUpdate().getVersione().intValue();
								String check_nomeSA_oldWithoutV = tmp_check_nomeSA+asps.getOldIDServizioForUpdate().getVersione().intValue();
								if(sa.getNome().endsWith(check_nomeSA)) {
									sa.setNome(sa.getNome().replace(check_nomeSA, 
											"/"+asps.getTipo()+"_"+asps.getNome()+"/v"+asps.getVersione().intValue()));
								}
								else if(sa.getNome().startsWith("__") && sa.getNome().contains(check_nomeSA)) {
									sa.setNome(sa.getNome().replace(check_nomeSA, 
											"/"+asps.getTipo()+"_"+asps.getNome()+"/v"+asps.getVersione().intValue()));
								}
								else if(sa.getNome().endsWith(check_nomeSA_oldWithoutV)) {
									sa.setNome(sa.getNome().replace(check_nomeSA_oldWithoutV, 
											"/"+asps.getTipo()+"_"+asps.getNome()+"/v"+asps.getVersione().intValue()));
								}
								else if(sa.getNome().startsWith("__") && sa.getNome().contains(check_nomeSA_oldWithoutV)) {
									sa.setNome(sa.getNome().replace(check_nomeSA_oldWithoutV, 
											"/"+asps.getTipo()+"_"+asps.getNome()+"/v"+asps.getVersione().intValue()));
								}
								listaPA_SA.add(sa);
								break;
							}
						}
					}
					// modifica nome Servizi Applicativi che riflette il nome della PA
				}
			}

		}

		List<Object> oggettiDaAggiornare = new ArrayList<Object>();
		
		// Aggiorno il servizio
		oggettiDaAggiornare.add(asps);

		// Aggiorno le eventuali porte delegate
		for (PortaDelegata portaDelegata : listaPD) {
			oggettiDaAggiornare.add(portaDelegata);
		}

		// aggiorno le eventuali porte applicative
		for (PortaApplicativa portaApplicativa : listaPA) {
			oggettiDaAggiornare.add(portaApplicativa);
		}
		
		// aggiorno gli eventuali servizi applicativi
		for (ServizioApplicativo sa : listaPA_SA) {
			oggettiDaAggiornare.add(sa);
		}

		// Se ho cambiato i dati significativi del servizio devo effettuare anche l'update degli accordi di servizio
		// che includono questi servizi come servizi componenti.
		if (!newUri.equals(oldUri)) {

			IDServizio idServizioOLD =  asps.getOldIDServizioForUpdate();
			String uriOLD = IDServizioFactory.getInstance().getUriFromIDServizio(idServizioOLD);
			List<AccordoServizioParteComune> ass = apcCore.accordiServizio_serviziComponenti(idServizioOLD);
			for(int i=0; i<ass.size(); i++){
				AccordoServizioParteComune accordoServizioComposto = ass.get(i);
				if(accordoServizioComposto.getServizioComposto()!=null){
					for(int j=0;j<accordoServizioComposto.getServizioComposto().sizeServizioComponenteList();j++){
						IDServizio idServizioComponente = IDServizioFactory.getInstance().
								getIDServizioFromValues(accordoServizioComposto.getServizioComposto().getServizioComponente(j).getTipo(), accordoServizioComposto.getServizioComposto().getServizioComponente(j).getNome(), 
										accordoServizioComposto.getServizioComposto().getServizioComponente(j).getTipoSoggetto(),accordoServizioComposto.getServizioComposto().getServizioComponente(j).getNomeSoggetto(), 
										accordoServizioComposto.getServizioComposto().getServizioComponente(j).getVersione());
						String uriServizioComponente = IDServizioFactory.getInstance().getUriFromIDServizio(idServizioComponente);
						if(uriServizioComponente.equals(uriOLD)){
							accordoServizioComposto.getServizioComposto().getServizioComponente(j).setTipoSoggetto(asps.getTipoSoggettoErogatore());
							accordoServizioComposto.getServizioComposto().getServizioComponente(j).setNomeSoggetto(asps.getNomeSoggettoErogatore());
							accordoServizioComposto.getServizioComposto().getServizioComponente(j).setTipo(asps.getTipo());
							accordoServizioComposto.getServizioComposto().getServizioComponente(j).setNome(asps.getNome());
							accordoServizioComposto.getServizioComposto().getServizioComponente(j).setVersione(asps.getVersione());
						}
					}
					oggettiDaAggiornare.add(accordoServizioComposto);
					//System.out.println("As SERVIZIO COMPONENTE ["+IDAccordo.getUriFromAccordo(accordoServizioComposto)+"]");
				}
			}
		}
		
		return oggettiDaAggiornare;
	}

}
