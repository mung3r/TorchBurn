package com.gmail.rcarretta.torchburn;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.File;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

class TorchBurnConfig {
	private static final String UTF8 = "UTF-8";
    private static final String configDirectory = "plugins/TorchBurn";
	private static final String configFileName = "TorchBurn.config";
	private final TorchBurn plugin;
	
	protected TorchBurnConfig(final TorchBurn plugin) {
		this.plugin = plugin;
	}
	
	protected void configRead () {
		try {
			File configDirFile = new File(configDirectory);
			File configFileFile = new File(configDirectory+"/"+configFileName);
			if (!configDirFile.exists() || !configDirFile.isDirectory()) {
				System.out.println("TorchBurn directory not found. Creating.");
				configDirFile.mkdir();
		    }
			if (!configFileFile.exists() || !configFileFile.isFile()) {
				System.out.println("TorchBurn configuration file not found. Creating.");
				configWrite();
			}
			
			System.out.println("Reading TorchBurn configuration.");
			FileInputStream fr = new FileInputStream(configDirectory + "/" + configFileName);
			BufferedReader br = new BufferedReader(new InputStreamReader(fr, UTF8));
			String line = br.readLine();
			while (line != null) {
				// read BurnDuration, LightIntensity, LightFalloff, RequireSneaking, AllowUnderwater, SetFire, FastServer
				if (line.length() == 0) {
					line = br.readLine();
					continue;
				}
				if (line.charAt(0) == '#') {
					line = br.readLine();
					continue;
				}
				String[] lines = line.split("=");
				if (lines.length > 2) {
					line = br.readLine();
					continue;
				}
				if (lines[0].equalsIgnoreCase("BurnDuration")) {
					int burnDuration = Integer.parseInt(lines[1]);
					plugin.setBurnDuration(burnDuration);
				}
				else if (lines[0].equalsIgnoreCase("LightIntensity")) {
					int lightIntensity = Integer.parseInt(lines[1]);
					if (lightIntensity < 1 || lightIntensity > 15) { 
						System.out.println("LightIntensity must be between 1 and 15. Using default value of 15.");
						lightIntensity = 15;
					}
					plugin.setLightIntensity(lightIntensity);
				}
				else if (lines[0].equalsIgnoreCase("LightFalloff")) {
					int lightFalloff = Integer.parseInt(lines[1]);
					if (lightFalloff < 1 || lightFalloff > 15) { 
						System.out.println("LightFalloff must be between 1 and 15. Using default value of 3.");
						lightFalloff = 3;
					}
					plugin.setLightFalloff(lightFalloff);
				}
				else if (lines[0].equalsIgnoreCase("RequireSneaking")) {
					boolean requireSneaking = true;
					if (lines[1].equalsIgnoreCase("true"))
						requireSneaking = true;
					else if (lines[1].equalsIgnoreCase("false"))
						requireSneaking = false;
					else {
						System.out.println("Boolean configuration argument expected for RequireSneaking but arg was non-boolean. Defaulting to true.");
					}
					plugin.setRequireSneaking(requireSneaking);
				}
				else if (lines[0].equalsIgnoreCase("AllowUnderwater")) {
					boolean allowUnderwater = false;
					if (lines[1].equalsIgnoreCase("true"))
						allowUnderwater = true;
					else if (lines[1].equalsIgnoreCase("false"))
						allowUnderwater = false;
					else {
						System.out.println("Boolean configuration argument expected for AllowUnderwater but arg was non-boolean. Defaulting to false.");
					}
					plugin.setAllowUnderwater(allowUnderwater);
				}
				else if (lines[0].equalsIgnoreCase("SetFire")) {
					boolean setFire = false;
					if (lines[1].equalsIgnoreCase("true"))
						setFire = true;
					else if (lines[1].equalsIgnoreCase("false"))
						setFire = false;
					else {
						System.out.println("Boolean configuration argument expected for SetFire but arg was non-boolean. Defaulting to false.");
					}
					plugin.setSetFire(setFire);
				}
				else if (lines[0].equalsIgnoreCase("FastServer")) {
					boolean fastServer = false;
					if (lines[1].equalsIgnoreCase("true"))
						fastServer = true;
					else if (lines[1].equalsIgnoreCase("false"))
						fastServer = false;
					else {
						System.out.println("Boolean configuration argument expected for FastServer but arg was non-boolean. Defaulting to false.");
					}
					plugin.setFastServer(fastServer);
				}

				else {
					System.out.println("Encountered unsupported configuration variable: " + lines[0] + " Ignoring.");
				}
				line = br.readLine();
			}
			br.close();
		}
		catch (NumberFormatException nfe) {
			System.out.println("Integer configuration argument expected but arg was non-integer.");
			nfe.printStackTrace();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	protected void configWrite () {
		System.out.println("Writing TorchBurn configuration.");
		try {
		    FileOutputStream fw = new FileOutputStream(configDirectory + "/" + configFileName);
			BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fw, UTF8));
			// write BurnDuration, LightIntensity, LightFalloff, RequireSneaking, AllowUnderwater, SetFire, FastServer
			bw.write("BurnDuration=" + plugin.getDuration() + "\n");
			bw.write("LightIntensity=" + plugin.getIntensity() + "\n");
			bw.write("LightFalloff=" + plugin.getFalloff() + "\n");
			bw.write("RequireSneaking=" + (plugin.getRequireSneaking() ? "true" : "false") + "\n");
			bw.write("AllowUnderwater=" + (plugin.getAllowUnderwater() ? "true" : "false") + "\n");
			bw.write("SetFire=" + (plugin.getSetFire() ? "true" : "false") + "\n");
			bw.write("FastServer=" + (plugin.getFastServer() ? "true" : "false") + "\n");
			bw.close();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
}
