package org.openspcoop2.web.ctrlstat.plugins;

public class WrapperExtendedBean {

	private IExtendedCoreServlet extendedServlet;
	private IExtendedBean extendedBean;
	private Object originalBean;
	private boolean manageOriginalBean = true;
	
	public boolean isManageOriginalBean() {
		return this.manageOriginalBean;
	}
	public void setManageOriginalBean(boolean manageOriginalBean) {
		this.manageOriginalBean = manageOriginalBean;
	}
	public IExtendedCoreServlet getExtendedServlet() {
		return this.extendedServlet;
	}
	public void setExtendedServlet(IExtendedCoreServlet extendedServlet) {
		this.extendedServlet = extendedServlet;
	}
	public IExtendedBean getExtendedBean() {
		return this.extendedBean;
	}
	public void setExtendedBean(IExtendedBean extendedBean) {
		this.extendedBean = extendedBean;
	}
	public Object getOriginalBean() {
		return this.originalBean;
	}
	public void setOriginalBean(Object originalBean) {
		this.originalBean = originalBean;
	}
}
