package com.etheller.warsmash.datasources;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class FolderDataSource implements DataSource {

	private final FileHandle folderPath;
	private final List<FileHandle> listfile = new ArrayList<>();
	private final boolean isAbsPath;

	public FolderDataSource(final String folderPath) {
		this.isAbsPath = new File(folderPath).isAbsolute();
		this.folderPath = isAbsPath ? Gdx.files.absolute(folderPath) : Gdx.files.internal(folderPath);
		List<FileHandle> childList = Arrays.stream(this.folderPath.list()).collect(Collectors.toList());
		listfile.addAll(childList);


		//android not auto expand the directory and isDirectory test always return false
		//do it ourselves
		listChild(childList);

		System.out.println("[DATA_SOURCE] " + listfile.size() + " files found in \"" + folderPath + "\"");
	}

	void listChild(List<FileHandle> files) {
		for (int i = 0; i < files.size(); i++) {
			FileHandle file = files.get(i);
			if (file.extension().equals("")) {
				FileHandle subFolder = isAbsPath ? files.get(i) : Gdx.files.internal(absToRelative(file.path()));
				List<FileHandle> childList = Arrays.stream(subFolder.list()).collect(Collectors.toList());
				this.listfile.addAll(childList);
				listChild(childList);
			}
		}
	}

	@Override
	public InputStream getResourceAsStream(String filepath) throws IOException {
		filepath = fixFilepath(filepath);
		if (!has(filepath)) {
			return null;
		}
		if (new File(filepath).isAbsolute()) {
			return Gdx.files.absolute(filepath).read();
		}
		else {
			if (this.isAbsPath) {
				return this.folderPath.child(filepath).read();
			}
			else {
				return Gdx.files.internal(filepath).read();
			}
		}
		//return Files.newInputStream(this.folderPath.resolve(filepath), StandardOpenOption.READ);
	}

	@Override
	public File getFile(String filepath) throws IOException {
		filepath = fixFilepath(filepath);
		if (!has(filepath)) {
			return null;
		}
		return new File(this.folderPath.toString() + File.separatorChar + filepath);
	}

	@Override
	public File getDirectory(String filepath) throws IOException {
		filepath = fixFilepath(filepath);
		File file = new File(this.folderPath.toString() + File.separatorChar + filepath);
		if (!file.exists() || !file.isDirectory()) {
			return null;
		}
		return file;
	}

	@Override
	public ByteBuffer read(String path) throws IOException {
		path = fixFilepath(path);
		if (!has(path)) {
			return null;
		}
		return ByteBuffer.wrap(Files.readAllBytes(Paths.get(this.folderPath.toString(), path)));
	}

	@Override
	public boolean has(String filepath) {
		filepath = fixFilepath(filepath);
		if ("".equals(filepath)) {
			return false; // special case for folder data source, dont do this
		}
		String finalFilepath = filepath;
		return this.listfile.stream().anyMatch(x -> pathEqual(finalFilepath, x.path()));

//		final Path resolvedPath = this.folderPath.resolve(filepath);
//		return Files.exists(resolvedPath) && !Files.isDirectory(resolvedPath);
	}

	@Override
	public Collection<String> getListfile() {
		return this.listfile.stream().map(x -> x.path()).collect(Collectors.toCollection(ArrayList::new));
		//return this.listfile;
	}

	boolean pathEqual(String path, String listitem) {
		listitem = fixFilepath(listitem);
		if (listitem.startsWith(File.separator))
			listitem = listitem.substring(1);
		else if (listitem.startsWith("." + File.separatorChar)) {
			listitem = listitem.substring(2);
		}
		return path.equalsIgnoreCase(listitem);
	}

	@Override
	public void close() {
	}

	String absToRelative(String path) {
		path = fixFilepath(path);
		if (path.startsWith(File.separator))
			return path.substring(1);
		if (path.startsWith("." + File.separator))
			return path.substring(2);
		return path;
	}

	private static String fixFilepath(final String filepath) {
		String path = filepath.replace('\\', File.separatorChar).replace('/', File.separatorChar).replace(':',
				File.separatorChar);

		return path;
	}
	@Override
	public String getPathName() {
		return this.folderPath.toString();
	}
}
