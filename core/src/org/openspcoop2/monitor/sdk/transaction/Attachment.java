package org.openspcoop2.monitor.sdk.transaction;

/**
 * Attachment
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class Attachment {
	
	public Attachment(byte[] content, String cid, String mime) {
		this.cid = cid;
		this.content = content;
		this.mime = mime;
	}
	
	protected String cid;
	protected String mime;
	protected byte[] content;
	private boolean updated = false;

	public boolean isUpdated() {
		return this.updated;
	}
	public void setUpdated(boolean updated) {
		this.updated = updated;
	}
	public String getContentID() {
		return this.cid;
	}
	public void setContentID(String cid) {
		this.cid = cid;
	}
	
	public String getContentType() {
		return this.mime;
	}
	public void setContentType(String mime) {
		this.mime = mime;
	}
	
	public byte[] getContentAsByte() {
		return this.content;
	}
	public void setContentAsByte(byte[] content) {
		this.content = content;
	}
	
}
