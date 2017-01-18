/*
 * OpenSPCoop - Customizable API Gateway 
 * http://www.openspcoop2.org
 * 
 * Copyright (c) 2005-2017 Link.it srl (http://link.it).
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

import java.util.List;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.openspcoop2.core.id.IDAccordo;
import org.openspcoop2.core.id.IDAccordoAzione;
import org.openspcoop2.core.id.IDFruizione;
import org.openspcoop2.core.id.IDPortType;
import org.openspcoop2.core.id.IDPortTypeAzione;
import org.openspcoop2.core.id.IDServizio;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.core.registry.AccordoCooperazione;
import org.openspcoop2.core.registry.AccordoServizioParteComune;
import org.openspcoop2.core.registry.AccordoServizioParteSpecifica;
import org.openspcoop2.core.registry.Azione;
import org.openspcoop2.core.registry.Fruitore;
import org.openspcoop2.core.registry.Operation;
import org.openspcoop2.core.registry.PortType;
import org.openspcoop2.core.registry.ProtocolProperty;
import org.openspcoop2.core.registry.Soggetto;
import org.openspcoop2.core.registry.constants.ProprietariProtocolProperty;
import org.openspcoop2.core.registry.driver.BeanUtilities;
import org.openspcoop2.core.registry.driver.DriverRegistroServiziException;
import org.openspcoop2.core.registry.driver.DriverRegistroServiziNotFound;
import org.openspcoop2.protocol.sdk.ProtocolException;
import org.openspcoop2.protocol.sdk.constants.ConsoleInterfaceType;
import org.openspcoop2.protocol.sdk.constants.ConsoleOperationType;
import org.openspcoop2.protocol.sdk.properties.AbstractConsoleItem;
import org.openspcoop2.protocol.sdk.properties.ConsoleConfiguration;
import org.openspcoop2.protocol.sdk.properties.IConsoleDynamicConfiguration;
import org.openspcoop2.protocol.sdk.registry.IRegistryReader;
import org.openspcoop2.web.ctrlstat.servlet.ConsoleHelper;
import org.openspcoop2.web.ctrlstat.servlet.archivi.ArchiviCostanti;
import org.openspcoop2.web.lib.mvc.BinaryParameter;
import org.openspcoop2.web.lib.mvc.Costanti;
import org.openspcoop2.web.lib.mvc.DataElement;
import org.openspcoop2.web.lib.mvc.DataElementType;
import org.openspcoop2.web.lib.mvc.PageData;
import org.openspcoop2.web.lib.mvc.Parameter;
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
					AccordoServizioParteComune apca = this.apcCore.getAccordoServizio(Integer.parseInt(idProprietario));
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
					AccordoServizioParteComune apc = this.apcCore.getAccordoServizio(idProp);
					for (PortType pt : apc.getPortTypeList()) {
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
					AccordoServizioParteComune apcPt = this.apcCore.getAccordoServizio(pt.getIdAccordo());
					IDAccordo idAccordoPt = this.idAccordoFactory.getIDAccordoFromValues(apcPt.getNome(),BeanUtilities.getSoggettoReferenteID(apcPt.getSoggettoReferente()),apcPt.getVersione());
					idPt.setIdAccordo(idAccordoPt);
					idPt.setNome(pt.getNome());

					return idPt;
				case SOGGETTO:
					Soggetto soggettoRegistro = (Soggetto) proprietario;
					IDSoggetto idSoggetto = new IDSoggetto(soggettoRegistro.getTipo(), soggettoRegistro.getNome()); 
					return idSoggetto;
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
				int idProp = Integer.parseInt(idProprietario);

				switch (tipoProprietario) {
				case ACCORDO_COOPERAZIONE:
					AccordoCooperazione ac = this.acCore.getAccordoCooperazione(idProp);
					return ac;
				case ACCORDO_SERVIZIO_PARTE_COMUNE:
					AccordoServizioParteComune as = this.apcCore.getAccordoServizio(idProp);
					return as;
				case ACCORDO_SERVIZIO_PARTE_SPECIFICA:
					AccordoServizioParteSpecifica aps = this.apsCore.getAccordoServizioParteSpecifica(idProp);
					return aps;
				case AZIONE_ACCORDO:
					AccordoServizioParteComune apca = this.apcCore.getAccordoServizio(idProp);
					for(Azione azione: apca.getAzioneList()){
						if(azione.getNome().equals(nomeProprieta))
							return azione;
					}
					return null;
				case FRUITORE:
					Fruitore servFru = this.apsCore.getServizioFruitore(idProp);
					return servFru;
				case OPERATION:
					AccordoServizioParteComune apcop = this.apcCore.getAccordoServizio(idProp);
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
					AccordoServizioParteComune apc = this.apcCore.getAccordoServizio(idProp);
					for (PortType pt : apc.getPortTypeList()) {
						if(pt.getNome().equals(nomeProprieta))
							return pt;
					}
					return null;
				case SOGGETTO:
					Soggetto soggettoRegistro = this.soggettiCore.getSoggettoRegistro(idProp);
					return soggettoRegistro;
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
			BinaryParameter contenutoDocumento, StringBuffer contenutoDocumentoStringBuffer, String errore, String tipologiaDocumentoScaricare, AbstractConsoleItem<?> binaryConsoleItem) { 

		/* ID */
		DataElement de = new DataElement();
		de.setLabel(ProtocolPropertiesCostanti.PARAMETRO_PP_ID);
		de.setValue(id);
		de.setType(DataElementType.HIDDEN);
		de.setName(ProtocolPropertiesCostanti.PARAMETRO_PP_ID);
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

		/* Contenuto Documento */
		if(contenutoDocumento != null && contenutoDocumento.getValue() != null && contenutoDocumento.getValue().length > 0){
			de = new DataElement();
			de.setLabel(ProtocolPropertiesCostanti.LABEL_NOME);
			de.setType(DataElementType.TEXT);
			de.setValue(contenutoDocumento.getFilename());
			dati.addElement(de);
			
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
				de.setLabel(ProtocolPropertiesCostanti.LABEL_DOCUMENTO_ATTUALE +":");
				de.setType(DataElementType.TEXT_AREA_NO_EDIT);
				de.setValue(contenutoDocumentoStringBuffer.toString());
				de.setRows(30);
				de.setCols(110);
				dati.addElement(de);
			}
			
			if(id != null){
				DataElement saveAs = new DataElement();
				saveAs.setValue(ProtocolPropertiesCostanti.LABEL_DOWNLOAD);
				saveAs.setType(DataElementType.LINK);
				saveAs.setUrl(ArchiviCostanti.SERVLET_NAME_DOCUMENTI_EXPORT, 
						new Parameter(ProtocolPropertiesCostanti.PARAMETRO_PP_ID_ALLEGATO, id),
						new Parameter(ArchiviCostanti.PARAMETRO_ARCHIVI_ALLEGATO_TIPO_ACCORDO_TIPO_DOCUMENTO, tipologiaDocumentoScaricare),
						new Parameter(ArchiviCostanti.PARAMETRO_ARCHIVI_ALLEGATO_TIPO_ACCORDO, ArchiviCostanti.PARAMETRO_VALORE_ARCHIVI_ALLEGATO_TIPO_PROTOCOL_PROPERTY));
				dati.add(saveAs);
			}

			de = new DataElement();
			de.setType(DataElementType.TITLE);
			de.setLabel(ProtocolPropertiesCostanti.LABEL_AGGIORNAMENTO);
			de.setValue("");
			de.setSize(this.getSize());
			dati.addElement(de);

		}else {
			de = new DataElement();
			de.setLabel(ProtocolPropertiesCostanti.LABEL_DOCUMENTO_ATTUALE +":");
			de.setType(DataElementType.TEXT);
			de.setValue(ProtocolPropertiesCostanti.LABEL_DOCUMENTO_NOT_FOUND);
			dati.addElement(de);
		}

		de = new DataElement();
		de.setLabel(ProtocolPropertiesCostanti.LABEL_CONTENUTO_NUOVO +":");
		de.setValue("");
		de.setType(DataElementType.FILE);
		de.setName(ProtocolPropertiesCostanti.PARAMETRO_PP_CONTENUTO_DOCUMENTO);
		de.setSize(this.getSize());
		de.setRequired(binaryConsoleItem.isRequired()); 
		dati.addElement(de);
		
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

		return dati;
	}

	public ConsoleConfiguration getConsoleDynamicConfiguration(Object idOggettoProprietario, String idProprietario,
			String nomeProprietario, String nomeParentProprietario, ProprietariProtocolProperty tipoProprietario,
			String tipoAccordo, ConsoleOperationType consoleOperationType, ConsoleInterfaceType consoleInterfaceType,
			IRegistryReader registryReader,IConsoleDynamicConfiguration consoleDynamicConfiguration) throws ProtocolException{
		try{
			if(tipoProprietario != null && idProprietario != null && idOggettoProprietario != null){
				switch (tipoProprietario) {
				case ACCORDO_COOPERAZIONE:
					IDAccordo idAccordoCooperazione = (IDAccordo) idOggettoProprietario;
					return consoleDynamicConfiguration.getDynamicConfigAccordoCooperazione(consoleOperationType, consoleInterfaceType, registryReader, idAccordoCooperazione);
				case ACCORDO_SERVIZIO_PARTE_COMUNE:
					IDAccordo idApc = (IDAccordo) idOggettoProprietario;
					if(tipoAccordo.equals(ProtocolPropertiesCostanti.PARAMETRO_VALORE_PP_TIPO_ACCORDO_PARTE_COMUNE)){
						return consoleDynamicConfiguration.getDynamicConfigAccordoServizioParteComune(consoleOperationType, consoleInterfaceType, registryReader, idApc);
					} else {
						return consoleDynamicConfiguration.getDynamicConfigAccordoServizioComposto(consoleOperationType, consoleInterfaceType, registryReader, idApc);
					}
				case ACCORDO_SERVIZIO_PARTE_SPECIFICA:
					IDServizio idAps =(IDServizio) idOggettoProprietario;			
					return consoleDynamicConfiguration.getDynamicConfigAccordoServizioParteSpecifica(consoleOperationType, consoleInterfaceType, registryReader, idAps);
				case AZIONE_ACCORDO:
					IDAccordoAzione idAccordoAzione = (IDAccordoAzione) idOggettoProprietario;
					return consoleDynamicConfiguration.getDynamicConfigAzione(consoleOperationType, consoleInterfaceType, registryReader, idAccordoAzione);
				case FRUITORE:
					IDFruizione idFruizione = (IDFruizione) idOggettoProprietario;
					return consoleDynamicConfiguration.getDynamicConfigFruizioneAccordoServizioParteSpecifica(consoleOperationType, consoleInterfaceType, registryReader, idFruizione);
				case OPERATION:
					IDPortTypeAzione idAzionePt = (IDPortTypeAzione) idOggettoProprietario;
					return consoleDynamicConfiguration.getDynamicConfigOperation(consoleOperationType, consoleInterfaceType, registryReader, idAzionePt);
				case PORT_TYPE:
					IDPortType idPt = (IDPortType) idOggettoProprietario;
					return consoleDynamicConfiguration.getDynamicConfigPortType(consoleOperationType, consoleInterfaceType, registryReader, idPt);
				case SOGGETTO:
					IDSoggetto idSoggetto = (IDSoggetto) idOggettoProprietario;
					return consoleDynamicConfiguration.getDynamicConfigSoggetto(consoleOperationType, consoleInterfaceType, registryReader, idSoggetto);
				}
			}

		}  catch (ProtocolException e) {
			throw e;
		} 
		return null;
	}

	public List<ProtocolProperty> getProtocolProperties(Object proprietario, String id, String nome, String idProprietario, String nomeProprietario,
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
				case SOGGETTO:
					Soggetto soggettoRegistro = (Soggetto) proprietario;
					protocolPropertyList = soggettoRegistro.getProtocolPropertyList();
					break;
				}

				return protocolPropertyList;
			}
		}  catch (Exception e) {
			throw e;
		} 

		return null;

	}

	public void salvaProperties(String userLogin, boolean smista, Object proprietario, List<ProtocolProperty> protocolPropertiesAggiornate,
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
					AccordoServizioParteComune apca = this.apcCore.getAccordoServizio(idProp);
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
					AccordoServizioParteComune apcop = this.apcCore.getAccordoServizio(idProp);
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
					AccordoServizioParteComune apc = this.apcCore.getAccordoServizio(idProp);
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
				case SOGGETTO:
					Soggetto soggettoRegistro = (Soggetto) proprietario;
					soggettoRegistro.setProtocolPropertyList(protocolPropertiesAggiornate);
					// salvataggio
					this.core.performUpdateOperation(userLogin, smista, proprietario);
					break;
				}
			}
		}  catch (Exception e) {
			throw e;
		}

	}

}
