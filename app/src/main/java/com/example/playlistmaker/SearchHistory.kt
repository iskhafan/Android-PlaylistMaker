package com.example.playlistmaker

import android.content.SharedPreferences
import com.google.gson.Gson

class SearchHistory(private val sharedPreferences: SharedPreferences) {

    private val gson = Gson()

    fun getHistory(): MutableList<Track> {
        val json = sharedPreferences.getString(SEARCH_HISTORY_KEY, null) ?: return mutableListOf()
        val tracksArray = gson.fromJson(json, Array<Track>::class.java)
        return tracksArray.toMutableList()
    }


    fun addTrack(track: Track) {
        val history = getHistory()

        val existingTrack = history.find { it.trackId == track.trackId }
        if (existingTrack != null) {
            history.remove(existingTrack)
        }

        history.add(0, track)

        if (history.size > MAX_SEARCH_HISTORY_SIZE) {
            history.removeAt(history.size - 1)
        }

        saveHistory(history)
    }

    fun clearHistory() {
        sharedPreferences.edit().remove(SEARCH_HISTORY_KEY).apply()
    }

    private fun saveHistory(history: List<Track>) {
        val json = gson.toJson(history)
        sharedPreferences.edit().putString(SEARCH_HISTORY_KEY, json).apply()
    }

    companion object {
        private const val SEARCH_HISTORY_KEY = "history_tracks_key"
        private const val MAX_SEARCH_HISTORY_SIZE = 10
    }
}