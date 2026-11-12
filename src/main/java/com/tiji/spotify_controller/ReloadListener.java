package com.tiji.spotify_controller;

import com.tiji.spotify_controller.util.TextUtils;
import net.fabricmc.fabric.api.resource.IdentifiableResourceReloadListener;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

//#if MC<=12101
//$$ import net.minecraft.util.profiling.ProfilerFiller;
//#endif

public class ReloadListener implements IdentifiableResourceReloadListener {
    @Override
    public ResourceLocation getFabricId() {
        return ResourceLocation.fromNamespaceAndPath(Main.MOD_ID, "reload_listener");
    }

    @Override
    public @NotNull CompletableFuture<Void> reload(PreparationBarrier barrier,
                                                   ResourceManager manager,
                                                   //#if MC<=12101
                                                   //$$ ProfilerFiller preparationsProfiler,
                                                   //$$ ProfilerFiller reloadProfiler,
                                                   //#endif
                                                   Executor backgroundExecutor,
                                                   Executor gameExecutor) {
        TextUtils.discardCache();
        return barrier.wait(null).thenAcceptAsync((nul) -> {});
    }
}
