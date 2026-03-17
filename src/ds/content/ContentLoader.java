package ds.content;

import ds.content.blocks.DSBlocksLoader;
import ds.content.items.DSItemLoader;
import ds.content.liquids.PiLiquids;
import ds.content.planets.DSPlanets;
import ds.content.planets.PiSectors;
import ds.content.units.PiUnits;

public class ContentLoader {
    public static void load(){
        DSStatusEffects.load();
        DSAttributes.load();
        SchematicsLoader.load();
        DSSounds.load();
        DSItemLoader.load();

        PiLiquids.load();
        PiUnits.loadUnits();
        DSBlocksLoader.load();

        //end load
        DSPlanets.loadContent();
        PiSectors.load();
        Pi312TechTree.load();
    }
}
