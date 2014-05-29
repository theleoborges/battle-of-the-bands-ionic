(ns server.handler
  (:use compojure.core)
  (:require [compojure.handler :as handler]
            [compojure.route :as route]
            [ring.util.response :as resp]
            [cheshire.core :refer [generate-string parse-string]]
            [clojure.java.io :as io]))

(def readings (atom []))
;;(def readings (atom [{"accel" -0.0625770071193461, "uuid" "d02cc198c4aaba23"} {"accel" -0.02444887696123743, "uuid" "d02cc198c4aaba23"} {"accel" -0.02595013095105969, "uuid" "d02cc198c4aaba23"} {"accel" 7.2819740426456825, "uuid" "d02cc198c4aaba23"} {"accel" 13.70456721851522, "uuid" "d02cc198c4aaba23"} {"accel" 12.621086256747716, "uuid" "d02cc198c4aaba23"} {"accel" 22.753593766289402, "uuid" "d02cc198c4aaba23"} {"accel" -1.9342286271924864, "uuid" "d02cc198c4aaba23"} {"accel" -0.7622546342974985, "uuid" "d02cc198c4aaba23"} {"accel" 0.06729047169787172, "uuid" "d02cc198c4aaba23"} {"accel" -0.019121296332306414, "uuid" "d02cc198c4aaba23"} {"accel" -0.0175460874933151, "uuid" "d02cc198c4aaba23"} {"accel" -0.05564720807719503, "uuid" "d02cc198c4aaba23"} {"accel" -0.0175460874933151, "uuid" "d02cc198c4aaba23"} {"accel" -0.05564720807719503, "uuid" "d02cc198c4aaba23"} {"accel" -0.0175460874933151, "uuid" "d02cc198c4aaba23"} {"accel" -0.05745450921267725, "uuid" "d02cc198c4aaba23"} {"accel" -0.015821151056641725, "uuid" "d02cc198c4aaba23"}]))

(defn to-csv-lines [readings]
  (->> (for [partition (vals (group-by #(% "uuid") readings))
             data (map vector (iterate inc 0) partition)
             :let [[id data] data]]
         (clojure.string/join "," [id (data "accel") (data "uuid")]))
       (interpose "\n")))


(defn spit-data [lines]
  (io/delete-file "data.csv" true)
  (doseq [line lines]
    (spit "data.csv" line :append true)) )

(defroutes app-routes
  (GET "/" [] (resp/resource-response "index.html" {:root "public"}))
  (GET "/reset" []
       (reset! readings [])
       "Ok.")
  (GET "/spit" []
       (-> @readings to-csv-lines spit-data)
       "Ok.")
  (GET "/accelerations" []
       {:status 200
        :headers {"Content-Type" "application/json"}
        :body (generate-string {:data @readings})})

  (GET "/summary" []

       (let [body
             (if (seq @readings)
               (let [data (map #(% "accel") @readings)
                     count (count data)
                     mean  (/ (reduce + data) count)
                     summary {:count count
                              :mean mean
                              :min (apply min data)
                              :max (apply max data)}]
                 {:key "Band1"
                  :values (reverse (map (fn [key color]
                                          {:x 0 :y (key summary)
                                           :color color})
                                        [:min :mean :max]
                                        ["#AAA7D9" "#6D65D6" "#1000D9"]
                                        ))})
               {:key "Band1"
                :values []})]
         {:status 200
          :headers {"Content-Type" "application/json"}
          :body (generate-string body)}))
  (POST "/accelerations" {body :body}
        (swap! readings conj (-> body slurp parse-string))
        "Ok.")
  (route/resources "/")
  (route/not-found "Not Found"))

(def app
  (handler/site app-routes))


(def mdata [{"accel" -0.0625770071193461, "uuid" "d02cc198c4aaba23"} {"accel" -0.02444887696123743, "uuid" "d02cc198c4aaba23"} {"accel" -0.02595013095105969, "uuid" "d02cc198c4aaba23"} {"accel" 7.2819740426456825, "uuid" "d02cc198c4aaba23"} {"accel" 13.70456721851522, "uuid" "d02cc198c4aaba23"} {"accel" 12.621086256747716, "uuid" "d02cc198c4aaba23"} {"accel" 22.753593766289402, "uuid" "d02cc198c4aaba23"} {"accel" -1.9342286271924864, "uuid" "d02cc198c4aaba23"} {"accel" -0.7622546342974985, "uuid" "d02cc198c4aaba23"} {"accel" 0.06729047169787172, "uuid" "d02cc198c4aaba23"} {"accel" -0.019121296332306414, "uuid" "d02cc198c4aaba23"} {"accel" -0.0175460874933151, "uuid" "d02cc198c4aaba23"} {"accel" -0.05564720807719503, "uuid" "d02cc198c4aaba23"} {"accel" -0.0175460874933151, "uuid" "d02cc198c4aaba23"} {"accel" -0.05564720807719503, "uuid" "d02cc198c4aaba23"} {"accel" -0.0175460874933151, "uuid" "d02cc198c4aaba23"} {"accel" -0.05745450921267725, "uuid" "d02cc198c4aaba23"} {"accel" -0.015821151056641725, "uuid" "d02cc198c4aaba23"}])

(def summary (let [data (map #(% "accel") mdata)
                   count (count data)
                   mean  (/ (reduce + data) count)
                   summary {:count count
                            :mean mean
                            :min (apply min data)
                            :max (apply max data)}]
               (map (fn [key]
                      {:x 0 :y (key summary)})
                    [:min :mean :max])))




;; {:key "Band1"
;;    :values (map-indexed (fn [idx d]
;;                           {:x idx
;;                            :y (d "accel")}) @readings)}



;;  (def sample-random-data (concat mdata (shuffle (map (fn [m] (assoc m "uuid" "Y")) mdata))))
;; ;; (apply concat (for [p (partition-by #(% "uuid") sample-random-data)]
;; ;;           (map (fn [id m]
;; ;;                  (clojure.string/join "," [id (m "accel") (m "uuid")])) (iterate inc 0) p)
;; ;;           ))


;; (apply concat (for [p (partition-by #(% "uuid") mdata)]
;;                               (map (fn [id m]
;;                                      (clojure.string/join "," [id (m "accel") (m "uuid")])) (iterate inc 0) p)
;;                               ))


;; (->> mdata
;;      (partition-by #(% "uuid"))
;;      ())


;; (->> (for [partition (vals (group-by #(% "uuid") mdata))
;;            data (map vector (iterate inc 0) partition)
;;            :let [[id data] data]]
;;        (clojure.string/join "," [id (data "accel") (data "uuid")]))
;;      (interpose "\n"))
