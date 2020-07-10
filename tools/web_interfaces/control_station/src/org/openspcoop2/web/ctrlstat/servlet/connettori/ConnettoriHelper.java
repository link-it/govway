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
package org.openspcoop2.web.ctrlstat.servlet.connettori;

import java.io.File;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.openspcoop2.core.commons.ISearch;
import org.openspcoop2.core.config.Connettore;
import org.openspcoop2.core.config.GenericProperties;
import org.openspcoop2.core.config.InvocazioneServizio;
import org.openspcoop2.core.config.PortaApplicativa;
import org.openspcoop2.core.config.PortaDelegata;
import org.openspcoop2.core.config.RispostaAsincrona;
import org.openspcoop2.core.config.ServizioApplicativo;
import org.openspcoop2.core.config.constants.CostantiConfigurazione;
import org.openspcoop2.core.config.constants.StatoFunzionalita;
import org.openspcoop2.core.config.driver.DriverConfigurazioneException;
import org.openspcoop2.core.config.driver.DriverConfigurazioneNotFound;
import org.openspcoop2.core.constants.CostantiConnettori;
import org.openspcoop2.core.constants.CostantiDB;
import org.openspcoop2.core.constants.TipiConnettore;
import org.openspcoop2.core.constants.TransferLengthModes;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.core.registry.AccordoServizioParteSpecifica;
import org.openspcoop2.core.registry.Fruitore;
import org.openspcoop2.core.registry.Property;
import org.openspcoop2.core.registry.Soggetto;
import org.openspcoop2.core.registry.constants.StatiAccordo;
import org.openspcoop2.core.registry.driver.IDServizioFactory;
import org.openspcoop2.pdd.core.autenticazione.ApiKeyUtilities;
import org.openspcoop2.pdd.core.connettori.ConnettoreNULL;
import org.openspcoop2.pdd.core.connettori.ConnettoreNULLEcho;
import org.openspcoop2.utils.DynamicStringReplace;
import org.openspcoop2.utils.UtilsException;
import org.openspcoop2.utils.certificate.ArchiveLoader;
import org.openspcoop2.utils.certificate.ArchiveType;
import org.openspcoop2.utils.certificate.PrincipalType;
import org.openspcoop2.utils.crypt.PasswordGenerator;
import org.openspcoop2.utils.crypt.PasswordVerifier;
import org.openspcoop2.utils.transport.http.SSLConstants;
import org.openspcoop2.utils.transport.http.SSLUtilities;
import org.openspcoop2.web.ctrlstat.core.Connettori;
import org.openspcoop2.web.ctrlstat.core.ControlStationCore;
import org.openspcoop2.web.ctrlstat.core.Utilities;
import org.openspcoop2.web.ctrlstat.costanti.CostantiControlStation;
import org.openspcoop2.web.ctrlstat.costanti.TipologiaConnettori;
import org.openspcoop2.web.ctrlstat.dao.SoggettoCtrlStat;
import org.openspcoop2.web.ctrlstat.plugins.ExtendedConnettore;
import org.openspcoop2.web.ctrlstat.plugins.ExtendedConnettoreConverter;
import org.openspcoop2.web.ctrlstat.plugins.servlet.ServletExtendedConnettoreUtils;
import org.openspcoop2.web.ctrlstat.servlet.ConsoleHelper;
import org.openspcoop2.web.ctrlstat.servlet.apc.AccordiServizioParteComuneCostanti;
import org.openspcoop2.web.ctrlstat.servlet.aps.AccordiServizioParteSpecificaCostanti;
import org.openspcoop2.web.ctrlstat.servlet.aps.erogazioni.ErogazioniCostanti;
import org.openspcoop2.web.ctrlstat.servlet.archivi.ArchiviCostanti;
import org.openspcoop2.web.ctrlstat.servlet.config.ConfigurazioneCostanti;
import org.openspcoop2.web.ctrlstat.servlet.pa.PorteApplicativeCostanti;
import org.openspcoop2.web.ctrlstat.servlet.pd.PorteDelegateCostanti;
import org.openspcoop2.web.ctrlstat.servlet.pd.PorteDelegateHelper;
import org.openspcoop2.web.ctrlstat.servlet.sa.ServiziApplicativiCostanti;
import org.openspcoop2.web.ctrlstat.servlet.sa.ServiziApplicativiHelper;
import org.openspcoop2.web.ctrlstat.servlet.soggetti.SoggettiCostanti;
import org.openspcoop2.web.lib.mvc.BinaryParameter;
import org.openspcoop2.web.lib.mvc.Costanti;
import org.openspcoop2.web.lib.mvc.DataElement;
import org.openspcoop2.web.lib.mvc.DataElementInfo;
import org.openspcoop2.web.lib.mvc.DataElementType;
import org.openspcoop2.web.lib.mvc.PageData;
import org.openspcoop2.web.lib.mvc.Parameter;
import org.openspcoop2.web.lib.mvc.ServletUtils;
import org.openspcoop2.web.lib.mvc.TipoOperazione;

