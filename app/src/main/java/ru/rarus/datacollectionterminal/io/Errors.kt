package ru.rarus.datacollectionterminal.io

class HttpException(val code : Int, message: String): Exception(message)

const val HTTP_CODE_OK = 200
const val HTTP_CODE_BAD_REQUEST = 400
const val HTTP_CODE_NOT_FOUND_ERROR = 404
const val HTTP_CODE_NOT_ACCEPTABLE = 406
const val HTTP_CODE_SERVER_UNKNOWN = 500

class Errors {
    data class HandlerError(val code: Int, val message: String)

    companion object {
        @JvmStatic
        fun createHttpException(code: Int):HttpException  {
            var message = "Неизвестная ошибка: $code"
            when (code) {
                HTTP_CODE_BAD_REQUEST -> message = "Некорректный запрос"
                HTTP_CODE_NOT_FOUND_ERROR -> message = "Ресурс не найден"
                HTTP_CODE_NOT_ACCEPTABLE -> message = "Метод не поддерживается"
                HTTP_CODE_SERVER_UNKNOWN -> message = "Ошибка сервера: $code"
            }
            return HttpException(code, message)
        }

        @JvmStatic
        fun makeOkError(): HandlerError {
            return HandlerError(HTTP_CODE_OK, "Метод выполнен успешно")
        }

        @JvmStatic
        fun makeNotImplementedError(): HandlerError {
            return HandlerError(HTTP_CODE_NOT_ACCEPTABLE, "Метод не поддерживается")
        }

        @JvmStatic
        fun makeNotFoundError(): HandlerError {
            return HandlerError(HTTP_CODE_NOT_FOUND_ERROR, "Ресурс не найден")
        }

        @JvmStatic
        fun makeBadRequestError(): HandlerError {
            return HandlerError(HTTP_CODE_BAD_REQUEST, "Некорректный запрос")
        }

        @JvmStatic
        fun makeServerError(text: String?): HandlerError {
            val errorDescr = text ?: "Неизвестная ошибка"
            return HandlerError(HTTP_CODE_SERVER_UNKNOWN, "Ошибка сервера: $errorDescr")
        }
    }
}
