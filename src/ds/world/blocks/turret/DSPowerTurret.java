package ds.world.blocks.turret;

import arc.struct.ObjectMap;
import ds.world.meta.DSStatValues;
import mindustry.world.blocks.defense.turrets.PowerTurret;
import mindustry.world.meta.Stat;

public class DSPowerTurret extends PowerTurret {
    public DSPowerTurret(String name) {
        super(name);
    }

    @Override
    public void setStats(){
        super.setStats();
        stats.remove(Stat.ammo);
        stats.add(Stat.ammo, DSStatValues.ammo(ObjectMap.of(this, shootType)));
    }
}
