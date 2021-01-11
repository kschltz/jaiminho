(ns jaiminho.core
  (:require [jaiminho.caches :as jc]
            [jaiminho.producer :as jaip]
            [jackdaw.client :as jd.client]))

(def ^:private state (atom nil))

(defn start! [{:keys [cache brokers]
               :or   {cache (jc/least-recent-ttl)}
               :as   conf}]
  {:pre [(some? cache) (string? brokers)]}
  (if (nil? @state)
    (do (reset! state (assoc conf :cache cache)))
    (throw (ex-info "Already running" @state))))

(defn stop! []
  (if-let [ps (-> @state
                  (:cache (agent nil))
                  deref
                  vals)]
    (run! #(.close %) ps))
  (reset! state nil))

(defn get-producer [topic-name]
  (-> (:cache @state)
      (get topic-name)))

(defn get-or-make-producers
  [cache-agent topic-names]
  (for [t topic-names]
    (if-let [found-producer (get-producer t)]
      [t found-producer]
      (do
        [t (doto (jaip/producer (:brokers @state)
                                :topic-names [t])
             (jc/put-val cache-agent t))]))))

(defn produce! [k v & topic-names]
  {:pre [(some? @state)]}
  (for [[topic-name producer] (get-or-make-producers
                                (:cache @state)
                                topic-names)]
    (jd.client/produce! producer
                        {:topic-name topic-name}
                        k
                        v)))