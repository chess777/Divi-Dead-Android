package com.csware.divi_dead.framework.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.os.Environment;
import android.preference.PreferenceManager;

import com.csware.divi_dead.framework.FileIO;

public class AndroidFileIO implements FileIO {
	Context			m_Context;
	AssetManager	m_Assets;
	String			m_ExternalStoragePath;

	public AndroidFileIO(Context p_Context) {
		this.m_Context = p_Context;
		this.m_Assets = p_Context.getAssets();
		this.m_ExternalStoragePath = Environment.getExternalStorageDirectory()
				.getAbsolutePath() + File.separator;
	}

	@Override
	public InputStream readAsset(String p_FileName) throws IOException {
		return m_Assets.open(p_FileName);
	}

	@Override
	public InputStream readFile(String p_FileName) throws IOException {
		return new FileInputStream(m_ExternalStoragePath + p_FileName);
	}

	@Override
	public OutputStream writeFile(String p_FileName) throws IOException {
		return new FileOutputStream(m_ExternalStoragePath + p_FileName);
	}

	public SharedPreferences getPreferences() {
		return PreferenceManager.getDefaultSharedPreferences(m_Context);
	}

	@Override
	public String getExternalStoragePath() {
		return m_ExternalStoragePath;
	}
}
