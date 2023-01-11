(ns month-progress.handling
  (:require
    [month-progress.telegram :as telegram]
    [java-time.api :as jt]
    [clojure.math :as math]
    [clojure.string :as str]
    ))




(defn the-handler 
  "Bot logic here"
  [config update trigger-id]
  
  (let 
    
    []
  
    (if trigger-id
      
      (let
        [today
         (jt/local-date)
         
         
         now
         (jt/truncate-to (jt/local-date-time) :hours)
         
         now
         (jt/minus now (jt/hours 3))
         
         
         first-hour
         (jt/truncate-to (jt/adjust now :first-day-of-month) :days)

         
         last-hour
         (jt/plus
           (jt/truncate-to (jt/adjust now :last-day-of-month) :days)
           (jt/hours 23))
         
         month-length (jt/time-between first-hour last-hour :hours)
         ;month-length (float  month-length)
         
         month-passed (jt/time-between first-hour (jt/truncate-to now :hours) :hours)
         ;month-passed (float month-passed)
         
         relation
         (fn [length]
           (->> 
            (/ length month-length)
            (* 100)
            math/round))
         
         relation-arr
         (->>
          (map (fn [hour]
                 {:hour hour
                  :relation (relation hour)}) (range month-length))
          (group-by :relation)
          (map (fn [item] (-> item last last))))
         
         %-passed
         (relation month-passed)
         #_100
         
         
         blacks
         (int (/ %-passed 5))
         
        
        
         blacks-str
         (str/join (repeat blacks "█"))
         
         whites-str
         (str/join (repeat (- 20 blacks) "░"))
         ]
        
        
        (if 
          (seq (filter 
                 (comp #{month-passed} :hour) 
                 relation-arr))
          (telegram/send-message
            config
            (:channel-id config)
            (str "<pre>" 
              (format "%3d" %-passed)
              " %" " " blacks-str whites-str "" 
              "</pre>")
            {:parse-mode :html}))))))


