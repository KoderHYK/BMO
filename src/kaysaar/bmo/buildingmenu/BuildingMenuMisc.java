package kaysaar.bmo.buildingmenu;

import ashlib.data.plugins.misc.AshMisc;
import ashlib.data.plugins.reflection.ReflectionBetterUtilis;
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.*;
import com.fs.starfarer.api.campaign.econ.CommodityOnMarketAPI;
import com.fs.starfarer.api.campaign.econ.Industry;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.campaign.econ.MutableCommodityQuantity;
import com.fs.starfarer.api.campaign.listeners.ListenerUtil;
import com.fs.starfarer.api.combat.MutableStat;
import com.fs.starfarer.api.impl.campaign.econ.impl.BaseIndustry;
import com.fs.starfarer.api.impl.campaign.econ.impl.ItemEffectsRepo;
import com.fs.starfarer.api.impl.campaign.ids.Industries;
import com.fs.starfarer.api.impl.campaign.ids.Items;
import com.fs.starfarer.api.impl.campaign.ids.Strings;
import com.fs.starfarer.api.impl.campaign.intel.BaseIntelPlugin;
import com.fs.starfarer.api.impl.codex.CodexDataV2;
import com.fs.starfarer.api.loading.IndustrySpecAPI;
import com.fs.starfarer.api.ui.*;
import com.fs.starfarer.api.util.Misc;
import com.fs.starfarer.api.util.MutableValue;
import com.fs.starfarer.api.util.Pair;
import com.fs.starfarer.codex2.CodexDialog;
import com.fs.starfarer.ui.P;
import kaysaar.bmo.buildingmenu.additionalreq.AdditionalReqManager;
import kaysaar.bmo.buildingmenu.industrytags.IndustryTagManager;
import kaysaar.bmo.buildingmenu.upgradepaths.IndustryItemsPanel;
import kaysaar.bmo.buildingmenu.upgradequeue.UpdateQueueMainManager;
import kaysaar.bmo.buildingmenu.upgradequeue.UpdateQueueMarketManager;

import java.awt.*;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.util.List;
import java.util.*;

