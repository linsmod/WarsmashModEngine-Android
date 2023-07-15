package com.etheller.warsmash.viewer5.gl;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import net.hydromatic.linq4j.Linq;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ShaderShellCodes {
	public ShaderShellCodes(String vshPath, String fshPath) {
		if (Gdx.app.getType() == Application.ApplicationType.Android) {
			vshPath = "gles3.2/" + vshPath;
			fshPath = "gles3.2/" + fshPath;
		}
		else {
			vshPath = "gl3.3/" + vshPath;
			fshPath = "gl3.3/" + fshPath;
		}

		vshLines.addAll(Linq.of(Gdx.files.internal(vshPath).readString().split("\n")) .toList());
		fshLines.addAll(Linq.of(Gdx.files.internal(fshPath).readString().split("\n")) .toList());
	}

	public List<String> vshLines = new ArrayList<>();
	public List<String> fshLines = new ArrayList<>();

	public ShaderShellCodes vshPrependLine(String vsh) {
		vshLines.add(0, vsh);
		return this;
	}

	public String vsh() {
		return String.join("\n", vshLines);
	}

	public String fsh() {
		return String.join("\n", fshLines);
	}
}
