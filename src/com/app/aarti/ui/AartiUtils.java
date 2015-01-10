package com.app.aarti.ui;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.os.Build.VERSION;

public class AartiUtils {
	public static final String ACTION_COMPLETED = "completed";
	public static final String ACTION_PAUSE = "pause";
	public static final String ACTION_PLAY = "play";
	public static final String ACTION_STOP = "stop";
	public static final int COMPLETED = 2;
	public static String KEY = "";
	public static final int PAUSE = 4;
	public static final int PLAY = 3;
	public static final int STOP = 5;

	public static class AartiConstants {
		public static final String DURGA_AARTI = "durga";
		public static final String GANESH_AARTI = "ganesh";
		public static final String GAYATRI_AARTI = "gayatri";
		public static final String HANUMAN_AARTI = "hanuman";
		public static final String SHANKAR_AARTI = "shiv";
		public static final String VISHNU_AARTI = "vishnu";
	}

	public static class DeviceAPIChecker {
		public static final boolean hasFroyo() {
			return Build.VERSION.SDK_INT >= 8;
		}

		public static final boolean hasGingerbread() {
			return Build.VERSION.SDK_INT >= 9;
		}

		public static final boolean hasHoneycomb() {
			return Build.VERSION.SDK_INT >= 11;
		}

		public static final boolean hasHoneycombMR1() {
			return Build.VERSION.SDK_INT >= 12;
		}

		public static final boolean hasICS() {
			return Build.VERSION.SDK_INT >= 14;
		}

		public static final boolean hasJellyBean() {
			return Build.VERSION.SDK_INT >= 16;
		}

		public static final boolean isLandscape(Context paramContext) {
			return paramContext.getResources().getConfiguration().orientation == 2;
		}

		public static final boolean isTablet(Context paramContext) {
			return (0xF & paramContext.getResources().getConfiguration().screenLayout) >= 3;
		}
	}
}
