package com.etheller.warsmash.viewer5.handlers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.etheller.warsmash.GdxEnv;
import com.etheller.warsmash.datasources.CompoundDataSource;
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

	public ResourceInfo(DataSource dataSource, String path) {
		this(dataSource, path, path);
	}

	public DataSource getDirectDataSource() {
		if (this.dataSource instanceof CompoundDataSource) {
			return ((CompoundDataSource) dataSource).getDataSource(path);
		}
		return this.dataSource;
	}

	public String getCachePath(String cacheFolder, String extAppend) {
		if (this.resolvedPath.contains("ReplaceableTextures")) {
			return rTrailSlash(GdxEnv.EXTERNAL_STORAGE_ROOT)
						   + File.separator
						   + rheadSlash(rTrailSlash(cacheFolder))
						   + File.separator
						   + rheadSlash(this.resolvedPath)
						   + ((Strings.isNullOrEmpty(extAppend) || extAppend.startsWith(".")) ? extAppend : "." + extAppend);
		}
		return rTrailSlash(GdxEnv.EXTERNAL_STORAGE_ROOT)
					   + File.separator
					   + rheadSlash(rTrailSlash(cacheFolder))
					   + File.separator
					   + this.getDirectDataSource().getPathName()
					   + File.separator
					   + rheadSlash(this.resolvedPath)
					   + ((Strings.isNullOrEmpty(extAppend) || extAppend.startsWith(".")) ? extAppend : "." + extAppend);
	}

	public FileHandle getCacheFile(String cacheFolder, String extAppend) {
		return Gdx.files.absolute(getCachePath(cacheFolder, extAppend));
	}

	String rheadSlash(final String str) {
		if (str.startsWith("\\") || str.startsWith("/")) {
			return str.substring(1);
		}
		return str;
	}

	String rTrailSlash(final String str) {
		if (str.endsWith("\\") || str.endsWith("/")) {
			return str.substring(0, str.length() - 1);
		}
		return str;
	}

	public InputStream getResourceAsStream() throws IOException {
		return this.dataSource.getResourceAsStream(this.path);
	}
}
