/*
 * GovWay - A customizable API Gateway 
 * http://www.govway.org
 *
 * from the Link.it OpenSPCoop project codebase
 * 
 * Copyright (c) 2005-2019 Link.it srl (http://link.it). 
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

package org.openspcoop2.protocol.trasparente.properties;

import java.util.List;

import org.openspcoop2.core.registry.ProtocolProperty;
import org.openspcoop2.protocol.sdk.IProtocolFactory;
import org.openspcoop2.protocol.sdk.ProtocolException;
import org.openspcoop2.protocol.sdk.constants.ConsoleInterfaceType;
import org.openspcoop2.protocol.sdk.constants.ConsoleItemType;
import org.openspcoop2.protocol.sdk.constants.ConsoleItemValueType;
import org.openspcoop2.protocol.sdk.constants.ConsoleOperationType;
import org.openspcoop2.protocol.sdk.properties.AbstractConsoleItem;
import org.openspcoop2.protocol.sdk.properties.BaseConsoleItem;
import org.openspcoop2.protocol.sdk.properties.BinaryConsoleItem;
import org.openspcoop2.protocol.sdk.properties.BinaryProperty;
import org.openspcoop2.protocol.sdk.properties.BooleanProperty;
import org.openspcoop2.protocol.sdk.properties.ConsoleConfiguration;
import org.openspcoop2.protocol.sdk.properties.ProtocolProperties;
import org.openspcoop2.protocol.sdk.properties.ProtocolPropertiesFactory;
import org.openspcoop2.protocol.sdk.properties.ProtocolPropertiesUtils;
import org.openspcoop2.protocol.sdk.properties.StringConsoleItem;
import org.openspcoop2.protocol.sdk.properties.StringProperty;
import org.openspcoop2.protocol.sdk.registry.IRegistryReader;

/**
 * TrasparenteConfigurazioneTest
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class TrasparenteConfigurazioneTest {

	public static final String TITOLO_SEZIONE_STATICA_ID = "titolo_sezione_statica";
	public static final String TITOLO_SEZIONE_STATICA_LABEL = "Configurazione properties Protocollo Trasparente tipo Statico";
	public static final String TITOLO_SEZIONE_STATICA_LABEL_OBB = "Configurazione properties Protocollo Trasparente tipo Statico Obbligatorie";
	public static final String SUFFISSO_OBBLIGATORIO = "_obb";

	public static final String PROP_STRING_STATICA_ID = "prop_string_statica";
	public static final String PROP_STRING_STATICA_LABEL = "Text Field";

	public static final String PROP_NUMBER_STATICA_ID = "prop_number_statica";
	public static final String PROP_NUMBER_STATICA_LABEL = "Number Field";
	
	public static final String PROP_BOOLEAN_STATICA_ID = "prop_boolean_statica";
	public static final String PROP_BOOLEAN_STATICA_LABEL = "Boolean Field";
	
	public static final String PROP_BINARY_STATICA_ID = "prop_binary_statica";
	public static final String PROP_BINARY_STATICA_LABEL = "Binary Field";
	
	public static final String PROP_SELECT_STATICA_ID = "prop_select_statica";
	public static final String PROP_SELECT_STATICA_LABEL = "Select List";
	
	public static final String PROP_SELECT_OPZIONE0_VALORE = "--";
	public static final String PROP_SELECT_OPZIONE0_LABEL = "--";
	
	public static final String PROP_SELECT_OPZIONE1_VALORE = "valore1";
	public static final String PROP_SELECT_OPZIONE1_LABEL = "Valore 1";
	
	public static final String PROP_SELECT_OPZIONE2_VALORE = "valore2";
	public static final String PROP_SELECT_OPZIONE2_LABEL = "Valore 2";
	
	public static final String PROP_SELECT_OPZIONE3_VALORE = "valore3";
	public static final String PROP_SELECT_OPZIONE3_LABEL = "Valore 3";
	
	public static final String PROP_TEXTAREA_STATICA_ID = "prop_textarea_statica";
	public static final String PROP_TEXTAREA_STATICA_LABEL = "Text Area Field";
	
	public static final String TITOLO_SEZIONE_DINAMICA_ID = "titolo_sezione_dinamica";
	public static final String TITOLO_SEZIONE_DINAMICA_LABEL = "Configurazione properties Protocollo Trasparente tipo Dinamico";
	
	public static final String PROP_BOOLEAN_DINAMICA_ID = "prop_boolean_dinamica";
	public static final String PROP_BOOLEAN_DINAMICA_LABEL = "Visualizza Sezione Dinamica";
	
	public static final String PROP_BINARY_DINAMICA_ID = "prop_binary_dinamica";
	public static final String PROP_BINARY_DINAMICA_LABEL = "Binary Field Dinamico";
	
	public static final String PROP_SELECT_DINAMICA_ID = "prop_select_dinamica";
	public static final String PROP_SELECT_DINAMICA_LABEL = "Select List";
	
	
	
	public static  ConsoleConfiguration getDynamicConfigTest(ConsoleOperationType consoleOperationType,
			ConsoleInterfaceType consoleInterfaceType, IRegistryReader registryReader, IProtocolFactory<?> factory,List<ProtocolProperty> protocolPropertyList)
					throws ProtocolException {
		
		ConsoleConfiguration configuration = new ConsoleConfiguration();
		
		if(ConsoleOperationType.ADD.equals(consoleOperationType))
			configuration = getDynamicConfigTestAdd(consoleOperationType, consoleInterfaceType, registryReader, factory, protocolPropertyList);
		else 
			configuration = getDynamicConfigTestChange(consoleOperationType, consoleInterfaceType, registryReader, factory, protocolPropertyList);
		
		return configuration;
	}
	
	
	public static  ConsoleConfiguration getDynamicConfigTestAdd(ConsoleOperationType consoleOperationType,
			ConsoleInterfaceType consoleInterfaceType, IRegistryReader registryReader, IProtocolFactory<?> factory,List<ProtocolProperty> protocolPropertyList)
					throws ProtocolException {
		
		ConsoleConfiguration configuration = new ConsoleConfiguration();
		
		BaseConsoleItem titoloSezioneStatica = ProtocolPropertiesFactory.newTitleItem(TrasparenteConfigurazioneTest.TITOLO_SEZIONE_STATICA_ID, TrasparenteConfigurazioneTest.TITOLO_SEZIONE_STATICA_LABEL);
		configuration.addConsoleItem(titoloSezioneStatica );
		
		AbstractConsoleItem<?> stringConsoleItem = ProtocolPropertiesFactory.newConsoleItem(ConsoleItemType.TEXT_EDIT,TrasparenteConfigurazioneTest.PROP_STRING_STATICA_ID, TrasparenteConfigurazioneTest.PROP_STRING_STATICA_LABEL);
		configuration.addConsoleItem(stringConsoleItem);
		
		AbstractConsoleItem<?> numberConsoleItem = ProtocolPropertiesFactory.newConsoleItem(ConsoleItemValueType.NUMBER, ConsoleItemType.TEXT_EDIT,TrasparenteConfigurazioneTest.PROP_NUMBER_STATICA_ID, TrasparenteConfigurazioneTest.PROP_NUMBER_STATICA_LABEL);
		configuration.addConsoleItem(numberConsoleItem);
		
		StringConsoleItem selectConsoleItem = (StringConsoleItem) ProtocolPropertiesFactory.newConsoleItem(ConsoleItemValueType.STRING,
				ConsoleItemType.SELECT,TrasparenteConfigurazioneTest.PROP_SELECT_STATICA_ID, TrasparenteConfigurazioneTest.PROP_SELECT_STATICA_LABEL);
		selectConsoleItem.addLabelValue(PROP_SELECT_OPZIONE0_LABEL, PROP_SELECT_OPZIONE0_VALORE);
		selectConsoleItem.addLabelValue(PROP_SELECT_OPZIONE1_LABEL, PROP_SELECT_OPZIONE1_VALORE);
		selectConsoleItem.addLabelValue(PROP_SELECT_OPZIONE2_LABEL, PROP_SELECT_OPZIONE2_VALORE);
		configuration.addConsoleItem(selectConsoleItem);
		
		AbstractConsoleItem<?> binaryConsoleItem = ProtocolPropertiesFactory.newConsoleItem(ConsoleItemValueType.BINARY, ConsoleItemType.FILE,TrasparenteConfigurazioneTest.PROP_BINARY_STATICA_ID, TrasparenteConfigurazioneTest.PROP_BINARY_STATICA_LABEL);
		binaryConsoleItem.setReloadOnChange(true); 
		configuration.addConsoleItem(binaryConsoleItem);
		
		AbstractConsoleItem<?> booleanConsoleItem = ProtocolPropertiesFactory.newConsoleItem(ConsoleItemValueType.BOOLEAN, ConsoleItemType.CHECKBOX,TrasparenteConfigurazioneTest.PROP_BOOLEAN_STATICA_ID, TrasparenteConfigurazioneTest.PROP_BOOLEAN_STATICA_LABEL);
		configuration.addConsoleItem(booleanConsoleItem);
		
		AbstractConsoleItem<?> textareaConsoleItem = ProtocolPropertiesFactory.newConsoleItem(ConsoleItemValueType.STRING, ConsoleItemType.TEXT_AREA,
				TrasparenteConfigurazioneTest.PROP_TEXTAREA_STATICA_ID, TrasparenteConfigurazioneTest.PROP_TEXTAREA_STATICA_LABEL);
		configuration.addConsoleItem(textareaConsoleItem);
		
		/* SEZIONE OBBLIGATORIA */
		
		BaseConsoleItem titoloSezioneStaticaObb = ProtocolPropertiesFactory.newTitleItem(TrasparenteConfigurazioneTest.TITOLO_SEZIONE_STATICA_ID + TrasparenteConfigurazioneTest.SUFFISSO_OBBLIGATORIO,
				TrasparenteConfigurazioneTest.TITOLO_SEZIONE_STATICA_LABEL_OBB);
		configuration.addConsoleItem(titoloSezioneStaticaObb );
		
		AbstractConsoleItem<?> stringConsoleItemObb = ProtocolPropertiesFactory.newConsoleItem(ConsoleItemType.TEXT_EDIT,
				TrasparenteConfigurazioneTest.PROP_STRING_STATICA_ID + TrasparenteConfigurazioneTest.SUFFISSO_OBBLIGATORIO, TrasparenteConfigurazioneTest.PROP_STRING_STATICA_LABEL);
		stringConsoleItemObb.setRequired(true);
		configuration.addConsoleItem(stringConsoleItemObb);
		
		AbstractConsoleItem<?> numberConsoleItemObb = ProtocolPropertiesFactory.newConsoleItem(ConsoleItemValueType.NUMBER,
				ConsoleItemType.TEXT_EDIT,TrasparenteConfigurazioneTest.PROP_NUMBER_STATICA_ID + TrasparenteConfigurazioneTest.SUFFISSO_OBBLIGATORIO, TrasparenteConfigurazioneTest.PROP_NUMBER_STATICA_LABEL);
		numberConsoleItemObb.setRequired(true);
		configuration.addConsoleItem(numberConsoleItemObb);
		
		StringConsoleItem selectConsoleItemObb = (StringConsoleItem) ProtocolPropertiesFactory.newConsoleItem(ConsoleItemValueType.STRING,
				ConsoleItemType.SELECT,TrasparenteConfigurazioneTest.PROP_SELECT_STATICA_ID + TrasparenteConfigurazioneTest.SUFFISSO_OBBLIGATORIO, TrasparenteConfigurazioneTest.PROP_SELECT_STATICA_LABEL);
		selectConsoleItemObb.addLabelValue(PROP_SELECT_OPZIONE0_LABEL, PROP_SELECT_OPZIONE0_VALORE);
		selectConsoleItemObb.addLabelValue(PROP_SELECT_OPZIONE1_LABEL, PROP_SELECT_OPZIONE1_VALORE);
		selectConsoleItemObb.addLabelValue(PROP_SELECT_OPZIONE2_LABEL, PROP_SELECT_OPZIONE2_VALORE);
		selectConsoleItemObb.setRequired(true);
		configuration.addConsoleItem(selectConsoleItemObb);
		
		AbstractConsoleItem<?> binaryConsoleItemObb = ProtocolPropertiesFactory.newConsoleItem(ConsoleItemValueType.BINARY,
				ConsoleItemType.FILE,TrasparenteConfigurazioneTest.PROP_BINARY_STATICA_ID + TrasparenteConfigurazioneTest.SUFFISSO_OBBLIGATORIO, TrasparenteConfigurazioneTest.PROP_BINARY_STATICA_LABEL);
		binaryConsoleItemObb.setRequired(true);
		configuration.addConsoleItem(binaryConsoleItemObb);
		
		AbstractConsoleItem<?> textareaConsoleItemObb = ProtocolPropertiesFactory.newConsoleItem(ConsoleItemValueType.STRING, ConsoleItemType.TEXT_AREA,
				TrasparenteConfigurazioneTest.PROP_TEXTAREA_STATICA_ID + TrasparenteConfigurazioneTest.SUFFISSO_OBBLIGATORIO, TrasparenteConfigurazioneTest.PROP_TEXTAREA_STATICA_LABEL);
		textareaConsoleItemObb.setRequired(true);
		configuration.addConsoleItem(textareaConsoleItemObb);
		
		/* Sezione Dinamica */
		
		BaseConsoleItem titoloSezioneDinamica = ProtocolPropertiesFactory.newTitleItem(TrasparenteConfigurazioneTest.TITOLO_SEZIONE_DINAMICA_ID, TrasparenteConfigurazioneTest.TITOLO_SEZIONE_DINAMICA_LABEL);
		configuration.addConsoleItem(titoloSezioneDinamica );
		
		AbstractConsoleItem<?> booleanConsoleItemDinamica = ProtocolPropertiesFactory.newConsoleItem(ConsoleItemValueType.BOOLEAN, 
				ConsoleItemType.CHECKBOX,TrasparenteConfigurazioneTest.PROP_BOOLEAN_DINAMICA_ID, TrasparenteConfigurazioneTest.PROP_BOOLEAN_DINAMICA_LABEL);
		booleanConsoleItemDinamica.setReloadOnChange(true); 
		configuration.addConsoleItem(booleanConsoleItemDinamica);
		
		StringConsoleItem selectConsoleItemDinamica = (StringConsoleItem) ProtocolPropertiesFactory.newConsoleItem(ConsoleItemValueType.STRING,
				ConsoleItemType.SELECT,TrasparenteConfigurazioneTest.PROP_SELECT_DINAMICA_ID, TrasparenteConfigurazioneTest.PROP_SELECT_DINAMICA_LABEL);
		selectConsoleItemDinamica.addLabelValue(PROP_SELECT_OPZIONE0_LABEL, PROP_SELECT_OPZIONE0_VALORE);
		selectConsoleItemDinamica.addLabelValue(PROP_SELECT_OPZIONE1_LABEL, PROP_SELECT_OPZIONE1_VALORE);
		selectConsoleItemDinamica.addLabelValue(PROP_SELECT_OPZIONE2_LABEL, PROP_SELECT_OPZIONE2_VALORE);
		configuration.addConsoleItem(selectConsoleItemDinamica);
		
		AbstractConsoleItem<?> binaryConsoleItemDinamica = ProtocolPropertiesFactory.newConsoleItem(ConsoleItemValueType.BINARY,
				ConsoleItemType.FILE,TrasparenteConfigurazioneTest.PROP_BINARY_DINAMICA_ID, TrasparenteConfigurazioneTest.PROP_BINARY_DINAMICA_LABEL);
		configuration.addConsoleItem(binaryConsoleItemDinamica);
		
		
		return configuration;
	}
	
	public static  ConsoleConfiguration getDynamicConfigTestChange(ConsoleOperationType consoleOperationType,
			ConsoleInterfaceType consoleInterfaceType, IRegistryReader registryReader, IProtocolFactory<?> factory,List<ProtocolProperty> protocolPropertyList)
					throws ProtocolException {
		
		ConsoleConfiguration configuration = new ConsoleConfiguration();
		
		BaseConsoleItem titoloSezioneStatica = ProtocolPropertiesFactory.newTitleItem(TrasparenteConfigurazioneTest.TITOLO_SEZIONE_STATICA_ID, TrasparenteConfigurazioneTest.TITOLO_SEZIONE_STATICA_LABEL);
		configuration.addConsoleItem(titoloSezioneStatica );
		
		AbstractConsoleItem<?> stringConsoleItem = ProtocolPropertiesFactory.newConsoleItem(ConsoleItemType.TEXT_EDIT,TrasparenteConfigurazioneTest.PROP_STRING_STATICA_ID, TrasparenteConfigurazioneTest.PROP_STRING_STATICA_LABEL);
		configuration.addConsoleItem(stringConsoleItem);
		
		AbstractConsoleItem<?> numberConsoleItem = ProtocolPropertiesFactory.newConsoleItem(ConsoleItemValueType.NUMBER, ConsoleItemType.TEXT_EDIT,TrasparenteConfigurazioneTest.PROP_NUMBER_STATICA_ID, TrasparenteConfigurazioneTest.PROP_NUMBER_STATICA_LABEL);
		configuration.addConsoleItem(numberConsoleItem);
		
		StringConsoleItem selectConsoleItem = (StringConsoleItem) ProtocolPropertiesFactory.newConsoleItem(ConsoleItemValueType.STRING,
				ConsoleItemType.SELECT,TrasparenteConfigurazioneTest.PROP_SELECT_STATICA_ID, TrasparenteConfigurazioneTest.PROP_SELECT_STATICA_LABEL);
		selectConsoleItem.addLabelValue(PROP_SELECT_OPZIONE0_LABEL, PROP_SELECT_OPZIONE0_VALORE);
		selectConsoleItem.addLabelValue(PROP_SELECT_OPZIONE1_LABEL, PROP_SELECT_OPZIONE1_VALORE);
		selectConsoleItem.addLabelValue(PROP_SELECT_OPZIONE2_LABEL, PROP_SELECT_OPZIONE2_VALORE);
		configuration.addConsoleItem(selectConsoleItem);
		
		AbstractConsoleItem<?> binaryConsoleItem = ProtocolPropertiesFactory.newConsoleItem(ConsoleItemValueType.BINARY, ConsoleItemType.FILE,TrasparenteConfigurazioneTest.PROP_BINARY_STATICA_ID, TrasparenteConfigurazioneTest.PROP_BINARY_STATICA_LABEL);
		configuration.addConsoleItem(binaryConsoleItem);
		
		AbstractConsoleItem<?> booleanConsoleItem = ProtocolPropertiesFactory.newConsoleItem(ConsoleItemValueType.BOOLEAN, ConsoleItemType.CHECKBOX,TrasparenteConfigurazioneTest.PROP_BOOLEAN_STATICA_ID, TrasparenteConfigurazioneTest.PROP_BOOLEAN_STATICA_LABEL);
		configuration.addConsoleItem(booleanConsoleItem);
		
		AbstractConsoleItem<?> textareaConsoleItem = ProtocolPropertiesFactory.newConsoleItem(ConsoleItemValueType.STRING, ConsoleItemType.TEXT_AREA,
				TrasparenteConfigurazioneTest.PROP_TEXTAREA_STATICA_ID, TrasparenteConfigurazioneTest.PROP_TEXTAREA_STATICA_LABEL);
		configuration.addConsoleItem(textareaConsoleItem);
		
	/* SEZIONE OBBLIGATORIA */
		
		BaseConsoleItem titoloSezioneStaticaObb = ProtocolPropertiesFactory.newTitleItem(TrasparenteConfigurazioneTest.TITOLO_SEZIONE_STATICA_ID + TrasparenteConfigurazioneTest.SUFFISSO_OBBLIGATORIO,
				TrasparenteConfigurazioneTest.TITOLO_SEZIONE_STATICA_LABEL_OBB);
		configuration.addConsoleItem(titoloSezioneStaticaObb );
		
		AbstractConsoleItem<?> stringConsoleItemObb = ProtocolPropertiesFactory.newConsoleItem(ConsoleItemType.TEXT_EDIT,
				TrasparenteConfigurazioneTest.PROP_STRING_STATICA_ID + TrasparenteConfigurazioneTest.SUFFISSO_OBBLIGATORIO, TrasparenteConfigurazioneTest.PROP_STRING_STATICA_LABEL);
		stringConsoleItemObb.setRequired(true);
		configuration.addConsoleItem(stringConsoleItemObb);
		
		AbstractConsoleItem<?> numberConsoleItemObb = ProtocolPropertiesFactory.newConsoleItem(ConsoleItemValueType.NUMBER,
				ConsoleItemType.TEXT_EDIT,TrasparenteConfigurazioneTest.PROP_NUMBER_STATICA_ID + TrasparenteConfigurazioneTest.SUFFISSO_OBBLIGATORIO, TrasparenteConfigurazioneTest.PROP_NUMBER_STATICA_LABEL);
		numberConsoleItemObb.setRequired(true);
		configuration.addConsoleItem(numberConsoleItemObb);
		
		StringConsoleItem selectConsoleItemObb = (StringConsoleItem) ProtocolPropertiesFactory.newConsoleItem(ConsoleItemValueType.STRING,
				ConsoleItemType.SELECT,TrasparenteConfigurazioneTest.PROP_SELECT_STATICA_ID + TrasparenteConfigurazioneTest.SUFFISSO_OBBLIGATORIO, TrasparenteConfigurazioneTest.PROP_SELECT_STATICA_LABEL);
		selectConsoleItemObb.addLabelValue(PROP_SELECT_OPZIONE0_LABEL, PROP_SELECT_OPZIONE0_VALORE);
		selectConsoleItemObb.addLabelValue(PROP_SELECT_OPZIONE1_LABEL, PROP_SELECT_OPZIONE1_VALORE);
		selectConsoleItemObb.addLabelValue(PROP_SELECT_OPZIONE2_LABEL, PROP_SELECT_OPZIONE2_VALORE);
		selectConsoleItemObb.setRequired(true);
		configuration.addConsoleItem(selectConsoleItemObb);
		
		AbstractConsoleItem<?> binaryConsoleItemObb = ProtocolPropertiesFactory.newConsoleItem(ConsoleItemValueType.BINARY,
				ConsoleItemType.FILE,TrasparenteConfigurazioneTest.PROP_BINARY_STATICA_ID + TrasparenteConfigurazioneTest.SUFFISSO_OBBLIGATORIO, TrasparenteConfigurazioneTest.PROP_BINARY_STATICA_LABEL);
		binaryConsoleItemObb.setRequired(true);
		configuration.addConsoleItem(binaryConsoleItemObb);
		
		AbstractConsoleItem<?> textareaConsoleItemObb = ProtocolPropertiesFactory.newConsoleItem(ConsoleItemValueType.STRING, ConsoleItemType.TEXT_AREA,
				TrasparenteConfigurazioneTest.PROP_TEXTAREA_STATICA_ID + TrasparenteConfigurazioneTest.SUFFISSO_OBBLIGATORIO, TrasparenteConfigurazioneTest.PROP_TEXTAREA_STATICA_LABEL);
		textareaConsoleItemObb.setRequired(true);
		configuration.addConsoleItem(textareaConsoleItemObb);
		
		/* Sezione Dinamica */
		
		BaseConsoleItem titoloSezioneDinamica = ProtocolPropertiesFactory.newTitleItem(TrasparenteConfigurazioneTest.TITOLO_SEZIONE_DINAMICA_ID, TrasparenteConfigurazioneTest.TITOLO_SEZIONE_DINAMICA_LABEL);
		configuration.addConsoleItem(titoloSezioneDinamica );
		
		AbstractConsoleItem<?> booleanConsoleItemDinamica = ProtocolPropertiesFactory.newConsoleItem(ConsoleItemValueType.BOOLEAN, 
				ConsoleItemType.CHECKBOX,TrasparenteConfigurazioneTest.PROP_BOOLEAN_DINAMICA_ID, TrasparenteConfigurazioneTest.PROP_BOOLEAN_DINAMICA_LABEL);
		booleanConsoleItemDinamica.setReloadOnChange(true); 
		configuration.addConsoleItem(booleanConsoleItemDinamica);
		
		StringConsoleItem selectConsoleItemDinamica = (StringConsoleItem) ProtocolPropertiesFactory.newConsoleItem(ConsoleItemValueType.STRING,
				ConsoleItemType.HIDDEN,TrasparenteConfigurazioneTest.PROP_SELECT_DINAMICA_ID, TrasparenteConfigurazioneTest.PROP_SELECT_DINAMICA_LABEL);
		selectConsoleItemDinamica.addLabelValue(PROP_SELECT_OPZIONE0_LABEL, PROP_SELECT_OPZIONE0_VALORE);
		selectConsoleItemDinamica.addLabelValue(PROP_SELECT_OPZIONE1_LABEL, PROP_SELECT_OPZIONE1_VALORE);
		selectConsoleItemDinamica.addLabelValue(PROP_SELECT_OPZIONE2_LABEL, PROP_SELECT_OPZIONE2_VALORE);
		
		configuration.addConsoleItem(selectConsoleItemDinamica);
		
		AbstractConsoleItem<?> binaryConsoleItemDinamica = ProtocolPropertiesFactory.newConsoleItem(ConsoleItemValueType.BINARY,
				ConsoleItemType.HIDDEN,TrasparenteConfigurazioneTest.PROP_BINARY_DINAMICA_ID, TrasparenteConfigurazioneTest.PROP_BINARY_DINAMICA_LABEL);
		configuration.addConsoleItem(binaryConsoleItemDinamica);
		
		
		return configuration;
	}


	public static void updateDynamicConfig(ConsoleConfiguration consoleConfiguration,
			ConsoleOperationType consoleOperationType, ConsoleInterfaceType consoleInterfaceType,
			ProtocolProperties properties, IRegistryReader registryReader) throws ProtocolException {
		
//		BooleanConsoleItem booleanConsoleItemDinamica  = (BooleanConsoleItem) ProtocolPropertiesUtils.getAbstractConsoleItem(consoleConfiguration.getConsoleItem(), TrasparenteConfigurazioneTest.PROP_BOOLEAN_DINAMICA_ID);
		BooleanProperty booleanPropertyDinamica = (BooleanProperty) ProtocolPropertiesUtils.getAbstractPropertyById(properties, TrasparenteConfigurazioneTest.PROP_BOOLEAN_DINAMICA_ID); 
		StringConsoleItem selectConsoleItemDinamica  = (StringConsoleItem) ProtocolPropertiesUtils.getAbstractConsoleItem(consoleConfiguration.getConsoleItem(), TrasparenteConfigurazioneTest.PROP_SELECT_DINAMICA_ID);
		StringProperty selectPropertyDinamica = (StringProperty) ProtocolPropertiesUtils.getAbstractPropertyById(properties, TrasparenteConfigurazioneTest.PROP_SELECT_DINAMICA_ID);
		BinaryConsoleItem binaryConsoleItemDinamica  = (BinaryConsoleItem) ProtocolPropertiesUtils.getAbstractConsoleItem(consoleConfiguration.getConsoleItem(), TrasparenteConfigurazioneTest.PROP_BINARY_DINAMICA_ID);
		BinaryProperty binaryPropertyDinamica = (BinaryProperty) ProtocolPropertiesUtils.getAbstractPropertyById(properties, TrasparenteConfigurazioneTest.PROP_BINARY_DINAMICA_ID);
		
		// check box settata
		if(booleanPropertyDinamica != null && booleanPropertyDinamica.getValue() != null && booleanPropertyDinamica.getValue()){
			selectConsoleItemDinamica.setType(ConsoleItemType.SELECT);
			binaryConsoleItemDinamica.setType(ConsoleItemType.FILE);
		} else {
			selectConsoleItemDinamica.setType(ConsoleItemType.HIDDEN);
			selectPropertyDinamica.setValue(PROP_SELECT_OPZIONE0_VALORE);
			binaryConsoleItemDinamica.setType(ConsoleItemType.HIDDEN);
			binaryPropertyDinamica.setValue(null);
			binaryPropertyDinamica.setFileName(null); 
		}
	}


	public static void validateDynamicConfig(ConsoleConfiguration consoleConfiguration,
			ConsoleOperationType consoleOperationType, ProtocolProperties properties, IRegistryReader registryReader) throws ProtocolException {

		StringConsoleItem selectConsoleItemObb  = (StringConsoleItem) ProtocolPropertiesUtils.getAbstractConsoleItem(consoleConfiguration.getConsoleItem(), TrasparenteConfigurazioneTest.PROP_SELECT_STATICA_ID + TrasparenteConfigurazioneTest.SUFFISSO_OBBLIGATORIO);
		StringProperty selectPropertyObb = (StringProperty) ProtocolPropertiesUtils.getAbstractPropertyById(properties, TrasparenteConfigurazioneTest.PROP_SELECT_STATICA_ID + TrasparenteConfigurazioneTest.SUFFISSO_OBBLIGATORIO);
		
		if(selectConsoleItemObb.isRequired()){
			if(selectPropertyObb != null && selectPropertyObb.getValue().equals(PROP_SELECT_OPZIONE0_VALORE))
				throw new ProtocolException("La property: " +  TrasparenteConfigurazioneTest.PROP_SELECT_STATICA_LABEL + " non puo' assumere valore: " + PROP_SELECT_OPZIONE0_LABEL); 
		}
	}
}
