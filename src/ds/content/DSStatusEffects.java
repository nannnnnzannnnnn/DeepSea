package ds.content;

import arc.struct.Seq;
import ds.type.entities.dsUnits.SubmarineUnitType;
import mindustry.entities.effect.ParticleEffect;
import mindustry.entities.units.StatusEntry;
import mindustry.gen.Unit;
import mindustry.graphics.Pal;
import mindustry.type.StatusEffect;

public class DSStatusEffects {
    public static StatusEffect waterLeak;
    public static void load(){
        waterLeak = new dsStatusEffect("water-leak"){{
            effect = new ParticleEffect(){{
                colorFrom = Pal.water;
                colorTo = Pal.water;
                sizeFrom = 1;
                sizeTo = 0;
                lifetime = 30;
            }};
        }
            @Override
            public void update(Unit unit, StatusEntry entry){
                super.update(unit, entry);
                if(unit.type instanceof SubmarineUnitType){
                    unit.damage(0.2f * (unit.health / (unit.maxHealth / 2)));
                    unit.speedMultiplier = Math.max(0.25f, unit.health / unit.maxHealth);
                } else unit.damage(0.025f * (unit.health / (unit.maxHealth / 2)));
            }
        };
    }

    public static class dsStatusEffect extends StatusEffect {
        public Seq<StatusEffect> override = new Seq<>();
        public dsStatusEffect(String name) {
            super(name);
            outline = false;
        }

        @Override
        public void update(Unit unit, StatusEntry entry) {
            super.update(unit, entry);
            override.each(unit::unapply);
        }

    }
}
