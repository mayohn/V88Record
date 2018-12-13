package com.v88.record;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;

import org.docx4j.XmlUtils;
import org.docx4j.dml.wordprocessingDrawing.Inline;
import org.docx4j.openpackaging.exceptions.Docx4JException;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.docx4j.openpackaging.parts.WordprocessingML.BinaryPartAbstractImage;
import org.docx4j.openpackaging.parts.WordprocessingML.MainDocumentPart;
import org.docx4j.wml.Drawing;
import org.docx4j.wml.ObjectFactory;
import org.docx4j.wml.P;
import org.docx4j.wml.R;

public class Docx {

	public static void doCreateNoHistory(String path, Record record) {
		File file = new File(path + "\\未找到记录.txt");//
		try {
			BufferedWriter noHostoryWriter = new BufferedWriter(new FileWriter(file, true));
			StringBuffer buffer = new StringBuffer();
			buffer.append("介绍人实名：" + record.getRecommendName() + "\r\n");
			buffer.append("介绍人qq：" + record.getRecommendQQ() + "\r\n");
			buffer.append("本人实名：" + record.getEntryName() + "\r\n");
			buffer.append("本人qq：" + record.getEntryQQ() + "\r\n");
			buffer.append("级别：" + record.getGrade() + "\r\n");
			buffer.append("入职日期：" + record.getTime() + "\r\n");
			buffer.append("接待人：" + record.getReceiver() + "\r\n");
			buffer.append("培训老师：" + record.getTeacher() + "\r\n");
			buffer.append("计数：" + record.getCount() + "\r\n\r\n");
			noHostoryWriter.write(buffer.toString());
			noHostoryWriter.flush();
			noHostoryWriter.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	// 分离数据
	public static void doCreateBeian(String path, Record record) {

		/* 输出数据 */
		try {
			BufferedWriter bw = new BufferedWriter(
					new OutputStreamWriter(
							new FileOutputStream(
									path + "\\txt\\" + record.getReceiver() + " " + record.getEntryName() + ".txt"),
							"UTF-8"));
			StringBuffer buffer = new StringBuffer();
			// buffer.append(record.getReceiver() + " " + record.getEntryName() + "\r\n");
			buffer.append(DesktopUtils.getSysClipboardText().replaceAll("\n", "\r\n") + "\r\n");
			buffer.append("**mmmmmm**\r\n");
			buffer.append("介绍人实名：" + record.getRecommendName() + "\r\n");
			buffer.append("介绍人qq：" + record.getRecommendQQ() + "\r\n");
			buffer.append("本人实名：" + record.getEntryName() + "\r\n");
			buffer.append("本人qq：" + record.getEntryQQ() + "\r\n");
			buffer.append("级别：" + record.getGrade() + "\r\n");
			buffer.append("入职日期：" + record.getTime() + "\r\n");
			buffer.append("接待人：" + record.getReceiver() + "\r\n");
			buffer.append("培训老师：" + record.getTeacher() + "\r\n");
			buffer.append("计数：" + record.getCount() + "\r\n");
			// buffer.append("***");
			bw.write(buffer.toString());
			// bw.newLine();
			bw.close();
		} catch (Exception e) {
			System.err.println("write errors :" + e);
		}
	}

	public static boolean deleteFile(String fileName) {
		File file = new File(fileName);
		// 如果文件路径所对应的文件存在，并且是一个文件，则直接删除
		if (file.exists() && file.isFile()) {
			if (file.delete()) {
				System.out.println("删除文件" + fileName + "成功！");
				return true;
			} else {
				System.out.println("删除文件" + fileName + "失败！");
				return false;
			}
		} else {
			System.out.println("删除文件失败：" + fileName + "不存在！");
			return false;
		}
	}

	public static void createDocx(String path, String fileName, String content) {
		WordprocessingMLPackage wordMLPackage;
		try {
			wordMLPackage = WordprocessingMLPackage.load(new java.io.File(path + "\\test.docx"));
			MainDocumentPart mdp = wordMLPackage.getMainDocumentPart();
			mdp.addStyledParagraphOfText("Title", fileName);
			String[] strs = content.split("\r\n");
			// 示例1：在标题样式中添加文本
			for (int i = 0; i < strs.length; i++) {
				mdp.addStyledParagraphOfText("Normal", strs[i]);
			}
			// 添加截图
			File file = new File(path + "\\bigPng\\" + fileName + ".png");
			byte[] bytes = convertImageToByteArray(file);
			BinaryPartAbstractImage imagePart = BinaryPartAbstractImage.createImagePart(wordMLPackage, bytes);
			int docPrId = 1;
			int cNvPrId = 2;
			Inline inline = imagePart.createImageInline("Filename hint", "Alternative text", docPrId, cNvPrId, false);
			P paragraph = addInlineImageToParagraph(inline);
			mdp.addObject(paragraph);

			String filename = path + "\\docx\\" + fileName + ".docx";
			wordMLPackage.save(new java.io.File(filename));
			System.out.println("保存" + filename);
		} catch (Docx4JException | IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * 创建一个对象工厂并用它创建一个段落和一个可运行块R. 然后将可运行块添加到段落中. 接下来创建一个图画并将其添加到可运行块R中. 最后我们将内联
	 * 对象添加到图画中并返回段落对象.
	 *
	 * @param inline
	 *            包含图片的内联对象.
	 * @return 包含图片的段落
	 */
	private static P addInlineImageToParagraph(Inline inline) {
		// 添加内联对象到一个段落中
		ObjectFactory factory = new ObjectFactory();
		P paragraph = factory.createP();
		R run = factory.createR();
		paragraph.getContent().add(run);
		Drawing drawing = factory.createDrawing();
		run.getContent().add(drawing);
		drawing.getAnchorOrInline().add(inline);
		return paragraph;
	}

	/**
	 * 将图片从文件对象转换成字节数组.
	 * 
	 * @param file
	 *            将要转换的文件
	 * @return 包含图片字节数据的字节数组
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	private static byte[] convertImageToByteArray(File file) throws FileNotFoundException, IOException {
		InputStream is = new FileInputStream(file);
		long length = file.length();
		// 不能使用long类型创建数组, 需要用int类型.
		if (length > Integer.MAX_VALUE) {
			System.out.println("File too large!!");
		}
		byte[] bytes = new byte[(int) length];
		int offset = 0;
		int numRead = 0;
		while (offset < bytes.length && (numRead = is.read(bytes, offset, bytes.length - offset)) >= 0) {
			offset += numRead;
		}
		// 确认所有的字节都没读取
		if (offset < bytes.length) {
			System.out.println("Could not completely read file " + file.getName());
		}
		is.close();
		return bytes;
	}

}
