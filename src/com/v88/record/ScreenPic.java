package com.v88.record;

import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import javax.imageio.ImageIO;
import javax.imageio.ImageReadParam;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import javax.print.Doc;

public class ScreenPic {
	private String path;
	private DesktopUtils desktopUtils = new DesktopUtils();

	/**
	 * 获取目录下所有文件(按时间排序)
	 * 
	 * @param path
	 * @return
	 */
	public static List<File> getFileSort(String path) {

		List<File> list = getFiles(path, new ArrayList<File>());

		if (list != null && list.size() > 0) {

			Collections.sort(list, new Comparator<File>() {
				public int compare(File file, File newFile) {
					if (file.lastModified() > newFile.lastModified()) {
						return 1;
					} else if (file.lastModified() == newFile.lastModified()) {
						return 0;
					} else {
						return -1;
					}

				}
			});

		}

		return list;
	}

	/**
	 * 
	 * 获取目录下所有文件
	 * 
	 * @param realpath
	 * @param files
	 * @return
	 */
	public static List<File> getFiles(String realpath, List<File> files) {

		File realFile = new File(realpath);
		if (realFile.isDirectory()) {
			File[] subfiles = realFile.listFiles();
			for (File file : subfiles) {
				if (file.isDirectory()) {
					getFiles(file.getAbsolutePath(), files);
				} else {
					files.add(file);
				}
			}
		}
		return files;
	}

	public void captureScreen(String fileName, String folder) throws Exception {
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		Rectangle screenRectangle = new Rectangle(screenSize);
		Robot robot = new Robot();
		BufferedImage image = robot.createScreenCapture(screenRectangle);
		// 截图保存的路径
		File screenFile = new File(fileName);
		// 如果路径不存在,则创建
		if (!screenFile.getParentFile().exists()) {
			screenFile.getParentFile().mkdirs();
		}
		// 判断文件是否存在，不存在就创建文件
		if (!screenFile.exists() && !screenFile.isDirectory()) {
			screenFile.mkdir();
		}

		File f = new File(screenFile, folder);
		ImageIO.write(image, "png", f);
		// 自动打开
		/*
		 * if (Desktop.isDesktopSupported() &&
		 * Desktop.getDesktop().isSupported(Desktop.Action.OPEN))
		 * Desktop.getDesktop().open(f);
		 */
	}

	public void cutImage(String filePath, int x, int y, int w, int h) throws Exception {
		// 首先通过ImageIo中的方法，创建一个Image + InputStream到内存
		ImageInputStream iis = ImageIO.createImageInputStream(new FileInputStream(filePath));
		// 再按照指定格式构造一个Reader（Reader不能new的）
		Iterator it = ImageIO.getImageReadersByFormatName("png");
		ImageReader imagereader = (ImageReader) it.next();
		// 再通过ImageReader绑定 InputStream
		imagereader.setInput(iis);

		// 设置感兴趣的源区域。
		ImageReadParam par = imagereader.getDefaultReadParam();
		par.setSourceRegion(new Rectangle(x, y, w, h));
		// 从 reader得到BufferImage
		BufferedImage bi = imagereader.read(0, par);

		// 将BuffeerImage写出通过ImageIO

		ImageIO.write(bi, "png", new File(filePath));

	}

