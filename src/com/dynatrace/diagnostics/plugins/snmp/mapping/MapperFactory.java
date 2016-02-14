/***************************************************
 * dynaTrace Diagnostics (c) dynaTrace software GmbH
 *
 * @file: CommonMapper.java
 * @date: 03.04.2013
 * @author: stefan.moschinski
 */
package com.dynatrace.diagnostics.plugins.snmp.mapping;

import org.snmp4j.smi.OID;

import com.dynatrace.diagnostics.pdk.Incident;
import com.dynatrace.diagnostics.pdk.Sensitivity;
import com.dynatrace.diagnostics.plugins.snmp.SnmpScalarMapper;
import com.dynatrace.diagnostics.plugins.snmp.domain.Summary;


/**
 *
 * @author stefan.moschinski
 */
public class MapperFactory {

	public static SnmpScalarMapper<Incident> createBasicInfoMapper(OID parentOid, int number) {
		return new IncidentInfoMapper(parentOid, number);
	}


	static SnmpScalarMapper<Sensitivity> createSensitivityMapper(OID parentOid, int number) {
		return new SnmpScalarMapper<Sensitivity>(parentOid, number) {

			@Override
			public void map(Sensitivity sensitivity) {
				addVariable(1, sensitivity.getType());
				addDuration(2, sensitivity.getMinimumIncidentDuration().getDurationInMs());
				addDuration(3, sensitivity.getCooldownDuration().getDurationInMs());
				addDuration(4, sensitivity.getMinimumNonViolatedDuration().getDurationInMs());
			}
		};
	}
	
	static SnmpScalarMapper<Summary> createSummaryMapper(OID parentOid, int number) {
		return new SnmpScalarMapper<Summary>(parentOid, number) {

			@Override
			public void map(Summary summary) {
				addVariable(1, summary.getSeverity());
				addVariable(2, summary.getNumberOfViolations());
				addVariable(3, summary.getNumberOfTriggerValues());
				addVariable(4, summary.getAffectedApplications());
			}
		};
	}

}
