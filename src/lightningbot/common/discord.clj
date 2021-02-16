(ns lightningbot.common.discord
  (:require [discljord.messaging :as msg]))

(defn reply-to-message
  [text cfg msg-id channel-id guild-id]
  (msg/create-message! (get-in cfg [:channels :msg-ch]) channel-id
                       :message-reference {:guild_id guild-id
                                           :channel_id channel-id
                                           :message_id msg-id}
                       :content text))

(defn create-embed
  [embed cfg channel-id]
  (msg/create-message! (get-in cfg [:channels :msg-ch]) channel-id
                       :embed embed))