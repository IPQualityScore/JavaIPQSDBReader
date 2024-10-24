import com.ipqualityscore.JavaIPQSDBReader.DBReader;
import com.ipqualityscore.JavaIPQSDBReader.FileReader;
import com.ipqualityscore.JavaIPQSDBReader.IPQSRecord;
import org.junit.Test;

import java.nio.file.FileSystems;

public class JavaIPQSDBReaderTest {
	@Test
	public void integrationTest() {
		try {
			FileReader reader = DBReader.Open(FileSystems.getDefault().getPath("IPQualityScore-IP-Reputation-Database-IPv4-Sample.ipqs"));

			String ip = "8.8.0.0";
			IPQSRecord record = reader.Fetch(ip);

			// Use the IPQSRecord to print some data about this IP.
			if(record.isProxy()){
				System.out.println(ip + " is a proxy.");
			} else {
				System.out.println(ip + " is not a proxy.");
			}

			if(record.isVPN()){
				System.out.println(ip + " is a vpn.");
			} else {
				System.out.println(ip + " is not a vpn.");
			}

			if(record.isTOR()){
				System.out.println(ip + " is a tor node.");
			} else {
				System.out.println(ip + " is not a tor node.");
			}

			if(record.isCrawler()){
				System.out.println(ip + " is a crawler.");
			} else {
				System.out.println(ip + " is not a crawler.");
			}

			if(record.isBot()){
				System.out.println(ip + " is likely a bot.");
			} else {
				System.out.println(ip + " is likely not a bot.");
			}

			if(record.hasRecentAbuse()){
				System.out.println(ip + " has recently engaged in abuse.");
			} else {
				System.out.println(ip + " has not engaged in abuse recently.");
			}

			if(record.isBlacklisted()){
				System.out.println(ip + " is blacklisted.");
			} else {
				System.out.println(ip + " is not blacklisted.");
			}

			if(record.isPrivate()){
				System.out.println(ip + " is a private (non routeable) IP address.");
			} else {
				System.out.println(ip + " is not a private IP address.");
			}

			if(record.isMobile()){
				System.out.println(ip + " is associated with a mobile carrier.");
			} else {
				System.out.println(ip + " is not likely to be a mobile carrier.");
			}

			if(record.hasOpenPorts()){
				System.out.println(ip + " has open ports.");
			} else {
				System.out.println(ip + " does not have open ports.");
			}

			if(record.isHostingProvider()){
				System.out.println(ip + " is a hosting provider.");
			} else {
				System.out.println(ip + " is not a hosting provider.");
			}

			if(record.isActiveVPN()){
				System.out.println(ip + " is an active VPN.");
			} else {
				System.out.println(ip + " is not an active VPN.");
			}

			if(record.isActiveTOR()){
				System.out.println(ip + " is an active TOR node.");
			} else {
				System.out.println(ip + " is not an active TOR node.");
			}

			if(record.isPublicAccessPoint()){
				System.out.println(ip + " is a public access point.");
			} else {
				System.out.println(ip + " is not a public access point.");
			}

			System.out.println(ip + " is from " + record.getCity() + ", " + record.getCountry() + " (" + record.getRegion() + ")");

			System.out.println(ip + "'s ISP is " + record.getISP() + " and is owned by " + record.getOrganization() + ".");

			System.out.println(ip + " has an ASN of " + record.getASN() + ".");

			System.out.println(ip + " has a timezone of " + record.getTimezone());

			System.out.println(ip + " has a zipcode of " + record.getZipcode());

			System.out.println(ip + " has a hostname of " + record.getHostname());

			System.out.println(ip + " has a geographic location of approximately: " + record.getLatitude() + ", " + record.getLongitude());

			System.out.println(ip + " is a " + record.getConnectionType().toString() + " connection. ");

			System.out.println(ip + " has a " + record.getAbuseVelocity().toString() + " abuse velocity.");

			System.out.println(ip + " has a fraud score of " + record.getFraudScore().forStrictness(0) + " for strictness level zero.");

			System.out.println(ip + " has a fraud score of " + record.getFraudScore().forStrictness(1) + " for strictness level one.");

			try {
				System.out.println(ip + " has a fraud score of " + record.getFraudScore().forStrictness(2) + " for strictness level one.");
			} catch (Exception e){
				System.out.println("yay");
			}
		} catch (Exception e){
			System.out.println(e.getMessage());
		}
	}
}
