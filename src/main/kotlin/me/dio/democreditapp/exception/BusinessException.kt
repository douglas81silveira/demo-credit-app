package me.dio.democreditapp.exception

data class BusinessException(
    override val message: String?
): RuntimeException(message) {
}