/**
 * ConnettoriHelper
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ConnettoriHelper extends ConsoleHelper {

	public ConnettoriHelper(HttpServletRequest request, PageData pd, 
			HttpSession session) throws Exception {
		super(request, pd,  session);
	}
	public ConnettoriHelper(ControlStationCore core, HttpServletRequest request, PageData pd, 
			HttpSession session) throws Exception {
		super(core, request, pd,  session);
	}

	public String getAutenticazioneHttp(String autenticazioneHttp,String endpointtype, String user){
		if((endpointtype!=null && (endpointtype.equals(TipiConnettore.HTTP.toString()) || endpointtype.equals(TipiConnettore.HTTPS.toString())))){
			if ( autenticazioneHttp==null && user!=null && !"".equals(user) ){
				autenticazioneHttp =  Costanti.CHECK_BOX_ENABLED;
			}  
		}
		else{
			autenticazioneHttp = null;
		}
		return autenticazioneHttp;
	}
	
	public void setTitleProprietaConnettoriCustom(PageData pd,TipoOperazione tipoOperazione,
			String servlet, String id, String nomeprov, String tipoprov,String nomeservizio,String tiposervizio,
			String myId, String correlato, String idSoggErogatore, String nomeservizioApplicativo,String idsil,String tipoAccordo,
			String provider, String idPorta) throws Exception{
		
		boolean isModalitaCompleta = this.isModalitaCompleta();
		Boolean vistaErogazioni = ServletUtils.getBooleanAttributeFromSession(ErogazioniCostanti.ASPS_EROGAZIONI_ATTRIBUTO_VISTA_EROGAZIONI, this.session);
		
		boolean accessoDaListaAPS = false;
		String accessoDaAPSParametro = null;
		// nell'erogazione vale sempre
		//if(gestioneErogatori) {
		accessoDaAPSParametro = this.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_CONNETTORE_DA_LISTA_APS);
		if(Costanti.CHECK_BOX_ENABLED_TRUE.equals(accessoDaAPSParametro)) {
			accessoDaListaAPS = true;
		}
		
		String azioneConnettoreIdPorta = this.getParameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_FRUITORE_VIEW_CONNETTORE_MAPPING_AZIONE_ID_PORTA);
		if(azioneConnettoreIdPorta==null) {
			azioneConnettoreIdPorta="";
		}
				
		if (AccordiServizioParteSpecificaCostanti.SERVLET_NAME_APS_CHANGE.equals(servlet)) {
			int idServizioInt = Integer.parseInt(id);
			AccordoServizioParteSpecifica asps = this.apsCore.getAccordoServizioParteSpecifica(idServizioInt);
			
			ServletUtils.setPageDataTitle(pd, 
					// t1
					new Parameter(AccordiServizioParteSpecificaCostanti.LABEL_APS, AccordiServizioParteSpecificaCostanti.SERVLET_NAME_APS_LIST), 
					// t2
					new Parameter(
						"Connettore del servizio "+IDServizioFactory.getInstance().getUriFromAccordo(asps), 
						AccordiServizioParteSpecificaCostanti.SERVLET_NAME_APS_CHANGE+
						"?"+ConnettoriCostanti.PARAMETRO_CONNETTORE_CUSTOM_ID+"=" + id+
						"&"+ConnettoriCostanti.PARAMETRO_CONNETTORE_CUSTOM_NOME_SERVIZIO+"=" + nomeservizio + 
						"&"+ConnettoriCostanti.PARAMETRO_CONNETTORE_CUSTOM_TIPO_SERVIZIO+"=" + tiposervizio
						)
					);

		}
		
		if (AccordiServizioParteSpecificaCostanti.SERVLET_NAME_APS_FRUITORI_CHANGE.equals(servlet)) {
			int idServizioInt = Integer.parseInt(id);
			AccordoServizioParteSpecifica asps = this.apsCore.getAccordoServizioParteSpecifica(idServizioInt);
			int idServizioFruitoreInt = Integer.parseInt(myId);
			Fruitore servFru = this.apsCore.getServizioFruitore(idServizioFruitoreInt);
			String nomefru = servFru.getNome();
			String tipofru = servFru.getTipo();
			Long idSoggettoFruitore = servFru.getIdSoggetto();
			
			Integer parentPD = ServletUtils.getIntegerAttributeFromSession(PorteDelegateCostanti.ATTRIBUTO_PORTE_DELEGATE_PARENT, this.session);
			if(parentPD == null) 
				parentPD = PorteDelegateCostanti.ATTRIBUTO_PORTE_DELEGATE_PARENT_NONE;
			
			String tipologia = ServletUtils.getObjectFromSession(this.session, String.class, AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_TIPO_EROGAZIONE);
			boolean gestioneFruitori = false;
			if(tipologia!=null) {
				if(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_TIPO_EROGAZIONE_VALUE_FRUIZIONE.equals(tipologia)) {
					gestioneFruitori = true;
				}
			}
			
			
			boolean viewOnlyConnettore = gestioneFruitori || (PorteDelegateCostanti.ATTRIBUTO_PORTE_DELEGATE_PARENT_CONFIGURAZIONE==parentPD);
			
			PorteDelegateHelper porteDelegateHelper = new PorteDelegateHelper(this.request, this.pd, this.session);
			List<Parameter> lstParm = porteDelegateHelper.getTitoloPD(PorteDelegateCostanti.ATTRIBUTO_PORTE_DELEGATE_PARENT_CONFIGURAZIONE, idSoggettoFruitore+"",id, myId);
			
			String protocollo = this.apsCore.getProtocolloAssociatoTipoServizio(asps.getTipo());
			String fruitoreLabel = this.getLabelNomeSoggetto(protocollo, tipofru , nomefru);
			
			List<Parameter> lstParameteriASPSFruitoriChange = new ArrayList<>();
			
			Parameter pId = new Parameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_ID, asps.getId()+"");
			Parameter pIdSoggettoErogatore = new Parameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_ID_SOGGETTO_EROGATORE, asps.getIdSoggetto()+"");
			Parameter pIdFruitore = new Parameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_MY_ID, myId);
			Parameter pConnettoreDaListaAPS = new Parameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_CONNETTORE_DA_LISTA_APS, accessoDaAPSParametro);
			Parameter pIdProviderFruitore = new Parameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_PROVIDER_FRUITORE, idSoggettoFruitore + "");
			Parameter pAzioneConnettoreIdPorta = new Parameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_FRUITORE_VIEW_CONNETTORE_MAPPING_AZIONE_ID_PORTA, azioneConnettoreIdPorta);
			
			lstParameteriASPSFruitoriChange.add(pId);
			lstParameteriASPSFruitoriChange.add(pIdFruitore);
			lstParameteriASPSFruitoriChange.add(pIdSoggettoErogatore);
			lstParameteriASPSFruitoriChange.add(pIdProviderFruitore);
			lstParameteriASPSFruitoriChange.add(pConnettoreDaListaAPS);
			lstParameteriASPSFruitoriChange.add(pAzioneConnettoreIdPorta);
			
			if(viewOnlyConnettore) {
				
				String labelPerPorta = null;
				if(accessoDaListaAPS) {
					if(!isModalitaCompleta) {
						if(vistaErogazioni != null && vistaErogazioni.booleanValue()) {
							labelPerPorta = ErogazioniCostanti.LABEL_ASPS_PORTE_DELEGATE_MODIFICA_CONNETTORE;
						} else {
							labelPerPorta = PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_CONNETTORE_DI+	porteDelegateHelper.getLabelIdServizio(asps);
						}
					}
					else {
						labelPerPorta = PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_CONNETTORE;
					}
				}
				else {
					PortaDelegata portaDelegata = this.porteDelegateCore.getPortaDelegata(Long.parseLong(azioneConnettoreIdPorta)); 
					labelPerPorta = this.porteDelegateCore.getLabelRegolaMappingFruizionePortaDelegata(
							PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_CONNETTORE_DI,
							PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_CONNETTORE,
							portaDelegata);
				}
				
				Parameter pConnettore = new Parameter(labelPerPorta, AccordiServizioParteSpecificaCostanti.SERVLET_NAME_APS_FRUITORI_CHANGE, lstParameteriASPSFruitoriChange.toArray(new Parameter[lstParameteriASPSFruitoriChange.size()]));
				if(accessoDaListaAPS) {
					lstParm.set(lstParm.size()-1, pConnettore);
				}
				else {
					lstParm.add(pConnettore);
				}
			}
			else {
				lstParm.set(lstParm.size()-1, new Parameter(fruitoreLabel, AccordiServizioParteSpecificaCostanti.SERVLET_NAME_APS_FRUITORI_CHANGE, lstParameteriASPSFruitoriChange.toArray(new Parameter[lstParameteriASPSFruitoriChange.size()])));
			}
			
			ServletUtils.setPageDataTitle(pd,lstParm);
		}
		
		if (ServiziApplicativiCostanti.SERVLET_NAME_SERVIZI_APPLICATIVI_ENDPOINT.equals(servlet)) {
			int idInt = Integer.parseInt(idsil);
			ServizioApplicativo sa = this.saCore.getServizioApplicativo(idInt);
			
			
			Integer parentSA = ServletUtils.getIntegerAttributeFromSession(ServiziApplicativiCostanti.ATTRIBUTO_SERVIZI_APPLICATIVI_PARENT, this.session);
			if(parentSA == null) parentSA = ServiziApplicativiCostanti.ATTRIBUTO_SERVIZI_APPLICATIVI_PARENT_NONE;
			
			ServiziApplicativiHelper saHelper = new ServiziApplicativiHelper(this.request, this.pd, this.session);
			List<Parameter> lstParm = saHelper.getTitoloSA(parentSA, provider, id, idPorta);
			
			String labelPerPorta = null;
			if(parentSA!=null && (parentSA.intValue() == ServiziApplicativiCostanti.ATTRIBUTO_SERVIZI_APPLICATIVI_PARENT_CONFIGURAZIONE)) {
				
				AccordoServizioParteSpecifica asps = this.apsCore.getAccordoServizioParteSpecifica(Integer.parseInt(id));
				
				if(accessoDaListaAPS) {
					if(!isModalitaCompleta) {
						if(vistaErogazioni != null && vistaErogazioni.booleanValue()) {
							labelPerPorta = ErogazioniCostanti.LABEL_ASPS_PORTE_APPLICATIVE_MODIFICA_CONNETTORE;
						} else {
							labelPerPorta = PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_CONNETTORE_DI+
								saHelper.getLabelIdServizio(asps);
						}
					}
					else {
						labelPerPorta = PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_CONNETTORE;
					}
				}
				else {
					PortaApplicativa pa = this.porteApplicativeCore.getPortaApplicativa(Long.parseLong(idPorta)); 
					labelPerPorta = this.porteApplicativeCore.getLabelRegolaMappingErogazionePortaApplicativa(
							PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_CONNETTORE_DI,
							PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_CONNETTORE,
							pa);
				}
			}
			else {
				labelPerPorta = ServiziApplicativiCostanti.LABEL_PARAMETRO_SERVIZI_APPLICATIVI_INVOCAZIONE_SERVIZIO_DI+nomeservizioApplicativo;
				
				labelPerPorta = "Connettore del servizio applicativo (InvocazioneServizio) " + nomeservizioApplicativo+" del soggetto "+sa.getTipoSoggettoProprietario()+"/"+sa.getNomeSoggettoProprietario();
			}
			
			if(accessoDaListaAPS) {
				lstParm.remove(lstParm.size()-1);
			}
			
			List<Parameter> lstParameteriSAEndpoint = new ArrayList<>();
			lstParameteriSAEndpoint.add(new Parameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_ASPS, id));
			lstParameteriSAEndpoint.add(new Parameter(ServiziApplicativiCostanti.PARAMETRO_SERVIZI_APPLICATIVI_PROVIDER, provider));
			lstParameteriSAEndpoint.add(new Parameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_CUSTOM_ID_SERVIZIO_APPLICATIVO, idsil));
			lstParameteriSAEndpoint.add(new Parameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_CUSTOM_NOME_SERVIZIO_APPLICATIVO,nomeservizioApplicativo ));
			lstParameteriSAEndpoint.add(new Parameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_PORTA, idPorta ));
			lstParameteriSAEndpoint.add(new Parameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_CONFIGURAZIONE_DATI_INVOCAZIONE,Costanti.CHECK_BOX_ENABLED_TRUE));
			lstParameteriSAEndpoint.add(new Parameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_CONNETTORE_DA_LISTA_APS,accessoDaAPSParametro ));

			lstParm.add(new Parameter(labelPerPorta,ServiziApplicativiCostanti.SERVLET_NAME_SERVIZI_APPLICATIVI_ENDPOINT, lstParameteriSAEndpoint.toArray(new Parameter[lstParameteriSAEndpoint.size()]))); 
					
			ServletUtils.setPageDataTitle(pd,lstParm);
		}
		
		if (ServiziApplicativiCostanti.SERVLET_NAME_SERVIZI_APPLICATIVI_ENDPOINT_RISPOSTA.equals(servlet)) {
			int idInt = Integer.parseInt(idsil);
			ServizioApplicativo sa = this.saCore.getServizioApplicativo(idInt);

			ServletUtils.setPageDataTitle(pd, 
					// t1
					new Parameter(ServiziApplicativiCostanti.LABEL_SERVIZIO_APPLICATIVO, ServiziApplicativiCostanti.SERVLET_NAME_SERVIZI_APPLICATIVI_LIST), 
					// t2
					new Parameter(			 
						"Connettore del servizio applicativo (RispostaAsincrona) " + nomeservizioApplicativo+" del soggetto "+sa.getTipoSoggettoProprietario()+"/"+sa.getNomeSoggettoProprietario(),
						ServiziApplicativiCostanti.SERVLET_NAME_SERVIZI_APPLICATIVI_ENDPOINT_RISPOSTA+
						"?"+ConnettoriCostanti.PARAMETRO_CONNETTORE_CUSTOM_NOME_SERVIZIO_APPLICATIVO+"=" + nomeservizioApplicativo + 
						"&"+ConnettoriCostanti.PARAMETRO_CONNETTORE_CUSTOM_ID_SERVIZIO_APPLICATIVO+"=" + idsil +
						"&"+ServiziApplicativiCostanti.PARAMETRO_SERVIZI_APPLICATIVI_PROVIDER+"=" + provider
					)
					);

		}
		
		if (SoggettiCostanti.SERVLET_NAME_SOGGETTI_ENDPOINT.equals(servlet)) {
			String protocollo = this.soggettiCore.getProtocolloAssociatoTipoSoggetto(tipoprov);
			String label = this.getLabelNomeSoggetto(protocollo, tipoprov , nomeprov);

			ServletUtils.setPageDataTitle(pd, 
					// t1
					new Parameter(SoggettiCostanti.LABEL_SOGGETTI,SoggettiCostanti.SERVLET_NAME_SOGGETTI_LIST), 
					// t2
					new Parameter("Connettore di " + label ,
						SoggettiCostanti.SERVLET_NAME_SOGGETTI_ENDPOINT+
						"?"+ConnettoriCostanti.PARAMETRO_CONNETTORE_CUSTOM_ID+"=" + id + 
						"&"+ConnettoriCostanti.PARAMETRO_CONNETTORE_CUSTOM_NOME_SOGGETTO+"=" + nomeprov + 
						"&"+ConnettoriCostanti.PARAMETRO_CONNETTORE_CUSTOM_TIPO_SOGGETTO+"=" + tipoprov
						)
					);

		}

		if(!TipoOperazione.LIST.equals(tipoOperazione)){
			ServletUtils.appendPageDataTitle(pd,
					// t1
					new Parameter(
						ConnettoriCostanti.LABEL_CONNETTORE_PROPRIETA,
						ConnettoriCostanti.SERVLET_NAME_CONNETTORI_CUSTOM_PROPERTIES_LIST,
						new Parameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_CUSTOM_SERVLET,servlet), 
						new Parameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_CUSTOM_ID,id),
						new Parameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_CUSTOM_NOME_SOGGETTO,nomeprov), 
						new Parameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_CUSTOM_TIPO_SOGGETTO,tipoprov),
						new Parameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_CUSTOM_NOME_SERVIZIO,nomeservizio), 
						new Parameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_CUSTOM_TIPO_SERVIZIO,tiposervizio), 
						new Parameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_CUSTOM_CORRELATO,correlato),
						new Parameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_CUSTOM_MY_ID,myId),
						new Parameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_CUSTOM_ID_SOGGETTO_EROGATORE,idSoggErogatore),
						new Parameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_CUSTOM_NOME_SERVIZIO_APPLICATIVO,nomeservizioApplicativo), 
						new Parameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_CUSTOM_ID_SERVIZIO_APPLICATIVO,idsil),
						new Parameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_PORTA, idPorta ),
						new Parameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_CONNETTORE_DA_LISTA_APS,accessoDaAPSParametro ),
						new Parameter(ServiziApplicativiCostanti.PARAMETRO_SERVIZI_APPLICATIVI_PROVIDER, provider),
						new Parameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_FRUITORE_VIEW_CONNETTORE_MAPPING_AZIONE_ID_PORTA, azioneConnettoreIdPorta)
						
							)
					);
			
			ServletUtils.appendPageDataTitle(pd,
					ServletUtils.getParameterAggiungi());
		} else {
			ServletUtils.appendPageDataTitle(pd,
					// t1
					new Parameter(
						ConnettoriCostanti.LABEL_CONNETTORE_PROPRIETA,null));
		}
		
		
		
	}

	
	
	
	public void prepareConnettorePropList(List<?> lista, ISearch ricerca,
			int newMyId,String tipoAccordo)
	throws Exception {
		try {
			
			String servlet = this.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_CUSTOM_SERVLET);
			String id = this.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_CUSTOM_ID);
			String nomeprov = this.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_CUSTOM_NOME_SOGGETTO);
			String tipoprov = this.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_CUSTOM_TIPO_SOGGETTO);
			String nomeservizio = this.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_CUSTOM_NOME_SERVIZIO);
			String tiposervizio = this.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_CUSTOM_TIPO_SERVIZIO);
			String myId = this.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_CUSTOM_MY_ID);
			if (newMyId != 0)
				myId = ""+newMyId;
			String correlato = this.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_CUSTOM_CORRELATO);
			String idSoggErogatore = this.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_CUSTOM_ID_SOGGETTO_EROGATORE);
			String nomeservizioApplicativo = this.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_CUSTOM_NOME_SERVIZIO_APPLICATIVO);
			String idsil = this.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_CUSTOM_ID_SERVIZIO_APPLICATIVO);
			String provider = this.getParameter(ServiziApplicativiCostanti.PARAMETRO_SERVIZI_APPLICATIVI_PROVIDER);
			String idPorta = this.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_CUSTOM_ID_PORTA);
			if(idPorta == null)
				idPorta = "";
			
			String accessoDaAPSParametro = this.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_CONNETTORE_DA_LISTA_APS);
			if(accessoDaAPSParametro == null)
				accessoDaAPSParametro = "";
			
			String azioneConnettoreIdPorta = this.getParameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_FRUITORE_VIEW_CONNETTORE_MAPPING_AZIONE_ID_PORTA);
			if(azioneConnettoreIdPorta == null)
				azioneConnettoreIdPorta = "";
			
			ServletUtils.addListElementIntoSession(this.session, ConnettoriCostanti.OBJECT_NAME_CONNETTORI_CUSTOM_PROPERTIES,
					new Parameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_CUSTOM_SERVLET, servlet),
					new Parameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_CUSTOM_ID, id),
					new Parameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_CUSTOM_NOME_SOGGETTO, nomeprov),
					new Parameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_CUSTOM_TIPO_SOGGETTO, tipoprov),
					new Parameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_CUSTOM_NOME_SERVIZIO, nomeservizio),
					new Parameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_CUSTOM_TIPO_SERVIZIO, tiposervizio),
					new Parameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_CUSTOM_MY_ID, myId),
					new Parameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_CUSTOM_CORRELATO, correlato),
					new Parameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_CUSTOM_ID_SOGGETTO_EROGATORE, idSoggErogatore),
					new Parameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_CUSTOM_NOME_SERVIZIO_APPLICATIVO, nomeservizioApplicativo),
					new Parameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_CUSTOM_ID_SERVIZIO_APPLICATIVO, idsil),
					new Parameter(ServiziApplicativiCostanti.PARAMETRO_SERVIZI_APPLICATIVI_PROVIDER, provider),
					new Parameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_CUSTOM_ID_PORTA, idPorta),
					new Parameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_CONNETTORE_DA_LISTA_APS, accessoDaAPSParametro),
					new Parameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_FRUITORE_VIEW_CONNETTORE_MAPPING_AZIONE_ID_PORTA, azioneConnettoreIdPorta));
			
//			Boolean contaListe = ServletUtils.getContaListeFromSession(this.session);
//			
			//int idLista = Liste.SERVIZI_SERVIZIO_APPLICATIVO;
			//int limit = ricerca.getPageSize(idLista);
			//int offset = ricerca.getIndexIniziale(idLista);
			//String search = (org.openspcoop2.core.constants.Costanti.SESSION_ATTRIBUTE_VALUE_RICERCA_UNDEFINED.equals(ricerca.getSearchString(idLista)) ? "" : ricerca.getSearchString(idLista));

			//this.pd.setIndex(offset);
			//this.pd.setPageSize(limit);
			this.pd.setNumEntries(lista.size());

			// setto la barra del titolo
			setTitleProprietaConnettoriCustom(this.pd, TipoOperazione.LIST, servlet, id, nomeprov, tipoprov, nomeservizio, tiposervizio, 
					myId, correlato, idSoggErogatore, nomeservizioApplicativo, idsil, tipoAccordo, provider,idPorta);
			
			ServletUtils.disabledPageDataSearch(this.pd);
			

			// setto le label delle colonne
			String[] labels = { ConnettoriCostanti.LABEL_PARAMETRO_CONNETTORE_CUSTOM_NOME, ConnettoriCostanti.LABEL_PARAMETRO_CONNETTORE_CUSTOM_VALORE };
			this.pd.setLabels(labels);

			// preparo i dati
			Vector<Vector<DataElement>> dati = new Vector<Vector<DataElement>>();

			if (lista != null) {
				Iterator<?> it = lista.iterator();

				while (it.hasNext()) {
					String nome = "", valore = "";
					if (servlet.equals(ServiziApplicativiCostanti.SERVLET_NAME_SERVIZI_APPLICATIVI_ENDPOINT) ||
							servlet.equals(ServiziApplicativiCostanti.SERVLET_NAME_SERVIZI_APPLICATIVI_ENDPOINT_RISPOSTA)) {
						org.openspcoop2.core.config.Property cp =
							(org.openspcoop2.core.config.Property) it.next();
						nome = cp.getNome();
						valore = cp.getValore();
					} else {
						Property cp = (Property) it.next();
						nome = cp.getNome();
						valore = cp.getValore();
					}

					Vector<DataElement> e = new Vector<DataElement>();

					DataElement de = new DataElement();
					de.setValue(nome);
					de.setIdToRemove(nome);
					e.addElement(de);

					de = new DataElement();
					de.setValue(valore);
					e.addElement(de);

					dati.addElement(e);
				}
			}

			// inserisco i campi hidden
			Hashtable<String, String> hidden = new Hashtable<String, String>();
			hidden.put(ConnettoriCostanti.PARAMETRO_CONNETTORE_CUSTOM_SERVLET, servlet);
			hidden.put(ConnettoriCostanti.PARAMETRO_CONNETTORE_CUSTOM_ID, id != null ? id : "");
			hidden.put(ConnettoriCostanti.PARAMETRO_CONNETTORE_CUSTOM_NOME_SOGGETTO, nomeprov != null ? nomeprov : "");
			hidden.put(ConnettoriCostanti.PARAMETRO_CONNETTORE_CUSTOM_TIPO_SOGGETTO, tipoprov != null ? tipoprov : "");
			hidden.put(ConnettoriCostanti.PARAMETRO_CONNETTORE_CUSTOM_NOME_SERVIZIO, nomeservizio != null ? nomeservizio : "");
			hidden.put(ConnettoriCostanti.PARAMETRO_CONNETTORE_CUSTOM_TIPO_SERVIZIO, tiposervizio != null ? tiposervizio : "");
			hidden.put(ConnettoriCostanti.PARAMETRO_CONNETTORE_CUSTOM_MY_ID, myId != null ? myId : "");
			hidden.put(ConnettoriCostanti.PARAMETRO_CONNETTORE_CUSTOM_CORRELATO, correlato != null ? correlato : "");
			hidden.put(ConnettoriCostanti.PARAMETRO_CONNETTORE_CUSTOM_ID_SOGGETTO_EROGATORE, idSoggErogatore != null ? idSoggErogatore : "");
			hidden.put(ConnettoriCostanti.PARAMETRO_CONNETTORE_CUSTOM_NOME_SERVIZIO_APPLICATIVO, nomeservizioApplicativo != null ? nomeservizioApplicativo : "");
			hidden.put(ConnettoriCostanti.PARAMETRO_CONNETTORE_CUSTOM_ID_SERVIZIO_APPLICATIVO, idsil != null ? idsil : "");
			hidden.put(ConnettoriCostanti.PARAMETRO_CONNETTORE_CUSTOM_ID_PORTA, idPorta != null ? idPorta : "");
			hidden.put(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_CONNETTORE_DA_LISTA_APS, accessoDaAPSParametro != null ? accessoDaAPSParametro : "");
			hidden.put(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_FRUITORE_VIEW_CONNETTORE_MAPPING_AZIONE_ID_PORTA, azioneConnettoreIdPorta != null ? azioneConnettoreIdPorta : "");

			this.pd.setHidden(hidden);

			this.pd.setDati(dati);
			this.pd.setAddButton(true);

		} catch (Exception e) {
			this.log.error("Exception: " + e.getMessage(), e);
			throw new Exception(e);
		}
	}
	
	public Vector<DataElement> addEndPointToDati(Vector<DataElement> dati,String connettoreDebug,
			String endpointtype, String autenticazioneHttp, String prefix, String url, String nome, String tipo,
			String user, String password, String initcont, String urlpgk,
			String provurl, String connfact, String sendas, String objectName, TipoOperazione tipoOperazione,
			String httpsurl, String httpstipologia, boolean httpshostverify,
			String httpspath, String httpstipo, String httpspwd,
			String httpsalgoritmo, boolean httpsstato, String httpskeystore,
			String httpspwdprivatekeytrust, String httpspathkey,
			String httpstipokey, String httpspwdkey,
			String httpspwdprivatekey, String httpsalgoritmokey,
			String httpsKeyAlias, String httpsTrustStoreCRLs,
			String tipoconn, String servletChiamante, String elem1, String elem2, String elem3,
			String elem4, String elem5, String elem6, String elem7, String elem8,
			boolean showSectionTitle,
			Boolean isConnettoreCustomUltimaImmagineSalvata,
			String proxyEnabled, String proxyHost, String proxyPort, String proxyUsername, String proxyPassword,
			String tempiRisposta_enabled, String tempiRisposta_connectionTimeout, String tempiRisposta_readTimeout, String tempiRisposta_tempoMedioRisposta,
			String opzioniAvanzate, String transfer_mode, String transfer_mode_chunk_size, String redirect_mode, String redirect_max_hop,
			String requestOutputFileName,String requestOutputFileNameHeaders,String requestOutputParentDirCreateIfNotExists,String requestOutputOverwriteIfExists,
			String responseInputMode, String responseInputFileName, String responseInputFileNameHeaders, String responseInputDeleteAfterRead, String responseInputWaitTime,
			boolean autenticazioneToken, String tokenPolicy,
			List<ExtendedConnettore> listExtendedConnettore, boolean forceEnabled,
			String protocollo, boolean forceHttps, boolean forceHttpsClient,
			boolean visualizzaSezioneSAServer, boolean servizioApplicativoServerEnabled, String servizioApplicativoServer, String[] listaSAServer) throws Exception {
		return addEndPointToDati(dati, connettoreDebug, endpointtype, autenticazioneHttp, prefix, url, nome, tipo, user,
				password, initcont, urlpgk, provurl, connfact, sendas,
				objectName,tipoOperazione, httpsurl, httpstipologia, httpshostverify,
				httpspath, httpstipo, httpspwd, httpsalgoritmo, httpsstato,
				httpskeystore, httpspwdprivatekeytrust, httpspathkey,
				httpstipokey, httpspwdkey, 
				httpspwdprivatekey, httpsalgoritmokey, 
				httpsKeyAlias, httpsTrustStoreCRLs,
				tipoconn, servletChiamante, elem1, elem2, elem3,
				elem4, elem5, elem6, elem7, elem8, null, showSectionTitle,
				isConnettoreCustomUltimaImmagineSalvata,
				proxyEnabled, proxyHost, proxyPort, proxyUsername, proxyPassword,
				tempiRisposta_enabled, tempiRisposta_connectionTimeout, tempiRisposta_readTimeout, tempiRisposta_tempoMedioRisposta,
				opzioniAvanzate, transfer_mode, transfer_mode_chunk_size, redirect_mode, redirect_max_hop,
				requestOutputFileName,requestOutputFileNameHeaders,requestOutputParentDirCreateIfNotExists,requestOutputOverwriteIfExists,
				responseInputMode, responseInputFileName, responseInputFileNameHeaders, responseInputDeleteAfterRead, responseInputWaitTime,
				autenticazioneToken, tokenPolicy,
				listExtendedConnettore, forceEnabled,
				protocollo, forceHttps, forceHttpsClient,visualizzaSezioneSAServer, servizioApplicativoServerEnabled, servizioApplicativoServer, listaSAServer);
	}

	// Controlla i dati del connettore custom
	boolean connettorePropCheckData() throws Exception {
		try {
			String servlet = this.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_CUSTOM_SERVLET);
			String id = this.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_CUSTOM_ID);
			//String nomeprov = this.getParameter("nomeprov");
			//String tipoprov = this.getParameter("tipoprov");
			//String nomeservizio = this.getParameter("nomeservizio");
			//String tiposervizio = this.getParameter("tiposervizio");
			String myId = this.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_CUSTOM_MY_ID);
			//String correlato = this.getParameter("correlato");
			//String idSoggErogatore = this.getParameter("idSoggErogatore");
			//String nomeservizioApplicativo = this.getParameter("nomeservizioApplicativo");
			String idsil = this.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_CUSTOM_ID_SERVIZIO_APPLICATIVO);

			String nome = this.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_CUSTOM_NOME);
			String valore = this.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_CUSTOM_VALORE);

			// Campi obbligatori
			if (nome.equals("") || valore.equals("")) {
				String tmpElenco = "";
				if (nome.equals("")) {
					tmpElenco = ConnettoriCostanti.LABEL_PARAMETRO_CONNETTORE_CUSTOM_NOME;
				}
				if (valore.equals("")) {
					if (tmpElenco.equals("")) {
						tmpElenco = ConnettoriCostanti.LABEL_PARAMETRO_CONNETTORE_CUSTOM_VALORE;
					} else {
						tmpElenco = tmpElenco + ", "+ConnettoriCostanti.LABEL_PARAMETRO_CONNETTORE_CUSTOM_VALORE;
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
			
			// Lunghezza
			if(this.checkLength255(nome, ConnettoriCostanti.LABEL_PARAMETRO_CONNETTORE_CUSTOM_NOME)==false) {
				return false;
			}
			if(this.checkLength255(valore, ConnettoriCostanti.LABEL_PARAMETRO_CONNETTORE_CUSTOM_VALORE)==false) {
				return false;
			}

			// Controllo che la property non sia gia' stata
			// registrata
			boolean giaRegistratoProprietaNormale = false;
			boolean giaRegistratoProprietaDebug = false;
			if (servlet.equals(AccordiServizioParteSpecificaCostanti.SERVLET_NAME_APS_CHANGE)) {
				AccordoServizioParteSpecifica asps = this.apsCore.getAccordoServizioParteSpecifica(Long.parseLong(id));
				org.openspcoop2.core.registry.Connettore connettore = asps.getConfigurazioneServizio().getConnettore();
				for (int j = 0; j < connettore.sizePropertyList(); j++) {
					Property tmpProp = connettore.getProperty(j);
					if (tmpProp.getNome().equals(nome)) {
						if(CostantiDB.CONNETTORE_DEBUG.equals(nome)){
							giaRegistratoProprietaDebug = true;
						}else{
							giaRegistratoProprietaNormale = true;
						}
						break;
					}
				}
			}
			if (servlet.equals(AccordiServizioParteSpecificaCostanti.SERVLET_NAME_APS_FRUITORI_CHANGE)) {
				int idServizioFruitoreInt = Integer.parseInt(myId);
				Fruitore servFru = this.apsCore.getServizioFruitore(idServizioFruitoreInt);
				org.openspcoop2.core.registry.Connettore connettore = servFru.getConnettore();
				for (int j = 0; j < connettore.sizePropertyList(); j++) {
					Property tmpProp = connettore.getProperty(j);
					if (tmpProp.getNome().equals(nome)) {
						if(CostantiDB.CONNETTORE_DEBUG.equals(nome)){
							giaRegistratoProprietaDebug = true;
						}else{
							giaRegistratoProprietaNormale = true;
						}
						break;
					}
				}
			}
			if (servlet.equals(ServiziApplicativiCostanti.SERVLET_NAME_SERVIZI_APPLICATIVI_ENDPOINT)) {
				int idSilInt = Integer.parseInt(idsil);
				ServizioApplicativo sa = this.saCore.getServizioApplicativo(idSilInt);
				InvocazioneServizio is = sa.getInvocazioneServizio();
				org.openspcoop2.core.config.Connettore connettore = is.getConnettore();
				for (int j = 0; j < connettore.sizePropertyList(); j++) {
					org.openspcoop2.core.config.Property tmpProp = connettore.getProperty(j);
					if (tmpProp.getNome().equals(nome)) {
						if(CostantiDB.CONNETTORE_DEBUG.equals(nome)){
							giaRegistratoProprietaDebug = true;
						}else{
							giaRegistratoProprietaNormale = true;
						}
						break;
					}
				}
			}
			if (servlet.equals(ServiziApplicativiCostanti.SERVLET_NAME_SERVIZI_APPLICATIVI_ENDPOINT_RISPOSTA)) {
				int idSilInt = Integer.parseInt(idsil);
				ServizioApplicativo sa = this.saCore.getServizioApplicativo(idSilInt);
				RispostaAsincrona ra = sa.getRispostaAsincrona();
				org.openspcoop2.core.config.Connettore connettore = ra.getConnettore();
				for (int j = 0; j < connettore.sizePropertyList(); j++) {
					org.openspcoop2.core.config.Property tmpProp = connettore.getProperty(j);
					if (tmpProp.getNome().equals(nome)) {
						if(CostantiDB.CONNETTORE_DEBUG.equals(nome)){
							giaRegistratoProprietaDebug = true;
						}else{
							giaRegistratoProprietaNormale = true;
						}
						break;
					}
				}
			}
			if (servlet.equals(SoggettiCostanti.SERVLET_NAME_SOGGETTI_ENDPOINT)) {
				int idInt = Integer.parseInt(id);
				SoggettoCtrlStat scs = this.soggettiCore.getSoggettoCtrlStat(idInt);
				Soggetto ss = scs.getSoggettoReg();
				org.openspcoop2.core.registry.Connettore connettore = ss.getConnettore();
				for (int j = 0; j < connettore.sizePropertyList(); j++) {
					Property tmpProp = connettore.getProperty(j);
					if (tmpProp.getNome().equals(nome)) {
						if(CostantiDB.CONNETTORE_DEBUG.equals(nome)){
							giaRegistratoProprietaDebug = true;
						}else{
							giaRegistratoProprietaNormale = true;
						}
						break;
					}
				}
			}

			if (giaRegistratoProprietaNormale) {
				this.pd.setMessage("La propriet&agrave; '" + nome + "' &egrave; gi&agrave; stata associata al connettore");
				return false;
			}
			if (giaRegistratoProprietaDebug) {
				this.pd.setMessage("La keyword '" + nome + "' non &egrave; associabile come nome ad una propriet&agrave; del connettore");
				return false;
			}

			return true;
		} catch (Exception e) {
			this.log.error("Exception: " + e.getMessage(), e);
			throw new Exception(e);
		}
	}

	public String readEndPointType() throws Exception{
		String endpointtype = this.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_ENDPOINT_TYPE);
		String endpointtype_check = this.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_ENDPOINT_TYPE_CHECK);
		String endpointtype_ssl = this.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_ENDPOINT_TYPE_ENABLE_HTTPS);
		return this.readEndPointType(endpointtype, endpointtype_check, endpointtype_ssl);
	}
	public String readEndPointType(String endpointtype,String endpointtype_check,String endpointtype_ssl){
				
		TipologiaConnettori tipologiaConnettori = null;
		try {
			tipologiaConnettori = Utilities.getTipologiaConnettori(this.core);
		} catch (Exception e) {
			// default
			tipologiaConnettori = TipologiaConnettori.TIPOLOGIA_CONNETTORI_ALL;
		}
		
		if(endpointtype_check!=null && !"".equals(endpointtype_check)){
			if (TipologiaConnettori.TIPOLOGIA_CONNETTORI_HTTP.equals(tipologiaConnettori)) {
				if(ServletUtils.isCheckBoxEnabled(endpointtype_check)){
					if(ServletUtils.isCheckBoxEnabled(endpointtype_ssl)){
						endpointtype = TipiConnettore.HTTPS.toString();
					}
					else{
						endpointtype = TipiConnettore.HTTP.toString();
					}
				}
				else{
					endpointtype = TipiConnettore.DISABILITATO.toString();
				}
			}
		}
		return endpointtype;
	}
	
	public Vector<DataElement> addOpzioniAvanzateHttpToDati(Vector<DataElement> dati,
			String opzioniAvanzate, String transfer_mode, String transfer_mode_chunk_size, String redirect_mode, String redirect_max_hop){
		
		boolean showOpzioniAvanzate = this.isModalitaAvanzata()
				&& ServletUtils.isCheckBoxEnabled(opzioniAvanzate);
		
		if(showOpzioniAvanzate){
			DataElement de = new DataElement();
			de.setLabel(ConnettoriCostanti.LABEL_CONNETTORE_OPZIONI_AVANZATE);
			de.setType(DataElementType.TITLE);
			dati.addElement(de);
		}
		else{
			if(this.isModalitaAvanzata() &&
					!ServletUtils.isCheckBoxEnabled(opzioniAvanzate)){
				transfer_mode=null;
				transfer_mode_chunk_size=null;
				redirect_mode=null;
				redirect_max_hop=null;
			}
		}
		
		// DataTransferMode
		DataElement de = new DataElement();
		de.setName(ConnettoriCostanti.PARAMETRO_CONNETTORE_OPZIONI_AVANZATE_TRANSFER_MODE);
		de.setLabel(ConnettoriCostanti.LABEL_PARAMETRO_CONNETTORE_OPZIONI_AVANZATE_TRANSFER_MODE);
		if(showOpzioniAvanzate){
			if(transfer_mode==null || "".equals(transfer_mode)){
				transfer_mode = ConnettoriCostanti.DEFAULT_TIPO_DATA_TRANSFER;
			}
			de.setType(DataElementType.SELECT);
			de.setValues(ConnettoriCostanti.TIPI_DATA_TRANSFER);
			de.setPostBack(true);
			de.setSelected(transfer_mode);
			de.setSize(this.getSize());
		}
		else{
			de.setType(DataElementType.HIDDEN);
		}
		de.setValue(transfer_mode);
		dati.addElement(de);
		
		de = new DataElement();
		de.setName(ConnettoriCostanti.PARAMETRO_CONNETTORE_OPZIONI_AVANZATE_TRANSFER_CHUNK_SIZE);
		de.setLabel(ConnettoriCostanti.LABEL_PARAMETRO_CONNETTORE_OPZIONI_AVANZATE_TRANSFER_CHUNK_SIZE);
		if(showOpzioniAvanzate && TransferLengthModes.TRANSFER_ENCODING_CHUNKED.getNome().equals(transfer_mode)){
			de.setType(DataElementType.TEXT_EDIT);
			de.setSize(this.getSize());
		}
		else{
			de.setType(DataElementType.HIDDEN);
		}
		de.setValue(transfer_mode_chunk_size);
		dati.addElement(de);
		
		
		// Redirect
		de = new DataElement();
		de.setName(ConnettoriCostanti.PARAMETRO_CONNETTORE_OPZIONI_AVANZATE_REDIRECT_MODE);
		de.setLabel(ConnettoriCostanti.LABEL_PARAMETRO_CONNETTORE_OPZIONI_AVANZATE_REDIRECT_MODE);
		if(showOpzioniAvanzate){
			if(redirect_mode==null || "".equals(redirect_mode)){
				redirect_mode = ConnettoriCostanti.DEFAULT_GESTIONE_REDIRECT;
			}
			de.setType(DataElementType.SELECT);
			de.setValues(ConnettoriCostanti.TIPI_GESTIONE_REDIRECT);
			de.setPostBack(true);
			de.setSelected(redirect_mode);
			de.setSize(this.getSize());
		}
		else{
			de.setType(DataElementType.HIDDEN);
		}
		de.setValue(redirect_mode);
		dati.addElement(de);
		
		de = new DataElement();
		de.setName(ConnettoriCostanti.PARAMETRO_CONNETTORE_OPZIONI_AVANZATE_REDIRECT_MAX_HOP);
		de.setLabel(ConnettoriCostanti.LABEL_PARAMETRO_CONNETTORE_OPZIONI_AVANZATE_REDIRECT_MAX_HOP);
		if(showOpzioniAvanzate && CostantiConfigurazione.ABILITATO.getValue().equals(redirect_mode)){
			de.setType(DataElementType.TEXT_EDIT);
			de.setSize(this.getSize());
		}
		else{
			de.setType(DataElementType.HIDDEN);
		}
		de.setValue(redirect_max_hop);
		dati.addElement(de);
		
		return dati;
	}
	
	public Vector<DataElement> addOpzioniAvanzateHttpToDatiAsHidden(Vector<DataElement> dati,
			String opzioniAvanzate, String transfer_mode, String transfer_mode_chunk_size, String redirect_mode, String redirect_max_hop){
		
		boolean showOpzioniAvanzate = this.isModalitaAvanzata()
				&& ServletUtils.isCheckBoxEnabled(opzioniAvanzate);
		
		if(!showOpzioniAvanzate){
			if(this.isModalitaAvanzata() &&
					!ServletUtils.isCheckBoxEnabled(opzioniAvanzate)){
				transfer_mode=null;
				transfer_mode_chunk_size=null;
				redirect_mode=null;
				redirect_max_hop=null;
			}
		}
		
		// DataTransferMode
		DataElement de = new DataElement();
		de.setName(ConnettoriCostanti.PARAMETRO_CONNETTORE_OPZIONI_AVANZATE_TRANSFER_MODE);
		de.setType(DataElementType.HIDDEN);
		de.setValue(transfer_mode);
		dati.addElement(de);
		
		de = new DataElement();
		de.setName(ConnettoriCostanti.PARAMETRO_CONNETTORE_OPZIONI_AVANZATE_TRANSFER_CHUNK_SIZE);
		de.setType(DataElementType.HIDDEN);
		de.setValue(transfer_mode_chunk_size);
		dati.addElement(de);
		
		
		// Redirect
		de = new DataElement();
		de.setName(ConnettoriCostanti.PARAMETRO_CONNETTORE_OPZIONI_AVANZATE_REDIRECT_MODE);
		de.setType(DataElementType.HIDDEN);
		de.setValue(redirect_mode);
		dati.addElement(de);
		
		de = new DataElement();
		de.setName(ConnettoriCostanti.PARAMETRO_CONNETTORE_OPZIONI_AVANZATE_REDIRECT_MAX_HOP);
		de.setType(DataElementType.HIDDEN);
		de.setValue(redirect_max_hop);
		dati.addElement(de);
		
		return dati;
	}
	
	public Vector<DataElement> addProxyToDati(Vector<DataElement> dati,
			String proxyHostname, String proxyPort, String proxyUsername, String proxyPassword){
		
		DataElement de = new DataElement();
		de.setLabel(ConnettoriCostanti.LABEL_CONNETTORE_PROXY);
		de.setType(DataElementType.TITLE);
		dati.addElement(de);
		
		de = new DataElement();
		de.setLabel(ConnettoriCostanti.LABEL_PARAMETRO_CONNETTORE_PROXY_HOSTNAME);
		de.setValue(proxyHostname);
		de.setType(DataElementType.TEXT_EDIT);
		de.setName(ConnettoriCostanti.PARAMETRO_CONNETTORE_PROXY_HOSTNAME);
		de.setSize(this.getSize());
		de.setRequired(true);
		dati.addElement(de);
		
		de = new DataElement();
		de.setLabel(ConnettoriCostanti.LABEL_PARAMETRO_CONNETTORE_PROXY_PORT);
		de.setValue(proxyPort);
		de.setType(DataElementType.TEXT_EDIT);
		de.setName(ConnettoriCostanti.PARAMETRO_CONNETTORE_PROXY_PORT);
		de.setSize(this.getSize());
		de.setRequired(true);
		dati.addElement(de);
		
		de = new DataElement();
		de.setLabel(ConnettoriCostanti.LABEL_PARAMETRO_CONNETTORE_PROXY_USERNAME);
		de.setValue(StringEscapeUtils.escapeHtml(proxyUsername));
		de.setType(DataElementType.TEXT_EDIT);
		de.setName(ConnettoriCostanti.PARAMETRO_CONNETTORE_PROXY_USERNAME);
		de.setSize(this.getSize());
		de.setRequired(false);
		dati.addElement(de);
		
		de = new DataElement();
		de.setLabel(ConnettoriCostanti.LABEL_PARAMETRO_CONNETTORE_PROXY_PASSWORD);
		de.setValue(StringEscapeUtils.escapeHtml(proxyPassword));
		de.setType(DataElementType.TEXT_EDIT);
		de.setName(ConnettoriCostanti.PARAMETRO_CONNETTORE_PROXY_PASSWORD);
		de.setSize(this.getSize());
		de.setRequired(false);
		dati.addElement(de);
		
		return dati;
	}
	
	public Vector<DataElement> addProxyToDatiAsHidden(Vector<DataElement> dati,
			String proxyEnabled, String proxyHostname, String proxyPort, String proxyUsername, String proxyPassword){
		
		DataElement de = new DataElement();
		de.setValue(proxyEnabled);
		de.setType(DataElementType.HIDDEN);
		de.setName(ConnettoriCostanti.PARAMETRO_CONNETTORE_PROXY_ENABLED);
		dati.addElement(de);
		
		de = new DataElement();
		de.setValue(proxyHostname);
		de.setType(DataElementType.HIDDEN);
		de.setName(ConnettoriCostanti.PARAMETRO_CONNETTORE_PROXY_HOSTNAME);
		dati.addElement(de);
		
		de = new DataElement();
		de.setValue(proxyPort);
		de.setType(DataElementType.HIDDEN);
		de.setName(ConnettoriCostanti.PARAMETRO_CONNETTORE_PROXY_PORT);
		dati.addElement(de);
		
		de = new DataElement();
		de.setValue(StringEscapeUtils.escapeHtml(proxyUsername));
		de.setType(DataElementType.HIDDEN);
		de.setName(ConnettoriCostanti.PARAMETRO_CONNETTORE_PROXY_USERNAME);
		dati.addElement(de);
		
		de = new DataElement();
		de.setValue(StringEscapeUtils.escapeHtml(proxyPassword));
		de.setType(DataElementType.HIDDEN);
		de.setName(ConnettoriCostanti.PARAMETRO_CONNETTORE_PROXY_PASSWORD);
		dati.addElement(de);
		
		return dati;
	}
	
	public Vector<DataElement> addTokenPolicy(Vector<DataElement> dati, String tokenPolicy) throws DriverConfigurazioneException{
	
		List<GenericProperties> gestorePolicyTokenList = this.confCore.gestorePolicyTokenList(null, ConfigurazioneCostanti.DEFAULT_VALUE_PARAMETRO_CONFIGURAZIONE_GESTORE_POLICY_TOKEN_TIPOLOGIA_RETRIEVE_POLICY_TOKEN, null);
		String [] policyLabels = new String[gestorePolicyTokenList.size() + 1];
		String [] policyValues = new String[gestorePolicyTokenList.size() + 1];
		
		policyLabels[0] = CostantiControlStation.DEFAULT_VALUE_NON_SELEZIONATO;
		policyValues[0] = CostantiControlStation.DEFAULT_VALUE_NON_SELEZIONATO;
		
		for (int i = 0; i < gestorePolicyTokenList.size(); i++) {
			GenericProperties genericProperties = gestorePolicyTokenList.get(i);
			policyLabels[(i+1)] = genericProperties.getNome();
			policyValues[(i+1)] = genericProperties.getNome();
		}
		
		DataElement de = new DataElement();
		de.setLabel(ConnettoriCostanti.LABEL_CONNETTORE_BEARER);
		de.setType(DataElementType.TITLE);
		dati.addElement(de);
		
		de = new DataElement();
		de.setLabel(ConnettoriCostanti.LABEL_PARAMETRO_CONNETTORE_TOKEN_POLICY);
		de.setType(DataElementType.SELECT);
		de.setName(ConnettoriCostanti.PARAMETRO_CONNETTORE_TOKEN_POLICY);
		de.setValues(policyValues);
		de.setValues(policyLabels);
		de.setSelected(tokenPolicy);
		de.setRequired(true);
		de.setPostBack(true);
		dati.addElement(de);
		
		return dati;
	}
	
	public boolean isTokenPolicyMode_useAuthorizationHeader(String tokenPolicy) throws DriverConfigurazioneException, DriverConfigurazioneNotFound{
		if(tokenPolicy==null || "".equals(tokenPolicy) || CostantiControlStation.DEFAULT_VALUE_NON_SELEZIONATO.equals(tokenPolicy)) {
			return false;
		}
		GenericProperties gestorePolicyToken = this.confCore.getGenericProperties(tokenPolicy,  ConfigurazioneCostanti.DEFAULT_VALUE_PARAMETRO_CONFIGURAZIONE_GESTORE_POLICY_TOKEN_TIPOLOGIA_RETRIEVE_POLICY_TOKEN);
		for (org.openspcoop2.core.config.Property p : gestorePolicyToken.getPropertyList()) {
			if(org.openspcoop2.pdd.core.token.Costanti.POLICY_RETRIEVE_TOKEN_FORWARD_MODE.equals(p.getNome())) {
				if(org.openspcoop2.pdd.core.token.Costanti.POLICY_RETRIEVE_TOKEN_FORWARD_MODE_RFC6750_HEADER.equals(p.getValore())) {
					return true;
				}
			}
		}	
		return false;
	}
	
	public Vector<DataElement> addTempiRispostaToDati(Vector<DataElement> dati,
			String tempiRisposta_connectionTimeout, String tempiRisposta_readTimeout, String tempiRisposta_tempoMedioRisposta){
		
		DataElement de = new DataElement();
		de.setLabel(ConnettoriCostanti.LABEL_CONNETTORE_TEMPI_RISPOSTA);
		de.setType(DataElementType.TITLE);
		dati.addElement(de);
		
		de = new DataElement();
		de.setLabel(ConnettoriCostanti.LABEL_PARAMETRO_CONNETTORE_TEMPI_RISPOSTA_CONNECTION_TIMEOUT);
		de.setValue(tempiRisposta_connectionTimeout);
		de.setType(DataElementType.TEXT_EDIT);
		de.setName(ConnettoriCostanti.PARAMETRO_CONNETTORE_TEMPI_RISPOSTA_CONNECTION_TIMEOUT);
		de.setSize(this.getSize());
		de.setRequired(true);
		dati.addElement(de);
		
		de = new DataElement();
		de.setLabel(ConnettoriCostanti.LABEL_PARAMETRO_CONNETTORE_TEMPI_RISPOSTA_READ_TIMEOUT);
		de.setValue(tempiRisposta_readTimeout);
		de.setType(DataElementType.TEXT_EDIT);
		de.setName(ConnettoriCostanti.PARAMETRO_CONNETTORE_TEMPI_RISPOSTA_READ_TIMEOUT);
		de.setSize(this.getSize());
		de.setRequired(true);
		dati.addElement(de);
		
		de = new DataElement();
		de.setLabel(ConnettoriCostanti.LABEL_PARAMETRO_CONNETTORE_TEMPI_RISPOSTA_TEMPO_MEDIO_RISPOSTA);
		de.setValue(tempiRisposta_tempoMedioRisposta);
		de.setType(DataElementType.TEXT_EDIT);
		de.setName(ConnettoriCostanti.PARAMETRO_CONNETTORE_TEMPI_RISPOSTA_TEMPO_MEDIO_RISPOSTA);
		de.setSize(this.getSize());
		de.setRequired(true);
		dati.addElement(de);
		
		return dati;
	}
	
	public Vector<DataElement> addTempiRispostaToDatiAsHidden(Vector<DataElement> dati,
			String tempiRisposta_enabled, String tempiRisposta_connectionTimeout, String tempiRisposta_readTimeout, String tempiRisposta_tempoMedioRisposta){
		
		DataElement de = new DataElement();
		de.setValue(tempiRisposta_enabled);
		de.setType(DataElementType.HIDDEN);
		de.setName(ConnettoriCostanti.PARAMETRO_CONNETTORE_TEMPI_RISPOSTA_REDEFINE);
		dati.addElement(de);
		
		de = new DataElement();
		de.setValue(tempiRisposta_connectionTimeout);
		de.setType(DataElementType.HIDDEN);
		de.setName(ConnettoriCostanti.PARAMETRO_CONNETTORE_TEMPI_RISPOSTA_CONNECTION_TIMEOUT);
		dati.addElement(de);
		
		de = new DataElement();
		de.setValue(tempiRisposta_readTimeout);
		de.setType(DataElementType.HIDDEN);
		de.setName(ConnettoriCostanti.PARAMETRO_CONNETTORE_TEMPI_RISPOSTA_READ_TIMEOUT);
		dati.addElement(de);
		
		de = new DataElement();
		de.setValue(tempiRisposta_tempoMedioRisposta);
		de.setType(DataElementType.HIDDEN);
		de.setName(ConnettoriCostanti.PARAMETRO_CONNETTORE_TEMPI_RISPOSTA_TEMPO_MEDIO_RISPOSTA);
		dati.addElement(de);
		
		return dati;
	}
	
	
	public Vector<DataElement> addCredenzialiToDati(Vector<DataElement> dati, String tipoauth, String utente, String password, String subject, String principal,
			String toCall, boolean showLabelCredenzialiAccesso, String endpointtype,boolean connettore,boolean visualizzaTipoAutenticazione,
			String prefix, boolean autenticazioneNessunaAbilitata) throws Exception {
		return this.addCredenzialiToDati(dati, tipoauth, null, utente, password, subject, principal, toCall, showLabelCredenzialiAccesso, endpointtype, connettore, visualizzaTipoAutenticazione, prefix, autenticazioneNessunaAbilitata, 
				null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,
				null,
				null,null,null,
				null);
	}
	public Vector<DataElement> addCredenzialiToDati(Vector<DataElement> dati, String tipoauth, String utente, String password, String subject, String principal,
			String toCall, boolean showLabelCredenzialiAccesso, String endpointtype,boolean connettore,boolean visualizzaTipoAutenticazione,
			String prefix, boolean autenticazioneNessunaAbilitata,
			String subtitleConfigurazione) throws Exception {
		return this.addCredenzialiToDati(dati, tipoauth, null, utente, password, subject, principal, toCall, showLabelCredenzialiAccesso, endpointtype, connettore, visualizzaTipoAutenticazione, prefix, autenticazioneNessunaAbilitata, 
				null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,
				null,
				null,null,null,
				subtitleConfigurazione);
	} 
	
	public Vector<DataElement> addCredenzialiToDati(Vector<DataElement> dati, String tipoauth, String oldtipoauth, String utente, String password, String subject, String principal,
			String toCall, boolean showLabelCredenzialiAccesso, String endpointtype,boolean connettore,boolean visualizzaTipoAutenticazione,
			String prefix, boolean autenticazioneNessunaAbilitata,String tipoCredenzialiSSLSorgente, ArchiveType tipoCredenzialiSSLTipoArchivio, BinaryParameter tipoCredenzialiSSLFileCertificato, String tipoCredenzialiSSLFileCertificatoPassword,
			List<String> listaAliasEstrattiCertificato, String tipoCredenzialiSSLAliasCertificato,	String tipoCredenzialiSSLAliasCertificatoSubject, String tipoCredenzialiSSLAliasCertificatoIssuer,
			String tipoCredenzialiSSLAliasCertificatoType, String tipoCredenzialiSSLAliasCertificatoVersion, String tipoCredenzialiSSLAliasCertificatoSerialNumber, String tipoCredenzialiSSLAliasCertificatoSelfSigned,
			String tipoCredenzialiSSLAliasCertificatoNotBefore, String tipoCredenzialiSSLAliasCertificatoNotAfter, String tipoCredenzialiSSLVerificaTuttiICampi, String tipoCredenzialiSSLConfigurazioneManualeSelfSigned,
			String issuer, String tipoCredenzialiSSLStatoElaborazioneCertificato,
			String changepwd, 
			String multipleApiKey, String appId, String apiKey) throws Exception{
		return addCredenzialiToDati(dati, tipoauth, oldtipoauth, utente, password, subject, principal,
				toCall, showLabelCredenzialiAccesso, endpointtype,connettore,visualizzaTipoAutenticazione,
				prefix, autenticazioneNessunaAbilitata,tipoCredenzialiSSLSorgente, tipoCredenzialiSSLTipoArchivio, tipoCredenzialiSSLFileCertificato, tipoCredenzialiSSLFileCertificatoPassword,
				listaAliasEstrattiCertificato, tipoCredenzialiSSLAliasCertificato,tipoCredenzialiSSLAliasCertificatoSubject, tipoCredenzialiSSLAliasCertificatoIssuer,
				tipoCredenzialiSSLAliasCertificatoType, tipoCredenzialiSSLAliasCertificatoVersion, tipoCredenzialiSSLAliasCertificatoSerialNumber, tipoCredenzialiSSLAliasCertificatoSelfSigned,
				tipoCredenzialiSSLAliasCertificatoNotBefore, tipoCredenzialiSSLAliasCertificatoNotAfter, tipoCredenzialiSSLVerificaTuttiICampi, tipoCredenzialiSSLConfigurazioneManualeSelfSigned,
				issuer, tipoCredenzialiSSLStatoElaborazioneCertificato,
				changepwd,
				multipleApiKey, appId, apiKey,
				null);
	}
	public Vector<DataElement> addCredenzialiToDati(Vector<DataElement> dati, String tipoauth, String oldtipoauth, String utente, String password, String subject, String principal,
			String toCall, boolean showLabelCredenzialiAccesso, String endpointtype,boolean connettore,boolean visualizzaTipoAutenticazione,
			String prefix, boolean autenticazioneNessunaAbilitata,String tipoCredenzialiSSLSorgente, ArchiveType tipoCredenzialiSSLTipoArchivio, BinaryParameter tipoCredenzialiSSLFileCertificato, String tipoCredenzialiSSLFileCertificatoPassword,
			List<String> listaAliasEstrattiCertificato, String tipoCredenzialiSSLAliasCertificato,	String tipoCredenzialiSSLAliasCertificatoSubject, String tipoCredenzialiSSLAliasCertificatoIssuer,
			String tipoCredenzialiSSLAliasCertificatoType, String tipoCredenzialiSSLAliasCertificatoVersion, String tipoCredenzialiSSLAliasCertificatoSerialNumber, String tipoCredenzialiSSLAliasCertificatoSelfSigned,
			String tipoCredenzialiSSLAliasCertificatoNotBefore, String tipoCredenzialiSSLAliasCertificatoNotAfter, String tipoCredenzialiSSLVerificaTuttiICampi, String tipoCredenzialiSSLConfigurazioneManualeSelfSigned,
			String issuer, String tipoCredenzialiSSLStatoElaborazioneCertificato,
			String changepwd, 
			String multipleApiKey, String appId, String apiKey,
			String subtitleConfigurazione) throws Exception{

		if(subtitleConfigurazione==null) {
			subtitleConfigurazione = ConnettoriCostanti.LABEL_CONFIGURAZIONE_SSL_TITLE_CONFIGURAZIONE;
		}
		
		DataElement de = null;

		if(prefix==null){
			prefix = "";
		}

		String[] tipoA = null;
		String[] labelTipoA = null;
		if(visualizzaTipoAutenticazione){
			if (ServiziApplicativiCostanti.SERVLET_NAME_SERVIZI_APPLICATIVI_ENDPOINT.equals(toCall) || 
					ServiziApplicativiCostanti.SERVLET_NAME_SERVIZI_APPLICATIVI_ENDPOINT_RISPOSTA.equals(toCall)) {
				if(TipiConnettore.HTTPS.toString().equals(endpointtype) || TipiConnettore.HTTP.toString().equals(endpointtype) || 
						TipiConnettore.JMS.toString().equals(endpointtype) || TipiConnettore.CUSTOM.toString().equals(endpointtype)){
					tipoA = new String[] { ConnettoriCostanti.AUTENTICAZIONE_TIPO_NESSUNA, 
							ConnettoriCostanti.AUTENTICAZIONE_TIPO_BASIC };
					if(TipiConnettore.HTTPS.toString().equals(endpointtype) || TipiConnettore.HTTP.toString().equals(endpointtype) ){
						labelTipoA = new String[] { CostantiConfigurazione.DISABILITATO.toString(), 
								ConnettoriCostanti.AUTENTICAZIONE_TIPO_BASIC };
					}else{
						labelTipoA = new String[] { CostantiConfigurazione.DISABILITATO.toString(), 
								CostantiConfigurazione.ABILITATO.toString() };
					}
				}else{
					tipoA = new String[] { ConnettoriCostanti.AUTENTICAZIONE_TIPO_NESSUNA };
					labelTipoA = new String[] { CostantiConfigurazione.DISABILITATO.toString() };
					tipoauth = ConnettoriCostanti.AUTENTICAZIONE_TIPO_NESSUNA;
				}
			} else {
				boolean autenticazioneNessuna = autenticazioneNessunaAbilitata;
				if (! (SoggettiCostanti.SERVLET_NAME_SOGGETTI_ADD.equals(toCall) || 
						SoggettiCostanti.SERVLET_NAME_SOGGETTI_CHANGE.equals(toCall)) ) {
					boolean creazione_modifica_SA = ServiziApplicativiCostanti.SERVLET_NAME_SERVIZI_APPLICATIVI_ADD.equals(toCall) || 
							ServiziApplicativiCostanti.SERVLET_NAME_SERVIZI_APPLICATIVI_CHANGE.equals(toCall);
					if (this.isModalitaStandard() || (!this.isModalitaCompleta() && creazione_modifica_SA)){
						autenticazioneNessuna = false;
					}
				}
				
				if (autenticazioneNessuna) {
					tipoA = ConnettoriCostanti.CREDENZIALI_CON_NESSUNA_VALUES;
					labelTipoA = ConnettoriCostanti.CREDENZIALI_CON_NESSUNA_LABELS;
				}
				else{
					tipoA = ConnettoriCostanti.CREDENZIALI_VALUES;
					labelTipoA = ConnettoriCostanti.CREDENZIALI_LABELS;
					if(tipoauth==null){
						tipoauth = ConnettoriCostanti.AUTENTICAZIONE_TIPO_SSL;
					}
				}
			}
		}

		boolean showSezioneCredenziali = true;
		if(visualizzaTipoAutenticazione==false){
			showSezioneCredenziali = true;
		}
		else if(tipoA.length == 1){
			showSezioneCredenziali = false;
		}

		if(showSezioneCredenziali) {
			if(showLabelCredenzialiAccesso){
				de = new DataElement();
				if(connettore){
					if(TipiConnettore.HTTPS.toString().equals(endpointtype) ||  TipiConnettore.HTTP.toString().equals(endpointtype) ){
						de.setLabel(prefix+ServiziApplicativiCostanti.LABEL_CREDENZIALI_ACCESSO_SERVIZIO_APPLICATIVO_HTTP);
					}
					else{
						de.setLabel(prefix+ServiziApplicativiCostanti.LABEL_CREDENZIALI_ACCESSO_SERVIZIO_APPLICATIVO);
					}
				}else{
					de.setLabel(prefix+ServiziApplicativiCostanti.LABEL_CREDENZIALI_ACCESSO_PORTA);
				}
				de.setType(DataElementType.TITLE);
				dati.addElement(de);
			}

			de = new DataElement();
			if(showLabelCredenzialiAccesso){
				de.setLabel(ServiziApplicativiCostanti.LABEL_TIPO_CREDENZIALE);
			}else{
				de.setLabel(ServiziApplicativiCostanti.LABEL_CREDENZIALE_ACCESSO);
			}
			if(connettore){
				de.setName(ConnettoriCostanti.PARAMETRO_INVOCAZIONE_CREDENZIALI_TIPO_AUTENTICAZIONE);
			}
			else{
				de.setName(ConnettoriCostanti.PARAMETRO_CREDENZIALI_TIPO_AUTENTICAZIONE);
			}
			if(visualizzaTipoAutenticazione){
				de.setType(DataElementType.SELECT);
				de.setLabels(labelTipoA);
				de.setValues(tipoA);
				de.setSelected(tipoauth);
				//		de.setOnChange("CambiaAuth('" + toCall + "')");
				de.setPostBack(true);
			}
			else{
				de.setType(DataElementType.HIDDEN);
				de.setValue(tipoauth);
			}
			dati.addElement(de);
			
			if (ConnettoriCostanti.AUTENTICAZIONE_TIPO_BASIC.equals(tipoauth)) {
				de = new DataElement();
				de.setLabel(ConnettoriCostanti.LABEL_PARAMETRO_CREDENZIALI_AUTENTICAZIONE_USERNAME);
				de.setValue(StringEscapeUtils.escapeHtml(utente));
				de.setType(DataElementType.TEXT_EDIT);
				if(connettore){
					de.setName(ConnettoriCostanti.PARAMETRO_INVOCAZIONE_CREDENZIALI_AUTENTICAZIONE_USERNAME);
				}
				else{
					de.setName(ConnettoriCostanti.PARAMETRO_CREDENZIALI_AUTENTICAZIONE_USERNAME);
				}
				de.setSize(this.getSize());
				de.setRequired(true);
				dati.addElement(de);

				
				
				de = new DataElement();
				de.setLabel(ConnettoriCostanti.LABEL_PARAMETRO_CREDENZIALI_AUTENTICAZIONE_PASSWORD);
				de.setValue(StringEscapeUtils.escapeHtml(password));
				// de.setType("crypt");
				de.setType(DataElementType.TEXT_EDIT);
				if(connettore){
					de.setName(ConnettoriCostanti.PARAMETRO_INVOCAZIONE_CREDENZIALI_AUTENTICAZIONE_PASSWORD);
				}
				else{
					
					boolean change = ServiziApplicativiCostanti.SERVLET_NAME_SERVIZI_APPLICATIVI_CHANGE.equals(toCall) || 
							SoggettiCostanti.SERVLET_NAME_SOGGETTI_CHANGE.equals(toCall);
					boolean soggetti = SoggettiCostanti.SERVLET_NAME_SOGGETTI_ADD.equals(toCall) || 
							SoggettiCostanti.SERVLET_NAME_SOGGETTI_CHANGE.equals(toCall);
					
					boolean passwordCifrata = ServletUtils.isCheckBoxEnabled(tipoCredenzialiSSLVerificaTuttiICampi); // tipoCredenzialiSSLVerificaTuttiICampi usata come informazione per sapere se una password e' cifrata o meno
					
					if(change && passwordCifrata ){
						DataElement deCifratura = new DataElement();
						deCifratura.setName(ConnettoriCostanti.PARAMETRO_CREDENZIALI_AUTENTICAZIONE_CONFIGURAZIONE_SSL_VERIFICA_TUTTI_CAMPI);
						deCifratura.setType(DataElementType.HIDDEN);
						deCifratura.setValue(tipoCredenzialiSSLVerificaTuttiICampi);
						dati.add(deCifratura);
					}
					
					// solo adesso posso reimpostare il change
					if(change) {
						if(!tipoauth.equals(oldtipoauth)) {
							change = false;
						}
					}
										
					if(change && passwordCifrata ){
						DataElement deModifica = new DataElement();
						deModifica.setLabel(ConnettoriCostanti.LABEL_PARAMETRO_CREDENZIALI_AUTENTICAZIONE_MODIFICA_PASSWORD);
						deModifica.setType(DataElementType.CHECKBOX);
						deModifica.setName(ConnettoriCostanti.PARAMETRO_CREDENZIALI_AUTENTICAZIONE_CHANGE_PASSWORD);
						deModifica.setPostBack(true);
						deModifica.setSelected(changepwd);
						deModifica.setSize(this.getSize());
						dati.addElement(deModifica);
						
						de.setType(DataElementType.HIDDEN);
						de.setName(ConnettoriCostanti.PARAMETRO_CREDENZIALI_AUTENTICAZIONE_PASSWORD);
					}
					
					if( (!change) || (!passwordCifrata) || (ServletUtils.isCheckBoxEnabled(changepwd)) ){
					
						if(change && passwordCifrata && ServletUtils.isCheckBoxEnabled(changepwd) ){
							de.setValue(null); // non faccio vedere una password cifrata
							de.setLabel(ConnettoriCostanti.LABEL_PARAMETRO_CREDENZIALI_AUTENTICAZIONE_NUOVA_PASSWORD);
						}
						
						// Nuova visualizzazione Password con bottone genera password
						de.setType(DataElementType.CRYPT);
						de.getPassword().setVisualizzaPasswordChiaro(true);
						de.getPassword().setVisualizzaBottoneGeneraPassword(true);
						
						PasswordVerifier passwordVerifier = null;
						boolean isBasicPasswordEnableConstraints = false;
						int lunghezzaPasswordGenerate= 10;
						if ( soggetti ) {
							lunghezzaPasswordGenerate = this.connettoriCore.getSoggettiBasicLunghezzaPasswordGenerate();
							isBasicPasswordEnableConstraints = this.connettoriCore.isSoggettiBasicPasswordEnableConstraints();
							if(isBasicPasswordEnableConstraints) {
								passwordVerifier = this.connettoriCore.getSoggettiPasswordVerifier();
							}
						}
						else {
							lunghezzaPasswordGenerate = this.connettoriCore.getApplicativiBasicLunghezzaPasswordGenerate();
							isBasicPasswordEnableConstraints = this.connettoriCore.isApplicativiBasicPasswordEnableConstraints();
							if(isBasicPasswordEnableConstraints) {
								passwordVerifier = this.connettoriCore.getApplicativiPasswordVerifier();
							}
						}
						if(passwordVerifier != null) {
							PasswordGenerator passwordGenerator = new PasswordGenerator(passwordVerifier);
							de.getPassword().setPasswordGenerator(passwordGenerator);
							de.setNote(passwordVerifier.help("<BR/>"));
						}
						de.getPassword().getPasswordGenerator().setDefaultLength(lunghezzaPasswordGenerate);
						
						de.setName(ConnettoriCostanti.PARAMETRO_CREDENZIALI_AUTENTICAZIONE_PASSWORD);
						
					}
				}
				de.setSize(this.getSize());
				de.setRequired(true);
				dati.addElement(de);

			}
			
			if (ConnettoriCostanti.AUTENTICAZIONE_TIPO_SSL.equals(tipoauth) && !connettore) {
				
				boolean add = ( SoggettiCostanti.SERVLET_NAME_SOGGETTI_ADD.equals(toCall) ||  ServiziApplicativiCostanti.SERVLET_NAME_SERVIZI_APPLICATIVI_ADD.equals(toCall));
				
				boolean visualizzaFieldCert = false;
				boolean visualizzaDownload = false;
				if(add) {
					visualizzaFieldCert = StringUtils.isEmpty(tipoCredenzialiSSLAliasCertificatoSubject);
				}
				else {
					visualizzaFieldCert = !tipoCredenzialiSSLStatoElaborazioneCertificato.equals(ConnettoriCostanti.VALUE_PARAMETRO_CREDENZIALI_AUTENTICAZIONE_CONFIGURAZIONE_SSL_NO_WIZARD)
							&& StringUtils.isEmpty(tipoCredenzialiSSLAliasCertificatoSubject);
					visualizzaDownload = tipoCredenzialiSSLStatoElaborazioneCertificato.equals(ConnettoriCostanti.VALUE_PARAMETRO_CREDENZIALI_AUTENTICAZIONE_CONFIGURAZIONE_SSL_NO_WIZARD);
				}
				
				

				/*
				String tipoCredenzialiSSLSorgente = ConnettoriCostanti.VALUE_PARAMETRO_CREDENZIALI_AUTENTICAZIONE_CONFIGURAZIONE_SSL_UPLOAD_CERTIFICATO;
				String tipoCredenzialiSSLTipoArchivio = ArchiveType.CER.name();
				BinaryParameter tipoCredenzialiSSLFileCertificato = new BinaryParameter();
				tipoCredenzialiSSLFileCertificato.setName(ConnettoriCostanti.PARAMETRO_CREDENZIALI_AUTENTICAZIONE_CONFIGURAZIONE_SSL_FILE_CERTIFICATO);
				List<String> listaAliasEstrattiCertificato = new ArrayList<String>();
				String tipoCredenzialiSSLFileCertificatoPassword = "";
				String tipoCredenzialiSSLAliasCertificato = "";
				String tipoCredenzialiSSLAliasCertificatoSubject= "";
				String tipoCredenzialiSSLAliasCertificatoIssuer= "";
				String tipoCredenzialiSSLAliasCertificatoType= "";
				String tipoCredenzialiSSLAliasCertificatoVersion= "";
				String tipoCredenzialiSSLAliasCertificatoSerialNumber= "";
				String tipoCredenzialiSSLAliasCertificatoSelfSigned= "";
				String tipoCredenzialiSSLAliasCertificatoNotBefore= "";
				String tipoCredenzialiSSLAliasCertificatoNotAfter = "";
				String tipoCredenzialiSSLVerificaTuttiICampi = "";
				String tipoCredenzialiSSLConfigurazioneManualeSelfSigned="";
				String issuer = "";
				*/
				
				boolean visualizzaSceltaConfigurazione = (visualizzaFieldCert || !tipoCredenzialiSSLSorgente.equals(ConnettoriCostanti.VALUE_PARAMETRO_CREDENZIALI_AUTENTICAZIONE_CONFIGURAZIONE_SSL_UPLOAD_CERTIFICATO)) &&
						!tipoCredenzialiSSLStatoElaborazioneCertificato.equals(ConnettoriCostanti.VALUE_PARAMETRO_CREDENZIALI_AUTENTICAZIONE_CONFIGURAZIONE_SSL_NO_WIZARD_ARCHIVI);
				
				de = new DataElement();
				de.setLabel(subtitleConfigurazione);
				de.setType(DataElementType.SUBTITLE);
				dati.addElement(de);
				
				// OP-816
				// 1. Select List - Upload Certificato / Configurazione Manuale
				de = new DataElement();
				de.setLabel(ConnettoriCostanti.LABEL_PARAMETRO_CREDENZIALI_AUTENTICAZIONE_CONFIGURAZIONE_SSL);
				de.setName(ConnettoriCostanti.PARAMETRO_CREDENZIALI_AUTENTICAZIONE_CONFIGURAZIONE_SSL);
				if(visualizzaSceltaConfigurazione) {
					de.setType(DataElementType.SELECT);
					de.setLabels(ConnettoriCostanti.PARAMETRO_CREDENZIALI_AUTENTICAZIONE_CONFIGURAZIONE_SSL_LABELS);
					de.setValues(ConnettoriCostanti.PARAMETRO_CREDENZIALI_AUTENTICAZIONE_CONFIGURAZIONE_SSL_VALUES);
					de.setSelected(tipoCredenzialiSSLSorgente);
					de.setPostBack(true);
					de.setSize(this.getSize());
				} else {
					de.setType(DataElementType.HIDDEN);
					de.setValue(tipoCredenzialiSSLSorgente);
				}
				dati.addElement(de);

				if(tipoCredenzialiSSLSorgente.equals(ConnettoriCostanti.VALUE_PARAMETRO_CREDENZIALI_AUTENTICAZIONE_CONFIGURAZIONE_SSL_UPLOAD_CERTIFICATO)) {
					// 1a. Select List Tipo Archivio
					de = new DataElement();
					de.setLabel(ConnettoriCostanti.LABEL_PARAMETRO_CREDENZIALI_AUTENTICAZIONE_CONFIGURAZIONE_SSL_TIPO_ARCHIVIO);
					de.setName(ConnettoriCostanti.PARAMETRO_CREDENZIALI_AUTENTICAZIONE_CONFIGURAZIONE_SSL_TIPO_ARCHIVIO);
					if(visualizzaFieldCert) {
						de.setType(DataElementType.SELECT);
						de.setLabels(ConnettoriCostanti.PARAMETRO_CREDENZIALI_AUTENTICAZIONE_CONFIGURAZIONE_SSL_TIPO_ARCHIVIO_LABELS);
						de.setValues(ConnettoriCostanti.PARAMETRO_CREDENZIALI_AUTENTICAZIONE_CONFIGURAZIONE_SSL_TIPO_ARCHIVIO_VALUES);
						de.setSelected(tipoCredenzialiSSLTipoArchivio.name());
						de.setSize(this.getSize());
						de.setPostBack(true);
						
						if(ArchiveType.CER.equals(tipoCredenzialiSSLTipoArchivio) || ArchiveType.JKS.equals(tipoCredenzialiSSLTipoArchivio)) {
							DataElementInfo dInfo = new DataElementInfo(ConnettoriCostanti.LABEL_PARAMETRO_CREDENZIALI_AUTENTICAZIONE_CONFIGURAZIONE_SSL_TIPO_ARCHIVIO);
							if(ArchiveType.CER.equals(tipoCredenzialiSSLTipoArchivio)) {
								dInfo.setHeaderBody(ConnettoriCostanti.LABEL_PARAMETRO_CREDENZIALI_AUTENTICAZIONE_CONFIGURAZIONE_SSL_TIPO_ARCHIVIO_INFO_CER);
								dInfo.setListBody(ConnettoriCostanti.LABEL_PARAMETRO_CREDENZIALI_AUTENTICAZIONE_CONFIGURAZIONE_SSL_TIPO_ARCHIVIO_INFO_CER_VALUES);
							}
							else {
								dInfo.setHeaderBody(ConnettoriCostanti.LABEL_PARAMETRO_CREDENZIALI_AUTENTICAZIONE_CONFIGURAZIONE_SSL_TIPO_ARCHIVIO_INFO_JKS);
							}
							de.setInfo(dInfo);
						}
						
					} else {
						de.setType(DataElementType.HIDDEN);
						de.setValue(tipoCredenzialiSSLTipoArchivio.name());
					}
					dati.addElement(de);
					
					de = new DataElement();
					de.setName(ConnettoriCostanti.PARAMETRO_CREDENZIALI_AUTENTICAZIONE_CONFIGURAZIONE_SSL_FILE_CERTIFICATO_PASSWORD);
					de.setLabel(ConnettoriCostanti.LABEL_PARAMETRO_CREDENZIALI_AUTENTICAZIONE_CONFIGURAZIONE_SSL_FILE_CERTIFICATO_PASSWORD);
					de.setValue(StringEscapeUtils.escapeHtml(tipoCredenzialiSSLFileCertificatoPassword));
					if(visualizzaFieldCert && (tipoCredenzialiSSLTipoArchivio.equals(ArchiveType.JKS) || tipoCredenzialiSSLTipoArchivio.equals(ArchiveType.PKCS12))) { 
						// 1a. Password per gli archivi JKS o PKCS12.
						de.setType(DataElementType.TEXT_EDIT);
						de.setSize(this.getSize());
						de.setRequired(true);
					} else {
						de.setType(DataElementType.HIDDEN);
					}
					
					dati.addElement(de);
					
					// 1a. File Upload
					String labelCertificato = null;
					if(ArchiveType.CER.equals(tipoCredenzialiSSLTipoArchivio)) {
						labelCertificato = ConnettoriCostanti.LABEL_PARAMETRO_CREDENZIALI_AUTENTICAZIONE_CONFIGURAZIONE_SSL_FILE_CERTIFICATO;
					}
					else {
						labelCertificato = ConnettoriCostanti.LABEL_PARAMETRO_CREDENZIALI_AUTENTICAZIONE_CONFIGURAZIONE_SSL_FILE_ARCHIVIO;
					}
					DataElement fileDataElement = tipoCredenzialiSSLFileCertificato.getFileDataElement(labelCertificato, "", getSize());
					fileDataElement.setRequired(true);
					fileDataElement.setPostBack(false); 
					
					if(!visualizzaFieldCert)
						fileDataElement.setType(DataElementType.HIDDEN);
					
					dati.add(fileDataElement);
					dati.addAll(tipoCredenzialiSSLFileCertificato.getFileNameDataElement());
					dati.add(tipoCredenzialiSSLFileCertificato.getFileIdDataElement());
										
					if(!visualizzaFieldCert) {
						de = new DataElement(); 
						de.setName(ConnettoriCostanti.PARAMETRO_CREDENZIALI_AUTENTICAZIONE_CONFIGURAZIONE_SSL_FILE_CERTIFICATO_LINK_MODIFICA);
						de.setValue(ConnettoriCostanti.LABEL_PARAMETRO_CREDENZIALI_AUTENTICAZIONE_CONFIGURAZIONE_SSL_FILE_CERTIFICATO_LINK_CAMBIA_+labelCertificato);
						de.setPostBack(true);
						// Imposto # per valorizzare la url, non viene utilizzata ma viene scatenata la post back
						de.setUrl("#");
						de.setType(DataElementType.LINK);
						dati.addElement(de);
					}
					
					// 1a. Eventuale Select per selezionare l'alias
					de = new DataElement();
					de.setLabel(ConnettoriCostanti.LABEL_PARAMETRO_CREDENZIALI_AUTENTICAZIONE_CONFIGURAZIONE_SSL_ALIAS_CERTIFICATO);
					de.setName(ConnettoriCostanti.PARAMETRO_CREDENZIALI_AUTENTICAZIONE_CONFIGURAZIONE_SSL_ALIAS_CERTIFICATO);
					if(listaAliasEstrattiCertificato.size() > 1) {
						
						de.setType(DataElementType.SELECT);
						
						List<String> listaLabels = new ArrayList<>();
						listaLabels.addAll(listaAliasEstrattiCertificato);
						listaLabels.add(0, "--");
						
						List<String> listaValues = new ArrayList<>();
						listaValues.addAll(listaAliasEstrattiCertificato);
						listaValues.add(0, "");
						
						de.setLabels(listaLabels);
						de.setValues(listaValues);
						de.setSelected(tipoCredenzialiSSLAliasCertificato);
						de.setPostBack(true);
						de.setSize(this.getSize());
						de.setRequired(true);
						
					} else {
						de.setType(DataElementType.HIDDEN);
						de.setValue(tipoCredenzialiSSLAliasCertificato);
					}
					dati.addElement(de);

					// 1a. Checkbox 'Verifica tutti i campi' + nota: attenzione questa opzione richiede l'aggiornamento del certificato a scadenza
					de = new DataElement();
					de.setName(ConnettoriCostanti.PARAMETRO_CREDENZIALI_AUTENTICAZIONE_CONFIGURAZIONE_SSL_VERIFICA_TUTTI_CAMPI);
					de.setLabel(ConnettoriCostanti.LABEL_PARAMETRO_CREDENZIALI_AUTENTICAZIONE_CONFIGURAZIONE_SSL_VERIFICA_TUTTI_CAMPI);
					if(StringUtils.isNotEmpty(tipoCredenzialiSSLAliasCertificatoSubject)) {
						de.setType(DataElementType.CHECKBOX);
						de.setNote(ConnettoriCostanti.NOTE_PARAMETRO_CREDENZIALI_AUTENTICAZIONE_CONFIGURAZIONE_SSL_VERIFICA_TUTTI_CAMPI);
						de.setSelected(ServletUtils.isCheckBoxEnabled(tipoCredenzialiSSLVerificaTuttiICampi));
						de.setSize(this.getSize());
					}else { 
						de.setType(DataElementType.HIDDEN);
						de.setValue(tipoCredenzialiSSLVerificaTuttiICampi);
					}
					dati.add(de);
					
					// 1a. Pannello Recap info certificato.
					
					if(StringUtils.isNotEmpty(tipoCredenzialiSSLAliasCertificatoSubject)) {
						de = new DataElement();
						de.setLabel(ConnettoriCostanti.LABEL_CONFIGURAZIONE_SSL_TITLE_INFORMAZIONI_CERTIFICATO+" "+
						tipoCredenzialiSSLAliasCertificatoType+" v"+tipoCredenzialiSSLAliasCertificatoVersion);
						de.setType(DataElementType.SUBTITLE);
						dati.addElement(de);
					}
					
					// link al download
					if(visualizzaDownload) {
						de = new DataElement(); 
						
						String idOggetto = null;
						String tipoOggetto = "";
								
						if(SoggettiCostanti.SERVLET_NAME_SOGGETTI_CHANGE.equals(toCall)) {
							tipoOggetto =  ArchiviCostanti.PARAMETRO_VALORE_ARCHIVI_ALLEGATO_TIPO_SOGGETTO;
							idOggetto = this.getParameter(SoggettiCostanti.PARAMETRO_SOGGETTO_ID);
						}
						
						if(ServiziApplicativiCostanti.SERVLET_NAME_SERVIZI_APPLICATIVI_CHANGE.equals(toCall)) {
							tipoOggetto =  ArchiviCostanti.PARAMETRO_VALORE_ARCHIVI_ALLEGATO_TIPO_SERVIZIO_APPLICATIVO;
							idOggetto = this.getParameter(ServiziApplicativiCostanti.PARAMETRO_SERVIZI_APPLICATIVI_ID);
						}
						
						de.setUrl(ArchiviCostanti.SERVLET_NAME_DOCUMENTI_EXPORT, 
								new Parameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_ALLEGATI_ID_ACCORDO, idOggetto),
								new Parameter(ArchiviCostanti.PARAMETRO_ARCHIVI_ALLEGATO_TIPO_ACCORDO, tipoOggetto),
								new Parameter(ArchiviCostanti.PARAMETRO_ARCHIVI_ALLEGATO_TIPO_ACCORDO_TIPO_DOCUMENTO, ArchiviCostanti.PARAMETRO_VALORE_ARCHIVI_ALLEGATO_TIPO_DOCUMENTO_CERTIFICATO_SSL));
						de.setValue(ConnettoriCostanti.LABEL_PARAMETRO_CREDENZIALI_AUTENTICAZIONE_CONFIGURAZIONE_SSL_FILE_CERTIFICATO_LINK_DOWNLOAD);
						de.setType(DataElementType.LINK);
						de.setDisabilitaAjaxStatus();
						dati.addElement(de);
					}
					
