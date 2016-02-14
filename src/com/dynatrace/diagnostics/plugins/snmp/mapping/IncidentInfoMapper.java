/***************************************************
 * dynaTrace Diagnostics (c) dynaTrace software GmbH
 *
 * @file: BasicInformationMapper.java
 * @date: 03.04.2013
 * @author: stefan.moschinski
 */
package com.dynatrace.diagnostics.plugins.snmp.mapping;

import static com.dynatrace.diagnostics.plugins.snmp.SnmpUtil.createOid;
import static com.dynatrace.diagnostics.plugins.snmp.mapping.MapperFactory.createSensitivityMapper;

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.snmp4j.smi.OID;

import com.dynatrace.diagnostics.pdk.AgentSource;
import com.dynatrace.diagnostics.pdk.CollectorSource;
import com.dynatrace.diagnostics.pdk.Incident;
import com.dynatrace.diagnostics.pdk.Measure;
import com.dynatrace.diagnostics.pdk.ServerSource;
import com.dynatrace.diagnostics.pdk.Source;
import com.dynatrace.diagnostics.pdk.SourceType;
import com.dynatrace.diagnostics.pdk.Violation;
import com.dynatrace.diagnostics.pdk.Violation.TriggerValue;
import com.dynatrace.diagnostics.plugins.snmp.SnmpScalarMapper;
import com.dynatrace.diagnostics.plugins.snmp.domain.Summary;


/**
 * Maps the Incident data to SNMP managed objects and returns the resulting map
 * that is accessible by calling the {@link #getOidVariableMappings()}.
 *
 * @author stefan.moschinski
 *
 * @author eugene.turetsky	added limit on size of SNMP traps, changed MIB layout and order of incidents
 *
 */
class IncidentInfoMapper extends SnmpScalarMapper<Incident> {

	private static final Logger log = Logger.getLogger(IncidentInfoMapper.class.getName());


	IncidentInfoMapper(OID parentOid, int number) {
		super(parentOid, number);
	}

	@Override
	public void map(Incident incident) {
		addVariable(1, incident.getIncidentRule().getName());
		addVariable(2, incident.getMessage());
		addVariable(3, incident.getIncidentRule().getDescription());
//		structures / subnodes to useable for TRAP notifications
//		addSubNode(createSummaryMapper(this.oid, 4), getSummaryInformation(incident));
		addVariable(4, incident.getSeverity());
		addVariable(5, incident.getKey().getUUID());
		addVariable(6, incident.getServerName());
		addVariable(7, incident.getKey().getSystemProfile());
		addDateVariable(8, incident.getStartTime().getTimestampInMs());

		if (incident.isClosed()) {
			addDateVariable(9, incident.getEndTime().getTimestampInMs());
			addVariable(10, incident.getDuration());
		}

		addVariable(11, incident.getRecordedSessionId());
		addVariable(12, incident.getRecordedSessionName());

		addSubNode(createSensitivityMapper(this.oid, 13), incident.getIncidentRule().getSensitivity());
		// added getOrderedViolations method that changes order of violations to pick up first violations which have agent-name/host, monitor name populated
		addSubNode(getViolationMapper(), getOrderedViolations(incident.getViolations()));
	}

