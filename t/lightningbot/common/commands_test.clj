(ns lightningbot.common.commands-test
  (:require [clojure.test :refer :all]
            [lightningbot.common.commands :as module]))

(deftest parse-command
  (testing "works with valid commands"
    (is (= (module/parse-command "?command sub args")
           {:prefix "?"
            :command "command"
            :sub-command "sub"
            :args "args"
            :match true}))))

(deftest match?
  (testing "works with a valid match"
    (is (= true
           (module/match? "?command sub args"
                          {:discord {:prefix "?"}}
                          "command"
                          "sub"))))
  (testing "fails with an invalid match"
    (is (= false
           (module/match? "?command sub args"
                          {:discord {:prefix "!"}}
                          "command"
                          "sub")))
    (is (= false
           (module/match? "?command sub args"
                          {:discord {:prefix "?"}}
                          "other-command"
                          "sub")))
    (is (= false
           (module/match? "?command sub args"
                          {:discord {:prefix "?"}}
                          "command"
                          "other-sub")))))