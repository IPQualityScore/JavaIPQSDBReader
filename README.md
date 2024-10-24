<div class="documentation_overview">
	<h1 class="doc-title">IPQualityScore IP Address Reputation &amp; Proxy Detection Java DB Reader</h1>
	<div class="spacing-10"></div>
	<h2 class="text-bold headerHR" style="font-size: 1.5em;">Flat File Version 1.0 (Reader 1.0.8)</h2>
	<div class="spacing-10"></div>
	<p>
        The IPQS Java reader brings a new standard of performance and compression to our IP reputation and geo location services. Setting up and using the reader can be achieved in minutes.
    </p>
    <p>
	    You can find the full <a href="https://www.ipqualityscore.com/documentation/ip-reputation-database/java" target="_blank">Java IPQualityScore DB Reader Documentation here</a> or view a description of what our <a href="https://www.ipqualityscore.com/proxy-detection-database" target="_blank">proxy detection database does here</a>.
	</p>
    <h6 class="text-bold headerHR">Installation</h6>
	<div class="spacing-10"></div>
		<p>
        Installation can be achieved using maven or included manually in your source build. To install via maven <a href="https://www.ipqualityscore.com/documentation/ip-reputation-database/java" target="_blank">view our install documentation here</a>.
    </p>
    <h6 class="text-bold headerHR">Usage</h6>
	<div class="spacing-10"></div>
	<p>
        Using our flat file database system to lookup an IP address is simple:
    </p>
    <div class="row">
		<div class="col-md-12 lgcode">
            <pre class="highlight markdown"><code>
