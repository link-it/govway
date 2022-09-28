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
package org.openspcoop2.web.ctrlstat.servlet.pd;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.openspcoop2.core.config.PortaDelegata;
import org.openspcoop2.core.config.TrasformazioneRegola;
import org.openspcoop2.core.config.TrasformazioneRegolaRichiesta;
import org.openspcoop2.core.config.TrasformazioneRest;
import org.openspcoop2.core.config.TrasformazioneSoap;
import org.openspcoop2.core.config.Trasformazioni;
import org.openspcoop2.core.config.constants.VersioneSOAP;
import org.openspcoop2.core.registry.AccordoServizioParteSpecifica;
import org.openspcoop2.core.registry.beans.AccordoServizioParteComuneSintetico;
import org.openspcoop2.message.constants.ServiceBinding;
import org.openspcoop2.web.ctrlstat.core.ControlStationCore;
import org.openspcoop2.web.ctrlstat.costanti.CostantiControlStation;
import org.openspcoop2.web.ctrlstat.servlet.GeneralHelper;
import org.openspcoop2.web.ctrlstat.servlet.apc.AccordiServizioParteComuneCore;
import org.openspcoop2.web.ctrlstat.servlet.aps.AccordiServizioParteSpecificaCore;
import org.openspcoop2.web.ctrlstat.servlet.soggetti.SoggettiCore;
import org.openspcoop2.web.lib.mvc.BinaryParameter;
import org.openspcoop2.web.lib.mvc.Costanti;
import org.openspcoop2.web.lib.mvc.DataElement;
import org.openspcoop2.web.lib.mvc.GeneralData;
import org.openspcoop2.web.lib.mvc.PageData;
import org.openspcoop2.web.lib.mvc.Parameter;
import org.openspcoop2.web.lib.mvc.ServletUtils;
import org.openspcoop2.web.lib.mvc.TipoOperazione;

/**
 * PorteDelegateTrasformazioniRichiesta
 * 
 * @author Giuliano Pintori (pintori@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 * 
 */
public class PorteDelegateTrasformazioniRichiesta extends Action {

	@Override
	public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {

		HttpSession session = request.getSession(true);

		// Inizializzo PageData
		PageData pd = new PageData();

		GeneralHelper generalHelper = new GeneralHelper(session);
		
		String userLogin = ServletUtils.getUserLoginFromSession(session);

		// Inizializzo GeneralData
		GeneralData gd = generalHelper.initGeneralData(request);
		
		try {
			
			PorteDelegateHelper porteDelegateHelper = new PorteDelegateHelper(request, pd, session);
			// prelevo il flag che mi dice da quale pagina ho acceduto la sezione delle porte delegate
			Integer parentPD = ServletUtils.getIntegerAttributeFromSession(PorteDelegateCostanti.ATTRIBUTO_PORTE_DELEGATE_PARENT, session, request);
			if(parentPD == null) parentPD = PorteDelegateCostanti.ATTRIBUTO_PORTE_DELEGATE_PARENT_NONE;
			String id = porteDelegateHelper.getParameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID);
			long idInt = Long.parseLong(id);
			String idsogg = porteDelegateHelper.getParameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID_SOGGETTO);
			
