@file:JvmName("IntentExt")

package android.content

import android.net.Uri

internal fun Intent.collectUris(): List<Uri> {
    return when (val clipData = this.clipData) {
        null -> listOfNotNull(data)
        else -> {
            val list = mutableListOf<Uri>()
            for (i in 0 until clipData.itemCount) {
                val uri = clipData.getItemAt(i).uri
                list.add(uri)
            }
            return list
        }
    }
}