try {
    // Open a DB file reader.
    FileReader reader = DBReader.Open("IPQualityScore-IP-Reputation-Database-Sample.ipqs");
    
    // Request data about a given IP address.
    String ip = "8.8.0.0";
    IPQSRecord record = reader.Fetch(ip);

    // Use the IPQSRecord to print some data about this IP.
    if(record.isProxy()){
        System.out.println(ip + " is a proxy.");
    } else {
        System.out.println(ip + " is not a proxy.");
    }
} catch (Exception e){
    System.out.println(e.getMessage());
}
           </code></pre>
        </div>
    </div>
    <h6 class="text-bold headerHR">IPQSRecord Methods</h6>
	<div class="spacing-10"></div>
	<p>
        Depending on which database file you receive some of these methods may be unavailable. If the method in question is unavailable in your database the method return will default to Java's default value for that type or false.
    </p>
    <div class="row">
		<div class="col-md-12">
			<table class="table table-legend custom-tablelegend">
				<thead>
					<tr>
						<th style="width:10%">Method</th>
						<th style="width:10%">Type</th>
						<th>Description</th>
					</tr>
				</thead>
				<tbody>
					<tr>
						<td>record.isProxy()</td>
						<td>boolean</td>
						<td>Is this IP address suspected to be a proxy? (SOCKS, Elite, Anonymous, VPN, Tor, etc.)</td>
					</tr>
                    <tr>
						<td>record.isVPN()</td>
						<td>boolean</td>
						<td>Is this IP suspected of being a VPN connection? This can include data center ranges which can become active VPNs at any time. The "proxy" status will always be true when this value is true.</td>
					</tr>
                    <tr>
						<td>record.isTOR()</td>
						<td>boolean</td>
						<td>Is this IP suspected of being a TOR connection? This can include previously active TOR nodes and exits which can become active TOR exits at any time. The "proxy" status will always be true when this value is true.</td>
					</tr>
                    <tr>
						<td>record.isCrawler()</td>
						<td>boolean</td>
						<td>Is this IP associated with being a confirmed crawler from a mainstream search engine such as Googlebot, Bingbot, Yandex, etc. based on hostname or IP address verification.</td>
					</tr>
                    <tr>
						<td>record.isBot()</td>
						<td>boolean</td>
						<td>Indicates if bots or non-human traffic has recently used this IP address to engage in automated fraudulent behavior. Provides stronger confidence that the IP address is suspicious.</td>
					</tr>
                    <tr>
						<td>record.hasRecentAbuse()</td>
						<td>boolean</td>
						<td>This value will indicate if there has been any recently verified abuse across our network for this IP address. Abuse could be a confirmed chargeback, compromised device, fake app install, or similar malicious behavior within the past few days.</td>
					</tr>
                    <tr>
						<td>record.isBlacklisted()</td>
						<td>boolean</td>
						<td>This value will indicate if the IP has been blacklisted by any 3rd party agency for spam, abuse or fraud.</td>
					</tr>
                    <tr>
						<td>record.isPrivate()</td>
						<td>boolean</td>
						<td>This value will indicate if the IP is a private, nonrouteable IP address.</td>
					</tr>
                    <tr>
						<td>record.isMobile()</td>
						<td>boolean</td>
						<td>This value will indicate if the IP is likely owned by a mobile carrier.</td>
					</tr>
                    <tr>
						<td>record.hasOpenPorts()</td>
						<td>boolean</td>
						<td>This value will indicate if the IP has recently had open (listening) ports.</td>
					</tr>
                    <tr>
						<td>record.isHostingProvider()</td>
						<td>boolean</td>
						<td>This value will indicate if the IP is likely owned by a hosting provider or is leased to a hosting company.</td>
					</tr>
                    <tr>
						<td>record.isActiveVPN()</td>
						<td>boolean</td>
						<td>Identifies active VPN connections used by popular VPN services and private VPN servers.</td>
					</tr>
                    <tr>
						<td>record.isActiveTOR()</td>
						<td>boolean</td>
						<td>Identifies active TOR exits on the TOR network.</td>
					</tr>
                    <tr>
						<td>record.isPublicAccessPoint()</td>
						<td>boolean</td>
						<td>Indicates if this IP is likely to be a public access point such as a coffee shop, college or library.</td>
					</tr>
                    <tr>
						<td>record.getConnectionType().getRaw()</td>
						<td>int</td>
						<td>
                            <p>
                                A numerical representation for the suspected type of connection for this IP address. It is generally reccomended you call the toString() function listed below instead of using this value, but it is available as an option.
                            </p>
                            <div class="row">
                                <div class="col-md-8 col-md-offset-2">
                                    <table class="table table-legend custom-tablelegend">
                                        <thead>
                                            <tr>
                                                <th>#</th>
                                                <th style="width:90%;">Enum Description</th>
                                            </tr>
                                        </thead>
                                        <tbody>
                                            <tr>
                                                <td>1</td>
                                                <td>Residential IP</td>
                                            </tr>
                                            <tr>
                                                <td>2</td>
                                                <td>Mobile IP</td>
                                            </tr>
                                            <tr>
                                                <td>3</td>
                                                <td>Corporate IP</td>
                                            </tr>
                                            <tr>
                                                <td>4</td>
                                                <td>Data Center IP</td>
                                            </tr>
                                            <tr>
                                                <td>5</td>
                                                <td>Educational IP</td>
                                            </tr>
                                        </tbody>
                                    </table>
                                </div>
                            </div>
                        </td>
					</tr>
                    <tr>
						<td>record.getConnectionType.toString()</td>
						<td>int</td>
						<td>
                            <p>
                                A string representation for the suspected type of connection for this IP address. (Residential, Mobile, Corporate, Data Center, Education or Unknown)
                            </p>
                        </td>
					</tr>
                    <tr>
						<td>record.getAbuseVelocity().getRaw()</td>
						<td>int</td>
						<td>
                            <p>
                                How frequently the IP address is engaging in abuse across the IPQS threat network. Can be used in combination with the Fraud Score to identify bad behavior. It is generally reccomended you call the toString() function listed below instead of using this value, but it is available as an option.
                            </p>
                            <div class="row">
                                <div class="col-md-8 col-md-offset-2">
                                    <table class="table table-legend custom-tablelegend">
                                        <thead>
                                            <tr>
                                                <th>#</th>
                                                <th style="width:90%;">Enum Description</th>
                                            </tr>
                                        </thead>
                                        <tbody>
                                            <tr>
                                                <td>1</td>
                                                <td>Low Recent Abuse IP</td>
                                            </tr>
                                            <tr>
                                                <td>2</td>
                                                <td>Medium Recent Abuse IP</td>
                                            </tr>
                                            <tr>
                                                <td>3</td>
                                                <td>High Recent Abuse IP</td>
                                            </tr>
                                        </tbody>
                                    </table>
                                </div>
                            </div>
                        </td>
                    </tr>
                    <tr>
						<td>record.getAbuseVelocity().toString()</td>
						<td>int</td>
						<td>
                            <p>
                                How frequently the IP address is engaging in abuse across the IPQS threat network. Values can be "high", "medium", "low", or "none".
                            </p>
                        </td>
                    </tr>
                    <tr>
						<td>record.getCountry()</td>
						<td>String</td>
						<td>
                            <p>
                                Two character country code of IP address or "N/A" if unknown.
                            </p>
                        </td>
                    </tr>
                    <tr>
						<td>record.getCity()</td>
						<td>String</td>
						<td>
                            <p>
                                City of IP address if available or "N/A" if unknown.
                            </p>
                        </td>
                    </tr>
                    <tr>
						<td>record.getISP()</td>
						<td>String</td>
						<td>
                            <p>
                                ISP if one is known. Otherwise "N/A".
                            </p>
                        </td>
                    </tr>
                    <tr>
						<td>record.getOrganization()</td>
						<td>String</td>
						<td>
                            <p>
                                Organization if one is known. Can be parent company or sub company of the listed ISP. Otherwise "N/A".
                            </p>
                        </td>
                    </tr>
                    <tr>
						<td>record.getASN()</td>
						<td>int</td>
						<td>
                            <p>
                                Autonomous System Number if one is known. Zero if nonexistent.
                            </p>
                        </td>
                    </tr>
                    <tr>
						<td>record.getTimezone()</td>
						<td>String</td>
						<td>
                            <p>
                                Timezone of IP address if available or "N/A" if unknown.
                            </p>
                        </td>
                    </tr>
                    <tr>
						<td>record.getZipcode()</td>
						<td>String</td>
						<td>
                            <p>
                                Suspected Zipcode of the IP address if available or empty string if unknown.
                            </p>
                        </td>
                    </tr>
                    <tr>
						<td>record.getLatitude()</td>
						<td>float</td>
						<td>
                            <p>
                                Latitude of IP address if available or 0.00 if unknown.
                            </p>
                        </td>
                    </tr>
                    <tr>
						<td>record.getLongitude()</td>
						<td>float</td>
						<td>
                            <p>
                                Longitude of IP address if available or 0.00 if unknown.
                            </p>
                        </td>
                    </tr>
                    <tr>
						<td>record.getFraudScore().forStrictness(int)</td>
						<td>int</td>
						<td>
                            <p>
                                Returns a fraud score for this IP address based on a strictness level. This method requires an int for the "strictness level" of the query and can be 0, 1 or 2. Some databases may contain 1 entry, others all 3. It is reccomended that you use the lowest strictness for Fraud Scoring. Increasing this value will expand the tests we perform. Levels 2+ have a higher risk of false-positives.
                            </p>
                        </td>
                    </tr>
                </tbody>
            </table>
        </div>
    </div>
    <h6 class="text-bold headerHR">Usage Notes</h6>
	<div class="spacing-10"></div>
	<p>
        Each database only holds either IPv4 or IPv6 data. Therefore you may need two instances of the reader available depending on your use case.
    </p>
</div>
