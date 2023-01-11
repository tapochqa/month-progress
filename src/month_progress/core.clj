(ns month-progress.core 
  (:gen-class)
  (:require
    [month-progress.polling  :as polling]
    [month-progress.lambda   :as lambda]
    [clojure.string    :as str]
    [cheshire.core     :as json]))


(defn polling
  [my-token]
  (polling/run-polling {:telegram {:token my-token} :polling {:update-timeout 1000}}))

(defn lambda
  [config]
  (-> (lambda/->request config)
      (lambda/handle-request! config)
      (lambda/response->)))

(defn -main
  [my-token channel-id]
  #_(polling/run-polling {:telegram {:token my-token} :polling {:update-timeout 1000}})
  (lambda {:token 
           my-token 
           :channel-id
           channel-id})
  )


(comment
    (binding [*in* (-> "trigger-request.json"
                 clojure.java.io/resource
                 clojure.java.io/reader)]

    (-main 
      "..."
      -1001863747484)))

















