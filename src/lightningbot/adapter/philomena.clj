(ns lightningbot.adapter.philomena
  (:require [clj-http.client :as cc]
            [discljord.messaging :as msg]
            [discljord.formatting :as fmt]
            [lightningbot.admins :as admins]
            [lightningbot.commands :as cmd]
            [lightningbot.prefs :as prefs]
            [clojure.string :as str])
  (:gen-class))

(defn channel-filter-preference-key
  [channel-id]
  (keyword (str "channel-filter-" channel-id)))

(defn api-root
  [ctx]
  (str (get-in ctx [:philomena :web-root]) "api/v1/json/"))

(defn api-search-images-endpoint
  [ctx]
  (str (api-root ctx) "search/images"))

(defn api-image-endpoint
  [ctx]
  (str (api-root ctx) "images/"))

(defn api-filters-endpoint
  [ctx]
  (str (api-root ctx) "filters/"))

(defn get-image-by-id
  [ctx filter id]
  (try
    (-> ctx
        (api-image-endpoint)
        (str id)
        (cc/get {:accept :json
                 :as :json
                 :query-params {"key" (get-in ctx [:philomena :api-key])
                                "filter_id" filter}})
        (get-in [:body :image]))
    (catch Exception _ nil)))

(defn get-number-of-results
  [ctx filter query]
  (-> ctx
      (api-search-images-endpoint)
      (cc/get {:accept :json
               :as :json
               :query-params {"key" (get-in ctx [:philomena :api-key])
                              "filter_id" filter
                              "q" query
                              "per_page" 1}})
      (get-in [:body :total])))

(defn get-search-image-results
  [ctx filter query per-page page-num]
  (try
    (-> ctx
        (api-search-images-endpoint)
        (cc/get {:accept :json
                 :as :json
                 :query-params {"key" (get-in ctx [:philomena :api-key])
                                "filter_id" filter
                                "q" query
                                "per_page" per-page
                                "page" page-num}})
        (get-in [:body :images]))
    (catch Exception _ [nil])))

(defn public-filter-exists?
  [ctx filter-id]
  (try
    (-> ctx
        (api-filters-endpoint)
        (str filter-id)
        (cc/get {:accept :json})
        (get-in [:status])
        (= 200))
    (catch Exception _ nil)))

(defn get-random-image
  [ctx filter query]
  (->> query
       (get-number-of-results ctx filter)
       (rand-int)
       (get-search-image-results ctx filter query 1)
       (first)))

(defn set-filter-for-channel
  [ctx channel-id filter-id]
  (prefs/store-preference ctx (channel-filter-preference-key channel-id) filter-id))

(defn get-filter-for-channel
  [ctx channel-id]
  (prefs/get-preference ctx (channel-filter-preference-key channel-id)))

(defn truncate
  [string length]
  (if (> (count string) length)
    (str (subs string 0 length) "…")
    string))

(defn make-image-embed
  [ctx image]
  {:title "Here's your pony picture!"
   :type "rich"
   :color 0xd241b5
   :fields [{:name "Description"
             :value (truncate (get-in image [:description]) 100)}
            {:name "Tags"
             :value (str/join ", " (get-in image [:tags]))}
            {:name "Full Res"
             :value (str (get-in ctx [:philomena :web-root]) "images/" (get-in image [:id]))}]
   :image {:url (get-in image [:representations :medium])}
   :footer {:text "Frosku x Lightning Dust OTP"}})

(defn filter-set-handler
  [_ {{author-id :id} :author :keys [channel-id guild-id content id]} ctx state]
  (let [parsed (cmd/parse-command content)]
    (when (and (cmd/is-command? ctx parsed)
               (cmd/command-type? "filter" parsed)
               (cmd/sub-command-type? "set" parsed))
      (if (not (admins/is-admin? ctx guild-id author-id))
       (msg/create-message! (:msg @state) channel-id
                             :message-reference {:guild_id guild-id
                                                 :channel_id channel-id
                                                 :message_id id}
                             :content "You are not an operator.")
       (if (not (public-filter-exists? ctx (:remainder parsed)))
         (msg/create-message! (:msg @state) channel-id
                             :message-reference {:guild_id guild-id
                                                 :channel_id channel-id
                                                 :message_id id}
                             :content (str "Filter " (:remainder parsed) " does not exist or is not public."))
         (let [filter-id (Integer/parseInt (:remainder parsed))]
           (set-filter-for-channel ctx channel-id filter-id)
           (msg/create-message! (:msg @state) channel-id
                             :message-reference {:guild_id guild-id
                                                 :channel_id channel-id
                                                 :message_id id}
                             :content (str "Filter " filter-id " set for this channel."))))))))

(defn random-image-handler
  [_ {:keys [channel-id guild-id content id]} ctx state]
  (let [parsed (cmd/parse-command content)
        query (:remainder parsed)]
    (when (and (cmd/is-command? ctx parsed)
               (cmd/command-type? "pony" parsed)
               (cmd/sub-command-type? "random" parsed))
      (let [filter-id (get-filter-for-channel ctx channel-id)]
        (if (nil? filter-id)
          (msg/create-message! (:msg @state) channel-id
                               :message-reference {:guild_id guild-id
                                                   :channel_id channel-id
                                                   :message_id id}
                               :content "No filter set for channel.")
          (let [image (get-random-image ctx filter-id query)]
            (if (nil? image) 
              (msg/create-message! (:msg @state) channel-id
                                   :message-reference {:guild_id guild-id
                                                       :channel_id channel-id
                                                       :message_id id}
                                   :content (str "No matches found for '" query "'."))
              (msg/create-message! (:msg @state) channel-id
                                   :embed (make-image-embed ctx image)))))))))

(defn id-image-handler
  [_ {:keys [channel-id guild-id content id]} ctx state]
  (let [parsed (cmd/parse-command content)
        image-id (:remainder parsed)]
    (when (and (cmd/is-command? ctx parsed)
               (cmd/command-type? "pony" parsed)
               (cmd/sub-command-type? "id" parsed))
      (let [filter-id (get-filter-for-channel ctx channel-id)]
        (if (nil? filter-id)
          (msg/create-message! (:msg @state) channel-id
                               :message-reference {:guild_id guild-id
                                                   :channel_id channel-id
                                                   :message_id id}
                               :content "No filter set for channel.")
          (let [image (get-image-by-id ctx filter-id image-id)]
            (if (nil? image)
              (msg/create-message! (:msg @state) channel-id
                                   :message-reference {:guild_id guild-id
                                                       :channel_id channel-id
                                                       :message_id id}
                                   :content (str "No matches found. Is that image suitable for this channel?"))
              (msg/create-message! (:msg @state) channel-id
                                   :embed (make-image-embed ctx image)))))))))