public class BuildingMenuMisc {
    public static HashMap<String,String>types = new HashMap<>();
    static {
        types.put("unique","Unique");
        types.put("structure","Structure");
        types.put("industry","Industry");
    }
    public static LinkedHashMap<String,Set<IndustrySpecAPI>>industryUpgrades = new LinkedHashMap<>();
    private static class ReflectionUtilis {
        // Code taken and modified from Grand Colonies
        private static final Class<?> fieldClass;
        private static final MethodHandles.Lookup lookup = MethodHandles.lookup();
        private static final MethodHandle setFieldHandle;
        private static final MethodHandle getFieldHandle;
        private static final MethodHandle getFieldNameHandle;
        private static final MethodHandle setFieldAccessibleHandle;
        private static final Class<?> methodClass;
        private static final MethodHandle getMethodNameHandle;
        private static final MethodHandle invokeMethodHandle;
        private static final MethodHandle setMethodAccessable;

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
                    for (Object obj : instances.getDeclaredFields()) {
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

        public static Pair<Object, Class<?>[]> getMethodFromSuperclass(String methodName, Object instance) {
            Class<?> currentClass = instance.getClass();

            while (currentClass != null) {
                // Retrieve all declared methods in the current class
                Object[] methods = currentClass.getDeclaredMethods();

                for (Object method : methods) {
                    try {
                        // Retrieve the MethodHandle for the getParameterTypes method
                        MethodHandle getParameterTypesHandle = ReflectionBetterUtilis.getParameterTypesHandle(method.getClass(), "getParameterTypes");
                        // Use the MethodHandle to retrieve the method's name

                        // Check if the method name matches
                        if (getMethodNameHandle.invoke(method).equals(methodName)) {
                            // Invoke the MethodHandle to get the parameter types
                            Class<?>[] parameterTypes = (Class<?>[]) getParameterTypesHandle.invoke(method);
                            return new Pair<>(method, parameterTypes);
                        }
                    } catch (Throwable e) {
                        e.printStackTrace();  // Handle any reflection errors
                    }
                }
                // Move to the superclass if no match is found
                currentClass = currentClass.getSuperclass();
            }

            // Return null if the method was not found in the class hierarchy
            return null;
        }

        public static Object invokeMethodWithAutoProjection(String methodName, Object instance, Object... arguments) {
            // Retrieve the method and its parameter types
            Pair<Object, Class<?>[]> methodPair = getMethodFromSuperclass(methodName, instance);

            // Check if the method was found
            if (methodPair == null) {
                try {
                    throw new NoSuchMethodException("Method " + methodName + " not found in class hierarchy of " + instance.getClass().getName());
                } catch (NoSuchMethodException e) {
                    throw new RuntimeException(e);
                }
            }

            Object method = methodPair.one;
            Class<?>[] parameterTypes = methodPair.two;

            // Prepare arguments by projecting them to the correct types
            Object[] projectedArgs = new Object[parameterTypes.length];
            for (int index = 0; index < parameterTypes.length; index++) {
                Object arg = (arguments.length > index) ? arguments[index] : null;

                if (arg == null) {
                    // If the expected type is a primitive type, throw an exception
                    if (parameterTypes[index].isPrimitive()) {
                        throw new IllegalArgumentException("Argument at index " + index + " cannot be null for primitive type " + parameterTypes[index].getName());
                    }
                    projectedArgs[index] = null; // Keep nulls as null for reference types
                } else {
                    // Try to convert the argument to the expected parameter type
                    try {
                        projectedArgs[index] = convertArgument(arg, parameterTypes[index]);
                    } catch (Exception e) {
                        throw new IllegalArgumentException("Cannot convert argument at index " + index + " to " + parameterTypes[index].getName(), e);
                    }
                }
            }

            // Ensure the method is accessible
            try {
                setMethodAccessable.invoke(method, true);
            } catch (Throwable e) {
                throw new RuntimeException(e);
            }
            // Invoke the method with the projected arguments
            try {
                return invokeMethodHandle.invoke(method, instance, projectedArgs);
            } catch (Throwable e) {
                throw new RuntimeException(e);
            }
        }

        // Helper function to convert an argument to the expected type
        public static Object convertArgument(Object arg, Class<?> targetType) {
            if (targetType.isAssignableFrom(arg.getClass())) {
                return arg; // Use as-is if types match
            } else if (targetType.isPrimitive()) {
                // Handle primitive types by boxing
                if (targetType == int.class) {
                    return ((Number) arg).intValue();
                } else if (targetType == long.class) {
                    return ((Number) arg).longValue();
                } else if (targetType == double.class) {
                    return ((Number) arg).doubleValue();
                } else if (targetType == float.class) {
                    return ((Number) arg).floatValue();
                } else if (targetType == short.class) {
                    return ((Number) arg).shortValue();
                } else if (targetType == byte.class) {
                    return ((Number) arg).byteValue();
                } else if (targetType == boolean.class) {
                    return arg;
                } else if (targetType == char.class) {
                    return arg;
                } else {
                    throw new IllegalArgumentException("Unsupported primitive type: " + targetType.getName());
                }
            } else {
                // For reference types, perform a cast if possible
                return targetType.cast(arg);
            }
        }
    }

    public static List<UIComponentAPI> getChildren(UIPanelAPI panelAPI) {
        return ReflectionUtilis.getChildrenCopy(panelAPI);
    }

    private static class ProductionUtil {
        public static UIPanelAPI getCoreUI() {
            CampaignUIAPI campaignUI;
            campaignUI = Global.getSector().getCampaignUI();
            InteractionDialogAPI dialog = campaignUI.getCurrentInteractionDialog();

            CoreUIAPI core;
            if (dialog == null) {
                core = (CoreUIAPI) ReflectionUtilis.invokeMethod("getCore", campaignUI);
            } else {
                core = (CoreUIAPI) ReflectionUtilis.invokeMethod("getCoreUI", dialog);
            }
            return core == null ? null : (UIPanelAPI) core;
        }

