package org.openspcoop2.pdd.core.handlers.notifier.engine;

import java.io.File;

import org.openspcoop2.utils.io.notifier.unblocked.ResultStreamingHandler;

public class NotifierResultStreamingHandler implements ResultStreamingHandler {

	private File file;
	private int executeUpdateRow;
	private boolean saveOnFileSystem;
	
	public File getFile() {
		return this.file;
	}
	public void setFile(File file) {
		this.file = file;
	}
	public int getExecuteUpdateRow() {
		return this.executeUpdateRow;
	}
	public void setExecuteUpdateRow(int executeUpdateRow) {
		this.executeUpdateRow = executeUpdateRow;
	}
	public boolean isSaveOnFileSystem() {
		return this.saveOnFileSystem;
	}
	public void setSaveOnFileSystem(boolean saveOnFileSystem) {
		this.saveOnFileSystem = saveOnFileSystem;
	}
	
}
