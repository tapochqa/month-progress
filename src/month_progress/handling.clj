(ns month-progress.handling
  (:require
    [month-progress.telegram :as telegram]
    [java-time.api :as jt]
    [clojure.math :as math]
    [clojure.string :as str]))


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
         (jt/plus now (jt/hours 3))
         
         
         present-month (jt/truncate-to (jt/adjust now :first-day-of-month) :days)
         
         months  [present-month
                  (jt/minus present-month (jt/months 1))
                  (jt/minus present-month (jt/months 2))]
         
         first-month 
         (first 
           (filter 
             (comp #{3 6 9 12} 
               (fn [m] (jt/as m :month-of-year))) months))
         
         first-hour
         (jt/truncate-to first-month :days)

         last-month
         (jt/plus first-month (jt/months 2))
         
         last-hour
         (jt/plus
           (jt/truncate-to (jt/adjust last-month :last-day-of-month) :days)
           (jt/hours 23))
         
         season-length (jt/time-between first-hour last-hour :hours)
         
         season-passed (jt/time-between first-hour (jt/truncate-to now :hours) :hours)
         
         relation
         (fn [length]
           (->> 
            (/ length season-length)
            (* 100)
            math/floor
            int))
         
         relation-arr
         (->>
          (map (fn [hour]
                 {:hour hour
                  :relation (relation hour)}) (range (inc season-length)))
          (group-by :relation)
          (map (fn [item] (-> item last first))))
         
         %-passed
         (relation season-passed)
         
         blacks
         (int (/ %-passed 5)) 
        
         blacks-str
         (str/join (repeat blacks "█"))
         
         whites-str
         (str/join (repeat (- 20 blacks) "░"))]
        
        
        (if 
          (seq (filter 
                 (comp #{season-passed} :hour) 
                 relation-arr))
          (telegram/send-message
            config
            (:channel-id config)
            (str "<pre>"
              (format "%3d" %-passed)
              " %" " " blacks-str whites-str "" 
              "</pre>")
            {:parse-mode :html}))))))



(comment
  
  (the-handler 0 0 1)
  
  (spit "target/coll" (sort-by :hour (the-handler 0 0 1))))





