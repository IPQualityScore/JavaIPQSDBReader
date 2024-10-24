package com.ipqualityscore.JavaIPQSDBReader;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Arrays;

public class IPQSRecord {
	private FileReader fileReader;

	public boolean parse(FileReader file, byte[] raw) throws IOException {
		this.fileReader = file;

		int current_byte = 0;
		if(file.hasBinaryData()){
			processFirstByte(raw[0]);
			processSecondByte(raw[1]);

			setConnectionType(ConnectionType.create(raw[2]));
			setAbuseVelocity(AbuseVelocity.create(raw[2]));
			current_byte = 3;
		} else {
			setConnectionType(ConnectionType.create(raw[0]));
			setAbuseVelocity(AbuseVelocity.create(raw[0]));
			current_byte = 1;
		}

		setFraudScore((new FraudScore()));
		for(Column column : file.getColumns()){
			if(column == null){
				throw new IOException("Invalid or nonexistant IP address specified for lookup. (EID: 12)");
			}

			String value;
			Column c = new Column();
			c.setName(column.getName());
			c.setType(column.getType());
			switch(column.getName()){
				case "ASN":
					setASN((int) Utility.toUnsignedInt(Arrays.copyOfRange(raw, current_byte, current_byte + 4)));
					c.setRawValue(String.valueOf(getASN()));

					current_byte += 4;
					break;
				case "Latitude":
					setLatitude(Float.intBitsToFloat((int) Utility.toUnsignedInt(Arrays.copyOfRange(raw, current_byte, current_byte + 4))));
					c.setRawValue(String.valueOf(getLatitude()));

					current_byte += 4;
					break;
				case "Longitude":
					setLongitude(Float.intBitsToFloat((int) Utility.toUnsignedInt(Arrays.copyOfRange(raw, current_byte, current_byte + 4))));
					c.setRawValue(String.valueOf(getLatitude()));

					current_byte += 4;
					break;
				case "ZeroFraudScore":
					getFraudScore().setFraudScore((int) Utility.uVarInt(Arrays.copyOfRange(raw, current_byte, current_byte + 1)), 0);
					c.setRawValue(String.valueOf(getFraudScore().forStrictness(0)));

					current_byte++;
					break;
				case "OneFraudScore":
					getFraudScore().setFraudScore((int) Utility.uVarInt(Arrays.copyOfRange(raw, current_byte, current_byte + 1)), 1);
					c.setRawValue(String.valueOf(getFraudScore().forStrictness(1)));

					current_byte++;
					break;
				default:
					if(c.getType().Has(Bitmask.StringData)){
						c.setRawValue(getRangedStringValue(file, Arrays.copyOfRange(raw, current_byte, current_byte + 4)));
						current_byte += 4;
					}
			}

			switch(c.getName()){
				case "Country":
					setCountry(c.getRawValue());
					break;
				case "City":
					setCity(c.getRawValue());
					break;
				case "Region":
					setRegion(c.getRawValue());
					break;
				case "ISP":
					setISP(c.getRawValue());
					break;
				case "Organization":
					setOrganization(c.getRawValue());
					break;
				case "Timezone":
					setTimezone(c.getRawValue());
					break;
				case "Zipcode":
					setZipcode(c.getRawValue());
					break;
				case "Hostname":
					setHostname(c.getRawValue());
					break;
			}

			getColumns().add(c);
		}

		return true;
	}

	private String getRangedStringValue(FileReader file, byte[] pointer) throws IOException {
		FileChannel channel = file.openChannel();
		channel.position(Utility.toUnsignedInt(pointer));

		ByteBuffer totalBytes = ByteBuffer.allocate(1);
		channel.read(totalBytes);

		ByteBuffer raw = ByteBuffer.allocate((int) Utility.toUnsignedInt(totalBytes));
		channel.read(raw);
		channel.close();
		return new String(raw.array());
	}

	private void processFirstByte(byte b){
		Bitmask mask = Bitmask.create(b);
		if(mask.Has(Bitmask.IsProxy)){
			setProxy(true);
		}

		if(mask.Has(Bitmask.IsVPN)){
			setVPN(true);
		}

		if(mask.Has(Bitmask.IsTOR)){
			setTOR(true);
		}

		if(mask.Has(Bitmask.IsCrawler)){
			setCrawler(true);
		}

		if(mask.Has(Bitmask.IsBot)){
			setBot(true);
		}

		if(mask.Has(Bitmask.RecentAbuse)){
			setRecentAbuse(true);
		}

		if(mask.Has(Bitmask.IsBlacklisted)){
			setBlacklisted(true);
		}

		if(mask.Has(Bitmask.IsPrivate)){
			setPrivate(true);
		}
	}

	private void processSecondByte(byte b){
		Bitmask mask = Bitmask.create(b);
		if(mask.Has(Bitmask.IsMobile)){
			setMobile(true);
		}

		if(mask.Has(Bitmask.HasOpenPorts)){
			setHasOpenPorts(true);
		}

		if(mask.Has(Bitmask.IsHostingProvider)){
			setHostingProvider(true);
		}

		if(mask.Has(Bitmask.ActiveVPN)){
			setActiveVPN(true);
		}

		if(mask.Has(Bitmask.ActiveTOR)){
			setActiveTOR(true);
		}

		if(mask.Has(Bitmask.PublicAccessPoint)){
			setPublicAccessPoint(true);
		}
	}

