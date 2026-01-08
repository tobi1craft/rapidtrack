package de.tobi1craft.rapidtrack.teavm;

import com.github.xpenatan.gdx.backends.teavm.config.TeaBuilder;
import org.teavm.tooling.TeaVMSourceFilePolicy;
import org.teavm.tooling.TeaVMTool;
import org.teavm.vm.TeaVMOptimizationLevel;

import java.io.IOException;

/**
 * Builds the TeaVM/HTML application.
 */
public class TeaVMBuilder {
    static void main() throws IOException {
        TeaVMConfig.configureWebapp();

        TeaVMTool tool = new TeaVMTool();
        tool.setObfuscated(false); //TODO: set true for production
        tool.setOptimizationLevel(TeaVMOptimizationLevel.FULL);
        tool.setMainClass(TeaVMLauncher.class.getName());

        //TODO: Remove all 3 for production
        tool.setDebugInformationGenerated(true);
        tool.setSourceMapsFileGenerated(true);
        tool.setSourceFilePolicy(TeaVMSourceFilePolicy.COPY);

        TeaBuilder.build(tool);
    }
}
