package apocalyptic.blocks.gas;

import java.util.Random;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.BlockStationary;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.util.Icon;
import net.minecraft.world.World;
import apocalyptic.Apocalyptic;
import apocalyptic.ApocalypticCreativeTab;

public class GasStationary extends BlockStationary {

    Random rand = new Random();

    public GasStationary(int i, Material material) {
        super(i, material);
        setHardness(100F);
        setResistance(1000.0F);
        disableStats();
        this.setTickRandomly(true);
        setCreativeTab(ApocalypticCreativeTab.tabApocalyptic);
    }

    @Override
    public int getRenderType() {
        return Apocalyptic.gasModel;
    }

    @Override
    public boolean renderAsNormalBlock() {
        return false;
    }

    @SideOnly(Side.CLIENT)
    public Icon getIcon(int par1, int par2) {
        return par1 != 0 && par1 != 1 ? this.theIcon[1] : this.theIcon[0];
    }

    @SideOnly(Side.CLIENT)
    public void registerIcons(IconRegister par1IconRegister) {
        this.theIcon = new Icon[]{par1IconRegister.registerIcon("apocalyptic:Gas"), par1IconRegister.registerIcon("apocalyptic:GasFlowing")};
    }

    @Override
    public int tickRate(World world) {
        return 40;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public int getRenderBlockPass() {
        return 1;
    }

    @Override
    public boolean isBlockReplaceable(World world, int i, int j, int k) {
        return true;
    }
    
    @Override
    public void onNeighborBlockChange(World par1World, int par2, int par3, int par4, int par5) {
        super.onNeighborBlockChange(par1World, par2, par3, par4, par5);
        if (par1World.getBlockId(par2, par3, par4) == this.blockID) {
            if (!this.checkForCreatingSource(par1World, par2, par3, par4)) {
                this.setNotStationary(par1World, par2, par3, par4);
            }
        }
    }
    
    private void setNotStationary(World par1World, int par2, int par3, int par4) {
        int l = par1World.getBlockMetadata(par2, par3, par4);
        par1World.setBlock(par2, par3, par4, this.blockID - 1, l, 2);
        par1World.scheduleBlockUpdate(par2, par3, par4, this.blockID - 1, this.tickRate(par1World));
    }

    /**
     * Kunik's code: first, check if there air block near.
     */
    private boolean checkForCreatingSource(World par1World, int par2, int par3, int par4) {
        if (par1World.getBlockId(par2, par3, par4) == this.blockID) {
            if (this.blockID == Apocalyptic.FogStill.blockID) {
                boolean var5 = false;

                if (var5 || par1World.getBlockMaterial(par2, par3, par4 - 1) == Material.air) {
                    var5 = true;
                }

                if (var5 || par1World.getBlockMaterial(par2, par3, par4 + 1) == Material.air) {
                    var5 = true;
                }

                if (var5 || par1World.getBlockMaterial(par2 - 1, par3, par4) == Material.air) {
                    var5 = true;
                }

                if (var5 || par1World.getBlockMaterial(par2 + 1, par3, par4) == Material.air) {
                    var5 = true;
                }

                if (var5 || par1World.getBlockMaterial(par2, par3 + 1, par4) == Material.air) {
                    var5 = true;
                }

                /**
                 * Kunik's code: second, check if there gas block, to prevent
                 * loop
                 */
                if (!var5 || par1World.getBlockMaterial(par2, par3, par4 - 1) == Material.gas) {
                    var5 = false;
                }

                if (!var5 || par1World.getBlockMaterial(par2, par3, par4 + 1) == Material.gas) {
                    var5 = false;
                }

                if (!var5 || par1World.getBlockMaterial(par2 - 1, par3, par4) == Material.gas) {
                    var5 = false;
                }

                if (!var5 || par1World.getBlockMaterial(par2 + 1, par3, par4) == Material.gas) {
                    var5 = false;
                }

                if (!var5 || par1World.getBlockMaterial(par2, par3 + 1, par4) == Material.gas) {
                    var5 = false;
                }

                if (var5) {
                    par1World.setBlock(par2, par3, par4, Apocalyptic.FogMoving.blockID);
                    return true;
                }
            }
        }
        return false;
    }
}
