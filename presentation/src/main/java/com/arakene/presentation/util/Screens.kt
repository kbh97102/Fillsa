package com.arakene.presentation.util

import kotlinx.serialization.Serializable

sealed interface Screens {

    val routeString: String

    @Serializable
    data object Login: Screens {
        override val routeString: String
            get() = "Login"
    }

    @Serializable
    data object Home: Screens {
        override val routeString: String
            get() = "home"
    }

    @Serializable
    data object List: Screens {
        override val routeString: String
            get() = "List"
    }

    @Serializable
    data object Calendar: Screens {
        override val routeString: String
            get() = "Calendar"
    }

    @Serializable
    data object MyPage: Screens {
        override val routeString: String
            get() = "My page"
    }
}