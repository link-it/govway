package org.openspcoop2.pdd.core.eventi;

import java.util.Date;

public class LastDateEvento {

	private Date date;
	private boolean exists;
	private boolean updated;
	
	public Date getDate() {
		return this.date;
	}
	public void setDate(Date date) {
		this.date = date;
	}
	public boolean isExists() {
		return this.exists;
	}
	public void setExists(boolean exists) {
		this.exists = exists;
	}
	public boolean isUpdated() {
		return this.updated;
	}
	public void setUpdated(boolean updated) {
		this.updated = updated;
	}
	
}
