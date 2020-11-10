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
package org.openspcoop2.web.ctrlstat.servlet.protocol_properties;

import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.openspcoop2.core.config.ServizioApplicativo;
import org.openspcoop2.core.id.IDAccordo;
import org.openspcoop2.core.id.IDAccordoAzione;
import org.openspcoop2.core.id.IDFruizione;
import org.openspcoop2.core.id.IDPortType;
import org.openspcoop2.core.id.IDPortTypeAzione;
import org.openspcoop2.core.id.IDResource;
import org.openspcoop2.core.id.IDServizio;
import org.openspcoop2.core.id.IDServizioApplicativo;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.core.mapping.ProprietariProtocolProperty;
import org.openspcoop2.core.registry.AccordoCooperazione;
import org.openspcoop2.core.registry.AccordoServizioParteComune;
import org.openspcoop2.core.registry.AccordoServizioParteSpecifica;
import org.openspcoop2.core.registry.Azione;
import org.openspcoop2.core.registry.Fruitore;
import org.openspcoop2.core.registry.Operation;
import org.openspcoop2.core.registry.PortType;
import org.openspcoop2.core.registry.ProtocolProperty;
import org.openspcoop2.core.registry.Resource;
import org.openspcoop2.core.registry.Soggetto;
import org.openspcoop2.core.registry.beans.AccordoServizioParteComuneSintetico;
import org.openspcoop2.core.registry.beans.PortTypeSintetico;
import org.openspcoop2.core.registry.constants.StatiAccordo;
import org.openspcoop2.core.registry.driver.BeanUtilities;
import org.openspcoop2.core.registry.driver.DriverRegistroServiziException;
import org.openspcoop2.core.registry.driver.DriverRegistroServiziNotFound;
import org.openspcoop2.protocol.sdk.ProtocolException;
import org.openspcoop2.protocol.sdk.constants.ConsoleOperationType;
import org.openspcoop2.protocol.sdk.properties.AbstractConsoleItem;
import org.openspcoop2.protocol.sdk.properties.BinaryConsoleItem;
import org.openspcoop2.protocol.sdk.properties.ConsoleConfiguration;
import org.openspcoop2.protocol.sdk.properties.IConsoleDynamicConfiguration;
import org.openspcoop2.protocol.sdk.properties.ProtocolProperties;
import org.openspcoop2.protocol.sdk.registry.IConfigIntegrationReader;
import org.openspcoop2.protocol.sdk.registry.IRegistryReader;
import org.openspcoop2.web.ctrlstat.core.ControlStationCore;
import org.openspcoop2.web.ctrlstat.servlet.ConsoleHelper;
import org.openspcoop2.web.ctrlstat.servlet.ac.AccordiCooperazioneCostanti;
import org.openspcoop2.web.ctrlstat.servlet.apc.AccordiServizioParteComuneCostanti;
import org.openspcoop2.web.ctrlstat.servlet.apc.AccordiServizioParteComuneHelper;
import org.openspcoop2.web.ctrlstat.servlet.apc.AccordiServizioParteComuneUtilities;
import org.openspcoop2.web.ctrlstat.servlet.apc.api.ApiCostanti;
import org.openspcoop2.web.ctrlstat.servlet.aps.AccordiServizioParteSpecificaCostanti;
import org.openspcoop2.web.ctrlstat.servlet.aps.AccordiServizioParteSpecificaHelper;
import org.openspcoop2.web.ctrlstat.servlet.archivi.ArchiviCostanti;
import org.openspcoop2.web.ctrlstat.servlet.pd.PorteDelegateCostanti;
import org.openspcoop2.web.ctrlstat.servlet.pd.PorteDelegateHelper;
import org.openspcoop2.web.ctrlstat.servlet.sa.ServiziApplicativiCostanti;
import org.openspcoop2.web.ctrlstat.servlet.soggetti.SoggettiCostanti;
import org.openspcoop2.web.lib.mvc.BinaryParameter;
import org.openspcoop2.web.lib.mvc.Costanti;
import org.openspcoop2.web.lib.mvc.DataElement;
import org.openspcoop2.web.lib.mvc.DataElementType;
import org.openspcoop2.web.lib.mvc.PageData;
import org.openspcoop2.web.lib.mvc.Parameter;
import org.openspcoop2.web.lib.mvc.ServletUtils;
import org.openspcoop2.web.lib.mvc.TipoOperazione;

