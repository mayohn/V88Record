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
	 * ��ȡĿ¼�������ļ�(��ʱ������)
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
	 * ��ȡĿ¼�������ļ�
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
		// ��ͼ�����·��
		File screenFile = new File(fileName);
		// ���·��������,�򴴽�
		if (!screenFile.getParentFile().exists()) {
			screenFile.getParentFile().mkdirs();
		}
		// �ж��ļ��Ƿ���ڣ������ھʹ����ļ�
		if (!screenFile.exists() && !screenFile.isDirectory()) {
			screenFile.mkdir();
		}

		File f = new File(screenFile, folder);
		ImageIO.write(image, "png", f);
		// �Զ���
		/*
		 * if (Desktop.isDesktopSupported() &&
		 * Desktop.getDesktop().isSupported(Desktop.Action.OPEN))
		 * Desktop.getDesktop().open(f);
		 */
	}

	public void cutImage(String filePath, int x, int y, int w, int h) throws Exception {
		// ����ͨ��ImageIo�еķ���������һ��Image + InputStream���ڴ�
		ImageInputStream iis = ImageIO.createImageInputStream(new FileInputStream(filePath));
		// �ٰ���ָ����ʽ����һ��Reader��Reader����new�ģ�
		Iterator it = ImageIO.getImageReadersByFormatName("png");
		ImageReader imagereader = (ImageReader) it.next();
		// ��ͨ��ImageReader�� InputStream
		imagereader.setInput(iis);

		// ���ø���Ȥ��Դ����
		ImageReadParam par = imagereader.getDefaultReadParam();
		par.setSourceRegion(new Rectangle(x, y, w, h));
		// �� reader�õ�BufferImage
		BufferedImage bi = imagereader.read(0, par);

		// ��BuffeerImageд��ͨ��ImageIO

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
		BufferedImage contentImage = null;
		BufferedImage configImage = null;
		// BufferedImage configImage1 = null;
		try {
			contentImage = ImageIO.read(new File(path + "\\png\\content.png"));
			configImage = ImageIO.read(new File(path + "\\png\\config.png"));
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		int contentImageHeight = contentImage.getHeight();
		int contentImageWidth = contentImage.getWidth();
		int configImageHeight = configImage.getHeight();
		int configImageWidth = configImage.getWidth();

		List<File> list = getFileSort(path + "\\txt");
		for (File file : list) {
			String fileName = file.getName().substring(0, file.getName().indexOf(".txt"));

			try {
				String[] contents = readTextFile(file).split("\\*\\*mmmmmm\\*\\*");
				String picStr = contents[1].replaceAll("\r\n", "");
				String benRenQQ = picStr.substring(picStr.indexOf("����qq��"), picStr.indexOf("����")).split("��")[1];

				ImageFind findContent = new ImageFind(path + "\\png\\content.png");

				Location locationContent = findContent.findImage();

				if (locationContent != null) {
					desktopUtils.mouseMove(locationContent.getX() + contentImageWidth + 30,
							locationContent.getY() + contentImageHeight / 2);
					desktopUtils.mouseLeftPress();// ������
					desktopUtils.allSelect();
					desktopUtils.setSysClipboardText(benRenQQ);// ��ճ�����������
					desktopUtils.pasteStr();
					ImageFind findConfig = new ImageFind(path + "\\png\\config.png");
					Location locationConfig = findConfig.findImage();

					if (locationConfig != null) {

						desktopUtils.mouseMove(locationConfig.getX() + configImageWidth / 2,
								locationConfig.getY() + configImageHeight / 2);
						desktopUtils.mouseLeftPress();// ����������

						ImageFind findResult0;
						Location locationResult0;
						ImageFind findResult;
						Location locationResult;
						int xunhuan = 0;
						while (true) {
							findResult0 = new ImageFind(path + "\\png\\result0.png");
							locationResult0 = findResult0.findImage();

							if (locationResult0 != null) {

								findResult = new ImageFind(path + "\\png\\result.png");
								locationResult = findResult.findImage();
								if (locationResult != null) {// �������˽ش�ͼ
									if (locationContent.getY() - locationResult.getY() <= 80) {// ����յ���������ʾ���
										desktopUtils.mouseMove(locationConfig.getX() + configImageWidth / 2,
												locationConfig.getY() - 100);
										desktopUtils.mouseWheel(2);
									} else if (locationResult.getY() - locationResult0.getY() <= 50) {// ����յ���������ʾ���
										desktopUtils.mouseMove(locationConfig.getX() + configImageWidth / 2,
												locationConfig.getY() - 100);
										desktopUtils.mouseWheel(-2);
									} else {
										try {
											captureScreen(path + "\\bigPng", fileName + ".png");
											cutImage(path + "\\bigPng\\" + fileName + ".png", locationResult0.getX(),
													locationResult0.getY(),
													locationConfig.getX() + configImageWidth - locationResult0.getX(),
													locationConfig.getY() - locationResult0.getY());
											Docx.createDocx(path, fileName, contents[0]);
										} catch (Exception e1) {
											// TODO Auto-generated catch block
											e1.printStackTrace();
										}

										break;
									}

								} else {// δ��ʾ��ɫ�������������Ҫת�����
									if (xunhuan <= 200) {
										desktopUtils.mouseMove(locationConfig.getX() + configImageWidth / 2,
												locationConfig.getY() - 100);
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
								Thread.currentThread().sleep(100);
							} catch (InterruptedException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}

					} else {
						System.out.println("δ�ҵ�ȷ����ť�������ͼƬ");
						break;
					}

				} else {
					System.out.println("δ�ҵ������������ͼƬ");
					break;
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

}
