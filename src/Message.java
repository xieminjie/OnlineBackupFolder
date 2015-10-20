import java.io.Serializable;
public class Message implements Serializable {
	private String actionType;
	private String toFilename;
	Message(String actionType,String toFilename){
		super();
		this.actionType = actionType;
		this.toFilename = toFilename;
	}
	public String getActionType() {
		return actionType;
	}
	public void setActionType(String actionType) {
		this.actionType = actionType;
	}
	public String getToFilename() {
		return toFilename;
	}
	public void setToFilename(String toFilename) {
		this.toFilename = toFilename;
	}
}
