package com.zeoflow.memo.compiler.processing

import com.zeoflow.memo.compiler.processing.javac.JavacElement
import com.zeoflow.memo.compiler.processing.ksp.KSFileAsOriginatingElement
import com.zeoflow.memo.compiler.processing.ksp.KspElement
import javax.lang.model.element.Element
import kotlin.contracts.contract

/**
 * Represents an element declared in code.
 *
 * @see [javax.lang.model.element.Element]
 * @see XExecutableElement
 * @see XVariableElement
 * @see XTypeElement
 */
interface XElement : XAnnotated {
    /**
     * Returns the string representation of the Element's kind.
     */
    fun kindName(): String
    /**
     * When the location of an element is unknown, this String is appended to the diagnostic
     * message. Without this information, developer gets no clue on where the error is.
     */
    val fallbackLocationText: String

    /**
     * The documentation comment of the element, or null if there is none.
     */
    val docComment: String?
}

/**
 * Checks whether this element represents an [XTypeElement].
 */
// we keep these as extension methods to be able to use contracts
fun XElement.isTypeElement(): Boolean {
    contract {
        returns(true) implies (this@isTypeElement is XTypeElement)
    }
    return this is XTypeElement
}

/**
 * Checks whether this element represents an [XVariableElement].
 */
fun XElement.isVariableElement(): Boolean {
    contract {
        returns(true) implies (this@isVariableElement is XVariableElement)
    }
    return this is XVariableElement
}

/**
 * Checks whether this element represents an [XMethodElement].
 */
fun XElement.isMethod(): Boolean {
    contract {
        returns(true) implies (this@isMethod is XMethodElement)
    }
    return this is XMethodElement
}

fun XElement.isConstructor(): Boolean {
    contract {
        returns(true) implies (this@isConstructor is XConstructorElement)
    }
    return this is XConstructorElement
}

/**
 * Attempts to get a Javac [Element] representing the originating element for attribution
 * when writing a file for incremental processing.
 *
 * In KSP a [KSFileAsOriginatingElement] will be returned, which is a synthetic javac element
 * that allows us to pass originating elements to JavaPoet and KotlinPoet, and later extract
 * the KSP file when writing with [XFiler].
 */
internal fun XElement.originatingElementForPoet(): Element? {
    return when (this) {
        is JavacElement -> element
        is KspElement -> containingFileAsOriginatingElement()
        else -> error("Originating element is not implemented for ${this.javaClass}")
    }
}
