(ns lightningbot.core
  (:require [clojure.core.async :as async]
            [discljord.connections :as conn]
            [discljord.events :as evt]
            [discljord.messaging :as msg]
            [lightningbot.booru.handlers :as booru]
            [lightningbot.common.config :as cfg]
            [lightningbot.op.handlers :as op]
            [taoensso.timbre :refer [debug]]))

(defn debug-handler
  [_ message _]
  (debug message))

(def handlers
  {:message-create [#'debug-handler
                    #'op/add-operator-handler
                    #'op/remove-operator-handler
                    #'op/author-is-operator-handler
                    #'booru/image-by-id-handler
                    #'booru/otp-image-handler
                    #'booru/random-image-handler
                    #'booru/set-channel-filter-handler
                    #'booru/remove-channel-filter-handler]})

(defn -main
  [& _]
  (let [config (cfg/get-config)
        event-ch (async/chan 1000)
        bot-ch (conn/connect-bot! (get-in config [:discord :token])
                                  event-ch
                                  :intents #{:guild-messages})
        msg-ch (msg/start-connection! (get-in config [:discord :token]))
        state (assoc config :channels {:event-ch event-ch
                                       :bot-ch bot-ch
                                       :msg-ch msg-ch})]
    (debug "Successfully started" state)
    (try
      (evt/message-pump! event-ch
                         #(evt/dispatch-handlers #'handlers %1 %2 state))
      (finally
        (msg/stop-connection! msg-ch)
        (conn/disconnect-bot! bot-ch)))))
