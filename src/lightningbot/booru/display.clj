(ns lightningbot.booru.display
  (:require [lightningbot.common.discord :as discord]
            [discljord.formatting :as fmt]
            [clojure.string :as str]))

(defn truncate-string
  [string length]
  (if (> (count string) length)
    (str (subs string 0 length) "…")
    string))

(defn filter-not-found-reply
  [cfg msg-id channel-id guild-id]
  (-> "Filter not found."
      (discord/reply-to-message cfg msg-id channel-id guild-id)))

(defn filter-set-reply
  [which cfg msg-id channel-id guild-id]
  (-> (str "Filter " which " set for channel " (fmt/mention-channel channel-id) ".")
      (discord/reply-to-message cfg msg-id channel-id guild-id)))

(defn filter-removed-reply
  [cfg msg-id channel-id guild-id]
  (-> (str "Filter removed for channel " (fmt/mention-channel channel-id) ".")
      (discord/reply-to-message cfg msg-id channel-id guild-id)))

(defn channel-has-no-filter-reply
  [cfg msg-id channel-id guild-id]
  (-> (str (fmt/mention-channel channel-id) " has no filter assigned.")
      (discord/reply-to-message cfg msg-id channel-id guild-id)))

(defn image-not-found-reply
  [cfg msg-id channel-id guild-id]
  (-> "No suitable image(s) found, is that query suitable for this channel?"
      (discord/reply-to-message cfg msg-id channel-id guild-id)))

(defn something-went-wrong-reply
  [cfg msg-id channel-id guild-id]
  (-> "Something unexpected went wrong."
      (discord/reply-to-message cfg msg-id channel-id guild-id)))

(defn send-image-embed
  [image cfg channel-id]
  (-> {}
      (assoc :title "Here's your pony!")
      (assoc :color 0x87E8D5)
      (assoc :fields (-> []
                         (conj {:name "Description"
                                :value (let [description (get-in image [:description])]
                                         (if (or (= "" description) (nil? description))
                                           "No description provided."
                                           (truncate-string description 100)))})
                         (conj {:name "Tags"
                                :value (str/join ", " (sort (get-in image [:tags])))})
                         (conj {:name "Source"
                                :value (str (get-in cfg [:philomena :web-root]) "images/" (get-in image [:id]))})))
      (assoc :image (-> {}
                        (assoc :url (get-in image [:representations :medium]))))
      (assoc :footer (-> {}
                         (assoc :text "Lightning Dust x Frosku OTP forever")))
      (discord/create-embed cfg channel-id)))