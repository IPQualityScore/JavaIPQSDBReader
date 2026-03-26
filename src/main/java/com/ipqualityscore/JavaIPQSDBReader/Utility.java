package com.ipqualityscore.JavaIPQSDBReader;

import java.io.*;
import java.io.FileReader;
import java.net.URL;
import java.util.Scanner;
import java.util.HashMap;
import java.nio.ByteOrder;
import java.nio.ByteBuffer;

public class Utility {
	/**
	 * Takes a ByteBuffer, treating its contents as unsigned little endian int bytes and converts it to a long.
	 * @return long
	 */
	public static long uVarInt(ByteBuffer buffer) {
		buffer.order(ByteOrder.LITTLE_ENDIAN);
		long x = 0;
		long s = 0;
		for(int i = 0; i < buffer.array().length; i++) {
			int currentUByte = Byte.toUnsignedInt(buffer.get(i));
			if(currentUByte < 0x80) {
				return x | (long) currentUByte << s;
			}

			x |= currentUByte & 0x7fL << s;
			s += 7;
		}

		return 0;
	}

	/**
	 * Takes an array of unsigned little endian int bytes and converts it to a long.
	 * @return long
	 */
	public static long uVarInt(byte[] bytes) {
		long x = 0;
		long s = 0;
		for(int b : bytes) {
			if(b < 0x80) {
				return x | (long) b << s;
			}

			x |= b & 0x7fL << s;
			s += 7;
		}

		return 0;
	}

	static String COUNTRY_LIST_RAW_URL = "https://ipqualityscore.com/api/raw/country/list";
	static String COUNTRY_LIST_CACHE_PATH = "countrylist.raw";
	public static boolean UpdateCountryList() {
		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter(COUNTRY_LIST_CACHE_PATH, false));

			Scanner raw = new Scanner(new URL(COUNTRY_LIST_RAW_URL).openStream(), "UTF-8").useDelimiter("\n");
			while (raw.hasNext()) {
				writer.append(raw.next() + "\n");
			}

			writer.close();
		} catch(Exception e){
			return false;
		}

		return true;
	}

	static int MAX_COUNTRY_CACHE_AGE = 7;
	public static HashMap<String, String> GetCountryList() throws IOException {
		HashMap<String, String> result = new HashMap<String, String>();

		BufferedReader cache;
		try {
			File f = new File(COUNTRY_LIST_CACHE_PATH);
			if(!f.exists() || f.lastModified() < (System.currentTimeMillis() - (MAX_COUNTRY_CACHE_AGE * 24 * 60 * 60 * 1000))){
				if(!UpdateCountryList()){
					throw new IOException("Unable to read/write to countrylist.raw. To do country conversions this file must be available.");
				}
			}

			cache = new BufferedReader(new FileReader(COUNTRY_LIST_CACHE_PATH));
		} catch(IOException e){
			if(!UpdateCountryList()){
				throw new IOException("Unable to read/write to countrylist.raw. To do country conversions this file must be available.");
			}

			cache = new BufferedReader(new FileReader(COUNTRY_LIST_CACHE_PATH));
		}

		String line;
		while ((line = cache.readLine()) != null) {
			int p = line.indexOf(':');
			if (p >= 0) {
				String key = line.substring(0, p - 1);
				String value = line.substring(p + 2);
				result.put(key, value);
			}
		}

		if(result.isEmpty()){
			throw new IOException("Unable to read/write to countrylist.raw. To do country conversions this file must be available.");
		}

		return result;
	}

	public static long toUnsignedInt(ByteBuffer buffer) {
		return toUnsignedInt(buffer.array()); // made this way to avoid modifying the original
	}

	public static long toUnsignedInt(byte[] bytes) {
		ByteBuffer buffer = ByteBuffer.allocate(4).put(bytes);
		buffer.order(ByteOrder.LITTLE_ENDIAN);
		buffer.position(0);
		return buffer.getInt() & 0xFFFFFFFFL;
	}
	public static long toUnsignedInt64(ByteBuffer buffer) {
		return toUnsignedInt64(buffer.array()); // made this way to avoid modifying the original
	}
	public static long toUnsignedInt64(byte[] bytes) {
		ByteBuffer buffer = ByteBuffer.allocate(8).put(bytes);
		buffer.order(ByteOrder.LITTLE_ENDIAN);
		buffer.position(0);
		return buffer.getInt() & 0xFFFFFFFFL;
	}

}
