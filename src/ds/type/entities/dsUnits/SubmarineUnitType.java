package ds.type.entities.dsUnits;

import arc.math.Mathf;
import ds.content.DSFx;
import ds.world.graphics.DSPal;
import ds.world.meta.DSEnv;
import ds.type.entities.DSUnitType;
import ds.type.entities.dsUnits.comp.UnderwaterUnitEngine;
import mindustry.entities.Effect;

public class SubmarineUnitType extends DSUnitType {
    public float bubblesInterval = 2;
    public Effect bubbleEffect = DSFx.dsMoveEffect;

    public SubmarineUnitType(String name) {
        super(name);
        envRequired = DSEnv.underwaterWarm;
        outlineColor = DSPal.dsUnitOutline;
        flying = true;
        engineOffset = 3;
        engineSize = 0;
        omniMovement = false;
    }

    public void setEngine(float x, float y, float rotation, boolean mirror){
        if(!mirror){
            engines.add(new UnderwaterUnitEngine(x, y, rotation));
        }else {
            for(int sign : Mathf.signs){
                engines.add(new UnderwaterUnitEngine(x * sign, y, rotation));
            }
        }
    }
}