        public static UIPanelAPI getCurrentTab() {
            UIPanelAPI coreUltimate = getCoreUI();
            UIPanelAPI core = (UIPanelAPI) ReflectionUtilis.invokeMethod("getCurrentTab", coreUltimate);
            return core == null ? null : (UIPanelAPI) core;
        }
    }

    public static ArrayList<IndustrySpecAPI> getSpecsOfParent(String parentTag) {
        ArrayList<IndustrySpecAPI> specs = new ArrayList<>();
        if (parentTag == null) return specs;
        for (IndustrySpecAPI allIndustrySpec : Global.getSettings().getAllIndustrySpecs()) {
            if (allIndustrySpec.hasTag(parentTag) && allIndustrySpec.hasTag("sub_item")) {
                specs.add(allIndustrySpec);
            }
        }
        return specs;
    }

    public static LinkedHashMap<IndustrySpecAPI, ArrayList<IndustrySpecAPI>> getSpecMapParentChild() {
        LinkedHashMap<IndustrySpecAPI, ArrayList<IndustrySpecAPI>> map = new LinkedHashMap<>();
        for (IndustrySpecAPI allIndustrySpec : Global.getSettings().getAllIndustrySpecs()) {
            if (allIndustrySpec.hasTag("parent_item")) {
                map.put(allIndustrySpec, getSpecsOfParent(allIndustrySpec.getData()));
            }
        }
        return map;
    }
    public static void startQueue(String industryId,MarketAPI market) {
        UpdateQueueMarketManager manager = (UpdateQueueMarketManager) market.getMemory().get(UpdateQueueMainManager.memKey);
        Set<String> str = BuildingMenuMisc.getUpgradePath(industryId);
        manager.addNewQueue(str);
        Industry ind =Global.getSettings().getIndustrySpec(manager.getQueue(industryId).getCurrIdOfUpgrade()).getNewPluginInstance(market);
        int cost = BuildingMenuMisc.getCostAndTimeFromPath(BuildingMenuMisc.getUpgradePath(industryId),market).one.intValue();
        Misc.getCurrentlyBeingConstructed(market);
        market.getConstructionQueue().addToEnd(ind.getId(), (int) ind.getBuildCost());
        MutableValue credits = Global.getSector().getPlayerFleet().getCargo().getCredits();
        credits.subtract(cost);
        if (credits.get() <= 0.0F) {
            credits.set(0.0F);
        }
        Global.getSector().getCampaignUI().getMessageDisplay().addMessage(String.format("Spent %s", Misc.getDGSCredits((cost)),Global.getSettings().getColor("standardUIIconColor"), Misc.getDGSCredits(cost), Color.ORANGE));
        Global.getSoundPlayer().playUISound("ui_build_industry", 1, 1);
    }
    public static Set<IndustrySpecAPI> getIndustryTree(String progenitor) {
        if(industryUpgrades.get(progenitor)==null){
            Set<IndustrySpecAPI> specs = new LinkedHashSet<>();
            if(!AshMisc.isStringValid(progenitor))return specs;
            for (IndustrySpecAPI allIndustrySpec : Global.getSettings().getAllIndustrySpecs()) {
                if (allIndustrySpec.getDowngrade() == null) continue;
                IndustrySpecAPI currentOne = allIndustrySpec;
                Set<IndustrySpecAPI> specsToProgenitor = new LinkedHashSet<>();
                while (currentOne.getDowngrade() != null) {
                    specsToProgenitor.add(currentOne);
                    if(!AshMisc.isStringValid(currentOne.getDowngrade()))break;
                    if (currentOne.getDowngrade().equals(currentOne.getId())) break; // KOL, WHY
                    currentOne = Global.getSettings().getIndustrySpec(currentOne.getDowngrade());
                    if (currentOne!=null&&currentOne.getId().equals(progenitor)) {
                        specs.addAll(specsToProgenitor);
                        break;
                    }

                }

            }
            industryUpgrades.put(progenitor,specs);
        }
      return industryUpgrades.get(progenitor);
    }

