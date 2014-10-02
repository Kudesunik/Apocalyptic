package apocalyptic.crossmod;

public class CrossmodInvTweaks {
	public boolean crossed;

	public CrossmodInvTweaks()
    {
        try
        {
            Class.forName("invtweaks.InvTweaks", false, this.getClass().getClassLoader());
            crossed = true;
        } catch (ClassNotFoundException e)
        {
        	crossed = false;
        }
    }

	public boolean isAvailable() {
		return this.crossed;
	}
}
