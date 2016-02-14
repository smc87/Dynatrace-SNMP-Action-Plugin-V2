/***************************************************
 * dynaTrace Diagnostics (c) dynaTrace software GmbH
 * 
 * @file: AbstractSnmpTable.java
 * @date: 15.04.2013
 * @author: stefan.moschinski
 */
package com.dynatrace.diagnostics.plugins.snmp;

import java.util.Arrays;

import org.snmp4j.smi.OID;


/**
 * A class subtyping this class represents a child table of another table;
 * 
 * @author stefan.moschinski
 */
public abstract class AbstractChildTable<T> extends SnmpAbstractTable<T> implements SnmpChildTable<T> {

	private final int[] parentIdx;


	public AbstractChildTable(OID tableOid, int... parentIdx) {
		super(tableOid);

		if (parentIdx == null || parentIdx.length == 0)
			throw new IllegalArgumentException("A child table requires at least one parent id");

		this.parentIdx = parentIdx;
	}

	@Override
	public int[] getParentIdx() {
		return Arrays.copyOf(parentIdx, parentIdx.length);
	}
}