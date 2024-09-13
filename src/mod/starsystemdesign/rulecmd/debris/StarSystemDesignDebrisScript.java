package mod.starsystemdesign.rulecmd.debris;

import java.util.List;
import java.util.Map;

import org.lwjgl.input.Keyboard;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.InteractionDialogAPI;
import com.fs.starfarer.api.campaign.OptionPanelAPI;
import com.fs.starfarer.api.campaign.SectorEntityToken;
import com.fs.starfarer.api.campaign.rules.MemoryAPI;
import com.fs.starfarer.api.impl.campaign.rulecmd.BaseCommandPlugin;
import com.fs.starfarer.api.impl.campaign.terrain.AsteroidBeltTerrainPlugin;
import com.fs.starfarer.api.impl.campaign.terrain.DebrisFieldTerrainPlugin;
import com.fs.starfarer.api.impl.campaign.terrain.NebulaTerrainPlugin;
import com.fs.starfarer.api.util.Misc;
import com.fs.starfarer.campaign.CampaignTerrain;

public class StarSystemDesignDebrisScript extends BaseCommandPlugin{

    @Override
    public boolean execute(String ruleId, InteractionDialogAPI dialog, List<Misc.Token> params, Map<String, MemoryAPI> memoryMap) {
        OptionPanelAPI opts = dialog.getOptionPanel();
        opts.clearOptions();

        String arg = null;
        SectorEntityToken target = dialog.getInteractionTarget().getOrbitFocus();
        try{
            arg = params.get(0).getString(memoryMap);
        }catch(IndexOutOfBoundsException e){}

        if(arg == null){
            opts.addOption("Remove object", "StarSystemDesignDebrisRemoveOption");
            opts.setEnabled("StarSystemDesignDebrisRemoveOption", false);

            if(target.getClass() == CampaignTerrain.class){
                // terrain
                CampaignTerrain terrain = (CampaignTerrain) target;
                if(terrain.getPlugin().getClass() == DebrisFieldTerrainPlugin.class){
                    DebrisFieldTerrainPlugin debris = (DebrisFieldTerrainPlugin)terrain.getPlugin();
                    if(debris.isScavenged()) opts.setEnabled("StarSystemDesignDebrisRemoveOption", true);
                } else if(terrain.getPlugin().getClass() == NebulaTerrainPlugin.class){
                    // AsteroidBelt will not removed because of its sprite will still exist
                    opts.setEnabled("StarSystemDesignDebrisRemoveOption", true);
                }
            }else{
                opts.setEnabled("StarSystemDesignDebrisRemoveOption", true);
            }
            opts.addOption("Back", "StarSystemDesignDebrisBackOption");
            opts.setShortcut("StarSystemDesignDebrisBackOption", Keyboard.KEY_ESCAPE, false, false, false, false);
        }else{
            switch(arg){
                case "remove":{
                    if(target.getClass() == CampaignTerrain.class){
                        // terrain
                        CampaignTerrain terrain = (CampaignTerrain) target;
                        if(terrain.getPlugin().getClass() == DebrisFieldTerrainPlugin.class){
                            DebrisFieldTerrainPlugin debris = (DebrisFieldTerrainPlugin)terrain.getPlugin();
                            debris.getParams().lastsDays = 1f;
                            dialog.dismiss();
                        }
                        else {
                            Global.getSector().getPlayerFleet().getStarSystem().removeEntity(target);
                            dialog.dismiss();
                        }
                    } else {
                        // Non-terrain, like planets
                        Global.getSector().getPlayerFleet().getStarSystem().removeEntity(target);
                        dialog.dismiss();
                    }
                }
            }
        }
        return true;
    }
}
