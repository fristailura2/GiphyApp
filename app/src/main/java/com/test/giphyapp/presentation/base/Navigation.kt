package com.test.giphyapp.presentation.base

import androidx.compose.runtime.Composable
import androidx.lifecycle.SavedStateHandle
import androidx.navigation.*
import androidx.navigation.compose.composable


fun NavGraphBuilder.composable(
    entity: NavigationEntity,
    content: @Composable (NavBackStackEntry) -> Unit
) {
    composable(
        route = entity.getFullRoute(),
        arguments = entity.getArgs(),
        content = content
    )
}


abstract class NavigationEntity(
    val name: String,
    private val parentEntity: NavigationEntity? = null,
) {
    protected abstract val params: List<NavigationParam<*>>
    fun getFullRoute(): String {
        return getArgsFreeRoute() +
                if (params.isEmpty())
                    ""
                else
                    PATH_PARAM_SEPARATOR +
                            params
                                .map { it.getArgString() }
                                .reduce { acc, s -> "$acc$PARAM_SEPARATOR$s" }
    }

    fun getParamPathBuilder(): ParamPathBuilder {
        return ParamPathBuilder()
    }

    fun getArgs(): List<NamedNavArgument> {
        return params.map {
            navArgument(it.name) {
                type = it.type
                nullable = it.isNullable
            }
        }
    }

    private fun NavigationParam<*>.getArgValueString(): String {
        return PARAM_VALUE_PATTERN.format(name)
    }

    private fun NavigationParam<*>.getArgString(): String {
        return PARAM_PATTERN.format(name)
    }

    private fun getArgsFreeRoute(): String {
        val rout = StringBuilder()
        var current: NavigationEntity? = this
        while (current != null) {
            rout.insert(0, "$PATH_SEPARATOR${current.name}")
            current = current.parentEntity
        }
        return rout.toString()
    }

    fun <TP : Any> getArg(savedStateHandle: SavedStateHandle, param: NavigationParam<TP?>): TP {
        return savedStateHandle.get<TP>(param.name)!!
    }

    fun getIntArg(savedStateHandle: SavedStateHandle, param: NavigationParam<Int>): Int {
        return savedStateHandle.get<Int>(param.name)!!
    }

    fun <TP : Any> getArgOrNull(
        savedStateHandle: SavedStateHandle,
        param: NavigationParam<TP?>
    ): TP? {
        return savedStateHandle.get<TP>(param.name)
    }

    inner class ParamPathBuilder {
        private val paramsValueMap: MutableMap<NavigationParam<*>, Any> = mutableMapOf()

        fun <T> addParam(paramEntry: Pair<NavigationParam<T>, T>) {
            paramsValueMap[paramEntry.first] = paramEntry.second!!
        }

        fun build(): String {
            if (!paramsValueMap.filter {
                    !it.key.isNullable
                }.map {
                    it.key
                }.containsAll(params.filter {
                    !it.isNullable
                })
            ) throw IllegalArgumentException("Not all required params are given")

            var resSting = getFullRoute()

            paramsValueMap
                .generateReplacePatternAndValue()
                .forEach {
                    resSting = resSting.replaceFirst(it.first, it.second.toString())

                }

            return resSting.replace(PATTERN, "")
        }

        private fun Map<NavigationParam<*>, Any>.generateReplacePatternAndValue(): List<Pair<String, Any>> {
            return map {
                val requiredParamType = it.key.type.name.lowercase().let {
                    JAVA_PRIMITIVE_PARAM_MAPPINGS.getOrElse(it) { it }
                }

                val paramType = it.value::class.simpleName?.lowercase()?.let {
                    JAVA_PRIMITIVE_PARAM_MAPPINGS.getOrElse(it) { it }
                }

                if (requiredParamType != paramType) {
                    throw IllegalArgumentException("Param ${it.key.name} should be $requiredParamType but is $paramType")
                }
                val patternToReplace = it.key.getArgValueString()
                val valueToReplace = it.value

                patternToReplace to valueToReplace
            }
        }
    }

    private companion object {
        val JAVA_PRIMITIVE_PARAM_MAPPINGS = mapOf(
            "integer" to "int",
            "character" to "char"
        )

        val PATTERN = Regex("\\{.+\\}")
        const val PARAM_SEPARATOR = "\$"
        const val PATH_SEPARATOR = "/"
        const val PATH_PARAM_SEPARATOR = "?"
        const val PARAM_KEY_PATTERN = "%1\$s"
        const val PARAM_VALUE_PATTERN = "{%1\$s}"
        const val PARAM_PATTERN = "$PARAM_KEY_PATTERN=$PARAM_VALUE_PATTERN"
    }
}

class NavigationParam<T>(
    val name: String,
    val isNullable: Boolean,
    val type: NavType<T>
)