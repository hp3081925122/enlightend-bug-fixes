package com.hp.enlightendbugfix.mixin;

import com.hp.enlightendbugfix.Enlightendbugfix;
import java.util.AbstractMap.SimpleEntry;
import java.util.Collection;
import net.mcreator.enlightened_end.EnlightenedEndMod;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.fml.util.thread.EffectiveSide;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * 修复 EnlightenedEndMod 全局延迟队列客户端误入泄漏与异常崩服问题。
 */
@Mixin(value = EnlightenedEndMod.class, remap = false)
public abstract class EnlightenedEndModMixin {
    @Shadow
    @Final
    private static Collection<SimpleEntry<Runnable, Integer>> workQueue;

    @Inject(method = "queueServerWork", at = @At("HEAD"), cancellable = true)
    private static void enlightendbugfix$guardServerSideOnly(int tick, Runnable action, CallbackInfo ci) {
        // 拦截客户端侧误调 queueServerWork，避免多人客户端队列永久堆积泄漏以及单人集成服跨线程捕获 ClientLevel
        if (action == null || EffectiveSide.get().isClient()) {
            ci.cancel();
        }
    }

    @Inject(method = "tick", at = @At("HEAD"), cancellable = true)
    private void enlightendbugfix$safeProcessWorkQueue(TickEvent.ServerTickEvent event, CallbackInfo ci) {
        if (event.phase == TickEvent.Phase.END) {
            workQueue.removeIf(work -> {
                int next = work.getValue() - 1;
                work.setValue(next);
                if (next <= 0) {
                    try {
                        work.getKey().run();
                    } catch (Throwable t) {
                        Enlightendbugfix.LOGGER.error("[EnlightendBugFix] Caught exception in EnlightenedEndMod queued server work", t);
                    }
                    return true;
                }
                return false;
            });
        }
        ci.cancel();
    }
}
