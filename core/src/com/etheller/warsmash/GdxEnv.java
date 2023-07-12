package com.etheller.warsmash;

import java.io.File;

public class GdxEnv {
	public static String EXTERNAL_STORAGE_ROOT = "<ERR_EXTERNAL_STORAGE_ROOT_NOT_SET>";

	public static String underExternalROOT(String path) {
		return EXTERNAL_STORAGE_ROOT + File.separator + path;
	}
}
