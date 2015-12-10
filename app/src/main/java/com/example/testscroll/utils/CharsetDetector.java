package com.example.testscroll.utils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;

import info.monitorenter.cpdetector.io.JChardetFacade;

public class CharsetDetector {
	
	/**
	 * 检测当前文件的编码方式
	 */
	public static Charset detect(InputStream in) {
		JChardetFacade detector = JChardetFacade.getInstance();
		Charset charset = null;
		try {
			in.mark(100);
			charset = detector.detectCodepage(in, 100);
			in.reset();
		} catch (IllegalArgumentException | IOException e) {
			e.printStackTrace();
		}
		return charset;
	}
	
}
