package com.v88.record;

import java.awt.AWTException;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class ImageFind {
	BufferedImage maxImage;// 屏幕截图
	BufferedImage smallImage;// 需要查找的图片
	int maxWidth;// 屏幕截图宽度
	int maxHeight;// 屏幕截图高度
	int smarllWidth;// 查找图片的宽度
	int smarllHeight;// 查找图片的高度
	int[][] maxRGBData;// 大图所有的RGB数据
	int[][] smallRGBData;// 小图所有RGB数据

	public ImageFind(String path) {
		maxImage = this.getFullScreenShot();
		smallImage = this.getBfImageFromPath(path);
		maxRGBData = getImageRGB(maxImage);
		smallRGBData = getImageRGB(smallImage);
		maxWidth = maxImage.getWidth();
		maxHeight = maxImage.getHeight();
		smarllWidth = smallImage.getWidth();
		smarllHeight = smallImage.getHeight();

	}

	public Location findImage() {

		for (int x = 0; x < maxWidth - smarllWidth; x++) {
			for (int y = 0; y < maxHeight - smarllHeight; y++) {
				if (maxRGBData[x][y] == smallRGBData[0][0]
						&& maxRGBData[x + smarllWidth - 1][y] == smallRGBData[smarllWidth - 1][0]
						&& maxRGBData[x][y + smarllHeight - 1] == smallRGBData[0][smarllHeight - 1]
						&& maxRGBData[x + smarllWidth - 1][y + smarllHeight
								- 1] == smallRGBData[smarllWidth - 1][smarllHeight - 1]) {

					if (isMatchAll(x, y)) {

						return new Location(x, y);
					}

				}
			}
		}

		return null;
	}

	public boolean isMatchAll(int x, int y) {
		int maxX = 0;
		int maxY = 0;
		for (int smallX = 0; smallX < smarllWidth; smallX++) {
			maxX = x + smallX;
			for (int smallY = 0; smallY < smarllHeight; smallY++) {
				maxY = y + smallY;
				if (!(smallRGBData[smallX][smallY] == maxRGBData[maxX][maxY])) {
					return false;
				}
			}

		}
		return true;

	}

	/**
	 * 从本地文件读取目标图片
	 * 
	 * @param keyImagePath
	 *            - 图片绝对路径
	 * @return 本地图片的BufferedImage对象
	 */
	public BufferedImage getBfImageFromPath(String keyImagePath) {
		BufferedImage bfImage = null;
		try {
			bfImage = ImageIO.read(new File(keyImagePath));
		} catch (IOException e) {
			e.printStackTrace();
		}
		return bfImage;
	}

	/**
	 * 全屏截图
	 * 
	 * @return 返回BufferedImage
	 */
	public BufferedImage getFullScreenShot() {
		BufferedImage bfImage = null;
		int width = (int) Toolkit.getDefaultToolkit().getScreenSize().getWidth();
		int height = (int) Toolkit.getDefaultToolkit().getScreenSize().getHeight();
		try {
			Robot robot = new Robot();
			bfImage = robot.createScreenCapture(new Rectangle(0, 0, width, height));
		} catch (AWTException e) {
			e.printStackTrace();
		}
		return bfImage;
	}

	public int[][] getImageRGB(BufferedImage bfImage) {
		int width = bfImage.getWidth();
		int height = bfImage.getHeight();
		int[][] result = new int[width][height];
		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				result[x][y] = bfImage.getRGB(x, y) & 0xFFFFFF;
			}
		}
		return result;
	}

	// public static void main(String[] args) {
	// ImageFind find = new ImageFind("D:/key.png");
	// find.findImage();
	// }

}
