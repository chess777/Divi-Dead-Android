package com.csware.divi_dead;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

public class FList {
	private ArrayList<String>	m_FileNames	= null;

	public FList() {

	}

	public void loadFList(byte[] p_FList) {
		int iOffset;
		int iStrSize;

		iOffset = 0;
		m_FileNames = new ArrayList<String>();
		while (iOffset < p_FList.length) {
			if ((p_FList.length - iOffset) > 14) {
				iStrSize = 14;
			}
			else {
				iStrSize = p_FList.length - iOffset;
			}
			try {
				m_FileNames
						.add(new String(p_FList, iOffset, iStrSize, "Shift_JIS"));
			}
			catch (UnsupportedEncodingException e) {
				throw new RuntimeException("Error loading FLIST!");
			}
			iOffset += 16;
		}
	}

	public int findStringIndex(String p_Name){
		String str;
		int i;
		int slashPos;
		int spacePos;
		
		if(m_FileNames == null){
			return -1;
		}
		
		for(i = 0; i < m_FileNames.size(); i++){
			str = m_FileNames.get(i);
			slashPos = str.indexOf('/');
			spacePos = str.indexOf(' ');
			if(slashPos == 0){
				break;
			}
			
			if(spacePos > 0){
				str = str.substring(0, spacePos);
				if(str.compareToIgnoreCase(p_Name) == 0){
					return i;
				}
			}
		}
		
		return -1;
	}
}