    public static LinkedHashSet<String> getUpgradePath(String upgradeID) {
        IndustrySpecAPI spec = Global.getSettings().getIndustrySpec(upgradeID);
        LinkedHashSet<String> path = new LinkedHashSet<>();
        path.add(spec.getId());

        // Collect the upgrade path
        while (spec.getDowngrade() != null) {
            if (spec.getDowngrade().equals(spec.getId())) {
                break;
            }
            spec = Global.getSettings().getIndustrySpec(spec.getDowngrade());
            path.add(spec.getId());
        }

        // Reverse the path
        LinkedList<String> reversedList = new LinkedList<>(path);
        LinkedHashSet<String> reversedPath = new LinkedHashSet<>();
        for (int i = reversedList.size() - 1; i >= 0; i--) {
            reversedPath.add(reversedList.get(i));
        }

        return reversedPath;
    }

    public static ArrayList<String> getOtherReasons(String id, MarketAPI market) {
        ArrayList<String> strs = new ArrayList<>();
        Industry ind = Global.getSettings().getIndustrySpec(id).getNewPluginInstance(market);
        if (!ind.isAvailableToBuild() && ind.getUnavailableReason() != null) {
            strs.add(ind.getUnavailableReason());
        }
        if (!AdditionalReqManager.getInstance().doesMetReq(id, market)) {
            strs.add(AdditionalReqManager.getInstance().getReq(id).getReason(market, id));
        }
        return strs;

    }

    public static Pair<Float, Float> getCostAndTimeFromPath(LinkedHashSet<String> path, MarketAPI market) {
        Pair<Float, Float> money = new Pair<>(0f, 0f);
        for (String s : path) {
            Industry ind = Global.getSettings().getIndustrySpec(s).getNewPluginInstance(market);
            money.one += ind.getBuildCost();
            money.two += ind.getBuildTime();
        }
        return money;
    }

    public static boolean canBuildAllInPath(String master, MarketAPI market) {
        float money = 0f;
        boolean metAllAvailable = true;
        for (String s : getUpgradePath(master)) {
            Industry ind = Global.getSettings().getIndustrySpec(s).getNewPluginInstance(market);
            if (!ind.isAvailableToBuild() || !AdditionalReqManager.getInstance().doesMetReq(ind.getSpec().getId(), market)) {
                metAllAvailable = false;
                break;
            }
            money += ind.getBuildCost();
        }
        return metAllAvailable && money <= Global.getSector().getPlayerFleet().getCargo().getCredits().get();

    }

    public static boolean canBuildAllInPathIgnoreLast(String master, MarketAPI market) {
        float money = 0f;
        boolean metAllAvailable = true;
        for (String s : getUpgradePath(master)) {
            Industry ind = Global.getSettings().getIndustrySpec(s).getNewPluginInstance(market);
            if(ind.getSpec().getId().equals(master))continue;
            if (!ind.isAvailableToBuild() || !AdditionalReqManager.getInstance().doesMetReq(ind.getSpec().getId(), market)) {

                metAllAvailable = false;

            }
            money += ind.getBuildCost();
        }
        return metAllAvailable && money <= Global.getSector().getPlayerFleet().getCargo().getCredits().get();

    }

