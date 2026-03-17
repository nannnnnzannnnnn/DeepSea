package ds.type;

import arc.graphics.Color;
import arc.struct.Seq;
import mindustry.type.Item;

public class FluxLiquid extends DSLiquid{

    public Seq<Item> containedItems = new Seq<>();

    public FluxLiquid(String name, Color color) {
        super(name, color);
    }
    public FluxLiquid(String name){
        super(name);
    }
}
