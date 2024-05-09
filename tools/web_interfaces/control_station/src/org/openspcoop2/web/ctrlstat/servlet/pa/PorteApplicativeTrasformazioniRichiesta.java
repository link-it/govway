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

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.openspcoop2.core.config.PortaApplicativa;
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
 * PorteApplicativeTrasformazioniRichiesta
 * 
 * @author Giuliano Pintori (pintori@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 * 
 */
public class PorteApplicativeTrasformazioniRichiesta extends Action {

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
			
			PorteApplicativeHelper porteApplicativeHelper = new PorteApplicativeHelper(request, pd, session);
			// prelevo il flag che mi dice da quale pagina ho acceduto la sezione delle porte applicative
			Integer parentPA = ServletUtils.getIntegerAttributeFromSession(PorteApplicativeCostanti.ATTRIBUTO_PORTE_APPLICATIVE_PARENT, session, request);
			if(parentPA == null) parentPA = PorteApplicativeCostanti.ATTRIBUTO_PORTE_APPLICATIVE_PARENT_NONE;
			String idPorta = porteApplicativeHelper.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID);
			long idInt = Long.parseLong(idPorta);
			String idsogg = porteApplicativeHelper.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_SOGGETTO);
			String idAsps = porteApplicativeHelper.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_ASPS);
			if(idAsps == null) 
				idAsps = "";
			
			String idTrasformazioneS = porteApplicativeHelper.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_TRASFORMAZIONE);
			long idTrasformazione = Long.parseLong(idTrasformazioneS);

			PorteApplicativeCore porteApplicativeCore = new PorteApplicativeCore();
			AccordiServizioParteSpecificaCore apsCore = new AccordiServizioParteSpecificaCore(porteApplicativeCore);
			AccordiServizioParteComuneCore apcCore = new AccordiServizioParteComuneCore(porteApplicativeCore);
			SoggettiCore soggettiCore = new SoggettiCore(porteApplicativeCore);
			
			// Preparo il menu
			porteApplicativeHelper.makeMenu();

			// Prendo nome della porta applicativa
			PortaApplicativa pa = porteApplicativeCore.getPortaApplicativa(idInt);
			String nomePorta = pa.getNome();
			String protocollo = soggettiCore.getProtocolloAssociatoTipoSoggetto(pa.getTipoSoggettoProprietario());
			
			Trasformazioni trasformazioni = pa.getTrasformazioni();
			TrasformazioneRegola oldRegola = null;
			for (TrasformazioneRegola reg : trasformazioni.getRegolaList()) {
				if(reg.getId().longValue() == idTrasformazione) {
					oldRegola = reg;
					break;
				}
			}
			if(oldRegola==null) {
				throw new Exception("TrasformazioneRegola con id '"+idTrasformazione+"' non trovata");
			}
			
			String trasformazioneContenutoAbilitatoS  = porteApplicativeHelper.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_TRASFORMAZIONI_REQ_CONVERSIONE_ENABLED);
			boolean trasformazioneContenutoAbilitato = trasformazioneContenutoAbilitatoS != null ? ServletUtils.isCheckBoxEnabled(trasformazioneContenutoAbilitatoS) : false;
			String trasformazioneContenutoTipoS = porteApplicativeHelper.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_TRASFORMAZIONI_REQ_CONVERSIONE_TIPO);
			org.openspcoop2.pdd.core.trasformazioni.TipoTrasformazione trasformazioneContenutoTipo = 
					trasformazioneContenutoTipoS != null ? org.openspcoop2.pdd.core.trasformazioni.TipoTrasformazione.toEnumConstant(trasformazioneContenutoTipoS) : 
						org.openspcoop2.pdd.core.trasformazioni.TipoTrasformazione.EMPTY;
			
			BinaryParameter trasformazioneContenutoTemplate = porteApplicativeHelper.getBinaryParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_TRASFORMAZIONI_REQ_CONVERSIONE_TEMPLATE);
			String trasformazioneContenutoTipoCheck = porteApplicativeHelper.getParameter(CostantiControlStation.PARAMETRO_CONFIGURAZIONE_TRASFORMAZIONI_REQ_CONVERSIONE_TIPO_CHECK);
			String trasformazioneRichiestaContentType = porteApplicativeHelper.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_TRASFORMAZIONI_REQ_CONTENT_TYPE);
			String trasformazioneRestAbilitatoS = porteApplicativeHelper.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_TRASFORMAZIONI_REST_TRANSFORMATION);
			boolean trasformazioneRestAbilitato =  trasformazioneRestAbilitatoS != null ? ServletUtils.isCheckBoxEnabled(trasformazioneRestAbilitatoS) : false;
			String trasformazioneRestMethod = porteApplicativeHelper.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_TRASFORMAZIONI_REST_METHOD);
			String trasformazioneRestPath = porteApplicativeHelper.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_TRASFORMAZIONI_REST_PATH);
			String trasformazioneSoapAbilitatoS = porteApplicativeHelper.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_TRASFORMAZIONI_SOAP_TRANSFORMATION);
			boolean trasformazioneSoapAbilitato =  trasformazioneSoapAbilitatoS != null ? ServletUtils.isCheckBoxEnabled(trasformazioneSoapAbilitatoS) : false;
			String trasformazioneSoapAction = porteApplicativeHelper.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_TRASFORMAZIONI_SOAP_ACTION);
			String trasformazioneSoapVersion = porteApplicativeHelper.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_TRASFORMAZIONI_SOAP_VERSION);
			String trasformazioneSoapEnvelope  =  porteApplicativeHelper.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_TRASFORMAZIONI_SOAP_ENVELOPE);
			String trasformazioneSoapEnvelopeTipoS = porteApplicativeHelper.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_TRASFORMAZIONI_SOAP_ENVELOPE_TIPO);
			org.openspcoop2.pdd.core.trasformazioni.TipoTrasformazione trasformazioneSoapEnvelopeTipo =
					trasformazioneSoapEnvelopeTipoS != null ? org.openspcoop2.pdd.core.trasformazioni.TipoTrasformazione.toEnumConstant(trasformazioneSoapEnvelopeTipoS) : 
						org.openspcoop2.pdd.core.trasformazioni.TipoTrasformazione.EMPTY;
			BinaryParameter trasformazioneSoapEnvelopeTemplate = porteApplicativeHelper.getBinaryParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_TRASFORMAZIONI_SOAP_ENVELOPE_TEMPLATE);
			String trasformazioneSoapEnvelopeTipoCheck = porteApplicativeHelper.getParameter(CostantiControlStation.PARAMETRO_CONFIGURAZIONE_TRASFORMAZIONI_SOAP_ENVELOPE_TIPO_CHECK);
			
			// Prendo il nome e il tipo del servizio
			AccordoServizioParteSpecifica asps = apsCore.getAccordoServizioParteSpecifica(Integer.parseInt(idAsps));
			AccordoServizioParteComuneSintetico apc = apcCore.getAccordoServizioSintetico(asps.getIdAccordo()); 
			ServiceBinding serviceBindingMessage = apcCore.toMessageServiceBinding(apc.getServiceBinding());
			
			String postBackElementName = porteApplicativeHelper.getPostBackElementName();
			
			// se ho modificato il soggetto ricalcolo il servizio e il service binding
			if (postBackElementName != null) {
				if(postBackElementName.equals(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_TRASFORMAZIONI_REQ_CONVERSIONE_ENABLED)) {
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
					
					porteApplicativeHelper.deleteBinaryParameters(trasformazioneContenutoTemplate, trasformazioneSoapEnvelopeTemplate);
				}
				
				if(postBackElementName.equals(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_TRASFORMAZIONI_REQ_CONVERSIONE_TIPO)) {
					trasformazioneContenutoTipoCheck = CostantiControlStation.VALUE_PARAMETRO_CONFIGURAZIONE_TRASFORMAZIONI_REQ_CONVERSIONE_TIPO_CHECK_UPDATE_TIPO;
					porteApplicativeHelper.deleteBinaryParameters(trasformazioneContenutoTemplate);
				}
				
				if(postBackElementName.equals(trasformazioneContenutoTemplate.getName()) &&
					StringUtils.isEmpty(trasformazioneContenutoTipoCheck)) {
					trasformazioneContenutoTipoCheck = CostantiControlStation.VALUE_PARAMETRO_CONFIGURAZIONE_TRASFORMAZIONI_REQ_CONVERSIONE_TIPO_CHECK_UPDATE_FILE;
				}
				
				if(postBackElementName.equals(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_TRASFORMAZIONI_SOAP_TRANSFORMATION)) {
					porteApplicativeHelper.deleteBinaryParameters(trasformazioneSoapEnvelopeTemplate);
					if(trasformazioneSoapAbilitato) {
						trasformazioneSoapAction = "";
						trasformazioneSoapEnvelope = CostantiControlStation.VALUE_PARAMETRO_CONFIGURAZIONE_TRASFORMAZIONI_SOAP_ENVELOPE_DISABILITATO;
						trasformazioneSoapEnvelopeTipo = org.openspcoop2.pdd.core.trasformazioni.TipoTrasformazione.EMPTY;
						trasformazioneSoapVersion = CostantiControlStation.DEFAULT_VALUE_PARAMETRO_CONFIGURAZIONE_TRASFORMAZIONI_RICHIESTA_SOAP_VERSION_11;
					} 
				}
				
				if(postBackElementName.equals(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_TRASFORMAZIONI_SOAP_ENVELOPE)) {
					porteApplicativeHelper.deleteBinaryParameters(trasformazioneSoapEnvelopeTemplate);
					if(CostantiControlStation.VALUE_PARAMETRO_CONFIGURAZIONE_TRASFORMAZIONI_SOAP_ENVELOPE_AS_ATTACHMENT.equals(trasformazioneSoapEnvelope)) {
						trasformazioneSoapEnvelopeTipo = org.openspcoop2.pdd.core.trasformazioni.TipoTrasformazione.EMPTY; 
					}
				}
				
				if(postBackElementName.equals(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_TRASFORMAZIONI_SOAP_ENVELOPE_TIPO)) {
					trasformazioneSoapEnvelopeTipoCheck = CostantiControlStation.VALUE_PARAMETRO_CONFIGURAZIONE_TRASFORMAZIONI_REQ_CONVERSIONE_TIPO_CHECK_UPDATE_TIPO;
					porteApplicativeHelper.deleteBinaryParameters(trasformazioneSoapEnvelopeTemplate);
				}
				
				if(postBackElementName.equals(trasformazioneSoapEnvelopeTemplate.getName()) &&
					StringUtils.isEmpty(trasformazioneSoapEnvelopeTipoCheck)) {
					trasformazioneSoapEnvelopeTipoCheck = CostantiControlStation.VALUE_PARAMETRO_CONFIGURAZIONE_TRASFORMAZIONI_REQ_CONVERSIONE_TIPO_CHECK_UPDATE_FILE;
				}
				
				if(postBackElementName.equals(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_TRASFORMAZIONI_REST_TRANSFORMATION) &&
					trasformazioneRestAbilitato) {
					trasformazioneRestMethod = "";
					trasformazioneRestPath = ""; 
				}
			}
			
			String nomeTrasformazione = oldRegola.getNome();
			Parameter pIdTrasformazione = new Parameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_TRASFORMAZIONE, idTrasformazioneS);
			
			// parametri visualizzazione link
			String servletTrasformazioniRichiestaHeadersList = PorteApplicativeCostanti.SERVLET_NAME_PORTE_APPLICATIVE_TRASFORMAZIONI_RICHIESTA_HEADER_LIST;
			String servletTrasformazioniRichiestaParametriList = PorteApplicativeCostanti.SERVLET_NAME_PORTE_APPLICATIVE_TRASFORMAZIONI_RICHIESTA_PARAMETRO_LIST;
			TrasformazioneRegolaRichiesta oldRichiesta = oldRegola.getRichiesta();
			int numeroTrasformazioniRichiestaHeaders= oldRichiesta != null ? oldRichiesta.sizeHeaderList() : 0;
			int numeroTrasformazioniRichiestaParametri = oldRichiesta != null ? oldRichiesta.sizeParametroUrlList() : 0;
			
			List<Parameter> parametriInvocazioneServletTrasformazioniRichiestaHeaders = new ArrayList<>();
			parametriInvocazioneServletTrasformazioniRichiestaHeaders.add(new Parameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID, idPorta));
			parametriInvocazioneServletTrasformazioniRichiestaHeaders.add(new Parameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_SOGGETTO, idsogg));
			parametriInvocazioneServletTrasformazioniRichiestaHeaders.add(new Parameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_ASPS, idAsps));
			parametriInvocazioneServletTrasformazioniRichiestaHeaders.add(pIdTrasformazione);
			List<Parameter> parametriInvocazioneServletTrasformazioniRichiestaParametri = new ArrayList<>();
			parametriInvocazioneServletTrasformazioniRichiestaParametri.add(new Parameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID, idPorta));
			parametriInvocazioneServletTrasformazioniRichiestaParametri.add(new Parameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_SOGGETTO, idsogg));
			parametriInvocazioneServletTrasformazioniRichiestaParametri.add(new Parameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_ASPS, idAsps));
			parametriInvocazioneServletTrasformazioniRichiestaParametri.add(pIdTrasformazione);
			
			List<Parameter> lstParam = porteApplicativeHelper.getTitoloPA(parentPA, idsogg, idAsps);

			String labelPerPorta = null;
			if(parentPA!=null && (parentPA.intValue() == PorteApplicativeCostanti.ATTRIBUTO_PORTE_APPLICATIVE_PARENT_CONFIGURAZIONE)) {
				labelPerPorta = porteApplicativeCore.getLabelRegolaMappingErogazionePortaApplicativa(
						PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_TRASFORMAZIONI_DI,
						PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_TRASFORMAZIONI,
						pa);
			}
			else {
				labelPerPorta = PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_TRASFORMAZIONI_DI+nomePorta;
			}

			lstParam.add(new Parameter(labelPerPorta,
					PorteApplicativeCostanti.SERVLET_NAME_PORTE_APPLICATIVE_TRASFORMAZIONI_LIST,
					new Parameter( PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID, idPorta),
					new Parameter( PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_SOGGETTO, idsogg),
					new Parameter( PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_ASPS, idAsps)
					));
			
			lstParam.add(new Parameter(nomeTrasformazione, PorteApplicativeCostanti.SERVLET_NAME_PORTE_APPLICATIVE_TRASFORMAZIONI_CHANGE, 
					new Parameter( PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID, idPorta),
					new Parameter( PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_SOGGETTO, idsogg),
					new Parameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_ASPS, idAsps), pIdTrasformazione));
			
			lstParam.add(new Parameter(PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_TRASFORMAZIONI_RICHIESTA, null));
			
			ServletUtils.setPageDataTitle(pd, lstParam);
			
			// imposta menu' contestuale
			porteApplicativeHelper.impostaComandiMenuContestualePA(idsogg, idAsps);
			
			// Se nomehid = null, devo visualizzare la pagina per l'inserimento
			// dati
			if (porteApplicativeHelper.isEditModeInProgress()) {
				// preparo i campi
				List<DataElement> dati = new ArrayList<>();
				dati.add(ServletUtils.getDataElementForEditModeFinished());
				
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

				dati = porteApplicativeHelper.addTrasformazioneRichiestaToDati(TipoOperazione.OTHER, protocollo, dati, idInt, oldRichiesta, false, idTrasformazioneS, trasformazioneContenutoAbilitato, trasformazioneContenutoTipo,
						trasformazioneContenutoTemplate, trasformazioneContenutoTipoCheck, trasformazioneRichiestaContentType, serviceBindingMessage, trasformazioneRestAbilitato, 
						trasformazioneRestMethod, trasformazioneRestPath, trasformazioneSoapAbilitato, trasformazioneSoapAction, 
						trasformazioneSoapVersion, trasformazioneSoapEnvelope, trasformazioneSoapEnvelopeTipo, trasformazioneSoapEnvelopeTemplate, trasformazioneSoapEnvelopeTipoCheck, 
						servletTrasformazioniRichiestaHeadersList, parametriInvocazioneServletTrasformazioniRichiestaHeaders, numeroTrasformazioniRichiestaHeaders,
						servletTrasformazioniRichiestaParametriList, parametriInvocazioneServletTrasformazioniRichiestaParametri, numeroTrasformazioniRichiestaParametri,
						apc.getServiceBinding());
				
				dati = porteApplicativeHelper.addHiddenFieldsToDati(TipoOperazione.CHANGE, idPorta, idsogg, idPorta,idAsps,  dati);
				
				pd.setDati(dati);

				ServletUtils.setGeneralAndPageDataIntoSession(request, session, gd, pd);

				return ServletUtils.getStrutsForwardEditModeInProgress(mapping, PorteApplicativeCostanti.OBJECT_NAME_PORTE_APPLICATIVE_TRASFORMAZIONI_RICHIESTA,  PorteApplicativeCostanti.TIPO_OPERAZIONE_TRASFORMAZIONI_RICHIESTA);
			}
			
			boolean isOk = porteApplicativeHelper.trasformazioniRichiestaCheckData(TipoOperazione.CHANGE, oldRegola, serviceBindingMessage);
			if (!isOk) {

				// preparo i campi
				List<DataElement> dati = new ArrayList<>();

				dati.add(ServletUtils.getDataElementForEditModeFinished());
				
				dati = porteApplicativeHelper.addTrasformazioneRichiestaToDati(TipoOperazione.OTHER, protocollo, dati, idInt, oldRichiesta, false, idTrasformazioneS, trasformazioneContenutoAbilitato, trasformazioneContenutoTipo,
						trasformazioneContenutoTemplate, trasformazioneContenutoTipoCheck, trasformazioneRichiestaContentType, serviceBindingMessage, trasformazioneRestAbilitato, 
						trasformazioneRestMethod, trasformazioneRestPath, trasformazioneSoapAbilitato, trasformazioneSoapAction, 
						trasformazioneSoapVersion, trasformazioneSoapEnvelope, trasformazioneSoapEnvelopeTipo, trasformazioneSoapEnvelopeTemplate, trasformazioneSoapEnvelopeTipoCheck,
						servletTrasformazioniRichiestaHeadersList, parametriInvocazioneServletTrasformazioniRichiestaHeaders, numeroTrasformazioniRichiestaHeaders,
						servletTrasformazioniRichiestaParametriList, parametriInvocazioneServletTrasformazioniRichiestaParametri, numeroTrasformazioniRichiestaParametri,
						apc.getServiceBinding());
				
				dati = porteApplicativeHelper.addHiddenFieldsToDati(TipoOperazione.CHANGE, idPorta, idsogg, idPorta,idAsps,  dati);
				
				pd.setDati(dati);

				ServletUtils.setGeneralAndPageDataIntoSession(request, session, gd, pd);

				return ServletUtils.getStrutsForwardEditModeCheckError(mapping, PorteApplicativeCostanti.OBJECT_NAME_PORTE_APPLICATIVE_TRASFORMAZIONI_RICHIESTA,  PorteApplicativeCostanti.TIPO_OPERAZIONE_TRASFORMAZIONI_RICHIESTA);
			}
			
			// aggiorno la regola
			trasformazioni = pa.getTrasformazioni();
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
			
			porteApplicativeCore.performUpdateOperation(userLogin, porteApplicativeHelper.smista(), pa);
			porteApplicativeHelper.deleteBinaryParameters(trasformazioneContenutoTemplate,trasformazioneSoapEnvelopeTemplate);
			
			// ricarico la richiesta
			// ricaricare id trasformazione
			pa = porteApplicativeCore.getPortaApplicativa(Long.parseLong(idPorta));

			TrasformazioneRegola trasformazioneAggiornata = porteApplicativeCore.getTrasformazione(pa.getId(), oldRegola.getNome());
			
			// setto il titolo della pagina
			nomeTrasformazione = oldRegola.getNome();
			pIdTrasformazione = new Parameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_TRASFORMAZIONE, trasformazioneAggiornata.getId() + "");
			
			// parametri visualizzazione link
			// FIX: non devo aggiornarli, tanto non modifichero' direttamente le due liste e il metodo 'getTrasformazione' da cui si ottiene trasformazioneAggiornata non ritorna le due liste
			//numeroTrasformazioniRichiestaHeaders= trasformazioneAggiornata.getRichiesta() != null ? trasformazioneAggiornata.getRichiesta().sizeHeaderList() : 0;
			//numeroTrasformazioniRichiestaParametri = trasformazioneAggiornata.getRichiesta() != null ? trasformazioneAggiornata.getRichiesta().sizeParametroUrlList() : 0;
			
			parametriInvocazioneServletTrasformazioniRichiestaHeaders = new ArrayList<>();
			parametriInvocazioneServletTrasformazioniRichiestaHeaders.add(new Parameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID, idPorta));
			parametriInvocazioneServletTrasformazioniRichiestaHeaders.add(new Parameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_SOGGETTO, idsogg));
			parametriInvocazioneServletTrasformazioniRichiestaHeaders.add(new Parameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_ASPS, idAsps));
			parametriInvocazioneServletTrasformazioniRichiestaHeaders.add(pIdTrasformazione);
			parametriInvocazioneServletTrasformazioniRichiestaParametri = new ArrayList<>();
			parametriInvocazioneServletTrasformazioniRichiestaParametri.add(new Parameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID, idPorta));
			parametriInvocazioneServletTrasformazioniRichiestaParametri.add(new Parameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_SOGGETTO, idsogg));
			parametriInvocazioneServletTrasformazioniRichiestaParametri.add(new Parameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_ASPS, idAsps));
			parametriInvocazioneServletTrasformazioniRichiestaParametri.add(pIdTrasformazione);
			
			lstParam = porteApplicativeHelper.getTitoloPA(parentPA, idsogg, idAsps);

			labelPerPorta = null;
			if(parentPA!=null && (parentPA.intValue() == PorteApplicativeCostanti.ATTRIBUTO_PORTE_APPLICATIVE_PARENT_CONFIGURAZIONE)) {
				labelPerPorta = porteApplicativeCore.getLabelRegolaMappingErogazionePortaApplicativa(
						PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_TRASFORMAZIONI_DI,
						PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_TRASFORMAZIONI,
						pa);
			}
			else {
				labelPerPorta = PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_TRASFORMAZIONI_DI+nomePorta;
			}

			lstParam.add(new Parameter(labelPerPorta,
					PorteApplicativeCostanti.SERVLET_NAME_PORTE_APPLICATIVE_TRASFORMAZIONI_LIST,
					new Parameter( PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID, idPorta),
					new Parameter( PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_SOGGETTO, idsogg),
					new Parameter( PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_ASPS, idAsps)
					));
			
			lstParam.add(new Parameter(nomeTrasformazione, PorteApplicativeCostanti.SERVLET_NAME_PORTE_APPLICATIVE_TRASFORMAZIONI_CHANGE, 
					new Parameter( PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID, idPorta),
					new Parameter( PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_SOGGETTO, idsogg),
					new Parameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_ASPS, idAsps), pIdTrasformazione));
			
			lstParam.add(new Parameter(PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_TRASFORMAZIONI_RICHIESTA, null));
			
			ServletUtils.setPageDataTitle(pd, lstParam);
			
			// preparo i campi
			List<DataElement> dati = new ArrayList<>();
			
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
			
			dati = porteApplicativeHelper.addTrasformazioneRichiestaToDati(TipoOperazione.OTHER, protocollo, dati, idInt, richiesta, false, trasformazioneAggiornata.getId() + "", trasformazioneContenutoAbilitato, trasformazioneContenutoTipo,
					trasformazioneContenutoTemplate, trasformazioneContenutoTipoCheck, trasformazioneRichiestaContentType, serviceBindingMessage, trasformazioneRestAbilitato, 
					trasformazioneRestMethod, trasformazioneRestPath, trasformazioneSoapAbilitato, trasformazioneSoapAction, 
					trasformazioneSoapVersion, trasformazioneSoapEnvelope, trasformazioneSoapEnvelopeTipo, trasformazioneSoapEnvelopeTemplate, trasformazioneSoapEnvelopeTipoCheck,
					servletTrasformazioniRichiestaHeadersList, parametriInvocazioneServletTrasformazioniRichiestaHeaders, numeroTrasformazioniRichiestaHeaders,
					servletTrasformazioniRichiestaParametriList, parametriInvocazioneServletTrasformazioniRichiestaParametri, numeroTrasformazioniRichiestaParametri,
					apc.getServiceBinding());
			
			dati = porteApplicativeHelper.addHiddenFieldsToDati(TipoOperazione.CHANGE, idPorta, idsogg, idPorta,idAsps,  dati);
			
			pd.setDati(dati);
						
			pd.setMessage(CostantiControlStation.LABEL_AGGIORNAMENTO_EFFETTUATO_CON_SUCCESSO, Costanti.MESSAGE_TYPE_INFO);
			dati.add(ServletUtils.getDataElementForEditModeFinished());
			
			ServletUtils.setGeneralAndPageDataIntoSession(request, session, gd, pd);
			
			// Forward control to the specified success URI
			return ServletUtils.getStrutsForwardEditModeFinished(mapping, PorteApplicativeCostanti.OBJECT_NAME_PORTE_APPLICATIVE_TRASFORMAZIONI_RICHIESTA,  PorteApplicativeCostanti.TIPO_OPERAZIONE_TRASFORMAZIONI_RICHIESTA);
		} catch (Exception e) {
			return ServletUtils.getStrutsForwardError(ControlStationCore.getLog(), e, pd, request, session, gd, mapping, 
					PorteApplicativeCostanti.OBJECT_NAME_PORTE_APPLICATIVE_TRASFORMAZIONI_RICHIESTA,  PorteApplicativeCostanti.TIPO_OPERAZIONE_TRASFORMAZIONI_RICHIESTA	);
		}
	}
}
