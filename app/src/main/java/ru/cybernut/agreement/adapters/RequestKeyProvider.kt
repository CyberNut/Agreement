package ru.cybernut.agreement.adapters

import androidx.recyclerview.selection.ItemKeyProvider
import ru.cybernut.agreement.data.Request

class RequestKeyProvider<T: Request>(private val items: List<T>): ItemKeyProvider<String>(ItemKeyProvider.SCOPE_MAPPED) {
    override fun getKey(position: Int): String? {
        return items.getOrNull(position)?.uuid
    }

    override fun getPosition(key: String): Int {
        val result = items.find { it.uuid == key }
        return if (result != null) {
            items.indexOf(result)
        } else
            0
    }
}