    public static void createTooltipForIndustry(
            BaseIndustry ind, Industry.IndustryTooltipMode mode, TooltipMakerAPI tooltip, boolean expanded, boolean hasTitle, float width, boolean showMods, boolean isHover, boolean isInQueue) {
        float pad = 3f;
        float opad = 10f;
        String mod = IndustryTagManager.getModNameForInd(ind.getSpec().getId());
        LabelAPI title = null;

        Color gray = Misc.getGrayColor();
        Color highlight = Misc.getHighlightColor();
        Color bad = Misc.getNegativeHighlightColor();
        if (hasTitle) {
            title = tooltip.addTitle("");
        }
        if (!mod.equalsIgnoreCase("vanilla") && !ind.getSpec().hasTag("parent_item")) {
            tooltip.addSectionHeading("Mod", ind.getMarket().getFaction().getBaseUIColor(), ind.getMarket().getFaction().getDarkUIColor(), Alignment.MID, 5f);
            tooltip.addPara("This industry is from %s", 5f, Color.ORANGE, mod);
        }
        if (isInQueue) {
            tooltip.addSectionHeading("Queue upgrade", ind.getMarket().getFaction().getBaseUIColor(), ind.getMarket().getFaction().getDarkUIColor(), Alignment.MID, 5f);
            Pair<Float, Float> data = BuildingMenuMisc.getCostAndTimeFromPath(getUpgradePath(ind.getSpec().getId()), ind.getMarket());
            int days = data.two.intValue();
            String daysStr = "days";
            if (days == 1) daysStr = "day";
            String costStr = Misc.getDGSCredits(data.one);
            String creditsStr = Misc.getDGSCredits(Global.getSector().getPlayerFleet().getCargo().getCredits().get());
            LabelAPI label = tooltip.addPara("%s and %s " + daysStr + " to build. You have %s.", opad,
                    highlight, costStr, "" + days, creditsStr);
            label.setHighlight(costStr, "" + days, creditsStr);
            if (Global.getSector().getPlayerFleet().getCargo().getCredits().get() >= data.one) {
                label.setHighlightColors(highlight, highlight, highlight);
            } else {
                label.setHighlightColors(bad, highlight, highlight);
            }
            tooltip.addPara("After confirming it will start queue, that will automatically upgrade industry.", Misc.getTooltipTitleAndLightHighlightColor(), 5f);
            if (!BuildingMenuMisc.canBuildAllInPathIgnoreLast(ind.getSpec().getId(), ind.getMarket())) {
                tooltip.addPara("Note!: One of requirements for subsequent industries in queue is not met", Misc.getNegativeHighlightColor(), 5f);
            }
            if (!expanded) {
                tooltip.addPara("Press %s for more info", 5f, Color.ORANGE, "F1");
            } else {
                for (String s : BuildingMenuMisc.getUpgradePath(ind.getSpec().getId())) {
                    tooltip.addPara(Global.getSettings().getIndustrySpec(s).getName(), Misc.getButtonTextColor(), 5f);
                    boolean added = false;
                    for (String otherReason : BuildingMenuMisc.getOtherReasons(s, ind.getMarket())) {
                        tooltip.addPara(BaseIntelPlugin.BULLET + otherReason, Misc.getNegativeHighlightColor(), 3f);
                        added = true;
                    }
                    if (!added) {
                        tooltip.addPara(BaseIntelPlugin.BULLET + "Met criteria", Misc.getPositiveHighlightColor(), 3f);
                    }
                }
            }

        }
        CustomPanelAPI testT = Global.getSettings().createCustom(width, 2, null);
        TooltipMakerAPI tTooltip1 = testT.createUIElement(testT.getPosition().getWidth(), 20, true);
        ind.createTooltip(mode, tTooltip1, expanded);
        TooltipMakerAPI tTooltip = testT.createUIElement(testT.getPosition().getWidth(), tTooltip1.getHeightSoFar(), false);
        ind.createTooltip(mode, tTooltip, expanded);
        UIPanelAPI componentAPI = (UIPanelAPI) BuildingMenuMisc.getChildren(tTooltip).get(0);
        if (hasTitle) {
            title.setText(((LabelAPI) BuildingMenuMisc.getChildren(componentAPI).get(0)).getText());
        }
        ReflectionUtilis.invokeMethodWithAutoProjection("makeNonExpandable",tTooltip);
        ((LabelAPI) BuildingMenuMisc.getChildren(componentAPI).get(0)).setText("");
        testT.addUIElement(tTooltip).inTL(-5, 0);
        testT.getPosition().setSize(width, tTooltip1.getHeightSoFar());
        tooltip.addCustom(testT, -13);

        tooltip.addSpacer(25);
        FactionAPI faction = ind.getMarket().getFaction();
        Color color = faction.getBaseUIColor();
        Color dark = faction.getDarkUIColor();
        if (!getItemsForIndustry(ind.getSpec().getId(), isHover).isEmpty() && (mode == Industry.IndustryTooltipMode.ADD_INDUSTRY || mode == Industry.IndustryTooltipMode.UPGRADE)) {
            tooltip.addSectionHeading("Installable Items", color, dark, Alignment.MID, opad);
            if (!isHover) {
                tooltip.addPara("These items can be installed either in this industry, or in one of it's upgraded versions, as long as all requirements are met.", gray, opad);

            } else {
                tooltip.addPara("These items can be installed in this industry, as long as all requirements are met.", gray, opad);

            }
            tooltip.addCustom(new IndustryItemsPanel(width, ind, isHover).getMainPanel(), 5f);
        }
        TooltipMakerAPI  test = tooltip.beginSubTooltip(testT.getPosition().getWidth());
        test.setCodexEntryId(CodexDataV2.getIndustryEntryId(ind.getSpec().getId()));
        tooltip.endSubTooltip();
        tooltip.addCustom(test,2f);
        tooltip.addSpacer(25f);
    }

