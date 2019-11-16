package com.v88.record;

import java.awt.AWTException;
import java.awt.Robot;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.InputMismatchException;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.imageio.ImageIO;
import javax.print.Doc;
import javax.swing.filechooser.FileSystemView;

public class V88Record {
	public static String path;
	public static HanyuPinyinHelper hanyuPinyinHelper;
	// private static String regStr =
	// "^[\\w\\?%&=\\-_]+\\(\\d*\\)(\\d*\\/\\d*\\/)?\\d*:\\d*(:\\d*)?";
	private static String regStr = ".+[\\(\\d+\\)|\\<.+\\>] \\d{1,2}\\:\\d{1,2}\\:\\d{1,2}";
	private static String[] strs;// �����������
	private static ArrayList<Record> recordList = new ArrayList<>();// ָ����ʦ���б�
	private static ArrayList<Record> nullList = new ArrayList<>();// û����ʦ���б�
	private static DesktopUtils desktopUtils = new DesktopUtils();
	private static BufferedImage searchImage = null;
	private static BufferedImage searchImage1 = null;
	private static int searchImageHeight;
	private static int searchImageWidth;
	private static int searchImageHeight1;
	private static int searchImageWidth1;

	// ^[\w\?%&=\-_]+\(\d*\)(\d*\/\d*\/)?\d*:\d*(:\d*)?
	public static void main(String[] args) throws AWTException {
		FileSystemView fsv = FileSystemView.getFileSystemView();
		File com = fsv.getHomeDirectory(); // ����Ƕ�ȡ����·���ķ�����
		path = com.getPath() + "\\KeFu";
		try {
			searchImage = ImageIO.read(new File(path + "\\png\\search.png"));
			searchImage1 = ImageIO.read(new File(path + "\\png\\search1.png"));
			searchImageHeight = searchImage.getHeight();
			searchImageWidth = searchImage.getWidth();
			searchImageHeight1 = searchImage1.getHeight();
			searchImageWidth1 = searchImage1.getWidth();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		int a = 0;
		while (!(a == 1 || a == 2 || a == 3)) {
			try {
				System.out.println("��ѡ�������1.��ʽ�����ݣ�2.��ʼ����,3�����ͼ");
				Scanner sc = new Scanner(System.in);
				a = sc.nextInt();
			} catch (Exception e) {
				System.out.println("������������������");

			}

		}
		if (a == 1) {
			System.out.println("��ʼ��ʽ������...");
			doWritePinyin(readText());
			doWriteDeleteSender(doDeleteSender());
			System.out.println("������ݸ�ʽ����");
		} else if (a == 2) {
			String gotoStr = "";
			boolean running = true;
			if (!getMark().equals("")) {
				System.out.println("��ȡ�ϴα���λ��:" + getMark());
				int b = 0;
				while (!(b == 1 || b == 2)) {
					try {
						System.out.println("��ѡ�������1.�����ϴΣ�2.���¿�ʼ");
						Scanner hostory = new Scanner(System.in);
						b = hostory.nextInt();
					} catch (Exception e) {
						System.out.println("������������������");
					}
				}
				if (b == 1) {// �����ϴ�
					gotoStr = getMark();
					running = false;
				} else if (b == 2) {// ������
				}
			}

			System.out.println("��ʼ����...");
			strs = doSeparateDate();
			for (int i = 0; i < strs.length; i++) {
				String[] items = strs[i].split("&");
				getRecord(items);
			}
			doWriteNoTeacher(nullList);
			System.out.println(recordList.size());
			for (int i = 0; i < recordList.size(); i++) {
				System.out.println("��ǰ���ȣ�" + recordList.get(i).getEntryName());
				if ((!recordList.get(i).getEntryName().equals(""))
						&& (recordList.get(i).getEntryName().equals(gotoStr))) {
					running = true;
				}
				if (running) {
					ImageFind find = new ImageFind(path + "\\png\\search.png");
					ImageFind find1 = new ImageFind(path + "\\png\\search1.png");
					Location location = find.findImage();
					Location location1 = find1.findImage();
					if (location != null) {
						dealDocx(location, recordList.get(i));
					} else if (location1 != null) {
						dealDocx(location1, recordList.get(i));
					} else {
						System.out.println("δ�ҵ�΢�������������ͼƬ");
						break;
					}

				}

			}
		} else if (a == 3) {
			ScreenPic pic = new ScreenPic();
			pic.start(path);
		}
	}

	public static void dealDocx(Location location, Record record) {

		desktopUtils.mouseMove(location.getX() + searchImageWidth + 20, location.getY() + searchImageHeight / 2);
		desktopUtils.mouseLeftPress();// ������
		desktopUtils.allSelect();
		desktopUtils.setSysClipboardText(record.getEntryQQ());// ��ճ�����������
		desktopUtils.pasteStr();
		String str = "";
		while (!(str.equals("n") || str.equals("t"))) {
			desktopUtils.setSysClipboardText(record.getReceiver() + " " + record.getEntryName());// ��ճ�����������
			try {
				System.out.println("�����޸��ǳ�Ȼ���ٸ���΢����Ϣ�������갴N��������T������");
				Scanner stepNext = new Scanner(System.in);
				str = stepNext.nextLine().toLowerCase();
			} catch (Exception e) {
				System.out.println("������������������");
			}
		}
		if (str.equals("n")) {
			// д���ĵ�
			Docx.doCreateBeian(path, record);
			System.out.println("�ѳɹ�������" + record.getEntryName());
		} else if (str.equals("t")) {
			Docx.doCreateNoHistory(path, record);
			System.out.println("��������" + record.getEntryName());
		}
		setMark(record.getEntryName());

	}

	// ��ȡ�ϴ�ִ��λ��
	public static String getMark() {

		/* ��ȡ���� */
		StringBuffer buffer = new StringBuffer();
		try {
			BufferedReader br = new BufferedReader(
					new InputStreamReader(new FileInputStream(new File(path + "\\��ʷ��¼.txt")), "UTF-8"));
			String lineTxt = null;

			while ((lineTxt = br.readLine()) != null) {
				buffer.append(lineTxt);

			}
			br.close();
		} catch (Exception e) {

		}

		return buffer.toString();

	}

	// ����ϴ�ִ��λ��
	public static void setMark(String index) {
		/* ������� */
		try {
			BufferedWriter bw = new BufferedWriter(
					new OutputStreamWriter(new FileOutputStream(new File(path + "\\��ʷ��¼.txt")), "UTF-8"));
			bw.write(index);
			// bw.newLine();
			bw.close();
		} catch (Exception e) {
			System.err.println("write errors :" + e);
		}

	}

	// ��װ����
	public static void getRecord(String[] strs) {
		Record record = new Record();

		for (int i = 0; i < strs.length; i++) {
			String[] values = strs[i].split(":");
			if ((strs[i].contains("jie") && strs[i].contains("shao") && strs[i].contains("ren")
					&& strs[i].contains("shi") && strs[i].contains("ming"))
					|| (strs[i].contains("tui") && strs[i].contains("jian") && strs[i].contains("ren")
							&& strs[i].contains("shi") && strs[i].contains("ming"))) {

				if (values.length > 1) {
					record.setRecommendName(values[1]);
				}

			} else if ((strs[i].contains("jie") && strs[i].contains("shao") && strs[i].contains("ren")
					&& strs[i].contains("qq"))
					|| (strs[i].contains("tui") && strs[i].contains("jian") && strs[i].contains("ren")
							&& strs[i].contains("qq"))) {
				if (values.length > 1) {
					record.setRecommendQQ(values[1]);
				}
			} else if ((strs[i].contains("ben") && strs[i].contains("ren") && strs[i].contains("shi")
					&& strs[i].contains("ming"))
					|| (strs[i].contains("xin") && strs[i].contains("hui") && strs[i].contains("yuan")
							&& strs[i].contains("shi") && strs[i].contains("ming"))) {
				if (values.length > 1) {
					record.setEntryName(values[1]);
				}
			} else if ((strs[i].contains("ben") && strs[i].contains("ren") && strs[i].contains("qq"))
					|| (strs[i].contains("xin") && strs[i].contains("hui") && strs[i].contains("yuan")
							&& strs[i].contains("qq"))) {
				if (values.length > 1) {
					record.setEntryQQ(values[1]);

				}
			} else if (strs[i].contains("ji") && strs[i].contains("bie")) {
				if (values.length > 1) {
					record.setGrade(values[1]);

				} else {
					if (strs[i].contains("huang") && strs[i].contains("zuan")) {
						record.setGrade("����");
					} else if (strs[i].contains("zhi") && strs[i].contains("zun")) {
						record.setGrade("����");
					}
				}
			} else if (strs[i].contains("ru") && strs[i].contains("zhi") && strs[i].contains("ri")
					&& strs[i].contains("qi")) {
				if (values.length > 1) {
					record.setTime(values[1]);

				}
			} else if (strs[i].contains("jie") && strs[i].contains("dai") && strs[i].contains("ren")) {
				if (values.length > 1) {
					record.setReceiver(values[1]);

				}
			} else if ((strs[i].contains("pei") && strs[i].contains("xun"))
					|| (strs[i].contains("lao") && strs[i].contains("shi"))) {
				if (values.length > 1) {
					record.setTeacher(values[1]);

				}
			} else if (strs[i].contains("ji") && strs[i].contains("shu")) {
				if (values.length > 1) {
					record.setCount(values[1]);
				}
			}

		}
		if (hanyuPinyinHelper.toHanyuPinyin(record.getTeacher()).toLowerCase().contains("yiyi")) {
			recordList.add(record);
		}
		if (record.getTeacher().equals("")) {
			nullList.add(record);
		}

	}

	// ��������
	public static String[] doSeparateDate() {
		int line = 0;
		hanyuPinyinHelper = new HanyuPinyinHelper();
		System.out.println("���ڶ�ȡ���ͷ�����ƴ��.txt��...");
		/* ��ȡ���� */
		StringBuffer buffer = new StringBuffer();
		try {
			BufferedReader br = new BufferedReader(
					new InputStreamReader(new FileInputStream(new File(path + "\\�ͷ�����ƴ��.txt")), "UTF-8"));
			String lineTxt = null;

			while ((lineTxt = br.readLine()) != null) {
				buffer.append(lineTxt);

			}
			br.close();
		} catch (Exception e) {
			System.err.println("�ͷ�����ƴ��.txt--��" + line + "�б���read errors :" + e);
		}
		String[] strs = buffer.toString().split("\\*");

		return strs;

	}

	// ��ȡ�ļ�·��
	public static String readText() {
		int line = 0;

		hanyuPinyinHelper = new HanyuPinyinHelper();
		System.out.println("��ȡ����...");
		/* ��ȡ���� */
		StringBuffer buffer = new StringBuffer();
		try {
			BufferedReader br = new BufferedReader(
					new InputStreamReader(new FileInputStream(new File(path + "\\�ͷ�����.txt")), "UTF-8"));
			String lineTxt = null;

			while ((lineTxt = br.readLine()) != null) {
				line++;
				if (!lineTxt.equals("")) {
					Pattern r = Pattern.compile(regStr);
					Matcher m = r.matcher(lineTxt);
					if (m.matches()) {
						// System.out.println(lineTxt);
					} else {
						lineTxt = lineTxt.replace(" ", "").trim().replace("��", ":");// �滻��������ð�ţ�
						String[] strArray = lineTxt.split(":");
						buffer.append(hanyuPinyinHelper.toHanyuPinyin(strArray[0]).toLowerCase());
						if (strArray.length > 1) {// ��ֹ����ð�ź����ǿյ�����
							buffer.append(":");
							buffer.append(strArray[1] + "\r\n");
						} else {
							buffer.append(":\r\n");
						}

					}

				}
			}
			br.close();
		} catch (Exception e) {
			System.err.println("�ͷ�����.txt---��" + line + "�б���read errors :" + e);
		}
		return buffer.toString();
	}

	// ����ת��ƴ��
	public static void doWritePinyin(String txt) {
		System.out.println("ת��ƴ��...");
		/* ������� */
		try {
			BufferedWriter bw = new BufferedWriter(
					new OutputStreamWriter(new FileOutputStream(new File(path + "\\�ͷ�����ƴ��.txt")), "UTF-8"));
			bw.write(txt);
			// bw.newLine();
			bw.close();
		} catch (Exception e) {
			System.err.println("write errors :" + e);
		}
	}

	// ȥ��qq������Ϣ����
	public static String doDeleteSender() {
		int line = 0;
		hanyuPinyinHelper = new HanyuPinyinHelper();
		System.out.println("��ȡ����ƴ������...");
		/* ��ȡ���� */
		StringBuffer buffer = new StringBuffer();
		try {
			BufferedReader br = new BufferedReader(
					new InputStreamReader(new FileInputStream(new File(path + "\\�ͷ�����ƴ��.txt")), "UTF-8"));
			String lineTxt = null;

			while ((lineTxt = br.readLine()) != null) {
				line++;
				if (!lineTxt.equals("")) {

					if (lineTxt.contains("ji") && (lineTxt.contains("shu"))) {
						buffer.append(lineTxt + "*\r\n");
					} else {
						buffer.append(lineTxt + "&\r\n");
					}

				}
			}
			br.close();
		} catch (Exception e) {
			System.err.println("�ͷ�����ƴ��.txt--��" + line + "�б���read errors :" + e);
		}
		// ˢѡ������ʦ�Ϳհ׵�
		String[] strs = buffer.toString().split("\\*");
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < strs.length; i++) {
			String[] items = strs[i].split("&");
			if (!((strs[i].contains("pei") && strs[i].contains("xun"))
					|| (strs[i].contains("lao") && strs[i].contains("shi")))) {
				for (int j = 0; j < items.length; j++) {
					String[] values = items[j].split(":");

					if (values[0].contains("jie") && values[0].contains("dai") && values[0].contains("ren")) {
						if (values.length > 1) {
							if (values[1].contains("����")||values[1].contains("����")) {
								sb.append(strs[i] + "&\r\npeixunlaoshi:����" + "*\r\n");
							}
						}
					}
				}
			} else {
				for (int j = 0; j < items.length; j++) {
					String[] values = items[j].split(":");

					if (values.length > 0) {
						if ((values[0].contains("pei") && values[0].contains("xun"))
								|| (values[0].contains("lao") && values[0].contains("shi"))) {
							if (values.length > 1) {
								if (values[1].contains("����")) {
									sb.append(strs[i] + "*\r\n");
								}
							} else {
								sb.append(strs[i] + "*\r\n");
							}
						}

					}

				}
			}

		}
		return sb.toString();

	}

