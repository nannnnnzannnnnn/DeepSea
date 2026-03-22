package ds.type.entities.comp;

import arc.struct.Seq;
import mindustry.gen.Unit;

//A class that need for store some data
public abstract class HarpoonBulletComp {
    public float BulletReturnData;
    public boolean BulletReturn;
    public Seq<Unit> targets = new Seq<>();
}