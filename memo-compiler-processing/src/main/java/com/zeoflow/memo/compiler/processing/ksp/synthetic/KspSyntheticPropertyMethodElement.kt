package com.zeoflow.memo.compiler.processing.ksp.synthetic

import com.zeoflow.memo.compiler.processing.XAnnotated
import com.zeoflow.memo.compiler.processing.XEquality
import com.zeoflow.memo.compiler.processing.XExecutableParameterElement
import com.zeoflow.memo.compiler.processing.XHasModifiers
import com.zeoflow.memo.compiler.processing.XMemberContainer
import com.zeoflow.memo.compiler.processing.XMethodElement
import com.zeoflow.memo.compiler.processing.XMethodType
import com.zeoflow.memo.compiler.processing.XType
import com.zeoflow.memo.compiler.processing.XTypeElement
import com.zeoflow.memo.compiler.processing.ksp.KspAnnotated
import com.zeoflow.memo.compiler.processing.ksp.KspAnnotated.UseSiteFilter.Companion.NO_USE_SITE_OR_GETTER
import com.zeoflow.memo.compiler.processing.ksp.KspAnnotated.UseSiteFilter.Companion.NO_USE_SITE_OR_SETTER
import com.zeoflow.memo.compiler.processing.ksp.KspAnnotated.UseSiteFilter.Companion.NO_USE_SITE_OR_SET_PARAM
import com.zeoflow.memo.compiler.processing.ksp.KspFieldElement
import com.zeoflow.memo.compiler.processing.ksp.KspHasModifiers
import com.zeoflow.memo.compiler.processing.ksp.KspProcessingEnv
import com.zeoflow.memo.compiler.processing.ksp.KspTypeElement
import com.zeoflow.memo.compiler.processing.ksp.findEnclosingMemberContainer
import com.zeoflow.memo.compiler.processing.ksp.overrides
import com.google.devtools.ksp.KspExperimental
import com.google.devtools.ksp.symbol.KSPropertyAccessor
import com.google.devtools.ksp.symbol.KSPropertyGetter
import com.google.devtools.ksp.symbol.KSPropertySetter
import java.util.Locale

/**
 * Kotlin properties don't have getters/setters in KSP. As Depot expects Java code, we synthesize
 * them.
 *
 * @see KspSyntheticPropertyMethodElement.Getter
 * @see KspSyntheticPropertyMethodElement.Setter
 * @see KspSyntheticPropertyMethodType
 */
