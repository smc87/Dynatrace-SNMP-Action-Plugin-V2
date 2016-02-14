/***************************************************
 * dynaTrace Diagnostics (c) dynaTrace software GmbH
 * 
 * @file: SourceBlub.java
 * @date: 16.04.2013
 * @author: stefan.moschinski
 */
package com.dynatrace.diagnostics.plugins.snmp.mapping;

import static com.dynatrace.diagnostics.pdk.SourceType.*;

import java.util.Collection;
import java.util.Map;
import java.util.TreeMap;

import org.snmp4j.smi.OID;
import org.snmp4j.smi.Variable;

import com.dynatrace.diagnostics.pdk.AgentGroupSource;
import com.dynatrace.diagnostics.pdk.AgentSource;
import com.dynatrace.diagnostics.pdk.MonitorSource;
import com.dynatrace.diagnostics.pdk.Source;
import com.dynatrace.diagnostics.pdk.SourceType;


/**
 * 
 * @author stefan.moschinski
 */
public class SourceMapper {


	private final Source source;
	private final SourceType sourceType;

	public SourceMapper(Source source) {
		this.source = source;
		sourceType = source == null ? null : source.getSourceType();
	}

	void addSourceColumns(ColumnBuilder columnBuilder) {
		columnBuilder.addColumn(sourceType);
		if (sourceType == Agent) {
			AgentSource agentSource = (AgentSource) source;
			columnBuilder.addColumn(agentSource.getName());
			columnBuilder.addColumn(agentSource.getHost());
		} else {
			columnBuilder.addColumn(null);
			columnBuilder.addColumn(null);
		}
		if (sourceType == Monitor) {
			MonitorSource monitorSource = (MonitorSource) source;
			columnBuilder.addColumn(monitorSource.getName());
		} else {
			columnBuilder.addColumn(null);
		}
	}

	Map<OID, Variable> createAgentGroupTable(OID agentGroupNameTableOid, int[] parentIdx) {
		Map<OID, Variable> entries = new TreeMap<OID, Variable>();
		if (sourceType == AgentGroup) {
			AgentGroupSource agentSource = (AgentGroupSource) source;
			Collection<String> agentGroupNames = agentSource.getAgentGroupNames();

			SingleValueTable agentGroupTable = new SingleValueTable(agentGroupNameTableOid, parentIdx);
			for (String agentGroupName : agentGroupNames) {
				agentGroupTable.addRow(agentGroupName);
			}
			entries.putAll(agentGroupTable.getOidVariableMappings());
		}
		return entries;
	}
}
