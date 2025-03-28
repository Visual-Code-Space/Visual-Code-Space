/*
 * This file is part of Visual Code Space.
 *
 * Visual Code Space is free software: you can redistribute it and/or modify it under the terms of
 * the GNU General Public License as published by the Free Software Foundation, either version 3 of
 * the License, or (at your option) any later version.
 *
 * Visual Code Space is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with Visual Code Space.
 * If not, see <https://www.gnu.org/licenses/>.
 */

package com.teixeira.vcspace.core

import com.vcspace.plugins.event.Event
import com.vcspace.plugins.event.EventListener
import com.vcspace.plugins.event.EventType

class EventManager {
    companion object {
        @JvmStatic
        val instance by lazy { EventManager() }
    }

    private val listeners = mutableMapOf<EventType, MutableList<EventListener>>()
    private val eventQueue = mutableListOf<Event>()

    fun registerListener(eventType: EventType, listener: EventListener) {
        listeners.getOrPut(eventType) { mutableListOf() }.add(listener)
    }

    fun unregisterListener(eventType: EventType, listener: EventListener) {
        listeners[eventType]?.remove(listener)
    }

    fun postEvent(event: Event) {
        eventQueue.add(event)
        processEvents()
    }

    private fun processEvents() {
        while (eventQueue.isNotEmpty()) {
            val event = eventQueue.removeAt(0)
            listeners[event.type]?.forEach { listener ->
                try {
                    listener.onEvent(event)
                } catch (e: Exception) {
                    // Handle exception
                    e.printStackTrace()
                }
            }
        }
    }

    fun clearListeners() {
        listeners.clear()
    }
}
