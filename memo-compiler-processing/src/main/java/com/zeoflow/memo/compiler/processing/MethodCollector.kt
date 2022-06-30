package com.zeoflow.memo.compiler.processing

/**
 * Helper class to collect all methods of an [XTypeElement] to implement
 * [XTypeElement.getAllMethods].
 */
private class MethodCollector(
    val target: XTypeElement
) {
    // group methods by name for fast overrides check
    private val selectionByName = mutableMapOf<String, MutableList<XMethodElement>>()

    // we keep a duplicate list to preserve declaration order, makes the generated code match
    // user code
    private val selection = mutableListOf<XMethodElement>()

    fun collect() {
        val selection = target.getDeclaredMethods().forEach(::addToSelection)

        target.superType
            ?.typeElement
            ?.getAllMethods()
            ?.forEach(::addIfNotOverridden)
        target.getSuperInterfaceElements().forEach {
            it.getAllMethods().forEach {
                if (!it.isStatic()) {
                    addIfNotOverridden(it)
                }
            }
        }
        return selection
    }

    fun getResult(): List<XMethodElement> {
        return selection
    }

    private fun addIfNotOverridden(candidate: XMethodElement) {
        if (!target.canAccessSuperMethod(candidate)) {
            return
        }
        val overridden = selectionByName[candidate.name]?.any { existing ->
            existing.overrides(candidate, target)
        } ?: false
        if (!overridden) {
            addToSelection(candidate.copyTo(target))
        }
    }

    private fun addToSelection(method: XMethodElement) {
        selectionByName.getOrPut(method.name) {
            mutableListOf()
        }.add(method)
        selection.add(method)
    }

    private fun XTypeElement.canAccessSuperMethod(other: XMethodElement): Boolean {
        if (other.isPublic() || other.isProtected()) {
            return true
        }
        if (other.isPrivate()) {
            return false
        }
        // check package
        return packageName == other.enclosingElement.className.packageName()
    }
}

internal fun XTypeElement.collectAllMethods(): List<XMethodElement> {
    val collector = MethodCollector(this)
    collector.collect()
    return collector.getResult()
}