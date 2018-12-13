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

	// ��ȡ������
	public int getScreenWidth() {
		int screenWidth = ((int) java.awt.Toolkit.getDefaultToolkit().getScreenSize().width);
		return screenWidth;
	}

	// ��ȡ����߶�
	public int getScreenHeight() {
		int screenHeight = ((int) java.awt.Toolkit.getDefaultToolkit().getScreenSize().height);
		return screenHeight;
	}

	// �������λ��
	public void mouseMove(int x, int y) {
		if (turnOff)
			System.out.println("�ƶ���굽(" + x + "," + y + ")");
		Point point = java.awt.MouseInfo.getPointerInfo().getLocation();
		while (!(point.x == x && point.y == y)) {
			robot.mouseMove(x, y);
			robot.delay(30);
			point = java.awt.MouseInfo.getPointerInfo().getLocation();
		}

	}

	// ������
	public void mouseLeftPress() {
		if (turnOff)
			System.out.println("����������");
		robot.mousePress(InputEvent.BUTTON1_MASK);
		robot.mouseRelease(InputEvent.BUTTON1_MASK);
		robot.delay(200);
	}

	// ����Ҽ�
	public void mouseRightPress() {
		if (turnOff)
			System.out.println("��������Ҽ�");
		robot.mousePress(InputEvent.BUTTON3_MASK);
		robot.mouseRelease(InputEvent.BUTTON3_MASK);
		robot.delay(200);
	}

	// ESCȡ������Ҽ�
	public void ESC() {
		if (turnOff)
			System.out.println("����ESC");
		robot.keyPress(KeyEvent.VK_ESCAPE);
		robot.keyRelease(KeyEvent.VK_ESCAPE);
		robot.delay(200);
	}

	/**
	 * ���ַ������Ƶ����а塣
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
	 * 1. �Ӽ��а������֡�
	 */
	public synchronized static String getSysClipboardText() {
		String ret = "";
		Clipboard sysClip = Toolkit.getDefaultToolkit().getSystemClipboard();
		// ��ȡ���а��е�����
		Transferable clipTf = sysClip.getContents(null);

		if (clipTf != null) {
			// ��������Ƿ����ı�����
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

	// ճ��
	public void pasteStr() {
		// ����crtl v�� ��
		robot.keyPress(KeyEvent.VK_CONTROL);
		robot.keyPress(KeyEvent.VK_V);
		// �ͷ�crtl v ��
		robot.keyRelease(KeyEvent.VK_V);
		robot.keyRelease(KeyEvent.VK_CONTROL);
		robot.delay(200);
	}

	// ȫѡ
	public void allSelect() {
		// ����crtl A�� ��
		robot.keyPress(KeyEvent.VK_CONTROL);
		robot.keyPress(KeyEvent.VK_A);
		// �ͷ�crtl A ��
		robot.keyRelease(KeyEvent.VK_A);
		robot.keyRelease(KeyEvent.VK_CONTROL);
		robot.delay(200);
	}

	public void mouseWheel(int wheelAmt) {

		robot.mouseWheel(wheelAmt);
		robot.delay(10);

	}

}