	public String readTextFile(File file) throws IOException {
		BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file), "UTF-8"));
		String lineTxt = null;
		StringBuffer buffer = new StringBuffer();
		while ((lineTxt = br.readLine()) != null) {
			buffer.append(lineTxt + "\r\n");
		}
		br.close();
		return buffer.toString();
	}

	public void start(String path) {
		this.path = path;
		BufferedImage configImage = null;
		// BufferedImage configImage1 = null;
		try {
			configImage = ImageIO.read(new File(path + "\\png\\config.png"));
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		int configImageHeight = configImage.getHeight();
		int configImageWidth = configImage.getWidth();

		List<File> list = getFileSort(path + "\\txt");
		for (File file : list) {
			String fileName = file.getName().substring(0, file.getName().indexOf(".txt"));

			try {
				String[] contents = readTextFile(file).split("\\*\\*mmmmmm\\*\\*");
				String picStr = contents[1].replaceAll("\r\n", "");
				String benRenQQ = picStr.substring(picStr.indexOf("本人qq："), picStr.indexOf("级别")).split("：")[1];

				ImageFind findConfig = new ImageFind(path + "\\png\\config.png");
				Location locationConfig = findConfig.findImage();

				ImageFind findBottom1 = new ImageFind(path + "\\png\\bottom1.png");
				Location locationBottom1 = findBottom1.findImage();

				ImageFind findBottom2 = new ImageFind(path + "\\png\\bottom2.png");
				Location locationBottom2 = findBottom2.findImage();
				if (locationConfig != null && (locationBottom1 != null || locationBottom2 != null)) {
					desktopUtils.mouseMove(locationConfig.getX() - 40, locationConfig.getY() + configImageHeight / 2);
					desktopUtils.mouseLeftPress();// 点击左键
					// desktopUtils.allSelect();
					desktopUtils.setSysClipboardText(benRenQQ);// 往粘贴板添加数据
					desktopUtils.pasteStr();

					desktopUtils.mouseMove(locationConfig.getX() + configImageWidth / 2,
							locationConfig.getY() + configImageHeight / 2);
					desktopUtils.mouseLeftPress();// 点击左键搜索

					ImageFind findResult0;
					Location locationResult0;
					ImageFind findResult;
					Location locationResult;
					ImageFind findFind;
					Location locationFind;
					int xunhuan = 0;
					try {
						Thread.currentThread().sleep(300);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					boolean isOnClick = false;// 搜索完点一次左键，避免多次点击搜索内容消失
					while (true) {
						findResult0 = new ImageFind(path + "\\png\\result0.png");
						locationResult0 = findResult0.findImage();
						findFind = new ImageFind(path + "\\png\\find.png");
						locationFind = findFind.findImage();
						if (locationResult0 != null && locationFind == null) {
							try {
								Thread.currentThread().sleep(500);
							} catch (InterruptedException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							findResult = new ImageFind(path + "\\png\\result.png");
							locationResult = findResult.findImage();
							if (locationResult != null) {// 搜索到了截大图
								Location locationBottom = locationBottom1 != null ? locationBottom1 : locationBottom2;
								if (locationBottom.getY() - locationResult.getY() <= 80) {// 如果收到的下面显示半截
									desktopUtils.mouseMove(locationConfig.getX()-50, locationConfig.getY() + 300);
									if (!isOnClick) {
										desktopUtils.mouseLeftPress();// 点击左键
										isOnClick = true;
									}
									desktopUtils.mouseWheel(2);
								} else if (locationResult.getY() - locationResult0.getY() <= 50) {// 如果收到的上面显示半截
									desktopUtils.mouseMove(locationConfig.getX() - 50, locationConfig.getY() + 300);
									if (!isOnClick) {
										desktopUtils.mouseLeftPress();// 点击左键
										isOnClick = true;
									}
									desktopUtils.mouseWheel(-2);
								} else {
									try {
										captureScreen(path + "\\bigPng", fileName + ".png");
										cutImage(path + "\\bigPng\\" + fileName + ".png", locationResult0.getX() - 30,
												locationResult0.getY(), locationBottom.getX() - locationResult0.getX(),
												locationBottom.getY() - locationResult0.getY());
										Docx.createDocx(path, fileName, contents[0]);
										ImageFind findback = new ImageFind(path + "\\png\\back.png");
										Location locationBack = findback.findImage();
										if (locationBack != null) {
											desktopUtils.mouseMove(locationBack.getX() + 10, locationBack.getY() + 10);
											desktopUtils.mouseLeftPress();// 点击左键

										}
									} catch (Exception e1) {
										// TODO Auto-generated catch block
										e1.printStackTrace();
									}

									break;
								}

							} else {// 未显示黄色的搜索结果，需要转动鼠标
								if (xunhuan <= 200) {
									desktopUtils.mouseMove(locationConfig.getX()-50, locationConfig.getY() + 300);
									if (!isOnClick) {
										desktopUtils.mouseLeftPress();// 点击左键
										isOnClick = true;
									}
									if (xunhuan < 5) {

									} else if (xunhuan >= 20) {
										desktopUtils.mouseWheel(4);
									} else if (xunhuan >= 40) {
										desktopUtils.mouseWheel(6);
									} else {
										desktopUtils.mouseWheel(2);
									}

								}
								xunhuan++;

							}
						}
						try {
							Thread.currentThread().sleep(200);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}

				} else {
					System.out.println("未找到搜索框，请更新图片");
					break;
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

}
