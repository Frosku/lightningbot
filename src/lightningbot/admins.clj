(ns lightningbot.admins
  (:require [discljord.formatting :as fmt]
            [discljord.messaging :as msg]
            [lightningbot.commands :as cmd]
            [lightningbot.prefs :as prefs]))

(defn get-preference-key
  [guild-id]
  (keyword (str "admins-" guild-id)))

(defn get-global-admin
  [ctx]
  (try
    (get-in ctx [:discord :global-admin])
    (catch Exception e nil)))

(defn get-admins
  [ctx guild-id]
  (->> guild-id
       (get-preference-key)
       (prefs/get-preference ctx)
       (vec)
       (cons (get-global-admin ctx))
       (vec)))

(defn add-admin
  [ctx guild-id admin-id]
  (->> guild-id
       (get-preference-key)
       (prefs/get-preference ctx)
       (vec)
       (cons admin-id)
       (set)
       (vec)
       (prefs/store-preference ctx (get-preference-key guild-id))))

(defn is-admin?
  [ctx guild-id user-id]
  (->> guild-id
       (get-admins ctx)
       (some #(= user-id %))
       (some?)))

(defn ami-admin-handler
  [_ {{author-id :id} :author :keys [channel-id guild-id content id]} ctx state]
  (let [parsed (cmd/parse-command content)]
    (when (and (cmd/is-command? ctx parsed)
               (cmd/command-type? "op" parsed)
               (cmd/sub-command-type? "ami" parsed))
      (msg/create-message! (:msg @state) channel-id
                           :message-reference {:guild_id guild-id
                                               :channel_id channel-id
                                               :message_id id}
                           :content (if (is-admin? ctx guild-id author-id)
                                      "You are an operator."
                                      "You are not an operator.")))))

(defn add-admin-handler
  [_ {{author-id :id} :author :keys [channel-id guild-id content id mentions]} ctx state]
  (let [parsed (cmd/parse-command content)]
    (when (and (cmd/is-command? ctx parsed)
               (cmd/command-type? "op" parsed)
               (cmd/sub-command-type? "add" parsed))
      (if (not (is-admin? ctx guild-id author-id))
        (msg/create-message! (:msg @state) channel-id
                             :message-reference {:guild_id guild-id
                                                 :channel_id channel-id
                                                 :message_id id}
                             :content "You are not an operator.")
        (if (not (= (count mentions) 1))
          (msg/create-message! (:msg @state) channel-id
                               :message-reference {:guild_id guild-id
                                                   :channel_id channel-id
                                                   :message_id id}
                               :content "Please mention exactly one user to add as an operator.")
          (let [new-operator (get-in (first mentions) [:id])]
            (add-admin ctx guild-id new-operator)
            (msg/create-message! (:msg @state) channel-id
                                 :message-reference {:guild_id guild-id
                                                     :channel_id channel-id
                                                     :message_id id}
                                 :content (str (fmt/mention-user new-operator) " added as operator."))))))))
