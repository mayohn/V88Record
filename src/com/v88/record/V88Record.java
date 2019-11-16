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
	private static String[] strs;// 分离出的数据
	private static ArrayList<Record> recordList = new ArrayList<>();// 指定老师的列表
	private static ArrayList<Record> nullList = new ArrayList<>();// 没有老师的列表
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
		File com = fsv.getHomeDirectory(); // 这便是读取桌面路径的方法了
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
				System.out.println("请选择操作：1.格式化数据，2.开始备案,3整理截图");
				Scanner sc = new Scanner(System.in);
				a = sc.nextInt();
			} catch (Exception e) {
				System.out.println("输入有误！请重新输入");

			}

		}
		if (a == 1) {
			System.out.println("开始格式化数据...");
			doWritePinyin(readText());
			doWriteDeleteSender(doDeleteSender());
			System.out.println("完成数据格式化！");
		} else if (a == 2) {
			String gotoStr = "";
			boolean running = true;
			if (!getMark().equals("")) {
				System.out.println("获取上次备案位置:" + getMark());
				int b = 0;
				while (!(b == 1 || b == 2)) {
					try {
						System.out.println("请选择操作：1.继续上次，2.重新开始");
						Scanner hostory = new Scanner(System.in);
						b = hostory.nextInt();
					} catch (Exception e) {
						System.out.println("输入有误！请重新输入");
					}
				}
				if (b == 1) {// 继续上次
					gotoStr = getMark();
					running = false;
				} else if (b == 2) {// 重新做
				}
			}

			System.out.println("开始备案...");
			strs = doSeparateDate();
			for (int i = 0; i < strs.length; i++) {
				String[] items = strs[i].split("&");
				getRecord(items);
			}
			doWriteNoTeacher(nullList);
			System.out.println(recordList.size());
			for (int i = 0; i < recordList.size(); i++) {
				System.out.println("当前进度：" + recordList.get(i).getEntryName());
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
						System.out.println("未找到微信搜索框，请更新图片");
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
		desktopUtils.mouseLeftPress();// 点击左键
		desktopUtils.allSelect();
		desktopUtils.setSysClipboardText(record.getEntryQQ());// 往粘贴板添加数据
		desktopUtils.pasteStr();
		String str = "";
		while (!(str.equals("n") || str.equals("t"))) {
			desktopUtils.setSysClipboardText(record.getReceiver() + " " + record.getEntryName());// 往粘贴板添加数据
			try {
				System.out.println("请先修改昵称然后再复制微信消息，复制完按N继续，按T跳过：");
				Scanner stepNext = new Scanner(System.in);
				str = stepNext.nextLine().toLowerCase();
			} catch (Exception e) {
				System.out.println("输入有误！请重新输入");
			}
		}
		if (str.equals("n")) {
			// 写入文档
			Docx.doCreateBeian(path, record);
			System.out.println("已成功备案：" + record.getEntryName());
		} else if (str.equals("t")) {
			Docx.doCreateNoHistory(path, record);
			System.out.println("已跳过：" + record.getEntryName());
		}
		setMark(record.getEntryName());

	}

	// 获取上次执行位置
	public static String getMark() {

		/* 读取数据 */
		StringBuffer buffer = new StringBuffer();
		try {
			BufferedReader br = new BufferedReader(
					new InputStreamReader(new FileInputStream(new File(path + "\\历史记录.txt")), "UTF-8"));
			String lineTxt = null;

			while ((lineTxt = br.readLine()) != null) {
				buffer.append(lineTxt);

			}
			br.close();
		} catch (Exception e) {

		}

		return buffer.toString();

	}

	// 标记上次执行位置
	public static void setMark(String index) {
		/* 输出数据 */
		try {
			BufferedWriter bw = new BufferedWriter(
					new OutputStreamWriter(new FileOutputStream(new File(path + "\\历史记录.txt")), "UTF-8"));
			bw.write(index);
			// bw.newLine();
			bw.close();
		} catch (Exception e) {
			System.err.println("write errors :" + e);
		}

	}

	// 封装对象
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
						record.setGrade("黄钻");
					} else if (strs[i].contains("zhi") && strs[i].contains("zun")) {
						record.setGrade("至尊");
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

	// 分离数据
	public static String[] doSeparateDate() {
		int line = 0;
		hanyuPinyinHelper = new HanyuPinyinHelper();
		System.out.println("正在读取“客服计数拼音.txt”...");
		/* 读取数据 */
		StringBuffer buffer = new StringBuffer();
		try {
			BufferedReader br = new BufferedReader(
					new InputStreamReader(new FileInputStream(new File(path + "\\客服计数拼音.txt")), "UTF-8"));
			String lineTxt = null;

			while ((lineTxt = br.readLine()) != null) {
				buffer.append(lineTxt);

			}
			br.close();
		} catch (Exception e) {
			System.err.println("客服计数拼音.txt--第" + line + "行报错read errors :" + e);
		}
		String[] strs = buffer.toString().split("\\*");

		return strs;

	}

	// 读取文件路径
	public static String readText() {
		int line = 0;

		hanyuPinyinHelper = new HanyuPinyinHelper();
		System.out.println("读取数据...");
		/* 读取数据 */
		StringBuffer buffer = new StringBuffer();
		try {
			BufferedReader br = new BufferedReader(
					new InputStreamReader(new FileInputStream(new File(path + "\\客服计数.txt")), "UTF-8"));
			String lineTxt = null;

			while ((lineTxt = br.readLine()) != null) {
				line++;
				if (!lineTxt.equals("")) {
					Pattern r = Pattern.compile(regStr);
					Matcher m = r.matcher(lineTxt);
					if (m.matches()) {
						// System.out.println(lineTxt);
					} else {
						lineTxt = lineTxt.replace(" ", "").trim().replace("：", ":");// 替换所有中文冒号：
						String[] strArray = lineTxt.split(":");
						buffer.append(hanyuPinyinHelper.toHanyuPinyin(strArray[0]).toLowerCase());
						if (strArray.length > 1) {// 防止出现冒号后面是空的现象
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
			System.err.println("客服计数.txt---第" + line + "行报错read errors :" + e);
		}
		return buffer.toString();
	}

	// 汉字转化拼音
	public static void doWritePinyin(String txt) {
		System.out.println("转换拼音...");
		/* 输出数据 */
		try {
			BufferedWriter bw = new BufferedWriter(
					new OutputStreamWriter(new FileOutputStream(new File(path + "\\客服计数拼音.txt")), "UTF-8"));
			bw.write(txt);
			// bw.newLine();
			bw.close();
		} catch (Exception e) {
			System.err.println("write errors :" + e);
		}
	}

	// 去除qq发送消息人名
	public static String doDeleteSender() {
		int line = 0;
		hanyuPinyinHelper = new HanyuPinyinHelper();
		System.out.println("读取数据拼音数据...");
		/* 读取数据 */
		StringBuffer buffer = new StringBuffer();
		try {
			BufferedReader br = new BufferedReader(
					new InputStreamReader(new FileInputStream(new File(path + "\\客服计数拼音.txt")), "UTF-8"));
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
			System.err.println("客服计数拼音.txt--第" + line + "行报错read errors :" + e);
		}
		// 刷选依依老师和空白的
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
							if (values[1].contains("阳阳")||values[1].contains("兮兮")) {
								sb.append(strs[i] + "&\r\npeixunlaoshi:依依" + "*\r\n");
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
								if (values[1].contains("依依")) {
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

	// 写入删除过发送者的信息
	public static void doWriteDeleteSender(String txt) {
		System.out.println("写入删除过发送人...");
		/* 输出数据 */
		try {
			BufferedWriter bw = new BufferedWriter(
					new OutputStreamWriter(new FileOutputStream(new File(path + "\\客服计数拼音.txt")), "UTF-8"));
			bw.write(txt);
			// bw.newLine();
			bw.close();
		} catch (Exception e) {
			System.err.println("write errors :" + e);
		}
	}

	// 写入没有培训老师的列表
	public static void doWriteNoTeacher(ArrayList<Record> list) {
		if (list.size() > 0) {
			System.out.println("有" + list.size() + "个没有培训老师的以保存到——>'没有培训老师.txt'");
		}

		/* 输出数据 */
		try {
			BufferedWriter bw = new BufferedWriter(
					new OutputStreamWriter(new FileOutputStream(new File(path + "\\没有培训老师.txt")), "UTF-8"));
			StringBuffer buffer = new StringBuffer();
			for (int i = 0; i < list.size(); i++) {
				buffer.append("介绍人实名:" + list.get(i).getRecommendName() + "\r\n");
				buffer.append("介绍人qq:" + list.get(i).getRecommendQQ() + "\r\n");
				buffer.append("本人实名:" + list.get(i).getEntryName() + "\r\n");
				buffer.append("本人qq:" + list.get(i).getEntryQQ() + "\r\n");
				buffer.append("级别:" + list.get(i).getGrade() + "\r\n");
				buffer.append("入职日期:" + list.get(i).getTime() + "\r\n");
				buffer.append("接待人:" + list.get(i).getReceiver() + "\r\n");
				buffer.append("培训老师:" + list.get(i).getTeacher() + "\r\n");
				buffer.append("计数:" + list.get(i).getCount() + "\r\n");
			}
			bw.write(buffer.toString());
			bw.close();
		} catch (Exception e) {
			System.err.println("write errors :" + e);
		}
	}
}