    public static ArrayList<IndustrySpecAPI> getAllSpecsWithoutDowngrade() {
        ArrayList<IndustrySpecAPI> specs = new ArrayList<>();
        for (IndustrySpecAPI allIndustrySpec : Global.getSettings().getAllIndustrySpecs()) {
            if (allIndustrySpec.getDowngrade() == null && !allIndustrySpec.hasTag("sub_item")&&!allIndustrySpec.hasTag("do_not_show_in_build_dialog")) {

                specs.add(allIndustrySpec);
            }
        }
        return specs;
    }

    public static ArrayList<IndustrySpecAPI> getAllSpecsWithoutDowngradeAndItsSubItems() {
        ArrayList<IndustrySpecAPI> specs = new ArrayList<>();
        for (IndustrySpecAPI allIndustrySpec : Global.getSettings().getAllIndustrySpecs()) {
            if (allIndustrySpec.getDowngrade() == null) {
                specs.add(allIndustrySpec);
            }
        }
        return specs;
    }
    public static ArrayList<IndustrySpecAPI> getAllSpecsWithoutSubItem() {
        ArrayList<IndustrySpecAPI> specs = new ArrayList<>();
        for (IndustrySpecAPI allIndustrySpec : Global.getSettings().getAllIndustrySpecs()) {
            if (allIndustrySpec.hasTag("sub_item")&&!allIndustrySpec.hasTag("do_not_show_in_build_dialog")) {
                specs.add(allIndustrySpec);
            }
        }
        return specs;
    }
    public static boolean isIndustryFromTreePresent(IndustrySpecAPI spec, MarketAPI marketToValidate) {
        if (spec.hasTag("parent_item")) {
            for (IndustrySpecAPI industrySpecAPI : getSpecsOfParent(spec.getData())) {
                if (marketToValidate.hasIndustry(industrySpecAPI.getId())||marketToValidate.getConstructionQueue().hasItem(industrySpecAPI.getId())) return true;
                for (IndustrySpecAPI subCurr : getIndustryTree(industrySpecAPI.getId())) {
                    if (marketToValidate.hasIndustry(subCurr.getId())||marketToValidate.getConstructionQueue().hasItem(subCurr.getId())) {
                        return true;
                    }
                }
            }
            return false;
        } else {
            for (IndustrySpecAPI industrySpecAPI : getIndustryTree(spec.getId())) {
                if (marketToValidate.hasIndustry(industrySpecAPI.getId())||marketToValidate.getConstructionQueue().hasItem(industrySpecAPI.getId())) {
                    return true;
                }
            }
            return false;
        }
    }

