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
	 * 创建索引
	 */
	public void createPdfLucene() {
		List<File> fileList = getFileList(Constants.PDF_PATH);

		// 创建索引分段组
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
				// 将pdf文件生成Lucene的索引文档。有一个默认的格式。但是pdf的文档内容只生成索引，不储存内容
				Document d = LucenePDFDocument.getDocument(pdFile);
				indexWriter.addDocument(d);
			}
			indexWriter.close();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 检索
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
	 * 目录不存在的话，创建目录
	 * 
	 * @param file
	 */
	private void creatFile(File file) {
		if (!file.exists()) {
			file.mkdir();
		}
	}

	/**
	 * 创建索引之前，先删除目录下原始文件
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
	 * 过滤目录下的文件
	 * 
	 * @param dirPath
	 *            想要获取文件的目录
	 * @return 返回文件list
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
	 * 判断文件是否是pdf文件
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
		pbl.searcher("我是");
	}
}
