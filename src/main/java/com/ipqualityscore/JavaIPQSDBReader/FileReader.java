package com.ipqualityscore.JavaIPQSDBReader;

import java.io.IOException;
import java.net.Inet6Address;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FileReader {
	public IPQSRecord Fetch(String ip) throws IOException {
		IPQSRecord record = new IPQSRecord();

		if(!isValidIP(ip)){
			throw new IOException("Invalid IP address specified for lookup.");
		}

		int position = 0;
		long[] previous = new long[128];
		long file_position = getTreeStart() + (this.getVersion() == 0x01 ? 5 : 9);
		StringBuilder literal = new StringBuilder(convertIPToBinaryLiteral(ip));

		FileChannel channel = openChannel();
		for(int l = 0; l < 257; l++){
			previous[position] = file_position;
			if(literal.length() <= position){
				throw new IOException("Invalid or nonexistent IP address specified for lookup. (EID: 8)");
			}

			channel.position(
				literal.charAt(position) == '0'
					? file_position
					: file_position + (this.getVersion() == 0x01 ? 4 : 8)
			);

			ByteBuffer read = ByteBuffer.allocate(this.getVersion() == 0x01 ? 4 : 8);
			channel.read(read);
			file_position = this.getVersion() == 0x01 ? Utility.toUnsignedInt(read) : Utility.toUnsignedInt64(read) ;

			if(!BlacklistFile) {
				if(file_position == 0){
					for(int i = 0; i <= position; i++){
						if(literal.charAt(position - i) == '1'){
							literal.setCharAt(position - i, '0');

							for(int n = (position - i + 1);n < literal.length();n++){
								literal.setCharAt(n, '1');
							}

							position = position - i;
							file_position = previous[position];
							break;
						}
					}

					continue;
				}
			}

			if(file_position < getTreeEnd()){
				if(file_position == 0){
					break;
				}

				position++;
				continue;
			}

			// In theory we're at a record.
			ByteBuffer raw = ByteBuffer.allocate((int) getRecordBytes());

			channel.position(file_position);
			channel.read(raw);

			if(record.parse(this, raw.array())) {
				channel.close();
				return record;
			}

			channel.close();
			throw new IOException("Invalid or nonexistent IP address specified for lookup. (EID: 12)");
		}

		channel.close();
		throw new IOException("Invalid or nonexistent IP address specified for lookup. (EID: 12)");
	}

	private String convertIPToBinaryLiteral(String ip) throws UnknownHostException {
		String result = "";
		if(ip.contains(":")){
			Inet6Address ipv6Address = (Inet6Address) Inet6Address.getByName(ip);
			for (String b : ipv6Address.getHostAddress().split("\\:")){
				result = result + String.format("%16s", Integer.toBinaryString(Integer.parseInt(b, 16))).replace(" ", "0");
			}
		} else {
			for (String b : ip.split("\\.")){
				result = result + String.format("%8s", Integer.toBinaryString(Integer.parseInt(b))).replace(" ", "0");
			}
		}

		return result;
	}

	private static final String IPV4_PATTERN = "^(([0-9]|[1-9][0-9]|1[0-9][0-9]|2[0-4][0-9]|25[0-5])(\\.(?!$)|$)){4}$";
	private static final Pattern ValidIPv4 = Pattern.compile(IPV4_PATTERN);

	private static final String IPV6_PATTERN_GENERIC = "([0-9a-f]{1,4}:){7}([0-9a-f]){1,4}";
	private static final String IPV6_PATTERN_SHORTENED = "^((?:[0-9A-Fa-f]{1,4}(?::[0-9A-Fa-f]{1,4})*)?)::((?:[0-9A-Fa-f]{1,4}(?::[0-9A-Fa-f]{1,4})*)?)$";
	private static final Pattern ValidIPv6Generic = Pattern.compile(IPV6_PATTERN_GENERIC);
	private static final Pattern ValidIPv6Shortened = Pattern.compile(IPV6_PATTERN_SHORTENED);

	private boolean isValidIP(String ip) throws IOException {
		if(isIPv6() == true) {
			if (ip.contains(".")) {
				throw new IOException("Attempted to look up IPv4 using IPv6 database file. Aborting.");
			}

			if(ValidIPv6Generic.matcher(ip).matches() || ValidIPv6Shortened.matcher(ip).matches()){
				return true;
			}
		} else {
			if(ip.contains(":")){
				throw new IOException("Attempted to look up IPv6 using IPv4 database file. Aborting.");
			}

			Matcher matcher = ValidIPv4.matcher(ip);
			return matcher.matches();
		}

		if(isIPv6() == false) {
			if (!IPRangeChecker.isValidRange("0.0.0.0", "255.0.0.0", ip)) {
				throw new IOException("Attempted to look up ip in 0.0.0.0/8 range. Aborting.");
			}
		}

		return false;
	}

	private Path path;
	private long TotalBytes;
	private long RecordBytes;
	private long TreeStart;
	private long TreeEnd;
	private boolean IPv6;
	private boolean Valid;
	private boolean BinaryData;
	private boolean BlacklistFile;
	private byte Version;
	private ArrayList<Column> Columns = new ArrayList<Column>();

	public FileChannel openChannel() throws IOException {
		return FileChannel.open(path, StandardOpenOption.READ);
	}

	public void setVersion(byte version) {
		Version = version;
	}
	public long getVersion() {
		return Version;
	}

	public void setPath(Path path) {
		this.path = path;
	}

	public long getTotalBytes() {
		return TotalBytes;
	}

	public void setTotalBytes(long totalBytes) {
		TotalBytes = totalBytes;
	}

	public long getRecordBytes() {
		return RecordBytes;
	}

	public void setRecordBytes(long recordBytes) {
		RecordBytes = recordBytes;
	}

	public long getTreeStart() {
		return TreeStart;
	}

	public void setTreeStart(long treeStart) {
		TreeStart = treeStart;
	}

	public long getTreeEnd() {
		return TreeEnd;
	}

	public void setTreeEnd(long treeEnd) {
		TreeEnd = treeEnd;
	}

	public void setBlacklistFile(boolean blacklistFile){ BlacklistFile = blacklistFile; }

	public boolean getBlacklistFile(){ return BlacklistFile; }

	public boolean isIPv6() {
		return IPv6;
	}

	public void setIPv6(boolean IPv6) {
		this.IPv6 = IPv6;
	}

	public boolean isValid() {
		return Valid;
	}

	public void setValid(boolean valid) {
		Valid = valid;
	}

	public boolean hasBinaryData() {
		return BinaryData;
	}

	public void setBinaryData(boolean binaryData) {
		BinaryData = binaryData;
	}

	public ArrayList<Column> getColumns() {
		return Columns;
	}

	public void setColumns(ArrayList<Column> columns) {
		Columns = columns;
	}

	private HashMap<String, String> countrylist;
	public String ConvertCountry(String cc) throws IOException {
		if(countrylist == null){
			countrylist = Utility.GetCountryList();
		}

		return countrylist.get(cc);
	}
}
