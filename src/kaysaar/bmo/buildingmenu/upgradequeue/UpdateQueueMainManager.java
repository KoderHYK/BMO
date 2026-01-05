package kaysaar.bmo.buildingmenu.upgradequeue;

import ashlib.data.plugins.reflection.ReflectionBetterUtilis;
import com.fs.starfarer.api.EveryFrameScript;
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.CampaignUIAPI;
import com.fs.starfarer.api.campaign.CoreUIAPI;
import com.fs.starfarer.api.campaign.CoreUITabId;
import com.fs.starfarer.api.campaign.InteractionDialogAPI;
import com.fs.starfarer.api.campaign.comm.CommMessageAPI;
import com.fs.starfarer.api.campaign.econ.CommodityOnMarketAPI;
import com.fs.starfarer.api.campaign.econ.Industry;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.impl.campaign.intel.BaseIntelPlugin;
import com.fs.starfarer.api.impl.campaign.intel.MessageIntel;
import com.fs.starfarer.api.ui.UIComponentAPI;
import com.fs.starfarer.api.ui.UIPanelAPI;
import com.fs.starfarer.api.util.IntervalUtil;
import com.fs.starfarer.api.util.Misc;
import com.fs.starfarer.api.util.Pair;
import com.fs.starfarer.campaign.ui.marketinfo.CommodityPanel;
import com.fs.starfarer.ui.impl.R;
import kaysaar.bmo.buildingmenu.MarketDialog;

import java.awt.*;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.util.Iterator;
import java.util.List;

public class UpdateQueueMainManager implements EveryFrameScript {
    public static String memKey = "$bmo_queue";
    public IntervalUtil util = new IntervalUtil(0.5f, 1f);

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

