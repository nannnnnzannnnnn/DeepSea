package ds.world.blocks.power;

import arc.graphics.Color;
import arc.math.Mathf;
import mindustry.graphics.Drawf;
import mindustry.world.blocks.power.ThermalGenerator;

public class AdvancedThermalGenerator extends ThermalGenerator {
    public float lightRadius = 32;
    public float lightOpacity = 0.4f;
    public float lightOffsetScl = 1;
    public Color lightColor = Color.scarlet;

    public AdvancedThermalGenerator(String name) {
        super(name);
    }

    public class AdvancedThermalGeneratorBuild extends ThermalGeneratorBuild{

        @Override
        public void drawLight(){
            Drawf.light(x, y, (size * 8 + lightRadius + Mathf.absin(10f, 4f)) * lightOffsetScl * Math.min(productionEfficiency, 2f), lightColor, lightOpacity);
        }
    }
}
