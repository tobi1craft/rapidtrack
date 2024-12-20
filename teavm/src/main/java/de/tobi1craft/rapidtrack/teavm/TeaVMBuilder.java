package de.tobi1craft.rapidtrack.teavm;

import com.github.xpenatan.gdx.backends.teavm.config.AssetFileHandle;
import com.github.xpenatan.gdx.backends.teavm.config.TeaBuildConfiguration;
import com.github.xpenatan.gdx.backends.teavm.config.TeaBuilder;
import com.github.xpenatan.gdx.backends.teavm.gen.SkipClass;
import org.teavm.tooling.TeaVMTool;
import org.teavm.vm.TeaVMOptimizationLevel;

import java.io.File;
import java.io.IOException;

/**
 * Builds the TeaVM/HTML application.
 */
@SkipClass
public class TeaVMBuilder {
    public static void main(String[] args) throws IOException {
        TeaBuildConfiguration teaBuildConfiguration = new TeaBuildConfiguration();
        teaBuildConfiguration.assetsPath.add(new AssetFileHandle("../assets"));
        teaBuildConfiguration.webappPath = new File("src/main").getCanonicalPath();

        // Register any extra classpath assets here:
        // teaBuildConfiguration.additionalAssetsClasspathFiles.add("de/tobi1craft/rapidtrack/asset.extension");

        // Register any classes or packages that require reflection here:
        // TeaReflectionSupplier.addReflectionClass("de.tobi1craft.rapidtrack.reflect");

        TeaVMTool tool = TeaBuilder.config(teaBuildConfiguration);
        tool.setMainClass(TeaVMLauncher.class.getName());
        // For many (or most) applications, using the highest optimization won't add much to build time.
        // If your builds take too long and runtime performance doesn't matter, you can change FULL to SIMPLE.
        tool.setOptimizationLevel(TeaVMOptimizationLevel.FULL);
        tool.setObfuscated(false); //TODO: set true for production
        TeaBuilder.build(tool);
    }
}
