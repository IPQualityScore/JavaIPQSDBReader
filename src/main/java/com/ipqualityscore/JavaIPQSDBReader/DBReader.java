package com.ipqualityscore.JavaIPQSDBReader;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Path;
import java.util.Arrays;

public class DBReader {
	public static FileReader Open(Path path) throws IOException {
		FileReader reader = new FileReader();
		reader.setValid(false);

		reader.setPath(path);

		FileChannel channel = reader.openChannel();

		ByteBuffer byte0 = ByteBuffer.allocate(1);
		channel.read(byte0);

		Bitmask fb = Bitmask.create(byte0); // Byte 0
		reader.setBinaryData(fb.Has(Bitmask.BinaryData));
		if(fb.Has(Bitmask.IPv4Map)) {
			reader.setValid(true);
			reader.setIPv6(false);
		}

		if(fb.Has(Bitmask.IPv6Map)) {
			reader.setValid(true);
			reader.setIPv6(true);
		}

		if(fb.Has(Bitmask.BlacklistData)) {
			reader.setBlacklistFile(true);
		}

		if(!reader.isValid()) {
			throw new IOException("Invalid file format, invalid first byte, EID 1.");
		}

		ByteBuffer versionByte = ByteBuffer.allocate(1);
		channel.read(versionByte);
		versionByte.flip();
		byte version = versionByte.get();
		if (version != 0x01 && version != 0x02){
			throw new IOException("Invalid file version, invalid header bytes, EID 1.");
		}
		reader.setVersion(version);

		ByteBuffer hbd = ByteBuffer.allocate((reader.getVersion() == 0x01 ? 3 : 4));
		channel.read(hbd); // Byte 2 - 5 / v2: 2 - 6

		long headerBytes = Utility.uVarInt(hbd);
		if(headerBytes == 0) {
			throw new IOException("Invalid file format, invalid record bytes, EID 2.");
		}

		ByteBuffer rbd = ByteBuffer.allocate(2);
		channel.read(rbd); // Byte 5 - 7 / v2: 6 - 8

		reader.setRecordBytes(Utility.uVarInt(rbd));
		if(reader.getRecordBytes() == 0) {
			throw new IOException("Invalid file format, invalid record bytes, EID 3.");
		}

		ByteBuffer var = ByteBuffer.allocate((reader.getVersion() == 0x01 ? 4 : 8));
		channel.read(var); // Byte 7 - 10 / v2: 8 - 16

		reader.setTotalBytes((reader.getVersion() == 0x01 ? Utility.toUnsignedInt(var) : Utility.toUnsignedInt64(var)));
		if(reader.getTotalBytes() == 0) {
			throw new IOException("Invalid file format, EID 4.");
		}

		reader.setTreeStart(headerBytes);
		ByteBuffer columns = ByteBuffer.allocate((int) (headerBytes - (reader.getVersion() == 0x01 ? 11 : 16) ));
		channel.read(columns);

		int totalColumns = (int) (((headerBytes) - (reader.getVersion() == 0x01 ? 11 : 16)) / 24);
		for(int i = 0; i < totalColumns; i++) {
			byte[] descriptionRaw = Arrays.copyOfRange(columns.array(), (i * 24), ((i + 1) * 24) - 2);

			Column n = new Column();
			n.setName((new String(descriptionRaw).replaceAll("\0", "")));
			n.setType(Bitmask.create(Byte.toUnsignedInt(columns.get(((i + 1) * 24) - 1))));
			reader.getColumns().add(n);
		}

		if(totalColumns == 0) {
			throw new IOException("File does not appear to be valid, no column data found. EID: 5");
		}

		ByteBuffer binaryTreeData = ByteBuffer.allocate(1);
		channel.read(binaryTreeData);
		if(!Bitmask.create(binaryTreeData).Has(Bitmask.TreeData)) {
			throw new IOException("File does not appear to be valid, bad binary tree. EID: 6");
		}

		ByteBuffer treeLength = ByteBuffer.allocate((reader.getVersion() == 0x01 ? 4 : 8));
		channel.read(treeLength);

		reader.setTreeEnd(reader.getTreeStart() + (reader.getVersion() == 0x01 ? Utility.toUnsignedInt(treeLength) : Utility.toUnsignedInt64(treeLength)));
		if(reader.getTreeEnd() == 0) {
			throw new IOException("File does not appear to be valid, tree size is too small. EID: 7");
		}

		channel.close();

		return reader;
	}


}
