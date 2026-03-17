package ds;

import arc.Core;
import mindustry.Vars;

public class DSSetting {
    public static void init(){
        Vars.ui.settings.addCategory("Deep sea",  root ->{
            root.checkPref("@setting.onlyDeepseaMusic", false);
        });
    }

    public static boolean getOnlyModMus(){
        return Core.settings.getBool("onlyDeepseaMusic", false);
    }
}
