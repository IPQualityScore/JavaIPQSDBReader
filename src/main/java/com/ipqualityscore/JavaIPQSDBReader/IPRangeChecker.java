package com.ipqualityscore.JavaIPQSDBReader;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class IPRangeChecker {

  public static long ipToLong(InetAddress ip) {
		byte[] octets = ip.getAddress();
		long result = 0;
		for (byte octet : octets) {
			result <<= 8;
			result |= octet & 0xff;
		}
		return result;
	}

	public static boolean isValidRange(String ipStart, String ipEnd,
			String ipToCheck) {
		try {
			long ipLo = ipToLong(InetAddress.getByName(ipStart));
			long ipHi = ipToLong(InetAddress.getByName(ipEnd));
			long ipToTest = ipToLong(InetAddress.getByName(ipToCheck));
			return (ipToTest >= ipLo && ipToTest <= ipHi);
		} catch (UnknownHostException e) {
			e.printStackTrace();
			return false;
		}
	}

	// public static void main(String[] args) {

	// 	System.out.println(isValidRange("122.170.122.0", "122.170.122.255",
	// 			"122.170.122.215"));

	// }

}

// import java.net.InetAddress;
// import java.net.UnknownHostException;

// public final class IpAddressMatcher {
//     private final int nMaskBits;
//     private final InetAddress requiredAddress;

//     public IpAddressMatcher(String ipAddress) {

//         if (ipAddress.indexOf('/') > 0) {
//             String[] addressAndMask = ipAddress.split("/");
//             ipAddress = addressAndMask[0];
//             nMaskBits = Integer.parseInt(addressAndMask[1]);
//         }
//         else {
//             nMaskBits = -1;
//         }
//         requiredAddress = parseAddress(ipAddress);
//         assert  (requiredAddress.getAddress().length * 8 >= nMaskBits) :
//                 String.format("IP address %s is too short for bitmask of length %d",
//                         ipAddress, nMaskBits);
//     }

//     public boolean matches(String address) {
//         InetAddress remoteAddress = parseAddress(address);

//         if (!requiredAddress.getClass().equals(remoteAddress.getClass())) {
//             return false;
//         }

//         if (nMaskBits < 0) {
//             return remoteAddress.equals(requiredAddress);
//         }

//         byte[] remAddr = remoteAddress.getAddress();
//         byte[] reqAddr = requiredAddress.getAddress();

//         int nMaskFullBytes = nMaskBits / 8;
//         byte finalByte = (byte) (0xFF00 >> (nMaskBits & 0x07));

//         for (int i = 0; i < nMaskFullBytes; i++) {
//             if (remAddr[i] != reqAddr[i]) {
//                 return false;
//             }
//         }

//         if (finalByte != 0) {
//             return (remAddr[nMaskFullBytes] & finalByte) == (reqAddr[nMaskFullBytes] & finalByte);
//         }

//         return true;
//     }

//     private InetAddress parseAddress(String address) {
//         try {
//             return InetAddress.getByName(address);
//         }
//         catch (UnknownHostException e) {
//             throw new IllegalArgumentException("Failed to parse address" + address, e);
//         }
//     }
// }