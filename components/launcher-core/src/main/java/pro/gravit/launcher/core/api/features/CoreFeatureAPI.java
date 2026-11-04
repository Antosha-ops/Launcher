package pro.gravit.launcher.core.api.features;

import pro.gravit.launcher.core.api.method.AuthMethod;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface CoreFeatureAPI {
    CompletableFuture<List<AuthMethod>> getAuthMethods();
    CompletableFuture<LauncherUpdateInfo> checkUpdates();

    /**
     * @param checkIntervalMinutes how often a running launcher should check for updates again,
     *                             as configured on the LaunchServer; 0 disables periodic checks
     */
    record LauncherUpdateInfo(String url, String version, boolean available, boolean required, long checkIntervalMinutes) {
        public LauncherUpdateInfo(String url, String version, boolean available, boolean required) {
            this(url, version, available, required, 0);
        }
    }

    enum UpdateVariant {
        JAR,
        EXE_WINDOWS_X86_64, EXE_WINDOWS_X86, EXE_WINDOWS_ARM64,
        LINUX_X86, LINUX_X86_64, LINUX_ARM64, LINUX_ARM32,
        MACOS_X86_64, MACOS_ARM64
    }
}
