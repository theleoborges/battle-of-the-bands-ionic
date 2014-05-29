;;; If this namespace requires macros, remember that ClojureScript's
;;; macros are written in Clojure and have to be referenced via the
;;; :require-macros directive where the :as keyword is required, while in Clojure is optional. Even
;;; if you can add files containing macros and compile-time only
;;; functions in the :source-paths setting of the :builds, it is
;;; strongly suggested to add them to the leiningen :source-paths.
(ns server.core
  (:require [ajax.core :refer [GET POST]]))

(enable-console-print!)

(def palette (js/Rickshaw.Color.Palette. #js {:scheme "httpStatus"}))

(defn transform-data [datum]
  (map (fn [name key color]
         {:name name
          :data [{:x 0 :y (datum key)}]
          :color (.color palette color)})
       ["band1-max" "band1-mean" "band1-min"]
       ["max" "mean" "min"]
       [400 300 200]))



(def wrapper (js/Rickshaw.Graph.Ajax.
              #js {
                   :element (.getElementById js/document "chart")
                   :dataURL "/summary"
                   :width 960
                   :height 500
                   :renderer "bar"
                   :min -5
                   :onData (comp clj->js reverse transform-data js->clj)
                   :onComplete (fn [w]
                                 (js/Rickshaw.Graph.Legend.
                                  #js {:element (.querySelector js/document "#legend")
                                       :graph (.-graph w)})
                                 (.render (js/Rickshaw.Graph.Axis.Y.
                                           #js {:graph (.-graph w)}))

                                 )}))
