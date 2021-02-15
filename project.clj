(defproject com.frosku/lightningbot "0.1.0-SNAPSHOT"
  :author "Frosku <frosku@frosku.com>"
  :description "Bot for general use on pony Discord servers."
  :url "http://github.com/Frosku/lightningbot"
  :license {:name "The Unlicense"
            :url "https://unlicense.org"}
  :dependencies [[org.clojure/clojure "1.10.1"]
                 [org.clojure/tools.cli "1.0.194"]
                 [org.suskalo/discljord "1.2.2"]
                 [throttler "1.0.0"]
                 [chrisjd/edn-config "0.1.1"]
                 [clj-http "3.10.1"]
                 [cheshire "5.10.0"]
                 [juxt/crux-core "20.07-1.9.2-beta"]
                 [juxt/crux-lmdb "20.07-1.9.2-alpha"]
                 [com.fzakaria/slf4j-timbre "0.3.19"]]
  :main lightningbot.core
  :aot :all
  :resource-paths ["res"]
  :repl-options {:init-ns lightningbot.core})
