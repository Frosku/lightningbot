(ns lightningbot.core
  (:require [clojure.core.async :as async]
            [clojure.string :as str]
            [discljord.connections :as conn]
            [discljord.events :as evt]
            [discljord.messaging :as msg]
            [lightningbot.context :as ctx]
            [lightningbot.adapter.philomena :as philomena]
            [lightningbot.admins :as admins]))

(def state (atom nil))

(defn debug-handler
  [event-type message ctx state]
  (println {:event-type event-type :message message :ctx ctx :state state}))

(def handlers
  {:message-create [#'debug-handler
                    #'admins/ami-admin-handler
                    #'admins/add-admin-handler
                    #'philomena/filter-set-handler
                    #'philomena/random-image-handler
                    #'philomena/id-image-handler]})

(defn -main
  [& args]
  (let [ctx (ctx/get-application-context)
        event-ch (async/chan 1000)
        bot-ch (conn/connect-bot! (get-in ctx [:discord :token]) event-ch :intents #{:guild-messages})
        msg-ch (msg/start-connection! (get-in ctx [:discord :token]))]
    (reset! state {:event event-ch :bot bot-ch :msg msg-ch})
    (try
      (evt/message-pump! event-ch #(evt/dispatch-handlers #'handlers %1 %2 ctx state))
      (finally
        (msg/stop-connection! msg-ch)
        (conn/disconnect-bot! bot-ch)))))