    // New method to sort IndustrySpecAPI by name alphabetically
    public static void sortIndustrySpecsByName(ArrayList<IndustrySpecAPI> industrySpecs) {
        Collections.sort(industrySpecs, new Comparator<IndustrySpecAPI>() {
            @Override
            public int compare(IndustrySpecAPI spec1, IndustrySpecAPI spec2) {
                return spec1.getName().compareToIgnoreCase(spec2.getName());
            }
        });
    }

    public static void sortDropDownButtonsByName(ArrayList<DropDownButton> buttons, final boolean ascending) {
        Collections.sort(buttons, new Comparator<DropDownButton>() {
            @Override
            public int compare(DropDownButton button1, DropDownButton button2) {
                String name1 = getButtonName(button1);
                String name2 = getButtonName(button2);
                return ascending ? name1.compareToIgnoreCase(name2) : name2.compareToIgnoreCase(name1);
            }
        });
    }

    // Sort by Type
    public static void sortDropDownButtonsByType(ArrayList<DropDownButton> buttons, final boolean ascending) {
        Collections.sort(buttons, new Comparator<DropDownButton>() {
            @Override
            public int compare(DropDownButton button1, DropDownButton button2) {
                String type1 = getButtonType(button1);
                String type2 = getButtonType(button2);
                return ascending ? type1.compareToIgnoreCase(type2) : type2.compareToIgnoreCase(type1);
            }
        });
    }

    // Sort by Build Time (Days)
    public static void sortDropDownButtonsByDays(ArrayList<DropDownButton> buttons, final boolean ascending) {
        Collections.sort(buttons, new Comparator<DropDownButton>() {
            @Override
            public int compare(DropDownButton button1, DropDownButton button2) {
                float days1 = calculateBuildTime(button1);
                float days2 = calculateBuildTime(button2);
                return ascending ? Float.compare(days1, days2) : Float.compare(days2, days1);
            }
        });
    }

    // Sort by Cost
    public static void sortDropDownButtonsByCost(ArrayList<DropDownButton> buttons, final boolean ascending, final MarketAPI market) {
        Collections.sort(buttons, new Comparator<DropDownButton>() {
            @Override
            public int compare(DropDownButton button1, DropDownButton button2) {
                float cost1 = calculateCost(button1,market);
                float cost2 = calculateCost(button2,market);
                return ascending ? Float.compare(cost1, cost2) : Float.compare(cost2, cost1);
            }
        });
    }

    // Utility Methods for Sorting Logic
    private static String getButtonName(DropDownButton button) {
        return button instanceof IndustryDropDownButton
                ? getIndustryString(((IndustryDropDownButton) button).mainSpec)
                : "Unknown";
    }

    private static String getButtonType(DropDownButton button) {
        return button instanceof IndustryDropDownButton
                ? getIndustryString(((IndustryDropDownButton) button).mainSpec)
                : "Unknown";
    }

    private static float calculateBuildTime(DropDownButton button) {
        if (button.droppableMode) {
            ArrayList<IndustrySpecAPI> subSpecs = ((IndustryDropDownButton) button).subSpecs;
            if (subSpecs != null && !subSpecs.isEmpty()) {
                float totalBuildTime = 0;
                for (IndustrySpecAPI spec : subSpecs) {
                    totalBuildTime += spec.getBuildTime();
                }
                return totalBuildTime / subSpecs.size();
            }
        }
        IndustrySpecAPI mainSpec = ((IndustryDropDownButton) button).mainSpec;
        return mainSpec != null ? mainSpec.getBuildTime() : 0;
    }

