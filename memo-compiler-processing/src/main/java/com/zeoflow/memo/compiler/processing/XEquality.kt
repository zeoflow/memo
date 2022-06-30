package com.zeoflow.memo.compiler.processing

/**
 * Helper interface to enforce implementing equality in wrappers so that we don't by mistake
 * create wrappers that do not properly handle equality.
 *
 * Enforcement is done in JavacType and JavacElement
 */
internal interface XEquality {
    /**
     * The list of items that should participate in equality checks.
     */
    val equalityItems: Array<out Any?>

    companion object {
        fun hashCode(elements: Array<out Any?>): Int {
            return elements.contentHashCode()
        }

        fun equals(first: Any?, second: Any?): Boolean {
            if (first !is XEquality || second !is XEquality) {
                return false
            }
            return equals(first.equalityItems, second.equalityItems)
        }

        fun equals(first: Array<out Any?>, second: Array<out Any?>): Boolean {
            // TODO there is probably a better way to do this
            if (first.size != second.size) {
                return false
            }
            repeat(first.size) {
                if (first[it] != second[it]) {
                    return false
                }
            }
            return true
        }
    }
}
