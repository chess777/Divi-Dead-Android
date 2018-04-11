package com.csware.divi_dead;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory.Options;
import android.graphics.Color;
import android.graphics.Rect;

import com.csware.divi_dead.GameState;
import com.csware.divi_dead.SystemState;
import com.csware.divi_dead.framework.Graphics;
import com.csware.divi_dead.framework.Pixmap;
import com.csware.divi_dead.framework.Graphics.PixmapFormat;
import com.csware.divi_dead.framework.impl.AndroidGraphics;
import com.csware.divi_dead.framework.impl.AndroidPixmap;

public class ScriptParser {
	
	enum ImageTransferEffects {
		e_NoEffect, e_OrderedBlocks, e_HorizontalBlinds, e_HorizontalBlindsCrossing, e_VerticalBlinds,
	}
	
	private final String	m_TAG					= "ScriptParser";
	// private ByteBuffer m_Script;
	private SystemState		m_SystemState;
	private GameState		m_GameState;
	private FList			m_FList;
	private boolean			m_GraphicsEffectRunning	= false;
	
	Pixmap					m_DrawingBuffer1		= null;
	Pixmap					m_DrawingBuffer2		= null;
	Graphics				m_GfxBuffer1			= null;
	Graphics				m_GfxBuffer2			= null;
	
	Pixmap					m_BgImage				= null;
	Pixmap					m_FgImage				= null;
	Pixmap					m_Character1Image		= null;
	Pixmap					m_Character2Image		= null;
	Pixmap					m_FgOverlayImage		= null;
	
	Rect					m_DrawingWindow			= new Rect();
	boolean					m_ClearScreen			= false;
	
	public ScriptParser() {
		m_DrawingBuffer1 = new AndroidPixmap(Bitmap.createBitmap(
				Assets.Configuration.Graphics.m_TARGET_SCREEN_WIDTH,
				Assets.Configuration.Graphics.m_TARGET_SCREEN_HEIGHT,
				Config.ARGB_8888), PixmapFormat.ARGB8888);
		
		m_DrawingBuffer2 = new AndroidPixmap(Bitmap.createBitmap(
				Assets.Configuration.Graphics.m_TARGET_SCREEN_WIDTH,
				Assets.Configuration.Graphics.m_TARGET_SCREEN_HEIGHT,
				Config.ARGB_8888), PixmapFormat.ARGB8888);
		
		m_GfxBuffer1 = new AndroidGraphics(null, m_DrawingBuffer1.getBitmap());
		m_GfxBuffer2 = new AndroidGraphics(null, m_DrawingBuffer2.getBitmap());
	}
	
	public void dispose() {
		m_GfxBuffer1 = null;
		m_GfxBuffer2 = null;
		
		if (m_DrawingBuffer1 != null) {
			m_DrawingBuffer1.dispose();
			m_DrawingBuffer1 = null;
		}
		if (m_DrawingBuffer2 != null) {
			m_DrawingBuffer2.dispose();
			m_DrawingBuffer2 = null;
		}
		
		if (m_BgImage != null) {
			m_BgImage.dispose();
			m_BgImage = null;
		}
		if (m_FgImage != null) {
			m_FgImage.dispose();
			m_FgImage = null;
		}
		if (m_Character1Image != null) {
			m_Character1Image.dispose();
			m_Character1Image = null;
		}
		if (m_Character2Image != null) {
			m_Character2Image.dispose();
			m_Character2Image = null;
		}
		if (m_FgOverlayImage != null) {
			m_FgOverlayImage.dispose();
			m_FgOverlayImage = null;
		}
	}
	
	public void processCycle() {
		
	}
	
	public void loadScript(String p_Name) {
		
	}
	
	public Pixmap loadBitmap(String p_Name) {
		byte[] data;
		Options options;
		Bitmap bitmap;
		
		markCgAsSeen(p_Name);
		data = Assets.m_SgArhive.getFileFromArhive(p_Name, "BMP");
		
		options = new BitmapFactory.Options();
		options.inPreferredConfig = Bitmap.Config.ARGB_8888;
		bitmap = BitmapFactory.decodeByteArray(data, 0, data.length, options);
		
		m_DrawingWindow.right = bitmap.getWidth();
		m_DrawingWindow.top = bitmap.getHeight();
		fingBitmapOrigin(bitmap);
		
		return new AndroidPixmap(bitmap, PixmapFormat.ARGB8888);
	}
	
