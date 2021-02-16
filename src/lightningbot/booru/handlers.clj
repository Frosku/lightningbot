(ns lightningbot.booru.handlers
  (:require [lightningbot.common.commands :as cmd]
            [lightningbot.booru.booru :as booru]
            [lightningbot.booru.display :as display]
            [lightningbot.booru.logic :as logic]
            [lightningbot.op.display :as opd]
            [lightningbot.op.logic :as op]
            [taoensso.timbre :refer [debug]]))

(defn set-channel-filter-handler
  [_ {{author-id :id} :author :keys [channel-id guild-id content id]} cfg]
  (when (cmd/match? content cfg "booru" "set-filter")
    (if-not (op/is-guild-op? cfg guild-id author-id)
      (opd/you-are-not-an-op-reply cfg id channel-id guild-id)
      (if-not (booru/public-filter-exists? cfg (:args (cmd/parse-command content)))
        (display/filter-not-found-reply cfg id channel-id guild-id)
        (do (logic/set-filter-for-channel cfg channel-id (:args (cmd/parse-command content)))
            (display/filter-set-reply (:args (cmd/parse-command content)) cfg id channel-id guild-id))))))

(defn remove-channel-filter-handler
  [_ {{author-id :id} :author :keys [channel-id guild-id content id]} cfg]
  (when (cmd/match? content cfg "booru" "remove-filter")
    (if-not (op/is-guild-op? cfg guild-id author-id)
      (opd/you-are-not-an-op-reply cfg id channel-id guild-id)
        (do (logic/set-filter-for-channel cfg channel-id nil)
            (display/filter-removed-reply cfg id channel-id guild-id)))))

(defn random-image-handler
  [_ {:keys [channel-id guild-id content id]} cfg]
  (when (cmd/match? content cfg "booru" "random")
    (let [filter (logic/get-filter-for-channel cfg channel-id)]
      (if (nil? filter)
        (display/channel-has-no-filter-reply cfg id channel-id guild-id)
        (try
          (let [image (try (logic/get-random-image cfg filter (:args (cmd/parse-command content)))
                           (catch Exception e (debug e) nil))]
            (if (nil? image)
              (display/image-not-found-reply cfg id channel-id guild-id)
              (display/send-image-embed image cfg channel-id))))))))

(defn otp-image-handler
  [_ {:keys [channel-id guild-id content id]} cfg]
  (when (cmd/match? content cfg "booru" "otp")
    (let [filter (logic/get-filter-for-channel cfg channel-id)]
      (if (nil? filter)
        (display/channel-has-no-filter-reply cfg id channel-id guild-id)
        (try
          (let [image (try (logic/get-random-image cfg filter "oc:frosku,lightning dust")
                           (catch Exception e (debug e) nil))]
            (if (nil? image)
              (display/image-not-found-reply cfg id channel-id guild-id)
              (display/send-image-embed image cfg channel-id))))))))

(defn image-by-id-handler
  [_ {:keys [channel-id guild-id content id]} cfg]
  (when (cmd/match? content cfg "booru" "by-id")
    (let [filter (logic/get-filter-for-channel cfg channel-id)]
      (if (nil? filter)
        (display/channel-has-no-filter-reply cfg id channel-id guild-id)
        (try
          (let [image (try (booru/get-image-by-id cfg filter (:args (cmd/parse-command content)))
                           (catch Exception e (debug e) nil))]
            (if (nil? image)
              (display/image-not-found-reply cfg id channel-id guild-id)
              (display/send-image-embed image cfg channel-id))))))))