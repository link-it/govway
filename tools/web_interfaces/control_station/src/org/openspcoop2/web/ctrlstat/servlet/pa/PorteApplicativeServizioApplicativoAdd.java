/*
 * GovWay - A customizable API Gateway 
 * http://www.govway.org
 *
 * from the Link.it OpenSPCoop project codebase
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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.openspcoop2.core.commons.Liste;
import org.openspcoop2.core.config.Connettore;
import org.openspcoop2.core.config.InvocazioneServizio;
import org.openspcoop2.core.config.PortaApplicativa;
import org.openspcoop2.core.config.PortaApplicativaAzione;
import org.openspcoop2.core.config.PortaApplicativaServizio;
import org.openspcoop2.core.config.PortaApplicativaServizioApplicativo;
import org.openspcoop2.core.config.PortaApplicativaSoggettoVirtuale;
import org.openspcoop2.core.config.ServizioApplicativo;
import org.openspcoop2.core.config.Soggetto;
import org.openspcoop2.core.config.constants.CostantiConfigurazione;
import org.openspcoop2.core.config.constants.StatoFunzionalita;
import org.openspcoop2.core.config.driver.DriverConfigurazioneException;
import org.openspcoop2.core.config.driver.DriverConfigurazioneNotFound;
import org.openspcoop2.core.constants.TipiConnettore;
import org.openspcoop2.core.id.IDServizio;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.core.registry.AccordoServizioParteComune;
import org.openspcoop2.core.registry.AccordoServizioParteSpecifica;
import org.openspcoop2.core.registry.Azione;
import org.openspcoop2.core.registry.Operation;
import org.openspcoop2.core.registry.PortType;
import org.openspcoop2.core.registry.constants.CostantiRegistroServizi;
import org.openspcoop2.core.registry.driver.DriverRegistroServiziNotFound;
import org.openspcoop2.core.registry.driver.IDAccordoFactory;
import org.openspcoop2.core.registry.driver.IDServizioFactory;
import org.openspcoop2.web.ctrlstat.core.ControlStationCore;
import org.openspcoop2.web.ctrlstat.core.Search;
import org.openspcoop2.web.ctrlstat.servlet.GeneralHelper;
import org.openspcoop2.web.ctrlstat.servlet.apc.AccordiServizioParteComuneCore;
import org.openspcoop2.web.ctrlstat.servlet.aps.AccordiServizioParteSpecificaCore;
import org.openspcoop2.web.ctrlstat.servlet.sa.ServiziApplicativiCore;
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

		Integer parentPA = ServletUtils.getIntegerAttributeFromSession(PorteApplicativeCostanti.ATTRIBUTO_PORTE_APPLICATIVE_PARENT, session);
		if(parentPA == null) parentPA = PorteApplicativeCostanti.ATTRIBUTO_PORTE_APPLICATIVE_PARENT_NONE;

		try {
			PorteApplicativeHelper porteApplicativeHelper = new PorteApplicativeHelper(request, pd, session);
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
			// String pdd = soggetto.getServer();

			// Prendo nome della porta applicativa
			PortaApplicativa pa = porteApplicativeCore.getPortaApplicativa(idInt);
			String nomePorta = pa.getNome();
			// long idPorta = pa.getId();
			PortaApplicativaServizio pas = pa.getServizio();
			int idServizio = -1;
			String tipo_servizio = null;
			String nome_servizio = null;
			Integer versione_servizio = null;
			if (pas != null) {
				idServizio = pas.getId().intValue();
				tipo_servizio = pas.getTipo();
				nome_servizio = pas.getNome();
				versione_servizio = pas.getVersione();
			}
			PortaApplicativaSoggettoVirtuale pasv = pa.getSoggettoVirtuale();
			long id_soggetto_virtuale = -1;
			String tipo_soggetto_virtuale = null;
			String nome_soggetto_virtuale = null;
			if (pasv != null) {
				id_soggetto_virtuale = pasv.getId();
				tipo_soggetto_virtuale = pasv.getTipo();
				nome_soggetto_virtuale = pasv.getNome();
			}
			PortaApplicativaAzione paa = pa.getAzione();
			String nomeAzione = "";
			if (paa != null)
				nomeAzione = paa.getNome();
			
			boolean behaviourDefined = false;
			if(pa!=null && pa.getBehaviour()!=null && !"".equals(pa.getBehaviour())) {
				behaviourDefined = true;
			}

			// Recupero eventuale idServizio mancante
			if(porteApplicativeCore.isRegistroServiziLocale()){
				if (idServizio <= 0) {
					long idSoggettoServizio = -1;
					if (id_soggetto_virtuale > 0) {
						idSoggettoServizio = id_soggetto_virtuale;
					} else if (tipo_soggetto_virtuale != null && (!("".equals(tipo_soggetto_virtuale))) && nome_soggetto_virtuale != null && (!("".equals(nome_soggetto_virtuale)))) {
						idSoggettoServizio = soggettiCore.getIdSoggetto(nome_soggetto_virtuale, tipo_soggetto_virtuale);
					} else {
						idSoggettoServizio = soggInt;
					}
					Soggetto soggServ = soggettiCore.getSoggetto(idSoggettoServizio);
					idServizio = (int) apsCore.getIdAccordoServizioParteSpecifica(nome_servizio, tipo_servizio, versione_servizio, soggServ.getNome(), soggServ.getTipo()); 
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
				Vector<DataElement> dati = new Vector<DataElement>();
				dati.addElement(ServletUtils.getDataElementForEditModeFinished());

				// Controllo vincolo profilo_collaborazione
				boolean isProfiloOneWay = true;

				// select profilo_collaborazione from servizi s, accordi a where
				// s.id_accordo=a.id and s.id=1;
				AccordoServizioParteSpecifica servSp = null;
				if(porteApplicativeCore.isRegistroServiziLocale()){
					servSp = apsCore.getAccordoServizioParteSpecifica(idServizio);
				}else{
					IDSoggetto soggettoErogatoreServizio = null;
					if(pa.getSoggettoVirtuale()!=null){
						soggettoErogatoreServizio = new IDSoggetto(pa.getSoggettoVirtuale().getTipo(), pa.getSoggettoVirtuale().getNome());
					}else{
						soggettoErogatoreServizio = new IDSoggetto(tipoSoggettoProprietario,nomeSoggettoProprietario);
					}
					IDServizio idServ = IDServizioFactory.getInstance().getIDServizioFromValues(tipo_servizio, nome_servizio, 
							soggettoErogatoreServizio, 
							versione_servizio); 
					try{
						servSp = apsCore.getServizio(idServ);
					}catch(DriverRegistroServiziNotFound dNot){
					}
				}
				AccordoServizioParteComune as = null;
				if(porteApplicativeCore.isRegistroServiziLocale()){
					int idAcc = servSp.getIdAccordo().intValue();
					as = apcCore.getAccordoServizio(idAcc);
				}
				else{
					as = apcCore.getAccordoServizio(IDAccordoFactory.getInstance().getIDAccordoFromUri(servSp.getAccordoServizioParteComune()));
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
						for (PortType pt : as.getPortTypeList()) {
							if(pt.getNome().equals(servSp.getPortType())){
								for (Operation op : pt.getAzioneList()) {
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
						for (int i = 0; i < as.sizeAzioneList(); i++) {
							Azione tmpAz = as.getAzione(i);
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
						pd.setMessage("E' possibile associare un solo Servizio Applicativo alla Porta Applicativa [" + nomePorta + "] in quanto l'Azione [" + nomeAzione + "] dell'Accordo di Servizio [" + nomeAccordo + "] e' stata definito con profilo [" + profiloCollaborazioneAzione + "]");
					else
						pd.setMessage("E' possibile associare un solo Servizio Applicativo alla Porta Applicativa [" + nomePorta + "] in quanto l'Accordo di Servizio [" + nomeAccordo + "] e' stato definito con profilo [" + profiloCollaborazioneAccordo + "]");

					pd.setInserisciBottoni(false);
					pd.setMode(Costanti.DATA_ELEMENT_EDIT_MODE_DISABLE_NAME);

					ServletUtils.setGeneralAndPageDataIntoSession(session, gd, pd);

					return ServletUtils.getStrutsForwardEditModeCheckError(mapping, PorteApplicativeCostanti.OBJECT_NAME_PORTE_APPLICATIVE_SERVIZIO_APPLICATIVO, 
							ForwardParams.ADD());
				}

				String [] servizioApplicativoList = PorteApplicativeServizioApplicativoAdd.loadSAErogatori(pa, saCore, soggInt, true);
				
				dati = porteApplicativeHelper.addPorteServizioApplicativoToDati(TipoOperazione.ADD,dati, "", servizioApplicativoList, pa.sizeServizioApplicativoList(),true);

				dati = porteApplicativeHelper.addHiddenFieldsToDati(TipoOperazione.ADD, idPorta, idsogg, idPorta, idAsps,dati);

				pd.setDati(dati);

				ServletUtils.setGeneralAndPageDataIntoSession(session, gd, pd);

				return ServletUtils.getStrutsForwardEditModeInProgress(mapping, PorteApplicativeCostanti.OBJECT_NAME_PORTE_APPLICATIVE_SERVIZIO_APPLICATIVO,
						ForwardParams.ADD());
			}

			// Controlli sui campi immessi
			boolean isOk = porteApplicativeHelper.porteAppServizioApplicativoCheckData(TipoOperazione.ADD);
			if (!isOk) {
				// setto la barra del titolo
				ServletUtils.setPageDataTitle(pd, lstParam);

				// preparo i campi
				Vector<DataElement> dati = new Vector<DataElement>();

				dati.addElement(ServletUtils.getDataElementForEditModeFinished());

				// Controllo vincolo profilo_collaborazione
				@SuppressWarnings("unused")
				boolean isProfiloOneWay = true;

				// select profilo_collaborazione from servizi s, accordi a where
				// s.id_accordo=a.id and s.id=1;
				AccordoServizioParteSpecifica servSp = null;
				if(porteApplicativeCore.isRegistroServiziLocale()){
					servSp = apsCore.getAccordoServizioParteSpecifica(idServizio);
				}else{
					IDSoggetto soggettoErogatoreServizio = null;
					if(pa.getSoggettoVirtuale()!=null){
						soggettoErogatoreServizio = new IDSoggetto(pa.getSoggettoVirtuale().getTipo(), pa.getSoggettoVirtuale().getNome());
					}else{
						soggettoErogatoreServizio = new IDSoggetto(tipoSoggettoProprietario,nomeSoggettoProprietario);
					}
					IDServizio idServ = IDServizioFactory.getInstance().getIDServizioFromValues(tipo_servizio, nome_servizio, 
							soggettoErogatoreServizio, 
							versione_servizio); 
					try{
						servSp = apsCore.getServizio(idServ);
					}catch(DriverRegistroServiziNotFound dNot){
					}
				}
				AccordoServizioParteComune as = null;
				if(porteApplicativeCore.isRegistroServiziLocale()){
					int idAcc = servSp.getIdAccordo().intValue();
					as = apcCore.getAccordoServizio(idAcc);
				}
				else{
					as = apcCore.getAccordoServizio(IDAccordoFactory.getInstance().getIDAccordoFromUri(servSp.getAccordoServizioParteComune()));
				}
				//				String nomeAccordo = as.getNome();
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
					for (int i = 0; i < as.sizeAzioneList(); i++) {
						Azione tmpAz = as.getAzione(i);
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

				// recupero il numero di servizi applicativi gia associati alla
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
				//						pd.setMessage("E' possibile associare un solo Servizio Applicativo alla Porta Applicativa [" + nomePorta + "] in quanto l'Azione [" + nomeAzione + "] dell'Accordo di Servizio [" + nomeAccordo + "]e' stata definito con profilo [" + profiloCollaborazioneAzione + "]");
				//					} else {
				//						pd.setMessage("E' possibile associare un solo Servizio Applicativo alla Porta Applicativa [" + nomePorta + "] in quanto l'Accordo di Servizio e' stato definito con profilo [" + profiloCollaborazioneAccordo + "]");
				//					}
				//					
				//					//[TODO] controllare in fase di esecuzione il comportamento di questo punto
				//					ServletUtils.setGeneralAndPageDataIntoSession(session, gd, pd);
				//					// Forward control to the specified success URI
				//					return ServletUtils.getStrutsForwardEditModeFinished(mapping, PorteApplicativeCostanti.OBJECT_NAME_PORTE_APPLICATIVE_SERVIZIO_APPLICATIVO, 
				//							ForwardParams.ADD());
				//				}

				String[] servizioApplicativoList = PorteApplicativeServizioApplicativoAdd.loadSAErogatori(pa, saCore, soggInt, true);

				dati = porteApplicativeHelper.addPorteServizioApplicativoToDati(TipoOperazione.ADD, dati, servizioApplicativo, servizioApplicativoList, pa.sizeServizioApplicativoList(),true);

				dati = porteApplicativeHelper.addHiddenFieldsToDati(TipoOperazione.ADD, idPorta, idsogg, idPorta, idAsps, dati);

				pd.setDati(dati);

				ServletUtils.setGeneralAndPageDataIntoSession(session, gd, pd);

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
			Search ricerca = (Search) ServletUtils.getSearchObjectFromSession(session, Search.class);

			int idLista = Liste.PORTE_APPLICATIVE_SERVIZIO_APPLICATIVO;

			ricerca = porteApplicativeHelper.checkSearchParameters(idLista, ricerca);

			List<ServizioApplicativo> lista = porteApplicativeCore.porteAppServizioApplicativoList(Integer.parseInt(idPorta), ricerca);

			porteApplicativeHelper.preparePorteAppServizioApplicativoList(nomePorta, ricerca, lista);

			ServletUtils.setGeneralAndPageDataIntoSession(session, gd, pd);
			// Forward control to the specified success URI
			return ServletUtils.getStrutsForwardEditModeFinished(mapping, PorteApplicativeCostanti.OBJECT_NAME_PORTE_APPLICATIVE_SERVIZIO_APPLICATIVO, 
					ForwardParams.ADD());
		} catch (Exception e) {
			return ServletUtils.getStrutsForwardError(ControlStationCore.getLog(), e, pd, session, gd, mapping, 
					PorteApplicativeCostanti.OBJECT_NAME_PORTE_APPLICATIVE_SERVIZIO_APPLICATIVO,
					ForwardParams.ADD());
		} 
	}
	
	
	public static String[] loadSAErogatori(PortaApplicativa pa, ServiziApplicativiCore saCore, int soggInt, boolean addSAEsistenti) throws DriverConfigurazioneException, DriverConfigurazioneNotFound{
		// recupero nome dei servizi applicativi gia associati alla
		// porta applicativa
		HashSet<String> saEsistenti = new HashSet<String>();
		if(addSAEsistenti){
			for (int i = 0; i < pa.sizeServizioApplicativoList(); i++) {
				PortaApplicativaServizioApplicativo tmpSA = pa.getServizioApplicativo(i);
				saEsistenti.add(tmpSA.getNome());
			}
		}

		List<ServizioApplicativo> listaSA = saCore.getServiziApplicativiByIdErogatore(Long.valueOf(soggInt));

		// rif bug #45
		// I servizi applicativi da visualizzare sono quelli che hanno
		// -Integration Manager (getMessage abilitato)
		// -connettore != disabilitato
		ArrayList<ServizioApplicativo> validSA = new ArrayList<ServizioApplicativo>();
		for (ServizioApplicativo sa : listaSA) {
			InvocazioneServizio invServizio = sa.getInvocazioneServizio();
			Connettore connettore = invServizio != null ? invServizio.getConnettore() : null;
			StatoFunzionalita getMessage = invServizio != null ? invServizio.getGetMessage() : null;

			if ((connettore != null && !TipiConnettore.DISABILITATO.getNome().equals(connettore.getTipo())) || CostantiConfigurazione.ABILITATO.equals(getMessage)) {
				// il connettore non e' disabilitato oppure il get
				// message e' abilitato
				// Lo aggiungo solo se gia' non esiste tra quelli
				// aggiunti
				if (saEsistenti.contains(sa.getNome()) == false)
					validSA.add(sa);
			}
		}

		// Prendo la lista di servizioApplicativo associati al soggetto
		// e la metto in un array
		String[] servizioApplicativoList = new String[validSA.size()];
		for (int i = 0; i < validSA.size(); i++) {
			ServizioApplicativo sa = validSA.get(i);
			servizioApplicativoList[i] = sa.getNome();
		}
		return servizioApplicativoList;

	}
}
