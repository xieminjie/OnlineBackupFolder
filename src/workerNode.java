
public class workerNode {
	private String serverAddress;
	private int port;
	private String workername;
	private String status;
	public workerNode(String workername,String serverAddress,int port,String status){
		this.setServerAddress(serverAddress);
		this.setPort(port);
		this.setWorkername(workername);
		this.setStatus(status);
	}
	public String getServerAddress() {
		return serverAddress;
	}
	public void setServerAddress(String serverAddress) {
		this.serverAddress = serverAddress;
	}
	public int getPort() {
		return port;
	}
	public void setPort(int port) {
		this.port = port;
	}
	public String getWorkername() {
		return workername;
	}
	public void setWorkername(String workername) {
		this.workername = workername;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	@Override
	public String toString() {
		return "workername = " + workername + ", serverAddress=" + serverAddress
				+ ", port=" + port + ", status=" + status;
	}

	
}