	public Pixmap loadMaskedBitmap(String p_Name, int p_TransparentColor) {
		byte[] data;
		Options options;
		Bitmap bitmap;
		int[] pixelData;
		int width;
		int height;
		int yIdx;
		int xIdx;
		
		markCgAsSeen(p_Name);
		data = Assets.m_SgArhive.getFileFromArhive(p_Name, "BMP");
		
		options = new BitmapFactory.Options();
		options.inPreferredConfig = Bitmap.Config.ARGB_8888;
		bitmap = BitmapFactory.decodeByteArray(data, 0, data.length, options);
		width = bitmap.getWidth();
		height = bitmap.getHeight();
		pixelData = new int[width];
		for (yIdx = 0; yIdx < height; yIdx++) {
			bitmap.getPixels(pixelData, 0, width, 0, yIdx, width, 1);
			for (xIdx = 0; xIdx < width; xIdx++) {
				if ((pixelData[xIdx] & 0x00FFFFFF) == (p_TransparentColor & 0x00FFFFFF)) {
					pixelData[xIdx] = pixelData[xIdx] & 0x00FFFFFF;
				}
			}
			bitmap.setPixels(pixelData, 0, width, 0, yIdx, width, 1);
		}
		
		m_DrawingWindow.right = bitmap.getWidth();
		m_DrawingWindow.top = bitmap.getHeight();
		fingBitmapOrigin(bitmap);
		
		return new AndroidPixmap(bitmap, PixmapFormat.ARGB8888);
	}
	
	public Pixmap loadMaskedBitmap(String p_Name) {
		byte[] data;
		Options options;
		Bitmap bitmap;
		int[] pixelData;
		int width;
		int height;
		int yIdx;
		int xIdx;
		int transparentColor;
		
		markCgAsSeen(p_Name);
		data = Assets.m_SgArhive.getFileFromArhive(p_Name, "BMP");
		
		options = new BitmapFactory.Options();
		options.inPreferredConfig = Bitmap.Config.ARGB_8888;
		bitmap = BitmapFactory.decodeByteArray(data, 0, data.length, options);
		width = bitmap.getWidth();
		height = bitmap.getHeight();
		pixelData = new int[width];
		transparentColor = bitmap.getPixel(0, 0);
		for (yIdx = 0; yIdx < height; yIdx++) {
			bitmap.getPixels(pixelData, 0, width, 0, yIdx, width, 1);
			for (xIdx = 0; xIdx < width; xIdx++) {
				if ((pixelData[xIdx] & 0x00FFFFFF) == (transparentColor & 0x00FFFFFF)) {
					pixelData[xIdx] = pixelData[xIdx] & 0x00FFFFFF;
				}
			}
			bitmap.setPixels(pixelData, 0, width, 0, yIdx, width, 1);
		}
		
		m_DrawingWindow.right = bitmap.getWidth();
		m_DrawingWindow.top = bitmap.getHeight();
		fingBitmapOrigin(bitmap);
		
		return new AndroidPixmap(bitmap, PixmapFormat.ARGB8888);
	}
	
	public Pixmap loadTransparentBitmap(String p_Name) {
		byte[] data;
		String maskName;
		Options options;
		Bitmap bitmap;
		Bitmap mask;
		int[] pixelData;
		int[] alphaData;
		int width;
		int height;
		int yIdx;
		int xIdx;
		int underlinePos;
		
		underlinePos = p_Name.indexOf('_');
		if (underlinePos >= 0) {
			maskName = p_Name.substring(0, underlinePos) + "_0";
		}
		else {
			throw new RuntimeException(m_TAG + "Error finding image mask");
		}
		
		data = Assets.m_SgArhive.getFileFromArhive(p_Name, "BMP");
		options = new BitmapFactory.Options();
		options.inPreferredConfig = Bitmap.Config.ARGB_8888;
		bitmap = BitmapFactory.decodeByteArray(data, 0, data.length, options);
		
		data = Assets.m_SgArhive.getFileFromArhive(maskName, "BMP");
		options.inPreferredConfig = Bitmap.Config.ALPHA_8;
		mask = BitmapFactory.decodeByteArray(data, 0, data.length, options);
		
		width = bitmap.getWidth();
		height = bitmap.getHeight();
		pixelData = new int[width];
		alphaData = new int[width];
		for (yIdx = 0; yIdx < height; yIdx++) {
			bitmap.getPixels(pixelData, 0, width, 0, yIdx, width, 1);
			mask.getPixels(alphaData, 0, width, 0, yIdx, width, 1);
			for (xIdx = 0; xIdx < width; xIdx++) {
				pixelData[xIdx] = (pixelData[xIdx] & 0x00FFFFFF)
						| (alphaData[xIdx] & 0xFF000000);
			}
			bitmap.setPixels(pixelData, 0, width, 0, yIdx, width, 1);
		}
		
		return new AndroidPixmap(bitmap, PixmapFormat.ARGB8888);
	}
	
