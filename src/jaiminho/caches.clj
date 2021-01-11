(ns jaiminho.caches
  (:require [clojure.core.cache :refer [lru-cache-factory
                                        ttl-cache-factory]]))
(defn least-recent-ttl
  [& {:keys [threshold ttl-ms]
      :or   {threshold 10
             ttl-ms    60000}}]
  (-> {}
      (lru-cache-factory :threshold threshold)
      (ttl-cache-factory :ttl ttl-ms)
      (agent :validator map?)))

(defn put-val [v cache-agent k]
  (send-off cache-agent assoc k v))