internal sealed class KspSyntheticPropertyMethodElement(
    val env: KspProcessingEnv,
    val field: KspFieldElement,
    accessor: KSPropertyAccessor?
) : XMethodElement,
    XEquality,
    XHasModifiers by KspHasModifiers.createForSyntheticAccessor(
        field.declaration,
        accessor
    ) {
    // NOTE: modifiers of the property are not necessarily my modifiers.
    //  that being said, it only matters if it is private in which case KAPT does not generate the
    //  synthetic hence we don't either.
    final override fun isJavaDefault() = false

    final override fun hasKotlinDefaultImpl() = false

    final override fun isSuspendFunction() = false

    final override val enclosingElement: XMemberContainer
        get() = this.field.enclosingElement

    final override fun isVarArgs() = false

    final override val executableType: XMethodType by lazy {
        KspSyntheticPropertyMethodType.create(
            element = this,
            container = field.containing.type
        )
    }

    override val docComment: String?
        get() = null

    final override fun asMemberOf(other: XType): XMethodType {
        return KspSyntheticPropertyMethodType.create(
            element = this,
            container = other
        )
    }

    override fun equals(other: Any?): Boolean {
        return XEquality.equals(this, other)
    }

    override fun hashCode(): Int {
        return XEquality.hashCode(equalityItems)
    }

    final override fun overrides(other: XMethodElement, owner: XTypeElement): Boolean {
        return env.resolver.overrides(this, other)
    }

    internal class Getter(
        env: KspProcessingEnv,
        field: KspFieldElement
    ) : KspSyntheticPropertyMethodElement(
        env = env,
        field = field,
        accessor = field.declaration.getter
    ),
        XAnnotated by KspAnnotated.create(
            env = env,
            delegate = field.declaration.getter,
            filter = NO_USE_SITE_OR_GETTER
        ) {
        override val equalityItems: Array<out Any?> by lazy {
            arrayOf(field, "getter")
        }

        @OptIn(KspExperimental::class)
        override val name: String by lazy {
            field.declaration.getter?.let {
                env.resolver.getJvmName(it)
            } ?: computeGetterName(field.name)
        }

        override val returnType: XType by lazy {
            field.type
        }

        override val parameters: List<XExecutableParameterElement>
            get() = emptyList()

        override fun kindName(): String {
            return "synthetic property getter"
        }

        override fun copyTo(newContainer: XTypeElement): XMethodElement {
            check(newContainer is KspTypeElement)
            return Getter(
                env = env,
                field = field.copyTo(newContainer)
            )
        }

        companion object {
            private fun computeGetterName(propName: String): String {
                // see https://kotlinlang.org/docs/reference/java-to-kotlin-interop.html#properties
                return if (propName.startsWith("is")) {
                    propName
                } else {
                    val capitalizedName = propName.replaceFirstChar {
                        if (it.isLowerCase()) it.titlecase(
                            Locale.US
                        ) else it.toString()
                    }
                    "get$capitalizedName"
                }
            }
        }
    }

    internal class Setter(
        env: KspProcessingEnv,
        field: KspFieldElement
    ) : KspSyntheticPropertyMethodElement(
        env = env,
        field = field,
        accessor = field.declaration.setter
    ),
        XAnnotated by KspAnnotated.create(
            env = env,
            delegate = field.declaration.setter,
            filter = NO_USE_SITE_OR_SETTER
        ) {
        override val equalityItems: Array<out Any?> by lazy {
            arrayOf(field, "setter")
        }

        @OptIn(KspExperimental::class)
        override val name: String by lazy {
            field.declaration.setter?.let {
                env.resolver.getJvmName(it)
            } ?: computeSetterName(field.name)
        }

        override val returnType: XType by lazy {
            env.voidType
        }

        override val parameters: List<XExecutableParameterElement> by lazy {
            listOf(
                SyntheticExecutableParameterElement(
                    env = env,
                    origin = this
                )
            )
        }

        override fun kindName(): String {
            return "synthetic property getter"
        }

        override fun copyTo(newContainer: XTypeElement): XMethodElement {
            check(newContainer is KspTypeElement)
            return Setter(
                env = env,
                field = field.copyTo(newContainer)
            )
        }

        private class SyntheticExecutableParameterElement(
            env: KspProcessingEnv,
            private val origin: Setter
        ) : XExecutableParameterElement,
            XAnnotated by KspAnnotated.create(
                env = env,
                delegate = origin.field.declaration.setter?.parameter,
                filter = NO_USE_SITE_OR_SET_PARAM
            ) {

            // todo constantValueData
            override val constantValue: Any
                get() = TODO("Not yet implemented")

            override val name: String by lazy {
                origin.field.declaration.setter?.parameter?.name?.asString() ?: "value"
            }
            override val type: XType
                get() = origin.field.type

            override val fallbackLocationText: String
                get() = "$name in ${origin.fallbackLocationText}"

            override fun asMemberOf(other: XType): XType {
                return origin.field.asMemberOf(other)
            }

            override val docComment: String?
                get() = null

            override fun kindName(): String {
                return "method parameter"
            }
        }

        companion object {
            private fun computeSetterName(propName: String): String {
                // see https://kotlinlang.org/docs/reference/java-to-kotlin-interop.html#properties
                return if (propName.startsWith("is")) {
                    "set${propName.substring(2)}"
                } else {
                    val capitalizedName = propName.replaceFirstChar {
                        if (it.isLowerCase()) it.titlecase(
                            Locale.US
                        ) else it.toString()
                    }
                    "set$capitalizedName"
                }
            }
        }
    }

    companion object {

        fun create(
            env: KspProcessingEnv,
            propertyAccessor: KSPropertyAccessor
        ): KspSyntheticPropertyMethodElement {
            val enclosingType = propertyAccessor.receiver.findEnclosingMemberContainer(env)

            checkNotNull(enclosingType) {
                "XProcessing does not currently support annotations on top level " +
                    "properties with KSP. Cannot process $propertyAccessor."
            }

            val field = KspFieldElement(
                env,
                propertyAccessor.receiver,
                enclosingType
            )

            return when (propertyAccessor) {
                is KSPropertyGetter -> {
                    Getter(env, field)
                }
                is KSPropertySetter -> {
                    Setter(env, field)
                }
                else -> error("Unsupported property accessor $propertyAccessor")
            }
        }
    }
}