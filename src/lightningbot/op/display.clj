(ns lightningbot.op.display
  (:require [discljord.formatting :as fmt]
            [lightningbot.common.discord :as discord]))

(defn you-are-an-op-reply
  [cfg msg-id channel-id guild-id]
  (-> "You are an operator."
      (discord/reply-to-message cfg msg-id channel-id guild-id)))

(defn you-are-not-an-op-reply
  [cfg msg-id channel-id guild-id]
  (-> "You are not an operator."
      (discord/reply-to-message cfg msg-id channel-id guild-id)))

(defn mention-only-one-user-reply
  [cfg msg-id channel-id guild-id]
  (-> "Please mention exactly one user for this command."
      (discord/reply-to-message cfg msg-id channel-id guild-id)))

(defn they-are-already-an-op-reply
  [who cfg msg-id channel-id guild-id]
  (-> (str (fmt/mention-user who) " is already an operator.")
      (discord/reply-to-message cfg msg-id channel-id guild-id)))

(defn they-are-not-an-op-reply
  [who cfg msg-id channel-id guild-id]
  (-> (str (fmt/mention-user who) " is not an operator.")
      (discord/reply-to-message cfg msg-id channel-id guild-id)))

(defn added-as-op-reply
  [who cfg msg-id channel-id guild-id]
  (-> (str (fmt/mention-user who) " added as an operator.")
      (discord/reply-to-message cfg msg-id channel-id guild-id)))

(defn removed-as-op-reply
  [who cfg msg-id channel-id guild-id]
  (-> (str (fmt/mention-user who) " removed as an operator.")
      (discord/reply-to-message cfg msg-id channel-id guild-id)))

(defn nice-try-reply
  [cfg msg-id channel-id guild-id]
  (-> (str "My owner is immune to your mortal attempts!")
      (discord/reply-to-message cfg msg-id channel-id guild-id)))