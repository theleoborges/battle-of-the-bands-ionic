;;; If this namespace requires macros, remember that ClojureScript's
;;; macros are written in Clojure and have to be referenced via the
;;; :require-macros directive where the :as keyword is required, while in Clojure is optional. Even
;;; if you can add files containing macros and compile-time only
;;; functions in the :source-paths setting of the :builds, it is
;;; strongly suggested to add them to the leiningen :source-paths.
(ns server.core
  (:require [ajax.core :refer [GET POST]]))

(.write js/document "Clojurescript")

(def margin {:top 20, :right 20, :bottom 30, :left 50})

(def width (-> 960
               (- (:left margin))
               (- (:right margin))))

(def height (-> 500
                (- (:top margin))
                (- (:bottom margin))))


(def x (-> js/d3 (.-time) (.scale) (.range #js [0 width])))
(def y (-> js/d3 (.-time) (.scale) (.range #js [height 0])))

(def xAxis (-> js/d3 (.-svg) (.axis) (.scale x) (.orient "bottom")))

(def yAxis (-> js/d3 (.-svg) (.axis) (.scale y) (.orient "left")))

(def line (-> js/d3 (.-svg) (.line)
              (.x (fn [d] (x (first d))))
              (.y (fn [d] (y (second d))))))

(def svg (-> js/d3 (.select "body") (.append "svg")
             (.attr "width" (-> width (+ (:left margin)) (+ (:right margin))))
             (.attr "height" (-> height (+ (:top margin)) (+ (:bottom margin))))
             (.append "g")
             (.attr "transform" (str "translate(" (:left margin) "," (:top margin) ")"))))



(-> svg
    (.append "g")
    (.attr "class" "x axis")
    (.attr "transform" (str "translate(0," height ")")))

(-> svg
    (.append "g")
    (.attr "class" "y axis")
    (.call yAxis)
    (.append "text")
    (.attr "transform" "rotate(-90)")
    (.attr "y" "6")
    (.attr "dy" ".71em")
    (.style "text-anchor", "end")
    (.text "Acceleration"))


(defn render [data]
  (let [data (clj->js (map vector (iterate inc 0) data))]
    (.domain x (.extent js/d3 data (fn [d] (first d))))
    (.domain y (.extent js/d3 data (fn [d] (second d))))
    (-> svg
        (.append "path")
        (.datum data)
        (.attr "class" "line")
        (.attr "d" line))))

;;(render data)

(GET "/accelerations" {:handler (fn [resp]
                                  (render (resp "data")))
                       :error-handler (fn [error] (.log js/console "error" error))
                       :format :json})
