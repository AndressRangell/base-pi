package com.facephi.soaplibrary;

public class User {

	private String name;
	private String identifier;
	private String template;
	private SoapOperationType operationType;

	public User() {}

	public User (String name, String template) {
		this.setName(name);
		this.setTemplate(template);
	}

	public User (String name, String identifier, String template) {
		this.setName(name);
		this.setIdentifier(identifier);
		this.setTemplate(template);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getIdentifier() {
		return identifier;
	}

	public void setIdentifier(String identifier) {
		this.identifier = identifier;
	}

	public String getTemplate() {
		return template;
	}

	public void setTemplate(String template) {
		this.template = template;
	}

	public SoapOperationType getOperationType() {
		return operationType;
	}

	public void setOperationType(SoapOperationType operationType) {
		this.operationType = operationType;
	}
}
