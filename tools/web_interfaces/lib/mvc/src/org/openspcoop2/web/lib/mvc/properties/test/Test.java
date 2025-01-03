/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2025 Link.it srl (https://link.it).
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
package org.openspcoop2.web.lib.mvc.properties.test;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.openspcoop2.core.mvc.properties.Config;
import org.openspcoop2.core.mvc.properties.provider.ExternalResources;
import org.openspcoop2.core.mvc.properties.utils.DBPropertiesUtils;
import org.openspcoop2.core.mvc.properties.utils.XSDValidator;
import org.openspcoop2.core.mvc.properties.utils.serializer.JaxbDeserializer;
import org.openspcoop2.pdd.core.byok.DriverBYOKUtilities;
import org.openspcoop2.utils.LoggerWrapperFactory;
import org.openspcoop2.utils.xml.AbstractValidatoreXSD;
import org.openspcoop2.web.lib.mvc.Costanti;
import org.openspcoop2.web.lib.mvc.DataElement;
import org.openspcoop2.web.lib.mvc.DataElementType;
import org.openspcoop2.web.lib.mvc.byok.LockUtilities;
import org.openspcoop2.web.lib.mvc.properties.beans.BaseItemBean;
import org.openspcoop2.web.lib.mvc.properties.beans.ConfigBean;
import org.openspcoop2.web.lib.mvc.properties.utils.ReadPropertiesUtilities;

/**     
 * Test
 *
 * @author Pintori Giuliano (pintori@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class Test {
	public static void main(String[] args) {
		try {
			System.out.println("TEST CONFIG 1");
			
			DriverBYOKUtilities driverBYOKUtilities = new DriverBYOKUtilities(false, null, null);
			LockUtilities lockUtilities = new LockUtilities(driverBYOKUtilities, false, null, null, null, null, false);
			
			File f = new File(Test.class.getResource("Test.xml").toURI());
			
			System.out.println("Validazione XSD del file in corso...");
			
			AbstractValidatoreXSD validator = XSDValidator.getXSDValidator(LoggerWrapperFactory.getLogger(Test.class)); 
			
			validator.valida(f);
			System.out.println("Validazione XSD della configurazione completata con successo");
			
			
			JaxbDeserializer xmlReader = new JaxbDeserializer();
			
			Config configDaFile = xmlReader.readConfig(f);
			
			System.out.println("Check Config JAVA Exists ["+(configDaFile != null)+"]");
			
			Map<String, String> mapDB = null;
			
			Map<String, Properties> mappaDB = DBPropertiesUtils.toMultiMap(mapDB, ReadPropertiesUtilities.getListaNomiProperties(configDaFile));
			
			ExternalResources externalResources = null;
			
			ConfigBean configurazioneAdd = ReadPropertiesUtilities.leggiConfigurazione(configDaFile, mappaDB, externalResources);
			
			System.out.println("ConfigurazioneAdd Resolve Conditions");
			
			configurazioneAdd.updateConfigurazione(configDaFile);
			
			System.out.println("ConfigurazioneAdd IN Pagina: ");
			
			Map<String, String> mapNameValue = new HashMap<>();
			for (BaseItemBean<?> item : configurazioneAdd.getListaItem()) {
				DataElement de = item.toDataElement(configurazioneAdd, mapNameValue, externalResources, lockUtilities);
				System.out.println("Item ["+de.getName()+"] Type ["	+de.getType() +"] Label ["+de.getLabel()+"] Value ["+de.getValue()+"]");
			}
						
			System.out.println("----------------------");
			
			
			
			System.out.println("Simulazione POSTBACK");
			
			configurazioneAdd.setValueFromRequest("usernameAction", Costanti.CHECK_BOX_ENABLED_ABILITATO, externalResources, lockUtilities);
			configurazioneAdd.setValueFromRequest("keystoreType", "pkcs12", externalResources, lockUtilities);
			
			System.out.println("ConfigurazioneAdd Resolve Conditions POSTBACK");
			
			configurazioneAdd.updateConfigurazione(configDaFile);
			
			System.out.println("ConfigurazioneAdd IN Pagina: ");
			
			mapNameValue = new HashMap<>();
			for (BaseItemBean<?> item : configurazioneAdd.getListaItem()) {
				DataElement de = item.toDataElement(configurazioneAdd, mapNameValue, externalResources, lockUtilities);
				if(de.getType().equals(DataElementType.CHECKBOX.toString()) || de.getType().equals(DataElementType.SELECT.toString()) )
					System.out.println("Item ["+de.getName()+"] Type ["	+de.getType() +"] Label ["+de.getLabel()+"] SelectedValue ["+de.getSelected()+"]");
				else 
					System.out.println("Item ["+de.getName()+"] Type ["	+de.getType() +"] Label ["+de.getLabel()+"] Value ["+de.getValue()+"]");
			}
			
			System.out.println("----------------------");
			
			
			System.out.println("Simulazione EDIT FINALE ADD ---> Clicco SALVA....");
			
			configurazioneAdd.setValueFromRequest("usernameAction", Costanti.CHECK_BOX_ENABLED_ABILITATO, externalResources, lockUtilities);
			configurazioneAdd.setValueFromRequest("keystoreType", "pkcs12", externalResources, lockUtilities);
			configurazioneAdd.setValueFromRequest("keystore", "/tmp/keystoreAdd.jks", externalResources, lockUtilities);
			configurazioneAdd.setValueFromRequest("encryptAction", Costanti.CHECK_BOX_ENABLED_ABILITATO, externalResources, lockUtilities);
			configurazioneAdd.setValueFromRequest("encryptSignatureAction", Costanti.CHECK_BOX_ENABLED_ABILITATO, externalResources, lockUtilities);
			
			
			System.out.println("Simulazione EDIT FINALE ADD ---> Validazione Input utente");
			
			configurazioneAdd.validazioneInputUtente("nome", "descrizione", configDaFile, externalResources);
			
			System.out.println("----------------------");
			
			mappaDB = configurazioneAdd.getPropertiesMap();	
			
			System.out.println("Values --> MAP Parziale:");
			
			for (String mapKey : mappaDB.keySet()) {
				System.out.println("Contenuto Mappa ["+mapKey+"]:  ");
				Properties properties = mappaDB.get(mapKey);
				
				for (Object propKey: properties.keySet()) {
					System.out.println("Item ["+propKey+"] Value ["+properties.get(propKey)+"]"); 
				}
				
				System.out.println("----------------------");
			}
			
			System.out.println("MAP Parziale --> MAP DB:");
			
			mapDB = DBPropertiesUtils.toMap(mappaDB);
			
			for (String propKey: mapDB.keySet()) {
				System.out.println("Item ["+propKey+"] Value ["+mapDB.get(propKey)+"]"); 
			}
			
			System.out.println("----------------------\n\n\n\n\n\n\n\n\n\n\n");
			
			
			
			System.out.println("Ricarico configurazione da DB ");
			
			System.out.println("MAP DB --> MAP Parziale:");
			
			mappaDB = DBPropertiesUtils.toMultiMap(mapDB);
			
			for (String mapKey : mappaDB.keySet()) {
				System.out.println("Contenuto Mappa ["+mapKey+"]:  ");
				Properties properties = mappaDB.get(mapKey);
				
				for (Object propKey: properties.keySet()) {
					System.out.println("Item ["+propKey+"] Value ["+properties.get(propKey)+"]"); 
				}
				
				System.out.println("----------------------");
			}
			
			System.out.println("Ricarico configurazione da DB ");
			
			ConfigBean configurazioneChange = ReadPropertiesUtilities.leggiConfigurazione(configDaFile, mappaDB, externalResources);
			
			System.out.println("----------------------");
			
			System.out.println("configurazioneChange Resolve Conditions");
			
			configurazioneChange.setValueFromRequest("keystoreType", "jks", externalResources, lockUtilities);
			configurazioneChange.setValueFromRequest("keystore", "/tmp/keystore.jks", externalResources, lockUtilities);
			configurazioneChange.setValueFromRequest("timeToLive", "120", externalResources, lockUtilities);
			
			configurazioneChange.updateConfigurazione(configDaFile);
			
			System.out.println("configurazioneChange IN Pagina: ");
			
			mapNameValue = new HashMap<>();
			for (BaseItemBean<?> item : configurazioneChange.getListaItem()) {
				DataElement de = item.toDataElement(configurazioneAdd, mapNameValue, externalResources, lockUtilities);
				if(de.getType().equals(DataElementType.CHECKBOX.toString()) || de.getType().equals(DataElementType.SELECT.toString()) )
					System.out.println("Item ["+de.getName()+"] Type ["	+de.getType() +"] Label ["+de.getLabel()+"] SelectedValue ["+de.getSelected()+"]");
				else 
					System.out.println("Item ["+de.getName()+"] Type ["	+de.getType() +"] Label ["+de.getLabel()+"] Value ["+de.getValue()+"]");
			}
						
			System.out.println("Simulazione EDIT FINALE CHANGE ---> Validazione Input utente");
			
			configurazioneChange.validazioneInputUtente("nome", "descrizione", configDaFile, externalResources);
			
			System.out.println("----------------------");
			
			
			Map<String, Properties> mappaDestinazione = configurazioneChange.getPropertiesMap();	
			
			System.out.println("Values --> MAP Parziale:");
			
			for (String mapKey : mappaDestinazione.keySet()) {
				System.out.println("Contenuto Mappa ["+mapKey+"]:  ");
				Properties properties = mappaDestinazione.get(mapKey);
				
				for (Object propKey: properties.keySet()) {
					System.out.println("Item ["+propKey+"] Value ["+properties.get(propKey)+"]"); 
				}
				
				System.out.println("----------------------");
			}
			
			System.out.println("MAP Parziale --> MAP DB:");
			
			mapDB = DBPropertiesUtils.toMap(mappaDestinazione);
			
			for (String propKey: mapDB.keySet()) {
				System.out.println("Item ["+propKey+"] Value ["+mapDB.get(propKey)+"]"); 
			}
			
			System.out.println("----------------------");
			
			
			System.out.println("TEST CONFIG 1 FINE ");
		}catch(Exception e) {
			e.printStackTrace(System.err);
		}
	}
}
