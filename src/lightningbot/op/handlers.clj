(ns lightningbot.op.handlers
  (:require [lightningbot.common.commands :as cmd]
            [lightningbot.op.display :as display]
            [lightningbot.op.logic :as logic]))

(defn author-is-operator-handler
  [_ {{author-id :id} :author :keys [channel-id guild-id content id]} cfg]
  (when (cmd/match? content cfg "op" "ami")
    (if (logic/is-guild-op? cfg guild-id author-id)
      (display/you-are-an-op-reply cfg id channel-id guild-id)
      (display/you-are-not-an-op-reply cfg id channel-id guild-id))))

(defn add-operator-handler
  [_ {{author-id :id} :author :keys [channel-id guild-id content id mentions]} cfg]
  (when (cmd/match? content cfg "op" "add")
    (if-not (logic/is-guild-op? cfg guild-id author-id)
      (display/you-are-not-an-op-reply cfg id channel-id guild-id)
      (if-not (= 1 (count mentions))
        (display/mention-only-one-user-reply cfg id channel-id guild-id)
        (if (logic/is-guild-op? cfg guild-id (logic/first-id mentions))
          (display/they-are-already-an-op-reply (logic/first-id mentions) cfg id channel-id guild-id)
          (do (logic/add-guild-op cfg guild-id (logic/first-id mentions))
              (display/added-as-op-reply (logic/first-id mentions) cfg id channel-id guild-id)))))))

(defn remove-operator-handler
  [_ {{author-id :id} :author :keys [channel-id guild-id content id mentions]} cfg]
  (when (cmd/match? content cfg "op" "remove")
    (if-not (logic/is-guild-op? cfg guild-id author-id)
      (display/you-are-not-an-op-reply cfg id channel-id guild-id)
      (if-not (= 1 (count mentions))
        (display/mention-only-one-user-reply cfg id channel-id guild-id)
        (if-not (logic/is-guild-op? cfg guild-id (logic/first-id mentions))
          (display/they-are-not-an-op-reply (logic/first-id mentions) cfg id channel-id guild-id)
          (if (= (logic/first-id mentions) (logic/get-bot-owner cfg))
            (display/nice-try-reply cfg id channel-id guild-id)
            (do (logic/remove-guild-op cfg guild-id (logic/first-id mentions))
                (display/removed-as-op-reply (logic/first-id mentions) cfg id channel-id guild-id))))))))