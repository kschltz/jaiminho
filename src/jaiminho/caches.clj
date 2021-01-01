(ns jaiminho.caches
  (:require [clojure.core.cache :refer [lru-cache-factory
                                        ttl-cache-factory]]))
(defn least-recent-ttl [ttl-ms]
  (-> {}
    (lru-cache-factory :threshold 10)
    (ttl-cache-factory :ttl ttl-ms)
    (agent :validator map?)))
