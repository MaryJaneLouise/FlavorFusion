package com.mariejuana.flavorfusion.data.constants

sealed class StateAPI {
    object Loading: StateAPI()
    class Failure(val e: Throwable) : StateAPI()
    class Success(val data: Any) : StateAPI()
    object Empty: StateAPI()
}