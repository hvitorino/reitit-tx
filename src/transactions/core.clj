(ns transactions.core
  (:require [io.pedestal.http :as server]
            [reitit.pedestal :as pedestal]
            [reitit.ring :as ring]
            [reitit.http :as http]
            [reitit.coercion.spec]
            [reitit.http.interceptors.muuntaja :as muuntaja]
    ;[reitit.swagger :as swagger]
    ;[reitit.swagger-ui :as swagger-ui]
    ;[reitit.http.coercion :as coercion]
    ;[reitit.dev.pretty :as pretty]
    ;[reitit.http.interceptors.parameters :as parameters]
    ;[reitit.http.interceptors.exception :as exception]
    ;[reitit.http.interceptors.multipart :as multipart]
    ;[clojure.core.async :as a]
    ;[clojure.java.io :as io]
    ;[muuntaja.core :as m]
            ))

(defonce all-tx (atom []))

(defn add-tx [all tx] (conj all tx))

;(defn remove-tx [all tx] (vec (remove #(= tx %) all)))
;
;(defn get-all [] @all-tx)

(defn all-tx-handler [_]
  {:status 200
   :body   @all-tx})

(defn add-tx-handler [_]
  (swap! all-tx add-tx {:id (inc (count @all-tx)) :value 1000.0})
  {:status 201
   :body   (first @all-tx)})

(def router
  (pedestal/routing-interceptor
    (http/router
      ["/api"
       ["/transactions" {:get  {:handler all-tx-handler}
                         :post {:handler add-tx-handler}}]])

    (ring/routes
      (ring/create-resource-handler)
      (ring/create-default-handler))

    {:interceptors [(muuntaja/format-interceptor)]}))


(defn start []
  (-> {::server/type   :jetty
       ::server/port   3000
       ::server/join?  false
       ;; no pedestal routes
       ::server/routes []}
      (server/default-interceptors)
      ;; use the reitit router
      (pedestal/replace-last-interceptor router)
      (server/dev-interceptors)
      (server/create-server)
      (server/start))
  (println "server running in port 3000"))

(defn -main []
  (start))