//						Subject:
					de = new DataElement();
					de.setName(ConnettoriCostanti.PARAMETRO_CREDENZIALI_AUTENTICAZIONE_CONFIGURAZIONE_SSL_ALIAS_CERTIFICATO_SUBJECT);
					de.setLabel(ConnettoriCostanti.LABEL_PARAMETRO_CREDENZIALI_AUTENTICAZIONE_CONFIGURAZIONE_SSL_ALIAS_CERTIFICATO_SUBJECT);
					de.setValue(StringEscapeUtils.escapeHtml(tipoCredenzialiSSLAliasCertificatoSubject));
					if(StringUtils.isNotEmpty(tipoCredenzialiSSLAliasCertificatoSubject)) {
						de.setType(DataElementType.TEXT_AREA_NO_EDIT);
						de.setRows(CostantiControlStation.LABEL_PARAMETRO_TEXT_AREA_SIZE);
					}else {
						de.setType(DataElementType.HIDDEN);
					}
					de.setSize(this.getSize());
					dati.add(de);
//							Issuer:
					de = new DataElement();
					de.setName(ConnettoriCostanti.PARAMETRO_CREDENZIALI_AUTENTICAZIONE_CONFIGURAZIONE_SSL_ALIAS_CERTIFICATO_ISSUER);
					de.setLabel(ConnettoriCostanti.LABEL_PARAMETRO_CREDENZIALI_AUTENTICAZIONE_CONFIGURAZIONE_SSL_ALIAS_CERTIFICATO_ISSUER);
					de.setValue(StringEscapeUtils.escapeHtml(tipoCredenzialiSSLAliasCertificatoIssuer));
					if(StringUtils.isNotEmpty(tipoCredenzialiSSLAliasCertificatoIssuer)) {
						de.setType(DataElementType.TEXT_AREA_NO_EDIT);
						de.setRows(CostantiControlStation.LABEL_PARAMETRO_TEXT_AREA_SIZE);
					}else {
						de.setType(DataElementType.HIDDEN);
					}
					de.setSize(this.getSize());
					dati.add(de);
//							Type:
					de = new DataElement();
					de.setName(ConnettoriCostanti.PARAMETRO_CREDENZIALI_AUTENTICAZIONE_CONFIGURAZIONE_SSL_ALIAS_CERTIFICATO_TYPE);
					de.setLabel(ConnettoriCostanti.LABEL_PARAMETRO_CREDENZIALI_AUTENTICAZIONE_CONFIGURAZIONE_SSL_ALIAS_CERTIFICATO_TYPE);
					de.setValue(tipoCredenzialiSSLAliasCertificatoType);
//					if(StringUtils.isNotEmpty(tipoCredenzialiSSLAliasCertificatoType)) {
//						de.setType(DataElementType.TEXT);
//					}else {
					de.setType(DataElementType.HIDDEN);
					//}
					de.setSize(this.getSize());
					dati.add(de);
//							Version:
					de = new DataElement();
					de.setName(ConnettoriCostanti.PARAMETRO_CREDENZIALI_AUTENTICAZIONE_CONFIGURAZIONE_SSL_ALIAS_CERTIFICATO_VERSION);
					de.setLabel(ConnettoriCostanti.LABEL_PARAMETRO_CREDENZIALI_AUTENTICAZIONE_CONFIGURAZIONE_SSL_ALIAS_CERTIFICATO_VERSION);
					de.setValue(tipoCredenzialiSSLAliasCertificatoVersion);
//					if(StringUtils.isNotEmpty(tipoCredenzialiSSLAliasCertificatoVersion)) {
//						de.setType(DataElementType.TEXT);
//					}else {
					de.setType(DataElementType.HIDDEN);
					//}
					de.setSize(this.getSize());
					dati.add(de);
