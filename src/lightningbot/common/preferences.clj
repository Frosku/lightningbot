(ns lightningbot.common.preferences
  (:require [cheshire.core :as json]
            [crux.api :as crux]
            [clojure.java.io :as io]))

(defn crux-node
  [cfg]
  (crux/start-node {:crux.node/topology '[crux.standalone/topology
                                          crux.kv.lmdb/kv-store]
                    :crux.kv/db-dir (str (io/file (get-in cfg [:crux :db])
                                                  (get-in cfg [:crux :db-dir])))
                    :crux.standalone/event-log-kv-store 'crux.kv.lmdb/kv
                    :crux.standalone/event-log-dir (str (io/file (get-in cfg [:crux :db])
                                                                 (get-in cfg [:crux :evt-dir])))
                    :crux.standalone/event-log-sync? true
                    :crux.kv/sync? true}))

(defn put-pref
  [cfg pref-key pref-value]
  (let [node (crux-node cfg)
        preference {:crux.db/id pref-key
                    :value (json/generate-string pref-value)}]
    (crux/await-tx node
                   (crux/submit-tx node [[:crux.tx/put preference]]))))

(defn get-pref
  [cfg pref-key]
  (let [node (crux-node cfg)
        query {:find '[v]
               :where '[[e :crux.db/id ?pref-key]
                        [e :value v]]
               :args [{'?pref-key pref-key}]}]
    (-> node
        (crux/db)
        (crux/q query)
        (first)
        (first)
        (json/parse-string))))
