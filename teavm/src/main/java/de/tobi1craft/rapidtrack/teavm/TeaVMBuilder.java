package de.tobi1craft.rapidtrack.teavm;

import com.github.xpenatan.gdx.backends.teavm.config.TeaBuilder;
import org.teavm.tooling.TeaVMSourceFilePolicy;
import org.teavm.tooling.TeaVMTool;
import org.teavm.tooling.sources.DirectorySourceFileProvider;
import org.teavm.vm.TeaVMOptimizationLevel;

import java.io.File;
import java.io.IOException;

/**
 * Builds the TeaVM/HTML application.
 */
public class TeaVMBuilder {
    public static void main(String[] args) throws IOException {
        TeaVMConfig.configureWebapp();

        TeaVMTool tool = new TeaVMTool();
        tool.setObfuscated(false); //TODO: set true for production
        tool.setOptimizationLevel(TeaVMOptimizationLevel.FULL);
        tool.setMainClass(TeaVMLauncher.class.getName());

        tool.setDebugInformationGenerated(true);
        tool.setSourceMapsFileGenerated(true);
        tool.setSourceFilePolicy(TeaVMSourceFilePolicy.COPY); //TODO: Maybe change for production

        File coreSourcePath = new File("../core/src/main/java");
        tool.addSourceFileProvider(new DirectorySourceFileProvider(coreSourcePath));

        int size = 64 * (1 << 20);
        tool.setMaxDirectBuffersSize(size);
        TeaBuilder.build(tool);
    }
}
