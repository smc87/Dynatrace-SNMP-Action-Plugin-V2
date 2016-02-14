/***************************************************
 * dynaTrace Diagnostics (c) dynaTrace software GmbH
 *
 * @file: OIDUtil.java
 * @date: 03.04.2013
 * @author: stefan.moschinski
 */
package com.dynatrace.diagnostics.plugins.snmp;

import static java.lang.String.format;

import java.util.Arrays;

import org.snmp4j.smi.OID;


/**
 *
 * @author stefan.moschinski
 */
public class SnmpUtil {

	/**
	 * Unfortunately, the {@link OID} class is not immutable,
	 * that is: oid2 = oid1.append(1) causes oid2 == oid1
	 * 
	 * @param base {@link OID} that should be used as base for the new {@link OID}
	 * @param suffix
	 * @param suffixes
	 * @return
	 */
	public static OID createOid(OID base, int suffix, int... suffixes) {
		OID newOID = new OID(base);
		newOID.append(suffix);
		for (int subSuffix : suffixes) {
			newOID.append(subSuffix);
		}
		return newOID;
	}

	public static OID createOid(OID base, String suffix) {
		OID newOID = new OID(base);
		newOID.append(suffix);
		return newOID;
	}

	/**
	 * 
	 * @param parentIdx
	 */
	public static int[] createCompoundIdx(int[] parentIdx, int subIdx) {
		int[] idx = Arrays.copyOf(parentIdx, parentIdx.length + 1);
		idx[parentIdx.length] = subIdx;
		return idx;
	}

	/**
	 * 
	 * @param reference value that should be checked whether it is <code>null</code> or not
	 * @param errorMessageTemplate
	 * @param errorMessageArgs
	 * @throws NullPointerException if the given {@code reference} is <code>null</code>
	 * @return
	 */
	public static <T> T checkNotNull(T reference, String errorMessageTemplate, Object... errorMessageArgs) {
		if (reference == null) {
			throw new NullPointerException(format(errorMessageTemplate, errorMessageArgs));
		}
		return reference;
	}


}
