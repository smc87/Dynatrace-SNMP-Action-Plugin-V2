package com.dynatrace.diagnostics.plugins.snmp.domain;

import java.util.Set;

import com.dynatrace.diagnostics.pdk.Incident.Severity;

public class Summary {
	private Integer numberOfViolations;
	private Integer numberOfTriggerValues;
	private Set<String> affectedApplications;
	private Severity severity;
	public Integer getNumberOfViolations() {
		return numberOfViolations;
	}
	public void setNumberOfViolations(Integer numberOfViolations) {
		this.numberOfViolations = numberOfViolations;
	}
	public Integer getNumberOfTriggerValues() {
		return numberOfTriggerValues;
	}
	public void setNumberOfTriggerValues(Integer numberOfTriggerValues) {
		this.numberOfTriggerValues = numberOfTriggerValues;
	}
	public Set<String> getAffectedApplications() {
		return affectedApplications;
	}
	public void setAffectedApplications(Set<String> affectedApplications) {
		this.affectedApplications = affectedApplications;
	}
	public Severity getSeverity() {
		return severity;
	}
	public void setSeverity(Severity severity) {
		this.severity = severity;
	}
	
	
	
}
