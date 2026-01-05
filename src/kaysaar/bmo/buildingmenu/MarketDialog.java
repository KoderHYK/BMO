package kaysaar.bmo.buildingmenu;

import ashlib.data.plugins.ui.models.BasePopUpDialog;
import ashlib.data.plugins.ui.models.TrapezoidButtonDetector;
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.CampaignUIAPI;
import com.fs.starfarer.api.campaign.CoreUIAPI;
import com.fs.starfarer.api.campaign.InteractionDialogAPI;
import com.fs.starfarer.api.campaign.econ.Industry;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.impl.campaign.econ.impl.BaseIndustry;
import com.fs.starfarer.api.input.InputEventAPI;
import com.fs.starfarer.api.ui.*;
import com.fs.starfarer.api.util.IntervalUtil;
import com.fs.starfarer.api.util.Misc;
import com.fs.starfarer.api.util.MutableValue;
import kaysaar.bmo.buildingmenu.upgradequeue.UpdateQueueMainManager;
import kaysaar.bmo.buildingmenu.upgradequeue.UpdateQueueMarketManager;
import org.lwjgl.input.Keyboard;

import java.awt.*;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.util.List;
import java.util.Set;

public class MarketDialog extends BasePopUpDialog {

    private static class ReflectionUtilis {
        // Code taken and modified from Grand Colonies
        private static final Class<?> fieldClass;
        private static final MethodHandles.Lookup lookup = MethodHandles.lookup();
        private static final java.lang.invoke.MethodHandle setFieldHandle;
        private static final java.lang.invoke.MethodHandle getFieldHandle;
        private static final java.lang.invoke.MethodHandle getFieldNameHandle;
        private static final java.lang.invoke.MethodHandle setFieldAccessibleHandle;
        private static final Class<?> methodClass;
        private static final java.lang.invoke.MethodHandle getMethodNameHandle;
        private static final java.lang.invoke.MethodHandle invokeMethodHandle;
        private static final java.lang.invoke.MethodHandle setMethodAccessable;

