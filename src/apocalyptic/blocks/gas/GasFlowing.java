package apocalyptic.blocks.gas;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.BlockFluid;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.util.Icon;
import net.minecraft.util.Vec3;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import apocalyptic.Apocalyptic;
import apocalyptic.ApocalypticCreativeTab;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class GasFlowing extends BlockFluid {

    int numAdjacentSources = 0;
    boolean isOptimalFlowDirection[] = new boolean[4];
    int flowCost[] = new int[4];

    public GasFlowing(int i, Material material) {
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

    @SideOnly(Side.CLIENT)
    @Override
    public int getBlockColor() {
        return 16777215;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public int colorMultiplier(IBlockAccess par1IBlockAccess, int par2, int par3, int par4) {
        if (this.blockMaterial != Material.gas) {
            return 16777215;
        } else {
            int var5 = 0;
            int var6 = 0;
            int var7 = 0;

            for (int var8 = -1; var8 <= 1; ++var8) {
                for (int var9 = -1; var9 <= 1; ++var9) {
                    int var10 = par1IBlockAccess.getBiomeGenForCoords(par2 + var9, par4 + var8).getWaterColorMultiplier();
                    var5 += (var10 & 16711680) >> 16;
                    var6 += (var10 & 65280) >> 8;
                    var7 += var10 & 255;
                }
            }

            return (var5 / 9 & 255) << 16 | (var6 / 9 & 255) << 8 | var7 / 9 & 255;
        }
    }

    @Override
    public int tickRate(World par1World) {
        return 40;
    }

    @Override
    public int getRenderBlockPass() {
        return 1;
    }
    
    private void updateFlow(World par1World, int par2, int par3, int par4) {
        int l = par1World.getBlockMetadata(par2, par3, par4);
        par1World.setBlock(par2, par3, par4, this.blockID + 1, l, 2);
    }

    /**
     * Old version 1.4.7
     * 
    private void updateFlow(World world, int i, int j, int k) {
        int l = world.getBlockMetadata(i, j, k);
        world.setBlock(i, j, k, this.blockID, l, 2);
        world.markBlockRangeForRenderUpdate(i, j, k, i, j, k);
        world.markBlockForUpdate(i, j, k);
    }
    */
    
    @Override
    public void updateTick(World par1World, int par2, int par3, int par4, Random par5Random)
    {
        int l = this.getFlowDecay(par1World, par2, par3, par4);
        byte b0 = 6;

        boolean flag = true;
        int i1;

        if (l > 0)
        {
            byte b1 = -100;
            this.numAdjacentSources = 0;
            int j1 = this.getSmallestFlowDecay(par1World, par2 - 1, par3, par4, b1);
            j1 = this.getSmallestFlowDecay(par1World, par2 + 1, par3, par4, j1);
            j1 = this.getSmallestFlowDecay(par1World, par2, par3, par4 - 1, j1);
            j1 = this.getSmallestFlowDecay(par1World, par2, par3, par4 + 1, j1);
            i1 = j1 + b0;

            if (i1 >= 8 || j1 < 0)
            {
                i1 = -1;
            }

            if (this.getFlowDecay(par1World, par2, par3 + 1, par4) >= 0)
            {
                int k1 = this.getFlowDecay(par1World, par2, par3 + 1, par4);

                if (k1 >= 8)
                {
                    i1 = k1;
                }
                else
                {
                    i1 = k1 + 8;
                }
            }

            if (i1 == l)
            {
                if (flag)
                {
                    this.updateFlow(par1World, par2, par3, par4);
                }
            }
            else
            {
                l = i1;

                if (i1 < 0)
                {
                    par1World.setBlockToAir(par2, par3, par4);
                }
                else
                {
                    par1World.setBlockMetadataWithNotify(par2, par3, par4, i1, 2);
                    par1World.scheduleBlockUpdate(par2, par3, par4, this.blockID, this.tickRate(par1World));
                    par1World.notifyBlocksOfNeighborChange(par2, par3, par4, this.blockID);
                }
            }
        }
        else
        {
            this.updateFlow(par1World, par2, par3, par4);
        }

        if (this.liquidCanDisplaceBlock(par1World, par2, par3 - 1, par4))
        {
            if (this.blockMaterial == Material.lava && par1World.getBlockMaterial(par2, par3 - 1, par4) == Material.water)
            {
                par1World.setBlock(par2, par3 - 1, par4, Block.stone.blockID);
                this.triggerLavaMixEffects(par1World, par2, par3 - 1, par4);
                return;
            }

            if (l >= 8)
            {
                this.flowIntoBlock(par1World, par2, par3 - 1, par4, l);
            }
            else
            {
                this.flowIntoBlock(par1World, par2, par3 - 1, par4, l + 8);
            }
        }
        else if (l >= 0 && (l == 0 || this.blockBlocksFlow(par1World, par2, par3 - 1, par4)))
        {
            boolean[] aboolean = this.getOptimalFlowDirections(par1World, par2, par3, par4);
            i1 = l + b0;

            if (l >= 8)
            {
                i1 = 1;
            }

            if (i1 >= 8)
            {
                return;
            }

            if (aboolean[0])
            {
                this.flowIntoBlock(par1World, par2 - 1, par3, par4, i1);
            }

            if (aboolean[1])
            {
                this.flowIntoBlock(par1World, par2 + 1, par3, par4, i1);
            }

            if (aboolean[2])
            {
                this.flowIntoBlock(par1World, par2, par3, par4 - 1, i1);
            }

            if (aboolean[3])
            {
                this.flowIntoBlock(par1World, par2, par3, par4 + 1, i1);
            }
        }
    }

    private void flowIntoBlock(World world, int i, int j, int k, int l) {
        if (liquidCanDisplaceBlock(world, i, j, k)) {
            int i1 = world.getBlockId(i, j, k);
            if (i1 > 0) {
                Block.blocksList[i1].dropBlockAsItem(world, i, j, k, world.getBlockMetadata(i, j, k), 0);
            }
            world.setBlock(i, j, k, blockID, l, 3);
        }
    }

    private boolean[] getOptimalFlowDirections(World world, int i, int j, int k) {
        for (int l = 0; l < 4; l++) {
            flowCost[l] = 0;
        }

        int i1 = flowCost[0];

        for (int k1 = 1; k1 < 4; k1++) {
            i1 = flowCost[k1];
        }

        for (int l1 = 0; l1 < 4; l1++) {
            isOptimalFlowDirection[l1] = flowCost[l1] == 0;
        }

        return isOptimalFlowDirection;
    }

    private Vec3 getFlowVector(IBlockAccess par1IBlockAccess, int par2, int par3, int par4) {
        Vec3 var5 = par1IBlockAccess.getWorldVec3Pool().getVecFromPool(0.0D, 0.0D, 0.0D);
        int var6 = this.getEffectiveFlowDecay(par1IBlockAccess, par2, par3, par4);

        for (int var7 = 0; var7 < 4; ++var7) {
            int var8 = par2;
            int var10 = par4;

            if (var7 == 0) {
                var8 = par2 - 1;
            }

            if (var7 == 1) {
                var10 = par4 - 1;
            }

            if (var7 == 2) {
                ++var8;
            }

            if (var7 == 3) {
                ++var10;
            }

            int var11 = this.getEffectiveFlowDecay(par1IBlockAccess, var8, par3, var10);
            int var12;

            if (var11 < 0) {
                if (!par1IBlockAccess.getBlockMaterial(var8, par3, var10).blocksMovement()) {
                    var11 = this.getEffectiveFlowDecay(par1IBlockAccess, var8, par3 - 1, var10);

                    if (var11 >= 0) {
                        var12 = var11 - (var6 - 8);
                        var5 = var5.addVector((double) ((var8 - par2) * var12), (double) ((par3 - par3) * var12), (double) ((var10 - par4) * var12));
                    }
                }
            } else if (var11 >= 0) {
                var12 = var11 - var6;
                var5 = var5.addVector((double) ((var8 - par2) * var12), (double) ((par3 - par3) * var12), (double) ((var10 - par4) * var12));
            }
        }

        if (par1IBlockAccess.getBlockMetadata(par2, par3, par4) >= 8) {
            boolean var13 = false;

            if (var13 || this.isBlockSolid(par1IBlockAccess, par2, par3, par4 - 1, 2)) {
                var13 = true;
            }

            if (var13 || this.isBlockSolid(par1IBlockAccess, par2, par3, par4 + 1, 3)) {
                var13 = true;
            }

            if (var13 || this.isBlockSolid(par1IBlockAccess, par2 - 1, par3, par4, 4)) {
                var13 = true;
            }

            if (var13 || this.isBlockSolid(par1IBlockAccess, par2 + 1, par3, par4, 5)) {
                var13 = true;
            }

            if (var13 || this.isBlockSolid(par1IBlockAccess, par2, par3 + 1, par4 - 1, 2)) {
                var13 = true;
            }

            if (var13 || this.isBlockSolid(par1IBlockAccess, par2, par3 + 1, par4 + 1, 3)) {
                var13 = true;
            }

            if (var13 || this.isBlockSolid(par1IBlockAccess, par2 - 1, par3 + 1, par4, 4)) {
                var13 = true;
            }

            if (var13 || this.isBlockSolid(par1IBlockAccess, par2 + 1, par3 + 1, par4, 5)) {
                var13 = true;
            }

            if (var13) {
                var5 = var5.normalize().addVector(0.0D, -6.0D, 0.0D);
            }
        }

        var5 = var5.normalize();
        return var5;
    }

    private boolean liquidCanDisplaceBlock(World world, int i, int j, int k) {
        Material material = world.getBlockMaterial(i, j, k);
        int block = world.getBlockId(i, j, k);
        if (material == blockMaterial) {
            return false;
        } else if (block == Apocalyptic.FilteredAir.blockID) {
            return false;
        } else {
            return !blockBlocksFlow(world, i, j, k);
        }
    }

    private boolean blockBlocksFlow(World world, int i, int j, int k) {
        int l = world.getBlockId(i, j, k);
        if (l == Block.waterStill.blockID || l == Block.waterMoving.blockID || l == Block.lavaStill.blockID || l == Block.lavaMoving.blockID
                || l == Block.redstoneWire.blockID || l == Block.redstoneRepeaterIdle.blockID || l == Block.torchRedstoneIdle.blockID || l == Block.torchRedstoneActive.blockID
                || l == Block.torchWood.blockID || l == Block.lever.blockID) {
            return true;
        }

        if (l == 0) {
            return false;
        }
        Material material = Block.blocksList[l].blockMaterial;
        return material.isSolid();
    }

    protected int getSmallestFlowDecay(World world, int i, int j, int k, int l) {
        int i1 = getFlowDecay(world, i, j, k);
        if (i1 < 0) {
            return l;
        }
        if (i1 >= 2) {
            i1 = 0;
        }
        return l >= 0 && i1 >= l ? l : i1;
    }

    @Override
    public void onBlockAdded(World world, int i, int j, int k) {
        if (world.getBlockId(i, j, k) == this.blockID) {
            world.scheduleBlockUpdate(i, j, k, this.blockID, tickRate(world));
        }
    }

    @Override
    public void onNeighborBlockChange(World par1World, int par2, int par3, int par4, int par5) {
        this.checkForStationary(par1World, par2, par3, par4);
    }

    private void checkForStationary(World par1World, int par2, int par3, int par4) {
        if (par1World.getBlockId(par2, par3, par4) == this.blockID) {
            if (this.blockID == Apocalyptic.FogMoving.blockID) {

                boolean var5 = true;

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

                if (!var5 || par1World.getBlockMaterial(par2 + 1, par3 + 1, par4) == Material.gas) {
                    var5 = false;
                }

                if (!var5 || par1World.getBlockMaterial(par2 - 1, par3 - 1, par4) == Material.gas) {
                    var5 = false;
                }

                if (var5) {
                    par1World.setBlock(par2, par3, par4, Apocalyptic.FogStill.blockID);
                }
            }
        }
    }

    @Override
    public boolean isBlockReplaceable(World world, int i, int j, int k) {
        return true;
    }

    @SideOnly(Side.CLIENT)
    public Icon getIcon(int par1, int par2) {
        return par1 != 0 && par1 != 1 ? this.theIcon[1] : this.theIcon[0];
    }

    @SideOnly(Side.CLIENT)
    public void registerIcons(IconRegister par1IconRegister) {
        this.theIcon = new Icon[]{par1IconRegister.registerIcon("apocalyptic:Gas"), par1IconRegister.registerIcon("apocalyptic:GasFlowing")};
    }
}
