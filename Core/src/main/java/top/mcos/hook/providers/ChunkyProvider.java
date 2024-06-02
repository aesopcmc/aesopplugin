package top.mcos.hook.providers;

import org.bukkit.Bukkit;
import org.popcraft.chunky.api.ChunkyAPI;
import top.mcos.AesopPlugin;
import top.mcos.config.ConfigLoader;
import top.mcos.hook.HookProvider;
import top.mcos.message.MessageHandler;

import java.util.concurrent.atomic.AtomicLong;

public final class ChunkyProvider implements HookProvider<ChunkyAPI> {
    private ChunkyAPI chunky;

    private AtomicLong updateTime = new AtomicLong();

    @Override
    public ChunkyProvider load() throws Exception {
        chunky = Bukkit.getServer().getServicesManager().load(ChunkyAPI.class);
        if(chunky!=null) {
            ChunkyAPI chunky = Bukkit.getServer().getServicesManager().load(ChunkyAPI.class);
            updateTime.set(System.currentTimeMillis());

            chunky.onGenerationComplete(event -> {
                AesopPlugin.logger.log("&e[chunk] &b世界【" + event.world() + "】区块已加载完毕。");
            });

            chunky.onGenerationProgress(event -> {
                if (ConfigLoader.rgConfig.isChunkyLoadingNoticeEnable()) {
                    long currentTime = System.currentTimeMillis();
                    //sender.sendMessagePrefixed(TranslationKey.TASK_DONE, world, chunkCount, String.format("%.2f", percentComplete), String.format("%01d", hours), String.format("%02d", minutes), String.format("%02d", seconds));
                    final boolean updateIntervalElapsed = ((currentTime - updateTime.get()) / 1e3) >
                            ConfigLoader.rgConfig.getChunkyLoadingNoticeDelay();
                    if (updateIntervalElapsed) {
                        String msg = ConfigLoader.rgConfig.getChunkyLoadingNoticeMessage();
                        msg = msg.replace("{world-name}", event.world())
                                .replace("{per}", String.format("%.2f", event.progress()))
                                .replace("{remain-times}",
                                        String.format("%01d", event.hours()) + ":" +
                                                String.format("%02d", event.minutes()) + ":" +
                                                String.format("%02d", event.seconds()));
                        MessageHandler.pushActionbarMessage(msg);
                        //AesopPlugin.logger.log(msg);
                        //AesopPlugin.logger.log("正在执行区块加载...世界:" + event.world() + " 进度:" + event.progress() +
                        //        " rate:" + event.rate() + " chunks:" + event.chunks() + " 预计剩余时间：" + event.hours() + ":" + event.minutes() + ":" + event.seconds());
                        updateTime.set(System.currentTimeMillis());
                    }
                }
            });
        }
        return this;
    }

    @Override
    public boolean isLoaded() {
        return chunky!=null;
    }

    @Override
    public ChunkyAPI getAPI() {
        return chunky;
    }

    @Override
    public String getAPIName() {
        return "ChunkyAPI";
    }
}
