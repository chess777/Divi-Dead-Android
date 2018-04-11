package com.csware.divi_dead.framework;

import java.io.FileDescriptor;

public interface Arhive {

	public void loadArhive(String p_FileName);

	public FileDescriptor getArhiveFD();

	public long getFileOffsetInArhive(String p_FileName, String p_FileType);

	public long getFileSizeInArhive(String p_FileName, String p_FileType);

	public byte[] getFileFromArhive(String p_FileName, String p_FileType);

	public void dispose();

}
