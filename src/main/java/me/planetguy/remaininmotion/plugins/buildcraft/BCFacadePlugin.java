package me.planetguy.remaininmotion.plugins.buildcraft;

import me.planetguy.lib.util.Debug;
import me.planetguy.remaininmotion.api.RiMRegistry;
import me.planetguy.remaininmotion.plugins.RemIMPluginsCommon;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.event.FMLInterModComms;

public class BCFacadePlugin {

	public static void tryLoad() {
		if (Loader.isModLoaded("BuildCraft|Transport")) {

			try {
				Class.forName("buildcraft.api.transport.pluggable.IFacadePluggable");

				Debug.dbg("Buildcraft special facade: loading");

				RemIMPluginsCommon.getFrameBlock();

				RiMRegistry.registerFrameCarriageMatcher(new SpecialFacadeCarriageMatcher());

				RiMRegistry.registerCloseableFactory(new SpecialFacadeCloseableFactory());

				FMLInterModComms.sendMessage("BuildCraft|Transport", "add-facade", RemIMPluginsCommon.getFrameBlock()
						+ "@1");

			} catch (Exception e) {

				Debug.dbg("Buildcraft special facade: not loading, missing API component");

			}

		} else {
			Debug.dbg("Buildcraft special facade: not loading, no BC");
		}
	}

}
