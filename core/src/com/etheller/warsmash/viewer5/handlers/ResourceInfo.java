package com.etheller.warsmash.viewer5.handlers;

import com.badlogic.gdx.files.FileHandle;
import com.etheller.warsmash.datasources.DataSource;
import com.google.common.base.Strings;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

public class ResourceInfo {
	public String path;
	public String resolvedPath;
	public DataSource dataSource;

	public ResourceInfo(DataSource dataSource, String path, String resolvedPath) {

		this.dataSource = dataSource;
		this.path = path;
		this.resolvedPath = resolvedPath;
	}

	public String getCachePath(String cacheFolder, String extAppend) {
		return rTrailSlash(cacheFolder)  +File.separator
					   + this.dataSource.getPathName()
					   + File.separator
					   + rheadSlash(this.resolvedPath)
					   +((Strings.isNullOrEmpty(extAppend) || extAppend.startsWith(".")) ? extAppend : "." + extAppend);
	}

	String rheadSlash(final String str) {
		if (str.startsWith("\\") || str.startsWith("/")) {
			return str.substring(1);
		}
		return str;
	}

	String rTrailSlash(final String str) {
		if (str.endsWith("\\") || str.endsWith("/")) {
			return str.substring(str.length() - 2);
		}
		return str;
	}

	public InputStream getResourceAsStream() throws IOException {
		return this.dataSource.getResourceAsStream(this.path);
	}
}
