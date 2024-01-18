/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2024 Link.it srl (https://link.it). 
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

import java.util.HashSet;
import java.util.List;
import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.openspcoop2.core.commons.Liste;
import org.openspcoop2.core.config.PortaApplicativa;
import org.openspcoop2.core.config.PortaApplicativaAzione;
import org.openspcoop2.core.config.PortaApplicativaServizio;
import org.openspcoop2.core.config.PortaApplicativaServizioApplicativo;
import org.openspcoop2.core.config.PortaApplicativaSoggettoVirtuale;
import org.openspcoop2.core.config.ServizioApplicativo;
import org.openspcoop2.core.config.Soggetto;
import org.openspcoop2.core.config.driver.DriverConfigurazioneException;
import org.openspcoop2.core.config.driver.db.IDServizioApplicativoDB;
import org.openspcoop2.core.id.IDServizio;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.core.registry.AccordoServizioParteSpecifica;
import org.openspcoop2.core.registry.beans.AccordoServizioParteComuneSintetico;
import org.openspcoop2.core.registry.beans.AzioneSintetica;
import org.openspcoop2.core.registry.beans.OperationSintetica;
import org.openspcoop2.core.registry.beans.PortTypeSintetico;
import org.openspcoop2.core.registry.constants.CostantiRegistroServizi;
import org.openspcoop2.core.registry.driver.DriverRegistroServiziNotFound;
import org.openspcoop2.core.registry.driver.IDAccordoFactory;
import org.openspcoop2.core.registry.driver.IDServizioFactory;
import org.openspcoop2.web.ctrlstat.core.ControlStationCore;
import org.openspcoop2.web.ctrlstat.core.ConsoleSearch;
import org.openspcoop2.web.ctrlstat.servlet.GeneralHelper;
import org.openspcoop2.web.ctrlstat.servlet.apc.AccordiServizioParteComuneCore;
import org.openspcoop2.web.ctrlstat.servlet.aps.AccordiServizioParteSpecificaCore;
import org.openspcoop2.web.ctrlstat.servlet.sa.ServiziApplicativiCore;
import org.openspcoop2.web.ctrlstat.servlet.sa.ServiziApplicativiHelper;
import org.openspcoop2.web.ctrlstat.servlet.soggetti.SoggettiCore;
import org.openspcoop2.web.lib.mvc.Costanti;
import org.openspcoop2.web.lib.mvc.DataElement;
import org.openspcoop2.web.lib.mvc.ForwardParams;
import org.openspcoop2.web.lib.mvc.GeneralData;
import org.openspcoop2.web.lib.mvc.PageData;
import org.openspcoop2.web.lib.mvc.Parameter;
import org.openspcoop2.web.lib.mvc.ServletUtils;
import org.openspcoop2.web.lib.mvc.TipoOperazione;

/**
 * porteAppServizioApplicativoAdd
 * 
 * @author Andrea Poli (apoli@link.it)
 * @author Stefano Corallo (corallo@link.it)
 * @author Sandra Giangrandi (sandra@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 * 
 */
public final class PorteApplicativeServizioApplicativoAdd extends Action {

	@Override
	public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {

		HttpSession session = request.getSession(true);

		// Inizializzo PageData
		PageData pd = new PageData();

		GeneralHelper generalHelper = new GeneralHelper(session);

		// Inizializzo GeneralData
		GeneralData gd = generalHelper.initGeneralData(request);

		try {
			PorteApplicativeHelper porteApplicativeHelper = new PorteApplicativeHelper(request, pd, session);
			// prelevo il flag che mi dice da quale pagina ho acceduto la sezione delle porte applicative
			Integer parentPA = ServletUtils.getIntegerAttributeFromSession(PorteApplicativeCostanti.ATTRIBUTO_PORTE_APPLICATIVE_PARENT, session, request);
			if(parentPA == null) parentPA = PorteApplicativeCostanti.ATTRIBUTO_PORTE_APPLICATIVE_PARENT_NONE;
			
			String idPorta = porteApplicativeHelper.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID);
			int idInt = Integer.parseInt(idPorta);
			String idsogg = porteApplicativeHelper.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_SOGGETTO);
			int soggInt = Integer.parseInt(idsogg);
			String servizioApplicativo = porteApplicativeHelper.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_SERVIZIO_APPLICATIVO);
			String idAsps = porteApplicativeHelper.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_ASPS);
			if(idAsps == null) 
				idAsps = "";
			PorteApplicativeCore porteApplicativeCore = new PorteApplicativeCore();
			SoggettiCore soggettiCore = new SoggettiCore(porteApplicativeCore);
			ServiziApplicativiCore saCore = new ServiziApplicativiCore(porteApplicativeCore);
			AccordiServizioParteComuneCore apcCore = new AccordiServizioParteComuneCore(porteApplicativeCore);
			AccordiServizioParteSpecificaCore apsCore = new AccordiServizioParteSpecificaCore(porteApplicativeCore);

			// Preparo il menu
			porteApplicativeHelper.makeMenu();

			// Prendo nome, tipo e pdd del soggetto
			String tipoSoggettoProprietario = null;
			String nomeSoggettoProprietario = null;
			if(porteApplicativeCore.isRegistroServiziLocale()){
				org.openspcoop2.core.registry.Soggetto soggetto = soggettiCore.getSoggettoRegistro(soggInt);
				tipoSoggettoProprietario = soggetto.getTipo();
				nomeSoggettoProprietario = soggetto.getNome();
			}
			else{
				org.openspcoop2.core.config.Soggetto soggetto = soggettiCore.getSoggetto(soggInt);
				tipoSoggettoProprietario = soggetto.getTipo();
				nomeSoggettoProprietario = soggetto.getNome();
			}

			// Prendo nome della porta applicativa
			PortaApplicativa pa = porteApplicativeCore.getPortaApplicativa(idInt);
			if(pa==null) {
				throw new Exception("PortaApplicativa con id '"+idInt+"' non trovata");
			}
			String nomePorta = pa.getNome();
			PortaApplicativaServizio pas = pa.getServizio();
			int idServizio = -1;
			String tipoServizio = null;
			String nomeServizio = null;
			Integer versioneServizio = null;
			if (pas != null) {
				idServizio = pas.getId().intValue();
				tipoServizio = pas.getTipo();
				nomeServizio = pas.getNome();
				versioneServizio = pas.getVersione();
			}
			PortaApplicativaSoggettoVirtuale pasv = pa.getSoggettoVirtuale();
			long idSoggettoVirtuale = -1;
			String tipoSoggettoVirtuale = null;
			String nomeSoggettoVirtuale = null;
			if (pasv != null) {
				idSoggettoVirtuale = pasv.getId();
				tipoSoggettoVirtuale = pasv.getTipo();
				nomeSoggettoVirtuale = pasv.getNome();
			}
			PortaApplicativaAzione paa = pa.getAzione();
			String nomeAzione = "";
			if (paa != null)
				nomeAzione = paa.getNome();
			
			boolean behaviourDefined = false;
			if(pa!=null && pa.getBehaviour()!=null && pa.getBehaviour().getNome()!=null && !"".equals(pa.getBehaviour().getNome())) {
				behaviourDefined = true;
			}

			// Recupero eventuale idServizio mancante
			if(porteApplicativeCore.isRegistroServiziLocale()){
				if (idServizio <= 0) {
					long idSoggettoServizio = -1;
					if (idSoggettoVirtuale > 0) {
						idSoggettoServizio = idSoggettoVirtuale;
					} else if (tipoSoggettoVirtuale != null && (!("".equals(tipoSoggettoVirtuale))) && nomeSoggettoVirtuale != null && (!("".equals(nomeSoggettoVirtuale)))) {
						idSoggettoServizio = soggettiCore.getIdSoggetto(nomeSoggettoVirtuale, tipoSoggettoVirtuale);
					} else {
						idSoggettoServizio = soggInt;
					}
					Soggetto soggServ = soggettiCore.getSoggetto(idSoggettoServizio);
					idServizio = (int) apsCore.getIdAccordoServizioParteSpecifica(nomeServizio, tipoServizio, versioneServizio, soggServ.getNome(), soggServ.getTipo()); 
				}
			}
			
			List<Parameter> lstParam = porteApplicativeHelper.getTitoloPA(parentPA, idsogg, idAsps);
			
			String labelPerPorta = null;
			if(parentPA!=null && (parentPA.intValue() == PorteApplicativeCostanti.ATTRIBUTO_PORTE_APPLICATIVE_PARENT_CONFIGURAZIONE)) {
				labelPerPorta = porteApplicativeCore.getLabelRegolaMappingErogazionePortaApplicativa(
						PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_SERVIZIO_APPLICATIVO_CONFIG_DI,
						PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_SERVIZIO_APPLICATIVO_CONFIG,
						pa);
			}
			else {
				labelPerPorta = PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_SERVIZIO_APPLICATIVO_CONFIG_DI+nomePorta;
			}
			
			lstParam.add(new Parameter(labelPerPorta,
					PorteApplicativeCostanti.SERVLET_NAME_PORTE_APPLICATIVE_SERVIZIO_APPLICATIVO_LIST,
					new Parameter( PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID, idPorta),
					new Parameter( PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_SOGGETTO, idsogg)
					));
			lstParam.add(ServletUtils.getParameterAggiungi());
			
			// Se servizioApplicativohid = null, devo visualizzare la pagina per
			// l'inserimento dati
			if (porteApplicativeHelper.isEditModeInProgress()) {
				
				// setto la barra del titolo
				ServletUtils.setPageDataTitle(pd, lstParam);

				// preparo i campi
				List<DataElement> dati = new ArrayList<>();
				dati.add(ServletUtils.getDataElementForEditModeFinished());

				// Controllo vincolo profilo_collaborazione
				boolean isProfiloOneWay = true;

				AccordoServizioParteSpecifica servSp = null;
				if(porteApplicativeCore.isRegistroServiziLocale()){
					servSp = apsCore.getAccordoServizioParteSpecifica(idServizio);
					if(servSp==null) {
						throw new Exception("AccordoServizioParteSpecifica con id '"+idServizio+"' non trovato");
					}
				}else{
					IDSoggetto soggettoErogatoreServizio = null;
					if(pa.getSoggettoVirtuale()!=null){
						soggettoErogatoreServizio = new IDSoggetto(pa.getSoggettoVirtuale().getTipo(), pa.getSoggettoVirtuale().getNome());
					}else{
						soggettoErogatoreServizio = new IDSoggetto(tipoSoggettoProprietario,nomeSoggettoProprietario);
					}
					IDServizio idServ = IDServizioFactory.getInstance().getIDServizioFromValues(tipoServizio, nomeServizio, 
							soggettoErogatoreServizio, 
							versioneServizio); 
					try{
						servSp = apsCore.getServizio(idServ);
					}catch(DriverRegistroServiziNotFound dNot){
						// ignore
					}
					if(servSp==null) {
						throw new Exception("AccordoServizioParteSpecifica con id '"+idServ+"' non trovato");
					}
				}
				AccordoServizioParteComuneSintetico as = null;
				if(porteApplicativeCore.isRegistroServiziLocale()){
					int idAcc = servSp.getIdAccordo().intValue();
					as = apcCore.getAccordoServizioSintetico(idAcc);
				}
				else{
					as = apcCore.getAccordoServizioSintetico(IDAccordoFactory.getInstance().getIDAccordoFromUri(servSp.getAccordoServizioParteComune()));
				}
				String nomeAccordo = as.getNome();
				// recupero profilo collaborazione accordo
				String profiloCollaborazioneAccordo = as.getProfiloCollaborazione().toString();
				if (profiloCollaborazioneAccordo.equals(CostantiRegistroServizi.ONEWAY.getValue())) {
					profiloCollaborazioneAccordo = "oneway";
				} else if (profiloCollaborazioneAccordo.equals(CostantiRegistroServizi.SINCRONO.getValue())) {
					profiloCollaborazioneAccordo = "sincrono";
				} else if (profiloCollaborazioneAccordo.equals(CostantiRegistroServizi.ASINCRONO_SIMMETRICO.getValue())) {
					profiloCollaborazioneAccordo = "asincronoSimmetrico";
				} else if (profiloCollaborazioneAccordo.equals(CostantiRegistroServizi.ASINCRONO_ASIMMETRICO.getValue())) {
					profiloCollaborazioneAccordo = "asincronoAsimmetrico";
				}

				// recupero profilo collaborazione azione se l'azione e'
				// specificata e il profilo azione e' ridefinito
				String profiloCollaborazioneAzione = "";
				if (nomeAzione != null && !nomeAzione.equals("")) {
					if(servSp.getPortType()!=null){
						for (PortTypeSintetico pt : as.getPortType()) {
							if(pt.getNome().equals(servSp.getPortType())){
								for (OperationSintetica op : pt.getAzione()) {
									if(op.getNome().equals(nomeAzione)){
										if(CostantiRegistroServizi.PROFILO_AZIONE_RIDEFINITO.equals(op.getProfAzione())){
											if(op.getProfiloCollaborazione()!=null)
												profiloCollaborazioneAzione = op.getProfiloCollaborazione().toString();
										}
										else{
											if(CostantiRegistroServizi.PROFILO_AZIONE_RIDEFINITO.equals(pt.getProfiloPT())){
												if(pt.getProfiloCollaborazione()!=null)
													profiloCollaborazioneAzione = pt.getProfiloCollaborazione().toString();
											}
										}
									}
								}
							}
						}
					}
					else{
						for (int i = 0; i < as.getAzione().size(); i++) {
							AzioneSintetica tmpAz = as.getAzione().get(i);
							if (tmpAz.getProfAzione().equals(CostantiRegistroServizi.PROFILO_AZIONE_RIDEFINITO)) {
								if(tmpAz.getProfiloCollaborazione()!=null)
									profiloCollaborazioneAzione = tmpAz.getProfiloCollaborazione().toString();
								break;
							}
						}
					}
				}

				// Controllo se nomeAzione e' specificato allora e' possibile
				// che il profilo azione sia ridefinito
				// quindi devo controllare se e' diverso da oneway
				if (nomeAzione != null && !nomeAzione.equals("")) {
					// se e' diverso da oneway posso avere solo un azione
					if ( profiloCollaborazioneAzione != null && !profiloCollaborazioneAzione.equals("") ){
						if (!profiloCollaborazioneAzione.equals(CostantiRegistroServizi.ONEWAY.getValue())) {
							isProfiloOneWay = false;
						}
					}
					else{
						if (!profiloCollaborazioneAccordo.equals(CostantiRegistroServizi.ONEWAY.getValue())) {
							isProfiloOneWay = false;
						}	
					}
				} else {
					// non ho azione (o azione con profilo non ridefinito)
					// allora considero il profilo dell'accordo
					if (!profiloCollaborazioneAccordo.equals(CostantiRegistroServizi.ONEWAY.getValue())) {
						isProfiloOneWay = false;
					}
				}

				// recupero il numero di servizi applicativi gia associati alla
				// porta applicativa
				// se il profilo non e' oneway allora ne posso avere solo uno
				// associato
				int numSAassociati = 0;
				if (!isProfiloOneWay)
					numSAassociati = pa.sizeServizioApplicativoList();

				if (!isProfiloOneWay && numSAassociati > 0 && !behaviourDefined) {
					if ((nomeAzione != null && !nomeAzione.equals("")) && (profiloCollaborazioneAzione != null && !profiloCollaborazioneAzione.equals("")))
						pd.setMessage("&Egrave; possibile associare un solo Servizio Applicativo alla Porta Applicativa [" + nomePorta + "] in quanto l'Azione [" + nomeAzione + "] dell'Accordo di Servizio [" + nomeAccordo + "] e' stata definito con profilo [" + profiloCollaborazioneAzione + "]");
					else
						pd.setMessage("&Egrave; possibile associare un solo Servizio Applicativo alla Porta Applicativa [" + nomePorta + "] in quanto l'Accordo di Servizio [" + nomeAccordo + "] e' stato definito con profilo [" + profiloCollaborazioneAccordo + "]");

					pd.setInserisciBottoni(false);
					pd.setMode(Costanti.DATA_ELEMENT_EDIT_MODE_DISABLE_NAME);

					ServletUtils.setGeneralAndPageDataIntoSession(request, session, gd, pd);

					return ServletUtils.getStrutsForwardEditModeCheckError(mapping, PorteApplicativeCostanti.OBJECT_NAME_PORTE_APPLICATIVE_SERVIZIO_APPLICATIVO, 
							ForwardParams.ADD());
				}

				String [] servizioApplicativoList = PorteApplicativeServizioApplicativoAdd.loadSAErogatori(pa, saCore, soggInt, true);
				
				dati = porteApplicativeHelper.addPorteServizioApplicativoToDati(TipoOperazione.ADD,dati, "", servizioApplicativoList, pa.sizeServizioApplicativoList(),true,true, false);

				dati = porteApplicativeHelper.addHiddenFieldsToDati(TipoOperazione.ADD, idPorta, idsogg, idPorta, idAsps,dati);

				pd.setDati(dati);

				ServletUtils.setGeneralAndPageDataIntoSession(request, session, gd, pd);

				return ServletUtils.getStrutsForwardEditModeInProgress(mapping, PorteApplicativeCostanti.OBJECT_NAME_PORTE_APPLICATIVE_SERVIZIO_APPLICATIVO,
						ForwardParams.ADD());
			}

			// Controlli sui campi immessi
			boolean isOk = porteApplicativeHelper.porteAppServizioApplicativoCheckData(TipoOperazione.ADD);
			if (!isOk) {
				// setto la barra del titolo
				ServletUtils.setPageDataTitle(pd, lstParam);

				// preparo i campi
				List<DataElement> dati = new ArrayList<>();

				dati.add(ServletUtils.getDataElementForEditModeFinished());

				// Controllo vincolo profilo_collaborazione
				@SuppressWarnings("unused")
				boolean isProfiloOneWay = true;

				AccordoServizioParteSpecifica servSp = null;
				if(porteApplicativeCore.isRegistroServiziLocale()){
					servSp = apsCore.getAccordoServizioParteSpecifica(idServizio);
					if(servSp==null) {
						throw new Exception("AccordoServizioParteSpecifica con id '"+idServizio+"' non trovato");
					}
				}else{
					IDSoggetto soggettoErogatoreServizio = null;
					if(pa.getSoggettoVirtuale()!=null){
						soggettoErogatoreServizio = new IDSoggetto(pa.getSoggettoVirtuale().getTipo(), pa.getSoggettoVirtuale().getNome());
					}else{
						soggettoErogatoreServizio = new IDSoggetto(tipoSoggettoProprietario,nomeSoggettoProprietario);
					}
					IDServizio idServ = IDServizioFactory.getInstance().getIDServizioFromValues(tipoServizio, nomeServizio, 
							soggettoErogatoreServizio, 
							versioneServizio); 
					try{
						servSp = apsCore.getServizio(idServ);
					}catch(DriverRegistroServiziNotFound dNot){
						// ignore
					}
					if(servSp==null) {
						throw new Exception("AccordoServizioParteSpecifica con id '"+idServ+"' non trovato");
					}
				}
				AccordoServizioParteComuneSintetico as = null;
				if(porteApplicativeCore.isRegistroServiziLocale()){
					int idAcc = servSp.getIdAccordo().intValue();
					as = apcCore.getAccordoServizioSintetico(idAcc);
				}
				else{
					as = apcCore.getAccordoServizioSintetico(IDAccordoFactory.getInstance().getIDAccordoFromUri(servSp.getAccordoServizioParteComune()));
				}

				// recupero profilo collaborazione accordo
				String profiloCollaborazioneAccordo = as.getProfiloCollaborazione().toString();
				if (profiloCollaborazioneAccordo.equals(CostantiRegistroServizi.ONEWAY.getValue())) {
					profiloCollaborazioneAccordo = "oneway";
				} else if (profiloCollaborazioneAccordo.equals(CostantiRegistroServizi.SINCRONO.getValue())) {
					profiloCollaborazioneAccordo = "sincrono";
				} else if (profiloCollaborazioneAccordo.equals(CostantiRegistroServizi.ASINCRONO_SIMMETRICO.getValue())) {
					profiloCollaborazioneAccordo = "asincronoSimmetrico";
				} else if (profiloCollaborazioneAccordo.equals(CostantiRegistroServizi.ASINCRONO_ASIMMETRICO.getValue())) {
					profiloCollaborazioneAccordo = "asincronoAsimmetrico";
				}

				// recupero profilo collaborazione azione se l'azione e'
				// specificata e il profilo azione e' ridefinito
				String profiloCollaborazioneAzione = "";
				if (nomeAzione != null && !nomeAzione.equals("")) {
					for (int i = 0; i < as.getAzione().size(); i++) {
						AzioneSintetica tmpAz = as.getAzione().get(i);
						if (tmpAz.getProfAzione().equals(CostantiRegistroServizi.PROFILO_AZIONE_RIDEFINITO)) {
							if(tmpAz.getProfiloCollaborazione()!=null){
								profiloCollaborazioneAzione = tmpAz.getProfiloCollaborazione().toString();
							}
							break;
						}
					}
				}

				// Controllo se nomeAzione e' specificato allora e' possibile
				// che il profilo azione sia ridefinito
				// quindi devo controllare se e' diverso da oneway
				if (nomeAzione != null && !nomeAzione.equals("")) {
					// se e' diverso da oneway posso avere solo un azione
					if (profiloCollaborazioneAzione != null && !profiloCollaborazioneAzione.equals("") && !profiloCollaborazioneAzione.equals(CostantiRegistroServizi.ONEWAY.getValue())) {
						isProfiloOneWay = false;
					}
				} else {
					// non ho azione (o azione con profilo non ridefinito)
					// allora considero il profilo dell'accordo
					if (!profiloCollaborazioneAccordo.equals(CostantiRegistroServizi.ONEWAY.getValue())) {
						isProfiloOneWay = false;
					}
				}

				/** recupero il numero di servizi applicativi gia associati alla
				// porta applicativa
				// se il profilo non e' oneway allora ne posso avere solo uno
				// associato
				//				int numSAassociati = 0;
				//				if (!isProfiloOneWay)
				//					numSAassociati = pa.sizeServizioApplicativoList();

				//				if (!isProfiloOneWay && numSAassociati > 0) {
				//					if ((nomeAzione != null || !"".equals(nomeAzione))) {
				//						if (profiloCollaborazioneAzione != null && !profiloCollaborazioneAzione.equals(""))
				//							profiloCollaborazioneAzione = profiloCollaborazioneAccordo;
				//
				//						pd.setMessage("&Egrave; possibile associare un solo Servizio Applicativo alla Porta Applicativa [" + nomePorta + "] in quanto l'Azione [" + nomeAzione + "] dell'Accordo di Servizio [" + nomeAccordo + "]e' stata definito con profilo [" + profiloCollaborazioneAzione + "]");
				//					} else {
				//						pd.setMessage("&Egrave; possibile associare un solo Servizio Applicativo alla Porta Applicativa [" + nomePorta + "] in quanto l'Accordo di Servizio e' stato definito con profilo [" + profiloCollaborazioneAccordo + "]");
				//					}
				//					
				//					ServletUtils.setGeneralAndPageDataIntoSession(request, session, gd, pd);
				//					// Forward control to the specified success URI
				//					return ServletUtils.getStrutsForwardEditModeFinished(mapping, PorteApplicativeCostanti.OBJECT_NAME_PORTE_APPLICATIVE_SERVIZIO_APPLICATIVO, 
				//							ForwardParams.ADD());
				//				}*/

				String[] servizioApplicativoList = PorteApplicativeServizioApplicativoAdd.loadSAErogatori(pa, saCore, soggInt, true);

				dati = porteApplicativeHelper.addPorteServizioApplicativoToDati(TipoOperazione.ADD, dati, servizioApplicativo, servizioApplicativoList, pa.sizeServizioApplicativoList(),true,true, false);

				dati = porteApplicativeHelper.addHiddenFieldsToDati(TipoOperazione.ADD, idPorta, idsogg, idPorta, idAsps, dati);

				pd.setDati(dati);

				ServletUtils.setGeneralAndPageDataIntoSession(request, session, gd, pd);

				return ServletUtils.getStrutsForwardEditModeCheckError(mapping, PorteApplicativeCostanti.OBJECT_NAME_PORTE_APPLICATIVE_SERVIZIO_APPLICATIVO, 
						ForwardParams.ADD());
			}

			// Inserisco il servizioApplicativo nel db
			PortaApplicativaServizioApplicativo sa = new PortaApplicativaServizioApplicativo();
			sa.setNome(servizioApplicativo);
			pa.addServizioApplicativo(sa);

			String userLogin = ServletUtils.getUserLoginFromSession(session);

			porteApplicativeCore.performUpdateOperation(userLogin, porteApplicativeHelper.smista(), pa);

			// Preparo la lista
			ConsoleSearch ricerca = (ConsoleSearch) ServletUtils.getSearchObjectFromSession(request, session, ConsoleSearch.class);

			int idLista = Liste.PORTE_APPLICATIVE_SERVIZIO_APPLICATIVO;

			ricerca = porteApplicativeHelper.checkSearchParameters(idLista, ricerca);

			List<ServizioApplicativo> lista = porteApplicativeCore.porteAppServizioApplicativoList(Integer.parseInt(idPorta), ricerca);

			porteApplicativeHelper.preparePorteAppServizioApplicativoList(nomePorta, ricerca, lista);

			ServletUtils.setGeneralAndPageDataIntoSession(request, session, gd, pd);
			// Forward control to the specified success URI
			return ServletUtils.getStrutsForwardEditModeFinished(mapping, PorteApplicativeCostanti.OBJECT_NAME_PORTE_APPLICATIVE_SERVIZIO_APPLICATIVO, 
					ForwardParams.ADD());
		} catch (Exception e) {
			return ServletUtils.getStrutsForwardError(ControlStationCore.getLog(), e, pd, request, session, gd, mapping, 
					PorteApplicativeCostanti.OBJECT_NAME_PORTE_APPLICATIVE_SERVIZIO_APPLICATIVO,
					ForwardParams.ADD());
		} 
	}
	
	
	public static String[] loadSAErogatori(PortaApplicativa pa, ServiziApplicativiCore saCore, long soggInt, boolean addSAEsistenti) throws DriverConfigurazioneException{
		// recupero nome dei servizi applicativi gia associati alla
		// porta applicativa
		HashSet<String> saEsistenti = new HashSet<>();
		if(addSAEsistenti){
			for (int i = 0; i < pa.sizeServizioApplicativoList(); i++) {
				PortaApplicativaServizioApplicativo tmpSA = pa.getServizioApplicativo(i);
				saEsistenti.add(tmpSA.getNome());
			}
		}

		// I servizi applicativi da visualizzare sono quelli che hanno
		// -Integration Manager (getMessage abilitato)
		// -connettore != disabilitato
		List<IDServizioApplicativoDB> listaIdSA = saCore.getIdServiziApplicativiWithIdErogatore(soggInt, true, true);
		return ServiziApplicativiHelper.toArray(listaIdSA);

	}
}
