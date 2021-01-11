/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2021 Link.it srl (https://link.it). 
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


package org.openspcoop2.utils.resources;

/**
 * Classe utilizzabile per raccogliere la configurazione dell'operazione mkdir
 *
 * @author Andrea Poli (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class FileSystemMkdirConfig {

	boolean checkCanRead = true;
	boolean checkCanWrite = true;
	boolean checkCanExecute = false;
	boolean crateParentIfNotExists = true;
	
	public boolean isCheckCanRead() {
		return this.checkCanRead;
	}
	public void setCheckCanRead(boolean checkCanRead) {
		this.checkCanRead = checkCanRead;
	}
	public boolean isCheckCanWrite() {
		return this.checkCanWrite;
	}
	public void setCheckCanWrite(boolean checkCanWrite) {
		this.checkCanWrite = checkCanWrite;
	}
	public boolean isCrateParentIfNotExists() {
		return this.crateParentIfNotExists;
	}
	public void setCrateParentIfNotExists(boolean crateParentIfNotExists) {
		this.crateParentIfNotExists = crateParentIfNotExists;
	}
	public boolean isCheckCanExecute() {
		return this.checkCanExecute;
	}
	public void setCheckCanExecute(boolean checkCanExecute) {
		this.checkCanExecute = checkCanExecute;
	}
}