	// д��ɾ���������ߵ���Ϣ
	public static void doWriteDeleteSender(String txt) {
		System.out.println("д��ɾ����������...");
		/* ������� */
		try {
			BufferedWriter bw = new BufferedWriter(
					new OutputStreamWriter(new FileOutputStream(new File(path + "\\�ͷ�����ƴ��.txt")), "UTF-8"));
			bw.write(txt);
			// bw.newLine();
			bw.close();
		} catch (Exception e) {
			System.err.println("write errors :" + e);
		}
	}

	// д��û����ѵ��ʦ���б�
	public static void doWriteNoTeacher(ArrayList<Record> list) {
		if (list.size() > 0) {
			System.out.println("��" + list.size() + "��û����ѵ��ʦ���Ա��浽����>'û����ѵ��ʦ.txt'");
		}

		/* ������� */
		try {
			BufferedWriter bw = new BufferedWriter(
					new OutputStreamWriter(new FileOutputStream(new File(path + "\\û����ѵ��ʦ.txt")), "UTF-8"));
			StringBuffer buffer = new StringBuffer();
			for (int i = 0; i < list.size(); i++) {
				buffer.append("������ʵ��:" + list.get(i).getRecommendName() + "\r\n");
				buffer.append("������qq:" + list.get(i).getRecommendQQ() + "\r\n");
				buffer.append("����ʵ��:" + list.get(i).getEntryName() + "\r\n");
				buffer.append("����qq:" + list.get(i).getEntryQQ() + "\r\n");
				buffer.append("����:" + list.get(i).getGrade() + "\r\n");
				buffer.append("��ְ����:" + list.get(i).getTime() + "\r\n");
				buffer.append("�Ӵ���:" + list.get(i).getReceiver() + "\r\n");
				buffer.append("��ѵ��ʦ:" + list.get(i).getTeacher() + "\r\n");
				buffer.append("����:" + list.get(i).getCount() + "\r\n");
			}
			bw.write(buffer.toString());
			bw.close();
		} catch (Exception e) {
			System.err.println("write errors :" + e);
		}
	}
}
