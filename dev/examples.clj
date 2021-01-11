(ns examples
  (:require [jaiminho.core :as jaiminho]))

;; Start up jaiminho assuming kafka
;; is running locally on port 9092
(jaiminho/start! {:brokers "localhost:9092"})

;;Produce a keyed message
;; to a number of topics,
;; assuming all the topics exists
(jaiminho/produce! "Some Key identifier"
                   "this is my message"
                   "dev.in" "dev.in1" "dev.in2")

;;Stops jaiminho, killing the producers
(jaiminho/stop!)


