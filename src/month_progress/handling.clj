(ns month-progress.handling
  (:require
    [month-progress.telegram :as telegram]))


(defn the-handler 
  "Bot logic here"
  [token message]
  
  (telegram/send-message {:token token} (-> message :chat :id) (:text message))
  
)
