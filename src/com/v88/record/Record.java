package com.v88.record;

public class Record {
	private String recommendName = "";// �Ƽ���
	private String recommendQQ = "";// �Ƽ���qq
	private String entryName = "";// ��ְ��
	private String entryQQ = "";// ��ְ��qq
	private String grade = "";// ����
	private String time = "";// ��ְ����
	private String receiver = "";// �Ӵ���
	private String teacher = "";// ��ѵ��ʦ
	private String count = "";// ����
	// ^[\w\?%&=\-_]+\(\d*\)(\d*\/\d*\/)?\d*:\d*(:\d*)?

	public String getRecommendName() {
		return recommendName;
	}

	public void setRecommendName(String recommendName) {
		this.recommendName = recommendName;
	}

	public String getTeacher() {
		return teacher;
	}

	public void setTeacher(String teacher) {
		this.teacher = teacher;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public String getRecommendQQ() {
		return recommendQQ;
	}

	public void setRecommendQQ(String recommendQQ) {
		this.recommendQQ = recommendQQ;
	}

	public String getEntryName() {
		return entryName;
	}

	public void setEntryName(String entryName) {
		this.entryName = entryName;
	}

	public String getEntryQQ() {
		return entryQQ;
	}

	public void setEntryQQ(String entryQQ) {
		this.entryQQ = entryQQ;
	}

	public String getGrade() {
		return grade;
	}

	public void setGrade(String grade) {
		this.grade = grade;
	}

	public String getReceiver() {
		return receiver;
	}

	public void setReceiver(String receiver) {
		this.receiver = receiver;
	}

	public String getCount() {
		return count;
	}

	public void setCount(String count) {
		this.count = count;
	}

	@Override
	public String toString() {
		return "Record [recommendName=" + recommendName + ", recommendQQ=" + recommendQQ + ", entryName=" + entryName
				+ ", entryQQ=" + entryQQ + ", grade=" + grade + ", time=" + time + ", receiver=" + receiver
				+ ", teacher=" + teacher + ", count=" + count + "]";
	}

}
