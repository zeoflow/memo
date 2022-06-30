package com.zeoflow.memo.compiler.ktx

import com.zeoflow.memo.compiler.processing.XProcessingEnv
import com.zeoflow.memo.compiler.processing.XProcessingStep.Companion.asAutoCommonProcessor
import com.zeoflow.memo.compiler.util.SimpleJavaVersion
import com.google.auto.common.BasicAnnotationProcessor
import com.zeoflow.memo.processor.MemoProcessingStep
import javax.lang.model.SourceVersion

/**
 * Annotation processor option to tell Gradle that Depot is an isolating annotation processor.
 */
private const val ISOLATING_ANNOTATION_PROCESSORS_INDICATOR =
    "org.gradle.annotation.processing.isolating"

/**
 * The annotation processor for Depot.
 */
class LibProcessor : BasicAnnotationProcessor() {

    /** Helper variable to avoid reporting the warning twice. */
    private var jdkVersionHasBugReported = false

    override fun steps(): MutableIterable<Step> {
        return mutableListOf(
            MemoProcessingStep(KotlinProcessingMachine(XProcessingEnv.create(
                processingEnv
            ))).asAutoCommonProcessor(processingEnv)
        )
    }

    override fun getSupportedOptions(): MutableSet<String> {
        val supportedOptions = ARG_OPTIONS.toMutableSet()
        // x processing is a cheap wrapper so it is fine to re-create.
        val xProcessing = XProcessingEnv.create(processingEnv)
        if (BooleanProcessorOptions.INCREMENTAL.getValue(xProcessing)) {
            if (methodParametersVisibleInClassFiles()) {
                // Depot can be incremental
                supportedOptions.add(ISOLATING_ANNOTATION_PROCESSORS_INDICATOR)
            } else {
                if (!jdkVersionHasBugReported) {
                    jdkVersionHasBugReported = true
                }
            }
        }

        return supportedOptions
    }

    /**
     * Returns `true` if the method parameters in class files can be accessed by Depot.
     *
     * Context: Depot requires access to the real parameter names of constructors (see
     * PojoProcessor.getParamNames). Depot uses the ExecutableElement.getParemters() API on the
     * constructor element to do this.
     *
     * When Depot is not yet incremental, the above API is working as expected. However, if we make
     * Depot incremental, during an incremental compile Gradle may want to pass class files instead
     * source files to annotation processors (to avoid recompiling the source files that haven't
     * changed). Due to JDK bug https://bugs.openjdk.java.net/browse/JDK-8007720, the class files
     * may lose the real parameter names of constructors, which would break Depot.
     *
     * The above JDK bug was fixed in JDK 11. The fix was also cherry-picked back into the
     * embedded JDK that was shipped with Android Studio 3.5+.
     *
     * Therefore, for Depot to be incremental, we need to check whether the JDK being used has the
     * fix: Either it is JDK 11+ or it is an embedded JDK that has the cherry-picked fix (version
     * 1.8.0_202-release-1483-b39-5509098 or higher).
     */
    private fun methodParametersVisibleInClassFiles(): Boolean {
        val currentJavaVersion = SimpleJavaVersion.getCurrentVersion() ?: return false

        if (currentJavaVersion >= SimpleJavaVersion.VERSION_11_0_0) {
            return true
        }

        val isEmbeddedJdk =
            System.getProperty("java.vendor")?.contains("JetBrains", ignoreCase = true)
                ?: false
        // We are interested in 3 ranges of Android Studio (AS) versions:
        //    1. AS 3.5.0-alpha09 and lower use JDK 1.8.0_152 or lower.
        //    2. AS 3.5.0-alpha10 up to 3.5.0-beta01 use JDK 1.8.0_202-release-1483-b39-5396753.
        //    3. AS 3.5.0-beta02 and higher use JDK 1.8.0_202-release-1483-b39-5509098 or higher,
        //       which have the cherry-picked JDK fix.
        // Therefore, if the JDK version is 1.8.0_202, we need to filter out those in range #2.
        return if (isEmbeddedJdk && (currentJavaVersion > SimpleJavaVersion.VERSION_1_8_0_202)) {
            true
        } else if (isEmbeddedJdk && (currentJavaVersion == SimpleJavaVersion.VERSION_1_8_0_202)) {
            System.getProperty("java.runtime.version")
                ?.let { it != "1.8.0_202-release-1483-b39-5396753" }
                ?: false
        } else {
            false
        }
    }

    override fun getSupportedSourceVersion(): SourceVersion {
        return SourceVersion.latest()
    }


    companion object {
        val ARG_OPTIONS by lazy {
            BooleanProcessorOptions.values().map { it.argName }
        }
    }

    enum class BooleanProcessorOptions(val argName: String, private val defaultValue: Boolean) {
        INCREMENTAL("com.zeoflow.memo.compiler.incremental", true),
        EXPAND_PROJECTION("com.zeoflow.memo.compiler.expandProjection", false);

        /**
         * Returns the value of this option passed through the [XProcessingEnv]. If the value
         * is null or blank, it returns the default value instead.
         */
        fun getValue(processingEnv: XProcessingEnv): Boolean {
            val value = processingEnv.options[argName]
            return if (value.isNullOrBlank()) {
                defaultValue
            } else {
                value.toBoolean()
            }
        }
    }
}
