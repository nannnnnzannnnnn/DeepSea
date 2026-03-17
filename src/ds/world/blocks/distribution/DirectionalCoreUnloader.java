package ds.world.blocks.distribution;

import arc.struct.Seq;
import mindustry.gen.Building;
import mindustry.type.Item;
import mindustry.world.Block;
import mindustry.world.blocks.distribution.DirectionalUnloader;
import mindustry.world.blocks.storage.CoreBlock;
import mindustry.world.blocks.storage.StorageBlock;

import static mindustry.Vars.content;

public class DirectionalCoreUnloader extends DirectionalUnloader {
    public DirectionalCoreUnloader(String name) {
        super(name);
        allowCoreUnload = true;
    }
    public Seq<Block> exceptions = new Seq<>();
    public class DirectionalCoreUnloaderBuild extends DirectionalUnloaderBuild{

        @Override
        public void updateTile(){
            if((unloadTimer += edelta()) >= speed){
                Building front = front(), back = back();

                if(front != null && back != null && back.items != null && front.team == team && back.team == team && back.canUnload() && ( checkExceptions(back.block, exceptions) ||(back instanceof CoreBlock.CoreBuild || (back instanceof StorageBlock.StorageBuild sb && sb.linkedCore != null)))){
                    if(unloadItem == null){
                        var itemseq = content.items();
                        int itemc = itemseq.size;
                        for(int i = 0; i < itemc; i++){
                            Item item = itemseq.get((i + offset) % itemc);
                            if(back.items.has(item) && front.acceptItem(this, item)){
                                front.handleItem(this, item);
                                back.items.remove(item, 1);
                                back.itemTaken(item);
                                offset = item.id + 1;
                                break;
                            }
                        }
                    }else if(back.items.has(unloadItem) && front.acceptItem(this, unloadItem)){
                        front.handleItem(this, unloadItem);
                        back.items.remove(unloadItem, 1);
                        back.itemTaken(unloadItem);
                    }
                }

                unloadTimer %= speed;
            }
        }

        protected boolean checkExceptions(Block back,Seq<Block>list){
            for(Block b : list){
                if(back == b) return  true;
            }
            return false;
        }
    }
}
