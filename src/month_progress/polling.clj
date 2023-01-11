(ns month-progress.polling
  (:require 
    [month-progress.telegram :as telegram]
    [month-progress.handling :as handling]
    [cheshire.core :as json]
    [clojure.pprint :refer [pprint]]))


(defn save-offset [offset-file offset]
  (spit offset-file (str offset)))


(defn load-offset [offset-file]
  (try
    (-> offset-file slurp Long/parseLong)
    (catch Throwable _
      nil)))

(defmacro with-safe-log
  "
  A macro to wrap Telegram calls (prevent the whole program from crushing).
  "
  [& body]
  `(try
     ~@body
     (catch Throwable e#
       (println (ex-message e#)))))


(defn run-polling
  [config]

  (let [{
         {:keys [udpate-timeout]} :polling
         :keys [telegram]}
        config

        me
        (telegram/get-me telegram)

        offset-file "TELEGRAM_OFFSET"

        context
        {:me me
         :telegram telegram
         :config config}

        offset
        (load-offset offset-file)]

    (loop [offset offset]

      (let [updates
            (with-safe-log
              (telegram/get-updates telegram
                              {:offset offset
                               :timeout udpate-timeout}))

            new-offset
            (or (some-> updates peek :update_id inc)
                offset)]

        (println "Got %s updates, next offset: %s, updates: %s"
                    (count updates)
                    new-offset
                    (json/generate-string updates {:pretty true}))

        (when offset
          (save-offset offset-file new-offset))
        (doseq [message updates]
          (pprint message)
          (handling/the-handler (:token telegram) (:message message)))

        (recur new-offset)))))