        public static java.util.List<UIComponentAPI> getChildrenCopy(UIPanelAPI panel) {
            try {
                return (java.util.List<UIComponentAPI>) invokeMethod("getChildrenCopy", panel);
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

    @Override
    public boolean isDone() {
        return false;
    }

    @Override
    public boolean runWhilePaused() {
        return true;
    }

    @Override
    public void advance(float amount) {
        util.advance(amount);
        if (CoreUITabId.CARGO.equals(Global.getSector().getCampaignUI().getCurrentCoreTab())) {
            Object overview = getOverview();
            if (overview != null) {
                MarketAPI currentlyAffectedMarket = null;
                for (UIComponentAPI componentAPI : ReflectionUtilis.getChildrenCopy((UIPanelAPI) overview)) {
                    if (componentAPI instanceof CommodityPanel) {
                        currentlyAffectedMarket = (MarketAPI) ReflectionUtilis.getPrivateVariable("market", componentAPI);
                        break;
                    }
                }
                if (currentlyAffectedMarket != null) {
                    if (!currentlyAffectedMarket.getMemory().contains(memKey)) {
                        currentlyAffectedMarket.getMemory().set(memKey, new UpdateQueueMarketManager());
                    }
                    UpdateQueueMarketManager man = (UpdateQueueMarketManager) currentlyAffectedMarket.getMemory().get(memKey);
                    Iterator<UpdateQueueInstance> ques = man.queues.iterator();
                    while (ques.hasNext()) {
                        UpdateQueueInstance que = ques.next();
                        if (!currentlyAffectedMarket.hasIndustry(que.lastId) && !currentlyAffectedMarket.getConstructionQueue().hasItem(que.lastId)) {
                            queueMessage(que);
                            refreshMarketUI(overview);
                            que.clear();
                            ques.remove();
                        } else if (currentlyAffectedMarket.hasIndustry(que.lastId)) {
                            Industry ind = currentlyAffectedMarket.getIndustry(que.lastId);
                            if (!ind.isBuilding() && !ind.isUpgrading()) {
                                String attempt = que.getNextInLine();
                                if (currentlyAffectedMarket.hasIndustry(que.getNextPrevInLine())) {
                                    que.popFromTop();
                                    if (que.getCurrIdOfUpgrade() == null) {
                                        refreshMarketUI(overview);
                                        que.clear();
                                        ques.remove();
                                    }
                                    String prev = ind.getSpec().getUpgrade();
                                    ind.getSpec().setUpgrade(que.getCurrIdOfUpgrade());
                                    ind.startUpgrading();
                                    ind.getSpec().setUpgrade(prev);
                                    MessageIntel intel = new MessageIntel(ind.getCurrentName() + " at " + currentlyAffectedMarket.getName(), Misc.getBasePlayerColor());
                                    intel.addLine(BaseIntelPlugin.BULLET + "Upgrade started");
                                    intel.setIcon(Global.getSector().getPlayerFaction().getCrest());
                                    intel.setSound(BaseIntelPlugin.getSoundStandardUpdate());
                                    Global.getSector().getCampaignUI().addMessage(intel, CommMessageAPI.MessageClickAction.COLONY_INFO, currentlyAffectedMarket);
                                    refreshMarketUI(overview);
                                } else {
                                    queueMessage(que);
                                    refreshMarketUI(overview);
                                    que.clear();
                                    ques.remove();
                                }


                            }

                        }
                    }
                }

            }
        } else {
            if (util.intervalElapsed()) {
                for (MarketAPI playerMarket : Misc.getPlayerMarkets(true)) {
                    if (!playerMarket.getMemory().contains(memKey)) {
                        playerMarket.getMemory().set(memKey, new UpdateQueueMarketManager());
                    }
                    UpdateQueueMarketManager man = (UpdateQueueMarketManager) playerMarket.getMemory().get(memKey);
                    Iterator<UpdateQueueInstance> ques = man.queues.iterator();
                    while (ques.hasNext()) {
                        UpdateQueueInstance que = ques.next();
                        if(que.getCurrIdOfUpgrade()==null){
                            que.clear();
                            ques.remove();
                        }
                        else{
                            if (playerMarket.hasIndustry(que.getCurrIdOfUpgrade())) {
                                Industry ind = playerMarket.getIndustry(que.getCurrIdOfUpgrade());
                                if (!ind.isBuilding() && !ind.isUpgrading()) {
                                    String attempt = que.getNextInLine();
                                    if (playerMarket.hasIndustry(que.getNextPrevInLine())) {
                                        que.popFromTop();
                                        if (que.getCurrIdOfUpgrade() == null) {
                                            que.clear();
                                            ques.remove();
                                        }
                                        else{
                                            String prev = ind.getSpec().getUpgrade();
                                            ind.getSpec().setUpgrade(que.getCurrIdOfUpgrade());
                                            ind.startUpgrading();
                                            ind.getSpec().setUpgrade(prev);
                                            MessageIntel intel = new MessageIntel(ind.getCurrentName() + " at " + playerMarket.getName(), Misc.getBasePlayerColor());
                                            intel.addLine(BaseIntelPlugin.BULLET + "Upgrade started");
                                            intel.setIcon(Global.getSector().getPlayerFaction().getCrest());
                                            intel.setSound(BaseIntelPlugin.getSoundStandardUpdate());
                                            Global.getSector().getCampaignUI().addMessage(intel, CommMessageAPI.MessageClickAction.COLONY_INFO, playerMarket);
                                        }


                                    } else {
                                        queueMessage(que);
                                        que.clear();
                                        ques.remove();
                                    }
                                }
                            }
                        }


                    }

                }
            }
        }

    }

    private static void queueMessage(UpdateQueueInstance que) {
        int cost = (int) que.getTotalCostForReturnIgnoreCurrent();
        if (cost != 0) {
            Global.getSector().getCampaignUI().getMessageDisplay().addMessage(String.format("Received back %s from cancelling upgrade queue", Misc.getDGSCredits((cost)), Global.getSettings().getColor("standardUIIconColor"), Misc.getDGSCredits(cost), Color.ORANGE));
            Global.getSector().getPlayerFleet().getCargo().getCredits().add(cost);
        }
    }

    public static Object getOverview() {
        UIPanelAPI currentTab = ProductionUtil.getCurrentTab();
        UIPanelAPI firstLayer = null, secondLayer = null;
        Object overview = null;

        if (currentTab != null) {
            for (UIComponentAPI componentAPI : ReflectionUtilis.getChildrenCopy(currentTab)) {
                if (ReflectionUtilis.hasMethodOfName("getMarketInfo", componentAPI)) {
                    firstLayer = (UIPanelAPI) componentAPI;
                    break;
                }
            }
            if (firstLayer != null) {
                for (UIComponentAPI componentAPI : ReflectionUtilis.getChildrenCopy(firstLayer)) {
                    if (ReflectionUtilis.hasMethodOfName("showOverview", componentAPI)) {
                        secondLayer = (UIPanelAPI) componentAPI;
                        overview = ReflectionUtilis.getChildrenCopy(secondLayer).get(0);
                        break;
                    }
                }
            }
            return overview;
        }
        return null;
    }

    private static void refreshMarketUI(Object overview) {
        if (overview != null) {
            ReflectionUtilis.invokeMethod("recreateWithEconUpdate", overview);

        }

    }

}
