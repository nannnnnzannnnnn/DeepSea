package ds.content.items;

import arc.struct.Seq;
import mindustry.type.Item;

public class DSItemLoader {
    public static Seq<Item>modItems = new Seq<>();
    public static void load(){
        PiItems.load();

        modItems.add(PiItems.piItems);
    }
}
