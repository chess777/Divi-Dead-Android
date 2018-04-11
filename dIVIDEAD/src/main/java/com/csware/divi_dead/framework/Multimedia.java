package com.csware.divi_dead.framework;

import java.io.FileDescriptor;

public interface Multimedia {
	public Music newMusic(String p_FileName);

	public Music newMusic(FileDescriptor p_FileDesc, long p_Offset,
			long p_Length);

	public Sound newSound(String p_FileName);

	public Video newVideo();
}