	private Collection<Violation> getOrderedViolations(Collection<Violation> violations) {
		LinkedList<Violation> orderedViolations = new LinkedList<Violation>();

		Source source;
		SourceType type;
		String s;
		for (Violation violation : violations) {
			if (violation != null && violation.getViolatedMeasure() != null && (type = (source = violation.getViolatedMeasure().getSource()).getSourceType()) != null) {
				switch (type) {
				case Agent:
					if ((s = ((AgentSource)source).getHost()) != null && !s.isEmpty()) {
						if (log.isLoggable(Level.FINER)) {
							log.finer("IncidentInfoMapper: getOrderedViolations method: Agent host is '" + s + "'; Agent name is '" + ((AgentSource)source).getName() + "'; adding violation first to the orderedViolations collection" );
						}
						orderedViolations.addFirst(violation);
					} else {
						if (log.isLoggable(Level.FINER)) {
							log.finer("IncidentInfoMapper: getOrderedViolations method: Agent host is '" + s + "'; Agent name is '" + ((AgentSource)source).getName() + "'; adding violation last to the orderedViolations collection" );
						}
						orderedViolations.addLast(violation);
					}
					break;
				case AgentGroup:
					orderedViolations.addLast(violation);
					break;
				case Monitor:
					if ((s = violation.getViolatedMeasure().getName()) != null && !s.isEmpty() && s.indexOf('@') != -1) {
						if (log.isLoggable(Level.FINER)) {
							log.finer("IncidentInfoMapper: getOrderedViolations method: Monitor name is '" + s + "' adding violation first to the orderedViolations collection" );
						}
						orderedViolations.addFirst(violation);
					} else {
						if (log.isLoggable(Level.FINER)) {
							log.finer("IncidentInfoMapper: getOrderedViolations method: Monitor name is '" + s + "' adding violation last to the orderedViolations collection" );
						}
						orderedViolations.addLast(violation);
					}
					break;
				case Collector:
					if ((s = ((CollectorSource)source).getHost()) != null && !s.isEmpty()) {
						if (log.isLoggable(Level.FINER)) {
							log.finer("IncidentInfoMapper: getOrderedViolations method: Collector host is '" + s + "'; Collector Name is '" + ((CollectorSource)source).getName() + "'; adding violation first to the orderedViolations collection" );
						}
						orderedViolations.addFirst(violation);
					} else {
						if (log.isLoggable(Level.FINER)) {
							log.finer("IncidentInfoMapper: getOrderedViolations method: Collector host is '" + s + "'; Collector Name is '" + ((CollectorSource)source).getName() + "'; adding violation last to the orderedViolations collection" );
						}
						orderedViolations.addLast(violation);
					}
					break;
				case Server:
					if ((s = ((ServerSource)source).getName()) != null && !s.isEmpty()) {
						if (log.isLoggable(Level.FINER)) {
							log.finer("IncidentInfoMapper: getOrderedViolations method: Server name is '" + s + "' adding violation first to the orderedViolations collection" );
						}
						orderedViolations.addFirst(violation);
					} else {
						if (log.isLoggable(Level.FINER)) {
							log.finer("IncidentInfoMapper: getOrderedViolations method: Server name is '" + s + "' adding violation first to the orderedViolations collection" );
						}
						orderedViolations.addLast(violation);
					}
					break;
				default:
					orderedViolations.addLast(violation);
					log.severe("IncidentInfoMapper: getOderedViolations method: violation '" + violation.getViolatedMeasure().getName() + "' contains unexpected source type '" + type.name() + "'");
					break;
				}
			}

		}

		return orderedViolations;
	}

	/**
	 *
	 * Generates summary string with number of violations and number of trigger values which triggered these violations
	 * @param incident
	 * @return
	 */
	private Summary getSummaryInformation(Incident incident) {

		// create and initialize Summary object
		Summary summary = new Summary();
		summary.setAffectedApplications(new HashSet<String>());

		if (incident == null) {
			return summary;
		}

		summary.setSeverity(incident.getSeverity());

		Collection<Violation> violations;
		if ((violations = incident.getViolations()) != null) {
			summary.setNumberOfViolations(violations.size());

			// calculate number of trigger values
			int n = 0;
			Collection<TriggerValue> tvalues;
			Measure measure;
			String app;
			for (Violation violation : violations) {
				if (violation == null) {
					continue;
				}

				if ((tvalues = violation.getTriggerValues()) != null) {
					n =+ tvalues.size();
				}

				if ((measure = violation.getViolatedMeasure()) != null && (app = measure.getApplication()) != null && !app.isEmpty()) {
					summary.getAffectedApplications().add(app);
				}
			}

			summary.setNumberOfTriggerValues(n);

		}

		return summary;
	}

	private ViolationMapper getViolationMapper() {
		ViolationChildTableOids violationChildTableOids = getChildTableOids();
		return new ViolationMapper(this.oid, 14, violationChildTableOids);
	}

	private ViolationChildTableOids getChildTableOids() {
		OID measureTableOid = createOid(this.oid, 15);
		OID measureAgentGroupTableOid = createOid(this.oid, 16);
		OID splittingsTableOid = createOid(this.oid, 17);
		OID triggerValueTableOid = createOid(this.oid, 18);
		OID triggerValueAgentMappingTableOid = createOid(this.oid, 19);

		ViolationChildTableOids violationChildTableOids = new ViolationChildTableOids(measureTableOid, measureAgentGroupTableOid, splittingsTableOid, triggerValueTableOid,
				triggerValueAgentMappingTableOid);
		return violationChildTableOids;
	}


}
