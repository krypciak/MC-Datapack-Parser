package me.krypek.mc.datapackparser;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;

import me.krypek.utils.Utils;

public class Datapack {
	private static final String PACKMCMETA_NAME = "pack.mcmeta";

	public record DatapackFunction(String submodule, String name, String contents) {}

	public final String NAME, DESCRIPTION;
	public final int PACK_FORMAT;

	private final String PACKMCMETA_CONTENTS;

	public final DatapackFunction[] functions;

	public Datapack(String name, int pack_format, String description, DatapackFunction[] functions) {
		this.NAME = name;
		this.PACK_FORMAT = pack_format;
		this.DESCRIPTION = description;
		this.functions = functions;
		//@f:off
		PACKMCMETA_CONTENTS = 
				  "{\n"
				+ "	\"pack\": {\n"
				+ "		\"pack_format\": " + PACK_FORMAT + ",\n"
				+ "		\"description\": \"" + DESCRIPTION + "\"\n"
				+ "	}\n"
				+ "}";
		
		/*LOAD_CONTENTS = 
				  "{\n"
				+ "    \"values\": [\n"
				+ "		\"" + tagSubmodule + ":load\"\n"
				+ "    ]\n"
				+ "}";
		
		TICK_CONTENTS = 
				  "{\n"
				+ "	\"values\": [\n"
				+ "		\"" + tagSubmodule + ":tick\"\n"
				+ "	]\n"
				+ "}\n";*/
		//@f:on
	}

	public void parse(String datapackPath) {
		File datapackFile = new File(datapackPath);
		datapackFile.mkdirs();
		datapackPath = datapackFile.getAbsolutePath();

		Utils.writeIntoFile(datapackPath + '/' + PACKMCMETA_NAME, PACKMCMETA_CONTENTS);

		for (DatapackFunction funcion : functions) { parseFunction(datapackPath, funcion); }
	}

	public void parseFunction(String datapackPath, DatapackFunction function) {
		String path = datapackPath + "/data/" + function.submodule + "/functions/" + function.name
				+ (!function.name.endsWith(".mcfunction") && !function.name.endsWith(".json") ? ".mcfunction" : "");
		System.out.println("Parsing to " + path);
		Utils.writeIntoFile(path, function.contents);
	}

	public static Datapack getDatapackFromFolder(String datapackPath) {
		File datapackFile = new File(datapackPath);
		if(!datapackFile.exists())
			throw new IllegalArgumentException("Datapack doesn't exist!");
		if(datapackFile.isDirectory())
			throw new IllegalArgumentException("Datapack path has to be a folder!");
		datapackPath = datapackFile.getAbsolutePath();

		String name = datapackFile.getName();
		final int pack_format;
		final String description;
		{
			String packmcmetaContents = Utils.readFromFile(datapackPath + '/' + PACKMCMETA_NAME, "");
			JSONObject packmcmetaJSON = new JSONObject(packmcmetaContents).getJSONObject("pack");
			pack_format = packmcmetaJSON.getInt("pack_format");
			description = packmcmetaJSON.getString("description");
		}

		File dataFolder = new File(datapackFile.getAbsolutePath() + '/' + "/data");
		if(!dataFolder.exists() || !dataFolder.isDirectory())
			throw new IllegalArgumentException("Data folder doesn't exist!");

		List<DatapackFunction> functionList = new ArrayList<>();

		File[] submodules = dataFolder.listFiles();
		for (File submodule : submodules) {
			String submoduleName = submodule.getName();
			if(submoduleName.equals("minecraft")) {
				File tickFile = new File(submodule.getAbsolutePath() + "/tags/functions/tick.json");
				if(tickFile.exists()) {
					functionList.add(new DatapackFunction("minecraft/tags", "tick", Utils.readFromFile(tickFile, "")));
				}
				File loadFile = new File(submodule.getAbsolutePath() + "/tags/functions/load.json");
				if(loadFile.exists()) {
					functionList.add(new DatapackFunction("minecraft/tags", "load", Utils.readFromFile(loadFile, "")));
				}
				continue;
			}

			File[] functions = new File(submodule.getAbsolutePath() + "/functions/").listFiles();
			for (File file : functions) {
				String contents = Utils.readFromFile(file, "\n");
				functionList.add(new DatapackFunction(submoduleName, file.getName(), contents));
			}
		}

		Datapack datapack = new Datapack(name, pack_format, description, functionList.toArray(DatapackFunction[]::new));
		return datapack;

	}

}
