package top.niunaijun.blackboxa.bean

/**
 * Represents the Google Mobile Services (GMS) installation status for a virtual user.
 *
 * @property userID The virtual user ID.
 * @property userName The display name of the virtual user.
 * @property isInstalledGms Whether GMS is currently installed for this user.
 */
data class GmsBean(val userID: Int, val userName: String, var isInstalledGms: Boolean)

/**
 * Represents the result of a GMS install or uninstall operation.
 *
 * @property userID The virtual user ID associated with the operation.
 * @property success Whether the operation succeeded.
 * @property msg A human-readable message describing the result of the operation.
 */
data class GmsInstallBean(val userID: Int, val success: Boolean, val msg: String)