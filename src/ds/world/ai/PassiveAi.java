package ds.world.ai;

import arc.math.Mathf;
import arc.math.geom.Vec2;
import arc.util.Time;
import mindustry.entities.units.AIController;

import static mindustry.Vars.*;

public class PassiveAi extends AIController {
    private final float targetMoveInterval = 300f;
    private final Vec2 targetCoord = new Vec2();
    private float switchInterval = 0;
    @Override
    public void updateMovement(){
        if(targetCoord.x == 0 & targetCoord.y == 0) findNewTargetPos();
        updateTimer();
        if(checkTarget()){
            findNewTargetPos();
        }
        moveToPos();
    }
    private void updateTimer(){
        if(switchInterval < targetMoveInterval) switchInterval += Time.delta;
    }
    private boolean checkTarget(){
        return (switchInterval >= targetMoveInterval);
    }
    private void findNewTargetPos() {
        if (unit == null)return;
        switchInterval = Mathf.random(targetMoveInterval * 0.25f, 0);
        targetCoord.set(
                unit.x() + Mathf.random(-50, 50),
                unit.y() + Mathf.random(-50, 50)
        );
    }
    private void moveToPos(){
        moveTo(targetCoord, 3);
    }
}
