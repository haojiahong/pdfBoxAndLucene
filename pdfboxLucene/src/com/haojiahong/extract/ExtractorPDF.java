package com.haojiahong.extract;

import java.io.IOException;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.util.PDFTextStripper;

public class ExtractorPDF {
	/**
	 * 抓取pdf中的文本内容
	 * @param file
	 * @return
	 */
	public static String getText(String file) {
		String s = "";
		String pdffile = file;
		PDDocument pdfdoc = null;
		try {
			pdfdoc = PDDocument.load(pdffile);
			PDFTextStripper stripper = new PDFTextStripper();
			s = stripper.getText(pdfdoc);

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (pdfdoc != null) {
					pdfdoc.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return s;
	}

	public static void main(String[] args) {
		try {
			String sc = getText("F:/1111aaaaaaaaaaaaaaimportant/aaa.pdf");
			System.out.print(sc);
			// toTextFile("D:/workspace/testsearch2/htmls/xxxx.pdf","D:/workspace/testsearch2/htmls/xxxx.txt");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
