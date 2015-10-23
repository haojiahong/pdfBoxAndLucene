package com.haojiahong.extract;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;
import org.apache.pdfbox.lucene.LucenePDFDocument;

public class PdfBoxLucene {
	/**
	 * ��������
	 */
	public void createPdfLucene() {
		List<File> fileList = getFileList(Constants.PDF_PATH);

		// ���������ֶ���
		IndexWriter indexWriter = null;
		try {
			File indexDir = new File(Constants.INDEX_STORE_PATH);
			creatFile(indexDir);
			delAllFile(indexDir);
			Directory dir = FSDirectory.open(indexDir);
			Analyzer anal = new StandardAnalyzer(Version.LUCENE_36);
			IndexWriterConfig iwc = new IndexWriterConfig(Version.LUCENE_36, anal);
			indexWriter = new IndexWriter(dir, iwc);
			for (File file : fileList) {
				File pdFile = new File(file.getPath());
				// ��pdf�ļ�����Lucene�������ĵ�����һ��Ĭ�ϵĸ�ʽ������pdf���ĵ�����ֻ��������������������
				Document d = LucenePDFDocument.getDocument(pdFile);
				indexWriter.addDocument(d);
			}
			indexWriter.close();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * ����
	 */
	public void searcher(String searchName) {
		IndexSearcher searcher = null;
		File file = new File(Constants.INDEX_STORE_PATH);
		if (!file.exists()) {
			return;
		}
		try {
			Directory dir = FSDirectory.open(new File(Constants.INDEX_STORE_PATH));
			IndexReader reader = IndexReader.open(dir);
			searcher = new IndexSearcher(reader);
			Analyzer anal = new StandardAnalyzer(Version.LUCENE_36);

			QueryParser parser = new QueryParser(Version.LUCENE_36, "contents", anal);
			Query query = parser.parse(searchName);

			ScoreDoc[] hits = searcher.search(query, null, 1000).scoreDocs;
			for (int i = 0; i < hits.length; i++) {
				Document hitDoc = searcher.doc(hits[i].doc);
				System.out.println("____________________________");
				// System.out.println(hitDoc.get("uid"));
				System.out.println(hitDoc.get("summary"));
				System.out.println(hitDoc.get("path"));
				System.out.println("____________________________");
			}
			reader.close();
			dir.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Ŀ¼�����ڵĻ�������Ŀ¼
	 * 
	 * @param file
	 */
	private void creatFile(File file) {
		if (!file.exists()) {
			file.mkdir();
		}
	}

	/**
	 * ��������֮ǰ����ɾ��Ŀ¼��ԭʼ�ļ�
	 * 
	 * @param file
	 * @return
	 */
	private boolean delAllFile(File file) {
		boolean flag = false;
		if (file != null) {
			File[] tempList = file.listFiles();
			File temp = null;
			for (int i = 0; i < tempList.length; i++) {
				temp = tempList[i];
				if (temp.isFile()) {
					temp.delete();
				}
			}
		}
		return flag;
	}

	/**
	 * ����Ŀ¼�µ��ļ�
	 * 
	 * @param dirPath
	 *            ��Ҫ��ȡ�ļ���Ŀ¼
	 * @return �����ļ�list
	 */
	public List<File> getFileList(String dirPath) {
		File[] files = new File(dirPath).listFiles();
		List<File> fileList = new ArrayList<File>();
		for (File file : files) {
			if (isPdfFile(file.getName())) {
				fileList.add(file);
			}
		}
		return fileList;
	}

	/**
	 * �ж��ļ��Ƿ���pdf�ļ�
	 * 
	 * @param fileName
	 * @return
	 */
	public boolean isPdfFile(String fileName) {
		if (fileName.lastIndexOf(".pdf") > 0) {
			return true;
		}
		return false;
	}

	public static void main(String[] args) {
		PdfBoxLucene pbl = new PdfBoxLucene();
		pbl.createPdfLucene();
		pbl.searcher("����");
	}
}
