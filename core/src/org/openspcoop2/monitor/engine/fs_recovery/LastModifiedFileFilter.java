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
package org.openspcoop2.monitor.engine.fs_recovery;

import java.io.File;
import java.io.FileFilter;
import java.util.Date;

/**
 * LastModifiedFileFilter
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class LastModifiedFileFilter implements FileFilter {
		private long msAttesaProcessingFile;
		
		public LastModifiedFileFilter(long msAttesaProcessingFile) {
			this.msAttesaProcessingFile = msAttesaProcessingFile;
		}

		@Override
		public boolean accept(File pathname) {
			//Accetto il file se non e' una directory e se non e' stato modificato negli ultimi this.msAttesaProcessingFile millisecondi 
			Date now = new Date();
			Long fromTime = now.getTime() - (this.msAttesaProcessingFile);
			return !pathname.isDirectory() && pathname.lastModified() < fromTime;
		}
		
}
