package apocalyptic.crossmod;

public class CrossmodIndustrialCraft2 {
	private boolean crossed;

	public CrossmodIndustrialCraft2() {
        try
        {
            Class.forName("ic2.api.IEnergyStorage", false, this.getClass().getClassLoader());
            crossed = true;
        } catch (ClassNotFoundException e)
        {
            crossed = false;
        }
	}
}
