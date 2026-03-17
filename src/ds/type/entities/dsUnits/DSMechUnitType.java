package ds.type.entities.dsUnits;

import ds.type.entities.DSUnitType;
import mindustry.gen.MechUnit;

public class DSMechUnitType extends DSUnitType {
    public DSMechUnitType(String name) {
        super(name);
        constructor = MechUnit::create;
    }
}
