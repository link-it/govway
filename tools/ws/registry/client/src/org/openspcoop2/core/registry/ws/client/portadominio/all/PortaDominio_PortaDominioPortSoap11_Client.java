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

package org.openspcoop2.core.registry.ws.client.portadominio.all;

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
 * 2019-09-19T15:16:25.230+02:00
 * Generated source version: 3.2.6
 *
 */
public final class PortaDominio_PortaDominioPortSoap11_Client {

    private static final QName SERVICE_NAME = new QName("http://www.openspcoop2.org/core/registry/management", "PortaDominioSoap11Service");

    private PortaDominio_PortaDominioPortSoap11_Client() {
    }

    public static void main(String args[]) throws java.lang.Exception {
        URL wsdlURL = PortaDominioSoap11Service.WSDL_LOCATION;
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

        PortaDominioSoap11Service ss = new PortaDominioSoap11Service(wsdlURL, PortaDominio_PortaDominioPortSoap11_Client.SERVICE_NAME);
        PortaDominio port = ss.getPortaDominioPortSoap11();
	
		new org.openspcoop2.core.registry.ws.client.utils.RequestContextUtils("portaDominio.soap11").addRequestContextParameters((javax.xml.ws.BindingProvider)port);

        {
        System.out.println("Invoking find...");
        org.openspcoop2.core.registry.ws.client.portadominio.all.SearchFilterPortaDominio _find_filter = new org.openspcoop2.core.registry.ws.client.portadominio.all.SearchFilterPortaDominio();
        try {
            org.openspcoop2.core.registry.PortaDominio _find__return = port.find(_find_filter);
            System.out.println("find.result=" + _find__return);

        } catch (RegistryServiceException_Exception e) {
            System.out.println("Expected exception: registry-service-exception has occurred.");
            System.out.println(e.toString());
        } catch (RegistryNotFoundException_Exception e) {
            System.out.println("Expected exception: registry-not-found-exception has occurred.");
            System.out.println(e.toString());
        } catch (RegistryNotImplementedException_Exception e) {
            System.out.println("Expected exception: registry-not-implemented-exception has occurred.");
            System.out.println(e.toString());
        } catch (RegistryMultipleResultException_Exception e) {
            System.out.println("Expected exception: registry-multiple-result-exception has occurred.");
            System.out.println(e.toString());
        } catch (RegistryNotAuthorizedException_Exception e) {
            System.out.println("Expected exception: registry-not-authorized-exception has occurred.");
            System.out.println(e.toString());
        }
            }
        {
        System.out.println("Invoking get...");
        org.openspcoop2.core.registry.IdPortaDominio _get_idPortaDominio = new org.openspcoop2.core.registry.IdPortaDominio();
        try {
            org.openspcoop2.core.registry.PortaDominio _get__return = port.get(_get_idPortaDominio);
            System.out.println("get.result=" + _get__return);

        } catch (RegistryServiceException_Exception e) {
            System.out.println("Expected exception: registry-service-exception has occurred.");
            System.out.println(e.toString());
        } catch (RegistryNotFoundException_Exception e) {
            System.out.println("Expected exception: registry-not-found-exception has occurred.");
            System.out.println(e.toString());
        } catch (RegistryNotImplementedException_Exception e) {
            System.out.println("Expected exception: registry-not-implemented-exception has occurred.");
            System.out.println(e.toString());
        } catch (RegistryMultipleResultException_Exception e) {
            System.out.println("Expected exception: registry-multiple-result-exception has occurred.");
            System.out.println(e.toString());
        } catch (RegistryNotAuthorizedException_Exception e) {
            System.out.println("Expected exception: registry-not-authorized-exception has occurred.");
            System.out.println(e.toString());
        }
            }
        {
        System.out.println("Invoking deleteById...");
        org.openspcoop2.core.registry.IdPortaDominio _deleteById_idPortaDominio = new org.openspcoop2.core.registry.IdPortaDominio();
        try {
            port.deleteById(_deleteById_idPortaDominio);

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
        System.out.println("Invoking inUse...");
        org.openspcoop2.core.registry.IdPortaDominio _inUse_idPortaDominio = new org.openspcoop2.core.registry.IdPortaDominio();
        try {
            org.openspcoop2.core.registry.ws.client.portadominio.all.UseInfo _inUse__return = port.inUse(_inUse_idPortaDominio);
            System.out.println("inUse.result=" + _inUse__return);

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
        System.out.println("Invoking create...");
        org.openspcoop2.core.registry.PortaDominio _create_portaDominio = new org.openspcoop2.core.registry.PortaDominio();
        try {
            port.create(_create_portaDominio);

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
        System.out.println("Invoking exists...");
        org.openspcoop2.core.registry.IdPortaDominio _exists_idPortaDominio = new org.openspcoop2.core.registry.IdPortaDominio();
        try {
            boolean _exists__return = port.exists(_exists_idPortaDominio);
            System.out.println("exists.result=" + _exists__return);

        } catch (RegistryServiceException_Exception e) {
            System.out.println("Expected exception: registry-service-exception has occurred.");
            System.out.println(e.toString());
        } catch (RegistryNotImplementedException_Exception e) {
            System.out.println("Expected exception: registry-not-implemented-exception has occurred.");
            System.out.println(e.toString());
        } catch (RegistryMultipleResultException_Exception e) {
            System.out.println("Expected exception: registry-multiple-result-exception has occurred.");
            System.out.println(e.toString());
        } catch (RegistryNotAuthorizedException_Exception e) {
            System.out.println("Expected exception: registry-not-authorized-exception has occurred.");
            System.out.println(e.toString());
        }
            }
        {
        System.out.println("Invoking updateOrCreate...");
        org.openspcoop2.core.registry.IdPortaDominio _updateOrCreate_oldIdPortaDominio = new org.openspcoop2.core.registry.IdPortaDominio();
        org.openspcoop2.core.registry.PortaDominio _updateOrCreate_portaDominio = new org.openspcoop2.core.registry.PortaDominio();
        try {
            port.updateOrCreate(_updateOrCreate_oldIdPortaDominio, _updateOrCreate_portaDominio);

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
        System.out.println("Invoking deleteAllByFilter...");
        org.openspcoop2.core.registry.ws.client.portadominio.all.SearchFilterPortaDominio _deleteAllByFilter_filter = new org.openspcoop2.core.registry.ws.client.portadominio.all.SearchFilterPortaDominio();
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
        System.out.println("Invoking findAllIds...");
        org.openspcoop2.core.registry.ws.client.portadominio.all.SearchFilterPortaDominio _findAllIds_filter = new org.openspcoop2.core.registry.ws.client.portadominio.all.SearchFilterPortaDominio();
        try {
            java.util.List<org.openspcoop2.core.registry.IdPortaDominio> _findAllIds__return = port.findAllIds(_findAllIds_filter);
            System.out.println("findAllIds.result=" + _findAllIds__return);

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
        System.out.println("Invoking count...");
        org.openspcoop2.core.registry.ws.client.portadominio.all.SearchFilterPortaDominio _count_filter = new org.openspcoop2.core.registry.ws.client.portadominio.all.SearchFilterPortaDominio();
        try {
            long _count__return = port.count(_count_filter);
            System.out.println("count.result=" + _count__return);

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
        System.out.println("Invoking update...");
        org.openspcoop2.core.registry.IdPortaDominio _update_oldIdPortaDominio = new org.openspcoop2.core.registry.IdPortaDominio();
        org.openspcoop2.core.registry.PortaDominio _update_portaDominio = new org.openspcoop2.core.registry.PortaDominio();
        try {
            port.update(_update_oldIdPortaDominio, _update_portaDominio);

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
        System.out.println("Invoking findAll...");
        org.openspcoop2.core.registry.ws.client.portadominio.all.SearchFilterPortaDominio _findAll_filter = new org.openspcoop2.core.registry.ws.client.portadominio.all.SearchFilterPortaDominio();
        try {
            java.util.List<org.openspcoop2.core.registry.PortaDominio> _findAll__return = port.findAll(_findAll_filter);
            System.out.println("findAll.result=" + _findAll__return);

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
        org.openspcoop2.core.registry.PortaDominio _delete_portaDominio = new org.openspcoop2.core.registry.PortaDominio();
        try {
            port.delete(_delete_portaDominio);

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
