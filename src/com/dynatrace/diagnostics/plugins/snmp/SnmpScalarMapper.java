/***************************************************
 * dynaTrace Diagnostics (c) dynaTrace software GmbH
 * 
 * @file: MibMeta.java
 * @date: 02.04.2013
 * @author: stefan.moschinski
 */
package com.dynatrace.diagnostics.plugins.snmp;

import java.util.Map.Entry;

import org.snmp4j.smi.OID;
import org.snmp4j.smi.Variable;


/**
 * 
 * @author stefan.moschinski
 */
public abstract class SnmpScalarMapper<T> extends SnmpManagedObject<T> {

	/**
	 * 
	 * @param parentOid OID of the parent managed object
	 * @param number id of this scalar (e.g., if the parentOid is 1.1 and the {@code number} is seven the OID of this object is
	 *        1.1.7
	 * @param suffixes
	 */
	public SnmpScalarMapper(OID parentOid, int number, int... suffixes) {
		super(parentOid, number, suffixes);
	}

	protected <X> SnmpScalarMapper<T> addSubNode(SnmpScalarMapper<X> subNode, X value) {
		subNode.map(value);
		for (Entry<OID, Variable> subEntry : subNode.getOidVariableMappings().entrySet()) {
			addToMap(subEntry.getKey(), subEntry.getValue());
		}
		return this;
	}

	/**
	 * Maps the given value to the scalar and tabular SNMP objects representing it.
	 * 
	 * @param value the value that should be mapped to SNMP managed objects
	 */
	public abstract void map(T value);

}