	public void fingBitmapOrigin(Bitmap p_Bitmap) {
		int[][] sizes = { { 0, 0 }, { 640, 480 }, { 576, 376 }, { 512, 320 },
				{ 640, 304 }, { 0, 0 }, { -1, -1 }, };
		int[][] origins = { { 0, 0 }, { 0, 0 }, { 32, 8 }, { 64, 32 },
				{ 0, 64 }, { 0, 0 }, { -1, -1 }, };
		int idx;
		
		for (idx = 0; sizes[idx][0] != -1; idx++) {
			if (p_Bitmap.getWidth() == sizes[idx][0]
					&& p_Bitmap.getHeight() == sizes[idx][1]) {
				break;
			}
		}
		
		if (sizes[idx][0] == -1) {
			m_DrawingWindow.left = 0;
			m_DrawingWindow.bottom = 0;
		}
		else {
			m_DrawingWindow.left = origins[idx][0];
			m_DrawingWindow.bottom = origins[idx][1];
		}
	}
	
	public void markCgAsSeen(String p_Name) {
		int idx;
		
		idx = m_FList.findStringIndex(p_Name);
		if (idx >= 0) {
			m_SystemState.m_CgHit[idx] = true;
		}
	}
	
	public void ClearScreen(ImageTransferEffects p_Effect) {
		
		m_ClearScreen = true;
		TransferImage(p_Effect);
		m_ClearScreen = false;
	}
	
	public void TransferImage(ImageTransferEffects p_Effect) {
		
		switch (p_Effect) {
		
		case e_OrderedBlocks:
			break;
		
		case e_HorizontalBlinds:
			break;
		
		case e_HorizontalBlindsCrossing:
			break;
		
		case e_VerticalBlinds:
			break;
		
		default:
			TransferImage(0, 0, m_DrawingWindow.right, m_DrawingWindow.top, 0,
					0);
			break;
		
		}
	}
	
	public void TransferImage(int p_DstX, int p_DstY, int p_Width,
			int p_Height, int p_SrcX, int p_SrcY) {
		if (m_ClearScreen) {
			m_GfxBuffer1.drawRect(m_DrawingWindow.left + p_DstX,
					m_DrawingWindow.bottom + p_DstY, p_Width, p_Height,
					Color.argb(255, 0, 0, 0));
		}
		else {
			m_GfxBuffer1.drawPixmap(m_DrawingBuffer2, m_DrawingWindow.left
					+ p_DstX, m_DrawingWindow.bottom + p_DstY,
					m_DrawingWindow.left + p_SrcX, m_DrawingWindow.bottom
							+ p_SrcY, p_Width, p_Height);
		}
	}
	
	public void TransferImageDirect(int p_DstX, int p_DstY, int p_Width,
			int p_Height, int p_SrcX, int p_SrcY) {
		
		m_GfxBuffer1.drawPixmap(m_DrawingBuffer2, p_DstX, p_DstY, p_SrcX,
				p_SrcY, p_Width, p_Height);
	}
	
	private void processMainMenu() {
		m_DrawingWindow.set(0, 0, 640, 480);
		ClearScreen(ImageTransferEffects.e_NoEffect);
		m_BgImage = loadBitmap("WAKU_A1");
		m_GfxBuffer2.drawPixmap(m_BgImage, m_DrawingWindow.left, m_DrawingWindow.bottom, 0,
				0, m_DrawingWindow.right, m_DrawingWindow.top);
		m_FgImage = loadBitmap("TITLE");
		m_GfxBuffer2.drawPixmap(m_BgImage, m_DrawingWindow.left, m_DrawingWindow.bottom, 0,
				0, m_DrawingWindow.right, m_DrawingWindow.top);
		m_DrawingWindow.set(0, 0, 640, 480);
		TransferImage(ImageTransferEffects.e_HorizontalBlinds);
	}
	
	// Print text and wait input
	private void processOpCode_0x0000() {
		
	}
	
	// Insert one text menu item
	private void processOpCode_0x0001() {
		
	}
	
	// Jump to position in script
	private void processOpCode_0x0002() {
		
	}
	
	// Store value into multiple flags
	private void processOpCode_0x0003() {
		
	}
	
