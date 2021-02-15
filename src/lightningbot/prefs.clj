(ns lightningbot.prefs
  (:require [cheshire.core :as json]
            [crux.api :as crux]
            [clojure.java.io :as io]))

(defn crux-node
  [ctx]
  (crux/start-node {:crux.node/topology '[crux.standalone/topology
                                          crux.kv.lmdb/kv-store]
                    :crux.kv/db-dir (str (io/file (get-in ctx [:crux :db])
                                                  (get-in ctx [:crux :db-dir])))
                    :crux.standalone/event-log-kv-store 'crux.kv.lmdb/kv
                    :crux.standalone/event-log-dir (str (io/file (get-in ctx [:crux :db])
                                                                 (get-in ctx [:crux :evt-dir])))
                    :crux.standalone/event-log-sync? true
                    :crux.kv/sync? true}))

(defn store-preference
  [ctx pref-key pref-value]
  (let [node (crux-node ctx)
        preference {:crux.db/id pref-key
                    :value (json/generate-string pref-value)}]
        (crux/await-tx node
                       (crux/submit-tx node [[:crux.tx/put preference]]))))

(defn get-preference
  [ctx pref-key]
  (let [node (crux-node ctx)
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
