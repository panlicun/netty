package com.topwalk.core.session;

public class Session {

	String nodeId;
	String nodeName;

	public Session() {
	}

	public Session(String nodeId, String nodeName) {
		this.nodeId = nodeId;
		this.nodeName = nodeName;
	}

	public Session(String nodeId) {
		this.nodeId = nodeId;
	}

	public String getNodeId() {
		return nodeId;
	}

	public void setNodeId(String nodeId) {
		this.nodeId = nodeId;
	}

	public String getNodeName() {
		return nodeName;
	}

	public void setNodeName(String nodeName) {
		this.nodeName = nodeName;
	}

	@Override
	public String toString() {
		return "Session{" +
				"nodeName='" + nodeName + "-" + nodeId + '\'' +
				'}';
	}
}
