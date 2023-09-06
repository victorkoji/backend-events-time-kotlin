package io.eventstime.exception

class CustomException(
    message: String? = ""
) : RuntimeException(message) {
    constructor(
        errorType: AuthErrorType
    ) : this(errorType.name)

    constructor(
        errorType: UserErrorType
    ) : this(errorType.name)

    constructor(
        errorType: UserGroupErrorType
    ) : this(errorType.name)

    constructor(
        errorType: EventErrorType
    ) : this(errorType.name)

    constructor(
        errorType: StandCategoryErrorType
    ) : this(errorType.name)
}
