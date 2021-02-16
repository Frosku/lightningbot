(ns lightningbot.common.commands
  (:require [clojure.string :as str]))

(defn parse-command
  [message]
  (let [prefix (str (first message))
        message-without-prefix (apply str (rest message))
        split-message (str/split message-without-prefix #"\s+")
        command (first split-message)
        sub-command (second split-message)
        args (str/join " " (next (next split-message)))]
    {:prefix prefix :command command :sub-command sub-command :args args :match true}))

(defn match-prefix?
  [parsed cfg]
  (if (not (= (get-in cfg [:discord :prefix]) (:prefix parsed)))
    (assoc parsed :match false)
    parsed))

(defn match-command?
  [parsed cmd]
  (if (not (= (:command parsed) cmd))
    (assoc parsed :match false)
    parsed))

(defn match-sub-command?
  [parsed sub]
  (if (not (= (:sub-command parsed) sub))
    (assoc parsed :match false)
    parsed))

(defn match?
  [message cfg cmd sub]
  (-> message
      (parse-command ,,,)
      (match-prefix? ,,, cfg)
      (match-command? ,,, cmd)
      (match-sub-command? ,,, sub)
      (:match ,,,)))