//							Serial Number:
					de = new DataElement();
					de.setName(ConnettoriCostanti.PARAMETRO_CREDENZIALI_AUTENTICAZIONE_CONFIGURAZIONE_SSL_ALIAS_CERTIFICATO_SERIAL_NUMBER);
					de.setLabel(ConnettoriCostanti.LABEL_PARAMETRO_CREDENZIALI_AUTENTICAZIONE_CONFIGURAZIONE_SSL_ALIAS_CERTIFICATO_SERIAL_NUMBER);
					de.setValue(tipoCredenzialiSSLAliasCertificatoSerialNumber);
					if(StringUtils.isNotEmpty(tipoCredenzialiSSLAliasCertificatoSerialNumber)) {
						de.setType(DataElementType.TEXT);
					}else {
						de.setType(DataElementType.HIDDEN);
					}
					de.setSize(this.getSize());
					dati.add(de);
//							SelfSigned:
					de = new DataElement();
					de.setName(ConnettoriCostanti.PARAMETRO_CREDENZIALI_AUTENTICAZIONE_CONFIGURAZIONE_SSL_ALIAS_CERTIFICATO_SELF_SIGNED);
					de.setLabel(ConnettoriCostanti.LABEL_PARAMETRO_CREDENZIALI_AUTENTICAZIONE_CONFIGURAZIONE_SSL_ALIAS_CERTIFICATO_SELF_SIGNED);
					de.setValue(tipoCredenzialiSSLAliasCertificatoSelfSigned);
					if(StringUtils.isNotEmpty(tipoCredenzialiSSLAliasCertificatoSelfSigned)) {
						de.setType(DataElementType.TEXT);
					}else {
						de.setType(DataElementType.HIDDEN);
					}
					de.setSize(this.getSize());
					dati.add(de);
//							NotBefore:
					de = new DataElement();
					de.setName(ConnettoriCostanti.PARAMETRO_CREDENZIALI_AUTENTICAZIONE_CONFIGURAZIONE_SSL_ALIAS_CERTIFICATO_NOT_BEFORE);
					de.setLabel(ConnettoriCostanti.LABEL_PARAMETRO_CREDENZIALI_AUTENTICAZIONE_CONFIGURAZIONE_SSL_ALIAS_CERTIFICATO_NOT_BEFORE);
					de.setValue(tipoCredenzialiSSLAliasCertificatoNotBefore);
					if(StringUtils.isNotEmpty(tipoCredenzialiSSLAliasCertificatoNotBefore)) {
						de.setType(DataElementType.TEXT);
					}else {
						de.setType(DataElementType.HIDDEN);
					}
					de.setSize(this.getSize());
					dati.add(de);
