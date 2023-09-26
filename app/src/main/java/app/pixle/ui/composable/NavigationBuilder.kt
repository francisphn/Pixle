package app.pixle.ui.composable

import android.content.Context

class NavigationBuilder {
    lateinit var navigateToMain: () -> Unit

    lateinit var navigateToCamera: () -> Unit

    lateinit var navigateToProfile: () -> Unit

    fun toMain(action: () -> Unit): NavigationBuilder {
        this.navigateToMain = action
        return this
    }

    fun toCamera(action: () -> Unit): NavigationBuilder {
        this.navigateToCamera = action
        return this
    }

    fun toProfile(action: () -> Unit): NavigationBuilder {
        this.navigateToProfile = action
        return this
    }

    companion object {
        private var instance: NavigationBuilder = NavigationBuilder()
        fun getInstance() = instance
    }
}