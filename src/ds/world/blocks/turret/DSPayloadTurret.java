package ds.world.blocks.turret;

import mindustry.content.Fx;
import mindustry.world.blocks.defense.turrets.PayloadAmmoTurret;

public class DSPayloadTurret extends PayloadAmmoTurret {
    public DSPayloadTurret(String name) {
        super(name);
        maxAmmo = 1;
        shootEffect = smokeEffect = Fx.none;
        outlinedIcon = 3;
    }
}
