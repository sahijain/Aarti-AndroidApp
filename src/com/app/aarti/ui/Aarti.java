package com.app.aarti.ui;

public class Aarti {

	private String mAartiName;

	public String getmAartiName() {
		return mAartiName;
	}

	public enum AartiTypes {
		AARTI, STUTI, MANTRA, AARTI_STUTI, CHALISA_AARTI, AARTI_MANTRA;
	}

	private AartiTypes mAartiTypes;

	public AartiTypes getmAartiTypes() {
		return mAartiTypes;
	}

	public void setmAartiTypes(AartiTypes mAartiTypes) {
		this.mAartiTypes = mAartiTypes;
	}

	public Aarti(String aartiName, AartiTypes type) {
		mAartiName = aartiName;
		mAartiTypes = type;
		initAartiFileNames(mAartiName, mAartiTypes);
	}

	private void initAartiFileNames(String aartiName, AartiTypes type) {

	}

}
