package me.planetguy.remaininmotion.core;

import java.io.File;
import java.util.HashMap;
import java.util.UUID;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent;
import me.planetguy.remaininmotion.network.PacketSpecterVelocity;
import net.minecraft.block.Block;
import me.planetguy.lib.PLHelper;
import me.planetguy.lib.prefab.GuiHandlerPrefab;
import me.planetguy.lib.util.Blacklist;
import me.planetguy.lib.util.Debug;
import me.planetguy.remaininmotion.drive.gui.ContainerDrive;
import me.planetguy.remaininmotion.drive.gui.GuiDirectional;
import me.planetguy.remaininmotion.drive.gui.GuiDriveCommon;
import me.planetguy.remaininmotion.drive.gui.GuiTranslocator;
import me.planetguy.remaininmotion.motion.BlacklistManager;
import me.planetguy.remaininmotion.plugins.RemIMPluginsCommon;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLInterModComms;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStoppingEvent;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;

@Mod(modid = ModRiM.Handle, name = ModRiM.Title, version = ModRiM.Version, dependencies = "required-after:planetguyLib;after:CoFHCore;after:BuildCraft|Transport")
public class ModRiM {
	public static final String	Namespace	= "me.planetguy.remaininmotion.";

	public static final String	Handle		= "JAKJ_RedstoneInMotion";

	public static final String	Title		= "Remain In motion";

	public static final String	Version		= "2.8.0";

	public static final String	Channel		= "JAKJ_RIM";

	public static PLHelper		plHelper;

	@Instance(ModRiM.Handle)
	public static ModRiM instance;

    //public HashMap<UUID,Integer> playerMountMap = new HashMap<UUID, Integer>();

	@EventHandler
	public void PreInit(FMLPreInitializationEvent Event) {
		plHelper	= new PLHelper(Handle);
		File oldFile = Event.getSuggestedConfigurationFile();
		File newFile = new File(oldFile.getParentFile().getAbsoluteFile() + File.separator + "remainInMotion.cfg");
		if (oldFile.exists()) {
			oldFile.renameTo(newFile);
		}
		(new RiMConfiguration(newFile)).Process();

        FMLCommonHandler.instance().bus().register(this);

		Core.HandlePreInit();
		
		RemIMPluginsCommon.instance.preInit();
	}

	@EventHandler
	public void Init(FMLInitializationEvent Event) {

        Core.HandleInit();
		RemIMPluginsCommon.instance.init();
	}

	@EventHandler
	public void PostInit(FMLPostInitializationEvent Event) {
		ClientSetupProxy.Instance.Execute();

		Core.HandlePostInit();
		
		
		GuiHandlerPrefab.create(this, new Class[]{
				ContainerDrive.class,
				ContainerDrive.class,
				ContainerDrive.class,
		},
		ClientSetupProxy.Instance.clientClasses());
		
		RemIMPluginsCommon.postInit();
	}

	@EventHandler
	public void ServerStopping(FMLServerStoppingEvent Event) {
		Core.HandleServerStopping();
	}

	@EventHandler
	public void handleIMCMessage(FMLInterModComms.IMCEvent event) {
		for (final FMLInterModComms.IMCMessage message : event.getMessages()) {
			try {
				if (message.key.equals("blacklistHard")) {
					handleBlacklistIMC(message.getStringValue(), BlacklistManager.blacklistHard, message.getSender());
				}else if (message.key.equals("blacklistSoft")) {
					handleBlacklistIMC(message.getStringValue(), BlacklistManager.blacklistSoft, message.getSender());
				}else if (message.key.equals("blacklistRotation")) {
					handleBlacklistIMC(message.getStringValue(), BlacklistManager.blacklistRotation, message.getSender());
				}
			}catch(Exception e) {
				System.err.println("Utterly incomprehensible IMC from "+message.getSender());
			}
		}
	}
	
	private void handleBlacklistIMC(String message, Blacklist targetList, String sender) {
		String[] parts=message.split(":");
		if(parts.length==1) {
			try {
				BlacklistManager.blacklistRotation.blacklist(Block.getBlockFromName(message));
			} catch (Exception e) {
				System.err.println("Received bad blacklist request from " + sender + ": " + message);
			}
		}else if(parts.length==3){
			try {
				BlacklistManager.blacklistRotation.blacklist(Block.getBlockFromName(parts[0]), Integer.parseInt(parts[2]));
			} catch (Exception e) {
				System.err.println("Received bad blacklist request from " + sender + ": " + message);
			}
		} else {
			System.err.println("Received impossible blacklist request from " + sender + ": " + message);
		}
	}

    @SubscribeEvent
    public void onJoin(PlayerEvent.PlayerLoggedInEvent event){
        if(!(event.player instanceof EntityPlayerMP)) return;
        if(((EntityPlayerMP) event.player).isClientWorld())
            PacketSpecterVelocity.send((EntityPlayerMP) event.player);
    }

    /*@SubscribeEvent
    public void onFinishTeleporting(EntityJoinWorldEvent event){
        if(!(event.entity instanceof EntityPlayerMP)) return;
        Integer mount = playerMountMap.get(event.entity.getUniqueID());
        if(mount != null) {
            Entity entity = event.world.getEntityByID(mount);
            ((EntityPlayerMP)event.entity).mountEntity(entity);
            playerMountMap.remove(event.entity.getUniqueID());
        }
    }*/
}