    private static float calculateCost(DropDownButton button,MarketAPI market) {
        if (button.droppableMode) {
            ArrayList<IndustrySpecAPI> subSpecs = ((IndustryDropDownButton) button).subSpecs;
            if (subSpecs != null && !subSpecs.isEmpty()) {
                float totalCost = 0;
                for (IndustrySpecAPI spec : subSpecs) {
                    totalCost += getSpecCost(spec,market);
                }
                return totalCost / subSpecs.size();
            }
        }
        IndustrySpecAPI mainSpec = ((IndustryDropDownButton) button).mainSpec;
        return mainSpec != null ? getSpecCost(mainSpec,market) : 0;
    }

    private static float getSpecCost(IndustrySpecAPI spec,MarketAPI market) {
        // Replace this logic with the actual method to get the cost in your implementation.
        return spec.getNewPluginInstance(market).getBuildCost(); // Example placeholder method
    }

    public static String getIndustryString(IndustrySpecAPI industry) {
        for (Map.Entry<String, String> entry : types.entrySet()) {
            if(industry.hasTag(entry.getKey())) {
                return entry.getValue();
            }
        }
        return "Unknown";
    }

    public static ArrayList<DropDownButton> searchIndustryByName(ArrayList<DropDownButton> buttons, String searchString, int threshold) {
        ArrayList<DropDownButton> matchingButtons = new ArrayList<>();

        for (DropDownButton button : buttons) {
            if (button instanceof IndustryDropDownButton) {
                IndustryDropDownButton industryButton = (IndustryDropDownButton) button;

                // Check mainSpec name
                if (industryButton.mainSpec != null && isValid(searchString, industryButton.mainSpec.getName(), threshold)) {
                    matchingButtons.add(button);
                    continue;
                }

                // Check subSpecs names if droppableMode
                if (button.droppableMode && industryButton.subSpecs != null) {
                    for (IndustrySpecAPI subSpec : industryButton.subSpecs) {
                        if (isValid(searchString, subSpec.getName(), threshold)) {
                            matchingButtons.add(button);
                            break; // Stop checking subSpecs for this button
                        }
                    }
                }
            }
        }

        return matchingButtons;
    }

    public static boolean isValid(String searchString, String target, int threshold) {
        String lowerSearchString = searchString.toLowerCase();
        String lowerTarget = target.toLowerCase();
        return AshMisc.levenshteinDistance(lowerSearchString, lowerTarget) <= threshold || lowerTarget.contains(lowerSearchString);
    }

    public static Set<String> getUpgradeChildren(String progenitor) {
        LinkedHashSet<String> children = new LinkedHashSet<>();
        for (IndustrySpecAPI allIndustrySpec : Global.getSettings().getAllIndustrySpecs()) {
            if (AshMisc.isStringValid(allIndustrySpec.getDowngrade()) && progenitor.equals(allIndustrySpec.getDowngrade())) {
                children.add(allIndustrySpec.getId());
            }
        }
        return children;
    }

    public static Set<String> getItemsForIndustry(String industry, boolean ignoreUpgrades) {
        Set<String> items = new LinkedHashSet<>();
        Set<IndustrySpecAPI> industries = getIndustryTree(industry);

        for (SpecialItemSpecAPI spec : Global.getSettings().getAllSpecialItemSpecs()) {
            if (spec.hasTag("mission_item") || ItemEffectsRepo.ITEM_EFFECTS.get(spec.getId()) == null) continue;
            if (spec.getParams() != null) {
                for (String loadEntry : AshMisc.loadEntries(spec.getParams(), ",")) {

                    if (loadEntry.trim().equals(industry)) {
                        items.add(spec.getId());
                    }
                    if (!ignoreUpgrades) {
                        for (IndustrySpecAPI s : industries) {
                            if (loadEntry.trim().equals(s.getId())) {
                                items.add(spec.getId());
                                break;
                            }
                        }
                    }

                }


            }

        }
        return items;
    }
}
