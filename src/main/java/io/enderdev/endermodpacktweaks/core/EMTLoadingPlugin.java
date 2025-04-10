package io.enderdev.endermodpacktweaks.core;

import com.google.common.collect.ImmutableMap;
import io.enderdev.endermodpacktweaks.EMTConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.ResourcePackListEntry;
import net.minecraft.launchwrapper.Launch;
import net.minecraft.launchwrapper.LaunchClassLoader;
import net.minecraftforge.common.ForgeVersion;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.relauncher.FMLLaunchHandler;
import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin;
import zone.rong.mixinbooter.IEarlyMixinLoader;

import javax.annotation.Nullable;
import java.lang.reflect.Field;
import java.util.*;
import java.util.function.BooleanSupplier;

@IFMLLoadingPlugin.Name("EnderModpackTweaksCore")
@IFMLLoadingPlugin.MCVersion(ForgeVersion.mcVersion)
@IFMLLoadingPlugin.SortingIndex(Integer.MIN_VALUE)
public class EMTLoadingPlugin implements IFMLLoadingPlugin, IEarlyMixinLoader {

    public static final boolean isClient = FMLLaunchHandler.side().isClient();

    private static final Map<String, BooleanSupplier> serversideMixinConfigs = ImmutableMap.copyOf(new HashMap<String, BooleanSupplier>() {
    });

    private static final Map<String, BooleanSupplier> commonMixinConfigs = ImmutableMap.copyOf(new HashMap<String, BooleanSupplier>() {
        {
            put("mixins.emt.minecraft.dragonfightmanager.json", () -> EMTConfig.MINECRAFT.DRAGON.enable);
            put("mixins.emt.minecraft.endgateway.json", () -> EMTConfig.MINECRAFT.END_GATEWAY.enable);
            put("mixins.emt.minecraft.endpodium.json", () -> EMTConfig.MINECRAFT.END_PODIUM.enable);
            put("mixins.emt.minecraft.netherportal.json", () -> EMTConfig.MINECRAFT.NETHER_PORTAL.enable);
            put("mixins.emt.minecraft.obsidianspike.json", () -> EMTConfig.MINECRAFT.OBSIDIAN_SPIKE.enable);
            put("mixins.emt.minecraft.endisland.json", () -> EMTConfig.MINECRAFT.END_ISLAND.enable);
            put("mixins.emt.minecraftforge.json", () -> true);
        }
    });

    private static final Map<String, BooleanSupplier> clientsideMixinConfigs = ImmutableMap.copyOf(new HashMap<String, BooleanSupplier>() {
        {
            put("mixins.emt.minecraft.bossbar.json", () -> EMTConfig.MINECRAFT.BOSS_BAR.enable);
            put("mixins.emt.minecraft.client.json", () -> true);
        }
    });

    @Override
    public String[] getASMTransformerClass() {
        return new String[0];
    }

    @Override
    public String getModContainerClass() {
        return null;
    }

    @Nullable
    @Override
    public String getSetupClass() {
        return null;
    }

    @Override
    public void injectData(Map<String, Object> data) {
        try {
            Field f_transformerExceptions = LaunchClassLoader.class.getDeclaredField("transformerExceptions");
            f_transformerExceptions.setAccessible(true);
            Set<String> transformerExceptions = (Set<String>) f_transformerExceptions.get(Launch.classLoader);
            transformerExceptions.remove("tyra314.toolprogression");
        }
        catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String getAccessTransformerClass() {
        return null;
    }

    @Override
    public List<String> getMixinConfigs() {
        List<String> configs = new ArrayList<>();
        if (isClient) {
            configs.addAll(clientsideMixinConfigs.keySet());
        } else {
            configs.addAll(serversideMixinConfigs.keySet());
        }
        configs.addAll(commonMixinConfigs.keySet());
        return configs;
    }

     @Override
    public boolean shouldMixinConfigQueue(String mixinConfig)
    {
        BooleanSupplier sidedSupplier = EMTLoadingPlugin.isClient ? clientsideMixinConfigs.get(mixinConfig) : null;
        BooleanSupplier commonSupplier = commonMixinConfigs.get(mixinConfig);
        return sidedSupplier != null ? sidedSupplier.getAsBoolean() : commonSupplier == null || commonSupplier.getAsBoolean();
    }
}
