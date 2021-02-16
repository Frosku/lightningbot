(ns lightningbot.common.config
  (:require [edn-config.core :as cfg]
            [clojure.java.io :as io]))

(defn get-config
  []
  (cfg/load-file (io/resource "config.edn")))
