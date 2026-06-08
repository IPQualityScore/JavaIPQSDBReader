package com.ipqualityscore.JavaIPQSDBReader;

import java.nio.ByteBuffer;

public class Bitmask {
	// Version
	public static final int ReaderVersion = 1;

	// Subset 1
	public static final int IPv4Map = 1;
	public static final int IPv6Map = 2;
	public static final int BlacklistData = 4;
	public static final int BinaryData = 128;

	// Subset 2
	public static final int TreeData = 4;
	public static final int StringData = 8;
	public static final int SmallIntData = 16;
	public static final int IntData = 32;
	public static final int FloatData = 64;

	// Subset 3
	public static final int IsProxy = 1;
	public static final int IsVPN = 2;
	public static final int IsTOR = 4;
	public static final int IsCrawler = 8;
	public static final int IsBot = 16;
	public static final int RecentAbuse = 32;
	public static final int IsBlacklisted = 64;
	public static final int IsPrivate = 128;

	// Subset 4
	public static final int IsMobile = 1;
	public static final int HasOpenPorts = 2;
	public static final int IsHostingProvider = 4;
	public static final int ActiveVPN = 8;
	public static final int ActiveTOR = 16;
	public static final int PublicAccessPoint = 32;
	public static final int FrequentAbuser = 64;
	public static final int TrustedApplication = 128;

	// Subset 5
	public static final int SharedIP = 1;
	public static final int SecurityScanner = 2;
	public static final int DynamicIP = 4;
	public static final int ConnectionTypeOne = 8;
	public static final int ConnectionTypeTwo = 16;
	public static final int ConnectionTypeThree = 32;
	public static final int AbuseVelocityOne = 64;
	public static final int AbuseVelocityTwo = 128;

	private int Raw;

	public static Bitmask create(ByteBuffer buffer) {
		return Bitmask.create((int) Utility.toUnsignedInt(buffer));
	}

	public static Bitmask create(int raw) {
		Bitmask bm = new Bitmask();
		bm.Set(raw);
		return bm;
	}

	public static Bitmask create(byte raw){
		return create(Byte.toUnsignedInt(raw));
	}

	public int getRaw() {
		return Raw;
	}

	public void setRaw(int raw) {
		Raw = raw;
	}

	public boolean Has(int value){
		return ((Raw & value) != 0);
	}

	public void Set(int value){
		Raw = Raw | value;
	}
}
