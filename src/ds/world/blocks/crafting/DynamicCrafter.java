package ds.world.blocks.crafting;

import arc.Core;
import arc.graphics.Color;
import arc.graphics.g2d.TextureRegion;
import arc.math.Mathf;
import arc.scene.ui.layout.Table;
import arc.struct.EnumSet;
import arc.struct.Seq;
import arc.util.Eachable;
import arc.util.Strings;
import arc.util.Time;
import arc.util.io.Reads;
import arc.util.io.Writes;
import ds.world.consumers.RecipeIO;
import ds.world.meta.DSStats;
import mindustry.Vars;
import mindustry.content.Fx;
import mindustry.entities.Effect;
import mindustry.entities.units.BuildPlan;
import mindustry.gen.Building;
import mindustry.gen.Icon;
import mindustry.graphics.Pal;
import mindustry.type.Item;
import mindustry.type.ItemStack;
import mindustry.type.Liquid;
import mindustry.type.LiquidStack;
import mindustry.ui.Styles;
import mindustry.world.Block;
import mindustry.world.consumers.Consume;
import mindustry.world.draw.DrawBlock;
import mindustry.world.draw.DrawDefault;
import mindustry.world.meta.BlockFlag;

//A LOT of code is stolen from Carpe-Diem. Sorry :'(
public class DynamicCrafter extends Block {

    public DrawBlock drawer = new DrawDefault();
    public Seq<RecipeIO> recipes = new Seq<>();
    public Effect craftEffect = Fx.none;
    public Effect updateEffect = Fx.smeltsmoke;
    public float updateEffectChance = 0.01f;
    public float warmupSpeed = 0.019f;

    public DynamicCrafter(String name) {
        super(name);
        configurable = true;
        update = true;
        solid = true;
        hasItems = true;
        sync = true;
        flags = EnumSet.of(BlockFlag.factory);


        config(Integer.class, (DynamicCraferBuild crafter, Integer recipeIDX) ->{
            if(crafter.recipeIDX < 0) crafter.recipeIDX = recipeIDX;
        });
    }

    public void addRecipes(RecipeIO...r){
        recipes.add(r);
    }

    @Override
    public void setBars(){
        super.setBars();
        removeBar("items");
    }

    @Override
    public void init(){
        for(RecipeIO r : recipes){
            r.apply(this);
        }

        for(RecipeIO r : recipes) r.init();

        consume(new Consume(){

            @Override
            public void apply(Block block) {
                boolean[] prevItemFilter = block.itemFilter;
                boolean[] prevLiquidFilter = block.liquidFilter;

                for (RecipeIO recipe : recipes) {
                    block.itemFilter = new boolean[Vars.content.items().size];
                    block.liquidFilter = new boolean[Vars.content.liquids().size];

                    for (Consume consume : recipe.consumers) {
                        consume.apply(block);
                    }

                    recipe.itemFilter = block.itemFilter;
                    recipe.liquidFilter = block.liquidFilter;
                }

                block.itemFilter = prevItemFilter;
                block.liquidFilter = prevLiquidFilter;
            }

            @Override
            public void trigger(Building build) {
                if (build instanceof DynamicCraferBuild crafter && crafter.getCurRecipe() != null) {
                    for (Consume consume : crafter.getCurRecipe().consumers) {
                        consume.trigger(crafter);
                    }
                }
            }

            @Override
            public float efficiency(Building build) {
                if (build instanceof DynamicCraferBuild crafter && crafter.getCurRecipe() != null) {
                    float minEfficiency = 1f;

                    for (Consume consume : crafter.getCurRecipe().consumers) {
                        minEfficiency = Math.min(minEfficiency, consume.efficiency(crafter));
                    }

                    return minEfficiency;
                } else {
                    return 0f;
                }
            }

            @Override
            public void build(Building build, Table table) {
                RecipeIO[] current = {null};

                table.table(cont -> {
                    table.update(() -> {
                        if (build instanceof DynamicCraferBuild crafter) {
                            RecipeIO recipe = crafter.getCurRecipe();
                            if (current[0] != recipe) {
                                current[0] = recipe;
                                rebuild(build, cont, current[0]);
                            }
                        }
                    });

                    rebuild(build, cont, current[0]);
                });
            }

            public void rebuild(Building build, Table table, RecipeIO recipe) {
                table.clear();

                if (recipe != null) {
                    for (Consume consume : recipe.consumers) {
                        consume.build(build, table);
                    }
                }
            }
        });

        super.init();
    }