	public String getCountryName() {
		try {
			return fileReader.ConvertCountry(Country);
		} catch(IOException e){}

		return "N/A";
	}

	public String getRawCountryName()  throws IOException {
		return fileReader.ConvertCountry(Country);
	}

	public boolean isProxy() {
		return IsProxy;
	}

	public void setProxy(boolean proxy) {
		IsProxy = proxy;
	}

	public boolean isVPN() {
		return IsVPN;
	}

	public void setVPN(boolean VPN) {
		IsVPN = VPN;
	}

	public boolean isTOR() {
		return IsTOR;
	}

	public void setTOR(boolean TOR) {
		IsTOR = TOR;
	}

	public boolean isCrawler() {
		return IsCrawler;
	}

	public void setCrawler(boolean crawler) {
		IsCrawler = crawler;
	}

	public boolean isBot() {
		return IsBot;
	}

	public void setBot(boolean bot) {
		IsBot = bot;
	}

	public boolean hasRecentAbuse() {
		return RecentAbuse;
	}

	public void setRecentAbuse(boolean recentAbuse) {
		RecentAbuse = recentAbuse;
	}

	public boolean isBlacklisted() {
		return IsBlacklisted;
	}

	public void setBlacklisted(boolean blacklisted) {
		IsBlacklisted = blacklisted;
	}

	public boolean isPrivate() {
		return IsPrivate;
	}

	public void setPrivate(boolean aPrivate) {
		IsPrivate = aPrivate;
	}

	public boolean isMobile() {
		return IsMobile;
	}

	public void setMobile(boolean mobile) {
		IsMobile = mobile;
	}

	public boolean hasOpenPorts() {
		return HasOpenPorts;
	}

	public void setHasOpenPorts(boolean hasOpenPorts) {
		HasOpenPorts = hasOpenPorts;
	}

	public boolean isHostingProvider() {
		return IsHostingProvider;
	}

	public void setHostingProvider(boolean hostingProvider) {
		IsHostingProvider = hostingProvider;
	}

	public boolean isActiveVPN() {
		return ActiveVPN;
	}

	public void setActiveVPN(boolean activeVPN) {
		ActiveVPN = activeVPN;
	}

	public boolean isActiveTOR() {
		return ActiveTOR;
	}

	public void setActiveTOR(boolean activeTOR) {
		ActiveTOR = activeTOR;
	}

	public boolean isPublicAccessPoint() {
		return PublicAccessPoint;
	}

	public void setPublicAccessPoint(boolean publicAccessPoint) {
		PublicAccessPoint = publicAccessPoint;
	}

	public ConnectionType getConnectionType() {
		return ConnectionType;
	}

	public void setConnectionType(ConnectionType connectionType) {
		ConnectionType = connectionType;
	}

	public AbuseVelocity getAbuseVelocity() {
		return AbuseVelocity;
	}

	public void setAbuseVelocity(AbuseVelocity abuseVelocity) {
		AbuseVelocity = abuseVelocity;
	}

	public String getCountry() {
		return Country;
	}

	public void setCountry(String country) {
		Country = country;
	}

	public String getCity() {
		return City;
	}

	public void setCity(String city) {
		City = city;
	}

	public String getRegion() {
		return Region;
	}

	public void setRegion(String region) {
		Region = region;
	}

	public String getISP() {
		return ISP;
	}

	public void setISP(String ISP) {
		this.ISP = ISP;
	}

	public String getOrganization() {
		return Organization;
	}

	public void setOrganization(String organization) {
		Organization = organization;
	}

	public String getTimezone() {
		return Timezone;
	}

	public void setTimezone(String timezone) {
		Timezone = timezone;
	}

	public String getZipcode() {
		return Zipcode;
	}

	public void setZipcode(String zipcode) {
		Zipcode = zipcode;
	}

	public String getHostname() {
		return Hostname;
	}

	public void setHostname(String hostname) {
		Hostname = hostname;
	}

	public Integer getASN() {
		return ASN;
	}

	public void setASN(Integer ASN) {
		this.ASN = ASN;
	}

	public Float getLatitude() {
		return Latitude;
	}

	public void setLatitude(Float latitude) {
		Latitude = latitude;
	}

	public Float getLongitude() {
		return Longitude;
	}

	public void setLongitude(Float longitude) {
		Longitude = longitude;
	}

	public FraudScore getFraudScore() {
		return FraudScore;
	}

	public void setFraudScore(FraudScore fraudScore) {
		FraudScore = fraudScore;
	}

	public ArrayList<Column> getColumns() {
		return Columns;
	}

	private boolean IsProxy = false;
	private boolean IsVPN = false;
	private boolean IsTOR = false;
	private boolean IsCrawler = false;
	private boolean IsBot = false;
	private boolean RecentAbuse = false;
	private boolean IsBlacklisted = false;
	private boolean IsPrivate = false;
	private boolean IsMobile = false;
	private boolean HasOpenPorts = false;
	private boolean IsHostingProvider = false;
	private boolean ActiveVPN = false;
	private boolean ActiveTOR = false;
	private boolean PublicAccessPoint = false;

	private ConnectionType ConnectionType;
	private AbuseVelocity AbuseVelocity;

	private String Country;
	private String City;
	private String Region;
	private String ISP;
	private String Organization;
	private String Timezone;
	private String Zipcode;
	private String Hostname;

	private Integer ASN;

	private Float Latitude;
	private Float Longitude;

	private FraudScore FraudScore;
	private ArrayList<Column> Columns = new ArrayList<Column>();
}
