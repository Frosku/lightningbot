(ns lightningbot.op.logic
  (:require [lightningbot.common.preferences :as prefs]))

(defn get-preference-key
  [guild-id]
  (keyword (str "ops-" guild-id)))

(defn get-bot-owner
  [cfg]
  (try
    (get-in cfg [:discord :bot-owner])
    (catch Exception e nil)))

(defn get-guild-ops
  [cfg guild-id]
  (->> guild-id
       (get-preference-key)
       (prefs/get-pref cfg)
       (vec)))

(defn get-applicable-ops
  [cfg guild-id]
  (->> guild-id
       (get-guild-ops cfg)
       (cons (get-bot-owner cfg))
       (vec)))

(defn add-guild-op
  [cfg guild-id admin-id]
  (->> guild-id
       (get-guild-ops cfg)
       (cons admin-id)
       (set)
       (vec)
       (prefs/put-pref cfg (get-preference-key guild-id))))

(defn remove-guild-op
  [cfg guild-id admin-id]
  (->> guild-id
       (get-guild-ops cfg)
       (remove #(= % admin-id))
       (vec)
       (prefs/put-pref cfg (get-preference-key guild-id))))

(defn is-guild-op?
  [cfg guild-id user-id]
  (->> guild-id
       (get-applicable-ops cfg)
       (some #(= user-id %))
       (some?)))

(defn first-id
  [users]
  (get-in (first users) [:id]))