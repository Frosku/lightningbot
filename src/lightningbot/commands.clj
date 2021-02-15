(ns lightningbot.commands
  (:require [clojure.string :as str]))

(defn parse-command
  [content]
  (let [prefix (str (first content))
        real-content (apply str (rest content))
        split (str/split real-content #"\s+")
        command (first split)
        sub-command (second split)
        remainder (str/join " " (next (next split)))]
    {:prefix prefix :command command :sub-command sub-command :remainder remainder}))

(defn is-command?
  [ctx parsed]
  (= (get-in ctx [:discord :prefix]) (get-in parsed [:prefix])))

(defn command-type?
  [cmd parsed]
  (= cmd (get-in parsed [:command])))

(defn sub-command-type?
  [sub parsed]
  (= sub (get-in parsed [:sub-command])))
