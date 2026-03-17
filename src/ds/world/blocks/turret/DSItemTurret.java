package ds.world.blocks.turret;

import ds.world.meta.DSStatValues;
import mindustry.world.blocks.defense.turrets.ItemTurret;
import mindustry.world.meta.Stat;

public class DSItemTurret extends ItemTurret {
    public DSItemTurret(String name) {
        super(name);
    }

    @Override
    public void setStats(){
        super.setStats();
        stats.remove(Stat.ammo);
        stats.add(Stat.ammo, DSStatValues.ammo(ammoTypes));
    }
}