    @Override
    public void setStats() {
        super.setStats();

        stats.add(DSStats.recipes, table -> {
            table.row();
            for (RecipeIO recipe : recipes) {
                recipe.display(table);
                table.row();
            }
        });
    }

    @Override
    public void drawPlanRegion(BuildPlan plan, Eachable<BuildPlan> list) {
        drawer.drawPlan(this, plan, list);
    }

    @Override
    public void getRegionsToOutline(Seq<TextureRegion> out) {
        drawer.getRegionsToOutline(this, out);
    }

    @Override
    public TextureRegion[] icons() {
        return drawer.finalIcons(this);
    }


    @Override
    public void load() {
        super.load();

        drawer.load(this);
    }

    public class DynamicCraferBuild extends Building{
        public int recipeIDX = 1;
        public float progress = 0;
        public float totalProgress = 0;
        public float warmup = 0;

        @Override
        public void draw(){
            drawer.draw(this);
        }


        @Override
        public float efficiencyScale() {
            float multiplier = 1f;

            for (Consume consume : consumers) {
                multiplier *= consume.efficiencyMultiplier(this);
            }
            return multiplier;
        }

        public RecipeIO getCurRecipe(){
            if (recipeIDX < 0 || recipeIDX >= recipes.size || !recipes.get(recipeIDX).recipeIsValid()) {
                return null;
            }
            return recipes.get(recipeIDX);
        }

        public void setRecipe(int idx){
            if(recipes.get(idx).recipeIsValid()) {
                progress = 0;
                recipeIDX = idx;
            }
        }

        @Override
        public void updateTile(){
            RecipeIO recipe = getCurRecipe();
            if(recipe != null){
                if(efficiency > 0){
                    progress += getProgressIncrease(recipe.recipeTime);
                    warmup = Mathf.approachDelta(warmup, warmupTarget(), warmupSpeed);

                    recipe.update(this);

                    if (wasVisible && Mathf.chanceDelta(updateEffectChance)) {
                        updateEffect.at(x + Mathf.range(block.size * 4f), y + Mathf.range(block.size * 4));
                    }

                }

                totalProgress += warmup * Time.delta;

                if (progress >= 1f) {
                    craft();
                }

                dumpOutputs();
            }
            if (items != null) {
                if (timer(timerDump, dumpTime / timeScale)) {
                    dump();
                }
            }
            if (liquids != null) {
                dumpLiquid(liquids.current());
            }
        }

        public void craft() {
            RecipeIO recipe = getCurRecipe();
            consume();

            if (recipe != null) {
                recipe.craft(this);
            }

            if (wasVisible) {
                craftEffect.at(x, y);
            }

            progress %= 1f;
        }



        public void dumpOutputs() {
            RecipeIO currentRecipe = getCurRecipe();
            if (currentRecipe != null) {
                currentRecipe.dumpOutputs(this);

                if (timer(timerDump, dumpTime / timeScale)) {
                    currentRecipe.dumpTimedOutputs(this);
                }
            }
        }

        @Override
        public boolean shouldConsume() {
            RecipeIO currentRecipe = getCurRecipe();
            return currentRecipe != null && currentRecipe.shouldConsume(this);
        }

        @Override
        public boolean acceptItem(Building source, Item item) {
            if (!hasItems) return false;

            RecipeIO currentRecipe = getCurRecipe();
            boolean recipeConsumes = false;

            if (currentRecipe != null) {
                recipeConsumes = currentRecipe.consumesItem(item);
            }
            return (consumesItem(item) || recipeConsumes) && items.get(item) < getMaximumAccepted(item);
        }

        @Override
        public boolean acceptLiquid(Building source, Liquid liquid) {
            if (!hasLiquids) return false;

            RecipeIO currentRecipe = getCurRecipe();
            boolean recipeConsumes = false;

            if (configurable) {
                if (currentRecipe != null) {
                    recipeConsumes = currentRecipe.consumesLiquid(liquid);
                }
            } else {
                for (RecipeIO recipe : recipes) {
                    if (recipe.consumesLiquid(liquid)) {
                        recipeConsumes = true;
                        break;
                    }
                }
            }

            return (consumesLiquid(liquid) || recipeConsumes);
        }

