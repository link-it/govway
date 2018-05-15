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
		private int minutiAttesaProcessingFile;
		
		public LastModifiedFileFilter(int minutiAttesaProcessingFile) {
			this.minutiAttesaProcessingFile = minutiAttesaProcessingFile;
		}

		@Override
		public boolean accept(File pathname) {
			//Accetto il file se non e' una directory e se non e' stato modificato negli ultimi this.minutiAttesaProcessingFile minuti 
			Date now = new Date();
			Long fromTime = now.getTime() - (60 * 1000 * this.minutiAttesaProcessingFile);
			return !pathname.isDirectory() && pathname.lastModified() < fromTime;
		}
		
}
