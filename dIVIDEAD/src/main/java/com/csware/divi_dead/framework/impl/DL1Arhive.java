package com.csware.divi_dead.framework.impl;

import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.List;

import com.csware.divi_dead.framework.Arhive;

public class DL1Arhive implements Arhive {

	class DL1ArhiveHeader {
		byte	m_bSign[]	= new byte[6];
		short	m_sReserved1;
		short	m_sEntryCount;
		int		m_iFileTableOffset;
		short	m_sReserved2;
	}

	class DL1ArhiveEntry {
		String	m_sFileName;
		int		m_iFileSize;
		int		m_iFileOffset;

		DL1ArhiveEntry() {
			m_sFileName = "";
		}
	}

	class PackedFileHeader {
		byte[]	m_baSign	= new byte[2];
		int		m_iPackedSize;
		int		m_iUnpackedSize;
	}

	private boolean					m_bHeaderOk			= false;
	private boolean					m_bFileTableOk		= false;
	private int						m_iFileTableSize;
	private RandomAccessFile		m_rafArhiveFile		= null;

	private DL1ArhiveHeader			m_dahArhiveHeader	= null;
	private List<DL1ArhiveEntry>	m_lArhiveEntries	= null;
	private DL1ArhiveEntry			m_daeCurrentEntry	= null;
	private int						m_iCurrentIndex		= -1;
	private String					m_sCurrentEntryName	= null;

	public DL1Arhive() {

	}

	@Override
	public void loadArhive(String p_FileName) {
		dispose();
		try {
			m_rafArhiveFile = new RandomAccessFile(p_FileName, "r");
		}
		catch (FileNotFoundException e) {
			throw new RuntimeException("Error opening arhive file: "
					+ e.getMessage());
		}

		loadArhiveHeader(m_rafArhiveFile);
		loadArhiveFileTable(m_rafArhiveFile);
	}

	@Override
	public FileDescriptor getArhiveFD() {
		if (!m_bHeaderOk || !m_bFileTableOk) {
			throw new RuntimeException("Arhive is not loaded!\n");
		}

		try {
			return m_rafArhiveFile.getFD();
		}
		catch (IOException eE) {
			throw new RuntimeException("Error getting arhive file descriptor\n");
		}
	}

	@Override
	public long getFileOffsetInArhive(String p_FileName, String p_FileType) {
		if (!m_bHeaderOk || !m_bFileTableOk) {
			throw new RuntimeException("Arhive is not loaded!\n");
		}

		updateCurrentEntry(p_FileName, p_FileType);

		return m_daeCurrentEntry.m_iFileOffset;
	}

	@Override
	public long getFileSizeInArhive(String p_FileName, String p_FileType) {
		if (!m_bHeaderOk || !m_bFileTableOk) {
			throw new RuntimeException("Arhive is not loaded!\n");
		}

		updateCurrentEntry(p_FileName, p_FileType);

		return m_daeCurrentEntry.m_iFileSize;
	}

	@Override
	public byte[] getFileFromArhive(String p_FileName, String p_FileType) {
		if (!m_bHeaderOk || !m_bFileTableOk) {
			throw new RuntimeException("Arhive is not loaded!\n");
		}

		byte[] baBuffer;
		byte[] baUnpackedBuffer;

		updateCurrentEntry(p_FileName, p_FileType);

		baBuffer = new byte[m_daeCurrentEntry.m_iFileSize];
		try {
			m_rafArhiveFile.seek(m_daeCurrentEntry.m_iFileOffset);
			m_rafArhiveFile.read(baBuffer, 0, m_daeCurrentEntry.m_iFileSize);
		}
		catch (IOException eE) {
			dispose();
			throw new RuntimeException("Error loading file: " + p_FileName
					+ " from arhive\n" + eE.getMessage());
		}

		baUnpackedBuffer = unpackFile(baBuffer);

		if (baUnpackedBuffer == null) {
			return baBuffer;
		}
		else {
			return baUnpackedBuffer;
		}
	}

	@Override
	public void dispose() {
		m_bHeaderOk = false;
		m_bFileTableOk = false;
		m_lArhiveEntries = null;
		m_dahArhiveHeader = null;
		m_daeCurrentEntry = null;
		m_iCurrentIndex = -1;
		m_sCurrentEntryName = null;
		m_lArhiveEntries = null;

		try {
			if (m_rafArhiveFile != null) {
				m_rafArhiveFile.close();
			}
		}
		catch (IOException eE) {
			// no problem if error occurs here
		}
	}

