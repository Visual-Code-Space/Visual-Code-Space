package com.raredev.vcspace.editor.completion

abstract class CompletionProvider {
    abstract fun getCompletions(params: CompletionParams?): List<VCSpaceCompletionItem?>?

    companion object {
        private val providers: Map<Class<*>, CompletionProvider> = HashMap()
        fun registerCompletionProviders() {
            if (providers.isEmpty()) {

            }
        }

        fun getCompletionProvider(clss: Class<*>): CompletionProvider? {
            return providers[clss]

        }
    }
}