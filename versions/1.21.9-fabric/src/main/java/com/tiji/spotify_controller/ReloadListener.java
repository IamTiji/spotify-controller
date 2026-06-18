package com.tiji.spotify_controller;

import com.tiji.spotify_controller.util.TextUtils;
import net.fabricmc.fabric.api.resource.IdentifiableResourceReloadListener;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

public class ReloadListener implements IdentifiableResourceReloadListener {
    @Override
    public ResourceLocation getFabricId() {
        return ResourceLocation.fromNamespaceAndPath(Main.MOD_ID, "reload_listener");
    }

    @Override
    public @NotNull CompletableFuture<Void> reload(SharedState sharedState,
                                                   Executor exectutor,
                                                   PreparationBarrier barrier,
                                                   Executor applyExectutor) {
        TextUtils.discardCache();
        return barrier.wait(null).thenAcceptAsync((nul) -> {});
    }
}
