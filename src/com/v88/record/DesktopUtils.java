package com.v88.record;

import java.awt.AWTException;
import java.awt.Point;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

public class DesktopUtils {
	public Robot robot;
	public boolean turnOff = false;

	public DesktopUtils() {
		try {
			robot = new Robot();
		} catch (AWTException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	// 获取桌面宽度
	public int getScreenWidth() {
		int screenWidth = ((int) java.awt.Toolkit.getDefaultToolkit().getScreenSize().width);
		return screenWidth;
	}

	// 获取桌面高度
	public int getScreenHeight() {
		int screenHeight = ((int) java.awt.Toolkit.getDefaultToolkit().getScreenSize().height);
		return screenHeight;
	}

	// 设置鼠标位置
	public void mouseMove(int x, int y) {
		if (turnOff)
			System.out.println("移动鼠标到(" + x + "," + y + ")");
		Point point = java.awt.MouseInfo.getPointerInfo().getLocation();
		while (!(point.x == x && point.y == y)) {
			robot.mouseMove(x, y);
			robot.delay(30);
			point = java.awt.MouseInfo.getPointerInfo().getLocation();
		}

	}

	// 鼠标左键
	public void mouseLeftPress() {
		if (turnOff)
			System.out.println("按下鼠标左键");
		robot.mousePress(InputEvent.BUTTON1_MASK);
		robot.mouseRelease(InputEvent.BUTTON1_MASK);
		robot.delay(200);
	}

	// 鼠标右键
	public void mouseRightPress() {
		if (turnOff)
			System.out.println("按下鼠标右键");
		robot.mousePress(InputEvent.BUTTON3_MASK);
		robot.mouseRelease(InputEvent.BUTTON3_MASK);
		robot.delay(200);
	}

	// ESC取消鼠标右键
	public void ESC() {
		if (turnOff)
			System.out.println("按下ESC");
		robot.keyPress(KeyEvent.VK_ESCAPE);
		robot.keyRelease(KeyEvent.VK_ESCAPE);
		robot.delay(200);
	}

	/**
	 * 将字符串复制到剪切板。
	 */
	public synchronized void setSysClipboardText(String writeMe) {
		while (true) {
			try {
				Clipboard clip = Toolkit.getDefaultToolkit().getSystemClipboard();
				Transferable tText = new StringSelection(writeMe);
				clip.setContents(tText, null);
				break;
			} catch (IllegalStateException e) {
				try {
					Thread.currentThread().sleep(500);
				} catch (InterruptedException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				continue;
			}

		}

	}

	/**
	 * 1. 从剪切板获得文字。
	 */
	public synchronized static String getSysClipboardText() {
		String ret = "";
		Clipboard sysClip = Toolkit.getDefaultToolkit().getSystemClipboard();
		// 获取剪切板中的内容
		Transferable clipTf = sysClip.getContents(null);

		if (clipTf != null) {
			// 检查内容是否是文本类型
			if (clipTf.isDataFlavorSupported(DataFlavor.stringFlavor)) {
				try {
					ret = (String) clipTf.getTransferData(DataFlavor.stringFlavor);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}

		return ret;
	}

	// 粘贴
	public void pasteStr() {
		// 按下crtl v键 ；
		robot.keyPress(KeyEvent.VK_CONTROL);
		robot.keyPress(KeyEvent.VK_V);
		// 释放crtl v 键
		robot.keyRelease(KeyEvent.VK_V);
		robot.keyRelease(KeyEvent.VK_CONTROL);
		robot.delay(200);
	}

	// 全选
	public void allSelect() {
		// 按下crtl A键 ；
		robot.keyPress(KeyEvent.VK_CONTROL);
		robot.keyPress(KeyEvent.VK_A);
		// 释放crtl A 键
		robot.keyRelease(KeyEvent.VK_A);
		robot.keyRelease(KeyEvent.VK_CONTROL);
		robot.delay(200);
	}

	public void mouseWheel(int wheelAmt) {

		robot.mouseWheel(wheelAmt);
		robot.delay(10);

	}

}