	private void loadArhiveHeader(RandomAccessFile p_rafArhiveFile) {
		int iIdx;
		ByteBuffer bbByteBuffer;

		try {
			if (p_rafArhiveFile == null || p_rafArhiveFile.length() <= 16) {
				throw new RuntimeException("File is too small!\n");
			}

			bbByteBuffer = ByteBuffer.allocate(16);
			bbByteBuffer.order(ByteOrder.LITTLE_ENDIAN);

			m_dahArhiveHeader = new DL1ArhiveHeader();

			p_rafArhiveFile.read(bbByteBuffer.array(), 0, 16);

			for (iIdx = 0; iIdx < 6; iIdx++) {
				m_dahArhiveHeader.m_bSign[iIdx] = bbByteBuffer.get();
			}
			m_dahArhiveHeader.m_sReserved1 = bbByteBuffer.getShort();
			m_dahArhiveHeader.m_sEntryCount = bbByteBuffer.getShort();
			m_dahArhiveHeader.m_iFileTableOffset = bbByteBuffer.getInt();
			m_dahArhiveHeader.m_sReserved2 = bbByteBuffer.getShort();
			m_iFileTableSize = m_dahArhiveHeader.m_sEntryCount * 16;

			if (m_dahArhiveHeader.m_bSign[0] == 'D'
					&& m_dahArhiveHeader.m_bSign[1] == 'L'
					&& m_dahArhiveHeader.m_bSign[2] == '1'
					&& p_rafArhiveFile.length() >= (m_dahArhiveHeader.m_iFileTableOffset + m_iFileTableSize)) {
				m_bHeaderOk = true;
			}
			else {
				throw new RuntimeException("Arhive is corrupt!\n");
			}
		}
		catch (IOException e) {
			throw new RuntimeException("Error reading arhive header: "
					+ e.getMessage());
		}
	}

	private void loadArhiveFileTable(RandomAccessFile p_rafArhiveFile) {
		int iIdx;
		int iCharIdx;
		int iCurrentOffset;
		int iOffsetInBuffer;
		DL1ArhiveEntry daeEntry;
		ByteBuffer bbByteBuffer;

		if (!m_bHeaderOk) {
			throw new RuntimeException("Arhive header is not initialized!\n");
		}

		iCurrentOffset = 16;
		iOffsetInBuffer = 0;
		m_lArhiveEntries = new ArrayList<DL1ArhiveEntry>();
		bbByteBuffer = ByteBuffer.allocate(m_iFileTableSize);
		bbByteBuffer.order(ByteOrder.LITTLE_ENDIAN);

		try {
			p_rafArhiveFile.seek(m_dahArhiveHeader.m_iFileTableOffset);
			if (p_rafArhiveFile.read(bbByteBuffer.array(), 0, m_iFileTableSize) != m_iFileTableSize) {
				throw new RuntimeException("File is too small!\n");
			}

			for (iIdx = 0; iIdx < m_dahArhiveHeader.m_sEntryCount; iIdx++) {
				daeEntry = new DL1ArhiveEntry();

				for (iCharIdx = 0; iCharIdx < 12
						&& bbByteBuffer.get(iOffsetInBuffer + iCharIdx) != 0; iCharIdx++) {

				}
				try {
					daeEntry.m_sFileName = new String(bbByteBuffer.array(),
							iOffsetInBuffer, iCharIdx, "UTF-8");
				}
				catch (UnsupportedEncodingException e) {
					throw new RuntimeException("Error decoding entry name!\n");
				}
				iOffsetInBuffer += 12;

				daeEntry.m_iFileSize = bbByteBuffer.getInt(iOffsetInBuffer);
				iOffsetInBuffer += 4;

				daeEntry.m_iFileOffset = iCurrentOffset;
				iCurrentOffset += daeEntry.m_iFileSize;

				if ((daeEntry.m_iFileOffset + daeEntry.m_iFileSize) > p_rafArhiveFile
						.length()) {
					throw new RuntimeException(
							String.format(
									"Arhive entry %06d points outside file!\nName: %s; size: %d; offset: %d\n",
									iIdx, daeEntry.m_sFileName,
									daeEntry.m_iFileSize,
									daeEntry.m_iFileOffset));
				}

				m_lArhiveEntries.add(daeEntry);
			}
			m_bFileTableOk = true;
		}
		catch (IOException e) {
			throw new RuntimeException("Error reading arhive header: "
					+ e.getMessage());
		}
	}

	private DL1ArhiveEntry getArhiveEntry(int p_iIndex) {
		if (!m_bHeaderOk || !m_bFileTableOk) {
			throw new RuntimeException("Arhive is not loaded!\n");
		}

		if (p_iIndex > m_lArhiveEntries.size() || p_iIndex < 0) {
			throw new RuntimeException("Entry index out of bounds");
		}

		return m_lArhiveEntries.get(p_iIndex);
	}

	private int findFileIndex(String p_sFileName) {
		if (!m_bHeaderOk || !m_bFileTableOk) {
			throw new RuntimeException("Arhive is not loaded!\n");
		}

		if (m_sCurrentEntryName != null) {
			if (p_sFileName.compareToIgnoreCase(m_sCurrentEntryName) == 0) {
				return m_iCurrentIndex;
			}
		}

		int iIdx;

		for (iIdx = 0; iIdx < m_dahArhiveHeader.m_sEntryCount; iIdx++) {
			if (p_sFileName
					.compareToIgnoreCase(getArhiveEntry(iIdx).m_sFileName) == 0) {
				m_sCurrentEntryName = p_sFileName;
				m_iCurrentIndex = iIdx;

				return iIdx;
			}
		}

		return -1;
	}

