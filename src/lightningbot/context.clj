(ns lightningbot.context
  (:require [edn-config.core :as cfg]
            [clojure.java.io :as io]))

(defn get-application-context
  []
  (cfg/load-file (io/resource "config.edn")))
