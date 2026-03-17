package ds.content;

import mindustry.world.meta.Attribute;

public class DSAttributes {
    public static Attribute sulfuric, geyser;

    public static void load(){{
        sulfuric = Attribute.add("sulfuric");
        geyser = Attribute.add("geyser");
    }}
}