	private PackedFileHeader loadPackedFileHeader(ByteBuffer p_baBuffer) {
		if (p_baBuffer == null || p_baBuffer.capacity() < 10) {
			return null;
		}

		PackedFileHeader pfhHeader;

		pfhHeader = new PackedFileHeader();
		p_baBuffer.position(0);
		pfhHeader.m_baSign[0] = p_baBuffer.get();
		pfhHeader.m_baSign[1] = p_baBuffer.get();
		pfhHeader.m_iPackedSize = p_baBuffer.getInt();
		pfhHeader.m_iUnpackedSize = p_baBuffer.getInt();

		return pfhHeader;
	}

	private byte[] unpackFile(byte[] p_baCompressed) {
		if (p_baCompressed.length <= 10) {
			return null;
		}

		ByteBuffer baBuffer;
		int iDstBufPos;
		int iBytesUnpacked;
		int iOffsetInWin;
		int iIdx;
		int iReadOffsetInWin;
		byte bFlagByte;
		byte bData;
		byte bRepeatCount;
		byte[] baSlideWinBuf;
		byte[] baUnpacked;
		PackedFileHeader pfhFileHeader;

		baBuffer = ByteBuffer.wrap(p_baCompressed);
		baBuffer.order(ByteOrder.LITTLE_ENDIAN);

		pfhFileHeader = loadPackedFileHeader(baBuffer);
		if (pfhFileHeader == null) {
			return null;
		}
		if (pfhFileHeader.m_baSign[0] != 'L'
				|| pfhFileHeader.m_baSign[1] != 'Z'
				|| pfhFileHeader.m_iPackedSize > baBuffer.capacity()) {
			return null;
		}

		baUnpacked = new byte[pfhFileHeader.m_iUnpackedSize];
		baSlideWinBuf = new byte[0x1000];
		for (iIdx = 0; iIdx < baSlideWinBuf.length; iIdx++) {
			baSlideWinBuf[iIdx] = 0;
		}
		iDstBufPos = 0;
		iBytesUnpacked = 0;
		iOffsetInWin = 0x0FEE;

		do {
			bFlagByte = baBuffer.get();

			for (iIdx = 0; iIdx < 8
					&& iBytesUnpacked < pfhFileHeader.m_iUnpackedSize; iIdx++) {
				if ((bFlagByte & 1) != 0) {
					bData = baBuffer.get();
					baUnpacked[iDstBufPos] = bData;
					baSlideWinBuf[iOffsetInWin] = bData;

					iDstBufPos++;
					iBytesUnpacked++;
					iOffsetInWin = (iOffsetInWin + 1) & 0x0FFF;
				}
				else {
					iReadOffsetInWin = baBuffer.getShort();
					bRepeatCount = (byte) (((iReadOffsetInWin >>> 8) & 0x0F) + 3);
					iReadOffsetInWin = ((iReadOffsetInWin >>> 4) & 0x0F00)
							| (iReadOffsetInWin & 0x00FF);

					for (; bRepeatCount > 0; bRepeatCount--) {
						baUnpacked[iDstBufPos] = baSlideWinBuf[iReadOffsetInWin];
						baSlideWinBuf[iOffsetInWin] = baSlideWinBuf[iReadOffsetInWin];

						iDstBufPos++;
						iBytesUnpacked++;
						iOffsetInWin = (iOffsetInWin + 1) & 0x0FFF;
						iReadOffsetInWin = (iReadOffsetInWin + 1) & 0x0FFF;
					}
				}
				bFlagByte = (byte) (bFlagByte >>> 1);
			}

		} while (iBytesUnpacked < pfhFileHeader.m_iUnpackedSize
				&& baBuffer.position() < baBuffer.capacity());

		return baUnpacked;
	}

	private void updateCurrentEntry(String p_FileName, String p_FileType) {
		if (!m_bHeaderOk || !m_bFileTableOk) {
			throw new RuntimeException("Arhive is not loaded!\n");
		}

		int iIdx;
		String sFile;

		if (m_daeCurrentEntry == null) {
			if (p_FileType.compareToIgnoreCase("") == 0) {
				sFile = p_FileName;
			}
			else {
				sFile = p_FileName + "." + p_FileType;
			}
			
			iIdx = findFileIndex(sFile);
			if (iIdx < 0) {
				throw new RuntimeException("File " + sFile + " not found!");
			}

			m_daeCurrentEntry = getArhiveEntry(iIdx);
		}
		else {
			if (p_FileType.compareToIgnoreCase("") == 0) {
				sFile = p_FileName;
			}
			else {
				sFile = p_FileName + "." + p_FileType;
			}
			
			if (sFile.compareToIgnoreCase(m_daeCurrentEntry.m_sFileName) != 0) {
				iIdx = findFileIndex(sFile);
				if (iIdx < 0) {
					throw new RuntimeException("File " + sFile
							+ "not found\n");
				}

				m_daeCurrentEntry = getArhiveEntry(iIdx);
			}
		}
	}
}
