(ns lightningbot.booru.logic
  (:require [lightningbot.booru.booru :as booru]
            [lightningbot.common.preferences :as prefs]))

(defn channel-filter-preference-key
  [channel-id]
  (keyword (str "channel-filter-" channel-id)))

(defn get-random-image
  [cfg filter query]
  (->> query
       (booru/get-number-of-results cfg filter)
       (rand-int)
       (booru/get-search-image-results cfg filter query 1)
       (first)))

(defn set-filter-for-channel
  [cfg channel-id filter-id]
  (prefs/put-pref cfg (channel-filter-preference-key channel-id) filter-id))

(defn get-filter-for-channel
  [cfg channel-id]
  (prefs/get-pref cfg (channel-filter-preference-key channel-id)))