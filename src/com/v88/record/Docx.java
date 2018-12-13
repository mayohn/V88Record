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
		File file = new File(path + "\\δ�ҵ���¼.txt");//
		try {
			BufferedWriter noHostoryWriter = new BufferedWriter(new FileWriter(file, true));
			StringBuffer buffer = new StringBuffer();
			buffer.append("������ʵ����" + record.getRecommendName() + "\r\n");
			buffer.append("������qq��" + record.getRecommendQQ() + "\r\n");
			buffer.append("����ʵ����" + record.getEntryName() + "\r\n");
			buffer.append("����qq��" + record.getEntryQQ() + "\r\n");
			buffer.append("����" + record.getGrade() + "\r\n");
			buffer.append("��ְ���ڣ�" + record.getTime() + "\r\n");
			buffer.append("�Ӵ��ˣ�" + record.getReceiver() + "\r\n");
			buffer.append("��ѵ��ʦ��" + record.getTeacher() + "\r\n");
			buffer.append("������" + record.getCount() + "\r\n\r\n");
			noHostoryWriter.write(buffer.toString());
			noHostoryWriter.flush();
			noHostoryWriter.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	// ��������
	public static void doCreateBeian(String path, Record record) {

		/* ������� */
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
			buffer.append("������ʵ����" + record.getRecommendName() + "\r\n");
			buffer.append("������qq��" + record.getRecommendQQ() + "\r\n");
			buffer.append("����ʵ����" + record.getEntryName() + "\r\n");
			buffer.append("����qq��" + record.getEntryQQ() + "\r\n");
			buffer.append("����" + record.getGrade() + "\r\n");
			buffer.append("��ְ���ڣ�" + record.getTime() + "\r\n");
			buffer.append("�Ӵ��ˣ�" + record.getReceiver() + "\r\n");
			buffer.append("��ѵ��ʦ��" + record.getTeacher() + "\r\n");
			buffer.append("������" + record.getCount() + "\r\n");
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
		// ����ļ�·������Ӧ���ļ����ڣ�������һ���ļ�����ֱ��ɾ��
		if (file.exists() && file.isFile()) {
			if (file.delete()) {
				System.out.println("ɾ���ļ�" + fileName + "�ɹ���");
				return true;
			} else {
				System.out.println("ɾ���ļ�" + fileName + "ʧ�ܣ�");
				return false;
			}
		} else {
			System.out.println("ɾ���ļ�ʧ�ܣ�" + fileName + "�����ڣ�");
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
			// ʾ��1���ڱ�����ʽ������ı�
			for (int i = 0; i < strs.length; i++) {
				mdp.addStyledParagraphOfText("Normal", strs[i]);
			}
			// ��ӽ�ͼ
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
			System.out.println("����" + filename);
		} catch (Docx4JException | IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * ����һ�����󹤳�����������һ�������һ�������п�R. Ȼ�󽫿����п���ӵ�������. ����������һ��ͼ����������ӵ������п�R��. ������ǽ�����
	 * ������ӵ�ͼ���в����ض������.
	 *
	 * @param inline
	 *            ����ͼƬ����������.
	 * @return ����ͼƬ�Ķ���
	 */
	private static P addInlineImageToParagraph(Inline inline) {
		// �����������һ��������
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
	 * ��ͼƬ���ļ�����ת�����ֽ�����.
	 * 
	 * @param file
	 *            ��Ҫת�����ļ�
	 * @return ����ͼƬ�ֽ����ݵ��ֽ�����
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	private static byte[] convertImageToByteArray(File file) throws FileNotFoundException, IOException {
		InputStream is = new FileInputStream(file);
		long length = file.length();
		// ����ʹ��long���ʹ�������, ��Ҫ��int����.
		if (length > Integer.MAX_VALUE) {
			System.out.println("File too large!!");
		}
		byte[] bytes = new byte[(int) length];
		int offset = 0;
		int numRead = 0;
		while (offset < bytes.length && (numRead = is.read(bytes, offset, bytes.length - offset)) >= 0) {
			offset += numRead;
		}
		// ȷ�����е��ֽڶ�û��ȡ
		if (offset < bytes.length) {
			System.out.println("Could not completely read file " + file.getName());
		}
		is.close();
		return bytes;
	}

}