//							NotAfter:
					de = new DataElement();
					de.setName(ConnettoriCostanti.PARAMETRO_CREDENZIALI_AUTENTICAZIONE_CONFIGURAZIONE_SSL_ALIAS_CERTIFICATO_NOT_AFTER);
					de.setLabel(ConnettoriCostanti.LABEL_PARAMETRO_CREDENZIALI_AUTENTICAZIONE_CONFIGURAZIONE_SSL_ALIAS_CERTIFICATO_NOT_AFTER);
					de.setValue(tipoCredenzialiSSLAliasCertificatoNotAfter);
					if(StringUtils.isNotEmpty(tipoCredenzialiSSLAliasCertificatoNotAfter)) {
						de.setType(DataElementType.TEXT);
					}else {
						de.setType(DataElementType.HIDDEN);
					}
					de.setSize(this.getSize());
					dati.add(de);
					
					
					// data element per pilotare la label del  tasto carica
					de = new DataElement();
					de.setName(ConnettoriCostanti.PARAMETRO_CREDENZIALI_AUTENTICAZIONE_CONFIGURAZIONE_SSL_WIZARD_STEP);
					de.setLabel("");
					de.setType(DataElementType.HIDDEN);
					de.setValue(tipoCredenzialiSSLStatoElaborazioneCertificato);
					dati.add(de);
					
				} else {
					
					// 1b  Checkbox selfSigned
					de = new DataElement();
					de.setName(ConnettoriCostanti.PARAMETRO_CREDENZIALI_AUTENTICAZIONE_CONFIGURAZIONE_SSL_MANUALE_SELF_SIGNED);
					de.setLabel(ConnettoriCostanti.LABEL_PARAMETRO_CREDENZIALI_AUTENTICAZIONE_CONFIGURAZIONE_SSL_MANUALE_SELF_SIGNED);
					de.setType(DataElementType.CHECKBOX);
					de.setSelected(ServletUtils.isCheckBoxEnabled(tipoCredenzialiSSLConfigurazioneManualeSelfSigned));
					de.setSize(this.getSize());
					de.setPostBack(true);
					dati.add(de);
					
					
					// 1b. TextArea Subject
					de = new DataElement();
					de.setLabel(ConnettoriCostanti.LABEL_PARAMETRO_CREDENZIALI_AUTENTICAZIONE_SUBJECT);
					de.setValue(StringEscapeUtils.escapeHtml(subject));
					de.setType(DataElementType.TEXT_AREA);
					de.setName(ConnettoriCostanti.PARAMETRO_CREDENZIALI_AUTENTICAZIONE_SUBJECT);
					de.setRows(CostantiControlStation.LABEL_PARAMETRO_TEXT_AREA_SIZE);
					de.setSize(this.getSize());
					de.setRequired(true);
					dati.addElement(de);
					
					// 1b. TextArea Issuer
					de = new DataElement();
					de.setLabel(ConnettoriCostanti.LABEL_PARAMETRO_CREDENZIALI_AUTENTICAZIONE_ISSUER);
					de.setName(ConnettoriCostanti.PARAMETRO_CREDENZIALI_AUTENTICAZIONE_ISSUER);
					if(!ServletUtils.isCheckBoxEnabled(tipoCredenzialiSSLConfigurazioneManualeSelfSigned)) {
						de.setType(DataElementType.TEXT_AREA);
						de.setValue(StringEscapeUtils.escapeHtml(issuer));
						de.setRows(CostantiControlStation.LABEL_PARAMETRO_TEXT_AREA_SIZE);
						de.setSize(this.getSize());
					} else {
						de.setType(DataElementType.HIDDEN);
						de.setValue("");
					}
					dati.addElement(de);
				}
			}
			
			if (ConnettoriCostanti.AUTENTICAZIONE_TIPO_APIKEY.equals(tipoauth)  && !connettore) {
				
				boolean multipleApiKeysEnabled = ServletUtils.isCheckBoxEnabled(multipleApiKey);
				boolean appIdModificabile = ConnettoriCostanti.PARAMETRO_CREDENZIALI_AUTENTICAZIONE_APP_ID_MODIFICABILE;
				
				boolean change = ServiziApplicativiCostanti.SERVLET_NAME_SERVIZI_APPLICATIVI_CHANGE.equals(toCall) || 
						SoggettiCostanti.SERVLET_NAME_SOGGETTI_CHANGE.equals(toCall);
				
				boolean soggetti = SoggettiCostanti.SERVLET_NAME_SOGGETTI_CHANGE.equals(toCall);
				boolean encryptPassword = soggetti ? this.connettoriCore.isSoggettiPasswordEncryptEnabled() : this.connettoriCore.isApplicativiPasswordEncryptEnabled();
								
				boolean modificaApiKey = ServletUtils.isCheckBoxEnabled(changepwd);
				String suffixOld = "";
				if(modificaApiKey) {
					suffixOld = "__OLD";
				}
				
				boolean passwordCifrata = ServletUtils.isCheckBoxEnabled(tipoCredenzialiSSLVerificaTuttiICampi); // tipoCredenzialiSSLVerificaTuttiICampi usata come informazione per sapere se una password e' cifrata o meno
				
				if(change && passwordCifrata ){
					DataElement deCifratura = new DataElement();
					deCifratura.setName(ConnettoriCostanti.PARAMETRO_CREDENZIALI_AUTENTICAZIONE_CONFIGURAZIONE_SSL_VERIFICA_TUTTI_CAMPI);
					deCifratura.setType(DataElementType.HIDDEN);
					deCifratura.setValue(tipoCredenzialiSSLVerificaTuttiICampi);
					dati.add(deCifratura);
				}
				
				// solo adesso posso reimpostare a false il change
				if(change) {
					if(!tipoauth.equals(oldtipoauth)) {
						change = false;
					}
				}
				
				de = new DataElement();
				de.setName(ConnettoriCostanti.PARAMETRO_CREDENZIALI_AUTENTICAZIONE_MULTIPLE_API_KEYS+suffixOld);
				de.setLabel(ConnettoriCostanti.LABEL_PARAMETRO_CREDENZIALI_AUTENTICAZIONE_MULTIPLE_API_KEYS);
				if(change) {
					de.setType(DataElementType.HIDDEN);
					de.setValue(multipleApiKey);
					if(!modificaApiKey) {
						DataElement deLabel = new DataElement();
						deLabel.setLabel(ConnettoriCostanti.LABEL_PARAMETRO_CREDENZIALI_AUTENTICAZIONE_MULTIPLE_API_KEYS);
						deLabel.setName(ConnettoriCostanti.PARAMETRO_CREDENZIALI_AUTENTICAZIONE_MULTIPLE_API_KEYS+"__LABEL");
						if(!multipleApiKeysEnabled) {
							deLabel.setType(DataElementType.TEXT);
						}
						else {
							deLabel.setType(DataElementType.HIDDEN); // faccio vedere sotto direttamente l'app id
						}
						deLabel.setValue(multipleApiKeysEnabled? CostantiConfigurazione.ABILITATO.getValue() : CostantiConfigurazione.DISABILITATO.getValue());
						dati.addElement(deLabel);						
					}
				}
				else {
					de.setType(DataElementType.CHECKBOX);
					de.setSelected(multipleApiKeysEnabled);
					de.setPostBack(true);
				}
				dati.addElement(de);
				
				de = new DataElement();
				de.setName(ConnettoriCostanti.PARAMETRO_CREDENZIALI_AUTENTICAZIONE_APP_ID+suffixOld);
				de.setLabel(ConnettoriCostanti.LABEL_PARAMETRO_CREDENZIALI_AUTENTICAZIONE_APP_ID_EMPTY_LABEL);
				if(change) {
					de.setLabel(ConnettoriCostanti.LABEL_PARAMETRO_CREDENZIALI_AUTENTICAZIONE_APP_ID);
				}
				de.setValue(StringEscapeUtils.escapeHtml(appId));
				if(multipleApiKeysEnabled) {
					if(appIdModificabile) {
						de.setType(DataElementType.TEXT_EDIT);
						de.setSize(this.getSize());
						de.setRequired(true);
					}
					else if(change && !modificaApiKey) {
						de.setType(DataElementType.TEXT);
					}
					else {
						de.setType(DataElementType.HIDDEN);
					}
				}else {
					de.setType(DataElementType.HIDDEN);
				}
				dati.addElement(de);
				
				if(change && !modificaApiKey && !encryptPassword && !passwordCifrata) {
					de = new DataElement();
					de.setName(ConnettoriCostanti.PARAMETRO_CREDENZIALI_AUTENTICAZIONE_API_KEY+"__LABEL");
					de.setLabel(ConnettoriCostanti.LABEL_PARAMETRO_CREDENZIALI_AUTENTICAZIONE_API_KEY);
					de.setType(DataElementType.TEXT_AREA_NO_EDIT);
					de.setRows(3);
					if(multipleApiKeysEnabled) {
						de.setValue(ApiKeyUtilities.encodeMultipleApiKey(password));
					}
					else {
						de.setValue(ApiKeyUtilities.encodeApiKey(utente, password));
					}
					dati.addElement(de);
				}
				
				if(change) {
					DataElement deModifica = new DataElement();
					deModifica.setLabel(ConnettoriCostanti.LABEL_PARAMETRO_CREDENZIALI_AUTENTICAZIONE_MODIFICA_API_KEY);
					deModifica.setType(DataElementType.CHECKBOX);
					deModifica.setName(ConnettoriCostanti.PARAMETRO_CREDENZIALI_AUTENTICAZIONE_CHANGE_PASSWORD);
					deModifica.setPostBack(true);
					deModifica.setSelected(changepwd);
					deModifica.setSize(this.getSize());
					dati.addElement(deModifica);
				}
				
				if(modificaApiKey) {
					
					de = new DataElement();
					de.setLabel(ConnettoriCostanti.LABEL_PARAMETRO_CREDENZIALI_AUTENTICAZIONE_NUOVA_API_KEY);
					de.setType(DataElementType.SUBTITLE);
					dati.addElement(de);
					
					de = new DataElement();
					de.setName(ConnettoriCostanti.PARAMETRO_CREDENZIALI_AUTENTICAZIONE_MULTIPLE_API_KEYS);
					de.setLabel(ConnettoriCostanti.LABEL_PARAMETRO_CREDENZIALI_AUTENTICAZIONE_MULTIPLE_API_KEYS);
					de.setType(DataElementType.CHECKBOX);
					de.setSelected(multipleApiKeysEnabled);
					de.setPostBack(true);
					dati.addElement(de);
					
					de = new DataElement();
					de.setName(ConnettoriCostanti.PARAMETRO_CREDENZIALI_AUTENTICAZIONE_APP_ID);
					de.setLabel(ConnettoriCostanti.LABEL_PARAMETRO_CREDENZIALI_AUTENTICAZIONE_APP_ID_EMPTY_LABEL);
					de.setValue(StringEscapeUtils.escapeHtml(appId));
					if(multipleApiKeysEnabled) {
						if(appIdModificabile) {
							de.setType(DataElementType.TEXT_EDIT);
							de.setSize(this.getSize());
							de.setRequired(true);
						}
						else {
							de.setType(DataElementType.HIDDEN);
						}
					}else {
						de.setType(DataElementType.HIDDEN);
					}
					dati.addElement(de);
									
				}
				
				de = new DataElement();
				de.setName(ConnettoriCostanti.PARAMETRO_CREDENZIALI_AUTENTICAZIONE_API_KEY);
				de.setValue(StringEscapeUtils.escapeHtml(apiKey));
				de.setType(DataElementType.HIDDEN);
				dati.addElement(de);
				
			}

			if (ConnettoriCostanti.AUTENTICAZIONE_TIPO_PRINCIPAL.equals(tipoauth)  && !connettore) {
				de = new DataElement();
				de.setLabel(ConnettoriCostanti.LABEL_PARAMETRO_CREDENZIALI_AUTENTICAZIONE_PRINCIPAL);
				de.setValue(StringEscapeUtils.escapeHtml(principal));
				de.setType(DataElementType.TEXT_EDIT);
				de.setName(ConnettoriCostanti.PARAMETRO_CREDENZIALI_AUTENTICAZIONE_PRINCIPAL);
				de.setSize(this.getSize());
				de.setRequired(true);
				dati.addElement(de);
			}

		}  
		else{
			de = new DataElement();
			de.setType(DataElementType.HIDDEN);
			de.setName(ConnettoriCostanti.PARAMETRO_INVOCAZIONE_CREDENZIALI_TIPO_AUTENTICAZIONE);
			de.setValue(ConnettoriCostanti.AUTENTICAZIONE_TIPO_NESSUNA);
			dati.addElement(de);
		}
		
		return dati;
	}
	
	public Vector<DataElement> addEndPointToDati(Vector<DataElement> dati,String connettoreDebug,
			String endpointtype, String autenticazioneHttp,String prefix, String url, String nome, String tipo,
			String user, String password, String initcont, String urlpgk,
			String provurl, String connfact, String sendas, String objectName, TipoOperazione tipoOperazione,
			String httpsurl, String httpstipologia, boolean httpshostverify,
			String httpspath, String httpstipo, String httpspwd,
			String httpsalgoritmo, boolean httpsstato, String httpskeystore,
			String httpspwdprivatekeytrust, String httpspathkey,
			String httpstipokey, String httpspwdkey,
			String httpspwdprivatekey, String httpsalgoritmokey,
			String httpsKeyAlias, String httpsTrustStoreCRLs,
			String tipoconn, String servletChiamante, String elem1, String elem2, String elem3,
			String elem4, String elem5, String elem6, String elem7, String elem8,
			String stato,
			boolean showSectionTitle,
			Boolean isConnettoreCustomUltimaImmagineSalvata,
			String proxyEnabled, String proxyHost, String proxyPort, String proxyUsername, String proxyPassword,
			String tempiRisposta_enabled, String tempiRisposta_connectionTimeout, String tempiRisposta_readTimeout, String tempiRisposta_tempoMedioRisposta,
			String opzioniAvanzate, String transfer_mode, String transfer_mode_chunk_size, String redirect_mode, String redirect_max_hop,
			String requestOutputFileName,String requestOutputFileNameHeaders,String requestOutputParentDirCreateIfNotExists,String requestOutputOverwriteIfExists,
			String responseInputMode, String responseInputFileName, String responseInputFileNameHeaders, String responseInputDeleteAfterRead, String responseInputWaitTime,
			boolean autenticazioneToken, String tokenPolicy,
			List<ExtendedConnettore> listExtendedConnettore, boolean forceEnabled,
			String protocollo, boolean forceHttps, boolean forceHttpsClient,
			boolean visualizzaSezioneSAServer, boolean servizioApplicativoServerEnabled, String servizioApplicativoServer, String[] listaSAServer) throws Exception {


		Boolean confPers = (Boolean) this.session.getAttribute(CostantiControlStation.SESSION_PARAMETRO_GESTIONE_CONFIGURAZIONI_PERSONALIZZATE);

		TipologiaConnettori tipologiaConnettori = null;
		try {
			tipologiaConnettori = Utilities.getTipologiaConnettori(this.core);
		} catch (Exception e) {
			// default
			tipologiaConnettori = TipologiaConnettori.TIPOLOGIA_CONNETTORI_ALL;
		}

		// override tipologiaconnettori :
		// se standard allora la tipologia connettori e' sempre http
		// indipendentemente
		// dalla proprieta settata
		if (this.isModalitaStandard()) {
			tipologiaConnettori = TipologiaConnettori.TIPOLOGIA_CONNETTORI_HTTP;
		}

		if(prefix==null){
			prefix="";
		}
		
		if(showSectionTitle){
			DataElement de = new DataElement();
			de.setLabel(prefix+ConnettoriCostanti.LABEL_CONNETTORE);
			de.setType(DataElementType.TITLE);
			dati.addElement(de);
		}

		boolean soloConnettoriHttp = false;
		if(forceHttps) {
			if(!this.isProfiloModIPA(protocollo)) {
				// la scelta se usare il tipo https o la configurazione jvm per https deve rimanere.
				// viene forzata solamente l'autenticazione client nel caso si scelga un connettore https per il profilo ModI PA
				endpointtype = TipiConnettore.HTTPS.toString();
			}
			else {
				// profilo modIPA, si deve usare solamente i connettori http
				if (!TipologiaConnettori.TIPOLOGIA_CONNETTORI_HTTP.equals(tipologiaConnettori)) {
					soloConnettoriHttp = true;
				}
			}
		}
		
		if(
				// serve sempre poter sceglire un applicativo server: this.isModalitaAvanzata() && 
				visualizzaSezioneSAServer && (listaSAServer != null && listaSAServer.length > 0)) {
			DataElement de = new DataElement();
			de.setLabel(ConnettoriCostanti.LABEL_PARAMETRO_CONNETTORE_ABILITA_USO_APPLICATIVO_SERVER);
			de.setName(ConnettoriCostanti.PARAMETRO_CONNETTORE_ABILITA_USO_APPLICATIVO_SERVER);
			de.setType(DataElementType.CHECKBOX);
			de.setSelected(servizioApplicativoServerEnabled);
			de.setPostBack(true);
			dati.addElement(de);
			
			de = new DataElement();
			de.setLabel(ConnettoriCostanti.LABEL_PARAMETRO_CONNETTORE_ID_APPLICATIVO_SERVER);
			de.setName(ConnettoriCostanti.PARAMETRO_CONNETTORE_ID_APPLICATIVO_SERVER);
			if(servizioApplicativoServerEnabled) {
				de.setSelected(servizioApplicativoServer);
				de.setValues(listaSAServer);
				de.setType(DataElementType.SELECT);
			} else {
				de.setType(DataElementType.HIDDEN);
				de.setValue(servizioApplicativoServer);
			}
			dati.addElement(de);
			
			if(servizioApplicativoServerEnabled) {
				dati = this.addEndPointToDatiAsHidden(dati, endpointtype, url, nome, tipo, user, password, initcont, urlpgk, provurl, connfact, sendas, objectName, tipoOperazione, httpsurl, httpstipologia, httpshostverify, httpspath, httpstipo, httpspwd, httpsalgoritmo, httpsstato, httpskeystore, httpspwdprivatekeytrust, httpspathkey, httpstipokey, httpspwdkey, httpspwdprivatekey, httpsalgoritmokey, httpsKeyAlias, httpsTrustStoreCRLs, tipoconn, servletChiamante, elem1, elem2, elem3, elem4, elem5, elem6, elem7, stato, proxyEnabled, proxyHost, proxyPort, proxyUsername, proxyPassword, tempiRisposta_enabled, tempiRisposta_connectionTimeout, tempiRisposta_readTimeout, tempiRisposta_tempoMedioRisposta, opzioniAvanzate, transfer_mode, transfer_mode_chunk_size, redirect_mode, redirect_max_hop, requestOutputFileName, requestOutputFileNameHeaders, requestOutputParentDirCreateIfNotExists, requestOutputOverwriteIfExists, responseInputMode, responseInputFileName, responseInputFileNameHeaders, responseInputDeleteAfterRead, responseInputWaitTime);
			}
		}
		
		if(!servizioApplicativoServerEnabled) {
	
			/** VISUALIZZAZIONE HTTP ONLY MODE */
	
			if (TipologiaConnettori.TIPOLOGIA_CONNETTORI_HTTP.equals(tipologiaConnettori)) {
				
				boolean configurazioneNonVisualizzabile = false;
				
				DataElement de = new DataElement();
				de.setLabel(ConnettoriCostanti.LABEL_CONNETTORE_ABILITATO);
	
				de.setName(ConnettoriCostanti.PARAMETRO_CONNETTORE_ENDPOINT_TYPE_CHECK);
				if(!TipiConnettore.HTTP.toString().equals(endpointtype) &&
						!TipiConnettore.HTTPS.toString().equals(endpointtype) &&
						(
								!TipiConnettore.DISABILITATO.toString().equals(endpointtype)
								|| 
								(TipiConnettore.DISABILITATO.toString().equals(endpointtype) && forceEnabled)
						)){
					de.setLabel(null);
					de.setValue(CostantiControlStation.LABEL_CONFIGURAZIONE_IMPOSTATA_MODALITA_AVANZATA_SHORT_MESSAGE);
					de.setType(DataElementType.TEXT);
					configurazioneNonVisualizzabile = true;
					this.pd.disableEditMode();
					this.pd.setMessage(CostantiControlStation.LABEL_CONFIGURAZIONE_IMPOSTATA_MODALITA_AVANZATA_LONG_MESSAGE, Costanti.MESSAGE_TYPE_INFO);
				}
				else if( (  (AccordiServizioParteSpecificaCostanti.OBJECT_NAME_APS.equals(objectName) && TipoOperazione.CHANGE.equals(tipoOperazione))
						|| 
						(AccordiServizioParteSpecificaCostanti.OBJECT_NAME_APS_FRUITORI.equals(objectName) && TipoOperazione.CHANGE.equals(tipoOperazione))
						)
						&& StatiAccordo.finale.toString().equals(stato) && this.isShowGestioneWorkflowStatoDocumenti() ){
					if (endpointtype.equals(TipiConnettore.HTTP.toString()) || endpointtype.equals(TipiConnettore.HTTPS.toString())) {
						de.setType(DataElementType.HIDDEN);
						de.setValue(Costanti.CHECK_BOX_ENABLED);
					}
					else{
						de.setLabel(null);
						de.setType(DataElementType.TEXT);
						de.setValue(TipiConnettore.DISABILITATO.toString());
					}
				}else{
					if(forceEnabled) {
						de.setType(DataElementType.HIDDEN);
						de.setValue(Costanti.CHECK_BOX_ENABLED);
					}
					else {
						de.setType(DataElementType.CHECKBOX);
						if (endpointtype.equals(TipiConnettore.HTTP.toString()) || endpointtype.equals(TipiConnettore.HTTPS.toString())) {
							de.setSelected(true);
						}
					}
					de.setPostBack(true);
					//				de.setOnClick("AbilitaEndPointHTTP(\"" + tipoOp + "\")");
				}
				dati.addElement(de);
				
				
				de = new DataElement();
				de.setName(ConnettoriCostanti.PARAMETRO_CONNETTORE_ENDPOINT_TYPE);
				if (endpointtype.equals(TipiConnettore.HTTP.toString())) {
					de.setValue(TipiConnettore.HTTP.toString());
				}
				else if (endpointtype.equals(TipiConnettore.HTTPS.toString())) {
					de.setValue(TipiConnettore.HTTPS.toString());
				} 
				else {
					de.setValue(TipiConnettore.DISABILITATO.toString());
				}
				de.setType(DataElementType.HIDDEN);
				dati.addElement(de);
	
				
				de = new DataElement();
				de.setLabel(ConnettoriCostanti.LABEL_PARAMETRO_CONNETTORE_DEBUG);
				de.setName(ConnettoriCostanti.PARAMETRO_CONNETTORE_DEBUG);
	//			if(this.core.isShowDebugOptionConnettore() && !TipiConnettore.DISABILITATO.toString().equals(endpointtype)){
	//				de.setType(DataElementType.CHECKBOX);
	//				if ( ServletUtils.isCheckBoxEnabled(connettoreDebug)) {
	//					de.setSelected(true);
	//				}
	//			}
	//			else{
				de.setType(DataElementType.HIDDEN);
				//}
				if ( ServletUtils.isCheckBoxEnabled(connettoreDebug)) {
					de.setValue("true");
				}
				else{
					de.setValue("false");
				}
				dati.addElement(de);
				
				
				de = new DataElement();
				de.setLabel(ConnettoriCostanti.LABEL_PARAMETRO_CONNETTORE_URL);
				String tmpUrl = url;
				if(url==null || "".equals(url) || "http://".equals(url) || "https://".equals(url) ){
					if (endpointtype.equals(TipiConnettore.HTTP.toString()) || endpointtype.equals(TipiConnettore.HTTPS.toString())) {
						if(this.isProfiloModIPA(protocollo)) {
							tmpUrl =TipiConnettore.HTTPS.toString()+"://";
						}
						else {
							tmpUrl =endpointtype+"://";
						}
					}
				}
				de.setValue(tmpUrl);
				if (endpointtype.equals(TipiConnettore.HTTP.toString()) || endpointtype.equals(TipiConnettore.HTTPS.toString())) {
					if ( !this.isShowGestioneWorkflowStatoDocumenti() || !StatiAccordo.finale.toString().equals(stato)) {
						de.setType(DataElementType.TEXT_AREA);
						de.setRequired(true);
					} else {
						de.setType(DataElementType.TEXT_AREA_NO_EDIT);
					}
					de.setRows(ConnettoriCostanti.LABEL_PARAMETRO_CONNETTORE_URL_SIZE);
				}else{
					de.setType(DataElementType.HIDDEN);
				}
				de.setName(ConnettoriCostanti.PARAMETRO_CONNETTORE_URL);
				de.setSize(this.getSize());
				dati.addElement(de);
				
	//			if (endpointtype.equals(TipiConnettore.HTTPS.toString())) {
	//				de = new DataElement();
	//				de.setLabel(ConnettoriCostanti.LABEL_PARAMETRO_CONNETTORE_HTTPS_URL);
	//				de.setValue(tmpUrl);
	//				de.setType(DataElementType.HIDDEN);
	//				de.setName(ConnettoriCostanti.PARAMETRO_CONNETTORE_HTTPS_URL);
	//				dati.addElement(de);
	//			}
				
				if (endpointtype.equals(TipiConnettore.HTTP.toString()) || endpointtype.equals(TipiConnettore.HTTPS.toString())) {
					
					boolean showAutenticazioneHttpBasic = true;
					if(autenticazioneToken) {
						showAutenticazioneHttpBasic = !isTokenPolicyMode_useAuthorizationHeader(tokenPolicy);
					}
					
	//				if(!ServiziApplicativiCostanti.SERVLET_NAME_SERVIZI_APPLICATIVI_ENDPOINT.equals(servletChiamante) &&
	//						!ServiziApplicativiCostanti.SERVLET_NAME_SERVIZI_APPLICATIVI_ENDPOINT_RISPOSTA.equals(servletChiamante) ){
					de = new DataElement();
					de.setLabel(ConnettoriCostanti.LABEL_CONNETTORE_HTTP);
					de.setName(ConnettoriCostanti.PARAMETRO_CONNETTORE_ENDPOINT_TYPE_ENABLE_HTTP);
					if(showAutenticazioneHttpBasic) {
						de.setType(DataElementType.CHECKBOX);
						if ( ServletUtils.isCheckBoxEnabled(autenticazioneHttp)) {
							de.setSelected(true);
						}
						de.setPostBack(true);
					}
					else {
						de.setType(DataElementType.HIDDEN);
						de.setValue(Costanti.CHECK_BOX_DISABLED);
						autenticazioneHttp = Costanti.CHECK_BOX_DISABLED;
					}
					dati.addElement(de);		
					//}
					
					de = new DataElement();
					de.setLabel(ConnettoriCostanti.LABEL_CONNETTORE_BEARER);
					de.setName(ConnettoriCostanti.PARAMETRO_CONNETTORE_TOKEN_POLICY_STATO);
					de.setType(DataElementType.CHECKBOX);
					de.setSelected(autenticazioneToken);
					de.setPostBack(true);
					dati.addElement(de);	
					
					de = new DataElement();
					de.setLabel(ConnettoriCostanti.LABEL_CONNETTORE_HTTPS);
					de.setName(ConnettoriCostanti.PARAMETRO_CONNETTORE_ENDPOINT_TYPE_ENABLE_HTTPS);
					de.setType(DataElementType.CHECKBOX);
					if (endpointtype.equals(TipiConnettore.HTTPS.toString())) {
						de.setSelected(true);
					}
					de.setPostBack(true);
					dati.addElement(de);
					
					// Proxy
					de = new DataElement();
					de.setLabel(ConnettoriCostanti.LABEL_CONNETTORE_PROXY);
					de.setName(ConnettoriCostanti.PARAMETRO_CONNETTORE_PROXY_ENABLED);
					de.setType(DataElementType.CHECKBOX);
					if ( ServletUtils.isCheckBoxEnabled(proxyEnabled)) {
						de.setSelected(true);
					}
					de.setPostBack(true);
					dati.addElement(de);
					
					// TempiRisposta
					de = new DataElement();
					de.setLabel(ConnettoriCostanti.LABEL_PARAMETRO_CONNETTORE_TEMPI_RISPOSTA_REDEFINE);
					de.setName(ConnettoriCostanti.PARAMETRO_CONNETTORE_TEMPI_RISPOSTA_REDEFINE);
					de.setType(DataElementType.CHECKBOX);
					if ( ServletUtils.isCheckBoxEnabled(tempiRisposta_enabled)) {
						de.setSelected(true);
					}
					de.setPostBack(true);
					dati.addElement(de);
					
				}	
				
				// Extended
				if(configurazioneNonVisualizzabile==false) {
					if(listExtendedConnettore!=null && listExtendedConnettore.size()>0){
						ServletExtendedConnettoreUtils.addToDatiEnabled(dati, listExtendedConnettore);
					}
				}
				
				// opzioni avanzate
				if (endpointtype.equals(TipiConnettore.HTTP.toString()) || endpointtype.equals(TipiConnettore.HTTPS.toString())) {
					de = new DataElement();
					de.setLabel(ConnettoriCostanti.LABEL_PARAMETRO_CONNETTORE_OPZIONI_AVANZATE);
					de.setName(ConnettoriCostanti.PARAMETRO_CONNETTORE_OPZIONI_AVANZATE);
					if (this.isModalitaAvanzata()) {
						de.setType(DataElementType.CHECKBOX);
						de.setValue(opzioniAvanzate);
						if ( ServletUtils.isCheckBoxEnabled(opzioniAvanzate)) {
							de.setSelected(true);
						}
					}else{
						de.setType(DataElementType.HIDDEN);
						de.setValue(Costanti.CHECK_BOX_DISABLED);
					}
					de.setPostBack(true);
					dati.addElement(de);
				}
				
				// Http Autenticazione
				if (ServletUtils.isCheckBoxEnabled(autenticazioneHttp)) {
					this.addCredenzialiToDati(dati, CostantiConfigurazione.CREDENZIALE_BASIC.getValue(), user, password, password, null,
							servletChiamante,true,endpointtype,true,false, prefix, true);
				}
				
				// Token Autenticazione
				if (autenticazioneToken) {
					this.addTokenPolicy(dati, tokenPolicy);
				}
				
				
				// Https
				if (endpointtype.equals(TipiConnettore.HTTPS.toString())) {
					ConnettoreHTTPSUtils.addHTTPSDati(dati, httpsurl, httpstipologia, httpshostverify, httpspath, httpstipo, 
							httpspwd, httpsalgoritmo, httpsstato, httpskeystore, httpspwdprivatekeytrust, httpspathkey, 
							httpstipokey, httpspwdkey, httpspwdprivatekey, httpsalgoritmokey, 
							httpsKeyAlias, httpsTrustStoreCRLs,
							stato, 
							this.core, this, this.getSize(), false, prefix,
							forceHttpsClient);
				}
				
				// Proxy
				if (endpointtype.equals(TipiConnettore.HTTP.toString()) || endpointtype.equals(TipiConnettore.HTTPS.toString())){
					if (ServletUtils.isCheckBoxEnabled(proxyEnabled)) {
						this.addProxyToDati(dati, proxyHost, proxyPort, proxyUsername, proxyPassword);
					}
				}
				
				// TempiRisposta
				if (endpointtype.equals(TipiConnettore.HTTP.toString()) || endpointtype.equals(TipiConnettore.HTTPS.toString())){
					if (ServletUtils.isCheckBoxEnabled(tempiRisposta_enabled)) {
						this.addTempiRispostaToDati(dati, tempiRisposta_connectionTimeout, tempiRisposta_readTimeout, tempiRisposta_tempoMedioRisposta);
					}
				}
				
				// Extended
				if(listExtendedConnettore!=null && listExtendedConnettore.size()>0){
					ServletExtendedConnettoreUtils.addToDatiExtendedInfo(dati, listExtendedConnettore);
				}
				
				// Opzioni Avanzate
				if (endpointtype.equals(TipiConnettore.HTTP.toString()) || endpointtype.equals(TipiConnettore.HTTPS.toString())){
					this.addOpzioniAvanzateHttpToDati(dati, opzioniAvanzate, transfer_mode, transfer_mode_chunk_size, redirect_mode, redirect_max_hop);
				}
				
			} else {
	
				/** VISUALIZZAZIONE COMPLETA CONNETTORI MODE */
	
				List<String> connettoriList = Connettori.getList();
				if(forceEnabled) {
					int indexDisabled = -1;
					for (int i = 0; i < connettoriList.size(); i++) {
						if(TipiConnettore.DISABILITATO.getNome().equals(connettoriList.get(i))) {
							indexDisabled = i;
							break;
						}
					}
					if(indexDisabled>=0) {
						connettoriList.remove(indexDisabled);
					}
				}
				int sizeEP = connettoriList.size();
				if (!connettoriList.contains(TipiConnettore.HTTPS.toString()))
					sizeEP++;
				if (confPers &&
						TipologiaConnettori.TIPOLOGIA_CONNETTORI_ALL.equals(tipologiaConnettori))
					sizeEP++;
				String[] tipoEP = new String[sizeEP];
				connettoriList.toArray(tipoEP);
				int newCount = connettoriList.size();
				if (!connettoriList.contains(TipiConnettore.HTTPS.toString())) {
					tipoEP[newCount] = TipiConnettore.HTTPS.toString();
					newCount++;
				}
				if (confPers &&
						TipologiaConnettori.TIPOLOGIA_CONNETTORI_ALL.equals(tipologiaConnettori))
					tipoEP[newCount] = TipiConnettore.CUSTOM.toString();
				//String[] tipoEP = { TipiConnettore.DISABILITATO.toString(), TipiConnettore.HTTP.toString(), TipiConnettore.JMS.toString(), TipiConnettore.NULL.toString(), TipiConnettore.NULL_ECHO.toString() };
	
				DataElement de = new DataElement();
				de.setLabel(ConnettoriCostanti.LABEL_PARAMETRO_CONNETTORE_ENDPOINT_TYPE);
				de.setType(DataElementType.SELECT);
				de.setName(ConnettoriCostanti.PARAMETRO_CONNETTORE_ENDPOINT_TYPE);
				if(soloConnettoriHttp) {
					List<String> connettoriListHttp = new ArrayList<>();
					connettoriListHttp.add(TipiConnettore.HTTP.toString());
					connettoriListHttp.add(TipiConnettore.HTTPS.toString());
					de.setValues(connettoriListHttp);
				}
				else {
					de.setValues(tipoEP);
				}
				de.setSelected(endpointtype);
				//		    de.setOnChange("CambiaEndPoint('" + tipoOp + "')");
				de.setPostBack(true);
				dati.addElement(de);
	
				
				de = new DataElement();
				de.setLabel(ConnettoriCostanti.LABEL_PARAMETRO_CONNETTORE_TIPO_PERSONALIZZATO);
				if (endpointtype == null || !endpointtype.equals(TipiConnettore.CUSTOM.toString()))
					de.setType(DataElementType.HIDDEN);
				else{
					de.setType(DataElementType.TEXT_EDIT);
					de.setRequired(true);
				}
				de.setName(ConnettoriCostanti.PARAMETRO_CONNETTORE_TIPO_PERSONALIZZATO);
				de.setValue(tipoconn);
				dati.addElement(de);
				
				
				
				de = new DataElement();
				de.setLabel(ConnettoriCostanti.LABEL_PARAMETRO_CONNETTORE_DEBUG);
				de.setName(ConnettoriCostanti.PARAMETRO_CONNETTORE_DEBUG);
				if(this.core.isShowDebugOptionConnettore() && !TipiConnettore.DISABILITATO.toString().equals(endpointtype)){
					de.setType(DataElementType.CHECKBOX);
					if ( ServletUtils.isCheckBoxEnabled(connettoreDebug)) {
						de.setSelected(true);
					}
				}
				else{
					de.setType(DataElementType.HIDDEN);
				}
				if ( ServletUtils.isCheckBoxEnabled(connettoreDebug)) {
					de.setValue("true");
				}
				else{
					de.setValue("false");
				}
				dati.addElement(de);	
				
				
				de = new DataElement();
				de.setLabel(ConnettoriCostanti.LABEL_PARAMETRO_CONNETTORE_URL);
				String tmpUrl = url;
				if(url==null || "".equals(url) || "http://".equals(url) || "https://".equals(url) ){
					if (endpointtype.equals(TipiConnettore.HTTP.toString()) || endpointtype.equals(TipiConnettore.HTTPS.toString())) {
						if(this.isProfiloModIPA(protocollo)) {
							tmpUrl =TipiConnettore.HTTPS.toString()+"://";
						}
						else {
							tmpUrl =endpointtype+"://";
						}
					}
				}
				de.setValue(tmpUrl);
				if (endpointtype.equals(TipiConnettore.HTTP.toString()) || endpointtype.equals(TipiConnettore.HTTPS.toString())){
					if (!this.isShowGestioneWorkflowStatoDocumenti() || !StatiAccordo.finale.toString().equals(stato)) {
						de.setType(DataElementType.TEXT_AREA);
						de.setRequired(true);
					} else {
						de.setType(DataElementType.TEXT_AREA_NO_EDIT);
					}
					de.setRows(ConnettoriCostanti.LABEL_PARAMETRO_CONNETTORE_URL_SIZE);
				}
				else{
					de.setType(DataElementType.HIDDEN);
				}
				de.setName(ConnettoriCostanti.PARAMETRO_CONNETTORE_URL);
				de.setSize(this.getSize());
				dati.addElement(de);
				
	//			if (endpointtype.equals(TipiConnettore.HTTPS.toString())) {
	//				de = new DataElement();
	//				de.setLabel(ConnettoriCostanti.LABEL_PARAMETRO_CONNETTORE_HTTPS_URL);
	//				de.setValue(tmpUrl);
	//				de.setType(DataElementType.HIDDEN);
	//				de.setName(ConnettoriCostanti.PARAMETRO_CONNETTORE_HTTPS_URL);
	//				dati.addElement(de);
	//			}
	
				if (endpointtype.equals(TipiConnettore.HTTP.toString()) || endpointtype.equals(TipiConnettore.HTTPS.toString())){
	//				if(!ServiziApplicativiCostanti.SERVLET_NAME_SERVIZI_APPLICATIVI_ENDPOINT.equals(servletChiamante) &&
	//						!ServiziApplicativiCostanti.SERVLET_NAME_SERVIZI_APPLICATIVI_ENDPOINT_RISPOSTA.equals(servletChiamante) ){
								
					boolean showAutenticazioneHttpBasic = true;
					if(autenticazioneToken) {
						showAutenticazioneHttpBasic = !isTokenPolicyMode_useAuthorizationHeader(tokenPolicy);
					}
					
					de = new DataElement();
					de.setLabel(ConnettoriCostanti.LABEL_CONNETTORE_HTTP);
					de.setName(ConnettoriCostanti.PARAMETRO_CONNETTORE_ENDPOINT_TYPE_ENABLE_HTTP);
					if(showAutenticazioneHttpBasic) {
						de.setType(DataElementType.CHECKBOX);
						if ( ServletUtils.isCheckBoxEnabled(autenticazioneHttp)) {
							de.setSelected(true);
						}
						de.setPostBack(true);
					}
					else {
						de.setType(DataElementType.HIDDEN);
						de.setValue(Costanti.CHECK_BOX_DISABLED);
						autenticazioneHttp = Costanti.CHECK_BOX_DISABLED;
					}
					dati.addElement(de);		
					//}
					
					de = new DataElement();
					de.setLabel(ConnettoriCostanti.LABEL_CONNETTORE_BEARER);
					de.setName(ConnettoriCostanti.PARAMETRO_CONNETTORE_TOKEN_POLICY_STATO);
					de.setType(DataElementType.CHECKBOX);
					de.setSelected(autenticazioneToken);
					de.setPostBack(true);
					dati.addElement(de);
					
					de = new DataElement();
					de.setLabel(ConnettoriCostanti.LABEL_CONNETTORE_PROXY);
					de.setName(ConnettoriCostanti.PARAMETRO_CONNETTORE_PROXY_ENABLED);
					de.setType(DataElementType.CHECKBOX);
					if ( ServletUtils.isCheckBoxEnabled(proxyEnabled)) {
						de.setSelected(true);
					}
					de.setPostBack(true);
					dati.addElement(de);
					
					// TempiRisposta
					de = new DataElement();
					de.setLabel(ConnettoriCostanti.LABEL_PARAMETRO_CONNETTORE_TEMPI_RISPOSTA_REDEFINE);
					de.setName(ConnettoriCostanti.PARAMETRO_CONNETTORE_TEMPI_RISPOSTA_REDEFINE);
					de.setType(DataElementType.CHECKBOX);
					if ( ServletUtils.isCheckBoxEnabled(tempiRisposta_enabled)) {
						de.setSelected(true);
					}
					de.setPostBack(true);
					dati.addElement(de);
				}
				
				// Extended
				if(listExtendedConnettore!=null && listExtendedConnettore.size()>0){
					ServletExtendedConnettoreUtils.addToDatiEnabled(dati, listExtendedConnettore);
				}
				
				// opzioni avanzate
				if (endpointtype.equals(TipiConnettore.HTTP.toString()) || endpointtype.equals(TipiConnettore.HTTPS.toString())) {
					de = new DataElement();
					de.setLabel(ConnettoriCostanti.LABEL_PARAMETRO_CONNETTORE_OPZIONI_AVANZATE);
					de.setName(ConnettoriCostanti.PARAMETRO_CONNETTORE_OPZIONI_AVANZATE);
					if (this.isModalitaAvanzata()) {
						de.setType(DataElementType.CHECKBOX);
						de.setValue(opzioniAvanzate);
						if ( ServletUtils.isCheckBoxEnabled(opzioniAvanzate)) {
							de.setSelected(true);
						}
					}else{
						de.setType(DataElementType.HIDDEN);
						de.setValue(Costanti.CHECK_BOX_DISABLED);
					}
					de.setPostBack(true);
					dati.addElement(de);
				}
				
				// Autenticazione http
				if (ServletUtils.isCheckBoxEnabled(autenticazioneHttp)) {
					this.addCredenzialiToDati(dati, CostantiConfigurazione.CREDENZIALE_BASIC.getValue(), user, password, password, null,
							servletChiamante,true,endpointtype,true,false, prefix, true);
				}
				
				// Token Autenticazione
				if (autenticazioneToken) {
					this.addTokenPolicy(dati, tokenPolicy);
				}
				
				// Custom
				if (endpointtype != null && endpointtype.equals(TipiConnettore.CUSTOM.toString()) &&
						!servletChiamante.equals(AccordiServizioParteSpecificaCostanti.SERVLET_NAME_APS_ADD) &&
						!servletChiamante.equals(AccordiServizioParteSpecificaCostanti.SERVLET_NAME_APS_FRUITORI_ADD) &&
						(isConnettoreCustomUltimaImmagineSalvata!=null && isConnettoreCustomUltimaImmagineSalvata)) {
					de = new DataElement();
					de.setType(DataElementType.LINK);
					de.setName(ConnettoriCostanti.PARAMETRO_CONNETTORE_CUSTOM_PROPRIETA);
					int numProp = 0;
					try {
						if (servletChiamante.equals(AccordiServizioParteSpecificaCostanti.SERVLET_NAME_APS_CHANGE)) {
							de.setUrl(ConnettoriCostanti.SERVLET_NAME_CONNETTORI_CUSTOM_PROPERTIES_LIST,
									new Parameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_CUSTOM_SERVLET, servletChiamante),
									new Parameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_CUSTOM_ID, elem1),
									new Parameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_CUSTOM_NOME_SERVIZIO, elem2),
									new Parameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_CUSTOM_TIPO_SERVIZIO, elem3),
									new Parameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_CUSTOM_VERSIONE_SERVIZIO, elem4));
							AccordoServizioParteSpecifica asps = this.apsCore.getAccordoServizioParteSpecifica(Long.parseLong(elem1));
							org.openspcoop2.core.registry.Connettore connettore = asps.getConfigurazioneServizio().getConnettore();
							if (connettore != null && (connettore.getCustom()!=null && connettore.getCustom()) ){
								for (int i = 0; i < connettore.sizePropertyList(); i++) {
									if(CostantiDB.CONNETTORE_DEBUG.equals(connettore.getProperty(i).getNome())==false  &&
											connettore.getProperty(i).getNome().startsWith(CostantiConnettori.CONNETTORE_EXTENDED_PREFIX)==false){
										numProp++;
									}
								}
								// Non devo contare la proprietà debug: numProp = connettore.sizePropertyList();
							}
						}
						if (servletChiamante.equals(AccordiServizioParteSpecificaCostanti.SERVLET_NAME_APS_FRUITORI_CHANGE)) {
							List<Parameter> lstParams = new ArrayList<>();
							
							String accessoDaAPSParametro = this.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_CONNETTORE_DA_LISTA_APS);
							if(accessoDaAPSParametro != null)
								lstParams.add(new Parameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_CONNETTORE_DA_LISTA_APS, accessoDaAPSParametro));
							
							lstParams.add(new Parameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_CUSTOM_SERVLET, servletChiamante));
							lstParams.add(new Parameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_CUSTOM_ID, elem1));
							lstParams.add(new Parameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_CUSTOM_MY_ID, elem2));
							lstParams.add(new Parameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_CUSTOM_ID_SOGGETTO_EROGATORE, elem3));
							lstParams.add(new Parameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_FRUITORE_VIEW_CONNETTORE_MAPPING_AZIONE_ID_PORTA, elem4));
							
							de.setUrl(ConnettoriCostanti.SERVLET_NAME_CONNETTORI_CUSTOM_PROPERTIES_LIST, lstParams.toArray(new Parameter[lstParams.size()]));
							
							
							int idServizioFruitoreInt = Integer.parseInt(elem2);
							Fruitore servFru = this.apsCore.getServizioFruitore(idServizioFruitoreInt);
							org.openspcoop2.core.registry.Connettore connettore = servFru.getConnettore();
							if (connettore != null && (connettore.getCustom()!=null && connettore.getCustom()) ){
								for (int i = 0; i < connettore.sizePropertyList(); i++) {
									if(CostantiDB.CONNETTORE_DEBUG.equals(connettore.getProperty(i).getNome())==false  &&
											connettore.getProperty(i).getNome().startsWith(CostantiConnettori.CONNETTORE_EXTENDED_PREFIX)==false){
										numProp++;
									}
								}
								// Non devo contare la proprietà debug: numProp = connettore.sizePropertyList();
							}
						}
						if (servletChiamante.equals(ServiziApplicativiCostanti.SERVLET_NAME_SERVIZI_APPLICATIVI_ENDPOINT) ||
								servletChiamante.equals(ServiziApplicativiCostanti.SERVLET_NAME_SERVIZI_APPLICATIVI_ENDPOINT_RISPOSTA)) {
							int idSilInt = Integer.parseInt(elem2);
							ServizioApplicativo sa = this.saCore.getServizioApplicativo(idSilInt);
							Soggetto soggetto = this.soggettiCore.getSoggettoRegistro(new IDSoggetto(sa.getTipoSoggettoProprietario(), sa.getNomeSoggettoProprietario()));
							List<Parameter> lstParams = new ArrayList<>();
							
							lstParams.add(new Parameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_CUSTOM_SERVLET, servletChiamante));
							lstParams.add(new Parameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_CUSTOM_NOME_SERVIZIO_APPLICATIVO, elem1));
							lstParams.add(new Parameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_CUSTOM_ID_SERVIZIO_APPLICATIVO, elem2));
							if(elem3 != null)
								lstParams.add(new Parameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_CUSTOM_ID, elem3));
							if(elem3 != null)
								lstParams.add(new Parameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_CUSTOM_ID_PORTA, elem4));
							lstParams.add(new Parameter(ServiziApplicativiCostanti.PARAMETRO_SERVIZI_APPLICATIVI_PROVIDER, soggetto.getId()+""));
							
							String accessoDaAPSParametro = this.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_CONNETTORE_DA_LISTA_APS);
							if(accessoDaAPSParametro != null)
								lstParams.add(new Parameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_CONNETTORE_DA_LISTA_APS, accessoDaAPSParametro));
	
							de.setUrl(ConnettoriCostanti.SERVLET_NAME_CONNETTORI_CUSTOM_PROPERTIES_LIST, lstParams.toArray(new Parameter[lstParams.size()]));
							
							if (servletChiamante.equals(ServiziApplicativiCostanti.SERVLET_NAME_SERVIZI_APPLICATIVI_ENDPOINT)) {
								InvocazioneServizio is = sa.getInvocazioneServizio();
								Connettore connettore = is.getConnettore();
								if(connettore.getCustom()==null || !connettore.getCustom()){
									// è cambiato il tipo
									de.setType(DataElementType.HIDDEN);
								}
								if (connettore != null && connettore.getCustom()!=null && connettore.getCustom()){
									for (int i = 0; i < connettore.sizePropertyList(); i++) {
										if(CostantiDB.CONNETTORE_DEBUG.equals(connettore.getProperty(i).getNome())==false  &&
												connettore.getProperty(i).getNome().startsWith(CostantiConnettori.CONNETTORE_EXTENDED_PREFIX)==false){
											numProp++;
										}
									}
									// Non devo contare la proprietà debug: numProp = connettore.sizePropertyList();
								}
							} else {
								RispostaAsincrona ra = sa.getRispostaAsincrona();
								Connettore connettore = ra.getConnettore();
								if(connettore.getCustom()==null || !connettore.getCustom()){
									// è cambiato il tipo
									de.setType(DataElementType.HIDDEN);
								}
								if (connettore != null && connettore.getCustom()!=null && connettore.getCustom()){
									for (int i = 0; i < connettore.sizePropertyList(); i++) {
										if(CostantiDB.CONNETTORE_DEBUG.equals(connettore.getProperty(i).getNome())==false  &&
												connettore.getProperty(i).getNome().startsWith(CostantiConnettori.CONNETTORE_EXTENDED_PREFIX)==false){
											numProp++;
										}
									}
									// Non devo contare la proprietà debug: numProp = connettore.sizePropertyList();
								}
							}
						}
						if (servletChiamante.equals(SoggettiCostanti.SERVLET_NAME_SOGGETTI_ENDPOINT)) {
							de.setUrl(ConnettoriCostanti.SERVLET_NAME_CONNETTORI_CUSTOM_PROPERTIES_LIST,
									new Parameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_CUSTOM_SERVLET, servletChiamante),
									new Parameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_CUSTOM_ID, elem1),
									new Parameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_CUSTOM_NOME_SOGGETTO, elem2),
									new Parameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_CUSTOM_TIPO_SOGGETTO, elem3));
							int idInt = Integer.parseInt(elem1);
							SoggettoCtrlStat scs = this.soggettiCore.getSoggettoCtrlStat(idInt);
							Soggetto ss = scs.getSoggettoReg();
							org.openspcoop2.core.registry.Connettore connettore = ss.getConnettore();
							if (connettore != null && (connettore.getCustom()!=null && connettore.getCustom()) ){
								for (int i = 0; i < connettore.sizePropertyList(); i++) {
									if(CostantiDB.CONNETTORE_DEBUG.equals(connettore.getProperty(i).getNome())==false  &&
											connettore.getProperty(i).getNome().startsWith(CostantiConnettori.CONNETTORE_EXTENDED_PREFIX)==false){
										numProp++;
									}
								}
								// Non devo contare la proprietà debug: numProp = connettore.sizePropertyList();
							}
						}
					} catch (Exception ex) {
						this.log.error("Exception: " + ex.getMessage(), ex);
					}
					de.setValue(ConnettoriCostanti.LABEL_CONNETTORE_PROPRIETA+"("+numProp+")");
					dati.addElement(de);
				}
				
				// Https
				if (endpointtype.equals(TipiConnettore.HTTPS.toString())) {
					ConnettoreHTTPSUtils.addHTTPSDati(dati, httpsurl, httpstipologia, httpshostverify, httpspath, httpstipo, httpspwd, httpsalgoritmo, 
							httpsstato, httpskeystore, httpspwdprivatekeytrust, httpspathkey, httpstipokey, httpspwdkey, httpspwdprivatekey, httpsalgoritmokey, 
							httpsKeyAlias, httpsTrustStoreCRLs,
							stato,
							this.core, this, this.getSize(), false, prefix,
							forceHttpsClient);
				}
	
				// Jms
				if (endpointtype.equals(TipiConnettore.JMS.getNome())) {
					ConnettoreJMSUtils.addJMSDati(dati, nome, tipo, user, password, initcont, urlpgk, 
							provurl, connfact, sendas, objectName, tipoOperazione, stato,
							this.core, this, this.getSize());
				}
				
				// FileSystem
				if (endpointtype.equals(TipiConnettore.FILE.toString())) {
					ConnettoreFileUtils.addFileDati(dati, this.getSize(),
							requestOutputFileName,requestOutputFileNameHeaders,requestOutputParentDirCreateIfNotExists,requestOutputOverwriteIfExists,
							responseInputMode, responseInputFileName, responseInputFileNameHeaders, responseInputDeleteAfterRead, responseInputWaitTime
							);
				}
				
				// Proxy
				if (endpointtype.equals(TipiConnettore.HTTP.toString()) || endpointtype.equals(TipiConnettore.HTTPS.toString())){
					if (ServletUtils.isCheckBoxEnabled(proxyEnabled)) {
						this.addProxyToDati(dati, proxyHost, proxyPort, proxyUsername, proxyPassword);
					}
				}
				
				// TempiRisposta
				if (endpointtype.equals(TipiConnettore.HTTP.toString()) || endpointtype.equals(TipiConnettore.HTTPS.toString())){
					if (ServletUtils.isCheckBoxEnabled(tempiRisposta_enabled)) {
						this.addTempiRispostaToDati(dati, tempiRisposta_connectionTimeout, tempiRisposta_readTimeout, tempiRisposta_tempoMedioRisposta);
					}
				}
				
				// Extended
				if(listExtendedConnettore!=null && listExtendedConnettore.size()>0){
					ServletExtendedConnettoreUtils.addToDatiExtendedInfo(dati, listExtendedConnettore);
				}
				
				// Opzioni Avanzate
				if (endpointtype.equals(TipiConnettore.HTTP.toString()) || endpointtype.equals(TipiConnettore.HTTPS.toString())){
					this.addOpzioniAvanzateHttpToDati(dati, opzioniAvanzate, transfer_mode, transfer_mode_chunk_size, redirect_mode, redirect_max_hop);
				}
	
			}
		}

		return dati;
	}
	
	public Vector<DataElement> addEndPointSAServerToDatiAsHidden(Vector<DataElement> dati, boolean servizioApplicativoServerEnabled, String servizioApplicativoServer){
		DataElement de = new DataElement();
		de.setType(DataElementType.HIDDEN);
		de.setName(ConnettoriCostanti.PARAMETRO_CONNETTORE_ABILITA_USO_APPLICATIVO_SERVER);
		de.setValue(servizioApplicativoServerEnabled + "");
		dati.addElement(de);
		
		de = new DataElement();
		de.setType(DataElementType.HIDDEN);
		de.setName(ConnettoriCostanti.PARAMETRO_CONNETTORE_ID_APPLICATIVO_SERVER);
		de.setValue(servizioApplicativoServer);
		dati.addElement(de);
		
		return dati;
	}

	public Vector<DataElement> addEndPointToDatiAsHidden(Vector<DataElement> dati,
			String endpointtype, String url, String nome, String tipo,
			String user, String password, String initcont, String urlpgk,
			String provurl, String connfact, String sendas, String objectName, TipoOperazione tipoOperazione,
			String httpsurl, String httpstipologia, boolean httpshostverify,
			String httpspath, String httpstipo, String httpspwd,
			String httpsalgoritmo, boolean httpsstato, String httpskeystore,
			String httpspwdprivatekeytrust, String httpspathkey,
			String httpstipokey, String httpspwdkey,
			String httpspwdprivatekey, String httpsalgoritmokey,
			String httpsKeyAlias, String httpsTrustStoreCRLs,
			String tipoconn, String servletChiamante, String elem1, String elem2, String elem3,
			String elem4, String elem5, String elem6, String elem7, String stato,
			String proxyEnabled, String proxyHost, String proxyPort, String proxyUsername, String proxyPassword,
			String tempiRisposta_enabled, String tempiRisposta_connectionTimeout, String tempiRisposta_readTimeout, String tempiRisposta_tempoMedioRisposta,
			String opzioniAvanzate, String transfer_mode, String transfer_mode_chunk_size, String redirect_mode, String redirect_max_hop,
			String requestOutputFileName,String requestOutputFileNameHeaders,String requestOutputParentDirCreateIfNotExists,String requestOutputOverwriteIfExists,
			String responseInputMode, String responseInputFileName, String responseInputFileNameHeaders, String responseInputDeleteAfterRead, String responseInputWaitTime) {


		Boolean confPers = (Boolean) this.session.getAttribute(CostantiControlStation.SESSION_PARAMETRO_GESTIONE_CONFIGURAZIONI_PERSONALIZZATE);

		TipologiaConnettori tipologiaConnettori = null;
		try {
			tipologiaConnettori = Utilities.getTipologiaConnettori(this.core);
		} catch (Exception e) {
			// default
			tipologiaConnettori = TipologiaConnettori.TIPOLOGIA_CONNETTORI_ALL;
		}

		// override tipologiaconnettori :
		// se standard allora la tipologia connettori e' sempre http
		// indipendentemente
		// dalla proprieta settata
		if (this.isModalitaStandard()) {
			tipologiaConnettori = TipologiaConnettori.TIPOLOGIA_CONNETTORI_HTTP;
		}

		DataElement de = new DataElement();
//		de.setLabel(ConnettoriCostanti.LABEL_CONNETTORE);
//		de.setType(DataElementType.TITLE);
//		dati.addElement(de);


		/** VISUALIZZAZIONE HTTP ONLY MODE */

		if (TipologiaConnettori.TIPOLOGIA_CONNETTORI_HTTP.equals(tipologiaConnettori)) {
			
			de = new DataElement();
			de.setLabel(ConnettoriCostanti.LABEL_CONNETTORE_ABILITATO);

			de.setName(ConnettoriCostanti.PARAMETRO_CONNETTORE_ENDPOINT_TYPE_CHECK);
			if(!TipiConnettore.HTTP.toString().equals(endpointtype) &&
					!TipiConnettore.HTTPS.toString().equals(endpointtype) &&
					!TipiConnettore.DISABILITATO.toString().equals(endpointtype)){
//				de.setLabel(ConnettoriCostanti.LABEL_CONNETTORE_CONNETTORE_IMPOSTATO_MODALITA_AVANZATA);
				de.setType(DataElementType.HIDDEN);
//				de.setValue(" ");
				de.setValue(Costanti.CHECK_BOX_DISABLED);
			}
			else if( (  (AccordiServizioParteSpecificaCostanti.OBJECT_NAME_APS.equals(objectName) && TipoOperazione.CHANGE.equals(tipoOperazione))
					|| 
					(AccordiServizioParteSpecificaCostanti.OBJECT_NAME_APS_FRUITORI.equals(objectName) && TipoOperazione.CHANGE.equals(tipoOperazione))
					)
					&& StatiAccordo.finale.toString().equals(stato) && this.isShowGestioneWorkflowStatoDocumenti() ){
				if (endpointtype.equals(TipiConnettore.HTTP.toString())) {
					de.setType(DataElementType.HIDDEN);
//					de.setSelected(true);
					de.setValue(Costanti.CHECK_BOX_ENABLED);
				}else{
					de.setType(DataElementType.HIDDEN);
					de.setLabel(TipiConnettore.DISABILITATO.toString());
					de.setValue(Costanti.CHECK_BOX_DISABLED);
				}
			}else{
				de.setType(DataElementType.HIDDEN);
				if (endpointtype.equals(TipiConnettore.HTTP.toString())) {
//					de.setSelected(true);
					de.setValue(Costanti.CHECK_BOX_ENABLED);
				}
//				de.setPostBack(true);
				//				de.setOnClick("AbilitaEndPointHTTP(\"" + tipoOp + "\")");
			}
			dati.addElement(de);

			de = new DataElement();
			de.setName(ConnettoriCostanti.PARAMETRO_CONNETTORE_ENDPOINT_TYPE);
			if (endpointtype.equals(TipiConnettore.HTTP.toString())) {
				de.setValue(TipiConnettore.HTTP.toString());
			}
			else if (endpointtype.equals(TipiConnettore.HTTPS.toString())) {
				de.setValue(TipiConnettore.HTTPS.toString());
			}else {
				de.setValue(TipiConnettore.DISABILITATO.toString());
			}
			de.setType(DataElementType.HIDDEN);
			dati.addElement(de);

			de = new DataElement();
			de.setLabel(ConnettoriCostanti.LABEL_PARAMETRO_CONNETTORE_URL);
			String defaultPrefixValue = "http://";
			if (endpointtype.equals(TipiConnettore.HTTPS.toString())) {
				defaultPrefixValue = "https://";
			}
			de.setValue((url != null) && !"".equals(url) && !"http://".equals(url) && !"https://".equals(url) ? url : defaultPrefixValue);
			de.setType(DataElementType.HIDDEN);
			de.setName(ConnettoriCostanti.PARAMETRO_CONNETTORE_URL);
			dati.addElement(de);

		} else {

			/** VISUALIZZAZIONE COMPLETA CONNETTORI MODE */


			int sizeEP = Connettori.getList().size();
			if (!Connettori.getList().contains(TipiConnettore.HTTPS.toString()))
				sizeEP++;
			if (confPers &&
					TipologiaConnettori.TIPOLOGIA_CONNETTORI_ALL.equals(tipologiaConnettori))
				sizeEP++;
			String[] tipoEP = new String[sizeEP];
			Connettori.getList().toArray(tipoEP);
			int newCount = Connettori.getList().size();
			if (!Connettori.getList().contains(TipiConnettore.HTTPS.toString())) {
				tipoEP[newCount] = TipiConnettore.HTTPS.toString();
				newCount++;
			}
			if (confPers &&
					TipologiaConnettori.TIPOLOGIA_CONNETTORI_ALL.equals(tipologiaConnettori))
				tipoEP[newCount] = TipiConnettore.CUSTOM.toString();
			//String[] tipoEP = { TipiConnettore.DISABILITATO.toString(), TipiConnettore.HTTP.toString(), TipiConnettore.JMS.toString(), TipiConnettore.NULL.toString(), TipiConnettore.NULL_ECHO.toString() };

			de = new DataElement();
			de.setLabel(ConnettoriCostanti.LABEL_PARAMETRO_CONNETTORE_ENDPOINT_TYPE);
			de.setType(DataElementType.HIDDEN);
			de.setName(ConnettoriCostanti.PARAMETRO_CONNETTORE_ENDPOINT_TYPE);
//			de.setValues(tipoEP);
			de.setValue(endpointtype);
			//		    de.setOnChange("CambiaEndPoint('" + tipoOp + "')");
//			de.setPostBack(true);
			dati.addElement(de);

			de = new DataElement();
			de.setLabel(ConnettoriCostanti.LABEL_PARAMETRO_CONNETTORE_URL);
			de.setValue(url);
//			if (endpointtype.equals(TipiConnettore.HTTP.toString())){
//				if (!this.isShowGestioneWorkflowStatoDocumenti() || !StatiAccordo.finale.toString().equals(stato)) {
//					de.setType(DataElementType.TEXT_EDIT);
//					de.setRequired(true);
//				} else {
//					de.setType(DataElementType.TEXT);
//				}
//			}
//			else{
				de.setType(DataElementType.HIDDEN);
//		}
			de.setName(ConnettoriCostanti.PARAMETRO_CONNETTORE_URL);
			de.setSize(this.getSize());
			dati.addElement(de);

			de = new DataElement();
			de.setLabel(ConnettoriCostanti.LABEL_PARAMETRO_CONNETTORE_TIPO_PERSONALIZZATO);
//			if (endpointtype == null || !endpointtype.equals(TipiConnettore.CUSTOM.toString()))
				de.setType(DataElementType.HIDDEN);
//			else{
//				de.setType(DataElementType.TEXT_EDIT);
//				de.setRequired(true);
//			}
			de.setName(ConnettoriCostanti.PARAMETRO_CONNETTORE_TIPO_PERSONALIZZATO);
			de.setValue(tipoconn);
			dati.addElement(de);

//			if (endpointtype != null && endpointtype.equals(TipiConnettore.CUSTOM.toString()) &&
//					!servletChiamante.equals(AccordiServizioParteSpecificaCostanti.SERVLET_NAME_APS_ADD) &&
//					!servletChiamante.equals(AccordiServizioParteSpecificaCostanti.SERVLET_NAME_APS_FRUITORI_ADD)) {
//				 Eliminati Link non dovrebbero servire... eventualmente riportare da su
//			}
		}

		if (endpointtype.equals(TipiConnettore.HTTPS.toString())) {
			ConnettoreHTTPSUtils.addHTTPSDatiAsHidden(dati, httpsurl, httpstipologia, httpshostverify, httpspath, httpstipo, httpspwd, 
					httpsalgoritmo, httpsstato, httpskeystore, httpspwdprivatekeytrust, httpspathkey, httpstipokey, httpspwdkey, httpspwdprivatekey, httpsalgoritmokey, 
					httpsKeyAlias, httpsTrustStoreCRLs,
					stato,
					this.core,this.getSize());
			
		}

		if (endpointtype.equals(TipiConnettore.JMS.getNome())) {
			ConnettoreJMSUtils.addJMSDatiAsHidden(dati, nome, tipoconn, user, password, initcont, urlpgk, 
					provurl, connfact, sendas, objectName, tipoOperazione, stato,
					this.core,this.getSize());
		}

		if (endpointtype.equals(TipiConnettore.FILE.toString())) {
			ConnettoreFileUtils.addFileDatiAsHidden(dati, 
					requestOutputFileName,requestOutputFileNameHeaders,requestOutputParentDirCreateIfNotExists,requestOutputOverwriteIfExists,
					responseInputMode, responseInputFileName, responseInputFileNameHeaders, responseInputDeleteAfterRead, responseInputWaitTime);
			
			
		}
		
		// Proxy
		if (endpointtype.equals(TipiConnettore.HTTP.toString()) || endpointtype.equals(TipiConnettore.HTTPS.toString())){
			this.addProxyToDatiAsHidden(dati, proxyEnabled, proxyHost, proxyPort, proxyUsername, proxyPassword);
		}
		
		// TempiRisposta
		if (endpointtype.equals(TipiConnettore.HTTP.toString()) || endpointtype.equals(TipiConnettore.HTTPS.toString())){
			this.addTempiRispostaToDatiAsHidden(dati, tempiRisposta_enabled, tempiRisposta_connectionTimeout, tempiRisposta_readTimeout, tempiRisposta_tempoMedioRisposta);
		}
		
		// Opzioni Avanzate
		if (endpointtype.equals(TipiConnettore.HTTP.toString()) || endpointtype.equals(TipiConnettore.HTTPS.toString())){
			this.addOpzioniAvanzateHttpToDatiAsHidden(dati, opzioniAvanzate, transfer_mode, transfer_mode_chunk_size, redirect_mode, redirect_max_hop);
		}
		
		return dati;
	}




	// Controlla i dati dell'end-point
	public boolean endPointCheckData(String protocollo, boolean servizioApplicativo, List<ExtendedConnettore> listExtendedConnettore) throws Exception {
		try {

			//String endpointtype = this.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_ENDPOINT_TYPE);
			String endpointtype = this.readEndPointType();
			String tipoconn = this.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_TIPO_PERSONALIZZATO);
			String autenticazioneHttp = this.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_ENDPOINT_TYPE_ENABLE_HTTP);
			
			String autenticazioneTokenS = this.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_TOKEN_POLICY_STATO);
			boolean autenticazioneToken = ServletUtils.isCheckBoxEnabled(autenticazioneTokenS);
			String token_policy = this.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_TOKEN_POLICY);
			
			// proxy
			String proxy_enabled = this.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_PROXY_ENABLED);
			String proxy_hostname = this.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_PROXY_HOSTNAME);
			String proxy_port = this.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_PROXY_PORT);
			String proxy_username = this.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_PROXY_USERNAME);
			String proxy_password = this.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_PROXY_PASSWORD);
			
			// tempiRisposta
			String tempiRisposta_enabled = this.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_TEMPI_RISPOSTA_REDEFINE);
			String tempiRisposta_connectionTimeout = this.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_TEMPI_RISPOSTA_CONNECTION_TIMEOUT);
			String tempiRisposta_readTimeout = this.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_TEMPI_RISPOSTA_READ_TIMEOUT);
			String tempiRisposta_tempoMedioRisposta = this.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_TEMPI_RISPOSTA_TEMPO_MEDIO_RISPOSTA);

			// opzioni avanzate
			String transfer_mode = this.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_OPZIONI_AVANZATE_TRANSFER_MODE);
			String transfer_mode_chunk_size = this.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_OPZIONI_AVANZATE_TRANSFER_CHUNK_SIZE);
			String redirect_mode = this.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_OPZIONI_AVANZATE_REDIRECT_MODE);
			String redirect_max_hop = this.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_OPZIONI_AVANZATE_REDIRECT_MAX_HOP);
			String opzioniAvanzate = getOpzioniAvanzate(this,transfer_mode, redirect_mode);
						
			// http
			String url = this.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_URL);

			// jms
			String nome = this.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_JMS_NOME_CODA);
			String tipo = this.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_JMS_TIPO_CODA);
			String user = this.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_JMS_USERNAME);
			String password = this.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_JMS_PASSWORD);
			String initcont = this.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_JMS_INIT_CTX);
			String urlpgk = this.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_JMS_URL_PKG);
			String provurl = this.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_JMS_PROVIDER_URL);
			String connfact = this.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_JMS_CONNECTION_FACTORY);
			String sendas = this.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_JMS_TIPO_OGGETTO_JMS);

			// https
			String httpsurl = url;
			String httpstipologia = this.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_HTTPS_SSL_TYPE);
			String httpshostverifyS = this.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_HTTPS_HOST_VERIFY);
			boolean httpshostverify = ServletUtils.isCheckBoxEnabled(httpshostverifyS);
			String httpspath = this.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_HTTPS_TRUST_STORE_LOCATION);
			String httpstipo = this.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_HTTPS_TRUST_STORE_TYPE);
			String httpspwd = this.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_HTTPS_TRUST_STORE_PASSWORD);
			String httpsalgoritmo = this.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_HTTPS_TRUST_MANAGEMENT_ALGORITM);
			String httpsstatoS = this.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_HTTPS_STATO);
			boolean httpsstato = ServletUtils.isCheckBoxEnabled(httpsstatoS);
			String httpskeystore = this.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_HTTPS_KEYSTORE_CLIENT_AUTH_MODE);
			String httpspwdprivatekeytrust = this.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_HTTPS_PASSWORD_PRIVATE_KEY_STORE);
			String httpspathkey = this.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_HTTPS_KEY_STORE_LOCATION);
			String httpstipokey = this.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_HTTPS_KEY_STORE_TYPE);
			String httpspwdkey = this.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_HTTPS_KEY_STORE_PASSWORD);
			String httpspwdprivatekey = this.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_HTTPS_PASSWORD_PRIVATE_KEY_KEYSTORE);
			String httpsalgoritmokey = this.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_HTTPS_KEY_MANAGEMENT_ALGORITM);
			String httpsKeyAlias = this.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_HTTPS_ALIAS_PRIVATE_KEY_KEYSTORE);
			String httpsTrustStoreCRLs = this.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_HTTPS_TRUST_STORE_CRL);
			
			if(ServletUtils.isCheckBoxEnabled(autenticazioneHttp)){
				user = this.getParameter(ConnettoriCostanti.PARAMETRO_INVOCAZIONE_CREDENZIALI_AUTENTICAZIONE_USERNAME);
				password = this.getParameter(ConnettoriCostanti.PARAMETRO_INVOCAZIONE_CREDENZIALI_AUTENTICAZIONE_PASSWORD);
			}
			
			// file
			String requestOutputFileName = this.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_FILE_REQUEST_OUTPUT_FILE_NAME);
			String requestOutputFileNameHeaders = this.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_FILE_REQUEST_OUTPUT_FILE_NAME_HEADERS);
			String requestOutputParentDirCreateIfNotExists = this.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_FILE_REQUEST_OUTPUT_AUTO_CREATE_DIR);
			String requestOutputOverwriteIfExists = this.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_FILE_REQUEST_OUTPUT_OVERWRITE_FILE_NAME);
			String responseInputMode = this.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_FILE_RESPONSE_INPUT_MODE);
			String responseInputFileName = this.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_FILE_RESPONSE_INPUT_FILE_NAME);
			String responseInputFileNameHeaders = this.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_FILE_RESPONSE_INPUT_FILE_NAME_HEADERS);
			String responseInputDeleteAfterRead = this.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_FILE_RESPONSE_INPUT_FILE_NAME_DELETE_AFTER_READ);
			String responseInputWaitTime = this.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_FILE_RESPONSE_INPUT_WAIT_TIME);
			
			
			String servizioApplicativoServerEnabledS = this.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_ABILITA_USO_APPLICATIVO_SERVER);
			boolean servizioApplicativoServerEnabled = ServletUtils.isCheckBoxEnabled(servizioApplicativoServerEnabledS);
			String servizioApplicativoServer = this.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_ID_APPLICATIVO_SERVER);
			
			return endPointCheckData(protocollo, servizioApplicativo, endpointtype, url, nome, tipo, user,
					password, initcont, urlpgk, provurl, connfact, sendas,
					httpsurl, httpstipologia, httpshostverify,
					httpspath, httpstipo, httpspwd, httpsalgoritmo, httpsstato,
					httpskeystore, httpspwdprivatekeytrust, httpspathkey,
					httpstipokey, httpspwdkey, 
					httpspwdprivatekey, httpsalgoritmokey,
					httpsKeyAlias, httpsTrustStoreCRLs,
					tipoconn, autenticazioneHttp,
					proxy_enabled,proxy_hostname,proxy_port,proxy_username,proxy_password,
					tempiRisposta_enabled, tempiRisposta_connectionTimeout, tempiRisposta_readTimeout, tempiRisposta_tempoMedioRisposta,
					opzioniAvanzate, transfer_mode, transfer_mode_chunk_size, redirect_mode, redirect_max_hop,
					requestOutputFileName,requestOutputFileNameHeaders,requestOutputParentDirCreateIfNotExists,requestOutputOverwriteIfExists,
					responseInputMode, responseInputFileName, responseInputFileNameHeaders, responseInputDeleteAfterRead, responseInputWaitTime,
					autenticazioneToken, token_policy,
					listExtendedConnettore,servizioApplicativoServerEnabled, servizioApplicativoServer);
			
		} catch (Exception e) {
			this.log.error("Exception: " + e.getMessage(), e);
			throw new Exception(e);
		}
	}

	// Controlla i dati dell'end-point
	public boolean endPointCheckData(String protocollo, boolean servizioApplicativo,
			String endpointtype, String url, String nome,
			String tipo, String user, String password, String initcont,
			String urlpgk, String provurl, String connfact, String sendas,
			String httpsurl, String httpstipologia, boolean httpshostverify,
			String httpspath, String httpstipo, String httpspwd,
			String httpsalgoritmo, boolean httpsstato, String httpskeystore,
			String httpspwdprivatekeytrust, String httpspathkey,
			String httpstipokey, String httpspwdkey,
			String httpspwdprivatekey, String httpsalgoritmokey,
			String httpsKeyAlias, String httpsTrustStoreCRLs,
			String tipoconn, String autenticazioneHttp,
			String proxy_enabled, String proxy_hostname, String proxy_port, String proxy_username, String proxy_password,
			String tempiRisposta_enabled, String tempiRisposta_connectionTimeout, String tempiRisposta_readTimeout, String tempiRisposta_tempoMedioRisposta,
			String opzioniAvanzate, String transfer_mode, String transfer_mode_chunk_size, String redirect_mode, String redirect_max_hop,
			String requestOutputFileName,String requestOutputFileNameHeaders,String requestOutputParentDirCreateIfNotExists,String requestOutputOverwriteIfExists,
			String responseInputMode, String responseInputFileName, String responseInputFileNameHeaders, String responseInputDeleteAfterRead, String responseInputWaitTime,
			boolean autenticazioneToken, String tokenPolicy,
			List<ExtendedConnettore> listExtendedConnettore,boolean servizioApplicativoServerEnabled, String servizioApplicativoServer)
					throws Exception {
		try{
			if(!servizioApplicativoServerEnabled) {	
				if (url == null)
					url = "";
				if (nome == null)
					nome = "";
				if (tipo == null)
					tipo = "";
				if (user == null)
					user = "";
				if (password == null)
					password = "";
				if (initcont == null)
					initcont = "";
				if (urlpgk == null)
					urlpgk = "";
				if (provurl == null)
					provurl = "";
				if (connfact == null)
					connfact = "";
				if (sendas == null)
					sendas = "";
				if (httpsurl == null)
					httpsurl = "";
				if (httpstipologia == null)
					httpstipologia = "";
				if (httpspath == null)
					httpspath = "";
				if (httpstipo == null)
					httpstipo = "";
				if (httpspwd == null)
					httpspwd = "";
				if (httpsalgoritmo == null)
					httpsalgoritmo = "";
				if (httpskeystore == null)
					httpskeystore = "";
				if (httpspwdprivatekeytrust == null)
					httpspwdprivatekeytrust = "";
				if (httpspathkey == null)
					httpspathkey = "";
				if (httpstipokey == null)
					httpstipokey = "";
				if (httpspwdkey == null)
					httpspwdkey = "";
				if (httpspwdprivatekey == null)
					httpspwdprivatekey = "";
				if (httpsalgoritmokey == null)
					httpsalgoritmokey = "";
				if (httpsKeyAlias == null)
					httpsKeyAlias = "";
				if (httpsTrustStoreCRLs == null)
					httpsTrustStoreCRLs = "";
				if (tipoconn == null)
					tipoconn = "";	
				if (proxy_enabled == null)
					proxy_enabled = "";
				if (proxy_hostname == null)
					proxy_hostname = "";
				if (proxy_port == null)
					proxy_port = "";
				if (proxy_username == null)
					proxy_username = "";
				if (proxy_password == null)
					proxy_password = "";
				if (tempiRisposta_enabled == null)
					tempiRisposta_enabled = "";
				if (tempiRisposta_connectionTimeout == null)
					tempiRisposta_connectionTimeout = "";
				if (tempiRisposta_readTimeout == null)
					tempiRisposta_readTimeout = "";
				if (tempiRisposta_tempoMedioRisposta == null)
					tempiRisposta_tempoMedioRisposta = "";
				if (transfer_mode == null)
					transfer_mode = "";
				if (transfer_mode_chunk_size == null)
					transfer_mode_chunk_size = "";
				if (redirect_mode == null)
					redirect_mode = "";
				if (redirect_max_hop == null)
					redirect_max_hop = "";			
				if (requestOutputFileName == null)
					requestOutputFileName = "";
				if (requestOutputFileNameHeaders == null)
					requestOutputFileNameHeaders = "";
				if (responseInputFileName == null)
					responseInputFileName = "";
				if (responseInputFileNameHeaders == null)
					responseInputFileNameHeaders = "";
				if (responseInputWaitTime == null)
					responseInputWaitTime = "";
				
				// Controllo che non ci siano spazi nei campi di testo
				if (
						(nome.indexOf(" ") != -1) ||
						(user.indexOf(" ") != -1) || 
						(password.indexOf(" ") != -1) || 
						(initcont.indexOf(" ") != -1) || 
						(urlpgk.indexOf(" ") != -1) || 
						(provurl.indexOf(" ") != -1) || 
						(connfact.indexOf(" ") != -1) ||
						(httpspath.indexOf(" ") != -1) ||
						(httpspwd.indexOf(" ") != -1) ||
						(httpsalgoritmo.indexOf(" ") != -1) ||
						(httpskeystore.indexOf(" ") != -1) ||
						(httpspwdprivatekeytrust.indexOf(" ") != -1) ||
						(httpspathkey.indexOf(" ") != -1) ||
						(httpspwdkey.indexOf(" ") != -1) ||
						(httpspwdprivatekey.indexOf(" ") != -1) ||
						(httpsalgoritmokey.indexOf(" ") != -1) ||
						(httpsKeyAlias.indexOf(" ") != -1) ||
						(tipoconn.indexOf(" ") != -1) ||
						(proxy_hostname.indexOf(" ") != -1) ||
						(proxy_port.indexOf(" ") != -1) ||
						(proxy_username.indexOf(" ") != -1) ||
						(proxy_password.indexOf(" ") != -1) ||
						(tempiRisposta_connectionTimeout.indexOf(" ") != -1) ||
						(tempiRisposta_readTimeout.indexOf(" ") != -1) ||
						(tempiRisposta_tempoMedioRisposta.indexOf(" ") != -1) ||
						(transfer_mode.indexOf(" ") != -1) ||
						(transfer_mode_chunk_size.indexOf(" ") != -1) ||
						(redirect_mode.indexOf(" ") != -1) ||
						(redirect_max_hop.indexOf(" ") != -1) ||
						(requestOutputFileName.indexOf(" ") != -1) ||
						(requestOutputFileNameHeaders.indexOf(" ") != -1) ||
						(responseInputFileName.indexOf(" ") != -1) ||
						(responseInputFileNameHeaders.indexOf(" ") != -1) ||
						(responseInputWaitTime.indexOf(" ") != -1) 
						) {
					this.pd.setMessage("Non inserire spazi nei campi di testo");
					return false;
				}
	
				if(url.startsWith(" ") || url.endsWith(" ")) {
					this.pd.setMessage(ConnettoriCostanti.LABEL_PARAMETRO_CONNETTORE_URL+" non deve iniziare o terminare con uno spazio");
					return false;
				}
				if(httpsurl.startsWith(" ") || httpsurl.endsWith(" ")) {
					this.pd.setMessage(ConnettoriCostanti.LABEL_PARAMETRO_CONNETTORE_URL+" non deve iniziare o terminare con uno spazio");
					return false;
				}
				if(url.indexOf(" ") != -1) {
					int indexOfSpace = url.indexOf(" ");
					int indexOfParameterSeparator = url.indexOf("?");
					if(indexOfParameterSeparator<=0) {
						this.pd.setMessage(ConnettoriCostanti.LABEL_PARAMETRO_CONNETTORE_URL+" non può contenere degli spazi");
						return false;
					}
					else if(indexOfSpace<indexOfParameterSeparator) {
						this.pd.setMessage(ConnettoriCostanti.LABEL_PARAMETRO_CONNETTORE_URL+" può contenere degli spazi solamente nei parametri");
						return false;
					}
				}
				if(httpsurl.indexOf(" ") != -1) {
					int indexOfSpace = httpsurl.indexOf(" ");
					int indexOfParameterSeparator = httpsurl.indexOf("?");
					if(indexOfParameterSeparator<=0) {
						this.pd.setMessage(ConnettoriCostanti.LABEL_PARAMETRO_CONNETTORE_URL+" non può contenere degli spazi");
						return false;
					}
					else if(indexOfSpace<indexOfParameterSeparator) {
						this.pd.setMessage(ConnettoriCostanti.LABEL_PARAMETRO_CONNETTORE_URL+" può contenere degli spazi solamente nei parametri");
						return false;
					}
				}
				
				if(ServletUtils.isCheckBoxEnabled(proxy_enabled)){
					
					if (proxy_hostname == null || "".equals(proxy_hostname)) {
						this.pd.setMessage(ConnettoriCostanti.LABEL_PARAMETRO_CONNETTORE_PROXY_HOSTNAME+" obbligatorio se si seleziona la comunicazione tramite Proxy");
						return false;
					}
					if (proxy_port == null || "".equals(proxy_port)) {
						this.pd.setMessage(ConnettoriCostanti.LABEL_PARAMETRO_CONNETTORE_PROXY_PORT+" obbligatorio se si seleziona la comunicazione tramite Proxy");
						return false;
					}
					int value = -1;
					try{
						value = Integer.parseInt(proxy_port);
						if(value<1 || value>65535){
							this.pd.setMessage(ConnettoriCostanti.LABEL_PARAMETRO_CONNETTORE_PROXY_PORT+" indicata per il Proxy deve essere un intero compreso tra 1 e  65.535");
							return false;
						}
					}catch(Exception e){
						this.pd.setMessage(ConnettoriCostanti.LABEL_PARAMETRO_CONNETTORE_PROXY_PORT+" indicata per il Proxy deve essere un numero intero: "+e.getMessage());
						return false;
					}
					
					try{
						new InetSocketAddress(proxy_hostname,value);
					}catch(Exception e){
						this.pd.setMessage(ConnettoriCostanti.LABEL_PARAMETRO_CONNETTORE_PROXY_HOSTNAME+" e "+
								ConnettoriCostanti.LABEL_PARAMETRO_CONNETTORE_PROXY_PORT+" indicati per il Proxy non sono corretti: "+e.getMessage());
						return false;
					}
					
					// Check lunghezza
					if(this.checkLength255(proxy_hostname, ConnettoriCostanti.LABEL_PARAMETRO_CONNETTORE_PROXY_HOSTNAME)==false) {
						return false;
					}
					if(proxy_username!=null && !"".equals(proxy_username)) {
						if(this.checkLength255(proxy_username, ConnettoriCostanti.LABEL_PARAMETRO_CONNETTORE_PROXY_USERNAME)==false) {
							return false;
						}
					}
					if(proxy_password!=null && !"".equals(proxy_password)) {
						if(this.checkLength255(proxy_password, ConnettoriCostanti.LABEL_PARAMETRO_CONNETTORE_PROXY_PASSWORD)==false) {
							return false;
						}
					}
				}
				
				if(ServletUtils.isCheckBoxEnabled(tempiRisposta_enabled)){
					
					try{
						int v = Integer.parseInt(tempiRisposta_connectionTimeout);
						if(v<=0) {
							throw new Exception("fornire un valore maggiore di zero");
						}
					}catch(Exception e){
						this.pd.setMessage(ConnettoriCostanti.LABEL_PARAMETRO_CONNETTORE_TEMPI_RISPOSTA_CONNECTION_TIMEOUT+" indicato nella sezione '"+
								ConnettoriCostanti.LABEL_CONNETTORE_TEMPI_RISPOSTA+"' deve essere un numero intero maggiore di zero");
						return false;
					}
					
					try{
						int v = Integer.parseInt(tempiRisposta_readTimeout);
						if(v<=0) {
							throw new Exception("fornire un valore maggiore di zero");
						}
					}catch(Exception e){
						this.pd.setMessage(ConnettoriCostanti.LABEL_PARAMETRO_CONNETTORE_TEMPI_RISPOSTA_READ_TIMEOUT+" indicato nella sezione '"+
								ConnettoriCostanti.LABEL_CONNETTORE_TEMPI_RISPOSTA+"' deve essere un numero intero maggiore di zero");
						return false;
					}
					
					try{
						int v = Integer.parseInt(tempiRisposta_tempoMedioRisposta);
						if(v<=0) {
							throw new Exception("fornire un valore maggiore di zero");
						}
					}catch(Exception e){
						this.pd.setMessage(ConnettoriCostanti.LABEL_PARAMETRO_CONNETTORE_TEMPI_RISPOSTA_TEMPO_MEDIO_RISPOSTA+" indicato nella sezione '"+
								ConnettoriCostanti.LABEL_CONNETTORE_TEMPI_RISPOSTA+"' deve essere un numero intero maggiore di zero");
						return false;
					}
					
				}
				
				if(ServletUtils.isCheckBoxEnabled(opzioniAvanzate)){
					
					if(TransferLengthModes.TRANSFER_ENCODING_CHUNKED.getNome().equals(transfer_mode)){
						if(transfer_mode_chunk_size!=null && !"".equals(transfer_mode_chunk_size)){
							int value = -1;
							try{
								value = Integer.parseInt(transfer_mode_chunk_size);
								if(value<1){
									this.pd.setMessage("Il valore indicato nel parametro '"+ConnettoriCostanti.LABEL_PARAMETRO_CONNETTORE_OPZIONI_AVANZATE_TRANSFER_CHUNK_SIZE+
											"' indicato per la modalità "+TransferLengthModes.TRANSFER_ENCODING_CHUNKED.getNome()+" deve essere un numero maggiore di zero.");
									return false;
								}
							}catch(Exception e){
								this.pd.setMessage("Il valore indicato nel parametro '"+ConnettoriCostanti.LABEL_PARAMETRO_CONNETTORE_OPZIONI_AVANZATE_TRANSFER_CHUNK_SIZE+
										"' indicato per la modalità "+TransferLengthModes.TRANSFER_ENCODING_CHUNKED.getNome()+" deve essere un numero intero: "+e.getMessage());
								return false;
							}
						}
					}
					
					if(CostantiConfigurazione.ABILITATO.getValue().equals(redirect_mode)){
						if(redirect_max_hop!=null && !"".equals(redirect_max_hop)){
							int value = -1;
							try{
								value = Integer.parseInt(redirect_max_hop);
								if(value<1){
									this.pd.setMessage("Il valore indicato nel parametro '"+ConnettoriCostanti.LABEL_PARAMETRO_CONNETTORE_OPZIONI_AVANZATE_REDIRECT_MAX_HOP+
											"' deve essere un numero maggiore di zero.");
									return false;
								}
							}catch(Exception e){
								this.pd.setMessage("Il valore indicato nel parametro '"+ConnettoriCostanti.LABEL_PARAMETRO_CONNETTORE_OPZIONI_AVANZATE_REDIRECT_MAX_HOP+
										"' deve essere un numero intero: "+e.getMessage());
								return false;
							}
						}
					}
					
					
				}
				
				if(ServletUtils.isCheckBoxEnabled(autenticazioneHttp)){
					if (user == null || "".equals(user)) {
						this.pd.setMessage("Username obbligatoria per l'autenticazione http");
						return false;
					}
					if (password == null || "".equals(password)) {
						this.pd.setMessage("Password obbligatoria per l'autenticazione http");
						return false;
					}
					if(this.checkLength255(user, ConnettoriCostanti.LABEL_PARAMETRO_CREDENZIALI_AUTENTICAZIONE_USERNAME)==false) {
						return false;
					}
					if(this.checkLength255(password, ConnettoriCostanti.LABEL_PARAMETRO_CREDENZIALI_AUTENTICAZIONE_PASSWORD)==false) {
						return false;
					}
				}
	
				if(autenticazioneToken) {
					if (tokenPolicy == null || "".equals(tokenPolicy) || CostantiControlStation.DEFAULT_VALUE_NON_SELEZIONATO.equals(tokenPolicy)) {
						this.pd.setMessage("Policy obbligatoria per l'autenticazione basata su token");
						return false;
					}
					
					List<GenericProperties> gestorePolicyTokenList = this.confCore.gestorePolicyTokenList(null, ConfigurazioneCostanti.DEFAULT_VALUE_PARAMETRO_CONFIGURAZIONE_GESTORE_POLICY_TOKEN_TIPOLOGIA_RETRIEVE_POLICY_TOKEN, null);
					List<String> policyValues = new ArrayList<>();
					for (int i = 0; i < gestorePolicyTokenList.size(); i++) {
						GenericProperties genericProperties = gestorePolicyTokenList.get(i);
						policyValues.add(genericProperties.getNome());
					}
					
					if(!policyValues.contains(tokenPolicy)) {
						this.pd.setMessage("Policy indicata per l'autenticazione basata su token, non esiste");
						return false;
					}
				}
				
				// Controllo che i campi "select" abbiano uno dei valori ammessi
				if (!Connettori.contains(endpointtype) && !endpointtype.equals(TipiConnettore.CUSTOM.toString())) {
					this.pd.setMessage("Tipo Connettore dev'essere uno tra : " + Connettori.getList());
					return false;
				}
	
				List<String> tipologie = null;
				try{
					tipologie = SSLUtilities.getSSLSupportedProtocols();
				}catch(Exception e){
					ControlStationCore.logError(e.getMessage(), e);
					tipologie = SSLUtilities.getAllSslProtocol();
				}
				if(!tipologie.contains(SSLConstants.PROTOCOL_TLS)){
					tipologie.add(SSLConstants.PROTOCOL_TLS); // retrocompatibilita'
				}
				if(!tipologie.contains(SSLConstants.PROTOCOL_SSL)){
					tipologie.add(SSLConstants.PROTOCOL_SSL); // retrocompatibilita'
				}
				if (!httpstipologia.equals("") &&
						!tipologie.contains(httpstipologia)) {
					this.pd.setMessage("Il campo Tipologia può assumere uno tra i seguenti valori: "+tipologie);
					return false;
				}
	
				if (!httpstipo.equals("") &&
						!Utilities.contains(httpstipo, ConnettoriCostanti.TIPOLOGIE_KEYSTORE)) {
					this.pd.setMessage("Il campo Tipo per l'Autenticazione Server può assumere uno tra i seguenti valori: "+Utilities.toString(ConnettoriCostanti.TIPOLOGIE_KEYSTORE, ","));
					return false;
				}
				if (!httpstipokey.equals("") &&
						!Utilities.contains(httpstipokey, ConnettoriCostanti.TIPOLOGIE_KEYSTORE)) {
					this.pd.setMessage("Il campo Tipo per l'Autenticazione Client può assumere uno tra i seguenti valori: "+Utilities.toString(ConnettoriCostanti.TIPOLOGIE_KEYSTORE, ","));
					return false;
				}
	
				// Controllo campi obbligatori per il tipo di connettore custom
				if (endpointtype.equals(TipiConnettore.CUSTOM.toString()) && (tipoconn == null || "".equals(tipoconn))) {
					this.pd.setMessage(ConnettoriCostanti.LABEL_PARAMETRO_CONNETTORE_TIPO_PERSONALIZZATO+" obbligatorio per il tipo di connettore custom");
					return false;
				}
				if (endpointtype.equals(TipiConnettore.CUSTOM.toString())) {
					if(this.checkLength255(tipoconn, ConnettoriCostanti.LABEL_PARAMETRO_CONNETTORE_TIPO_PERSONALIZZATO)==false) {
						return false;
					}
				}
	
				// Controllo campi obbligatori per il tipo di connettore http
				if (endpointtype.equals(TipiConnettore.HTTP.toString()) && (url == null || "".equals(url))) {
					this.pd.setMessage("Url obbligatoria per il tipo di connettore http");
					return false;
				}
	
				// Se il tipo di connettore è custom, tipoconn non può essere
				if (endpointtype.equals(TipiConnettore.CUSTOM.toString()) && (tipoconn.equals(TipiConnettore.HTTP.toString()) || tipoconn.equals(TipiConnettore.HTTPS.toString()) || tipoconn.equals(TipiConnettore.JMS.toString()) || tipoconn.equals(TipiConnettore.NULL.toString()) || tipoconn.equals(TipiConnettore.NULLECHO.toString()) || tipoconn.equals(TipiConnettore.DISABILITATO.toString()) )) {
					this.pd.setMessage(ConnettoriCostanti.LABEL_PARAMETRO_CONNETTORE_TIPO_PERSONALIZZATO+" non può assumere i valori: disabilitato,http,https,jms,null,nullEcho");
					return false;
				}
	
				// Se e' stata specificata la url, dev'essere valida
				if (endpointtype.equals(TipiConnettore.HTTP.toString()) && !url.equals("") ){
					try{
						org.openspcoop2.utils.regexp.RegExpUtilities.validateUrl(url);
					}catch(Exception e){
						this.pd.setMessage("Url non correttamente formata: "+e.getMessage());
						return false;
					}
					if(this.checkLength255(url, ConnettoriCostanti.LABEL_PARAMETRO_CONNETTORE_URL)==false) {
						return false;
					}
					if(this.isProfiloModIPA(protocollo) && !servizioApplicativo) {
						if(!httpsurl.toLowerCase().trim().startsWith("https://")) {
							this.pd.setMessage("Il profilo richiede una url con prefisso https://");
							return false;
						}
					}
				}
	
				// Controllo campi obbligatori per il tipo di connettore jms
				if (endpointtype.equals(TipiConnettore.JMS.toString())) {
					if (nome == null || "".equals(nome)) {
						this.pd.setMessage("Nome della coda/topic obbligatorio per il tipo di connettore jms");
						return false;
					}
					if (tipo == null || "".equals(tipo)) {
						this.pd.setMessage("Tipo di coda obbligatorio per il tipo di connettore jms");
						return false;
					}
					if (sendas == null || "".equals(sendas)) {
						this.pd.setMessage("Tipo di messaggio (SendAs) obbligatorio per il tipo di connettore jms");
						return false;
					}
					if (connfact == null || "".equals(connfact)) {
						this.pd.setMessage("Connection Factory obbligatoria per il tipo di connettore jms");
						return false;
					}
					
					if(this.checkLength255(nome, ConnettoriCostanti.LABEL_PARAMETRO_CONNETTORE_JMS_NOME_CODA)==false) {
						return false;
					}
					if(this.checkLength255(connfact, ConnettoriCostanti.LABEL_PARAMETRO_CONNETTORE_JMS_CONNECTION_FACTORY)==false) {
						return false;
					}
					if(user!=null && !"".equals(user)) {
						if(this.checkLength255(user, ConnettoriCostanti.LABEL_PARAMETRO_CONNETTORE_JMS_USERNAME)==false) {
							return false;
						}
					}
					if(password!=null && !"".equals(password)) {
						if(this.checkLength255(password, ConnettoriCostanti.LABEL_PARAMETRO_CONNETTORE_JMS_PASSWORD)==false) {
							return false;
						}
					}
					if(initcont!=null && !"".equals(initcont)) {
						if(this.checkLength255(initcont, ConnettoriCostanti.LABEL_PARAMETRO_CONNETTORE_JMS_INIT_CTX)==false) {
							return false;
						}
					}
					if(urlpgk!=null && !"".equals(urlpgk)) {
						if(this.checkLength255(urlpgk, ConnettoriCostanti.LABEL_PARAMETRO_CONNETTORE_JMS_URL_PKG)==false) {
							return false;
						}
					}
					if(provurl!=null && !"".equals(provurl)) {
						if(this.checkLength255(provurl, ConnettoriCostanti.LABEL_PARAMETRO_CONNETTORE_JMS_PROVIDER_URL)==false) {
							return false;
						}
					}
				}
	
				if (endpointtype.equals(TipiConnettore.JMS.toString()) && !Utilities.contains(tipo, ConnettoriCostanti.TIPI_CODE_JMS)) {
					this.pd.setMessage("Tipo Jms dev'essere: "+Utilities.toString(ConnettoriCostanti.TIPI_CODE_JMS, ","));
					return false;
				}
				if (endpointtype.equals(TipiConnettore.JMS.toString()) && !Utilities.contains(sendas, ConnettoriCostanti.TIPO_SEND_AS) ) {
					this.pd.setMessage("Send As dev'essere: "+Utilities.toString(ConnettoriCostanti.TIPO_SEND_AS, ","));
					return false;
				}
	
				// Controllo campi obbligatori per il tipo di connettore https
				if (endpointtype.equals(TipiConnettore.HTTPS.toString())) {
					if ("".equals(httpsurl)) {
						this.pd.setMessage("Url obbligatorio per il tipo di connettore https");
						return false;
					}else{
						try{
							org.openspcoop2.utils.regexp.RegExpUtilities.validateUrl(httpsurl);
						}catch(Exception e){
							this.pd.setMessage("Url non correttamente formata: "+e.getMessage());
							return false;
						}
					}
					if(this.checkLength255(httpsurl, ConnettoriCostanti.LABEL_PARAMETRO_CONNETTORE_HTTPS_URL)==false) {
						return false;
					}
					
					if(!httpsurl.toLowerCase().trim().startsWith("https://")) {
						if(this.isProfiloModIPA(protocollo)) {
							this.pd.setMessage("Il profilo richiede una url con prefisso https://");
							return false;
						}
						else {
							this.pd.setMessage("Url deve possedere il prefisso https://");
							return false;
						}
					}
					
					if ("".equals(httpspath)) {
						this.pd.setMessage("Il campo 'Path' è obbligatorio per l'Autenticazione Server");
						return false;
					}else{
						try{
							File f = new File(httpspath);
							f.getAbsolutePath();
						}catch(Exception e){
							this.pd.setMessage("Il campo 'Path', obbligatorio per l'Autenticazione Server, non è correttamente definito: "+e.getMessage());
							return false;
						}
					}
					if(this.checkLength255(httpspath, ConnettoriCostanti.LABEL_PARAMETRO_CONNETTORE_HTTPS_TRUST_STORE_LOCATION)==false) {
						return false;
					}
					
					if ("".equals(httpspwd)) {
						this.pd.setMessage("La password del TrustStore è necessaria per l'Autenticazione Server");
						return false;
					}
					if(this.checkLength255(httpspwd, ConnettoriCostanti.LABEL_PARAMETRO_CONNETTORE_HTTPS_TRUST_STORE_PASSWORD)==false) {
						return false;
					}
					
					if ("".equals(httpsalgoritmo)) {
						this.pd.setMessage("Il campo 'Algoritmo' è obbligatorio per l'Autenticazione Server");
						return false;
					}
					if(this.checkLength255(httpsalgoritmo, ConnettoriCostanti.LABEL_PARAMETRO_CONNETTORE_HTTPS_TRUST_MANAGEMENT_ALGORITM)==false) {
						return false;
					}
					
					if(httpsTrustStoreCRLs!=null && !"".equals(httpsTrustStoreCRLs)) {
						httpsTrustStoreCRLs = httpsTrustStoreCRLs.trim();
						String [] tmp = httpsTrustStoreCRLs.split(",");
						if(tmp!=null && tmp.length>0) {
							for (String crl : tmp) {
								if(crl!=null) {
									crl = crl.trim();
									if(!"".equals(crl) && crl.contains(" ")) {
										this.pd.setMessage("I path inseriti nel campo '"+ConnettoriCostanti.LABEL_PARAMETRO_CONNETTORE_HTTPS_TRUST_STORE_CRL+"' non devono contenere spazi");
										return false;
									}
								}
							}
						}
						
					}
					
					if (httpsstato) {
						if (ConnettoriCostanti.DEFAULT_CONNETTORE_HTTPS_KEYSTORE_CLIENT_AUTH_MODE_DEFAULT.equals(httpskeystore)) {
							if ("".equals(httpspwdprivatekeytrust)) {
								this.pd.setMessage("La password della chiave privata è necessaria in caso di Autenticazione Client abilitata");
								return false;
							}
							if(this.checkLength255(httpspwdprivatekeytrust, ConnettoriCostanti.LABEL_PARAMETRO_CONNETTORE_HTTPS_PASSWORD_PRIVATE_KEY_KEYSTORE)==false) {
								return false;
							}
							
							if ("".equals(httpsalgoritmokey)) {
								this.pd.setMessage("Il campo 'Algoritmo' è obbligatorio in caso di Autenticazione Client abilitata");
								return false;
							}
							if(this.checkLength255(httpsalgoritmokey, ConnettoriCostanti.LABEL_PARAMETRO_CONNETTORE_HTTPS_KEY_MANAGEMENT_ALGORITM)==false) {
								return false;
							}
							
						} else {
							if ("".equals(httpspathkey)) {
								this.pd.setMessage("Il campo 'Path' è obbligatorio per l'Autenticazione Client, in caso di dati di accesso al KeyStore ridefiniti");
								return false;
							}else{
								try{
									File f = new File(httpspathkey);
									f.getAbsolutePath();
								}catch(Exception e){
									this.pd.setMessage("Il campo 'Path', obbligatorio per l'Autenticazione Client in caso di dati di accesso al KeyStore ridefiniti, non è correttamente definito: "+e.getMessage());
									return false;
								}
							}
							if(this.checkLength255(httpspathkey, ConnettoriCostanti.LABEL_PARAMETRO_CONNETTORE_HTTPS_KEY_STORE_LOCATION)==false) {
								return false;
							}
							
							if ("".equals(httpspwdkey)) {
								this.pd.setMessage("La password del KeyStore è necessaria per l'Autenticazione Client, in caso di dati di accesso al KeyStore ridefiniti");
								return false;
							}
							if(this.checkLength255(httpspwdkey, ConnettoriCostanti.LABEL_PARAMETRO_CONNETTORE_HTTPS_KEY_STORE_PASSWORD)==false) {
								return false;
							}
							
							if ("".equals(httpspwdprivatekey)) {
								this.pd.setMessage("La password della chiave privata è necessaria in caso di Autenticazione Client abilitata");
								return false;
							}
							if(this.checkLength255(httpspwdprivatekey, ConnettoriCostanti.LABEL_PARAMETRO_CONNETTORE_HTTPS_PASSWORD_PRIVATE_KEY_KEYSTORE)==false) {
								return false;
							}
							
							if ("".equals(httpsalgoritmokey)) {
								this.pd.setMessage("Il campo 'Algoritmo' è obbligatorio per l'Autenticazione Client, in caso di dati di accesso al KeyStore ridefiniti");
								return false;
							}
							if(this.checkLength255(httpsalgoritmokey, ConnettoriCostanti.LABEL_PARAMETRO_CONNETTORE_HTTPS_KEY_MANAGEMENT_ALGORITM)==false) {
								return false;
							}
						}
					}
				}
				
				// Controllo campi obbligatori per il tipo di connettore file
				if (endpointtype.equals(TipiConnettore.FILE.toString())) {
					
					if ("".equals(requestOutputFileName)) {
						this.pd.setMessage("'"+ConnettoriCostanti.LABEL_PARAMETRO_CONNETTORE_FILE_REQUEST_OUTPUT_FILE_NAME+"' ("+
								ConnettoriCostanti.LABEL_CONNETTORE_REQUEST_OUTPUT+") obbligatorio per il tipo di connettore file");
						return false;
					}else{
						try{
							DynamicStringReplace.validate(requestOutputFileName, false);
						}catch(Exception e){
							this.pd.setMessage("Il valore indicato nel parametro '"+ConnettoriCostanti.LABEL_PARAMETRO_CONNETTORE_FILE_REQUEST_OUTPUT_FILE_NAME+"' ("+
									ConnettoriCostanti.LABEL_CONNETTORE_REQUEST_OUTPUT+") non risulta corretto: "+e.getMessage());
							return false;
						}
					}
					if(this.checkLength255(requestOutputFileName, ConnettoriCostanti.LABEL_PARAMETRO_CONNETTORE_FILE_REQUEST_OUTPUT_FILE_NAME)==false) {
						return false;
					}
					
					if(requestOutputFileNameHeaders!=null && !"".equals(requestOutputFileNameHeaders)){
						try{
							DynamicStringReplace.validate(requestOutputFileNameHeaders, false);
						}catch(Exception e){
							this.pd.setMessage("Il valore indicato nel parametro '"+ConnettoriCostanti.LABEL_PARAMETRO_CONNETTORE_FILE_REQUEST_OUTPUT_FILE_NAME_HEADERS+"' ("+
									ConnettoriCostanti.LABEL_CONNETTORE_REQUEST_OUTPUT+") non risulta corretto: "+e.getMessage());
							return false;
						}
						if(this.checkLength255(requestOutputFileNameHeaders, ConnettoriCostanti.LABEL_PARAMETRO_CONNETTORE_FILE_REQUEST_OUTPUT_FILE_NAME_HEADERS)==false) {
							return false;
						}
					}
					
					if(CostantiConfigurazione.ABILITATO.getValue().equals(responseInputMode)){
						
						if ("".equals(responseInputFileName)) {
							this.pd.setMessage("'"+ConnettoriCostanti.LABEL_PARAMETRO_CONNETTORE_FILE_RESPONSE_INPUT_FILE_NAME+"' ("+
									ConnettoriCostanti.LABEL_CONNETTORE_RESPONSE_INPUT+") obbligatorio per il tipo di connettore file");
							return false;
						}else{
							try{
								DynamicStringReplace.validate(responseInputFileName, false);
							}catch(Exception e){
								this.pd.setMessage("Il valore indicato nel parametro '"+ConnettoriCostanti.LABEL_PARAMETRO_CONNETTORE_FILE_RESPONSE_INPUT_FILE_NAME+"' ("+
										ConnettoriCostanti.LABEL_CONNETTORE_RESPONSE_INPUT+") non risulta corretto: "+e.getMessage());
								return false;
							}
						}
						if(this.checkLength255(responseInputFileName, ConnettoriCostanti.LABEL_PARAMETRO_CONNETTORE_FILE_RESPONSE_INPUT_FILE_NAME)==false) {
							return false;
						}
						
						if(responseInputFileNameHeaders!=null && !"".equals(responseInputFileNameHeaders)){
							try{
								DynamicStringReplace.validate(responseInputFileNameHeaders, false);
							}catch(Exception e){
								this.pd.setMessage("Il valore indicato nel parametro '"+ConnettoriCostanti.LABEL_PARAMETRO_CONNETTORE_FILE_RESPONSE_INPUT_FILE_NAME_HEADERS+"' ("+
										ConnettoriCostanti.LABEL_CONNETTORE_RESPONSE_INPUT+") non risulta corretto: "+e.getMessage());
								return false;
							}
							if(this.checkLength255(responseInputFileNameHeaders, ConnettoriCostanti.LABEL_PARAMETRO_CONNETTORE_FILE_RESPONSE_INPUT_FILE_NAME_HEADERS)==false) {
								return false;
							}
						}
						
						if(responseInputWaitTime!=null && !"".equals(responseInputWaitTime)){
							int value = -1;
							try{
								value = Integer.parseInt(responseInputWaitTime);
								if(value<1){
									this.pd.setMessage("Il valore indicato nel parametro '"+ConnettoriCostanti.LABEL_PARAMETRO_CONNETTORE_FILE_RESPONSE_INPUT_WAIT_TIME+
											"' ("+
										ConnettoriCostanti.LABEL_CONNETTORE_RESPONSE_INPUT+") deve essere un numero maggiore di zero.");
									return false;
								}
							}catch(Exception e){
								this.pd.setMessage("Il valore indicato nel parametro '"+ConnettoriCostanti.LABEL_PARAMETRO_CONNETTORE_FILE_RESPONSE_INPUT_WAIT_TIME+
										"' ("+
									ConnettoriCostanti.LABEL_CONNETTORE_RESPONSE_INPUT+") deve essere un numero intero: "+e.getMessage());
								return false;
							}
						}
						
					}
				}
				
				
				try{
					ServletExtendedConnettoreUtils.checkInfo(listExtendedConnettore);
				}catch(Exception e){
					this.pd.setMessage(e.getMessage());
					return false;
				}
				
			} else {
				if(StringUtils.isBlank(servizioApplicativoServer)) { 
					this.pd.setMessage("Il campo '"+ConnettoriCostanti.LABEL_PARAMETRO_CONNETTORE_ID_APPLICATIVO_SERVER
							+ "' &egrave; quando si abilita il campo '"+ConnettoriCostanti.LABEL_PARAMETRO_CONNETTORE_ABILITA_USO_APPLICATIVO_SERVER+"'.");
					return false;
				}
			}
			
			return true;			
		} catch (Exception e) {
			this.log.error("Exception: " + e.getMessage(), e);
			throw new Exception(e);
		}
	}


	public void fillConnettore(org.openspcoop2.core.registry.Connettore connettore,
			String connettoreDebug,
			String tipoConnettore, String oldtipo, String tipoconn, String http_url, String jms_nome,
			String jms_tipo, String user, String pwd,
			String jms_nf_initial, String jms_nf_urlPkg, String jms_np_url,
			String jms_connection_factory, String jms_send_as,
			String httpsurl, String httpstipologia, boolean httpshostverify,
			String httpspath, String httpstipo, String httpspwd,
			String httpsalgoritmo, boolean httpsstato, String httpskeystore,
			String httpspwdprivatekeytrust, String httpspathkey,
			String httpstipokey, String httpspwdkey,
			String httpspwdprivatekey, String httpsalgoritmokey,
			String httpsKeyAlias, String httpsTrustStoreCRLs,
			String proxyEnabled, String proxyHost, String proxyPort, String proxyUsername, String proxyPassword,
			String tempiRisposta_enabled, String tempiRisposta_connectionTimeout, String tempiRisposta_readTimeout, String tempiRisposta_tempoMedioRisposta,
			String opzioniAvanzate, String transfer_mode, String transfer_mode_chunk_size, String redirect_mode, String redirect_max_hop,
			String requestOutputFileName,String requestOutputFileNameHeaders,String requestOutputParentDirCreateIfNotExists,String requestOutputOverwriteIfExists,
			String responseInputMode, String responseInputFileName, String responseInputFileNameHeaders, String responseInputDeleteAfterRead, String responseInputWaitTime,
			String tokenPolicy,
			List<ExtendedConnettore> listExtendedConnettore)
					throws Exception {
		try {
			
			// azzero proprieta esistenti precedentemente
			// (se il connettore è custom lo faccio solo se prima
			// non era custom)
			if (!tipoConnettore.equals(TipiConnettore.CUSTOM.toString()) ||
					!tipoConnettore.equals(oldtipo)) {
				while(connettore.sizePropertyList()>0)
					connettore.removeProperty(0);
			}
			
			String debugValue = null;
			if(ServletUtils.isCheckBoxEnabled(connettoreDebug)){
				debugValue = "true";
			}
			else{
				debugValue = "false";
			}
			boolean found = false;
			for (int i = 0; i < connettore.sizePropertyList(); i++) {
				Property pCheck = connettore.getProperty(i);
				if(CostantiDB.CONNETTORE_DEBUG.equals(pCheck.getNome())){
					pCheck.setValore(debugValue);
					found = true;
					break;
				}
			}
			if(!found){
				Property p = new Property();
				p.setNome(CostantiDB.CONNETTORE_DEBUG);
				p.setValore(debugValue);
				connettore.addProperty(p);
			}

			org.openspcoop2.core.registry.Property prop = null;

			if (tipoConnettore.equals(TipiConnettore.CUSTOM.toString()))
				connettore.setTipo(tipoconn);
			else
				connettore.setTipo(tipoConnettore);
			// Inizializzo a false... Poi eventualmente lo setto a true
			connettore.setCustom(false);
			if (tipoConnettore.equals(TipiConnettore.HTTP.getNome())) {
				
				prop = new org.openspcoop2.core.registry.Property();
				prop.setNome(CostantiDB.CONNETTORE_HTTP_LOCATION);
				prop.setValore(http_url);
				connettore.addProperty(prop);
				
				if(user!=null){
					prop = new org.openspcoop2.core.registry.Property();
					prop.setNome(CostantiDB.CONNETTORE_USER);
					prop.setValore(user);
					connettore.addProperty(prop);
				}
				
				if(pwd!=null){
					prop = new org.openspcoop2.core.registry.Property();
					prop.setNome(CostantiDB.CONNETTORE_PWD);
					prop.setValore(pwd);
					connettore.addProperty(prop);
				}

			} else if (tipoConnettore.equals(TipiConnettore.JMS.getNome())) {
				ConnettoreJMSUtils.fillConnettoreRegistry(connettore, jms_nome, jms_tipo, user, pwd, 
						jms_nf_initial, jms_nf_urlPkg, jms_np_url, jms_connection_factory, jms_send_as);
			} else if (tipoConnettore.equals(TipiConnettore.NULL.getNome())) {
				// nessuna proprieta per connettore null
			} else if (tipoConnettore.equals(TipiConnettore.NULLECHO.getNome())) {
				// nessuna proprieta per connettore nullEcho
			} else if (tipoConnettore.equals(TipiConnettore.HTTPS.getNome())) {
				ConnettoreHTTPSUtils.fillConnettoreRegistry(connettore, httpsurl, httpstipologia, httpshostverify, httpspath, 
						httpstipo, httpspwd, httpsalgoritmo, httpsstato, httpskeystore, httpspwdprivatekeytrust, 
						httpspathkey, httpstipokey, httpspwdkey, httpspwdprivatekey, httpsalgoritmokey,
						httpsKeyAlias, httpsTrustStoreCRLs,
						user, pwd);
			} else if (tipoConnettore.equals(TipiConnettore.FILE.getNome())) {
				ConnettoreFileUtils.fillConnettoreRegistry(connettore, 
						requestOutputFileName,requestOutputFileNameHeaders,requestOutputParentDirCreateIfNotExists,requestOutputOverwriteIfExists,
						responseInputMode, responseInputFileName, responseInputFileNameHeaders, responseInputDeleteAfterRead, responseInputWaitTime);
			} else if (tipoConnettore.equals(TipiConnettore.CUSTOM.toString())) {
				connettore.setCustom(true);
			} else if (!tipoConnettore.equals(TipiConnettore.DISABILITATO.getNome()) &&
					!tipoConnettore.equals(TipiConnettore.CUSTOM.toString())) {
				Property [] cp = this.connettoriCore.getPropertiesConnettore(tipoConnettore);
				List<Property> cps = new ArrayList<Property>();
				if(cp!=null){
					for (int i = 0; i < cp.length; i++) {
						cps.add(cp[i]);
					}
				}
				connettore.setPropertyList(cps);
			}
			
			// Proxy
			if(ServletUtils.isCheckBoxEnabled(proxyEnabled)){
				
				prop = new org.openspcoop2.core.registry.Property();
				prop.setNome(CostantiDB.CONNETTORE_PROXY_TYPE);
				prop.setValore(CostantiConnettori.CONNETTORE_HTTP_PROXY_TYPE_VALUE_HTTP);
				connettore.addProperty(prop);
				
				if(proxyHost!=null && !"".equals(proxyHost)){
					prop = new org.openspcoop2.core.registry.Property();
					prop.setNome(CostantiDB.CONNETTORE_PROXY_HOSTNAME);
					prop.setValore(proxyHost);
					connettore.addProperty(prop);
				}
				
				if(proxyPort!=null && !"".equals(proxyPort)){
					prop = new org.openspcoop2.core.registry.Property();
					prop.setNome(CostantiDB.CONNETTORE_PROXY_PORT);
					prop.setValore(proxyPort);
					connettore.addProperty(prop);
				}
				
				if(proxyUsername!=null && !"".equals(proxyUsername)){
					prop = new org.openspcoop2.core.registry.Property();
					prop.setNome(CostantiDB.CONNETTORE_PROXY_USERNAME);
					prop.setValore(proxyUsername);
					connettore.addProperty(prop);
				}
				
				if(proxyPassword!=null && !"".equals(proxyPassword)){
					prop = new org.openspcoop2.core.registry.Property();
					prop.setNome(CostantiDB.CONNETTORE_PROXY_PASSWORD);
					prop.setValore(proxyPassword);
					connettore.addProperty(prop);
				}
				
			}
			
			// TempiRisposta
			if(ServletUtils.isCheckBoxEnabled(tempiRisposta_enabled)){
				
				if(tempiRisposta_connectionTimeout!=null && !"".equals(tempiRisposta_connectionTimeout)){
					prop = new org.openspcoop2.core.registry.Property();
					prop.setNome(CostantiDB.CONNETTORE_CONNECTION_TIMEOUT);
					prop.setValore(tempiRisposta_connectionTimeout);
					connettore.addProperty(prop);
				}
				
				if(tempiRisposta_readTimeout!=null && !"".equals(tempiRisposta_readTimeout)){
					prop = new org.openspcoop2.core.registry.Property();
					prop.setNome(CostantiDB.CONNETTORE_READ_CONNECTION_TIMEOUT);
					prop.setValore(tempiRisposta_readTimeout);
					connettore.addProperty(prop);
				}
				
				if(tempiRisposta_tempoMedioRisposta!=null && !"".equals(tempiRisposta_tempoMedioRisposta)){
					prop = new org.openspcoop2.core.registry.Property();
					prop.setNome(CostantiDB.CONNETTORE_TEMPO_MEDIO_RISPOSTA);
					prop.setValore(tempiRisposta_tempoMedioRisposta);
					connettore.addProperty(prop);
				}
				
			}
			
			// OpzioniAvanzate
			//if(ServletUtils.isCheckBoxEnabled(opzioniAvanzate)){ li devo impostare anche in caso di HIDDEN
				
			if(TransferLengthModes.CONTENT_LENGTH.getNome().equals(transfer_mode) ||
					TransferLengthModes.TRANSFER_ENCODING_CHUNKED.getNome().equals(transfer_mode)){
				
				// nel caso di default non devo creare la proprietà
				prop = new org.openspcoop2.core.registry.Property();
				prop.setNome(CostantiDB.CONNETTORE_HTTP_DATA_TRANSFER_MODE);
				prop.setValore(transfer_mode);
				connettore.addProperty(prop);
				
			}
			
			if(TransferLengthModes.TRANSFER_ENCODING_CHUNKED.getNome().equals(transfer_mode)){
				if(transfer_mode_chunk_size!=null && !"".equals(transfer_mode_chunk_size)){
					prop = new org.openspcoop2.core.registry.Property();
					prop.setNome(CostantiDB.CONNETTORE_HTTP_DATA_TRANSFER_MODE_CHUNK_SIZE);
					prop.setValore(transfer_mode_chunk_size);
					connettore.addProperty(prop);
				}
			}
			
			if(CostantiConfigurazione.ABILITATO.getValue().equals(redirect_mode) ||
					CostantiConfigurazione.DISABILITATO.getValue().equals(redirect_mode)){
				
				// nel caso di default non devo creare la proprietà
				prop = new org.openspcoop2.core.registry.Property();
				prop.setNome(CostantiDB.CONNETTORE_HTTP_REDIRECT_FOLLOW);
				prop.setValore(redirect_mode);
				connettore.addProperty(prop);
				
			}
			
			if(CostantiConfigurazione.ABILITATO.getValue().equals(redirect_mode)){
				if(redirect_max_hop!=null && !"".equals(redirect_max_hop)){
					prop = new org.openspcoop2.core.registry.Property();
					prop.setNome(CostantiDB.CONNETTORE_HTTP_REDIRECT_MAX_HOP);
					prop.setValore(redirect_max_hop);
					connettore.addProperty(prop);
				}
			}
			
			if(tokenPolicy!=null && !"".equals(tokenPolicy) && !CostantiControlStation.DEFAULT_VALUE_NON_SELEZIONATO.equals(tokenPolicy)) {
				prop = new org.openspcoop2.core.registry.Property();
				prop.setNome(CostantiDB.CONNETTORE_TOKEN_POLICY);
				prop.setValore(tokenPolicy);
				connettore.addProperty(prop);
			}
				
			
			// Extended
			ExtendedConnettoreConverter.fillExtendedInfoIntoConnettore(listExtendedConnettore, connettore);

		} catch (Exception e) {
			this.log.error("Exception: " + e.getMessage(), e);
			throw new Exception(e);
		}

	}


	public void fillConnettore(org.openspcoop2.core.config.Connettore connettore,
			String connettoreDebug,
			String tipoConnettore, String oldtipo, String tipoconn, String http_url, String jms_nome,
			String jms_tipo, String jms_user, String jms_pwd,
			String jms_nf_initial, String jms_nf_urlPkg, String jms_np_url,
			String jms_connection_factory, String jms_send_as,
			String httpsurl, String httpstipologia, boolean httpshostverify,
			String httpspath, String httpstipo, String httpspwd,
			String httpsalgoritmo, boolean httpsstato, String httpskeystore,
			String httpspwdprivatekeytrust, String httpspathkey,
			String httpstipokey, String httpspwdkey,
			String httpspwdprivatekey, String httpsalgoritmokey,
			String httpsKeyAlias, String httpsTrustStoreCRLs,
			String proxyEnabled, String proxyHost, String proxyPort, String proxyUsername, String proxyPassword,
			String tempiRisposta_enabled, String tempiRisposta_connectionTimeout, String tempiRisposta_readTimeout, String tempiRisposta_tempoMedioRisposta,
			String opzioniAvanzate, String transfer_mode, String transfer_mode_chunk_size, String redirect_mode, String redirect_max_hop,
			String requestOutputFileName,String requestOutputFileNameHeaders,String requestOutputParentDirCreateIfNotExists,String requestOutputOverwriteIfExists,
			String responseInputMode, String responseInputFileName, String responseInputFileNameHeaders, String responseInputDeleteAfterRead, String responseInputWaitTime,
			String tokenPolicy,
			List<ExtendedConnettore> listExtendedConnettore)
					throws Exception {
		try {
			
			// azzero proprieta esistenti precedentemente
			// (se il connettore è custom lo faccio solo se prima
			// non era custom)
			if (!tipoConnettore.equals(TipiConnettore.CUSTOM.toString()) ||
					!tipoConnettore.equals(oldtipo)) {
				while(connettore.sizePropertyList()>0)
					connettore.removeProperty(0);
			}

			
			String debugValue = null;
			if(ServletUtils.isCheckBoxEnabled(connettoreDebug)){
				debugValue = "true";
			}
			else{
				debugValue = "false";
			}
			boolean found = false;
			for (int i = 0; i < connettore.sizePropertyList(); i++) {
				org.openspcoop2.core.config.Property pCheck = connettore.getProperty(i);
				if(CostantiDB.CONNETTORE_DEBUG.equals(pCheck.getNome())){
					pCheck.setValore(debugValue);
					found = true;
					break;
				}
			}
			if(!found){
				org.openspcoop2.core.config.Property p = new org.openspcoop2.core.config.Property();
				p.setNome(CostantiDB.CONNETTORE_DEBUG);
				p.setValore(debugValue);
				connettore.addProperty(p);
			}
			
			org.openspcoop2.core.config.Property prop = null;

			if (tipoConnettore.equals(TipiConnettore.CUSTOM.toString()))
				connettore.setTipo(tipoconn);
			else
				connettore.setTipo(tipoConnettore);
			// Inizializzo a false... Poi eventualmente lo setto a true
			connettore.setCustom(false);
			if (tipoConnettore.equals(TipiConnettore.HTTP.getNome())) {
				prop = new org.openspcoop2.core.config.Property();
				prop.setNome(CostantiDB.CONNETTORE_HTTP_LOCATION);
				prop.setValore(http_url);
				connettore.addProperty(prop);

			} else if (tipoConnettore.equals(TipiConnettore.JMS.getNome())) {
				ConnettoreJMSUtils.fillConnettoreConfig(connettore, jms_nome, jms_tipo, jms_user, jms_pwd, 
						jms_nf_initial, jms_nf_urlPkg, jms_np_url, jms_connection_factory, jms_send_as);
			} else if (tipoConnettore.equals(TipiConnettore.NULL.getNome())) {
				// nessuna proprieta per connettore null
			} else if (tipoConnettore.equals(TipiConnettore.NULLECHO.getNome())) {
				// nessuna proprieta per connettore nullEcho
			} else if (tipoConnettore.equals(TipiConnettore.HTTPS.getNome())) {
				ConnettoreHTTPSUtils.fillConnettoreConfig(connettore, httpsurl, httpstipologia, httpshostverify, httpspath, httpstipo, 
						httpspwd, httpsalgoritmo, httpsstato, httpskeystore, httpspwdprivatekeytrust, httpspathkey, httpstipokey, 
						httpspwdkey, httpspwdprivatekey, httpsalgoritmokey,
						httpsKeyAlias, httpsTrustStoreCRLs);
			} else if (tipoConnettore.equals(TipiConnettore.FILE.getNome())) {
				ConnettoreFileUtils.fillConnettoreConfig(connettore, 
						requestOutputFileName,requestOutputFileNameHeaders,requestOutputParentDirCreateIfNotExists,requestOutputOverwriteIfExists,
						responseInputMode, responseInputFileName, responseInputFileNameHeaders, responseInputDeleteAfterRead, responseInputWaitTime);
			} else if (tipoConnettore.equals(TipiConnettore.CUSTOM.toString())) {
				connettore.setCustom(true);
			}else if (!tipoConnettore.equals(TipiConnettore.DISABILITATO.getNome()) &&
					!tipoConnettore.equals(TipiConnettore.CUSTOM.toString())) {
				org.openspcoop2.core.config.Property [] cp = this.connettoriCore.getPropertiesConnettoreConfig(tipoConnettore);
				List<org.openspcoop2.core.config.Property> cps = new ArrayList<org.openspcoop2.core.config.Property>();
				if(cp!=null){
					for (int i = 0; i < cp.length; i++) {
						cps.add(cp[i]);
					}
				}
				connettore.setPropertyList(cps);
			}
			
			// Proxy
			if(ServletUtils.isCheckBoxEnabled(proxyEnabled)){
				
				prop = new org.openspcoop2.core.config.Property();
				prop.setNome(CostantiDB.CONNETTORE_PROXY_TYPE);
				prop.setValore(CostantiConnettori.CONNETTORE_HTTP_PROXY_TYPE_VALUE_HTTP);
				connettore.addProperty(prop);
				
				if(proxyHost!=null && !"".equals(proxyHost)){
					prop = new org.openspcoop2.core.config.Property();
					prop.setNome(CostantiDB.CONNETTORE_PROXY_HOSTNAME);
					prop.setValore(proxyHost);
					connettore.addProperty(prop);
				}
				
				if(proxyPort!=null && !"".equals(proxyPort)){
					prop = new org.openspcoop2.core.config.Property();
					prop.setNome(CostantiDB.CONNETTORE_PROXY_PORT);
					prop.setValore(proxyPort);
					connettore.addProperty(prop);
				}
				
				if(proxyUsername!=null && !"".equals(proxyUsername)){
					prop = new org.openspcoop2.core.config.Property();
					prop.setNome(CostantiDB.CONNETTORE_PROXY_USERNAME);
					prop.setValore(proxyUsername);
					connettore.addProperty(prop);
				}
				
				if(proxyPassword!=null && !"".equals(proxyPassword)){
					prop = new org.openspcoop2.core.config.Property();
					prop.setNome(CostantiDB.CONNETTORE_PROXY_PASSWORD);
					prop.setValore(proxyPassword);
					connettore.addProperty(prop);
				}
				
			}
			
			// TempiRisposta
			if(ServletUtils.isCheckBoxEnabled(tempiRisposta_enabled)){
				
				if(tempiRisposta_connectionTimeout!=null && !"".equals(tempiRisposta_connectionTimeout)){
					prop = new org.openspcoop2.core.config.Property();
					prop.setNome(CostantiDB.CONNETTORE_CONNECTION_TIMEOUT);
					prop.setValore(tempiRisposta_connectionTimeout);
					connettore.addProperty(prop);
				}
				
				if(tempiRisposta_readTimeout!=null && !"".equals(tempiRisposta_readTimeout)){
					prop = new org.openspcoop2.core.config.Property();
					prop.setNome(CostantiDB.CONNETTORE_READ_CONNECTION_TIMEOUT);
					prop.setValore(tempiRisposta_readTimeout);
					connettore.addProperty(prop);
				}
				
				if(tempiRisposta_tempoMedioRisposta!=null && !"".equals(tempiRisposta_tempoMedioRisposta)){
					prop = new org.openspcoop2.core.config.Property();
					prop.setNome(CostantiDB.CONNETTORE_TEMPO_MEDIO_RISPOSTA);
					prop.setValore(tempiRisposta_tempoMedioRisposta);
					connettore.addProperty(prop);
				}
				
			}
			
			// OpzioniAvanzate
			//if(ServletUtils.isCheckBoxEnabled(opzioniAvanzate)){ li devo impostare anche in caso di HIDDEN
				
			if(TransferLengthModes.CONTENT_LENGTH.getNome().equals(transfer_mode) ||
					TransferLengthModes.TRANSFER_ENCODING_CHUNKED.getNome().equals(transfer_mode)){
				
				// nel caso di default non devo creare la proprietà
				prop = new org.openspcoop2.core.config.Property();
				prop.setNome(CostantiDB.CONNETTORE_HTTP_DATA_TRANSFER_MODE);
				prop.setValore(transfer_mode);
				connettore.addProperty(prop);
				
			}
			
			if(TransferLengthModes.TRANSFER_ENCODING_CHUNKED.getNome().equals(transfer_mode)){
				if(transfer_mode_chunk_size!=null && !"".equals(transfer_mode_chunk_size)){
					prop = new org.openspcoop2.core.config.Property();
					prop.setNome(CostantiDB.CONNETTORE_HTTP_DATA_TRANSFER_MODE_CHUNK_SIZE);
					prop.setValore(transfer_mode_chunk_size);
					connettore.addProperty(prop);
				}
			}
			
			if(CostantiConfigurazione.ABILITATO.getValue().equals(redirect_mode) ||
					CostantiConfigurazione.DISABILITATO.getValue().equals(redirect_mode)){
				
				// nel caso di default non devo creare la proprietà
				prop = new org.openspcoop2.core.config.Property();
				prop.setNome(CostantiDB.CONNETTORE_HTTP_REDIRECT_FOLLOW);
				prop.setValore(redirect_mode);
				connettore.addProperty(prop);
				
			}
			
			if(CostantiConfigurazione.ABILITATO.getValue().equals(redirect_mode)){
				if(redirect_max_hop!=null && !"".equals(redirect_max_hop)){
					prop = new org.openspcoop2.core.config.Property();
					prop.setNome(CostantiDB.CONNETTORE_HTTP_REDIRECT_MAX_HOP);
					prop.setValore(redirect_max_hop);
					connettore.addProperty(prop);
				}
			}
			
			if(tokenPolicy!=null && !"".equals(tokenPolicy) && !CostantiControlStation.DEFAULT_VALUE_NON_SELEZIONATO.equals(tokenPolicy)) {
				prop = new org.openspcoop2.core.config.Property();
				prop.setNome(CostantiDB.CONNETTORE_TOKEN_POLICY);
				prop.setValore(tokenPolicy);
				connettore.addProperty(prop);
			}
				
			
			// Extended
			ExtendedConnettoreConverter.fillExtendedInfoIntoConnettore(listExtendedConnettore, connettore);

		} catch (Exception e) {
			this.log.error("Exception: " + e.getMessage(), e);
			throw new Exception(e);
		}

	}

	public static String getOpzioniAvanzate(ConsoleHelper helper,String transfer_mode, String redirect_mode) throws Exception{

		String opzioniAvanzate = helper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_OPZIONI_AVANZATE);
		return getOpzioniAvanzate(opzioniAvanzate, transfer_mode, redirect_mode);
		
	}
	public static String getOpzioniAvanzate(String opzioniAvanzate,String transfer_mode, String redirect_mode){
		
		if(opzioniAvanzate!=null && !"".equals(opzioniAvanzate)){
			return opzioniAvanzate;
		}
		
		if(opzioniAvanzate==null || "".equals(opzioniAvanzate)){
			opzioniAvanzate = Costanti.CHECK_BOX_DISABLED;
		}
		if( (transfer_mode!=null && !"".equals(transfer_mode)) || (redirect_mode!=null && !"".equals(redirect_mode)) ){
			opzioniAvanzate = Costanti.CHECK_BOX_ENABLED;
		}
		return opzioniAvanzate;
	}
	
	public boolean credenzialiCheckData(TipoOperazione tipoOp, boolean oldPasswordCifrata, boolean encryptEnabled, PasswordVerifier passwordVerifier) throws Exception{
		String tipoauth = this.getParameter(ConnettoriCostanti.PARAMETRO_CREDENZIALI_TIPO_AUTENTICAZIONE);
		if (tipoauth == null) {
			tipoauth = ConnettoriCostanti.DEFAULT_AUTENTICAZIONE_TIPO;
		}
		String utente = this.getParameter(ConnettoriCostanti.PARAMETRO_CREDENZIALI_AUTENTICAZIONE_USERNAME);
		String password = this.getParameter(ConnettoriCostanti.PARAMETRO_CREDENZIALI_AUTENTICAZIONE_PASSWORD);
		// String confpw = this.getParameter("confpw");
		String subject = this.getParameter(ConnettoriCostanti.PARAMETRO_CREDENZIALI_AUTENTICAZIONE_SUBJECT);
		String principal = this.getParameter(ConnettoriCostanti.PARAMETRO_CREDENZIALI_AUTENTICAZIONE_PRINCIPAL);
		String appId = this.getParameter(ConnettoriCostanti.PARAMETRO_CREDENZIALI_AUTENTICAZIONE_APP_ID);
		
		if (tipoauth.equals(ConnettoriCostanti.AUTENTICAZIONE_TIPO_BASIC)) {
			
			boolean validaPassword = false;
			if(TipoOperazione.ADD.equals(tipoOp) || !encryptEnabled) {
				validaPassword = true;
			}
			else {
				String changePwd = this.getParameter(ConnettoriCostanti.PARAMETRO_CREDENZIALI_AUTENTICAZIONE_CHANGE_PASSWORD);
				if(ServletUtils.isCheckBoxEnabled(changePwd)) {
					validaPassword = true;
				}
			}
			
			boolean passwordEmpty = false;
			if(validaPassword) {
				if(password==null || password.equals("")) {
					passwordEmpty = true;
				}
			}
			
			if(utente.equals("") || passwordEmpty){
				String tmpElenco = "";
				if (utente.equals("")) {
					tmpElenco = ConnettoriCostanti.LABEL_PARAMETRO_CREDENZIALI_AUTENTICAZIONE_USERNAME;
				}
				if (passwordEmpty) {
					if (tmpElenco.equals("")) {
						tmpElenco = ConnettoriCostanti.LABEL_PARAMETRO_CREDENZIALI_AUTENTICAZIONE_PASSWORD;
					} else {
						tmpElenco = tmpElenco + ", "+ConnettoriCostanti.LABEL_PARAMETRO_CREDENZIALI_AUTENTICAZIONE_PASSWORD;
					}
				}
				/*
				 * if (confpw.equals("")) { if (tmpElenco.equals("")) {
				 * tmpElenco = "Conferma password"; } else { tmpElenco =
				 * tmpElenco + ", Conferma password"; } }
				 */
				this.pd.setMessage("Dati incompleti. E' necessario indicare: " + tmpElenco);
				return false;
			}
			if(this.checkLength255(utente, ConnettoriCostanti.LABEL_PARAMETRO_CREDENZIALI_AUTENTICAZIONE_USERNAME)==false) {
				return false;
			}
			if(this.checkLength255(password, ConnettoriCostanti.LABEL_PARAMETRO_CREDENZIALI_AUTENTICAZIONE_PASSWORD)==false) {
				return false;
			}
			
			if (tipoauth.equals(ConnettoriCostanti.AUTENTICAZIONE_TIPO_BASIC) && ((utente.indexOf(" ") != -1) || (validaPassword && password.indexOf(" ") != -1))) {
				this.pd.setMessage("Non inserire spazi nei campi di testo");
				return false;
			}
			
			if(validaPassword) {
				if(passwordVerifier!=null){
					StringBuilder motivazioneErrore = new StringBuilder();
					if(passwordVerifier.validate(utente, password, motivazioneErrore)==false){
						this.pd.setMessage(motivazioneErrore.toString());
						return false;
					}
				}
			}
		}
		
		if (tipoauth.equals(ConnettoriCostanti.AUTENTICAZIONE_TIPO_SSL)){
			String tipoCredenzialiSSLSorgente = this.getParameter(ConnettoriCostanti.PARAMETRO_CREDENZIALI_AUTENTICAZIONE_CONFIGURAZIONE_SSL);
			if(tipoCredenzialiSSLSorgente == null) {
				tipoCredenzialiSSLSorgente = ConnettoriCostanti.VALUE_PARAMETRO_CREDENZIALI_AUTENTICAZIONE_CONFIGURAZIONE_SSL_CONFIGURAZIONE_MANUALE;
			}
			String tipoCredenzialiSSLConfigurazioneManualeSelfSigned= this.getParameter(ConnettoriCostanti.PARAMETRO_CREDENZIALI_AUTENTICAZIONE_CONFIGURAZIONE_SSL_MANUALE_SELF_SIGNED);
//			if (tipoCredenzialiSSLConfigurazioneManualeSelfSigned == null) {
//				tipoCredenzialiSSLConfigurazioneManualeSelfSigned = Costanti.CHECK_BOX_ENABLED;
//			}
			
			if(tipoCredenzialiSSLSorgente.equals(ConnettoriCostanti.VALUE_PARAMETRO_CREDENZIALI_AUTENTICAZIONE_CONFIGURAZIONE_SSL_CONFIGURAZIONE_MANUALE)) { 
				if (subject.equals("")) {
					this.pd.setMessage("Dati incompleti. E' necessario indicare il "+ ConnettoriCostanti.LABEL_PARAMETRO_CREDENZIALI_AUTENTICAZIONE_SUBJECT);
					return false;
				}
				
				if(this.checkLengthSubject_SSL_Principal(subject, ConnettoriCostanti.LABEL_PARAMETRO_CREDENZIALI_AUTENTICAZIONE_SUBJECT)==false) {
					return false;
				}
				
				try{
					org.openspcoop2.utils.certificate.CertificateUtils.validaPrincipal(subject, PrincipalType.subject);
				}catch(Exception e){
					this.pd.setMessage("Le credenziali di tipo ssl  possiedono un subject non valido: "+e.getMessage());
					return false;
				}
				
				// se non e' selfsigned validare anche l'issuer
				if(!ServletUtils.isCheckBoxEnabled(tipoCredenzialiSSLConfigurazioneManualeSelfSigned)) {
					String issuer = this.getParameter(ConnettoriCostanti.PARAMETRO_CREDENZIALI_AUTENTICAZIONE_ISSUER);
					
					// e' opzionale!
//					if (issuer.equals("")) {
//						this.pd.setMessage("Dati incompleti. E' necessario indicare il "+ ConnettoriCostanti.LABEL_PARAMETRO_CREDENZIALI_AUTENTICAZIONE_ISSUER);
//						return false;
//					}
					
					if(this.checkLengthSubject_SSL_Principal(issuer, ConnettoriCostanti.LABEL_PARAMETRO_CREDENZIALI_AUTENTICAZIONE_ISSUER)==false) {
						return false;
					}
					
					if(StringUtils.isNotEmpty(issuer)) {
						try{
							org.openspcoop2.utils.certificate.CertificateUtils.validaPrincipal(issuer, PrincipalType.issuer);
						}catch(Exception e){
							this.pd.setMessage("Le credenziali di tipo ssl possiedono un issuer non valido: "+e.getMessage());
							return false;
						}
					}
				}
			} else {
				
				
				String tipoCredenzialiSSLWizardStep = this.getParameter(ConnettoriCostanti.PARAMETRO_CREDENZIALI_AUTENTICAZIONE_CONFIGURAZIONE_SSL_WIZARD_STEP);
				boolean validaFieldCert = tipoOp.equals(TipoOperazione.ADD) || !tipoCredenzialiSSLWizardStep.equals(ConnettoriCostanti.VALUE_PARAMETRO_CREDENZIALI_AUTENTICAZIONE_CONFIGURAZIONE_SSL_NO_WIZARD);
				
				String tipoCredenzialiSSLTipoArchivioS = this.getParameter(ConnettoriCostanti.PARAMETRO_CREDENZIALI_AUTENTICAZIONE_CONFIGURAZIONE_SSL_TIPO_ARCHIVIO);
				org.openspcoop2.utils.certificate.ArchiveType tipoCredenzialiSSLTipoArchivio= null;
				if(tipoCredenzialiSSLTipoArchivioS == null) {
					tipoCredenzialiSSLTipoArchivio = org.openspcoop2.utils.certificate.ArchiveType.CER; 
				} else {
					tipoCredenzialiSSLTipoArchivio = org.openspcoop2.utils.certificate.ArchiveType.valueOf(tipoCredenzialiSSLTipoArchivioS);
				}
				BinaryParameter tipoCredenzialiSSLFileCertificato = this.getBinaryParameter(ConnettoriCostanti.PARAMETRO_CREDENZIALI_AUTENTICAZIONE_CONFIGURAZIONE_SSL_FILE_CERTIFICATO);
				
				if(validaFieldCert) {
					if(tipoCredenzialiSSLFileCertificato.getValue() == null || tipoCredenzialiSSLFileCertificato.getValue().length == 0) {
						this.pd.setMessage("Dati incompleti. E' necessario indicare il " + ConnettoriCostanti.LABEL_PARAMETRO_CREDENZIALI_AUTENTICAZIONE_CONFIGURAZIONE_SSL_FILE_CERTIFICATO);
						return false;
					}
					
					// validazione certificato caricato
					if(tipoCredenzialiSSLTipoArchivio.equals(ArchiveType.CER)) {
						try {
							ArchiveLoader.load(tipoCredenzialiSSLFileCertificato.getValue());
						}catch(UtilsException e) {
							this.pd.setMessage("Il Certificato selezionato non &egrave; valido: "+e.getMessage());
							return false;
						}
					} else {
						String tipoCredenzialiSSLFileCertificatoPassword = this.getParameter(ConnettoriCostanti.PARAMETRO_CREDENZIALI_AUTENTICAZIONE_CONFIGURAZIONE_SSL_FILE_CERTIFICATO_PASSWORD);
						
						if(StringUtils.isEmpty(tipoCredenzialiSSLFileCertificatoPassword)) {
							this.pd.setMessage("Dati incompleti. E' necessario indicare la " + ConnettoriCostanti.LABEL_PARAMETRO_CREDENZIALI_AUTENTICAZIONE_CONFIGURAZIONE_SSL_FILE_CERTIFICATO_PASSWORD);
							return false;
						}
						List<String> listaAliasEstrattiCertificato = new ArrayList<String>();
						try {
							listaAliasEstrattiCertificato = ArchiveLoader.readAliases(tipoCredenzialiSSLTipoArchivio, tipoCredenzialiSSLFileCertificato.getValue(), tipoCredenzialiSSLFileCertificatoPassword);
						}catch(UtilsException e) {
							this.pd.setMessage("Il Certificato selezionato non &egrave; valido: "+e.getMessage());
							return false;
						}
						String tipoCredenzialiSSLAliasCertificato = this.getParameter(ConnettoriCostanti.PARAMETRO_CREDENZIALI_AUTENTICAZIONE_CONFIGURAZIONE_SSL_ALIAS_CERTIFICATO);
						
						if(StringUtils.isEmpty(tipoCredenzialiSSLAliasCertificato)) {
							this.pd.setMessage("Dati incompleti. E' necessario indicare un " + ConnettoriCostanti.LABEL_PARAMETRO_CREDENZIALI_AUTENTICAZIONE_CONFIGURAZIONE_SSL_ALIAS_CERTIFICATO);
							return false;
						}
						
						if(!listaAliasEstrattiCertificato.contains(tipoCredenzialiSSLAliasCertificato)) {
							this.pd.setMessage("L'" + ConnettoriCostanti.LABEL_PARAMETRO_CREDENZIALI_AUTENTICAZIONE_CONFIGURAZIONE_SSL_ALIAS_CERTIFICATO + " indicato non &egrave; presente all'interno del file caricato.");
							return false;
						}
						
						try {
							ArchiveLoader.load(tipoCredenzialiSSLTipoArchivio, tipoCredenzialiSSLFileCertificato.getValue(), tipoCredenzialiSSLAliasCertificato, tipoCredenzialiSSLFileCertificatoPassword);
						}catch(UtilsException e) {
							this.pd.setMessage("Il Certificato selezionato non &egrave; valido: "+e.getMessage());
							return false;
						}
					}
				}
			}
		}
		
		if (tipoauth.equals(ConnettoriCostanti.AUTENTICAZIONE_TIPO_APIKEY)) {
			boolean appIdModificabile = ConnettoriCostanti.PARAMETRO_CREDENZIALI_AUTENTICAZIONE_APP_ID_MODIFICABILE ;
			if(appIdModificabile && ServletUtils.isCheckBoxEnabled(appId)) {
				if(utente.equals("")) {
					String tmpElenco = "";
					if (utente.equals("")) {
						tmpElenco = ConnettoriCostanti.LABEL_PARAMETRO_CREDENZIALI_AUTENTICAZIONE_APP_ID;
					}
					this.pd.setMessage("Dati incompleti. E' necessario indicare: " + tmpElenco);
					return false;
				}
				if(this.checkLengthSubject_SSL_Principal(utente, ConnettoriCostanti.LABEL_PARAMETRO_CREDENZIALI_AUTENTICAZIONE_APP_ID)==false) {
					return false;
				}
			}
		}
		
		if (tipoauth.equals(ConnettoriCostanti.AUTENTICAZIONE_TIPO_PRINCIPAL) ) {
			if(principal.equals("")) {
				String tmpElenco = "";
				if (principal.equals("")) {
					tmpElenco = ConnettoriCostanti.LABEL_PARAMETRO_CREDENZIALI_AUTENTICAZIONE_PRINCIPAL;
				}
				this.pd.setMessage("Dati incompleti. E' necessario indicare: " + tmpElenco);
				return false;
			}
			if(this.checkLengthSubject_SSL_Principal(principal, ConnettoriCostanti.LABEL_PARAMETRO_CREDENZIALI_AUTENTICAZIONE_PRINCIPAL)==false) {
				return false;
			}
		}
				
		if (!tipoauth.equals(ConnettoriCostanti.AUTENTICAZIONE_TIPO_BASIC) && 
				!tipoauth.equals(ConnettoriCostanti.AUTENTICAZIONE_TIPO_SSL) && 
				!tipoauth.equals(ConnettoriCostanti.AUTENTICAZIONE_TIPO_PRINCIPAL) && 
				!tipoauth.equals(ConnettoriCostanti.AUTENTICAZIONE_TIPO_APIKEY) && 
				!tipoauth.equals(ConnettoriCostanti.AUTENTICAZIONE_TIPO_NESSUNA)) {
			this.pd.setMessage("Tipo dev'essere "+
					ConnettoriCostanti.AUTENTICAZIONE_TIPO_BASIC+", "+
					ConnettoriCostanti.AUTENTICAZIONE_TIPO_SSL+" o "+
					ConnettoriCostanti.AUTENTICAZIONE_TIPO_PRINCIPAL+" o "+
					ConnettoriCostanti.AUTENTICAZIONE_TIPO_APIKEY+" o "+
					ConnettoriCostanti.AUTENTICAZIONE_TIPO_NESSUNA);
			return false;
		}
		
		return true;
	}
	
	public Vector<DataElement> addConnettoreDefaultRidefinitoToDati(Vector<DataElement> dati, TipoOperazione tipoOp, String modalita, String[] modalitaValues, String[] modalitaLabels, boolean servletRidefinito, String servletConnettore, Parameter[] parametriServletConnettore) {
		DataElement de = new DataElement();
		de.setLabel(ConnettoriCostanti.LABEL_CONNETTORE);
		de.setType(DataElementType.TITLE);
		dati.addElement(de);
		
		de = new DataElement();
		de.setName(ConnettoriCostanti.PARAMETRO_CONNETTORE_MODALITA);
		de.setLabel(ConnettoriCostanti.LABEL_PARAMETRO_CONNETTORE_MODALITA);
		de.setType(DataElementType.SELECT);
		de.setValues(modalitaValues);
		de.setLabels(modalitaLabels);
		de.setPostBack(true);
		de.setSelected(modalita);
		dati.addElement(de);
		
		// link visualizza
		if(servletRidefinito && modalita.equals(ConnettoriCostanti.VALUE_PARAMETRO_MODALITA_CONNETTORE_RIDEFINITO)) {
			de = new DataElement();
			de.setType(DataElementType.LINK);
			de.setName(ConnettoriCostanti.PARAMETRO_CONNETTORE_CUSTOM_PROPRIETA);
			de.setUrl(servletConnettore, parametriServletConnettore);
			ServletUtils.setDataElementVisualizzaLabel(de);
			dati.addElement(de);	
		}
		
		return dati;
	}
	
	public boolean connettoreDefaultRidefinitoCheckData(TipoOperazione tipoOp, String modalita) throws Exception{
		
		if (StringUtils.isEmpty(modalita)) {
			this.pd.setMessage("Il campo "+ConnettoriCostanti.LABEL_PARAMETRO_CONNETTORE_MODALITA+" non pu&ograve; essere vuoto");
			return false;
		}
		
		return true;
	}
	
	public String getTooltipConnettore(ServizioApplicativo sa, org.openspcoop2.core.config.InvocazioneServizio is) {
		StringBuilder sbCon = new StringBuilder();
		if(ServiziApplicativiCostanti.VALUE_SERVIZI_APPLICATIVI_TIPO_SERVER.equals(sa.getTipo())) {
			sbCon.append(ConnettoriCostanti.LABEL_SERVER);
			sbCon.append(": ");
			sbCon.append(sa.getNome());
			sbCon.append(CostantiControlStation.TOOLTIP_BREAK_LINE);
		}
		sbCon.append(this.getLabelConnettore(is));
		return sbCon.toString();
	}
	
	public String getLabelConnettore(ServizioApplicativo sa, org.openspcoop2.core.config.InvocazioneServizio is) {
		StringBuilder sbCon = new StringBuilder();
		if(ServiziApplicativiCostanti.VALUE_SERVIZI_APPLICATIVI_TIPO_SERVER.equals(sa.getTipo())) {
			//sbCon.append(sa.getNome());
			//sbCon.append(" ");
		}
		sbCon.append(this.getLabelConnettore(is));
		return sbCon.toString();
	}
	
	public String getLabelConnettore(org.openspcoop2.core.config.InvocazioneServizio is) {
		String urlConnettore = this.getLabelConnettore(is.getConnettore());
		
		if(is.getGetMessage()!=null && StatoFunzionalita.ABILITATO.equals(is.getGetMessage())) {
			urlConnettore = urlConnettore + " [MessageBox]";
		}
		
		return urlConnettore;
	}
	public String getLabelConnettore(org.openspcoop2.core.registry.Connettore connettore) {
		return this.getLabelConnettore(connettore.mappingIntoConnettoreConfigurazione());
	}
	public String getLabelConnettore(org.openspcoop2.core.config.Connettore connettore) {
		String urlConnettore = "";
		
		List<org.openspcoop2.core.config.Property> cp = connettore.getPropertyList();
		
		//TipiConnettore.HTTP.getNome() e anche TipiConnettore.HTTPS.getNome() -> location
		//TipiConnettore.DISABILITATO.getNome() ci scrivi "disabilitato"
		//TipiConnettore.FILE.getNome() CostantiConnettori.CONNETTORE_FILE_REQUEST_OUTPUT_FILE
		//TipiConnettore.JMS.compareTo() CostantiConnettori.CONNETTORE_LOCATION
//			TipiConnettore.NULL 
//			TipiConnettore.CUSTOM -> connettore custom
		String tipo = connettore.getTipo();
		
		String tipoLabel = "[" + connettore.getTipo() + "] ";
		if ((connettore.getCustom()!=null && connettore.getCustom()) && 
				!connettore.getTipo().equals(CostantiDB.CONNETTORE_TIPO_HTTPS) && 
				!connettore.getTipo().equals(CostantiDB.CONNETTORE_TIPO_FILE)) {
			tipo = ConnettoriCostanti.DEFAULT_CONNETTORE_TYPE_CUSTOM;
		}  

		if(tipo.equals(ConnettoriCostanti.DEFAULT_CONNETTORE_TYPE_CUSTOM)) {
			urlConnettore = tipoLabel + ConnettoriCostanti.LABEL_CONNETTORE_CUSTOM;
		} else	if(tipo.equals(TipiConnettore.DISABILITATO.getNome())) {
			urlConnettore = CostantiControlStation.DEFAULT_VALUE_DISABILITATO;
		} else if(tipo.equals(TipiConnettore.NULL.getNome())) {
			urlConnettore = tipoLabel + ConnettoreNULL.LOCATION;
		} else if(tipo.equals(TipiConnettore.NULLECHO.getNome())) {
			urlConnettore = tipoLabel + ConnettoreNULLEcho.LOCATION;
		} else {  
			String propertyName = CostantiConnettori.CONNETTORE_LOCATION;
			if(tipo.equals(TipiConnettore.FILE.getNome()))
				propertyName = CostantiConnettori.CONNETTORE_FILE_REQUEST_OUTPUT_FILE;
		
			for (int i = 0; i < connettore.sizePropertyList(); i++) {
				org.openspcoop2.core.config.Property singlecp = cp.get(i);
				if (singlecp.getNome().equals(propertyName)) {
					if(!tipo.equals(TipiConnettore.HTTP.getNome()) && !tipo.equals(TipiConnettore.HTTPS.getNome())) {
						urlConnettore = tipoLabel + singlecp.getValore();
					}
					else {
						urlConnettore = singlecp.getValore();
					}
					
					break;
				}
			}
		}
		return urlConnettore;
	}
}
