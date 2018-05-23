/*
 * OpenSPCoop - Customizable API Gateway 
 * http://www.openspcoop2.org
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

package org.openspcoop2.protocol.engine.archive;

import org.openspcoop2.core.config.driver.db.DriverConfigurazioneDB;
import org.openspcoop2.core.controllo_traffico.dao.jdbc.JDBCServiceManager;
import org.openspcoop2.core.registry.driver.db.DriverRegistroServiziDB;

/**
 *  ArchiveEngine
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ArchiveEngine extends AbstractArchiveEngine {

	public ArchiveEngine(DriverRegistroServiziDB driverRegistroServizi,
			DriverConfigurazioneDB driverConfigurazione,
			JDBCServiceManager serviceManagerControlloCongestione) {
		super(driverRegistroServizi, driverConfigurazione, serviceManagerControlloCongestione);
	}

	@Override
	public boolean isVisioneOggettiGlobale(String userLogin) {
		return true;
	}

}