/**
 * ProtocolPropertiesHelper
 *
 * @author Pintori Giuliano (pintori@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ProtocolPropertiesHelper extends ConsoleHelper {

	public ProtocolPropertiesHelper(HttpServletRequest request, PageData pd, HttpSession session) {
		super(request, pd, session);
	}
	public ProtocolPropertiesHelper(ControlStationCore core, HttpServletRequest request, PageData pd, HttpSession session) {
		super(core, request, pd, session);
	}

	public Object getIdOggettoProprietario(Object proprietario, String idProprietario, String nomeProprieta, String nomeParentProprieta, ProprietariProtocolProperty tipoProprietario, String tipoAccordo) throws Exception {
		try{
			if(tipoProprietario != null && proprietario != null && proprietario != null){
				switch (tipoProprietario) {
				case ACCORDO_COOPERAZIONE:
					AccordoCooperazione ac = (AccordoCooperazione) proprietario;
					IDAccordo idAccordoCooperazione = this.idAccordoFactory.getIDAccordoFromValues(ac.getNome(),BeanUtilities.getSoggettoReferenteID(ac.getSoggettoReferente()),ac.getVersione());
					return idAccordoCooperazione;
				case ACCORDO_SERVIZIO_PARTE_COMUNE:
					AccordoServizioParteComune as = (AccordoServizioParteComune) proprietario;
					IDAccordo idAccordo = this.idAccordoFactory.getIDAccordoFromValues(as.getNome(),BeanUtilities.getSoggettoReferenteID(as.getSoggettoReferente()),as.getVersione());
					return idAccordo;
				case ACCORDO_SERVIZIO_PARTE_SPECIFICA:
					AccordoServizioParteSpecifica aps = (AccordoServizioParteSpecifica) proprietario;
					IDServizio idAps = this.idServizioFactory.getIDServizioFromAccordo(aps);
					return idAps;
				case AZIONE_ACCORDO:
					Azione azione = (Azione) proprietario;
					AccordoServizioParteComuneSintetico apca = this.apcCore.getAccordoServizioSintetico(Integer.parseInt(idProprietario));
					IDAccordo idAccordoAz = this.idAccordoFactory.getIDAccordoFromValues(apca.getNome(),BeanUtilities.getSoggettoReferenteID(apca.getSoggettoReferente()),apca.getVersione());
					IDAccordoAzione idAccordoAzione = new IDAccordoAzione();
					idAccordoAzione.setIdAccordo(idAccordoAz);
					idAccordoAzione.setNome(azione.getNome());

					return idAccordoAzione;
				case FRUITORE:
					Fruitore fruitore = (Fruitore) proprietario; 
					IDSoggetto idSoggettoFruitore = new IDSoggetto(fruitore.getTipo(), fruitore.getNome()); 
					AccordoServizioParteSpecifica apsFrui = this.apsCore.getAccordoServizioParteSpecifica(fruitore.getIdServizio());
					IDServizio idApsFrui = this.idServizioFactory.getIDServizioFromAccordo(apsFrui);
					IDFruizione idFruizione = new IDFruizione();
					idFruizione.setIdServizio(idApsFrui);
					idFruizione.setIdFruitore(idSoggettoFruitore);

					return idFruizione;
				case OPERATION:
					org.openspcoop2.core.registry.Operation azionePt = (Operation) proprietario;
					IDPortType idPortTypeAz = new IDPortType();
					int idProp = Integer.parseInt(idProprietario);
					AccordoServizioParteComuneSintetico apc = this.apcCore.getAccordoServizioSintetico(idProp);
					for (PortTypeSintetico pt : apc.getPortType()) {
						if(pt.getNome().equals(nomeParentProprieta)){
							IDAccordo idAccordoPt = this.idAccordoFactory.getIDAccordoFromValues(apc.getNome(),BeanUtilities.getSoggettoReferenteID(apc.getSoggettoReferente()),apc.getVersione());
							idPortTypeAz.setIdAccordo(idAccordoPt);
							idPortTypeAz.setNome(pt.getNome());
							break;
						}
					}

					IDPortTypeAzione idAzionePt = new IDPortTypeAzione();
					idAzionePt.setIdPortType(idPortTypeAz); 
					idAzionePt.setNome(azionePt.getNome());

					return idAzionePt;
				case PORT_TYPE:
					PortType pt = (PortType) proprietario;
					IDPortType idPt = new IDPortType();
					AccordoServizioParteComuneSintetico apcPt = this.apcCore.getAccordoServizioSintetico(pt.getIdAccordo());
					IDAccordo idAccordoPt = this.idAccordoFactory.getIDAccordoFromValues(apcPt.getNome(),BeanUtilities.getSoggettoReferenteID(apcPt.getSoggettoReferente()),apcPt.getVersione());
					idPt.setIdAccordo(idAccordoPt);
					idPt.setNome(pt.getNome());

					return idPt;
				case RESOURCE:
					org.openspcoop2.core.registry.Resource risorsa = (org.openspcoop2.core.registry.Resource) proprietario;
					AccordoServizioParteComuneSintetico apcr = this.apcCore.getAccordoServizioSintetico(Integer.parseInt(idProprietario));
					IDAccordo idAccordoRisorsa = this.idAccordoFactory.getIDAccordoFromValues(apcr.getNome(),
							BeanUtilities.getSoggettoReferenteID(apcr.getSoggettoReferente()),apcr.getVersione());
					IDResource idAccordoR = new IDResource();
					idAccordoR.setIdAccordo(idAccordoRisorsa);
					idAccordoR.setNome(risorsa.getNome());

					return idAccordoR;
				case SOGGETTO:
					Soggetto soggettoRegistro = (Soggetto) proprietario;
					IDSoggetto idSoggetto = new IDSoggetto(soggettoRegistro.getTipo(), soggettoRegistro.getNome()); 
					return idSoggetto;
				case SERVIZIO_APPLICATIVO:
					ServizioApplicativo servizioApplicativo = (ServizioApplicativo) proprietario;
					IDServizioApplicativo idServizioApplicativo = new IDServizioApplicativo();
					idServizioApplicativo.setIdSoggettoProprietario(new IDSoggetto(servizioApplicativo.getTipoSoggettoProprietario(), servizioApplicativo.getNomeSoggettoProprietario()));
					idServizioApplicativo.setNome(servizioApplicativo.getNome());
					return idServizioApplicativo;
					
				}
			}

		}  catch (DriverRegistroServiziException e) {
			throw e;
		}
		return null;
	}

	public Object getOggettoProprietario(String idProprietario, String nomeProprieta, String nomeParentProprieta, ProprietariProtocolProperty tipoProprietario, String tipoAccordo) throws Exception {
		try{
			if(tipoProprietario != null && idProprietario != null){
				long idProp = Long.valueOf(idProprietario);

				switch (tipoProprietario) {
				case ACCORDO_COOPERAZIONE:
					AccordoCooperazione ac = this.acCore.getAccordoCooperazione(idProp);
					return ac;
				case ACCORDO_SERVIZIO_PARTE_COMUNE:
					AccordoServizioParteComune as = this.apcCore.getAccordoServizioFull(idProp);
					return as;
				case ACCORDO_SERVIZIO_PARTE_SPECIFICA:
					AccordoServizioParteSpecifica aps = this.apsCore.getAccordoServizioParteSpecifica(idProp);
					return aps;
				case AZIONE_ACCORDO:
					AccordoServizioParteComune apca = this.apcCore.getAccordoServizioFull(idProp);
					for(Azione azione: apca.getAzioneList()){
						if(azione.getNome().equals(nomeProprieta))
							return azione;
					}
					return null;
				case FRUITORE:
					Fruitore servFru = this.apsCore.getServizioFruitore(idProp);
					return servFru;
				case OPERATION:
					AccordoServizioParteComune apcop = this.apcCore.getAccordoServizioFull(idProp);
					for (PortType pt : apcop.getPortTypeList()) {
						if(pt.getNome().equals(nomeParentProprieta)){
							for (org.openspcoop2.core.registry.Operation azione : pt.getAzioneList()) {
								if(azione.getNome().equals(nomeProprieta ))
									return azione;
							}
						}
					}
					return null;
				case PORT_TYPE:
					AccordoServizioParteComune apc = this.apcCore.getAccordoServizioFull(idProp);
					for (PortType pt : apc.getPortTypeList()) {
						if(pt.getNome().equals(nomeProprieta))
							return pt;
					}
					return null;
				case RESOURCE:
					AccordoServizioParteComune apcr = this.apcCore.getAccordoServizioFull(idProp);
					for(Resource risorsa: apcr.getResourceList()){
						if(risorsa.getNome().equals(nomeProprieta))
							return risorsa;
					}
					return null;
				case SOGGETTO:
					Soggetto soggettoRegistro = this.soggettiCore.getSoggettoRegistro(idProp);
					return soggettoRegistro;
				case SERVIZIO_APPLICATIVO:
					ServizioApplicativo servizioApplicativo = this.saCore.getServizioApplicativo(idProp);
					return servizioApplicativo;
				}
			}

		}  catch (DriverRegistroServiziNotFound e) {
			throw e;
		} catch (DriverRegistroServiziException e) {
			throw e;
		}
		return null;
	}
	
	public String getStatoOggettoProprietario(String idProprietario, String nomeProprieta, String nomeParentProprieta, ProprietariProtocolProperty tipoProprietario, String tipoAccordo) throws Exception {
		try{
			if(tipoProprietario != null && idProprietario != null){
				int idProp = Integer.parseInt(idProprietario);

				switch (tipoProprietario) {
				case ACCORDO_COOPERAZIONE:
					AccordoCooperazione ac = this.acCore.getAccordoCooperazione(idProp);
					return ac.getStatoPackage();
				case ACCORDO_SERVIZIO_PARTE_COMUNE:
					AccordoServizioParteComuneSintetico as = this.apcCore.getAccordoServizioSintetico(idProp);
					return as.getStatoPackage();
				case ACCORDO_SERVIZIO_PARTE_SPECIFICA:
					AccordoServizioParteSpecifica aps = this.apsCore.getAccordoServizioParteSpecifica(idProp);
					return aps.getStatoPackage();
				case AZIONE_ACCORDO:
					AccordoServizioParteComuneSintetico apca = this.apcCore.getAccordoServizioSintetico(idProp);
					return apca.getStatoPackage();
				case FRUITORE:
					Fruitore servFru = this.apsCore.getServizioFruitore(idProp);
					return servFru.getStatoPackage();
				case OPERATION:
					AccordoServizioParteComuneSintetico apcop = this.apcCore.getAccordoServizioSintetico(idProp);
					return apcop.getStatoPackage();
				case PORT_TYPE:
					AccordoServizioParteComuneSintetico apc = this.apcCore.getAccordoServizioSintetico(idProp);
					return apc.getStatoPackage();
				case RESOURCE:
					AccordoServizioParteComuneSintetico apcr = this.apcCore.getAccordoServizioSintetico(idProp);
					return apcr.getStatoPackage();
				case SOGGETTO:
					return null;
				case SERVIZIO_APPLICATIVO:
					return null;
				}
			}
		}  catch (DriverRegistroServiziNotFound e) {
			throw e;
		} catch (DriverRegistroServiziException e) {
			throw e;
		}
		return null;
	}

	public Vector<DataElement> addProtocolPropertyChangeToDati(TipoOperazione tipoOp, Vector<DataElement> dati, String protocollo, String id, String nome,
			String idProprietario, ProprietariProtocolProperty tipoProprietario, String tipoAccordo, String nomeProprietario,String nomeParentProprietario, String urlChange, String label,
			BinaryParameter contenutoDocumento, StringBuilder contenutoDocumentoStringBuilder, String errore, String tipologiaDocumentoScaricare, AbstractConsoleItem<?> binaryConsoleItem,
			boolean readOnly) throws Exception { 

		/* ID */
		DataElement de = new DataElement();
		de.setLabel(ProtocolPropertiesCostanti.PARAMETRO_PP_ID);
		de.setValue(id);
		de.setType(DataElementType.HIDDEN);
		de.setName(ProtocolPropertiesCostanti.PARAMETRO_PP_ID);
		dati.addElement(de);

		/* CHANGE_BINARY */
		de = new DataElement();
		de.setLabel(ProtocolPropertiesCostanti.PARAMETRO_PP_CHANGE_BINARY);
		de.setValue(ProtocolPropertiesCostanti.PARAMETRO_PP_CHANGE_BINARY_VALUE_TRUE);
		de.setType(DataElementType.HIDDEN);
		de.setName(ProtocolPropertiesCostanti.PARAMETRO_PP_CHANGE_BINARY);
		dati.addElement(de);
		
		/* NOME */
		de = new DataElement();
		de.setLabel(ProtocolPropertiesCostanti.PARAMETRO_PP_NOME);
		de.setValue(nome);
		de.setType(DataElementType.HIDDEN);
		de.setName(ProtocolPropertiesCostanti.PARAMETRO_PP_NOME);
		dati.addElement(de);

		/* ID_PROPRIETARIO */
		de = new DataElement();
		de.setLabel(ProtocolPropertiesCostanti.PARAMETRO_PP_ID_PROPRIETARIO);
		de.setValue(idProprietario);
		de.setType(DataElementType.HIDDEN);
		de.setName(ProtocolPropertiesCostanti.PARAMETRO_PP_ID_PROPRIETARIO);
		dati.addElement(de);

		/* PROTOCOLLO */
		de = new DataElement();
		de.setLabel(ProtocolPropertiesCostanti.PARAMETRO_PP_PROTOCOLLO);
		de.setValue(protocollo);
		de.setType(DataElementType.HIDDEN);
		de.setName(ProtocolPropertiesCostanti.PARAMETRO_PP_PROTOCOLLO);
		dati.addElement(de);

		/* TIPO_PROPRIETARIO */
		de = new DataElement();
		de.setLabel(ProtocolPropertiesCostanti.PARAMETRO_PP_TIPO_PROPRIETARIO);
		de.setValue(tipoProprietario.toString());
		de.setType(DataElementType.HIDDEN);
		de.setName(ProtocolPropertiesCostanti.PARAMETRO_PP_TIPO_PROPRIETARIO);
		dati.addElement(de);

		/* TIPO_ACCORDO */
		de = new DataElement();
		de.setLabel(ProtocolPropertiesCostanti.PARAMETRO_PP_TIPO_ACCORDO);
		de.setValue(tipoAccordo);
		de.setType(DataElementType.HIDDEN);
		de.setName(ProtocolPropertiesCostanti.PARAMETRO_PP_TIPO_ACCORDO);
		dati.addElement(de);

		/* NOME_PROPRIETARIO */
		de = new DataElement();
		de.setLabel(ProtocolPropertiesCostanti.PARAMETRO_PP_NOME_PROPRIETARIO);
		de.setValue(nomeProprietario);
		de.setType(DataElementType.HIDDEN);
		de.setName(ProtocolPropertiesCostanti.PARAMETRO_PP_NOME_PROPRIETARIO);
		dati.addElement(de);

		/* NOME_PARENT_PROPRIETARIO */
		de = new DataElement();
		de.setLabel(ProtocolPropertiesCostanti.PARAMETRO_PP_NOME_PARENT_PROPRIETARIO);
		de.setValue(nomeParentProprietario);
		de.setType(DataElementType.HIDDEN);
		de.setName(ProtocolPropertiesCostanti.PARAMETRO_PP_NOME_PARENT_PROPRIETARIO);
		dati.addElement(de);

		/* URL_CHANGE */
		de = new DataElement();
		de.setLabel(ProtocolPropertiesCostanti.PARAMETRO_PP_URL_ORIGINALE_CHANGE);
		de.setValue(urlChange);
		de.setType(DataElementType.HIDDEN);
		de.setName(ProtocolPropertiesCostanti.PARAMETRO_PP_URL_ORIGINALE_CHANGE);
		dati.addElement(de);
		
		de = new DataElement();
		de.setLabel(binaryConsoleItem.getLabel());
		de.setType(DataElementType.TITLE);
		dati.addElement(de);
		
		boolean showContent = true;
		if(binaryConsoleItem instanceof BinaryConsoleItem) {
			showContent = ((BinaryConsoleItem)binaryConsoleItem).isShowContent();
		}
		
		String statoPackage = this.getStatoOggettoProprietario(idProprietario, nomeProprietario, nomeParentProprietario, tipoProprietario, tipoAccordo);

		// solo per le properties con stato finale blocco l'edit
		if(this.isShowGestioneWorkflowStatoDocumenti() && statoPackage != null && StatiAccordo.finale.toString().equals(statoPackage)) {
			this.pd.disableEditMode();
			
			if(contenutoDocumento != null && contenutoDocumento.getValue() != null && contenutoDocumento.getValue().length > 0){
				de = new DataElement();
				de.setLabel(ProtocolPropertiesCostanti.LABEL_NOME);
				de.setType(DataElementType.TEXT);
				de.setValue(contenutoDocumento.getFilename());
				dati.addElement(de);
				
				if(showContent) {
					if(errore!=null){
						de = new DataElement();
						de.setValue(errore);
						de.setLabel(binaryConsoleItem.getLabel());
						de.setType(DataElementType.TEXT);
						de.setSize(this.getSize());
						dati.addElement(de);
					}
					else{
						de = new DataElement();
						de.setLabel(ProtocolPropertiesCostanti.LABEL_DOCUMENTO_ATTUALE);
						de.setType(DataElementType.TEXT_AREA_NO_EDIT);
						de.setValue(contenutoDocumentoStringBuilder.toString());
						de.setRows(30);
						de.setCols(100);
						dati.addElement(de);
					}
				}
					
			}else {
				de = new DataElement();
//				de.setLabel(binaryConsoleItem.getLabel());
				de.setType(DataElementType.TEXT);
				de.setValue(ProtocolPropertiesCostanti.LABEL_DOCUMENTO_NOT_FOUND);
				de.setSize(this.getSize());
				dati.addElement(de);
			}
			
			
			if(id != null && !"".equals(id)){
				DataElement saveAs = new DataElement();
				saveAs.setValue(ProtocolPropertiesCostanti.LABEL_DOWNLOAD);
				saveAs.setType(DataElementType.LINK);
				saveAs.setUrl(ArchiviCostanti.SERVLET_NAME_DOCUMENTI_EXPORT, 
						new Parameter(ProtocolPropertiesCostanti.PARAMETRO_PP_ID_ALLEGATO, id),
						new Parameter(ArchiviCostanti.PARAMETRO_ARCHIVI_ALLEGATO_TIPO_ACCORDO_TIPO_DOCUMENTO, tipologiaDocumentoScaricare),
						new Parameter(ArchiviCostanti.PARAMETRO_ARCHIVI_ALLEGATO_TIPO_ACCORDO, ArchiviCostanti.PARAMETRO_VALORE_ARCHIVI_ALLEGATO_TIPO_PROTOCOL_PROPERTY));
				saveAs.setDisabilitaAjaxStatus();
				dati.add(saveAs);
			}
			
			de = new DataElement();
			de.setType(DataElementType.HIDDEN);
			de.setName(Costanti.PARAMETER_FILENAME_PREFIX + ProtocolPropertiesCostanti.PARAMETRO_PP_CONTENUTO_DOCUMENTO);
			de.setValue(contenutoDocumento.getFilename());
			dati.addElement(de);
	
			de = new DataElement();
			de.setType(DataElementType.HIDDEN);
			de.setName(Costanti.PARAMETER_FILEID_PREFIX + ProtocolPropertiesCostanti.PARAMETRO_PP_CONTENUTO_DOCUMENTO);
			de.setValue(contenutoDocumento.getId());
			dati.addElement(de);
		}else {
			/* Contenuto Documento */
			if(contenutoDocumento != null && contenutoDocumento.getValue() != null && contenutoDocumento.getValue().length > 0){
				de = new DataElement();
				de.setLabel(ProtocolPropertiesCostanti.LABEL_NOME);
				de.setType(DataElementType.TEXT);
				de.setValue(contenutoDocumento.getFilename());
				dati.addElement(de);
	
				if(showContent) {
					if(errore!=null){
						de = new DataElement();
						de.setValue(errore);
						de.setLabel(binaryConsoleItem.getLabel());
						de.setType(DataElementType.TEXT);
						de.setSize(this.getSize());
						dati.addElement(de);
					}
					else{
						de = new DataElement();
						de.setLabel(ProtocolPropertiesCostanti.LABEL_DOCUMENTO_ATTUALE);
						de.setType(DataElementType.TEXT_AREA_NO_EDIT);
						de.setValue(contenutoDocumentoStringBuilder.toString());
						de.setRows(30);
						de.setCols(100);
						dati.addElement(de);
					}
				}
	
				if(id != null && !"".equals(id)){
					DataElement saveAs = new DataElement();
					saveAs.setValue(ProtocolPropertiesCostanti.LABEL_DOWNLOAD);
					saveAs.setType(DataElementType.LINK);
					saveAs.setUrl(ArchiviCostanti.SERVLET_NAME_DOCUMENTI_EXPORT, 
							new Parameter(ProtocolPropertiesCostanti.PARAMETRO_PP_ID_ALLEGATO, id),
							new Parameter(ArchiviCostanti.PARAMETRO_ARCHIVI_ALLEGATO_TIPO_ACCORDO_TIPO_DOCUMENTO, tipologiaDocumentoScaricare),
							new Parameter(ArchiviCostanti.PARAMETRO_ARCHIVI_ALLEGATO_TIPO_ACCORDO, ArchiviCostanti.PARAMETRO_VALORE_ARCHIVI_ALLEGATO_TIPO_PROTOCOL_PROPERTY));
					saveAs.setDisabilitaAjaxStatus();
					dati.add(saveAs);
				}
	
				if(!readOnly) {
					de = new DataElement();
					de.setType(DataElementType.TITLE);
					de.setLabel(ProtocolPropertiesCostanti.LABEL_AGGIORNAMENTO +" " +binaryConsoleItem.getLabel());
					de.setValue("");
					de.setSize(this.getSize());
					dati.addElement(de);
				}
	
			}else {
				de = new DataElement();
				de.setLabel(ProtocolPropertiesCostanti.LABEL_DOCUMENTO_ATTUALE);
				de.setType(DataElementType.TEXT);
				de.setValue(ProtocolPropertiesCostanti.LABEL_DOCUMENTO_NOT_FOUND);
				dati.addElement(de);
			}
	
			if(!readOnly) {
				de = new DataElement();
				de.setLabel(ProtocolPropertiesCostanti.LABEL_CONTENUTO_NUOVO);
				de.setValue("");
				de.setType(DataElementType.FILE);
				de.setName(ProtocolPropertiesCostanti.PARAMETRO_PP_CONTENUTO_DOCUMENTO);
				de.setSize(this.getSize());
				de.setRequired(binaryConsoleItem.isRequired()); 
				dati.addElement(de);
			}
	
			de = new DataElement();
			de.setType(DataElementType.HIDDEN);
			de.setName(Costanti.PARAMETER_FILENAME_PREFIX + ProtocolPropertiesCostanti.PARAMETRO_PP_CONTENUTO_DOCUMENTO);
			de.setValue(contenutoDocumento.getFilename());
			dati.addElement(de);
	
			de = new DataElement();
			de.setType(DataElementType.HIDDEN);
			de.setName(Costanti.PARAMETER_FILEID_PREFIX + ProtocolPropertiesCostanti.PARAMETRO_PP_CONTENUTO_DOCUMENTO);
			de.setValue(contenutoDocumento.getId());
			dati.addElement(de);
	
			if(!readOnly) {
				if(contenutoDocumento != null && contenutoDocumento.getValue() != null && contenutoDocumento.getValue().length > 0 && !binaryConsoleItem.isRequired()){
					de = new DataElement();
					de.setBold(true);
					de.setLabel(ProtocolPropertiesCostanti.LABEL_DOCUMENTO_CHANGE_CLEAR_WARNING);
					de.setValue(ProtocolPropertiesCostanti.LABEL_DOCUMENTO_CHANGE_CLEAR);
					de.setType(DataElementType.NOTE);
					de.setName(ProtocolPropertiesCostanti.PARAMETRO_PP_CONTENUTO_DOCUMENTO_WARN);
					de.setSize(this.getSize());
					dati.addElement(de);
				}
			}
	
			if(readOnly) {
				this.pd.disableEditMode();
			}
		}
		
		return dati;
	}

	public ConsoleConfiguration getConsoleDynamicConfiguration(Object idOggettoProprietario, String idProprietario,
			String nomeProprietario, String nomeParentProprietario, ProprietariProtocolProperty tipoProprietario,
			String tipoAccordo, ConsoleOperationType consoleOperationType, 
			IRegistryReader registryReader,IConfigIntegrationReader configRegistryReader,IConsoleDynamicConfiguration consoleDynamicConfiguration) throws ProtocolException{
		try{
			if(tipoProprietario != null && idProprietario != null && idOggettoProprietario != null){
				switch (tipoProprietario) {
				case ACCORDO_COOPERAZIONE:
					IDAccordo idAccordoCooperazione = (IDAccordo) idOggettoProprietario;
					return consoleDynamicConfiguration.getDynamicConfigAccordoCooperazione(consoleOperationType, this, 
							registryReader, configRegistryReader, idAccordoCooperazione);
				case ACCORDO_SERVIZIO_PARTE_COMUNE:
					IDAccordo idApc = (IDAccordo) idOggettoProprietario;
					if(tipoAccordo.equals(ProtocolPropertiesCostanti.PARAMETRO_VALORE_PP_TIPO_ACCORDO_PARTE_COMUNE)){
						return consoleDynamicConfiguration.getDynamicConfigAccordoServizioParteComune(consoleOperationType, this, 
								registryReader, configRegistryReader, idApc);
					} else {
						return consoleDynamicConfiguration.getDynamicConfigAccordoServizioComposto(consoleOperationType, this, 
								registryReader, configRegistryReader, idApc);
					}
				case ACCORDO_SERVIZIO_PARTE_SPECIFICA:
					IDServizio idAps =(IDServizio) idOggettoProprietario;			
					return consoleDynamicConfiguration.getDynamicConfigAccordoServizioParteSpecifica(consoleOperationType, this, 
							registryReader, configRegistryReader, idAps);
				case AZIONE_ACCORDO:
					IDAccordoAzione idAccordoAzione = (IDAccordoAzione) idOggettoProprietario;
					return consoleDynamicConfiguration.getDynamicConfigAzione(consoleOperationType, this, 
							registryReader, configRegistryReader, idAccordoAzione);
				case FRUITORE:
					IDFruizione idFruizione = (IDFruizione) idOggettoProprietario;
					return consoleDynamicConfiguration.getDynamicConfigFruizioneAccordoServizioParteSpecifica(consoleOperationType, this, 
							registryReader, configRegistryReader, idFruizione);
				case OPERATION:
					IDPortTypeAzione idAzionePt = (IDPortTypeAzione) idOggettoProprietario;
					return consoleDynamicConfiguration.getDynamicConfigOperation(consoleOperationType, this, 
							registryReader, configRegistryReader, idAzionePt);
				case PORT_TYPE:
					IDPortType idPt = (IDPortType) idOggettoProprietario;
					return consoleDynamicConfiguration.getDynamicConfigPortType(consoleOperationType, this, 
							registryReader, configRegistryReader, idPt);
				case RESOURCE:
					IDResource idAccordoRisorsa = (IDResource) idOggettoProprietario;
					String httpMethod = null;
					String path = null;
					try {
						Resource r = registryReader.getResourceAccordo(idAccordoRisorsa);
						httpMethod = r.getMethod()!=null ? r.getMethod().getValue() : null;
						path = r.getPath();
					}catch(Exception e) {}
					return consoleDynamicConfiguration.getDynamicConfigResource(consoleOperationType, this, 
							registryReader, configRegistryReader, idAccordoRisorsa, httpMethod, path);
				case SOGGETTO:
					IDSoggetto idSoggetto = (IDSoggetto) idOggettoProprietario;
					return consoleDynamicConfiguration.getDynamicConfigSoggetto(consoleOperationType, this, 
							registryReader, configRegistryReader, idSoggetto);
				case SERVIZIO_APPLICATIVO:
					IDServizioApplicativo idServizioApplicativo = (IDServizioApplicativo) idOggettoProprietario;
					return consoleDynamicConfiguration.getDynamicConfigServizioApplicativo(consoleOperationType, this, 
							registryReader, configRegistryReader, idServizioApplicativo);
				}
			}

		}  catch (ProtocolException e) {
			throw e;
		} 
		return null;
	}

	public boolean isProtocolPropertiesRegistry(ProprietariProtocolProperty tipoProprietario){
		try{
			if(tipoProprietario != null){
				switch (tipoProprietario) {
				case ACCORDO_COOPERAZIONE:
				case ACCORDO_SERVIZIO_PARTE_COMUNE:
				case ACCORDO_SERVIZIO_PARTE_SPECIFICA:
				case AZIONE_ACCORDO:
				case FRUITORE:
				case OPERATION:
				case PORT_TYPE:
				case RESOURCE:
				case SOGGETTO:
					return true;
				case SERVIZIO_APPLICATIVO:
					return false;
				}
			}
		}  catch (Exception e) {
			throw e;
		} 

		return false;
	}
	
	public List<ProtocolProperty> getProtocolPropertiesRegistry(Object proprietario, String id, String nome, String idProprietario, String nomeProprietario,
			String nomeParentProprietario, ProprietariProtocolProperty tipoProprietario, String tipoAccordo) throws Exception {
		try{
			if(proprietario != null){
				List<ProtocolProperty> protocolPropertyList  = null;
				switch (tipoProprietario) {
				case ACCORDO_COOPERAZIONE:
					AccordoCooperazione ac = (AccordoCooperazione) proprietario;
					protocolPropertyList = ac.getProtocolPropertyList();
					break;
				case ACCORDO_SERVIZIO_PARTE_COMUNE:
					AccordoServizioParteComune as = (AccordoServizioParteComune) proprietario;
					protocolPropertyList = as.getProtocolPropertyList();
					break;
				case ACCORDO_SERVIZIO_PARTE_SPECIFICA:
					AccordoServizioParteSpecifica aps = (AccordoServizioParteSpecifica) proprietario;
					protocolPropertyList = aps.getProtocolPropertyList();
					break;
				case AZIONE_ACCORDO:
					Azione azione = (Azione) proprietario;
					protocolPropertyList = azione.getProtocolPropertyList();
					break;
				case FRUITORE:
					Fruitore fruitore = (Fruitore) proprietario; 
					protocolPropertyList = fruitore.getProtocolPropertyList();
					break;
				case OPERATION:
					org.openspcoop2.core.registry.Operation azionePt = (Operation) proprietario;
					protocolPropertyList = azionePt.getProtocolPropertyList();
					break;
				case PORT_TYPE:
					PortType pt = (PortType) proprietario;
					protocolPropertyList = pt.getProtocolPropertyList();
					break;
				case RESOURCE:
					Resource risorsa = (Resource) proprietario;
					protocolPropertyList = risorsa.getProtocolPropertyList();
					break;
				case SOGGETTO:
					Soggetto soggettoRegistro = (Soggetto) proprietario;
					protocolPropertyList = soggettoRegistro.getProtocolPropertyList();
					break;
				case SERVIZIO_APPLICATIVO:
					throw new Exception("Con il TipoProprietario indicato ("+tipoProprietario+") deve essere invocato il metodo 'getProtocolPropertiesConfig'");
				}

				return protocolPropertyList;
			}
		}  catch (Exception e) {
			throw e;
		} 

		return null;

	}
	
	public List<org.openspcoop2.core.config.ProtocolProperty> getProtocolPropertiesConfig(Object proprietario, String id, String nome, String idProprietario, String nomeProprietario,
			String nomeParentProprietario, ProprietariProtocolProperty tipoProprietario, String tipoAccordo) throws Exception {
		try{
			if(proprietario != null){
				List<org.openspcoop2.core.config.ProtocolProperty> protocolPropertyList  = null;
				switch (tipoProprietario) {
				case ACCORDO_COOPERAZIONE:
				case ACCORDO_SERVIZIO_PARTE_COMUNE:
				case ACCORDO_SERVIZIO_PARTE_SPECIFICA:
				case AZIONE_ACCORDO:
				case FRUITORE:
				case OPERATION:
				case PORT_TYPE:
				case RESOURCE:
				case SOGGETTO:
					throw new Exception("Con il TipoProprietario indicato ("+tipoProprietario+") deve essere invocato il metodo 'getProtocolPropertiesRegistry'");
				case SERVIZIO_APPLICATIVO:
					ServizioApplicativo servizioApplicativo = (ServizioApplicativo) proprietario;
					protocolPropertyList = servizioApplicativo.getProtocolPropertyList();
					break;
				}

				return protocolPropertyList;
			}
		}  catch (Exception e) {
			throw e;
		} 

		return null;

	}

	public void salvaPropertiesRegistry(String userLogin, boolean smista, Object proprietario, List<ProtocolProperty> protocolPropertiesAggiornate,
			String id, String nome, String idProprietario, String nomeProprietario, String nomeParentProprietario,
			ProprietariProtocolProperty tipoProprietario, String tipoAccordo) throws Exception {
		try{
			if(proprietario != null){
				int idProp = Integer.parseInt(idProprietario);
				boolean save = false;
				switch (tipoProprietario) {
				case ACCORDO_COOPERAZIONE:
					AccordoCooperazione ac = (AccordoCooperazione) proprietario;
					ac.setProtocolPropertyList(protocolPropertiesAggiornate);
					// salvataggio
					this.core.performUpdateOperation(userLogin, smista, proprietario);
					break;
				case ACCORDO_SERVIZIO_PARTE_COMUNE:
					AccordoServizioParteComune as = (AccordoServizioParteComune) proprietario;
					as.setProtocolPropertyList(protocolPropertiesAggiornate);
					// salvataggio
					this.core.performUpdateOperation(userLogin, smista, proprietario);
					break;
				case ACCORDO_SERVIZIO_PARTE_SPECIFICA:
					AccordoServizioParteSpecifica aps = (AccordoServizioParteSpecifica) proprietario;
					aps.setProtocolPropertyList(protocolPropertiesAggiornate);
					// salvataggio
					this.core.performUpdateOperation(userLogin, smista, proprietario);
					break;
				case AZIONE_ACCORDO:
					Azione newAzione = (Azione) proprietario;
					AccordoServizioParteComune apca = this.apcCore.getAccordoServizioFull(idProp);
					for(Azione azione: apca.getAzioneList()){
						if(azione.getNome().equals(newAzione.getNome())){
							azione.setProtocolPropertyList(protocolPropertiesAggiornate);
							save = true;
							break;
						}
					}

					// salvataggio
					if(save)
						this.core.performUpdateOperation(userLogin, smista, apca);
					break;
				case FRUITORE:
					Fruitore fruitore = (Fruitore) proprietario; 
					AccordoServizioParteSpecifica apsFrui = this.apsCore.getAccordoServizioParteSpecifica(fruitore.getIdServizio());

					for (Fruitore fr : apsFrui.getFruitoreList()) {
						if(fr.getTipo().equals(fruitore.getTipo()) && fr.getNome().equals(fruitore.getNome())){
							fr.setProtocolPropertyList(protocolPropertiesAggiornate);
							save = true;
							break;
						}
					}

					// salvataggio
					if(save)
						this.core.performUpdateOperation(userLogin, smista, apsFrui);
					break;
				case OPERATION:
					org.openspcoop2.core.registry.Operation newAzionePt = (Operation) proprietario;
					AccordoServizioParteComune apcop = this.apcCore.getAccordoServizioFull(idProp);
					for (PortType pt : apcop.getPortTypeList()) {
						if(pt.getNome().equals(nomeParentProprietario)){
							for (org.openspcoop2.core.registry.Operation azione : pt.getAzioneList()) {
								if(azione.getNome().equals(newAzionePt.getNome())){
									azione.setProtocolPropertyList(protocolPropertiesAggiornate);
									save = true;
									break;
								}
							}
						}
					}
					// salvataggio
					if(save)
						this.core.performUpdateOperation(userLogin, smista, apcop);
					break;
				case PORT_TYPE:
					AccordoServizioParteComune apc = this.apcCore.getAccordoServizioFull(idProp);
					PortType newPt = (PortType) proprietario;

					for (PortType pt : apc.getPortTypeList()) {
						if(pt.getNome().equals(newPt.getNome())){
							pt.setProtocolPropertyList(protocolPropertiesAggiornate);
							save = true;
							break;
						}

					}
					// salvataggio
					if(save)
						this.core.performUpdateOperation(userLogin, smista, apc);
					break;
				case RESOURCE:
					Resource newResource = (Resource) proprietario;
					AccordoServizioParteComune apcr = this.apcCore.getAccordoServizioFull(idProp);
					for(Resource resource: apcr.getResourceList()){
						if(resource.getNome().equals(newResource.getNome())){
							resource.setProtocolPropertyList(protocolPropertiesAggiornate);
							save = true;
							break;
						}
					}

					// salvataggio
					if(save)
						this.core.performUpdateOperation(userLogin, smista, apcr);
					break;
				case SOGGETTO:
					Soggetto soggettoRegistro = (Soggetto) proprietario;
					soggettoRegistro.setProtocolPropertyList(protocolPropertiesAggiornate);
					// salvataggio
					this.core.performUpdateOperation(userLogin, smista, proprietario);
					break;
					
				case SERVIZIO_APPLICATIVO:
					throw new Exception("Con il TipoProprietario indicato ("+tipoProprietario+") deve essere invocato il metodo 'salvaPropertiesRegistry'");
				}
			}
		}  catch (Exception e) {
			throw e;
		}

	}
	
	public void salvaPropertiesConfig(String userLogin, boolean smista, Object proprietario, List<org.openspcoop2.core.config.ProtocolProperty> protocolPropertiesAggiornate,
			String id, String nome, String idProprietario, String nomeProprietario, String nomeParentProprietario,
			ProprietariProtocolProperty tipoProprietario, String tipoAccordo) throws Exception {
		try{
			if(proprietario != null){
				@SuppressWarnings("unused")
				int idProp = Integer.parseInt(idProprietario);
				switch (tipoProprietario) {
				case ACCORDO_COOPERAZIONE:
				case ACCORDO_SERVIZIO_PARTE_COMUNE:
				case ACCORDO_SERVIZIO_PARTE_SPECIFICA:
				case AZIONE_ACCORDO:
				case FRUITORE:
				case OPERATION:
				case PORT_TYPE:
				case RESOURCE:
				case SOGGETTO:
					throw new Exception("Con il TipoProprietario indicato ("+tipoProprietario+") deve essere invocato il metodo 'salvaPropertiesRegistry'");
					
				case SERVIZIO_APPLICATIVO:
					ServizioApplicativo servizioApplicativo = (ServizioApplicativo) proprietario;
					servizioApplicativo.setProtocolPropertyList(protocolPropertiesAggiornate);
					// salvataggio
					this.core.performUpdateOperation(userLogin, smista, proprietario);
					break;
				}
			}
		}  catch (Exception e) {
			throw e;
		}

	}

	public void validateDynamicConfig(IConsoleDynamicConfiguration consoleDynamicConfiguration, Object idOggettoProprietario, String tipoAccordo, ProprietariProtocolProperty tipoProprietario,
			ConsoleConfiguration consoleConfiguration, ConsoleOperationType consoleOperationType,ProtocolProperties protocolProperties, 
			IRegistryReader registryReader, IConfigIntegrationReader configRegistryReader) throws ProtocolException {
		try{
			if(idOggettoProprietario != null){
				switch (tipoProprietario) {
				case ACCORDO_COOPERAZIONE:
					IDAccordo idAccordoCooperazione = (IDAccordo) idOggettoProprietario;
					consoleDynamicConfiguration.validateDynamicConfigCooperazione(consoleConfiguration, consoleOperationType, this, protocolProperties, 
							registryReader, configRegistryReader, idAccordoCooperazione);
					break;
				case ACCORDO_SERVIZIO_PARTE_COMUNE:
					IDAccordo idApc = (IDAccordo) idOggettoProprietario;
					
					if(tipoAccordo.equals(ProtocolPropertiesCostanti.PARAMETRO_VALORE_PP_TIPO_ACCORDO_PARTE_COMUNE))
						consoleDynamicConfiguration.validateDynamicConfigAccordoServizioParteComune(consoleConfiguration, consoleOperationType, this, protocolProperties, 
								registryReader, configRegistryReader, idApc);
					else 
						consoleDynamicConfiguration.validateDynamicConfigAccordoServizioComposto(consoleConfiguration, consoleOperationType, this, protocolProperties, 
								registryReader, configRegistryReader, idApc);
					break;
				case ACCORDO_SERVIZIO_PARTE_SPECIFICA:
					IDServizio idAps =(IDServizio) idOggettoProprietario;
					
					consoleDynamicConfiguration.validateDynamicConfigAccordoServizioParteSpecifica(consoleConfiguration, consoleOperationType, this, protocolProperties, 
							registryReader, configRegistryReader, idAps);
					break;
				case AZIONE_ACCORDO:
					IDAccordoAzione idAccordoAzione = (IDAccordoAzione) idOggettoProprietario;
					consoleDynamicConfiguration.validateDynamicConfigAzione(consoleConfiguration, consoleOperationType, this, protocolProperties, 
							registryReader, configRegistryReader, idAccordoAzione);
					break;
				case FRUITORE:
					IDFruizione idFruizione = (IDFruizione) idOggettoProprietario;
					consoleDynamicConfiguration.validateDynamicConfigFruizioneAccordoServizioParteSpecifica(consoleConfiguration, consoleOperationType, this,  protocolProperties, 
							registryReader, configRegistryReader, idFruizione);
					break;
				case OPERATION:
					IDPortTypeAzione idAzionePt = (IDPortTypeAzione) idOggettoProprietario;
					consoleDynamicConfiguration.validateDynamicConfigOperation(consoleConfiguration, consoleOperationType, this, protocolProperties, 
							registryReader, configRegistryReader, idAzionePt);
					break;
				case PORT_TYPE:
					IDPortType idPt = (IDPortType) idOggettoProprietario;
					consoleDynamicConfiguration.validateDynamicConfigPortType(consoleConfiguration, consoleOperationType, this, protocolProperties, 
							registryReader, configRegistryReader, idPt);
					break;
				case RESOURCE:
					IDResource idAccordoRisorsa = (IDResource) idOggettoProprietario;
					String httpMethod = null;
					String path = null;
					try {
						Resource r = registryReader.getResourceAccordo(idAccordoRisorsa);
						httpMethod = r.getMethod()!=null ? r.getMethod().getValue() : null;
						path = r.getPath();
					}catch(Exception e) {}
					consoleDynamicConfiguration.validateDynamicConfigResource(consoleConfiguration, consoleOperationType, this, protocolProperties, 
							registryReader, configRegistryReader, idAccordoRisorsa, httpMethod, path);
					break;
				case SOGGETTO:
					IDSoggetto idSoggetto = (IDSoggetto) idOggettoProprietario;
					consoleDynamicConfiguration.validateDynamicConfigSoggetto(consoleConfiguration, consoleOperationType, this, protocolProperties, 
							registryReader, configRegistryReader, idSoggetto);
					break;
				case SERVIZIO_APPLICATIVO:
					IDServizioApplicativo idServizioApplicativo = (IDServizioApplicativo) idOggettoProprietario;
					consoleDynamicConfiguration.validateDynamicConfigServizioApplicativo(consoleConfiguration, consoleOperationType, this, protocolProperties, 
							registryReader, configRegistryReader, idServizioApplicativo);
					break;
				}
			}
		}catch(ProtocolException e){
			throw e;
		}
	}
	
	public List<Parameter> getTitolo(Object proprietario,ProprietariProtocolProperty tipoProprietario, 
			String id, String nome, String idProprietario, String nomeProprietario, String nomeParentProprietario, String urlChange,String tipoAccordo) throws Exception{
		return getTitolo(proprietario, tipoProprietario, id, nome, idProprietario, nomeProprietario, nomeParentProprietario, urlChange, tipoAccordo,false);
	}
	
	public List<Parameter> getTitolo(Object proprietario,ProprietariProtocolProperty tipoProprietario, 
			String id, String nome, String idProprietario, String nomeProprietario, String nomeParentProprietario, String urlChange,String tipoAccordo, boolean updateUrlChange) throws Exception{
		AccordiServizioParteComuneHelper apcHelper = null;
		AccordiServizioParteSpecificaHelper apsHelper = null;
		Boolean isModalitaVistaApiCustom = ServletUtils.getBooleanAttributeFromSession(ApiCostanti.SESSION_ATTRIBUTE_VISTA_APC_API, this.session, false);
		List<Parameter> lstParam = new ArrayList<Parameter>();
		String labelProprietario = nomeProprietario;
		String tipoProtocollo = null;
		IDAccordo idAccordoParteComune = null;
		try{
			String urlDecode = URLDecoder.decode(urlChange,"UTF-8");
			String tipologia = ServletUtils.getObjectFromSession(this.session, String.class, AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_TIPO_EROGAZIONE);
			boolean gestioneFruitori = false;
			if(tipologia!=null) {
				if(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_TIPO_EROGAZIONE_VALUE_FRUIZIONE.equals(tipologia)) {
					gestioneFruitori = true;
				}
			}
			
			Parameter pTipoAccordo = new Parameter(AccordiServizioParteComuneUtilities.getParametroAccordoServizio(tipoAccordo).getName(), AccordiServizioParteComuneUtilities.getParametroAccordoServizio(tipoAccordo).getValue());
			Parameter pIdApc = null;
			Parameter pNomeApc = null;
			
			if(proprietario != null){
				int idProp = Integer.parseInt(idProprietario);
				switch (tipoProprietario) {
				case ACCORDO_COOPERAZIONE:
					AccordoCooperazione ac = (AccordoCooperazione) proprietario;
					lstParam.add(new Parameter(AccordiCooperazioneCostanti.LABEL_ACCORDI_COOPERAZIONE, AccordiCooperazioneCostanti.SERVLET_NAME_ACCORDI_COOPERAZIONE_LIST));
					labelProprietario = this.getLabelIdAccordoCooperazione(ac);
					// Escape della url del link, risolve il problema di autorizzazione
					lstParam.add(new Parameter(labelProprietario,urlDecode));
					break;
				case ACCORDO_SERVIZIO_PARTE_COMUNE:
					apcHelper = new AccordiServizioParteComuneHelper(this.request, this.pd, this.session); 
					AccordoServizioParteComune as = (AccordoServizioParteComune) proprietario;
					idAccordoParteComune = this.idAccordoFactory.getIDAccordoFromValues(as.getNome(),BeanUtilities.getSoggettoReferenteID(as.getSoggettoReferente()),as.getVersione());
					tipoProtocollo = this.soggettiCore.getProtocolloAssociatoTipoSoggetto(as.getSoggettoReferente().getTipo());
					labelProprietario = this.getLabelIdAccordo(tipoProtocollo , idAccordoParteComune); 
					
					List<Parameter> parametriDaUrlAs = Parameter.estraiParametriDaUrl(urlDecode);
					String parParz = null;
					for (Parameter parameter : parametriDaUrlAs) {
						if(parameter.getName().equals(ApiCostanti.PARAMETRO_APC_API_GESTIONE_PARZIALE)) {
							parParz = parameter.getValue();
							break;
						}
					}
					
					lstParam = apcHelper.getTitoloApc(TipoOperazione.OTHER, as, tipoAccordo, labelProprietario, AccordiServizioParteComuneCostanti.SERVLET_NAME_APC_CHANGE, parParz, true);
					lstParam.get(lstParam.size() -1).setValue(urlDecode); 
//					lstParam.add(new Parameter(labelProprietario,urlDecode));
					break;
				case ACCORDO_SERVIZIO_PARTE_SPECIFICA:
					apsHelper = new AccordiServizioParteSpecificaHelper(this.request, this.pd, this.session); 
					AccordoServizioParteSpecifica aps = (AccordoServizioParteSpecifica) proprietario;
					
					List<Parameter> parametriDaUrlAps = Parameter.estraiParametriDaUrl(urlDecode);
					String tipoSoggettoFruitore = null;
					String nomeSoggettoFruitore = null;
					for (Parameter parameter : parametriDaUrlAps) {
						if(parameter.getName().equals(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_TIPO_SOGGETTO_FRUITORE)) {
							tipoSoggettoFruitore = parameter.getValue();
							break;
						}
					}
					
					for (Parameter parameter : parametriDaUrlAps) {
						if(parameter.getName().equals(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_NOME_SOGGETTO_FRUITORE)) {
							nomeSoggettoFruitore = parameter.getValue();
							break;
						}
					}
					
					tipoProtocollo = this.soggettiCore.getProtocolloAssociatoTipoSoggetto(aps.getTipoSoggettoErogatore());
					if(gestioneFruitori) {
						IDSoggetto idSoggettoFruitore = null;
						if(tipoSoggettoFruitore!=null && !"".equals(tipoSoggettoFruitore) &&
								nomeSoggettoFruitore!=null && !"".equals(nomeSoggettoFruitore)) {
							idSoggettoFruitore = new IDSoggetto(tipoSoggettoFruitore, nomeSoggettoFruitore);
						}
						labelProprietario = apsHelper.getLabelServizioFruizione(tipoProtocollo, idSoggettoFruitore, aps);
					}
					else {
						labelProprietario = apsHelper.getLabelServizioErogazione(tipoProtocollo, aps);
					}
					
					lstParam = apsHelper.getTitoloAps(TipoOperazione.OTHER, aps, gestioneFruitori, labelProprietario, AccordiServizioParteSpecificaCostanti.SERVLET_NAME_APS_CHANGE, true, tipoSoggettoFruitore, nomeSoggettoFruitore);
					lstParam.get(lstParam.size() -1).setName(AccordiServizioParteComuneCostanti.LABEL_PARAMETRO_APC_PROTOCOLLO); 
					lstParam.get(lstParam.size() -1).setValue(urlDecode); 
					// Escape della url del link, risolve il problema di autorizzazione
//					lstParam.add(new Parameter(labelProprietario,urlDecode));
					break;
				case AZIONE_ACCORDO:
					Azione newAzione = (Azione) proprietario;
					AccordoServizioParteComune apca = this.apcCore.getAccordoServizioFull(idProp);
					for(Azione azione: apca.getAzioneList()){
						if(azione.getNome().equals(newAzione.getNome())){
							labelProprietario = azione.getNome();
							break;
						}
					}
					
					tipoProtocollo = this.soggettiCore.getProtocolloAssociatoTipoSoggetto(apca.getSoggettoReferente().getTipo());
					idAccordoParteComune = this.idAccordoFactory.getIDAccordoFromValues(apca.getNome(),BeanUtilities.getSoggettoReferenteID(apca.getSoggettoReferente()),apca.getVersione());
					pIdApc = new Parameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_ID, apca.getId()+"");
					pNomeApc = new Parameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_NOME, apca.getNome());
					
					lstParam.add(new Parameter(AccordiServizioParteComuneUtilities.getTerminologiaAccordoServizio(tipoAccordo), AccordiServizioParteComuneCostanti.SERVLET_NAME_APC_LIST, pTipoAccordo));
					lstParam.add(new Parameter(AccordiServizioParteComuneCostanti.LABEL_AZIONI + " di " + this.getLabelIdAccordo(tipoProtocollo , idAccordoParteComune), 
							AccordiServizioParteComuneCostanti.SERVLET_NAME_APC_AZIONI_LIST, pIdApc,pNomeApc, pTipoAccordo));
					// Escape della url del link, risolve il problema di autorizzazione
					lstParam.add(new Parameter(labelProprietario,urlDecode));
					break;
				case FRUITORE:
					Fruitore fruitore = (Fruitore) proprietario; 
					AccordoServizioParteSpecifica apsFrui = this.apsCore.getAccordoServizioParteSpecifica(fruitore.getIdServizio());
					
					for (Fruitore fr : apsFrui.getFruitoreList()) {
						if(fr.getTipo().equals(fruitore.getTipo()) && fr.getNome().equals(fruitore.getNome())){
							tipoProtocollo = this.soggettiCore.getProtocolloAssociatoTipoSoggetto(fr.getTipo());
							labelProprietario = this.getLabelNomeSoggetto(tipoProtocollo, fr.getTipo() , fr.getNome());
							break;
						}
					}
					
					// prelevo il flag che mi dice da quale pagina ho acceduto la sezione delle porte delegate
					Integer parentPD = ServletUtils.getIntegerAttributeFromSession(PorteDelegateCostanti.ATTRIBUTO_PORTE_DELEGATE_PARENT, this.session);
					if(parentPD == null) 
						parentPD = PorteDelegateCostanti.ATTRIBUTO_PORTE_DELEGATE_PARENT_NONE;
					
					PorteDelegateHelper porteDelegateHelper = new PorteDelegateHelper(this.request, this.pd, this.session);
					lstParam = porteDelegateHelper.getTitoloPD(PorteDelegateCostanti.ATTRIBUTO_PORTE_DELEGATE_PARENT_CONFIGURAZIONE, fruitore.getIdSoggetto() +"",apsFrui.getId() +"", fruitore.getId() +"");
					labelProprietario = AccordiServizioParteComuneCostanti.LABEL_PARAMETRO_APC_PROTOCOLLO; 
					lstParam.set(lstParam.size()-1, new Parameter(labelProprietario, urlDecode));
					
					break;
				case OPERATION:
					apcHelper = new AccordiServizioParteComuneHelper(this.request, this.pd, this.session); 
					org.openspcoop2.core.registry.Operation newAzionePt = (Operation) proprietario;
					AccordoServizioParteComune apcop = this.apcCore.getAccordoServizioFull(idProp);
					for (PortType pt : apcop.getPortTypeList()) {
						if(pt.getNome().equals(nomeParentProprietario)){
							for (org.openspcoop2.core.registry.Operation azione : pt.getAzioneList()) {
								if(azione.getNome().equals(newAzionePt.getNome())){
									labelProprietario = azione.getNome();
									break;
								}
							}
						}
					}
					
					tipoProtocollo = this.soggettiCore.getProtocolloAssociatoTipoSoggetto(apcop.getSoggettoReferente().getTipo());
					idAccordoParteComune = this.idAccordoFactory.getIDAccordoFromValues(apcop.getNome(),BeanUtilities.getSoggettoReferenteID(apcop.getSoggettoReferente()),apcop.getVersione());
					pIdApc = new Parameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_ID, apcop.getId()+"");
					pNomeApc = new Parameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_NOME, apcop.getNome());
					Parameter pNomePt = new Parameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_PORT_TYPES_NOME, nomeParentProprietario);
					lstParam = apcHelper.getTitoloApc(TipoOperazione.OTHER, apcop, tipoAccordo, this.getLabelIdAccordo(tipoProtocollo , idAccordoParteComune), null, false);
					String labelPortTypesOp = isModalitaVistaApiCustom ? AccordiServizioParteComuneCostanti.LABEL_PORT_TYPES : AccordiServizioParteComuneCostanti.LABEL_PORT_TYPES + " di " + this.getLabelIdAccordo(tipoProtocollo , idAccordoParteComune);
					lstParam.add(new Parameter(labelPortTypesOp, AccordiServizioParteComuneCostanti.SERVLET_NAME_APC_PORT_TYPES_LIST, pIdApc,pNomeApc, pTipoAccordo));
					lstParam.add(new Parameter(AccordiServizioParteComuneCostanti.LABEL_AZIONI + " di " + nomeParentProprietario,	AccordiServizioParteComuneCostanti.SERVLET_NAME_APC_PORT_TYPE_OPERATIONS_LIST,pIdApc,pNomePt,pTipoAccordo));
					// Escape della url del link, risolve il problema di autorizzazione
					lstParam.add(new Parameter(labelProprietario,urlDecode));
					break;
				case PORT_TYPE:
					apcHelper = new AccordiServizioParteComuneHelper(this.request, this.pd, this.session); 
					AccordoServizioParteComune apc = this.apcCore.getAccordoServizioFull(idProp);
					PortType newPt = (PortType) proprietario;

					for (PortType pt : apc.getPortTypeList()) {
						if(pt.getNome().equals(newPt.getNome())){
							labelProprietario = pt.getNome();
							break;
						}

					}
					pIdApc = new Parameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_ID, apc.getId()+"");
					pNomeApc = new Parameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_NOME, apc.getNome());
					idAccordoParteComune = this.idAccordoFactory.getIDAccordoFromValues(apc.getNome(),BeanUtilities.getSoggettoReferenteID(apc.getSoggettoReferente()),apc.getVersione());
					tipoProtocollo = this.soggettiCore.getProtocolloAssociatoTipoSoggetto(apc.getSoggettoReferente().getTipo());
					lstParam = apcHelper.getTitoloApc(TipoOperazione.OTHER, apc, tipoAccordo, this.getLabelIdAccordo(tipoProtocollo , idAccordoParteComune), null, false);
					String labelPortTypes = isModalitaVistaApiCustom ? AccordiServizioParteComuneCostanti.LABEL_PORT_TYPES : AccordiServizioParteComuneCostanti.LABEL_PORT_TYPES + " di " + this.getLabelIdAccordo(tipoProtocollo , idAccordoParteComune);
					lstParam.add(new Parameter(labelPortTypes, AccordiServizioParteComuneCostanti.SERVLET_NAME_APC_PORT_TYPES_LIST, pIdApc, pNomeApc, pNomeApc));

					// Escape della url del link, risolve il problema di autorizzazione
					lstParam.add(new Parameter(labelProprietario,urlDecode));
					break;
				case RESOURCE:
					apcHelper = new AccordiServizioParteComuneHelper(this.request, this.pd, this.session); 
					Resource newResource = (Resource) proprietario;
					AccordoServizioParteComune apcr = this.apcCore.getAccordoServizioFull(idProp);
					Long idRs = newResource.getId();
					for(Resource resource: apcr.getResourceList()){
						if(resource.getNome().equals(newResource.getNome())){
//							labelProprietario =  APIUtils.normalizeResourceName(HttpMethod.toEnumConstant(resource.get_value_method()), resource.getPath());
							labelProprietario = resource.getNome();
							idRs = resource.getId();
							break;
						}
					}
					
					Parameter pLabelProprietario = new Parameter(labelProprietario,urlDecode);
					if(updateUrlChange) {
						List<Parameter> parametriDaUrl = Parameter.estraiParametriDaUrl(urlDecode);
						String pathDaUrl = Parameter.estraiPathDaUrl(urlDecode);
						
						for (Parameter parameter : parametriDaUrl) {
							if(parameter.getName().equals(AccordiServizioParteComuneCostanti.PARAMETRO_APC_RESOURCES_ID)) {
								parameter.setValue(idRs+"");
								break;
							}
						}

						pLabelProprietario = new Parameter(labelProprietario,pathDaUrl,parametriDaUrl);
					}
					
					
					
					tipoProtocollo = this.soggettiCore.getProtocolloAssociatoTipoSoggetto(apcr.getSoggettoReferente().getTipo());
					idAccordoParteComune = this.idAccordoFactory.getIDAccordoFromValues(apcr.getNome(),BeanUtilities.getSoggettoReferenteID(apcr.getSoggettoReferente()),apcr.getVersione());
					pIdApc = new Parameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_ID, apcr.getId()+"");
					pNomeApc = new Parameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_NOME, apcr.getNome());
					lstParam = apcHelper.getTitoloApc(TipoOperazione.OTHER, apcr, tipoAccordo, this.getLabelIdAccordo(tipoProtocollo , idAccordoParteComune), null, false);
					String labelRisorse = isModalitaVistaApiCustom ? AccordiServizioParteComuneCostanti.LABEL_RISORSE : AccordiServizioParteComuneCostanti.LABEL_RISORSE + " di " + this.getLabelIdAccordo(tipoProtocollo , idAccordoParteComune);
					lstParam.add(new Parameter(labelRisorse, AccordiServizioParteComuneCostanti.SERVLET_NAME_APC_RESOURCES_LIST, pIdApc,pNomeApc, pTipoAccordo));
					// Escape della url del link, risolve il problema di autorizzazione
					lstParam.add(pLabelProprietario);
					break;
				case SOGGETTO:
					Soggetto soggettoRegistro = (Soggetto) proprietario;
					lstParam.add(new Parameter(SoggettiCostanti.LABEL_SOGGETTI, SoggettiCostanti.SERVLET_NAME_SOGGETTI_LIST));
					tipoProtocollo = this.soggettiCore.getProtocolloAssociatoTipoSoggetto(soggettoRegistro.getTipo());
					labelProprietario = this.getLabelNomeSoggetto(tipoProtocollo, soggettoRegistro.getTipo() , soggettoRegistro.getNome());
					// Escape della url del link, risolve il problema di autorizzazione
					lstParam.add(new Parameter(labelProprietario,urlDecode));
					break;
				case SERVIZIO_APPLICATIVO:
					ServizioApplicativo servizioApplicativo = (ServizioApplicativo) proprietario;
					
					Integer parentSA = ServletUtils.getIntegerAttributeFromSession(ServiziApplicativiCostanti.ATTRIBUTO_SERVIZI_APPLICATIVI_PARENT, this.session);
					if(parentSA == null) parentSA = ServiziApplicativiCostanti.ATTRIBUTO_SERVIZI_APPLICATIVI_PARENT_NONE;
					Boolean useIdSogg = parentSA == ServiziApplicativiCostanti.ATTRIBUTO_SERVIZI_APPLICATIVI_PARENT_SOGGETTO;
					
					String labelApplicativi = ServiziApplicativiCostanti.LABEL_SERVIZI_APPLICATIVI;
					String labelApplicativiDi = ServiziApplicativiCostanti.LABEL_PARAMETRO_SERVIZI_APPLICATIVI_DI;
					if(this.isModalitaCompleta()==false) {
						labelApplicativi = ServiziApplicativiCostanti.LABEL_APPLICATIVI;
						labelApplicativiDi = ServiziApplicativiCostanti.LABEL_PARAMETRO_APPLICATIVI_DI;
					}
					
					if(useIdSogg){
						
						tipoProtocollo = this.soggettiCore.getProtocolloAssociatoTipoSoggetto(servizioApplicativo.getTipoSoggettoProprietario());
						String tipoENomeSoggetto = this.getLabelNomeSoggetto(tipoProtocollo, servizioApplicativo.getTipoSoggettoProprietario(), servizioApplicativo.getNomeSoggettoProprietario());
						
						lstParam.add(new Parameter(ServiziApplicativiCostanti.LABEL_PARAMETRO_SERVIZI_APPLICATIVI_SOGGETTI, SoggettiCostanti.SERVLET_NAME_SOGGETTI_LIST));
						lstParam.add(new Parameter(labelApplicativiDi + tipoENomeSoggetto,
										ServiziApplicativiCostanti.SERVLET_NAME_SERVIZI_APPLICATIVI_LIST,
										new Parameter(ServiziApplicativiCostanti.PARAMETRO_SERVIZI_APPLICATIVI_PROVIDER,servizioApplicativo.getIdSoggetto()+"")));								
						// Escape della url del link, risolve il problema di autorizzazione
						lstParam.add(new Parameter(nome, urlDecode));
					}else {
						lstParam.add(new Parameter(labelApplicativi, ServiziApplicativiCostanti.SERVLET_NAME_SERVIZI_APPLICATIVI_LIST));
						// Escape della url del link, risolve il problema di autorizzazione
						lstParam.add(new Parameter(servizioApplicativo.getNome(), urlDecode));
					}
					break;
				}
			}
		}  catch (Exception e) {
			throw e;
		}
		return lstParam;
	}
}
