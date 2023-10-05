package app.pixle.ui.composable

class NavigationBuilder {
    lateinit var navigateToMain: () -> Unit

    lateinit var navigateToCamera: () -> Unit

    lateinit var navigateToProfile: () -> Unit

    lateinit var navigateToPreferences: () -> Unit

    lateinit var navigateBack: () -> Unit

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

    fun toPreferences(action: () -> Unit): NavigationBuilder {
        this.navigateToPreferences = action
        return this
    }

    fun back(action: () -> Unit): NavigationBuilder {
        this.navigateBack = action
        return this
    }

    companion object {
        private var instance: NavigationBuilder? = null
        fun getInstance() : NavigationBuilder {
            return instance ?: synchronized(this) {
                NavigationBuilder().also { instance = it }
            }
        }
    }
}