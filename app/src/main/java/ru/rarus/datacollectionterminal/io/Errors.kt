package ru.rarus.datacollectionterminal.io

class Errors {
    data class HandlerError(val code: Int, val message: String)

    companion object {

        @JvmStatic
        fun makeOkError(): HandlerError {
            return HandlerError(200, "Метод выполнен успешно")
        }

        @JvmStatic
        fun makeNotImplementedError(): HandlerError {
            return HandlerError(406, "Метод не поддерживается")
        }

        @JvmStatic
        fun makeNotFoundError(): HandlerError {
            return HandlerError(404, "Ресурс не найден")
        }

        @JvmStatic
        fun makeBadRequestError(): HandlerError {
            return HandlerError(400, "Некорректный запрос")
        }

        @JvmStatic
        fun makeServerError(text: String?): HandlerError {
            val errorDescr = text ?: "Неизвестная ошибка"
            return HandlerError(500, "Ошибка сервера: $errorDescr")
        }
    }
}
