package chtree.database;

import chtree.type.Paragraph;

public interface TreeIO {
	// Updated
	// 打开文件名为filename的依存树库文件并检验数据有效性，抛出异常（文件I/O，XML解析等）。
	// 如已打开库文件则抛出异常。
	public void openLibrary(String filename) throws Exception;

	// 返回当前打开的依存树库文件名（完整路径），未打开依存树库文件则返回null。
	public String getLibName();

	// 关闭依存树库文件，如未打开库文件则抛出异常。
	public void closeLibrary() throws Exception;

	// Updated
	// 将内存中的数据更新到原依存树库文件，抛出异常。
	// 保持节点属性的顺序不变，即<sent id="0" cont="...">不要变成<sent cont="..." id="0">
	public void updateLibrary() throws Exception;

	// Updated
	// 将内存中的数据更新到新的依存树库文件（保存副本、另存为），抛出异常。
	// getLibName()的返回值（即当前打开的库文件名）变为filename。
	// 保持节点属性的顺序不变，同上。
	public void updateLibraryAs(String filename) throws Exception;

	// 返回段落数组
	public Paragraph[] getParas();
}
