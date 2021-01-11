(ns jaiminho.producer
  (:require [jackdaw.admin :as jadm]
            [jackdaw.client :as jcli]))



(defn adm-client [bootstrap-servers]
  (jadm/->AdminClient {"bootstrap.servers" bootstrap-servers}))

(def default-config
  {
   "key.serializer" "org.apache.kafka.common.serialization.StringSerializer"
   "value.serializer" "org.apache.kafka.common.serialization.StringSerializer"
   "acks" "all"})

(defn producer
  [bootstrap-servers & {:keys [producer-conf topic-names]}]
  (let [adm (adm-client bootstrap-servers)
        tns (->> topic-names
                 (map #(hash-map :topic-name %)))]
    (if (every? #(jadm/topic-exists? adm %) tns)
      (jcli/producer (merge {"bootstrap.servers" bootstrap-servers}
                            default-config
                            producer-conf))
      (throw (ex-info "Missing a topic" {:topics topic-names})))))