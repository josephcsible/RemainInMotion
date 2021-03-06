package me.planetguy.remaininmotion.carriage;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import me.planetguy.lib.prefab.BlockContainerBase;
import me.planetguy.remaininmotion.core.CreativeTab;
import me.planetguy.remaininmotion.base.ToolItemSet;
import me.planetguy.remaininmotion.api.FrameCarriageMatcher;
import me.planetguy.remaininmotion.api.RiMRegistry;
import me.planetguy.remaininmotion.base.ICamouflageable;
import me.planetguy.remaininmotion.core.ModRiM;
import me.planetguy.remaininmotion.core.RIMBlocks;

public class BlockSimpleFrame extends BlockContainerBase implements ICamouflageable {

	public IIcon corners, sides;
	
	
	public BlockSimpleFrame() {
		super(Material.wood, "simpleFrame", TileEntity.class);
		this.setCreativeTab(CreativeTab.Instance);
		RiMRegistry.registerFrameCarriageMatcher(new FrameCarriageMatcher() {

			@Override
			public boolean isFrameCarriage(Block block1, int meta1,
					TileEntity entity1) {
				return block1==BlockSimpleFrame.this;
			}
			
		});
	}
	
	@Override
	public void registerBlockIcons(IIconRegister ir) {
		corners=ir.registerIcon(ModRiM.Handle + ":FMPCarriage_Corners");
	}

	@Override
	public IIcon getIconCamouflaged(IBlockAccess world, int x, int y, int z,
			int side) {
		return null;
	}
	
	public IIcon getIcon(int side, int meta) {
		if(meta==0)
			return BlockCarriage.Types.Frame.OpenIcon;
		else if(meta==1)
			return BlockCarriage.Types.Frame.ClosedIcon;
		else
			return corners;
	}
	
	@Override
	public boolean onBlockActivated(World w, int x, int y, int z, EntityPlayer Player, int Side, float HitX,
			float HitY, float HitZ) {
		if(ToolItemSet.IsScrewdriverOrEquivalent(Player.inventory.getCurrentItem())) {
			w.setBlock(x, y, z, RIMBlocks.Carriage);
			w.getBlock(x, y, z).onBlockActivated(w, x, y, z, Player, Side, HitX, HitY, HitZ);
			return true;
		}else
			return false;
	}
	
	public boolean isOpaqueCube() {
		return false;
	}
	
	public int countTooltipLines() {
		return 1;
	}

}
