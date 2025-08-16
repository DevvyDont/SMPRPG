package xyz.devvydont.smprpg.extensions

/**
 * Allows you to query an enum in a result oriented fashion, without worrying about case sensitivity.
 * This is useful since you do not have to deal with an exception prone conversion, or ugly enum LINQ.
 * Simply use this by calling `queryEnum<MyEnumClass>("some_enum_member")` If it doesn't exist, it will be null.
 */
inline fun <reified T : Enum<T>> queryEnum(key: String): T? {
    return enumValues<T>().firstOrNull() {it.name.equals(key, ignoreCase = true)}
}