        static {
            try {
                fieldClass = Class.forName("java.lang.reflect.Field", false, Class.class.getClassLoader());
                setFieldHandle = lookup.findVirtual(fieldClass, "set", MethodType.methodType(Void.TYPE, Object.class, Object.class));
                getFieldHandle = lookup.findVirtual(fieldClass, "get", MethodType.methodType(Object.class, Object.class));
                getFieldNameHandle = lookup.findVirtual(fieldClass, "getName", MethodType.methodType(String.class));
                setFieldAccessibleHandle = lookup.findVirtual(fieldClass, "setAccessible", MethodType.methodType(Void.TYPE, boolean.class));

                methodClass = Class.forName("java.lang.reflect.Method", false, Class.class.getClassLoader());
                getMethodNameHandle = lookup.findVirtual(methodClass, "getName", MethodType.methodType(String.class));
                invokeMethodHandle = lookup.findVirtual(methodClass, "invoke", MethodType.methodType(Object.class, Object.class, Object[].class));
                setMethodAccessable = lookup.findVirtual(methodClass, "setAccessible", MethodType.methodType(Void.TYPE, boolean.class));
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        public static Object getPrivateVariable(String fieldName, Object instanceToGetFrom) {
            try {
                Class<?> instances = instanceToGetFrom.getClass();
                while (instances != null) {
                    for (Object obj : instances.getDeclaredFields()) {
                        setFieldAccessibleHandle.invoke(obj, true);
                        String name = (String) getFieldNameHandle.invoke(obj);
                        if (name.equals(fieldName)) {
                            return getFieldHandle.invoke(obj, instanceToGetFrom);
                        }
                    }
                    for (Object obj : instances.getFields()) {
                        setFieldAccessibleHandle.invoke(obj, true);
                        String name = (String) getFieldNameHandle.invoke(obj);
                        if (name.equals(fieldName)) {
                            return getFieldHandle.invoke(obj, instanceToGetFrom);
                        }
                    }
                    instances = instances.getSuperclass();
                }
                return null;
            } catch (Throwable e) {
                throw new RuntimeException(e);
            }
        }

        public static Object getPrivateVariableFromSuperClass(String fieldName, Object instanceToGetFrom) {
            try {
                Class<?> instances = instanceToGetFrom.getClass();
                while (instances != null) {
                    for (Object obj : instances.getDeclaredFields()) {
                        setFieldAccessibleHandle.invoke(obj, true);
                        String name = (String) getFieldNameHandle.invoke(obj);
                        if (name.equals(fieldName)) {
                            return getFieldHandle.invoke(obj, instanceToGetFrom);
                        }
                    }
                    for (Object obj : instances.getFields()) {
                        setFieldAccessibleHandle.invoke(obj, true);
                        String name = (String) getFieldNameHandle.invoke(obj);
                        if (name.equals(fieldName)) {
                            return getFieldHandle.invoke(obj, instanceToGetFrom);
                        }
                    }
                    instances = instances.getSuperclass();
                }
                return null;
            } catch (Throwable e) {
                throw new RuntimeException(e);
            }
        }

        public static void setPrivateVariableFromSuperclass(String fieldName, Object instanceToModify, Object newValue) {
            try {
                Class<?> instances = instanceToModify.getClass();
                while (instances != null) {
                    for (Object  obj : instances.getDeclaredFields()) {
                        setFieldAccessibleHandle.invoke(obj, true);
                        String name = (String) getFieldNameHandle.invoke(obj);
                        if (name.equals(fieldName)) {
                            setFieldHandle.invoke(obj, instanceToModify, newValue);
                            return;
                        }
                    }
                    for (Object obj : instances.getFields()) {
                        setFieldAccessibleHandle.invoke(obj, true);
                        String name = (String) getFieldNameHandle.invoke(obj);
                        if (name.equals(fieldName)) {
                            setFieldHandle.invoke(obj, instanceToModify, newValue);
                            return;
                        }
                    }
                    instances = instances.getSuperclass();
                }
            } catch (Throwable e) {
                throw new RuntimeException(e);
            }
        }

        public static boolean hasMethodOfName(String name, Object instance) {
            try {
                for (Object method : instance.getClass().getMethods()) {
                    if (getMethodNameHandle.invoke(method).equals(name)) {
                        return true;
                    }
                }
                return false;
            } catch (Throwable e) {
                return false;
            }
        }

        public static Object invokeMethod(String methodName, Object instance, Object... arguments) {
            try {
                Object method = instance.getClass().getMethod(methodName);
                return invokeMethodHandle.invoke(method, instance, arguments);
            } catch (Throwable e) {
                throw new RuntimeException(e);
            }
        }

        public static List<UIComponentAPI> getChildrenCopy(UIPanelAPI panel) {
            try {
                return (List<UIComponentAPI>) invokeMethod("getChildrenCopy", panel);
            } catch (Throwable e) {
                return null;
            }
        }

        public static Object invokeMethodWithAutoProjection(String methodName, Object instance, Object... arguments) {
            try {
                Object method = instance.getClass().getMethod(methodName);
                return invokeMethodHandle.invoke(method, instance, arguments);
            } catch (Throwable e) {
                throw new RuntimeException(e);
            }
        }
    }
    private static class ProductionUtil{
        public static UIPanelAPI getCoreUI() {
            CampaignUIAPI campaignUI;
            campaignUI = Global.getSector().getCampaignUI();
            InteractionDialogAPI dialog = campaignUI.getCurrentInteractionDialog();

            CoreUIAPI core;
            if (dialog == null) {
                core = (CoreUIAPI) ReflectionUtilis.invokeMethod("getCore",campaignUI);
            }
            else {
                core = (CoreUIAPI) ReflectionUtilis.invokeMethod( "getCoreUI",dialog);
            }
            return core == null ? null : (UIPanelAPI) core;
        }

        public static UIPanelAPI getCurrentTab() {
            UIPanelAPI coreUltimate = getCoreUI();
            UIPanelAPI core = (UIPanelAPI) ReflectionUtilis.invokeMethod("getCurrentTab",coreUltimate);
            return core == null ? null : (UIPanelAPI) core;
        }
    }
    public MarketAPI market;
    Object overview;
    public IndustryTable table;
    public IndustrySearchPanel searchPanel;
    public IntervalUtil util = null;
    public IndustryTagFilter filter;
    public IndustryShowcaseUI showcaseUI;
    public boolean dissableExit = false;
    public MarketDialog(String headerTitle, MarketAPI market,Object overview) {
        super(headerTitle);
        this.market = market;
        this.overview = overview;
    }


    @Override
    public void createUI(CustomPanelAPI panelAPI) {
        createHeaader(panelAPI);

        TooltipMakerAPI tooltip = panelAPI.createUIElement(panelAPI.getPosition().getWidth()-30,panelAPI.getPosition().getHeight()-y,false);
        createContentForDialog(tooltip,panelAPI.getPosition().getWidth()-30,panelAPI.getPosition().getHeight()-y-70);
        panelAPI.addUIElement(tooltip).inTL(x,y);
        createConfirmAndCancelSection(panelAPI);;
    }


    public void createContentForDialog(TooltipMakerAPI tooltip, float width,float height) {
        CustomPanelAPI panel =  Global.getSettings().createCustom(width,panelToInfluence.getPosition().getHeight(),null);
         table = new IndustryTable(630,height-30,panel,true,0,0,this);
         searchPanel = new IndustrySearchPanel(200,20,table);
        filter = new IndustryTagFilter(402,20,this);
        showcaseUI = new IndustryShowcaseUI(width - 640,height,this);
        table.createSections();
        table.createTable();
        IndustryInfoBottom bottom = new IndustryInfoBottom(market,width,30);
        tooltip.addCustom(panel,5f).getPosition().inTL(0,30);
        tooltip.addCustom(searchPanel.getMainPanel(),5f).getPosition().inTL(5,0);
        tooltip.addCustom(filter.getMainPanel(),5f).getPosition().inTL(210,0);
        tooltip.addCustom(showcaseUI.getMainPanel(),5f).getPosition().inTL(635,0);
        tooltip.addCustom(bottom.getMainPanel(),5f).getPosition().inTL(5,height+33);
    }
@Override
    public ButtonAPI generateConfirmButton(TooltipMakerAPI tooltip){
        ButtonAPI button = tooltip.addButton("Build","confirm",market.getFaction().getBaseUIColor(),market.getFaction().getDarkUIColor(), Alignment.MID, CutStyle.TL_BR,160,25,0f);
        button.setShortcut(Keyboard.KEY_G,true);
        confirmButton = button;
        return button;
    }
    @Override
    public ButtonAPI generateCancelButton(TooltipMakerAPI tooltip){
        ButtonAPI button = tooltip.addButton("Dismiss","cancel", market.getFaction().getBaseUIColor(),market.getFaction().getDarkUIColor(),Alignment.MID,CutStyle.TL_BR,buttonConfirmWidth,25,0f);
        button.setShortcut(Keyboard.KEY_A,true);
        cancelButton = button;
        return button;
    }
    @Override
    public void applyConfirmScript() {
        if(table.specToBuilt!=null){

            if(isInUpgradeMode){
                if(!market.getMemory().contains(UpdateQueueMainManager.memKey)){
                    market.getMemory().set(UpdateQueueMainManager.memKey,new UpdateQueueMarketManager());
                }
                UpdateQueueMarketManager manager = (UpdateQueueMarketManager) market.getMemory().get(UpdateQueueMainManager.memKey);
                Set<String> str = BuildingMenuMisc.getUpgradePath(table.specToBuilt.getId());
                manager.addNewQueue(str);
                Industry ind =Global.getSettings().getIndustrySpec(manager.getQueue(table.specToBuilt.getId()).getCurrIdOfUpgrade()).getNewPluginInstance(market);
                int cost = BuildingMenuMisc.getCostAndTimeFromPath(BuildingMenuMisc.getUpgradePath(table.specToBuilt.getId()),market).one.intValue();
                Misc.getCurrentlyBeingConstructed(market);
                this.market.getConstructionQueue().addToEnd(ind.getId(), (int) ind.getBuildCost());
                MutableValue credits = Global.getSector().getPlayerFleet().getCargo().getCredits();
                credits.subtract(cost);
                if (credits.get() <= 0.0F) {
                    credits.set(0.0F);
                }
                Global.getSector().getCampaignUI().getMessageDisplay().addMessage(String.format("Spent %s", Misc.getDGSCredits((cost)),Global.getSettings().getColor("standardUIIconColor"), Misc.getDGSCredits(cost), Color.ORANGE));
                Global.getSoundPlayer().playUISound("ui_build_industry", 1, 1);
            }
            else{
                Industry ind = table.specToBuilt.getNewPluginInstance(market);
                int cost = (int) ind.getBuildCost();
                Misc.getCurrentlyBeingConstructed(market);
                this.market.getConstructionQueue().addToEnd(ind.getId(), cost);
                MutableValue credits = Global.getSector().getPlayerFleet().getCargo().getCredits();
                credits.subtract(cost);
                if (credits.get() <= 0.0F) {
                    credits.set(0.0F);
                }
                Global.getSector().getCampaignUI().getMessageDisplay().addMessage(String.format("Spent %s", Misc.getDGSCredits((cost)),Global.getSettings().getColor("standardUIIconColor"), Misc.getDGSCredits(cost), Color.ORANGE));
                Global.getSoundPlayer().playUISound("ui_build_industry", 1, 1);
            }





        }
        ReflectionUtilis.invokeMethod("recreateWithEconUpdate",overview);
    }

    @Override
    public void processInput(List<InputEventAPI> events) {
        for (InputEventAPI event : events) {
            if(frames>=limit&&reachedMaxHeight){
                if(event.isMouseDownEvent()&&!isDialog){
                    TrapezoidButtonDetector detector = new TrapezoidButtonDetector();
                    float xLeft = panelToInfluence.getPosition().getX();
                    float xRight = panelToInfluence.getPosition().getX()+panelToInfluence.getPosition().getWidth();
                    float yBot = panelToInfluence.getPosition().getY();
                    float yTop = panelToInfluence.getPosition().getY()+panelToInfluence.getPosition().getHeight();
                    boolean hovers = detector.determineIfHoversOverButton(xLeft,yTop,xRight,yTop,xLeft,yBot,xRight,yBot,Global.getSettings().getMouseX(),Global.getSettings().getMouseY());
                    if(!hovers){
                        ProductionUtil.getCoreUI().removeComponent(panelToInfluence);
                        event.consume();
                        onExit();
                    }
                }
                if(!event.isConsumed()&&!dissableExit){
                    if(event.getEventValue()== Keyboard.KEY_ESCAPE){
                        ProductionUtil.getCoreUI().removeComponent(panelToInfluence);
                        event.consume();
                        onExit();
                        break;
                    }
                }
            }
            event.consume();
        }



    }


    @Override
    public void onExit() {
        for (DropDownButton o : table.copyOfButtons) {
            o.clear();
        }
        table.copyOfButtons.clear();
        table.dropDownButtons.clear();
        table.clearTable();
        table.activeTags.clear();
        table.specs.clear();
        BuildingUITracker.didIt = false;
    }

    @Override
    public void advance(float amount) {
        super.advance(amount);
        if(Global.getSettings().isShowingCodex()){
            util = new IntervalUtil(0.2f, 0.2f);
            dissableExit = true;
        }
        if(util!=null&&!Global.getSettings().isShowingCodex()){
            util.advance(amount);
            if(util.intervalElapsed()){
                dissableExit =  false;
                util = null;
            }
        }
        if(table!=null){
            table.advance(amount);
            if(table.specToBuilt!=null){
                BaseIndustry ind = (BaseIndustry) table.specToBuilt.getNewPluginInstance(market);
                if(isAvailableToBuild(ind,ind.getMarket(),isInUpgradeMode)){
                    if(!confirmButton.isEnabled()){
                        confirmButton.setEnabled(true);
                    }
                }
                else{
                    if(confirmButton.isEnabled()){
                        confirmButton.setEnabled(false);
                    }
                }
            }
            else{
                if(confirmButton.isEnabled()){
                    confirmButton.setEnabled(false);
                }
            }
        }


    }
    public boolean isInUpgradeMode = false;
    public void setInUpgradeMode(boolean inUpgradeMode) {
        isInUpgradeMode = inUpgradeMode;
    }

    public boolean isInUpgradeMode() {
        return isInUpgradeMode;
    }

    public static boolean isAvailableToBuild(Industry ind,MarketAPI market,boolean isInUpgradeMode){
        boolean exceededLimit = ind.isIndustry()&& Misc.getMaxIndustries(ind.getMarket())<=Misc.getNumIndustries(market);
        if(isInUpgradeMode){
            return BuildingMenuMisc.canBuildAllInPath(ind.getSpec().getId(),market)&&!exceededLimit;
        }
        return  ind.isAvailableToBuild()&&!exceededLimit&&Global.getSector().getPlayerFleet().getCargo().getCredits().get()>=ind.getBuildCost()&&!market.getConstructionQueue().hasItem(ind.getSpec().getId());
    }

}
