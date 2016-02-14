/***************************************************
 * dynaTrace Diagnostics (c) dynaTrace software GmbH
 *
 * @file: SnmpValue.java
 * @date: 16.04.2013
 * @author: stefan.moschinski
 */
package com.dynatrace.diagnostics.plugins.snmp;

import static com.dynatrace.diagnostics.plugins.snmp.SnmpUtil.createOid;
import static com.dynatrace.diagnostics.plugins.snmp.ValueMapper.*;
import static java.lang.String.format;

import java.text.SimpleDateFormat;
import java.util.SortedMap;
import java.util.TreeMap;

import org.snmp4j.smi.OID;
import org.snmp4j.smi.Variable;


/**
 *
 * @author stefan.moschinski
 */
public abstract class SnmpManagedObject<T> {

	private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");

	protected final OID oid;
	private final SortedMap<OID, Variable> variableBindings;

	protected SnmpManagedObject(OID parentOid, int number, int... suffixes) {
		this.oid = createOid(parentOid, number, suffixes);
		this.variableBindings = new TreeMap<OID, Variable>();
	}

	SnmpManagedObject<T> addToMap(OID oid, Variable value) {
		if (variableBindings.containsKey(oid)) {
			throw new IllegalArgumentException(format("The OID '%s' already exists, its value is '%s'", oid, variableBindings.get(oid)));
		}

		variableBindings.put(oid, value);
		return this;
	}

	protected SnmpManagedObject<T> addVariable(int oidSuffix, Object value) {
		return addToMap(createOid(this.oid, oidSuffix), octetString(value));
	}

	protected SnmpManagedObject<T> addDefaultNullValue(int oidSuffix) {
		return addToMap(createOid(this.oid, oidSuffix), octetString(null));
	}

	protected SnmpManagedObject<T> addVariable(int oidSuffix, int value) {
		return addToMap(createOid(this.oid, oidSuffix), unsignedInt(value));
	}

	protected SnmpManagedObject<T> addVariable(OID oid, Object value) {
		return addToMap(oid, octetString(value));
	}

	protected SnmpManagedObject<T> addVariable(OID oid, Variable value) {
		return addToMap(oid, value);
	}

	protected SnmpManagedObject<T> addVariable(OID oid, int value) {
		return addToMap(oid, unsignedInt(value));
	}

	protected SnmpManagedObject<T> addDuration(int oidSuffix, long value) {
		return addToMap(createOid(this.oid, oidSuffix), timeTicks(value));
	}

	protected SnmpManagedObject<T> addDateVariable(int oidSuffix, long value) {
		return addVariable(oidSuffix, dateFormat.format(value));
	}

	protected SnmpManagedObject<T> addVariable(int oid, int subOid, Object value) {
		return addToMap(createOid(this.oid, oid, subOid), octetString(value));
	}

	/**
	 * This method returns the OID to SNMP value mappings
	 *
	 * @return the variableBindings
	 */
	public SortedMap<OID, Variable> getOidVariableMappings() {
		return variableBindings;
	}
}
