package com.csware.divi_dead.framework;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public interface FileIO {
	public InputStream readAsset(String p_FileName) throws IOException;

	public InputStream readFile(String p_FileName) throws IOException;

	public OutputStream writeFile(String p_FileName) throws IOException;
	
	public String getExternalStoragePath();
}
