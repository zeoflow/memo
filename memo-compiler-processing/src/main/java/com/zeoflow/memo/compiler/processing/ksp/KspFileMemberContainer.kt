package com.zeoflow.memo.compiler.processing.ksp

import com.zeoflow.memo.compiler.processing.XAnnotated
import com.google.devtools.ksp.symbol.AnnotationUseSiteTarget
import com.google.devtools.ksp.symbol.KSDeclaration
import com.google.devtools.ksp.symbol.KSFile
import com.squareup.javapoet.ClassName

/**
 * [XMemberContainer] implementation for KSFiles.
 */
internal class KspFileMemberContainer(
    private val env: KspProcessingEnv,
    private val ksFile: KSFile
) : KspMemberContainer,
    XAnnotated by KspAnnotated.create(
        env = env,
        delegate = ksFile,
        filter = KspAnnotated.UseSiteFilter.FILE
    ) {
    override val type: KspType?
        get() = null
    override val declaration: KSDeclaration?
        get() = null
    override val className: ClassName by lazy {

        val pkgName = ksFile.packageName.asString().let {
            if (it == "<root>") {
                ""
            } else {
                it
            }
        }
        ClassName.get(
            pkgName, ksFile.findClassName()
        )
    }

    override fun kindName(): String {
        return "file"
    }

    override val fallbackLocationText: String = ksFile.filePath

    override val docComment: String?
        get() = null

    companion object {
        private fun KSFile.findClassName(): String {
            return annotations.firstOrNull {
                it.useSiteTarget == AnnotationUseSiteTarget.FILE &&
                    it.annotationType.resolve().declaration.qualifiedName?.asString() ==
                    JvmName::class.qualifiedName
            }?.arguments?.firstOrNull {
                it.name?.asString() == "name"
            }?.value?.toString() ?: fileName.replace(".kt", "Kt")
        }
    }
}