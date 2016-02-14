package com.dynatrace.diagnostics.plugins.snmp;

import static com.dynatrace.diagnostics.plugins.snmp.ValueMapper.octetString;
import static com.dynatrace.diagnostics.plugins.snmp.ValueMapper.timeTicks;

import org.snmp4j.PDU;
import org.snmp4j.PDUv1;
import org.snmp4j.ScopedPDU;
import org.snmp4j.Target;
import org.snmp4j.mp.MessageProcessingModel;
import org.snmp4j.mp.SnmpConstants;
import org.snmp4j.smi.OID;
import org.snmp4j.smi.VariableBinding;
import org.snmp4j.util.PDUFactory;

/**
 * PDU = protocol data unit
 */
class TrapPDUFactory implements PDUFactory {

	@Override
	public PDU createPDU(Target target) {
		PDU request;
		switch (target.getVersion()) {
			case SnmpConstants.version1:
				request = new PDUv1();
				request.setType(PDU.V1TRAP);
				break;
			case SnmpConstants.version3:
				request = new ScopedPDU();
				request.setType(PDU.TRAP);
				break;
			default:
				request = new PDU();
				request.setType(PDU.TRAP);
				break;
		}
		return request;
	}

	/**
	 * Creates a new {@link PDU} and sets TRAP properties for enterprise-specific TRAP PDUs.
	 * 
	 * @param timestamp The time stamp to set (100th seconds since startup).
	 * @param enterprise The enterprise OID to set.
	 * @param trapType The Number of the TRAP to set.
	 */
	public PDU createTrapPDU(Target target, long timestamp, OID enterprise, int trapType) {
		PDU pdu = createPDU(target);
		setTrapInfos(pdu, timestamp, enterprise, trapType);
		return pdu;
	}


	private static void setTrapInfos(PDU pdu, long timestamp, OID enterprise, int trapType) {
		if (pdu instanceof PDUv1) {
			// should we support SNMP v1 at all?
			PDUv1 pduv1 = (PDUv1) pdu;
			pduv1.setTimestamp(timestamp);
			pduv1.setEnterprise(enterprise);
			pduv1.setGenericTrap(PDUv1.ENTERPRISE_SPECIFIC);
			pduv1.setSpecificTrap(trapType);
		} else {
			// trap snmp requires sysuptime at beginning
			pdu.add(new VariableBinding(SnmpConstants.sysUpTime, timeTicks(timestamp)));

			OID trapOID = SnmpUtil.createOid(enterprise, 0, trapType);
			pdu.add(new VariableBinding(SnmpConstants.snmpTrapOID, trapOID));
			pdu.add(new VariableBinding(SnmpConstants.sysDescr, octetString("dynaTrace Trap")));

//			pdu.add(new VariableBinding(SnmpConstants.snmpTrapEnterprise, enterprise));
		}
	}

	@Override
	public PDU createPDU(MessageProcessingModel arg0) {
		throw new UnsupportedOperationException("This method is currently not supported");
	}


}
