package apocalyptic.client.renderer;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.world.IBlockAccess;
import apocalyptic.Apocalyptic;
import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;

public class GasRenderer implements ISimpleBlockRenderingHandler {
	private int ext;
	public boolean statement;

	public boolean testBlock(IBlockAccess world, int x, int y, int z) {
		ext = 0;
        if (world.getBlockId(x + 1, y, z) == Block.torchWood.blockID) {
        	if (world.getBlockMaterial(x + 2, y, z) != Material.air) {ext++;}
        	if (world.getBlockMaterial(x + 1, y + 1, z) != Material.air) {ext++;}
        	if (world.getBlockMaterial(x + 1, y, z - 1) != Material.air) {ext++;}
        	if (world.getBlockMaterial(x + 1, y, z + 1) != Material.air) {ext++;}
        	if(ext == 4) {statement = false;}
        }
        else if (world.getBlockId(x, y, z + 1) == Block.torchWood.blockID) {
        	if (world.getBlockMaterial(x, y, z + 2) != Material.air) {ext++;}
        	if (world.getBlockMaterial(x, y + 1, z + 1) != Material.air) {ext++;}
        	if (world.getBlockMaterial(x - 1, y, z + 1) != Material.air) {ext++;}
        	if (world.getBlockMaterial(x + 1, y, z + 1) != Material.air) {ext++;}
        	if(ext == 4) {statement = false;}
        }
        else if (world.getBlockId(x - 1, y, z) == Block.torchWood.blockID) {
        	if (world.getBlockMaterial(x - 2, y, z) != Material.air) {ext++;}
        	if (world.getBlockMaterial(x - 1, y + 1, z) != Material.air) {ext++;}
        	if (world.getBlockMaterial(x - 1, y, z - 1) != Material.air) {ext++;}
        	if (world.getBlockMaterial(x - 1, y, z + 1) != Material.air) {ext++;}
        	if(ext == 4) {statement = false;}
        }
        else if (world.getBlockId(x, y, z - 1) == Block.torchWood.blockID) {
        	if (world.getBlockMaterial(x, y, z - 2) != Material.air) {ext++;}
        	if (world.getBlockMaterial(x, y + 1, z - 1) != Material.air) {ext++;}
        	if (world.getBlockMaterial(x + 1, y, z - 1) != Material.air) {ext++;}
        	if (world.getBlockMaterial(x - 1, y, z - 1) != Material.air) {ext++;}
        	if(ext == 4) {statement = false;}
        }
        else if (world.getBlockId(x, y - 1, z) == Block.torchWood.blockID) {
        	if (world.getBlockMaterial(x + 1, y - 1, z) != Material.air) {ext++;}
        	if (world.getBlockMaterial(x - 1, y - 1, z) != Material.air) {ext++;}
        	if (world.getBlockMaterial(x, y - 1, z + 1) != Material.air) {ext++;}
        	if (world.getBlockMaterial(x, y - 1, z - 1) != Material.air) {ext++;}
        	if(ext == 4) {statement = false;}
        }
        else {
        	statement = true;
        }
        return statement;
	}

	@Override
	public void renderInventoryBlock(Block block, int metadata, int modelID, RenderBlocks renderer) {
	}

	@Override
	public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z, Block block, int modelId, RenderBlocks renderer) {
		if (testBlock(world, x, y, z)) {
		if (block.getRenderType() != Apocalyptic.gasModel)
			return true;

		renderer.renderBlockFluids(block, x, y, z);
			return true;
		}
		else {
			return false;
		}
	}

	@Override
	public boolean shouldRender3DInInventory() {
		return false;
	}

	@Override
	public int getRenderId() {
		return Apocalyptic.gasModel;
	}
}
