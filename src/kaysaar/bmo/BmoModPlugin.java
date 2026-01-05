package kaysaar.bmo;

import com.fs.starfarer.api.BaseModPlugin;
import com.fs.starfarer.api.Global;
import kaysaar.bmo.buildingmenu.BuildingUITracker;
import kaysaar.bmo.buildingmenu.industrytags.IndustryTagManager;
import kaysaar.bmo.buildingmenu.tooltipinjector.ModIndustryTooltipInjector;
import kaysaar.bmo.buildingmenu.upgradepaths.CustomUpgradePath;
import kaysaar.bmo.buildingmenu.upgradepaths.UpgradePathManager;
import kaysaar.bmo.buildingmenu.upgradequeue.UpdateQueueMainManager;
import org.lwjgl.util.vector.Vector2f;

import java.util.LinkedHashMap;

import static ashlib.data.plugins.AshLibPlugin.fontInsigniaMedium;

public class BmoModPlugin extends BaseModPlugin {

    @Override
    public void onApplicationLoad() throws Exception {
        super.onApplicationLoad();

        // Test that the .jar is loaded and working, using the most obnoxious way possible.
        Global.getSettings().loadFont(fontInsigniaMedium);

    }

    @Override
    public void onGameLoad(boolean newGame) {
        Global.getSector().addTransientScript(new BuildingUITracker());
        Global.getSector().addTransientScript(new UpdateQueueMainManager());
        Global.getSector().getListenerManager().addListener(new ModIndustryTooltipInjector(),true);
        UpgradePathManager.getInstance().rePopulate();
        //Note : Should be done from AoTD side, but i dont wanna make sudden update
        if(Global.getSettings().getModManager().isModEnabled("aotd_vok")){


        }
    }

    // You can add more methods from ModPlugin here. Press Control-O in IntelliJ to see options.
}
