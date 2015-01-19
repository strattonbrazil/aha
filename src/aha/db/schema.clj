(ns aha.db.schema
  (:require [cheshire.core :refer :all]
            [clojure.java.io :refer [file]]))

; TODO: pull form environment variable
(def db-store (str (.getName (file ".")) "/aha.json"))

(defn initialized?
  "checks to see if the database file is present"
  []
  (.exists (new java.io.File db-store))
)

(defn init-db []
  (let [data { :menus [ {:id 1, :title "Illustrations", :pages []}, {:id 2, :title "Work", :pages [] } ] }]
    (generate-stream data
                     (clojure.java.io/writer db-store)))
)

