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

package org.openspcoop2.core.registry.ws.client.accordocooperazione.crud;

/**
 * Please modify this class to meet your needs
 * This class is not complete
 */

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import javax.xml.namespace.QName;

/**
 * This class was generated by Apache CXF 3.2.6
 * 2019-09-19T15:14:41.199+02:00
 * Generated source version: 3.2.6
 *
 */
public final class AccordoCooperazione_AccordoCooperazionePortSoap12_Client {

    private static final QName SERVICE_NAME = new QName("http://www.openspcoop2.org/core/registry/management", "AccordoCooperazioneSoap12Service");

    private AccordoCooperazione_AccordoCooperazionePortSoap12_Client() {
    }

    public static void main(String args[]) throws java.lang.Exception {
        URL wsdlURL = AccordoCooperazioneSoap12Service.WSDL_LOCATION;
        if (args.length > 0 && args[0] != null && !"".equals(args[0])) {
            File wsdlFile = new File(args[0]);
            try {
                if (wsdlFile.exists()) {
                    wsdlURL = wsdlFile.toURI().toURL();
                } else {
                    wsdlURL = new URL(args[0]);
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
        }

        AccordoCooperazioneSoap12Service ss = new AccordoCooperazioneSoap12Service(wsdlURL, AccordoCooperazione_AccordoCooperazionePortSoap12_Client.SERVICE_NAME);
        AccordoCooperazione port = ss.getAccordoCooperazionePortSoap12();
	
		new org.openspcoop2.core.registry.ws.client.utils.RequestContextUtils("accordoCooperazione.soap12").addRequestContextParameters((javax.xml.ws.BindingProvider)port);

        {
        System.out.println("Invoking deleteAllByFilter...");
        org.openspcoop2.core.registry.ws.client.accordocooperazione.crud.SearchFilterAccordoCooperazione _deleteAllByFilter_filter = new org.openspcoop2.core.registry.ws.client.accordocooperazione.crud.SearchFilterAccordoCooperazione();
        try {
            long _deleteAllByFilter__return = port.deleteAllByFilter(_deleteAllByFilter_filter);
            System.out.println("deleteAllByFilter.result=" + _deleteAllByFilter__return);

        } catch (RegistryServiceException_Exception e) {
            System.out.println("Expected exception: registry-service-exception has occurred.");
            System.out.println(e.toString());
        } catch (RegistryNotImplementedException_Exception e) {
            System.out.println("Expected exception: registry-not-implemented-exception has occurred.");
            System.out.println(e.toString());
        } catch (RegistryNotAuthorizedException_Exception e) {
            System.out.println("Expected exception: registry-not-authorized-exception has occurred.");
            System.out.println(e.toString());
        }
            }
        {
        System.out.println("Invoking deleteById...");
        org.openspcoop2.core.registry.IdAccordoCooperazione _deleteById_idAccordoCooperazione = new org.openspcoop2.core.registry.IdAccordoCooperazione();
        try {
            port.deleteById(_deleteById_idAccordoCooperazione);

        } catch (RegistryServiceException_Exception e) {
            System.out.println("Expected exception: registry-service-exception has occurred.");
            System.out.println(e.toString());
        } catch (RegistryNotImplementedException_Exception e) {
            System.out.println("Expected exception: registry-not-implemented-exception has occurred.");
            System.out.println(e.toString());
        } catch (RegistryNotAuthorizedException_Exception e) {
            System.out.println("Expected exception: registry-not-authorized-exception has occurred.");
            System.out.println(e.toString());
        }
            }
        {
        System.out.println("Invoking deleteAll...");
        try {
            long _deleteAll__return = port.deleteAll();
            System.out.println("deleteAll.result=" + _deleteAll__return);

        } catch (RegistryServiceException_Exception e) {
            System.out.println("Expected exception: registry-service-exception has occurred.");
            System.out.println(e.toString());
        } catch (RegistryNotImplementedException_Exception e) {
            System.out.println("Expected exception: registry-not-implemented-exception has occurred.");
            System.out.println(e.toString());
        } catch (RegistryNotAuthorizedException_Exception e) {
            System.out.println("Expected exception: registry-not-authorized-exception has occurred.");
            System.out.println(e.toString());
        }
            }
        {
        System.out.println("Invoking create...");
        org.openspcoop2.core.registry.AccordoCooperazione _create_accordoCooperazione = new org.openspcoop2.core.registry.AccordoCooperazione();
        try {
            port.create(_create_accordoCooperazione);

        } catch (RegistryServiceException_Exception e) {
            System.out.println("Expected exception: registry-service-exception has occurred.");
            System.out.println(e.toString());
        } catch (RegistryNotImplementedException_Exception e) {
            System.out.println("Expected exception: registry-not-implemented-exception has occurred.");
            System.out.println(e.toString());
        } catch (RegistryNotAuthorizedException_Exception e) {
            System.out.println("Expected exception: registry-not-authorized-exception has occurred.");
            System.out.println(e.toString());
        }
            }
        {
        System.out.println("Invoking update...");
        org.openspcoop2.core.registry.IdAccordoCooperazione _update_oldIdAccordoCooperazione = new org.openspcoop2.core.registry.IdAccordoCooperazione();
        org.openspcoop2.core.registry.AccordoCooperazione _update_accordoCooperazione = new org.openspcoop2.core.registry.AccordoCooperazione();
        try {
            port.update(_update_oldIdAccordoCooperazione, _update_accordoCooperazione);

        } catch (RegistryServiceException_Exception e) {
            System.out.println("Expected exception: registry-service-exception has occurred.");
            System.out.println(e.toString());
        } catch (RegistryNotFoundException_Exception e) {
            System.out.println("Expected exception: registry-not-found-exception has occurred.");
            System.out.println(e.toString());
        } catch (RegistryNotImplementedException_Exception e) {
            System.out.println("Expected exception: registry-not-implemented-exception has occurred.");
            System.out.println(e.toString());
        } catch (RegistryNotAuthorizedException_Exception e) {
            System.out.println("Expected exception: registry-not-authorized-exception has occurred.");
            System.out.println(e.toString());
        }
            }
        {
        System.out.println("Invoking updateOrCreate...");
        org.openspcoop2.core.registry.IdAccordoCooperazione _updateOrCreate_oldIdAccordoCooperazione = new org.openspcoop2.core.registry.IdAccordoCooperazione();
        org.openspcoop2.core.registry.AccordoCooperazione _updateOrCreate_accordoCooperazione = new org.openspcoop2.core.registry.AccordoCooperazione();
        try {
            port.updateOrCreate(_updateOrCreate_oldIdAccordoCooperazione, _updateOrCreate_accordoCooperazione);

        } catch (RegistryServiceException_Exception e) {
            System.out.println("Expected exception: registry-service-exception has occurred.");
            System.out.println(e.toString());
        } catch (RegistryNotImplementedException_Exception e) {
            System.out.println("Expected exception: registry-not-implemented-exception has occurred.");
            System.out.println(e.toString());
        } catch (RegistryNotAuthorizedException_Exception e) {
            System.out.println("Expected exception: registry-not-authorized-exception has occurred.");
            System.out.println(e.toString());
        }
            }
        {
        System.out.println("Invoking delete...");
        org.openspcoop2.core.registry.AccordoCooperazione _delete_accordoCooperazione = new org.openspcoop2.core.registry.AccordoCooperazione();
        try {
            port.delete(_delete_accordoCooperazione);

        } catch (RegistryServiceException_Exception e) {
            System.out.println("Expected exception: registry-service-exception has occurred.");
            System.out.println(e.toString());
        } catch (RegistryNotImplementedException_Exception e) {
            System.out.println("Expected exception: registry-not-implemented-exception has occurred.");
            System.out.println(e.toString());
        } catch (RegistryNotAuthorizedException_Exception e) {
            System.out.println("Expected exception: registry-not-authorized-exception has occurred.");
            System.out.println(e.toString());
        }
            }

        System.exit(0);
    }

}
