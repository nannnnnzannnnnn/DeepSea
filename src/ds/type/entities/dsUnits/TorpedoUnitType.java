package ds.type.entities.dsUnits;

import ds.content.DSFx;
import ds.world.meta.DSEnv;
import mindustry.entities.abilities.MoveEffectAbility;
import mindustry.gen.Sounds;
import mindustry.graphics.Pal;
import mindustry.type.unit.MissileUnitType;

public class TorpedoUnitType extends MissileUnitType {
    public TorpedoUnitType(String name) {
        super(name);
        health = 40;
        envRequired = DSEnv.underwaterWarm;
        trailLength = 0;
        engineSize = 0;
        lowAltitude = true;
        engineOffset = 0;
        loopSound = Sounds.none;
        outlineColor = Pal.darkOutline;
        abilities.add(new MoveEffectAbility(){{
            effect = DSFx.torpedoTrail;
            minVelocity = 0.05f;
        }});
    }
}