	// Process flag operations
	private void processOpCode_0x0004() {
		
	}
	
	// Wait for key press
	private void processOpCode_0x0005() {
		
	}
	
	// Save position in script & prepare for new text menu
	private void processOpCode_0x0006() {
		
	}
	
	// Process main menus and in-game text menu
	private void processOpCode_0x0007() {
		
	}
	
	// Restore saved position in script and set selected menu item
	private void processOpCode_0x000A() {
		
	}
	
	// Skip 2 bytes and continue (NOP?)
	private void processOpCode_0x000D() {
		
	}
	
	// Read flag Index and Value
	private void processOpCode_0x000E() {
		
	}
	
	// Read flag Index and Value
	private void processOpCode_0x000F() {
		
	}
	
	// Read flag Index and Value
	private void processOpCode_0x0010() {
		
	}
	
	// Sleep [x10 msec]
	private void processOpCode_0x0011() {
		
	}
	
	// Sleep [x10 msec], then wait for key press
	private void processOpCode_0x0012() {
		
	}
	
	// Load and display BG
	private void processOpCode_0x0013() {
		
	}
	
	// Blit CG with effect
	private void processOpCode_0x0014() {
		
	}
	
	// Load and display CG with transparent color, remove characters
	private void processOpCode_0x0016() {
		
	}
	
	// Remove image from screen with effect
	private void processOpCode_0x0017() {
		
	}
	
	// Load script
	private void processOpCode_0x0018() {
		
	}
	
	// Show credits and return to start screen
	private void processOpCode_0x0019() {
		
	}
	
	// Fade to black
	private void processOpCode_0x001E() {
		
	}
	
	// Fade to white
	private void processOpCode_0x001F() {
		
	}
	
	// Store random value into a flag
	private void processOpCode_0x0025() {
		
	}
	
	// Play BG music
	private void processOpCode_0x0026() {
		
	}
	
	// Stop BG music
	private void processOpCode_0x0028() {
		
	}
	
	// Skip cycle command
	private void processOpCode_0x0029() {
		
	}
	
	// Play wave file (voice)
	private void processOpCode_0x002B() {
		
	}
	
	// Load pic origin and size
	private void processOpCode_0x0030() {
		
	}
	
	// Finished some point in game - unused
	private void processOpCode_0x0031() {
		
	}
	
	// Set flag to false
	private void processOpCode_0x0032() {
		
	}
	
	// Set flag to true
	private void processOpCode_0x0033() {
		
	}
	
	// Play wave file (effects)
	private void processOpCode_0x0035() {
		
	}
	
	// Stop wave playback
	private void processOpCode_0x0036() {
		
	}
	
	// Prepare 2 pics for graphical menu, display first with horizontal blinds
	// effect
	private void processOpCode_0x0037() {
		
	}
	
	// Save position in script & prepare for new graphic menu
	private void processOpCode_0x0038() {
		
	}
	
	// Load graphical menu item's link in script and bounding rectangle on
	// screen
	private void processOpCode_0x0040() {
		
	}
	
	// Select from graphical menu
	private void processOpCode_0x0041() {
		
	}
	
	// Output text at specified coordinates
	private void processOpCode_0x0042() {
		
	}
	
	// Paint black and restore image
	private void processOpCode_0x0043() {
		
	}
	
	// Load picture to buffer right from screen
	private void processOpCode_0x0044() {
		
	}
	
	// Is menu on screen
	private void processOpCode_0x0045() {
		
	}
	
	// Display BG command, clear other parts
	private void processOpCode_0x0046() {
		
	}
	
	// Load and display CG command
	private void processOpCode_0x0047() {
		
	}
	
	// Load and display CG command
	private void processOpCode_0x0048() {
		
	}
	
	// Set required action to ?
	private void processOpCode_0x0049() {
		
	}
	
	// Blit picture with specified effect from bottom of screen (show clear
	// background)
	private void processOpCode_0x004A() {
		
	}
	
	// Load and show character
	private void processOpCode_0x004B() {
		
	}
	
	// Show two characters on screen
	private void processOpCode_0x004C() {
		
	}
	
	// Show a light flash at the end of game
	private void processOpCode_0x004D() {
		
	}
	
	// Slide image I_101A over main image I_101 (scroll down)
	private void processOpCode_0x004E() {
		
	}
	
	// Slide image I_101B over image I_101A (scroll up)
	private void processOpCode_0x004F() {
		
	}
	
	// Set current game time
	private void processOpCode_0x0050() {
		
	}
	
}
