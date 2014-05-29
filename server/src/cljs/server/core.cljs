;;; If this namespace requires macros, remember that ClojureScript's
;;; macros are written in Clojure and have to be referenced via the
;;; :require-macros directive where the :as keyword is required, while in Clojure is optional. Even
;;; if you can add files containing macros and compile-time only
;;; functions in the :source-paths setting of the :builds, it is
;;; strongly suggested to add them to the leiningen :source-paths.
(ns server.core
  (:require [ajax.core :refer [GET POST]]
            [cljs.core.async :as async
             :refer [chan <! >! timeout close!]])
  (:require-macros [cljs.core.async.macros :refer [go go-loop]]))

(enable-console-print!)

(def chart (.multiBarChart js/nv.models))

(go-loop []
  (.json js/d3 "/summary"
         (fn [resp]
           (.addGraph js/nv (fn []
                              (.tickFormat (.-xAxis chart) (.format js/d3 ",f"))
                              (.tickFormat (.-yAxis chart) (.format js/d3 ",.1f"))
                              (-> (.select js/d3 "#chart svg")
                                  (.datum (clj->js [resp]))
                                  .transition
                                  (.duration 500)
                                  (.call chart))
                              (.windowResize js/nv.utils (.-update chart))))))
  (<! (timeout 1000))
  (recur))
