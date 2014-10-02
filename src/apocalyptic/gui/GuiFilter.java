package apocalyptic.gui;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.StatCollector;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import apocalyptic.container.ContainerFilter;
import apocalyptic.tile.TileEntityFilter;

@SideOnly(Side.CLIENT)
public class GuiFilter extends GuiContainer {
	public TileEntityFilter tileentity;

    public GuiFilter(InventoryPlayer player_inventory, TileEntityFilter tile_entity){
            super(new ContainerFilter(tile_entity, player_inventory));
            this.tileentity = tile_entity;
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int i, int j){
            fontRenderer.drawString(StatCollector.translateToLocal("container.inventory"), 8, ySize - 92 , 0xffffff);
            fontRenderer.drawString("Filter", 70, 6, 0xffffff);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float f, int i, int j){
            GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);

            this.mc.renderEngine.bindTexture("/apocalyptic/sprites/GUIFilter.png");

            int x = (width - xSize) / 2;

            int y = (height - ySize) / 2;

            this.drawTexturedModalRect(x, y, 0, 0, xSize, ySize);

            if (this.tileentity.electricityStored > 0) {
            	double cap = this.tileentity.getCapacity();
            	double els = this.tileentity.electricityStored;
            	int summ = (int) (els/cap * 20);
            	if (summ > 20) {
            		summ = 20;
            	}
            	this.drawTexturedModalRect(x + 26, y + 35, 176, 0, 14, summ);
            }
    }
}
