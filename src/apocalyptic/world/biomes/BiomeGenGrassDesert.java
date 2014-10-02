package apocalyptic.world.biomes;

import net.minecraft.entity.passive.EntityChicken;
import net.minecraft.entity.passive.EntityCow;
import net.minecraft.entity.passive.EntityPig;
import net.minecraft.entity.passive.EntitySheep;
import net.minecraft.world.biome.BiomeGenDesert;
import net.minecraft.world.biome.SpawnListEntry;

public class BiomeGenGrassDesert extends BiomeGenDesert {
    
    public BiomeGenGrassDesert (int par1) {
        super(par1);
        this.spawnableCreatureList.add(new SpawnListEntry(EntitySheep.class, 10, 4, 4));
        this.spawnableCreatureList.add(new SpawnListEntry(EntityPig.class, 10, 4, 4));
        this.spawnableCreatureList.add(new SpawnListEntry(EntityChicken.class, 12, 4, 4));
        this.spawnableCreatureList.add(new SpawnListEntry(EntityCow.class, 8, 4, 4));
    }
}
