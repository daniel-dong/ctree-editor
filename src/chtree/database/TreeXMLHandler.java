// To be solved: how to ensure all methods of an instance 
// to be provoked mutually exclusively.

package chtree.database;

import java.io.File;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import chtree.type.Paragraph;
import chtree.type.Sentence;
import chtree.type.Word;

public class TreeXMLHandler implements TreeIO {
	private String filename;
	private Paragraph[] paragraph;

	public TreeXMLHandler() {
		this.filename = "";
		this.paragraph = null;
	}

	@Override
	synchronized public void openLibrary(String filename) throws Exception {
		if (paragraph != null)
			throw (new Exception("当前已打开依存树库文件：" + this.filename));

		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db = dbf.newDocumentBuilder();
		Document doc = db.parse(new File(filename));

		NodeList paraNodeList = doc.getElementsByTagName("para");
		ArrayList<Paragraph> paraStorage = new ArrayList<Paragraph>();

		for (int i = 0; i < paraNodeList.getLength(); ++i) {
			ArrayList<Sentence> sentStorage = new ArrayList<Sentence>();
			Node paraNode = paraNodeList.item(i);
			NamedNodeMap paraMap = paraNode.getAttributes();
			int paraId = Integer.parseInt(paraMap.getNamedItem("id")
					.getNodeValue());

			NodeList sentNodeList = paraNode.getChildNodes();
			for (int j = 0; j < sentNodeList.getLength(); ++j) {
				Node sentNode = sentNodeList.item(j);

				if (!sentNode.getNodeName().equals("#text")) {
					ArrayList<Word> wordStorage = new ArrayList<Word>();
					NamedNodeMap sentMap = sentNode.getAttributes();
					int sentId = Integer.parseInt(sentMap.getNamedItem("id")
							.getNodeValue());
					String sentCont = sentMap.getNamedItem("cont")
							.getNodeValue();

					NodeList wordNodeList = sentNode.getChildNodes();
					for (int k = 0; k < wordNodeList.getLength(); ++k) {
						Node wordNode = wordNodeList.item(k);
						if (!wordNode.getNodeName().equals("#text")) {
							NamedNodeMap wordMap = wordNode.getAttributes();
							int wordId = Integer.parseInt(wordMap.getNamedItem(
									"id").getNodeValue());
							String wordCont = wordMap.getNamedItem("cont")
									.getNodeValue();
							String wordPos = wordMap.getNamedItem("pos")
									.getNodeValue();
							int wordParent = Integer.parseInt(wordMap
									.getNamedItem("parent").getNodeValue());
							String wordRelate = wordMap.getNamedItem("relate")
									.getNodeValue();
							Word word = new Word(wordId, wordCont, wordPos,
									wordParent, wordRelate);
							wordStorage.add(word);
						}
					}
					Word[] wordArray = (Word[]) wordStorage
							.toArray(new Word[0]);
					Sentence sent = new Sentence(sentId, sentCont, wordArray);
					sentStorage.add(sent);
				}
			}
			Sentence[] sentArray = (Sentence[]) sentStorage
					.toArray(new Sentence[0]);
			Paragraph para = new Paragraph(paraId, sentArray);
			paraStorage.add(para);
		}

		this.filename = filename;
		this.paragraph = (Paragraph[]) paraStorage.toArray(new Paragraph[0]);
	}

	// 返回当前打开的依存树库文件名（完整路径），未打开依存树库文件则返回空字符串
	@Override
	synchronized public String getLibName() {
		return filename;
	}

	// 关闭依存树库文件
	@Override
	synchronized public void closeLibrary() throws Exception {
		if (paragraph == null)
			throw (new Exception("当前未打开依存树库文件！"));
		filename = "";
		paragraph = null;
	}

	private static void doc2XmlFile(Document document, String filename)
			throws Exception {
		TransformerFactory tFactory = TransformerFactory.newInstance();
		Transformer transformer = tFactory.newTransformer();
		transformer.setOutputProperty(OutputKeys.ENCODING, "GB2312");

		DOMSource source = new DOMSource(document);
		StreamResult result = new StreamResult(new File(filename));
		transformer.transform(source, result);
	}

	synchronized private void writeToLib(String filename) throws Exception {
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db = dbf.newDocumentBuilder();
		org.w3c.dom.Document doc = null;
		doc = db.newDocument();
		Element root = doc.createElement("doc");
		doc.appendChild(root);

		for (int i = 0; i < paragraph.length; ++i) {
			Element paraEle = doc.createElement("para");
			root.appendChild(paraEle);
			paraEle.setAttribute("id", Integer.toString(paragraph[i].getId()));

			Sentence[] sent = paragraph[i].getSentences();
			for (int j = 0; j < sent.length; ++j) {
				Element sentEle = doc.createElement("sent");
				paraEle.appendChild(sentEle);
				sentEle.setAttribute("id", Integer.toString(sent[j].getId()));
				sentEle.setAttribute("cont", sent[j].getCont());

				Word[] word = sent[j].getWords();
				for (int k = 0; k < word.length; ++k) {
					Element wordEle = doc.createElement("word");
					sentEle.appendChild(wordEle);
					wordEle.setAttribute("id",
							Integer.toString(word[k].getId()));
					wordEle.setAttribute("cont", word[k].getCont());
					wordEle.setAttribute("parent",
							Integer.toString(word[k].getParent()));
					wordEle.setAttribute("relate", word[k].getRelateString());
					wordEle.setAttribute("pos", word[k].getPosString());
				}
			}
		}
		doc2XmlFile(doc, filename);
	}

	// 将内存中的数据更新到依存树库文件
	@Override
	synchronized public void updateLibrary() throws Exception {
		if (paragraph == null)
			throw (new Exception("当前未打开依存树库文件！"));
		writeToLib(getLibName());
	}

	@Override
	synchronized public void updateLibraryAs(String filename) throws Exception {
		if (paragraph == null)
			throw (new Exception("当前未打开依存树库文件！"));
		writeToLib(filename);
		this.filename = filename;
	}

	// 返回段落数组
	@Override
	synchronized public Paragraph[] getParas() {
		return paragraph;
	}
}