			String idAsps = porteDelegateHelper.getParameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID_ASPS);
			if(idAsps == null)
				idAsps = "";
			
			String idFruizione = porteDelegateHelper.getParameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID_FRUIZIONE);
			if(idFruizione == null)
				idFruizione = "";
			
			String idTrasformazioneS = porteDelegateHelper.getParameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID_TRASFORMAZIONE);
			long idTrasformazione = Long.parseLong(idTrasformazioneS);
			
			String trasformazioneContenutoAbilitatoS  = porteDelegateHelper.getParameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_TRASFORMAZIONI_REQ_CONVERSIONE_ENABLED);
			boolean trasformazioneContenutoAbilitato = trasformazioneContenutoAbilitatoS != null ? ServletUtils.isCheckBoxEnabled(trasformazioneContenutoAbilitatoS) : false;
			String trasformazioneContenutoTipoS = porteDelegateHelper.getParameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_TRASFORMAZIONI_REQ_CONVERSIONE_TIPO);
			org.openspcoop2.pdd.core.trasformazioni.TipoTrasformazione trasformazioneContenutoTipo = 
					trasformazioneContenutoTipoS != null ? org.openspcoop2.pdd.core.trasformazioni.TipoTrasformazione.toEnumConstant(trasformazioneContenutoTipoS) : 
						org.openspcoop2.pdd.core.trasformazioni.TipoTrasformazione.EMPTY;
			
			BinaryParameter trasformazioneContenutoTemplate = porteDelegateHelper.getBinaryParameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_TRASFORMAZIONI_REQ_CONVERSIONE_TEMPLATE);
			String trasformazioneContenutoTipoCheck = porteDelegateHelper.getParameter(CostantiControlStation.PARAMETRO_CONFIGURAZIONE_TRASFORMAZIONI_REQ_CONVERSIONE_TIPO_CHECK);
			String trasformazioneRichiestaContentType = porteDelegateHelper.getParameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_TRASFORMAZIONI_REQ_CONTENT_TYPE);
			String trasformazioneRestAbilitatoS = porteDelegateHelper.getParameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_TRASFORMAZIONI_REST_TRANSFORMATION);
			boolean trasformazioneRestAbilitato =  trasformazioneRestAbilitatoS != null ? ServletUtils.isCheckBoxEnabled(trasformazioneRestAbilitatoS) : false;
			String trasformazioneRestMethod = porteDelegateHelper.getParameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_TRASFORMAZIONI_REST_METHOD);
			String trasformazioneRestPath = porteDelegateHelper.getParameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_TRASFORMAZIONI_REST_PATH);
			String trasformazioneSoapAbilitatoS = porteDelegateHelper.getParameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_TRASFORMAZIONI_SOAP_TRANSFORMATION);
			boolean trasformazioneSoapAbilitato =  trasformazioneSoapAbilitatoS != null ? ServletUtils.isCheckBoxEnabled(trasformazioneSoapAbilitatoS) : false;
			String trasformazioneSoapAction = porteDelegateHelper.getParameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_TRASFORMAZIONI_SOAP_ACTION);
			String trasformazioneSoapVersion = porteDelegateHelper.getParameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_TRASFORMAZIONI_SOAP_VERSION);
			String trasformazioneSoapEnvelope  =  porteDelegateHelper.getParameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_TRASFORMAZIONI_SOAP_ENVELOPE);
			String trasformazioneSoapEnvelopeTipoS = porteDelegateHelper.getParameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_TRASFORMAZIONI_SOAP_ENVELOPE_TIPO);
			org.openspcoop2.pdd.core.trasformazioni.TipoTrasformazione trasformazioneSoapEnvelopeTipo =
					trasformazioneSoapEnvelopeTipoS != null ? org.openspcoop2.pdd.core.trasformazioni.TipoTrasformazione.toEnumConstant(trasformazioneSoapEnvelopeTipoS) : 
						org.openspcoop2.pdd.core.trasformazioni.TipoTrasformazione.EMPTY;
			BinaryParameter trasformazioneSoapEnvelopeTemplate = porteDelegateHelper.getBinaryParameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_TRASFORMAZIONI_SOAP_ENVELOPE_TEMPLATE);
			String trasformazioneSoapEnvelopeTipoCheck = porteDelegateHelper.getParameter(CostantiControlStation.PARAMETRO_CONFIGURAZIONE_TRASFORMAZIONI_SOAP_ENVELOPE_TIPO_CHECK);
			
			Parameter pId = new Parameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID, id);
			Parameter pIdSoggetto = new Parameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID_SOGGETTO, idsogg);
			Parameter pIdAsps = new Parameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID_ASPS, idAsps);
			Parameter pIdFruizione = new Parameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID_FRUIZIONE, idFruizione);

			PorteDelegateCore porteDelegateCore = new PorteDelegateCore();
			AccordiServizioParteSpecificaCore apsCore = new AccordiServizioParteSpecificaCore(porteDelegateCore);
			AccordiServizioParteComuneCore apcCore = new AccordiServizioParteComuneCore(porteDelegateCore);
			SoggettiCore soggettiCore = new SoggettiCore(porteDelegateCore);
			
			// Preparo il menu
			porteDelegateHelper.makeMenu();

			// Prendo nome della porta applicativa
			PortaDelegata portaDelegata = porteDelegateCore.getPortaDelegata(idInt);
			String nomePorta = portaDelegata.getNome();
			String protocollo = soggettiCore.getProtocolloAssociatoTipoSoggetto(portaDelegata.getTipoSoggettoProprietario());
			
			Trasformazioni trasformazioni = portaDelegata.getTrasformazioni();
			TrasformazioneRegola oldRegola = null;
			for (TrasformazioneRegola reg : trasformazioni.getRegolaList()) {
				if(reg.getId().longValue() == idTrasformazione) {
					oldRegola = reg;
					break;
				}
			}
			
			// Prendo il nome e il tipo del servizio
			AccordoServizioParteSpecifica asps = apsCore.getAccordoServizioParteSpecifica(Integer.parseInt(idAsps));
			AccordoServizioParteComuneSintetico apc = apcCore.getAccordoServizioSintetico(asps.getIdAccordo()); 
			ServiceBinding serviceBindingMessage = apcCore.toMessageServiceBinding(apc.getServiceBinding());
			
			String postBackElementName = porteDelegateHelper.getPostBackElementName();
			
			// se ho modificato il soggetto ricalcolo il servizio e il service binding
			if (postBackElementName != null) {
				if(postBackElementName.equals(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_TRASFORMAZIONI_REQ_CONVERSIONE_ENABLED)) {
					trasformazioneContenutoTipo = org.openspcoop2.pdd.core.trasformazioni.TipoTrasformazione.EMPTY;
					trasformazioneRichiestaContentType = "";
					trasformazioneSoapAbilitato = false;
					trasformazioneSoapAction = "";
					trasformazioneSoapEnvelope = CostantiControlStation.VALUE_PARAMETRO_CONFIGURAZIONE_TRASFORMAZIONI_SOAP_ENVELOPE_DISABILITATO;
					trasformazioneSoapEnvelopeTipo = org.openspcoop2.pdd.core.trasformazioni.TipoTrasformazione.EMPTY;
					trasformazioneSoapVersion = CostantiControlStation.DEFAULT_VALUE_PARAMETRO_CONFIGURAZIONE_TRASFORMAZIONI_RICHIESTA_SOAP_VERSION_11;
					trasformazioneRestAbilitato = false;
					if(ServiceBinding.SOAP.equals(serviceBindingMessage)) {
						trasformazioneRestMethod = "";
						trasformazioneRestPath = ""; 
					}
					
					porteDelegateHelper.deleteBinaryParameters(trasformazioneContenutoTemplate, trasformazioneSoapEnvelopeTemplate);
				}
				
				if(postBackElementName.equals(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_TRASFORMAZIONI_REQ_CONVERSIONE_TIPO)) {
					trasformazioneContenutoTipoCheck = CostantiControlStation.VALUE_PARAMETRO_CONFIGURAZIONE_TRASFORMAZIONI_REQ_CONVERSIONE_TIPO_CHECK_UPDATE_TIPO;
					porteDelegateHelper.deleteBinaryParameters(trasformazioneContenutoTemplate);
				}
				
				if(postBackElementName.equals(trasformazioneContenutoTemplate.getName())) {
					if(StringUtils.isEmpty(trasformazioneContenutoTipoCheck))
						trasformazioneContenutoTipoCheck = CostantiControlStation.VALUE_PARAMETRO_CONFIGURAZIONE_TRASFORMAZIONI_REQ_CONVERSIONE_TIPO_CHECK_UPDATE_FILE;
				}
				
				if(postBackElementName.equals(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_TRASFORMAZIONI_SOAP_TRANSFORMATION)) {
					porteDelegateHelper.deleteBinaryParameters(trasformazioneSoapEnvelopeTemplate);
					if(trasformazioneSoapAbilitato) {
						trasformazioneSoapAction = "";
						trasformazioneSoapEnvelope = CostantiControlStation.VALUE_PARAMETRO_CONFIGURAZIONE_TRASFORMAZIONI_SOAP_ENVELOPE_DISABILITATO;
						trasformazioneSoapEnvelopeTipo = org.openspcoop2.pdd.core.trasformazioni.TipoTrasformazione.EMPTY;
						trasformazioneSoapVersion = CostantiControlStation.DEFAULT_VALUE_PARAMETRO_CONFIGURAZIONE_TRASFORMAZIONI_RICHIESTA_SOAP_VERSION_11;
					} 
				}
				
				if(postBackElementName.equals(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_TRASFORMAZIONI_SOAP_ENVELOPE)) {
					porteDelegateHelper.deleteBinaryParameters(trasformazioneSoapEnvelopeTemplate);
					if(CostantiControlStation.VALUE_PARAMETRO_CONFIGURAZIONE_TRASFORMAZIONI_SOAP_ENVELOPE_AS_ATTACHMENT.equals(trasformazioneSoapEnvelope)) {
						trasformazioneSoapEnvelopeTipo = org.openspcoop2.pdd.core.trasformazioni.TipoTrasformazione.EMPTY; 
					}
				}
				
				if(postBackElementName.equals(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_TRASFORMAZIONI_SOAP_ENVELOPE_TIPO)) {
					trasformazioneSoapEnvelopeTipoCheck = CostantiControlStation.VALUE_PARAMETRO_CONFIGURAZIONE_TRASFORMAZIONI_REQ_CONVERSIONE_TIPO_CHECK_UPDATE_TIPO;
					porteDelegateHelper.deleteBinaryParameters(trasformazioneSoapEnvelopeTemplate);
				}
				
				if(postBackElementName.equals(trasformazioneSoapEnvelopeTemplate.getName())) {
					if(StringUtils.isEmpty(trasformazioneSoapEnvelopeTipoCheck))
						trasformazioneSoapEnvelopeTipoCheck = CostantiControlStation.VALUE_PARAMETRO_CONFIGURAZIONE_TRASFORMAZIONI_REQ_CONVERSIONE_TIPO_CHECK_UPDATE_FILE;
				}
				
				if(postBackElementName.equals(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_TRASFORMAZIONI_REST_TRANSFORMATION)) {
					if(trasformazioneRestAbilitato) {
						trasformazioneRestMethod = "";
						trasformazioneRestPath = ""; 
					}
				}
			}
			
			String nomeTrasformazione = oldRegola.getNome();
			Parameter pIdTrasformazione = new Parameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID_TRASFORMAZIONE, idTrasformazioneS);
			
			// parametri visualizzazione link
			String servletTrasformazioniRichiestaHeadersList = PorteDelegateCostanti.SERVLET_NAME_PORTE_DELEGATE_TRASFORMAZIONI_RICHIESTA_HEADER_LIST;
			String servletTrasformazioniRichiestaParametriList = PorteDelegateCostanti.SERVLET_NAME_PORTE_DELEGATE_TRASFORMAZIONI_RICHIESTA_PARAMETRO_LIST;
			TrasformazioneRegolaRichiesta oldRichiesta = oldRegola.getRichiesta();
			int numeroTrasformazioniRichiestaHeaders= oldRichiesta != null ? oldRichiesta.sizeHeaderList() : 0;
			int numeroTrasformazioniRichiestaParametri = oldRichiesta != null ? oldRichiesta.sizeParametroUrlList() : 0;
		 
			List<Parameter> parametriInvocazioneServletTrasformazioniRichiestaHeaders = new ArrayList<Parameter>();
			parametriInvocazioneServletTrasformazioniRichiestaHeaders.add(pId);
			parametriInvocazioneServletTrasformazioniRichiestaHeaders.add(pIdSoggetto);
			parametriInvocazioneServletTrasformazioniRichiestaHeaders.add(pIdAsps);
			parametriInvocazioneServletTrasformazioniRichiestaHeaders.add(pIdFruizione);
			parametriInvocazioneServletTrasformazioniRichiestaHeaders.add(pIdTrasformazione);
			List<Parameter> parametriInvocazioneServletTrasformazioniRichiestaParametri = new ArrayList<Parameter>();
			parametriInvocazioneServletTrasformazioniRichiestaParametri.add(pId);
			parametriInvocazioneServletTrasformazioniRichiestaParametri.add(pIdSoggetto);
			parametriInvocazioneServletTrasformazioniRichiestaParametri.add(pIdAsps);
			parametriInvocazioneServletTrasformazioniRichiestaParametri.add(pIdFruizione);
			parametriInvocazioneServletTrasformazioniRichiestaParametri.add(pIdTrasformazione);
			
			List<Parameter> lstParam = porteDelegateHelper.getTitoloPD(parentPD, idsogg, idAsps, idFruizione);
			
			String labelPerPorta = null;
			if(parentPD!=null && (parentPD.intValue() == PorteDelegateCostanti.ATTRIBUTO_PORTE_DELEGATE_PARENT_CONFIGURAZIONE)) {
				labelPerPorta = porteDelegateCore.getLabelRegolaMappingFruizionePortaDelegata(
						PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_TRASFORMAZIONI_DI,
						PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_TRASFORMAZIONI,
						portaDelegata);
			}
			else {
				labelPerPorta = PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_TRASFORMAZIONI_DI+nomePorta;
			}

			lstParam.add(new Parameter(labelPerPorta, PorteDelegateCostanti.SERVLET_NAME_PORTE_DELEGATE_TRASFORMAZIONI_LIST, pId, pIdSoggetto, pIdAsps, pIdFruizione));
			
			lstParam.add(new Parameter(nomeTrasformazione, PorteDelegateCostanti.SERVLET_NAME_PORTE_DELEGATE_TRASFORMAZIONI_CHANGE,
					pId, pIdSoggetto, pIdAsps, pIdFruizione, pIdTrasformazione));
			
			lstParam.add(new Parameter(PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_TRASFORMAZIONI_RICHIESTA, null));
			
			ServletUtils.setPageDataTitle(pd, lstParam);
			
			// Se nomehid = null, devo visualizzare la pagina per l'inserimento
			// dati
			if (porteDelegateHelper.isEditModeInProgress()) {
				// preparo i campi
				Vector<DataElement> dati = new Vector<DataElement>();
				dati.addElement(ServletUtils.getDataElementForEditModeFinished());
				
				// primo accesso
				if(trasformazioneContenutoAbilitatoS == null) {
					
					if(oldRichiesta == null) {
						trasformazioneContenutoAbilitato = false;
						trasformazioneContenutoTipo = org.openspcoop2.pdd.core.trasformazioni.TipoTrasformazione.EMPTY;
						trasformazioneRichiestaContentType = "";
						trasformazioneSoapAbilitato = false;
						trasformazioneSoapAction = "";
						trasformazioneSoapEnvelope = CostantiControlStation.VALUE_PARAMETRO_CONFIGURAZIONE_TRASFORMAZIONI_SOAP_ENVELOPE_DISABILITATO;
						trasformazioneSoapEnvelopeTipo = org.openspcoop2.pdd.core.trasformazioni.TipoTrasformazione.EMPTY;
						trasformazioneSoapVersion = CostantiControlStation.DEFAULT_VALUE_PARAMETRO_CONFIGURAZIONE_TRASFORMAZIONI_RICHIESTA_SOAP_VERSION_11;
						trasformazioneRestAbilitato = false;
						trasformazioneRestMethod = "";
						trasformazioneRestPath = ""; 
					} else {
						trasformazioneContenutoAbilitato = oldRichiesta.getConversione();
						trasformazioneContenutoTipo = StringUtils.isNotEmpty(oldRichiesta.getConversioneTipo()) ? 
								org.openspcoop2.pdd.core.trasformazioni.TipoTrasformazione.toEnumConstant(oldRichiesta.getConversioneTipo()) : org.openspcoop2.pdd.core.trasformazioni.TipoTrasformazione.EMPTY;
						trasformazioneContenutoTemplate.setValue(oldRichiesta.getConversioneTemplate());
						trasformazioneRichiestaContentType = StringUtils.isNotEmpty(oldRichiesta.getContentType()) ? oldRichiesta.getContentType() : "";
						
						if(oldRichiesta.getTrasformazioneSoap() == null) {
							trasformazioneSoapAbilitato = false;
							trasformazioneSoapAction = "";
							trasformazioneSoapEnvelope = CostantiControlStation.VALUE_PARAMETRO_CONFIGURAZIONE_TRASFORMAZIONI_SOAP_ENVELOPE_DISABILITATO;
							trasformazioneSoapEnvelopeTipo = org.openspcoop2.pdd.core.trasformazioni.TipoTrasformazione.EMPTY;
							trasformazioneSoapVersion = CostantiControlStation.DEFAULT_VALUE_PARAMETRO_CONFIGURAZIONE_TRASFORMAZIONI_RICHIESTA_SOAP_VERSION_11;
						} else {
							trasformazioneSoapAbilitato = true;
							trasformazioneSoapAction = StringUtils.isNotEmpty(oldRichiesta.getTrasformazioneSoap().getSoapAction()) ? oldRichiesta.getTrasformazioneSoap().getSoapAction() : "";
							
							if(oldRichiesta.getTrasformazioneSoap().isEnvelope()) {
								if(oldRichiesta.getTrasformazioneSoap().isEnvelopeAsAttachment()) {
									trasformazioneSoapEnvelope = CostantiControlStation.VALUE_PARAMETRO_CONFIGURAZIONE_TRASFORMAZIONI_SOAP_ENVELOPE_AS_ATTACHMENT;
								} else {
									trasformazioneSoapEnvelope = CostantiControlStation.VALUE_PARAMETRO_CONFIGURAZIONE_TRASFORMAZIONI_SOAP_ENVELOPE_AS_BODY;
								}
							} else {
								trasformazioneSoapEnvelope = CostantiControlStation.VALUE_PARAMETRO_CONFIGURAZIONE_TRASFORMAZIONI_SOAP_ENVELOPE_DISABILITATO;
							}
							
							trasformazioneSoapEnvelopeTipo = StringUtils.isNotEmpty(oldRichiesta.getTrasformazioneSoap().getEnvelopeBodyConversioneTipo()) ? 
									org.openspcoop2.pdd.core.trasformazioni.TipoTrasformazione.toEnumConstant(oldRichiesta.getTrasformazioneSoap().getEnvelopeBodyConversioneTipo())
									: org.openspcoop2.pdd.core.trasformazioni.TipoTrasformazione.EMPTY;
							if(oldRichiesta.getTrasformazioneSoap().getVersione() != null) {
								
								switch (oldRichiesta.getTrasformazioneSoap().getVersione()) {
								case _1_2:
									trasformazioneSoapVersion = CostantiControlStation.DEFAULT_VALUE_PARAMETRO_CONFIGURAZIONE_TRASFORMAZIONI_RICHIESTA_SOAP_VERSION_12;
									break;
								case _1_1:
								default:
									trasformazioneSoapVersion = CostantiControlStation.DEFAULT_VALUE_PARAMETRO_CONFIGURAZIONE_TRASFORMAZIONI_RICHIESTA_SOAP_VERSION_11;
									break;
								}
							}else {
								trasformazioneSoapVersion = CostantiControlStation.DEFAULT_VALUE_PARAMETRO_CONFIGURAZIONE_TRASFORMAZIONI_RICHIESTA_SOAP_VERSION_11;
							}
							trasformazioneSoapEnvelopeTemplate.setValue(oldRichiesta.getTrasformazioneSoap().getEnvelopeBodyConversioneTemplate()); 
						}
							
						
						if(oldRichiesta.getTrasformazioneRest() == null) {
							trasformazioneRestAbilitato = false;
							trasformazioneRestMethod = "";
							trasformazioneRestPath = "";
						} else {
							trasformazioneRestAbilitato = true;
							trasformazioneRestMethod = StringUtils.isNotEmpty(oldRichiesta.getTrasformazioneRest().getMetodo()) ? oldRichiesta.getTrasformazioneRest().getMetodo() : "";
							trasformazioneRestPath = StringUtils.isNotEmpty(oldRichiesta.getTrasformazioneRest().getPath()) ? oldRichiesta.getTrasformazioneRest().getPath() : "";
						}
 
					}
				}

				dati = porteDelegateHelper.addTrasformazioneRichiestaToDati(TipoOperazione.OTHER, protocollo, dati, idInt, oldRichiesta, true, idTrasformazioneS, trasformazioneContenutoAbilitato, trasformazioneContenutoTipo,
						trasformazioneContenutoTemplate, trasformazioneContenutoTipoCheck, trasformazioneRichiestaContentType, serviceBindingMessage, trasformazioneRestAbilitato, 
						trasformazioneRestMethod, trasformazioneRestPath, trasformazioneSoapAbilitato, trasformazioneSoapAction, 
						trasformazioneSoapVersion, trasformazioneSoapEnvelope, trasformazioneSoapEnvelopeTipo, trasformazioneSoapEnvelopeTemplate, trasformazioneSoapEnvelopeTipoCheck,
						servletTrasformazioniRichiestaHeadersList, parametriInvocazioneServletTrasformazioniRichiestaHeaders, numeroTrasformazioniRichiestaHeaders,
						servletTrasformazioniRichiestaParametriList, parametriInvocazioneServletTrasformazioniRichiestaParametri, numeroTrasformazioniRichiestaParametri,
						apc.getServiceBinding());
				
				dati = porteDelegateHelper.addHiddenFieldsToDati(TipoOperazione.CHANGE, id, idsogg, null, idAsps, 
						idFruizione, portaDelegata.getTipoSoggettoProprietario(), portaDelegata.getNomeSoggettoProprietario(), dati);
				
				pd.setDati(dati);

				ServletUtils.setGeneralAndPageDataIntoSession(request, session, gd, pd);

				return ServletUtils.getStrutsForwardEditModeInProgress(mapping, PorteDelegateCostanti.OBJECT_NAME_PORTE_DELEGATE_TRASFORMAZIONI_RICHIESTA,  PorteDelegateCostanti.TIPO_OPERAZIONE_TRASFORMAZIONI_RICHIESTA);
			}
			
			boolean isOk = porteDelegateHelper.trasformazioniRichiestaCheckData(TipoOperazione.CHANGE, oldRegola, serviceBindingMessage);
			if (!isOk) {

				// preparo i campi
				Vector<DataElement> dati = new Vector<DataElement>();

				dati.addElement(ServletUtils.getDataElementForEditModeFinished());
				
				dati = porteDelegateHelper.addTrasformazioneRichiestaToDati(TipoOperazione.OTHER, protocollo, dati, idInt, oldRichiesta, true, idTrasformazioneS, trasformazioneContenutoAbilitato, trasformazioneContenutoTipo,
						trasformazioneContenutoTemplate, trasformazioneContenutoTipoCheck, trasformazioneRichiestaContentType, serviceBindingMessage, trasformazioneRestAbilitato, 
						trasformazioneRestMethod, trasformazioneRestPath, trasformazioneSoapAbilitato, trasformazioneSoapAction, 
						trasformazioneSoapVersion, trasformazioneSoapEnvelope, trasformazioneSoapEnvelopeTipo, trasformazioneSoapEnvelopeTemplate, trasformazioneSoapEnvelopeTipoCheck,
						servletTrasformazioniRichiestaHeadersList, parametriInvocazioneServletTrasformazioniRichiestaHeaders, numeroTrasformazioniRichiestaHeaders,
						servletTrasformazioniRichiestaParametriList, parametriInvocazioneServletTrasformazioniRichiestaParametri, numeroTrasformazioniRichiestaParametri,
						apc.getServiceBinding());
				
				dati = porteDelegateHelper.addHiddenFieldsToDati(TipoOperazione.CHANGE, id, idsogg, null, idAsps, 
						idFruizione, portaDelegata.getTipoSoggettoProprietario(), portaDelegata.getNomeSoggettoProprietario(), dati);
				
				pd.setDati(dati);

				ServletUtils.setGeneralAndPageDataIntoSession(request, session, gd, pd);

				return ServletUtils.getStrutsForwardEditModeCheckError(mapping, PorteDelegateCostanti.OBJECT_NAME_PORTE_DELEGATE_TRASFORMAZIONI_RICHIESTA,  PorteDelegateCostanti.TIPO_OPERAZIONE_TRASFORMAZIONI_RICHIESTA);
			}
			
			// aggiorno la regola
			trasformazioni = portaDelegata.getTrasformazioni();
			for (TrasformazioneRegola reg : trasformazioni.getRegolaList()) {
				if(reg.getId().longValue() == idTrasformazione) {
					
					if(reg.getRichiesta() == null)
						reg.setRichiesta(new TrasformazioneRegolaRichiesta());
					
					reg.getRichiesta().setConversione(trasformazioneContenutoAbilitato);
					
					if(trasformazioneContenutoAbilitato) {
						if(trasformazioneContenutoTipo.isTemplateRequired()) {
							// 	se e' stato aggiornato il template lo sovrascrivo
							if(trasformazioneContenutoTemplate.getValue() != null && trasformazioneContenutoTemplate.getValue().length  > 0)
								reg.getRichiesta().setConversioneTemplate(trasformazioneContenutoTemplate.getValue());
						}else {
							reg.getRichiesta().setConversioneTemplate(null);
						}
						
						if(trasformazioneRichiestaContentType!=null && !"".equals(trasformazioneRichiestaContentType)) {
							reg.getRichiesta().setContentType(trasformazioneRichiestaContentType);
						}
						else {
							reg.getRichiesta().setContentType(null);
						}
						reg.getRichiesta().setConversioneTipo(trasformazioneContenutoTipo.getValue());
						
						if(trasformazioneRestAbilitato) {
							if(reg.getRichiesta().getTrasformazioneRest() == null) 
								reg.getRichiesta().setTrasformazioneRest(new TrasformazioneRest());
							
							reg.getRichiesta().getTrasformazioneRest().setMetodo(trasformazioneRestMethod);
							reg.getRichiesta().getTrasformazioneRest().setPath(trasformazioneRestPath);
						} else {
							reg.getRichiesta().setTrasformazioneRest(null);
							
							if(ServiceBinding.REST.equals(serviceBindingMessage)) {
								if(StringUtils.isNotEmpty(trasformazioneRestMethod) || StringUtils.isNotEmpty(trasformazioneRestPath)) {
									if(reg.getRichiesta().getTrasformazioneRest() == null) 
										reg.getRichiesta().setTrasformazioneRest(new TrasformazioneRest());
									
									reg.getRichiesta().getTrasformazioneRest().setMetodo(trasformazioneRestMethod);
									reg.getRichiesta().getTrasformazioneRest().setPath(trasformazioneRestPath);
								} 
							}
						}
						
						if(trasformazioneSoapAbilitato) {
							if(reg.getRichiesta().getTrasformazioneSoap() == null)
								reg.getRichiesta().setTrasformazioneSoap(new TrasformazioneSoap());
							
							reg.getRichiesta().getTrasformazioneSoap().setSoapAction(trasformazioneSoapAction);
							reg.getRichiesta().getTrasformazioneSoap().setVersione(VersioneSOAP.toEnumConstant(trasformazioneSoapVersion));
							if(CostantiControlStation.VALUE_PARAMETRO_CONFIGURAZIONE_TRASFORMAZIONI_SOAP_ENVELOPE_AS_ATTACHMENT.equals(trasformazioneSoapEnvelope)) { // attachment
								reg.getRichiesta().getTrasformazioneSoap().setEnvelope(true);
								reg.getRichiesta().getTrasformazioneSoap().setEnvelopeAsAttachment(true);
								reg.getRichiesta().getTrasformazioneSoap().setEnvelopeBodyConversioneTipo(trasformazioneSoapEnvelopeTipo.getValue());
								
								if(trasformazioneSoapEnvelopeTipo.isTemplateRequired()) {
									// 	se e' stato aggiornato il template lo sovrascrivo
									if(trasformazioneSoapEnvelopeTemplate.getValue() != null && trasformazioneSoapEnvelopeTemplate.getValue().length  > 0)
										reg.getRichiesta().getTrasformazioneSoap().setEnvelopeBodyConversioneTemplate(trasformazioneSoapEnvelopeTemplate.getValue());
								}else {
									reg.getRichiesta().getTrasformazioneSoap().setEnvelopeBodyConversioneTemplate(null);
								}
								
							} else if(CostantiControlStation.VALUE_PARAMETRO_CONFIGURAZIONE_TRASFORMAZIONI_SOAP_ENVELOPE_AS_BODY.equals(trasformazioneSoapEnvelope)) { // body
								reg.getRichiesta().getTrasformazioneSoap().setEnvelope(true);
								reg.getRichiesta().getTrasformazioneSoap().setEnvelopeAsAttachment(false);
								reg.getRichiesta().getTrasformazioneSoap().setEnvelopeBodyConversioneTemplate(null);
								reg.getRichiesta().getTrasformazioneSoap().setEnvelopeBodyConversioneTipo(null);
							} else  { // disabilitato
								reg.getRichiesta().getTrasformazioneSoap().setEnvelope(false);
								reg.getRichiesta().getTrasformazioneSoap().setEnvelopeAsAttachment(false);
								reg.getRichiesta().getTrasformazioneSoap().setEnvelopeBodyConversioneTemplate(null);
								reg.getRichiesta().getTrasformazioneSoap().setEnvelopeBodyConversioneTipo(null);
							}
								
						} else {
							reg.getRichiesta().setTrasformazioneSoap(null);
						}
					} else {
						reg.getRichiesta().setConversioneTemplate(null);
						reg.getRichiesta().setContentType(null);
						reg.getRichiesta().setConversioneTipo(null);
						reg.getRichiesta().setTrasformazioneRest(null);
						reg.getRichiesta().setTrasformazioneSoap(null);
						
						if(ServiceBinding.REST.equals(serviceBindingMessage)) {
							if(StringUtils.isNotEmpty(trasformazioneRestMethod) || StringUtils.isNotEmpty(trasformazioneRestPath)) {
								if(reg.getRichiesta().getTrasformazioneRest() == null) 
									reg.getRichiesta().setTrasformazioneRest(new TrasformazioneRest());
								
								reg.getRichiesta().getTrasformazioneRest().setMetodo(trasformazioneRestMethod);
								reg.getRichiesta().getTrasformazioneRest().setPath(trasformazioneRestPath);
							} else {
								reg.getRichiesta().setTrasformazioneRest(null);
							}
						}
					}
					
					break;
				}
			}
			
			porteDelegateCore.performUpdateOperation(userLogin, porteDelegateHelper.smista(), portaDelegata);
			porteDelegateHelper.deleteBinaryParameters(trasformazioneContenutoTemplate,trasformazioneSoapEnvelopeTemplate);
			
			// ricarico la richiesta
			// ricaricare id trasformazione
			portaDelegata = porteDelegateCore.getPortaDelegata(Long.parseLong(id));

			TrasformazioneRegola trasformazioneAggiornata = porteDelegateCore.getTrasformazione(portaDelegata.getId(), oldRegola.getNome());
			
			// setto il titolo della pagina
			nomeTrasformazione = oldRegola.getNome();
			pIdTrasformazione = new Parameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID_TRASFORMAZIONE, trasformazioneAggiornata.getId() + "");
			
			// parametri visualizzazione link
			numeroTrasformazioniRichiestaHeaders= trasformazioneAggiornata.getRichiesta() != null ? trasformazioneAggiornata.getRichiesta().sizeHeaderList() : 0;
			numeroTrasformazioniRichiestaParametri = trasformazioneAggiornata.getRichiesta() != null ? trasformazioneAggiornata.getRichiesta().sizeParametroUrlList() : 0;
			
			parametriInvocazioneServletTrasformazioniRichiestaHeaders = new ArrayList<Parameter>();
			parametriInvocazioneServletTrasformazioniRichiestaHeaders.add(pId);
			parametriInvocazioneServletTrasformazioniRichiestaHeaders.add(pIdSoggetto);
			parametriInvocazioneServletTrasformazioniRichiestaHeaders.add(pIdAsps);
			parametriInvocazioneServletTrasformazioniRichiestaHeaders.add(pIdFruizione);
			parametriInvocazioneServletTrasformazioniRichiestaHeaders.add(pIdTrasformazione);
			parametriInvocazioneServletTrasformazioniRichiestaParametri = new ArrayList<Parameter>();
			parametriInvocazioneServletTrasformazioniRichiestaParametri.add(pId);
			parametriInvocazioneServletTrasformazioniRichiestaParametri.add(pIdSoggetto);
			parametriInvocazioneServletTrasformazioniRichiestaParametri.add(pIdAsps);
			parametriInvocazioneServletTrasformazioniRichiestaParametri.add(pIdFruizione);
			parametriInvocazioneServletTrasformazioniRichiestaParametri.add(pIdTrasformazione);
			
			lstParam = porteDelegateHelper.getTitoloPD(parentPD, idsogg, idAsps, idFruizione);
			
			labelPerPorta = null;
			if(parentPD!=null && (parentPD.intValue() == PorteDelegateCostanti.ATTRIBUTO_PORTE_DELEGATE_PARENT_CONFIGURAZIONE)) {
				labelPerPorta = porteDelegateCore.getLabelRegolaMappingFruizionePortaDelegata(
						PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_TRASFORMAZIONI_DI,
						PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_TRASFORMAZIONI,
						portaDelegata);
			}
			else {
				labelPerPorta = PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_TRASFORMAZIONI_DI+nomePorta;
			}

			lstParam.add(new Parameter(labelPerPorta, PorteDelegateCostanti.SERVLET_NAME_PORTE_DELEGATE_TRASFORMAZIONI_LIST, pId, pIdSoggetto, pIdAsps, pIdFruizione));
			
			
			lstParam.add(new Parameter(nomeTrasformazione, PorteDelegateCostanti.SERVLET_NAME_PORTE_DELEGATE_TRASFORMAZIONI_CHANGE, 
					pId, pIdSoggetto, pIdAsps, pIdFruizione, pIdTrasformazione));
			
			lstParam.add(new Parameter(PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_TRASFORMAZIONI_RICHIESTA, null));
			
			ServletUtils.setPageDataTitle(pd, lstParam);
			
			// preparo i campi
			Vector<DataElement> dati = new Vector<DataElement>();
			
			TrasformazioneRegolaRichiesta richiesta = trasformazioneAggiornata.getRichiesta();
			
			if(richiesta == null) {
				trasformazioneContenutoAbilitato = false;
				trasformazioneContenutoTipo = org.openspcoop2.pdd.core.trasformazioni.TipoTrasformazione.EMPTY;
				trasformazioneRichiestaContentType = "";
				trasformazioneSoapAbilitato = false;
				trasformazioneSoapAction = "";
				trasformazioneSoapEnvelope = CostantiControlStation.VALUE_PARAMETRO_CONFIGURAZIONE_TRASFORMAZIONI_SOAP_ENVELOPE_DISABILITATO;
				trasformazioneSoapEnvelopeTipo = org.openspcoop2.pdd.core.trasformazioni.TipoTrasformazione.EMPTY;
				trasformazioneSoapVersion = CostantiControlStation.DEFAULT_VALUE_PARAMETRO_CONFIGURAZIONE_TRASFORMAZIONI_RICHIESTA_SOAP_VERSION_11;
				trasformazioneRestAbilitato = false;
				trasformazioneRestMethod = "";
				trasformazioneRestPath = ""; 
			} else {
				trasformazioneContenutoAbilitato = richiesta.getConversione();
				trasformazioneContenutoTipo = StringUtils.isNotEmpty(richiesta.getConversioneTipo()) ? 
						org.openspcoop2.pdd.core.trasformazioni.TipoTrasformazione.toEnumConstant(richiesta.getConversioneTipo()) : org.openspcoop2.pdd.core.trasformazioni.TipoTrasformazione.EMPTY;
				trasformazioneContenutoTemplate.setValue(richiesta.getConversioneTemplate());
				trasformazioneRichiestaContentType = StringUtils.isNotEmpty(richiesta.getContentType()) ? richiesta.getContentType() : "";
				
				if(richiesta.getTrasformazioneSoap() == null) {
					trasformazioneSoapAbilitato = false;
					trasformazioneSoapAction = "";
					trasformazioneSoapEnvelope = CostantiControlStation.VALUE_PARAMETRO_CONFIGURAZIONE_TRASFORMAZIONI_SOAP_ENVELOPE_DISABILITATO;
					trasformazioneSoapEnvelopeTipo = org.openspcoop2.pdd.core.trasformazioni.TipoTrasformazione.EMPTY;
					trasformazioneSoapVersion = CostantiControlStation.DEFAULT_VALUE_PARAMETRO_CONFIGURAZIONE_TRASFORMAZIONI_RICHIESTA_SOAP_VERSION_11;
				} else {
					trasformazioneSoapAbilitato = true;
					trasformazioneSoapAction = StringUtils.isNotEmpty(richiesta.getTrasformazioneSoap().getSoapAction()) ? richiesta.getTrasformazioneSoap().getSoapAction() : "";
					
					if(richiesta.getTrasformazioneSoap().isEnvelope()) {
						if(richiesta.getTrasformazioneSoap().isEnvelopeAsAttachment()) {
							trasformazioneSoapEnvelope = CostantiControlStation.VALUE_PARAMETRO_CONFIGURAZIONE_TRASFORMAZIONI_SOAP_ENVELOPE_AS_ATTACHMENT;
						} else {
							trasformazioneSoapEnvelope = CostantiControlStation.VALUE_PARAMETRO_CONFIGURAZIONE_TRASFORMAZIONI_SOAP_ENVELOPE_AS_BODY;
						}
					} else {
						trasformazioneSoapEnvelope = CostantiControlStation.VALUE_PARAMETRO_CONFIGURAZIONE_TRASFORMAZIONI_SOAP_ENVELOPE_DISABILITATO;
					}
					
					trasformazioneSoapEnvelopeTipo = StringUtils.isNotEmpty(richiesta.getTrasformazioneSoap().getEnvelopeBodyConversioneTipo()) ? 
							org.openspcoop2.pdd.core.trasformazioni.TipoTrasformazione.toEnumConstant(richiesta.getTrasformazioneSoap().getEnvelopeBodyConversioneTipo())
							: org.openspcoop2.pdd.core.trasformazioni.TipoTrasformazione.EMPTY;
					if(richiesta.getTrasformazioneSoap().getVersione() != null) {
						
						switch (richiesta.getTrasformazioneSoap().getVersione()) {
						case _1_2:
							trasformazioneSoapVersion = CostantiControlStation.DEFAULT_VALUE_PARAMETRO_CONFIGURAZIONE_TRASFORMAZIONI_RICHIESTA_SOAP_VERSION_12;
							break;
						case _1_1:
						default:
							trasformazioneSoapVersion = CostantiControlStation.DEFAULT_VALUE_PARAMETRO_CONFIGURAZIONE_TRASFORMAZIONI_RICHIESTA_SOAP_VERSION_11;
							break;
						}
					}else {
						trasformazioneSoapVersion = CostantiControlStation.DEFAULT_VALUE_PARAMETRO_CONFIGURAZIONE_TRASFORMAZIONI_RICHIESTA_SOAP_VERSION_11;
					}
					trasformazioneSoapEnvelopeTemplate.setValue(richiesta.getTrasformazioneSoap().getEnvelopeBodyConversioneTemplate()); 
				}
					
				
				if(richiesta.getTrasformazioneRest() == null) {
					trasformazioneRestAbilitato = false;
					trasformazioneRestMethod = "";
					trasformazioneRestPath = "";
				} else {
					trasformazioneRestAbilitato = true;
					trasformazioneRestMethod = StringUtils.isNotEmpty(richiesta.getTrasformazioneRest().getMetodo()) ? richiesta.getTrasformazioneRest().getMetodo() : "";
					trasformazioneRestPath = StringUtils.isNotEmpty(richiesta.getTrasformazioneRest().getPath()) ? richiesta.getTrasformazioneRest().getPath() : "";
				}

			}
			
			trasformazioneContenutoTipoCheck= "";
			trasformazioneSoapEnvelopeTipoCheck = "";
			
			dati = porteDelegateHelper.addTrasformazioneRichiestaToDati(TipoOperazione.OTHER, protocollo, dati, idInt, richiesta, true, trasformazioneAggiornata.getId() + "", trasformazioneContenutoAbilitato, trasformazioneContenutoTipo,
					trasformazioneContenutoTemplate, trasformazioneContenutoTipoCheck, trasformazioneRichiestaContentType, serviceBindingMessage, trasformazioneRestAbilitato, 
					trasformazioneRestMethod, trasformazioneRestPath, trasformazioneSoapAbilitato, trasformazioneSoapAction, 
					trasformazioneSoapVersion, trasformazioneSoapEnvelope, trasformazioneSoapEnvelopeTipo, trasformazioneSoapEnvelopeTemplate, trasformazioneSoapEnvelopeTipoCheck,
					servletTrasformazioniRichiestaHeadersList, parametriInvocazioneServletTrasformazioniRichiestaHeaders, numeroTrasformazioniRichiestaHeaders,
					servletTrasformazioniRichiestaParametriList, parametriInvocazioneServletTrasformazioniRichiestaParametri, numeroTrasformazioniRichiestaParametri,
					apc.getServiceBinding());
			
			dati = porteDelegateHelper.addHiddenFieldsToDati(TipoOperazione.CHANGE, id, idsogg, null, idAsps, 
					idFruizione, portaDelegata.getTipoSoggettoProprietario(), portaDelegata.getNomeSoggettoProprietario(), dati);
			
			pd.setDati(dati);
						
			pd.setMessage(CostantiControlStation.LABEL_AGGIORNAMENTO_EFFETTUATO_CON_SUCCESSO, Costanti.MESSAGE_TYPE_INFO);
			//pd.disableEditMode();
			dati.addElement(ServletUtils.getDataElementForEditModeFinished());
			
			ServletUtils.setGeneralAndPageDataIntoSession(request, session, gd, pd);
			
			// Forward control to the specified success URI
			return ServletUtils.getStrutsForwardEditModeFinished(mapping, PorteDelegateCostanti.OBJECT_NAME_PORTE_DELEGATE_TRASFORMAZIONI_RICHIESTA,  PorteDelegateCostanti.TIPO_OPERAZIONE_TRASFORMAZIONI_RICHIESTA);
		} catch (Exception e) {
			return ServletUtils.getStrutsForwardError(ControlStationCore.getLog(), e, pd, request, session, gd, mapping, 
					PorteDelegateCostanti.OBJECT_NAME_PORTE_DELEGATE_TRASFORMAZIONI_RICHIESTA,  PorteDelegateCostanti.TIPO_OPERAZIONE_TRASFORMAZIONI_RICHIESTA	);
		}
	}
}