        @Override
        public float totalProgress() {
            return totalProgress;
        }

        @Override
        public float warmup(){
            return warmup;
        }

        @Override
        public float progress(){
            return Mathf.clamp(progress);
        }

        public float warmupTarget(){
            return 1f;
        }

        @Override
        public boolean shouldAmbientSound(){
            return efficiency > 0;
        }

        @Override
        public void buildConfiguration(Table table){
            table.table(Styles.grayPanel, t ->{
                t.image().color(Pal.gray).height(4).growX().row();
                t.table(buttons ->{
                    for(int i = 0; i < recipes.size; i++){
                        final int recipeIndex = i;
                        RecipeIO recipe = recipes.get(i);
                        if(recipe == null) continue;

                        boolean isCurrent = recipeIDX == recipeIndex;

                        buttons.button(b ->{
                            b.center();
                            if(recipe.recipeIsValid()) {
                                b.table(content -> {
                                    content.stack(
                                            new Table(td -> {
                                                td.table(input -> addRecipeResources(input, recipe))
                                                        .left().grow().pad(10f);

                                                td.table(arrow -> {
                                                    arrow.image(Icon.right).color(Pal.darkishGray).size(20f);
                                                }).pad(10f);
                                                td.add(Core.bundle.format("ui.craftTime", Strings.autoFixed(recipe.recipeTime / 60, 3)));

                                                td.table(output -> addOutputResources(output, recipe))
                                                        .right().grow().pad(10f);
                                            })
                                    ).growX().row();
                                }).grow();
                                b.update(() -> b.setChecked(isCurrent));
                            } else b.image(Icon.cancel).color(Color.white).size(20);
                        }, Styles.togglet, () ->{
                            if(!isCurrent & recipes.get(recipeIndex).recipeIsValid()) {
                                setRecipe(recipeIndex);
                            }
                            deselect();
                        }).growX().row();
                    }
                }).pad(4);
            });
        }
        // Вспомогательный метод для добавления ресурсов рецепта
        private void addRecipeResources(Table table, RecipeIO recipe){
                if(recipe.input != null){
                    for(ItemStack input : recipe.input){
                        if(input == null || input.item == null) continue;
                        addResourceElement(table, input.item.uiIcon,
                                Strings.autoFixed(input.amount / (recipe.recipeTime / 60), 3) + "/s", false);
                    }
                }
                if(recipe.liquidIn != null){
                    for(LiquidStack liquid : recipe.liquidIn){
                        if(liquid == null || liquid.liquid == null) continue;
                        addResourceElement(table, liquid.liquid.uiIcon,
                                Strings.autoFixed(liquid.amount * 60, 3) + "/s", false);
                    }
                }
        }
        private void addOutputResources(Table table, RecipeIO recipe){
            if(recipe.output != null){
                for(ItemStack output : recipe.output){
                    if(output == null || output.item == null) continue;
                    addResourceElement(table, output.item.uiIcon,
                            Strings.autoFixed(output.amount / (recipe.recipeTime / 60), 3) + "/s", true);
                }
            }
            if(recipe.liquidOut != null){
                for(LiquidStack liquid : recipe.liquidOut){
                    if(liquid == null || liquid.liquid == null) continue;
                    addResourceElement(table, liquid.liquid.uiIcon,
                            Strings.autoFixed(liquid.amount * 60, 3) + "/s", true);
                }
            }
        }

        private void addResourceElement(Table table, TextureRegion icon, String text, boolean isOutput){
            table.table(element -> {
                element.image(icon).size(20).padRight(2);
                element.add(text).color(Color.lightGray);
            }).padRight(3);
        }

        @Override
        public void write(Writes write) {
            super.write(write);

            write.i(recipeIDX);
            write.f(progress);

        }

        @Override
        public void read(Reads read, byte revision) {
            super.read(read, revision);

            recipeIDX = read.i();
            progress = read.f();
        }

    }
}
